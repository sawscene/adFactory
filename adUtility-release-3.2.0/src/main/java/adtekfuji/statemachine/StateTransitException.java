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
public class StateTransitException extends StateException {

    private final Class state;
    private final Object trigger;

    public StateTransitException(Class state, Object trigger) {
        super("State transit is exception. state:" + state + ", trigger:" + trigger);
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
