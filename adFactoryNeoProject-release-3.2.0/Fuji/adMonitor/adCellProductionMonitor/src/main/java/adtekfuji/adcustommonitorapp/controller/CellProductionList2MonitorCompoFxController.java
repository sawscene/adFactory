/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.controller;

import adtekfuji.adcustommonitorapp.helper.List2MonitorCreator;
import adtekfuji.adcustommonitorapp.service.CellProductionMonitorService;
import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.forfujiapp.clientnativeservice.ChangeDateMonitoringHandler;
import jp.adtekfuji.forfujiapp.clientnativeservice.ChangeDateMonitoringService;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorList2InfoEntity;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.javafxcommon.utils.SwitchCompoObserver;
import jp.adtekfuji.javafxcommon.utils.SwitchCompoSubject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Date;
import jp.adtekfuji.forfujiapp.common.AdFactoryForFujiClientAppConfig;
import org.apache.commons.lang.time.DateUtils;

/**
 * セル生産監視画面リスト表示クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.5.Mon
 */
@FxComponent(id = "CellProductionList2MonitorCompo", fxmlPath = "/fxml/adcustommonitorapp/cellProductionList2MonitorCompo.fxml")
public class CellProductionList2MonitorCompoFxController implements Initializable, ChangeDateMonitoringHandler, SwitchCompoObserver {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat sdfHeader = new SimpleDateFormat("MM/dd(E)");
    private final ArrayList<Calendar> holidays = new ArrayList<>();
    private final List2MonitorCreator list2MonitorCreator1 = new List2MonitorCreator();
    private final List2MonitorCreator list2MonitorCreator2 = new List2MonitorCreator();
    private final List2MonitorCreator list2MonitorCreator3 = new List2MonitorCreator();
    private final SwitchCompoSubject switchCompoSubject = SwitchCompoSubject.getInstance();
    private final String borderStyle = "-fx-border-width: 1.0 0.5 1.0 0.5; -fx-border-color: black black black black; -fx-border-style: solid dashed solid dashed;";
    private final String fontStyleList = " -fx-font-size: " + properties.getProperty(ClientPropertyConstants.PROP_KEY_LIST2_FONTSIZE_LIST, ClientPropertyConstants.DEFAULT_LIST2_FONTSIZE_LIST) + ";";
    private final String fontAlignmentList = "-fx-alignment: bottom-left;";
    private final String fontStyleHeader = " -fx-font-size: " + properties.getProperty(ClientPropertyConstants.PROP_KEY_LIST2_FONTSIZE_HEADER, ClientPropertyConstants.DEFAULT_LIST2_FONTSIZE_HEADER) + ";";
    private final String defaultFontColor = "black";
    private final String defaultBackColor = "white";
    private Calendar cal1 = Calendar.getInstance();
    private Calendar cal2 = Calendar.getInstance();
    private Calendar cal3 = Calendar.getInstance();

    private final AdFactoryForFujiClientAppConfig config = new AdFactoryForFujiClientAppConfig();
    //private final DisplayedStatusInfoFacade displayedStatusInfoFacade = new DisplayedStatusInfoFacade(config.getAdFactoryForFujiServerAddress());
    //private final OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade(config.getAdFactoryForFujiServerAddress());

    List<UnitInfoEntity> unitEntitys = new ArrayList<>();
    List<OrganizationInfoEntity> organizationEntitys = new ArrayList<>();
    List<DisplayedStatusInfoEntity> displayedStatusInfoEntitys = new ArrayList<>();

