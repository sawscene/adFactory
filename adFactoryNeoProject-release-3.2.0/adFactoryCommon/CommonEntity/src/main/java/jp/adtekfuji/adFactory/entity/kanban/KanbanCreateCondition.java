/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバン作成条件
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "kanbanCreateCondition")
public class KanbanCreateCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private String kanbanName;// カンバン名
    @XmlElement()
    private Long workflowId;// 工程順ID
    @XmlElement()
    private Long parentId;// カンバン階層ID
    @XmlElement()
    private String creator;// 作成者(組織識別名)
    @XmlElement()
    private Boolean isOnePieceFlow;// 通常生産かどうか(true:通常生産, false:グループ生産)
    @XmlElement()
    private Integer lotQuantity;// ロット数
    @XmlElement()
    private Date startTime;// 開始予定時間
    @XmlElementWrapper(name = "workGroups")
    @XmlElement(name = "workGroup")
    private List<WorkGroup> workGroups;// 工程グループ一覧
    @XmlElement()
    private Integer productionType;// 生産タイプ(0:一個流し生産, 1:ロット一個流し生産, 2:ロット流し生産)

    /**
     * コンストラクタ
     */
    public KanbanCreateCondition() {
    }

    /**
     * コンストラクタ
     *
     * @param kanbanName カンバン名
     * @param workflowId 工程順ID
     * @param parentId カンバン階層ID
     * @param creator 作成者(組織識別名)
     * @param isOnePieceFlow 通常生産かどうか(true:通常生産, false:グループ生産)
     * @param lotQuantity ロット数
     * @param startTime 開始予定時間
     * @param workGroups 工程グループ一覧
     * @param productionType 生産タイプ(0:一個流し生産, 1:ロット一個流し生産, 2:ロット流し生産)
     */
    public KanbanCreateCondition(String kanbanName, Long workflowId, Long parentId, String creator, Boolean isOnePieceFlow, Integer lotQuantity, Date startTime, List<WorkGroup> workGroups, Integer productionType) {
        this.kanbanName = kanbanName;
        this.workflowId = workflowId;
        this.parentId = parentId;
        this.creator = creator;
        this.isOnePieceFlow = isOnePieceFlow;
        this.lotQuantity = lotQuantity;
        this.startTime = startTime;
        this.workGroups = workGroups;
        this.productionType = productionType;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getWorkflowId() {
        return this.workflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * カンバン階層IDを取得する。
     *
     * @return カンバン階層ID
     */
    public Long getParentId() {
        return this.parentId;
    }

    /**
     * カンバン階層IDを設定する。
     *
     * @param parentId カンバン階層ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 作成者(組織識別名)を取得する。
     *
     * @return 作成者(組織識別名)
     */
    public String getCreator() {
        return this.creator;
    }

    /**
     * 作成者(組織識別名)を設定する。
     *
     * @param creator 作成者(組織識別名)
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * 通常生産かどうかを取得する。
     *
     * @return 通常生産かどうか(true:通常生産, false:グループ生産)
     */
    public Boolean isOnePieceFlow() {
        return this.isOnePieceFlow;
    }

    /**
     * 通常生産かどうかを設定する。
     *
     * @param isOnePieceFlow 通常生産かどうか(true:通常生産, false:グループ生産)
     */
    public void setOnePieceFlow(Boolean isOnePieceFlow) {
        this.isOnePieceFlow = isOnePieceFlow;
    }

    /**
     * ロット数を取得する。
     *
     * @return ロット数
     */
    public Integer getLotQuantity() {
        return this.lotQuantity;
    }

    /**
     * ロット数を設定する。
     *
     * @param lotQuantity ロット数
     */
    public void setLotQuantity(Integer lotQuantity) {
        this.lotQuantity = lotQuantity;
    }

    /**
     * 開始予定時間を取得する。
     *
     * @return 開始予定時間
     */
    public Date getStartTime() {
        return this.startTime;
    }

    /**
     * 開始予定時間を設定する。
     *
     * @param startTime 開始予定時間
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 工程グループ一覧を取得する。
     *
     * @return 工程グループ一覧
     */
    public List<WorkGroup> getWorkGroups() {
        return this.workGroups;
    }

    /**
     * 工程グループ一覧を設定する。
     *
     * @param workGroups 工程グループ一覧
     */
    public void setWorkGroups(List<WorkGroup> workGroups) {
        this.workGroups = workGroups;
    }

    /**
     * 生産タイプを取得する。
     *
     * @return 生産タイプ (0:一個流し生産, 1:ロット一個流し生産, 2:ロット流し生産)
     */
    public Integer getProductionType() {
        return this.productionType;
    }

    /**
     * 生産タイプを設定する。
     *
     * @param productionType 生産タイプ (0:一個流し生産, 1:ロット一個流し生産, 2:ロット流し生産)
     */
    public void setProductionType(Integer productionType) {
        this.productionType = productionType;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanCreateCondition{")
                .append("kanbanName=").append(this.kanbanName)
                .append(", workflowId=").append(this.workflowId)
                .append(", parentId=").append(this.parentId)
                .append(", creator=").append(this.creator)
                .append(", isOnePieceFlow=").append(this.isOnePieceFlow)
                .append(", lotQuantity=").append(this.lotQuantity)
                .append(", startTime=").append(this.startTime)
                .append(", workGroups=").append(this.workGroups)
                .append(", productionType=").append(this.productionType)
                .append("}")
                .toString();
    }
}
