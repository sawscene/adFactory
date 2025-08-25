/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.statemachine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author ke.yokoi
 * @param <E> triggerEnum
 */
public class StateMachine<E> {

    private final StateTransitData transitData = new StateTransitData();
    private transient final Map<Class, State> stateCollection = new HashMap<>();

    public StateMachine() {
    }

    /**
     * add triggers
     *
     * @param triggers
     */
    public void addTriggers(List<E> triggers) {
        transitData.setTriggerCollection(triggers);
    }

    /**
     * add state
     *
     * @param state
     */
    public void addState(State state) {
        if (Objects.isNull(transitData.getDefaultState())) {
            transitData.setDefaultState(state);
            transitData.setNowState(state);
        }
        stateCollection.put(state.getClass(), state);
    }

    /**
     * start transit
     *
     * @throws adtekfuji.statemachine.StateException
     * @throws java.lang.ReflectiveOperationException
     */
    public synchronized void start() throws StateException, ReflectiveOperationException {
        Object retTrg = transitData.getNowState().startAction(null);
        if (Objects.nonNull(retTrg)) {
            ignite((E) retTrg, null);
        }
    }

    /**
     * start transit
     *
     * @param obj
     * @throws adtekfuji.statemachine.StateException
     * @throws java.lang.ReflectiveOperationException
     */
    public synchronized void start(Object obj) throws StateException, ReflectiveOperationException {
        Object retTrg = transitData.getNowState().startAction(obj);
        if (Objects.nonNull(retTrg)) {
            ignite((E) retTrg, obj);
        }
    }

    /**
     * ignite trigger
     *
     * @param trigger
     * @throws adtekfuji.statemachine.StateException
     * @throws java.lang.ReflectiveOperationException
     */
    public synchronized void ignite(E trigger) throws StateException, ReflectiveOperationException {
        transitData.changeTrigger(trigger, Boolean.TRUE);

        for (Map.Entry<Object, Boolean> e : transitData.getTriggerCollection().entrySet()) {
            if (e.getValue()) {
                igniteImp(trigger, null);
            }
        }
    }

    /**
     * ignite trigger
     *
     * @param trigger
     * @param obj Transfer Object
     * @throws adtekfuji.statemachine.StateException
     * @throws java.lang.ReflectiveOperationException
     */
    public synchronized void ignite(E trigger, Object obj) throws StateException, ReflectiveOperationException {
        transitData.changeTrigger(trigger, Boolean.TRUE);

        for (Map.Entry<Object, Boolean> e : transitData.getTriggerCollection().entrySet()) {
            if (e.getValue()) {
                igniteImp(trigger, obj);
            }
        }
    }

    private void igniteImp(E trigger, Object obj) throws StateException, ReflectiveOperationException {
        Class nextTransit = transitData.getNowState().getNextTransit(trigger);
        if (nextTransit == ExceptionTransit.class) {
            //異常遷移.
            throw new StateTransitException(transitData.getNowState().getClass(), trigger);
        } else if (nextTransit == NothingTransit.class) {
            //次遷移なしだが、トリガは消費.
            transitData.getNowState().ignite(trigger, obj);
            transitData.changeTrigger(trigger, Boolean.FALSE);
        } else if (nextTransit == IgnoreTransit.class) {
            //次遷移なしで、トリガは浮かしておく.
        } else if (nextTransit == TerminateTransit.class) {
            //状態遷移終了.
            State state = transitData.getNowState();
            state.ignite(trigger, obj);
            state.endAction(obj);
            transitData.clear();
            throw new StateTerminateException(state.getClass(), trigger);
        } else if (nextTransit == HistoryTransit.class) {
            //ヒストリー.
            State state = transitData.getNowState();
            state.ignite(trigger, obj);
            transitData.changeTrigger(trigger, Boolean.FALSE);
            state.endAction(obj);
            //ヒストリーにある状態に戻す.
            state = transitData.popHistory();
            if (Objects.isNull(state)) {
                throw new StateInvalidHistoryException(state.getClass(), trigger);
            }
            transitData.setNowState(state);
            Object retTrg = state.startAction(obj);
            if (Objects.nonNull(retTrg)) {
                ignite((E) retTrg, obj);
            }
        } else {
            //次遷移あり.
            State state = transitData.getNowState();
            Class nextState = state.ignite(trigger, obj);
            transitData.changeTrigger(trigger, Boolean.FALSE);
            state.endAction(obj);
            //ヒストリーを使用するトリガーか.
            if (state.isUseHistory(trigger)) {
                transitData.pushHistory(state);
            }
            //次の状態へ.
            state = stateCollection.get(nextState);
            if (Objects.isNull(state)) {
                throw new StateNotSpecifiedException(nextState);
            }
            transitData.setNowState(state);
            Object retTrg = state.startAction(obj);
            if (Objects.nonNull(retTrg)) {
                ignite((E) retTrg, obj);
            }
        }
    }

    public synchronized Class getNowState() {
        return transitData.getNowState().getClass();
    }

    public synchronized StateTransitData getTransitData() {
        return transitData;
    }

    public synchronized void setTransitData(StateTransitData transitData) {
        State defaultState = transitData.getDefaultState();
        if (Objects.nonNull(defaultState) && stateCollection.containsKey(defaultState.getClass())) {
            this.transitData.setDefaultState(stateCollection.get(defaultState.getClass()));
        }
        State nowState = transitData.getNowState();
        if (Objects.nonNull(nowState) && stateCollection.containsKey(nowState.getClass())) {
            this.transitData.setNowState(stateCollection.get(nowState.getClass()));
        }
        State transitState = transitData.getStackState().peekLast();
        if (Objects.nonNull(transitState) && stateCollection.containsKey(transitState.getClass())) {
            this.transitData.pushHistory(stateCollection.get(transitState.getClass()));
        }
        for (Map.Entry<Object, Boolean> e : transitData.getTriggerCollection().entrySet()) {
            Map<Object, Boolean> triggers = this.transitData.getTriggerCollection();
            for (Map.Entry<Object, Boolean> t : triggers.entrySet()) {
                if (t.toString().equals(e.toString())) {
                    this.transitData.changeTrigger(t, Boolean.TRUE);
                }
            }
        }
    }

    public synchronized void forceClear() {
        transitData.clear();
    }

}
