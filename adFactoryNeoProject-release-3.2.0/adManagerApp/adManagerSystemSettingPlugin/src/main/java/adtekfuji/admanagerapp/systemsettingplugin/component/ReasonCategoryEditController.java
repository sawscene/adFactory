/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.admanagerapp.systemsettingplugin.common.ReasonCategoryCell;
import adtekfuji.admanagerapp.systemsettingplugin.utils.ResponseUtils;
import adtekfuji.clientservice.ReasonCategoryInfoFacede;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ReasonCategoryInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 理由編集コントローラー
 * 
 * @author s-heya
 */
public class ReasonCategoryEditController implements Initializable, ChangeListener<TreeItem<ReasonCategoryInfoEntity>> {
    private final static Logger logger = LogManager.getLogger();

    private final ReasonCategoryInfoFacede reasonCategoryFacade = new ReasonCategoryInfoFacede();
    private TreeItem<ReasonCategoryInfoEntity> rootItem;
    private ReasonTypeEnum reasonType;

    @FXML
    protected TreeView<ReasonCategoryInfoEntity> treeView;
    @FXML
    private Pane progress;
    @FXML
    private Button addReasonCategoryButton;
    @FXML
    private Button editReasonCategoryButton;
    @FXML
    private Button deleteReasonCategoryButton;
    
    /**
     * 理由区分の切替え
     * 
     * @param observable
     * @param oldValue
     * @param newValue 
     */
    @Override
    public void changed(ObservableValue<? extends TreeItem<ReasonCategoryInfoEntity>> observable, TreeItem<ReasonCategoryInfoEntity> oldValue, TreeItem<ReasonCategoryInfoEntity> newValue) {
        if (Objects.nonNull(oldValue)
                && !Objects.equals(newValue.getValue(), oldValue.getValue())
                && !this.confirmChanges(oldValue.getValue())) {
            this.treeView.getSelectionModel().selectedItemProperty().removeListener(this);
            this.treeView.getSelectionModel().select(oldValue);
            this.treeView.getSelectionModel().selectedItemProperty().addListener(this);
            return;
        }
        this.selectedReasonCategory(newValue.getValue());
    };

    /**
     * 画面を初期化する。
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.blockUI(false);
        
        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            this.addReasonCategoryButton.setDisable(true);
            this.editReasonCategoryButton.setDisable(true);
            this.deleteReasonCategoryButton.setDisable(true);
        }
        
        this.treeView.getSelectionModel().selectedItemProperty().addListener(this);
    }

    /**
     * キーが押下された。
     * 
     * @param key
     */
    @FXML
    private void onKeyPressed(KeyEvent key) {
        if (KeyCode.F5.equals(key.getCode())) {
            this.treeView.getRoot().setExpanded(false);
            this.createRoot();
        }
    }

    /**
     * 理由区分を追加する。
     *
     * @param event
     */
    @FXML
    private void onAddReasonCategory(ActionEvent event) {
        try {
            if (!this.confirmChanges(getSelectedReasonCategory())) {
                return;
            }

            SceneContiner sc = SceneContiner.getInstance();

            ReasonCategoryInfoEntity category = new ReasonCategoryInfoEntity();
            category.setReasonType(this.reasonType);

            if (this.editReasonCategory(category)) {
                ResponseEntity res = reasonCategoryFacade.add(category);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    this.treeView.getRoot().setExpanded(false);
                    
                    updateTree(ResponseUtils.uriToId(res.getUri()));
                    
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.WorkCategoryEdit"),
                            String.format(LocaleUtils.getString("key.FailedToCreate"), LocaleUtils.getString("key.WorkClassification")));
                }
            }
            
            // キャッシュをクリア
            CacheUtils.removeCacheData(ReasonCategoryInfoEntity.class);
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 理由区分を編集する。
     *
     * @param event
     */
    @FXML
    private void onEditReasonCategory(ActionEvent event) {
        try {
            if (!this.confirmChanges(getSelectedReasonCategory())) {
                return;
            }

            SceneContiner sc = SceneContiner.getInstance();

            TreeItem<ReasonCategoryInfoEntity> item = this.treeView.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getValue().getId())) {
                return;
            }

            ReasonCategoryInfoEntity category = this.treeView.getSelectionModel().getSelectedItem().getValue();
            String name = category.getReasonCategoryName();

