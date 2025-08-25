/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.controller;

import adtekfuji.adcustommonitorapp.helper.ListMonitorCreator;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import jp.adtekfuji.forfujiapp.clientnativeservice.ChangeDateMonitoringHandler;
import jp.adtekfuji.forfujiapp.clientnativeservice.ChangeDateMonitoringService;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorListInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * セル生産監視画面リスト表示クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.5.Mon
 */
@FxComponent(id = "CellProductionListMonitorCompo", fxmlPath = "/fxml/adcustommonitorapp/cellProductionListMonitorCompo.fxml")
public class CellProductionListMonitorCompoFxController implements Initializable, ChangeDateMonitoringHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TableView<MonitorListInfoEntity> transitionTable;
    @FXML
    private TableColumn<MonitorListInfoEntity, String> mainColumn;
    @FXML
    private TableColumn<MonitorListInfoEntity, String> subColumn;
    @FXML
    private TableColumn<MonitorListInfoEntity, String> shipDateColumn;
    @FXML
    private TableColumn<MonitorListInfoEntity, String> unitColumn;
    @FXML
    private TableColumn<MonitorListInfoEntity, String> transitionColumn;
    @FXML
    private TableColumn<MonitorListInfoEntity, String> statusColumn;
    @FXML
    Pane progressPane;

    private SimpleDateFormat formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(CellProductionListMonitorCompoFxController.class.getName() + ":initialize start");

        transitionTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));

        //日付選択用のピッカーを今日の日付に設定
        startDatePicker.setValue(DateUtils.toLocalDate(DateUtils.getBeginningOfDate(new Date())));
        startDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            createListMonitor();
        });
        bindColumn();
        createListMonitor();
        ChangeDateMonitoringService service = ChangeDateMonitoringService.getInstance();
        service.setChangeDateMonitoringHandler(this);
        service.start();

        logger.info(CellProductionListMonitorCompoFxController.class.getName() + ":initialize end");
    }

    /**
     * モニター画面生成処理
     *
     */
    private void createListMonitor() {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    transitionTable.getItems().clear();
                    endDatePicker.setValue(DateUtils.toLocalDate(DateUtils.getEndOfDate(new Date())));
                    new ListMonitorCreator().createList(transitionTable,
                            DateUtils.getBeginningOfDate(DateUtils.toDate(startDatePicker.getValue())),
                            DateUtils.getEndOfDate(DateUtils.toDate(endDatePicker.getValue())));
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * カラムの情報とのバインド処理
     *
     */
    private void bindColumn() {
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        mainColumn.setText(properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_MAIN_TITLE_COLUMN, ClientPropertyConstants.DEFAULT_SELECT_MAIN_TITLE));
        mainColumn.setCellValueFactory((TableColumn.CellDataFeatures<MonitorListInfoEntity, String> param) -> param.getValue().mainTitleProperty());
        subColumn.setText(properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_SUB_TITLE_COLUMN, ClientPropertyConstants.DEFAULT_SELECT_SUB_TITLE));
        subColumn.setCellValueFactory((TableColumn.CellDataFeatures<MonitorListInfoEntity, String> param) -> param.getValue().subTitleProperty());
        shipDateColumn.setCellValueFactory((TableColumn.CellDataFeatures<MonitorListInfoEntity, String> param) -> Bindings.createStringBinding(()
                -> Objects.isNull(param.getValue().getShipDate()) ? "" : formatter.format(param.getValue().getShipDate())));
        unitColumn.setCellValueFactory((TableColumn.CellDataFeatures<MonitorListInfoEntity, String> param) -> param.getValue().unitTemplateNameProperty());
        transitionColumn.setCellValueFactory((TableColumn.CellDataFeatures<MonitorListInfoEntity, String> param) -> Bindings.createStringBinding(()
                -> MessageFormat.format("{0, number, 0.0}%", param.getValue().getWorkProgress() * 100)));
        statusColumn.setCellValueFactory((TableColumn.CellDataFeatures<MonitorListInfoEntity, String> param) -> Bindings.createStringBinding(()
                -> LocaleUtils.getString(param.getValue().getUnitStatus().getResourceKey())));

        // 画面更新時のステータスカラーの追加
        transitionTable.setRowFactory(new Callback<TableView<MonitorListInfoEntity>, TableRow<MonitorListInfoEntity>>() {
            @Override
            public TableRow<MonitorListInfoEntity> call(TableView<MonitorListInfoEntity> tableView) {
                final TableRow<MonitorListInfoEntity> row = new TableRow<MonitorListInfoEntity>() {
                    @Override
                    protected void updateItem(MonitorListInfoEntity list, boolean empty) {
                        super.updateItem(list, empty);
                        if (!empty) {
                            if (!list.getBackgroundColor().isEmpty()) {
                                setStyle("-fx-background-color: " + list.getBackgroundColor() + ";");
                            }
                        }
                    }
                };
                return row;
            }
        });
    }

    /**
     * 画面の利用制限
     *
     * @param isBlock true;有効/false:無効
     */
    private void blockUI(boolean isBlock) {
        sc.blockUI(isBlock);
        progressPane.setVisible(isBlock);
    }

    /**
     * 日付が変わった場合の画面更新処理
     *
     */
    @Override
    public void changeDate() {
        blockUI(false);
        createListMonitor();
    }

}
