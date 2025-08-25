/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.master;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 休憩マスタ
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "breaktime")
public class BreakTimeInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty breaktimeIdProperty;
    private StringProperty breaktimeNameProperty;
    private ObjectProperty<Date> starttimeProperty;
    private ObjectProperty<Date> endtimeProperty;

    @XmlElement(required = true)
    private Long breaktimeId;// 休憩ID
    @XmlElement()
    private String breaktimeName;// 休憩名称
    @XmlElement()
    private Date starttime;// 開始時間
    @XmlElement()
    private Date endtime;// 終了時間

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    /**
     * コンストラクタ
     */
    public BreakTimeInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param in 休憩マスタ
     */
    public BreakTimeInfoEntity(BreakTimeInfoEntity in) {
        this.breaktimeId = in.breaktimeId;
        this.breaktimeName = in.breaktimeName;
        this.starttime = in.starttime;
        this.endtime = in.endtime;
        this.verInfo = in.verInfo;
    }

    /**
     * コンストラクタ
     *
     * @param breaktimeId 休憩ID
     * @param breaktimeName 休憩名称
     */
    public BreakTimeInfoEntity(Long breaktimeId, String breaktimeName) {
        this.breaktimeId = breaktimeId;
        this.breaktimeName = breaktimeName;
    }

    /**
     * コンストラクタ
     *
     * @param breaktimeId 休憩ID
     * @param breaktimeName 休憩名称
     * @param starttime 開始時間
     * @param endtime 終了時間
     */
    public BreakTimeInfoEntity(Long breaktimeId, String breaktimeName, Date starttime, Date endtime) {
        this.breaktimeId = breaktimeId;
        this.breaktimeName = breaktimeName;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    /**
     * コンストラクタ
     *
     * @param breaktimeName 休憩名称
     * @param starttime 開始時間
     * @param endtime 終了時間
     */
    public BreakTimeInfoEntity(String breaktimeName, Date starttime, Date endtime) {
        this.breaktimeName = breaktimeName;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    /**
     * 休憩IDプロパティを取得する。
     *
     * @return 休憩ID
     */
    public LongProperty breaktimeIdProperty() {
        if (Objects.isNull(this.breaktimeIdProperty)) {
            this.breaktimeIdProperty = new SimpleLongProperty(this.breaktimeId);
        }
        return this.breaktimeIdProperty;
    }

    /**
     * 休憩名称プロパティを取得する。
     *
     * @return 休憩名称
     */
    public StringProperty breaktimeNameProperty() {
        if (Objects.isNull(this.breaktimeNameProperty)) {
            this.breaktimeNameProperty = new SimpleStringProperty(this.breaktimeName);
        }
        return this.breaktimeNameProperty;
    }

    /**
     * 開始時間プロパティを取得する。
     *
     * @return 開始時間
     */
    public ObjectProperty<Date> starttimeProperty() {
        if (Objects.isNull(starttimeProperty)) {
            starttimeProperty = new SimpleObjectProperty<>(starttime);
        }
        return starttimeProperty;
    }

    /**
     * 終了時間プロパティを取得する。
     *
     * @return 終了時間
     */
    public ObjectProperty<Date> endtimeProperty() {
        if (Objects.isNull(endtimeProperty)) {
            endtimeProperty = new SimpleObjectProperty<>(endtime);
        }
        return endtimeProperty;
    }

    /**
     * 休憩IDを取得する。
     *
     * @return 休憩ID
     */
    public Long getBreaktimeId() {
        if (Objects.nonNull(breaktimeIdProperty)) {
            return breaktimeIdProperty.get();
        }
        return breaktimeId;
    }

    /**
     * 休憩IDを設定する。
     *
     * @param breaktimeId 休憩ID
     */
    public void setBreaktimeId(Long breaktimeId) {
        if (Objects.nonNull(breaktimeIdProperty)) {
            breaktimeIdProperty.set(breaktimeId);
        } else {
            this.breaktimeId = breaktimeId;
        }
    }

    /**
     * 休憩名称を取得する。
     *
     * @return 休憩名称
     */
    public String getBreaktimeName() {
        if (Objects.nonNull(this.breaktimeNameProperty)) {
            return this.breaktimeNameProperty.get();
        }
        return this.breaktimeName;
    }

    /**
     * 休憩名称を設定する。
     *
     * @param breaktimeName 休憩名称
     */
    public void setBreaktimeName(String breaktimeName) {
        if (Objects.nonNull(this.breaktimeNameProperty)) {
            this.breaktimeNameProperty.set(breaktimeName);
        } else {
            this.breaktimeName = breaktimeName;
        }
    }

    /**
     * 開始時間を取得する。
     *
     * @return 開始時間
     */
    public Date getStarttime() {
        if (Objects.nonNull(this.starttimeProperty)) {
            return this.starttimeProperty.get();
        }
        return this.starttime;
    }

    /**
     * 開始時間を設定する。
     *
     * @param starttime 開始時間
     */
    public void setStarttime(Date starttime) {
        if (Objects.nonNull(this.starttimeProperty)) {
            this.starttimeProperty.set(starttime);
        } else {
            this.starttime = starttime;
        }
    }

    /**
     * 終了時間を取得する。
     *
     * @return 終了時間
     */
    public Date getEndtime() {
        if (Objects.nonNull(this.endtimeProperty)) {
            return this.endtimeProperty.get();
        }
        return this.endtime;
    }

    /**
     * 終了時間を設定する。
     *
     * @param endtime 終了時間
     */
    public void setEndtime(Date endtime) {
        if (Objects.nonNull(this.endtimeProperty)) {
            this.endtimeProperty.set(endtime);
        } else {
            this.endtime = endtime;
        }
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
     * 内部変数を更新する。
     */
    public void updateData() {
        this.breaktimeName = this.getBreaktimeName();
        this.starttime = this.getStarttime();
        this.endtime = this.getEndtime();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.breaktimeId ^ (this.breaktimeId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.breaktimeName);
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
        final BreakTimeInfoEntity other = (BreakTimeInfoEntity) obj;
        if (!Objects.equals(this.getBreaktimeId(), other.getBreaktimeId())
                || !Objects.equals(this.getBreaktimeName(), other.getBreaktimeName())
                || !Objects.equals(this.getStarttime(), other.getStarttime())
                || !Objects.equals(this.getEndtime(), other.getEndtime())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("BreaktimeEntity{")
                .append("breaktimeId=").append(this.breaktimeId)
                .append(", ")
                .append("breaktimeName=").append(this.breaktimeName)
                .append(", ")
                .append("starttime=").append(this.starttime)
                .append(", ")
                .append("endtime=").append(this.endtime)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
