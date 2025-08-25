/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import adtekfuji.utility.DateUtils;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.SampleResponse;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.search.DeliveryCondition;
import jp.adtekfuji.adFactory.entity.search.LotTraceCondition;
import jp.adtekfuji.adFactory.entity.search.MaterialCondition;
import jp.adtekfuji.adFactory.entity.search.OperationLogCondition;
import jp.adtekfuji.adFactory.entity.warehouse.AvailableInventoryInfo;
import jp.adtekfuji.adFactory.entity.warehouse.LogStockInfo;
import jp.adtekfuji.adFactory.entity.warehouse.MstProductInfo;
import jp.adtekfuji.adFactory.entity.warehouse.ReserveInventoryParamInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnLotTraceInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnReserveMaterialInfo;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 倉庫案内 RESTクラス
 *
 * @author 14-0282
 */
public class WarehouseInfoFaced {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();

    private final static String WAREHOUSE_PATH = "/warehouse";
    private final static String LOCATION_PATH = "/location";
    private final static String PARTS_PATH = "/parts";
    private final static String BOM_PATH = "/bom";
    private final static String STOCK_PATH = "/stock";
    private final static String SUPPLY_PATH = "/supply";
    private final static String DELIVERY_PATH = "/delivery";
    private final static String IMPORT_PATH = "/import";
    private final static String PRODUCT_PATH = "/product";
    private final static String MATERIAL_PATH = "/material";
    private final static String PARTS_NO_PATH = "/partsNo";
    private final static String INVENRORY_PATH = "/inventory";
    private final static String START_PATH = "/start";
    private final static String COMPLETE_PATH = "/complete";
    private final static String CANCEL_PATH = "/cancel";
    private final static String CONFIRM_PATH = "/confirm";
    private final static String IN_STOCK_PATH = "/in-stock";

