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
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.actual.WarehouseInventoryActualEntity;
import jp.adtekfuji.adFactory.entity.search.WarehouseInventorySearchCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 棚卸実績
 * @author nar-nakamura
 */
public class WarehouseInventoryFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String WAREHOUSE_INVENTORY_PATH = "/warehouse_inventory_actual";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String SEARCH_PATH = "/search";
    private final static String FROM_TO_PATH = "?from=%s&to=%s";
    private final static String LABELNO_PATH = "/labelno";
    private final static String LABELNO_QUERY = "?workflowId=%s&fromDate=%s&toDate=%s&affiliationCode=%s";

    /**
     * 棚卸実績
     */
    public WarehouseInventoryFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * 指定期間の棚卸実績の総数を取得する。
     * 
     * @param condition 検索条件
     * ※必須条件
     *   ・棚卸の工程順ID (InventoriesWorkflowId)
     *   ・払出の工程順ID (PayoutWorkflowId)
     *   ・棚卸開始日時 (FromDate)
     *   ・棚卸終了日時 (ToDate)
     * @return 棚卸実績数
     */
    public Long countSearch(WarehouseInventorySearchCondition condition) {
        logger.info("countSearch:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_INVENTORY_PATH);
            sb.append(SEARCH_PATH);
            sb.append(COUNT_PATH);
            return Long.parseLong((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return -1L;
    }

    /**
     * 指定期間の棚卸実績を取得する。
     *
     * @param condition 検索条件
     * ※必須条件
     *   ・棚卸の工程順ID (InventoriesWorkflowId)
     *   ・払出の工程順ID (PayoutWorkflowId)
     *   ・棚卸開始日時 (FromDate)
     *   ・棚卸終了日時 (ToDate)
     * @return 棚卸実績
     */
    public List<WarehouseInventoryActualEntity> findSearch(WarehouseInventorySearchCondition condition) {
        logger.info("findSearch:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_INVENTORY_PATH);
            sb.append(SEARCH_PATH);
            return restClient.put(sb.toString(), condition, new GenericType<List<WarehouseInventoryActualEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * 指定期間の棚卸実績を取得する。
     * 
     * @param condition 検索条件
     * ※必須条件
     *   ・棚卸の工程順ID (InventoriesWorkflowId)
     *   ・払出の工程順ID (PayoutWorkflowId)
     *   ・棚卸開始日時 (FromDate)
     *   ・棚卸終了日時 (ToDate)
     * @param from 取得開始インデックス
     * @param to 取得終了インデックス
     * @return 棚卸実績
     */
    public List<WarehouseInventoryActualEntity> findSearchRange(WarehouseInventorySearchCondition condition, Long from, Long to) {
        logger.info("findSearchRange:{},{},{}", condition, from, to);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_INVENTORY_PATH);
            sb.append(SEARCH_PATH);
            sb.append(RANGE_PATH);
            sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            return restClient.put(sb.toString(), condition, new GenericType<List<WarehouseInventoryActualEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }
    
    /**
     * 指定された期間・部品所属の最後の棚卸連番を取得する。
     * 
     * @param condition 検索条件
     * ※必須条件
     *   ・棚卸の工程順ID (InventoriesWorkflowId)
     *   ・払出の工程順ID (PayoutWorkflowId)
     *   ・棚卸開始日時 (FromDate)
     *   ・棚卸終了日時 (ToDate)
     *   ・部品所属コード (ActualAffiliationCode)
     * @return 最後の棚卸連番
     */
    public Long findSearchLabelNo(WarehouseInventorySearchCondition condition) {
        logger.info("getLabelNo:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_INVENTORY_PATH);
            sb.append(SEARCH_PATH);
            sb.append(LABELNO_PATH);
            return Long.parseLong((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return -1L;
    }

    /**
     * 指定された期間・部品所属の最後の棚卸連番を取得する。(高速版)
     *
     * @param workflowId 棚卸の工程順ID
     * @param fromDate 棚卸開始日 (yyyy-MM-dd)
     * @param toDate 棚卸終了日 (yyyy-MM-dd)
     * @param affiliationCode 部品所属コード
     * @return 
     */
    public Long findSearchLabelNo(Long workflowId, String fromDate, String toDate, String affiliationCode) {
        logger.info("getLabelNo: workflowId={}, fromDate={}, toDate={}, affiliationCode={}", workflowId, fromDate, toDate, affiliationCode);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_INVENTORY_PATH);
            sb.append(LABELNO_PATH);
            sb.append(String.format(LABELNO_QUERY, workflowId, fromDate, toDate, affiliationCode));
            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return -1L;
    }
}
