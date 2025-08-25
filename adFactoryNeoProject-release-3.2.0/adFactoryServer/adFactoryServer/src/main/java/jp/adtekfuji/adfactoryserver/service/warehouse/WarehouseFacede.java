/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service.warehouse;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.SampleResponse;
import jp.adtekfuji.adFactory.entity.search.DeliveryCondition;
import jp.adtekfuji.adFactory.entity.search.LotTraceCondition;
import jp.adtekfuji.adFactory.entity.search.MaterialCondition;
import jp.adtekfuji.adFactory.entity.search.OperationLogCondition;
import jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.warehouse.AvailableInventoryEntity;
import jp.adtekfuji.adfactoryserver.entity.warehouse.DeliveryParam;
import jp.adtekfuji.adfactoryserver.entity.warehouse.LogStock;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstProduct;
import jp.adtekfuji.adfactoryserver.entity.warehouse.ReserveInventoryParam;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDelivery;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDeliveryItem;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnLotTrace;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnMaterial;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnReserveMaterial;
import jp.adtekfuji.adfactoryserver.model.warehouse.WarehouseModel;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * 倉庫案内ファサード
 * 倉庫案内のクライアントの窓口となるクラスです。
 * 
 * @see <a href="https://ja.wikipedia.org/wiki/Facade_パターン">Facade パターン</a>
 * @author s-heya
 */
@Singleton
@Path("warehouse")
public class WarehouseFacede {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();
    
    @Inject
    private WarehouseModel model;
    
    /**
     * 倉庫案内を初期化する。
     * 
     */
    @PostConstruct
    public void initialize() {
        logger.info("WarehouseFacede initialized.");

        // 棚マスタを読み込み
    }
   
