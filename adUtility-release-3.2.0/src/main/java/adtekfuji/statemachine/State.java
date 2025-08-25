/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.statemachine;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author ke.yokoi
 * @param <E> triggerEnum
 */
public abstract class State<E> {

    class TriggerData {

        private Class nextState;
        private Class triggerAction;
        private Boolean useHistory;

        public TriggerData(Class nextState, Class triggerAction, Boolean useHistory) {
            this.nextState = nextState;
            this.triggerAction = triggerAction;
            this.useHistory = useHistory;
        }

    }

    private final Map<E, TriggerData> triggerCollection = new HashMap<>();

    protected State() {
    }

    /**
     * add combination next state and trigger action.
     *
     * @param triggerEnum
     * @param nextState
     * @param triggerAction
     */
    public final void addTrigger(E triggerEnum, Class nextState, Class triggerAction) {
        triggerCollection.put(triggerEnum, new TriggerData(nextState, triggerAction, Boolean.FALSE));
    }

    /**
     * add combination next state and trigger action and using history.
     *
     * @param triggerEnum
     * @param nextState
     * @param triggerAction
     */
    public final void addHistoryTrigger(E triggerEnum, Class nextState, Class triggerAction) {
        triggerCollection.put(triggerEnum, new TriggerData(nextState, triggerAction, Boolean.TRUE));
    }

    /**
     * get next transit
     *
     * @param triggerEnum
     * @return
     */
    public final Class getNextTransit(E triggerEnum) {
        TriggerData data = triggerCollection.get(triggerEnum);
        return data.nextState;
    }

    /**
     * is use history
     *
     * @param triggerEnum
     * @return
     */
    public final Boolean isUseHistory(E triggerEnum) {
        TriggerData data = triggerCollection.get(triggerEnum);
        return data.useHistory;
    }

    /**
     * Ignite
     *
     * @param triggerEnum
     * @param obj Transfer Object
     * @return next transfer state's class
     * @throws java.lang.ReflectiveOperationException
     */
    public final Class ignite(E triggerEnum, Object obj) throws ReflectiveOperationException {
        TriggerData data = triggerCollection.get(triggerEnum);
        Class nextState = data.nextState;
        if (Objects.nonNull(data.triggerAction)) {
            TriggerAction triggerAction = (TriggerAction) data.triggerAction.newInstance();
            Class retState = triggerAction.action(obj);
            if (Objects.nonNull(retState)) {
                nextState = retState;
            }
        }
        return nextState;
    }

    /**
     * start action
     *
     * @param obj
     * @return
     */
    public abstract E startAction(Object obj);

    /**
     * end action
     *
     * @param obj
     */
    public abstract void endAction(Object obj);
}
