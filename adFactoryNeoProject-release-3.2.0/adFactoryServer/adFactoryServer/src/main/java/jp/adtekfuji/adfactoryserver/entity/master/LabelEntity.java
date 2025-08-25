/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.master;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * ラベルマスタ
 * 
 * @author s-heya
 */
@Entity
@Table(name = "mst_label")
@XmlRootElement(name = "label")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 追加時の重複チェック
    @NamedQuery(name = "LabelEntity.checkAdd", query = "SELECT COUNT(o.labelId) FROM LabelEntity o WHERE o.labelName = :labelName"),
    // 更新時の重複チェック
    @NamedQuery(name = "LabelEntity.checkUpdate", query = "SELECT COUNT(o.labelId) FROM LabelEntity o WHERE o.labelName = :labelName AND o.labelId != :labelId"),
    @NamedQuery(name = "LabelEntity.findAll", query = "SELECT o FROM LabelEntity o ORDER BY o.labelPriority, o.labelId"),
    @NamedQuery(name = "LabelEntity.findByName", query = "SELECT o FROM LabelEntity o WHERE o.labelName = :labelName")})
public class LabelEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "label_id")
    private Long labelId;

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 128)
    @Column(name = "label_name")
    private String labelName;

    @Size(max = 8)
    @Column(name = "font_color")
    private String fontColor;

    @Size(max = 8)
    @Column(name = "back_color")
    private String backColor;

    @Column(name = "label_priority")
    private Integer labelPriority;

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo;

    /**
     * コンストラクタ
     */
    public LabelEntity() {
    }

    /**
     * コンストラクタ
     * 
     * @param labelName ラベル名
     */
    public LabelEntity(String labelName) {
        this.labelName = labelName;
    }

    /**
     * コンストラクタ
     * 
     * @param labelName ラベル名
     * @param fontColor 文字色
     * @param backColor 背景色
     * @param labelPriority 優先度
     */
    public LabelEntity(String labelName, String fontColor, String backColor, Integer labelPriority) {
        this.labelName = labelName;
        this.fontColor = fontColor;
        this.backColor = backColor;
        this.labelPriority = labelPriority;
    }
    
    /**
     * ラベルIDを取得する。
     * 
     * @return ラベルID
     */
    public Long getLabelId() {
        return labelId;
    }

    /**
     * ラベルIDを設定する。
     * 
     * @param labelId ラベルID
     */
    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    /**
     * ラベル名を取得する。
     * 
     * @return ラベル名
     */
    public String getLabelName() {
        return labelName;
    }

    /**
     * ラベル名を設定する。
     * 
     * @param labelName ラベル名 
     */
    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    /**
     * 文字色を取得する。
     * 
     * @return 文字色
     */
    public String getFontColor() {
        return fontColor;
    }

    /**
     * 文字色を設定する。
     * 
     * @param fontColor 文字色 
     */
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * 背景色を取得する。
     * 
     * @return 背景色
     */
    public String getBackColor() {
        return backColor;
    }

    /**
     * 背景色を設定する。
     * 
     * @param backColor 背景色 
     */
    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    /**
     * 優先度を取得する。
     * 
     * @return 優先度
     */
    public Integer getLabelPriority() {
        return labelPriority;
    }

    /**
     * 優先度を設定する。
     * 
     * @param labelPriority 優先度 
     */
    public void setLabelPriority(Integer labelPriority) {
        this.labelPriority = labelPriority;
    }

    /**
     * 排他用バージョンを取得する。
     * 
     * @return 排他用バージョン
     */
    public Integer getVerInfo() {
        return verInfo;
    }

    /**
     * 排他用バージョンを設定する。
     * 
     * @param verInfo 排他用バージョン
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * ハッシュコードを取得する。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.labelId);
        hash = 53 * hash + Objects.hashCode(this.labelName);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true: 等しい(同値)、false: 異なる
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
        final LabelEntity other = (LabelEntity) obj;
        if (!Objects.equals(this.labelId, other.labelId)) {
            return false;
        }
        return Objects.equals(this.labelName, other.labelName);
    }

   /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("LabelEntity{")
                .append("labelId=").append(this.labelId)
                .append(", labelName=").append(this.labelName)
                .append(", fontColor=").append(this.fontColor)
                .append(", backColor=").append(this.backColor)
                .append(", labelPriority=").append(this.labelPriority)
                .append("}")
                .toString();
    }
}
