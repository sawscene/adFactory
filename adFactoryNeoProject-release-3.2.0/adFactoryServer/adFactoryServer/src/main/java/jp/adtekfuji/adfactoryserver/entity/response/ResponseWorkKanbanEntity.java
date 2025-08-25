/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.response;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;

/**
 * 結果情報(工程カンバン)
 * 
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "responseWorkKanban")
public class ResponseWorkKanbanEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private final Boolean isSuccess;
    @XmlElement()
    private String uri;
    @XmlElement()
    private ServerErrorTypeEnum errorType;
    @XmlElement()
    private Long errorCode;
    @XmlElementWrapper(name = "workKanbans")
    @XmlElement(name = "workKanban")
    private List<WorkKanbanEntity> workKanbans;

    /**
     * コンストラクタ
     */
    public ResponseWorkKanbanEntity() {
        this.isSuccess = null;
    }

    /**
     * コンストラクタ
     *
     * @param isSuccess 
     */
    private ResponseWorkKanbanEntity(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * 結果を成功にする。
     *
     * @param workKanbans
     * @return ResponseWorkKanbanEntity
     */
    public static ResponseWorkKanbanEntity success(List<WorkKanbanEntity> workKanbans) {
        ResponseWorkKanbanEntity entity = new ResponseWorkKanbanEntity(true).errorType(ServerErrorTypeEnum.SUCCESS);
        entity.setWorkKanbans(workKanbans);
        return entity;
    }

    /**
     * 結果を失敗にして、エラー種別を設定する。
     *
     * @param errorType エラー種別
     * @return ResponseWorkKanbanEntity
     */
    public static ResponseWorkKanbanEntity failed(ServerErrorTypeEnum errorType) {
        return new ResponseWorkKanbanEntity(false).errorType(errorType);
    }

    /**
     * URIを設定する。
     *
     * @param uri URI
     * @return ResponseWorkKanbanEntity
     */
    public ResponseWorkKanbanEntity uri(URI uri) {
        this.uri = uri.getPath();
        return this;
    }

    /**
     * エラー種別を設定する。
     *
     * @param errorType エラー種別
     * @return ResponseWorkKanbanEntity
     */
    public ResponseWorkKanbanEntity errorType(ServerErrorTypeEnum errorType) {
        this.errorType = errorType;
        return this;
    }

    /**
     * エラーコードを設定する。
     *
     * @param errorCode エラーコード
     * @return ResponseWorkKanbanEntity
     */
    public ResponseWorkKanbanEntity errorCode(Long errorCode) {
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
     * 工程カンバン一覧を取得する。
     *
     * @return 工程カンバン一覧
     */
    public List<WorkKanbanEntity> getWorkKanbans() {
        return this.workKanbans;
    }

    /**
     * 工程カンバン一覧を設定する。
     * 
     * @param workKanbans 工程カンバン一覧
     */
    public void setWorkKanbans(List<WorkKanbanEntity> workKanbans) {
        this.workKanbans = workKanbans;
    }

    /**
     * ハッシュコードを返す。
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
     * @return treu:一致、false:不一致
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return getClass() == obj.getClass();
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ResponseWorkflowEntity{")
                .append("isSuccess=").append(this.isSuccess)
                .append(", uri=").append(this.uri)
                .append(", errorType=").append(this.errorType)
                .append(", errorCode=").append(this.errorCode)
                .append(", value=").append(this.workKanbans)
                .toString();
    }    
}
