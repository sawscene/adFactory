/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.model.data;

import adtekfuji.andon.agenda.common.Constants;
import adtekfuji.property.AdProperty;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import jp.adtekfuji.andon.enumerate.*;
import org.apache.commons.lang3.time.DateUtils;

/**
 * 設定情報
 *
 * @author s-heya
 */
public class ConfigData {

    public static final int SEARCH_RANGE = 50;

    private static ConfigData instance = null;
    private final String pattern[] = {"HH:mm"};

    private AndonMonitorTypeEnum monitorType;
    private DisplayModeEnum mode;
    private String adFactoryServerURI;

    private int diplayedSideNum;
    private String kanbanStatuses;
    private String kanbanStartDate;
    private String kanbanEndDate;
    private double PrefHeightTitle = 50.0;
    private double PrefHeightPlanActual = 30.0;
    private int restMaxSize;

    // 表示条件
    private Date startTime;// 開始時間
    private Date endTime;// 終了時間
    private int updateInterval;// 更新間隔(分)
    private String modelName;// モデル名
    private Long displayPeriod; // 表示期間(日)
    private DisplayOrderEnum displayOrder; // 表示順

    private String nowBarColor;   // 現在時刻のバーを塗る場合の色
    private Boolean isShowTimeLine;  // 現在の時刻にラインを表示する色
    private boolean isChangeEvenColor = false; // 偶数行の色を変更するか?
    private boolean isFlatPanel = false; // フラットタイプのパネルにするか?

    // 休憩時間
    private LinkedList<BreakTime> breakTimeCollection = new LinkedList<>();

    // 画面設定(縦軸)
    private int targetMonitor;// (縦軸)ディスプレイ番号
    private boolean isFullScreen;// (縦軸)フルスクリーン表示
    private TimeAxisEnum timeAxis;// 時間軸(横軸と共通)
    private ShowOrder showOrder;// (縦軸)表示順
    private boolean onlyPlaned;// (縦軸)予定のみ表示
    private boolean showActualTime;// (縦軸)進捗時間表示
    private boolean displaySupportResults;// (縦軸)応援者の実績を表示
    private ContentTypeEnum contentType;// (縦軸)詳細表示
    private int timeUnit;// (縦軸)時間軸の表示単位(分)
    private boolean togglePages;// (縦軸)ページ切り替え
    private int toggleTime;// (縦軸)ページ切り替え間隔(秒)
    private boolean autoScroll;// (縦軸)自動スクロール
    private Date autoScrollTime;// (縦軸)自動スクロール範囲
    private AgendaDisplayPatternEnum agendaDisplayPattern;//(縦軸)まとめて表示

    // Liteモニターのみの設定
    private int processDisplayColumns;// 表示列数
    private int processDisplayRows;// 表示行数
    private int processPageSwitchingInterval;// 工程ページ切替間隔(秒)
    private String callSoundSetting;// 呼出音設定
    
    // 画面設定(横軸)
    private int horizonTargetMonitor;// (横軸)ディスプレイ番号
    private boolean horizonIsFullScreen;// (横軸)フルスクリーン表示
    private TimeScaleEnum horizonTimeScale;// (横軸)時間軸スケール
    private int horizonTimeUnit;// (横軸)時間軸の表示単位(分)
    private int horizonShowDays;// (横軸)表示日数
    private int horizonShowMonths;// (横軸)表示月数
    private boolean horizonShowHoliday;// (横軸)休日表示
    private int horizonColumnNum;// (横軸)カラム数
    private ShowOrder horizonShowOrder;// (横軸)表示順
    private int horizonRowHight;// (横軸)行の高さ(ピクセル)
    private boolean horizonTogglePages;// (横軸)ページ切り替え
    private int horizonToggleTime;// (横軸)ページ切り替え間隔(秒)
    private boolean horizonAutoScroll;// (横軸)自動スクロール
    private Date horizonAutoScrollTime;// (横軸)自動スクロール範囲
    private AgendaDisplayPatternEnum horizonAgendaDisplayPattern;//(横軸)まとめて表示
    private PlanActualShowTypeEnum horizonPlanActualShowType;// (横軸)予実表示
    private boolean horizonShowActualTime; // (横軸)進捗時間表示
    private boolean horizonDisplaySupportResults;// (横軸)応援者の実績を表示
    private ContentTypeEnum horizonContentType;// (横軸)詳細表示
    private BeforeDaysEnum horizonBeforeDays;

    // フォントサイズ
    private double titleFontSize;// タイトルのフォントサイズ
    private double headerFontSize;// ヘッダーのフォントサイズ
    private double columnFontSize;// カラムのフォントサイズ
    private double itemFontSize;// アイテムのフォントサイズ
    private double zoomFontSize;// 拡大スライドバーのフォントサイズ

    // 画面設定(払出状況)
    /** (払出状況)ディスプレイ番号 */
    private int payoutDisplayNumber;
    /** (払出状況)フルスクリーン表示 */
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

    
    /**
     * 設備識別名
     */
    private String equipmentIdName;

    /**
     * コンストラクタ
     */
    private ConfigData() {
    }

