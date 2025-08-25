package jp.adtekfuji.adfactoryserver.entity.operation;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 作業者操作実績
 *
 * @author yu.nara
 */
@XmlRootElement(name = "operation")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationInfoEntity implements Serializable {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");

    @XmlElement()
    private Long operationId;   //操作ID

    @XmlElement()
    private String operateDatetime;// 操作時間

    @XmlElement()
    private Long equipmentId;// 設備ID

    @XmlElement()
    private Long organizationId;// 組織ID

    @XmlElement()
    private String operateApp;// 操作アプリ

    @XmlElement()
    private String operationType;// 操作タイプ

    @XmlElement()
    private String addInfo; // 追加情報

    public OperationInfoEntity(Date operateDatetime, Long equipmentId, Long organizationId, String operateApp, String operationType, String addInfo) {
        this.operateDatetime = sdf.format(operateDatetime);
        this.equipmentId = equipmentId;
        this.organizationId = organizationId;
        this.operateApp = operateApp;
        this.operationType = operationType;
        this.addInfo = addInfo;
    }

    public OperationInfoEntity() {

    }

    /**
     * 操作時間取得
     *
     * @return 操作時間
     */
    public Date getOperateDatetime() {
        if (Objects.isNull(operateDatetime)) {
            return null;
        }

        try {
            return sdf.parse(operateDatetime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 操作時間設定
     *
     * @param operateDatetime 　操作時間
     */
    public void setOperateDatetime(Date operateDatetime) {
        this.operateDatetime = sdf.format(operateDatetime);
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
                "operateDatetime=" + operateDatetime +
                ", equipmentId=" + equipmentId +
                ", organizationId=" + organizationId +
                ", operateApp='" + operateApp + '\'' +
                ", operationType='" + operationType + '\'' +
                ", addInfo='" + addInfo + '\'' +
                '}';
    }

}
