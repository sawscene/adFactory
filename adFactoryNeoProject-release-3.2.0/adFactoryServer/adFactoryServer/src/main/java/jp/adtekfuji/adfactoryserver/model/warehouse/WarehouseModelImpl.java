/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model.warehouse;

import adtekfuji.utility.DateUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.SampleResponse;
import jp.adtekfuji.adFactory.entity.search.DeliveryCondition;
import jp.adtekfuji.adFactory.entity.search.LotTraceCondition;
import jp.adtekfuji.adFactory.entity.search.MaterialCondition;
import jp.adtekfuji.adFactory.entity.search.OperationLogCondition;
import jp.adtekfuji.adFactory.entity.warehouse.Location;
import jp.adtekfuji.adFactory.enumerate.DeliveryRule;
import jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum;
import jp.adtekfuji.adFactory.enumerate.MaterialGroupEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WarehouseEvent;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.common.ServiceConfig;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.warehouse.AvailableInventoryEntity;
import jp.adtekfuji.adfactoryserver.entity.warehouse.DeliveryParam;
import jp.adtekfuji.adfactoryserver.entity.warehouse.LogStock;
import jp.adtekfuji.adfactoryserver.entity.warehouse.LogStock_;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstBom;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstLocation;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstLocation_;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstProduct;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstProduct_;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstStock;
import jp.adtekfuji.adfactoryserver.entity.warehouse.ReserveInventoryParam;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDelivery;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDeliveryItem;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDeliveryItemPK_;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDeliveryItem_;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDelivery_;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnLotTrace;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnLotTracePK_;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnLotTrace_;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnMaterial;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnMaterial_;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnReserveMaterial;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.service.SearchType;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.LocaleUtils;
import jp.adtekfuji.adfactoryserver.utility.WarehouseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

/**
 * 倉庫案内モデル実装
 *
 * @author s-heya
 */
public class WarehouseModelImpl {

    private final Logger logger = LogManager.getLogger();

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    // 受入の棚番号
    public String receivingLocation;

    /**
     * 倉庫案内モデルを初期化する。
     */
    @PostConstruct
    public void initialize() {
        logger.info("WarehouseModelImpl initialize. ");
        
        FileManager fileManager = FileManager.getInstance();
        String lang = fileManager.getSystemProperties().getProperty("default_lang", "ja");
        this.receivingLocation = LocaleUtils.getString("warehouse.ReceivingLocation", lang);
    }

    /**
     * 受入場の棚番号を取得する。
     * 
     * @return 棚情報
     */
    public String getReceivingLocationNo() {
        return this.receivingLocation;
    }

    /**
     * EntityManager を設定する。
     *
     * @param em エンティティマネージャー
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    /**
     * バッチ処理を実行する。
     */
    public void doBatch() {
        try {
            logger.info("doBatch start.");

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("doBatch end.");
        }
    }

    /**
     * サーバーアドレスを取得する。
     *
     * @return サーバーアドレス
     */
    public String getServerAddress() {
        return FileManager.getInstance().getSystemProperties().getProperty("serverAddress", "https://localhost");
    }

    /**
     * 組織マスタを取得する。
     * アクセス権限は無視されます。
     *
     * @param employeeNo 社員番号(組織識別子)
     * @return 組織マスタ
     */
    public OrganizationEntity findOrganization(String employeeNo) {
        try {
            TypedQuery<OrganizationEntity> queryExist = em.createNamedQuery("OrganizationEntity.findByIdentNotRemove", OrganizationEntity.class);
            queryExist.setParameter("organizationIdentify", employeeNo);

            OrganizationEntity organization = queryExist.getSingleResult();
            em.detach(organization);
            organization.setPassword(null);

            return organization;
        } catch (NoResultException ex) {
            logger.fatal(ex);
            return null;
        }
    }

