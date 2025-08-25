/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorworkstatusplugin;

import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import jp.adtekfuji.adFactory.adinterface.command.CallingNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.adinterface.command.TimerCommand;
import jp.adtekfuji.adFactory.adinterface.command.WorkReportCommand;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import jp.adtekfuji.andon.property.WorkSetting;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程進捗フレームのコントローラー
 *
 * @author s-heya
 */
@AndonComponent(title = "工程進捗フレーム")
@FxComponent(id = "WorkStatusCompo", fxmlPath = "/fxml/admonitorworkstatusplugin/WorkStatusCompo.fxml")
public class WorkStatusCompoController  implements Initializable, ArgumentDelivery, AdAndonComponentInterface {

    /**
     * 工程名セル
     */
    class NameTableCell extends TableCell<WorkStatus, String> {
        private final VBox vBox = new VBox();
        private final Text text = new Text();

        public NameTableCell() {
            this.vBox.getChildren().add(this.text);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            synchronized (WorkStatusCompoController.this) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    this.setGraphic(null);
                } else {
                    WorkStatus workStatus = this.getTableView().getItems().get(this.getIndex());
                    DisplayedStatusInfoEntity displayedStatus = workStatus.getEquipmentStatus();

                    this.text.setText(item);
                    this.text.setTextAlignment(TextAlignment.LEFT);
                    this.text.setStyle(String.format("-fx-font-size: %fpx;", fontSize));

                    if (Objects.nonNull(setting.getBreaktimes()) && BreaktimeUtil.isBreaktime(setting.getBreaktimes(), new Date())) {
                        Optional<DisplayedStatusInfoEntity> optional = displaySetting.values().stream().filter(o -> Objects.equals(o.getStatusName(), StatusPatternEnum.BREAK_TIME)).findFirst();
                        displayedStatus = optional.isPresent() ? optional.get() : null;
                    }

                    if (workStatus.isCalled()) {
                        // 呼出中
                        Optional<DisplayedStatusInfoEntity> optional = displaySetting.values().stream().filter(o -> Objects.equals(o.getStatusName(), StatusPatternEnum.CALLING)).findFirst();
                        displayedStatus = optional.isPresent() ? optional.get() : null;
                    }

                    if (Objects.nonNull(displayedStatus)) {
                        this.vBox.setStyle(String.format("-fx-padding: 4; -fx-alignment: center-left; -fx-background-color: %s;", displayedStatus.getBackColor()));
                        this.text.setFill(Color.web(displayedStatus.getFontColor()));
                    } else {
                        this.vBox.setStyle("-fx-padding: 4; -fx-alignment: center-left; -fx-background-color: transparent;");
                        this.text.setFill(Color.WHITE);
                    }

                    this.setGraphic(this.vBox);
                }
            }
        }
    };

    /**
     * 作業進捗セル
     */
    class WorkTableCell extends TableCell<WorkStatus, Integer> {
        private final VBox pane = new VBox();
        private final Text text = new Text();

        public WorkTableCell() {
            this.pane.getChildren().add(this.text);
        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                this.setGraphic(null);
            } else {
                WorkStatus workStatus = this.getTableView().getItems().get(this.getIndex());

                DisplayedStatusInfoEntity displayedStatus = null;
                if (displaySetting.containsKey(workStatus.getWorkStatusId())) {
                    displayedStatus = displaySetting.get(workStatus.getWorkStatusId());
                }

                this.text.setText(MonitorTools.formatTaktTime(item));
                this.text.setTextAlignment(TextAlignment.RIGHT);
                this.text.setStyle(String.format("-fx-font-size: %fpx;", fontSize));

                if (Objects.nonNull(displayedStatus)) {
                    this.pane.setStyle(String.format("-fx-padding: 4; -fx-alignment: center-right; -fx-background-color: %s;", displayedStatus.getBackColor()));
                    this.text.setFill(Color.web(displayedStatus.getFontColor()));
                } else {
                    this.pane.setStyle("-fx-padding: 4; -fx-alignment: center-right; -fx-background-color: transparent;");
                    this.text.setFill(Color.WHITE);
                }

                this.setGraphic(this.pane);
            }
        }
    };

    /**
     * 当日進捗セル
     */
    class TodayTableCell extends TableCell<WorkStatus, Integer> {
        private final VBox vBox = new VBox();
        private final Text text = new Text();

        public TodayTableCell() {
            this.vBox.getChildren().add(this.text);
        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                this.setGraphic(null);
            } else {
                WorkStatus workStatus = this.getTableView().getItems().get(this.getIndex());

                DisplayedStatusInfoEntity displayedStatus = null;
                if (displaySetting.containsKey(workStatus.getTodayStatusId())) {
                    displayedStatus = displaySetting.get(workStatus.getTodayStatusId());
                }

                this.text.setText(MonitorTools.formatTaktTime(item));
                this.text.setTextAlignment(TextAlignment.RIGHT);
                this.text.setStyle(String.format("-fx-font-size: %fpx;", fontSize));

                if (Objects.nonNull(displayedStatus)) {
                    this.vBox.setStyle(String.format("-fx-padding: 4; -fx-alignment: center-right; -fx-background-color: %s;", displayedStatus.getBackColor()));
                    this.text.setFill(Color.web(displayedStatus.getFontColor()));
                } else {
                    this.vBox.setStyle("-fx-padding: 4; -fx-alignment: center-right; -fx-background-color: transparent;");
                    this.text.setFill(Color.WHITE);
                }

                this.setGraphic(this.vBox);
            }
        }
    };

    // CSSプロパティ名
    public static final String STYLE_STATUS_BACKGROUND_COLOR = "status-background-color:";

    private static final Logger logger = LogManager.getLogger();
    private final ObservableList<WorkStatus> works = FXCollections.observableArrayList();
    private Long monitorId;
    private AndonMonitorLineProductSetting setting;
    private Map<Long, DisplayedStatusInfoEntity> displaySetting;
    private double fontSize = Double.NaN;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<WorkStatus> tableView;
    @FXML
    private TableColumn<WorkStatus, String> nameColumn;
    @FXML
    private TableColumn<WorkStatus, Integer> workColumn;
    @FXML
    private TableColumn<WorkStatus, Integer> todayColumn;

    /**
     * 工程進捗フレームを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        tableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        try {
            if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_MIDDLE)) {
                AdProperty.getProperties().setProperty(Constants.FONT_SIZE_MIDDLE, Constants.DEF_FONT_SIZE_MIDDLE);
            }
            this.fontSize = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_MIDDLE));

            this.monitorId = AndonLoginFacade.getMonitorId();

            if (0 != this.monitorId) {
                this.setting = (AndonMonitorLineProductSetting) new AndonMonitorSettingFacade()
                        .getLineSetting(this.monitorId, AndonMonitorLineProductSetting.class);
                List<DisplayedStatusInfoEntity> list =  new DisplayedStatusInfoFacade().findAll();
                this.displaySetting = list.stream().collect(Collectors.toMap(DisplayedStatusInfoEntity::getStatusId, d -> d));

                if (this.setting.isReportByWork()) {
                    for (WorkSetting workSetting : setting.getWorkCollection()) {
                        this.works.add(new WorkStatus(workSetting.getOrder(), workSetting.getTitle(), workSetting.getWorkIds()));
                    }
                } else {
                    for (WorkEquipmentSetting equipmentSetting : setting.getWorkEquipmentCollection()) {
                        this.works.add(new WorkStatus(equipmentSetting.getOrder(), equipmentSetting.getTitle(), equipmentSetting.getEquipmentIds()));
                    }
                }
            }

            this.rootPane.getStylesheets().clear();
            this.rootPane.getStylesheets().add("/styles/WorkStatusCompo.css");
            this.rootPane.widthProperty().addListener((observable, oldValue, newValue) -> {

                double width = (newValue.doubleValue() - 28.0) / 3;

                double columnWidth1 = Double.valueOf(AdProperty.getProperties().getProperty("columnWidth1", String.valueOf(width)));
                double columnWidth2 = Double.valueOf(AdProperty.getProperties().getProperty("columnWidth2", String.valueOf(width)));
                double columnWidth3 = Double.valueOf(AdProperty.getProperties().getProperty("columnWidth3", String.valueOf(width)));

                this.nameColumn.setPrefWidth(columnWidth1);
                this.workColumn.setPrefWidth(columnWidth2);
                this.todayColumn.setPrefWidth(columnWidth3);
                Platform.runLater(() -> this.tableView.refresh());

                // カラムの幅からフォントサイズを算出
                //double textWidth = TextUtils.computeTextWidth(Font.font("Meiryo UI", this.fontSize), "-00:00:00", Double.MAX_VALUE);
                //this.fontSize = this.fontSize * (width / textWidth) * 0.9;

                for (Node node : this.tableView.lookupAll(".scroll-bar")) {
                    if (node instanceof ScrollBar) {
                        ScrollBar scrollBar = (ScrollBar) node;
                        if (scrollBar.getOrientation().equals(Orientation.VERTICAL)) {
                            scrollBar.heightProperty().addListener((observable1, oldValue1, newValue1) -> {
                                // 行の高さ
                                double size = newValue1.doubleValue() / this.tableView.getItems().size() - 1;
                                // 行の高さからフォントサイズを算出
                                this.fontSize = Math.min(this.fontSize, size * 0.7);
                                this.tableView.setFixedCellSize(size);
                                Platform.runLater(() -> this.tableView.refresh());
                            });
                            break;
                        }
                    }
                }
            });

            this.tableView.setItems(this.works);
            this.tableView.setSelectionModel(null);

            this.nameColumn.setCellFactory(column -> new NameTableCell());
            this.workColumn.setCellFactory(column -> new WorkTableCell());
            this.todayColumn.setCellFactory(column -> new TodayTableCell());

            this.nameColumn.widthProperty().addListener((observable, oldValue, newValue) -> {
                 AdProperty.getProperties().setProperty("columnWidth1", String.valueOf(newValue.doubleValue()));
            });
            this.workColumn.widthProperty().addListener((observable, oldValue, newValue) -> {
                 AdProperty.getProperties().setProperty("columnWidth2", String.valueOf(newValue.doubleValue()));
            });
            this.todayColumn.widthProperty().addListener((observable, oldValue, newValue) -> {
                 AdProperty.getProperties().setProperty("columnWidth3", String.valueOf(newValue.doubleValue()));
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * パラメータを設定する。
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
    }

   /**
     * 表示を更新する。
     *
     * @param msg
     */
    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof WorkReportCommand) {
            WorkReportCommand command = (WorkReportCommand) msg;

            if (!StringUtils.isEmpty(this.setting.getModelName())
                    && !StringUtils.equals(this.setting.getModelName(), command.getModelName())) {
                // 作業報告コマンドのモデル名が一致しない場合は無視
                return;
            }
            
            // モデル名が設定されていないか、作業報告コマンドのモデル名が一致する場合は更新
            DisplayedStatusInfoEntity displayedStatus = null;
            if (displaySetting.containsKey(command.getEquipmentStatusId())) {
                displayedStatus = displaySetting.get(command.getEquipmentStatusId());
            }

            if (this.setting.isReportByWork()) {
                for (WorkStatus workStatus : this.works) {
                    workStatus.update(command.getWorkId(), command, this.displaySetting);
                }

            } else {
                for (WorkStatus workStatus : this.works) {
                    workStatus.update(command.getEquipmentId(), command, this.displaySetting);
                }
            }

            Platform.runLater(() -> this.tableView.refresh());

        } else if (msg instanceof CallingNoticeCommand) {
            CallingNoticeCommand command = (CallingNoticeCommand) msg;
            if (this.setting.isReportByWork()) {
                if (Objects.nonNull(command.getWorkId())) {
                    for (WorkStatus workStatus : this.works) {
                        if (workStatus.contains(command.getWorkId())) {
                            workStatus.setCalled(command.getIsCall());
                        }
                    }
                }

            } else {
                for (WorkStatus workStatus : this.works) {
                    if (workStatus.contains(command.getEquipmentId())) {
                        workStatus.setCalled(command.getIsCall());
                    }
                }
            }

        } else if (msg instanceof TimerCommand) {
            Platform.runLater(() -> this.tableView.refresh());

        } else if (msg instanceof ResetCommand) {
            this.works.clear();
            if (this.setting.isReportByWork()) {
                for (WorkSetting workSetting : setting.getWorkCollection()) {
                    this.works.add(new WorkStatus(workSetting.getOrder(), workSetting.getTitle(), workSetting.getWorkIds()));
                }
            } else {
                for (WorkEquipmentSetting equipmentSetting : setting.getWorkEquipmentCollection()) {
                    this.works.add(new WorkStatus(equipmentSetting.getOrder(), equipmentSetting.getTitle(), equipmentSetting.getEquipmentIds()));
                }
            }
        }
    }

    /**
     * フレームを終了する。
     */
    @Override
    public void exitComponent() {
        logger.info("exitComponent");
    }
}
