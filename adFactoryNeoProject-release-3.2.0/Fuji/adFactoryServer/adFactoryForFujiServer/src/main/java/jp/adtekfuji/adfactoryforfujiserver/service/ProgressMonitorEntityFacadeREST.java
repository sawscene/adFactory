/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

import adtekfuji.utility.StringUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitActualResultEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitPropertyEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitWorkKanbanEntity;
import jp.adtekfuji.adfactoryforfujiserver.service.standard.ActualResultEntityFacade;
import jp.adtekfuji.adfactoryforfujiserver.service.standard.AgendaEntityFacade;
import jp.adtekfuji.adfactoryforfujiserver.service.standard.WorkKanbanEntityFacade;
import jp.adtekfuji.adfactoryforfujiserver.utility.DateUtils;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorGraphData;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorGraphInfoEntity;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorListInfoEntity;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorPanelInfoEntity;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 進捗モニタ表示情報取得用REST
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.20.Thr
 */
@Singleton
@Path("monitor")
public class ProgressMonitorEntityFacadeREST {

    @EJB
    private UnitEntityFacadeREST unitEntityFacadeREST;
    @EJB
    private WorkKanbanEntityFacade workKanbanEntityFacade;
    @EJB
    private ActualResultEntityFacade actualResultEntityFacade;
    @EJB
    private AgendaEntityFacade agendaEntityFacade;

    private final Logger logger = LogManager.getLogger();
    private final static long RANGE = 20;
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ProgressMonitorEntityFacadeREST() {
    }

    /**
     * 生産ユニットの生産情報(パネル)を取得する
     *
     * @param condition
     * @param option タイトルの表示設定:nonNull:指定した生産ユニットIDに紐づいているプロパティを代わりにタイトルとして表示/
     * isNull:指定した生産ユニットIDの名前をタイトルに表示
     * @return
     */
    @Lock(LockType.READ)
    @PUT
    @Path("panel")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<MonitorPanelInfoEntity> getMonitorPanel(UnitSearchCondition condition, @QueryParam("title_option") String option) {
        logger.info("getMonitorPanelEntity: {},{}", condition, option);

        List<MonitorPanelInfoEntity> entities = new ArrayList<>();
        List<UnitEntity> units = null;

        if (Objects.nonNull(condition.getUnitId())) {
            units = new ArrayList<>();
            UnitEntity unit = this.unitEntityFacadeREST.find(condition.getUnitId());
            List<Long> kanbanIds = this.unitEntityFacadeREST.getKanbanIds(unit.getUnitId());
            unit.setKanbanIds(kanbanIds);
            units.add(unit);
        } else {
            units = unitEntityFacadeREST.findSearchRange(condition, null, null);
        }

        for (UnitEntity unit : units) {
            entities.add(this.getMonitorPanel(unit, option));
        }

        return entities;
    }

