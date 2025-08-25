/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.PasswordEncoder;
import adtekfuji.utility.StringUtils;
import jakarta.annotation.PostConstruct;
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
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import jp.adtekfuji.adFactory.adinterface.command.DeviceConnectionServiceCommand;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.SampleResponse;
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginRequest;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginResult;
import jp.adtekfuji.adFactory.entity.organization.LocaleFileInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.search.OrganizationSearchCondition;
import jp.adtekfuji.adFactory.enumerate.*;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adfactoryserver.common.ServiceConfig;
import jp.adtekfuji.adfactoryserver.entity.access.Hierarchy;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.IndirectWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.ConWorkkanbanOrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.ConWorkkanbanOrganizationEntity_;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity_;
import jp.adtekfuji.adfactoryserver.entity.master.BreaktimeEntity;
import jp.adtekfuji.adfactoryserver.entity.master.ReasonCategoryEntity;
import jp.adtekfuji.adfactoryserver.entity.master.ReasonMasterEntity;
import jp.adtekfuji.adfactoryserver.entity.master.RoleAuthorityEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.ConOrganizationBreaktimeEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.ConOrganizationReasonEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.ConOrganizationRoleEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.ConOrganizationWorkCategoryEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity_;
import static jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity_.organizationId;
import jp.adtekfuji.adfactoryserver.entity.resource.LocaleFileEntity;
import jp.adtekfuji.adfactoryserver.entity.resource.ResourceEntity;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.QueryUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * 組織情報REST
 *
 * @author ke.yokoi
 */
