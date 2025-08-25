/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.work;

import java.util.List;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.HierarchyTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.HierarchyEntity;

/**
 * 工程階層情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "workHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkHierarchyEntity extends HierarchyEntity {

    @XmlElementWrapper(name = "works")
    @XmlElement(name = "work")
    @Transient
    private List<WorkEntity> workCollection = null;// 工程情報一覧

    /**
     * コンストラクタ
     */
    public WorkHierarchyEntity() {
        super.setHierarchyType(HierarchyTypeEnum.WORK);
    }

    /**
     * コンストラクタ
     *
     * @param parentId 親階層ID
     * @param hierarchyName 階層名
     */
    public WorkHierarchyEntity(Long parentId, String hierarchyName) {
        super.setHierarchyType(HierarchyTypeEnum.WORK);
        super.setParentHierarchyId(parentId);
        super.setHierarchyName(hierarchyName);
    }

    /**
     * 階層マスタに変換する。
     *
     * @return 階層マスタ
     * @throws Exception 
     */
    public HierarchyEntity upcast() throws Exception {
        HierarchyEntity hierarchy = new HierarchyEntity();
        hierarchy.setHierarchyId(this.getHierarchyId());
        hierarchy.setHierarchyType(this.getHierarchyType());
        hierarchy.setHierarchyName(this.getHierarchyName());
        hierarchy.setParentHierarchyId(this.getParentHierarchyId());
        hierarchy.setVerInfo(this.getVerInfo());
        hierarchy.setChildCount(this.getChildCount());
        return hierarchy;
    }

    /**
     * 工程階層IDを取得する。
     *
     * @return 工程階層ID
     */
    @XmlElement(name="workHierarchyId")
    public Long getWorkHierarchyId() {
        return super.getHierarchyId();
    }

    /**
     * 工程階層IDを設定する。
     *
     * @param workHierarchyId 工程階層ID
     */
    public void setWorkHierarchyId(Long workHierarchyId) {
        super.setHierarchyId(workHierarchyId);
    }

    /**
     * 工程情報一覧を取得する。
     *
     * @return 工程情報一覧
     */
    public List<WorkEntity> getWorkCollection() {
        return this.workCollection;
    }

    /**
     * 工程情報一覧を設定する。
     *
     * @param workCollection 工程情報一覧
     */
    public void setWorkCollection(List<WorkEntity> workCollection) {
        this.workCollection = workCollection;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkHierarchyEntity{")
                .append("workHierarchyId=").append(this.getWorkHierarchyId())
                .append(", ")
                .append("hierarchyName=").append(this.getHierarchyName())
                .append(", ")
                .append("parentHierarchyId=").append(this.getParentHierarchyId())
                .append(", ")
                .append("verInfo=").append(this.getVerInfo())
                .append("}")
                .toString();
    }
}
