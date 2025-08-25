/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.admanagerapp.systemsettingplugin.common.RoleRecordFactory;
import adtekfuji.admanagerapp.systemsettingplugin.utils.ResponseUtils;
import adtekfuji.clientservice.RoleInfoFacade;
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
import jp.adtekfuji.adFactory.entity.master.RoleAuthorityInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.property.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 休憩設定画面
 *
 * @author ke.yokoi
 */
@FxComponent(id = "RoleEditCompo", fxmlPath = "/fxml/compo/role_edit_compo.fxml")
public class RoleEditCompoFxController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static RoleInfoFacade roleInfoFacade = new RoleInfoFacade();

    private LinkedList<RoleAuthorityInfoEntity> cashDatas = new LinkedList<>();
    private LinkedList<RoleAuthorityInfoEntity> operatDatas = new LinkedList<>();

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
    private void OnRegist(ActionEvent event) {
        logger.info("regist start");

        runRegistThread();
    }

    /**
     * 保存を実施する
     *
     * @return 保存を行わなかったときfalse
     */
    private boolean runRegistThread() {
        Boolean isCancel = true;
        try {
            blockUI(true);

            if (checkEmpty()) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return false;
            }

            if (!validItems()) {
                return false;
            }

            if (!getRemovedDatas().isEmpty()) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.contactRemoveRoleAssignedOrganization"));
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
                    for (RoleAuthorityInfoEntity entity : getRemovedDatas()) {
                        entity.updateData();
                        res = roleInfoFacade.remove(entity.getRoleId());
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }
                    logger.info("new create");
                    for (RoleAuthorityInfoEntity entity : getAdditionalDatas()) {
                        entity.updateData();
                        res = roleInfoFacade.add(entity);
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }
                    logger.info("Update");
                    for (RoleAuthorityInfoEntity entity : getUpdateDatas()) {
                        entity.updateData();
                        res = roleInfoFacade.update(entity);
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
                        cashDatas.clear();
                        operatDatas.clear();

                        // ロール情報を取得
                        cashDatas = new LinkedList<>(roleInfoFacade.findAll());
                        operatDatas = new LinkedList<>(cloneRoleInfo(cashDatas));
                        operatDatas.sort(Comparator.comparing(role -> role.getRoleName()));
                        Platform.runLater(() -> {
                            propertyPane.getChildren().clear();
                            Table table = new Table(propertyPane.getChildren()).isAddRecord(Boolean.TRUE)
                                    .isColumnTitleRecord(Boolean.TRUE).title(LocaleUtils.getString("key.EditRole")).styleClass("ContentTitleLabel");
                            table.setAbstractRecordFactory(new RoleRecordFactory(table, operatDatas));
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
    private List<RoleAuthorityInfoEntity> getAdditionalDatas() {
        ArrayList<RoleAuthorityInfoEntity> subArr1 = new ArrayList(operatDatas);
        ArrayList<RoleAuthorityInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(cashDatas);
        subArr1.stream().forEach((s) -> {
            if (s.getRoleId() == null) {
                logger.debug("追加されたデータ:{}", s.getRoleName());
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
    private List<RoleAuthorityInfoEntity> getUpdateDatas() {
        ArrayList<RoleAuthorityInfoEntity> subArr1 = new ArrayList(operatDatas);
        ArrayList<RoleAuthorityInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(cashDatas);
        subArr1.stream().forEach((s) -> {
            if (s.getRoleId() != null) {
                logger.debug("編集されたデータ:{}", s.getRoleName());
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
    private List<RoleAuthorityInfoEntity> getRemovedDatas() {
        boolean delFlag = true;
        ArrayList<RoleAuthorityInfoEntity> subArr1 = new ArrayList(cashDatas);
        ArrayList<RoleAuthorityInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(operatDatas);
        for (RoleAuthorityInfoEntity s : subArr1) {
            for (RoleAuthorityInfoEntity op : operatDatas) {
                if (Objects.equals(s.getRoleId(), op.getRoleId())) {
                    delFlag = false;
                    break;
                }
            }
            if (!delFlag) {
                delFlag = true;
            } else {
                logger.debug("削除されたデータ:{}", s.getRoleName());
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
    private List<RoleAuthorityInfoEntity> cloneRoleInfo(List<RoleAuthorityInfoEntity> entitys) {

        List<RoleAuthorityInfoEntity> cloneEntitys = new ArrayList<>();

        entitys.stream().map((entity) -> {
            return new RoleAuthorityInfoEntity(entity);
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
        if (getAdditionalDatas().stream().anyMatch((e) -> (Objects.isNull(e.getRoleName()) || e.getRoleName().isEmpty()))) {
            return true;
        }
        if (getUpdateDatas().stream().anyMatch((e) -> (Objects.isNull(e.getRoleName()) || e.getRoleName().isEmpty()))) {
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
        // 重複チェック
        Set<String> set = new HashSet<>();
        for (RoleAuthorityInfoEntity role : operatDatas) {
            // 別IDで登録されている名称は設定不可
            long count = cashDatas.stream().filter(p -> p.getRoleName().equals(role.getRoleName()) && !p.getRoleId().equals(role.getRoleId())).count();
            if (count > 0) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), role.getRoleName()));
                return false;
            }

            if (set.contains(role.getRoleName())) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), role.getRoleName()));
                return false;
            } else {
                set.add(role.getRoleName());
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
