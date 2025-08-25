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
public class WorkParameterWorkEntity {
    private long workId;
    private Integer taktTime;
    private List<WorkParameterWorkSectionEntity> workSection = new ArrayList<>();

    public long getWorkId() {
        return workId;
    }

    public void setWorkId(long workId) {
        this.workId = workId;
    }

    public Integer getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    public List<WorkParameterWorkSectionEntity> getWorkSection() {
        return workSection;
    }

    public void setWorkSection(List<WorkParameterWorkSectionEntity> workSection) {
        this.workSection = workSection;
    }
}
