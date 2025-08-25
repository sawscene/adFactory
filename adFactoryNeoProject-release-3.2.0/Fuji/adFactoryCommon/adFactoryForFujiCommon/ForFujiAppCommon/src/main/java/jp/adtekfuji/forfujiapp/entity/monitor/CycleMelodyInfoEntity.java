/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.monitor;

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * サイクルメロディ情報 (サイクルタクトタイムフレーム用)
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "cycleMelody")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CycleMelodyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private ObjectProperty<CycleMelodyTypeEnum> melodyTypeProperty = null;
    private StringProperty melodyPathProperty = null;

    private CycleMelodyTypeEnum melodyType;
    private String melodyPath;

    /**
     * サイクルメロディ情報 (サイクルタクトタイムフレーム用)
     */
    public CycleMelodyInfoEntity() {
    }

    /**
     * サイクルメロディ情報 (サイクルタクトタイムフレーム用)
     *
     * @param melodyType メロディ種別
     * @param melodyPath メロディパス
     */
    public CycleMelodyInfoEntity(CycleMelodyTypeEnum melodyType, String melodyPath) {
        this.melodyType = melodyType;
        this.melodyPath = melodyPath;
    }

    /**
     * メロディ種別のプロパティ値を取得する。
     *
     * @return メロディ種別のプロパティ値
     */
    public ObjectProperty melodyTypeProperty() {
        if (Objects.isNull(this.melodyTypeProperty)) {
            this.melodyTypeProperty = new SimpleObjectProperty<>(this.melodyType);
        }
        return this.melodyTypeProperty;
    }

    /**
     * メロディパスのプロパティ値を取得する。
     *
     * @return メロディパスのプロパティ値
     */
    public StringProperty melodyPathProperty() {
        if (Objects.isNull(this.melodyPathProperty)) {
            this.melodyPathProperty = new SimpleStringProperty(this.melodyPath);
        }
        return this.melodyPathProperty;
    }

    /**
     * メロディ種別を取得する。
     *
     * @return メロディ種別
     */
    public CycleMelodyTypeEnum getMelodyType() {
        if (Objects.nonNull(this.melodyTypeProperty)) {
            return this.melodyTypeProperty.get();
        }
        return this.melodyType;
    }

    /**
     * メロディ種別を設定する。
     *
     * @param melodyType メロディ種別
     */
    public void setMelodyType(CycleMelodyTypeEnum melodyType) {
        if (Objects.nonNull(this.melodyTypeProperty)) {
            this.melodyTypeProperty.set(melodyType);
        } else {
            this.melodyType = melodyType;
        }
    }

    /**
     * メロディパスを取得する。
     *
     * @return メロディパス
     */
    public String getMelodyPath() {
        if (Objects.nonNull(this.melodyPathProperty)) {
            return this.melodyPathProperty.get();
        }
        return this.melodyPath;
    }

    /**
     * メロディパスを設定する。
     *
     * @param melodyPath メロディパス
     */
    public void setMelodyPath(String melodyPath) {
        if (Objects.nonNull(this.melodyPathProperty)) {
            this.melodyPathProperty.set(melodyPath);
        } else {
            this.melodyPath = melodyPath;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.getMelodyType());
        hash = 31 * hash + Objects.hashCode(this.getMelodyPath());
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
        final CycleMelodyInfoEntity other = (CycleMelodyInfoEntity) obj;
        if (!Objects.equals(this.getMelodyType(), other.getMelodyType())) {
            return false;
        }
        if (!Objects.equals(this.getMelodyPath(), other.getMelodyPath())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CycleMelodyInfoEntity{" + "melodyType=" + getMelodyType() + ", melodyPath=" + getMelodyPath() + '}';
    }
    
    @Override
    public CycleMelodyInfoEntity clone() {
        CycleMelodyInfoEntity entity = new CycleMelodyInfoEntity();
        
        entity.setMelodyPath(getMelodyPath());
        entity.setMelodyType(getMelodyType());
        
        return entity;
    }
        
    public boolean equalsDisplayInfo(CycleMelodyInfoEntity other) {
        if (Objects.equals(this.getMelodyPath(), other.getMelodyPath())
                && Objects.equals(this.getMelodyType(), other.getMelodyType())) {
            return true;
        }
        return false;
    }
}
