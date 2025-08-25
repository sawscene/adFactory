/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.reconnectdialog;

import adtekfuji.locale.LocaleUtils;
import jakarta.ws.rs.WebApplicationException;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author e-mori
 */
public class ErrorAnalyzer {

    public String getErrMassage(Exception ex) {
        String errMessage = "";
        if (ex instanceof ExecutionException) {
            ex = ((Exception) ex.getCause());
        }

        //if (ex instanceof ClientHandlerException) {
        //    errMessage = LocaleUtils.getString("key.ServerProblemMessage");
        //} else
        if (ex instanceof RuntimeException || ex instanceof WebApplicationException) {
            errMessage = LocaleUtils.getString("key.ServerReconnectMessage");
        } else {
            errMessage = String.format(LocaleUtils.getString("key.AplicationErrMessage") + "{}", ex.getMessage());
        }
        return errMessage;
    }

    public String getRegistErrMassage(Exception ex, String appName) {
        String errMessage = "";
        if (ex instanceof ExecutionException) {
            ex = ((Exception) ex.getCause());
        }

        //if (ex instanceof ClientHandlerException) {
        //    errMessage = String.format(LocaleUtils.getString("key.ServerProblemRegistrationMessage"),"");
        //} else
        if (ex instanceof RuntimeException || ex instanceof WebApplicationException) {
            errMessage = String.format(LocaleUtils.getString("key.ServerReconnectRegistrationMessage"),"");
        } else {
            errMessage = String.format(LocaleUtils.getString("key.AplicationErrMessage") + "{}", ex.getMessage());
        }
        return errMessage;
    }
}
