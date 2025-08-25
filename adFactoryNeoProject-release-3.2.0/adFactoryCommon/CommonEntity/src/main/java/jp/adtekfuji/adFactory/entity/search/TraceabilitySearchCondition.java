/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * トレーサビリティの検索条件
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "traceabilitySearchCondition")
public class TraceabilitySearchCondition implements Serializable {

    @XmlElement()
    String kanbanName;
    @XmlElement()
    String modelName;

    @XmlElement()
    Boolean isAll = false;

    /**
     * コンストラクタ
     */
    public void TraceabilitySearchCondition() {
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
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
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 全て取得？
     *
     * @return 全て取得？(true:全て, false:最新のみ)
     */
    public Boolean getIsAll() {
        return this.isAll;
    }

    /**
     * 全て取得？を設定する。
     *
     * @param isAll 全て取得？(true:全て, false:最新のみ)
     */
    public void setIsAll(Boolean isAll) {
        this.isAll = isAll;
    }

    @Override
    public String toString() {
        return new StringBuilder("TraceabilitySearchCondition{")
                .append("kanbanName=").append(this.kanbanName)
                .append(", ")
                .append("modelName=").append(this.modelName)
                .append(", ")
                .append("isAll=").append(this.isAll)
                .append("}")
                .toString();
    }
}
