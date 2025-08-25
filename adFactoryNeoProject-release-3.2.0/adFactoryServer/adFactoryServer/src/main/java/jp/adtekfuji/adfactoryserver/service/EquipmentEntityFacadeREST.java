/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Triplet;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jp.adtekfuji.adFactory.adinterface.command.CallingNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.DeviceConnectionServiceCommand;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentCallRequest;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentImportEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginRequest;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.entity.operation.OperateAppEnum;
import jp.adtekfuji.adFactory.entity.operation.OperationTypeEnum;
import jp.adtekfuji.adFactory.entity.search.EquipmentSearchCondition;
import jp.adtekfuji.adFactory.entity.system.SystemPropEntity;
import jp.adtekfuji.adFactory.enumerate.*;
import jp.adtekfuji.adfactoryserver.entity.access.Hierarchy;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity_;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentTypeEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentTypeEntity_;
import jp.adtekfuji.adfactoryserver.entity.operation.*;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.resource.LocaleFileEntity;
import jp.adtekfuji.adfactoryserver.model.EquipmentRuntimeData;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.model.LineManager;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.adfactoryserver.utility.QueryUtils;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * 設備情報REST
 *
 * @author ke.yokoi
 */
@Singleton
@Path("equipment")
public class EquipmentEntityFacadeREST extends AbstractFacade<EquipmentEntity> {
    
    private static final List<EquipmentTypeEnum> TERMINAL_TYPES = Arrays.asList(EquipmentTypeEnum.TERMINAL, EquipmentTypeEnum.LITE, EquipmentTypeEnum.REPORTER);

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private AdIntefaceClientFacade adInterfaceFacade;

    @EJB
    private AndonLineMonitorFacadeREST lineMonitorFacade;

    @EJB
    private SystemResource systemResource;

    @EJB
    private OrganizationEntityFacadeREST organizationFacade;

    @EJB
    private AccessHierarchyEntityFacadeREST authRest;

    @EJB
    private EquipmentTypeEntityFacadeREST equipmentTypeFacade;

    @EJB
    private AccessHierarchyEntityFacadeREST accessHierarchyFacade;

    @EJB
    private ResourceEntityFacadeREST resourceRest;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public EquipmentEntityFacadeREST() {
        super(EquipmentEntity.class);
    }

    /**
     * 設備IDを指定して、ルートまでの親設備・指定した設備・最下層までの子設備の設備ID一覧を取得する。
     *
     * @param equipmentId 設備ID
     * @return 設備ID一覧
     */
    @Lock(LockType.READ)
    public List<Long> getEquipmentPerpetuity(Long equipmentId) {
        List<Long> equipmentIds = new ArrayList<>();
        equipmentIds.add(equipmentId);
        equipmentIds.addAll(this.getEquipmentParentPerpetuity(equipmentId));
        equipmentIds.addAll(this.getEquipmentChildPerpetuity(equipmentId));
        return equipmentIds;
    }

    /**
     * 設備IDを指定して、指定した設備・最下層までの子設備の設備ID一覧を取得する。
     *
     * @param equipmentId 設備ID
     * @return 設備ID一覧
     */
    @Lock(LockType.READ)
    public List<Long> getEquipmentChildren(Long equipmentId) {
        List<Long> equipmentIds = new ArrayList<>();
        equipmentIds.add(equipmentId);
        equipmentIds.addAll(this.getEquipmentChildPerpetuity(equipmentId));
        return equipmentIds;
    }

    /**
     * 設備IDを指定して、ルートまでの親設備の設備ID一覧を取得する。(指定した設備は含まない)
     *
     * @param equipmentId 設備ID
     * @return 設備ID一覧
     */
    @Lock(LockType.READ)
    private List<Long> getEquipmentParentPerpetuity(Long equipmentId) {
        List<Long> equipmentIds = new ArrayList<>();

        // 設備IDを指定して、親設備IDを取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("EquipmentEntity.findParentId", Long.class);
        query.setParameter("equipmentId", equipmentId);
        Long id;
        try {
            id = query.getSingleResult();
        } catch (NoResultException ex) {
            id = null;
        }

        if (Objects.nonNull(id) && id != 0) {
            equipmentIds.add(id);
            equipmentIds.addAll(this.getEquipmentParentPerpetuity(id));
        }

        return equipmentIds;
    }

    /**
     * 設備IDを指定して、最下層までの子設備の設備ID一覧を取得する。(指定した設備は含まない)
     *
     * @param equipmentId 設備ID
     * @return 設備ID一覧
     */
    @Lock(LockType.READ)
    private List<Long> getEquipmentChildPerpetuity(Long equipmentId) {
        List<Long> equipmentIds = new ArrayList<>();

        // 設備IDを指定して、子設備ID一覧を取得する。
        List<Long> ids = this.findChildIds(equipmentId);

        for (Long id : ids) {
            equipmentIds.add(id);
            equipmentIds.addAll(this.getEquipmentChildPerpetuity(id));
        }

        return equipmentIds;
    }

    /**
     * 指定した設備の子設備情報一覧を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な設備情報を対象とする。
     *
     * @param id             設備ID
     * @param userId         ユーザーID (組織ID)
     * @param from           範囲の先頭
     * @param to             範囲の末尾
     * @param isLicenseCount ライセンス数を取得するか
     * @param authId         認証ID
     * @return 子設備情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("tree/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<EquipmentEntity> findTreeRange(@QueryParam("id") Long id, @QueryParam("user") Long userId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("isLicenseCount") Boolean isLicenseCount, @QueryParam("authId") Long authId) {
        logger.info("findTreeRange: id={}, userId={}, from={}, to={}, authId={}", id, userId, from, to, authId);

        // 設備IDの指定がない場合はルートとする。
        if (Objects.isNull(id)) {
            id = 0L;
        }
        
        List<EquipmentEntity> entities = this.findChild(id, userId, from, to);
        
        if (Objects.isNull(isLicenseCount) || !isLicenseCount) {
            for (EquipmentEntity entity : entities) {
                // 子設備の数を取得
                entity.setChildCount(this.countChild(entity.getEquipmentId(), userId));
            }
            return entities;
        }
        
        Long terminalTypeId = this.getEquipmentType(EquipmentTypeEnum.TERMINAL).getEquipmentTypeId();
        Long liteTypeId = this.getEquipmentType(EquipmentTypeEnum.LITE).getEquipmentTypeId();
        Long reporterTypeId = this.getEquipmentType(EquipmentTypeEnum.REPORTER).getEquipmentTypeId();

        for (EquipmentEntity entity : entities) {
            // 子設備の数を取得
            entity.setChildCount(this.countChild(entity.getEquipmentId(), userId));
            
            // ライセンス数を取得
            Triplet<Long, Long, Long> res = countUsedLicenses(entity, terminalTypeId, liteTypeId, reporterTypeId);
            entity.setLicenseCount(res.first);
            entity.setLiteCount(res.second);
            entity.setReporterCount(res.third);
        }

        return entities;
    }

    /**
     * 指定した設備の子設備情報の件数を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な設備情報を対象とする。
     *
     * @param id     設備ID
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return 子設備の件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("tree/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countTree(@QueryParam("id") Long id, @QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        logger.info("countTree: id={}, userId={}, authId={}", id, userId, authId);

        // 設備IDの指定がない場合はルートとする。
        if (Objects.isNull(id)) {
            id = 0L;
        }

        // 指定した設備の子設備の件数を取得する。
        Long count = this.countChild(id, userId);
        return String.valueOf(count);
    }

    /**
     * 指定した設備の子設備の件数を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な設備情報を対象とする。
     *
     * @param equipmentId 設備ID
     * @param userId      ユーザーID (組織ID)
     * @return 子設備の件数
     */
    @Lock(LockType.READ)
    private long countChild(Long equipmentId, Long userId) {
        TypedQuery<Long> query;

        if (Objects.isNull(userId)) {
            // 指定した設備IDの子設備情報の件数を取得する。
            query = this.em.createNamedQuery("EquipmentEntity.countChild", Long.class);
        } else {
            // ユーザーIDが指定されている場合、アクセス可能な設備情報を対象とする。

            // ユーザーのルートまでの親組織ID一覧を取得する。
            List<Long> organizationIds = this.organizationFacade.findAncestors(userId);

            // 指定した設備IDの子設備情報の件数を取得する。(指定ユーザーがアクセス可能な設備のみ)
            query = this.em.createNamedQuery("EquipmentEntity.countChildByUserId", Long.class);
            query.setParameter("ancestors", organizationIds);
        }
        query.setParameter("equipmentId", equipmentId);

        return query.getSingleResult();
    }

    /**
     * 設備の子孫に指定した設備種別IDの設備が幾つ存在するかカウントする。
     *
     * @param entity          対象設備
     * @param equipmentTypeId 設備種別ID
     * @return 指定した設備種別ID
     */
    @Lock(LockType.READ)
    private long countOffspringByEquipmentTypeId(EquipmentEntity entity, Long equipmentTypeId) {

        long count = 0;

        if (Objects.nonNull(entity) && Objects.equals(entity.getEquipmentTypeId(), equipmentTypeId)) {
            count++;
        }

        List<EquipmentEntity> children = this.findChild(entity.getEquipmentId(), null, null, null);
        if (children == null) {
            return count;
        }

        for (EquipmentEntity child : children) {
            // 末端まで再帰的に判定
            count += countOffspringByEquipmentTypeId(child, equipmentTypeId);
        }
        return count;
    }
    