    @FXML
    private Label date1;
    @FXML
    private Label date2;
    @FXML
    private Label date3;
    @FXML
    private Label notStartedTitle1;
    @FXML
    private Label notStartedTitle2;
    @FXML
    private Label notStartedTitle3;
    @FXML
    private Label workmanshipTitle1;
    @FXML
    private Label workmanshipTitle2;
    @FXML
    private Label workmanshipTitle3;
    @FXML
    private Label shipmentsTitle1;
    @FXML
    private Label shipmentsTitle2;
    @FXML
    private Label shipmentsTitle3;
    @FXML
    private Label Separator11;
    @FXML
    private Label Separator12;
    @FXML
    private Label Separator21;
    @FXML
    private Label Separator22;
    @FXML
    private Label Separator31;
    @FXML
    private Label Separator32;
    @FXML
    private Label notStarted1;
    @FXML
    private Label notStarted2;
    @FXML
    private Label notStarted3;
    @FXML
    private Label workmanship1;
    @FXML
    private Label workmanship2;
    @FXML
    private Label workmanship3;
    @FXML
    private Label shipments1;
    @FXML
    private Label shipments2;
    @FXML
    private Label shipments3;
    @FXML
    private TableView<MonitorList2InfoEntity> transitionTable1;
    @FXML
    private TableView<MonitorList2InfoEntity> transitionTable2;
    @FXML
    private TableView<MonitorList2InfoEntity> transitionTable3;
    @FXML
    private TableColumn<MonitorList2InfoEntity, String> work11Column;
    @FXML
    private TableColumn<MonitorList2InfoEntity, String> work12Column;
    @FXML
    private TableColumn<MonitorList2InfoEntity, String> work21Column;
    @FXML
    private TableColumn<MonitorList2InfoEntity, String> work22Column;
    @FXML
    private TableColumn<MonitorList2InfoEntity, String> work31Column;
    @FXML
    private TableColumn<MonitorList2InfoEntity, String> work32Column;
    @FXML
    Pane progressPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(CellProductionList2MonitorCompoFxController.class.getName() + ":initialize start");
        blockUI(false);

        // 受信時の呼び出し先設定
        CellProductionMonitorService.getInstance().clearCellProductionMonitorServiceInterfaces();
        CellProductionMonitorService.getInstance().addCellProductionMonitorServiceInterface(list2MonitorCreator1);
        CellProductionMonitorService.getInstance().addCellProductionMonitorServiceInterface(list2MonitorCreator2);
        CellProductionMonitorService.getInstance().addCellProductionMonitorServiceInterface(list2MonitorCreator3);

        bindColumn();
        setColumnWidth();
        setStyle();

        createListMonitor();

        ChangeDateMonitoringService service = ChangeDateMonitoringService.getInstance();
        service.setChangeDateMonitoringHandler(this);
        service.start();

        switchCompoSubject.setObserver(this);
        logger.info(CellProductionList2MonitorCompoFxController.class.getName() + ":initialize end");
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
                    setDisplayDate();
                    drawDate();

                    Calendar cal = (Calendar) cal3.clone();
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    updateUnitList(cal1.getTime(), cal.getTime());
                    updateOrganizationList();
                    updateDisplayedStatusList();

                    list2MonitorCreator1.setUnitList(unitEntitys.stream().filter((entity) -> (0 == DateUtils.truncate(cal1.getTime(), Calendar.DAY_OF_MONTH).compareTo(DateUtils.truncate(entity.getCompDatetime(), Calendar.DAY_OF_MONTH)))).collect(Collectors.toList()));
                    list2MonitorCreator2.setUnitList(unitEntitys.stream().filter((entity) -> (0 == DateUtils.truncate(cal2.getTime(), Calendar.DAY_OF_MONTH).compareTo(DateUtils.truncate(entity.getCompDatetime(), Calendar.DAY_OF_MONTH)))).collect(Collectors.toList()));
                    list2MonitorCreator3.setUnitList(unitEntitys.stream().filter((entity) -> (0 == DateUtils.truncate(cal3.getTime(), Calendar.DAY_OF_MONTH).compareTo(DateUtils.truncate(entity.getCompDatetime(), Calendar.DAY_OF_MONTH)))).collect(Collectors.toList()));
                    list2MonitorCreator1.setOrganizationList(organizationEntitys);
                    list2MonitorCreator2.setOrganizationList(organizationEntitys);
                    list2MonitorCreator3.setOrganizationList(organizationEntitys);
                    list2MonitorCreator1.setDisplayedStatusList(displayedStatusInfoEntitys);
                    list2MonitorCreator2.setDisplayedStatusList(displayedStatusInfoEntitys);
                    list2MonitorCreator3.setDisplayedStatusList(displayedStatusInfoEntitys);

