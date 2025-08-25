package jp.adtekfuji.adFactory.entity.search;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.operation.OperateAppEnum;
import jp.adtekfuji.adFactory.entity.operation.OperationTypeEnum;

/**
 * 作業者操作実績 検索条件
 *
 * @author yu.nara
 */
@XmlRootElement(name = "operationSearchCondition")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationSerachCondition implements Serializable {
 
    private static final long serialVersionUID = 1L; 

    @XmlElement()
    private Long equipmentId; // 設備ID

    @XmlElement()
    private Long organizationId; // 組織ID

    @XmlElement()
    private String operateApp; // 操作アプリ

    @XmlElement()
    private String operationType; // 操作タイプ

    @XmlElementWrapper(name = "organizationIds")
    @XmlElement(name = "organizationId")
    private List<Long> organizationCollection = null; // 組織ID一覧

    /**
     * コンストラクタ
     * 
     * @param equipmentId 設備ID
     * @param organizationId 組織ID
     * @param operateApp アプリケーションタイプ
     * @param operationType 操作タイプ
     */
    public OperationSerachCondition(Long equipmentId, Long organizationId, OperateAppEnum operateApp, OperationTypeEnum operationType) {
        this.equipmentId = equipmentId;
        this.organizationId = organizationId;
        this.operateApp = operateApp.getName();
        this.operationType = operationType.getName();
    }

    public OperationSerachCondition() {
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
    public OperateAppEnum getOperateApp() {
        return OperateAppEnum.toEnum(this.operateApp);
    }

    /**
     * 操作アプリ設定
     *
     * @param operateApp 操作アプリ設定
     */
    public void setOperateApp(OperateAppEnum operateApp) {
        this.operateApp = operateApp.getName();
    }

    /**
     * 操作タイプ取得
     *
     * @return 操作タイプ
     */
    public OperationTypeEnum getOperationType() {
        return OperationTypeEnum.toEnum(operationType);
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
     * 組織ID一覧を取得する。
     *
     * @return 組織ID一覧
     */
    public List<Long> getOrganizationCollection() {
        return this.organizationCollection;
    }

    /**
     * 組織ID一覧を設定する。
     *
     * @param organizationCollection 組織ID一覧
     */
    public void setOrganizationCollection(List<Long> organizationCollection) {
        this.organizationCollection = organizationCollection;
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "OperationSerachCondition{" +
                "equipmentId=" + equipmentId +
                ", organizationId=" + organizationId +
                ", operateApp='" + operateApp + '\'' +
                ", operationType='" + operationType + '\'' +
                '}';
    }
}
