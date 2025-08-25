/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.utils;

import javafx.scene.control.Alert.AlertType;

/**
 * 情報チェック処理の応答結果
 *
 * @author ek.mori
 * @version 1.6.1
 * @since 2017.01.25.Wen
 */
public class CheckerUtilEntity {
    
    private final Boolean isSuccsess;
    private final AlertType alertType;
    private final String errTitle;
    private final String errMessage;

    public CheckerUtilEntity(Boolean isSuccsess, AlertType alertType, String errTitle, String errMessage) {
        this.isSuccsess = isSuccsess;
        this.alertType = alertType;
        this.errTitle = errTitle;
        this.errMessage = errMessage;
    }

    public Boolean isSuccsess() {
        return isSuccsess;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public String getErrTitle() {
        return errTitle;
    }

    public String getErrMessage() {
        return errMessage;
    }
    
    
}
