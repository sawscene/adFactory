/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.admanagerapp.systemsettingplugin.common.IndirectWorkRecordFactory;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.IndirectWorkInfoFacade;
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
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.javafxcommon.property.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 間接作業一覧の編集
 *
 * @author nar-nakamura
 */
@FxComponent(id = "IndirectWorkEditCompo", fxmlPath = "/fxml/admanagersystemsettingplugin/indirect_work_edit_compo.fxml")
public class IndirectWorkEditCompoFxController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static IndirectWorkInfoFacade indirectWorkInfoFacade = new IndirectWorkInfoFacade();
    private final long MAX_LOAD_SIZE = ClientServiceProperty.getRestRangeNum();

    private LinkedList<IndirectWorkInfoEntity> cashDatas = new LinkedList<>();
    private LinkedList<IndirectWorkInfoEntity> dispDatas = new LinkedList<>();

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
     * 
     * 
     * @return 保存を行わなかったときfalse
     */
    private boolean runRegistThread() {
        if (checkEmpty()) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
            return false;
        }

        if (!validItems()) {
            return false;
        }
        
        Platform.runLater(() -> {
            blockUI(true);
        });
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    logger.info("Delete");
                    for (IndirectWorkInfoEntity entity : getRemovedDatas()) {
                        entity.updateMember();
                        indirectWorkInfoFacade.delete(entity);
                    }
                    logger.info("new create");
                    for (IndirectWorkInfoEntity entity : getAdditionalDatas()) {
                        entity.updateMember();
                        indirectWorkInfoFacade.regist(entity);
                    }
                    logger.info("Update");
                    for (IndirectWorkInfoEntity entity : getUpdateDatas()) {
                        entity.updateMember();
                        indirectWorkInfoFacade.update(entity);
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    Platform.runLater(() -> {
                        blockUI(false);
                        runUpdateViewThread();
                    });
                }
                return null;
            }
        };
        registThread = new Thread(task);
        registThread.start();

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
                        dispDatas.clear();

                        // 間接作業を取得
                        cashDatas = new LinkedList<>();
                        Long max = indirectWorkInfoFacade.count();
                        if (max > 0) {
                            for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
                                List<IndirectWorkInfoEntity> indirectWorks = indirectWorkInfoFacade.findRange(count, count + MAX_LOAD_SIZE - 1);
                                if (Objects.isNull(indirectWorks) || indirectWorks.isEmpty()) {
                                    break;
                                }
                                cashDatas.addAll(indirectWorks);
                            }
                        }

                        dispDatas = new LinkedList<>(cloneEditEntity(cashDatas));
                        dispDatas.sort(Comparator.comparing(indirectWork -> indirectWork.getWorkNumber()));
                        Platform.runLater(() -> {
                            propertyPane.getChildren().clear();
                            Table table = new Table(propertyPane.getChildren()).isAddRecord(Boolean.TRUE)
                                    .isColumnTitleRecord(Boolean.TRUE).title(LocaleUtils.getString("key.EditIndirectWork")).styleClass("ContentTitleLabel");
                            table.setAbstractRecordFactory(new IndirectWorkRecordFactory(table, dispDatas));
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
    private List<IndirectWorkInfoEntity> getAdditionalDatas() {
        ArrayList<IndirectWorkInfoEntity> subArr1 = new ArrayList(dispDatas);
        ArrayList<IndirectWorkInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(cashDatas);
        subArr1.stream().forEach((s) -> {
            if (Objects.isNull(s.getIndirectWorkId())) {
                logger.debug("追加されたデータ:{}", s);
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
    private List<IndirectWorkInfoEntity> getUpdateDatas() {
        ArrayList<IndirectWorkInfoEntity> subArr1 = new ArrayList(dispDatas);
        ArrayList<IndirectWorkInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(cashDatas);
        subArr1.stream().forEach((s) -> {
            if (Objects.nonNull(s.getIndirectWorkId())) {
                logger.debug("編集されたデータ:{}", s);
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
    private List<IndirectWorkInfoEntity> getRemovedDatas() {
        boolean delFlag = true;
        ArrayList<IndirectWorkInfoEntity> subArr1 = new ArrayList(cashDatas);
        ArrayList<IndirectWorkInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(dispDatas);
        for (IndirectWorkInfoEntity s : subArr1) {
            for (IndirectWorkInfoEntity op : dispDatas) {
                if (Objects.equals(s.getIndirectWorkId(), op.getIndirectWorkId())) {
                    delFlag = false;
                    break;
                }
            }
            if (!delFlag) {
                delFlag = true;
            } else {
                logger.debug("削除されたデータ:{}", s);
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
    private List<IndirectWorkInfoEntity> cloneEditEntity(List<IndirectWorkInfoEntity> entitys) {

        List<IndirectWorkInfoEntity> cloneEntitys = new ArrayList<>();

        entitys.stream().map((entity) -> {
            return new IndirectWorkInfoEntity(entity);
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

        // 分類番号
        if (getAdditionalDatas().stream().anyMatch((e) -> (Objects.isNull(e.getClassNumber()) || e.getClassNumber().isEmpty()))) {
            return true;
        }
        if (getUpdateDatas().stream().anyMatch((e) -> (Objects.isNull(e.getClassNumber()) || e.getClassNumber().isEmpty()))) {
            return true;
        }
        // 作業番号
        if (getAdditionalDatas().stream().anyMatch((e) -> (Objects.isNull(e.getWorkNumber()) || e.getWorkNumber().isEmpty()))) {
            return true;
        }
        if (getUpdateDatas().stream().anyMatch((e) -> (Objects.isNull(e.getWorkNumber()) || e.getWorkNumber().isEmpty()))) {
            return true;
        }
        // 作業名
        if (getAdditionalDatas().stream().anyMatch((e) -> (Objects.isNull(e.getWorkName()) || e.getWorkName().isEmpty()))) {
            return true;
        }
        if (getUpdateDatas().stream().anyMatch((e) -> (Objects.isNull(e.getWorkName()) || e.getWorkName().isEmpty()))) {
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
        for (IndirectWorkInfoEntity indirectWork : dispDatas) {
            if (set.contains(indirectWork.getWorkNumber())) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), indirectWork.getWorkNumber()));
                return false;
            } else {
                set.add(indirectWork.getWorkNumber());
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
