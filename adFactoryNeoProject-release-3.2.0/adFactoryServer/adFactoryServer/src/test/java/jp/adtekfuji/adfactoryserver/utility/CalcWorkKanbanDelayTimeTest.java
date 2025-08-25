/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class CalcWorkKanbanDelayTimeTest {

    private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public CalcWorkKanbanDelayTimeTest() {
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

    @Test
    public void testCalcDelayTime1() throws ParseException {
        System.out.println("calcDelayTime1");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 09:00:00"), null, (long)30 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:20:00"), df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:20:00"), null, (long)20 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 09:20:00"), df.parse("2015/11/18 14:00:00"), null, null, null, (long)0 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 09:30:00"), df.parse("2015/11/18 10:00:00"), df.parse("2015/11/18 09:30:00"), df.parse("2015/11/18 10:00:00"), null, (long)30 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.WORKING, df.parse("2015/11/18 10:00:00"), df.parse("2015/11/18 10:30:00"), null, null, null, (long)0 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 10:30:00"), df.parse("2015/11/18 11:00:00"), df.parse("2015/11/18 10:30:00"), df.parse("2015/11/18 11:00:00"), null, (long)30 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 11:00:00"), df.parse("2015/11/18 11:30:00"), null, null, null, (long)0 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 11:30:00"), df.parse("2015/11/18 12:00:00"), null, null, null, (long)0 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 15:00:00"), df.parse("2015/11/18 17:00:00"), null, null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 11:45:00"), 0);
        assertThat(result, is(-13200000L));
        long sec = Math.abs((result / 1000) % 60);
        long min = Math.abs((result / 1000 / 60) % 60);
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(-3L));
        assertThat(min, is(40L));
        assertThat(sec, is(0L));
    }

    @Test
    public void testCalcDelayTime2() throws ParseException {
        System.out.println("calcDelayTime2");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 08:35:00"), df.parse("2015/11/18 08:55:00"), null, (long)20 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:20:00"), df.parse("2015/11/18 08:55:00"), df.parse("2015/11/18 09:10:00"), null, (long)15 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 09:20:00"), df.parse("2015/11/18 14:00:00"), null, null, null, (long)0 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 09:30:00"), df.parse("2015/11/18 10:00:00"), df.parse("2015/11/18 09:30:00"), df.parse("2015/11/18 10:00:00"), null, (long)30 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.WORKING, df.parse("2015/11/18 10:00:00"), df.parse("2015/11/18 10:30:00"), null, null, null, (long)0 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 10:30:00"), df.parse("2015/11/18 11:00:00"), df.parse("2015/11/18 10:30:00"), df.parse("2015/11/18 11:00:00"), null, (long)30 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 11:00:00"), df.parse("2015/11/18 11:30:00"), null, null, null, (long)0 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 11:30:00"), df.parse("2015/11/18 12:00:00"), null, null, null, (long)0 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 15:00:00"), df.parse("2015/11/18 17:00:00"), null, null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 11:45:00"), 0);
        //assertThat(result, is(-12300000L));
        assertThat(result, is(-13200000L));
        long sec = Math.abs((result / 1000) % 60);
        long min = Math.abs((result / 1000 / 60) % 60);
        long hour = (result / 1000 / 60 / 60) % 60;
        //assertThat(hour, is(-3L));
        //assertThat(min, is(25L));
        //assertThat(sec, is(0L));
        assertThat(hour, is(-3L));
        assertThat(min, is(40L));
        assertThat(sec, is(0L));
    }

    @Test
    public void testCalcDelayTime3() throws ParseException {
        System.out.println("calcDelayTime3");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 09:00:00"), null, (long)30 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:20:00"), df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:20:00"), null, (long)20 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 09:20:00"), df.parse("2015/11/18 14:00:00"), df.parse("2015/11/18 09:20:00"), df.parse("2015/11/18 11:20:00"), null, (long)2 * 60 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 09:30:00"), df.parse("2015/11/18 10:00:00"), df.parse("2015/11/18 09:30:00"), df.parse("2015/11/18 10:00:00"), null, (long)30 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.WORKING, df.parse("2015/11/18 10:00:00"), df.parse("2015/11/18 10:30:00"), null, null, null, (long)0 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 10:30:00"), df.parse("2015/11/18 11:00:00"), df.parse("2015/11/18 10:30:00"), df.parse("2015/11/18 11:00:00"), null, (long)30 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 11:00:00"), df.parse("2015/11/18 11:30:00"), null, null, null, (long)0 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 11:30:00"), df.parse("2015/11/18 12:00:00"), null, null, null, (long)0 * 60 * 1000));
        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 15:00:00"), df.parse("2015/11/18 17:00:00"), null, null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 11:45:00"), 0);
        //assertThat(result, is(8700000L));
        assertThat(result, is(3600000L));
        long sec = Math.abs((result / 1000) % 60);
        long min = Math.abs((result / 1000 / 60) % 60);
        long hour = (result / 1000 / 60 / 60) % 60;
        //assertThat(hour, is(2L));
        //assertThat(min, is(25L));
        //assertThat(sec, is(0L));
        assertThat(hour, is(1L));
        assertThat(min, is(0L));
        assertThat(sec, is(0L));
    }

    @Test
    public void testCalcDelayTime4() throws ParseException {
        System.out.println("calcDelayTime4");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:30:00"), null, null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 08:30:00"), 0);
        assertThat(result, is(0L));
        long sec = Math.abs((result / 1000) % 60);
        long min = Math.abs((result / 1000 / 60) % 60);
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(0L));
        assertThat(min, is(0L));
        assertThat(sec, is(0L));
    }

    @Test
    public void testCalcDelayTime5() throws ParseException {
        System.out.println("calcDelayTime5");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 09:00:00"), null, null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 08:40:00"), 0);
        assertThat(result, is(-600000L));
        long sec = (result / 1000) % 60;
        long min = (result / 1000 / 60) % 60;
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(0L));
        assertThat(min, is(-10L));
        assertThat(sec, is(0L));
    }

    @Test
    public void testCalcDelayTime6() throws ParseException {
        System.out.println("calcDelayTime6");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 09:00:00"), null, null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 09:00:00"), 0);
        assertThat(result, is(-1800000L));
        long sec = (result / 1000) % 60;
        long min = (result / 1000 / 60) % 60;
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(0L));
        assertThat(min, is(-30L));
        assertThat(sec, is(0L));
    }

    @Test
    public void testCalcDelayTime7() throws ParseException {
        System.out.println("calcDelayTime7");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.PLANNED, df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 09:00:00"), null, null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 09:30:00"), 0);
        assertThat(result, is(-3600000L));
        long sec = (result / 1000) % 60;
        long min = (result / 1000 / 60) % 60;
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(-1L));
        assertThat(min, is(0L));
        assertThat(sec, is(0L));
    }

    @Test
    public void testCalcDelayTime8() throws ParseException {
        System.out.println("calcDelayTime8");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 09:00:00"), null, null, null, (long)10 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 08:50:00"), -10 * 60 * 1000);
        assertThat(result, is(600000L));
        long sec = (result / 1000) % 60;
        long min = (result / 1000 / 60) % 60;
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(0L));
        assertThat(min, is(10L));
        assertThat(sec, is(0L));
    }

    @Test
    public void testCalcDelayTime9() throws ParseException {
        System.out.println("calcDelayTime9");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.WORKING, df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 08:40:00"), null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 08:50:00"), -10 * 60 * 1000);
        assertThat(result, is(-600000L));
        long sec = (result / 1000) % 60;
        long min = (result / 1000 / 60) % 60;
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(0L));
        assertThat(min, is(-10L));
        assertThat(sec, is(0L));
    }

    @Test
    public void testCalcDelayTime10() throws ParseException {
        System.out.println("calcDelayTime10");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.WORKING, df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 08:40:00"), null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 09:10:00"), -10 * 60 * 1000);
        assertThat(result, is(-600000L));
        long sec = (result / 1000) % 60;
        long min = (result / 1000 / 60) % 60;
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(0L));
        assertThat(min, is(-10L));
        assertThat(sec, is(0L));
    }

    @Test
    public void testCalcDelayTime11() throws ParseException {
        System.out.println("calcDelayTime11");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.WORKING, df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 08:40:00"), null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 09:20:00"), -10 * 60 * 1000);
        assertThat(result, is(-1200000L));
        long sec = (result / 1000) % 60;
        long min = (result / 1000 / 60) % 60;
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(0L));
        assertThat(min, is(-20L));
        assertThat(sec, is(0L));
    }

    @Test
    public void testCalcDelayTime12() throws ParseException {
        System.out.println("calcDelayTime12");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.COMPLETION, df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:30:00"), null, null, null, (long)20 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 09:00:00"), 20 * 60 * 1000);
        assertThat(result, is(1800000L));
        long sec = (result / 1000) % 60;
        long min = (result / 1000 / 60) % 60;
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(0L));
        assertThat(min, is(30L));
        assertThat(sec, is(0L));
    }

    @Test
    public void testCalcDelayTime13() throws ParseException {
        System.out.println("calcDelayTime13");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.WORKING, df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:30:00"), df.parse("2015/11/18 08:40:00"), null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 09:00:00"), 20 * 60 * 1000);
        assertThat(result, is(1200000L));
        long sec = (result / 1000) % 60;
        long min = (result / 1000 / 60) % 60;
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(0L));
        assertThat(min, is(20L));
        assertThat(sec, is(0L));
    }
    @Test
    public void testCalcDelayTime14() throws ParseException {
        System.out.println("calcDelayTime14");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.WORKING, df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:30:00"), df.parse("2015/11/18 08:40:00"), null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 09:20:00"), 20 * 60 * 1000);
        assertThat(result, is(600000L));
        long sec = (result / 1000) % 60;
        long min = (result / 1000 / 60) % 60;
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(0L));
        assertThat(min, is(10L));
        assertThat(sec, is(0L));
    }
    @Test
    public void testCalcDelayTime15() throws ParseException {
        System.out.println("calcDelayTime15");

        List<WorkKanbanTimeData> datas = new ArrayList<>();

        datas.add(new WorkKanbanTimeData(KanbanStatusEnum.WORKING, df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:30:00"), df.parse("2015/11/18 08:40:00"), null, null, (long)0 * 60 * 1000));

        long result = CalcWorkKanbanDelayTime.calcDelayTimes(datas, df.parse("2015/11/18 09:40:00"), 20 * 60 * 1000);
        assertThat(result, is(-600000L));
        long sec = (result / 1000) % 60;
        long min = (result / 1000 / 60) % 60;
        long hour = (result / 1000 / 60 / 60) % 60;
        assertThat(hour, is(0L));
        assertThat(min, is(-10L));
        assertThat(sec, is(0L));
    }
}
