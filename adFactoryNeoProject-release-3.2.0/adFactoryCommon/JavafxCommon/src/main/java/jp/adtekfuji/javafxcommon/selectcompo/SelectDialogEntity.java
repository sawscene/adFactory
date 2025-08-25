/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.selectcompo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.object.ObjectInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;

/**
 * 選択ダイアログ情報
 *
 * @author e-mori
 * @param <E>
 */
public class SelectDialogEntity<E> {

    private ObjectProperty<List<EquipmentInfoEntity>> equipmentsProperty;
    private ObjectProperty<List<OrganizationInfoEntity>> organizationsProperty;
    private ObjectProperty<List<OrganizationInfoEntity>> organizationTreesProperty;
    private ObjectProperty<List<WorkflowInfoEntity>> workflowsProperty;
    private ObjectProperty<List<WorkInfoEntity>> worksProperty;
    private ObjectProperty<List<KanbanInfoEntity>> kanbansProperty;
    private ObjectProperty<List<KanbanHierarchyInfoEntity>> kanbanHierarchiesProperty;
    private ObjectProperty<List<ObjectInfoEntity>> objectsProperty;
    private List<EquipmentInfoEntity> equipments = new ArrayList<>();
    private List<OrganizationInfoEntity> organizations = new ArrayList<>();
    private List<WorkflowInfoEntity> workflows = new ArrayList<>();
    private List<WorkInfoEntity> works = new ArrayList<>();
    private List<KanbanInfoEntity> kanbans = new ArrayList<>();
    private List<KanbanHierarchyInfoEntity> kanbanHierarchies = new ArrayList<>();
    private List<ObjectInfoEntity> objects = new ArrayList<>();

    private KanbanSearchCondition condition = new KanbanSearchCondition();
    private String uribase = null;
    private boolean useInternalCondition;
    private boolean useLatestRev;
    private boolean visibleUseLatestRev = false;// 「常に最新版を使用する」を表示する？
    private Boolean liteHierarchyOnly; // adFactory Liteの階層のみを表示する

    /**
     * 承認権限のある組織のみ表示
     */
    private boolean approvalAuthorityOnly = false;

    /**
     * コンストラクタ
     */
    public SelectDialogEntity() {
    }

    /**
     * Lite階層指定に設定
     * @return 
     */
    public SelectDialogEntity liteHierarchyOnly() {
        this.liteHierarchyOnly = true;
        return this;
    }

    /**
     * 設備一覧を設定する。
     *
     * @param equipments 設備一覧
     * @return 設定後の選択ダイアログ情報
     */
    public SelectDialogEntity equipments(List<EquipmentInfoEntity> equipments) {
        this.equipments = equipments;
        return this;
    }

    /**
     * 組織一覧を設定する。
     *
     * @param organizations 組織一覧
     * @return 設定後の選択ダイアログ情報
     */
    public SelectDialogEntity organizations(List<OrganizationInfoEntity> organizations) {
        this.organizations = organizations;
        return this;
    }
    
    /**
     * 工程順一覧を設定する。
     *
     * @param workflows 工程順一覧
     * @return 設定後の選択ダイアログ情報
     */
    public SelectDialogEntity workflows(List<WorkflowInfoEntity> workflows) {
        this.workflows = workflows;
        return this;
    }

    /**
     * 工程一覧を設定する。
     *
     * @param works 工程一覧
     * @return 設定後の選択ダイアログ情報
     */
    public SelectDialogEntity works(List<WorkInfoEntity> works) {
        this.works = works;
        return this;
    }

    /**
     * カンバン一覧とカンバン検索条件を設定する。
     *
     * @param kanbans カンバン一覧
     * @param condition カンバン検索条件
     * @param useLiteHierarchy Lite階層指定
     * @return 設定後の選択ダイアログ情報
     */
    public SelectDialogEntity kanbans(List<KanbanInfoEntity> kanbans, KanbanSearchCondition condition, Boolean useLiteHierarchy) {
        this.kanbans = kanbans;
        this.condition = condition;
        this.liteHierarchyOnly = useLiteHierarchy;
        return this;
    }

