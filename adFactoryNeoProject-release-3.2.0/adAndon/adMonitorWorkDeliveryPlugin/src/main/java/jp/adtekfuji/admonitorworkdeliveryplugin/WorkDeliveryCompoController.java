/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorworkdeliveryplugin;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.TextUtils;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.adinterface.command.TimerCommand;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.ProductivityEntity;
import jp.adtekfuji.andon.property.MonitorSettingTP;
import jp.adtekfuji.andon.property.WorkSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * グループ進捗フレームのコントローラー
 *
 * @author s-heya
 */
@AndonComponent(title = "グループ進捗フレーム")
@FxComponent(id = "WorkDeliveryCompo", fxmlPath = "/fxml/admonitorworkdeliveryplugin/WorkDeliveryCompo.fxml")
public class WorkDeliveryCompoController  implements Initializable, ArgumentDelivery, AdAndonComponentInterface {

    /**
     * 数値型セル
     */
    class NumberCell extends TableCell<RowData, Integer> {
        private final VBox vBox = new VBox();
        private final Text text = new Text();
        private final Pos alignment;

        public NumberCell(Pos alignment) {
            this.alignment = alignment;
            this.vBox.getChildren().add(this.text);
        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            synchronized (WorkDeliveryCompoController.this) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    this.setGraphic(null);
                } else {
                    RowData rowData = this.getTableView().getItems().get(this.getIndex());

                    this.text.setText(String.valueOf(item));
                    this.text.setTextAlignment(TextAlignment.LEFT);
                    this.text.setStyle(String.format("-fx-font-size: %fpx;", fontSize));
                    this.text.setFill(rowData.getFontColor());

                    Color color = rowData.getBackColor();
                    String hex = String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
                    this.vBox.setStyle(String.format("-fx-padding: 4 8 4 8; -fx-background-color: %s;", hex));
                    this.vBox.setAlignment(alignment);

                    this.setGraphic(this.vBox);
                }
            }
        }
    };

    /**
     * テキスト型セル
     */
    class TextCell extends TableCell<RowData, String> {
        private final VBox vBox = new VBox();
        private final Text text = new Text();
        private final Pos alignment;

        public TextCell(Pos alignment) {
            this.alignment = alignment;
            this.vBox.getChildren().add(this.text);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            synchronized (WorkDeliveryCompoController.this) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    this.setGraphic(null);
                } else {
                    RowData rowData = this.getTableView().getItems().get(this.getIndex());

                    this.text.setText(String.valueOf(item));
                    this.text.setTextAlignment(TextAlignment.LEFT);
                    this.text.setStyle(String.format("-fx-font-size: %fpx;", fontSize));
                    this.text.setFill(rowData.getFontColor());

                    Color color = rowData.getBackColor();
                    String hex = String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
                    this.vBox.setStyle(String.format("-fx-padding: 4 8 4 8; -fx-background-color: %s;", hex));
                    this.vBox.setAlignment(alignment);

                    this.setGraphic(this.vBox);
                }
            }
        }
    };

    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade monitorFacade = new AndonLineMonitorFacade();
    private final ObservableList<RowData> rows = FXCollections.observableArrayList();
    private final Set<Long> workIds = new HashSet<>();
    private Long monitorId;
    private MonitorSettingTP setting;
    private double fontSize = Double.NaN;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<RowData> tableView;
    @FXML
    private TableColumn<RowData, String> nameColumn;
    @FXML
    private TableColumn<RowData, Integer> planColumn;
    @FXML
    private TableColumn<RowData, Integer> actualColumn;
    @FXML
    private TableColumn<RowData, Integer> diffColumn;
    @FXML
    private TableColumn<RowData, String> progColumn;

    /**
     * グループ進捗フレームを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        tableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        try {
            // フォントサイズ
            if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_MIDDLE)) {
                AdProperty.getProperties().setProperty(Constants.FONT_SIZE_MIDDLE, Constants.DEF_FONT_SIZE_MIDDLE);
            }
            this.fontSize = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_MIDDLE));

            // モニタID
            this.monitorId = AndonLoginFacade.getMonitorId();

            if (0 != this.monitorId) {
                // 進捗モニタ設定情報
                AndonMonitorSettingFacade monitorSettingFacade = new AndonMonitorSettingFacade();
                this.setting = (MonitorSettingTP) monitorSettingFacade.getLineSetting(monitorId, MonitorSettingTP.class);

                LocalDate today = LocalDate.now();
                ZoneId zoneId = ZoneId.systemDefault();

                for (WorkSetting workSetting : setting.getGroupWorkCollection()) {
                    Date startWorkTime = Date.from(workSetting.getStartWorkTime().atDate(today).atZone(zoneId).toInstant());
                    Date endWorkTime = Date.from(workSetting.getEndWorkTime().atDate(today).atZone(zoneId).toInstant());
                    List<BreakTimeInfoEntity> breaktimes = BreaktimeUtil.getAppropriateBreaktimes(this.setting.getBreaktimes(), startWorkTime, endWorkTime);

                    // 1日の作業時間
                    long breakTime = 0;
                    for (BreakTimeInfoEntity breaktime : breaktimes) {
                        LocalTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(breaktime.getStarttime().getTime()), zoneId).toLocalTime();
                        LocalTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(breaktime.getEndtime().getTime()), zoneId).toLocalTime();
                        breakTime += ChronoUnit.MILLIS.between(start, end);
                    }
                    Long workTime = ChronoUnit.MILLIS.between(workSetting.getStartWorkTime(), workSetting.getEndWorkTime()) - breakTime;

                    this.rows.add(new RowData(workSetting, workTime));
                    this.workIds.addAll(workSetting.getWorkIds());
                }

                this.rows.sort((a, b)-> a.getSetting().getOrder() - b.getSetting().getOrder());
            }

            this.rootPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                // カラムの列幅、フォントサイズを調整
                double width = (this.rootPane.getWidth() - 28.0) / 7;
                double textWidth = TextUtils.computeTextWidth(Font.font("Meiryo UI", this.fontSize), "- 00h 00m 00s", Double.MAX_VALUE);
                this.fontSize = this.fontSize * (width * 2 / textWidth) * 0.9;

                this.nameColumn.setPrefWidth(width * 2);
                this.planColumn.setPrefWidth(width);
                this.actualColumn.setPrefWidth(width);
                this.diffColumn.setPrefWidth(width);
                this.progColumn.setPrefWidth(width * 2);
                Platform.runLater(() -> this.tableView.refresh());

                for (Node node : this.tableView.lookupAll(".scroll-bar")) {
                    if (node instanceof ScrollBar) {
                        ScrollBar scrollBar = (ScrollBar) node;
                        if (scrollBar.getOrientation().equals(Orientation.VERTICAL)) {
                            scrollBar.heightProperty().addListener((ObservableValue<? extends Number> observable1, Number oldValue1, Number newValue1) -> {
                                // テーブルがすべて表示されるように、行の高さを調整
                                double size = newValue1.doubleValue() / this.tableView.getItems().size() - 1;
                                this.fontSize = Math.min(this.fontSize, size * 0.6);
                                this.tableView.setFixedCellSize(size);
                                Platform.runLater(() -> this.tableView.refresh());
                            });
                            break;
                        }
                    }
                }
            });

            this.tableView.setItems(this.rows);
            this.tableView.setSelectionModel(null);

            this.nameColumn.setCellFactory(column -> new TextCell(Pos.CENTER_LEFT));
            this.planColumn.setCellFactory(column -> new NumberCell(Pos.CENTER_RIGHT));
            this.actualColumn.setCellFactory(column -> new NumberCell(Pos.CENTER_RIGHT));
            this.diffColumn.setCellFactory(column -> new NumberCell(Pos.CENTER_RIGHT));
            this.progColumn.setCellFactory(column -> new TextCell(Pos.CENTER_RIGHT));

            this.refresh();

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
        if (msg instanceof ActualNoticeCommand) {
            ActualNoticeCommand actual = (ActualNoticeCommand) msg;

            // モデル名
            if (!StringUtils.like(actual.getModelName(), this.setting.getModelName())) {
                return;
            }

            if (actual.getWorkKanbanStatus() == KanbanStatusEnum.COMPLETION) {
                for (RowData data : this.rows) {
                    if (data.getSetting().getWorkIds().contains(actual.getWorkId())) {
                        data.actualNumProperty().set(data.actualNumProperty().get() + 1);
                    }
                }

                this.updateData();
            }

        } else if (msg instanceof TimerCommand) {
            this.updateData();
        } else if (msg instanceof ResetCommand) {
            // リカバリー
            this.refresh();
        }
    }

    /**
     * フレームを終了する。
     */
    @Override
    public void exitComponent() {
        logger.info("exitComponent");
    }

    /**
     * フレームを更新する。
     */
    private void refresh() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (WorkDeliveryCompoController.this) {
                        List<ProductivityEntity> productivities = monitorFacade.getDailyWorkProductivity(new ArrayList<>(workIds));

                        for (RowData data : rows) {
                            data.actualNumProperty().set(0);
                        }

                        for (ProductivityEntity productivity : productivities) {
                            for (RowData data : rows) {
                                if (data.getSetting().getWorkIds().contains(productivity.getId())) {
                                    data.actualNumProperty().set(data.actualNumProperty().get() + productivity.getProdCount().intValue());
                                }
                            }
                        }

                        updateData();
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * Rowデータを更新する。
     */
    private synchronized void updateData() {
        LocalDate today = LocalDate.now();
        Date now = new Date();

        for (RowData data : this.rows) {
            if (0 >= data.getTaktTime()) {
                continue;
            }

            WorkSetting workSetting = data.getSetting();

            // 現在までの作業時間
            Date startWorkTime = Date.from(workSetting.getStartWorkTime().atDate(today).atZone(ZoneId.systemDefault()).toInstant());
            long currentTime = BreaktimeUtil.getDiffTime(this.setting.getBreaktimes(), startWorkTime, now);

            // 現在の目標数
            long currentGoal = Math.min(currentTime / data.getTaktTime(), workSetting.getPlanNum());
            currentGoal = (0 < currentGoal) ? currentGoal : 0;

            // 差異
            int diff = data.actualNumProperty().get() - (int) currentGoal;

            if (0 < currentGoal) {
                if (diff <= -(this.setting.getGroupWarnThreshold())) {
                    // 警告表示
                    data.setFontColor(this.setting.getWarningFontColor());
                    data.setBackColor(this.setting.getWarningBackColor());
                } else if (diff <= -(this.setting.getGroupAttenThreshold())) {
                    // 注意表示
                    data.setFontColor(this.setting.getCautionFontColor());
                    data.setBackColor(this.setting.getCautionBackColor());
                } else {
                    data.setFontColor(Color.WHITE);
                    data.setBackColor(Color.BLACK);
                }
            } else {
                data.setFontColor(Color.WHITE);
                data.setBackColor(Color.BLACK);
            }

            data.currentGoalProperty().set((int) currentGoal);
            data.diffProperty().set(diff);
            data.progressProperty().set(DateUtils.formatTaktTime(diff * data.getTaktTime()));
        }

        Platform.runLater(() -> this.tableView.refresh());
    }
}
