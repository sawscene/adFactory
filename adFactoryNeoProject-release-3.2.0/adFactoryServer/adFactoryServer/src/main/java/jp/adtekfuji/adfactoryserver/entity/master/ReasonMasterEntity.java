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
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;

/**
 * 理由マスタ
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "mst_reason")
@XmlRootElement(name = "reason")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 理由種別・理由を指定して、件数を取得する。(追加時の理由重複チェック)
    @NamedQuery(name = "ReasonMasterEntity.checkAddByReason", query = "SELECT COUNT(r.reasonId) FROM ReasonMasterEntity r WHERE r.reasonType = :reasonType AND r.reason = :reason"),
    // 理由種別・理由・理由区分IDを指定して、件数を取得する。(追加時の理由重複チェック)
    @NamedQuery(name = "ReasonMasterEntity.checkAddByReasonCategoryId", query = "SELECT COUNT(r.reasonId) FROM ReasonMasterEntity r WHERE r.reasonType = :reasonType AND r.reason = :reason AND r.reasonCategoryId = :reasonCategoryId"),
    // 理由種別・理由・理由IDを指定して、件数を取得する。(更新時の理由重複チェック)
    @NamedQuery(name = "ReasonMasterEntity.checkUpdateByReason", query = "SELECT COUNT(r.reasonId) FROM ReasonMasterEntity r WHERE r.reasonType = :reasonType AND r.reason = :reason AND r.reasonId != :reasonId"),
    // 理由種別・理由・理由ID・理由区分IDを指定して、件数を取得する。(更新時の理由重複チェック)
    @NamedQuery(name = "ReasonMasterEntity.checkUpdateByReasonCategoryId", query = "SELECT COUNT(r.reasonId) FROM ReasonMasterEntity r WHERE r.reasonType = :reasonType AND r.reason = :reason AND r.reasonId != :reasonId AND r.reasonCategoryId = :reasonCategoryId"),
    // 理由種別・理由IDを指定して、理由を取得する。
    @NamedQuery(name = "ReasonMasterEntity.findByTypeAndReasonId", query = "SELECT r FROM ReasonMasterEntity r WHERE r.reasonType = :reasonType AND r.reasonId = :reasonId"),
    // 理由種別・理由IDを指定して、理由を削除する。
    @NamedQuery(name = "ReasonMasterEntity.removeByType", query = "DELETE FROM ReasonMasterEntity r WHERE r.reasonType = :reasonType AND r.reasonId = :reasonId"),
    // 理由種別を指定して、件数を取得する。
    @NamedQuery(name = "ReasonMasterEntity.countByType", query = "SELECT COUNT(r.reasonId) FROM ReasonMasterEntity r WHERE r.reasonType = :reasonType"),
    // 理由種別を指定して、理由一覧を取得する。
    @NamedQuery(name = "ReasonMasterEntity.findByType", query = "SELECT r FROM ReasonMasterEntity r WHERE r.reasonType = :reasonType ORDER BY r.reasonId"),
    // 理由区分IDを指定して、理由一覧を取得する。
    @NamedQuery(name = "ReasonMasterEntity.findByCategoryId", query = "SELECT r FROM ReasonMasterEntity r JOIN ReasonCategoryEntity rc ON r.reasonCategoryId = rc.reasonCategoryId WHERE rc.reasonCategoryId IN :reasonCategoryIds ORDER BY r.reasonId"),
    // 理由区分名を指定して、理由一覧を取得する。
    @NamedQuery(name = "ReasonMasterEntity.findByCategoryName", query = "SELECT r FROM ReasonMasterEntity r JOIN ReasonCategoryEntity rc ON r.reasonCategoryId = rc.reasonCategoryId WHERE rc.reasonCategoryName = :reasonCategoryName AND rc.reasonType = :reasonType ORDER BY r.reasonId"),
    // 理由区分IDを指定して、件数を取得する。
    @NamedQuery(name = "ReasonMasterEntity.countByCategory", query = "SELECT COUNT(i.reasonId) FROM ReasonMasterEntity i WHERE i.reasonCategoryId IN :reasonCategoryIds"),
})
public class ReasonMasterEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "reason_id")
    @XmlElement(name = "id")
    private Long reasonId;// 理由ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "reason_type")
    @XmlElement(name = "reason_type")
    private ReasonTypeEnum reasonType;// 理由種別

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "reason")
    private String reason;// 理由

    @Size(max = 128)
    @Column(name = "font_color")
    private String fontColor;// 文字色

    @Size(max = 128)
    @Column(name = "back_color")
    private String backColor;// 背景色

    @Enumerated(EnumType.STRING)
    @Column(name = "light_pattern")
    private LightPatternEnum lightPattern;// 点灯パターン

    @Basic(optional = false)
    @Column(name = "reason_order")
    @XmlElement(name = "reason_order")
    private Long reasonOrder;// 理由オーダー

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション
    
    @Basic(optional = false)
    @Column(name = "reason_category_id")
    @XmlElement(name = "reason_category_id")
    private Long reasonCategoryId;// 理由区分ID

    /**
     * コンストラクタ
     */
    public ReasonMasterEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param reasonType 理由種別
     * @param reason 理由
     * @param fontColor 文字色
     * @param backColor 背景色
     * @param lightPattern  点灯パターン
     */
    public ReasonMasterEntity(ReasonTypeEnum reasonType, String reason, String fontColor, String backColor, LightPatternEnum lightPattern) {
        this.reasonType = reasonType;
        this.reason = reason;
        this.fontColor = fontColor;
        this.backColor = backColor;
        this.lightPattern = lightPattern;
    }
    
    /**
     * コンストラクタ
     *
     * @param reasonType 理由種別
     * @param reason 理由
     * @param fontColor 文字色
     * @param backColor 背景色
     * @param lightPattern  点灯パターン
     * @param reasonCategoryId 理由区分Id
     */
    public ReasonMasterEntity(ReasonTypeEnum reasonType, String reason, String fontColor, String backColor, LightPatternEnum lightPattern, Long reasonCategoryId) {
        this.reasonType = reasonType;
        this.reason = reason;
        this.fontColor = fontColor;
        this.backColor = backColor;
        this.lightPattern = lightPattern;
        this.reasonCategoryId = reasonCategoryId;
    }

    /**
     * 理由マスタを継承しているクラスに変換する。
     *
     * @param <T> 
     * @param destClass 理由マスタを継承しているクラス
     * @return 理由マスタを継承しているクラスの理由情報
     * @throws Exception 
     */
    public <T extends ReasonMasterEntity> T downcast(Class<T> destClass) throws Exception {
        T obj = destClass.newInstance();
        obj.setReasonId(this.reasonId);
        obj.setReasonType(this.reasonType);
        obj.setReason(this.reason);
        obj.setFontColor(this.fontColor);
        obj.setBackColor(this.backColor);
        obj.setLightPattern(this.lightPattern);
        obj.setReasonOrder(this.reasonOrder);
        obj.setVerInfo(this.verInfo);
        obj.setReasonCategoryId(this.reasonCategoryId);
        return obj;
    }

    /**
     * 理由IDを取得する。
     *
     * @return 理由ID
     */
    public Long getReasonId() {
        return this.reasonId;
    }

    /**
     * 理由IDを設定する。
     *
     * @param reasonId 理由ID
     */
    public void setReasonId(Long reasonId) {
        this.reasonId = reasonId;
    }

    /**
     * 理由種別を取得する。
     *
     * @return 理由種別
     */
    public ReasonTypeEnum getReasonType() {
        return this.reasonType;
    }

    /**
     * 理由種別を設定する。
     *
     * @param type 理由種別
     */
    public void setReasonType(ReasonTypeEnum type) {
        this.reasonType = type;
    }

    /**
     * 理由を取得する。
     *
     * @return 理由
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * 理由を設定する。
     *
     * @param reason 理由
     */
    public void setReason(String reason) {
        this.reason = reason;
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
     * 点灯パターンを取得する。
     *
     * @return 点灯パターン
     */
    public LightPatternEnum getLightPattern() {
        return lightPattern;
    }

    /**
     * 点灯パターンを設定する。
     *
     * @param lightPattern 点灯パターン
     */
    public void setLightPattern(LightPatternEnum lightPattern) {
        this.lightPattern = lightPattern;
    }

    /**
     * 理由オーダーを取得する。
     *
     * @return 理由オーダー
     */
    public Long getReasonOrder() {
        return this.reasonOrder;
    }

    /**
     * 理由オーダーを設定する。
     *
     * @param order 理由オーダー
     */
    public void setReasonOrder(Long order) {
        this.reasonOrder = order;
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
     * 理由区分IDを取得する。
     *
     * @return 理由区分ID
     */
    public Long getReasonCategoryId() {
        return this.reasonCategoryId;
    }

    /**
     * 理由区分IDを設定する。
     *
     * @param reasonCategoryId 理由区分ID
     */
    public void setReasonCategoryId(Long reasonCategoryId) {
        this.reasonCategoryId = reasonCategoryId;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.reasonId);
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
        final ReasonMasterEntity other = (ReasonMasterEntity) obj;
        return Objects.equals(this.reasonId, other.reasonId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */    
    @Override
    public String toString() {
        return new StringBuilder("ReasonMasterEntity{")
                .append("reasonId=").append(this.reasonId)
                .append(", reasonType=").append(this.reasonType)
                .append(", reason=").append(this.reason)
                .append(", fontColor=").append(this.fontColor)
                .append(", backColor=").append(this.backColor)
                .append(", lightPattern=").append(this.lightPattern)
                .append(", reasonOrder=").append(this.reasonOrder)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
