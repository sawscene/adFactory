/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model;

import jp.adtekfuji.adfactoryserver.model.ActrualResultRuntimeData;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import static org.hamcrest.CoreMatchers.is;
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
public class ActrualResultRuntimeDataTest {

    public ActrualResultRuntimeDataTest() {
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
    public void testTransactionId_EqiupAndOrgnization() {
        System.out.println("testTransactionId_EqiupAndOrgnization");

        ActrualResultRuntimeData rutimeData = ActrualResultRuntimeData.getInstance();

        ActualProductReportEntity report = new ActualProductReportEntity(0L, 200L, 300L, 400L, 500L, null, KanbanStatusEnum.WORKING, null, null);
        boolean ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        long tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(1L));

        report.setTransactionId(1L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(2L));

        report.setTransactionId(2L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(3L));

        report.setTransactionId(1L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(false));
        tid = rutimeData.getNextTransactionId(report);
        assertThat(tid, is(3L));

        report.setTransactionId(2L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(false));
        tid = rutimeData.getNextTransactionId(report);
        assertThat(tid, is(3L));

        report.setTransactionId(3L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(4L));

        report.setTransactionId(10L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(11L));

        report.setTransactionId(0L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(1L));

        report.setTransactionId(1L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(2L));
    }

    @Test
    public void testTransactionId_TerminalIdent() {
        System.out.println("testTransactionId_TerminalIdent");

        ActrualResultRuntimeData rutimeData = ActrualResultRuntimeData.getInstance();

        ActualProductReportEntity report = new ActualProductReportEntity(0L, 200L, 300L, "terminal1", null, KanbanStatusEnum.WORKING, null, null);
        boolean ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        long tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(1L));

        report.setTransactionId(1L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(2L));

        report.setTransactionId(2L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(3L));

        report.setTransactionId(1L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(false));
        tid = rutimeData.getNextTransactionId(report);
        assertThat(tid, is(3L));

        report.setTransactionId(2L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(false));
        tid = rutimeData.getNextTransactionId(report);
        assertThat(tid, is(3L));

        report.setTransactionId(3L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(4L));

        report.setTransactionId(10L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(11L));

        report.setTransactionId(0L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(1L));

        report.setTransactionId(1L);
        ret = rutimeData.checkTransactionId(report);
        assertThat(ret, is(true));
        tid = rutimeData.forwardTransactionId(report);
        assertThat(tid, is(2L));
    }

}
