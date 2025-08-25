/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.service;

/**
 * セル生産進捗モニタネイティブサービスインターフェース
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.20.Thr
 */
public interface CellProductionMonitorServiceInterface {
    
    /**
     * 情報受信
     * 
     * @param kanbanId コマンド
     */
    public void receivedActualDataKanbanId(long kanbanId);        
    
}
