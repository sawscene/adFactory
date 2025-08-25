package adtekfuji.admanagerapp.unittemplateplugin.component.table;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import adtekfuji.admanagerapp.unittemplateplugin.component.UnitTemplateListCompoInterface;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレートテーブル
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTemplateTableController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final UnitTemplateListCompoInterface listCompoInterface;

    @FXML
    private PropertySaveTableView<UnitTemplateTableDataEntity> unittemplateTable;
    @FXML
    private TableColumn unittemplateColumn;
    @FXML
    private TableColumn updateByColumn;
    @FXML
    private TableColumn updateDateColumn;
    @FXML
    private Button craeateButton;
    @FXML
    private Button editButton;
    @FXML
    private Button copyButton;
    @FXML
    private Button deleteButton;

    public UnitTemplateTableController(UnitTemplateListCompoInterface listCompoInterface) {
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
        logger.info(UnitTemplateTableController.class.getName() + ":initialize start");

        unittemplateTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        unittemplateColumn.setCellValueFactory(new PropertyValueFactory("name"));
        updateByColumn.setCellValueFactory(new PropertyValueFactory("updatePersonName"));
        updateDateColumn.setCellValueFactory(new PropertyValueFactory("updateDatetime"));
        unittemplateTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        unittemplateTable.init("unittemplateTable");

        //工程順ダブルクリック
        unittemplateTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                onEditButton(new ActionEvent());
            }
        });

        logger.info(UnitTemplateTableController.class.getName() + ":initialize end");
    }

    /**
     * 新規作成ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onCreateButton(ActionEvent event) {
        logger.info(UnitTemplateTableController.class.getName() + ":onCreateButton start");

        try {
            UnitTemplateHierarchyInfoEntity hierarchy = this.listCompoInterface.getSelectTree();
            if (Objects.nonNull(hierarchy) && Objects.nonNull(hierarchy.getParentId())) {
                UnitTemplateInfoEntity info = new UnitTemplateInfoEntity();
                info.setParentId(hierarchy.getUnitTemplateHierarchyId());
                info.setParentName(hierarchy.getHierarchyName());
                info.setFkOutputKanbanHierarchyId(0l);
                Platform.runLater(() -> {
                    sc.setComponent("ContentNaviPane", "UnittemplateDetailComp", info);
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(UnitTemplateTableController.class.getName() + ":onCreateButton end");
    }

    /**
     * 編集ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onEditButton(ActionEvent event) {
        logger.info(UnitTemplateTableController.class.getName() + ":onEditButton start");

        try {
            if (Objects.isNull(unittemplateTable.getSelectionModel().getSelectedItem())) {
                return;
            }

            UnitTemplateInfoEntity selectedTemplate = unittemplateTable.getSelectionModel().getSelectedItem().getUnitTemplateInfoEntity();
            UnitTemplateHierarchyInfoEntity hierarchy = this.listCompoInterface.getSelectTree();
            if (Objects.nonNull(selectedTemplate) && Objects.nonNull(hierarchy)) {
                UnitTemplateInfoEntity info = RestAPI.getUnitTemplate(selectedTemplate.getUnitTemplateId());
                info.setParentName(hierarchy.getHierarchyName());
                Platform.runLater(() -> {
                    sc.setComponent("ContentNaviPane", "UnittemplateDetailComp", info);
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(UnitTemplateTableController.class.getName() + ":onEditButton end");
    }

    /**
     * 複製ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onCopyButton(ActionEvent event) {
        logger.info(UnitTemplateTableController.class.getName() + ":onCopyButton start");

        try {
            if (Objects.isNull(unittemplateTable.getSelectionModel().getSelectedItem())) {
                return;
            }

            UnitTemplateInfoEntity item = unittemplateTable.getSelectionModel().getSelectedItem().getUnitTemplateInfoEntity();
            UnitTemplateHierarchyInfoEntity hierarchy = this.listCompoInterface.getSelectTree();
            if (Objects.nonNull(item) && Objects.nonNull(hierarchy)) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Copy"), LocaleUtils.getString("key.CopyMessage"));
                if (ret.equals(ButtonType.OK)) {
                    ResponseEntity res = RestAPI.copyUnitTemplate(item);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        this.listCompoInterface.updateTree();
                    } else {
                        //TODO:エラー時の処理
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(UnitTemplateTableController.class.getName() + ":onCopyButton end");

    }

    /**
     * 削除ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onDeleteButton(ActionEvent event) {
        logger.info(UnitTemplateTableController.class.getName() + ":onDeleteButton start");

        try {
            if (Objects.isNull(unittemplateTable.getSelectionModel().getSelectedItem())) {
                return;
            }

            UnitTemplateInfoEntity item = unittemplateTable.getSelectionModel().getSelectedItem().getUnitTemplateInfoEntity();
            UnitTemplateHierarchyInfoEntity hierarchy = this.listCompoInterface.getSelectTree();
            if (Objects.nonNull(item) && Objects.nonNull(hierarchy)) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getUnitTemplateName());
                if (ret.equals(ButtonType.OK)) {
                    ResponseEntity res = RestAPI.deleteUnitTemplate(item);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        this.listCompoInterface.updateTree();
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(UnitTemplateTableController.class.getName() + ":onDeleteButton end");
    }

    /**
     * ユニットテンプレートテーブルの更新
     *
     * @param unitTemplates 表示する情報
     */
    public void updateTable(List<UnitTemplateInfoEntity> unitTemplates) {
        logger.info(UnitTemplateTableController.class.getName() + ":createTable start");

        List<UnitTemplateTableDataEntity> list = new ArrayList<>();

        for (UnitTemplateInfoEntity unitTemplate : unitTemplates) {
            UnitTemplateTableDataEntity data = new UnitTemplateTableDataEntity(unitTemplate);

            if (Objects.nonNull(unitTemplate.getFkUpdatePersonId())) {
                OrganizationInfoEntity organization = CacheUtils.getCacheOrganization(unitTemplate.getFkUpdatePersonId());
                data.setUpdatePersonName(organization.getOrganizationName());
            }

            list.add(data);
        }

        Platform.runLater(() -> {
            unittemplateTable.setItems(FXCollections.observableArrayList(list));
            unittemplateTable.getSortOrder().add(unittemplateColumn);
            logger.info(UnitTemplateTableController.class.getName() + ":createTable end");
        });
    }

    /**
     * ユニットテンプレートテーブルの初期化
     *
     */
    public void clearTableList() {
        Platform.runLater(() -> {
            unittemplateTable.getItems().clear();
            unittemplateTable.getSelectionModel().clearSelection();
        });
    }
}
