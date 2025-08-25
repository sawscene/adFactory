/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class StatusPatternEnumTest {

    public StatusPatternEnumTest() {
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
    public void testGetStatusPattern() throws Exception {
        System.out.println("testGetStatusPattern");

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        StatusPatternEnum result;

        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.PLANNING, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null, null, df.parse("2015/1/1 11:30:00"));
        assertThat(result, is(StatusPatternEnum.PLAN_NORMAL));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.PLANNING, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null, null, df.parse("2015/1/1 12:00:00"));
        assertThat(result, is(StatusPatternEnum.PLAN_NORMAL));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.PLANNING, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null, null, df.parse("2015/1/1 12:30:00"));
        assertThat(result, is(StatusPatternEnum.PLAN_DELAYSTART));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.PLANNING, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null, null, df.parse("2015/1/1 13:00:00"));
        assertThat(result, is(StatusPatternEnum.PLAN_DELAYSTART));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.PLANNING, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null, null, df.parse("2015/1/1 13:30:00"));
        assertThat(result, is(StatusPatternEnum.PLAN_DELAYSTART));

        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.PLANNED, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null, null, df.parse("2015/1/1 11:30:00"));
        assertThat(result, is(StatusPatternEnum.PLAN_NORMAL));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.PLANNED, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null, null, df.parse("2015/1/1 12:00:00"));
        assertThat(result, is(StatusPatternEnum.PLAN_NORMAL));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.PLANNED, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null, null, df.parse("2015/1/1 12:30:00"));
        assertThat(result, is(StatusPatternEnum.PLAN_DELAYSTART));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.PLANNED, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null, null, df.parse("2015/1/1 13:00:00"));
        assertThat(result, is(StatusPatternEnum.PLAN_DELAYSTART));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.PLANNED, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null, null, df.parse("2015/1/1 13:30:00"));
        assertThat(result, is(StatusPatternEnum.PLAN_DELAYSTART));

        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.WORKING, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 11:30:00"), null, df.parse("2015/1/1 11:30:00"));
        assertThat(result, is(StatusPatternEnum.WORK_NORMAL));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.WORKING, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 12:00:00"), null, df.parse("2015/1/1 12:00:00"));
        assertThat(result, is(StatusPatternEnum.WORK_NORMAL));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.WORKING, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 12:30:00"), null, df.parse("2015/1/1 12:30:00"));
        assertThat(result, is(StatusPatternEnum.WORK_DELAYSTART));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.WORKING, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 12:30:00"), null, df.parse("2015/1/1 13:00:00"));
        assertThat(result, is(StatusPatternEnum.WORK_DELAYSTART));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.WORKING, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 12:30:00"), null, df.parse("2015/1/1 13:30:00"));
        assertThat(result, is(StatusPatternEnum.WORK_DELAYCOMP));

        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.SUSPEND, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null, null, null);
        assertThat(result, is(StatusPatternEnum.SUSPEND_NORMAL));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.INTERRUPT, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null, null, null);
        assertThat(result, is(StatusPatternEnum.INTERRUPT_NORMAL));

        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.COMPLETION, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 12:30:00"), null);
        assertThat(result, is(StatusPatternEnum.COMP_NORMAL));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.COMPLETION, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), null);
        assertThat(result, is(StatusPatternEnum.COMP_NORMAL));
        result = StatusPatternEnum.getStatusPattern(KanbanStatusEnum.COMPLETION, df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:00:00"), df.parse("2015/1/1 12:00:00"), df.parse("2015/1/1 13:30:00"), null);
        assertThat(result, is(StatusPatternEnum.COMP_DELAYCOMP));
    }

    class CompareTestDate {

        private final StatusPatternEnum now;
        private final StatusPatternEnum next;
        private final StatusPatternEnum result;

        public CompareTestDate(StatusPatternEnum now, StatusPatternEnum next, StatusPatternEnum result) {
            this.now = now;
            this.next = next;
            this.result = result;
        }

        public StatusPatternEnum getNow() {
            return now;
        }

        public StatusPatternEnum getNext() {
            return next;
        }

        public StatusPatternEnum getResult() {
            return result;
        }

        @Override
        public String toString() {
            return "CompareTestDate{" + "now=" + now + ", next=" + next + ", result=" + result + '}';
        }

    }

    @Test
    public void testCompareStatus() throws Exception {
        System.out.println("compareStatus");

        List<CompareTestDate> testDatas = new ArrayList<>();
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.PLAN_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.PLAN_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.WORK_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.WORK_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.WORK_DELAYCOMP));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.PLAN_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.PLAN_NORMAL));

        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.PLAN_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.PLAN_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.PLAN_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.WORK_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.WORK_DELAYCOMP));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.PLAN_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.PLAN_DELAYSTART));

        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.WORK_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.PLAN_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.WORK_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.WORK_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.WORK_DELAYCOMP));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.WORK_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.WORK_NORMAL));

        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.WORK_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.PLAN_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.WORK_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.WORK_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.WORK_DELAYCOMP));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.WORK_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.WORK_DELAYSTART));

        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.WORK_DELAYCOMP));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.PLAN_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.WORK_DELAYCOMP));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.WORK_DELAYCOMP));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.WORK_DELAYCOMP));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.WORK_DELAYCOMP));
        testDatas.add(new CompareTestDate(StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.WORK_DELAYCOMP));

        testDatas.add(new CompareTestDate(StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.SUSPEND_NORMAL));

        testDatas.add(new CompareTestDate(StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.INTERRUPT_NORMAL));

        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.PLAN_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.PLAN_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.WORK_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.WORK_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.WORK_DELAYCOMP));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.COMP_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.COMP_DELAYCOMP));

        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.PLAN_NORMAL, StatusPatternEnum.PLAN_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.PLAN_DELAYSTART, StatusPatternEnum.PLAN_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.WORK_NORMAL, StatusPatternEnum.WORK_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.WORK_DELAYSTART, StatusPatternEnum.WORK_DELAYSTART));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.WORK_DELAYCOMP, StatusPatternEnum.WORK_DELAYCOMP));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.SUSPEND_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.INTERRUPT_NORMAL, StatusPatternEnum.INTERRUPT_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.COMP_NORMAL, StatusPatternEnum.COMP_NORMAL));
        testDatas.add(new CompareTestDate(StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.COMP_DELAYCOMP, StatusPatternEnum.COMP_DELAYCOMP));

        StatusPatternEnum result;
        for (CompareTestDate testData : testDatas) {
            result = StatusPatternEnum.compareStatus(testData.getNow(), testData.getNext());
            System.out.println(testData + " : " + result);
            assertThat(result, is(testData.getResult()));
        }
    }

}