    /**
     * コンストラクタ
     */
    public WarehouseInfoFaced() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
        restClient.setReadTimeout(60 * 3 * 1000);
    }

    /**
     * コンストラクタ
     * 
     * @param uriBase パス
     */
    public WarehouseInfoFaced(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
        restClient.setReadTimeout(60 * 3 * 1000);
    }

    /**
     * 棚情報インポート
     *
     * @param filePath
     * @return
     */
    public ResponseEntity importLocation(String filePath) {
        logger.info("importFile: " + filePath);

        StringBuilder sb = new StringBuilder();
        sb.append(WAREHOUSE_PATH);
        sb.append(LOCATION_PATH);
        sb.append(IMPORT_PATH);

        return importFile(sb.toString(), filePath);
    }

    /**
     * 部品マスタ インポート
     *
     * @param filePath
     * @return
     */
    public ResponseEntity importPartMst(String filePath) {

        StringBuilder sb = new StringBuilder();
        sb.append(WAREHOUSE_PATH);
        sb.append(PARTS_PATH);
        sb.append(IMPORT_PATH);

        return importFile(sb.toString(), filePath);
    }

    /**
     * 部品構成マスタ インポート
     *
     * @param filePath
     * @return
     */
    public ResponseEntity importBom(String filePath) {

        StringBuilder sb = new StringBuilder();
        sb.append(WAREHOUSE_PATH);
        sb.append(BOM_PATH);
        sb.append(IMPORT_PATH);

        return importFile(sb.toString(), filePath);
    }

    /**
     * 保管方法マスタ インポート
     *
     * @param filePath
     * @return
     */
    public ResponseEntity importStock(String filePath) {

        StringBuilder sb = new StringBuilder();
        sb.append(WAREHOUSE_PATH);
        sb.append(STOCK_PATH);
        sb.append(IMPORT_PATH);

        return importFile(sb.toString(), filePath);
    }

    /**
     * 納入情報 インポート
     *
     * @param filePath
     * @return
     */
    public ResponseEntity importSupply(String filePath) {

        StringBuilder sb = new StringBuilder();
        sb.append(WAREHOUSE_PATH);
        sb.append(SUPPLY_PATH);
        sb.append(IMPORT_PATH);

        return importFile(sb.toString(), filePath);
    }

    /**
     * 出庫指示情報 インポート
     *
     * @param filePath
     * @return
     */
    public ResponseEntity importDelivery(String filePath) {

        StringBuilder sb = new StringBuilder();
        sb.append(WAREHOUSE_PATH);
        sb.append(DELIVERY_PATH);
        sb.append(IMPORT_PATH);

        return importFile(sb.toString(), filePath);
    }

    /**
     * 在庫情報のインポート
     *
     * @param filePath ファイルパス
     * @return 処理結果
     */
    public ResponseEntity importInStock(String filePath) {

        StringBuilder sb = new StringBuilder();
        sb.append(WAREHOUSE_PATH);
        sb.append("/instock");
        sb.append(IMPORT_PATH);

        return this.importFile(sb.toString(), filePath);
    }

    /**
     * 資材を受入・入庫する。
     * 
     * @param employeeNo 社員番号
     * @param supplyNo 図番
     * @param stockNum 入庫数
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @param isUpdate 更新フラグ
     * @param isSplit 分割フラグ
     * @return 処理結果
     */
    public ResponseEntity reciveWarehouse(String employeeNo, String supplyNo, Integer stockNum, String areaName, String locationNo, Boolean isUpdate, Boolean isSplit) {
        logger.info("reciveWarehouse");
        
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(WAREHOUSE_PATH);
            sb.append(MATERIAL_PATH);
            // 社員番号
            sb.append("?employeeNo=");
            if (StringUtils.isEmpty(employeeNo) && AuthorityEnum.SYSTEM_ADMIN.equals(loginUser.getAuthorityType())) {
                // デバッグモード
                sb.append(URLEncoder.encode(LoginUserInfoEntity.ADMIN_LOGIN_ID, "UTF-8"));
            } else {
                sb.append(URLEncoder.encode(employeeNo, "UTF-8"));
            }
            // 発注番号
            sb.append("&supplyNo=");
            sb.append(URLEncoder.encode(supplyNo, "UTF-8"));
            // 入庫数
            sb.append("&stockNum=");
            sb.append(stockNum);
            // 区画名
            sb.append("&areaName=");
            sb.append(URLEncoder.encode(areaName, "UTF-8"));
            // 棚番号
            if (!StringUtils.isEmpty(locationNo)) {
                sb.append("&locationNo=");
                sb.append(URLEncoder.encode(locationNo, "UTF-8"));
            }

            sb.append("&isUpdate=");
            sb.append(isUpdate);
            sb.append("&isSplit=");
            sb.append(isSplit);

            return (ResponseEntity) restClient.post(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * ファイルインポート REST呼び出し処理
     *
     */
    private ResponseEntity importFile(String path, String filePath) {
        logger.info("importFile: " + filePath);
        try {
            return (ResponseEntity) restClient.upload(path, filePath, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.UNKNOWN_ERROR);
        }
    }
    
    /**
     * 区画名一覧を取得する。
     * 
     * @return 区画名一覧
     */
    public List<String> findAllAreaName() {
        try {
            logger.info("findAllAreaName");

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/areaName");

            SampleResponse response = (SampleResponse) restClient.find(sb.toString(), SampleResponse.class);
            if (ServerErrorTypeEnum.SUCCESS != ServerErrorTypeEnum.valueOf(response.getStatus())) {
                return new ArrayList<>();
            }

            return response.getDataList();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
    
    /**
     * 部品番号の生成
     * 
     * @return 部品番号
     */
    public String nextPartsNo(){
        try{
            logger.info("nextPartsNo");

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append(MATERIAL_PATH);
            sb.append(PARTS_NO_PATH);

            return (String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
        }catch(Exception ex){
            logger.fatal(ex, ex);
            return new String();
        }
    }
    
    /**
     * 部品マスタ情報を取得する。
     * 
     * @param figureNo
     * @param productNo
     * @return 
     */
    public MstProductInfo findProduct(String figureNo, String productNo) {
        try {
            logger.info("findProduct: { }", figureNo, productNo);
        
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append(PRODUCT_PATH);
            
            // パラメータ
            sb.append("?figureNo=");
            if (Objects.nonNull(figureNo) && !figureNo.isEmpty()) {
                sb.append(URLEncoder.encode(figureNo, "UTF-8"));
            }
            sb.append("&productNo=");
            if (Objects.nonNull(productNo) && !productNo.isEmpty()) {
                sb.append(URLEncoder.encode(productNo, "UTF-8"));
            }

            return (MstProductInfo) restClient.find(sb.toString(), MstProductInfo.class);
           
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new MstProductInfo();
        }
    }
    
    /**
     * 資材情報の件数を取得する。
     * 
     * @param condition 検索条件
     * @return 件数
     */
    public Integer countMaterials(MaterialCondition condition) {
        try {
            logger.info("countMaterials: {}", condition);

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/material/search/count");

            return Integer.parseInt((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0;
        }
    }

    /**
     * 資材情報を検索する。
     * 
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 資材情報一覧
     */
    public List<TrnMaterialInfo> searchMaterials(MaterialCondition condition, Integer from, Integer to) {
        try {
            logger.info("searchMaterials: {} {} {}", condition, from, to);

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/material/search/range");

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(String.format("?from=%s&to=%s", String.valueOf(from), String.valueOf(to)));
            }

            return restClient.put(sb.toString(), condition, new GenericType<List<TrnMaterialInfo>>(){});

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
    
    /**
     * 入出庫実績情報を取得する。
     * 
     * @param materialNos 資材番号一覧
     * @return 入出庫実績情報一覧
     */
    public List<LogStockInfo> findLogStock(List<String> materialNos) {
        try {
            if (Objects.isNull(materialNos) || materialNos.isEmpty()) {
                return new ArrayList<>();
            }

            logger.info("findLogStock: " + materialNos.size());
        
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/logStock");
            
            // パラメータ
            sb.append("?id=");
            sb.append(RestClient.encode(materialNos.get(0)));
            for (int ii = 1; ii < materialNos.size(); ii++) {
                sb.append("&id=");
                sb.append(RestClient.encode(materialNos.get(ii)));
            }

            return restClient.find(sb.toString(), new GenericType<List<LogStockInfo>>(){});
           
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
    
    /**
     * 未同期の入出庫実績情報を取得する。
     * 
     * @param synced 同期フラグ
     * @param max 最大件数
     * @return 入出庫実績情報一覧
     */
    public List<LogStockInfo> findSyncedLogStock(boolean synced, int max) {
        try {
            logger.info("findSyncedLogStock");// ※ {}で引数の出力を行なうとエラーになるので注意。
        
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/logStock/synced");
            
            // パラメータ
            sb.append("?synced=");
            sb.append(synced);
            sb.append("&max=");
            sb.append(max);
            
            return restClient.find(sb.toString(), new GenericType<List<LogStockInfo>>(){});

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
 
    /**
     * 入出庫実績情報の同期フラグを更新する。
     * 
     * @param eventIds イベントID一覧
     * @param synced 同期フラグ
     * @return 更新件数
     */
    public int updateSyncedLogStock(List<Long> eventIds, Boolean synced) {
        try {
            logger.info("updateSyncedLogStock");// ※ {}で引数の出力を行なうとエラーになるので注意。
        
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/logStock/synced");    
            
            // パラメータ
            sb.append("?synced=");
            sb.append(synced);
            sb.append("&id=");
            sb.append(eventIds.get(0));
            for (int ii = 1; ii < eventIds.size(); ii++) {
                sb.append("&id=");
                sb.append(eventIds.get(ii));
            }            

            return Integer.parseInt((String) restClient.put(sb.toString(), null, MediaType.TEXT_PLAIN_TYPE, String.class));
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0;
        }
    }

    /**
     * ロットトレース情報の件数を取得する。
     * 
     * @param condition 検索条件
     * @return 件数
     */
    public Integer countLotTrace(LotTraceCondition condition) {
        try {
            logger.info("countLotTrace: {}", condition);

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/lot-trace/search/count");

            return Integer.parseInt((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0;
        }
    }

    /**
     * ロットトレース情報を検索する。
     * 
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return ロットトレース情報一覧
     */
    public List<TrnLotTraceInfo> searchLotTrace(LotTraceCondition condition, Integer from, Integer to) {
        try {
            logger.info("searchLotTrace: {} {} {}", condition, from, to);

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/lot-trace/search/range");

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(String.format("?from=%s&to=%s", String.valueOf(from), String.valueOf(to)));
            }

            return restClient.put(sb.toString(), condition, new GenericType<List<TrnLotTraceInfo>>(){});

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定した区画の棚卸を開始して、棚卸作業ができるようにする。
     *
     * @param areaName 区画名　※.省略時は全区画
     * @param reset 棚卸結果を消去して開始する？(null: 結果が存在する場合はエラー, true: 消去して開始, false: 消去せず開始)
     * @param authId 認証ID　※必須
     * @return 結果
     */
    public ResponseEntity inventoryStart(String areaName, Boolean reset, Long authId) {
        logger.info("inventoryStart: areaName={}, reset={}, authId={}", areaName, reset, authId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append(INVENRORY_PATH);
            sb.append(START_PATH);

            // パラメータ
            sb.append("?authId=");
            sb.append(authId);

            if (!StringUtils.isEmpty(areaName)) {
                sb.append("&area=");
                sb.append(URLEncoder.encode(areaName, StandardCharsets.UTF_8.name()));
            }

            if (Objects.nonNull(reset)) {
                sb.append("&reset=");
                sb.append(reset);
            }

            return (ResponseEntity) restClient.put(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 指定した区画の棚卸を完了して、棚卸結果を資材情報に反映する。
     *
     * @param areaName 区画名　※.省略時は全区画
     * @param authId 認証ID　※必須
     * @param timeout タイムアウト時間(ms)
     * @return 結果
     */
    public ResponseEntity inventoryComplete(String areaName, Long authId, int timeout) {
        logger.info("inventoryComplete: areaName={}, authId={}", areaName, authId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append(INVENRORY_PATH);
            sb.append(COMPLETE_PATH);

            // パラメータ
            sb.append("?authId=");
            sb.append(authId);

            if (!StringUtils.isEmpty(areaName)) {
                sb.append("&area=");
                sb.append(URLEncoder.encode(areaName, StandardCharsets.UTF_8.name()));
            }

            return (ResponseEntity) restClient.putWithTimeout(sb.toString(), null, ResponseEntity.class, timeout);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 指定した区画の棚卸を中止する。
     *
     * @param areaName 区画名　※.省略時は全区画
     * @param authId 認証ID　※必須
     * @return 結果
     */
    public ResponseEntity inventoryCancel(String areaName, Long authId) {
        logger.info("inventoryCancel: areaName={}, authId={}", areaName, authId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append(INVENRORY_PATH);
            sb.append(CANCEL_PATH);

            // パラメータ
            sb.append("?authId=");
            sb.append(authId);

            if (!StringUtils.isEmpty(areaName)) {
                sb.append("&area=");
                sb.append(URLEncoder.encode(areaName, StandardCharsets.UTF_8.name()));
            }

            return (ResponseEntity) restClient.put(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 指定した資材情報の在庫数を更新する。
     *
     * @param materialNo 資材番号
     * @param quantity 在庫数
     * @param authId 認証ID(更新者)
     * @return 結果
     */
    public ResponseEntity updateMaterialInStock(String materialNo, Integer quantity, Long authId) {
        logger.info("updateMaterialInStock: materialNo={}, quantity={}, authId={}", materialNo, quantity, authId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append(MATERIAL_PATH);
            sb.append(IN_STOCK_PATH);

            // パラメータ
            sb.append("?id=");
            sb.append(RestClient.encode(materialNo));
            sb.append("&quantity=");
            sb.append(quantity);
            sb.append("&authId=");
            sb.append(authId);

            return (ResponseEntity) restClient.put(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.UNKNOWN_ERROR);
        }
    }
    
    /**
     * 検索条件を指定して、出庫指示情報の件数を取得する。
     * 
     * @param condition 出庫指示情報の検索条件
     * @return 出庫指示情報の件数
     */
    public Integer countDelivery(DeliveryCondition condition) {
        try {
            logger.info("countDelivery: {}", condition);

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/delivery/search/count");

            return Integer.parseInt((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0;
        }
    }
    
    /**
     * 検索条件を指定して、出庫指示情報一覧を取得する。
     * 
     * @param condition 出庫指示情報の検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 出庫指示情報一覧
     */
    public List<TrnDeliveryInfo> searchDeliveryRange(DeliveryCondition condition, Long from, Long to) {
        try {
            logger.info("searchDeliveryRange: {} {} {}", condition, from, to);

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/delivery/search/range");

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(String.format("?from=%s&to=%s", String.valueOf(from), String.valueOf(to)));
            }

            return restClient.put(sb.toString(), condition, new GenericType<List<TrnDeliveryInfo>>(){});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

   /**
     * 検索条件を指定して、作業ログ情報の件数を取得する。
     * 
     * @param condition 作業ログ情報の検索条件
     * @return 作業ログ情報の件数
     */
    public Integer countOperationLog(OperationLogCondition condition) {
        try {
            logger.info("countOperationLog: {}", condition);

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/operation-log/search/count");

            return Integer.parseInt((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0;
        }
    }
    
    /**
     * 検索条件を指定して、作業ログ情報一覧を取得する。
     * 
     * @param condition 作業ログ情報の検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 作業ログ情報一覧
     */
    public List<LogStockInfo> searchOperationLog(OperationLogCondition condition, Long from, Long to) {
        try {
            logger.info("searchOperationLog: {} {} {}", condition, from, to);

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/operation-log/search/range");

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(String.format("?from=%s&to=%s", String.valueOf(from), String.valueOf(to)));
            }

            return restClient.put(sb.toString(), condition, new GenericType<List<LogStockInfo>>(){});

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 在庫引当を行う。
     * 
     * @param deliveryNos 払出指示番号
     * @param areaName 区画名
     * @return 処理結果
     */
    public ResponseEntity reserveInventoryAuto(List<String> deliveryNos, String areaName) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/inventory/reserve/auto");

            // パラメータ
            sb.append("?id=");
            sb.append(RestClient.encode(deliveryNos.get(0)));
            for (int ii = 1; ii < deliveryNos.size(); ii++) {
                sb.append("&id=");
                sb.append(RestClient.encode(deliveryNos.get(ii)));
            }
            
            sb.append("&areaName=");
            sb.append(RestClient.encode(areaName));
            
            LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
            if(Objects.nonNull(loginUser.getId())) {
                sb.append(String.format("&authId=%d", loginUser.getId()));
            }

            return (ResponseEntity) restClient.post(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 在庫引当を行う。
     * 
     * @param param 在庫引当情報
     * @return 処理結果
     */
    public ResponseEntity reserveInventory(ReserveInventoryParamInfo param) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/inventory/reserve?");
           
            LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
            if(Objects.nonNull(loginUser.getId())) {
                sb.append(String.format("&authId=%d", loginUser.getId()));
            }

            return (ResponseEntity) restClient.post(sb.toString(), param, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 在庫引当を解除する。
     * 
     * @param deliveryNos 払出指示番号
     * @return 
     */
    public ResponseEntity releaseReservation(List<String> deliveryNos) {
        logger.info("releaseReservation: ");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/inventory/release");

            // パラメータ
            sb.append("?id=");
            sb.append(RestClient.encode(deliveryNos.get(0)));
            for (int ii = 1; ii < deliveryNos.size(); ii++) {
                sb.append("&id=");
                sb.append(RestClient.encode(deliveryNos.get(ii)));
            }

            return (ResponseEntity) restClient.put(sb.toString(), null, ResponseEntity.class);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 在庫引当を解除する。
     * 
     * @param deliveryNo 払出指示番号
     * @param itemNo
     * @return 
     */
    public ResponseEntity releaseReservation(String deliveryNo, Integer itemNo) {
        logger.info("releaseReservation: deliveryNo={}, itemNo={}", deliveryNo, itemNo);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/inventory/item/release");

            // パラメータ
            sb.append("?id=");
            sb.append(RestClient.encode(deliveryNo));
            sb.append("&no=");
            sb.append(itemNo);

            LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
            if(Objects.nonNull(loginUser.getId())) {
                sb.append(String.format("&authId=%d", loginUser.getId()));
            }

            return (ResponseEntity) restClient.put(sb.toString(), null, ResponseEntity.class);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.UNKNOWN_ERROR);
        }
    }
    
    /**
     * 払出指示情報を削除する。
     * 
     * @param deliveries 払出指示情報
     * @return 処理結果
     */
    public SampleResponse delete(List<TrnDeliveryInfo> deliveries) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/delivery");

            // パラメータ
            sb.append("?id=");
            sb.append(RestClient.encode(deliveries.get(0).getDeliveryNo()));
            for (int ii = 1; ii < deliveries.size(); ii++) {
                sb.append("&id=");
                sb.append(RestClient.encode(deliveries.get(ii).getDeliveryNo()));
            }

            LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
            if(Objects.nonNull(loginUser.getId())) {
                sb.append(String.format("&authId=%d", loginUser.getId()));
            }

            return (SampleResponse) restClient.delete(sb.toString(), SampleResponse.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new SampleResponse(ServerErrorTypeEnum.SERVER_FETAL.name(), null);
        }
    }
    
    /**
     * 有効在庫情報を取得する。
     * 有効在庫とは、現在庫数から引当済み在庫、受注残、不良品を差し引いた論理在庫のこと。
     * 
     * @param deliveryNo 出庫番号
     * @param itemNo 明細番号
     * @param areaName 区画名
     * @return 有効在庫情報一覧
     */
    public List<AvailableInventoryInfo> findAvailableInventory(String deliveryNo, Integer itemNo, String areaName) {
        try {
            logger.info("findAvailableInventory: {} {} {}", deliveryNo, itemNo, areaName);

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/inventory/available");

            // パラメータ
            sb.append("?deliveryNo=");
            sb.append(RestClient.encode(deliveryNo));
            sb.append("&itemNo=");
            sb.append(itemNo);
            sb.append("&areaName=");
            sb.append(RestClient.encode(areaName));

            LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
            if(Objects.nonNull(loginUser.getId())) {
                sb.append(String.format("&authId=%d", loginUser.getId()));
            }

            return restClient.find(sb.toString(), new GenericType<List<AvailableInventoryInfo>>(){});

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
    
    /**
     * 払出ステータスを更新する。
     * 
     * @param deliveryNos
     * @param status
     * @return 
     */
    public ResponseEntity updateDaliveryStatus(List<String> deliveryNos, DeliveryStatusEnum status) {
        logger.debug("updateDaliveryStatus: deliveryNos={}, status={}", deliveryNos, status);
        try {
            if (deliveryNos.isEmpty()) {
                return new ResponseEntity();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/delivery/status");
            sb.append("?id=");
            sb.append(RestClient.encode(deliveryNos.get(0)));
            for (int i = 1; i < deliveryNos.size(); i++) {
                sb.append("&id=");
                sb.append(RestClient.encode(deliveryNos.get(i)));
            }
            sb.append("&status=");
            sb.append(status);

            LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
            if(Objects.nonNull(loginUser.getId())) {
                sb.append(String.format("&authId=%d", loginUser.getId()));
            }

            return (ResponseEntity) restClient.put(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 払出指示の払出予定日を更新する。
     * 
     * @param deliveryNos 払出指示番号
     * @param date 払出予定日
     * @return 処理結果
     */
    public ResponseEntity updateDaliveryDate(List<String> deliveryNos, Date date) {
        logger.debug("updateStatus: deliveryNos={}, date={}", deliveryNos, date);
        try {
            if (deliveryNos.isEmpty()) {
                return new ResponseEntity();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/delivery/date");
            sb.append("?id=");
            sb.append(RestClient.encode(deliveryNos.get(0)));
            for (int i = 1; i < deliveryNos.size(); i++) {
                sb.append("&id=");
                sb.append(RestClient.encode(deliveryNos.get(i)));
            }
            sb.append("&date=");
            sb.append(RestClient.encode(DateUtils.format(date)));

            LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
            if(Objects.nonNull(loginUser.getId())) {
                sb.append(String.format("&authId=%d", loginUser.getId()));
            }

            return (ResponseEntity) restClient.put(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.UNKNOWN_ERROR);
        }
    }
    
    /**
     * 該当する資材番号の在庫引当情報を取得する。
     * 
     * @param materialNo 資材番号
     * @return 在庫引当情報一覧
     */
    public List<TrnReserveMaterialInfo> findReserveMaterials(String materialNo) {
        try {
            logger.info("findReserveMaterials: {}", materialNo);

            StringBuilder sb = new StringBuilder();
            sb.append(WAREHOUSE_PATH);
            sb.append("/inventory/reserve");

            // パラメータ
            sb.append("?materialNo=");
            sb.append(RestClient.encode(materialNo));

            LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
            if(Objects.nonNull(loginUser.getId())) {
                sb.append(String.format("&authId=%d", loginUser.getId()));
            }

            return restClient.find(sb.toString(), new GenericType<List<TrnReserveMaterialInfo>>(){});

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
}
