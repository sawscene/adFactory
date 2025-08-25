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
public class WorkParameterWorkSectionEntity {
    private int order;
    private String documentTitle;
    private String fileName;
    private String physicalFileName;
    private List<WorkParameterWorkCheckInfoEntity> workCheckInfo = new ArrayList<>();

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPhysicalFileName() {
        return physicalFileName;
    }

    public void setPhysicalFileName(String physicalFileName) {
        this.physicalFileName = physicalFileName;
    }

    public List<WorkParameterWorkCheckInfoEntity> getWorkCheckInfo() {
        return workCheckInfo;
    }

    public void setWorkCheckInfo(List<WorkParameterWorkCheckInfoEntity> workCheckInfo) {
        this.workCheckInfo = workCheckInfo;
    }
}