    /**
     * 棚マスタをインポートする。
     * 
     * @param inputStreams 入力ストリーム
     * @param authId 認証ID
     * @return 処理結果
     */
    @POST
    @Path(value="location/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_XML)
    public Response importLocation(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("authId") Long authId) {
        if (Objects.isNull(inputStreams) || inputStreams.isEmpty()) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        InputStream inputStream = inputStreams.get(0);
        return model.importLocation(inputStream, null);
    }
    
    /**
     * 部品マスタをインポートする。
     * 
     * @param inputStreams 入力ストリーム
     * @param authId 認証ID
     * @return 処理結果
     */
    @POST
    @Path(value="parts/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_XML)
    public Response importParts(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("authId") Long authId) {
        if (Objects.isNull(inputStreams) || inputStreams.isEmpty()) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        InputStream inputStream = inputStreams.get(0);
        return model.importParts(inputStream, null);
    }

    /**
     * 部品構成マスタをインポートする。
     * 
     * @param inputStreams 入力ストリーム
     * @param authId 認証ID
     * @return 処理結果
     */
    @POST
    @Path(value="bom/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_XML)
    public Response importBOM(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("authId") Long authId) {
        if (Objects.isNull(inputStreams) || inputStreams.isEmpty()) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        InputStream inputStream = inputStreams.get(0);
        return model.importBOM(inputStream, null);
    }

    /**
     * 保管方法マスタをインポートする。
     * 
     * @param inputStreams 入力ストリーム
     * @param authId 認証ID
     * @return 処理結果
     */
    @POST
    @Path(value="stock/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_XML)
    public Response importStock(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("authId") Long authId) {
        if (Objects.isNull(inputStreams) || inputStreams.isEmpty()) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        InputStream inputStream = inputStreams.get(0);
        return model.importStock(inputStream, null);
    }

    /**
     * 納入情報及び、支給品情報をインポートする。
     * 
     * @param inputStreams 入力ストリーム
     * @param authId 認証ID
     * @return 処理結果
     */
    @POST
    @Path(value="supply/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_XML)
    public Response importSupply(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("authId") Long authId) {
        if (Objects.isNull(inputStreams) || inputStreams.isEmpty()) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }
        
        InputStream inputStream = inputStreams.get(0);
        return model.importSupply(inputStream, null);
    }

    /**
     * 出庫指示情報をインポートする。
     * 
     * @param inputStreams 入力ストリーム
     * @param authId 認証ID
     * @return 処理結果
     */
    @POST
    @Path(value="delivery/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_XML)
    public Response importDelivery(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("authId") Long authId) {
        if (Objects.isNull(inputStreams) || inputStreams.isEmpty()) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }
        
        InputStream inputStream = inputStreams.get(0);
        return model.importDelivery(inputStream, null);
    }

    /**
     * 在庫情報をインポートする。
     * 
     * @param inputStreams 入力ストリーム
     * @param authId 認証ID
     * @return 処理結果
     */
    @POST
    @Path(value="instock/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_XML)
    public Response importInStock(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("authId") Long authId) {
        if (Objects.isNull(inputStreams) || inputStreams.isEmpty()) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }
        
        InputStream inputStream = inputStreams.get(0);
        return model.importInStock(inputStream, null);
    }

    /**
     * 資材情報の件数を取得する。
     * 
     * @param condition 検索条件
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("material/search/count")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public String countMaterials(MaterialCondition condition, @QueryParam("authId") Long authId) {
        return model.countMaterials(condition);
    }

    /**
     * 資材情報を検索する。
     * 
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 資材情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("material/search/range")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<TrnMaterial> searchMaterials(MaterialCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        List<TrnMaterial> list = model.searchMaterials(condition, from, to);
        logger.info("searchMaterials: " + list.size());
        return list;
    }

    /**
     * 入出庫実績情報を取得する。
     * 
     * @param materialNos 資材番号一覧
     * @param authId 認証ID
     * @return 入出庫実績情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("logStock")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<LogStock> findLogStock(@QueryParam("id") List<String> materialNos, @QueryParam("authId") Long authId) {
        return model.findLogStock(materialNos);
    }

    /**
     * 未同期の入出庫実績情報を取得する。
     * 
     * @param synced 同期フラグ
     * @param max 最大件数
     * @param authId 認証ID
     * @return 入出庫実績情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("logStock/synced")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<LogStock> findSyncedLogStock(@QueryParam("synced") Boolean synced, @QueryParam("max") Integer max, @QueryParam("authId") Long authId) {
        logger.info("getSyncLog: " + synced);
        if (Objects.isNull(synced)) {
            synced = false;
        }
        return model.findSyncedLogStock(synced, max);
    }

    /**
     * 入出庫実績情報の同期フラグを更新する。
     * 
     * @param eventIds イベントID一覧
     * @param synced 同期フラグ
     * @param authId 認証ID
     * @return 更新件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("logStock/synced")
    @Produces({MediaType.TEXT_PLAIN})
    public String updateSyncedLogStock(@QueryParam("id") List<Long> eventIds, @QueryParam("synced") Boolean synced, @QueryParam("authId") Long authId) {
        logger.info("updateSyncedLogStock: " + synced);
        int count = model.updateSyncedLogStock(eventIds, synced);
        return String.valueOf(count);
    }

    /**
     * 区画名一覧を取得する。
     * 
     * @param authId 認証ID
     * @return 区画名一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("areaName")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public SampleResponse findAllAreaName(@QueryParam("authId") Long authId) {
        List<String> areaNames = model.getAreaNames();
        if (areaNames.isEmpty()) {
            areaNames.add(LocaleUtils.getString("warehouse.DefaultAreaName"));
        }
        
        SampleResponse response =  new SampleResponse(ServerErrorTypeEnum.SUCCESS.name(), areaNames);
        //return Response.ok().entity(response).build();
        return response;
    }
    
    /**
     * 図番をキーにして部品マスタを取得する。
     * 
     * @param figureNo 図番
     * @param productNo 品目
     * @param authId 認証ID
     * @return 部品マスタ
     */
    @Lock(LockType.READ)
    @GET
    @Path("product")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public MstProduct findProduct(@QueryParam("figureNo") String figureNo, @QueryParam("productNo") String productNo, @QueryParam("authId") Long authId) {
        if (!StringUtils.isEmpty(figureNo)) {
            return this.model.findProductByFigureNo(figureNo);
        }
        if (!StringUtils.isEmpty(productNo)) {
            return this.model.findProduct(productNo);
        }
        return null;
    }
    
    /**
     * 部品番号を生成する。
     * 
     * @param authId 認証ID
     * @return 部品番号
     */
    @GET
    @Path("material/partsNo")
    @Produces({MediaType.TEXT_PLAIN})
    public String nextPartsNo(@QueryParam("authId") Long authId) {
        return null;
    }
    
    /**
     * 資材を受入・入庫する。<br>
     * 資材情報 又は、部品マスタが未登録の場合、それらを登録してから受入・入庫すする。
     * 
     * @param employeeNo 社員番号
     * @param supplyNo
     * @param figureNo 図番
     * @param productNo 品目
     * @param partsNo 部品番号
     * @param serialNo 製造番号
     * @param stockNum 入庫数
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @param isUpdate 更新フラグ
     * @param isSplit 
     * @param authId 認証ID
     * @return 処理結果
     */
    @POST
    @Path("material")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response reciveWarehouse(@QueryParam("employeeNo") String employeeNo, 
            @QueryParam("supplyNo") String supplyNo, 
            @QueryParam("figureNo") String figureNo, 
            @QueryParam("productNo") String productNo, 
            @QueryParam("partsNo") String partsNo, 
            @QueryParam("serialNo") String serialNo, 
            @QueryParam("stockNum") Integer stockNum, 
            @QueryParam("areaName") String areaName, 
            @QueryParam("locationNo") String locationNo, 
            @QueryParam("isUpdate") Boolean isUpdate, 
            @QueryParam("isSplit") Boolean isSplit, 
            @QueryParam("authId") Long authId) {
        
        logger.info("reciveWarehouse: ");
        
        Response response = this.model.reciveWarehouse(employeeNo, supplyNo, stockNum, areaName);
        if (response.getStatus() != HttpURLConnection.HTTP_OK) {
            return response;
        }
        
        TrnMaterial material = this.model.findMaterialBySupplyNo(supplyNo);
        if (Objects.isNull(material)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_MATERIAL)).build();
        }
       
        if (Boolean.TRUE.equals(isSplit)) {
            // 分納の場合、受入場に移動する
            Response moveRes = this.model.moveWarehouse(employeeNo, material.getMaterialNo(), stockNum, areaName, this.model.getReceivingLocationNo());
            if (moveRes.getStatus() != HttpURLConnection.HTTP_OK) {
                return moveRes;
            }
 
            material = (TrnMaterial) moveRes.getEntity();
            logger.info("Created TrnMaterial: {}", material);
        }

        return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS).resources(material.getMaterialNo())).build();
    }
    
