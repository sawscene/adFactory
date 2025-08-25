/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.clientservice;

import adtekfuji.rest.RestClient;
import com.sun.jersey.api.client.GenericType;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.forfujiapp.common.AdFactoryForFujiClientAppConfig;
import jp.adtekfuji.forfujiapp.common.RestConstants;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレートREST
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTemplateInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient = new RestClient();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final static String USER_PATH = "user=%s";

    public UnitTemplateInfoFacade() {
        restClient.setUriBase(new AdFactoryForFujiClientAppConfig().getAdFactoryForFujiServerAddress());
    }

    public UnitTemplateInfoFacade(String uriBase) {
        restClient.setUriBase(uriBase);
    }

    /**
     * ユニットテンプレートを取得
     *
     * @param id ID
     * @return レスポンス
     */
    public UnitTemplateInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder path = new StringBuilder();
            path.append(RestConstants.PATH_UNIT_TEMPLATE);
            path.append(String.format(RestConstants.PATH_ID_TARGET, id.toString()));
            return (UnitTemplateInfoEntity) restClient.find(path.toString(), UnitTemplateInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 生産ユニットテンプレートを取得する
     *
     * @param unitTemplateIds
     * @return
     */
    public List<UnitTemplateInfoEntity> find(List<Long> unitTemplateIds) {
        logger.debug("find:{}", unitTemplateIds);
        try {
            StringBuilder path = new StringBuilder();
            path.append(RestConstants.PATH_UNIT_TEMPLATE);
            path.append(RestConstants.PATH_FIND);
            path.append(RestConstants.QUERY_PATH);
            for (int i = 0; i < unitTemplateIds.size(); i++) {
                path.append("id=");
                path.append(unitTemplateIds.get(i));
                if (i < unitTemplateIds.size() - 1) {
                    path.append(RestConstants.QUERY_AND);
                }
            }
            return restClient.find(path.toString(), new GenericType<List<UnitTemplateInfoEntity>>() {});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 新しいユニットテンプレートの登録
     *
     * @param unitTemplateInfo
     * @return レスポンス
     */
    public ResponseEntity regist(UnitTemplateInfoEntity unitTemplateInfo) {
        logger.debug("regist:{}", unitTemplateInfo);
        try {
            return (ResponseEntity) restClient.post(RestConstants.PATH_UNIT_TEMPLATE, unitTemplateInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity().errorCode(500L).errorType(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * ユニットテンプレートの更新
     *
     * @param unitTemplateInfo
     * @return レスポンス
     */
    public ResponseEntity update(UnitTemplateInfoEntity unitTemplateInfo) {
        logger.debug("update:{}", unitTemplateInfo);
        try {
            return (ResponseEntity) restClient.put(RestConstants.PATH_UNIT_TEMPLATE, unitTemplateInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity().errorCode(500L).errorType(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * ユニットテンプレートのコピー
     *
     * @param id コピーしたいテンプレートのID
     * @return レスポンス
     */
    public ResponseEntity copy(Long id) {
        logger.debug("copy:{}", id);
        try {
            StringBuilder path = new StringBuilder();
            path.append(RestConstants.PATH_UNIT_TEMPLATE);
            path.append(RestConstants.PATH_COPY);
            path.append(RestConstants.PATH_SEPARATOR);
            path.append(id);
            return (ResponseEntity) restClient.post(path.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * ユニットテンプレートの削除
     *
     * @param unitTemplateInfo
     * @return レスポンス
     */
    public ResponseEntity remove(UnitTemplateInfoEntity unitTemplateInfo) {
        logger.debug("remove:{}", unitTemplateInfo);
        try {
            return (ResponseEntity) restClient.delete(RestConstants.PATH_UNIT_TEMPLATE, unitTemplateInfo.getUnitTemplateId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity().errorCode(500L).errorType(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 名称からユニットテンプレートを取得
     *
     * @param name
     * @return ユニットテンプレート
     */
    public UnitTemplateInfoEntity findName(String name) {
        logger.debug("findName:{}", name);
        try {
            StringBuilder path = new StringBuilder();
            path.append(RestConstants.PATH_UNIT_TEMPLATE);
            path.append(RestConstants.PATH_NAME);
            path.append(RestConstants.QUERY_PATH);
            path.append(String.format(RestConstants.QUERY_NAME, name));
            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                path.append(RestConstants.QUERY_AND);
                path.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }
            return (UnitTemplateInfoEntity) restClient.find(path.toString(), UnitTemplateInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }
}
