/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin.entity;

import java.util.List;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanPropertyCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanStatusCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportProductCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportWorkKanbanCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportWorkKanbanPropertyCsv;

/**
 *
 * @author fu-kato
 */
public class WorkPlanInfo {

    List<ImportKanbanCsv> importKanbans;
    List<ImportKanbanPropertyCsv> importKanbanProps;
    List<ImportWorkKanbanCsv> importWorkKanbans;
    List<ImportWorkKanbanPropertyCsv> importWkKanbanProps;
    List<ImportKanbanStatusCsv> importKanbanStatuss;
    List<ImportProductCsv> importProduct;

    /**
     * 
     * @param importKanbans
     * @param importKanbanProps
     * @param importWorkKanbans
     * @param importWkKanbanProps
     * @param importKanbanStatuss
     * @param importProduct 
     */
    public WorkPlanInfo(List<ImportKanbanCsv> importKanbans, List<ImportKanbanPropertyCsv> importKanbanProps, List<ImportWorkKanbanCsv> importWorkKanbans, List<ImportWorkKanbanPropertyCsv> importWkKanbanProps, List<ImportKanbanStatusCsv> importKanbanStatuss, List<ImportProductCsv> importProduct) {
        this.importKanbans = importKanbans;
        this.importKanbanProps = importKanbanProps;
        this.importWorkKanbans = importWorkKanbans;
        this.importWkKanbanProps = importWkKanbanProps;
        this.importKanbanStatuss = importKanbanStatuss;
        this.importProduct = importProduct;
    }

    /**
     * 
     * @return 
     */
    public List<ImportKanbanCsv> getImportKanbans() {
        return this.importKanbans;
    }

    /**
     * 
     * @return 
     */
    public List<ImportKanbanPropertyCsv> getImportKanbanProps() {
        return this.importKanbanProps;
    }

    /**
     * 
     * @return 
     */
    public List<ImportWorkKanbanCsv> getImportWorkKanbans() {
        return this.importWorkKanbans;
    }

    /**
     * 
     * @return 
     */
    public List<ImportWorkKanbanPropertyCsv> getImportWkKanbanProps() {
        return this.importWkKanbanProps;
    }

    /**
     * 
     * @return 
     */
    public List<ImportKanbanStatusCsv> getImportKanbanStatuss() {
        return this.importKanbanStatuss;
    }

    /**
     * 
     * @return 
     */
    public List<ImportProductCsv> getImportProduct() {
        return this.importProduct;
    }
}