    /**
     * 棚マスタをインポートする。
     *
     * @param inputStream 入力ストリーム
     * @param metaData メタデータ
     * @return
     */
    @ExecutionTimeLogging
    public Response importLocation(InputStream inputStream, FormDataContentDisposition metaData) {
        File file = null;
        try {
            logger.info("importLocation start.");

            Date now = new Date();

            // 一時ファイルに出力
            String filePath = FileManager.getInstance().createTempFile("location-", ".json", inputStream);
            StringBuilder sb = new StringBuilder();
            file = new File(filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            if (sb.length() == 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.FILE_NOT_EXIST)).build();
            }

            List<MstLocation> locationList = JsonUtils.jsonToObjects(sb.toString(), MstLocation[].class);
            int beforeCount = this.getTableCount(MstLocation.class);

            // 全てのエンティティをDETACHED
            em.clear();

            for (MstLocation location : locationList) {
                if (StringUtils.isEmpty(location.getLocationNo())) {
                    // パラメータが無効
                    logger.warn("Invalid parameters: row=" + locationList.indexOf(location) + 1);
                    continue;
                }

                if (StringUtils.isBlank(location.getAreaName())) {
                    location.setAreaName("");
                }

                MstLocation src = this.findLocation(location.getAreaName(), location.getLocationNo(), false);

                if (Objects.isNull(src)) {
                    location.setCreateDate(now);
                    em.persist(location);
                    src = location;
                } else {
                    if (!StringUtils.isBlank(location.getJsonNewAreaName())) {
                        src.setAreaName(location.getJsonNewAreaName());
                    }

                    if (!StringUtils.isBlank(location.getJsonNewlocationNo())) {
                        src.setLocationNo(location.getJsonNewlocationNo());
                    }

                    src.setGuideOrder(location.getGuideOrder());
                    src.setLocationSpec(location.getLocationSpec());
                }

                src.setUpdateDate(now);
            }

            //TypedQuery<MstLocation> query = em.createNamedQuery("MstLocation.findNnecessity", MstLocation.class);
            //query.setParameter("updateDate", now, TemporalType.TIMESTAMP);
            //List<MstLocation> entities = query.getResultList();
            //for (MstLocation entity : entities) {
            //    int sum = entity.getStockList().stream().map(o -> o.getStockNum()).reduce(0, Integer::sum);
            //    if (sum == 0) {
            //        em.remove(entity);
            //    }
            //}

            // データベースへの反映とキャッシュをクリア
            em.flush();
            em.clear();
            //this.findAll(MstLocation.class).stream().forEach(o -> em.refresh(o));

            int afterCount = this.getTableCount(MstLocation.class);
            logger.info("mst_location: before_num={}, after_num={}, import_num={}", beforeCount, afterCount, locationList.size());

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();

        } finally {
            // 一時ファイルを削除する。
            this.deleteFile(file);

            logger.info("importLocation end.");
        }
    }

    /**
     * 部品マスタをインポートする。
     *
     * @param inputStream 入力ストリーム
     * @param metaData メタデータ
     * @return
     */
    @ExecutionTimeLogging
    public Response importParts(InputStream inputStream, FormDataContentDisposition metaData) {
        File file = null;
        try {
            logger.info("importParts start.");

            Date now = new Date();

            FileManager fileManager = FileManager.getInstance();

            // 一時ファイルに出力
            String filePath = fileManager.createTempFile("parts-", ".json", inputStream);
            StringBuilder sb = new StringBuilder();
            file = new File(filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            if (sb.length() == 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.FILE_NOT_EXIST)).build();
            }

            List<MstProduct> productList = JsonUtils.jsonToObjects(sb.toString(), MstProduct[].class);
            int beforeCount = this.getTableCount(MstProduct.class);

            // 全てのエンティティをDETACHED
            em.clear();

            for (MstProduct product : productList) {
                if (StringUtils.isEmpty(product.getProductNo())) {
                    // パラメータが無効
                    logger.warn("Invalid parameters: row=" + productList.indexOf(product) + 1);
                    continue;
                }

                MstProduct src = this.findProduct(product.getProductNo(), false);
                
                if (Objects.isNull(src)) {
                    product.setCreateDate(now);
                    em.persist(product);
                    src = product;
                }
                
                src.setFigureNo(StringUtils.isBlank(product.getFigureNo()) ? product.getProductNo() : product.getFigureNo());
                src.setProductName(product.getProductName());
                src.setUnit(product.getUnit());

                Map<String, String> map = new HashMap<>();
                map.put(Constants.VENDOR, product.getJsonVendor());
                map.put(Constants.SPEC, product.getJsonSpec());

                src.setProperty(map);
                src.setUpdateDate(now);

                // データベースへの反映とキャッシュをクリア
                em.flush();
                em.clear();
            }

            //Date date = this.getRemoveDate(now);
            //
            //TypedQuery<MstProduct> query = em.createNamedQuery("MstProduct.findNnecessity", MstProduct.class);
            //query.setParameter("updateDate", date, TemporalType.TIMESTAMP);
            //List<MstProduct> entities = query.getResultList();
            //for (MstProduct entity : entities) {
            //    int sum = entity.getStockList().stream().map(o -> o.getStockNum()).reduce(0, Integer::sum);
            //    if (sum > 0) {
            //        logger.info("Unable to delete the data (in stock): " + entity.getProductNo());
            //        continue;
            //    }
            //
            //    // 子部品として使用されているか
            //    Long count = this.countBomByChildId(entity.getProductId());
            //    if (count > 0) {
            //        logger.info("Unable to delete the data (use parts): " + entity.getProductNo());
            //        continue;
            //    }
            //
            //    // 資材情報を削除
            //    this.deleteMaterial(entity.getProductId());
            //
            //    // 部品マスタ、部品構成マスタ、在庫マスタを削除
            //    em.remove(entity);
            //}

            // データベースへの反映とキャッシュをクリア
            em.flush();
            em.clear();
            //this.findAll(MstProduct.class).stream().forEach(o -> em.refresh(o));

            int afterCount = this.getTableCount(MstProduct.class);
            logger.info("mst_product: before_num={}, after_num={}, import_num={}", beforeCount, afterCount, productList.size());

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();

        } finally {
            // 一時ファイルを削除する。
            this.deleteFile(file);

            logger.info("importParts end.");
        }
    }

    /**
     * 部品構成マスタをインポートする。
     *
     * @param inputStream 入力ストリーム
     * @param metaData メタデータ
     * @return
     */
    @ExecutionTimeLogging
    public Response importBOM(InputStream inputStream, FormDataContentDisposition metaData) {
        File file = null;
        try {
            logger.info("importBOM start.");

            Date now = new Date();

            FileManager fileManager = FileManager.getInstance();

            // 一時ファイルに出力
            String filePath = fileManager.createTempFile("bom-", ".json", inputStream);
            StringBuilder sb = new StringBuilder();
            file = new File(filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            if (sb.length() == 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.FILE_NOT_EXIST)).build();
            }

            List<MstBom> bomList = JsonUtils.jsonToObjects(sb.toString(), MstBom[].class);
            int beforeCount = this.getTableCount(MstBom.class);

            // 全てのエンティティをDETACHED
            em.clear();

            for (MstBom bom : bomList) {
                if (StringUtils.isEmpty(bom.getJsonProductNo())
                        || StringUtils.isEmpty(bom.getJsonPartsNo())) {
                    // パラメータが無効
                    logger.warn("Invalid parameters: row=" + bomList.indexOf(bom) + 1);
                    continue;
                }

                if (Objects.isNull(bom.getRequiredNum())) {
                    bom.setRequiredNum(1);
                }

                MstProduct product = this.findProduct(bom.getJsonProductNo(), true);
                if (Objects.isNull(product)) {
                    // 部品マスタが存在しない
                    logger.warn("MstProduct does not exist: productNo=" + bom.getJsonProductNo());
                    continue;
                }

                MstProduct parts = this.findProduct(bom.getJsonPartsNo(), true);
                if (Objects.isNull(parts)) {
                    logger.warn("MstProduct does not exist: productNo={} partsNo={}", bom.getJsonProductNo(), bom.getJsonPartsNo());
                    continue;
                }

                MstBom src = this.findBom(product.getProductId(), bom.getUnitNo(), parts.getProductId());
                if (Objects.isNull(src)) {
                    src = new MstBom(product.getProductId(), bom.getUnitNo(), parts, bom.getRequiredNum(), now);
                    em.persist(src);
                } else {
                    src.setRequiredNum(bom.getRequiredNum());
                }

                src.setUpdateDate(now);

                // データベースへの反映とキャッシュをクリア
                em.flush();
                em.clear();
            }

            TypedQuery<MstBom> query = em.createNamedQuery("MstBom.findNnecessity", MstBom.class);
            query.setParameter("updateDate", now, TemporalType.TIMESTAMP);
            List<MstBom> entities = query.getResultList();
            for (MstBom entity : entities) {
                em.remove(entity);
            }

            // データベースへの反映とキャッシュをクリア
            em.flush();
            em.clear();
            //this.findAll(MstProduct.class).stream().forEach(o -> em.refresh(o));

            int afterCount = this.getTableCount(MstBom.class);
            logger.info("mst_bom: before_num={}, after_num={}, import_num={}", beforeCount, afterCount, bomList.size());

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();

        } finally {
            // 一時ファイルを削除する。
            this.deleteFile(file);

            logger.info("importBOM end.");
        }
    }

    /**
     * 保管方法マスタをインポートする。
     *
     * @param inputStream 入力ストリーム
     * @param metaData メタデータ
     * @return
     */
    @ExecutionTimeLogging
    public Response importStock(InputStream inputStream, FormDataContentDisposition metaData) {
        File file = null;
        try {
            logger.info("importStock start.");

            Date now = new Date();

            FileManager fileManager = FileManager.getInstance();

            // 一時ファイルに出力
            String filePath = fileManager.createTempFile("stock-", ".json", inputStream);
            StringBuilder sb = new StringBuilder();
            file = new File(filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            if (sb.length() == 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.FILE_NOT_EXIST)).build();
            }

            List<MstProduct> productList = JsonUtils.jsonToObjects(sb.toString(), MstProduct[].class);

            // 全てのエンティティをDETACHED
            em.clear();

            // 指定棚と重要度ランクを一括消去
            //int beforeCount = em.createNamedQuery("MstProduct.crearAll").executeUpdate();
            int beforeCount = this.getTableCount(MstProduct.class);

            for (MstProduct product : productList) {
                if (StringUtils.isEmpty(product.getProductNo())
                        || StringUtils.isEmpty(product.getJsonAreaName())
                        || StringUtils.isEmpty(product.getProductNo())) {
                    // パラメータが無効
                    logger.warn("Invalid parameters: row=" + productList.indexOf(product) + 1);
                    continue;
                }

                MstProduct src = this.findProduct(product.getProductNo(), false);
                if (Objects.isNull(src)) {
                    // 部品マスタが存在しない
                    logger.warn("MstProduct does not exist: productNo=" + product.getProductNo());
                    logger.info("Create MstProduct: productNo=" + product.getProductNo());
                    src = this.createProduct(product.getProductNo(), null, now);
                }

                if (!now.equals(src.getUpdateDate())) {
                    src.setLocationList(null);
                }

                MstLocation location = this.findLocation(product.getJsonAreaName(), product.getJsonLocationNo(), true);
                if (Objects.isNull(location)) {
                    // 棚マスタが存在しない
                    logger.warn("MstLocation does not exist: areaName={} locationNo={}", product.getJsonAreaName(), product.getJsonLocationNo());
                    continue;
                }

                Location reserve = new Location(product.getJsonAreaName(), product.getJsonLocationNo());
                if (!src.getLocationList().contains(reserve)) {
                    List<Location> list = new ArrayList<>(src.getLocationList());
                    list.add(reserve);
                    src.setLocationList(list);
                }
                try {
                    src.setImportantRank(StringUtils.isEmpty(product.getJsonRank()) ? 0 : Short.valueOf(product.getJsonRank()));
                } catch (Exception ex) {
                    
                }
                
                src.setUpdateDate(now);

                // データベースへの反映とキャッシュをクリア
                em.flush();
                em.clear();
            }

            // データベースへの反映と同期
            //em.flush();
            //this.findAll(MstProduct.class).stream().forEach(o -> em.refresh(o));

            int afterCount = this.getTableCount(MstProduct.class);
            logger.info("mst_product: before_num={}, after_num={}, import_num={}", beforeCount, afterCount, productList.size());

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();

        } finally {
            // 一時ファイルを削除する。
            this.deleteFile(file);

            logger.info("importStock end.");
        }
    }

    /**
     * 納入情報及び、支給品情報をインポートする。
     *
     * @param inputStream 入力ストリーム
     * @param metaData メタデータ
     * @return
     */
    @ExecutionTimeLogging
    public Response importSupply(InputStream inputStream, FormDataContentDisposition metaData) {
        File file = null;
        try {
            logger.info("importSupply start.");

            Date now = new Date();

            FileManager fileManager = FileManager.getInstance();

            // 一時ファイルに出力
            String filePath = fileManager.createTempFile("supply-", ".json", inputStream);
            StringBuilder sb = new StringBuilder();
            file = new File(filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            if (sb.length() == 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.FILE_NOT_EXIST)).build();
            }

            List<TrnMaterial> supplyList = JsonUtils.jsonToObjects(sb.toString(), TrnMaterial[].class);

            // 全てのエンティティをDETACHED
            em.clear();

            int addNum = 0;
            int modifyNum = 0;
            int deleteNum = 0;

            for (TrnMaterial material : supplyList) {
                if (StringUtils.isEmpty(material.getSupplyNo())
                        || Objects.isNull(material.getItemNo())
                        || StringUtils.isEmpty(material.getJsonProductNo())
                        || Objects.isNull(material.getArrivalNum())) {
                    // パラメータが無効
                    logger.warn("Invalid parameters: row=" + supplyList.indexOf(material) + 1);
                    continue;
                }

                MstProduct product = this.findProduct(material.getJsonProductNo(), false);
                if (Objects.isNull(product)) {
                    // 部品マスタが存在しない
                    logger.warn("MstProduct does not exist: productNo=" + material.getJsonProductNo());
                    product = this.createProduct(material.getJsonProductNo(), material.getJsonProductName(), now);
                  
                    //Map<String, String> map = new HashMap<>();
                    //map.put(this.materialAttribute, material.getJosnMaterial());    // 材質
                    //map.put(this.vendorAttribute, material.getJsonVendor());        // メーカー
                    //map.put(this.specAttribute, material.getJsonSpec());            // 規格・型式
                    //product.setProperty(map);
                }

                TrnMaterial src = this.findMaterialBySupplyNo(material.getSupplyNo(), false);
                if (Objects.isNull(src)) {
                    src = new TrnMaterial(TrnMaterial.SUPPLY_PREFIX + material.getSupplyNo(), material.getSupplyNo(), material.getCategory(), now);
                    em.persist(src);

                    src.setItemNo(material.getItemNo());
                    src.setOrderNo(material.getOrderNo());
                    src.setProduct(product);
                    if (Objects.nonNull(material.getArrivalPlan())) {
                        src.setArrivalPlan(material.getArrivalPlan());
                    }
                    src.setArrivalNum(material.getArrivalNum());
                    src.setUpdateDate(now);

                    if (!StringUtils.isEmpty(material.getJosnNote()) && material.getJosnNote().startsWith("#")) {
                        // ユニット番号を取得
                        String[] values = material.getJosnNote().split(",");
                        if (values[0].length() > 1) {
                            material.setJsonUnitNo(values[0]);
                        }
                    }

                    Map<String, String> map = new HashMap<>();
                    map.put(Constants.MATERIAL, material.getJosnMaterial());    // 材質
                    map.put(Constants.VENDOR, material.getJsonVendor());        // メーカー
                    map.put(Constants.SPEC, material.getJsonSpec());
                    map.put(Constants.NOTE, material.getJosnNote());
                    src.setProperty(map);
                    src.setUnitNo(material.getJsonUnitNo());
                    src.setSepc(material.getJsonSpec());
                    src.setNote(material.getJosnNote());

                    addNum++;

                    if (!StringUtils.isEmpty(material.getJsonProductName()) 
                            && !StringUtils.equals(product.getProductName(), material.getJsonProductName())) {
                        // 品目名を更新
                        product.setProductName(material.getJsonProductName());
                    }
                } else {
                    if (1 == material.getJsonDeleteFlag()) {
                        // 強制削除
                        em.remove(src);
                        deleteNum++;

                    } else if (1 == material.getJsonModifyFlag() || src.getItemNo() < material.getItemNo()) {
                        if (Objects.equals(src.getItemNo(), material.getItemNo())) {
                            src.setOrderNo(material.getOrderNo());
                            src.setProduct(product);
                            src.setArrivalNum(material.getArrivalNum());

                            if (!StringUtils.isEmpty(material.getJosnNote()) && material.getJosnNote().startsWith("#")) {
                                // ユニット番号を取得
                                String[] values = material.getJosnNote().split(",");
                                if (values[0].length() > 1) {
                                    material.setJsonUnitNo(values[0]);
                                }
                            }
                    
                            Map<String, String> map = new HashMap<>();
                            map.put(Constants.MATERIAL, material.getJosnMaterial());    // 材質
                            map.put(Constants.VENDOR, material.getJsonVendor());        // メーカー
                            map.put(Constants.SPEC, material.getJsonSpec());
                            map.put(Constants.NOTE, material.getJosnNote());
                            src.setProperty(map);
                            src.setUnitNo(material.getJsonUnitNo());
                            src.setSepc(material.getJsonSpec());
                            src.setNote(material.getJosnNote());
                            
                            if (!StringUtils.isEmpty(material.getJsonProductName()) 
                                    && !StringUtils.equals(product.getProductName(), material.getJsonProductName())) {
                                // 品目名を更新
                                product.setProductName(material.getJsonProductName());
                            }
                        } else {
                            src.setItemNo(material.getItemNo());
                            src.setArrivalNum(src.getArrivalNum() + material.getArrivalNum());
                        }
                        src.setArrivalPlan(material.getArrivalPlan());
                        src.setUpdateDate(now);

                        modifyNum++;
                    }
                }

                // データベースへの反映とキャッシュをクリア
                em.flush();
                em.clear();
            }

            logger.info("trn_material: add_num={}, modify_num={}, delete_num={}, import_num={}", addNum, modifyNum, deleteNum, supplyList.size());

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();

        } finally {
            // 一時ファイルを削除する。
            this.deleteFile(file);

            logger.info("importSupply end.");
        }
    }

    /**
     * 出庫指示情報をインポートする。
     *
     * @param inputStream 入力ストリーム
     * @param metaData メタデータ
     * @return 処理結果
     */
    @ExecutionTimeLogging
    public Response importDelivery(InputStream inputStream, FormDataContentDisposition metaData) {
        File file = null;
        try {
            logger.info("importDelivery start.");

            Date now = new Date();

            FileManager fileManager = FileManager.getInstance();

            // 一時ファイルに出力
            String filePath = fileManager.createTempFile("delivery-", ".json", inputStream);
            StringBuilder sb = new StringBuilder();
            file = new File(filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            if (sb.length() == 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.FILE_NOT_EXIST)).build();
            }

            List<TrnDeliveryItem> deliveryList = JsonUtils.jsonToObjects(sb.toString(), TrnDeliveryItem[].class);
            Map<String, List<TrnDeliveryItem>> deliveryMap = deliveryList.stream()
                    .collect(Collectors.groupingBy(o -> o.getPK().getDeliveryNo()));

            // 既に払出指示が登録されています。
            List<TrnDelivery> list = this.findDeliveryAll(deliveryMap.keySet().stream().collect(Collectors.toList()));
            if (!list.isEmpty()) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NAME_OVERLAP)).build();
            }
            
            // 全てのエンティティをDETACHED
            em.clear();

            int addNum = 0;
            int modifyNum = 0;
            int deleteNum = 0;

            for (Entry<String, List<TrnDeliveryItem>> entry : deliveryMap.entrySet()) {
                if (StringUtils.isEmpty(entry.getKey())
                        || entry.getValue().isEmpty()) {
                    // パラメータが無効
                    logger.warn("Invalid parameters: row=" + deliveryList.indexOf(entry.getValue().get(0)) + 1);
                    continue;
                }

                String deliveryNo = entry.getKey();
                List<TrnDeliveryItem> items = entry.getValue();

                // 明細番号によるソート
                Collections.sort(items, (p1, p2) -> {
                    return Integer.compare(p1.getPK().getItemNo(), p2.getPK().getItemNo());
                });

                int itemNo = 1;
                boolean valid = true;
                for (TrnDeliveryItem item : items) {
                    
                    if (item.getPK().getItemNo() != itemNo 
                            || StringUtils.isEmpty(item.getJsonProductNo())
                            || Objects.isNull(item.getRequiredNum())
                            ) {
                        // データが無効
                        logger.warn("Invalid identifier: DeliveryN={} ItemNo={}", item.getPK().getDeliveryNo(), item.getPK().getItemNo());
                        valid = false;
                        continue;
                    }

                    MstProduct product = this.findProduct(item.getJsonProductNo(), true);
                    if (Objects.isNull(product)) {
                        logger.warn("MstProduct does not exist: DeliveryN={} ItemNo={}, productNo={}", item.getPK().getDeliveryNo(), item.getPK().getItemNo(), item.getJsonProductNo());
                        logger.info("Create MstProduct: productNo=" + item.getJsonProductNo());

                        product = this.createProduct(item.getJsonProductNo(), item.getJsonProductName(), now);
                        
                        Map<String, String> map = new HashMap<>();
                        map.put(Constants.SPEC, StringUtils.isEmpty(item.getJsonSpec()) ? "" : item.getJsonSpec());
                        //map.put(MATERIAL, material);
                        map.put(Constants.VENDOR, StringUtils.isEmpty(item.getJsonVendor()) ? "" : item.getJsonVendor());

                        product.setProperty(map);
                        product.setUnit(StringUtils.isEmpty(item.getJsonUnit()) ? "" : item.getJsonUnit());
                    }

                    item.setProduct(product);

                    itemNo++;
                }

                if (!valid) {
                    continue;
                }

                TrnDelivery src = this.findDelivery(deliveryNo, false);
                if (Objects.isNull(src)) {
                    src = new TrnDelivery(deliveryNo, now);
                    TrnDeliveryItem deliveryItem = items.get(0);

                    src.setOrderNo(deliveryItem.getOrderNo());
                    src.setSerialNo(deliveryItem.getSerialNo());
                    src.setModelName(deliveryItem.getJsonModelName());
                    src.setUnitNo(deliveryItem.getUnitNo());
                    src.setDeliveryList(items);
                    src.setStatus(Objects.equals(deliveryItem.getJsonDeliveryRule(), 2) ? DeliveryStatusEnum.CONFIRM : DeliveryStatusEnum.WAITING);
                    src.setDeliveryRule(deliveryItem.getJsonDeliveryRule());
                    
                    if (!StringUtils.isEmpty(deliveryItem.getOrderNo())) {
                        String[] array = deliveryItem.getOrderNo().split("-");
                        if (array.length < 2) {
                            src.setSerialStart(deliveryItem.getOrderNo());
                            src.setSerialEnd(deliveryItem.getOrderNo());                            
                        } else {
                            src.setSerialStart(array[array.length - 2]);
                            src.setSerialEnd(array[array.length - 1]);
                        }
                    }

                    src.setUpdateDate(now);

                    int requiredNum = 0;
                    for (TrnDeliveryItem item : items) {
                        if (Objects.nonNull(item.getDueDate())) {
                            if (Objects.isNull(src.getDueDate()) || item.getDueDate().after(src.getDueDate())) {
                                src.setDueDate(item.getDueDate());
                            }
                        }
                        
                        if (!Objects.equals(item.getArrange(), 2)) {
                            // 在庫品(消耗品)以外
                            requiredNum += item.getRequiredNum();
                        }
                      
                        item.setDeliveryNum(0);
                        item.setCreateDate(now);
                        item.setUpdateDate(now);
                        addNum++;
                    }

                    src.setStockOutNum(requiredNum);
                    em.persist(src);

                } else {
                    src.setOrderNo(items.get(0).getOrderNo());
                    src.setSerialNo(items.get(0).getSerialNo());
                    src.setModelName(items.get(0).getJsonModelName());
                    src.setUnitNo(items.get(0).getUnitNo());
                    src.setDueDate(now);
                    src.setUpdateDate(now);

                    int requiredNum = 0;
                    for (TrnDeliveryItem item : items) {

                        Optional<TrnDeliveryItem> optional = src.getDeliveryList().stream()
                                .filter(o -> Objects.equals(o.getPK().getItemNo(), item.getPK().getItemNo()))
                                .findFirst();

                        if (!optional.isPresent()) {
                            item.setDeliveryNum(0);
                            item.setCreateDate(now);
                            item.setUpdateDate(now);

                            if (Objects.nonNull(item.getDueDate()) && item.getDueDate().after(src.getDueDate())) {
                                src.setDueDate(item.getDueDate());
                            }

                            if (!Objects.equals(item.getArrange(), 2)) {
                                // 在庫品(消耗品)以外
                                requiredNum += item.getRequiredNum();
                            }

                            src.getDeliveryList().add(item);
                            addNum++;

                        } else {

                            TrnDeliveryItem srcItem = optional.get();

                            if (1 == item.getJsonDeleteFlag()) {
                                src.getDeliveryList().remove(srcItem);
                                deleteNum++;

                                if (src.getDeliveryList().isEmpty()) {
                                    // 出庫指示アイテムが空になった場合
                                    em.remove(src);
                                    src = null;
                                    break;
                                }

                            } else {
                                srcItem.setOrderNo(item.getOrderNo());
                                srcItem.setSerialNo(item.getSerialNo());
                                srcItem.setProduct(item.getProduct());
                                srcItem.setRequiredNum(item.getRequiredNum());
                                srcItem.setLocationNo(item.getLocationNo());
                                srcItem.setDueDate(item.getDueDate());
                                srcItem.setUsageNum(item.getUsageNum());
                                srcItem.setArrange(item.getArrange());
                                srcItem.setArrangeNo(item.getArrangeNo());

                                if (Objects.nonNull(item.getDueDate()) && item.getDueDate().after(src.getDueDate())) {
                                    src.setDueDate(item.getDueDate());
                                }
 
                                if (!Objects.equals(item.getReserve(), 2)) {
                                    // 在庫品(消耗品)以外
                                    requiredNum += item.getRequiredNum();
                                }

                                modifyNum++;
                            }
                        }
                    }

                    src.setStockOutNum(requiredNum);
                }

                // データベースへの反映とキャッシュをクリア
                em.flush();
                em.clear();
            }

            logger.info("trn_delivery_item: add_num={}, modify_num={}, delete_num={}, import_num={}", addNum, modifyNum, deleteNum, deliveryList.size());

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();

        } finally {
            // 一時ファイルを削除する。
            this.deleteFile(file);

            logger.info("importDelivery end.");
        }
    }

    /**
     * 在庫情報出庫指示情報をインポートする。
     *
     * @param inputStream 入力ストリーム
     * @param metaData メタデータ
     * @return 処理結果
     */
    public Response importInStock(InputStream inputStream, FormDataContentDisposition metaData) {
        File file = null;
        try {
            logger.info("importInStock start.");

            Date now = new Date();

            FileManager fileManager = FileManager.getInstance();

            // 一時ファイルに出力
            String filePath = fileManager.createTempFile("instock-", ".json", inputStream);
            StringBuilder sb = new StringBuilder();
            file = new File(filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            if (sb.length() == 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.FILE_NOT_EXIST)).build();
            }

            List<TrnMaterial> supplyList = JsonUtils.jsonToObjects(sb.toString(), TrnMaterial[].class);

            // 全てのエンティティをDETACHED
            em.clear();

            int addNum = 0;
            int modifyNum = 0;
            int deleteNum = 0;

            for (TrnMaterial material : supplyList) {
                if (StringUtils.isEmpty(material.getSupplyNo())
                        || StringUtils.isEmpty(material.getJsonProductNo())
                        || Objects.isNull(material.getInStockNum())) {
                    // パラメータが無効
                    logger.warn("Invalid parameters: row=" + supplyList.indexOf(material) + 1);
                    continue;
                }

                MstProduct product = this.findProduct(material.getJsonProductNo(), false);
                if (Objects.isNull(product)) {
                    // 部品マスタが存在しない
                    logger.warn("MstProduct does not exist: productNo=" + material.getJsonProductNo());
                    product = this.createProduct(material.getJsonProductNo(), material.getJsonProductName(), now);
                }
                
                if (StringUtils.isEmpty(material.getJsonAreaName())) {
                    material.setJsonAreaName("UNKNOWN");
                }

                if (StringUtils.isEmpty(material.getJsonLocNo())) {
                    material.setJsonLocNo("UNKNOWN");
                }
                
                MstLocation location = this.findLocation(material.getJsonAreaName(), material.getJsonLocNo(), false);
                if (Objects.isNull(location)) {
                    // 棚マスタが存在しない
                    logger.warn("MstLocation does not exist: areaName={}, locNo=", material.getJsonAreaName(), material.getJsonLocNo());
                    location = new MstLocation(material.getJsonAreaName(), material.getJsonLocNo());
                    location.setCreateDate(now);
                    em.persist(location);
                }                

                TrnMaterial src = this.findMaterialBySupplyNo(material.getSupplyNo(), false);
                if (Objects.isNull(src)) {
                    src = new TrnMaterial(TrnMaterial.SUPPLY_PREFIX + material.getSupplyNo(), material.getSupplyNo(), material.getCategory(), now);
                    em.persist(src);

                    src.setItemNo(1);
                    src.setCategory((short) 9);
                    src.setStockNum(material.getInStockNum());
                    src.setInStockNum(material.getInStockNum());
                    src.setLocation(location);
                    src.setProduct(product);
                    src.setPartsNo(this.nextPartsNo(product.getProductId()));

                    Map<String, String> map = new HashMap<>();
                    map.put(Constants.MATERIAL, material.getJosnMaterial());    // 材質
                    map.put(Constants.VENDOR, material.getJsonVendor());        // メーカー
                    map.put(Constants.SPEC, material.getJsonSpec());
                    map.put(Constants.NOTE, material.getJosnNote());
                    src.setProperty(map);
                    src.setSepc(material.getJsonSpec());                    

                    addNum++;

                } else {
                    src.setItemNo(src.getItemNo() + 1);
                    src.setStockNum(material.getInStockNum() + src.getDeliveryNum());
                    src.setInStockNum(material.getInStockNum());

                    modifyNum++;
                }

                src.setStockDate(now);
                src.setUpdateDate(now);

                // データベースへの反映とキャッシュをクリア
                em.flush();
                em.clear();
            }

            logger.info("trn_material: add_num={}, modify_num={}, delete_num={}, import_num={}", addNum, modifyNum, deleteNum, supplyList.size());

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();

        } finally {
            // 一時ファイルを削除する。
            this.deleteFile(file);

            logger.info("importInStock end.");
        }
    }

    /**
     * テーブル件数を取得する。
     *
     * @param clazz Classオブジェクト
     * @return テーブル件数
     */
    @Lock(LockType.READ)
    private int getTableCount(Class clazz) {
        jakarta.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        jakarta.persistence.criteria.Root rt = cq.from(clazz);
        cq.select(em.getCriteriaBuilder().count(rt));
        jakarta.persistence.Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * オブジェクトを全件取得する。
     *
     * @param <T> 型
     * @param clazz Classオブジェクト
     * @return オブジェクト一覧
     */
    @Lock(LockType.READ)
    private <T> List<T> findAll(Class clazz) {
        jakarta.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(clazz));
        return em.createQuery(cq).getResultList();
    }

    /**
     * 区画名一覧を取得する。
     *
     * @return 区画名一覧
     */
    @Lock(LockType.READ)
    public List<String> getAreaNames() {
        try {
            TypedQuery<String> query = em.createNamedQuery("MstLocation.getAreaNames", String.class);
            return query.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * 棚マスタ照会
     * 棚マスタを取得する。
     *
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @param isReadOnly 読み取り専用
     * @return 棚マスタ
     */
    @Lock(LockType.READ)
    protected MstLocation findLocation(String areaName, String locationNo, boolean isReadOnly) {
        try {
            TypedQuery<MstLocation> query = em.createNamedQuery("MstLocation.find", MstLocation.class);
            query.setParameter("areaName", areaName);
            query.setParameter("locationNo", locationNo);
            if (!isReadOnly) {
                query.setLockMode(LockModeType.OPTIMISTIC);
            }
            MstLocation location = query.getSingleResult();
            //if (isReadOnly && Objects.nonNull(location)) {
            //    em.detach(location);
            //}
            return location;
        } catch (NoResultException ex) {
            return null;
        }
    }

    static final String KEY_QTY = "QTY=";

    /**
     * QRラベルによる資材照会
     * 資材情報を取得する。
     *
     * @param materialNo 資材番号
     * @param isReadOnly 読み取り専用
     * @return 資材情報
     */
    @Lock(LockType.READ)
    protected TrnMaterial findMaterial(String materialNo, boolean isReadOnly) {
    
        try {
            Integer _sortNum = null;
            String _materialNo = materialNo;
      
            if (!StringUtils.isEmpty(materialNo)) {
                int index = materialNo.lastIndexOf(KEY_QTY);
                if (index > 0) {
                    try {
                        _sortNum = Integer.parseInt(materialNo.substring(index + KEY_QTY.length()));
                        _materialNo = materialNo.substring(0, index - 1).trim();
                    } catch (Exception ex) {
                    }
                }
            }
            
            if (isReadOnly) {
                TrnMaterial material =  em.find(TrnMaterial.class, _materialNo);
                if (Objects.nonNull(material)) {
                    material.setSortNum(_sortNum);
                }
                return material;
            }

            TrnMaterial material = em.find(TrnMaterial.class, _materialNo, LockModeType.OPTIMISTIC);
            if (Objects.nonNull(material)) {
                material.setSortNum(_sortNum);
            }
            return material;
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * 現品票(納品書)による資材照会
     * 資材情報を取得する。
     *
     * @param supplyNo 納入番号(発注番号/倉庫オーダー)
     * @param isReadOnly 読み取り専用
     * @return 資材情報
     */
    @Lock(LockType.READ)
    protected TrnMaterial findMaterialBySupplyNo(String supplyNo, boolean isReadOnly) {
        try {
            Integer _sortNum = null;
            String _supplyNo = supplyNo;
      
            if (!StringUtils.isEmpty(supplyNo)) {
                int index = supplyNo.lastIndexOf(KEY_QTY);
                if (index > 0) {
                    try {
                        _sortNum = Integer.parseInt(supplyNo.substring(index + KEY_QTY.length()));
                        _supplyNo = supplyNo.substring(0, index - 1).trim();
                    } catch (Exception ex) {
                    }
                }
            }

            TypedQuery<TrnMaterial> query = em.createNamedQuery("TrnMaterial.findBySupplyNo", TrnMaterial.class);
            query.setParameter("supplyNo", _supplyNo);
            if (!isReadOnly) {
                query.setLockMode(LockModeType.OPTIMISTIC);
            }
            TrnMaterial material = query.getSingleResult();
            material.setSortNum(_sortNum);
            return material;
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * 製造指示書による資材照会
     * 資材情報を取得する。
     *
     * @param orderNo 製造番号
     * @param isReadOnly 読み取り専用
     * @return 資材情報
     */
    @Lock(LockType.READ)
    protected TrnMaterial findMaterialByOrderNo(String orderNo, boolean isReadOnly) {
        try {
            TypedQuery<TrnMaterial> query = em.createNamedQuery("TrnMaterial.findByOrderNo", TrnMaterial.class);
            query.setParameter("orderNo", orderNo);
            if (!isReadOnly) {
                query.setLockMode(LockModeType.OPTIMISTIC);
            }
            TrnMaterial material = query.getSingleResult();
            return material;
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * 末尾番号を生成する。
     *
     * @param supplyNo 資材番号
     * @return
     */
    @Lock(LockType.READ)
    protected Integer createBranchNo(String supplyNo) {
        try {
            Query query = em.createNamedQuery("TrnMaterial.maxBranchNo");
            query.setParameter("supplyNo", supplyNo + Constants.LIKE_PATTERN);
            Integer max = (Integer) query.getSingleResult();
            return Objects.nonNull(max) ? max + 1 : 1;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 1;
        }
    }

    /**
     * 図番をキーにして部品マスタを取得する。
     *
     * @param figureNo 図番
     * @param isReadOnly 読み取り専用
     * @return 部品マスタ
     */
    @Lock(LockType.READ)
    protected MstProduct findProductByFigureNo(String figureNo, boolean isReadOnly) {
        try {
            TypedQuery<MstProduct> query = em.createNamedQuery("MstProduct.findByFigureNo", MstProduct.class);
            query.setParameter("figureNo", figureNo);
            if (!isReadOnly) {
                query.setLockMode(LockModeType.OPTIMISTIC);
            }
            MstProduct product = query.getSingleResult();
            return product;

        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * 部品マスタを取得する。
     *
     * @param productNo 品目
     * @param isReadOnly 読み取り専用
     * @return 部品マスタ
     */
    @Lock(LockType.READ)
    protected MstProduct findProduct(String productNo, boolean isReadOnly) {
        try {
            TypedQuery<MstProduct> query = em.createNamedQuery("MstProduct.findByProductNo", MstProduct.class);
            query.setParameter("productNo", productNo.toUpperCase());
            if (!isReadOnly) {
                query.setLockMode(LockModeType.OPTIMISTIC);
            }
            MstProduct product = query.getSingleResult();
            //if (isReadOnly && Objects.nonNull(product)) {
            //    em.detach(product);
            //}
            return product;

        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * 部品マスタを新規登録する。
     *
     * @param productNo 品目
     * @param productName 品名
     * @param createDate 作成日時
     * @return 部品マスタ
     */
    public MstProduct createProduct(String productNo, String productName, Date createDate) {
        MstProduct product = new MstProduct(productNo, productName, createDate);
        product.setFigureNo(productNo);
        em.persist(product);
        return product;
    }

    /**
     * 資材を受入する。<br>
     * 資材情報 又は、部品マスタが未登録の場合、エラーを返す。
     *
     * @param employeeNo 社員番号
     * @param materialNo 資材番号
     * @param stockNum 入庫数
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @return 処理結果
     */
    public Response reciveWarehouse(String employeeNo, String materialNo, Integer stockNum, String areaName, String locationNo) {
        logger.info("reciveWarehouse: employeeNo={} materialNo={} stockNum={} areaName={} locationNo={}", employeeNo, materialNo, stockNum, areaName, locationNo);
        return this.doTransaction(WarehouseEvent.RECIVE, employeeNo, null, materialNo, null, null, stockNum, areaName, locationNo, null);
    }

    /**
     * 資材を受入・入庫する。<br>
     * 資材情報 又は、部品マスタが未登録の場合、それらを登録してから受入・入庫すする。
     *
     * @param event イベント
     * @param employeeNo 社員番号
     * @param srcMaterial 資材情報
     * @param srcProduct 部品マスタ
     * @param stockNum 入庫数
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @return 処理結果
     */
    public Response reciveWarehouseWithRegist(WarehouseEvent event, String employeeNo, TrnMaterial srcMaterial, MstProduct srcProduct, Integer stockNum, String areaName, String locationNo) {
        try {
            logger.info("reciveWarehouse start: employeeNo={} {} {} stockNum={} areaName={} locationNo={}", employeeNo, srcMaterial, srcProduct, stockNum, areaName, locationNo);

            // 資材情報を取得
            TrnMaterial material;
            if (StringUtils.startsWith(srcMaterial.getMaterialNo(), TrnMaterial.SUPPLY_PREFIX)
                    || StringUtils.startsWith(srcMaterial.getMaterialNo(), TrnMaterial.ORDER_PREFIX)) {
                material = this.findMaterial(srcMaterial.getMaterialNo(), true);
            } else {
                material = this.findMaterialBySupplyNo(srcMaterial.getSupplyNo(), true);
            }

            if (Objects.isNull(material) && Objects.nonNull(srcProduct)
                    && !StringUtils.isEmpty(srcProduct.getProductNo())) {
                Date now = new Date();

                // 部品マスタを取得
                MstProduct product = this.findProduct(srcProduct.getProductNo(), false);
                if (Objects.isNull(product)) {
                    logger.warn("MstProduct does not exist: productNo=" + srcProduct.getProductNo());
                    logger.info("Create MstProduct: productNo=" + srcProduct.getProductNo());
                    product = this.createProduct(srcProduct.getProductNo(), srcProduct.getProductName(), now);
                    product.setUpdateDate(now);
                    em.flush();
                }

                // 資材情報を登録
                material = new TrnMaterial(srcMaterial.getMaterialNo(), srcMaterial.getSupplyNo(), (short) srcMaterial.getCategory(), now);
                material.setItemNo(1);
                material.setOrderNo(srcMaterial.getOrderNo());
                material.setSerialNo(srcMaterial.getSerialNo());
                material.setPartsNo(srcMaterial.getPartsNo());
                material.setProduct(product);
                material.setArrivalNum(srcMaterial.getArrivalNum());
                material.setProperty(new HashMap<>());
                material.setUpdateDate(now);
                em.persist(material);
                em.flush();
            }

            return this.doTransaction(event, employeeNo, material, srcMaterial.getMaterialNo(), null, null, stockNum, areaName, locationNo, null);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();

        } finally {
            logger.info("reciveWarehouse end.");
        }
    }

    /**
     * 加工品を受入・入庫する。<br>
     * 部品マスタが未登録の場合、エラーを返す。
     *
     * @param employeeNo 社員番号
     * @param orderNo 製造番号
     * @param itemNo 枝番
     * @param productNo 品目
     * @param arrivalNum 納入予定数
     * @param stockNum 入庫数
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @return 処理結果
     */
    public Response reciveWarehouseForOrder(String employeeNo, String orderNo, Integer itemNo, String productNo, Integer arrivalNum, Integer stockNum, String areaName, String locationNo) {
        try {
            logger.info("reciveWarehouseForOrder start: employeeNo={} orderNo={} itemNo={} productNo={} stockNum={} areaName={} locationNo={}", employeeNo, orderNo, itemNo, productNo, stockNum, areaName, locationNo);

            // 資材情報を取得
            TrnMaterial material = this.findMaterialByOrderNo(orderNo, false);
            if (Objects.isNull(material)) {
                Date now = new Date();

                // 部品マスタを取得
                MstProduct product = this.findProduct(productNo, false);
                if (Objects.isNull(product)) {
                    logger.warn("MstProduct does not exist: productNo=" + productNo);
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_PRODUCT)).build();
                }

                // 資材情報を登録
                material = new TrnMaterial(TrnMaterial.ORDER_PREFIX + orderNo, orderNo, (short) 3, now);
                material.setItemNo(itemNo);
                material.setOrderNo(orderNo);
                material.setProduct(product);
                material.setArrivalNum(arrivalNum);
                material.setProperty(new HashMap<>());
                material.setUpdateDate(now);
                em.persist(material);
                em.flush();
            }

            return this.doTransaction(WarehouseEvent.RECIVE, employeeNo, material, material.getMaterialNo(), null, null, stockNum, areaName, locationNo, null);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();

        } finally {
            logger.info("reciveWarehouseForOrder end.");
        }
    }

    /**
     * 資材を受入・入庫する。<br>
     * 資材情報 又は、部品マスタが未登録の場合、それらを登録してから受入・入庫すする。
     *
     * @param employeeNo 社員番号
     * @param figureNo 図番
     * @param productNo 品目
     * @param partsNo 部品番号
     * @param serialNo 製造番号
     * @param stockNum 入庫数
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @param isUpdate 更新フラグ
     * @return 処理結果
     */
    /**
    public Response reciveWarehouseForPartsNo(String employeeNo, String figureNo, String productNo, String partsNo, String serialNo, Integer stockNum, String areaName, String locationNo, Boolean isUpdate) {
        try {
            logger.info("reciveWarehouse start: employeeNo={} figureNo={} productNo={} partsNo={} stockNum={} areaName={} locationNo={}", employeeNo, figureNo, productNo, partsNo, stockNum, areaName, locationNo);

            MstProduct product;

            if (!org.apache.commons.lang3.StringUtils.isEmpty(figureNo)) {
                // 図番をキーにして部品マスタを取得
                product = this.findProductByFigureNo(figureNo, true);
                if (Objects.isNull(product)) {
                    logger.error("MstProduct does not exist: figureNo=" + figureNo);
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_PRODUCT)).build();
                }

            } else if (!org.apache.commons.lang3.StringUtils.isEmpty(productNo)) {
                // 品目をキーにして部品マスタを取得
                product = this.findProduct(productNo, true);
                if (Objects.isNull(product)) {
                    logger.error("MstProduct does not exist: productNo=" + productNo);
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_PRODUCT)).build();
                }

            } else {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            if (StringUtils.isEmpty(partsNo)) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            if (StringUtils.isEmpty(serialNo)) {
                serialNo = partsNo;
            }

            if (Objects.isNull(isUpdate)) {
                isUpdate = false;
            }

            TrnMaterial material = this.findMaterialBySupplyNo(partsNo, true);
            if (Objects.nonNull(material)) {
                if (!isUpdate) {
                    // 同じ部品番号の資材情報が登録されている
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.PARTS_NO_OVERLAP)).build();
                }

                if (material.getDeliveryNum() > 0) {
                    // 既に出庫されているため、編集不可
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.MATERIAL_NON_EDITABLE)).build();
                }
            }

            Date now = new Date();

            if (Objects.isNull(material)) {
                // 資材情報を新規登録
                material = new TrnMaterial(TrnMaterial.ORDER_PREFIX + partsNo, partsNo, (short) 4, now);
                material.setItemNo(1);
                material.setProduct(product);
                material.setSerialNo(serialNo);
                material.setPartsNo(partsNo);
                material.setArrivalNum(stockNum);
                material.setProperty(new HashMap<>());
                material.setUpdateDate(now);
                em.persist(material);
                em.flush();
            } else {
                stockNum = stockNum - material.getArrivalNum();
                material.setProduct(product);
                material.setSerialNo(serialNo);
                material.setArrivalNum(material.getArrivalNum() + stockNum);
                material.setUpdateDate(now);
            }

            return this.doTransaction(WarehouseEvent.RECIVE, employeeNo, material, material.getMaterialNo(), null, null, stockNum, areaName, locationNo, null);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();

        } finally {
            logger.info("reciveWarehouse end.");
        }
    } 
    **/

    /**
     * 資材を受入・入庫する。<br>
     * 資材情報 又は、部品マスタが未登録の場合、それらを登録してから受入・入庫すする。
     *
     * @param employeeNo 社員番号
     * @param supplyNo 発注番号
     * @param stockNum 入庫数
     * @param areaName 区画名
     * @return 処理結果
     */
    public Response reciveWarehouse(String employeeNo, String supplyNo, Integer stockNum, String areaName) {
        try {
            logger.info("reciveWarehouse start: employeeNo={} supplyNo={} stockNum={} areaName={}", employeeNo, supplyNo, stockNum, areaName);

            TrnMaterial material = this.findMaterialBySupplyNo(supplyNo, false);
            //if (Objects.nonNull(material)) {
            //    if (material.getDeliveryNum() > 0) {
            //        // 既に出庫されているため、編集不可
            //        return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.MATERIAL_NON_EDITABLE)).build();
            //    }
            //}

            //Integer diffNum = stockNum - material.getStockNum();

            return this.doTransaction(WarehouseEvent.RECIVE, employeeNo, material, material.getMaterialNo(), null, null, stockNum, areaName, null, null);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();

        } finally {
            logger.info("reciveWarehouse end.");
        }
    }

    /**
     * 支給品を出庫する。<br>
     * 出庫指示情報の事前登録なし場合
     *
     * @param employeeNo 社員番号
     * @param materialNo 資材番号
     * @param deliveryNum 出庫数
     * @param printOrderNo 印刷用の製造番号
     * @return 処理結果
     */
    public Response leaveWarehouse(String employeeNo, String materialNo, Integer deliveryNum, String printOrderNo) {
        logger.info("leaveWarehouse: employeeNo={} materialNo={} deliveryNum={}", employeeNo, materialNo, deliveryNum);
        return this.doTransaction(WarehouseEvent.LEAVE, employeeNo, null, materialNo, materialNo, 1, deliveryNum, null, null, printOrderNo);
    }

    /**
     * 資材を出庫する。<br>
     * 出庫指示情報の事前登録あり場合
     *
     * @param event イベント
     * @param employeeNo 社員番号
     * @param deliveryNo 出庫番号
     * @param itemNo 明細番号
     * @param materialNo 資材番号
     * @param deliveryNum 出庫数
     * @param printOrderNo 印刷用の製造番号
     * @param date 払出日
     * @return 処理結果
     */
    public Response leaveWarehouse(WarehouseEvent event, String employeeNo, String deliveryNo, Integer itemNo, String materialNo, Integer deliveryNum, String printOrderNo, Date date) {
        logger.info("leaveWarehouse: event={} employeeNo={} deliveryNo={} itemNo={} materialNo={} deliveryNum={}", event, employeeNo, deliveryNo, itemNo, materialNo, deliveryNum);
        return this.doTransaction(event, employeeNo, null, materialNo, deliveryNo, itemNo, deliveryNum, null, null, printOrderNo, date);
    }

    /**
     * 資材を、別の棚に移動する。
     *
     * @param employeeNo 社員番号
     * @param materialNo 資材番号
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @return 処理結果
     */
    public Response moveWarehouse(String employeeNo, String materialNo, String areaName, String locationNo) {
        logger.info("moveWarehouse: employeeNo={} materialNo={} areaName={} locationNo={}", employeeNo, materialNo, areaName, locationNo);
        return this.doTransaction(WarehouseEvent.MOVE, employeeNo, null, materialNo, null, null, 0, areaName, locationNo, null);
    }

    /**
     * 指定した数量の資材を、別の棚に移動する。<br>
     * 別の棚に移動した資材は、新たな資材情報として管理します。
     *
     * @param employeeNo 社員番号
     * @param materialNo 資材番号
     * @param moveNum 移動数
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @return 処理結果
     */
    public Response moveWarehouse(String employeeNo, String materialNo, Integer moveNum, String areaName, String locationNo) {
        logger.info("moveWarehouse: employeeNo={} materialNo={} moveNum={} areaName={} locationNo={}", employeeNo, materialNo, moveNum, areaName, locationNo);

        TrnMaterial material = this.findMaterial(materialNo, false);
        if (Objects.isNull(material)) {
            // 資材情報が存在しない
            logger.warn("TrnMaterial does not exist: materialNo=" + materialNo);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_MATERIAL)).build();
        }

        // 元の資材情報の在庫数を更新
        Response response1 = this.doTransaction(WarehouseEvent.MOVE, employeeNo, material, materialNo, null, null,
                -(moveNum), material.getLocation().getAreaName(), material.getLocation().getLocationNo(), null);
        if (response1.getStatus() != HttpURLConnection.HTTP_OK) {
            return response1;
        }

        String originNo = material.getSupplyNo();
        // 移動する資材の情報を登録
        Date now = new Date();
        if (material.getBranchNo() > 0) {
            // 末尾番号あり
            int pos = originNo.lastIndexOf(Constants.SUFFIX_BRANCH_NO + material.getBranchNo());
            if (pos > 0) {
                originNo = originNo.substring(0, pos);
            }
        }

        if (material.getMaterialNo().startsWith(TrnMaterial.SUPPLY_PREFIX + material.getLocation().getAreaName())
                && !StringUtils.equals(material.getLocation().getAreaName(), areaName)) {
            originNo = areaName + Constants.KEY_SEPARATOR + material.getProduct().getProductNo();
            logger.info("Area changed: {}, {}", material.getSupplyNo(), originNo);
        }

        // 末尾番号を生成
        Integer newBranchNo = this.createBranchNo(originNo);

        String prefix = materialNo.startsWith(TrnMaterial.SUPPLY_PREFIX) ? TrnMaterial.SUPPLY_PREFIX : TrnMaterial.ORDER_PREFIX;
        String newMaterialNo = prefix + originNo + Constants.SUFFIX_BRANCH_NO + newBranchNo;
        String newSupplyNo = originNo + Constants.SUFFIX_BRANCH_NO + newBranchNo;

        TrnMaterial newMaterial = new TrnMaterial(newMaterialNo, newSupplyNo, (short) material.getCategory(), now);
        newMaterial.setItemNo(1);
        newMaterial.setOrderNo(material.getOrderNo());
        newMaterial.setSerialNo(material.getSerialNo());
        newMaterial.setProduct(material.getProduct());
        newMaterial.setArrivalNum(0);
        newMaterial.setStockDate(material.getStockDate());
        newMaterial.setArrivalDate(material.getArrivalDate());
        newMaterial.setBranchNo(newBranchNo);
        newMaterial.setProperty(material.getProperty());
        newMaterial.setPartsNo(material.getPartsNo());
        newMaterial.setSepc(material.getSepc());
        newMaterial.setNote(material.getNote());
        newMaterial.setUpdateDate(now);
        em.persist(newMaterial);
        em.flush();

        logger.info("Add record: " + newMaterial);

        Response response2 = this.doTransaction(WarehouseEvent.MOVE, employeeNo, newMaterial, materialNo, null, null, moveNum, areaName, locationNo, null);
        if (response1.getStatus() != HttpURLConnection.HTTP_OK) {
            return response2;
        }

        em.flush();
        em.detach(newMaterial);

        return Response.ok().entity(newMaterial).build();
    }

    /**
     * トランザクション処理を実行する。
     *
     * @param event イベント
     * @param employeeNo 社員番号
     * @param material 資材情報
     * @param materialNo 資材番号
     * @param deliveryNo 出庫指示番号
     * @param itemNo 明細番号
     * @param stockNum 入出庫数
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @param printOrderNo 印刷用の製造番号(出庫のみ)
     * @return Response
     */
    private Response doTransaction(WarehouseEvent event, String employeeNo, TrnMaterial material, String materialNo, String deliveryNo, Integer itemNo, Integer stockNum, String areaName, String locationNo, String printOrderNo) {
        return this.doTransaction(event, employeeNo, material, materialNo, deliveryNo, itemNo, stockNum, areaName, locationNo, printOrderNo, new Date());
    }
 
    /**
     * トランザクション処理を実行する。
     *
     * @param event イベント
     * @param employeeNo 社員番号
     * @param material 資材情報
     * @param materialNo 資材番号
     * @param deliveryNo 出庫指示番号
     * @param itemNo 明細番号
     * @param stockNum 入出庫数
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @param printOrderNo 印刷用の製造番号(出庫のみ)
     * @param eventDate 指定された入出庫日
     * @return Response
     */
    @ExecutionTimeLogging
    private Response doTransaction(WarehouseEvent event, String employeeNo, TrnMaterial material, String materialNo, String deliveryNo, Integer itemNo, Integer stockNum, String areaName, String locationNo, String printOrderNo, Date eventDate) {
        try {
            logger.info("doTransaction start: event={} employeeNo={} materialNo={} deliveryNo={} itemNo={} stockNum={} areaName={} locationNo={}", event, employeeNo, materialNo, deliveryNo, itemNo, stockNum, areaName, locationNo);

            Date now = new Date();

            if (Objects.isNull(stockNum)) {
                stockNum = 0;
            }

            OrganizationEntity organization = this.findOrganization(employeeNo);
            if (Objects.isNull(organization)) {
                // 組織マスタが存在しない
                logger.warn("OrganizationEntity does not exist: employeeNo=" + employeeNo);
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_ORGANIZATION)).build();
            }

            // 資材情報の取得
            if (Objects.isNull(material)) {
                if (StringUtils.startsWith(materialNo, TrnMaterial.SUPPLY_PREFIX)
                        || StringUtils.startsWith(materialNo, TrnMaterial.ORDER_PREFIX)) {
                    material = this.findMaterial(materialNo, false);
                } else {
                    material = this.findMaterialBySupplyNo(materialNo, false);
                }
            }

            if (Objects.isNull(material)) {
                // 資材情報が存在しない
                logger.warn("TrnMaterial does not exist: materialNo=" + materialNo);
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_MATERIAL)).build();
            }

            // 現在の保管棚
            MstLocation oldLocation = material.getLocation();
            int oldStockNum = 0;

            if (Objects.equals(event, WarehouseEvent.ENTRY)) {
                // 入庫の場合は受入場にある資材を保管棚に移動する。
                // oldLocation = this.findLocation(areaName, receivingLocation, true);
                oldLocation = material.getLocation();

                MstStock receivingStock = this.findStock(oldLocation.getLocationId(), material.getProduct().getProductId());
                if (Objects.nonNull(receivingStock)) {
                    oldStockNum = receivingStock.getStockNum();
                }
            }

            MstLocation location = null;

            boolean isInventory = Objects.equals(event, WarehouseEvent.INVENTORY_IMPL);

            if (Objects.equals(event, WarehouseEvent.LEAVE)
                || Objects.equals(event, WarehouseEvent.SHIPPING)) {
                // TODO 先入れ先出し管理
                location = material.getLocation();

            } else if (!isInventory
                    || (isInventory && !StringUtils.isEmpty(locationNo))) {
                // 棚マスタの取得
                location = this.findLocation(areaName, locationNo, true);
                if (Objects.isNull(location)) {
                    switch (event) {
                        case RECIVE:
                        case ENTRY:
                        case RECEIPT_PRODUCTION:
                            if (StringUtils.isEmpty(areaName)) {
                                // 棚マスタが存在しない
                                logger.warn("Area name is invalid.");
                                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_LOCATION)).build();
                            }

                            // 受入の棚番号
                            location = this.findLocation(areaName, receivingLocation, true);
                            if (Objects.isNull(location)) {
                                // 受入の棚番号を登録
                                location = new MstLocation(areaName, receivingLocation);
                                location.setCreateDate(now);
                                em.persist(location);
                            }
                            break;

                        case MOVE:
                        case INVENTORY:
                        case INVENTORY_IMPL:
                            // 棚マスタが存在しない
                            logger.warn("MstLocation does not exist: areaName={} locationNo={}", areaName, locationNo);
                            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_LOCATION)).build();

                        default:
                    }
                }
            }

            /////// ここから更新処理 ///////
            TrnDelivery delivery = null;
            TrnDeliveryItem deliveryItem = null;

            switch (event) {
                case RECIVE:
                    if (StringUtils.isEmpty(material.getPartsNo())) {
                        material.setPartsNo(this.nextPartsNo(material.getProduct().getProductId()));
                        material.setArrivalDate(now); // 納入日時
                    }
                    material.setStockNum(material.getStockNum() + stockNum);
                    material.setInStockNum(material.getInStockNum() + stockNum);

                    if (Objects.isNull(material.getLocation())) {
                        material.setLocation(location);
                    }

                    material.setStockDate(now); // 最終納入日時

                    deliveryItem = this.findDeliveryItem(material.getMaterialNo(), 1, false);
                    if (Objects.nonNull(deliveryItem)) {
                        deliveryItem.setRequiredNum(material.getInStockNum());
                        deliveryItem.setUpdateDate(now);
                    }
                    break;

                case ENTRY:
                    material.setLocation(location);
                    break;

                case RECEIPT_PRODUCTION:
                    material.setStockNum(stockNum);
                    material.setInStockNum(stockNum);
                    material.setStockDate(now);
                    material.setLocation(location);
                    material.setArrivalDate(now);
                    break;

                case MOVE:
                    if (stockNum != 0) {
                        //if (!Objects.equals(material.getLocation(), location)) {
                        //    // 入庫実績がある場合、同じ棚にしか入庫できない
                        //    logger.warn("Wrong location: areaName={} locationNo={}", material.getLocation().getAreaName(), material.getLocation().getLocationNo());
                        //    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.WRONG_LOCATION)).build();
                        //}

                        //material.setStockNum(material.getStockNum() + stockNum);      // 入荷数
                        material.setInStockNum(material.getInStockNum() + stockNum);    // 在庫数
                    }

                    if (!Objects.equals(material.getLocation(), location)) {
                        material.setLocation(location);
                    }
                    break;

                case LEAVE:
                case SHIPPING:
                    if (!StringUtils.isEmpty(deliveryNo) && Objects.nonNull(itemNo)) {
                        // 出庫指示アイテム情報を更新
                        deliveryItem = this.findDeliveryItem(deliveryNo, itemNo, false);
                        if (Objects.isNull(deliveryItem)) {
                            logger.warn("TrnDeliveryItem does not exist: deliveryNo={} itemNo={}", deliveryNo, itemNo);
                            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_DELIVERY)).build();
                        }

                        deliveryItem.setDeliveryNum(deliveryItem.getDeliveryNum() + stockNum);
                        deliveryItem.setUpdateDate(now);
                        
                        delivery = this.findDelivery(deliveryNo,  false);
                        if (this.isWithdraw(delivery)) {
                            // 在庫払出の場合
                            TrnReserveMaterial reserveMaterial = this.findReserveMaterial(deliveryNo, itemNo, material.getMaterialNo(), false);
                            reserveMaterial.setDeliveryNum(stockNum);
                            em.merge(reserveMaterial);
                            logger.info("Updated TrnReserveMaterial: {}", reserveMaterial);
                        }

                        if (deliveryItem.getRequiredNum() <= deliveryItem.getDeliveryNum()) {
                            long count = delivery.getDeliveryList().stream()
                                    .filter(o -> !Objects.equals(o.getArrange(), 2)  // 在庫品以外
                                            && o.getRequiredNum() != 0
                                            && o.getRequiredNum() > o.getDeliveryNum())
                                    .count();
                            if (count == 0L) {
                                delivery.setStatus(this.isWithdraw(delivery) ? DeliveryStatusEnum.PICKED : DeliveryStatusEnum.COMPLETED);
                                delivery.setUpdateDate(now);
                            } else {
                                if (!DeliveryStatusEnum.WORKING.equals(delivery.getStatus())) {
                                    delivery.setStatus(DeliveryStatusEnum.WORKING);
                                    delivery.setUpdateDate(now);
                                }
                            }
                        }

                        if (WarehouseEvent.LEAVE.equals(event)) {
                            // ロットトレース情報を登録
                            TrnLotTrace lotTrace = this.findLotTrace(deliveryNo, itemNo, materialNo, 0L, false);
                            if (Objects.isNull(lotTrace)) {
                                lotTrace = new TrnLotTrace(deliveryNo, itemNo, materialNo, 0L, now);
                                //lotTrace.setTraceNum(Objects.nonNull(deliveryItem.getUsageNum()) ? deliveryItem.getUsageNum() : deliveryItem.getRequiredNum());
                                lotTrace.setTraceNum(0);
                                em.persist(lotTrace);
                            }
                            lotTrace.setSerialNo(material.getSerialNo());
                            lotTrace.setPartsNo(material.getPartsNo());
                            lotTrace.setTraceNum(lotTrace.getTraceNum() + stockNum);
                            lotTrace.setUpdateDate(now);
                        }
                    }

                    material.setDeliveryNum(material.getDeliveryNum() + stockNum);

                    if (!this.isWithdraw(delivery)) {
                        // 在庫払出以外の場合
                        material.setInStockNum(material.getInStockNum() - stockNum);
                    }

                    break;

                case INVENTORY:
                case INVENTORY_IMPL:
                    // 棚卸の情報を更新する。
                    material.setInventoryNum(stockNum);
                    material.setInventoryLocation(location);
                    material.setInventoryDate(now);
                    material.setInventoryPersonNo(employeeNo);
                    break;

                default:
                    break;
            }

            material.setUpdateDate(now);

            // 在庫マスタを更新する。
            switch (event) {
                case RECIVE: // 受入
                    this.updateStock(location, material.getProduct(), stockNum, now, location, 0);
                    break;

                case ENTRY: // 入庫
                case RECEIPT_PRODUCTION:
                    this.updateStock(location, material.getProduct(), oldStockNum, now, oldLocation, oldStockNum);
                    break;

                case MOVE: // 移動
                    this.updateStock(location, material.getProduct(), stockNum, now, oldLocation, stockNum);
                    break;

                case LEAVE: // 出庫
                    if (!this.isWithdraw(delivery)) {
                        this.updateStock(location, material.getProduct(), -stockNum, now, location, 0);
                    }
                    break;

                case SHIPPING: // 出荷払出
                    this.updateStock(location, material.getProduct(), -stockNum, now, location, 0);
                    break;

                default:
                    break;
            }

            // 入出庫実績
            LogStock logStock = null;
            switch (event) {
                case RECIVE:
                case ENTRY:
                case RECEIPT_PRODUCTION:
                case MOVE:
                    logStock = LogStock.createEntryLog(event.getId(), material, stockNum, employeeNo, now);
                    em.persist(logStock);
                    break;

                case LEAVE:
                    logStock = LogStock.createLeaveLog(event.getId(), material, deliveryItem, stockNum, employeeNo, now);
                    if (!StringUtils.isEmpty(printOrderNo)) {
                        logStock.setOrderNo(printOrderNo);
                    }

                    em.persist(logStock);
                    break;

                case SHIPPING:
                    Date date = DateUtils.toDate(DateUtils.toLocalDate(eventDate), DateUtils.toLocalTime(now));
                    logStock = LogStock.createShippingLog(event.getId(), material, deliveryItem, stockNum, employeeNo, date, now);
                    em.persist(logStock);
                    break;

                case INVENTORY_IMPL: // 棚卸実施
                    // 在庫調整
                    Integer adjustment = material.getInventoryNum() - material.getInStockNum();

                    logStock = LogStock.createInventoryLog(WarehouseEvent.INVENTORY_IMPL, material, employeeNo, now, adjustment);
                    this.em.persist(logStock);
                    break;

                case INVENTORY: 
                    // 棚卸の入出庫実績は、棚卸実施時ではなく棚卸承認時に追加する。
                default:
                    break;
            }

            em.flush();
            em.clear();

            return Response.ok().entity(ResponseEntity.success().userData(logStock)).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        } finally {
            logger.info("doTransaction end.");
        }
    }

    /**
     * 出庫指示アイテム一覧を取得する。
     *
     * @param deliveryNo 出庫番号
     * @param areaName 区画名
     * @param deliveryRule 払出ルール
     * @return 出庫指示アイテム一覧
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<TrnDeliveryItem> findDeliveryItem(String deliveryNo, String areaName, DeliveryRule deliveryRule) {
        logger.info("findDeliveryItem start: deliveryNo={} areaNama={}", deliveryNo, areaName);

        try {
            TrnDelivery delivery = em.find(TrnDelivery.class, deliveryNo);
            if (Objects.isNull(delivery)) {
                logger.warn("TrnDeliveryItem does not exist: deliveryNo={}", deliveryNo);
                return new ArrayList<>();
            }

            if (DeliveryRule.ISSUE.equals(deliveryRule)
                && !Objects.equals(DeliveryRule.ISSUE.getId(), delivery.getDeliveryRule())) {
                logger.warn("Different delivery rules: deliveryNo={}", deliveryNo);
                return new ArrayList<>();                        
            }
            
            // 未出庫の出庫指示アイテム情報を抽出
            TypedQuery<TrnDeliveryItem> query1 = em.createNamedQuery("TrnDeliveryItem.findDeliveryOrder", TrnDeliveryItem.class);
            query1.setParameter("deliveryNo", deliveryNo);
            List<TrnDeliveryItem> deliveryItems = query1.getResultList();
            if (deliveryItems.isEmpty()) {
                return deliveryItems;
            }
            
            if (this.isWithdraw(delivery)) {
                // 在庫品 または 在庫引当なし の部品は除外する 
                List<TrnDeliveryItem> _deliveryItems = deliveryItems.stream()
                        .filter(o -> o.getArrange() != 2 && o.getReserve() != 0)
                        .collect(Collectors.toList());
                
                //Collections.sort(_deliveryItems, (p1, p2) -> {
                //    return p1.getGuideLocationNo().compareTo(p2.getGuideLocationNo());
                //});
                Collections.sort(_deliveryItems, (p1, p2) -> {
                    return p1.getPK().getItemNo() - p2.getPK().getItemNo();
                });
                return _deliveryItems;
            }

            // 部品IDを抽出
            List<Long> productIds = deliveryItems.stream().map(o -> o.getProduct().getProductId()).collect(Collectors.toList());

            // 資材情報(在庫あり)を抽出
            TypedQuery<TrnMaterial> query2 = em.createNamedQuery("TrnMaterial.findInStock", TrnMaterial.class);
            query2.setParameter("areaName", areaName);
            query2.setParameter("productIds", productIds);
            List<TrnMaterial> materialList = query2.getResultList();
            Map<Long, List<TrnMaterial>> materialMap = materialList.stream()
                    .collect(Collectors.groupingBy(o -> o.getProduct().getProductId()));

            for (TrnDeliveryItem deliveryItem : deliveryItems) {
                List<TrnMaterial> list = materialMap.get(deliveryItem.getProduct().getProductId());
                if (Objects.isNull(list) || list.isEmpty()) {
//                    deliveryItem.setGuideOrder(999999);
                    continue;
                }

                // 要求数
                int reqNum = deliveryItem.getRequiredNum() - deliveryItem.getDeliveryNum();

                // 納入日時順になっていること
                for (TrnMaterial material : list) {
                    // 要求数を満たすまで資材情報を追加
                    int num = (reqNum <= material.getInStockNum()) ? reqNum : material.getInStockNum() - reqNum;
                    material.setRequiredNum(num);
                    logger.info("Get TrnMaterial: DeliveryNum={}, MaterialNo={}, InStockNum={}, RequiredNum={}", reqNum, material.getMaterialNo(), material.getInStockNum(), num);
                    deliveryItem.getMaterialList().add(material);
                    reqNum = reqNum - num;
                    if (reqNum <= 0) {
                        break;
                    }
                }

                //if (Objects.nonNull(list.get(0).getLocation().getGuideOrder())) {
                //    deliveryItem.setGuideOrder(list.get(0).getLocation().getGuideOrder());
                //} else {
                //    deliveryItem.setGuideOrder(999999);
                //}
            }

            //Collections.sort(deliveryItems, (p1, p2) -> {
            //    return Integer.compare(p1.getGuideOrder(), p2.getGuideOrder());
            //});
            Collections.sort(deliveryItems, (p1, p2) -> {
                return p1.getGuideLocationNo().compareTo(p2.getGuideLocationNo());
            });

            return deliveryItems;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 出庫指示情報を取得する。
     *
     * @param oderNo 製造番号
     * @param serialNo シリアル番号
     * @return 出庫指示アイテム一覧
     */
    @Lock(LockType.READ)
    public List<TrnDeliveryItem> findDeliveryByOrderNo(String oderNo, String serialNo) {
        try {
            List<TrnDeliveryItem> list = null;

            return list;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 出庫指示情報を取得する。
     *
     * @param materialNo 資材番号
     * @return
     */
    public TrnDeliveryItem findDeliveryByMaterialNo(String materialNo) {
        try {
            Date now = new Date();

            TrnMaterial material = this.findMaterial(materialNo, true);
            if (Objects.isNull(material)) {
                logger.warn("TrnMaterial does not exist: materialNo=" + materialNo);
                return null;
            }

            if (this.countReserveMaterial(materialNo) > 0) {
                logger.warn("Unable to leave the warehouse: materialNo=" + materialNo);
                return null;
            }
            
            TrnDelivery delivery = this.findDelivery(materialNo, false);
            if (Objects.nonNull(delivery)) {
            
                if (!delivery.getDeliveryList().isEmpty()) {
                    TrnDeliveryItem deliveryItem = delivery.getDeliveryList().get(0);
                    deliveryItem.setOrderNo(material.getOrderNo());
                    deliveryItem.setSerialNo(material.getSerialNo());
                    deliveryItem.setProperty(material.getProperty());
                    deliveryItem.setLocationNo(material.getLocation().getLocationNo());
                    deliveryItem.setRequiredNum(material.getInStockNum() + deliveryItem.getDeliveryNum());

                    // modified 2022-09-16 s-heya
                    if (!Objects.equals(deliveryItem.getProduct(), material.getProduct())) {
                        deliveryItem.setProduct(material.getProduct());
                        deliveryItem.setUpdateDate(now);
                    }

                    em.merge(delivery);
                    em.flush();
                    em.clear();
                    
                    return deliveryItem;
                }
            } else {
                delivery = new TrnDelivery(materialNo, now);
                delivery.setOrderNo(material.getOrderNo());
                delivery.setSerialNo(material.getSerialNo());
                em.persist(delivery);
            }

            TrnDeliveryItem deliveryItem = new TrnDeliveryItem(materialNo, 1, now);
            deliveryItem.setMaterialNo(materialNo);
            deliveryItem.setOrderNo(material.getOrderNo());
            deliveryItem.setSerialNo(material.getSerialNo());
            deliveryItem.setProduct(material.getProduct());
            //deliveryItem.setRequiredNum(material.getStockNum() - material.getDeliveryNum());
            deliveryItem.setRequiredNum(material.getInStockNum());
            deliveryItem.setProperty(material.getProperty());
            deliveryItem.setLocationNo(material.getLocation().getLocationNo());
            deliveryItem.setUpdateDate(now);

            delivery.getDeliveryList().add(deliveryItem);
            delivery.setUpdateDate(now);

            em.merge(delivery);
            em.flush();
            em.clear();

            return deliveryItem;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 出庫指示情報を取得する。
     *
     * @param deliveryNo 出庫番号
     * @param productNo 品目
     * @param productName 品名
     * @param orderNo 製造番号
     * @param serialNo シリアル番号
     * @param requiredNum 要求数
     * @param areaName 区画名
     * @return 出庫指示情報
     */
    @ExecutionTimeLogging
    public TrnDeliveryItem findOrCreateDeliveryItem(String deliveryNo, String productNo, String productName, String orderNo, String serialNo, int requiredNum, String areaName) {
        logger.info("findOrCreateDeliveryItem start: deliveryNo={} productNo={} orderNo={} serialNo={} requiredNum={}", deliveryNo, productNo, orderNo, serialNo, requiredNum);
 
        try {

            if (StringUtils.isEmpty(deliveryNo) || StringUtils.isEmpty(productNo)) {
                return null;
            }

            TrnDeliveryItem deliveryItem = null;

            TrnDelivery delivery = this.findDelivery(deliveryNo, false);
            if (Objects.isNull(delivery) || delivery.getDeliveryList().isEmpty()) {
                Date now = new Date();

                // 部品マスタを取得
                MstProduct product = this.findProduct(productNo, false);
                if (Objects.isNull(product)) {
                    product = this.createProduct(productNo, productName, now);
                    product.setUpdateDate(now);
                    em.flush();
                }

                if (Objects.isNull(delivery)) {
                    delivery = new TrnDelivery(deliveryNo, now);
                    delivery.setOrderNo(orderNo);
                    delivery.setSerialNo(serialNo);
                    em.persist(delivery);
                }

                deliveryItem = new TrnDeliveryItem(deliveryNo, 1, now);
                deliveryItem.setOrderNo(orderNo);
                deliveryItem.setSerialNo(serialNo);
                deliveryItem.setProduct(product);
                deliveryItem.setRequiredNum(requiredNum);
                deliveryItem.setUpdateDate(now);

                delivery.getDeliveryList().add(deliveryItem);
                delivery.setUpdateDate(now);

                em.flush();
                em.clear();
            }

            // 資材情報を取得するため
            List<TrnDeliveryItem> deliveryList = this.findDeliveryItem(deliveryNo, areaName, DeliveryRule.NORMAL);
            if (!deliveryList.isEmpty()) {
                // 出庫が残っている
                return deliveryList.get(0);
            }

            // 出庫済の場合
            if (Objects.nonNull(delivery) && !delivery.getDeliveryList().isEmpty()) {
                return delivery.getDeliveryList().get(0);
            }

            return null;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 部品構成マスタを取得する。
     *
     * @param parentId 親部品ID
     * @param unitNo ユニット番号
     * @param childId 子部品ID
     * @return 部品構成マスタ
     */
    @Lock(LockType.READ)
    private MstBom findBom(Long parentId, String unitNo, Long childId) {
        try {
            TypedQuery<MstBom> query = em.createNamedQuery("MstBom.find", MstBom.class);
            query.setParameter("parentId", parentId);
            query.setParameter("unitNo", unitNo);
            query.setParameter("childId", childId);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * 部品構成マスタを取得する。
     *
     * @param parentId 親部品ID
     * @return 部品構成マスタ一覧
     */
    @Lock(LockType.READ)
    public List<MstBom> findBomByParentId(Long parentId) {
        try {
            TypedQuery<MstBom> query = em.createNamedQuery("MstBom.findByParentId", MstBom.class);
            query.setParameter("parentId", parentId);
            return query.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * 親部品点数を取得する。
     *
     * @param childId 子部品ID
     * @return 親部品点数
     */
    @Lock(LockType.READ)
    private Long countBomByChildId(Long childId) {
        try {
            TypedQuery<Long> query = em.createNamedQuery("MstBom.countByChildId", Long.class);
            query.setParameter("childId", childId);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return 0L;
        }
    }

    /**
     * データ削除対象日時を取得する。
     *
     * @param now 現在日時
     * @return データ削除対象日時
     */
    @Lock(LockType.READ)
    private Date getRemoveDate(Date now) {
        String days = FileManager.getInstance().getSystemProperties().getProperty("storageDays", "60");
        return org.apache.commons.lang3.time.DateUtils.addDays(now, -Integer.parseInt(days));
    }

    /**
     * 資材情報を削除する。
     *
     * @param productId 部品ID
     * @return 削除数
     */
    private int deleteMaterial(Long productId) {
        try {
            Query query = em.createNamedQuery("TrnMaterial.deleteAllByProductId");
            query.setParameter("productId", productId);
            int rows = query.executeUpdate();
            logger.info("Deleted the TrnMaterial: " + rows);
            return rows;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0;
        }
    }

    /**
     * 出庫指示情報を取得する。
     *
     * @param deliveryNos 出庫番号
     * @return 出庫指示情報
     */
    @Lock(LockType.READ)
    protected List<TrnDelivery> findDeliveryAll(List<String> deliveryNos) {
        try {
            TypedQuery<TrnDelivery> query = em.createNamedQuery("TrnDelivery.findAll", TrnDelivery.class);
            query.setParameter("deliveryNo", deliveryNos);
            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 出庫指示情報を取得する。
     *
     * @param deliveryNo 出庫番号
     * @param isReadOnly 読み取り専用
     * @return 出庫指示情報
     */
    @Lock(LockType.READ)
    protected TrnDelivery findDelivery(String deliveryNo, boolean isReadOnly) {
        try {
            TypedQuery<TrnDelivery> query = em.createNamedQuery("TrnDelivery.find", TrnDelivery.class);
            query.setParameter("deliveryNo", deliveryNo);
            if (!isReadOnly) {
                query.setLockMode(LockModeType.OPTIMISTIC);
            }
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * 出庫指示アイテム情報を取得する。
     *
     * @param deliveryNo 出庫番号
     * @param itemNo 明細番号
     * @param isReadOnly 読み取り専用
     * @return 出庫指示アイテム情報
     */
    @Lock(LockType.READ)
    public TrnDeliveryItem findDeliveryItem(String deliveryNo, Integer itemNo, boolean isReadOnly) {
        try {
            TypedQuery<TrnDeliveryItem> query = em.createNamedQuery("TrnDeliveryItem.find", TrnDeliveryItem.class);
            query.setParameter("deliveryNo", deliveryNo);
            query.setParameter("itemNo", itemNo);
            if (!isReadOnly) {
                query.setLockMode(LockModeType.OPTIMISTIC);
            }
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
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
    @Lock(LockType.READ)
    public List<AvailableInventoryEntity> findAvailableInventory(String deliveryNo, Integer itemNo, String areaName) {
        logger.info("findAvailableInventory: deliveryNo={} itemNo={} areaName={}", deliveryNo, itemNo, areaName);

        try {
            TrnDeliveryItem deliveryItem = this.findDeliveryItem(deliveryNo, itemNo, true);
            if (Objects.isNull(deliveryItem)) {
                logger.warn("Not found the TrnDeliveryItem: deliveryNo={} itemNo={}", deliveryNo, itemNo);
                return null;
            }
           
            TypedQuery<AvailableInventoryEntity> query = this.em.createNamedQuery("AvailableInventoryEntity.find", AvailableInventoryEntity.class);
            query.setParameter(1, deliveryItem.getPK().getDeliveryNo());
            query.setParameter(2, deliveryItem.getPK().getItemNo());
            query.setParameter(3, deliveryItem.getProduct().getProductId());
            query.setParameter(4, areaName);
            List<AvailableInventoryEntity> list = query.getResultList();
 
            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 在庫マスタを取得する。
     *
     * @param locationId 棚ID
     * @param productId 部品ID
     * @return 在庫マスタ
     */
    protected MstStock findStock(Long locationId, Long productId) {
        try {
            TypedQuery<MstStock> query = em.createNamedQuery("MstStock.find", MstStock.class);
            query.setParameter("locationId", locationId);
            query.setParameter("productId", productId);
            MstStock stock = query.getSingleResult();
            return stock;

        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * 指定された品目の在庫数を取得する。
     *
     * @param productNo 品目D
     * @param areaName 区画名
     * @return 在庫数
     */
    @Lock(LockType.READ)
    public Integer getInStockNum(String productNo, String areaName) {
        TypedQuery<Long> query = em.createNamedQuery("MstStock.sumInStock", Long.class);
        query.setParameter("productNo", productNo);
        query.setParameter("areaName", areaName);
        Long inStockNum = query.getSingleResult();
        if (Objects.isNull(inStockNum)) {
            return 0;
        }
        return query.getSingleResult().intValue();
    }

    /**
     * 検索条件に一致した資材情報の件数を取得する。
     *
     * @param condition 検索条件
     * @return 件数
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public String countMaterials(MaterialCondition condition) {
        logger.info("countMaterials: " + condition);
        try {
            Query query = this.createSearchMaterialQuery(SearchType.COUNT, condition);
            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
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
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<TrnMaterial> searchMaterials(MaterialCondition condition, Integer from, Integer to) {
        logger.info("searchMaterials: " + condition);
        try {
            Query query = this.createSearchMaterialQuery(SearchType.SEARCH_RANGE, condition);
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 資材情報のクエリを作成する。
     *
     * @param type 検索種別
     * @param condition 検索条件
     * @return クエリ
     */
    @Lock(LockType.READ)
    private Query createSearchMaterialQuery(SearchType type, MaterialCondition condition) {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaQuery query = builder.createQuery();
        Root<TrnMaterial> root = query.from(TrnMaterial.class);

        //Join<TrnMaterial, MstProduct> joinProduct = root.join(TrnMaterial_.product, JoinType.LEFT);
        //root.fetch(TrnMaterial_.product, JoinType.LEFT);

        //Join<TrnMaterial, MstLocation> joinLocation = root.join(TrnMaterial_.location, JoinType.LEFT);
        //root.fetch(TrnMaterial_.location, JoinType.LEFT);

        // 検索条件
        List<Predicate> where = new ArrayList();

        // 品目
        if (!StringUtils.isEmpty(condition.getProductNo())) {
            // 大文字小文字を区別しない
            //where.add(builder.like(builder.lower(root.get(TrnMaterial_.product).get(MstProduct_.productNo)), "%" + condition.getProductNo().toLowerCase() + "%"));
            // 大文字小文字を区別する
            where.add(builder.like(root.get(TrnMaterial_.product).get(MstProduct_.productNo), "%" + condition.getProductNo() + "%"));
        }

        // 納入番号／発注番号
        if (!StringUtils.isEmpty(condition.getSupplyNo())) {
            where.add(builder.like(root.get(TrnMaterial_.supplyNo), "%" + condition.getSupplyNo() + "%"));
        }

        // 製造オーダー番号
        if (!StringUtils.isEmpty(condition.getOrderNo())) {
            where.add(builder.like(root.get(TrnMaterial_.orderNo), "%" + condition.getOrderNo() + "%"));
        }

        // 資材番号
        if (!StringUtils.isEmpty(condition.getMaterialNo())) {
            where.add(builder.like(root.get(TrnMaterial_.materialNo), "%" + condition.getMaterialNo() + "%"));
        }

        // 区画名
        if (!StringUtils.isEmpty(condition.getAreaName())) {
            where.add(builder.equal(root.get(TrnMaterial_.location).get(MstLocation_.areaName), condition.getAreaName()));
        }

        // 棚番号
        if (!StringUtils.isEmpty(condition.getLocationNo())) {
            where.add(builder.like(root.get(TrnMaterial_.location).get(MstLocation_.locationNo), "%" + condition.getLocationNo() + "%"));
        }

        // 製造番号
        if (!StringUtils.isEmpty(condition.getSerialNo())) {
            where.add(builder.like(root.get(TrnMaterial_.serialNo), "%" + condition.getSerialNo() + "%"));
        }

        // ロット番号
        if (!StringUtils.isEmpty(condition.getPartsNo())) {
            where.add(builder.like(root.get(TrnMaterial_.partsNo), "%" + condition.getPartsNo() + "%"));
        }

        // 入庫日
        if (Objects.nonNull(condition.getStockDate())) {
            Date formDate = adtekfuji.utility.DateUtils.getBeginningOfDate(condition.getStockDate());
            Date toDate = adtekfuji.utility.DateUtils.getEndOfDate(condition.getStockDate());
            where.add(builder.and(
                    builder.greaterThanOrEqualTo(root.get(TrnMaterial_.stockDate), formDate),
                    builder.lessThanOrEqualTo(root.get(TrnMaterial_.stockDate), toDate)));
        } else if (Objects.nonNull(condition.getInspected()) && condition.getInspected()) {
            // 日付範囲
            if (Objects.nonNull(condition.getFromDate()) && Objects.nonNull(condition.getToDate())) {
                Date formDate = adtekfuji.utility.DateUtils.getBeginningOfDate(condition.getFromDate());
                Date toDate = adtekfuji.utility.DateUtils.getEndOfDate(condition.getToDate());
                where.add(builder.and(
                        builder.greaterThanOrEqualTo(root.get(TrnMaterial_.inspectedAt), formDate),
                        builder.lessThanOrEqualTo(root.get(TrnMaterial_.inspectedAt), toDate)));
            } else if (Objects.nonNull(condition.getFromDate())) {
                Date formDate = adtekfuji.utility.DateUtils.getBeginningOfDate(condition.getFromDate());
                where.add(builder.greaterThanOrEqualTo(root.get(TrnMaterial_.inspectedAt), formDate));
            } else if (Objects.nonNull(condition.getToDate())) {
                Date toDate = adtekfuji.utility.DateUtils.getEndOfDate(condition.getToDate());
                where.add(builder.lessThanOrEqualTo(root.get(TrnMaterial_.inspectedAt), toDate));
            }
        } else {
            // 日付範囲
            if (Objects.nonNull(condition.getFromDate()) && Objects.nonNull(condition.getToDate())) {
                Date formDate = adtekfuji.utility.DateUtils.getBeginningOfDate(condition.getFromDate());
                Date toDate = adtekfuji.utility.DateUtils.getEndOfDate(condition.getToDate());
                where.add(builder.and(
                        builder.greaterThanOrEqualTo(root.get(TrnMaterial_.stockDate), formDate),
                        builder.lessThanOrEqualTo(root.get(TrnMaterial_.stockDate), toDate)));
            } else if (Objects.nonNull(condition.getFromDate())) {
                Date formDate = adtekfuji.utility.DateUtils.getBeginningOfDate(condition.getFromDate());
                where.add(builder.greaterThanOrEqualTo(root.get(TrnMaterial_.stockDate), formDate));
            } else if (Objects.nonNull(condition.getToDate())) {
                Date toDate = adtekfuji.utility.DateUtils.getEndOfDate(condition.getToDate());
                where.add(builder.lessThanOrEqualTo(root.get(TrnMaterial_.stockDate), toDate));
            }
        }

        // 在庫がない資材を含める
        if (!Boolean.TRUE.equals(condition.getOutStock())
               && !Boolean.TRUE.equals(condition.getUnarrivedOnly())) {
            where.add(builder.greaterThan(root.get(TrnMaterial_.inStockNum), 0));
        }

        // 棚卸
        if (Boolean.TRUE.equals(condition.getInventory())) {
            // 棚卸開始されている資材のみ取得する。
            where.add(builder.isTrue(root.get(TrnMaterial_.inventoryFlag)));

            List<Predicate> inventoryWhere = new ArrayList();

            if (!Boolean.TRUE.equals(condition.getInventoryUnregistered())
                    && !Boolean.TRUE.equals(condition.getInventoryDifferent())
                    && !Boolean.TRUE.equals(condition.getInventoryNoDifferent())) {
                where.add(builder.isFalse(root.get(TrnMaterial_.inventoryFlag)));
            } else {
                if (Boolean.TRUE.equals(condition.getInventoryUnregistered())) {
                    // 未実施
                    inventoryWhere.add(builder.isNull(root.get(TrnMaterial_.inventoryNum)));
                }

                if (Boolean.TRUE.equals(condition.getInventoryDifferent())
                        && Boolean.TRUE.equals(condition.getInventoryNoDifferent())) {
                    // 実施済すべて
                    inventoryWhere.add(builder.isNotNull(root.get(TrnMaterial_.inventoryNum)));
                } else if (Boolean.TRUE.equals(condition.getInventoryDifferent())) {
                    // 在庫過不足あり
                    inventoryWhere.add(builder.and(
                            builder.isNotNull(root.get(TrnMaterial_.inventoryNum)),
                            builder.notEqual(root.get(TrnMaterial_.inventoryNum), root.get(TrnMaterial_.inStockNum)))
                    );
                } else if (Boolean.TRUE.equals(condition.getInventoryNoDifferent())) {
                    // 在庫過不足なし
                    inventoryWhere.add(builder.and(
                            builder.isNotNull(root.get(TrnMaterial_.inventoryNum)),
                            builder.equal(root.get(TrnMaterial_.inventoryNum), root.get(TrnMaterial_.inStockNum)))
                    );
                }

                where.add(builder.or(inventoryWhere.toArray(new Predicate[inventoryWhere.size()])));
            }
        }

        // ユニット番号
        if (!StringUtils.isEmpty(condition.getUnitNo())) {
            where.add(builder.like(root.get(TrnMaterial_.unitNo), "%" + condition.getUnitNo() + "%"));
        }

        // 未納入品のみ
        if (Boolean.TRUE.equals(condition.getUnarrivedOnly())) {
            where.add(builder.greaterThan(root.get(TrnMaterial_.arrivalNum), root.get(TrnMaterial_.stockNum)));
        }

        if (SearchType.COUNT.equals(type)) {
            query.select(builder.count(root.get(TrnMaterial_.materialNo)))
                    .where(builder.and(where.toArray(new Predicate[where.size()])));

        } else {
            List<Order> orders = new LinkedList();
            if (MaterialGroupEnum.UNIT.equals(condition.getGroupBy())) {
                orders.add(builder.asc(root.get(TrnMaterial_.unitNo)));
                orders.add(builder.asc(root.get(TrnMaterial_.orderNo)));
            } else {
                orders.add(builder.asc(root.get(TrnMaterial_.product).get(MstProduct_.productNo)));
            }
            orders.add(builder.asc(root.get(TrnMaterial_.materialNo)));
            query.select(root)
                    .where(builder.and(where.toArray(new Predicate[where.size()])))
                    .orderBy(orders);
        }

        return this.em.createQuery(query);
    }

    /**
     * 検索条件に一致した作業ログ情報を件数を取得する。
     * 
     * @param condition 検索条件
     * @return 作業ログの件数
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public String countOperationLog(OperationLogCondition condition) {
        logger.info("countOperationLog: " + condition);
        try {
            Query query = this.getSearchOperationLogQuery(SearchType.COUNT, condition);
            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 作業ログ情報を検索する。
     * 
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 作業ログ一覧
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<LogStock> searchOperationLog(OperationLogCondition condition, Integer from, Integer to) {
        logger.info("searchOperationLog: " + condition);
        try {
            Query query = this.getSearchOperationLogQuery(SearchType.SEARCH_RANGE, condition);
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }        
    }
    
    /**
     * 作業ログ情報のクエリを作成する。
     *
     * @param type 検索種別
     * @param condition 検索条件
     * @return クエリ
     */
    @Lock(LockType.READ)
    private Query getSearchOperationLogQuery(SearchType type, OperationLogCondition condition) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<LogStock> root = cq.from(LogStock.class);

        jakarta.persistence.criteria.Path<Date> pathDate = root.get(LogStock_.eventDate);
        jakarta.persistence.criteria.Path<String> pathAreaName = root.get(LogStock_.areaName);
        jakarta.persistence.criteria.Path<String> pathPersonNo = root.get(LogStock_.personNo);
        jakarta.persistence.criteria.Path<String> pathProductNo = root.get(LogStock_.productNo);
        jakarta.persistence.criteria.Path<String> pathOrderNo = root.get(LogStock_.orderNo);
        jakarta.persistence.criteria.Path<String> pathDeliveryNo = root.get(LogStock_.deliveryNo);
        jakarta.persistence.criteria.Path<String> pathPartsNo = root.get(LogStock_.partsNo);
        jakarta.persistence.criteria.Path<Short> pathEvent = root.get(LogStock_.eventKind);

        // 検索条件
        List<Predicate> where = new LinkedList();

        if (Objects.nonNull(condition.getAreaName())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathAreaName), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getAreaName())) + "%"),
                    cb.like(pathAreaName, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getAreaName()) + "%")
            ));
        }

        // 社員番号
        if (Objects.nonNull(condition.getOrganizationIds())) {
            List<Predicate> nameSubWhere = new ArrayList();
            condition.getOrganizationIds().stream()
                    .map(id -> this.findOrganizationIdentify(id))
                    .forEach(o ->  {
                        String personNo = (String) o;
                        if (!StringUtils.isEmpty(personNo)) {
                            nameSubWhere.add(cb.like(cb.lower(pathPersonNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(personNo)) + "%"));
                            nameSubWhere.add(cb.like(pathPersonNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(personNo) + "%"));
                        }
                    });

            where.add(cb.or(nameSubWhere.toArray(new Predicate[nameSubWhere.size()])));
        }
            
        // 品目
        if (Objects.nonNull(condition.getProductNo())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathProductNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getProductNo())) + "%"),
                    cb.like(pathProductNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getProductNo()) + "%")
            ));
        }

        // 製造番号
        if (Objects.nonNull(condition.getOrderNo())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathOrderNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getOrderNo())) + "%"),
                    cb.like(pathOrderNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getOrderNo()) + "%")
            ));
        }

        // 払出指示番号
        if (Objects.nonNull(condition.getDeliveryNo())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathDeliveryNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getDeliveryNo())) + "%"),
                    cb.like(pathDeliveryNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getDeliveryNo()) + "%")
            ));
        }

        // 部品番号
        if (Objects.nonNull(condition.getPartsNo())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathPartsNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getPartsNo())) + "%"),
                    cb.like(pathPartsNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getPartsNo()) + "%")
            ));
        }
        
        // 種別
        if (Objects.nonNull(condition.getCategories())) {
            List<Short> eventIds = condition.getCategories().stream()
                    .filter(o -> Objects.nonNull(WarehouseEvent.valueOf(o)))
                    .map(o -> WarehouseEvent.valueOf(o).getId())
                    .collect(Collectors.toList());
            if (!eventIds.isEmpty()) {
                where.add(pathEvent.in(eventIds));
            }
        }
        
        // 日時範囲の先頭
        if (Objects.nonNull(condition.getFromDate())) {
            where.add(cb.greaterThanOrEqualTo(pathDate, condition.getFromDate()));
        }

        // 日時範囲の末尾
        if (Objects.nonNull(condition.getToDate())) {
            where.add(cb.lessThanOrEqualTo(pathDate, condition.getToDate()));
        }

        if (SearchType.COUNT.equals(type)) {
            cq.select(cb.count(root.get(LogStock_.eventId)))
                    .where(cb.and(where.toArray(new Predicate[where.size()])));
        } else {
            cq.select(root)
                    .where(cb.and(where.toArray(new Predicate[where.size()])))
                    .orderBy(cb.asc(pathDate));
        }

        return this.em.createQuery(cq);
    }

    /**
     * 入出庫実績情報を取得する。
     *
     * @param materialNos 資材番号一覧
     * @return 入出庫実績情報一覧
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<LogStock> findLogStock(List<String> materialNos) {
        logger.info("findLogStock: " + materialNos);
        TypedQuery<LogStock> query = em.createNamedQuery("LogStock.findByMaterialNo", LogStock.class);
        query.setParameter("materialNos", materialNos);
        List<LogStock> logList = query.getResultList();
        return logList;
    }

    /**
     * 入出庫実績情報を取得する。
     *
     * @param materialNo 資材番号
     * @param eventKind イベント種別
     * @return 入出庫実績情報一覧
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<LogStock> findLogStock(String materialNo, Short eventKind) {
        logger.info("findLogStock: " + materialNo);
        TypedQuery<LogStock> query = em.createNamedQuery("LogStock.findByEventKind", LogStock.class);
        query.setParameter("materialNo", materialNo);
        query.setParameter("eventKind", eventKind);
        List<LogStock> logList = query.getResultList();
        return logList;
    }

    /**
     * 未同期の入出庫実績情報を取得する。
     *
     * @param synced 同期フラグ
     * @param max 最大件数
     * @return 入出庫実績情報一覧
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<LogStock> findSyncedLogStock(Boolean synced, Integer max) {
        TypedQuery<LogStock> query = em.createNamedQuery("LogStock.findBySynced", LogStock.class);
        query.setParameter("synced", synced);
        if (Objects.nonNull(max)) {
            query.setMaxResults(max);
        }
        return query.getResultList();
    }

    /**
     * 入出庫実績情報の同期フラグを更新する。
     *
     * @param eventIds イベントID一覧
     * @param synced 同期フラグ
     * @return 更新件数
     */
    @ExecutionTimeLogging
    public int updateSyncedLogStock(List<Long> eventIds, Boolean synced) {
        if (Objects.isNull(eventIds) || eventIds.isEmpty() || Objects.isNull(synced)) {
            logger.warn("Invalid parameters.");
            return 0;
        }

        // 同期フラグを更新する
        Query query = em.createNamedQuery("LogStock.updateSynced");
        query.setParameter("synced", synced);
        query.setParameter("eventIds", eventIds);
        return query.executeUpdate();
    }

    /**
     * 区画コードを取得する。
     *
     * @param areaName 区画名
     * @return 区画コード
     */
    @Lock(LockType.READ)
    public String getAreaCode(String areaName) {

        // 区画名と区画コードを格納したファイルを読み込む
        FileManager fileManager = FileManager.getInstance();
        String filePath = fileManager.getLocalePath(FileManager.Data.Config, "AreaCode.json");

        File file = new File(filePath);
        if (!file.exists()) {
            return "";
        }

        try {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "SJIS"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            if (sb.length() == 0) {
                return "";
            }

            Map<String, String> areaMap = JsonUtils.jsonToMap(sb.toString());

            if (areaMap.containsKey(areaName)) {
                return areaMap.get(areaName);
            }

        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        return "";
    }

    private static final String FORMAT_PARTSNO = "%07d-%09d";

    /**
     * 部品番号を生成する。
     *
     * @param productId 部品マスタID
     * @return 部品番号
     */
    @Lock(LockType.READ)
    public String nextPartsNo(Long productId) {

        Pattern pattern = Pattern.compile("^\\d{7}-\\d{9}$");
        String partsNo;

        try {
            TypedQuery<String> query = em.createNamedQuery("TrnMaterial.maxPartsNo", String.class);
            query.setParameter("productId", productId);

            String value = query.getSingleResult();
            if (!StringUtils.isEmpty(value)
                    && pattern.matcher(value).matches()) {
                String val = value.substring(8);
                int seqNo = Integer.valueOf(val) + 1;
                partsNo = String.format(FORMAT_PARTSNO, productId, seqNo);

            } else {
                partsNo = String.format(FORMAT_PARTSNO, productId, 1);
            }

        } catch (NoResultException ex) {
            partsNo = String.format(FORMAT_PARTSNO, productId, 1);
        }

        return partsNo;
    }

    /**
     * ロットトレース情報を取得する。
     *
     * @param deliveryNo 出庫番号
     * @param itemNo 明細番号
     * @param materialNo 資材番号
     * @param workKanbanId カンバンID
     * @param isReadOnly 読み取り専用
     * @return ロットトレース
     */
    @Lock(LockType.READ)
    protected TrnLotTrace findLotTrace(String deliveryNo, int itemNo, String materialNo, Long workKanbanId, boolean isReadOnly) {
        try {
            TypedQuery<TrnLotTrace> query = em.createNamedQuery("TrnLotTrace.find", TrnLotTrace.class);
            query.setParameter("deliveryNo", deliveryNo);
            query.setParameter("itemNo", itemNo);
            query.setParameter("materialNo", materialNo);
            query.setParameter("workKanbanId", workKanbanId);
            if (!isReadOnly) {
                query.setLockMode(LockModeType.OPTIMISTIC);
            }
            TrnLotTrace lotTrace = query.getSingleResult();
            return lotTrace;
        } catch (NoResultException ex) {
            return null;
        }
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
    public List<TrnLotTrace> searchLotTraceRange(LotTraceCondition condition, Integer from, Integer to, Long authId) {
        try {
            Query query = this.getSearchLotTraceQuery(SearchType.SEARCH, condition);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return query.getResultList();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 検索条件を指定して、ロットトレースの件数を取得する。
     *
     * @param condition ロットトレースの検索条件
     * @param authId 認証ID
     * @return ロットトレースの件数
     */
    @Lock(LockType.READ)
    public String countLotTrace(LotTraceCondition condition, Long authId) {
        try {
            Query query = this.getSearchLotTraceQuery(SearchType.COUNT, condition);
            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 検索条件でロットトレース情報を検索するクエリを取得する。
     *
     * @param type 検索種別
     * @param condition 検索条件
     * @return 検索クエリ
     */
    private Query getSearchLotTraceQuery(SearchType type, LotTraceCondition condition) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<TrnLotTrace> poolLotTrace = cq.from(TrnLotTrace.class);

        // 出庫番号
        jakarta.persistence.criteria.Path<String> pathDeliveryNo = poolLotTrace.get(TrnLotTrace_.pk).get(TrnLotTracePK_.deliveryNo);
        // 明細番号
        jakarta.persistence.criteria.Path<Integer> pathItemNo = poolLotTrace.get(TrnLotTrace_.pk).get(TrnLotTracePK_.itemNo);
        // 資材番号
        jakarta.persistence.criteria.Path<String> pathMaterialNo = poolLotTrace.get(TrnLotTrace_.pk).get(TrnLotTracePK_.materialNo);
        // ロット番号
        jakarta.persistence.criteria.Path<String> pathPartsNo = poolLotTrace.get(TrnLotTrace_.partsNo);
        // 組付け日時
        jakarta.persistence.criteria.Path<Date> pathAssemblyDatetime = poolLotTrace.get(TrnLotTrace_.assemblyDatetime);
        // 作業者
        jakarta.persistence.criteria.Path<String> pathPersonName = poolLotTrace.get(TrnLotTrace_.personName);
        // 確認
        jakarta.persistence.criteria.Path<Boolean> pathConfirm = poolLotTrace.get(TrnLotTrace_.confirm);
        // 追跡無効
        jakarta.persistence.criteria.Path<Boolean> pathDisabled = poolLotTrace.get(TrnLotTrace_.disabled);

        // 製造オーダー番号
        jakarta.persistence.criteria.Path<String> pathOrderNo = poolLotTrace.get(TrnLotTrace_.deliveryItem).get(TrnDeliveryItem_.orderNo);
        // 品目
        jakarta.persistence.criteria.Path<String> pathProductNo = poolLotTrace.get(TrnLotTrace_.deliveryItem).get(TrnDeliveryItem_.product).get(MstProduct_.productNo);

        // 工程カンバンID
        jakarta.persistence.criteria.Path<Long> pathWorkKanbanId = poolLotTrace.get(TrnLotTrace_.pk).get(TrnLotTracePK_.workKanbanId);;

        // 検索条件
        List<Predicate> where = new LinkedList();

        // 出庫番号
        if (Objects.nonNull(condition.getDeliveryNo())) {
            if (condition.isEqualDeliveryNo()) {
                where.add(cb.equal(cb.lower(pathDeliveryNo), adtekfuji.utility.StringUtils.toLowerCase(condition.getDeliveryNo())));
            } else {
                where.add(cb.or(
                        cb.like(cb.lower(pathDeliveryNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getDeliveryNo())) + "%"),
                        cb.like(pathDeliveryNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getDeliveryNo()) + "%")
                ));
            }
        }
        
        // 資材番号
        if (Objects.nonNull(condition.getMaterialNos()) && !condition.getMaterialNos().isEmpty()) {
            if (condition.isEqualMaterialNo()) {
                //where.add(cb.equal(cb.lower(pathMaterialNo), adtekfuji.utility.StringUtils.toLowerCase(condition.getMaterialNo())));
                where.add(pathMaterialNo.in(condition.getMaterialNos()));
            } else {
                where.add(cb.or(
                        cb.like(cb.lower(pathMaterialNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getMaterialNos().get(0))) + "%"),
                        cb.like(pathMaterialNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getMaterialNos().get(0)) + "%")
                ));
            }
        }

        // 製造オーダー番号
        if (Objects.nonNull(condition.getOrderNo())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathOrderNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getOrderNo())) + "%"),
                    cb.like(pathOrderNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getOrderNo()) + "%")
            ));
        }

        // 品目
        if (Objects.nonNull(condition.getProductNo())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathProductNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getProductNo())) + "%"),
                    cb.like(pathProductNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getProductNo()) + "%")
            ));
        }

        // ロット番号
        if (Objects.nonNull(condition.getPartsNo())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathPartsNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getPartsNo())) + "%"),
                    cb.like(pathPartsNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getPartsNo()) + "%")
            ));
        }

        // 作業者
        if (Objects.nonNull(condition.getPersonName())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathPersonName), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getPersonName())) + "%"),
                    cb.like(pathPersonName, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getPersonName()) + "%")
            ));
        }

        // 確認
        if (Objects.nonNull(condition.getConfirm())) {
            where.add(cb.equal(pathConfirm, condition.getConfirm()));
        }

        // 作業済のみ取得するか
        if (condition.isWorkedOnly()) {
            where.add(cb.isNotNull(pathWorkKanbanId));
        }

        // 追跡無効
        where.add(cb.notEqual(pathDisabled, Boolean.TRUE));

        // 日時範囲の先頭
        if (Objects.nonNull(condition.getFromDate())) {
            where.add(cb.greaterThanOrEqualTo(pathAssemblyDatetime, condition.getFromDate()));
        }

        // 日時範囲の末尾
        if (Objects.nonNull(condition.getToDate())) {
            where.add(cb.lessThanOrEqualTo(pathAssemblyDatetime, condition.getToDate()));
        }

        if (!condition.isEqualDeliveryNo()) {
            where.add(cb.isNotNull(pathAssemblyDatetime));
        }

        if (SearchType.COUNT.equals(type)) {
            cq.select(cb.count(pathDeliveryNo))
                    .where(cb.and(where.toArray(new Predicate[where.size()])));
        } else {
            cq.select(poolLotTrace)
                    .where(cb.and(where.toArray(new Predicate[where.size()])))
                    .orderBy(cb.asc(pathDeliveryNo), cb.asc(pathItemNo));
        }

        return this.em.createQuery(cq);
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
    public List<TrnDeliveryItem> searchDeliveryItemRange(DeliveryCondition condition, Integer from, Integer to, Long authId) {
        try {
            Query query = this.getSearchDeliveryItemQuery(SearchType.SEARCH, condition);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return query.getResultList();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 検索条件を指定して、出庫情報の件数を取得する。
     *
     * @param condition 出庫情報の検索条件
     * @param authId 認証ID
     * @return 出庫情報の件数
     */
    @Lock(LockType.READ)
    public String countDeliveryItems(DeliveryCondition condition, Long authId) {
        try {
            Query query = this.getSearchDeliveryItemQuery(SearchType.COUNT, condition);
            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 検索条件で出庫情報を検索するクエリを取得する。
     *
     * @param type 検索種別
     * @param condition 検索条件
     * @return 検索クエリ
     */
    private Query getSearchDeliveryItemQuery(SearchType type, DeliveryCondition condition) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<TrnDeliveryItem> poolDeliveryItem = cq.from(TrnDeliveryItem.class);

        // 出庫番号
        jakarta.persistence.criteria.Path<String> pathDeliveryNo = poolDeliveryItem.get(TrnDeliveryItem_.pk).get(TrnDeliveryItemPK_.deliveryNo);
        // 明細番号
        jakarta.persistence.criteria.Path<Integer> pathItemNo = poolDeliveryItem.get(TrnDeliveryItem_.pk).get(TrnDeliveryItemPK_.itemNo);
        // 製造オーダー番号
        jakarta.persistence.criteria.Path<String> pathOrderNo = poolDeliveryItem.get(TrnDeliveryItem_.orderNo);
        // 製造番号(ロット番号)
        jakarta.persistence.criteria.Path<String> pathSerialNo = poolDeliveryItem.get(TrnDeliveryItem_.serialNo);
        // 納期
        jakarta.persistence.criteria.Path<Date> pathDueDate = poolDeliveryItem.get(TrnDeliveryItem_.dueDate);
    
        // 検索条件
        List<Predicate> where = new LinkedList();

        // 出庫番号
        if (Objects.nonNull(condition.getDeliveryNo())) {
            if (condition.isEqualDeliveryNo()) {
                where.add(cb.equal(cb.lower(pathDeliveryNo), adtekfuji.utility.StringUtils.toLowerCase(condition.getDeliveryNo())));
            } else {
                where.add(cb.or(
                        cb.like(cb.lower(pathDeliveryNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getDeliveryNo())) + "%"),
                        cb.like(pathDeliveryNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getDeliveryNo()) + "%")
                ));
            }
        }

        // 製造オーダー番号
        if (Objects.nonNull(condition.getOrderNo())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathOrderNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getOrderNo())) + "%"),
                    cb.like(pathOrderNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getOrderNo()) + "%")
            ));
        }

        // 製造番号(ロット番号)
        if (Objects.nonNull(condition.getSerialNo())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathSerialNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getSerialNo())) + "%"),
                    cb.like(pathSerialNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getSerialNo()) + "%")
            ));
        }
            
        // 日付範囲
        if (Objects.nonNull(condition.getFromDate()) && Objects.nonNull(condition.getToDate())) {
            Date formDate = adtekfuji.utility.DateUtils.getBeginningOfDate(condition.getFromDate());
            Date toDate = adtekfuji.utility.DateUtils.getEndOfDate(condition.getToDate());
            where.add(cb.and(
                    cb.greaterThanOrEqualTo(pathDueDate, formDate),
                    cb.lessThanOrEqualTo(pathDueDate, toDate)));
        } else if (Objects.nonNull(condition.getFromDate())) {
            Date formDate = adtekfuji.utility.DateUtils.getBeginningOfDate(condition.getFromDate());
            where.add(cb.greaterThanOrEqualTo(pathDueDate, formDate));
        } else if (Objects.nonNull(condition.getToDate())) {
            Date toDate = adtekfuji.utility.DateUtils.getEndOfDate(condition.getToDate());
            where.add(cb.lessThanOrEqualTo(pathDueDate, toDate));
        }
       
        if (SearchType.COUNT.equals(type)) {
            cq.select(cb.count(pathDeliveryNo))
                    .where(cb.and(where.toArray(new Predicate[where.size()])));
        } else {
            cq.select(poolDeliveryItem)
                    .where(cb.and(where.toArray(new Predicate[where.size()])))
                    .orderBy(cb.asc(pathDeliveryNo), cb.asc(pathItemNo));
        }

        return this.em.createQuery(cq);
    }

    /**
     * 検索条件を指定して、出庫指示情報一覧を取得する。
     *
     * @param condition 出庫情報の検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 出庫指示情報一覧
     */
    @Lock(LockType.READ)
    public List<TrnDelivery> searchDeliveryRange(DeliveryCondition condition, Integer from, Integer to, Long authId) {
        try {
            Query query = this.getSearchDeliveryQuery(SearchType.SEARCH, condition);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return query.getResultList();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 検索条件を指定して、出庫情報の件数を取得する。
     *
     * @param condition 出庫情報の検索条件
     * @param authId 認証ID
     * @return 出庫情報の件数
     */
    @Lock(LockType.READ)
    public String countDelivery(DeliveryCondition condition, Long authId) {
        try {
            Query query = this.getSearchDeliveryQuery(SearchType.COUNT, condition);
            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 検索条件で出庫指示情報を検索するクエリを取得する。
     *
     * @param type 検索種別
     * @param condition 検索条件
     * @return 検索クエリ
     */
    private Query getSearchDeliveryQuery(SearchType type, DeliveryCondition condition) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<TrnDelivery> poolDelivery = cq.from(TrnDelivery.class);

        // 出庫番号
        jakarta.persistence.criteria.Path<String> pathDeliveryNo = poolDelivery.get(TrnDelivery_.deliveryNo);
        // 製造オーダー番号
        jakarta.persistence.criteria.Path<String> pathOrderNo = poolDelivery.get(TrnDelivery_.orderNo);
        // 製造番号(ロット番号)
        jakarta.persistence.criteria.Path<String> pathSerialNo = poolDelivery.get(TrnDelivery_.serialNo);
        // 納期
        jakarta.persistence.criteria.Path<Date> pathDueDate = poolDelivery.get(TrnDelivery_.dueDate);
        // 機種名
        jakarta.persistence.criteria.Path<String> pathModelName = poolDelivery.get(TrnDelivery_.modelName);
        // ユニットコード
        jakarta.persistence.criteria.Path<String> pathUnitNo = poolDelivery.get(TrnDelivery_.unitNo);
        // 出庫日
        jakarta.persistence.criteria.Path<Date> pathDeliveryDate = poolDelivery.get(TrnDelivery_.deliveryDate);
        // 払出ステータス
        jakarta.persistence.criteria.Path<DeliveryStatusEnum> pathStatus = poolDelivery.get(TrnDelivery_.status);
        // 出庫ルール
        jakarta.persistence.criteria.Path<Integer> pathDeliveryRule = poolDelivery.get(TrnDelivery_.deliveryRule);

        // 検索条件
        List<Predicate> where = new LinkedList();

        // 出庫番号
        if (Objects.nonNull(condition.getDeliveryNo())) {
            if (condition.isEqualDeliveryNo()) {
                where.add(cb.equal(cb.lower(pathDeliveryNo), adtekfuji.utility.StringUtils.toLowerCase(condition.getDeliveryNo())));
            } else {
                where.add(cb.or(
                        cb.like(cb.lower(pathDeliveryNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getDeliveryNo())) + "%"),
                        cb.like(pathDeliveryNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getDeliveryNo()) + "%")
                ));
            }
        }

        // 製造オーダー番号
        if (Objects.nonNull(condition.getOrderNo())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathOrderNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getOrderNo())) + "%"),
                    cb.like(pathOrderNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getOrderNo()) + "%")
            ));
        }

        // 製造番号(ロット番号)
        if (Objects.nonNull(condition.getSerialNo())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathSerialNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getSerialNo())) + "%"),
                    cb.like(pathSerialNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getSerialNo()) + "%")
            ));
        }
       
        // 機種名
        if (Objects.nonNull(condition.getModelName())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathModelName), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getModelName())) + "%"),
                    cb.like(pathModelName, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getModelName()) + "%")
            ));
        }       
        
        // ユニットコード
        if (Objects.nonNull(condition.getUnitNo())) {
            if (Objects.nonNull(condition.isExactMatch()) && condition.isExactMatch()) {
                where.add(cb.equal(pathUnitNo, condition.getUnitNo()));
            } else {
                where.add(cb.or(
                        cb.like(cb.lower(pathUnitNo), "%" + adtekfuji.utility.StringUtils.escapeLikeChar(adtekfuji.utility.StringUtils.toLowerCase(condition.getUnitNo())) + "%"),
                        cb.like(pathUnitNo, "%" + adtekfuji.utility.StringUtils.escapeLikeChar(condition.getUnitNo()) + "%")
                ));
            }
        }

        // 払出ステータス
        if (Objects.nonNull(condition.getStatuses()) && !condition.getStatuses().isEmpty()) {
            where.add(pathStatus.in(condition.getStatuses()));
        }

        if (Objects.nonNull(condition.getDeliveryRule())) {
            where.add(cb.equal(pathDeliveryRule, condition.getDeliveryRule()));
        } else {
            where.add(pathDeliveryRule.isNotNull());
        }

        // 予定日
        if (Objects.nonNull(condition.getFromDate()) && Objects.nonNull(condition.getToDate())) {
            Date formDate = adtekfuji.utility.DateUtils.getBeginningOfDate(condition.getFromDate());
            Date toDate = adtekfuji.utility.DateUtils.getEndOfDate(condition.getToDate());
            where.add(cb.and(
                    cb.greaterThanOrEqualTo(pathDueDate, formDate),
                    cb.lessThanOrEqualTo(pathDueDate, toDate)));
        } else if (Objects.nonNull(condition.getFromDate())) {
            Date formDate = adtekfuji.utility.DateUtils.getBeginningOfDate(condition.getFromDate());
            where.add(cb.greaterThanOrEqualTo(pathDueDate, formDate));
        } else if (Objects.nonNull(condition.getToDate())) {
            Date toDate = adtekfuji.utility.DateUtils.getEndOfDate(condition.getToDate());
            where.add(cb.lessThanOrEqualTo(pathDueDate, toDate));
        }

        // 出庫日
        if (Objects.nonNull(condition.getFromDeliveryDate()) && Objects.nonNull(condition.getToDeliveryDate())) {
            Date formDate = adtekfuji.utility.DateUtils.getBeginningOfDate(condition.getFromDeliveryDate());
            Date toDate = adtekfuji.utility.DateUtils.getEndOfDate(condition.getToDeliveryDate());
            where.add(cb.and(
                    cb.greaterThanOrEqualTo(pathDeliveryDate, formDate),
                    cb.lessThanOrEqualTo(pathDeliveryDate, toDate)));
        } else if (Objects.nonNull(condition.getFromDeliveryDate())) {
            Date formDate = adtekfuji.utility.DateUtils.getBeginningOfDate(condition.getFromDeliveryDate());
            where.add(cb.greaterThanOrEqualTo(pathDeliveryDate, formDate));
        } else if (Objects.nonNull(condition.getToDeliveryDate())) {
            Date toDate = adtekfuji.utility.DateUtils.getEndOfDate(condition.getToDeliveryDate());
            where.add(cb.lessThanOrEqualTo(pathDeliveryDate, toDate));
        }

        if (SearchType.COUNT.equals(type)) {
            cq.select(cb.count(pathDeliveryNo))
                    .where(cb.and(where.toArray(new Predicate[where.size()])));
        } else {
            cq.select(poolDelivery)
                    .where(cb.and(where.toArray(new Predicate[where.size()])))
                    .orderBy(cb.asc(pathStatus), cb.asc(pathDueDate), cb.asc(pathModelName), cb.asc(pathUnitNo), cb.asc(pathDeliveryNo));
        }

        return this.em.createQuery(cq);
    }

    /**
     * 資材情報の棚卸情報を更新する。
     *
     * @param employeeNo 社員番号
     * @param srcMaterial 資材情報
     * @param srcProduct 部品マスタ
     * @param inventoryNum 棚卸在庫数
     * @param areaName 区画名
     * @param inventoryLocationNo 棚番訂正
     * @return 処理結果
     */
    public Response registInventory(String employeeNo, TrnMaterial srcMaterial, MstProduct srcProduct, Integer inventoryNum, String areaName, String inventoryLocationNo) {
        logger.info("registInventory start: employeeNo={}, srcMaterial={}, srcProduct={}, inventoryNum={}, areaName={}, inventoryLocationNo={}", employeeNo, srcMaterial, srcProduct, inventoryNum, areaName, inventoryLocationNo);
        try {
            // 資材情報を取得
            TrnMaterial material;
            if (StringUtils.startsWith(srcMaterial.getMaterialNo(), TrnMaterial.SUPPLY_PREFIX)
                    || StringUtils.startsWith(srcMaterial.getMaterialNo(), TrnMaterial.ORDER_PREFIX)) {
                material = this.findMaterial(srcMaterial.getMaterialNo(), false);
            } else {
                material = this.findMaterialBySupplyNo(srcMaterial.getSupplyNo(), false);
            }

            if (Objects.isNull(material) && Objects.nonNull(srcProduct)
                    && !StringUtils.isEmpty(srcProduct.getProductNo())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
            }

            return this.doTransaction(WarehouseEvent.INVENTORY_IMPL, employeeNo, material, srcMaterial.getMaterialNo(), null, null, inventoryNum, areaName, inventoryLocationNo, null);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        } finally {
            logger.info("registInventory end.");
        }
    }

    /**
     * 区画名を指定して、棚卸実施中かどうかを取得する。
     *
     * @param areaName 区画名
     * @return 棚卸実施中か？(true:棚卸実施中, false:棚卸未実施)
     */
    public Boolean getAreaInventoryFlag(String areaName) {
        try {
            TypedQuery<Long> query = em.createNamedQuery("MstLocation.getInventoryLocationIds", Long.class);
            query.setParameter("areaName", areaName);
            query.setMaxResults(1);

            return Objects.nonNull(query.getSingleResult());
        } catch (NoResultException ex) {
            return false;
        }
    }

    /**
     * 指定した区画の棚卸を開始して、棚卸作業ができるようにする。
     *
     * @param areaName 区画名　※.省略時は全区画
     * @param reset 棚卸結果を消去して開始する？(null: 結果が存在する場合はエラー, true: 消去して開始, false: 消去せず開始)
     * @param authId 認証ID
     * @return 結果
     */
    public Response inventoryStart(String areaName, Boolean reset, Long authId) {
        logger.info("inventoryStart: areaName={}, reset={}, authId={}", areaName, reset, authId);
        try {
            if (Objects.isNull(authId)) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            String personNo = this.findOrganizationIdentify(authId);
            if (StringUtils.isEmpty(personNo)) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            boolean isInitInventory = false;
            if (Objects.isNull(reset)) {
                // 区画名を指定して、最後の棚卸実施日を取得する。(中断後の再開時のみ日時が返ってくる)
                Date lastInventoryDate = this.getLastInventoryDate(areaName);
                if (Objects.nonNull(lastInventoryDate)) {
                    // 棚卸結果が存在する。
                    return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_INVENTORY_RESULT)
                            .userData(lastInventoryDate)).build();
                }
            } else if (reset) {
                // 指定区画の棚卸結果を消去する。
                isInitInventory = true;
            }

            // 区画名を指定して、資材情報の棚卸実施フラグをONにする。
            this.updateMaterialInventoryFlag(areaName, true, isInitInventory);

            // 区画名を指定して、棚マスタの棚卸実施フラグをONにする。
            this.updateLocationInventoryFlag(areaName, true);

            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 棚卸を完了して、棚卸結果を資材情報に反映する。
     *
     * @param areaName 区画名　※.省略時は全区画
     * @param authId 認証ID
     * @return 結果
     */
    public Response inventoryComplete(String areaName, Long authId) {
        logger.info("inventoryComplete: areaName={}, authId={}", areaName, authId);
        try {
            if (Objects.isNull(authId)) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            Date now = new Date();

            String personNo = this.findOrganizationIdentify(authId);
            if (StringUtils.isEmpty(personNo)) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            // 区画名を指定して、棚卸実施中の資材情報一覧を取得する。
            TypedQuery<TrnMaterial> query = this.em.createNamedQuery("TrnMaterial.findInventoryByAreaName2", TrnMaterial.class);
            query.setParameter("areaName", areaName);
            List<TrnMaterial> materials = query.getResultList();

            if (materials.isEmpty()) {
                // 棚卸開始していない。
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOT_START_INVENTORY)).build();
            }
            
            //Date term = DateUtils.addMonths(now, 6);

            // 資材情報の棚卸結果を在庫に反映する。
            for (TrnMaterial material : materials) {
                material.setInventoryFlag(false);

                if (Objects.isNull(material.getInventoryNum())) {
                    continue; // 棚卸未実施の資材は無視する
    
                    // 棚卸未実施の資材は、棚卸在庫数「0」で棚卸した事にする。
                    //TypedQuery<LogStock> query = em.createNamedQuery("LogStock.lastByMaterialNo", LogStock.class);
                    //query.setParameter("materialNo", material.getMaterialNo());
                    //query.setMaxResults(1);
                    //List<LogStock> list = query.getResultList();
                    //if (list.isEmpty()) {
                    //    continue;
                    //}
                    //
                    //LogStock logStosk = list.get(0);
                    //if (term.after(logStosk.getEventDate())) {
                    //    // 保持期間内
                    //    continue;       
                    //}

                    //material.setInventoryNum(0);
                    //material.setInventoryPersonNo(personNo);
                    //material.setInventoryDate(now);
                }

                // 棚卸実施者を取得
                //String worker = "";
                //TypedQuery<LogStock> query1 = em.createNamedQuery("LogStock.lastByMaterialNoAndEventKind", LogStock.class);
                //query1.setParameter("materialNo", material.getMaterialNo());
                //query1.setParameter("eventKind", WarehouseEvent.INVENTORY_IMPL.getId());
                //query1.setMaxResults(1);
                //List<LogStock> list = query1.getResultList();
                //if (!list.isEmpty()) {
                //    worker = list.get(0).getPersonNo();
                //}

                // 在庫調整
                Integer adjustment = material.getInventoryNum() - material.getInStockNum();

                // 棚卸在庫数
                material.setInStockNum(material.getInventoryNum());

                MstLocation oldLocation = material.getLocation();
                int oldInStockNum = material.getInStockNum();

                // 棚番訂正
                if (Objects.nonNull(material.getInventoryLocation())) {
                    material.setLocation(material.getInventoryLocation());
                }

                // 更新日時
                material.setUpdateDate(now);

                // 棚卸結果をクリアする。(棚卸実施日と棚卸実施者は残す)
                material.setInventoryNum(null);
                material.setInventoryLocation(null);

                // 在庫マスタの在庫数を再計算する。
                // TODO 処理が遅いため修正が必要。
                //if (Objects.nonNull(material.getLocation())) {
                //    this.updateStock(material.getLocation(), material.getProduct(), material.getInStockNum(), now, oldLocation, oldInStockNum);
                //}

                // 入出庫実績に棚卸実績を登録する。
                LogStock logStock = LogStock.createInventoryLog(WarehouseEvent.INVENTORY, material, personNo, now, adjustment);
                this.em.persist(logStock);
            }

            this.em.flush();
            this.em.clear();

            // 資材情報の保持月数が設定されている場合、不要な資材情報を削除する。
            int deleteMonth = ServiceConfig.getInstance().getMaterialDeleteMonth();
            if (deleteMonth >= 0) {
                Date deleteDate = null;
                if (deleteMonth > 0) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.MONTH, -deleteMonth);
                    deleteDate = calendar.getTime();
                }

                // 区画名を指定して、入出庫が完了した資材情報を削除する。
                this.deleteCompMaterialByAreaName(areaName, deleteDate);
            }
            
            // 区画名を指定して、資材情報の棚卸実施フラグをOFFにする。
            this.updateMaterialInventoryFlag(areaName, false, false);
            
            // 区画名を指定して、棚マスタの棚卸実施フラグをOFFにする。
            this.updateLocationInventoryFlag(areaName, false);

            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        } finally {
            logger.info("inventoryComplete end.");
        }
    }

    /**
     * 指定した区画の棚卸を中止する。
     *
     * @param areaName 区画名　※.省略時は全区画
     * @param authId 認証ID
     * @return 結果
     */
    public Response inventoryCancel(String areaName, Long authId) {
        logger.info("inventoryCancel: areaName={}, authId={}", areaName, authId);
        try {
            if (Objects.isNull(authId)) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            String personNo = this.findOrganizationIdentify(authId);
            if (StringUtils.isEmpty(personNo)) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            if (!this.getAreaInventoryFlag(areaName)) {
                // 棚卸開始していない。
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOT_START_INVENTORY)).build();
            }

            // 区画名を指定して、資材情報の棚卸実施フラグをOFFにする。
            this.updateMaterialInventoryFlag(areaName, false, false);

            // 区画名を指定して、棚マスタの棚卸実施フラグをOFFにする。
            this.updateLocationInventoryFlag(areaName, false);

            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 
     * @param organizationId
     * @return 
     */
    private String findOrganizationIdentify(Long organizationId) {
        try {
            if (Objects.isNull(organizationId)) {
                return null;
            }

            TypedQuery<String> query = this.em.createNamedQuery("OrganizationEntity.findIdentifyById", String.class);
            query.setParameter("organizationId", organizationId);

            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * 区画名を指定して、棚マスタの棚卸実施フラグを更新する。
     *
     * @param areaName 区画名
     * @param inventoryFlag 棚卸実施フラグ
     */
    private void updateLocationInventoryFlag(String areaName, boolean inventoryFlag) {
        Query query;
        if (adtekfuji.utility.StringUtils.isEmpty(areaName)) {
            // すべての棚の棚卸実施フラグを更新する。
            query = this.em.createNamedQuery("MstLocation.updateAllInventoryFlag");
        } else {
            // 区画名を指定して、棚卸実施フラグを更新する。
            query = this.em.createNamedQuery("MstLocation.updateAreaInventoryFlag");
            query.setParameter("areaName", areaName);
        }

        query.setParameter("inventoryFlag", inventoryFlag);

        query.executeUpdate();
    }

    /**
     * 区画名を指定して、資材情報の棚卸実施フラグを更新する。
     *
     * @param areaName 区画名
     * @param inventoryFlag 棚卸実施フラグ
     * @param isInit 棚卸実績を消去する？
     */
    private void updateMaterialInventoryFlag(String areaName, boolean inventoryFlag, boolean isInit) {
        Query query;
        if (adtekfuji.utility.StringUtils.isEmpty(areaName)) {
            // すべての棚の棚卸実施フラグを更新する。
            if (isInit) {
                query = this.em.createNamedQuery("TrnMaterial.updateAllInventoryFlagAndInit");
            } else {
                query = this.em.createNamedQuery("TrnMaterial.updateAllInventoryFlag");
            }
        } else {
            // 区画名を指定して、棚卸実施フラグを更新する。
            if (isInit) {
                query = this.em.createNamedQuery("TrnMaterial.updateAreaInventoryFlagAndInit");
            } else {
                query = this.em.createNamedQuery("TrnMaterial.updateAreaInventoryFlag");
            }

            query.setParameter("areaName", areaName);
        }

        query.setParameter("inventoryFlag", inventoryFlag);

        query.executeUpdate();
    }

    /**
     * 区画名を指定して、棚卸実施中の資材情報一覧を取得する。
     *
     * @param areaName 区画名
     * @return 資材情報一覧
     */
    private List<TrnMaterial> findInventoryMaterials(String areaName) {
        TypedQuery<TrnMaterial> query;
        if (StringUtils.isEmpty(areaName)) {
            query = this.em.createNamedQuery("TrnMaterial.findInventoryByAllLocation", TrnMaterial.class);
        } else {
            query = this.em.createNamedQuery("TrnMaterial.findInventoryByAreaName", TrnMaterial.class);
            query.setParameter("areaName", areaName);
        }

        return query.getResultList();
    }

    /**
     * 在庫マスタを更新する。
     * 
     * @param location 棚マスタ
     * @param product 部品マスタ
     * @param stockNum 数量
     * @param date 更新日時
     * @param oldLocation 古い棚マスタ
     * @param oldStockNum 古い数量
     */
    private void updateStock(MstLocation location, MstProduct product, int stockNum, Date date, MstLocation oldLocation, int oldStockNum) {
        if (Objects.isNull(location)) {
            return;
        }

        // 在庫マスタを取得する。
        MstStock stock = this.findStock(location.getLocationId(), product.getProductId());
        if (Objects.isNull(stock)) {
            // 存在しない場合は新規作成する。
            stock = new MstStock(location, product, date);
            this.em.persist(stock);
        }

        if (Objects.equals(location, oldLocation) || Objects.isNull(oldLocation)) {
            // 棚番訂正なし

            // 在庫数を更新する。
            stock.setStockNum(stock.getStockNum() + stockNum);
        } else {
            // 棚番訂正あり

            // 棚番訂正前の棚から在庫を削除する。
            MstStock oldStock = this.findStock(oldLocation.getLocationId(), product.getProductId());
            // 在庫情報からインポートされた資材情報は棚番号がUNKNOWNとなり、在庫が存在しない
            if (Objects.nonNull(oldStock)) {
                oldStock.setStockNum(oldStock.getStockNum() - oldStockNum);
                oldStock.setStockDate(date);
            }

            // 棚番訂正後の棚に、棚卸在庫数分の在庫を追加する。
            stock.setStockNum(stock.getStockNum() + stockNum);
        }

        stock.setStockDate(date);
    }

    /**
     * 指定した資材情報の在庫数を更新する。
     *
     * @param materialNo 資材番号
     * @param quantity 在庫数
     * @param authId 認証ID(更新者)
     * @return 結果
     */
    public Response updateMaterialInStock(String materialNo, Integer quantity, Long authId) {
        logger.info("updateMaterialInStock: materialNo={}, quantity={}, authId={}", materialNo, quantity, authId);
        try {
            if (StringUtils.isEmpty(materialNo)
                    || Objects.isNull(quantity)
                    || quantity < 0) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            Date now = new Date();

            String personNo = this.findOrganizationIdentify(authId);

            // 資材情報を取得する。
            TrnMaterial material = this.findMaterial(materialNo, false);

            int oldInStockNum = material.getInStockNum();
            if (oldInStockNum == quantity) {
                // 変更なし
                return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
            }

            material.setInStockNum(quantity);
            material.setUpdateDate(now);

            this.updateStock(material.getLocation(), material.getProduct(), quantity, now, material.getLocation(), oldInStockNum);

            em.flush();
            em.clear();

            // 入出庫実績にデータ修正実績を登録する。
            LogStock logStock = LogStock.createInventoryLog(WarehouseEvent.EDIT_DATA, material, personNo, now, null);
            this.em.persist(logStock);

            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }
    
    /**
     * 検査結果を更新する。
     * 
     * @param materialNo 資材番号
     * @param defectNum 不良品数
     * @param note コメント
     * @param personNo 社員番号
     * @return 
     */
    public Response updateMaterialInspection(String materialNo, Integer defectNum, String note, String personNo) {
        logger.info("updateMaterialInspection: materialNo={}, defectNum={}, personNo={}", materialNo, defectNum, personNo);
        try {
            if (StringUtils.isEmpty(materialNo)
                    || Objects.isNull(defectNum)
                    || Objects.isNull(personNo)) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            Date now = new Date();

            TrnMaterial material = this.findMaterial(materialNo, false);

            material.setDefectNum(Objects.nonNull(material.getDefectNum()) ? material.getDefectNum() + defectNum : defectNum);
            material.setStockNum(material.getStockNum() - defectNum);
            material.setInStockNum(material.getInStockNum() - defectNum);
            material.setInspectedAt(now);
            material.setUpdateDate(now);

            em.flush();
            em.clear();

            LogStock logStock = LogStock.createInspectionLog(material, defectNum, note, personNo, now);
            this.em.persist(logStock);

            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 入出庫実績を更新する。
     * 
     * @param logStock 入出庫実績
     * @return 処理応答
     */
    public Response updateLogStock(LogStock logStock) {
        try {
            TypedQuery<LogStock> query = em.createNamedQuery("LogStock.find", LogStock.class);
            query.setParameter("eventId", logStock.getEventId());

            LogStock update = query.getSingleResult();
            update.setOrderNo(logStock.getOrderNo());
            
            em.flush();
            em.clear();
            
            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
 
        } catch (NoResultException ex) {
            logger.fatal(ex, ex);
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_LOGSTOCK)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }    
    }

    /**
     * ファイルを削除する。
     *
     * @param file ファイル
     */
    private void deleteFile(File file) {
        try {
            if (Objects.isNull(file) || !file.exists()) {
                return;
            }

            file.delete();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 区画名を指定して、入出庫が完了した資材情報を削除する。(予定数入庫済で在庫なしの資材情報)
     *
     * @param areaName 区画名
     * @param deleteDate 削除期限(これ以前の更新日を対象とする) ※null:全て
     * @return 削除数
     */
    private int deleteCompMaterialByAreaName(String areaName, Date deleteDate) {
        logger.info("deleteCompMaterialByAreaName: areaName={}, deleteDate={}", areaName, deleteDate);
        try {
            Query query;
            if (Objects.isNull(deleteDate)) {
                query = this.em.createNamedQuery("TrnMaterial.deleteCompMaterialByAreaName");
            } else {
                query = this.em.createNamedQuery("TrnMaterial.deleteCompMaterialByAreaNameDate");
                query.setParameter("deleteDate", deleteDate);
            }

            query.setParameter("areaName", areaName);

            int rows = query.executeUpdate();
            logger.info("Deleted the TrnMaterial: {}", rows);

            return rows;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0;
        }
    }

    /**
     * 区画名を指定して、最後の棚卸実施日を取得する。(棚卸在庫数が入力されている情報がある場合のみ日時が返る)
     *
     * @param areaName 区画名
     * @return 最後の棚卸実施日
     */
    private Date getLastInventoryDate(String areaName) {
        logger.info("getLastInventoryDate: areaName={}", areaName);
        try {
            TypedQuery<Date> query = this.em.createNamedQuery("TrnMaterial.lastInventoryDateByAreaName", Date.class);
            query.setParameter("areaName", areaName);

            return query.getSingleResult();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }
    
    /**
     * 出庫情報を更新する。
     * 
     * @param deliveryItem 出庫情報
     * @return 処理結果
     */
    public Response updateDeliveryItem(TrnDeliveryItem deliveryItem) {
        try {
            em.merge(deliveryItem);
            
            em.flush();
            em.clear();
            
            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }        
    }

    /**
     * 払出指示情報を追加する。
     * 
     * @param param 払出指示情報
     * @param authId 認証ID
     * @return 処理結果
     */
    @ExecutionTimeLogging   
    public Response addDelivery(DeliveryParam param, Long authId) {
        if (Objects.isNull(param) || Objects.isNull(param.getDeliveries())) {
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }
 
        try {
            Date now = new Date();
            
            for (TrnDelivery delivery : param.getDeliveries()) {
                delivery.setCreateDate(now);
                delivery.setUpdateDate(now);
                this.em.persist(delivery);
                
                for (TrnDeliveryItem deliveryItem : delivery.getDeliveryList()) {

                    if (Objects.isNull(this.findProduct(deliveryItem.getProduct().getProductNo(), true))) {
                        MstProduct product = deliveryItem.getProduct();
                        product.setCreateDate(now);
                        product.setUpdateDate(now);
                        this.em.persist(product);
                    }

                    deliveryItem.setCreateDate(now);
                    deliveryItem.setUpdateDate(now);
                    this.em.persist(deliveryItem);
                }

                // データベースへの反映とキャッシュをクリア
                em.flush();
                em.clear();
            }
            
            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }        
    }
    
    /**
     * 払出ステータスを更新する。
     * 
     * @param deliveryNo 払出指示番号
     * @param status 払出ステータス 
     * @return 処理結果
     */
    public Response updateDeliveryStatus(String deliveryNo, DeliveryStatusEnum status) {
        logger.info("updateDeliveryStatus: deliveryNo={}, status={}", deliveryNo, status);

        try {
            TrnDelivery delivery = this.findDelivery(deliveryNo, false);
            if (Objects.nonNull(delivery)) {
            
                delivery.setStatus(status);
                delivery.setUpdateDate(new Date());
                
                em.flush();
                em.clear();

                return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
            }

            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_DELIVERY)).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }        
        
    }

    /**
     * 払出指示の払出予定日を更新する。
     * 
     * @param deliveryNo 払出指示番号
     * @param date 払出予定日
     * @return 処理結果
     */
    public Response updateDeliveryDate(String deliveryNo, Date date) {
        logger.info("updateDeliveryDate: deliveryNo={}, date={}", deliveryNo, date);
        
        try {
            TrnDelivery delivery = this.findDelivery(deliveryNo, false);
            if (Objects.nonNull(delivery)) {
            
                delivery.setDueDate(date);
                delivery.setUpdateDate(new Date());
                
                em.flush();
                em.clear();

                return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
            }

            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_DELIVERY)).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }     
    }

    /**
     * すべてのユニットNoを取得する。
     * 
     * @param isDelivery
     * @return ユニットNo一覧
     */
    public List<String> findUnitAll(boolean isDelivery) {
        try {
            Query query = this.em.createNamedQuery(isDelivery ? "TrnDelivery.findUnitAllDelivery" : "TrnDelivery.findUnitAll", String.class);
            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * すべての機種名を取得する。
     * 
     * @param isDelivery
     * @return 機種名一覧
     */
    public List<String> findModelAll(boolean isDelivery) {
        try {
            Query query = this.em.createNamedQuery(isDelivery ? "TrnDelivery.findModelAll" : "TrnDelivery.findModelAllDelivery", String.class);
            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
    
    /**
     * 払出指示情報を削除する。
     * 
     * @param deliveryNos
     * @return 
     */
    public SampleResponse removeDelivery(List<String> deliveryNos) {
        logger.info("removeDelivery: count={}", deliveryNos.size());

        try {
            TypedQuery<TrnDelivery> query = em.createNamedQuery("TrnDelivery.findAll", TrnDelivery.class);
            query.setParameter("deliveryNo", deliveryNos);
            query.setLockMode(LockModeType.OPTIMISTIC);
            List<TrnDelivery> list = query.getResultList();

            List<String> undeleteItems = new ArrayList<>();
            
            for (TrnDelivery delivery : list) {
                
                if (DeliveryStatusEnum.WORKING.equals(delivery.getStatus()) 
                        || DeliveryStatusEnum.SUSPEND.equals(delivery.getStatus())) {
                    // 払出中・中断中の払出指示は削除不可
                    undeleteItems.add(delivery.getDeliveryNo());
                    continue;
                }
                
                int itemRows = delivery.getDeliveryList().size();

                // 不要なトレース情報を削除
                TypedQuery<TrnLotTrace> deleteQuery = em.createNamedQuery("TrnLotTrace.deleteByDeliveryNo", TrnLotTrace.class);
                deleteQuery.setParameter("deliveryNo", delivery.getDeliveryNo());
                int lotTraceRows = deleteQuery.executeUpdate();

                // 出庫アイテム情報と在庫引当情報を削除
                delivery.getDeliveryList().forEach(o -> em.remove(o));

                // 出庫指示情報を削除
                em.remove(delivery);

                logger.info("removeDelivery: deliveryNo={}, itemRows={}, lotTraceRows={}", delivery.getDeliveryNo(), itemRows, lotTraceRows);
            }

            em.flush();
            em.clear();
           
            return new SampleResponse(ServerErrorTypeEnum.SUCCESS.name(), undeleteItems);
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new SampleResponse(ServerErrorTypeEnum.SERVER_FETAL.name(), null);
        }
    }

    /**
     * 自動引当を行う。
     * 
     * @param deliveryNos 払出指示番号
     * @param areaName 区画名
     * @param authId 認証ID
     * @return 処理結果
     */
    public Response reserveInventoryAuto(List<String> deliveryNos, String areaName, Long authId) {
        logger.info("reserveInventoryAuto: areaName={}, authId={}", areaName, authId);

        try {
            Date now = new Date();

            String personNo = this.findOrganizationIdentify(authId);
            if (StringUtils.isEmpty(personNo)) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }
            
            // 在庫引当を解除
            //Query deleteQuery = em.createNamedQuery("TrnReserveMaterial.deleteAll");
            //deleteQuery.setParameter("deliveryNo", deliveryNos);
            //deleteQuery.executeUpdate();
            
            TypedQuery<TrnDelivery> query = em.createNamedQuery("TrnDelivery.findAll", TrnDelivery.class);
            query.setParameter("deliveryNo", deliveryNos);
            query.setLockMode(LockModeType.OPTIMISTIC);
            List<TrnDelivery> list = query.getResultList();
            
            list.stream()
                .flatMap(o -> o.getDeliveryList().stream())
                    .forEach(o -> {
                        o.getReserveMaterials().clear();
                        o.setReserve(0);
                        o.setUpdateDate(now);
                    });
            
            em.flush();

            for (TrnDelivery delivery : list) {
                // 欠品数
                delivery.setStockOutNum(0);

                // 部品IDを抽出
                List<Long> productIds = delivery.getDeliveryList().stream()
                        .map(o -> o.getProduct().getProductId()).collect(Collectors.toList());

                // 資材情報(在庫あり)を抽出
                TypedQuery<TrnMaterial> query2 = em.createNamedQuery("TrnMaterial.findInStock", TrnMaterial.class);
                query2.setParameter("areaName", areaName);
                query2.setParameter("productIds", productIds);
                List<TrnMaterial> materialList = query2.getResultList();
                Map<Long, List<TrnMaterial>> materialMap = materialList.stream()
                        .collect(Collectors.groupingBy(o -> o.getProduct().getProductId()));
                
                for (TrnDeliveryItem deliveryItem : delivery.getDeliveryList()) {
                    if (Objects.equals(deliveryItem.getArrange(), 2)) {
                        // 在庫品(消耗品)
                        deliveryItem.setReserve(3);
                        continue;
                    }
                    
                    List<TrnMaterial> materials = materialMap.get(deliveryItem.getProduct().getProductId());

                    int reserve = 0;
                    int reqNum = deliveryItem.getRequiredNum() - deliveryItem.getDeliveryNum();
                    List<TrnReserveMaterial> reserveMaterials = new ArrayList<>();
                    
                    if (Objects.isNull(materials)) {
                        deliveryItem.setReserve(reserve);
                        delivery.setStockOutNum(delivery.getStockOutNum() + reqNum);
                        continue;
                    }

                    if (!StringUtils.isEmpty(deliveryItem.getArrangeNo())) {
                        // 先行手配がある場合
                        List<String> supplyNos = WarehouseUtils.extractSupplyNo(deliveryItem.getArrangeNo());

                        List<TrnMaterial> arrangeMaterials = materials.stream()
                                .filter(o -> supplyNos.contains(o.getSupplyNo()))
                                .collect(Collectors.toList());

                        for (TrnMaterial material : arrangeMaterials) {
                            if (reqNum == 0) {
                                break;
                            }
                            
                            int availableNum = material.getAvailableNum();
                            int reservedNum = (reqNum <= availableNum) ? reqNum : availableNum;

                            if (reservedNum > 0) {
                                TrnReserveMaterial reserveMaterial = new TrnReserveMaterial(deliveryItem.getPK().getDeliveryNo(), deliveryItem.getPK().getItemNo(), material.getMaterialNo(), reservedNum, now, personNo);
                                reserveMaterials.add(reserveMaterial);

                                reqNum -= reservedNum;
                                reserve = (reqNum == 0) ? 3 : 1;  // 3: 引当済[○]、1: 一部引当[△]
                                
                                material.getReserveMaterials().add(reserveMaterial);
                            }
                        }
                    }

                    if (reqNum > 0) {
                        // 払出指示と同じ図番、同じ製番の部品在庫がある場合、無条件にその部品在庫を引当て
                        List<TrnMaterial> choiceList = materials.stream()
                                .filter(o -> o.containsOrderNo(delivery.getModelName(), deliveryItem.getOrderNo()))
                                .sorted(TrnMaterial.materialNoComparator)
                                .collect(Collectors.toList());

                        for (TrnMaterial material : choiceList) {
                            int availableNum = material.getAvailableNum();
                            int reservedNum = (reqNum <= availableNum) ? reqNum : availableNum;

                            if (reservedNum > 0) {
                                TrnReserveMaterial reserveMaterial = new TrnReserveMaterial(deliveryItem.getPK().getDeliveryNo(), deliveryItem.getPK().getItemNo(), material.getMaterialNo(), reservedNum, now, personNo);
                                reserveMaterials.add(reserveMaterial);

                                reqNum -= reservedNum;
                                reserve = (reqNum == 0) ? 3 : 1;  // 3: 引当済[○]、1: 一部引当[△]

                                material.getReserveMaterials().add(reserveMaterial);
                            }

                            if (reqNum == 0) {
                                break;
                            }
                        }

                        // ② 払出指示と同じ製番の部品在庫はなく、同じ図番の部品在庫はある場合、入庫順に在庫引当て
                        for (TrnMaterial material : materials) {
                            if (reqNum == 0) {
                                break;
                            }
                            
                            if (reserveMaterials.stream()
                                    .filter(o -> StringUtils.equals(o.getPK().getMaterialNo(), material.getMaterialNo()))
                                    .findFirst()
                                    .isPresent()) {
                                continue;
                            }
                                    
                            int availableNum = material.getAvailableNum();
                            int reservedNum = (reqNum <= availableNum) ? reqNum : availableNum;

                            if (reservedNum > 0) {
                                TrnReserveMaterial reserveMaterial = new TrnReserveMaterial(deliveryItem.getPK().getDeliveryNo(), deliveryItem.getPK().getItemNo(), material.getMaterialNo(), reservedNum, now, personNo);
                                reserveMaterials.add(reserveMaterial);

                                reqNum -= reservedNum;
                                reserve = (reqNum == 0) ? 4 : 2;  // 4: 引当済(製番違い)[○]、2: 一部引当(製番違い)[△]

                                material.getReserveMaterials().add(reserveMaterial);
                            }
                        }
                    }

                    logger.info("reserveInventory: deliveryNo={}, itemNo={}, productNo={}, itemNo={}, reserve={}", 
                            deliveryItem.getPK().getDeliveryNo(), deliveryItem.getPK().getItemNo(), deliveryItem.getProduct().getProductNo(), deliveryItem.getRequiredNum(), reserve);

                    reserveMaterials.forEach(o -> {
                        em.persist(o);
                        logger.info("reserveInventory: ", o);
                    });

                    deliveryItem.setReserve(reserve);
                    deliveryItem.setReserveMaterials(reserveMaterials);
                    
                    delivery.setStockOutNum(delivery.getStockOutNum() + reqNum);
                }
            }

            em.flush();
            em.clear();

            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 在庫引当を行う。
     * 
     * @param param 在庫引当情報
     * @param authId 認証ID
     * @return 処理結果
     */
    public Response reserveInventory(ReserveInventoryParam param, Long authId) {
        logger.info("reserveInventory: authId={}", authId);

        if (Objects.isNull(param) || Objects.isNull(param.getReserveMaterials())) {
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        try {
            Date now = new Date();

            String personNo = this.findOrganizationIdentify(authId);
            if (StringUtils.isEmpty(personNo)) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            TrnDelivery delivery = this.findDelivery(param.getDeliveryNo(), false);
            if (Objects.isNull(delivery)) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_DELIVERY)).build();
            }

            TrnDeliveryItem deliveryItem = this.findDeliveryItem(param.getDeliveryNo(), param.getItemNo(), false);
            if (Objects.isNull(deliveryItem)) {
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_DELIVERY)).build();
            }
            
            int reqNum = deliveryItem.getRequiredNum();
            boolean specified = true;
            
            List<TrnReserveMaterial> reserveMaterials = new ArrayList<>();
            
            for (TrnReserveMaterial src : param.getReserveMaterials()) {
                if (src.getReservedNum() <= 0) {
                    continue;
                }
                
                TrnMaterial material = this.findMaterial(src.getMaterialNo(), true);

                if (material.getAvailableNum() < src.getReservedNum()) {
                    return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
                }

                TrnReserveMaterial reserveMaterial = new TrnReserveMaterial(src.getDeliveryNo(), src.getItemNo(), src.getMaterialNo(), src.getReservedNum(), src.getReservedAt(), personNo);
                reserveMaterial.setNote(src.getNote());

                reserveMaterials.add(reserveMaterial);
                
                reqNum -= src.getReservedNum();
                specified = specified & material.containsOrderNo(delivery.getModelName(), deliveryItem.getOrderNo());
            }
            
            // 引当解除
            if (!deliveryItem.getReserveMaterials().isEmpty()) {
                Response response = this.releaseReservation(param.getDeliveryNo(), param.getItemNo());
                if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                    return response;
                }
            }

            if (reqNum != deliveryItem.getRequiredNum()) {
                if (specified) {
                    deliveryItem.setReserve(reqNum == 0 ? 3 : 1);  // 3: 引当済[○]、1: 一部引当[△]
                } else {
                    deliveryItem.setReserve(reqNum == 0 ? 4 : 2);  // 4: 引当済(製番違い)[○]、2: 一部引当(製番違い)[△]
                }
            } else {
                deliveryItem.setReserve(0);
            }
           
            reserveMaterials.forEach(o -> {
                em.persist(o);
                logger.info("reserveInventory: ", o);
            });

            deliveryItem.setReserveMaterials(reserveMaterials);
            deliveryItem.setUpdateDate(now);

            delivery.setStockOutNum(reqNum);
            delivery.setUpdateDate(now);

            em.flush();
            em.clear();
                    
            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }
    
    /**
     * 在庫引当を解除する。
     * 
     * @param deliveryNos 払出指示番号
     * @return 
     */
    public Response releaseReservationAll(List<String> deliveryNos) {
        logger.info("releaseReservation: ");

        try {
            Date now = new Date();
           
            TypedQuery<TrnDelivery> query = em.createNamedQuery("TrnDelivery.findAll", TrnDelivery.class);
            query.setParameter("deliveryNo", deliveryNos);
            query.setLockMode(LockModeType.OPTIMISTIC);
            List<TrnDelivery> list = query.getResultList();

            for (TrnDelivery delivery : list) {
                delivery.setStockOutNum(0);
                delivery.getDeliveryList().stream()
                        .filter(o -> !Objects.equals(o.getArrange(), 2))
                        .forEach(o -> {
                    o.getReserveMaterials().clear();
                    o.setReserve(0);
                    o.setUpdateDate(now);
                    delivery.setStockOutNum(delivery.getStockOutNum() + o.getRequiredNum());
                });
                delivery.setUpdateDate(now);
            };

            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build(); 
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }
 
    /**
     * 在庫引当を解除する。
     * 
     * @param deliveryNo 払出指示番号
     * @param itemNo
     * @return 
     */
    public Response releaseReservation(String deliveryNo, Integer itemNo) {
        logger.info("releaseReservation: deliveryNo={}, itemNo={}", deliveryNo, itemNo);

        try {
            Date now = new Date();

            TypedQuery<Long> sumQuery = em.createNamedQuery("TrnReserveMaterial.sumByItemNo", Long.class);
            sumQuery.setParameter("deliveryNo", deliveryNo);
            sumQuery.setParameter("itemNo", itemNo);
            Long reservedNum = sumQuery.getSingleResult();
            if (Objects.isNull(reservedNum)) {
                reservedNum = 0L;
            }
            
            //Query deleteQuery = em.createNamedQuery("TrnReserveMaterial.deleteByItemNo");
            //deleteQuery.setParameter("deliveryNo", deliveryNo);
            //deleteQuery.setParameter("itemNo", itemNo);
            //int deletedRows = deleteQuery.executeUpdate();

            TypedQuery<TrnDeliveryItem> query = em.createNamedQuery("TrnDeliveryItem.find", TrnDeliveryItem.class);
            query.setParameter("deliveryNo", deliveryNo);
            query.setParameter("itemNo", itemNo);
            query.setLockMode(LockModeType.OPTIMISTIC);
            TrnDeliveryItem deliveryItem = query.getSingleResult();

            deliveryItem.getReserveMaterials().clear();
            deliveryItem.setReserve(0);
            deliveryItem.setUpdateDate(now);

            TypedQuery<TrnDelivery> deliveryQuery = em.createNamedQuery("TrnDelivery.find", TrnDelivery.class);
            deliveryQuery.setParameter("deliveryNo", deliveryNo);
            deliveryQuery.setLockMode(LockModeType.OPTIMISTIC);
            TrnDelivery delivery = deliveryQuery.getSingleResult();

            delivery.setStockOutNum(delivery.getStockOutNum() + reservedNum.intValue());
            delivery.setUpdateDate(now);
            
            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build(); 
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }
    
    /**
     * 在庫引当情報を取得する。
     * 
     * @param deliveryNo
     * @param itemNo
     * @param materialNo
     * @param isReadOnly
     * @return 
     */
    @Lock(LockType.READ)
    public TrnReserveMaterial findReserveMaterial(String deliveryNo, Integer itemNo, String materialNo, boolean isReadOnly) {
        try {
            TypedQuery<TrnReserveMaterial> query = em.createNamedQuery("TrnReserveMaterial.find", TrnReserveMaterial.class);
            query.setParameter("deliveryNo", deliveryNo);
            query.setParameter("itemNo", itemNo);
            query.setParameter("materialNo", materialNo);
            if (!isReadOnly) {
                query.setLockMode(LockModeType.OPTIMISTIC);
            }
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
    /**
     * 在庫引当情報の件数を取得する。
     * 
     * @param materialNo 資材番号
     * @return 
     */
    @Lock(LockType.READ)
    protected Long countReserveMaterial(String materialNo) {
        try {
            TypedQuery<Long> query = em.createNamedQuery("TrnReserveMaterial.countByMaterialNo", Long.class);
            query.setParameter("materialNo", materialNo);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return 0L;
        }
    }

    /**
     * 在庫払出かどうかを返す。
     * 
     * @param delivery
     * @return 
     */
    private boolean isWithdraw(TrnDelivery delivery) {
        return Objects.nonNull(delivery) && Objects.equals(delivery.getDeliveryRule(), 2);
    }
    
    /**
     * 在庫払出処理を実行する。
     * 
     * @param deliveryNo 出庫指示番号
     * @param employeeNo 社員番号
     * @return 処理結果
     */
    public Response doDelivery(String deliveryNo, String employeeNo) {
        try {
            TrnDelivery delivery = this.findDelivery(deliveryNo,  false);
            if (!this.isWithdraw(delivery)) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }
            
            if (DeliveryStatusEnum.COMPLETED.equals(delivery.getStatus())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SUCCESS)).build();
            }
            
            Date now = new Date();
            
            for (TrnDeliveryItem deliveryItem : delivery.getDeliveryList()) {
                int withdrawNum = 0;
                
                for (TrnReserveMaterial reserveMaterial : deliveryItem.getReserveMaterials()) {
                    withdrawNum += reserveMaterial.getDeliveryNum();

                    // 資材情報
                    TrnMaterial material = reserveMaterial.getMaterial();
                    material.setInStockNum(material.getInStockNum() - reserveMaterial.getDeliveryNum());
                    material.setUpdateDate(now);
                    em.merge(material);
                    
                    // 在庫マスタ
                    this.updateStock(material.getLocation(), material.getProduct(), -reserveMaterial.getDeliveryNum(), now, null, 0);

                    // 入出庫履歴
                    LogStock logStock = LogStock.createLeaveLog(WarehouseEvent.DELIVERY.getId(), material, deliveryItem, reserveMaterial.getDeliveryNum(), employeeNo, now);
                    logStock.setOrderNo(delivery.getOrderNo());
                    em.persist(logStock);
                }

                deliveryItem.setWithdrawNum(withdrawNum);
                deliveryItem.setUpdateDate(now);
                em.merge(deliveryItem);
            }
            
            delivery.setStatus(DeliveryStatusEnum.COMPLETED);
            delivery.setDeliveryDate(now);

            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
       
        return null;
    }
    
    /**
     * 
     * @param material
     * @return 
     */
    public boolean updateReserveMaterial(TrnMaterial material) {

        // 在庫不足の部品
        TypedQuery<TrnDeliveryItem> query = this.em.createNamedQuery("TrnDeliveryItem.findOutOfStock", TrnDeliveryItem.class);
        query.setParameter("productId", material.getProduct().getProductId());
        List<TrnDeliveryItem> deliveryItems = query.getResultList();
        
        if (deliveryItems.isEmpty()) {
            return false;
        }
        
        List<String> deliveryNos = deliveryItems.stream()
                .map(o -> o.getPK().getDeliveryNo())
                .distinct()
                .collect(Collectors.toList());
        
        TypedQuery<TrnDelivery> query2 = em.createNamedQuery("TrnDelivery.findAllRule2", TrnDelivery.class);
        query2.setParameter("deliveryNo", deliveryNos);
        List<TrnDelivery> deliveries = query2.getResultList();
        
        if (deliveries.isEmpty()) {
            return false;
        }
     
        final List<DeliveryStatusEnum> condition = Arrays.asList(DeliveryStatusEnum.WORKING, DeliveryStatusEnum.SUSPEND, DeliveryStatusEnum.PICKED, DeliveryStatusEnum.COMPLETED);
        
        for (TrnDeliveryItem deliveryItem : deliveryItems) {
            if (deliveries.stream()
                    .filter(o -> deliveryItem.getPK().getDeliveryNo().equals(o.getDeliveryNo())
                            && material.containsOrderNo(o.getModelName(), o.getOrderNo())
                            && condition.contains(o.getStatus()))
                    .findFirst()
                    .isPresent()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 在庫引当をおこなう。
     * 
     * @param deliveryNo 払出指示番号
     * @param itemNo 明細番号
     * @param material 資材情報
     * @return 
     */
    public Integer reserveInventory(String deliveryNo, Integer itemNo, TrnMaterial material) {
        try {
            Date now = new Date();

            TrnDelivery delivery = this.findDelivery(deliveryNo, false);
            if (Objects.isNull(delivery)) {
                return 0;
            }

            TrnDeliveryItem deliveryItem = this.findDeliveryItem(deliveryNo, itemNo, false);
            if (Objects.isNull(deliveryItem)) {
                return 0;
            }

            int reservedNum = deliveryItem.getReservedNum();
            int requiredNum = deliveryItem.getRequiredNum() - reservedNum;
            int availableNum = material.getAvailableNum();
            int reservationNum = availableNum > requiredNum ? requiredNum : availableNum;
            
            if (reservationNum <= 0) {
                return 0;
            }

            TrnReserveMaterial reserveMaterial = this.findReserveMaterial(deliveryNo, itemNo, material.getMaterialNo(), false);
            if (Objects.isNull(reserveMaterial)) {
                reserveMaterial = new TrnReserveMaterial(deliveryNo, itemNo, material.getMaterialNo(), reservationNum, new Date(), "system");
                em.persist(reserveMaterial);

                deliveryItem.getReserveMaterials().add(reserveMaterial);
                material.getReserveMaterials().add(reserveMaterial);
            } else {
                reserveMaterial.setReservedNum(reserveMaterial.getReservedNum() + reservationNum);
                reserveMaterial.setReservedAt(new Date());
                reserveMaterial.setPersonNo("system");
            }

            reserveMaterial.setNote("システムにより引当てられました");

            requiredNum -= reservationNum;

            if ((deliveryItem.getReserve() == 0 || deliveryItem.getReserve() == 1) 
                    && material.containsOrderNo(delivery.getModelName(), deliveryItem.getOrderNo())) {
                deliveryItem.setReserve(requiredNum == 0 ? 3 : 1);  // 3: 引当済[○]、1: 一部引当[△]
            } else {
                deliveryItem.setReserve(requiredNum == 0 ? 4 : 2);  // 4: 引当済(製番違い)[○]、2: 一部引当(製番違い)[△]
            }
           
            deliveryItem.setUpdateDate(now);
            
            delivery.setStockOutNum(delivery.getStockOutNum() - reservationNum);
            delivery.setUpdateDate(now);
            
            em.flush();
            em.clear();

            return material.getAvailableNum();
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0;
        }
    }
    
    /**
     * 該当する資材番号の在庫引当情報を取得する。
     * 
     * @param materialNo 資材番号
     * @return 在庫引当情報一覧
     */
    @Lock(LockType.READ)
    public List<TrnReserveMaterial> findReserveMaterials(String materialNo) {
        try {
            TypedQuery<TrnReserveMaterial> query = em.createNamedQuery("TrnReserveMaterial.findByMaterialNo", TrnReserveMaterial.class);
            query.setParameter("materialNo", materialNo);
            return query.getResultList();
        } catch (NoResultException ex) {
            return new ArrayList<>();
        }
    }
}
