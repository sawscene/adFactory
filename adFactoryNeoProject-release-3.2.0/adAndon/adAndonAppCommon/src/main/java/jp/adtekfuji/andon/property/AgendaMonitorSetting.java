/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.property;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.andon.enumerate.*;

/**
 * アジェンダモニター設定
 *
 * @author fu-kato
 */
@XmlRootElement(name = "agenda")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AgendaMonitorSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    // モニター種別
    private ObjectProperty<AndonMonitorTypeEnum> monitorTypeProperty;
    
    // 表示条件プロパティ
    private ObjectProperty<LocalDateTime> targetDateProperty;// 対象日プロパティ
    private ObjectProperty<LocalTime> startWorkTimeProperty;// 開始時間プロパティ(hh:mm)
    private ObjectProperty<LocalTime> endWorkTimeProperty;// 終了時間プロパティ(hh:mm)
    private ObjectProperty<DisplayModeEnum> modeProperty;// 表示対象プロパティ
    private LongProperty updateIntervalProperty;// 更新間隔プロパティ(分)
    private StringProperty modelNameProperty;// モデル名プロパティ
    private LongProperty displayPeriodProperty;//表示期間
    private ObjectProperty<DisplayOrderEnum> displayOrderProperty;

    // 画面設定(縦軸)プロパティ
    private IntegerProperty displayNumberProperty;// (縦軸)ディスプレイ番号プロパティ
    private BooleanProperty fullScreenProperty;// (縦軸)フルスクリーン表示プロパティ
    private ObjectProperty<TimeAxisEnum> timeAxisProperty;// 時間軸プロパティ(横軸と共通)
    private IntegerProperty columnCountProperty;// (縦軸)カラム数プロパティ
    private ObjectProperty<ShowOrder> showOrderProperty;// (縦軸)表示順プロパティ
    private BooleanProperty visibleOnlyPlanProperty;// (縦軸)予定のみ表示プロパティ
    private BooleanProperty showActualTimeProperty;// (縦軸)進捗時間表示プロパティ
    private BooleanProperty displaySupportResultsProperty;// (縦軸)応援者の実績を表示プロパティ
    private ObjectProperty<ContentTypeEnum> contentProperty;// (縦軸)詳細表示プロパティ
    private IntegerProperty timeUnitProperty;// (縦軸)時間軸の表示単位プロパティ(分)
    private BooleanProperty togglePagesProperty;// (縦軸)ページ切り替えプロパティ
    private IntegerProperty pageToggleTimeProperty;// (縦軸)ページ切り替え間隔プロパティ(秒)
    private BooleanProperty autoScrollProperty;// (縦軸)自動スクロールプロパティ
    private ObjectProperty<LocalTime> scrollUnitProperty;// (縦軸)自動スクロール範囲プロパティ[HH:mm]
    private ObjectProperty<AgendaDisplayPatternEnum> agendaDisplayPatternProperty;//(縦軸)まとめて表示

    // Liteモニターのみの設定
    private IntegerProperty processDisplayColumnsProperty;// 表示列数
    private IntegerProperty processDisplayRowsProperty;// 表示行数
    private IntegerProperty processPageSwitchingIntervalProperty;// 工程ページ切替間隔(秒)
    private StringProperty callSoundSettingProperty;// 呼出音設定

    // 画面設定(横軸)プロパティ
    private IntegerProperty horizonDisplayNumberProperty;// (横軸)ディスプレイ番号プロパティ
    private BooleanProperty horizonFullScreenProperty;// (横軸)フルスクリーン表示プロパティ
    private ObjectProperty<TimeScaleEnum> horizonTimeScaleProperty;// (横軸)時間軸スケールプロパティ
    private IntegerProperty horizonTimeUnitProperty;// (横軸)時間軸の表示単位プロパティ(分)
    private ObjectProperty<BeforeDaysEnum> horizonBeforeDaysProperty;// (横軸)表示開始日プロパティ
    private IntegerProperty horizonShowDaysProperty;// (横軸)表示日数プロパティ
    private IntegerProperty horizonShowMonthsProperty;// (横軸)表示月数プロパティ
    private BooleanProperty horizonShowHolidayProperty;// (横軸)休日表示プロパティ
    private IntegerProperty horizonColumnCountProperty;// (横軸)カラム数プロパティ
    private ObjectProperty<ShowOrder> horizonShowOrderProperty;// (横軸)表示順プロパティ
    private IntegerProperty horizonRowHightProperty;// (横軸)行の高さプロパティ(ピクセル)
    private ObjectProperty<AgendaDisplayPatternEnum> horizonAgendaDisplayPatternProperty;//(横軸)まとめて表示
    private BooleanProperty horizonTogglePagesProperty;// (横軸)ページ切り替えプロパティ
    private IntegerProperty horizonPageToggleTimeProperty;// (横軸)ページ切り替え間隔プロパティ(秒)
    private BooleanProperty horizonAutoScrollProperty;// (横軸)自動スクロールプロパティ
    private ObjectProperty<LocalTime> horizonScrollUnitProperty;// (横軸)自動スクロール範囲プロパティ
    private ObjectProperty<PlanActualShowTypeEnum> horizonPlanActualShowTypeProperty;// (横軸)予実表示プロパティ
    private BooleanProperty horizonShowActualTimeProperty; // (横軸)進捗時間表示プロパティ
    private BooleanProperty horizonDisplaySupportResultsProperty;// (横軸)応援者の実績を表示プロパティ
    private ObjectProperty<ContentTypeEnum> horizonContentProperty;// (横軸)詳細表示プロパティ

    // フォントサイズプロパティ
    private DoubleProperty titleSizeProperty;// タイトルのフォントサイズ
    private DoubleProperty headerSizeProperty;// ヘッダーのフォントサイズ
    private DoubleProperty columnSizeProperty;// カラムのフォントサイズ
    private DoubleProperty itemSizeProperty;// アイテムのフォントサイズ
    private DoubleProperty zoomBarSizeProperty;// 拡大スライドバーのフォントサイズ

    // モニター種別
    private AndonMonitorTypeEnum monitorType;

    // 表示条件
    private LocalDateTime targetDate;// 対象日
    private LocalTime startWorkTime;// 開始時間(hh:mm)
    private LocalTime endWorkTime;// 終了時間(hh:mm)
    private DisplayModeEnum mode;// 表示対象
    private List<Long> kanbanIds = new ArrayList<>();// カンバンID一覧
    private List<Long> liteKanbanIds = new ArrayList<>();// LiteカンバンID一覧
    private List<Long> organizationIds = new ArrayList<>();// 組織ID一覧
    private List<Long> lineIds = new ArrayList<>();// ライン設備ID一覧
    private List<Long> kanbanHierarchyIds = new ArrayList<>(); // カンバン階層ID一覧
    private long updateInterval;// 更新間隔(分)
    private String modelName;// モデル名
    private long displayPeriod;//表示期間
    private DisplayOrderEnum displayOrder; // 表示順

    // 休憩時間
    private List<BreakTimeInfoEntity> breaktimes = new ArrayList<>();

    // 画面設定(縦軸)
    private int displayNumber;// (縦軸)ディスプレイ番号
    private boolean fullScreen;// (縦軸)フルスクリーン表示
    private TimeAxisEnum timeAxis; // 時間軸(横軸と共通)
    private int columnCount;// (縦軸)カラム数
    private ShowOrder showOrder;// (縦軸)表示順
    private boolean visibleOnlyPlan;// (縦軸)予定のみ表示
    private boolean showActualTime;// (縦軸)進捗時間表示
    private boolean displaySupportResults;// (縦軸)応援者の実績を表示
    private ContentTypeEnum content;// (縦軸)詳細表示
    private int timeUnit;// (縦軸)時間軸の表示単位(分)
    private boolean togglePages;// (縦軸)ページ切り替え
    private int pageToggleTime;// (縦軸)ページ切り替え間隔(秒)
    private boolean autoScroll;// (縦軸)自動スクロール
    private LocalTime scrollUnit;// (縦軸)自動スクロール範囲[HH:mm]
    private AgendaDisplayPatternEnum agendaDisplayPattern;//(縦軸)まとめて表示
    
    // Liteモニターのみの設定
    private int processDisplayColumns;// 表示列数
    private int processDisplayRows;// 表示行数
    private int processPageSwitchingInterval;// 工程ページ切替間隔(秒)
    private String callSoundSetting;// 呼出音設定

    // 画面設定(横軸)
    private int horizonDisplayNumber;// (横軸)ディスプレイ番号
    private boolean horizonFullScreen;// (横軸)フルスクリーン表示
    private TimeScaleEnum horizonTimeScale;// (横軸)時間軸スケール
    private int horizonTimeUnit;// (横軸)時間軸の表示単位(分)
    private BeforeDaysEnum beforeDays;// (横軸)表示開始日 
    private int horizonShowDays;// (横軸)表示日数
    private int horizonShowMonths;// (横軸)表示月数
    private boolean horizonShowHoliday;// (横軸)休日表示
    private int horizonColumnCount;// (横軸)カラム数
    private ShowOrder horizonShowOrder;// (横軸)表示順
    private int horizonRowHight;// (横軸)行の高さ(ピクセル)
    private AgendaDisplayPatternEnum horizonAgendaDisplayPattern;//(横軸)まとめて表示
    private boolean horizonTogglePages;// (横軸)ページ切り替え
    private int horizonPageToggleTime;// (横軸)ページ切り替え間隔(秒)
    private boolean horizonAutoScroll;// (横軸)自動スクロール
    private LocalTime horizonScrollUnit;// (横軸)自動スクロール範囲
    private PlanActualShowTypeEnum horizonPlanActualShowType;// (横軸)予実表示
    private boolean horizonShowActualTime;//(横軸)進捗時間表示
    private boolean horizonDisplaySupportResults;// (横軸)応援者の実績を表示
    private ContentTypeEnum horizonContent;// (横軸)詳細表示

    // 画面設定(払出状況)
    /** ディスプレイ番号 */
    private int payoutDisplayNumber;
    /** フルスクリーン表示 */
    private boolean payoutFullScreen;
    /** 払出完了の行数 */
    private int payoutCompleteLineCount;
    /** 払出待ちの行数 */
    private int payoutWaitingLineCount;
    /** ピッキング中の行数 */
    private int pickingLineCount;
    /** 受付の行数 */
    private int receptionLineCount;
    /** 払出完了の表示日数 */
    private int payoutCompleteDisplayDays;
    /** ページ切り替え間隔(秒) */
    private int pagingIntervalSeconds;
    /** フォントサイズ */
    private int fontSizePs;
    
    /** ディスプレイ番号プロパティ */
    private IntegerProperty payoutDisplayNumberProperty;
    /** フルスクリーン表示プロパティ */
    private BooleanProperty payoutFullScreenProperty;
    /** 払出完了の行数プロパティ */
    private IntegerProperty payoutCompleteLineCountProperty;
    /** 払出待ちの行数プロパティ */
    private IntegerProperty payoutWaitingLineCountProperty;
    /** ピッキング中の行数プロパティ */
    private IntegerProperty pickingLineCountProperty;
    /** 受付の行数プロパティ */
    private IntegerProperty receptionLineCountProperty;
    /** 払出完了の表示日数プロパティ */
    private IntegerProperty payoutCompleteDisplayDaysProperty;
    /** ページ切り替え間隔(秒)プロパティ */
    private IntegerProperty pagingIntervalSecondsProperty;
    /** フォントサイズプロパティ */
    private IntegerProperty fontSizePsProperty;
    
    
    // フォントサイズ
    private double titleSize;// タイトルのフォントサイズ
    private double headerSize;// ヘッダーのフォントサイズ
    private double columnSize;// カラムのフォントサイズ
    private double itemSize;// アイテムのフォントサイズ
    private double zoomBarSize;// 拡大スライドバーのフォントサイズ

    /**
     * コンストラクタ
     */
    public AgendaMonitorSetting() {
        // ここで初期値を設定する
        this.monitorType = AndonMonitorTypeEnum.LINE_PRODUCT;

        // 表示条件
        this.targetDate = LocalDateTime.now();// 対象日
        this.startWorkTime = LocalTime.of(8, 0);// 開始時間(hh:mm)
        this.endWorkTime = LocalTime.of(19, 0);// 終了時間(hh:mm)
        this.mode = DisplayModeEnum.LINE;// 表示対象
        this.updateInterval = 3;// 更新間隔(分)
        this.modelName = "";// モデル名
        this.displayPeriod = 0;//表示期間

        // 画面設定(縦軸)
        this.displayNumber = 1;// (縦軸)ディスプレイ番号
        this.fullScreen = true;// (縦軸)フルスクリーン表示
        this.timeAxis = TimeAxisEnum.getDefault();// 時間軸(横軸と共通)
        this.columnCount = 5;// (縦軸)カラム数
        this.showOrder = ShowOrder.getDefault();// (縦軸)表示順
        this.visibleOnlyPlan = false;// (縦軸)予定のみ表示
        this.showActualTime = false;// (縦軸)進捗時間表示
        this.displaySupportResults = false;// (縦軸)応援者の実績を表示
        this.content = ContentTypeEnum.WORKFLOW_NAME;// (縦軸)詳細表示
        this.timeUnit = 30;// (縦軸)時間軸の表示単位(分)
        this.togglePages = true;// (縦軸)ページ切り替え
        this.pageToggleTime = 10;// (縦軸)ページ切り替え間隔(秒)
        this.autoScroll = false;// (縦軸)自動スクロール
        this.scrollUnit = LocalTime.of(3, 0);// (縦軸)自動スクロール範囲[HH:mm]
        this.agendaDisplayPattern = AgendaDisplayPatternEnum.getDefault();//(縦軸)まとめて表示
        
        // Liteモニターのみの設定
        this.processDisplayColumns = 8;// 表示列数
        this.processDisplayRows = 8;// 表示列数
        this.processPageSwitchingInterval = 10;// 工程ページ切替間隔(秒)
        this.callSoundSetting = "";// 呼出音設定
        
        // 画面設定(横軸)
        this.horizonDisplayNumber = 1;// (横軸)ディスプレイ番号
        this.horizonFullScreen = true;// (横軸)フルスクリーン表示
        this.horizonTimeScale = TimeScaleEnum.getDefault();// (横軸)時間軸スケール
        this.beforeDays = BeforeDaysEnum.getDefault();// (横軸)表示開始日
        this.horizonTimeUnit = 30;// (横軸)時間軸の表示単位(分)
        this.horizonShowDays = 1;// (横軸)表示日数
        this.horizonShowMonths = 1;// (横軸)表示月数
        this.horizonShowHoliday = false;// (横軸)休日表示
        this.horizonColumnCount = 31;// (横軸)カラム数
        this.horizonShowOrder = ShowOrder.getDefault();// (横軸)表示順
        this.horizonRowHight = 100;// (横軸)行の高さ(ピクセル)
        this.horizonAgendaDisplayPattern = AgendaDisplayPatternEnum.getDefault();//(横軸)まとめて表示
        this.horizonTogglePages = true;// (横軸)ページ切り替え
        this.horizonPageToggleTime = 10;// (横軸)ページ切り替え間隔(秒)
        this.horizonAutoScroll = false;// (横軸)自動スクロール
        this.horizonScrollUnit = LocalTime.of(3, 0);// (横軸)自動スクロール範囲
        this.horizonPlanActualShowType = PlanActualShowTypeEnum.getDefault();// (横軸)予実表示
        this.horizonShowActualTime = false;// (横軸)進捗時間表示
        this.horizonDisplaySupportResults = false;// (横軸)応援者の実績を表示
        this.horizonContent = ContentTypeEnum.WORKFLOW_NAME;// (横軸)詳細表示

        // 画面設定(払出状況)
        this.payoutDisplayNumber = 1;
        this.payoutFullScreen = true;
        this.payoutCompleteLineCount = 2;
        this.payoutWaitingLineCount = 2;
        this.pickingLineCount = 1;
        this.receptionLineCount = 3;
        this.payoutCompleteDisplayDays = 1;
        this.pagingIntervalSeconds = 3;
        this.fontSizePs = 15;
        
        // フォントサイズ
        this.titleSize = 17.0;// タイトルのフォントサイズ
        this.headerSize = 17.0;// ヘッダーのフォントサイズ
        this.columnSize = 20.0;// カラムのフォントサイズ
        this.itemSize = 15.0;// アイテムのフォントサイズ
        this.zoomBarSize = 17.0;// 拡大スライドバーのフォントサイズ
    }

    /**
     * アジェンダモニター設定を生成する。
     *
     * @return アジェンダモニター設定
     */
    public static AgendaMonitorSetting create() {
        AgendaMonitorSetting setting = new AgendaMonitorSetting();
        return setting;
    }

    /**
     * 対象日プロパティを取得する。
     *
     * @return 対象日プロパティ
     */
    public ObjectProperty<LocalDateTime> targetDateProperty() {
        if (Objects.isNull(this.targetDateProperty)) {
            this.targetDateProperty = new SimpleObjectProperty<>(this.targetDate);
        }
        return this.targetDateProperty;
    }

    /**
     * 対象日を取得する。
     *
     * @return 対象日
     */
    public LocalDateTime getTargetDate() {
        if (Objects.nonNull(this.targetDateProperty)) {
            return this.targetDateProperty.get();
        }
        return this.targetDate;
    }

    /**
     * 対象日を設定する。
     *
     * @param targetDate 対象日
     */
    public void setTargetDate(LocalDateTime targetDate) {
        if (Objects.nonNull(this.targetDateProperty)) {
            this.targetDateProperty.set(targetDate);
        }
        this.targetDate = targetDate;
    }

    /**
     * モニター種別プロパティを取得する。
     *
     * @return モニター種別プロパティ
     */
    public ObjectProperty<AndonMonitorTypeEnum> monitorTypeProperty() {
        if (Objects.isNull(this.monitorTypeProperty)) {
            this.monitorTypeProperty = new SimpleObjectProperty<>(this.monitorType);
        }
        return this.monitorTypeProperty;
    }

    /**
     * モニター種別を取得する。
     *
     * @return モニター種別
     */
    public AndonMonitorTypeEnum getMonitorType() {
        if (Objects.nonNull(this.monitorTypeProperty)) {
            return this.monitorTypeProperty.get();
        }
        return this.monitorType;
    }

    /**
     * モニター種別を設定する。
     *
     * @param monitorType モニター種別
     */
    public void setMonitorType(AndonMonitorTypeEnum monitorType) {
        if (Objects.nonNull(this.monitorTypeProperty)) {
            this.monitorTypeProperty.set(monitorType);
        }
        this.monitorType = monitorType;
    }

    /**
     * 開始時間プロパティ(hh:mm)を取得する。
     *
     * @return 開始時間プロパティ(hh:mm)
     */
    public ObjectProperty<LocalTime> startWorkTimeProperty() {
        if (Objects.isNull(this.startWorkTimeProperty)) {
            this.startWorkTimeProperty = new SimpleObjectProperty<>(this.startWorkTime);
        }
        return this.startWorkTimeProperty;
    }

    /**
     * 開始時間(hh:mm)を取得する。
     *
     * @return 開始時間(hh:mm)
     */
    public LocalTime getStartWorkTime() {
        if (Objects.nonNull(this.startWorkTimeProperty)) {
            return this.startWorkTimeProperty.get();
        }
        return this.startWorkTime;
    }

    /**
     * 開始時間(hh:mm)を設定する。
     *
     * @param startWorkTime 開始時間(hh:mm)
     */
    public void setStartWorkTime(LocalTime startWorkTime) {
        if (Objects.nonNull(this.startWorkTimeProperty)) {
            this.startWorkTimeProperty.set(startWorkTime);
        }
        this.startWorkTime = startWorkTime;
    }

    /**
     * 終了時間プロパティ(hh:mm)を取得する。
     *
     * @return 終了時間プロパティ(hh:mm)
     */
    public ObjectProperty<LocalTime> endWorkTimeProperty() {
        if (Objects.isNull(this.endWorkTimeProperty)) {
            this.endWorkTimeProperty = new SimpleObjectProperty<>(this.endWorkTime);
        }
        return this.endWorkTimeProperty;
    }

    /**
     * 終了時間(hh:mm)を取得する。
     *
     * @return 終了時間(hh:mm)
     */
    public LocalTime getEndWorkTime() {
        if (Objects.nonNull(this.endWorkTimeProperty)) {
            return this.endWorkTimeProperty.get();
        }
        return this.endWorkTime;
    }

    /**
     * 終了時間(hh:mm)を設定する。
     *
     * @param endWorkTime 終了時間(hh:mm)
     */
    public void setEndWorkTime(LocalTime endWorkTime) {
        if (Objects.nonNull(this.endWorkTimeProperty)) {
            this.endWorkTimeProperty.set(endWorkTime);
        }
        this.endWorkTime = endWorkTime;
    }

    /**
     * 表示対象プロパティを取得する。
     *
     * @return 表示対象プロパティ
     */
    public ObjectProperty<DisplayModeEnum> modeProperty() {
        if (Objects.isNull(this.modeProperty)) {
            this.modeProperty = new SimpleObjectProperty<>(this.mode);
        }
        return this.modeProperty;
    }

    /**
     * 表示対象を取得する。
     *
     * @return 表示対象
     */
    public DisplayModeEnum getMode() {
        if (Objects.nonNull(this.modeProperty)) {
            return this.modeProperty.get();
        }
        return this.mode;
    }

    /**
     * 表示対象を設定する。
     *
     * @param mode 表示対象
     */
    public void setMode(DisplayModeEnum mode) {
        if (Objects.nonNull(this.modeProperty)) {
            this.modeProperty.set(mode);
        }
        this.mode = mode;
    }

    /**
     * カンバンID一覧を取得する。
     *
     * @return カンバンID一覧
     */
    @XmlElementWrapper(name = "kanbanIds")
    @XmlElement(name = "id")
    public List<Long> getKanbanIds() {
        return this.kanbanIds;
    }

    /**
     * カンバンID一覧を設定する。
     *
     * @param kanbanIds カンバンID一覧
     */
    public void setKanbanIds(List<Long> kanbanIds) {
        this.kanbanIds = kanbanIds;
    }

    /**
     * LiteカンバンID一覧を取得する。
     *
     * @return LiteカンバンID一覧
     */
    @XmlElementWrapper(name = "liteKanbanIds")
    @XmlElement(name = "id")
    public List<Long> getLiteKanbanIds() {
        return this.liteKanbanIds;
    }

    /**
     * カンバンID一覧を設定する。
     *
     * @param liteKanbanIds LiteカンバンID一覧
     */
    public void setLiteKanbanIds(List<Long> liteKanbanIds) {
        this.liteKanbanIds = liteKanbanIds;
    }

    /**
     * 組織ID一覧を取得する。
     *
     * @return 組織ID一覧
     */
    @XmlElementWrapper(name = "organizationIds")
    @XmlElement(name = "id")
    public List<Long> getOrganizationIds() {
        return this.organizationIds;
    }

    /**
     * 組織ID一覧を設定する。
     *
     * @param organizationIds 組織ID一覧
     */
    public void setOrganizationIds(List<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }

    /**
     * ライン設備ID一覧を取得する。
     *
     * @return ライン設備ID一覧
     */
    @XmlElementWrapper(name = "lineIds")
    @XmlElement(name = "id")
    public List<Long> getLineIds() {
        return this.lineIds;
    }

    /**
     * ライン設備ID一覧を設定する。
     *
     * @param lineIds ライン設備ID一覧
     */
    public void setLineIds(List<Long> lineIds) {
        this.lineIds = lineIds;
    }


    /**
     * カンバン階層ID一覧を取得する。
     *
     * @return カンバン階層ID一覧
     */
    @XmlElementWrapper(name = "kanbanHierarchyIds")
    @XmlElement(name = "id")
    public List<Long> getKanbanHierarchyIds() {
        return this.kanbanHierarchyIds;
    }

    /**
     * ライン設備ID一覧を設定する。
     *
     * @param kanbanHierarchyIds カンバン階層ID一覧
     */
    public void setKanbanHierarchyIds(List<Long> kanbanHierarchyIds) {
        this.kanbanHierarchyIds = kanbanHierarchyIds;
    }


    /**
     * 更新間隔プロパティ(分)を取得する。
     *
     * @return 更新間隔プロパティ(分)
     */
    public LongProperty updateIntervalProperty() {
        if (Objects.isNull(this.updateIntervalProperty)) {
            this.updateIntervalProperty = new SimpleLongProperty(this.updateInterval);
        }
        return this.updateIntervalProperty;
    }

    /**
     * 更新間隔(分)を取得する。
     *
     * @return 更新間隔(分)
     */
    public Long getUpdateInterval() {
        if (Objects.nonNull(this.updateIntervalProperty)) {
            return this.updateIntervalProperty.get();
        }
        return this.updateInterval;
    }

    /**
     * 更新間隔(分)を設定する。
     *
     * @param updateInterval 更新間隔(分)
     */
    public void setUpdateInterval(Long updateInterval) {
        if (Objects.nonNull(this.updateIntervalProperty)) {
            this.updateIntervalProperty.set(updateInterval);
        }
        this.updateInterval = updateInterval;
    }

    /**
     * モデル名プロパティを取得する。
     *
     * @return モデル名プロパティ
     */
    public StringProperty modelNameProperty() {
        if (Objects.isNull(this.modelNameProperty)) {
            this.modelNameProperty = new SimpleStringProperty(this.modelName);
        }
        return this.modelNameProperty;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        if (Objects.nonNull(modelNameProperty)) {
            return this.modelNameProperty.get();
        }
        return this.modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        if (Objects.nonNull(this.modelNameProperty)) {
            this.modelNameProperty.set(modelName);
        }
        this.modelName = modelName;
    }

    /**
     * 表示期間
     * @return 表示期間プロパティ
     */
    public LongProperty displayPeriodProperty() {
        if(Objects.isNull(this.displayPeriodProperty)) {
            this.displayPeriodProperty = new SimpleLongProperty(this.displayPeriod);
        }
        return this.displayPeriodProperty;
    }

    /**
     * 表示期間
     * @return 表示期間
     */
    public long getDisplayPeriod() {
        if(Objects.nonNull(this.displayPeriodProperty)) {
            return this.displayPeriodProperty.get();
        }
        return displayPeriod;
    }


    /**
     * 表示期間
     * @param displayPeriod 表示期間
     */
    public void setDisplayPeriod(long displayPeriod) {
        if(Objects.nonNull(this.displayPeriodProperty)) {
            this.displayPeriodProperty.set(displayPeriod);
        }
        this.displayPeriod = displayPeriod;
    }

    /**
     * 表示順
     * @return 表示順
     */
    public ObjectProperty<DisplayOrderEnum> displayOrderProperty() {
        if(Objects.isNull(this.displayOrderProperty)) {
            this.displayOrderProperty = new SimpleObjectProperty<>(this.displayOrder);
        }
        return this.displayOrderProperty;
    }

    /**
     * 表示順
     * @return 表示順
     */
    public DisplayOrderEnum getDisplayOrder() {
        if (Objects.nonNull(this.displayOrderProperty)) {
            return this.displayOrderProperty.get();
        }
        return displayOrder;
    }

    /**
     * 表示順
     * @param displayOrder 表示順
     */
    public void setDisplayOrder(DisplayOrderEnum displayOrder) {
        if (Objects.nonNull(this.displayOrderProperty)) {
            this.displayOrderProperty.set(displayOrder);
        }
        this.displayOrder = displayOrder;
    }

    /**
     * 休憩時間一覧を取得する。
     *
     * @return 休憩時間一覧
     */
    @XmlElementWrapper(name = "breaktimes")
    @XmlElement(name = "breaktime")
    public List<BreakTimeInfoEntity> getBreaktimes() {
        return this.breaktimes;
    }

    /**
     * 休憩時間一覧を設定する。
     *
     * @param breaktimes 休憩時間一覧
     */
    public void setBreaktimes(List<BreakTimeInfoEntity> breaktimes) {
        this.breaktimes = breaktimes;
    }

    /**
     * (縦軸)ディスプレイ番号プロパティを取得する。
     *
     * @return (縦軸)ディスプレイ番号プロパティ
     */
    public IntegerProperty displayNumberProperty() {
        if (Objects.isNull(this.displayNumberProperty)) {
            this.displayNumberProperty = new SimpleIntegerProperty(this.displayNumber);
        }
        return this.displayNumberProperty;
    }

    /**
     * (縦軸)ディスプレイ番号を取得する。
     *
     * @return (縦軸)ディスプレイ番号
     */
    public int getDisplayNumber() {
        if (Objects.nonNull(this.displayNumberProperty)) {
            return this.displayNumberProperty.get();
        }
        return this.displayNumber;
    }

    /**
     * (縦軸)ディスプレイ番号を設定する。
     *
     * @param displayNumber (縦軸)ディスプレイ番号
     */
    public void setDisplayNumber(int displayNumber) {
        if (Objects.nonNull(this.displayNumberProperty)) {
            this.displayNumberProperty.set(displayNumber);
        }
        this.displayNumber = displayNumber;
    }

    /**
     * (縦軸)フルスクリーン表示プロパティを取得する。
     *
     * @return (縦軸)フルスクリーン表示プロパティ
     */
    public BooleanProperty fullScreenProperty() {
        if (Objects.isNull(this.fullScreenProperty)) {
            this.fullScreenProperty = new SimpleBooleanProperty(this.fullScreen);
        }
        return this.fullScreenProperty;
    }

    /**
     * (縦軸)フルスクリーン表示を取得する。
     *
     * @return (縦軸)フルスクリーン表示
     */
    public boolean getFullScreen() {
        if (Objects.nonNull(this.fullScreenProperty)) {
            return this.fullScreenProperty.get();
        }
        return this.fullScreen;
    }

    /**
     * (縦軸)フルスクリーン表示を設定する。
     *
     * @param fullScreen (縦軸)フルスクリーン表示
     */
    public void setFullScreen(boolean fullScreen) {
        if (Objects.nonNull(this.fullScreenProperty)) {
            this.fullScreenProperty.set(fullScreen);
        }
        this.fullScreen = fullScreen;
    }

    /**
     * 時間軸プロパティを取得する。(横軸と共通)
     *
     * @return 時間軸プロパティ
     */
    public ObjectProperty timeAxisProperty() {
        if (Objects.isNull(this.timeAxisProperty)) {
            this.timeAxisProperty = new SimpleObjectProperty<>(this.timeAxis);
        }
        return this.timeAxisProperty;
    }

    /**
     * 時間軸を取得する。
     *
     * @return 時間軸
     */
    public TimeAxisEnum getTimeAxis() {
        if (Objects.nonNull(this.timeAxisProperty)) {
            return this.timeAxisProperty.get();
        }
        return this.timeAxis;
    }

    /**
     * 時間軸を設定する。
     *
     * @param timeAxis 時間軸
     */
    public void setTimeAxis(TimeAxisEnum timeAxis) {
        if (Objects.nonNull(this.timeAxisProperty)) {
            this.timeAxisProperty.set(timeAxis);
        } else {
            this.timeAxis = timeAxis;
        }
    }

    /**
     * (縦軸)カラム数プロパティを取得する。
     *
     * @return (縦軸)カラム数プロパティ
     */
    public IntegerProperty columnCountProperty() {
        if (Objects.isNull(this.columnCountProperty)) {
            this.columnCountProperty = new SimpleIntegerProperty(this.columnCount);
        }
        return this.columnCountProperty;
    }

    /**
     * (縦軸)カラム数を取得する。
     *
     * @return (縦軸)カラム数
     */
    public int getColumnCount() {
        if (Objects.nonNull(this.columnCountProperty)) {
            return this.columnCountProperty.get();
        }
        return this.columnCount;
    }

    /**
     * (縦軸)カラム数を設定する。
     *
     * @param columnCount (縦軸)カラム数
     */
    public void setColumnCount(int columnCount) {
        if (Objects.nonNull(this.columnCountProperty)) {
            this.columnCountProperty.set(columnCount);
        }
        this.columnCount = columnCount;
    }

    /**
     * (縦軸)表示順プロパティを取得する。
     *
     * @return (縦軸)表示順プロパティ
     */
    public ObjectProperty showOrderProperty() {
        if (Objects.isNull(this.showOrderProperty)) {
            this.showOrderProperty = new SimpleObjectProperty<>(this.showOrder);
        }
        return this.showOrderProperty;
    }

    /**
     * (縦軸)表示順を取得する。
     *
     * @return　(縦軸)表示順
     */
    public ShowOrder getShowOrder() {
        if (Objects.nonNull(this.showOrderProperty)) {
            return this.showOrderProperty.get();
        }
        return this.showOrder;
    }

    /**
     * (縦軸)表示順を設定する。
     *
     * @param order (縦軸)表示順
     */
    public void setShowOrder(ShowOrder order) {
        if (Objects.nonNull(this.showOrderProperty)) {
            this.showOrderProperty.set(order);
        }
        this.showOrder = order;
    }

    /**
     * (縦軸)予定のみ表示プロパティを取得する。
     *
     * @return (縦軸)予定のみ表示プロパティ
     */
    public BooleanProperty visibleOnlyPlanProperty() {
        if (Objects.isNull(this.visibleOnlyPlanProperty)) {
            this.visibleOnlyPlanProperty = new SimpleBooleanProperty(this.visibleOnlyPlan);
        }
        return this.visibleOnlyPlanProperty;
    }

    /**
     * (縦軸)予定のみ表示を取得する。
     *
     * @return (縦軸)予定のみ表示
     */
    public boolean getVisibleOnlyPlan() {
        if (Objects.nonNull(this.visibleOnlyPlanProperty)) {
            return this.visibleOnlyPlanProperty.get();
        }
        return this.visibleOnlyPlan;
    }

    /**
     * (縦軸)予定のみ表示を設定する。
     *
     * @param visibleOnlyPlan (縦軸)予定のみ表示
     */
    public void setVisibleOnlyPlan(boolean visibleOnlyPlan) {
        if (Objects.nonNull(this.visibleOnlyPlanProperty)) {
            this.visibleOnlyPlanProperty.set(visibleOnlyPlan);
        }
        this.visibleOnlyPlan = visibleOnlyPlan;
    }
    
    /**
     * (縦軸)進捗時間表示プロパティを取得する。
     * @return (縦軸)進捗時間表示プロパティ
     */
    public BooleanProperty showActualTimeProperty() {
        if (Objects.isNull(this.showActualTimeProperty)) {
            this.showActualTimeProperty = new SimpleBooleanProperty(this.showActualTime);
        }
        return this.showActualTimeProperty;
    }

    /**
     * (縦軸)進捗時間表示を取得する
     * @return (縦軸)進捗時間表示
     */
    public boolean getShowActualTime() {
        if (Objects.nonNull(this.showActualTimeProperty)) {
            return this.showActualTimeProperty.get();
        }
        return this.showActualTime;
    }

    /**
     * (縦軸)進捗時間表示を設定する
     * @param val (縦軸)進捗時間表示
     */
    public void setShowActualTime(boolean val) {
        if (Objects.nonNull(this.showActualTimeProperty)) {
            this.showActualTimeProperty.set(val);
        }
        this.showActualTime = val;
    }

    /**
     * (縦軸)応援者の実績を表示プロパティを取得する。
     *
     * @return (縦軸)応援者の実績を表示プロパティ (true:表示, false:非表示)
     */
    public BooleanProperty displaySupportResultsProperty() {
        if (Objects.isNull(this.displaySupportResultsProperty)) {
            this.displaySupportResultsProperty = new SimpleBooleanProperty(this.displaySupportResults);
        }
        return this.displaySupportResultsProperty;
    }

    /**
     * (縦軸)応援者の実績を表示を取得する。
     *
     * @return (縦軸)応援者の実績を表示 (true:表示, false:非表示)
     */
    public boolean getDisplaySupportResults() {
        if (Objects.nonNull(this.displaySupportResultsProperty)) {
            return this.displaySupportResultsProperty.get();
        }
        return this.displaySupportResults;
    }

    /**
     * (縦軸)応援者の実績を表示を設定する。
     *
     * @param displaySupportResults (縦軸)応援者の実績を表示 (true:表示, false:非表示)
     */
    public void setDisplaySupportResults(boolean displaySupportResults) {
        if (Objects.nonNull(this.displaySupportResultsProperty)) {
            this.displaySupportResultsProperty.set(displaySupportResults);
        }
        this.displaySupportResults = displaySupportResults;
    }

    /**
     * (縦軸)詳細表示プロパティを取得する。
     *
     * @return (縦軸)詳細表示プロパティ
     */
    public ObjectProperty contentProperty() {
        if (Objects.isNull(this.contentProperty)) {
            this.contentProperty = new SimpleObjectProperty(this.content);
        }
        return this.contentProperty;
    }

    /**
     * (縦軸)詳細表示を取得する。
     *
     * @return (縦軸)詳細表示
     */
    public ContentTypeEnum getContent() {
        if (Objects.nonNull(this.contentProperty)) {
            return this.contentProperty.get();
        }
        return this.content;
    }

    /**
     * (縦軸)詳細表示を設定する。
     *
     * @param content (縦軸)詳細表示
     */
    public void setContent(ContentTypeEnum content) {
        if (Objects.nonNull(this.contentProperty)) {
            this.contentProperty.set(content);
        }
        this.content = content;
    }

    /**
     * (縦軸)時間軸の表示単位プロパティ(分)を取得する。
     *
     * @return (縦軸)時間軸の表示単位プロパティ(分)
     */
    public IntegerProperty timeUnitProperty() {
        if (Objects.isNull(this.timeUnitProperty)) {
            this.timeUnitProperty = new SimpleIntegerProperty(this.timeUnit);
        }
        return this.timeUnitProperty;
    }

    /**
     * (縦軸)時間軸の表示単位(分)を取得する。
     *
     * @return (縦軸)時間軸の表示単位(分)
     */
    public int getTimeUnit() {
        if (Objects.nonNull(this.timeUnitProperty)) {
            return this.timeUnitProperty.get();
        }
        return this.timeUnit;
    }

    /**
     * (縦軸)時間軸の表示単位(分)を設定する。
     *
     * @param timeUnit (縦軸)時間軸の表示単位(分)
     */
    public void setTimeUnit(int timeUnit) {
        if (Objects.nonNull(this.timeUnitProperty)) {
            this.timeUnitProperty.set(timeUnit);
        }
        this.timeUnit = timeUnit;
    }

    /**
     * (縦軸)ページ切り替えプロパティを取得する。
     *
     * @return (縦軸)ページ切り替えプロパティ
     */
    public BooleanProperty togglePagesProperty() {
        if (Objects.isNull(this.togglePagesProperty)) {
            this.togglePagesProperty = new SimpleBooleanProperty(this.togglePages);
        }
        return this.togglePagesProperty;
    }

    /**
     * (縦軸)ページ切り替えを取得する。
     *
     * @return (縦軸)ページ切り替え
     */
    public boolean getTogglePages() {
        if (Objects.nonNull(this.togglePagesProperty)) {
            return this.togglePagesProperty.get();
        }
        return this.togglePages;
    }

    /**
     * (縦軸)ページ切り替えを設定する。
     *
     * @param togglePages (縦軸)ページ切り替え
     */
    public void setTogglePages(boolean togglePages) {
        if (Objects.nonNull(this.togglePagesProperty)) {
            this.togglePagesProperty.set(togglePages);
        }
        this.togglePages = togglePages;
    }

    /**
     * (縦軸)ページ切り替え間隔プロパティ(秒)を取得する。
     *
     * @return (縦軸)ページ切り替え間隔プロパティ(秒)
     */
    public IntegerProperty pageToggleTimeProperty() {
        if (Objects.isNull(this.pageToggleTimeProperty)) {
            this.pageToggleTimeProperty = new SimpleIntegerProperty(this.pageToggleTime);
        }
        return this.pageToggleTimeProperty;
    }

    /**
     * (縦軸)ページ切り替え間隔(秒)を取得する。
     *
     * @return (縦軸)ページ切り替え間隔(秒)
     */
    public int getPageToggleTime() {
        if (Objects.nonNull(this.pageToggleTimeProperty)) {
            return this.pageToggleTimeProperty.get();
        }
        return this.pageToggleTime;
    }

    /**
     * (縦軸)ページ切り替え間隔(秒)を設定する。
     *
     * @param pageToggleTime (縦軸)ページ切り替え間隔(秒)
     */
    public void setPageToggleTime(int pageToggleTime) {
        if (Objects.nonNull(this.pageToggleTimeProperty)) {
            this.pageToggleTimeProperty.set(pageToggleTime);
        }
        this.pageToggleTime = pageToggleTime;
    }

    /**
     * (縦軸)自動スクロールプロパティを取得する。
     *
     * @return (縦軸)自動スクロールプロパティ
     */
    public BooleanProperty autoScrollProperty() {
        if (Objects.isNull(this.autoScrollProperty)) {
            this.autoScrollProperty = new SimpleBooleanProperty(this.autoScroll);
        }
        return this.autoScrollProperty;
    }

    /**
     * (縦軸)自動スクロールを取得する。
     *
     * @return (縦軸)自動スクロール
     */
    public boolean getAutoScroll() {
        if (Objects.nonNull(this.autoScrollProperty)) {
            return this.autoScrollProperty.get();
        }
        return this.autoScroll;
    }

    /**
     * (縦軸)自動スクロールを設定する。
     *
     * @param autoScroll (縦軸)自動スクロール
     */
    public void setAutoScroll(boolean autoScroll) {
        if (Objects.nonNull(this.autoScrollProperty)) {
            this.autoScrollProperty.set(autoScroll);
        }
        this.autoScroll = autoScroll;
    }

    /**
     * (縦軸)自動スクロール範囲プロパティ[HH:mm]を取得する。
     *
     * @return (縦軸)自動スクロール範囲プロパティ[HH:mm]
     */
    public ObjectProperty scrollUnitProperty() {
        if (Objects.isNull(this.scrollUnitProperty)) {
            this.scrollUnitProperty = new SimpleObjectProperty(this.scrollUnit);
        }
        return this.scrollUnitProperty;
    }

    /**
     * (縦軸)自動スクロール範囲[HH:mm]を取得する。
     *
     * @return (縦軸)自動スクロール範囲[HH:mm]
     */
    public LocalTime getScrollUnit() {
        if (Objects.nonNull(this.scrollUnitProperty)) {
            return this.scrollUnitProperty.get();
        }
        return this.scrollUnit;
    }

    /**
     * (縦軸)自動スクロール範囲[HH:mm]を設定する。
     *
     * @param scrollUnit (縦軸)自動スクロール範囲[HH:mm]
     */
    public void setScrollUnit(LocalTime scrollUnit) {
        if (Objects.nonNull(this.scrollUnitProperty)) {
            this.scrollUnitProperty.set(scrollUnit);
        }
        this.scrollUnit = scrollUnit;
    }

    /**
     * 表示列数プロパティを取得する。
     *
     * @return 表示列数プロパティ
     */
    public IntegerProperty processDisplayColumnsProperty() {
        if (Objects.isNull(this.processDisplayColumnsProperty)) {
            this.processDisplayColumnsProperty = new SimpleIntegerProperty(this.processDisplayColumns);
        }
        return this.processDisplayColumnsProperty;
    }

    /**
     * 表示列数を取得する。
     *
     * @return 表示列数
     */
    public int getProcessDisplayColumns() {
        if (Objects.nonNull(this.processDisplayColumnsProperty)) {
            return this.processDisplayColumnsProperty.get();
        }
        return this.processDisplayColumns;
    }

    /**
     * 表示列数を設定する。
     *
     * @param processDisplayColumns 表示列数
     */
    public void setProcessDisplayColumns(int processDisplayColumns) {
        if (Objects.nonNull(this.processDisplayColumnsProperty)) {
            this.processDisplayColumnsProperty.set(processDisplayColumns);
        }
        this.processDisplayColumns = processDisplayColumns;
    }

    /**
     * 表示行数プロパティを取得する。
     *
     * @return 表示列数プロパティ
     */
    public IntegerProperty processDisplayRowsProperty() {
        if (Objects.isNull(this.processDisplayRowsProperty)) {
            this.processDisplayRowsProperty = new SimpleIntegerProperty(this.processDisplayRows);
        }
        return this.processDisplayRowsProperty;
    }

    /**
     * 表示行数を取得する。
     *
     * @retur 表示行数
     */
    public int getProcessDisplayRows() {
        if (Objects.nonNull(this.processDisplayRowsProperty)) {
            return this.processDisplayRowsProperty.get();
        }
        return this.processDisplayRows;
    }

    /**
     * 表示行数を設定する。
     *
     * @param processDisplayRows 表示行数
     */
    public void setProcessDisplayRows(int processDisplayRows) {
        if (Objects.nonNull(this.processDisplayRowsProperty)) {
            this.processDisplayRowsProperty.set(processDisplayRows);
        }
        this.processDisplayRows = processDisplayRows;
    }

    /**
     * 工程ページ切替間隔プロパティを取得する。
     *
     * @return 工程ページ切替間隔プロパティ
     */
    public IntegerProperty processPageSwitchingIntervalProperty() {
        if (Objects.isNull(this.processPageSwitchingIntervalProperty)) {
            this.processPageSwitchingIntervalProperty = new SimpleIntegerProperty(this.processPageSwitchingInterval);
        }
        return this.processPageSwitchingIntervalProperty;
    }

    /**
     * 工程ページ切替間隔を取得する。
     *
     * @return 工程ページ切替間隔
     */
    public int getProcessPageSwitchingInterval() {
        if (Objects.nonNull(this.processPageSwitchingIntervalProperty)) {
            return this.processPageSwitchingIntervalProperty.get();
        }
        return this.processPageSwitchingInterval;
    }

    /**
     * 工程ページ切替間隔を設定する。
     *
     * @param processPageSwitchingInterval 工程ページ切替間隔
     */
    public void setProcessPageSwitchingInterval(int processPageSwitchingInterval) {
        if (Objects.nonNull(this.processPageSwitchingIntervalProperty)) {
            this.processPageSwitchingIntervalProperty.set(processPageSwitchingInterval);
        }
        this.processPageSwitchingInterval = processPageSwitchingInterval;
    }

    /**
     * 呼出音設定プロパティを取得する。
     *
     * @return 呼出音設定プロパティ
     */
    public StringProperty callSoundSettingProperty() {
        if (Objects.isNull(this.callSoundSettingProperty)) {
            this.callSoundSettingProperty = new SimpleStringProperty(this.callSoundSetting);
        }
        return this.callSoundSettingProperty;
    }

    /**
     * 呼出音設定を取得する。
     *
     * @return 呼出音設定
     */
    public String getCallSoundSetting() {
        if (Objects.nonNull(this.callSoundSettingProperty)) {
            return this.callSoundSettingProperty.get();
        }
        return this.callSoundSetting;
    }

    /**
     * 呼出音設定を設定する。
     *
     * @param callSoundSetting 呼出音設定
     */
    public void setCallSoundSetting(String callSoundSetting) {
        if (Objects.nonNull(this.callSoundSettingProperty)) {
            this.callSoundSettingProperty.set(callSoundSetting);
        }
        this.callSoundSetting = callSoundSetting;
    }

    /**
     * (横軸)ディスプレイ番号プロパティを取得する。
     *
     * @return (横軸)ディスプレイ番号プロパティ
     */
    public IntegerProperty horizonDisplayNumberProperty() {
        if (Objects.isNull(this.horizonDisplayNumberProperty)) {
            this.horizonDisplayNumberProperty = new SimpleIntegerProperty(this.horizonDisplayNumber);
        }
        return this.horizonDisplayNumberProperty;
    }

    /**
     * (横軸)ディスプレイ番号を取得する。
     *
     * @return (横軸)ディスプレイ番号
     */
    public int getHorizonDisplayNumber() {
        if (Objects.nonNull(this.horizonDisplayNumberProperty)) {
            return this.horizonDisplayNumberProperty.get();
        }
        return this.horizonDisplayNumber;
    }

    /**
     * (横軸)ディスプレイ番号を設定する。
     *
     * @param horizonDisplayNumber (横軸)ディスプレイ番号
     */
    public void setHorizonDisplayNumber(int horizonDisplayNumber) {
        if (Objects.nonNull(this.horizonDisplayNumberProperty)) {
            this.horizonDisplayNumberProperty.set(horizonDisplayNumber);
        }
        this.horizonDisplayNumber = horizonDisplayNumber;
    }

    /**
     * (横軸)フルスクリーン表示プロパティを取得する。
     *
     * @return (横軸)フルスクリーン表示プロパティ
     */
    public BooleanProperty horizonFullScreenProperty() {
        if (Objects.isNull(this.horizonFullScreenProperty)) {
            this.horizonFullScreenProperty = new SimpleBooleanProperty(this.horizonFullScreen);
        }
        return this.horizonFullScreenProperty;
    }

    /**
     * (横軸)フルスクリーン表示を取得する。
     *
     * @return (横軸)フルスクリーン表示
     */
    public boolean getHorizonFullScreen() {
        if (Objects.nonNull(this.horizonFullScreenProperty)) {
            return this.horizonFullScreenProperty.get();
        }
        return this.horizonFullScreen;
    }

    /**
     * (横軸)フルスクリーン表示を設定する。
     *
     * @param horizonFullScreen (横軸)フルスクリーン表示
     */
    public void setHorizonFullScreen(boolean horizonFullScreen) {
        if (Objects.nonNull(this.horizonFullScreenProperty)) {
            this.horizonFullScreenProperty.set(horizonFullScreen);
        }
        this.horizonFullScreen = horizonFullScreen;
    }

    /**
     * 時間軸スケールを取得する。
     *
     * @return 時間軸スケール
     */
    public TimeScaleEnum getTimeScale() {
        switch (this.timeAxis) {
            case HorizonAxis:
                return this.getHorizonTimeScale();
            case VerticalAxis:
            default:
                return TimeScaleEnum.Time;
        }
    }

    /**
     * (横軸)時間軸スケールプロパティを取得する。
     *
     * @return (横軸)時間軸スケールプロパティ
     */
    public ObjectProperty horizonTimeScaleProperty() {
        if (Objects.isNull(this.horizonTimeScaleProperty)) {
            this.horizonTimeScaleProperty = new SimpleObjectProperty<>(this.horizonTimeScale);
        }
        return this.horizonTimeScaleProperty;
    }

    /**
     * (横軸)時間軸スケールを取得する。
     *
     * @return (横軸)時間軸スケール
     */
    public TimeScaleEnum getHorizonTimeScale() {
        if (Objects.nonNull(this.horizonTimeScaleProperty)) {
            return this.horizonTimeScaleProperty.get();
        }
        return this.horizonTimeScale;
    }

    /**
     * (横軸)時間軸スケールを設定する。
     *
     * @param timeScale (横軸)時間軸スケール
     */
    public void setHorizonTimeScale(TimeScaleEnum timeScale) {
        if (Objects.nonNull(this.horizonTimeScaleProperty)) {
            this.horizonTimeScaleProperty.set(timeScale);
        }
        this.horizonTimeScale = timeScale;
    }

    /**
     * (横軸)時間軸の表示単位プロパティ(分)を取得する。
     *
     * @return (横軸)時間軸の表示単位プロパティ(分)
     */
    public IntegerProperty horizonTimeUnitProperty() {
        if (Objects.isNull(this.horizonTimeUnitProperty)) {
            this.horizonTimeUnitProperty = new SimpleIntegerProperty(this.horizonTimeUnit);
        }
        return this.horizonTimeUnitProperty;
    }

    /**
     * (横軸)時間軸の表示単位(分)を取得する。
     *
     * @return (横軸)時間軸の表示単位(分)
     */
    public int getHorizonTimeUnit() {
        if (Objects.nonNull(this.horizonTimeUnitProperty)) {
            return this.horizonTimeUnitProperty.get();
        }
        return this.horizonTimeUnit;
    }

    /**
     * (横軸)時間軸の表示単位(分)を設定する。
     *
     * @param horizonTimeUnit (横軸)時間軸の表示単位(分)
     */
    public void setHorizonTimeUnit(int horizonTimeUnit) {
        if (Objects.nonNull(this.horizonTimeUnitProperty)) {
            this.horizonTimeUnitProperty.set(horizonTimeUnit);
        }
        this.horizonTimeUnit = horizonTimeUnit;
    }
	
    /**
     * 表示開始日を設定する。
     * 
     * @return 表示開始日
     */
    public ObjectProperty horizonBeforeDaysProperty() {
        if (Objects.isNull(horizonBeforeDaysProperty)) {
            horizonBeforeDaysProperty = new SimpleObjectProperty<>(beforeDays);
        }
        return horizonBeforeDaysProperty;
    }

    /**
     * 表示開始日を取得する。
     *
     * @return 表示開始日
     */
    public BeforeDaysEnum getHorizonBeforeDays() {
        if (Objects.nonNull(this.horizonBeforeDaysProperty)) {
            return this.horizonBeforeDaysProperty.get();
        }
        return this.beforeDays;
    }

    /**
     * 表示開始日を取得する。
     *
     * @param beforeDays 表示開始日
     */
    public void setHorizonBeforeDays(BeforeDaysEnum beforeDays){
        if (Objects.nonNull(this.horizonBeforeDaysProperty)) {
            this.horizonBeforeDaysProperty.set(beforeDays);
        }
        this.beforeDays = beforeDays;
    }


    /**
     * 表示日数を取得する。
     *
     * @return 表示日数
     */
    public int getShowDays() {
        switch (this.timeAxis) {
            case HorizonAxis:
                return this.getHorizonShowDays();
            case VerticalAxis:
            default:
                return 1;
        }
    }

    /**
     * (横軸)表示日数プロパティを取得する。
     *
     * @return (横軸)表示日数プロパティ
     */
    public IntegerProperty horizonShowDaysProperty() {
        if (Objects.isNull(this.horizonShowDaysProperty)) {
            this.horizonShowDaysProperty = new SimpleIntegerProperty(this.horizonShowDays);
        }
        return this.horizonShowDaysProperty;
    }

    /**
     * (横軸)表示日数を取得する。
     *
     * @return (横軸)表示日数
     */
    public int getHorizonShowDays() {
        if (Objects.nonNull(this.horizonShowDaysProperty)) {
            return this.horizonShowDaysProperty.get();
        }
        return this.horizonShowDays;
    }

    /**
     * (横軸)表示日数を設定する。
     *
     * @param horizonShowDays (横軸)表示日数
     */
    public void setHorizonShowDays(int horizonShowDays) {
        if (Objects.nonNull(this.horizonShowDaysProperty)) {
            this.horizonShowDaysProperty.set(horizonShowDays);
        }
        this.horizonShowDays = horizonShowDays;
    }

    /**
     * 表示月数を取得する。
     *
     * @return 表示月数
     */
    public int getShowMonths() {
        switch (this.timeAxis) {
            case HorizonAxis:
                return this.getHorizonShowMonths();
            case VerticalAxis:
            default:
                return 1;
        }
    }

    /**
     * (横軸)表示月数プロパティを取得する。
     *
     * @return (横軸)表示月数プロパティ
     */
    public IntegerProperty horizonShowMonthsProperty() {
        if (Objects.isNull(this.horizonShowMonthsProperty)) {
            this.horizonShowMonthsProperty = new SimpleIntegerProperty(this.horizonShowMonths);
        }
        return this.horizonShowMonthsProperty;
    }

    /**
     * (横軸)表示月数を取得する。
     *
     * @return (横軸)表示月数
     */
    public int getHorizonShowMonths() {
        if (Objects.nonNull(this.horizonShowMonthsProperty)) {
            return this.horizonShowMonthsProperty.get();
        }
        return this.horizonShowMonths;
    }

    /**
     * (横軸)表示月数を設定する。
     *
     * @param horizonShowMonths (横軸)表示月数
     */
    public void setHorizonShowMonths(int horizonShowMonths) {
        if (Objects.nonNull(this.horizonShowMonthsProperty)) {
            this.horizonShowMonthsProperty.set(horizonShowMonths);
        }
        this.horizonShowMonths = horizonShowMonths;
    }

    /**
     * (横軸)休日表示プロパティを取得する。
     *
     * @return (横軸)休日表示プロパティ
     */
    public BooleanProperty horizonShowHolidayProperty() {
        if (Objects.isNull(this.horizonShowHolidayProperty)) {
            this.horizonShowHolidayProperty = new SimpleBooleanProperty(this.horizonShowHoliday);
        }
        return this.horizonShowHolidayProperty;
    }

    /**
     * (横軸)休日表示を取得する。
     *
     * @return (横軸)休日表示
     */
    public boolean getHorizonShowHoliday() {
        if (Objects.nonNull(this.horizonShowHolidayProperty)) {
            return this.horizonShowHolidayProperty.get();
        }
        return this.horizonShowHoliday;
    }

    /**
     * (横軸)休日表示を設定する。
     *
     * @param horizonShowHoliday (横軸)休日表示
     */
    public void setHorizonShowHoliday(boolean horizonShowHoliday) {
        if (Objects.nonNull(this.horizonShowHolidayProperty)) {
            this.horizonShowHolidayProperty.set(horizonShowHoliday);
        }
        this.horizonShowHoliday = horizonShowHoliday;
    }

    /**
     * (横軸)カラム数プロパティを取得する。
     *
     * @return (横軸)カラム数プロパティ
     */
    public IntegerProperty horizonColumnCountProperty() {
        if (Objects.isNull(this.horizonColumnCountProperty)) {
            this.horizonColumnCountProperty = new SimpleIntegerProperty(this.horizonColumnCount);
        }
        return this.horizonColumnCountProperty;
    }

    /**
     * (横軸)カラム数を取得する。
     *
     * @return (横軸)カラム数
     */
    public int getHorizonColumnCount() {
        if (Objects.nonNull(this.horizonColumnCountProperty)) {
            return this.horizonColumnCountProperty.get();
        }
        return this.horizonColumnCount;
    }

    /**
     * (横軸)カラム数を設定する。
     *
     * @param horizonColumnCount (横軸)カラム数
     */
    public void setHorizonColumnCount(int horizonColumnCount) {
        if (Objects.nonNull(this.horizonColumnCountProperty)) {
            this.horizonColumnCountProperty.set(horizonColumnCount);
        }
        this.horizonColumnCount = horizonColumnCount;
    }

    /**
     * (横軸)表示順プロパティを取得する。
     *
     * @return (横軸)表示順プロパティ
     */
    public ObjectProperty horizonShowOrderProperty() {
        if (Objects.isNull(this.horizonShowOrderProperty)) {
            this.horizonShowOrderProperty = new SimpleObjectProperty<>(this.horizonShowOrder);
        }
        return this.horizonShowOrderProperty;
    }

    /**
     * (横軸)表示順を取得する。
     *
     * @return (横軸)表示順
     */
    public ShowOrder getHorizonShowOrder() {
        if (Objects.nonNull(this.horizonShowOrderProperty)) {
            return this.horizonShowOrderProperty.get();
        }
        return this.horizonShowOrder;
    }

    /**
     * (横軸)表示順を設定する。
     *
     * @param horizonOrder (横軸)表示順
     */
    public void setHorizonShowOrder(ShowOrder horizonOrder) {
        if (Objects.nonNull(this.horizonShowOrderProperty)) {
            this.horizonShowOrderProperty.set(horizonOrder);
        }
        this.horizonShowOrder = horizonOrder;
    }

    /**
     * (横軸)行の高さプロパティ(ピクセル)を取得する。
     *
     * @return (横軸)行の高さプロパティ(ピクセル)
     */
    public IntegerProperty horizonRowHightProperty() {
        if (Objects.isNull(this.horizonRowHightProperty)) {
            this.horizonRowHightProperty = new SimpleIntegerProperty(this.horizonRowHight);
        }
        return this.horizonRowHightProperty;
    }

    /**
     * (横軸)行の高さ(ピクセル)を取得する。
     *
     * @return (横軸)行の高さ(ピクセル)
     */
    public int getHorizonRowHight() {
        if (Objects.nonNull(this.horizonRowHightProperty)) {
            return this.horizonRowHightProperty.get();
        }
        return this.horizonRowHight;
    }

    /**
     * (横軸)行の高さ(ピクセル)を設定する。
     *
     * @param horizonRowHight (横軸)行の高さ(ピクセル)
     */
    public void setHorizonRowHight(int horizonRowHight) {
        if (Objects.nonNull(this.horizonRowHightProperty)) {
            this.horizonRowHightProperty.set(horizonRowHight);
        }
        this.horizonRowHight = horizonRowHight;
    }



    /**
     * 予実表示プロパティ
    
     * @return 設定値
     */
    public ObjectProperty agendaDisplayPatternProperty() {
        if (Objects.isNull(agendaDisplayPatternProperty)) {
            agendaDisplayPatternProperty = new SimpleObjectProperty<>(agendaDisplayPattern);
        }
        return agendaDisplayPatternProperty;
    }


    /**
     * 予実の表示パターン
     *
     * @return 設定値
     */
    public AgendaDisplayPatternEnum getAgendaDisplayPattern() {
        if (Objects.nonNull(agendaDisplayPatternProperty)) {
            return agendaDisplayPatternProperty.get();
        }
        return agendaDisplayPattern;
    }

    /**
     * 予実の表示パターンを設定する
     *
     * @param in 設定値
     */
    public void setAgendaDisplayPattern(AgendaDisplayPatternEnum in) {
        if (Objects.nonNull(agendaDisplayPatternProperty)) {
            agendaDisplayPatternProperty.set(in);
        } else {
            this.agendaDisplayPattern = in;
        }
    }


    /**
     * 予実表示プロパティ
     *
     * @return 設定値
     */
    public ObjectProperty horizonAgendaDisplayPatternProperty() {
        if (Objects.isNull(horizonAgendaDisplayPatternProperty)) {
            horizonAgendaDisplayPatternProperty = new SimpleObjectProperty<>(horizonAgendaDisplayPattern);
        }
        return horizonAgendaDisplayPatternProperty;
    }


    /**
     * 予実の表示パターン
     *
     * @return 設定値
     */
    public AgendaDisplayPatternEnum getHorizonAgendaDisplayPattern() {
        if (Objects.nonNull(horizonAgendaDisplayPatternProperty)) {
            return horizonAgendaDisplayPatternProperty.get();
        }
        return horizonAgendaDisplayPattern;
    }

    /**
     * 予実の表示パターンを設定する
     *
     * @param in 設定値
     */
    public void setHorizonAgendaDisplayPattern(AgendaDisplayPatternEnum in) {
        if (Objects.nonNull(horizonAgendaDisplayPatternProperty)) {
            horizonAgendaDisplayPatternProperty.set(in);
        } else {
            this.horizonAgendaDisplayPattern = in;
        }
    }




    /**
     * (横軸)ページ切り替えプロパティを取得する。
     *
     * @return (横軸)ページ切り替えプロパティ
     */
    public BooleanProperty horizonTogglePagesProperty() {
        if (Objects.isNull(this.horizonTogglePagesProperty)) {
            this.horizonTogglePagesProperty = new SimpleBooleanProperty(this.horizonTogglePages);
        }
        return this.horizonTogglePagesProperty;
    }




    /**
     * (横軸)ページ切り替えを取得する。
     *
     * @return (横軸)ページ切り替え
     */
    public boolean getHorizonTogglePages() {
        if (Objects.nonNull(this.horizonTogglePagesProperty)) {
            return this.horizonTogglePagesProperty.get();
        }
        return this.horizonTogglePages;
    }

    /**
     * (横軸)ページ切り替えを設定する。
     *
     * @param horizonTogglePages (横軸)ページ切り替え
     */
    public void setHorizonTogglePages(boolean horizonTogglePages) {
        if (Objects.nonNull(this.horizonTogglePagesProperty)) {
            this.horizonTogglePagesProperty.set(horizonTogglePages);
        }
        this.horizonTogglePages = horizonTogglePages;
    }

    /**
     * (横軸)ページ切り替え間隔プロパティ(秒)を取得する。
     *
     * @return (横軸)ページ切り替え間隔プロパティ(秒)
     */
    public IntegerProperty horizonPageToggleTimeProperty() {
        if (Objects.isNull(this.horizonPageToggleTimeProperty)) {
            this.horizonPageToggleTimeProperty = new SimpleIntegerProperty(this.horizonPageToggleTime);
        }
        return this.horizonPageToggleTimeProperty;
    }

    /**
     * (横軸)ページ切り替え間隔(秒)を取得する。
     *
     * @return (横軸)ページ切り替え間隔(秒)
     */
    public int getHorizonPageToggleTime() {
        if (Objects.nonNull(this.horizonPageToggleTimeProperty)) {
            return this.horizonPageToggleTimeProperty.get();
        }
        return this.horizonPageToggleTime;
    }

    /**
     * (横軸)ページ切り替え間隔(秒)を設定する。
     *
     * @param horizonPageToggleTime (横軸)ページ切り替え間隔(秒)
     */
    public void setHorizonPageToggleTime(int horizonPageToggleTime) {
        if (Objects.nonNull(this.horizonPageToggleTimeProperty)) {
            this.horizonPageToggleTimeProperty.set(horizonPageToggleTime);
        }
        this.horizonPageToggleTime = horizonPageToggleTime;
    }

    /**
     * (横軸)自動スクロールプロパティを取得する。
     *
     * @return (横軸)自動スクロールプロパティ
     */
    public BooleanProperty horizonAutoScrollProperty() {
        if (Objects.isNull(this.horizonAutoScrollProperty)) {
            this.horizonAutoScrollProperty = new SimpleBooleanProperty(this.horizonAutoScroll);
        }
        return this.horizonAutoScrollProperty;
    }

    /**
     * (横軸)自動スクロールを取得する。
     *
     * @return (横軸)自動スクロール
     */
    public boolean getHorizonAutoScroll() {
        if (Objects.nonNull(this.horizonAutoScrollProperty)) {
            return this.horizonAutoScrollProperty.get();
        }
        return this.horizonAutoScroll;
    }

    /**
     * (横軸)自動スクロールを設定する。
     *
     * @param horizonAutoScroll (横軸)自動スクロール
     */
    public void setHorizonAutoScroll(boolean horizonAutoScroll) {
        if (Objects.nonNull(this.horizonAutoScrollProperty)) {
            this.horizonAutoScrollProperty.set(horizonAutoScroll);
        }
        this.horizonAutoScroll = horizonAutoScroll;
    }

    /**
     * (横軸)自動スクロール範囲プロパティを取得する。
     *
     * @return (横軸)自動スクロール範囲プロパティ
     */
    public ObjectProperty horizonScrollUnitProperty() {
        if (Objects.isNull(this.horizonScrollUnitProperty)) {
            this.horizonScrollUnitProperty = new SimpleObjectProperty(this.horizonScrollUnit);
        }
        return this.horizonScrollUnitProperty;
    }

    /**
     * (横軸)自動スクロール範囲を取得する。
     *
     * @return (横軸)自動スクロール範囲
     */
    public LocalTime getHorizonScrollUnit() {
        if (Objects.nonNull(this.horizonScrollUnitProperty)) {
            return this.horizonScrollUnitProperty.get();
        }
        return this.horizonScrollUnit;
    }

    /**
     * (横軸)自動スクロール範囲を設定する。
     *
     * @param horizonScrollUnit (横軸)自動スクロール範囲
     */
    public void setHorizonScrollUnit(LocalTime horizonScrollUnit) {
        if (Objects.nonNull(this.horizonScrollUnitProperty)) {
            this.horizonScrollUnitProperty.set(horizonScrollUnit);
        }
        this.horizonScrollUnit = horizonScrollUnit;
    }

    /**
     * (横軸)予実表示プロパティを取得する。
     *
     * @return (横軸)予実表示プロパティ
     */
    public ObjectProperty horizonPlanActualShowTypeProperty() {
        if (Objects.isNull(this.horizonPlanActualShowTypeProperty)) {
            this.horizonPlanActualShowTypeProperty = new SimpleObjectProperty<>(this.horizonPlanActualShowType);
        }
        return this.horizonPlanActualShowTypeProperty;
    }

    /**
     * (横軸)予実表示を取得する。
     *
     * @return (横軸)予実表示
     */
    public PlanActualShowTypeEnum getHorizonPlanActualShowType() {
        if (Objects.nonNull(this.horizonPlanActualShowTypeProperty)) {
            return this.horizonPlanActualShowTypeProperty.get();
        }
        return this.horizonPlanActualShowType;
    }

    /**
     * (横軸)予実表示を設定する。
     *
     * @param planActualShowType (横軸)予実表示
     */
    public void setHorizonPlanActualShowType(PlanActualShowTypeEnum planActualShowType) {
        if (Objects.nonNull(this.horizonPlanActualShowTypeProperty)) {
            this.horizonPlanActualShowTypeProperty.set(planActualShowType);
        }
        this.horizonPlanActualShowType = planActualShowType;
    }

    /**
     * (横軸)進捗時間表示プロパティを取得する。
     * @return (横軸)進捗時間表示プロパティ
     */
    public BooleanProperty horizonShowActualTimeProperty() {
        if (Objects.isNull(this.horizonShowActualTimeProperty)) {
            this.horizonShowActualTimeProperty = new SimpleBooleanProperty(this.horizonShowActualTime);
        }
        return this.horizonShowActualTimeProperty;
    }

    /**
     * (横軸)進捗時間表示を取得する
     * @return (横軸)進捗時間表示
     */
    public boolean getHorizonShowActualTime() {
        if (Objects.nonNull(this.horizonShowActualTimeProperty)) {
            return this.horizonShowActualTimeProperty.get();
        }
        return this.horizonShowActualTime;
    }

    /**
     * (横軸)進捗時間表示を設定する
     * @param val (横軸)進捗時間表示
     */
    public void setHorizonShowActualTime(boolean val) {
        if (Objects.nonNull(this.horizonShowActualTimeProperty)) {
            this.horizonShowActualTimeProperty.set(val);
        }
        this.horizonShowActualTime = val;
    }

    /**
     * (横軸)応援者の実績を表示プロパティを取得する。
     *
     * @return (横軸)応援者の実績を表示プロパティ (true:表示, false:非表示)
     */
    public BooleanProperty horizonDisplaySupportResultsProperty() {
        if (Objects.isNull(this.horizonDisplaySupportResultsProperty)) {
            this.horizonDisplaySupportResultsProperty = new SimpleBooleanProperty(this.horizonDisplaySupportResults);
        }
        return this.horizonDisplaySupportResultsProperty;
    }

    /**
     * (横軸)応援者の実績を表示を取得する。
     *
     * @return (横軸)応援者の実績を表示 (true:表示, false:非表示)
     */
    public boolean getHorizonDisplaySupportResults() {
        if (Objects.nonNull(this.horizonDisplaySupportResultsProperty)) {
            return this.horizonDisplaySupportResultsProperty.get();
        }
        return this.horizonDisplaySupportResults;
    }

    /**
     * (横軸)応援者の実績を表示を設定する。
     *
     * @param horizonDisplaySupportResults (横軸)応援者の実績を表示 (true:表示, false:非表示)
     */
    public void setHorizonDisplaySupportResults(boolean horizonDisplaySupportResults) {
        if (Objects.nonNull(this.horizonContentProperty)) {
            this.horizonDisplaySupportResultsProperty.set(horizonDisplaySupportResults);
        }
        this.horizonDisplaySupportResults = horizonDisplaySupportResults;
    }

    /**
     * (横軸)詳細表示を取得する。
     *
     * @return (横軸)詳細表示
     */
    public ObjectProperty horizonContentProperty() {
        if (Objects.isNull(this.horizonContentProperty)) {
            this.horizonContentProperty = new SimpleObjectProperty(this.horizonContent);
        }
        return this.horizonContentProperty;
    }

    /**
     * (横軸)詳細表示を取得する。
     *
     * @return (横軸)詳細表示
     */
    public ContentTypeEnum getHorizonContent() {
        if (Objects.nonNull(this.horizonContentProperty)) {
            return this.horizonContentProperty.get();
        }
        return this.horizonContent;
    }

    /**
     * (横軸)詳細表示を設定する。
     *
     * @param horizonContent (横軸)詳細表示
     */
    public void setHorizonContent(ContentTypeEnum horizonContent) {
        if (Objects.nonNull(this.horizonContentProperty)) {
            this.horizonContentProperty.set(horizonContent);
        }
        this.horizonContent = horizonContent;
    }

    // ******************************
    // ***** 画面設定(払出状況) *****
    // ******************************
    /**
     * (払出状況)ディスプレイ番号プロパティを取得する。
     *
     * @return (払出状況)ディスプレイ番号プロパティ
     */
    public IntegerProperty payoutDisplayNumberProperty() {
        if (Objects.isNull(this.payoutDisplayNumberProperty)) {
            this.payoutDisplayNumberProperty = new SimpleIntegerProperty(this.payoutDisplayNumber);
        }
        return this.payoutDisplayNumberProperty;
    }

    /**
     * (払出状況)ディスプレイ番号を取得する。
     *
     * @return (払出状況)ディスプレイ番号
     */
    public int getPayoutDisplayNumber() {
        if (Objects.nonNull(this.payoutDisplayNumberProperty)) {
            return this.payoutDisplayNumberProperty.get();
        }
        return this.payoutDisplayNumber;
    }

    /**
     * (払出状況)ディスプレイ番号を設定する。
     *
     * @param payoutdisplayNumber (払出状況)ディスプレイ番号
     */
    public void setPayoutDisplayNumber(int payoutdisplayNumber) {
        if (Objects.nonNull(this.payoutDisplayNumberProperty)) {
            this.payoutDisplayNumberProperty.set(payoutdisplayNumber);
        }
        this.payoutDisplayNumber = payoutdisplayNumber;
    }

    /**
     * (払出状況)フルスクリーン表示プロパティを取得する。
     *
     * @return (払出状況)フルスクリーン表示プロパティ
     */
    public BooleanProperty payoutFullScreenProperty() {
        if (Objects.isNull(this.payoutFullScreenProperty)) {
            this.payoutFullScreenProperty = new SimpleBooleanProperty(this.payoutFullScreen);
        }
        return this.payoutFullScreenProperty;
    }

    /**
     * (払出状況)フルスクリーン表示を取得する。
     *
     * @return (払出状況)フルスクリーン表示
     */
    public boolean getPayoutFullScreen() {
        if (Objects.nonNull(this.payoutFullScreenProperty)) {
            return this.payoutFullScreenProperty.get();
        }
        return this.payoutFullScreen;
    }

    /**
     * (払出状況)フルスクリーン表示を設定する。
     *
     * @param payoutFullScreen (払出状況)フルスクリーン表示
     */
    public void setPayoutFullScreen(boolean payoutFullScreen) {
        if (Objects.nonNull(this.payoutFullScreenProperty)) {
            this.payoutFullScreenProperty.set(fullScreen);
        }
        this.payoutFullScreen = payoutFullScreen;
    }

    /**
     * 払出完了の行数プロパティを取得する。
     * 
     * @return 払出完了の行数プロパティ
     */
    public IntegerProperty PayoutCompleteLineCountProperty() {
        if (Objects.isNull(this.payoutCompleteLineCountProperty)) {
            this.payoutCompleteLineCountProperty = new SimpleIntegerProperty(this.payoutCompleteLineCount);
        }
        return this.payoutCompleteLineCountProperty;
    }

    /**
     * 払出完了の行数を取得する。
     *
     * @return 払出完了の行数
     */
    public int getPayoutCompleteLineCount() {
        if (Objects.nonNull(this.payoutCompleteLineCountProperty)) {
            return this.payoutCompleteLineCountProperty.get();
        }
        return this.payoutCompleteLineCount;
    }

    /**
     * 払出完了の行数を設定する。
     * 
     * @param value 払出完了の行数
     */
    public void setPayoutCompleteLineCount(int value) {
        if (Objects.nonNull(this.payoutCompleteLineCountProperty)) {
            this.payoutCompleteLineCountProperty.set(value);
        }
        this.payoutCompleteLineCount = value;
    }

    /**
     * 払出待ちの行数プロパティを取得する。
     *
     * @return 払出待ちの行数プロパティ
     */
    public IntegerProperty PayoutWaitingLineCountProperty() {
        if (Objects.isNull(this.payoutWaitingLineCountProperty)) {
            this.payoutWaitingLineCountProperty = new SimpleIntegerProperty(this.payoutWaitingLineCount);
        }
        return this.payoutWaitingLineCountProperty;
    }

    /**
     * 払出待ちの行数を取得する。
     * 
     * @return 払出待ちの行数
     */
    public int getPayoutWaitingLineCount() {
        if (Objects.nonNull(this.payoutWaitingLineCountProperty)) {
            return this.payoutWaitingLineCountProperty.get();
        }
        return this.payoutWaitingLineCount;
    }

    /**
     * 払出待ちの行数を設定する。
     *
     * @param value 払出待ちの行数
     */
    public void setPayoutWaitingLineCount(int value) {
        if (Objects.nonNull(this.payoutWaitingLineCountProperty)) {
            this.payoutWaitingLineCountProperty.set(value);
        }
        this.payoutWaitingLineCount = value;
    }

    /**
     * ピッキング中の行数プロパティを取得する。
     * 
     * @return ピッキング中の行数プロパティ
     */
    public IntegerProperty PickingLineCountProperty() {
        if (Objects.isNull(this.pickingLineCountProperty)) {
            this.pickingLineCountProperty = new SimpleIntegerProperty(this.pickingLineCount);
        }
        return this.pickingLineCountProperty;
    }

    /**
     * ピッキング中の行数を取得する。
     * 
     * @return ピッキング中の行数
     */
    public int getPickingLineCount() {
        if (Objects.nonNull(this.pickingLineCountProperty)) {
            return this.pickingLineCountProperty.get();
        }
        return this.pickingLineCount;
    }

    /**
     * ピッキング中の行数を設定する。
     * 
     * @param value ピッキング中の行数
     */
    public void setPickingLineCount(int value) {
        if (Objects.nonNull(this.pickingLineCountProperty)) {
            this.pickingLineCountProperty.set(value);
        }
        this.pickingLineCount = value;
    }

    /**
     * 受付の行数プロパティを取得する。
     * 
     * @return 受付の行数プロパティ
     */
    public IntegerProperty ReceptionLineCountProperty() {
        if (Objects.isNull(this.receptionLineCountProperty)) {
            this.receptionLineCountProperty = new SimpleIntegerProperty(this.receptionLineCount);
        }
        return this.receptionLineCountProperty;
    }

    /**
     * 受付の行数を取得する。
     * 
     * @return 受付の行数
     */
    public int getReceptionLineCount() {
        if (Objects.nonNull(this.receptionLineCountProperty)) {
            return this.receptionLineCountProperty.get();
        }
        return this.receptionLineCount;
    }

    /**
     * 受付の行数を設定する。
     * 
     * @param value 受付の行数
     */
    public void setReceptionLineCount(int value) {
        if (Objects.nonNull(this.receptionLineCountProperty)) {
            this.receptionLineCountProperty.set(value);
        }
        this.receptionLineCount = value;
    }

    /**
     * 払出完了の表示日数プロパティを取得する。
     * 
     * @return 払出完了の表示日数プロパティ
     */
    public IntegerProperty PayoutCompleteDisplayDaysProperty() {
        if (Objects.isNull(this.payoutCompleteDisplayDaysProperty)) {
            this.payoutCompleteDisplayDaysProperty = new SimpleIntegerProperty(this.payoutCompleteDisplayDays);
        }
        return this.payoutCompleteDisplayDaysProperty;
    }

    /**
     * 払出完了の表示日数を取得する。
     * 
     * @return 払出完了の表示日数
     */
    public int getPayoutCompleteDisplayDays() {
        if (Objects.nonNull(this.payoutCompleteDisplayDaysProperty)) {
            return this.payoutCompleteDisplayDaysProperty.get();
        }
        return this.payoutCompleteDisplayDays;
    }

    /**
     * 払出完了の表示日数を設定する。
     * 
     * @param value 払出完了の表示日数
     */
    public void setPayoutCompleteDisplayDays(int value) {
        if (Objects.nonNull(this.payoutCompleteDisplayDaysProperty)) {
            this.payoutCompleteDisplayDaysProperty.set(value);
        }
        this.payoutCompleteDisplayDays = value;
    }

    /**
     * ページ切り替え間隔(秒)プロパティを取得する。
     * 
     * @return ページ切り替え間隔(秒)プロパティ
     */
    public IntegerProperty PagingIntervalSecondsProperty() {
        if (Objects.isNull(this.pagingIntervalSecondsProperty)) {
            this.pagingIntervalSecondsProperty = new SimpleIntegerProperty(this.pagingIntervalSeconds);
        }
        return this.pagingIntervalSecondsProperty;
    }

    /**
     * ページ切り替え間隔(秒)を取得する。
     *
     * @return ページ切り替え間隔(秒)
     */
    public int getPagingIntervalSeconds() {
        if (Objects.nonNull(this.pagingIntervalSecondsProperty)) {
            return this.pagingIntervalSecondsProperty.get();
        }
        return this.pagingIntervalSeconds;
    }

    /**
     * ページ切り替え間隔(秒)を設定する。
     *
     * @param value ページ切り替え間隔(秒)
     */
    public void setPagingIntervalSeconds(int value) {
        if (Objects.nonNull(this.pagingIntervalSecondsProperty)) {
            this.pagingIntervalSecondsProperty.set(value);
        }
        this.pagingIntervalSeconds = value;
    }

    /**
     * フォントサイズプロパティを取得する。
     *
     * @return フォントサイズプロパティ
     */
    public IntegerProperty FontSizePsProperty() {
        if (Objects.isNull(this.fontSizePsProperty)) {
            this.fontSizePsProperty = new SimpleIntegerProperty(this.fontSizePs);
        }
        return this.fontSizePsProperty;
    }

    /**
     * フォントサイズを取得する。
     *
     * @return フォントサイズ
     */
    public int getFontSizePs() {
        if (Objects.nonNull(this.fontSizePsProperty)) {
            return this.fontSizePsProperty.get();
        }
        return this.fontSizePs;
    }

    /**
     * フォントサイズを設定する。
     *
     * @param value フォントサイズ
     */
    public void setFontSizePs(int value) {
        if (Objects.nonNull(this.fontSizePsProperty)) {
            this.fontSizePsProperty.set(value);
        }
        this.fontSizePs = value;
    }


    /**
     * タイトルのフォントサイズプロパティを取得する。
     *
     * @return タイトルのフォントサイズプロパティ
     */
    public DoubleProperty titleSizeProperty() {
        if (Objects.isNull(this.titleSizeProperty)) {
            this.titleSizeProperty = new SimpleDoubleProperty(this.titleSize);
        }
        return this.titleSizeProperty;
    }

    /**
     * タイトルのフォントサイズを取得する。
     *
     * @return タイトルのフォントサイズ
     */
    public Double getTitleSize() {
        if (Objects.nonNull(this.titleSizeProperty)) {
            return this.titleSizeProperty.get();
        }
        return this.titleSize;
    }

    /**
     * タイトルのフォントサイズを設定する。
     *
     * @param titleSize タイトルのフォントサイズ
     */
    public void setTitleSize(Double titleSize) {
        if (Objects.nonNull(this.titleSizeProperty)) {
            this.titleSizeProperty.set(titleSize);
        }
        this.titleSize = titleSize;
    }

    /**
     * ヘッダーのフォントサイズプロパティを取得する。
     *
     * @return ヘッダーのフォントサイズプロパティ
     */
    public DoubleProperty headerSizeProperty() {
        if (Objects.isNull(this.headerSizeProperty)) {
            this.headerSizeProperty = new SimpleDoubleProperty(this.headerSize);
        }
        return this.headerSizeProperty;
    }

    /**
     * ヘッダーのフォントサイズを取得する。
     *
     * @return ヘッダーのフォントサイズ
     */
    public Double getHeaderSize() {
        if (Objects.nonNull(this.headerSizeProperty)) {
            return this.headerSizeProperty.get();
        }
        return this.headerSize;
    }

    /**
     * ヘッダーのフォントサイズを設定する。
     *
     * @param headerSize ヘッダーのフォントサイズ
     */
    public void setHeaderSize(Double headerSize) {
        if (Objects.nonNull(this.headerSizeProperty)) {
            this.headerSizeProperty.set(headerSize);
        }
        this.headerSize = headerSize;
    }

    /**
     * カラムのフォントサイズプロパティを取得する。
     *
     * @return カラムのフォントサイズプロパティ
     */
    public DoubleProperty columnSizeProperty() {
        if (Objects.isNull(this.columnSizeProperty)) {
            this.columnSizeProperty = new SimpleDoubleProperty(this.columnSize);
        }
        return this.columnSizeProperty;
    }

    /**
     * カラムのフォントサイズを取得する。
     *
     * @return カラムのフォントサイズ
     */
    public Double getColumnSize() {
        if (Objects.nonNull(this.columnSizeProperty)) {
            return this.columnSizeProperty.get();
        }
        return this.columnSize;
    }

    /**
     * カラムのフォントサイズを設定する。
     *
     * @param columnSize カラムのフォントサイズ
     */
    public void setColumnSize(Double columnSize) {
        if (Objects.nonNull(this.columnSizeProperty)) {
            this.columnSizeProperty.set(columnSize);
        }
        this.columnSize = columnSize;
    }

    /**
     * アイテムのフォントサイズプロパティを取得する。
     *
     * @return アイテムのフォントサイズプロパティ
     */
    public DoubleProperty itemSizeProperty() {
        if (Objects.isNull(this.itemSizeProperty)) {
            this.itemSizeProperty = new SimpleDoubleProperty(this.itemSize);
        }
        return this.itemSizeProperty;
    }

    /**
     * アイテムのフォントサイズを取得する。
     *
     * @return アイテムのフォントサイズ
     */
    public Double getItemSize() {
        if (Objects.nonNull(this.itemSizeProperty)) {
            return this.itemSizeProperty.get();
        }
        return this.itemSize;
    }

    /**
     * アイテムのフォントサイズを設定する。
     *
     * @param itemSize アイテムのフォントサイズ
     */
    public void setItemSize(Double itemSize) {
        if (Objects.nonNull(this.itemSizeProperty)) {
            this.itemSizeProperty.set(itemSize);
        }
        this.itemSize = itemSize;
    }

    /**
     * 拡大スライドバーのフォントサイズプロパティを取得する。
     *
     * @return 拡大スライドバーのフォントサイズプロパティ
     */
    public DoubleProperty zoomBarSizeProperty() {
        if (Objects.isNull(this.zoomBarSizeProperty)) {
            this.zoomBarSizeProperty = new SimpleDoubleProperty(this.zoomBarSize);
        }
        return this.zoomBarSizeProperty;
    }

    /**
     * 拡大スライドバーのフォントサイズを取得する。
     *
     * @return 拡大スライドバーのフォントサイズ
     */
    public Double getZoomBarSize() {
        if (Objects.nonNull(this.zoomBarSizeProperty)) {
            return this.zoomBarSizeProperty.get();
        }
        return this.zoomBarSize;
    }

    /**
     * 拡大スライドバーのフォントサイズを設定する。
     *
     * @param zoomBarSize 拡大スライドバーのフォントサイズ
     */
    public void setZoomBarSize(Double zoomBarSize) {
        if (Objects.nonNull(this.zoomBarSizeProperty)) {
            this.zoomBarSizeProperty.set(zoomBarSize);
        }
        this.zoomBarSize = zoomBarSize;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.monitorType);
        hash = 79 * hash + Objects.hashCode(this.targetDate);
        hash = 79 * hash + Objects.hashCode(this.startWorkTime);
        hash = 79 * hash + Objects.hashCode(this.endWorkTime);
        hash = 79 * hash + Objects.hashCode(this.mode);
        hash = 79 * hash + Objects.hashCode(this.kanbanIds);
        hash = 79 * hash + Objects.hashCode(this.liteKanbanIds);
        hash = 79 * hash + (int) (this.updateInterval ^ (this.updateInterval >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AgendaMonitorSetting other = (AgendaMonitorSetting) obj;
        if (this.monitorType != other.monitorType) {
            return false;
        }
        if (this.updateInterval != other.updateInterval) {
            return false;
        }
        if (!Objects.equals(this.targetDate, other.targetDate)) {
            return false;
        }
        if (!Objects.equals(this.startWorkTime, other.startWorkTime)) {
            return false;
        }
        if (!Objects.equals(this.endWorkTime, other.endWorkTime)) {
            return false;
        }
        if (this.mode != other.mode) {
            return false;
        }
        if (!Objects.equals(this.kanbanIds, other.kanbanIds)) {
            return false;
        }
        if (!Objects.equals(this.liteKanbanIds, other.liteKanbanIds)) {
            return false;
        }
        return true;
    }

    /**
     * 表示される情報をコピーする
     *
     * @return
     */
    @Override
    public AgendaMonitorSetting clone() {
        AgendaMonitorSetting setting = new AgendaMonitorSetting();
        // モニター種別
        setting.setMonitorType(this.getMonitorType());
        // 表示条件
        setting.setTargetDate(this.getTargetDate());// 対象日
        setting.setStartWorkTime(this.getStartWorkTime());// 開始時間(hh:mm)
        setting.setEndWorkTime(this.getEndWorkTime());// 終了時間(hh:mm)
        setting.setMode(getMode());// 表示対象
        setting.setKanbanIds(new ArrayList<>(this.getKanbanIds()));// カンバンID一覧
        setting.setLiteKanbanIds(new ArrayList<>(this.getLiteKanbanIds()));// LiteカンバンID一覧
        setting.setOrganizationIds(new ArrayList<>(this.getOrganizationIds()));// 組織ID一覧
        setting.setLineIds(new ArrayList<>(this.getLineIds()));// ライン設備ID一覧
        setting.setKanbanHierarchyIds((new ArrayList<>(this.getKanbanHierarchyIds()))); // カンバン階層ID一覧
        setting.setUpdateInterval(this.getUpdateInterval());// 更新間隔(分)
        setting.setModelName(this.getModelName());// モデル名
        setting.setDisplayPeriod(this.getDisplayPeriod()); //表示期間
        setting.setDisplayOrder(this.getDisplayOrder()); // 表示順

        // 休憩時間
        setting.setBreaktimes(new ArrayList<>(this.getBreaktimes()));

        // 画面設定(縦軸)
        setting.setDisplayNumber(this.getDisplayNumber());// (縦軸)ディスプレイ番号
        setting.setFullScreen(this.getFullScreen());// (縦軸)フルスクリーン表示
        setting.setTimeAxis(this.getTimeAxis());// 時間軸(横軸と共通)
        setting.setColumnCount(this.getColumnCount());// (縦軸)カラム数
        setting.setShowOrder(this.getShowOrder());// (縦軸)表示順
        setting.setVisibleOnlyPlan(this.getVisibleOnlyPlan());// (縦軸)予定のみ表示
        setting.setShowActualTime(this.getShowActualTime());// (縦軸)進捗時間表示
        setting.setDisplaySupportResults(this.getDisplaySupportResults());// (縦軸)応援者の実績を表示
        setting.setContent(this.getContent());// (縦軸)詳細表示
        setting.setTimeUnit(this.getTimeUnit());// (縦軸)時間軸の表示単位(分)
        setting.setTogglePages(this.getTogglePages());// (縦軸)ページ切り替え
        setting.setPageToggleTime(this.getPageToggleTime());// (縦軸)ページ切り替え間隔(秒)
        setting.setAutoScroll(this.getAutoScroll());// (縦軸)自動スクロール
        setting.setScrollUnit(this.getScrollUnit());// (縦軸)自動スクロール範囲[HH:mm]
        setting.setAgendaDisplayPattern(this.getAgendaDisplayPattern());//(縦軸)まとめて表示

        // Liteモニターのみの設定
        setting.setProcessDisplayColumns(this.getProcessDisplayColumns());//表示列数
        setting.setProcessDisplayRows(this.getProcessDisplayRows());//表示行数
        setting.setProcessPageSwitchingInterval(this.getProcessPageSwitchingInterval());//工程ページ切替間隔(秒)
        setting.setCallSoundSetting(this.getCallSoundSetting());//呼出音設定

        // 画面設定(横軸)
        setting.setHorizonDisplayNumber(this.getHorizonDisplayNumber());// (横軸)ディスプレイ番号
        setting.setHorizonFullScreen(this.getHorizonFullScreen());// (横軸)フルスクリーン表示
        setting.setHorizonTimeScale(this.getHorizonTimeScale());// (横軸)時間軸スケール
        setting.setHorizonTimeUnit(this.getHorizonTimeUnit());// (横軸)時間軸の表示単位(分)
        setting.setHorizonShowDays(this.getHorizonShowDays());// (横軸)表示日数
        setting.setHorizonShowMonths(this.getHorizonShowMonths());// (横軸)表示月数
        setting.setHorizonShowHoliday(this.getHorizonShowHoliday());// (横軸)休日表示
        setting.setHorizonColumnCount(this.getHorizonColumnCount());// (横軸)カラム数
        setting.setHorizonShowOrder(this.getHorizonShowOrder());// (横軸)表示順
        setting.setHorizonRowHight(this.getHorizonRowHight());// (横軸)行の高さ(ピクセル)
        setting.setHorizonTogglePages(this.getHorizonTogglePages());// (横軸)ページ切り替え
        setting.setHorizonPageToggleTime(this.getHorizonPageToggleTime());// (横軸)ページ切り替え間隔(秒)
        setting.setHorizonAutoScroll(this.getHorizonAutoScroll());// (横軸)自動スクロール
        setting.setHorizonScrollUnit(this.getHorizonScrollUnit());// (横軸)自動スクロール範囲
        setting.setHorizonAgendaDisplayPattern(this.getHorizonAgendaDisplayPattern());//(横軸)まとめて表示
        setting.setHorizonPlanActualShowType(this.getHorizonPlanActualShowType());// (横軸)予実表示
        setting.setHorizonShowActualTime(this.getHorizonShowActualTime()); // (横軸)進捗時間表示
        setting.setHorizonDisplaySupportResults(this.getHorizonDisplaySupportResults());// (横軸)応援者の実績を表示
        setting.setHorizonContent(this.getHorizonContent());// (横軸)詳細表示
        setting.setHorizonBeforeDays(this.getHorizonBeforeDays());

        // 画面設定(払出状況)
        setting.setPayoutDisplayNumber(this.getPayoutDisplayNumber());              // (払出状況)ディスプレイ番号
        setting.setPayoutFullScreen(this.getPayoutFullScreen());                    // (払出状況)フルスクリーン表示
        setting.setPayoutCompleteLineCount(this.getPayoutCompleteLineCount());      // 払出完了の行数
        setting.setPayoutWaitingLineCount(this.getPayoutWaitingLineCount());        // 払出待ちの行数
        setting.setPickingLineCount(this.getPickingLineCount());                    // ピッキング中の行数
        setting.setReceptionLineCount(this.getReceptionLineCount());                // 受付の行数
        setting.setPayoutCompleteDisplayDays(this.getPayoutCompleteDisplayDays());  // 払出完了の表示日数
        setting.setPagingIntervalSeconds(this.getPagingIntervalSeconds());          // ページ切り替え間隔(秒)
        setting.setFontSizePs(this.getFontSizePs());                                // フォントサイズ

        // フォントサイズ
        setting.setTitleSize(this.getTitleSize());// タイトルのフォントサイズ
        setting.setHeaderSize(this.getHeaderSize());// ヘッダーのフォントサイズ
        setting.setColumnSize(this.getColumnSize());// カラムのフォントサイズ
        setting.setItemSize(this.getItemSize());// アイテムのフォントサイズ
        setting.setZoomBarSize(this.getZoomBarSize());// 拡大スライドバーのフォントサイズ

        return setting;
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other
     * @return 一致するときtrue
     */
    public boolean equalsDisplayInfo(AgendaMonitorSetting other) {
        if (Objects.equals(this.getMonitorType(), other.getMonitorType())
                && Objects.equals(this.getAutoScroll(), other.getAutoScroll())
                && Objects.equals(this.getTargetDate(), other.getTargetDate())
                && Objects.equals(this.getStartWorkTime(), other.getStartWorkTime())
                && Objects.equals(this.getEndWorkTime(), other.getEndWorkTime())
                && Objects.equals(this.getMode(), other.getMode())
                && Objects.equals(this.getKanbanIds(), other.getKanbanIds())
                && Objects.equals(this.getLiteKanbanIds(), other.getLiteKanbanIds())
                && Objects.equals(this.getOrganizationIds(), other.getOrganizationIds())
                && Objects.equals(this.getLineIds(), other.getLineIds())
                && Objects.equals(this.getKanbanHierarchyIds(), other.getKanbanHierarchyIds())
                && Objects.equals(this.getUpdateInterval(), other.getUpdateInterval())
                && Objects.equals(this.getModelName(), other.getModelName())
                && Objects.equals(this.getDisplayPeriod(), other.getDisplayPeriod())
                && Objects.equals(this.getDisplayOrder(), other.getDisplayOrder())
                && Objects.equals(this.getBreaktimes(), other.getBreaktimes())
                && Objects.equals(this.getDisplayNumber(), other.getDisplayNumber())
                && Objects.equals(this.getFullScreen(), other.getFullScreen())
                && Objects.equals(this.getTimeAxis(), other.getTimeAxis())
                && Objects.equals(this.getColumnCount(), other.getColumnCount())
                && Objects.equals(this.getShowOrder(), other.getShowOrder())
                && Objects.equals(this.getVisibleOnlyPlan(), other.getVisibleOnlyPlan())
                && Objects.equals(this.getShowActualTime(), other.getShowActualTime())
                && Objects.equals(this.getContent(), other.getContent())
                && Objects.equals(this.getTimeUnit(), other.getTimeUnit())
                && Objects.equals(this.getTogglePages(), other.getTogglePages())
                && Objects.equals(this.getPageToggleTime(), other.getPageToggleTime())
                && Objects.equals(this.getAutoScroll(), other.getAutoScroll())
                && Objects.equals(this.getScrollUnit(), other.getScrollUnit())
                && Objects.equals(this.getDisplaySupportResults(), other.getDisplaySupportResults())
                && Objects.equals(this.getAgendaDisplayPattern(), other.getAgendaDisplayPattern())
                && Objects.equals(this.getProcessDisplayColumns(), other.getProcessDisplayColumns())
                && Objects.equals(this.getProcessDisplayRows(), other.getProcessDisplayRows())
                && Objects.equals(this.getProcessPageSwitchingInterval(), other.getProcessPageSwitchingInterval())
                && Objects.equals(this.getCallSoundSetting(), other.getCallSoundSetting())
                && Objects.equals(this.getHorizonDisplayNumber(), other.getHorizonDisplayNumber())
                && Objects.equals(this.getHorizonFullScreen(), other.getHorizonFullScreen())
                && Objects.equals(this.getHorizonTimeScale(), other.getHorizonTimeScale())
                && Objects.equals(this.getHorizonTimeUnit(), other.getHorizonTimeUnit())
                && Objects.equals(this.getHorizonShowDays(), other.getHorizonShowDays())
                && Objects.equals(this.getHorizonShowMonths(), other.getHorizonShowMonths())
                && Objects.equals(this.getHorizonShowHoliday(), other.getHorizonShowHoliday())
                && Objects.equals(this.getHorizonColumnCount(), other.getHorizonColumnCount())
                && Objects.equals(this.getHorizonShowOrder(), other.getHorizonShowOrder())
                && Objects.equals(this.getHorizonRowHight(), other.getHorizonRowHight())
                && Objects.equals(this.getHorizonAutoScroll(), other.getHorizonAutoScroll())
                && Objects.equals(this.getHorizonScrollUnit(), other.getHorizonScrollUnit())
                && Objects.equals(this.getHorizonTogglePages(), other.getHorizonTogglePages())
                && Objects.equals(this.getHorizonPageToggleTime(), other.getHorizonPageToggleTime())
                && Objects.equals(this.getHorizonAgendaDisplayPattern(), other.getHorizonAgendaDisplayPattern())
                && Objects.equals(this.getHorizonPlanActualShowType(), other.getHorizonPlanActualShowType())
                && Objects.equals(this.getHorizonShowActualTime(), other.getHorizonShowActualTime())
                && Objects.equals(this.getHorizonContent(), other.getHorizonContent())
                && Objects.equals(this.getHorizonDisplaySupportResults(), other.getHorizonDisplaySupportResults())
                && Objects.equals(this.getHorizonBeforeDays(), other.getHorizonBeforeDays())
                && Objects.equals(this.getPayoutDisplayNumber(), other.getPayoutDisplayNumber())
                && Objects.equals(this.getPayoutFullScreen(), other.getPayoutFullScreen())
                && Objects.equals(this.getPayoutCompleteLineCount(), other.getPayoutCompleteLineCount())
                && Objects.equals(this.getPayoutWaitingLineCount(), other.getPayoutWaitingLineCount())
                && Objects.equals(this.getPickingLineCount(), other.getPickingLineCount())
                && Objects.equals(this.getReceptionLineCount(), other.getReceptionLineCount())
                && Objects.equals(this.getPayoutCompleteDisplayDays(), other.getPayoutCompleteDisplayDays())
                && Objects.equals(this.getPagingIntervalSeconds(), other.getPagingIntervalSeconds())
                && Objects.equals(this.getFontSizePs(), other.getFontSizePs())
                && Objects.equals(this.getTitleSize(), other.getTitleSize())
                && Objects.equals(this.getHeaderSize(), other.getHeaderSize())
                && Objects.equals(this.getColumnSize(), other.getColumnSize())
                && Objects.equals(this.getItemSize(), other.getItemSize())
                && Objects.equals(this.getZoomBarSize(), other.getZoomBarSize())) {
            return true;
        }

        return false;
    }
}
