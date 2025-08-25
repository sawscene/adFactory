/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバン別計画実績情報(VIEW)の検索条件
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "kanbanTopicSearchCondition")
public class KanbanTopicSearchCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * コンテンツタイプ
     */
    @XmlEnum(String.class)
    public enum ContentType {
        DAYS_KANBAN,             // 日別カンバン計画実績
        DAYS_ORGANIZATION,       // 日別作業者計画実績
        MONTHS_KANBAN,           // 月別カンバン計画実績
        DAYS_LINE;               // ライン別カンバン計画実績
    }

    @XmlElement()
    private ContentType contentType = null;// コンテンツタイプ

    @XmlElementWrapper(name = "primaryKeys")
    @XmlElement(name = "primaryKey")
    private List<Long> primaryKeys = null;// 主キー一覧

    @XmlElement()
    private Date fromDate = null;// 日時範囲の先頭

    @XmlElement()
    private Date toDate = null;// 日時範囲の末尾

    @XmlElement()
    private Boolean withParents = null;// 検索キーに親のキーを含めるかどうか

    @XmlElement()
    private String modelName;// モデル名

    /**
     * コンストラクタ
     */
    public KanbanTopicSearchCondition() {
    }

    /**
     * コンストラクタ
     *
     * @param contentType コンテンツタイプ
     */
    public KanbanTopicSearchCondition(ContentType contentType) {
        this.contentType = contentType;
    }

    /**
     * コンテンツタイプを取得する。
     *
     * @return コンテンツタイプ
     */
    public ContentType getContentType() {
        return this.contentType;
    }

    /**
     * コンテンツタイプを設定する。
     *
     * @param contentType コンテンツタイプ
     */
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    /**
     * 主キー一覧を取得する。
     *
     * @return 主キー一覧
     */
    public List<Long> getPrimaryKeys() {
        return this.primaryKeys;
    }

    /**
     * 主キー一覧を設定する。
     *
     * @param primaryKeys 主キー一覧
     */
    public void setPrimaryKeys(List<Long> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    /**
     * 日時範囲の先頭を取得する。
     *
     * @return 日時範囲の先頭
     */
    public Date getFromDate() {
        return this.fromDate;
    }

    /**
     * 日時範囲の先頭を設定する。
     *
     * @param fromDate 日時範囲の先頭
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * 日時範囲の末尾を取得する。
     *
     * @return 日時範囲の末尾
     */
    public Date getToDate() {
        return this.toDate;
    }

    /**
     * 日時範囲の末尾を設定する。
     *
     * @param toDate 日時範囲の末尾
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * 検索キーに親のキーを含めるかどうかを取得する。
     *
     * @return 検索キーに親のキーを含めるかどうか
     */
    public Boolean isWithParents() {
        return withParents;
    }

    /**
     * 検索キーに親のキーを含めるかどうかを設定する。
     *
     * @param withParents 検索キーに親のキーを含めるかどうか
     */
    public void setWithParent(Boolean withParents) {
        this.withParents = withParents;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanTopicSearchCondition{")
                .append("contentType=").append(this.contentType)
                .append(", ")
                .append("primaryKeys=").append(this.primaryKeys)
                .append(", ")
                .append("fromDate=").append(this.fromDate)
                .append(", ")
                .append("toDate=").append(this.toDate)
                .append(", ")
                .append("withParents=").append(this.withParents)
                .append(", ")
                .append("modelName=").append(this.modelName)
                .append("}")
                .toString();
    }
}
