/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.model;

import adtekfuji.andon.agenda.common.AgendaCompoInterface;
import adtekfuji.andon.agenda.common.AgendaSettings;
import adtekfuji.andon.agenda.common.CallingPool;
import adtekfuji.andon.agenda.common.Constants;
import adtekfuji.andon.agenda.common.KanbanStatusConfig;
import adtekfuji.andon.agenda.model.data.Agenda;
import adtekfuji.andon.agenda.model.data.AgendaGroup;
import adtekfuji.andon.agenda.model.data.AgendaPlan;
import adtekfuji.andon.agenda.model.data.AgendaTopic;
import adtekfuji.andon.agenda.model.data.ConfigData;
import adtekfuji.andon.agenda.model.data.CurrentData;
import adtekfuji.andon.agenda.model.data.DurationModel;
import adtekfuji.andon.agenda.model.data.KanbanStatusInfo;
import adtekfuji.andon.agenda.model.data.WorkStatusInfo;
import adtekfuji.andon.agenda.service.AgendaFacade;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.actual.WorkRecordInfoEntity;
import jp.adtekfuji.adFactory.entity.agenda.ActualProductInfoEntity;
import jp.adtekfuji.adFactory.entity.agenda.KanbanTopicInfoEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.operation.IndirectWorkOperationEntity;
import jp.adtekfuji.adFactory.entity.operation.OperateAppEnum;
import jp.adtekfuji.adFactory.entity.operation.OperationAddInfoEntity;
import jp.adtekfuji.adFactory.entity.operation.OperationEntity;
import jp.adtekfuji.adFactory.entity.operation.OperationTypeEnum;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanTopicSearchCondition;
import jp.adtekfuji.adFactory.entity.search.OperationSerachCondition;
import jp.adtekfuji.adFactory.entity.search.PropertySearchCondition;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ActualResultDailyEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.MatchTypeEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.andon.enumerate.*;
import jp.adtekfuji.andon.media.MelodyPlayer;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * アジェンダモデル
 *
 * @author s-heya
 */
public class AgendaModel {
        
    private static AgendaModel instance = null;
    final static Pattern workNumPattern = Pattern.compile("^.+\\s\\[(\\d+(-\\d+)?)\\].*$");

    private final Logger logger = LogManager.getLogger();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final CashManager cache = CashManager.getInstance();
    private final ConfigData config = ConfigData.getInstance();
    private final CurrentData currentData = CurrentData.getInstance();
    private final MelodyPlayer melodyPlayer = new MelodyPlayer();
    private final CallingPool callingPool = CallingPool.getInstance();
    private AgendaFacade agendaFacade;
    private KanbanInfoFacade kanbanFacade;
    private WorkKanbanInfoFacade workKanbanFacade;
    private OrganizationInfoFacade organizationInfoFacade;
    private KanbanHierarchyInfoFacade kanbanHierarchyFacade = new KanbanHierarchyInfoFacade();
    private ActualResultInfoFacade actualResultFacade = new ActualResultInfoFacade();

    private static final int KANBAN_RANGE = 20;// カンバン情報取得の最大カンバン数(この数毎にREST呼び出し)

    // カンバンごとの休憩時間
    private final Map<Long, List<BreakTimeInfoEntity>> kanbanBreakTimeMap = new HashMap<>();
    private final Map<Long, BreakTimeInfoEntity> breakTimeMap = new HashMap<>();
    // 休日
    private final List<Date> allHolidays = new ArrayList();
    private final List<Date> holidays = new ArrayList();
    private List<DisplayedStatusInfoEntity> displayedStatuses;
    private AgendaCompoInterface controller;
    private boolean isUpdate;
    private Timer refreshTimer;
    private Timer systemTimer;
    private Alert alert;
    private final Object lock = new Object();

    // 進捗情報CSVファイルの設定
    private static final String CSV_CHARSET = "MS932";// エンコード
    private static final String CSV_QUOTE = "\"";// 囲み文字
    private static final String CSV_SEPARATOR = ",";// 区切り文字

    // Date比較用
    private final Comparator<Date> dateComparator = (a, b) -> {
        if (a.before(b)) {
            return -1;
        } else if (a.after(b)) {
            return 1;
        } else {
            return 0;
        }
    };

    // 計画時間を比較
    private final Comparator<KanbanTopicInfoEntity> planDateComparator = (a, b) -> dateComparator.compare(
            Objects.isNull(a.getPlanStartTime()) ? new Date(Long.MAX_VALUE) : a.getPlanStartTime(),
            Objects.isNull(b.getPlanStartTime()) ? new Date(Long.MAX_VALUE) : b.getPlanStartTime()
    );

    // 工程カンバン計画順
    private final Comparator<List<KanbanTopicInfoEntity>> topicListComparator = (a, b) -> dateComparator.compare(
            a.stream().min(planDateComparator).map(topic -> topic.getPlanStartTime()).orElse(new Date(Long.MAX_VALUE)),
            b.stream().min(planDateComparator).map(topic -> topic.getPlanStartTime()).orElse(new Date(Long.MAX_VALUE))
    );

    /**
     * 工程カンバンの計画開始時間の比較
     */
    private final Comparator<KanbanTopicInfoEntity> kanbanTimeComparator = (KanbanTopicInfoEntity a, KanbanTopicInfoEntity b) -> {
        return a.getPlanStartTime().compareTo(b.getPlanStartTime());
    };
    
    /**
     * カンバンの比較
     */
    private final Comparator<KanbanTopicInfoEntity> kanbanComparator = (KanbanTopicInfoEntity a, KanbanTopicInfoEntity b) -> {
        return a.getKanbanId().compareTo(b.getKanbanId());
    };
    
    /**
     * 工程の種類の比較
     */
    private final Comparator<KanbanTopicInfoEntity> kanbanWorkComparator = (KanbanTopicInfoEntity a, KanbanTopicInfoEntity b) -> {
        return a.isSeparateWorkFlag().compareTo(b.isSeparateWorkFlag());
    };

    /**
     * カンバンの比較
     */
    private final Comparator<WorkRecordInfoEntity> actualKanbanComparator = (WorkRecordInfoEntity a, WorkRecordInfoEntity b) -> {
        return a.getKanbanId().compareTo(b.getKanbanId());
    };

    /**
     * 実績開始時間の比較
     */
    private final Comparator<WorkRecordInfoEntity> actualTimeComparator = (WorkRecordInfoEntity a, WorkRecordInfoEntity b) -> {
        return a.getActualStartTime().compareTo(b.getActualStartTime());
    };
    
    /**
     * 工程の種類の比較
     */
    private final Comparator<WorkRecordInfoEntity> actualWorkComparator = (WorkRecordInfoEntity a, WorkRecordInfoEntity b) -> {
        return a.isSeparateWorkFlag().compareTo(b.isSeparateWorkFlag());
    };
    
    // ヘッダーで定義されるadInterface通知受け取り
    private Consumer<Object> notice;

    /**
     * コンストラクタ
     */
    public AgendaModel() {
        SceneContiner sc = SceneContiner.getInstance();
        this.isUpdate = false;

        sc.getStage().setOnCloseRequest((WindowEvent we) -> {
            this.refreshTimerCancel();
            this.systemTimerCancel();
        });
    }

    /**
     * インスタンスを取得する。
     *
     * @return
     */
    public static AgendaModel getInstance() {
        if (Objects.isNull(instance)) {
            instance = new AgendaModel();
        }
        return instance;
    }

