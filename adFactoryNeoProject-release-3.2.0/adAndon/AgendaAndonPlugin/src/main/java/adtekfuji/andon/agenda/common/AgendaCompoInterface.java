/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.common;

/**
 * AgendaCompornentのインターフェース
 * @author kenji.yokoi
 */
public interface AgendaCompoInterface {
    /**
     * 表示更新
     */
    void updateDisplay();

    /**
     * 呼出しを使うか
     * @return true:呼出しを利用する
     */
    default boolean isUseCall() { return false; };

    /**
     * 表示更新
     * @param isUpdateTimeDrawing 更新時間での描画フラグ
     */
    default void updateDisplay(boolean isUpdateTimeDrawing) {  };
}
