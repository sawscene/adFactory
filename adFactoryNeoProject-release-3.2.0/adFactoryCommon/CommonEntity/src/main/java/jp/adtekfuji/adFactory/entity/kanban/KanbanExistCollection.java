/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバン重複
 *
 * @author ta-ito
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "kanbanExistCollection")
public class KanbanExistCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "KanbanExistEntities")
    @XmlElement(name = "KanbanExistEntity")
    private List<KanbanExistEntity> kanbanExistCollection = null;// カンバン存在情報一覧

    /**
     * コンストラクタ
     */
    public KanbanExistCollection() {
    }

    /**
     * カンバン情報からkanbanExistCollectionを作成
     *
     * @param entities
     */
    public void addKanbanInfoCollection(List<KanbanInfoEntity> entities) {
        if (Objects.isNull(kanbanExistCollection)) {
            kanbanExistCollection = new ArrayList<>();
        }
        entities.stream().forEach((entity) -> {
            kanbanExistCollection.add(new KanbanExistEntity(entity.getKanbanId(), entity.getKanbanName(), entity.getKanbanSubname(), entity.getFkWorkflowId()));
        });
    }

    /**
     * カンバン存在情報一覧を取得する。
     *
     * @return カンバン存在情報一覧
     */
    public List<KanbanExistEntity> getKanbanExistCollection() {
        return kanbanExistCollection;
    }

    /**
     * カンバン存在情報一覧を設定する。
     *
     * @param kanbanExistCollection カンバン存在情報一覧
     */
    public void setKanbanExistCollection(List<KanbanExistEntity> kanbanExistCollection) {
        this.kanbanExistCollection = kanbanExistCollection;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.kanbanExistCollection);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KanbanExistCollection other = (KanbanExistCollection) obj;
        return true;
    }

    @Override
    public String toString() {
        String size = null;
        if (Objects.nonNull(this.kanbanExistCollection)) {
            size = String.valueOf(this.kanbanExistCollection.size());
        }

        return new StringBuilder("KanbanExistCollection{")
                .append(size)
                .append("}")
                .toString();
    }
}
