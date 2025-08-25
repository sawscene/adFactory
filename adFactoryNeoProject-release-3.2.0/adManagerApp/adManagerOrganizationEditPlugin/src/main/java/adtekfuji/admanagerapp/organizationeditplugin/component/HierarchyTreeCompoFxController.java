/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.organizationeditplugin.component;

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
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.treecell.OrganizationTreeCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author ta.ito
 */
@FxComponent(id = "OrganizationHierarchyTreeCompo", fxmlPath = "/fxml/compo/organization_hierarchy_tree_compo.fxml")
public class HierarchyTreeCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();
    private TreeDialogEntity treeDialogEntity = null;

    private final static long ROOT_ID = 0;

    @FXML
    private TreeView<OrganizationInfoEntity> hierarchyTree;
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

            hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<OrganizationInfoEntity>> observable, TreeItem<OrganizationInfoEntity> oldValue, TreeItem<OrganizationInfoEntity> newValue) -> {
                if (Objects.nonNull(newValue)) {
                    selectedItemName.setText(newValue.getValue().getOrganizationName());
                    treeDialogEntity.setTreeSelectedItem(newValue);
                } else {
                    Platform.runLater(() -> {
                        selectedItemName.setText("");
                    });
                }
            });

            Platform.runLater(() -> {
                itemElementName.setText(treeDialogEntity.getElementName());
                hierarchyTree.rootProperty().setValue((TreeItem<OrganizationInfoEntity>) treeDialogEntity.getTreeRootItem());
                reRenderTree();
            });
        }
    }

    /**
     * ツリーの再描画
     */
    private void reRenderTree() {
        hierarchyTree.setCellFactory((TreeView<OrganizationInfoEntity> p) -> new OrganizationTreeCell());
    }
}
