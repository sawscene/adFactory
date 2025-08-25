/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.worktimereporter;

import adtekfuji.clientservice.BreaktimeInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import adtekfuji.property.AdProperty;
import com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.actual.ActualPropertyEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition;
import jp.adtekfuji.adFactory.entity.view.ReportOutInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.worktimereporter.service.ActualResultInfoFacade;
import static org.hamcrest.CoreMatchers.is;

import org.apache.commons.lang.StringUtils;
import org.junit.*;

import static org.junit.Assert.*;

import org.junit.rules.TemporaryFolder;

import static org.mockito.Mockito.*;
import org.mockito.ArgumentMatchers;

/**
 * OutputActualFacadeのユニットテスト
 *
 * @author ke.yokoi
 */
public class OutputActualFacadeTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private OutputActualInfo mockInfo;
    private ActualResultInfoFacade mockActualResultInfoFacade;
    private BreaktimeInfoFacade mockBreaktimeInfoFacade;
    private OrganizationInfoFacade mockOrganizationInfoFacade;
    private OutputActualFacade facade;

    public OutputActualFacadeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        // テスト用のtempフォルダを作成
        File tempDir = tempFolder.newFolder("temp");


        String filename = "adFactory.properties";
        File propFile = new File(getClass().getClassLoader().getResource(filename).getFile());
        AdProperty.rebasePath(propFile.getParent());
        AdProperty.load(filename);

        // モックの設定
        mockInfo = mock(OutputActualInfo.class);
        when(mockInfo.getAdFactoryAddress()).thenReturn("http://localhost:8080");
        when(mockInfo.getUptakeInterval()).thenReturn(24);
        when(mockInfo.getFolderAddress()).thenReturn(tempFolder.getRoot().getAbsolutePath());
        when(mockInfo.getReadFileNameAddress()).thenReturn(tempFolder.getRoot().getAbsolutePath());
        when(mockInfo.getUserName()).thenReturn("user");
        when(mockInfo.getPassword()).thenReturn("password");

        // ActualResultInfoFacadeのモック
        mockActualResultInfoFacade = mock(ActualResultInfoFacade.class);

        // BreaktimeInfoFacadeのモック
        mockBreaktimeInfoFacade = mock(BreaktimeInfoFacade.class);

        // OrganizationInfoFacadeのモック
        mockOrganizationInfoFacade = mock(OrganizationInfoFacade.class);

        // テスト対象のインスタンス作成
        facade = new OutputActualFacade(mockInfo);
    }

    @After
    public void tearDown() {
    }

    /**
     * Math.ceilを使用した時間計算のテスト
     */
    @Test
    public void testMath() throws Exception {
        System.out.println("testMath");

        int time = 1;    //1msec
        int out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 1000;    //1sec
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 10000;    //10sec
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 59999;
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 60000;
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 60001;
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(2));
    }

    /**
     * Math.roundを使用した時間計算のテスト
     */
    @Test
    public void testRound() throws Exception {
        System.out.println("testRound");

        int time = 29999;    //29.999sec
        int out = (int) Math.round((double) time / 60.0 / 1000.0);
        assertThat(out, is(0));

        time = 30000;    //30.000sec
        out = (int) Math.round((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 30001;    //30.001sec
        out = (int) Math.round((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));
    }

    /**
     * millisecondsToMinutesメソッドのテスト
     */
    @Test
    public void testMillisecondsToMinutes() {
        System.out.println("testMillisecondsToMinutes");

        // テスト対象のインスタンス作成
        OutputActualFacade instance = new OutputActualFacade(mockInfo);

        // 0ミリ秒は0分
        double value = 0;
        int expResult = 0;
        int result = instance.millisecondsToMinutes(value);
        assertEquals(expResult, result);

        // 59999ミリ秒（59.999秒）は0分
        value = 59999;
        expResult = 0;
        result = instance.millisecondsToMinutes(value);
        assertEquals(expResult, result);

        // 60000ミリ秒（60秒）は1分
        value = 60000;
        expResult = 1;
        result = instance.millisecondsToMinutes(value);
        assertEquals(expResult, result);

        // 120000ミリ秒（120秒）は2分
        value = 120000;
        expResult = 2;
        result = instance.millisecondsToMinutes(value);
        assertEquals(expResult, result);
    }

    /**
     * groupActualsメソッドのテスト
     * このメソッドは指図番号、社員番号、ワークセンターでグループ化する
     */
    @Test
    public void testGroupActuals() {
        System.out.println("testGroupActuals");

        // テストデータの作成
        List<ReportOutInfoEntity> actuals = new ArrayList<>();

        // グループA(worker1 - order1 - center1)
        ReportOutInfoEntity actual1 = new ReportOutInfoEntity();
        actual1.setOrganizationIdentName("worker1");
        actual1.setWorkingTime(60000);
        List<ActualPropertyEntity> actualProperties1 = new ArrayList<>();
        actualProperties1.add(new ActualPropertyEntity("指図番号", CustomPropertyTypeEnum.TYPE_STRING, "order1", 0));
        actualProperties1.add(new ActualPropertyEntity("ワークセンター", CustomPropertyTypeEnum.TYPE_STRING, "center1", 0));
        actual1.setPropertyCollection(actualProperties1);
        actuals.add(actual1);

        // グループA(worker1 - order1 - center1)
        ReportOutInfoEntity actual2 = new ReportOutInfoEntity();
        actual2.setOrganizationIdentName("worker1");
        actual2.setWorkingTime(60000);
        List<ActualPropertyEntity> actualProperties2 = new ArrayList<>();
        actualProperties2.add(new ActualPropertyEntity("指図番号", CustomPropertyTypeEnum.TYPE_STRING, "order1", 0));
        actualProperties2.add(new ActualPropertyEntity("ワークセンター", CustomPropertyTypeEnum.TYPE_STRING, "center1", 0));
        actual2.setPropertyCollection(actualProperties2);
        actuals.add(actual2);

        // グループB(worker2 - order1 - center1)
        ReportOutInfoEntity actual3 = new ReportOutInfoEntity();
        actual3.setOrganizationIdentName("worker2");
        List<ActualPropertyEntity> actualProperties3 = new ArrayList<>();
        actualProperties3.add(new ActualPropertyEntity("指図番号", CustomPropertyTypeEnum.TYPE_STRING, "order1", 0));
        actualProperties3.add(new ActualPropertyEntity("ワークセンター", CustomPropertyTypeEnum.TYPE_STRING, "center1", 0));
        actual3.setPropertyCollection(actualProperties3);
        actual3.setWorkingTime(60000);
        actuals.add(actual3);

        // グループC(worker1 - order2 - center1)
        ReportOutInfoEntity actual4 = new ReportOutInfoEntity();
        actual4.setOrganizationIdentName("worker1");
        actual4.setWorkingTime(180000);
        List<ActualPropertyEntity> actualProperties4 = new ArrayList<>();
        actualProperties4.add(new ActualPropertyEntity("指図番号", CustomPropertyTypeEnum.TYPE_STRING, "order2", 0));
        actualProperties4.add(new ActualPropertyEntity("ワークセンター", CustomPropertyTypeEnum.TYPE_STRING, "center1", 0));
        actual4.setPropertyCollection(actualProperties4);
        actuals.add(actual4);

        // グループD(worker1 - order1 - center2)
        ReportOutInfoEntity actual5 = new ReportOutInfoEntity();
        actual5.setOrganizationIdentName("worker1");
        actual5.setWorkingTime(240000);
        List<ActualPropertyEntity> actualProperties5 = new ArrayList<>();
        actualProperties5.add(new ActualPropertyEntity("指図番号", CustomPropertyTypeEnum.TYPE_STRING, "order1", 0));
        actualProperties5.add(new ActualPropertyEntity("ワークセンター", CustomPropertyTypeEnum.TYPE_STRING, "center2", 0));
        actual5.setPropertyCollection(actualProperties5);
        actuals.add(actual5);

        // グループE(worker2 - order2 - center1)
        ReportOutInfoEntity actual6 = new ReportOutInfoEntity();
        actual6.setOrganizationIdentName("worker2");
        actual6.setWorkingTime(300000);
        List<ActualPropertyEntity> actualProperties6 = new ArrayList<>();
        actualProperties6.add(new ActualPropertyEntity("指図番号", CustomPropertyTypeEnum.TYPE_STRING, "order2", 0));
        actualProperties6.add(new ActualPropertyEntity("ワークセンター", CustomPropertyTypeEnum.TYPE_STRING, "center1", 0));
        actual6.setPropertyCollection(actualProperties6);
        actuals.add(actual6);

        // グループF(worker2 - order1 - center2)
        ReportOutInfoEntity actual9 = new ReportOutInfoEntity();
        actual9.setOrganizationIdentName("worker2");
        actual9.setWorkingTime(360000);
        List<ActualPropertyEntity> actualProperties9 = new ArrayList<>();
        actualProperties9.add(new ActualPropertyEntity("指図番号", CustomPropertyTypeEnum.TYPE_STRING, "order1", 0));
        actualProperties9.add(new ActualPropertyEntity("ワークセンター", CustomPropertyTypeEnum.TYPE_STRING, "center2", 0));
        actual9.setPropertyCollection(actualProperties9);
        actuals.add(actual9);

        // グループF(worker1 - order2 - center2)
        ReportOutInfoEntity actual7 = new ReportOutInfoEntity();
        actual7.setOrganizationIdentName("worker1");
        actual7.setWorkingTime(420000);
        List<ActualPropertyEntity> actualProperties7 = new ArrayList<>();
        actualProperties7.add(new ActualPropertyEntity("指図番号", CustomPropertyTypeEnum.TYPE_STRING, "order2", 0));
        actualProperties7.add(new ActualPropertyEntity("ワークセンター", CustomPropertyTypeEnum.TYPE_STRING, "center2", 0));
        actual7.setPropertyCollection(actualProperties7);
        actuals.add(actual7);

        // グループG(worker2 - order2 - center2)
        ReportOutInfoEntity actual8 = new ReportOutInfoEntity();
        actual8.setOrganizationIdentName("worker2");
        actual8.setWorkingTime(480000);
        List<ActualPropertyEntity> actualProperties8 = new ArrayList<>();
        actualProperties8.add(new ActualPropertyEntity("指図番号", CustomPropertyTypeEnum.TYPE_STRING, "order2", 0));
        actualProperties8.add(new ActualPropertyEntity("ワークセンター", CustomPropertyTypeEnum.TYPE_STRING, "center2", 0));
        actual8.setPropertyCollection(actualProperties8);
        actuals.add(actual8);



        // テスト実行
        List<ReportOutInfoEntity> result = facade.groupActuals(actuals);

        // 検証
        assertEquals(8, result.size()); // 2つのグループになるはず

        // グループ化された実績の作業時間を検証
        for (ReportOutInfoEntity entity : result) {
            String key = entity.getOrganizationIdentName() + ","
                    + entity.getPropertyValue(OutputActualFacade.ORDER_NUMBER).get() + ","
                    + entity.getPropertyValue(OutputActualFacade.WORK_CENTER).get();
            Integer workingTime = entity.getWorkingTime();

            switch (key) {
                case "worker1,order1,center1" ->
                        assertEquals(Integer.valueOf(120000), workingTime);
                case "worker2,order1,center1" ->
                        assertEquals(Integer.valueOf(60000), workingTime);
                case "worker1,order2,center1" ->
                        assertEquals(Integer.valueOf(180000), workingTime);
                case "worker1,order1,center2" ->
                        assertEquals(Integer.valueOf(240000), workingTime);
                case "worker2,order2,center1" ->
                        assertEquals(Integer.valueOf(300000), workingTime);
                case "worker2,order1,center2" ->
                        assertEquals(Integer.valueOf(360000), workingTime);
                case "worker1,order2,center2" ->
                        assertEquals(Integer.valueOf(420000), workingTime);
                case "worker2,order2,center2" ->
                        assertEquals(Integer.valueOf(480000), workingTime);
                default -> fail("Unexpected organization: " + entity.getOrganizationIdentName());
            }
        }
    }

    /**
     * removeTimeWorkingZeroMinuteメソッドのテスト
     * このメソッドは工数が0分のデータを削除する
     */
    @Test
    public void testRemoveTimeWorkingZeroMinute() {
        System.out.println("testRemoveTimeWorkingZeroMinute");

        // テストデータの作成
        List<ReportOutInfoEntity> actuals = new ArrayList<>();

        // 工数が0分の実績
        ReportOutInfoEntity actual1 = new ReportOutInfoEntity();
        actual1.setWorkingTime(0);
        actuals.add(actual1);

        // 工数が1分の実績
        ReportOutInfoEntity actual2 = new ReportOutInfoEntity();
        actual2.setWorkingTime(60000); // 1分
        actuals.add(actual2);

        // 工数が2分の実績
        ReportOutInfoEntity actual3 = new ReportOutInfoEntity();
        actual3.setWorkingTime(120000); // 2分
        actuals.add(actual3);

        // テスト実行
        List<ReportOutInfoEntity> result = facade.removeTimeWorkingZeroMinute(actuals);

        // 検証
        assertEquals(2, result.size()); // 工数が0分の実績は削除されるはず

        // 残った実績の工数を検証
        for (ReportOutInfoEntity entity : result) {
            assertNotEquals(Integer.valueOf(0), entity.getWorkingTime()); // 工数が0分の実績はないはず
        }
    }

    /**
     * isDirectActualメソッドのテスト
     * このメソッドは直接工数実績か判定する
     */
    @Test
    public void testIsDirectActual() throws Exception {
        System.out.println("testIsDirectActual");

        // privateメソッドをテストするためにリフレクションを使用
        java.lang.reflect.Method method = OutputActualFacade.class.getDeclaredMethod("isDirectActual", ReportOutInfoEntity.class);
        method.setAccessible(true);

        // actualIdがnullの場合はfalse
        ReportOutInfoEntity actual1 = new ReportOutInfoEntity();
        actual1.setActualId(null);
        boolean result1 = (boolean) method.invoke(facade, actual1);
        assertFalse(result1);

        // actualIdが設定されている場合はtrue
        ReportOutInfoEntity actual2 = new ReportOutInfoEntity();
        actual2.setActualId(1L);
        boolean result2 = (boolean) method.invoke(facade, actual2);
        assertTrue(result2);
    }

    /**
     * テスト用のReportOutInfoEntityを作成するヘルパーメソッド
     */
    long now = System.currentTimeMillis();
    private ReportOutInfoEntity createTestEntity(
            String workerName,
            long minutesAgo,
            KanbanStatusEnum status,
            Long actualId,
            Long workKanbanId,
            String kousuCollab,
            String order,
            String center) {
        ReportOutInfoEntity entity = new ReportOutInfoEntity();
        entity.setOrganizationIdentName(workerName);
        entity.setWorkingTime(0);
        entity.setImplementDatetime(new java.util.Date(now - minutesAgo * 60000));
        entity.setActualStatus(status);
        entity.setActualId(actualId);
        entity.setFkWorkKanbanwId(workKanbanId);

        List<ActualPropertyEntity> propertyList = new ArrayList<>();
        propertyList.add(new ActualPropertyEntity(OutputActualFacade.KOUSU_COLLAB, CustomPropertyTypeEnum.TYPE_STRING, kousuCollab, 0));
        propertyList.add(new ActualPropertyEntity(OutputActualFacade.WORK_CENTER, CustomPropertyTypeEnum.TYPE_STRING, center, 0));
        propertyList.add(new ActualPropertyEntity(OutputActualFacade.ORDER_NUMBER, CustomPropertyTypeEnum.TYPE_STRING, order, 0));
        entity.setPropertyCollection(propertyList);

        return entity;
    }

    /**
     * 作業時間の検証を行うヘルパーメソッド
     */
    private void verifyWorkingTime(ReportOutInfoEntity entity, String workerName, Long kanbanId, Long actualId, double expectedMinutes) {
        if (entity.getOrganizationIdentName().equals(workerName)) {
            if (kanbanId != null && entity.getFkWorkKanbanId() == kanbanId) {
                assertEquals(expectedMinutes * 60000, entity.getWorkingTime().doubleValue(),0.1);
            } else if (actualId != null && entity.getActualId() == actualId) {
                assertEquals(expectedMinutes * 60000, entity.getWorkingTime().doubleValue(),0.1);
            }
        }
    }

    /**
     * deleteOverlapTimeメソッドのテスト
     * このメソッドは重複する時間を削除する
     */
    @Test
    public void testDeleteOverlapTime() throws Exception {
        System.out.println("testDeleteOverlapTime");

        // privateメソッドをテストするためにリフレクションを使用
        java.lang.reflect.Method method = OutputActualFacade.class.getDeclaredMethod("deleteOverlapTime", List.class);
        method.setAccessible(true);

        // モックの設定
        OrganizationInfoFacade mockOrgFacade = mock(OrganizationInfoFacade.class);

        BreaktimeInfoFacade mockBreakFacade = mock(BreaktimeInfoFacade.class);


        // リフレクションでモックをセット
        java.lang.reflect.Field orgField = facade.getClass().getDeclaredField("organizationFacade");
        orgField.setAccessible(true);
        orgField.set(facade, mockOrgFacade);

        java.lang.reflect.Field breakField = facade.getClass().getDeclaredField("breaktimeFacade");
        breakField.setAccessible(true);
        breakField.set(facade, mockBreakFacade);

        // テストデータの作成
        List<ReportOutInfoEntity> actuals = new ArrayList<>();

        // 作業者1の実績データ (開始と完了の重複)
        actuals.add(createTestEntity("worker1", 60, KanbanStatusEnum.WORKING, 1L, 101L, "","",""));     // 1時間前
        actuals.add(createTestEntity("worker1", 30, KanbanStatusEnum.COMPLETION, 2L, 101L, "","",""));  // 30分前
        actuals.add(createTestEntity("worker1", 50, KanbanStatusEnum.WORKING, 3L, 102L, "","",""));     // 50分前
        actuals.add(createTestEntity("worker1", 25, KanbanStatusEnum.COMPLETION, 4L, 102L, "","",""));  // 25分前
        when(mockOrgFacade.findName("worker1")).thenReturn(new OrganizationInfoEntity());

        // 作業者2の実績データ（重複なし）
        actuals.add(createTestEntity("worker2", 60, KanbanStatusEnum.WORKING, 5L, 201L, "","",""));     // 1時間前
        actuals.add(createTestEntity("worker2", 30, KanbanStatusEnum.COMPLETION, 6L, 201L, "","",""));  // 30分前
        when(mockOrgFacade.findName("worker2")).thenReturn(new OrganizationInfoEntity());

        // 作業者3の実績データ（休憩あり）
        actuals.add(createTestEntity("worker3", 60, KanbanStatusEnum.WORKING, 7L, 301L, "","",""));     // 1時間前
        actuals.add(createTestEntity("worker3", 50, KanbanStatusEnum.SUSPEND, 8L, 301L, "","",""));     // 50分前
        actuals.add(createTestEntity("worker3", 40, KanbanStatusEnum.WORKING, 9L, 301L, "","",""));     // 40分前
        actuals.add(createTestEntity("worker3", 10, KanbanStatusEnum.COMPLETION, 10L, 301L, "","","")); // 10分前
        when(mockOrgFacade.findName("worker3")).thenReturn(new OrganizationInfoEntity());

        // 作業者4の実績データ（内側に重複）
        actuals.add(createTestEntity("worker4", 60, KanbanStatusEnum.WORKING, 7L, 401L, "","",""));     // 1時間前
        actuals.add(createTestEntity("worker4", 10, KanbanStatusEnum.COMPLETION, 8L, 401L, "","",""));  // 10分前
        actuals.add(createTestEntity("worker4", 40, KanbanStatusEnum.WORKING, 9L, 402L, "","",""));     // 40分前
        actuals.add(createTestEntity("worker4", 10, KanbanStatusEnum.COMPLETION, 10L, 402L, "","","")); // 10分前
        when(mockOrgFacade.findName("worker4")).thenReturn(new OrganizationInfoEntity());

        // 作業者5の実績データ（中断に重複）
        actuals.add(createTestEntity("worker5", 60, KanbanStatusEnum.WORKING, 7L, 401L, "","",""));     // 1時間前
        actuals.add(createTestEntity("worker5", 50, KanbanStatusEnum.SUSPEND, 8L, 401L, "","",""));  // 10分前
        actuals.add(createTestEntity("worker5", 20, KanbanStatusEnum.WORKING, 9L, 401L, "","",""));     // 1時間前
        actuals.add(createTestEntity("worker5", 10, KanbanStatusEnum.COMPLETION, 10L, 401L, "","",""));  // 10分前

        actuals.add(createTestEntity("worker5", 60, KanbanStatusEnum.WORKING, 11L, 402L, "","",""));     // 40分前
        actuals.add(createTestEntity("worker5", 10, KanbanStatusEnum.COMPLETION, 12L, 402L, "","","")); // 10分前
        when(mockOrgFacade.findName("worker5")).thenReturn(new OrganizationInfoEntity());

        // 作業者6の実績データ（中断に重複）
        actuals.add(createTestEntity("worker6", 60, KanbanStatusEnum.WORKING, 7L, 401L, "","",""));     // 1時間前
        actuals.add(createTestEntity("worker6", 50, KanbanStatusEnum.SUSPEND, 8L, 401L, "","",""));  // 10分前
        actuals.add(createTestEntity("worker6", 20, KanbanStatusEnum.WORKING, 9L, 401L, "","",""));     // 1時間前
        actuals.add(createTestEntity("worker6", 10, KanbanStatusEnum.COMPLETION, 10L, 401L, "","",""));  // 10分前

        actuals.add(createTestEntity("worker6", 50, KanbanStatusEnum.WORKING, 11L, 402L, "","",""));     // 40分前
        actuals.add(createTestEntity("worker6", 20, KanbanStatusEnum.COMPLETION, 12L, 402L, "","","")); // 10分前
        when(mockOrgFacade.findName("worker6")).thenReturn(new OrganizationInfoEntity());

        // 作業者7
        actuals.add(createTestEntity("worker7", 60, KanbanStatusEnum.WORKING, 1L, 101L, "","",""));     // 1時間前
        actuals.add(createTestEntity("worker7", 30, KanbanStatusEnum.COMPLETION, 2L, 101L, "","",""));  // 30分前
        actuals.add(createTestEntity("worker7", 50, KanbanStatusEnum.WORKING, 3L, 102L, "","",""));     // 50分前
        actuals.add(createTestEntity("worker7", 25, KanbanStatusEnum.COMPLETION, 4L, 102L, "","",""));  // 25分前

        OrganizationInfoEntity org = new OrganizationInfoEntity();
        org.setBreakTimeInfoCollection(Arrays.asList(1L,2L));

        when(mockBreakFacade.find(1)).thenReturn(new BreakTimeInfoEntity("1", new java.util.Date(now - 55 * 60000), new java.util.Date(now - 45 * 60000)));
        when(mockBreakFacade.find(2)).thenReturn(new BreakTimeInfoEntity("2", new java.util.Date(now - 40 * 60000), new java.util.Date(now - 20 * 60000)));

        when(mockOrgFacade.findName("worker7")).thenReturn(org);

        // テスト実行
        @SuppressWarnings("unchecked")
        List<ReportOutInfoEntity> result = (List<ReportOutInfoEntity>) method.invoke(facade, actuals);

        // 検証
        assertNotNull(result);

        // 作業時間が計算されていることを確認
        for (ReportOutInfoEntity entity : result) {
            if (entity.getActualStatus() == KanbanStatusEnum.WORKING) {
                // 作業開始実績には作業時間が設定されているはず
                assertNotNull(entity.getWorkingTime());

                // 作業者1の重複時間の検証
                if (entity.getOrganizationIdentName().equals("worker1")) {
                    if (entity.getFkWorkKanbanId() == 101L) {
                        // 最初のカンバンは1時間から50分までの10分間は単独、50分から30分までの20分間は重複（半分の10分）
                        // 合計20分の作業時間になるはず
                        verifyWorkingTime(entity, "worker1", 101L, null, 20);
                    } else if (entity.getFkWorkKanbanId() == 102L) {
                        // 2つ目のカンバンは50分から30分までの20分間は重複（半分の10分）、30分から25分までの5分間は単独
                        // 合計15分の作業時間になるはず
                        verifyWorkingTime(entity, "worker1", 102L, null, 15);
                    }
                }
                // 作業者2の作業時間の検証
                else if (entity.getOrganizationIdentName().equals("worker2")) {
                    // 重複なしなので1時間から30分までの30分間の作業時間になるはず
                    verifyWorkingTime(entity, "worker2", 201L, null, 30);
                }
                // 作業者3の作業時間検証
                else if (entity.getOrganizationIdentName().equals("worker3")) {
                    if (entity.getActualId() == 7L) {
                        verifyWorkingTime(entity, "worker3", null, 7L, 10);
                    } else if (entity.getActualId() == 9L) {
                        verifyWorkingTime(entity, "worker3", null, 9L, 30);
                    }
                }
                // 作業者4の重複時間の検証
                else if (entity.getOrganizationIdentName().equals("worker4")) {
                    if (entity.getFkWorkKanbanId() == 401L) {
                        verifyWorkingTime(entity, "worker4", 401L, null, 35);
                    } else if (entity.getFkWorkKanbanId() == 402L) {
                        verifyWorkingTime(entity, "worker4", 402L, null, 15);
                    }
                }
                else if (entity.getOrganizationIdentName().equals("worker5")) {
                    if (entity.getActualId() == 7L) {
                        verifyWorkingTime(entity, "worker5", null, 7L, 5);
                    } else if (entity.getActualId() == 9L) {
                        verifyWorkingTime(entity, "worker5", null, 9L, 5);
                    } else if (entity.getActualId() == 11L) {
                        verifyWorkingTime(entity, "worker5", null, 11L, 40);
                    }
                }
                else if (entity.getOrganizationIdentName().equals("worker6")) {
                    if (entity.getActualId() == 7L) {
                        verifyWorkingTime(entity, "worker6", null, 7L, 10);
                    } else if (entity.getActualId() == 9L) {
                        verifyWorkingTime(entity, "worker6", null, 9L, 10);
                    } else if (entity.getActualId() == 11L) {
                        verifyWorkingTime(entity, "worker6", null, 11L, 30);
                    }
                }
                if (entity.getOrganizationIdentName().equals("worker7")) {
                    if (entity.getActualId() == 1L) {
                        verifyWorkingTime(entity, "worker7", null, 1L, 7.5);
                    } else if (entity.getActualId() == 3L) {
                        verifyWorkingTime(entity, "worker7", null, 3L, 2.5);
                    }
                }
            } else if (entity.getActualStatus() == KanbanStatusEnum.COMPLETION) {
                // 完了実績の作業時間は0になるはず
                assertEquals(0, entity.getWorkingTime().intValue());
            }
        }
    }

    /**
     * outputメソッドのテスト
     * このメソッドは工数実績ファイルを作成して、共有フォルダーにアップロードする
     */
    @Test
    public void testOutput() throws Exception {
        System.out.println("testOutput");


        // OutputActualFacadeのテスト用インスタンスを作成
        OutputActualFacade testFacade = new OutputActualFacade(mockInfo);


        // モックの設定
        OrganizationInfoFacade mockOrgFacade = mock(OrganizationInfoFacade.class);

        BreaktimeInfoFacade mockBreakFacade = mock(BreaktimeInfoFacade.class);


        // リフレクションでモックをセット
        java.lang.reflect.Field orgField = testFacade.getClass().getDeclaredField("organizationFacade");
        orgField.setAccessible(true);
        orgField.set(testFacade, mockOrgFacade);

        java.lang.reflect.Field breakField = testFacade.getClass().getDeclaredField("breaktimeFacade");
        breakField.setAccessible(true);
        breakField.set(testFacade, mockBreakFacade);

        // 実績データ1
        // テストデータの作成
        List<ReportOutInfoEntity> actuals = new ArrayList<>();

        // 作業者1の実績データ (開始と完了の重複)
        actuals.add(createTestEntity("worker1", 60, KanbanStatusEnum.WORKING, 1L, 101L, "YES", "order1","center1"));     // 1時間前
        actuals.add(createTestEntity("worker1", 30, KanbanStatusEnum.COMPLETION, 2L, 101L, "YES","order1","center1"));  // 30分前
        actuals.add(createTestEntity("worker1", 50, KanbanStatusEnum.WORKING, 3L, 102L, "YES","order1","center1"));     // 50分前
        actuals.add(createTestEntity("worker1", 25, KanbanStatusEnum.COMPLETION, 4L, 102L, "YES","order1","center1"));  // 25分前

        actuals.add(createTestEntity("worker1", 60, KanbanStatusEnum.WORKING, 1L, 103L, "YES", "order1","center2"));     // 1時間前
        actuals.add(createTestEntity("worker1", 30, KanbanStatusEnum.COMPLETION, 2L, 103L, "YES","order1","center2"));  // 30分前
        actuals.add(createTestEntity("worker1", 50, KanbanStatusEnum.WORKING, 3L, 104L, "YES","order1","center2"));     // 50分前
        actuals.add(createTestEntity("worker1", 25, KanbanStatusEnum.COMPLETION, 4L, 104L, "YES","order1","center2"));  // 25分前

        when(mockOrgFacade.findName("worker1")).thenReturn(new OrganizationInfoEntity());



        // ActualResultInfoFacadeのモックを設定
        ActualResultInfoFacade mockActualFacade = mock(ActualResultInfoFacade.class);
        when(mockActualFacade.reportOutSearch(ArgumentMatchers.any(ReportOutSearchCondition.class))).thenReturn(actuals);

        // ActualResultInfoFacadeのモックをセットアップ
        java.lang.reflect.Field facadeField = testFacade.getClass().getDeclaredField("actualResultInfoFacade");
        facadeField.setAccessible(true);
        facadeField.set(testFacade, mockActualFacade);

        // テスト実行
        int result = testFacade.output(OutputActualFacade.SEARCH_TYPE.INTERVAL_TIME);

        // 検証
        // 2つのファイルが作成されるはず
        assertEquals(2, result);

        // tempフォルダにファイルが作成されていないことを確認（アップロード後に削除されるため）
        File tempDir = new File(tempFolder.getRoot(), "temp");
        File[] files = tempDir.listFiles((dir, name) -> name.endsWith(".csv"));
        assertEquals(0, files.length);

        // 出力先フォルダにファイルが作成されていることを確認
        File[] outputFiles = tempFolder.getRoot().listFiles((dir, name) -> name.endsWith(".txt"));
        assertEquals(1, outputFiles.length);

        // 出力ファイル名のチェック
        String outputFileName = outputFiles[0].getName();
        assertTrue("ファイル名が正しい形式ではありません: " + outputFileName, 
                outputFileName.startsWith(OutputActualFacade.READ_FILE_NAME_LIST_FILE_PREFIX) && outputFileName.endsWith(".txt"));

        // 出力ファイルの内容をチェック
        java.nio.file.Path filePath = outputFiles[0].toPath();
        List<String> lines = java.nio.file.Files.readAllLines(filePath, java.nio.charset.StandardCharsets.UTF_8);

        // 2つのワークセンターのファイル名が含まれていることを確認
        assertEquals("ファイルに2つのエントリが含まれていません", 2, lines.size());

        // 各行がワークセンター名で始まることを確認
        boolean hasCenter1 = false;
        boolean hasCenter2 = false;

        for (String line : lines) {
            String[] parts = line.split(",");
            assertEquals("ファイルの内容が正しい形式ではありません: " + line, 2, parts.length);

            String fileName = parts[0];
            if (fileName.startsWith("center1_")) {
                hasCenter1 = true;
            } else if (fileName.startsWith("center2_")) {
                hasCenter2 = true;
            }
        }

        assertTrue("center1のファイルが見つかりません", hasCenter1);
        assertTrue("center2のファイルが見つかりません", hasCenter2);

        // inフォルダに入るべきデータがあるか?
        File[] center1_resultFiles = tempFolder.getRoot().listFiles((dir, name) -> name.startsWith("center1") && name.endsWith(".csv"));
        assertEquals(1, center1_resultFiles.length);
        List<String> center1_lines = java.nio.file.Files.readAllLines(center1_resultFiles[0].toPath());
        Assert.assertEquals("ライン数が1", center1_lines.size(), 1);
        {
            String[] cells = StringUtils.split(center1_lines.getFirst(), ',');
            Assert.assertEquals("指図番号が正しい事", "order1", cells[0]);
            Assert.assertEquals("作業者が正しい事", "worker10101", cells[1]);
            Assert.assertEquals("工数が正しい事", "17", cells[3]);
            Assert.assertEquals("作業区が正しい事", "center1", cells[4]);
        }

        File[] center2_resultFiles = tempFolder.getRoot().listFiles((dir, name) -> name.startsWith("center2") && name.endsWith(".csv"));
        assertEquals(1, center2_resultFiles.length);
        List<String> center2_lines = java.nio.file.Files.readAllLines(center2_resultFiles[0].toPath());
        Assert.assertEquals("ライン数が1", center2_lines.size(), 1);
        {
            String[] cells = StringUtils.split(center1_lines.getFirst(), ',');
            Assert.assertEquals("指図番号が正しい事", "order1", cells[0]);
            Assert.assertEquals("作業者が正しい事", "worker10101", cells[1]);
            Assert.assertEquals("工数が正しい事", "17", cells[3]);
            Assert.assertEquals("作業区が正しい事", "center1", cells[4]);
        }
    }
}
