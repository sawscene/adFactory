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
@Table(name = "mst_interrupt_reason")
@XmlRootElement(name = "interruptReason")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "InterruptReasonEntity.findAll", query = "SELECT i FROM InterruptReasonEntity i"),
    @NamedQuery(name = "InterruptReasonEntity.findByInterruptId", query = "SELECT i FROM InterruptReasonEntity i WHERE i.interruptId = :interruptId"),
    @NamedQuery(name = "InterruptReasonEntity.findByInterruptReason", query = "SELECT i FROM InterruptReasonEntity i WHERE i.interruptReason = :interruptReason"),
    @NamedQuery(name = "InterruptReasonEntity.findByFontColor", query = "SELECT i FROM InterruptReasonEntity i WHERE i.fontColor = :fontColor"),
    @NamedQuery(name = "InterruptReasonEntity.findByBackColor", query = "SELECT i FROM InterruptReasonEntity i WHERE i.backColor = :backColor"),
    @NamedQuery(name = "InterruptReasonEntity.findByLightPattern", query = "SELECT i FROM InterruptReasonEntity i WHERE i.lightPattern = :lightPattern")})
public class InterruptReasonEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "interrupt_id")
    private Long interruptId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "interrupt_reason")
    private String interruptReason;
    @Size(max = 128)
    @Column(name = "font_color")
    private String fontColor;
    @Size(max = 128)
    @Column(name = "back_color")
    private String backColor;
    @Enumerated(EnumType.STRING)
    @Column(name = "light_pattern")
    private LightPatternEnum lightPattern;

    public InterruptReasonEntity() {
    }

    public InterruptReasonEntity(String interruptReason, String fontColor, String backColor, LightPatternEnum lightPattern) {
        this.interruptReason = interruptReason;
        this.fontColor = fontColor;
        this.backColor = backColor;
        this.lightPattern = lightPattern;
    }

    public Long getInterruptId() {
        return interruptId;
    }

    public void setInterruptId(Long interruptId) {
        this.interruptId = interruptId;
    }

    public String getInterruptReason() {
        return interruptReason;
    }

    public void setInterruptReason(String interruptReason) {
        this.interruptReason = interruptReason;
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
        hash += (interruptId != null ? interruptId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InterruptReasonEntity)) {
            return false;
        }
        InterruptReasonEntity other = (InterruptReasonEntity) object;
        if ((this.interruptId == null && other.interruptId != null) || (this.interruptId != null && !this.interruptId.equals(other.interruptId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "InterruptReasonEntity{" + "interruptId=" + interruptId + ", interruptReason=" + interruptReason + ", fontColor=" + fontColor + ", backColor=" + backColor + ", lightPattern=" + lightPattern + '}';
    }

}
