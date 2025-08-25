/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jp.adtekfuji.adfactoryforfujiserver.entity.accessfuji.AccessHierarchyFujiEntity;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.forfujiapp.entity.accessfuji.AccessHierarchyFujiTypeEnum;

/**
 *
 * @author j.min
 */
@Singleton
@Path("accessfuji")
public class AccessHierarchyFujiEntityFacadeREST extends AbstractFacade<AccessHierarchyFujiEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryForFujiServer_war_1.0PU")
    private EntityManager em;
    private final Logger logger = LogManager.getLogger();

    public AccessHierarchyFujiEntityFacadeREST() {
        super(AccessHierarchyFujiEntity.class);
    }

    @Lock(LockType.READ)
    @GET
    @Path("tree")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<AccessHierarchyFujiEntity> find(@QueryParam("type") AccessHierarchyFujiTypeEnum type, @QueryParam("id") Long id) {
        logger.info("find:{},{}", type, id);
        return this.getRange(type, id, null, null);
    }
  
    @Lock(LockType.READ)
    @GET
    @Path("tree/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public Long getHierarchyCount(@QueryParam("type") AccessHierarchyFujiTypeEnum type, @QueryParam("id") Long id) {
        logger.info("getHierarchyCount:{},{}", type, id);
        TypedQuery<Long> query = em.createNamedQuery("AccessHierarchyFujiEntity.getCount", Long.class);
        query.setParameter("type", type);
        query.setParameter("id", id);
        return query.getSingleResult();
    }
    
    @Lock(LockType.READ)
    @GET
    @Path("tree/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<AccessHierarchyFujiEntity> getHierarchyRange(@QueryParam("type") AccessHierarchyFujiTypeEnum type, @QueryParam("id") Long id, @QueryParam("from") Integer from, @QueryParam("to") Integer to) {
        logger.info("getHierarchyRange:{},{},{}", type, id, from, to);
        return this.getRange(type, id, from, to);
    }

    /**
     *
     * @param entity
     * @return
     * @throws URISyntaxException
     */
    @POST
    @Path("tree")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(AccessHierarchyFujiEntity entity) throws URISyntaxException {
        try {
            logger.info("add:{}", entity);
            //重複確認.
            TypedQuery<Long> query = em.createNamedQuery("AccessHierarchyFujiEntity.check", Long.class);
            query.setParameter("type", entity.getTypeId());
            query.setParameter("id", entity.getFkHierarchyId());
            query.setParameter("data", entity.getFkOrganizationId());
            if (query.getSingleResult() > 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }
            //保存
            em.persist(entity);
            URI uri = new URI("access/tree/");
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }
    
    @DELETE
    @Path("tree")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@QueryParam("type") AccessHierarchyFujiTypeEnum type, @QueryParam("id") Long id, @QueryParam("data") List<Long> datas) {
        try {
            logger.info("remove:{},{},{}", type, id, datas);
            //削除
            datas.stream().forEach((data) -> {
                super.remove(new AccessHierarchyFujiEntity(type, id, data));
            });
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }
    
    @Lock(LockType.READ)
    private List<AccessHierarchyFujiEntity> getRange(AccessHierarchyFujiTypeEnum type, Long id, Integer from, Integer to) {
        TypedQuery<AccessHierarchyFujiEntity> query = em.createNamedQuery("AccessHierarchyFujiEntity.find", AccessHierarchyFujiEntity.class);
        query.setParameter("type", type);
        query.setParameter("id", id);
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }
        return query.getResultList();
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
