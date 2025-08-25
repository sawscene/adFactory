/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.component;

import adtekfuji.admanagerapp.kanbaneditpluginels.common.WorkHierarchyTreeCell;
import adtekfuji.clientservice.WorkHierarchyInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程選択クラス
 *
 * @author e-mori
 */
@FxComponent(id = "WorkSelectionCompoELS", fxmlPath = "/fxml/admanakanbaneditpluginels/work_selection_compo.fxml")
public class WorkSelectionCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final static long ROOT_ID = 0;
    private final static long RANGE = 20;

    private TreeItem<WorkHierarchyInfoEntity> rootItem = new TreeItem<>(new WorkHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.ProcessHierarch")));
    private final static WorkHierarchyInfoFacade workHierarchyInfoFacade = new WorkHierarchyInfoFacade();

    private WorkInfoEntity workInfoEntity = null;

    private final ChangeListener<Boolean> changeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
                blockUI(true);
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            expand(treeItem);
                        } finally {
                            Platform.runLater(() -> blockUI(false));
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        }
    };

    @FXML
    private TreeView<WorkHierarchyInfoEntity> hierarchyTree;
    @FXML
    private Label itemElementName;
    @FXML
    private TextField selectedItemName;
    @FXML
    private StackPane stackPane;
    @FXML
    private ListView<WorkInfoEntity> treeItemList;
    @FXML
    private Pane progressPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootItem = new TreeItem<>(new WorkHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.ProcessHierarch")));
        itemElementName.setText(LocaleUtils.getString("key.ProcessName"));
        Callback<ListView<WorkInfoEntity>, ListCell<WorkInfoEntity>> cellFactory = (ListView<WorkInfoEntity> param) -> new ListItemCell();
        treeItemList.setCellFactory(cellFactory);

        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    createRoot();
                } finally {
                    Platform.runLater(() -> blockUI(false));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof WorkInfoEntity) {
            workInfoEntity = (WorkInfoEntity) argument;

            hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<WorkHierarchyInfoEntity>> observable, TreeItem<WorkHierarchyInfoEntity> oldValue, TreeItem<WorkHierarchyInfoEntity> newValue) -> {
                if (Objects.nonNull(newValue) && newValue.getValue().getWorkHierarchyId() != ROOT_ID) {
                    updateListView(newValue.getValue().getWorkInfoCollection());
                } else {
                    clearWorkList();
                    selectedItemName.setText("");
                }
            });

            treeItemList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends WorkInfoEntity> observable, WorkInfoEntity oldValue, WorkInfoEntity newValue) -> {
                if (Objects.nonNull(newValue)) {
                    selectedItemName.setText(newValue.getWorkName());
                    workInfoEntity.setWorkId(newValue.getWorkId());
                    workInfoEntity.setWorkName(newValue.getWorkName());
                    workInfoEntity.setTaktTime(newValue.getTaktTime());
                } else {
                    workInfoEntity.setWorkId(null);
                }
            });
        }
    }

    /**
     * ListView表示用セル
     *
     */
    class ListItemCell extends ListCell<WorkInfoEntity> {

        @Override
        protected void updateItem(WorkInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setText(item.getWorkName());
            } else {
                setText("");
            }
        }
    }

    /**
     * リスト更新
     *
     * @param entitys
     */
    private void updateListView(List<WorkInfoEntity> entitys) {
        treeItemList.getItems().clear();
        entitys.sort(Comparator.comparing(work -> work.getWorkName()));
        treeItemList.setItems(FXCollections.observableArrayList(entitys));
    }

    /**
     * リストの初期化
     *
     */
    private void clearWorkList() {
        treeItemList.getItems().clear();
        treeItemList.getSelectionModel().clearSelection();
    }

   /**
     * ツリーの親階層生成
     *
     */
    public void createRoot() {
        try {
            logger.debug("createRoot start.");

            long count = workHierarchyInfoFacade.getTopHierarchyCount();
            this.rootItem.getChildren().clear();
            this.rootItem.getValue().setChildCount(count);

            for (long from = 0; from < count; from += RANGE) {
                List<WorkHierarchyInfoEntity> entities = workHierarchyInfoFacade.getTopHierarchyRange(from, from + RANGE - 1, true);

                entities.stream().forEach((entity) -> {
                    TreeItem<WorkHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    this.rootItem.getChildren().add(item);
                });
            }

            Platform.runLater(() -> {
                this.hierarchyTree.rootProperty().setValue(rootItem);
                this.hierarchyTree.setCellFactory((TreeView<WorkHierarchyInfoEntity> o) -> new WorkHierarchyTreeCell());
            });

            this.rootItem.setExpanded(true);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.debug("createRoot end.");
        }
    }

    /**
     * ツリー展開
     *
     * @param parentItem 親階層
     */
    private synchronized void expand(TreeItem<WorkHierarchyInfoEntity> parentItem) {
        try {
            logger.debug("expand start: {}", parentItem.getValue());

            parentItem.getChildren().clear();

            long count = parentItem.getValue().getChildCount();

            for (long from = 0; from <= count; from += RANGE) {
                List<WorkHierarchyInfoEntity> entities = workHierarchyInfoFacade.getAffilationHierarchyRange(parentItem.getValue().getWorkHierarchyId(), from, from + RANGE - 1, true);

                entities.stream().forEach((entity) -> {
                    TreeItem<WorkHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    parentItem.getChildren().add(item);
                });
            }

            Platform.runLater(() -> this.hierarchyTree.setCellFactory((TreeView<WorkHierarchyInfoEntity> o) -> new WorkHierarchyTreeCell()));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.debug("expand end.");
        }
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        stackPane.setDisable(flg);
        progressPane.setVisible(flg);
    }

}
