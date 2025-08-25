/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.actual;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 不良品情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "defectInfo")
public class DefectInfoEntity implements Serializable {

    @XmlElement
    private String porder;// 注文番号

    @XmlElement
    private DefectReasonEntity defectReason;// 不良理由

    @XmlElement
    private Integer defectNum;// 不良数

    @XmlElement
    private List<DefectSerialEntity> defectSerials;// シリアル番号毎の不良情報一覧

    @XmlElement
    private Integer lvol;// 指示数

    /**
     * コンストラクタ
     */
    public DefectInfoEntity() {
    }

    /**
     * 注文番号を取得する。
     *
     * @return 注文番号
     */
    public String getPorder() {
        return this.porder;
    }

    /**
     * 注文番号を設定する。
     *
     * @param porder 注文番号
     */
    public void setPorder(String porder) {
        this.porder = porder;
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

    /**
     * 不良数を取得する。
     *
     * @return 不良数
     */
    public Integer getDefectNum() {
        return this.defectNum;
    }

    /**
     * 不良数を設定する。
     *
     * @param defectNum 不良数
     */
    public void setDefectNum(Integer defectNum) {
        this.defectNum = defectNum;
    }

    /**
     * シリアル番号毎の不良情報一覧を取得する。
     *
     * @return 不良情報一覧
     */
    public List<DefectSerialEntity> getDefectSerials() {
        return this.defectSerials;
    }

    /**
     * シリアル番号毎の不良情報一覧を設定する。
     *
     * @param defectSerials シリアル番号毎の不良情報一覧
     */
    public void setDefectSerials(List<DefectSerialEntity> defectSerials) {
        this.defectSerials = defectSerials;
    }

    /**
     * 指示数を取得する。
     *
     * @return 指示数
     */
    public Integer getLvol() {
        return this.lvol;
    }

    /**
     * 指示数を設定する。
     *
     * @param lvol 指示数
     */
    public void setLvol(Integer lvol) {
        this.lvol = lvol;
    }

    @Override
    public String toString() {
        return new StringBuilder("DefectInfoEntity{")
                .append("porder=").append(this.porder)
                .append(", defectReason=").append(this.defectReason)
                .append(", defectNum=").append(this.defectNum)
                .append(", defectSerials=").append(this.defectSerials)
                .append(", lvol=").append(this.lvol)
                .append("}")
                .toString();
    }
}
