/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.entity;

import java.util.List;
import jp.adtekfuji.adFactory.entity.job.OrderInfoEntity;

/**
 * カンバン基本情報
 *
 * @author nar-nakamura
 */
public class KanbanBaseInfoEntity {

    private String workflowName;
    private Integer lotQuantity;
    private List<KanbanBaseInfoPropertyEntity> propertyCollection;

    private OrderInfoEntity orderInfo;// 注番情報

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

    /**
     * 注番情報を取得する。
     *
     * @return 注番情報
     */
    public OrderInfoEntity getOrderInfo() {
        return this.orderInfo;
    }

    /**
     * 注番情報を設定する。
     *
     * @param orderInfo 注番情報
     */
    public void setOrderInfo(OrderInfoEntity orderInfo) {
        this.orderInfo = orderInfo;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanBaseInfoEntity")
                .append("workflowName=").append(this.workflowName)
                .append(", lotQuantity=").append(this.lotQuantity)
                .append(", propertyCollection=").append(this.propertyCollection)
                .append(", orderInfo=").append(this.orderInfo)
                .append("}")
                .toString();
    }
}
