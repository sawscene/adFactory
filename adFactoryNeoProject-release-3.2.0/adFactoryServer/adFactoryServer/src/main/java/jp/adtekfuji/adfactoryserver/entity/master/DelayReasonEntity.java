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
 * 遅延理由マスタ
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "delayReason")
@XmlAccessorType(XmlAccessType.FIELD)
public class DelayReasonEntity extends ReasonMasterEntity {

    /**
     * コンストラクタ
     */
    public DelayReasonEntity() {
        super.setReasonType(ReasonTypeEnum.TYPE_DELAY);
        super.setReasonOrder(0L);
    }

    /**
     * コンストラクタ
     *
     * @param delayReason 遅延理由
     * @param fontColor 文字色
     * @param backColor 背景色
     * @param lightPattern 点灯パターン
     */
    public DelayReasonEntity(String delayReason, String fontColor, String backColor, LightPatternEnum lightPattern) {
        super.setReasonType(ReasonTypeEnum.TYPE_DELAY);
        super.setReason(delayReason);
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
     * 遅延理由IDを取得する。
     *
     * @return 遅延理由ID
     */
    @XmlElement(name="delayId")
    public Long getDelayId() {
        return super.getReasonId();
    }

    /**
     * 遅延理由IDを設定する。
     *
     * @param delayId 遅延理由ID
     */
    public void setDelayId(Long delayId) {
        super.setReasonId(delayId);
    }

    /**
     * 遅延理由を取得する。
     *
     * @return 遅延理由
     */
    @XmlElement(name="delayReason")
    public String getDelayReason() {
        return super.getReason();
    }

    /**
     * 遅延理由を設定する。
     *
     * @param delayReason 遅延理由
     */
    public void setDelayReason(String delayReason) {
        super.setReason(delayReason);
    }

    @Override
    public String toString() {
        return new StringBuilder("DelayReasonEntity{")
                .append("delayId=").append(this.getDelayId())
                .append(", ")
                .append("delayReason=").append(this.getDelayReason())
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
