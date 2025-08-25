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
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "workEquipmentSettng")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class WorkEquipmentSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    private IntegerProperty orderProperty = null;
    private StringProperty titleProperty = null;
    // 分類名
    private StringProperty categoryNameProperty;
    // 目標値
    private IntegerProperty planNumProperty;
    // 開始時間
    private StringProperty startWorkTimeProperty;
    // 終了時間
    private StringProperty endWorkTimeProperty;
    // 呼び出しメロディ
    private StringProperty callMelodyPathProperty = null;

    private Integer order = 0;
    private String title = "";
    private String categoryName;
    private Integer planNum;
    private List<Long> equipmentIds = new ArrayList<>();
    private LocalTime startWorkTime;
    private LocalTime endWorkTime;
    private String callMelodyPath = "";

    public WorkEquipmentSetting() {
    }

    public IntegerProperty orderProperty() {
        if (Objects.isNull(orderProperty)) {
            orderProperty = new SimpleIntegerProperty(order);
        }
        return orderProperty;
    }

    public Integer getOrder() {
        if (Objects.nonNull(orderProperty)) {
            return orderProperty.get();
        }
        return order;
    }

    public void setOrder(Integer order) {
        if (Objects.nonNull(orderProperty)) {
            orderProperty.set(order);
        } else {
            this.order = order;
        }
    }

    public StringProperty titleProperty() {
        if (Objects.isNull(titleProperty)) {
            titleProperty = new SimpleStringProperty(title);
        }
        return titleProperty;
    }

    public String getTitle() {
        if (Objects.nonNull(titleProperty)) {
            return titleProperty.get();
        }
        return title;
    }

    public void setTitle(String title) {
        if (Objects.nonNull(titleProperty)) {
            titleProperty.set(title);
        } else {
            this.title = title;
        }
    }

    /**
     * 分類名プロパティを取得する。
     *
     * @return
     */
    public StringProperty categoryNameProperty() {
        if (Objects.isNull(this.categoryNameProperty)) {
            if (StringUtils.isEmpty(this.categoryName)) {
                this.categoryName = "";
            }
            this.categoryNameProperty = new SimpleStringProperty(this.categoryName);
        }
        return this.categoryNameProperty;
    }

    /**
     * 分類名を取得する。
     *
     * @return
     */
    public String getCategoryName() {
        if (Objects.nonNull(this.categoryNameProperty)) {
            return this.categoryNameProperty.get();
        }
        return categoryName;
    }

    /**
     * 分類名を設定する。
     *
     * @param categoryName
     */
    public void setCategoryName(String categoryName) {
        if (Objects.nonNull(this.categoryNameProperty)) {
            this.categoryNameProperty.set(categoryName);
        } else {
            this.categoryName = categoryName;
        }
    }

    /**
     * 目標値プロパティを取得する。
     *
     * @return
     */
    public IntegerProperty planNumProperty() {
        if (Objects.isNull(this.planNumProperty)) {
            if (Objects.isNull(this.planNum)) {
                this.planNum = 0;
            }
            this.planNumProperty = new SimpleIntegerProperty(this.planNum);
        }
        return this.planNumProperty;
    }

    /**
     * 目標値を取得する。
     *
     * @return
     */
    public Integer getPlanNum() {
        if (Objects.nonNull(this.planNumProperty)) {
            return this.planNumProperty.get();
        }
        return this.planNum;
    }

    /**
     * 目標値を設定する。
     *
     * @param planNum
     */
    public void setPlanNum(Integer planNum) {
        if (Objects.nonNull(this.planNumProperty)) {
            this.planNumProperty.set(planNum);
        } else {
            this.planNum = planNum;
        }
    }

    /**
     * 開始時間プロパティを取得する。
     *
     * @return
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
     * 終了時間プロパティを取得する。
     *
     * @return
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

    @XmlElementWrapper(name = "equipmentIds")
    @XmlElement(name = "equipmentId")
    public List<Long> getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(List<Long> equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public StringProperty callMelodyPathProperty() {
        if (Objects.isNull(callMelodyPathProperty)) {
            callMelodyPathProperty = new SimpleStringProperty(callMelodyPath);
        }
        return callMelodyPathProperty;
    }

    public String getCallMelodyPath() {
        if (Objects.nonNull(callMelodyPathProperty)) {
            return callMelodyPathProperty.get();
        }
        return callMelodyPath;
    }

    public void setCallMelodyPath(String title) {
        if (Objects.nonNull(callMelodyPathProperty)) {
            callMelodyPathProperty.set(title);
        } else {
            this.callMelodyPath = title;
        }
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
        final WorkEquipmentSetting other = (WorkEquipmentSetting) obj;
        return true;
    }

    @Override
    public String toString() {
        return "WorkEquipmentSettng{" + "order=" + getOrder() + ", title=" + getTitle() + ", equipmentIds=" + equipmentIds + '}';
    }
    
    /**
     * 表示される情報をコピーする
     * 
     * @return 
     */
    public WorkEquipmentSetting clone() {
        WorkEquipmentSetting equip = new WorkEquipmentSetting();

        equip.setPlanNum(getPlanNum());
        equip.setTitle(getTitle());
        equip.setEndWorkTime(getEndWorkTime());
        equip.setStartWorkTime(getStartWorkTime());
        equip.setEquipmentIds(new ArrayList(getEquipmentIds()));
        equip.setCategoryName(getCategoryName());
        equip.setCallMelodyPath(getCallMelodyPath());
        
        return equip;
    }

    /**
     * 表示される情報の内容が一致するか調べる
     * 
     * @param other
     * @return 
     */
    public boolean equalsDisplayInfo(WorkEquipmentSetting other) {
        if(Objects.equals(getPlanNum(), other.getPlanNum())
                && Objects.equals(getTitle(), other.getTitle())
                && Objects.equals(getEndWorkTime(), other.getEndWorkTime())
                && Objects.equals(getStartWorkTime(), other.getStartWorkTime())
                && Objects.equals(getEquipmentIds(), other.getEquipmentIds())
                && Objects.equals(getCategoryName(), other.getCategoryName())
                && Objects.equals(getCallMelodyPath(), other.getCallMelodyPath())) {
            return true;
        }
        return false;
    }
}
