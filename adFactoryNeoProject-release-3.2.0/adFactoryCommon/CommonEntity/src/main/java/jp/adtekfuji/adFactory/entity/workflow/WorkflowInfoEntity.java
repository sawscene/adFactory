/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.workflow;

import java.io.Serializable;
import java.util.*;

import adtekfuji.utility.StringUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.entity.approval.ApprovalInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adFactory.utility.PropertyUtils;

/**
 * 工程順情報
 *
 * @author ta.ito
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workflow")
public class WorkflowInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty workflowIdProperty;
    private LongProperty parentIdProperty;
    private StringProperty workflowNameProperty;
    private IntegerProperty workflowRevProperty;
    private StringProperty workflowRevisionProperty;
    private StringProperty workflowDiaglamProperty;
    private LongProperty fkUpdatePersonIdProperty;
    private ObjectProperty<Date> updateDatetimeProperty;
    private StringProperty ledgerPathProperty;
    private StringProperty workflowNumberProperty;
    private StringProperty modelNameProperty;

    @XmlElement(required = true)
    private Long workflowId;// 工程順ID
    @XmlElement()
    private Long parentId;// 親階層ID
    @XmlElement()
    private String workflowName;// 工程順名
    @XmlElement()
    private Integer workflowRev;// 版数
    @XmlElement()
    private String workflowRevision;// 版名
    @XmlElement()
    private String workflowDiaglam;// ワークフロー図
    @XmlElement()
    private Long fkUpdatePersonId;// 更新者(組織ID)
    @XmlElement()
    private Date updateDatetime;// 更新日時
    @XmlElement()
    private String ledgerPath;// 帳票テンプレートパス
    @XmlElement()
    private String workflowNumber;// 作業番号

    @XmlTransient
    private String parentName; // 親階層名

    @XmlTransient
    private List<KanbanPropertyTemplateInfoEntity> kanbanPropertyTemplateInfoCollection = null;// カンバンプロパティテンプレート一覧

    @XmlElementWrapper(name = "conWorkflowWorks")
    @XmlElement(name = "conWorkflowWork")
    private List<ConWorkflowWorkInfoEntity> conWorkflowWorkInfoCollection = null;// 工程の関連付け情報一覧

    @XmlElementWrapper(name = "conWorkflowSeparateworks")
    @XmlElement(name = "conWorkflowSeparatework")
    private List<ConWorkflowSeparateworkInfoEntity> conWorkflowSeparateworkInfoCollection = null;// 追加工程の関連付け情報一覧

    @XmlElement()
    private Integer latestRev;// 最新版数
    @XmlElement()
    private String modelName;// モデル名
    @XmlElement()
    private Date openTime;// 始業時間
    @XmlElement()
    private Date closeTime;// 就業時間
    @XmlElement()
    private SchedulePolicyEnum schedulePolicy = SchedulePolicyEnum.PriorityParallel;// 作業順序

    @XmlElement()
    private String workflowAddInfo;// 追加情報(JSON)
    @XmlElement()
    private String serviceInfo;// サービス情報(JSON)

    @XmlElement()
    private Long approvalId; // 申請ID
    @XmlElement()
    private ApprovalStatusEnum approvalState; // 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    @XmlElement()
    private ApprovalInfoEntity approval; // 申請情報

    /**
     * コンストラクタ
     */
    public WorkflowInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workflowId 工程順ID
     * @param parentId 親階層ID
     * @param workflowName 工程順名
     * @param workflowRevision 版名
     * @param workflowDiaglam ワークフロー図
     * @param fkUpdatePersonId 更新者(組織ID)
     * @param updateDatetime 更新日時
     * @param ledgerPath 帳票テンプレートパス
     */
    public WorkflowInfoEntity(Long workflowId, Long parentId, String workflowName, String workflowRevision, String workflowDiaglam, Long fkUpdatePersonId, Date updateDatetime, String ledgerPath) {
        this.workflowId = workflowId;
        this.parentId = parentId;
        this.workflowName = workflowName;
        this.workflowRev = 1;
        this.workflowRevision = workflowRevision;
        this.workflowDiaglam = workflowDiaglam;
        this.fkUpdatePersonId = fkUpdatePersonId;
        this.updateDatetime = updateDatetime;
        this.ledgerPath = ledgerPath;
    }

    /**
     * 工程順IDプロパティを取得する。
     *
     * @return 工程順IDプロパティ
     */
    public LongProperty workflowIdProperty() {
        if (Objects.isNull(this.workflowIdProperty)) {
            this.workflowIdProperty = new SimpleLongProperty(this.workflowId);
        }
        return this.workflowIdProperty;
    }

    /**
     * 工程順階層IDプロパティを取得する。
     *
     * @return 工程順階層IDプロパティ
     */
    public LongProperty parentIdProperty() {
        if (Objects.isNull(this.parentIdProperty)) {
            this.parentIdProperty = new SimpleLongProperty(this.parentId);
        }
        return this.parentIdProperty;
    }

    /**
     * 工程順名プロパティを取得する。
     *
     * @return 工程順名プロパティ
     */
    public StringProperty workflowNameProperty() {
        if (Objects.isNull(this.workflowNameProperty)) {
            this.workflowNameProperty = new SimpleStringProperty(this.workflowName);
        }
        return this.workflowNameProperty;
    }

    /**
     * 版数プロパティを取得する。
     *
     * @return 版数プロパティ
     */
    public IntegerProperty workflowRevProperty() {
        if (Objects.isNull(this.workflowRevProperty)) {
            this.workflowRevProperty = new SimpleIntegerProperty(this.workflowRev);
        }
        return this.workflowRevProperty;
    }

    // TODO: 削除予定
    public StringProperty workflowRevisionProperty() {
        if (Objects.isNull(this.workflowRevisionProperty)) {
            this.workflowRevisionProperty = new SimpleStringProperty(this.workflowRevision);
        }
        return this.workflowRevisionProperty;
    }

    /**
     * 工程順ダイアグラムプロパティを取得する。
     *
     * @return 工程順ダイアグラムプロパティ
     */
    public StringProperty workflowDiaglamProperty() {
        if (Objects.isNull(workflowDiaglamProperty)) {
            this.workflowDiaglamProperty = new SimpleStringProperty(this.workflowDiaglam);
        }
        return this.workflowDiaglamProperty;
    }

    /**
     * 更新者の組織IDプロパティを取得する。
     *
     * @return 更新者の組織IDプロパティ
     */
    public LongProperty fkUpdatePersonIdProperty() {
        if (Objects.isNull(this.fkUpdatePersonIdProperty)) {
            this.fkUpdatePersonIdProperty = new SimpleLongProperty(this.fkUpdatePersonId);
        }
        return this.fkUpdatePersonIdProperty;
    }

    /**
     * 更新日時プロパティを取得する。
     *
     * @return 更新日時プロパティ
     */
    public ObjectProperty<Date> updateDatetimeProperty() {
        if (Objects.isNull(this.updateDatetimeProperty)) {
            this.updateDatetimeProperty = new SimpleObjectProperty(this.updateDatetime);
        }
        return this.updateDatetimeProperty;
    }

    /**
     * 帳票テンプレートパスプロパティを取得する。
     *
     * @return 帳票テンプレートパスプロパティ
     */
    public StringProperty ledgerPathProperty() {
        if (Objects.isNull(this.ledgerPathProperty)) {
            this.ledgerPathProperty = new SimpleStringProperty(this.ledgerPath);
        }
        return this.ledgerPathProperty;
    }

    /**
     * モデル名プロパティを取得する。
     *
     * @return モデル名プロパティ
     */
    public StringProperty modelNameProperty() {
        if (Objects.isNull(this.modelNameProperty)) {
            this.modelNameProperty = new SimpleStringProperty(this.modelName);
        }
        return this.modelNameProperty;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getWorkflowId() {
        if (Objects.nonNull(this.workflowIdProperty)) {
            return this.workflowIdProperty.get();
        }
        return this.workflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(Long workflowId) {
        if (Objects.nonNull(this.workflowIdProperty)) {
            this.workflowIdProperty.set(workflowId);
        } else {
            this.workflowId = workflowId;
        }
    }

    /**
     * 工程順階層IDを取得する。
     *
     * @return 工程順階層ID
     */
    public Long getParentId() {
        if (Objects.nonNull(this.parentIdProperty)) {
            return this.parentIdProperty.get();
        }
        return this.parentId;
    }

    /**
     * 工程順階層IDを設定する。
     *
     * @param parentId 工程順階層ID
     */
    public void setParentId(Long parentId) {
        if (Objects.nonNull(this.parentIdProperty)) {
            this.parentIdProperty.set(parentId);
        } else {
            this.parentId = parentId;
        }
    }

    /**
     * 親階層名 取得
     * @return 親階層名
     */
    public String getParentName() {
        return parentName;
    }

    /**
     * 親階層名 設定
     * @param parentName 親階層名
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
        if (Objects.nonNull(this.workflowNameProperty)) {
            return this.workflowNameProperty.get();
        }
        return this.workflowName;
    }

    /**
     * 工程順名を設定する。
     *
     * @param workflowName 工程順名
     */
    public void setWorkflowName(String workflowName) {
        if (Objects.nonNull(this.workflowNameProperty)) {
            this.workflowNameProperty.set(workflowName);
        } else {
            this.workflowName = workflowName;
        }
    }

    /**
     * 版数を取得する。
     *
     * @return 版数
     */
    public Integer getWorkflowRev() {
        if (Objects.nonNull(this.workflowRevProperty)) {
            return this.workflowRevProperty.get();
        }
        return this.workflowRev;
    }

    /**
     * 版数を設定する。
     *
     * @param workflowRev 版数
     */
    public void setWorkflowRev(Integer workflowRev) {
        if (Objects.nonNull(this.workflowRevProperty)) {
            this.workflowRevProperty.set(workflowRev);
        } else {
            this.workflowRev = workflowRev;
        }
    }

    // TODO: 削除予定
    /**
     * 版名を取得する。※.削除予定
     *
     * @return 版名
     */
    public String getWorkflowRevision() {
        if (Objects.nonNull(this.workflowRevisionProperty)) {
            return this.workflowRevisionProperty.get();
        }
        return this.workflowRevision;
    }

    // TODO: 削除予定
    /**
     * 版名を設定する。※.削除予定
     *
     * @param workflowRevision 版名
     */
    public void setWorkflowRevision(String workflowRevision) {
        if (Objects.nonNull(this.workflowRevisionProperty)) {
            this.workflowRevisionProperty.set(workflowRevision);
        } else {
            this.workflowRevision = workflowRevision;
        }
    }

    /**
     * 工程順ダイアグラムを取得する。
     *
     * @return 工程順ダイアグラム
     */
    public String getWorkflowDiaglam() {
        if (Objects.nonNull(this.workflowDiaglamProperty)) {
            return this.workflowDiaglamProperty.get();
        }
        return this.workflowDiaglam;
    }

    /**
     * 工程順ダイアグラムを設定する。
     *
     * @param workflowDiaglam 工程順ダイアグラム
     */
    public void setWorkflowDiaglam(String workflowDiaglam) {
        if (Objects.nonNull(this.workflowDiaglamProperty)) {
            this.workflowDiaglamProperty.set(workflowDiaglam);
        } else {
            this.workflowDiaglam = workflowDiaglam;
        }
    }

    /**
     * 更新者の組織IDを取得する。
     *
     * @return 更新者の組織ID
     */
    public Long getFkUpdatePersonId() {
        if (Objects.nonNull(this.fkUpdatePersonIdProperty)) {
            return this.fkUpdatePersonIdProperty.get();
        }
        return this.fkUpdatePersonId;
    }

    /**
     * 更新者の組織IDを設定する。
     *
     * @param fkUpdatePersonId 更新者の組織ID
     */
    public void setFkUpdatePersonId(Long fkUpdatePersonId) {
        if (Objects.nonNull(this.fkUpdatePersonIdProperty)) {
            this.fkUpdatePersonIdProperty.set(fkUpdatePersonId);
        } else {
            this.fkUpdatePersonId = fkUpdatePersonId;
        }
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDatetime() {
        if (Objects.nonNull(this.updateDatetimeProperty)) {
            return this.updateDatetimeProperty.get();
        }
        return this.updateDatetime;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDatetime 更新日時
     */
    public void setUpdateDatetime(Date updateDatetime) {
        if (Objects.nonNull(this.updateDatetimeProperty)) {
            this.updateDatetimeProperty.set(updateDatetime);
        } else {
            this.updateDatetime = updateDatetime;
        }
    }

    /**
     * 帳票テンプレートパスを取得する。
     *
     * @return 帳票テンプレートパス
     */
    public String getLedgerPath() {
        if (Objects.nonNull(this.ledgerPathProperty)) {
            return this.ledgerPathProperty.get();
        }
        return this.ledgerPath;
    }

    /**
     * 帳票テンプレートパスを設定する。
     *
     * @param ledgerPath 帳票テンプレートパス
     */
    public void setLedgerPath(String ledgerPath) {
        if (Objects.nonNull(this.ledgerPathProperty)) {
            this.ledgerPathProperty.set(ledgerPath);
        } else {
            this.ledgerPath = ledgerPath;
        }
    }

    /**
     * 帳票テンプレートパスをリストで取得する。
     *
     * @return 帳票テンプレートパスリスト
     */
    public List<SimpleStringProperty> getLedgerPathPropertyCollection() {
        return PropertyUtils.stringToPropertyList(this.getLedgerPath(), "\\|");
    }

    /**
     * 帳票テンプレートパスをリストで設定する。
     *
     * @param ledgerPathPropertyCollection 帳票テンプレートパスリスト
     */
    public void setLedgerPathPropertyCollection(List<SimpleStringProperty> ledgerPathPropertyCollection) {
        this.setLedgerPath(PropertyUtils.propertyListToString(ledgerPathPropertyCollection, "|"));
    }

    /**
     * 作業番号を取得する。
     *
     * @return 作業番号
     */
    public String getWorkflowNumber() {
        if (Objects.nonNull(this.workflowNumberProperty)) {
            return this.workflowNumberProperty.get();
        }
        return this.workflowNumber;
    }

    /**
     * 作業番号を設定する。
     *
     * @param workflowNumber 作業番号
     */
    public void setWorkflowNumber(String workflowNumber) {
        if (Objects.nonNull(this.workflowNumberProperty)) {
            this.workflowNumberProperty.set(workflowNumber);
        } else {
            this.workflowNumber = workflowNumber;
        }
    }

    /**
     * 追加情報を追加
     * @param entity
     */
    public void addKanbanPropertyTemplateInfo(KanbanPropertyTemplateInfoEntity entity) {
        if (Objects.isNull(this.kanbanPropertyTemplateInfoCollection)) {
            if (StringUtils.isEmpty(this.getWorkflowAddInfo())) {
                this.setKanbanPropertyTemplateInfoCollection(new ArrayList<>());
            } else {
                this.setKanbanPropertyTemplateInfoCollection(JsonUtils.jsonToObjects(this.getWorkflowAddInfo(), KanbanPropertyTemplateInfoEntity[].class));
            }
        }
        this.kanbanPropertyTemplateInfoCollection.add(entity);
    }

    /**
     * カンバンプロパティテンプレート情報一覧を取得する。
     *
     * @return カンバンプロパティテンプレート情報一覧
     */
    public List<KanbanPropertyTemplateInfoEntity> getKanbanPropertyTemplateInfoCollection() {
        
        // リストがnull (JSONから未変換) の場合、JSONから変換してリストにセット
        if(Objects.isNull(this.kanbanPropertyTemplateInfoCollection)){
            // 変換した結果をエンティティにセットする
            this.setKanbanPropertyTemplateInfoCollection(JsonUtils.jsonToObjects(this.getWorkflowAddInfo(), KanbanPropertyTemplateInfoEntity[].class));
        }
        return this.kanbanPropertyTemplateInfoCollection;
    }

    /**
     * カンバンプロパティテンプレート情報一覧を設定する。
     *
     * @param kanbanPropertyTemplateInfoCollection カンバンプロパティテンプレート情報一覧
     */
    public void setKanbanPropertyTemplateInfoCollection(List<KanbanPropertyTemplateInfoEntity> kanbanPropertyTemplateInfoCollection) {
        this.kanbanPropertyTemplateInfoCollection = kanbanPropertyTemplateInfoCollection;
    }

    /**
     * カンバンプロパティテンプレート情報マップを取得する。
     *
     * @return カンバンプロパティテンプレート情報
     */
    public Map<String, String> getKanbanPropertyTemplate() {
        Map<String, String> map = new HashMap<>();
        for (KanbanPropertyTemplateInfoEntity entity : this.kanbanPropertyTemplateInfoCollection) {
            map.put(entity.getKanbanPropName(), entity.getKanbanPropInitialValue());
        }
        return map;
    }

    /**
     * 工程の関連付け情報一覧を取得する。
     *
     * @return 工程の関連付け情報一覧
     */
    public List<ConWorkflowWorkInfoEntity> getConWorkflowWorkInfoCollection() {
        return this.conWorkflowWorkInfoCollection;
    }

    /**
     * 工程の関連付け情報一覧を設定する。
     *
     * @param conWorkflowWorkInfoCollection 工程の関連付け情報一覧
     */
    public void setConWorkflowWorkInfoCollection(List<ConWorkflowWorkInfoEntity> conWorkflowWorkInfoCollection) {
        this.conWorkflowWorkInfoCollection = conWorkflowWorkInfoCollection;
    }

    /**
     * 追加工程の関連付け情報一覧を取得する。
     *
     * @return 追加工程の関連付け情報一覧
     */
    public List<ConWorkflowSeparateworkInfoEntity> getConWorkflowSeparateworkInfoCollection() {
        return this.conWorkflowSeparateworkInfoCollection;
    }

    /**
     * 追加工程の関連付け情報一覧を設定する。
     *
     * @param conWorkflowSeparateworkInfoCollection 追加工程の関連付け情報一覧
     */
    public void setConWorkflowSeparateworkInfoCollection(List<ConWorkflowSeparateworkInfoEntity> conWorkflowSeparateworkInfoCollection) {
        this.conWorkflowSeparateworkInfoCollection = conWorkflowSeparateworkInfoCollection;
    }

    /**
     * 最新版数を取得する。
     *
     * @return 最新版数
     */
    public Integer getLatestRev() {
        return this.latestRev;
    }

    /**
     * 最新版数を設定する。
     *
     * @param latestRev 最新版数
     */
    public void setLatestRev(Integer latestRev) {
        this.latestRev = latestRev;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 始業時間を取得する。
     *
     * @return 始業時間
     */
    public Date getOpenTime() {
        return this.openTime;
    }

    /**
     * 始業時間を設定する。
     *
     * @param openTime 始業時間
     */
    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    /**
     * 終業時間を取得する。
     *
     * @return 終業時間
     */
    public Date getCloseTime() {
        return this.closeTime;
    }

    /**
     * 終業時間を設定する。
     *
     * @param closeTime 終業時間
     */
    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    /**
     * 作業順序を取得する。
     *
     * @return 作業順序
     */
    public SchedulePolicyEnum getSchedulePolicy() {
        return this.schedulePolicy;
    }

    /**
     * 作業順序を設定する。
     *
     * @param schedulePolicy 作業順序
     */
    public void setSchedulePolicy(SchedulePolicyEnum schedulePolicy) {
        this.schedulePolicy = schedulePolicy;
    }

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getWorkflowAddInfo() {
        return this.workflowAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param workflowAddInfo 追加情報(JSON)
     */
    public void setWorkflowAddInfo(String workflowAddInfo) {
        this.workflowAddInfo = workflowAddInfo;
    }

    /**
     * サービス情報(JSON)を取得する。
     *
     * @return サービス情報(JSON)
     */
    public String getServiceInfo() {
        return this.serviceInfo;
    }

    /**
     * サービス情報(JSON)を設定する。
     *
     * @param serviceInfo サービス情報(JSON)
     */
    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    /**
     * 申請IDを取得する。
     *
     * @return 申請ID
     */
    public Long getApprovalId() {
        return this.approvalId;
    }

    /**
     * 申請IDを設定する。
     *
     * @param approvalId 申請ID
     */
    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    /**
     * 承認状態を取得する。
     *
     * @return 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    public ApprovalStatusEnum getApprovalState() {
        return this.approvalState;
    }

    /**
     * 承認状態を設定する。
     *
     * @param approvalState 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    public void setApprovalState(ApprovalStatusEnum approvalState) {
        this.approvalState = approvalState;
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * 申請情報を取得する。
     *
     * @return 申請情報
     */
    public ApprovalInfoEntity getApproval() {
        return this.approval;
    }

    /**
     * 申請情報を設定する。
     *
     * @param approval 申請情報
     */
    public void setApproval(ApprovalInfoEntity approval) {
        this.approval = approval;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.workflowId ^ (this.workflowId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.workflowName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkflowInfoEntity other = (WorkflowInfoEntity) obj;
        if (!Objects.equals(this.getWorkflowId(), other.getWorkflowId())) {
            return false;
        }
        return Objects.equals(this.getWorkflowName(), other.getWorkflowName());
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkflowInfoEntity{")
                .append("workflowId=").append(this.workflowId)
                .append(", parentId=").append(this.parentId)
                .append(", workflowName=").append(this.workflowName)
                .append(", workflowRev=").append(this.workflowRev)
                .append(", workflowRevision=").append(this.workflowRevision)
                .append(", workflowDiaglam=").append(this.workflowDiaglam)
                .append(", fkUpdatePersonId=").append(this.fkUpdatePersonId)
                .append(", updateDatetime=").append(this.updateDatetime)
                .append(", ledgerPath=").append(this.ledgerPath)
                .append(", workflowNumber=").append(this.workflowNumber)
                .append(", latestRev=").append(this.latestRev)
                .append(", modelName=").append(this.modelName)
                .append(", openTime=").append(this.openTime)
                .append(", closeTime=").append(this.closeTime)
                .append(", schedulePolicy=").append(this.schedulePolicy)
                .append(", approvalId=").append(this.approvalId)
                .append(", approvalState=").append(this.approvalState)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }

    /**
     * 工程順情報をコピーする
     *
     * @return
     */
    @Override
    public WorkflowInfoEntity clone() {
        WorkflowInfoEntity entity = new WorkflowInfoEntity();

        entity.setWorkflowId(this.workflowId);
        entity.setParentId(this.parentId);
        entity.setWorkflowName(this.workflowName);
        entity.setWorkflowRev(this.workflowRev);
        entity.setWorkflowRevision(this.workflowRevision);
        entity.setWorkflowDiaglam(this.workflowDiaglam);
        entity.setFkUpdatePersonId(this.fkUpdatePersonId);
        entity.setUpdateDatetime((Date) this.updateDatetime.clone());
        entity.setLedgerPath(this.ledgerPath);
        entity.setWorkflowNumber(this.workflowNumber);

        entity.setConWorkflowSeparateworkInfoCollection(new LinkedList<>());
        this.conWorkflowSeparateworkInfoCollection.forEach(c -> entity.getConWorkflowSeparateworkInfoCollection().add(c.clone()));

        entity.setKanbanPropertyTemplateInfoCollection(new LinkedList<>());
        this.getKanbanPropertyTemplateInfoCollection().forEach(c -> entity.getKanbanPropertyTemplateInfoCollection().add(c.clone()));

        entity.setConWorkflowWorkInfoCollection(new LinkedList<>());
        this.conWorkflowWorkInfoCollection.forEach(c -> entity.getConWorkflowWorkInfoCollection().add(c.clone()));

        entity.setLatestRev(this.latestRev);
        entity.setModelName(this.modelName);
        entity.setOpenTime(this.openTime);
        entity.setCloseTime(this.closeTime);
        entity.setSchedulePolicy(this.schedulePolicy);

        // 追加情報
        entity.setWorkflowAddInfo(this.getWorkflowAddInfo());
        // サービス情報
        entity.setServiceInfo(this.getServiceInfo());

        entity.setVerInfo(this.getVerInfo());

        // 申請情報
        entity.setApprovalId(this.getApprovalId());
        entity.setApprovalState(this.getApprovalState());
        entity.setApproval(this.getApproval());

        return entity;
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other
     * @return
     */
    public boolean equalsDisplayInfo(WorkflowInfoEntity other) {
        boolean ret = false;
        if (Objects.equals(this.getWorkflowName(), other.getWorkflowName())
                && Objects.equals(this.getWorkflowRev(), other.getWorkflowRev())
                && Objects.equals(this.getLedgerPath(), other.getLedgerPath())
                && Objects.equals(this.getWorkflowNumber(), other.getWorkflowNumber())
                && Objects.equals(this.getWorkflowDiaglam(), other.getWorkflowDiaglam())
                && this.separateWorksEquals(this.getConWorkflowSeparateworkInfoCollection(), other.getConWorkflowSeparateworkInfoCollection())
                && this.propertiesEquals(this.getKanbanPropertyTemplateInfoCollection(), other.getKanbanPropertyTemplateInfoCollection())
                && this.workflowsEquals(this.getConWorkflowWorkInfoCollection(), other.getConWorkflowWorkInfoCollection())
                && Objects.equals(this.getModelName(), other.getModelName())
                && Objects.equals(this.getOpenTime(), other.getOpenTime())
                && Objects.equals(this.getCloseTime(), other.getCloseTime())
                && Objects.equals(this.getSchedulePolicy(), other.getSchedulePolicy())
                // 追加情報
                // サービス情報
                ) {
            ret = true;
        }
        return ret;
    }

    /**
     * 追加工程が同じ情報を持つか比較する
     *
     * @param a
     * @param b
     * @return 同一の時true
     */
    private boolean separateWorksEquals(List a, List b) {
        if (Objects.isNull(a) || Objects.isNull(b)) {
            return false;
        }

        if (a.size() != b.size()) {
            return false;
        }

        Iterator<ConWorkflowSeparateworkInfoEntity> ita = a.iterator();
        Iterator<ConWorkflowSeparateworkInfoEntity> itb = b.iterator();

        while (ita.hasNext()) {
            ConWorkflowSeparateworkInfoEntity ena = ita.next();
            ConWorkflowSeparateworkInfoEntity enb = itb.next();
            if (!ena.equalsDisplayInfo(enb)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 追加情報が同一の内容であるか調べる
     *
     * @param a
     * @param b
     * @return 同一の時true
     */
    private boolean propertiesEquals(List a, List b) {
        if (Objects.isNull(a) || Objects.isNull(b)) {
            return false;
        }

        if (a.size() != b.size()) {
            return false;
        }

        Iterator<KanbanPropertyTemplateInfoEntity> ita = a.iterator();
        Iterator<KanbanPropertyTemplateInfoEntity> itb = b.iterator();

        while (ita.hasNext()) {
            KanbanPropertyTemplateInfoEntity ena = ita.next();
            KanbanPropertyTemplateInfoEntity enb = itb.next();
            if (!ena.equalsDisplayInfo(enb)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 工程順が一致するか調べる
     *
     * @param a
     * @param b
     * @return 同一の時true
     */
    private boolean workflowsEquals(List a, List b) {
        if (Objects.isNull(a) || Objects.isNull(b)) {
            return false;
        }

        if (a.size() != b.size()) {
            return false;
        }

        Iterator<ConWorkflowWorkInfoEntity> ita = a.iterator();
        Iterator<ConWorkflowWorkInfoEntity> itb = b.iterator();

        while (ita.hasNext()) {
            ConWorkflowWorkInfoEntity ena = ita.next();
            ConWorkflowWorkInfoEntity enb = itb.next();
            if (!ena.equalsDisplayInfo(enb)) {
                return false;
            }
        }

        return true;
    }
}