    /**
     * 指定した階層の使用済みのライセンス数を返す。
     * 
     * @param entity 
     * @param terminalTypeId
     * @param liteTypeId
     * @param reporterTypeId
     * @return 
     */
    @Lock(LockType.READ)
    private Triplet<Long, Long, Long> countUsedLicenses(EquipmentEntity entity, Long terminalTypeId, Long liteTypeId, Long reporterTypeId) {

        long first = 0;
        long second = 0;
        long third = 0;

        if (Objects.equals(entity.getEquipmentTypeId(), terminalTypeId)) {
            first++;
        } else if (Objects.equals(entity.getEquipmentTypeId(), liteTypeId)) {
            second++;
        } else if (Objects.equals(entity.getEquipmentTypeId(), reporterTypeId)) {
            third++;
        }

        List<EquipmentEntity> children = this.findChild(entity.getEquipmentId(), null, null, null);
        if (children == null) {
            return Triplet.of(first, second, third);
        }

        for (EquipmentEntity child : children) {
            Triplet<Long, Long, Long> res = countUsedLicenses(child,  terminalTypeId, liteTypeId, reporterTypeId);

            first += res.first;
            second += res.second;
            third += res.third;
        }

        return Triplet.of(first, second, third);
    }
    /**
     * 設備IDを指定して、設備情報を取得する。
     *
     * @param id     設備ID
     * @param authId 認証ID
     * @return 設備情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public EquipmentEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);
        EquipmentEntity equipment = super.find(id);
        if (Objects.isNull(equipment)) {
            return new EquipmentEntity();
        }

        return equipment;
    }

    /**
     * 設備ID一覧を指定して、設備情報一覧を取得する。
     * ※.設備ID一覧の指定がない場合は全件取得。(削除済の組織も含む)
     * ※.設備ID一覧の指定がある場合、削除済の設備は対象外。
     *
     * @param ids    設備ID一覧
     * @param authId 認証ID
     * @return 設備情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<EquipmentEntity> find(@QueryParam("id") final List<Long> ids, @QueryParam("authId") Long authId) {
        logger.info("find: ids={}, authId={}", ids, authId);
        List<EquipmentEntity> equipments;
        try {
            if (Objects.isNull(ids) || ids.isEmpty()) {
                // 設備情報を全件取得する。
                equipments = super.findAll();
            } else {
                // 設備IDを指定して、設備情報を取得する。(削除済の設備は対象外)
                TypedQuery<EquipmentEntity> query = this.em.createNamedQuery("EquipmentEntity.findByIdsNotRemove", EquipmentEntity.class);
                query.setParameter("equipmentIds", ids);

                equipments = query.getResultList();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
        return equipments;
    }

    /**
     * 指定した設備識別名の設備情報を取得する。
     * ※.削除済の設備は対象外。
     *
     * @param name   設備識別名
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return 設備情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public EquipmentEntity findByName(@QueryParam("name") String name, @QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        logger.info("findName: name={}, userId={}, authId={}", name, userId, authId);
        EquipmentEntity equipment;

        // 設備識別名を指定して、設備情報を取得する。(削除済の設備は対象外)
        TypedQuery<EquipmentEntity> queryExist = this.em.createNamedQuery("EquipmentEntity.findByIdentNorRemove", EquipmentEntity.class);
        queryExist.setParameter("equipmentIdentify", name);
        try {
            equipment = queryExist.getSingleResult();
        } catch (NoResultException ex) {
            logger.fatal(ex);
            return new EquipmentEntity();
        }

        // 対象設備からルートまでの階層アクセス権をチェックする。
        boolean isAccessible = this.isHierarchyAccessible(equipment.getEquipmentId(), userId);
        if (!isAccessible) {
            return new EquipmentEntity();
        }

        return equipment;
    }

    /**
     * 設備情報一覧を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な設備情報を対象とする。
     *
     * @param userId ユーザーID (組織ID)
     * @param from   範囲の先頭
     * @param to     範囲の末尾
     * @param authId 認証ID
     * @return 設備情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<EquipmentEntity> findRange(@QueryParam("user") Long userId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: userId={}, from={}, to={}, authId={}", userId, from, to, authId);

        TypedQuery<EquipmentEntity> query;
        if (Objects.isNull(userId)) {
            // 設備情報をすべて取得する。 ※.削除済も対象
            query = this.em.createNamedQuery("EquipmentEntity.findAll", EquipmentEntity.class);
        } else {
            // ユーザーIDが指定されている場合、アクセス可能な設備情報を対象とする。

            // ユーザーのルートまでの親組織ID一覧を取得する。
            List<Long> organizationIds = this.organizationFacade.findAncestors(userId);

            // 設備情報一覧を取得する。(指定ユーザーがアクセス可能な設備のみ) ※.削除済も対象
            query = this.em.createNamedQuery("EquipmentEntity.findByUserId", EquipmentEntity.class);
            query.setParameter("ancestors", organizationIds);
        }

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        return query.getResultList();
    }

    /**
     * 設備情報の件数を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な設備情報を対象とする。
     *
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return 設備情報の件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countAll(@QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        logger.info("countAll: userId={}, authId={}", userId, authId);

        long count;
        if (Objects.isNull(userId)) {
            count = super.count();
        } else {
            // ユーザーIDが指定されている場合、アクセス可能な設備情報を対象とする。

            // ユーザーのルートまでの親組織ID一覧を取得する。
            List<Long> organizationIds = this.organizationFacade.findAncestors(userId);

            // 設備情報の件数を取得する。(指定ユーザーがアクセス可能な設備のみ)
            TypedQuery<Long> query = this.em.createNamedQuery("EquipmentEntity.countByUserId", Long.class);
            query.setParameter("ancestors", organizationIds);

            count = query.getSingleResult();
        }
        return String.valueOf(count);
    }

    /**
     * 設備検索条件を指定して、設備情報一覧を取得する。
     *
     * @param condition 設備検索条件
     * @param from      範囲指定始点
     * @param to        範囲指定終点
     * @param authId    認証ID
     * @return 設備情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<EquipmentEntity> searchEquipment(EquipmentSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchEquipment: {}, from={}, to={}, authId={}", condition, from, to, authId);

        Query query = this.getSearchQuery(SearchType.SEARCH, condition);

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        List<EquipmentEntity> entities = query.getResultList();

        if (Objects.isNull(authId)) {
            return entities;
        }
        
        OrganizationEntity user = this.organizationFacade.find(authId);
        if (AuthorityEnum.SYSTEM_ADMIN.equals(user.getAuthorityType())) {
            return entities;
        }
        
        // アクセスが許可されているか
        List<EquipmentEntity> result = new ArrayList<>();
        List<Long> organizationIds = this.organizationFacade.findAncestors(authId);

        for (EquipmentEntity entity : entities) {
            // 対象設備のアクセス権を取得
            boolean allow = true;
            List<Hierarchy> permissionList = this.getAccessPermission(entity.getEquipmentId());
            for (Hierarchy permission : permissionList) {
                String[] list = permission.getOrganization().split(",");
                if (!Stream.of(list).map(o -> o.trim()).mapToLong(Long::parseLong).anyMatch(o -> organizationIds.contains(o))) {
                    allow = false;
                    break;
                }
            }
            
            if (allow) {
                result.add(entity);
                if (Boolean.TRUE.equals(condition.isWithChildCount())) {
                    // 子設備の数を取得
                    entity.setChildCount(this.countChild(entity.getEquipmentId(), authId));
                }
            }
        }

        return result;
    }

    /**
     * 設備検索条件を指定して、設備情報の件数を取得する。
     *
     * @param condition 設備検索条件
     * @param authId    認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countEquipment(EquipmentSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countEquipment: {}, authId={}", condition, authId);

        Query query = this.getSearchQuery(SearchType.COUNT, condition);
        return String.valueOf(query.getSingleResult());
    }

    /**
     * 検索条件で設備情報を検索するクエリを取得する。
     *
     * @param type      検索種別
     * @param condition 検索条件
     * @return 検索クエリ
     */
    @Lock(LockType.READ)
    private Query getSearchQuery(SearchType type, EquipmentSearchCondition condition) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<EquipmentEntity> poolEquipment = cq.from(EquipmentEntity.class);

        jakarta.persistence.criteria.Path<Long> pathEquipmentId = poolEquipment.get(EquipmentEntity_.equipmentId);
        jakarta.persistence.criteria.Path<String> pathEquipmentName = poolEquipment.get(EquipmentEntity_.equipmentName);
        jakarta.persistence.criteria.Path<String> pathEquipmentIdentify = poolEquipment.get(EquipmentEntity_.equipmentIdentify);
        jakarta.persistence.criteria.Path<Long> pathEquipmentTypeId = poolEquipment.get(EquipmentEntity_.equipmentTypeId);
        jakarta.persistence.criteria.Path<Boolean> pathRemoveFlag = poolEquipment.get(EquipmentEntity_.removeFlag);
        jakarta.persistence.criteria.Path<Long> pathParentEquipmentId = poolEquipment.get(EquipmentEntity_.parentEquipmentId);
        jakarta.persistence.criteria.Path<String> pathIpv4Address = poolEquipment.get(EquipmentEntity_.ipv4Address);

