/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.DateUtils;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.utility.AggregateMonitorInfoFacade;
import jp.adtekfuji.andon.entity.MonitorEquipmentPlanNumInfoEntity;
import jp.adtekfuji.andon.entity.MonitorPlanNumInfoEntity;
import jp.adtekfuji.andon.entity.MonitorWorkPlanNumInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import jp.adtekfuji.andon.property.WorkSetting;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nar-nakamura
 */
public class AggregateMonitorInfoFacadeTest {

    private static ServiceTestData serviceTestData = null;

    private static AggregateMonitorInfoFacade facade = null;

    public AggregateMonitorInfoFacadeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        serviceTestData = new ServiceTestData();
        ServiceTestData.setUpClass();

        facade = new AggregateMonitorInfoFacade();
//        facade.setKanbanRest(ServiceTestData.getKanbanRest());
        facade.setWorkKanbanRest(ServiceTestData.getWorkKanbanRest());
        facade.setActualResultRest(ServiceTestData.getActualResultRest());
        facade.setEquipmentRest(ServiceTestData.getEquipmentRest());

        LicenseManager.setupTest();
    }

    @AfterClass
    public static void tearDownClass() {
        ServiceTestData.tearDownClass();
    }

    @Before
    public void setUp() {
        if (Objects.nonNull(serviceTestData)) {
            serviceTestData.setUp();
        }
    }

    @After
    public void tearDown() {
        if (Objects.nonNull(serviceTestData)) {
            serviceTestData.tearDown();
        }
    }

    /**
     * 指定ラインの日単位の生産数情報を取得するテスト
     *
     * @throws Exception 
     */
    @Ignore//TODO: LineProductEntity が View のため、getMonthlyPlanInfo()が実行できないため無効化。
    @Test
    public void testGetDailyPlanInfo() throws Exception {
        System.out.println("testGetDailyPlanInfo start.");

        serviceTestData.createTestData();
        serviceTestData.createTestKanbans(false);

        AndonMonitorLineProductSetting setting1 = AndonMonitorLineProductSetting.create();
        setting1.setLineId(serviceTestData.getLineEquipmentId(0));

        AndonMonitorLineProductSetting setting2 = AndonMonitorLineProductSetting.create();
        setting2.setLineId(serviceTestData.getLineEquipmentId(1));

        AndonMonitorLineProductSetting setting3 = AndonMonitorLineProductSetting.create();
        setting3.setLineId(serviceTestData.getLineEquipmentId(2));

        // 正しい実績数
        List<Integer> actualNums = new LinkedList();
        actualNums.add(0);
        actualNums.add(0);
        actualNums.add(0);

        // 当日の実績数をチェックする。
        this.checkDailyPlanInfo(setting1, actualNums.get(0));
        this.checkDailyPlanInfo(setting2, actualNums.get(1));
        this.checkDailyPlanInfo(setting3, actualNums.get(2));

        // 前日・当日と実績を作成しながら「指定ラインの日単位の生産数情報を取得」のチェックを行なう。
        Long transactionId = 1L;

        Date today = DateUtils.getBeginningOfDate(new Date());// 当日 0時

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        Date workingDate = cal.getTime();// 前日 0時

        List<String> workflowNames = Arrays.asList(ServiceTestData.WORKFLOW_NAME_1, ServiceTestData.WORKFLOW_NAME_2, ServiceTestData.WORKFLOW_NAME_3);

        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int listId = 0; listId < 3; listId++) {
                    KanbanEntity kanban = serviceTestData.getKanbanList(workflowNames.get(listId), 1).get(index);
                    transactionId = serviceTestData.compKanban(kanban, workingDate, transactionId);

                    kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);

                    // 当日分のみ正しい実績数にカウント
                    if (today.equals(DateUtils.getBeginningOfDate(kanban.getActualCompTime()))) {
                        actualNums.set(listId, actualNums.get(listId) + 1);
                    }

                    // 当日の実績数をチェックする。
                    this.checkDailyPlanInfo(setting1, actualNums.get(0));
                    this.checkDailyPlanInfo(setting2, actualNums.get(1));
                    this.checkDailyPlanInfo(setting3, actualNums.get(2));
                }

                index++;
            }

            // 1日進める
            cal.add(Calendar.DAY_OF_MONTH, 1);
            workingDate = cal.getTime();
        }

        System.out.println("testGetDailyPlanInfo end.");
    }

    /**
     * 指定した期間の生産数情報を取得するテスト (対象設備を巡回した数をカウント)
     *
     * @throws Exception 
     */
    @Test
    public void testGetPlanNumInfoEquip() throws Exception {
        System.out.println("testGetPlanNumInfoEquip start.");

        Date now = new Date();
        Date fromDate = DateUtils.getBeginningOfDate(now);
        Date toDate = DateUtils.getEndOfDate(now);

        serviceTestData.createTestData();
        serviceTestData.createTestKanbans(false);

        EquipmentEntity adPro1 = serviceTestData.getEquipments().stream().filter(p -> ServiceTestData.EQUIPMENT_IDENT_1_1.equals(p.getEquipmentIdentify())).findFirst().get();
        EquipmentEntity adPro2 = serviceTestData.getEquipments().stream().filter(p -> ServiceTestData.EQUIPMENT_IDENT_1_2.equals(p.getEquipmentIdentify())).findFirst().get();
        EquipmentEntity adPro3 = serviceTestData.getEquipments().stream().filter(p -> ServiceTestData.EQUIPMENT_IDENT_1_3.equals(p.getEquipmentIdentify())).findFirst().get();
        List<EquipmentEntity> targetEquips = Arrays.asList(adPro1, adPro2, adPro3);

        AndonMonitorLineProductSetting setting = AndonMonitorLineProductSetting.create();

        WorkEquipmentSetting workEquipment1 = new WorkEquipmentSetting();
        workEquipment1.setEquipmentIds(Arrays.asList(adPro1.getEquipmentId()));

        WorkEquipmentSetting workEquipment2 = new WorkEquipmentSetting();
        workEquipment2.setEquipmentIds(Arrays.asList(adPro2.getEquipmentId()));

        WorkEquipmentSetting workEquipment3 = new WorkEquipmentSetting();
        workEquipment3.setEquipmentIds(Arrays.asList(adPro3.getEquipmentId()));

        setting.setWorkEquipmentCollection(Arrays.asList(workEquipment1, workEquipment2, workEquipment3));

        // 正しい実績数
        List<Integer> actualNums = new LinkedList();
        actualNums.add(0);
        actualNums.add(0);
        actualNums.add(0);

        // 実績数をチェックする。
        this.checkPlanNumInfoEquip(setting, 0, fromDate, toDate);

        // 前日・当日と実績を作成しながら「指定ラインの日単位の生産数情報を取得」のチェックを行なう。
        Long transactionId = 1L;

        Date today = DateUtils.getBeginningOfDate(new Date());// 当日 0時

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        Date workingDate = cal.getTime();// 前日 0時

        List<String> workflowNames = Arrays.asList(ServiceTestData.WORKFLOW_NAME_1, ServiceTestData.WORKFLOW_NAME_2, ServiceTestData.WORKFLOW_NAME_3);

        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int listId = 0; listId < 3; listId++) {
                    KanbanEntity kanban = serviceTestData.getKanbanList(workflowNames.get(listId), 1).get(index);

                    List<WorkKanbanEntity> workKanbans = kanban.getWorkKanbanCollection();
                    Collections.sort(workKanbans, ((a, b) -> a.getWorkKanbanOrder().compareTo(b.getWorkKanbanOrder())));

                    Calendar workCal = Calendar.getInstance();
                    workCal.setTime(workingDate);

                    Date startDate = workCal.getTime();
                    int targetEquipIndex = 0;
                    for (WorkKanbanEntity workKanban : workKanbans) {
                        transactionId = serviceTestData.compWorkKanban(kanban, workKanban, startDate, transactionId, targetEquips.get(targetEquipIndex));

                        workCal.add(Calendar.MINUTE, 1);
                        workCal.add(Calendar.MINUTE, 1);
                        startDate = workCal.getTime();

                        workKanban = ServiceTestData.getWorkKanbanRest().find(workKanban.getWorkKanbanId(), null);

                        // 当日分のみ正しい実績数にカウント
                        if (today.equals(DateUtils.getBeginningOfDate(workKanban.getActualCompTime()))) {
                            actualNums.set(targetEquipIndex, actualNums.get(targetEquipIndex) + 1);
                        }

                        this.checkPlanNumInfoEquip(setting, actualNums.stream().min((a, b) -> a.compareTo(b)).get(), fromDate, toDate);

                        targetEquipIndex++;
                        if (targetEquipIndex >= targetEquips.size()) {
                            targetEquipIndex = 0;
                        }
                    }
                }

                index++;
            }

            // 1日進める
            cal.add(Calendar.DAY_OF_MONTH, 1);
            workingDate = cal.getTime();
        }

        System.out.println("testGetPlanNumInfoEquip end.");
    }

    /**
     * 指定した期間の生産数情報を取得するテスト (対象工程を巡回した数をカウント)
     *
     * @throws Exception 
     */
    @Test
    public void testGetPlanNumInfoWork() throws Exception {
        System.out.println("testGetPlanNumInfoWork start.");

        Date now = new Date();
        Date fromDate = DateUtils.getBeginningOfDate(now);
        Date toDate = DateUtils.getEndOfDate(now);

        serviceTestData.createTestData();
        serviceTestData.createTestKanbans(false);

        EquipmentEntity adPro1 = serviceTestData.getEquipments().stream().filter(p -> ServiceTestData.EQUIPMENT_IDENT_1_1.equals(p.getEquipmentIdentify())).findFirst().get();

        WorkEntity work1 = serviceTestData.getWorks().get(0);
        WorkEntity work2 = serviceTestData.getWorks().get(1);
        WorkEntity work3 = serviceTestData.getWorks().get(2);
        List<Long> targetWorkIds = Arrays.asList(work1.getWorkId(), work2.getWorkId(), work3.getWorkId());

        AndonMonitorLineProductSetting setting = AndonMonitorLineProductSetting.create();

        WorkSetting workSetting1 = new WorkSetting();
        workSetting1.setWorkIds(Arrays.asList(work1.getWorkId()));

        WorkSetting workSetting2 = new WorkSetting();
        workSetting2.setWorkIds(Arrays.asList(work2.getWorkId()));

        WorkSetting workSetting3 = new WorkSetting();
        workSetting3.setWorkIds(Arrays.asList(work3.getWorkId()));

        setting.setWorkCollection(Arrays.asList(workSetting1, workSetting2, workSetting3));

        // 正しい実績数
        List<Integer> actualNums = new LinkedList();
        actualNums.add(0);
        actualNums.add(0);
        actualNums.add(0);

        // 実績数をチェックする。
        this.checkPlanNumInfoWork(setting, 0, fromDate, toDate);

        // 前日・当日と実績を作成しながら「指定ラインの日単位の生産数情報を取得」のチェックを行なう。
        Long transactionId = 1L;

        Date today = DateUtils.getBeginningOfDate(new Date());// 当日 0時

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        Date workingDate = cal.getTime();// 前日 0時

        List<String> workflowNames = Arrays.asList(ServiceTestData.WORKFLOW_NAME_1, ServiceTestData.WORKFLOW_NAME_2, ServiceTestData.WORKFLOW_NAME_3);

        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int listId = 0; listId < 3; listId++) {
                    KanbanEntity kanban = serviceTestData.getKanbanList(workflowNames.get(listId), 1).get(index);

                    List<WorkKanbanEntity> workKanbans = kanban.getWorkKanbanCollection();
                    Collections.sort(workKanbans, ((a, b) -> a.getWorkKanbanOrder().compareTo(b.getWorkKanbanOrder())));

                    Calendar workCal = Calendar.getInstance();
                    workCal.setTime(workingDate);

                    Date startDate = workCal.getTime();
                    for (WorkKanbanEntity workKanban : workKanbans) {
                        transactionId = serviceTestData.compWorkKanban(kanban, workKanban, startDate, transactionId, adPro1);

                        workCal.add(Calendar.MINUTE, 1);
                        workCal.add(Calendar.MINUTE, 1);
                        startDate = workCal.getTime();

                        workKanban = ServiceTestData.getWorkKanbanRest().find(workKanban.getWorkKanbanId(), null);

                        // 当日分のみ正しい実績数にカウント
                        if (today.equals(DateUtils.getBeginningOfDate(workKanban.getActualCompTime()))) {
                            int targetWorkIndex = targetWorkIds.indexOf(workKanban.getWorkId());
                            if (targetWorkIndex >= 0) {
                                actualNums.set(targetWorkIndex, actualNums.get(targetWorkIndex) + 1);
                            }
                        }

                        this.checkPlanNumInfoWork(setting, actualNums.stream().min((a, b) -> a.compareTo(b)).get(), fromDate, toDate);
                    }
                }

                index++;
            }

            // 1日進める
            cal.add(Calendar.DAY_OF_MONTH, 1);
            workingDate = cal.getTime();
        }

        System.out.println("testGetPlanNumInfoWork end.");
    }

    /**
     * 指定ラインの日単位の生産数情報を取得するテスト (製品情報によるロット生産)
     *
     * @throws Exception 
     */
    @Ignore//TODO: LineProductEntity が View のため、getMonthlyPlanInfo()が実行できないため無効化。
    @Test
    public void testGetDailyPlanInfo_Products() throws Exception {
        System.out.println("testGetDailyPlanInfo_Products start.");

        serviceTestData.createTestData();
        serviceTestData.createTestKanbans(true);// 製品情報あり

        AndonMonitorLineProductSetting setting1 = AndonMonitorLineProductSetting.create();
        setting1.setLineId(serviceTestData.getLineEquipmentId(0));

        AndonMonitorLineProductSetting setting2 = AndonMonitorLineProductSetting.create();
        setting2.setLineId(serviceTestData.getLineEquipmentId(1));

        AndonMonitorLineProductSetting setting3 = AndonMonitorLineProductSetting.create();
        setting3.setLineId(serviceTestData.getLineEquipmentId(2));

        // 正しい実績数
        List<Integer> actualNums = new LinkedList();
        actualNums.add(0);
        actualNums.add(0);
        actualNums.add(0);

        // 当日の実績数をチェックする。
        this.checkDailyPlanInfo(setting1, actualNums.get(0));
        this.checkDailyPlanInfo(setting2, actualNums.get(1));
        this.checkDailyPlanInfo(setting3, actualNums.get(2));

        // 前日・当日と実績を作成しながら「指定ラインの日単位の生産数情報を取得」のチェックを行なう。
        Long transactionId = 1L;

        Date today = DateUtils.getBeginningOfDate(new Date());// 当日 0時

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        Date workingDate = cal.getTime();// 前日 0時

        List<String> workflowNames = Arrays.asList(ServiceTestData.WORKFLOW_NAME_1, ServiceTestData.WORKFLOW_NAME_2, ServiceTestData.WORKFLOW_NAME_3);

        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int listId = 0; listId < 3; listId++) {
                    KanbanEntity kanban = serviceTestData.getKanbanList(workflowNames.get(listId), 1).get(index);

                    int productNum = 1;
                    if (Objects.nonNull(kanban.getProducts()) && kanban.getProducts().size() > 0) {
                        productNum = kanban.getProducts().size();
                    }

                    transactionId = serviceTestData.compKanban(kanban, workingDate, transactionId);

                    kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);

                    // 当日分のみ正しい実績数にカウント
                    if (today.equals(DateUtils.getBeginningOfDate(kanban.getActualCompTime()))) {
                        actualNums.set(listId, actualNums.get(listId) + productNum);
                    }

                    // 当日の実績数をチェックする。
                    this.checkDailyPlanInfo(setting1, actualNums.get(0));
                    this.checkDailyPlanInfo(setting2, actualNums.get(1));
                    this.checkDailyPlanInfo(setting3, actualNums.get(2));
                }

                index++;
            }

            // 1日進める
            cal.add(Calendar.DAY_OF_MONTH, 1);
            workingDate = cal.getTime();
        }

        System.out.println("testGetDailyPlanInfo_Products end.");
    }

    /**
     * 指定ラインの日単位の実績数をチェックする。
     *
     * @param setting 進捗モニタ設定
     * @param actualNum 正しい実績数
     * @throws Exception 実績数が異なる場合、エラーになる。
     */
    private void checkDailyPlanInfo(AndonMonitorLineProductSetting setting, Integer actualNum) throws Exception {
        MonitorPlanNumInfoEntity info = facade.getDailyPlanInfo(setting);
        assertEquals(info.getActualNum(), actualNum);
    }

    /**
     * 実績数をチェックする。(対象設備を巡回した数をカウント)
     *
     * @param setting 進捗モニタ設定
     * @param actualNum 正しい実績数
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @throws Exception 実績数が異なる場合、エラーになる。
     */
    private void checkPlanNumInfoEquip(AndonMonitorLineProductSetting setting, Integer actualNum, Date fromDate, Date toDate) throws Exception {
        MonitorPlanNumInfoEntity info = facade.getPlanNumInfoEquip(setting, fromDate, toDate);
        assertEquals(info.getActualNum(), actualNum);
    }

    /**
     * 実績数をチェックする。(対象工程を巡回した数をカウント)
     *
     * @param setting 進捗モニタ設定
     * @param actualNum 正しい実績数
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @throws Exception 実績数が異なる場合、エラーになる。
     */
    private void checkPlanNumInfoWork(AndonMonitorLineProductSetting setting, Integer actualNum, Date fromDate, Date toDate) throws Exception {
        MonitorPlanNumInfoEntity info = facade.getPlanNumInfoWork(setting, fromDate, toDate);
        assertEquals(info.getActualNum(), actualNum);
    }

    /**
     * 指定ラインの月単位の生産数情報を取得するテスト
     *
     * @throws Exception 
     */
    @Ignore//TODO: LineProductEntity が View のため、getMonthlyPlanInfo()が実行できないため無効化。
    @Test
    public void testGetMonthlyPlanInfo() throws Exception {
        System.out.println("testGetMonthlyPlanInfo start.");

        serviceTestData.createTestData();
        serviceTestData.createTestKanbans(false);

        AndonMonitorLineProductSetting setting1 = AndonMonitorLineProductSetting.create();
        setting1.setLineId(serviceTestData.getLineEquipmentId(0));

        AndonMonitorLineProductSetting setting2 = AndonMonitorLineProductSetting.create();
        setting2.setLineId(serviceTestData.getLineEquipmentId(1));

        AndonMonitorLineProductSetting setting3 = AndonMonitorLineProductSetting.create();
        setting3.setLineId(serviceTestData.getLineEquipmentId(2));

        // 正しい実績数
        List<Integer> actualNums = new LinkedList();
        actualNums.add(0);
        actualNums.add(0);
        actualNums.add(0);

        // 当月の実績数をチェックする。
        this.checkMonthlyPlanInfo(setting1, actualNums.get(0));
        this.checkMonthlyPlanInfo(setting2, actualNums.get(1));
        this.checkMonthlyPlanInfo(setting3, actualNums.get(2));

        // 前月・当月と実績を作成しながら「指定ラインの月単位の生産数情報を取得」のチェックを行なう。
        Long transactionId = 1L;

        Date today = DateUtils.getBeginningOfDate(new Date());// 当日 0時
        Date firstDay = DateUtils.getBeginningOfMonth(today);// 当月の1日 0時

        Calendar cal = Calendar.getInstance();
        cal.setTime(firstDay);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        Date workingDate = DateUtils.getEndOfMonth(cal.getTime());// 前月の最終日 0時

        List<String> workflowNames = Arrays.asList(ServiceTestData.WORKFLOW_NAME_1, ServiceTestData.WORKFLOW_NAME_2, ServiceTestData.WORKFLOW_NAME_3);

        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int listId = 0; listId < 3; listId++) {
                    KanbanEntity kanban = serviceTestData.getKanbanList(workflowNames.get(listId), 1).get(index);
                    transactionId = serviceTestData.compKanban(kanban, workingDate, transactionId);

                    kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);

                    // 当月分のみ正しい実績数にカウント
                    Date actualDate = DateUtils.getBeginningOfDate(kanban.getActualCompTime());
                    if (firstDay.equals(DateUtils.getBeginningOfMonth(actualDate))) {
                        actualNums.set(listId, actualNums.get(listId) + 1);
                    }

                    // 当月の実績数をチェックする。
                    this.checkMonthlyPlanInfo(setting1, actualNums.get(0));
                    this.checkMonthlyPlanInfo(setting2, actualNums.get(1));
                    this.checkMonthlyPlanInfo(setting3, actualNums.get(2));
                }

                index++;
            }

            // 1日進める
            cal.add(Calendar.DAY_OF_MONTH, 1);
            workingDate = cal.getTime();
        }

        System.out.println("testGetMonthlyPlanInfo end.");
    }

    /**
     * 月単位の生産数情報を取得するテスト (対象設備を巡回した数をカウント)
     *
     * @throws Exception 
     */
    @Test
    public void testGetMonthlyPlanInfoEquip() throws Exception {
        System.out.println("testGetMonthlyPlanInfoEquip start.");

        Date now = new Date();
        Date fromDate = DateUtils.getBeginningOfMonth(now);
        Date toDate = DateUtils.getEndOfMonth(now);

        serviceTestData.createTestData();
        serviceTestData.createTestKanbans(false);

        EquipmentEntity adPro1 = serviceTestData.getEquipments().stream().filter(p -> ServiceTestData.EQUIPMENT_IDENT_1_1.equals(p.getEquipmentIdentify())).findFirst().get();
        EquipmentEntity adPro2 = serviceTestData.getEquipments().stream().filter(p -> ServiceTestData.EQUIPMENT_IDENT_1_2.equals(p.getEquipmentIdentify())).findFirst().get();
        EquipmentEntity adPro3 = serviceTestData.getEquipments().stream().filter(p -> ServiceTestData.EQUIPMENT_IDENT_1_3.equals(p.getEquipmentIdentify())).findFirst().get();
        List<EquipmentEntity> targetEquips = Arrays.asList(adPro1, adPro2, adPro3);

        AndonMonitorLineProductSetting setting = AndonMonitorLineProductSetting.create();

        WorkEquipmentSetting workEquipment1 = new WorkEquipmentSetting();
        workEquipment1.setEquipmentIds(Arrays.asList(adPro1.getEquipmentId()));

        WorkEquipmentSetting workEquipment2 = new WorkEquipmentSetting();
        workEquipment2.setEquipmentIds(Arrays.asList(adPro2.getEquipmentId()));

        WorkEquipmentSetting workEquipment3 = new WorkEquipmentSetting();
        workEquipment3.setEquipmentIds(Arrays.asList(adPro3.getEquipmentId()));

        setting.setWorkEquipmentCollection(Arrays.asList(workEquipment1, workEquipment2, workEquipment3));

        // 正しい実績数
        List<Integer> actualNums = new LinkedList();
        actualNums.add(0);
        actualNums.add(0);
        actualNums.add(0);

        // 当月の実績数をチェックする。
        this.checkPlanNumInfoEquip(setting, 0, fromDate, toDate);

        // 前月・当月と実績を作成しながら生産数情報のチェックを行なう。
        Long transactionId = 1L;

        Date today = DateUtils.getBeginningOfDate(new Date());// 当日 0時
        Date firstDay = DateUtils.getBeginningOfMonth(today);// 当月の1日 0時

        Calendar cal = Calendar.getInstance();
        cal.setTime(firstDay);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        Date workingDate = DateUtils.getEndOfMonth(cal.getTime());// 前月の最終日 23:59:59.999

        List<String> workflowNames = Arrays.asList(ServiceTestData.WORKFLOW_NAME_1, ServiceTestData.WORKFLOW_NAME_2, ServiceTestData.WORKFLOW_NAME_3);

        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int listId = 0; listId < 3; listId++) {
                    KanbanEntity kanban = serviceTestData.getKanbanList(workflowNames.get(listId), 1).get(index);

                    List<WorkKanbanEntity> workKanbans = kanban.getWorkKanbanCollection();
                    Collections.sort(workKanbans, ((a, b) -> a.getWorkKanbanOrder().compareTo(b.getWorkKanbanOrder())));

                    Calendar workCal = Calendar.getInstance();
                    workCal.setTime(workingDate);

                    Date startDate = workCal.getTime();
                    int targetEquipIndex = 0;
                    for (WorkKanbanEntity workKanban : workKanbans) {
                        transactionId = serviceTestData.compWorkKanban(kanban, workKanban, startDate, transactionId, targetEquips.get(targetEquipIndex));

                        workCal.add(Calendar.MINUTE, 1);
                        workCal.add(Calendar.MINUTE, 1);
                        startDate = workCal.getTime();

                        workKanban = ServiceTestData.getWorkKanbanRest().find(workKanban.getWorkKanbanId(), null);

                        // 当月分のみ正しい実績数にカウント
                        Date actualDate = DateUtils.getBeginningOfDate(workKanban.getActualCompTime());
                        if (firstDay.equals(DateUtils.getBeginningOfMonth(actualDate))) {
                            actualNums.set(targetEquipIndex, actualNums.get(targetEquipIndex) + 1);
                        }

                        this.checkPlanNumInfoEquip(setting, actualNums.stream().min((a, b) -> a.compareTo(b)).get(), fromDate, toDate);

                        targetEquipIndex++;
                        if (targetEquipIndex >= targetEquips.size()) {
                            targetEquipIndex = 0;
                        }
                    }
                }

                index++;
            }

            // 1日進める
            cal.add(Calendar.DAY_OF_MONTH, 1);
            workingDate = cal.getTime();
        }

        System.out.println("testGetMonthlyPlanInfoEquip end.");
    }

    /**
     * 月単位の生産数情報を取得するテスト (対象工程を巡回した数をカウント)
     *
     * @throws Exception 
     */
    @Test
    public void testGetMonthlyPlanInfoWork() throws Exception {
        System.out.println("testGetMonthlyPlanInfoWork start.");

        Date now = new Date();
        Date fromDate = DateUtils.getBeginningOfMonth(now);
        Date toDate = DateUtils.getEndOfMonth(now);

        serviceTestData.createTestData();
        serviceTestData.createTestKanbans(false);

        EquipmentEntity adPro1 = serviceTestData.getEquipments().stream().filter(p -> ServiceTestData.EQUIPMENT_IDENT_1_1.equals(p.getEquipmentIdentify())).findFirst().get();

        WorkEntity work1 = serviceTestData.getWorks().get(0);
        WorkEntity work2 = serviceTestData.getWorks().get(1);
        WorkEntity work3 = serviceTestData.getWorks().get(2);
        List<Long> targetWorkIds = Arrays.asList(work1.getWorkId(), work2.getWorkId(), work3.getWorkId());

        AndonMonitorLineProductSetting setting = AndonMonitorLineProductSetting.create();

        WorkSetting workSetting1 = new WorkSetting();
        workSetting1.setWorkIds(Arrays.asList(work1.getWorkId()));

        WorkSetting workSetting2 = new WorkSetting();
        workSetting2.setWorkIds(Arrays.asList(work2.getWorkId()));

        WorkSetting workSetting3 = new WorkSetting();
        workSetting3.setWorkIds(Arrays.asList(work3.getWorkId()));

        setting.setWorkCollection(Arrays.asList(workSetting1, workSetting2, workSetting3));

        // 正しい実績数
        List<Integer> actualNums = new LinkedList();
        actualNums.add(0);
        actualNums.add(0);
        actualNums.add(0);

        // 当月の実績数をチェックする。
        this.checkPlanNumInfoWork(setting, 0, fromDate, toDate);

        // 前月・当月と実績を作成しながら生産数情報のチェックを行なう。
        Long transactionId = 1L;

        Date today = DateUtils.getBeginningOfDate(new Date());// 当日 0時
        Date firstDay = DateUtils.getBeginningOfMonth(today);// 当月の1日 0時

        Calendar cal = Calendar.getInstance();
        cal.setTime(firstDay);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        Date workingDate = DateUtils.getEndOfMonth(cal.getTime());// 前月の最終日 23:59:59.999

        List<String> workflowNames = Arrays.asList(ServiceTestData.WORKFLOW_NAME_1, ServiceTestData.WORKFLOW_NAME_2, ServiceTestData.WORKFLOW_NAME_3);

        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int listId = 0; listId < 3; listId++) {
                    KanbanEntity kanban = serviceTestData.getKanbanList(workflowNames.get(listId), 1).get(index);

                    List<WorkKanbanEntity> workKanbans = kanban.getWorkKanbanCollection();
                    Collections.sort(workKanbans, ((a, b) -> a.getWorkKanbanOrder().compareTo(b.getWorkKanbanOrder())));

                    Calendar workCal = Calendar.getInstance();
                    workCal.setTime(workingDate);

                    Date startDate = workCal.getTime();
                    for (WorkKanbanEntity workKanban : workKanbans) {
                        transactionId = serviceTestData.compWorkKanban(kanban, workKanban, startDate, transactionId, adPro1);

                        workCal.add(Calendar.MINUTE, 1);
                        workCal.add(Calendar.MINUTE, 1);
                        startDate = workCal.getTime();

                        workKanban = ServiceTestData.getWorkKanbanRest().find(workKanban.getWorkKanbanId(), null);

                        // 当月分のみ正しい実績数にカウント
                        Date actualDate = DateUtils.getBeginningOfDate(workKanban.getActualCompTime());
                        if (firstDay.equals(DateUtils.getBeginningOfMonth(actualDate))) {
                            int targetWorkIndex = targetWorkIds.indexOf(workKanban.getWorkId());
                            if (targetWorkIndex >= 0) {
                                actualNums.set(targetWorkIndex, actualNums.get(targetWorkIndex) + 1);
                            }
                        }

                        this.checkPlanNumInfoWork(setting, actualNums.stream().min((a, b) -> a.compareTo(b)).get(), fromDate, toDate);
                    }
                }

                index++;
            }

            // 1日進める
            cal.add(Calendar.DAY_OF_MONTH, 1);
            workingDate = cal.getTime();
        }

        System.out.println("testGetMonthlyPlanInfoWork end.");
    }

    /**
     * 指定ラインの月単位の実績数をチェックする。
     *
     * @param setting 進捗モニタ設定
     * @param actualNum 正しい実績数
     * @throws Exception 実績数が異なる場合、エラーになる。
     */
    private void checkMonthlyPlanInfo(AndonMonitorLineProductSetting setting, Integer actualNum) throws Exception {
        MonitorPlanNumInfoEntity info = facade.getMonthlyPlanInfo(setting);// TODO: LineProductEntity が View のためエラーになる。
        assertEquals(info.getActualNum(), actualNum);
    }

    /**
     * 指定ラインの日単位の設備生産数情報を取得するテスト
     *
     * @throws Exception 
     */
    @Ignore//TODO: ReportOutEntity が View のため、getDailyEquipmentPlanInfo()が実行できないため無効化。
    @Test
    public void testGetDailyEquipmentPlanInfo() throws Exception {
        System.out.println("testGetDailyEquipmentPlanInfo start.");

        serviceTestData.createTestData();
        serviceTestData.createTestKanbans(false);

        List<WorkEquipmentSetting> equipmentSettings1 = new LinkedList();

        EquipmentEntity adPro1 = serviceTestData.getLineEquipments(0).get(0);
        EquipmentEntity adPro2 = serviceTestData.getLineEquipments(0).get(1);
        EquipmentEntity adPro3 = serviceTestData.getLineEquipments(0).get(2);

        WorkEquipmentSetting workEquipmentSetting1_1 = new WorkEquipmentSetting();
        workEquipmentSetting1_1.setTitle(adPro1.getEquipmentName());
        workEquipmentSetting1_1.setOrder(0);
        workEquipmentSetting1_1.setEquipmentIds(Arrays.asList(adPro1.getEquipmentId()));

        WorkEquipmentSetting workEquipmentSetting1_2 = new WorkEquipmentSetting();
        workEquipmentSetting1_2.setTitle(adPro2.getEquipmentName());
        workEquipmentSetting1_2.setOrder(1);
        workEquipmentSetting1_2.setEquipmentIds(Arrays.asList(adPro2.getEquipmentId()));

        WorkEquipmentSetting workEquipmentSetting1_3 = new WorkEquipmentSetting();
        workEquipmentSetting1_2.setTitle(adPro3.getEquipmentName());
        workEquipmentSetting1_2.setOrder(2);
        workEquipmentSetting1_2.setEquipmentIds(Arrays.asList(adPro3.getEquipmentId()));

        equipmentSettings1.add(workEquipmentSetting1_1);
        equipmentSettings1.add(workEquipmentSetting1_2);
        equipmentSettings1.add(workEquipmentSetting1_3);

        AndonMonitorLineProductSetting setting1 = AndonMonitorLineProductSetting.create();
        setting1.setLineId(serviceTestData.getLineEquipmentId(0));
        setting1.setWorkEquipmentCollection(equipmentSettings1);


        List<Integer> actualNums = new LinkedList();
        actualNums.add(0);
        actualNums.add(0);
        actualNums.add(0);

        this.checkDailyEquipmentPlanInfo(setting1, actualNums);

        System.out.println("testGetDailyEquipmentPlanInfo end.");
    }

    /**
     * 指定ラインの日単位の設備実績数をチェックする。
     *
     * @param setting
     * @param actualNums
     * @throws Exception 
     */
    private void checkDailyEquipmentPlanInfo(AndonMonitorLineProductSetting setting, List<Integer> actualNums) throws Exception {
        List<MonitorEquipmentPlanNumInfoEntity> infos = facade.getDailyEquipmentPlanInfo(setting);// TODO: ReportOutEntity が View のためエラーになる。
        assertEquals(infos.size(), actualNums.size());
        for (int i = 0; i < infos.size(); i++) {
            MonitorEquipmentPlanNumInfoEntity info = infos.get(i);
            assertEquals(info.getActualNum(), actualNums.get(i));
        }
    }

    /**
     * 日別工程計画実績数を取得するテスト
     *
     * @throws Exception 
     */
    @Ignore//TODO: ReportOutEntity が View のため、getDailyWorkPlanNum()が実行できないため無効化。
    @Test
    public void testGetDailyWorkPlanNum() throws Exception {
        System.out.println("testGetDailyWorkPlanNum start.");

        serviceTestData.createTestData();
        serviceTestData.createTestKanbans(false);

        List<WorkSetting> workSettings = new LinkedList();

        WorkSetting workSetting1_1 = new WorkSetting();
        workSetting1_1.setTitle(serviceTestData.getWorks().get(0).getWorkName());
        workSetting1_1.setOrder(0);
        workSetting1_1.setWorkIds(Arrays.asList(serviceTestData.getWorks().get(0).getWorkId()));

        WorkSetting workSetting1_2 = new WorkSetting();
        workSetting1_2.setTitle(serviceTestData.getWorks().get(1).getWorkName());
        workSetting1_2.setOrder(1);
        workSetting1_2.setWorkIds(Arrays.asList(serviceTestData.getWorks().get(1).getWorkId()));

        WorkSetting workSetting1_3 = new WorkSetting();
        workSetting1_3.setTitle(serviceTestData.getWorks().get(2).getWorkName());
        workSetting1_3.setOrder(2);
        workSetting1_3.setWorkIds(Arrays.asList(serviceTestData.getWorks().get(2).getWorkId()));

        workSettings.add(workSetting1_1);
        workSettings.add(workSetting1_2);
        workSettings.add(workSetting1_3);

        AndonMonitorLineProductSetting setting1 = AndonMonitorLineProductSetting.create();
        setting1.setLineId(serviceTestData.getLineEquipmentId(0));
        setting1.setWorkCollection(workSettings);


        String pluginName = "pluginName";

        Integer actualNum = 0;

        this.checkDailyWorkPlanInfo(setting1, pluginName, actualNum);

        System.out.println("testGetDailyWorkPlanNum end.");
    }

    /**
     * 日別工程実績数をチェックする。
     *
     * @param setting
     * @param pluginName
     * @param actualNum
     * @throws Exception 
     */
    private void checkDailyWorkPlanInfo(AndonMonitorLineProductSetting setting, String pluginName, Integer actualNum) throws Exception {
        MonitorWorkPlanNumInfoEntity info = facade.getDailyWorkPlanNum(setting, pluginName);// TODO: ReportOutEntity が View のためエラーになる。
        assertEquals(info.getActualNum(), actualNum);
    }

    /**
     * 工程別計画実績数を取得するテスト
     *
     * @throws Exception 
     */
    @Ignore//TODO: ReportOutEntity が View のため、getWorkPlanNum()が実行できないため無効化。
    @Test
    public void testGetWorkPlanNum() throws Exception {
        System.out.println("testGetWorkPlanNum start.");

        serviceTestData.createTestData();
        serviceTestData.createTestKanbans(false);

        List<WorkSetting> workSettings = new LinkedList();

        WorkSetting workSetting1_1 = new WorkSetting();
        workSetting1_1.setTitle(serviceTestData.getWorks().get(0).getWorkName());
        workSetting1_1.setOrder(0);
        workSetting1_1.setWorkIds(Arrays.asList(serviceTestData.getWorks().get(0).getWorkId()));

        WorkSetting workSetting1_2 = new WorkSetting();
        workSetting1_2.setTitle(serviceTestData.getWorks().get(1).getWorkName());
        workSetting1_2.setOrder(1);
        workSetting1_2.setWorkIds(Arrays.asList(serviceTestData.getWorks().get(1).getWorkId()));

        WorkSetting workSetting1_3 = new WorkSetting();
        workSetting1_3.setTitle(serviceTestData.getWorks().get(2).getWorkName());
        workSetting1_3.setOrder(2);
        workSetting1_3.setWorkIds(Arrays.asList(serviceTestData.getWorks().get(2).getWorkId()));

        workSettings.add(workSetting1_1);
        workSettings.add(workSetting1_2);
        workSettings.add(workSetting1_3);

        AndonMonitorLineProductSetting setting1 = AndonMonitorLineProductSetting.create();
        setting1.setLineId(serviceTestData.getLineEquipmentId(0));
        setting1.setWorkCollection(workSettings);


        List<Integer> actualNums = new LinkedList();
        actualNums.add(0);
        actualNums.add(0);
        actualNums.add(0);

        this.checkWorkPlanInfo(setting1, actualNums);

        System.out.println("testGetWorkPlanNum end.");
    }

    /**
     * 工程別実績数をチェックする。
     *
     * @param setting
     * @param actualNums
     * @throws Exception 
     */
    private void checkWorkPlanInfo(AndonMonitorLineProductSetting setting, List<Integer> actualNums) throws Exception {
        List<MonitorWorkPlanNumInfoEntity> infos = facade.getWorkPlanNum(setting);// TODO: ReportOutEntity が View のためエラーになる。
        assertEquals(infos.size(), actualNums.size());
        for (int i = 0; i < infos.size(); i++) {
            MonitorWorkPlanNumInfoEntity info = infos.get(i);
            assertEquals(info.getActualNum(), actualNums.get(i));
        }
    }
}
