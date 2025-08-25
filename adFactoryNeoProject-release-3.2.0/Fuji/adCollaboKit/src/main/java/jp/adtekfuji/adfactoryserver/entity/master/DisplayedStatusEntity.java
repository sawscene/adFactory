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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_displayed_status")
@XmlRootElement(name = "displayedStatus")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "DisplayedStatusEntity.findAll", query = "SELECT d FROM DisplayedStatusEntity d"),
    @NamedQuery(name = "DisplayedStatusEntity.findByStatusId", query = "SELECT d FROM DisplayedStatusEntity d WHERE d.statusId = :statusId"),
    @NamedQuery(name = "DisplayedStatusEntity.findByStatusName", query = "SELECT d FROM DisplayedStatusEntity d WHERE d.statusName = :statusName"),
    @NamedQuery(name = "DisplayedStatusEntity.findByFontColor", query = "SELECT d FROM DisplayedStatusEntity d WHERE d.fontColor = :fontColor"),
    @NamedQuery(name = "DisplayedStatusEntity.findByBackColor", query = "SELECT d FROM DisplayedStatusEntity d WHERE d.backColor = :backColor"),
    @NamedQuery(name = "DisplayedStatusEntity.findByLightPattern", query = "SELECT d FROM DisplayedStatusEntity d WHERE d.lightPattern = :lightPattern")})
public class DisplayedStatusEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "status_id")
    private Long statusId;
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "status_name")
    private StatusPatternEnum statusName;
    @Size(max = 128)
    @Column(name = "font_color")
    private String fontColor;
    @Size(max = 128)
    @Column(name = "back_color")
    private String backColor;
    @Enumerated(EnumType.STRING)
    @Column(name = "light_pattern")
    private LightPatternEnum lightPattern;
    @Size(max = 256)
    @Column(name = "notation_name")
    private String notationName = "";
    @Column(name = "melody_path", length = 32672)
    private String melodyPath = "";
    @Column(name = "melody_repeat")
    private Boolean melodyRepeat = false;

    public DisplayedStatusEntity() {
    }

    public DisplayedStatusEntity(StatusPatternEnum statusName, String fontColor, String backColor, LightPatternEnum lightPattern) {
        this.statusName = statusName;
        this.fontColor = fontColor;
        this.backColor = backColor;
        this.lightPattern = lightPattern;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public StatusPatternEnum getStatusName() {
        return statusName;
    }

    public void setStatusName(StatusPatternEnum statusName) {
        this.statusName = statusName;
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

    public String getNotationName() {
        return notationName;
    }

    public void setNotationName(String notationName) {
        this.notationName = notationName;
    }

    public String getMelodyPath() {
        return melodyPath;
    }

    public void setMelodyPath(String melodyPath) {
        this.melodyPath = melodyPath;
    }

    public Boolean getMelodyRepeat() {
        return melodyRepeat;
    }

    public void setMelodyRepeat(Boolean melodyRepeat) {
        this.melodyRepeat = melodyRepeat;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (statusId != null ? statusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DisplayedStatusEntity)) {
            return false;
        }
        DisplayedStatusEntity other = (DisplayedStatusEntity) object;
        if ((this.statusId == null && other.statusId != null) || (this.statusId != null && !this.statusId.equals(other.statusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DisplayedStatusEntity{" + "statusId=" + statusId + ", statusName=" + statusName + ", fontColor=" + fontColor + ", backColor=" + backColor + ", lightPattern=" + lightPattern + ", notationName=" + notationName + ", melodyPath=" + melodyPath + ", melodyRepeat=" + melodyRepeat + '}';
    }

}