    /**
     * カンバン階層一覧を設定する。
     * @param kanbansHierarchies カンバン階層一覧
     * @param useLiteHierarchy Lite階層指定
     * @return 設定後の選択ダイアログ情報
     */
    public SelectDialogEntity kanbanHierarchies(List<KanbanHierarchyInfoEntity> kanbansHierarchies, Boolean useLiteHierarchy) {
        this.kanbanHierarchies = kanbansHierarchies;
        this.liteHierarchyOnly = useLiteHierarchy;
        return this;
    }


    /**
     * モノ一覧を設定する。
     *
     * @param objects モノ一覧
     * @return 設定後の選択ダイアログ情報
     */
    public SelectDialogEntity objects(List<ObjectInfoEntity> objects) {
        this.objects = objects;
        return this;
    }

    /**
     * URIベースを設定する。
     *
     * @param uriBase URIベース
     * @return 設定後の選択ダイアログ情報
     */
    public SelectDialogEntity uri(String uriBase) {
        this.uribase = uriBase;
        return this;
    }
    
    /**
     * 設備一覧プロパティを取得する。
     *
     * @return 設備一覧プロパティ
     */
    public ObjectProperty<List<EquipmentInfoEntity>> equipmentsProperty() {
        if (Objects.isNull(this.equipmentsProperty)) {
            this.equipmentsProperty = new SimpleObjectProperty<>(this.equipments);
        }
        return this.equipmentsProperty;
    }

    /**
     * 組織一覧プロパティを取得する。
     *
     * @return 組織一覧プロパティ
     */
    public ObjectProperty<List<OrganizationInfoEntity>> organizationsProperty() {
        if (Objects.isNull(this.organizationsProperty)) {
            this.organizationsProperty = new SimpleObjectProperty<>(this.organizations);
        }
        return this.organizationsProperty;
    }

    /**
     * 工程順一覧プロパティを取得する。
     *
     * @return 工程順一覧プロパティ
     */
    public ObjectProperty<List<WorkflowInfoEntity>> workflowsProperty() {
        if (Objects.isNull(this.workflowsProperty)) {
            this.workflowsProperty = new SimpleObjectProperty<>(this.workflows);
        }
        return this.workflowsProperty;
    }

    /**
     * 工程一覧プロパティを取得する。
     *
     * @return 工程一覧プロパティ
     */
    public ObjectProperty<List<WorkInfoEntity>> worksProperty() {
        if (Objects.isNull(this.worksProperty)) {
            this.worksProperty = new SimpleObjectProperty<>(this.works);
        }
        return this.worksProperty;
    }

    /**
     * カンバン一覧プロパティを取得する。
     *
     * @return カンバン一覧プロパティ
     */
    public ObjectProperty<List<KanbanInfoEntity>> kanbansProperty() {
        if (Objects.isNull(this.kanbansProperty)) {
            this.kanbansProperty = new SimpleObjectProperty<>(this.kanbans);
        }
        return this.kanbansProperty;
    }

    /**
     * カンバン階層一覧プロパティを取得する。
     *
     * @return カンバン階層一覧プロパティ
     */
    public ObjectProperty<List<KanbanHierarchyInfoEntity>> kanbanHierarchiesProperty() {
        if(Objects.isNull(this.kanbanHierarchiesProperty)) {
            this.kanbanHierarchiesProperty = new SimpleObjectProperty<>(this.kanbanHierarchies);
        }
        return this.kanbanHierarchiesProperty;
    }


