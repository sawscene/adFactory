/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice;

import jp.adtekfuji.DbImportService.DbImportTask;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ka.makihara
 */
public class ImportTaskTest {

    public ImportTaskTest() {
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

    /**
     * Test of concatResult method, of class ImportTask.
     */
    @Test
    public void testConcatResult() {
        Map<String, Integer> a = new HashMap();
        Map<String, Integer> b = new HashMap();

        a.put("procNum", 0);
        a.put("successNum", 1);
        a.put("skipKanbanNum", 2);
        a.put("failedNum", 3);
        a.put("other_a", 4);

        b.put("procNum", 2);
        b.put("successNum", 3);
        b.put("skipKanbanNum", 5);
        b.put("failedNum", 7);
        b.put("other_b", 11);

        final Map<String, Integer> ret = DbImportTask.concatResult(a, b);

        assertTrue(ret.get("procNum") == 2
                && ret.get("successNum") == 4
                && ret.get("skipKanbanNum") == 7
                && ret.get("failedNum") == 10
                && ret.get("other_a") == 4
                && ret.get("other_b") == 11
        );
    }

    @Test
    public void testConcatResultIsNull() {
        Map<String, Integer> a = new HashMap();
        Map<String, Integer> b = null;

        a.put("procNum", 0);
        a.put("successNum", 1);
        a.put("skipKanbanNum", 2);
        a.put("failedNum", 3);
        a.put("other_a", 4);

        final Map<String, Integer> ret = DbImportTask.concatResult(a, b);

        assertTrue(ret.get("procNum") == 0
                && ret.get("successNum") == 1
                && ret.get("skipKanbanNum") == 2
                && ret.get("failedNum") == 3
                && ret.get("other_a") == 4
        );
    }

    @Test
    public void testConcatResultIsNullBoth() {
        Map<String, Integer> a = null;
        Map<String, Integer> b = null;

        final Map<String, Integer> ret = DbImportTask.concatResult(a, b);

        assertTrue(Objects.isNull(ret));
    }
}
