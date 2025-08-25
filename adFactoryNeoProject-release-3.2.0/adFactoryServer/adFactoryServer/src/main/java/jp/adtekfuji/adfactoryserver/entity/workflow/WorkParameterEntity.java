/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author i.chugin
 */
public class WorkParameterEntity {
    private List<WorkParameterWorkEntity> work = new ArrayList<>();

    public List<WorkParameterWorkEntity> getWork() {
        return work;
    }

    public void setWork(List<WorkParameterWorkEntity> work) {
        this.work = work;
    }
}
