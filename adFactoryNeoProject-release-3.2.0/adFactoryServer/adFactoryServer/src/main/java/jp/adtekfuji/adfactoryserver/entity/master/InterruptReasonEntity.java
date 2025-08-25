/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.master;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;

/**
 * 中断理由マスタ
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "interruptReason")
@XmlAccessorType(XmlAccessType.FIELD)
public class InterruptReasonEntity extends ReasonMasterEntity {

    /**
     * コンストラクタ
     */
    public InterruptReasonEntity() {
        super.setReasonType(ReasonTypeEnum.TYPE_INTERRUPT);
        super.setReasonOrder(0L);
    }

    /**
     * コンストラクタ
     *
     * @param interruptReason 中断理由
     * @param fontColor 文字色
     * @param backColor 背景色
     * @param lightPattern 点灯パターン
     */
    public InterruptReasonEntity(String interruptReason, String fontColor, String backColor, LightPatternEnum lightPattern) {
        super.setReasonType(ReasonTypeEnum.TYPE_INTERRUPT);
        super.setReason(interruptReason);
        super.setFontColor(fontColor);
        super.setBackColor(backColor);
        super.setLightPattern(lightPattern);
        super.setReasonOrder(0L);
    }

    /**
     * 理由マスタに変換する。
     *
     * @return 理由マスタ
     * @throws Exception 
     */
    public ReasonMasterEntity upcast() throws Exception {
        ReasonMasterEntity reason = new ReasonMasterEntity();
        reason.setReasonId(this.getReasonId());
        reason.setReasonType(this.getReasonType());
        reason.setReason(this.getReason());
        reason.setFontColor(this.getFontColor());
        reason.setBackColor(this.getBackColor());
        reason.setLightPattern(this.getLightPattern());
        reason.setReasonOrder(this.getReasonOrder());
        reason.setVerInfo(this.getVerInfo());
        return reason;
    }

    /**
     * 中断理由IDを取得する。
     *
     * @return 中断理由ID
     */
    @XmlElement(name="interruptId")
    public Long getInterruptId() {
        return super.getReasonId();
    }

    /**
     * 中断理由IDを設定する。
     *
     * @param interruptId 中断理由ID
     */
    public void setInterruptId(Long interruptId) {
        super.setReasonId(interruptId);
    }

    /**
     * 中断理由を取得する。
     *
     * @return 中断理由
     */
    @XmlElement(name="interruptReason")
    public String getInterruptReason() {
        return super.getReason();
    }

    /**
     * 中断理由を設定する。
     *
     * @param interruptReason 中断理由
     */
    public void setInterruptReason(String interruptReason) {
        super.setReason(interruptReason);
    }

    @Override
    public String toString() {
        return new StringBuilder("InterruptReasonEntity{")
                .append("interruptId=").append(this.getInterruptId())
                .append(", ")
                .append("interruptReason=").append(this.getInterruptReason())
                .append(", ")
                .append("fontColor=").append(this.getFontColor())
                .append(", ")
                .append("backColor=").append(this.getBackColor())
                .append(", ")
                .append("lightPattern=").append(this.getLightPattern())
                .append(", ")
                .append("verInfo=").append(this.getVerInfo())
                .append("}")
                .toString();
    }
}
