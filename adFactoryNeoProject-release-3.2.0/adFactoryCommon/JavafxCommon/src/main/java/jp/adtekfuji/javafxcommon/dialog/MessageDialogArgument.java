/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.dialog;

import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogButtons;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogType;

/**
 * メッセージダイアログの引数
 *
 * @author nar-nakamura
 */
public class MessageDialogArgument {

    private final String message;
    private final MessageDialogType dialogType;
    private final MessageDialogButtons dialogButtons;

    private double borderWidth;
    private String borderColor;
    private String backgroundColor;

    /**
     * メッセージダイアログの引数
     *
     * @param message メッセージ
     * @param dialogType ダイアログ種別
     * @param dialogButtons 表示ボタン種別
     */
    public MessageDialogArgument(String message, MessageDialogType dialogType, MessageDialogButtons dialogButtons) {
        this.message = message;
        this.dialogType = dialogType;
        this.dialogButtons = dialogButtons;
        this.borderWidth = 3.0;
        this.borderColor = "#000000";
        this.backgroundColor = "#ffffff";
    }

    /**
     * メッセージを取得する。
     *
     * @return メッセージ
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * ダイアログ種別を取得する。
     *
     * @return ダイアログ種別
     */
    public MessageDialogType getDialogType() {
        return this.dialogType;
    }

    /**
     * 表示ボタン種別を取得する。
     *
     * @return 表示ボタン種別
     */
    public MessageDialogButtons getDialogButtons() {
        return this.dialogButtons;
    }

    /**
     * メッセージ欄の枠線の太さを取得する。
     *
     * @return メッセージ欄の枠線の太さ
     */
    public double getBorderWidth() {
        return this.borderWidth;
    }

    /**
     * メッセージ欄の枠線の太さを設定する。
     *
     * @param value メッセージ欄の枠線の太さ
     */
    public void setBorderWidth(double value) {
        this.borderWidth = value;
    }

    /**
     * メッセージ欄の枠線の色を取得する。
     *
     * @return メッセージ欄の枠線の色
     */
    public String getBorderColor() {
        return this.borderColor;
    }

    /**
     * メッセージ欄の枠線の色を設定する。
     *
     * @param value メッセージ欄の枠線の色
     */
    public void setBorderColor(String value) {
        this.borderColor = value;
    }

    /**
     * メッセージ欄の背景の色を取得する。
     *
     * @return メッセージ欄の背景の色
     */
    public String getBackgroundColor() {
        return this.backgroundColor;
    }

    /**
     * メッセージ欄の背景の色を設定する。
     *
     * @param value メッセージ欄の背景の色
     */
    public void setBackgroundColor(String value) {
        this.backgroundColor = value;
    }
}