        // 検索条件
        List<Predicate> where = new LinkedList();

        if (condition.isMatch()) {
            // 完全一致検索

            // 設備名
            if (Objects.nonNull(condition.getEquipmentName())) {
                where.add(cb.equal(cb.lower(pathEquipmentName), StringUtils.toLowerCase(condition.getEquipmentName())));
            }

            // 設備識別名
            if (Objects.nonNull(condition.getEquipmentIdentName())) {
                where.add(cb.equal(cb.lower(pathEquipmentIdentify), StringUtils.toLowerCase(condition.getEquipmentIdentName())));
            }

            // 親設備の設備名
            if (Objects.nonNull(condition.getParentName())) {
                Root<EquipmentEntity> subEquipment = cq.from(EquipmentEntity.class);
                jakarta.persistence.criteria.Path<Long> pathId = subEquipment.get(EquipmentEntity_.equipmentId);
                jakarta.persistence.criteria.Path<String> pathName = subEquipment.get(EquipmentEntity_.equipmentName);

                Subquery<Long> equipNameSubquery = cq.subquery(Long.class);
                equipNameSubquery.select(pathId)
                        .where(cb.equal(cb.lower(pathName), StringUtils.toLowerCase(condition.getParentName())));

                where.add(pathParentEquipmentId.in(equipNameSubquery));
            }

            // 親設備の設備識別名
            if (Objects.nonNull(condition.getParentIdentName())) {
                Root<EquipmentEntity> subEquipment = cq.from(EquipmentEntity.class);
                jakarta.persistence.criteria.Path<Long> pathId = subEquipment.get(EquipmentEntity_.equipmentId);
                jakarta.persistence.criteria.Path<String> pathIdent = subEquipment.get(EquipmentEntity_.equipmentIdentify);

                Subquery<Long> equipIdentSubquery = cq.subquery(Long.class);
                equipIdentSubquery.select(pathId)
                        .where(cb.equal(cb.lower(pathIdent), StringUtils.toLowerCase(condition.getParentIdentName())));

                where.add(pathParentEquipmentId.in(equipIdentSubquery));
            }
        } else {
            // あいまい検索

            // 設備名
            if (Objects.nonNull(condition.getEquipmentName())) {
                where.add(cb.like(cb.lower(pathEquipmentName), QueryUtils.getLikeValue(condition.getEquipmentName())));
            }

            // 設備識別名
            if (Objects.nonNull(condition.getEquipmentIdentName())) {
                where.add(cb.like(cb.lower(pathEquipmentIdentify), QueryUtils.getLikeValue(condition.getEquipmentIdentName())));
            }

            // 親設備の設備名
            if (Objects.nonNull(condition.getParentName())) {
                Root<EquipmentEntity> subEquipment = cq.from(EquipmentEntity.class);
                jakarta.persistence.criteria.Path<Long> pathId = subEquipment.get(EquipmentEntity_.equipmentId);
                jakarta.persistence.criteria.Path<String> pathName = subEquipment.get(EquipmentEntity_.equipmentName);

                Subquery<Long> equipNameSubquery = cq.subquery(Long.class);
                equipNameSubquery.select(pathId)
                        .where(cb.like(cb.lower(pathName), QueryUtils.getLikeValue(condition.getParentName())));

                where.add(pathParentEquipmentId.in(equipNameSubquery));
            }

            // 親設備の設備識別名
            if (Objects.nonNull(condition.getParentIdentName())) {
                Root<EquipmentEntity> subEquipment = cq.from(EquipmentEntity.class);
                jakarta.persistence.criteria.Path<Long> pathId = subEquipment.get(EquipmentEntity_.equipmentId);
                jakarta.persistence.criteria.Path<String> pathIdent = subEquipment.get(EquipmentEntity_.equipmentIdentify);

                Subquery<Long> equipIdentSubquery = cq.subquery(Long.class);
                equipIdentSubquery.select(pathId)
                        .where(cb.like(cb.lower(pathIdent), QueryUtils.getLikeValue(condition.getParentIdentName())));

                where.add(pathParentEquipmentId.in(equipIdentSubquery));
            }
        }

        // 設備種別
        if (Objects.nonNull(condition.getEquipmentType())) {
            Root<EquipmentTypeEntity> poolEquipmentType = cq.from(EquipmentTypeEntity.class);
            jakarta.persistence.criteria.Path<Long> pathId = poolEquipmentType.get(EquipmentTypeEntity_.equipmentTypeId);
            jakarta.persistence.criteria.Path<EquipmentTypeEnum> pathType = poolEquipmentType.get(EquipmentTypeEntity_.name);

            Subquery<Long> equipTypeSubquery = cq.subquery(Long.class);
            equipTypeSubquery.select(pathId)
                    .where(cb.equal(pathType, condition.getEquipmentType()));

            where.add(pathEquipmentTypeId.in(equipTypeSubquery));
        }

        // IPv4アドレス
        if (Objects.nonNull(condition.getIpv4Address())) {
            where.add(cb.equal(pathIpv4Address, condition.getIpv4Address()));
        }

        // 削除フラグ
        if (Objects.nonNull(condition.getRemoveFlag())) {
            where.add(cb.equal(pathRemoveFlag, condition.getRemoveFlag()));
        }

        if (SearchType.COUNT.equals(type)) {
            cq.select(cb.count(pathEquipmentId))
                    .where(cb.and(where.toArray(new Predicate[where.size()])));
        } else {
            cq.select(poolEquipment)
                    .where(cb.and(where.toArray(new Predicate[where.size()])))
                    .orderBy(cb.asc(pathEquipmentId));
        }