    /**
     * 生産ユニットの生産情報(パネル)を取得する
     *
     * @param unit 生産ユニット情報
     * @return
     */
    private MonitorPanelInfoEntity getMonitorPanel(UnitEntity unit, String option) {
        logger.info("createMonitorPanelInfo start.");

        MonitorPanelInfoEntity info = new MonitorPanelInfoEntity();
        StringBuilder workNames = new StringBuilder();
        StringBuilder workerNames = new StringBuilder();
        long progressTime = 0L;
        Date date = new Date(0L);
        UnitWorkKanbanEntity lastWorkKanban = null;

        //List<UnitPropertyEntity> properties = this.unitEntityFacadeREST.getProperty(unit.getUnitId());
        //Map<String, UnitPropertyEntity> map = properties.stream().collect(
        //    Collectors.toMap(UnitPropertyEntity::getUnitPropertyName, d -> d, (d1, d2) -> d1)
        //);

        // カンバンID
        List<Long> kanbanIds = unit.getKanbanIds();

        // 工程カンバン
        List<UnitWorkKanbanEntity> workKanbans = PostgreAPI.getWorkKanbans(this.unitEntityFacadeREST.getEntityManager(), kanbanIds);

        // 実績
        List<UnitActualResultEntity> actuals = PostgreAPI.getActualResults(this.unitEntityFacadeREST.getEntityManager(), kanbanIds);

        for (UnitWorkKanbanEntity workKanban : workKanbans) {

            long workKanbanId = workKanban.getWorkKanbanId();
            List<UnitActualResultEntity> tActuals = actuals.stream().filter(o -> o.getFkWorkKanbanId() == workKanbanId).collect(Collectors.toList());

            if (workKanban.getWorkStatus() == KanbanStatusEnum.WORKING) {

                if (workNames.indexOf(workKanban.getWorkName()) < 0) {
                    workNames.append(workKanban.getWorkName()).append(",");
                }

                for (UnitActualResultEntity actual : tActuals) {
                    if (workerNames.indexOf(actual.getOrganizationName()) < 0) {
                        workerNames.append(actual.getOrganizationName()).append(",");
                    }
                }
            }

            switch (workKanban.getWorkStatus()) {
                case WORKING:
                    if (date.before(workKanban.getActualStartTime())) {
                        lastWorkKanban = workKanban;
                    }
                    break;
                case COMPLETION:
                    if (date.before(workKanban.getActualCompTime())) {
                        lastWorkKanban = workKanban;
                    }
                    break;
                default:
                    break;
            }
        }

        if (Objects.nonNull(lastWorkKanban)) {
            if (lastWorkKanban.getWorkStatus() == KanbanStatusEnum.WORKING) {
                progressTime = DateUtils.differenceOfDateTimeMillsec(lastWorkKanban.getStartDatetime(), lastWorkKanban.getActualStartTime());
            } else {
                progressTime = DateUtils.differenceOfDateTimeMillsec(lastWorkKanban.getCompDatetime(), lastWorkKanban.getActualCompTime());
            }
        }

        // タイトル
        if (!StringUtils.isEmpty(option)) {
            info.setTitle(this.getOptionalTitle(unit, option) + "(" + unit.getUnitTemplateName() + ")");
        } else {
            info.setTitle(unit.getUnitName());
        }

        info.setUnitId(unit.getUnitId());
        info.setStartDate(unit.getStartDatetime());
        info.setEndDate(unit.getCompDatetime());
        info.setWorkName(workNames.toString());
        info.setProgressTimeMillisec(progressTime);
        info.setWorkerName(workerNames.toString());
        info.setKanbanIds(kanbanIds);
        info.setBackgroundColor(progressTime < 0 ? "#FF0000" : "#0000FF");

        return info;
    }

    /**
     * 生産ユニットの生産情報(グラフ)を取得する
     *
     * @param condition
     * @param option タイトルの表示設定:nonNull:指定した生産ユニットIDに紐づいているプロパティを代わりにタイトルとして表示/
     * isNull:指定した生産ユニットIDの名前をタイトルに表示
     * @return
     */
    @Lock(LockType.READ)
    @PUT
    @Path("graph")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<MonitorGraphInfoEntity> getMonitorGraph(UnitSearchCondition condition, @QueryParam("title_option") String option) {
        logger.info("getMonitorGraphEntity: {}", condition);

        List<MonitorGraphInfoEntity> entities = new ArrayList<>();
        List<UnitEntity> units = null;

        if (Objects.nonNull(condition.getUnitId())) {
            units = new ArrayList<>();
            UnitEntity unit = this.unitEntityFacadeREST.find(condition.getUnitId());
            List<Long> kanbanIds = this.unitEntityFacadeREST.getKanbanIds(unit.getUnitId());
            unit.setKanbanIds(kanbanIds);
            units.add(unit);
        } else {
            units = unitEntityFacadeREST.findSearchRange(condition, null, null);
        }

        for (UnitEntity unit : units) {
            MonitorGraphInfoEntity info = new MonitorGraphInfoEntity();
            List<MonitorGraphData> graphData = new ArrayList<>();

            // カンバンID
            List<Long> kanbanIds = unit.getKanbanIds();

            // 工程カンバン
            List<UnitWorkKanbanEntity> workKanbans = PostgreAPI.getWorkKanbansInWork(this.unitEntityFacadeREST.getEntityManager(), kanbanIds);

            long ii = 0;
            for (UnitWorkKanbanEntity workKanban : workKanbans) {
                ii++;

                MonitorGraphData data = new MonitorGraphData(ii,
                        workKanban.getWorkName(),
                        workKanban.getTaktTime().longValue(),
                        workKanban.getSumTimes().longValue(),
                        DateUtils.differenceOfDateTimeMillsec(workKanban.getStartDatetime(), workKanban.getActualStartTime()),
                        workKanban.getStartDatetime(),
                        workKanban.getActualCompTime(),
                        workKanban.getActualStartTime(),
                        workKanban.getActualCompTime());

                graphData.add(data);
            }

            if (Objects.nonNull(option)) {
                info.setTitle(this.getOptionalTitle(unit, option));
            } else {
                info.setTitle(unit.getUnitName());
            }

            info.setUnitId(unit.getUnitId());
            info.setGraphData(graphData);
            info.setKanbanIds(kanbanIds);

            entities.add(info);
        }

        return entities;
    }

