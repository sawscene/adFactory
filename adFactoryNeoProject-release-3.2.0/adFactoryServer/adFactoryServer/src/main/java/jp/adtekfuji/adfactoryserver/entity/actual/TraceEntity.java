/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 品質トレーサビリティ情報
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "trace")
@Entity
public class TraceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "actual_id")
    private Long actualId;// 実績ID

    @Id
    @Column(name = "disp")
    private Integer disp;// 表示順

    @Size(max = 256)
    @Column(name = "work_name")
    private String workName;// 工程名

    @Size(max = 256)
    @Column(name = "tag_name")
    private String tagName;// タグ名

    @Column(name = "date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;// 実施日時

    @Column(name = "trace_value")
    private String traceValue;// トレース値

    /**
     * コンストラクタ
     */
    public TraceEntity() {
    }

    /**
     * 実績IDを取得する。
     *
     * @return 実績ID
     */
    public Long getActualId() {
        return this.actualId;
    }

    /**
     * 実績IDを設定する。
     *
     * @param actualId 実績ID
     */
    public void setActualId(Long actualId) {
        this.actualId = actualId;
    }

    /**
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public Integer getDisp() {
        return this.disp;
    }

    /**
     * 表示順を設定する。
     *
     * @param disp 表示順
     */
    public void setDisp(Integer disp) {
        this.disp = disp;
    }

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 工程名を設定する。
     *
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * タグ名を取得する。
     *
     * @return タグ名
     */
    public String getTagName() {
        return this.tagName;
    }

    /**
     * タグ名を設定する。
     *
     * @param tagName タグ名
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * 実施日時を取得する。
     *
     * @return 実施日時
     */
    public Date getDateTime() {
        return this.dateTime;
    }

    /**
     * 実施日時を設定する。
     *
     * @param dateTime 実施日時
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * トレース値を取得する。
     *
     * @return トレース値
     */
    public String getTraceValue() {
        return this.traceValue;
    }

    /**
     * トレース値を設定する。
     *
     * @param traceValue トレース値
     */
    public void setTraceValue(String traceValue) {
        this.traceValue = traceValue;
    }

    @Override
    public String toString() {
        return new StringBuilder("TraceEntity{")
                .append("actualId=").append(this.actualId)
                .append(", ")
                .append("disp=").append(this.disp)
                .append(", ")
                .append("workName=").append(this.workName)
                .append(", ")
                .append("tagName=").append(this.tagName)
                .append(", ")
                .append("dateTime=").append(this.dateTime)
                .append(", ")
                .append("traceValue=").append(this.traceValue)
                .append("}")
                .toString();
    }
}
