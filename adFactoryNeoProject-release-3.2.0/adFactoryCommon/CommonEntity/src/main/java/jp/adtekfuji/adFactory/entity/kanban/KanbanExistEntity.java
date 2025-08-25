/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバン存在情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "kanbanExist")
public class KanbanExistEntity {

    @XmlElement()
    private Long kanbanId;// カンバンID

    @XmlElement()
    private String kanbanName;// カンバン名

    @XmlElement()
    private String kanbanSubname;// サブカンバン名

    @XmlElement()
    private Long fkWorkflowId;// 工程順ID

    /**
     * コンストラクタ
     */
    public KanbanExistEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param kanbanId カンバンID
     * @param kanbanName カンバン名
     * @param kanbanSubname サブカンバン名
     * @param fkWorkflowId 工程順ID
     */
    public KanbanExistEntity(Long kanbanId, String kanbanName, String kanbanSubname, Long fkWorkflowId) {
        this.kanbanId = kanbanId;
        this.kanbanName = kanbanName;
        this.kanbanSubname = kanbanSubname;
        this.fkWorkflowId = fkWorkflowId;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getKanbanId() {
        return kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    /**
     * サブカンバン名を取得する。
     *
     * @return サブカンバン名
     */
    public String getKanbanSubname() {
        return kanbanSubname;
    }

    /**
     * サブカンバン名を設定する。
     *
     * @param kanbanSubname サブカンバン名
     */
    public void setKanbanSubname(String kanbanSubname) {
        this.kanbanSubname = kanbanSubname;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getFkWorkflowId() {
        return fkWorkflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param fkWorkflowId 工程順ID
     */
    public void setFkWorkflowId(Long fkWorkflowId) {
        this.fkWorkflowId = fkWorkflowId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.kanbanId);
        hash = 37 * hash + Objects.hashCode(this.kanbanName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KanbanExistEntity other = (KanbanExistEntity) obj;
        if (!Objects.equals(this.kanbanName, other.kanbanName)) {
            return false;
        }
        if (!Objects.equals(this.kanbanId, other.kanbanId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanExistEntity{")
                .append("kanbanId=").append(this.kanbanId)
                .append(", ")
                .append("kanbanName=").append(this.kanbanName)
                .append(", ")
                .append("kanbanSubname=").append(this.kanbanSubname)
                .append(", ")
                .append("fkWorkflowId=").append(this.fkWorkflowId)
                .append("}")
                .toString();
    }
}
