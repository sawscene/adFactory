/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.fxscene;

import javafx.scene.control.Dialog;

/**
 * ダイアログのインターフェイス
 *
 * @author s-heya
 */
public interface DialogHandler {

    /**
     * Dialogを設定する。
     *
     * @param dialog
     */
    void setDialog(Dialog dialog);
}