    /**
     * 
     * @param employeeNo 社員番号
     * @param materialNo 資材番号
     * @param moveNum 移動数
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @param authId
     * @return 
     */
    @POST
    @Path("material/move")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response moveWarehouse(@QueryParam("employeeNo") String employeeNo, @QueryParam("materialNo") String materialNo, @QueryParam("moveNum") Integer moveNum, 
            @QueryParam("areaName") String areaName, @QueryParam("locationNo") String locationNo, @QueryParam("authId") Long authId) {
        return this.model.moveWarehouse(employeeNo, materialNo, moveNum, areaName, locationNo);
    }
    /**
     * 検索条件を指定して、ロットトレース一覧を取得する。
     *
     * @param condition ロットトレースの検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return ロットトレース一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("lot-trace/search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<TrnLotTrace> searchLotTraceRange(LotTraceCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchLotTraceRange: {}, from={}, to={}, authId={}", condition, from, to, authId);
        return this.model.searchLotTraceRange(condition, from, to, authId);
    }

    /**
     * 検索条件を指定して、ロットトレースの件数を取得する。
     *
     * @param condition ロットトレースの検索条件
     * @param authId 認証ID
     * @return ロットトレースの件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("lot-trace/search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({MediaType.TEXT_PLAIN})
    @ExecutionTimeLogging
    public String countLotTrace(LotTraceCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countLotTrace: {}, authId={}", condition, authId);
        return this.model.countLotTrace(condition, authId);
    }

    /**
     * 検索条件を指定して、出庫情報一覧を取得する。
     *
     * @param condition 出庫情報の検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 出庫情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("delivery/item/search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<TrnDeliveryItem> searchDeliveryItemRange(DeliveryCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchDeliveryItemRange: {}, from={}, to={}, authId={}", condition, from, to, authId);
        List<TrnDeliveryItem> ret = this.model.searchDeliveryItemRange(condition, from, to, authId);
        ret.stream()
                .map(TrnDeliveryItem::getProduct)
                .forEach(item -> {
                    item.setMaterialList(null);
                    item.setStockList(null);
                });
        return ret;
    }
    
    /**
     * 検索条件を指定して、出庫情報の件数を取得する。
     *
     * @param condition 出庫情報の検索条件
     * @param authId 認証ID
     * @return 出庫情報の件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("delivery/item/search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({MediaType.TEXT_PLAIN})
    @ExecutionTimeLogging
    public String countDeliveryItems(DeliveryCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countDeliveryItems: {}, authId={}", condition, authId);
        return this.model.countDeliveryItems(condition, authId);
    }
    
    /**
     * 資材情報を取得する。
     * 
     * @param materialNo 資材番号
     * @param authId 認証ID
     * @return 資材情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("material")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public TrnMaterial findMaterial(@QueryParam("id") String materialNo, @QueryParam("authId") Long authId) {
        logger.info("findMaterial: {}, authId={}", materialNo, authId);
        return this.model.findMaterial(materialNo);
    }

    /**
     * 指定した区画の棚卸を開始して、棚卸作業ができるようにする。
     *
     * @param areaName 区画名　※.省略時は全区画
     * @param reset 棚卸結果を消去して開始する？(null: 結果が存在する場合はエラー, true: 消去して開始, false: 消去せず開始)
     * @param authId 認証ID　※必須
     * @return 結果
     */
    @Lock(LockType.READ)
    @PUT
    @Path("inventory/start")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response inventoryStart(@QueryParam("area")String areaName, @QueryParam("reset")Boolean reset, @QueryParam("authId") Long authId) {
        return this.model.inventoryStart(areaName, reset, authId);
    }

