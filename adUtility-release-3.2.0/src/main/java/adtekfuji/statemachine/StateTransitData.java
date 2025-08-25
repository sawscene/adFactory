/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.statemachine;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author ke.yokoi
 */
public class StateTransitData implements Serializable {

    private static final long serialVersionUID = 1L;
    private State defaultState = null;
    private State nowState = null;
    private final Map<Object, Boolean> triggerCollection = new LinkedHashMap<>();
    private Deque<State> stackState = new ArrayDeque<>();

    public StateTransitData() {
    }

    public void clear() {
        nowState = defaultState;
        for (Map.Entry<Object, Boolean> e : triggerCollection.entrySet()) {
            triggerCollection.replace(e.getKey(), Boolean.FALSE);
        }
        stackState.clear();
    }

    public Map<Object, Boolean> getTriggerCollection() {
        return triggerCollection;
    }

    public <E> void setTriggerCollection(List<E> triggers) {
        triggerCollection.clear();
        for (Object trg : triggers) {
            triggerCollection.put(trg, Boolean.FALSE);
        }
    }

    public <E> void changeTrigger(E trigger, Boolean flag) {
        triggerCollection.replace(trigger, flag);
    }

    public Deque<State> getStackState() {
        return stackState;
    }

    public void setStackState(Deque<State> stackState) {
        this.stackState = stackState;
    }

    public void pushHistory(State state) {
        this.stackState.offerLast(state);
    }

    public State popHistory() {
        if (Objects.isNull(this.stackState.peekLast())) {
            return null;
        }
        return this.stackState.pollLast();
    }

    public State getDefaultState() {
        return defaultState;
    }

    public void setDefaultState(State defaultState) {
        this.defaultState = defaultState;
    }

    public State getNowState() {
        return nowState;
    }

    public void setNowState(State nowState) {
        this.nowState = nowState;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StateTransitData{" + "defaultState=" + defaultState + ", nowState=" + nowState + ", triggerCollection=" + triggerCollection + ", stackState=" + stackState + '}';
    }

}
