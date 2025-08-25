/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.component;

import adtekfuji.admanagerapp.andonsetting.common.BreakTimeIdData;
import adtekfuji.admanagerapp.andonsetting.common.BreakTimeRecordFactory;
import adtekfuji.admanagerapp.andonsetting.common.Constants;
import adtekfuji.admanagerapp.andonsetting.dialog.KanbanHierarchySelectDialog;
import adtekfuji.admanagerapp.andonsetting.dialog.KanbanSelectDialog;
import adtekfuji.admanagerapp.andonsetting.dialog.LineSelectDialog;
import adtekfuji.admanagerapp.andonsetting.dialog.OrganizationSelectDialog;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.WarehouseMode;
import jp.adtekfuji.andon.enumerate.*;
import jp.adtekfuji.andon.property.AgendaMonitorSetting;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.controls.SwitchButton;
import jp.adtekfuji.javafxcommon.controls.TimeHMTextField;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 * アジェンダモニター設定画面
 *
 * @author fu-kato
 */
public class AgendaMonitorSettingController extends AndonSettingController {

    final int MIN_MONITOR_NUMBER = 1;
    final int MAX_MONITOR_NUMBER = 5;

    @FXML
    private GridPane conditionPane;
    @FXML
    private DatePicker targetDate;
    @FXML
    private Label startTimeLabel;
    @FXML
    private TimeHMTextField startTime;
    @FXML
    private Label endTimeLabel;
    @FXML
    private TimeHMTextField endTime;
    @FXML
    private ComboBox<DisplayModeEnum> targetLine;
    @FXML
    private Button selectTarget;
    @FXML
    private RestrictedTextField updateInterval;
    @FXML
    private Label modelNameLabel;
    @FXML
    private TextField modelNameField;
    @FXML
    private Label displayPeriodLabel;
    @FXML
    private RestrictedTextField displayPeriodField;

    @FXML
    private VBox breaktimeSetting;

    // 表示設定(縦軸)
    @FXML
    private GridPane varticalScreenSetting;
    @FXML
    private ComboBox<Integer> displayNumber;
    @FXML
    private SwitchButton fullScrren;
    @FXML
    private Label timeAxisLabel;
    @FXML
    private ComboBox<TimeAxisEnum> timeAxis;
    @FXML
    private TextField columnCount;
    @FXML
    private Label showOrderLabel;
    @FXML
    private ComboBox<ShowOrder> showOrder;
    @FXML
    private SwitchButton planOnly;
    @FXML
    private Label showActualTimeLabel;
    @FXML
    private SwitchButton showActualTime;
    @FXML
    private Label displaySupportResultsLabel;
    @FXML
    private SwitchButton displaySupportResults;
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
    private Label agendaDisplayPatternLabel;
    @FXML
    private ComboBox<AgendaDisplayPatternEnum> agendaDisplayPattern;

    // 表示設定(横軸)
    @FXML
    private GridPane horizonScreenSetting;
    @FXML
    private ComboBox<Integer> horizonDisplayNumber;
    @FXML
    private SwitchButton horizonFullScrren;
    @FXML
    private Label horizonTimeAxisLabel;
    @FXML
    private ComboBox<TimeAxisEnum> horizonTimeAxis;
    @FXML
    private ComboBox<TimeScaleEnum> horizonTimeScale;
    @FXML
    private Label horizonShowDaysLabel;
    @FXML
    private TextField horizonShowDays;
    @FXML
    private Label horizonShowMonthsLabel;
    @FXML
    private TextField horizonShowMonths;
    @FXML
    private Label horizonShowHolidayLabel;
    @FXML
    private SwitchButton horizonShowHoliday;
    @FXML
    private Label horizonColumnCountLabel;
    @FXML
    private TextField horizonColumnCount;
    @FXML
    private Label horizonShowOrderLabel;
    @FXML
    private TextField horizonRowHight;
    @FXML
    private ComboBox<ShowOrder> horizonShowOrder;
    @FXML
    private Label horizonTimeUnitLabel;
    @FXML
    private TextField horizonTimeUnit;
    @FXML
    private SwitchButton horizonTogglePages;
    @FXML
    private TextField horizonTogglePageTime;
    @FXML
    private Label horizonAutoScrollLabel;
    @FXML
    private SwitchButton horizonAutoScroll;
    @FXML
    private Label horizonAutoScrollUnitLabel;
    @FXML
    private TimeHMTextField horizonAutoScrollUnit;
    @FXML
    private Label horizonAgendaDisplayPatternLabel;
    @FXML
    private ComboBox<AgendaDisplayPatternEnum> horizonAgendaDisplayPattern;
    @FXML
    private Label horizonPlanActualShowTypeLabel;
    @FXML
    private ComboBox<PlanActualShowTypeEnum> horizonPlanActualShowType;
    @FXML
    private Label horizonShowActualTimeLabel;
    @FXML
    private SwitchButton horizonShowActualTime;
    @FXML
    private Label horizonDisplaySupportResultsLabel;
    @FXML
    private SwitchButton horizonDisplaySupportResults;
    @FXML
    private Label horizonContentTypeLabel;
    @FXML
    private ComboBox<ContentTypeEnum> horizonContentType;

    // 表示設定(払出状況)
    /** 画面設定(払出状況) グループ */
    @FXML
    private GridPane payoutStatusScreenSetting;
    /** ディスプレイ番号 */
    @FXML
    private ComboBox<Integer> payoutdisplayNumber;
    /** フルスクリーン表示 */
    @FXML
    private SwitchButton payoutFullScreen;
    /** 払出完了の行数項目 */
    @FXML
    private RestrictedTextField payoutCompleteLineCount;
    /** 払出待ちの行数項目 */
    @FXML
    private RestrictedTextField payoutWaitingLineCount;
    /** ピッキング中の行数項目 */
    @FXML
    private RestrictedTextField pickingLineCount;
    /** 受付の行数項目 */
    @FXML
    private RestrictedTextField receptionLineCount;
    /** 払出完了の表示日数項目 */
    @FXML
    private RestrictedTextField payoutCompleteDisplayDays;
    /** ページ切り替え間隔(秒)項目 */
    @FXML
    private RestrictedTextField pagingIntervalSeconds;
    /** フォントサイズ項目 */
    @FXML
    private RestrictedTextField fontSizePs;

    /** フォントサイズ設定グループラベル */
    @FXML
    private VBox fontSettingLabel;
    
    // フォントサイズ設定
    /** フォントサイズ設定グループ */
    @FXML
    private GridPane fontSetting;
    @FXML
    private TextField titleText;
    @FXML
    private TextField headerText;
    @FXML
    private Label columnLabel;
    @FXML
    private TextField columnText;
    @FXML
    private TextField itemText;
    @FXML
    private TextField zoomBarText;
    @FXML
    private ComboBox<BeforeDaysEnum> horizonBeforeDays;
    @FXML
    private Label horizonBeforeDaysLabel;
    @FXML
    private Label displayOrderLabel;
    @FXML
    private ComboBox<DisplayOrderEnum> displayOrder;