    /**
     * 指定した区画の棚卸を完了して、棚卸結果を資材情報に反映する。
     *
     * @param areaName 区画名　※.省略時は全区画
     * @param authId 認証ID　※必須
     * @return 結果
     */
    @Lock(LockType.READ)
    @PUT
    @Path("inventory/complete")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response inventoryComplete(@QueryParam("area")String areaName, @QueryParam("authId") Long authId) {
        return this.model.inventoryComplete(areaName, authId);
    }

    /**
     * 指定した区画の棚卸を中止する。
     *
     * @param areaName 区画名　※.省略時は全区画
     * @param authId 認証ID　※必須
     * @return 結果
     */
    @Lock(LockType.READ)
    @PUT
    @Path("inventory/cancel")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response inventoryCancel(@QueryParam("area")String areaName, @QueryParam("authId") Long authId) {
        return this.model.inventoryCancel(areaName, authId);
    }

    /**
     * 指定した資材情報の在庫数を更新する。
     *
     * @param materialNo 資材番号
     * @param quantity 在庫数
     * @param authId 認証ID(更新者)
     * @return 結果
     */
    @PUT
    @Path("material/in-stock")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response updateMaterialInStock(@QueryParam("id")String materialNo, @QueryParam("quantity")Integer quantity, @QueryParam("authId") Long authId) {
        return this.model.updateMaterialInStock(materialNo, quantity, authId);
    }
    
    /**
     * 検索条件を指定して、出庫情報一覧を取得する。
     *
     * @param condition 出庫情報の検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 出庫情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("delivery/search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<TrnDelivery> searchDeliveryRange(DeliveryCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchDeliveryRange: {}, from={}, to={}, authId={}", condition, from, to, authId);
        return this.model.searchDeliveryRange(condition, from, to, authId);
    }
    
    /**
     * 検索条件を指定して、出庫情報の件数を取得する。
     *
     * @param condition 出庫情報の検索条件
     * @param authId 認証ID
     * @return 出庫情報の件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("delivery/search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({MediaType.TEXT_PLAIN})
    @ExecutionTimeLogging
    public String countDelivery(DeliveryCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countLotTrace: {}, authId={}", condition, authId);
        return this.model.countDelivery(condition, authId);
    }

    /**
     * 作業ログ情報を検索する。
     * 
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 作業ログ一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("operation-log/search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<LogStock> searchOperationLogRange(OperationLogCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchOperationLogRange: {}, from={}, to={}, authId={}", condition, from, to, authId);
        return this.model.searchOperationLog(condition, from, to);
    }
    
    /**
     * 検索条件に一致した作業ログ情報を件数を取得する。
     *
     * @param condition 検索条件
     * @param authId 認証ID
     * @return 作業ログ情報の件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("operation-log/search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({MediaType.TEXT_PLAIN})
    @ExecutionTimeLogging
    public String countOperationLog(OperationLogCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countOperationLog: {}, authId={}", condition, authId);
        return this.model.countOperationLog(condition);
    }

    /**
     * 払出指示情報を追加する。
     * 
     * @param param 払出指示情報
     * @param authId 認証ID
     * @return 処理結果
     */
    @POST
    @Path("delivery")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public Response addDelivery(@QueryParam("param") DeliveryParam param, @QueryParam("authId") Long authId) {
        return model.addDelivery(param, authId);
    }