    /**
     * モノ一覧プロパティを取得する。
     *
     * @return モノ一覧プロパティ
     */
    public ObjectProperty<List<ObjectInfoEntity>> objectsProperty() {
        if (Objects.isNull(this.objectsProperty)) {
            this.objectsProperty = new SimpleObjectProperty<>(this.objects);
        }
        return this.objectsProperty;
    }

    /**
     * 設備一覧を取得する。
     *
     * @return 設備一覧
     */
    public List<EquipmentInfoEntity> getEquipments() {
        if (Objects.nonNull(this.equipmentsProperty)) {
            return this.equipmentsProperty.get();
        }
        return this.equipments;
    }

    /**
     * 設備一覧を設定する。
     *
     * @param equipments 設備一覧
     */
    public void setEquipments(List<EquipmentInfoEntity> equipments) {
        if (Objects.nonNull(this.equipmentsProperty)) {
            this.equipmentsProperty.set(equipments);
        } else {
            this.equipments = equipments;
        }
    }

    /**
     * 組織一覧を取得する。
     *
     * @return 組織一覧
     */
    public List<OrganizationInfoEntity> getOrganizations() {
        if (Objects.nonNull(this.organizationsProperty)) {
            return this.organizationsProperty.get();
        }
        return this.organizations;
    }

    /**
     * 組織一覧を設定する。
     *
     * @param organizations 組織一覧
     */
    public void setOrganizations(List<OrganizationInfoEntity> organizations) {
        if (Objects.nonNull(this.organizationsProperty)) {
            this.organizationsProperty.set(organizations);
        } else {
            this.organizations = organizations;
        }
    }
    
    /**
     * 工程順一覧を取得する。
     *
     * @return 工程順一覧
     */
    public List<WorkflowInfoEntity> getWorkflows() {
        if (Objects.nonNull(this.workflowsProperty)) {
            return this.workflowsProperty.get();
        }
        return this.workflows;
    }

    /**
     * 工程順一覧を設定する。
     *
     * @param workflows 工程順一覧
     */
    public void setWorkflows(List<WorkflowInfoEntity> workflows) {
        if (Objects.nonNull(this.workflowsProperty)) {
            this.workflowsProperty.set(workflows);
        } else {
            this.workflows = workflows;
        }
    }

    /**
     * 工程一覧を取得する。
     *
     * @return 工程一覧
     */
    public List<WorkInfoEntity> getWorks() {
        if (Objects.nonNull(this.worksProperty)) {
            return this.worksProperty.get();
        }
        return this.works;
    }

    /**
     * 工程一覧を設定する。
     *
     * @param works 工程一覧
     */
    public void setWorks(List<WorkInfoEntity> works) {
        if (Objects.nonNull(this.worksProperty)) {
            this.worksProperty.set(works);
        } else {
            this.works = works;
        }
    }

    /**
     * カンバン一覧を取得する。
     *
     * @return カンバン一覧
     */
    public List<KanbanInfoEntity> getKanbans() {
        if (Objects.nonNull(this.kanbansProperty)) {
            return this.kanbansProperty.get();
        }
        return this.kanbans;
    }

    /**
     * カンバン一覧を設定する。
     *
     * @param kanbans カンバン一覧
     */
    public void setKanbans(List<KanbanInfoEntity> kanbans) {
        if (Objects.nonNull(kanbansProperty)) {
            this.kanbansProperty.set(kanbans);
        } else {
            this.kanbans = kanbans;
        }
    }

    /**
     * カンバン一覧を取得する。
     *
     * @return カンバン一覧
     */
    public List<KanbanHierarchyInfoEntity> getKanbanHierarchies() {
        if (Objects.nonNull(this.kanbanHierarchiesProperty)) {
            return this.kanbanHierarchiesProperty.get();
        }
        return this.kanbanHierarchies;
    }

