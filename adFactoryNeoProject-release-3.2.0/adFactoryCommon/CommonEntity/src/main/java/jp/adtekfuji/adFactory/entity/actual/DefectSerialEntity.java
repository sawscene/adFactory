/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.actual;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * シリアル番号毎の不良情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "defectSerial")
public class DefectSerialEntity implements Serializable {

    @XmlElement
    private String serialNo;// シリアル番号

    @XmlElement
    private DefectReasonEntity defectReason;// 不良理由

    /**
     * コンストラクタ
     */
    public DefectSerialEntity() {
    }

    /**
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public String getSerialNo() {
        return this.serialNo;
    }

    /**
     * シリアル番号を設定する。
     *
     * @param serialNo シリアル番号
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * 不良理由を取得する。
     *
     * @return 不良理由
     */
    public DefectReasonEntity getDefectReason() {
        return this.defectReason;
    }

    /**
     * 不良理由を設定する。
     *
     * @param defectReason 不良理由
     */
    public void setDefectReason(DefectReasonEntity defectReason) {
        this.defectReason = defectReason;
    }

    @Override
    public String toString() {
        return new StringBuilder("DefectSerialEntity{")
                .append("serialNo=").append(this.serialNo)
                .append(", defectReason=").append(this.defectReason)
                .append("}")
                .toString();
    }
}
