/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.services;

import adtekfuji.admanagerapp.warehouseplugin.services.ImportParameter;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * インポート設定情報
 * 
 * @author 14-0282
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WarehouseCommonSettingInfo {
    public static final String FORMAT_CSV = "CSV";
    public static final String FORMAT_EXCEL = "EXCEL";
    public static final String CSV_SEPARATOR = ",";
    
    @XmlElement(name="format")
    private final String format;
    @XmlElement(name="filename")
    private final String fileName;
    @XmlElement(name="doublequotation")
    private final boolean doubleQuotation;
    @XmlElement(name="encode")
    private final String encode;
    @XmlElement(name="separator")
    private final String separator;
    @XmlElement(name="startline")
    private final Integer startLine;
    @XmlElement(name="qrCode")
    private final String qrCode;
    @XmlElement(name="parameter")
    private final List<ImportParameter> parameters;
    
    /**
     * コンストラクタ
     */
    public WarehouseCommonSettingInfo(){
        this.format = null;
        this.fileName = null;
        this.doubleQuotation = true;
        this.encode = "S-JIS";
        this.separator = CSV_SEPARATOR;
        this.startLine = 1;
        this.qrCode = null;
        this.parameters = new ArrayList<>();
    }

    /**
     * フォーマットを取得する。
     * 
     * @return フォーマット
     */
    public String getFormat() {
        return format;
    }

    /**
     * ファイル名を取得する。
     * 
     * @return ファイル名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * ダブルクォーテーションを取得する。
     * 
     * @return ダブルクォーテーション
     */
    public boolean isDoubleQuotation() {
        return doubleQuotation;
    }

    /**
     * 文字コードを取得する。
     * 
     * @return 文字コード
     */
    public String getEncode() {
        return encode;
    }

    /**
     * 区切り文字
     * 
     * @return 区切り文字
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * 読み込み開始ライン番号を取得する。
     * 
     * @return 読み込み開始ライン番号
     */
    public Integer getStartLine() {
        return startLine;
    }

    /**
     * QRコードの書式を取得する。
     * 
     * @return QRコードの書式
     */
    public String getQRCode() {
        return qrCode;
    }

    /**
     * パラメータを取得する。
     * 
     * @return パラメーター
     */
    public List<ImportParameter> getParameters() {
        return parameters;
    }
    
    /**
     * 文字列表現を取得する。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("WarehouseCommonSettingInfo{")
            .append("format=").append(this.format)
            .append(", fileName=").append(this.fileName)
            .append(", doubleQuotation=").append(this.doubleQuotation)
            .append(", encode=").append(this.encode)
            .append(", separator=").append(this.separator)
            .append(", startLine=").append(this.startLine)
            .append(", parameters=").append(this.parameters)
            .append("}")
            .toString();
    }    
}
