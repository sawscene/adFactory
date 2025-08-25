/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * ProdResultEntity プライマリキー
 * 
 * @author s-heya
 */
@Embeddable
public class ProdResultEntityPK implements Serializable {
    //@NotNull
    @Column(name = "fk_kanban_id")
    private Long fkKanbanId;
    //@NotNull
    @Column(name = "fk_work_id")
    private Long fkWorkId;
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "unique_id")
    private String uniqueId;

    public ProdResultEntityPK() {
    }

    public ProdResultEntityPK(Long fkKanbanId, Long fkWorkId, String uniqueId) {
        this.fkKanbanId = fkKanbanId;
        this.fkWorkId = fkWorkId;
        this.uniqueId = uniqueId;
    }
    public Long getFkKanbanId() {
        return fkKanbanId;
    }

    public void setFkKanbanId(Long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

    public Long getFkWorkId() {
        return fkWorkId;
    }

    public void setFkWorkId(Long fkWorkId) {
        this.fkWorkId = fkWorkId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.fkKanbanId);
        hash = 29 * hash + Objects.hashCode(this.fkWorkId);
        hash = 29 * hash + Objects.hashCode(this.uniqueId);
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
        final ProdResultEntityPK other = (ProdResultEntityPK) obj;
        if (!Objects.equals(this.uniqueId, other.uniqueId)) {
            return false;
        }
        if (!Objects.equals(this.fkKanbanId, other.fkKanbanId)) {
            return false;
        }
        return Objects.equals(this.fkWorkId, other.fkWorkId);
    }

    @Override
    public String toString() {
        return "ProdResultEntityPK{" + "fkKanbanId=" + fkKanbanId + ", fkWorkId=" + fkWorkId + ", uniqueId=" + uniqueId + '}';
    }

}
