/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.andon.property.WorkSetting;

/**
 * 進捗モニタ 工程計画実績数情報
 *
 * @author s-heya
 */
@XmlRootElement(name = "monitorWorkSettingInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitorWorkPlanNumInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Integer order;// 表示順

    @XmlElement
    private String title;// タイトル

    @XmlElement
    private String pluginName;// プラグイン名

    @XmlElementWrapper(name = "workIds")
    @XmlElement(name = "workId")
    private List<Long> workIds;// 工程ID一覧

    @XmlElement
    private Integer planNum;// 計画数

    @XmlElement
    private Integer actualNum;// 実績数

    /**
     * コンストラクタ
     */
    public MonitorWorkPlanNumInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param setting 工程設定情報
     */
    public MonitorWorkPlanNumInfoEntity(WorkSetting setting) {
        this.order = setting.getOrder();
        this.title = setting.getTitle();
        this.pluginName = setting.getPluginName();
        this.planNum = setting.getPlanNum();
        this.workIds = setting.getWorkIds();
    }

    /**
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public Integer getOrder() {
        return this.order;
    }

    /**
     * タイトルを取得する。
     *
     * @return タイトル
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * プラグイン名を取得する。
     *
     * @return プラグイン名
     */
    public String getPluginName() {
        return this.pluginName;
    }

    /**
     * 工程ID一覧を取得する。
     *
     * @return 工程ID一覧
     */
    public List<Long> getWorkIds() {
        return this.workIds;
    }

    /**
     * 計画数を取得する。
     *
     * @return 計画数
     */
    public Integer getPlanNum() {
        return this.planNum;
    }

    /**
     * 計画数を設定する。
     *
     * @param planNum 計画数
     */
    public void setPlanNum(Integer planNum) {
        this.planNum = planNum;
    }

    /**
     * 実績数を取得する。
     *
     * @return 実績数
     */
    public Integer getActualNum() {
        return this.actualNum;
    }

    /**
     * 実績数を設定する。
     *
     * @param actualNum 実績数
     */
    public void setActualNum(Integer actualNum) {
        this.actualNum = actualNum;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.pluginName);
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
        final MonitorWorkPlanNumInfoEntity other = (MonitorWorkPlanNumInfoEntity) obj;
        return Objects.equals(this.pluginName, other.pluginName);
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorWorkPlanNumInfoEntity{")
                .append("order=").append(this.order)
                .append(", ")
                .append("title=").append(this.title)
                .append(", ")
                .append("pluginName=").append(this.pluginName)
                .append(", ")
                .append("workIds=").append(this.workIds)
                .append(", ")
                .append("planNum=").append(this.planNum)
                .append(", ")
                .append("actualNum=").append(this.actualNum)
                .append("}")
                .toString();
    }
}