            if (this.editReasonCategory(category)) {
                //作業区分を更新
                ResponseEntity res = reasonCategoryFacade.update(category);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    this.treeView.getRoot().setExpanded(false);
                    updateTree(category.getId());

                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.WorkCategoryEdit"),
                            String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.WorkClassification")));

                    category.setReasonCategoryName(name);

                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.WorkCategoryEdit"), LocaleUtils.getString("key.alert.differentVerInfo"));

                    this.treeView.getRoot().setExpanded(false);

                    updateTree(category.getId());
                } else {
                    category.setReasonCategoryName(name);
                }
            }

            // キャッシュをクリア
            CacheUtils.removeCacheData(ReasonCategoryInfoEntity.class);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 理由区分を削除する。
     * 
     * @param event 
     */
    @FXML
    private void onDeleteReasonCategory(ActionEvent event) {
        try {
            if (!this.confirmChanges(getSelectedReasonCategory())) {
                return;
            }

            SceneContiner sc = SceneContiner.getInstance();

            this.blockUI(true);
            
            TreeItem<ReasonCategoryInfoEntity> item = this.treeView.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getValue().getId())) {
                return;
            }
            
            if (item.getValue().isDefaultReasonCategory()) {
                // デフォルトの理由区分の場合は、「情報が保護されているため削除できません」を表示
                sc.showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.confirm"), LocaleUtils.getString("key.DeleteErrProtectedData"), 
                        item.getValue().getReasonCategoryName());
                return;
            }

            // 削除確認メッセージを表示
            ButtonType buttonType = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION,
                    LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getValue().getReasonCategoryName());
            if (ButtonType.CANCEL.equals(buttonType)) {
                return;
            }

            ReasonCategoryInfoEntity category = this.treeView.getSelectionModel().getSelectedItem().getValue();

            // 理由区分を削除
            ResponseEntity res = this.reasonCategoryFacade.remove(category.getId());
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                this.treeView.getRoot().setExpanded(false);
                updateTree(item.getValue().getId());

            } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.EXIST_RELATION_DELETE)) {
                sc.showAlert(Alert.AlertType.WARNING,
                        LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.WorkCategory.Delete.RelationExist"));

            } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ReasonCategoryEdit"),
                        String.format(LocaleUtils.getString("key.FailedToDelete"), LocaleUtils.getString("key.WorkClassification")));
            }

            // キャッシュをクリア
            CacheUtils.removeCacheData(ReasonCategoryInfoEntity.class);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }
    
    /**
     * 変更内容を確認する。
     * 
     * @return true: 処理続行、false: 処理キャンセル
     */
    private boolean confirmChanges(ReasonCategoryInfoEntity category) {
        if (this.isChanged()) {
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            ButtonType buttonType = SceneContiner.getInstance().showMessageBox(Alert.AlertType.NONE, title, message,
                    new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);

            if (ButtonType.CANCEL.equals(buttonType)) {
                return false;
            }

            if (ButtonType.YES.equals(buttonType) && !this.regist(category)) {
                return false;
            } 
        }
        return true;
    }

    /**
     * 理由区分を取得する。
     * 
     * @return 理由区分
     */
    public ReasonTypeEnum getReasonType() {
        return reasonType;
    }
    
    /**
     * 理由区分を設定する。
     * 
     * @param reasonType 理由区分 
     */
    protected void setReasonCategory(ReasonTypeEnum reasonType) {
        this.reasonType = reasonType;
    }

    /**
     * 理由区分ツリーを更新する。
     *
     * @param id
     */
    public void updateTree(Long id) {
        this.blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    createRoot();
                    Platform.runLater(() -> {
                        treeView.getRoot().setExpanded(true);
                        select(treeView.getRoot(), id);
                    });
                } finally {
                    Platform.runLater(() -> blockUI(false));                    
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 理由区分ツリーのルートアイテムを作成する。
     *
     */
    private void createRoot() {
        if (Objects.isNull(rootItem)) {
            //ツリールート作成
            this.rootItem = new TreeItem<>(new ReasonCategoryInfoEntity(0L, this.reasonType, LocaleUtils.getString("key.ReasonCategory")));
            Platform.runLater(() -> {
                this.treeView.rootProperty().setValue(this.rootItem);
                this.treeView.setCellFactory(o -> new ReasonCategoryCell());
            });
        }

        this.rootItem.setExpanded(false);
        this.rootItem.getChildren().clear();

        List<ReasonCategoryInfoEntity> entities = reasonCategoryFacade.findType(this.reasonType);
        entities.forEach(o -> rootItem.getChildren().add(new TreeItem<>(o)));

        this.rootItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getReasonCategoryName()));
        Platform.runLater(() -> this.treeView.setCellFactory(o -> new ReasonCategoryCell()));
    }
    
    /**
     * 理由区分を選択する。
     * 
     * @param parent
     * @param id 
     */
    private void select(TreeItem<ReasonCategoryInfoEntity> parent, Long id) {
        Optional<TreeItem<ReasonCategoryInfoEntity>> opt = parent.getChildren().stream().
                filter(o -> id.equals(o.getValue().getId())).findFirst();
        if (opt.isPresent()) {
            this.treeView.getSelectionModel().select(opt.get());
        }
    }

    /**
     * 理由区分を編集する
     * 
     * @param category
     * @return 
     */
    private boolean editReasonCategory(ReasonCategoryInfoEntity category) {
        try {
            String orgName = category.getReasonCategoryName();
            if (Objects.isNull(orgName)) {
                orgName = "";
            }

            SceneContiner sc = SceneContiner.getInstance();
            String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.WorkCategoryName"));
            String newName = sc.showTextInputDialog(LocaleUtils.getString("key.Edit"), message, LocaleUtils.getString("key.WorkCategoryName"), orgName);

            if (Objects.nonNull(newName)) {
                newName = adtekfuji.utility.StringUtils.trim2(newName);
                if (newName.isEmpty()) {
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                } else if (!Objects.equals(newName, orgName)) {
                    category.setReasonCategoryName(newName);
                    return true;
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return false;
    }

    /**
     * 画面操作を無効にする。
     *
     * @param block
     */
    protected void blockUI(boolean block) {
        SceneContiner.getInstance().blockUI("ContentNaviPane", block);
        this.progress.setVisible(block);
    }

    /**
     * 理由が変更されたかどうかを返す。
     * 
     * @return 
     */
    protected boolean isChanged() {
        return true;
    }

    /**
     * 理由区分が選択された。
     * 
     * @param category 理由区分情報
     */
    protected void selectedReasonCategory(ReasonCategoryInfoEntity category) {
    }
    
    /**
     * 現在の理由区分を取得する。
     * 
     * @return 理由区分情報
     */
    protected ReasonCategoryInfoEntity getSelectedReasonCategory() {
        if (Objects.isNull(this.treeView.getSelectionModel().getSelectedItem())) {
            return null;
        }
        return this.treeView.getSelectionModel().getSelectedItem().getValue();
    }
    
    /**
     * 理由を登録する。
     * 
     * @param category 理由区分
     * @return 
     */
    protected boolean regist(ReasonCategoryInfoEntity category) {
        return true;
    }
}
