/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.PartsInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.PartsRemoveCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 完成品情報
 * 
 * @author s-heya
 */
public class PartsInfoFacade {
    
    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    /**
     * コンストラクタ
     */
    public PartsInfoFacade() {
        this.restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }
    
    /**
     * パーツIDの一部の文字列から完成品情報を検索する。
     * 
     * @param keyword パーツIDの一部の文字列
     * @return 完成品情報
     */
    public List<PartsInfoEntity> searchParts(String keyword) {
        logger.debug("searchParts: {}", keyword);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("/parts/search?keyword=").append(RestClient.encode(keyword));

            return (List<PartsInfoEntity>) restClient.find(sb.toString(), new GenericType<List<PartsInfoEntity>>() {});

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 完成品情報を削除する。
     * 
     * @param condition 完成品情報削除条件
     * @return 処理結果
     */
    public ResponseEntity removeForced(PartsRemoveCondition condition) {
        logger.debug("removeForced");
        try {
            
            return (ResponseEntity) restClient.put("/parts/remove", condition, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }
    
}
