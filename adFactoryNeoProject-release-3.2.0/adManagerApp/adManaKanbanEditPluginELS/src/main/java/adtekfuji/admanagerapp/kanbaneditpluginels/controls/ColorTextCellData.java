/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.controls;

import javafx.scene.paint.Color;

/**
 * 色指定可能なテキストセルデータ
 *
 * @author nar-nakamura
 */
public class ColorTextCellData {

    private final String text;
    private Color textColor;

    /**
     * コンストラクタ
     *
     * @param text テキスト
     */
    public ColorTextCellData(String text) {
        this.text = text;
        this.textColor = Color.BLACK;
    }

    /**
     * コンストラクタ
     *
     * @param text テキスト
     * @param textColor テキストの色
     */
    public ColorTextCellData(String text, Color textColor) {
        this.text = text;
        this.textColor = textColor;
    }

    /**
     * テキストを取得する。
     *
     * @return テキスト
     */
    public String getText() {
        return this.text;
    }

    /**
     * テキストの色を取得する。
     *
     * @return テキストの色
     */
    public Color getTextColor() {
        return this.textColor;
    }

    /**
     * テキストの色を設定する。
     *
     * @param value テキストの色
     */
    public void setTextColor(Color value) {
        this.textColor = value;
    }
}
