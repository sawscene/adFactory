/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package jp.adtekfuji.javafxcommon.event;

/**
 * アクションイベントリスナー
 * 
 * @author s-heya
 */
public interface ActionEventListener {

    /**
     * アクションイベント
     */
    public enum SceneEvent {
        Close;
    }

    /**
     * アクションイベント通知
     * 
     * @param event アクションイベント
     * @param param パラメーター
     */
    void onNotification(SceneEvent event, Object param);
}
