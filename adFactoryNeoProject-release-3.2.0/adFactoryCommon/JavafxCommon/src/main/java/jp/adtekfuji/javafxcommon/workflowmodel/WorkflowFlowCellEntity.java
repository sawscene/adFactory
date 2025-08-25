/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.workflowmodel;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 工程順フローセル情報エンティティ
 *
 * @author s-maeda
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workflowFlowCell")
public class WorkflowFlowCellEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private String cellId;

    public WorkflowFlowCellEntity() {
    }

    public WorkflowFlowCellEntity(String cellId) {
        this.cellId = cellId;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }
}
