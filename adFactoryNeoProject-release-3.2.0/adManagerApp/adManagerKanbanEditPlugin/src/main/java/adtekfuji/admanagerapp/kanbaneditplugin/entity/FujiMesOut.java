/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 検査データ 出力用データ。
 * @author okada
 */
public class FujiMesOut {

    /** カンバンID */
    private Long kanbanId;
    /** カンバン名 */
    private String kanbanName;
    /** 工程順ID */
    private Long workflowId;
    /** 工程ID */
    private String workId;
    /** 検査データの検査値情報 */
    List<FujiMesData> checkValueInfos;
    /** エラーフラグ */
    private boolean errorFlag;

    /**
     * 検査データ 出力用データ。
     *
     */
    public FujiMesOut() {
        initialization(0l, "", 0l, "");
    }

    /**
     * 検査データ 出力用データ。
     * 
     * @param kanbanId   カンバンID
     * @param kanbanName カンバン名
     * @param workflowId 工程順ID
     * @param workId     工程ID
     */
    public FujiMesOut(Long kanbanId, String kanbanName, Long workflowId, String workId) {
        initialization(kanbanId, kanbanName, workflowId,workId);
    }

    /**
     * 初期化。
     * 
     * @param kanbanId   カンバンID
     * @param kanbanName カンバン名
     * @param workflowId 工程順ID
     * @param workId     工程ID
     */    
    private void initialization(Long kanbanId, String kanbanName, Long workflowId, String workId) {
        this.kanbanId = kanbanId;
        this.kanbanName = kanbanName;
        this.workflowId = workflowId;
        this.workId = workId;
        this.checkValueInfos = new ArrayList<>();
        this.errorFlag = false;
    }
    

    /**
     * カンバンIDを取得する。
     *
     * @return
     */
    public Long getKanbanId() {
        return this.kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param value
     */
    public void setKanbanId(Long value) {
        this.kanbanId = value;
    }

    /**
     * カンバン名を取得する。
     *
     * @return
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param value
     */
    public void setKanbanName(String value) {
        this.kanbanName = value;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return
     */
    public Long getWorkflowId() {
        return this.workflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param value
     */
    public void setWorkflowId(Long value) {
        this.workflowId = value;
    }

    /**
     * 工程IDを取得する。
     *
     * @return
     */
    public String getWorkId() {
        return this.workId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param value
     */
    public void setWorkId(String value) {
        this.workId = value;
    }

    /**
     * 検査データの検査値情報を設定する。
     *
     * @param value
     */
    public void setCheckValueInfos(List<FujiMesData> value) {
        this.checkValueInfos = value;
    }

    /**
     * 検査データの検査値情報を取得する。
     *
     * @return
     */
    public List<FujiMesData> getCheckValueInfos() {
        return this.checkValueInfos;
    }

    /**
     * エラーフラグを取得する。
     * 
     * @return
     */
    public boolean getErrorFlag() {
        return this.errorFlag;
    }

    /**
     * エラーフラグを設定する。
     * 
     * @param value
     */
    public void setErrorFlag(boolean value) {
        this.errorFlag = value;
    }
    
    
    @Override
    public String toString() {
        return "CheckDataEntity{" +
                "kanbanId=" + this.kanbanId +
                ", kanbanName=" + this.kanbanName +
                ", workflowId=" + this.workflowId +
                ", workId=" + this.workId +
                ", checkValueInfos=" + this.checkValueInfos.toString() +
                ", errorFlag=" + this.errorFlag+
                "}";
    }
}
