/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.importformat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 休日のフォーマット情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "holidayFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class HolidayFormatInfo {

    private String csvFileEncode;
    private String xlsSheetName;

    private String csvStartRow;
    private String xlsStartRow;
    private String csvHolidayDate;
    private String xlsHolidayDate;
    private String csvHolidayName;
    private String xlsHolidayName;

    /**
     * コンストラクタ
     */
    public HolidayFormatInfo() {
    }

    /**
     *
     * @return
     */
    public String getCsvFileEncode() {
        return this.csvFileEncode;
    }

    /**
     *
     * @param csvFileEncode
     */
    public void setCsvFileEncode(String csvFileEncode) {
        this.csvFileEncode = csvFileEncode;
    }

    /**
     *
     * @return
     */
    public String getXlsSheetName() {
        return this.xlsSheetName;
    }

    /**
     *
     * @param xlsSheetName
     */
    public void setXlsSheetName(String xlsSheetName) {
        this.xlsSheetName = xlsSheetName;
    }

    /**
     *
     * @return
     */
    public String getCsvStartRow() {
        return this.csvStartRow;
    }

    /**
     *
     * @param csvStartRow
     */
    public void setCsvStartRow(String csvStartRow) {
        this.csvStartRow = csvStartRow;
    }

    /**
     *
     * @return
     */
    public String getXlsStartRow() {
        return this.xlsStartRow;
    }

    /**
     *
     * @param xlsStartRow
     */
    public void setXlsStartRow(String xlsStartRow) {
        this.xlsStartRow = xlsStartRow;
    }

    /**
     *
     * @return
     */
    public String getCsvHolidayDate() {
        return this.csvHolidayDate;
    }

    /**
     *
     * @param csvHolidayDate
     */
    public void setCsvHolidayDate(String csvHolidayDate) {
        this.csvHolidayDate = csvHolidayDate;
    }

    /**
     *
     * @return
     */
    public String getXlsHolidayDate() {
        return this.xlsHolidayDate;
    }

    /**
     *
     * @param xlsHolidayDate
     */
    public void setXlsHolidayDate(String xlsHolidayDate) {
        this.xlsHolidayDate = xlsHolidayDate;
    }

    /**
     *
     * @return
     */
    public String getCsvHolidayName() {
        return this.csvHolidayName;
    }

    /**
     *
     * @param csvHolidayName
     */
    public void setCsvHolidayName(String csvHolidayName) {
        this.csvHolidayName = csvHolidayName;
    }

    /**
     *
     * @return
     */
    public String getXlsHolidayName() {
        return this.xlsHolidayName;
    }

    /**
     *
     * @param xlsHolidayName
     */
    public void setXlsHolidayName(String xlsHolidayName) {
        this.xlsHolidayName = xlsHolidayName;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanFormatInfo{")
                .append("csvFileEncode=").append(this.csvFileEncode)
                .append(", xlsSheetName=").append(this.xlsSheetName)
                .append(", csvStartRow=").append(this.csvStartRow)
                .append(", xlsStartRow=").append(this.xlsStartRow)
                .append(", csvHolidayDate=").append(this.csvHolidayDate)
                .append(", xlsHolidayDate=").append(this.xlsHolidayDate)
                .append(", csvHolidayName=").append(this.csvHolidayName)
                .append(", xlsHolidayName=").append(this.xlsHolidayName)
                .append("}")
                .toString();
    }
}
