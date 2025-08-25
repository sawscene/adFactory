/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.statemachine.linestatus;

import adtekfuji.statemachine.ExceptionTransit;
import adtekfuji.statemachine.HistoryTransit;
import adtekfuji.statemachine.IgnoreTransit;
import adtekfuji.statemachine.NothingTransit;
import adtekfuji.statemachine.StateMachine;
import adtekfuji.statemachine.TerminateTransit;
import java.util.Arrays;

/**
 *
 * @author ke.yokoi
 */
public class LineStatusState {

    public enum Trriger {

        START,
        SUSPEND,
        RESUME,
        COMPLETE,
        FINISH_REFRESH,
        BREAKTIME,
        FINISH_BREAKTIME,
        FINISH_CLEANUP,
        ARRIVAL_NEXT_CICLE_TIME,
        INDIVIDUAL_PASS,
        CHANGE_ANDON_SETTING,
        REFRESH,
        ACTUAL_REPORT,
        EXIT,
    }

    public static StateMachine build() {
        StopState<Trriger> stop = new StopState();
        stop.addTrigger(Trriger.START, RunningState.class, null);
        stop.addTrigger(Trriger.SUSPEND, NothingTransit.class, null);
        stop.addTrigger(Trriger.RESUME, NothingTransit.class, null);
        stop.addTrigger(Trriger.COMPLETE, CompleteState.class, null);
        stop.addTrigger(Trriger.FINISH_REFRESH, ExceptionTransit.class, null);
        stop.addHistoryTrigger(Trriger.BREAKTIME, BreakTimeState.class, null);
        stop.addTrigger(Trriger.FINISH_BREAKTIME, ExceptionTransit.class, null);
        stop.addTrigger(Trriger.FINISH_CLEANUP, ExceptionTransit.class, null);
        stop.addTrigger(Trriger.ARRIVAL_NEXT_CICLE_TIME, ExceptionTransit.class, null);
        stop.addTrigger(Trriger.INDIVIDUAL_PASS, NothingTransit.class, CompletePointingProcessTriggerAction.class);
        stop.addTrigger(Trriger.CHANGE_ANDON_SETTING, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        stop.addTrigger(Trriger.REFRESH, NothingTransit.class, InsertRefteshTimeTriggerAction.class);
        stop.addTrigger(Trriger.ACTUAL_REPORT, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        stop.addTrigger(Trriger.EXIT, TerminateTransit.class, null);

        RunningState<Trriger> running = new RunningState();
        running.addTrigger(Trriger.START, NothingTransit.class, null);
        running.addTrigger(Trriger.SUSPEND, SuspendState.class, null);
        running.addTrigger(Trriger.RESUME, NothingTransit.class, null);
        running.addTrigger(Trriger.COMPLETE, CompleteState.class, null);
        running.addTrigger(Trriger.FINISH_REFRESH, ExceptionTransit.class, null);
        running.addHistoryTrigger(Trriger.BREAKTIME, BreakTimeState.class, null);
        running.addTrigger(Trriger.FINISH_BREAKTIME, ExceptionTransit.class, null);
        running.addTrigger(Trriger.FINISH_CLEANUP, ExceptionTransit.class, null);
        running.addTrigger(Trriger.ARRIVAL_NEXT_CICLE_TIME, ExceptionTransit.class, null);
        running.addTrigger(Trriger.INDIVIDUAL_PASS, NothingTransit.class, CompletePointingProcessTriggerAction.class);
        running.addTrigger(Trriger.CHANGE_ANDON_SETTING, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        running.addTrigger(Trriger.REFRESH, NothingTransit.class, InsertRefteshTimeTriggerAction.class);
        running.addTrigger(Trriger.ACTUAL_REPORT, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        running.addTrigger(Trriger.EXIT, TerminateTransit.class, null);

        SuspendState<Trriger> suspend = new SuspendState();
        suspend.addTrigger(Trriger.START, NothingTransit.class, null);
        suspend.addTrigger(Trriger.SUSPEND, NothingTransit.class, null);
        suspend.addTrigger(Trriger.RESUME, RunningState.class, null);
        suspend.addTrigger(Trriger.COMPLETE, CompleteState.class, null);
        suspend.addTrigger(Trriger.FINISH_REFRESH, ExceptionTransit.class, null);
        suspend.addHistoryTrigger(Trriger.BREAKTIME, BreakTimeState.class, null);
        suspend.addTrigger(Trriger.FINISH_BREAKTIME, ExceptionTransit.class, null);
        suspend.addTrigger(Trriger.FINISH_CLEANUP, ExceptionTransit.class, null);
        suspend.addTrigger(Trriger.ARRIVAL_NEXT_CICLE_TIME, ExceptionTransit.class, null);
        suspend.addTrigger(Trriger.INDIVIDUAL_PASS, NothingTransit.class, CompletePointingProcessTriggerAction.class);
        suspend.addTrigger(Trriger.CHANGE_ANDON_SETTING, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        suspend.addTrigger(Trriger.REFRESH, NothingTransit.class, InsertRefteshTimeTriggerAction.class);
        suspend.addTrigger(Trriger.ACTUAL_REPORT, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        suspend.addTrigger(Trriger.EXIT, TerminateTransit.class, null);

        RefreshState<Trriger> refresh = new RefreshState();
        refresh.addTrigger(Trriger.START, NothingTransit.class, null);
        refresh.addTrigger(Trriger.SUSPEND, NothingTransit.class, null);
        refresh.addTrigger(Trriger.RESUME, NothingTransit.class, null);
        refresh.addTrigger(Trriger.COMPLETE, NothingTransit.class, null);
        refresh.addTrigger(Trriger.FINISH_REFRESH, CompleteState.class, null);
        refresh.addHistoryTrigger(Trriger.BREAKTIME, BreakTimeState.class, null);
        refresh.addTrigger(Trriger.FINISH_BREAKTIME, ExceptionTransit.class, null);
        refresh.addTrigger(Trriger.FINISH_CLEANUP, ExceptionTransit.class, null);
        refresh.addTrigger(Trriger.ARRIVAL_NEXT_CICLE_TIME, ExceptionTransit.class, null);
        refresh.addTrigger(Trriger.INDIVIDUAL_PASS, NothingTransit.class, CompletePointingProcessTriggerAction.class);
        refresh.addTrigger(Trriger.CHANGE_ANDON_SETTING, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        refresh.addTrigger(Trriger.REFRESH, NothingTransit.class, InsertRefteshTimeTriggerAction.class);
        refresh.addTrigger(Trriger.ACTUAL_REPORT, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        refresh.addTrigger(Trriger.EXIT, TerminateTransit.class, null);

        CompleteState<Trriger> comp = new CompleteState();
        comp.addTrigger(Trriger.START, NothingTransit.class, null);
        comp.addTrigger(Trriger.SUSPEND, NothingTransit.class, null);
        comp.addTrigger(Trriger.RESUME, NothingTransit.class, null);
        comp.addTrigger(Trriger.COMPLETE, NothingTransit.class, null);
        comp.addTrigger(Trriger.FINISH_REFRESH, ExceptionTransit.class, null);
        comp.addHistoryTrigger(Trriger.BREAKTIME, BreakTimeState.class, null);
        comp.addTrigger(Trriger.FINISH_BREAKTIME, ExceptionTransit.class, null);
        comp.addTrigger(Trriger.FINISH_CLEANUP, ExceptionTransit.class, null);
        comp.addTrigger(Trriger.ARRIVAL_NEXT_CICLE_TIME, RunningState.class, null);
        comp.addTrigger(Trriger.INDIVIDUAL_PASS, NothingTransit.class, CompletePointingProcessTriggerAction.class);
        comp.addTrigger(Trriger.CHANGE_ANDON_SETTING, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        comp.addTrigger(Trriger.REFRESH, NothingTransit.class, InsertRefteshTimeTriggerAction.class);
        comp.addTrigger(Trriger.ACTUAL_REPORT, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        comp.addTrigger(Trriger.EXIT, TerminateTransit.class, null);

        BreakTimeState<Trriger> breaktime = new BreakTimeState();
        breaktime.addTrigger(Trriger.START, NothingTransit.class, null);
        breaktime.addTrigger(Trriger.SUSPEND, NothingTransit.class, null);
        breaktime.addTrigger(Trriger.RESUME, NothingTransit.class, null);
        breaktime.addTrigger(Trriger.COMPLETE, NothingTransit.class, null);
        breaktime.addTrigger(Trriger.FINISH_REFRESH, IgnoreTransit.class, null);
        breaktime.addTrigger(Trriger.BREAKTIME, NothingTransit.class, null);
        breaktime.addTrigger(Trriger.FINISH_BREAKTIME, CleanupState.class, null);
        breaktime.addTrigger(Trriger.FINISH_CLEANUP, ExceptionTransit.class, null);
        breaktime.addTrigger(Trriger.ARRIVAL_NEXT_CICLE_TIME, ExceptionTransit.class, null);
        breaktime.addTrigger(Trriger.INDIVIDUAL_PASS, NothingTransit.class, CompletePointingProcessTriggerAction.class);
        breaktime.addTrigger(Trriger.CHANGE_ANDON_SETTING, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        breaktime.addTrigger(Trriger.REFRESH, NothingTransit.class, InsertRefteshTimeTriggerAction.class);
        breaktime.addTrigger(Trriger.ACTUAL_REPORT, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        breaktime.addTrigger(Trriger.EXIT, TerminateTransit.class, null);

        CleanupState<Trriger> cleanup = new CleanupState();
        cleanup.addTrigger(Trriger.START, NothingTransit.class, null);
        cleanup.addTrigger(Trriger.SUSPEND, NothingTransit.class, null);
        cleanup.addTrigger(Trriger.RESUME, NothingTransit.class, null);
        cleanup.addTrigger(Trriger.COMPLETE, NothingTransit.class, null);
        cleanup.addTrigger(Trriger.FINISH_REFRESH, IgnoreTransit.class, null);
        cleanup.addTrigger(Trriger.BREAKTIME, ExceptionTransit.class, null);
        cleanup.addTrigger(Trriger.FINISH_BREAKTIME, ExceptionTransit.class, null);
        cleanup.addTrigger(Trriger.FINISH_CLEANUP, HistoryTransit.class, null);
        cleanup.addTrigger(Trriger.ARRIVAL_NEXT_CICLE_TIME, ExceptionTransit.class, null);
        cleanup.addTrigger(Trriger.INDIVIDUAL_PASS, NothingTransit.class, CompletePointingProcessTriggerAction.class);
        cleanup.addTrigger(Trriger.CHANGE_ANDON_SETTING, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        cleanup.addTrigger(Trriger.REFRESH, NothingTransit.class, InsertRefteshTimeTriggerAction.class);
        cleanup.addTrigger(Trriger.ACTUAL_REPORT, NothingTransit.class, RecalculationPlanningTimeTriggerAction.class);
        cleanup.addTrigger(Trriger.EXIT, TerminateTransit.class, null);

        //状態遷移マシンの準備
        StateMachine<Trriger> stateMachie = new StateMachine();
        stateMachie.addTriggers(Arrays.asList(Trriger.values()));
        stateMachie.addState(stop);
        stateMachie.addState(running);
        stateMachie.addState(suspend);
        stateMachie.addState(refresh);
        stateMachie.addState(comp);
        stateMachie.addState(breaktime);
        stateMachie.addState(cleanup);

        return stateMachie;
    }

}
