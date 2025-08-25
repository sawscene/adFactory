/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.admanagerapp.systemsettingplugin.common.BreaktimeRecordFactory;
import adtekfuji.admanagerapp.systemsettingplugin.utils.ResponseUtils;
import adtekfuji.clientservice.BreaktimeInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 休憩設定画面
 *
 * @author e-mori
 */
@FxComponent(id = "BreaktimeEditCompo", fxmlPath = "/fxml/compo/breaktime_edit_compo.fxml")
public class BreaktimeEditCompoFxController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static BreaktimeInfoFacade breaktimeInfoFacade = new BreaktimeInfoFacade();

    private LinkedList<BreakTimeInfoEntity> cashDatas = new LinkedList<>();
    private LinkedList<BreakTimeInfoEntity> operatDatas = new LinkedList<>();

    private static Thread registThread = null;

    @FXML
    private Button registButton;
    @FXML
    private VBox propertyPane;
    @FXML
    private Pane Progress;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //役割の権限によるボタン無効化.
        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            registButton.setDisable(true);
        }
        runUpdateViewThread();
    }

    @FXML
    private void onRegist(ActionEvent event) {
        logger.info("regist start");

        runRegistThread();
    }

    /**
     * 保存を実施
     * 
     * @return 保存に失敗したとき、あるいは保存を行わなかったときfalse
     */
    private boolean runRegistThread() {
        Boolean isCancel = true;
        try {
            blockUI(true);

            if (this.checkEmpty()) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return false;
            }

            if (!this.validItems()) {
                return false;
            }
            
            if (!this.getRemovedDatas().isEmpty()) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.contactRemoveBreaktimeAssignedOrganization"));
                if (ret.equals(ButtonType.CANCEL)) {
                    return false;
                }
            }

            isCancel = false;

            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
                    ResponseEntity res = null;
                    logger.info("Delete");
                    for (BreakTimeInfoEntity entity : getRemovedDatas()) {
                        entity.updateData();
                        res = breaktimeInfoFacade.remove(entity.getBreaktimeId());
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }
                    logger.info("new create");
                    for (BreakTimeInfoEntity entity : getAdditionalDatas()) {
                        entity.updateData();
                        res = breaktimeInfoFacade.add(entity);
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }
                    logger.info("Update");
                    for (BreakTimeInfoEntity entity : getUpdateDatas()) {
                        entity.updateData();
                        res = breaktimeInfoFacade.update(entity);
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }
                    return res;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // 処理結果
                        ResponseEntity res = this.getValue();
                        if (Objects.isNull(res) || Objects.isNull(res.getErrorType())) {
                            return;
                        }

                        if (!ServerErrorTypeEnum.SUCCESS.equals(res.getErrorType())) {
                            ResponseUtils.showAlertDialog(res);
                        }
                        
                        // 休憩時間のキャッシュを削除する。
                        CacheUtils.removeCacheData(BreakTimeInfoEntity.class);

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                        runUpdateViewThread();
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    blockUI(false);
                    runUpdateViewThread();
                }
            };
            registThread = new Thread(task);
            registThread.start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }

        return true;
    }

    /**
     * 画面更新用スレッド
     *
     */
    private void runUpdateViewThread() {
        try {
            // 登録処理中の場合、その完了を待つ
            if (Objects.nonNull(registThread) && registThread.isAlive()) {
                registThread.join();
            }

            Platform.runLater(() -> {
                blockUI(true);
            });
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    try {
                        cashDatas.clear();
                        operatDatas.clear();

                        //休憩理由全件取得
                        cashDatas = new LinkedList<>(breaktimeInfoFacade.findAll());
                        operatDatas = new LinkedList<>(cloneBreaktime(cashDatas));
                        operatDatas.sort(Comparator.comparing(breaktime -> breaktime.getStarttime()));
                        Platform.runLater(() -> {
                            propertyPane.getChildren().clear();
                            Table table = new Table(propertyPane.getChildren()).isAddRecord(Boolean.TRUE)
                                    .isColumnTitleRecord(Boolean.TRUE).title(LocaleUtils.getString("key.EditBreakTime")).styleClass("ContentTitleLabel");
                            table.setAbstractRecordFactory(new BreaktimeRecordFactory(table, operatDatas));
                        });

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        Platform.runLater(() -> {
                            blockUI(false);
                        });
                    }
                    return null;
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 追加された情報の一覧取得
     *
     * @return 追加情報一覧
     */
    private List<BreakTimeInfoEntity> getAdditionalDatas() {
        ArrayList<BreakTimeInfoEntity> subArr1 = new ArrayList(operatDatas);
        ArrayList<BreakTimeInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(cashDatas);
        subArr1.stream().forEach((s) -> {
            if (s.getBreaktimeId() == null) {
                logger.debug("追加されたデータ:{}", s.getBreaktimeName());
                subArr2.add(s);
            }
        });
        return subArr2;
    }

    /**
     * 更新された情報の一覧取得
     *
     * @return 更新一覧
     */
    private List<BreakTimeInfoEntity> getUpdateDatas() {
        ArrayList<BreakTimeInfoEntity> subArr1 = new ArrayList(operatDatas);
        ArrayList<BreakTimeInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(cashDatas);
        subArr1.stream().forEach((s) -> {
            if (s.getBreaktimeId() != null) {
                logger.debug("編集されたデータ:{}", s.getBreaktimeName());
                subArr2.add(s);
            }
        });
        return subArr2;
    }

    /**
     * 削除された情報の一覧取得
     *
     * @return 削除情報
     */
    private List<BreakTimeInfoEntity> getRemovedDatas() {
        boolean delFlag = true;
        ArrayList<BreakTimeInfoEntity> subArr1 = new ArrayList(cashDatas);
        ArrayList<BreakTimeInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(operatDatas);
        for (BreakTimeInfoEntity s : subArr1) {
            for (BreakTimeInfoEntity op : operatDatas) {
                if (Objects.equals(s.getBreaktimeId(), op.getBreaktimeId())) {
                    delFlag = false;
                    break;
                }
            }
            if (!delFlag) {
                delFlag = true;
            } else {
                logger.debug("削除されたデータ:{}", s.getBreaktimeName());
                subArr2.add(s);
            }
        }
        return subArr2;
    }

    /**
     * 編集用データの作成
     *
     * @param entitys 編集元のデータ
     * @return コピーされた編集元データ
     */
    private List<BreakTimeInfoEntity> cloneBreaktime(List<BreakTimeInfoEntity> entitys) {

        List<BreakTimeInfoEntity> cloneEntitys = new ArrayList<>();

        entitys.stream().map((entity) -> {
            return new BreakTimeInfoEntity(entity);
        }).forEach((cloneEntity) -> {
            cloneEntitys.add(cloneEntity);
        });

        return cloneEntitys;
    }

    /**
     * 未入力チェック
     *
     * @return 未入力:true
     */
    private boolean checkEmpty() {
        if (getAdditionalDatas().stream().anyMatch((e) -> (Objects.isNull(e.getBreaktimeName()) || e.getBreaktimeName().isEmpty()))) {
            return true;
        }
        if (getUpdateDatas().stream().anyMatch((e) -> (Objects.isNull(e.getBreaktimeName()) || e.getBreaktimeName().isEmpty()))) {
            return true;
        }

        return false;
    }

    /**
     * 入力項目チェック
     *
     * @return 入力データ有効:true, 入力エラー:false
     */
    private boolean validItems() {
        //開始終了時間確認
        if (getAdditionalDatas().stream().anyMatch((e) -> (Objects.isNull(e.getStarttime()) || Objects.isNull(e.getEndtime())))
                || getUpdateDatas().stream().anyMatch((e) -> (Objects.isNull(e.getStarttime()) || Objects.isNull(e.getEndtime())))) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.TimeFormatErrMessage"));
            return false;
        }

        //開始<終了時間確認
        Calendar starttime = Calendar.getInstance();
        Calendar endtime = Calendar.getInstance();
        for (BreakTimeInfoEntity entity : getAdditionalDatas()) {
            starttime.setTime(entity.getStarttime());
            endtime.setTime(entity.getEndtime());
            starttime.set(endtime.get(Calendar.YEAR), Calendar.MONTH, Calendar.DATE);
            endtime.set(starttime.get(Calendar.YEAR), Calendar.MONTH, Calendar.DATE);
            if (endtime.before(starttime)) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DateCompErrMessage"));
                return false;
            }
        }
        for (BreakTimeInfoEntity entity : getUpdateDatas()) {
            starttime.setTime(entity.getStarttime());
            endtime.setTime(entity.getEndtime());
            starttime.set(endtime.get(Calendar.YEAR), Calendar.MONTH, Calendar.DATE);
            endtime.set(starttime.get(Calendar.YEAR), Calendar.MONTH, Calendar.DATE);
            if (endtime.before(starttime)) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DateCompErrMessage"));
                return false;
            }
        }

        //休憩時間重複チェック
        Set<String> set = new HashSet<>();
        for (BreakTimeInfoEntity breakTime : operatDatas) {
            // 別IDで登録されている名称は設定不可
            long count = cashDatas.stream().filter(p -> p.getBreaktimeName().equals(breakTime.getBreaktimeName()) && !p.getBreaktimeId().equals(breakTime.getBreaktimeId())).count();
            if (count > 0) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), breakTime.getBreaktimeName()));
                return false;
            }

            if (set.contains(breakTime.getBreaktimeName())) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), breakTime.getBreaktimeName()));
                return false;
            } else {
                set.add(breakTime.getBreaktimeName());
            }
        }

        return true;
    }

    private void blockUI(boolean b) {
        sc.blockUI(b);
        Progress.setVisible(b);
    }
    
    /**
     * 変更がかけられたかチェック
     * 
     * @return 
     */
    private boolean isChanged() {
        // 編集権限なしは常に無変更
        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            return false;
        }

        if (!getRemovedDatas().isEmpty() || !getAdditionalDatas().isEmpty() || !getUpdateDatas().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 画面破棄時に内容に変更がないか調べて変更があるなら保存する
     * 
     * @return 
     */
    @Override
    public boolean destoryComponent() {
        if (isChanged()) {
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            if (ButtonType.YES == buttonType) {
                return runRegistThread();
            } else if (ButtonType.CANCEL == buttonType) {
                return false;
            }
        }
        return true;
    }
}
