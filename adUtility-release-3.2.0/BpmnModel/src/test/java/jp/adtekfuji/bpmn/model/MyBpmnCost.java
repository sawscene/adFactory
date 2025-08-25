/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model;

import jp.adtekfuji.bpmn.model.entity.BpmnCost;

/**
 *
 * @author ke.yokoi
 */
public class MyBpmnCost implements BpmnCost {

    private final Long weight;
    private final Long capacity;

    public MyBpmnCost(Long weight, Long capacity) {
        this.weight = weight;
        this.capacity = capacity;
    }

    @Override
    public Long getWeight() {
        return weight;
    }

    @Override
    public Long getCapacity() {
        return capacity;
    }

}
