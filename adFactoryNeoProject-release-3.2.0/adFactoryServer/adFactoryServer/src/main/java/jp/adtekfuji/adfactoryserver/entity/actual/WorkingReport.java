/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

/**
 * 仕掛中作業情報
 *
 * @author s-heya
 */
public class WorkingReport {
    private Long pairId = null;
    private int workTime = 0;
    private String addInfo;
    

    /**
     * コンストラクタ
     */
    public WorkingReport() {
    }

    /**
     * ペアIDを取得する。
     *
     * @return ペアID
     */
    public Long getPairId() {
        return this.pairId;
    }

    /**
     * ペアIDを設定する。
     *
     * @param pairId ペアID
     */
    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    /**
     * 作業時間[ms]を取得する。
     *
     * @return 作業時間[ms]
     */
    public int getWorkTime() {
        return this.workTime;
    }

    /**
     * 作業時間[ms]を設定する。
     *
     * @param workTime 作業時間[ms]
     */
    public void setWorkTime(int workTime) {
        this.workTime = workTime;
    }

    /**
     * 追加情報を取得する。
     * 
     * @return 追加情報(JSON文字列)
     */
    public String getAddInfo() {
        return addInfo;
    }

    /**
     * 追加情報を設定する。
     * 
     * @param addInfo 追加情報(JSON文字列)
     */
    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }
    
    @Override
    public String toString() {
        return new StringBuilder("QueryActualResponse{")
                .append("pairId=").append(this.pairId)
                .append(", workTime=").append(this.workTime)
                .append("}")
                .toString();
    }
}
