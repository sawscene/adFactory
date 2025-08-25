/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adappentity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adreporter.utils.DateUtilsEx;
import jp.adtekfuji.adreporter.utils.StringUtils;
import jp.adtekfuji.adappentity.enumerate.KanbanStatusEnum;

/**
 * カンバン情報
 *
 * @author nar-nakamura
 */
public class KanbanEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long kanbanId;
    private Long parentId;
    private String kanbanName;
    private Integer workflowRev;
    private String kanbanSubname;
    private long fkWorkflowId;
    private String workflowName;
    private String startDatetime;
    private String compDatetime;
    private Long fkUpdatePersonId;
    private String updateDatetime;
    private KanbanStatusEnum kanbanStatus;
    private Long fkInterruptReasonId;
    private Long fkDelayReasonId;
    private List<KanbanPropertyEntity> properties;
    private List<WorkKanbanEntity> workKanbans;
    private List<WorkKanbanEntity> separateWorkKanbans;
    private boolean isUpdated;
    private Integer lotQuantity;
    private List<ProductEntity> products;
    private String modelName;
    private Integer repairNum;

    private int workCount;

    public Long getKanbanId() {
        if (Objects.isNull(this.kanbanId)) {
            return 0L;
        }
        return this.kanbanId;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public String getKanbanName() {
        return this.kanbanName;
    }

    public String getKanbanSubname() {
        return this.kanbanSubname;
    }

    public long getFkWorkflowId() {
        return this.fkWorkflowId;
    }

    public Date getStartDatetime() throws Exception {
        return DateUtilsEx.toDate(this.startDatetime);
    }

    public String getWorkflowName() {
        return this.workflowName;
    }

    public Date getCompDatetime() throws Exception {
        return DateUtilsEx.toDate(this.compDatetime);
    }

    public Long getFkUpdatePersonId() {
        return this.fkUpdatePersonId;
    }

    public Date getUpdateDatetime() throws Exception {
        return DateUtilsEx.toDate(this.updateDatetime);
    }

    public KanbanStatusEnum getKanbanStatus() {
        return this.kanbanStatus;
    }

    public void setKanbanStatus(KanbanStatusEnum status) {
        this.kanbanStatus = status;
    }

    public Long getFkInterruptReasonId() {
        return this.fkInterruptReasonId;
    }

    public Long getFkDelayReasonId() {
        return this.fkDelayReasonId;
    }

    public List<KanbanPropertyEntity> getProperties() {
        return this.properties;
    }

    /**
     * プロパティを取得する
     *
     * @param name
     * @return
     */
    public String getProperty(String name) {
        String value = null;
        for (KanbanPropertyEntity property : this.properties) {
            if (StringUtils.equals(name, property.getKanbanPropName())) {
                value = property.getKanbanPropValue();
                break;
            }
        }
        return value;
    }

    public List<WorkKanbanEntity> getWorkKanbans() {
        return this.workKanbans;
    }

    public List<WorkKanbanEntity> getSeparateWorkKanbans() {
        return this.separateWorkKanbans;
    }

    /**
     * 工程順の版数を取得する。
     *
     * @return
     */
    public Integer getWorkflowRev() {
        return workflowRev;
    }

    /**
     * ロット数量を取得する。
     *
     * @return
     */
    public Integer getLotQuantity() {
        if (Objects.isNull(this.lotQuantity)) {
            this.lotQuantity = 0;
        }
        return this.lotQuantity;
    }

    /**
     * 製品情報を取得する。
     *
     * @return
     */
    public List<ProductEntity> getProducts() {
        return this.products;
    }

    /**
     * 製品情報を設定する。
     *
     * @param products
     */
    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }

    /**
     * 製品情報が含まれているかを返す。
     *
     * @param uid
     * @return
     */
    public boolean containsProduct(String uid) {
        if (Objects.isNull(this.products)) {
            return false;
        }

        boolean contains = false;
        for (ProductEntity product : this.products) {
            if (StringUtils.equals(uid, product.getUniqueId())) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * 製品情報を取得する。
     *
     * @param uid
     * @return
     */
    public ProductEntity getProduct(String uid) {
        if (Objects.isNull(this.products)) {
            return null;
        }

        ProductEntity result = null;
        for (ProductEntity product : this.products) {
            if (StringUtils.equals(uid, product.getUniqueId())) {
                result = product;
                break;
            }
        }
        return result;
    }

    public void setWorkCount(int workCount) {
        this.workCount = workCount;
    }

    public int getWorkCount() {
        return this.workCount;
    }

    /**
     * モデル名を取得する。
     *
     * @return
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * 補修数を取得する。
     *
     * @return
     */
    public int getRepairNum() {
        if (Objects.isNull(this.repairNum)) {
            return 0;
        }
        return this.repairNum;
    }

    public boolean IsEmpty() {
        return Objects.isNull(this.kanbanId);
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    @Override
    public String toString() {
        return "KanbanEntity { kanbanId=" + this.getKanbanName() + ", parentId=" + this.getParentId() + ", kanbanName=" + this.getKanbanName()
                + ", kanbanSubname=" + this.getKanbanSubname() + ", fkWorkflowId=" + this.getFkWorkflowId() + ", workflowName=" + this.getWorkflowName()
                + ", startDatetime=" + this.startDatetime + ", compDatetime=" + this.compDatetime + ", fkUpdatePersonId=" + this.getFkUpdatePersonId()
                + ", updateDatetime=" + this.updateDatetime + ", kanbanStatus=" + this.getKanbanStatus() + ", fkInterruptReasonId=" + this.getFkInterruptReasonId()
                + ", fkDelayReasonId=" + this.getFkDelayReasonId() + " }";
    }
}