@Singleton
@Path("organization")
public class OrganizationEntityFacadeREST extends AbstractFacade<OrganizationEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private EquipmentEntityFacadeREST equipmentRest;

    @EJB
    private RoleEntityFacadeREST roleRest;

    @EJB
    private AccessHierarchyEntityFacadeREST authRest;

    @EJB
    private BreaktimeEntityFacadeREST breaktimeRest;

    @EJB
    private ResourceEntityFacadeREST resourceRest;

    @EJB
    private AdIntefaceClientFacade adIntefaceFacade;
    
    @EJB
    private ReasonMasterEntityFacadeREST reasonRest;
    
    @EJB
    IndirectWorkEntityFacadeREST indirectWorkRest;

    private final Logger logger = LogManager.getLogger();
    private String ldapProviderURL;
    private String ldapDomain;

    /**
     * コンストラクタ
     */
    public OrganizationEntityFacadeREST() {
        super(OrganizationEntity.class);
    }

    /**
     * クラスを初期化する。
     */
    @PostConstruct
    public void initialize() {
        try {
            ServiceConfig config = ServiceConfig.getInstance();
            
            // LDAP認証 接続先情報取得
            this.ldapProviderURL = config.getLdapProviderURL();
            this.ldapDomain = config.getLdapDomain();

        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }
    
    /**
     * 組織IDを指定して、ルートまでの親組織・指定した組織・最下層までの子組織の組織ID一覧を取得する。
     *
     * @param organizationId 組織ID
     * @return 組織ID一覧
     */
    @Lock(LockType.READ)
    public List<Long> getOrganizationPerpetuity(Long organizationId) {
        List<Long> organizationIds = new ArrayList();
        organizationIds.add(organizationId);
        organizationIds.addAll(this.getOrganizationParentPerpetuity(organizationId));
        organizationIds.addAll(this.getOrganizationChildPerpetuity(organizationId));
        return organizationIds;
    }

    /**
     * 組織IDを指定して、指定した組織・最下層までの子組織の組織ID一覧を取得する。
     *
     * @param organizationId 組織ID
     * @return 組織ID一覧
     */
    @Lock(LockType.READ)
    public List<Long> getOrganizationChildren(Long organizationId) {
        List<Long> organizationIds = new ArrayList();
        organizationIds.add(organizationId);
        organizationIds.addAll(this.getOrganizationChildPerpetuity(organizationId));
        return organizationIds;
    }

    /**
     * 組織IDを指定して、ルートまでの親組織の組織ID一覧を取得する。(指定した組織は含まない)
     *
     * @param organizationId 組織ID
     * @return 組織ID一覧
     */
    @Lock(LockType.READ)
    public List<Long> getOrganizationParentPerpetuity(Long organizationId) {
        List<Long> organizationIds = new ArrayList();

        // 設備IDを指定して、親設備IDを取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("OrganizationEntity.findParentId", Long.class);
        query.setParameter("organizationId", organizationId);
        Long id; 
        try {
            id = query.getSingleResult();
        } catch (NoResultException ex) {
            id = null;
        }

        if (Objects.nonNull(id) && id != 0) {
            organizationIds.add(id);
            organizationIds.addAll(this.getOrganizationParentPerpetuity(id));
        }

        return organizationIds;
    }

    /**
     * 組織IDを指定して、最下層までの子組織の組織ID一覧を取得する。(指定した組織は含まない)
     *
     * @param organizationId 組織ID
     * @return 組織ID一覧
     */
    @Lock(LockType.READ)
    private List<Long> getOrganizationChildPerpetuity(Long organizationId) {
        List<Long> organizationIds = new ArrayList();

        // 組織IDを指定して、子組織ID一覧を取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("OrganizationEntity.findChildId", Long.class);
        query.setParameter("organizationId", organizationId);
        List<Long> ids = query.getResultList();

        for (Long id : ids) {
            organizationIds.add(id);
            organizationIds.addAll(this.getOrganizationChildPerpetuity(id));
        }

        return organizationIds;
    }

    /**
     * 指定した組織の子組織情報一覧を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な組織情報を対象とする。
     *
     * @param id 組織ID
     * @param userId ユーザーID (組織ID)
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 子組織情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("tree/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<OrganizationEntity> findTreeRange(@QueryParam("id") Long id, @QueryParam("user") Long userId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findTree: id={}, userId={}, from={}, to={}, authId={}", id, userId, from, to, authId);
        // 組織IDの指定がない場合はルートとする。
        if (Objects.isNull(id)) {
            id = 0L;
        }

        // 指定した組織の子組織情報一覧を取得する。
        List<OrganizationEntity> entities = this.findChild(id, userId, from, to);
        this.em.clear(); //※重要 この処理がないとpasswordがきえてしまう...
        for (OrganizationEntity entity : entities) {
            entity.setPassword(null);
            entity.setChildCount(this.countChild(entity.getOrganizationId(), userId));
        }
        return entities;
    }

    /**
     * 指定した組織の子組織情報の件数を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な組織情報を対象とする。
     *
     * @param id 組織ID
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return 子組織の件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("tree/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countTree(@QueryParam("id") Long id, @QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        logger.info("countTree: id={}, userId={}, authId={}", id, userId, authId);
        // 組織IDの指定がない場合はルートとする。
        if (Objects.isNull(id)) {
            id = 0L;
        }

        // 指定した組織の子組織の件数を取得する。
        Long count = this.countChild(id, userId);
        return String.valueOf(count);
    }

    /**
     * 指定した組織の子組織の件数を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な組織情報を対象とする。
     *
     * @param organizationId 組織ID
     * @param userId ユーザーID (組織ID)
     * @return 子組織の件数
     */
    @Lock(LockType.READ)
    private long countChild(Long organizationId, Long userId) {
        TypedQuery<Long> query;

        if (Objects.isNull(userId)) {
            // 指定した設備IDの子設備情報の件数を取得する。
            query = this.em.createNamedQuery("OrganizationEntity.countChild", Long.class);
        } else {
            // ユーザーIDが指定されている場合、アクセス可能な設備情報を対象とする。

            // ユーザーのルートまでの親組織ID一覧を取得する。
            List<Long> organizationIds = this.findAncestors(userId);

            // 指定した設備IDの子設備情報の件数を取得する。(指定ユーザーがアクセス可能な設備のみ)
            query = this.em.createNamedQuery("OrganizationEntity.countChildByUserId", Long.class);
            query.setParameter("ancestors", organizationIds);
        }
        query.setParameter("organizationId", organizationId);

        return query.getSingleResult();
    }

    /**
     * 組織IDを指定して、組織情報を取得する。(基本情報のみ)
     *
     * @param id 組織ID
     * @return 組織情報
     */
    @Lock(LockType.READ)
    public OrganizationEntity findBasicInfo(Long id) {
        OrganizationEntity organization = super.find(id);
        if (Objects.isNull(organization)) {
            return new OrganizationEntity();
        }

        return organization;
    }

    /**
     * 組織IDを指定して、組織情報を取得する。
     *
     * @param id 組織ID
     * @param authId 認証ID
     * @return 組織情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public OrganizationEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);
        OrganizationEntity organization = super.find(id);
        if (Objects.isNull(organization)) {
            return new OrganizationEntity();
        }

        // 休憩時間等の情報をセットする。
        organization = this.setInfo(organization);

        this.em.clear(); //※重要 この処理がないとpasswordがきえてしまう...
        organization.setPassword(null);

        return organization;
    }

    /**
     * 組織ID一覧を指定して、組織情報一覧を取得する。
     * ※.組織ID一覧の指定がない場合は全件取得。(削除済の組織も含む)
     * ※.組織ID一覧の指定がある場合、削除済の組織は対象外。
     *
     * @param ids 組織ID一覧
     * @param authId 認証ID
     * @return 組織情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<OrganizationEntity> find(@QueryParam("id") final List<Long> ids, @QueryParam("authId") Long authId) {
        logger.info("find: ids={}, authId={}", ids, authId);
        List<OrganizationEntity> organizations;
        try {
            if (Objects.isNull(ids) || ids.isEmpty()) {
                // 組織情報を全件取得する。
                organizations = super.findAll();
            } else {
                // 組織ID一覧を指定して、組織情報一覧を取得する。(削除済の設備は対象外)
                TypedQuery<OrganizationEntity> query = this.em.createNamedQuery("OrganizationEntity.findByIdsNotRemove", OrganizationEntity.class);
                query.setParameter("organizationIds", ids);

                organizations = query.getResultList();
            }

            // 休憩時間等の情報をセットする。
            for (OrganizationEntity organization : organizations) {
                organization = this.setInfo(organization);
            }

            this.em.clear(); //※重要 この処理がないとpasswordがきえてしまう...
            for (OrganizationEntity organization : organizations) {
                organization.setPassword(null);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
        return organizations;
    }

    /**
     * 組織情報に休憩時間等の情報をセットする。
     *
     * @param entity 組織情報
     * @return 組織情報
     */
    @Lock(LockType.READ)
    private OrganizationEntity setInfo(OrganizationEntity entity) {
        entity.setBreaktimeCollection(this.getBreaktimes(entity.getOrganizationId()));
        entity.setRoleCollection(this.getRoleEntity(entity.getOrganizationId(), entity.getAuthorityType()));
        entity.setWorkCategoryCollection(this.getWorkCategoryIds(entity.getOrganizationId()));

        entity.setInterruptCategoryCollection(new ArrayList<>());
        entity.setDelayCategoryCollection(new ArrayList<>());
        entity.setCallCategoryCollection(new ArrayList<>());
        
        List<ConOrganizationReasonEntity> reasons = this.getReason(entity.getOrganizationId());
        reasons.forEach(o -> {
            switch (o.getReasonType()) {
               case TYPE_INTERRUPT:
                    entity.getInterruptCategoryCollection().add(o.getReasonCategoryId());
                    break;
                case TYPE_DELAY:
                    entity.getDelayCategoryCollection().add(o.getReasonCategoryId());
                    break;
                case TYPE_CALL:
                    entity.getCallCategoryCollection().add(o.getReasonCategoryId());
                    break;
                default:
                    break;
            }
        });

        return entity;
    }

    /**
     * 指定した組織識別名の組織情報を取得する。
     * ※.削除済の組織は対象外。
     *
     * @param name 組織識別名
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return 組織情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public OrganizationEntity findByName(@QueryParam("name") String name, @QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        logger.info("findName: name={}, userId={}, authId={}", name, userId, authId);
        OrganizationEntity organization;

        // 組織識別名を指定して、組織情報を取得する。(削除済の設備は対象外)
        TypedQuery<OrganizationEntity> queryExist = em.createNamedQuery("OrganizationEntity.findByIdentNotRemove", OrganizationEntity.class);
        queryExist.setParameter("organizationIdentify", name);
        try {
            organization = queryExist.getSingleResult();
        } catch (NoResultException ex) {
            logger.fatal(ex);
            return new OrganizationEntity();
        }

        // 対象組織からルートまでの階層アクセス権をチェックする。
        boolean isAccessible = this.isHierarchyAccessible(organization.getOrganizationId(), userId);
        if (!isAccessible) {
            return new OrganizationEntity();
        }

        // 休憩時間等の情報をセットする。
        organization = this.setInfo(organization);

        this.em.clear(); //※重要 この処理がないとpasswordがきえてしまう...
        organization.setPassword(null);

        return organization;
    }

    /**
     * 組織情報一覧を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な組織情報を対象とする。
     *
     * @param userId ユーザーID (組織ID)
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 組織情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<OrganizationEntity> findRange(@QueryParam("user") Long userId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("childCount") Boolean childCount, @QueryParam("authId") Long authId) {
        logger.info("findRange: userId={}, from={}, to={}, authId={}", userId, from, to, authId);

        TypedQuery<OrganizationEntity> query;
        if(Objects.isNull(userId)) {
            // 組織情報をすべて取得する。 ※.削除済も対象
            query = this.em.createNamedQuery("OrganizationEntity.findAll", OrganizationEntity.class);
        } else {
            // ユーザーIDが指定されている場合、アクセス可能な設備情報を対象とする。

            // ユーザーのルートまでの親組織ID一覧を取得する。
            List<Long> organizationIds = this.findAncestors(userId);

            // 組織情報一覧を取得する。(指定ユーザーがアクセス可能な組織のみ) ※.削除済も対象
            query = this.em.createNamedQuery("OrganizationEntity.findByUserId", OrganizationEntity.class);
            query.setParameter("ancestors", organizationIds);

        }

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        List<OrganizationEntity> entities = query.getResultList();

        for (OrganizationEntity entity : entities) {
            entity = this.setInfo(entity);
        }

        this.em.clear(); //※重要 この処理がないとpasswordがきえてしまう...

        if (Objects.nonNull(childCount) && childCount) {
            for (OrganizationEntity entity : entities) {
                entity.setPassword(null);
                entity.setChildCount(this.countChild(entity.getOrganizationId(), userId));
            }
        } else {
            for (OrganizationEntity entity : entities) {
                entity.setPassword(null);
            }
        }
        return entities;
    }

    /**
     * 組織情報の件数を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な組織情報を対象とする。
     *
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return 組織情報の件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countAll(@QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        logger.info("countAll: userId={}, authId={}", userId, authId);

        long count;
        if(Objects.isNull(userId)) {
            count = super.count();
        } else {
            // ユーザーIDが指定されている場合、アクセス可能な組織情報を対象とする。

            // ユーザーのルートまでの親組織ID一覧を取得する。
            List<Long> organizationIds = this.findAncestors(userId);

            // 組織情報の件数を取得する。(指定ユーザーがアクセス可能な組織のみ)
            TypedQuery<Long> query = this.em.createNamedQuery("OrganizationEntity.countByUserId", Long.class);
            query.setParameter("ancestors", organizationIds);

            count = query.getSingleResult();
        }
        return String.valueOf(count);
    }

    /**
     * 指定した設備で作業可能な組織の組織情報一覧を取得する。
     *
     * @param equipmentId 設備ID
     * @param userId ユーザーID (組織ID)
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 組織情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("equipment/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<OrganizationEntity> findByEquipmentId(@QueryParam("id") Long equipmentId, @QueryParam("user") Long userId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findByEquipmentId: equipmentId={}, userId={}, from={}, to={}, authId={}", equipmentId, userId, from, to, authId);
        List<OrganizationEntity> entities = new LinkedList();

        // 指定した設備で作業可能な組織ID一覧を取得する。
        List<Long> organizaionIds = this.getAssignedOrganization(equipmentId);
        if (Objects.isNull(organizaionIds) || organizaionIds.isEmpty()) {
            return entities;
        }

        // 組織ID一覧を指定して、組織情報一覧を取得する。 ※.削除済も対象
        TypedQuery<OrganizationEntity> query = this.em.createNamedQuery("OrganizationEntity.findByIds", OrganizationEntity.class);
        query.setParameter("organizationIds", organizaionIds);

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        entities.addAll(query.getResultList());

        this.em.clear(); //※重要 この処理がないとpasswordがきえてしまう...
        for (OrganizationEntity entity : entities) {
            entity.setPassword(null);
            // adProductAppの作業者リストに親組織を非表示にするため、子の数をセット
            entity.setChildCount(this.countChild(entity.getOrganizationId(), userId));
        }
        return entities;
    }

    /**
     * 指定した設備で作業可能な組織の件数を取得する。
     *
     * @param equipmentId 設備ID
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("equipment/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countByEquipmentId(@QueryParam("id") Long equipmentId, @QueryParam("authId") Long authId) {
        logger.info("countByEquipmentId: equipmentId={}, authId={}", equipmentId, authId);

        // 指定した設備で作業可能な組織ID一覧を取得する。
        List<Long> organizaionIds = this.getAssignedOrganization(equipmentId);

        int count = 0;
        if (Objects.nonNull(organizaionIds)) {
            count = organizaionIds.size();
        }

        return String.valueOf(count);
    }

    /**
     * 指定した設備で作業可能な組織ID一覧を取得する。
     *
     * @param equipmentId 設備ID
     * @return 組織ID一覧
     */
    @Lock(LockType.READ)
    private List<Long> getAssignedOrganization(Long equipmentId) {
        // 指定した設備を含むルートから最下層までの設備ID一覧を取得する。
        List<Long> equipmentIds = this.equipmentRest.getEquipmentPerpetuity(equipmentId);

        // 指定した設備で作業可能な工程カンバンに割り当てられている組織ID一覧を取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("OrganizationEntity.findAssignedOrganization", Long.class);
        query.setParameter("workStatuses", Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND));
        query.setParameter("kanbanStatuses", Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND));
        query.setParameter("equipmentIds", equipmentIds);

        List<Long> organizaionIdsByKanban = query.getResultList();
        if (Objects.isNull(organizaionIdsByKanban) || organizaionIdsByKanban.isEmpty()) {
            return new LinkedList();
        }

        Set<Long> organizaionIdsSet = new TreeSet();
        for (Long id : organizaionIdsByKanban) {
            organizaionIdsSet.addAll(getOrganizationChildren(id));
        }

        return new LinkedList(organizaionIdsSet);
    }

    /**
     * 工程カンバンの検索条件(工程カンバンID, ステータス, 計画日時)で組織情報一覧を取得する。
     *
     * @param condition カンバン検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 組織情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<OrganizationEntity> searchOrganization(KanbanSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchOrganization: {}, from={}, to={}", condition, from, to);

        // 工程カンバンの検索条件(工程カンバンID,ステータス, 計画日時)で組織情報を検索するクエリを取得する。
        Query query = this.getKanbanSearchQuery(SearchType.SEARCH, condition);

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        List<OrganizationEntity> entities = query.getResultList();
        for (OrganizationEntity entity : entities) {
            entity = this.setInfo(entity);
        }

        this.em.clear(); //※重要 この処理がないとpasswordがきえてしまう...
        for (OrganizationEntity entity : entities) {
            entity.setPassword(null);
            // adProductAppの作業者リストに親組織を非表示にするため、子の数をセット
            if (Objects.nonNull(condition.getIncludeChildOrganizaitonFlag()) && condition.getIncludeChildOrganizaitonFlag()) {
                entity.setChildCount(this.countChild(entity.getOrganizationId(), condition.getLoginUserId()));
            }
        }
        return entities;
    }

    /**
     * 工程カンバンの検索条件(ステータス, 計画日時)で組織情報の件数を取得する。
     *
     * @param condition カンバン検索条件
     * @param authId 認証ID
     * @return 組織情報
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countOrganization(KanbanSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countOrganization: {}", condition);
        Query query = this.getKanbanSearchQuery(SearchType.COUNT, condition);
        return String.valueOf(query.getSingleResult());
    }

    /**
     * 工程カンバンの検索条件(工程カンバンID, ステータス, 計画日時)で組織情報を検索するクエリを取得する。
     *
     * @param type 検索種別
     * @param condition 工程カンバンの検索条件 (工程カンバンID, ステータス, 計画日時)
     * @return 検索クエリ
     */
    @Lock(LockType.READ)
    private Query getKanbanSearchQuery(SearchType type, KanbanSearchCondition condition) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        // 工程カンバン情報
        Root<WorkKanbanEntity> subWKanban = cq.from(WorkKanbanEntity.class);
        jakarta.persistence.criteria.Path<Long> pathWKanbanId = subWKanban.get(WorkKanbanEntity_.workKanbanId);
        jakarta.persistence.criteria.Path<KanbanStatusEnum> pathWKanbanStatus = subWKanban.get(WorkKanbanEntity_.workStatus);
        jakarta.persistence.criteria.Path<Date> pathWKanbanStartDt = subWKanban.get(WorkKanbanEntity_.startDatetime);
        jakarta.persistence.criteria.Path<Date> pathWKanbanCompDt = subWKanban.get(WorkKanbanEntity_.compDatetime);

        // 工程カンバンの検索条件
        List<Predicate> whereWKanban = new LinkedList();

        // 工程カンバンID
        if (Objects.nonNull(condition.getWorkKanbanCollection())) {
            whereWKanban.add(pathWKanbanId.in(condition.getWorkKanbanCollection()));
        }
        // 工程ステータス
        if (Objects.nonNull(condition.getKanbanStatusCollection())) {
            whereWKanban.add(pathWKanbanStatus.in(condition.getKanbanStatusCollection()));
        }
        // 計画日時
        if (Objects.nonNull(condition.getFromDate())) {
            whereWKanban.add(cb.or(
                    cb.greaterThanOrEqualTo(pathWKanbanStartDt, condition.getFromDate()),
                    cb.greaterThanOrEqualTo(pathWKanbanCompDt, condition.getFromDate())
            ));
        }
        if (Objects.nonNull(condition.getToDate())) {
            whereWKanban.add(cb.or(
                    cb.lessThanOrEqualTo(pathWKanbanStartDt, condition.getToDate()),
                    cb.lessThanOrEqualTo(pathWKanbanCompDt, condition.getToDate())
            ));
        }

        Subquery<Long> workKanbanSubquery = cq.subquery(Long.class);
        workKanbanSubquery.select(pathWKanbanId)
                .where(cb.and(whereWKanban.toArray(new Predicate[whereWKanban.size()])));

        // 工程カンバン・組織関連付け情報
        Root<ConWorkkanbanOrganizationEntity> subCWKanban = cq.from(ConWorkkanbanOrganizationEntity.class);
        jakarta.persistence.criteria.Path<Long> pathCKanbanOrgId = subCWKanban.get(ConWorkkanbanOrganizationEntity_.organizationId);
        jakarta.persistence.criteria.Path<Long> pathCKanbanWKanbanId = subCWKanban.get(ConWorkkanbanOrganizationEntity_.workKanbanId);

        // 
        Subquery<Long> conKanbanSubquery = cq.subquery(Long.class);
        conKanbanSubquery.select(pathCKanbanOrgId)
                .where(pathCKanbanWKanbanId.in(workKanbanSubquery));
        
        // 組織情報
        Root<OrganizationEntity> poolOrganization = cq.from(OrganizationEntity.class);
        jakarta.persistence.criteria.Path<Long> pathOrganizationId = poolOrganization.get(OrganizationEntity_.organizationId);
        
        // ID一覧に変換
        cq.select(pathOrganizationId)
                .where(pathOrganizationId.in(conKanbanSubquery));
        List<Long> ids = this.em.createQuery(cq).getResultList();
        // ID一覧から子組織追加
        Set<Long> organizationIdsSet = new TreeSet();
        if (Objects.nonNull(condition.getIncludeChildOrganizaitonFlag()) && condition.getIncludeChildOrganizaitonFlag()) {
            for (Long id : ids) {
                organizationIdsSet.addAll(getOrganizationChildren(id));
            }
        } else {
            organizationIdsSet.addAll(ids);
        }

        // 組織情報取得
        if (SearchType.COUNT.equals(type)) {
            cq.select(cb.count(pathOrganizationId))
                    .where(pathOrganizationId.in(organizationIdsSet));
        } else {
            cq.select(poolOrganization)
                    .where(pathOrganizationId.in(organizationIdsSet))
                    .orderBy(cb.asc(pathOrganizationId));
        }

        return this.em.createQuery(cq);
    }

    /**
     * 組織情報を登録する。
     *
     * @param entity 組織情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(OrganizationEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            // 組織識別名の重複を確認する。(削除済も含む)
            TypedQuery<Long> query = this.em.createNamedQuery("OrganizationEntity.checkAddByIdent", Long.class);
            query.setParameter("organizationIdentify", entity.getOrganizationIdentify());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 言語
            if (Objects.nonNull(entity.getLocaleFileInfos())) {
                List<LocaleFileEntity> langIds = new ArrayList();
                for (LocaleFileEntity locale : entity.getLocaleFileInfos()) {
                    if (!locale.getIsUpdate() || Objects.isNull(locale.resource())) {
                        continue;
                    }
                    ResourceEntity resourceEnt = locale.resource();

                    if (Objects.isNull(resourceEnt.getResourceId()) && Objects.nonNull(resourceEnt.getResourceString())) {
                        // add
                        ResponseEntity res = (ResponseEntity)this.resourceRest.add(resourceEnt).getEntity();
                        resourceEnt.setResourceId(res.getUriId());
                        langIds.add(locale);
                    }
                }
                // 言語カラムの言語ファイル情報(JSON)を更新
                entity.setLangIds(JsonUtils.objectsToJson(langIds));
                entity.setLocaleFileInfos(null);
            }

            // 組織情報を登録する。
            entity.setRemoveFlag(false);
            super.create(entity);
            this.em.flush();

            // 組織・休憩関連付け情報を追加する。
            this.addBreaktime(entity);
            // 組織・役割関連付け情報を追加する。
            this.addRoleEntity(entity);
            // 組織・作業区分関連付け情報を追加する。
            this.addWorkCategories(entity);
            // 組織・理由区分関連付けを追加する。
            this.addReason(entity);

            // 設備の変更を通知する
            DeviceConnectionServiceCommand command = new DeviceConnectionServiceCommand(DeviceConnectionServiceCommand.COMMAND.UPDATE_ORGANIZATION);
            this.adIntefaceFacade.noticeDeviceConnectionService(command);

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("organization/").append(entity.getOrganizationId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 組織情報をコピーする。
     *
     * @param id 組織ID
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

        OrganizationEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
        }

        boolean isFind = true;
        StringBuilder name = new StringBuilder(entity.getOrganizationIdentify())
                .append(SUFFIX_COPY);
        while (isFind) {
            // 組織識別名の重複を確認する。(削除済も含む)
            TypedQuery<Long> checkQuery = em.createNamedQuery("OrganizationEntity.checkAddByIdent", Long.class);
            checkQuery.setParameter("organizationIdentify", name.toString());
            if (checkQuery.getSingleResult() > 0) {
                name.append(SUFFIX_COPY);
                continue;
            }
            isFind = false;
        }

        OrganizationEntity newEntity = new OrganizationEntity(entity);
        newEntity.setPassword(new PasswordEncoder().encode(""));
        newEntity.setOrganizationIdentify(name.toString());
        newEntity.setBreaktimeCollection(entity.getBreaktimeCollection());
        newEntity.setRoleCollection(entity.getRoleCollection());
        newEntity.setWorkCategoryCollection(entity.getWorkCategoryCollection());

        // 新規追加する。
        return this.add(newEntity, authId);
    }

    /**
     * 組織情報を更新する。
     *
     * @param entity 組織情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(OrganizationEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 排他用バージョンを確認する。
            OrganizationEntity target = super.find(entity.getOrganizationId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 自身は親階層に指定できない。
            if (Objects.nonNull(entity.getParentOrganizationId()) && Objects.equals(entity.getOrganizationId(), entity.getParentOrganizationId())) {
                // 移動不可能な階層
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)).build();
            }

            // 組織識別名の重複を確認する。
            TypedQuery<Long> query = this.em.createNamedQuery("OrganizationEntity.checkUpdateByIdent", Long.class);
            query.setParameter("organizationId", entity.getOrganizationId());
            query.setParameter("organizationIdentify", entity.getOrganizationIdentify());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 自身の子以降の階層への移動にならないか確認する。
            if (!this.isMoveableHierarchy(entity.getOrganizationId(), entity.getParentOrganizationId())) {
                // 移動不可能な階層
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)).build();
            }

            // パスワード更新の確認
            PasswordEncoder encoder = new PasswordEncoder();
            if (Objects.nonNull(entity.getPassword()) && entity.getPassword().isEmpty()) {
                // パスワードが空の場合、パスワードをリセットする。
                entity.setPassword(encoder.encode(""));
            } else if (entity.getPassword() == null ? entity.getPassword() == null : entity.getPassword().equals(encoder.encode(""))) {
                // パスワードがnullの場合、元のパスワードをセットする。(パスワード変更なし)
                entity.setPassword(super.find(entity.getOrganizationId()).getPassword());
            }

            // 言語
            if (Objects.nonNull(entity.getLocaleFileInfos())) {
                List<LocaleFileEntity> langIds = new ArrayList();
                for (LocaleFileEntity locale : entity.getLocaleFileInfos()) {
                    if (Objects.isNull(locale.resource())) {
                        continue;
                    }
                    if (!locale.getIsUpdate()) {
                        if (Objects.nonNull(locale.resource().getResourceId())) {
                           langIds.add(locale);
                        }
                        continue;
                    }
                    ResourceEntity resourceEnt = locale.resource();
                    
                    if (Objects.isNull(resourceEnt.getResourceString())) {
                        // delete
                        this.resourceRest.remove(resourceEnt.getResourceId());
                    } else if (Objects.isNull(resourceEnt.getResourceId())) {
                        // add
                        ResponseEntity res = (ResponseEntity)this.resourceRest.add(resourceEnt).getEntity();
                        resourceEnt.setResourceId(res.getUriId());
                        langIds.add(locale);
                    } else {
                        // update
                        this.resourceRest.update(resourceEnt, authId);
                        langIds.add(locale);
                    }
                }
                // 言語カラムの言語ファイル情報(JSON)を更新
                entity.setLangIds(JsonUtils.objectsToJson(langIds));
                entity.setLocaleFileInfos(null);
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 組織情報を更新する。
            super.edit(entity);

            // 組織・休憩関連付け情報を削除して追加する。
            this.removeBreaktime(entity.getOrganizationId());
            this.addBreaktime(entity);
            // 組織・役割関連付け情報を削除して追加する。
            this.removeRoleEntity(entity.getOrganizationId());
            this.addRoleEntity(entity);
            // 組織・作業区分関連付け情報を削除して追加する。
            this.removeWorkCategoryIds(entity.getOrganizationId());
            this.addWorkCategories(entity);
            // 組織・理由区分関連付けを削除・追加する。
            this.removeReason(entity.getOrganizationId());
            this.addReason(entity);


            DeviceConnectionServiceCommand command = new DeviceConnectionServiceCommand(DeviceConnectionServiceCommand.COMMAND.UPDATE_ORGANIZATION);
            this.adIntefaceFacade.noticeDeviceConnectionService(command);

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定した組織IDの組織情報を削除する。
     *
     * @param id 組織ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("remove: id={}, authId={}", id, authId);

        // 指定された組織に、子組織がある場合は削除できない。
        if (this.countChild(id, null) > 0) {
            logger.info("not remove at exist child:{}", id);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_HIERARCHY_DELETE)).build();
        }

        // 階層アクセス権情報を削除する。
        this.authRest.remove(AccessHierarchyTypeEnum.OrganizationHierarchy, id);

        // 組織IDを指定して、組織情報を取得する。
        OrganizationEntity entity = this.find(id, authId);

        // SystemAdminは削除できない。
        if (AuthorityEnum.SYSTEM_ADMIN.equals(entity.getAuthorityType())) {
            logger.info("not remove system admin:{}", entity);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOT_DELETE_SYSTEM_ADMIN)).build();
        }

        // 組織IDを指定して、工程・組織関連付け情報を削除する。
        Query queryWorkFlow = this.em.createNamedQuery("ConWorkOrganizationEntity.removeByOrganizationId");
        queryWorkFlow.setParameter("organizationId", id);
        int numWorkFlow = queryWorkFlow.executeUpdate();// 削除した件数が返る。

        // 組織IDを指定して、工程カンバン・組織関連付け情報の件数を取得する。
        TypedQuery<Long> queryKanban = this.em.createNamedQuery("ConWorkkanbanOrganizationEntity.countByOrganizationId", Long.class);
        queryKanban.setParameter("organizationId", id);
        Long numKanban = queryKanban.getSingleResult();

        // 関連付けが無い場合、完全に削除する。
        if (numWorkFlow == 0 && numKanban == 0) {
            logger.info("Physically remove: {}", id);

            // 言語ファイルを削除する
            if (Objects.nonNull(entity.getLangIds())) {
                List<LocaleFileEntity> localeInfos = JsonUtils.jsonToObjects(entity.getLangIds(), LocaleFileEntity[].class);
                for (LocaleFileEntity locale : localeInfos) {
                    ResourceEntity resourceEnt = locale.resource();
                    if (Objects.nonNull(resourceEnt)) {
                        // delete
                        this.resourceRest.remove(resourceEnt.getResourceId());
                    }
                }
            }

            // 組織・休憩関連付け情報を削除する。
            this.removeBreaktime(id);
            // 組織・役割関連付け情報を削除する。
            this.removeRoleEntity(id);
            // 組織・作業区分関連付け情報を削除する。
            this.removeWorkCategoryIds(id);
            // 組織・理由区分関連付けをする。
            this.removeReason(id);
            // 組織情報を削除する。
            super.remove(super.find(id));

            // 設備の変更を通知する
            DeviceConnectionServiceCommand command = new DeviceConnectionServiceCommand(DeviceConnectionServiceCommand.COMMAND.UPDATE_ORGANIZATION);
            this.adIntefaceFacade.noticeDeviceConnectionService(command);

            return Response.ok().entity(ResponseEntity.success()).build();
        }

        // 関連付けがある場合、削除フラグで論理削除する。

        if (Objects.isNull(entity.getOrganizationId())) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }

        // 削除済の名称に変更する。
        boolean isFind = true;
        int num = 1;
        String baseName = new StringBuilder(entity.getOrganizationIdentify())
                .append(SUFFIX_REMOVE)
                .toString();
        String name = new StringBuilder(baseName)
                .append(num)
                .toString();
        while (isFind) {
            // 設備識別名の重複を確認する。
            TypedQuery<Long> checkQuery = em.createNamedQuery("OrganizationEntity.checkUpdateByIdent", Long.class);
            checkQuery.setParameter("organizationId", id);
            checkQuery.setParameter("organizationIdentify", name);
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

        entity.setParentOrganizationId(null);// 階層情報を削除する。
        entity.setOrganizationIdentify(name);
        entity.setRemoveFlag(true);
        
        // 設備の変更を通知する
        DeviceConnectionServiceCommand command = new DeviceConnectionServiceCommand(DeviceConnectionServiceCommand.COMMAND.UPDATE_ORGANIZATION);
        this.adIntefaceFacade.noticeDeviceConnectionService(command);

        // 組織情報を更新する。
        super.edit(entity);
        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * 組織のログイン処理を行ない、ログイン結果・組織情報を取得する。
     *
     * @param request 組織ログイン要求情報
     * @param withAuth 
     * @param authId 認証ID
     * @return ログイン結果
     * @throws URISyntaxException 
     */
    @PUT
    @Path("login")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public OrganizationLoginResult login(OrganizationLoginRequest request, @QueryParam("withAuth") Boolean withAuth, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("login: {}, authId={}", request, authId);

        // adminユーザーの存在を確認して、存在しない場合は作成する。
        this.checkExsistAdmin();

        if (Objects.isNull(request.getLoginType())) {
            return OrganizationLoginResult.failed(ServerErrorTypeEnum.NOT_AUTH_ORGANIZATION);
        }

        switch (request.getLoginType()) {
            case PASSWORD:
                // パスワードで組織ログインする。
                return this.loginPassword(request, withAuth);
            case BARCODE:
                // バーコードで組織ログインする。
                return this.loginBarcode(request, withAuth);
            case LDAP:
                // LDAP認証で組織ログインする。
                return this.loginLdap(request, withAuth);
        }

        return OrganizationLoginResult.failed(ServerErrorTypeEnum.NOT_AUTH_ORGANIZATION);
    }

    /**
     * 指定されたユーザーIDを子組織IDとする組織を文字列で取得する。
     *
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return 文字列
     */
    @Lock(LockType.READ)
    @GET
    @Path("tree/ancestors")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String findAncestorsString(@QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        // ユーザーのルートまでの親組織ID一覧を取得する。
        List<Long> parentIdsOfLoginUser = this.findAncestors(userId);
        String result = parentIdsOfLoginUser.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return result;
    }

    /**
     * 
     * @param userId ユーザーID (組織ID)
     * @return 
     */
    @Lock(LockType.READ)
    public List<Long> findAncestors(Long userId) {
        if (userId == 0L) {
            return Arrays.asList(userId);
        }

        List<Long> parentIdsOfLoginUser = new ArrayList();

        if (Objects.isNull(userId)) {
            return parentIdsOfLoginUser;
        }

        // 組織IDを指定して、親組織IDを取得する。 ※.削除済も対象
        TypedQuery<Long> query = this.em.createNamedQuery("OrganizationEntity.findParentId", Long.class);

        Long organizationId = userId;
        while (organizationId != 0L) {
            try {
                query.setParameter("organizationId", organizationId);

                Long parentId = query.getSingleResult();

                parentIdsOfLoginUser.add(organizationId);
                organizationId = parentId;
            } catch (Exception ex) {
                organizationId = 0L;
            }
        }

        return parentIdsOfLoginUser;
    }

    /**
     * ADMINユーザーの存在を確認して、存在しない場合は作成する。
     *
     * @return 組織ID
     * @throws URISyntaxException 
     */
    @Lock(LockType.READ)
    public long checkExsistAdmin() throws URISyntaxException {
        OrganizationEntity organization = this.findByName(ADMIN_USER, null, null);
        if (Objects.isNull(organization) || Objects.isNull(organization.getOrganizationId())) {
            logger.info("create ADMIN user.");
            PasswordEncoder encoder = new PasswordEncoder();
            OrganizationEntity admin = new OrganizationEntity(0L, ADMIN_USER, ADMIN_USER, AuthorityEnum.SYSTEM_ADMIN, null, encoder.encode(ADMIN_USER), "", null, null);
            this.add(admin, null);
            organization = this.findByName(ADMIN_USER, null, null);
        }
        return organization.getOrganizationId();
    }

    /**
     * パスワードで組織ログインする。
     *
     * @param request 組織ログイン要求情報
     * @param withAuth 
     * @return 組織ログイン結果
     */
    @Lock(LockType.READ)
    private OrganizationLoginResult loginPassword(OrganizationLoginRequest request, Boolean withAuth) {
        // 組織識別名を指定して、組織情報を取得する。(削除済の設備は対象外)
        TypedQuery<OrganizationEntity> query = this.em.createNamedQuery("OrganizationEntity.findByIdentNotRemove", OrganizationEntity.class);
        query.setParameter("organizationIdentify", request.getLoginId());

        try {
            OrganizationEntity organization = query.getSingleResult();

            // パスワードを確認する。
            if (!organization.getPassword().equals(request.getAuthData())) {
                return OrganizationLoginResult.failed(ServerErrorTypeEnum.NOT_AUTH_ORGANIZATION);
            }

            organization.setBreaktimeCollection(getBreaktimes(organization.getOrganizationId()));
            organization.setRoleCollection(getRoleEntity(organization.getOrganizationId(), organization.getAuthorityType()));
            organization.setWorkCategoryCollection(getWorkCategoryIds(organization.getOrganizationId()));

            List<String> roleAuthes = null;
            if (Objects.nonNull(withAuth) && withAuth) {
                // TODO: 役割マスタが変更になったことによるエラー
//                roleAuthes = organization.getRoleCollection().stream()
//                        .flatMap(id -> this.roleRest.find(id).getRoleAuthorityEntities().stream())
//                        .filter(RoleAuthorityEntity::getAuthorityEnable)
//                        .map(RoleAuthorityEntity::getAuthorityType)
//                        .collect(Collectors.toList());
                roleAuthes = this.getRoleAuthorities(organization.getRoleCollection());
            }

            // 間接作業を設定 (対象組織に設定されていなければ上位階層から取得)
            List<Long> ids = this.getWorkCategoriesFromParent(organization);
            if (Objects.nonNull(ids) && !ids.isEmpty()) {
                List<IndirectWorkEntity> indirectWorks = this.indirectWorkRest.findCategory(ids, null);
                organization.setIndirectWorks(indirectWorks);
            } else {
                organization.setIndirectWorks(new ArrayList<>());
            }

            // 言語ファイル設定(対象組織に設定されていなければ上位階層から取得)
            organization.setLocaleFileInfos(this.createLocaleFileInfos(organization));

            return OrganizationLoginResult.success(organization.getOrganizationId(), roleAuthes).organizationInfo(createOrganizationInfo(organization));
        } catch (NoResultException ex) {
            return OrganizationLoginResult.failed(ServerErrorTypeEnum.NOT_LOGINID_ORGANIZATION);
        }
    }

    /**
     * バーコードで組織ログインする。
     *
     * @param request 組織ログイン要求情報
     * @param withAuth 
     * @return 組織ログイン結果
     */
    @Lock(LockType.READ)
    private OrganizationLoginResult loginBarcode(OrganizationLoginRequest request, Boolean withAuth) {
        try {
            TypedQuery<OrganizationEntity> query = this.em.createNamedQuery("OrganizationEntity.findByIdentNotRemove", OrganizationEntity.class);
            query.setParameter("organizationIdentify", request.getAuthData());
            OrganizationEntity organization = query.getSingleResult();
            
            this.setInfo(organization);
            
            List<String> roleAuthes = null;
            if (Objects.nonNull(withAuth) && withAuth) {
                roleAuthes = this.getRoleAuthorities(organization.getRoleCollection());
            }

            // 間接作業を設定 (対象組織に設定されていなければ上位階層から取得)
            List<Long> ids = this.getWorkCategoriesFromParent(organization);
            if (Objects.nonNull(ids) && !ids.isEmpty()) {
                List<IndirectWorkEntity> indirectWorks = this.indirectWorkRest.findCategory(ids, null);
                organization.setIndirectWorks(indirectWorks);
            } else {
                organization.setIndirectWorks(new ArrayList<>());
            }
            
            // 言語ファイル設定 (対象組織に設定されていなければ上位階層から取得)
            organization.setLocaleFileInfos(this.createLocaleFileInfos(organization));

            return OrganizationLoginResult.success(organization.getOrganizationId(), roleAuthes).organizationInfo(createOrganizationInfo(organization));
        } catch (NoResultException ex) {
            return OrganizationLoginResult.failed(ServerErrorTypeEnum.NOT_LOGINID_ORGANIZATION);
        }
    }
    
    /**
     * 親組織の作業区分IDを取得する.
     * 
     * @param organization
     * @return 
     */
    @Lock(LockType.READ)
    private List<Long> getWorkCategoriesFromParent(OrganizationEntity organization) {
        if (Objects.isNull(organization.getWorkCategoryCollection()) || organization.getWorkCategoryCollection().isEmpty()) {
            if (Objects.nonNull(organization.getParentOrganizationId()) && organization.getParentOrganizationId() > 0L) {
                OrganizationEntity parent = super.find(organization.getParentOrganizationId());
                if (Objects.isNull(parent)) {
                    return null;
                }
                parent.setWorkCategoryCollection(this.getWorkCategoryIds(parent.getOrganizationId()));
                return this.getWorkCategoriesFromParent(parent);
            }
        }
        return organization.getWorkCategoryCollection();
    }
    
    /**
     * LDAP認証を行い組織ログインする。
     *
     * @param request 組織ログイン要求情報
     * @param withAuth  trueの場合機能権限も一緒に取得する
     * @return 組織ログイン結果
     */
    @Lock(LockType.READ)
    private OrganizationLoginResult loginLdap(OrganizationLoginRequest request, Boolean withAuth) {
        logger.info("loginLdap: start");
        // パスワードが空の場合
        if (Objects.isNull(request.getAuthData()) || request.getAuthData().isEmpty() ) {
            logger.info("loginLdap: password is null or empty");
            return OrganizationLoginResult.failed(ServerErrorTypeEnum.NOT_AUTH_ORGANIZATION);
        }
        
        PasswordEncoder encoder = new PasswordEncoder();
        String password = encoder.decodeAES(request.getAuthData());
        
        // 管理ユーザの場合は、LDAP認証をしない。
        if(request.getLoginId().equals(ADMIN_USER)){
            request.setAuthData(encoder.encode(password));
            return loginPassword(request, withAuth);
        }
        
        // 組織識別名を指定して、組織情報を取得する。(削除済の設備は対象外)
        TypedQuery<OrganizationEntity> query = this.em.createNamedQuery("OrganizationEntity.findByIdentNotRemove", OrganizationEntity.class);
        query.setParameter("organizationIdentify", request.getLoginId());

        try {
            OrganizationEntity organization = query.getSingleResult();

            // LDAP接続情報
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            props.put(Context.PROVIDER_URL, this.ldapProviderURL); // 接続先 ldap://～ or ldaps://～
            props.put(Context.REFERRAL, "ignore");
            props.put(Context.SECURITY_AUTHENTICATION, "simple");
            props.put(Context.SECURITY_PRINCIPAL, request.getLoginId() + "@" + this.ldapDomain); // ユーザーID＠ドメイン名
            props.put(Context.SECURITY_CREDENTIALS, password); // パスワード

            // パスワードが空だと、ユーザーの有無にかかわらず、LDAP認証を通過してしまう
            if (StringUtils.isEmpty(password)) {
                // パスワードが空。
                logger.warn("loginLdap: Password is empty.");
                return OrganizationLoginResult.failed(ServerErrorTypeEnum.NOT_AUTH_ORGANIZATION);
            }

            // LDAPS(SSL)の場合
            if (this.ldapProviderURL.startsWith("ldaps")) {
                props.put("java.naming.ldap.factory.socket", "jp.adtekfuji.testldap.SimpleSSLSocketFactory");
            }

            try {
                // LDAP認証を行う。
                DirContext context = new InitialDirContext(props);
                // LDAP認証が成功したので後片付け
                context.close();
            } catch (AuthenticationException ae) {
                // 認証エラー
                logger.warn("loginLdap: AuthenticationException");
                return OrganizationLoginResult.failed(ServerErrorTypeEnum.NOT_AUTH_ORGANIZATION);
            } catch (Exception e) {
                // その他のエラー
                logger.fatal("loginLdap: Exception");
                return OrganizationLoginResult.failed(ServerErrorTypeEnum.LOGIN_LDAP_EXCEPTION);
            }

            organization.setBreaktimeCollection(getBreaktimes(organization.getOrganizationId()));
            organization.setRoleCollection(getRoleEntity(organization.getOrganizationId(), organization.getAuthorityType()));
            organization.setWorkCategoryCollection(getWorkCategoryIds(organization.getOrganizationId()));

            List<String> roleAuthes = null;
            if (Objects.nonNull(withAuth) && withAuth) {
                roleAuthes = this.getRoleAuthorities(organization.getRoleCollection());
            }

            // 言語ファイル設定(対象組織に設定されていなければ上位階層から取得)
            organization.setLocaleFileInfos(this.createLocaleFileInfos(organization));

            logger.info("loginLdap: success");
            return OrganizationLoginResult.success(organization.getOrganizationId(), roleAuthes).organizationInfo(createOrganizationInfo(organization));
        } catch (NoResultException ex) {
            logger.info("loginLdap: NoResultException");
            return OrganizationLoginResult.failed(ServerErrorTypeEnum.NOT_LOGINID_ORGANIZATION);
        }
    }

    /**
     * 組織情報を生成する。
     * 
     * @param entity
     * @return 
     */
    @Lock(LockType.READ)
    private OrganizationInfoEntity createOrganizationInfo(OrganizationEntity entity) {
        OrganizationInfoEntity info = new OrganizationInfoEntity(entity.getOrganizationId(), entity.getOrganizationIdentify(), entity.getOrganizationName(), entity.getAuthorityType());
        info.setParentId(entity.getParentOrganizationId());
        info.setBreakTimeInfoCollection(entity.getBreaktimeCollection());
        info.setRoleCollection(entity.getRoleCollection());
        info.setWorkCategoryCollection(entity.getWorkCategoryCollection());
        if (Objects.nonNull(entity.getLocaleFileInfos()) && !entity.getLocaleFileInfos().isEmpty()) {
            info.setLocaleFileInfoCollection(new LinkedList<LocaleFileInfoEntity>());
            for (LocaleFileEntity localeInfo : entity.getLocaleFileInfos()) {
                info.getLocaleFileInfoCollection().add(localeInfo.cast());
            }
        }
        // 間接作業一覧
        if (Objects.nonNull(entity.getIndirectWorks()) && !entity.getIndirectWorks().isEmpty()) {
            info.setIndirectWorkCollection(new LinkedList<IndirectWorkInfoEntity>());
            for (IndirectWorkEntity indirectWork : entity.getIndirectWorks()) {
                info.getIndirectWorkCollection().add(new IndirectWorkInfoEntity(indirectWork.getIndirectWorkId(), indirectWork.getWorkNumber(), indirectWork.getWorkName()));
            }
        }
        return info;
    }

    /**
     * 指定した組織の子組織情報一覧を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な組織情報を対象とする。
     *
     * @param id 組織ID
     * @param userId ユーザーID (組織ID)
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 子組織情報一覧
     */
    @Lock(LockType.READ)
    private List<OrganizationEntity> findChild(Long id, Long userId, Integer from, Integer to) {
        TypedQuery<OrganizationEntity> query;
        if(Objects.isNull(userId)) {
            // 指定した組織IDの子組織情報一覧を取得する。
            query = this.em.createNamedQuery("OrganizationEntity.findChild", OrganizationEntity.class);
        } else {
            // ユーザーIDが指定されている場合、アクセス可能な組織情報を対象とする。

            // ユーザーのルートまでの親組織ID一覧を取得する。
            List<Long> organizationIds = this.findAncestors(userId);

            // 指定した組織IDの子組織情報一覧を取得する。(指定ユーザーがアクセス可能な組織のみ)
            query = this.em.createNamedQuery("OrganizationEntity.findChildByUserId", OrganizationEntity.class);
            query.setParameter("ancestors", organizationIds);
        }
        query.setParameter("organizationId", id);

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }
        return query.getResultList();
    }

    @Lock(LockType.READ)
    public List<ConOrganizationBreaktimeEntity> getBreakTimesAll(){
        // 組織IDを指定して、休憩ID一覧を取得する。
        TypedQuery<ConOrganizationBreaktimeEntity> query = this.em.createNamedQuery("ConOrganizationBreaktimeEntity.findAll", ConOrganizationBreaktimeEntity.class);
        return query.getResultList();
    }


    /**
     * 組織IDを指定して、休憩ID一覧を取得する。
     *
     * @param organizationId 組織ID
     * @return 休憩ID一覧
     */
    @Lock(LockType.READ)
    public List<Long> getBreaktimes(Long organizationId) {
        // 組織IDを指定して、休憩ID一覧を取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("ConOrganizationBreaktimeEntity.findBreaktimeId", Long.class);
        query.setParameter("organizationId", organizationId);
        return query.getResultList();
    }

    /**
     * 組織ID一覧を指定して、休憩ID一覧を取得する。
     *
     * @param organizationIds 組織ID一覧
     * @return 休憩ID一覧
     */
    @Lock(LockType.READ)
    public List<Long> getBreaktimes(List<Long> organizationIds) {
        // 組織ID一覧を指定して、休憩ID一覧を取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("ConOrganizationBreaktimeEntity.findBreaktimeIdByOrganizationIds", Long.class);
        query.setParameter("organizationIds", organizationIds);
        return query.getResultList();
    }

    /**
     * 組織IDを指定して、組織・休憩関連付け情報を削除する。
     *
     * @param organizationId 組織ID
     */
    private void removeBreaktime(Long organizationId) {
        // 組織IDを指定して、組織・休憩関連付け情報を削除する。
        Query query = this.em.createNamedQuery("ConOrganizationBreaktimeEntity.removeByOrganizationId");
        query.setParameter("organizationId", organizationId);
        query.executeUpdate();
    }

    /**
     * 組織・休憩関連付け情報を追加する。
     *
     * @param entity 組織情報
     */
    private void addBreaktime(OrganizationEntity entity) {
        if (Objects.nonNull(entity.getBreaktimeCollection())) {
            for (Long id : entity.getBreaktimeCollection()) {
                ConOrganizationBreaktimeEntity con = new ConOrganizationBreaktimeEntity(entity.getOrganizationId(), id);
                this.em.persist(con);
            }
        }
    }

    /**
     * 
     * @param organizationId
     * @param oldAuthType
     * @return 
     */
    private List<Long> getRoleEntity(Long organizationId, AuthorityEnum oldAuthType) {
        // 組織IDを指定して、役割ID一覧を取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("ConOrganizationRoleEntity.findRoleId", Long.class);
        query.setParameter("organizationId", organizationId);
        List<Long> roleIds = query.getResultList();
        if (roleIds.isEmpty()) {
            if (oldAuthType == AuthorityEnum.SYSTEM_ADMIN || oldAuthType == AuthorityEnum.ADMINISTRATOR) {
                Long roleId = this.roleRest.getDefualtRole().getRoleId();
                ConOrganizationRoleEntity con = new ConOrganizationRoleEntity(organizationId, roleId);
                this.em.persist(con);
                roleIds.add(roleId);
            }
        }
        return roleIds;
    }

    /**
     * 組織IDを指定して、組織・役割関連付け情報を削除する。
     *
     * @param organizationId 組織ID
     */
    private void removeRoleEntity(Long organizationId) {
        // 組織IDを指定して、組織・役割関連付け情報を削除する。
        Query query = this.em.createNamedQuery("ConOrganizationRoleEntity.removeByOrganizationId");
        query.setParameter("organizationId", organizationId);
        query.executeUpdate();
    }

    /**
     * 組織・役割関連付け情報を追加する。
     *
     * @param entity 組織情報
     */
    private void addRoleEntity(OrganizationEntity entity) {
        if (Objects.nonNull(entity.getRoleCollection())) {
            for (Long id : entity.getRoleCollection()) {
                ConOrganizationRoleEntity con = new ConOrganizationRoleEntity(entity.getOrganizationId(), id);
                this.em.persist(con);
            }
        }
    }

    /**
     * 組織IDを指定して、作業区分ID一覧を取得する。
     * 
     * @param organizationId 組織ID
     * @return 作業区分ID一覧
     */
    @Lock(LockType.READ)
    public List<Long> getWorkCategoryIds(Long organizationId) {
        // 組織IDを指定して、作業区分ID一覧を取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("ConOrganizationWorkCategoryEntity.findWorkCategoryId", Long.class);
        query.setParameter("organizationId", organizationId);
        return query.getResultList();
    }

    /**
     * 組織IDを指定して、組織・作業区分関連付け情報を削除する。
     *
     * @param organizationId  組織ID
     */
    private void removeWorkCategoryIds(Long organizationId) {
        // 組織IDを指定して、組織・作業区分関連付け情報を削除する。
        Query query = this.em.createNamedQuery("ConOrganizationWorkCategoryEntity.removeByOrganizationId");
        query.setParameter("organizationId", organizationId);
        query.executeUpdate();
    }

    /**
     * 組織・作業区分関連付け情報を追加する。
     *
     * @param entity 組織情報
     */
    private void addWorkCategories(OrganizationEntity entity) {
        if (Objects.nonNull(entity.getWorkCategoryCollection())) {
            for (Long id : entity.getWorkCategoryCollection()) {
                ConOrganizationWorkCategoryEntity con = new ConOrganizationWorkCategoryEntity(entity.getOrganizationId(), id);
                this.em.persist(con);
            }
        }
    }

    /**
     * 指定した組織IDとその子以降の組織ID一覧を取得する。
     *
     * @param organizationIds 組織ID一覧
     * @return 指定した組織IDとその子以降の組織ID一覧
     */
    @Lock(LockType.READ)
    public Set<Long> getRelatedOrganizationIds(List<Long> organizationIds) {
        Set<Long> ids = new HashSet<>();
        ids.addAll(organizationIds);
        for (Long parentId : organizationIds) {
            ids.addAll(this.getOrganizationChildren(parentId));
        }
        return ids;
    }

    /**
     * 指定された階層に、ユーザーがアクセス可能か取得する。
     *
     * @param id 階層ID (設備ID)
     * @param userId ユーザーID (組織ID)
     * @return アクセス (true: アクセス可能, false: アクセス不可)
     */
    @Lock(LockType.READ)
    private boolean isHierarchyAccessible(Long id, Long userId) {
        // ユーザーIDが未指定の場合、アクセス可能。
        if (Objects.isNull(userId)) {
            return true;
        }

        // 組織のアクセス権をチェックする。
        boolean isAccessible = this.authRest.isAccessible(AccessHierarchyTypeEnum.OrganizationHierarchy, id, userId);
        if (!isAccessible) {
            return false;
        }

        // 組織IDを指定して、親組織IDを取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("OrganizationEntity.findParentId", Long.class);
        query.setParameter("organizationId", id);

        Long parentId;
        try {
            parentId = query.getSingleResult();
        } catch (Exception ex) {
            parentId = null;
        }

        // 親組織が存在する場合、親組織の階層アクセス権をチェックする。
        if (Objects.nonNull(parentId) && parentId > 0){
            isAccessible = this.isHierarchyAccessible(parentId, userId);
        }

        return isAccessible;
    }

    /**
     * 役割ID一覧を指定して、権限名一覧を取得する。
     *
     * @param roleIds 役割ID一覧
     * @return 権限名一覧
     */
    @Lock(LockType.READ)
    private List<String> getRoleAuthorities(List<Long> roleIds) {
        Set<String> authes = new HashSet();
        for (Long roleId : roleIds) {
            // 役割情報を取得する。
            RoleAuthorityEntity auth = this.roleRest.find(roleId, null);

            // 実績削除権限
            if (auth.getActualDel()) {
                authes.add(RoleAuthorityTypeEnum.DELETE_ACTUAL.name());
            }
            // リソース編集権限
            if (auth.getResourceEdit()) {
                authes.add(RoleAuthorityTypeEnum.EDITED_RESOOURCE.name());
            }
            // カンバン作成権限
            if (auth.getKanbanCreate()) {
                authes.add(RoleAuthorityTypeEnum.MAKED_KANBAN.name());
            }
            // 工程・工程順編集権限
            if (auth.getWorkflowEdit()) {
                authes.add(RoleAuthorityTypeEnum.EDITED_WORKFLOW.name());
            }            
            // ライン管理権限
            if (auth.getLineManage()) {
                authes.add(RoleAuthorityTypeEnum.MANAGED_LINE.name());
            }
            // 実績出力権限
            if (auth.getActualOutput()) {
                authes.add(RoleAuthorityTypeEnum.OUTPUT_ACTUAL.name());
            }
            // 工程・工程順参照権限
            if (auth.getWorkflowReference()) {
                authes.add(RoleAuthorityTypeEnum.REFERENCE_WORKFLOW.name());
            }            
            // カンバン参照権限
            if (auth.getKanbanReference()) {
                authes.add(RoleAuthorityTypeEnum.REFERENCE_KANBAN.name());
            }
            // リソース参照権限
            if (auth.getResourceReference()) {
                authes.add(RoleAuthorityTypeEnum.REFERENCE_RESOOURCE.name());
            }
            // アクセス権編集権限
            if (auth.getAccessEdit()) {
                authes.add(RoleAuthorityTypeEnum.RIGHT_ACCESS.name());
            }
        }

        if (authes.isEmpty()) {
            return null;
        } else {
            return new ArrayList(authes);
        }
    }

    /**
     * 組織IDを指定して、組織名を取得する。
     *
     * @param id 組織ID
     * @return 組織名
     */
    @Lock(LockType.READ)
    public String findNameById(long id) {
        try {
            TypedQuery<String> query = this.em.createNamedQuery("OrganizationEntity.findNameById", String.class);
            query.setParameter("organizationId", id);
            return query.getSingleResult();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 組織IDを指定して、組織識別名を取得する。
     *
     * @param id 組織ID
     * @return 組織識別名
     */
    @Lock(LockType.READ)
    public String findIdentifyById(long id) {
        try {
            TypedQuery<String> query = this.em.createNamedQuery("OrganizationEntity.findIdentifyById", String.class);
            query.setParameter("organizationId", id);
            return query.getSingleResult();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 移動可能な階層かチェックする。
     *
     * @param id 階層ID
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
     * 組織IDを指定して、親組織IDを取得する。
     *
     * @param id 組織ID
     * @return 親組織ID
     */
    private Long findParentId(Long id) {
        try {
            // 組織IDを指定して、親組織IDを取得する。
            TypedQuery<Long> query = this.em.createNamedQuery("OrganizationEntity.findParentId", Long.class);
            query.setParameter("organizationId", id);

            return query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * TSVファイルから組織をインポートする。
     *
     * @param inputStreams TSVファイル
     * @return 結果(失敗してもOKが返る)
     */
    @POST
    @Path("import/file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response importFile(@FormDataParam("file") List<InputStream> inputStreams) {
        return this.importFile(inputStreams.get(0), null);
    }

    /**
     * TSVファイルから組織をインポートする。
     * 
     * @param inputStream
     * @param metaData
     * @return 
     */
    @POST
    @Path(value="import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_XML)
    @ExecutionTimeLogging
    public Response importFile(@FormDataParam("file") InputStream inputStream, @FormDataParam("file") FormDataContentDisposition metaData) {
        final String DELIMITER = "\t";

        try {
            final Date updateDatetime = new Date();

            String fileName = "import-" + new SimpleDateFormat("yyyyMMddHHmmss").format(updateDatetime) + ".tsv";

            FileManager fileManager = FileManager.getInstance();
            String filePath = fileManager.getLocalePath(FileManager.Data.Import, fileName);
            logger.info("importFile: " + filePath);

            BufferedInputStream bf = new BufferedInputStream(inputStream);
            byte[] buff = new byte[1024];

            try (OutputStream out = new FileOutputStream(new File(filePath))) {
                int len = -1;
                while ((len = bf.read(buff)) >= 0) {
                    out.write(buff, 0, len);
                }
                out.flush();
            }

            long adminId = this.checkExsistAdmin();
            List<BreaktimeEntity> breaktimes = breaktimeRest.findRange(null, null, null);
            List<Long> breaktimeIds = breaktimes.stream().map(BreaktimeEntity::getBreaktimeId).collect(Collectors.toList());

            int count = 0;

            File file = new File(filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] items = line.split(DELIMITER, -1);// 行末の""も取得するため、第2引数に-1を指定。
                    if (items.length < 3) {
                        break;
                    }

                    String organizationIdentify = items[0];
                    String organizationName = items[1];
                    String parentName = items[2];
                    String mailAddress = (items.length >= 4)? items[3] : null; // メールアドレス

                    PasswordEncoder encoder = new PasswordEncoder();
                    String password = encoder.encode("");

                    // 親組織IDを取得
                    // 親組織が指定なしの場合はルートID
                    long parentId = 0L;
                    OrganizationEntity parent = null;
                    if (!StringUtils.isEmpty(parentName)) {
                        parent = this.findByName(parentName, null, null);

                        if (Objects.isNull(parent) || Objects.isNull(parent.getOrganizationId())) {
                            // 親組織が存在しない場合
                            parent = new OrganizationEntity(0L, parentName, parentName, null, null, null, "", adminId, updateDatetime);
                            parent.setBreaktimeCollection(breaktimeIds);
                            this.add(parent, null);

                            parent = this.findByName(parentName, null, null);
                            parentId = parent.getOrganizationId();

                        } else {
                            parentId = parent.getOrganizationId();
                        }
                    }

                    // 子組織を更新
                    OrganizationEntity organization = this.findByName(organizationIdentify, null, null);
                    if (Objects.isNull(organization) || Objects.isNull(organization.getOrganizationId())) {
                        // 追加
                        organization = new OrganizationEntity(parentId, organizationName, organizationIdentify, null, null, password, "", adminId, updateDatetime);
                        organization.setMailAddress(mailAddress);
                        
                        if (Objects.nonNull(parent)) {
                            organization.setBreaktimeCollection(parent.getBreaktimeCollection());
                        } else {
                            organization.setBreaktimeCollection(breaktimeIds);
                        }

                        this.add(organization, null);

                        organization = this.findByName(organizationIdentify, null, null);
                        if (!this.isMoveableHierarchy(organization.getOrganizationId(), parentId)) {
                            logger.warn("Illegal hierarchy: {} {}", parentName, organizationIdentify);
                            this.remove(organization.getOrganizationId(), null);
                        }

                    } else {
                        // 更新
                        if (!StringUtils.equals(organization.getOrganizationName(), organizationName)) {
                            organization.setOrganizationName(organizationName);
                        }

                        if (!Objects.equals(organization.getParentOrganizationId(), parentId)) {
                            // 階層をチェック
                            if (this.isMoveableHierarchy(organization.getOrganizationId(), parentId)) {
                                organization.setParentOrganizationId(parentId);
                            } else {
                                logger.warn("Illegal hierarchy: {} {}", parentName, organizationIdentify);
                            }
                        }

                        // メールアドレス
                        if (!Objects.equals(organization.getMailAddress(), mailAddress)) {
                            organization.setMailAddress(mailAddress);
                        }

                        organization.setUpdatePersonId(adminId);
                        organization.setUpdateDatetime(updateDatetime);
                        this.update(organization, null);
                    }

                    count++;
                }
            }

            // 削除
            if (count > 0) {
                // ルート直下の組織を取得する。
                List<OrganizationEntity> childOrganizations = this.findChild(0L, null, null, null);

                // 不要な組織を削除する。
                for (int i = childOrganizations.size() - 1; i >= 0; i--) {
                    this.removeUnnecessary(childOrganizations.get(i), updateDatetime);
                }
            }

            file.delete();

            // 設備の変更を通知する
            DeviceConnectionServiceCommand command = new DeviceConnectionServiceCommand(DeviceConnectionServiceCommand.COMMAND.UPDATE_ORGANIZATION);
            this.adIntefaceFacade.noticeDeviceConnectionService(command);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * 指定した日時より前に更新された組織・子組織を削除する。
     *
     * @param organization 組織情報
     * @param updateDatetime 更新日時
     * @return 指定した組織が削除されたか？
     */
    private boolean removeUnnecessary(OrganizationEntity organization, Date updateDatetime) {
        boolean result = false;
        try {
            // 子組織を取得する。
            List<OrganizationEntity> childOrganizations = this.findChild(organization.getOrganizationId(), null, null, null);

            int childNum = childOrganizations.size();

            // 不要な子組織を削除する。
            for (int i = childNum - 1; i >= 0; i--) {
                if (this.removeUnnecessary(childOrganizations.get(i), updateDatetime)) {
                    childNum--;
                }
            }

            // 今回追加・更新した組織は削除しない。
            if (Objects.nonNull(organization.getUpdateDatetime())
                    && !updateDatetime.after(organization.getUpdateDatetime())) {
                return result;
            }

            // adminは削除しない。
            if (AuthorityEnum.SYSTEM_ADMIN.equals(organization.getAuthorityType())) {
                return result;
            }

            // 子組織がある場合は削除しない。
            if (childNum > 0) {
                logger.info("Undeletable organization (exist child): {}", organization.getOrganizationIdentify());
                return result;
            }

            // 工程順の工程で使用している場合は削除しない。
            TypedQuery<Long> query1 = em.createNamedQuery("ConWorkOrganizationEntity.countByOrganizationId", Long.class);
            query1.setParameter("organizationId", organization.getOrganizationId());
            Long num = query1.getSingleResult();

            if (num > 0) {
                logger.info("Undeletable organization (used in work): {}", organization.getOrganizationIdentify());
                return result;
            }

            // 組織を削除する。
            logger.info("Remove organization: {}", organization.getOrganizationIdentify());
            this.remove(organization.getOrganizationId(), null);

            result = true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 言語ファイル情報を作成する
     *
     * @param organization 組織情報
     */
    private List<LocaleFileEntity> createLocaleFileInfos(OrganizationEntity organization) {
        logger.info("createLocaleFileInfos start");
        try {
            List<LocaleFileEntity> resultList = this.lookUpLocaleFileInfo(organization, new ArrayList(), false, false, false);
            for (LocaleFileEntity info : resultList) {
                info.setResource(this.resourceRest.find(info.resource().getResourceId()));
            }
            return resultList;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        } finally {
            logger.info("createLocaleFileInfos end");
        }
    }

    /**
     * 言語ファイルを検索する(対象組織に設定されていなければ上位階層から取得)
     *
     * @param entity 対象組織
     * @param resultList 言語ファイルリスト
     * @param hasGetAdManager 管理者言語ファイルを取得したか
     * @param hasGetAdProduct 作業者言語ファイルを取得したか
     * @param hasGetCustum カスタム言語ファイルを取得したか
     *
     * @return 言語ファイルリスト
     */
    private List<LocaleFileEntity> lookUpLocaleFileInfo(OrganizationEntity entity, List<LocaleFileEntity> resultList, boolean hasGetAdManager, boolean hasGetAdProduct, boolean hasGetCustum) {
        if (Objects.nonNull(entity.getLangIds())) {
            List<LocaleFileEntity> list = JsonUtils.jsonToObjects(entity.getLangIds(), LocaleFileEntity[].class);
            for (LocaleFileEntity localeInfo : list) {
                if (Objects.isNull(localeInfo) || Objects.isNull(localeInfo.resource()) || Objects.isNull(localeInfo.resource().getResourceId())) {
                    continue;
                }
                switch (localeInfo.getLocaleType()) {
                    case ADMANAGER:
                        if (!hasGetAdManager) {
                            resultList.add(localeInfo);
                            hasGetAdManager = true;
                        }
                        break;
                    case ADPRODUCT:
                        if (!hasGetAdProduct) {
                            resultList.add(localeInfo);
                            hasGetAdProduct = true;
                        }
                        break;
                    case CUSTUM:
                        if (!hasGetCustum) {
                            resultList.add(localeInfo);
                            hasGetCustum = true;
                        }
                        break;
                }
            }
        }

        if ((!hasGetAdManager || !hasGetAdProduct || !hasGetCustum)
                || Objects.nonNull(entity.getParentOrganizationId())) {
            // １種類でも見つかっていなければ上位階層から再検索
            OrganizationEntity parentEntity = this.find(entity.getParentOrganizationId());
            if (Objects.nonNull(parentEntity)) {
                return this.lookUpLocaleFileInfo(parentEntity, resultList, hasGetAdManager, hasGetAdProduct, hasGetCustum);
            }
        }

        return resultList;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public void setEquipmentRest(EquipmentEntityFacadeREST equipmentRest) {
        this.equipmentRest = equipmentRest;
    }

    public void setRoleRest(RoleEntityFacadeREST roleRest) {
        this.roleRest = roleRest;
    }

    public void setAuthRest(AccessHierarchyEntityFacadeREST authRest) {
        this.authRest = authRest;
    }

    public void setAdInterfaceClientFacade(AdIntefaceClientFacade adIntefaceFacade) {
        this.adIntefaceFacade = adIntefaceFacade;
    }

    public void setIindirectWorkRest(IndirectWorkEntityFacadeREST indirectWorkRest) {
        this.indirectWorkRest = indirectWorkRest;
    }

    @Lock(LockType.READ)
    @GET
    @Path("mails")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public SampleResponse getMailingList(@QueryParam("id") final List<Long> ids, @QueryParam("authId") Long authId) {
        logger.info("getMailingList: ids={}, authId={}", ids, authId);

        if (ids.isEmpty()) {
            return new SampleResponse(ServerErrorTypeEnum.SUCCESS.name(), new ArrayList<>());
        }
        java.sql.Array idArray = null;
        try {
            idArray = this.em.unwrap(Connection.class).createArrayOf("integer", ids.toArray());

            final String sql = "WITH recursive r AS (SELECT mo.organization_id id  FROM mst_organization mo  WHERE mo.organization_id = ANY(?1)  UNION ALL  SELECT mo2.organization_id id  FROM mst_organization mo2, r  WHERE mo2.parent_organization_id = r.id ) SELECT distinct(mo3.mail_address) FROM mst_organization mo3 JOIN r ON mo3.organization_id = r.id WHERE mo3.mail_address NOTNULL AND mo3.remove_flag = FALSE";
            final Query query = em
                    .createNativeQuery(sql)
                    .setParameter(1, idArray);

            final List<String> mailList = (List<String>) query.getResultList();

            return new SampleResponse(ServerErrorTypeEnum.SUCCESS.name(), mailList);

        } catch (SQLException e) {
            e.printStackTrace();
            return new SampleResponse(ServerErrorTypeEnum.SERVER_FETAL.name(), null);
        }

    }

    /**
     * 組織情報を検索するクエリを取得する。
     *
     * @param condition 検索条件
     * @return 検索クエリ
     */
    @Lock(LockType.READ)
    private Query getSearchQuery(OrganizationSearchCondition condition) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<OrganizationEntity> root = cq.from(OrganizationEntity.class);

        jakarta.persistence.criteria.Path<Long> pathOrganizationId = root.get(OrganizationEntity_.organizationId);
        jakarta.persistence.criteria.Path<String> pathOrganizationName = root.get(OrganizationEntity_.organizationName);
        jakarta.persistence.criteria.Path<String> pathOrganizationIdentify = root.get(OrganizationEntity_.organizationIdentify);
        jakarta.persistence.criteria.Path<Boolean> pathRemoveFlag = root.get(OrganizationEntity_.removeFlag);

        // 検索条件
        List<Predicate> where = new LinkedList();

        if (condition.isMatch()) {
            // 完全一致検索

            // 組織名
            if (Objects.nonNull(condition.getOrganizationName())) {
                where.add(cb.equal(cb.lower(pathOrganizationName), StringUtils.toLowerCase(condition.getOrganizationName())));
            }

            // 組織識別名
            if (Objects.nonNull(condition.getOrganizationIdentify())) {
                where.add(cb.equal(cb.lower(pathOrganizationIdentify), StringUtils.toLowerCase(condition.getOrganizationIdentify())));
            }

        } else {
            // あいまい検索

            // 組織名
            if (Objects.nonNull(condition.getOrganizationName())) {
                where.add(cb.like(cb.lower(pathOrganizationName), QueryUtils.getLikeValue(condition.getOrganizationName())));
            }

            // 組織識別名
            if (Objects.nonNull(condition.getOrganizationIdentify())) {
                where.add(cb.like(cb.lower(pathOrganizationIdentify), QueryUtils.getLikeValue(condition.getOrganizationIdentify())));
            }
        }

        // 削除フラグ
        if (Objects.nonNull(condition.getRemoveFlag())) {
            where.add(cb.equal(pathRemoveFlag, condition.getRemoveFlag()));
        }

        cq.select(root)
                .where(cb.and(where.toArray(new Predicate[where.size()])))
                .orderBy(cb.asc(pathOrganizationId));

        return this.em.createQuery(cq);
    }

    /**
     * 組織情報を検索する。
     *
     * @param condition 検索条件
     * @param authId    認証ID
     * @return 組織情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<OrganizationEntity> searchOrganization(OrganizationSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("searchOrganization: {}, authId={}", condition, authId);

        if (Objects.isNull(authId)) {
            return new ArrayList<>();
        }

        Query query = this.getSearchQuery(condition);
        List<OrganizationEntity> entities = query.getResultList();
        
        OrganizationEntity user = this.find(authId);
        if (AuthorityEnum.SYSTEM_ADMIN.equals(user.getAuthorityType())) {
            return entities;
        }

        List<OrganizationEntity> result = new ArrayList<>();
        List<Long> organizationIds = this.findAncestors(authId);

        for (OrganizationEntity entity : entities) {
            boolean allow = true;
            
            // アクセスが許可されているか
            List<Hierarchy> permissionList = this.getAccessPermission(entity.getOrganizationId());
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
                    entity.setChildCount(this.countChild(entity.getOrganizationId(), authId));
                }
            }
        }

        return result;
    }

    /**
     * アクセス権を取得する。
     * 
     * @param id 組織ID
     * @return アクセス権
     */
    @Lock(LockType.READ)
    private List<Hierarchy> getAccessPermission(Long id) {
        Query query = this.em.createNamedQuery("Hierarchy.findOrganization", Hierarchy.class);
        query.setParameter(1, id);
        return query.getResultList();
    }

    /**
     * 組織の先祖一覧を取得する。
     * 
     * @param ids 検索ID
     * @param authId 認証ID
     * @return 組織の先祖一覧
     */
    public List<OrganizationEntity> findOrganizationAncestors(final List<Long> ids, Long authId) {
        logger.info("findOrganizationAncestors: ids={}, authId={}", ids, authId);

        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            java.sql.Array idArray = this.em.unwrap(Connection.class).createArrayOf("integer", ids.toArray());
            final Query query = em
                    .createNamedQuery("OrganizationEntity.findAncestorsByIds", OrganizationEntity.class)
                    .setParameter(1, idArray);

            final List<OrganizationEntity> result = query.getResultList();
            return result;
        } catch (SQLException ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
    
    /**
     * 理由区分ID一覧を取得する。
     *
     * @param organizationId 組織ID
     * @return 理由区分ID一覧
     */
    @Lock(LockType.READ)
    public List<ConOrganizationReasonEntity> getReason(Long organizationId) {
        TypedQuery<ConOrganizationReasonEntity> query = this.em.createNamedQuery("ConOrganizationReasonEntity.findByOrganizationId", ConOrganizationReasonEntity.class);
        query.setParameter("organizationId", organizationId);
        return query.getResultList();
    }

    /**
     * 組織・理由区分関連付け情報を削除する。
     *
     * @param organizationId 組織ID
     */
    private void removeReason(Long organizationId) {
        // 組織IDを指定して、組織・休憩関連付け情報を削除する。
        Query query = this.em.createNamedQuery("ConOrganizationReasonEntity.removeByOrganizationId");
        query.setParameter("organizationId", organizationId);
        query.executeUpdate();
    }

    /**
     * 組織・理由区分関連付け情報を追加する。
     *
     * @param entity 組織情報
     */
    private void addReason(OrganizationEntity entity) {
        // 中断理由
        if (Objects.nonNull(entity.getInterruptCategoryCollection())) {
            for (Long id : entity.getInterruptCategoryCollection()) {
                ConOrganizationReasonEntity con = new ConOrganizationReasonEntity(entity.getOrganizationId(), id, ReasonTypeEnum.TYPE_INTERRUPT);
                this.em.persist(con);
            }
        }

        // 遅延理由
        if (Objects.nonNull(entity.getDelayCategoryCollection())) {
            for (Long id : entity.getDelayCategoryCollection()) {
                ConOrganizationReasonEntity con = new ConOrganizationReasonEntity(entity.getOrganizationId(), id, ReasonTypeEnum.TYPE_DELAY);
                this.em.persist(con);
            }
        }

        // 呼出理由
        if (Objects.nonNull(entity.getCallCategoryCollection())) {
            for (Long id : entity.getCallCategoryCollection()) {
                ConOrganizationReasonEntity con = new ConOrganizationReasonEntity(entity.getOrganizationId(), id, ReasonTypeEnum.TYPE_CALL);
                this.em.persist(con);
            }
        }
    }
    
    /**
     * 理由情報を設定する。
     * 
     * @param entity 組織情報
     */
    private void setReason(OrganizationEntity entity) {
        try {
            TypedQuery<ReasonMasterEntity> query = this.em.createNamedQuery("ReasonMasterEntity.findByOrganizationId", ReasonMasterEntity.class);
            query.setParameter("organizationId", organizationId);
            List<ReasonMasterEntity> reasons = query.getResultList();

            reasons.forEach(o -> {
                switch (o.getReasonType()) {
                   case TYPE_INTERRUPT:
                        entity.getDelayReasons().add(o);
                        break;
                    case TYPE_DELAY:
                        entity.getDelayReasons().add(o);
                        break;
                    case TYPE_CALL:
                        entity.getCallReasons().add(o);
                        break;
                    default:
                        break;
                }
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 組織に関連した理由を取得する。
     * 
     * @param organizationId 組織ID
     * @param reasonType 理由種別
     * @param authId 認識ID
     * @return 理由情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("reason")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ReasonMasterEntity> findReason(@QueryParam("id") Long organizationId, @QueryParam("type") ReasonTypeEnum reasonType, @QueryParam("authId") Long authId) {
        logger.info("findReason: organizationId={}, reasonType={}, authId={}", organizationId, reasonType, authId);

        try {
            List<Long> reasonCategoryIds = null;
            
            OrganizationEntity organization = this.find(organizationId, authId);
            switch (reasonType) {
                case TYPE_INTERRUPT:
                    while (Objects.nonNull(organization.getParentOrganizationId()) && organization.getInterruptCategoryCollection().isEmpty()) {
                        organization = this.find(organization.getParentOrganizationId(), authId);
                    }
                    reasonCategoryIds = organization.getInterruptCategoryCollection();
                    break;
                case TYPE_DELAY:
                    while (Objects.nonNull(organization.getParentOrganizationId()) && organization.getDelayCategoryCollection().isEmpty()) {
                        organization = this.find(organization.getParentOrganizationId(), authId);
                    }
                    reasonCategoryIds = organization.getDelayCategoryCollection();
                    break;
                case TYPE_CALL:
                    while (Objects.nonNull(organization.getParentOrganizationId()) && organization.getCallCategoryCollection().isEmpty()) {
                        organization = this.find(organization.getParentOrganizationId(), authId);
                    }
                    reasonCategoryIds = organization.getCallCategoryCollection();
                    break;
            }
            
            if (Objects.isNull(reasonCategoryIds) || reasonCategoryIds.isEmpty()) {
                // デフォルトの理由を返す
                TypedQuery<ReasonCategoryEntity> query = this.em.createNamedQuery("ReasonCategoryEntity.findDefaultByType", ReasonCategoryEntity.class);
                query.setParameter("reasonType", reasonType);
                List<ReasonCategoryEntity> reasonCategories = query.getResultList();
                if (reasonCategories.isEmpty()) {
                    logger.warn("Default reason category is nothing.");
                    return new ArrayList();
                }

                reasonCategoryIds = reasonCategories.stream().map(o -> o.getReasonCategoryId()).collect(Collectors.toList());
            }
            
            return reasonRest.findByCategoryId(reasonCategoryIds, authId);

        } catch (Exception ex) {
            logger.fatal(ex);
            return new ArrayList();
        } finally {
            logger.info("findReason: end");
        }
    }
}
