/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adtekfuji.clientservice;

import adtekfuji.clientservice.common.Paths;
import static adtekfuji.clientservice.common.Paths.QUERY_PATH;
import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import adtekfuji.utility.StringUtils;
import jakarta.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.SampleResponse;
import jp.adtekfuji.adFactory.entity.job.MstDsItemInfo;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 品番マスタ情報 RESTクライアント
 * 
 * @author s-heya
 */
public class DsItemFacade {
    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();

    private final String PATH = "/dsItem";
    
    /**
     * コンストラクタ
     */
    public DsItemFacade() {
        this.restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }
    
    /**
     * 品番マスタ情報を登録する。
     * 
     * @param entity 品番マスタ情報
     * @return 処理結果
     */
    public ResponseEntity regist(MstDsItemInfo entity) {
        logger.debug("regist: {}", entity);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH);
            sb.append(QUERY_PATH);

            if (Objects.nonNull(loginUser.getId())) {
                sb.append(String.format(Paths.AUTHID_PATH, loginUser.getId()));
            }
            
            return (ResponseEntity) restClient.post(sb.toString(), entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * 品番マスタ情報の登録件数を返す。                                
     * 
     * @param category 区分
     * @param productNo 品番
     * @return 登録件数
     */
    public Integer count(Integer category, String productNo) {
        logger.debug("count: {}", category);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH);
            sb.append(Paths.COUNT_PATH);
            sb.append(QUERY_PATH);
            sb.append("category=");
            sb.append(category);

            if (!StringUtils.isEmpty(productNo)) {
                sb.append("&productNo=");
                sb.append(productNo);
            }

            if (Objects.nonNull(loginUser.getId())) {
                sb.append(Paths.AND_PATH);
                sb.append(String.format(Paths.AUTHID_PATH, loginUser.getId()));
            }

            return Integer.valueOf((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));

        } catch (NumberFormatException ex) {
            logger.fatal(ex, ex);
            return 0;
        }
    }

    /**
     * 品番マスタ情報一覧を取得する。
     * 
     * @param category 区分
     * @param productNo 品番
     * @param from 範囲先頭
     * @param to 範囲末尾
     * @return 品番マスタ情報一覧
     */
    public List<MstDsItemInfo> findRange(Integer category, String productNo, Long from, Long to) {
        logger.debug("findRange: {}", category);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH);
            sb.append(Paths.RANGE_PATH);
            sb.append(QUERY_PATH);
            sb.append("category=");
            sb.append(category);

            if (!StringUtils.isEmpty(productNo)) {
                sb.append("&productNo=");
                sb.append(productNo);
            }            

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(Paths.AND_PATH);
                sb.append(String.format(Paths.FROM_TO_PATH, from, to));
            }
            
            if (Objects.nonNull(loginUser.getId())) {
                sb.append(Paths.AND_PATH);
                sb.append(String.format(Paths.AUTHID_PATH, loginUser.getId()));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<MstDsItemInfo>>() {});

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
    
    /**
     * 品番マスタ情報を削除する。
     * 
     * @param id 品番マスタ情報ID
     * @return 処理結果
     */
    public SampleResponse delete(List<MstDsItemInfo> items) {
        logger.debug("delete");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH);
            sb.append(QUERY_PATH);

            // パラメータ
            sb.append("id=");
            sb.append(items.get(0).getProductId());
            for (int ii = 1; ii < items.size(); ii++) {
                sb.append("&id=");
                sb.append(items.get(ii).getProductId());
            }

            if (Objects.nonNull(loginUser.getId())) {
                sb.append(Paths.AND_PATH);
                sb.append(String.format(Paths.AUTHID_PATH, loginUser.getId()));
            }

            return (SampleResponse) restClient.delete(sb.toString(), SampleResponse.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new SampleResponse();
        }
    }

    /**
     * 品番マスタ情報を更新する。
     * 
     * @param entity 品番マスタ情報
     * @return 処理結果
     */
    public ResponseEntity update(MstDsItemInfo entity) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH);
            sb.append(QUERY_PATH);

            if (Objects.nonNull(loginUser.getId())) {
                sb.append(String.format(Paths.AUTHID_PATH, loginUser.getId()));
            }
            
            return (ResponseEntity) restClient.put(sb.toString(), entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }
}