                    transitionTable1.getItems().clear();
                    transitionTable2.getItems().clear();
                    transitionTable3.getItems().clear();
                    list2MonitorCreator1.createList(transitionTable1);
                    list2MonitorCreator2.createList(transitionTable2);
                    list2MonitorCreator3.createList(transitionTable3);
                    Platform.runLater(() -> {
                        list2MonitorCreator1.sort();
                        list2MonitorCreator2.sort();
                        list2MonitorCreator3.sort();
                    });
                    list2MonitorCreator1.createHeader(notStarted1, workmanship1, shipments1);
                    list2MonitorCreator2.createHeader(notStarted2, workmanship2, shipments2);
                    list2MonitorCreator3.createHeader(notStarted3, workmanship3, shipments3);
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

        work11Column.setCellValueFactory((TableColumn.CellDataFeatures<MonitorList2InfoEntity, String> param) -> Bindings.createStringBinding(() -> Objects.isNull(param.getValue().getOrganization1()) ? "" : param.getValue().getOrganization1().getOrganizationName()));
        work12Column.setCellValueFactory((TableColumn.CellDataFeatures<MonitorList2InfoEntity, String> param) -> Bindings.createStringBinding(() -> Objects.isNull(param.getValue().getOrganization2()) ? "" : param.getValue().getOrganization2().getOrganizationName()));
        work21Column.setCellValueFactory((TableColumn.CellDataFeatures<MonitorList2InfoEntity, String> param) -> Bindings.createStringBinding(() -> Objects.isNull(param.getValue().getOrganization1()) ? "" : param.getValue().getOrganization1().getOrganizationName()));
        work22Column.setCellValueFactory((TableColumn.CellDataFeatures<MonitorList2InfoEntity, String> param) -> Bindings.createStringBinding(() -> Objects.isNull(param.getValue().getOrganization2()) ? "" : param.getValue().getOrganization2().getOrganizationName()));
        work31Column.setCellValueFactory((TableColumn.CellDataFeatures<MonitorList2InfoEntity, String> param) -> Bindings.createStringBinding(() -> Objects.isNull(param.getValue().getOrganization1()) ? "" : param.getValue().getOrganization1().getOrganizationName()));
        work32Column.setCellValueFactory((TableColumn.CellDataFeatures<MonitorList2InfoEntity, String> param) -> Bindings.createStringBinding(() -> Objects.isNull(param.getValue().getOrganization2()) ? "" : param.getValue().getOrganization2().getOrganizationName()));

