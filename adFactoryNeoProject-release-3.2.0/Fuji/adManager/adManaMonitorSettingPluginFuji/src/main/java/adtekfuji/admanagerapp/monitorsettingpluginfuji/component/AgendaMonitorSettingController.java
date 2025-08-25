/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingpluginfuji.component;

import adtekfuji.admanagerapp.monitorsettingpluginfuji.common.BreakTimeIdData;
import adtekfuji.admanagerapp.monitorsettingpluginfuji.common.BreakTimeRecordFactory;
import adtekfuji.admanagerapp.monitorsettingpluginfuji.dialog.KanbanSelectDialog;
import adtekfuji.admanagerapp.monitorsettingpluginfuji.dialog.OrganizationSelectDialog;
import adtekfuji.admanagerapp.monitorsettingpluginfuji.dialog.LineSelectDialog;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import adtekfuji.locale.LocaleUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.andon.enumerate.AndonMonitorTypeEnum;
import jp.adtekfuji.andon.enumerate.ContentTypeEnum;
import jp.adtekfuji.andon.enumerate.DisplayModeEnum;
import jp.adtekfuji.andon.property.AgendaMonitorSetting;
import jp.adtekfuji.andon.property.MonitorSettingFuji;
import jp.adtekfuji.javafxcommon.controls.SwitchButton;
import jp.adtekfuji.javafxcommon.controls.TimeHMTextField;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 * アジェンダモニター設定画面
 *
 * @author fu-kato
 */
public class AgendaMonitorSettingController extends AndonSettingController implements ChangeListener<Number> {

    final int MIN_MONITOR_NUMBER = 1;
    final int MAX_MONITOR_NUMBER = 5;

    @FXML
    private GridPane conditionPane;
    @FXML
    private DatePicker targetDate;
    @FXML
    private TimeHMTextField startTime;
    @FXML
    private TimeHMTextField endTime;
    @FXML
    private ComboBox<DisplayModeEnum> targetLine;
    @FXML
    private Button selectTarget;
    @FXML
    private TextField updateInterval;
    @FXML
    private Label modenNameLabel;
    @FXML
    private TextField modelNameField;
    @FXML
    private VBox breaktimeSetting;
    @FXML
    private ComboBox<Integer> displayNumber;
    @FXML
    private SwitchButton fullScrren;
    @FXML
    private TextField columnCount;
    @FXML
    private SwitchButton planOnly;
    @FXML
    private ComboBox<ContentTypeEnum> contentType;
    @FXML
    private TextField timeUnit;
    @FXML
    private SwitchButton togglePages;
    @FXML
    private TextField togglePageTime;
    @FXML
    private SwitchButton autoScroll;
    @FXML
    private TimeHMTextField autoScrollUnit;
    @FXML
    private TextField titleText;
    @FXML
    private TextField headerText;
    @FXML
    private TextField columnText;
    @FXML
    private TextField itemText;
    @FXML
    private TextField zoomBarText;

    private final long monitorId;
    private final AgendaMonitorSetting agenda;
    private final MonitorSettingFuji setting;

    // 親ウィンドウ
    private Stage parent;

