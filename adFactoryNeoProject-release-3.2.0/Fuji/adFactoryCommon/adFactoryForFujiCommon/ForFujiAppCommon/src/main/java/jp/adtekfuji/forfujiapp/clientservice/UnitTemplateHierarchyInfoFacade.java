/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.clientservice;

import adtekfuji.rest.RestClient;
import com.sun.jersey.api.client.GenericType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.forfujiapp.common.AdFactoryForFujiClientAppConfig;
import jp.adtekfuji.forfujiapp.common.RestConstants;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレート階層REST
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTemplateHierarchyInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient = new RestClient();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final static String PATH_UNITTEMPLATE_TREE = RestConstants.PATH_UNIT_TEMPLATE + RestConstants.PATH_TREE;
    private final static String USER_PATH = "user=%s";

    public UnitTemplateHierarchyInfoFacade(String basePath) {
        restClient.setUriBase(basePath);
    }

    public UnitTemplateHierarchyInfoFacade() {
        restClient.setUriBase(new AdFactoryForFujiClientAppConfig().getAdFactoryForFujiServerAddress());
    }

    /**
     * ユニットテンプレート階層を取得
     *
     * @param id ID
     * @return レスポンス
     */
    public UnitTemplateHierarchyInfoEntity findTree(Long id) {
        logger.debug("find:{}", id);
        StringBuilder path = new StringBuilder();
        path.append(PATH_UNITTEMPLATE_TREE);
        path.append(String.format(RestConstants.PATH_ID_TARGET, id.toString()));
        return (UnitTemplateHierarchyInfoEntity) restClient.find(path.toString(), UnitTemplateHierarchyInfoEntity.class);
    }

    /**
     * 階層に含まれるユニットテンプレート階層の数を取得
     *
     * @param parentId 親階層ID
     * @return 最上位階層のユニットテンプレート階層数
     */
    public Long findTreeCount(Long parentId) {
        logger.info("findTreeCount:{}DFWFWFE", parentId);
        StringBuilder path = new StringBuilder();
        path.append(PATH_UNITTEMPLATE_TREE);
        path.append(RestConstants.PATH_COUNT);
        if (Objects.nonNull(parentId)) {
            path.append(RestConstants.QUERY_PATH);
            path.append(String.format(RestConstants.QUERY_ID, parentId.toString()));
            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                path.append(RestConstants.QUERY_AND);
                path.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }
        } else {
            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                path.append(RestConstants.QUERY_PATH);
                path.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }
        }
        String count = (String) restClient.find(path.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
        return Long.parseLong(count);
    }

    /**
     * 階層に含まれるユニットテンプレート階層を範囲指定で取得
     *
     * @param parentId 親階層ID
     * @param from 頭数
     * @param to 尾数
     * @param hasChild
     * @return 最上位階層のユニットテンプレート階層一覧
     */
    public List<UnitTemplateHierarchyInfoEntity> findTreeRange(Long parentId, Long from, Long to, Boolean hasChild) {
        logger.info("findTreeRange:{},{},{}", parentId, from, to, hasChild);

        StringBuilder path = new StringBuilder();
        path.append(PATH_UNITTEMPLATE_TREE);
        path.append(RestConstants.PATH_RANGE);
        path.append(RestConstants.QUERY_PATH);
        if (Objects.nonNull(parentId)) {
            path.append(String.format(RestConstants.QUERY_ID, parentId.toString()));
            path.append(RestConstants.QUERY_AND);
        }
        path.append(String.format(RestConstants.QUERY_FROM_TO, from.toString(), to.toString()));
        if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
            path.append(RestConstants.QUERY_AND);
            path.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
        }
        if (Objects.nonNull(hasChild)) {
            path.append(RestConstants.QUERY_AND);
            path.append(String.format(RestConstants.QUERY_HAS_CHILD, hasChild.toString()));
        }

        List<UnitTemplateHierarchyInfoEntity> entities = restClient.findAll(path.toString(), new GenericType<List<UnitTemplateHierarchyInfoEntity>>() {});
        return entities;
    }

    /**
     * 新しいユニットテンプレート階層の登録
     *
     * @param unitTemplateHierarchyInfo
     * @return レスポンス
     */
    public ResponseEntity regist(UnitTemplateHierarchyInfoEntity unitTemplateHierarchyInfo) {
        logger.debug("registHierarchy:{}", unitTemplateHierarchyInfo);
        try {
            return (ResponseEntity) restClient.post(PATH_UNITTEMPLATE_TREE, unitTemplateHierarchyInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * ユニットテンプレート階層の更新
     *
     * @param unitTemplateHierarchyInfo
     * @return レスポンス
     */
    public ResponseEntity update(UnitTemplateHierarchyInfoEntity unitTemplateHierarchyInfo) {
        logger.debug("updateHierarchy:{}", unitTemplateHierarchyInfo);
        try {
            return (ResponseEntity) restClient.put(PATH_UNITTEMPLATE_TREE, unitTemplateHierarchyInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * ユニットテンプレート階層の削除
     *
     * @param unitTemplateHierarchyInfo
     * @return レスポンス
     */
    public ResponseEntity remove(UnitTemplateHierarchyInfoEntity unitTemplateHierarchyInfo) {
        logger.debug("removeHierarchy:{}", unitTemplateHierarchyInfo);
        try {
            return (ResponseEntity) restClient.delete(PATH_UNITTEMPLATE_TREE, unitTemplateHierarchyInfo.getUnitTemplateHierarchyId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     *
     *
     * @param hierarchyId
     * @return
     */
    public Long countUnitTemplate(long hierarchyId) {
        logger.info("countUnitTemplate:{}", hierarchyId);
        StringBuilder sb = new StringBuilder();
        sb.append(PATH_UNITTEMPLATE_TREE);
        sb.append(RestConstants.PATH_UNIT_TEMPLATE);
        sb.append(RestConstants.PATH_COUNT);
        sb.append(RestConstants.QUERY_PATH);
        sb.append(String.format(RestConstants.QUERY_ID, String.valueOf(hierarchyId)));
        String count = (String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
        return Long.parseLong(count);
    }

    /**
     * ユニットを検索する
     *
     * @param hierarchyId
     * @param from
     * @param to
     * @return
     */
    public List<UnitTemplateInfoEntity> findUnitTemplateRange(long hierarchyId, long from, long to) {
        logger.debug("findUnitTemplateRange:{},{},{}", hierarchyId, from, to);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH_UNITTEMPLATE_TREE);
            sb.append(RestConstants.PATH_UNIT_TEMPLATE);
            sb.append(RestConstants.PATH_RANGE);
            sb.append(RestConstants.QUERY_PATH);
            sb.append(String.format(RestConstants.QUERY_ID, hierarchyId));
            sb.append(RestConstants.QUERY_AND);
            sb.append(String.format(RestConstants.QUERY_FROM_TO, String.valueOf(from), String.valueOf(to)));
            return restClient.findAll(sb.toString(), new GenericType<List<UnitTemplateInfoEntity>>() {});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
}
