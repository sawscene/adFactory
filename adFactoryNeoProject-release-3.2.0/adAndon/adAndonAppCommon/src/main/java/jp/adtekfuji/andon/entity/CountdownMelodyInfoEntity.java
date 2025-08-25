/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import jp.adtekfuji.andon.enumerate.CountdownMelodyInfoTypeEnum;
import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * サイクルカウントダウンメロディ情報 (サイクルカウントダウンフレーム用)
 *
 * @author s-maeda
 */
@XmlRootElement(name = "countdownMelodyInfo")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CountdownMelodyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private ObjectProperty<CountdownMelodyInfoTypeEnum> melodyInfoTypeProperty = null;
    private StringProperty melodyInfoBodyProperty = null;

    private CountdownMelodyInfoTypeEnum melodyInfoType;
    private String melodyInfoBody;

    /**
     * サイクルカウントダウンメロディ情報 (サイクルカウントダウンフレーム用)
     */
    public CountdownMelodyInfoEntity() {
    }

    /**
     * サイクルカウントダウンメロディ情報 (サイクルカウントダウンフレーム用)
     *
     * @param melodyInfoType メロディ種別
     * @param melodyInfoBody メロディパス
     */
    public CountdownMelodyInfoEntity(CountdownMelodyInfoTypeEnum melodyInfoType, String melodyInfoBody) {
        this.melodyInfoType = melodyInfoType;
        this.melodyInfoBody = melodyInfoBody;
    }

    /**
     * メロディ情報種別のプロパティ値を取得する。
     *
     * @return メロディ情報種別のプロパティ値
     */
    public ObjectProperty melodyInfoTypeProperty() {
        if (Objects.isNull(this.melodyInfoTypeProperty)) {
            this.melodyInfoTypeProperty = new SimpleObjectProperty<>(this.melodyInfoType);
        }
        return this.melodyInfoTypeProperty;
    }

    /**
     * メロディ情報のプロパティ値を取得する。
     *
     * @return メロディパスのプロパティ値
     */
    public StringProperty melodyInfoBodyProperty() {
        if (Objects.isNull(this.melodyInfoBodyProperty)) {
            this.melodyInfoBodyProperty = new SimpleStringProperty(this.melodyInfoBody);
        }
        return this.melodyInfoBodyProperty;
    }

    /**
     * メロディ情報種別を取得する。
     *
     * @return メロディ情報種別
     */
    public CountdownMelodyInfoTypeEnum getMelodyInfoType() {
        if (Objects.nonNull(this.melodyInfoTypeProperty)) {
            return this.melodyInfoTypeProperty.get();
        }
        return this.melodyInfoType;
    }

    /**
     * メロディ情報種別を設定する。
     *
     * @param melodyInfoType メロディ情報種別
     */
    public void setMelodyInfoType(CountdownMelodyInfoTypeEnum melodyInfoType) {
        if (Objects.nonNull(this.melodyInfoTypeProperty)) {
            this.melodyInfoTypeProperty.set(melodyInfoType);
        } else {
            this.melodyInfoType = melodyInfoType;
        }
    }

    /**
     * メロディ情報を取得する。
     *
     * @return メロディ情報
     */
    public String getMelodyInfoBody() {
        if (Objects.nonNull(this.melodyInfoBodyProperty)) {
            return this.melodyInfoBodyProperty.get();
        }
        return this.melodyInfoBody;
    }

    /**
     * メロディ情報を設定する。
     *
     * @param melodyInfoBody メロディ情報
     */
    public void setMelodyInfoBody(String melodyInfoBody) {
        if (Objects.nonNull(this.melodyInfoBodyProperty)) {
            this.melodyInfoBodyProperty.set(melodyInfoBody);
        } else {
            this.melodyInfoBody = melodyInfoBody;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.getMelodyInfoType());
        hash = 31 * hash + Objects.hashCode(this.getMelodyInfoBody());
        return hash;
    }

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
        final CountdownMelodyInfoEntity other = (CountdownMelodyInfoEntity) obj;
        if (!Objects.equals(this.melodyInfoBody, other.melodyInfoBody)) {
            return false;
        }
        return this.melodyInfoType == other.melodyInfoType;
    }

    @Override
    public String toString() {
        return "CountdownMelodyInfoEntity{" + "melodyType=" + getMelodyInfoType()
                + ", melodyInfoBody=" + getMelodyInfoBody() + '}';
    }
    
    /**
     * 表示される内容をコピーして返す
     * 
     * @return 
     */
    @Override
    public CountdownMelodyInfoEntity clone() {
        CountdownMelodyInfoEntity entity = new CountdownMelodyInfoEntity();
        entity.setMelodyInfoBody(getMelodyInfoBody());
        entity.setMelodyInfoType(getMelodyInfoType());
        return entity;
    }

    /**
     * 表示される内容が一致するか調べえる
     * 
     * @param other
     * @return 
     */
    public boolean equalsDisplayInfo(CountdownMelodyInfoEntity other) {
        if(Objects.equals(getMelodyInfoBody(), other.getMelodyInfoBody())
                && Objects.equals(getMelodyInfoType(), other.getMelodyInfoType())) {
            return true;
        }
        return false;
    }
}
