/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.helper;

import adtekfuji.adcustommonitorapp.service.CellProductionMonitorService;
import adtekfuji.adcustommonitorapp.service.CellProductionMonitorServiceInterface;
import adtekfuji.property.AdProperty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorListInfoEntity;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * リストモニタ成用クラス
 *
 * @author e-mori
 * @version 1.4.3
 * @since 2016.12.16.Fri
 */
public class ListMonitorCreator implements CellProductionMonitorServiceInterface {

    private final static Logger logger = LogManager.getLogger();
    private final ObservableList<MonitorListInfoEntity> items = FXCollections.observableArrayList();
    private TableView<MonitorListInfoEntity> transitionTable;
    private final Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
    private UnitSearchCondition condition;

    /**
     * リスト生成処理
     *
     * @param transitionTable リストの一覧を表示する親の画面
     * @param startDate 実績情報表示開始日
     * @param endDate 実績情報表示終了日
     */
    public void createList(TableView<MonitorListInfoEntity> transitionTable, Date startDate, Date endDate) {
        logger.info(ListMonitorCreator.class.getName() + ":createList start");
        this.transitionTable = transitionTable;

        // 受信時の呼び出し先設定
        CellProductionMonitorService.getInstance().clearCellProductionMonitorServiceInterfaces();
        CellProductionMonitorService.getInstance().addCellProductionMonitorServiceInterface(this);

        // 検索条件
        String[] unitTemplateIds = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_UNITTEMPLATE, "0").split(",");
        this.condition = new UnitSearchCondition().fromDate(startDate).toDate(endDate).unitTemplateIdCollection(new ArrayList<>());
        for (String unitTemplateId : unitTemplateIds) {
            condition.getUnittemplateIdCollection().add(Long.parseLong(unitTemplateId));
        }

        //units = RestAPI.searchUnit(this.condition);
        //units.sort(Comparator.comparing((item) -> item.getCompDatetime()));

        try {
            List<MonitorListInfoEntity> list = RestAPI.getMonitorList(this.condition,
                    properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_MAIN_TITLE_COLUMN, ClientPropertyConstants.DEFAULT_SELECT_MAIN_TITLE),
                    properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_SUB_TITLE_COLUMN, ClientPropertyConstants.DEFAULT_SELECT_SUB_TITLE));

            for (MonitorListInfoEntity info : list) {
                info.setBackgroundColor(this.getBackgroundColor(info));
                this.transitionTable.getItems().add(info);
                items.add(info);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(ListMonitorCreator.class.getName() + ":createList end");
    }

    @Override
    public void receivedActualDataKanbanId(long kanbanId) {
        MonitorListInfoEntity updateItem = checkCarryKanbanId(kanbanId);
        if (Objects.nonNull(updateItem)) {
            updateThread(updateItem);
        }
    }

    /**
     * カンバンIDを保有しているか確認
     *
     * @param kanbanId 確認するカンバンID
     * @return true:持っている/false:持っていない
     */
    private MonitorListInfoEntity checkCarryKanbanId(long kanbanId) {
        logger.info(ListMonitorCreator.class.getName() + ":checkCarryKanbanId start");
        for (MonitorListInfoEntity item : items) {
            if (Objects.nonNull(item.getKanbanIds())) {
                for (Long id : item.getKanbanIds()) {
                    if (id.equals(kanbanId)) {
                        return item;
                    }
                }
            }
        }
        logger.info(ListMonitorCreator.class.getName() + ":checkCarryKanbanId end");
        return null;
    }

    /**
     * 更新処理
     *
     */
    private void updateThread(MonitorListInfoEntity item) {
        logger.info(ListMonitorCreator.class.getName() + ":updateThread start");
        blockUI(Boolean.TRUE);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    condition.setUnitId(item.getUnitId());

                    List<MonitorListInfoEntity> list = RestAPI.getMonitorList(condition,
                        properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_MAIN_TITLE_COLUMN, ClientPropertyConstants.DEFAULT_SELECT_MAIN_TITLE),
                        properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_SUB_TITLE_COLUMN, ClientPropertyConstants.DEFAULT_SELECT_SUB_TITLE));

                    if (list.isEmpty()) {
                        item.update(list.get(0));
                        item.setBackgroundColor(getBackgroundColor(item));
                    }

                    Platform.runLater(() -> {
                        ObservableList<MonitorListInfoEntity> copy = FXCollections.observableArrayList();
                        for (MonitorListInfoEntity item : items) {
                            copy.add(new MonitorListInfoEntity().copy(item));
                        }
                        transitionTable.getItems().clear();
                        transitionTable.setItems(copy);
                    });
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(Boolean.FALSE);
                }
                logger.info(ListMonitorCreator.class.getName() + ":updateThread end");
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * ユニットの表示色設定
     *
     * @param unit
     * @param info
     * @return
     */
    private String getBackgroundColor(MonitorListInfoEntity info) {
        String color = "lightgray";
        if (Objects.nonNull(info)) {
            switch (info.getUnitStatus()) {
                case PLANNED:
                    if (new Date().after(info.getStartDate())) {
                        color = "orange";
                    }
                    break;
                case WORKING:
                    color = "blue";
                    if (info.getProgressTimeMillisec() > 0) {
                        color = "red";
                    }
                    break;
                case SUSPEND:
                    color = "yellow";
                    break;
                default:
                    color = "gray";
                    break;
            }
        }
        return color;
    }

    private void blockUI(Boolean isBlockUI) {
        transitionTable.setDisable(isBlockUI);
    }
}
