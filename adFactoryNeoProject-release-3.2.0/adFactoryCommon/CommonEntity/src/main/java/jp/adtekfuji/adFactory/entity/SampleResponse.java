/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * REST APIの汎用レスポンス
 * 
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class SampleResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "status", required = false)
    private String status;
 
    @XmlElementWrapper(name = "dataList")
    @XmlElement(name = "data", required = false)
    private List<String> dataList;
    
    @XmlElement(required = false)
    private String result;

    @XmlElement(required = false)
    private String message;
    
    /**
     * コンストラクタ
     */
    public SampleResponse() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param status ステータス
     * @param dataList データ一覧
     */
    public SampleResponse(String status, List<String> dataList) {
        this.status = status;
        this.dataList = dataList;
    }

    /**
     * ステータスを取得する。
     * 
     * @return ステータス
     */
    public String getStatus() {
        return status;
    }

    /**
     * ステータスを設定する。
     * 
     * @param status ステータス
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * データ一覧を取得する。
     * 
     * @return データ一覧
     */
    public List<String> getDataList() {
        return dataList;
    }

    /**
     * データ一覧を設定する。
     * 
     * @param dataList データ一覧
     */
    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    /**
     * 結果を取得する。
     * 
     * @return 結果
     */
    public String getResult() {
        return result;
    }

    /**
     * エラーメッセージを取得する。
     * 
     * @return エラーメッセージ
     */
    public String getMessage() {
        return message;
    }

    /**
     * 文字列表現を取得する。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "SampleResponse{" + "status=" + status + ", dataList=" + dataList + ", result=" + result + ", message=" + message + '}';
    }

}
