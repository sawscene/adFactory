/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.organization;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 認証マスタ
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "authenticationInfo")
public class AuthenticationInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty authenticationIdProperty;
    private LongProperty fkMasterIdProperty;
    private ObjectProperty<Date> authenticationPeriodProperty;
    private StringProperty authenticationTypeProperty;
    private StringProperty authenticationDataProperty;
    private BooleanProperty useLockProperty;

    @XmlElement(required = true)
    private Long authenticationId;
    @XmlElement()
    private Long fkMasterId;
    @XmlElement()
    private Date authenticationPeriod;
    @XmlElement()
    private String authenticationType;
    @XmlElement()
    private String authenticationData;
    @XmlElement()
    private Boolean useLock;

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    public AuthenticationInfoEntity() {
    }

    public AuthenticationInfoEntity(Long authenticationId, Long fkMasterId, String authenticationType, String authenticationData) {
        this.authenticationId = authenticationId;
        this.fkMasterId = fkMasterId;
        this.authenticationType = authenticationType;
        this.authenticationData = authenticationData;
    }

    public LongProperty authenticationIdProperty() {
        if (Objects.isNull(authenticationIdProperty)) {
            authenticationIdProperty = new SimpleLongProperty(authenticationId);
        }
        return authenticationIdProperty;
    }

    public LongProperty fkMasterIdProperty() {
        if (Objects.isNull(fkMasterIdProperty)) {
            fkMasterIdProperty = new SimpleLongProperty(fkMasterId);
        }
        return fkMasterIdProperty;
    }

    public ObjectProperty<Date> authenticationPeriodProperty() {
        if (Objects.isNull(authenticationPeriodProperty)) {
            authenticationPeriodProperty = new SimpleObjectProperty<>(authenticationPeriod);
        }
        return authenticationPeriodProperty;
    }

    public StringProperty authenticationTypeProperty() {
        if (Objects.isNull(authenticationTypeProperty)) {
            authenticationTypeProperty = new SimpleStringProperty(authenticationType);
        }
        return authenticationTypeProperty;
    }

    public StringProperty authenticationDataProperty() {
        if (Objects.isNull(authenticationDataProperty)) {
            authenticationDataProperty = new SimpleStringProperty(authenticationData);
        }
        return authenticationDataProperty;
    }

    public BooleanProperty useLockProperty() {
        if (Objects.isNull(useLockProperty)) {
            useLockProperty = new SimpleBooleanProperty(useLock);
        }
        return useLockProperty;
    }

    public Long getAuthenticationId() {
        if (Objects.nonNull(authenticationIdProperty)) {
            return authenticationIdProperty.get();
        }
        return authenticationId;
    }

    public void setAuthenticationId(Long authenticationId) {
        if (Objects.nonNull(authenticationIdProperty)) {
            authenticationIdProperty.set(authenticationId);
        } else {
            this.authenticationId = authenticationId;
        }
    }

    public Long getFkMasterId() {
        if (Objects.nonNull(fkMasterIdProperty)) {
            return fkMasterIdProperty.get();
        }
        return fkMasterId;
    }

    public void setFkMasterId(Long fkMasterId) {
        if (Objects.nonNull(fkMasterIdProperty)) {
            fkMasterIdProperty.set(fkMasterId);
        } else {
            this.fkMasterId = fkMasterId;
        }
    }

    public Date getAuthenticationPeriod() {
        if (Objects.nonNull(authenticationPeriodProperty)) {
            return authenticationPeriodProperty.get();
        }
        return authenticationPeriod;
    }

    public void setAuthenticationPeriod(Date authenticationPeriod) {
        if (Objects.nonNull(authenticationPeriodProperty)) {
            authenticationPeriodProperty.set(authenticationPeriod);
        } else {
            this.authenticationPeriod = authenticationPeriod;
        }
    }

    public String getAuthenticationType() {
        if (Objects.nonNull(authenticationTypeProperty)) {
            return authenticationTypeProperty.get();
        }
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        if (Objects.nonNull(authenticationTypeProperty)) {
            authenticationTypeProperty.set(authenticationType);
        } else {
            this.authenticationType = authenticationType;
        }
    }

    public String getAuthenticationData() {
        if (Objects.nonNull(authenticationDataProperty)) {
            return authenticationDataProperty.get();
        }
        return authenticationData;
    }

    public void setAuthenticationData(String authenticationValue) {
        if (Objects.nonNull(authenticationDataProperty)) {
            authenticationDataProperty.set(authenticationValue);
        } else {
            this.authenticationData = authenticationValue;
        }
    }

    public Boolean getUseLock() {
        if (Objects.nonNull(useLockProperty)) {
            return useLockProperty.get();
        }
        return useLock;
    }

    public void setUseLock(Boolean useLock) {
        if (Objects.nonNull(useLockProperty)) {
            useLockProperty.set(useLock);
        } else {
            this.useLock = useLock;
        }
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.authenticationId);
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
        final AuthenticationInfoEntity other = (AuthenticationInfoEntity) obj;
        if (!Objects.equals(this.authenticationId, other.authenticationId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("AuthenticationInfoEntity{")
                .append("authenticationId=").append(this.authenticationId)
                .append(", ")
                .append("fkMasterId=").append(this.fkMasterId)
                .append(", ")
                .append("authenticationPeriod=").append(this.authenticationPeriod)
                .append(", ")
                .append("authenticationType=").append(this.authenticationType)
                .append(", ")
                .append("authenticationData=").append(this.authenticationData)
                .append(", ")
                .append("useLock=").append(this.useLock)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