    /**
     * カンバン階層一覧を設定する。
     *
     * @param kanbanHierarchies カンバン階層一覧
     */
    public void setKanbanHierarchies(List<KanbanHierarchyInfoEntity> kanbanHierarchies) {
        if (Objects.nonNull(kanbanHierarchiesProperty)) {
            this.kanbanHierarchiesProperty.set(kanbanHierarchies);
        } else {
            this.kanbanHierarchies = kanbanHierarchies;
        }
    }


    /**
     * モノ一覧を取得する。
     *
     * @return モノ一覧
     */
    public List<ObjectInfoEntity> getObjects() {
        if (Objects.nonNull(this.objectsProperty)) {
            return this.objectsProperty.get();
        }
        return this.objects;
    }

    /**
     * モノ一覧を設定する。
     *
     * @param objects モノ一覧
     */
    public void setObjects(List<ObjectInfoEntity> objects) {
        if (Objects.nonNull(this.objectsProperty)) {
            this.objectsProperty.set(objects);
        } else {
            this.objects = objects;
        }
    }

    /**
     * カンバン検索条件を取得する。
     *
     * @return カンバン検索条件
     */
    public KanbanSearchCondition getCondition() {
        return this.condition;
    }

    /**
     * カンバン検索条件を設定する。
     *
     * @param condition カンバン検索条件
     */
    public void setCondition(KanbanSearchCondition condition) {
        this.condition = condition;
    }

    /**
     * URIベースを取得する。
     *
     * @return URIベース
     */
    public String getUribase() {
        return this.uribase;
    }

    /**
     * URIベースを設定する。
     *
     * @param uribase URIベース
     */
    public void setUribase(String uribase) {
        this.uribase = uribase;
    }
    
    /**
     * 内部条件設定が有効？
     *
     * @return 内部条件設定が有効？ (true:有効, false:無効)
     */
    public boolean isUseInternalCondition() {
        return this.useInternalCondition;
    }

    /**
     * 内部条件設定が有効かを設定する。
     *
     * @param internalCondition 内部条件設定が有効？ (true:有効, false:無効)
     */
    public void enableInternalCondition(boolean internalCondition) {
        this.useInternalCondition = internalCondition;
    }

    /**
     * 常に最新版を使用する？
     *
     * @return 常に最新版を使用する？ (true:使用する, false:使用しない)
     */
    public boolean isUseLatestRev() {
        return this.useLatestRev;
    }

    /**
     * 常に最新版を使用するかを設定する。
     *
     * @param useLatestRev 常に最新版を使用する？ (true:使用する, false:使用しない)
     */
    public void setUseLatestRev(boolean useLatestRev) {
        this.useLatestRev = useLatestRev;
    }

    /**
     * 「常に最新版を使用する」を表示するかを取得する。
     *
     * @return 表示 (true:表示する, false:表示しない)
     */
    public boolean isVisibleUseLatestRev() {
        return this.visibleUseLatestRev;
    }

    /**
     * 「常に最新版を使用する」を表示するかを設定する。
     *
     * @param visibleUseLatestRev 表示 (true:表示する, false:表示しない)
     */
    public void setVisibleUseLatestRev(boolean visibleUseLatestRev) {
        this.visibleUseLatestRev = visibleUseLatestRev;
    }

    /**
     * 承認権限のある組織のみ表示するかを取得する。
     *
     * @return true:承認権限のある組織のみ表示, false:全て表示
     */
    public boolean isApprovalAuthorityOnly() {
        return this.approvalAuthorityOnly;
    }

    /**
     * 承認権限のある組織のみ表示するかを設定する。
     *
     * @param approvalAuthorityOnly true:承認権限のある組織のみ表示, false:全て表示
     */
    public void setApprovalAuthorityOnly(boolean approvalAuthorityOnly) {
        this.approvalAuthorityOnly = approvalAuthorityOnly;
    }

    /**
     * Lite階層指定を取得する。
     *
     * @return Lite階層指定
     */
    public Boolean getLiteHierarchyOnly() {
        return this.liteHierarchyOnly;
    }

}
