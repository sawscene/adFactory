/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.entity;

import java.util.List;

/**
 * カンバン基本情報
 *
 * @author nar-nakamura
 */
public class KanbanBaseInfoEntity {

    private String workflowName;
    private Integer lotQuantity;
    private List<KanbanBaseInfoPropertyEntity> propertyCollection;

    /**
     * カンバン基本情報
     */
    public KanbanBaseInfoEntity() {

    }

    /**
     * 工程順名を取得する。
     *
     * @return
     */
    public String getWorkflowName() {
        return this.workflowName;
    }

    /**
     * 工程順名を設定する。
     *
     * @param value
     */
    public void setWorkflowName(String value) {
        this.workflowName = value;
    }

    /**
     * ロット数量を取得する。
     *
     * @return
     */
    public Integer getLotQuantity() {
        return this.lotQuantity;
    }

    /**
     * ロット数量を設定する。
     *
     * @param value
     */
    public void setLotQuantity(Integer value) {
        this.lotQuantity = value;
    }

    /**
     * カンバンプロパティを取得する。
     *
     * @return 
     */
    public List<KanbanBaseInfoPropertyEntity> getPropertyCollection() {
        return propertyCollection;
    }

    /**
     * カンバンプロパティを設定する。
     *
     * @param value 
     */
    public void setPropertyCollection(List<KanbanBaseInfoPropertyEntity> value) {
        this.propertyCollection = value;
    }

    @Override
    public String toString() {
        return "KanbanBaseInfoEntity{" +
                "workflowName=" + this.workflowName +
                ", lotQuantity=" + this.lotQuantity +
                ", propertyCollection=" + this.propertyCollection +
                "}";
    }
}
