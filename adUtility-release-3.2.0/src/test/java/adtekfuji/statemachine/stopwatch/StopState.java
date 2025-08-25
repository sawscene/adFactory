/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.statemachine.stopwatch;

import adtekfuji.statemachine.State;

/**
 *
 * @author ke.yokoi
 */
public class StopState<E> extends State {

    public StopState() {
    }

    @Override
    public E startAction(Object obj) {
        System.out.println("StartAct:" + StopState.class);
        return null;
    }

    @Override
    public void endAction(Object obj) {
        System.out.println("EndAct:" + StopState.class);
    }

}
