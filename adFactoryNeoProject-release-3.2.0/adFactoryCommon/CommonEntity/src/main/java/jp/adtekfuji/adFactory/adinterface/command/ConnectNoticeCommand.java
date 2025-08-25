/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adinterface.command;

import java.io.Serializable;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;

/**
 * 接続通知コマンド
 * 
 * @author ke.yokoi
 */
public class ConnectNoticeCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private EquipmentTypeEnum equipmentType;
    private Long euipmenteId;
    private boolean workProgressFlag = false;// ログイン設備の工程進捗フラグ (作業者端末のみ)

    /**
     * コンストラクタ
     */
    public ConnectNoticeCommand() {
    }

    /**
     * コンストラクタ
     *
     * @param equipmentType 設備種別
     */
    public ConnectNoticeCommand(EquipmentTypeEnum equipmentType) {
        this.equipmentType = equipmentType;
    }

    /**
     * コンストラクタ (モニタ端末)
     *
     * @param equipmentType 設備種別
     * @param euipmenteId 設備ID
     */
    public ConnectNoticeCommand(EquipmentTypeEnum equipmentType, Long euipmenteId) {
        this.equipmentType = equipmentType;
        this.euipmenteId = euipmenteId;
    }

    /**
     * コンストラクタ (作業者端末)
     *
     * @param equipmentType 設備種別
     * @param euipmenteId ログイン設備の設備ID
     * @param workProgressFlag ログイン設備の工程進捗フラグ
     */
    public ConnectNoticeCommand(EquipmentTypeEnum equipmentType, Long euipmenteId, boolean workProgressFlag) {
        this.equipmentType = equipmentType;
        this.euipmenteId = euipmenteId;
        this.workProgressFlag = workProgressFlag;
    }

    /**
     * 設備種別を取得する。
     *
     * @return 設備種別
     */
    public EquipmentTypeEnum getEquipmentType() {
        return this.equipmentType;
    }

    /**
     * 設備種別を設定する。
     *
     * @param equipmentType 設備種別
     */
    public void setEquipmentType(EquipmentTypeEnum equipmentType) {
        this.equipmentType = equipmentType;
    }

    /**
     * 設備IDを取得する。
     * 
     * @return 設備ID
     */
    public Long getEuipmenteId() {
        return this.euipmenteId;
    }

    /**
     * 工程進捗フラグを取得する。
     *
     * @return 工程進捗フラグ
     */
    public boolean isWorkProgressFlag() {
        return this.workProgressFlag;
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
        final ConnectNoticeCommand other = (ConnectNoticeCommand) obj;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ConnectNoticeCommand{")
                .append("equipmentType=").append(this.equipmentType)
                .append(", euipmenteId=").append(this.euipmenteId)
                .append(", workProgressFlag=").append(this.workProgressFlag)
                .append("}")
                .toString();
    }
}
