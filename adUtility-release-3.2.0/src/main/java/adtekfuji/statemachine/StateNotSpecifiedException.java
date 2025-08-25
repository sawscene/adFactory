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
public class StateNotSpecifiedException extends StateException {

    private final Class state;

    public StateNotSpecifiedException(Class state) {
        super("State not specified. state:" + state);
        this.state = state;
    }

    public Class getState() {
        return state;
    }

}
