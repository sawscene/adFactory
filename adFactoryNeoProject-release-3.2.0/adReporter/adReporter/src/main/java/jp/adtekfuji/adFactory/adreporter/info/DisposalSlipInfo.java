/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adreporter.info;

import java.io.Serializable;
import jp.adtekfuji.adappentity.ActualResultEntity;
import jp.adtekfuji.adappentity.KanbanEntity;
import java.util.List;
import jp.adtekfuji.adappentity.ProductEntity;

/**
 * 廃棄伝票情報
 *
 * @author nar-nakamura
 */
public class DisposalSlipInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String equipmentIdentName;
    private String issuerName;
    private KanbanEntity kanban;
    private List<ActualResultEntity> actualResults;
    private List<ProductEntity> products;
    
    /**
     * コンストラクタ
     */
    public DisposalSlipInfo() {
    }

    /**
     * 設備識別名を取得する。
     *
     * @return 設備識別名
     */
    public String getEquipmentIdentName() {
        return this.equipmentIdentName;
    }

    /**
     * 設備識別名を設定する。
     *
     * @param equipmentIdentName 設備識別名
     */
    public void setEquipmentIdentName(String equipmentIdentName) {
        this.equipmentIdentName = equipmentIdentName;
    }

    /**
     * 発行者名を取得する。
     *
     * @return 発行者名
     */
    public String getIssuerName() {
        return this.issuerName;
    }

    /**
     * 発行者名を設定する。
     *
     * @param issuerName 発行者名
     */
    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    /**
     * カンバン情報を取得する。
     *
     * @return カンバン情報
     */
    public KanbanEntity getKanban() {
        return this.kanban;
    }

    /**
     * カンバン情報を設定する。
     *
     * @param kanban カンバン情報
     */
    public void setKanban(KanbanEntity kanban) {
        this.kanban = kanban;
    }

    /**
     * 工程実績一覧を取得する。
     *
     * @return 工程実績一覧
     */
    public List<ActualResultEntity> getActualResults() {
        return this.actualResults;
    }

    /**
     * 工程実績一覧を設定する。
     *
     * @param actualResults 工程実績一覧
     */
    public void setActualResults(List<ActualResultEntity> actualResults) {
        this.actualResults = actualResults;
    }

    /**
     * 廃棄情報を取得する。
     * 
     * @return 
     */
    public List<ProductEntity> getProducts() {
        return products;
    }

    /**
     * 廃棄情報を設定する。
     * 
     * @param products 
     */
    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "DisposalSlipInfo{" + "equipmentIdentName=" + equipmentIdentName + ", issuerName=" + issuerName + 
                ", kanban=" + kanban + ", actualResults=" + actualResults + ", products=" + products + '}';
    }

}
