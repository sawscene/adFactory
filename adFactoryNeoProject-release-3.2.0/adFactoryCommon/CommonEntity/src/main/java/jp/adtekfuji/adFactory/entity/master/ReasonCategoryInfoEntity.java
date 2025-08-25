/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.master;

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;

/**
 * 理由区分マスタ
 *
 * @author
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "reasonCategory")
public class ReasonCategoryInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty reasonCategoryIdProperty;
    private ObjectProperty<ReasonTypeEnum> reasonTypeProperty;
    private StringProperty reasonCategoryNameProperty;

    @XmlElement(required = true)
    private Long reasonCategoryId;          // 理由区分ID
    @XmlElement()
    private ReasonTypeEnum reasonType;      // 理由種別
    @XmlElement()
    private String reasonCategoryName;      // 理由区分名
    @XmlElement()
    private Boolean defaultReasonCategory = false; // デフォルト理由区分
    @XmlElement()
    private Integer verInfo;                // 排他用バーション

    /**
     * コンストラクタ
     */
    public ReasonCategoryInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param in 理由区分マスタ
     */
    public ReasonCategoryInfoEntity(ReasonCategoryInfoEntity in) {
        this.reasonCategoryId = in.reasonCategoryId;
        this.reasonType = in.reasonType;
        this.reasonCategoryName = in.reasonCategoryName;
        this.defaultReasonCategory = in.defaultReasonCategory;
        this.verInfo = in.verInfo;
    }

    /**
     * コンストラクタ
     *
     * @param reasonCategoryId 理由区分ID
     * @param type 理由種別
     * @param reasonCategoryName 理由区分名
     */
    public ReasonCategoryInfoEntity(Long reasonCategoryId, ReasonTypeEnum type, String reasonCategoryName) {
        this.reasonCategoryId = reasonCategoryId;
        this.reasonType = type;
        this.reasonCategoryName = reasonCategoryName;
    }

    /**
     * 理由区分IDプロパティを取得する。
     *
     * @return 理由区分ID
     */
    public LongProperty reasonCategoryIdProperty() {
        if (Objects.isNull(this.reasonCategoryIdProperty)) {
            this.reasonCategoryIdProperty = new SimpleLongProperty(this.reasonCategoryId);
        }
        return this.reasonCategoryIdProperty;
    }

    /**
     * 理由種別プロパティを取得する。
     *
     * @return 理由種別
     */
    public ObjectProperty<ReasonTypeEnum> typeProperty() {
        if (Objects.isNull(this.reasonTypeProperty)) {
            this.reasonTypeProperty = new SimpleObjectProperty(this.reasonType);
        }
        return this.reasonTypeProperty;
    }

    /**
     * 理由区分名プロパティを取得する。
     *
     * @return 理由区分名
     */
    public StringProperty reasonCategoryNameProperty() {
        if (Objects.isNull(this.reasonCategoryNameProperty)) {
            this.reasonCategoryNameProperty = new SimpleStringProperty(this.reasonCategoryName);
        }
        return this.reasonCategoryNameProperty;
    }

    /**
     * 理由区分IDを取得する。
     *
     * @return 理由区分ID
     */
    public Long getId() {
        if (Objects.nonNull(this.reasonCategoryIdProperty)) {
            return this.reasonCategoryIdProperty.get();
        }
        return this.reasonCategoryId;
    }

    /**
     * 理由種別を取得する。
     *
     * @return 理由種別
     */
    public ReasonTypeEnum getReasonType() {
        if (Objects.nonNull(this.reasonTypeProperty)) {
            return this.reasonTypeProperty.get();
        }
        return this.reasonType;
    }

    /**
     * 理由種別を設定する。
     * 
     * @param reasonType 理由種別
     */
    public void setReasonType(ReasonTypeEnum reasonType) {
        if (Objects.nonNull(this.reasonTypeProperty)) {
            this.reasonTypeProperty.set(reasonType);
        } else {
            this.reasonType = reasonType;
        }
    }

    /**
     * 理由区分名を取得する。
     *
     * @return 理由区分名
     */
    public String getReasonCategoryName() {
        if (Objects.nonNull(this.reasonCategoryNameProperty)) {
            return this.reasonCategoryNameProperty.get();
        }
        return this.reasonCategoryName;
    }

    /**
     *  理由区分名を設定する。
     * 
     * @param reasonCategoryName 理由区分名
     */
    public void setReasonCategoryName(String reasonCategoryName) {
        if (Objects.nonNull(this.reasonCategoryNameProperty)) {
            this.reasonCategoryNameProperty.set(reasonCategoryName);
        } else {
            this.reasonCategoryName = reasonCategoryName;
        }
    }

    /**
     * デフォルト理由区分かどうかを返す。
     * 
     * @return true: デフォルト、false: 非デフォルト
     */
    public Boolean isDefaultReasonCategory() {
        return defaultReasonCategory;
    }

    /**
     * デフォルト理由区分を設定する。
     * 
     * @param defaultReasonCategory true: デフォルト、false: 非デフォルト
     */
    public void setDefaultReasonCaegory(Boolean defaultReasonCategory) {
        this.defaultReasonCategory = defaultReasonCategory;
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.reasonCategoryId);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true:同じである、false:異なる
     */
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
        final ReasonCategoryInfoEntity other = (ReasonCategoryInfoEntity) obj;
        return Objects.equals(this.reasonCategoryId, other.reasonCategoryId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ReasonCategoryEntity{")
                .append("reasonCategoryId=").append(this.reasonCategoryId)
                .append(", reasonType=").append(this.reasonType)
                .append(", reasonCategoryName=").append(this.reasonCategoryName)
                .append(", defaultReasonCategory=").append(this.defaultReasonCategory)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
