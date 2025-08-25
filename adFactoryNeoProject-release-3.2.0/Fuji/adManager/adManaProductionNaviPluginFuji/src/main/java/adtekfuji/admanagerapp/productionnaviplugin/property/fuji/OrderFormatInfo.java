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
 * 計画情報のフォーマット情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "orderFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderFormatInfo {

    private String csvFileEncode;// CSV: ファイルエンコード
    private String csvFileName;// CSV: ファイル名

    private String csvStartRow;// CSV: 読み込み開始行

//    private String csvWorkCenter;// CSV: ワークセンター
//    private String csvModel;// CSV: 機種
//    private String csvSn;// CSV: S/N
//    private String csvDeliveryDate;// CSV: 納期日
//    private String csvQuantity;// CSV: 数量
//    private String csvOrderNo;// CSV: オーダー番号
//    private String csvProductionOrderNo;// CSV: 製造オーダー番号
//    private String csvSerialNumber;// CSV: シリアル番号
//    private String csvProcessCode;// CSV: 工程コード
//    private String csvProcessName;// CSV: 工程名
//    private String csvTactTime;// CSV: タクトタイム
//    private String csvPartNo;// CSV: 品目コード
//    private String csvProductName;// CSV: 品名
//    private String csvStartDatetime;// CSV: 開始日時
//    private String csvCompDatetime;// CSV: 終了日時
//    private String csvWorkerId;// CSV: 作業員ID
//    private String csvProcedure;// CSV: 手順

    private String fileName;// ファイル名

    /**
     * コンストラクタ
     */
    public OrderFormatInfo() {
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
        return new StringBuilder("OrderFormatInfo{")
                .append("csvFileEncode=").append(this.csvFileEncode)
                .append(", csvFileName=").append(this.csvFileName)
                .append(", csvStartRow=").append(this.csvStartRow)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
