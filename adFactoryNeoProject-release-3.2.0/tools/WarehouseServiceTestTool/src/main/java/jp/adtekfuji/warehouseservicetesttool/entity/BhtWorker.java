/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.warehouseservicetesttool.entity;

/**
 * ハンディ端末 作業者マスタ
 * @author nar-nakamura
 */
public class BhtWorker {
    private String workerCode;
    private String workerId;
    private String workerName;
    
    public BhtWorker() {
        
    }
    
    /**
     * 作業者マスタ
     * @param workerCode
     * @param workerId
     * @param workerName 
     */
    public BhtWorker(String workerCode, String workerId, String workerName) {
        this.workerCode = workerCode;
        this.workerId = workerId;
        this.workerName = workerName;
    }
    
    /**
     * 作業者バーコード
     * @return 
     */
    public String getWorkerCode() {
        return this.workerCode;
    }
    /**
     * 作業者バーコード
     * @param workerCode 
     */
    public void setWorkerCode(String workerCode) {
        this.workerCode = workerCode;
    }
    
    /**
     * 作業者ID
     * @return 
     */
    public String getWorkerId() {
        return this.workerId;
    }
    /**
     * 作業者ID
     * @param workerId 
     */
    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }
    
    /**
     * 作業者名
     * @return 
     */
    public String getWorkerName() {
        return this.workerName;
    }
    /**
     * 作業者名
     * @param workerName 
     */
    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }
    
    @Override
    public String toString() {
        return "BhtWorker{" +
                "workerCode=" + this.workerCode +
                ", workerId=" + this.workerId +
                ", workerName=" + this.workerName +
                "}";
    }    
}
