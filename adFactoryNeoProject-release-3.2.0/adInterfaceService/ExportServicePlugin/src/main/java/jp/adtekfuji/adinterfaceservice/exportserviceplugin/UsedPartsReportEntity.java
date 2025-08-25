/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.exportserviceplugin;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;


/**
 * 使用部品連携報告
 *
 * @author y-harada
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "UsedPartsReport")
public class UsedPartsReportEntity implements Serializable {

    //親品目
    @XmlElement()
    private String parentProductNum;
    //親シリアル
    @XmlElement()
    private String parentSerial;
    //子品目
    @XmlElement()
    private String subProductNum;
    //子シリアル
    @XmlElement()
    private String subSerial;

    /**
     * コンストラクタ
     */
    void UsedPartsReport() {
    }

    /**
     * 親品目を取得する
     *
     * @return
     */
    public String getParentProductNum() {
        return this.parentProductNum;
    }

    /**
     * 親品目を設定する。
     *
     * @param productNum 品番
     */
    public void setParentProductNum(String productNum) {
        this.parentProductNum = productNum;
    }
    
    /**
     * 親シリアルを取得する
     *
     * @return
     */
    public String getParentSerial() {
        return this.parentSerial;
    }

    /**
     * 親シリアルを設定する。
     *
     * @param serial シリアル
     */
    public void setParentSerial(String serial) {
        this.parentSerial = serial;
    }

    /**
     * 子品目を取得する
     *
     * @return 
     */
    public String getSubProductNum() {
        return this.subProductNum;
    }

    /**
     * 子品目を設定する。
     *
     * @param productNum 品番
     */
    public void setSubProductNum(String productNum) {
        this.subProductNum = productNum;
    }
    
    /**
     * 子シリアルを取得する
     *
     * @return 
     */
    public String getSubSerial() {
        return this.subSerial;
    }

    /**
     * 子シリアルを設定する。
     *
     * @param serial シリアル
     */
    public void setSubSerial(String serial) {
        this.subSerial = serial;
    }

    @Override
    public String toString() {
        return "UsedPartsReport{"
                + "parentProductNum=" + this.parentProductNum
                + ", parentSerial=" + this.parentSerial
                + ", subProductNum=" + this.subProductNum
                + ", subSerial=" + this.subSerial
                + '}';
    }   
}
