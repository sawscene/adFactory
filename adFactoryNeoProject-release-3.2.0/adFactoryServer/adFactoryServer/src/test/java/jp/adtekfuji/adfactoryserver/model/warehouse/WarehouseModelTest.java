/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model.warehouse;

import java.io.FileInputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.DeliveryRule;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WarehouseEvent;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.warehouse.LogStock;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstBom;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstLocation;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstProduct;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstStock;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDelivery;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDeliveryItem;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnMaterial;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.service.ServiceTestData;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.hasSize;
import org.junit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.runners.MethodSorters;

/**
 * 倉庫案内モデルのユニットテスト
 * 
 * @author s-heya
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WarehouseModelTest {
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static EntityTransaction tx;
    private static WarehouseModel model;
    private static ServiceTestData serviceTestData;

    private static OrganizationEntity organization1;
    private static OrganizationEntity organization2;
    private static OrganizationEntity organization3;

    public WarehouseModelTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        serviceTestData = new ServiceTestData();
        ServiceTestData.setUpClass();
        
        em = ServiceTestData.getEntityManager();

        model = new WarehouseModel();
        model.setEntityManager(em);

        FileManager.getInstance().getSystemProperties().setProperty("storageDays", "0");
    }
    
    @AfterClass
    public static void tearDownClass() {
        if (Objects.nonNull(em)) {
            em.close();
        }

        if (Objects.nonNull(emf)) {
            emf.close();
        }
    }
    
    @Before
    public void setUp() {
        tx = em.getTransaction();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * オブジェクトを全件取得する。
     * 
     * @param <T> 型
     * @param clazz Classオブジェクト
     * @return オブジェクト一覧
     */
    public <T> List<T> findAll(Class clazz) {
        jakarta.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(clazz));
        return em.createQuery(cq).getResultList();
    }

    /**
     * 棚マスタのインポートをテストする。
     * 
     * @throws Exception 
     */
    @Test
    public void test01_importLocation() throws Exception {
        try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\add_location.json")) {
            tx.begin();
            model.importLocation(stream, null);
            tx.commit();
        }

        try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\edit_location.json")) {
            tx.begin();
            model.importLocation(stream, null);
            tx.commit();
        }
        
        // すべての棚情報を取得
        List<MstLocation> locationList = this.findAll(MstLocation.class);
        assertThat(locationList, is(hasSize(8)));
        
        // 区画名一覧を取得
        List<String> areaNameList = model.getAreaNames();
        assertThat(areaNameList, is(hasSize(3)));
    }
    
    /**
     * 部品マスタのインポートをテストする。
     * 
     * @throws Exception 
     */
    @Test
    public void test02_importParts() throws Exception {
        try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\add_parts.json")) {
            tx.begin();
            model.importParts(stream, null);
            tx.commit();
        }

        List<MstProduct> productList = this.findAll(MstProduct.class);
        assertThat(productList, is(hasSize(5)));

        try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\edit_parts_1.json")) {
            tx.begin();
            model.importParts(stream, null);
            tx.commit();
        }

        productList = this.findAll(MstProduct.class);
        assertThat(productList, is(hasSize(10)));
    }
    
    /**
     * 部品構成マスタのインポートをテストする。
     * 
     * @throws Exception 
     */
    @Test
    public void test03_importBOM() throws Exception {
        try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\add_bom.json")) {
            tx.begin();
            model.importBOM(stream, null);
            tx.commit();
        }

        try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\edit_bom.json")) {
            tx.begin();
            model.importBOM(stream, null);
            tx.commit();
        }

        List<MstBom> bomList = this.findAll(MstBom.class);
        assertThat(bomList, is(hasSize(5)));

        MstProduct product3 = model.findProduct("prodNo-3", null);
        assertThat(product3.getPartsList(), is(hasSize(3)));
        assertThat(product3.getPartsList().get(0).getChild(), is(notNullValue()));

        MstProduct product4 = model.findProduct("prodNo-4", null);
        assertThat(product4.getPartsList(), is(hasSize(2)));

        bomList = model.findBomByParentId(product4.getProductId());
        assertThat(bomList, is(hasSize(2)));

        try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\edit_parts_2.json")) {
            tx.begin();
            model.importParts(stream, null);
            tx.commit();
        }
        
        bomList = model.findBomByParentId(product4.getProductId());
        assertThat(bomList, is(hasSize(2)));      
    }

    /**
     * 保管方法マスタのインポートをテストする。
     * 
     * @throws Exception 
     */
    @Test
    public void test04_importStock() throws Exception {
        try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\add_stock.json")) {
            tx.begin();
            model.importStock(stream, null);
            tx.commit();
        }

        try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\edit_stock.json")) {
            tx.begin();
            model.importStock(stream, null);
            tx.commit();
        }

        List<MstProduct> productList = this.findAll(MstProduct.class);
        assertThat(productList, is(hasSize(10)));    
    }
    
    /**
     * 納入情報のインポートをテストする。
     * 
     * @throws Exception 
     */
    @Test
    public void test05_importSupply() throws Exception {
        try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\add_supply.json")) {
            tx.begin();
            model.importSupply(stream, null);
            tx.commit();
        }
        
        List<TrnMaterial> materialList = this.findAll(TrnMaterial.class);
        assertThat(materialList, is(hasSize(4)));
        
        MstProduct product3 = model.findProduct("prodNo-3", null);
        assertThat(product3.getMaterialList(), is(hasSize(2)));
        
        try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\edit_supply.json")) {
            tx.begin();
            model.importSupply(stream, null);
            tx.commit();
        }

        assertThat(product3.getMaterialList(), is(hasSize(2)));

        materialList = this.findAll(TrnMaterial.class);
        assertThat(materialList, is(hasSize(7)));

        product3 = model.findProduct("prodNo-3", null);
        assertThat(product3.getMaterialList(), is(hasSize(3)));
    }

    /**
     * 出庫指示情報のインポートをテストする。
     * 
     * @throws Exception 
     */
    @Test
    public void test06_importDelivery() throws Exception {
       try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\add_delivery.json")) {
            tx.begin();
            model.importDelivery(stream, null);
            tx.commit();
        }
        
        List<TrnDelivery> deliveryList = this.findAll(TrnDelivery.class);
        assertThat(deliveryList, is(hasSize(2)));

        List<TrnDeliveryItem> itemList = this.findAll(TrnDeliveryItem.class);
        assertThat(itemList, is(hasSize(6)));
       
        //try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\edit_delivery_1.json")) {
        //    tx.begin();
        //    model.importDelivery(stream, null);
        //    tx.commit();
        //}
        //
        //deliveryList = this.findAll(TrnDelivery.class);
        //assertThat(deliveryList, is(hasSize(3)));
        //
        //TrnDelivery delivery1 = model.findDelivery("delivery-1");
        //assertThat(delivery1.getDeliveryList(), is(hasSize(2)));
        //
        //TrnDelivery delivery2 = model.findDelivery("delivery-2");
        //assertThat(delivery2.getDeliveryList(), is(hasSize(5)));
        //
        //itemList = this.findAll(TrnDeliveryItem.class);
        //assertThat(itemList, is(hasSize(8)));
        //
        //try (FileInputStream stream= new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\warehouse\\edit_delivery_2.json")) {
        //    tx.begin();
        //    model.importDelivery(stream, null);
        //    tx.commit();
        //}
        //
        //deliveryList = this.findAll(TrnDelivery.class);
        //assertThat(deliveryList, is(hasSize(2)));
    }
    
    /**
     * 支給品(QRコード)の入庫処理をテストする。
     * オペレーションNo: E1
     * 
     * @throws Exception 
     */
    @Test
    public void test07_enterWarehouse1() throws Exception {
    }

    /**
     * 購入品の入庫処理をテストする。
     * オペレーションNo: E2
     * 
     * @throws Exception 
     */
    @Test
    public void test07_enterWarehouse2() throws Exception {
        Response res;
        
        List<OrganizationEntity> organizations = this.findAll(OrganizationEntity.class);
        if (organizations.isEmpty()) {
            organizations = serviceTestData.createTestOrganizations();
        }
        organization1 = organizations.get(0);
        organization2 = organizations.get(1);
        organization3 = organizations.get(2);
        
        TrnMaterial material2 = model.findMaterialBySupplyNo("supply-2");
        
        // 組織マスタが存在しない
        tx.begin();
        res = model.reciveWarehouse("1", material2.getMaterialNo(), 10, "第一倉庫", "AZ-1-1");
        tx.commit();
        assertThat(((ResponseEntity) res.getEntity()).getErrorType() , is(ServerErrorTypeEnum.NOTFOUND_ORGANIZATION));

        // 資材情報が存在しない
        tx.begin();
        res = model.reciveWarehouse(organization1.getOrganizationIdentify(), "material-1", 10, "第一倉庫", "AZ-1-1");
        tx.commit();
        assertThat(((ResponseEntity) res.getEntity()).getErrorType(), is(ServerErrorTypeEnum.NOTFOUND_MATERIAL));
 
        // 正常終了
        tx.begin();
        res = model.reciveWarehouse(organization1.getOrganizationIdentify(), material2.getMaterialNo(), 10, "第一倉庫", "ZZ-9-1");
        tx.commit();
        
        assertThat(((ResponseEntity) res.getEntity()).isSuccess(), is(true));
        assertThat(((ResponseEntity) res.getEntity()).getErrorType() , is(ServerErrorTypeEnum.SUCCESS));

        // 棚マスタが存在しない
        tx.begin();
        res = model.reciveWarehouse(organization1.getOrganizationIdentify(), material2.getMaterialNo(), 100, "第一倉庫", null);
        tx.commit();
        //assertThat(((ResponseEntity) res.getEntity()).getErrorType() , is(ServerErrorTypeEnum.NOTFOUND_LOCATION));
        assertThat(((ResponseEntity) res.getEntity()).isSuccess(), is(true));

        material2 = model.findMaterialBySupplyNo("supply-2");
        assertThat(material2.getLocation().getAreaName(), is("第一倉庫"));
        assertThat(material2.getLocation().getLocationNo(), is("ZZ-9-1"));
        assertThat(material2.getStockNum(), is(110));
        assertThat(material2.getDeliveryNum(), is(0));
        assertThat(material2.getInStockNum(), is(110));
        
        MstProduct product = model.findProduct(material2.getProduct().getProductNo());
        assertThat(product.getStockList(), is(hasSize(2)));
        assertThat(product.getStockList().get(0).getStockNum(), is(10));

        List<MstStock> stockList = this.findAll(MstStock.class);
        assertThat(stockList, is(hasSize(2)));
        
        List<LogStock> logList = this.findAll(LogStock.class);
        assertThat(logList, is(hasSize(2)));

        tx.begin();
        res = model.reciveWarehouse(organization1.getOrganizationIdentify(), material2.getMaterialNo(), 20, "第一倉庫", "ZZ-9-1");
        tx.commit();

        assertThat(((ResponseEntity) res.getEntity()).isSuccess(), is(true));

        material2 = model.findMaterialBySupplyNo("supply-2");
        assertThat(material2.getStockNum(), is(130));
        assertThat(material2.getDeliveryNum(), is(0));
        assertThat(material2.getInStockNum(), is(130));
        
        product = model.findProduct(material2.getProduct().getProductNo());
        assertThat(product.getStockList(), is(hasSize(2)));
        assertThat(product.getStockList().get(0).getStockNum(), is(30));

        stockList = this.findAll(MstStock.class);
        assertThat(stockList, is(hasSize(2)));
        
        logList = this.findAll(LogStock.class);
        assertThat(logList, is(hasSize(3)));
        
        // 在庫数を取得
        int inStockNum = model.getInStockNum("prodNo-11", "第一倉庫");
        assertThat(inStockNum, is(130));

        inStockNum = model.getInStockNum("prodNo-11", "第二倉庫");
        assertThat(inStockNum, is(0));

        tx.begin();
        res = model.reciveWarehouse(organization1.getOrganizationIdentify(), "supply-8", 10, "第一倉庫", "AZ-1-2");
        tx.commit();

        assertThat(((ResponseEntity) res.getEntity()).isSuccess(), is(true));

        tx.begin();
        res = model.reciveWarehouse(organization1.getOrganizationIdentify(), "supply-9", 10, "第一倉庫", "AZ-1-3");
        tx.commit();

        assertThat(((ResponseEntity) res.getEntity()).isSuccess(), is(true));

        tx.begin();
        res = model.reciveWarehouse(organization1.getOrganizationIdentify(), "supply-10", 10, "第一倉庫", "AZ-1-4");
        tx.commit();

        assertThat(((ResponseEntity) res.getEntity()).isSuccess(), is(true));
    }
    
    /**
     * 購入品(QRコード)の入庫処理をテストする。
     * オペレーションNo: E3
     * 
     * @throws Exception 
     */
    @Test
    public void test07_enterWarehouse3() throws Exception {
    }

    /**
     * 加工品(QRコード)の入庫処理をテストする。
     * オペレーションNo: E5
     * 
     * @throws Exception 
     */
    @Test
    public void test07_enterWarehouse5() throws Exception {
    }

    /**
     * 支給品(QRコード)の出庫処理をテストする。
     * オペレーションNo: L1
     * 
     * @throws Exception 
     */
    @Test
    public void test08_leaveWarehouse1() throws Exception {
        Response res;

        // 出庫指示アイテムを取得
        tx.begin();
        TrnDeliveryItem deliveryItem = model.findDeliveryByMaterialNo(TrnMaterial.SUPPLY_PREFIX + "supply-2");
        tx.commit();
 
        assertThat(deliveryItem, is(notNullValue()));
        //assertThat(deliveryItem.getDeliveryNum(), is(130));

        tx.begin();
        res = model.leaveWarehouse(organization1.getOrganizationIdentify(), deliveryItem.getMaterialNo(), 10, null);
        tx.commit();
    
        assertThat(((ResponseEntity) res.getEntity()).isSuccess(), is(true));
        
        TrnMaterial material = model.findMaterial(TrnMaterial.SUPPLY_PREFIX + "supply-2");
        assertThat(material.getStockNum(), is(130));
        assertThat(material.getDeliveryNum(), is(10));
        assertThat(material.getInStockNum(), is(120));
        
        // 在庫数を取得
        int inStockNum = model.getInStockNum("prodNo-11", "第一倉庫");
        assertThat(inStockNum, is(120));

        // prodNo-14を出庫
        tx.begin();
        res = model.leaveWarehouse(organization1.getOrganizationIdentify(), deliveryItem.getMaterialNo(), 10, null);
        tx.commit();

        assertThat(((ResponseEntity) res.getEntity()).isSuccess(), is(true));

        material = model.findMaterial(TrnMaterial.SUPPLY_PREFIX + "supply-2");
        assertThat(material.getStockNum(), is(130));
        assertThat(material.getDeliveryNum(), is(20));
        assertThat(material.getInStockNum(), is(110));

        // 在庫数を取得
        inStockNum = model.getInStockNum("prodNo-11", "第一倉庫");
        assertThat(inStockNum, is(110));
    }

    /**
     * 払出指示書による出庫処理をテストする。
     * オペレーションNo: L2
     * 
     * @throws Exception 
     */
    @Test
    public void test08_leaveWarehouse2() throws Exception {
        Response res1;
        Response res2;

        // 出庫指示アイテム一覧を取得
        List<TrnDeliveryItem> deliveryItemList  = model.findDeliveryItem("delivery-2", "第一倉庫", DeliveryRule.NORMAL);
        assertThat(deliveryItemList, is(hasSize(3)));
        
        // 案内順を検証
        String[] productNoList = deliveryItemList.stream().map(o -> o.getProduct().getProductNo()).toArray(String[]::new);
        assertThat(productNoList, arrayContaining("prodNo-12", "prodNo-14", "prodNo-11"));

        // 在庫が無い
        //assertThat(deliveryItemList.get(4).getMaterialList(), is(hasSize(0)));

        TrnDeliveryItem deliveryItem2 = deliveryItemList.get(0);
        TrnMaterial material2 = deliveryItem2.getMaterialList().get(0);
        
        // prodNo-12の50個のうち10個を出庫 
        tx.begin();
        res1 = model.leaveWarehouse(WarehouseEvent.LEAVE, organization1.getOrganizationIdentify(), deliveryItem2.getPK().getDeliveryNo(), deliveryItem2.getPK().getItemNo(), material2.getMaterialNo(), 10, null, new Date());
        tx.commit();
        
        assertThat(((ResponseEntity) res1.getEntity()).isSuccess(), is(true));

        // 出庫指示アイテム一覧を取得
        deliveryItemList  = model.findDeliveryItem("delivery-2", "第一倉庫", DeliveryRule.NORMAL);
        assertThat(deliveryItemList, is(hasSize(2)));

        // prodNo-12の40個を出庫 
        tx.begin();
        res1 = model.leaveWarehouse(WarehouseEvent.LEAVE, organization1.getOrganizationIdentify(), deliveryItem2.getPK().getDeliveryNo(), deliveryItem2.getPK().getItemNo(), material2.getMaterialNo(), 40, null, new Date());
        tx.commit();

        assertThat(((ResponseEntity) res1.getEntity()).isSuccess(), is(true));

        // 出庫指示アイテム一覧を取得
        deliveryItemList  = model.findDeliveryItem("delivery-2", "第一倉庫", DeliveryRule.NORMAL);
        assertThat(deliveryItemList, is(hasSize(2)));

        productNoList = deliveryItemList.stream().map(o -> o.getProduct().getProductNo()).toArray(String[]::new);
        assertThat(productNoList, arrayContaining("prodNo-14", "prodNo-11"));

        TrnDeliveryItem deliveryItem1 = deliveryItemList.get(0);
        TrnMaterial material1 = deliveryItem2.getMaterialList().get(0);

        TrnDeliveryItem deliveryItem3 = deliveryItemList.get(1);
        TrnMaterial material3 = deliveryItem3.getMaterialList().get(0);

        // prodNo-11、prodNo-14 を出庫
        tx.begin();
        res1 = model.leaveWarehouse(WarehouseEvent.LEAVE, organization1.getOrganizationIdentify(), deliveryItem1.getPK().getDeliveryNo(), deliveryItem1.getPK().getItemNo(), material1.getMaterialNo(), 10, null, new Date());
        res2 = model.leaveWarehouse(WarehouseEvent.LEAVE, organization1.getOrganizationIdentify(), deliveryItem3.getPK().getDeliveryNo(), deliveryItem3.getPK().getItemNo(), material3.getMaterialNo(), 60, null, new Date());
        tx.commit();

        assertThat(((ResponseEntity) res1.getEntity()).isSuccess(), is(true));
        assertThat(((ResponseEntity) res2.getEntity()).isSuccess(), is(true));

        // 出庫指示アイテム一覧を取得
        deliveryItemList  = model.findDeliveryItem("delivery-2", "第一倉庫", DeliveryRule.NORMAL);
        assertThat(deliveryItemList, is(hasSize(0)));
    }

    /**
     * 払出指示書(QRコード)による出庫処理をテストする。
     * オペレーションNo: L3
     * 
     * @throws Exception 
     */
    @Test
    public void test08_leaveWarehouse3() throws Exception {
    }

    /**
     * 資材情報の検索をテストする。
     * 
     * @throws Exception 
     */
    @Test
    public void test09_searchMaterials() throws Exception {
    }

    /**
     * 入出庫実績情報の同期フラグの更新をテストする。
     * 
     * @throws Exception 
     */
    @Test
    public void test10_updateSynced() throws Exception {
        List<LogStock> logStockList = model.findSyncedLogStock(false, 100);
        List<Long> eventIds = logStockList.stream().map(o -> o.getEventId()).collect(Collectors.toList());
        
        tx.begin();
        int count = model.updateSyncedLogStock(eventIds, Boolean.TRUE);
        tx.commit();
        
        assertThat(count, is(eventIds.size()));
    }

    //@Test
    //public void test11_nextPartsNo() throws Exception {
    //    Response res;
    //
    //    MstProduct product = model.findProductByFigureNo("fig-3", true);
    //
    //    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    //    String date = LocalDateTime.now().format(formatter);
    //    String partsNo = String.format("%s%04d", date, 11);
    //
    //    // 正常終了
    //    tx.begin();
    //    res = model.reciveWarehouseForPartsNo(organization1.getOrganizationIdentify(), product.getFigureNo(), product.getProductNo(), partsNo, null, 10, "第一倉庫", null, null);
    //    tx.commit();
    //
    //    assertThat(((ResponseEntity) res.getEntity()).isSuccess(), is(true));
    //    assertThat(((ResponseEntity) res.getEntity()).getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
    //
    //    partsNo = String.format("%s%04d", date, 12);
    //
    //    String nextPartsNo = model.nextPartsNo();
    //    assertThat(nextPartsNo, is(partsNo));       
    //}
}
