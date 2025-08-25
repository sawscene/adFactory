/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.adlinkbarcode;

/**
 * シリアル通信リスナー
 * 
 * @author s-heya
 */
public interface SerialCommunicationListener {
    /**
     * テキストデータを受信した。
     * 
     * @param text
     */
    void onReceive(String text);
}
