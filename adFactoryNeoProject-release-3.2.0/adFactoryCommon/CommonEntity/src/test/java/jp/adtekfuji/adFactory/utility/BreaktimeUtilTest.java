/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ke.yokoi
 */
public class BreaktimeUtilTest {

    public BreaktimeUtilTest() {
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

    class TestData {

        private final Date start;
        private final Date end;
        private final long actualTime;
        DateFormat df = new SimpleDateFormat("HH:mm:ss");

        public TestData(Date start, Date end, long actualTime) {
            this.start = start;
            this.end = end;
            this.actualTime = actualTime;
        }

        public Date getStart() {
            return start;
        }

        public Date getEnd() {
            return end;
        }

        public long getActualTime() {
            return actualTime;
        }

        @Override
        public String toString() {
            return "TestData{" + "start=" + df.format(start) + ", end=" + df.format(end) + ", actualTime=" + actualTime + '}';
        }
    }

    @Test
    public void testIsBreaktime() throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList<>();
        breaktimeCollection.add(new BreakTimeInfoEntity("1", df.parse("2000/1/1 12:00:00"), df.parse("2000/1/1 12:45:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("2", df.parse("2000/1/1 17:00:00"), df.parse("2000/1/1 17:15:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("3", df.parse("2000/1/1 18:45:00"), df.parse("2000/1/1 19:00:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity(null, df.parse("2000/1/1 21:00:00"), df.parse("2000/1/2 08:30:00")));

        boolean isBreaktime;
        isBreaktime = BreaktimeUtil.isBreaktime(breaktimeCollection, df.parse("2016/3/15 11:30:00"));
        assertThat(isBreaktime, is(false));
        isBreaktime = BreaktimeUtil.isBreaktime(breaktimeCollection, df.parse("2016/3/15 12:00:00"));
        assertThat(isBreaktime, is(true));
        isBreaktime = BreaktimeUtil.isBreaktime(breaktimeCollection, df.parse("2016/3/15 12:30:00"));
        assertThat(isBreaktime, is(true));
        isBreaktime = BreaktimeUtil.isBreaktime(breaktimeCollection, df.parse("2016/3/15 12:45:00"));
        assertThat(isBreaktime, is(true));
        isBreaktime = BreaktimeUtil.isBreaktime(breaktimeCollection, df.parse("2016/3/15 13:00:00"));
        assertThat(isBreaktime, is(false));

        isBreaktime = BreaktimeUtil.isBreaktime(breaktimeCollection, df.parse("2016/3/15 20:00:00"));
        assertThat(isBreaktime, is(false));
        isBreaktime = BreaktimeUtil.isBreaktime(breaktimeCollection, df.parse("2016/3/15 21:00:00"));
        assertThat(isBreaktime, is(true));
        isBreaktime = BreaktimeUtil.isBreaktime(breaktimeCollection, df.parse("2016/3/16 08:00:00"));
        assertThat(isBreaktime, is(true));
        isBreaktime = BreaktimeUtil.isBreaktime(breaktimeCollection, df.parse("2016/3/16 08:30:00"));
        assertThat(isBreaktime, is(true));
        isBreaktime = BreaktimeUtil.isBreaktime(breaktimeCollection, df.parse("2016/3/16 09:00:00"));
        assertThat(isBreaktime, is(false));
    }

    @Test
    public void testGetEndOfBreaktime() throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList<>();
        breaktimeCollection.add(new BreakTimeInfoEntity("1", df.parse("2000/1/1 12:00:00"), df.parse("2000/1/1 12:45:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("2", df.parse("2000/1/1 17:00:00"), df.parse("2000/1/1 17:15:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("3", df.parse("2000/1/1 18:45:00"), df.parse("2000/1/1 19:00:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity(null, df.parse("2000/1/1 21:00:00"), df.parse("2000/1/2 08:30:00")));

        Date date;
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 11:30:00"));
        assertThat(date, is(nullValue()));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 12:00:00"));
        assertThat(date, is(df.parse("2016/3/15 12:45:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 12:30:00"));
        assertThat(date, is(df.parse("2016/3/15 12:45:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 12:45:00"));
        assertThat(date, is(df.parse("2016/3/15 12:45:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 13:00:00"));
        assertThat(date, is(nullValue()));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 17:00:00"));
        assertThat(date, is(df.parse("2016/3/15 17:15:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 17:10:00"));
        assertThat(date, is(df.parse("2016/3/15 17:15:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 17:15:00"));
        assertThat(date, is(df.parse("2016/3/15 17:15:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 17:30:00"));
        assertThat(date, is(nullValue()));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 18:45:00"));
        assertThat(date, is(df.parse("2016/3/15 19:00:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 18:50:00"));
        assertThat(date, is(df.parse("2016/3/15 19:00:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 19:00:00"));
        assertThat(date, is(df.parse("2016/3/15 19:00:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 19:30:00"));
        assertThat(date, is(nullValue()));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 20:00:00"));
        assertThat(date, is(nullValue()));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 21:00:00"));
        assertThat(date, is(df.parse("2016/3/16 08:30:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/15 22:00:00"));
        assertThat(date, is(df.parse("2016/3/16 08:30:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/16 08:00:00"));
        assertThat(date, is(df.parse("2016/3/16 08:30:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/16 08:30:00"));
        assertThat(date, is(df.parse("2016/3/16 08:30:00")));
        date = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, df.parse("2016/3/16 09:00:00"));
        assertThat(date, is(nullValue()));
    }

    @Test
    public void testGetDiffTime() throws Exception {
        System.out.println("getDiffTime");
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList<>();
        breaktimeCollection.add(new BreakTimeInfoEntity("1", df.parse("2000/1/1 12:00:00"), df.parse("2000/1/1 12:30:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("2", df.parse("2000/1/1 12:15:00"), df.parse("2000/1/1 12:45:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("3", df.parse("2000/1/1 17:01:00"), df.parse("2000/1/1 17:08:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("4", df.parse("2000/1/1 17:00:00"), df.parse("2000/1/1 17:08:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("5", df.parse("2000/1/1 17:00:00"), df.parse("2000/1/1 17:15:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("6", df.parse("2000/1/1 17:01:00"), df.parse("2000/1/1 17:08:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("7", df.parse("2000/1/1 18:45:00"), df.parse("2000/1/1 19:00:00")));

        List<TestData> testDatas = new ArrayList<>();
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 11:30:00"), 30 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 12:00:00"), 60 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 12:30:00"), 60 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 12:45:00"), 60 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 15:00:00"), 195 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 17:00:00"), 315 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 17:10:00"), 315 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 17:15:00"), 315 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 18:00:00"), 360 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 18:45:00"), 405 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 18:50:00"), 405 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 19:00:00"), 405 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/18 19:30:00"), 435 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:00:00"), df.parse("2015/12/18 12:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:00:00"), df.parse("2015/12/18 12:30:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:00:00"), df.parse("2015/12/18 12:45:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:00:00"), df.parse("2015/12/18 15:00:00"), 135 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:00:00"), df.parse("2015/12/18 17:00:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:00:00"), df.parse("2015/12/18 17:10:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:00:00"), df.parse("2015/12/18 17:15:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:00:00"), df.parse("2015/12/18 18:00:00"), 300 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:00:00"), df.parse("2015/12/18 18:45:00"), 345 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:00:00"), df.parse("2015/12/18 18:50:00"), 345 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:00:00"), df.parse("2015/12/18 19:00:00"), 345 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:00:00"), df.parse("2015/12/18 19:30:00"), 375 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:30:00"), df.parse("2015/12/18 12:30:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:30:00"), df.parse("2015/12/18 12:45:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:30:00"), df.parse("2015/12/18 15:00:00"), 135 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:30:00"), df.parse("2015/12/18 17:00:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:30:00"), df.parse("2015/12/18 17:10:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:30:00"), df.parse("2015/12/18 17:15:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:30:00"), df.parse("2015/12/18 18:00:00"), 300 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:30:00"), df.parse("2015/12/18 18:45:00"), 345 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:30:00"), df.parse("2015/12/18 18:50:00"), 345 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:30:00"), df.parse("2015/12/18 19:00:00"), 345 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:30:00"), df.parse("2015/12/18 19:30:00"), 375 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:45:00"), df.parse("2015/12/18 12:45:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:45:00"), df.parse("2015/12/18 15:00:00"), 135 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:45:00"), df.parse("2015/12/18 17:00:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:45:00"), df.parse("2015/12/18 17:10:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:45:00"), df.parse("2015/12/18 17:15:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:45:00"), df.parse("2015/12/18 18:00:00"), 300 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:45:00"), df.parse("2015/12/18 18:45:00"), 345 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:45:00"), df.parse("2015/12/18 18:50:00"), 345 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:45:00"), df.parse("2015/12/18 19:00:00"), 345 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 12:45:00"), df.parse("2015/12/18 19:30:00"), 375 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 13:00:00"), df.parse("2015/12/18 15:00:00"), 120 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 13:00:00"), df.parse("2015/12/18 17:00:00"), 240 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 13:00:00"), df.parse("2015/12/18 17:10:00"), 240 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 13:00:00"), df.parse("2015/12/18 17:15:00"), 240 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 13:00:00"), df.parse("2015/12/18 18:00:00"), 285 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 13:00:00"), df.parse("2015/12/18 18:45:00"), 330 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 13:00:00"), df.parse("2015/12/18 18:50:00"), 330 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 13:00:00"), df.parse("2015/12/18 19:00:00"), 330 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 13:00:00"), df.parse("2015/12/18 19:30:00"), 360 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:00:00"), df.parse("2015/12/18 17:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:00:00"), df.parse("2015/12/18 17:10:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:00:00"), df.parse("2015/12/18 17:15:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:00:00"), df.parse("2015/12/18 18:00:00"), 45 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:00:00"), df.parse("2015/12/18 18:45:00"), 90 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:00:00"), df.parse("2015/12/18 18:50:00"), 90 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:00:00"), df.parse("2015/12/18 19:00:00"), 90 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:00:00"), df.parse("2015/12/18 19:30:00"), 120 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:10:00"), df.parse("2015/12/18 17:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:10:00"), df.parse("2015/12/18 17:10:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:10:00"), df.parse("2015/12/18 17:15:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:10:00"), df.parse("2015/12/18 18:00:00"), 45 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:10:00"), df.parse("2015/12/18 18:45:00"), 90 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:10:00"), df.parse("2015/12/18 18:50:00"), 90 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:10:00"), df.parse("2015/12/18 19:00:00"), 90 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:10:00"), df.parse("2015/12/18 19:30:00"), 120 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:15:00"), df.parse("2015/12/18 17:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:15:00"), df.parse("2015/12/18 17:10:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:15:00"), df.parse("2015/12/18 17:15:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:15:00"), df.parse("2015/12/18 18:00:00"), 45 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:15:00"), df.parse("2015/12/18 18:45:00"), 90 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:15:00"), df.parse("2015/12/18 18:50:00"), 90 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:15:00"), df.parse("2015/12/18 19:00:00"), 90 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 17:15:00"), df.parse("2015/12/18 19:30:00"), 120 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 18:00:00"), df.parse("2015/12/18 18:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 18:00:00"), df.parse("2015/12/18 18:45:00"), 45 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 18:00:00"), df.parse("2015/12/18 18:50:00"), 45 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 18:00:00"), df.parse("2015/12/18 19:00:00"), 45 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 18:00:00"), df.parse("2015/12/18 19:30:00"), 75 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 18:45:00"), df.parse("2015/12/18 18:45:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 18:45:00"), df.parse("2015/12/18 18:50:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 18:45:00"), df.parse("2015/12/18 19:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 18:45:00"), df.parse("2015/12/18 19:30:00"), 30 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 18:50:00"), df.parse("2015/12/18 18:50:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 18:50:00"), df.parse("2015/12/18 19:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 18:50:00"), df.parse("2015/12/18 19:30:00"), 30 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 19:00:00"), df.parse("2015/12/18 19:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 19:00:00"), df.parse("2015/12/18 19:30:00"), 30 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 19:30:00"), df.parse("2015/12/18 20:30:00"), 60 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/12/18 11:00:00"), df.parse("2015/12/19 21:00:00"), (long) (31.5 * 60 * 60 * 1000)));

        for (TestData testData : testDatas) {
            long retTime = BreaktimeUtil.getDiffTime(breaktimeCollection, testData.getStart(), testData.getEnd());
            System.out.println(testData + ", retTime:" + retTime);
            assertThat(retTime, is(testData.getActualTime()));
        }
    }

    @Test
    public void testGetDiffTime2() throws Exception {
        System.out.println("getDiffTime2");
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList<>();

        breaktimeCollection.add(new BreakTimeInfoEntity("1", df.parse("2000/1/1 12:00:00"), df.parse("2000/1/1 12:30:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("2", df.parse("2000/1/1 12:15:00"), df.parse("2000/1/1 12:45:00")));
//        breaktimeCollection.addAll(KanbanTimeUtils.getEmploymentOutTime(df.parse("2015/1/1 9:45:00"), df.parse("2015/1/2 12:45:00"), df.parse("2015/1/1 8:30:00"), df.parse("2015/1/1 17:00:00"), "yyyy/MM/dd HH:mm:ss"));
        breaktimeCollection.add(new BreakTimeInfoEntity("XX", df.parse("2015/1/1 17:00:00"), df.parse("2015/1/2 8:30:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("3", df.parse("2000/1/1 17:00:00"), df.parse("2000/1/1 17:15:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("4", df.parse("2000/1/1 18:45:00"), df.parse("2000/1/1 19:00:00")));

        List<TestData> testDatas = new ArrayList<>();
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 11:30:00"), 30 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 12:00:00"), 60 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 12:30:00"), 60 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 12:45:00"), 60 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 15:00:00"), 195 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 17:00:00"), 315 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 17:10:00"), 315 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 17:15:00"), 315 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 18:00:00"), 315 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 18:45:00"), 315 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 18:50:00"), 315 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 19:00:00"), 315 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/1 19:30:00"), 315 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 12:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 12:30:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 12:45:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 15:00:00"), 135 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 17:00:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 17:10:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 17:15:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 18:00:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 18:45:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 18:50:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 19:00:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 19:30:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:30:00"), df.parse("2015/1/1 12:30:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:30:00"), df.parse("2015/1/1 12:45:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:30:00"), df.parse("2015/1/1 15:00:00"), 135 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:30:00"), df.parse("2015/1/1 17:00:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:30:00"), df.parse("2015/1/1 17:10:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:30:00"), df.parse("2015/1/1 17:15:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:30:00"), df.parse("2015/1/1 18:00:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:30:00"), df.parse("2015/1/1 18:45:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:30:00"), df.parse("2015/1/1 18:50:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:30:00"), df.parse("2015/1/1 19:00:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:30:00"), df.parse("2015/1/1 19:30:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:45:00"), df.parse("2015/1/1 12:45:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:45:00"), df.parse("2015/1/1 15:00:00"), 135 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:45:00"), df.parse("2015/1/1 17:00:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:45:00"), df.parse("2015/1/1 17:10:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:45:00"), df.parse("2015/1/1 17:15:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:45:00"), df.parse("2015/1/1 18:00:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:45:00"), df.parse("2015/1/1 18:45:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:45:00"), df.parse("2015/1/1 18:50:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:45:00"), df.parse("2015/1/1 19:00:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 12:45:00"), df.parse("2015/1/1 19:30:00"), 255 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 15:00:00"), 120 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 17:00:00"), 240 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 17:10:00"), 240 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 17:15:00"), 240 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 18:00:00"), 240 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 18:45:00"), 240 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 18:50:00"), 240 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 19:00:00"), 240 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 19:30:00"), 240 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:00:00"), df.parse("2015/1/1 17:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:00:00"), df.parse("2015/1/1 17:10:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:00:00"), df.parse("2015/1/1 17:15:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:00:00"), df.parse("2015/1/1 18:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:00:00"), df.parse("2015/1/1 18:45:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:00:00"), df.parse("2015/1/1 18:50:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:00:00"), df.parse("2015/1/1 19:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:00:00"), df.parse("2015/1/1 19:30:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:10:00"), df.parse("2015/1/1 17:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:10:00"), df.parse("2015/1/1 17:10:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:10:00"), df.parse("2015/1/1 17:15:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:10:00"), df.parse("2015/1/1 18:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:10:00"), df.parse("2015/1/1 18:45:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:10:00"), df.parse("2015/1/1 18:50:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:10:00"), df.parse("2015/1/1 19:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:10:00"), df.parse("2015/1/1 19:30:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:15:00"), df.parse("2015/1/1 17:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:15:00"), df.parse("2015/1/1 17:10:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:15:00"), df.parse("2015/1/1 17:15:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:15:00"), df.parse("2015/1/1 18:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:15:00"), df.parse("2015/1/1 18:45:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:15:00"), df.parse("2015/1/1 18:50:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:15:00"), df.parse("2015/1/1 19:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 17:15:00"), df.parse("2015/1/1 19:30:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 18:00:00"), df.parse("2015/1/1 18:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 18:00:00"), df.parse("2015/1/1 18:45:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 18:00:00"), df.parse("2015/1/1 18:50:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 18:00:00"), df.parse("2015/1/1 19:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 18:00:00"), df.parse("2015/1/1 19:30:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 18:45:00"), df.parse("2015/1/1 18:45:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 18:45:00"), df.parse("2015/1/1 18:50:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 18:45:00"), df.parse("2015/1/1 19:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 18:45:00"), df.parse("2015/1/1 19:30:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 18:50:00"), df.parse("2015/1/1 18:50:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 18:50:00"), df.parse("2015/1/1 19:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 18:50:00"), df.parse("2015/1/1 19:30:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 19:00:00"), df.parse("2015/1/1 19:00:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 19:00:00"), df.parse("2015/1/1 19:30:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 19:30:00"), df.parse("2015/1/1 20:30:00"), 0 * 60 * 1000));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/3 8:30:00"), (long) (13.0 * 60 * 60 * 1000)));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/3 17:00:00"), (long) (20.75 * 60 * 60 * 1000)));
        testDatas.add(new TestData(df.parse("2015/1/1 11:00:00"), df.parse("2015/1/3 21:00:00"), (long) (20.75 * 60 * 60 * 1000)));

        for (TestData testData : testDatas) {
            long retTime = BreaktimeUtil.getDiffTime(breaktimeCollection, testData.getStart(), testData.getEnd());
            System.out.println(testData + ", retTime:" + retTime);
            assertThat(retTime, is(testData.getActualTime()));
        }
    }

}