        work11Column.setCellFactory(tableColumn -> {
            return new TableCell<MonitorList2InfoEntity, String>() {
                @Override
                protected void updateItem(final String item, final boolean empty) {
                    String fontColor = defaultFontColor;
                    String backColor = defaultBackColor;
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        StatusPatternEnum status = list2MonitorCreator1.getStatus(transitionTable1.getItems().get(getIndex()).getKanban1Status());
                        fontColor = getFontColor(status);
                        backColor = getBackgroundColor(status);
                    }
                    setStyle("-fx-text-background-color: " + fontColor + ";" + "-fx-background-color: " + backColor + ";" + borderStyle + fontStyleList + fontAlignmentList);
                }
            };
        });

        work12Column.setCellFactory(tableColumn -> {
            return new TableCell<MonitorList2InfoEntity, String>() {
                @Override
                protected void updateItem(final String item, final boolean empty) {
                    String fontColor = defaultFontColor;
                    String backColor = defaultBackColor;
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        StatusPatternEnum status = list2MonitorCreator1.getStatus(transitionTable1.getItems().get(getIndex()).getKanban2Status());
                        fontColor = getFontColor(status);
                        backColor = getBackgroundColor(status);
                    }
                    setStyle("-fx-text-background-color: " + fontColor + ";" + "-fx-background-color: " + backColor + ";" + borderStyle + fontStyleList + fontAlignmentList);
                }
            };
        });

        work21Column.setCellFactory(tableColumn -> {
            return new TableCell<MonitorList2InfoEntity, String>() {
                @Override
                protected void updateItem(final String item, final boolean empty) {
                    String fontColor = defaultFontColor;
                    String backColor = defaultBackColor;
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        StatusPatternEnum status = list2MonitorCreator2.getStatus(transitionTable2.getItems().get(getIndex()).getKanban1Status());
                        fontColor = getFontColor(status);
                        backColor = getBackgroundColor(status);
                    }
                    setStyle("-fx-text-background-color: " + fontColor + ";" + "-fx-background-color: " + backColor + ";" + borderStyle + fontStyleList + fontAlignmentList);
                }

            };
        });

        work22Column.setCellFactory(tableColumn -> {
            return new TableCell<MonitorList2InfoEntity, String>() {
                @Override
                protected void updateItem(final String item, final boolean empty) {
                    String fontColor = defaultFontColor;
                    String backColor = defaultBackColor;
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        StatusPatternEnum status = list2MonitorCreator2.getStatus(transitionTable2.getItems().get(getIndex()).getKanban2Status());
                        fontColor = getFontColor(status);
                        backColor = getBackgroundColor(status);
                    }
                    setStyle("-fx-text-background-color: " + fontColor + ";" + "-fx-background-color: " + backColor + ";" + borderStyle + fontStyleList + fontAlignmentList);
                }
            };
        });

        work31Column.setCellFactory(tableColumn -> {
            return new TableCell<MonitorList2InfoEntity, String>() {
                @Override
                protected void updateItem(final String item, final boolean empty) {
                    String fontColor = defaultFontColor;
                    String backColor = defaultBackColor;
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        StatusPatternEnum status = list2MonitorCreator3.getStatus(transitionTable3.getItems().get(getIndex()).getKanban1Status());
                        fontColor = getFontColor(status);
                        backColor = getBackgroundColor(status);
                    }
                    setStyle("-fx-text-background-color: " + fontColor + ";" + "-fx-background-color: " + backColor + ";" + borderStyle + fontStyleList + fontAlignmentList);
                }
            };
        });

        work32Column.setCellFactory(tableColumn -> {
            return new TableCell<MonitorList2InfoEntity, String>() {
                @Override
                protected void updateItem(final String item, final boolean empty) {
                    String fontColor = defaultFontColor;
                    String backColor = defaultBackColor;
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        StatusPatternEnum status = list2MonitorCreator3.getStatus(transitionTable3.getItems().get(getIndex()).getKanban2Status());
                        fontColor = getFontColor(status);
                        backColor = getBackgroundColor(status);
                    }
                    setStyle("-fx-text-background-color: " + fontColor + ";" + "-fx-background-color: " + backColor + ";" + borderStyle + fontStyleList + fontAlignmentList);
                }
            };
        });

        String str = properties.getProperty(ClientPropertyConstants.PROP_KEY_LIST2_TITLE_COLUMN, "");
        String[] titles = str.split(",");

        int i = 0;
        for (String title : titles) {
            TableColumn<MonitorList2InfoEntity, String> propertyColumn1 = new TableColumn<>(title);
            TableColumn<MonitorList2InfoEntity, String> propertyColumn2 = new TableColumn<>(title);
            TableColumn<MonitorList2InfoEntity, String> propertyColumn3 = new TableColumn<>(title);

            propertyColumn1.setCellValueFactory((TableColumn.CellDataFeatures<MonitorList2InfoEntity, String> param) -> param.getValue().getPropertyValue(title));
            propertyColumn2.setCellValueFactory((TableColumn.CellDataFeatures<MonitorList2InfoEntity, String> param) -> param.getValue().getPropertyValue(title));
            propertyColumn3.setCellValueFactory((TableColumn.CellDataFeatures<MonitorList2InfoEntity, String> param) -> param.getValue().getPropertyValue(title));

            transitionTable1.getColumns().add(i, propertyColumn1);
            transitionTable2.getColumns().add(i, propertyColumn2);
            transitionTable3.getColumns().add(i, propertyColumn3);
            i++;
        }

        // 画面更新時のステータスカラーの追加
        transitionTable1.setRowFactory(new Callback<TableView<MonitorList2InfoEntity>, TableRow<MonitorList2InfoEntity>>() {
            @Override
            public TableRow<MonitorList2InfoEntity> call(TableView<MonitorList2InfoEntity> tableView) {
                final TableRow<MonitorList2InfoEntity> row = new TableRow<MonitorList2InfoEntity>() {
                    @Override
                    protected void updateItem(MonitorList2InfoEntity list, boolean empty) {
                        super.updateItem(list, empty);
                        if (empty || list == null) {
                            setItem(null);
                            setGraphic(null);
                            setStyle("-fx-text-background-color: " + defaultFontColor + ";" + "-fx-background-color: " + defaultBackColor + ";");
                        } else {
                            setItem(list);
                            StatusPatternEnum status = list2MonitorCreator1.getStatus(list.getKanban1Status(), list.getKanban2Status());
                            setStyle("-fx-text-background-color: " + getFontColor(status) + ";" + "-fx-background-color: " + getBackgroundColor(status) + ";");
                        }
                    }
                };
                return row;
            }
        });

        transitionTable2.setRowFactory(new Callback<TableView<MonitorList2InfoEntity>, TableRow<MonitorList2InfoEntity>>() {
            @Override
            public TableRow<MonitorList2InfoEntity> call(TableView<MonitorList2InfoEntity> tableView) {
                final TableRow<MonitorList2InfoEntity> row = new TableRow<MonitorList2InfoEntity>() {
                    @Override
                    protected void updateItem(MonitorList2InfoEntity list, boolean empty) {
                        super.updateItem(list, empty);
                        if (empty || list == null) {
                            setItem(null);
                            setGraphic(null);
                            setStyle("-fx-text-background-color: " + defaultFontColor + ";" + "-fx-background-color: " + defaultBackColor + ";");
                        } else {
                            setItem(list);
                            StatusPatternEnum status = list2MonitorCreator2.getStatus(list.getKanban1Status(), list.getKanban2Status());
                            setStyle("-fx-text-background-color: " + getFontColor(status) + ";" + "-fx-background-color: " + getBackgroundColor(status) + ";");
                        }
                    }
                };
                return row;
            }
        });

        transitionTable3.setRowFactory(new Callback<TableView<MonitorList2InfoEntity>, TableRow<MonitorList2InfoEntity>>() {
            @Override
            public TableRow<MonitorList2InfoEntity> call(TableView<MonitorList2InfoEntity> tableView) {
                final TableRow<MonitorList2InfoEntity> row = new TableRow<MonitorList2InfoEntity>() {
                    @Override
                    protected void updateItem(MonitorList2InfoEntity list, boolean empty) {
                        super.updateItem(list, empty);
                        if (empty || list == null) {
                            setItem(null);
                            setGraphic(null);
                            setStyle("-fx-text-background-color: " + defaultFontColor + ";" + "-fx-background-color: " + defaultBackColor + ";");
                        } else {
                            setItem(list);
                            StatusPatternEnum status = list2MonitorCreator3.getStatus(list.getKanban1Status(), list.getKanban2Status());
                            setStyle("-fx-text-background-color: " + getFontColor(status) + ";" + "-fx-background-color: " + getBackgroundColor(status) + ";");
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

    /**
     * 日付描画
     *
     */
    private void drawDate() {
        Platform.runLater(() -> {
            date1.setText(sdfHeader.format(cal1.getTime()));
            date2.setText(sdfHeader.format(cal2.getTime()));
            date3.setText(sdfHeader.format(cal3.getTime()));
        });
    }

    /**
     * 表示日付設定
     *
     */
    private void setDisplayDate() {
        readHolidays();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        while (!(holidays.indexOf(cal) == -1)) {
            // 休日リストと一致しなくなるまで翌日に進める.
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        cal1 = (Calendar) cal.clone();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        while (!(holidays.indexOf(cal) == -1)) {
            // 休日リストと一致しなくなるまで翌日に進める.
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        cal2 = (Calendar) cal.clone();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        while (!(holidays.indexOf(cal) == -1)) {
            // 休日リストと一致しなくなるまで翌日に進める.
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        cal3 = (Calendar) cal.clone();
    }

    /**
     * 休日ファイル読込.
     *
     */
    private void readHolidays() {
        holidays.clear();
        String filePath = properties.getProperty(ClientPropertyConstants.PROP_KEY_LIST2_HOLIDAY_FILE, ClientPropertyConstants.DEFAULT_LIST2_HOLIDAY_FILE);

        if (filePath.isEmpty()) {
            return;
        }

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            if (!file.isFile()) {
                return;
            }
            if (!file.canRead()) {
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(file));

            String str;
            while ((str = br.readLine()) != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(str));
                holidays.add(cal);
            }

            br.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    // 画面切り替え(または、閉じる)通知.
    // リストの列幅を覚えておく.
    @Override
    public void switchCompo() {
        int i = 0;
        for (TableColumn tc : transitionTable1.getColumns()) {
            properties.setProperty(ClientPropertyConstants.PROP_KEY_LIST2_COLUMN_WIDTH1 + String.valueOf(i), String.valueOf(tc.getWidth()));
            i++;
        }
        i = 0;
        for (TableColumn tc : transitionTable2.getColumns()) {
            properties.setProperty(ClientPropertyConstants.PROP_KEY_LIST2_COLUMN_WIDTH2 + String.valueOf(i), String.valueOf(tc.getWidth()));
            i++;
        }
        i = 0;
        for (TableColumn tc : transitionTable3.getColumns()) {
            properties.setProperty(ClientPropertyConstants.PROP_KEY_LIST2_COLUMN_WIDTH3 + String.valueOf(i), String.valueOf(tc.getWidth()));
            i++;
        }
        switchCompoSubject.deleteObserver();
    }

    private void setColumnWidth() {
        int i = 0;
        String columnWidth = ClientPropertyConstants.DEFAULT_LIST2_COLUMN_WIDTH;
        for (TableColumn tc : transitionTable1.getColumns()) {
            columnWidth = properties.getProperty(ClientPropertyConstants.PROP_KEY_LIST2_COLUMN_WIDTH1 + String.valueOf(i), ClientPropertyConstants.DEFAULT_LIST2_COLUMN_WIDTH);
            tc.setPrefWidth(Double.parseDouble(columnWidth));
            i++;
        }
        i = 0;
        for (TableColumn tc : transitionTable2.getColumns()) {
            columnWidth = properties.getProperty(ClientPropertyConstants.PROP_KEY_LIST2_COLUMN_WIDTH2 + String.valueOf(i), ClientPropertyConstants.DEFAULT_LIST2_COLUMN_WIDTH);
            tc.setPrefWidth(Double.parseDouble(columnWidth));
            i++;
        }
        i = 0;
        for (TableColumn tc : transitionTable3.getColumns()) {
            columnWidth = properties.getProperty(ClientPropertyConstants.PROP_KEY_LIST2_COLUMN_WIDTH3 + String.valueOf(i), ClientPropertyConstants.DEFAULT_LIST2_COLUMN_WIDTH);
            tc.setPrefWidth(Double.parseDouble(columnWidth));
            i++;
        }
    }

    private void setStyle() {
        date1.setStyle(fontStyleHeader);
        date2.setStyle(fontStyleHeader);
        date3.setStyle(fontStyleHeader);
        notStarted1.setStyle(fontStyleHeader);
        notStarted2.setStyle(fontStyleHeader);
        notStarted3.setStyle(fontStyleHeader);
        workmanship1.setStyle(fontStyleHeader);
        workmanship2.setStyle(fontStyleHeader);
        workmanship3.setStyle(fontStyleHeader);
        shipments1.setStyle(fontStyleHeader);
        shipments2.setStyle(fontStyleHeader);
        shipments3.setStyle(fontStyleHeader);
        notStartedTitle1.setStyle(fontStyleHeader);
        notStartedTitle2.setStyle(fontStyleHeader);
        notStartedTitle3.setStyle(fontStyleHeader);
        workmanshipTitle1.setStyle(fontStyleHeader);
        workmanshipTitle2.setStyle(fontStyleHeader);
        workmanshipTitle3.setStyle(fontStyleHeader);
        shipmentsTitle1.setStyle(fontStyleHeader);
        shipmentsTitle2.setStyle(fontStyleHeader);
        shipmentsTitle3.setStyle(fontStyleHeader);
        Separator11.setStyle(fontStyleHeader);
        Separator12.setStyle(fontStyleHeader);
        Separator21.setStyle(fontStyleHeader);
        Separator22.setStyle(fontStyleHeader);
        Separator31.setStyle(fontStyleHeader);
        Separator32.setStyle(fontStyleHeader);

        Double columnHight = Double.parseDouble(properties.getProperty(ClientPropertyConstants.PROP_KEY_LIST2_COLUMN_HIGHT, ClientPropertyConstants.DEFAULT_LIST2_COLUMN_HIGHT));
        transitionTable1.setFixedCellSize(columnHight);
        transitionTable2.setFixedCellSize(columnHight);
        transitionTable3.setFixedCellSize(columnHight);

        transitionTable1.getColumns().stream().forEach((tc) -> {
            tc.setStyle(borderStyle + fontStyleList + fontAlignmentList);
        });

        transitionTable2.getColumns().stream().forEach((tc) -> {
            tc.setStyle(borderStyle + fontStyleList + fontAlignmentList);
        });

        transitionTable3.getColumns().stream().forEach((tc) -> {
            tc.setStyle(borderStyle + fontStyleList + fontAlignmentList);
        });
    }

    @FXML
    public void OnClickedTable1(MouseEvent event) {
        list2MonitorCreator1.sort();
    }

    @FXML
    public void OnClickedTable2(MouseEvent event) {
        list2MonitorCreator2.sort();
    }

    @FXML
    public void OnClickedTable3(MouseEvent event) {
        list2MonitorCreator3.sort();
    }

    public void updateUnitList(Date startDate, Date endDate) {
        // 検索条件
        String[] unitTemplateIds = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST2_UNITTEMPLATE, "0").split(",");
        UnitSearchCondition condition = new UnitSearchCondition().fromDate(startDate).toDate(endDate).unitTemplateIdCollection(new ArrayList<>());
        for (String unitTemplateId : unitTemplateIds) {
            condition.getUnittemplateIdCollection().add(Long.parseLong(unitTemplateId));
        }

        // ユニットの取得.
        unitEntitys = RestAPI.basicSearchUnit(condition);
    }

    public void updateOrganizationList() {
        if (!properties.containsKey(ClientPropertyConstants.ADFACTORY_SERVICE_URI)) {
            properties.setProperty(ClientPropertyConstants.ADFACTORY_SERVICE_URI, "http://localhost:8080/adFactoryServer/rest");
        }
        organizationEntitys = new OrganizationInfoFacade(properties.getProperty(ClientPropertyConstants.ADFACTORY_SERVICE_URI, "http://localhost:8080/adFactoryServer/rest")).findAll();
    }

    public void updateDisplayedStatusList() {
        if (!properties.containsKey(ClientPropertyConstants.ADFACTORY_SERVICE_URI)) {
            properties.setProperty(ClientPropertyConstants.ADFACTORY_SERVICE_URI, "http://localhost:8080/adFactoryServer/rest");
        }
        displayedStatusInfoEntitys = new DisplayedStatusInfoFacade(properties.getProperty(ClientPropertyConstants.ADFACTORY_SERVICE_URI, "http://localhost:8080/adFactoryServer/rest")).findAll();
    }

    /**
     * ユニットの背景色設定
     *
     * @param status
     * @return
     */
    public String getBackgroundColor(StatusPatternEnum status) {
        return displayedStatusInfoEntitys.stream().filter(entity -> entity.getStatusName().equals(status)).findFirst().get().getBackColor();
    }

    /**
     * ユニットの背景色設定
     *
     * @param status
     * @return
     */
    public String getFontColor(StatusPatternEnum status) {
        return displayedStatusInfoEntitys.stream().filter(entity -> entity.getStatusName().equals(status)).findFirst().get().getFontColor();
    }

}