    /**
     * ConfigDataのインスタンスを取得する。
     *
     * @return ConfigDataのインスタンス
     */
    public static ConfigData getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ConfigData();
        }
        return instance;
    }

    /**
     * ConfigDataのインスタンスを設定する。
     *
     * @param instance ConfigDataのインスタンス
     */
    public static void setInstance(ConfigData instance) {
        ConfigData.instance = instance;
    }

    /**
     * adFactoryServerURIを取得する。
     *
     * @return adFactoryServerURI
     */
    public String getAdFactoryServerURI() {
        return this.adFactoryServerURI;
    }

    /**
     * adFactoryServerURIを設定する。
     *
     * @param adFactoryServerURI adFactoryServerURI
     */
    public void setAdFactoryServerURI(String adFactoryServerURI) {
        this.adFactoryServerURI = adFactoryServerURI;
    }

    /**
     *
     */
    public void clearDisplayDataes() {
        //startTime = "";
        //endTime = "";
    }

    public void setMonitorType(AndonMonitorTypeEnum monitorType) { this.monitorType = monitorType; }

    public AndonMonitorTypeEnum getMonitorType() { return this.monitorType; }

    public void setMode(DisplayModeEnum mode) { this.mode = mode; }

    public DisplayModeEnum getMode() { return this.mode; }

    /**
     * 設備識別名を取得する。
     *
     * @return 設備識別名
     */
    public String getEquipmentIdName() {
        return this.equipmentIdName;
    }

    /**
     * 設備識別名を設定する。
     *
     * @param equipmentIdName 設備識別名
     */
    public void setEquipmentIdName(String equipmentIdName) {
        this.equipmentIdName = equipmentIdName;
    }

    /**
     * diplayedSideNumを取得する。
     *
     * @return diplayedSideNum
     */
    public int getDiplayedSideNum() {
        return this.diplayedSideNum;
    }

    /**
     * diplayedSideNumを設定する。
     *
     * @param diplayedSideNum diplayedSideNum
     */
    public void setDiplayedSideNum(int diplayedSideNum) {
        this.diplayedSideNum = diplayedSideNum;
    }

    /**
     * カンバンステータスを取得する。
     *
     * @return カンバンステータス
     */
    public String getKanbanStatuses() {
        return this.kanbanStatuses;
    }

    /**
     * カンバンステータスを設定する。
     *
     * @param kanbanStatuses カンバンステータス
     */
    public void setKanbanStatuses(String kanbanStatuses) {
        this.kanbanStatuses = kanbanStatuses;
    }

    /**
     * kanbanStartDateを取得する。
     *
     * @return kanbanStartDate
     */
    public String getKanbanStartDate() {
        return this.kanbanStartDate;
    }

    /**
     * kanbanStartDate を設定する。
     *
     * @param kanbanStartDate kanbanStartDate
     */
    public void setKanbanStartDate(String kanbanStartDate) {
        this.kanbanStartDate = kanbanStartDate;
    }

    /**
     * kanbanEndDateを取得する。
     *
     * @return kanbanEndDate
     */
    public String getKanbanEndDate() {
        return this.kanbanEndDate;
    }

    /**
     * kanbanEndDateを設定する。
     *
     * @param kanbanEndDate kanbanEndDate
     */
    public void setKanbanEndDate(String kanbanEndDate) {
        this.kanbanEndDate = kanbanEndDate;
    }

    /**
     * タイトルの高さを取得する。
     *
     * @return タイトルの高さ
     */
    public double getPrefHeightTitle() {
        return this.PrefHeightTitle;
    }

    /**
     * タイトルの高さを設定する。
     *
     * @param PrefHeightTitle タイトルの高さ
     */
    public void setPrefHeightTitle(double PrefHeightTitle) {
        this.PrefHeightTitle = PrefHeightTitle;
    }

    /**
     * 計画・実績の高さを取得する。
     *
     * @return 計画・実績の高さ
     */
    public double getPrefHeightPlanActual() {
        return this.PrefHeightPlanActual;
    }

    /**
     * 計画・実績の高さを設定する。
     *
     * @param PrefHeightPlanActual 計画・実績の高さ
     */
    public void setPrefHeightPlanActual(double PrefHeightPlanActual) {
        this.PrefHeightPlanActual = PrefHeightPlanActual;
    }

    /**
     * RESTの最大取得数を取得する。
     *
     * @return RESTの最大取得数
     */
    public int getRestMaxSize() {
        return this.restMaxSize;
    }

    /**
     * RESTの最大取得数を設定する。
     *
     * @param restMaxSize RESTの最大取得数
     */
    public void setRestMaxSize(int restMaxSize) {
        this.restMaxSize = restMaxSize;
    }

    /**
     * 開始時間を取得する。
     *
     * @return 開始時間
     */
    public Date getStartTime() {
        return new Date(this.startTime.getTime());
    }

    /**
     * 開始時間を設定する。
     *
     * @param startTime 開始時間
     */
    public void setStartTime(String startTime) {
        try {
            this.startTime = DateUtils.parseDate(startTime, this.pattern);
        } catch (Exception ex) {
        }
    }

    /**
     * 終了時間を取得する。
     *
     * @return 終了時間
     */
    public Date getEndTime() {
        return new Date(this.endTime.getTime());
    }

    /**
     * 終了時間を設定する。
     *
     * @param endTime 終了時間
     */
    public void setEndTime(String endTime) {
        try {
            this.endTime = DateUtils.parseDate(endTime, this.pattern);
        } catch (Exception ex) {
        }
    }

    /**
     * 更新間隔(分)を取得する。
     *
     * @return 更新間隔(分)
     */
    public int getUpdateInterval() {
        return this.updateInterval;
    }

    /**
     * 更新時間(ミリ秒)を取得する。(分からミリ秒単位に変換)
     *
     * @return 更新時間(ミリ秒)
     */
    public int getUpdateIntervalMillisec() {
        return this.updateInterval * 60 * 1000;
    }

    /**
     * 更新間隔(分)を設定する。
     *
     * @param updateInterval 更新間隔(分)
     */
    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 表示期間取得
     * @return 表示期間
     */
    public Long getDisplayPeriod() {
        return displayPeriod;
    }

    /**
     * 表示期間設定
     * @param displayPeriod 表示期間設定
     */
    public void setDisplayPeriod(Long displayPeriod) {
        this.displayPeriod = displayPeriod;
    }

    /**
     * 表示順
     * @return 表示順
     */
    public DisplayOrderEnum getDisplayOrder() {
        return displayOrder;
    }

    /**
     * 表示順
     * @param displayOrder 表示順
     */
    public void setDisplayOrder(DisplayOrderEnum displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * 休憩時間一覧を取得する。
     *
     * @return 休憩時間一覧
     */
    public LinkedList<BreakTime> getBreakTimes() {
        return this.breakTimeCollection;
    }

    /**
     * 休憩時間一覧を設定する。
     *
     * @param breakTimeCollection 休憩時間一覧
     */
    public void setBreakTimes(LinkedList<BreakTime> breakTimeCollection) {
        this.breakTimeCollection = breakTimeCollection;
    }

    /**
     * ディスプレイ番号を取得する。
     *
     * @return ディスプレイ番号
     */
    public int getTargetMonitor() {
        if (DisplayModeEnum.PAYOUT_STATUS.equals(this.mode)) {
            return this.payoutDisplayNumber;
        }

        switch (this.timeAxis) {
            case HorizonAxis:
                return this.horizonTargetMonitor;
            case VerticalAxis:
            default:
                return this.targetMonitor;
        }
    }

    /**
     * (縦軸)ディスプレイ番号を設定する。
     *
     * @param targetMonitor (縦軸)ディスプレイ番号
     */
    public void setTargetMonitor(int targetMonitor) {
        this.targetMonitor = targetMonitor;
    }

    /**
     * フルスクリーン表示するかを取得する。
     *
     * @return フルスクリーン表示 (true:する, false:しない)
     */
    public boolean isFullScreen() {
        if (DisplayModeEnum.PAYOUT_STATUS.equals(this.mode)) {
            return this.payoutFullScreen;
        }

        switch (this.timeAxis) {
            case HorizonAxis:
                return horizonIsFullScreen;
            case VerticalAxis:
            default:
                return isFullScreen;
        }
    }

    /**
     * (縦軸)フルスクリーン表示を設定する。
     *
     * @param isFullScreen (縦軸)フルスクリーン表示
     */
    public void setFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
    }

    /**
     * 縦時間軸かどうかを取得する。
     *
     * @return 縦時間軸か (true:縦, false:横)
     */
    public boolean isVerticalAxis() {
        return this.timeAxis == TimeAxisEnum.VerticalAxis;
    }

    /**
     * 時間軸を取得する。(横軸と共通)
     *
     * @return 時間軸
     */
    public TimeAxisEnum getTimeAxis() {
        return this.timeAxis;
    }

    /**
     * 時間軸を設定する。(横軸と共通)
     *
     * @param timeAxis 時間軸
     */
    public void setTimeAxis(TimeAxisEnum timeAxis) {
        this.timeAxis = timeAxis;
    }

    /**
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public ShowOrder getShowOrder() {
        switch (this.timeAxis) {
            case HorizonAxis:
                return this.horizonShowOrder;
            case VerticalAxis:
            default:
                return this.showOrder;
        }
    }

    /**
     * (縦軸)表示順を設定する。
     *
     * @param showOrder (縦軸)表示順
     */
    public void setShowOrder(ShowOrder showOrder) {
        this.showOrder = showOrder;
    }

    /**
     * (縦軸)予定のみ表示を取得する。
     *
     * @return (縦軸)予定のみ表示
     */
    public boolean isOnlyPlaned() {
        return this.onlyPlaned;
    }

    /**
     * (縦軸)予定のみ表示を設定する。
     *
     * @param onlyPlaned (縦軸)予定のみ表示
     */
    public void setOnlyPlaned(boolean onlyPlaned) {
        this.onlyPlaned = onlyPlaned;
    }
    
    /**
     * (縦軸)進捗時間表示を取得する。
     * 
     * @return (縦軸)進捗時間表示
     */
    public boolean getShowActualTime() {
        return this.showActualTime;
    }
    
    /**
     * (縦軸)進捗時間表示を設定する。
     * @param val 進捗時間表示
     */
    public void setShowActualTime(boolean val) {
        this.showActualTime = val;
    }

    /**
     * 応援者の実績を表示するかを取得する。
     *
     * @return 応援者の実績を表示 (true:する, false:しない)
     */
    public boolean isDisplaySupportResults() {
        switch (this.timeAxis) {
            case HorizonAxis:
                return this.horizonDisplaySupportResults;
            case VerticalAxis:
            default:
                return this.displaySupportResults;
        }
    }

    /**
     * (縦軸)応援者の実績を表示を設定する。
     *
     * @param displaySupportResults (縦軸)応援者の実績を表示
     */
    public void setDisplaySupportResults(boolean displaySupportResults) {
        this.displaySupportResults = displaySupportResults;
    }

    /**
     * (縦軸)詳細表示を取得する。
     *
     * @return (縦軸)詳細表示
     */
    public ContentTypeEnum getContentType() {
        switch (this.timeAxis) {
            case HorizonAxis:
                return this.horizonContentType;
            case VerticalAxis:
            default:
                return this.contentType;
        }
    }

    /**
     * (縦軸)詳細表示を設定する。
     *
     * @param contentType (縦軸)詳細表示
     */
    public void setContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
    }

    /**
     * (縦軸)時間軸の表示単位(分)を取得する。
     *
     * @return (縦軸)時間軸の表示単位(分)
     */
    public int getTimeUnit() {
        return this.timeUnit;
    }

    /**
     * (縦軸)時間軸の表示単位(分)を設定する。
     *
     * @param timeUnit (縦軸)時間軸の表示単位(分)
     */
    public void setTimeUnit(int timeUnit) {
        this.timeUnit = timeUnit;
    }

    /**
     * 表示日数を取得する。
     *
     * @return　設定値
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
     * 表示月数を取得する。
     *
     * @return　設定値
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
     * 休日を表示するか (TRUE=表示する)
     *
     * @return 休日を標示するかどうか
     */
    public boolean isShowHoliday() {
        switch (this.timeAxis) {
            case HorizonAxis:
                if (this.isTimeScaleDay()) {
                    return this.getHorizonShowHoliday();
                } else {
                    return true;
                }
            case VerticalAxis:
            default:
                return true;
        }
    }

    /**
     * ページ切り替えを取得する。
     *
     * @return ページ切り替え
     */
    public boolean isTogglePages() {
        switch (this.timeAxis) {
            case HorizonAxis:
                return this.horizonTogglePages;
            case VerticalAxis:
            default:
                return this.togglePages;
        }
    }

    /**
     * (縦軸)ページ切り替えを設定する。
     *
     * @param togglePages (縦軸)ページ切り替え
     */
    public void setTogglePages(boolean togglePages) {
        this.togglePages = togglePages;
    }

    /**
     * (縦軸)ページ切り替え間隔(秒)を取得する。
     *
     * @return (縦軸)ページ切り替え間隔(秒)
     */
    public int getToggleTime() {
        return this.toggleTime;
    }

    /**
     * (縦軸)ページ切り替え間隔(秒)を設定する。
     *
     * @param toggleTime (縦軸)ページ切り替え間隔(秒)
     */
    public void setToggleTime(int toggleTime) {
        this.toggleTime = toggleTime;
    }

    /**
     * 自動スクロールするかを取得する。
     * ※払出状況の場合は'しない'固定
     *
     * @return 自動スクロール (true:する, false:しない)
     */
    public boolean isAutoScroll() {
        if (DisplayModeEnum.PAYOUT_STATUS.equals(this.mode)
                || DisplayModeEnum.PRODUCT_PROGRESS.equals(this.mode)) {
            return false;
        }

        switch (this.timeAxis) {
            case HorizonAxis:
                if (horizonTimeScale.equals(TimeScaleEnum.Time)) {
                    return this.horizonAutoScroll;
                } else {
                    return false;
                }
            case VerticalAxis:
            default:
                return this.autoScroll;
        }
    }

    /**
     * (縦軸)自動スクロールを設定する。
     *
     * @param autoScroll (縦軸)自動スクロール
     */
    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    /**
     * (縦軸)自動スクロール範囲を取得する。
     *
     * @return (縦軸)自動スクロール範囲
     */
    public Date getAutoScrollTime() {
        return this.autoScrollTime;
    }

    /**
     * (縦軸)自動スクロール範囲を設定する。
     *
     * @param autoScrollTime (縦軸)自動スクロール範囲
     */
    public void setAutoScrollTime(String autoScrollTime) {
        try {
            this.autoScrollTime = DateUtils.parseDate(autoScrollTime, this.pattern);
        } catch (Exception ex) {
        }
    }

    /**
     * (横軸)ディスプレイ番号を設定する。
     *
     * @param targetMonitor (横軸)ディスプレイ番号
     */
    public void setHorizonTargetMonitor(int targetMonitor) {
        this.horizonTargetMonitor = targetMonitor;
    }

    /**
     * (横軸)フルスクリーン表示を設定する
     *
     * @param isFullScreen (横軸)フルスクリーン表示
     */
    public void setHorizonFullScreen(boolean isFullScreen) {
        this.horizonIsFullScreen = isFullScreen;
    }

    /**
     * (横軸)時間軸スケールを取得する。
     *
     * @return (横軸)時間軸スケール
     */
    public TimeScaleEnum getHorizonTimeScale() {
        return this.horizonTimeScale;
    }

    /**
     * (横軸)時間軸スケールを設定する。
     *
     * @param timeScaleEnum (横軸)時間軸スケール
     */
    public void setHorizonTimeScale(TimeScaleEnum timeScaleEnum) {
        this.horizonTimeScale = timeScaleEnum;
    }

    /**
     * (横軸)時間軸の表示単位(分)を取得する。
     *
     * @return (横軸)時間軸の表示単位(分)
     */
    public int getHorizonTimeUnit() {
        return this.horizonTimeUnit;
    }

    /**
     * (横軸)時間軸の表示単位(分)を設定する。
     *
     * @param timeUnit (横軸)時間軸の表示単位(分)
     */
    public void setHorizonTimeUnit(int timeUnit) {
        this.horizonTimeUnit = timeUnit;
    }

    /**
     * (横軸)表示日数を取得する。
     *
     * @return (横軸)表示日数
     */
    public int getHorizonShowDays() {
        return this.horizonShowDays;
    }

    /**
     * (横軸)表示日数を設定する。
     *
     * @param days (横軸)表示日数
     */
    public void setHorizonShowDays(int days) {
        this.horizonShowDays = days;
    }

    /**
     * (横軸)表示月数を取得する。
     *
     * @return (横軸)表示月数
     */
    public int getHorizonShowMonths() {
        return horizonShowMonths;
    }

    /**
     * (横軸)表示月数を設定する。
     *
     * @param months (横軸)表示月数
     */
    public void setHorizonShowMonths(int months) {
        this.horizonShowMonths = months;
    }

    /**
     * (横軸)休日表示を取得する。
     *
     * @return (横軸)休日表示
     */
    public boolean getHorizonShowHoliday() {
        return this.horizonShowHoliday;
    }

    /**
     * (横軸)休日表示を設定する。
     *
     * @param showHoliday (横軸)休日表示
     */
    public void setHorizonShowHoliday(boolean showHoliday) {
        this.horizonShowHoliday = showHoliday;
    }

    /**
     * (横軸)カラム数を取得する。
     *
     * @return (横軸)カラム数
     */
    public int getHorizonColumnNum() {
        return this.horizonColumnNum;
    }

    /**
     * (横軸)カラム数を設定する。
     *
     * @param num (横軸)カラム数
     */
    public void setHorizonColumnNum(int num) {
        this.horizonColumnNum = num;
    }

    /**
     * (横軸)表示順を設定する。
     *
     * @param showOrder (横軸)表示順
     */
    public void setHorizonShowOrder(ShowOrder showOrder) {
        this.horizonShowOrder = showOrder;
    }

    /**
     * (横軸)行の高さ(ピクセル)を取得する。
     *
     * @return (横軸)行の高さ(ピクセル)
     */
    public int getHorizonRowHight() {
        return this.horizonRowHight;
    }

    /**
     * (横軸)行の高さ(ピクセル)を設定する。
     *
     * @param hight (横軸)行の高さ(ピクセル)
     */
    public void setHorizonRowHight(int hight) {
        this.horizonRowHight = hight;
    }

    /**
     * (横軸)ページ切り替えを取得する。
     *
     * @return (横軸)ページ切り替え
     */
    public Boolean getHorizonTogglePages() {
        return this.horizonTogglePages;
    }

    /**
     * (横軸)ページ切り替えを設定する。
     *
     * @param horizonTogglePages (横軸)ページ切り替え
     */
    public void setHorizonTogglePages(Boolean horizonTogglePages) {
        this.horizonTogglePages = horizonTogglePages;
    }

    /**
     * (横軸)ページ切り替え間隔(秒)を取得する。
     *
     * @return (横軸)ページ切り替え間隔(秒)
     */
    public int getHorizonToggleTime() {
        return this.horizonToggleTime;
    }

    /**
     * (横軸)ページ切り替え間隔(秒)を設定する。
     *
     * @param horizonToggleTime (横軸)ページ切り替え間隔(秒)
     */
    public void setHorizonToggleTime(int horizonToggleTime) {
        this.horizonToggleTime = horizonToggleTime;
    }

    /**
     * (横軸)自動スクロールを取得する。
     *
     * @return (横軸)自動スクロール
     */
    public Boolean getHorizonAutoScroll() {
        return this.horizonAutoScroll;
    }

    /**
     * (横軸)自動スクロールを設定する。
     *
     * @param horizonAutoScroll (横軸)自動スクロール
     */
    public void setHorizonAutoScroll(Boolean horizonAutoScroll) {
        this.horizonAutoScroll = horizonAutoScroll;
    }

    /**
     * (横軸)自動スクロール範囲を取得する。
     *
     * @return (横軸)自動スクロール範囲
     */
    public Date getHorizonAutoScrollTime() {
        return this.horizonAutoScrollTime;
    }

    /**
     * (横軸)自動スクロール範囲を設定する。
     *
     * @param horizonAutoScrollTime (横軸)自動スクロール範囲
     */
    public void setHorizonAutoScrollTime(String horizonAutoScrollTime) {
        try {
            this.horizonAutoScrollTime = DateUtils.parseDate(horizonAutoScrollTime, pattern);
        } catch (Exception ex) {
        }
    }

    /**
     * (横軸)予実表示を取得する。
     *
     * @return (横軸)予実表示
     */
    public PlanActualShowTypeEnum getHorizonPlanActualShowType() {
        return DisplayModeEnum.PRODUCT_PROGRESS.equals(this.mode) ? PlanActualShowTypeEnum.PlanOnly : this.horizonPlanActualShowType;
    }

    /**
     * (横軸)予実表示を設定する。
     *
     * @param planActualShowType (横軸)予実表示
     */
    public void setHorizonPlanActualShowType(PlanActualShowTypeEnum planActualShowType) {
        this.horizonPlanActualShowType = planActualShowType;
    }

    /**
     * (横軸)進捗時間表示を取得する。
     * @return 進捗時間表示
     */
    public boolean getHorizonShowActualTime() {
        return this.horizonShowActualTime;
    }

    /**
     * (横軸)進捗時間表示を設定
     * @param val 進捗時間表示
     */
    public void setHorizonShowActualTime(boolean val) {
        this.horizonShowActualTime = val;
    }

    /**
     * (横軸)応援者の実績を表示を取得する。
     *
     * @return (横軸)応援者の実績を表示
     */
    public boolean isHorizonDisplaySupportResults() {
        return this.horizonDisplaySupportResults;
    }

    /**
     * (横軸)応援者の実績を表示を設定する。
     *
     * @param horizonDisplaySupportResults (横軸)応援者の実績を表示
     */
    public void setHorizonDisplaySupportResults(boolean horizonDisplaySupportResults) {
        this.horizonDisplaySupportResults = horizonDisplaySupportResults;
    }

    /**
     * (横軸)詳細表示を設定する。
     *
     * @param contentType (横軸)詳細表示
     */
    public void setHorizonContentType(ContentTypeEnum contentType) {
        this.horizonContentType = contentType;
    }

    /**
     * タイトルのフォントサイズを取得する。
     *
     * @return タイトルのフォントサイズ
     */
    public double getTitleFontSize() {
        return this.titleFontSize;
    }

    /**
     * タイトルのフォントサイズを設定する。
     *
     * @param titleFontSize タイトルのフォントサイズ
     */
    public void setTitleFontSize(double titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    /**
     * ヘッダーのフォントサイズを取得する。
     *
     * @return ヘッダーのフォントサイズ
     */
    public double getHeaderFontSize() {
        return this.headerFontSize;
    }

    /**
     * ヘッダーのフォントサイズを設定する。
     *
     * @param headerFontSize ヘッダーのフォントサイズ
     */
    public void setHeaderFontSize(double headerFontSize) {
        this.headerFontSize = headerFontSize;
    }

    /**
     * カラムのフォントサイズを取得する。
     *
     * @return カラムのフォントサイズ
     */
    public double getColumnFontSize() {
        return this.columnFontSize;
    }

    /**
     * カラムのフォントサイズを設定する。
     *
     * @param columnFontSize カラムのフォントサイズ
     */
    public void setColumnFontSize(double columnFontSize) {
        this.columnFontSize = columnFontSize;
    }

    /**
     * アイテムのフォントサイズを取得する。
     *
     * @return アイテムのフォントサイズ
     */
    public double getItemFontSize() {
        return this.itemFontSize;
    }

    /**
     * アイテムのフォントサイズを設定する。
     *
     * @param itemFontSize アイテムのフォントサイズ
     */
    public void setItemFontSize(double itemFontSize) {
        this.itemFontSize = itemFontSize;
    }

    /**
     * 拡大スライドバーのフォントサイズを取得する。
     *
     * @return 拡大スライドバーのフォントサイズ
     */
    public double getZoomFontSize() {
        return this.zoomFontSize;
    }

    /**
     * 拡大スライドバーのフォントサイズを設定する。
     *
     * @param zoomFontSize 拡大スライドバーのフォントサイズ
     */
    public void setZoomFontSize(double zoomFontSize) {
        this.zoomFontSize = zoomFontSize;
    }

    /**
     * 時間軸スケールが時間か判定する
     *
     * @return TRUE:時間 FALSE:それ以外
     */
    public boolean isTimeScaleTime() {
        switch (this.timeAxis) {
            case HorizonAxis:
                return (this.horizonTimeScale.equals(TimeScaleEnum.Time));
            case VerticalAxis:
            default:
                return true;
        }
    }

    /**
     * 時間軸スケールが1日(or半日)か判定する
     *
     * @return TRUE:1日or半日 FALSE:それ以外
     */
    public boolean isTimeScaleDay() {
        switch (this.timeAxis) {
            case HorizonAxis:
                return (this.horizonTimeScale.equals(TimeScaleEnum.Day)
                        || this.horizonTimeScale.equals(TimeScaleEnum.HalfDay));
            case VerticalAxis:
            default:
                return false;
        }
    }

    /**
     * 時間軸スケールが1月(or週)か判定する
     *
     * @return TRUE:1月or週 FALSE:それ以外
     */
    public boolean isTimeScaleMonth() {
        switch (this.timeAxis) {
            case HorizonAxis:
                return (this.horizonTimeScale.equals(TimeScaleEnum.Month)
                        || this.horizonTimeScale.equals(TimeScaleEnum.Week));
            case VerticalAxis:
            default:
                return false;
        }
    }

    /**
     * 予実の表示パターンを取得
     * @return 設定値
     */
    public AgendaDisplayPatternEnum getAgendaDisplayPattern() { return agendaDisplayPattern; }

    /**
     * 予実表示パターンを設定
     * @param in 設定値
     */
    public void setAgendaDisplayPattern(AgendaDisplayPatternEnum in) { agendaDisplayPattern = in; }

    /**
     * 表示列数を取得
     * @return 設定値
     */
    public int getProcessDisplayColumns() { return processDisplayColumns; }

    /**
     * 表示列数を設定
     * @param in 設定値
     */
    public void setProcessDisplayColumns(int in) { processDisplayColumns = in; }

    /**
     * 表示行数を取得
     * @return 設定値
     */
    public int getProcessDisplayRows() { return processDisplayRows; }

    /**
     * 表示行数を設定
     * @param in 設定値
     */
    public void setProcessDisplayRows(int in) { processDisplayRows = in; }

    /**
     * 工程ページ切替間隔を取得
     * @return 設定値
     */
    public int getProcessPageSwitchingInterval() { return processPageSwitchingInterval; }

    /**
     * 工程ページ切替間隔を設定
     * @param in 設定値
     */
    public void setProcessPageSwitchingInterval(int in) { processPageSwitchingInterval = in; }

    /**
     * 呼出音設定を取得
     * @return 設定値
     */
    public String getCallSoundSetting() { return callSoundSetting; }

    /**
     * 呼出音設定を設定
     * @param in 設定値
     */
    public void setCallSoundSetting(String in) { callSoundSetting = in; }

    /**
     * 予実の表示パターンを取得
     * @return 設定値
     */
    public AgendaDisplayPatternEnum getHorizonAgendaDisplayPattern() { return horizonAgendaDisplayPattern; }

    /**
     * 予実表示パターンを設定
     * @param in 設定値
     */
    public void setHorizonAgendaDisplayPattern(AgendaDisplayPatternEnum in) { horizonAgendaDisplayPattern = in; }

    /**
     * 計画アジェンダを表示するか (TRUE=表示する)
     *
     * @return 計画を表示するかどうか
     */
    public boolean isShowPlan() {
        if (DisplayModeEnum.PRODUCT_PROGRESS.equals(this.mode)) {
            return true;
        }

        switch (this.timeAxis) {
            case HorizonAxis:
                return !(this.horizonPlanActualShowType.equals(PlanActualShowTypeEnum.ActualOnly));
            case VerticalAxis:
            default:
                return true;
        }
    }

    /**
     * 現在時間のバーの色を取得
     * @return 現在時刻のバーの色
     */
    public String getNowBarColor() {
        return nowBarColor;
    }

    /**
     * 現在時刻のバーの色の設定
     * @param nowBarColor 現在時刻のバーの色
     */
    public void setNowBarColor(String nowBarColor) {
        this.nowBarColor = nowBarColor;
    }

    /**
     * 現在時刻ラインの表示非表示
     * @return true: 表示, false:非表示
     */
    public Boolean getShowTimeLine() {
        return isShowTimeLine;
    }

    /**
     * 現在時刻ラインの表示非表示を設定
     * @param showTimeLine true: 表示, false:非表示
     */
    public void setShowTimeLine(Boolean showTimeLine) {
        isShowTimeLine = showTimeLine;
    }

    /**
     * 奇数行の色を変更するか?
     * @return true する false しない
     */
    public boolean getChangeEvenColor() {
        return isChangeEvenColor;
    }

    /**
     * 奇数行の色を変更するか?
     * @param changeEvenColor true: する, false: しない
     */
    public void setChangeEvenColor(boolean changeEvenColor) {
        isChangeEvenColor = changeEvenColor;
    }

    /**
     * フラットタイプのパネルにするか?
     * @return true する, flase しない
     */
    public boolean isFlatPanel() {
        return isFlatPanel;
    }

    /**
     * フラットタイプのパネルにするか?
     * @param flatPanel true する, flase しない
     */
    public void setFlatPanel(boolean flatPanel) {
        isFlatPanel = flatPanel;
    }

    /**
     * 実績アジェンダを表示するか (TRUE=表示する)
     *
     * @return 実績を表示するかどうか
     */
    public boolean isShowActual() {
        if (DisplayModeEnum.PRODUCT_PROGRESS.equals(this.mode)) {
            return true;
        }

        switch (this.timeAxis) {
            case HorizonAxis:
                return !(this.horizonPlanActualShowType.equals(PlanActualShowTypeEnum.PlanOnly));
            case VerticalAxis:
            default:
                return !this.onlyPlaned;
        }
    }

    /**
     * 製品進捗を表示するかを返す
     * 
     * @return 
     */
    public boolean isShowProgress() {
        return DisplayModeEnum.PRODUCT_PROGRESS.equals(this.mode);
    }
    
    /**
     * 表示開始日を取得する
     * 
     * @return 表示開始日
     */
    public BeforeDaysEnum getHorizonBeforeDays(){
        return this.horizonBeforeDays;
    }
    
    /**
     * 表示開始日を取得する
     * 
     * @param beforeDays 表示開始日
     */
    public void setHorizonBeforeDays(BeforeDaysEnum beforeDays){
        this.horizonBeforeDays = beforeDays;
    }
    
    /**
     * 生産数の進捗を表示するかどうかを返す。
     * 
     * @return 
     */
    public boolean isShowProductionProgress() {
        return Constants.PRODUCTION_PROGRESS.equals(AdProperty.getProperties().getProperty(Constants.AGENDA_PRODUCT_PROGRESS, Constants.DEF_AGENDA_PRODUCT_PROGRESS));
    }

    // ******************************
    // ***** 画面設定(払出状況) *****
    // ******************************
    /**
     * (払出状況)ディスプレイ番号を取得する。
     *
     * @return (払出状況)ディスプレイ番号
     */
    public int getPayoutDisplayNumber() {
        return this.payoutDisplayNumber;
    }

    /**
     * (払出状況)ディスプレイ番号を設定する。
     * 
     * @param value (払出状況)ディスプレイ番号
     */
    public void setPayoutDisplayNumber(int value) {
        this.payoutDisplayNumber = value;
    }

    /**
     * (払出状況)フルスクリーン表示を取得する。
     *
     * @return (払出状況)フルスクリーン表示
     */
    public boolean getPayoutFullScreen() {
        return this.payoutFullScreen;
    }

    /**
     * (払出状況)フルスクリーン表示を設定する。
     * 
     * @param value (払出状況)フルスクリーン表示
     */
    public void setPayoutFullScreen(boolean value) {
        this.payoutFullScreen = value;
    }

    /**
     * 払出完了の行数を取得する。
     *
     * @return 払出完了の行数
     */
    public int getPayoutCompleteLineCount() {
        return this.payoutCompleteLineCount;
    }

    /**
     * 払出完了の行数を設定する。
     * 
     * @param value 払出完了の行数
     */
    public void setPayoutCompleteLineCount(int value) {
        this.payoutCompleteLineCount = value;
    }

    /**
     * 払出待ちの行数を取得する。
     * 
     * @return 払出待ちの行数
     */
    public int getPayoutWaitingLineCount() {
        return this.payoutWaitingLineCount;
    }

    /**
     * 払出待ちの行数を設定する。
     *
     * @param value 払出待ちの行数
     */
    public void setPayoutWaitingLineCount(int value) {
        this.payoutWaitingLineCount = value;
    }

    /**
     * ピッキング中の行数を取得する。
     * 
     * @return ピッキング中の行数
     */
    public int getPickingLineCount() {
        return this.pickingLineCount;
    }

    /**
     * ピッキング中の行数を設定する。
     * 
     * @param value ピッキング中の行数
     */
    public void setPickingLineCount(int value) {
        this.pickingLineCount = value;
    }

    /**
     * 受付の行数を取得する。
     * 
     * @return 受付の行数
     */
    public int getReceptionLineCount() {
        return this.receptionLineCount;
    }

    /**
     * 受付の行数を設定する。
     * 
     * @param value 受付の行数
     */
    public void setReceptionLineCount(int value) {
        this.receptionLineCount = value;
    }

    /**
     * 払出完了の表示日数を取得する。
     * 
     * @return 払出完了の表示日数
     */
    public int getPayoutCompleteDisplayDays() {
        return this.payoutCompleteDisplayDays;
    }

    /**
     * 払出完了の表示日数を設定する。
     * 
     * @param value 払出完了の表示日数
     */
    public void setPayoutCompleteDisplayDays(int value) {
        this.payoutCompleteDisplayDays = value;
    }

    /**
     * ページ切り替え間隔(秒)を取得する。
     *
     * @return ページ切り替え間隔(秒)
     */
    public int getPagingIntervalSeconds() {
        return this.pagingIntervalSeconds;
    }

    /**
     * ページ切り替え間隔(秒)を設定する。
     *
     * @param value ページ切り替え間隔(秒)
     */
    public void setPagingIntervalSeconds(int value) {
        this.pagingIntervalSeconds = value;
    }

    /**
     * フォントサイズを取得する。
     *
     * @return フォントサイズ
     */
    public int getFontSizePs() {
        return this.fontSizePs;
    }

    /**
     * フォントサイズを設定する。
     *
     * @param value フォントサイズ
     */
    public void setFontSizePs(int value) {
        this.fontSizePs = value;
    }
    
    /**
     * 標準サイクルタイムが有効かどうかを返す。
     * 
     * @return 
     */
    public boolean isEnableCycleTime() {
        return Boolean.TRUE.toString().equalsIgnoreCase(AdProperty.getProperties().getProperty("enableCycleTimeImport", "false"));
    }
    
    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ConfigData{")
                .append("adFactoryServerURI=").append(this.adFactoryServerURI)
                .append(", monitorType=").append(monitorType)
                .append(", updateInterval=").append(this.updateInterval)
                .append(", modelName=").append(this.modelName)
                .append(", diplayedSideNum=").append(this.diplayedSideNum)
                .append(", showOrder=").append(this.showOrder)
                .append(", onlyPlaned=").append(this.onlyPlaned)
                .append(", displaySupportResults=").append(this.displaySupportResults)
                .append(", startTime=").append(this.startTime)
                .append(", endTime=").append(this.endTime)
                .append(", contentType=").append(this.contentType)
                .append(", timeUnit=").append(this.timeUnit)
                .append(", autoScroll=").append(this.autoScroll)
                .append(", autoScrollTime=").append(this.autoScrollTime)
                .append(", togglePages=").append(this.togglePages)
                .append(", toggleTime=").append(this.toggleTime)
                .append(", isFullScreen=").append(this.isFullScreen)
                .append(", timeAxis=").append(this.timeAxis)
                .append(", restMaxSize=").append(this.restMaxSize)
                .append(", agendaDisplayPattern=").append(this.agendaDisplayPattern)
                .append(", processDisplayColumns=").append(this.processDisplayColumns)
                .append(", processDisplayRows=").append(this.processDisplayRows)
                .append(", processPageSwitchingInterval=").append(this.processPageSwitchingInterval)
                .append(", callSoundSetting=").append(this.callSoundSetting)
                .append(", horizonTargetMonitor=").append(this.horizonTargetMonitor)
                .append(", horizonIsFullScreen=").append(this.horizonIsFullScreen)
                .append(", horizonTimeScale=").append(this.horizonTimeScale)
                .append(", horizonTimeUnit=").append(this.horizonTimeUnit)
                .append(", horizonShowDays=").append(this.horizonShowDays)
                .append(", horizonShowMonths=").append(this.horizonShowMonths)
                .append(", horizonShowHoliday=").append(this.horizonShowHoliday)
                .append(", horizonColumnNum=").append(this.horizonColumnNum)
                .append(", horizonShowOrder=").append(this.horizonShowOrder)
                .append(", horizonRowHight=").append(this.horizonRowHight)
                .append(", horizonAutoScroll=").append(this.horizonAutoScroll)
                .append(", horizonAutoScrollTime=").append(this.horizonAutoScrollTime)
                .append(", horizonTogglePages=").append(this.horizonTogglePages)
                .append(", horizonToggleTime=").append(this.horizonToggleTime)
                .append(", horizonAgendaDisplayPattern=").append(this.horizonAgendaDisplayPattern)
                .append(", horizonPlanActualShowType=").append(this.horizonPlanActualShowType)
                .append(", horizonDisplaySupportResults=").append(this.horizonDisplaySupportResults)
                .append(", horizonContentType=").append(this.horizonContentType)
                .append(", horizonBeforeDays=").append(this.horizonBeforeDays)
                .append(", breakTimeCollection=").append(this.breakTimeCollection)
                .append(", equipmentIdName=").append(this.equipmentIdName)
                .append(", nowBarColor=").append(this.nowBarColor)
                .append(", isShowTimeLine=").append(this.isShowTimeLine)
                .append(", payoutdisplayNumber=").append(this.payoutDisplayNumber)
                .append(", payoutFullScreen=").append(this.payoutFullScreen)
                .append(", payoutCompleteLineCount=").append(this.payoutCompleteLineCount)
                .append(", payoutWaitingLineCount=").append(this.payoutWaitingLineCount)
                .append(", pickingLineCount=").append(this.pickingLineCount)
                .append(", receptionLineCount=").append(this.receptionLineCount)
                .append(", payoutCompleteDisplayDays=").append(this.payoutCompleteDisplayDays)
                .append(", pagingIntervalSeconds=").append(this.pagingIntervalSeconds)
                .append(", fontSizePs=").append(this.fontSizePs)
                .append("}")
                .toString();
    }
}
