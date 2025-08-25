/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.statemachine.stopwatch;

import adtekfuji.statemachine.ExceptionTransit;
import adtekfuji.statemachine.IgnoreTransit;
import adtekfuji.statemachine.StateMachine;
import java.util.Arrays;

/**
 *
 * @author ke.yokoi
 */
public class StopWatchState {

    public enum Trriger {

        START,
        STOP,
        RESET,
    }

    public static StateMachine build() {
        //待機中状態の準備
        StopState<Trriger> stop = new StopState();
        stop.addTrigger(Trriger.START, StartState.class, TriggerActionA.class);
        stop.addTrigger(Trriger.STOP, ExceptionTransit.class, null);
        stop.addTrigger(Trriger.RESET, IgnoreTransit.class, null);
        //計測中状態の準備
        StartState<Trriger> start = new StartState();
        start.addTrigger(Trriger.START, IgnoreTransit.class, null);
        start.addTrigger(Trriger.STOP, SuspendState.class, null);
        start.addTrigger(Trriger.RESET, IgnoreTransit.class, TriggerActionA.class);
        //一時停止中状態の準備
        SuspendState<Trriger> suspend = new SuspendState();
        suspend.addTrigger(Trriger.START, StartState.class, null);
        suspend.addTrigger(Trriger.STOP, IgnoreTransit.class, TriggerActionA.class);
        suspend.addTrigger(Trriger.RESET, StopState.class, null);
        //状態遷移マシンの準備
        StateMachine<Trriger> stateMachie = new StateMachine();
        stateMachie.addTriggers(Arrays.asList(Trriger.values()));
        stateMachie.addState(stop);
        stateMachie.addState(start);
        stateMachie.addState(suspend);

        return stateMachie;
    }

}
