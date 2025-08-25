/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.controls;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;

/**
 * 開始日と終了日を選択する
 *
 * @author fu-kato
 */
public class FromToPicker extends FlowPane implements Initializable {

    private final ResourceBundle rb = LocaleUtils.getBundle("locale");
    protected final org.apache.logging.log4j.Logger logger = LogManager.getLogger();
    private static final String DISABLE_DATE_STYLE = "-fx-background-color: lightgray;";// カレンダーで選択不可な日のスタイル

    // プロパティ保存用定数
    public static String PICKER_ENABLE_DATE = "fromto.enable.date";
    public static String PICKER_FROM_DATE = "fromto.from.date";
    public static String PICKER_TO_DATE = "fromto.to.date";

    @FXML
    CheckBox enableDateSelect;

    @FXML
    DatePicker fromDatePicker;

    @FXML
    DatePicker toDatePicker;

    public FromToPicker() {
        try {
            URL url = getClass().getResource("/fxml/compo/from_to_picker.fxml");
            FXMLLoader loader = new FXMLLoader(url, rb);
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.fromDatePicker.disableProperty().bind(Bindings.not(enableDateSelect.selectedProperty()));
        this.toDatePicker.disableProperty().bind(Bindings.not(enableDateSelect.selectedProperty()));

        // 開始日には、終了日より後の日は選択できない。
        fromDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (Objects.isNull(date) || Objects.isNull(toDatePicker.getValue())) {
                            return;
                        }

                        if (date.isAfter(toDatePicker.getValue())) {
                            setDisable(true);
                            setStyle(DISABLE_DATE_STYLE);
                        }
                    }
                };
            }
        });

        // 終了日には、開始日より前の日は選択できない。
        toDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (Objects.isNull(date) || Objects.isNull(fromDatePicker.getValue())) {
                            return;
                        }

                        if (date.isBefore(fromDatePicker.getValue())) {
                            setDisable(true);
                            setStyle(DISABLE_DATE_STYLE);
                        }
                    }
                };
            }
        });
    }

    public LocalDate getFrom() {
        return fromDatePicker.getValue();
    }

    public LocalDate getTo() {
        return toDatePicker.getValue();
    }

    public LocalDateTime getFromStart() {
        return LocalDateTime.of(getFrom(), LocalTime.of(0, 0, 0));
    }

    public LocalDateTime getToEnd() {
        return LocalDateTime.of(getTo(), LocalTime.of(23, 59, 59));
    }

    /**
     * チェックが無効、またはどちらの日付も指定されていない場合すべての範囲を対象とする。
     *
     * @return
     */
    public boolean isSelectAll() {
        return !enableDateSelect.isSelected()
                || (Objects.isNull(fromDatePicker.getValue()) && Objects.isNull(toDatePicker.getValue()));
    }

    /**
     * 入力した時間が、指定した開始日の00:00:00から終了日の23:59:59の範囲に存在するか調べる。<br>
     * 開始日の指定がない場合、終了日の23:59:59以前全てを対象とする。<br>
     * 終了日の指定がない場合、開始日の00:00:00以降すべてを対象とする。
     *
     * @param current
     * @return
     */
    public boolean isBetween(LocalDateTime current) {
        boolean isValidStart = Objects.isNull(getFrom()) || current.isEqual(getFromStart()) || current.isAfter(getFromStart());
        boolean isValidEnd = Objects.isNull(getTo()) || current.isEqual(getToEnd()) || current.isBefore(getToEnd());

        return isValidStart && isValidEnd;
    }

    public void load(Properties prop) {
        enableDateSelect.setSelected(Boolean.valueOf(prop.getProperty(PICKER_ENABLE_DATE, "false")));

        String fromValue = prop.getProperty(PICKER_FROM_DATE, "");
        if (!StringUtils.isEmpty(fromValue)) {
            LocalDate localDate = LocalDate.parse(fromValue, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            fromDatePicker.setValue(localDate);
        }

        String toValue = prop.getProperty(PICKER_TO_DATE, "");
        if (!StringUtils.isEmpty(toValue)) {
            LocalDate localDate = LocalDate.parse(toValue, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            toDatePicker.setValue(localDate);
        }
    }

    public void save(Properties prop) {
        prop.setProperty(PICKER_ENABLE_DATE, String.valueOf(this.enableDateSelect.isSelected()));
        prop.setProperty(PICKER_FROM_DATE,
                Objects.isNull(fromDatePicker.getValue()) ? "" : DateTimeFormatter.ofPattern("yyyy/MM/dd").format(fromDatePicker.getValue()));
        prop.setProperty(PICKER_TO_DATE,
                Objects.isNull(toDatePicker.getValue()) ? "" : DateTimeFormatter.ofPattern("yyyy/MM/dd").format(toDatePicker.getValue()));
    }
}
