/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.work;

import adtekfuji.utility.StringUtils;
import java.io.Serializable;
import javafx.scene.paint.Color;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;

/**
 * 色指定可能なテキストセルデータ
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Default(DefaultType.FIELD)
public class InputValueColor implements Serializable{

    @XmlElement
    @Element(required = true)
    private String text;
    
    @XmlElement
    @Element(required = true)
    private String textColor;
    
    @XmlElement
    @Element(required = true)
    private String textBkColor;
    
    /**
     * コンストラクタ
     *
     * @param　なし
     */
    public InputValueColor() {

    }
        
    /**
     * コンストラクタ
     *
     * @param In
     */
    public InputValueColor(InputValueColor In) {
        this.text = In.getText();
        this.textColor = In.getTextColor();
	this.textBkColor = In.getTextBkColor();
    }

    /**
     * コンストラクタ
     *
     * @param text テキスト
     */
    public InputValueColor(String text) {
        this.text = text;
        this.textColor = StringUtils.colorToRGBCode(Color.BLACK);
	this.textBkColor = StringUtils.colorToRGBCode(Color.WHITE); 
    }

    /**
     * コンストラクタ
     *
     * @param text テキスト
     * @param textColor テキストの色
     * @param textBkColor テキストの背景色
     */
    public InputValueColor(String text, String textColor, String textBkColor) {
        this.text = text;
        this.textColor = textColor;
	this.textBkColor = textBkColor;
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
    public String getTextColor() {
        return this.textColor;
    }

    /**
     * テキストの色を設定する。
     *
     * @param value テキストの色
     */
    public void setTextColor(String value) {
        this.textColor = value;
    }

    /**
     * テキストの背景色を取得する。
     *
     * @return テキストの背景色
     */
    public String getTextBkColor() {
        return this.textBkColor;
    }

    /**
     * テキストの背景色を設定する。
     *
     * @param value テキストの背景色
     */
    public void setTextBkColor(String value) {
        this.textBkColor = value;
    }
    
    @Override
    public String toString() {
        return new StringBuilder("ColorTextBkCellData{")
                .append("TEXT").append(this.text)
                .append(", TEXTCOLOR").append(this.textColor)
                .append(", TEXTBKCOLOR").append(this.textBkColor)
                .append("}")
                .toString();
    }
}
