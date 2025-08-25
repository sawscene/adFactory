/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.component.tree;

import jp.adtekfuji.forfujiapp.javafx.tree.edior.UnitTemplateAndWorkflowTreeEditor;
import jp.adtekfuji.forfujiapp.javafx.tree.entity.UnitTemplateHierarchyTreeEntity;
import jp.adtekfuji.forfujiapp.javafx.tree.entity.WorkflowHierarchyTreeEntity;
import jp.adtekfuji.forfujiapp.javafx.tree.cell.TreeCellInterface;
import adtekfuji.admanagerapp.unittemplateplugin.component.UnitTemplateDetailCompoInterface;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.common.AdFactoryForFujiClientAppConfig;
import jp.adtekfuji.forfujiapp.common.EntityConstants;
import jp.adtekfuji.forfujiapp.common.UIControlInterface;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレート工程順階層画面
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.31.Mon
 */
public class UnitTemplateAndWorkflowTreeController implements Initializable, UIControlInterface {

    private final Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final UnitTemplateDetailCompoInterface detailCompoInterface;
    private final AdFactoryForFujiClientAppConfig config = new AdFactoryForFujiClientAppConfig();

    @FXML
    private TabPane treeTab;
    @FXML
    private TreeView<TreeCellInterface> unittemplateTree;
    @FXML
    private TreeView<TreeCellInterface> workflowTree;
    @FXML
    private Button addSeriesButton;
    @FXML
    private Button addParallelButton;

    public UnitTemplateAndWorkflowTreeController(UnitTemplateDetailCompoInterface detailCompoInterface) {
        this.detailCompoInterface = detailCompoInterface;
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(UnitTemplateAndWorkflowTreeController.class.getName() + ":initialize start");

        // ユニットテンプレート/工程順階層作成
        WorkflowHierarchyTreeEntity workflowHierarchy = new WorkflowHierarchyTreeEntity(new WorkflowHierarchyInfoEntity(EntityConstants.ROOT_ID, "ルート"));
        UnitTemplateHierarchyTreeEntity unittemplateHierarchy = new UnitTemplateHierarchyTreeEntity(new UnitTemplateHierarchyInfoEntity(EntityConstants.ROOT_ID, "ルート"));
        UnitTemplateAndWorkflowTreeEditor treeEditor
                = new UnitTemplateAndWorkflowTreeEditor(workflowTree, new TreeItem<>(workflowHierarchy, new ImageView(new Image(getClass().getResourceAsStream("/image/folder_top.png")))),
                        unittemplateTree, new TreeItem<>(unittemplateHierarchy, new ImageView(new Image(getClass().getResourceAsStream("/image/folder_top.png")))), this);
        UnitTemplateAndWorkflowTreeDragAndDropEvents.configureUnitTemplateTreeView(unittemplateTree);
        UnitTemplateAndWorkflowTreeDragAndDropEvents.configureWorkflowTreeView(workflowTree);

        logger.info(UnitTemplateAndWorkflowTreeController.class.getName() + ":initialize end");
    }

    /**
     * 現在選択しているアイテムを返す
     *
     * @return 現在選択しているツリーのアイテム
     */
    public Object getSelectTreeItem() {
        if (!treeTab.getSelectionModel().isEmpty()) {
            if (treeTab.getSelectionModel().getSelectedItem().getText().equals(LocaleUtils.getString("key.OrderProcesses")) && !workflowTree.getSelectionModel().isEmpty()) {
                // 工程順の場合の挿入処理
                Optional<TreeItem<TreeCellInterface>> opt = Optional.ofNullable(workflowTree.getSelectionModel().getSelectedItem());
                if (opt.isPresent()) {
                    return opt.get().getValue().getEntity();
                }
            } else if (treeTab.getSelectionModel().getSelectedItem().getText().equals(LocaleUtils.getString("key.unittemplate")) && !unittemplateTree.getSelectionModel().isEmpty()) {
                // ユニットテンプレートの場合の挿入処理
                Optional<TreeItem<TreeCellInterface>> opt = Optional.ofNullable(unittemplateTree.getSelectionModel().getSelectedItem());
                if (opt.isPresent()) {
                    return opt.get().getValue().getEntity();
                }
            }
        }
        return null;
    }

    /**
     * ワークの直列挿入処理
     *
     * @param event
     */
    @FXML
    public void onAddSerialButton(ActionEvent event) {
        logger.info(UnitTemplateAndWorkflowTreeController.class.getName() + ":onAddSerialButton start");
        this.detailCompoInterface.addSerial();
        logger.info(UnitTemplateAndWorkflowTreeController.class.getName() + ":onAddSerialButton end");
    }

    /**
     * ワークの並列挿入処理
     *
     * @param event
     */
    @FXML
    public void onAddParallelButton(ActionEvent event) {
        logger.info(UnitTemplateAndWorkflowTreeController.class.getName() + ":onAddParallelButton start");
        this.detailCompoInterface.addParallel();
        logger.info(UnitTemplateAndWorkflowTreeController.class.getName() + ":onAddParallelButton end");
    }

    /**
     * 画面に使用制限をかける
     *
     * @param isBlock
     */
    @Override
    public void blockUI(boolean isBlock) {
        this.detailCompoInterface.blockUI(isBlock);
    }

    @Override
    public void updateUI() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
