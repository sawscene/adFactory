/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.admanagerapp.systemsettingplugin.common.ReasonRecordFactory;
import adtekfuji.admanagerapp.systemsettingplugin.utils.ResponseUtils;
import adtekfuji.clientservice.ReasonInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
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
import jp.adtekfuji.adFactory.entity.master.ReasonInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.property.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author
 */
@FxComponent(id = "CallReasonEditCompo", fxmlPath = "/fxml/compo/call_reason_edit_compo.fxml")
public class CallReasonEditCompoFxController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ReasonInfoFacade reasonInfoFacade = new ReasonInfoFacade();

    private LinkedList<ReasonInfoEntity> cacheData = new LinkedList<>();
    private LinkedList<ReasonInfoEntity> opeData = new LinkedList<>();

    private static Thread registThread = null;
    private Long order;

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
    private void OnRegist(ActionEvent event) {
        logger.fatal("regist start");

        runRegistThread();
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
     * 編集用エンティティリストの作成
     *
     * @param entitys
     * @return
     */
    private List<ReasonInfoEntity> cloneReason(List<ReasonInfoEntity> entitys) {

        List<ReasonInfoEntity> cloneEntitys = new ArrayList<>();

        entitys.stream().map((entity) -> {
            return new ReasonInfoEntity(entity);
        }).forEach((cloneEntity) -> {
            cloneEntitys.add(cloneEntity);
        });

        return cloneEntitys;
    }

    /**
     * 入力項目チェック
     *
     * @return 入力データ有効:true, 入力エラー:false
     */
    private boolean validItems() {
        //重複チェック
        Set<String> set = new HashSet<>();
        for (ReasonInfoEntity reason : opeData) {
            // 別IDで登録されている名称は設定不可
            long count = cacheData.stream().filter(p -> p.getReason().equals(reason.getReason()) && !p.getId().equals(reason.getId())).count();
            if (count > 0) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), reason.getReason()));
                return false;
            }

            if (set.contains(reason.getReason())) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), reason.getReason()));
                return false;
            } else {
                set.add(reason.getReason());
            }
        }

        return true;
    }

    /**
     * 未入力チェック
     *
     * @return 未入力:true
     */
    private boolean checkEmpty() {
        if (getAdditionalDatas().stream().anyMatch((e) -> (Objects.isNull(e.getReason()) || e.getReason().isEmpty()))) {
            return true;
        }
        if (getUpdateDatas().stream().anyMatch((e) -> (Objects.isNull(e.getReason()) || e.getReason().isEmpty()))) {
            return true;
        }

        return false;
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

            /*
            if (!this.getRemovedDatas().isEmpty()) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.contactRemoveBreaktimeAssignedOrganization"));
                if (ret.equals(ButtonType.CANCEL)) {
                    return false;
                }
            }
             */
            isCancel = false;

            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
                    ResponseEntity res = null;
                    logger.info("Delete");
                    for (ReasonInfoEntity entity : getRemovedDatas()) {
                        entity.updateData();
                        res = reasonInfoFacade.remove(entity.getId());
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }
                    logger.info("new create");
                    for (ReasonInfoEntity entity : getAdditionalDatas()) {
                        entity.updateData();
                        res = reasonInfoFacade.add(entity);
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }
                    logger.info("Update");
                    for (ReasonInfoEntity entity : getUpdateDatas()) {
                        entity.updateData();
                        res = reasonInfoFacade.update(entity);
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
                        cacheData.clear();
                        opeData.clear();

                        //理由全件取得
                        cacheData = new LinkedList<>(reasonInfoFacade.findType(ReasonTypeEnum.TYPE_CALL));
                        opeData = new LinkedList<>(cloneReason(cacheData));

                        //オーダー順でソートする
                        opeData.sort(Comparator.comparing(order -> order.getReasonOrder()));

                        Platform.runLater(() -> {
                            propertyPane.getChildren().clear();
                            Table table = new Table(propertyPane.getChildren()).isAddRecord(Boolean.TRUE)
                                    .isColumnTitleRecord(Boolean.TRUE).title(LocaleUtils.getString("key.EditCallReason")).styleClass("ContentTitleLabel");
                            table.setAbstractRecordFactory(new ReasonRecordFactory(table, opeData));
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
    private List<ReasonInfoEntity> getAdditionalDatas() {
        ArrayList<ReasonInfoEntity> subArr1 = new ArrayList(opeData);
        ArrayList<ReasonInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(cacheData);
        subArr1.stream().forEach((s) -> {
            if (s.getId() == null) {
                logger.debug("追加されたデータ:{}", s.getReason());
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
    private List<ReasonInfoEntity> getUpdateDatas() {
        ArrayList<ReasonInfoEntity> subArr1 = new ArrayList(opeData);
        ArrayList<ReasonInfoEntity> subArr2 = new ArrayList();

        order = 0L;
        subArr1.stream().forEach((s) -> {
            s.setReasonOrder(order);
            order += 1;
        });
        subArr1.removeAll(cacheData);

        subArr1.stream().forEach((s) -> {
            if (s.getId() != null) {
                logger.debug("編集されたデータ:{}", s.getReason());
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
    private List<ReasonInfoEntity> getRemovedDatas() {
        boolean delFlag = true;
        ArrayList<ReasonInfoEntity> subArr1 = new ArrayList(cacheData);
        ArrayList<ReasonInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(opeData);
        for (ReasonInfoEntity s : subArr1) {
            for (ReasonInfoEntity op : opeData) {
                if (Objects.equals(s.getId(), op.getId())) {
                    delFlag = false;
                    break;
                }
            }
            if (!delFlag) {
                delFlag = true;
            } else {
                logger.debug("削除されたデータ:{}", s.getReason());
                subArr2.add(s);
            }
        }
        return subArr2;
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
