/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 進捗モニタ タクトタイム情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "monitorLineTaktInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitorLineTaktInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long taktTime = 0L;// タクトタイム

    /**
     * コンストラクタ
     */
    public MonitorLineTaktInfoEntity() {
    }

    /**
     * タクトタイムを設定して、タクトタイム情報を取得する。
     *
     * @param taktTime タクトタイム
     * @return タクトタイム情報
     */
    public MonitorLineTaktInfoEntity taktTime(Long taktTime) {
        this.taktTime = taktTime;
        return this;
    }

    /**
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public Long getTaktTime() {
        return this.taktTime;
    }

    /**
     * タクトタイムを設定する。
     *
     * @param taktTime タクトタイム
     */
    public void setTaktTime(Long taktTime) {
        this.taktTime = taktTime;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final MonitorLineTaktInfoEntity other = (MonitorLineTaktInfoEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorLineTaktInfoEntity{")
                .append("taktTime=").append(this.taktTime)
                .append("}")
                .toString();
    }
}