    private final long monitorId;
    private final AgendaMonitorSetting agenda;

    // 親ウィンドウ
    private Stage parent;

    /**
     * 表示条件の対象日を非表示にする。
     */
    private void removeTargetDateRow() {
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
    public AgendaMonitorSettingController(AndonSettingCompoFxController settingController, long monitorId, AgendaMonitorSetting setting, Stage parent, boolean showAll) {

        this.setComponentController(settingController);

        this.monitorId = monitorId;
        this.agenda = setting;
        this.parent = Objects.isNull(parent) ? sc.getStage() : parent;

        URL url = getClass().getResource("/fxml/compo/agenda_monitor_setting.fxml");
        FXMLLoader loader = new FXMLLoader(url, rb);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();

            // 各コントロールの初期設定
            this.initControl();

            // 表示条件

            // 対象日
            this.targetDate.setValue(Objects.isNull(this.agenda.getTargetDate()) ? LocalDate.now() : this.agenda.getTargetDate().toLocalDate());
            // 開始時間(hh:mm)
            this.startTime.setText(this.agenda.getStartWorkTime().toString());
            // 終了時間(hh:mm)
            this.endTime.setText(this.agenda.getEndWorkTime().toString());

            this.targetLine.getSelectionModel().select(this.agenda.getMode());
            this.targetLine.getSelectionModel().select(Objects.isNull(this.agenda.getMode()) ? DisplayModeEnum.LINE : this.agenda.getMode());
            this.selectTarget.setText(getTargetName(this.agenda));

            // 表示対象
            this.agenda.modeProperty().bind(this.targetLine.getSelectionModel().selectedItemProperty());
            // 更新間隔(分)
            this.updateInterval.textProperty().bindBidirectional(this.agenda.updateIntervalProperty(), new NumberStringConverter());
            // モデル名
            this.modelNameField.textProperty().bindBidirectional(this.agenda.modelNameProperty());
            // 表示期間(日)
            this.displayPeriodField.textProperty().bindBidirectional(this.agenda.displayPeriodProperty(), new NumberStringConverter());
            // 表示順
            this.displayOrder.getSelectionModel().select(Objects.isNull(this.agenda.getDisplayOrder()) ? DisplayOrderEnum.values()[0] : this.agenda.getDisplayOrder());
            this.agenda.displayOrderProperty().bind(this.displayOrder.getSelectionModel().selectedItemProperty());

            // 休憩時間
            this.breaktimeIdCollection.clear();
            for (BreakTimeInfoEntity b : this.agenda.getBreaktimes()) {
                BreakTimeInfoEntity breakEntity = CacheUtils.getCacheBreakTime(b.getBreaktimeId());
                if (Objects.isNull(breakEntity)) {
                    // 削除された休憩時間
                    this.isDeletedItems = true;
                    continue;
                }

                this.breaktimeIdCollection.add(new BreakTimeIdData(b.getBreaktimeId()));
            }

            // 画面設定(縦軸)

            // (縦軸)ディスプレイ番号
            this.displayNumber.getSelectionModel().select(Objects.isNull(this.agenda.getDisplayNumber()) ? 0 : this.agenda.getDisplayNumber() - 1);
            this.agenda.displayNumberProperty().bind(this.displayNumber.getSelectionModel().selectedItemProperty());
            // (縦軸)フルスクリーン表示
            Bindings.bindBidirectional(this.fullScrren.switchOnProperty(), this.agenda.fullScreenProperty());
            // 時間軸
            this.timeAxis.getSelectionModel().select(Objects.isNull(this.agenda.getTimeAxis()) ? TimeAxisEnum.getDefault() : this.agenda.getTimeAxis());
            this.agenda.timeAxisProperty().bind(this.timeAxis.getSelectionModel().selectedItemProperty());
            // (縦軸)カラム数
            Bindings.bindBidirectional(this.columnCount.textProperty(), this.agenda.columnCountProperty(), new NumberStringConverter());
            // (縦軸)表示順
            this.showOrder.getSelectionModel().select(Objects.isNull(this.agenda.getShowOrder()) ? ShowOrder.getDefault() : this.agenda.getShowOrder());
            this.agenda.showOrderProperty().bind(this.showOrder.getSelectionModel().selectedItemProperty());
            // (縦軸)予定のみ表示
            Bindings.bindBidirectional(this.planOnly.switchOnProperty(), this.agenda.visibleOnlyPlanProperty());
            // (縦軸)進捗時間表示
            Bindings.bindBidirectional(this.showActualTime.switchOnProperty(), this.agenda.showActualTimeProperty());
            // (縦軸)応援者の実績を表示
            Bindings.bindBidirectional(this.displaySupportResults.switchOnProperty(), this.agenda.displaySupportResultsProperty());
            // (縦軸)詳細表示
            this.contentType.getSelectionModel().select(Objects.isNull(this.agenda.getContent()) ? ContentTypeEnum.WORKFLOW_NAME : this.agenda.getContent());
            this.agenda.contentProperty().bind(this.contentType.getSelectionModel().selectedItemProperty());
            // (縦軸)時間軸の表示単位(分)
            Bindings.bindBidirectional(this.timeUnit.textProperty(), this.agenda.timeUnitProperty(), new NumberStringConverter());
            // (縦軸)ページ切り替え
            Bindings.bindBidirectional(this.togglePages.switchOnProperty(), this.agenda.togglePagesProperty());
            // (縦軸)ページ切り替え間隔(秒)
            Bindings.bindBidirectional(this.togglePageTime.textProperty(), this.agenda.pageToggleTimeProperty(), new NumberStringConverter());
            // (縦軸)自動スクロール
            Bindings.bindBidirectional(this.autoScroll.switchOnProperty(), this.agenda.autoScrollProperty());
            // (縦軸)自動スクロール範囲[HH:mm]
            this.autoScrollUnit.setText(this.agenda.getScrollUnit().toString());
			// (縦軸)まとめて表示
            agendaDisplayPattern.getSelectionModel().select(Objects.isNull(agenda.getAgendaDisplayPattern())? AgendaDisplayPatternEnum.getDefault() : this.agenda.getAgendaDisplayPattern());
            this.agenda.agendaDisplayPatternProperty().bind(agendaDisplayPattern.getSelectionModel().selectedItemProperty());

            // 画面設定(横軸)

            // (横軸)ディスプレイ番号
            this.horizonDisplayNumber.getSelectionModel().select(Objects.isNull(this.agenda.getHorizonDisplayNumber()) ? 0 : this.agenda.getHorizonDisplayNumber() - 1);
            this.agenda.horizonDisplayNumberProperty().bind(this.horizonDisplayNumber.getSelectionModel().selectedItemProperty());
            // (横軸)フルスクリーン表示
            Bindings.bindBidirectional(this.horizonFullScrren.switchOnProperty(), this.agenda.horizonFullScreenProperty());
            // (横軸)時間軸
            this.horizonTimeAxis.getSelectionModel().select(Objects.isNull(this.agenda.getTimeAxis()) ? TimeAxisEnum.getDefault() : this.agenda.getTimeAxis());
            this.horizonTimeAxis.valueProperty().bindBidirectional(this.timeAxis.valueProperty());
            // (横軸)時間軸スケール
            this.horizonTimeScale.getSelectionModel().select(Objects.isNull(this.agenda.getHorizonTimeScale()) ? TimeScaleEnum.getDefault() : this.agenda.getHorizonTimeScale());
            this.agenda.horizonTimeScaleProperty().bind(horizonTimeScale.getSelectionModel().selectedItemProperty());
            // (横軸)表示開始日
            this.horizonBeforeDays.getSelectionModel().select(Objects.isNull(this.agenda.getHorizonBeforeDays()) ? BeforeDaysEnum.getDefault() : this.agenda.getHorizonBeforeDays());
            this.agenda.horizonBeforeDaysProperty().bind(horizonBeforeDays.getSelectionModel().selectedItemProperty());
            // (横軸)時間軸の表示単位(分)
            Bindings.bindBidirectional(this.horizonTimeUnit.textProperty(), this.agenda.horizonTimeUnitProperty(), new NumberStringConverter());
            // (横軸)表示日数
            Bindings.bindBidirectional(this.horizonShowDays.textProperty(), this.agenda.horizonShowDaysProperty(), new NumberStringConverter());
            // (横軸)表示月数
            Bindings.bindBidirectional(this.horizonShowMonths.textProperty(), this.agenda.horizonShowMonthsProperty(), new NumberStringConverter());
            // (横軸)休日表示
            Bindings.bindBidirectional(this.horizonShowHoliday.switchOnProperty(), this.agenda.horizonShowHolidayProperty());
            // (横軸)カラム数
            Bindings.bindBidirectional(this.horizonColumnCount.textProperty(), this.agenda.horizonColumnCountProperty(), new NumberStringConverter());
            // (横軸)表示順
            this.agenda.horizonShowOrderProperty().bind(this.horizonShowOrder.getSelectionModel().selectedItemProperty());
            // 
            this.horizonShowOrder.getSelectionModel().select(Objects.isNull(this.agenda.getHorizonShowOrder()) ? ShowOrder.getDefault() : this.agenda.getHorizonShowOrder());
            // (横軸)行の高さ(ピクセル)
            Bindings.bindBidirectional(this.horizonRowHight.textProperty(), this.agenda.horizonRowHightProperty(), new NumberStringConverter());
			// (横軸)まとめて表示
            horizonAgendaDisplayPattern.getSelectionModel().select(Objects.isNull(agenda.getHorizonAgendaDisplayPattern())?AgendaDisplayPatternEnum.getDefault() : this.agenda.getHorizonAgendaDisplayPattern());
            this.agenda.horizonAgendaDisplayPatternProperty().bind(horizonAgendaDisplayPattern.getSelectionModel().selectedItemProperty());
            // (横軸)ページ切り替え
            Bindings.bindBidirectional(this.horizonTogglePages.switchOnProperty(), this.agenda.horizonTogglePagesProperty());
            // (横軸)ページ切り替え間隔(秒)
            Bindings.bindBidirectional(this.horizonTogglePageTime.textProperty(), this.agenda.horizonPageToggleTimeProperty(), new NumberStringConverter());
            // (横軸)自動スクロール
            Bindings.bindBidirectional(this.horizonAutoScroll.switchOnProperty(), this.agenda.horizonAutoScrollProperty());
            // (横軸)自動スクロール範囲
            this.horizonAutoScrollUnit.setText(this.agenda.getHorizonScrollUnit().toString());
            // (横軸)予実表示
            this.horizonPlanActualShowType.getSelectionModel().select(Objects.isNull(this.agenda.getHorizonPlanActualShowType()) ? PlanActualShowTypeEnum.getDefault() : this.agenda.getHorizonPlanActualShowType());
            this.agenda.horizonPlanActualShowTypeProperty().bind(this.horizonPlanActualShowType.getSelectionModel().selectedItemProperty());
            // (横軸)進捗時間表示
            Bindings.bindBidirectional(this.horizonShowActualTime.switchOnProperty(), this.agenda.horizonShowActualTimeProperty());
            // (横軸)応援者の実績を表示
            Bindings.bindBidirectional(this.horizonDisplaySupportResults.switchOnProperty(), this.agenda.horizonDisplaySupportResultsProperty());
            // (横軸)詳細表示
            this.horizonContentType.getSelectionModel().select(Objects.isNull(this.agenda.getHorizonContent()) ? ContentTypeEnum.WORKFLOW_NAME : this.agenda.getHorizonContent());
            this.agenda.horizonContentProperty().bind(this.horizonContentType.getSelectionModel().selectedItemProperty());


            // 画面設定(払出状況)

            // (払出状況)ディスプレイ番号
            this.payoutdisplayNumber.getSelectionModel().select(Objects.isNull(this.agenda.getPayoutDisplayNumber()) ? 0 : this.agenda.getPayoutDisplayNumber() - 1);
            this.agenda.payoutDisplayNumberProperty().bind(this.payoutdisplayNumber.getSelectionModel().selectedItemProperty());
            // (払出状況)フルスクリーン表示
            Bindings.bindBidirectional(this.payoutFullScreen.switchOnProperty(), this.agenda.payoutFullScreenProperty());
            // 払出完了の行数
            this.payoutCompleteLineCount.textProperty().bindBidirectional(this.agenda.PayoutCompleteLineCountProperty(), new NumberStringConverter());
            // 払出待ちの行数
            this.payoutWaitingLineCount.textProperty().bindBidirectional(this.agenda.PayoutWaitingLineCountProperty(), new NumberStringConverter());
            // ピッキング中の行数
            this.pickingLineCount.textProperty().bindBidirectional(this.agenda.PickingLineCountProperty(), new NumberStringConverter());
            // 受付の行数
            this.receptionLineCount.textProperty().bindBidirectional(this.agenda.ReceptionLineCountProperty(), new NumberStringConverter());
            // 払出完了の表示日数
            this.payoutCompleteDisplayDays.textProperty().bindBidirectional(this.agenda.PayoutCompleteDisplayDaysProperty(), new NumberStringConverter());
            // ページ切り替え間隔(秒)
            this.pagingIntervalSeconds.textProperty().bindBidirectional(this.agenda.PagingIntervalSecondsProperty(), new NumberStringConverter());
            // フォントサイズ
            this.fontSizePs.textProperty().bindBidirectional(this.agenda.FontSizePsProperty(), new NumberStringConverter());

            
            // フォントサイズ

            // タイトルのフォントサイズ
            Bindings.bindBidirectional(this.titleText.textProperty(), this.agenda.titleSizeProperty(), new NumberStringConverter());
            // ヘッダーのフォントサイズ
            Bindings.bindBidirectional(this.headerText.textProperty(), this.agenda.headerSizeProperty(), new NumberStringConverter());
            // カラムのフォントサイズ
            Bindings.bindBidirectional(this.columnText.textProperty(), this.agenda.columnSizeProperty(), new NumberStringConverter());
            // アイテムのフォントサイズ
            Bindings.bindBidirectional(this.itemText.textProperty(), this.agenda.itemSizeProperty(), new NumberStringConverter());
            // 拡大スライドバーのフォントサイズ
            Bindings.bindBidirectional(this.zoomBarText.textProperty(), this.agenda.zoomBarSizeProperty(), new NumberStringConverter());

            // 休憩時間
            Table breaktimeTable = new Table(this.breaktimeSetting.getChildren()).title(LocaleUtils.getString("key.AndonLineSettingBreakTime")).isAddRecord(true).styleClass("ContentTitleLabel");
            breaktimeTable.setAbstractRecordFactory(new BreakTimeRecordFactory(breaktimeTable, cache.getItemList(BreakTimeInfoEntity.class, new ArrayList()), this.breaktimeIdCollection));

            // 画面設定(縦軸)、画面設定(横軸)は払出状況時には非表示
            // また、画面設定(払出状況)は払出状況時のみ表示
            BooleanBinding virtical = Bindings.equal(TimeAxisEnum.VerticalAxis, this.agenda.timeAxisProperty());
            BooleanBinding payoutBB = Bindings.equal(DisplayModeEnum.PAYOUT_STATUS, this.targetLine.getSelectionModel().selectedItemProperty());
            this.varticalScreenSetting.managedProperty().bind(payoutBB.not().and(virtical));
            this.varticalScreenSetting.visibleProperty().bind(this.varticalScreenSetting.managedProperty());
            this.horizonScreenSetting.managedProperty().bind(payoutBB.not().and(virtical.not()));
            this.horizonScreenSetting.visibleProperty().bind(this.horizonScreenSetting.managedProperty());
            this.payoutStatusScreenSetting.managedProperty().bind(payoutBB);
            this.payoutStatusScreenSetting.visibleProperty().bind(this.payoutStatusScreenSetting.managedProperty());

            // フォントサイズは払出状況時には非表示
            this.fontSettingLabel.managedProperty().bind(payoutBB.not());
            this.fontSettingLabel.visibleProperty().bind(this.fontSettingLabel.managedProperty());
            this.fontSetting.managedProperty().bind(payoutBB.not());
            this.fontSetting.visibleProperty().bind(this.fontSetting.managedProperty());

            if (!showAll) {
                this.removeTargetDateRow();
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
        this.agenda.setTargetDate(LocalDateTime.of(this.targetDate.getValue(), LocalTime.of(0, 0, 0)));
        this.agenda.setStartWorkTime(converter.fromString(this.startTime.getText()));
        this.agenda.setEndWorkTime(converter.fromString(this.endTime.getText()));

        // 休憩時間
        List<BreakTimeInfoEntity> breaktimes = new ArrayList<>();
        for (BreakTimeIdData id : this.breaktimeIdCollection) {
            BreakTimeInfoEntity breakTime = CacheUtils.getCacheBreakTime(id.getId());
            if (Objects.isNull(breakTime)) {
                // 存在しないID
                continue;
            }
            breaktimes.add(breakTime);
        }
        this.agenda.setBreaktimes(breaktimes);

        // 画面設定
        this.agenda.setScrollUnit(converter.fromString(this.autoScrollUnit.getText()));
        this.agenda.setHorizonScrollUnit(converter.fromString(this.horizonAutoScrollUnit.getText()));
    }

    /**
     * 設定内容を取得する。
     *
     * @return
     */
    @Override
    public AndonMonitorLineProductSetting getInputResult() {
        logger.info("getAndonSetting: " + this.agenda);

        updateUnbindPropeties();

        AndonMonitorLineProductSetting result = AndonMonitorLineProductSetting.create();

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
        if (this.agenda.getStartWorkTime().isAfter(this.agenda.getEndWorkTime())) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DateCompErrMessage"), this.parent);
            return false;
        }

        // 休憩時間の重複
        Set<Long> set1 = new HashSet<>();
        for (BreakTimeIdData breakTimeId : this.breaktimeIdCollection) {
            if (set1.contains(breakTimeId.getId())) {
                BreakTimeInfoEntity breakTime = CacheUtils.getCacheBreakTime(breakTimeId.getId());
                if (Objects.isNull(breakTime)) {
                    // 存在しないID
                    continue;
                }
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), breakTime.getBreaktimeName()), this.parent);
                return false;
            } else {
                set1.add(breakTimeId.getId());
            }
        }

        // 自動スクロール範囲
        if (this.agenda.getScrollUnit().isBefore(LocalTime.of(0, 30)) || this.agenda.getScrollUnit().isAfter(LocalTime.of(8, 0))) {
            String message = new StringBuilder(LocaleUtils.getString("key.alert.inputValidation"))
                    .append("\r")
                    .append(" - ").append(LocaleUtils.getString("key.autoScrollTime"))
                    .toString();
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message, this.parent);
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

                ret = KanbanSelectDialog.showDialog((ActionEvent) event, date, false);
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

                    this.agenda.setLineIds(recv.stream().map(EquipmentInfoEntity::getEquipmentId).collect(Collectors.toList()));
                    eventSrc.setText(getTargetName(recv.stream().map(EquipmentInfoEntity::getEquipmentName).collect(Collectors.toList())));
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
            case PRODUCT_PROGRESS:
                List<Long> kanbanHierarchyIds = this.agenda.getKanbanHierarchyIds();
                eventSrc.setUserData(kanbanHierarchyIds);

                ret = KanbanHierarchySelectDialog.showDialog((ActionEvent) event, false);
                if (ret.equals(ButtonType.OK)) {
                    List<KanbanHierarchyInfoEntity> recv = (List) eventSrc.getUserData();
                    this.agenda.setKanbanHierarchyIds(recv.stream().map(KanbanHierarchyInfoEntity::getKanbanHierarchyId).collect(Collectors.toList()));
                    eventSrc.setText(getTargetName(recv.stream().map(KanbanHierarchyInfoEntity::getHierarchyName).collect(Collectors.toList())));
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
        
        boolean isWarehouseOption = Boolean.parseBoolean(AdProperty.getProperties().getProperty("@Warehouse", "false"));
        boolean isDeliveryMonitor = WarehouseMode.HAMAI.equals(WarehouseMode.valueOf(AdProperty.getProperties().getProperty("wh_mode", "STANDARD")));
        if (!isWarehouseOption || !isDeliveryMonitor) {
            // 倉庫案内オプションが無効 又は 運用モード≠HAMAI の場合
            this.targetLine.getItems().remove(DisplayModeEnum.PAYOUT_STATUS);
        }

        // 払出状況の無効性スキーム
        final BooleanBinding payoutBB = Bindings.equal(DisplayModeEnum.PAYOUT_STATUS, this.targetLine.getSelectionModel().selectedItemProperty());
        
        // 割り当てボタンは払出状況時には非表示
        this.selectTarget.managedProperty().bind(payoutBB.not());
        this.selectTarget.visibleProperty().bind(this.selectTarget.managedProperty());

        // モデル名は対象がライン又は払出状況の時のみ機能するためそれ以外では無効にしておく
        final BooleanBinding bb = Bindings.equal(DisplayModeEnum.LINE, this.targetLine.getSelectionModel().selectedItemProperty());
        this.modelNameField.managedProperty().bind(bb.or(payoutBB));
        this.modelNameLabel.managedProperty().bind(bb.or(payoutBB));
        this.modelNameField.visibleProperty().bind(bb.or(payoutBB));
        this.modelNameLabel.visibleProperty().bind(bb.or(payoutBB));

        this.displayOrder.setItems(FXCollections.observableArrayList(DisplayOrderEnum.values()));
        this.displayOrder.setConverter(new StringConverter<DisplayOrderEnum>() {
            @Override
            public String toString(DisplayOrderEnum object) {
                return LocaleUtils.getString(object.getName());
            }

            @Override
            public DisplayOrderEnum fromString(String string) {
                return null;
            }
        });

        // ======================= 製品進捗表示 ========================
        // 表示期間は対象が製造進捗の時のみ機能するためそれ以外では無効にしておく
        final BooleanBinding displayBB = Bindings.equal(DisplayModeEnum.PRODUCT_PROGRESS, this.targetLine.getSelectionModel().selectedItemProperty());
        // 表示期間は製品進捗表示時のみ表示
        this.displayPeriodLabel.managedProperty().bind(displayBB);
        this.displayPeriodField.managedProperty().bind(displayBB);
        this.displayOrderLabel.managedProperty().bind(displayBB);
        this.displayOrder.managedProperty().bind(displayBB);
        this.displayPeriodLabel.visibleProperty().bind(displayBB);
        this.displayPeriodField.visibleProperty().bind(displayBB);
        this.displayOrderLabel.visibleProperty().bind(displayBB);
        this.displayOrder.visibleProperty().bind(displayBB);

        // 製品進捗表示時は時間軸が横のみ対応
        this.timeAxis.disableProperty().bind(displayBB);
        this.horizonTimeAxis.disableProperty().bind(displayBB);

        // 製品進捗表示時は時間軸を強制的に横にする
        this.targetLine.valueProperty().addListener((ObservableValue<? extends DisplayModeEnum> observable, DisplayModeEnum oldValue, DisplayModeEnum newValue) -> {
            if(newValue.equals(DisplayModeEnum.PRODUCT_PROGRESS)) {
                this.timeAxis.valueProperty().setValue(TimeAxisEnum.HorizonAxis);
                this.horizonTimeAxis.valueProperty().set(TimeAxisEnum.HorizonAxis);
            } else if (!Boolean.parseBoolean(AdProperty.getProperties().getProperty(Constants.VISIBLE_TIME_AXIS, Constants.VISIBLE_TIME_AXIS_DEFAULT))) {
                // 横軸表示無効の場合は進捗表示以外を選んだ場合に強制に縦にする
                this.timeAxis.valueProperty().setValue(TimeAxisEnum.VerticalAxis);
                this.horizonTimeAxis.valueProperty().setValue(TimeAxisEnum.VerticalAxis);
            }
        });

        final BooleanBinding nDisplayBB = Bindings.not(displayBB);
        // 製品進捗表示時は予実表示パターンは非表示
        this.horizonAgendaDisplayPatternLabel.managedProperty().bind(nDisplayBB);
        this.horizonAgendaDisplayPattern.managedProperty().bind(nDisplayBB);
        this.horizonAgendaDisplayPatternLabel.visibleProperty().bind(nDisplayBB);
        this.horizonAgendaDisplayPattern.visibleProperty().bind(nDisplayBB);

        // 製品進捗表示時は予実表示は非表示
        this.horizonPlanActualShowTypeLabel.managedProperty().bind(nDisplayBB);
        this.horizonPlanActualShowType.managedProperty().bind(nDisplayBB);
        this.horizonPlanActualShowTypeLabel.visibleProperty().bind(nDisplayBB);
        this.horizonPlanActualShowType.visibleProperty().bind(nDisplayBB);

        // 製品進捗表示時進捗時間表示は非表示
        this.horizonShowActualTimeLabel.managedProperty().bind(nDisplayBB);
        this.horizonShowActualTime.managedProperty().bind(nDisplayBB);
        this.horizonShowActualTimeLabel.visibleProperty().bind(nDisplayBB);
        this.horizonShowActualTime.visibleProperty().bind(nDisplayBB);

        // 製品進捗表示時は応援者は非表示
        this.horizonDisplaySupportResultsLabel.managedProperty().bind(nDisplayBB);
        this.horizonDisplaySupportResults.managedProperty().bind(nDisplayBB);
        this.horizonDisplaySupportResultsLabel.visibleProperty().bind(nDisplayBB);
        this.horizonDisplaySupportResults.visibleProperty().bind(nDisplayBB);

        // 製品進捗表示時は詳細表示は非表示
        this.horizonContentTypeLabel.managedProperty().bind(nDisplayBB);
        this.horizonContentType.managedProperty().bind(nDisplayBB);
        this.horizonContentTypeLabel.visibleProperty().bind(nDisplayBB);
        this.horizonContentType.visibleProperty().bind(nDisplayBB);

        // 製品進捗表示時はカラムフォントサイズは非表示
        this.columnLabel.managedProperty().bind(nDisplayBB);
        this.columnText.managedProperty().bind(nDisplayBB);
        this.columnLabel.visibleProperty().bind(nDisplayBB);
        this.columnText.visibleProperty().bind(nDisplayBB);

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

        // 「時間軸」は「visibleTimeAxis=false」の場合に非表示にする。(デフォルト:true)
        if (!Boolean.parseBoolean(AdProperty.getProperties().getProperty(Constants.VISIBLE_TIME_AXIS, Constants.VISIBLE_TIME_AXIS_DEFAULT))) {
            this.timeAxisLabel.setManaged(false);
            this.timeAxis.setManaged(false);
            this.horizonTimeAxisLabel.setManaged(false);
            this.horizonTimeAxis.setManaged(false);
        }

        // 画面設定(縦軸)
        this.displayNumber.setItems(FXCollections.observableArrayList(IntStream.rangeClosed(MIN_MONITOR_NUMBER, MAX_MONITOR_NUMBER).boxed().collect(Collectors.toList())));
        this.showOrderLabel.managedProperty().bind(this.showOrder.managedProperty());
        this.showOrderLabel.visibleProperty().bind(this.showOrder.visibleProperty());
        BooleanBinding vob = Bindings.equal(DisplayModeEnum.KANBAN, this.targetLine.getSelectionModel().selectedItemProperty());
        this.showOrder.managedProperty().bind(vob);
        this.showOrder.visibleProperty().bindBidirectional(this.showOrder.managedProperty());
        this.showOrder.setItems(FXCollections.observableArrayList(ShowOrder.values()));
        this.showOrder.setConverter(new StringConverter<ShowOrder>() {
            @Override
            public String toString(ShowOrder object) {
                return LocaleUtils.getString(object.getName());
            }

            @Override
            public ShowOrder fromString(String string) {
                return null;
            }
        });

        this.timeAxis.setItems(FXCollections.observableArrayList(TimeAxisEnum.values()));
        this.timeAxis.setConverter(new StringConverter<TimeAxisEnum>() {
            @Override
            public String toString(TimeAxisEnum object) {
                return LocaleUtils.getString(object.getName());
            }

            @Override
            public TimeAxisEnum fromString(String string) {
                return null;
            }
        });

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

        // 画面設定(横軸)
        this.horizonDisplayNumber.setItems(FXCollections.observableArrayList(IntStream.rangeClosed(MIN_MONITOR_NUMBER, MAX_MONITOR_NUMBER).boxed().collect(Collectors.toList())));
        this.horizonTimeAxis.setItems(FXCollections.observableArrayList(TimeAxisEnum.values()));
        this.horizonTimeAxis.setConverter(new StringConverter<TimeAxisEnum>() {
            @Override
            public String toString(TimeAxisEnum object) {
                return LocaleUtils.getString(object.getName());
            }

            @Override
            public TimeAxisEnum fromString(String string) {
                return null;
            }
        });

        // 時間軸スケール
        List<TimeScaleEnum> timeScales = new LinkedList();
        timeScales.add(TimeScaleEnum.Time);
        timeScales.add(TimeScaleEnum.HalfDay);
        timeScales.add(TimeScaleEnum.Day);

        // 「1週間」と「1カ月」は「enableLongTime=true」の場合のみ表示する。(デフォルト:true)
        if (Boolean.valueOf(AdProperty.getProperties().getProperty(Constants.ENABLE_LONG_TIME, Constants.ENABLE_LONG_TIME_DEFAULT))) {
            timeScales.add(TimeScaleEnum.Week);
            timeScales.add(TimeScaleEnum.Month);
        }

        this.horizonTimeScale.setItems(FXCollections.observableArrayList(timeScales));
        this.horizonTimeScale.setConverter(new StringConverter<TimeScaleEnum>() {
            @Override
            public String toString(TimeScaleEnum object) {
                return LocaleUtils.getString(object.getName());
            }

            @Override
            public TimeScaleEnum fromString(String string) {
                return null;
            }
        });

        BooleanBinding bindShowTimeUnit = Bindings.equal(TimeScaleEnum.Time, this.horizonTimeScale.getSelectionModel().selectedItemProperty());
        BooleanBinding bindShowDays = Bindings.equal(TimeScaleEnum.Day, this.horizonTimeScale.getSelectionModel().selectedItemProperty()).or(Bindings.equal(TimeScaleEnum.HalfDay, this.horizonTimeScale.getSelectionModel().selectedItemProperty()));
        BooleanBinding bindShowMonths = Bindings.equal(TimeScaleEnum.Week, this.horizonTimeScale.getSelectionModel().selectedItemProperty()).or(Bindings.equal(TimeScaleEnum.Month, this.horizonTimeScale.getSelectionModel().selectedItemProperty()));
        BooleanBinding bindShowDaysandMonths = 
                Bindings.equal(TimeScaleEnum.Day, this.horizonTimeScale.getSelectionModel().selectedItemProperty())
                        .or(Bindings.equal(TimeScaleEnum.HalfDay, this.horizonTimeScale.getSelectionModel().selectedItemProperty()))
                        .or(Bindings.equal(TimeScaleEnum.Week, this.horizonTimeScale.getSelectionModel().selectedItemProperty()));
        // 時間
        this.horizonTimeUnitLabel.managedProperty().bind(this.horizonTimeUnit.managedProperty());
        this.horizonTimeUnitLabel.visibleProperty().bind(this.horizonTimeUnit.visibleProperty());
        this.horizonTimeUnit.managedProperty().bind(bindShowTimeUnit);
        this.horizonTimeUnit.visibleProperty().bindBidirectional(this.horizonTimeUnit.managedProperty());
        this.horizonAutoScrollLabel.managedProperty().bind(this.horizonAutoScroll.managedProperty());
        this.horizonAutoScrollLabel.visibleProperty().bind(this.horizonAutoScroll.visibleProperty());
        this.horizonAutoScroll.managedProperty().bind(bindShowTimeUnit);
        this.horizonAutoScroll.visibleProperty().bindBidirectional(this.horizonAutoScroll.managedProperty());
        this.horizonAutoScrollUnitLabel.managedProperty().bind(this.horizonAutoScrollUnit.managedProperty());
        this.horizonAutoScrollUnitLabel.visibleProperty().bind(this.horizonAutoScrollUnit.visibleProperty());
        this.horizonAutoScrollUnit.managedProperty().bind(bindShowTimeUnit);
        this.horizonAutoScrollUnit.visibleProperty().bindBidirectional(this.horizonAutoScrollUnit.managedProperty());
        // 1日・半日
        this.horizonShowDaysLabel.managedProperty().bind(this.horizonShowDays.managedProperty());
        this.horizonShowDaysLabel.visibleProperty().bind(this.horizonShowDays.visibleProperty());
        this.horizonShowDays.managedProperty().bind(bindShowDays);
        this.horizonShowDays.visibleProperty().bindBidirectional(this.horizonShowDays.managedProperty());
        this.horizonShowHolidayLabel.managedProperty().bind(this.horizonShowHoliday.managedProperty());
        this.horizonShowHolidayLabel.visibleProperty().bind(this.horizonShowHoliday.visibleProperty());
        this.horizonShowHoliday.managedProperty().bind(bindShowDays);
        this.horizonShowHoliday.visibleProperty().bindBidirectional(this.horizonShowHoliday.managedProperty());
        // 1ヵ月・1週間
        this.horizonShowMonthsLabel.managedProperty().bind(this.horizonShowMonths.managedProperty());
        this.horizonShowMonthsLabel.visibleProperty().bind(this.horizonShowMonths.visibleProperty());
        this.horizonShowMonths.managedProperty().bind(bindShowMonths);
        this.horizonShowMonths.visibleProperty().bindBidirectional(this.horizonShowMonths.managedProperty());

        this.horizonColumnCountLabel.managedProperty().bind(this.horizonColumnCount.managedProperty());
        this.horizonColumnCountLabel.visibleProperty().bind(this.horizonColumnCount.visibleProperty());
        this.horizonColumnCount.managedProperty().bind(bindShowTimeUnit.not());
        this.horizonColumnCount.visibleProperty().bindBidirectional(this.horizonColumnCount.managedProperty());


        this.horizonShowOrderLabel.managedProperty().bind(this.showOrder.managedProperty());
        this.horizonShowOrderLabel.visibleProperty().bind(this.showOrder.visibleProperty());
        this.horizonShowOrder.managedProperty().bind(vob);
        this.horizonShowOrder.visibleProperty().bindBidirectional(this.horizonShowOrder.managedProperty());
        this.horizonShowOrder.setItems(FXCollections.observableArrayList(ShowOrder.values()));
        this.horizonShowOrder.setConverter(new StringConverter<ShowOrder>() {
            @Override
            public String toString(ShowOrder object) {
                return LocaleUtils.getString(object.getName());
            }

            @Override
            public ShowOrder fromString(String string) {
                return null;
            }
        });
        this.horizonContentType.setItems(FXCollections.observableArrayList(ContentTypeEnum.values()));
        this.horizonContentType.setConverter(new StringConverter<ContentTypeEnum>() {
            @Override
            public String toString(ContentTypeEnum object) {
                return LocaleUtils.getString(object.getName());
            }

            @Override
            public ContentTypeEnum fromString(String string) {
                return null;
            }
        });

        BooleanBinding bow =
                Bindings.or(
                        Bindings.or(
                                Bindings.equal(DisplayModeEnum.LINE, targetLine.getSelectionModel().selectedItemProperty()),
                                Bindings.equal(DisplayModeEnum.WORKER, targetLine.getSelectionModel().selectedItemProperty())),
                        Bindings.equal(DisplayModeEnum.KANBAN, targetLine.getSelectionModel().selectedItemProperty())
                );
        this.agendaDisplayPatternLabel.visibleProperty().bind(bow);
        this.agendaDisplayPatternLabel.managedProperty().bind(agendaDisplayPatternLabel.visibleProperty());

        this.agendaDisplayPattern.visibleProperty().bind(bow);
        this.agendaDisplayPattern.managedProperty().bind(agendaDisplayPattern.visibleProperty());
        this.agendaDisplayPattern.setItems(FXCollections.observableArrayList(AgendaDisplayPatternEnum.values()));
        this.agendaDisplayPattern.setConverter(new StringConverter<AgendaDisplayPatternEnum>() {
            @Override
            public String toString(AgendaDisplayPatternEnum object) {
                return LocaleUtils.getString(object.getName());
            }

            @Override
            public AgendaDisplayPatternEnum fromString(String string) {
                return null;
            }
        });


        this.horizonAgendaDisplayPatternLabel.visibleProperty().bind(bow);
        this.horizonAgendaDisplayPatternLabel.managedProperty().bind(horizonAgendaDisplayPatternLabel.visibleProperty());

        this.horizonAgendaDisplayPattern.visibleProperty().bind(bow);
        this.horizonAgendaDisplayPattern.managedProperty().bind(horizonAgendaDisplayPattern.visibleProperty());
        this.horizonAgendaDisplayPattern.setItems(FXCollections.observableArrayList(AgendaDisplayPatternEnum.values()));
        this.horizonAgendaDisplayPattern.setConverter(new StringConverter<AgendaDisplayPatternEnum>() {
            @Override
            public String toString(AgendaDisplayPatternEnum object) {
                return LocaleUtils.getString(object.getName());
            }

            @Override
            public AgendaDisplayPatternEnum fromString(String string) {
                return null;
            }
        });

        // 1日・半日・1週間
        this.horizonBeforeDaysLabel.managedProperty().bindBidirectional(this.horizonBeforeDays.managedProperty());
        this.horizonBeforeDaysLabel.visibleProperty().bindBidirectional(this.horizonBeforeDays.visibleProperty());
        this.horizonBeforeDays.managedProperty().bind(bindShowDaysandMonths);
        this.horizonBeforeDays.visibleProperty().bindBidirectional(this.horizonBeforeDays.managedProperty());
        this.horizonBeforeDays.setItems(FXCollections.observableArrayList(BeforeDaysEnum.values()));
        this.horizonBeforeDays.setConverter(new StringConverter<BeforeDaysEnum>() {
            @Override
            public String toString(BeforeDaysEnum object) {
                return BeforeDaysEnum.toString(object);
            }

            @Override
            public BeforeDaysEnum fromString(String string) {
                return null;
            }
        });

        this.horizonPlanActualShowType.setItems(FXCollections.observableArrayList(PlanActualShowTypeEnum.values()));
        this.horizonPlanActualShowType.setConverter(new StringConverter<PlanActualShowTypeEnum>() {
            @Override
            public String toString(PlanActualShowTypeEnum object) {
                return LocaleUtils.getString(object.getName());
            }

            @Override
            public PlanActualShowTypeEnum fromString(String string) {
                return null;
            }
        });

        // 予定のみ表示がONの場合、「応援者の実績を表示」と「進捗時間表示」を無効にして設定をOFFにする。
        this.planOnly.switchOnProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            this.displaySupportResultsLabel.setDisable(newValue);
            this.displaySupportResults.setDisable(newValue);
            this.showActualTimeLabel.setDisable(newValue);
            this.showActualTime.setDisable(newValue);
            if (newValue) {
                this.displaySupportResults.switchOnProperty().set(false);
                this.showActualTime.switchOnProperty().set(false);
            }
        });
        
        // 時間軸スケールが「1週間」か「1ヵ月」、または予実表示が「予定のみ」の場合、「応援者の実績を表示」を無効にして設定をOFFにする。
        this.horizonTimeScale.valueProperty().addListener((ObservableValue<? extends TimeScaleEnum> observable, TimeScaleEnum oldValue, TimeScaleEnum newValue) -> {
            this.checkDisableHorizonSupportResult();
            this.setUpdateIntervalLimit();
        });

        this.horizonPlanActualShowType.valueProperty().addListener((ObservableValue<? extends PlanActualShowTypeEnum> observable, PlanActualShowTypeEnum oldValue, PlanActualShowTypeEnum newValue) -> {
            this.checkDisableHorizonSupportResult();
        });
        
        // 予実表示が「予定のみ」の場合、「進捗時間表示」を無効にして設定をOFFにする。
        this.horizonPlanActualShowType.valueProperty().addListener((ObservableValue<? extends PlanActualShowTypeEnum> observable, PlanActualShowTypeEnum oldValue, PlanActualShowTypeEnum newValue) -> {
            this.checkDisableHorizonShowActualTime();
        });
        
        // 時間単位が横軸時のみ昼休憩は有効
        //BooleanBinding timeBindings = Bindings.equal(TimeScaleEnum.Time, this.horizonTimeScale.getSelectionModel().selectedItemProperty())
        //        .or(Bindings.notEqual(TimeAxisEnum.HorizonAxis,this.horizonTimeAxis.getSelectionModel().selectedItemProperty()));
        //breaktimeSetting.disableProperty().bind(timeBindings.not());

        // 昼休憩は払出状況時には非表示
        this.breaktimeSetting.managedProperty().bind(payoutBB.not());
        this.breaktimeSetting.visibleProperty().bind(this.breaktimeSetting.managedProperty());

        // 表示の開始時間・完了時間は日、半日、時間のみ有効
        BooleanBinding timeScaleBindings = Bindings.not(Bindings.equal(TimeScaleEnum.Time, this.horizonTimeScale.getSelectionModel().selectedItemProperty())
                .or(Bindings.equal(TimeScaleEnum.Day, this.horizonTimeScale.getSelectionModel().selectedItemProperty()))
                .or(Bindings.equal(TimeScaleEnum.HalfDay, this.horizonTimeScale.getSelectionModel().selectedItemProperty()))
                .or(Bindings.equal(TimeAxisEnum.VerticalAxis,this.horizonTimeAxis.getSelectionModel().selectedItemProperty())));

        this.startTimeLabel.disableProperty().bind(timeScaleBindings);
        this.startTime.disableProperty().bind(timeScaleBindings);
        this.endTimeLabel.disableProperty().bind(timeScaleBindings);
        this.endTime.disableProperty().bind(timeScaleBindings);
        
        this.payoutdisplayNumber.setItems(FXCollections.observableArrayList(IntStream.rangeClosed(MIN_MONITOR_NUMBER, MAX_MONITOR_NUMBER).boxed().collect(Collectors.toList())));
        

        // 表示の開始時間・完了時間は払出状況時には非表示
        this.startTimeLabel.managedProperty().bind(payoutBB.not());
        this.startTimeLabel.visibleProperty().bind(this.startTimeLabel.managedProperty());
        this.startTime.managedProperty().bind(payoutBB.not());
        this.startTime.visibleProperty().bind(this.startTime.managedProperty());
        this.endTimeLabel.managedProperty().bind(payoutBB.not());
        this.endTimeLabel.visibleProperty().bind(this.endTimeLabel.managedProperty());
        this.endTime.managedProperty().bind(payoutBB.not());
        this.endTime.visibleProperty().bind(this.endTime.managedProperty());



    }

    /**
     * 表示対象に表示する項目を取得する
     *
     * @param setting
     * @return ,でつながれた項目
     */
    private String getTargetName(AgendaMonitorSetting setting) {

        DisplayModeEnum displayModeEnum = setting.getMode();
        if (Objects.isNull(displayModeEnum)) {
            displayModeEnum = DisplayModeEnum.KANBAN;
        }

        switch (displayModeEnum) {
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
            case PRODUCT_PROGRESS:
                List<KanbanHierarchyInfoEntity> recv = CacheUtils.getCacheKanbanHierarchy(setting.getKanbanHierarchyIds());
                return getTargetName(recv.stream().map(KanbanHierarchyInfoEntity::getHierarchyName).collect(Collectors.toList()));
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

    /**
     * 時間軸スケールが「1週間」か「1ヵ月」、または予実表示が「予定のみ」の場合、「応援者の実績を表示」を無効にして設定をOFFにする。
     */
    private void checkDisableHorizonSupportResult() {
        boolean isDisableSupportResult = false;

        TimeScaleEnum timeScale = this.horizonTimeScale.getValue();
        PlanActualShowTypeEnum showType = this.horizonPlanActualShowType.getValue();

        // 時間軸スケールが「1週間」か「1ヵ月」、または予実表示が「予定のみ」の場合、「応援者の実績を表示」を無効にする。
        if (TimeScaleEnum.Week.equals(timeScale)
                || TimeScaleEnum.Month.equals(timeScale)) {
            isDisableSupportResult = true;
        } else if (PlanActualShowTypeEnum.PlanOnly.equals(showType)) {
            isDisableSupportResult = true;
        }

        this.horizonDisplaySupportResultsLabel.setDisable(isDisableSupportResult);
        this.horizonDisplaySupportResults.setDisable(isDisableSupportResult);

        // 「応援者の実績を表示」が無効の場合は設定をOFFにする。
        if (isDisableSupportResult) {
            this.horizonDisplaySupportResults.switchOnProperty().set(false);
        }

    }
    
    /**
     * 予実表示が「予定のみ」の場合、「進捗時間表示」を無効にして設定をOFFにする。
     */
    private void checkDisableHorizonShowActualTime() {
        boolean isDisableShowActualTime = false;
        
        PlanActualShowTypeEnum showType = this.horizonPlanActualShowType.getValue();

        // 予実表示が「予定のみ」の場合、「進捗時間表示」を無効にする。
        if (PlanActualShowTypeEnum.PlanOnly.equals(showType)) {
            isDisableShowActualTime = true;
        }

        this.horizonShowActualTimeLabel.setDisable(isDisableShowActualTime);
        this.horizonShowActualTime.setDisable(isDisableShowActualTime);

        // 「進捗時間表示」が無効の場合は設定をOFFにする。
        if (isDisableShowActualTime) {
            this.horizonShowActualTime.switchOnProperty().set(false);
        }
    }
    

    /**
     * 更新間隔の範囲を設定する。
     */
    private void setUpdateIntervalLimit() {
        TimeScaleEnum timeScale = this.horizonTimeScale.getValue();

        long minLimit;
        long maxLimit;

        if (TimeScaleEnum.Week.equals(timeScale)
                || TimeScaleEnum.Month.equals(timeScale)) {
            // 1週間・1ヵ月
            minLimit = Constants.AGENDA_MIN_INTERVAL_WEEKLY;
            maxLimit = Constants.AGENDA_MAX_INTERVAL_WEEKLY;
        } else {
            minLimit = Constants.AGENDA_MIN_INTERVAL;
            maxLimit = Constants.AGENDA_MAX_INTERVAL;
        }

        this.updateInterval.setMinLimit(minLimit);
        this.updateInterval.setMaxLimit(maxLimit);

        // 範囲外の値が設定されていた場合、上下限値に変更する。
        if (this.agenda.getUpdateInterval() < minLimit) {
            this.agenda.setUpdateInterval(minLimit);
        } else if (this.agenda.getUpdateInterval() > maxLimit) {
            this.agenda.setUpdateInterval(maxLimit);
        }
    }
}
