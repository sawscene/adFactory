/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.statemachine;

/**
 *
 * @author ke.yokoi
 */
public class StateInvalidHistoryException extends StateException {

    private final Class state;
    private final Object trigger;

    public StateInvalidHistoryException(Class state, Object trigger) {
        super("State invalid history. state:" + state + ", trigger:" + trigger);
        this.state = state;
        this.trigger = trigger;
    }

    public Class getState() {
        return state;
    }

    public Object getTrigger() {
        return trigger;
    }

}
