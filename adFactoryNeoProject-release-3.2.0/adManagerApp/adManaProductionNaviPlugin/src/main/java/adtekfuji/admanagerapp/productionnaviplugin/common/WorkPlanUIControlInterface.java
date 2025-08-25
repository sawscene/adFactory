/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

/**
 * コントローラのUI操作用インターフェース
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.27.Thu
 */
public interface WorkPlanUIControlInterface {
    
    public void updateUI();

    /**
     * ブロックUI操作
     *
     * @param isBlock ブロックUIの有無
     */
    public void blockUI(boolean isBlock);
}
