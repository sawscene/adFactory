package jp.adtekfuji.adFactory.entity.operation;

import java.util.Date;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 作業者操作実績
 *
 * @author yu.nara
 */
@XmlRootElement(name = "operation")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationEntity {

    @XmlElement(required = false)
    private Long operationId = null;   //操作ID

    @XmlElement(required = false)
    private Date operateDatetime = null;// 操作時間

    @XmlElement(required = false)
    private String reportDate = null;

    @XmlElement(required = false)
    private Long equipmentId = null;// 設備ID

    @XmlElement(required = false)
    private Long organizationId = null;// 組織ID

    @XmlElement(required = false)
    private String operateApp = null;// 操作アプリ

    @XmlElement(required = false)
    private String operationType = null;// 操作タイプ

    @XmlElement(required = false)
    private String addInfo = null; // 追加情報

    public OperationEntity(Date operateDatetime, Long equipmentId, Long organizationId, OperateAppEnum operateApp, OperationTypeEnum operationType, OperationAddInfoEntity addInfo) {
        this.operateDatetime = operateDatetime;
        this.equipmentId = equipmentId;
        this.organizationId = organizationId;
        this.operateApp = operateApp.getName();
        this.operationType = operationType.getName();
        this.setAddInfo(addInfo);
    }

    public OperationEntity() {

    }

    /**
     * 操作ID取得
     *
     * @return 操作ID
     */
    public Long getOperationId() {
        return this.operationId;
    }

    /**
     * 操作時間取得
     *
     * @return 操作時間
     */
    public Date getOperateDatetime() {
        return this.operateDatetime;
    }

    /**
     * 操作時間設定
     *
     * @param operateDatetime 　操作時間
     */
    public void setOperateDatetime(Date operateDatetime) {
        this.operateDatetime = operateDatetime;
    }

    /**
     * レポート時間取得
     * @return レポート時間
     */
    public String getReportDate() {
        return reportDate;
    }

    /**
     * レポート時間設定
     * @param reportDate レポート時間
     */
    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
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
     * 操作アプリ設定
     *
     * @param operateApp 操作アプリ設定
     */
    public void setOperateApp(OperateAppEnum operateApp) {
        this.operateApp = operateApp.name();
    }


    /**
     * 操作タイプ設定
     *
     * @param operationType 操作タイプ設定
     */
    public void setOperationType(OperationTypeEnum operationType) {
        this.operationType = operationType.getName();
    }

    /**
     * 追加情報取得
     *
     * @return 追加情報
     */
    public OperationAddInfoEntity getAddInfo() {
        return JsonUtils.jsonToObject(addInfo, OperationAddInfoEntity.class);
    }

    /**
     * 追加情報取得
     *
     * @param addInfo 追加情報
     */
    public void setAddInfo(OperationAddInfoEntity addInfo) {
        this.addInfo = JsonUtils.objectToJson(addInfo);
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "OperationEntity{" +
                "operateDatetime=" + operateDatetime +
                ", equipmentId=" + equipmentId +
                ", organizationId=" + organizationId +
                ", operateApp='" + operateApp + '\'' +
                ", operationType='" + operationType + '\'' +
                ", addInfo='" + addInfo + '\'' +
                '}';
    }

}
