/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import jp.adtekfuji.adFactory.entity.kanban.TraceabilityEntity;
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
public class TraceabilityJdbcTest {
    
    public TraceabilityJdbcTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Ignore
    @Test
    public void test() throws ParseException {
        System.out.println("test");

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        TraceabilityJdbc jdbc = TraceabilityJdbc.getInstance();

        long kanbanId = 123L;
        String kanbanName = "カンバン①";
        String workflowName = "こうていじゅん②";
        int workflowRev = 4;

        // 削除
        jdbc.deleteKanbanTraceability(kanbanId);

        // 追加
        TraceabilityEntity trace1 = new TraceabilityEntity();

        trace1.setKanbanId(kanbanId);// カンバンID
        trace1.setKanbanName(kanbanName);// カンバン名
        trace1.setKanbanName(workflowName);// 工程順名
        trace1.setWorkflowRev(workflowRev);// 版数
        trace1.setWorkKanbanId(567L);// 工程カンバンID
        trace1.setActualId(0L);// 工程実績ID
        trace1.setTraceName("こうもくⅠ");// 項目名
        trace1.setTraceOrder(1);// 順
        trace1.setLowerLimit(123.456);// 規格下限
        trace1.setUpperLimit(789.012);// 規格上限
        trace1.setTraceValue("aaa③");// 値
        trace1.setTraceConfirm(true);// 確認
        trace1.setEquipmentName("きき④");// 設備名
        trace1.setOrganizationName("さぎょうしゃ⑤");// 組織名
        trace1.setImplementDatetime(df.parse("2019/01/21 12:31:45.678"));// 作業日時
        trace1.setTraceTag("tag_aaa");
        trace1.setTraceProps("{\"key1\":\"value1-1\", \"key2\":\"value1-2\"}");

        TraceabilityEntity trace2 = new TraceabilityEntity();

        trace2.setKanbanId(kanbanId);// カンバンID
        trace2.setKanbanName(kanbanName);// カンバン名
        trace2.setKanbanName(workflowName);// 工程順名
        trace2.setWorkflowRev(workflowRev);// 版数
        trace2.setWorkKanbanId(567L);// 工程カンバンID
        trace1.setActualId(0L);// 工程実績ID
        trace2.setTraceName("こうもくⅡ");// 項目名
        trace2.setTraceOrder(2);// 順
        trace2.setLowerLimit(111.111);// 規格下限
        trace2.setUpperLimit(222.222);// 規格上限
        trace2.setTraceValue("bbb⑥");// 値
        trace2.setTraceConfirm(true);// 確認
        trace2.setEquipmentName("きき⑦");// 設備名
        trace2.setOrganizationName("さぎょうしゃ⑧");// 組織名
        trace2.setImplementDatetime(df.parse("2019/01/21 12:31:46.999"));// 作業日時
        trace2.setTraceTag("tag_bbb");
        trace2.setTraceProps("{\"key1\":\"value2-1\", \"key2\":\"value2-2\"}");

        List<TraceabilityEntity> traces = new ArrayList();
        traces.add(trace1);
        traces.add(trace2);

        jdbc.addTraceability(0L, traces);

        // 取得
        List<TraceabilityEntity> results = jdbc.getKanbanTraceability(kanbanId, false);

        System.out.println("getKanbanTraceability end.");
//        assertEquals(expResult, result);
    }
    
}