    /**
     * 生産ユニットの生産情報(リスト)を取得する
     *
     * @param condition
     * @param mainTitle
     * @param subTitle
     * @return
     * @throws Exception
     */
    @Lock(LockType.READ)
    @PUT
    @Path("list")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<MonitorListInfoEntity> getMonitorList(UnitSearchCondition condition, @QueryParam("mainTitle") String mainTitle, @QueryParam("subTitle") String subTitle) throws Exception {
        logger.info("getMonitorListEntity: {},{},{}", condition, mainTitle, subTitle);

        List<MonitorListInfoEntity> entities = new ArrayList<>();
        List<UnitEntity> units = null;

        if (Objects.nonNull(condition.getUnitId())) {
            units = new ArrayList<>();
            UnitEntity unit = this.unitEntityFacadeREST.find(condition.getUnitId());
            List<Long> kanbanIds = this.unitEntityFacadeREST.getKanbanIds(unit.getUnitId());
            unit.setKanbanIds(kanbanIds);
            units.add(unit);
        } else {
            units = unitEntityFacadeREST.findSearchRange(condition, null, null);
        }

        for (UnitEntity unit : units) {
            List<UnitPropertyEntity> properties = this.unitEntityFacadeREST.getProperty(unit.getUnitId());
            Map<String, UnitPropertyEntity> map = properties.stream().collect(
                Collectors.toMap(UnitPropertyEntity::getUnitPropertyName, d -> d, (d1, d2) -> d1)
            );

            double progressRate = 0D;
            long progressTime = 0L;

            // カンバンID
            List<Long> kanbanIds = unit.getKanbanIds();

            // 生産ユニットの状態
            KanbanStatusEnum status = this.unitEntityFacadeREST.getUnitStatus(kanbanIds);
            switch (status) {
                case WORKING:
                    // 進捗率
                    progressRate = PostgreAPI.getProgressRate(this.unitEntityFacadeREST.getEntityManager(), kanbanIds);
                    // 遅れ時間
                    progressTime = this.unitEntityFacadeREST.getProgressTime(kanbanIds);
                    break;
                case SUSPEND:
                    // 進捗率
                    progressRate = PostgreAPI.getProgressRate(this.unitEntityFacadeREST.getEntityManager(), kanbanIds);
                    break;
                case COMPLETION:
                    progressRate = 100D;
                    break;
            }

            MonitorListInfoEntity entity = new MonitorListInfoEntity(unit.getUnitId(),
                map.containsKey(mainTitle) ? map.get(mainTitle).getUnitPropertyValue() : "", // タイトル
                map.containsKey(subTitle) ? map.get(mainTitle).getUnitPropertyValue() : "", // サブタイトル
                unit.getStartDatetime(), unit.getCompDatetime(), unit.getUnitTemplateName(), status, progressRate, progressTime);

            entity.setKanbanIds(kanbanIds);
            entities.add(entity);
        }

        return entities;
    }

    /**
     * 任意のタイトル情報を取得
     *
     * @param unit 生産ユニット
     * @param option 任意のタイトル項目
     * @return タイトル
     */
    private String getOptionalTitle(UnitEntity unit, String option) {
        if (Objects.isNull(unit.getUnitPropertyCollection())) {
            return unit.getUnitName();
        }
        for (UnitPropertyEntity property : unit.getUnitPropertyCollection()) {
            if (property.getUnitPropertyName().equals(option)) {
                if (Objects.nonNull(property.getUnitPropertyValue())) {
                    return property.getUnitPropertyValue();
                }
            }
        }
        return unit.getUnitName();
    }

    public void setUnitEntityFacadeREST(UnitEntityFacadeREST unitEntityFacadeREST) {
        this.unitEntityFacadeREST = unitEntityFacadeREST;
    }
}
