/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * プロパティ検索条件
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "propertySearchCondition")
public class PropertySearchCondition {

    @XmlElementWrapper(name = "parentIds")
    @XmlElement(name = "parentId")
    private List<Long> parentIdCollection = null;

    @XmlElementWrapper(name = "propNames")
    @XmlElement(name = "propName")
    private List<String> propNameCollection = null;

    /**
     * コンストラクタ
     */
    public PropertySearchCondition() {
    }

    /**
     * 親ID一覧を設定する。
     *
     * @param parentIdList 親ID一覧
     * @return プロパティ検索条件
     */
    public PropertySearchCondition parentIdList(List<Long> parentIdList) {
        this.parentIdCollection = parentIdList;
        return this;
    }

    /**
     * プロパティ名一覧を設定する。
     *
     * @param propNameList プロパティ名一覧
     * @return プロパティ検索条件
     */
    public PropertySearchCondition propNameList(List<String> propNameList) {
        this.propNameCollection = propNameList;
        return this;
    }

    /**
     * 親ID一覧を取得する。
     *
     * @return 親ID一覧
     */
    public List<Long> getParentIdList() {
        return parentIdCollection;
    }

    /**
     * 親ID一覧を設定する。
     *
     * @param parentIdList 親ID一覧
     */
    public void setParentIdList(List<Long> parentIdList) {
        this.parentIdCollection = parentIdList;
    }

    /**
     * プロパティ名一覧を取得する。
     *
     * @return プロパティ名一覧
     */
    public List<String> getPropNameList() {
        return propNameCollection;
    }

    /**
     * プロパティ名一覧を設定する。
     *
     * @param propNameList プロパティ名一覧
     */
    public void setPropNameList(List<String> propNameList) {
        this.propNameCollection = propNameList;
    }

    @Override
    public String toString() {
        return new StringBuilder("PropertySearchCondition{")
                .append("parentIdCollection=").append(this.parentIdCollection)
                .append(", propNameCollection=").append(this.propNameCollection)
                .append("}")
                .toString();
    }
}
