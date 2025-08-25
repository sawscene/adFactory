/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.statemachine.linestatus;

import adtekfuji.statemachine.TriggerAction;

/**
 *
 * @author ke.yokoi
 */
public class CompletePointingProcessTriggerAction implements TriggerAction {

    @Override
    public Class action(Object obj) {
        System.out.println("TrgAct:" + getClass());
        return null;
    }

}
