/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.selectcompo;

import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.controls.EditableListCell;
import jp.adtekfuji.javafxcommon.treecell.KanbanHierarchyTreeCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;

/**
 * カンバン選択ダイアログ<br>
 * <br>
 * 呼び出し元<br>
 * アジェンダモニターの設定ダイアログ
 *
 * @author e-mori
 */
@FxComponent(id = "KanbanSelectionCompo", fxmlPath = "/fxml/compo/kanban_selection_compo.fxml")
public class KanbanSelectionCompoFxController implements Initializable, ArgumentDelivery {

    public class DisplayData {

        private final Long id;
        private final Long workflowId;
        private String kanbanName = "";
        private String workflowName = "";
        private String status = "";
        private String startDate = "";
        private String endDate = "";
        private final KanbanInfoEntity entity;

        public DisplayData(KanbanInfoEntity entity) {
            this.id = entity.getKanbanId();
            this.workflowId = entity.getFkWorkflowId();
            this.kanbanName = entity.getKanbanName();
            if (Objects.nonNull(entity.getWorkflowName())) {
                this.workflowName = entity.getWorkflowName();
            }
            this.status = LocaleUtils.getString(entity.getKanbanStatus().getResourceKey());
            if (Objects.nonNull(entity.getStartDatetime())) {
                this.startDate = formatter.format(entity.getStartDatetime());
            }
            if (Objects.nonNull(entity.getCompDatetime())) {
                this.endDate = formatter.format(entity.getCompDatetime());
            }
            this.entity = entity;
        }

        public Long getId() {
            return id;
        }

        public Long getWorkflowId() {
            return workflowId;
        }

        public String getKanbanName() {
            return kanbanName;
        }

        public String getWorkflowName() {
            return workflowName;
        }

        public String getStatus() {
            return status;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public KanbanInfoEntity getEntity() {
            return entity;
        }
    }
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private SimpleDateFormat formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
    private KanbanHierarchyInfoFacade kanbanHierarchyInfoFacade;
    private KanbanInfoFacade kanbanInfoFacade;

    private SelectDialogEntity settingDialogEntity;
    private final static long ROOT_ID = 0;
    private final static long SEARCH_MAX = 100;
    private final static long MAX_LOAD_SIZE = 20;

    private Boolean countOverFlag = false;
    
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
    private HBox conditionPane;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private CheckListView<KanbanStatusEnum> statusList;

    @FXML
    private StackPane stackPane;
    @FXML
    private ListView<KanbanInfoEntity> itemList;
    @FXML
    private TreeView<KanbanHierarchyInfoEntity> hierarchyTree;
    @FXML
    private TableView<DisplayData> kanbanList;
    @FXML
    private TableColumn kanbanNameColumn;
    @FXML
    private TableColumn workflowNameColumn;
    @FXML
    private TableColumn statusColumn;
    @FXML
    private TableColumn startTimeColumn;
    @FXML
    private TableColumn endTimeColumn;
    @FXML
    private Pane Progress;

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
        
