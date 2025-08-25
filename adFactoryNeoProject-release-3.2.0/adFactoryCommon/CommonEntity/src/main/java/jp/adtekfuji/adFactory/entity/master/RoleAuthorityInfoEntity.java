/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.master;

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * 役割権限情報
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "roleAuthority")
public class RoleAuthorityInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty roleIdProperty;
    private StringProperty roleNameProperty;

    /**
     * 実績削除権限プロパティ
     */
    private BooleanProperty actualDelProperty;

    /**
     * リソース編集権限プロパティ
     */
    private BooleanProperty resourceEditProperty;

    /**
     * カンバン作成権限プロパティ
     */
    private BooleanProperty kanbanCreateProperty;

    /**
     * 工程・工程順編集権限プロパティ
     */
    private BooleanProperty workflowEditProperty;

    /**
     * ライン管理権限プロパティ
     */
    private BooleanProperty lineManageProperty;

    /**
     * 実績出力権限プロパティ
     */
    private BooleanProperty actualOutputProperty;

    /**
     * 工程・工程順参照権限プロパティ
     */
    private BooleanProperty workflowReferenceProperty;

    /**
     * カンバン参照権限プロパティ
     */
    private BooleanProperty kanbanReferenceProperty;

    /**
     * リソース参照権限プロパティ
     */
    private BooleanProperty resourceReferenceProperty;

    /**
     * アクセス権編集権限プロパティ
     */
    private BooleanProperty accessEditProperty;
    
    /**
     * 承認権限プロパティ
     */
    private BooleanProperty approveProperty;

    @XmlElement(required = true)
    private Long roleId;
    @XmlElement()
    private String roleName;
    @XmlElement()
    private boolean actualDel = false;
    @XmlElement()
    private boolean resourceEdit = false;
    @XmlElement()
    private boolean kanbanCreate = false;
    @XmlElement()
    private boolean workflowEdit = false;
    @XmlElement()
    private boolean lineManage = false;
    @XmlElement()
    private boolean actualOutput = false;
    @XmlElement()
    private boolean workflowReference = false;
    @XmlElement()
    private boolean kanbanReference = false;
    @XmlElement()
    private boolean resourceReference = false;
    @XmlElement()
    private boolean accessEdit = false;
    @XmlElement()
    private boolean approve = false;


    @XmlElement()
    private Integer verInfo;// 排他用バーション

    public RoleAuthorityInfoEntity() {
    }

    /**
     * コンストラクタ(既存エンティティを元に作成)
     *
     * @param in 元にするエンティティ
     */
    public RoleAuthorityInfoEntity(RoleAuthorityInfoEntity in) {
        this.roleId = in.getRoleId();
        this.roleName = in.getRoleName();
        this.actualDel = in.getActualDel();
        this.resourceEdit = in.getResourceEdit();
        this.kanbanCreate = in.getKanbanCreate();
        this.workflowEdit = in.getWorkflowEdit();
        this.lineManage = in.getLineManage();
        this.actualOutput = in.getActualOutput();
        this.workflowReference = in.getWorkflowReference();
        this.kanbanReference = in.getKanbanReference();
        this.resourceReference = in.getResourceReference();
        this.accessEdit = in.getAccessEdit();
        this.approve = in.getApprove();
        this.verInfo = in.verInfo;
    }

    /**
     * コンストラクタ
     *
     * @param roleId 権限ID
     * @param roleName 権限名
     */
    public RoleAuthorityInfoEntity(Long roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    /**
     * 役割IDプロパティを取得する。
     *
     * @return 役割ID
     */
    public LongProperty roleIdProperty() {
        if (Objects.isNull(this.roleIdProperty)) {
            this.roleIdProperty = new SimpleLongProperty(this.roleId);
        }
        return this.roleIdProperty;
    }

    /**
     * 役割名プロパティを取得する。
     *
     * @return 役割名
     */
    public StringProperty roleNameProperty() {
        if (Objects.isNull(this.roleNameProperty)) {
            this.roleNameProperty = new SimpleStringProperty(this.roleName);
        }
        return this.roleNameProperty;
    }

    /**
     * 実績削除権限のプロパティを返します
     *
     * @return JavaFXのプロパティ
     */
    public BooleanProperty actualDelProperty() {
        if (Objects.isNull(this.actualDelProperty)) {
            this.actualDelProperty = new SimpleBooleanProperty(this.actualDel);
        }
        return this.actualDelProperty;
    }

    /**
     * リソース編集権限のプロパティを返します
     *
     * @return JavaFXのプロパティ
     */
    public BooleanProperty resourceEditProperty() {
        if (Objects.isNull(this.resourceEditProperty)) {
            this.resourceEditProperty = new SimpleBooleanProperty(this.resourceEdit);
        }
        return this.resourceEditProperty;
    }

    /**
     * カンバン作成権限のプロパティを返します
     *
     * @return JavaFXのプロパティ
     */
    public BooleanProperty kanbanCreateProperty() {
        if (Objects.isNull(this.kanbanCreateProperty)) {
            this.kanbanCreateProperty = new SimpleBooleanProperty(this.kanbanCreate);
        }
        return this.kanbanCreateProperty;
    }

    /**
     * 工程・工程順編集権限のプロパティを返します
     *
     * @return JavaFXのプロパティ
     */
    public BooleanProperty workflowEditProperty() {
        if (Objects.isNull(this.workflowEditProperty)) {
            this.workflowEditProperty = new SimpleBooleanProperty(this.workflowEdit);
        }
        return this.workflowEditProperty;
    }

    /**
     * ライン管理権限のプロパティを返します
     *
     * @return JavaFXのプロパティ
     */
    public BooleanProperty lineManageProperty() {
        if (Objects.isNull(this.lineManageProperty)) {
            this.lineManageProperty = new SimpleBooleanProperty(this.lineManage);
        }
        return this.lineManageProperty;
    }

    /**
     * 実績出力権限のプロパティを返します
     *
     * @return JavaFXのプロパティ
     */
    public BooleanProperty actualOutputProperty() {
        if (Objects.isNull(this.actualOutputProperty)) {
            this.actualOutputProperty = new SimpleBooleanProperty(this.actualOutput);
        }
        return this.actualOutputProperty;
    }

    /**
     * 工程・工程順参照権限のプロパティを返します
     *
     * @return JavaFXのプロパティ
     */
    public BooleanProperty workflowReferenceProperty() {
        if (Objects.isNull(this.workflowReferenceProperty)) {
            this.workflowReferenceProperty = new SimpleBooleanProperty(this.workflowReference);
        }
        return this.workflowReferenceProperty;
    }

    /**
     * カンバン参照権限のプロパティを返します
     *
     * @return JavaFXのプロパティ
     */
    public BooleanProperty kanbanReferenceProperty() {
        if (Objects.isNull(this.kanbanReferenceProperty)) {
            this.kanbanReferenceProperty = new SimpleBooleanProperty(this.kanbanReference);
        }
        return this.kanbanReferenceProperty;
    }

    /**
     * リソース参照権限のプロパティを返します
     *
     * @return JavaFXのプロパティ
     */
    public BooleanProperty resourceReferenceProperty() {
        if (Objects.isNull(this.resourceReferenceProperty)) {
            this.resourceReferenceProperty = new SimpleBooleanProperty(this.resourceReference);
        }
        return this.resourceReferenceProperty;
    }

    /**
     * アクセス権編集権限のプロパティを返します
     *
     * @return JavaFXのプロパティ
     */
    public BooleanProperty accessEditProperty() {
        if (Objects.isNull(this.accessEditProperty)) {
            this.accessEditProperty = new SimpleBooleanProperty(this.accessEdit);
        }
        return this.accessEditProperty;
    }
    
    /**
     * 承認権限のプロパティを返します
     *
     * @return JavaFXのプロパティ
     */
    public BooleanProperty approveProperty() {
        if (Objects.isNull(this.approveProperty)) {
            this.approveProperty = new SimpleBooleanProperty(this.approve);
        }
        return this.approveProperty;
    }


    /**
     * 役割IDを取得する。
     *
     * @return 役割ID
     */
    public Long getRoleId() {
        if (Objects.nonNull(this.roleIdProperty)) {
            return this.roleIdProperty.get();
        }
        return this.roleId;
    }

    /**
     * 役割IDを設定する。
     *
     * @param roleId 役割ID
     */
    public void setRoleId(Long roleId) {
        if (Objects.nonNull(this.roleIdProperty)) {
            this.roleIdProperty.set(roleId);
        } else {
            this.roleId = roleId;
        }
    }

    /**
     * 役割名を取得する。
     *
     * @return 役割名
     */
    public String getRoleName() {
        if (Objects.nonNull(this.roleNameProperty)) {
            return this.roleNameProperty.get();
        }
        return this.roleName;
    }

    /**
     * 役割名を設定する。
     *
     * @param roleName 役割名
     */
    public void setRoleName(String roleName) {
        if (Objects.nonNull(this.roleNameProperty)) {
            this.roleNameProperty.set(roleName);
        } else {
            this.roleName = roleName;
        }
    }

    /**
     * 実績削除権限を取得する。
     *
     * @return 実績削除権限
     */
    public boolean getActualDel() {
        if (Objects.nonNull(this.actualDelProperty)) {
            return this.actualDelProperty.get();
        }
        return this.actualDel;
    }

    /**
     * 実績削除権限を設定する。
     *
     * @param actualDel 実績削除権限
     */
    public void setActualDel(boolean actualDel) {
        if (Objects.nonNull(this.actualDelProperty)) {
            this.actualDelProperty.set(actualDel);
        } else {
            this.actualDel = actualDel;
        }
    }

    /**
     * リソース編集権限を取得する。
     *
     * @return リソース編集権限
     */
    public boolean getResourceEdit() {
        if (Objects.nonNull(this.resourceEditProperty)) {
            return this.resourceEditProperty.get();
        }
        return this.resourceEdit;
    }

    /**
     * リソース編集権限を設定する。
     *
     * @param resourceEdit リソース編集権限
     */
    public void setResourceEdit(boolean resourceEdit) {
        if (Objects.nonNull(this.resourceEditProperty)) {
            this.resourceEditProperty.set(resourceEdit);
        } else {
            this.resourceEdit = resourceEdit;
        }
    }

    /**
     * カンバン作成権限を取得する。
     *
     * @return カンバン作成権限
     */
    public boolean getKanbanCreate() {
        if (Objects.nonNull(this.kanbanCreateProperty)) {
            return this.kanbanCreateProperty.get();
        }
        return this.kanbanCreate;
    }

    /**
     * カンバン作成権限を設定する。
     *
     * @param kanbanCreate カンバン作成権限
     */
    public void setKanbanCreate(boolean kanbanCreate) {
        if (Objects.nonNull(this.kanbanCreateProperty)) {
            this.kanbanCreateProperty.set(kanbanCreate);
        } else {
            this.kanbanCreate = kanbanCreate;
        }
    }

    /**
     * 工程・工程順編集権限を取得する。
     *
     * @return 工程・工程順編集権限
     */
    public boolean getWorkflowEdit() {
        if (Objects.nonNull(this.workflowEditProperty)) {
            return this.workflowEditProperty.get();
        }
        return this.workflowEdit;
    }

    /**
     * 工程・工程順編集権限を設定する。
     *
     * @param workflowEdit 工程・工程順編集権限
     */
    public void setWorkflowEdit(boolean workflowEdit) {
        if (Objects.nonNull(this.workflowEditProperty)) {
            this.workflowEditProperty.set(workflowEdit);
        } else {
            this.workflowEdit = workflowEdit;
        }
    }

    /**
     * ライン管理権限を取得する。
     *
     * @return ライン管理権限
     */
    public boolean getLineManage() {
        if (Objects.nonNull(this.lineManageProperty)) {
            return this.lineManageProperty.get();
        }
        return this.lineManage;
    }

    /**
     * ライン管理権限を設定する。
     *
     * @param lineManage ライン管理権限
     */
    public void setLineManage(boolean lineManage) {
        if (Objects.nonNull(this.lineManageProperty)) {
            this.lineManageProperty.set(lineManage);
        } else {
            this.lineManage = lineManage;
        }
    }

    /**
     * 実績出力権限を取得する。
     *
     * @return 実績出力権限
     */
    public boolean getActualOutput() {
        if (Objects.nonNull(this.actualOutputProperty)) {
            return this.actualOutputProperty.get();
        }
        return this.actualOutput;
    }

    /**
     * 実績出力権限を設定する。
     *
     * @param actualOutput 実績出力権限
     */
    public void setActualOutput(boolean actualOutput) {
        if (Objects.nonNull(this.actualOutputProperty)) {
            this.actualOutputProperty.set(actualOutput);
        } else {
            this.actualOutput = actualOutput;
        }
    }

    /**
     * 工程・工程順参照権限を取得する。
     *
     * @return 工程・工程順参照権限
     */
    public boolean getWorkflowReference() {
        if (Objects.nonNull(this.workflowReferenceProperty)) {
            return this.workflowReferenceProperty.get();
        }
        return this.workflowReference;
    }

    /**
     * 工程・工程順参照権限を設定する。
     *
     * @param workflowReference 工程・工程順参照権限
     */
    public void setWorkflowReference(boolean workflowReference) {
        if (Objects.nonNull(this.workflowReferenceProperty)) {
            this.workflowReferenceProperty.set(workflowReference);
        } else {
            this.workflowReference = workflowReference;
        }
    }

    /**
     * カンバン参照権限を取得する。
     *
     * @return カンバン参照権限
     */
    public boolean getKanbanReference() {
        if (Objects.nonNull(this.kanbanReferenceProperty)) {
            return this.kanbanReferenceProperty.get();
        }
        return this.kanbanReference;
    }

    /**
     * カンバン参照権限を設定する。
     *
     * @param kanbanReference カンバン参照権限
     */
    public void setKanbanReference(boolean kanbanReference) {
        if (Objects.nonNull(this.kanbanReferenceProperty)) {
            this.kanbanReferenceProperty.set(kanbanReference);
        } else {
            this.kanbanReference = kanbanReference;
        }
    }

    /**
     * リソース参照権限を取得する。
     *
     * @return リソース参照権限
     */
    public boolean getResourceReference() {
        if (Objects.nonNull(this.resourceReferenceProperty)) {
            return this.resourceReferenceProperty.get();
        }
        return this.resourceReference;
    }

    /**
     * リソース参照権限を設定する。
     *
     * @param resourceReference リソース参照権限
     */
    public void setResourceReference(boolean resourceReference) {
        if (Objects.nonNull(this.resourceReferenceProperty)) {
            this.resourceReferenceProperty.set(resourceReference);
        } else {
            this.resourceReference = resourceReference;
        }
    }

    /**
     * アクセス権編集権限を取得する。
     *
     * @return アクセス権編集権限
     */
    public boolean getAccessEdit() {
        if (Objects.nonNull(this.accessEditProperty)) {
            return this.accessEditProperty.get();
        }
        return this.accessEdit;
    }

    /**
     * アクセス権編集権限を設定する。
     *
     * @param accessEdit アクセス権編集権限
     */
    public void setAccessEdit(boolean accessEdit) {
        if (Objects.nonNull(this.accessEditProperty)) {
            this.accessEditProperty.set(accessEdit);
        } else {
            this.accessEdit = accessEdit;
        }
    }
    
    /**
     * 承認権限を取得する。
     *
     * @return 承認権限
     */
    public boolean getApprove() {
        if (Objects.nonNull(this.approveProperty)) {
            return this.approveProperty.get();
        }
        return this.approve;
    }

    /**
     * 承認権限を設定する。
     *
     * @param approve 承認権限
     */
    public void setApprove(boolean approve) {
        if (Objects.nonNull(this.approveProperty)) {
            this.approveProperty.set(approve);
        } else {
            this.approve = approve;
        }
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
     * 内部変数を更新する。
     */
    public void updateData() {
        this.roleName = this.getRoleName();
        this.actualDel = this.getActualDel();
        this.resourceEdit = this.getResourceEdit();
        this.kanbanCreate = this.getKanbanCreate();
        this.workflowEdit = this.getWorkflowEdit();
        this.lineManage = this.getLineManage();
        this.actualOutput = this.getActualOutput();
        this.workflowReference = this.getWorkflowReference();
        this.kanbanReference = this.getKanbanReference();
        this.resourceReference = this.getResourceReference();
        this.accessEdit = this.getAccessEdit();
        this.approve = this.getApprove();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roleId != null ? roleId.hashCode() : 0);
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
        final RoleAuthorityInfoEntity other = (RoleAuthorityInfoEntity) obj;
        if (!Objects.equals(this.getRoleId(), other.getRoleId())
                || !Objects.equals(this.getRoleName(), other.getRoleName())
                || !Objects.equals(this.getActualDel(), other.getActualDel())
                || !Objects.equals(this.getResourceEdit(), other.getResourceEdit())
                || !Objects.equals(this.getKanbanCreate(), other.getKanbanCreate())
                || !Objects.equals(this.getWorkflowEdit(), other.getWorkflowEdit())
                || !Objects.equals(this.getLineManage(), other.getLineManage())
                || !Objects.equals(this.getActualOutput(), other.getActualOutput())
                || !Objects.equals(this.getWorkflowReference(), other.getWorkflowReference())
                || !Objects.equals(this.getKanbanReference(), other.getKanbanReference())
                || !Objects.equals(this.getResourceReference(), other.getResourceReference())
                || !Objects.equals(this.getAccessEdit(), other.getAccessEdit())
                || !Objects.equals(this.getApprove(), other.getApprove())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("RoleAuthorityInfoEntity{")
                .append("roleId=").append(this.roleId)
                .append(", ")
                .append("roleName=").append(this.roleName)
                .append(", ")
                .append("actualDel=").append(this.actualDel)
                .append(", ")
                .append("resourceEdit=").append(this.resourceEdit)
                .append(", ")
                .append("kanbanCreate=").append(this.kanbanCreate)
                .append(", ")
                .append("workflowEdit=").append(this.workflowEdit)
                .append(", ")
                .append("lineManage=").append(this.lineManage)
                .append(", ")
                .append("actualOutput=").append(this.actualOutput)
                .append(", ")
                .append("workflowReference=").append(this.workflowReference)
                .append(", ")
                .append("kanbanReference=").append(this.kanbanReference)
                .append(", ")
                .append("resourceReference=").append(this.resourceReference)
                .append(", ")
                .append("accessEdit=").append(this.accessEdit)
                .append(", ")
                .append("approvalAuth=").append(this.approve)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
