/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | s
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
import jp.adtekfuji.forfujiapp.entity.unit.UnitHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニット階層REST
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitHierarchyInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient = new RestClient();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final static String PATH_UNIT_TREE = RestConstants.PATH_UNIT + RestConstants.PATH_TREE;
    private final static String USER_PATH = "user=%s";

    public UnitHierarchyInfoFacade() {
        restClient.setUriBase(new AdFactoryForFujiClientAppConfig().getAdFactoryForFujiServerAddress());
    }

    /**
     * ユニット階層を取得
     *
     * @param id ID
     * @return レスポンス
     */
    public UnitHierarchyInfoEntity findTree(Long id) {
        logger.debug("find:{}", id);
        StringBuilder path = new StringBuilder();
        path.append(PATH_UNIT_TREE);
        path.append(String.format(RestConstants.PATH_ID_TARGET, id.toString()));
        return (UnitHierarchyInfoEntity) restClient.find(path.toString(), UnitHierarchyInfoEntity.class);
    }

    /**
     * 階層に含まれるユニット階層の数を取得
     *
     * @param parentId 親階層ID
     * @return 最上位階層のユニット階層数
     */
    public Long findTreeCount(Long parentId) {
        logger.info("findTreeCount:{}", parentId);
        StringBuilder path = new StringBuilder();
        path.append(PATH_UNIT_TREE);
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
     * 階層に含まれるユニット階層を範囲指定で取得
     *
     * @param parentId 親階層ID
     * @param from 頭数
     * @param to 尾数
     * @param hasChild
     * @return 最上位階層のユニット階層一覧
     */
    public List<UnitHierarchyInfoEntity> findTreeRange(Long parentId, Long from, Long to, Boolean hasChild) {
        logger.info("findTreeRange:{},{},{}", parentId, from, to);
        StringBuilder path = new StringBuilder();
        path.append(PATH_UNIT_TREE);
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

        List<UnitHierarchyInfoEntity> entities = restClient.findAll(path.toString(), new GenericType<List<UnitHierarchyInfoEntity>>() {});
        return entities;
    }

    /**
     * 新しいユニット階層の登録
     *
     * @param unitHierarchyInfo
     * @return レスポンス
     */
    public ResponseEntity regist(UnitHierarchyInfoEntity unitHierarchyInfo) {
        logger.debug("registHierarchy:{}", unitHierarchyInfo);
        try {
            return (ResponseEntity) restClient.post(PATH_UNIT_TREE, unitHierarchyInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * ユニット階層の更新
     *
     * @param unitHierarchyInfo
     * @return レスポンス
     */
    public ResponseEntity update(UnitHierarchyInfoEntity unitHierarchyInfo) {
        logger.debug("updateHierarchy:{}", unitHierarchyInfo);
        try {
            return (ResponseEntity) restClient.put(PATH_UNIT_TREE, unitHierarchyInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * ユニット階層の削除
     *
     * @param unitHierarchyInfo
     * @return レスポンス
     */
    public ResponseEntity remove(UnitHierarchyInfoEntity unitHierarchyInfo) {
        logger.debug("removeHierarchy:{}", unitHierarchyInfo);
        try {
            return (ResponseEntity) restClient.delete(PATH_UNIT_TREE, unitHierarchyInfo.getUnitHierarchyId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 指定したユニット階層名のユニット階層を取得 (階層のみでユニットリストは取得しない)
     *
     * @param name ユニット階層名
     * @return ユニット階層
     */
    public UnitHierarchyInfoEntity findHierarchyName(String name) {
        logger.debug("findHierarchyName:{}", name);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(RestConstants.PATH_UNIT);
            sb.append(RestConstants.PATH_TREE);
            sb.append(RestConstants.PATH_NAME);
            sb.append(RestConstants.QUERY_PATH);
            sb.append(String.format(RestConstants.QUERY_NAME, name));
            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(RestConstants.QUERY_AND);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }
            return (UnitHierarchyInfoEntity) restClient.find(sb.toString(), UnitHierarchyInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new UnitHierarchyInfoEntity();
        }
    }

    /**
     * ユニット階層に属するユニット情報の個数を取得
     *
     * @param hierarchyId ユニット階層ID
     * @return ユニット情報の数
     */
    public Long countUnit(long hierarchyId) {
        return this.countUnit(hierarchyId, true);
    }

    /**
     * ユニット階層に属するユニット情報の個数を取得
     *
     * @param hierarchyId ユニット階層ID
     * @param isAll すべて取得？ (true:すべて, false:未完了のみ)
     * @return ユニット情報の数
     */
    public Long countUnit(long hierarchyId, boolean isAll) {
        logger.info("countUnit:{}", hierarchyId);
        StringBuilder sb = new StringBuilder();
        sb.append(RestConstants.PATH_UNIT);
        sb.append(RestConstants.PATH_TREE);
        sb.append(RestConstants.PATH_UNIT);
        sb.append(RestConstants.PATH_COUNT);
        sb.append(RestConstants.QUERY_PATH);
        sb.append(String.format(RestConstants.QUERY_ID, String.valueOf(hierarchyId)));
        sb.append(RestConstants.QUERY_AND);
        sb.append(String.format(RestConstants.QUERY_ALL, isAll));
        String count = (String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
        return Long.parseLong(count);
    }

    /**
     * ユニット階層に属するユニット情報を取得
     *
     * @param hierarchyId ユニット階層ID
     * @param from rangeの先頭
     * @param to rangeの末尾
     * @return ユニット情報リスト
     */
    public List<UnitInfoEntity> findUnitRange(long hierarchyId, long from, long to) {
        return findUnitRange(hierarchyId, from, to, true);
    }

    /**
     * ユニット階層に属するユニット情報を取得
     *
     * @param hierarchyId ユニット階層ID
     * @param from rangeの先頭
     * @param to rangeの末尾
     * @param isAll すべて取得？ (true:すべて, false:未完了のみ)
     * @return ユニット情報リスト
     */
    public List<UnitInfoEntity> findUnitRange(long hierarchyId, long from, long to, boolean isAll) {
        logger.debug("findUnitRange:{},{},{}", hierarchyId, from, to);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(RestConstants.PATH_UNIT);
            sb.append(RestConstants.PATH_TREE);
            sb.append(RestConstants.PATH_UNIT);
            sb.append(RestConstants.PATH_RANGE);
            sb.append(RestConstants.QUERY_PATH);
            sb.append(String.format(RestConstants.QUERY_ID, hierarchyId));
            sb.append(RestConstants.QUERY_AND);
            sb.append(String.format(RestConstants.QUERY_FROM_TO, String.valueOf(from), String.valueOf(to)));
            sb.append(RestConstants.QUERY_AND);
            sb.append(String.format(RestConstants.QUERY_ALL, isAll));
            return restClient.findAll(sb.toString(), new GenericType<List<UnitInfoEntity>>() {});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
}
