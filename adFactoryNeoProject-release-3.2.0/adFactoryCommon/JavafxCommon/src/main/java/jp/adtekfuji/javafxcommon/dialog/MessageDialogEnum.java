/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.dialog;

/**
 *
 * @author nar-nakamura
 */
public class MessageDialogEnum {

    public enum MessageDialogType {
        None,
        Infomation,
        Question,
        Warning,
        Error;
    }

    public enum MessageDialogButtons {
        OK,
        OKCancel,
        YesNo,
        YesNoCancel,
        YesToAllNoCancel,
        YesToAllNoToAllCancel,
    }

    public enum MessageDialogResult {
        Yes,
        YesToAll,
        No,
        NoToAll,
        Ok,
        Cancel;
    }
}
