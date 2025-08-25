/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXB;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.system.SoftwareUpdateEntity;
import jp.adtekfuji.adFactory.entity.system.SystemOptionEntity;
import jp.adtekfuji.adFactory.entity.system.SystemPropEntity;
import jp.adtekfuji.adfactoryserver.entity.system.TroubleReportEntity;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;

/**
 * 間接作業情報取得用RESTのテストクラス。
 * 
 * @author z-okado
 */
public class SystemResourceTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static SystemResource rest = null;
    private static SystemOptionEntity optionEntity = null;
    private static SystemPropEntity systemPropEntity = null;
    private static List<SystemOptionEntity> optionEntitys =null;

    private Response restRes = null;
    private ResponseEntity res = null;
    private SoftwareUpdateEntity softwareUpdate = null;

    /**
     * コンストラクタ。
     */
    public SystemResourceTest(){
    }

    /**
     * 最初の設定。
     */
    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        rest = new SystemResource();
        optionEntity = new SystemOptionEntity();
    }

    /**
     * リソース解放。
     */
    @AfterClass
    public static void tearDownClass() {
        if (Objects.nonNull(em)) {
            em.close();
        }
        if (Objects.nonNull(emf)) {
            emf.close();
        }
    }

    /**
     * トランザクションを取得。
     */
    @Before
    public void setUp() {
        tx = em.getTransaction();
    }

    /**
     * DBの状態をリセットをする。
     */
    @After
    public void tearDown() {
        DatabaseControll.reset(em, tx);
    }

    /**
     * ソフトウェアアップデート取得のテスト。
     */
    @Ignore// 条件の合う設定ファイル(softwareupdate.xml)を配置した環境でのみ実行可
    @Test
    public void findSoftwareUpdateTest() throws ParseException{
        restRes = rest.findSoftwareUpdate(0L);
        softwareUpdate = JAXB.unmarshal(new StringReader(restRes.getEntity().toString()), SoftwareUpdateEntity.class);

        // 比較確認用の日付
        Date dateCorrect = new Date();
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateCorrect = sdformat.parse("2017-04-01 00:00:00");

        // C:\adFactory\deploy\softwareupdate.xml を配置しておくこと
        assertThat(restRes.getStatus(), is(200));
        assertThat(softwareUpdate.getDate(), is(dateCorrect));
        assertThat(softwareUpdate.getRetryDownload(), is(3L));
        assertThat(softwareUpdate.getPackageCollection().size(), is(5));
        assertTrue("パッケージ内容確認1", softwareUpdate.getPackageCollection().stream().filter(e -> e.getName().equals("adProduct")).allMatch(e -> e.getSee().equals("/adProductApp/version.ini")));
        assertTrue("パッケージ内容確認2", softwareUpdate.getPackageCollection().stream().filter(e -> e.getName().equals("adProductLana")).allMatch(e -> e.getParams().equals("/VERYSILENT")));
    }

    /**
     * ライセンスオプション取得(全件)のテスト。
     */
    @Ignore// 条件の合うライセンスファイルを配置した環境でのみ実行可
    @Test
    public void findLicenseOptionsTest(){
        optionEntitys = rest.findLicenseOptions(0L);

        // ライセンスファイルを配置しておくこと
        assertThat(optionEntitys.size(), is(14));
        assertTrue("値の一致確認(kanbanEditor)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@kanbanEditor")).allMatch(e -> e.getEnable().equals(true)));
        assertTrue("値の一致確認(EquipmentEditor)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@EquipmentEditor")).allMatch(e -> e.getEnable().equals(true)));
        assertTrue("値の一致確認(SystemSettingEditor)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@SystemSettingEditor")).allMatch(e -> e.getEnable().equals(true)));
        assertTrue("値の一致確認(ProductionNavi)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@ProductionNavi")).allMatch(e -> e.getEnable().equals(true)));
        assertTrue("値の一致確認(MonitorSettingEditor)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@MonitorSettingEditor")).allMatch(e -> e.getEnable().equals(true)));
        assertTrue("値の一致確認(LineManaged)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@LineManaged")).allMatch(e -> e.getEnable().equals(true)));
        assertTrue("値の一致確認(OrganizationEditor)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@OrganizationEditor")).allMatch(e -> e.getEnable().equals(true)));
        assertTrue("値の一致確認(CsvReportOut)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@CsvReportOut")).allMatch(e -> e.getEnable().equals(true)));
        assertTrue("値の一致確認(DocumentViewer)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@DocumentViewer")).allMatch(e -> e.getEnable().equals(true)));
        assertTrue("値の一致確認(LineTimer)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@LineTimer")).allMatch(e -> e.getEnable().equals(true)));
        assertTrue("値の一致確認(Traceability)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@Traceability")).allMatch(e -> e.getEnable().equals(true)));
        assertTrue("値の一致確認(WorkflowEditor)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@WorkflowEditor")).allMatch(e -> e.getEnable().equals(true)));
        assertTrue("値の一致確認(Warehouse)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@Warehouse")).allMatch(e -> e.getEnable().equals(false)));
        assertTrue("値の一致確認(Scheduling)", optionEntitys.stream().filter(e -> e.getOptionName().equals("@Scheduling")).allMatch(e -> e.getEnable().equals(true)));
    }

    /**
     * ライセンスオプション取得のテスト。
     */
    @Ignore// 条件の合うライセンスファイルを配置した環境でのみ実行可
    @Test
    public void findLicenseOptionTest(){
        optionEntity = rest.findLicenseOption("@KanbanEditor", 0L);
        SystemOptionEntity optionEntityCorrect = new SystemOptionEntity("@KanbanEditor", true);

        assertTrue(optionEntity.equals(optionEntityCorrect));
        assertThat(optionEntity.getEnable(), is(true));
    }

    /**
     * プロパティ取得(全件)のテスト。
     */
    @Ignore// 条件の合う設定ファイル(adFactory.properties)を配置した環境でのみ実行可
    @Test
    public void findSystemPropertiesTest(){
        List<SystemPropEntity> systemPropEntitys = rest.findSystemProperties(0L);

        // c:\adFactory\conf\adFactory.propertiesを配置しておくこと

        assertThat(systemPropEntitys.size(), is(3));
        assertTrue("値の一致", systemPropEntitys.stream().filter(e -> e.getKey().equals("adInterfaceServiceAddress")).allMatch(e -> e.getValue().equals("127.0.0.1")));
        assertTrue("値の一致", systemPropEntitys.stream().filter(e -> e.getKey().equals("adInterfaceServicePortNum")).allMatch(e -> e.getValue().equals("18005")));
        assertTrue("値の一致", systemPropEntitys.stream().filter(e -> e.getKey().equals("adFactoryCrypt")).allMatch(e -> e.getValue().equals("true")));
    }

    /**
     * プロパティ取得のテスト。
     */
    @Ignore// 条件の合う設定ファイル(adFactory.properties)を配置した環境でのみ実行可
    @Test
    public void findSystemPropertyTest(){
        String propartyValue = rest.findSystemProperty("adInterfaceServicePortNum", 0L);
        
        // c:\adFactory\conf\adFactory.propertiesを配置しておくこと
        assertThat(propartyValue, is("18005"));
    }

    /**
     * 障害レポート送信のテスト。
     */
    @Ignore// TODO: 障害レポート送信はメールサーバー等の考慮が必要なため無効化
    @Test
    public void sendTroubleReportTest(){
        TroubleReportEntity report = new TroubleReportEntity();
        restRes = rest.sendTroubleReport(report, 0L);

        assertThat(restRes.getStatus(), is(200));
    }
}
