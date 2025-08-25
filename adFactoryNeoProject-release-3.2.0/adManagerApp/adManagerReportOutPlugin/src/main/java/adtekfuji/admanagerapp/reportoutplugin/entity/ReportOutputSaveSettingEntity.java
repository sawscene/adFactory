/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.reportoutplugin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 * 実績出力画面 設定保存用エンティティ
 *
 * @author y-harada
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportOutputSaveSettingEntity {

    private Map<String, Boolean> additionalColumns;   // 追加項目
    
    private String columnsOrder; // 列の並び順
    private String summaryColumnsOrder; // Summary列の並び順
    private Map<String, String> columnsWidth; // 列の横幅
    private Map<String, String> summaryColumnsWidth; // Summary列の横幅

    private String columnsVisible; // 列の表示/非表示
    private String summaryColumnsVisible; // Summary列の表示/非表示


    /**
     * コンストラクタ
     */
    public ReportOutputSaveSettingEntity() {
    }

    /**
     * 追加項目を取得する。
     *
     * @return 追加項目
     */
    public Map<String, Boolean> getAdditionalColumns() {
        return this.additionalColumns;
    }

    /**
     * 追加項目を設定する。
     *
     * @param additionalColumns 追加項目
     */
    public void setAdditionalColumns(Map<String, Boolean> additionalColumns) {
        this.additionalColumns = additionalColumns;
    }
    
    /**
     * 列の並び順を取得する。
     *
     * @return 列の並び順
     */
    public String getColumnsOrder() {
        return this.columnsOrder;
    }

    /**
     * 列の並び順を設定する。
     *
     * @param columnsOrder 列の並び順
     */
    public void setColumnsOrder(String columnsOrder) {
        this.columnsOrder = columnsOrder;
    }
    
    /**
     * Summary列の並び順を取得する。
     *
     * @return Summary列の並び順
     */
    public String getSummaryColumnsOrder() {
        return this.summaryColumnsOrder;
    }

    /**
     * 列の並び順を設定する。
     *
     * @param summaryColumnsOrder 列の並び順
     */
    public void setSummaryColumnsOrder(String summaryColumnsOrder) {
        this.summaryColumnsOrder = summaryColumnsOrder;
    }
    
    /**
     * 列の横幅を取得する。
     *
     * @return 列の横幅
     */
    public Map<String, String> getColumnsWidth() {
        return this.columnsWidth;
    }

    /**
     * 列の横幅を設定する。
     *
     * @param columnsWidth 列の横幅
     */
    public void setColumnsWidth(Map<String, String> columnsWidth) {
        this.columnsWidth = columnsWidth;
    }
    
    /**
     * 列の横幅を取得する。
     *
     * @return 列の横幅
     */
    public Map<String, String> getSummaryColumnsWidth() {
        return this.summaryColumnsWidth;
    }

    /**
     * 列の横幅を設定する。
     *
     * @param summaryColumnsWidth 列の横幅
     */
    public void setSummaryColumnsWidth(Map<String, String> summaryColumnsWidth) {
        this.summaryColumnsWidth = summaryColumnsWidth;
    }
    
    /**
     * 列の表示/非表示を取得する。
     *
     * @return 列の表示/非表示
     */
    public String getColumnsVisible() {
        return this.columnsVisible;
    }

    /**
     * 列の表示/非表示を設定する。
     *
     * @param columnsVisible 列の表示/非表示
     */
    public void setColumnsVisible(String columnsVisible) {
        this.columnsVisible = columnsVisible;
    }
    
    /**
     * 列の表示/非表示を取得する。
     *
     * @return 列の表示/非表示
     */
    public String getSummaryColumnsVisible() {
        return this.summaryColumnsVisible;
    }

    /**
     * 列の表示/非表示を設定する。
     *
     * @param summaryColumnsVisible 列の表示/非表示
     */
    public void setSummaryColumnsVisible(String summaryColumnsVisible) {
        this.summaryColumnsVisible = summaryColumnsVisible;
    }

    @Override
    public String toString() {
        return new StringBuilder("ReportOutputCondition{")
                .append("additionalColumns=").append(this.additionalColumns)
                .append(", columnOrder=").append(this.columnsOrder)
                .append(", summaryColumnOrder=").append(this.summaryColumnsOrder)
                .append(", columnWidth=").append(this.columnsWidth)
                .append(", summaryColumnWidth=").append(this.summaryColumnsWidth)
                .append(", columnVisible=").append(this.columnsVisible)
                .append(", summaryColumnVisible=").append(this.summaryColumnsVisible)
                .append("}")
                .toString();
    }
}