    /**
     * モデルを初期化する。
     */
    public void initialize() {
        try {
            String uri = config.getAdFactoryServerURI();

            this.agendaFacade = new AgendaFacade();
            this.workKanbanFacade = new WorkKanbanInfoFacade(uri);
            this.organizationInfoFacade = new OrganizationInfoFacade(uri);
            this.kanbanFacade = new KanbanInfoFacade();
            
            DisplayedStatusInfoFacade displayedStatusFacede = new DisplayedStatusInfoFacade(uri);
            this.displayedStatuses = displayedStatusFacede.findAll();

            CacheUtils.createCacheBreakTime(true);

            List<BreakTimeInfoEntity> breakTimes = cache.getItemList(BreakTimeInfoEntity.class, new ArrayList());
            for (BreakTimeInfoEntity breakTime : breakTimes) {
                this.breakTimeMap.put(breakTime.getBreaktimeId(), breakTime);
            }
            
            CacheUtils.createCacheHoliday(true);
            
            this.allHolidays.clear();
            List<HolidayInfoEntity> holidayList = cache.getItemList(HolidayInfoEntity.class, new ArrayList());
            for (HolidayInfoEntity holiday : holidayList) {
                if (!this.allHolidays.contains(holiday.getHolidayDate())) {
                    this.allHolidays.add(holiday.getHolidayDate());
                }
            }
            
            cache.setNewCashList(OrganizationInfoEntity.class);
                        
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * インスタンス破棄
     */
    public void destory() {
        if(Objects.nonNull(this.melodyPlayer)) {
            this.melodyPlayer.stop();
        }
    }

    /**
     * コントローラーを設定する。
     *
     * @param controller
     */
    public void setController(AgendaCompoInterface controller) {
        this.controller = controller;
        // 呼出し処理の更新
        this.noticeCall();
    }

    public boolean isUpdate() {
        return this.isUpdate;
    }

    public void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    /**
     * カンバン毎の休憩時間マップをクリアする。
     */
    public void clearKanbanBreakTimes() {
        this.kanbanBreakTimeMap.clear();
    }

    /**
     * システムタイマーを取得する。
     * (現在のシステムタイマーを破棄して、新規作成したシステムタイマーを返す)
     *
     * @return システムタイマー
     */
    public Timer getSystemTimer() {
        this.systemTimerCancel();
        this.systemTimer = new Timer();
        return this.systemTimer;
    }

    /**
     * システムタイマーをキャンセル(破棄)する。
     */
    private void systemTimerCancel() {
        if (Objects.nonNull(this.systemTimer)) {
            this.systemTimer.cancel();
            this.systemTimer.purge();
            this.systemTimer = null;
        }
    }

    /**
     * 休憩時間を取得する。
     *
     * @return
     */
    public Map<Long, BreakTimeInfoEntity> getBreakTimes() {
        return this.breakTimeMap;
    }

    /**
     * 休憩時間を取得する。
     *
     * @param id
     * @return
     */
    public BreakTimeInfoEntity getBreakTime(long id) {
        BreakTimeInfoEntity breakTime = null;
        if (this.breakTimeMap.containsKey(id)) {
            breakTime = this.breakTimeMap.get(id);
        }
        return breakTime;
    }
    
    /**
     * 休日を取得する。
     *
     * @return
     */
    public List<Date> getHolidays() {
        return this.holidays;
    }

    /**
     * 表示ステータス設定情報を取得する。
     *
     * @return
     */
    public List<DisplayedStatusInfoEntity> getDisplayedStatuses() {
        return this.displayedStatuses;
    }

    /**
     * HeaderCompoで定義されるadInterfaceからの通知受け取り時処理
     * 
     * @param notice 
     */
    public void setNotice(Consumer<Object> notice) {
        this.notice = notice;
    }

    /**
     * 計画実績を再取得する。
     */
    synchronized private void updateData() {
        logger.info("updateData start.");
        this.hideAlert();

        Map<Long, Agenda> agendas = new LinkedHashMap<>();
        List<KanbanTopicInfoEntity> kanbanTopics = null;

        if (config.getMonitorType() == AndonMonitorTypeEnum.LITE_MONITOR) {
            // Liteモニターの場合、当日に固定
            this.currentData.setDate(LocalDateTime.now());
        }

        calcBeforeDays();
        // 自動スクロールが有効で日付が変わった場合はカレンダーを含め更新する
        if (config.isAutoScroll()) {
            Date from = currentData.getFromDate();
            Date current = Date.from(LocalDateTime.now().toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            
            if(!Objects.equals(from, current)) {
                this.notice.accept(new ResetCommand(AgendaSettings.getMonitorId()));

                // noticeからここupdateDataが呼ばれるため今回はここまで
                return;
            }
        }

        // 休日を勘案して検索対象日を設定
        this.setToDateExceptHoliday(this.currentData.getFromDate(), this.allHolidays);
    
        switch (this.currentData.getDisplayMode()) {
            case WORKER:
                if (config.getMonitorType() != AndonMonitorTypeEnum.LITE_MONITOR) {
                    kanbanTopics = this.updtateWorkerData(agendas);
                } else {
                    this.updtateLiteWorkerData(agendas);
                }
                break;
            case LINE:
                if (config.getMonitorType() != AndonMonitorTypeEnum.LITE_MONITOR) {
                    kanbanTopics = this.updateLineData(agendas);
                } else {
                    this.updateLiteLineData(agendas);
                }
                break;
            case PRODUCT_PROGRESS:
                // 製品進捗
                agendas = this.updateProductProgressData();
                break;
            case KANBAN:
            default:
                if (config.getMonitorType() != AndonMonitorTypeEnum.LITE_MONITOR) {
                    kanbanTopics = this.updateKanbanData(agendas);
                } else {
                    this.updateLiteKanbanData(agendas);
                }
                break;
        }

        this.currentData.setAgendas(agendas);

        if (KanbanStatusConfig.getEnableKanbanStatusCsv()) {
            // 進捗情報CSVファイルを出力する。
            this.outputStatusInfoCsv(kanbanTopics, agendas);
        }
        
        logger.info("updateData end.");
    }
    
    /**
     * 作業者設定時のデータ収集
     * @param agendas
     */
    List<KanbanTopicInfoEntity> updtateWorkerData(Map<Long, Agenda> agendas) {
        List<KanbanTopicInfoEntity> kanbanTopics = null;
        Map<Long, List<KanbanTopicInfoEntity>> topicMap = new HashMap<>();
        List<WorkRecordInfoEntity> workRecords = new ArrayList<>();

        KanbanTopicSearchCondition condition;

        if (this.currentData.getOrganizations().isEmpty()) {
            return kanbanTopics;
        }

        List<Long> organizationIds = new ArrayList<>();
        for (OrganizationInfoEntity organization : this.currentData.getOrganizations()) {
            organizationIds.add(organization.getOrganizationId());
        }

        int fetchSize = Integer.parseInt(AdProperty.getProperties().getProperty(Constants.FETCH_SIZE, Constants.FETCH_SIZE_DEFAULT));

        Date fromDate, toDate;
        if (!config.isVerticalAxis() && config.isAutoScroll()) {
            fromDate = DateUtils.addDays(this.currentData.getFromDate(), -1);
            toDate = DateUtils.addDays(this.currentData.getToDate(), 1);
        } else {
            fromDate = this.currentData.getFromDate();
            toDate = this.currentData.getToDate();
        }

        for (int from = 0; from < organizationIds.size();) {
            int to = from + fetchSize;
            to = to < organizationIds.size() ? to : organizationIds.size();

            condition = new KanbanTopicSearchCondition(KanbanTopicSearchCondition.ContentType.DAYS_ORGANIZATION);
            condition.setPrimaryKeys(organizationIds.subList(from, to));
            condition.setFromDate(fromDate);
            condition.setToDate(toDate);
            condition.setWithParent(true);

            // 計画を取得
            kanbanTopics = this.agendaFacade.findTopic(condition);
            for (KanbanTopicInfoEntity topic : kanbanTopics) {
                List<KanbanTopicInfoEntity> list = null;
                if (topicMap.containsKey(topic.getOrganizationId())) {
                    list = topicMap.get(topic.getOrganizationId());
                } else {
                    list = new LinkedList<>();
                    topicMap.put(topic.getOrganizationId(), list);
                }
                list.add(topic);
            }

            from = to;
        }

        Date now = new Date();

        for (int from = 0; from < organizationIds.size();) {
            int to = from + fetchSize;
            to = to < organizationIds.size() ? to : organizationIds.size();

            workRecords.addAll(this.agendaFacade.getHistory(AgendaFacade.Type.ORGANIZATION, organizationIds.subList(from, to), fromDate, toDate));

            from = to;
        }

        if (!config.isDisplaySupportResults()) {
            // 応援者の実績を除外する。
            workRecords = workRecords.stream()
                    .filter(p -> !p.isAssist())
                    .collect(Collectors.toList());
        }

        for (OrganizationInfoEntity organization : this.currentData.getOrganizations()) {
            List<KanbanTopicInfoEntity> topics;
            if (topicMap.containsKey(organization.getOrganizationId())) {
                topics = topicMap.get(organization.getOrganizationId());
            } else {
                topics = new ArrayList<>();
            }
            OrganizationInfoEntity parent = CacheUtils.getCacheOrganization(organization.getParentId());
            List<WorkRecordInfoEntity> list = workRecords.stream().filter(o -> Objects.equals(o.getOrganizationId(), organization.getOrganizationId())).collect(Collectors.toList());
            Agenda agenda = this.normalizeOrganization(new Agenda(organization.getOrganizationId(), Objects.nonNull(parent) ? parent.getOrganizationName() : "", organization.getOrganizationName(), null, null), topics, list, null, now);
            agendas.put(organization.getOrganizationId(), agenda);
        }

        return kanbanTopics;
    }
    
    /**
     * Liteモニター 作業者設定時のデータ収集
     * @param agendas
     */
    void updtateLiteWorkerData(Map<Long, Agenda> agendas) {
        if (this.currentData.getOrganizations().isEmpty()) {
            return;
        }

        List<Long> organizationIds = new ArrayList<>();
        for (OrganizationInfoEntity organization : this.currentData.getOrganizations()) {
            organizationIds.add(organization.getOrganizationId());
        }
        if (!callingPool.containsOrganizationCall(organizationIds)) {
            // 組織の呼び出しが無い場合、メロディを停止
            if (this.melodyPlayer.isPlaying()) {
                this.melodyPlayer.stop();
            }
        }

        ActualSearchCondition conditionActual;
        OperationSerachCondition conditionOperation;
        final Date now = new Date();
        for (OrganizationInfoEntity organization : this.currentData.getOrganizations()) {
            // 前回の作業実績(間接作業)を取得
            conditionOperation = new OperationSerachCondition();
            conditionOperation.setOrganizationId(organization.getOrganizationId());
            conditionOperation.setOperateApp(OperateAppEnum.ADPRODUCTLITE);
            conditionOperation.setOperationType(OperationTypeEnum.INDIRECT_WORK);
            OperationEntity operation = this.agendaFacade.getLastOperation(conditionOperation);

            // 前回の作業実績(直接作業)を取得
            conditionActual = new ActualSearchCondition();
            conditionActual.setOrganizationCollection(Arrays.asList(organization.getOrganizationId()));
            conditionActual.setResultDailyEnum(ActualResultDailyEnum.ALL);
            ActualResultEntity actual = this.agendaFacade.getLastActualResult(conditionActual);
            
            if (Objects.isNull(operation) && Objects.isNull(actual)) {
                OrganizationInfoEntity parent = CacheUtils.getCacheOrganization(organization.getParentId());
                Agenda agenda = this.normalizeOrganization(new Agenda(organization.getOrganizationId(), Objects.nonNull(parent) ? parent.getOrganizationName() : "", organization.getOrganizationName(), null, null), new ArrayList<>(), new ArrayList<>(), null, now);
                agendas.put(organization.getOrganizationId(), agenda);
                continue;
            }
            
            // 間接作業 or 直接作業のどちらを表示するか
            boolean dispIndirectWork;
            if (Objects.isNull(operation)) {
                dispIndirectWork = false;
            } else {
                if (Objects.isNull(actual)) {
                    dispIndirectWork = true;
                } else {
                    dispIndirectWork = operation.getOperateDatetime().after(actual.getImplementDatetime());
                }
            }

            // 作業者履歴情報を作成
            WorkRecordInfoEntity workRecord = new WorkRecordInfoEntity();
            workRecord.setIsIndirectData(dispIndirectWork);
            workRecord.setOrganizationId(organization.getOrganizationId());
            workRecord.setOrganizationName(organization.getOrganizationName());

            if (dispIndirectWork) {
                // 間接作業を表示する
                if (Objects.isNull(operation)) {
                    continue;
                }
                
                OperationAddInfoEntity addInfo = operation.getAddInfo();
                if (Objects.isNull(addInfo)) {
                    continue;
                }

                IndirectWorkOperationEntity indirectOperation = addInfo.getIndirectWorkOperationEntity();
                if (Objects.isNull(indirectOperation)) {
                    continue;
                }

                IndirectWorkInfoEntity indirectWork = CacheUtils.getCacheIndirectWork(indirectOperation.getIndirectWorkId());
                if (Objects.isNull(indirectWork)) {
                    continue;
                }

                Long id = operation.getOperationId();
                workRecord.setActualId(id);
                workRecord.setEquipmentId(operation.getEquipmentId());

                KanbanStatusEnum status;
                if (indirectOperation.getDoIndirect()) {
                    // 作業中
                    workRecord.setActualStartTime(operation.getOperateDatetime());
                    workRecord.setActualEndTime(null);
                    status = KanbanStatusEnum.WORKING;
                } else {
                    // 完了
                    workRecord.setActualStartTime(null);
                    workRecord.setActualEndTime(operation.getOperateDatetime());
                    status = KanbanStatusEnum.COMPLETION;
                }
                workRecord.setActualStatus(status);
                
                workRecord.setKanbanId(id);
                workRecord.setKanbanStatus(status);

                workRecord.setWorkKanbanId(id);
                workRecord.setWorkKanbanStatus(status);
                workRecord.setWorkKanbanOrder(0);
                workRecord.setTaktTime(0);
                workRecord.setSumTimes(0L);

                workRecord.setWorkId(id);
                workRecord.setWorkName(indirectWork.getWorkName());

                workRecord.setReason(indirectOperation.getReason());
            } else {
                // 直接作業を表示する
                if (Objects.isNull(actual)) {
                    continue;
                }

                KanbanInfoEntity kanban = this.agendaFacade.getKanban(actual.getFkKanbanId());
                WorkKanbanInfoEntity workKanban = this.agendaFacade.getWorkKanban(actual.getFkWorkKanbanId());
                if (Objects.isNull(kanban) || Objects.isNull(workKanban)) {
                    continue;
                }

                workRecord.setActualId(actual.getActualId());
                workRecord.setEquipmentId(actual.getFkEquipmentId());
                workRecord.setActualStatus(actual.getActualStatus());

                workRecord.setKanbanId(kanban.getKanbanId());
                workRecord.setKanbanStatus(kanban.getKanbanStatus());
                workRecord.setKanbanName(kanban.getKanbanName());
                workRecord.setWorkflowName(kanban.getWorkflowName());
                workRecord.setModelName(kanban.getModelName());

                workRecord.setWorkKanbanId(workKanban.getWorkKanbanId());
                workRecord.setWorkKanbanStatus(workKanban.getWorkStatus());
                workRecord.setWorkKanbanOrder(workKanban.getWorkKanbanOrder());
                workRecord.setTaktTime(workKanban.getTaktTime());
                workRecord.setSumTimes(workKanban.getSumTimes());
                workRecord.setActualStartTime(workKanban.getActualStartTime());
                workRecord.setActualEndTime(workKanban.getActualCompTime());

                workRecord.setWorkId(workKanban.getFkWorkId());
                workRecord.setWorkName(workKanban.getWorkName());

                if (KanbanStatusEnum.SUSPEND.equals(actual.getActualStatus())) {
                    workRecord.setReason(actual.getInterruptReason());
                }
            }

            OrganizationInfoEntity parent = CacheUtils.getCacheOrganization(organization.getParentId());
            Agenda agenda = this.normalizeLiteOrganization(
                    new Agenda(organization.getOrganizationId(), Objects.nonNull(parent) ? parent.getOrganizationName() : "", organization.getOrganizationName(), null, null), 
                    Arrays.asList(workRecord), organizationIds, now);
            agendas.put(organization.getOrganizationId(), agenda);
        }
    }
    
    /**
     * ライン設定時のデータ収集
     * @param agendas
     */
    List<KanbanTopicInfoEntity> updateLineData(Map<Long, Agenda> agendas) {
        List<KanbanTopicInfoEntity> kanbanTopics = null;
        Map<Long, List<KanbanTopicInfoEntity>> topicMap = new HashMap<>();
        Map<Long, List<KanbanTopicInfoEntity>> actualTopicMap = new HashMap<>();
        List<KanbanTopicInfoEntity> kanbanActualTopics;
        List<WorkRecordInfoEntity> workRecords;

        if (this.currentData.getEquipmentIds().isEmpty()) {
            return kanbanTopics;
        }

        Date fromDate, toDate;
        if (!config.isVerticalAxis() && config.isAutoScroll()) {
            fromDate = DateUtils.addDays(this.currentData.getFromDate(), -1);
            toDate = DateUtils.addDays(this.currentData.getToDate(), 1);
        } else {
            fromDate = this.currentData.getFromDate();
            toDate = this.currentData.getToDate();
        }

        KanbanTopicSearchCondition condition = new KanbanTopicSearchCondition(KanbanTopicSearchCondition.ContentType.DAYS_LINE);
        condition.setPrimaryKeys(this.currentData.getEquipmentIds());
        condition.setFromDate(fromDate);
        condition.setToDate(toDate);
        condition.setModelName(this.config.getModelName());

        kanbanTopics = this.agendaFacade.findTopic(condition);
        for (KanbanTopicInfoEntity topic : kanbanTopics) {
            List<KanbanTopicInfoEntity> list = null;
            if (topicMap.containsKey(topic.getKanbanId())) {
                list = topicMap.get(topic.getKanbanId());
            } else {
                list = new LinkedList<>();
                topicMap.put(topic.getKanbanId(), list);
            }
            list.add(topic);
        }
        // カンバン情報を取得する.
        List<Long> kanbanIdList = kanbanTopics.stream().map(s -> s.getKanbanId()).distinct().collect(Collectors.toList());
        List<KanbanInfoEntity> result = kanbanFacade.find(kanbanIdList);
        currentData.setKanbans(result);

        // 計画時間順に表示する
        Map<Long, List<KanbanTopicInfoEntity>> orderedTopicMap = topicMap.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue, topicListComparator))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        Date now = new Date();

        if (!this.config.isTimeScaleMonth()) {
            // 実績を作業履歴から取得
            workRecords = this.agendaFacade.getHistory(AgendaFacade.Type.KANBAN, new ArrayList<>(topicMap.keySet()), fromDate, toDate);

            if (!config.isDisplaySupportResults()) {
                // 応援者の実績を除外する。
                workRecords = workRecords.stream()
                        .filter(p -> !p.isAssist())
                        .collect(Collectors.toList());
            }

            // 工程順名を表示する
            for (List<KanbanTopicInfoEntity> topics : orderedTopicMap.values()) {
                KanbanTopicInfoEntity topic = topics.get(0);
                //if (!this.kanbanBreakTimeMap.containsKey(topic.getKanbanId())) {
                //  List<WorkKanbanInfoEntity> workKanbans = this.workKanbanFacade.getWorkKanbans(topic.getKanbanId());//////////////////
                //  this.kanbanBreakTimeMap.put(topic.getKanbanId(), this.getBreakTimes(workKanbans));
                //}

                String text;
                if (ContentTypeEnum.WORKFLOW_NAME == this.config.getContentType()) {
                    // 工程順名を表示する
                    text =  Objects.isNull(topic.getWorkflowName()) ? "" : topic.getWorkflowName();
                } else {
                    // モデル名を表示する
                    text =  Objects.isNull(topic.getModelName()) ? "" : topic.getModelName();
                }

                StatusPatternEnum pattern = StatusPatternEnum.getStatusPattern(topic.getKanbanStatus(), topic.getKanbanStatus(), topic.getKanbanPlanStartTime(), topic.getKanbanPlanEndTime(), topic.getKanbanActualStartTime(), topic.getKanbanActualEndTime(), now);

                List<WorkRecordInfoEntity> list = workRecords.stream().filter(o -> Objects.equals(o.getKanbanId(), topic.getKanbanId())).collect(Collectors.toList());
                Agenda agenda = this.normalizeKanban(new Agenda(topic.getKanbanId(), topic.getKanbanName(), text, null, null), topics, list, null, now ,pattern);
                if (!agenda.getPlans().isEmpty() || !agenda.getActuals().isEmpty()) {
                    Optional<KanbanInfoEntity> kanban = currentData.getKanbans().stream().filter(s -> s.getKanbanId().equals(topic.getKanbanId())).findFirst();
                    if (kanban.isPresent()) {
                        agenda.setKanbanAddInfos(kanban.get().getKanbanAddInfo());
                    }
                    agendas.put(topic.getKanbanId(), agenda);
                }
            }
        } else {
            // 実績をカンバン別計画実績から取得
            kanbanActualTopics = this.agendaFacade.findActualTopic(condition);

            for (KanbanTopicInfoEntity topic : kanbanActualTopics) {
                List<KanbanTopicInfoEntity> list = null;
                if (actualTopicMap.containsKey(topic.getKanbanId())) {
                    list = actualTopicMap.get(topic.getKanbanId());
                } else {
                    list = new LinkedList<>();
                    actualTopicMap.put(topic.getKanbanId(), list);
                }
                list.add(topic);
            }

            for (List<KanbanTopicInfoEntity> topics : orderedTopicMap.values()) {
                KanbanTopicInfoEntity topic = topics.get(0);
                //if (!this.kanbanBreakTimeMap.containsKey(topic.getKanbanId())) {
                //  List<WorkKanbanInfoEntity> workKanbans = this.workKanbanFacade.getWorkKanbans(topic.getKanbanId());////////////////
                //  this.kanbanBreakTimeMap.put(topic.getKanbanId(), this.getBreakTimes(workKanbans));
                //}

                String text;
                if (ContentTypeEnum.WORKFLOW_NAME == this.config.getContentType()) {
                    // 工程順名を表示する
                    text =  Objects.isNull(topic.getWorkflowName()) ? "" : topic.getWorkflowName();
                } else {
                    // モデル名を表示する
                    text =  Objects.isNull(topic.getModelName()) ? "" : topic.getModelName();
                }

                StatusPatternEnum pattern = StatusPatternEnum.getStatusPattern(topic.getKanbanStatus(), topic.getKanbanStatus(), topic.getKanbanPlanStartTime(), topic.getKanbanPlanEndTime(), topic.getKanbanActualStartTime(), topic.getKanbanActualEndTime(), now);

                List<KanbanTopicInfoEntity> actualTopics = actualTopicMap.get(topic.getKanbanId());
                Agenda agenda = this.normalizeKanban(new Agenda(topic.getKanbanId(), topic.getKanbanName(), text, null, null), topics, null, actualTopics, now, pattern);
                if (!agenda.getPlans().isEmpty() || !agenda.getActuals().isEmpty()) {
                    Optional<KanbanInfoEntity> kanban = currentData.getKanbans().stream().filter(s -> s.getKanbanId().equals(topic.getKanbanId())).findFirst();
                    if (kanban.isPresent()) {
                        agenda.setKanbanAddInfos(kanban.get().getKanbanAddInfo());
                    }
                    agendas.put(topic.getKanbanId(), agenda);
                }
            }
        }
        
        return kanbanTopics;
    }

    /**
     * 表示順序対応
     * @param displayOrder
     * @return
     */
    Comparator<ActualProductInfoEntity> getComparator(DisplayOrderEnum displayOrder) {
        switch(displayOrder) {
            case DISPLAY_ORDER_BY_NAME:
                return Comparator.comparing(ActualProductInfoEntity::getProductNumber);
            case DISPLAY_ORDER_BY_START_TIME:
                return Comparator.comparing(ActualProductInfoEntity::getStartDatetime);
            case DISPLAY_ORDER_BY_COMP_TIME:
                return Comparator.comparing(ActualProductInfoEntity::getCompDatetime);
            case DISPLAY_ORDER_BY_CREATE:
            default:
                return Comparator.comparing(ActualProductInfoEntity::getKanbanId);
        }
    }

    /**
     * 製品進捗情報を更新する
     * 
     * @return 製品進捗情報
     */
    private Map<Long, Agenda> updateProductProgressData() {
        final List<Long> kanbanHierarchyIds = this.currentData.getKanbanHierarchyIds();
        if (kanbanHierarchyIds.isEmpty()) {
            return new LinkedHashMap<>();
        }

        final Comparator<ActualProductInfoEntity> comparator = getComparator(this.config.getDisplayOrder());
        final Date now1 = new Date();
        final List<ActualProductInfoEntity> actualProductInfoEntities = this.agendaFacade.findActualProductTopic(kanbanHierarchyIds, now1, this.currentData.getDisplayPeriod(), this.currentData.getFromDate(), this.currentData.getToDate());
        Map<Long, Agenda> agendas
                = actualProductInfoEntities
                .stream()
                .sorted(comparator)
                .map(entity -> new Tuple<>(entity.getKanbanId(), this.normalizeProductProgress(entity, now1)))
                .filter(entity -> Objects.nonNull(entity.getRight()))
                .collect(Collectors.toMap(Tuple::getLeft, Tuple::getRight, (a, b) -> a, LinkedHashMap::new));
        return agendas;
    }
    
    /**
     * カンバン設定時のデータ収集
     * @param agendas
     */
    List<KanbanTopicInfoEntity> updateKanbanData(Map<Long, Agenda> agendas) {
        List<KanbanTopicInfoEntity> kanbanTopics = null;
        Map<Long, List<KanbanTopicInfoEntity>> topicMap = new HashMap<>();
        Map<Long, List<KanbanTopicInfoEntity>> actualTopicMap = new HashMap<>();
        List<KanbanTopicInfoEntity> kanbanActualTopics;
        List<WorkRecordInfoEntity> workRecords;

        if (this.currentData.getKanbans().isEmpty()) {
            return kanbanTopics;
        }
       
        List<Long> kanbanIds = this.currentData.getKanbans().stream()
                .map(o -> o.getKanbanId()).collect(Collectors.toList());

        Date fromDate, toDate;
        if (!config.isVerticalAxis() && config.isAutoScroll()) {
            // 横軸にて24H超えても表示する様に対応
            fromDate = DateUtils.addHours(this.currentData.getFromDate(), -1);
            toDate = DateUtils.addHours(this.currentData.getToDate(), 1);
        } else {
            fromDate = this.currentData.getFromDate();
            toDate = this.currentData.getToDate();
        }

        KanbanTopicSearchCondition condition = new KanbanTopicSearchCondition(KanbanTopicSearchCondition.ContentType.DAYS_KANBAN);
        condition.setPrimaryKeys(kanbanIds);
        condition.setFromDate(fromDate);
        condition.setToDate(toDate);

        kanbanTopics = this.agendaFacade.findTopic(condition);
        
        logger.info("Number of KanbanTopics: {}:", kanbanTopics.size());

        for (KanbanTopicInfoEntity topic : kanbanTopics) {
            List<KanbanTopicInfoEntity> list = null;
            if (topicMap.containsKey(topic.getKanbanId())) {
                list = topicMap.get(topic.getKanbanId());
            } else {
                list = new LinkedList<>();
                topicMap.put(topic.getKanbanId(), list);
            }
            list.add(topic);
        }

        final List<Long> orderedKanbanIds = sortBySetting(this.config.getShowOrder(), topicMap, kanbanIds);

        Date now = new Date();

        if (!this.config.isTimeScaleMonth()) {
            // 実績を作業履歴から取得
            workRecords = this.agendaFacade.getHistory(AgendaFacade.Type.KANBAN, kanbanIds, fromDate, toDate);

            if (!config.isDisplaySupportResults()) {
                // 応援者の実績を除外する。
                workRecords = workRecords.stream()
                        .filter(p -> !p.isAssist())
                        .collect(Collectors.toList());
            }

            // idから現在のカンバンを取得
            final Function<Long, Optional<KanbanInfoEntity>> idToKanban = id -> this.currentData.getKanbans().stream()
                    .filter(kanban -> kanban.getKanbanId().equals(id))
                    .findAny();

            final List<KanbanInfoEntity> kanbans = orderedKanbanIds.stream()
                    .map(idToKanban)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            for (KanbanInfoEntity kanban : kanbans) {
                //if (!this.kanbanBreakTimeMap.containsKey(kanban.getKanbanId())) {
                //  List<WorkKanbanInfoEntity> workKanbans = this.workKanbanFacade.getWorkKanbans(kanban.getKanbanId());////////////////
                //  this.kanbanBreakTimeMap.put(kanban.getKanbanId(), this.getBreakTimes(workKanbans));
                //
                //  // カンバン情報に工程順名がセットされていない場合、工程カンバン情報から取得してセットしておく。(工程カンバン情報には必ずセットされている)
                //  if (StringUtils.isEmpty(kanban.getWorkflowName())) {
                //      kanban.setWorkflowName(workKanbans.get(0).getWorkflowName());
                //  }
                //}

                if (StringUtils.isEmpty(kanban.getWorkflowName())) {
                    List<WorkKanbanInfoEntity> workKanbans = this.workKanbanFacade.getWorkKanbans(kanban.getKanbanId());
                    kanban.setWorkflowName(workKanbans.get(0).getWorkflowName());
                }


                List<KanbanTopicInfoEntity> topics;
                if (topicMap.containsKey(kanban.getKanbanId())) {
                    topics = topicMap.get(kanban.getKanbanId());
                } else {
                    topics = new ArrayList<>();
                }

                String text;
                if (ContentTypeEnum.WORKFLOW_NAME == this.config.getContentType()) {
                    // 工程順名を表示する
                    text =  Objects.isNull(kanban.getWorkflowName()) ? "" : kanban.getWorkflowName();
                } else {
                    // モデル名を表示する
                    text =  Objects.isNull(kanban.getModelName()) ? "" : kanban.getModelName();
                }

                StatusPatternEnum pattern = StatusPatternEnum.getStatusPattern(kanban.getKanbanStatus(), kanban.getKanbanStatus(), kanban.getStartDatetime(), kanban.getCompDatetime(), null, null, now);

                List<WorkRecordInfoEntity> list = workRecords.stream().filter(o -> Objects.equals(o.getKanbanId(), kanban.getKanbanId())).collect(Collectors.toList());
                Agenda agenda = this.normalizeKanban(new Agenda(kanban.getKanbanId(), kanban.getKanbanName(), text, null, null), topics, list, null, now, pattern);
                agenda.setKanbanAddInfos(kanban.getKanbanAddInfo());
                agendas.put(kanban.getKanbanId(), agenda);
            }

        } else {
            // 実績をカンバン別計画実績から取得
            kanbanActualTopics = this.agendaFacade.findActualTopic(condition);
            for (KanbanTopicInfoEntity topic : kanbanActualTopics) {
                List<KanbanTopicInfoEntity> list = null;
                if (actualTopicMap.containsKey(topic.getKanbanId())) {
                    list = actualTopicMap.get(topic.getKanbanId());
                } else {
                    list = new LinkedList<>();
                    actualTopicMap.put(topic.getKanbanId(), list);
                }
                list.add(topic);
            }

            // idから現在のカンバンを取得
            final Function<Long, Optional<KanbanInfoEntity>> idToKanban = id -> this.currentData.getKanbans().stream()
                    .filter(kanban -> kanban.getKanbanId().equals(id))
                    .findAny();

            final List<KanbanInfoEntity> kanbans = orderedKanbanIds.stream()
                    .map(idToKanban)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            for (KanbanInfoEntity kanban : kanbans) {
                //if (!this.kanbanBreakTimeMap.containsKey(kanban.getKanbanId())) {
                //  List<WorkKanbanInfoEntity> workKanbans = this.workKanbanFacade.getWorkKanbans(kanban.getKanbanId());///////////////////
                //  this.kanbanBreakTimeMap.put(kanban.getKanbanId(), this.getBreakTimes(workKanbans));

                    // カンバン情報に工程順名がセットされていない場合、工程カンバン情報から取得してセットしておく。(工程カンバン情報には必ずセットされている)
                    if (StringUtils.isEmpty(kanban.getWorkflowName())) {
                        List<WorkKanbanInfoEntity> workKanbans = this.workKanbanFacade.getWorkKanbans(kanban.getKanbanId());
                        kanban.setWorkflowName(workKanbans.get(0).getWorkflowName());
                    }
                //}

                List<KanbanTopicInfoEntity> topics;
                if (topicMap.containsKey(kanban.getKanbanId())) {
                    topics = topicMap.get(kanban.getKanbanId());
                } else {
                    topics = new ArrayList<>();
                }

                List<KanbanTopicInfoEntity> actualTopics;
                if (actualTopicMap.containsKey(kanban.getKanbanId())) {
                    actualTopics = actualTopicMap.get(kanban.getKanbanId());
                } else {
                    actualTopics = new ArrayList<>();
                }

                String text;
                if (ContentTypeEnum.WORKFLOW_NAME == this.config.getContentType()) {
                    // 工程順名を表示する
                    text =  Objects.isNull(kanban.getWorkflowName()) ? "" : kanban.getWorkflowName();
                } else {
                    // モデル名を表示する
                    text =  Objects.isNull(kanban.getModelName()) ? "" : kanban.getModelName();
                }

                StatusPatternEnum pattern = StatusPatternEnum.getStatusPattern(kanban.getKanbanStatus(), kanban.getKanbanStatus(), kanban.getStartDatetime(), kanban.getCompDatetime(), null, null, now);

                Agenda agenda = this.normalizeKanban(new Agenda(kanban.getKanbanId(), kanban.getKanbanName(), text, null, null), topics, null, actualTopics, now, pattern);
                agenda.setKanbanAddInfos(kanban.getKanbanAddInfo());
                agendas.put(kanban.getKanbanId(), agenda);
            }
        }

        return kanbanTopics;
    }
    
    private Pattern folderPattern = Pattern.compile("^.+-CLOSE-\\d{4}/\\d{2}$");
    
    private List<Long> fetchKanbanHierarchy(Long hierarchyId) {
        List<Long> hierarchyIds = new ArrayList<>();
        List<KanbanHierarchyInfoEntity> hierarchies = kanbanHierarchyFacade.getAffilationHierarchyRange(hierarchyId, null, null);
        if (!hierarchies.isEmpty()) {
            for (KanbanHierarchyInfoEntity hierarchy : hierarchies) {
                Matcher match = folderPattern.matcher(hierarchy.getHierarchyName());
                if (!match.find()) {
                    hierarchyIds.add(hierarchy.getKanbanHierarchyId());
                    hierarchyIds.addAll(fetchKanbanHierarchy(hierarchy.getKanbanHierarchyId()));
                }
            }
        }
        return hierarchyIds;
    }
    
    
    /**
     * Liteモニター カンバン設定時のデータ収集
     * @param agendas
     * @param kanbanTopics 
     */
    void updateLiteLineData(Map<Long, Agenda> agendas) {

        agendas.clear();
        if (this.currentData.getEquipmentIds().isEmpty()) {
            return;
        }

        // 作業中のカンバンを取得
        KanbanTopicSearchCondition condition = new KanbanTopicSearchCondition(KanbanTopicSearchCondition.ContentType.DAYS_LINE);
        condition.setPrimaryKeys(this.currentData.getEquipmentIds());
        condition.setFromDate(this.currentData.getFromDate());
        condition.setToDate(this.currentData.getToDate());
        condition.setModelName(this.config.getModelName());

        List<KanbanTopicInfoEntity> kanbanTopics = this.agendaFacade.findTopic(condition);
        List<Long> kanbanIds = kanbanTopics.stream()
            .map(o -> o.getKanbanId()).distinct().collect(Collectors.toList());

        List<KanbanInfoEntity> kanbanList = this.getKanbans(kanbanIds);
      
        String liteTreeName = AdProperty.getProperties().getProperty(Config.LITE_HIERARCHY_TOP_KEY);
        if (!StringUtils.isEmpty(liteTreeName)) {
            KanbanHierarchyInfoEntity liteHierarchy = kanbanHierarchyFacade.findHierarchyName(liteTreeName);
            if (Objects.nonNull(liteHierarchy.getKanbanHierarchyId())) {
                List<Long> hierarchyIds = new ArrayList<>();
                hierarchyIds.add(liteHierarchy.getKanbanHierarchyId());
                hierarchyIds.addAll(fetchKanbanHierarchy(liteHierarchy.getKanbanHierarchyId()));
                for (Long hierarchyId : hierarchyIds) {
                    kanbanList.addAll(kanbanFacade.findByParentId(hierarchyId, null, this.config.getModelName(), null, null, Arrays.asList(KanbanStatusEnum.PLANNED)));
                }
            }
        }
        
        Date now = new Date();
        Date zeroTime = null;
        SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
        try {
            zeroTime = sd.parse("00:00");
        } catch(ParseException e) {
        }
        
        if (this.melodyPlayer.isPlaying()) {
            this.melodyPlayer.stop();
        }
        
        // 工程カンバンをTopicに展開してAgendaに入れる
        List<KanbanInfoEntity> sortedKanban = kanbanList.stream()
            .sorted((a,b) -> a.getKanbanId().compareTo(b.getKanbanId()))
            .collect(Collectors.toList());
        for (KanbanInfoEntity kanban : sortedKanban) {
            if (Objects.isNull(kanban.getWorkKanbanCollection())) {
                kanban.setWorkKanbanCollection(this.workKanbanFacade.getWorkKanbans(kanban.getKanbanId()));
            }
            
            if (Objects.isNull(kanban.getWorkKanbanCollection())) {
                continue;
            }

            StatusPatternEnum pattern = StatusPatternEnum.getStatusPattern(kanban.getKanbanStatus(), kanban.getKanbanStatus(), null, null, null, null, now);
            StatusPatternEnum kanbanPattern = StatusPatternEnum.compareStatus(StatusPatternEnum.COMP_NORMAL, pattern);
            
            int lotNum = 1;
            Matcher m = workNumPattern.matcher(kanban.getKanbanName());
            if (m.find() && m.groupCount() >= 2) {
                // カンバン名を解析して、ロット数を取得する
                final List<String> controlNo = getControlNo(m.group(1));
                lotNum = controlNo.size();
            }
            
            long planWork = 0L;
            long actualWork = 0L;
            List<AgendaTopic> topics = new ArrayList<>();

            for (WorkKanbanInfoEntity workKanban : kanban.getWorkKanbanCollection()) {
                if (workKanban.getSkipFlag()) {
                    continue;
                }
                KanbanTopicInfoEntity topic = new KanbanTopicInfoEntity();
                topic.setKanbanId(workKanban.getFkKanbanId());
                topic.setKanbanName(workKanban.getKanbanName());
                topic.setKanbanStatus(kanban.getKanbanStatus());
                topic.setModelName(kanban.getModelName());
                topic.setWorkId(workKanban.getFkWorkId());
                topic.setWorkKanbanId(workKanban.getWorkKanbanId());
                topic.setWorkKanbanOrder(workKanban.getWorkKanbanOrder());
                topic.setWorkKanbanStatus(workKanban.getWorkStatus());
                topic.setWorkName(workKanban.getWorkName());
                topic.setWorkflowName(workKanban.getWorkflowName());
                topic.setPlanStartTime(zeroTime);
                topic.setPlanEndTime(zeroTime);
                if (workKanban.getOrganizationCollection().size() > 0) {
                    topic.setOrganizationId(workKanban.getOrganizationCollection().get(0));
                } else {
                    topic.setOrganizationId(0L);
                }
 
                long sumTimes = 0l;
                boolean isBlink = false;
                boolean isManagement = workKanban.getTaktTime() > 0;
                
                LongProperty workTime = new SimpleLongProperty(0);
                if (KanbanStatusEnum.WORKING.equals(workKanban.getWorkStatus())) {
                    //ActualSearchCondition searchCondition = new ActualSearchCondition();
                    //searchCondition.setKanbanId(workKanban.getFkKanbanId());
                    //searchCondition.setWorkKanbanCollection(Arrays.asList(workKanban.getWorkKanbanId()));
                    //searchCondition.setKanbanStatusCollection(Arrays.asList(KanbanStatusEnum.WORKING));
                    //searchCondition.setResultDailyEnum(ActualResultDailyEnum.ALL);
                    //List<ActualResultEntity> actualResults = this.actualResultFacade.search(searchCondition);

                    List<WorkRecordInfoEntity> workRecords = this.agendaFacade.getHistory(AgendaFacade.Type.WORKKANBAN, Arrays.asList(workKanban.getWorkKanbanId()), null, null);
                    workRecords.stream()
                            .filter(workRecord -> Objects.isNull(workRecord.getActualEndTime()))
                            .forEach(workRecord -> {
                                // 作業時間
                                OrganizationInfoEntity organization = (OrganizationInfoEntity) cache.getItem(OrganizationInfoEntity.class, workRecord.getOrganizationId());
                                if (Objects.nonNull(organization.getOrganizationId())) {
                                    List<BreakTimeInfoEntity> breakTimes = organization.getBreakTimeInfoCollection().stream()
                                        .map(breakTimeId -> this.breakTimeMap.get(breakTimeId)).collect(Collectors.toList());
                                    workTime.set(workTime.get() + BreaktimeUtil.getDiffTime(breakTimes, workRecord.getActualStartTime(), now));
                                }
                            });
                }

                // 累計作業時間
                sumTimes = workKanban.getSumTimes() + workTime.get();

                StatusPatternEnum workPattern;
                switch (workKanban.getWorkStatus()) {
                    case PLANNING:
                    case PLANNED:
                    default:
                        workPattern = StatusPatternEnum.PLAN_NORMAL;
                        break;
                    case WORKING:
                        workPattern = !isManagement ? StatusPatternEnum.WORK_NORMAL : (workKanban.getTaktTime() * lotNum - sumTimes >= 0) ? StatusPatternEnum.WORK_NORMAL : StatusPatternEnum.WORK_DELAYCOMP;
                        break;
                    case SUSPEND:
                        workPattern = StatusPatternEnum.SUSPEND_NORMAL;
                        break;
                    case INTERRUPT:
                        workPattern = StatusPatternEnum.INTERRUPT_NORMAL;
                        break;
                    case COMPLETION:
                        workPattern = !isManagement ? StatusPatternEnum.COMP_NORMAL : (workKanban.getTaktTime() - sumTimes >= 0) ? StatusPatternEnum.COMP_NORMAL : StatusPatternEnum.COMP_DELAYCOMP;
                        break;
                }

                kanbanPattern = StatusPatternEnum.compareStatus(kanbanPattern, workPattern);

                Optional<DisplayedStatusInfoEntity> opt = this.displayedStatuses.stream()
                        .filter(entity->Objects.equals(entity.getStatusName(), workPattern)).findFirst();

                if (opt.isPresent()) {
                    topic.setFontColor(opt.get().getFontColor());
                    topic.setBackColor(opt.get().getBackColor());
                    if (isManagement && KanbanStatusEnum.WORKING.equals(workKanban.getWorkStatus()) && StatusPatternEnum.WORK_DELAYCOMP.equals(workPattern)) {
                        // 標準作業時間を超過したためアラートを通知
                        isBlink = LightPatternEnum.BLINK.equals(opt.get().getLightPattern());
                        this.playStatusMelody(opt.get());
                    }
                }
                
                planWork += workKanban.getTaktTime();
                actualWork += sumTimes;
                
                String time = StringTime.convertMillisToStringTime(sumTimes);
                AgendaTopic plan = new AgendaTopic(workKanban.getFkKanbanId(), workKanban.getWorkKanbanId(), topic.getOrganizationId(),
                    //workKanban.getWorkName(), StringUtils.isEmpty(topic.getOrganizationName()) ? "" : topic.getOrganizationName(), "", 
                    workKanban.getWorkName(), time, "", 
                    topic.getKanbanStatus(), topic.getWorkKanbanStatus(), topic.getTaktTime(), 
                    topic.getPlanStartTime(), topic.getPlanEndTime(), topic.getActualStartTime(), 
                    topic.getActualEndTime(), topic.getFontColor(), topic.getBackColor(), topic.getSumTimes(), 
                    topic.getWorkKanbanOrder(), false);
                plan.setBlink(isBlink);
                if (isManagement && (KanbanStatusEnum.WORKING.equals(workKanban.getWorkStatus()) || KanbanStatusEnum.SUSPEND.equals(workKanban.getWorkStatus()))) {
                    // 作業進捗
                    double progress = (double)sumTimes / (double)(workKanban.getTaktTime() * lotNum);
                    plan.setProgress(progress < 1.0 ? progress : 1.0);
                }
                topics.add(plan);
            }
            
            String text;
            if (ContentTypeEnum.WORKFLOW_NAME == this.config.getContentType()) {
                // 工程順名を表示する
                text =  Objects.isNull(kanban.getWorkflowName()) ? "" : kanban.getWorkflowName();
            } else {
                // モデル名を表示する
                text =  Objects.isNull(kanban.getModelName()) ? "" : kanban.getModelName();
            }

            Agenda agenda = new Agenda(kanban.getKanbanId(), kanban.getKanbanName(), text, null, null);
            AgendaPlan plan = new AgendaPlan(zeroTime, zeroTime);
            plan.getTopics().put(kanban.getKanbanId(), topics);
            agenda.getPlans().add(plan);
            agenda.setKanbanAddInfos(kanban.getKanbanAddInfo());
            agenda.setPlanWork(planWork);
            agenda.setActualWork(actualWork);
            
            // ヘッダータイトルの文字色、背景色、点灯パターンを設定
            for (DisplayedStatusInfoEntity entity : this.displayedStatuses) {
                if (entity.getStatusName().equals(kanbanPattern)) {
                    agenda.setFontColor(entity.getFontColor());
                    agenda.setBackColor(entity.getBackColor());
                    if (entity.getLightPattern() == LightPatternEnum.BLINK) {
                        agenda.setBlink(true);
                    }
                    //logger.info("kanban:{},{} ({},{})", kanban.getKanbanId(), kanbanPattern, agenda.getFontColor(), agenda.getBackColor());
                    break;
                }
            }
            agendas.put(kanban.getKanbanId(), agenda);
       }
    }

    /**
     * Liteモニター カンバン設定時のデータ収集
     * @param agendas
     * @param kanbanTopics 
     */
    void updateLiteKanbanData(Map<Long, Agenda> agendas) {

        agendas.clear();
        if (this.currentData.getKanbans().isEmpty()) {
            return;
        }

        // 指定IDのカンバンを収集
        List<Long> kanbanIds = new ArrayList<>();
        for (KanbanInfoEntity kanban : this.currentData.getKanbans()) {
            kanbanIds.add(kanban.getKanbanId());
        }
        List<KanbanInfoEntity> kanbanList = this.getKanbans(kanbanIds);

        Date now = new Date();
        Date zeroTime = null;
        SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
        try {
            zeroTime = sd.parse("00:00");
        } catch(ParseException e) {
        }

        if (this.melodyPlayer.isPlaying()) {
            this.melodyPlayer.stop();
        }

        // 工程カンバンをTopicに展開してAgendaに入れる
        List<KanbanInfoEntity> sortedKanban = kanbanList.stream()
            .sorted(Comparator
                .comparing((KanbanInfoEntity item) -> kanbanIds.indexOf(item.getKanbanId()))
                .thenComparing(KanbanInfoEntity::getKanbanId))
            .collect(Collectors.toList());

        for (KanbanInfoEntity kanban : sortedKanban) {
            StatusPatternEnum pattern = StatusPatternEnum.getStatusPattern(kanban.getKanbanStatus(), kanban.getKanbanStatus(), null, null, null, null, now);
            StatusPatternEnum kanbanPattern = StatusPatternEnum.compareStatus(StatusPatternEnum.COMP_NORMAL, pattern);
            List<AgendaTopic> topics = new ArrayList<>();

            int lotNum = 1;
            Matcher m = workNumPattern.matcher(kanban.getKanbanName());
            if (m.find() && m.groupCount() >= 2) {
                // カンバン名を解析して、ロット数を取得する
                final List<String> controlNo = getControlNo(m.group(1));
                lotNum = controlNo.size();
            }

            long planWork = 0L;
            long actualWork = 0L;
            
            for (WorkKanbanInfoEntity workKanban : kanban.getWorkKanbanCollection()) {
                if (workKanban.getSkipFlag()) {
                    continue;
                }
                KanbanTopicInfoEntity topic = new KanbanTopicInfoEntity();
                topic.setKanbanId(workKanban.getFkKanbanId());
                topic.setKanbanName(workKanban.getKanbanName());
                topic.setKanbanStatus(kanban.getKanbanStatus());
                topic.setModelName(kanban.getModelName());
                topic.setWorkId(workKanban.getFkWorkId());
                topic.setWorkKanbanId(workKanban.getWorkKanbanId());
                topic.setWorkKanbanOrder(workKanban.getWorkKanbanOrder());
                topic.setWorkKanbanStatus(workKanban.getWorkStatus());
                topic.setWorkName(workKanban.getWorkName());
                topic.setWorkflowName(workKanban.getWorkflowName());
                topic.setPlanStartTime(zeroTime);
                topic.setPlanEndTime(zeroTime);
                if (workKanban.getOrganizationCollection().size() > 0) {
                    topic.setOrganizationId(workKanban.getOrganizationCollection().get(0));
                } else {
                    topic.setOrganizationId(0L);
                }

                long sumTimes = 0l;
                boolean isBlink = false;
                boolean isManagement = workKanban.getTaktTime() > 0;
                
                LongProperty workTime = new SimpleLongProperty(0);

                if (KanbanStatusEnum.WORKING.equals(workKanban.getWorkStatus())) {
                    List<WorkRecordInfoEntity> workRecords = this.agendaFacade.getHistory(AgendaFacade.Type.WORKKANBAN, Arrays.asList(workKanban.getWorkKanbanId()), null, null);
                    workRecords.stream()
                            .filter(workRecord -> Objects.isNull(workRecord.getActualEndTime()))
                            .forEach(workRecord -> {
                                // 作業時間
                                OrganizationInfoEntity organization = (OrganizationInfoEntity) cache.getItem(OrganizationInfoEntity.class, workRecord.getOrganizationId());
                                if (Objects.nonNull(organization.getOrganizationId())) {
                                    List<BreakTimeInfoEntity> breakTimes = organization.getBreakTimeInfoCollection().stream()
                                        .map(breakTimeId -> this.breakTimeMap.get(breakTimeId)).collect(Collectors.toList());
                                    workTime.set(workTime.get() + BreaktimeUtil.getDiffTime(breakTimes, workRecord.getActualStartTime(), now));
                                }
                            });
                }

                // 累計作業時間
                sumTimes = workKanban.getSumTimes() + workTime.get();

                StatusPatternEnum workPattern;
                switch (workKanban.getWorkStatus()) {
                    case PLANNING:
                    case PLANNED:
                    default:
                        workPattern = StatusPatternEnum.PLAN_NORMAL;
                        break;
                    case WORKING:
                        workPattern = !isManagement ? StatusPatternEnum.WORK_NORMAL : (workKanban.getTaktTime() * lotNum - sumTimes >= 0) ? StatusPatternEnum.WORK_NORMAL : StatusPatternEnum.WORK_DELAYCOMP;
                        break;
                    case SUSPEND:
                        workPattern = StatusPatternEnum.SUSPEND_NORMAL;
                        break;
                    case INTERRUPT:
                        workPattern = StatusPatternEnum.INTERRUPT_NORMAL;
                        break;
                    case COMPLETION:
                        workPattern = !isManagement ? StatusPatternEnum.COMP_NORMAL : (workKanban.getTaktTime() * lotNum - sumTimes >= 0) ? StatusPatternEnum.COMP_NORMAL : StatusPatternEnum.COMP_DELAYCOMP;
                        break;
                }

                kanbanPattern = StatusPatternEnum.compareStatus(kanbanPattern, workPattern);

                Optional<DisplayedStatusInfoEntity> opt = this.displayedStatuses.stream()
                        .filter(entity->Objects.equals(entity.getStatusName(), workPattern)).findFirst();

                if (opt.isPresent()) {
                    topic.setFontColor(opt.get().getFontColor());
                    topic.setBackColor(opt.get().getBackColor());
                    if (isManagement && KanbanStatusEnum.WORKING.equals(workKanban.getWorkStatus()) && StatusPatternEnum.WORK_DELAYCOMP.equals(workPattern)) {
                        // 標準作業時間を超過したためアラートを通知
                        isBlink = LightPatternEnum.BLINK.equals(opt.get().getLightPattern());
                        this.playStatusMelody(opt.get());
                    }
                }
                
                planWork += workKanban.getTaktTime();
                actualWork += sumTimes;

                String time = StringTime.convertMillisToStringTime(sumTimes);
                AgendaTopic plan = new AgendaTopic(workKanban.getFkKanbanId(), workKanban.getWorkKanbanId(), topic.getOrganizationId(),
                    //workKanban.getWorkName(), StringUtils.isEmpty(topic.getOrganizationName()) ? "" : topic.getOrganizationName(), "", 
                    workKanban.getWorkName(), time, "", 
                    topic.getKanbanStatus(), topic.getWorkKanbanStatus(), topic.getTaktTime(), 
                    topic.getPlanStartTime(), topic.getPlanEndTime(), topic.getActualStartTime(), 
                    topic.getActualEndTime(), topic.getFontColor(), topic.getBackColor(), topic.getSumTimes(), 
                    topic.getWorkKanbanOrder(), false);
                plan.setBlink(isBlink);
                if (isManagement && (KanbanStatusEnum.WORKING.equals(workKanban.getWorkStatus()) || KanbanStatusEnum.SUSPEND.equals(workKanban.getWorkStatus()))) {
                    // 作業進捗
                    double progress = (double)sumTimes / (double)(workKanban.getTaktTime() * lotNum);
                    plan.setProgress(progress < 1.0 ? progress : 1.0);
                }
                topics.add(plan);
            }
            
            String text;
            if (ContentTypeEnum.WORKFLOW_NAME == this.config.getContentType()) {
                // 工程順名を表示する
                text =  Objects.isNull(kanban.getWorkflowName()) ? "" : kanban.getWorkflowName();
            } else {
                // モデル名を表示する
                text =  Objects.isNull(kanban.getModelName()) ? "" : kanban.getModelName();
            }

            Agenda agenda = new Agenda(kanban.getKanbanId(), kanban.getKanbanName(), text, null, null);
            AgendaPlan plan = new AgendaPlan(zeroTime, zeroTime);
            plan.getTopics().put(kanban.getKanbanId(), topics);
            agenda.getPlans().add(plan);
            agenda.setKanbanAddInfos(kanban.getKanbanAddInfo());
            agenda.setPlanWork(planWork);
            agenda.setActualWork(actualWork);
            
            // ヘッダータイトルの文字色、背景色、点灯パターンを設定
            for (DisplayedStatusInfoEntity entity : this.displayedStatuses) {
                if (entity.getStatusName().equals(kanbanPattern)) {
                    agenda.setFontColor(entity.getFontColor());
                    agenda.setBackColor(entity.getBackColor());
                    if (entity.getLightPattern() == LightPatternEnum.BLINK) {
                        agenda.setBlink(true);
                    }
                    //logger.info("kanban:{},{} ({},{})", kanban.getKanbanId(), kanbanPattern, agenda.getFontColor(), agenda.getBackColor());
                    break;
                }
            }
            agendas.put(kanban.getKanbanId(), agenda);
        }
    }

    /**
     * カンバンID一覧を指定して、カンバン情報一覧(詳細情報付き)を取得する。
     *
     * @param kanbanIds カンバンID一覧
     * @return カンバン情報一覧
     */
    private List<KanbanInfoEntity> getKanbans(List<Long> kanbanIds) {
        try {
            List<KanbanInfoEntity> kanbans = new LinkedList();

            for (int rangeFrom = 0; rangeFrom < kanbanIds.size(); rangeFrom += KANBAN_RANGE) {
                int rangeTo = rangeFrom + KANBAN_RANGE;
                if (rangeTo > kanbanIds.size()) {
                    rangeTo = kanbanIds.size();
                }

                List<Long> rangeIds = kanbanIds.subList(rangeFrom, rangeTo);
                kanbans.addAll(this.kanbanFacade.find(rangeIds, true));
            }

            return kanbans;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * カンバンの表示順を決定する。
     * <p>
     * 表示順の場合：　アジェンダモニター設定で決めた順番で左から表示する。
     * </p>
     * <p>
     * 計画順の場合：　計画時間の早いカンバンを左から表示し、その後に計画時間の存在しないものを設定順に表示する。
     * </p>
     *
     * @param topicMap
     * @param kanbanIds
     * @return
     */
    private List<Long> sortBySetting(ShowOrder viewOrder, Map<Long, List<KanbanTopicInfoEntity>> topicMap, List<Long> kanbanIds) {
        final List<Long> orderedKanbanIds = Objects.equals(viewOrder, ShowOrder.PlanTimeOrder)
                ? topicMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(topicListComparator))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList())
                : new ArrayList<>();

        // topicMapはkanbanIdから絞り込んだものなので足りないidがある。追加。
        orderedKanbanIds.addAll(kanbanIds.stream()
                .filter(id -> !orderedKanbanIds.contains(id))
                .collect(Collectors.toList()));

        return orderedKanbanIds;
    }

    /**
     * 計画実績画面を更新する。
     */
    public void refresh() {
        logger.info("refresh start. config={}", this.config);

        try {
            this.refreshTimerCancel();

            if (Objects.isNull(this.config.getUpdateInterval()) || this.config.getUpdateInterval() == 0) {
                logger.info("refresh timer has cancelled by updateInterval.");
                return;
            }

            this.refresh(0);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 計画実績画面を更新する。
     * 
     * @param delay 遅延時間
     */
    private void refresh(long delay) {
        synchronized (this.lock) {
            try {
                if (Objects.nonNull(refreshTimer)) {
                    // refreshTimer.cancel();
                    logger.info("In process updateTas.");
                    return;
                }

                TimerTask updateTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            logger.info("updateTask start.");

                            if (isUpdate()) {
                                // データ更新
                                updateData();

                                // 画面表示なしの場合、描画更新は行なわない。
                                if (!KanbanStatusConfig.getEnableView()) {
                                    return;
                                }

                                if (Objects.nonNull(controller)) {
                                    ThreadUtils.joinFXThread(() -> {
                                        controller.updateDisplay();
                                        return null;
                                    });
                                }
                            }
                            
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);

                        } finally {
                            logger.info("updateTask cancel.");
                            refreshTimer.cancel();
                            refreshTimer = null;

                            refresh(config.getUpdateIntervalMillisec());
                            logger.info("updateTask end.");
                        }
                    }

                    @Override
                    public boolean cancel() {
                        logger.info("Schedule has been canceled.");
                        return super.cancel();
                    }
                };

                // タイマー再開
                refreshTimer = new Timer();
                refreshTimer.schedule(updateTask, delay);

            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
    }
    
    /**
     * アラートを表示する。
     */
    public void showAlert() {
        SceneContiner sc = SceneContiner.getInstance();
        Platform.runLater(() -> {
            try {
                logger.info("showAlert start.");

                if (this.alert != null) {
                    this.alert.close();
                }

                this.alert = new Alert(Alert.AlertType.ERROR);
                this.alert.initOwner(sc.getStage());
                this.alert.initStyle(StageStyle.UTILITY);
                this.alert.setTitle(LocaleUtils.getString("key.ConnectionErrorTitle"));
                this.alert.getDialogPane().setHeaderText(LocaleUtils.getString("key.ConnectionErrorTitle"));
                this.alert.getDialogPane().setContentText(LocaleUtils.getString("key.ConnectionErrorContent"));
                this.alert.getDialogPane().getButtonTypes().remove(ButtonType.CANCEL);
                this.alert.show();
            } finally {
                logger.info("showAlert end.");
            }
        });
    }

    /**
     * アラートを消去する。
     */
    public void hideAlert() {
        Platform.runLater(() -> {
            try {
                logger.info("hideAlert start.");
                if (this.alert != null) {
                    this.alert.close();
                }
            } finally {
                logger.info("hideAlert end.");
            }
        });
    }

    /**
     * 予実情報をカンバン表示用に再構築する。
     *
     * @param agenda 予実情報
     * @param kanbanTopics カンバン予実情報(計画)
     * @param workRecords 作業履歴情報
     * @param kanbanActualTopics カンバン予実情報(実績)
     * @param now 現在時刻
     * @param statusPattern カンバンステータス
     * @return 予実情報
     */
    public Agenda normalizeKanban(Agenda agenda, List<KanbanTopicInfoEntity> kanbanTopics, List<WorkRecordInfoEntity> workRecords, List<KanbanTopicInfoEntity> kanbanActualTopics, Date now, StatusPatternEnum statusPattern) {

        Set<AgendaTopic> planTopics = new HashSet<>();
        StatusPatternEnum kanbanPattern = StatusPatternEnum.compareStatus(StatusPatternEnum.COMP_NORMAL, statusPattern);

        int rowCount = 0; // 予定の行数
        int actualRows = 0; // 実績の行数
        
        // 工程の種類(通常工程・追加工程) -> 工程カンバンの計画開始時間でソート
        kanbanTopics.sort(this.kanbanWorkComparator.thenComparing(this.kanbanTimeComparator));

        Date fromDate, toDate;
        if (!config.isVerticalAxis() && config.isAutoScroll()) {
            fromDate = DateUtils.addDays(this.currentData.getFromDate(), -1);
            toDate = DateUtils.addDays(this.currentData.getToDate(), 1);
        } else {
            fromDate = this.currentData.getFromDate();
            toDate = this.currentData.getToDate();
        }

        // 計画
        for (KanbanTopicInfoEntity kanbanTopic : kanbanTopics) {
            if ((kanbanTopic.getPlanStartTime().before(fromDate) && kanbanTopic.getPlanEndTime().before(fromDate))
                || (kanbanTopic.getPlanStartTime().after(toDate) && kanbanTopic.getPlanEndTime().after(toDate))) {
                // 表示期間外
                continue;
            }

            AgendaTopic plan = new AgendaTopic(kanbanTopic.getKanbanId(), kanbanTopic.getWorkKanbanId(), kanbanTopic.getOrganizationId(), kanbanTopic.getWorkName(),
                    StringUtils.isEmpty(kanbanTopic.getOrganizationName()) ? "" : kanbanTopic.getOrganizationName(), "", 
                    kanbanTopic.getKanbanStatus(), kanbanTopic.getWorkKanbanStatus(), kanbanTopic.getTaktTime(), 
                    kanbanTopic.getPlanStartTime(), kanbanTopic.getPlanEndTime(), kanbanTopic.getActualStartTime(), 
                    kanbanTopic.getActualEndTime(), kanbanTopic.getFontColor(), kanbanTopic.getBackColor(), kanbanTopic.getSumTimes(), 
                    kanbanTopic.getWorkKanbanOrder(), false);

            planTopics.add(plan);
           
            rowCount = Math.max(rowCount, plan.getRow());

            // 作業中 又は、中断中の場合、実績完了時間を設定
            if (plan.hasActual() && Objects.isNull(plan.getActualEndTime())) {
                plan.setActualEndTime(now);
            }

            // 文字色、背景色、点灯パターンを設定
            StatusPatternEnum pattern = this.setStyle(plan, now);
            kanbanPattern = StatusPatternEnum.compareStatus(kanbanPattern, pattern);

            // 計画時間が重なっているトピックをグループ化
            boolean found = false;
            for (AgendaPlan group : agenda.getPlans()) {
                if ((group.getStartDate().before(plan.getPlanStartTime()) && group.getEndDate().after(plan.getPlanStartTime()))
                        || (group.getStartDate().before(plan.getPlanEndTime()) && group.getEndDate().after(plan.getPlanEndTime()))
                        || group.getStartDate().equals(plan.getPlanStartTime())
                        || group.getEndDate().equals(plan.getPlanEndTime())) {

                    List<AgendaTopic> topics = null;
                    if (group.getTopics().containsKey(plan.getKanbanId())) {
                        topics = group.getTopics().get(plan.getKanbanId());
                    } else {
                        topics = new ArrayList<>();
                        group.getTopics().put(plan.getKanbanId(), topics);
                    }

                    for (AgendaTopic entity : topics) {
                        if (entity.equals(plan)) {
                            entity.setTitle2(entity.getTitle2() + ", " + plan.getTitle2());
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        topics.add(plan);
                        if (group.getStartDate().after(plan.getPlanStartTime())) {
                            group.setStartDate(plan.getPlanStartTime());
                        }
                        if (group.getEndDate().before(plan.getPlanEndTime())) {
                            group.setEndDate(plan.getPlanEndTime());
                        }
                        found = true;
                    }
                    break;
                }
            }

            if (!found) {
                List<AgendaTopic> topics = new ArrayList<>();
                topics.add(plan);
                AgendaPlan group = new AgendaPlan(plan.getPlanStartTime(), plan.getPlanEndTime());
                group.getTopics().put(plan.getKanbanId(), topics);
                agenda.getPlans().add(group);
            }
        }

        // 実績
        if (config.isTimeScaleMonth()) {
            kanbanActualTopics.sort(this.kanbanWorkComparator.thenComparing(this.kanbanTimeComparator));

            for (KanbanTopicInfoEntity kanbanTopic : kanbanActualTopics) {

                AgendaTopic actual = new AgendaTopic(kanbanTopic.getKanbanId(), kanbanTopic.getWorkKanbanId(), kanbanTopic.getOrganizationId(), kanbanTopic.getWorkName(),
                        kanbanTopic.getWorkerName(), "", kanbanTopic.getKanbanStatus(), kanbanTopic.getWorkKanbanStatus(), 
                        kanbanTopic.getTaktTime(), kanbanTopic.getPlanStartTime(), kanbanTopic.getPlanEndTime(), kanbanTopic.getActualStartTime(),
                        kanbanTopic.getActualEndTime(), kanbanTopic.getFontColor(), kanbanTopic.getBackColor(), kanbanTopic.getSumTimes(), kanbanTopic.getWorkKanbanOrder(), false);

                if (!actual.hasActual()) {
                    continue;
                }

                actualRows = Math.max(actualRows, actual.getRow());

                rowCount = Math.max(rowCount, actual.getRow());

                // 作業中の実績の場合、完了時間に開始時間 + 残り作業時間 + 休憩時間を設定
                if (actual.hasActual() && Objects.isNull(actual.getActualEndTime())) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(actual.getActualStartTime());
                    cal.add(Calendar.SECOND, actual.getTaktTime() / 1000 - Long.valueOf(actual.getSumTimes() / 1000).intValue());

                    // 休憩時間
                    if (cal.getTime().after(now)) {
                        actual.setActualEndTime(cal.getTime());
                    } else {
                        actual.setActualEndTime(now);
                    }
                }

                // 文字色、背景色、点灯パターンを設定
                StatusPatternEnum pattern = this.setStyle(actual, now);
                kanbanPattern = StatusPatternEnum.compareStatus(kanbanPattern, pattern);

                // 実績時間が重なっているトピックをグループ化
                boolean found = false;
                for (AgendaGroup group : agenda.getActuals()) {
                    if ((group.getStartDate().before(actual.getActualStartTime()) && group.getEndDate().after(actual.getActualStartTime()))
                            || (group.getStartDate().before(actual.getActualEndTime()) && group.getEndDate().after(actual.getActualEndTime()))
                            || group.getStartDate().equals(actual.getActualStartTime())
                            || group.getEndDate().equals(actual.getActualEndTime())) {

                        group.getTopics().add(actual);
                        if (group.getStartDate().after(actual.getActualStartTime())) {
                            group.setStartDate(actual.getActualStartTime());
                        }
                        if (group.getEndDate().before(actual.getActualEndTime())) {
                            group.setEndDate(actual.getActualEndTime());
                        }
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    AgendaGroup group = new AgendaGroup(actual.getActualStartTime(), actual.getActualEndTime());
                    group.getTopics().add(actual);
                    agenda.getActuals().add(group);
                }
            }

        } else {
           
            workRecords.sort(this.actualWorkComparator.thenComparing(this.actualTimeComparator));
            
            for (WorkRecordInfoEntity workRecord : workRecords) {
                
                AgendaTopic actual = new AgendaTopic(workRecord.getKanbanId(), workRecord.getWorkKanbanId(), workRecord.getOrganizationId(), workRecord.getWorkName(),
                        workRecord.getOrganizationName(), "", workRecord.getKanbanStatus(), workRecord.getWorkKanbanStatus(), 
                        workRecord.getTaktTime(), workRecord.getPlanStartTime(), workRecord.getPlanEndTime(), workRecord.getActualStartTime(), 
                        workRecord.getActualEndTime(), workRecord.getFontColor(), workRecord.getBackColor(), workRecord.getSumTimes(), 
                        workRecord.getWorkKanbanOrder(), workRecord.getIsIndirectData());

                actualRows = Math.max(actualRows, actual.getRow());

                rowCount = Math.max(rowCount, actual.getRow());

                // 作業中の実績の場合、完了時間に開始時間 + 残り作業時間 + 休憩時間を設定
                if (actual.hasActual() && Objects.isNull(actual.getActualEndTime())) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(actual.getActualStartTime());
                    cal.add(Calendar.SECOND, actual.getTaktTime() / 1000 - Long.valueOf(actual.getSumTimes()).intValue());

                    // 休憩時間
                    if (cal.getTime().after(now)) {
                        actual.setActualEndTime(cal.getTime());
                    } else {
                        actual.setActualEndTime(now);
                    }
                }

                // 文字色、背景色、点灯パターンを設定
                StatusPatternEnum pattern = this.setStyle(actual, now, workRecord);
                kanbanPattern = StatusPatternEnum.compareStatus(kanbanPattern, pattern);

                // 実績時間が重なっているトピックをグループ化
                boolean found = false;
                for (AgendaGroup group : agenda.getActuals()) {
                    if ((group.getStartDate().before(actual.getActualStartTime()) && group.getEndDate().after(actual.getActualStartTime()))
                            || (group.getStartDate().before(actual.getActualEndTime()) && group.getEndDate().after(actual.getActualEndTime()))
                            || group.getStartDate().equals(actual.getActualStartTime())
                            || group.getEndDate().equals(actual.getActualEndTime())) {

                        group.getTopics().add(actual);
                        if (group.getStartDate().after(actual.getActualStartTime())) {
                            group.setStartDate(actual.getActualStartTime());
                        }
                        if (group.getEndDate().before(actual.getActualEndTime())) {
                            group.setEndDate(actual.getActualEndTime());
                        }
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    AgendaGroup group = new AgendaGroup(actual.getActualStartTime(), actual.getActualEndTime());
                    group.getTopics().add(actual);
                    agenda.getActuals().add(group);
                }
            }
        }

        // 行数
        agenda.setRowCount(rowCount + 1);

        // ヘッダータイトルの文字色、背景色、点灯パターンを設定
        for (DisplayedStatusInfoEntity entity : this.displayedStatuses) {
            if (entity.getStatusName().equals(kanbanPattern)) {
                agenda.setFontColor(entity.getFontColor());
                agenda.setBackColor(entity.getBackColor());
                if (entity.getLightPattern() == LightPatternEnum.BLINK) {
                    agenda.setBlink(true);
                }
                break;
            }
        }

        // 進捗時間を設定
        List<AgendaTopic> list = new ArrayList<>(planTopics);
        long delayTime = this.calcDelayTime(list, this.getLastTime(list, now), this.getAdvanceTime(list));
        agenda.setDelayTimeMillisec(delayTime);

        return agenda;
    }

    /**
     * 予実情報を作業者表示用に再構築する。
     *
     * @param agenda 予実情報
     * @param kanbanTopics カンバン予実情報(計画)
     * @param workRecords 作業履歴情報
     * @param kanbanActualTopics カンバン予実情報(実績)
     * @param now 現在時刻
     * @return 予実情報
     */
    public Agenda normalizeOrganization(Agenda agenda, List<KanbanTopicInfoEntity> kanbanTopics, List<WorkRecordInfoEntity> workRecords, List<KanbanTopicInfoEntity> kanbanActualTopics, Date now) {
        Set<AgendaTopic> planTopics = new HashSet<>();
        StatusPatternEnum kanbanPattern = StatusPatternEnum.COMP_NORMAL;

        /**
         * カンバンのインデックス(表示位置)を算出
         */
        Map<Long, Integer> actualMap = new HashMap<>();
        Integer rowIndex = 0; // インデックス
        
        if (this.config.isTimeScaleMonth()
                && !Objects.equals(this.currentData.getDisplayMode(), DisplayModeEnum.WORKER)) {
            // 1週間・1ヵ月で、表示対象が作業者以外の場合

            // カンバンID -> 工程種類でソートする
            kanbanActualTopics.sort(this.kanbanComparator.thenComparing(this.kanbanWorkComparator));

            for (KanbanTopicInfoEntity kanbanTopic : kanbanActualTopics) {
                if (!actualMap.containsKey(kanbanTopic.getKanbanId())) {
                    actualMap.put(kanbanTopic.getKanbanId(), rowIndex);
                    rowIndex = 0;
                }

                int order = kanbanTopic.getWorkKanbanOrder() % 10000;
                rowIndex = Math.max(rowIndex, order);
            }
        } else {
            // 時間・半日・1日の場合

            // カンバンID -> 工程種類でソートする
            workRecords.sort(this.actualKanbanComparator.thenComparing(this.actualWorkComparator));

            for (WorkRecordInfoEntity workRecord : workRecords) {
                if (!actualMap.containsKey(workRecord.getKanbanId())) {
                    actualMap.put(workRecord.getKanbanId(), rowIndex);
                    rowIndex = 0;
                }

                int order = workRecord.getWorkKanbanOrder() % 10000;
                rowIndex = Math.max(rowIndex, order);
            }
        }
        
        /**
         * 計画情報を構築
         */
        rowIndex = 0;

        // カンバンID -> 工程種類でソートする
        kanbanTopics.sort(this.kanbanTimeComparator.thenComparing(this.kanbanComparator).thenComparing(this.kanbanWorkComparator));

        Map<Long, Integer> kanbanMap = new HashMap<>();
        Integer rowCount = 0; // 行数
        
        for (KanbanTopicInfoEntity kanbanTopic : kanbanTopics) {

            if (!kanbanMap.containsKey(kanbanTopic.getKanbanId())) {
                if (actualMap.containsKey(kanbanTopic.getKanbanId())) {
                    // 計画から取得したインデックスと実績から取得したインデックスを比較して、インデックスを決定する
                    rowCount += Math.max(rowIndex, actualMap.get(kanbanTopic.getKanbanId()));
                } else {
                    rowCount += rowIndex;
                }
                
                kanbanMap.put(kanbanTopic.getKanbanId(), rowCount);
                rowIndex = 0;
            }

            Integer row = kanbanMap.get(kanbanTopic.getKanbanId());
            int order = kanbanTopic.getWorkKanbanOrder() % 10000;
            row = row + order - 1;
            rowIndex = Math.max(rowIndex, order);

            AgendaTopic plan = new AgendaTopic(kanbanTopic.getKanbanId(), kanbanTopic.getWorkKanbanId(), kanbanTopic.getOrganizationId(), kanbanTopic.getKanbanName(),
                    (ContentTypeEnum.WORKFLOW_NAME == this.config.getContentType()) ? kanbanTopic.getWorkflowName() : kanbanTopic.getModelName(), 
                    kanbanTopic.getWorkName(), kanbanTopic.getKanbanStatus(), 
                    kanbanTopic.getWorkKanbanStatus(), kanbanTopic.getTaktTime(), kanbanTopic.getPlanStartTime(), 
                    kanbanTopic.getPlanEndTime(), kanbanTopic.getActualStartTime(), kanbanTopic.getActualEndTime(), 
                    kanbanTopic.getFontColor(), kanbanTopic.getBackColor(), kanbanTopic.getSumTimes(), false);

            plan.setRow(row);
            planTopics.add(plan);

            // 作業中 又は、中断中の場合、実績完了時間を設定
            if (plan.hasActual() && Objects.isNull(plan.getActualEndTime())) {
                plan.setActualEndTime(now);
            }

            // 文字色、背景色、点灯パターンを設定
            StatusPatternEnum pattern = this.setStyle(plan, now);
            kanbanPattern = StatusPatternEnum.compareStatus(kanbanPattern, pattern);

            // 計画時間が重なっているトピックをグループ化
            AgendaPlan group = agenda.getPlans()
                    .stream()
                    .filter(g->g.isScheduleConflict(plan))
                    .findFirst().orElseGet(()-> {
                        AgendaPlan tmp = new AgendaPlan();
                        agenda.getPlans().add(tmp);
                        return tmp;
                    });

            group.expansion(plan);
        }

        /**
         * 実績情報を構築
         */
        if (this.config.isTimeScaleMonth()
                && !Objects.equals(this.currentData.getDisplayMode(), DisplayModeEnum.WORKER)) {
            // 1週間・1ヵ月で、表示対象が作業者以外の場合
            for (KanbanTopicInfoEntity kanbanTopic : kanbanActualTopics) {

                if (!kanbanMap.containsKey(kanbanTopic.getKanbanId())) {
                    rowCount += rowIndex;
                    kanbanMap.put(kanbanTopic.getKanbanId(), rowCount);
                    rowIndex = 0;
                }

                Integer row = kanbanMap.get(kanbanTopic.getKanbanId());
                int order = kanbanTopic.getWorkKanbanOrder() % 10000;
                row = row + order - 1;
                rowIndex = Math.max(rowIndex, order);

                AgendaTopic actual = new AgendaTopic(kanbanTopic.getKanbanId(), kanbanTopic.getWorkKanbanId(), kanbanTopic.getOrganizationId(), kanbanTopic.getKanbanName(),
                        (ContentTypeEnum.WORKFLOW_NAME == this.config.getContentType()) ? kanbanTopic.getWorkflowName() : kanbanTopic.getModelName(), 
                        kanbanTopic.getWorkName(), kanbanTopic.getKanbanStatus(), kanbanTopic.getWorkKanbanStatus(), 
                        kanbanTopic.getTaktTime(), kanbanTopic.getPlanStartTime(), kanbanTopic.getPlanEndTime(), kanbanTopic.getActualStartTime(), 
                        kanbanTopic.getActualEndTime(), kanbanTopic.getFontColor(), kanbanTopic.getBackColor(), kanbanTopic.getSumTimes(), false);

                actual.setRow(row);

                // 作業中の実績の場合、完了時間に開始時間 + 残り作業時間 + 休憩時間を設定
                if (actual.hasActual() && Objects.isNull(actual.getActualEndTime())) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(actual.getActualStartTime());
                    cal.add(Calendar.SECOND, actual.getTaktTime() / 1000 - Long.valueOf(actual.getSumTimes() / 1000).intValue());

                    // 休憩時間
                    if (cal.getTime().after(now)) {
                        actual.setActualEndTime(cal.getTime());
                    } else {
                        actual.setActualEndTime(now);
                    }
                }

                // 文字色、背景色、点灯パターンを設定
                StatusPatternEnum pattern = this.setStyle(actual, now);
                kanbanPattern = StatusPatternEnum.compareStatus(kanbanPattern, pattern);

                // 実績時間が重なっているトピックをグループ化
                boolean found = false;
                for (AgendaGroup group : agenda.getActuals()) {
                    if ((group.getStartDate().before(actual.getActualStartTime()) && group.getEndDate().after(actual.getActualStartTime()))
                            || (group.getStartDate().before(actual.getActualEndTime()) && group.getEndDate().after(actual.getActualEndTime()))
                            || group.getStartDate().equals(actual.getActualStartTime())
                            || group.getEndDate().equals(actual.getActualEndTime())) {

                        group.getTopics().add(actual);
                        if (group.getStartDate().after(actual.getActualStartTime())) {
                            group.setStartDate(actual.getActualStartTime());
                        }
                        if (group.getEndDate().before(actual.getActualEndTime())) {
                            group.setEndDate(actual.getActualEndTime());
                        }
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    AgendaGroup group = new AgendaGroup(actual.getActualStartTime(), actual.getActualEndTime());
                    group.getTopics().add(actual);
                    agenda.getActuals().add(group);
                }
            }
        } else {
            // 時間・半日・1日の場合
            for (WorkRecordInfoEntity workRecord : workRecords) {

                if (!kanbanMap.containsKey(workRecord.getKanbanId())) {
                    rowCount += rowIndex;
                    kanbanMap.put(workRecord.getKanbanId(), rowCount);
                    rowIndex = 0;
                }

                Integer row = kanbanMap.get(workRecord.getKanbanId());
                int order = workRecord.getWorkKanbanOrder() % 10000;
                row = row + order - 1;
                rowIndex = Math.max(rowIndex, order);

                AgendaTopic actual = new AgendaTopic(workRecord.getKanbanId(), workRecord.getWorkKanbanId(), workRecord.getOrganizationId(), workRecord.getKanbanName(),
                        (ContentTypeEnum.WORKFLOW_NAME == this.config.getContentType()) ? workRecord.getWorkflowName() : workRecord.getModelName(), 
                        workRecord.getWorkName(), workRecord.getKanbanStatus(), workRecord.getWorkKanbanStatus(), workRecord.getTaktTime(),
                        workRecord.getPlanStartTime(), workRecord.getPlanEndTime(), workRecord.getActualStartTime(), 
                        workRecord.getActualEndTime(), workRecord.getFontColor(), workRecord.getBackColor(), workRecord.getSumTimes(), workRecord.getIsIndirectData());

                actual.setRow(row);
                actual.setActualStatus(Objects.isNull(workRecord.getActualStatus()) ? KanbanStatusEnum.WORKING : workRecord.getActualStatus());

                // 作業中の実績の場合、完了時間に開始時間 + 残り作業時間 + 休憩時間を設定
                if (actual.hasActual() && Objects.isNull(actual.getActualEndTime())) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(actual.getActualStartTime());
                    cal.add(Calendar.SECOND, actual.getTaktTime() / 1000 - Long.valueOf(actual.getSumTimes() / 1000).intValue());

                    // 休憩時間
                    if (cal.getTime().after(now)) {
                        actual.setActualEndTime(cal.getTime());
                    } else {
                        actual.setActualEndTime(now);
                    }
                }
                
                // 文字色、背景色、点灯パターンを設定
                StatusPatternEnum pattern = this.setStyle(actual, now, workRecord);
                kanbanPattern = StatusPatternEnum.compareStatus(kanbanPattern, pattern);

                // 実績時間が重なっているトピックをグループ化
                boolean found = false;
                for (AgendaGroup group : agenda.getActuals()) {
                    if ((group.getStartDate().before(actual.getActualStartTime()) && group.getEndDate().after(actual.getActualStartTime()))
                            || (group.getStartDate().before(actual.getActualEndTime()) && group.getEndDate().after(actual.getActualEndTime()))
                            || group.getStartDate().equals(actual.getActualStartTime())
                            || group.getEndDate().equals(actual.getActualEndTime())) {

                        group.getTopics().add(actual);
                        if (group.getStartDate().after(actual.getActualStartTime())) {
                            group.setStartDate(actual.getActualStartTime());
                        }
                        if (group.getEndDate().before(actual.getActualEndTime())) {
                            group.setEndDate(actual.getActualEndTime());
                        }
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    AgendaGroup group = new AgendaGroup(actual.getActualStartTime(), actual.getActualEndTime());
                    group.getTopics().add(actual);
                    agenda.getActuals().add(group);
                }
            }
        }

        rowCount = rowCount + rowIndex;
        agenda.setRowCount(rowCount);
                        
        // ヘッダータイトルの文字色、背景色、点灯パターンを設定
        for (DisplayedStatusInfoEntity entity : this.displayedStatuses) {
            if (entity.getStatusName() == kanbanPattern) {
                agenda.setFontColor(entity.getFontColor());
                agenda.setBackColor(entity.getBackColor());
                if (entity.getLightPattern() == LightPatternEnum.BLINK) {
                    agenda.setBlink(true);
                }
                break;
            }
        }

        // 進捗時間を設定
        List<AgendaTopic> list = new ArrayList<>(planTopics);
        long delayTime = this.calcDelayTime(list, this.getLastTime(list, now), this.getAdvanceTime(list));
        agenda.setDelayTimeMillisec(delayTime);

        return agenda;
    }

    /**
     * 製造番号を取得する。
     * 
     * @param src
     * @return 
     */
    static List<String> getControlNo(String src) {
        List<String> controlNos = new ArrayList<>();
        if (StringUtils.isEmpty(src)) {
            return controlNos;
        }

        List<List<Integer>> progList
                = Stream.of(src.replace(" ", "").split(","))
                .filter(str -> !StringUtils.isEmpty(str))
                .map(str -> str.split("-"))
                .map(strList -> Stream.of(strList).map(Integer::parseInt).collect(toList()))
                .map(strList -> strList.size() >= 2
                        ? Arrays.asList(Math.min(strList.get(0), strList.get(1)), Math.max(strList.get(0), strList.get(1)))
                        : Arrays.asList(strList.get(0), strList.get(0)))
                .sorted(Comparator.comparing(item -> item.get(0)))
                .collect(toList());

        List<List<Integer>> range = new ArrayList<>();
        range.add(progList.get(0));
        progList.forEach(item -> {
            List<Integer> last = range.get(range.size() - 1);
            if (last.get(1) + 1 >= item.get(0)) {
                last.set(1, Math.max(item.get(1), last.get(1)));
            } else {
                range.add(item);
            }
        });

        List<String> controlNoGroups = range.stream()
                .map(item -> Objects.equals(item.get(0), item.get(1))
                        ? String.valueOf(item.get(0))
                        : (String.valueOf(item.get(0)) + '-' + String.valueOf(item.get(1))))
                .collect(Collectors.toList());

        for (String controlNoGroup : controlNoGroups) {
            // 先頭と末尾の製番を取得
            String rangeStart;
            String rangeEnd;
            String[] controlNoArray = controlNoGroup.split("-");
            switch (controlNoArray.length) {
                case 1:
                    rangeStart = controlNoArray[0];
                    rangeEnd = rangeStart;
                    break;
                case 2:
                    rangeStart = controlNoArray[0];
                    rangeEnd = controlNoArray[1];
                    break;
                default:
                    continue;
            }

            // 製番リストを生成
            int startNo = Integer.valueOf(rangeStart);
            int endNo = Integer.valueOf(rangeEnd);
            for (int controlNo = startNo; controlNo <= endNo; controlNo++) {
                controlNos.add(String.valueOf(controlNo));
            }
        }

        return controlNos;
    }
    
    /**
     * 予実情報をLiteモニターの作業者表示用に再構築する。
     *
     * @param agenda 予実情報
     * @param workRecords 作業履歴情報
     * @param organizationIds 組織IDリスト
     * @param now 現在時刻
     * @return 予実情報
     */
    public Agenda normalizeLiteOrganization(Agenda agenda, List<WorkRecordInfoEntity> workRecords, List<Long> organizationIds, Date now) {
        StatusPatternEnum kanbanPattern = StatusPatternEnum.COMP_NORMAL;

        /**
         * カンバンのインデックス(表示位置)を算出
         */
        Map<Long, Integer> actualMap = new HashMap<>();
        Integer rowIndex = 0; // インデックス
        
        // カンバンID -> 工程種類でソートする
        workRecords.sort(this.actualKanbanComparator.thenComparing(this.actualWorkComparator));

        for (WorkRecordInfoEntity workRecord : workRecords) {
            if (!actualMap.containsKey(workRecord.getKanbanId())) {
                actualMap.put(workRecord.getKanbanId(), rowIndex);
                rowIndex = 0;
            }

            int order = workRecord.getWorkKanbanOrder() % 10000;
            rowIndex = Math.max(rowIndex, order);
        }
        
        rowIndex = 0;
        Map<Long, Integer> kanbanMap = new HashMap<>();
        Integer rowCount = 0; // 行数

        /**
         * 実績情報を構築
         */
        for (WorkRecordInfoEntity workRecord : workRecords) {

            if (!kanbanMap.containsKey(workRecord.getKanbanId())) {
                rowCount += rowIndex;
                kanbanMap.put(workRecord.getKanbanId(), rowCount);
                rowIndex = 0;
            }

            Integer row = kanbanMap.get(workRecord.getKanbanId());
            int order = workRecord.getWorkKanbanOrder() % 10000;
            row = row + order - 1;
            rowIndex = Math.max(rowIndex, order);
            
            int lotNum = 1;
            if (!StringUtils.isEmpty(workRecord.getKanbanName())) {
                Matcher m = workNumPattern.matcher(workRecord.getKanbanName());
                if (m.find() && m.groupCount() >= 2) {
                    // カンバン名を解析して、ロット数を取得する
                    final List<String> controlNo = getControlNo(m.group(1));
                    lotNum = controlNo.size();
                }
            }

            AgendaTopic actual = new AgendaTopic(workRecord.getKanbanId(), workRecord.getWorkKanbanId(), workRecord.getOrganizationId(), workRecord.getKanbanName(),
                    (ContentTypeEnum.WORKFLOW_NAME == this.config.getContentType()) ? workRecord.getWorkflowName() : workRecord.getModelName(), 
                    workRecord.getWorkName(), workRecord.getKanbanStatus(), workRecord.getWorkKanbanStatus(), workRecord.getTaktTime(),
                    workRecord.getPlanStartTime(), workRecord.getPlanEndTime(), workRecord.getActualStartTime(), 
                    workRecord.getActualEndTime(), workRecord.getFontColor(), workRecord.getBackColor(), workRecord.getSumTimes(), workRecord.getIsIndirectData());

            actual.setRow(row);
            actual.setActualStatus(Objects.isNull(workRecord.getActualStatus()) ? KanbanStatusEnum.WORKING : workRecord.getActualStatus());
            actual.setReason(workRecord.getReason());

            // 作業中の実績の場合、完了時間に開始時間 + 残り作業時間 + 休憩時間を設定
            if (actual.hasActual() && Objects.isNull(actual.getActualEndTime())) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(actual.getActualStartTime());
                cal.add(Calendar.SECOND, actual.getTaktTime() / 1000 - Long.valueOf(actual.getSumTimes() / 1000).intValue());

                // 休憩時間
                if (cal.getTime().after(now)) {
                    actual.setActualEndTime(cal.getTime());
                } else {
                    actual.setActualEndTime(now);
                }
            }
            
            boolean isManagement = workRecord.getTaktTime() > 0;
            LongProperty workTime = new SimpleLongProperty(0);

            if (KanbanStatusEnum.WORKING.equals(workRecord.getWorkKanbanStatus())) {
                List<WorkRecordInfoEntity> records = this.agendaFacade.getHistory(AgendaFacade.Type.WORKKANBAN, Arrays.asList(workRecord.getWorkKanbanId()), null, null);
                records.stream()
                        .filter(record -> Objects.isNull(record.getActualEndTime()))
                        .forEach(record -> {
                            // 作業時間
                            OrganizationInfoEntity organization = (OrganizationInfoEntity) cache.getItem(OrganizationInfoEntity.class, record.getOrganizationId());
                            if (Objects.nonNull(organization.getOrganizationId())) {
                                List<BreakTimeInfoEntity> breakTimes = organization.getBreakTimeInfoCollection().stream()
                                        .map(breakTimeId -> this.breakTimeMap.get(breakTimeId)).collect(Collectors.toList());
                                workTime.set(workTime.get() + BreaktimeUtil.getDiffTime(breakTimes, record.getActualStartTime(), now));
                            }
                        });
            }

            // 累計作業時間
            long sumTimes = workRecord.getSumTimes() + workTime.get();
            actual.setSumTimes(sumTimes);

            StatusPatternEnum workPattern;
            switch (workRecord.getWorkKanbanStatus()) {
                case PLANNING:
                case PLANNED:
                default:
                    workPattern = StatusPatternEnum.PLAN_NORMAL;
                    break;
                case WORKING:
                    if (Objects.equals(workRecord.getActualStatus(), KanbanStatusEnum.SUSPEND)) {
                        // 実績ステータスが「一時中断」の場合
                        workPattern = StatusPatternEnum.SUSPEND_NORMAL;
                    } else if (Objects.equals(workRecord.getActualStatus(), KanbanStatusEnum.COMPLETION)) {
                        // 実績ステータスが「作業完了」の場合
                        workPattern = !isManagement ? StatusPatternEnum.COMP_NORMAL : (workRecord.getTaktTime() * lotNum - sumTimes >= 0) ? StatusPatternEnum.COMP_NORMAL : StatusPatternEnum.COMP_DELAYCOMP;
                    } else {
                        workPattern = !isManagement ? StatusPatternEnum.WORK_NORMAL : (workRecord.getTaktTime() * lotNum - sumTimes >= 0) ? StatusPatternEnum.WORK_NORMAL : StatusPatternEnum.WORK_DELAYCOMP;
                    }
                    break;
                case SUSPEND:
                    workPattern = StatusPatternEnum.SUSPEND_NORMAL;
                    break;
                case INTERRUPT:
                    workPattern = StatusPatternEnum.INTERRUPT_NORMAL;
                    break;
                case COMPLETION:
                    workPattern = !isManagement ? StatusPatternEnum.COMP_NORMAL : (workRecord.getTaktTime() * lotNum - sumTimes >= 0) ? StatusPatternEnum.COMP_NORMAL : StatusPatternEnum.COMP_DELAYCOMP;
                    break;                
            }
            kanbanPattern = StatusPatternEnum.compareStatus(kanbanPattern, workPattern);

            Optional<DisplayedStatusInfoEntity> opt = this.displayedStatuses.stream()
                        .filter(entity -> Objects.equals(entity.getStatusName(), workPattern)).findFirst();

            if (opt.isPresent()) {
                actual.setFontColor(opt.get().getFontColor());
                actual.setBackColor(opt.get().getBackColor());
                if (isManagement && KanbanStatusEnum.WORKING.equals(workRecord.getWorkKanbanStatus()) && StatusPatternEnum.WORK_DELAYCOMP.equals(workPattern)) {
                    // 標準作業時間を超過したためアラートを通知
                    boolean isBlink = LightPatternEnum.BLINK.equals(opt.get().getLightPattern());
                    actual.setBlink(isBlink);
                    if (!callingPool.containsOrganizationCall(organizationIds)) {
                        // 組織の呼び出しが無い場合、メロディを再生する
                        this.playStatusMelody(opt.get());
                    }
                }
            }
            if (isManagement && (KanbanStatusEnum.WORKING.equals(workRecord.getWorkKanbanStatus()) || KanbanStatusEnum.SUSPEND.equals(workRecord.getWorkKanbanStatus()))) {
                // 作業進捗を設定
                double progress = (double)sumTimes / (double)(workRecord.getTaktTime() * lotNum);
                actual.setProgress(progress < 1.0 ? progress : 1.0);
            }

            // 実績時間が重なっているトピックをグループ化
            boolean found = false;
            for (AgendaGroup group : agenda.getActuals()) {
                if ((group.getStartDate().before(actual.getActualStartTime()) && group.getEndDate().after(actual.getActualStartTime()))
                        || (group.getStartDate().before(actual.getActualEndTime()) && group.getEndDate().after(actual.getActualEndTime()))
                        || group.getStartDate().equals(actual.getActualStartTime())
                        || group.getEndDate().equals(actual.getActualEndTime())) {

                    group.getTopics().add(actual);
                    if (group.getStartDate().after(actual.getActualStartTime())) {
                        group.setStartDate(actual.getActualStartTime());
                    }
                    if (group.getEndDate().before(actual.getActualEndTime())) {
                        group.setEndDate(actual.getActualEndTime());
                    }
                    found = true;
                    break;
                }
            }

            if (!found) {
                AgendaGroup group = new AgendaGroup(actual.getActualStartTime(), actual.getActualEndTime());
                group.getTopics().add(actual);
                agenda.getActuals().add(group);
            }
        }

        rowCount = rowCount + rowIndex;
        agenda.setRowCount(rowCount);

        return agenda;
    }

    /**
     * 製品進捗表示
     * @param actualProduct エンティティ
     * @param now 現在時刻
     * @return エンティティ
     */
    public Agenda normalizeProductProgress(ActualProductInfoEntity actualProduct, final Date now) {

        Agenda agenda = new Agenda();
        agenda.setTitle1(actualProduct.getProductNumber());
        agenda.setDelayTimeMillisec((long)0);
        agenda.setShowPlanAndActualLabel(false);
        List<AddInfoEntity> addInfoEntities = JsonUtils.jsonToObjects(actualProduct.getKanbanAdditionalInfo(), AddInfoEntity[].class);
       
        // 顧客名設定
        final String customerName = addInfoEntities.stream()
                .filter(entity->"顧客名".equals(entity.getKey()))
                .map(AddInfoEntity::getVal)
                .filter(obj->!StringUtils.isEmpty(obj))
                .findFirst()
                // 顧客名が無い場合モデル名を表示
                .orElse(Optional.ofNullable(actualProduct.getModelName()).orElse(""));

        agenda.setTitle2(customerName);

        long totalNum; // 工程数 or ロット数
        long compNum;
        if (config.isShowProductionProgress()) {
            // 「進捗：完了ロット数／ロット数」で表示する
            totalNum = Objects.isNull(actualProduct.getLotQuantity()) ? 0 : actualProduct.getLotQuantity();
            compNum = Objects.isNull(actualProduct.getCompNum()) ? 0 : actualProduct.getCompNum();
        } else {
            // 「進捗：完了工程数／工程数」で表示する
            totalNum = actualProduct.getWorkNum();
            compNum = actualProduct.getCompWorkNum();
        }

        final KanbanStatusEnum kanbanStatus = StatusPatternEnum.toKanbanStatus(actualProduct.getKanbanStatus());

        logger.info("productNum={}, num={}, compNum={}", actualProduct.getProductNumber(), totalNum, compNum);
 
        // 計画
        AgendaTopic plan = new AgendaTopic(
                actualProduct.getKanbanId(),
                0L,
                0L,
                config.isShowProductionProgress() 
                        ? LocaleUtils.getString(StatusPatternEnum.toKanbanStatus(actualProduct.getKanbanStatus()).getResourceKey()) + " " + LocaleUtils.getString("key.Quantity") + " " + totalNum 
                        : LocaleUtils.getString(StatusPatternEnum.toKanbanStatus(actualProduct.getKanbanStatus()).getResourceKey()),
                "",
                "",
                kanbanStatus,
                kanbanStatus,
                0,
                actualProduct.getStartDatetime(),
                actualProduct.getCompDatetime(),
                null,
                null,
                "black",
                "white",
                0L,
                false
        );
        plan.setRow(0);

        AgendaPlan planGroup = new AgendaPlan();
        planGroup.getTopics().put(plan.getKanbanId(), new ArrayList<>());
        planGroup.getTopics().get(plan.getKanbanId()).add(plan);
        planGroup.setStartDate(plan.getPlanStartTime());
        planGroup.setEndDate(plan.getPlanEndTime());
        agenda.getPlans().add(planGroup);

        // 製品進捗
        Date startDateTime = Objects.nonNull(actualProduct.getActualStartDatetime()) 
                ? actualProduct.getActualStartDatetime() 
                : actualProduct.getStartDatetime();

        Date compDateTime;
        if (totalNum > 0 && compNum >= totalNum) {
            // 完了
            compDateTime = actualProduct.getActualCompDatetime();
        } else {
            // 作業中は予想完了時間を表示 
            List<BreakTimeInfoEntity> breakTimes = config.getBreakTimes().stream()
                    .map(o -> getBreakTime(o.getId()))
                    .collect(Collectors.toList());

            LocalDate localDate = adtekfuji.utility.DateUtils.toLocalDate(this.currentData.getFromDate());
            LocalTime startTime = adtekfuji.utility.DateUtils.toLocalTime(config.getEndTime());
            LocalTime endTime = adtekfuji.utility.DateUtils.toLocalTime(config.getStartTime());
            Date startOverTime = adtekfuji.utility.DateUtils.toDate(localDate, startTime);
            Date endOverTime = (startTime.compareTo(endTime) > 0) 
                    ? adtekfuji.utility.DateUtils.toDate(localDate.plusDays(1), endTime)
                    : adtekfuji.utility.DateUtils.toDate(localDate, endTime);
            breakTimes.add(new BreakTimeInfoEntity(0L, "", startOverTime, endOverTime));

            long workTime;
            if (config.isShowProductionProgress()) {
                if (Objects.isNull(actualProduct.getActualStartDatetime())) {
                    // 計画済み
                    long delayTime = now.getTime() - actualProduct.getStartDatetime().getTime();
                    if (delayTime < 0) { 
                        compDateTime = actualProduct.getCompDatetime();
                    } else {
                        compDateTime = this.includeBreakTime(actualProduct.getCompDatetime(), new Date(actualProduct.getCompDatetime().getTime() + delayTime), -1, breakTimes);
                        compDateTime = this.includeHoliday(compDateTime, false);
                    }

                } else {
                    if (config.isEnableCycleTime()
                            && Objects.nonNull(actualProduct.getCycleTime())) {
                        // 標準作業時間から予想完了日時を算出 (イトーキ様向け)
                        workTime = actualProduct.getCycleTime() * 1000L * (totalNum - compNum);

                    } else {
                        // 平均サイクルタイムから予想完了日時を算出
                        if (compNum > 0) {
                            workTime = BreaktimeUtil.getDiffTime(breakTimes, actualProduct.getActualStartDatetime(), now);
                            workTime = (workTime / compNum) * (totalNum - compNum);
                        } else {
                            workTime = BreaktimeUtil.getDiffTime(breakTimes, actualProduct.getStartDatetime(), actualProduct.getCompDatetime());
                        }
                    }

                    compDateTime = this.includeBreakTime(actualProduct.getActualStartDatetime(), new Date(now.getTime() + workTime), -1, breakTimes);
                    compDateTime = this.includeHoliday(compDateTime, false);
                }

            } else {
                DurationModel duration = this.estimate(actualProduct, now, breakTimes);
                compDateTime = duration.getEndDate();
            }
        }

        AgendaTopic progress = new AgendaTopic(
                actualProduct.getKanbanId(),
                0L,
                0L,
                LocaleUtils.getString(StatusPatternEnum.toKanbanStatus(actualProduct.getKanbanStatus()).getResourceKey()),
                String.format("%s : %d/%d", LocaleUtils.getString("key.Progress"), compNum, totalNum),
                "",
                kanbanStatus,
                kanbanStatus,
                0,
                startDateTime,
                compDateTime,
                actualProduct.getActualStartDatetime(),
                actualProduct.getActualCompDatetime(),
                "black",
                "white",
                0L,
                false
        );
        progress.setRow(0);
        progress.setProgress(totalNum == 0 ? 1 : (double)compNum/(double)totalNum);

        // 文字色、背景色、点灯パターンを設定
        final StatusPatternEnum pattern = actualProduct.getKanbanStatus();
        this.displayedStatuses.stream()
                .filter(entity -> Objects.equals(entity.getStatusName(), pattern))
                .findFirst()
                .ifPresent(entity-> {
                    if(!StringUtils.isEmpty(entity.getNotationName())) {
                        progress.setTitle1(entity.getNotationName());
                    }
                    progress.setFontColor(entity.getFontColor());
                    progress.setBackColor2(entity.getBackColor());
                    progress.setBlink(entity.getLightPattern() == LightPatternEnum.BLINK);
                });

        // 作業中以前の状態
        switch (pattern) {
            case PLAN_NORMAL:
            case PLAN_DELAYSTART:
                progress.setBackColor(progress.getBackColor2());
                break;
            case SUSPEND_NORMAL:
                if (!"FORCED".equals(actualProduct.getInterruptReason())) {
                    if (Objects.nonNull(actualProduct.getInterruptReason())) {
                        progress.setTitle1(actualProduct.getInterruptReason());
                    }
                    if (Objects.nonNull(actualProduct.getBackColor())) {
                        progress.setBackColor2(actualProduct.getBackColor());
                    }
                    if (Objects.nonNull(actualProduct.getFontColor())) {
                        progress.setFontColor(actualProduct.getFontColor());
                    }
                    if (Objects.nonNull(actualProduct.getLightPattern())) {
                        progress.setBlink(LightPatternEnum.BLINK.equals(actualProduct.getLightPattern()));
                    }
                }
                progress.setBackColor("#808080");
                break;
            case DEFECT:
                if (Objects.nonNull(actualProduct.getDefectReason())) {
                    progress.setTitle1(actualProduct.getDefectReason());
                }
                progress.setTitle2("");
            default:
                progress.setBackColor("#808080");
                break;
        }

        // ヘッダータイトルの文字色、背景色、点灯パターンを設定
        this.displayedStatuses.stream()
                .filter(displayedStatus -> Objects.equals(pattern, displayedStatus.getStatusName()))
                .findFirst()
                .ifPresent(entity -> {
                    agenda.setFontColor(entity.getFontColor());
                    agenda.setBackColor(entity.getBackColor());
                    if (entity.getLightPattern() == LightPatternEnum.BLINK) {
                        agenda.setBlink(true);
                    }
                });

        agenda.setFontColor("white");
        agenda.setBackColor("rgba(60,60,60)");
        agenda.setBlink(false);

        AgendaPlan group = new AgendaPlan();
        group.getTopics().put(progress.getKanbanId(), new ArrayList<>());
        group.getTopics().get(progress.getKanbanId()).add(progress);
        group.setStartDate(progress.getPlanStartTime());
        group.setEndDate(progress.getPlanEndTime());
        agenda.getProgress().add(group);
        return agenda;
    }


    /**
     * カンバンの休憩時間を取得する。
     *
     * @param workKanbans
     * @return
     */
    private List<BreakTimeInfoEntity> getBreakTimes(List<WorkKanbanInfoEntity> workKanbans) {
        Set<BreakTimeInfoEntity> breakTimes = new HashSet<>();
        for (WorkKanbanInfoEntity workKanban : workKanbans) {
            if (Objects.nonNull(workKanban.getOrganizationCollection()) && !workKanban.getOrganizationCollection().isEmpty()) {
                OrganizationInfoEntity organization = CacheUtils.getCacheOrganization(workKanban.getOrganizationCollection().get(0));
                if (Objects.isNull(organization)) {
                    continue;
                }
                for (Long breaktimeId : organization.getBreakTimeInfoCollection()) {
                    BreakTimeInfoEntity breakTime = this.breakTimeMap.get(breaktimeId);
                    breakTimes.add(breakTime);
                }
            }
        }
        return new ArrayList<>(breakTimes);
    }

    /**
     * スタイルを設定する。
     *
     * @param topic
     * @param now
     * @return
     */
    public StatusPatternEnum setStyle(AgendaTopic topic, Date now) {
        StatusPatternEnum pattern = StatusPatternEnum.getStatusPattern(topic.getKanbanStatus(), topic.getWorkKanbanStatus(), topic.getPlanStartTime(), topic.getPlanEndTime(), topic.getActualStartTime(), topic.getActualEndTime(), now);
        if (config.getMonitorType().equals(AndonMonitorTypeEnum.LITE_MONITOR)) {
            //Liteでは開始遅れ・完了遅れを計画通りに扱う
            switch(pattern){
                case PLAN_DELAYSTART:
                    pattern = StatusPatternEnum.PLAN_NORMAL;
                    break;
                case WORK_DELAYSTART:
                case WORK_DELAYCOMP:
                    pattern = StatusPatternEnum.WORK_NORMAL;
                    break;
                case COMP_DELAYCOMP:
                    pattern = StatusPatternEnum.COMP_NORMAL;
                    break;
                default:
                    break;
            }
        }
        for (DisplayedStatusInfoEntity entity : this.displayedStatuses) {
            if (entity.getStatusName() == pattern) {
                // 2778 予定通り作業中の工程も、ステータス一覧の編集で設定された色を反映する
                //if (pattern != StatusPatternEnum.WORK_NORMAL) {
                topic.setFontColor(entity.getFontColor());
                topic.setBackColor(entity.getBackColor());
                //}
                if (entity.getLightPattern() == LightPatternEnum.BLINK) {
                    topic.setBlink(true);
                }
                break;
            }
        }
        return pattern;
    }

    /**
     * スタイルを設定する。
     *
     * @param actual
     * @param now
     * @param workRecord
     * @return
     */
    public StatusPatternEnum setStyle(AgendaTopic actual, Date now, WorkRecordInfoEntity workRecord) {
        StatusPatternEnum pattern = StatusPatternEnum.getStatusPattern(actual.getKanbanStatus(), actual.getWorkKanbanStatus(), actual.getPlanStartTime(), actual.getPlanEndTime(), workRecord.getWorkStartTime(), workRecord.getWorkEndTime(), now);
        if (config.getMonitorType().equals(AndonMonitorTypeEnum.LITE_MONITOR)) {
            //Liteでは開始遅れ・完了遅れを計画通りに扱う
            switch(pattern){
                case PLAN_DELAYSTART:
                    pattern = StatusPatternEnum.PLAN_NORMAL;
                    break;
                case WORK_DELAYSTART:
                case WORK_DELAYCOMP:
                    pattern = StatusPatternEnum.WORK_NORMAL;
                    break;
                case COMP_DELAYCOMP:
                    pattern = StatusPatternEnum.COMP_NORMAL;
                    break;
                default:
                    break;
            }
        }
        for (DisplayedStatusInfoEntity entity : this.displayedStatuses) {
            if (entity.getStatusName() == pattern) {
                actual.setFontColor(entity.getFontColor());
                actual.setBackColor(entity.getBackColor());
                if (entity.getLightPattern() == LightPatternEnum.BLINK) {
                    actual.setBlink(true);
                }
                break;
            }
        }
        return pattern;
    }

    /**
     * 最後の実績完了時間を取得する。
     *
     * @param topics
     * @param now
     * @return
     */
    private Date getLastTime(List<AgendaTopic> agendaTopic, Date now) {
        Date last = new Date(0L);
        for (AgendaTopic topic : agendaTopic) {
            if (topic.getWorkKanbanStatus() == KanbanStatusEnum.COMPLETION) {
                if (Objects.nonNull(topic.getActualEndTime()) && topic.getActualEndTime().after(last)) {
                    last = topic.getActualEndTime();
                }
            } else {
                last = now;
                break;
            }
        }
        return last;
    }

    /**
     * カンバンの前倒し時間を取得する。
     *
     * @param topics
     * @return
     */
    private long getAdvanceTime(List<AgendaTopic> topics) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 10);
        Date actualStartTime = cal.getTime();
        Date planStartTime = cal.getTime();

