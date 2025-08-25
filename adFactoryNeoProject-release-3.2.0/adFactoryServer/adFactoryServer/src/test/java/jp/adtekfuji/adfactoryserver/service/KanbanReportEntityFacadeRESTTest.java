/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ReportTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanReportEntity;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * カンバン帳票情報RESTのテスト
 *
 * @author nar-nakamura
 */
public class KanbanReportEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;

    private static KanbanReportEntityFacadeREST rest = null;

    private static KanbanEntityFacadeREST kanbanRest = null;

    public KanbanReportEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        kanbanRest = new KanbanEntityFacadeREST();
        kanbanRest.setEntityManager(em);

        rest = new KanbanReportEntityFacadeREST();
        rest.setEntityManager(em);
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
        DatabaseControll.reset(em, tx);
    }

    @Test
    public void test() throws Exception {
        System.out.println("test");

        int templateCount = 3;

        List<Long> kanbanIds = new LinkedList();
        List<Long> removeIds = new LinkedList();

        String kanbanBaseName = "kanban";

        for (int kanbanNo = 1; kanbanNo <= 5; kanbanNo++) {
            String kanbanName = new StringBuilder(kanbanBaseName)
                    .append(kanbanNo)
                    .toString();

            KanbanEntity kanban = new KanbanEntity(kanbanName, null, null, null, null, KanbanStatusEnum.PLANNING);

            tx.begin();
            Response response = kanbanRest.add(kanban, null);
            ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
            assertThat(responseEntity.isSuccess(), is(true));
            tx.commit();

            kanbanIds.add(kanban.getKanbanId());

            if (kanbanNo % 2 > 0) {
                removeIds.add(kanban.getKanbanId());
            }
        }

        em.clear();

        int reportCount = kanbanIds.size() * templateCount;

        // テスト用のカンバン帳票情報を作成する。
        List<KanbanReportEntity> kanbanReports = this.createKanbanReports(kanbanIds, templateCount);

        // カンバン帳票情報を登録する。
        for (KanbanReportEntity kanbanReport : kanbanReports) {
            tx.begin();
            Response response = rest.add(kanbanReport, null);
            ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
            assertThat(responseEntity.isSuccess(), is(true));
            tx.commit();
        }

        em.clear();

        String allCount = rest.countByKanbanId(kanbanIds, null);
        assertThat(Integer.valueOf(allCount), is(reportCount));

        List<KanbanReportEntity> allReports = rest.findByKanbanId(kanbanIds, null, null, null);
        for (KanbanReportEntity report : allReports) {
            Optional<KanbanReportEntity> optReport = kanbanReports.stream()
                    .filter(p -> Objects.equals(p.getKanbanId(), report.getKanbanId())
                            && StringUtils.equals(p.getTemplateName(), report.getTemplateName()))
                    .findFirst();
            assertThat(optReport.isPresent(), is(true));

            KanbanReportEntity kanbanReport = optReport.get();
            assertThat(report.getOutputDate(), is(kanbanReport.getOutputDate()));
            assertThat(report.getFilePath(), is(kanbanReport.getFilePath()));
            assertThat(report.getReportType(), is(kanbanReport.getReportType()));

            // カンバン名は作成時のEntityにセットしていないので、カンバンを取得して比較する。
            String kanbanName = kanbanRest.findBasicInfo(report.getKanbanId()).getKanbanName();
            assertThat(report.getKanbanName(), is(kanbanName));
        }

        // カンバンID一覧を指定して、カンバン帳票情報の件数を取得する。
        for (long kanbanId : kanbanIds) {
            String count = rest.countByKanbanId(Arrays.asList(kanbanId), null);
            assertThat(Integer.valueOf(count), is(templateCount));

            List<KanbanReportEntity> reports = rest.findByKanbanId(Arrays.asList(kanbanId), null, null, null);
            for (KanbanReportEntity report : reports) {
                Optional<KanbanReportEntity> optReport = kanbanReports.stream()
                        .filter(p -> Objects.equals(p.getKanbanId(), report.getKanbanId())
                                && StringUtils.equals(p.getTemplateName(), report.getTemplateName()))
                        .findFirst();
                assertThat(optReport.isPresent(), is(true));

                KanbanReportEntity kanbanReport = optReport.get();
                assertThat(report.getOutputDate(), is(kanbanReport.getOutputDate()));
                assertThat(report.getFilePath(), is(kanbanReport.getFilePath()));
                assertThat(report.getReportType(), is(kanbanReport.getReportType()));

                // カンバン名は作成時のEntityにセットしていないので、カンバンを取得して比較する。
                String kanbanName = kanbanRest.findBasicInfo(report.getKanbanId()).getKanbanName();
                assertThat(report.getKanbanName(), is(kanbanName));
            }
        }

        allCount = rest.countByKanbanId(kanbanIds, null);
        assertThat(Integer.valueOf(allCount), is(reportCount));

        // カンバンID一覧を指定して、カンバン帳票情報一覧を削除する。(存在しないカンバン)
        long maxKanbanId = kanbanIds.stream().mapToLong(p -> p).max().getAsLong();
        tx.begin();
        rest.removeByKanbanId(Arrays.asList(maxKanbanId + 1));
        tx.commit();
        em.clear();

        allCount = rest.countByKanbanId(kanbanIds, null);
        assertThat(Integer.valueOf(allCount), is(reportCount));

        // カンバン帳票情報を更新する。
        KanbanReportEntity updateReport = kanbanReports.get(0);
        updateReport.setTemplateName("newTemplateName");
        updateReport.setOutputDate(new Date());
        updateReport.setFilePath("newFilePath");
        updateReport.setReportType(ReportTypeEnum.MULTIPLE_REPORT);

        tx.begin();
        rest.update(updateReport, null);
        tx.commit();
        em.clear();

        KanbanReportEntity updateReportResult = rest.find(updateReport.getKanbanReportId());
        assertThat(updateReportResult.getTemplateName(), is(updateReport.getTemplateName()));
        assertThat(updateReportResult.getOutputDate(), is(updateReport.getOutputDate()));
        assertThat(updateReportResult.getFilePath(), is(updateReport.getFilePath()));
        assertThat(updateReportResult.getReportType(), is(updateReport.getReportType()));

        // カンバンID一覧を指定して、カンバン帳票情報一覧を削除する。
        tx.begin();
        rest.removeByKanbanId(removeIds);
        tx.commit();
        em.clear();

        reportCount -= templateCount * removeIds.size();
        allCount = rest.countByKanbanId(kanbanIds, null);
        assertThat(Integer.valueOf(allCount), is(reportCount));

        for (long kanbanId : kanbanIds) {
            String count = rest.countByKanbanId(Arrays.asList(kanbanId), null);
            if (removeIds.contains(kanbanId)) {
                assertThat(Integer.valueOf(count), is(0));
            } else {
                assertThat(Integer.valueOf(count), is(templateCount));
            }
        }
    }

    /**
     * テスト用のカンバン帳票情報を作成する。
     *
     * @param kanbanIds カンバンID一覧
     * @param templateCount カンバン毎のテンプレート数
     * @return カンバン帳票情報一覧
     * @throws Exception 
     */
    private List<KanbanReportEntity> createKanbanReports(List<Long> kanbanIds, int templateCount) throws Exception {
        List<KanbanReportEntity> kanbanReports = new LinkedList();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();

        String templateBase = "kanbanReport";
        String templateExt = ".xlsx";
        String fileFolder = "C:\\adFactory\\report\\";

        for (Long kanbanId : kanbanIds) {
            for (int templateNo = 1; templateNo <= templateCount; templateNo++) {
                KanbanReportEntity kanbanReport = new KanbanReportEntity();

                String templateName = new StringBuilder(templateBase)
                        .append(templateNo)
                        .append(templateExt)
                        .toString();

                String dateString = sdf.format(date);
                String filePath = new StringBuilder(fileFolder)
                        .append(templateBase)
                        .append(templateNo)
                        .append("_")
                        .append(dateString)
                        .append(templateExt)
                        .toString();

                kanbanReport.setKanbanId(kanbanId);
                kanbanReport.setTemplateName(templateName);
                kanbanReport.setOutputDate(date);
                kanbanReport.setFilePath(filePath);
                kanbanReport.setReportType(ReportTypeEnum.KANBAN_REPORT);

                kanbanReports.add(kanbanReport);
            }
        }

        return kanbanReports;
    }
}
