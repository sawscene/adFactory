/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service.workflow;

import jp.adtekfuji.adfactoryserver.service.workflow.WorkflowProcess;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.holiday.HolidayEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowHierarchyEntity;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.service.ServiceTestData;
import jp.adtekfuji.adfactoryserver.utility.TestUtils;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nar-nakamura
 */
public class WorkflowProcessTest {

    private static ServiceTestData serviceTestData = null;

    private static final String WORKNAME1 = "工程①";
    private static final String WORKNAME2 = "工程②";
    private static final String WORKNAME3 = "工程③";
    private static final String WORKNAME4 = "工程④";
    private static final String WORKNAME5 = "工程⑤";
    private static final String WORKNAME6 = "工程⑥";
    private static final String WORKNAME7 = "工程⑦";
    private static final String WORKNAME8 = "工程⑧";
    private static final String WORKNAME9 = "工程⑨";
    private static final String WORKNAME10 = "工程⑩";

    public WorkflowProcessTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        serviceTestData = new ServiceTestData();
        ServiceTestData.setUpClass();

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
     * カンバンに基準時間を設定するテスト
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetBaseTime() throws Exception {
        System.out.println("testSetBaseTime");

        serviceTestData.createTestData();

        // テスト用のカンバンを作成する。
        KanbanEntity kanban = this.createTestKanban();

        // 休憩時間
        List<BreakTimeInfoEntity> breaktimes = serviceTestData.getBreaktimes();

        // 休日
        List<HolidayEntity> holidays = serviceTestData.getHolidays();

        // 基準開始時間「10:00」→「11:00」(オフセット「01:00」)
        this.testSetBaseTime1(kanban, breaktimes, holidays);
        this.testSetBaseTime1_1(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「12:00」(オフセット「02:00」)
        this.testSetBaseTime1(kanban, breaktimes, holidays);
        this.testSetBaseTime1_2(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「09:00」(オフセット「-01:00」)
        this.testSetBaseTime1(kanban, breaktimes, holidays);
        this.testSetBaseTime1_3(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「08:00」(オフセット「-02:00」)
        this.testSetBaseTime1(kanban, breaktimes, holidays);
        this.testSetBaseTime1_4(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「07:00」(オフセット「-03:00」)
        this.testSetBaseTime1(kanban, breaktimes, holidays);
        this.testSetBaseTime1_5(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「前日17:00」(オフセット「-17:00」)
        this.testSetBaseTime1(kanban, breaktimes, holidays);
        this.testSetBaseTime1_6(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「前日16:00」(オフセット「-18:00」)
        this.testSetBaseTime1(kanban, breaktimes, holidays);
        this.testSetBaseTime1_7(kanban, breaktimes, holidays);

        // 休日をはさんだ日程
        // 基準開始時間「10:00」→「11:00」(オフセット「01:00」)
        this.testSetBaseTime2(kanban, breaktimes, holidays);
        this.testSetBaseTime2_1(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「12:00」(オフセット「02:00」)
        this.testSetBaseTime2(kanban, breaktimes, holidays);
        this.testSetBaseTime2_2(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「09:00」(オフセット「-01:00」)
        this.testSetBaseTime2(kanban, breaktimes, holidays);
        this.testSetBaseTime2_3(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「08:00」(オフセット「-02:00」)
        this.testSetBaseTime2(kanban, breaktimes, holidays);
        this.testSetBaseTime2_4(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「07:00」(オフセット「-03:00」)
        this.testSetBaseTime2(kanban, breaktimes, holidays);
        this.testSetBaseTime2_5(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「前日17:00」(オフセット「-17:00」)
        this.testSetBaseTime2(kanban, breaktimes, holidays);
        this.testSetBaseTime2_6(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「前日16:00」(オフセット「-18:00」)
        this.testSetBaseTime2(kanban, breaktimes, holidays);
        this.testSetBaseTime2_7(kanban, breaktimes, holidays);

        // 前日が休日
        // 基準開始時間「10:00」→「11:00」(オフセット「01:00」)
        this.testSetBaseTime3(kanban, breaktimes, holidays);
        this.testSetBaseTime3_1(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「12:00」(オフセット「02:00」)
        this.testSetBaseTime3(kanban, breaktimes, holidays);
        this.testSetBaseTime3_2(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「09:00」(オフセット「-01:00」)
        this.testSetBaseTime3(kanban, breaktimes, holidays);
        this.testSetBaseTime3_3(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「08:00」(オフセット「-02:00」)
        this.testSetBaseTime3(kanban, breaktimes, holidays);
        this.testSetBaseTime3_4(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「07:00」(オフセット「-03:00」)
        this.testSetBaseTime3(kanban, breaktimes, holidays);
        this.testSetBaseTime3_5(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「前日17:00」(オフセット「-17:00」)
        this.testSetBaseTime3(kanban, breaktimes, holidays);
        this.testSetBaseTime3_6(kanban, breaktimes, holidays);
        // 基準開始時間「10:00」→「前日16:00」(オフセット「-18:00」)
        this.testSetBaseTime3(kanban, breaktimes, holidays);
        this.testSetBaseTime3_7(kanban, breaktimes, holidays);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/10 10:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime1(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 12:00:00",// 工程③
                "2019/04/10 14:00:00",// 工程④
                "2019/04/10 15:00:00",// 工程⑤
                "2019/04/10 16:00:00",// 工程⑥
                "2019/04/11 09:00:00",// 工程⑦
                "2019/04/11 10:00:00",// 工程⑧
                "2019/04/11 11:00:00",// 工程⑨
                "2019/04/11 12:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 12:00:00",// 工程②
                "2019/04/10 14:00:00",// 工程③
                "2019/04/10 15:00:00",// 工程④
                "2019/04/10 16:00:00",// 工程⑤
                "2019/04/10 17:00:00",// 工程⑥
                "2019/04/11 10:00:00",// 工程⑦
                "2019/04/11 11:00:00",// 工程⑧
                "2019/04/11 12:00:00",// 工程⑨
                "2019/04/11 14:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/10 11:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime1_1(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/10 11:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 12:00:00",// 工程②
                "2019/04/10 14:00:00",// 工程③
                "2019/04/10 15:00:00",// 工程④
                "2019/04/10 16:00:00",// 工程⑤
                "2019/04/11 09:00:00",// 工程⑥
                "2019/04/11 10:00:00",// 工程⑦
                "2019/04/11 11:00:00",// 工程⑧
                "2019/04/11 12:00:00",// 工程⑨
                "2019/04/11 14:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 12:00:00",// 工程①
                "2019/04/10 14:00:00",// 工程②
                "2019/04/10 15:00:00",// 工程③
                "2019/04/10 16:00:00",// 工程④
                "2019/04/10 17:00:00",// 工程⑤
                "2019/04/11 10:00:00",// 工程⑥
                "2019/04/11 11:00:00",// 工程⑦
                "2019/04/11 12:00:00",// 工程⑧
                "2019/04/11 14:00:00",// 工程⑨
                "2019/04/11 15:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/10 12:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime1_2(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/10 12:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 12:00:00",// 工程①
                "2019/04/10 14:00:00",// 工程②
                "2019/04/10 15:00:00",// 工程③
                "2019/04/10 16:00:00",// 工程④
                "2019/04/11 09:00:00",// 工程⑤
                "2019/04/11 10:00:00",// 工程⑥
                "2019/04/11 11:00:00",// 工程⑦
                "2019/04/11 12:00:00",// 工程⑧
                "2019/04/11 14:00:00",// 工程⑨
                "2019/04/11 15:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 14:00:00",// 工程①
                "2019/04/10 15:00:00",// 工程②
                "2019/04/10 16:00:00",// 工程③
                "2019/04/10 17:00:00",// 工程④
                "2019/04/11 10:00:00",// 工程⑤
                "2019/04/11 11:00:00",// 工程⑥
                "2019/04/11 12:00:00",// 工程⑦
                "2019/04/11 14:00:00",// 工程⑧
                "2019/04/11 15:00:00",// 工程⑨
                "2019/04/11 16:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/10 09:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime1_3(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/10 09:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 09:00:00",// 工程①
                "2019/04/10 10:00:00",// 工程②
                "2019/04/10 11:00:00",// 工程③
                "2019/04/10 12:00:00",// 工程④
                "2019/04/10 14:00:00",// 工程⑤
                "2019/04/10 15:00:00",// 工程⑥
                "2019/04/10 16:00:00",// 工程⑦
                "2019/04/11 09:00:00",// 工程⑧
                "2019/04/11 10:00:00",// 工程⑨
                "2019/04/11 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 12:00:00",// 工程③
                "2019/04/10 14:00:00",// 工程④
                "2019/04/10 15:00:00",// 工程⑤
                "2019/04/10 16:00:00",// 工程⑥
                "2019/04/10 17:00:00",// 工程⑦
                "2019/04/11 10:00:00",// 工程⑧
                "2019/04/11 11:00:00",// 工程⑨
                "2019/04/11 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/10 08:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime1_4(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/10 08:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 08:00:00",// 工程①
                "2019/04/10 10:00:00",// 工程②
                "2019/04/10 11:00:00",// 工程③
                "2019/04/10 12:00:00",// 工程④
                "2019/04/10 14:00:00",// 工程⑤
                "2019/04/10 15:00:00",// 工程⑥
                "2019/04/10 16:00:00",// 工程⑦
                "2019/04/11 09:00:00",// 工程⑧
                "2019/04/11 10:00:00",// 工程⑨
                "2019/04/11 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 12:00:00",// 工程③
                "2019/04/10 14:00:00",// 工程④
                "2019/04/10 15:00:00",// 工程⑤
                "2019/04/10 16:00:00",// 工程⑥
                "2019/04/10 17:00:00",// 工程⑦
                "2019/04/11 10:00:00",// 工程⑧
                "2019/04/11 11:00:00",// 工程⑨
                "2019/04/11 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/10 07:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime1_5(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/10 07:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 07:00:00",// 工程①
                "2019/04/10 10:00:00",// 工程②
                "2019/04/10 11:00:00",// 工程③
                "2019/04/10 12:00:00",// 工程④
                "2019/04/10 14:00:00",// 工程⑤
                "2019/04/10 15:00:00",// 工程⑥
                "2019/04/10 16:00:00",// 工程⑦
                "2019/04/11 09:00:00",// 工程⑧
                "2019/04/11 10:00:00",// 工程⑨
                "2019/04/11 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 12:00:00",// 工程③
                "2019/04/10 14:00:00",// 工程④
                "2019/04/10 15:00:00",// 工程⑤
                "2019/04/10 16:00:00",// 工程⑥
                "2019/04/10 17:00:00",// 工程⑦
                "2019/04/11 10:00:00",// 工程⑧
                "2019/04/11 11:00:00",// 工程⑨
                "2019/04/11 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/09 17:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime1_6(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/09 17:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 09:00:00",// 工程①
                "2019/04/10 10:00:00",// 工程②
                "2019/04/10 11:00:00",// 工程③
                "2019/04/10 12:00:00",// 工程④
                "2019/04/10 14:00:00",// 工程⑤
                "2019/04/10 15:00:00",// 工程⑥
                "2019/04/10 16:00:00",// 工程⑦
                "2019/04/11 09:00:00",// 工程⑧
                "2019/04/11 10:00:00",// 工程⑨
                "2019/04/11 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 12:00:00",// 工程③
                "2019/04/10 14:00:00",// 工程④
                "2019/04/10 15:00:00",// 工程⑤
                "2019/04/10 16:00:00",// 工程⑥
                "2019/04/10 17:00:00",// 工程⑦
                "2019/04/11 10:00:00",// 工程⑧
                "2019/04/11 11:00:00",// 工程⑨
                "2019/04/11 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/09 16:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime1_7(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/09 16:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/09 16:00:00",// 工程①
                "2019/04/10 09:00:00",// 工程②
                "2019/04/10 10:00:00",// 工程③
                "2019/04/10 11:00:00",// 工程④
                "2019/04/10 12:00:00",// 工程⑤
                "2019/04/10 14:00:00",// 工程⑥
                "2019/04/10 15:00:00",// 工程⑦
                "2019/04/10 16:00:00",// 工程⑧
                "2019/04/11 09:00:00",// 工程⑨
                "2019/04/11 10:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/09 17:00:00",// 工程①
                "2019/04/10 10:00:00",// 工程②
                "2019/04/10 11:00:00",// 工程③
                "2019/04/10 12:00:00",// 工程④
                "2019/04/10 14:00:00",// 工程⑤
                "2019/04/10 15:00:00",// 工程⑥
                "2019/04/10 16:00:00",// 工程⑦
                "2019/04/10 17:00:00",// 工程⑧
                "2019/04/11 10:00:00",// 工程⑨
                "2019/04/11 11:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/12 10:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime2(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/12 10:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 10:00:00",// 工程①
                "2019/04/12 11:00:00",// 工程②
                "2019/04/12 12:00:00",// 工程③
                "2019/04/12 14:00:00",// 工程④
                "2019/04/12 15:00:00",// 工程⑤
                "2019/04/12 16:00:00",// 工程⑥
                "2019/04/15 09:00:00",// 工程⑦
                "2019/04/15 10:00:00",// 工程⑧
                "2019/04/15 11:00:00",// 工程⑨
                "2019/04/15 12:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 12:00:00",// 工程②
                "2019/04/12 14:00:00",// 工程③
                "2019/04/12 15:00:00",// 工程④
                "2019/04/12 16:00:00",// 工程⑤
                "2019/04/12 17:00:00",// 工程⑥
                "2019/04/15 10:00:00",// 工程⑦
                "2019/04/15 11:00:00",// 工程⑧
                "2019/04/15 12:00:00",// 工程⑨
                "2019/04/15 14:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/12 11:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime2_1(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/12 11:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 12:00:00",// 工程②
                "2019/04/12 14:00:00",// 工程③
                "2019/04/12 15:00:00",// 工程④
                "2019/04/12 16:00:00",// 工程⑤
                "2019/04/15 09:00:00",// 工程⑥
                "2019/04/15 10:00:00",// 工程⑦
                "2019/04/15 11:00:00",// 工程⑧
                "2019/04/15 12:00:00",// 工程⑨
                "2019/04/15 14:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 12:00:00",// 工程①
                "2019/04/12 14:00:00",// 工程②
                "2019/04/12 15:00:00",// 工程③
                "2019/04/12 16:00:00",// 工程④
                "2019/04/12 17:00:00",// 工程⑤
                "2019/04/15 10:00:00",// 工程⑥
                "2019/04/15 11:00:00",// 工程⑦
                "2019/04/15 12:00:00",// 工程⑧
                "2019/04/15 14:00:00",// 工程⑨
                "2019/04/15 15:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/12 11:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime2_2(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/12 11:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 12:00:00",// 工程②
                "2019/04/12 14:00:00",// 工程③
                "2019/04/12 15:00:00",// 工程④
                "2019/04/12 16:00:00",// 工程⑤
                "2019/04/15 09:00:00",// 工程⑥
                "2019/04/15 10:00:00",// 工程⑦
                "2019/04/15 11:00:00",// 工程⑧
                "2019/04/15 12:00:00",// 工程⑨
                "2019/04/15 14:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 12:00:00",// 工程①
                "2019/04/12 14:00:00",// 工程②
                "2019/04/12 15:00:00",// 工程③
                "2019/04/12 16:00:00",// 工程④
                "2019/04/12 17:00:00",// 工程⑤
                "2019/04/15 10:00:00",// 工程⑥
                "2019/04/15 11:00:00",// 工程⑦
                "2019/04/15 12:00:00",// 工程⑧
                "2019/04/15 14:00:00",// 工程⑨
                "2019/04/15 15:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/12 09:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime2_3(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/12 09:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 09:00:00",// 工程①
                "2019/04/12 10:00:00",// 工程②
                "2019/04/12 11:00:00",// 工程③
                "2019/04/12 12:00:00",// 工程④
                "2019/04/12 14:00:00",// 工程⑤
                "2019/04/12 15:00:00",// 工程⑥
                "2019/04/12 16:00:00",// 工程⑦
                "2019/04/15 09:00:00",// 工程⑧
                "2019/04/15 10:00:00",// 工程⑨
                "2019/04/15 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 10:00:00",// 工程①
                "2019/04/12 11:00:00",// 工程②
                "2019/04/12 12:00:00",// 工程③
                "2019/04/12 14:00:00",// 工程④
                "2019/04/12 15:00:00",// 工程⑤
                "2019/04/12 16:00:00",// 工程⑥
                "2019/04/12 17:00:00",// 工程⑦
                "2019/04/15 10:00:00",// 工程⑧
                "2019/04/15 11:00:00",// 工程⑨
                "2019/04/15 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/12 08:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime2_4(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/12 08:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 08:00:00",// 工程①
                "2019/04/12 10:00:00",// 工程②
                "2019/04/12 11:00:00",// 工程③
                "2019/04/12 12:00:00",// 工程④
                "2019/04/12 14:00:00",// 工程⑤
                "2019/04/12 15:00:00",// 工程⑥
                "2019/04/12 16:00:00",// 工程⑦
                "2019/04/15 09:00:00",// 工程⑧
                "2019/04/15 10:00:00",// 工程⑨
                "2019/04/15 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 10:00:00",// 工程①
                "2019/04/12 11:00:00",// 工程②
                "2019/04/12 12:00:00",// 工程③
                "2019/04/12 14:00:00",// 工程④
                "2019/04/12 15:00:00",// 工程⑤
                "2019/04/12 16:00:00",// 工程⑥
                "2019/04/12 17:00:00",// 工程⑦
                "2019/04/15 10:00:00",// 工程⑧
                "2019/04/15 11:00:00",// 工程⑨
                "2019/04/15 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/12 07:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime2_5(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/12 07:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 07:00:00",// 工程①
                "2019/04/12 10:00:00",// 工程②
                "2019/04/12 11:00:00",// 工程③
                "2019/04/12 12:00:00",// 工程④
                "2019/04/12 14:00:00",// 工程⑤
                "2019/04/12 15:00:00",// 工程⑥
                "2019/04/12 16:00:00",// 工程⑦
                "2019/04/15 09:00:00",// 工程⑧
                "2019/04/15 10:00:00",// 工程⑨
                "2019/04/15 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 10:00:00",// 工程①
                "2019/04/12 11:00:00",// 工程②
                "2019/04/12 12:00:00",// 工程③
                "2019/04/12 14:00:00",// 工程④
                "2019/04/12 15:00:00",// 工程⑤
                "2019/04/12 16:00:00",// 工程⑥
                "2019/04/12 17:00:00",// 工程⑦
                "2019/04/15 10:00:00",// 工程⑧
                "2019/04/15 11:00:00",// 工程⑨
                "2019/04/15 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/11 17:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime2_6(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/11 17:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 09:00:00",// 工程①
                "2019/04/12 10:00:00",// 工程②
                "2019/04/12 11:00:00",// 工程③
                "2019/04/12 12:00:00",// 工程④
                "2019/04/12 14:00:00",// 工程⑤
                "2019/04/12 15:00:00",// 工程⑥
                "2019/04/12 16:00:00",// 工程⑦
                "2019/04/15 09:00:00",// 工程⑧
                "2019/04/15 10:00:00",// 工程⑨
                "2019/04/15 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 10:00:00",// 工程①
                "2019/04/12 11:00:00",// 工程②
                "2019/04/12 12:00:00",// 工程③
                "2019/04/12 14:00:00",// 工程④
                "2019/04/12 15:00:00",// 工程⑤
                "2019/04/12 16:00:00",// 工程⑥
                "2019/04/12 17:00:00",// 工程⑦
                "2019/04/15 10:00:00",// 工程⑧
                "2019/04/15 11:00:00",// 工程⑨
                "2019/04/15 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/11 16:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime2_7(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/11 16:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/11 16:00:00",// 工程①
                "2019/04/12 09:00:00",// 工程②
                "2019/04/12 10:00:00",// 工程③
                "2019/04/12 11:00:00",// 工程④
                "2019/04/12 12:00:00",// 工程⑤
                "2019/04/12 14:00:00",// 工程⑥
                "2019/04/12 15:00:00",// 工程⑦
                "2019/04/12 16:00:00",// 工程⑧
                "2019/04/15 09:00:00",// 工程⑨
                "2019/04/15 10:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/11 17:00:00",// 工程①
                "2019/04/12 10:00:00",// 工程②
                "2019/04/12 11:00:00",// 工程③
                "2019/04/12 12:00:00",// 工程④
                "2019/04/12 14:00:00",// 工程⑤
                "2019/04/12 15:00:00",// 工程⑥
                "2019/04/12 16:00:00",// 工程⑦
                "2019/04/12 17:00:00",// 工程⑧
                "2019/04/15 10:00:00",// 工程⑨
                "2019/04/15 11:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/08 10:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime3(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/08 10:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/08 10:00:00",// 工程①
                "2019/04/08 11:00:00",// 工程②
                "2019/04/08 12:00:00",// 工程③
                "2019/04/08 14:00:00",// 工程④
                "2019/04/08 15:00:00",// 工程⑤
                "2019/04/08 16:00:00",// 工程⑥
                "2019/04/09 09:00:00",// 工程⑦
                "2019/04/09 10:00:00",// 工程⑧
                "2019/04/09 11:00:00",// 工程⑨
                "2019/04/09 12:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/08 11:00:00",// 工程①
                "2019/04/08 12:00:00",// 工程②
                "2019/04/08 14:00:00",// 工程③
                "2019/04/08 15:00:00",// 工程④
                "2019/04/08 16:00:00",// 工程⑤
                "2019/04/08 17:00:00",// 工程⑥
                "2019/04/09 10:00:00",// 工程⑦
                "2019/04/09 11:00:00",// 工程⑧
                "2019/04/09 12:00:00",// 工程⑨
                "2019/04/09 14:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/08 11:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime3_1(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/08 11:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/08 11:00:00",// 工程①
                "2019/04/08 12:00:00",// 工程②
                "2019/04/08 14:00:00",// 工程③
                "2019/04/08 15:00:00",// 工程④
                "2019/04/08 16:00:00",// 工程⑤
                "2019/04/09 09:00:00",// 工程⑥
                "2019/04/09 10:00:00",// 工程⑦
                "2019/04/09 11:00:00",// 工程⑧
                "2019/04/09 12:00:00",// 工程⑨
                "2019/04/09 14:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/08 12:00:00",// 工程①
                "2019/04/08 14:00:00",// 工程②
                "2019/04/08 15:00:00",// 工程③
                "2019/04/08 16:00:00",// 工程④
                "2019/04/08 17:00:00",// 工程⑤
                "2019/04/09 10:00:00",// 工程⑥
                "2019/04/09 11:00:00",// 工程⑦
                "2019/04/09 12:00:00",// 工程⑧
                "2019/04/09 14:00:00",// 工程⑨
                "2019/04/09 15:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/08 12:00:00」に設定)
     *
     * 
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime3_2(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/08 12:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/08 12:00:00",// 工程①
                "2019/04/08 14:00:00",// 工程②
                "2019/04/08 15:00:00",// 工程③
                "2019/04/08 16:00:00",// 工程④
                "2019/04/09 09:00:00",// 工程⑤
                "2019/04/09 10:00:00",// 工程⑥
                "2019/04/09 11:00:00",// 工程⑦
                "2019/04/09 12:00:00",// 工程⑧
                "2019/04/09 14:00:00",// 工程⑨
                "2019/04/09 15:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/08 14:00:00",// 工程①
                "2019/04/08 15:00:00",// 工程②
                "2019/04/08 16:00:00",// 工程③
                "2019/04/08 17:00:00",// 工程④
                "2019/04/09 10:00:00",// 工程⑤
                "2019/04/09 11:00:00",// 工程⑥
                "2019/04/09 12:00:00",// 工程⑦
                "2019/04/09 14:00:00",// 工程⑧
                "2019/04/09 15:00:00",// 工程⑨
                "2019/04/09 16:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/08 09:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime3_3(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/08 09:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/08 09:00:00",// 工程①
                "2019/04/08 10:00:00",// 工程②
                "2019/04/08 11:00:00",// 工程③
                "2019/04/08 12:00:00",// 工程④
                "2019/04/08 14:00:00",// 工程⑤
                "2019/04/08 15:00:00",// 工程⑥
                "2019/04/08 16:00:00",// 工程⑦
                "2019/04/09 09:00:00",// 工程⑧
                "2019/04/09 10:00:00",// 工程⑨
                "2019/04/09 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/08 10:00:00",// 工程①
                "2019/04/08 11:00:00",// 工程②
                "2019/04/08 12:00:00",// 工程③
                "2019/04/08 14:00:00",// 工程④
                "2019/04/08 15:00:00",// 工程⑤
                "2019/04/08 16:00:00",// 工程⑥
                "2019/04/08 17:00:00",// 工程⑦
                "2019/04/09 10:00:00",// 工程⑧
                "2019/04/09 11:00:00",// 工程⑨
                "2019/04/09 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/08 08:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime3_4(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/08 08:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/08 08:00:00",// 工程①
                "2019/04/08 10:00:00",// 工程②
                "2019/04/08 11:00:00",// 工程③
                "2019/04/08 12:00:00",// 工程④
                "2019/04/08 14:00:00",// 工程⑤
                "2019/04/08 15:00:00",// 工程⑥
                "2019/04/08 16:00:00",// 工程⑦
                "2019/04/09 09:00:00",// 工程⑧
                "2019/04/09 10:00:00",// 工程⑨
                "2019/04/09 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/08 10:00:00",// 工程①
                "2019/04/08 11:00:00",// 工程②
                "2019/04/08 12:00:00",// 工程③
                "2019/04/08 14:00:00",// 工程④
                "2019/04/08 15:00:00",// 工程⑤
                "2019/04/08 16:00:00",// 工程⑥
                "2019/04/08 17:00:00",// 工程⑦
                "2019/04/09 10:00:00",// 工程⑧
                "2019/04/09 11:00:00",// 工程⑨
                "2019/04/09 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/08 07:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime3_5(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/08 07:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/08 07:00:00",// 工程①
                "2019/04/08 10:00:00",// 工程②
                "2019/04/08 11:00:00",// 工程③
                "2019/04/08 12:00:00",// 工程④
                "2019/04/08 14:00:00",// 工程⑤
                "2019/04/08 15:00:00",// 工程⑥
                "2019/04/08 16:00:00",// 工程⑦
                "2019/04/09 09:00:00",// 工程⑧
                "2019/04/09 10:00:00",// 工程⑨
                "2019/04/09 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/08 10:00:00",// 工程①
                "2019/04/08 11:00:00",// 工程②
                "2019/04/08 12:00:00",// 工程③
                "2019/04/08 14:00:00",// 工程④
                "2019/04/08 15:00:00",// 工程⑤
                "2019/04/08 16:00:00",// 工程⑥
                "2019/04/08 17:00:00",// 工程⑦
                "2019/04/09 10:00:00",// 工程⑧
                "2019/04/09 11:00:00",// 工程⑨
                "2019/04/09 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/07 17:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime3_6(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/07 17:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/08 09:00:00",// 工程①
                "2019/04/08 10:00:00",// 工程②
                "2019/04/08 11:00:00",// 工程③
                "2019/04/08 12:00:00",// 工程④
                "2019/04/08 14:00:00",// 工程⑤
                "2019/04/08 15:00:00",// 工程⑥
                "2019/04/08 16:00:00",// 工程⑦
                "2019/04/09 09:00:00",// 工程⑧
                "2019/04/09 10:00:00",// 工程⑨
                "2019/04/09 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/08 10:00:00",// 工程①
                "2019/04/08 11:00:00",// 工程②
                "2019/04/08 12:00:00",// 工程③
                "2019/04/08 14:00:00",// 工程④
                "2019/04/08 15:00:00",// 工程⑤
                "2019/04/08 16:00:00",// 工程⑥
                "2019/04/08 17:00:00",// 工程⑦
                "2019/04/09 10:00:00",// 工程⑧
                "2019/04/09 11:00:00",// 工程⑨
                "2019/04/09 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * カンバンに基準時間を設定するテスト (基準開始日時を「2019/04/07 16:00:00」に設定)
     *
     * @param kanban カンバン情報
     * @param breaktimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception 
     */
    private void testSetBaseTime3_7(KanbanEntity kanban, List<BreakTimeInfoEntity> breaktimes, List<HolidayEntity> holidays) throws Exception {
        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/07 16:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/08 09:00:00",// 工程①
                "2019/04/08 10:00:00",// 工程②
                "2019/04/08 11:00:00",// 工程③
                "2019/04/08 12:00:00",// 工程④
                "2019/04/08 14:00:00",// 工程⑤
                "2019/04/08 15:00:00",// 工程⑥
                "2019/04/08 16:00:00",// 工程⑦
                "2019/04/09 09:00:00",// 工程⑧
                "2019/04/09 10:00:00",// 工程⑨
                "2019/04/09 11:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/08 10:00:00",// 工程①
                "2019/04/08 11:00:00",// 工程②
                "2019/04/08 12:00:00",// 工程③
                "2019/04/08 14:00:00",// 工程④
                "2019/04/08 15:00:00",// 工程⑤
                "2019/04/08 16:00:00",// 工程⑥
                "2019/04/08 17:00:00",// 工程⑦
                "2019/04/09 10:00:00",// 工程⑧
                "2019/04/09 11:00:00",// 工程⑨
                "2019/04/09 12:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 工程順の作業時間を更新するテスト
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdateTimetable() throws Exception {
        System.out.println("testUpdateTimetable");

        serviceTestData.createTestData();

        // テスト用のカンバンを作成する。
        KanbanEntity kanban = this.createTestKanban();

        // 工程①のタクトタイムを「01:00:00」から「00:30:00」に変更
        this.testUpdateTimetable1(kanban);
        // 工程⑤のタクトタイムを「01:00:00」から「00:30:00」に変更
        this.testUpdateTimetable2(kanban);
        // 工程③のタクトタイムを「01:00:00」から「01:30:00」に変更
        this.testUpdateTimetable3(kanban);
        // 工程⑩のタクトタイムを「01:00:00」から「01:30:00」に変更
        this.testUpdateTimetable4(kanban);
    }

    /**
     * 工程順の作業時間を更新するテスト (工程①のタクトタイムを「01:00:00」から「00:30:00」に変更)
     *
     * @param kanban カンバン情報
     * @throws Exception 
     */
    private void testUpdateTimetable1(KanbanEntity kanban) throws Exception {
        WorkEntity work = serviceTestData.getWorks().stream().filter(p -> WORKNAME1.equals(p.getWorkName())).findFirst().get();

        work.setTaktTime(30 * 60 * 1000);// タクトタイム (ミリ秒)

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        WorkflowEntity workflow = kanban.getWorkflow();

        // 工程順の作業時間を更新する。
        workflowProcess.updateTimetable(kanban.getWorkflow(), work);

        List<String> startDates = Arrays.asList(
                "1970/01/01 00:00:00",// 工程①
                "1970/01/01 00:30:00",// 工程②
                "1970/01/01 01:30:00",// 工程③
                "1970/01/01 02:30:00",// 工程④
                "1970/01/01 03:30:00",// 工程⑤
                "1970/01/01 04:30:00",// 工程⑥
                "1970/01/01 05:30:00",// 工程⑦
                "1970/01/01 06:30:00",// 工程⑧
                "1970/01/01 07:30:00",// 工程⑨
                "1970/01/01 08:30:00");// 工程⑩

        List<String> compDates = Arrays.asList(
                "1970/01/01 00:30:00",// 工程①
                "1970/01/01 01:30:00",// 工程②
                "1970/01/01 02:30:00",// 工程③
                "1970/01/01 03:30:00",// 工程④
                "1970/01/01 04:30:00",// 工程⑤
                "1970/01/01 05:30:00",// 工程⑥
                "1970/01/01 06:30:00",// 工程⑦
                "1970/01/01 07:30:00",// 工程⑧
                "1970/01/01 08:30:00",// 工程⑨
                "1970/01/01 09:30:00");// 工程⑩

        // 工程順の工程開始・完了日時をチェックする。
        TestUtils.checkUpdateTimetablePlanDate(workflow, startDates, compDates);
    }

    /**
     * 工程順の作業時間を更新するテスト (工程⑤のタクトタイムを「01:00:00」から「00:30:00」に変更)
     * @param kanban カンバン情報
     * @throws Exception 
     */
    private void testUpdateTimetable2(KanbanEntity kanban) throws Exception {
        WorkEntity work = serviceTestData.getWorks().stream().filter(p -> WORKNAME5.equals(p.getWorkName())).findFirst().get();

        work.setTaktTime(30 * 60 * 1000);// タクトタイム (ミリ秒)

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        WorkflowEntity workflow = kanban.getWorkflow();

        // 工程順の作業時間を更新する。
        workflowProcess.updateTimetable(kanban.getWorkflow(), work);

        List<String> startDates = Arrays.asList(
                "1970/01/01 00:00:00",// 工程①
                "1970/01/01 00:30:00",// 工程②
                "1970/01/01 01:30:00",// 工程③
                "1970/01/01 02:30:00",// 工程④
                "1970/01/01 03:30:00",// 工程⑤
                "1970/01/01 04:00:00",// 工程⑥
                "1970/01/01 05:00:00",// 工程⑦
                "1970/01/01 06:00:00",// 工程⑧
                "1970/01/01 07:00:00",// 工程⑨
                "1970/01/01 08:00:00");// 工程⑩

        List<String> compDates = Arrays.asList(
                "1970/01/01 00:30:00",// 工程①
                "1970/01/01 01:30:00",// 工程②
                "1970/01/01 02:30:00",// 工程③
                "1970/01/01 03:30:00",// 工程④
                "1970/01/01 04:00:00",// 工程⑤
                "1970/01/01 05:00:00",// 工程⑥
                "1970/01/01 06:00:00",// 工程⑦
                "1970/01/01 07:00:00",// 工程⑧
                "1970/01/01 08:00:00",// 工程⑨
                "1970/01/01 09:00:00");// 工程⑩

        // 工程順の工程開始・完了日時をチェックする。
        TestUtils.checkUpdateTimetablePlanDate(workflow, startDates, compDates);
    }

    /**
     * 工程順の作業時間を更新するテスト (工程③のタクトタイムを「01:00:00」から「01:30:00」に変更)
     *
     * @param kanban カンバン情報
     * @throws Exception 
     */
    private void testUpdateTimetable3(KanbanEntity kanban) throws Exception {
        WorkEntity work = serviceTestData.getWorks().stream().filter(p -> WORKNAME3.equals(p.getWorkName())).findFirst().get();

        work.setTaktTime(90 * 60 * 1000);// タクトタイム (ミリ秒)

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        WorkflowEntity workflow = kanban.getWorkflow();

        // 工程順の作業時間を更新する。
        workflowProcess.updateTimetable(kanban.getWorkflow(), work);

        List<String> startDates = Arrays.asList(
                "1970/01/01 00:00:00",// 工程①
                "1970/01/01 00:30:00",// 工程②
                "1970/01/01 01:30:00",// 工程③
                "1970/01/01 03:00:00",// 工程④
                "1970/01/01 04:00:00",// 工程⑤
                "1970/01/01 04:30:00",// 工程⑥
                "1970/01/01 05:30:00",// 工程⑦
                "1970/01/01 06:30:00",// 工程⑧
                "1970/01/01 07:30:00",// 工程⑨
                "1970/01/01 08:30:00");// 工程⑩

        List<String> compDates = Arrays.asList(
                "1970/01/01 00:30:00",// 工程①
                "1970/01/01 01:30:00",// 工程②
                "1970/01/01 03:00:00",// 工程③
                "1970/01/01 04:00:00",// 工程④
                "1970/01/01 04:30:00",// 工程⑤
                "1970/01/01 05:30:00",// 工程⑥
                "1970/01/01 06:30:00",// 工程⑦
                "1970/01/01 07:30:00",// 工程⑧
                "1970/01/01 08:30:00",// 工程⑨
                "1970/01/01 09:30:00");// 工程⑩

        // 工程順の工程開始・完了日時をチェックする。
        TestUtils.checkUpdateTimetablePlanDate(workflow, startDates, compDates);
    }

    /**
     * 工程順の作業時間を更新するテスト (工程⑩のタクトタイムを「01:00:00」から「01:30:00」に変更)
     *
     * @param kanban カンバン情報
     * @throws Exception 
     */
    private void testUpdateTimetable4(KanbanEntity kanban) throws Exception {
        WorkEntity work = serviceTestData.getWorks().stream().filter(p -> WORKNAME10.equals(p.getWorkName())).findFirst().get();

        work.setTaktTime(90 * 60 * 1000);// タクトタイム (ミリ秒)

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        WorkflowEntity workflow = kanban.getWorkflow();

        // 工程順の作業時間を更新する。
        workflowProcess.updateTimetable(kanban.getWorkflow(), work);

        List<String> startDates = Arrays.asList(
                "1970/01/01 00:00:00",// 工程①
                "1970/01/01 00:30:00",// 工程②
                "1970/01/01 01:30:00",// 工程③
                "1970/01/01 03:00:00",// 工程④
                "1970/01/01 04:00:00",// 工程⑤
                "1970/01/01 04:30:00",// 工程⑥
                "1970/01/01 05:30:00",// 工程⑦
                "1970/01/01 06:30:00",// 工程⑧
                "1970/01/01 07:30:00",// 工程⑨
                "1970/01/01 08:30:00");// 工程⑩

        List<String> compDates = Arrays.asList(
                "1970/01/01 00:30:00",// 工程①
                "1970/01/01 01:30:00",// 工程②
                "1970/01/01 03:00:00",// 工程③
                "1970/01/01 04:00:00",// 工程④
                "1970/01/01 04:30:00",// 工程⑤
                "1970/01/01 05:30:00",// 工程⑥
                "1970/01/01 06:30:00",// 工程⑦
                "1970/01/01 07:30:00",// 工程⑧
                "1970/01/01 08:30:00",// 工程⑨
                "1970/01/01 10:00:00");// 工程⑩

        // 工程順の工程開始・完了日時をチェックする。
        TestUtils.checkUpdateTimetablePlanDate(workflow, startDates, compDates);
    }

    /**
     * 工程の標準作業時間が設定された工程でカンバンを作成するテスト
     *
     * @throws Exception 
     */
    @Test
    public void testStandardTime() throws Exception {
        System.out.println("testStandardTime");

        serviceTestData.createTestData();

        // 休憩時間
        List<BreakTimeInfoEntity> breaktimes = serviceTestData.getBreaktimes();

        // 休日
        List<HolidayEntity> holidays = serviceTestData.getHolidays();

        OrganizationEntity parentWorker1 = serviceTestData.getOrganizations().stream().filter(p -> ServiceTestData.ORGANIZATION_IDENT_P1.equals(p.getOrganizationIdentify())).findFirst().get();
        EquipmentEntity parentEquip1 = serviceTestData.getEquipments().stream().filter(p -> ServiceTestData.EQUIPMENT_IDENT_P1.equals(p.getEquipmentIdentify())).findFirst().get();

        OrganizationEntity worker1 = serviceTestData.getOrganizations().stream().filter(p -> ServiceTestData.ORGANIZATION_IDENT_1_1.equals(p.getOrganizationIdentify())).findFirst().get();

        // 工程階層
        WorkHierarchyEntity workHierarchy = serviceTestData.getWorkHierarchies().get(0);
        // 工程順階層
        WorkflowHierarchyEntity workflowHierarchy = serviceTestData.getWorkflowHierarchies().get(0);
        // カンバン階層
        KanbanHierarchyEntity kanbanHierarchy = serviceTestData.getKanbanHierarchies().get(0);

        // 工程
        WorkEntity work1 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME1, 1, true, false);
        WorkEntity work2 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME2, 1, true, false);
        WorkEntity work3 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME3, 1, true, false);
        WorkEntity work4 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME4, 1, true, false);
        WorkEntity work5 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME5, 1, true, false);
        WorkEntity work6 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME6, 1, true, false);
        WorkEntity work7 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME7, 1, true, false);
        WorkEntity work8 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME8, 1, true, false);
        WorkEntity work9 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME9, 1, true, false);
        WorkEntity work10 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME10, 1, true, false);

        List<WorkEntity> works1 = Arrays.asList(work1, work2, work3, work4, work5, work6, work7, work8, work9, work10);

        // 工程順
        WorkflowEntity workflow1 = serviceTestData.createWorkflowInfo(workflowHierarchy.getWorkflowHierarchyId(), ServiceTestData.WORKFLOW_NAME_1, 1, ServiceTestData.MODEL_NAME_1, works1, parentWorker1.getOrganizationIdentify(), parentEquip1.getEquipmentIdentify());

        // カンバン
        String createrIdentify = worker1.getOrganizationIdentify();

        List<Date> standardStartDate = Arrays.asList(
                TestUtils.parseDatetime("1970/01/01 00:00:00"),// 工程①
                TestUtils.parseDatetime("1970/01/01 01:00:00"),// 工程②
                TestUtils.parseDatetime("1970/01/01 02:00:00"),// 工程③
                TestUtils.parseDatetime("1970/01/03 00:00:00"),// 工程④
                TestUtils.parseDatetime("1970/01/03 01:00:00"),// 工程⑤
                TestUtils.parseDatetime("1970/01/03 02:00:00"),// 工程⑥
                TestUtils.parseDatetime("1970/01/03 03:00:00"),// 工程⑦
                TestUtils.parseDatetime("1970/01/03 04:00:00"),// 工程⑧
                TestUtils.parseDatetime("1970/01/03 05:00:00"),// 工程⑨
                TestUtils.parseDatetime("1970/01/03 06:00:00"));// 工程⑩

        List<Date> standardEndDate = Arrays.asList(
                TestUtils.parseDatetime("1970/01/01 01:00:00"),// 工程①
                TestUtils.parseDatetime("1970/01/01 02:00:00"),// 工程②
                TestUtils.parseDatetime("1970/01/01 03:00:00"),// 工程③
                TestUtils.parseDatetime("1970/01/03 01:00:00"),// 工程④
                TestUtils.parseDatetime("1970/01/03 02:00:00"),// 工程⑤
                TestUtils.parseDatetime("1970/01/03 03:00:00"),// 工程⑥
                TestUtils.parseDatetime("1970/01/03 04:00:00"),// 工程⑦
                TestUtils.parseDatetime("1970/01/03 05:00:00"),// 工程⑧
                TestUtils.parseDatetime("1970/01/03 06:00:00"),// 工程⑨
                TestUtils.parseDatetime("1970/01/03 07:00:00"));// 工程⑩

        serviceTestData.updateConWorkflowWork(workflow1, standardStartDate, standardEndDate);

        // カンバン
        KanbanEntity kanban = serviceTestData.createKanban("カンバン①", workflow1.getWorkflowName(), workflow1.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);

        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(workflow1);

        // カンバンに基準時間を設定する。
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, holidays);

        List<Date> startDates = Arrays.asList(
                TestUtils.parseDatetime("2019/04/10 10:00:00"),// 工程①
                TestUtils.parseDatetime("2019/04/10 11:00:00"),// 工程②
                TestUtils.parseDatetime("2019/04/10 12:00:00"),// 工程③
                TestUtils.parseDatetime("2019/04/12 09:00:00"),// 工程④
                TestUtils.parseDatetime("2019/04/12 10:00:00"),// 工程⑤
                TestUtils.parseDatetime("2019/04/12 11:00:00"),// 工程⑥
                TestUtils.parseDatetime("2019/04/12 12:00:00"),// 工程⑦
                TestUtils.parseDatetime("2019/04/12 14:00:00"),// 工程⑧
                TestUtils.parseDatetime("2019/04/12 15:00:00"),// 工程⑨
                TestUtils.parseDatetime("2019/04/12 16:00:00"));// 工程⑩

        List<Date> compDates = Arrays.asList(
                TestUtils.parseDatetime("2019/04/10 11:00:00"),// 工程①
                TestUtils.parseDatetime("2019/04/10 12:00:00"),// 工程②
                TestUtils.parseDatetime("2019/04/10 14:00:00"),// 工程③
                TestUtils.parseDatetime("2019/04/12 10:00:00"),// 工程④
                TestUtils.parseDatetime("2019/04/12 11:00:00"),// 工程⑤
                TestUtils.parseDatetime("2019/04/12 12:00:00"),// 工程⑥
                TestUtils.parseDatetime("2019/04/12 14:00:00"),// 工程⑦
                TestUtils.parseDatetime("2019/04/12 15:00:00"),// 工程⑧
                TestUtils.parseDatetime("2019/04/12 16:00:00"),// 工程⑨
                TestUtils.parseDatetime("2019/04/12 17:00:00"));// 工程⑩

        for (int i = 0; i < kanban.getWorkKanbanCollection().size(); i++) {
            WorkKanbanEntity workKanban = kanban.getWorkKanbanCollection().get(i);
            assertThat(workKanban.getStartDatetime(), is(startDates.get(i)));
            assertThat(workKanban.getCompDatetime(), is(compDates.get(i)));
        }
    }

    /**
     * テスト用のカンバンを作成する。(工程①～⑩の直列工程)
     *
     * @return カンバン情報
     * @throws Exception 
     */
    public KanbanEntity createTestKanban() throws Exception {
        OrganizationEntity parentWorker1 = serviceTestData.getOrganizations().stream().filter(p -> ServiceTestData.ORGANIZATION_IDENT_P1.equals(p.getOrganizationIdentify())).findFirst().get();
        EquipmentEntity parentEquip1 = serviceTestData.getEquipments().stream().filter(p -> ServiceTestData.EQUIPMENT_IDENT_P1.equals(p.getEquipmentIdentify())).findFirst().get();

        OrganizationEntity worker1 = serviceTestData.getOrganizations().stream().filter(p -> ServiceTestData.ORGANIZATION_IDENT_1_1.equals(p.getOrganizationIdentify())).findFirst().get();

        // 工程階層
        WorkHierarchyEntity workHierarchy = serviceTestData.getWorkHierarchies().get(0);
        // 工程順階層
        WorkflowHierarchyEntity workflowHierarchy = serviceTestData.getWorkflowHierarchies().get(0);
        // カンバン階層
        KanbanHierarchyEntity kanbanHierarchy = serviceTestData.getKanbanHierarchies().get(0);

        // 工程
        WorkEntity work1 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME1, 1, true, false);
        WorkEntity work2 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME2, 1, true, false);
        WorkEntity work3 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME3, 1, true, false);
        WorkEntity work4 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME4, 1, true, false);
        WorkEntity work5 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME5, 1, true, false);
        WorkEntity work6 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME6, 1, true, false);
        WorkEntity work7 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME7, 1, true, false);
        WorkEntity work8 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME8, 1, true, false);
        WorkEntity work9 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME9, 1, true, false);
        WorkEntity work10 = serviceTestData.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORKNAME10, 1, true, false);

        List<WorkEntity> works1 = Arrays.asList(work1, work2, work3, work4, work5, work6, work7, work8, work9, work10);

        // 工程順
        WorkflowEntity workflow1 = serviceTestData.createWorkflowInfo(workflowHierarchy.getWorkflowHierarchyId(), ServiceTestData.WORKFLOW_NAME_1, 1, ServiceTestData.MODEL_NAME_1, works1, parentWorker1.getOrganizationIdentify(), parentEquip1.getEquipmentIdentify());

        // カンバン
        String createrIdentify = worker1.getOrganizationIdentify();
        return serviceTestData.createKanban("カンバン①", workflow1.getWorkflowName(), workflow1.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
    }

    /**
     * 休日情報を作成する。
     *
     * @param name 名前
     * @param date 日付
     * @return 休日情報
     */
    private HolidayEntity createHoliday(String name, Date date) {
        HolidayEntity holiday = new HolidayEntity();
        holiday.setHolidayName(name);
        holiday.setHolidayDate(date);
        return holiday;
    }

    /**
     * 指定した工程順のワークフロープロセスを取得する。
     *
     * @param workflow 工程順
     * @return ワークフロープロセス
     */
    private WorkflowProcess getWorkflowProcess(WorkflowEntity workflow) {
        WorkflowProcess workflowProcess = new WorkflowProcess(workflow);
        workflowProcess.setOrganizationRest(ServiceTestData.getOrganizationRest());
        return workflowProcess;
    }

    /**
     * カンバンに基準時間を設定するテスト(休日の設定なし)
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetBaseTime_HolidaysIsEmpty() throws Exception {
        System.out.println("testSetBaseTime_HolidaysIsEmpty");

        serviceTestData.createTestData();

        // テスト用のカンバンを作成する。
        KanbanEntity kanban = this.createTestKanban();

        // 休憩時間
        List<BreakTimeInfoEntity> breaktimes = serviceTestData.getBreaktimes();

        // 基準時間
        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        WorkflowProcess workflowProcess = this.getWorkflowProcess(kanban.getWorkflow());

        // カンバンに基準時間を設定する。(休日が空)
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, new ArrayList());

        // 基準時間設定後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 12:00:00",// 工程③
                "2019/04/10 14:00:00",// 工程④
                "2019/04/10 15:00:00",// 工程⑤
                "2019/04/10 16:00:00",// 工程⑥
                "2019/04/11 09:00:00",// 工程⑦
                "2019/04/11 10:00:00",// 工程⑧
                "2019/04/11 11:00:00",// 工程⑨
                "2019/04/11 12:00:00");// 工程⑩

        // 基準時間設定後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 12:00:00",// 工程②
                "2019/04/10 14:00:00",// 工程③
                "2019/04/10 15:00:00",// 工程④
                "2019/04/10 16:00:00",// 工程⑤
                "2019/04/10 17:00:00",// 工程⑥
                "2019/04/11 10:00:00",// 工程⑦
                "2019/04/11 11:00:00",// 工程⑧
                "2019/04/11 12:00:00",// 工程⑨
                "2019/04/11 14:00:00");// 工程⑩

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);

        // カンバンに基準時間を設定する。(休日がnull)
        workflowProcess.setBaseTime(kanban, breaktimes, baseTime, null);
        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }
}
