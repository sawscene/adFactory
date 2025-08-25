package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualAdProduct;

import adtekfuji.property.AdProperty;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginResult;
import jp.adtekfuji.adFactory.entity.search.AddInfoSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.search.ProducibleWorkKanbanCondition;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class VirtualAdProductTest {

    VirtualAdProduct virtualAdProduct = null;

    @Before
    public void setUp() throws Exception {
//        AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
//        AdProperty.load("adInterface.properties");
//
//        virtualAdProduct = VirtualAdProduct.createInstance().get();
    }

    @Test
    public void loginEquipment() {

//        EquipmentLoginResult success = virtualAdProduct.loginEquipment("AJE");
//        Assert.assertTrue(success.getIsSuccess());
//
//        EquipmentLoginResult success2 = virtualAdProduct.loginEquipment("AJE");
//        Assert.assertTrue(success2.getIsSuccess());
//
//        EquipmentLoginResult fail = virtualAdProduct.loginEquipment("AAA");
//        Assert.assertFalse(fail.getIsSuccess());
    }

    @Test
    public void loginOrganization() {
//        OrganizationLoginResult success = virtualAdProduct.loginOrganization("admin", "admin");
//        Assert.assertTrue(success.getIsSuccess());
//
//        OrganizationLoginResult success2 = virtualAdProduct.loginOrganization("admin", "admin");
//        Assert.assertTrue(success2.getIsSuccess());
//
//        OrganizationLoginResult fail1 = virtualAdProduct.loginOrganization("admin", "admin1");
//        Assert.assertFalse(fail1.getIsSuccess());
//
//        OrganizationLoginResult fail2 = virtualAdProduct.loginOrganization("admin1", "admin");
//        Assert.assertFalse(fail2.getIsSuccess());

    }

    @Test
    public void searchProductWorkKanban() {

//        EquipmentLoginResult equipment = virtualAdProduct.loginEquipment("NXAD");
//        OrganizationLoginResult organization = virtualAdProduct.loginOrganization("4022", "");
//
//        ProducibleWorkKanbanCondition condition = new ProducibleWorkKanbanCondition();
//        condition.setEquipmentCollection(Collections.singletonList(equipment.getEquipmentId()));
//        condition.setOrganizationCollection(Collections.singletonList(organization.getOrganizationId()));
//        condition.setAddInfoSearchConditions(Arrays.asList(
//                new AddInfoSearchCondition("工程コード", "MCY0"),
//                new AddInfoSearchCondition("数量", "1")
//        ));
//
//        List<WorkKanbanInfoEntity> workKanbanInfoEntities = virtualAdProduct.searchProductWorkKanban(condition, 0, 0);
//        int a=0;
//        ++a;
    }

    @Test
    public void working() {
//        // 設備ログイン
//        EquipmentLoginResult equipment = virtualAdProduct.loginEquipment("NXAD");
//
//        // 組織ログイン
//        OrganizationLoginResult organization = virtualAdProduct.loginOrganization("4022", "");
//
//        // 工程カンバン検索条件
//        ProducibleWorkKanbanCondition condition = new ProducibleWorkKanbanCondition();
//        condition.setEquipmentCollection(Collections.singletonList(equipment.getEquipmentId()));
//        condition.setOrganizationCollection(Collections.singletonList(organization.getOrganizationId()));
//        condition.setAddInfoSearchConditions(Arrays.asList(
//                new AddInfoSearchCondition("工程コード", "MCY0"),
//                new AddInfoSearchCondition("数量", "1")
//        ));
//
//        // 検索結果
//        List<WorkKanbanInfoEntity> workKanbanInfoEntities = virtualAdProduct.searchProductWorkKanban(condition, 0, 0);
//
//        // 初期レポート
//        List<Long> workKanbanList = Collections.singletonList(workKanbanInfoEntities.get(0).getWorkKanbanId());
//        ActualProductReportResult a = virtualAdProduct.startWork(workKanbanList);
//        ActualProductReportResult b = virtualAdProduct.suspendWork(workKanbanList);
//        ActualProductReportResult c = virtualAdProduct.startWork(workKanbanList);
//        ActualProductReportResult d = virtualAdProduct.compWork(workKanbanList);

    }

}