        this.itemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      
        //エンティティメンバーとバインド
        kanbanNameColumn.setCellValueFactory(new PropertyValueFactory<>("kanbanName"));
        workflowNameColumn.setCellValueFactory(new PropertyValueFactory<>("workflowName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        kanbanList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 検索条件のステータス項目を設定する。
        statusList.setItems(FXCollections.observableArrayList(KanbanStatusEnum.values()));
        statusList.setCellFactory(litView -> new CheckBoxListCell<KanbanStatusEnum>(statusList::getItemBooleanProperty) {
            @Override
            public void updateItem(KanbanStatusEnum item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(LocaleUtils.getString(item.getResourceKey()));
                }
            }
        });
        statusList.getCheckModel().checkAll();

        // ツリーの階層が選択されたときに指定した条件に一致しているものを表示する
        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<KanbanHierarchyInfoEntity>> observable, TreeItem<KanbanHierarchyInfoEntity> oldValue, TreeItem<KanbanHierarchyInfoEntity> newValue) -> {
            if (Objects.nonNull(newValue) && newValue.getValue().getKanbanHierarchyId() != ROOT_ID) {
                draw(newValue.getValue());
            } else {
                Platform.runLater(() -> this.clearKanbanList());
            }
        });
    }

    /**
     * ListView表示用セル
     *
     */
    class ListItemCell extends EditableListCell<KanbanInfoEntity> {

        private final Label label = new Label();

        public ListItemCell() {
            super(true, true);
            this.label.setMinWidth(20.0);
            this.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                this.label.setPrefWidth(newValue.doubleValue() - 90.0);
            });
            addItem(this.label);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }

        @Override
        protected void updateItem(KanbanInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                this.label.setText(item.getKanbanName());
            }
        }
    }

    /**
     * 選択したツリーのカンバンを表示する
     *
     * @param entity
     */
    private void draw(KanbanHierarchyInfoEntity entity) {
        logger.info("draw start; id={}", entity.getKanbanHierarchyId());

        try {
            blockUI(true);

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        if (settingDialogEntity.isUseInternalCondition()) {
                            searchKanbanData(entity);
                        } else {
                            searchKanbanData(entity, settingDialogEntity.getCondition());
                        }
                    } finally {
                        Platform.runLater(() -> blockUI(false));
                    }
                    return null;
                }
            };
            new Thread(task).start();
        } catch (Exception ex) {
            blockUI(false);
            logger.fatal(ex, ex);
        }
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectDialogEntity) {
            this.settingDialogEntity = (SelectDialogEntity) argument;

            if (Objects.nonNull(this.settingDialogEntity.getUribase())) {
                this.kanbanHierarchyInfoFacade = new KanbanHierarchyInfoFacade(this.settingDialogEntity.getUribase());
                this.kanbanInfoFacade = new KanbanInfoFacade(this.settingDialogEntity.getUribase());
            } else {
                this.kanbanHierarchyInfoFacade = new KanbanHierarchyInfoFacade();
                this.kanbanInfoFacade = new KanbanInfoFacade();
            }

            // 内部条件設定が有効の場合のみステータスと日付を表示
            this.conditionPane.setVisible(this.settingDialogEntity.isUseInternalCondition());
            this.conditionPane.setManaged(this.settingDialogEntity.isUseInternalCondition());

            this.itemList.getItems().addAll(this.settingDialogEntity.getKanbans());
            this.settingDialogEntity.kanbansProperty().bind(this.itemList.itemsProperty());

            Callback<ListView<KanbanInfoEntity>, ListCell<KanbanInfoEntity>> cellFactory = (ListView<KanbanInfoEntity> param) -> new ListItemCell();
            this.itemList.setCellFactory(cellFactory);

            this.blockUI(true);

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        createRoot();
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        Platform.runLater(() -> {
                            if (countOverFlag) {
                                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.overRangeSearchTitle"), String.format(LocaleUtils.getString("key.overRangeSearchMessage"), SEARCH_MAX));
                            }
                            blockUI(false);
                        });
                    }
                    return null;
                }
            };
            new Thread(task).start();
        }
    }

    @FXML
    private void OnAdd(ActionEvent event) {
        //追加処理
        if (Objects.nonNull(this.kanbanList.getSelectionModel().getSelectedItem())) {
            for (DisplayData item : this.kanbanList.getSelectionModel().getSelectedItems()) {
                KanbanInfoEntity entity = new KanbanInfoEntity(item.getId(), null, item.getKanbanName(), null);
                entity.setWorkflowName(item.getWorkflowName());
                if (!itemList.getItems().contains(entity)) {
                    this.itemList.getItems().add(entity);
                }
            }
        }
    }

    @FXML
    private void OnRemove(ActionEvent event) {
        //削除処理
        if (Objects.nonNull(this.itemList.getSelectionModel().getSelectedIndices())) {
            this.itemList.getSelectionModel()
                         .getSelectedIndices()
                         .stream()
                         .sorted(Comparator.reverseOrder())
                         .mapToInt(Integer::intValue)
                         .forEach(index -> this.itemList.getItems().remove(index));
        }
    }

    @FXML
    private void onUpdate(ActionEvent event) {
        if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())) {
            return;
        }

        draw(hierarchyTree.getSelectionModel().getSelectedItem().getValue());
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        this.stackPane.setDisable(flg);
        this.Progress.setVisible(flg);
    }

    /**
     * ツリーの親階層生成
     *
     */
    private synchronized void createRoot() {
        try {
            logger.debug("createRoot start.");

            TreeItem<KanbanHierarchyInfoEntity> rootItem = new TreeItem<>(new KanbanHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.Kanban")));

            // Lite工程階層を取得.
            KanbanHierarchyInfoEntity kanbankHierarchy = new KanbanHierarchyInfoEntity(0L, "");
            if (Objects.nonNull(settingDialogEntity.getLiteHierarchyOnly()) && settingDialogEntity.getLiteHierarchyOnly()) {
                Properties properties = AdProperty.getProperties();
                String liteTreeName = properties.getProperty(Config.LITE_HIERARCHY_TOP_KEY);
                kanbankHierarchy = this.kanbanHierarchyInfoFacade.findHierarchyName(liteTreeName);
                if (Objects.isNull(kanbankHierarchy.getKanbanHierarchyId())) {
                    throw new NullPointerException();
                }
                rootItem.getValue().setKanbanHierarchyId(kanbankHierarchy.getKanbanHierarchyId());
            }

            //親階層の情報を取得
            long count = 0;
            if (Objects.isNull(settingDialogEntity.getLiteHierarchyOnly()) || !settingDialogEntity.getLiteHierarchyOnly()) {
                count = kanbanHierarchyInfoFacade.getTopHierarchyCount();
            } else {
                count = kanbanHierarchyInfoFacade.getAffilationHierarchyCount(kanbankHierarchy.getKanbanHierarchyId());
            }
            rootItem.getValue().setChildCount(count);

            for (long from = 0; from < count; from += MAX_LOAD_SIZE) {
                List<KanbanHierarchyInfoEntity> entities;
                if (Objects.isNull(settingDialogEntity.getLiteHierarchyOnly())) {
                    entities = kanbanHierarchyInfoFacade.getTopHierarchyRange(from, from + MAX_LOAD_SIZE - 1, true);
                } else if (!settingDialogEntity.getLiteHierarchyOnly()) {
                    entities = kanbanHierarchyInfoFacade.getTopHierarchyRange(from, from + MAX_LOAD_SIZE - 1, false);
                } else {
                    entities = kanbanHierarchyInfoFacade.getAffilationHierarchyRange(kanbankHierarchy.getKanbanHierarchyId(), from, from + MAX_LOAD_SIZE - 1);
                }

                entities.stream().forEach((entity) -> {
                    TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    rootItem.getChildren().add(item);
                });
            } 
            // 名前でソートする
            rootItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getHierarchyName()));

            Platform.runLater(() -> {
                this.hierarchyTree.rootProperty().setValue(rootItem);
                this.hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new KanbanHierarchyTreeCell());
                this.hierarchyTree.getSelectionModel().select(rootItem);
            });

            rootItem.setExpanded(true);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.debug("createRoot end.");
        }
    }

    /**
     * ツリーの子階層生成
     *
     * @param parentItem 親階層
     */
    private synchronized void expand(TreeItem<KanbanHierarchyInfoEntity> parentItem) {
        try {
            parentItem.getChildren().clear();
            long count = parentItem.getValue().getChildCount();

            for (long from = 0; from < count; from += MAX_LOAD_SIZE) {
                List<KanbanHierarchyInfoEntity> entities = kanbanHierarchyInfoFacade.getAffilationHierarchyRange(parentItem.getValue().getKanbanHierarchyId(), from, from + MAX_LOAD_SIZE - 1);

                entities.stream().forEach((entity) -> {
                    TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    parentItem.getChildren().add(item);
                });
            }
            // 名前でソートする
            parentItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getHierarchyName()));

            Platform.runLater(() -> this.hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new KanbanHierarchyTreeCell()));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    private void clearKanbanList() {
        kanbanList.getItems().clear();
        kanbanList.getSelectionModel().clearSelection();
    }

    /**
     * カンバン検索を行う。条件はダイアログで設定したステータスと日付を使う
     */
    private void searchKanbanData(KanbanHierarchyInfoEntity entity) {

        Date fromDate = Objects.isNull(fromDatePicker.getValue()) ? null : DateUtils.getBeginningOfDate(fromDatePicker.getValue());
        Date toDate = Objects.isNull(toDatePicker.getValue()) ? null : DateUtils.getEndOfDate(toDatePicker.getValue());

        KanbanSearchCondition condition = new KanbanSearchCondition();

        condition.setKanbanStatusCollection(statusList.getCheckModel().getCheckedItems());
        condition.setFromDate(fromDate);
        condition.setToDate(toDate);

        searchKanbanData(entity, condition);
    }

    /**
     * 保持している条件で絞り込みを行う
     */
    private void searchKanbanData(KanbanHierarchyInfoEntity entity, KanbanSearchCondition condition) {
        try {
            KanbanSearchCondition sub = condition.hierarchyId(entity.getKanbanHierarchyId());

            ObservableList<DisplayData> tableData = FXCollections.observableArrayList();
            Long count = kanbanInfoFacade.countSearch(sub);

            logger.debug("search data:{}", count);
            countOverFlag = false;
            if (count > SEARCH_MAX) {
                count = SEARCH_MAX;
                countOverFlag = true;
            }
            for (long from = 0; from <= count; from += MAX_LOAD_SIZE) {
                List<KanbanInfoEntity> kanbans = kanbanInfoFacade.findSearchRange(sub, from, from + MAX_LOAD_SIZE - 1);
                if (!kanbans.isEmpty()) {
                    kanbans.stream().forEach((e) -> {
                        tableData.add(new DisplayData(e));
                    });
                }
            }

            Platform.runLater(() -> {
                clearKanbanList();
                kanbanList.setItems(tableData);
                kanbanList.getSortOrder().add(kanbanNameColumn);
            });

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

}
