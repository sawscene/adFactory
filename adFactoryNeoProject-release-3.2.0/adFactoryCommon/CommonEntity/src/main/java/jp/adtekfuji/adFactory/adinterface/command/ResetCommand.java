/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adinterface.command;

import java.io.Serializable;

/**
 * リセットコマンド
 *
 * @author s-heya
 */
public class ResetCommand  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long monitorId;

    /**
     * コンストラクタ
     */
    public ResetCommand() {
    }

    /**
     * コンストラクタ
     * 
     * @param monitorId モニターID 
     */
    public ResetCommand(Long monitorId) {
        this.monitorId = monitorId;
    }

    /**
     * モニターIDを取得する。
     * 
     * @return モニターID
     */
    public Long getMonitorId() {
        return monitorId;
    }

    /**
     * モニターIDを設定する。
     * 
     * @param monitorId モニターID
     */
    public void setMonitorId(long monitorId) {
        this.monitorId = monitorId;
    }
}
