/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.WorkflowInfoFacade;
import java.util.List;
import java.util.Objects;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程順Facade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 */
@Singleton
@Path("workflow")
public class WorkflowEntityFacade {

    private final Logger logger = LogManager.getLogger();

    private final WorkflowInfoFacade workflowFacade = new WorkflowInfoFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress());

    public WorkflowEntityFacade() {
    }

    /**
     * 指定されたIDの工程順を取得
     *
     * @param id 工程順ID
     * @return 工程順
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @ExecutionTimeLogging
    @Produces({"application/xml", "application/json"})
    public WorkflowInfoEntity find(@PathParam("id") Long id) {
        return this.workflowFacade.find(id);
    }

    /**
     * 指定された名前の工程順を取得
     *
     * @param name 工程順名
     * @return 工程順
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public WorkflowInfoEntity findName(@QueryParam("name") String name) {
        return this.workflowFacade.findName(name);
    }

    /**
     * 登録されている工程順の数を取得
     *
     * @return 工程順の数
     */
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public Long getWorkflowCount() {
        return this.workflowFacade.getWorkflowCount();
    }

    /**
     * 登録されている工程順を範囲指定で取得
     *
     * @param from 頭数
     * @param to 尾数
     * @return 工程順一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkflowInfoEntity> findRange(@QueryParam("from") Long from, @QueryParam("to") Long to) {
        return this.workflowFacade.getWorkflowRange(from, to);
    }

    /**
     * 指定したID工程順のタクトタイム(作業時間)を取得する
     *
     * @param id 工程順ID
     * @return タクトタイム
     */
    @GET
    @Path("tacttime/{id}")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public Long getWorkflowTactTime(@PathParam("id") Long id) {
        logger.info("getWorkflowTactTime");
        try {
            WorkflowInfoEntity entity = find(id);
            Long taktTime = 0l;
            if (Objects.nonNull(entity.getConWorkflowWorkInfoCollection()) && !entity.getConWorkflowWorkInfoCollection().isEmpty()) {
                if (entity.getConWorkflowWorkInfoCollection().size() == 1) {
                    taktTime = entity.getConWorkflowWorkInfoCollection().get(0).getTaktTime();
                } else if (entity.getConWorkflowWorkInfoCollection().size() > 1) {
                    Long min = entity.getConWorkflowWorkInfoCollection().get(0).getStandardStartTime().getTime();
                    Long max = entity.getConWorkflowWorkInfoCollection().get(0).getStandardEndTime().getTime();
                    for (ConWorkflowWorkInfoEntity con : entity.getConWorkflowWorkInfoCollection()) {
                        if(min > con.getStandardStartTime().getTime()){
                            min = con.getStandardStartTime().getTime();
                        }
                        if(max < con.getStandardEndTime().getTime()){
                            max = con.getStandardEndTime().getTime();
                        }
                    }
                    taktTime = max - min;
                }
            }

            return taktTime;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return 0l;
    }
}
