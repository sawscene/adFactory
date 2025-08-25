/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin.common;

/**
 * インポート結果を出力する方法を決定する
 *
 * @author fu-kato
 */
public interface WorkPlanResultLogger {

    /**
     * インポート結果の出力を行う
     *
     * @param message
     */
    public void addResult(String message);
}
