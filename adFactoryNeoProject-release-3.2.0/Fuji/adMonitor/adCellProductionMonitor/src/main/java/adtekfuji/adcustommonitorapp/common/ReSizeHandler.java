/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.common;

/**
 * サイズ変更のハンドル処理
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.8.Thr
 */
public interface ReSizeHandler {

    /**
     * サイズ調整
     *
     * @param xSize
     * @param ySize
     */
    public void resize(double xSize, double ySize);
}
