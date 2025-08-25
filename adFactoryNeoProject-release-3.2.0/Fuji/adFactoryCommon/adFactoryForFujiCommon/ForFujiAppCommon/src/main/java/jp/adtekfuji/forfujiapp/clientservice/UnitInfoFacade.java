/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | s
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.clientservice;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import com.sun.jersey.api.client.GenericType;
import java.util.List;
import javax.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.forfujiapp.common.AdFactoryForFujiClientAppConfig;
import jp.adtekfuji.forfujiapp.common.RestConstants;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitKanbanInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットREST
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitInfoFacade {

    private final Logger logger = LogManager.getLogger();
    //private final RestClient restClient = new RestClient();
    private final RestClient restClient;

    public UnitInfoFacade() {
        RestClientProperty property = new RestClientProperty(ClientServiceProperty.getServerUri());
        property.setTimeout(120 * 1000);
        restClient = new RestClient(property);

        restClient.setUriBase(new AdFactoryForFujiClientAppConfig().getAdFactoryForFujiServerAddress());
    }

    /**
     * ユニットを取得
     *
     * @param id ID
     * @return レスポンス
     */
    public UnitInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        StringBuilder path = new StringBuilder();
        path.append(RestConstants.PATH_UNIT);
        path.append(String.format(RestConstants.PATH_ID_TARGET, id.toString()));
        return (UnitInfoEntity) restClient.find(path.toString(), UnitInfoEntity.class);
    }

    /**
     * 検索数取得
     *
     * @param condition 条件
     * @return 検索数
     */
    public Long countSearch(UnitSearchCondition condition) {
        logger.debug("countSearch:{}", condition);
        StringBuilder path = new StringBuilder();
        path.append(RestConstants.PATH_UNIT);
        path.append(RestConstants.PATH_SEARCH);
        path.append(RestConstants.PATH_COUNT);
        return Long.parseLong((String) restClient.put(path.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));
    }

    /**
     * 指定された範囲のUnit情報を検索
     *
     * @param condition 条件
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲のカンバン一覧
     */
    public List<UnitInfoEntity> findSearch(Long from, Long to, UnitSearchCondition condition) {
        logger.debug("findSearchRange:{}", condition);
        StringBuilder path = new StringBuilder();
        path.append(RestConstants.PATH_UNIT);
        path.append(RestConstants.PATH_SEARCH);
        path.append(RestConstants.PATH_RANGE);
        path.append(RestConstants.QUERY_PATH);
        path.append(String.format(RestConstants.QUERY_FROM_TO, from.toString(), to.toString()));
        return restClient.put(path.toString(), condition, new GenericType<List<UnitInfoEntity>>() {
        });
    }

    /**
     * 指定された範囲のUnit情報を検索
     *
     * @param condition 条件
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲のカンバン一覧
     */
    public List<UnitInfoEntity> findBasicSearch(Long from, Long to, UnitSearchCondition condition) {
        logger.debug("findBasicSearchRange:{}", condition);
        StringBuilder path = new StringBuilder();
        path.append(RestConstants.PATH_UNIT);
        path.append(RestConstants.PATH_BASICSEARCH);
        path.append(RestConstants.PATH_RANGE);
        path.append(RestConstants.QUERY_PATH);
        path.append(String.format(RestConstants.QUERY_FROM_TO, from.toString(), to.toString()));
        return restClient.put(path.toString(), condition, new GenericType<List<UnitInfoEntity>>() {
        });
    }

    /**
     * 指定したユニットIDの作業ステータスを取得する
     *
     * @param id
     * @return
     */
    public KanbanStatusEnum getStatus(Long id) {
        logger.debug("getStatus:{}", id);
        StringBuilder path = new StringBuilder();
        path.append(RestConstants.PATH_UNIT);
        path.append(RestConstants.PATH_STATUS);
        path.append(String.format(RestConstants.PATH_ID_TARGET, id));
        String name = (String) restClient.find(path.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
        KanbanStatusEnum status = KanbanStatusEnum.valueOf(name);
        return status;
    }

    /**
     * 新しいユニットの登録
     *
     * @param unitInfo
     * @return レスポンス
     */
    public ResponseEntity regist(UnitInfoEntity unitInfo) {
        logger.debug("regist:{}", unitInfo);
        try {
            return (ResponseEntity) restClient.post(RestConstants.PATH_UNIT, unitInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity().errorCode(500L).errorType(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * ユニットの更新
     *
     * @param unitInfo
     * @return レスポンス
     */
    public ResponseEntity update(UnitInfoEntity unitInfo) {
        logger.debug("update:{}", unitInfo);
        try {
            return (ResponseEntity) restClient.put(RestConstants.PATH_UNIT, unitInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity().errorCode(500L).errorType(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * ユニットの削除
     *
     * @param unitInfo
     * @return レスポンス
     */
    public ResponseEntity remove(UnitInfoEntity unitInfo) {
        logger.debug("remove:{}", unitInfo);
        try {
            return (ResponseEntity) restClient.delete(RestConstants.PATH_UNIT, unitInfo.getUnitId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity().errorCode(500L).errorType(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 名称からユニットを取得
     *
     * @param name ユニット名
     * @return レスポンス
     */
    public UnitInfoEntity find(String name) {
        logger.debug("find:{}", name);
        StringBuilder path = new StringBuilder();
        path.append(RestConstants.PATH_UNIT);
        path.append(RestConstants.PATH_NAME);
        path.append(RestConstants.QUERY_PATH);
        path.append(String.format(RestConstants.QUERY_NAME, name));
        return (UnitInfoEntity) restClient.find(path.toString(), UnitInfoEntity.class);
    }

    /**
     * URIからユニットを取得
     *
     * @param uri ユニットのURI
     * @return レスポンス
     * @throws java.lang.Exception
     */
    public UnitInfoEntity findURI(String uri) throws Exception {
        logger.debug("findURI:{}", uri);
        try {
            return (UnitInfoEntity) restClient.find("/" + uri, UnitInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン情報を取得する。
     *
     * @param kanbanIds
     * @return
     */
    public List<UnitKanbanInfoEntity> findUnitKanbans(List<Long> kanbanIds) {
        logger.debug("findUnitKanbans:{}", kanbanIds);
        StringBuilder sb = new StringBuilder();
        sb.append(RestConstants.PATH_UNIT);
        sb.append(RestConstants.PATH_KANBAN);
        sb.append(RestConstants.QUERY_PATH);
        for (int i = 0; i < kanbanIds.size(); i++) {
            sb.append("id=");
            sb.append(kanbanIds.get(i));
            if (i < kanbanIds.size() - 1) {
                sb.append(RestConstants.QUERY_AND);
            }
        }
        return restClient.findAll(sb.toString(), new GenericType<List<UnitKanbanInfoEntity>>() {});
    }
}
