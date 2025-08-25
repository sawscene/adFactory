/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.property;

import adtekfuji.utility.StringUtils;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 対象工程情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "workSettng")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class WorkSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    private IntegerProperty orderProperty = null;// 表示順
    private StringProperty titleProperty = null;// タイトル
    private StringProperty pluginNameProperty = null;// プラグイン名
    private IntegerProperty planNumProperty = null;// 計画数
    private StringProperty startWorkTimeProperty;// 開始時間
    private StringProperty endWorkTimeProperty;// 終了時間

    private Integer order = 0;// 表示順
    private String title = "";// タイトル
    private String pluginName = "";// プラグイン名
    private Integer planNum = 0;// 計画数
    private LocalTime startWorkTime;// 開始時間
    private LocalTime endWorkTime;// 終了時間

    private List<Long> workIds = new ArrayList();// 工程ID一覧

    /**
     * コンストラクタ
     */
    public WorkSetting() {
    }

    /**
     * 表示順プロパティを取得する。
     *
     * @return 表示順
     */
    public IntegerProperty orderProperty() {
        if (Objects.isNull(this.orderProperty)) {
            this.orderProperty = new SimpleIntegerProperty(this.order);
        }
        return this.orderProperty;
    }

    /**
     * タイトルプロパティを取得する。
     *
     * @return タイトル
     */
    public StringProperty titleProperty() {
        if (Objects.isNull(this.titleProperty)) {
            this.titleProperty = new SimpleStringProperty(this.title);
        }
        return this.titleProperty;
    }

    /**
     * プラグイン名プロパティを取得する。
     *
     * @return プラグイン名
     */
    public StringProperty pluginNameProperty() {
        if (Objects.isNull(this.pluginNameProperty)) {
            this.pluginNameProperty = new SimpleStringProperty(this.pluginName);
        }
        return this.pluginNameProperty;
    }

    /**
     * 計画数プロパティを取得する。
     *
     * @return 計画数
     */
    public IntegerProperty planNumProperty() {
        if (Objects.isNull(this.planNumProperty)) {
            this.planNumProperty = new SimpleIntegerProperty(this.planNum);
        }
        return this.planNumProperty;
    }

    /**
     * 開始時間プロパティを取得する。
     *
     * @return 開始時間
     */
    public StringProperty startWorkTimeProperty() {
        if (Objects.isNull(this.startWorkTimeProperty)) {
            if (Objects.nonNull(this.startWorkTime)) {
                this.startWorkTimeProperty = new SimpleStringProperty(DateTimeFormatter.ofPattern("HH:mm:ss").format(this.startWorkTime));
            } else {
                this.startWorkTimeProperty = new SimpleStringProperty();
            }
        }
        return this.startWorkTimeProperty;
    }

    /**
     * 終了時間プロパティを取得する。
     *
     * @return 終了時間
     */
    public StringProperty endWorkTimeProperty() {
        if (Objects.isNull(this.endWorkTimeProperty)) {
            if (Objects.nonNull(this.endWorkTime)) {
                this.endWorkTimeProperty = new SimpleStringProperty(DateTimeFormatter.ofPattern("HH:mm:ss").format(this.endWorkTime));
            } else {
                this.endWorkTimeProperty = new SimpleStringProperty();
            }
        }
        return this.endWorkTimeProperty;
    }

    /**
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public Integer getOrder() {
        if (Objects.nonNull(this.orderProperty)) {
            return this.orderProperty.get();
        }
        return this.order;
    }

    /**
     * 表示順を設定する。
     *
     * @param order 表示順
     */
    public void setOrder(Integer order) {
        if (Objects.nonNull(this.orderProperty)) {
            this.orderProperty.set(order);
        } else {
            this.order = order;
        }
    }

    /**
     * タイトルを取得する。
     *
     * @return タイトル
     */
    public String getTitle() {
        if (Objects.nonNull(this.titleProperty)) {
            return this.titleProperty.get();
        }
        return this.title;
    }

    /**
     * タイトルを設定する。
     *
     * @param title タイトル
     */
    public void setTitle(String title) {
        if (Objects.nonNull(this.titleProperty)) {
            this.titleProperty.set(title);
        } else {
            this.title = title;
        }
    }

    /**
     * プラグイン名を取得する。
     *
     * @return プラグイン名
     */
    public String getPluginName() {
        if (Objects.nonNull(this.pluginNameProperty)) {
            return this.pluginNameProperty.get();
        }
        return this.pluginName;
    }

    /**
     * プラグイン名を設定する。
     *
     * @param pluginName プラグイン名
     */
    public void setPluginName(String pluginName) {
        if (Objects.nonNull(this.pluginNameProperty)) {
            this.pluginNameProperty.set(pluginName);
        } else {
            this.pluginName = pluginName;
        }
    }

    /**
     * 計画数を取得する。
     *
     * @return 計画数
     */
    public Integer getPlanNum() {
        if (Objects.nonNull(this.planNumProperty)) {
            return this.planNumProperty.get();
        }
        return this.planNum;
    }

    /**
     * 計画数を設定する。
     *
     * @param planNum 計画数
     */
    public void setPlanNum(Integer planNum) {
        if (Objects.nonNull(this.planNumProperty)) {
            this.planNumProperty.set(planNum);
        } else {
            this.planNum = planNum;
        }
    }

    /**
     * 開始時間を取得する。
     *
     * @return
     */
    public LocalTime getStartWorkTime() {
        if (Objects.nonNull(this.startWorkTimeProperty)) {
            if (StringUtils.isEmpty(startWorkTimeProperty.get())) {
                return null;
            }
            return LocalTime.parse(startWorkTimeProperty.get());
        }
        return startWorkTime;
    }

    /**
     * 開始時間を設定する。
     *
     * @param startWorkTime
     */
    public void setStartWorkTime(LocalTime startWorkTime) {
        if (Objects.nonNull(this.startWorkTimeProperty)) {
            this.startWorkTimeProperty.set(DateTimeFormatter.ofPattern("HH:mm:ss").format(startWorkTime));
        } else {
            this.startWorkTime = startWorkTime;
        }
    }

    /**
     * 終了時間を取得する。
     *
     * @return
     */
    public LocalTime getEndWorkTime() {
        if (Objects.nonNull(this.endWorkTimeProperty)) {
            if (StringUtils.isEmpty(this.endWorkTimeProperty.get())) {
                return null;
            }
            return LocalTime.parse(this.endWorkTimeProperty.get());
        }
        return this.endWorkTime;
    }

    /**
     * 終了時間を設定する。
     *
     * @param endWorkTime
     */
    public void setEndWorkTime(LocalTime endWorkTime) {
        if (Objects.nonNull(this.endWorkTimeProperty)) {
            this.endWorkTimeProperty.set(DateTimeFormatter.ofPattern("HH:mm:ss").format(endWorkTime));
        } else {
            this.endWorkTime = endWorkTime;
        }
    }

    /**
     * 工程ID一覧を取得する。
     *
     * @return 工程ID一覧
     */
    @XmlElementWrapper(name = "workIds")
    @XmlElement(name = "workId")
    public List<Long> getWorkIds() {
        return this.workIds;
    }

    /**
     * 工程ID一覧を設定する。
     *
     * @param workIds 工程ID一覧
     */
    public void setWorkIds(List<Long> workIds) {
        this.workIds = workIds;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkSetting{")
                .append("order=").append(this.order)
                .append(", ")
                .append("title=").append(this.title)
                .append(", ")
                .append("pluginName=").append(this.pluginName)
                .append(", ")
                .append("planNum=").append(this.planNum)
                .append(", ")
                .append("startWorkTime=").append(this.startWorkTime)
                .append(", ")
                .append("endWorkTime=").append(this.endWorkTime)
                .append(", ")
                .append("workIds=").append(this.workIds)
                .append("}")
                .toString();
    }

    /**
     * 表示される情報をコピーする
     *
     * @return
     */
    @Override
    public WorkSetting clone() {
        WorkSetting ws = new WorkSetting();

        ws.setTitle(this.getTitle());
        ws.setStartWorkTime(this.getStartWorkTime());
        ws.setEndWorkTime(this.getEndWorkTime());
        ws.setPluginName(this.getPluginName());
        ws.setPlanNum(this.getPlanNum());
        ws.setWorkIds(this.getWorkIds());

        return ws;
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other
     * @return
     */
    public boolean equalsDisplayInfo(WorkSetting other) {
        if (Objects.equals(this.getTitle(), other.getTitle())
                && Objects.equals(this.getEndWorkTime(), other.getEndWorkTime())
                && Objects.equals(this.getStartWorkTime(), other.getStartWorkTime())
                && Objects.equals(this.getPluginName(), other.getPluginName())
                && Objects.equals(this.getPlanNum(), other.getPlanNum())
                && Objects.equals(this.getWorkIds(), other.getWorkIds())) {
            return true;
        }
        return false;
    }
}
