/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * カンバンQR情報
 * 
 * @author s-heya
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class KanbanCode implements Serializable {
    
    @JsonProperty("kanbanName")
    private String kanbanName;
    @JsonProperty("modelName")
    private String modelName;
    @JsonProperty("orderNo")
    private String orderNo;
    @JsonProperty("listNo")
    private String listNo;          // リスト番号
    @JsonProperty("workName")
    private String workName;
    @JsonProperty("productNo")
    private String productNo;
    @JsonProperty("lotQuantity")
    private Integer lotQuantity;
    @JsonProperty("dueDate")
    private String dueDate;
    @JsonProperty("workSetup")
    private Boolean workSetup;
    @JsonProperty("resourceType")
    private String resourceType;    // 資源区分
    @JsonProperty("resourceNo")
    private String resourceNo;      // 資源コード
    @JsonProperty("workNo")
    private String workNo;          // 工程コード
    @JsonProperty("hierarchyName")
    private String hierarchyName;
    @JsonProperty("workflowName")
    private String workflowName;
    
    /**
     * コンストラクタ
     */
    public KanbanCode() {
    }

    /**
     * コンストラクタ
     * 
     * @param map 
     */
    public KanbanCode(Map<String, Object> map) {
        this.kanbanName = (String) map.get("kanbanName");
        this.modelName = (String) map.get("modelName");
        this.orderNo = (String) map.get("orderNo");
        this.listNo = (String) map.get("listNo");
        this.workName = (String) map.get("workName");
        this.productNo = (String) map.get("productNo");
        this.lotQuantity = (Integer) map.getOrDefault("lotQuantity", 1);
        this.dueDate = (String) map.get("dueDate");
        this.workSetup = (Boolean) map.getOrDefault("workSetup", false);
        this.resourceType = (String) map.get("resourceType");
        this.resourceNo = (String) map.get("resourceNo");
        this.workNo = (String) map.get("workNo");
        this.hierarchyName = (String) map.get("hierarchyName");
        this.workflowName = (String) map.get("workflowName");
    }

    public String getKanbanName() {
        return kanbanName;
    }

    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getListNo() {
        return listNo;
    }

    public void setListNo(String listNo) {
        this.listNo = listNo;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public Integer getLotQuantity() {
        return lotQuantity;
    }

    public void setLotQuantity(Integer lotQuantity) {
        this.lotQuantity = lotQuantity;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getWorkSetup() {
        return workSetup;
    }

    public void setWorkSetup(Boolean workSetup) {
        this.workSetup = workSetup;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(String resourceNo) {
        this.resourceNo = resourceNo;
    }

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    /**
     * JSON文字列からカンバンQR情報を取得する。
     * 
     * @param json JSON文字列
     * @return カンバンQR情報
     */
    public static KanbanCode lookup(String json) {
        KanbanCode kanbanCode = null;
        List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(json, ServiceInfoEntity[].class);
        for (ServiceInfoEntity serviceInfo : serviceInfos) {
            if (Objects.equals(serviceInfo.getService(), ServiceInfoEntity.SERVICE_INFO_KANBANQR) && Objects.nonNull(serviceInfo.getJob())) {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) serviceInfo.getJob();
                kanbanCode = new KanbanCode(map);
                break;
            }
        }
        return kanbanCode;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 
     */
    @Override
    public String toString() {
        return new StringBuilder("KanbanCode{")
                .append("kanbanName=").append(this.kanbanName)
                .append(", modelName=").append(this.modelName)
                .append(", orderNo=").append(this.orderNo)
                .append(", listNo=").append(this.listNo)
                .append(", workName=").append(this.workName)
                .append(", productNo=").append(this.productNo)
                .append(", lotQuantity=").append(this.lotQuantity)
                .append(", dueDate=").append(this.dueDate)
                .append(", workSetup=").append(this.workSetup)
                .append(", resourceType=").append(this.resourceType)
                .append(", resourceNo=").append(this.resourceNo)
                .append(", workNo=").append(this.workNo)
                .append(", hierarchyName=").append(this.lotQuantity)
                .append(", workflowName=").append(this.workflowName)
                .append("}")
                .toString();
    }

    
}
