/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXB;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.CancelWorkCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.adinterface.command.WorkDetail;
import jp.adtekfuji.adFactory.adinterface.command.WorkResult;
import jp.adtekfuji.adFactory.entity.MessageEntity;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.ResultResponse;
import jp.adtekfuji.adFactory.entity.actual.DefectInfoEntity;
import jp.adtekfuji.adFactory.entity.actual.DefectSerialEntity;
import jp.adtekfuji.adFactory.entity.assemblyparts.AssemblyPartsUpdateRequest;
import jp.adtekfuji.adFactory.entity.directwork.ActualAddInfoEntity;
import jp.adtekfuji.adFactory.entity.directwork.WorkReportWorkNumEntity;
import jp.adtekfuji.adFactory.entity.dsKanban.DsActual;
import jp.adtekfuji.adFactory.entity.dsKanban.DsKanban;
import jp.adtekfuji.adFactory.entity.dsKanban.DsKanbanCreateCondition;
import jp.adtekfuji.adFactory.entity.dsKanban.DsKanbanProperty;
import jp.adtekfuji.adFactory.entity.dsKanban.DsParts;
import jp.adtekfuji.adFactory.entity.dsKanban.DsPickup;
import jp.adtekfuji.adFactory.entity.job.KanbanCode;
import jp.adtekfuji.adFactory.entity.job.KanbanProduct;
import jp.adtekfuji.adFactory.entity.job.MultiWork;
import jp.adtekfuji.adFactory.entity.job.OrderInfoEntity;
import jp.adtekfuji.adFactory.entity.job.WorkComment;
import jp.adtekfuji.adFactory.entity.job.WorkCommentContainer;
import jp.adtekfuji.adFactory.entity.kanban.*;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.entity.operation.OperateAppEnum;
import jp.adtekfuji.adFactory.entity.operation.OperationTypeEnum;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.HolidaySearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.search.OperationSerachCondition;
import jp.adtekfuji.adFactory.entity.search.PropertySearchCondition;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition;
import jp.adtekfuji.adFactory.entity.warehouse.LotTracePartsInfo;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ActualResultDailyEnum;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import static jp.adtekfuji.adFactory.enumerate.CompCountTypeEnum.EQUIPMENT;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.DataTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanSearchTypeEnum;
import static jp.adtekfuji.adFactory.enumerate.KanbanSearchTypeEnum.A;
import static jp.adtekfuji.adFactory.enumerate.KanbanSearchTypeEnum.B;
import jp.adtekfuji.adFactory.enumerate.KanbanSortPatternEnum;
import static jp.adtekfuji.adFactory.enumerate.KanbanSortPatternEnum.CREATE;
import static jp.adtekfuji.adFactory.enumerate.KanbanSortPatternEnum.NAME;
import static jp.adtekfuji.adFactory.enumerate.KanbanSortPatternEnum.PLAN;
import static jp.adtekfuji.adFactory.enumerate.KanbanSortPatternEnum.STATUS;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.LineManagedCommandEnum;
import jp.adtekfuji.adFactory.enumerate.LineManagedStateEnum;
import jp.adtekfuji.adFactory.enumerate.MatchTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServiceTypeEnum;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.common.SystemConfig;
import jp.adtekfuji.adfactoryserver.entity.ErrorResultEntity;
import jp.adtekfuji.adfactoryserver.entity.ListWrapper;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualAditionEntity;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity_;
import jp.adtekfuji.adfactoryserver.entity.actual.ProdResultEntity;
import jp.adtekfuji.adfactoryserver.entity.actual.WorkingReport;
import jp.adtekfuji.adfactoryserver.entity.assemblyparts.AssemblyPartsEntity;
import jp.adtekfuji.adfactoryserver.entity.directwork.DirectActualEntity;
import jp.adtekfuji.adfactoryserver.entity.dsKanban.MstDsItem;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity_;
import jp.adtekfuji.adfactoryserver.entity.holiday.HolidayEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.*;
import jp.adtekfuji.adfactoryserver.entity.master.BreaktimeEntity;
import jp.adtekfuji.adfactoryserver.entity.operation.OperationEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity_;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDeliveryItem;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnLotTrace;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnMaterial;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity_;
import jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity_;
import jp.adtekfuji.adfactoryserver.model.ActrualResultRuntimeData;
import jp.adtekfuji.adfactoryserver.model.AssemblyPartsModel;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.model.LineManager;
import jp.adtekfuji.adfactoryserver.model.TraceabilityJdbc;
import jp.adtekfuji.adfactoryserver.model.warehouse.WarehouseModel;
import jp.adtekfuji.adfactoryserver.service.workflow.LotWorkflowModelFacede;
import jp.adtekfuji.adfactoryserver.service.workflow.WorkflowInteface;
import jp.adtekfuji.adfactoryserver.service.workflow.WorkflowModelFacade;
import jp.adtekfuji.adfactoryserver.service.workflow.WorkflowProcess;
import jp.adtekfuji.adfactoryserver.utility.AggregateLineInfoFacade;
import jp.adtekfuji.adfactoryserver.utility.BreakTimeUtils;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import static jp.adtekfuji.adfactoryserver.utility.JsonUtils.jsonToObjects;
import jp.adtekfuji.adfactoryserver.utility.LocaleUtils;
import jp.adtekfuji.andon.entity.LineTimerControlRequest;
import jp.adtekfuji.andon.entity.MonitorLineTimerInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * カンバン情報REST
 *
 * @author ke.yokoi
 */
@Singleton
@Path("kanban")
public class KanbanEntityFacadeREST extends AbstractFacade<KanbanEntity> {

    /**
     * カンバン名のフォーマット
     * 
     * <pre>
     * {機種名} [{製造番号}] {出荷予定日}
     * 
     * ・各項目(機種名、製造番号、出荷予定日)は半角スペース区切り
     * ・機種名は任意の文字列
     * ・製造番号は1桁以上の数字で、[]で囲む
     * 　単一の製造番号は[2634]、複数の製造番号は[2601-2604]のように設定
     * ・出荷予定日は任意の文字列(オプション扱い)
     * </pre>
     */
    final static Pattern workReportWorkNumPattern = Pattern.compile("^.+\\s\\[(\\d+(-\\d+)?)\\].*$");


    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private WorkKanbanEntityFacadeREST workKandanRest;

    @EJB
    private WorkEntityFacadeREST workRest;

    @EJB
    private WorkflowEntityFacadeREST workflowRest;

    @EJB
    private OrganizationEntityFacadeREST organizationRest;

    @EJB
    private EquipmentEntityFacadeREST equipmentRest;

    @EJB
    private ActualResultEntityFacadeREST actualResultRest;
    
    @EJB
    private ActualAditionEntityFacadeREST actualAditionRest;

    @EJB
    private BreaktimeEntityFacadeREST breaktimeRest;

    @EJB
    private WorkKanbanWorkingEntityFacadeREST workKanbanWorkingRest;

    @EJB
    private AdIntefaceClientFacade adIntefaceFacade;

    @EJB
    private KanbanHierarchyEntityFacadeREST kanbanHierarchyRest;

    @EJB
    private AggregateLineInfoFacade lineFacade;

    @EJB
    private HolidayEntityFacadeREST holidayRest;

    @EJB
    private PartsEntityFacadeREST partsRest;

    @EJB
    private KanbanReportEntityFacadeREST kanbanReportRest;
    
    @EJB
    private OperationEntityFacadeREST operationRest;

    @EJB
    private DirectActualEntityFacadeREST directActualEntityFacadeREST;
    
    @EJB
    private DsItemFacade dsItemFacade;
    
    @Inject
    private WarehouseModel warehouseModel;

    @Inject
    private AssemblyPartsModel assemblyPartsModel;

    private final Logger logger = LogManager.getLogger();
    private final LineManager lineManager = LineManager.getInstance();

    private final static int WORKFLOW_ENTITYSET_CAPACITY = 10;
    private final static int WORK_ENTITYSET_CAPACITY = 1000;
    private final static float LOAD_FACTOR = 0.75f;
    private final static String REPAIR = "REPAIR";
    private final static String REPAIR_NUM = "REPAIR_NUM";
    private final static String DESC = "DESC";
    private static final String pattern[] = {DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(), "yyyy-MM-dd"};
    private static final String CLOSE_KANBAN_HIERARCHY_FORMAT = "%1s-CLOSE-%2$tY/%2$tm";// 完了カンバンの移動先階層名 (親階層名-CLOSE-yyyy/MM)
    private static final String SERVICE_INFO_LOT = "els";
    private static final String TAG_LOT_TRACE_PARTS = "_LOT_TRACE_PARTS";

    private Date zeroTime;
    private String word_spec1 = null;
    private String word_spec2 = null;
    private String word_spec3 = null;
    private String word_spec4 = null;
    private Long loginUserId = null;
    private boolean isTraceabilityDBEnabled;    // トレーサビリティDB使用フラグ (品質トレーサビリティの保存先)
    private boolean isWorkOvertime;             // 休憩時間中でのカウントダウン継続設定
    private boolean isDbOutput;                 // 実績データの外部出力
    private boolean isEnablePartsTrace;         // 部品トレースを有効にする
    private boolean isWorkReportWorkNumVisible = false ; // 作業日報に作業数と製造番号を付加する
    private boolean isEnableRework = false;     // 後戻り作業を有効にする

    // 工程順情報のキャッシュ
    private final Map<Long, WorkflowEntity> workflowEntitySet = Collections.synchronizedMap(new LinkedHashMap(WORKFLOW_ENTITYSET_CAPACITY, LOAD_FACTOR, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > WORKFLOW_ENTITYSET_CAPACITY;
        }
    });

    // 工程情報のキャッシュ
    private final Map<Long, WorkEntity> workEntitySet = Collections.synchronizedMap(new LinkedHashMap(WORK_ENTITYSET_CAPACITY, LOAD_FACTOR, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > WORK_ENTITYSET_CAPACITY;
        }
    });

    /**
     * コンストラクタ
     */
    public KanbanEntityFacadeREST() {
        super(KanbanEntity.class);
    }

    /**
     * クラスを初期化する。
     */
    @PostConstruct
    public void initialize() {

        try {
            this.zeroTime = DateUtils.convertTimeZone(new Date(0), "UTC");

            this.isWorkReportWorkNumVisible = SystemConfig.getInstance().isWorkReportWorkNumVisible();

            Properties properties = FileManager.getInstance().getSystemProperties();
            this.word_spec1 = properties.getProperty("word_spec1", "");
            this.word_spec2 = properties.getProperty("word_spec2", "");
            this.word_spec3 = properties.getProperty("word_spec3", "");
            this.word_spec4 = properties.getProperty("word_spec4", "");

            // トレーサビリティDB使用フラグ (品質トレーサビリティの保存先)
            this.isTraceabilityDBEnabled = Boolean.valueOf(properties.getProperty(Constants.TRACEABILITY_DB_ENABLED_KEY, Constants.TRACEABILITY_DB_ENABLED_DEF));

            // 実績データの外部出力
            this.isDbOutput = Boolean.valueOf(properties.getProperty("enableDbOutput", "false"));

            // 実績データの外部出力
            this.isEnablePartsTrace = Boolean.valueOf(properties.getProperty("enablePartsTrace", "false"));
        
            // 休憩時間中でのカウントダウン継続設定
            this.isWorkOvertime = StringUtils.parseBoolean(properties.getProperty(Constants.WORK_OVERTIME, "false"));

            // 後戻り作業 (Liteのみ)
            this.isEnableRework = Boolean.valueOf(properties.getProperty("LiteDefectWork", "false"));
            
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 指定したIDのカンバン情報を取得する。(基本情報のみ)
     *
     * @param id カンバンID
     * @return カンバン情報
     */
    @Lock(LockType.READ)
    public KanbanEntity findBasicInfo(Long id) {
        KanbanEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            return new KanbanEntity();
        }

        // カンバンが属する階層の階層IDを取得する。
        Long parentId = this.findParentId(id);
        if (Objects.nonNull(parentId)) {
            entity.setParentId(parentId);
        }

        return entity;
    }

    /**
     * カンバンIDを指定して、カンバン情報を取得する。
     *
     * @param id カンバンID
     * @param authId 認証ID
     * @return カンバン情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public KanbanEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);
        try {
            KanbanEntity entity = super.find(id);
            if (Objects.isNull(entity)) {
                return new KanbanEntity();
            }

            // カンバンが属する階層の階層IDを取得する。
            Long parentId = this.findParentId(id);
            if (Objects.nonNull(parentId)) {
                entity.setParentId(parentId);
            }

            // 詳細情報を取得してセットする。
            this.getDetails(entity, true);

            return entity;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }


    /**
     * 指定したカンバン名・工程順名・版数のカンバン情報を取得する。
     *
     * @param name カンバン名
     * @param workflowName 工程順名
     * @param workflowRev 版数
     * @param authId 認証ID
     * @return カンバン情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public KanbanEntity findByName(@QueryParam("name") String name, @QueryParam("workflowName") String workflowName, @QueryParam("rev") Integer workflowRev, @QueryParam("authId") Long authId) {
        logger.info("findByName: name={}, workflowName={}, rev={}, authId={}", name, workflowName, workflowRev, authId);
        try {
            TypedQuery<KanbanEntity> query;
            if (StringUtils.isEmpty(workflowName)) {
                // カンバン名を指定して、カンバン情報を取得する。
                query = this.em.createNamedQuery("KanbanEntity.findByKanbanName", KanbanEntity.class);
            } else {
                //if (Objects.isNull(workflowRev)) {
                //    // 版数指定なしの場合は最新版 (削除済・未承認は除く)
                //    workflowRev = this.workflowRest.getLatestRev(workflowName, true, true);
                //}

                // カンバン名・工程順名・版数を指定して、カンバン情報を取得する。
                if (Objects.isNull(workflowRev)) {
                    query = this.em.createNamedQuery("KanbanEntity.findByName", KanbanEntity.class);
                    query.setParameter("workflowName", workflowName);
                } else {
                    query = this.em.createNamedQuery("KanbanEntity.findByNameAndRev", KanbanEntity.class);
                    query.setParameter("workflowName", workflowName);
                    query.setParameter("workflowRev", workflowRev);
                }
            }

            query.setParameter("kanbanName", name);
            query.setMaxResults(1);

            KanbanEntity entity = query.getSingleResult();

            if (Objects.isNull(entity)) {
                throw new NoResultException();
            }

            // カンバンが属する階層の階層IDを取得する。
            Long parentId = this.findParentId(entity.getKanbanId());
            if (Objects.nonNull(parentId)) {
                entity.setParentId(parentId);
            }

            // 詳細情報を取得してセットする。
            this.getDetails(entity, true);

            logger.info("Found a kanban:{}, {}, {}", entity.getKanbanName(), entity.getWorkflowName(), entity.getWorkflowRev());

            return entity;
        } catch (NoResultException ex) {
            logger.fatal(ex);
            return new KanbanEntity();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン情報一覧を取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return カンバン情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);
        try {
            List<KanbanEntity> entities;
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                entities = super.findRange(from, to);
            } else {
                entities = super.findAll();
            }

            for (KanbanEntity entity : entities) {
                // カンバンが属する階層の階層IDを取得する。
                Long parentId = this.findParentId(entity.getKanbanId());
                if (Objects.nonNull(parentId)) {
                    entity.setParentId(parentId);
                }

                // 詳細情報を取得してセットする。
                this.getDetails(entity, false);
            }

            return entities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン情報の件数を取得する。
     *
     * @param authId 認証ID
     * @return カンバン情報一覧の件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countAll(@QueryParam("authId") Long authId) {
        logger.info("countAll: authId={}", authId);
        try {
            return String.valueOf(super.count());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン検索条件を指定して、カンバン情報一覧を取得する。
     *
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return カンバン情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanEntity> searchKanban(KanbanSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchKanban: {}, from={}, to={}, authId={}", condition, from, to, authId);
        try {
            Query query = this.getSearchQuery(SearchType.SEARCH, condition);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            List<KanbanEntity> entities = query.getResultList();

            for (KanbanEntity entity : entities) {
                // カンバンが属する階層の階層IDを取得する。
                Long parentId = this.findParentId(entity.getKanbanId());
                if (Objects.nonNull(parentId)) {
                    entity.setParentId(parentId);
                }

                // 詳細情報を取得してセットする。
                this.getDetails(entity, condition.isAdditionalInfo());
            }

            return entities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン検索条件を指定して、カンバン情報の件数を取得する。
     *
     * @param condition 検索条件
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countKanban(KanbanSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countKanban: {}, authId={}", condition, authId);
        try {
            Query query = this.getSearchQuery(SearchType.COUNT, condition);
            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン存在コレクション情報を用いてカンバン存在コレクション情報を取得
     *
     * @param entity カンバン存在コレクション情報
     * @param authId 認証ID
     * @return カンバン存在コレクション情報
     */
    @Lock(LockType.READ)
    @PUT
    @Path("exist")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public KanbanExistCollection existKanban(KanbanExistCollection entity, @QueryParam("authId") Long authId) {
        logger.info("existKanban: {}, authId={}", entity, authId);
        try {
            KanbanExistCollection exist = new KanbanExistCollection();
            exist.setKanbanExistCollection(new ArrayList());
            for (KanbanExistEntity kanbanExist : entity.getKanbanExistCollection()) {
                if (this.existKanban(kanbanExist)) {
                    exist.getKanbanExistCollection().add(kanbanExist);
                }
            }

            return exist;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン情報を登録する。
     *
     * @param entity カンバン情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(KanbanEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);

        final Date now = new Date();
        try {
            // カンバン名の重複を確認する。(削除済も含む)
            KanbanExistEntity exist = new KanbanExistEntity(null, entity.getKanbanName(), entity.getKanbanSubname(), entity.getWorkflowId());
            if (this.existKanban(exist)) {
                // 該当するものがあった場合、重複を通知する。
                logger.info("not update overlap name:{}", entity);
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            entity.setKanbanStatus(KanbanStatusEnum.PLANNING);
            entity.setDefectNum(0);

            // カンバン情報を登録する。
            super.create(entity);
            this.em.flush();

            // カンバンの階層関連付け情報を登録する。
            this.addHierarchy(entity);

            // 工程順IDが設定されている場合、工程カンバン情報を作成する。
            if (Objects.nonNull(entity.getWorkflowId())) {
                // 工程順情報を取得する。
                WorkflowEntity workflow = this.getWorkflowEntity(entity.getWorkflowId());

                if (StringUtils.isEmpty(entity.getModelName())) {
                    entity.setModelName(workflow.getModelName());
                }


                // 追加情報をセットする。
                entity.setKanbanAddInfo(workflow.getWorkflowAddInfo());

                // 工程カンバン情報を登録する。
                this.addWorkKanban(entity, workflow);

                // 開始予定日時が設定されている場合、カンバンの基準時間を更新する。
                if (Objects.isNull(entity.getStartDatetime())) {
                    entity.setStartDatetime(now);
                }

                this.updateBaseTime(entity, workflow, entity.getStartDatetime());
                entity.getWorkKanbanCollection()
                        .forEach(workKanbanEntity -> this.em.persist(workKanbanEntity));
            }

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("kanban/").append(entity.getKanbanId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    // TODO: 未使用 (カンバンのコピーには、工程カンバン等の付随情報のコピーも必要)
    /**
     * カンバン情報をコピーする。
     *
     * @param id カンバンID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Path("copy")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response copy(@PathParam("id") Long id, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("copy: id={}, authId={}", id, authId);
        try {
            KanbanEntity entity = this.find(id, authId);
            if (Objects.isNull(entity.getKanbanId())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
            }

            boolean isFind = true;
            StringBuilder name = new StringBuilder(entity.getKanbanName())
                    .append(SUFFIX_COPY);
            while (isFind) {
                // カンバン名の重複を確認する。(削除済も含む)
                KanbanExistEntity exist = new KanbanExistEntity(
                        null, name.toString(), entity.getKanbanSubname(), entity.getWorkflowId());
                if (this.existKanban(exist)) {
                    name.append(SUFFIX_COPY);
                    continue;
                }
                isFind = false;
            }

            KanbanEntity newEntity = new KanbanEntity(entity);
            newEntity.setKanbanName(name.toString());

            // 新規追加する。
            this.add(newEntity, authId);

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("kanban/").append(newEntity.getKanbanId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * カンバン情報を更新する。
     *
     * @param entity カンバン情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(KanbanEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 排他用バージョンを確認する。
            KanbanEntity target = super.find(entity.getKanbanId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // カンバン名の重複を確認する。(削除済も含む)
            KanbanExistEntity exist = new KanbanExistEntity(entity.getKanbanId(), entity.getKanbanName(), entity.getKanbanSubname(), entity.getWorkflowId());
            if (this.existKanban(exist)) {
                // 該当するものがあった場合、重複を通知する。
                logger.info("not update overlap name:{}", entity);
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }


            // 現在のカンバン情報を取得する。
            KanbanEntity nowKanban = this.find(entity.getKanbanId(), authId);
            this.em.detach(nowKanban);

            // 編集可能な状態か確認する。
            final boolean isUpdatable = nowKanban.getKanbanStatus().isKanbanUpdatableStatus;

            final ServerErrorTypeEnum errorType = isUpdatable
                    ? ServerErrorTypeEnum.SUCCESS
                    : ServerErrorTypeEnum.UPDATE_ONLY_STATUS;

            // 編集可能で、工程順IDが設定されている場合、工程カンバン情報を更新する。
            if (isUpdatable && Objects.nonNull(entity.getWorkflowId())) {
                // 工程順情報を取得する。
                WorkflowEntity workflow = this.workflowRest.find(entity.getWorkflowId(), null, authId);
                entity.setWorkflow(workflow);

                // 工程カンバン情報を更新する。
                this.updateWorkKanban(entity);
            }

            KanbanStatusEnum newStatus = entity.getKanbanStatus();
            entity.setKanbanStatus(nowKanban.getKanbanStatus());

            entity.setActualStartTime(nowKanban.getActualStartTime());
            entity.setActualCompTime(nowKanban.getActualCompTime());
            entity.setDefectNum(nowKanban.getDefectNum());
            entity.setCompNum(nowKanban.getCompNum());
            entity.setUpdateDatetime(new Date()); // 更新日時

            if (Boolean.parseBoolean(FileManager.getInstance().getSystemProperties().getProperty("enableCycleTimeImport", "false")) 
                    && KanbanStatusEnum.PLANNED.equals(newStatus)
                    && Objects.isNull(entity.getActualStartTime())
                    && Objects.nonNull(entity.getCycleTime())) {
                entity.setCompDatetime(this.estimate(entity));
            }
            
            // カンバン情報を更新する。
            super.edit(entity);

            if (isUpdatable && !Objects.equals(nowKanban.getKanbanStatus(), newStatus)) {
                // カンバンステータスを更新
                logger.info("Update kanban status: {}, {}, {}", entity.getKanbanName(), nowKanban.getKanbanStatus(), newStatus);
                this.updateStatus(Arrays.asList(entity.getKanbanId()), newStatus.toString(), false, false, entity.getUpdatePersonId());
            }

            // カンバンの階層関連付け情報を更新する。
            this.registHierarchy(entity);

            // ロット流しカンバンの場合、シリアル番号毎の一個流しカンバンを作成する。
            if (entity.getProductionType() == 2) {
                ServerErrorTypeEnum err = this.addSerialKanban(entity);
                if (!ServerErrorTypeEnum.SUCCESS.equals(err)) {
                    return Response.serverError().entity(ResponseEntity.failed(err)).build();
                }
            }

            return Response.ok().entity(ResponseEntity.success().errorType(errorType)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定したカンバンIDのカンバン情報を削除する。
     *
     * @param id カンバンID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("remove: id={}, authId={}", id, authId);
        try {
            // カンバンIDを指定して、カンバン情報を取得する。
            KanbanEntity kanban = this.find(id, authId);
            if (Objects.isNull(kanban.getKanbanId())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_DELETE)).build();
            }

            // 実績があるカンバンは削除不可。
            ActualSearchCondition condition = new ActualSearchCondition().kanbanId(id);
            Long count = Long.parseLong(this.actualResultRest.countActualResult(condition, authId));
            if (count > 0) {
                logger.fatal("not delete kanban:{}", kanban);
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.THERE_START_NON_DELETABLE)).build();
            }

            // 工程カンバン情報を削除する。
            this.removeWorkKanban(kanban);
            // カンバンの階層関連付け情報を削除する。
            this.removeHierarchy(kanban.getKanbanId());
            // 製品情報を削除する。
            this.removeProducts(id);
            // カンバン帳票情報を削除する。
            this.kanbanReportRest.removeByKanbanId(Arrays.asList(id));

            // カンバン情報をを削除する。
            super.remove(kanban);

            // 削除フラグの立っている工程順を使用している場合は工程順を削除する
            final WorkflowEntity workflowEntity = this.workflowRest.find(kanban.getWorkflowId());
            if (workflowEntity.getRemoveFlag()) {
                // 使用しているカンバンが無い場合は削除
                final Long numKanban = this.workflowRest.countKanbanAssociation(workflowEntity.getWorkflowId());
                if (numKanban <= 0) {
                    this.workflowRest.removeWorkflow(workflowEntity.getWorkflowId(), false, null);
                }
            }
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定したカンバンIDのカンバン情報を強制的に削除する。
     *
     * @param id カンバンID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("forced/{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response removeForced(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("removeForced: id={}, authId={}", id, authId);
        try {
            // 生産実績を削除する。
            this.removeProdResult(id);

            // カンバンIDを指定して、工程実績付加情報を削除する。
            this.actualAditionRest.removeKanbanActualAditions(id);

            // カンバンIDで工程実績を削除する。
            this.actualResultRest.removeKanbanActuals(id);

            // カンバンIDを指定して、完成品情報を削除する。
            this.partsRest.deleteKanbanParts(id);

            // トレーサビリティDB使用時は、カンバンIDでトレーサビリティを削除する。
            if (this.isTraceabilityDBEnabled) {
                TraceabilityJdbc jdbc = TraceabilityJdbc.getInstance();
                jdbc.deleteKanbanTraceability(id);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }

        // カンバンを削除する。
        return this.remove(id, authId);
    }

    /**
     * 工程順を進める。
     *
     * @param kanban カンバン情報
     * @param workflow 工程順情報
     * @param workId 工程ID
     * @return true:正常終了、false:異常終了
     */
    public Boolean startWorkflow(KanbanEntity kanban, WorkflowEntity workflow, Long workId) {
        logger.info("startWorkflow start: kanbanId={}, workId={}", kanban.getKanbanId(), workId);
        
        try {
            WorkflowInteface workflowModel;
            if (kanban.getProductionType() != 1) {
                // 一個流し生産・ロット生産の場合
                workflowModel = WorkflowModelFacade.createInstance(this.workKandanRest, kanban.getWorkKanbanCollection(), workflow.getWorkflowDiaglam());
            } else {
                // ロット一個流し生産の場合
                workflowModel = LotWorkflowModelFacede.createInstance(this.workKandanRest, kanban, workflow.getWorkflowDiaglam());
            }

            if (Objects.nonNull(kanban.getWorkflowId()) && Objects.nonNull(workflow.getWorkflowDiaglam())) {
                // 実行許可
                workflowModel.executeWorkflow(workId, null);
            }

            // 追加工程は全て実行許可
            if (Objects.nonNull(kanban.getSeparateworkKanbanCollection())) {
                for (WorkKanbanEntity workKanban : kanban.getSeparateworkKanbanCollection()) {
                    workKanban.setImplementFlag(true);
         
                    if (StringUtils.isEmpty(workKanban.getServiceInfo())) {
                        continue;
                    }
       
                    // サービス情報
                    List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(workKanban.getServiceInfo(), ServiceInfoEntity[].class);
                    for (ServiceInfoEntity serviceInfo : serviceInfos) {
                        if (StringUtils.equals(serviceInfo.getService(), "product")) {
                            List<KanbanProduct> products = KanbanProduct.toKanbanProducts(serviceInfo);
                            for (KanbanProduct product : products) {
                                product.setImplement(true);
                            }
                            serviceInfo.setJob(products);
                            workKanban.setServiceInfo(JsonUtils.objectsToJson(serviceInfos));
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        } finally {
            logger.info("startWorkflow end.");
        }
        return true;
    }

    /**
     * 詳細情報を取得してセットする。
     *
     * @param kanban カンバン情報
     * @param isGetWorks 工程カンバン情報を取得するか(true:取得する, false:取得しない) 
     */
    @Lock(LockType.READ)
    private void getDetails(KanbanEntity kanban, boolean isGetWorks) {
        // 工程順情報を取得する。
        WorkflowEntity workflow = kanban.getWorkflow();
        if (Objects.isNull(workflow)) {
            workflow = this.workflowRest.findBasicInfo(kanban.getWorkflowId());
            kanban.setWorkflow(workflow);
        }

        kanban.setWorkflowName(workflow.getWorkflowName());
        kanban.setWorkflowRev(workflow.getWorkflowRev());
        kanban.setLedgerPath(workflow.getLedgerPath());

        if (isGetWorks) {
            // 通常工程の工程カンバン情報を取得してセットする。
            kanban.setWorkKanbanCollection(this.getWorkKanban(kanban.getKanbanId()));
            // 追加工程の工程カンバン情報を取得してセットする。
            kanban.setSeparateworkKanbanCollection(this.getSeparateWorkKanban(kanban.getKanbanId()));
            // 製品情報を取得してセットする。
            kanban.setProducts(this.getProducts(kanban));
        }
    }

    /**
     * 製品情報を削除する。
     *
     * @param id カンバンID
     */
    private void removeProducts(Long id) {
        Query query = this.em.createNamedQuery("ProductEntity.removeByKanbanId");
        query.setParameter("fkKanbanId", id);
        query.executeUpdate();
    }

    /**
     * 生産実績を削除する。
     *
     * @param id カンバンID
     */
    private void removeProdResult(Long id) {
        Query query = this.em.createNamedQuery("ProdResultEntity.removeByKanbanId");
        query.setParameter("fkKanbanId", id);
        query.executeUpdate();
    }

    // TODO: 未使用
//    /**
//     * 製品情報を削除する。
//     *
//     * @param productIds 製品ID一覧
//     */
//    private void removeByProductIds(List<Long> productIds) {
//        Query query = em.createNamedQuery("ProductEntity.removeByProductIds");
//        query.setParameter("productIds", productIds);
//        query.executeUpdate();
//    }

    /**
     * カンバンIDを指定して、カンバンが属する階層の階層IDを取得する。
     *
     * @param id カンバンID
     * @return カンバン階層ID
     */
    @Lock(LockType.READ)
    private Long findParentId(Long id) {
        TypedQuery<Long> query = this.em.createNamedQuery("ConKanbanHierarchyEntity.findHierarchyId", Long.class);
        query.setParameter("kanbanId", id);
        query.setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            // 親階層が設定されていない。
            return null;
        }
    }

    /**
     * 指定したIDのカンバンの階層関連付け情報を削除する。
     *
     * @param id カンバンID
     */
    private void removeHierarchy(Long id) {
        // カンバンIDを指定して、カンバン階層関連付け情報を削除する。
        Query query = this.em.createNamedQuery("ConKanbanHierarchyEntity.removeByKanbanId");
        query.setParameter("kanbanId", id);

        query.executeUpdate();
    }

    /**
     * カンバンの階層関連付け情報を登録する。
     *
     * @param entity カンバン情報
     */
    private void addHierarchy(KanbanEntity entity) {
        ConKanbanHierarchyEntity hierarchy = new ConKanbanHierarchyEntity(entity.getParentId(), entity.getKanbanId());
        this.em.persist(hierarchy);
    }

    /**
     * カンバンの階層関連付け情報を登録または更新する。
     *
     * @param entity カンバン情報
     * @return 
     */
    private boolean registHierarchy(KanbanEntity entity) {
        boolean result = false;

        // カンバンIDを指定して、階層関連付け情報の件数を取得する。
        TypedQuery<Long> countQuery = this.em.createNamedQuery("ConKanbanHierarchyEntity.countByKanbanId", Long.class);
        countQuery.setParameter("kanbanId", entity.getKanbanId());

        Long count = countQuery.getSingleResult();
        if (count == 0) {
            // カンバンの階層関連付け情報を新規登録する。
            ConKanbanHierarchyEntity hierarchy = new ConKanbanHierarchyEntity(entity.getParentId(), entity.getKanbanId());
            this.em.persist(hierarchy);
            result = true;
        } else {
            // カンバンの階層関連付け情報を更新する。
            Query updateQuery = this.em.createNamedQuery("ConKanbanHierarchyEntity.updateHierarchyId");
            updateQuery.setParameter("hierarchyId", entity.getParentId());
            updateQuery.setParameter("kanbanId", entity.getKanbanId());

            int updateCount = updateQuery.executeUpdate();
            if (updateCount == 1) {
                result = true;
            }
        }

        return result;
    }

    /**
     * カンバンIDを指定して、通常工程の工程カンバン情報一覧を取得する。
     *
     * @param id カンバンID
     * @return 工程カンバン情報一覧
     */
    @Lock(LockType.READ)
    private List<WorkKanbanEntity> getWorkKanban(Long id) {
        return this.workKandanRest.getWorkKanban(id, false, null, null);
    }

    /**
     * カンバンIDを指定して、追加工程の工程カンバン情報一覧を取得する。
     *
     * @param id カンバンID
     * @return 工程カンバン情報一覧
     */
    @Lock(LockType.READ)
    private List<WorkKanbanEntity> getSeparateWorkKanban(Long id) {
        return this.workKandanRest.getWorkKanban(id, true, null, null);
    }

    /**
     * 製品情報を取得する。
     *
     * @param entity
     * @return
     */
    @Lock(LockType.READ)
    private List<ProductEntity> getProducts(KanbanEntity entity) {
        if (Objects.isNull(entity.getLotQuantity())) {
            return null;
        }
        return this.getProducts(entity.getKanbanId(), null);
    }

    /**
     * 製品情報を取得する。
     *
     * @param kanbanId カンバンID
     * @param authId 認証ID
     * @return 製品情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("product/{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ProductEntity> getProducts(@PathParam("id") Long kanbanId, @QueryParam("authId") Long authId) {
        TypedQuery<ProductEntity> query = em.createNamedQuery("ProductEntity.findByKanbanId", ProductEntity.class);
        query.setParameter("fkKanbanId", kanbanId);
        return query.getResultList();
    }

    /**
     * 生産実績を取得する。
     *
     * @param kanbanId
     * @param workId
     * @return
     */
    @Lock(LockType.READ)
    private Map<String, ProdResultEntity> getProdResultMap(Long kanbanId, Long workId) {
        Map<String, ProdResultEntity> results = new HashMap();

        TypedQuery<ProdResultEntity> query = this.em.createNamedQuery("ProdResultEntity.findByKanbanIdAndWorkId", ProdResultEntity.class);
        query.setParameter("fkKanbanId", kanbanId);
        query.setParameter("fkWorkId", workId);
        List<ProdResultEntity> resultList = query.getResultList();

        for (ProdResultEntity entiy : resultList) {
            results.put(entiy.getPK().getUniqueId(), entiy);
        }
        return results;
    }

    /**
     * 工程順情報を取得する。(同時呼び出し不可)
     *
     * @param workflowId 工程順ID
     * @return 工程順情報
     */
    private WorkflowEntity getWorkflowEntity(Long workflowId) {
        logger.info("getWorkflowEntity: workflowId={}", workflowId);
        WorkflowEntity workflow;
        if (this.workflowEntitySet.containsKey(workflowId)) {
            // キャッシュにある場合、キャッシュから取得する。
            workflow = this.workflowEntitySet.get(workflowId);

            // 現在の工程順情報を取得する。(基本情報のみ)
            WorkflowEntity nowWorkflow = this.workflowRest.findBasicInfo(workflowId);
            if (nowWorkflow.getUpdateDatetime().after(workflow.getUpdateDatetime())) {
                // 更新日時が変わっていたら、工程順情報を再取得する。
                workflow = this.workflowRest.find(workflowId, null, null);
                this.workflowEntitySet.put(workflowId, workflow);
            }
        } else {
            // キャッシュにない場合、工程順情報を取得する。
            workflow = this.workflowRest.find(workflowId, null, null);
            this.workflowEntitySet.put(workflowId, workflow);
        }
        return workflow;
    }

    private final Map<Long, WorkflowEntity> workflowMap = new HashMap();

    private WorkflowEntity getWorkflowEntity2(Long workflowId) {
        logger.info("getWorkflowEntity2: workflowId={}", workflowId);
        WorkflowEntity workflow;
        if (this.workflowMap.containsKey(workflowId)) {
            // キャッシュにある場合、キャッシュから取得する。
            workflow = this.workflowMap.get(workflowId);
        } else {
            // キャッシュにない場合、工程順情報を取得する。
            workflow = this.workflowRest.find(workflowId, null, null);
            this.workflowMap.put(workflowId, workflow);
        }
        return workflow;
    }

    /**
     * 最新版の工程順情報を取得する。
     */
    private WorkflowEntity getWorkflowLatest(Long workflowId) {
        logger.info("getWorkflowLatestRev: workflowId={}", workflowId);
        WorkflowEntity workflow = this.workflowRest.find(workflowId, true, null);
        return this.workflowRest.findByName(workflow.getWorkflowName(), null, null, null, null);
    }

    /**
     * 工程情報を取得する。 (同時呼び出し不可)
     *
     * @param workId 工程ID
     * @return 工程情報
     */
    private WorkEntity getWorkEntity(Long workId, Long kanbanId) {
        WorkEntity work;
        if (this.workEntitySet.containsKey(workId)) {
            // キャッシュにある場合、キャッシュから取得する。
            work = this.workEntitySet.get(workId);

            // 現在の工程情報を取得する。(基本情報のみ)
            WorkEntity nowWork = this.workRest.findBasicInfo(workId);
            if (nowWork.getUpdateDatetime().after(work.getUpdateDatetime())) {
                // 更新日時が変わっていたら、工程情報を再取得する。
                work = this.workRest.find(workId, false, kanbanId, true, null);
                this.workEntitySet.put(workId, work);
            }
        } else {
            // キャッシュにない場合、工程情報を取得する。
            work = this.workRest.find(workId, false, kanbanId, true, null);
            this.workEntitySet.put(workId, work);
        }
        return work;
    }

    /**
     * カンバンの実績を取得する
     *
     * @param kanbanId
     * @return
     */
    @Lock(LockType.READ)
    private List<ActualResultEntity> getActualResults(Long kanbanId) {
        ActualSearchCondition condition = new ActualSearchCondition().kanbanId(kanbanId);
        return actualResultRest.searchActualResult(condition, null, null, null);
    }

    /**
     * 工程カンバン情報を登録する。
     *
     * @param entity カンバン情報
     * @param workflow 工程順情報
     * @throws URISyntaxException 
     */
    private void addWorkKanban(KanbanEntity entity, WorkflowEntity workflow) throws URISyntaxException {
        // 並列工程の行数
        IntegerProperty rowsProperty = new SimpleIntegerProperty(0);

        // 通常工程の工程カンバン情報を作成する。
        List<WorkKanbanEntity> workKanbans = this.createWorkKanban(
                entity, workflow.getWorkflowId(), workflow.getConWorkflowWorkCollection(), false,
                entity.getUpdatePersonId(), entity.getUpdateDatetime(), rowsProperty);

        // 追加工程の工程カンバン情報を作成する。
        List<WorkKanbanEntity> sepWorkKanbans = this.createWorkKanban(
                entity, workflow.getWorkflowId(), workflow.getConWorkflowSeparateworkCollection(), true,
                entity.getUpdatePersonId(), entity.getUpdateDatetime(), rowsProperty);

        if (!sepWorkKanbans.isEmpty()) {
            workKanbans.addAll(sepWorkKanbans);
        }

        // 工程カンバン情報を登録する。
        this.workKandanRest.addAll(workKanbans);
    }

    /**
     * 工程カンバン情報を作成する。
     *
     * @param entity カンバン情報
     * @param workflowId 工程順ID
     * @param conWorks 工程順・工程関連付け情報一覧
     * @param separateWorkFlag 追加工程フラグ
     * @param updatePersonId 更新者(組織ID)
     * @param updateDatetime 更新日時
     * @param rowsProperty 並列工程の行数
     * @return 工程カンバン情報一覧
     */
    private List<WorkKanbanEntity> createWorkKanban(KanbanEntity entity, Long workflowId, List<ConWorkflowWorkEntity> conWorks, boolean separateWorkFlag, Long updatePersonId, Date updateDatetime, IntegerProperty rowsProperty) {
        List<WorkKanbanEntity> workKanbans = new LinkedList();

        int rows = 0;
        if (separateWorkFlag) {
            rows = rowsProperty.get() + 1;
        }

        for (ConWorkflowWorkEntity conWork : conWorks) {
            // 工程情報を取得する。
            WorkEntity work0 = this.getWorkEntity(conWork.getWorkId(), null);
            WorkEntity work = new WorkEntity(work0);
            work.setWorkId(work0.getWorkId());
            workflowRest.applyWorkParameters(work, workflowId, entity.getModelName());

            // 工程カンバン情報を作成する。
            WorkKanbanEntity workKanban = new WorkKanbanEntity(entity.getKanbanId(),
                    workflowId, conWork.getWorkId(), conWork.getWorkName(),
                    separateWorkFlag, false, conWork.getSkipFlag(),
                    conWork.getStandardStartTime(), conWork.getStandardEndTime(), work.getTaktTime(), 0L,
                    updatePersonId, updateDatetime,
                    KanbanStatusEnum.PLANNED, null, null, conWork.getWorkflowOrder());

            // 通常工程の場合、同時作業フラグの処理を行なう。
            if (!separateWorkFlag) {
                // 工程の追加情報のJSON文字列を追加情報一覧に変換する。
                List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(work.getWorkAddInfo(), AddInfoEntity[].class);

                // 「同時作業禁止」の設定を検索する。
                Optional<AddInfoEntity> opt = addInfos.stream()
                        .filter(p -> Constants.DISABLE_SYNC_WORK.equals(p.getKey())).findFirst();

                if (opt.isPresent()) {
                    if (Constants.YES.equals(opt.get().getVal())) {
                        workKanban.setSyncWork(true);
                    } else {
                        workKanban.setSyncWork(false);
                    }
                }

                int order = (workKanban.getWorkKanbanOrder() % 10000);
                rows = Math.max(rows, order);
            } else {
                workKanban.setWorkKanbanOrder(rows + workKanban.getWorkKanbanOrder());
            }

            // 設備割り当てをセットする。
            workKanban.setEquipmentCollection(conWork.getEquipmentCollection());
            // 組織割り当てをセットする。
            workKanban.setOrganizationCollection(conWork.getOrganizationCollection());
            // 追加情報をセットする。
            workKanban.setWorkKanbanAddInfo(work.getWorkAddInfo());

            workKanbans.add(workKanban);
        }

        rowsProperty.set(rows);
        return workKanbans;
    }

    /**
     * カンバン作成条件に従い工程カンバンを追加する。
     *
     * @param entity
     * @param workflow
     * @param condition
     * @throws URISyntaxException
     */
    private void addWorkKanban(KanbanEntity entity, WorkflowEntity workflow, KanbanCreateCondition condition) throws URISyntaxException {
        List<WorkKanbanEntity> workKanbans = new ArrayList();

        // 休憩時間を取得する。
        Set<Long> breaktimeIds = new HashSet();
        for (ConWorkflowWorkEntity conWork : workflow.getConWorkflowWorkCollection()) {
            for (Long organizationId : conWork.getOrganizationCollection()) {
                breaktimeIds.addAll(this.organizationRest.getBreaktimes(organizationId));
            }
        }
        List<BreaktimeEntity> breakTimes = this.breaktimeRest.find(new ArrayList(breaktimeIds));

        Date startTime;
        Date endTime;
        Date nextTime = null;
        int order = 1;

        if (condition.isOnePieceFlow()) {
            // 通常生産(一個流し・ロット流し)の場合
            endTime = condition.getStartTime();

            int lotQuantity = condition.getLotQuantity();
            if (condition.getProductionType() == 2) {
                // ロット流し生産の場合、ロット数分の工程カンバンは作成しない。
                lotQuantity = 1;
            }

            // 通常工程の工程カンバン
            for (ConWorkflowWorkEntity conWork : workflow.getConWorkflowWorkCollection()) {
                WorkEntity work0 = this.getWorkEntity(conWork.getWorkId(), null);
                WorkEntity work = new WorkEntity(work0);
                work.setWorkId(work0.getWorkId());
                workflowRest.applyWorkParameters(work, workflow.getWorkflowId(), entity.getModelName());
                long taktTime = conWork.getStandardEndTime().getTime() - conWork.getStandardStartTime().getTime();
                int count = 1;

                // 工程の追加情報のJSON文字列を追加情報一覧に変換する。
                List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(work.getWorkAddInfo(), AddInfoEntity[].class);
                boolean syncWork = addInfos.stream().anyMatch(p -> Constants.DISABLE_SYNC_WORK.equals(p.getKey()) && Constants.YES.equals(p.getVal()));

                Optional<AddInfoEntity> productQty = addInfos.stream().filter(p -> Constants.PRODUCT_QTY.equals(p.getKey())).findFirst();
                if (productQty.isPresent()) {
                    try {
                        count = Integer.parseInt(productQty.get().getVal());
                    } catch (Exception ex) {
                    }
                }

                for (int ii = 0; ii < lotQuantity; ii += count) {
                    startTime = endTime;
                    Date estimatedTime = new Date(startTime.getTime() + taktTime);

                    List<BreaktimeEntity> breakInWork = BreakTimeUtils.getBreakInWork(breakTimes, startTime, estimatedTime);
                    endTime = BreakTimeUtils.getEndTimeWithBreak(breakInWork, startTime, estimatedTime);

                    nextTime = (0 == ii && !conWork.getSkipFlag()) ? endTime : startTime;

                    // 工程カンバン
                    WorkKanbanEntity workKanban = new WorkKanbanEntity(entity.getKanbanId(), workflow.getWorkflowId(), conWork.getWorkId(), conWork.getWorkName(), false, false,
                            conWork.getSkipFlag(), startTime, endTime, work.getTaktTime(), 0L, entity.getUpdatePersonId(), entity.getUpdateDatetime(),
                            KanbanStatusEnum.PLANNED, null, null, (order * 10000 + ii + 1));
                    
                    workKanban.setSyncWork(syncWork); // 同時作業禁止
 
                    workKanban.setEquipmentCollection(conWork.getEquipmentCollection());
                    workKanban.setOrganizationCollection(conWork.getOrganizationCollection());

                    if (condition.getProductionType() == 1) {
                        // ロット1個流し生産
                        workKanban.setSerialNumber((ii + count <= lotQuantity) ? ii + count : lotQuantity);

                        if (productQty.isPresent()) {
                            int val = (ii + count <= lotQuantity) ? count : ii + count - lotQuantity;
                            productQty.get().setVal(String.valueOf(val));
                        }

                        workKanban.setWorkKanbanAddInfo(JsonUtils.objectsToJson(addInfos));

                    } else {
                        workKanban.setWorkKanbanAddInfo(work.getWorkAddInfo());
                    }
                    
                    workKanbans.add(workKanban);
                }

                endTime = nextTime;
                order++;
            }

        } else {
            // グループ生産の場合
            int num = 0;

            for (WorkGroup workGroup : condition.getWorkGroups()) {
                endTime = workGroup.getStartTime();
                order = 1;
                int count = num + workGroup.getQauntity().intValue();

                // 通常工程の工程カンバン
                for (ConWorkflowWorkEntity conWork : workflow.getConWorkflowWorkCollection()) {
                    WorkEntity work = this.getWorkEntity(conWork.getWorkId(), entity.getKanbanId());
                    long taktTime = (conWork.getStandardEndTime().getTime() - conWork.getStandardStartTime().getTime()) * workGroup.getQauntity();
                    startTime = endTime;
                    Date estimatedTime = new Date(startTime.getTime() + taktTime);

                    List<BreaktimeEntity> breakInWork = BreakTimeUtils.getBreakInWork(breakTimes, startTime, estimatedTime);
                    endTime = BreakTimeUtils.getEndTimeWithBreak(breakInWork, startTime, estimatedTime);

                    for (int ii = num + 1; ii <= count; ii++) {
                        // 工程カンバン
                        WorkKanbanEntity workKanban = new WorkKanbanEntity(entity.getKanbanId(), workflow.getWorkflowId(), conWork.getWorkId(), conWork.getWorkName(), false, false,
                                conWork.getSkipFlag(), startTime, endTime, work.getTaktTime(), 0L, entity.getUpdatePersonId(), entity.getUpdateDatetime(),
                                KanbanStatusEnum.PLANNED, null, null, (order * 10000 + ii));
                        workKanban.setSerialNumber(ii);

                        // 工程の追加情報のJSON文字列を追加情報一覧に変換する。
                        List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(work.getWorkAddInfo(), AddInfoEntity[].class);

                        // 同時作業禁止
                        boolean syncWork = addInfos.stream()
                                .anyMatch(p -> Constants.DISABLE_SYNC_WORK.equals(p.getKey())
                                        && Constants.YES.equals(p.getVal()));
                        workKanban.setSyncWork(syncWork);

                        workKanban.setEquipmentCollection(conWork.getEquipmentCollection());
                        workKanban.setOrganizationCollection(conWork.getOrganizationCollection());

                        // 追加情報をセットする。
                        workKanban.setWorkKanbanAddInfo(work.getWorkAddInfo());

                        workKanbans.add(workKanban);
                    }

                    if (conWork.getSkipFlag()) {
                        endTime = startTime;
                    }

                    order++;
                }

                num += workGroup.getQauntity();
            }
        }

        // 追加工程の工程カンバン
        for (ConWorkflowWorkEntity conWork : workflow.getConWorkflowSeparateworkCollection()) {
            // 工程
            WorkEntity work = getWorkEntity(conWork.getWorkId(), entity.getKanbanId());

            // バラ工程カンバン
            WorkKanbanEntity workKanban = new WorkKanbanEntity(entity.getKanbanId(),
                    workflow.getWorkflowId(), conWork.getWorkId(), conWork.getWorkName(), true, false,
                    conWork.getSkipFlag(), conWork.getStandardStartTime(), conWork.getStandardEndTime(), work.getTaktTime(), 0L, entity.getUpdatePersonId(), entity.getUpdateDatetime(),
                    KanbanStatusEnum.PLANNED, null, null, conWork.getWorkflowOrder());

            long workTime = conWork.getStandardEndTime().getTime() - conWork.getStandardStartTime().getTime();
            workKanban.setEquipmentCollection(conWork.getEquipmentCollection());
            workKanban.setOrganizationCollection(conWork.getOrganizationCollection());

            // 追加情報をセットする。
            workKanban.setWorkKanbanAddInfo(work.getWorkAddInfo());

            workKanban.setStartDatetime(condition.getStartTime());
            workKanban.setCompDatetime(new Date(condition.getStartTime().getTime() + workTime));
            workKanbans.add(workKanban);
        }

        this.workKandanRest.addAll(workKanbans);

        Date startDate = workKanbans.stream().map(o -> o.getStartDatetime()).min(Date::compareTo).get();
        entity.setStartDatetime(startDate);

        Date compDate = workKanbans.stream().map(o -> o.getCompDatetime()).max(Date::compareTo).get();
        entity.setCompDatetime(compDate);
    }

    /**
     * 工程カンバン情報を更新する。
     *
     * @param entity カンバン情報
     * @throws URISyntaxException 
     */
    private void updateWorkKanban(KanbanEntity entity) throws URISyntaxException {

        // 通常工程
        if (Objects.nonNull(entity.getWorkKanbanCollection())) {
            // 通常工程の工程カンバン情報一覧を取得する。
            List<WorkKanbanEntity> sources = this.workKandanRest.getWorkKanbans(entity.getKanbanId(), false);

            for (WorkKanbanEntity workKanban : entity.getWorkKanbanCollection()) {
                if (!workKanban.getWorkStatus().isWorkKanbanUpdatableStatus) {
                    Optional<WorkKanbanEntity> optional = sources.stream()
                            .filter(o -> Objects.equals(o.getWorkKanbanId(), workKanban.getWorkKanbanId()))
                            .findFirst();
                    if (optional.isPresent()) {
                        WorkKanbanEntity source = optional.get();
                        workKanban.setActualStartTime(source.getActualStartTime());
                        workKanban.setActualCompTime(source.getActualCompTime());
                    }

                    // 実施フラグをONにする。
                    workKanban.setImplementFlag(true);
                }
            }
        }

        // 追加工程
        if (Objects.nonNull(entity.getSeparateworkKanbanCollection())) {
            // 追加工程の工程カンバン情報一覧を取得する。
            List<WorkKanbanEntity> sources = this.workKandanRest.getWorkKanbans(entity.getKanbanId(), true);
           
            entity.getSeparateworkKanbanCollection().sort((a, b) -> {
                return a.getStartDatetime().compareTo(b.getStartDatetime()); 
            });
            
            for (WorkKanbanEntity workKanban : entity.getSeparateworkKanbanCollection()) {
                if (!workKanban.getWorkStatus().isWorkKanbanUpdatableStatus) {
                    Optional<WorkKanbanEntity> optional = sources.stream()
                            .filter(o -> Objects.equals(o.getWorkKanbanId(), workKanban.getWorkKanbanId()))
                            .findFirst();
                    if (optional.isPresent()) {
                        WorkKanbanEntity source = optional.get();
                        workKanban.setActualStartTime(source.getActualStartTime());
                        workKanban.setActualCompTime(source.getActualCompTime());
                    }

                    // 実施フラグをONにする。
                    workKanban.setImplementFlag(true);
                }
            }
        }

        List<WorkKanbanEntity> workKanbans = Stream.of(entity.getWorkKanbanCollection(),entity.getSeparateworkKanbanCollection()).flatMap(Collection::stream).collect(toList());

        // 作業中以外の工程カンバン作業者リストを消去しておく.
        workKanbans
                .stream()
                .filter(workKanban-> !KanbanStatusEnum.WORKING.equals(workKanban.getWorkStatus()))
                .map(WorkKanbanEntity::getWorkKanbanId)
                .filter(Objects::nonNull)
                .forEach(this.workKanbanWorkingRest::deleteWorking);

        // 並列工程の行数
        AtomicInteger rows = new AtomicInteger(workKanbans
                .stream()
                .mapToInt(workKanban->workKanban.getWorkKanbanOrder() % 10000)
                .max()
                .orElse(0));

        workKanbans
                .stream()
                .filter(workKanban->workKanban.getWorkKanbanOrder()==0)
                .forEach(workKanban->workKanban.setWorkKanbanOrder(rows.incrementAndGet()));

        // 工程カンバン情報を更新する。
        this.workKandanRest.updateAll(entity.getKanbanId(), workKanbans);
    }

    /**
     * 工程カンバン情報を削除する。
     *
     * @param entity カンバン情報
     */
    private void removeWorkKanban(KanbanEntity entity) {
        this.workKandanRest.removeAll(entity.getWorkKanbanCollection());
        this.workKandanRest.removeAll(entity.getSeparateworkKanbanCollection());
    }

    /**
     * 工程カンバンが更新可能なステータスか確認する。
     *
     * @param entity カンバン情報
     * @return 更新可能なステータスか (true:更新可能, false:更新不可)
     */
    @Lock(LockType.READ)
    private static boolean checkStatusWorkKanban(KanbanEntity entity) {
        // 作業中の通常工程を検索する。
        if (Objects.nonNull(entity.getWorkKanbanCollection())) {
            boolean isExists = entity.getWorkKanbanCollection().stream()
                    .anyMatch(p -> KanbanStatusEnum.WORKING.equals(p.getWorkStatus()));
            if (isExists) {
                return false;
            }
        }

        // 作業中の追加工程を検索する。
        if (Objects.nonNull(entity.getSeparateworkKanbanCollection())) {
            boolean isExists = entity.getSeparateworkKanbanCollection().stream()
                    .anyMatch(p -> KanbanStatusEnum.WORKING.equals(p.getWorkStatus()));
            if (isExists) {
                return false;
            }
        }

        return true;
    }

    /**
     * 検索条件でカンバン情報を検索するクエリを取得する。
     *
     * @param type 検索種別
     * @param condition 検索条件
     * @return 検索クエリ
     */
    @Lock(LockType.READ)
    private Query getSearchQuery(SearchType type, KanbanSearchCondition condition) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<KanbanEntity> poolKanban = cq.from(KanbanEntity.class);

        jakarta.persistence.criteria.Path<Long> pathKanbanId = poolKanban.get(KanbanEntity_.kanbanId);
        jakarta.persistence.criteria.Path<String> pathKanbanName = poolKanban.get(KanbanEntity_.kanbanName);
        jakarta.persistence.criteria.Path<String> pathKanbanSubname = poolKanban.get(KanbanEntity_.kanbanSubname);
        jakarta.persistence.criteria.Path<String> pathModelName = poolKanban.get(KanbanEntity_.modelName);
        jakarta.persistence.criteria.Path<String> pathProductionNumber = poolKanban.get(KanbanEntity_.productionNumber);

        jakarta.persistence.criteria.Path<Long> pathWorkflowId = poolKanban.get(KanbanEntity_.workflowId);

        jakarta.persistence.criteria.Path<KanbanStatusEnum> pathKanbanStatus = poolKanban.get(KanbanEntity_.kanbanStatus);

        jakarta.persistence.criteria.Path<Date> pathStartDatetime = poolKanban.get(KanbanEntity_.startDatetime);
        jakarta.persistence.criteria.Path<Date> pathCompDatetime = poolKanban.get(KanbanEntity_.compDatetime);

        jakarta.persistence.criteria.Path<Date> pathActualStartTime = poolKanban.get(KanbanEntity_.actualStartTime);
        jakarta.persistence.criteria.Path<Date> pathActualCompTime = poolKanban.get(KanbanEntity_.actualCompTime);

        // 検索条件
        List<Predicate> where = new ArrayList();

        // 検索条件で、設備ID一覧の指定があり、親設備フラグが有効な場合、設備ID一覧に親設備のIDを追加する。
        HashSet<Long> equipmentIdCollection = new HashSet();
        if (Objects.nonNull(condition.getEquipmentCollection())) {
            equipmentIdCollection.addAll(condition.getEquipmentCollection());
            if (Objects.nonNull(condition.getEquipmentIdWithParent()) && condition.getEquipmentIdWithParent()) {
                for (Long id : condition.getEquipmentCollection()) {
                    equipmentIdCollection.addAll(this.equipmentRest.getEquipmentPerpetuity(id));
                }
            }
        }

        // 検索条件で、組織ID一覧の指定があり、親組織フラグが有効な場合、組織ID一覧に親組織のIDを追加する。
        HashSet<Long> organizationIdCollection = new HashSet();
        if (Objects.nonNull(condition.getOrganizationCollection())) {
            organizationIdCollection.addAll(condition.getOrganizationCollection());
            if (Objects.nonNull(condition.getOrganizationIdWithParent()) && condition.getOrganizationIdWithParent()) {
                for (Long id : condition.getOrganizationCollection()) {
                    organizationIdCollection.addAll(this.organizationRest.getOrganizationPerpetuity(id));
                }
            }
        }

        // 階層ID
        if (Objects.nonNull(condition.getHierarchyId())) {
            Root<ConKanbanHierarchyEntity> subCon = cq.from(ConKanbanHierarchyEntity.class);

            jakarta.persistence.criteria.Path<Long> pathConWorkKanId = subCon.get(ConKanbanHierarchyEntity_.kanbanId);
            jakarta.persistence.criteria.Path<Long> pathConHierarchyId = subCon.get(ConKanbanHierarchyEntity_.kanbanHierarchyId);

            Subquery<Long> conSubquery = cq.subquery(Long.class);

            conSubquery.select(pathConWorkKanId)
                    .where(cb.equal(pathConHierarchyId, condition.getHierarchyId()));

            where.add(pathKanbanId.in(conSubquery));
        }

        // カンバンID
        if (Objects.nonNull(condition.getKanbanId())) {
            where.add(cb.equal(pathKanbanId, condition.getKanbanId()));
        }

        // カンバン名
        if (Objects.nonNull(condition.getKanbanName())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathKanbanName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(condition.getKanbanName())) + "%"),
                    cb.like(pathKanbanName, "%" + StringUtils.escapeLikeChar(condition.getKanbanName()) + "%")
            ));
        }

        // サブカンバン名
        if (Objects.nonNull(condition.getKanbanSubname())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathKanbanSubname), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(condition.getKanbanSubname())) + "%"),
                    cb.like(pathKanbanSubname, "%" + StringUtils.escapeLikeChar(condition.getKanbanSubname()) + "%")
            ));
        }

        // モデル名
        if (Objects.nonNull(condition.getModelName())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathModelName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(condition.getModelName())) + "%"),
                    cb.like(pathModelName, "%" + StringUtils.escapeLikeChar(condition.getModelName()) + "%")
            ));        }

        // 製造番号
        if (Objects.nonNull(condition.getProductionNumber())) {
            if (MatchTypeEnum.MATCH.equals(condition.getMatchType())) {
                where.add(cb.equal(pathProductionNumber, condition.getProductionNumber()));
                
            } else {
                where.add(cb.or(
                        cb.like(cb.lower(pathProductionNumber), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(condition.getProductionNumber())) + "%"),
                        cb.like(pathProductionNumber, "%" + StringUtils.escapeLikeChar(condition.getProductionNumber()) + "%")
                ));
            }
        }
        
        // 工程順ID
        if (Objects.nonNull(condition.getWorkflowIdCollection())) {
            where.add(pathWorkflowId.in(condition.getWorkflowIdCollection()));
        }

        // カンバンステータス
        if (Objects.nonNull(condition.getKanbanStatusCollection())) {
            where.add(pathKanbanStatus.in(condition.getKanbanStatusCollection()));
        }

        // 工程カンバン
        if (!equipmentIdCollection.isEmpty()
                || Objects.nonNull(condition.getEquipmentNameCollection())
                || !organizationIdCollection.isEmpty()
                || Objects.nonNull(condition.getOrganizationNameCollection())) {
            Root<WorkKanbanEntity> subWorkKan = cq.from(WorkKanbanEntity.class);

            jakarta.persistence.criteria.Path<Long> pathKanId = subWorkKan.get(WorkKanbanEntity_.kanbanId);
            jakarta.persistence.criteria.Path<Long> pathWorkKanId = subWorkKan.get(WorkKanbanEntity_.workKanbanId);

            Subquery<Long> wkanSubquery = cq.subquery(Long.class);

            List<Predicate> subWhere = new ArrayList();

            // 設備
            if (!equipmentIdCollection.isEmpty()
                    || Objects.nonNull(condition.getEquipmentNameCollection())) {
                // 工程カンバン・設備関連付け情報
                Root<ConWorkkanbanEquipmentEntity> subCon = cq.from(ConWorkkanbanEquipmentEntity.class);

                jakarta.persistence.criteria.Path<Long> pathConWorkKanId = subCon.get(ConWorkkanbanEquipmentEntity_.workKanbanId);
                jakarta.persistence.criteria.Path<Long> pathConEquId = subCon.get(ConWorkkanbanEquipmentEntity_.equipmentId);

                Subquery<Long> conSubquery = cq.subquery(Long.class);

                List<Predicate> conSubWhere = new ArrayList();

                // 設備ID
                if (!equipmentIdCollection.isEmpty()) {
                    conSubWhere.add(pathConEquId.in(equipmentIdCollection));
                }

                // 設備名または設備識別名
                if (Objects.nonNull(condition.getEquipmentNameCollection())) {
                    Root<EquipmentEntity> subEqu = cq.from(EquipmentEntity.class);

                    jakarta.persistence.criteria.Path<Long> pathId = subEqu.get(EquipmentEntity_.equipmentId);
                    jakarta.persistence.criteria.Path<String> pathName = subEqu.get(EquipmentEntity_.equipmentName);
                    jakarta.persistence.criteria.Path<String> pathIdent = subEqu.get(EquipmentEntity_.equipmentIdentify);

                    Subquery<Long> equSubquery = cq.subquery(Long.class);

                    List<Predicate> equSubWhere = new ArrayList();

                    for (String name : condition.getEquipmentNameCollection()) {
                        equSubWhere.add(cb.like(cb.lower(pathName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                        equSubWhere.add(cb.like(pathName, "%" + StringUtils.escapeLikeChar(name) + "%"));
                        equSubWhere.add(cb.like(cb.lower(pathIdent), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                        equSubWhere.add(cb.like(pathIdent, "%" + StringUtils.escapeLikeChar(name) + "%"));
                    }

                    equSubquery.select(pathId)
                            .where(cb.or(equSubWhere.toArray(new Predicate[equSubWhere.size()])));

                    conSubWhere.add(pathConEquId.in(equSubquery));
                }

                conSubquery.select(pathConWorkKanId)
                        .where(cb.and(conSubWhere.toArray(new Predicate[conSubWhere.size()])));

                subWhere.add(pathWorkKanId.in(conSubquery));
            }

            // 組織
            if (!organizationIdCollection.isEmpty()
                    || Objects.nonNull(condition.getOrganizationNameCollection())) {
                // 工程カンバン・組織関連付け情報
                Root<ConWorkkanbanOrganizationEntity> subCon = cq.from(ConWorkkanbanOrganizationEntity.class);

                jakarta.persistence.criteria.Path<Long> pathConWorkKanId = subCon.get(ConWorkkanbanOrganizationEntity_.workKanbanId);
                jakarta.persistence.criteria.Path<Long> pathConOrgId = subCon.get(ConWorkkanbanOrganizationEntity_.organizationId);

                Subquery<Long> conSubquery = cq.subquery(Long.class);

                List<Predicate> conSubWhere = new ArrayList();

                // 組織ID
                if (!organizationIdCollection.isEmpty()) {
                    conSubWhere.add(pathConOrgId.in(organizationIdCollection));
                }

                // 組織名または組織識別名
                if (Objects.nonNull(condition.getOrganizationNameCollection())) {
                    Root<OrganizationEntity> subOrg = cq.from(OrganizationEntity.class);

                    jakarta.persistence.criteria.Path<Long> pathId = subOrg.get(OrganizationEntity_.organizationId);
                    jakarta.persistence.criteria.Path<String> pathName = subOrg.get(OrganizationEntity_.organizationName);
                    jakarta.persistence.criteria.Path<String> pathIdent = subOrg.get(OrganizationEntity_.organizationIdentify);

                    Subquery<Long> orgSubquery = cq.subquery(Long.class);

                    List<Predicate> orgSubWhere = new ArrayList();

                    for (String name : condition.getOrganizationNameCollection()) {
                        orgSubWhere.add(cb.like(cb.lower(pathName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                        orgSubWhere.add(cb.like(pathName, "%" + StringUtils.escapeLikeChar(name) + "%"));
                        orgSubWhere.add(cb.like(cb.lower(pathIdent), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                        orgSubWhere.add(cb.like(pathIdent, "%" + StringUtils.escapeLikeChar(name) + "%"));
                    }

                    orgSubquery.select(pathId)
                            .where(cb.or(orgSubWhere.toArray(new Predicate[orgSubWhere.size()])));

                    conSubWhere.add(pathConOrgId.in(orgSubquery));
                }

                conSubquery.select(pathConWorkKanId)
                        .where(cb.and(conSubWhere.toArray(new Predicate[conSubWhere.size()])));

                subWhere.add(pathWorkKanId.in(conSubquery));
            }

            wkanSubquery.select(pathKanId)
                    .where(cb.and(subWhere.toArray(new Predicate[subWhere.size()])));

            where.add(pathKanbanId.in(wkanSubquery));
        }

        // 計画日時
        if (Objects.nonNull(condition.getFromDate())) {
            where.add(cb.or(
                    cb.greaterThanOrEqualTo(pathStartDatetime, condition.getFromDate()),
                    cb.greaterThanOrEqualTo(pathCompDatetime, condition.getFromDate())
            ));
        }
        if (Objects.nonNull(condition.getToDate())) {
            where.add(cb.or(
                    cb.lessThanOrEqualTo(pathStartDatetime, condition.getToDate()),
                    cb.lessThanOrEqualTo(pathCompDatetime, condition.getToDate())
            ));
        }

        if (Objects.isNull(condition.getSearchMode()) || 0 == condition.getSearchMode()) {
            // 完了日時
            if (Objects.nonNull(condition.getFromActualCompTime())) {
                where.add(cb.greaterThanOrEqualTo(pathActualCompTime, condition.getFromActualCompTime()));
            }

            if (Objects.nonNull(condition.getToActualCompTime())) {
                where.add(cb.lessThanOrEqualTo(pathActualCompTime, condition.getToActualCompTime()));
            }

        } else if (1 == condition.getSearchMode()) {
            // 作業期間(実績開始日時・実績完了日時)が重なるカンバンを抽出
            if (Objects.nonNull(condition.getFromActualStartTime())
                    && Objects.nonNull(condition.getToActualCompTime())) {

                //SELECT
                //    v.kanban_id
                //    , v.kanban_name
                //    , v.kanban_status
                //    , v.actual_start_datetime
                //    , v.actual_comp_datetime 
                //FROM
                //    trn_kanban v
                //WHERE
                //    ( 
                //        v.actual_start_datetime <= to_timestamp('2024/11/10 23:59:59', 'YYYY/MM/DD HH24:MI:SS') 
                //        AND v.actual_comp_datetime >= to_timestamp('2024/10/25 00:00:00', 'YYYY/MM/DD HH24:MI:SS')
                //    ) 
                //    OR ( 
                //        v.actual_comp_datetime IS NULL 
                //        AND v.actual_start_datetime <= to_timestamp('2024/11/10 23:59:59', 'YYYY/MM/DD HH24:MI:SS') 
                //        AND CURRENT_TIMESTAMP >= to_timestamp('2024/10/25 00:00:00', 'YYYY/MM/DD HH24:MI:SS')
                //    );

                where.add(cb.or(
                    cb.and(
                        // 実績開始日時 <= 検索条件の終了日時 AND 実績完了日時 >= 検索条件の開始日時 
                        cb.isNotNull(pathActualStartTime),
                        cb.isNotNull(pathActualCompTime),
                        cb.lessThanOrEqualTo(pathActualStartTime, condition.getToActualCompTime()),
                        cb.greaterThanOrEqualTo(pathActualCompTime, condition.getFromActualStartTime())
                        ),
                    cb.and(
                        cb.isNotNull(pathActualStartTime),
                        cb.isNull(pathActualCompTime),
                        cb.lessThanOrEqualTo(pathActualStartTime, condition.getToActualCompTime()),
                        cb.greaterThanOrEqualTo(cb.currentTimestamp(), condition.getFromActualStartTime())
                        )
                    )
                );

            } else if (Objects.nonNull(condition.getFromActualStartTime())) {
                // 検索条件の終了日時なし

                //WHERE
                //    v.actual_start_datetime >= to_timestamp('2024/11/16 00:00:00', 'YYYY/MM/DD HH24:MI:SS')
                //    OR ( 
                //        v.actual_comp_datetime IS NULL 
                //        AND v.actual_start_datetime <= to_timestamp('2024/11/16 00:00:00', 'YYYY/MM/DD HH24:MI:SS') 
                //    );

                where.add(cb.and(
                        cb.isNotNull(pathActualStartTime),
                        cb.or(
                            cb.greaterThanOrEqualTo(pathActualStartTime, condition.getFromActualStartTime()),
                            cb.and(
                                cb.isNull(pathActualCompTime),
                                cb.lessThanOrEqualTo(pathActualStartTime, condition.getFromActualStartTime())
                                ),
                            cb.and(
                                cb.isNotNull(pathActualCompTime),
                                cb.greaterThanOrEqualTo(pathActualCompTime, condition.getFromActualStartTime())
                                )
                            )
                    )
                );

            } else if (Objects.nonNull(condition.getToActualCompTime())) {
                // 検索条件の開始日時なし

                where.add(
                    cb.and(
                        cb.isNotNull(pathActualStartTime),
                        cb.lessThanOrEqualTo(pathActualStartTime, condition.getToActualCompTime())
                    )
                );

            }
        }

        // 実績
        if (Objects.nonNull(condition.getFromActualDate())
                || Objects.nonNull(condition.getToActualDate())
                || Objects.nonNull(condition.getActualOrganizationNameCollection())) {
            Root<ActualResultEntity> subAct = cq.from(ActualResultEntity.class);

            jakarta.persistence.criteria.Path<Long> pathActKanId = subAct.get(ActualResultEntity_.kanbanId);
            jakarta.persistence.criteria.Path<Date> pathActDt = subAct.get(ActualResultEntity_.implementDatetime);
            jakarta.persistence.criteria.Path<Long> pathActOrgId = subAct.get(ActualResultEntity_.organizationId);

            Subquery<Long> actSubquery = cq.subquery(Long.class);

            List<Predicate> subWhere = new ArrayList();
            
            // 実績日時
            if (Objects.nonNull(condition.getFromActualDate())) {
                subWhere.add(cb.greaterThanOrEqualTo(pathActDt, condition.getFromActualDate()));
            }
            
            if (Objects.nonNull(condition.getToActualDate())) {
                subWhere.add(cb.lessThanOrEqualTo(pathActDt, condition.getToActualDate()));
            }

            // 実績の組織名
            if (Objects.nonNull(condition.getActualOrganizationNameCollection())) {
                Root<OrganizationEntity> subOrg = cq.from(OrganizationEntity.class);

                jakarta.persistence.criteria.Path<Long> pathId = subOrg.get(OrganizationEntity_.organizationId);
                jakarta.persistence.criteria.Path<String> pathName = subOrg.get(OrganizationEntity_.organizationName);
                //jakarta.persistence.criteria.Path<String> pathIdent = subOrg.get(OrganizationEntity_.organizationIdentify);

                Subquery<Long> orgSubquery = cq.subquery(Long.class);

                List<Predicate> orgSubWhere = new ArrayList();

                for (String name : condition.getActualOrganizationNameCollection()) {
                    orgSubWhere.add(cb.like(cb.lower(pathName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                    orgSubWhere.add(cb.like(pathName, "%" + StringUtils.escapeLikeChar(name) + "%"));
                    //orgSubWhere.add(cb.like(cb.lower(pathIdent), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                    //orgSubWhere.add(cb.like(pathIdent, "%" + StringUtils.escapeLikeChar(name) + "%"));
                }

                orgSubquery.select(pathId)
                        .where(cb.or(orgSubWhere.toArray(new Predicate[orgSubWhere.size()])));

                subWhere.add(pathActOrgId.in(orgSubquery));
            }

            actSubquery.select(pathActKanId)
                    .where(cb.and(subWhere.toArray(new Predicate[subWhere.size()])));

            where.add(pathKanbanId.in(actSubquery));
        }

        if (SearchType.COUNT.equals(type)) {
            cq.select(cb.count(pathKanbanId))
                    .where(cb.and(where.toArray(new Predicate[where.size()])));
        } else {
            cq.select(poolKanban)
                    .where(cb.and(where.toArray(new Predicate[where.size()])))
                    .orderBy(cb.asc(pathStartDatetime), cb.asc(pathKanbanId));
        }

        return this.em.createQuery(cq);
    }

    /**
     * 工程実績を登録する。
     *
     * @param report 工程実績情報
     * @param isCheckWorking 二重作業の判定 (true：する, false：しない)
     * @param authId 認証ID
     * @return 工程実績登録結果
     */
    @POST
    @Path("report")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ActualProductReportResult report(ActualProductReportEntity report, @QueryParam("checkWorking") Boolean isCheckWorking, @QueryParam("authId") Long authId) {
        logger.info("report: {}, isCheckWorking={}, authId={}", report, isCheckWorking, authId);
        try{
            return this.doReport(report, isCheckWorking, false, authId);
        } finally {
            logger.info("report end.");
        }
    }

    /**
     * [Lite] 工程実績を登録する。
     *
     * @param report 工程実績情報
     * @param authId 認証ID
     * @return 工程実績登録結果
     */
    @POST
    @Path("lite/report")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ActualProductReportResult reportLite(ActualProductReportEntity report, @QueryParam("authId") Long authId) {
        logger.info("reportLite: {}, authId={}", report, authId);

        if (report.getStatus() == KanbanStatusEnum.WORKING) {
            if (!StringUtils.isEmpty(report.getServiceInfo())) {
                KanbanCode kanbanCode = KanbanCode.lookup(report.getServiceInfo());
                if (Objects.nonNull(kanbanCode)) {
                    // カンバン登録
                    KanbanEntity kanban = this.findByName(kanbanCode.getKanbanName(), kanbanCode.getWorkflowName(), null, authId);
                    if (Objects.isNull(kanban.getKanbanId())) {

                        Response res = this.createKanban(kanbanCode.getKanbanName(), kanbanCode.getWorkflowName(), kanbanCode.getHierarchyName(), "admin", null, null);
                        ResponseEntity response = (ResponseEntity) res.getEntity();
                        logger.info("Created kanban: {}",  response);
                        
                        if (!response.isSuccess()) {
                            return new ActualProductReportResult(response.getErrorType(), ActrualResultRuntimeData.getInstance().forwardTransactionId(report));
                        }
                        
                        String[] value = response.getUri().split("/");
                        Long kanbanId = Long.parseLong(value[value.length - 1]);

                        kanban = this.find(kanbanId, authId);
                        
                        List<AddInfoEntity> kanbanProps = JsonUtils.jsonToObjects(kanban.getKanbanAddInfo(), AddInfoEntity[].class);
                        kanbanProps.add(new AddInfoEntity("品目", CustomPropertyTypeEnum.TYPE_STRING, kanbanCode.getProductNo(), kanbanProps.size() + 1, null));
                        kanbanProps.add(new AddInfoEntity("納期", CustomPropertyTypeEnum.TYPE_DATE, kanbanCode.getDueDate(), kanbanProps.size() + 1, null));

                        kanban.setModelName(kanbanCode.getModelName());
                        kanban.setProductionNumber(kanbanCode.getOrderNo());
                        kanban.setKanbanAddInfo(JsonUtils.objectsToJson(kanbanProps));

                        this.edit(kanban);
                        this.em.flush();
                    }

                    report.setKanbanId(kanban.getKanbanId());

                    kanban.getWorkKanbanCollection().stream()
                            .filter(o -> StringUtils.equals(o.getWorkName(), kanbanCode.getWorkName()))
                            .findFirst()
                            .ifPresent(o -> report.setWorkKanbanId(o.getWorkKanbanId()));
                }
            }
           
            // 既に作業中の場合、許可しない
            //ActualSearchCondition actualCondition = new ActualSearchCondition()
            //    .organizationList(Arrays.asList(report.getOrganizationId()))
            //    .kanbanId(report.getKanbanId())
            //    .resultDailyEnum(ActualResultDailyEnum.ALL);
            //
            //ActualResultEntity lastActual = this.actualResultRest.findLastActualResult(actualCondition, null);
            //if (Objects.nonNull(lastActual) 
            //        && (lastActual.getActualStatus() == KanbanStatusEnum.WORKING || lastActual.getActualStatus() == KanbanStatusEnum.OTHER)) {
            //    logger.warn("Not allowed to working: " + lastActual.toString());
            //    return new ActualProductReportResult(ServerErrorTypeEnum.ALREADY_WORKING_ORGANIZATION, ActrualResultRuntimeData.getInstance().forwardTransactionId(report)).details(lastActual.getEquipmentName());
            //}

            OperationSerachCondition operationCondition = new OperationSerachCondition(null, report.getOrganizationId(), OperateAppEnum.ADPRODUCTLITE, OperationTypeEnum.INDIRECT_WORK);
            OperationEntity lastOperation = this.operationRest.getLastOperation(operationCondition, report.getOrganizationId());
            if (Objects.nonNull(lastOperation)
                    && Objects.nonNull(lastOperation.getAddInfo())
                    && Objects.nonNull(lastOperation.getAddInfo().getIndirectWork())
                    && Objects.nonNull(lastOperation.getAddInfo().getIndirectWork().getDoIndirect())
                    && lastOperation.getAddInfo().getIndirectWork().getDoIndirect()) {
                logger.warn("Not allowed to working: " + lastOperation);
                EquipmentEntity equipment = this.equipmentRest.find(lastOperation.getEquipmentId(), authId);
                return new ActualProductReportResult(ServerErrorTypeEnum.ALREADY_WORKING_ORGANIZATION, ActrualResultRuntimeData.getInstance().forwardTransactionId(report)).details(equipment.getEquipmentName());
            }
        }
        
        return this.doReport(report, false, true, authId);
    }



    /**
     * 工程実績を登録する。
     *
     * @param report 工程実績情報
     * @param isCheckWorking 二重作業の判定 (true：する, false：しない)
     * @param authId 認証ID
     * @return 工程実績登録結果
     */
    @POST
    @Path("multi-report")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ActualProductReportResult multireport(ActualProductReportEntity report, @QueryParam("checkWorking") Boolean isCheckWorking, @QueryParam("authId") Long authId) {
        logger.info("report: {}, isCheckWorking={}, authId={}", report, isCheckWorking, authId);
        try{
            return this.doMultiReport(report, isCheckWorking, authId);
        } finally {
            logger.info("report end.");
        }
    }

    /**
     * 工程実績を登録する。
     *
     * @param report 工程実績情報
     * @param isCheckWorking 二重作業の判定 (true：する, false：しない)
     * @param authId 認証ID
     * @return 工程実績登録結果
     */
    public ActualProductReportResult doMultiReport(ActualProductReportEntity report, Boolean isCheckWorking, Long authId) {
        logger.info("doMultiReport: {}, isCheckWorking={}, authId={}", report, isCheckWorking, authId);

        ActrualResultRuntimeData runtimeData = ActrualResultRuntimeData.getInstance();
        
        try {
            if (!runtimeData.checkTransactionId(report)) {
                //すでに実績を受け取っているので無視する.
                logger.info("Invalid report: {}", report);
                return new ActualProductReportResult(ServerErrorTypeEnum.SUCCESS, runtimeData.getNextTransactionId(report));
            }

            // 二重作業の判定が有効で、通知された実績のステータスが「作業中」の場合、二重作業の判定を行なう。
            if (Objects.nonNull(isCheckWorking) && isCheckWorking
                    && KanbanStatusEnum.WORKING.equals(report.getStatus())) {
                // 他の設備で作業中の組織の場合、「他端末で作業中の組織」のエラーを返す。
                if (this.checkWorking(report.getEquipmentId(), report.getOrganizationId(), report.getReportDatetime())) {
                    return new ActualProductReportResult(ServerErrorTypeEnum.ALREADY_WORKING_ORGANIZATION, runtimeData.forwardTransactionId(report));
                }
            }

            if(report.getWorkKanbanCollection().isEmpty()) {
                logger.fatal("workKanbanCollection is Empty");
                return new ActualProductReportResult(ServerErrorTypeEnum.NOTFOUND_WORKKANBAN, runtimeData.forwardTransactionId(report), new ArrayList<>());
            }

            // 工程カンバン一覧取得
            List<WorkKanbanEntity> workKanbanEntities = this.workKandanRest.findByWorkKanbanId(report.getWorkKanbanCollection(), authId);

            // 工程カンバンが無い物がある場合は異常
            if (report.getWorkKanbanCollection().size() != workKanbanEntities.size()) {
                    logger.fatal("Not found workKanban: {}", report);
                    return new ActualProductReportResult(ServerErrorTypeEnum.NOTFOUND_WORKKANBAN, runtimeData.forwardTransactionId(report), new ArrayList<>());
            }

            final KanbanStatusEnum kanbanStatus = report.getStatus();

            // 中断・完了は作業中のカンバンのみを対象とする
            if (KanbanStatusEnum.SUSPEND.equals(kanbanStatus)
            ||  KanbanStatusEnum.COMPLETION.equals(kanbanStatus)) {
                workKanbanEntities =
                        workKanbanEntities
                                .stream()
                                .filter(workKanbanEntity -> KanbanStatusEnum.WORKING.equals(workKanbanEntity.getWorkStatus()))
                                .filter(workKanbanEntity -> this.workKanbanWorkingRest.countWorking(workKanbanEntity.getWorkKanbanId()) > 0)
                                .collect(toList());
            }

            // 工程カンバンが無い物がある場合は異常
            if (workKanbanEntities.isEmpty()) {
                logger.fatal("Not found working workKanban: {}", report);
                return new ActualProductReportResult(ServerErrorTypeEnum.NOTFOUND_WORKINGWORK, runtimeData.forwardTransactionId(report), new ArrayList<>());
            }


            // 応援者が作業中の場合、完了・中止・不良は不可
            if (Objects.isNull(report.isSupportMode()) || !report.isSupportMode()) {
                if (KanbanStatusEnum.SUSPEND.equals(kanbanStatus)
                        || KanbanStatusEnum.COMPLETION.equals(kanbanStatus)
                        || KanbanStatusEnum.DEFECT.equals(kanbanStatus)) {
                    if ( workKanbanEntities
                            .stream()
                            .map(workKanbanEntity-> workKanbanWorkingRest.getSupporterNumber((workKanbanEntity.getWorkKanbanId())))
                            .anyMatch(supporterNumber->supporterNumber > 0)) {
                        return new ActualProductReportResult(ServerErrorTypeEnum.THERE_SUPPORT_WORKING, runtimeData.forwardTransactionId(report));
                    }
                }
            }

            if (Objects.nonNull(report.isAllowSupportWork()) && !report.isAllowSupportWork()) {
                if (KanbanStatusEnum.WORKING.equals(kanbanStatus)) {
                    if ( workKanbanEntities
                            .stream()
                            .map(workKanbanEntity-> workKanbanWorkingRest.countWorking((workKanbanEntity.getWorkKanbanId())))
                            .anyMatch(supporterNumber->supporterNumber > 0)) {
                        return new ActualProductReportResult(ServerErrorTypeEnum.THERE_WORKING_NON_START, runtimeData.forwardTransactionId(report));
                    }
                }
            }


            // カンバン一覧取得
            final List<Long> kanbanIds =
                    workKanbanEntities
                            .stream()
                            .map(WorkKanbanEntity::getKanbanId)
                            .distinct()
                            .collect(toList());
            List<KanbanEntity> kanbanEntities = this.findByKanbanId(kanbanIds, false, authId);

            // カンバンが無い物がある場合は異常
            if (kanbanIds.size()!=kanbanEntities.size()) {
                logger.fatal("Not found kanban: {}", report);
                return new ActualProductReportResult(ServerErrorTypeEnum.NOTFOUND_KANBAN, runtimeData.forwardTransactionId(report));
            }

            // 工程順一覧を取得
            List<Long> workflowIds =
                    kanbanEntities
                            .stream()
                            .map(KanbanEntity::getWorkflowId)
                            .distinct()
                            .collect(toList());
            List<WorkflowEntity> workflowEntities =  this.workflowRest.find(workflowIds, authId);

            // 工程順一覧が無い場合は異常
            if (workflowIds.size() != workflowEntities.size()) {
                logger.fatal("Not found workflow: {}", report);
                return new ActualProductReportResult(ServerErrorTypeEnum.NOTFOUND_WORKFLOW, runtimeData.forwardTransactionId(report));
            }

            // 工程順Mapを作成
            Map<Long, WorkflowEntity> workflowEntityMap =
                    workflowEntities
                            .stream()
                            .collect(toMap(WorkflowEntity::getWorkflowId, Function.identity()));

            // 工程順の情報を設定
            kanbanEntities.forEach(kanbanEntity -> {
                WorkflowEntity workflowEntity = workflowEntityMap.get(kanbanEntity.getWorkflowId());
                kanbanEntity.setWorkflow(workflowEntity);
                kanbanEntity.setWorkflowName(workflowEntity.getWorkflowName());
                kanbanEntity.setWorkflowRev(workflowEntity.getWorkflowRev());
            });

            // カンバン情報にカンバン階層IDがない場合、取得する。
            kanbanEntities
                    .stream()
                    .filter(kanbanEntity->Objects.isNull(kanbanEntity.getParentId()))
                    .forEach(kanbanEntity -> {
                        Long parentId = this.findParentId(kanbanEntity.getKanbanId());
                        kanbanEntity.setParentId(parentId);
                    });

            // スケジュールなしの場合 又は、開始時間の未設定の場合、開始時間を更新
            if (KanbanStatusEnum.WORKING.equals(report.getStatus())
                    && ServiceTypeEnum.GENERIC.equals(report.getServiceType())) {

                final boolean isNoSchedule =
                        !LicenseManager.getInstance().isLicenceOption(LicenseOptionType.Scheduling.getName())
                        || !report.getIsSchedule();
                
                kanbanEntities
                        .stream()
                        .filter(kanbanEntity -> isNoSchedule || Objects.isNull(kanbanEntity.getStartDatetime()))
                        .filter(kanbanEntity-> KanbanStatusEnum.PLANNED.equals(kanbanEntity.getKanbanStatus()))
                        .forEach(kanbanEntity -> {
                            logger.info("No schedule : {}" ,kanbanEntity.getKanbanName());
                            this.updateBaseTime(kanbanEntity, kanbanEntity.getWorkflow(), report.getReportDatetime());
                        });
            }
            
            // カンバンMapを作成
            final Map<Long, KanbanEntity> kanbanEntityMap =
                    kanbanEntities
                            .stream()
                            .collect(toMap(KanbanEntity::getKanbanId, Function.identity()));

            // 作業を開始できるかチェックする。
            if (KanbanStatusEnum.WORKING.equals(kanbanStatus)) {
                Optional<ServerErrorTypeEnum> serverErrorTypeEnum =
                        workKanbanEntities
                                .stream()
                                .map(workKanbanEntity -> this.check(report, workKanbanEntity, kanbanEntityMap.get(workKanbanEntity.getKanbanId())))
                                .filter(error -> !ServerErrorTypeEnum.SUCCESS.equals(error))
                                .findFirst();
                if (serverErrorTypeEnum.isPresent()) {
                    return new ActualProductReportResult(serverErrorTypeEnum.get(), runtimeData.forwardTransactionId(report));
                }
            }

            Date reportDate = null;
            Long workTime = null;
            MultiWork multiWork = MultiWork.lookup(report.getServiceInfo());
            if ((KanbanStatusEnum.SUSPEND.equals(report.getStatus()) || KanbanStatusEnum.COMPLETION.equals(report.getStatus()))
                    && workKanbanEntities.size() > 1 ) {
                // 複数の作業をまとめて着完の場合、作業時間をシフトする
                if (MultiWork.MultiWorkType.Sequential.equals(multiWork.getMultiWorkType())) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sssX");
                    reportDate = df.parse(multiWork.getStartDate());

                    LocalDateTime begin = DateUtils.toLocalDateTime(reportDate);
                    LocalDateTime end = DateUtils.toLocalDateTime(report.getReportDatetime());
                    Duration duration = Duration.between(begin, end);
                    workTime = duration.toMillis() / workKanbanEntities.size();
                }
            }

            // 同時並行作業数
            int simultaneousNum
                    = MultiWork.MultiWorkType.Simultaneous.equals(multiWork.getMultiWorkType())
                    ? workKanbanEntities.size()
                    : 1;
            // 登録
            List<ServerErrorTypeEnum> registReportErrorList = new ArrayList<>();
            List<ActualResultEntity> retActualEntites= new ArrayList<>();
            ObjectProperty<ActualResultEntity> retEntity = new SimpleObjectProperty<>();
            for (WorkKanbanEntity workKanbanEntity : workKanbanEntities) {
                KanbanEntity kanbanEntity = kanbanEntityMap.get(workKanbanEntity.getKanbanId());
                retEntity.setValue(null);
                report.setKanbanId(workKanbanEntity.getKanbanId());
                report.setWorkKanbanId(workKanbanEntity.getWorkKanbanId());
                
                if (Objects.nonNull(workTime)) {
                    // 複数の作業をまとめて着完の場合、作業時間をシフトする
                    ActualSearchCondition lastConditon = new ActualSearchCondition()
                        .kanbanId(report.getKanbanId())
                        .workKanbanList(Arrays.asList(report.getWorkKanbanId()))
                        .equipmentList(Arrays.asList(report.getEquipmentId()))
                        .organizationList(Arrays.asList(report.getOrganizationId()))
                        .statusList(Arrays.asList(KanbanStatusEnum.WORKING))
                        .toDate(report.getReportDatetime())// 実績日時より前
                        .resultDailyEnum(ActualResultDailyEnum.ALL);

                    ActualResultEntity lastActualResult = this.actualResultRest.findLastActualResult(lastConditon, authId);
                    if (Objects.nonNull(lastActualResult)) {
                        lastActualResult.setImplementDatetime(reportDate);
                        em.merge(lastActualResult);
                    }

                    reportDate = new Date(reportDate.getTime() + workTime);
                    report.setReportDatetime(reportDate);
                }

                ServerErrorTypeEnum errorType = this.registReport(report, kanbanEntity, kanbanEntity.getWorkflow(), workKanbanEntity, retEntity, false, simultaneousNum, authId);
                registReportErrorList.add(errorType);
                if (ServerErrorTypeEnum.SUCCESS.equals(errorType)
                        && Objects.nonNull(retEntity.get())) {
                    retActualEntites.add(retEntity.get());
                }
            }

            if (retActualEntites.size() >=2 && !MultiWork.MultiWorkType.Parallel.equals(multiWork.getMultiWorkType())) {
                String multi = retActualEntites
                        .stream()
                        .map(ActualResultEntity::getActualId)
                        .map(String::valueOf)
                        .collect(joining(","));

                retActualEntites
                        .forEach(actualResult -> {
                            List<AddInfoEntity> addInfo = JsonUtils.jsonToObjects(actualResult.getActualAddInfo(), AddInfoEntity[].class);
                            addInfo.add(new AddInfoEntity("Multi", CustomPropertyTypeEnum.TYPE_STRING, multi, addInfo.size(), null));
                            actualResult.setActualAddInfo(JsonUtils.objectsToJson(addInfo));
                            em.merge(actualResult);
                        });
            }

            // 登録にて異常が発生したら終了
            Optional<ServerErrorTypeEnum> optRegistError =
                    registReportErrorList
                            .stream()
                            .filter(registReportError -> !ServerErrorTypeEnum.SUCCESS.equals(registReportError))
                            .findFirst();

            if (optRegistError.isPresent()) {
                return new ActualProductReportResult(optRegistError.get(), runtimeData.forwardTransactionId(report));
            }

            // 登録が成功した工程カンバン群
            List<Long> workKanbanIds =
                    workKanbanEntities
                            .stream()
                            .map(WorkKanbanEntity::getWorkKanbanId)
                            .collect(toList());

            long completeCount = 0L; // 1日の完了数
            if (KanbanStatusEnum.COMPLETION.equals(report.getStatus())
                    && ServiceTypeEnum.GENERIC.equals(report.getServiceType())) {
                completeCount = workKanbanEntities
                        .stream()
                        .mapToLong(workKanbanEntity -> this.workKandanRest.countCompletionForDay(workKanbanEntity.getWorkflowId(), workKanbanEntity.getWorkId(), report.getReportDatetime()))
                        .sum();
            }

            // トランザクションIDを更新
            long nextTid = runtimeData.forwardTransactionId(report);
            logger.info("Succeeded to update. nextTid: {}", nextTid);

            return new ActualProductReportResult(ServerErrorTypeEnum.SUCCESS, nextTid, workKanbanIds, completeCount);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ActualProductReportResult(ServerErrorTypeEnum.SERVER_FETAL, runtimeData.forwardTransactionId(report), new ArrayList<>());
        } finally {
            logger.info("doMUltiReport end.");
        }
    }

    /**
     * 工程実績を登録する。(DB登録ファイルあり)
     *
     * @param inputStreams マルチパートリクエストボディ
     * @param isCheckWorking 二重作業の判定 (true：する, false：しない)
     * @param authId 認証ID 
     * @param dataType データ形式
     * @return 工程実績登録結果
     */
    @POST
    @Path("report/multipart")
    @Consumes("multipart/form-data")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ActualProductReportResult reportMultiPart(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("checkWorking") Boolean isCheckWorking, @QueryParam("authId") Long authId, @QueryParam("dataType") DataTypeEnum dataType) {
        logger.info("reportMultiPart: isCheckWorking={}, authId={}", isCheckWorking, authId);
        ActualProductReportEntity report = null;
        try{
            // 1番目(ActualProductReportEntity)
            InputStreamReader isr = new InputStreamReader(inputStreams.get(0), "UTF-8");
            if (DataTypeEnum.JSON.equals(dataType)) {
                // JSON
                Stream<String> stream = new BufferedReader(isr).lines();
                String json = stream.collect(Collectors.joining());
                report = JsonUtils.jsonToObject(json, ActualProductReportEntity.class);
            } else {
                // JSON以外
                report = JAXB.unmarshal(isr, ActualProductReportEntity.class);
            }

            for (int count = 2; count <= inputStreams.size(); count++) {
                // 2番目以降(ファイルデータ)
                InputStream inputStream = inputStreams.get(count - 1);
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                byte[] buff = new byte[1024];
                int len = 0;
                
                // ファイル情報抽出
                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
                    while ((len = bis.read(buff)) != -1) {
                        byteArrayOutputStream.write(buff, 0, len);
                    }
                    byte[] fileByte = byteArrayOutputStream.toByteArray();

                    // ファイルデータを格納
                    if (Objects.nonNull(report.getAditions().get(count - 2))) {
                        report.getAditions().get(count - 2).setRawData(fileByte);
                    }
                }
            }
        } catch (Exception ex){ 
            logger.fatal(ex, ex);
            return new ActualProductReportResult(ServerErrorTypeEnum.SERVER_FETAL, ActrualResultRuntimeData.getInstance().forwardTransactionId(report));
        } finally {
            logger.info("reportMultiPart end.");
        }
        
        try{
            return this.doReport(report, isCheckWorking, false, authId);
        } finally {
            logger.info("reportMultiPart end.");
        }
    }
   
    /**
     * 工程実績を登録する。
     *
     * @param report 工程実績情報
     * @param isCheckWorking 二重作業の判定 (true：する, false：しない)
     * @param byLite adProduct Liteによる更新
     * @param authId 認証ID
     * @return 工程実績登録結果
     */
    public ActualProductReportResult doReport(ActualProductReportEntity report, Boolean isCheckWorking, boolean byLite, Long authId) {
        logger.info("doReport: {}, isCheckWorking={}, authId={}", report, isCheckWorking, authId);

        ServerErrorTypeEnum e;
        ActrualResultRuntimeData runtimeData = ActrualResultRuntimeData.getInstance();
        List<Long> workKanbanIds = new ArrayList();

        try {
            if (!runtimeData.checkTransactionId(report)) {
                //すでに実績を受け取っているので無視する.
                logger.info("Invalid report: {}", report);
                return new ActualProductReportResult(ServerErrorTypeEnum.SUCCESS, runtimeData.getNextTransactionId(report));
            }

            KanbanEntity kanban = super.find(report.getKanbanId());
            if (Objects.isNull(kanban)) {
                logger.fatal("Not found kanban: {}", report);
                return new ActualProductReportResult(ServerErrorTypeEnum.NOTFOUND_KANBAN, runtimeData.forwardTransactionId(report));
            }

            // 二重作業の判定が有効で、通知された実績のステータスが「作業中」の場合、二重作業の判定を行なう。
            if (Objects.nonNull(isCheckWorking) && isCheckWorking
                    && KanbanStatusEnum.WORKING.equals(report.getStatus())) {
                // 他の設備で作業中の組織の場合、「他端末で作業中の組織」のエラーを返す。
                if (this.checkWorking(report.getEquipmentId(), report.getOrganizationId(), report.getReportDatetime())) {
                    return new ActualProductReportResult(ServerErrorTypeEnum.ALREADY_WORKING_ORGANIZATION, runtimeData.forwardTransactionId(report));
                }
            }

            // 工程順情報を取得する。
            WorkflowEntity workflow = this.workflowRest.find(kanban.getWorkflowId(), null, authId);

            kanban.setWorkflow(workflow);
            kanban.setWorkflowName(workflow.getWorkflowName());
            kanban.setWorkflowRev(workflow.getWorkflowRev());

            if (KanbanStatusEnum.PLANNED.equals(kanban.getKanbanStatus())
                    && KanbanStatusEnum.WORKING.equals(report.getStatus())
                    && ServiceTypeEnum.GENERIC.equals(report.getServiceType())) {
                
                boolean update = true;
                if (!StringUtils.isEmpty(kanban.getServiceInfo())) {
                    // デンソー高棚様の場合、計画を更新しない
                    DsKanban dsKanban = DsKanban.lookup(kanban.getServiceInfo());
                    update = Objects.isNull(dsKanban);
                }

                if (update) {
                    if (!(LicenseManager.getInstance().isLicenceOption(LicenseOptionType.Scheduling.getName()) && report.getIsSchedule())
                            || Objects.isNull(kanban.getStartDatetime())) {
                        // スケジュールなしの場合 又は、開始時間の未設定の場合、開始時間を更新
                        logger.info("No schedule.");
                        this.updateBaseTime(kanban, workflow, report.getReportDatetime());
                    }
                }
            }

            // カンバン情報にカンバン階層IDがない場合、取得する。
            if (Objects.isNull(kanban.getParentId())) {
                Long parentId = this.findParentId(kanban.getKanbanId());
                kanban.setParentId(parentId);
            }

            Long completeCount = 0L; // 1日の完了数

            if (Objects.isNull(report.getWorkKanbanCollection())) {
                // 工程カンバン情報を取得する。
                WorkKanbanEntity workKanban = this.workKandanRest.find(report.getWorkKanbanId(), authId);
                if (Objects.isNull(workKanban.getWorkKanbanId())) {
                    logger.fatal("Not found workKanban: {}", report);
                    return new ActualProductReportResult(ServerErrorTypeEnum.NOTFOUND_WORKKANBAN, runtimeData.forwardTransactionId(report));
                }

                switch (report.getStatus()) {
                    case COMPLETION:
                    case SUSPEND:
                        if (!KanbanStatusEnum.WORKING.equals(workKanban.getWorkStatus())) {
                            long workNum = this.workKanbanWorkingRest.countWorking(workKanban.getWorkKanbanId());
                            if (workNum <= 0) {
                                // 既に作業がキャンセルされているため無視する
                                logger.info("Work canceled: {} {}", workKanban.getKanbanName(), workKanban.getWorkName());
                                return new ActualProductReportResult(ServerErrorTypeEnum.SUCCESS, runtimeData.forwardTransactionId(report));
                            }
                        }

                        // 応援者が作業中の場合、完了は不可
                        if (Objects.isNull(report.isSupportMode()) || report.isSupportMode() == false) {
                            Long supporterNumber = workKanbanWorkingRest.getSupporterNumber((workKanban.getWorkKanbanId()));
                            if (supporterNumber > 0) {
                                return new ActualProductReportResult(ServerErrorTypeEnum.THERE_SUPPORT_WORKING, runtimeData.forwardTransactionId(report));
                            }
                        }
                        break;

                    case DEFECT:
                        // 応援者が作業中の場合、ロットアウトは不可
                        if (Objects.isNull(report.isSupportMode()) || report.isSupportMode() == false) {
                            Long supporterNumber = workKanbanWorkingRest.getSupporterNumber((workKanban.getWorkKanbanId()));
                            if (supporterNumber > 0) {
                                return new ActualProductReportResult(ServerErrorTypeEnum.THERE_SUPPORT_WORKING_LOTOUT, runtimeData.forwardTransactionId(report));
                            }
                        }
                        break;

                    case OTHER:
                        // その他の場合は、実績データの登録のみ
                        ErrorResultEntity errorResult = this.updateActualResult(kanban, workKanban, report, null, null, 0, report.getInterruptReason(), false, kanban.getServiceInfo(), 0, null, null, null);
                        return new ActualProductReportResult(errorResult.getErrorType(), runtimeData.forwardTransactionId(report));
                }

                // 作業を開始できるかチェックする。
                e = this.check(report, workKanban, kanban);
                if (!ServerErrorTypeEnum.SUCCESS.equals(e)) {
                    return new ActualProductReportResult(e, runtimeData.forwardTransactionId(report));
                }

                // 間接作業実施中の場合停止させる
                Response response = operationRest.completeIndirectWork(report.getReportDatetime(), report.getOrganizationId(), authId);
                if(!((ResponseEntity)response.getEntity()).isSuccess()) {
                    return new ActualProductReportResult(ServerErrorTypeEnum.SERVER_FETAL, runtimeData.forwardTransactionId(report));
                }

                e = this.registReport(report, kanban, workflow, workKanban, byLite, 1, authId);
                if (!ServerErrorTypeEnum.SUCCESS.equals(e)) {
                    return new ActualProductReportResult(e, runtimeData.forwardTransactionId(report));
                }

                workKanbanIds.add(workKanban.getWorkKanbanId());

                if (KanbanStatusEnum.COMPLETION.equals(report.getStatus())
                        && ServiceTypeEnum.GENERIC.equals(report.getServiceType())) {
                    // 1日の完了数
                    completeCount = this.workKandanRest.countCompletionForDay(workflow.getWorkflowId(), workKanban.getWorkId(), report.getReportDatetime());
                }

            } else {
                List<WorkKanbanEntity> workKanbans = new ArrayList();
                for (Long workKanbanId : report.getWorkKanbanCollection()) {
                    // 工程カンバン情報を取得する。
                    WorkKanbanEntity workKanban = this.workKandanRest.find(workKanbanId, authId);
                    if (Objects.isNull(workKanban.getWorkKanbanId())) {
                        logger.fatal("Not found workKanban: {}", report);
                        return new ActualProductReportResult(ServerErrorTypeEnum.NOTFOUND_WORKKANBAN, runtimeData.forwardTransactionId(report), new ArrayList());
                    }
                    workKanbans.add(workKanban);
                }

                if (KanbanStatusEnum.WORKING.equals(report.getStatus())) {
                    for (WorkKanbanEntity workKanban : workKanbans) {
                        e = this.check(report, workKanban, kanban);
                        if (ServerErrorTypeEnum.SUCCESS != e) {
                            return new ActualProductReportResult(e, runtimeData.forwardTransactionId(report), new ArrayList());
                        }
                    }
                } else if (KanbanStatusEnum.COMPLETION.equals(report.getStatus())) {
                    kanban.setWorkKanbanCount(this.workKandanRest.countWorkKanban(report.getKanbanId(), false));
                }

                // 間接作業実施中の場合停止させる
                Response response = operationRest.completeIndirectWork(report.getReportDatetime(), report.getOrganizationId(), authId);
                if(!((ResponseEntity)response.getEntity()).isSuccess()) {
                    return new ActualProductReportResult(ServerErrorTypeEnum.SERVER_FETAL, runtimeData.forwardTransactionId(report));
                }

                for (WorkKanbanEntity workKanban : workKanbans) {
                    if (!KanbanStatusEnum.WORKING.equals(report.getStatus())
                            && !KanbanStatusEnum.WORKING.equals(workKanban.getWorkStatus())) {
                        long workNum = this.workKanbanWorkingRest.countWorking(workKanban.getWorkKanbanId());
                        if (workNum <= 0) {
                            // 既に作業がキャンセルされているため無視する
                            logger.info("Work canceled: {} {}", workKanban.getKanbanName(), workKanban.getWorkName());
                            workKanbanIds.add(workKanban.getWorkKanbanId());
                            continue;
                        }
                    }

                    // 工程実績を登録する。
                    e = this.registReport(report, kanban, workflow, workKanban, false, 1, authId);
                    if (!ServerErrorTypeEnum.SUCCESS.equals(e)) {
                        return new ActualProductReportResult(e, runtimeData.forwardTransactionId(report), workKanbanIds);
                    }
                    workKanbanIds.add(workKanban.getWorkKanbanId());
                }
            }

            // トランザクションIDを更新
            long nextTid = runtimeData.forwardTransactionId(report);
            logger.info("Succeeded to update. nextTid: {}", nextTid);

            return new ActualProductReportResult(ServerErrorTypeEnum.SUCCESS, nextTid, workKanbanIds, completeCount);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ActualProductReportResult(ServerErrorTypeEnum.SERVER_FETAL, runtimeData.forwardTransactionId(report), workKanbanIds);
        } finally {
            logger.info("doReport end.");
        }
    }

    /**
     * 指定組織が、指定設備以外で作業中かチェックする。
     *
     * @param equipmentId 設備ID
     * @param organizationId 組織ID
     * @param reportDatetime 作業日時
     * @return true：他の設備で作業中, false：他の設備で作業していない
     * @throws Exception
     */
    @Lock(LockType.READ)
    private boolean checkWorking(long equipmentId, long organizationId, Date reportDatetime) throws Exception {
        logger.info("checkWorking: equipmentId={}, organizationId={}", equipmentId, organizationId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        List<KanbanStatusEnum> workStatus = Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND);

        // 作業中のカンバンのカンバンID一覧を取得するサブクエリ。
        Subquery<Long> kanbanSubquery = cq.subquery(Long.class);
        Root<KanbanEntity> poolKanban = kanbanSubquery.from(KanbanEntity.class);

        jakarta.persistence.criteria.Path<Long> pathKanbanId = poolKanban.get(KanbanEntity_.kanbanId);
        jakarta.persistence.criteria.Path<KanbanStatusEnum> pathKanbanStatus = poolKanban.get(KanbanEntity_.kanbanStatus);

        kanbanSubquery.select(pathKanbanId)
                .where(pathKanbanStatus.in(workStatus));

        // 作業中の工程カンバンの工程カンバンID一覧を取得するサブクエリ。
        Subquery<Long> workKanbanSubquery = cq.subquery(Long.class);
        Root<WorkKanbanEntity> poolWorkKanban = workKanbanSubquery.from(WorkKanbanEntity.class);

        jakarta.persistence.criteria.Path<Long> pathWorkKanbanId = poolWorkKanban.get(WorkKanbanEntity_.workKanbanId);
        jakarta.persistence.criteria.Path<Boolean> pathSkipFlag = poolWorkKanban.get(WorkKanbanEntity_.skipFlag);
        jakarta.persistence.criteria.Path<KanbanStatusEnum> pathWorkStatus = poolWorkKanban.get(WorkKanbanEntity_.workStatus);
        jakarta.persistence.criteria.Path<Long> pathFkKanbanId = poolWorkKanban.get(WorkKanbanEntity_.kanbanId);

        List<Predicate> workKanbanWhere = new ArrayList();
        workKanbanWhere.add(cb.equal(pathSkipFlag, false));
        workKanbanWhere.add(pathWorkStatus.in(workStatus));
        workKanbanWhere.add(pathFkKanbanId.in(kanbanSubquery));

        workKanbanSubquery.select(pathWorkKanbanId)
                .where(cb.and(workKanbanWhere.toArray(new Predicate[workKanbanWhere.size()])));

        // 指定された組織が、指定された設備以外の設備で、作業中の工程カンバンについて、工程実績一覧を取得する。
        Root<ActualResultEntity> poolActualResult = cq.from(ActualResultEntity.class);

        jakarta.persistence.criteria.Path<Long> pathActualId = poolActualResult.get(ActualResultEntity_.actualId);
        jakarta.persistence.criteria.Path<Long> pathFkOrganizationId = poolActualResult.get(ActualResultEntity_.organizationId);
        jakarta.persistence.criteria.Path<Long> pathFkEquipmentId = poolActualResult.get(ActualResultEntity_.equipmentId);
        jakarta.persistence.criteria.Path<Long> pathFkWorkKanbanId = poolActualResult.get(ActualResultEntity_.workKanbanId);
        jakarta.persistence.criteria.Path<Date> pathImplementDatetime = poolActualResult.get(ActualResultEntity_.implementDatetime);

        List<Predicate> where = new ArrayList();
        where.add(cb.equal(pathFkOrganizationId, organizationId));
        where.add(cb.notEqual(pathFkEquipmentId, equipmentId));
        where.add(pathFkWorkKanbanId.in(workKanbanSubquery));

        // チェック対象期間
        Date fromDate = DateUtils.toDate(DateUtils.toLocalDate(reportDatetime).minusDays(1));
        where.add(cb.greaterThanOrEqualTo(pathImplementDatetime, fromDate));

        cq.select(poolActualResult)
                .where(cb.and(where.toArray(new Predicate[where.size()])))
                .orderBy(cb.asc(pathFkEquipmentId), cb.asc(pathFkWorkKanbanId), cb.desc(pathImplementDatetime), cb.desc(pathActualId));

        Query query = this.em.createQuery(cq);

        List<ActualResultEntity> actuals = query.getResultList();

        // 設備・工程カンバンごとに、最新の実績ステータスが「作業中」の実績がないかチェックする。
        Long prevEquipmentId = null;
        Long prevWorkKanbanId = null;
        for (ActualResultEntity actual : actuals) {
            if ((Objects.isNull(prevEquipmentId) && Objects.isNull(prevWorkKanbanId))
                    || (Objects.nonNull(prevEquipmentId) && !prevEquipmentId.equals(actual.getEquipmentId()))
                    || (Objects.nonNull(prevWorkKanbanId) && !prevWorkKanbanId.equals(actual.getWorkKanbanId()))) {
                if (KanbanStatusEnum.WORKING.equals(actual.getActualStatus())) {
                    return true;
                }

                prevEquipmentId = actual.getEquipmentId();
                prevWorkKanbanId = actual.getWorkKanbanId();
            }
        }

        return false;
    }

    /**
     * 作業を開始できるかチェックする。
     *
     * @param report 工程実績情報
     * @param workKanban 工程カンバン情報
     * @return 結果
     * @throws Exception
     */
    @Lock(LockType.READ)
    private ServerErrorTypeEnum check(ActualProductReportEntity report, WorkKanbanEntity workKanban, KanbanEntity kanban) {
        if (report.getServiceType() == ServiceTypeEnum.GENERIC) {
            if (KanbanStatusEnum.WORKING == report.getStatus()) {
                // 同時作業禁止 かつ 作業中の場合
                if (workKanban.isSyncWork()
                        && KanbanStatusEnum.WORKING.equals(workKanban.getWorkStatus())) {
                    logger.fatal("!!! WORKING !!! {}", report);
                    return ServerErrorTypeEnum.THERE_WORKING_NON_START;
                }

                // 作業が完了している場合
                // 但し、やり直しの場合は作業を開始できる
                if (!report.isRework()
                        && KanbanStatusEnum.COMPLETION.equals(workKanban.getWorkStatus())) {
                    logger.fatal("!!! COMPLETED !!! {}", report);
                    return ServerErrorTypeEnum.THERE_COMPLETED_NON_START;
                }
                
                // kanbanstatusのチェック 計画中か中止の場合は実行不可
                if (KanbanStatusEnum.PLANNING.equals(kanban.getKanbanStatus()) || KanbanStatusEnum.INTERRUPT.equals(kanban.getKanbanStatus())) {
                    logger.fatal("!!! KanbanStatus is PLANNING or INTERRUPT so can't start working !!! {}", report);
                    return ServerErrorTypeEnum.THERE_INTERRUPT_NON_START;
                }
                
                // 応援者の場合工程が作業中でない場合は開始できない
                if (Objects.nonNull(report.isSupportMode()) && report.isSupportMode() == true) {
                    List<WorkKanbanWorkingEntity> workKanbanWorkingList = workKanbanWorkingRest.getWorking(workKanban.getWorkKanbanId());
                    if (workKanbanWorkingList.isEmpty()) {
                         logger.fatal("!!! main worker do not work this workKanban !!! {}", report);
                        return ServerErrorTypeEnum.THERE_NOT_WORKING_NON_START;
                    }
                }
                
                if (!StringUtils.isEmpty(kanban.getServiceInfo())) {
                    // シリアル番号を確認
                    List<String> serialNumbers = null;
                    if (!StringUtils.isEmpty(report.getServiceInfo())) {
                        List<KanbanProduct> products = KanbanProduct.lookupProductList(report.getServiceInfo(), ServiceInfoEntity.SERVICE_INFO_PRODUCT);
                        serialNumbers = products.isEmpty() ? null : products.stream().map(o -> o.getUid()).collect(Collectors.toList());
                    } else if (!StringUtils.isEmpty(report.getSerialNo())) {
                        serialNumbers = Arrays.asList(report.getSerialNo());
                    }

                    if (Objects.nonNull(serialNumbers) && !StringUtils.isEmpty(workKanban.getServiceInfo())) {
                        List<KanbanProduct> products = KanbanProduct.lookupProductList(workKanban.getServiceInfo(), ServiceInfoEntity.SERVICE_INFO_PRODUCT);
                        List<KanbanStatusEnum> statuses = Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.SUSPEND); // 作業開始可能な作業状態
                        List<String> _serialNumbers = serialNumbers;
        
                        long count = products.stream().filter(o -> 
                                        _serialNumbers.contains(o.getUid()) 
                                        && o.getImplement() 
                                        && (statuses.contains(o.getStatus()) || report.isRework()))
                                    .count();

                        if (serialNumbers.size() != count) {
                            logger.warn("There is a serial number that cannot be worked. {}", report);
                            return ServerErrorTypeEnum.THERE_WORKING_NON_START;                        
                        }
                    }
                }
            }
        }
        return ServerErrorTypeEnum.SUCCESS;
    }

    /**
     * 工程実績を登録する。
     *
     * @param report 工程実績情報
     * @param kanban カンバン情報
     * @param workflow 工程順情報
     * @param workKanban 工程カンバン情報
     * @param  byLite adProduct Liteによる更新
     * @return 結果
     * @throws Exception
     */
    private ServerErrorTypeEnum registReport(ActualProductReportEntity report, KanbanEntity kanban, WorkflowEntity workflow, WorkKanbanEntity workKanban, boolean byLite, int simultaneousNum, Long authId) throws Exception {
        return registReport(report, kanban, workflow, workKanban, null, byLite, simultaneousNum, authId);
    }

    /**
     * 工程実績を登録する。
     *
     * @param report 工程実績情報
     * @param kanban カンバン情報
     * @param workflow 工程順情報
     * @param workKanban 工程カンバン情報
     * @param retEntity 戻り値
     * @param  byLite adProduct Liteによる更新
     * @return 結果
     * @throws Exception
     */
    private ServerErrorTypeEnum registReport(ActualProductReportEntity report, KanbanEntity kanban, WorkflowEntity workflow, WorkKanbanEntity workKanban, ObjectProperty<ActualResultEntity> retEntity, boolean byLite, int simultaneousNum, Long authId) throws Exception {

        WorkingReport workingReport = null;
        Long pairId = null;
        Integer nonWorkTime = null;
        Integer compNum = 0; // 完了数
        int workTime = 0;
        List<String> serialNumbers = null; // シリアル番号
        String serviceName = null;
        
        String interruptReason = report.getInterruptReason();
        String baseServiceInfo = kanban.getServiceInfo(); // 不良品情報の登録時に更新されてしまうため、元の情報を保持しておく。
        
        // 工程カンバン作業中リストを更新
        // 同一作業者端末の応援者の場合、工程カンバン作業中リストを更新しない
        long workNum = this.workKanbanWorkingRest.updateWorking(report.getStatus(), new WorkKanbanWorkingEntity(workKanban.getWorkKanbanId(), report.getEquipmentId(), report.getOrganizationId(), report.isSupportMode()), byLite);
        boolean isWorkCompletion = !report.isWorkSupport() && (workNum <= 0);

        if (!StringUtils.isEmpty(kanban.getServiceInfo())) {
            List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(kanban.getServiceInfo(), ServiceInfoEntity[].class);
            for (ServiceInfoEntity serviceInfo : serviceInfos) {
                if (Objects.isNull(serviceInfo.getJob())) {
                    continue;
                }
                serviceName = serviceInfo.getService();

                switch (serviceInfo.getService()) {
                    case ServiceInfoEntity.SERVICE_INFO_PRODUCT:
                        // 日本マイクロニクス様
                        List<KanbanProduct> products = KanbanProduct.lookupProductList(report.getServiceInfo(), ServiceInfoEntity.SERVICE_INFO_PRODUCT);
                        if (products.isEmpty()) {
                            products = KanbanProduct.lookupProductList(kanban.getServiceInfo(), ServiceInfoEntity.SERVICE_INFO_PRODUCT);
                        }
                        serialNumbers = products.isEmpty() ? null : products.stream().map(o -> o.getUid()).collect(Collectors.toList());
                        break;

                    case ServiceInfoEntity.SERVICE_INFO_DSKANBAN:
                        // デンソー高棚様
                        if (!workKanban.getSeparateWorkFlag()) {
                            int serial = Objects.nonNull(workKanban.getActualNum1()) ? workKanban.getActualNum1() + 1 : 1;
                            serialNumbers = Arrays.asList(String.valueOf(serial));
                        }
                        break;
                }
            }         

        } else if (!StringUtils.isEmpty(report.getSerialNo())) {
            serialNumbers = Arrays.asList(report.getSerialNo());
        }

        // 工程カンバンのステータスを変更
        workKanban.setImplementFlag(true);
        switch (report.getStatus()) {
            case WORKING:
                if (KanbanStatusEnum.PLANNED.equals(workKanban.getWorkStatus())) {
                    this.startWorkKanban(report, kanban, workKanban);

                } else if (report.isRework()
                        && Arrays.asList(KanbanStatusEnum.COMPLETION, KanbanStatusEnum.INTERRUPT).contains(workKanban.getWorkStatus())) {
                    // やり直しの場合、開始日時・完了日時を更新
                    workKanban.setActualStartTime(report.getReportDatetime());
                    workKanban.setActualCompTime(null);

                    int reworkNum = Objects.nonNull(workKanban.getReworkNum()) ? workKanban.getReworkNum() : 0;
                    workKanban.setReworkNum(reworkNum + 1);
                }

                this.updateProducts(report, kanban, workKanban, serialNumbers, serviceName);

                workKanban.setWorkStatus(KanbanStatusEnum.WORKING);

                if (LicenseManager.getInstance().isLicenceOption(LicenseOptionType.LineTimer.getName())) {
                    // ライン生産開始
                    this.startLine(report.getEquipmentId(), report.getReportDatetime(), kanban.getModelName());
                }

                // 作業中(WORKING)の場合、当日中の一時中断からの再開なら、中断時間と中断理由をセットする。
                ActualSearchCondition prevConditon = new ActualSearchCondition()
                        .workKanbanList(Arrays.asList(report.getWorkKanbanId()))
                        //.equipmentList(Arrays.asList(report.getEquipmentId())) // Liteで、どの作業者端末からも中断・完了を出来るようにするため
                        .organizationList(Arrays.asList(report.getOrganizationId()))
                        .statusList(Arrays.asList(KanbanStatusEnum.SUSPEND))
                        .fromDate(DateUtils.getBeginningOfDate(report.getReportDatetime()))// 実績日の0時以降
                        .toDate(new Date(report.getReportDatetime().getTime() - 1))// 実績日時より前
                        .resultDailyEnum(ActualResultDailyEnum.ALL);

                ActualResultEntity prevActual = this.actualResultRest.findLastActualResult(prevConditon, null);
                if (Objects.nonNull(prevActual)) {
                    //Long diffTime = report.getReportDatetime().getTime() - prevActual.getImplementDatetime().getTime();
                    //nonWorkTime = diffTime.intValue();// 中断時間[ms]

                    // 中断時間から休憩時間を除く
                    List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList();
                    if (Objects.nonNull(report.getOrganizationId())) {
                        List<Long> breaktimeIds = this.organizationRest.getBreaktimes(report.getOrganizationId());
                        if (!breaktimeIds.isEmpty()) {
                            List<BreaktimeEntity> breaktimes = this.breaktimeRest.find(breaktimeIds);
                            for (BreaktimeEntity breaktime : breaktimes) {
                                breaktimeCollection.add(new BreakTimeInfoEntity(breaktime.getBreaktimeName(), breaktime.getStarttime(), breaktime.getEndtime()));
                            }
                        }
                    }
                    nonWorkTime = (int) BreaktimeUtil.getDiffTime(breaktimeCollection, prevActual.getImplementDatetime(), report.getReportDatetime());

                    pairId = prevActual.getActualId();// 一時中断(SUSPEND)の実績ID
                    interruptReason = prevActual.getInterruptReason();// 中断理由
                }
                break;

            case COMPLETION: {
                    workingReport = this.getWorkingReport(report);
                    workTime = workingReport.getWorkTime()/simultaneousNum;
                    pairId = workingReport.getPairId();

                    kanban.setWorkKanbanCollection(this.workKandanRest.getWorkKanbans(kanban.getKanbanId(), false));
                    kanban.setSeparateworkKanbanCollection(this.workKandanRest.getWorkKanbans(kanban.getKanbanId(), true));

                    Tuple<KanbanStatusEnum, Integer> status = this.updateProducts(report, kanban, workKanban, serialNumbers, serviceName);
                    compNum = status.getRight();

                    if (compNum > 0) {
                        // 作業が完了した製品は、次工程へ進める
                        this.progressWork(report, kanban, workflow, workKanban, serialNumbers);
                    }

                    if (StringUtils.equals(serviceName, ServiceInfoEntity.SERVICE_INFO_DSKANBAN)) {
                        // デンソー高棚様
                        if (DSWORK_PRDUCTION[0].equals(workKanban.getWorkName())) {
                            // 部品集荷の実績を保存
                            DsPickup dsPickup = DsPickup.lookup(report.getServiceInfo());
                            if (Objects.nonNull(dsPickup)) {
                                workKanban.setServiceInfo(report.getServiceInfo());
                            }
                        }

                        if (!workKanban.getSeparateWorkFlag()) {
                            // 完成数
                            compNum = 1;

                            // 完成数が指示数に達成した場合、工程カンバンを完了にする
                            workKanban.setActualNum1(Objects.nonNull(workKanban.getActualNum1()) ? workKanban.getActualNum1() + 1 : 1);
                            isWorkCompletion = workKanban.getActualNum1() >= kanban.getLotQuantity();
                        }
                    }

                    if (isWorkCompletion) {
                        if (kanban.getProductionType() == 0) {
                            if (KanbanStatusEnum.COMPLETION.equals(status.getLeft())) {
                                workKanban.setActualCompTime(report.getReportDatetime());
                                workKanban.setWorkStatus(KanbanStatusEnum.COMPLETION);
                            } else {
                                workKanban.setWorkStatus(status.getLeft());
                            }
                        } else {
                            workKanban.setActualCompTime(report.getReportDatetime());
                            workKanban.setWorkStatus(KanbanStatusEnum.COMPLETION);
                        }
                    }
                }
                break;

            case SUSPEND: {
                    workingReport = this.getWorkingReport(report);
                    workTime = workingReport.getWorkTime()/simultaneousNum;
                    pairId = workingReport.getPairId();

                    Tuple<KanbanStatusEnum, Integer> status = this.updateProducts(report, kanban, workKanban, serialNumbers, serviceName);

                    if (isWorkCompletion) {
                        workKanban.setWorkStatus(status.getLeft());
                    }
                    
                    if (StringUtils.equals(serviceName, ServiceInfoEntity.SERVICE_INFO_DSKANBAN)) {
                        if (DSWORK_PRDUCTION[0].equals(workKanban.getWorkName())) {
                            // 部品集荷の実績を保存
                            DsPickup dsPickup = DsPickup.lookup(report.getServiceInfo());
                            if (Objects.nonNull(dsPickup)) {
                                workKanban.setServiceInfo(report.getServiceInfo());
                            }
                        }
                    }
                }
                break;

            case DEFECT:
                this.updateProducts(report, kanban, workKanban, serialNumbers, serviceName);

                workKanban.setWorkStatus(KanbanStatusEnum.DEFECT);
                
                if (StringUtils.isEmpty(kanban.getServiceInfo())) {
                    kanban.setDefectNum((Objects.nonNull(kanban.getLotQuantity()) ? kanban.getLotQuantity() : 1 ));
                }
                break;

            default:
                workKanban.setWorkStatus(report.getStatus());
                break;
        }
        
        if (Objects.nonNull(report.getTaktTime())) {
            logger.info("Set the takt time: " + report.getTaktTime());
            workKanban.setTaktTime(report.getTaktTime());
        }
                
        // 作業時間を加算
        workKanban.setSumTimes(workKanban.getSumTimes() + workTime);
        // 工程カンバンテーブルの要実績出力フラグ更新

        if (Objects.isNull(workKanban.getReworkNum()) || workKanban.getReworkNum() == 0 && !workKanban.getNeedActualOutputFlag()) {
           workKanban.setNeedActualOutputFlag(true);
        }

        if (ActualProductReportEntity.FORCED.equals(report.getInterruptReason())) {
            workKanban.setNeedActualOutputFlag(false);
        }

        this.workKandanRest.editWorkKanban(workKanban);

        // やり直し作業の中断または完了時、それ以降の完了済みの工程カンバン情報を更新する。
        if (report.isRework()
                && Arrays.asList(KanbanStatusEnum.COMPLETION, KanbanStatusEnum.SUSPEND).contains(report.getStatus())
                && report.isLaterRework()) {
            List<WorkKanbanEntity> workKanbans = this.workKandanRest.getWorkKanbans(report.getKanbanId(), false);
            kanban.setWorkKanbanCollection(workKanbans);
            workKanbans.stream()
                    .filter(p -> p.getWorkKanbanOrder() > workKanban.getWorkKanbanOrder()
                            && KanbanStatusEnum.COMPLETION.equals(p.getWorkStatus()))
                    .forEach(entity -> {
                        entity.setWorkStatus(KanbanStatusEnum.PLANNED);
                        entity.setActualStartTime(null);
                        entity.setActualCompTime(null);
                        if (Objects.isNull(entity.getReworkNum())) {
                            entity.setReworkNum(1);
                        } else {
                            entity.setReworkNum(entity.getReworkNum() + 1);
                        }
                    });
        }

        // 全体ステータスの変更
        KanbanStatusEnum allStatus = KanbanStatusEnum.DEFECT.equals(workKanban.getWorkStatus()) ? KanbanStatusEnum.DEFECT : this.updateStatus(kanban);
        if (Objects.nonNull(allStatus) && allStatus != kanban.getKanbanStatus()) {
            logger.info("setKanbanStatus:{}", allStatus);
            switch (allStatus) {
                case WORKING:
                    // カンバン開始日時を更新
                    if (kanban.getKanbanStatus() == KanbanStatusEnum.PLANNED) {
                        kanban.setActualStartTime(report.getReportDatetime());
                        //Integer num = this.updateProductInfo(report.getProducts());
                        //if (Objects.nonNull(num)) {
                        //    kanban.setLotQuantity(num);
                        //}
                    }
                    break;

                case COMPLETION:
                    kanban.setActualCompTime(report.getReportDatetime());

                    // カンバンを完了カンバン用の階層に移動する。
                    this.moveCloseKanban(kanban);

                    // 製品情報に完成日時を設定
                    for (ProductEntity product : this.getProducts(kanban.getKanbanId(), null)) {
                        product.setCompDatetime(report.getReportDatetime());
                    }
                    break;
            }
            kanban.setKanbanStatus(allStatus);
        }

        // 工程を進める
        if (isWorkCompletion) {
            boolean isWorkflowCompletion = KanbanStatusEnum.COMPLETION.equals(kanban.getKanbanStatus());
            
            if (!isWorkflowCompletion) {
                isWorkflowCompletion = this.progressWork(report, kanban, workflow, workKanban, serialNumbers);
            }

            if (KanbanStatusEnum.COMPLETION.equals(report.getStatus())) {
                if (report.isRework() || Objects.nonNull(workKanban.getReworkNum())) {
                    compNum = 0;

                } else {
                    if (kanban.getProductionType() != 1) {
                        if (Objects.isNull(serialNumbers)) {
                            compNum = (Objects.nonNull(kanban.getLotQuantity()) ? kanban.getLotQuantity() : 1) - kanban.getDefectNum();
                            workKanban.setActualNum1(compNum);
                        }

                    } else {
                        // ロット一個流し生産／シリアル番号単位の作業の場合
                        compNum = 1;

                        List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(workKanban.getWorkKanbanAddInfo(), AddInfoEntity[].class);
                        if (Objects.nonNull(addInfos)) {
                            // 最後の工程かどうか
                            Optional<AddInfoEntity> finalWork = addInfos.stream().filter(p -> Constants.FINAL_WORK.equals(p.getKey())).findFirst();
                            if (finalWork.isPresent()) {
                                isWorkflowCompletion = true;
                            }
                            
                            // 数量を取得
                            Optional<AddInfoEntity> productQty = addInfos.stream().filter(p -> Constants.PRODUCT_QTY.equals(p.getKey())).findFirst();
                            if (productQty.isPresent()) {
                                try {
                                    compNum = Integer.parseInt(productQty.get().getVal());
                                } catch (Exception ex) {
                                    logger.fatal(ex, ex);
                                }
                            }
                        }

                        workKanban.setActualNum1(Objects.isNull(workKanban.getActualNum1()) ? compNum : workKanban.getActualNum1() + compNum);
                    }

                    if (isWorkflowCompletion && compNum > 0) {
                        kanban.setCompNum(kanban.getCompNum() + compNum);
                    }                
                }
            }
        }

        if (StringUtils.equals(serviceName, ServiceInfoEntity.SERVICE_INFO_DSKANBAN)) {
            this.updateDsKanban(report, kanban, workKanban, workTime, isWorkCompletion);
        }
        
        super.edit(kanban);

        // 実績通知処理
        ErrorResultEntity errorResult = this.updateActualResult(kanban, workKanban, report, workTime, pairId, nonWorkTime, 
                interruptReason, isWorkCompletion, baseServiceInfo, compNum, null, workingReport, serialNumbers);

        if (!ServerErrorTypeEnum.SUCCESS.equals(errorResult.getErrorType())) {
            return errorResult.getErrorType();
        }

        ActualResultEntity actual = (ActualResultEntity) errorResult.getValue();
        if (Objects.nonNull(retEntity)) {
            retEntity.setValue(actual);
        }

        // adInterfaceへの通知
        if (Objects.nonNull(this.adIntefaceFacade)) {
                    
            if (LicenseManager.getInstance().isLicenceOption(LicenseOptionType.LineTimer.getName())
                    && Objects.isNull(workKanban.getReworkNum())
                    && report.getStatus() == KanbanStatusEnum.COMPLETION 
                    && isWorkCompletion) {
                // ライン生産完了
                this.endLine(report.getEquipmentId(), kanban.getModelName(), compNum, kanban.getKanbanStatus());
            }

            KanbanStatusEnum workStatus = workKanban.getWorkStatus(); 
            if (!StringUtils.isEmpty(report.getSerialNo())) {
                workStatus = isWorkCompletion ? report.getStatus() : workKanban.getWorkStatus();
            }
        
            ActualNoticeCommand command = new ActualNoticeCommand(actual.getActualId(),
                    kanban.getKanbanId(),
                    workKanban.getWorkKanbanId(),
                    workKanban.getWorkId(),
                    null,
                    report.getEquipmentId(),
                    null,
                    report.getOrganizationId(),
                    workStatus,
                    kanban.getKanbanStatus(),
                    report.getStatus(),
                    kanban.getModelName(),
                    isWorkCompletion,
                    compNum,
                    report.getReportDatetime());

            if (isDbOutput) {
                // 実績データを外部出力する場合
                OrganizationEntity organization = this.organizationRest.findBasicInfo(actual.getOrganizationId());
                EquipmentEntity equipment = this.equipmentRest.find(actual.getEquipmentId(), null);
                
                if (Objects.nonNull(kanban.getParentId()) && kanban.getParentId() != 0) {
                    // カンバン階層情報を取得して、階層名をセットする。
                    actual.setKanbanParentName(this.kanbanHierarchyRest.findBasicInfo(kanban.getParentId()).getHierarchyName());
                }

                WorkResult workResult = new WorkResult(actual.getKanbanParentName(),
                        kanban.getKanbanName(),
                        kanban.getKanbanSubname(),
                        workflow.getWorkflowName(),
                        workKanban.getWorkName(), 
                        organization.getOrganizationName(), 
                        organization.getOrganizationIdentify(), 
                        equipment.getEquipmentName(),
                        equipment.getEquipmentIdentify(), 
                        actual.getInterruptReason(),
                        actual.getDelayReason(), 
                        actual.getImplementDatetime(),
                        workKanban.getTaktTime(),
                        actual.getWorkingTime(),
                        pairId);

                List<WorkDetail> details = new ArrayList<>();
                workResult.setDetails(details);

                // 工程プロパティを取得
                WorkEntity work = this.workRest.findBasicInfo(workKanban.getWorkId());
                List<AddInfoEntity> workCheckInfos = JsonUtils.jsonToObjects(work.getWorkCheckInfo(), AddInfoEntity[].class);
                List<AddInfoEntity> actualAddInfos = JsonUtils.jsonToObjects(actual.getActualAddInfo(), AddInfoEntity[].class);
                Map<Integer, AddInfoEntity> map = workCheckInfos.stream().collect(Collectors.toMap(o -> o.getDisp(), o -> o));

                int order = 1;
                for (AddInfoEntity entity : actualAddInfos) {

                    String name; // 項目名
                    String tag; // タグ

                    if (map.containsKey(entity.getAccessoryId())) {
                        name = map.get(entity.getAccessoryId()).getKey();
                        tag = entity.getKey();
                    } else {
                        name = entity.getKey();
                        tag = entity.getKey();
                    }
                    WorkDetail detail = new WorkDetail(order++, name, tag, entity.getVal());
                    details.add(detail);
                }

                if (this.isTraceabilityDBEnabled && Objects.nonNull(report.getTraceabilities())) {
                    for (TraceabilityEntity trace : report.getTraceabilities()) {
                        // OK判定
                        if (Objects.nonNull(trace.getTraceConfirm())) {
                            String okTag = trace.getTraceTag() + "_OK";
                            details.add(new WorkDetail(order++, trace.getTraceName(), okTag, trace.getTraceConfirm() ? "1" : "0"));
                        }
                        
                        // 値
                        if (Objects.nonNull(trace.getTraceValue())) {
                            details.add(new WorkDetail(order++, trace.getTraceName(), trace.getTraceTag(), trace.getTraceValue()));
                        }
                        
                        // 設備管理名
                        if (Objects.nonNull(trace.getEquipmentName())) {
                            String equipmentTag = trace.getTraceTag() + "_EQUIPMENT";
                            details.add(new WorkDetail(order++, trace.getTraceName(), equipmentTag, trace.getEquipmentName()));
                        }

                        if (!StringUtils.isEmpty(trace.getTraceProps())) {
                            ObjectMapper mapper = new ObjectMapper();
                            Map<String, String> traceMap = mapper.readValue(trace.getTraceProps(), new TypeReference<LinkedHashMap<String, String>>(){});
                            for (Map.Entry<String, String> entry : traceMap.entrySet()) {
                                details.add(new WorkDetail(order++, trace.getTraceName(), entry.getKey(), entry.getValue()));
                            }
                        }
                    }
                }

                command.setWorkResult(workResult);
            }

            this.adIntefaceFacade.noticeActual(command);
        }

        return ServerErrorTypeEnum.SUCCESS;
    }
    
    /**
     * 製品情報を更新する。
     * 
     * @param report 工程実績情報
     * @param kanban カンバン情報
     * @param workKanban 工程カンバン情報
     * @param serialNumbers 更新対象のシリアル番号
     * @param serviceName サービス名
     * @return 工程カンバンのステータス、作業が完了して製品数
     */
    private Tuple<KanbanStatusEnum, Integer> updateProducts(ActualProductReportEntity report, KanbanEntity kanban, WorkKanbanEntity workKanban, List<String> serialNumbers, String serviceName) {
        if (!StringUtils.equals(serviceName, ServiceInfoEntity.SERVICE_INFO_PRODUCT) || Objects.isNull(serialNumbers)) {
            return new Tuple(report.getStatus(), 0);
        }
 
        // 不良品を取得
        Map<String, KanbanProduct> defectProducts = KanbanProduct.lookupProductList(report.getServiceInfo(), ServiceInfoEntity.SERVICE_INFO_DEFECT)
                .stream().collect(Collectors.toMap(o -> o.getUid(), o -> o));

        // カンバンの製品情報を更新
        if (!defectProducts.isEmpty()) {
            List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(kanban.getServiceInfo(), ServiceInfoEntity[].class);
            for (ServiceInfoEntity serviceInfo : serviceInfos) {
                if (StringUtils.equals(serviceInfo.getService(), ServiceInfoEntity.SERVICE_INFO_PRODUCT)) {
                    List<KanbanProduct> products = KanbanProduct.toKanbanProducts(serviceInfo);
                    int defectNum = 0;

                    for (KanbanProduct product : products) {
                        if (defectProducts.containsKey(product.getUid())) {
                            KanbanProduct defect = defectProducts.get(product.getUid());
                            product.setDefect(defect.getDefect());
                            if (!StringUtils.isEmpty(product.getDefect())) {
                                defectNum++;
                            }
                        }
                    }

                    serviceInfo.setJob(products);
                    kanban.setServiceInfo(JsonUtils.objectsToJson(serviceInfos));
                    kanban.setDefectNum(defectNum);
                    break;
                }
            }
        }
        
        // 工程カンバンの製品情報を更新、工程ステータスと完了数を取得
        int compNum = 0;
        long defectNum = 0;
        
        List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(workKanban.getServiceInfo(), ServiceInfoEntity[].class);
        for (ServiceInfoEntity serviceInfo : serviceInfos) {
            if (StringUtils.equals(serviceInfo.getService(), ServiceInfoEntity.SERVICE_INFO_PRODUCT)) {
                List<KanbanProduct> products = KanbanProduct.toKanbanProducts(serviceInfo);
                
                for (KanbanProduct o : products) {
                    if (!serialNumbers.contains(o.getUid())) {
                        continue;
                    }

                    o.setStatus(report.getStatus());

                    if (!defectProducts.containsKey(o.getUid())) {
                        switch (report.getStatus()) {
                            case WORKING:
                                o.setDefect(null);
                                if (StringUtils.isEmpty(o.getStartTime())) {
                                    o.setStartTime(DateUtils.format(report.getReportDatetime()));
                                }
                                break;
                            case COMPLETION:
                                o.setDefect(null);
                                o.setCompTime(DateUtils.format(report.getReportDatetime()));
                                compNum++;
                                break;
                            case DEFECT:
                                o.setDefect(report.getDefectReason());
                                break;
                            default:
                                o.setDefect(null);
                                break;
                        }

                    } else {
                        // 不良
                        KanbanProduct defect = defectProducts.get(o.getUid());
                        if (!StringUtils.isEmpty(defect.getDefect())) {
                            o.setStatus(KanbanStatusEnum.DEFECT);
                        }
                        o.setDefect(defect.getDefect());
                    }
                }

                defectNum = products.stream().filter(o -> !StringUtils.isEmpty(o.getDefect())).count();

                serviceInfo.setJob(products);
                workKanban.setServiceInfo(JsonUtils.objectsToJson(serviceInfos));
                workKanban.setActualNum1((int)products.stream().filter(o -> KanbanStatusEnum.COMPLETION.equals(o.getStatus())).count());

                if (report.getStatus() == KanbanStatusEnum.WORKING) {
                    return new Tuple(KanbanStatusEnum.WORKING, 0);
                }
                
                for (KanbanProduct product : products) {
                    if (KanbanStatusEnum.SUSPEND.equals(product.getStatus())) {
                        //return new Tuple((defectNum == 0 ? KanbanStatusEnum.SUSPEND : KanbanStatusEnum.DEFECT), compNum);
                        return new Tuple(KanbanStatusEnum.SUSPEND, compNum);
                    }
                }

                for (KanbanProduct product : products) {
                    if (!KanbanStatusEnum.COMPLETION.equals(product.getStatus()) 
                            && StringUtils.isEmpty(product.getDefect())) {
                        return new Tuple(KanbanStatusEnum.WORKING, compNum);
                    }
                }

                break;
            }
        }

        //return new Tuple((defectNum == 0 ? KanbanStatusEnum.COMPLETION : KanbanStatusEnum.DEFECT), compNum);
        return new Tuple(KanbanStatusEnum.COMPLETION, compNum);
    }

    /**
     * 工程を進める。
     * 
     * @param report 工程実績情報
     * @param kanban カンバン情報
     * @param workflow 工程順情報
     * @param workKanban 工程カンバン情報
     * @param serialNumbers シリアル番号
     * @return
     */
    private boolean progressWork(ActualProductReportEntity report, KanbanEntity kanban, WorkflowEntity workflow, WorkKanbanEntity workKanban, List<String> serialNumbers) {
        boolean result = false;
        
        try {
            logger.info("progressWork start: kanbanId={}, workId={}", kanban.getKanbanId(), workKanban.getWorkId());

            if (KanbanStatusEnum.COMPLETION.equals(report.getStatus())) {
                if (Objects.nonNull(report.getProducts())
                        && !report.getProducts().isEmpty()) {
                    kanban.setRepairNum(0);

                    if (Objects.nonNull(report.getPropertyCollection())) {
                        Optional<ActualProductReportPropertyEntity> opt = report.getPropertyCollection().stream()
                                .filter(o -> REPAIR_NUM.equals(o.getPropertyName()))
                                .findFirst();
                        if (opt.isPresent()) {
                            ActualProductReportPropertyEntity property = opt.get();
                            try {
                                // 補修数を更新
                                kanban.setRepairNum(Integer.parseInt(property.getPropertyValue()));

                                if (0 == kanban.getRepairNum()) {
                                    // 補修工程をスキップ
                                    this.skip(kanban, workKanban, REPAIR);
                                }
                            } catch (NumberFormatException ex) {
                                logger.fatal(ex, ex);
                            }
                        }
                    }

                    // 工程順を進める
                    WorkflowInteface workflowModel = WorkflowModelFacade.createInstance(this.workKandanRest, kanban.getWorkKanbanCollection(), workflow.getWorkflowDiaglam());

                    result = workflowModel.executeWorkflow(workKanban.getWorkId(), serialNumbers);

                } else {
                    WorkflowInteface workflowModel;

                    if (kanban.getProductionType() != 1) {
                        // 一個流し生産、ロット生産の場合
                        workflowModel = WorkflowModelFacade.createInstance(this.workKandanRest, kanban.getWorkKanbanCollection(), workflow.getWorkflowDiaglam());
                        result = workflowModel.executeWorkflow(workKanban.getWorkId(), serialNumbers);
                    } else {
                        // ロット一個流し生産、グループ生産の場合
                        // カンバンの完了判定のため、工程カンバン数を取得
                        if (Objects.isNull(report.getWorkKanbanCollection())) {
                            kanban.setWorkKanbanCount(this.workKandanRest.countWorkKanban(kanban.getKanbanId(), false));
                        }

                        workflowModel = LotWorkflowModelFacede.createInstance(this.workKandanRest, kanban, workflow.getWorkflowDiaglam());
                        result = workflowModel.executeWorkflow(workKanban.getWorkId(), Objects.nonNull(workKanban.getSerialNumber()) ? Arrays.asList(String.valueOf(workKanban.getSerialNumber())) : null);
                    }
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("progressWork end.");
        }
        
        return result;
    }

    /**
     * 工程をスキップする。
     *
     * @param kanban
     * @param workKanban
     * @param workName
     */
    private void skip(KanbanEntity kanban, WorkKanbanEntity workKanban, String workName) {
        try {
            // 工程カンバンの追加情報のJSON文字列を追加情報一覧に変換する。
            List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(workKanban.getWorkKanbanAddInfo(), AddInfoEntity[].class);
            if (Objects.isNull(addInfos)) {
                return;
            }

            // 対象工程名を取得
            Optional<AddInfoEntity> opt = addInfos.stream().filter(o -> workName.equals(o.getKey())).findFirst();
            if (!opt.isPresent()) {
                return;
            }

            AddInfoEntity property = opt.get();
            WorkKanbanEntity tagetWork = this.workKandanRest.findByWorkName(kanban.getKanbanId(), property.getVal(), null);
            if (Objects.isNull(tagetWork)) {
                return;
            }

            logger.info("Skip work: {}", tagetWork.getWorkName());
            tagetWork.setSkipFlag(true);
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * カンバンを完了カンバン用の階層に移動する。
     *
     * @param kanban 移動対象のカンバン
     */
    private void moveCloseKanban(KanbanEntity kanban) throws Exception {
        logger.info("moveCloseKanban: {}", kanban);

        // カンバンが属する階層の階層IDを取得する。
        Long parentId = this.findParentId(kanban.getKanbanId());

        if (Objects.nonNull(parentId)) {
            // カンバン階層情報を取得して、完了カンバンの自動移動フラグがtrueの場合、カンバンを移動する。
            KanbanHierarchyEntity hierarchy = this.kanbanHierarchyRest.findBasicInfo(parentId);
            if (hierarchy.getPartitionFlag()) {
                // 移動先の階層名
                String destName = String.format(CLOSE_KANBAN_HIERARCHY_FORMAT, hierarchy.getHierarchyName(), kanban.getActualCompTime());

                KanbanHierarchyEntity destHierarchy = this.kanbanHierarchyRest.findHierarchyByName(destName, loginUserId, null);
                if (Objects.isNull(destHierarchy) || Objects.isNull(destHierarchy.getKanbanHierarchyId())) {
                    // 該当する階層名の階層が存在しない場合、カンバンの階層の子階層として新規作成する。
                    destHierarchy = new KanbanHierarchyEntity();
                    destHierarchy.setHierarchyName(destName);
                    destHierarchy.setParentId(parentId);

                    this.kanbanHierarchyRest.add(destHierarchy, null);
                }

                kanban.setParentId(destHierarchy.getKanbanHierarchyId());

                // 階層情報を更新する。
                this.removeHierarchy(kanban.getKanbanId());
                this.addHierarchy(kanban);
            }
        }
    }

    /**
     * カンバンの基準時間を更新する。
     *
     * @param kanban カンバン
     * @param workflow 工程順
     * @param baseTime 開始予定日時
     */
    private void updateBaseTime(KanbanEntity kanban, WorkflowEntity workflow, Date baseTime) {
        try {
            // 作業中のカンバンは更新不可。
            if (KanbanStatusEnum.WORKING.equals(kanban.getKanbanStatus())) {
                return;
            }

            // 工程順情報を取得する。
            if (Objects.isNull(workflow) || Objects.isNull(workflow.getConWorkflowWorkCollection())) {
                workflow = this.workflowRest.find(kanban.getWorkflowId(), null, null);
            }

            // 通常工程の工程カンバン情報一覧を取得してセットする。
            kanban.setWorkKanbanCollection(this.getWorkKanban(kanban.getKanbanId()));
            // 追加工程の工程カンバン情報一覧を取得してセットする。
            kanban.setSeparateworkKanbanCollection(this.getSeparateWorkKanban(kanban.getKanbanId()));

            // 工程カンバンに割り当てられている全組織の休憩情報一覧を取得する。
            List<BreakTimeInfoEntity> kanbanBreakTimes = new ArrayList();

            List<Long> workKanbanIds =kanban.getWorkKanbanCollection().stream()
                    .map(p -> p.getWorkKanbanId())
                    .collect(Collectors.toList());
            List<Long> organizationIds = this.workKandanRest.getOrganizationCollection(workKanbanIds);

            Set<Long> breaktimeIds = new HashSet();
            for (Long organizationId : organizationIds) {
                breaktimeIds.addAll(this.organizationRest.getBreaktimes(organizationId));
            }

            if (!breaktimeIds.isEmpty()) {
                List<BreaktimeEntity> breakTimes = this.breaktimeRest.find(new ArrayList(breaktimeIds));
                for (BreaktimeEntity breakTime : breakTimes) {
                    kanbanBreakTimes.add(new BreakTimeInfoEntity(breakTime.getBreaktimeId(), breakTime.getBreaktimeName(), breakTime.getStarttime(), breakTime.getEndtime()));
                }
            }

            // 基準日時以降の休日情報一覧を取得する。
            HolidaySearchCondition holidayCondition = new HolidaySearchCondition()
                    .fromDate(DateUtils.getBeginningOfDate(baseTime));
            List<HolidayEntity> holidays = this.holidayRest.searchHoliday(holidayCondition, null);

            // カンバンの基準時間を更新する。
            WorkflowProcess workflowProcess = new WorkflowProcess(workflow);
            workflowProcess.setOrganizationRest(this.organizationRest);
            workflowProcess.setBaseTime(kanban, kanbanBreakTimes, baseTime, holidays);

            // 工程カンバンを更新
            //for (WorkKanbanEntity workKanban : kanban.getWorkKanbanCollection()) {
            //    this.workKandanREST.editWorkKanban(workKanban);
            //}
            // カンバンを更新
            //super.edit(kanban);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンの基準時間を更新する。
     *
     * @param kanban カンバン
     * @param workflow 工程順
     * @param baseTime 開始予定日時
     */
    private void updateBaseTime2(KanbanEntity kanban, WorkflowEntity workflow, Date baseTime) {
        try {
            // 作業中のカンバンは更新不可。
            if (KanbanStatusEnum.WORKING.equals(kanban.getKanbanStatus())) {
                return;
            }

            // 工程カンバンに割り当てられている全組織の休憩情報一覧を取得する。
            List<BreakTimeInfoEntity> breaktimes = new ArrayList();

            List<Long> workKanbanIds = kanban.getWorkKanbanCollection().stream().map(WorkKanbanEntity::getWorkKanbanId).collect(Collectors.toList());
            List<Long> organizationIds = this.workKandanRest.getOrganizationCollection(workKanbanIds);

            Set<Long> breaktimeIds = new HashSet();
            for (Long organizationId : organizationIds) {
                breaktimeIds.addAll(this.organizationRest.getBreaktimes(organizationId));
            }

            if (!breaktimeIds.isEmpty()) {
                List<BreaktimeEntity> breakTimes = this.breaktimeRest.find(new ArrayList(breaktimeIds));
                for (BreaktimeEntity breakTime : breakTimes) {
                    breaktimes.add(new BreakTimeInfoEntity(breakTime.getBreaktimeId(), breakTime.getBreaktimeName(), breakTime.getStarttime(), breakTime.getEndtime()));
                }
            }

            // 基準日時以降の休日情報一覧を取得する。
            HolidaySearchCondition holidayCondition = new HolidaySearchCondition()
                    .fromDate(DateUtils.getBeginningOfDate(baseTime));
            List<HolidayEntity> holidays = this.holidayRest.searchHoliday(holidayCondition, null);

            // カンバンの基準時間を更新する。
            WorkflowProcess workflowProcess = new WorkflowProcess(workflow);
            workflowProcess.setOrganizationRest(this.organizationRest);
            workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 実績通知を記録する。
     *
     * @param kanban カンバン情報
     * @param workKanban 工程カンバン情報
     * @param report 生産実績通知
     * @param workTime 作業時間[ms]
     * @param pairId ペアID
     * @param nonWorkTime 中断時間[ms]
     * @param interruptReason 中断理由
     * @param isWorkCompletion 工程カンバン完了フラグ
     * @param kanbanServiceInfo 更新前のカンバンサービス情報
     * @param compNum 完了数
     * @param addInfos
     * @param workingReport 仕掛中作業情報
     * @param srialNumbers シリアル番号
     * @return 処理結果
     * @throws Exception 
     */
    public ErrorResultEntity updateActualResult(KanbanEntity kanban, WorkKanbanEntity workKanban, ActualProductReportEntity report, Integer workTime, 
            Long pairId, Integer nonWorkTime, String interruptReason, boolean isWorkCompletion, String kanbanServiceInfo, int compNum, List<AddInfoEntity> addInfos,
            WorkingReport workingReport, List<String> srialNumbers) throws Exception {
        ServerErrorTypeEnum res;

        // 設備名を取得する。
        String equipmentName = "";
        if (Objects.nonNull(report.getEquipmentId())) {
            equipmentName = this.equipmentRest.findNameById(report.getEquipmentId());
        }
        // 組織名を取得する。
        String organizationName = "";
        if (Objects.nonNull(report.getOrganizationId())) {
            organizationName = this.organizationRest.findNameById(report.getOrganizationId());
        }

        // 工程実績情報を作成する。
        ActualResultEntity actual = new ActualResultEntity(
                kanban.getKanbanId(), workKanban.getWorkKanbanId(), report.getReportDatetime(),
                report.getTransactionId(), report.getEquipmentId(), report.getOrganizationId(), kanban.getWorkflowId(), workKanban.getWorkId(),
                report.getStatus(), interruptReason, report.getDelayReason(), workTime, pairId, nonWorkTime, workKanban.getReworkNum(), false);
        actual.setKanbanName(kanban.getKanbanName());
        actual.setEquipmentName(equipmentName);
        actual.setOrganizationName(organizationName);
        actual.setWorkflowName(kanban.getWorkflowName());
        actual.setWorkName(workKanban.getWorkName());
        actual.setCompNum(compNum);
        actual.setDefectNum(report.getDefectNum());
        actual.setServiceInfo(report.getServiceInfo());

        if (Objects.nonNull(report.isSupportMode()) && report.isSupportMode()) {
            actual.setAssist(1);
        }
        
        if (KanbanStatusEnum.DEFECT.equals(report.getStatus())) {
            // 不良理由
            actual.setDefectReason(report.getDefectReason());
        }
        
        // シリアル番号
        if (Objects.nonNull(srialNumbers)) {
           actual.setSerialNo(String.join("|", srialNumbers));

        } else if (!StringUtils.isEmpty(kanban.getServiceInfo())) {
            List<KanbanProduct> products = KanbanProduct.lookupProductList(kanban.getServiceInfo(), ServiceInfoEntity.SERVICE_INFO_PRODUCT);
            if (!products.isEmpty()) {
                actual.setSerialNo(String.join("|", products.stream().map(o -> o.getUid()).collect(Collectors.toList())));
            }
        }
       
        // 追加情報一覧
        addInfos = Objects.isNull(addInfos) ? new LinkedList<>() : addInfos;
        if (Objects.nonNull(report.getPropertyCollection()) && !report.getPropertyCollection().isEmpty()) {
            for (ActualProductReportPropertyEntity prop : report.getPropertyCollection()) {
                addInfos.add(new AddInfoEntity(prop.getPropertyName(), CustomPropertyTypeEnum.toEnum(prop.getPropertyType()), prop.getPropertyValue(), prop.getPropertyOrder(), prop.getWorkPropertyId(), prop.getMemo()));
            }
        } else {
            int offset = 0;

            // カンバンの追加情報のJSON文字列を追加情報一覧に変換する。
            List<AddInfoEntity> kanbanProps = JsonUtils.jsonToObjects(kanban.getKanbanAddInfo(), AddInfoEntity[].class);
            if (Objects.nonNull(kanbanProps)) {
                offset = kanbanProps.size();
                addInfos.addAll(kanbanProps);
            }

            // 工程カンバンの追加情報のJSON文字列を追加情報一覧に変換する。
            List<AddInfoEntity> workProps = JsonUtils.jsonToObjects(workKanban.getWorkKanbanAddInfo(), AddInfoEntity[].class);
            if (Objects.nonNull(workProps)) {
                for (AddInfoEntity prop : workProps) {
                    addInfos.add(new AddInfoEntity(prop.getKey(), prop.getType(), prop.getVal(), prop.getDisp() + offset, null));
                }
            }
        }

        // 追加情報一覧をJSON文字列に変換する。
        String actualAddInfo = JsonUtils.objectsToJson(addInfos);

        // 検査結果
        actual.setActualAddInfo(actualAddInfo);

        Map<String, Long> lotSerialWorkflows = null;// ロット流し生産のシリアル番号・工程順IDマップ
        if (kanban.getProductionType() == 2) {
            // カンバンのサービス情報(JSON)からロット流し生産のシリアル番号・工程順IDマップを取得する。
            lotSerialWorkflows = this.getLotSerialWorkflows(kanbanServiceInfo);

            // シリアル番号ありの場合、ロットカンバンの作業時間は「0」にする。(シリアル番号のカンバンで作業時間の集計を行なうため)
            if (Objects.nonNull(lotSerialWorkflows) && !lotSerialWorkflows.isEmpty()) {
                actual.setWorkingTime(0);
            }
        }

        // 工程実績情報を登録する。
        this.actualResultRest.add(actual);

        if (Objects.nonNull(actual.getWorkingTime()) && actual.getWorkingTime() > 0 ) {
            // 直接工数を登録
            int workType = 0;
            String addInfo = null;
            WorkReportWorkNumEntity workReportWorkNum = new WorkReportWorkNumEntity();
            String classKey = "";
            
            if (this.isWorkReportWorkNumVisible) {
                if (!StringUtils.isEmpty(report.getServiceInfo())) {
                    KanbanCode kanbanCode = KanbanCode.lookup(report.getServiceInfo());
                    if (Objects.nonNull(kanbanCode)) {
                        workReportWorkNum.setListNo(kanbanCode.getListNo()); // リスト番号
                        workReportWorkNum.setResourceType(kanbanCode.getResourceType()); // 資源区部
                        workReportWorkNum.setResources(kanbanCode.getResourceNo()); // 資源コード
                        workReportWorkNum.setWorkNo(kanbanCode.getWorkNo()); // 工程コード
                        workReportWorkNum.setWorkSetup(kanbanCode.getWorkSetup()); // 段取り
                        workReportWorkNum.setOrderNum(kanbanCode.getLotQuantity().doubleValue()); // 指示数
                    }
                }

                Matcher m = workReportWorkNumPattern.matcher(kanban.getKanbanName());
                if (m.find() && m.groupCount() >= 2) {
                    final List<String> controlNo = DirectActualEntityFacadeREST.formatControlNumber(m.group(1));
                    final int digit = DirectActualEntityFacadeREST.calculateDigit(m.group(1));
                    workReportWorkNum.setControlNo(controlNo);
                    workReportWorkNum.setSelectedControlNo(controlNo);
                    workReportWorkNum.setDigit(digit);
                }

                ActualAddInfoEntity actualAddInfoEntity = new ActualAddInfoEntity();
                actualAddInfoEntity.setWorkReportWorkNum(workReportWorkNum);
                addInfo = JsonUtils.objectToJson(actualAddInfoEntity);
            }
            
            if (this.isEnableRework && Objects.nonNull(workingReport)) {
                List<AddInfoEntity> props = JsonUtils.jsonToObjects(workingReport.getAddInfo(), AddInfoEntity[].class);

                // 作業区分が後戻り作業・赤作業の場合、不具合箇所と作業理由を追加する
                boolean rework = props.stream()
                    .filter(p -> CustomPropertyTypeEnum.TYPE_STRING.equals(p.getType())
                            && StringUtils.equals(p.getKey(), LocaleUtils.getString("work_type"))
                            && StringUtils.equals(p.getVal(), LocaleUtils.getString("rework")))
                    .findFirst().isPresent();
                
                if (rework) {
                    workType = 3;
                    
                    // 不具合箇所
                    Optional<AddInfoEntity> remarks1 = props.stream()
                        .filter(p -> CustomPropertyTypeEnum.TYPE_STRING.equals(p.getType())
                                && StringUtils.equals(p.getKey(), LocaleUtils.getString("rework_unit")))
                        .findFirst();

                    // 作業理由
                    Optional<AddInfoEntity> remarks2 = props.stream()
                        .filter(p -> CustomPropertyTypeEnum.TYPE_STRING.equals(p.getType())
                                && StringUtils.equals(p.getKey(), LocaleUtils.getString("rework_reason")))
                        .findFirst();

                    if (remarks1.isPresent()) {
                        workReportWorkNum.setRemarks1(remarks1.get().getVal());
                        classKey = workReportWorkNum.getRemarks1();
                    }

                    if (remarks2.isPresent()) {
                        workReportWorkNum.setRemarks2(remarks2.get().getVal());
                        classKey = classKey + workReportWorkNum.getRemarks2();
                    }

                    ActualAddInfoEntity actualAddInfoEntity = new ActualAddInfoEntity();
                    actualAddInfoEntity.setWorkReportWorkNum(workReportWorkNum);
                    addInfo = JsonUtils.objectToJson(actualAddInfoEntity);
                }
            }

            DirectActualEntity directActual = new DirectActualEntity(
                workType,
                report.getReportDatetime(),
                report.getOrganizationId(),
                actual.getWorkId(),
                actual.getWorkName(),
                Objects.isNull(kanban.getKanbanSubname()) ? "" : kanban.getKanbanSubname(),
                actual.getWorkingTime(),
                actual.getWorkflowId(),
                kanban.getKanbanName(),
                kanban.getModelName(),
                actual.getCompNum(),
                1,
                kanban.getProductionNumber(),
                classKey,
                report.getOrganizationId());

            directActual.setActualAddInfo(addInfo);
            directActualEntityFacadeREST.add(directActual);
        }

        if (Objects.nonNull(actual.getNonWorkTime()) && actual.getNonWorkTime() > 0 ) {
            DirectActualEntity directActualEntity = new DirectActualEntity(
                    2,
                    report.getReportDatetime(),
                    report.getOrganizationId(),
                    actual.getWorkId(),
                    actual.getInterruptReason(),
                    kanban.getKanbanSubname(),
                    actual.getNonWorkTime(),
                    actual.getWorkflowId(),
                    kanban.getKanbanName(),
                    kanban.getModelName(),
                    0,
                    3,
                    kanban.getProductionNumber(),
                    "",
                    report.getOrganizationId());
            directActualEntityFacadeREST.add(directActualEntity);
        }

        // 工程カンバンの最終実績IDを更新する。
        this.workKandanRest.updateLastActualId(workKanban.getWorkKanbanId(), actual.getActualId());

        // トレーサビリティDB使用時は、トレーサビリティDBにデータを追加する。
        if (this.isTraceabilityDBEnabled && Objects.nonNull(report.getTraceabilities()) && !report.getTraceabilities().isEmpty()) {
            TraceabilityJdbc jdbc = TraceabilityJdbc.getInstance();
            jdbc.addTraceability(actual.getActualId(), report.getTraceabilities());
        }

        // ロット流し生産のシリアル番号毎の実績を登録する。(工程カンバンの最終実績IDは更新しない)
        res = this.addSerialKanbanActual(actual, lotSerialWorkflows, workTime);
        if (!ServerErrorTypeEnum.SUCCESS.equals(res)) {
            return new ErrorResultEntity(res, null);
        }

        // 工程カンバン完了時のみ、完成品の処理を行なう。
        if (KanbanStatusEnum.COMPLETION.equals(report.getStatus()) && isWorkCompletion) {
            // 完成品情報を登録する。
            res = this.partsRest.registParts(report.getParts(), report.getWorkKanbanId(), report.getReportDatetime());
            if (!ServerErrorTypeEnum.SUCCESS.equals(res)) {
                return new ErrorResultEntity(res, null);
            }

            // 使用済の完成品情報を削除する。
            List<String> partsIds = addInfos.stream()
                    .filter(p -> CustomPropertyTypeEnum.TYPE_TRACE.equals(p.getType())
                            && p.getKey().matches(".*\\_PARTSID")
                            && !StringUtils.isEmpty(p.getVal()))
                    .map(p -> p.getVal())
                    .collect(Collectors.toList());

            res = this.partsRest.removeParts(partsIds, report.getWorkKanbanId());
            if (!ServerErrorTypeEnum.SUCCESS.equals(res)) {
                return new ErrorResultEntity(res, null);
            }
        }

        try{
            // 実績付加情報をDBに登録
            if (Objects.nonNull(report.getAditions())) {
                report.getAditions().stream()
                        .forEach(v -> actualAditionRest.add(new ActualAditionEntity(actual.getActualId(), v.getDataName(), v.getTag(), v.getRawData())));
            }
        } catch (Exception ex) {
            return new ErrorResultEntity(ServerErrorTypeEnum.ACTUAL_ADITION_ADD_ERROR, null);
        }

        // ロットトレース情報を更新する。
        if (!StringUtils.isEmpty(report.getLotTraceParts())) {
            this.updateLotTrace(kanban.getKanbanId(), kanban.getKanbanName(), kanban.getModelName(), workKanban.getWorkKanbanId(), workKanban.getWorkName(), report.getOrganizationId(), organizationName, report.getReportDatetime(), report.getLotTraceParts());
        }
        
        return new ErrorResultEntity(res, actual);
    }
   
    /**
     * 工程カンバンの状態からカンバンステータスを取得する。
     *
     * @param kanban カンバン情報
     * @return カンバンステータス
     */
    @Lock(LockType.READ)
    protected KanbanStatusEnum updateStatus(KanbanEntity kanban) {
        TypedQuery<Long> query;
        Long num;

        // 工程カンバンの中止はカンバンステータスに反映しない。
        query = this.em.createNamedQuery("WorkKanbanEntity.countByIdAndStatus", Long.class);
        query.setParameter("kanbanId", kanban.getKanbanId());
        query.setParameter("workStatuses", Collections.singletonList(KanbanStatusEnum.WORKING));
        num = query.getSingleResult();
        if (num > 0) {
            return KanbanStatusEnum.WORKING;
        }
 
        // 中断中の工程カンバンがあるか確認する。
        query = this.em.createNamedQuery("WorkKanbanEntity.countByIdAndStatus", Long.class);
        query.setParameter("kanbanId", kanban.getKanbanId());
        query.setParameter("workStatuses", Arrays.asList(KanbanStatusEnum.SUSPEND));
        num = query.getSingleResult();
        if (num > 0) {
            return KanbanStatusEnum.SUSPEND;
        }

        // 工程カンバンが全て完了またはスキップになっているか確認する。
        query = this.em.createNamedQuery("WorkKanbanEntity.countCompOrSkip", Long.class);
        query.setParameter("kanbanId", kanban.getKanbanId());
        num = query.getSingleResult();
        if (num > 0 && num == (kanban.getWorkKanbanCount() + kanban.getSeparateWorkKanbanCount())) {
            return KanbanStatusEnum.COMPLETION;
        }

        return null;
    }

    /**
     * 仕掛中作業情報を取得する。
     *
     * @param report 生産実績通知
     * @return 仕掛中作業情報
     */
    @Lock(LockType.READ)
    private WorkingReport getWorkingReport(ActualProductReportEntity report) {
        WorkingReport workingReport = new WorkingReport();

        //作業者の休憩時間間隔を取得.
        List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList();
        if (Objects.nonNull(report.getOrganizationId()) && !this.isWorkOvertime) {
            List<Long> breaktimeIds = this.organizationRest.getBreaktimes(report.getOrganizationId());
            if (!breaktimeIds.isEmpty()) {
                List<BreaktimeEntity> breaktimes = this.breaktimeRest.find(breaktimeIds);
                for (BreaktimeEntity breaktime : breaktimes) {
                    breaktimeCollection.add(new BreakTimeInfoEntity(breaktime.getBreaktimeName(), breaktime.getStarttime(), breaktime.getEndtime()));
                }
            }
        }

        //直近の開始時間との差分を計算.
        switch (report.getStatus()) {
            case COMPLETION:
            case SUSPEND:
                ActualSearchCondition lastConditon = new ActualSearchCondition()
                        .kanbanId(report.getKanbanId())
                        .workKanbanList(Arrays.asList(report.getWorkKanbanId()))
                        //.equipmentList(Arrays.asList(report.getEquipmentId())) // Liteで、どの作業者端末からも中断・完了を出来るようにするため
                        .organizationList(Arrays.asList(report.getOrganizationId()))
                        .statusList(Arrays.asList(KanbanStatusEnum.WORKING))
                        .toDate(new Date(report.getReportDatetime().getTime()))// 実績日時より前
                        .resultDailyEnum(ActualResultDailyEnum.ALL);
                ActualResultEntity lastActualResult = this.actualResultRest.findLastActualResult(lastConditon, null);
                if (Objects.nonNull(lastActualResult)) {
                    workingReport.setWorkTime((int) BreaktimeUtil.getDiffTime(breaktimeCollection, lastActualResult.getImplementDatetime(), report.getReportDatetime()));
                    workingReport.setPairId(lastActualResult.getActualId());
                    workingReport.setAddInfo(lastActualResult.getActualAddInfo());
                }
                break;
            default:
                break;
        }
        return workingReport;
    }

    /**
     * カンバン名・工程順名・版数からカンバンを作成する。
     *
     * @param name カンバン名
     * @param workflowName 工程順名
     * @param parent カンバン階層
     * @param creator 作成者(組織識別子)
     * @param workflowRev 版数
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @POST
    @Path("create")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response createKanban(@QueryParam("name") String name, @QueryParam("workflow") String workflowName, @QueryParam("parent") String parent, @QueryParam("creator") String creator, @QueryParam("rev") Integer workflowRev, @QueryParam("authId") Long authId) {
        logger.info("createKanban: name={}, workflowName={}, parent={}, creator={}, workflowRev={}, authId={}", name, workflowName, parent, creator, workflowRev, authId);
        try {
            if (Objects.isNull(name) || Objects.isNull(workflowName) || Objects.isNull(parent) || Objects.isNull(creator)) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            if (Objects.isNull(workflowRev)) {
                // 版数指定なしの場合は最新版 (削除済・未承認は除く)
                workflowRev = this.workflowRest.getLatestRev(workflowName, true, true);
            }

            Date now = new Date();

            // 工程順を取得する。
            WorkflowEntity workflow = this.workflowRest.findByName(workflowName, workflowRev, false, loginUserId, authId);
            if (Objects.isNull(workflow.getWorkflowId())) {
                // 存在しない場合、工程順なしを通知する。
                logger.fatal("Workflow is not found:{}", workflowName);
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORKFLOW)).build();
            }

            // カンバン名の重複を確認する。(削除済も含む)
            KanbanExistEntity exist = new KanbanExistEntity(null, name, null, workflow.getWorkflowId());
            if (this.existKanban(exist)) {
                // 該当するものがあった場合、重複を通知する。
                logger.fatal("Kanban is exists:{},{}", name, workflowName);
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // デフォルト階層を取得する。
            KanbanHierarchyEntity kanbanHierarchy = this.kanbanHierarchyRest.findHierarchyByName(parent, loginUserId, authId);
            if (Objects.isNull(kanbanHierarchy.getKanbanHierarchyId())) {
                // 存在しない場合、階層を作成する。
                logger.fatal("Hierarchy is not found:{}", parent);
                kanbanHierarchy = new KanbanHierarchyEntity(0L, parent);
                this.kanbanHierarchyRest.add(kanbanHierarchy, authId);
            }

            // 組織を取得する。
            OrganizationEntity organization = this.organizationRest.findByName(creator, loginUserId, authId);
            if (Objects.isNull(organization.getOrganizationId())) {
                logger.fatal("Organization is not found:{}", creator);
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_ORGANIZATION)).build();
            }

            // カンバンを作成する。
            KanbanEntity kanban = new KanbanEntity(kanbanHierarchy.getKanbanHierarchyId(), name, null, workflow.getWorkflowId(), workflowName, null, null, organization.getOrganizationId(), now, KanbanStatusEnum.PLANNED, null, null);
            kanban.setModelName(workflow.getModelName());
            kanban.setDefectNum(0);

            // カンバン情報を登録する。
            super.create(kanban);
            this.em.flush();

            // 階層関連付け情報を登録する。
            this.addHierarchy(kanban);

            // 追加情報をセットする。
            kanban.setKanbanAddInfo(workflow.getWorkflowAddInfo());

            // 工程カンバン情報を登録する。
            this.addWorkKanban(kanban, workflow);

            // カンバンの基準時間を更新する。
            this.updateBaseTime(kanban, workflow, now);
            kanban.setWorkKanbanCollection(this.workKandanRest.getWorkKanbans(kanban.getKanbanId(), false));

            // カンバン情報を更新する。
            super.edit(kanban);

            // 工程順を進める。
            if (!this.startWorkflow(kanban, workflow, null)) {
                logger.fatal("Can not be started the workflow.");
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
            }

            // 工程カンバン情報を更新する。
            this.updateWorkKanban(kanban);

            // キャッシュを更新
            kanban.setWorkflow(workflow);

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("kanban/").append(kanban.getKanbanId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        } finally {
            logger.info("create end.");
        }
    }

    /**
     * カンバン作成条件に従いカンバンを作成する。
     *
     * @param condition カンバン作成条件
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @POST
    @Path("create/condition")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response createKanban(KanbanCreateCondition condition, @QueryParam("authId") Long authId) {
        logger.info("createKanban: {}, authId={}", condition, authId);
        try {
            if (StringUtils.isEmpty(condition.getKanbanName()) || Objects.isNull(condition.isOnePieceFlow()) || Objects.isNull(condition.getLotQuantity())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            if (condition.getLotQuantity() == 0) {
                condition.setLotQuantity(1);
            }

            if (Objects.isNull(condition.getProductionType())) {
                condition.setProductionType(0);
            }

            // カンバン名の重複を確認する。(削除済も含む)
            KanbanExistEntity exist = new KanbanExistEntity(null, condition.getKanbanName(), null, condition.getWorkflowId());
            if (existKanban(exist)) {
                // 該当するものがあった場合、重複を通知する。
                logger.info("not update overlap name:{}", condition.getKanbanName());
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 工程順を取得する。
            WorkflowEntity workflow = this.getWorkflowEntity(condition.getWorkflowId());
            if (Objects.isNull(workflow.getWorkflowId())) {
                // 存在しない場合、工程順なしを通知する。
                logger.fatal("Workflow is not found:{}", workflow.getWorkflowId());
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORKFLOW)).build();
            }

            // 組織を取得する。
            OrganizationEntity organization;
            if (StringUtils.isEmpty(condition.getCreator())) {
                logger.info("creator is empty.");
                organization = new OrganizationEntity();
            } else {
                organization = this.organizationRest.findByName(condition.getCreator(), loginUserId, authId);
                if (Objects.isNull(organization.getOrganizationId())) {
                    logger.fatal("Organization is not found:{}", condition.getCreator());
                    //return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_ORGANIZATION)).build();
                }
            }

            Date now = new Date();

            // カンバンを作成する。
            KanbanEntity entity = new KanbanEntity(condition.getParentId(), condition.getKanbanName(), null, workflow.getWorkflowId(), workflow.getWorkflowName(), null, null, organization.getOrganizationId(), now, KanbanStatusEnum.PLANNING, null, null);
            entity.setWorkflow(workflow);
            entity.setModelName(workflow.getModelName());
            entity.setLotQuantity(condition.getLotQuantity());
            entity.setProductionType(condition.getProductionType());
            entity.setDefectNum(0);

            // カンバン情報を登録する。
            super.create(entity);
            this.em.flush();

            // 階層関連付け情報を登録する。
            this.addHierarchy(entity);

            // 追加情報をセットする。
            entity.setKanbanAddInfo(workflow.getWorkflowAddInfo());

            // 工程カンバン情報を登録する。
            this.addWorkKanban(entity, workflow, condition);
           
            // カンバンの基準時間を更新する。
            this.updateBaseTime(entity, workflow, condition.getStartTime());

            entity.getWorkKanbanCollection()
				.forEach(workKanbanEntity -> this.em.merge(workKanbanEntity));

            // カンバン情報を更新する。
            super.edit(entity);
            
            this.em.flush();
            this.em.clear();

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("kanban/").append(entity.getKanbanId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        } finally {
            logger.info("createKanban end.");
        }
    }
    
    /**
     * カンバン検索条件情報に従い、取得範囲を指定しカンバン情報の一覧を取得
     *
     * @param condition 検索条件
     * @param from 指定範囲の先頭
     * @param to 指定範囲の末尾
     * @param authId 認証ID
     * @return DBアクセス結果
     */
    @Lock(LockType.READ)
    @PUT
    @Path("results/search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanEntity> searchResult(KanbanSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchResult: {}, from={}, to={}, authId={}", condition, from, to, authId);
        try {
            Query query = this.getSearchQuery(SearchType.SEARCH, condition);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            List<KanbanEntity> kanbans = query.getResultList();
            for (KanbanEntity kanban : kanbans) {
                // 通常工程の工程カンバン情報を取得してセットする。
                kanban.setWorkKanbanCollection(this.getWorkKanban(kanban.getKanbanId()));
                // 追加工程の工程カンバン情報を取得してセットする。
                kanban.setSeparateworkKanbanCollection(this.getSeparateWorkKanban(kanban.getKanbanId()));
                // 工程実績情報をセットする。
                kanban.setActualResultCollection(this.getActualResults(kanban.getKanbanId()));
            }

            return kanbans;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 生産可能なカンバンの個数を取得する。
     *
     * @param equipmentId 設備ID
     * @param organizationId 組織ID
     * @param keyword 対象となるカンバン名(部分一致検索)
     * @param support 作業中のカンバンのみを返す
     * @param authId 認証ID
     * @return 個数
     */
    @Lock(LockType.READ)
    @GET
    @Path("product/search/count")
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countProduct(@QueryParam("equipment") Long equipmentId, @QueryParam("organization") Long organizationId, @QueryParam("keyword") String keyword, @QueryParam("support") Boolean support, @QueryParam("authId") Long authId) {
        logger.info("countProduct: equipmentId={}, organizationId={}, keyword={}, support={}, authId={}", equipmentId, organizationId, keyword, support, authId);
        try {
            // 2020/1/17 カンバンリストのフィルタリング 検索キーワード追加
            if (Objects.isNull(keyword)){
                keyword = "";
            }

            Query query;
            if (Objects.isNull(support) || !support) {
                query = em.createNamedQuery("KanbanEntity.countProductAll", Long.class);
                query.setParameter("kanbanStatuses", Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND));
            } else {
                query = em.createNamedQuery("KanbanEntity.countProduct", Long.class);
                query.setParameter("kanbanStatuses", Arrays.asList(KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND));
            }

            query.setParameter("equipmentIds", equipmentRest.getEquipmentPerpetuity(equipmentId));
            query.setParameter("organizationIds", organizationRest.getOrganizationPerpetuity(organizationId));

            //List<Long> hierarchy = kanbanHierarchyRest.getKanbanHierarchyIdsByName("adFactoryReport");
            //query.setParameter("kanbanHierarchyIds", hierarchy.isEmpty() ? Collections.singletonList(0) : hierarchy);

            query.setParameter("keyword", "%"+StringUtils.escapeLikeChar(keyword)+"%");

            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        } finally {
            logger.info("countProduct end.");
        }
    }

    /**
     * 生産可能なカンバン一覧を取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param equipmentId 設備ID
     * @param organizationId 組織ID
     * @param toDate 日付
     * @param keyword 対象となるカンバン名(部分一致検索)
     * @param sort ソートパターン PLAN(計画順) or NAME(名前順)
     * @param orderby ソート方向 ASC(昇順) or DESC(降順)
     * @param support 作業中のカンバンのみを返す
     * @param authId 認証ID
     * @return カンバン情報一覧
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("product/search/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanEntity> searchProduct(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("equipment") Long equipmentId, @QueryParam("organization") Long organizationId, @QueryParam("toDate") String toDate, @QueryParam("keyword") String keyword, @QueryParam("sort") String sort, @QueryParam("orderby") String orderby, @QueryParam("support") Boolean support, @QueryParam("detail") Boolean withDetail, @QueryParam("authId") Long authId) throws Exception {
        logger.info("searchProduct: from={}, to={}, equipment={}, organizationId={}, toDate={}, keyword={}, sort={}, orderby={}, support={}, authId={}", from, to, equipmentId, organizationId, toDate, keyword, sort, orderby, support, authId);
        StringBuilder queryName = new StringBuilder();

        try {
            TypedQuery<KanbanEntity> query;

            if (Objects.isNull(keyword)){
                keyword = "";
            }

            KanbanSortPatternEnum kanbanSortPattern = null;
            if (Objects.nonNull(sort)){
                kanbanSortPattern = KanbanSortPatternEnum.getEnum(sort);
            }

            if (Objects.isNull(kanbanSortPattern)){
                kanbanSortPattern = KanbanSortPatternEnum.PLAN;
            }

            if (Objects.isNull(support) || !support) {
                queryName.append("KanbanEntity.findProductAll");
            } else {
                // 応援の場合
                queryName.append("KanbanEntity.findProduct");
            }

            switch (kanbanSortPattern) {
                case STATUS:
                    // ステータス順ソート
                    if (Objects.nonNull(toDate)) {
                        // 設備ID・組織ID・日時を指定して、開始予定日時が指定日時以前で、生産可能なカンバン情報一覧を取得する。
                        if (Objects.nonNull(orderby) && DESC.equals(orderby)) {
                            queryName.append("TermStatusDesc");
                        } else {
                            queryName.append("TermStatusAsc");
                        }
                    } else {
                        // 設備ID・組織IDを指定して、生産可能なカンバン情報一覧を取得する。
                        if (Objects.nonNull(orderby) && DESC.equals(orderby)) {
                            queryName.append("StatusDesc");
                        } else {
                            queryName.append("StatusAsc");
                        }
                    }
                    break;
                case NAME:
                    // 名前順ソート
                    if (Objects.nonNull(toDate)) {
                        // 設備ID・組織ID・日時を指定して、開始予定日時が指定日時以前で、生産可能なカンバン情報一覧を取得する。
                        if (Objects.nonNull(orderby) && DESC.equals(orderby)) {
                            queryName.append("TermNameDesc");
                        } else {
                            queryName.append("TermNameAsc");
                        }
                    } else {
                        // 設備ID・組織IDを指定して、生産可能なカンバン情報一覧を取得する。
                        if (Objects.nonNull(orderby) && DESC.equals(orderby)) {
                            queryName.append("NameDesc");
                        } else {
                            queryName.append("NameAsc");
                        }
                    }
                    break;
                case CREATE:
                    // 作成順ソート
                    if (Objects.nonNull(toDate)) {
                        // 設備ID・組織ID・日時を指定して、開始予定日時が指定日時以前で、生産可能なカンバン情報一覧を取得する。
                        if (Objects.nonNull(orderby) && DESC.equals(orderby)) {
                            queryName.append("TermCreateDesc");
                        } else {
                            queryName.append("TermCreateAsc");
                        }
                    } else {
                        // 設備ID・組織IDを指定して、生産可能なカンバン情報一覧を取得する。
                        if (Objects.nonNull(orderby) && DESC.equals(orderby)) {
                            queryName.append("CreateDesc");
                        } else {
                            queryName.append("CreateAsc");
                        }
                    }
                    break;

                case PLAN:
                default:
                    // 計画順ソート
                    if (Objects.nonNull(toDate)) {
                        // 設備ID・組織ID・日時を指定して、開始予定日時が指定日時以前で、生産可能なカンバン情報一覧を取得する。
                        if (Objects.nonNull(orderby) && DESC.equals(orderby)) {
                            queryName.append("TermPlanDesc");
                        } else {
                            queryName.append("TermPlanAsc");
                        }
                    } else {
                        // 設備ID・組織IDを指定して、生産可能なカンバン情報一覧を取得する。
                        if (Objects.nonNull(orderby) && DESC.equals(orderby)) {
                            queryName.append("PlanDesc");
                        } else {
                            queryName.append("PlanAsc");
                        }
                    }
                    break;
            }

            query = this.em.createNamedQuery(queryName.toString(), KanbanEntity.class);
            if (Objects.isNull(support) || !support) {
                query.setParameter("kanbanStatuses", Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND));
            } else {
                query.setParameter("kanbanStatuses", Arrays.asList(KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND));
            }

            // 設備・組織は、ルートまでの親と、最下層までの子も対象とする。
            query.setParameter("equipmentIds", this.equipmentRest.getEquipmentPerpetuity(equipmentId));
            query.setParameter("organizationIds", this.organizationRest.getOrganizationPerpetuity(organizationId));
            if (Objects.nonNull(toDate)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
                query.setParameter("toDate", sdf.parse(toDate));
            }

            //List<Long> hierarchy = kanbanHierarchyRest.getKanbanHierarchyIdsByName("adFactoryReport");
            //query.setParameter("kanbanHierarchyIds", hierarchy.isEmpty() ? Collections.singletonList(0) : hierarchy);

            if(Objects.nonNull(keyword)){
                query.setParameter("keyword", "%"+StringUtils.escapeLikeChar(keyword)+"%");
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            List<KanbanEntity> entities = query.getResultList();
            if (Objects.isNull(withDetail) || withDetail) {
                for (KanbanEntity entity : entities) {
                    // 詳細情報を取得してセットする。
                    this.getDetails(entity, false);
                }
            }

            return entities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        } finally {
            logger.info("findProduct end: " + queryName.toString());
        }
    }

    // V1.9.4から未使用
    /**
     * 設備に対する完了数を取得する。
     *
     * @param equipmentIds 設備ID一覧
     * @param modelName モデル名
     * @return 完了数
     */
    @Lock(LockType.READ)
    public long getCompletionByEquipmentId(List<Long> equipmentIds, String modelName) {
        try {
            logger.info("getCompletionByEquipmentId: equipmentIds={}, modelName={}", equipmentIds, modelName);

            if (Objects.isNull(equipmentIds) || equipmentIds.isEmpty()) {
                return 0L;
            }

            Date now = new Date();
            Date fromDate = DateUtils.getBeginningOfDate(now);
            Date toDate = new Date(fromDate.getTime() + (3600 * 24 * 1000));

            CriteriaBuilder cb = this.em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();

            Root<KanbanEntity> poolKanban = cq.from(KanbanEntity.class);

            jakarta.persistence.criteria.Path<Long> pathKanbanId = poolKanban.get(KanbanEntity_.kanbanId);
            jakarta.persistence.criteria.Path<Date> pathActualCompTime = poolKanban.get(KanbanEntity_.actualCompTime);
            jakarta.persistence.criteria.Path<KanbanStatusEnum> pathKanbanStatus = poolKanban.get(KanbanEntity_.kanbanStatus);
            jakarta.persistence.criteria.Path<String> pathModelName = poolKanban.get(KanbanEntity_.modelName);
            jakarta.persistence.criteria.Path<Integer> pathLotQuantity = poolKanban.get(KanbanEntity_.lotQuantity);

            // 対象となる工程カンバンID一覧を取得するサブクエリ
            Subquery<Long> workKanbanIdQuery = cq.subquery(Long.class);
            Root<ConWorkkanbanEquipmentEntity> poolConWorkkanbanEquipment = cq.from(ConWorkkanbanEquipmentEntity.class);

            jakarta.persistence.criteria.Path<Long> pathFkWorkKanbanId = poolConWorkkanbanEquipment.get(ConWorkkanbanEquipmentEntity_.workKanbanId);
            jakarta.persistence.criteria.Path<Long> pathFkEquipmentId = poolConWorkkanbanEquipment.get(ConWorkkanbanEquipmentEntity_.equipmentId);

            workKanbanIdQuery.select(pathFkWorkKanbanId)
                    .where(cb.and(pathFkEquipmentId.in(equipmentIds)));

            // 対象となるカンバンID一覧を取得するサブクエリ
            Subquery<Long> kanbanIdQuery = cq.subquery(Long.class);
            Root<WorkKanbanEntity> poolWorkKanban = cq.from(WorkKanbanEntity.class);

            jakarta.persistence.criteria.Path<Long> pathFkKanbanId = poolWorkKanban.get(WorkKanbanEntity_.kanbanId);
            jakarta.persistence.criteria.Path<Long> pathWorkKanbanId = poolWorkKanban.get(WorkKanbanEntity_.workKanbanId);

            kanbanIdQuery.select(pathFkKanbanId)
                    .where(pathWorkKanbanId.in(workKanbanIdQuery));

            // 検索条件
            List<Predicate> where = new ArrayList();
            where.add(cb.greaterThanOrEqualTo(pathActualCompTime, fromDate));
            where.add(cb.lessThan(pathActualCompTime, toDate));
            where.add(cb.equal(pathKanbanStatus, KanbanStatusEnum.COMPLETION));

            if (Objects.nonNull(modelName) && !modelName.isEmpty()) {
                where.add(cb.like(pathModelName, StringUtils.escapeLike(modelName)));
            }

            where.add(pathKanbanId.in(kanbanIdQuery));

            // ロット数量が null の場合は「1」とする。
            CriteriaBuilder.Coalesce<Integer> coLotQuantity = cb.coalesce();
            coLotQuantity.value(pathLotQuantity);
            coLotQuantity.value(1);

            // 完了数(ロット数量の合計)を取得する。
            cq.select(cb.sumAsLong(coLotQuantity))
                    .where(cb.and(where.toArray(new Predicate[where.size()])));

            Query query = this.em.createQuery(cq);

            Object result = query.getSingleResult();
            if (Objects.isNull(result)) {
                // 対象レコードが0件の場合はnullになるので、完了数0件で返す。
                return 0L;
            }

            return (Long) result;
        } finally {
            logger.info("getCompletionByEquipmentId end.");
        }
    }

    /**
     * カンバンID一覧を指定して、カンバン情報一覧を取得する。
     * [GET] /rest/kanban
     *
     * @param kanbanIds カンバンID一覧
     * @param isDetail 詳細情報を取得する？ (true: する, false: しない)
     * @param authId 認証ID
     * @return カンバン情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanEntity> findByKanbanId(@QueryParam("id") final List<Long> kanbanIds, @QueryParam("detail") Boolean isDetail, @QueryParam("authId") Long authId) {
        logger.info("findByKanbanId: isDetail={}, kanbanIds={}, authId={}", isDetail, kanbanIds, authId);
        try {
            // カンバンID一覧を指定して、カンバン情報一覧を取得する。
            TypedQuery<KanbanEntity> query = this.em.createNamedQuery("KanbanEntity.findByKanbanIds", KanbanEntity.class);
            query.setParameter("kanbanIds", kanbanIds);

            List<KanbanEntity> kanbans = query.getResultList();

            if (Objects.nonNull(isDetail) && isDetail) {
                for (KanbanEntity kanban : kanbans) {
                    // カンバンが属する階層の階層IDを取得する。
                    Long parentId = this.findParentId(kanban.getKanbanId());
                    if (Objects.nonNull(parentId)) {
                        kanban.setParentId(parentId);
                    }

                    // 詳細情報を取得してセットする。
                    this.getDetails(kanban, true);
                }
            }

            return kanbans;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン追加情報を更新する。
     *　
     * @param entity カンバンプロパティ情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @PUT
    @Path("property")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updateProperty(KanbanPropertyEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("updateProperty: {}, authId={}", entity, authId);

        if (Objects.isNull(entity)
                || Objects.isNull(entity.getFkKanbanId())
                || StringUtils.isEmpty(entity.getKanbanPropertyName())) {
            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
        }

        ServerErrorTypeEnum errorType = ServerErrorTypeEnum.SUCCESS;

        try {
            // カンバンIDを指定して、追加情報を取得する。
            TypedQuery<String> query = this.em.createNamedQuery("KanbanEntity.findAddInfo", String.class);
            query.setParameter("kanbanId", entity.getFkKanbanId());
            query.setMaxResults(1);

            String kabanAddInfo = query.getSingleResult();

            // カンバンの追加情報のJSON文字列を追加情報一覧に変換する。
            List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(kabanAddInfo, AddInfoEntity[].class);

            Optional<AddInfoEntity> opt = addInfos.stream().filter(p -> p.getKey().equals(entity.getKanbanPropertyName())).findFirst();
            if (opt.isPresent()) {
                // 追加情報の値を更新する。
                opt.get().setVal(entity.getKanbanPropertyValue());

            } else {
                // 該当する追加情報がないため、新規に登録する
                AddInfoEntity addInfo = new AddInfoEntity(entity.getKanbanPropertyName(), CustomPropertyTypeEnum.valueOf(entity.getKanbanPropertyType()), entity.getKanbanPropertyValue(), addInfos.size() + 1, null);
                addInfos.add(addInfo);
            }

            // 追加情報一覧をJSON文字列に変換する。
            kabanAddInfo = JsonUtils.objectsToJson(addInfos);

            // カンバンIDを指定して、追加情報を更新する。
            Query updateQuery = this.em.createNamedQuery("KanbanEntity.updateAddInfo");
            updateQuery.setParameter("addInfo", kabanAddInfo);
            updateQuery.setParameter("kanbanId", entity.getFkKanbanId());

            updateQuery.executeUpdate();

        } catch (NoResultException ex) {
            logger.warn(ex, ex);
            errorType = ServerErrorTypeEnum.NOTFOUND_UPDATE;
        }

        return Response.ok().entity(ResponseEntity.success().errorType(errorType)).build();
    }

    /**
     * 納期／備考／入庫日／イベント情報の更新 (デンソー高棚様向け)
     * 
     * @param property カンバン更新情報
     * @param authId 認証ID
     * @return 処理結果
     */
    @PUT
    @Path("property/ds")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updateDsProperty(DsKanbanProperty property, @QueryParam("authId") Long authId) {
        logger.info("updateDsProperty: property={}, authId={}", property, authId);

        if (Objects.isNull(property)
                || Objects.isNull(property.getKanbanId())
                || StringUtils.isEmpty(property.getPropertyName())) {
            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
        }

        final String NOTE = "NOTE";
        final String EVENT = "EVENT";
        final String DUEDATE = "DUEDATE";
        final String INVENTORYDATE = "INVENTORYDATE";
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        
        ServerErrorTypeEnum errorType = ServerErrorTypeEnum.SUCCESS;
        
        try {
            // 組織を取得する。
            OrganizationEntity organization = this.organizationRest.find(authId);
            if (Objects.isNull(organization.getOrganizationId())) {
                logger.fatal("Organization is not found:{}", authId);
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_ORGANIZATION)).build();
            }

            KanbanEntity kanban = this.find(property.getKanbanId(), authId);
            
            switch (property.getPropertyName()) {
                case NOTE:
                case EVENT: {
                        List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(kanban.getKanbanAddInfo(), AddInfoEntity[].class);
                        Optional<AddInfoEntity> opt = addInfos.stream().filter(p -> p.getKey().equals(property.getPropertyName())).findFirst();
                        if (opt.isPresent()) {
                            // 追加情報の値を更新する。
                            opt.get().setVal(property.getPropertyValue());
                        } else {
                            // 該当する追加情報がないため、新規に登録する
                            AddInfoEntity addInfo = new AddInfoEntity(property.getPropertyName(), CustomPropertyTypeEnum.TYPE_STRING, property.getPropertyValue(), addInfos.size() + 1, null);
                            addInfos.add(addInfo);
                        }

                        // 追加情報一覧をJSON文字列に変換する。
                        String kabanAddInfo = JsonUtils.objectsToJson(addInfos);
                        kanban.setKanbanAddInfo(kabanAddInfo);

                        DsKanban dsKanban = DsKanban.lookup(kanban.getServiceInfo());
                        if (Objects.nonNull(dsKanban)) {
                            if (NOTE.equals(property.getPropertyName())) {
                                dsKanban.setNote(property.getPropertyValue());
                            } else if (EVENT.equals(property.getPropertyName())) {
                                dsKanban.setEvent(property.getPropertyValue());
                            }

                            ServiceInfoEntity dsKanbanInfo = new ServiceInfoEntity();
                            dsKanbanInfo.setService(ServiceInfoEntity.SERVICE_INFO_DSKANBAN);
                            dsKanbanInfo.setJob(dsKanban);
                            kanban.setServiceInfo(JsonUtils.objectToJson(Arrays.asList(dsKanbanInfo)));
                        }
                    }
                    break;
                case DUEDATE: {
                        Date date = sdf.parse(property.getPropertyValue());
                        kanban.setCompDatetime(date);
                    } 
                    break;
                case INVENTORYDATE: {
                        Date date = sdf.parse(property.getPropertyValue());
                        kanban.setStartDatetime(date);
                    }
                    break;
                default:
                    errorType = ServerErrorTypeEnum.INVALID_ARGUMENT;
                    break;
            }

            if (ServerErrorTypeEnum.SUCCESS.equals(errorType)) {
                kanban.setUpdatePersonId(authId);
                kanban.setUpdateDatetime(new Date());
                this.em.merge(kanban);
            }

        } catch (NoResultException ex) {
            logger.warn(ex, ex);
            errorType = ServerErrorTypeEnum.NOTFOUND_UPDATE;
        } catch (ParseException ex) {
            logger.warn(ex, ex);
            errorType = ServerErrorTypeEnum.INVALID_ARGUMENT;
        }

        return Response.ok().entity(ResponseEntity.success().errorType(errorType)).build();
    }

    /**
     * 指定されたカンバン情報のフィールドに値を設定する。
     * バージョンを指定すると、バージョンチェックを行なう。
     * 
     * @param kanbanId カンバンID
     * @param fieldName 更新するフィールド名
     * @param value 新しい値
     * @param verInfo カンバン情報のバージョン
     * @param authId 組織ID
     * @return 処理結果
     * @throws URISyntaxException 
     */
    @PUT
    @Path("field/{id}")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updateField(@PathParam("id") Long kanbanId, @QueryParam("field") String fieldName, @QueryParam("val") String value, @QueryParam("verInfo") Integer verInfo, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("updateField: kanbanId={}, field={}, val={}, verInfo={}, authId={}", kanbanId, fieldName, value, verInfo, authId);

        if (Objects.isNull(fieldName) || Objects.isNull(authId)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        try {
            KanbanEntity kanban = super.find(kanbanId);
            if (Objects.isNull(kanban)) {
                // カンバンが見つからない
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_KANBAN)).build();
            }

            if (Objects.nonNull(verInfo) && !kanban.getVerInfo().equals(verInfo)) {
                // 現在の排他用バージョンと異なる
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            switch (StringUtils.toUpperCase(fieldName)) {
                case "PRODUCTNUMBER": // 製造番号
                    kanban.setProductionNumber((String)value);
                    break;
                default:
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            kanban.setUpdatePersonId(authId);
            kanban.setUpdateDatetime(new Date());

            // カンバン情報を更新
            super.edit(kanban);
            
        } catch (Exception ex) {
            logger.warn(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
        }

        return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
    }
    
    /**
     * 任意の条件でカンバンを検索する。
     *
     * @param organizationIds 未使用
     * @param equipmentIds
     * @param modelName
     * @param kanbanStatuses
     * @param fromDate
     * @param toDate
     * @return
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<Long> find(List<Long> organizationIds, List<Long> equipmentIds, String modelName, List<KanbanStatusEnum> kanbanStatuses, Date fromDate, Date toDate) {
        try {
            logger.info("find: {}, {}, {}, {}, {}, {}", organizationIds, equipmentIds, modelName, kanbanStatuses, fromDate, toDate);

            CriteriaBuilder cb = this.em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery(Long.class);

            Root<KanbanEntity> poolKanban = cq.from(KanbanEntity.class);
            jakarta.persistence.criteria.Path<Long> pathKanbanId = poolKanban.get(KanbanEntity_.kanbanId);
            jakarta.persistence.criteria.Path<Date> pathPlanStartTime = poolKanban.get(KanbanEntity_.startDatetime);
            jakarta.persistence.criteria.Path<Date> pathPlanCompTime = poolKanban.get(KanbanEntity_.compDatetime);
            jakarta.persistence.criteria.Path<Date> pathActualStartTime = poolKanban.get(KanbanEntity_.actualStartTime);
            jakarta.persistence.criteria.Path<Date> pathActualCompTime = poolKanban.get(KanbanEntity_.actualCompTime);
            jakarta.persistence.criteria.Path<KanbanStatusEnum> pathKanbanStatus = poolKanban.get(KanbanEntity_.kanbanStatus);
            jakarta.persistence.criteria.Path<String> pathModelName = poolKanban.get(KanbanEntity_.modelName);

            // 対象となる工程カンバンID一覧を取得するサブクエリ
            Subquery<Long> workKanbanIdQuery = cq.subquery(Long.class);
            //if (Objects.nonNull(organizationIds)) {
            //    Root<ConWorkkanbanOrganizationEntity> poolConWorkkanbanOrganization = cq.from(ConWorkkanbanOrganizationEntity.class);
            //    jakarta.persistence.criteria.Path<Long> pathFkWorkKanbanId = poolConWorkkanbanOrganization.get(ConWorkkanbanOrganizationEntity_.conWorkkanbanOrganizationEntityPK).get(ConWorkkanbanOrganizationEntityPK_.fkWorkkanbanId);
            //    jakarta.persistence.criteria.Path<Long> pathFkOrganizationId = poolConWorkkanbanOrganization.get(ConWorkkanbanOrganizationEntity_.conWorkkanbanOrganizationEntityPK).get(ConWorkkanbanOrganizationEntityPK_.fkOrganizationId);
            //    workKanbanIdQuery.select(pathFkWorkKanbanId).distinct(true).where(pathFkOrganizationId.in(organizationIds));
            //}

            if (Objects.nonNull(equipmentIds)) {
                Root<ConWorkkanbanEquipmentEntity> poolConWorkkanbanEquipment = cq.from(ConWorkkanbanEquipmentEntity.class);
                jakarta.persistence.criteria.Path<Long> pathFkWorkKanbanId = poolConWorkkanbanEquipment.get(ConWorkkanbanEquipmentEntity_.workKanbanId);
                jakarta.persistence.criteria.Path<Long> pathFkEquipmentId = poolConWorkkanbanEquipment.get(ConWorkkanbanEquipmentEntity_.equipmentId);
                workKanbanIdQuery.select(pathFkWorkKanbanId).where(pathFkEquipmentId.in(equipmentIds));
            }

            // 対象となるカンバンID一覧を取得するサブクエリ
            Root<WorkKanbanEntity> poolWorkKanban = cq.from(WorkKanbanEntity.class);
            jakarta.persistence.criteria.Path<Long> pathFkKanbanId = poolWorkKanban.get(WorkKanbanEntity_.kanbanId);
            jakarta.persistence.criteria.Path<Long> pathWorkKanbanId = poolWorkKanban.get(WorkKanbanEntity_.workKanbanId);

            Subquery<Long> kanbanIdQuery = cq.subquery(Long.class);
            kanbanIdQuery.select(pathFkKanbanId).where(pathWorkKanbanId.in(workKanbanIdQuery));

            // 検索条件
            List<Predicate> where = new ArrayList();

            // モデル名
            if (!StringUtils.isEmpty(modelName)) {
                where.add(cb.like(pathModelName, StringUtils.escapeLike(modelName)));
            }

            // カンバンステータス
            if (Objects.nonNull(kanbanStatuses)) {
                where.add(pathKanbanStatus.in(kanbanStatuses));
            }

            // 期間
            if (Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
                Predicate fromPlan = cb.or(cb.greaterThanOrEqualTo(pathPlanStartTime, fromDate), cb.greaterThanOrEqualTo(pathPlanCompTime, fromDate));
                Predicate toPlan = cb.or(cb.lessThanOrEqualTo(pathPlanStartTime, toDate), cb.lessThanOrEqualTo(pathPlanCompTime, toDate));
                Predicate termPlan = cb.and(fromPlan, toPlan);

                where.add(termPlan);
            }

            where.add(pathKanbanId.in(kanbanIdQuery));

            cq.select(pathKanbanId).where(cb.and(where.toArray(new Predicate[where.size()])));

            Query query = this.em.createQuery(cq);

            return query.getResultList();
        } finally {
            logger.info("find end.");
        }
    }

    /**
     * ユニークID一覧を指定して、該当する製品情報を持つカンバン情報を取得する。
     *
     * @param uniqueIds ユニークID一覧
     * @param authId 認証ID
     * @return カンバン情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("uid")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public KanbanEntity findByUID(@QueryParam("uid") List<String> uniqueIds, @QueryParam("authId") Long authId) {
        logger.info("findUID: uniqueIds={}, authId={}", uniqueIds, authId);
        try {
            if (Objects.isNull(uniqueIds) || uniqueIds.isEmpty()) {
                return null;
            }

            // ユニークID一覧を指定して、製品情報一覧を取得する。(製品IDの逆順)
            TypedQuery<ProductEntity> query = this.em.createNamedQuery("ProductEntity.findByUID", ProductEntity.class);
            query.setParameter("uniqueIds", uniqueIds);
            List<ProductEntity> products = query.getResultList();

            // カンバンステータスが「計画中」以外のカンバンを検索する。
            KanbanEntity kanban = null;
            for (ProductEntity product : products) {
                if (Objects.nonNull(product.getFkKanbanId())) {
                    KanbanEntity k = this.find(product.getFkKanbanId(), authId);
                    if (Objects.nonNull(k.getKanbanId()) && k.getKanbanStatus() != KanbanStatusEnum.PLANNING) {
                        kanban = k;
                        break;
                    }
                }
            }

            return kanban;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 条件を指定して、カンバン情報が存在するか確認する。
     *
     * @param entity 条件
     * @return 存在するか (true:存在する, false:存在しない)
     */
    @Lock(LockType.READ)
    private boolean existKanban(KanbanExistEntity entity) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<KanbanEntity> poolKanban = cq.from(KanbanEntity.class);

        jakarta.persistence.criteria.Path<Long> pathKanbanId = poolKanban.get(KanbanEntity_.kanbanId);
        jakarta.persistence.criteria.Path<String> pathKanbanName = poolKanban.get(KanbanEntity_.kanbanName);
        jakarta.persistence.criteria.Path<String> pathKanbanSubname = poolKanban.get(KanbanEntity_.kanbanSubname);
        jakarta.persistence.criteria.Path<Long> pathWorkflowId = poolKanban.get(KanbanEntity_.workflowId);

        // 検索条件
        List<Predicate> where = new LinkedList();

        // カンバンID
        if (Objects.isNull(entity.getKanbanId())) {
            where.add(cb.isNotNull(pathKanbanId));
        } else {
            where.add(cb.notEqual(pathKanbanId, entity.getKanbanId()));
        }

        // カンバン名
        if (Objects.isNull(entity.getKanbanName())) {
            where.add(cb.isNull(pathKanbanName));
        } else {
            where.add(cb.equal(pathKanbanName, entity.getKanbanName()));
        }

        // サブカンバン名
        if (Objects.isNull(entity.getKanbanSubname())) {
            where.add(cb.isNull(pathKanbanSubname));
        } else {
            where.add(cb.equal(pathKanbanSubname, entity.getKanbanSubname()));
        }

        // 工程順ID
        if (Objects.isNull(entity.getFkWorkflowId())) {
            where.add(cb.isNull(pathWorkflowId));
        } else {
            where.add(cb.equal(pathWorkflowId, entity.getFkWorkflowId()));
        }

        cq.select(cb.count(pathKanbanId))
                .where(cb.and(where.toArray(new Predicate[where.size()])));

        TypedQuery<Long> query = this.em.createQuery(cq);

        Long count = query.getSingleResult();
        if (count > 0) {
            return true;
        }

        return false;
    }

    /**
     * ライン生産が開始された。
     *
     * @param equipmentId
     * @param date
     * @param modelName
     */
    private void startLine(Long equipmentId, Date date, String modelName) {
        try {
            EquipmentEntity equipment = this.equipmentRest.find(equipmentId, null);

            boolean isStarted = false;
            Date now = new Date();

            for (AndonMonitorLineProductSetting setting : this.lineManager.getLineSetting()) {
                if (!setting.getLineId().equals(equipment.getParentEquipmentId()) || !setting.isFollowStart()) {
                    continue;
                }

                if (!StringUtils.like(modelName, setting.getModelName())) {
                    continue;
                }

                Instant instant = setting.getStartWorkTime().atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
                Date startWorkTime = Date.from(instant);
                if (setting.getTodayStartTime().after(startWorkTime)) {
                    continue;
                }

                if (startWorkTime.after(now)) {
                    continue;
                }

                // 本日の作業開始時間を保存
                setting.setTodayStartTime(now);
                logger.info("Today's start time: {}, {}, {}, {}", setting.getMonitorId(), setting.getLineId(), equipmentId, now);

                isStarted = true;

                // 自動カウントダウン
                if (setting.isAutoCountdown()) {
                    LineTimerControlRequest setup = new LineTimerControlRequest(setting.getMonitorId(), LineManagedCommandEnum.SETUP, now, 0L, (long) setting.getLineTakt().toSecondOfDay());
                    this.lineFacade.postDailyTimerControlRequest(setting.getMonitorId(), setup, now, setting);

                    MonitorLineTimerInfoEntity lineTimer = this.lineManager.getLineTimer(setting.getMonitorId());
                    if (Objects.isNull(lineTimer)) {
                        continue;
                    }

                    int cycle = Integer.MAX_VALUE;

                    Date fromDate = DateUtils.getBeginningOfDate(now);
                    Date toDate = DateUtils.getEndOfDate(now);
                    for (WorkEquipmentSetting workEquipment : setting.getWorkEquipmentCollection()) {
                        if (!workEquipment.getEquipmentIds().isEmpty()) {
                            long id = workEquipment.getEquipmentIds().get(0);

                            ReportOutSearchCondition condition = new ReportOutSearchCondition()
                                    .equipmentIdList(workEquipment.getEquipmentIds())
                                    .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
                                    .fromDate(fromDate).toDate(toDate)
                                    .modelName(setting.getModelName());

                            int actualNum = Integer.parseInt(this.actualResultRest.countReportOut(condition, null));

                            lineTimer.delivered().put(id, actualNum);
                            logger.info("Delivered: {},{}", id, actualNum);

                            cycle = Math.min(cycle, actualNum);
                        }
                    }

                    if (cycle < Integer.MAX_VALUE) {
                        lineTimer.setCycle(cycle);
                        logger.info("Cycle: {}", cycle);
                    }

                    if (setting.getDailyPlanNum() <= lineTimer.getCycle()) {
                        // 既に計画数を達成しているため、カウントダウンを開始しない
                        logger.info("Achieved the plan. Countdown has over.");
                        continue;
                    }

                    if (lineTimer.getLineTimerState() == LineManagedStateEnum.STOP) {
                        // カウントダウンリセット
                        LineTimerControlRequest reset = new LineTimerControlRequest(setting.getMonitorId(), LineManagedCommandEnum.RESET, now, 0L, (long) setting.getLineTakt().toSecondOfDay());
                        this.lineFacade.postDailyTimerControlRequest(setting.getMonitorId(), reset, now, setting);
                    }

                    // カウントダウン開始
                    LineTimerControlRequest start = new LineTimerControlRequest(setting.getMonitorId(), LineManagedCommandEnum.START, now, 0L, (long) setting.getLineTakt().toSecondOfDay());
                    this.lineFacade.postDailyTimerControlRequest(setting.getMonitorId(), start, now, setting);
                    logger.info("Countdown started.");
                }
            }

            if (isStarted) {
                // リセット通知
                this.adIntefaceFacade.notice(new ResetCommand());
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * ライン生産が完了した。
     *
     * @param equipmentId 作業者端末の設備ID
     * @patam modelName モデル名
     * @param productNum 完成数
     * @param kanbanStatus カンバンステータス
     */
    private void endLine(Long equipmentId, String modelName, int productNum, KanbanStatusEnum kanbanStatus) {
        try {
            logger.info("endLine: {}, {}, {}", equipmentId, modelName, productNum);

            if (Objects.isNull(equipmentId)) {
                return;
            }

            if (1 > productNum) {
                // カンバンのロット数量が0の場合があるため
                productNum = 1;
            }
            
            EquipmentEntity equipment = this.equipmentRest.find(equipmentId, null);

            for (AndonMonitorLineProductSetting setting : this.lineManager.getLineSetting()) {
                if (Objects.isNull(setting.getCompCountType())) {
                    continue;
                }

                if (!setting.getLineId().equals(equipment.getParentEquipmentId()) || !setting.isAutoCountdown()) {
                    // 自動カウントダウンが無効
                    continue;
                }

                if (!StringUtils.like(modelName, setting.getModelName())) {
                    // 対象外のモデル
                    continue;
                }

                MonitorLineTimerInfoEntity lineTimer = this.lineManager.getLineTimer(setting.getMonitorId());
                if (Objects.isNull(lineTimer)) {
                    logger.info("LineTimer is null.");
                    continue;
                }
                
                boolean isCompleted = true;
                int actualNum = 0;
                
                switch (setting.getCompCountType()) {
                    case WORK:
                        if (Objects.nonNull(setting.getWorkCollection())) {
                            Date now = new Date();
                            Set<Long> workIds = setting.getWorkCollection().stream().flatMap(o -> o.getWorkIds().stream()).collect(Collectors.toSet());
                            actualNum = this.actualResultRest.getActualNum(setting.getCompCountType(), new ArrayList<>(workIds), DateUtils.getBeginningOfDate(now), DateUtils.getEndOfDate(now), setting.getModelName());
                            if (actualNum <= lineTimer.getCycle()) {
                                isCompleted = false;
                            }
                        }   
                        break;

                    case EQUIPMENT:
                        Integer delivered = lineTimer.delivered().get(equipmentId);
                        if (Objects.nonNull(delivered)) {
                            lineTimer.delivered().put(equipmentId, delivered + productNum);
                        } else {
                            lineTimer.delivered().put(equipmentId, productNum);
                        }

                        actualNum = lineTimer.getCycle() + productNum;

                        for (Integer num : lineTimer.delivered().values()) {
                            if (num < actualNum) {
                                isCompleted = false;
                                break;
                            }
                        } 
                        break;

                    case KANBAN:
                    default:
                        isCompleted = KanbanStatusEnum.COMPLETION.equals(kanbanStatus);
                        actualNum = lineTimer.getCycle() + productNum;
                        break;
                }
                
                logger.info("endLine: isCompleted={} cycleNum={} actualNum={}", isCompleted, lineTimer.getCycle(), actualNum);
                
                if (isCompleted) {
                    // 進捗モニターのカウントダウンをリセットする
                    Date now = new Date();
                    lineTimer.setCycle(actualNum);
                    lineTimer.setCompTime(now.getTime());

                    logger.info("Countdown status: " + lineTimer.getLineTimerState());

                    if (lineTimer.getLineTimerState() == LineManagedStateEnum.TAKTCOUNT || lineTimer.getLineTimerState() == LineManagedStateEnum.STARTCOUNT) {
                        LineTimerControlRequest stop = new LineTimerControlRequest(setting.getMonitorId(), LineManagedCommandEnum.STOP, now, 0L, (long) setting.getLineTakt().toSecondOfDay());
                        this.lineFacade.postDailyTimerControlRequest(setting.getMonitorId(), stop, now, setting);
                        lineTimer = this.lineManager.getLineTimer(setting.getMonitorId());
                    }

                    if (lineTimer.getLineTimerState() == LineManagedStateEnum.STOP || lineTimer.getLineTimerState() == LineManagedStateEnum.START_WAIT) {
                        if (setting.getDailyPlanNum() > actualNum) {
                            LineTimerControlRequest reset = new LineTimerControlRequest(setting.getMonitorId(), LineManagedCommandEnum.RESET, now, 0L, (long) setting.getLineTakt().toSecondOfDay());
                            this.lineFacade.postDailyTimerControlRequest(setting.getMonitorId(), reset, now, setting);

                            LineTimerControlRequest start = new LineTimerControlRequest(setting.getMonitorId(), LineManagedCommandEnum.START, now, 0L, (long) setting.getLineTakt().toSecondOfDay());
                            this.lineFacade.postDailyTimerControlRequest(setting.getMonitorId(), start, now, setting);

                            logger.info("Countdown started: " + actualNum);
                        } else {
                            logger.info("Achieved the plan. Countdown is over.");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 指定されたカンバン階層IDのカンバン情報一覧を取得する。
     *
     * @param parentId カンバン階層ID
     * @param kanbanName カンバン名
     * @param modelName モデル名
     * @param productNo 製造番号
     * @param fromDate 開始予定日
     * @param toDate 終了予定日
     * @param statusNames カンバンステータス
     * @param authId 認証ID
     * @return カンバン情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("parent/{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanEntity> findByParentId(@PathParam("id") Long parentId, @QueryParam("name") String kanbanName, @QueryParam("modelName") String modelName, 
            @QueryParam("productNo") String productNo, @QueryParam("workflowName") String workflwonName, @QueryParam("workName") String workName, @QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, @QueryParam("status") List<String> statusNames, @QueryParam("authId") Long authId) throws Exception {
        logger.info("findByParentId: parentId={}, kanbanName={}, modelName={}, productNo={}, workflowName={}, workName={}, fromDate={}, toDate={}, statusNames={}, authId={}", parentId, kanbanName, modelName, productNo, workflwonName, workName, fromDate, toDate, statusNames, authId);
        try {

            // 作業予定日
            Date fromDateTime = this.stringToDate(fromDate, pattern, null);
            if (Objects.nonNull(fromDateTime)) {
                fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);
            }
            Date toDateTime = this.stringToDate(toDate, pattern, null);
            if (Objects.nonNull(toDateTime)) {
                toDateTime = DateUtils.getEndOfDate(toDateTime);
            }

            // カンバンステータス
            List<KanbanStatusEnum> kanbanStatus = new ArrayList();
            if (Objects.nonNull(statusNames)) {
                for (String statusName : statusNames) {
                    kanbanStatus.add(KanbanStatusEnum.valueOf(statusName));
                }
            }

            CriteriaBuilder cb = this.em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();

            Root<KanbanEntity> poolKanban = cq.from(KanbanEntity.class);
            Join<KanbanEntity, WorkflowEntity> poolWorkflow = poolKanban.join(KanbanEntity_.workflow);

            // 指定されたカンバン階層のカンバンID一覧を取得するサブクエリ
            Subquery<Long> subquery = cq.subquery(Long.class);
            Root<ConKanbanHierarchyEntity> poolConKanbanHierarchy = subquery.from(ConKanbanHierarchyEntity.class);

            jakarta.persistence.criteria.Path<Long> pathConParentId = poolConKanbanHierarchy.get(ConKanbanHierarchyEntity_.kanbanHierarchyId);
            jakarta.persistence.criteria.Path<Long> pathConKanbanId = poolConKanbanHierarchy.get(ConKanbanHierarchyEntity_.kanbanId);

            Root<WorkEntity> poolWorkEntity = cq.from(WorkEntity.class);
            jakarta.persistence.criteria.Path<String> pathWorkName = poolWorkEntity.get(WorkEntity_.workName);
            jakarta.persistence.criteria.Path<Long> pathWorkId = poolWorkEntity.get(WorkEntity_.workId);

            Root<WorkKanbanEntity> poolWorkKanban = cq.from(WorkKanbanEntity.class);
            jakarta.persistence.criteria.Path<Long> pathWKKanbanId = poolWorkKanban.get(WorkKanbanEntity_.kanbanId);
            jakarta.persistence.criteria.Path<Long> pathWkWorkId = poolWorkKanban.get(WorkKanbanEntity_.workId);

            // 項目のパス
            jakarta.persistence.criteria.Path<Long> pathKanbanId = poolKanban.get(KanbanEntity_.kanbanId);
            jakarta.persistence.criteria.Path<String> pathKanbanName = poolKanban.get(KanbanEntity_.kanbanName);
            jakarta.persistence.criteria.Path<String> pathModelName = poolKanban.get(KanbanEntity_.modelName);
            jakarta.persistence.criteria.Path<String> pathProductionNumber = poolKanban.get(KanbanEntity_.productionNumber);
            jakarta.persistence.criteria.Path<Date> pathStartDatetime = poolKanban.get(KanbanEntity_.startDatetime);
            jakarta.persistence.criteria.Path<Date> pathCompDatetime = poolKanban.get(KanbanEntity_.compDatetime);
            jakarta.persistence.criteria.Path<KanbanStatusEnum> pathKanbanStatus = poolKanban.get(KanbanEntity_.kanbanStatus);

            jakarta.persistence.criteria.Path<String> pathWorkflowName = poolWorkflow.get(WorkflowEntity_.workflowName);
            jakarta.persistence.criteria.Path<Integer> pathWorkflowRev = poolWorkflow.get(WorkflowEntity_.workflowRev);

            // 検索条件
            List<Predicate> where = new ArrayList();

            // トップ階層以外は、指定された階層の階下を検索対象とする
            List<Long> kanbanHierarchyIds = new ArrayList<>();
            kanbanHierarchyIds.add(parentId);

            if (Objects.nonNull(authId) && (
                !StringUtils.isEmpty(kanbanName)
                || !StringUtils.isEmpty(modelName)
                || !StringUtils.isEmpty(productNo)
                || !StringUtils.isEmpty(workflwonName)
                || !StringUtils.isEmpty(workName)
                || (Objects.nonNull(statusNames) && !statusNames.isEmpty()))) {

                // ユーザーのルートまでの親階層ID一覧を取得する。
                List<Long> organizationIds = this.organizationRest.findAncestors(authId);
                java.sql.Array idArray = this.em.unwrap(Connection.class).createArrayOf("bigint", organizationIds.toArray());

                TypedQuery<Long> findTreeChild = this.em.createNamedQuery("KanbanHierarchyEntity.findTreeChild", Long.class);
                findTreeChild.setParameter(1, parentId);
                findTreeChild.setParameter(2, idArray);

                kanbanHierarchyIds.addAll(findTreeChild.getResultList());
            }

            subquery.select(pathConKanbanId)
                    .where(pathConParentId.in(kanbanHierarchyIds));
            where.add(pathKanbanId.in(subquery));
            
            // カンバン名
            if (Objects.nonNull(kanbanName) && !kanbanName.isEmpty()) {
                where.add(cb.or(
                                cb.like(cb.lower(pathKanbanName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(kanbanName)) + "%"),
                                cb.like(pathKanbanName, "%" + StringUtils.escapeLikeChar(kanbanName) + "%")
                ));
            }

            // モデル名
            if (Objects.nonNull(modelName) && !modelName.isEmpty()) {
                where.add(cb.or(
                        cb.like(cb.lower(pathModelName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(modelName)) + "%"),
                        cb.like(pathModelName, "%" + StringUtils.escapeLikeChar(modelName) + "%")
                ));
            }

            // 製造番号
            if (Objects.nonNull(productNo)&& !productNo.isEmpty()) {
                where.add(cb.or(
                        cb.like(cb.lower(pathProductionNumber), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(productNo)) + "%"),
                        cb.like(pathProductionNumber, "%" + StringUtils.escapeLikeChar(productNo) + "%")
                ));
            }

            if (!StringUtils.isEmpty(workflwonName)) {
                where.add(cb.or(
                        cb.like(cb.lower(pathWorkflowName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(workflwonName)) + "%"),
                        cb.like(pathWorkflowName, "%" + StringUtils.escapeLikeChar(workflwonName) + "%")
                ));
            }

            if (!StringUtils.isEmpty(workName)) {
                Subquery<Long> workSubquery = cq.subquery(Long.class)
                        .select(pathWKKanbanId)
                        .where(pathWkWorkId.in(cq.subquery(Long.class)
                                .select(pathWorkId)
                                .where(cb.or(
                                        cb.like(cb.lower(pathWorkName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(workName)) + "%"),
                                        cb.like(pathWorkName, "%" + StringUtils.escapeLikeChar(workName) + "%")))));
                where.add(pathKanbanId.in(workSubquery));
            }

            // 作業予定日
            if (Objects.nonNull(fromDateTime)) {
                where.add(cb.or(
                        cb.greaterThanOrEqualTo(pathStartDatetime, fromDateTime),
                        cb.greaterThanOrEqualTo(pathCompDatetime, fromDateTime)
                ));
            }
            if (Objects.nonNull(toDateTime)) {
                where.add(cb.or(
                        cb.lessThanOrEqualTo(pathStartDatetime, toDateTime),
                        cb.lessThanOrEqualTo(pathCompDatetime, toDateTime)
                ));
            }

            // カンバンステータス
            if (!kanbanStatus.isEmpty()) {
                where.add(pathKanbanStatus.in(kanbanStatus));
            }

            Expression<Long> literalParentId = cb.literal(parentId);

            TypedQuery<KanbanEntity> query = this.em.createQuery(cq.select(cb.construct(KanbanEntity.class, poolKanban, literalParentId, pathWorkflowName, pathWorkflowRev))
                    .where(where.toArray(new Predicate[where.size()]))
                    .orderBy(cb.asc(pathStartDatetime), cb.asc(pathKanbanName), cb.asc(pathKanbanId)));

            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     *
     * @param dateString
     * @param dateFormat
     * @param defaultValue
     * @return
     */
    @Lock(LockType.READ)
    private Date stringToDate(String dateString, String dateFormat[], Date defaultValue) {
        try {
            return Objects.nonNull(dateString) ? org.apache.commons.lang3.time.DateUtils.parseDate(dateString, dateFormat) : defaultValue;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return defaultValue;
        }
    }

    /**
     * カンバンの計画時間を変更する。
     *
     * @param condition 計画時間変更条件
     * @param kanbanIds カンバンID一覧
     * @param authId 認証ID(更新者)
     * @return 結果
     */
    @Path("plan")
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updatePlan(PlanChangeCondition condition, @QueryParam("id") final List<Long> kanbanIds, @QueryParam("authId") Long authId) {
        logger.info("updatePlan: condition={}, kanbanIds={}, authId={}", condition, kanbanIds, authId);
        try {
            for (Long kanbanId : kanbanIds) {
                KanbanEntity kanban = super.find(kanbanId);

                // 通常工程の工程カンバン情報を取得してセットする。
                kanban.setWorkKanbanCollection(this.getWorkKanban(kanbanId));
                // 追加工程の工程カンバン情報を取得してセットする。
                kanban.setSeparateworkKanbanCollection(this.getSeparateWorkKanban(kanbanId));

                kanban.setUpdatePersonId(authId); // 更新者
                kanban.setUpdateDatetime(new Date()); // 更新日

                Date baseDatetime;
                if (Objects.nonNull(condition.getStartDatetime())) {
                    baseDatetime = condition.getStartDatetime();
                } else if (Objects.nonNull(kanban.getStartDatetime())) {
                    baseDatetime = kanban.getStartDatetime();
                } else {
                    baseDatetime = DateUtils.min();
                }

                // 休憩時間一覧を取得する。
                List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList();

                Set<Long> organizationIds = new LinkedHashSet();
                for (WorkKanbanEntity workKanban : kanban.getWorkKanbanCollection()) {
                    organizationIds.addAll(workKanban.getOrganizationCollection());
                }

                Set<Long> breaktimeIds = new HashSet();
                for (Long organizationId : organizationIds) {
                    breaktimeIds.addAll(this.organizationRest.getBreaktimes(organizationId));
                }

                if (!breaktimeIds.isEmpty()) {
                    List<BreaktimeEntity> breakTimes = this.breaktimeRest.find(new ArrayList(breaktimeIds));
                    for (BreaktimeEntity breakTime : breakTimes) {
                        breaktimeCollection.add(new BreakTimeInfoEntity(breakTime.getBreaktimeId(), breakTime.getBreaktimeName(), breakTime.getStarttime(), breakTime.getEndtime()));
                    }
                }

                // 休憩時間一覧に中断時間を追加する。
                if (Objects.nonNull(condition.getInterruptFromTime()) && Objects.nonNull(condition.getInterruptToTime())) {
                    BreakTimeInfoEntity interruptTime = new BreakTimeInfoEntity(0L, "interrupt", condition.getInterruptFromTime(), condition.getInterruptToTime());
                    breaktimeCollection.add(interruptTime);
                }

                // 休日
                HolidaySearchCondition holidayCondition = new HolidaySearchCondition()
                        .fromDate(DateUtils.getBeginningOfDate(baseDatetime));
                List<HolidayEntity> holidays = this.holidayRest.searchHoliday(holidayCondition, authId);

                WorkflowEntity workflow = this.workflowRest.find(kanban.getWorkflowId(), null, authId);

                // カンバンの基準時間を更新
                WorkflowProcess workflowProcess = new WorkflowProcess(workflow);
                workflowProcess.setOrganizationRest(this.organizationRest);
                workflowProcess.setBaseTime(kanban, breaktimeCollection, baseDatetime, holidays);

                // 工程カンバンを更新
                for (WorkKanbanEntity workKanban : kanban.getWorkKanbanCollection()) {
                    this.workKandanRest.editWorkKanban(workKanban);
                }
            }

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 当日開始のカンバン数を取得する。
     *
     * @param fromDate 開始日
     * @param toDate 終了日
     * @param authId 認証ID
     * @return カンバン数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count/daily")
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countKanban(@QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("authId") Long authId) {
        logger.info("countKanban: {} {}", fromDate, toDate);

        try {
            Date fromDateTime = Objects.nonNull(fromDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern) : new Date();
            fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);

            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();
            toDateTime = DateUtils.getEndOfDate(toDateTime);

            Query query = this.em.createNamedQuery("KanbanEntity.countKanban", Long.class);
            query.setParameter("fromDate", fromDateTime, TemporalType.TIMESTAMP);
            query.setParameter("toDate", toDateTime, TemporalType.TIMESTAMP);

            List<Long> hierarchy = kanbanHierarchyRest.getKanbanHierarchyIdsByName("adFactoryReport");
            query.setParameter("kanbanHierarchyIds", hierarchy.isEmpty() ? Collections.singletonList(0) : hierarchy);

            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return "0";
        }
    }

    /**
     * カンバンプロパティ一覧を取得する。
     *
     * @param condition プロパティ検索条件 (親ID一覧(カンバンID一覧), プロパティ名)
     * @param authId 認証ID
     * @return カンバンプロパティ一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("property/search")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanPropertyInfoEntity> searchKanbanProperties(PropertySearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("searchKanbanProperties: {}", condition);
        try {
            List<KanbanPropertyInfoEntity> propInfos = new ArrayList();

            // カンバンID一覧を指定して、カンバン情報一覧を取得する。
            List<KanbanEntity> kanbans = this.findByKanbanId(condition.getParentIdList(), null, authId);
            for (KanbanEntity kanban : kanbans) {
                // カンバンの追加情報のJSON文字列を追加情報一覧に変換する。
                List<KanbanPropertyInfoEntity> props = JsonUtils.jsonToObjects(kanban.getKanbanAddInfo(), KanbanPropertyInfoEntity[].class);
                if (props.isEmpty()) {
                    continue;
                }

                for (KanbanPropertyInfoEntity prop : props) {
                    prop.setFkKanbanId(kanban.getKanbanId());
                }

                propInfos.addAll(props);
            }

            return propInfos;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン検索種別によりカンバン情報を取得する。
     *
     * @param searchType カンバン検索種別
     * @param code バーコード文字列
     * @param workflowName 工程順名
     * @param workflowRev 工程順版数
     * @param equipmentId 設備ID
     * @param organizationId 組織ID
     * @param authId 認証ID
     * @return カンバン情報
     * @throws Exception 
     */
    @Lock(LockType.READ)
    @GET
    @Path("type")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public KanbanEntity findByKanbanSearchType(@QueryParam("type") String searchType, @QueryParam("code") String code, @QueryParam("workflowName") String workflowName, @QueryParam("rev") Integer workflowRev, @QueryParam("equipment") Long equipmentId, @QueryParam("organization") Long organizationId, @QueryParam("authId") Long authId) throws Exception {
        try {
            logger.info("findByKanbanSearchType: searchType={}, code={}, workflowName={}, workflowRev={}, equipmentId={}, organization={}", searchType, code, workflowName, workflowRev, equipmentId, organizationId);

            KanbanSearchTypeEnum kanbanSearchType = KanbanSearchTypeEnum.getEnum(searchType);

            switch (kanbanSearchType) {
                case B:
                    // 現品票の検索
                    return this.findByKanbanSearchTypeB(code, workflowName, workflowRev, equipmentId, organizationId);
                case A:
                default:
                    // カンバン名で検索 (設備・組織の割り当てに関係なく取得される)
                    return this.findByName(code, workflowName, workflowRev, authId);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        } finally {
            logger.info("findByKanbanSearchType end.");
        }
    }

    /**
     * カンバン検索種別「タイプB」でカンバン情報を取得する。
     *
     * @param code バーコード文字列
     * @param workflowName 工程順名
     * @param workflowRev 工程順版数
     * @param equipmentId 設備ID
     * @param organizationId 組織ID
     * @return カンバン情報
     * @throws Exception 
     */
    @Lock(LockType.READ)
    private KanbanEntity findByKanbanSearchTypeB(String code, String workflowName, Integer workflowRev, Long equipmentId, Long organizationId) throws Exception {
        // カンバンを取得する。
        TypedQuery<KanbanEntity> query = null;
        if (!StringUtils.isEmpty(workflowName)) {
            // 工程順の指定あり
            query = em.createNamedQuery("KanbanEntity.findProductTypeBByWorkflowId", KanbanEntity.class);
            query.setParameter("workflowId", this.workflowRest.findIdByName(workflowName, workflowRev));
        } else {
            // 工程順の指定なし
            query = em.createNamedQuery("KanbanEntity.findProductTypeB", KanbanEntity.class);
        }

        // カンバン名はLIKE用に記号の前にエスケープ文字を追加して、前方一致のため末尾に「%」を追加する。
        String kanbanName = new StringBuilder(StringUtils.escapeLikeChar(code)).append("%").toString();

        List<Long> hierarchy = kanbanHierarchyRest.getKanbanHierarchyIdsByName("adFactoryReport");
        query.setParameter("kanbanHierarchyIds", hierarchy.isEmpty() ? Collections.singletonList(0) : hierarchy);

        query.setParameter("kanbanName", kanbanName);
        query.setParameter("equipmentIds", this.equipmentRest.getEquipmentPerpetuity(equipmentId));
        query.setParameter("organizationIds", this.organizationRest.getOrganizationPerpetuity(organizationId));

        List<KanbanEntity> entities = query.getResultList();
        if (entities.isEmpty()) {
            return new KanbanEntity();
        }

        KanbanEntity kanban = entities.get(0);// 作業対象カンバン (号機番号付きのカンバンが無い場合、最初のカンバンが対象)

        // カンバン名は「<現品票のバーコード> #<号機番号> <生産日>」

        Integer minNum = null;// 最小号機番号
        for (KanbanEntity entity : entities) {
            String[] buf = entity.getKanbanName().split(" ", 3);

            // 号機番号をチェックする。
            if (buf.length > 1 && buf[1].length() > 0 && buf[1].substring(0, 1).equals("#")) {
                // 2ブロック目の文字列について、「#」より後の文字列が数値の場合、現在の最小号機番号より小さかったら作業対象カンバンを変更する。
                String snum = buf[1].substring(1);
                if (StringUtils.isInteger(snum)) {
                    int num = Integer.parseInt(snum);
                    if (Objects.isNull(minNum) || num < minNum) {
                        minNum = num;
                        kanban = entity;
                    }
                }
            }
        }

        // カンバンが属する階層の階層IDを取得する。
        Long parentId = this.findParentId(kanban.getKanbanId());
        if (Objects.nonNull(parentId)) {
            kanban.setParentId(parentId);
        }

        // 詳細情報を取得してセットする。
        this.getDetails(kanban, true);

        return kanban;
    }

    /**
     * ステータスを更新する。
     * 
     * @param kanbanIds カンバンID一覧
     * @param status カンバンステータス
     * @param cancel 強制中断
     * @param authId 認証ID
     * @return 処理結果(更新者)
     */
    @PUT
    @Path("status")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updateStatus(@QueryParam("id") List<Long> kanbanIds, @QueryParam("status") String status, @QueryParam("cancel") Boolean cancel, @QueryParam("authId") Long authId) {
        return this.updateStatus(kanbanIds, status, cancel, null, authId);
    }

    /**
     * ステータスを更新する。
     * 
     * @param kanbanIds カンバンID一覧
     * @param status カンバンステータス
     * @param cancel 強制中断
     * @param isRemoveKanbanReport カンバン帳票情報を削除する？(nullの場合はtrue扱い)
     * @param authId 認証ID(更新者)
     * @return 処理結果
     */
    public Response updateStatus(List<Long> kanbanIds, String status, Boolean cancel, Boolean isRemoveKanbanReport, Long authId) {
        logger.info("updateStatus: kanbanIds={}, status={}, cancel={}", kanbanIds, status, cancel);

        if (Objects.isNull(kanbanIds) || kanbanIds.isEmpty()) {
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        if (StringUtils.isEmpty(status)) {
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        boolean isCancel = Objects.nonNull(cancel)? cancel : false;

        List<KanbanEntity> kanbans = this.findByKanbanId(kanbanIds, null, null);
        if (kanbans.size() != kanbanIds.size()) {
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_KANBAN)).build();
        }

        KanbanStatusEnum newStatus = KanbanStatusEnum.getEnum(status);
        Date now = new Date();
        int errorCount = 0;

        try {
            switch (newStatus) {
                case PLANNING:
                    this.planingKanban(kanbans, isCancel, isRemoveKanbanReport, now, authId);
                    break;

                case PLANNED:
                    for (KanbanEntity kanban : kanbans) {
                        boolean apply = true;
                        Long workId = null;
                        kanban = this.find(kanban.getKanbanId(), null);

                        switch (kanban.getKanbanStatus()) {
                            case PLANNING:
                            case PLANNED:
                                break;
                            case WORKING:
                            case SUSPEND:
                                if (isCancel) {
                                    apply = this.cancelWork(kanban, authId);
                                }  else {
                                    apply = this.checkStatusWorkKanban(kanban);
                                }

                                if (apply) {
                                    // 最後の完了実績を取得
                                    ActualSearchCondition conditon = new ActualSearchCondition()
                                        .kanbanId(kanban.getKanbanId())
                                        .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
                                        .fromDate(kanban.getActualStartTime())
                                        .resultDailyEnum(ActualResultDailyEnum.ALL);

                                    ActualResultEntity lastActualResult = this.actualResultRest.findLastActualResult(conditon, null);
                                    if (Objects.nonNull(lastActualResult)) {
                                        workId = lastActualResult.getWorkId();
                                    }
                                }
                                break;
                            case COMPLETION:
                            case INTERRUPT:
                            case DEFECT:
                                if (isCancel) {
                                    apply = this.cancelWork(kanban, authId);
                                }

                                if (apply) {
                                    // 最後の完了実績を取得
                                    ActualSearchCondition conditon = new ActualSearchCondition()
                                        .kanbanId(kanban.getKanbanId())
                                        .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
                                        .fromDate(kanban.getActualStartTime())
                                        .resultDailyEnum(ActualResultDailyEnum.ALL);

                                    ActualResultEntity lastActualResult = this.actualResultRest.findLastActualResult(conditon, null);
                                    if (Objects.nonNull(lastActualResult)) {
                                        workId = lastActualResult.getWorkId();
                                    }
                                }
                                break;
                        }

                        if (!apply) {
                            errorCount++;
                            continue;
                        }

                        // 工程順を進める
                        WorkflowEntity workflow = this.workflowRest.findBasicInfo(kanban.getWorkflowId());
                        this.startWorkflow(kanban, workflow, workId);

                        // 20/06/12 カンバン編集画面にて入力された計画時間が更新されてしまうため、計画時間を更新しない s-heya
                        // this.updateBaseTime(kanban, now);

                        kanban.setKanbanStatus(newStatus);
                        kanban.setUpdatePersonId(authId);
                        kanban.setUpdateDatetime(now);
                        this.edit(kanban);
                    }
                    break;

                case COMPLETION:
                    for (KanbanEntity kanban : kanbans) {
                        boolean apply = true;
                        switch (kanban.getKanbanStatus()) {
                            case PLANNING:
                            case PLANNED:
                                if (!isCancel) {
                                    apply = false;
                                }
                                break;
                            case INTERRUPT:
                            case WORKING:
                            case SUSPEND:
                                kanban.setWorkKanbanCollection(this.getWorkKanban(kanban.getKanbanId()));
                                kanban.setSeparateworkKanbanCollection(this.getSeparateWorkKanban(kanban.getKanbanId()));
                                if (isCancel) {
                                    apply = this.cancelWork(kanban, authId);
                                }
                                break;
                            case COMPLETION:

                            case DEFECT:
                                apply = false;
                                break;
                        }

                        if (!apply) {
                            errorCount++;
                            continue;
                        }
                        
                        if (Objects.isNull(kanban.getActualCompTime())) {
                            // 実績完了日時
                            kanban.setActualCompTime(now);
                        }

                        kanban.setKanbanStatus(newStatus);
                        kanban.setUpdatePersonId(authId);
                        kanban.setUpdateDatetime(now);
                        this.edit(kanban);
                    }
                    break;
                case WORKING:
                case SUSPEND:
                case OTHER:
                case DEFECT:
                    for (KanbanEntity kanban : kanbans) {
                        if(KanbanStatusEnum.INTERRUPT.equals(kanban.getKanbanStatus())) {
                            kanban.setKanbanStatus(newStatus);
                            kanban.setUpdatePersonId(authId);
                            kanban.setUpdateDatetime(now);
                            this.edit(kanban);
                        }
                    }
                    break;
                case INTERRUPT:
                    for (KanbanEntity kanban : kanbans) {
                        boolean apply = true;
                        switch (kanban.getKanbanStatus()) {
                            case PLANNING:
                            case PLANNED:
                                break;
                            case WORKING:
                            case SUSPEND:
                                kanban.setWorkKanbanCollection(this.getWorkKanban(kanban.getKanbanId()));
                                kanban.setSeparateworkKanbanCollection(this.getSeparateWorkKanban(kanban.getKanbanId()));
                                if (isCancel) {
                                    apply = this.cancelWork(kanban, authId);
                                }
                                break;
                            case COMPLETION:
                            case INTERRUPT:
                            case DEFECT:
                                apply = false;
                                break;
                        }

                        if (!apply) {
                            errorCount++;
                            continue;
                        }

                        kanban.setKanbanStatus(newStatus);
                        kanban.setUpdatePersonId(authId);
                        kanban.setUpdateDatetime(now);
                        this.edit(kanban);
                    }
                    break;

                default:
                    return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

        } catch (Exception ex) {
            logger.fatal(ex);
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }

        if (errorCount > 0) {
            logger.fatal("Have Some Error");
            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.NOT_SOME_UPDATED)).build();
        }

        return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
    }
    
    /**
     * カンバン情報を計画中状態に更新する。
     *
     * @param kanbans カンバン情報一覧
     * @param isCancel 強制中断
     * @param isRemoveKanbanReport カンバン帳票情報の削除
     * @param now 更新日時
     * @param authId 認証ID(更新者)
     * @throws Exception
     */
    private void planingKanban(List<KanbanEntity> kanbans, boolean isCancel, Boolean isRemoveKanbanReport, Date now, Long authId) throws Exception {
        List<Long> removeReportKanbanIds = new ArrayList();

        for (KanbanEntity kanban : kanbans) {
            boolean apply = true;
            kanban = this.find(kanban.getKanbanId(), null);

            switch (kanban.getKanbanStatus()) {
                case PLANNING:
                    apply = false;
                    break;
                case PLANNED:
                    break;
                case WORKING:
                case SUSPEND:
                    if (isCancel) {
                        apply = this.cancelWork(kanban, authId);
                    }
                    break;
                case COMPLETION:
                case INTERRUPT:
                case DEFECT:
                    break;
            }

            if (apply) {
                for (WorkKanbanEntity workKanban : kanban.getWorkKanbanCollection()) {
                    this.planingWorkKanban(workKanban);
                }

                for (WorkKanbanEntity workKanban : kanban.getSeparateworkKanbanCollection()) {
                    this.planingWorkKanban(workKanban);
                }

                kanban.setKanbanStatus(KanbanStatusEnum.PLANNING);
                kanban.setRepairNum(0);
                kanban.setUpdatePersonId(authId);
                kanban.setUpdateDatetime(now);
                this.edit(kanban);

                if (Objects.isNull(isRemoveKanbanReport) || isRemoveKanbanReport) {
                    removeReportKanbanIds.add(kanban.getKanbanId());
                }
            }
        }

        if (!removeReportKanbanIds.isEmpty()) {
            // カンバン帳票情報を削除する。
            this.kanbanReportRest.removeByKanbanId(removeReportKanbanIds);
        }
    }

    /**
     * 工程カンバン情報を計画中状態に更新する。
     * 
     * @param workKanban 工程カンバン情報
     */    
    private void planingWorkKanban(WorkKanbanEntity workKanban) {
        workKanban.setWorkStatus(KanbanStatusEnum.PLANNED);
        workKanban.setImplementFlag(false);
		
        if(!KanbanStatusEnum.PLANNED.equals(workKanban.getWorkStatus())
        && !KanbanStatusEnum.PLANNING.equals(workKanban.getWorkStatus())) {
            Integer num = workKanban.getReworkNum();
            workKanban.setReworkNum(Objects.nonNull(num)?num+1:0);
        }
        
        if (StringUtils.isEmpty(workKanban.getServiceInfo())) {
            return;
        }

        // プロダクト情報
        List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(workKanban.getServiceInfo(), ServiceInfoEntity[].class);
        for (ServiceInfoEntity serviceInfo : serviceInfos) {
            if (StringUtils.equals(serviceInfo.getService(), "product")) {
                List<KanbanProduct> products = KanbanProduct.toKanbanProducts(serviceInfo);
                for (KanbanProduct product : products) {
                    product.setStatus(KanbanStatusEnum.PLANNED);
                    product.setImplement(false);
                }
                serviceInfo.setJob(products);
                workKanban.setServiceInfo(JsonUtils.objectsToJson(serviceInfos));
                break;
            }
        }
    }

    /**
     * 作業を中止する。
     * 
     * @param kanban カンバン情報
     * @return true:正常終了、false:異常終了
     * @throws Exception 
     */
    private boolean cancelWork(KanbanEntity kanban, Long authId) throws Exception {
        try {
            logger.info("cancelWork start: kanban={}", kanban);

            WorkflowEntity workflow = this.workflowRest.findBasicInfo(kanban.getWorkflowId());
            Date date = new Date();

            for (WorkKanbanEntity workKanban : kanban.getWorkKanbanCollection()) {
                if (workKanban.getWorkStatus() == KanbanStatusEnum.WORKING) {
                    List<WorkKanbanWorkingEntity> workingList = this.workKanbanWorkingRest.getWorking(workKanban.getWorkKanbanId());
                    for (WorkKanbanWorkingEntity working : workingList) {
                        ActualProductReportEntity report = new ActualProductReportEntity(0L,
                                workKanban.getKanbanId(), 
                                workKanban.getWorkKanbanId(), 
                                working.getEquipmentId(),
                                working.getOrganizationId(),
                                date, KanbanStatusEnum.SUSPEND, "FORCED", null);

                        this.registReport(report, kanban, workflow, workKanban, false, 1, authId);
                    }
                }
            }

            for (WorkKanbanEntity workKanban : kanban.getSeparateworkKanbanCollection()) {
                if (workKanban.getWorkStatus() == KanbanStatusEnum.WORKING) {
                    List<WorkKanbanWorkingEntity> workingList = this.workKanbanWorkingRest.getWorking(workKanban.getWorkKanbanId());
                    for (WorkKanbanWorkingEntity working : workingList) {
                        ActualProductReportEntity report = new ActualProductReportEntity(0L,
                                workKanban.getKanbanId(), 
                                workKanban.getWorkKanbanId(), 
                                working.getEquipmentId(),
                                working.getOrganizationId(),
                                date, KanbanStatusEnum.SUSPEND, "FORCED", null);

                        this.registReport(report, kanban, workflow, workKanban, false, 1, authId);
                    }
                }
            }

            if (Objects.nonNull(this.adIntefaceFacade)) {
                // 作業キャンセルコマンドを発行
                this.adIntefaceFacade.notice(new CancelWorkCommand(kanban.getKanbanId(), date));
            }

            return true;
        } finally {
            logger.info("cancelWork end.");
        }
    }
    
    /**
     * 工程ステータスを更新する。
     * 
     * @param kanban
     * @param status 
     */
    private void updateWorkStatus(KanbanEntity kanban, KanbanStatusEnum status) {
        for (WorkKanbanEntity workKanban : kanban.getWorkKanbanCollection()) {
            workKanban.setWorkStatus(status);
        }

        for (WorkKanbanEntity workKanban : kanban.getSeparateworkKanbanCollection()) {
            workKanban.setWorkStatus(status);
        }
    }

    /*
     * (ELS)注文番号を指定して、残り台数を取得する。
     *
     * @param porder 注文番号
     * @param authId 認証ID
     * @return 残り台数
     */
    @GET
    @Path("service/els/rem")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String findRemainingNum(@QueryParam("porder") String porder, @QueryParam("authId") Long authId) {
        logger.info("findRemainingNum: porder={}, authId={}", porder, authId);
        try {
            // 注文番号を指定して、残り台数を取得する。
            TypedQuery<Long> query = this.em.createNamedQuery("KanbanEntity.calcOrderInfoRemByPorder", Long.class);
            query.setParameter(1, porder);// 注文番号

            Long rem = query.getSingleResult();
            if (rem < 0) {
                rem = 0L;
            }

            return String.valueOf(rem);
        } catch (NoResultException ex) {
            return String.valueOf(-1);// 未登録の注文番号のため、クライアント側で計画数を残り台数とする。
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * シリアル番号毎の一個流しカンバンを作成する。
     *
     * @param entity カンバン情報
     */
    private ServerErrorTypeEnum addSerialKanban(KanbanEntity entity) {
        try {
            // カンバンのサービス情報(JSON)からロット生産情報一覧を取得する。
            List<OrderInfoEntity> orderInfos = this.getOrderInfos(entity.getServiceInfo());

            // シリアル番号が未登録の場合は何もしない。
            boolean isExistSerial = false;
            for (OrderInfoEntity orderInfo : orderInfos) {
                if (Objects.nonNull(orderInfo.getSn()) && !orderInfo.getSn().isEmpty()) {
                    isExistSerial = true;
                    break;
                }
            }

            if (!isExistSerial) {
                return ServerErrorTypeEnum.SUCCESS;
            }

            // 1件目の情報から工程順を取得する。
            OrderInfoEntity firstOrderInfo = orderInfos.get(0);

            Long workflowId = firstOrderInfo.getWorkflowId();
            if (Objects.isNull(workflowId)) {
                // 一個流しカンバンの工程順名 (<KIKAKU_KATASIKI>–<KBUMONAME>)
                String workflowName = new StringBuilder(firstOrderInfo.getKikakuKatasiki())
                        .append("-")
                        .append(firstOrderInfo.getKbumoName())
                        .toString();
                workflowId = this.workflowRest.findIdByName(workflowName, null);
            }

            WorkflowEntity workflow = this.getWorkflowEntity2(workflowId);

            // シリアル番号毎のカンバン(基本情報のみ)を登録する。
            for (OrderInfoEntity orderInfo : orderInfos) {
                if (Objects.isNull(orderInfo.getSn())) {
                    continue;
                }

                for (String serialNo : orderInfo.getSn()) {
                    // 既に存在する場合はスキップする。
                    Long kanbanId = this.findIdByKanbanName(serialNo, workflow.getWorkflowId());
                    if (Objects.nonNull(kanbanId)) {
                        continue;
                    }

                    this.em.clear();

                    KanbanEntity kan = new KanbanEntity();

                    kan.setKanbanName(serialNo);// カンバン名 (シリアル番号)
                    kan.setKanbanSubname(orderInfo.getPorder());// サブカンバン名 (注文番号)
                    kan.setWorkflowId(workflow.getWorkflowId());// 工程順ID
                    kan.setWorkflowName(workflow.getWorkflowName());// 工程順名
                    kan.setModelName(workflow.getModelName());// モデル名
                    kan.setParentId(entity.getParentId());// カンバン階層ID
                    kan.setUpdatePersonId(entity.getUpdatePersonId());// 更新者
                    kan.setUpdateDatetime(entity.getUpdateDatetime());// 更新日時
                    kan.setKanbanStatus(KanbanStatusEnum.PLANNING);// ステータス
                    kan.setDefectNum(0);

                    // カンバン情報を登録する。
                    super.create(kan);
                    this.em.flush();

                    // カンバンの階層関連付け情報を登録する。
                    this.addHierarchy(kan);

                    // 追加情報をセットする。
                    kan.setKanbanAddInfo(workflow.getWorkflowAddInfo());

                    // 工程カンバン情報を生成する。
                    IntegerProperty rowsProperty = new SimpleIntegerProperty(0);
                    List<WorkKanbanEntity> workKanbans = this.createWorkKanban(
                            kan, workflow.getWorkflowId(), workflow.getConWorkflowWorkCollection(), false,
                            kan.getUpdatePersonId(), kan.getUpdateDatetime(), rowsProperty);

                    List<WorkKanbanEntity> sepWorkKanbans = this.createWorkKanban(
                            kan, workflow.getWorkflowId(), workflow.getConWorkflowSeparateworkCollection(), true,
                            kan.getUpdatePersonId(), kan.getUpdateDatetime(), rowsProperty);

                    if (!sepWorkKanbans.isEmpty()) {
                        workKanbans.addAll(sepWorkKanbans);
                    }

                    for (WorkKanbanEntity workKanban : workKanbans) {
                        // 工程カンバン情報を登録する。
                        this.em.persist(workKanban);
                    }
                    this.em.flush();
        
                    for (WorkKanbanEntity workKanban : workKanbans) {
                        // 関連付け情報を登録する。
                        this.workKandanRest.addConnection(workKanban);
                    }
                    this.em.flush();

                    kan.setWorkKanbanCollection(workKanbans);
                    kan.setSeparateworkKanbanCollection(sepWorkKanbans);

                    // ロット生産カンバンから作成される一個流し生産カンバンには計画時間を設定しない。2020/07/06 s-heya
                    // 開始予定日時が設定されている場合、カンバンの基準時間を更新する。
                    //if (Objects.nonNull(entity.getStartDatetime())) {
                    //    this.updateBaseTime2(kan, workflow, entity.getStartDatetime());
                    //}

                    // 工程順を進める。
                    if (!this.startWorkflow(kan, workflow, null)) {
                        return ServerErrorTypeEnum.SERVER_FETAL;
                    }

                    // カンバンプロパティ
                    int propOrder = 0;
                    List<AddInfoEntity> addInfos;
                    if (Objects.isNull(entity.getKanbanAddInfo())) {
                        addInfos = new LinkedList();
                    } else {
                        addInfos = JsonUtils.jsonToObjects(entity.getKanbanAddInfo(), AddInfoEntity[].class);
                        propOrder += addInfos.size();
                    }

                    addInfos.add(new AddInfoEntity(LocaleUtils.getString("key.Porder"), CustomPropertyTypeEnum.TYPE_STRING, orderInfo.getPorder(), propOrder++, null));// 注番
                    addInfos.add(new AddInfoEntity(LocaleUtils.getString("key.ProductName"), CustomPropertyTypeEnum.TYPE_STRING, orderInfo.getHinmei(), propOrder++, null));// 品名
                    addInfos.add(new AddInfoEntity(LocaleUtils.getString("key.KikakuKatasiki"), CustomPropertyTypeEnum.TYPE_STRING, orderInfo.getKikakuKatasiki(), propOrder++, null));// 規格・型式
                    addInfos.add(new AddInfoEntity(LocaleUtils.getString("key.FigureNumber"), CustomPropertyTypeEnum.TYPE_STRING, orderInfo.getSyanaiZuban(), propOrder++, null));// 図番
                    addInfos.add(new AddInfoEntity(LocaleUtils.getString("key.InternalComments"), CustomPropertyTypeEnum.TYPE_STRING, orderInfo.getSyanaiComment(), propOrder++, null));// 社内コメント
                    addInfos.add(new AddInfoEntity(LocaleUtils.getString("key.worknumber"), CustomPropertyTypeEnum.TYPE_STRING, orderInfo.getKban(), propOrder++, null));// 工程番号

                    addInfos.add(new AddInfoEntity(LocaleUtils.getString("key.SerialNumber"), CustomPropertyTypeEnum.TYPE_STRING, serialNo, propOrder++, null));// シリアル番号
                    addInfos.add(new AddInfoEntity(LocaleUtils.getString("key.LotQuantity"), CustomPropertyTypeEnum.TYPE_INTEGER, String.valueOf(orderInfo.getKvol()), propOrder++, null));// ロット数量

                    kan.setKanbanAddInfo(JsonUtils.objectsToJson(addInfos));

                    kan.setKanbanStatus(KanbanStatusEnum.PLANNED);
                    kan.setUpdateDatetime(new Date());

                    // カンバン情報を更新する。
                    super.edit(kan);
                    //this.em.flush();
                }
            }

            return ServerErrorTypeEnum.SUCCESS;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ServerErrorTypeEnum.SERVER_FETAL;
        }
    }

    /**
     * ロット流し生産のシリアル番号毎の実績を登録する。(工程カンバンの最終実績IDは更新しない)
     *
     * @param actual ロット流し生産の実績
     * @param serialWorkflows シリアル番号・工程順IDマップ
     * @param workTime 作業時間
     */
    private ServerErrorTypeEnum addSerialKanbanActual(ActualResultEntity actual, Map<String, Long> serialWorkflows, Integer workTime) {
        try {
            if (Objects.isNull(serialWorkflows) || serialWorkflows.isEmpty() || Objects.isNull(workTime)) {
                return ServerErrorTypeEnum.SUCCESS;
            }

            int serialWorkTime;// シリアル番号毎の作業時間

            // 開始・中断・完了の場合のみ処理を行ない、中断・完了時の作業時間はシリアル数で割った値とする。
            switch (actual.getActualStatus()) {
                case WORKING:// 開始
                    serialWorkTime = 0;
                    break;
                case SUSPEND:// 中断
                case COMPLETION:// 完了
                    serialWorkTime = workTime / serialWorkflows.size();
                    break;
                default:
                    return ServerErrorTypeEnum.SUCCESS;
            }

            // シリアル番号毎の実績を登録する。
            for (String serialNo : serialWorkflows.keySet()) {
                // カンバン名・工程順IDを指定して、カンバンIDを取得する。
                Long kanbanId = this.findIdByKanbanName(serialNo, serialWorkflows.get(serialNo));
                if (Objects.isNull(kanbanId)) {
                    continue;
                }

                // 中断・完了の工程実績情報を作成する。
                ActualResultEntity act = new ActualResultEntity();

                act.setKanbanId(kanbanId);// カンバンID
                act.setWorkKanbanId(actual.getWorkKanbanId());// 工程カンバンID
                act.setImplementDatetime(actual.getImplementDatetime());// 実施日時
                act.setTransactionId(actual.getTransactionId());// トランザクションID
                act.setEquipmentId(actual.getEquipmentId());// 設備ID
                act.setOrganizationId(actual.getOrganizationId());// 組織ID
                act.setWorkflowId(actual.getWorkflowId());// 工程順ID
                act.setWorkId(actual.getWorkId());// 工程ID
                act.setActualStatus(actual.getActualStatus());// 工程実績ステータス
                act.setInterruptReason(actual.getInterruptReason());// 中断理由
                act.setDelayReason(actual.getDelayReason());// 遅延理由

                act.setWorkingTime(serialWorkTime);// 作業時間[ms]
                act.setPairId(null);// ペアID
                act.setNonWorkTime(null);// 中断時間[ms]

                act.setKanbanName(serialNo);
                act.setEquipmentName(actual.getEquipmentName());
                act.setOrganizationName(actual.getOrganizationName());
                act.setWorkflowName(actual.getWorkflowName());
                act.setWorkName(actual.getWorkName());

                act.setReworkNum(actual.getReworkNum()); // 作業やり直し回数
                act.setCompNum(0);// 完成数

                // 工程実績情報を登録する。
                this.actualResultRest.add(act);
            }

            return ServerErrorTypeEnum.SUCCESS;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ServerErrorTypeEnum.SERAL_ACTUAL_ADD_ERROR;
        }
    }

    /**
     * カンバンのサービス情報(JSON)からロット生産情報一覧を取得する。
     *
     * @param serviceInfo カンバンのサービス情報(JSON)
     * @return ロット生産情報一覧 (ない場合はnull)
     */
    @Lock(LockType.READ)
    private List<OrderInfoEntity> getOrderInfos(String serviceInfo) {
        if (StringUtils.isEmpty(serviceInfo)) {
            // サービス情報がない。
            return null;
        }

        // カンバンのサービス情報のJSON文字列をサービス情報一覧に変換する。
        List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(serviceInfo, ServiceInfoEntity[].class);

        // サービス情報一覧から、ロット生産情報を取得。
        Optional<ServiceInfoEntity> opt = serviceInfos.stream()
                .filter(p -> Objects.equals(p.getService(), SERVICE_INFO_LOT))
                .findFirst();
        if (!opt.isPresent()) {
            // サービス情報一覧にロット生産情報がない。
            return null;
        }

        Object job = opt.get().getJob();
        if (Objects.isNull(job)) {
            // ロット生産情報に作業情報がない。
            return null;
        }

        List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) job;
        List<OrderInfoEntity> orderInfos = new LinkedList();
        for (LinkedHashMap<String, Object> item : list) {
            OrderInfoEntity orderInfo = new OrderInfoEntity(item);
            orderInfos.add(orderInfo);
        }

        return orderInfos;
    }

    /**
     * ロット流し生産のシリアル番号・工程順IDマップを取得する。
     *
     * @param kanbanServiceInfo カンバンのサービス情報(JSON)
     * @return シリアル番号・工程順IDマップ
     */
    private Map<String, Long> getLotSerialWorkflows(String kanbanServiceInfo) {
        // カンバンのサービス情報(JSON)からロット生産情報一覧を取得する。
        List<OrderInfoEntity> orderInfos = this.getOrderInfos(kanbanServiceInfo);
        if (Objects.isNull(orderInfos) || orderInfos.isEmpty()) {
            return null;
        }

        Map<String, Long> map = new LinkedHashMap();

        Map<String, Long> workflows = new HashMap();
        for (OrderInfoEntity orderInfo : orderInfos) {
            if (Objects.isNull(orderInfo.getSn()) || orderInfo.getSn().isEmpty()) {
                continue;
            }

            long workflowId;
            if (Objects.nonNull(orderInfo.getWorkflowId())) {
                workflowId = orderInfo.getWorkflowId();
            } else {
                String workflowName = new StringBuilder(orderInfo.getKikakuKatasiki())
                        .append("-")
                        .append(orderInfo.getKbumoName())
                        .toString();

                if (workflows.containsKey(workflowName)) {
                    workflowId = workflows.get(workflowName);
                } else {
                    workflowId = this.workflowRest.findIdByName(workflowName, null);
                    workflows.put(workflowName, workflowId);
                }
            }

            for (String serialNo : orderInfo.getSn()) {
                map.put(serialNo, workflowId);
            }
        }

        return map;
    }

    /**
     * カンバン名・工程順IDを指定して、カンバンIDを取得する。
     *
     * @param kanbanName カンバン名
     * @param workKanbanId 工程順ID
     * @return カンバンID
     */
    @Lock(LockType.READ)
    private Long findIdByKanbanName(String kanbanName, Long workKanbanId) {
        try {
            TypedQuery<Long> query = this.em.createNamedQuery("KanbanEntity.findIdByKanbanName", Long.class);

            query.setParameter("kanbanName", kanbanName);
            query.setParameter("workflowId", workKanbanId);

            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
   
    /**
     * 作業開始時に工程カンバン情報を更新する。
     * 
     * @param report 実績通知情報
     * @param kanban カンバン情報
     * @param workKanban 工程カンバン情報
     */
    private void startWorkKanban(ActualProductReportEntity report, KanbanEntity kanban, WorkKanbanEntity workKanban) {
        workKanban.setActualStartTime(report.getReportDatetime());
        
        if (StringUtils.isEmpty(workKanban.getServiceInfo())) {
            return;
        }

        List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(workKanban.getServiceInfo(), ServiceInfoEntity[].class);
        for (ServiceInfoEntity serviceInfo : serviceInfos) {
            if (StringUtils.equals(serviceInfo.getService(), "product")) {
                List<KanbanProduct> destList = KanbanProduct.toKanbanProducts(serviceInfo);

                List<KanbanProduct> srcList = KanbanProduct.lookupProductList(kanban.getServiceInfo(), ServiceInfoEntity.SERVICE_INFO_PRODUCT);
                for (KanbanProduct src : srcList) {
                    for (KanbanProduct desr : destList) {
                        if (StringUtils.equals(src.getUid(), desr.getUid())) {
                            desr.setDefect(src.getDefect());
                            break;
                        }
                    }
                }

                serviceInfo.setJob(destList);
                workKanban.setServiceInfo(JsonUtils.objectsToJson(serviceInfos));
                break;
            }
        }
    }

    /**
     * カンバンの注番情報に不良品情報を反映して、反映後の注番情報を取得する。
     *
     * @param kanban カンバン情報
     * @param defects 不良品情報(JSON)
     * @return 不良品情報を反映した注番情報(JSON)
     */
    private String setKanbanDefects(KanbanEntity kanban, String defects) {
        if (Objects.isNull(defects)
                || StringUtils.isEmpty(kanban.getServiceInfo())) {
            return null;
        }

        // 不良品情報のJSON文字列を不良品情報一覧に変換する。
        List<DefectInfoEntity> defectInfos = JsonUtils.jsonToObjects(defects, DefectInfoEntity[].class);

        // カンバンのサービス情報のJSON文字列をサービス情報一覧に変換する。
        List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(kanban.getServiceInfo(), ServiceInfoEntity[].class);

        List<OrderInfoEntity> orderInfos = null;
        for (ServiceInfoEntity serviceInfo : serviceInfos) {
            // ロット流し生産の情報以外はスキップする。
            if (!Objects.equals(serviceInfo.getService(), SERVICE_INFO_LOT)
                    || Objects.isNull(serviceInfo.getJob())) {
                continue;
            }

            List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) serviceInfo.getJob();
            orderInfos = new LinkedList();

            for (LinkedHashMap<String, Object> item : list) {
                // 注番情報のMapを注番情報
                OrderInfoEntity orderInfo = new OrderInfoEntity(item);
                orderInfos.add(orderInfo);

                // 不良になったシリアルを削除する。
                int lvol = orderInfo.getLvol();
                int defect = orderInfo.getDefect();
                List<String> sn = orderInfo.getSn();

                Map<String, String> defectSerials = orderInfo.getDefectSerials();
                if (Objects.isNull(defectSerials)) {
                    defectSerials = new LinkedHashMap();
                }

                Optional<DefectInfoEntity> optDefectInfo = defectInfos.stream()
                        .filter(p -> Objects.equals(orderInfo.getPorder(), p.getPorder()))
                        .findFirst();

                if (optDefectInfo.isPresent()) {
                    DefectInfoEntity defectInfo = optDefectInfo.get();
                    int newDefect = 0;// 今回の不良数

                    if (Objects.nonNull(defectInfo.getDefectSerials())) {
                        // 不良品のシリアル番号を削除する。
                        for (DefectSerialEntity defectSerial : defectInfo.getDefectSerials()) {
                            if (sn.remove(defectSerial.getSerialNo())) {
                                newDefect++;
                            }

                            // シリアル番号毎の不良情報を追加する。
                            String defectReason = "";
                            if (Objects.nonNull(defectSerial.getDefectReason())
                                    && Objects.nonNull(defectSerial.getDefectReason().getDefectValue())) {
                                defectReason = defectSerial.getDefectReason().getDefectValue();
                            }

                            defectSerials.put(defectSerial.getSerialNo(), defectReason);
                        }
                    } else {
                        newDefect = defectInfo.getDefectNum();
                    }

                    lvol -= newDefect;// 指示数から今回の不良数を減算する。
                    defect += newDefect;// 不良数を加算する。
                }

                // 注番情報の指示数・不良数・シリアル番号・シリアル番号毎の不良情報を更新する。
                orderInfo.setLvol(lvol);
                orderInfo.setDefect(defect);
                orderInfo.setSn(sn);
                orderInfo.setDefectSerials(defectSerials);
            }

            // サービス情報の注番情報を更新する。
            serviceInfo.setJob(orderInfos);

            break;
        }

        // サービス情報一覧をJSON文字列に変換して、カンバンのサービス情報(JSON)を更新する。
        kanban.setServiceInfo(JsonUtils.objectsToJson(serviceInfos));

        if (Objects.isNull(orderInfos)) {
            return null;
        }

        return JsonUtils.objectsToJson(orderInfos);
    }

    /**
     * 不良品の実績を登録する。
     *
     * @param actual 作業の工程実績
     * @param serialWorkflows シリアル番号・工程順IDマップ
     * @param defects 不良品情報一覧(JSON)
     */
    private ServerErrorTypeEnum addDefectActual(KanbanEntity kanban, WorkKanbanEntity workKanban, ActualProductReportEntity report, Map<String, Long> serialWorkflows) {
        logger.info("addDefectActual: workKanban={}, defects={}", workKanban, report.getDefects());
        try {
            String defects = report.getDefects();
            if (Objects.isNull(defects)) {
                return ServerErrorTypeEnum.SUCCESS;
            }

            // 設備名を取得する。
            String equipmentName = "";
            if (Objects.nonNull(report.getEquipmentId())) {
                equipmentName = this.equipmentRest.findNameById(report.getEquipmentId());
            }
            // 組織名を取得する。
            String organizationName = "";
            if (Objects.nonNull(report.getOrganizationId())) {
                organizationName = this.organizationRest.findNameById(report.getOrganizationId());
            }

            // 不良品情報のJSON文字列を不良品情報一覧に変換する。
            List<DefectInfoEntity> defectInfos = JsonUtils.jsonToObjects(defects, DefectInfoEntity[].class);

            for (DefectInfoEntity defectInfo : defectInfos) {
                if (Objects.isNull(defectInfo.getDefectSerials()) || defectInfo.getDefectSerials().isEmpty()) {
                    // シリアルなしの場合、注番毎の実績を登録する。
                    ActualResultEntity act = new ActualResultEntity();

                    act.setKanbanId(kanban.getKanbanId());// カンバンID
                    act.setWorkKanbanId(workKanban.getWorkKanbanId());// 工程カンバンID
                    act.setImplementDatetime(report.getReportDatetime());// 実施日時
                    act.setTransactionId(report.getTransactionId());// トランザクションID
                    act.setEquipmentId(report.getEquipmentId());// 設備ID
                    act.setOrganizationId(report.getOrganizationId());// 組織ID
                    act.setWorkflowId(kanban.getWorkflowId());// 工程順ID
                    act.setWorkId(workKanban.getWorkId());// 工程ID
                    act.setActualStatus(KanbanStatusEnum.DEFECT);// 工程実績ステータス
                    act.setInterruptReason(null);// 中断理由
                    act.setDelayReason(null);// 遅延理由

                    act.setWorkingTime(0);// 作業時間[ms]
                    act.setPairId(null);// ペアID
                    act.setNonWorkTime(null);// 中断時間[ms]
                    act.setReworkNum(0);

                    act.setKanbanName(kanban.getKanbanName());
                    act.setEquipmentName(equipmentName);
                    act.setOrganizationName(organizationName);
                    act.setWorkflowName(kanban.getWorkflowName());
                    act.setWorkName(workKanban.getWorkName());

                    act.setCompNum(0);// 完成数

                    // 不良理由
                    String defectReason = "";
                    if (Objects.nonNull(defectInfo.getDefectReason())) {
                        defectReason = defectInfo.getDefectReason().getDefectValue();
                    }
                    act.setDefectReason(defectReason);

                    act.setDefectNum(defectInfo.getDefectNum());// 不良数

                    // 追加情報(不良品情報)
                    AddInfoEntity addInfo = new AddInfoEntity("defect", CustomPropertyTypeEnum.TYPE_DEFECT, JsonUtils.objectToJson(defectInfo), 0, null);
                    act.setActualAddInfo(JsonUtils.objectToJson(Arrays.asList(addInfo)));

                    // 工程実績情報を登録する。
                    this.actualResultRest.add(act);
                } else {
                    // シリアルありの場合、シリアル番号毎に実績を登録する。
                    for (DefectSerialEntity defectSerial : defectInfo.getDefectSerials()) {
                        // カンバン名・工程順IDを指定して、カンバンIDを取得する。
                        Long kanbanId = this.findIdByKanbanName(defectSerial.getSerialNo(), serialWorkflows.get(defectSerial.getSerialNo()));
                        if (Objects.isNull(kanbanId)) {
                            continue;
                        }

                        ActualResultEntity act = new ActualResultEntity();

                        act.setKanbanId(kanbanId);// カンバンID
                        act.setWorkKanbanId(workKanban.getWorkKanbanId());// 工程カンバンID
                        act.setImplementDatetime(report.getReportDatetime());// 実施日時
                        act.setTransactionId(report.getTransactionId());// トランザクションID
                        act.setEquipmentId(report.getEquipmentId());// 設備ID
                        act.setOrganizationId(report.getOrganizationId());// 組織ID
                        act.setWorkflowId(kanban.getWorkflowId());// 工程順ID
                        act.setWorkId(workKanban.getWorkId());// 工程ID
                        act.setActualStatus(KanbanStatusEnum.DEFECT);// 工程実績ステータス
                        act.setInterruptReason(null);// 中断理由
                        act.setDelayReason(null);// 遅延理由

                        act.setWorkingTime(0);// 作業時間[ms]
                        act.setPairId(null);// ペアID
                        act.setNonWorkTime(null);// 中断時間[ms]
                        act.setReworkNum(workKanban.getReworkNum());

                        act.setKanbanName(defectSerial.getSerialNo());
                        act.setEquipmentName(equipmentName);
                        act.setOrganizationName(organizationName);
                        act.setWorkflowName(kanban.getWorkflowName());
                        act.setWorkName(workKanban.getWorkName());

                        act.setCompNum(0);// 完成数

                        // 不良理由
                        String defectReason = "";
                        if (Objects.nonNull(defectSerial.getDefectReason())) {
                            defectReason = defectSerial.getDefectReason().getDefectValue();
                        }
                        act.setDefectReason(defectReason);

                        act.setDefectNum(1);// 不良数

                        act.setActualAddInfo(null);// 追加情報

                        // 工程実績情報を登録する。
                        this.actualResultRest.add(act);
                    }
                }
            }

            return ServerErrorTypeEnum.SUCCESS;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ServerErrorTypeEnum.DEFECT_ACTUAL_ADD_ERROR;
        }
    }

    /**
     * 補充カンバンを登録する。
     *
     * @param baseKanban 元のカンバン
     * @param defects 不良品情報一覧(JSON)
     * @param creatorId 作成者(組織ID)
     * @param workKanbanId 実績の工程カンバンID
     * @param parentId カンバン階層ID
     */
    private ServerErrorTypeEnum createReplenishmentKanban(KanbanEntity baseKanban, String defects, Long creatorId, long workKanbanId, ActualProductReportEntity report) {
        logger.info("createReplenishmentKanban: baseKanbanId={}, creatorId={}, workKanbanId={}", baseKanban.getKanbanId(), creatorId, workKanbanId);
        try {
            if (Objects.isNull(defects)) {
                return ServerErrorTypeEnum.SUCCESS;
            }

            // 不良品情報のJSON文字列を不良品情報一覧に変換する。
            List<DefectInfoEntity> defectInfos = JsonUtils.jsonToObjects(defects, DefectInfoEntity[].class);
            if (defectInfos.isEmpty()) {
                return ServerErrorTypeEnum.SUCCESS;
            }

            // 作成者
            String creator = null;
            if (Objects.nonNull(creatorId)) {
                creator = this.organizationRest.findIdentifyById(creatorId);
            }

            // 補充カンバン名
            String kanbanName = this.createReplenishmentKanbanName(baseKanban.getKanbanName(), baseKanban.getWorkflowId());

            int lotQuantity = 0;// ロット数

            // カンバンのサービス情報(JSON)からロット生産情報一覧を取得する。
            List<OrderInfoEntity> baseOrderInfos = this.getOrderInfos(baseKanban.getServiceInfo());

            // 不良品情報からロット生産情報を作成する。
            List<OrderInfoEntity> orderInfos = new LinkedList();
            for (DefectInfoEntity defectInfo : defectInfos) {
                // 元カンバンの注番情報から該当する注文番号の情報を取得する。
                Optional<OrderInfoEntity> orderInfoOpt = baseOrderInfos.stream()
                        .filter(p -> Objects.equals(p.getPorder(), defectInfo.getPorder()))
                        .findFirst();

                OrderInfoEntity orderInfo;// 補充カンバンの注番情報
                if (orderInfoOpt.isPresent()) {
                    orderInfo = new OrderInfoEntity(orderInfoOpt.get());
                } else {
                    orderInfo = new OrderInfoEntity();
                    orderInfo.setPorder(defectInfo.getPorder());
                }

                int lvol = defectInfo.getDefectNum();

                List<String> sn = null;
                if (Objects.nonNull(defectInfo.getDefectSerials())) {
                    sn = defectInfo.getDefectSerials().stream()
                            .map(p -> p.getSerialNo())
                            .collect(Collectors.toList());
                }

                orderInfo.setLvol(lvol);// 指示数 = 不良数
                orderInfo.setDefect(0);// 不良数 = 0
                orderInfo.setRem(0);// 残り台数: 0
                orderInfo.setSn(sn);// シリアル番号: 不良品のシリアル番号

                orderInfos.add(orderInfo);

                lotQuantity += lvol;// ロット数に加算
            }

            // 補充カンバン用のサービス情報
            ServiceInfoEntity serviceInfo = new ServiceInfoEntity();
            serviceInfo.setService(SERVICE_INFO_LOT);
            serviceInfo.setJob(orderInfos);

            // 補充カンバンを作成する。
            KanbanCreateCondition condition = new KanbanCreateCondition(
                    kanbanName,
                    baseKanban.getWorkflowId(),
                    baseKanban.getParentId(),
                    creator,
                    true,
                    lotQuantity,
                    new Date(),
                    null,
                    2
            );

            Response response = this.createKanban(condition, null);
            ResponseEntity res = (ResponseEntity) response.getEntity();
            if (!res.isSuccess()) {
                // 作成エラー
                logger.fatal("create kanban fatal: kanbanName={}, workflowId={}", kanbanName, baseKanban.getWorkflowId());
                return ServerErrorTypeEnum.REPLENISHMENT_KANBAN_ADD_ERROR;
            }

            // 作成した補充カンバンのカンバンIDを取得する。
            int pos = res.getUri().lastIndexOf("/");
            String idStr = res.getUri().substring(pos + 1);

            Long kanbanId = Long.valueOf(idStr);

            // 作成した補充カンバンを取得する。
            KanbanEntity kanban = this.find(kanbanId, null);

            // 作成した補充カンバンに情報を追加する。
            kanban.setModelName(baseKanban.getModelName());// モデル名
            kanban.setServiceInfo(JsonUtils.objectToJson(Arrays.asList(serviceInfo))); // サービス情報(JSON)

            // 追加情報に「補充枝番」をセットする。
            List<AddInfoEntity> baseAddInfos = JsonUtils.jsonToObjects(baseKanban.getKanbanAddInfo(), AddInfoEntity[].class);

            String repKey = LocaleUtils.getString("key.ReplenishmentNumber");// 補充枝番
            List<AddInfoEntity> addInfos = baseAddInfos.stream()
                    .filter(p -> !Objects.equals(p.getKey(), repKey))
                    .collect(Collectors.toList());

            Pattern ptn = Pattern.compile("(.*)-(\\d+$)");
            Matcher mat = ptn.matcher(kanbanName);
            String repNum = "";
            if (mat.find()) {
                repNum = mat.group(2);
            }

            int repDisp = 0;
            if (!addInfos.isEmpty()) {
                repDisp = addInfos.stream()
                        .mapToInt(p -> p.getDisp())
                        .max()
                        .getAsInt() + 1;
            }

            addInfos.add(new AddInfoEntity(repKey, CustomPropertyTypeEnum.TYPE_INTEGER, repNum, repDisp, null));

            kanban.setKanbanAddInfo(JsonUtils.objectsToJson(addInfos));// 追加情報(JSON)

            kanban.setKanbanStatus(KanbanStatusEnum.PLANNED);
            kanban.setUpdateDatetime(new Date());

            // 元のカンバンの工程実績一覧を取得する。
            List<ActualResultEntity> baseActuals = actualResultRest.find(Arrays.asList(baseKanban.getKanbanId()), null, null);

            if (Objects.isNull(baseKanban.getWorkKanbanCollection())) {
                baseKanban.setWorkKanbanCollection(this.getWorkKanban(baseKanban.getKanbanId()));
            }

            Boolean separateWorkFlag = null;
            Long workId = null;;
            Optional<WorkKanbanEntity> optDefectWorkKanban = baseKanban.getWorkKanbanCollection().stream()
                    .filter(p -> Objects.equals(p.getWorkKanbanId(), workKanbanId))
                    .findFirst();
            if (optDefectWorkKanban.isPresent()) {
                WorkKanbanEntity defectWorkKanban = optDefectWorkKanban.get();
                workId = defectWorkKanban.getWorkId();
                separateWorkFlag = defectWorkKanban.getSeparateWorkFlag();
            }

            // 元のカンバンで完了した工程を完了扱いにする。
            for (WorkKanbanEntity wkan : kanban.getWorkKanbanCollection()) {
                Optional<WorkKanbanEntity> baseWkanOpt = baseKanban.getWorkKanbanCollection().stream()
                        .filter(p -> Objects.equals(p.getSeparateWorkFlag(), wkan.getSeparateWorkFlag())
                                && Objects.equals(p.getWorkId(), wkan.getWorkId()))
                        .findFirst();

                if (!baseWkanOpt.isPresent()) {
                    continue;
                }

                WorkKanbanEntity baseWkan = baseWkanOpt.get();

                wkan.setImplementFlag(baseWkan.getImplementFlag());// 実施フラグ

                // 工程ステータス
                wkan.setWorkStatus(baseWkan.getWorkStatus());

                // 作業やり直し回数
                if (Objects.isNull(baseWkan.getReworkNum())) {
                    wkan.setReworkNum(1);
                } else {
                    wkan.setReworkNum(baseWkan.getReworkNum() + 1);
                }

                // 元の工程カンバンの最後のトレーサビリティ情報を引き継ぐ。
                if (Objects.nonNull(baseActuals)) {
                    ComparatorChain comparator = new ComparatorChain();
                    comparator.addComparator(new BeanComparator("implementDatetime", new NullComparator()));
                    comparator.addComparator(new BeanComparator("actualId", new NullComparator()));

                    Optional<ActualResultEntity> baseActOpt = baseActuals.stream()
                            .filter(p -> Objects.equals(p.getWorkKanbanId(), baseWkan.getWorkKanbanId())
                                    && Objects.equals(p.getActualStatus(), KanbanStatusEnum.COMPLETION))
                            .sorted(comparator.reversed())
                            .findFirst();

                    if (baseActOpt.isPresent()) {
                        this.addReplenishmentActual(wkan, baseActOpt.get());
                    }
                }

                // 不良品登録した現在の工程を「中断中」にして、中断実績を作成する。
                if (Objects.equals(wkan.getSeparateWorkFlag(), separateWorkFlag)
                        && Objects.equals(wkan.getWorkId(), workId)) {
                    wkan.setWorkStatus(KanbanStatusEnum.SUSPEND);

                    Long actualId = this.addReplenishmentSuspendActual(wkan, report);

                    // 最終実績ID
                    wkan.setLastActualId(actualId);
                }
            }

            // 補充カンバンを更新する。
            response = this.update(kanban, null);
            res = (ResponseEntity) response.getEntity();
            if (!res.isSuccess()) {
                // 作成エラー
                logger.fatal("update kanban fatal: kanbanName={}, workflowId={}", kanbanName, baseKanban.getWorkflowId());
                return ServerErrorTypeEnum.REPLENISHMENT_KANBAN_ADD_ERROR;
            }

            return ServerErrorTypeEnum.SUCCESS;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ServerErrorTypeEnum.REPLENISHMENT_KANBAN_ADD_ERROR;
        }
    }

    /**
     * 補充カンバンの工程実績情報を登録する。
     *
     * @param wkan 補充カンバンの工程カンバン情報
     * @param baseAct 元カンバンの工程実績情報
     */
    private void addReplenishmentActual(WorkKanbanEntity wkan, ActualResultEntity baseAct) {
        // 工程実績情報を作成する。
        ActualResultEntity actual = new ActualResultEntity(
                wkan.getKanbanId(), wkan.getWorkKanbanId(), baseAct.getImplementDatetime(),
                baseAct.getTransactionId(), baseAct.getEquipmentId(), baseAct.getOrganizationId(), wkan.getWorkflowId(), wkan.getWorkId(),
                baseAct.getActualStatus(), null, null, 0, null, null, 0, false);
        actual.setKanbanName(wkan.getKanbanName());
        actual.setEquipmentName(baseAct.getEquipmentName());
        actual.setOrganizationName(baseAct.getOrganizationName());
        actual.setWorkflowName(baseAct.getWorkflowName());
        actual.setWorkName(baseAct.getWorkName());

        actual.setCompNum(0);

        // 検査結果
        actual.setActualAddInfo(baseAct.getActualAddInfo());

        // 工程実績情報を登録する。
        this.actualResultRest.add(actual);

        // トレーサビリティDB使用時は、トレーサビリティDBにデータを追加する。
        if (this.isTraceabilityDBEnabled) {
            TraceabilityJdbc jdbc = TraceabilityJdbc.getInstance();

            List<TraceabilityEntity> traces = jdbc.getWorkKanbanTraceability(actual.getActualId(), false);

            jdbc.addTraceability(actual.getActualId(), traces);
        }
    }

    /**
     * 補充カンバンの中断実績を作成する。
     *
     * @param wkan 補充カンバンの工程カンバン情報
     * @param report 実績情報
     * @return 実績ID
     */
    private Long addReplenishmentSuspendActual(WorkKanbanEntity wkan, ActualProductReportEntity report) {
        // 設備名を取得する。
        String equipmentName = "";
        if (Objects.nonNull(report.getEquipmentId())) {
            equipmentName = this.equipmentRest.findNameById(report.getEquipmentId());
        }

        // 組織名を取得する。
        String organizationName = "";
        if (Objects.nonNull(report.getOrganizationId())) {
            organizationName = this.organizationRest.findNameById(report.getOrganizationId());
        }

        // 工程実績情報を作成する。
        ActualResultEntity actual = new ActualResultEntity(
                wkan.getKanbanId(), wkan.getWorkKanbanId(), report.getReportDatetime(),
                report.getTransactionId(), report.getEquipmentId(), report.getOrganizationId(), wkan.getWorkflowId(), wkan.getWorkId(),
                KanbanStatusEnum.SUSPEND, null, null, 0, null, null, 0, false);
        actual.setKanbanName(wkan.getKanbanName());
        actual.setEquipmentName(equipmentName);
        actual.setOrganizationName(organizationName);
        actual.setWorkflowName(wkan.getWorkflowName());
        actual.setWorkName(wkan.getWorkName());

        actual.setCompNum(0);

        // 追加情報一覧
        List<AddInfoEntity> addInfos = new LinkedList();
        if (Objects.nonNull(report.getPropertyCollection()) && !report.getPropertyCollection().isEmpty()) {
            for (ActualProductReportPropertyEntity prop : report.getPropertyCollection()) {
                addInfos.add(new AddInfoEntity(prop.getPropertyName(), CustomPropertyTypeEnum.toEnum(prop.getPropertyType()), prop.getPropertyValue(), prop.getPropertyOrder(), prop.getMemo()));
            }
        }

        // 追加情報一覧をJSON文字列に変換する。
        String actualAddInfo = JsonUtils.objectsToJson(addInfos);

        // 検査結果
        actual.setActualAddInfo(actualAddInfo);

        // 工程実績情報を登録する。
        this.actualResultRest.add(actual);

        // トレーサビリティDB使用時は、トレーサビリティDBにデータを追加する。
        if (this.isTraceabilityDBEnabled) {
            TraceabilityJdbc jdbc = TraceabilityJdbc.getInstance();

            List<TraceabilityEntity> traces = jdbc.getWorkKanbanTraceability(actual.getActualId(), false);

            jdbc.addTraceability(actual.getActualId(), traces);
        }

        return actual.getActualId();
    }

    /**
     * 新しい補充カンバン名を取得する。
     *
     * @param kanbanName 元のカンバン名
     * @param workflowId 工程順ID
     * @return 補充カンバン名
     */
    private String createReplenishmentKanbanName(String kanbanName, long workflowId) {
        StringBuilder baseName = new StringBuilder(kanbanName);// 元のカンバン名
        int num = 1;// 枝番

        // カンバン名が「<カンバン名>-<枝番>」の場合、「元のカンバン名」と「枝番」を更新する。
        Pattern ptn = Pattern.compile("(.*)-(\\d+$)");
        Matcher mat = ptn.matcher(kanbanName);
        if (mat.find()) {
            baseName = new StringBuilder(mat.group(1));
            num = Integer.valueOf(mat.group(2)) + 1;
        }

        String name = new StringBuilder(baseName).append("-").append(num).toString();// 補充カンバン名

        // 登録されていない枝番を付与する。
        boolean isFind = true;
        while (isFind) {
            // カンバン名の重複を確認する。(削除済も含む)
            KanbanExistEntity exist = new KanbanExistEntity(null, name, null, workflowId);
            if (this.existKanban(exist)) {
                num++;
                name = new StringBuilder(baseName).append("-").append(num).toString();
                continue;
            }

            isFind = false;
        }

        return name;
    }

    /**
     * 
     * @param job 作業情報
     * @return 注番情報一覧
     * @throws Exception 
     */
    @Lock(LockType.READ)
    public List<OrderInfoEntity> castOrderInfoList(Object job) throws Exception {
        List<OrderInfoEntity> orderInfos = new LinkedList();
        for (LinkedHashMap<String, Object> item : (List<LinkedHashMap<String, Object>>) job) {
            OrderInfoEntity orderInfo = new OrderInfoEntity(item);
            orderInfos.add(orderInfo);
        }
        return orderInfos;
    }

    /**
     * 不良理由の実績を登録して、補充カンバンを作成する。
     *
     * @param report 工程実績情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @POST
    @Path("report/defects")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ResultResponse reportDefects(ActualProductReportEntity report, @QueryParam("authId") Long authId) {
        logger.info("reportDefects: {}, authId={}", report, authId);
        try {
            ServerErrorTypeEnum res;

            if (Objects.isNull(report.getDefects())
                    || report.getDefects().isEmpty()) {
                logger.fatal("Defects is empty: {}", report);
                return ResultResponse.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // カンバン情報を取得する。
            KanbanEntity kanban = this.find(report.getKanbanId(), authId);
            if (Objects.isNull(kanban)) {
                logger.fatal("Not found kanban: {}", report);
                return ResultResponse.failed(ServerErrorTypeEnum.NOTFOUND_KANBAN);
            }

            // 不良品情報のJSON文字列を不良品情報一覧に変換する。
            List<DefectInfoEntity> defectInfos = null;
            if (Objects.nonNull(report.getDefects())) {
                defectInfos = JsonUtils.jsonToObjects(report.getDefects(), DefectInfoEntity[].class);
            }

            // カンバンのサービス情報(JSON)からロット生産情報一覧を取得する。
            List<OrderInfoEntity> orderInfos = this.getOrderInfos(kanban.getServiceInfo());

            if (Objects.nonNull(orderInfos)
                    && Objects.nonNull(defectInfos)) {
                for (OrderInfoEntity orderInfo : orderInfos) {
                    // 不良品情報と現在のカンバンで注番毎の指示数が異なる場合、エラーで現在の注番情報を返す。(他のadProductで不良品を登録している)
                    Optional<DefectInfoEntity> optDefectInfo = defectInfos.stream()
                            .filter(p -> Objects.equals(p.getPorder(), orderInfo.getPorder()))
                            .findFirst();
                    if (optDefectInfo.isPresent()
                            && !Objects.equals(optDefectInfo.get().getLvol(), orderInfo.getLvol())) {
                        logger.fatal("Already updated defects: {}", report);
                        // 現在の注番情報(JSON)をセットしてエラーを返す。
                        String job = JsonUtils.objectsToJson(orderInfos);
                        return ResultResponse.failed(ServerErrorTypeEnum.DEFECT_ACTUAL_FAILED).result(job);
                    }
                }
            }

            // 工程カンバン情報を取得する。
            Optional<WorkKanbanEntity> optWorkKanban = kanban.getWorkKanbanCollection().stream()
                    .filter(p -> Objects.equals(p.getWorkKanbanId(), report.getWorkKanbanId()))
                    .findFirst();
            if (!optWorkKanban.isPresent()) {
                logger.fatal("Not found workKanban: {}", report);
                return ResultResponse.failed(ServerErrorTypeEnum.NOTFOUND_WORKKANBAN);
            }

            WorkKanbanEntity workKanban = optWorkKanban.get();

            Map<String, Long> lotSerialWorkflows = null;// ロット流し生産のシリアル番号・工程順IDマップ
            if (kanban.getProductionType() == 2) {
                // カンバンのサービス情報(JSON)からロット流し生産のシリアル番号・工程順IDマップを取得する。
                lotSerialWorkflows = this.getLotSerialWorkflows(kanban.getServiceInfo());
            }

            // カンバンの注番情報に不良品情報を反映して、反映後の注番情報を取得する。
            String newJob = this.setKanbanDefects(kanban, report.getDefects());

            // カンバンの不良品情報(サービス情報)を更新する。
            this.updateServiceInfo(kanban.getKanbanId(), kanban.getServiceInfo());

            // 不良品の実績を登録する。(工程カンバンの最終実績IDは更新しない)
            res = this.addDefectActual(kanban, workKanban, report, lotSerialWorkflows);
            if (!ServerErrorTypeEnum.SUCCESS.equals(res)) {
                return ResultResponse.failed(ServerErrorTypeEnum.DEFECT_ACTUAL_ADD_ERROR);
            }

            // 補充カンバンを登録する。
            res = this.createReplenishmentKanban(kanban, report.getDefects(), report.getOrganizationId(), report.getWorkKanbanId(), report);
            if (!ServerErrorTypeEnum.SUCCESS.equals(res)) {
                return ResultResponse.failed(ServerErrorTypeEnum.REPLENISHMENT_KANBAN_ADD_ERROR);
            }

            // 不良品情報を反映した注番情報(JSON)をセットして成功を返す。
            return ResultResponse.success().errorType(ServerErrorTypeEnum.SUCCESS).result(newJob);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
        } finally {
            logger.info("reportDefects end.");
        }
    }

    /**
     * カンバンIDを指定して、サービス情報を更新する。
     *
     * @param kanbanId カンバンID
     * @param serviceInfo サービス情報
     */
    private void updateServiceInfo(long kanbanId, String serviceInfo) {
        Query updateQuery = this.em.createNamedQuery("KanbanEntity.updateServiceInfo");
        updateQuery.setParameter("serviceInfo", serviceInfo);
        updateQuery.setParameter("kanbanId", kanbanId);
        updateQuery.executeUpdate();
    }

    /**
     * カンバンIDを指定して、承認情報を追加・更新する。
     *
     * @param id カンバンID
     * @param approval 承認情報
     * @param verInfo
     * @param authId 認証ID(更新者)
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Path("approval/{id}")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updateApproval(@PathParam("id") Long id, ApprovalEntity approval, @QueryParam("verInfo") Integer verInfo, @QueryParam("authId") Long authId) {
        logger.info("updateApproval: id={}, approval={}, authId={}", id, approval, authId);
        try {
            if (Objects.isNull(id)
                    || Objects.isNull(approval.getOrder())
                    // || Objects.isNull(approval.getApprove())
                    || Objects.isNull(approval.getApprover())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            KanbanEntity kanban = super.find(id);
            if (Objects.isNull(kanban)) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_KANBAN)).build();
            }

            if (!kanban.getVerInfo().equals(verInfo)) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            String json = kanban.getApproval();

            List<ApprovalEntity> list = JsonUtils.jsonToObjects(json, ApprovalEntity[].class);

            // カンバンに、該当する承認番号の承認情報が既にある場合は一旦削除する。
            Optional<ApprovalEntity> opt = list.stream()
                    .filter(p -> Objects.equals(p.getOrder(), approval.getOrder()))
                    .findFirst();
            if (opt.isPresent()) {
                list.remove(opt.get());
            }

            // 承認情報を追加する。
            list.add(approval);
            list.sort(Comparator.comparing(item -> item.getOrder()));

            kanban.setApproval(JsonUtils.objectsToJson(list));
            kanban.setUpdatePersonId(authId);
            kanban.setUpdateDatetime(new Date());

            // カンバン情報を更新する。
            super.edit(kanban);

            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * ロットトレース情報を更新する。
     *
     * @param kanbanId カンバンID
     * @param kanbanName カンバン名
     * @param modelName モデル名
     * @param workKanbanId 工程カンバンID
     * @param workName 工程名
     * @param organizationId 組織ID
     * @param organizationName 組織名
     * @param implementDatetime 実施日時
     * @param lotTraceParts ロットトレース部品情報一覧(JSON)
     */
    private void updateLotTrace(long kanbanId, String kanbanName, String modelName, long workKanbanId, String workName, Long organizationId, String organizationName, Date implementDatetime, String lotTraceParts) {
        if (StringUtils.isEmpty(lotTraceParts)) {
            return;
        }

        try {
            // 工程カンバンに紐づいているロットトレース情報から作業情報を削除する。
            this.clearLotTraceWorkInfo(workKanbanId);
            
            List<LotTracePartsInfo> partsList = JsonUtils.jsonToObjects(lotTraceParts, LotTracePartsInfo[].class);
            if (partsList.isEmpty()) {
                return;
            }

            Date now = new Date();
            
            String personNo = null;
            if (Objects.nonNull(organizationId)) {
                personNo = this.organizationRest.findIdentifyById(organizationId);
            }

            // ロットトレース情報を更新する。
            for (LotTracePartsInfo parts : partsList) {
                
                //TrnLotTrace lotTrace = this.findLotTrace(parts.getDeliveryNo(), parts.getItemNo(), parts.getMaterialNo(), workKanbanId);
                //if (Objects.isNull(lotTrace)) {
                    if (StringUtils.isEmpty(parts.getMaterialNo())) {
                        continue;
                    }
                    
                    if (parts.getIsDelete()) {
                        continue;
                    }
                
                    TrnMaterial material = this.warehouseModel.findMaterial(parts.getMaterialNo(), false);
                    if (Objects.isNull(material)) {
                        // 資材情報が見つからない
                        logger.info("TrnMaterial does not exist: " + parts.getMaterialNo());              
                        continue;
                    }
                    
                    if (StringUtils.isEmpty(material.getPartsNo())) {
                        // 在庫データはロット番号が採番されていないものがある
                        material.setPartsNo(this.warehouseModel.nextPartsNo(material.getProduct().getProductId()));
                    }

                    // 新規登録
                    TrnLotTrace lotTrace = new TrnLotTrace(parts.getDeliveryNo(), parts.getItemNo(), parts.getMaterialNo(), workKanbanId, now);
                    lotTrace.setTraceNum(parts.getTraceNum());
                    lotTrace.setPartsNo(material.getPartsNo());
                    em.persist(lotTrace);
                    
                    TrnDeliveryItem deliveryItem = this.warehouseModel.findDeliveryItem(parts.getDeliveryNo(), parts.getItemNo(), true);
                    lotTrace.setDeliveryItem(deliveryItem);
                //}
                
                //if (parts.isDelete()) {
                //    lotTrace.setDisabled(false);
                //    lotTrace.setUpdateDate(now);
                //    continue;
                //}
               
                lotTrace.setTraceNum(parts.getTraceNum());
                lotTrace.setKanbanId(kanbanId);
                lotTrace.setKanbanName(kanbanName);
                lotTrace.setModelName(modelName);
                lotTrace.setWorkName(workName);
                lotTrace.setPersonNo(personNo);
                lotTrace.setPersonName(organizationName);
                lotTrace.setAssemblyDatetime(implementDatetime);
                lotTrace.setSerialNo(parts.getSerialNo());
                lotTrace.setConfirm(parts.getIsDone());
                lotTrace.setDisabled(false);
                lotTrace.setUpdateDate(now);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程カンバンに紐づいているロットトレース情報から作業情報を削除する。
     *
     * @param workKanbanId 工程カンバンID
     */
    private void clearLotTraceWorkInfo(long workKanbanId) {
        Query query = this.em.createNamedQuery("TrnLotTrace.deleteWorkInfo");
        query.setParameter("workKanbanId", workKanbanId);
        query.executeUpdate();
        this.em.flush();
    }

    /**
     * ロットトレース情報を取得する。
     *
     * @param deliveryNo 出庫番号
     * @param itemNo 明細番号
     * @param materialNo 資材番号
     * @param workKanbanId カンバンID
     * @return ロットトレース情報
     */
    private TrnLotTrace findLotTrace(String deliveryNo, int itemNo, String materialNo, Long workKanbanId) {
        try {
            TypedQuery<TrnLotTrace> query = em.createNamedQuery("TrnLotTrace.find", TrnLotTrace.class);
            query.setParameter("deliveryNo", deliveryNo);
            query.setParameter("itemNo", itemNo);
            query.setParameter("materialNo", materialNo);
            query.setParameter("workKanbanId", workKanbanId);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
     
    /**
     * ラベル情報を更新する。
     *
     * @param id カンバンID
     * @param labelIds ラベルID一覧
     * @param verInfo 排他用バージョン
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @PUT
    @Path("label/{id}")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updateLabel(@PathParam("id") Long id, @QueryParam("label") List<Long> labelIds, @QueryParam("verInfo") Integer verInfo, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("updateLabel: kanbanId={}, authId={}", id, authId);

        ServerErrorTypeEnum errorType = ServerErrorTypeEnum.SUCCESS;

        try {
            KanbanEntity kanban = super.find(id);
            if (Objects.isNull(kanban)) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_KANBAN)).build();
            }

            if (!kanban.getVerInfo().equals(verInfo)) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // ラベルID一覧をJSON文字列に変換
            String kanbanLabel = JsonUtils.objectsToJson(labelIds);
            kanban.setKanbanLabel(kanbanLabel);

            kanban.setUpdatePersonId(authId);
            kanban.setUpdateDatetime(new Date());

            // カンバン情報を更新
            super.edit(kanban);
            
        } catch (Exception ex) {
            logger.warn(ex, ex);
            errorType = ServerErrorTypeEnum.NOTFOUND_UPDATE;
        }

        return Response.ok().entity(ResponseEntity.success().errorType(errorType)).build();
    }

    /**
     * カンバン親PIDまたはカンバン名を指定して、使用部品情報の件数を取得する。
     *
     * @param kanbanPartsId カンバン親PID
     * @param kanbanName カンバン名
     * @param authId 認証ID
     * @return 件数
     * @throws URISyntaxException 
     */
    @Lock(LockType.READ)
    @GET
    @Path("parts/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countAssemblyParts(@QueryParam("parentPid") String kanbanPartsId, @QueryParam("name") String kanbanName, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("countAssemblyParts: kanbanPartsId={}, kanbanName={}, authId={}", kanbanPartsId, kanbanName, authId);
        try {
            long count;
            if (!StringUtils.isEmpty(kanbanPartsId)) {
                count = this.assemblyPartsModel.countByKanbanPartsId(kanbanPartsId, authId);
            } else {
                count = this.assemblyPartsModel.countByKanbanName(kanbanName, authId);
            }

            return String.valueOf(count);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン親PIDまたはカンバン名を指定して、使用部品情報一覧を取得する。
     *
     * @param kanbanPartsId カンバン親PID
     * @param kanbanName カンバン名
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 使用部品情報一覧
     * @throws URISyntaxException
     */
    @GET
    @Path("parts")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<AssemblyPartsEntity> findAssemblyParts(@QueryParam("parentPid") String kanbanPartsId, @QueryParam("name") String kanbanName, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("findAssemblyParts: kanbanPartsId={}, kanbanName={}, authId={}", kanbanPartsId, kanbanName, authId);
        try {
            if (!StringUtils.isEmpty(kanbanPartsId)) {
                return this.assemblyPartsModel.findByKanbanPartsId(kanbanPartsId, from, to, authId);
            } else {
                return this.assemblyPartsModel.findByKanbanName(kanbanName, from, to, authId);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 使用部品情報を更新する。
     *
     * @param request 使用部品更新要求
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @PUT
    @Path("parts")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    @Transactional
    public Response updateParts(AssemblyPartsUpdateRequest request, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("updateParts: request={}, authId={}", request, authId);
        try {
            ResponseEntity res = this.assemblyPartsModel.updateParts(request.getPids(), request.getAssembleds(), request.getVerInfos(), request.getAssembledDate(), authId);
            return Response.ok().entity(res).build();
        } catch (Exception ex){
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }
	
    /**
     * JSONファイルから組織をインポートする。
     *
     * @param inputStreams JSONファイル(FormDataParam)
     * @param authId
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @POST
    @Path(value="import")
    @Consumes("multipart/form-data")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response importFile(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("authId") Long authId) {
        logger.info("importFile: start");

        final Date now = new Date();

        String fileName = "import-" + new SimpleDateFormat("yyyyMMddHHmmss").format(now) + ".json";

        FileManager fileManager = FileManager.getInstance();
        String filePath = fileManager.getLocalePath(FileManager.Data.Import, fileName);
        logger.info("importFile: " + filePath);

        BufferedInputStream bf = new BufferedInputStream(inputStreams.get(0));
        byte[] buff = new byte[1024];
        File file = new File(filePath);

        try (OutputStream out = new FileOutputStream(file)) {
            int len = -1;
            while ((len = bf.read(buff)) >= 0) {
                out.write(buff, 0, len);
            }
            out.flush();
        } catch (Exception ex) {
            // ************ ファイル書き込みに失敗　-> サーバー処理エラー　SERVER_FETAL
            logger.fatal(ex, ex);

            ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
            MessageEntity message = new MessageEntity("%s", "key.FaildToProcess");
            ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
            return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
        }

        String jsonStr = null;
        try {
            jsonStr = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8).collect(Collectors.joining(System.getProperty("line.separator")));
        } catch (IOException ex) {
            // *********** jsonの書き込みに失敗　-> サーバー処理エラー　SERVER_FETAL
            logger.fatal(ex, ex);
            ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
            MessageEntity message = new MessageEntity("%s", "key.FaildToProcess");
            ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
            return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
        }
        List<KanbanInfoEntity> kanbanInfoEntityList = JsonUtils.jsonToObjects(jsonStr, KanbanInfoEntity[].class);

        Map<String, KanbanHierarchyEntity> kanbanHierarchyEntityMap = new HashMap<>(); // 階層マップ
        Map<String, WorkflowEntity> workflowEntityMap = new HashMap<>();               // 工程順マップ

        // 組織マップ
        final Map<String, OrganizationEntity> organizationEntityMap = this.organizationRest.find((List<Long>)null, authId).stream().collect(toMap(OrganizationEntity::getOrganizationIdentify, Function.identity()));
        // 設備マップ
        final Map<String, EquipmentEntity> equipmentEntityMap = this.equipmentRest.find((List<Long>)null, authId).stream().collect(toMap(EquipmentEntity::getEquipmentIdentify, Function.identity()));

        logger.info("****** Start Regist Kanban");
        List<ResultResponse> retList = new ArrayList<>();

        Map<Long, Set<String>> workNameMap = new HashMap<>();
        Map<Long, Set<String>> separateWorkNameMap = new HashMap<>();

        for (KanbanInfoEntity kanbanInfoEntity : kanbanInfoEntityList) {
            final String kanbanName = kanbanInfoEntity.getKanbanName();

            // 階層マップの更新
            final String parentName = kanbanInfoEntity.getParentName();
            if (!kanbanHierarchyEntityMap.containsKey(parentName)) {
                kanbanHierarchyEntityMap.put(parentName, kanbanHierarchyRest.findHierarchyByName(kanbanInfoEntity.getParentName(), loginUserId, authId));
            }

            // 階層有無確認
            KanbanHierarchyEntity kanbanHierarchyEntity = kanbanHierarchyEntityMap.get(parentName);
            if (Objects.isNull(kanbanHierarchyEntity) || Objects.isNull(kanbanHierarchyEntity.getKanbanHierarchyId())) {
                // *************** 階層が無い　-> NOTFOUND_PARENT
                logger.info("Not Found Kanban Hierarchy : KanbanName={} Hierarchy={}", kanbanName, parentName);

                MessageEntity message = new MessageEntity("[" + kanbanName + "] > %s [" + parentName + "]", "key.ImportKanban_HierarchyNothing");

                ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.NOTFOUND_PARENT);
                ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                retList.add(ret);
                continue;
            }
            kanbanInfoEntity.setParentId(kanbanHierarchyEntity.getKanbanHierarchyId());

            // 工程順マップの更新
            final String workflowName = kanbanInfoEntity.getWorkflowName();
            final Integer workflowRev = kanbanInfoEntity.getWorkflowRev();
            final String workflowNameAndRev = workflowName + ":" + workflowRev;
            if (!workflowEntityMap.containsKey(workflowNameAndRev)) {
                WorkflowEntity workflowEntity = workflowRest.findByName(workflowName, workflowRev, true, loginUserId, authId);
                if (Objects.nonNull(workflowEntity) && Objects.nonNull(workflowEntity.getWorkflowId())) {
                    workflowEntityMap.put(workflowNameAndRev, workflowEntity);
                    workNameMap.put(workflowEntity.getWorkflowId(),
                            workflowEntity
                                    .getConWorkflowWorkCollection()
                                    .stream()
                                    .map(ConWorkflowWorkEntity::getWorkName)
                                    .collect(toSet()));

                    separateWorkNameMap.put(workflowEntity.getWorkflowId(),
                            workflowEntity
                                    .getConWorkflowSeparateworkCollection()
                                    .stream()
                                    .map(ConWorkflowWorkEntity::getWorkName)
                                    .collect(toSet()));
                }
            }

            // 工程順の確認
            WorkflowEntity workflowInfoEntity = workflowEntityMap.computeIfAbsent(workflowNameAndRev, key -> null);
            if (Objects.isNull(workflowInfoEntity) || Objects.isNull(workflowInfoEntity.getWorkflowId())) {
                // **************** 工程順が見つからない -> NOTFOUND_WORKFLOW
                logger.info("Not Found Workflow : kanbanName={} workflowName={}", kanbanName, workflowNameAndRev);

                MessageEntity message = new MessageEntity("[" + kanbanName + "] > %s [" + workflowNameAndRev + "]", "key.ImportKanban_WorkflowNothing");

                ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.NOTFOUND_WORKFLOW);
                ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                retList.add(ret);
                continue;
            }

            // 工程の情報が登録できるか確認
            if (Objects.nonNull(kanbanInfoEntity.getWorkKanbanCollection())) {
                Set<String> workNameSet = workNameMap.get(workflowInfoEntity.getWorkflowId());
                Optional<String> duplicateWorkKanbanName = kanbanInfoEntity.getWorkKanbanCollection()
                        .stream()
                        .map(WorkKanbanInfoEntity::getWorkName)
                        .filter(workKanban -> !workNameSet.contains(workKanban))
                        .findFirst();
                if (duplicateWorkKanbanName.isPresent()) {
                    MessageEntity message = new MessageEntity("[" + kanbanName + "] > %s [" + duplicateWorkKanbanName.get() + "]", "key.alert.notfound.workkanbanError");

                    ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.NOTFOUND_WORKKANBAN);
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    retList.add(ret);
                    continue;
                }
            }

            // 追加工程の情報が登録できるか確認
            if (Objects.nonNull(kanbanInfoEntity.getSeparateworkKanbanCollection())) {
                Set<String> separateWorkNameSet = separateWorkNameMap.get(workflowInfoEntity.getWorkflowId());
                Optional<String> duplicateSeparateWorkKanbanName = kanbanInfoEntity.getSeparateworkKanbanCollection()
                        .stream()
                        .map(WorkKanbanInfoEntity::getWorkName)
                        .filter(workKanban -> !separateWorkNameSet.contains(workKanban))
                        .findFirst();
                if (duplicateSeparateWorkKanbanName.isPresent()) {
                    MessageEntity message = new MessageEntity("[" + kanbanName + "] > %s [" + duplicateSeparateWorkKanbanName.get() + "]", "key.alert.notfound.workkanbanError");

                    ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.NOTFOUND_WORKKANBAN);
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    retList.add(ret);
                    continue;
                }
            }


            kanbanInfoEntity.setFkWorkflowId(workflowInfoEntity.getWorkflowId());

            // カンバン名を指定して、カンバン情報を取得する。
            TypedQuery<KanbanEntity> query;

            if (Objects.nonNull(kanbanInfoEntity.getWorkflowRev())) {
                query = this.em.createNamedQuery("KanbanEntity.findByNameAndRev", KanbanEntity.class);
                query.setParameter("kanbanName", kanbanName);
                query.setParameter("workflowName", workflowInfoEntity.getWorkflowName());
                query.setParameter("workflowRev", workflowInfoEntity.getWorkflowRev());
            } else {

                query = this.em.createNamedQuery("KanbanEntity.findLatest", KanbanEntity.class);
                query.setParameter(1, kanbanName);
                query.setParameter(2, workflowInfoEntity.getWorkflowName());
            }
            query.setMaxResults(1);

            KanbanEntity kanban = null;
            KanbanStatusEnum kanbanInitStatus = KanbanStatusEnum.PLANNED;
            try {
                kanban = query.getSingleResult();
                kanbanInitStatus = kanban.getKanbanStatus();
            } catch (NoResultException ex) {
                logger.fatal(ex, ex);
                kanban = new KanbanEntity();
            }
            Long kanbanId = null;
            boolean existKanban = Objects.nonNull(kanban) && Objects.nonNull(kanban.getKanbanId());
            if (existKanban) {
                kanbanId = kanban.getKanbanId();
                if (!kanban.getKanbanStatus().isKanbanUpdatableStatus || KanbanStatusEnum.COMPLETION.equals(kanban.getKanbanStatus())) {
                    // ***************** カンバンのステータスが変更不可 -> THERE_START_NON_EDITABLE
                    logger.info("No Edit Status : kanbanName={}, kanbanStatus={}", kanbanName, kanban.getKanbanStatus());
                    ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.THERE_START_NON_EDITABLE);

                    MessageEntity message = new MessageEntity(" > カンバンが反映できないステータスです: %s [" + kanbanName + "] ", kanban.getKanbanStatus().getResourceKey());
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    retList.add(ret);
                    continue;
                }

                if (kanban.getKanbanStatus().equals(KanbanStatusEnum.COMPLETION)) {
                    kanbanInfoEntity.setKanbanStatus(KanbanStatusEnum.COMPLETION);
                }

                // 仕掛中
                final boolean isWorking = Stream.of(this.getWorkKanban(kanban.getKanbanId()), this.getSeparateWorkKanban(kanban.getKanbanId()))
                        .flatMap(Collection::stream)
                        .map(WorkKanbanEntity::getWorkStatus)
                        .anyMatch(status -> KanbanStatusEnum.WORKING.equals(status));
                if (isWorking) {
                    // *************** 仕掛中である。 -> THERE_START_NON_DELETABLE
                    logger.info("No Edit Status & Working : kanbanName={} ", kanbanName);
                    ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.THERE_START_NON_DELETABLE);
                    MessageEntity message = new MessageEntity(" > %s [" + kanbanName + "]", "key.CanNotRegistWorkingWorkKanban");
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    retList.add(ret);
                    continue;
                }

                // 工程順が更新されている
                if (!workflowInfoEntity.getWorkflowId().equals(kanban.getWorkflowId())) {
                    final boolean isWorked = Stream.of(this.getWorkKanban(kanban.getKanbanId()), this.getSeparateWorkKanban(kanban.getKanbanId()))
                            .flatMap(Collection::stream)
                            .map(WorkKanbanEntity::getWorkStatus)
                            .filter(status -> !KanbanStatusEnum.WORKING.equals(status))
                            .anyMatch(status -> !KanbanStatusEnum.PLANNED.equals(status));
                    if (isWorked) {
                        // *************** 仕掛中である。 -> THERE_START_NON_DELETABLE
                        logger.info("Workflow No Edit Status & Working : kanbanName={} ", kanbanName);
                        ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.THERE_START_NON_DELETABLE);
                        MessageEntity message = new MessageEntity(" > %s [" + kanbanName + "]", "key.KanbanCannotUpdateByNewWorkFlow");
                        ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                        retList.add(ret);
                        continue;
                    }

                    KanbanEntity kanbanEntity = this.find(kanban.getKanbanId(), authId);
                    // 工程カンバン情報を削除する。
                    this.removeWorkKanban(kanbanEntity);
                    // カンバンの階層関連付け情報を削除する。
                    this.removeHierarchy(kanbanEntity.getKanbanId());
                    // 製品情報を削除する。
                    this.removeProducts(kanbanEntity.getKanbanId());
                    // カンバン帳票情報を削除する。
                    this.kanbanReportRest.removeByKanbanId(Collections.singletonList(kanbanEntity.getKanbanId()));
                    // カンバン情報をを削除する。
                    this.remove(kanbanEntity);
                    existKanban = false;
                    em.clear();
                } else {
                    if (Objects.nonNull(kanbanInfoEntity.getStartDatetime())) {
                        kanban.setStartDatetime(kanbanInfoEntity.getStartDatetime());
                        em.persist(kanban);
                    }
                }
            }

            if (!existKanban) {
                // 新規作成
                Response response;
                if (kanbanInfoEntity.getProductionType() == 1) {
                    // ロット一個流し生産のカンバンを登録
                    KanbanCreateCondition cond = new KanbanCreateCondition(
                            kanbanInfoEntity.getKanbanName(),   // カンバン名
                            kanbanInfoEntity.getFkWorkflowId(), // 工程順ID
                            kanbanInfoEntity.getParentId(),     // 階層ID
                            Objects.isNull(loginUserId) ? "0" : loginUserId.toString(),
                            true,
                            kanbanInfoEntity.getLotQuantity(),  // ロット数
                            kanbanInfoEntity.getStartDatetime(),// 開始日時
                            new ArrayList<>(),
                            kanbanInfoEntity.getProductionType()// 生産タイプ
                    );
                    response = this.createKanban(cond, authId);
                } else {
                    // 一個流し生産 or ロット生産のカンバンを登録
                    KanbanEntity newKanban = new KanbanEntity();
                    newKanban.setKanbanName(kanbanInfoEntity.getKanbanName());             // カンバン名
                    newKanban.setWorkflowId(kanbanInfoEntity.getFkWorkflowId());           // 工程順ID
                    newKanban.setParentId(kanbanInfoEntity.getParentId());                 // 階層ID
                    newKanban.setLotQuantity(kanbanInfoEntity.getLotQuantity());           // ロット数
                    newKanban.setStartDatetime(kanbanInfoEntity.getStartDatetime());       // 開始日時
                    newKanban.setProductionType(kanbanInfoEntity.getProductionType());     // 生産タイプ

                    try {
                        response = this.add(newKanban, authId);
                    } catch (URISyntaxException ex) {
                        //////  ありあえない -> SERVER_FETAL
                        logger.info("ExceptionError : kanbanName={} ", kanbanName);
                        logger.fatal(ex, ex);

                        ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
                        MessageEntity message = new MessageEntity(" > %s[" + kanbanName + "]", "key.FaildToProcess");
                        ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                        retList.add(ret);
                        continue;
                    }
                }
                ResponseEntity res = (ResponseEntity) response.getEntity();
                if (!res.isSuccess()) {
                    logger.info("Kanban Can't Create Error: kanbanName={} ", kanbanName);
                    /// ************* エラー内容を返す
                    ResultResponse ret = ResultResponse.failed(res.getErrorType());
                    MessageEntity message = new MessageEntity(" > %s [" + kanbanName + "]", "key.FaildToProcess");
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    retList.add(ret);
                    continue;
                }

                // 作成した補充カンバンのカンバンIDを取得する。
                int pos = res.getUri().lastIndexOf("/");
                String idStr = res.getUri().substring(pos + 1);

                kanbanId = Long.valueOf(idStr);
            }

            // 作成した補充カンバンを取得する。
            kanban = this.find(kanbanId, authId);
            kanban.setUpdateDatetime(kanbanInfoEntity.getUpdateDatetime());
            kanban.setUpdatePersonId(kanbanInfoEntity.getFkUpdatePersonId());

            // 中止に状態を変更
            kanban.setKanbanStatus(KanbanStatusEnum.INTERRUPT);
            //this.updateStatus(Collections.singletonList(kanbanId), "INTERRUPT", false, authId);
            this.em.flush();
            this.em.clear();

            // カンバンプロパティ設定
            List<KanbanPropertyInfoEntity> kanbanPropertyInfoEntities =
                    JsonUtils.jsonToObjects(kanban.getKanbanAddInfo(), KanbanPropertyInfoEntity[].class).stream()
                            .sorted(Comparator.comparingInt(KanbanPropertyInfoEntity::getKanbanPropertyOrder))
                            .collect(toList());
            // プロパティを追加
            kanbanPropertyInfoEntities.addAll(kanbanInfoEntity.getPropertyCollection());
            // 重複を取り除く
            kanbanPropertyInfoEntities = new ArrayList<>(kanbanPropertyInfoEntities.stream()
                    .collect(toMap(KanbanPropertyInfoEntity::getKanbanPropertyName, Function.identity(), (oldVal, newVal) -> newVal, LinkedHashMap::new))
                    .values());

            // 表示順を更新
            AtomicInteger orderNum = new AtomicInteger(0);
            kanbanPropertyInfoEntities.forEach(p -> p.setKanbanPropertyOrder(orderNum.getAndIncrement()));

            kanban.setKanbanAddInfo(JsonUtils.objectToJson(kanbanPropertyInfoEntities));

            if (Objects.isNull(kanbanInfoEntity.getWorkKanbanCollection())) {
                logger.info("workKanban is Empty");
                final KanbanStatusEnum kanbanStatus = Objects.isNull(kanbanInfoEntity.getKanbanStatus()) ? KanbanStatusEnum.PLANNED : kanbanInfoEntity.getKanbanStatus();
                kanban.setKanbanStatus(kanbanStatus);
                this.update(kanban, authId);
                ResultResponse ret = ResultResponse.success().errorType(ServerErrorTypeEnum.SUCCESS);
                retList.add(ret);
                continue;
            }

            Map<String, WorkKanbanInfoEntity> workKanbanInfoEntityMap
                    = kanbanInfoEntity
                    .getWorkKanbanCollection()
                    .stream()
                    .collect(toMap(WorkKanbanInfoEntity::getWorkName, Function.identity()));

            List<MessageEntity> msg = new ArrayList<>();
            boolean isWorKanbanTimeUpdate = false;
            List<WorkKanbanEntity> reportWork = new ArrayList<>();
            ////////////////////////////// 工程カンバン更新 //////////////////////////////////
            for(WorkKanbanEntity workKanban : kanban.getWorkKanbanCollection()) {
                if(Objects.isNull(workKanban) || Objects.isNull(workKanban.getWorkKanbanId())) {
                    // 工程カンバンが見つからない
                    logger.info("not Found WorkKanban: kanbanName={}", kanbanName);
                    msg.add(new MessageEntity("    >> %s ["+ workKanban.getWorkName() + "]" , "key.alert.notfound.workkanbanError"));
                    continue;
                }

                WorkKanbanInfoEntity workKanbanInfoEntity = workKanbanInfoEntityMap.get(workKanban.getWorkName());
                if (Objects.isNull(workKanbanInfoEntity)) {
                    // 更新データが無い
                    logger.info("not Found WorkKanban UpdateDate : kanbanName={} workKanbanName={}", kanbanName, workKanban.getWorkName());
                    //msg.add(new MessageEntity("   ["+workKanban.getWorkName()+"] >> %s", "key.impprt.data.not"));
                    continue;
                }

                workKanban.setSkipFlag(workKanbanInfoEntity.getSkipFlag());  // スキップフラグ

                final KanbanStatusEnum nowState = workKanban.getWorkStatus();
                final KanbanStatusEnum plan = workKanbanInfoEntity.getWorkStatus();
                if (KanbanStatusEnum.WORKING.equals(nowState)
                        || (KanbanStatusEnum.COMPLETION.equals(nowState) && !KanbanStatusEnum.COMPLETION.equals(plan))
                        || (KanbanStatusEnum.PLANNING.equals(nowState) && !KanbanStatusEnum.PLANNED.equals(plan))) {
                    // 工程カンバンが変更不可状態
                    logger.info("No Edit WorkKanban Status: kanbanName={} workKanbanName={} workKanbanStatus={}", kanbanName, workKanban.getWorkName(), workKanban.getWorkStatus());
                    msg.add(new MessageEntity("    %s (%s)["+workKanban.getWorkName()+"] >>",  "key.ImportKanban_NotUpdateWorkKanban", workKanban.getWorkStatus().getResourceKey()));
                    continue;
                }

                if (Objects.nonNull(workKanbanInfoEntity.getTaktTime())) {
                    workKanban.setTaktTime(workKanbanInfoEntity.getTaktTime());  // タクトタイム
                }

                if(Objects.nonNull(workKanbanInfoEntity.getStartDatetime()) && Objects.nonNull(workKanbanInfoEntity.getCompDatetime())) {
                    //両方とも設定されている
                    isWorKanbanTimeUpdate = true;
                    workKanban.setStartDatetime(workKanbanInfoEntity.getStartDatetime()); // 開始時間
                    workKanban.setCompDatetime(workKanbanInfoEntity.getCompDatetime());   // 完了時間
                } else if (Objects.nonNull(workKanbanInfoEntity.getStartDatetime())){
                    // 開始のみ設定されている
                    isWorKanbanTimeUpdate = true;
                    workKanban.setStartDatetime(workKanbanInfoEntity.getStartDatetime()); // 開始時間
                    workKanban.setCompDatetime(workKanbanInfoEntity.getStartDatetime());   // 完了時間
                } else if (Objects.nonNull(workKanbanInfoEntity.getCompDatetime())){
                    // 完了のみ設定されている
                    isWorKanbanTimeUpdate = true;
                    workKanban.setStartDatetime(workKanbanInfoEntity.getCompDatetime()); // 開始時間
                    workKanban.setCompDatetime(workKanbanInfoEntity.getCompDatetime());   // 完了時間
                }

                // 設備登録
                final List<String> equipmentIdentifyCollection = workKanbanInfoEntity.getEquipmentIdentifyCollection();
                if(Objects.nonNull(equipmentIdentifyCollection) && !equipmentIdentifyCollection.isEmpty()) {

                    // 設備関連付けを削除
                    workKandanRest.removeEquipmentConnection(workKanban.getWorkKanbanId());

                    // 設備一覧の作成
                    List<Long> equipmentIdCollection
                            = equipmentIdentifyCollection
                            .stream()
                            .map(equipmentEntityMap::get)
                            .filter(Objects::nonNull)
                            .map(EquipmentEntity::getEquipmentId)
                            .collect(toList());

                    // 設備一覧の登録
                    equipmentIdCollection
                            .forEach(equipmentId -> {
                                ConWorkkanbanEquipmentEntity con = new ConWorkkanbanEquipmentEntity(workKanban.getWorkKanbanId(), equipmentId);
                                this.em.persist(con);
                            });

                    workKanban.setEquipmentCollection(equipmentIdCollection);
                    if(equipmentIdentifyCollection.isEmpty()) {
                        logger.info("not Found Equipment : kanbanName={} workKanbanName={}, equipment={}", kanbanName, workKanban.getWorkName(), String.join(",", equipmentIdentifyCollection));
                        //msg.add(new MessageEntity("   ["+workKanban.getWorkName()+"] > %s ("+String.join(",", equipmentIdentifyCollection)+")",  "key.ImportKanban_EquipmentNothing"));
                    }
                }

                // 組織登録
                final List<String> organizationIdentifyCollection = workKanbanInfoEntity.getOrganizationIdentifyCollection();
                if(Objects.nonNull(organizationIdentifyCollection) && !organizationIdentifyCollection.isEmpty()) {

                    workKandanRest.removeOrganizationConnection(workKanban.getWorkKanbanId());

                    // 組織一覧の作成
                    List<Long> organizationIdCollection =
                            organizationIdentifyCollection
                                    .stream()
                                    .map(organizationEntityMap::get)
                                    .filter(Objects::nonNull)
                                    .map(OrganizationEntity::getOrganizationId)
                                    .collect(toList());

                    // 組織一覧の登録
                    organizationIdCollection.forEach(organizationId -> {
                        ConWorkkanbanOrganizationEntity con = new ConWorkkanbanOrganizationEntity(workKanban.getWorkKanbanId(), organizationId);
                        this.em.persist(con);
                    });

                    workKanban.setOrganizationCollection(organizationIdCollection);
                    if(organizationIdCollection.isEmpty()) {
                        logger.info("not Found Organization : kanbanName={} workKanbanName={}, organization={}", kanbanName, workKanban.getWorkName(), String.join(",", organizationIdentifyCollection));
                        //msg.add(new MessageEntity("   ["+workKanban.getWorkName()+"] >> %s ("+String.join(",", organizationIdentifyCollection)+")",  "key.ImportKanban_OrganizationNothing"));
                    }
                }

                // 工程カンバンプロパティ設定
                List<WorkKanbanPropertyInfoEntity> workKanbanPropertyInfoEntityCollection =
                        JsonUtils.jsonToObjects(workKanban.getWorkKanbanAddInfo(), WorkKanbanPropertyInfoEntity[].class)
                                .stream()
                                .sorted(Comparator.comparingInt(WorkKanbanPropertyInfoEntity::getWorkKanbanPropOrder))
                                .collect(toList());
                // プロパティを追加
                workKanbanPropertyInfoEntityCollection.addAll(workKanbanInfoEntity.getPropertyCollection());
                // 重複を取り除く
                workKanbanPropertyInfoEntityCollection = new ArrayList<>(workKanbanPropertyInfoEntityCollection
                        .stream()
                        .collect(toMap(WorkKanbanPropertyInfoEntity::getWorkKanbanPropName, Function.identity(), (oldVal, newVal)->newVal , LinkedHashMap::new))
                        .values());
                // 表示順を更新
                AtomicInteger order = new AtomicInteger(0);
                workKanbanPropertyInfoEntityCollection.forEach(p->p.setWorkKanbanPropOrder(order.getAndIncrement()));
                workKanban.setWorkKanbanAddInfo(JsonUtils.objectToJson(workKanbanPropertyInfoEntityCollection));

                if(!KanbanStatusEnum.COMPLETION.equals(workKanban.getWorkStatus())
                        && !KanbanStatusEnum.WORKING.equals(workKanban.getWorkStatus())
                    && KanbanStatusEnum.COMPLETION.equals(workKanbanInfoEntity.getWorkStatus())) {
                    // 実績報告が必要な物は後で完了報告する
                    reportWork.add(workKanban);
                }
            }

            if (isWorKanbanTimeUpdate) {
                Optional<Date> startTime = kanban.getWorkKanbanCollection().stream().map(WorkKanbanEntity::getStartDatetime).min(Comparator.comparing(Function.identity()));
                Optional<Date> compTime = kanban.getWorkKanbanCollection().stream().map(WorkKanbanEntity::getCompDatetime).max(Comparator.comparing(Function.identity()));

                if (startTime.isPresent() && compTime.isPresent()) {
                    kanban.setStartDatetime(startTime.get());
                    kanban.setCompDatetime(compTime.get());
                } else if(startTime.isPresent()) {
                    kanban.setStartDatetime(startTime.get());
                } else if (compTime.isPresent()) {
                    kanban.setCompDatetime(compTime.get());
                } else {
                    kanban.setStartDatetime(now);
                    kanban.setCompDatetime(now);
                }
            } else {
                Map<Long, WorkKanbanEntity> workKanbanEntityMap
                        = kanban
                        .getWorkKanbanCollection()
                        .stream()
                        .collect(toMap(WorkKanbanEntity::getWorkKanbanId, Function.identity()));

                Date baseTime
                        = kanban
                        .getWorkKanbanCollection()
                        .stream()
                        .filter(workKanbanEntity -> KanbanStatusEnum.COMPLETION.equals(workKanbanEntity.getWorkStatus()) || KanbanStatusEnum.DEFECT.equals(workKanbanEntity.getWorkStatus()))
                        .map(WorkKanbanEntity::getActualCompTime)
                        .filter(Objects::nonNull)
                        .reduce(now, (a,b) -> a.after(b) ? a : b);

                if (Objects.nonNull(kanbanInfoEntity.getStartDatetime())
                        && kanbanInfoEntity.getStartDatetime().after(baseTime)){
                    // カンバンの開始時間が設定されている
                    baseTime = kanbanInfoEntity.getStartDatetime();
                }

                this.updateBaseTime(kanban, kanban.getWorkflow(), baseTime);
                // ↑で完了も更新されている為、完了した物の時間を復元
                kanban.setWorkKanbanCollection(kanban
                        .getWorkKanbanCollection()
                        .stream()
                        .map(workKanbanEntity -> {
                            WorkKanbanEntity workKanban = workKanbanEntityMap.get(workKanbanEntity.getWorkKanbanId());
                            if (Objects.nonNull(workKanban)
                                    && !KanbanStatusEnum.COMPLETION.equals(workKanban.getWorkStatus())
                                    && !KanbanStatusEnum.DEFECT.equals(workKanban.getWorkStatus())) {
                                workKanban.setStartDatetime(workKanbanEntity.getStartDatetime());
                                workKanban.setCompDatetime(workKanbanEntity.getCompDatetime());
                            }
                            return workKanban;
                        })
                        .filter(Objects::nonNull)
                        .collect(toList()));
            }

            if (StringUtils.nonEmpty(kanbanInfoEntity.getModelName())) {
                kanban.setModelName(kanbanInfoEntity.getModelName());               // モデル名
            }

            if (StringUtils.nonEmpty(kanbanInfoEntity.getProductionNumber())) {
                kanban.setProductionNumber(kanbanInfoEntity.getProductionNumber()); // 製造番号
            }

            kanban.setParentId((kanbanInfoEntity.getParentId()));               // カンバン階層

            // カンバンステータス
            KanbanStatusEnum kanbanStatus = kanbanInitStatus;
            KanbanStatusEnum changeStatus = kanbanInfoEntity.getKanbanStatus();
            if (Objects.nonNull(changeStatus)) {
                if (KanbanStatusEnum.PLANNING.equals(changeStatus)) {
                    boolean isWorking = kanban.getWorkKanbanCollection().stream().anyMatch(workKanbanEntity -> !KanbanStatusEnum.PLANNED.equals(workKanbanEntity.getWorkStatus()));
                    if (isWorking) {
                        kanbanStatus = KanbanStatusEnum.INTERRUPT;
                    } else {
                        kanbanStatus = KanbanStatusEnum.PLANNING;
                    }
                } else {
                    kanbanStatus = changeStatus;
                }
            }

            kanban.setKanbanStatus(kanbanStatus);
            this.update(kanban,authId);
            this.em.clear();

            Date date = new Date();

            // ***************** 実績登録
            WorkflowEntity workflowEntity = workflowRest.find(kanban.getWorkflowId());
            for (WorkKanbanEntity workKanban : reportWork) {
                workKanban.setActualStartTime(date);
                workKanban.setActualCompTime(date);

                ActualProductReportEntity report = new ActualProductReportEntity(0L,
                        workKanban.getKanbanId(),
                        workKanban.getWorkKanbanId(),
                        0L,
                        0L,
                        new Date(),
                        KanbanStatusEnum.COMPLETION,
                        ActualProductReportEntity.FORCED,
                        null);

                KanbanEntity kanbanEntity = this.find(workKanban.getKanbanId());
                if (Objects.nonNull(workflowEntity)) {
                    try {
                        this.registReport(report, kanbanEntity, workflowEntity, workKanban, false, 1, authId);
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }
            }

            ResultResponse ret = ResultResponse.success().errorType(ServerErrorTypeEnum.SUCCESS);

            if(!msg.isEmpty()) {
                msg.add(0, new MessageEntity("["+kanbanName+"]> "));
                ret.result(JsonUtils.objectsToJson(msg));
            }
            retList.add(ret);
        }

        logger.info("****** End Regist Kanban");
        return Response.ok().entity(new GenericEntity<List<ResultResponse>>(retList){}).build();

    }


    /**
     * EntityManager を取得する。
     *
     * @return EntityManager
     */
    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    /**
     * テスト用：EntityManager を設定する。
     *
     * @param em EntityManager
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    /**
     * テスト用：工程カンバン情報RESTを設定する。
     *
     * @param workKandanRest 工程カンバン情報REST 
     */
    public void setWorkKandanREST(WorkKanbanEntityFacadeREST workKandanRest) {
        this.workKandanRest = workKandanRest;
    }

    /**
     * テスト用：工程情報RESTを設定する。
     *
     * @param workRest 工程情報REST
     */
    public void setWorkRest(WorkEntityFacadeREST workRest) {
        this.workRest = workRest;
    }


    /**
     * テスト用
     * @param directActualEntityFacadeREST 直接工数REST
     */
    public void setDirectActualEntityFacadeREST(DirectActualEntityFacadeREST directActualEntityFacadeREST) {
        this.directActualEntityFacadeREST = directActualEntityFacadeREST;
    }

    /**
     * テスト用：工程順情報RESTを設定する。
     *
     * @param workflowRest 工程順情報REST
     */
    public void setWorkflowRest(WorkflowEntityFacadeREST workflowRest) {
        this.workflowRest = workflowRest;
    }

    /**
     * テスト用：工程実績情報RESTを設定する。
     *
     * @param actualResultRest 工程実績情報REST
     */
    public void setActualResultRest(ActualResultEntityFacadeREST actualResultRest) {
        this.actualResultRest = actualResultRest;
    }

    /**
     * テスト用：組織情報RESTを設定する。
     *
     * @param organizationRest 組織情報REST
     */
    public void setOrganizationRest(OrganizationEntityFacadeREST organizationRest) {
        this.organizationRest = organizationRest;
    }

    /**
     * テスト用：設備情報RESTを設定する。
     *
     * @param equipmentRest 設備情報REST
     */
    public void setEquipmentRest(EquipmentEntityFacadeREST equipmentRest) {
        this.equipmentRest = equipmentRest;
    }

    /**
     * テスト用：休憩時間情報RESTを設定する。
     *
     * @param breaktimeRest 休憩時間情報REST
     */
    public void setBreaktimeRest(BreaktimeEntityFacadeREST breaktimeRest) {
        this.breaktimeRest = breaktimeRest;
    }

    /**
     * テスト用：工程カンバン作業中情報RESTを設定する。
     *
     * @param workKanbanWorkingRest 工程カンバン作業中情報REST
     */
    public void setWorkKanbanWorkingRest(WorkKanbanWorkingEntityFacadeREST workKanbanWorkingRest) {
        this.workKanbanWorkingRest = workKanbanWorkingRest;
    }

    /**
     * テスト用：休日情報取得用RESTを設定する。
     *
     * @param holidayRest 休日情報取得用REST
     */
    public void setHolidayEntityFacadeREST(HolidayEntityFacadeREST holidayRest) {
        this.holidayRest = holidayRest;
    }

    /**
     * テスト用：カンバン階層情報RESTを設定する。
     *
     * @param kanbanHierarchyRest カンバン階層情報REST
     */
    public void setKanbanHierarchyEntityFacadeRest(KanbanHierarchyEntityFacadeREST kanbanHierarchyRest) {
        this.kanbanHierarchyRest = kanbanHierarchyRest;
    }

    /**
     * テスト用：完成品情報RESTを設定する。
     *
     * @param partsRest 完成品情報REST
     */
    public void setPartsEntityFacadeREST(PartsEntityFacadeREST partsRest) {
        this.partsRest = partsRest;
    }

    /**
     * テスト用：カンバン帳票情報RESTを設定する。
     *
     * @param kanbanReportRest カンバン帳票情報REST
     */
    public void setKanbanReportEntityFacedeREST(KanbanReportEntityFacadeREST kanbanReportRest) {
        this.kanbanReportRest = kanbanReportRest;
    }

    /**
     * テスト用:
     * @param operationRest
     */
    public void setOperationEntityFacadeREST(OperationEntityFacadeREST operationRest) {
        this.operationRest = operationRest;
    }

    /**
     * テスト用: 品番マスタ情報RESTを設定する
     * @param dsItemFacade 
     */
    public void setDsItemFacade(DsItemFacade dsItemFacade) {
        this.dsItemFacade = dsItemFacade;
    }
    
    /**
     * 使用部品情報をインポートする。
     *
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @PUT
    @Path("parts/import")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response importParts(@QueryParam("authId") Long authId) throws URISyntaxException {
        this.assemblyPartsModel.importParts();
        return Response.ok().build();
    }

    /**
     * 作業のやり直しをおこなうため、工程カンバン情報を更新する。
     * 
     * @param workKanbanIds 工程カンバンID一覧
     * @param serialNo シリアル番号
     * @return 処理結果
     */
    @PUT
    @Path("/rework")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public Response updateRework(@QueryParam("id") List<Long> workKanbanIds, @QueryParam("serialNo") String serialNo, @QueryParam("authId") Long authId) {
        logger.info("updateRework: workKanbanIds={}", workKanbanIds);

        if (Objects.isNull(workKanbanIds) || workKanbanIds.isEmpty()) {
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        for (Long workKanbanId : workKanbanIds) {
            WorkKanbanEntity workKanban = this.workKandanRest.find(workKanbanId, authId);
            if (Objects.isNull(workKanban) || !workKanban.getImplementFlag()) {
                continue;
            }

            workKanban.setWorkStatus(KanbanStatusEnum.PLANNED);
            workKanban.setActualStartTime(null);
            workKanban.setActualCompTime(null);
            if (Objects.isNull(workKanban.getReworkNum())) {
                workKanban.setReworkNum(1);
            } else {
                workKanban.setReworkNum(workKanban.getReworkNum() + 1);
            }

            KanbanEntity kanban = this.find(workKanban.getKanbanId());
            if (KanbanStatusEnum.COMPLETION.equals(kanban.getKanbanStatus())) {
                // カンバンのステータスが作業完了の場合、計画済に戻す
                this.updateStatus(Arrays.asList(workKanban.getKanbanId()), KanbanStatusEnum.PLANNED.toString(), false, authId);
            }
            
            // カンバン情報のサービス情報を更新
            if (!StringUtils.isEmpty(serialNo) && !StringUtils.isEmpty(workKanban.getServiceInfo())) {
                List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(kanban.getServiceInfo(), ServiceInfoEntity[].class);
                for (ServiceInfoEntity serviceInfo : serviceInfos) {
                    if (StringUtils.equals(serviceInfo.getService(), "product")) {
                        List<KanbanProduct> products = KanbanProduct.toKanbanProducts(serviceInfo);
                        
                        for (KanbanProduct product : products) {
                            if (StringUtils.equals(serialNo, product.getUid())) {
                                product.setDefect("");
                                break;
                            }
                        }

                        serviceInfo.setJob(products);
                        kanban.setServiceInfo(JsonUtils.objectsToJson(serviceInfos));
                        break;
                    }
                }
            }
        }
        
        return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
    }

    /**
     * 使用部品情報をインポートする。
     *
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Path(value="/product-plan/import")
    @Consumes("multipart/form-data")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response importPlanInfo(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("authId") Long authId) {

        Function<WorkEntity, String> getWorkCode = (WorkEntity workEntity) -> {
            List<AddInfoEntity> list = jsonToObjects(workEntity.getWorkAddInfo(), AddInfoEntity[].class);
            if (Objects.isNull(list) || list.isEmpty()) {
                return "";
            }

            return list.stream()
                    .filter(item->StringUtils.equals("工程コード", item.getKey()))
                    .findFirst()
                    .map(AddInfoEntity::getVal)
                    .orElse("");
        };


        logger.info("importPlanInfo: start");

        final Date now = new Date();

        String fileName = "import-" + new SimpleDateFormat("yyyyMMddHHmmss").format(now) + ".json";

        FileManager fileManager = FileManager.getInstance();
        String filePath = fileManager.getLocalePath(FileManager.Data.Import, fileName);
        logger.info("importFile: " + filePath);

        BufferedInputStream bf = new BufferedInputStream(inputStreams.get(0));
        byte[] buff = new byte[1024];
        File file = new File(filePath);

        try (OutputStream out = new FileOutputStream(file)) {
            int len = -1;
            while ((len = bf.read(buff)) >= 0) {
                out.write(buff, 0, len);
            }
            out.flush();
        } catch (Exception ex) {
            // ************ ファイル書き込みに失敗　-> サーバー処理エラー　SERVER_FETAL
            logger.fatal(ex, ex);

            ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
            MessageEntity message = new MessageEntity("%s", "key.FaildToProcess");
            ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
            return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
        }

        String jsonStr = null;
        try {
            jsonStr = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8).collect(Collectors.joining(System.getProperty("line.separator")));
        } catch (IOException e) {
            // *********** jsonの書き込みに失敗　-> サーバー処理エラー　SERVER_FETAL
            e.printStackTrace();
            ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
            MessageEntity message = new MessageEntity("%s", "key.FaildToProcess");
            ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
            return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
        }

        try {
            List<ProductPlanEntity> productInfoList = JsonUtils.jsonToObjects(jsonStr, ProductPlanEntity[].class);
            Map<String, String> workList
                    = productInfoList
                    .stream()
                    .map(ProductPlanEntity::getWorkName)
                    .distinct()
                    .map(workName -> workRest.findByName(workName, null, null, null, null))
                    .collect(toMap(WorkEntity::getWorkName, getWorkCode, (a, b) -> a));

            // 工程コードを設定
            productInfoList.forEach(item -> item.setWorkCode(workList.getOrDefault(item.getWorkName(), item.getWorkCode())));

            // 全件削除
            Query query = this.em.createNamedQuery("ProductPlanEntity.deleteAll", ProductPlanEntity.class);
            query.executeUpdate();
            em.flush();

            // 月初計画取込
            productInfoList.forEach(productInfo -> em.persist(productInfo));
            em.flush();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }

        return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
    }

    /**
     * 工程順名からカンバンを取得する
     * @param workflowName 工程順名
     * @param authId 認証ID
     * @return カンバン情報
     */
    @Lock(LockType.READ)
    public List<KanbanEntity> findByWorkflowName(List<String> workflowName, Long authId)
    {
        if(workflowName.isEmpty()) {
            return new ArrayList<>();
        }

        Query query = this.em.createNamedQuery("KanbanEntity.findByWorkflowName", KanbanEntity.class);
        query.setParameter("workflowName", workflowName);
        return query.getResultList();

    }

    /**
     * カンバンIDから親階層を取得すrう
     * @param kanbanIds カンバンID
     * @return 親階層
     */
    public List<ConKanbanHierarchyEntity> findParentEntityByKanbanIds(List<Long> kanbanIds) {

        if(kanbanIds.isEmpty()) {
            return new ArrayList<>();
        }

        // カンバンの階層関連付け情報を更新する。
        Query query = this.em.createNamedQuery("ConKanbanHierarchyEntity.findByKanbanIds");
        query.setParameter("kanbanIds", kanbanIds);

        return query.getResultList();
    }
    
    /**
     * 工程実績を一括登録する。
     * 
     * @param param
     * @param authId 組織ID
     * @return 工程実績登録結果
     */
    @POST
    @Path("lite/report/batch")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response reportBatch(ReportBatchParam param, @QueryParam("authId") Long authId) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("reports=");
        if (Objects.nonNull(param.getReports())) {
            param.getReports().stream().forEach(o -> sb.append(o).append(" "));
        }
        sb.append(", operations=");
        if (Objects.nonNull(param.getOperations())) {
            param.getOperations().stream().forEach(o -> sb.append(o).append(" "));
        }

        logger.info("reportBatch: status={}, {}, authId={}", param.getStatus(), sb.toString(), authId);

        ServerErrorTypeEnum errorType = ServerErrorTypeEnum.SUCCESS;

        try {
            if (!(KanbanStatusEnum.WORKING.equals(param.getStatus()) 
                    || KanbanStatusEnum.SUSPEND.equals(param.getStatus())
                    || KanbanStatusEnum.COMPLETION.equals(param.getStatus()))) {
                return Response.serverError().entity(ResponseEntity.failed((ServerErrorTypeEnum.INVALID_ARGUMENT))).build();
            }
            
            ActualProductReportResult result = null;
            if (Objects.nonNull(param.getReports())) {
                for (ActualProductReportEntity report : param.getReports()) {
                    
                    // トランザクションIDの照合方法が異なるため
                    report.setOperateApp(OperateAppEnum.ADPRODUCTLITE);
                    
                    if (Objects.nonNull(result)) {
                        report.setTransactionId(result.getNextTransactionID());
                    }
                    
                    result = this.reportLite(report, authId);
                    if (!ServerErrorTypeEnum.SUCCESS.equals(result.getResultType())) {
                        errorType = result.getResultType();
                        logger.fatal(String.format("Faild: %s %s", errorType.toString(), report.toString()));
                        OrganizationEntity org = this.organizationRest.find(report.getOrganizationId(), authId);
                        return Response.ok().entity(ResponseEntity.failed(errorType).userData(org.getOrganizationName()).nextTransactionId(result.getNextTransactionID())).build();
                    }
                }
            }

            if (Objects.nonNull(param.getOperations())) {
                switch (param.getStatus()) {
                    case WORKING:
                        for (OperationEntity operation : param.getOperations()) {
                            Response response = this.operationRest.registerStart(operation, authId);
                            ResponseEntity res = (ResponseEntity) response.getEntity();
                            if (!res.isSuccess()) {
                                errorType = res.getErrorType();
                                logger.fatal(String.format("Faild: %s %s", errorType.toString(), operation.toString()));
                                OrganizationEntity org = this.organizationRest.find(operation.getOrganizationId(), authId);
                                return Response.ok().entity(ResponseEntity.failed(errorType).userData(org.getOrganizationName())).build();
                            }
                        }
                        break;

                    case SUSPEND:
                    case COMPLETION:
                        for (OperationEntity operation : param.getOperations()) {
                            Response response = this.operationRest.registerComp(operation, authId);
                            ResponseEntity res = (ResponseEntity) response.getEntity();
                            if (!res.isSuccess()) {
                                errorType = res.getErrorType();
                                logger.fatal(String.format("Faild: %s %s", errorType.toString(), operation.toString()));
                                OrganizationEntity org = this.organizationRest.find(operation.getOrganizationId(), authId);
                                return Response.ok().entity(ResponseEntity.failed(errorType).userData(org.getOrganizationName())).build();
                            }
                        }
                        break;

                    default:
                        break;
                }
            }

            return Response.ok().entity(ResponseEntity.success().nextTransactionId(Objects.nonNull(result) ? result.getNextTransactionID() : null)).build();
            
        } finally {
            logger.info("reportBatch end.");
        }
    }

    /**
     * 工程順名を指定して作業中の工程カンバンを含むカンバンを取得する
     * @param param 工程カンバン名
     * @return 作業中の工程カンバン数
     */
    @POST
    @Path("/withWorkingWK")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanEntity> getKanbanWithWorkingWork(ListWrapper<String> param)
    {
        try {
            List<String> workflowName = param.getList();
            logger.info("getWorkingWorkKanbanCount : {}", workflowName);
            java.sql.Array workflowNamesArray = this.em.unwrap(Connection.class).createArrayOf("text", param.getList().toArray());
            TypedQuery<KanbanEntity> query = this.em.createNamedQuery("KanbanEntity.findWithWorkingWorkByWorkflowName", KanbanEntity.class);
            query.setParameter(1, workflowNamesArray);
            return query.getResultList();
        } catch(Exception ex) {
            logger.error(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 
     * @param kanban
     * @return 
     */
    private Date estimate(KanbanEntity kanban) {
        List<Long> workKanbanIds =kanban.getWorkKanbanCollection().stream()
                .map(p -> p.getWorkKanbanId())
                .collect(Collectors.toList());
        List<Long> organizationIds = this.workKandanRest.getOrganizationCollection(workKanbanIds);

        Set<Long> breaktimeIds = organizationIds.stream()
                .map(o -> this.organizationRest.getBreaktimes(o))
                .flatMap(o -> o.stream())
                .collect(Collectors.toSet());
                    
        List<BreakTimeInfoEntity> breakTimes = new ArrayList<>();
        if (!breaktimeIds.isEmpty()) {
            List<BreaktimeEntity> list = this.breaktimeRest.find(new ArrayList(breaktimeIds));
            breakTimes = list.stream()
                    .map(o -> new BreakTimeInfoEntity(o.getBreaktimeId(), o.getBreaktimeName(), o.getStarttime(), o.getEndtime()))
                    .collect(Collectors.toList());
        }

        WorkflowEntity workflow = kanban.getWorkflow();
        if (Objects.nonNull(workflow.getOpenTime()) && Objects.nonNull(workflow.getCloseTime())) {
            LocalDate localDate = DateUtils.toLocalDate(kanban.getStartDatetime());
            LocalTime startTime = DateUtils.toLocalTime(workflow.getCloseTime());
            LocalTime endTime = DateUtils.toLocalTime(workflow.getOpenTime());
            Date startOverTime = DateUtils.toDate(localDate, startTime);
            Date endOverTime = (startTime.compareTo(endTime) > 0) 
                    ? DateUtils.toDate(localDate.plusDays(1), endTime) 
                    : DateUtils.toDate(localDate, endTime);
            breakTimes.add(new BreakTimeInfoEntity(0L, "", startOverTime, endOverTime));
        }
        
        HolidaySearchCondition holidayCondition = new HolidaySearchCondition()
                .fromDate(DateUtils.getBeginningOfDate(kanban.getStartDatetime()));
        List<HolidayEntity> holidays = this.holidayRest.searchHoliday(holidayCondition, null);

        long time = kanban.getCycleTime() * 1000L * kanban.getLotQuantity() + kanban.getStartDatetime().getTime();
        Date date = this.includeBreakTime(kanban.getStartDatetime(), new Date(time), -1, breakTimes);
        return this.includeHoliday(date, holidays);
    }

    /**
     * 休憩を含めた時間を返す。 
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
     * 休日を含めた時間を返す。
     *
     * @param targetDate 対象日時
     * @param holidays 
     * @return
     */
    private Date includeHoliday(Date targetDate, List<HolidayEntity> holidays) {
        Date date = targetDate;
        if (Objects.nonNull(holidays)) {
            for (HolidayEntity holiday : holidays) {
                Date holidayStart = DateUtils.getBeginningOfDate(holiday.getHolidayDate());
                Date holidayEnd = DateUtils.getEndOfDate(holiday.getHolidayDate());
                if ((holidayStart.before(date) || holidayStart.equals(date))
                        && (holidayEnd.after(date) || holidayEnd.equals(date))) {
                    // 休日の場合
                    LocalDateTime nextDay = DateUtils.toLocalDateTime(date).plusDays(1);
                    date = DateUtils.toDate(nextDay);
                } else if (holidayStart.after(date)) {
                    // 休日が対象日時より後の場合
                    break;
                }
            }
        }
        return date;
    }

    final String[] DSWORK_PRDUCTION = { "部品集荷", "治具集荷(組付)", "製品組付", "治具集荷(検査)", "製品検査", "梱包払出" };
    final String[] DSWORK_INSPECTION = { "検査", "製品返却", "帳票類提出", "品質記録", "梱包払出" };

    /**
     * カンバンを登録する。(デンソー高棚様向け)
     * 
     * @param condition
     * @param authId
     * @return 
     */
    @POST
    @Path("create/ds")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response createDsKanban(DsKanbanCreateCondition condition, @QueryParam("authId") Long authId) {
        logger.info("createDsKanban: condition={}, authId={}", condition, authId);
        
        final String DSLINE_PRDUCTION = "補給生産";
        final String DSLINE_INSPECTION = "検査";
        final String SERIAL = "SERIAL";
        
        try {
            if (Objects.isNull(condition.getCategory()) 
                    || Objects.isNull(condition.getProductNo()) 
                    || Objects.isNull(condition.getQuantity())
                    || Objects.isNull(condition.getQrCode())
                    || Objects.isNull(authId)) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }
            
            String hierarchyName = null;
            switch (condition.getCategory()) {
                case 1:
                    // 補給生産
                    if (StringUtils.isEmpty(condition.getPackageCode())) {
                        return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
                    }
                    hierarchyName = DSLINE_PRDUCTION;
                    break;
                case 2:
                    // 検査
                    if (StringUtils.isEmpty(condition.getSerial())) {
                        return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
                    }   
                    hierarchyName = DSLINE_INSPECTION;
                    break;
                default:
                    return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            // 組織を取得する。
            OrganizationEntity organization = this.organizationRest.find(authId);
            if (Objects.isNull(organization.getOrganizationId())) {
                logger.fatal("Organization is not found:{}", authId);
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_ORGANIZATION)).build();
            }

            // カンバン階層を取得する
            KanbanHierarchyEntity kanbanHierarchy = this.kanbanHierarchyRest.findHierarchyByName(hierarchyName, null, null);
            if (Objects.isNull(kanbanHierarchy.getKanbanHierarchyId())) {
                logger.fatal("Hierarchy is not found: {}", DSLINE_PRDUCTION);
                kanbanHierarchy = new KanbanHierarchyEntity(0L, hierarchyName);
                this.kanbanHierarchyRest.add(kanbanHierarchy, authId);
            }

            MstDsItem dsItem = this.dsItemFacade.findByProductNo(condition.getCategory(), condition.getProductNo());
            if (Objects.isNull(dsItem.getProductId())) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_ITEM)).build();
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            Date now = new Date();

            StringBuilder _kanbanName = new StringBuilder(dsItem.getProductNo());
            if (2 == condition.getCategory()) {
                // 検査の場合、シリアルを付加
                _kanbanName.append(" ");
                _kanbanName.append(condition.getSerial());
            }
            _kanbanName.append(" ");
            _kanbanName.append(sdf.format(now));
            String kanbanName = _kanbanName.toString();

            KanbanEntity kanban = null;

            if (1 == condition.getCategory()) {
                /******************************
                 * 製品組付カンバンの登録
                ******************************/
                
                if (Objects.isNull(dsItem.getWorkflow1()) || Objects.isNull(dsItem.getWorkflow2())) {
                    // 品番マスタ情報の組付工程 又は、検査工程が未登録
                    return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORKFLOW)).build();
                }

                // カンバン名の重複を確認する。(削除済も含む)
                if (existKanban(new KanbanExistEntity(null, kanbanName, null, dsItem.getWorkflow1()))) {
                    logger.info("not update overlap name:{}", kanbanName);
                    return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
                }

                // 工程順を取得する。
                WorkflowEntity workflow = this.getWorkflowLatest(dsItem.getWorkflow1());
                if (Objects.isNull(workflow.getWorkflowId())) {
                    logger.fatal("Workflow is not found:{}", workflow.getWorkflowId());
                    return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORKFLOW)).build();
                }

                // カンバンを作成する。
                kanban = new KanbanEntity(kanbanHierarchy.getKanbanHierarchyId(), kanbanName, null, workflow.getWorkflowId(), workflow.getWorkflowName(), null, null, organization.getOrganizationId(), now, KanbanStatusEnum.PLANNING, null, null);
                kanban.setKanbanSubname(condition.getQrCode());
                kanban.setWorkflow(workflow);
                kanban.setModelName(dsItem.getProductNo());
                kanban.setLotQuantity(condition.getQuantity());
                kanban.setProductionType(0);
                kanban.setDefectNum(0);

                // カンバン情報を登録する。
                super.create(kanban);
                this.em.flush();

                // 階層関連付け情報を登録する。
                this.addHierarchy(kanban);

                // 追加情報をセットする。
                kanban.setKanbanAddInfo(workflow.getWorkflowAddInfo());

                // サービス情報
                List<DsActual> actuals = new ArrayList<>();
                actuals.add(new DsActual("1"));
                actuals.add(new DsActual("2"));
                actuals.add(new DsActual("3"));
                actuals.add(new DsActual("4"));
                actuals.add(new DsActual("5"));
                actuals.add(new DsActual("6"));
                
                DsKanban dsKanban = new DsKanban(dsItem.getCategory(), dsItem.getProductNo(), dsItem.getProductName(), dsItem.getSpec(), condition.getPackageCode(), dsItem.getLocation1(), dsItem.getLocation2(), actuals);

                ServiceInfoEntity dsKanbanInfo = new ServiceInfoEntity();
                dsKanbanInfo.setService(ServiceInfoEntity.SERVICE_INFO_DSKANBAN);
                dsKanbanInfo.setJob(dsKanban);
                kanban.setServiceInfo(JsonUtils.objectToJson(Arrays.asList(dsKanbanInfo)));

                // 工程カンバン情報を登録する。
                this.addWorkKanban(kanban, workflow);
                
                // 追加工程カンバンを登録する
                int order = kanban.getSeparateworkKanbanCollection().size();
                WorkKanbanEntity workKanban = this.createDsWorkKanban(DSWORK_PRDUCTION[0], kanban.getKanbanId(), workflow.getWorkflowId(), now, ++order); // 部品集荷

                // 部品集荷の工程カンバン情報に構成部品情報を格納する
                DsPickup dsPickup = new DsPickup(dsItem.getCategory(), dsItem.getProductNo());
                List<DsParts> partsList = JsonUtils.jsonToObjects(dsItem.getBom(), DsParts[].class);
                partsList.stream().forEach(o -> o.setQuantity(o.getQuantity() * condition.getQuantity()));
                
                dsPickup.setPartsList(partsList);
                
                ServiceInfoEntity dsPickupInfo = new ServiceInfoEntity();
                dsPickupInfo.setService(ServiceInfoEntity.SERVICE_INFO_DSPICKUP);
                dsPickupInfo.setJob(dsPickup);
                workKanban.setServiceInfo(JsonUtils.objectToJson(Arrays.asList(dsPickupInfo)));

                kanban.getSeparateworkKanbanCollection().add(workKanban);
                kanban.getSeparateworkKanbanCollection().add(this.createDsWorkKanban(DSWORK_PRDUCTION[1], kanban.getKanbanId(), workflow.getWorkflowId(), now, ++order)); // 治具集荷(組付)
                kanban.getSeparateworkKanbanCollection().add(this.createDsWorkKanban(DSWORK_PRDUCTION[3], kanban.getKanbanId(), workflow.getWorkflowId(), now, ++order)); // 治具集荷(検査)
                kanban.getSeparateworkKanbanCollection().add(this.createDsWorkKanban(DSWORK_PRDUCTION[5], kanban.getKanbanId(), workflow.getWorkflowId(), now, ++order)); // 梱包払出
                
                kanban.setStartDatetime(now);
                //this.updateBaseTime(kanban, workflow, now);

                kanban.getWorkKanbanCollection()
                    .forEach(workKanbanEntity -> this.em.merge(workKanbanEntity));

                // カンバン情報を更新する。
                super.edit(kanban);

                this.updateStatus(Arrays.asList(kanban.getKanbanId()), KanbanStatusEnum.PLANNED.name(), false, false, authId);

                /******************************
                 * 製品検査カンバンの登録
                ******************************/

                // カンバン名の重複を確認する。(削除済も含む)
                if (existKanban(new KanbanExistEntity(null, kanbanName, null, dsItem.getWorkflow2()))) {
                    logger.info("not update overlap name:{}", kanbanName);
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
                }

                // 工程順を取得する。
                WorkflowEntity subWorkflow = this.getWorkflowLatest(dsItem.getWorkflow2());
                if (Objects.isNull(subWorkflow.getWorkflowId())) {
                    logger.fatal("Workflow is not found:{}", subWorkflow.getWorkflowId());
                    return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORKFLOW)).build();
                }

                // カンバンを作成する。
                KanbanEntity subKanban = new KanbanEntity(kanbanHierarchy.getKanbanHierarchyId(), kanbanName, null, subWorkflow.getWorkflowId(), subWorkflow.getWorkflowName(), null, null, organization.getOrganizationId(), now, KanbanStatusEnum.PLANNING, null, null);
                subKanban.setKanbanSubname("製品検査用");
                subKanban.setWorkflow(subWorkflow);
                subKanban.setModelName(dsItem.getProductNo());
                subKanban.setLotQuantity(condition.getQuantity());
                subKanban.setProductionType(0);
                subKanban.setDefectNum(0);

                // カンバン情報を登録する。
                super.create(subKanban);
                this.em.flush();

                // 階層関連付け情報を登録する。
                this.addHierarchy(subKanban);

                // 追加情報をセットする。
                subKanban.setKanbanAddInfo(subWorkflow.getWorkflowAddInfo());

                dsKanban.setMainKanbanId(kanban.getKanbanId());
                dsKanban.setActuals(new ArrayList<>());
                subKanban.setServiceInfo(JsonUtils.objectToJson(Arrays.asList(dsKanbanInfo)));
                   
                // 工程カンバン情報を登録する。
                this.addWorkKanban(subKanban, subWorkflow);

                // カンバンの基準時間を更新する。
                kanban.setStartDatetime(now);
                //this.updateBaseTime(subKanban, subWorkflow, now);

                subKanban.getWorkKanbanCollection()
                    .forEach(workKanbanEntity -> this.em.merge(workKanbanEntity));

                // カンバン情報を更新する。
                super.edit(subKanban);

                this.updateStatus(Arrays.asList(subKanban.getKanbanId()), KanbanStatusEnum.PLANNED.name(), false, false, authId);

            } else {
                /******************************
                 * 検査カンバンの登録
                ******************************/

                if (Objects.isNull(dsItem.getWorkflow2())) {
                    // 品番マスタ情報の組付工程 又は、検査工程が未登録
                    return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORKFLOW)).build();
                }

                // カンバン名の重複を確認する。(削除済も含む)
                if (existKanban(new KanbanExistEntity(null, kanbanName, null, dsItem.getWorkflow2()))) {
                    logger.info("not update overlap name:{}", kanbanName);
                    return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
                }

                // 工程順を取得する。
                WorkflowEntity workflow = this.getWorkflowLatest(dsItem.getWorkflow2());
                if (Objects.isNull(workflow.getWorkflowId())) {
                    logger.fatal("Workflow is not found:{}", workflow.getWorkflowId());
                    return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORKFLOW)).build();
                }

                // カンバンを作成する。
                kanban = new KanbanEntity(kanbanHierarchy.getKanbanHierarchyId(), kanbanName, null, workflow.getWorkflowId(), workflow.getWorkflowName(), null, null, organization.getOrganizationId(), now, KanbanStatusEnum.PLANNING, null, null);
                kanban.setKanbanSubname(condition.getQrCode());
                kanban.setWorkflow(workflow);
                kanban.setModelName(dsItem.getProductNo());
                kanban.setLotQuantity(1);
                kanban.setProductionType(0);
                kanban.setDefectNum(0);

                // カンバン情報を登録する。
                super.create(kanban);
                this.em.flush();

                // 階層関連付け情報を登録する。)
                this.addHierarchy(kanban);

                // サービス情報
                List<DsActual> actuals = new ArrayList<>();
                actuals.add(new DsActual("1"));
                actuals.add(new DsActual("2"));
                actuals.add(new DsActual("3"));
                actuals.add(new DsActual("4"));
                
                DsKanban dsKanban = new DsKanban(dsItem.getCategory(), dsItem.getProductNo(), dsItem.getProductName(), dsItem.getSpec(), condition.getPackageCode(), dsItem.getLocation1(), dsItem.getLocation2(), actuals);

                ServiceInfoEntity dsKanbanInfo = new ServiceInfoEntity();
                dsKanbanInfo.setService(ServiceInfoEntity.SERVICE_INFO_DSKANBAN);
                dsKanbanInfo.setJob(dsKanban);
                kanban.setServiceInfo(JsonUtils.objectToJson(Arrays.asList(dsKanbanInfo)));

                // 追加情報をセットする。
                kanban.setKanbanAddInfo(workflow.getWorkflowAddInfo());
                updateKanbanAddInfo(kanban, SERIAL, CustomPropertyTypeEnum.TYPE_STRING, condition.getSerial());

                // 工程カンバン情報を登録する。
                this.addWorkKanban(kanban, workflow);
                
                // 追加工程カンバンを登録する
                int order = kanban.getSeparateworkKanbanCollection().size();
                kanban.getSeparateworkKanbanCollection().add(this.createDsWorkKanban(DSWORK_INSPECTION[1], kanban.getKanbanId(), workflow.getWorkflowId(), now, ++order)); // 製品返却
                kanban.getSeparateworkKanbanCollection().add(this.createDsWorkKanban(DSWORK_INSPECTION[2], kanban.getKanbanId(), workflow.getWorkflowId(), now, ++order)); // 帳票類提出
                kanban.getSeparateworkKanbanCollection().add(this.createDsWorkKanban(DSWORK_INSPECTION[3], kanban.getKanbanId(), workflow.getWorkflowId(), now, ++order)); // 品質記録
                kanban.getSeparateworkKanbanCollection().add(this.createDsWorkKanban(DSWORK_INSPECTION[4], kanban.getKanbanId(), workflow.getWorkflowId(), now, ++order)); // 梱包払出

                //kanban.setStartDatetime(now);
                //this.updateBaseTime(kanban, workflow, now);

                kanban.getWorkKanbanCollection()
                    .forEach(workKanbanEntity -> this.em.merge(workKanbanEntity));

                // カンバン情報を更新する。
                super.edit(kanban);

                this.updateStatus(Arrays.asList(kanban.getKanbanId()), KanbanStatusEnum.PLANNED.name(), false, false, authId);
            }

            this.em.flush();
            this.em.clear();

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("kanban/").append(kanban.getKanbanId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        } finally {
            logger.info("createKanban end.");
        }
    }
    
    /**
     * カンバン追加情報を更新する。
     * 
     * @param kanban
     * @param propertyName
     * @param type
     * @param propertyValue 
     */
    private void updateKanbanAddInfo(KanbanEntity kanban, String propertyName, CustomPropertyTypeEnum type, String propertyValue) {
        logger.info("updateKanbanAddInfo: kanbanName={}, propertyName={} type={} propertyValue={}", kanban.getKanbanName(), propertyName, type, propertyValue);
        
        List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(kanban.getKanbanAddInfo(), AddInfoEntity[].class);

        Optional<AddInfoEntity> opt = addInfos.stream().filter(p -> p.getKey().equals(propertyName)).findFirst();
        if (opt.isPresent()) {
            // 追加情報の値を更新する。
            opt.get().setVal(propertyValue);
        } else {
            // 該当する追加情報がないため、新規に登録する
            AddInfoEntity addInfo = new AddInfoEntity(propertyName, type, propertyValue, addInfos.size() + 1, null);
            addInfos.add(addInfo);
        }

        // 追加情報一覧をJSON文字列に変換する。
        kanban.setKanbanAddInfo(JsonUtils.objectsToJson(addInfos));
    }
    
    /**
     * 実績情報を更新する。
     * 
     * @param report
     * @param kanban
     * @param workKanban
     * @param workTime
     * @param isWorkCompletion true: 工程完了、false: 工程未完了
     */
    private void updateDsKanban(ActualProductReportEntity report, KanbanEntity kanban, WorkKanbanEntity workKanban, int workTime, boolean isWorkCompletion) {
        logger.info("updateDsKanban: kanban={}", kanban);
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

            KanbanEntity mainKanban = kanban;
            int index = 0;
            
            DsKanban dsKanban = DsKanban.lookup(kanban.getServiceInfo());
            if (Objects.isNull(dsKanban)) {
                logger.fatal("DsKanban is incorrect.");
                return;
            }
            
            if (1 == dsKanban.getCategory()) {
                if (Objects.nonNull(dsKanban.getMainKanbanId())) {
                    // 子カンバン(製品検査用)の場合、親カンバンを更新
                    mainKanban = this.find(dsKanban.getMainKanbanId());
                    if (Objects.isNull(mainKanban)) {
                        logger.fatal("Not found Kanban: mainKanbanId={}", dsKanban.getMainKanbanId());
                        return;
                    }

                    dsKanban = DsKanban.lookup(mainKanban.getServiceInfo());
                    if (Objects.isNull(dsKanban) || Objects.isNull(dsKanban.getActuals())) {
                        logger.fatal("DsKanban is incorrect.");
                        return;
                    }
                }
               
                if (DSWORK_PRDUCTION[0].equals(workKanban.getWorkName())) {
                    // 部品集荷
                } else if (DSWORK_PRDUCTION[1].equals(workKanban.getWorkName())) {
                    index = 1; // 治具集荷(組付)
                } else if (DSWORK_PRDUCTION[3].equals(workKanban.getWorkName())) {
                    index = 3; // 治具集荷(検査)
                } else if (DSWORK_PRDUCTION[5].equals(workKanban.getWorkName())) {
                    index = 5; // 梱包払出
                } else {
                    index = Objects.equals(mainKanban.getKanbanId(), kanban.getKanbanId()) ?  2 : 4; // 製品組付 : 製品検査
                }

            } else {
                if (DSWORK_INSPECTION[1].equals(workKanban.getWorkName())) {
                    index = 1; // 製品返却
                } else if (DSWORK_INSPECTION[2].equals(workKanban.getWorkName())) {
                    index = 2; // 帳票類提出
                } else if (DSWORK_INSPECTION[3].equals(workKanban.getWorkName())) {
                    index = 3; // 品質記録
                } else if (DSWORK_INSPECTION[4].equals(workKanban.getWorkName())) {
                    index = 4; // 梱包払出
                }
            }
            
            DsActual actual = dsKanban.getActuals().get(index);
            actual.setPersonName(this.organizationRest.findNameById(report.getOrganizationId()));

            if (workKanban.getSeparateWorkFlag()) {
                switch (report.getStatus()) {
                    case WORKING:
                        if (Objects.isNull(actual.getStartDateTime())) {
                            actual.setStartDateTime(sdf.format(report.getReportDatetime()));
                        }
                        break;
                    case COMPLETION:
                        actual.setCompDateTime(sdf.format(report.getReportDatetime()));
                        actual.setWorkTime(workKanban.getSumTimes() / 1000);
                        break;
                    case SUSPEND:
                        actual.setWorkTime(workKanban.getSumTimes() / 1000);
                        break;
                }

            } else {

                switch (workKanban.getWorkStatus()) {
                    case WORKING:
                        if (Objects.isNull(actual.getStartDateTime())) {
                            actual.setStartDateTime(sdf.format(report.getReportDatetime()));
                        }
                        if (!KanbanStatusEnum.WORKING.equals(report.getStatus())) {
                            actual.setWorkTime(Objects.nonNull(actual.getWorkTime()) ? actual.getWorkTime() + workTime / 1000 : workTime / 1000);
                        }
                        break;

                    case COMPLETION:
                        List<WorkKanbanEntity> workKanbans = this.getWorkKanban(kanban.getKanbanId());
                       
                        Long count = workKanbans.stream()
                                .filter(o -> o.getSkipFlag() || KanbanStatusEnum.COMPLETION.equals(o.getWorkStatus()) )
                                .count();
                        if (count == kanban.getWorkKanbanCollection().size()) {
                            actual.setCompDateTime(sdf.format(report.getReportDatetime()));
                        }
                        
                        actual.setWorkTime(Objects.nonNull(actual.getWorkTime()) ? actual.getWorkTime() + workTime / 1000 : workTime / 1000);
                        break;

                    case SUSPEND:
                        //sumTimes = this.getWorkKanban(kanban.getKanbanId()).stream()
                        //        .filter(o -> KanbanStatusEnum.SUSPEND.equals(o.getWorkStatus()) || KanbanStatusEnum.COMPLETION.equals(o.getWorkStatus()) )
                        //        .mapToLong(o -> o.getSumTimes())
                        //        .sum();
                        actual.setWorkTime(Objects.nonNull(actual.getWorkTime()) ? actual.getWorkTime() + workTime / 1000 : workTime / 1000);
                        break;
                }
            }
            
            ServiceInfoEntity dsKanbanInfo = new ServiceInfoEntity();
            dsKanbanInfo.setService(ServiceInfoEntity.SERVICE_INFO_DSKANBAN);
            dsKanbanInfo.setJob(dsKanban);
            mainKanban.setServiceInfo(JsonUtils.objectToJson(Arrays.asList(dsKanbanInfo)));
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        
    }

    /**
     * 
     * @param workName
     * @param kanbanId
     * @param workflowId
     * @param now
     * @param order
     * @return
     * @throws Exception 
     */
    public WorkKanbanEntity createDsWorkKanban(String workName, Long kanbanId, Long workflowId, Date now, Integer order) throws Exception {
        Long adminId = this.organizationRest.checkExsistAdmin();

        WorkEntity work = null;
        try {
            TypedQuery<WorkEntity> query = this.em.createNamedQuery("WorkEntity.findLatestRevByName", WorkEntity.class);
            query.setParameter("workName", workName);
            work = query.getSingleResult();

        } catch (NoResultException ex) {
            // 工程マスタが存在しない
            work = new WorkEntity();
            work.setWorkName(workName);
            work.setWorkRev(1);
            work.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
            work.setUpdatePersonId(adminId);
            work.setUpdateDatetime(now);
            
            this.em.persist(work);
            this.em.flush();
        }

        // 工程カンバン情報を作成する。
        WorkKanbanEntity workKanban = new WorkKanbanEntity(kanbanId, workflowId, work.getWorkId(), workName,
                true, true, false, now, now, 0, 0L, adminId, now, KanbanStatusEnum.PLANNED, null, null, order);
        
        this.em.persist(workKanban);
        this.em.flush();
        
        return workKanban;
    }
    
    /**
     * 作業コメントを追加する。
     * 
     * @param comment 作業コメント
     * @param kanbanId カンバンID
     * @param authId 認証ID
     * @return 
     */
    @POST
    @Path("comment/{kanbanId}")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response addWorkComment(WorkComment comment, @PathParam("kanbanId") Long kanbanId, @QueryParam("authId") Long authId) throws Exception {
        logger.info("addWorkComment: kanbanId={}, comment={}, authId={}", kanbanId, comment, authId);

        if (Objects.isNull(kanbanId) || Objects.isNull(authId)) {
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        KanbanEntity kanban = super.find(kanbanId);
        if (Objects .isNull(kanban)) {
            // カンバンが見つからない
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_KANBAN)).build();
        }
        
        WorkCommentContainer container = WorkCommentContainer.lookup(kanban.getServiceInfo());
        if (Objects.isNull(container)) {
            container = new WorkCommentContainer();
        }
        
        container.add(comment);
        
        if (WorkComment.Type.Image.equals(comment.getType())) {
            FileManager.getInstance().createDirectory(FileManager.Data.COMMENTS, kanbanId.toString());
        }
        
        // サービス情報を更新
        boolean updated = false;
        List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(kanban.getServiceInfo(), ServiceInfoEntity[].class);
        for (ServiceInfoEntity serviceInfo : serviceInfos) {
            if (StringUtils.equals(serviceInfo.getService(), ServiceInfoEntity.SERVICE_INFO_COMMENTS)) {
                serviceInfo.setJob(container);
                kanban.setServiceInfo(JsonUtils.objectsToJson(serviceInfos));
                updated = true;
                break;
            }
        }
        
        if (!updated) {
            ServiceInfoEntity serviceInfo = new ServiceInfoEntity();
            serviceInfo.setService(ServiceInfoEntity.SERVICE_INFO_COMMENTS);
            serviceInfo.setJob(container);
            serviceInfos.add(serviceInfo);
            kanban.setServiceInfo(JsonUtils.objectToJson(serviceInfos));            
        }
        
        kanban.setUpdateDatetime(new Date());
        kanban.setUpdatePersonId(authId);
        
        this.em.merge(kanban);
        
        // コメントIDを返す
        URI uri = new URI(comment.getId().toString());
        return Response.ok().entity(ResponseEntity.success().uri(uri)).build();
    }

    /**
     * 作業コメントを削除する。
     * 
     * @param kanbanId カンバンID
     * @param commentId コメントID
     * @param authId 認証ID
     * @return 
     */
    @DELETE
    @Path("comment/{kanbanId}/{comentId}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response removeWorkComment(@PathParam("kanbanId") Long kanbanId, @PathParam("comentId") Long commentId, @QueryParam("authId") Long authId) throws Exception {
         logger.info("removeWorkComment: kanbanId={}, commentId={}, authId={}", kanbanId, commentId, authId);

        if (Objects.isNull(kanbanId) || Objects.isNull(authId)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        KanbanEntity kanban = super.find(kanbanId);
        if (Objects .isNull(kanban)) {
            // カンバンが見つからない
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_KANBAN)).build();
        }
        
        WorkCommentContainer container = WorkCommentContainer.lookup(kanban.getServiceInfo());
        if (Objects.isNull(container)) {
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        WorkComment comment = container.get(commentId);
        if (Objects.isNull(comment)) {
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_ITEM)).build();
        }
        
        Long adminId = this.organizationRest.checkExsistAdmin();
        
        if (!Objects.equals(adminId, authId) && !Objects.equals(comment.getOrgId(), authId)) {
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMITTED_EDIT_RESOURCE)).build();
        }

        // コメントの削除
        if (container.remove(commentId)) {
            if (WorkComment.Type.Image.equals(comment.getType())) {
                // ファイルの削除
                String p[] = comment.getData().split("/");
                String filePath = new StringBuilder(p[p.length - 2])
                        .append(File.separator)
                        .append(p[p.length - 1])
                        .toString();
                FileManager.getInstance().remove(FileManager.Data.COMMENTS, filePath);
            }
        }
        
        // サービス情報を更新
        boolean updated = false;
        List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(kanban.getServiceInfo(), ServiceInfoEntity[].class);
        for (ServiceInfoEntity serviceInfo : serviceInfos) {
            if (StringUtils.equals(serviceInfo.getService(), ServiceInfoEntity.SERVICE_INFO_COMMENTS)) {
                serviceInfo.setJob(container);
                kanban.setServiceInfo(JsonUtils.objectsToJson(serviceInfos));
                updated = true;
                break;
            }
        }
        
        if (!updated) {
            ServiceInfoEntity serviceInfo = new ServiceInfoEntity();
            serviceInfo.setService(ServiceInfoEntity.SERVICE_INFO_COMMENTS);
            serviceInfo.setJob(container);
            serviceInfos.add(serviceInfo);
            kanban.setServiceInfo(JsonUtils.objectToJson(serviceInfos));            
        }
        
        this.em.merge(kanban);
        
        return Response.ok().entity(ResponseEntity.success()).build();
    }
}
