/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.view;

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 作業日報情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workReport")
public class WorkReportInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private IntegerProperty workTypeProperty;// 作業種別プロパティ
    private StringProperty workDateProperty;// 作業日プロパティ
    private LongProperty organizationIdProperty;// 組織IDプロパティ
    private StringProperty organizationIdentifyProperty;// 組織識別名プロパティ
    private StringProperty organizationNameProperty;// 組織名プロパティ
    private LongProperty indirectActualIdProperty;// 間接工数実績IDプロパティ
    private LongProperty workIdProperty;// 作業IDプロパティ
    private StringProperty classNumberProperty;// 分類番号プロパティ
    private StringProperty workNumberProperty;// 作業Noプロパティ
    private StringProperty workNameProperty;// 作業内容プロパティ
    private StringProperty orderNumberProperty;// 注文番号プロパティ
    private IntegerProperty workTimeProperty;// 工数(ms)プロパティ
    private LongProperty workflowIdProperty;// 工程順IDプロパティ
    private StringProperty kanbanNameProperty;// カンバン名プロパティ
    private StringProperty modelNameProperty;// モデル名プロパティ
    private IntegerProperty actualNumProperty;// 実績数プロパティ
    private IntegerProperty workTypeOrderProperty;// 作業種別の順プロパティ
    private StringProperty productionNumberProperty;// 製造番号プロパティ

    @XmlElement()
    private Integer workType;// 作業種別
    @XmlElement()
    private String workDate;// 作業日
    @XmlElement()
    private Long organizationId;// 組織ID
    @XmlElement()
    private String organizationIdentify;// 組織識別名
    @XmlElement()
    private String organizationName;// 組織名
    @XmlElement()
    private Long indirectActualId;// 間接工数実績ID
    @XmlElement()
    private Long workId;// 作業ID
    @XmlElement()
    private String classNumber;// 分類番号
    @XmlElement()
    private String workNumber;// 作業No
    @XmlElement()
    private String workName;// 作業内容
    @XmlElement()
    private String orderNumber;// 注文番号
    @XmlElement()
    private Integer workTime;// 工数(ms)
    @XmlElement()
    private Long workflowId;// 工程順ID
    @XmlElement()
    private String kanbanName;// カンバン名
    @XmlElement()
    private String modelName;// モデル名
    @XmlElement()
    private Integer actualNum;// 実績数
    @XmlElement()
    private Integer workTypeOrder;// 作業種別の順
    @XmlElement()
    private String productionNumber;// 製造番号
    @XmlElement()
    private String workReprotAddInfo; // 追加情報
    @XmlElement()
    private String classKey; // 分類キー
    @XmlElement()
    private String serialNumbers; // シリアル番号

    /**
     * コンストラクタ
     */
    public WorkReportInfoEntity() {
    }

    /**
     * 作業種別プロパティを取得する。
     *
     * @return 作業種別 (0:直接作業, 1:間接作業, 2:中断時間)
     */
    public IntegerProperty workTypeProperty() {
        if (Objects.isNull(this.workTypeProperty)) {
            this.workTypeProperty = new SimpleIntegerProperty(this.workType);
        }
        return this.workTypeProperty;
    }

    /**
     * 作業日プロパティを取得する。
     *
     * @return 作業日 ('yyyyMMdd')
     */
    public StringProperty workDateProperty() {
        if (Objects.isNull(this.workDateProperty)) {
            this.workDateProperty = new SimpleStringProperty(this.workDate);
        }
        return this.workDateProperty;
    }

    /**
     * 組織IDプロパティを取得する。
     *
     * @return 組織ID
     */
    public LongProperty organizationIdProperty() {
        if (Objects.isNull(this.organizationIdProperty)) {
            this.organizationIdProperty = new SimpleLongProperty(this.organizationId);
        }
        return this.organizationIdProperty;
    }

    /**
     * 組織識別名プロパティを取得する。
     *
     * @return 組織識別名
     */
    public StringProperty organizationIdentifyProperty() {
        if (Objects.isNull(this.organizationIdentifyProperty)) {
            this.organizationIdentifyProperty = new SimpleStringProperty(this.organizationIdentify);
        }
        return this.organizationIdentifyProperty;
    }

    /**
     * 組織名プロパティを取得する。
     *
     * @return 組織名
     */
    public StringProperty organizationNameProperty() {
        if (Objects.isNull(this.organizationNameProperty)) {
            this.organizationNameProperty = new SimpleStringProperty(this.organizationName);
        }
        return this.organizationNameProperty;
    }

    /**
     * 間接工数実績IDプロパティを取得する。
     *
     * @return 間接工数実績ID
     */
    public LongProperty indirectActualIdProperty() {
        if (Objects.isNull(this.indirectActualIdProperty)) {
            this.indirectActualIdProperty = new SimpleLongProperty(this.indirectActualId);
        }
        return this.indirectActualIdProperty;
    }

    /**
     * 作業IDプロパティを取得する。
     *
     * @return 作業ID
     */
    public LongProperty workIdProperty() {
        if (Objects.isNull(this.workIdProperty)) {
            this.workIdProperty = new SimpleLongProperty(this.workId);
        }
        return this.workIdProperty;
    }

    /**
     * 分類番号プロパティを取得する。
     *
     * @return 分類番号
     */
    public StringProperty classNumberProperty() {
        if (Objects.isNull(this.classNumberProperty)) {
            this.classNumberProperty = new SimpleStringProperty(this.classNumber);
        }
        return this.classNumberProperty;
    }

    /**
     * 作業番号プロパティを取得する。
     *
     * @return 作業番号
     */
    public StringProperty workNumberProperty() {
        if (Objects.isNull(this.workNumberProperty)) {
            this.workNumberProperty = new SimpleStringProperty(this.workNumber);
        }
        return this.workNumberProperty;
    }

    /**
     * 作業名プロパティを取得する。
     *
     * @return 作業名
     */
    public StringProperty workNameProperty() {
        if (Objects.isNull(this.workNameProperty)) {
            this.workNameProperty = new SimpleStringProperty(this.workName);
        }
        return this.workNameProperty;
    }

    /**
     * 注文番号プロパティを取得する。
     *
     * @return 注文番号
     */
    public StringProperty orderNumberProperty() {
        if (Objects.isNull(this.orderNumberProperty)) {
            this.orderNumberProperty = new SimpleStringProperty(this.orderNumber);
        }
        return this.orderNumberProperty;
    }

    /**
     * 工数(ms)プロパティを取得する。
     *
     * @return 工数(ms)
     */
    public IntegerProperty workTimeProperty() {
        if (Objects.isNull(this.workTimeProperty)) {
            this.workTimeProperty = new SimpleIntegerProperty(this.workTime);
        }
        return this.workTimeProperty;
    }

    /**
     * 工程順IDプロパティを取得する。
     *
     * @return 工程順ID
     */
    public LongProperty workflowIdProperty() {
        if (Objects.isNull(this.workflowIdProperty)) {
            this.workflowIdProperty = new SimpleLongProperty(this.workflowId);
        }
        return this.workflowIdProperty;
    }

    /**
     * カンバン名プロパティを取得する。
     *
     * @return カンバン名
     */
    public StringProperty kanbanNameProperty() {
        if (Objects.isNull(this.kanbanNameProperty)) {
            this.kanbanNameProperty = new SimpleStringProperty(this.kanbanName);
        }
        return this.kanbanNameProperty;
    }

    /**
     * モデル名プロパティを取得する。
     *
     * @return モデル名
     */
    public StringProperty modelNameProperty() {
        if (Objects.isNull(this.modelNameProperty)) {
            this.modelNameProperty = new SimpleStringProperty(this.modelName);
        }
        return this.modelNameProperty;
    }

    /**
     * 実績数プロパティを取得する。
     *
     * @return 実績数
     */
    public IntegerProperty actualNumProperty() {
        if (Objects.isNull(this.actualNumProperty)) {
            this.actualNumProperty = new SimpleIntegerProperty(this.actualNum);
        }
        return this.actualNumProperty;
    }

    /**
     * 作業種別の順プロパティを取得する。
     *
     * @return 作業種別の順
     */
    public IntegerProperty workTypeOrderProperty() {
        if (Objects.isNull(this.workTypeOrderProperty)) {
            this.workTypeOrderProperty = new SimpleIntegerProperty(this.workTypeOrder);
        }
        return this.workTypeOrderProperty;
    }

    /**
     * 製造番号プロパティを取得する。
     *
     * @return 製造番号
     */
    public StringProperty productionNumberProperty() {
        if (Objects.isNull(this.productionNumberProperty)) {
            this.productionNumberProperty = new SimpleStringProperty(this.productionNumber);
        }
        return this.productionNumberProperty;
    }

    /**
     * 作業種別を取得する。
     *
     * @return 作業種別 (0:直接作業, 1:間接作業, 2:中断時間)
     */
    public Integer getWorkType() {
        if (Objects.nonNull(this.workTypeProperty)) {
            return this.workTypeProperty.get();
        }
        return this.workType;
    }

    /**
     * 作業種別を設定する。
     *
     * @param workType 作業種別 (0:直接作業, 1:間接作業, 2:中断時間)
     */
    public void setWorkType(Integer workType) {
        if (Objects.nonNull(this.workTypeProperty)) {
            this.workTypeProperty.set(workType);
        } else {
            this.workType = workType;
        }
    }

    /**
     * 作業日を取得する。
     *
     * @return 作業日 ('yyyyMMdd')
     */
    public String getWorkDate() {
        if (Objects.nonNull(this.workDateProperty)) {
            return this.workDateProperty.get();
        }
        return this.workDate;
    }

    /**
     * 作業日を設定する。
     *
     * @param workDate 作業日 ('yyyyMMdd')
     */
    public void setWorkDate(String workDate) {
        if (Objects.nonNull(this.workDateProperty)) {
            this.workDateProperty.set(workDate);
        } else {
            this.workDate = workDate;
        }
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getOrganizationId() {
        if (Objects.nonNull(this.organizationIdProperty)) {
            return this.organizationIdProperty.get();
        }
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        if (Objects.nonNull(this.organizationIdProperty)) {
            this.organizationIdProperty.set(organizationId);
        } else {
            this.organizationId = organizationId;
        }
    }

    /**
     * 組織識別名を取得する。
     *
     * @return 組織識別名
     */
    public String getOrganizationIdentify() {
        if (Objects.nonNull(this.organizationIdentifyProperty)) {
            return this.organizationIdentifyProperty.get();
        }
        return this.organizationIdentify;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param organizationIdentify 組織識別名
     */
    public void setOrganizationIdentify(String organizationIdentify) {
        if (Objects.nonNull(this.organizationIdentifyProperty)) {
            this.organizationIdentifyProperty.set(organizationIdentify);
        } else {
            this.organizationIdentify = organizationIdentify;
        }
    }

    /**
     * 組織名を取得する。
     *
     * @return 組織名
     */
    public String getOrganizationName() {
        if (Objects.nonNull(this.organizationNameProperty)) {
            return this.organizationNameProperty.get();
        }
        return this.organizationName;
    }

    /**
     * 組織名を設定する。
     *
     * @param organizationName 組織名
     */
    public void setOrganizationName(String organizationName) {
        if (Objects.nonNull(this.organizationNameProperty)) {
            this.organizationNameProperty.set(organizationName);
        } else {
            this.organizationName = organizationName;
        }
    }

    /**
     * 間接工数実績IDを取得する。
     *
     * @return 間接工数実績ID
     */
    public Long getIndirectActualId() {
        if (Objects.nonNull(this.indirectActualIdProperty)) {
            return this.indirectActualIdProperty.get();
        }
        return this.indirectActualId;
    }

    /**
     * 間接工数実績IDを設定する。
     *
     * @param indirectActualId 間接工数実績ID
     */
    public void setIndirectActualId(Long indirectActualId) {
        if (Objects.nonNull(this.indirectActualIdProperty)) {
            this.indirectActualIdProperty.set(indirectActualId);
        } else {
            this.indirectActualId = indirectActualId;
        }
    }

    /**
     * 作業IDを取得する。
     *
     * @return 作業ID
     */
    public Long getWorkId() {
        if (Objects.nonNull(this.workIdProperty)) {
            return this.workIdProperty.get();
        }
        return this.workId;
    }

    /**
     * 作業IDを設定する。
     *
     * @param workId 作業ID
     */
    public void setWorkId(Long workId) {
        if (Objects.nonNull(this.workIdProperty)) {
            this.workIdProperty.set(workId);
        } else {
            this.workId = workId;
        }
    }

    /**
     * 分類番号を取得する。
     *
     * @return 分類番号
     */
    public String getClassNumber() {
        if (Objects.nonNull(this.classNumberProperty)) {
            return this.classNumberProperty.get();
        }
        return this.classNumber;
    }

    /**
     * 分類番号を設定する。
     *
     * @param classNumber 分類番号
     */
    public void setClassNumber(String classNumber) {
        if (Objects.nonNull(this.classNumberProperty)) {
            this.classNumberProperty.set(classNumber);
        } else {
            this.classNumber = classNumber;
        }
    }

    /**
     * 作業番号を取得する。
     *
     * @return 作業番号
     */
    public String getWorkNumber() {
        if (Objects.nonNull(this.workNumberProperty)) {
            return this.workNumberProperty.get();
        }
        return this.workNumber;
    }

    /**
     * 作業番号を設定する。
     *
     * @param workNumber 作業番号
     */
    public void setWorkNumber(String workNumber) {
        if (Objects.nonNull(this.workNumberProperty)) {
            this.workNumberProperty.set(workNumber);
        } else {
            this.workNumber = workNumber;
        }
    }

    /**
     * 作業名を取得する。
     *
     * @return 作業名
     */
    public String getWorkName() {
        if (Objects.nonNull(this.workNameProperty)) {
            return this.workNameProperty.get();
        }
        return this.workName;
    }

    /**
     * 作業名を設定する。
     *
     * @param workName 作業名
     */
    public void setWorkName(String workName) {
        if (Objects.nonNull(this.workNameProperty)) {
            this.workNameProperty.set(workName);
        } else {
            this.workName = workName;
        }
    }

    /**
     * 注文番号を取得する。
     *
     * @return 注文番号
     */
    public String getOrderNumber() {
        if (Objects.nonNull(this.orderNumberProperty)) {
            return this.orderNumberProperty.get();
        }
        return this.orderNumber;
    }

    /**
     * 注文番号を設定する。
     *
     * @param orderNumber 注文番号
     */
    public void setOrderNumber(String orderNumber) {
        if (Objects.nonNull(this.orderNumberProperty)) {
            this.orderNumberProperty.set(orderNumber);
        } else {
            this.orderNumber = orderNumber;
        }
    }

    /**
     * 工数(ms)を取得する。
     *
     * @return 工数(ms)
     */
    public Integer getWorkTime() {
        if (Objects.nonNull(this.workTimeProperty)) {
            return this.workTimeProperty.get();
        }
        return this.workTime;
    }

    /**
     * 工数(ms)を設定する。
     *
     * @param workTime 工数(ms)
     */
    public void setWorkTime(Integer workTime) {
        if (Objects.nonNull(this.workTimeProperty)) {
            this.workTimeProperty.set(workTime);
        } else {
            this.workTime = workTime;
        }
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getWorkflowId() {
        if (Objects.nonNull(this.workflowIdProperty)) {
            return this.workflowIdProperty.get();
        }
        return this.workflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(Long workflowId) {
        if (Objects.nonNull(this.workflowIdProperty)) {
            this.workflowIdProperty.set(workflowId);
        } else {
            this.workflowId = workflowId;
        }
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        if (Objects.nonNull(this.kanbanNameProperty)) {
            return this.kanbanNameProperty.get();
        }
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        if (Objects.nonNull(this.kanbanNameProperty)) {
            this.kanbanNameProperty.set(kanbanName);
        } else {
            this.kanbanName = kanbanName;
        }
    }

    /**
     * モデル名を取得する。
     *
     * @return
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 実績数を取得する。
     *
     * @return
     */
    public Integer getActualNum() {
        return actualNum;
    }

    /**
     * 実績数を設定する。
     *
     * @param actualNum
     */
    public void setActualNum(Integer actualNum) {
        this.actualNum = actualNum;
    }

    /**
     * 作業種別の順を取得する。
     *
     * @return 作業種別の順
     */
    public Integer getWorkTypeOrder() {
        return this.workTypeOrder;
    }

    /**
     * 作業種別の順を設定する。
     *
     * @param workTypeOrder 作業種別の順
     */
    public void setWorkTypeOrder(Integer workTypeOrder) {
        this.workTypeOrder = workTypeOrder;
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getProductionNumber() {
        if (Objects.nonNull(this.productionNumberProperty)) {
            return this.productionNumberProperty.get();
        }
        return this.productionNumber;
    }

    /**
     * 製造番号を設定する。
     *
     * @param productionNumber 製造番号
     */
    public void setProductionNumber(String productionNumber) {
        if (Objects.nonNull(this.productionNumberProperty)) {
            this.productionNumberProperty.set(productionNumber);
        } else {
            this.productionNumber = productionNumber;
        }
    }

    /**
     * 追加情報を取得する。
     *
     * @return 追加情報
     */
    public String getWorkReprotAddInfo() {
        return workReprotAddInfo;
    }

    /**
     * 追加情報を設定する。
     *
     * @param workReprotAddInfo 追加情報
     */
    public void setWorkReprotAddInfo(String workReprotAddInfo) {
        this.workReprotAddInfo = workReprotAddInfo;
    }

    /**
     * 分類キーを取得する。
     * 
     * @return 分類キー 
     */
    public String getClassKey() {
        return classKey;
    }

    /**
     * 分類キーを設定する。
     * 
     * @param classKey 分類キー
     */
    public void setClassKey(String classKey) {
        this.classKey = classKey;
    }

    /**
     * シリアル番号を取得する。
     * 
     * @return シリアル番号
     */
    public String getSerialNumbers() {
        return serialNumbers;
    }

    /**
     * ハッシュコードを取得する。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.workType);
        hash = 59 * hash + Objects.hashCode(this.workDate);
        hash = 59 * hash + Objects.hashCode(this.organizationId);
        hash = 59 * hash + Objects.hashCode(this.indirectActualId);
        hash = 59 * hash + Objects.hashCode(this.workId);
        hash = 59 * hash + Objects.hashCode(this.classNumber);
        hash = 59 * hash + Objects.hashCode(this.orderNumber);
        hash = 59 * hash + Objects.hashCode(this.workflowId);
        hash = 59 * hash + Objects.hashCode(this.kanbanName);
        hash = 59 * hash + Objects.hashCode(this.productionNumber);
        hash = 59 * hash + Objects.hashCode(this.classKey);
        hash = 59 * hash + Objects.hashCode(this.serialNumbers);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true: 等しい(同値)、false: 異なる
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkReportInfoEntity other = (WorkReportInfoEntity) obj;
        if (!Objects.equals(this.workType, other.workType)) {
            return false;
        }
        if (!Objects.equals(this.workDate, other.workDate)) {
            return false;
        }
        if (!Objects.equals(this.organizationId, other.organizationId)) {
            return false;
        }
        if (!Objects.equals(this.indirectActualId, other.indirectActualId)) {
            return false;
        }
        if (!Objects.equals(this.workId, other.workId)) {
            return false;
        }
        if (!Objects.equals(this.classNumber, other.classNumber)) {
            return false;
        }
        if (!Objects.equals(this.orderNumber, other.orderNumber)) {
            return false;
        }
        if (!Objects.equals(this.workflowId, other.workflowId)) {
            return false;
        }
        if (!Objects.equals(this.kanbanName, other.kanbanName)) {
            return false;
        }
        if (!Objects.equals(this.productionNumber, other.productionNumber)) {
            return false;
        }
        if (!Objects.equals(this.classKey, other.classKey)) {
            return false;
        }
        return Objects.equals(this.serialNumbers, other.serialNumbers);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("WorkReportInfoEntity{")
                .append("workType=").append(this.workType)
                .append(", workDate=").append(this.workDate)
                .append(", organizationId=").append(this.organizationId)
                .append(", organizationIdentify=").append(this.organizationIdentify)
                .append(", organizationName=").append(this.organizationName)
                .append(", indirectActualId=").append(this.indirectActualId)
                .append(", workId=").append(this.workId)
                .append(", classNumber=").append(this.classNumber)
                .append(", workNumber=").append(this.workNumber)
                .append(", workName=").append(this.workName)
                .append(", orderNumber=").append(this.orderNumber)
                .append(", workTime=").append(this.workTime)
                .append(", workflowId=").append(this.workflowId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", modelNmae=").append(this.modelName)
                .append(", actualNum=").append(this.actualNum)
                .append(", workTypeOrder=").append(this.workTypeOrder)
                .append(", productionNumber=").append(this.productionNumber)
                .append(", classKey=").append(this.classKey)
                .append(", serialNumbers=").append(this.serialNumbers)
                .append("}")
                .toString();
    }
}
