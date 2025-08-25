/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this  file, choose Tools | s
 * and open the  in the editor.
 */
package adtekfuji.admanagerapp.unitplugin.component.tree;

import jp.adtekfuji.forfujiapp.javafx.tree.edior.UnitTreeEditor;
import jp.adtekfuji.forfujiapp.common.UIControlInterface;
import adtekfuji.admanagerapp.unitplugin.component.UnitListCompoInterface;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.clientservice.UriConvertUtils;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.forfujiapp.clientservice.AccessHierarchyFujiInfoFacade;
import jp.adtekfuji.forfujiapp.common.AdFactoryForFujiClientAppConfig;
import jp.adtekfuji.forfujiapp.common.EntityConstants;
import jp.adtekfuji.forfujiapp.common.UnitEditPermanenceData;
import jp.adtekfuji.forfujiapp.entity.accessfuji.AccessHierarchyFujiTypeEnum;
import jp.adtekfuji.forfujiapp.entity.unit.UnitHierarchyInfoEntity;
import jp.adtekfuji.javafxcommon.selectcompo.AccessAuthSettingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニット階層ツリー
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTreeController implements Initializable, UIControlInterface {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle RB = LocaleUtils.getBundle("locale.locale");
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final UnitListCompoInterface listCompoInterface;
    private final AdFactoryForFujiClientAppConfig config = new AdFactoryForFujiClientAppConfig();
    private final UnitEditPermanenceData unitEditPermanenceData = UnitEditPermanenceData.getInstance();
    private final static int REGEX_NAME_NUMBER = 256;

    private UnitTreeEditor unitTreeEditor;

    @FXML
    private TreeView<UnitHierarchyInfoEntity> hierarchyTree;
    @FXML
    private ToolBar hierarchyBtnArea;
    @FXML
    private Button authButton;
    @FXML
    private Button craeateButton;
    @FXML
    private Button editButton;
    @FXML
    private Button copyButton;
    @FXML
    private Button deleteButton;

    public UnitTreeController(UnitListCompoInterface listCompoInterface) {
        this.listCompoInterface = listCompoInterface;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(UnitTreeController.class.getName() + ":initialize start");

        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.RIGHT_ACCESS)) {
            hierarchyBtnArea.getItems().remove(authButton);
        }

        // 階層選択時
        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<UnitHierarchyInfoEntity>> observable, TreeItem<UnitHierarchyInfoEntity> oldValue, TreeItem<UnitHierarchyInfoEntity> newValue) -> {
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        if (Objects.nonNull(newValue) && newValue.getValue().getUnitHierarchyId() != EntityConstants.ROOT_ID) {
                            listCompoInterface.updateTable(newValue.getValue());
                            unitEditPermanenceData.setSelectedUnitHierarchy(newValue);
                        } else {
                            listCompoInterface.clearTable();
                            unitEditPermanenceData.setSelectedUnitHierarchy(null);
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    blockUI(false);
                }

                @Override
                protected void failed() {
                    super.failed();
                    blockUI(false);
                }
            };
            new Thread(task).start();
        });

        Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("/image/folder_top.png")));
        unitTreeEditor = new UnitTreeEditor(
                hierarchyTree, new TreeItem<>(new UnitHierarchyInfoEntity(0L, null, LocaleUtils.getString("key.UnitHierarchy")), rootIcon), this);

        logger.info(UnitTreeController.class.getName() + ":initialize end");
    }

    /**
     * 新規作成ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onAuthButton(ActionEvent event) {
        logger.info(UnitTreeController.class.getName() + ":onAuthButton start");
        try {
            TreeItem<UnitHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && !item.getValue().getUnitHierarchyId().equals(0L)) {
                //ダイアログに表示させるデータを設定
                AccessHierarchyFujiTypeEnum type = AccessHierarchyFujiTypeEnum.UnitHierarchy;
                long id = item.getValue().getUnitHierarchyId();
                AccessHierarchyFujiInfoFacade accessHierarchyFujiInfoFacade = new AccessHierarchyFujiInfoFacade();
                long count = accessHierarchyFujiInfoFacade.getCount(type, id);
                long range = 100;
                List<OrganizationInfoEntity> deleteList = new ArrayList();
                for (long from = 0; from <= count; from += range) {
                    List<OrganizationInfoEntity> entities = accessHierarchyFujiInfoFacade.getRange(type, id, from, from + range - 1);
                    deleteList.addAll(entities);
                }
                AccessAuthSettingEntity accessAuthSettingEntity 
                        = new AccessAuthSettingEntity(item.getValue().getHierarchyName(), deleteList);
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.EditedAuth"), "AccessAuthSettingCompo", accessAuthSettingEntity);
                if (ret.equals(ButtonType.OK)) {
                    List<OrganizationInfoEntity> registList = accessAuthSettingEntity.getAuthOrganizations();
                    for(int i=0; i<registList.size(); i++) {
                        OrganizationInfoEntity o = registList.get(i);
                        if(deleteList.contains(o)) {
                            deleteList.remove(o);
                            registList.remove(o);
                            i--;
                        }
                    }
                    if(!deleteList.isEmpty()) {
                        accessHierarchyFujiInfoFacade.delete(type, id, deleteList);
                    }
                    if(!registList.isEmpty()) {
                        accessHierarchyFujiInfoFacade.regist(type, id, registList);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(UnitTreeController.class.getName() + ":onAuthButton end");
    }

    /**
     * 新規作成ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onCreateButton(ActionEvent event) {
        logger.info(UnitTreeController.class.getName() + ":onCreateButton start");
        try {
            TreeItem<UnitHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item)) {
                String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
                String hierarchyName = sc.showTextInputDialog(LocaleUtils.getString("key.NewCreate"), message, LocaleUtils.getString("key.HierarchyName"), "");
                if (Objects.isNull(hierarchyName)) {
                    return;
                } else if (hierarchyName.isEmpty()) {
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                    return;
                } else if (hierarchyName.length() > REGEX_NAME_NUMBER) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.NewCreate"), LocaleUtils.getString("key.warn.enterCharacters256"));
                    return;
                }

                UnitHierarchyInfoEntity hierarchy = new UnitHierarchyInfoEntity();
                hierarchy.setHierarchyName(hierarchyName);
                hierarchy.setParentId(item.getValue().getUnitHierarchyId());

                ResponseEntity rs = RestAPI.registUnitHierarchy(hierarchy);
                if (rs.isSuccess()) {
                    unitTreeEditor.updateTreeItemThread(item, UriConvertUtils.getUriToId(rs.getUri()));
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(UnitTreeController.class.getName() + ":onCreateButton end");
    }

    /**
     * 編集ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onEditButton(ActionEvent event) {
        logger.info(UnitTreeController.class.getName() + ":onEditButton start");
        try {
            TreeItem<UnitHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && item.getValue().getUnitHierarchyId() != 0) {
                String orgName = item.getValue().getHierarchyName();
                String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
                String hierarchyName = sc.showTextInputDialog(LocaleUtils.getString("key.Edit"), message, LocaleUtils.getString("key.HierarchyName"), orgName);
                if (Objects.isNull(hierarchyName)) {
                    return;
                } else if (hierarchyName.isEmpty()) {
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                    return;
                } else if (hierarchyName.length() > REGEX_NAME_NUMBER) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Edit"), LocaleUtils.getString("key.warn.enterCharacters256"));
                    return;
                }

                if (!orgName.equals(hierarchyName)) {
                    item.getValue().setHierarchyName(hierarchyName);
                    ResponseEntity rs = RestAPI.updateUnitHierarchy(item.getValue());
                    if (rs.isSuccess()) {
                        unitTreeEditor.updateTreeItemThread(item.getParent(), item.getValue().getUnitHierarchyId());
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(UnitTreeController.class.getName() + ":onEditButton end");
    }

    /**
     * 削除ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onDeleteButton(ActionEvent event) {
        logger.info(UnitTreeController.class.getName() + ":onDeleteButton start");
        try {
            TreeItem<UnitHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && Objects.nonNull(item.getParent())) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getValue().getHierarchyName());
                if (ret.equals(ButtonType.OK)) {
                    ResponseEntity rs = RestAPI.deleteUnitHierarchy(item.getValue());
                    if (rs.isSuccess()) {
                        unitTreeEditor.updateTreeItemThread(item.getParent(), item.getValue().getUnitHierarchyId());
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(UnitTreeController.class.getName() + ":onDeleteButton end");
    }

    public void updateTree() {
        unitTreeEditor.updateListItemThread(hierarchyTree.getSelectionModel().getSelectedItem());
    }

    /**
     * 選択されている階層データを取得
     *
     * @return 選択されている階層情報
     */
    public UnitHierarchyInfoEntity getSelectHierarchy() {
        if (hierarchyTree.getSelectionModel().isEmpty()) {
            return null;
        } else if (hierarchyTree.getSelectionModel().getSelectedItem().getValue().getUnitHierarchyId().equals(EntityConstants.ROOT_ID)) {
            return null;
        }
        return hierarchyTree.getSelectionModel().getSelectedItem().getValue();
    }

    /**
     * 画面に使用制限をかける
     *
     * @param isBlock
     */
    @Override
    public void blockUI(boolean isBlock) {
        this.listCompoInterface.blockUI(isBlock);
    }

    @Override
    public void updateUI() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
