/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.component;

import adtekfuji.clientservice.WorkInfoFacade;
import adtekfuji.clientservice.WorkflowHierarchyInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ledger.LedgerTargetEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * 対象工程選択ダイアログ
 *
 * @author yu.nara
 */
@FxComponent(id = "WorkSelectionCompo", fxmlPath = "/fxml/compo/work_selection_compo.fxml")
public class WorkSelectionCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private static final WorkflowHierarchyInfoFacade workflowHierarchyInfoFacade = new WorkflowHierarchyInfoFacade();
    private static final WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private static final WorkInfoFacade workInfoFacade = new WorkInfoFacade();
    private final static long ROOT_ID = 0;

    @FXML
    private TreeView<ItemEntity> hierarchyTree;
    @FXML
    private ListView<ItemEntity> itemList;
    @FXML
    private Pane progressPane;
    @FXML
    private StackPane stackPane;


    private List<LedgerTargetEntity> ledgerTargetEntities;
    /**
     * TreeView表示用セル
     *
     */
    static class TreeItemCell extends TreeCell<ItemEntity> {

        @Override
        protected void updateItem(ItemEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                this.setText(item.getTreeName());
            } else {
                this.setText("");
            }
        }
    }

    /**
     * ListView表示用セル
     *
     */
    static class ListItemCell extends ListCell<ItemEntity> {
        @Override
        protected void updateItem(ItemEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                this.setText(item.getListName());
            } else {
                this.setText("");
            }
        }
    }

    /**
     * 管理用 IF
     */
    interface ItemEntity {
        /**
         * ツリー表示名取得
         * @return ツリー表示名
         */
        String getTreeName();

        /**
         * リスト表示名取得
         * @return リスト表示名
         */
        String getListName();

        /**
         * 子階層要素生成
         * @return 子階層要素
         */
        List<ItemEntity> createChildren();

        /**
         * 同じ要素化を確認
         * @param item 比較対象
         * @return ture:同じ / false: 異なる
         */
        boolean isSame(ItemEntity item);

        /**
         * 帳票管理情報取得
         * @return 帳票管理情報
         */
        LedgerTargetEntity createLedgerTargetEntity();
    }

    /**
     * 工程順階層用クラス
     */
    static class WorkflowHierarchyItemEntity implements ItemEntity {

        private final Logger logger = LogManager.getLogger();
        private final SceneContiner sc = SceneContiner.getInstance();

        private static final String additionalName = "(" + LocaleUtils.getString("key.OrderProcessesHierarch") +")";

        private final WorkflowHierarchyInfoEntity workflowHierarchyInfoEntity;

        WorkflowHierarchyItemEntity(WorkflowHierarchyInfoEntity workflowHierarchyInfoEntity) {
            super();
            this.workflowHierarchyInfoEntity = workflowHierarchyInfoEntity;
        }

        /**
         * ツリー表示名取得
         * @return ツリー表示名
         */
        @Override
        public String getTreeName() {
            return workflowHierarchyInfoEntity.getHierarchyName();
        }

        /**
         * リスト表示名取得
         * @return リスト表示名
         */
        @Override
        public String getListName() { return getTreeName() + additionalName; }

        /**
         * 同じ要素化を確認
         * @param item 比較対象
         * @return ture:同じ / false: 異なる
         */
        public boolean isSame(ItemEntity item) {
            if (!(item instanceof WorkflowHierarchyItemEntity)) {
                return false;
            }

            WorkflowHierarchyItemEntity _item = (WorkflowHierarchyItemEntity) item;
            return Objects.equals(_item.workflowHierarchyInfoEntity, this.workflowHierarchyInfoEntity);
        }

        /**
         * 子階層要素生成
         * @return 子階層要素
         */
        @Override
        public List<ItemEntity> createChildren() {
            try {
                List<WorkflowHierarchyInfoEntity> workflowHierarchyInfoEntities
                        = workflowHierarchyInfoFacade.getAffilationHierarchyRange(workflowHierarchyInfoEntity.getWorkflowHierarchyId(), null, null, true);

                List<ItemEntity> workflowHierarchy = workflowHierarchyInfoEntities.stream().map(WorkflowHierarchyItemEntity::new).collect(toList());
                List<ItemEntity> workflow
                        = Objects.nonNull(workflowHierarchyInfoEntity.getWorkflowInfoCollection())
                        ? workflowHierarchyInfoEntity.getWorkflowInfoCollection().stream().map(WorkflowItemEntity::new).collect(toList())
                        : new ArrayList<>();

                return Stream.concat(workflowHierarchy.stream(), workflow.stream()).collect(toList());
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.TargetWork") + LocaleUtils.getString("key.Choice"),
                        String.format(LocaleUtils.getString("key.FailedToGet"), LocaleUtils.getString("key.TargetWork")));
                return new ArrayList<>();
            }
        }

        /**
         * 帳票管理情報取得
         * @return 帳票管理情報
         */
        @Override
        public LedgerTargetEntity createLedgerTargetEntity() {
            return new LedgerTargetEntity(getListName(), this.workflowHierarchyInfoEntity.getWorkflowHierarchyId(), null, null);
        }
    }

    /**
     * 工程順用クラス
     */
    static class WorkflowItemEntity implements ItemEntity {

        private static final String additionalName = "(" + LocaleUtils.getString("key.OrderProcesses") +")";
        private final WorkflowInfoEntity workflowInfoEntity;
        WorkflowItemEntity(WorkflowInfoEntity workflowInfoEntity) {
            super();
            this.workflowInfoEntity = workflowInfoEntity;
        }

        /**
         * ツリー表示名取得
         * @return ツリー表示名
         */
        @Override
        public String getTreeName() {
            return String.format("%s : %d",workflowInfoEntity.getWorkflowName(), workflowInfoEntity.getWorkflowRev());
        }

        /**
         * リスト表示名取得
         * @return リスト表示名
         */
        @Override
        public String getListName() { return getTreeName() + additionalName; }

        /**
         * 同じ要素化を確認
         * @param item 比較対象
         * @return ture:同じ / false: 異なる
         */
        public boolean isSame(ItemEntity item) {
            if (!(item instanceof WorkflowItemEntity)) {
                return false;
            }
            WorkflowItemEntity _item = (WorkflowItemEntity) item;
            return Objects.equals(_item.workflowInfoEntity, this.workflowInfoEntity);
        }

        /**
         * 子階層要素生成
         * @return 子階層要素
         */
        @Override
        public List<ItemEntity> createChildren() {
            List<WorkInfoEntity> work = workInfoFacade.getWorkRangeByWorkflow(workflowInfoEntity.getWorkflowId(), null, null);
            return work.stream().map(item -> new WorkItemEntity(workflowInfoEntity, item)).collect(toList());
        }

        /**
         * 帳票管理情報取得
         * @return 帳票管理情報
         */
        @Override
        public LedgerTargetEntity createLedgerTargetEntity() {
            return new LedgerTargetEntity(getListName(), null, this.workflowInfoEntity.getWorkflowId(), null);
        }
    }

    /**
     * 工程用クラス
     */
    static class WorkItemEntity implements ItemEntity {
        private final WorkInfoEntity workInfoEntity;
        private final WorkflowInfoEntity workflowInfoEntity;
        private static final String additionalName = "(" + LocaleUtils.getString("key.Process") +")";


        WorkItemEntity(WorkflowInfoEntity workflowItemEntity, WorkInfoEntity workInfoEntity) {
            super();
            this.workflowInfoEntity = workflowItemEntity;
            this.workInfoEntity = workInfoEntity;
        }

        /**
         * ツリー表示名取得
         * @return ツリー表示名
         */
        @Override
        public String getTreeName() {
            return String.format("%s : %d", workInfoEntity.getWorkName(), workInfoEntity.getWorkRev());
        }

        /**
         * リスト表示名取得
         * @return リスト表示名
         */
        @Override
        public String getListName() { return getTreeName() + additionalName; }

        /**
         * 子階層要素生成
         * @return 子階層要素
         */
        @Override
        public List<ItemEntity> createChildren() {
            return new ArrayList<>();
        }

        /**
         * 同じ要素化を確認
         * @param item 比較対象
         * @return ture:同じ / false: 異なる
         */
        public boolean isSame(ItemEntity item) {
            if (!(item instanceof WorkItemEntity)) {
                return false;
            }
            WorkItemEntity _item = (WorkItemEntity) item;
            return Objects.equals(_item.workInfoEntity, this.workInfoEntity);
        }

        /**
         * 帳票管理情報取得
         * @return 帳票管理情報
         */
        @Override
        public LedgerTargetEntity createLedgerTargetEntity() {
            return new LedgerTargetEntity(getListName(), null, this.workflowInfoEntity.getWorkflowId(), this.workInfoEntity.getWorkId());
        }
    }

    // ツリーのルート要素
    private TreeItem<ItemEntity> rootItem;


    /**
     * 工程選択用ダイアログを初期化する。
     *
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.hierarchyTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        createRoot();

        // 選択済リスト
        Callback<ListView<ItemEntity>, ListCell<ItemEntity>> cellFactory = (ListView<ItemEntity> param) -> new ListItemCell();
        this.itemList.setCellFactory(cellFactory);
        this.itemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * 表示用エンティティ作成
     * @param entity 帳票情報
     * @return 表示用エンティティ
     */
    ItemEntity createItemEntity(LedgerTargetEntity entity) {
        try {
            // 工程順階層情報がある物は必ず工程順階層
            if (Objects.nonNull(entity.getWorkflowHierarchyId())) {
                WorkflowHierarchyInfoEntity workflowHierarchyEntity = workflowHierarchyInfoFacade.find(entity.getWorkflowHierarchyId());
                return Objects.isNull(workflowHierarchyEntity) ? null : new WorkflowHierarchyItemEntity(workflowHierarchyEntity);
            }

            // 工程順階層が無く工程IDが無い物はない
            if (Objects.isNull(entity.getWorkflowId())) {
                return null;
            }

            // 工程順を取得
            WorkflowInfoEntity workflowEntity = workflowInfoFacade.find(entity.getWorkflowId());
            if (Objects.isNull(workflowEntity)) {
                return null;
            }

            // 工程が無い場合は必ず工程順のみ
            if (Objects.isNull(entity.getWorkId())) {
                return new WorkflowItemEntity(workflowEntity);
            }

            // 工程を取得
            WorkInfoEntity workEntity = workInfoFacade.find(entity.getWorkId());
            if (Objects.isNull(workEntity)) {
                return null;
            }

            // 工程用エンティティを返す
            return new WorkItemEntity(workflowEntity, workEntity);
        }
        catch (Exception ex) {
            logger.fatal(ex, ex);
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.TargetWork") + LocaleUtils.getString("key.Choice"),
                    String.format(LocaleUtils.getString("key.FailedToGet"), LocaleUtils.getString("key.TargetWork")));
            return null;
        }
    }

    /**
     * 引数設定
     * @param argument  引数
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof List) {
            this.ledgerTargetEntities = (List<LedgerTargetEntity>) argument;

            List<ItemEntity> itemEntities
                    = this.ledgerTargetEntities
                    .stream()
                    .map(this::createItemEntity)
                    .filter(Objects::nonNull)
                    .collect(toList());
            this.itemList.setItems(FXCollections.observableArrayList(itemEntities));
        }
    }

    /**
     * 右のリストに追加
     */
    @FXML
    private void OnAdd() {
        List<TreeItem<ItemEntity>> selectedItems = this.hierarchyTree.getSelectionModel().getSelectedItems();
        if (Objects.isNull(selectedItems) ) {
            return;
        }

        selectedItems
                .stream()
                .filter(item -> !Objects.equals(item, this.rootItem))
                .map(TreeItem::getValue)
                .filter(item -> this.itemList.getItems().stream().noneMatch(item::isSame))
                .forEach(item -> this.itemList.getItems().add(item));

        // 右のリストを保存
        this.ledgerTargetEntities.clear();
        this.ledgerTargetEntities.addAll(this.itemList.getItems().stream().map(ItemEntity::createLedgerTargetEntity).collect(toList()));
    }

    /**
     * 右のリストから削除
     */
    @FXML
    private void OnRemove() {
        List<ItemEntity> selectedItems = this.itemList.getSelectionModel().getSelectedItems();
        if (Objects.isNull(this.itemList.getItems())) {
            return;
        }
        new ArrayList<>(selectedItems).forEach(item -> this.itemList.getItems().remove(item));
        this.ledgerTargetEntities.clear();
        this.ledgerTargetEntities.addAll(this.itemList.getItems().stream().map(ItemEntity::createLedgerTargetEntity).collect(toList()));
    }

    /**
     * ツリーを開いたときのイベント処理
     */
    private final ChangeListener<Boolean> changeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue observable, Boolean oldValue, Boolean newValue) {
            if (Objects.isNull(newValue) || newValue.equals(false)) {
                return;
            }

            TreeItem<ItemEntity> treeItem = (TreeItem<ItemEntity>) ((BooleanProperty) observable).getBean();
            blockUI(true);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        treeItem.getChildren()
                                .forEach(item -> {
                                    // 子要素を作ってリスナーの登録
                                    List<TreeItem<ItemEntity>> newChildren
                                            = item
                                            .getValue()
                                            .createChildren()
                                            .stream()
                                            .map(TreeItem::new)
                                            .peek(treeItem -> treeItem.expandedProperty().addListener(changeListener))
                                            .collect(toList());
                                    item.getChildren().addAll(newChildren);
                                });

                        // 開いたら情報取得用のリスナーは削除
                        treeItem.expandedProperty().removeListener(changeListener);
                    } finally {
                        Platform.runLater(() -> blockUI(false));
                    }
                    return null;
                }
            };
            new Thread(task).start();
        }
    };

    /**
     * ツリーの親階層生成
     */
    private void createRoot() {
        // ルートが存在しない場合は新規作成する。
        if (Objects.isNull(this.rootItem)) {
            this.rootItem = new TreeItem<>();
        }

        try {
            blockUI(true);
            this.rootItem.getChildren().clear();
            ItemEntity itemEntity = new WorkflowHierarchyItemEntity(new WorkflowHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.OrderProcessesHierarch")));
            this.rootItem.setValue(itemEntity);

            final String liteTreeName = workflowHierarchyInfoFacade.getLiteTreeName();
            List<TreeItem<ItemEntity>> treeItems
                    = this.rootItem
                    .getValue()
                    .createChildren()
                    .stream()
                    .filter(item -> !StringUtils.equals(liteTreeName, item.getTreeName()))
                    .map(TreeItem::new)
                    .peek(item -> item.expandedProperty().addListener(changeListener))
                    .collect(toList());
            this.rootItem.getChildren().addAll(treeItems);
            this.rootItem.expandedProperty().addListener(changeListener);

            Platform.runLater(() -> {
                this.hierarchyTree.rootProperty().setValue(this.rootItem);
                this.hierarchyTree.setCellFactory((TreeView<ItemEntity> o) -> new TreeItemCell());
                this.hierarchyTree.getSelectionModel().select(rootItem);
                this.rootItem.setExpanded(true);
            });

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.TargetWork") + LocaleUtils.getString("key.Choice"),
                    String.format(LocaleUtils.getString("key.FailedToGet"), LocaleUtils.getString("key.TargetWork")));
        } finally {
            blockUI(false);
        }
    }

    /**
     * UIロック
     *
     * @param flg フラグ
     */
    private void blockUI(Boolean flg) {
        this.stackPane.setDisable(flg);
        this.progressPane.setVisible(flg);
    }
}
