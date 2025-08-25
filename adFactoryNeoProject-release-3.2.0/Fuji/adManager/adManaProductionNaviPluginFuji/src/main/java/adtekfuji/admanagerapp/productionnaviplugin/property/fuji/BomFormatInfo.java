/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.property.fuji;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * BOM情報のフォーマット情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "bomFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class BomFormatInfo {

    private String csvFileEncode;// CSV: ファイルエンコード
    private String csvFileName;// CSV: ファイル名

    private String csvStartRow;// CSV: 読み込み開始行

//    private String csvOrderNo;// CSV: オーダー番号
//    private String csvProductionOrderNo;// CSV: 製造オーダー番号
//    private String csvSerialNumber;// CSV: シリアル番号
//    private String csvOrderPartNo;// CSV: 親品目コード
//    private String csvRequiredPartNo;// CSV: 子品目コード
//    private String csvProcessNo;// CSV: 工程No
//    private String csvProcessCode;// CSV: 工程コード
//    private String csvProcessName;// CSV: 工程名
//    private String csvRequiredQuantity;// CSV: 必要量

    private String fileName;// ファイル名

    /**
     * コンストラクタ
     */
    public BomFormatInfo() {
    }

    /**
     * CSV: ファイルエンコードを取得する。
     *
     * @return CSV: ファイルエンコード
     */
    public String getCsvFileEncode() {
        return this.csvFileEncode;
    }

    /**
     * CSV: ファイルエンコードを設定する。
     *
     * @param csvFileEncode CSV: ファイルエンコード
     */
    public void setCsvFileEncode(String csvFileEncode) {
        this.csvFileEncode = csvFileEncode;
    }

    /**
     * CSV: ファイル名を取得する。
     *
     * @return CSV: ファイル名
     */
    public String getCsvFileName() {
        return this.csvFileName;
    }

    /**
     * CSV: ファイル名を設定する。
     *
     * @param csvFileName CSV: ファイル名
     */
    public void setCsvFileName(String csvFileName) {
        this.csvFileName = csvFileName;
    }

    /**
     * CSV: 読み込み開始行を取得する。
     *
     * @return CSV: 読み込み開始行
     */
    public String getCsvStartRow() {
        return this.csvStartRow;
    }

    /**
     * CSV: 読み込み開始行を設定する。
     *
     * @param csvStartRow CSV: 読み込み開始行
     */
    public void setCsvStartRow(String csvStartRow) {
        this.csvStartRow = csvStartRow;
    }

    /**
     * ファイル名を取得する。
     *
     * @return ファイル名
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * ファイル名を設定する。
     *
     * @param fileName ファイル名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return new StringBuilder("BomFormatInfo{")
                .append("csvFileEncode=").append(this.csvFileEncode)
                .append(", csvFileName=").append(this.csvFileName)
                .append(", csvStartRow=").append(this.csvStartRow)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