    /**
     * 払出指示情報を削除する。
     * 
     * @param deliveryNos 払出指示番号一覧
     * @param authId 認証ID
     * @return 処理結果
     */
    @DELETE
    @Path("delivery")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public SampleResponse removeDelivery(@QueryParam("id") List<String> deliveryNos, @QueryParam("authId") Long authId) {
        return model.removeDelivery(deliveryNos);
    }

    /**
     * 自動引当を行う。
     * 
     * @param deliveryNos 払出指示番号
     * @param areaName 区画名
     * @param authId 認証ID
     * @return 処理結果
     */
    @POST
    @Path("inventory/reserve/auto")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response reserveInventoryAuto(@QueryParam("id") List<String> deliveryNos, @QueryParam("areaName") String areaName, @QueryParam("authId") Long authId) {
        return model.reserveInventoryAuto(deliveryNos, areaName, authId);
    }

    /**
     * 在庫引当を行う。
     * 
     * @param param 在庫引当情報
     * @param authId 認証ID
     * @return 処理結果
     */
    @POST
    @Path("inventory/reserve")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response reserveInventory(ReserveInventoryParam param, @QueryParam("authId") Long authId) {
        return model.reserveInventory(param, authId);
    }

    /**
     * 在庫引当を解除する。
     * 
     * @param deliveryNos 払出指示番号
     * @param authId
     * @return 
     */
    @PUT
    @Path("inventory/release")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response releaseReservationAll(@QueryParam("id") List<String> deliveryNos, @QueryParam("authId") Long authId) {
        return model.releaseReservationAll(deliveryNos);
    }

    /**
     * 在庫引当を解除する。
     * 
     * @param deliveryNo 払出指示番号
     * @param itemNo
     * @param authId
     * @return 
     */
    @PUT
    @Path("inventory/item/release")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response releaseReservation(@QueryParam("id") String deliveryNo, @QueryParam("no") Integer itemNo, @QueryParam("authId") Long authId) {
        return model.releaseReservation(deliveryNo, itemNo);
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
    @Lock(LockType.READ)
    @GET
    @Path("inventory/available")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public List<AvailableInventoryEntity> findAvailableInventory(@QueryParam("deliveryNo") String deliveryNo, @QueryParam("itemNo") Integer itemNo, @QueryParam("areaName") String areaName) {
        return model.findAvailableInventory(deliveryNo, itemNo, areaName);
    }

    /**
     * 払出ステータスを更新する。
     * 
     * @param deliveryNos
     * @param status
     * @param authId
     * @return 
     */
    @PUT
    @Path("delivery/status")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response updateDeliveryStatus(@QueryParam("id")List<String> deliveryNos, @QueryParam("status")String status, @QueryParam("authId") Long authId) {
        DeliveryStatusEnum _status = DeliveryStatusEnum.valueOf(status);
        if (Objects.isNull(_status)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }
        
        Response response = null;
        for (String deliveryNo : deliveryNos) {
            response = model.updateDeliveryStatus(deliveryNo, _status);
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                break;
            }
        }

        return response;
    }

    /**
     * 払出指示の払出予定日を更新する。
     * 
     * @param deliveryNos 払出指示番号
     * @param date 払出予定日
     * @param authId
     * @return 処理結果
     */
    @PUT
    @Path("delivery/date")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public Response updateDeliveryDate(@QueryParam("id")List<String> deliveryNos, @QueryParam("date")String date, @QueryParam("authId") Long authId) {
        Date _date = DateUtils.parse(date);
        if (Objects.isNull(_date)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        Response response = null;
        for (String deliveryNo : deliveryNos) {
            response = model.updateDeliveryDate(deliveryNo, _date);
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                break;
            }
        }

        return response;
    }

    /**
     * 該当する資材番号の在庫引当情報を取得する。
     * 
     * @param materialNo 資材番号
     * @return 在庫引当情報一覧
     */
    @GET
    @Path("inventory/reserve")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ExecutionTimeLogging
    public List<TrnReserveMaterial> findReserveMaterials(@QueryParam("materialNo") String materialNo, @QueryParam("authId") Long authId) {
        return model.findReserveMaterials(materialNo);
    }
}
