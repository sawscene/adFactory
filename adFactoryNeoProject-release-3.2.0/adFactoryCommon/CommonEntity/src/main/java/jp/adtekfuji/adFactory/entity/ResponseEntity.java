/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.Serializable;
import java.net.URI;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;

/**
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class ResponseEntity implements Serializable {

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
    private String resources;
    @XmlTransient()
    private Exception exception;
    @XmlTransient()
    private Object userData;
    @XmlTransient()
    private Long nextTransactionId;
  

    public ResponseEntity() {
        this.isSuccess = null;
    }
    
    public ResponseEntity(Exception exception) {
        this.isSuccess = null;
        this.exception = exception;
    }
    
    private ResponseEntity(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public static ResponseEntity success() {
        return new ResponseEntity(true).errorType(ServerErrorTypeEnum.SUCCESS);
    }

    public static ResponseEntity failed(ServerErrorTypeEnum errorType) {
        return new ResponseEntity(false).errorType(errorType);
    }

    public ResponseEntity uri(URI uri) {
        this.uri = uri.getPath();
        return this;
    }

    public ResponseEntity errorType(ServerErrorTypeEnum errorType) {
        this.errorType = errorType;
        return this;
    }

    public ResponseEntity errorCode(Long errorCode) {
        this.errorCode = errorCode;
        return this;
    }
    
    /**
     * ユーザーデータ
     * 
     * @param userData ユーザーデータ
     * @return ResponseEntity
     */
    public ResponseEntity userData(Object userData) {
        this.userData = userData;
        return this;
    }

    /**
     * トランザクションID
     * 
     * @param nextTransactionId トランザクションID
     * @return ResponseEntity
     */
    public ResponseEntity nextTransactionId(Long nextTransactionId) {
        this.nextTransactionId = nextTransactionId;
        return this;
    }
    
    /**
     * リソース情報
     * 
     * @param resources リソース情報
     * @return ResponseEntity
     */
    public ResponseEntity resources(String resources) {
        this.resources = resources;
        return this;
    }

    public Boolean isSuccess() {
        return isSuccess;
    }
    
    public Boolean getIsSuccess() {
        return isSuccess;       
    }

    public void setUri(String str) {
        this.uri = str;
    }

    public String getUri() {
        return uri;
    }

    public ServerErrorTypeEnum getErrorType() {
        return errorType;
    }

    public Long getErrorCode() {
        return errorCode;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    /**
     * URI末尾に含まれるIDを取得する
     * @return ID
     */
    public long getUriId() {
        long ret = 0;
        try {
            String[] split = this.uri.split("/");
            ret = Long.parseLong(split[split.length - 1]);
        } catch (Exception ex) {
        }
        return ret;
	}

    /**
     * ユーザーデータを取得する。
     * 
     * @return ユーザーデータ 
     */
    public Object getUserData() {
        return userData;
    }

    /**
     * トランザクションIDを取得する。
     * 
     * @return トランザクションID
     */
    public Long getNextTransactionId() {
        return nextTransactionId;
    }

    /**
     * トランザクションIDを設定する。
     * 
     * @param nextTransactionId トランザクションID
     */
    public void setNextTransactionId(Long nextTransactionId) {
        this.nextTransactionId = nextTransactionId;
    }

    /**
     * リソース情報を取得する。
     * 
     * @return 
     */
    public String getResources() {
        return resources;
    }

    /**
     * リソース情報を設定する。
     * 
     * @param resources 
     */
    public void setResources(String resources) {
        this.resources = resources;
    }

    /**
     * ハッシュコードを取得する。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true: 一致、false: 不一致  
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResponseEntity other = (ResponseEntity) obj;
        return true;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "ResponseEntity{" + "isSuccess=" + isSuccess + ", uri=" + uri + ", errorType=" + errorType + ", errorCode=" + errorCode + '}';
    }

}
