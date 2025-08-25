/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.OrganizationInfoFacade;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 組織Facade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.18.Tue
 */
@Singleton
@Path("organization")
public class OrganizationEntityFacade {

    private final Logger logger = LogManager.getLogger();

    private final OrganizationInfoFacade organizationFacade = new OrganizationInfoFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress());

    public OrganizationEntityFacade() {
    }

    /**
     * IDで指定された階層の組織個数取得
     *
     * @param parentId 組織ID
     * @return parentId->IsNull:最上位階層数取得/NonNull:指定された階層数取得
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("tree/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public Long findTreeCount(@QueryParam("id") Long parentId) throws Exception {
        return this.organizationFacade.getAffilationHierarchyCount(parentId);
    }

    /**
     * 階層に含まれる組織階層を範囲指定で取得
     *
     * @param parentId 親階層ID
     * @param from 頭数
     * @param to 尾数
     * @return 最上位階層の組織階層一覧
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("tree/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<OrganizationInfoEntity> findTreeRange(@QueryParam("id") Long parentId, @QueryParam("from") Long from, @QueryParam("to") Long to) throws Exception {
        return this.organizationFacade.getAffilationHierarchyRange(parentId, from, to);
    }

    /**
     * 指定されたIDの組織を取得
     *
     * @param id 組織ID
     * @return レスポンス
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @Path("{id}")
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public OrganizationInfoEntity find(@PathParam("id") Long id) throws Exception {
        return this.organizationFacade.find(id);
    }

    /**
     * 指定した組織名の組織を取得
     *
     * @param name 組織名
     * @return 組織
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("/name")
    @Produces({"application/xml", "application/json"})
    public OrganizationInfoEntity findName(@QueryParam("name") String name) throws Exception {
        return this.organizationFacade.findName(name);
    }

    public List<Long> findAncesors(Long userId) {
        logger.debug("find user:{}", userId);
        List<Long> resultList = new ArrayList<>();
        try {
            String result = this.organizationFacade.findAncestorsString(userId);
            String[] resultArray = result.split(",");
            for(int i=0; i<resultArray.length; i++) {
                resultList.add(Long.parseLong(resultArray[i]));
            }
            return resultList;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return resultList;
        }
    }

    /**
     * 全組織数取得
     *
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public Long count() {
        return this.organizationFacade.count();
    }

    /**
     * 指定された範囲の組織情報を取得
     *
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲の組織一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<OrganizationInfoEntity> findRange(@QueryParam("from") Long from, @QueryParam("to") Long to) {
        return this.organizationFacade.findRange(from, to);
    }
}
