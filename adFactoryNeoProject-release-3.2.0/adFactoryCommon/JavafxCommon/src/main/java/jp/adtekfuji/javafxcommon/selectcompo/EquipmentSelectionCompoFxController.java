/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.selectcompo;

import adtekfuji.cash.CashManager;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import adtekfuji.locale.LocaleUtils;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.javafxcommon.treecell.EquipmentTreeCell;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 設備選択ダイアログ
 *
 * @author e-mori
 */
@FxComponent(id = "EquipmentSelectionCompo", fxmlPath = "/fxml/compo/equipment_selection_compo.fxml")
public class EquipmentSelectionCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final static long ROOT_ID = 0;

    private SelectDialogEntity settingDialogEntity;
    private List<EquipmentInfoEntity> entities;

    @FXML
    private TreeView<EquipmentInfoEntity> hierarchyTree;
    @FXML
    private ListView<EquipmentInfoEntity> itemList;
    @FXML
    private Pane progressPane;
    @FXML
    private StackPane stackPane;

    /**
     * 設備選択ダイアログを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.hierarchyTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.itemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 選択済リスト
        Callback<ListView<EquipmentInfoEntity>, ListCell<EquipmentInfoEntity>> cellFactory = (ListView<EquipmentInfoEntity> param) -> new ListItemCell();
        this.itemList.setCellFactory(cellFactory);

        this.blockUI(true);

        Task task = new Task<TreeItem<EquipmentInfoEntity>>() {
            @Override
            protected TreeItem<EquipmentInfoEntity> call() throws Exception {
                CashManager cache = CashManager.getInstance();

                // キャッシュに設備情報を読み込む。(未キャッシュの場合のみ)
                CacheUtils.createCacheEquipment(true);

                // 削除済みのデータを取り除く。
                entities = ((List<EquipmentInfoEntity>) cache.getItemList(EquipmentInfoEntity.class, new ArrayList<>()))
                        .stream().filter(p -> Objects.isNull(p.getRemoveFlag()) || !p.getRemoveFlag())
                        .collect(Collectors.toList());

                return createRoot();
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    TreeItem<EquipmentInfoEntity> rootItem = this.getValue();

                    hierarchyTree.setRoot(rootItem);
                    hierarchyTree.setCellFactory((TreeView<EquipmentInfoEntity> o) -> new EquipmentTreeCell());

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
            this.itemList.getItems().addAll(settingDialogEntity.getEquipments());
            this.settingDialogEntity.equipmentsProperty().bind(itemList.itemsProperty());
        }
    }

    @FXML
    private void OnAdd(ActionEvent event) {
        for (TreeItem<EquipmentInfoEntity> item : hierarchyTree.getSelectionModel().getSelectedItems()) {
            if (Objects.nonNull(item.getParent())
                    && !itemList.getItems().contains(item.getValue())) {
                this.itemList.getItems().add(item.getValue());
            }
        }
    }

    @FXML
    private void OnRemove(ActionEvent event) {
        if (Objects.nonNull(itemList.getSelectionModel().getSelectedItem())) {
            itemList.getSelectionModel()
                    .getSelectedIndices()
                    .stream()
                    .sorted(Comparator.reverseOrder())
                    .mapToInt(Integer::intValue)
                    .forEach(index -> this.itemList.getItems().remove(index));
        }
    }

    /**
     * ListView表示用セル
     *
     */
    class ListItemCell extends ListCell<EquipmentInfoEntity> {

        @Override
        protected void updateItem(EquipmentInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                this.setText(item.getEquipmentName());
            } else {
                this.setText("");
            }
        }
    }

    /**
     * ツリーの親階層生成
     *
     */
    private TreeItem<EquipmentInfoEntity> createRoot() {
        TreeItem<EquipmentInfoEntity> rootItem = new TreeItem<>(new EquipmentInfoEntity(ROOT_ID, null, LocaleUtils.getString("key.Equipment"), null));
        try {
            logger.info("createRoot start.");

            rootItem.getChildren().clear();

            List<EquipmentInfoEntity> rootList = new ArrayList<>();
            this.entities.stream().filter((entity) -> Objects.nonNull(entity.getParentId()) && entity.getParentId() == ROOT_ID).forEach((entity) -> {
                rootList.add(entity);
            });

            rootList.sort((a, b) -> a.getEquipmentName().compareTo(b.getEquipmentName()));
            rootList.stream().forEach((entity) -> {
                addHierarchies(new TreeItem<>(entity), rootItem);
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("createTreeRoot end.");
        }
        return rootItem;
    }

    /**
     * 階層生成
     *
     * @param childItem 子階層
     * @param parentItem 親階層
     */
    private void addHierarchies(TreeItem<EquipmentInfoEntity> childItem, TreeItem<EquipmentInfoEntity> parentItem) {
        try {
            List<EquipmentInfoEntity> children = new ArrayList<>();

            this.entities.stream().filter((entity) -> (Objects.equals(entity.getParentId(), childItem.getValue().getEquipmentId()))).forEach((entity) -> {
                children.add(entity);
            });

            if (!children.isEmpty()) {
                children.sort((a, b) -> a.getEquipmentName().compareTo(b.getEquipmentName()));
                children.stream().forEach((entity) -> {
                    addHierarchies(new TreeItem<>(entity), childItem);
                });
            }

            parentItem.getChildren().add(childItem);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
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
}
