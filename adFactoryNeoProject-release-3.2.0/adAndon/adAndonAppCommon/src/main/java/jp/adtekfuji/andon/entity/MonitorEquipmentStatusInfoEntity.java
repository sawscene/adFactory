/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.andon.enumerate.MonitorStatusEnum;
import static jp.adtekfuji.andon.enumerate.MonitorStatusEnum.READY;
import static jp.adtekfuji.andon.enumerate.MonitorStatusEnum.SUSPEND;
import static jp.adtekfuji.andon.enumerate.MonitorStatusEnum.WORKING;

/**
 * 進捗モニタ 設備ステータス情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "monitorEquipmentStatusInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitorEquipmentStatusInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer order;// 表示順
    private String name;// 名称
    private String fontColor;// 文字色
    private String backColor;// 背景色
    private MonitorStatusEnum status;// ステータス
    private Long equipmentId;// 設備ID
    private Boolean called;// 呼び出しフラグ
    private String callReason;// 呼び出し理由

    /**
     * コンストラクタ
     */
    public MonitorEquipmentStatusInfoEntity() {
    }

    /**
     * 表示順を設定して、設備ステータス情報を取得する。
     *
     * @param order 表示順
     * @return 設備ステータス情報
     */
    public MonitorEquipmentStatusInfoEntity order(Integer order) {
        this.order = order;
        return this;
    }

    /**
     * 名称を設定して、設備ステータス情報を取得する。
     *
     * @param name 名称
     * @return 設備ステータス情報
     */
    public MonitorEquipmentStatusInfoEntity name(String name) {
        this.name = name;
        return this;
    }

    /**
     * 文字色を設定して、設備ステータス情報を取得する。
     *
     * @param fontColor 文字色
     * @return 設備ステータス情報
     */
    public MonitorEquipmentStatusInfoEntity fontColor(String fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    /**
     * 背景色を設定して、設備ステータス情報を取得する。
     *
     * @param backColor 背景色
     * @return 設備ステータス情報
     */
    public MonitorEquipmentStatusInfoEntity backColor(String backColor) {
        this.backColor = backColor;
        return this;
    }

    /**
     * ステータスを設定して、設備ステータス情報を取得する。
     *
     * @param status ステータス
     * @return 設備ステータス情報
     */
    public MonitorEquipmentStatusInfoEntity status(MonitorStatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * 設備IDを設定して、設備ステータス情報を取得する。
     *
     * @param equipmentId 設備ID
     * @return 設備ステータス情報
     */
    public MonitorEquipmentStatusInfoEntity equipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
        return this;
    }

    /**
     * 呼び出しフラグを設定して、設備ステータス情報を取得する。
     *
     * @param called 呼び出しフラグ
     * @return 設備ステータス情報
     */
    public MonitorEquipmentStatusInfoEntity called(Boolean called) {
        this.called = called;
        return this;
    }

    /**
     * ステータスパターンを設定して、設備ステータス情報を取得する。
     *
     * @param pattern ステータスパターン
     * @return 設備ステータス情報
     */
    public MonitorEquipmentStatusInfoEntity status(StatusPatternEnum pattern) {
        switch (pattern) {
            case PLAN_NORMAL:
                this.status = MonitorStatusEnum.READY;
                break;
            case WORK_NORMAL:
                this.status = MonitorStatusEnum.WORKING;
                break;
            case SUSPEND_NORMAL:
                this.status = MonitorStatusEnum.SUSPEND;
                break;
            case INTERRUPT_NORMAL:
            case COMP_NORMAL:
                this.status = MonitorStatusEnum.READY;
                break;
            case BREAK_TIME:
                this.status = MonitorStatusEnum.BREAK_TIME;
                break;
            case CALLING:
                this.status = MonitorStatusEnum.CALL;
                break;
        }
        return this;
    }

    /**
     * カンバンステータスを設定して、設備ステータス情報を取得する。
     *
     * @param status カンバンステータス
     * @return 設備ステータス情報
     */
    public MonitorEquipmentStatusInfoEntity status(KanbanStatusEnum status) {
        switch (status) {
            case WORKING:
                this.status = MonitorStatusEnum.WORKING;
                break;
            case INTERRUPT:
                this.status = MonitorStatusEnum.READY;
                break;
            case SUSPEND:
                this.status = MonitorStatusEnum.SUSPEND;
                break;
            case COMPLETION:
                this.status = MonitorStatusEnum.READY;
                break;
        }
        return this;
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
     * 表示順を設定する。
     *
     * @param order 表示順
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * 名称を取得する。
     *
     * @return 名称
     */
    public String getName() {
        return this.name;
    }

    /**
     * 名称を設定する。
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 文字色を取得する。
     *
     * @return 文字色
     */
    public String getFontColor() {
        return this.fontColor;
    }

    /**
     * 文字色を設定する。
     *
     * @param fontColor 文字色
     */
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * 背景色を取得する。
     *
     * @return 背景色
     */
    public String getBackColor() {
        return this.backColor;
    }

    /**
     * 背景色を設定する。
     *
     * @param backColor 背景色
     */
    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    /**
     * ステータスを取得する。
     *
     * @return ステータス
     */
    public MonitorStatusEnum getStatus() {
        return this.status;
    }

    /**
     * ステータスを設定する。
     *
     * @param status ステータス
     */
    public void setStatus(MonitorStatusEnum status) {
        this.status = status;
    }

    /**
     * 設備IDを取得する。
     *
     * @return 設備ID
     */
    public Long getEquipmentId() {
        return this.equipmentId;
    }

    /**
     * 設備IDを設定する。
     *
     * @param equipmentId 設備ID
     */
    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * 呼び出しフラグを取得する。
     *
     * @return 呼び出しフラグ
     */
    public Boolean isCalled() {
        return this.called;
    }

    /**
     * 呼び出しフラグを設定する。
     *
     * @param called 呼び出しフラグ
     */
    public void setCalled(Boolean called) {
        this.called = called;
    }

    /**
     * 呼び出し理由を取得する。
     *
     * @return 呼び出し理由
     */
    public String getReason() {
        return this.callReason;
    }

    /**
     * 呼び出し理由を設定する。
     *
     * @param reason 呼び出し理由
     */
    public void setReason(String reason) {
        this.callReason = reason;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    /**
     * 設備ステータス情報一覧を、優先度が高い順に並び替える。
     *
     * @param statuses 設備ステータス情報一覧
     */
    public static void sort(List<MonitorEquipmentStatusInfoEntity> statuses) {
        statuses.sort((left, right) -> {
            if (left.equals(right)) {
                return 0;
            }
            MonitorEquipmentStatusInfoEntity ret = MonitorEquipmentStatusInfoEntity.comparator(left, right);
            return ret.equals(left) ? -1 : 1;
        });
    }

    /**
     * 優先度が高い設備ステータス情報を返す。
     *
     * @param left 設備ステータス情報
     * @param right 設備ステータス情報
     * @return 優先度が高い設備ステータス情報
     */
    private static MonitorEquipmentStatusInfoEntity comparator(MonitorEquipmentStatusInfoEntity left, MonitorEquipmentStatusInfoEntity right) {
        MonitorEquipmentStatusInfoEntity ret = left;
        switch (right.getStatus()) {
            case READY:
                break;
            case WORKING:
                if (left.getStatus() == READY) {
                    ret = right;
                }
                break;
            case SUSPEND:
                if (left.getStatus() == READY || left.getStatus() == WORKING) {
                    ret = right;
                }
                break;
            case CALL:
                if (left.getStatus() == READY || left.getStatus() == WORKING || left.getStatus() == SUSPEND) {
                    ret = right;
                }
                break;
            case BREAK_TIME:
                ret = right;
                break;
        }
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MonitorEquipmentStatusInfoEntity other = (MonitorEquipmentStatusInfoEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorEquipmentStatusInfoEntity{")
                .append("order=").append(this.order)
                .append(", ")
                .append("name=").append(this.name)
                .append(", ")
                .append("fontColor=").append(this.fontColor)
                .append(", ")
                .append("backColor=").append(this.backColor)
                .append(", ")
                .append("status=").append(this.status)
                .append(", ")
                .append("equipmentId=").append(this.equipmentId)
                .append(", ")
                .append("called=").append(this.called)
                .append(", ")
                .append("callReason=").append(this.callReason)
                .append("}")
                .toString();
    }
}
