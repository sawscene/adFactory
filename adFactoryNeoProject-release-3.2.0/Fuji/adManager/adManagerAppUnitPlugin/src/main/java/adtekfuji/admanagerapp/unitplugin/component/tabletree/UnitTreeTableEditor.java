/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unitplugin.component.tabletree;

import adtekfuji.cash.CashManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.UIControlInterface;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitKanbanInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットツリーテーブル生成処理
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTreeTableEditor {

    private final Logger logger = LogManager.getLogger();

    private final TreeTableView<UnitTreeTableDataEntity> treeTable;
    private final TreeItem<UnitTreeTableDataEntity> rootItem;
    private final UIControlInterface controlInterface;

    private final Image folderIcon
            = new Image(getClass().getResourceAsStream("/image/folder.png"));
    private final Image fileIcon
            = new Image(getClass().getResourceAsStream("/image/file.png"));

    public UnitTreeTableEditor(TreeTableView<UnitTreeTableDataEntity> treeTable, TreeItem<UnitTreeTableDataEntity> rootItem, UIControlInterface controlInterface) {
        this.treeTable = treeTable;
        this.rootItem = rootItem;
        this.controlInterface = controlInterface;
        if (this.rootItem.getChildren().isEmpty()) {
            createUnitTreeTableRootThread();
        }
    }

    /**
     * ユニットテンプレートツリーの親階層生成
     *
     */
    private void createUnitTreeTableRootThread() {
        logger.debug("createTreeRoot start.");
        try {
            //ツリー設定
            rootItem.getChildren().clear();
            rootItem.setExpanded(true);

            CashManager cache = CashManager.getInstance();
            UnitInfoEntity rootUnit = (UnitInfoEntity) rootItem.getValue().getEntity();

            List<Long> kanbanIds = new ArrayList<>();
            rootUnit.getConUnitAssociateCollection().stream().forEach((con) -> {
                if (Objects.nonNull(con.getFkKanbanId())) {
                    kanbanIds.add(con.getFkKanbanId());
                }
            });

            List<UnitKanbanInfoEntity> kanbans = RestAPI.getUnitKanbans(kanbanIds);

            //階層データ取得
            rootUnit.getConUnitAssociateCollection().stream().forEach((con) -> {
                if (Objects.nonNull(con.getFkUnitId())) {
                    UnitInfoEntity childUnit = (UnitInfoEntity) cache.getItem(UnitInfoEntity.class, con.getFkUnitId());
                    if (Objects.isNull(childUnit)) {
                        childUnit = RestAPI.getUnit(con.getFkUnitId());
                        cache.setItem(UnitInfoEntity.class, con.getFkUnitId(), childUnit);
                    }
                    if (Objects.nonNull(childUnit.getUnitId())) {
                        TreeItem<UnitTreeTableDataEntity> item = new TreeItem<>(new UnitTreeTableDataEntity(childUnit), new ImageView(folderIcon));
                        rootItem.getChildren().add(item);
                        createUnitTreeTableBranchsThread(item);
                    }
                } else if (Objects.nonNull(con.getFkKanbanId())) {
                    Optional<UnitKanbanInfoEntity> optional = kanbans.stream().filter(o -> Objects.equals(con.getFkKanbanId(), o.getKanbanId())).findFirst();
                    if (optional.isPresent()) {
                        TreeItem<UnitTreeTableDataEntity> item = new TreeItem<>(new UnitTreeTableDataEntity(optional.get()), new ImageView(fileIcon));
                        rootItem.getChildren().add(item);
                    }
                }
            });

            //名前でソートする
            rootItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getName()));

            //ルートの描画
            Platform.runLater(() -> {
                treeTable.rootProperty().setValue(rootItem);
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.debug("createTreeRoot end.");
    }

    /**
     * ユニットテンプレートツリーの子階層生成
     *
     * @param parentItem 親階層
     */
    private void createUnitTreeTableBranchsThread(TreeItem<UnitTreeTableDataEntity> parentItem) {
        try {
            parentItem.getChildren().clear();
            parentItem.setExpanded(true);

            //親階層が保有する情報の数をカウント
            CashManager cm = CashManager.getInstance();
            UnitInfoEntity branchUnit = (UnitInfoEntity) parentItem.getValue().getEntity();

            List<Long> kanbanIds = new ArrayList<>();
            branchUnit.getConUnitAssociateCollection().stream().forEach((con) -> {
                if (Objects.nonNull(con.getFkKanbanId())) {
                    kanbanIds.add(con.getFkKanbanId());
                }
            });

            List<UnitKanbanInfoEntity> kanbans = RestAPI.getUnitKanbans(kanbanIds);

            branchUnit.getConUnitAssociateCollection().stream().forEach((con) -> {
                if (Objects.nonNull(con.getFkUnitId())) {
                    UnitInfoEntity childUnit = (UnitInfoEntity) cm.getItem(UnitInfoEntity.class, con.getFkUnitId());
                    //UnitInfoEntity childUnit = RestAPI.getUnit(con.getFkUnitId());
                    if (Objects.isNull(childUnit)) {
                        childUnit = RestAPI.getUnit(con.getFkUnitId());
                        cm.setItem(UnitInfoEntity.class, con.getFkUnitId(), childUnit);
                    }
                    if (Objects.nonNull(childUnit.getUnitId())) {
                        TreeItem<UnitTreeTableDataEntity> item = new TreeItem<>(new UnitTreeTableDataEntity(childUnit), new ImageView(folderIcon));
                        parentItem.getChildren().add(item);
                        createUnitTreeTableBranchsThread(item);
                    }
                } else if (Objects.nonNull(con.getFkKanbanId())) {
                    Optional<UnitKanbanInfoEntity> optional = kanbans.stream().filter(o -> Objects.equals(con.getFkKanbanId(), o.getKanbanId())).findFirst();
                    if (optional.isPresent()) {
                        TreeItem<UnitTreeTableDataEntity> item = new TreeItem<>(new UnitTreeTableDataEntity(optional.get()), new ImageView(fileIcon));
                        parentItem.getChildren().add(item);
                    }
                }
            });

            //名前でソートする
            parentItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getName()));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * UIロック
     *
     * @param flag
     */
    private void blockUI(Boolean flag) {
        controlInterface.blockUI(flag);
    }
}
