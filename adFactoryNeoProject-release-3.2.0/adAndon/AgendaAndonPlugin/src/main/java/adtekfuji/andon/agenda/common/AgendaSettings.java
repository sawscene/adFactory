/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.common;

import adtekfuji.admanagerapp.andonsetting.utils.KanbanUtils;
import adtekfuji.andon.agenda.model.data.BreakTime;
import adtekfuji.andon.agenda.model.data.ConfigData;
import adtekfuji.andon.agenda.model.data.CurrentData;
import adtekfuji.clientservice.KanbanInfoFacade;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.enumerate.AndonMonitorTypeEnum;
import jp.adtekfuji.andon.enumerate.DisplayModeEnum;
import jp.adtekfuji.andon.property.AgendaMonitorSetting;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.utility.AndonMonitorLineProductSettingFileAccessor;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * サーバーのアジェンダモニター設定とローカルなアジェンタモニター設定との変換
 *
 * @author fu-kato
 */
public class AgendaSettings {

    private static final Logger logger = LogManager.getLogger();
    private static final CurrentData currentData = CurrentData.getInstance();
    private static final ConfigData config = ConfigData.getInstance();
    private static Long monitorId;
    private static AndonMonitorLineProductSetting setting;

    /**
     * 設定を読み込む。
     */
    public static void load() {
        logger.info("load start: monitorId = {}", monitorId);

        try {
            monitorId = AndonLoginFacade.getMonitorId();

            if (monitorId != 0) {
                // サーバーから
                AndonMonitorSettingFacade facade = new AndonMonitorSettingFacade();
                setting = (AndonMonitorLineProductSetting) facade
                        .getLineSetting(monitorId, AndonMonitorLineProductSetting.class);
            }

            if (Objects.isNull(setting)) {
                // ローカルから
                AndonMonitorLineProductSettingFileAccessor accessor = new AndonMonitorLineProductSettingFileAccessor();
                String filePath = accessor.getFilePath();
                File file = new File(filePath);
                if (!file.exists()) {
                    accessor.save(AndonMonitorLineProductSetting.create());
                }
                setting = accessor.load();
            }

            if (Objects.isNull(setting)) {
                setting = AndonMonitorLineProductSetting.create();
            }

            if ((setting.getMonitorType() != AndonMonitorTypeEnum.AGENDA && setting.getMonitorType() != AndonMonitorTypeEnum.LITE_MONITOR)
                    || Objects.isNull(setting.getAgendaMonitorSetting())) {
                setting.setMonitorType(AndonMonitorTypeEnum.AGENDA);
                setting.setAgendaMonitorSetting(AgendaMonitorSetting.create());
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info("load end");
    }

    /**
     * ローカルに設定ファイルを保存する
     *
     * @param value 進捗モニター設定情報
     */
    public static void save(AndonMonitorLineProductSetting value) {
        if (0 != monitorId) {
            // サーバーに保存
            AndonMonitorSettingFacade facade = new AndonMonitorSettingFacade();
            facade.setLineSetting(monitorId, value);
        } else {
            AndonMonitorLineProductSettingFileAccessor accessor = new AndonMonitorLineProductSettingFileAccessor();
            accessor.save(value);
        }
        
        // 設定情報を再ロード
        setting = null;
        AgendaSettings.load();
        AgendaSettings.buildConfigData();
        AgendaSettings.buildCurrentData();
    }

    public static Long getMonitorId() {
        return monitorId;
    }

    public static AndonMonitorLineProductSetting getAndonSetting() {
        return setting;
    }

    /**
     * アジェンダモニター設定からCurrentDataを構築する。
     */
    public static void buildCurrentData() {
        logger.info("buildCurrentData start: " + setting);

        if (Objects.isNull(setting)) {
            return;
        }

        AgendaMonitorSetting agenda = setting.getAgendaMonitorSetting();

        try {
            KanbanInfoFacade kanbanFacade = new KanbanInfoFacade();
            
            currentData.setTimeScale(agenda.getTimeScale());
            
            switch (currentData.getTimeScale()) {
                case Day:
                case HalfDay:
                    currentData.setDays(agenda.getTargetDate(), agenda.getShowDays());
                    break;
                case Week:
                case Month:
                    currentData.setMonths(agenda.getTargetDate(), agenda.getShowMonths());
                    break;
                case Time:
                default:
                    currentData.setDate(agenda.getTargetDate());
                    break;
            }
            
            currentData.setDisplayMode(agenda.getMode());
            currentData.setDisplayPeriod(agenda.getDisplayPeriod());
            currentData.setDisplayOrder(agenda.getDisplayOrder());

            switch (currentData.getDisplayMode()) {
                case WORKER:
                    List<OrganizationInfoEntity> organizations = CacheUtils.getCacheOrganization(agenda.getOrganizationIds());

                    // カレント情報に組織一覧をセットする。
                    currentData.seOrganizations(organizations);
                    break;
                case KANBAN:
                    AndonMonitorTypeEnum monitorType = config.getMonitorType();
                    List<KanbanInfoEntity> result;
                    switch(monitorType) {
                        case AGENDA:
                            result = kanbanFacade.find(agenda.getKanbanIds());
                            currentData.setKanbans(KanbanUtils.sortByIdList(result, agenda.getKanbanIds()));
                            break;
                        case LITE_MONITOR:
                            result = kanbanFacade.find(agenda.getLiteKanbanIds());
                            currentData.setKanbans(KanbanUtils.sortByIdList(result, agenda.getLiteKanbanIds()));
                            break;
                    }
                    break;
                case LINE:
                    // カレント情報に設備ID一覧をセットする。
                    currentData.setEquipmentIds(agenda.getLineIds());
                    break;
                case PRODUCT_PROGRESS:
                    currentData.setKanbanHierarchyIds(agenda.getKanbanHierarchyIds());
                    break;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("buildCurrentData end.");
        }
    }

    /**
     * アジェンダモニター設定からConfigDataを構築する。
     */
    public static void buildConfigData() {
        logger.info("buildConfigData start:" + setting);

        if (Objects.isNull(setting)) {
            return;
        }

        AgendaMonitorSetting agenda = setting.getAgendaMonitorSetting();

        //モニター種別
        config.setMonitorType(setting.getMonitorType());
        // 表示モード
        config.setMode(agenda.getMode());

        // 表示条件
        config.setStartTime(agenda.getStartWorkTime().toString());// 開始時間
        config.setEndTime(agenda.getEndWorkTime().toString());// 終了時間
        config.setUpdateInterval(agenda.getUpdateInterval().intValue());// 更新間隔(分)
        config.setModelName(agenda.getModelName());// モデル名
        config.setDisplayPeriod(agenda.getDisplayPeriod()); // 表示期間設定
        config.setDisplayOrder(agenda.getDisplayOrder()); // 表示順

        if(DisplayModeEnum.PRODUCT_PROGRESS.equals(agenda.getMode())) {
            config.setNowBarColor("#e36969"); // 現在時刻の表示色の色
            config.setShowTimeLine(false);    // タイムラインを表示しない
            config.setChangeEvenColor(false); // 偶数行の色を変えない
            config.setFlatPanel(true);        // フラットのパネルにしない
        } else {
            config.setNowBarColor(null);     // 現在時刻の表示色の色
            config.setShowTimeLine(true);    // タイムラインを表示する
            config.setChangeEvenColor(true); // 偶数行の色を変える
            config.setFlatPanel(false);      // フラットのパネルにする
        }

        // 休憩時間
        config.setBreakTimes(agenda.getBreaktimes().stream()
                .map(entity -> new BreakTime(entity.getBreaktimeId()))
                .collect(Collectors.toCollection(() -> new LinkedList<BreakTime>())));

        // 画面設定(縦軸)
        config.setTargetMonitor(agenda.getDisplayNumber());
        config.setFullScreen(agenda.getFullScreen());
        config.setTimeAxis(agenda.getTimeAxis());
        config.setDiplayedSideNum(agenda.getColumnCount());
        config.setShowOrder(agenda.getShowOrder());
        config.setOnlyPlaned(agenda.getVisibleOnlyPlan());
        config.setShowActualTime(agenda.getShowActualTime());
        config.setDisplaySupportResults(agenda.getDisplaySupportResults());
        config.setContentType(agenda.getContent());
        config.setTogglePages(agenda.getTogglePages());
        config.setToggleTime(agenda.getPageToggleTime());
        config.setTimeUnit(agenda.getTimeUnit());
        config.setAutoScroll(agenda.getAutoScroll());
        config.setAutoScrollTime(agenda.getScrollUnit().toString());
		config.setAgendaDisplayPattern(agenda.getAgendaDisplayPattern());
        
        // Liteモニターのみの設定
        config.setProcessDisplayColumns(agenda.getProcessDisplayColumns());
        config.setProcessDisplayRows(agenda.getProcessDisplayRows());
        config.setProcessPageSwitchingInterval(agenda.getProcessPageSwitchingInterval());
        config.setCallSoundSetting(agenda.getCallSoundSetting());

        // 画面設定(横軸)
        config.setHorizonTargetMonitor(agenda.getHorizonDisplayNumber());
        config.setHorizonFullScreen(agenda.getHorizonFullScreen());
        config.setHorizonTimeScale(agenda.getHorizonTimeScale());
        config.setHorizonTimeUnit(agenda.getHorizonTimeUnit());
        config.setHorizonShowDays(agenda.getHorizonShowDays());
        config.setHorizonShowMonths(agenda.getHorizonShowMonths());
        config.setHorizonShowHoliday(agenda.getHorizonShowHoliday());
        config.setHorizonColumnNum(agenda.getHorizonColumnCount());
        config.setHorizonShowOrder(agenda.getHorizonShowOrder());
        config.setHorizonRowHight(agenda.getHorizonRowHight());
        config.setHorizonTogglePages(agenda.getHorizonTogglePages());
        config.setHorizonToggleTime(agenda.getHorizonPageToggleTime());
        config.setHorizonAutoScroll(agenda.getHorizonAutoScroll());
        config.setHorizonAutoScrollTime(agenda.getHorizonScrollUnit().toString());
        config.setHorizonAgendaDisplayPattern(agenda.getHorizonAgendaDisplayPattern());
        config.setHorizonPlanActualShowType(agenda.getHorizonPlanActualShowType());
        config.setHorizonShowActualTime(agenda.getHorizonShowActualTime());
        config.setHorizonDisplaySupportResults(agenda.getHorizonDisplaySupportResults());
        config.setHorizonContentType(agenda.getHorizonContent());
        config.setHorizonBeforeDays(agenda.getHorizonBeforeDays());
        
        // フォントサイズ
        config.setTitleFontSize(agenda.getTitleSize());
        config.setColumnFontSize(agenda.getColumnSize());
        config.setItemFontSize(agenda.getItemSize());
        config.setHeaderFontSize(agenda.getHeaderSize());
        config.setZoomFontSize(agenda.getZoomBarSize());

        // 画面設定(払出状況)
        config.setPayoutDisplayNumber(agenda.getPayoutDisplayNumber());
        config.setPayoutFullScreen(agenda.getPayoutFullScreen());
        config.setPayoutCompleteLineCount(agenda.getPayoutCompleteLineCount());
        config.setPayoutWaitingLineCount(agenda.getPayoutWaitingLineCount());
        config.setPickingLineCount(agenda.getPickingLineCount());
        config.setReceptionLineCount(agenda.getReceptionLineCount());
        config.setPayoutCompleteDisplayDays(agenda.getPayoutCompleteDisplayDays());
        config.setPagingIntervalSeconds(agenda.getPagingIntervalSeconds());
        config.setFontSizePs(agenda.getFontSizePs());
        
        // その他
        config.setPrefHeightTitle(50.0);
        config.setPrefHeightPlanActual(30.0);

        logger.info("buildConfigData end.");
    }

    /**
     * CurrentData、ConfigDataからAgendaMonitorSettingへデータを更新する。<br>
     */
    public static void updateAndonSettings() {
        // 通常上記DataとAgendaMonitorSettingは一致している
        // 一致しなくなるのはアジェンダモニター側のカレンダーを変更したときのみなのでそれを反映する
        currentData.setFromDate(currentData.getKeepTargetDay());
        setting.getAgendaMonitorSetting().setTargetDate(LocalDateTime.ofInstant(currentData.getFromDate().toInstant(), ZoneId.systemDefault()));
    }
}