        return this.em.createQuery(cq);
    }

    /**
     * 設備情報を登録する。
     *
     * @param entity 設備情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(EquipmentEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            // 設備識別名の重複を確認する。(削除済も含む)
            TypedQuery<Long> query = this.em.createNamedQuery("EquipmentEntity.checkAddByIdent", Long.class);
            query.setParameter("equipmentIdentify", entity.getEquipmentIdentify());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // ライセンスの評価
            if (Objects.nonNull(entity.getEquipmentTypeId())) {
                EquipmentTypeEntity equipmentType = this.equipmentTypeFacade.find(entity.getEquipmentTypeId());
                if (Objects.nonNull(equipmentType) && TERMINAL_TYPES.contains(equipmentType.getName())) {
                    int count = this.countEquipmentType(equipmentType.getEquipmentTypeId());
                    LicenseManager licenseManager = LicenseManager.getInstance();
                    if (licenseManager.checkLicense(equipmentType, count)) {
                        return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.LICENSE_ERROR)).build();
                    }
                }
            }

            // 言語
            if (Objects.nonNull(entity.getLocaleFileInfos())) {
                List<LocaleFileEntity> langIds =
                        entity.getLocaleFileInfos()
                                .stream()
                                .filter(locale -> Objects.nonNull(locale.resource()))
                                .filter(locale -> Objects.nonNull(locale.resource().getResourceString()))
                                .map(locale -> {
                                    ResponseEntity res = (ResponseEntity) this.resourceRest.add(locale.resource()).getEntity();
                                    if (!res.isSuccess()) {
                                        return null;
                                    }
                                    locale.resource().setResourceId(res.getUriId());
                                    return locale;
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                // 言語カラムの言語ファイル情報(JSON)を更新
                entity.setLangIds(jp.adtekfuji.adFactory.utility.JsonUtils.objectsToJson(langIds));
            }

            // 設備情報を登録する。
            entity.setRemoveFlag(false);
            super.create(entity);
            this.em.flush();

            // 作業者端末の設備種別IDを取得する。
            Long terminalEquipmentTypeId = this.getEquipmentType(EquipmentTypeEnum.TERMINAL).getEquipmentTypeId();
            // 作業者端末を削除した場合のみ通知
            if (terminalEquipmentTypeId.equals(entity.getEquipmentTypeId())) {
                // 設備の変更を通知する
                DeviceConnectionServiceCommand command = new DeviceConnectionServiceCommand(DeviceConnectionServiceCommand.COMMAND.UPDATE_EQUIPMENT);
                this.adInterfaceFacade.noticeDeviceConnectionService(command);
            }

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("equipment/").append(entity.getEquipmentId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 設備情報をコピーする。
     *
     * @param id     設備ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Path("copy/{id}")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response copy(@PathParam("id") Long id, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("copy: id={}, authId={}", id, authId);

        EquipmentEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
        }

        boolean isFind = true;
        StringBuilder name = new StringBuilder(entity.getEquipmentIdentify())
                .append(SUFFIX_COPY);
        while (isFind) {
            // 設備識別名の重複を確認する。(削除済も含む)
            TypedQuery<Long> checkQuery = em.createNamedQuery("EquipmentEntity.checkAddByIdent", Long.class);
            checkQuery.setParameter("equipmentIdentify", name.toString());
            if (checkQuery.getSingleResult() > 0) {
                name.append(SUFFIX_COPY);
                continue;
            }
            isFind = false;
        }

        // 言語カラムの言語ファイル情報(JSON)を更新


        EquipmentEntity newEntity = new EquipmentEntity(entity);
        newEntity.setEquipmentIdentify(name.toString());

        // 言語ファイルのコピー
        if (Objects.nonNull(newEntity.getLangIds())) {
            List<LocaleFileEntity> langIds = jp.adtekfuji.adFactory.utility.JsonUtils.jsonToObjects(newEntity.getLangIds(), LocaleFileEntity[].class);
            List<LocaleFileEntity> newLangIds = langIds.stream()
                    .map(langId -> {
                        ResponseEntity res = (ResponseEntity) this.resourceRest.copy(langId.resource().getResourceId()).getEntity();
                        if (!res.isSuccess()) {
                            return null;
                        }
                        langId.resource().setResourceId(res.getUriId());
                        return langId;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            newEntity.setLangIds(jp.adtekfuji.adFactory.utility.JsonUtils.objectsToJson(newLangIds));
        }

        // 新規追加する。
        return this.add(newEntity, authId);
    }

    /**
     * 設備情報を更新する。
     *
     * @param entity 設備情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(EquipmentEntity entity, @QueryParam("authId") Long authId) {
        entity.updateMember();
        logger.info("update: {}, authId={}", entity, authId);

        try {
            // 排他用バージョンを確認する。
            EquipmentEntity target = super.find(entity.getEquipmentId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 自身は親階層に指定できない。
            if (Objects.nonNull(entity.getParentEquipmentId()) && Objects.equals(entity.getEquipmentId(), entity.getParentEquipmentId())) {
                // 移動不可能な階層
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)).build();
            }

            // 設備識別名の重複を確認する。
            TypedQuery<Long> query = this.em.createNamedQuery("EquipmentEntity.checkUpdateByIdent", Long.class);
            query.setParameter("equipmentId", entity.getEquipmentId());
            query.setParameter("equipmentIdentify", entity.getEquipmentIdentify());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 自身の子以降の階層への移動にならないか確認する。
            if (!this.isMoveableHierarchy(entity.getEquipmentId(), entity.getParentEquipmentId())) {
                // 移動不可能な階層
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)).build();
            }

            EquipmentEntity current = this.find(entity.getEquipmentId(), authId);
            EquipmentTypeEntity oldType = Objects.nonNull(current.getEquipmentTypeId()) ? this.equipmentTypeFacade.find(current.getEquipmentTypeId()) : new EquipmentTypeEntity();
            EquipmentTypeEntity newType = Objects.nonNull(entity.getEquipmentTypeId()) ? this.equipmentTypeFacade.find(entity.getEquipmentTypeId()) : new EquipmentTypeEntity();

            if (!Objects.equals(current.getEquipmentTypeId(), entity.getEquipmentTypeId())) {
                LicenseManager licenseManager = LicenseManager.getInstance();
            
                if (TERMINAL_TYPES.contains(newType.getName())) {
                    // ライセンスをチェックする
                    int count = this.countEquipmentType(newType.getEquipmentTypeId());
                    if (licenseManager.checkLicense(newType, count)) {
                        return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.LICENSE_ERROR)).build();
                    }
                }

                if (TERMINAL_TYPES.contains(oldType.getName())) {
                    // 更新前の設備種別が作業者端末だった場合、設備ログイン状態を解除する
                    licenseManager.remove(current.getEquipmentIdentify(), oldType.getName());
                }
            }
            
            // 次回校正日
            if (Objects.nonNull(entity.getCalNextDate())) {
                entity.setCalNextDate(DateUtils.getBeginningOfDate(entity.getCalNextDate()));
            }

            // 言語
            if (Objects.nonNull(entity.getLocaleFileInfos())) {
                List<LocaleFileEntity> langIds = new ArrayList();
                List<LocaleFileEntity> targetLangIds = jp.adtekfuji.adFactory.utility.JsonUtils.jsonToObjects(target.getLangIds(), LocaleFileEntity[].class);
                for (LocaleFileEntity locale : entity.getLocaleFileInfos()) {
                    if (Objects.isNull(locale.resource())|| Objects.isNull(locale.resource().getResourceKey())) {
                        continue;
                    }

                    Optional<LocaleFileEntity> optTargetLangId = targetLangIds.stream()
                            .filter(langId -> Objects.equals(locale.getLocaleType(), langId.getLocaleType()))
                            .findFirst();

                    // 追加
                    if (!optTargetLangId.isPresent()) {
                        if (!StringUtils.isEmpty(locale.resource().getResourceString())) {
                            ResponseEntity res = (ResponseEntity) this.resourceRest.add(locale.resource()).getEntity();
                            if (res.isSuccess()) {
                                locale.resource().setResourceId(res.getUriId());
                                langIds.add(locale);
                            }
                        }
                        continue;
                    }

                    if (!StringUtils.isEmpty(locale.resource().getResourceString())) {
                        // 更新
                        locale.resource().setResourceId(optTargetLangId.get().resource().getResourceId());
                        this.resourceRest.update(locale.resource(), authId).getEntity();
                    }
                    targetLangIds.remove(optTargetLangId.get());
                    langIds.add(locale);
                }

                // 削除
                for (LocaleFileEntity locale : targetLangIds) {
                    this.resourceRest.remove(locale.resource().getResourceId());
                }

                // 登録
                entity.setLangIds(jp.adtekfuji.adFactory.utility.JsonUtils.objectsToJson(langIds));
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 設備情報を更新する。
            super.edit(entity);

            // 作業者端末を変更した場合のみ通知
            if (EquipmentTypeEnum.TERMINAL.equals(oldType.getName())) {
                // 設備の変更を通知する
                DeviceConnectionServiceCommand command = new DeviceConnectionServiceCommand(DeviceConnectionServiceCommand.COMMAND.UPDATE_EQUIPMENT);
                this.adInterfaceFacade.noticeDeviceConnectionService(command);
            }

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定した設備IDの設備情報を削除する。
     *
     * @param id     設備ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("remove: id={}, authId={}", id, authId);

        // 指定された設備に、子設備がある場合は削除できない。
        if (this.countChild(id, null) > 0) {
            logger.info("not remove at exist child:{}", id);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_HIERARCHY_DELETE)).build();
        }

        // 階層アクセス権情報を削除する。
        this.authRest.remove(AccessHierarchyTypeEnum.EquipmentHierarchy, id);

        // 設備IDを指定して、設備情報を取得する。
        EquipmentEntity entity = this.find(id, authId);

        // 設備IDを指定して、工程・設備関連付け情報を削除する。
        Query queryWorkFlow = this.em.createNamedQuery("ConWorkEquipmentEntity.removeByEquipmentId");
        queryWorkFlow.setParameter("equipmentId", id);
        int numWorkFlow = queryWorkFlow.executeUpdate();// 削除した件数が返る。

        // 設備IDを指定して、工程カンバン・設備関連付け情報の件数を取得する。
        TypedQuery<Long> queryKanban = this.em.createNamedQuery("ConWorkkanbanEquipmentEntity.countByEquipmentId", Long.class);
        queryKanban.setParameter("equipmentId", id);
        Long numKanban = queryKanban.getSingleResult();

        // 関連付けが無い場合、完全に削除する。
        if (numWorkFlow == 0 && numKanban == 0) {
            logger.info("remove-real:{}", id);

            // ライセンスマネージャの接続情報を削除する。
            this.removeLicenceInfo(entity);

            // 言語ファイルを削除する
            if(Objects.nonNull(entity.getLangIds())) {
                List<LocaleFileEntity> localeInfos = jp.adtekfuji.adFactory.utility.JsonUtils.jsonToObjects(entity.getLangIds(), LocaleFileEntity[].class);
                localeInfos.forEach(locale -> this.resourceRest.remove(locale.resource().getResourceId()));
            }

            // 作業者端末の設備種別IDを取得する。
            Long terminalEquipmentTypeId = this.getEquipmentType(EquipmentTypeEnum.TERMINAL).getEquipmentTypeId();
            // 作業者端末を削除した場合のみ通知
            if (terminalEquipmentTypeId.equals(entity.getEquipmentTypeId())) {
                // 設備の変更を通知する
                DeviceConnectionServiceCommand command = new DeviceConnectionServiceCommand(DeviceConnectionServiceCommand.COMMAND.UPDATE_ORGANIZATION);
                this.adInterfaceFacade.noticeDeviceConnectionService(command);
            }

            // 設備情報を削除する。
            super.remove(entity);
            return Response.ok().entity(ResponseEntity.success()).build();
        }

        // 関連付けがある場合、削除フラグで論理削除する。

        if (Objects.isNull(entity.getEquipmentId())) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }

        // 削除済の名称に変更する。
        boolean isFind = true;
        int num = 1;
        String baseName = new StringBuilder(entity.getEquipmentIdentify())
                .append(SUFFIX_REMOVE)
                .toString();
        String name = new StringBuilder(baseName)
                .append(num)
                .toString();
        while (isFind) {
            // 設備識別名の重複を確認する。
            TypedQuery<Long> checkQuery = this.em.createNamedQuery("EquipmentEntity.checkUpdateByIdent", Long.class);
            checkQuery.setParameter("equipmentId", id);
            checkQuery.setParameter("equipmentIdentify", name);
            if (checkQuery.getSingleResult() > 0) {
                num++;
                name = new StringBuilder(baseName)
                        .append(num)
                        .toString();
                continue;
            }
            isFind = false;
        }
        logger.info("remove-logic:{},{}", id, name);

        // ライセンスマネージャの接続情報を削除する。
        this.removeLicenceInfo(entity);

        entity.setParentEquipmentId(null);// 階層情報を削除する。
        entity.setEquipmentIdentify(name);
        entity.setRemoveFlag(true);

        // 作業者端末の設備種別IDを取得する。
        Long terminalEquipmentTypeId = this.getEquipmentType(EquipmentTypeEnum.TERMINAL).getEquipmentTypeId();
        // 作業者端末を削除した場合のみ通知
        if (terminalEquipmentTypeId.equals(entity.getEquipmentTypeId())) {
            DeviceConnectionServiceCommand command = new DeviceConnectionServiceCommand(DeviceConnectionServiceCommand.COMMAND.UPDATE_EQUIPMENT);
            this.adInterfaceFacade.noticeDeviceConnectionService(command);
        }

        // 設備情報を更新する。
        super.edit(entity);
        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * ライセンスマネージャの接続情報を削除する。
     *
     * @param entity 設備情報
     */
    private void removeLicenceInfo(EquipmentEntity entity) {
        if (Objects.isNull(entity) || Objects.isNull(entity.getEquipmentTypeId())) {
            return;
        }

        EquipmentTypeEntity equipmentType = this.equipmentTypeFacade.find(entity.getEquipmentTypeId());
        if (Objects.nonNull(equipmentType) && TERMINAL_TYPES.contains(equipmentType.getName())) {
            LicenseManager licenseManager = LicenseManager.getInstance();
            licenseManager.remove(entity.getEquipmentIdentify(), equipmentType.getName());
        }
    }

    /**
     * 設備のログイン処理を行ない、ログイン結果・設備情報を取得する。
     *
     * @param httpRequest
     * @param request 設備ログイン要求情報
     * @param authId  認証ID
     * @return 設備ログイン結果
     * @throws URISyntaxException
     */
    @PUT
    @Path("login")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public EquipmentLoginResult login(@Context HttpServletRequest httpRequest, EquipmentLoginRequest request, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("login: {}, authId={}", request, authId);

        if (Objects.nonNull(httpRequest)) {
            logger.info("HttpServletRequest remoteAddr={}", httpRequest.getRemoteAddr());
        }
       
        if (Objects.isNull(request.getLoginType())) {
            request.setLoginType(EquipmentLoginRequest.LoginType.IDENT_NAME);
        }

        switch (request.getLoginType()) {
            case IDENT_NAME:
                if (Objects.nonNull(httpRequest) && StringUtils.isEmpty(request.getMacAddress())) {
                    request.setMacAddress(httpRequest.getRemoteAddr());
                }
                
                // 設備識別名で設備ログインする。
                return this.loginName(request, authId);
            case IP4_ADDRESS:
                // IPアドレスで設備ログインする。
                return this.loginIpAddress(request, authId);
        }

        return EquipmentLoginResult.failed(ServerErrorTypeEnum.NOT_PERMIT_EQUIPMENT);
    }

    /**
     * 呼出がある設備ID一覧を取得する。（デバッグ用）
     *
     * @param authId 認証ID
     * @return 設備ID一覧
     * @throws URISyntaxException
     */
    @Lock(LockType.READ)
    @GET
    @Path("call")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<EquipmentCallRequest> findCall(@QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("findCall: authId={}", authId);
        List<EquipmentCallRequest> calls = new ArrayList<>();
        Map<Long, Long> runtimeDatas = EquipmentRuntimeData.getInstance().getCollections();
        for (Map.Entry<Long, Long> runtimeData : runtimeDatas.entrySet()) {
            calls.add(new EquipmentCallRequest(runtimeData.getKey(), runtimeData.getValue(), true, ""));
        }
        return calls;
    }

    /**
     * 呼出/呼出解除を通知する。
     *
     * @param request 呼び出し要求情報
     * @param authId  認証ID
     * @return
     * @throws URISyntaxException
     */
    @POST
    @Path("call")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response call(EquipmentCallRequest request, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("call: {}, authId={}", request, authId);



        EquipmentEntity entity = super.find(request.getEquipmentId());
        if (Objects.isNull(entity)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
        }

        EquipmentRuntimeData equipmentCall = EquipmentRuntimeData.getInstance();
        equipmentCall.updateCall(request.getEquipmentId(), request.getOrganizationId(), request.isCall());

        registOperationHistory(request);

        if (Objects.nonNull(this.adInterfaceFacade)) {
            this.adInterfaceFacade.noticeCalling(new CallingNoticeCommand(request.isCall(), request.getEquipmentId(), request.getOrganizationId(), request.getReason()));
        }

        return Response.ok().entity(ResponseEntity.success()).build();
    }


    /**
     * 作業者履歴の登録
     * @param request
     */
    private void registOperationHistory(EquipmentCallRequest request) {
        final Date now = new Date();
        // 作業者履歴登録
        OperationEntity opEntity = new OperationEntity();
        opEntity.setOperateDatetime(now);
        opEntity.setEquipmentId(request.getEquipmentId());
        opEntity.setOrganizationId(request.getOrganizationId());
        opEntity.setOperateApp(Objects.nonNull(request.getOperateApp()) ? request.getOperateApp() : OperateAppEnum.ADPRODUCT);
        opEntity.setOperationType(OperationTypeEnum.CALL);
        CallOperationEntity opeAddInfoEntity = new CallOperationEntity(request.isCall(), request.getReason(), request.getWorkId(), request.getWorkKanbanId());

        try {
            if (!request.isCall()) {
                TypedQuery<Long> query = this.em.createNamedQuery("OperationEntity.findCallPair", Long.class);
                query.setParameter(1, opEntity.getOperateApp().getName());
                query.setParameter(2, opEntity.getOperationType().getName());
                query.setParameter(3, request.getEquipmentId());
                query.setParameter(4, opEntity.getOperationType().getName());

                Long pairId = query.getSingleResult();
                opeAddInfoEntity.setPairId(pairId);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        OperationAddInfoEntity opeAtionAddInfoEntity = new OperationAddInfoEntity();
        opeAtionAddInfoEntity.setCallOperationEntity(opeAddInfoEntity);

        opEntity.setAddInfo(opeAtionAddInfoEntity);
        em.persist(opEntity);
        em.flush();

    }

    /**
     * 設備識別名のリストからそれに対応するエンティティとその子階層のエンティティを取得する。
     *
     * @param identifiers 設備識別名のリスト
     * @param types       取得する設備種別
     * @return
     */
    @Lock(LockType.READ)
    public List<EquipmentEntity> findByNames(List<String> identifiers, EquipmentTypeEnum... types) {
        logger.info("findNames: identifiers={}, types={}", identifiers, types);

        // 設備識別名リストが空の場合、nullを返す。
        if (Objects.isNull(identifiers) || identifiers.isEmpty()) {
            return null;
        }

        // 設備識別名一覧を指定して、設備情報一覧を取得する。(削除済の設備は対象外)
        TypedQuery<EquipmentEntity> query = this.em.createNamedQuery("EquipmentEntity.findByIdentsNotRemove", EquipmentEntity.class);
        query.setParameter("equipmentIdentifiers", identifiers);
        try {
            final List<EquipmentEntity> entities = query.getResultList();
            final List<EquipmentEntity> children = this.findChildren(entities.stream()
                    .map(EquipmentEntity::getEquipmentId)
                    .collect(Collectors.toList()), null, null);

            // 子階層も追加
            for (EquipmentEntity child : children) {
                long childCount = this.countChild(child.getEquipmentId(), null);
                child.setChildCount(childCount);

                if (entities.stream().noneMatch(e -> e.getEquipmentId().equals(child.getEquipmentId()))) {
                    entities.add(child);
                }
            }

            // 現状このメソッドは校正情報のために使っているが、校正情報は測定機器と製造設備のみで動作するため絞り込む
            final List<Long> typeIds = Stream.of(types)
                    .map(type -> this.equipmentTypeFacade.findType(type).getEquipmentTypeId())
                    .collect(Collectors.toList());

            entities.removeIf(entity -> !typeIds.contains(entity.getEquipmentTypeId()) && !identifiers.contains(entity.getEquipmentIdentify()));

            return entities;
        } catch (Exception ex) {
            logger.fatal(ex);
            return null;
        }
    }

    /**
     * 子設備の設備ID一覧を取得する。
     *
     * @param lineId 設備ID
     * @param userId ユーザーID (組織ID)
     * @return 設備ID一覧
     */
    @Lock(LockType.READ)
    public Set<Long> getChild(Long lineId, Long userId) {
        List<EquipmentEntity> equipments = this.findTreeRange(lineId, userId, null, null, null, null);
        Set<Long> equipmentIds = new HashSet<>();
        equipmentIds.add(lineId);
        for (EquipmentEntity equipment : equipments) {
            equipmentIds.add(equipment.getEquipmentId());
        }
        return equipmentIds;
    }

    /**
     * 設備識別名で設備ログインする。
     *
     * @param request 設備ログイン要求情報
     * @param authId  認証ID
     * @return 設備ログイン結果
     */
    private EquipmentLoginResult loginName(EquipmentLoginRequest request, Long authId) {
        EquipmentSearchCondition condition = new EquipmentSearchCondition()
                .equipmentIdentName(request.getAuthData()) // 設備識別名
                .equipmentType(request.getEquipmentType()); // 設備種別
        condition.setRemoveFlag(false); // 削除済は対象外
        condition.setMatch(true); // 完全一致

        // システム設定を取得する。
        List<SystemPropEntity> props = this.systemResource.findSystemProperties(authId);

        List<EquipmentEntity> equipments = this.searchEquipment(condition, null, null, authId);
        if (equipments.size() != 1) {
            return EquipmentLoginResult.failed(ServerErrorTypeEnum.NOT_PERMIT_EQUIPMENT).systemProps(props);
        }

        LicenseManager licenseManager = LicenseManager.getInstance();

        // ライセンスの評価
        if (TERMINAL_TYPES.contains(request.getEquipmentType())) {
            
            EquipmentTypeEntity type = this.getEquipmentType(equipments.get(0).getEquipmentTypeId());
            if (!type.getName().equals(request.getEquipmentType())) {
                // 設備種別が不一致
                return EquipmentLoginResult.failed(ServerErrorTypeEnum.EQUIPMENT_TYPE_INCORRECT);
            }
            
            if (!licenseManager.join(request.getAuthData(), request.getMacAddress(), request.getEquipmentType())) {
                return EquipmentLoginResult.failed(ServerErrorTypeEnum.LICENSE_ERROR);
            }
        }

        EquipmentLoginResult result = this.createLoginSuccessResult(equipments.get(0), props);

        if (EquipmentTypeEnum.TERMINAL.equals(request.getEquipmentType()) && licenseManager.isLicenceOption(LicenseOptionType.LineTimer.getName())) {
            LineManager lineManager = LineManager.getInstance();
            for (AndonMonitorLineProductSetting setting : lineManager.getLineSetting()) {
                if (Objects.equals(setting.getLineId(), result.getEquipmentInfo().getParentId())) {
                    result.setLineId(setting.getLineId());
                    break;
                }
            }
        }

        try {
            // 開始時間・終了時間を返す
            if (EquipmentTypeEnum.TERMINAL.equals(request.getEquipmentType())) {
                condition = new EquipmentSearchCondition().equipmentType(EquipmentTypeEnum.MONITOR);
                List<EquipmentEntity> monitors = this.searchEquipment(condition, null, null, authId);
                for (EquipmentEntity monitor : monitors) {
                    // 進捗モニタ設定情報を取得する。
                    AndonMonitorLineProductSetting setting = lineMonitorFacade.getLineSetting(monitor.getEquipmentId());
                    if (Objects.nonNull(setting.getLineId()) && 0L != setting.getLineId()) {
                        List<EquipmentEntity> terminals = this.findTreeRange(setting.getLineId(), null, null, null, null, authId);
                        if (terminals.contains(equipments.get(0))) {
                            result.setStartWorkTime(setting.getStartWorkTime());
                            result.setEndWorkTime(setting.getEndWorkTime());
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return result;
    }

    /**
     * IPアドレスで設備ログインする。
     *
     * @param request 設備ログイン要求情報
     * @param authId  認証ID
     * @return 設備ログイン結果
     */
    private EquipmentLoginResult loginIpAddress(EquipmentLoginRequest request, Long authId) {
        EquipmentSearchCondition condition = new EquipmentSearchCondition()
                .equipmentType(request.getEquipmentType())// 設備種別
                .ipv4Address(request.getAuthData());// IPアドレス
        condition.setRemoveFlag(false);// 削除済は対象外
        condition.setMatch(true); // 完全一致

        // システム設定を取得する。
        List<SystemPropEntity> props = this.systemResource.findSystemProperties(authId);

        List<EquipmentEntity> equipments = searchEquipment(condition, null, null, authId);
        if (equipments.size() != 1) {
            return EquipmentLoginResult.failed(ServerErrorTypeEnum.NOT_PERMIT_EQUIPMENT).systemProps(props);
        }

        EquipmentLoginResult result = this.createLoginSuccessResult(equipments.get(0), props);

        try {
            // 開始時間・終了時間を返す
            if (EquipmentTypeEnum.TERMINAL == request.getEquipmentType()) {
                condition = new EquipmentSearchCondition().equipmentType(EquipmentTypeEnum.MONITOR);
                List<EquipmentEntity> monitors = this.searchEquipment(condition, null, null, authId);
                for (EquipmentEntity monitor : monitors) {
                    // 進捗モニタ設定情報を取得する。
                    AndonMonitorLineProductSetting setting = lineMonitorFacade.getLineSetting(monitor.getEquipmentId());
                    if (Objects.nonNull(setting.getLineId()) && 0L != setting.getLineId()) {
                        List<EquipmentEntity> terminals = this.findTreeRange(setting.getLineId(), null, null, null, null, authId);
                        if (terminals.contains(equipments.get(0))) {
                            result.setStartWorkTime(setting.getStartWorkTime());
                            result.setEndWorkTime(setting.getEndWorkTime());
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return result;
    }

    /**
     * ログイン成功時の設備ログイン結果情報を作成する。
     *
     * @param entity 設備情報
     * @param props  システムプロパティ一覧
     * @return 設備ログイン結果情報
     */
    @Lock(LockType.READ)
    private EquipmentLoginResult createLoginSuccessResult(EquipmentEntity entity, List<SystemPropEntity> props) {
        EquipmentRuntimeData equipmentCall = EquipmentRuntimeData.getInstance();
        Long equipmentId = entity.getEquipmentId();

        EquipmentInfoEntity equipmentInfo = new EquipmentInfoEntity(entity.getEquipmentId(), entity.getEquipmentIdentify(), entity.getEquipmentName(), entity.getEquipmentTypeId());
        equipmentInfo.setIpv4Address(entity.getIpv4Address());
        equipmentInfo.setWorkProgressFlag(entity.getWorkProgressFlag());
        equipmentInfo.setParentId(entity.getParentEquipmentId());
        return EquipmentLoginResult.success(equipmentId, equipmentCall.checkCall(equipmentId)).equipmentInfo(equipmentInfo).systemProps(props);
    }

    /**
     * 指定した設備の子設備情報一覧を取得する。
     *
     * @param id     設備ID
     * @param userId ユーザーID (組織ID)
     * @param from   範囲の先頭
     * @param to     範囲の末尾
     * @return 子設備情報一覧
     */
    @Lock(LockType.READ)
    private List<EquipmentEntity> findChild(Long id, Long userId, Integer from, Integer to) {
        TypedQuery<EquipmentEntity> query;

        if (Objects.isNull(userId)) {
            // 指定した設備IDの子設備情報一覧を取得する。
            query = this.em.createNamedQuery("EquipmentEntity.findChild", EquipmentEntity.class);
        } else {
            // ユーザーIDが指定されている場合、アクセス可能な設備情報を対象とする。

            // ユーザーのルートまでの親組織ID一覧を取得する。
            List<Long> organizationIds = this.organizationFacade.findAncestors(userId);

            // 指定した設備IDの子設備情報一覧を取得する。(指定ユーザーがアクセス可能な設備のみ)
            query = this.em.createNamedQuery("EquipmentEntity.findChildByUserId", EquipmentEntity.class);
            query.setParameter("ancestors", organizationIds);
        }
        query.setParameter("equipmentId", id);

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }
        return query.getResultList();
    }

    /**
     * 指定した設備ID一覧から、それらの設備の子設備一覧を取得する。
     *
     * @param equipmentIds
     * @param from
     * @param to
     * @return
     */
    @Lock(LockType.READ)
    private List<EquipmentEntity> findChildren(List<Long> equipmentIds, Integer from, Integer to) {
        if (equipmentIds.isEmpty()) {
            return new ArrayList();
        }

        TypedQuery<EquipmentEntity> query;

        if (Objects.isNull(equipmentIds)) {
            query = this.em.createNamedQuery("EquipmentEntity.findAll", EquipmentEntity.class);
        } else {
            query = this.em.createNamedQuery("EquipmentEntity.findChilds", EquipmentEntity.class);
            query.setParameter("equipmentIds", equipmentIds);
        }

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        return query.getResultList();
    }

    /**
     * 設備種別名を指定して、設備種別情報を取得する。
     *
     * @param type 設備種別
     * @return 設備種別情報
     */
    @Lock(LockType.READ)
    public EquipmentTypeEntity getEquipmentType(EquipmentTypeEnum type) {
        try {
            // 設備種別名を指定して、設備種別情報を取得する。
            TypedQuery<EquipmentTypeEntity> query = this.em.createNamedQuery("EquipmentTypeEntity.findByName", EquipmentTypeEntity.class);
            query.setParameter("name", type);
            EquipmentTypeEntity entity = query.getSingleResult();
            return entity;
        } catch (Exception ex) {
            EquipmentTypeEntity entity = new EquipmentTypeEntity(type);
            entity.setEquipmentTypeId(1L);
            return entity;
        }
    }

    /**
     * 設備種別IDを指定して、設備種別情報を取得する。
     * 
     * @param id 設備種別ID
     * @return 
     */
    @Lock(LockType.READ)
    public EquipmentTypeEntity getEquipmentType(Long id) {
        try {
            // 設備種別IDを指定して、設備種別情報を取得する。
            TypedQuery<EquipmentTypeEntity> query = this.em.createNamedQuery("EquipmentTypeEntity.findById", EquipmentTypeEntity.class);
            query.setParameter("id", id);
            EquipmentTypeEntity entity = query.getSingleResult();
            return entity;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 全ての設備種別を取得する
     * @return 全設備種別
     */
    public List<EquipmentTypeEntity> getEquipmentType() {
        try {
            // 設備種別IDを指定して、設備種別情報を取得する。
            TypedQuery<EquipmentTypeEntity> query = this.em.createNamedQuery("EquipmentTypeEntity.findAll", EquipmentTypeEntity.class);
            return query.getResultList();
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    /**
     * 設備種別IDを指定して、設備情報の件数を取得する。
     *
     * @param equipmentTypeId 設備種別ID
     * @return
     */
    @Lock(LockType.READ)
    private int countEquipmentType(Long equipmentTypeId) {
        // 設備種別IDを指定して、設備情報の件数を取得する。(削除済の設備は対象外)
        TypedQuery<Long> query = this.em.createNamedQuery("EquipmentEntity.countByEquipmentType", Long.class);
        query.setParameter("equipmentTypeId", equipmentTypeId);
        return query.getSingleResult().intValue();
    }

    /**
     * 指定した設備ID一覧とそれらの子以降の設備ID一覧を取得する。
     *
     * @param equipmentIds 設備ID一覧
     * @return 指定した設備IDとその子以降の設備ID一覧
     */
    @Lock(LockType.READ)
    public Set<Long> getRelatedEquipmentIds(List<Long> equipmentIds) {
        Set<Long> ids = new HashSet<>();
        ids.addAll(equipmentIds);
        for (Long parentId : equipmentIds) {
            ids.addAll(this.getEquipmentChildren(parentId));
        }
        return ids;
    }

    /**
     * 指定された階層に、ユーザーがアクセス可能か取得する。
     *
     * @param id     階層ID (設備ID)
     * @param userId ユーザーID (組織ID)
     * @return アクセス (true: アクセス可能, false: アクセス不可)
     */
    @Lock(LockType.READ)
    private boolean isHierarchyAccessible(Long id, Long userId) {
        // ユーザーIDが未指定の場合、アクセス可能。
        if (Objects.isNull(userId)) {
            return true;
        }

        // 設備のアクセス権をチェックする。
        boolean isAccessible = this.accessHierarchyFacade.isAccessible(AccessHierarchyTypeEnum.EquipmentHierarchy, id, userId);
        if (!isAccessible) {
            return false;
        }

        // 設備IDを指定して、親設備IDを取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("EquipmentEntity.findParentId", Long.class);
        query.setParameter("equipmentId", id);

        Long parentId;
        try {
            parentId = query.getSingleResult();
        } catch (Exception ex) {
            parentId = null;
        }

        // 親設備が存在する場合、親設備の階層アクセス権をチェックする。
        if (Objects.nonNull(parentId) && parentId > 0) {
            isAccessible = this.isHierarchyAccessible(parentId, userId);
        }

        return isAccessible;
    }

    /**
     * 設備IDを指定して、設備名を取得する。
     *
     * @param id 設備ID
     * @return 設備名
     */
    @Lock(LockType.READ)
    public String findNameById(long id) {
        try {
            TypedQuery<String> query = this.em.createNamedQuery("EquipmentEntity.findNameById", String.class);
            query.setParameter("equipmentId", id);
            return query.getSingleResult();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 設備IDを指定して、子設備ID一覧を取得する。
     *
     * @param id 設備ID
     * @return 設備ID一覧
     */
    @Lock(LockType.READ)
    public List<Long> findChildIds(long id) {
        try {
            TypedQuery<Long> query = this.em.createNamedQuery("EquipmentEntity.findChildId", Long.class);
            query.setParameter("equipmentId", id);
            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 移動可能な階層かチェックする。
     *
     * @param id       階層ID
     * @param parentId 移動先の階層ID
     * @return 移動可能な階層か (true:可能, false:不可)
     */
    private boolean isMoveableHierarchy(Long id, Long parentId) {
        // ルートへの移動は可能。
        if (parentId.equals(0L)) {
            return true;
        }

        // 階層IDを指定して、親階層IDを取得する。
        Long nextParentId = this.findParentId(parentId);

        if (Objects.isNull(nextParentId)) {
            // ルートまで繋がっていないので移動不可。
            return false;
        } else if (nextParentId.equals(id)) {
            // ループするため移動不可。
            return false;
        } else if (nextParentId.equals(0L)) {
            // ルートまで繋がっているので移動可能。
            return true;
        } else {
            // さらに親階層をチェックする。
            return this.isMoveableHierarchy(id, nextParentId);
        }
    }

    /**
     * 設備IDを指定して、親設備IDを取得する。
     *
     * @param id 設備ID
     * @return 親設備ID
     */
    private Long findParentId(Long id) {
        try {
            // 設備IDを指定して、親設備IDを取得する。
            TypedQuery<Long> query = this.em.createNamedQuery("EquipmentEntity.findParentId", Long.class);
            query.setParameter("equipmentId", id);

            return query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * JSONファイルから設備情報をインポートする。
     *
     * @param inputStreams JSONファイル(FormDataParam)
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @POST
    @Path(value = "import")
    @Consumes("multipart/form-data")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response importFile(@FormDataParam("file") ArrayList<InputStream> inputStreams) {
        logger.info("importFile: start");
        try {
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
            }

            long adminId = organizationFacade.checkExsistAdmin();
            String jsonStr = Files.lines(Paths.get(filePath), Charset.forName("UTF-8")).collect(Collectors.joining(System.getProperty("line.separator")));
            List<EquipmentImportEntity> list = JsonUtils.jsonToObjects(jsonStr, EquipmentImportEntity[].class);

            // インポートする設備識別名のリスト
            Set<String> importIdents = list.stream()
                    .map(p -> p.getEquipmentIdentify())
                    .collect(Collectors.toSet());

            // インポートする親設備識別名のリスト
            Set<String> importParents = list.stream()
                    .filter(p -> !p.getParentName().isEmpty())
                    .map(p -> p.getParentName())
                    .collect(Collectors.toSet());

            importIdents.addAll(importParents);

            // ルート直下の設備を取得する。
            List<EquipmentEntity> childEquipments = this.findChild(0L, null, null, null);

            // 不要な設備を削除する。
            for (int i = childEquipments.size() - 1; i >= 0; i--) {
                this.removeEquipmentWhenImport(childEquipments.get(i), importIdents);
            }

            for (EquipmentImportEntity info : list) {

                String equipmentIdentify = info.getEquipmentIdentify();
                String equipmentName = info.getEquipmentName();
                String parentName = info.getParentName();
                Long equipmentType = info.getEquipmentTypeId();
                // 校正実施者（組織識別名）から組織IDを取得
                Long calPersonId = null;
                OrganizationEntity organization = organizationFacade.findByName(info.getCalperson(), null, null);
                if (Objects.nonNull(organization.getOrganizationId())) {
                    calPersonId = organization.getOrganizationId();
                }

                // 親設備IDを取得
                // 親設備が指定なしの場合はルートID
                long parentId = 0L;
                EquipmentEntity parent = null;
                if (!StringUtils.isEmpty(parentName)) {
                    parent = this.findByName(parentName, null, null);

                    if (Objects.isNull(parent) || Objects.isNull(parent.getEquipmentId())) {
                        // 親設備が存在しない場合
                        parent = new EquipmentEntity(0L, parentName, parentName, null, adminId, now);
                        this.add(parent, null);

                        ResponseEntity parResponse = (ResponseEntity) this.add(parent, null).getEntity();
                        if (!parResponse.isSuccess()) {
                            logger.warn("Faled parent add: {}", parent.getEquipmentIdentify());
                        }

                        parent = this.findByName(parentName, null, null);
                        parentId = parent.getEquipmentId();

                    } else {
                        parentId = parent.getEquipmentId();
                    }
                }

                // 子設備を更新
                EquipmentEntity equipment = this.findByName(equipmentIdentify, null, null);
                if (Objects.isNull(equipment) || Objects.isNull(equipment.getEquipmentId())) {
                    // 追加
                    equipment = new EquipmentEntity(parentId, equipmentName, equipmentIdentify, equipmentType, adminId, now);
                    equipment.setWorkProgressFlag(false);
                    // プラグイン名
                    equipment.setPluginName(info.getPluginName());
                    // 削除フラグ
                    equipment.setRemoveFlag(info.isRemoveFlg());
                    // 追加情報
                    equipment.setEquipmentAddInfo(info.getAddInfoToString());
                    // プラグイン名が1：Simple以外のときに校正の設定を追加
                    if (!StringUtils.isEmpty(info.getPluginName()) && !info.getPluginName().equals("Simple")) {
                        // 機器校正有無
                        equipment.setCalFlag(info.getCalFlg());
                        // 次回校正日
                        equipment.setCalNextDate(info.getCalNextDate());
                        // 警告表示日数
                        equipment.setCalWarningDays(info.getCalWarnigDays());
                        // 校正間隔
                        equipment.setCalTerm(info.getCalTerm());
                        // 校正間隔単位
                        equipment.setCalTermUnit(TermUnitEnum.fromString(info.getCalTermUnit()));
                        // 最終校正日
                        equipment.setCalLastDate(info.getCalLastDate());
                        // 校正実施者
                        equipment.setCalPersonId(calPersonId);
                    }

                    ResponseEntity addResponse = (ResponseEntity) this.add(equipment, null).getEntity();
                    if (!addResponse.isSuccess()) {
                        logger.warn("Faled add: {}", equipmentIdentify);
                    }

                    equipment = this.findByName(equipmentIdentify, null, null);
                    if (!this.isMoveableHierarchy(equipment.getEquipmentId(), parentId)) {
                        logger.warn("Illegal hierarchy: {} {}", parentName, equipmentIdentify);
                        this.remove(equipment.getEquipmentId(), null);
                    }

                } else {
                    // 更新
                    if (!StringUtils.equals(equipment.getEquipmentName(), equipmentName)) {
                        equipment.setEquipmentName(equipmentName);
                    }

                    if (!Objects.equals(equipment.getParentEquipmentId(), parentId)) {
                        // 階層をチェック
                        if (this.isMoveableHierarchy(equipment.getEquipmentId(), parentId)) {
                            equipment.setParentEquipmentId(parentId);
                        } else {
                            logger.warn("Illegal hierarchy: {} {}", parentName, equipmentIdentify);
                        }
                    }

                    // プラグイン名
                    equipment.setPluginName(info.getPluginName());
                    // 削除フラグ
                    equipment.setRemoveFlag(info.isRemoveFlg());
                    // 追加情報
                    equipment.setEquipmentAddInfo(info.getAddInfoToString());
                    // プラグイン名が1：Simple以外のときに校正の設定を追加
                    if (!StringUtils.isEmpty(info.getPluginName()) && !info.getPluginName().equals("Simple")) {
                        // 機器校正有無
                        equipment.setCalFlag(info.getCalFlg());
                        // 次回校正日
                        equipment.setCalNextDate(info.getCalNextDate());
                        // 警告表示日数
                        equipment.setCalWarningDays(info.getCalWarnigDays());
                        // 校正間隔
                        equipment.setCalTerm(info.getCalTerm());
                        // 校正間隔単位
                        equipment.setCalTermUnit(TermUnitEnum.fromString(info.getCalTermUnit()));
                        // 最終校正日
                        equipment.setCalLastDate(info.getCalLastDate());
                        // 校正実施者
                        equipment.setCalPersonId(calPersonId);
                    }

                    equipment.setUpdatePersonId(adminId);
                    equipment.setUpdateDatetime(now);

                    ResponseEntity updResponse = (ResponseEntity) this.update(equipment, null).getEntity();
                    if (!updResponse.isSuccess()) {
                        logger.warn("Faled update: {}", equipmentIdentify);
                    }
                }
            }
            file.delete();

            // 設備の変更を通知する
            DeviceConnectionServiceCommand command = new DeviceConnectionServiceCommand(DeviceConnectionServiceCommand.COMMAND.UPDATE_EQUIPMENT);
            this.adInterfaceFacade.noticeDeviceConnectionService(command);

        } catch (IOException | URISyntaxException ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        } finally {
            logger.info("importFile end :");
        }

        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * 指定した設備識別名一覧に含まれない設備・子設備を削除する。
     *
     * @param equipment    設備情報
     * @param importIdents 設備識別名一覧
     * @return 指定した設備が削除されたか？
     */
    private boolean removeUnnecessary(EquipmentEntity equipment, Set<String> importIdents) {
        boolean result = false;
        try {
            // 子組織を取得する。
            List<EquipmentEntity> childEquipments = this.findChild(equipment.getEquipmentId(), null, null, null);

            int childNum = childEquipments.size();

            // 不要な子設備を削除する。
            for (int i = childNum - 1; i >= 0; i--) {
                if (this.removeUnnecessary(childEquipments.get(i), importIdents)) {
                    childNum--;
                }
            }

            // 今回追加・更新する設備は削除しない。
            if (importIdents.contains(equipment.getEquipmentIdentify())) {
                return result;
            }

            // 子設備がある場合は削除しない。
            if (childNum > 0) {
                logger.info("Undeletable equipment (exist child): {}", equipment.getEquipmentIdentify());
                return result;
            }

            // 工程順の工程で使用している場合は削除しない。
            TypedQuery<Long> query1 = em.createNamedQuery("ConWorkEquipmentEntity.countByEquipmentId", Long.class);
            query1.setParameter("equipmentId", equipment.getEquipmentId());
            Long num = query1.getSingleResult();

            if (num > 0) {
                logger.info("Undeletable equipment (used in work): {}", equipment.getEquipmentIdentify());
                return result;
            }

            // 設備を削除する。
            logger.info("Remove equipment: {}", equipment.getEquipmentIdentify());
            this.remove(equipment.getEquipmentId(), null);

            result = true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 設備のインポート実行時に不要な設備を削除する
     *
     * @param equipment    設備情報
     * @param importIdents 設備識別名一覧
     * @return 指定した設備が削除されたか？
     */
    private boolean removeEquipmentWhenImport(EquipmentEntity equipment, Set<String> importIdents) {
        try {

            // 子組織を取得する。
            List<EquipmentEntity> childEquipments = this.findChild(equipment.getEquipmentId(), null, null, null);

            int childNum = childEquipments.size();

            // 末端の設備から削除するために子設備がいないものから実行。
            for (int i = childNum - 1; i >= 0; i--) {
                if (this.removeEquipmentWhenImport(childEquipments.get(i), importIdents)) {
                    childNum--;
                }
            }

            // 削除の条件にあっているか判定。
            boolean isRemove = isRemoveableEquipment(equipment, importIdents, childNum);


            // 設備を削除する。
            if (isRemove) {
                logger.info("Remove equipment: {}", equipment.getEquipmentIdentify());
                this.remove(equipment.getEquipmentId(), null);
                return true;
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return false;
    }

    /**
     * 削除してよい設備かどうか判定する
     *
     * @param equipment    設備情報
     * @param importIdents 設備識別名一覧
     * @return true = 削除してよい　false = 削除不可
     */
    private boolean isRemoveableEquipment(EquipmentEntity equipment, Set<String> importIdents, int childNum) {
        try {
            // 測定機器の設備でなければ削除しない           
            if (!Objects.equals(equipment.getEquipmentTypeId(), this.getEquipmentType(EquipmentTypeEnum.MEASURE).getEquipmentTypeId())) {
                //ログを出す
                return false;
            }

            // 子設備がある場合は削除しない。
            if (childNum > 0) {
                logger.info("Undeletable equipment (exist child): {}", equipment.getEquipmentIdentify());
                return false;
            }

            // 今回追加・更新する設備は削除しない。
            if (importIdents.contains(equipment.getEquipmentIdentify())) {
                //ログを出す
                return false;
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        }
        return true;
    }

    /**
     * アクセス権を取得する。
     * 
     * @param id 設備ID
     * @return アクセス権
     */
    @Lock(LockType.READ)
    private List<Hierarchy> getAccessPermission(Long id) {
        Query query = this.em.createNamedQuery("Hierarchy.findEquipment", Hierarchy.class);
        query.setParameter(1, id);
        return query.getResultList();
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
        this.lineMonitorFacade = new AndonLineMonitorFacadeREST();
        this.systemResource = new SystemResource();
    }

    protected void setAuthRest(AccessHierarchyEntityFacadeREST authRest) {
        this.authRest = authRest;
    }

    protected void setEquipmentTypeRest(EquipmentTypeEntityFacadeREST equipmentTypeRest) {
        this.equipmentTypeFacade = equipmentTypeRest;
    }

    public void setAdInterfaceClientFacade(AdIntefaceClientFacade adInterfaceFacade) {
        this.adInterfaceFacade = adInterfaceFacade;
    }

    /**
     * 製造設備の祖先の一覧を取得する
     *
     * @param ids
     * @param authId
     * @return
     */
    public List<EquipmentEntity> findEquipmentAncestors(final List<Long> ids, Long authId) {
        logger.info("findEquipmentAncestors: ids={}, authId={}", ids, authId);

        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            java.sql.Array idArray = this.em.unwrap(Connection.class).createArrayOf("integer", ids.toArray());
            final Query query = em
                    .createNamedQuery("EquipmentEntity.findAncestorsByIds", EquipmentEntity.class)
                    .setParameter(1, idArray);

            final List<EquipmentEntity> result = query.getResultList();
            return result;
        } catch (SQLException ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }


}
