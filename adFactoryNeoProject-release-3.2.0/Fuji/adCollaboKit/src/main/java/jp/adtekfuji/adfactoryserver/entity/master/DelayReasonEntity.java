/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.master;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_delay_reason")
@XmlRootElement(name = "delayReason")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "DelayReasonEntity.findAll", query = "SELECT d FROM DelayReasonEntity d"),
    @NamedQuery(name = "DelayReasonEntity.findByDelayId", query = "SELECT d FROM DelayReasonEntity d WHERE d.delayId = :delayId"),
    @NamedQuery(name = "DelayReasonEntity.findByDelayReason", query = "SELECT d FROM DelayReasonEntity d WHERE d.delayReason = :delayReason"),
    @NamedQuery(name = "DelayReasonEntity.findByFontColor", query = "SELECT d FROM DelayReasonEntity d WHERE d.fontColor = :fontColor"),
    @NamedQuery(name = "DelayReasonEntity.findByBackColor", query = "SELECT d FROM DelayReasonEntity d WHERE d.backColor = :backColor"),
    @NamedQuery(name = "DelayReasonEntity.findByLightPattern", query = "SELECT d FROM DelayReasonEntity d WHERE d.lightPattern = :lightPattern")})
public class DelayReasonEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "delay_id")
    private Long delayId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "delay_reason")
    private String delayReason;
    @Size(max = 128)
    @Column(name = "font_color")
    private String fontColor;
    @Size(max = 128)
    @Column(name = "back_color")
    private String backColor;
    @Enumerated(EnumType.STRING)
    @Column(name = "light_pattern")
    private LightPatternEnum lightPattern;

    public DelayReasonEntity() {
    }

    public DelayReasonEntity(String delayReason, String fontColor, String backColor, LightPatternEnum lightPattern) {
        this.delayReason = delayReason;
        this.fontColor = fontColor;
        this.backColor = backColor;
        this.lightPattern = lightPattern;
    }

    public Long getDelayId() {
        return delayId;
    }

    public void setDelayId(Long delayId) {
        this.delayId = delayId;
    }

    public String getDelayReason() {
        return delayReason;
    }

    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public LightPatternEnum getLightPattern() {
        return lightPattern;
    }

    public void setLightPattern(LightPatternEnum lightPattern) {
        this.lightPattern = lightPattern;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (delayId != null ? delayId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DelayReasonEntity)) {
            return false;
        }
        DelayReasonEntity other = (DelayReasonEntity) object;
        if ((this.delayId == null && other.delayId != null) || (this.delayId != null && !this.delayId.equals(other.delayId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DelayReasonEntity{" + "delayId=" + delayId + ", delayReason=" + delayReason + ", fontColor=" + fontColor + ", backColor=" + backColor + ", lightPattern=" + lightPattern + '}';
    }

}
