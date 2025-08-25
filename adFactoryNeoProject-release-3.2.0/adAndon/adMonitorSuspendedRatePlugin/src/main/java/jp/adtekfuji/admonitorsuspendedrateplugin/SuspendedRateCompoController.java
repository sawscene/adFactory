/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorsuspendedrateplugin;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.TextUtils;
import adtekfuji.utility.ThreadUtils;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.DefectEntity;
import jp.adtekfuji.andon.property.MonitorSettingTP;
import jp.adtekfuji.andon.property.WorkSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 中断発生率フレームのコントローラー
 *
 * @author s-heya
 */
@AndonComponent(title = "中断発生率フレーム")
@FxComponent(id = "SuspendedRateCompo", fxmlPath = "/fxml/admonitorsuspendedrateplugin/SuspendedRateCompo.fxml")
public class SuspendedRateCompoController  implements Initializable, ArgumentDelivery, AdAndonComponentInterface {

    public static final String FONT_SIZE_TITLE = "fontSizeTitle";
    public static final String DEF_FONT_SIZE_TITLE = "60.0";

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
            synchronized (SuspendedRateCompoController.this) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    this.setGraphic(null);
                } else {
                    RowData rowData = this.getTableView().getItems().get(this.getIndex());

                    this.text.setText(String.valueOf(item));
                    this.text.setTextAlignment(TextAlignment.LEFT);
                    this.text.setStyle(String.format("-fx-font-size: %fpx;", fontSizeM));
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
     * パーセント型セル
     */
    class PercentCell extends TableCell<RowData, Double> {
        private final VBox vBox = new VBox();
        private final Text text = new Text();
        private final Pos alignment;

        public PercentCell(Pos alignment) {
            this.alignment = alignment;
            this.vBox.getChildren().add(this.text);
        }

        @Override
        protected void updateItem(Double item, boolean empty) {
            synchronized (SuspendedRateCompoController.this) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    this.setGraphic(null);
                } else {
                    RowData rowData = this.getTableView().getItems().get(this.getIndex());

                    this.text.setText(String.format("%.1f%%", item));
                    this.text.setTextAlignment(TextAlignment.LEFT);
                    this.text.setStyle(String.format("-fx-font-size: %fpx;", fontSizeM));
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
            synchronized (SuspendedRateCompoController.this) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    this.setGraphic(null);
                } else {
                    RowData rowData = this.getTableView().getItems().get(this.getIndex());

                    this.text.setText(String.valueOf(item));
                    this.text.setTextAlignment(TextAlignment.LEFT);
                    this.text.setStyle(String.format("-fx-font-size: %fpx;", fontSizeM));
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
    private double fontSizeM = Double.NaN;
    private double fontSizeTitle = Double.NaN;
    private double textWidth = Double.MAX_VALUE;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private VBox contentPane;
    @FXML
    private Label titleLabel;
    @FXML
    private TableView<RowData> tableView;
    @FXML
    private TableColumn<RowData, String> nameColumn;
    @FXML
    private TableColumn<RowData, Integer> suspendedNumColumn;
    @FXML
    private TableColumn<RowData, Double> suspendedRateColumn;

    /**
     * 中断発生率フレームを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        tableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        try {
            this.tableView.getStylesheets().addAll(getClass().getResource("/styles/hidden-tableview-headers.css").toExternalForm());

            // フォントサイズ
            if (!AdProperty.getProperties().containsKey(FONT_SIZE_TITLE)) {
                AdProperty.getProperties().setProperty(FONT_SIZE_TITLE, DEF_FONT_SIZE_TITLE);
            }
            this.fontSizeTitle = Double.parseDouble(AdProperty.getProperties().getProperty(FONT_SIZE_TITLE));

            if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_MIDDLE)) {
                AdProperty.getProperties().setProperty(Constants.FONT_SIZE_MIDDLE, Constants.DEF_FONT_SIZE_MIDDLE);
            }
            this.fontSizeM = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_MIDDLE));

            // モニタID
            this.monitorId = AndonLoginFacade.getMonitorId();

            if (0 != this.monitorId) {
                // 進捗モニタ設定情報
                AndonMonitorSettingFacade monitorSettingFacade = new AndonMonitorSettingFacade();
                this.setting = (MonitorSettingTP) monitorSettingFacade.getLineSetting(monitorId, MonitorSettingTP.class);

                this.titleLabel.setText(this.setting.getSuspendedTitle());
                this.titleLabel.setStyle(String.format("-fx-text-fill: Black; -fx-font-size: %fpx; -fx-background-color: LightGray;", fontSizeTitle));

                for (WorkSetting workSetting : setting.getSuspendedWorkCollection()) {
                    this.rows.add(new RowData(workSetting));
                    this.workIds.addAll(workSetting.getWorkIds());
                    this.textWidth = TextUtils.computeTextWidth(Font.font("Meiryo UI", this.fontSizeM), workSetting.getTitle(), textWidth);
                }

                this.rows.sort((a, b)-> a.getSetting().getOrder() - b.getSetting().getOrder());
            }

            //this.titleLabel.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            //    // タイトルのフォントサイズを高さに合わせる
            //    this.fontSizeL = Math.min(fontSizeL, newValue.doubleValue() * 0.9);
            //    this.titleLabel.setStyle(String.format("-fx-text-fill: Black; -fx-font-size: %fpx; -fx-background-color: LightGray;", this.fontSizeL));
            //});

            this.contentPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

                double width = this.contentPane.getWidth() - 28.0;

                // タイトルのフォントサイズを幅に合わせる
                this.titleLabel.setPrefWidth(width);

                Text helper = new Text(this.setting.getSuspendedTitle());
                helper.setFont(Font.font("Meiryo UI", fontSizeTitle));
                this.fontSizeTitle =  width >= helper.getBoundsInLocal().getWidth() ? this.fontSizeTitle : this.fontSizeTitle * (width / helper.getBoundsInLocal().getWidth() * 0.9);
                this.titleLabel.setStyle(String.format("-fx-text-fill: Black; -fx-font-size: %fpx; -fx-background-color: LightGray;", this.fontSizeTitle));

                // カラムの列幅、フォントサイズを調整
                double columnWidth = width / 10;
                this.fontSizeM = this.fontSizeM * (columnWidth * 3 / this.textWidth) * 0.9;

                this.nameColumn.setPrefWidth(columnWidth * 4);
                this.suspendedNumColumn.setPrefWidth(columnWidth * 3);
                this.suspendedRateColumn.setPrefWidth(columnWidth * 3);
                Platform.runLater(() -> this.tableView.refresh());

                for (Node node : this.tableView.lookupAll(".scroll-bar")) {
                    if (node instanceof ScrollBar) {
                        ScrollBar scrollBar = (ScrollBar) node;
                        if (scrollBar.getOrientation().equals(Orientation.VERTICAL)) {
                            scrollBar.heightProperty().addListener((ObservableValue<? extends Number> observable1, Number oldValue1, Number newValue1) -> {
                                // テーブルがすべて表示されるように、行の高さを調整
                                double height = newValue1.doubleValue() / this.tableView.getItems().size() - 1;
                                this.fontSizeM = Math.min(this.fontSizeM, height * 0.6);
                                this.tableView.setFixedCellSize(height);

                                try {
                                    ThreadUtils.waitFor(this, 100);
                                } catch (InterruptedException ex) {
                                    logger.fatal(ex, ex);
                                }

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
            this.suspendedNumColumn.setCellFactory(column -> new NumberCell(Pos.CENTER_RIGHT));
            this.suspendedRateColumn.setCellFactory(column -> new PercentCell(Pos.CENTER_RIGHT));

            Platform.runLater(() -> this.refresh());

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

            switch (actual.getWorkKanbanStatus()) {
                case COMPLETION:
                    for (RowData data : this.rows) {
                        if (data.getSetting().getWorkIds().contains(actual.getWorkId())) {
                            data.setActualNum(data.getActualNum() + 1);
                        }
                    }
                    break;
                case SUSPEND:
                    for (RowData data : this.rows) {
                        if (data.getSetting().getWorkIds().contains(actual.getWorkId())) {
                            data.suspendedNumProperty().set(data.suspendedNumProperty().get() + 1);
                        }
                    }
                    break;
                default:
                    break;
            }

            this.updateData();
        } else if (msg instanceof ResetCommand) {
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
                    synchronized (SuspendedRateCompoController.this) {
                        List<DefectEntity> defects = monitorFacade.getDailyWorkDefect(new ArrayList<>(workIds));

                        for (RowData data : rows) {
                            data.suspendedNumProperty().set(0);
                            data.setActualNum(0);
                        }

                        for (DefectEntity defect : defects) {
                            for (RowData data : rows) {
                                if (data.getSetting().getWorkIds().contains(defect.getId())) {
                                    data.suspendedNumProperty().set(data.suspendedNumProperty().get() + defect.getDefectCount().intValue());
                                    data.setActualNum(data.getActualNum() + defect.getProdCount().intValue());
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
    private void updateData() {
        for (RowData data : this.rows) {
            double rate = 0.0;

            if (0 < data.getActualNum()) {
                // 中断発生率
                rate = (double) data.suspendedNumProperty().get() / (double) data.getActualNum() * 100.0;

                if (this.setting.getSuspendedWarnThreshold() <= rate) {
                    // 警告表示
                    data.setFontColor(this.setting.getWarningFontColor());
                    data.setBackColor(this.setting.getWarningBackColor());
                } else if (this.setting.getSuspendedAttenThreshold() <= rate) {
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

            data.suspendedRateProperty().set(rate);
        }

        Platform.runLater(() -> this.tableView.refresh());
    }
}
