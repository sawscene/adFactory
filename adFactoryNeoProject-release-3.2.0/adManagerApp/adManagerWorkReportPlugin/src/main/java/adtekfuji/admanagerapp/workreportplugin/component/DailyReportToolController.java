/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.component;

import adtekfuji.admanagerapp.workreportplugin.common.WorkReportConfig;
import adtekfuji.admanagerapp.workreportplugin.entity.DailyReportToolSetting;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業日報ツール
 *
 * @author nar-nakamura
 */
@FxComponent(id = "DailyReportTool", fxmlPath = "/fxml/admanagerworkreportplugin/daily_report_tool.fxml")
public class DailyReportToolController implements Initializable, ArgumentDelivery {

    private static final Logger logger = LogManager.getLogger();
    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private static final String COLOR_TRANSPARENT = "transparent";// 透明色

    private DailyReportToolSetting toolSetting = new DailyReportToolSetting();// 作業日報ツール設定

    @FXML
    private TextField referenceTimeField;// 基準時間入力欄
    @FXML
    private Pane workTimeBgPane;// 作業時間背景
    @FXML
    private Label workTimeLabel;// 作業時間ラベル
    @FXML
    private Label workMinLabel;// 作業時間(分)ラベル
    @FXML
    private Label differenceTimeLabel;// 差異ラベル
    @FXML
    private Label differenceMinLabel;// 差異(分)ラベル

    /**
     * コンストラクタ
     */
    public DailyReportToolController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // 基準時間入力欄の入力制限
        TextFormatter<String> timeFormatter = new TextFormatter<>(change -> {
            Pattern timePattern = Pattern.compile("^([0-9]{1,6}):([0-9]|[0-5][0-9])$");
            if (!timePattern.matcher(change.getControlNewText()).matches()) {
                return null;
            }
            return change;
        });
        this.referenceTimeField.setTextFormatter(timeFormatter);
    }

    @Override
    public void setArgument(Object argument) {
        try {
            if (argument instanceof DailyReportToolSetting) {
                this.toolSetting = (DailyReportToolSetting) argument;

                String workTimeBgStyle = new StringBuilder("-fx-background-color: ")
                        .append(StringUtils.isEmpty(this.toolSetting.getWorkTimeBgColor()) ? COLOR_TRANSPARENT : this.toolSetting.getWorkTimeBgColor())
                        .append("; -fx-text-background-color: black;")
                        .toString();
                this.workTimeBgPane.setStyle(workTimeBgStyle);

                // 基準時間
                String referenceTime = WorkReportConfig.getWorkReportToolReferenceTime();
                this.referenceTimeField.setText(referenceTime);

                // 作業時間
                if (null == this.toolSetting.getWorkMin()) {
                    this.toolSetting.setWorkMin(0);
                }

                String workTime = this.getTimeString(this.toolSetting.getWorkMin());
                String workMin = this.getMinuteString(this.toolSetting.getWorkMin());

                this.workTimeLabel.setText(workTime);
                this.workMinLabel.setText(workMin);

                // 基準時間入力欄が変更されたら差異の表示を更新する。
                this.referenceTimeField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    // oldValueがnullの場合は初期更新のため、何もしない
                    if (Objects.isNull(oldValue)) {
                        return;
                    }

                    this.updateView();
                });

                this.updateView();
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 表示を更新する。
     */
    private void updateView() {
        try {
            // 差異
            int referenceMin = this.getMinute(this.referenceTimeField.getText());
            int diff = referenceMin - this.toolSetting.getWorkMin();

            String differenceTime = this.getTimeString(diff);
            String differenceMin = this.getMinuteString(diff);

            this.differenceTimeLabel.setText(differenceTime);
            this.differenceMinLabel.setText(differenceMin);

            WorkReportConfig.setWorkReportToolReferenceTime(this.referenceTimeField.getText());

        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 時間(H:mm)文字列を取得する。
     *
     * @param minute 時間(分)
     * @return 時間(H:mm)文字列
     */
    private String getTimeString(int minute) {
        int min = Math.abs(minute % 60);
        int hour = Math.abs(minute / 60);

        String minus = "";
        if (minute < 0) {
            minus = "-";
        }

        return String.format("%s%d:%02d", minus, hour, min);
    }

    /**
     * 時間(分)文字列を取得する。
     *
     * @param minute 時間(分)
     * @return 時間(分)文字列
     */
    private String getMinuteString(int minute) {
        return new StringBuilder("(")
                .append(String.valueOf(minute))
                .append(" ")
                .append(LocaleUtils.getString("key.Minute"))
                .append(")")
                .toString();
    }

    /**
     * 時間(H:mm)文字列から、時間(分)を取得する。
     *
     * @param time 時間(H:mm)文字列
     * @return 時間(分)
     */
    private int getMinute(String time) {
        try {
            Pattern pattern = Pattern.compile("([0-9].*):([0-9]|[0-5][0-9])$");
            Matcher matcher = pattern.matcher(time);
            if (!matcher.matches()) {
                return 0;
            }

            String hour = matcher.group(1);
            String min = matcher.group(2);

            return (Integer.valueOf(hour) * 60) + Integer.valueOf(min);
        } catch (Exception ex) {
            return 0;
        }
    }
}
