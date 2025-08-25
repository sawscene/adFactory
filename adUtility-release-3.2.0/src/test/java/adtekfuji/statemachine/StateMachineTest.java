/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.statemachine;

import adtekfuji.statemachine.linestatus.CompleteState;
import adtekfuji.statemachine.linestatus.LineStatusState;
import adtekfuji.statemachine.linestatus.RunningState;
import adtekfuji.statemachine.stopwatch.StopState;
import adtekfuji.statemachine.stopwatch.StopWatchState;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class StateMachineTest {

    public StateMachineTest() {
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
    public void testStopWatch() {
        System.out.println("testStopWatch");
        StateMachine stopWatch = StopWatchState.build();
        try {
            stopWatch.start(null);
            stopWatch.ignite(StopWatchState.Trriger.START, null);
            stopWatch.ignite(StopWatchState.Trriger.STOP, null);
            stopWatch.ignite(StopWatchState.Trriger.START, null);
            stopWatch.ignite(StopWatchState.Trriger.STOP, null);
            stopWatch.ignite(StopWatchState.Trriger.RESET, null);
            stopWatch.ignite(StopWatchState.Trriger.STOP, null);
        } catch (StateTransitException ex) {
            assertThat(ex.getState().toString(), is(StopState.class.toString()));
            assertThat(ex.getTrigger(), is(StopWatchState.Trriger.STOP));
        } catch (StateException | ReflectiveOperationException ex) {
        }
    }

    @Test
    public void testLineStatus() {
        System.out.println("testLineStatus");
        StateMachine lineStatus = LineStatusState.build();
        try {
            lineStatus.start(null);
            lineStatus.ignite(LineStatusState.Trriger.START, null);
            lineStatus.ignite(LineStatusState.Trriger.SUSPEND, null);
            lineStatus.ignite(LineStatusState.Trriger.RESUME, null);
            lineStatus.ignite(LineStatusState.Trriger.COMPLETE, null);
            lineStatus.ignite(LineStatusState.Trriger.ARRIVAL_NEXT_CICLE_TIME, null);
            lineStatus.ignite(LineStatusState.Trriger.INDIVIDUAL_PASS, null);
            lineStatus.ignite(LineStatusState.Trriger.CHANGE_ANDON_SETTING, null);
            lineStatus.ignite(LineStatusState.Trriger.REFRESH, null);
            lineStatus.ignite(LineStatusState.Trriger.ACTUAL_REPORT, null);
            lineStatus.ignite(LineStatusState.Trriger.BREAKTIME, null);
            lineStatus.ignite(LineStatusState.Trriger.FINISH_BREAKTIME, null);
            lineStatus.ignite(LineStatusState.Trriger.FINISH_CLEANUP, null);
            lineStatus.ignite(LineStatusState.Trriger.EXIT, null);
        } catch (StateTerminateException ex) {
            assertThat(ex.getState().toString(), is(RunningState.class.toString()));
            assertThat(ex.getTrigger(), is(LineStatusState.Trriger.EXIT));
        } catch (StateException | ReflectiveOperationException ex) {
        }
    }

    @Test
    public void testLineStatusSerialize() {
        System.out.println("testLineStatusSerialize");
        try {
            StateMachine lineStatus = LineStatusState.build();
            lineStatus.start(null);
            lineStatus.ignite(LineStatusState.Trriger.START, null);
            lineStatus.ignite(LineStatusState.Trriger.SUSPEND, null);
            lineStatus.ignite(LineStatusState.Trriger.RESUME, null);
            lineStatus.ignite(LineStatusState.Trriger.COMPLETE, null);
            lineStatus.ignite(LineStatusState.Trriger.BREAKTIME, null);
            StateTransitData data = lineStatus.getTransitData();
            StateMachine lineStatus2 = LineStatusState.build();
            lineStatus2.setTransitData(data);
            lineStatus2.ignite(LineStatusState.Trriger.FINISH_BREAKTIME, null);
            lineStatus2.ignite(LineStatusState.Trriger.FINISH_CLEANUP, null);
            lineStatus2.ignite(LineStatusState.Trriger.EXIT, null);
        } catch (StateTerminateException ex) {
            assertThat(ex.getState().toString(), is(CompleteState.class.toString()));
            assertThat(ex.getTrigger(), is(LineStatusState.Trriger.EXIT));
        } catch (StateException | ReflectiveOperationException ex) {
        }
    }
}
