/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.WorkHierarchyTreeCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author ta.ito
 */
@FxComponent(id = "WorkHierarchyTreeCompo", fxmlPath = "/fxml/compo/work_hierarchy_tree_compo.fxml")
public class WorkHierarchyTreeCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();
    private TreeDialogEntity treeDialogEntity = null;

    private final static long ROOT_ID = 0;

    @FXML
    private TreeView<WorkHierarchyInfoEntity> hierarchyTree;
    @FXML
    private Label itemElementName;
    @FXML
    private TextField selectedItemName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof TreeDialogEntity) {
            treeDialogEntity = (TreeDialogEntity) argument;

            hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<WorkHierarchyInfoEntity>> observable, TreeItem<WorkHierarchyInfoEntity> oldValue, TreeItem<WorkHierarchyInfoEntity> newValue) -> {
                boolean isSet = false;
                if (treeDialogEntity.getIsUseHierarchy()) {
                    // 階層の場合は、自分と同じ名前の階層には移動不可
                    if (Objects.nonNull(newValue) && !newValue.equals(treeDialogEntity.getSourceItem())) {
                        isSet = true;
                    }
                } else {
                    // データの場合は、ルートには移動不可
                    if (Objects.nonNull(newValue) && newValue.getValue().getWorkHierarchyId() != ROOT_ID) {
                        isSet = true;
                    }
                }

                if (isSet) {
                    selectedItemName.setText(newValue.getValue().getHierarchyName());
                    treeDialogEntity.setTreeSelectedItem(newValue);
                } else {
                    Platform.runLater(() -> {
                        selectedItemName.setText("");
                        treeDialogEntity.setTreeSelectedItem(null);
                    });
                }
            });

            Platform.runLater(() -> {
                itemElementName.setText(treeDialogEntity.getElementName());
                hierarchyTree.rootProperty().setValue((TreeItem<WorkHierarchyInfoEntity>) treeDialogEntity.getTreeRootItem());
                reRenderTree();
            });
        }
    }

    /**
     * ツリーの再描画
     */
    private void reRenderTree() {
        hierarchyTree.setCellFactory((TreeView<WorkHierarchyInfoEntity> p) -> new WorkHierarchyTreeCell());
    }
}
