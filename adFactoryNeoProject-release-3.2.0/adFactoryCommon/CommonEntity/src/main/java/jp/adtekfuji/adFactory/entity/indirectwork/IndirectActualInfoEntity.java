/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.indirectwork;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;

/**
 * 間接工数実績
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "indirectActual")
public class IndirectActualInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty indirectActualIdProperty;
    private LongProperty fkIndirectWorkIdProperty;
    private ObjectProperty<Date> implementDatetimeProperty;
    private LongProperty transactionIdProperty;
    private LongProperty fkOrganizationIdProperty;
    private IntegerProperty workTimeProperty;
    private StringProperty productionNumProperty;

    private ObjectProperty<IndirectWorkInfoEntity> indirectWorkProperty;
    private ObjectProperty<OrganizationInfoEntity> organizationProperty;

    @XmlElement(required = true)
    private Long indirectActualId;
    @XmlElement()
    private Long fkIndirectWorkId;
    @XmlElement()
    private Date implementDatetime;
    @XmlElement()
    private Long transactionId;
    @XmlElement()
    private Long fkOrganizationId;
    @XmlElement()
    private Integer workTime;
    @XmlElement()
    private String productionNum;

    @XmlTransient
    private IndirectWorkInfoEntity indirectWork;
    @XmlTransient
    private OrganizationInfoEntity organization;

    /**
     * コンストラクタ
     */
    public IndirectActualInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param indirectActualId 間接工数実績ID
     * @param fkIndirectWorkId 間接作業ID
     * @param implementDatetime 実施日時
     * @param transactionId トランザクションID
     * @param fkOrganizationId 組織ID
     * @param workTime 作業時間[ms]
     * @param productionNum 製造番号
     */
    public IndirectActualInfoEntity(Long indirectActualId, Long fkIndirectWorkId, Date implementDatetime, Long transactionId, Long fkOrganizationId, int workTime, String productionNum) {
        this.indirectActualId = indirectActualId;
        this.fkIndirectWorkId = fkIndirectWorkId;
        this.implementDatetime = implementDatetime;
        this.transactionId = transactionId;
        this.fkOrganizationId = fkOrganizationId;
        this.workTime = workTime;
        this.productionNum = productionNum;
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
     * 間接作業IDプロパティを取得する。
     *
     * @return 間接作業ID
     */
    public LongProperty fkIndirectWorkIdProperty() {
        if (Objects.isNull(this.fkIndirectWorkIdProperty)) {
            this.fkIndirectWorkIdProperty = new SimpleLongProperty(this.fkIndirectWorkId);
        }
        return this.fkIndirectWorkIdProperty;
    }

    /**
     * 実施日時プロパティを取得する。
     *
     * @return 実施日時
     */
    public ObjectProperty<Date> implementDatetimeProperty() {
        if (Objects.isNull(implementDatetimeProperty)) {
            implementDatetimeProperty = new SimpleObjectProperty<>(this.implementDatetime);
        }
        return implementDatetimeProperty;
    }

    /**
     * トランザクションIDプロパティを取得する。
     *
     * @return トランザクションID
     */
    public LongProperty transactionIdProperty() {
        if (Objects.isNull(this.transactionIdProperty)) {
            this.transactionIdProperty = new SimpleLongProperty(this.transactionId);
        }
        return this.transactionIdProperty;
    }

    /**
     * 組織IDプロパティを取得する。
     *
     * @return 組織ID
     */
    public LongProperty fkOrganizationIdProperty() {
        if (Objects.isNull(this.fkOrganizationIdProperty)) {
            this.fkOrganizationIdProperty = new SimpleLongProperty(this.fkOrganizationId);
        }
        return this.fkOrganizationIdProperty;
    }

    /**
     * 作業時間[ms]プロパティを取得する。
     *
     * @return 作業時間[ms]
     */
    public IntegerProperty workTimeProperty() {
        if (Objects.isNull(this.workTimeProperty)) {
            this.workTimeProperty = new SimpleIntegerProperty(this.workTime);
        }
        return this.workTimeProperty;
    }

    /**
     * 製造番号プロパティを取得する。
     *
     * @return 製造番号
     */
    public StringProperty productionNumProperty() {
        if (Objects.isNull(this.productionNumProperty)) {
            this.productionNumProperty = new SimpleStringProperty(this.productionNum);
        }
        return this.productionNumProperty;
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
     * 間接作業IDを取得する。
     *
     * @return 間接作業ID
     */
    public Long getFkIndirectWorkId() {
        if (Objects.nonNull(this.fkIndirectWorkIdProperty)) {
            return this.fkIndirectWorkIdProperty.get();
        }
        return this.fkIndirectWorkId;
    }

    /**
     * 間接作業IDを設定する。
     *
     * @param fkIndirectWorkId 間接作業ID
     */
    public void setFkIndirectWorkId(Long fkIndirectWorkId) {
        if (Objects.nonNull(this.fkIndirectWorkIdProperty)) {
            this.fkIndirectWorkIdProperty.set(fkIndirectWorkId);
        } else {
            this.fkIndirectWorkId = fkIndirectWorkId;
        }
    }

    /**
     * 実施日時を取得する。
     *
     * @return 実施日時
     */
    public Date getImplementDatetime() {
        if (Objects.nonNull(this.implementDatetimeProperty)) {
            return this.implementDatetimeProperty.get();
        }
        return this.implementDatetime;
    }

    /**
     * 実施日時を設定する。
     *
     * @param implementDatetime 実施日時
     */
    public void setImplementDatetime(Date implementDatetime) {
        if (Objects.nonNull(this.implementDatetimeProperty)) {
            this.implementDatetimeProperty.set(implementDatetime);
        } else {
            this.implementDatetime = implementDatetime;
        }
    }

    /**
     * トランザクションIDを取得する。
     *
     * @return トランザクションID
     */
    public Long getTransactionId() {
        if (Objects.nonNull(this.transactionIdProperty)) {
            return this.transactionIdProperty.get();
        }
        return this.transactionId;
    }

    /**
     * トランザクションIDを設定する。
     *
     * @param transactionId トランザクションID
     */
    public void setTransactionId(Long transactionId) {
        if (Objects.nonNull(this.transactionIdProperty)) {
            this.transactionIdProperty.set(transactionId);
        } else {
            this.transactionId = transactionId;
        }
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getFkOrganizationId() {
        if (Objects.nonNull(this.fkOrganizationIdProperty)) {
            return this.fkOrganizationIdProperty.get();
        }
        return this.fkOrganizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param fkOrganizationId 組織ID
     */
    public void setFkOrganizationId(Long fkOrganizationId) {
        if (Objects.nonNull(this.fkOrganizationIdProperty)) {
            this.fkOrganizationIdProperty.set(fkOrganizationId);
        } else {
            this.fkOrganizationId = fkOrganizationId;
        }
    }

    /**
     * 作業時間[ms]を取得する。
     *
     * @return 作業時間[ms]
     */
    public Integer getWorkTime() {
        if (Objects.nonNull(this.workTimeProperty)) {
            return this.workTimeProperty.get();
        }
        return this.workTime;
    }

    /**
     * 作業時間[ms]を設定する。
     *
     * @param workTime 作業時間[ms]
     */
    public void setWorkTime(Integer workTime) {
        if (Objects.nonNull(this.workTimeProperty)) {
            this.workTimeProperty.set(workTime);
        } else {
            this.workTime = workTime;
        }
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getProductionNum() {
        if (Objects.nonNull(this.productionNumProperty)) {
            return this.productionNumProperty.get();
        }
        return this.productionNum;
    }

    /**
     * 製造番号を設定する。
     *
     * @param productionNum 製造番号
     */
    public void setProductionNum(String productionNum) {
        if (Objects.nonNull(this.productionNumProperty)) {
            this.productionNumProperty.set(productionNum);
        } else {
            this.productionNum = productionNum;
        }
    }

    /**
     * 間接作業情報を取得する。
     *
     * @return 間接作業情報
     */
    public IndirectWorkInfoEntity getIndirectWork() {
        if (Objects.nonNull(this.indirectWorkProperty)) {
            return this.indirectWorkProperty.get();
        }
        return this.indirectWork;
    }

    /**
     * 組織情報を取得する。
     *
     * @return 組織情報
     */
    public OrganizationInfoEntity getOrganization() {
        if (Objects.nonNull(this.organizationProperty)) {
            return this.organizationProperty.get();
        }
        return this.organization;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.getIndirectActualId());
        hash = 47 * hash + Objects.hashCode(this.getFkIndirectWorkId());
        hash = 47 * hash + Objects.hashCode(this.getImplementDatetime());
        hash = 47 * hash + Objects.hashCode(this.getTransactionId());
        hash = 47 * hash + Objects.hashCode(this.getFkOrganizationId());
        hash = 47 * hash + Objects.hashCode(this.getWorkTime());
        hash = 47 * hash + Objects.hashCode(this.getProductionNum());
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
        final IndirectActualInfoEntity other = (IndirectActualInfoEntity) obj;
        if (!Objects.equals(this.getIndirectActualId(), other.getIndirectActualId())) {
            return false;
        }
        if (!Objects.equals(this.getFkIndirectWorkId(), other.getFkIndirectWorkId())) {
            return false;
        }
        if (!Objects.equals(this.getImplementDatetime(), other.getImplementDatetime())) {
            return false;
        }
        if (!Objects.equals(this.getTransactionId(), other.getTransactionId())) {
            return false;
        }
        if (!Objects.equals(this.getFkOrganizationId(), other.getFkOrganizationId())) {
            return false;
        }
        if (!Objects.equals(this.getWorkTime(), other.getWorkTime())) {
            return false;
        }
        if (!Objects.equals(this.getProductionNum(), other.getProductionNum())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "IndirectActualEntity{" + "indirectActualId=" + this.getIndirectActualId() + ", fkIndirectWorkId=" + this.getFkIndirectWorkId() + ", implementDatetime=" + this.getImplementDatetime() + ", transactionId=" + this.getTransactionId() + ", workTime=" + this.getWorkTime() + ", productionNum=" + this.getProductionNum() + '}';
    }
}
