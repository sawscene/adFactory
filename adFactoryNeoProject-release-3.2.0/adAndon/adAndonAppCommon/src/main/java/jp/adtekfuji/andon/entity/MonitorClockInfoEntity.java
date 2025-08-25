/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.andon.enumerate.HorizonAlignmentTypeEnum;

/**
 * 進捗モニタ 時計情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "monitorClockInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitorClockInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clockFormat;// 日時フォーマット
    private HorizonAlignmentTypeEnum horizonAlignment;// 水平位置

    /**
     * コンストラクタ
     */
    public MonitorClockInfoEntity() {
    }

    /**
     * 日時フォーマットを設定して、時計情報を取得する。
     *
     * @param clockFormat 日時フォーマット
     * @return 時計情報
     */
    public MonitorClockInfoEntity clockFormat(String clockFormat) {
        this.clockFormat = clockFormat;
        return this;
    }

    /**
     * 水平位置を設定して、時計情報を取得する。
     *
     * @param horizonAlignment 水平位置
     * @return 時計情報
     */
    public MonitorClockInfoEntity horizonAlignment(HorizonAlignmentTypeEnum horizonAlignment) {
        this.horizonAlignment = horizonAlignment;
        return this;
    }

    /**
     * 日時フォーマットを取得する。
     *
     * @return 日時フォーマット
     */
    public String getClockFormat() {
        return this.clockFormat;
    }

    /**
     * 日時フォーマットを設定する。
     *
     * @param clockFormat 日時フォーマット
     */
    public void setClockFormat(String clockFormat) {
        this.clockFormat = clockFormat;
    }

    /**
     * 水平位置を取得する。
     *
     * @return 水平位置
     */
    public HorizonAlignmentTypeEnum getHorizonAlignment() {
        return this.horizonAlignment;
    }

    /**
     * 水平位置を設定する。
     *
     * @param horizonAlignment 水平位置
     */
    public void setHorizonAlignment(HorizonAlignmentTypeEnum horizonAlignment) {
        this.horizonAlignment = horizonAlignment;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.clockFormat);
        hash = 79 * hash + Objects.hashCode(this.horizonAlignment);
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
        final MonitorClockInfoEntity other = (MonitorClockInfoEntity) obj;
        if (!Objects.equals(this.clockFormat, other.clockFormat)) {
            return false;
        }
        if (this.horizonAlignment != other.horizonAlignment) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorClockInfoEntity{")
                .append("clockFormat=").append(this.clockFormat)
                .append(", ")
                .append("horizonAlignment=").append(this.horizonAlignment)
                .append("}")
                .toString();
    }
}
