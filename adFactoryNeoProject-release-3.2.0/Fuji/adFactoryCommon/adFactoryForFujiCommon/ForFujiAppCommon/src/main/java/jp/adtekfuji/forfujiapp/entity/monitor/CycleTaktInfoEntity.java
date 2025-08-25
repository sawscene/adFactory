/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.monitor;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * サイクルタクト情報 (サイクルタクトタイムフレーム用)
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "cycleTakt")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CycleTaktInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty cycleNoProperty = null;
    private ObjectProperty<Date> taktTimeProperty = null;

    private Long cycleNo;
    private Date taktTime;

    /**
     * サイクルタクト情報 (サイクルタクトタイムフレーム用)
     */
    public CycleTaktInfoEntity() {
    }

    /**
     * サイクルタクト情報 (サイクルタクトタイムフレーム用)
     *
     * @param cycleNo
     * @param taktTime 
     */
    public CycleTaktInfoEntity(Long cycleNo, Date taktTime) {
        this.cycleNo = cycleNo;
        this.taktTime = taktTime;
    }

    /**
     * サイクル番号のプロパティ値を取得する。
     *
     * @return サイクル番号のプロパティ値
     */
    public LongProperty cycleNoProperty() {
        if (Objects.isNull(this.cycleNoProperty)) {
            this.cycleNoProperty = new SimpleLongProperty(this.cycleNo);
        }
        return this.cycleNoProperty;
    }

    /**
     * タクトタイムのプロパティ値を取得する。
     *
     * @return タクトタイムのプロパティ値
     */
    public ObjectProperty<Date> taktTimeProperty() {
        if (Objects.isNull(this.taktTimeProperty)) {
            this.taktTimeProperty = new SimpleObjectProperty<>(this.taktTime);
        }
        return this.taktTimeProperty;
    }

    /**
     * サイクル番号を取得する。
     *
     * @return サイクル番号
     */
    public Long getCycleNo() {
        if (Objects.nonNull(this.cycleNoProperty)) {
            return this.cycleNoProperty.get();
        }
        return this.cycleNo;
    }

    /**
     * サイクル番号を設定する。
     *
     * @param cycleNo サイクル番号
     */
    public void setCycleNo(Long cycleNo) {
        if (Objects.nonNull(this.cycleNoProperty)) {
            this.cycleNoProperty.set(cycleNo);
        } else {
            this.cycleNo = cycleNo;
        }
    }

    /**
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public Date getTaktTime() {
        if (Objects.nonNull(this.taktTimeProperty)) {
            return this.taktTimeProperty.get();
        }
        return this.taktTime;
    }

    /**
     * タクトタイムを設定する。
     *
     * @param taktTime タクトタイム
     */
    public void setTaktTime(Date taktTime) {
        if (Objects.nonNull(this.taktTimeProperty)) {
            this.taktTimeProperty.set(taktTime);
        } else {
            this.taktTime = taktTime;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.getCycleNo());
        hash = 37 * hash + Objects.hashCode(this.getTaktTime());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CycleTaktInfoEntity other = (CycleTaktInfoEntity) obj;
        if (!Objects.equals(this.getCycleNo(), other.getCycleNo())) {
            return false;
        }
        if (!Objects.equals(this.getTaktTime(), other.getTaktTime())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CycleTaktInfoEntity{" + "cycleNo=" + getCycleNo() + ", taktTime=" + getTaktTime() + '}';
    }
    
    @Override
    public CycleTaktInfoEntity clone() {
        CycleTaktInfoEntity entity = new CycleTaktInfoEntity();
        
        entity.setCycleNo(getCycleNo());
        entity.setTaktTime(getTaktTime());
        
        return entity;
    }
    
    public boolean equalsDisplayInfo(CycleTaktInfoEntity other) {
        if (Objects.equals(this.getCycleNo(), other.getCycleNo())
                && Objects.equals(this.getTaktTime(), other.getTaktTime())) {
            return true;
        }
        return false;
    }
}