    /**
     * 表示条件の対象日を非表示にする。
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        Set<Node> deleteNodes = new HashSet<>();
        for (Node child : this.conditionPane.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(child);
            int r = rowIndex == null ? 0 : rowIndex;

            if (r > 0) {
                GridPane.setRowIndex(child, r - 1);
            } else if (r == 0) {
                deleteNodes.add(child);
            }
        }

        this.conditionPane.getChildren().removeAll(deleteNodes);
        this.conditionPane.widthProperty().removeListener(this);
    }

    /**
     * コンストラクタ
     *
     * @param settingController
     * @param monitorId
     * @param setting
     * @param parent
     * @param showAll
     */
    public AgendaMonitorSettingController(MonitorSettingCompoFxController settingController, long monitorId, MonitorSettingFuji setting, Stage parent, boolean showAll) {

        this.setComponentController(settingController);

        this.monitorId = monitorId;
        this.setting = setting;
        this.agenda = setting.getAgendaMonitorSetting().clone();
        this.parent = Objects.isNull(parent) ? sc.getStage() : parent;

        URL url = getClass().getResource("/fxml/monitorsettingpluginfuji/agenda_monitor_setting.fxml");
        FXMLLoader loader = new FXMLLoader(url, rb);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();

            // 各コントロールの初期設定
            initControl();

            // 表示条件
            targetDate.setValue(Objects.isNull(this.agenda.getTargetDate()) ? LocalDate.now() : this.agenda.getTargetDate().toLocalDate());
            startTime.setText(this.agenda.getStartWorkTime().toString());
            endTime.setText(this.agenda.getEndWorkTime().toString());
            targetLine.getSelectionModel().select(this.agenda.getMode());
            targetLine.getSelectionModel().select(Objects.isNull(this.agenda.getMode()) ? DisplayModeEnum.LINE : this.agenda.getMode());
            selectTarget.setText(getTargetName(this.agenda));
            this.agenda.modeProperty().bind(targetLine.getSelectionModel().selectedItemProperty());
            updateInterval.textProperty().bindBidirectional(this.agenda.updateIntervalProperty(), new NumberStringConverter());
            modelNameField.textProperty().bindBidirectional(agenda.modelNameProperty());

            // 休憩時間
            breaktimeIdCollection.clear();
            for (BreakTimeInfoEntity b : this.agenda.getBreaktimes()) {
                BreakTimeInfoEntity breakEntity = CacheUtils.getCacheBreakTime(b.getBreaktimeId());
                if (Objects.isNull(breakEntity)) {
                    // 削除された休憩時間
                    isDeletedItems = true;
                    continue;
                }

                breaktimeIdCollection.add(new BreakTimeIdData(b.getBreaktimeId()));
            }

            // 画面設定
            displayNumber.getSelectionModel().select(Objects.isNull(this.agenda.getDisplayNumber()) ? 0 : this.agenda.getDisplayNumber() - 1);
            this.agenda.displayNumberProperty().bind(displayNumber.getSelectionModel().selectedItemProperty());
            Bindings.bindBidirectional(this.fullScrren.switchOnProperty(), this.agenda.fullScreenProperty());
            Bindings.bindBidirectional(columnCount.textProperty(), this.agenda.columnCountProperty(), new NumberStringConverter());
            Bindings.bindBidirectional(planOnly.switchOnProperty(), this.agenda.visibleOnlyPlanProperty());
            contentType.getSelectionModel().select(Objects.isNull(this.agenda.getContent()) ? ContentTypeEnum.WORKFLOW_NAME : this.agenda.getContent());
            this.agenda.contentProperty().bind(contentType.getSelectionModel().selectedItemProperty());
            Bindings.bindBidirectional(timeUnit.textProperty(), this.agenda.timeUnitProperty(), new NumberStringConverter());
            Bindings.bindBidirectional(togglePages.switchOnProperty(), this.agenda.togglePagesProperty());
            Bindings.bindBidirectional(togglePageTime.textProperty(), this.agenda.pageToggleTimeProperty(), new NumberStringConverter());
            Bindings.bindBidirectional(autoScroll.switchOnProperty(), this.agenda.autoScrollProperty());
            autoScrollUnit.setText(this.agenda.getScrollUnit().toString());

            // フォントサイズ
            Bindings.bindBidirectional(titleText.textProperty(), this.agenda.titleSizeProperty(), new NumberStringConverter());
            Bindings.bindBidirectional(headerText.textProperty(), this.agenda.headerSizeProperty(), new NumberStringConverter());
            Bindings.bindBidirectional(columnText.textProperty(), this.agenda.columnSizeProperty(), new NumberStringConverter());
            Bindings.bindBidirectional(itemText.textProperty(), this.agenda.itemSizeProperty(), new NumberStringConverter());
            Bindings.bindBidirectional(zoomBarText.textProperty(), this.agenda.zoomBarSizeProperty(), new NumberStringConverter());

            // 休憩時間
            Table breaktimeTable = new Table(breaktimeSetting.getChildren()).title(LocaleUtils.getString("key.AndonLineSettingBreakTime")).isAddRecord(true).styleClass("ContentTitleLabel");
            breaktimeTable.setAbstractRecordFactory(new BreakTimeRecordFactory(breaktimeTable, cache.getItemList(BreakTimeInfoEntity.class, new ArrayList()), breaktimeIdCollection));

            if (!showAll) {
                this.conditionPane.widthProperty().addListener(this);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * bindできないものを再度取得して設定
     *
     */
    private void updateUnbindPropeties() {
        LocalTimeStringConverter converter = new LocalTimeStringConverter(DateTimeFormatter.ISO_LOCAL_TIME, DateTimeFormatter.ISO_LOCAL_TIME);

        // 表示条件
        this.agenda.setTargetDate(LocalDateTime.of(targetDate.getValue(), LocalTime.of(0, 0, 0)));
        this.agenda.setStartWorkTime(converter.fromString(startTime.getText()));
        this.agenda.setEndWorkTime(converter.fromString(endTime.getText()));

        // 休憩時間
        List<BreakTimeInfoEntity> breaktimes = new ArrayList<>();
        for (BreakTimeIdData id : breaktimeIdCollection) {
            BreakTimeInfoEntity breakTime = CacheUtils.getCacheBreakTime(id.getId());
            if (Objects.isNull(breakTime)) {
                // 存在しないID
                continue;
            }
            breaktimes.add(breakTime);
        }
        this.agenda.setBreaktimes(breaktimes);

        // 画面設定
        this.agenda.setScrollUnit(converter.fromString(autoScrollUnit.getText()));
    }

    /**
     * 設定内容を取得する。
     *
     * @return
     */
    @Override
    public MonitorSettingFuji getInputResult() {
        logger.info("getAndonSetting: " + this.agenda);

        updateUnbindPropeties();

        MonitorSettingFuji result = setting.clone();

        result.setMonitorType(AndonMonitorTypeEnum.AGENDA);
        result.setMonitorId(this.monitorId);
        result.setAgendaMonitorSetting(this.agenda);

        return result;
    }

    /**
     * 入力した項目が有効なものか調べる。
     *
     * @return 全て正常に入力された場合true
     */
    @Override
    public boolean isValidItems() {

        updateUnbindPropeties();

        // 開始時間・終了時間の前後
        if (agenda.getStartWorkTime().isAfter(agenda.getEndWorkTime())) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DateCompErrMessage"), parent);
            return false;
        }

        // 休憩時間の重複
        Set<Long> set1 = new HashSet<>();
        for (BreakTimeIdData breakTimeId : breaktimeIdCollection) {
            if (set1.contains(breakTimeId.getId())) {
                BreakTimeInfoEntity breakTime = CacheUtils.getCacheBreakTime(breakTimeId.getId());
                if (Objects.isNull(breakTime)) {
                    // 存在しないID
                    continue;
                }
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), breakTime.getBreaktimeName()), parent);
                return false;
            } else {
                set1.add(breakTimeId.getId());
            }
        }

        // 自動スクロール範囲
        if (agenda.getScrollUnit().isBefore(LocalTime.of(0, 30)) || agenda.getScrollUnit().isAfter(LocalTime.of(8, 0))) {
            String message = new StringBuilder(LocaleUtils.getString("key.alert.inputValidation"))
                    .append("\r")
                    .append(" - ").append(LocaleUtils.getString("key.autoScrollTime"))
                    .toString();
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message, parent);
            return false;
        }

        return true;
    }

    /**
     * 表示対象選択ダイアログの表示 表示対象コンボボックスによって表示するダイアログを選択し表示する
     *
     * @param event
     */
    @FXML
    public void onSelectLine(Event event) {
        Button eventSrc = (Button) event.getSource();

        ButtonType ret;
        switch (this.targetLine.getSelectionModel().selectedItemProperty().getValue()) {
            case KANBAN:
                Instant instant = this.targetDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                Date date = Date.from(instant);

                List<Long> kanbanIds = this.agenda.getKanbanIds();
                eventSrc.setUserData(kanbanIds);

                ret = KanbanSelectDialog.showDialog((ActionEvent) event, date);
                if (ret.equals(ButtonType.OK)) {
                    List<KanbanInfoEntity> recv = (List) eventSrc.getUserData();

                    this.agenda.setKanbanIds(recv.stream().map(KanbanInfoEntity::getKanbanId).collect(Collectors.toList()));
                    eventSrc.setText(getTargetName(recv.stream().map(KanbanInfoEntity::getKanbanName).collect(Collectors.toList())));
                }
                break;
            case LINE:
                List<Long> lineIds = this.agenda.getLineIds();
                eventSrc.setUserData(lineIds);

                ret = LineSelectDialog.showDialogLineEntity(event);
                if (ret.equals(ButtonType.OK)) {
                    List<EquipmentInfoEntity> recv = (List) eventSrc.getUserData();

                    // ラインのみ最初の一つだけを使用する
                    this.agenda.setLineIds(recv.stream().map(EquipmentInfoEntity::getEquipmentId).limit(1).collect(Collectors.toList()));
                    eventSrc.setText(getTargetName(recv.stream().map(EquipmentInfoEntity::getEquipmentName).limit(1).collect(Collectors.toList())));
                }
                break;
            case WORKER:
                List<Long> orhanizationIds = this.agenda.getOrganizationIds();
                eventSrc.setUserData(orhanizationIds);

                ret = OrganizationSelectDialog.showDialog((ActionEvent) event);
                if (ret.equals(ButtonType.OK)) {
                    List<OrganizationInfoEntity> recv = (List) eventSrc.getUserData();

                    this.agenda.setOrganizationIds(recv.stream().map(OrganizationInfoEntity::getOrganizationId).collect(Collectors.toList()));
                    eventSrc.setText(getTargetName(recv.stream().map(OrganizationInfoEntity::getOrganizationName).collect(Collectors.toList())));
                }
                break;
        }
    }

    /**
     * 各コントロールを初期化する
     */
    private void initControl() {
        // 表示条件
        this.targetLine.setItems(FXCollections.observableArrayList(DisplayModeEnum.values()));
        this.targetLine.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.isNull(oldValue)) {
                return;
            }

            if (oldValue != newValue) {
                this.selectTarget.setText(this.getTargetName(this.agenda));
            }
        });

        // モデル名は対象がラインの時のみ機能するためそれ以外では無効にしておく
        final BooleanBinding bb = Bindings.not(Bindings.equal(DisplayModeEnum.LINE, targetLine.getSelectionModel().selectedItemProperty()));
        this.modelNameField.disableProperty().bind(bb);
        this.modenNameLabel.disableProperty().bind(bb);

        this.targetLine.setConverter(new StringConverter<DisplayModeEnum>() {
            @Override
            public String toString(DisplayModeEnum object) {
                return LocaleUtils.getString(object.getName());
            }

            @Override
            public DisplayModeEnum fromString(String string) {
                return null;
            }
        });

        // 画面設定
        this.displayNumber.setItems(FXCollections.observableArrayList(IntStream.rangeClosed(MIN_MONITOR_NUMBER, MAX_MONITOR_NUMBER).boxed().collect(Collectors.toList())));
        this.contentType.setItems(FXCollections.observableArrayList(ContentTypeEnum.values()));
        this.contentType.setConverter(new StringConverter<ContentTypeEnum>() {
            @Override
            public String toString(ContentTypeEnum object) {
                return LocaleUtils.getString(object.getName());
            }

            @Override
            public ContentTypeEnum fromString(String string) {
                return null;
            }
        });
    }

    /**
     * 表示対象に表示する項目を取得する
     *
     * @param setting
     * @return ,でつながれた項目
     */
    private String getTargetName(AgendaMonitorSetting setting) {

        switch (setting.getMode()) {
            case KANBAN: {
                List<KanbanInfoEntity> recv = KanbanSelectDialog.getKanbans(setting.getKanbanIds());
                return getTargetName(recv.stream().map(KanbanInfoEntity::getKanbanName).collect(Collectors.toList()));
            }
            case LINE: {
                List<EquipmentInfoEntity> recv = LineSelectDialog.getLines(setting.getLineIds());
                return getTargetName(recv.stream().map(EquipmentInfoEntity::getEquipmentName).collect(Collectors.toList()));
            }
            case WORKER: {
                List<OrganizationInfoEntity> recv = CacheUtils.getCacheOrganization(setting.getOrganizationIds());
                return getTargetName(recv.stream().map(OrganizationInfoEntity::getOrganizationName).collect(Collectors.toList()));
            }
            default:
                return getTargetName(Collections.emptyList());
        }
    }

    /**
     * 空じゃないリストの名前を,でつないた文字を返す<br>
     * 空の場合「割り当てなし」を返す
     *
     * @param names
     * @return ,でつながれた項目
     */
    private String getTargetName(List<String> names) {
        if (names.isEmpty()) {
            return LocaleUtils.getString("key.AndonLineSettingNotAllocate");
        }
        return names.stream().collect(Collectors.joining(", "));
    }
}
