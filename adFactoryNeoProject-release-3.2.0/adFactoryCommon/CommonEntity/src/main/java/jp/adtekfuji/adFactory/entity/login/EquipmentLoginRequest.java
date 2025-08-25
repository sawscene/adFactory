/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.login;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;

/**
 * 設備ログイン要求情報.
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "equipmentLoginRequest")
public class EquipmentLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlEnum(String.class)
    public enum LoginType {

        IP4_ADDRESS,
        MAC_ADDRESS,
        IDENT_NAME
    }

    @XmlElement(required = true)
    private LoginType loginType;
    @XmlElement(required = true)
    private EquipmentTypeEnum equipmentType;
    @XmlElement(required = true)
    private String authData;
    @XmlElement(required = false)
    private String macAddress;

    public EquipmentLoginRequest() {
    }

    public EquipmentLoginRequest(LoginType loginType, EquipmentTypeEnum equipmentType, String authData, String macAddress) {
        this.loginType = loginType;
        this.equipmentType = equipmentType;
        this.authData = authData;
        this.macAddress = macAddress;
    }

    public static EquipmentLoginRequest ip4AddressType(EquipmentTypeEnum equipmentType, String address) {
        return new EquipmentLoginRequest(LoginType.IP4_ADDRESS, equipmentType, address, null);
    }

    public static EquipmentLoginRequest macAddressType(EquipmentTypeEnum equipmentType, String address) {
        return new EquipmentLoginRequest(LoginType.MAC_ADDRESS, equipmentType, address, address);
    }

    public static EquipmentLoginRequest identNameType(EquipmentTypeEnum equipmentType, String identName) {
        return new EquipmentLoginRequest(LoginType.IDENT_NAME, equipmentType, identName, null);
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public EquipmentTypeEnum getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentTypeEnum equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getAuthData() {
        return authData;
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final EquipmentLoginRequest other = (EquipmentLoginRequest) obj;
        return true;
    }

    @Override
    public String toString() {
        return "EquipmentLoginRequest{" + "loginType=" + loginType + ", equipmentType=" + equipmentType + ", authData=" + authData + ", macAddress=" + macAddress + '}';
    }

}
