/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.master;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

/**
 * サービス情報
 *
 * @author nar-nakamura
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ServiceInfoEntity implements Serializable {

    public final static String SERVICE_INFO_PRODUCT = "product";
    public final static String SERVICE_INFO_DEFECT = "defect";
    public final static String SERVICE_INFO_KANBANQR = "kanbanQr";
    public final static String SERVICE_INFO_MULTI = "multi";
    public final static String SERVICE_INFO_DSKANBAN = "dsKanban";
    public final static String SERVICE_INFO_DSPICKUP = "dsPickup";
    public final static String SERVICE_INFO_COMMENTS = "comments";

    private String service;// サービス名
    private Object job;// 作業情報

    /**
     * コンストラクタ
     */
    public ServiceInfoEntity() {
    }

    /**
     * サービス名を取得する。
     *
     * @return サービス名
     */
    public String getService() {
        return this.service;
    }

    /**
     * サービス名を設定する。
     *
     * @param service サービス名
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * 作業情報を取得する。
     *
     * @return 作業情報
     */
    public Object getJob() {
        return this.job;
    }

    /**
     * 作業情報を設定する。
     *
     * @param job 作業情報
     */
    public void setJob(Object job) {
        this.job = job;
    }
  
    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現 
     */
    @Override
    public String toString() {
        return new StringBuilder("ServiceInfoEntity{")
                .append("service=").append(this.service)
                .append(", job=").append(this.job)
                .append("}")
                .toString();
    }
}