        boolean exist = false;
        for (AgendaTopic topic : topics) {
            if (topic.hasActual() && actualStartTime.after(topic.getActualStartTime())) {
                actualStartTime = topic.getActualStartTime();
                exist = true;
            }

            if (planStartTime.after(topic.getPlanStartTime())) {
                planStartTime = topic.getPlanStartTime();
            }
        }

        if (exist) {
            long time = planStartTime.getTime() - actualStartTime.getTime();
            return (time > 0) ? time : 0;
        }

        return 0;
    }

    /**
     * カンバンの進捗時間を算出する。
     *
     * @param topics
     * @param now
     * @param shiftTime
     * @return
     */
    private long calcDelayTime(List<AgendaTopic> topics, Date now, long shiftTime) {
        long delayTime = 0L;
        Date planEndTime = new Date(0L);

        // 進捗を集計
        for (AgendaTopic topic : topics) {

            if (!(topic.getPlanStartTime().after(now) && (topic.getWorkKanbanStatus() == KanbanStatusEnum.PLANNING || topic.getWorkKanbanStatus() == KanbanStatusEnum.PLANNED))) {
                delayTime += calcDelayTime(topic, now, shiftTime);
            }

            if (topic.getPlanEndTime().after(planEndTime)) {
                planEndTime = topic.getPlanEndTime();
            }
        }

        // 現在時刻が最後の予定完了時刻を過ぎていたら、その分遅れになる
        if (now.getTime() + shiftTime > planEndTime.getTime()) {
            delayTime = delayTime - (now.getTime() + shiftTime - planEndTime.getTime());
        }

        return delayTime + shiftTime;
    }

    /**
     * 工程の進捗時間を算出する。
     *
     * @param topic
     * @param now
     * @param advanceTime
     * @return
     */
    private long calcDelayTime(AgendaTopic topic, Date now, long advanceTime) {

        if (Objects.nonNull(topic.getOrganizationId()) && !this.kanbanBreakTimeMap.containsKey(topic.getOrganizationId())) {
            OrganizationInfoEntity organizationInfoEntity = this.organizationInfoFacade.find(topic.getOrganizationId());
            this.kanbanBreakTimeMap.put(topic.getOrganizationId(), organizationInfoEntity.getBreakTimeInfoCollection()
                    .stream()
                    .map(this.breakTimeMap::get)
                    .collect(Collectors.toList()));
        }

        List<BreakTimeInfoEntity> breakTimes = this.kanbanBreakTimeMap.get(topic.getOrganizationId());

        long delayTime = BreaktimeUtil.getDiffTime(breakTimes, topic.getPlanStartTime(), new Date(now.getTime() + advanceTime));
        long diffTime = BreaktimeUtil.getDiffTime(breakTimes, topic.getPlanStartTime(), topic.getPlanEndTime());

        if (delayTime > diffTime) {
            delayTime = diffTime;
        }

        if (delayTime < 0) {
            delayTime = 0;
        }

        long proceedTime = 0L;
        switch (topic.getWorkKanbanStatus()) {
            case PLANNED:
            case SUSPEND:
            case INTERRUPT:
                proceedTime = topic.getSumTimes();
                break;
            case WORKING:
                proceedTime = BreaktimeUtil.getDiffTime(breakTimes, topic.getActualStartTime(), now);
                break;
            case COMPLETION:
                proceedTime = diffTime;
                break;
            default:
                break;
        }

        if (proceedTime > diffTime) {
            proceedTime = diffTime;
        }

        if (proceedTime < 0) {
            proceedTime = 0;
        }

        return proceedTime - delayTime;
    }

    /**
     * 更新タイマーを停止する。
     */
    private void refreshTimerCancel() {
        synchronized (this.lock) {
            if (Objects.nonNull(this.refreshTimer)) {
                this.refreshTimer.cancel();
                this.refreshTimer.purge();
                this.refreshTimer = null;
            }
        }
    }

    /**
     * 進捗情報CSVファイルを出力する。
     *
     * @param kanbanTopics
     * @param agendas
     */
    private void outputStatusInfoCsv(List<KanbanTopicInfoEntity> kanbanTopics, Map<Long, Agenda> agendas) {
        try {
            if (Objects.isNull(kanbanTopics)) {
                return;
            }

            // カンバン進捗情報CSVファイルのパス
            String kanbanFilePath = KanbanStatusConfig.getKanbanStatusCsvPath();
            // 工程進捗情報CSVファイルのパス
            String workFilePath = KanbanStatusConfig.getWorkStatusCsvPath();
            // 遅れ警告の閾値(％)(設定値以上の遅れで警告)
            long kanbanWarningThreshold = KanbanStatusConfig.getKanbanWarningThreshold();
            // 付加情報1(kanban_info1)に出力するプロパティ名
            String kanbanInfo1Name = KanbanStatusConfig.getKanbanInfo1();

            List<Long> kanbanIds = kanbanTopics.stream()
                    .map(p -> p.getKanbanId())
                    .collect(Collectors.toList());
            List<String> propNames = Arrays.asList(kanbanInfo1Name);
            List<KanbanPropertyInfoEntity> props = getKanbanProps(kanbanIds, propNames);

            Map<Long, KanbanStatusInfo> kanbans = new LinkedHashMap();// カンバン進捗情報
            Map<Long, WorkStatusInfo> works = new LinkedHashMap();// 工程進捗情報

            long sumTakt = 0;
            long befId = -1;

            String currentWork = "";
            Date currentDate = null;

            for (KanbanTopicInfoEntity kanbanTopic : kanbanTopics) {
                // 工程進捗情報を作成する。
                WorkStatusInfo work = createWorkStatusInfo(kanbanTopic);
                if (works.containsKey(kanbanTopic.getWorkKanbanId())) {
                    works.replace(kanbanTopic.getWorkKanbanId(), work);
                } else {
                    works.put(kanbanTopic.getWorkKanbanId(), work);
                }

                // カンバン進捗情報
                if (befId == kanbanTopic.getKanbanId()) {
                    sumTakt += kanbanTopic.getTaktTime() / 1000;
                } else {
                    sumTakt = kanbanTopic.getTaktTime() / 1000;
                    befId = kanbanTopic.getKanbanId();

                    // 現在作業中の工程
                    currentWork = "";
                    currentDate = null;
                }

                // 現在作業中の工程 (開始していて完了しておらず、最後に開始した工程)
                if (Objects.nonNull(kanbanTopic.getActualStartTime()) && Objects.isNull(kanbanTopic.getActualEndTime())) {
                    if (Objects.nonNull(currentDate) && currentDate.after(kanbanTopic.getActualStartTime())) {
                        continue;
                    }

                    // 開始日時が一番遅い
                    currentWork = kanbanTopic.getWorkName();
                    currentDate = kanbanTopic.getActualStartTime();
                }

                Agenda agenda = agendas.get(kanbanTopic.getKanbanId());

                // 付加情報1 (kanban_info1)
                String kanbanInfo1Value = "";
                Optional<KanbanPropertyInfoEntity> opt = props.stream()
                        .filter(p -> p.getFkKanbanId().equals(kanbanTopic.getKanbanId()) && p.getKanbanPropertyName().equals(kanbanInfo1Name))
                        .findFirst();
                if (opt.isPresent()) {
                    kanbanInfo1Value = opt.get().getKanbanPropertyValue();
                }

                // 遅れ時間(秒) (delay_sec)
                Long delaySec = 0L;
                if (Objects.nonNull(agenda)) {
                    delaySec = - agenda.getDelayTimeMillisec() / 1000;
                }
                if (delaySec < 0) {
                    delaySec = 0L;
                }

                // 遅れ警告時間 = (カンバンの全工程のタクトタイムの合計 × 遅れ時間警告の閾値) / 100
                long warnSec = (sumTakt * kanbanWarningThreshold) / 100L;

                // カンバン進捗情報を作成する。
                KanbanStatusInfo kanban = createKanbanStatusInfo(kanbanTopic, currentWork, kanbanInfo1Value, delaySec, warnSec);

                if (kanbans.containsKey(kanbanTopic.getKanbanId())) {
                    kanbans.replace(kanbanTopic.getKanbanId(), kanban);
                } else {
                    kanbans.put(kanbanTopic.getKanbanId(), kanban);
                }
            }

            // CSVファイルに出力する。
            this.outputWorkStatusFile(workFilePath, works);
            this.outputKanbanStatusFile(kanbanFilePath, kanbans);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程進捗情報を作成する。
     *
     * @param kanbanTopic カンバン計画実績
     *
     * @return 工程進捗情報
     */
    private WorkStatusInfo createWorkStatusInfo(KanbanTopicInfoEntity kanbanTopic) {
        WorkStatusInfo work = new WorkStatusInfo();

        // ステータス (work_status)
        int workStatus;
        switch (kanbanTopic.getWorkKanbanStatus()) {
            case PLANNING:// 計画中
            case PLANNED:// 計画済
                workStatus = 0;
                break;
            case WORKING:// 作業中
                workStatus = 1;
                break;
            case SUSPEND:// 中断中
                workStatus = 4;
                break;
            case COMPLETION:// 作業完了
                workStatus = 5;
                break;
            default:
                workStatus = 9;
        }

        // カンバン名 (kanban_name)
        work.setKanbanName(kanbanTopic.getKanbanName());
        // 表示順 (order_num)
        work.setOrderNum(kanbanTopic.getWorkKanbanOrder());
        // 工程名 (work_name)
        work.setWorkName(kanbanTopic.getWorkName());
        // ステータス (kanban_status)
        work.setWorkStatus(workStatus);
        // 計画開始時間 (plan_start_datetime)
        work.setPlanStartDatetime(kanbanTopic.getPlanStartTime());
        // 計画完了時間 (plan_comp_datetime)
        work.setPlanCompDatetime(kanbanTopic.getPlanEndTime());
        // 実績開始時間 (start_datetime)
        work.setStartDatetime(kanbanTopic.getActualStartTime());
        // 実績完了時間 (comp_datetime)
        work.setCompDatetime(kanbanTopic.getActualEndTime());
        // 工程順名 (workflow_name)
        work.setWorkflowName(kanbanTopic.getWorkflowName());
        // 版数 (workflow_rev)
        work.setWorkflowRev(kanbanTopic.getWorkflowRev());

        return work;
    }

    /**
     * カンバン進捗情報を作成する。
     *
     * @param kanbanTopic カンバン計画実績
     * @param currentWork 作業中の工程
     * @param kanbanInfo1Value 付加情報1
     * @param delaySec 遅れ時間(秒)
     * @param warnSec 遅れ警告時間(秒)
     * @return カンバン進捗情報
     */
    private KanbanStatusInfo createKanbanStatusInfo(KanbanTopicInfoEntity kanbanTopic, String currentWork, String kanbanInfo1Value, Long delaySec, long warnSec) {
        KanbanStatusInfo kanban = new KanbanStatusInfo();

        // ステータス (kanban_status)
        int kanbanStatus;
        switch (kanbanTopic.getKanbanStatus()) {
            case PLANNING:// 計画中
            case PLANNED:// 計画済
                kanbanStatus = 0;
                break;
            case WORKING:// 作業中
            case SUSPEND:// 中断中
                if (delaySec <= 0) {
                    kanbanStatus = 1;// 作業中 (計画通り)
                } else if (delaySec >= warnSec) {
                    kanbanStatus = 3;// 作業中 (警告)
                } else {
                    kanbanStatus = 2;// 作業中 (注意)
                }
                break;
            case COMPLETION:// 作業完了
                kanbanStatus = 5;
                break;
            default:
                kanbanStatus = 9;
        }

        // カンバン名 (kanban_name)
        kanban.setKanbanName(kanbanTopic.getKanbanName());
        // モデル名 (model_name)
        kanban.setModelName(kanbanTopic.getModelName());
        // 付加情報1 (kanban_info1)
        kanban.setKanbanInfo1(kanbanInfo1Value);
        // ステータス (kanban_status)
        kanban.setKanbanStatus(kanbanStatus);
        // 計画開始時間 (plan_start_datetime)
        kanban.setPlanStartDatetime(kanbanTopic.getKanbanPlanStartTime());
        // 計画完了時間 (plan_comp_datetime)
        kanban.setPlanCompDatetime(kanbanTopic.getKanbanPlanEndTime());
        // 実績開始時間 (start_datetime)
        kanban.setStartDatetime(kanbanTopic.getKanbanActualStartTime());
        // 実績完了時間 (comp_datetime)
        kanban.setCompDatetime(kanbanTopic.getKanbanActualEndTime());
        // 遅れ時間(秒) (delay_sec)
        kanban.setDelaySec(delaySec.intValue());
        // 作業中の工程 (current_work)
        kanban.setCurrentWork(currentWork);
        // 工程順名 (workflow_name)
        kanban.setWorkflowName(kanbanTopic.getWorkflowName());
        // 版数 (workflow_rev)
        kanban.setWorkflowRev(kanbanTopic.getWorkflowRev());

        return kanban;
    }

    /**
     * 工程進捗情報CSVファイルを出力する。
     *
     * @param filePath 工程進捗情報CSVファイルパス
     * @param works 工程進捗情報一覧
     */
    private void outputWorkStatusFile(String filePath, Map<Long, WorkStatusInfo> works) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            File file = new File(filePath);
            if (!file.isAbsolute()) {
                return;
            }

            File folder = new File(file.getParent());
            if (!folder.exists()) {
                // フォルダがない場合は作成する。
                if (!folder.mkdirs()) {
                    return;
                }
            }

            // CSVファイルに出力する。
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), CSV_CHARSET))) {
                // ヘッダーを出力する。
                StringBuilder title = new StringBuilder();
                title.append(CSV_QUOTE).append("kanban_name").append(CSV_QUOTE);// カンバン名
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("order_num").append(CSV_QUOTE);// 表示順
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("work_name").append(CSV_QUOTE);// 工程名
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("work_status").append(CSV_QUOTE);// ステータス
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("plan_start_datetime").append(CSV_QUOTE);// 計画開始時間
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("plan_comp_datetime").append(CSV_QUOTE);// 計画完了時間
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("start_datetime").append(CSV_QUOTE);// 実績開始時間
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("comp_datetime").append(CSV_QUOTE);// 実績完了時間
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("workflow_name").append(CSV_QUOTE);// 工程順名
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("workflow_rev").append(CSV_QUOTE);// 版数

                writer.write(title.toString());
                writer.newLine();

                // データを出力する。
                for (WorkStatusInfo work : works.values()) {
                    StringBuilder sb = new StringBuilder();

                    // 計画開始時間 (plan_start_datetime)
                    String planStartDatetime = this.formatDatetime(work.getPlanStartDatetime(), sdf);
                    // 計画完了時間 (plan_comp_datetime)
                    String planCompDatetime = this.formatDatetime(work.getPlanCompDatetime(), sdf);
                    // 実績開始時間 (start_datetime)
                    String startDatetime = this.formatDatetime(work.getStartDatetime(), sdf);
                    // 実績完了時間 (comp_datetime)
                    String compDatetime = this.formatDatetime(work.getCompDatetime(), sdf);

                    // カンバン名 (kanban_name)
                    sb.append(CSV_QUOTE).append(work.getKanbanName()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 表示順 (order_num)
                    sb.append(CSV_QUOTE).append(work.getOrderNum()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 工程名 (work_name)
                    sb.append(CSV_QUOTE).append(work.getWorkName()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // ステータス (kanban_status)
                    sb.append(CSV_QUOTE).append(work.getWorkStatus()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 計画開始時間 (plan_start_datetime)
                    sb.append(CSV_QUOTE).append(planStartDatetime).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 計画完了時間 (plan_comp_datetime)
                    sb.append(CSV_QUOTE).append(planCompDatetime).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 実績開始時間 (start_datetime)
                    sb.append(CSV_QUOTE).append(startDatetime).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 実績完了時間 (comp_datetime)
                    sb.append(CSV_QUOTE).append(compDatetime).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 工程順名 (workflow_name)
                    sb.append(CSV_QUOTE).append(work.getWorkflowName()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 版数 (workflow_rev)
                    sb.append(CSV_QUOTE).append(work.getWorkflowRev()).append(CSV_QUOTE);

                    writer.write(sb.toString());
                    writer.newLine();
                }

                writer.close();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバン進捗情報CSVファイルを出力する。
     *
     * @param filePath カンバン進捗情報CSVファイルパス
     * @param kanbans カンバン進捗情報一覧
     */
    private void outputKanbanStatusFile(String filePath, Map<Long, KanbanStatusInfo> kanbans) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            File file = new File(filePath);
            if (!file.isAbsolute()) {
                return;
            }

            File folder = new File(file.getParent());
            if (!folder.exists()) {
                // フォルダがない場合は作成する。
                if (!folder.mkdirs()) {
                    return;
                }
            }

            // CSVファイルに出力する。
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), CSV_CHARSET))) {
                // ヘッダーを出力する。
                StringBuilder title = new StringBuilder();
                title.append(CSV_QUOTE).append("kanban_name").append(CSV_QUOTE);// カンバン名
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("model_name").append(CSV_QUOTE);// モデル名
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("kanban_info1").append(CSV_QUOTE);// 付加情報1
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("kanban_status").append(CSV_QUOTE);// ステータス
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("plan_start_datetime").append(CSV_QUOTE);// 計画開始時間
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("plan_comp_datetime").append(CSV_QUOTE);// 計画完了時間
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("start_datetime").append(CSV_QUOTE);// 実績開始時間
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("comp_datetime").append(CSV_QUOTE);// 実績完了時間
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("delay_sec").append(CSV_QUOTE);// 遅れ時間(秒)
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("current_work").append(CSV_QUOTE);// 作業中の工程
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("workflow_name").append(CSV_QUOTE);// 工程順名
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append("workflow_rev").append(CSV_QUOTE);// 版数

                writer.write(title.toString());
                writer.newLine();

                // データを出力する。
                for (KanbanStatusInfo kanban : kanbans.values()) {
                    StringBuilder sb = new StringBuilder();

                    // 計画開始時間 (plan_start_datetime)
                    String planStartDatetime = this.formatDatetime(kanban.getPlanStartDatetime(), sdf);
                    // 計画完了時間 (plan_comp_datetime)
                    String planCompDatetime = this.formatDatetime(kanban.getPlanCompDatetime(), sdf);
                    // 実績開始時間 (start_datetime)
                    String startDatetime = this.formatDatetime(kanban.getStartDatetime(), sdf);
                    // 実績完了時間 (comp_datetime)
                    String compDatetime = this.formatDatetime(kanban.getCompDatetime(), sdf);

                    // カンバン名 (kanban_name)
                    sb.append(CSV_QUOTE).append(kanban.getKanbanName()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // モデル名 (model_name)
                    sb.append(CSV_QUOTE).append(kanban.getModelName()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 付加情報1 (kanban_info1)
                    sb.append(CSV_QUOTE).append(kanban.getKanbanInfo1()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // ステータス (kanban_status)
                    sb.append(CSV_QUOTE).append(kanban.getKanbanStatus()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 計画開始時間 (plan_start_datetime)
                    sb.append(CSV_QUOTE).append(planStartDatetime).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 計画完了時間 (plan_comp_datetime)
                    sb.append(CSV_QUOTE).append(planCompDatetime).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 実績開始時間 (start_datetime)
                    sb.append(CSV_QUOTE).append(startDatetime).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 実績完了時間 (comp_datetime)
                    sb.append(CSV_QUOTE).append(compDatetime).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 遅れ時間(秒) (delay_sec)
                    sb.append(CSV_QUOTE).append(kanban.getDelaySec()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 作業中の工程 (current_work)
                    sb.append(CSV_QUOTE).append(kanban.getCurrentWork()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 工程順名 (workflow_name)
                    sb.append(CSV_QUOTE).append(kanban.getWorkflowName()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 版数 (workflow_rev)
                    sb.append(CSV_QUOTE).append(kanban.getWorkflowRev()).append(CSV_QUOTE);

                    writer.write(sb.toString());
                    writer.newLine();
                }

                writer.close();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンプロパティ一覧を取得する。
     *
     * @param kanbanIds カンバンID一覧
     * @param propNames プロパティ名一覧
     * @return カンバンプロパティ一覧
     */
    private List<KanbanPropertyInfoEntity> getKanbanProps(List<Long> kanbanIds, List<String> propNames) {
        List<KanbanPropertyInfoEntity> result = new ArrayList();
        try {
            PropertySearchCondition condition = new PropertySearchCondition()
                    .parentIdList(kanbanIds)
                    .propNameList(propNames);

            KanbanInfoFacade facade = new KanbanInfoFacade();
            result = facade.findKanbanPropSearch(condition);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 指定したフォーマットで日時文字列を取得する。
     *
     * @param date 日時
     * @param sdf 日時フォーマット
     * @return 日時文字列
     */
    private String formatDatetime(Date date, SimpleDateFormat sdf) {
        String result = "";
        if (Objects.nonNull(date)) {
            result = sdf.format(date);
        }
        return result;
    }

    /**
     * 休日を除いた場合の検索対象日(TO)設定
     *
     * @param from 日時
     * @param holidays 休日対象日リスト
     */
    private void setToDateExceptHoliday(Date from, List<Date> holidays) {

        this.holidays.clear();
        if (this.config.isShowHoliday()) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        int days = config.getShowDays();
        Date day = from;
        int count = 0;

        while (true) {
            if (!holidays.contains(day)) {
                // 休日でなければ加算
                count++;
                if (count == days) {
                    // 表示日数分になったら対象取得日を設定
                    this.currentData.setToDate(new Date(day.getTime() + (23 * 3600 + 59 * 60 + 59) * 1000L));
                    return;
                }
            } else {
                // 休日であれば対象休日リストに加える
                this.holidays.add(day);
            }
            // 計算日を一日進める
            calendar.setTime(day);
            calendar.add(Calendar.DATE, 1);
            day = calendar.getTime();
        }
    }

    /**
     * 表示開始日を計算する。
     */
    private void calcBeforeDays() {
        if (TimeAxisEnum.VerticalAxis.equals(config.getTimeAxis())) {
            currentData.setDate(currentData.getKeepTargetDay().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        } else {
            switch (config.getHorizonTimeScale()) {
                case HalfDay:
                case Day:
                case Week:
                    BeforeDaysEnum beforeDays = this.config.getHorizonBeforeDays();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentData.getKeepTargetDay());
                    calendar.add(Calendar.DAY_OF_MONTH, -(beforeDays.getValue()));
                    currentData.setFromDate(calendar.getTime());
                    break;
                case Month:
                    currentData.setFromDate(currentData.getKeepTargetDay());
                    break;
                case Time:
                default:
                    currentData.setDate(currentData.getKeepTargetDay().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    break;
            }
        }
    }

    /**
     * 呼出し
     */
    public void noticeCall() {
        // 画面を更新する
        try {
            ThreadUtils.joinFXThread(() -> {
                controller.updateDisplay();
                return null;
            });
        } catch (Exception ex) {
        }
                                    
        if (Objects.isNull(controller) || !controller.isUseCall()) {
            melodyPlayer.stop();
            return;
        }

        try {
            // 呼出音を鳴らす
            List<Long> organizationList = new ArrayList<>();
            if (Objects.nonNull(currentData.getAgendas())) {
                currentData.getAgendas().values().forEach((agenda) -> {
                    agenda.getPlans().forEach((plans) -> {
                        plans.getTopics().entrySet().forEach((entry) -> {
                            entry.getValue().forEach( topic -> {
                                organizationList.add(topic.getOrganizationId());
                            });
                        });
                    });
                    agenda.getActuals().forEach((actuals) -> {
                        actuals.getTopics().stream().forEach((topic) -> {
                            organizationList.add(topic.getOrganizationId());
                        });
                    });
                });
            }
            if (callingPool.containsOrganizationCall(organizationList)) {
                if (melodyPlayer.isPlaying()) {
                    melodyPlayer.stop();
                }
                melodyPlayer.play(config.getCallSoundSetting(), true);
            } else {
                melodyPlayer.stop();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

    }
    
    /**
     * 現在日時と工程カンバンの作業開始日時の差分（ミリ秒）を取得
     * 
     * @param now 現在日時
     * @param startDatetime 作業開始日時
     * @return long 現在日時との差分
     */
    private long getNowDatetimeDiff(Date now, Date actualStartTime) {
        try {
            Instant startDatetimeInstant = actualStartTime.toInstant();
            LocalDateTime localStartDatetime = LocalDateTime.ofInstant(startDatetimeInstant, ZoneId.systemDefault());
            Instant nowInstant = now.toInstant();
            LocalDateTime nowDatetime = LocalDateTime.ofInstant(nowInstant, ZoneId.systemDefault());
            // 現在日時から作業開始日時の差分を取得
            Duration duration = Duration.between(localStartDatetime, nowDatetime);
            return duration.toMillis();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Long.valueOf(0);
        }
    }

    /**
     * 工程カンバンの完了予定日時を取得
     * 
     * @param actualStartTime 開始日時
     * @param taktTime タクトタイム
     * @return Date 完了予定日時
     */
    private Date getWorkCompDatetime(Date actualStartTime, int taktTime) {
        try {
            Instant startDatetimeInstant = actualStartTime.toInstant();
            LocalDateTime localStartDatetime = LocalDateTime.ofInstant(startDatetimeInstant, ZoneId.systemDefault());
            // 開始日時にタクトタイム（ミリ秒）を加算
            localStartDatetime = localStartDatetime.plusSeconds(taktTime / 1000);
            return Date.from(localStartDatetime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * ステータスのメロディを再生する
     * 
     * @param statusInfo ステータス情報
     */
    private synchronized void playStatusMelody(DisplayedStatusInfoEntity statusInfo) {
        try {
            if (this.melodyPlayer.isPlaying()) {
                // 既にメロディ再生中なら何もしない
                return;
            }
            String melodyPath = statusInfo.getMelodyPath();
            Boolean isRepeat = statusInfo.getMelodyRepeat();
            if (!StringUtils.isEmpty(melodyPath)) {
                this.melodyPlayer.play(melodyPath, isRepeat);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 休憩を含めた時間を求める。 
     *
     * @param startDatetime
     * @param endDatetime
     * @param days
     * @param breakTimes
     * @return
     */
    private Date includeBreakTime(Date startDatetime, Date endDatetime, int days, List<BreakTimeInfoEntity> breakTimes) {

        Date result = endDatetime;

        long startTime = startDatetime.getTime();
        long endTime = endDatetime.getTime();
        Date date = null;
        long breakTime = 0;

        List<BreakTimeInfoEntity> entities = BreaktimeUtil.getAppropriateBreaktimes(breakTimes, startDatetime, endDatetime, days);
        for (BreakTimeInfoEntity entity : entities) {
            long startBreakTime = entity.getStarttime().getTime();
            long endBreakTime = entity.getEndtime().getTime();

            if ((startTime > startBreakTime && startTime < endBreakTime) && (endTime < endBreakTime && endTime > startBreakTime)) {
                // ④ (工程開始 > 休憩開始 and 工程開始 < 休憩終了) and (工程終了 < 休憩終了 and 工程終了 > 休憩開始)
                // 休憩時間を一部加算する
                breakTime += endBreakTime - startTime;
                date = entity.getEndtime();
            } else if (startTime <= startBreakTime && endTime >= endBreakTime) {
                // ① 工程開始 <= 休憩開始 and 工程終了 >= 休憩終了
                // 休憩時間を全部加算する
                breakTime += endBreakTime - startBreakTime;
                date = entity.getEndtime();
            } else if (endTime < endBreakTime && endTime > startBreakTime) {
                // ② 工程終了 < 休憩終了 and 工程終了 > 休憩開始
                // 休憩時間を全部加算する
                breakTime += endBreakTime - startBreakTime;
                date = entity.getEndtime();
            } else if (startTime > startBreakTime && startTime < endBreakTime) {
                // ③ 工程開始 > 休憩開始 and 工程開始 < 休憩終了
                // 休憩時間を一部加算する
                breakTime += endBreakTime - startTime;
                date = entity.getEndtime();
            }
        }

        if (0 != breakTime) {
            result = this.includeBreakTime(date, new Date(endTime + breakTime), 0, breakTimes);
        }

        return result;
    }

    /**
     * 作業時間を取得する。
     * 
     * @param actualProduct
     * @param now
     * @param breakTimes
     * @return 
     */
    private DurationModel estimate(ActualProductInfoEntity actualProduct, Date now, List<BreakTimeInfoEntity> breakTimes) {
        DurationModel duration = new DurationModel();
               
        KanbanSearchCondition condition = new KanbanSearchCondition();
        condition.setProductionNumber(actualProduct.getProductNumber());
        condition.setMatchType(MatchTypeEnum.MATCH);
        
        List<KanbanInfoEntity> kanbans = this.kanbanFacade.searchResults(condition, null, null);
        for (KanbanInfoEntity kanban : kanbans) {
            duration.plus(this.estimate(kanban, now, breakTimes));
        }

        return duration;
    }

    /**
     * 作業期間を求める。
     * 
     * @param kanban
     * @param now
     * @param breakTimes
     * @return 
     */
    private DurationModel estimate(KanbanInfoEntity kanban, Date now, List<BreakTimeInfoEntity> breakTimes) {
       
        if (KanbanStatusEnum.PLANNED.equals(kanban.getKanbanStatus())) {
            long delayTime = now.getTime() - kanban.getStartDatetime().getTime();

            Date compTime;
            if (delayTime < 0) { 
                compTime = kanban.getCompDatetime();
            } else {
                compTime = this.includeBreakTime(kanban.getCompDatetime(), new Date(kanban.getCompDatetime().getTime() + delayTime), -1, breakTimes);
                compTime = this.includeHoliday(compTime, false);
            }

            return new DurationModel(kanban.getStartDatetime(), compTime);
        }

        if (KanbanStatusEnum.COMPLETION.equals(kanban.getKanbanStatus())) {
            return new DurationModel(kanban.getActualStartTime(), kanban.getActualCompTime());
        }

        long workTime = 0L;
        for (WorkKanbanInfoEntity workKanban : kanban.getWorkKanbanCollection()) {

            if (workKanban.getSkipFlag()) {
                continue;
            }
            
            int taktTime = workKanban.getTaktTime() * kanban.getLotQuantity();

            switch (workKanban.getWorkStatus()) {
                case WORKING:
                    LongProperty workingTime = new SimpleLongProperty(0);
                    List<WorkRecordInfoEntity> workRecords = this.agendaFacade.getHistory(AgendaFacade.Type.WORKKANBAN, Arrays.asList(workKanban.getWorkKanbanId()), null, null);
                    workRecords.stream()
                            .filter(o -> Objects.isNull(o.getActualEndTime()))
                            .forEach(o -> {
                                workingTime.set(workingTime.get() + BreaktimeUtil.getDiffTime(breakTimes, o.getActualStartTime(), now));
                            });
                    long sumTimes = workKanban.getSumTimes() + workingTime.get();
                    workTime += taktTime > sumTimes ? taktTime : sumTimes;
                    break;
                case PLANNED:
                case SUSPEND:
                    long elapsedTime = BreaktimeUtil.getDiffTime(breakTimes, workKanban.getActualStartTime(), now);
                    workTime += taktTime > elapsedTime ? taktTime : elapsedTime;
                    break;
                case COMPLETION:
                    workTime += workKanban.getSumTimes();
                    break;
                default:
                    break;
            }
        }
        
        Date endDate = this.includeBreakTime(kanban.getActualStartTime(), new Date(kanban.getActualStartTime().getTime() + workTime), -1, breakTimes);
        endDate = this.includeHoliday(endDate, false);

        return new DurationModel(kanban.getActualStartTime(), endDate);
    }

    /**
     * 対象日時が休日かチェックして、休日の場合は翌営業日を返し、違う場合は対象日時をそのまま返す。
     *
     * @param targetDate 対象日時
     * @param isStartDate 開始日時？
     * @return
     */
    private Date includeHoliday(Date targetDate, boolean isStartDate) {
        LocalTime openTime = adtekfuji.utility.DateUtils.toLocalTime(config.getStartTime());

        Date date = targetDate;
        if (Objects.nonNull(this.holidays) && !this.holidays.isEmpty()) {
            for (Date holiday : this.holidays) {
                Date holidayStart = adtekfuji.utility.DateUtils.getBeginningOfDate(holiday);
                Date holidayEnd = adtekfuji.utility.DateUtils.getEndOfDate(holiday);
                if ((holidayStart.before(date) || holidayStart.equals(date))
                        && (holidayEnd.after(date) || holidayEnd.equals(date))) {
                    // 休日の場合は翌日にする。
                    LocalDateTime nextDay = adtekfuji.utility.DateUtils.toLocalDateTime(date).plusDays(1);
                    if (isStartDate) {
                        // 開始日時の場合は時刻も変更する。
                        date = adtekfuji.utility.DateUtils.toDate(nextDay.toLocalDate(), openTime);
                    } else {
                        date = adtekfuji.utility.DateUtils.toDate(nextDay);
                    }
                } else if (holidayStart.after(date)) {
                    // 休日が対象日時より後の場合、以降の休日はチェック不要。
                    break;
                }
            }
        }
        return date;
    }
}
