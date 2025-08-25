/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.entity;

import java.time.LocalDate;
import jp.adtekfuji.adFactory.entity.view.WorkReportInfoEntity;
import org.apache.commons.lang3.StringUtils;

/**
 * 作業日報出力データ
 *
 * @author s-heya
 */
public class WorkReportCsv {

    private WorkReportInfoEntity workReport;
    private LocalDate workDate;
    private String workTime;
    private int workNum;
    private String unitTime;
    private String serialNumber;
    
    /**
     * コンストラクタ
     */
    public WorkReportCsv() {
        this.workReport = new WorkReportInfoEntity();
    }

    /**
     * コンストラクタ
     *
     * @param row 作業日報の行情報
     * @param serialNumber シリアル番号
     */
    public WorkReportCsv(WorkReportRowInfo row, String serialNumber) {
        this.workReport = new WorkReportInfoEntity();
        this.workReport.setWorkType(row.getWorkReport().getWorkType());
        this.workReport.setOrganizationName(row.getWorkReport().getOrganizationName());
        this.workReport.setOrganizationIdentify(row.getWorkReport().getOrganizationIdentify());
        this.workReport.setOrganizationId(row.getWorkReport().getOrganizationId());
        this.workReport.setWorkNumber(row.getWorkReport().getWorkNumber());
        this.workReport.setKanbanName(row.getWorkReport().getKanbanName());
        this.workReport.setProductionNumber(row.getWorkReport().getProductionNumber());
        this.workReport.setOrderNumber(row.getWorkReport().getOrderNumber());
        this.workReport.setWorkName(row.getWorkReport().getWorkName());
        this.workReport.setModelName(row.getWorkReport().getModelName());
        this.workReport.setActualNum(1);

        this.workDate = row.getWorkDate();
        this.workTime = row.getWorkTime();
        
        if (StringUtils.isEmpty(serialNumber)) {
            this.workNum = row.getWorkReport().getActualNum();
        } else {
            this.workNum = 1;
            this.unitTime = row.getUnitTime();
            this.serialNumber = serialNumber;
        }
    }

    /**
     * 作業日報情報を取得する。
     *
     * @return 作業日報情報
     */
    public WorkReportInfoEntity getWorkReport() {
        return this.workReport;
    }

    /**
     * 工数文字列(h:mm:ss)を取得する。
     *
     * @return 工数文字列(h:mm:ss)
     */
    public String getWorkTime() {
        return workTime;
    }

    /**
     * 作業日を取得する。
     *
     * @return 作業日
     */
    public LocalDate getWorkDate() {
        return workDate;
    }

    /**
     * 作業数を取得する。
     *
     * @return 作業数
     */
    public int getWorkNum() {
        return this.workNum;
    }
    
    /**
     * 工数/台を取得する。
     * 
     * @return 工数/台
     */
    public String getUnitTime() {
        return this.unitTime;
    }

    /**
     * シリアル番号を取得する。
     * 
     * @return シリアル番号
     */
    public String getSerialNumber() {
        return serialNumber;
    }
}

