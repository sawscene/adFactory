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
public interface TriggerAction {

    /**
     *
     * @param obj Transfer Object
     * @return next transfer state's class. null is default.
     */
    public Class action(Object obj);

}
