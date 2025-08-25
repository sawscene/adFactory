/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.organization;

import com.fasterxml.jackson.annotation.JsonProperty;
import jp.adtekfuji.adFactory.utility.JsonUtils;

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 設定情報
 *
 * @author yu.nara
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ConfigInfo")
public class ConfigInfoEntity {

    public static final String defBackColor = "#2F80ED";  // Ver.3.0.0 から背景色を明るい青に変更(元は#0070C0)。 2024/11/14 s-heya
    public static final String defFontColor = "#FFFFFF";
    public static final String defConfigEntityJson = JsonUtils.objectToJson(new ConfigInfoEntity());

    @XmlElement()
    @JsonProperty("backColor")
    private String backColor = defBackColor;

    @XmlElement()
    @JsonProperty("fontColor")
    private String fontColor = defFontColor;

    /**
     * コンストラクタ
     */
    public ConfigInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param other
     */
    public ConfigInfoEntity(ConfigInfoEntity other) {
        this.backColor = other.backColor;
    }

    /**
     * 背景色を取得
     * @return 背景色
     */
    public String getBackColor() {
        return backColor;
    }

    /**
     * 背景色を設定
     * @param backColor 背景色
     */
    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }


    /**
     * 文字色を取得
     * @return 文字色
     */
    public String getFontColor() {
        return fontColor;
    }

    /**
     * 文字色を設定
     * @param fontColor 文字色
     */
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * オブジェクトを比較する。
     *
     * @param obj オブジェクト
     * @return true:同じである、false:異なる
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigInfoEntity)) return false;
        ConfigInfoEntity that = (ConfigInfoEntity) o;
        return Objects.equals(backColor, that.backColor)
                && Objects.equals(fontColor, that.fontColor);
    }

    /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.backColor);
        return hash;
    }
}
