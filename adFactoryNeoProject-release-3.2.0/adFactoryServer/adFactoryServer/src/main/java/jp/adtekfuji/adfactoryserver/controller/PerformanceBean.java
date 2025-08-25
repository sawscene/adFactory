/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Named;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginRequest;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.actual.ProductionPerformanceEntity;
import jp.adtekfuji.adfactoryserver.model.LineManager;
import jp.adtekfuji.adfactoryserver.model.PerformanceModel;
import jp.adtekfuji.adfactoryserver.service.EquipmentEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.view.DataTableColumn;
import jp.adtekfuji.adfactoryserver.view.PerformanceCell;
import jp.adtekfuji.adfactoryserver.view.PerformanceRow;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 生産実績マネージドビーン
 * 
 * @author s-heya
 */
@Named(value="performanceBean")
@SessionScoped
public class PerformanceBean implements  Serializable {
        
    private final String MONITOR_ID = "m-monitor";
    private final Logger logger = LogManager.getLogger();

    @EJB
    private EquipmentEntityFacadeREST equipmentRest;
    @EJB
    private PerformanceModel performModel;

    private Long equipmentId;
    private AndonMonitorLineProductSetting setting;

    private String title;
    private boolean selectProductionNumber = false;
    private Date toDate;
    private Date fromDate;
    private final List<DataTableColumn> dataTableColumns = new ArrayList<>();
    private final Map<String, PerformanceRow> dataSet = new HashMap<>();
    private final Map<Long, Integer> indexer = new HashMap<>(); // Key:工程ID、Value:カラムインデックス
    private PerformanceRow totalRow;
        
    /**
     * コンストラクタ
     */
    public PerformanceBean() {
        this.toDate = new Date();
        this.fromDate = new Date();
    }
    
    /**
     * 進捗画面を初期化する。
     */
    @PostConstruct
    public void init() {
        try {
            EquipmentLoginRequest request = EquipmentLoginRequest.identNameType(EquipmentTypeEnum.MONITOR, MONITOR_ID);
            EquipmentLoginResult result = this.equipmentRest.login(null, request, null);
            if (!result.getIsSuccess()) {
                return;
            }
            
            this.equipmentId = result.getEquipmentId();
            this.setting = LineManager.getInstance().getLineSetting(this.equipmentId);
            
            this.title = this.setting.getTitle();
            this.setting.getWorkCollection().forEach(o -> {
                if (this.setting.getUseDailyPlanNum()) {
                    o.setPlanNum(this.setting.getDailyPlanNum());
                }
                int index = this.dataTableColumns.size();
                this.dataTableColumns.add(new DataTableColumn(index, o.getTitle(), o));
                o.getWorkIds().forEach(id -> this.indexer.put(id, index));
            });

            this.update(null);
            
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 進捗を更新する。
     * 
     * @param event 
     */
    public void update(ActionEvent event) {
        try {
            logger.info("update start.");
           
            this.dataSet.clear();
            
            List<ProductionPerformanceEntity> dataList = this.performModel.sumPerformance(this.fromDate, this.toDate, this.selectProductionNumber);
            
            this.totalRow = new PerformanceRow(0, "全て", "全て", this.dataTableColumns.size());
            this.dataSet.put("total", totalRow);
 
            for (ProductionPerformanceEntity data : dataList) {
               
                if (!this.indexer.containsKey(data.getWorkId())) {
                    continue;
                }

                int index = this.indexer.get(data.getWorkId());
                int actualNum = data.getActualNum1() + data.getActualNum2();
                String key = this.selectProductionNumber ? data.getModelName() + data.getProductionNumber() : data.getModelName();
                
                PerformanceRow row;
                if (this.dataSet.containsKey(key)) {
                    row = this.dataSet.get(key);
                } else {
                    WorkSetting workSetting = (WorkSetting) this.dataTableColumns.get(index).getUserData();
                    row = new PerformanceRow(this.dataSet.size(), data.getModelName(), data.getProductionNumber(), this.dataTableColumns.size(), workSetting);
                    this.dataSet.put(key, row);
                }

                PerformanceCell cell = row.getCells().get(index);
                cell.setPlanNum(cell.getPlanNum() + data.getPlanNum());
                cell.setActualNum(cell.getActualNum() + actualNum);

                // 列合計
                PerformanceCell totalCell = this.totalRow.getCells().get(index);
                totalCell.setPlanNum(totalCell.getPlanNum() + data.getPlanNum());
                totalCell.setActualNum(totalCell.getActualNum() + actualNum);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("update end.");
        }
    }
   
    /**
     * ページのタイトル名を取得する。
     * 
     * @return ページのタイトル名
     */
    public String getTitle() {
        return title;
    }

    /**
     * 終了日付を取得する。
     * 
     * @return 終了日付
     */
    public Date getToDate() {
        return this.toDate;
    }

    /**
     * 終了日付を設定する。
     * 
     * @param toDate 終了日付
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * 開始日付を取得する。
     * 
     * @return 開始日付
     */
    public Date getFromDate() {
        return this.fromDate;
    }

    /**
     * 開始日付を設定する。
     * 
     * @param fromDate 開始日付
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * 製造番号が選択されているかを返す。
     * 
     * @return true: 選択、false: 未選択
     */
    public boolean isSelectProductionNumber() {
        return selectProductionNumber;
    }

    /**
     *  製造番号を選択する。
     * 
     * @param selectProductionNumber true: 選択、false: 選択解除
     */
    public void setSelectProductionNumber(boolean selectProductionNumber) {
        this.selectProductionNumber = selectProductionNumber;
    }

    /**
     * テーブル列を取得する。
     * 
     * @return 
     */
    public List<DataTableColumn> getDataTableColumns() {
        return this.dataTableColumns;
    }

    /**
     * 
     * @return 
     */
    public List<PerformanceRow> getDataList() {
        // ソート
        List<PerformanceRow> values = new ArrayList<>(this.dataSet.values());
        Collections.sort(values, (PerformanceRow o1, PerformanceRow o2) -> o1.getIndex().compareTo(o2.getIndex()));
        return values;
    }
}
