/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unitplugin.component.tabletree;

import javafx.scene.control.*;
import jp.adtekfuji.forfujiapp.common.UIControlInterface;
import adtekfuji.admanagerapp.unitplugin.component.UnitDetailCompoInterface;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.WorkflowInfoFacade;
import jp.adtekfuji.forfujiapp.dialog.DialogController;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitKanbanInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットツリーテーブル画面
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.11.Fri
 */
public class UniTreeTableController implements Initializable, UIControlInterface {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final UnitDetailCompoInterface detailCompoInterface;
    private final WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();

    @FXML
    private TreeTableView<UnitTreeTableDataEntity> unitTreeTable;
    @FXML
    private TreeTableColumn<UnitTreeTableDataEntity, String> titleColumn;
    @FXML
    private TreeTableColumn<UnitTreeTableDataEntity, String> templateColumn;
    @FXML
    private TreeTableColumn<UnitTreeTableDataEntity, String> statusColumn;
    @FXML
    private TreeTableColumn<UnitTreeTableDataEntity, String> startDateColumn;
    @FXML
    private TreeTableColumn<UnitTreeTableDataEntity, String> endDateColumn;

    public UniTreeTableController(UnitDetailCompoInterface detailCompoInterface) {
        this.detailCompoInterface = detailCompoInterface;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(UniTreeTableController.class.getName() + ":initialize start");

        unitTreeTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        titleColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<UnitTreeTableDataEntity, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getName()));
        templateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<UnitTreeTableDataEntity, String> param)
                -> {
            String displayName = param.getValue().getValue().getTamplateName();
            if (param.getValue().getValue().getEntity() instanceof UnitKanbanInfoEntity) {
                Integer rev = workflowInfoFacade.find(
                        ((UnitKanbanInfoEntity) param.getValue().getValue().getEntity()).getFkWorkflowId()).getWorkflowRev();
                displayName = displayName + " : " + rev;
            }
            return new ReadOnlyStringWrapper(displayName);
        });
        statusColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<UnitTreeTableDataEntity, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getStatus()));
        startDateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<UnitTreeTableDataEntity, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getStartDate()));
        startDateColumn.setSortType(TreeTableColumn.SortType.ASCENDING);
        endDateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<UnitTreeTableDataEntity, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getEndDate()));
        unitTreeTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        unitTreeTable.getSortOrder().add(startDateColumn);

        // ツリーテーブルのレコード選択時のイベント
        unitTreeTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                blockUI(true);
                try {
                    Object entity = unitTreeTable.getSelectionModel().getSelectedItem().getValue().getEntity();

                    if (entity instanceof UnitInfoEntity) {
                        // ユニット編集画面表示
                        unitTreeTable.getSelectionModel().getSelectedItem().setExpanded(true);
                        UnitInfoEntity unitInfoEntity = (UnitInfoEntity) unitTreeTable.getSelectionModel().getSelectedItem().getValue().getEntity();
                        final String oldName = unitInfoEntity.getUnitName();

                        DialogController.showEditUnit(unitInfoEntity, this);

                        // エディターで変更したユニットを取り直し.
                        unitInfoEntity = RestAPI.getUnit(unitInfoEntity.getUnitId());
                        CashManager cache = CashManager.getInstance();
                        cache.setItem(UnitInfoEntity.class, unitInfoEntity.getUnitId(), unitInfoEntity);

                        //カンバンのユニット名更新
                        final String newName = unitInfoEntity.getUnitName();
                        if (!Objects.equals(oldName, newName)) {
                            UnitInfoEntity rootUnit = (UnitInfoEntity) unitTreeTable.getRoot().getValue().getEntity();

                            List<Long> kanbanIds = new ArrayList<>();
                            rootUnit.getConUnitAssociateCollection().stream().forEach((con) -> {
                                if (Objects.nonNull(con.getFkKanbanId())) {
                                    kanbanIds.add(con.getFkKanbanId());
                                }
                            });

                            List<UnitKanbanInfoEntity> kanbans = RestAPI.getUnitKanbans(kanbanIds);
                            kanbans.stream()
                                    .map(unitKanban -> RestAPI.getKanban(unitKanban.getKanbanId()))
                                    .forEach(kanban -> {
                                        kanban.setKanbanName(kanban.getKanbanName().replaceFirst("^" + oldName, newName));
                                        RestAPI.updateKanban(kanban);
                                    });
                        }

                        updateUI();
                    } else if (entity instanceof UnitKanbanInfoEntity) {
                        // カンバン編集画面表示
                        unitTreeTable.getSelectionModel().getSelectedItem().setExpanded(true);

                        KanbanInfoEntity kanban = RestAPI.getKanban(((UnitKanbanInfoEntity) entity).getKanbanId());
                        DialogController.showEditKanban(kanban, this);

                        // カンバンを再取得
                        this.updateUI();
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
            }
        });

        logger.info(UniTreeTableController.class.getName() + ":initialize end");
    }

    /**
     * 一覧画面に戻る
     *
     * @param event
     */
    @FXML
    public void onBackToList(ActionEvent event) {
        logger.info(UniTreeTableController.class.getName() + ":onBackToList start");
        sc.setComponent("ContentNaviPane", "UnitListComp");
        logger.info(UniTreeTableController.class.getName() + ":onBackToList end");
    }

    /**
     * 生産ユニットのツリーテーブルを生成
     *
     * @param entity
     */
    public void createTreeTable(UnitInfoEntity entity) {
        try {
            CashManager cm = CashManager.getInstance();
            UnitInfoEntity temp = (UnitInfoEntity) cm.getItem(UnitInfoEntity.class, entity.getUnitId());
            if (Objects.isNull(temp)) {
                temp = RestAPI.getUnit(entity.getUnitId());
                cm.setItem(UnitInfoEntity.class, entity.getUnitId(), temp);
            }

            UnitTreeTableDataEntity treeTableData = new UnitTreeTableDataEntity(temp);
            UnitTreeTableEditor treeEditor
                    = new UnitTreeTableEditor(unitTreeTable,
                            new TreeItem<>(treeTableData, new ImageView(new Image(getClass().getResourceAsStream("/image/folder_top.png")))), this);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    @Override
    public void blockUI(boolean isBlock) {
        this.detailCompoInterface.blockUI(isBlock);
    }

    /**
     * 現在表示されている画面の更新
     *
     */
    @Override
    public void updateUI() {
        createTreeTable((UnitInfoEntity) unitTreeTable.getRoot().getValue().getEntity());
    }
}
