/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.component.tree;

import adtekfuji.admanagerapp.unittemplateplugin.component.UnitTemplateListCompoInterface;
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
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.forfujiapp.clientservice.AccessHierarchyFujiInfoFacade;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.clientservice.UriConvertUtils;
import jp.adtekfuji.forfujiapp.common.AdFactoryForFujiClientAppConfig;
import jp.adtekfuji.forfujiapp.common.EntityConstants;
import jp.adtekfuji.forfujiapp.common.UIControlInterface;
import jp.adtekfuji.forfujiapp.common.UnitTemplateEditPermanenceData;
import jp.adtekfuji.forfujiapp.entity.accessfuji.AccessHierarchyFujiTypeEnum;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.javafx.tree.edior.UnitTemplateTreeEditor;
import jp.adtekfuji.javafxcommon.selectcompo.AccessAuthSettingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレート階層ツリー
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTemplateTreeController implements Initializable, UIControlInterface {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle RB = LocaleUtils.getBundle("locale.locale");
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final UnitTemplateListCompoInterface listCompoInterface;
    private final AdFactoryForFujiClientAppConfig config = new AdFactoryForFujiClientAppConfig();
    private final UnitTemplateEditPermanenceData unitTemplateEditPermanenceData = UnitTemplateEditPermanenceData.getInstance();

    private final static int REGEX_NAME_NUMBER = 256;

    private UnitTemplateTreeEditor unitTemplateTreeEditor;

    @FXML
    private TreeView<UnitTemplateHierarchyInfoEntity> hierarchyTree;
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

    public UnitTemplateTreeController(UnitTemplateListCompoInterface listCompoInterface) {
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
        logger.info(UnitTemplateTreeController.class.getName() + ":initialize start");

        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.RIGHT_ACCESS)) {
            hierarchyBtnArea.getItems().remove(authButton);
        }

        // 階層選択時
        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<UnitTemplateHierarchyInfoEntity>> observable, TreeItem<UnitTemplateHierarchyInfoEntity> oldValue, TreeItem<UnitTemplateHierarchyInfoEntity> newValue) -> {
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        if (Objects.nonNull(newValue) && newValue.getValue().getUnitTemplateHierarchyId() != EntityConstants.ROOT_ID) {
                            listCompoInterface.updateTable(newValue.getValue());
                            unitTemplateEditPermanenceData.setSelectedUnitTemplateHierarchy(newValue);
                        } else {
                            listCompoInterface.clearTable();
                            unitTemplateEditPermanenceData.setSelectedUnitTemplateHierarchy(null);
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
        this.unitTemplateTreeEditor = new UnitTemplateTreeEditor(this.hierarchyTree, new TreeItem<>(new UnitTemplateHierarchyInfoEntity(0L, null, LocaleUtils.getString("key.UnitTemplateHierarchy")), rootIcon), this);

        logger.info(UnitTemplateTreeController.class.getName() + ":initialize end");
    }

    /**
     * 権限編集ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onAuthButton(ActionEvent event) {
        logger.info(UnitTemplateTreeController.class.getName() + ":onAuthButton start");
        try {
            TreeItem<UnitTemplateHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && !item.getValue().getUnitTemplateHierarchyId().equals(0L)) {
                //ダイアログに表示させるデータを設定
                AccessHierarchyFujiTypeEnum type = AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy;
                long id = item.getValue().getUnitTemplateHierarchyId();
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
        logger.info(UnitTemplateTreeController.class.getName() + ":onAuthButton end");
    }

    /**
     * 新規作成ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onCreateButton(ActionEvent event) {
        logger.info(UnitTemplateTreeController.class.getName() + ":onCreateButton start");
        try {
            TreeItem<UnitTemplateHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
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

                UnitTemplateHierarchyInfoEntity hierarchy = new UnitTemplateHierarchyInfoEntity();
                hierarchy.setHierarchyName(hierarchyName);
                hierarchy.setParentId(item.getValue().getUnitTemplateHierarchyId());

                ResponseEntity rs = RestAPI.registUnitTemplateHierarchy(hierarchy);
                if (rs.isSuccess()) {
                    unitTemplateTreeEditor.updateTreeItemThread(item, UriConvertUtils.getUriToId(rs.getUri()));
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(UnitTemplateTreeController.class.getName() + ":onCreateButton end");
    }

    /**
     * 編集ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onEditButton(ActionEvent event) {
        logger.info(UnitTemplateTreeController.class.getName() + ":onEditButton start");
        try {
            TreeItem<UnitTemplateHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && item.getValue().getUnitTemplateHierarchyId() != 0) {
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
                    ResponseEntity res = RestAPI.updateUnitTemplateHierarchy(item.getValue());
                    if (res.isSuccess()) {
                        unitTemplateTreeEditor.updateTreeItemThread(item.getParent(), item.getValue().getUnitTemplateHierarchyId());
                    } else {
                        //TODO:エラー時の処理
                        item.getValue().setHierarchyName(orgName);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(UnitTemplateTreeController.class.getName() + ":onEditButton end");
    }

    /**
     * 削除ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onDeleteButton(ActionEvent event) {
        logger.info(UnitTemplateTreeController.class.getName() + ":onDeleteButton start");
        try {
            TreeItem<UnitTemplateHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && Objects.nonNull(item.getParent())) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getValue().getHierarchyName());
                if (ret.equals(ButtonType.OK)) {
                    ResponseEntity res = RestAPI.deleteUnitTemplateHierarchy(item.getValue());
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        unitTemplateTreeEditor.updateTreeItemThread(item.getParent(), item.getValue().getUnitTemplateHierarchyId());
                    } else {
                        //TODO:エラー時の処理
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(UnitTemplateTreeController.class.getName() + ":onDeleteButton end");
    }

    public void updateTree() {
        unitTemplateTreeEditor.updateListItemThread(hierarchyTree.getSelectionModel().getSelectedItem());
    }

    /**
     * 選択されている階層データを取得
     *
     * @return 選択されている階層情報
     */
    public UnitTemplateHierarchyInfoEntity getSelectHierarchy() {
        if (hierarchyTree.getSelectionModel().isEmpty()) {
            return null;
        } else if (hierarchyTree.getSelectionModel().getSelectedItem().getValue().getUnitTemplateHierarchyId().equals(EntityConstants.ROOT_ID)) {
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
