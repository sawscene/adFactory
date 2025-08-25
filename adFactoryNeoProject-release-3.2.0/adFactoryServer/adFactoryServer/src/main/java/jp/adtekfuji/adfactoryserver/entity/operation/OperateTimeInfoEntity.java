package jp.adtekfuji.adfactoryserver.entity.operation;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * 作業者操作実績
 *
 * @author yu.nara
 */
@XmlRootElement(name = "operation")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperateTimeInfoEntity implements Serializable {

    @XmlElement(name = "operateTime")
    private Long operateTime;// 操作時間

    @XmlElement(name = "equipmentId")
    private Long equipmentId;// 設備ID

    @XmlElement(name = "organizationId")
    private Long organizationId;// 組織ID

    @XmlElement(name = "operateApp")
    private String operateApp;// 操作アプリ

    @XmlElement(name = "operationType")
    private String operationType;// 操作タイプ

    @XmlElement(name = "addInfo")
    private String addInfo; // 追加情報

    public OperateTimeInfoEntity(Long operateTime, Long equipmentId, Long organizationId, String operateApp, String operationType, String addInfo) {
        this.operateTime = operateTime;
        this.equipmentId = equipmentId;
        this.organizationId = organizationId;
        this.operateApp = operateApp;
        this.operationType = operationType;
        this.addInfo = addInfo;
    }

    public OperateTimeInfoEntity() {

    }

    /**
     * 操作時間取得
     *
     * @return 操作時間
     */
    public Long getOperateTime() {
        return operateTime;
    }

    /**
     * 操作時間設定
     *
     * @param operateDatetime 　操作時間
     */
    public void setOperateDatetime(Long operateTime) {
        this.operateTime = operateTime;
    }

    /**
     * 設備ID取得
     *
     * @return 設備ID
     */
    public Long getEquipmentId() {
        return equipmentId;
    }

    /**
     * 設備ID設定
     *
     * @param equipmentId 設備ID
     */
    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * 組織ID取得
     *
     * @return 設備ID
     */
    public Long getOrganizationId() {
        return organizationId;
    }

    /**
     * 組織ID
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 操作アプリ取得
     *
     * @return 操作アプリ
     */
    public String getOperateApp() {
        return this.operateApp;
    }

    /**
     * 操作アプリ設定
     *
     * @param operateApp 操作アプリ設定
     */
    public void setOperateApp(String operateApp) {
        this.operateApp = operateApp;
    }

    /**
     * 操作タイプ取得
     *
     * @return 操作タイプ
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * 操作タイプ設定
     *
     * @param operationType 操作タイプ設定
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    /**
     * 追加情報取得
     * @return 追加情報取得
     */
    public String getAddInfo() {
        return addInfo;
    }

    /**
     * 追加情報設定
     * @return 追加情報設定
     */
    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "OperationInfoEntity{" +
                "operateTime=" + operateTime +
                ", equipmentId=" + equipmentId +
                ", organizationId=" + organizationId +
                ", operateApp='" + operateApp + '\'' +
                ", operationType='" + operationType + '\'' +
                ", addInfo='" + addInfo + '\'' +
                '}';
    }

}
