/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.response;

import java.io.Serializable;
import java.net.URI;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;

/**
 * 結果情報 (工程順)
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "responseWorkflow")
public class ResponseWorkflowEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private final Boolean isSuccess;
    @XmlElement()
    private String uri;
    @XmlElement()
    private ServerErrorTypeEnum errorType;
    @XmlElement()
    private Long errorCode;
    @XmlElement()
    private WorkflowEntity value;

    /**
     * コンストラクタ
     */
    public ResponseWorkflowEntity() {
        this.isSuccess = null;
    }

    /**
     * コンストラクタ
     *
     * @param isSuccess 
     */
    private ResponseWorkflowEntity(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * 結果を成功にする。
     *
     * @return 
     */
    public static ResponseWorkflowEntity success() {
        return new ResponseWorkflowEntity(true).errorType(ServerErrorTypeEnum.SUCCESS);
    }

    /**
     * 結果を失敗にして、エラー種別を設定する。
     *
     * @param errorType エラー種別
     * @return 
     */
    public static ResponseWorkflowEntity failed(ServerErrorTypeEnum errorType) {
        return new ResponseWorkflowEntity(false).errorType(errorType);
    }

    /**
     * URIを設定する。
     *
     * @param uri URI
     * @return 
     */
    public ResponseWorkflowEntity uri(URI uri) {
        this.uri = uri.getPath();
        return this;
    }

    /**
     * エラー種別を設定する。
     *
     * @param errorType エラー種別
     * @return 
     */
    public ResponseWorkflowEntity errorType(ServerErrorTypeEnum errorType) {
        this.errorType = errorType;
        return this;
    }

    /**
     * エラーコードを設定する。
     *
     * @param errorCode エラーコード
     * @return 
     */
    public ResponseWorkflowEntity errorCode(Long errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    /**
     * 成功したか？
     *
     * @return (true:成功, false:失敗)
     */
    public Boolean isSuccess() {
        return this.isSuccess;
    }

    /**
     * URIを取得する。
     *
     * @return URI
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * エラー種別を取得する。
     *
     * @return エラー種別
     */
    public ServerErrorTypeEnum getErrorType() {
        return this.errorType;
    }

    /**
     * エラーコードを取得する。
     *
     * @return エラーコード
     */
    public Long getErrorCode() {
        return errorCode;
    }

    /**
     * 値を取得する。
     *
     * @return 値
     */
    public WorkflowEntity getValue() {
        return this.value;
    }

    /**
     * 値を設定する。
     *
     * @param value 値
     */
    public void setValue(WorkflowEntity value) {
        this.value = value;
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
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ResponseWorkflowEntity{")
                .append("isSuccess=").append(this.isSuccess)
                .append(", uri=").append(this.uri)
                .append(", errorType=").append(this.errorType)
                .append(", errorCode=").append(this.errorCode)
                .append(", value=").append(this.value)
                .toString();
    }
}
