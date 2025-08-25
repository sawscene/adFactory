/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author yu.kikukawa
 */
public class KanbanStartCompDateTest {

    private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public KanbanStartCompDateTest() {
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
     * Test of getWorkKanbanCompDateTime method, of class KanbanStartCompDate.
     */
    @Test
    public void testGetWorkKanbanCompDateTime() throws ParseException {

        WorkKanbanInfoEntity wKanban1 = new WorkKanbanInfoEntity(1L, 2L, 3L, 4L, 5L, "work1");
        WorkKanbanInfoEntity wKanban2 = new WorkKanbanInfoEntity(6L, 7L, 8L, 9L, 10L, "work2");
        WorkKanbanInfoEntity wKanban3 = new WorkKanbanInfoEntity(11L, 12L, 13L, 14L, 15L, "work3");
        wKanban1.setStartDatetime(df.parse("2016/03/30 08:00:00"));
        wKanban1.setCompDatetime(df.parse("2016/03/30 09:00:00"));
        wKanban2.setStartDatetime(df.parse("2016/03/30 09:00:00"));
        wKanban2.setCompDatetime(df.parse("2016/03/30 10:00:00"));
        wKanban3.setStartDatetime(df.parse("2016/03/30 10:00:00"));
        wKanban3.setCompDatetime(df.parse("2016/03/30 11:00:00"));
        wKanban1.setSkipFlag(false);
        wKanban2.setSkipFlag(false);
        wKanban3.setSkipFlag(false);

        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        wKanbans.add(wKanban1);
        wKanbans.add(wKanban2);
        wKanbans.add(wKanban3);

        System.out.println("getWorkKanbanCompDateTime1");
        Date expResult = df.parse("2016/03/30 11:00:00");
        Date result = KanbanStartCompDate.getWorkKanbanCompDateTime(wKanbans);
        assertEquals(expResult, result);

        wKanban1.setSkipFlag(false);
        wKanban2.setSkipFlag(false);
        wKanban3.setSkipFlag(true);

        wKanbans.clear();
        wKanbans.add(wKanban1);
        wKanbans.add(wKanban2);
        wKanbans.add(wKanban3);

        System.out.println("getWorkKanbanCompDateTime2");
        expResult = df.parse("2016/03/30 10:00:00");
        result = KanbanStartCompDate.getWorkKanbanCompDateTime(wKanbans);
        assertEquals(expResult, result);

        wKanban1.setSkipFlag(true);
        wKanban2.setSkipFlag(true);
        wKanban3.setSkipFlag(true);

        wKanbans.clear();
        wKanbans.add(wKanban1);
        wKanbans.add(wKanban2);
        wKanbans.add(wKanban3);
        System.out.println("getWorkKanbanCompDateTime3");
        expResult = null;
        result = KanbanStartCompDate.getWorkKanbanCompDateTime(wKanbans);
        assertEquals(expResult, result);
    }

    /**
     * Test of getWorkKanbanStartDateTime method, of class KanbanStartCompDate.
     */
    @Test
    public void testGetWorkKanbanStartDateTime() throws ParseException {

        WorkKanbanInfoEntity wKanban1 = new WorkKanbanInfoEntity(1L, 2L, 3L, 4L, 5L, "work1");
        WorkKanbanInfoEntity wKanban2 = new WorkKanbanInfoEntity(6L, 7L, 8L, 9L, 10L, "work2");
        WorkKanbanInfoEntity wKanban3 = new WorkKanbanInfoEntity(11L, 12L, 13L, 14L, 15L, "work3");
        wKanban1.setStartDatetime(df.parse("2016/03/30 08:00:00"));
        wKanban1.setCompDatetime(df.parse("2016/03/30 09:00:00"));
        wKanban2.setStartDatetime(df.parse("2016/03/30 09:00:00"));
        wKanban2.setCompDatetime(df.parse("2016/03/30 10:00:00"));
        wKanban3.setStartDatetime(df.parse("2016/03/30 10:00:00"));
        wKanban3.setCompDatetime(df.parse("2016/03/30 11:00:00"));
        wKanban1.setSkipFlag(false);
        wKanban2.setSkipFlag(false);
        wKanban3.setSkipFlag(false);

        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        wKanbans.add(wKanban1);
        wKanbans.add(wKanban2);
        wKanbans.add(wKanban3);

        System.out.println("getWorkKanbanStartDateTime1");
        Date expResult = df.parse("2016/03/30 08:00:00");
        Date result = KanbanStartCompDate.getWorkKanbanStartDateTime(wKanbans);
        assertEquals(expResult, result);

        wKanban1.setSkipFlag(true);
        wKanban2.setSkipFlag(false);
        wKanban3.setSkipFlag(false);

        wKanbans.clear();
        wKanbans.add(wKanban1);
        wKanbans.add(wKanban2);
        wKanbans.add(wKanban3);

        System.out.println("getWorkKanbanStartDateTime2");
        expResult = df.parse("2016/03/30 09:00:00");
        result = KanbanStartCompDate.getWorkKanbanStartDateTime(wKanbans);
        assertEquals(expResult, result);

        wKanban1.setSkipFlag(true);
        wKanban2.setSkipFlag(true);
        wKanban3.setSkipFlag(true);

        wKanbans.clear();
        wKanbans.add(wKanban1);
        wKanbans.add(wKanban2);
        wKanbans.add(wKanban3);
        System.out.println("getWorkKanbanStartDateTime3");
        expResult = null;
        result = KanbanStartCompDate.getWorkKanbanStartDateTime(wKanbans);
        assertEquals(expResult, result);
    }

}
