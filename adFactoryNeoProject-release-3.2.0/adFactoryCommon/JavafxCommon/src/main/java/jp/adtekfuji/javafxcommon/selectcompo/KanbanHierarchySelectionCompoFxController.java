/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.selectcompo;

import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import adtekfuji.locale.LocaleUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.javafxcommon.treecell.KanbanHierarchyTreeCell;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン階層選択ダイアログ
 *
 * @author yu.nara
 */
@FxComponent(id = "KanbanHierarchySelectionCompo", fxmlPath = "/fxml/compo/kanbanHierarchy_selection_compo.fxml")
public class KanbanHierarchySelectionCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final static long ROOT_ID = 0;

    private SelectDialogEntity settingDialogEntity;

    /**
     * 承認権限のあるカンバン階層のみ表示
     */
    private boolean approvalAuthorityOnly = false;

    /**
     * 選択されたカンバン階層一覧
     */
    private List<KanbanHierarchyInfoEntity> entities;

    @FXML
    private TreeView<KanbanHierarchyInfoEntity> hierarchyTree;
    @FXML
    private ListView<KanbanHierarchyInfoEntity> itemList;
    @FXML
    private Pane progressPane;
    @FXML
    private StackPane stackPane;

    /**
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.hierarchyTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.itemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 選択済リスト
        Callback<ListView<KanbanHierarchyInfoEntity>, ListCell<KanbanHierarchyInfoEntity>> cellFactory = (ListView<KanbanHierarchyInfoEntity> param) -> new ListItemCell();
        this.itemList.setCellFactory(cellFactory);

        blockUI(true);

        Task task = new Task<TreeItem<KanbanHierarchyInfoEntity>>() {
            @Override
            protected TreeItem<KanbanHierarchyInfoEntity> call() throws Exception {
                CashManager cache = CashManager.getInstance();

                // キャッシュにカンバン階層情報を読み込む。(未キャッシュの場合のみ)
                CacheUtils.createCacheKanbanHierarchy(true);

                // 削除済みのデータを取り除く。
                entities = (List<KanbanHierarchyInfoEntity>) cache.getItemList(KanbanHierarchyInfoEntity.class, new ArrayList<>());
                return createRoot();
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    TreeItem<KanbanHierarchyInfoEntity> rootItem = this.getValue();

                    hierarchyTree.setRoot(rootItem);
                    hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new KanbanHierarchyTreeCell());

                    rootItem.setExpanded(true);

                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
            }

            @Override
            protected void failed() {
                super.failed();
                if (Objects.nonNull(this.getException())) {
                    logger.fatal(this.getException(), this.getException());
                }
                blockUI(false);
            }
        };
        new Thread(task).start();
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectDialogEntity) {
            this.settingDialogEntity = (SelectDialogEntity) argument;
            this.itemList.getItems().addAll(settingDialogEntity.getKanbanHierarchies());
            this.settingDialogEntity.kanbanHierarchiesProperty().bind(itemList.itemsProperty());
            this.approvalAuthorityOnly = settingDialogEntity.isApprovalAuthorityOnly();
        }
    }

    @FXML
    private void OnAdd(ActionEvent event) {
        for (TreeItem<KanbanHierarchyInfoEntity> item : this.hierarchyTree.getSelectionModel().getSelectedItems()) {
            if (Objects.nonNull(item.getParent())
                    && !this.itemList.getItems().contains(item.getValue())) {
                this.itemList.getItems().add(item.getValue());
            }
        }
    }

    @FXML
    private void OnRemove(ActionEvent event) {
        if (Objects.nonNull(this.itemList.getSelectionModel().getSelectedIndices())) {
            this.itemList.getSelectionModel()
                         .getSelectedIndices()
                         .stream()
                         .sorted(Comparator.reverseOrder())
                         .mapToInt(Integer::intValue)
                         .forEach(index -> this.itemList.getItems().remove(index));
        }
    }

    /**
     * ListView表示用セル
     */
    static class ListItemCell extends ListCell<KanbanHierarchyInfoEntity> {

        @Override
        protected void updateItem(KanbanHierarchyInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                this.setText(item.getHierarchyName());
            } else {
                this.setText("");
            }
        }
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        this.stackPane.setDisable(flg);
        this.progressPane.setVisible(flg);
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
            CashManager cache = CashManager.getInstance();

            final long MAX_LOAD_SIZE = 20;
            KanbanHierarchyInfoFacade facade = new KanbanHierarchyInfoFacade();
            for (long from = 0; from < count; from += MAX_LOAD_SIZE) {
                List<KanbanHierarchyInfoEntity> entities = facade.getAffilationHierarchyRange(parentItem.getValue().getKanbanHierarchyId(), from, from + MAX_LOAD_SIZE - 1);

                entities.forEach((entity) -> {
                    TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                        item.expandedProperty().addListener(this.changeListener);
                    }

                    cache.setItem(KanbanHierarchyInfoEntity.class, entity.getKanbanHierarchyId(), entity);
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

    private final ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) -> {
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
    };

    /**
     * ツリーの親階層生成
     */
    private TreeItem<KanbanHierarchyInfoEntity> createRoot() {
        try {
            logger.info("createRoot start.");

            KanbanHierarchyInfoEntity rootEntity = new KanbanHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.Kanban"));
            return this.createHierarchy(rootEntity).orElse(null);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("createRoot end.");
        }
        return null;
    }

    /**
     * 階層を追加する。
     *
     * @param childItem  子階層
     * @param parentItem 親階層
     */
    private Optional<TreeItem<KanbanHierarchyInfoEntity>> createHierarchy(KanbanHierarchyInfoEntity parentEntity) {
        try {
            List<KanbanHierarchyInfoEntity> childEntities = this.entities
                    .stream()
                    .filter((entity) -> (Objects.equals(entity.getParentId(), parentEntity.getKanbanHierarchyId())))
                    .sorted(Comparator.comparing(KanbanHierarchyInfoEntity::getHierarchyName))
                    .collect(Collectors.toList());

            TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(parentEntity);
            if (childEntities.isEmpty() && parentEntity.getChildCount() <=0 ) {
                return Optional.of(item);
            }


            if (childEntities.isEmpty()
                    || (Objects.nonNull(parentEntity.getChildCount()) && childEntities.size()!=parentEntity.getChildCount())) {
                item.getChildren().add(new TreeItem<>());
                item.expandedProperty().addListener(this.changeListener);
                return Optional.of(item);
            }

            childEntities
                    .stream()
                    .map(this::createHierarchy)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(entity->item.getChildren().add(entity));
            return Optional.of(item);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return Optional.empty();
    }

    /**
     * 指定されたカンバン階層IDの組織情報を取得する。
     *
     * @param organizationId 組織ID
     * @return 組織情報
     */
    private KanbanHierarchyInfoEntity getKanbanHierarchyById(Long kanbanHierarchyId) {
        return entities
                .stream()
                .filter(p -> kanbanHierarchyId.equals(p.getKanbanHierarchyId()))
                .findFirst().orElse(null);
    }
}
