/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.login;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.system.SystemPropEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.xmladapter.LocalTimeXmlAdapter;

/**
 * 設備ログイン結果情報
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "equipmentLoginResult")
public class EquipmentLoginResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private Boolean isSuccess = Boolean.FALSE;// 成功したか

    @XmlElement
    private ServerErrorTypeEnum errorType;// エラー種別

    @XmlElement(required = true)
    private Long equipmentId;// 設備ID

    @XmlElement
    private EquipmentInfoEntity equipmentInfo;// 設備情報

    @XmlElement
    private Boolean isCall;// 呼び出し中か

    @XmlElement
    private Long lineId;// ラインID

    /**
     * 開始時間
     */
    @XmlElement
    @XmlJavaTypeAdapter(LocalTimeXmlAdapter.class)
    private LocalTime startWorkTime;

    /**
     * 終了時間
     */
    @XmlElement
    @XmlJavaTypeAdapter(LocalTimeXmlAdapter.class)
    private LocalTime endWorkTime;

    /**
     * システム設定一覧
     */
    @XmlElementWrapper(name = "systemProps", required = false)
    @XmlElement(name = "systemProp")
    private List<SystemPropEntity> systemProps;

    /**
     * コンストラクタ
     */
    public EquipmentLoginResult() {
    }

    /**
     * コンストラクタ
     *
     * @param isSuccess 成功か (true:成功, false:失敗)
     * @param isCall 呼び出し中か (true:呼び出し中, false:呼び出し中ではない)
     */
    private EquipmentLoginResult(Boolean isSuccess, Boolean isCall) {
        this.isSuccess = isSuccess;
        this.isCall = isCall;
    }

    /**
     * 成功時の設備ログイン結果情報を作成する。
     *
     * @param equipmentId 設備ID
     * @param isCall 呼び出しか (true:呼び出し中, false:呼び出し中ではない)
     * @return 設備ログイン結果情報
     */
    public static EquipmentLoginResult success(Long equipmentId, Boolean isCall) {
        return new EquipmentLoginResult(true, isCall).errorType(ServerErrorTypeEnum.SUCCESS).equipmentId(equipmentId);
    }

    /**
     * 失敗時の設備ログイン結果情報を作成する。
     *
     * @param errorType エラー種別
     * @return 設備ログイン結果情報
     */
    public static EquipmentLoginResult failed(ServerErrorTypeEnum errorType) {
        return new EquipmentLoginResult(false, false).errorType(errorType);
    }

    /**
     * エラー種別を設定する。
     *
     * @param errorType エラー種別
     * @return 設備ログイン結果情報
     */
    public EquipmentLoginResult errorType(ServerErrorTypeEnum errorType) {
        this.errorType = errorType;
        return this;
    }

    /**
     * 設備IDを設定する。
     *
     * @param equipmentId 設備ID
     * @return 設備ログイン結果情報
     */
    public EquipmentLoginResult equipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
        return this;
    }

    /**
     * 設備情報を設定する。
     *
     * @param equipmentInfo 設備情報
     * @return 設備ログイン結果情報
     */
    public EquipmentLoginResult equipmentInfo(EquipmentInfoEntity equipmentInfo) {
        this.equipmentInfo = equipmentInfo;
        return this;
    }

    /**
     * システム設定一覧を設定する。
     *
     * @param systemProps システム設定一覧
     * @return 設備ログイン結果情報
     */
    public EquipmentLoginResult systemProps(List<SystemPropEntity> systemProps) {
        this.systemProps = systemProps;
        return this;
    }

    /**
     * 成功かを取得する。
     *
     * @return 成功か (true:成功, false:失敗)
     */
    public Boolean getIsSuccess() {
        return this.isSuccess;
    }

    /**
     * 成功かを設定する。
     *
     * @param isSuccess 成功か (true:成功, false:失敗)
     */
    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * エラー種別を取得する。
     *
     * @return エラー種別
     */
    public ServerErrorTypeEnum getErrorType() {
        return this.errorType;
    }

    /**
     * エラー種別を設定する。
     *
     * @param errorType エラー種別
     */
    public void setErrorType(ServerErrorTypeEnum errorType) {
        this.errorType = errorType;
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
     * 設備情報を取得する。
     *
     * @return 設備情報
     */
    public EquipmentInfoEntity getEquipmentInfo() {
        return this.equipmentInfo;
    }

    /**
     * 設備情報を設定する。
     *
     * @param equipmentInfo 設備情報
     */
    public void setEquipmentInfo(EquipmentInfoEntity equipmentInfo) {
        this.equipmentInfo = equipmentInfo;
    }

    /**
     * 呼び出し中かを取得する。
     *
     * @return 呼び出し中か (true:呼び出し中, false:呼び出し中ではない)
     */
    public Boolean getIsCall() {
        return this.isCall;
    }

    /**
     * 呼び出し中かを設定する。
     *
     * @param isCall 呼び出し中か (true:呼び出し中, false:呼び出し中ではない)
     */
    public void setIsCall(Boolean isCall) {
        this.isCall = isCall;
    }

    /**
     * 開始時間を取得する。
     *
     * @return 開始時間
     */
    public LocalTime getStartWorkTime() {
        return this.startWorkTime;
    }

    /**
     * 開始時間を設定する。
     *
     * @param startWorkTime 開始時間
     */
    public void setStartWorkTime(LocalTime startWorkTime) {
        this.startWorkTime = startWorkTime;
    }

    /**
     * 終了時間を取得する。
     *
     * @return 終了時間
     */
    public LocalTime getEndWorkTime() {
        return this.endWorkTime;
    }

    /**
     * 終了時間を設定する。
     *
     * @param endWorkTime 終了時間
     */
    public void setEndWorkTime(LocalTime endWorkTime) {
        this.endWorkTime = endWorkTime;
    }

    /**
     * システム設定一覧を取得する。
     *
     * @return システム設定一覧
     */
    public List<SystemPropEntity> getSystemProps() {
        return this.systemProps;
    }

    /**
     * システム設定一覧を設定する。
     *
     * @param systemProps システム設定一覧
     */
    public void setSystemProps(List<SystemPropEntity> systemProps) {
        this.systemProps = systemProps;
    }

    /**
     * ラインIDを取得する。
     *
     * @return ラインID
     */
    public Long getLineId() {
        return this.lineId;
    }

    /**
     * ラインIDを設定する。
     *
     * @param lineId ラインID
     */
    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.isSuccess);
        hash = 89 * hash + Objects.hashCode(this.errorType);
        hash = 89 * hash + Objects.hashCode(this.equipmentId);
        hash = 89 * hash + Objects.hashCode(this.equipmentInfo);
        hash = 89 * hash + Objects.hashCode(this.isCall);
        hash = 89 * hash + Objects.hashCode(this.lineId);
        hash = 89 * hash + Objects.hashCode(this.startWorkTime);
        hash = 89 * hash + Objects.hashCode(this.endWorkTime);
        hash = 89 * hash + Objects.hashCode(this.systemProps);
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
        final EquipmentLoginResult other = (EquipmentLoginResult) obj;
        if (!Objects.equals(this.isSuccess, other.isSuccess)) {
            return false;
        }
        if (this.errorType != other.errorType) {
            return false;
        }
        if (!Objects.equals(this.equipmentId, other.equipmentId)) {
            return false;
        }
        if (!Objects.equals(this.equipmentInfo, other.equipmentInfo)) {
            return false;
        }
        if (!Objects.equals(this.isCall, other.isCall)) {
            return false;
        }
        if (!Objects.equals(this.lineId, other.lineId)) {
            return false;
        }
        if (!Objects.equals(this.startWorkTime, other.startWorkTime)) {
            return false;
        }
        if (!Objects.equals(this.endWorkTime, other.endWorkTime)) {
            return false;
        }
        if (!Objects.equals(this.systemProps, other.systemProps)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("EquipmentLoginResult{")
                .append("isSuccess=").append(this.isSuccess)
                .append(", ")
                .append("errorType=").append(this.errorType)
                .append(", ")
                .append("equipmentId=").append(this.equipmentId)
                .append(", ")
                .append("equipmentInfo=").append(this.equipmentInfo)
                .append(", ")
                .append("isCall=").append(this.isCall)
                .append(", ")
                .append("lineId=").append(this.lineId)
                .append(", ")
                .append("startWorkTime=").append(this.startWorkTime)
                .append(", ")
                .append("endWorkTime=").append(this.endWorkTime)
                .append(", ")
                .append("systemProps=").append(this.systemProps)
                .append("}")
                .toString();
    }
}
