/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.master;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 役割権限情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_role_authority")
@XmlRootElement(name = "roleAuthority")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 追加時のロール名重複チェック
    @NamedQuery(name = "RoleAuthorityEntity.checkAddByRoleName", query = "SELECT COUNT(r.roleId) FROM RoleAuthorityEntity r WHERE r.roleName = :roleName"),
    // 更新時のロール名重複チェック
    @NamedQuery(name = "RoleAuthorityEntity.checkUpdateByRoleName", query = "SELECT COUNT(r.roleId) FROM RoleAuthorityEntity r WHERE r.roleName = :roleName AND r.roleId != :roleId"),
    // リソース編集権限のある役割の存在チェック
    @NamedQuery(name = "RoleAuthorityEntity.findResourceEdit", query = "SELECT r FROM RoleAuthorityEntity r WHERE r.resourceEdit = true"),

    // 組織IDを指定して、役割権限情報一覧を取得する。
    @NamedQuery(name = "RoleAuthorityEntity.findByOrganizationId", query = "SELECT r FROM RoleAuthorityEntity r WHERE r.roleId IN (SELECT c.roleId FROM ConOrganizationRoleEntity c WHERE c.organizationId = :organizationId GROUP BY c.roleId)"),
})
public class RoleAuthorityEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "role_id")
    private Long roleId;// 役割ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "role_name")
    private String roleName;// 役割名

    @Basic(optional = false)
    @Column(name = "actual_del")
    private boolean actualDel;// 実績削除権限

    @Basic(optional = false)
    @Column(name = "resource_edit")
    private boolean resourceEdit;// リソース編集権限

    @Basic(optional = false)
    @Column(name = "kanban_create")
    private boolean kanbanCreate;// カンバン作成権限

    @Basic(optional = false)
    @Column(name = "line_manage")
    private boolean lineManage;// ライン管理権限

    @Basic(optional = false)
    @Column(name = "actual_output")
    private boolean actualOutput;// 実績出力権限

    @Basic(optional = false)
    @Column(name = "kanban_reference")
    private boolean kanbanReference;// カンバン参照権限

    @Basic(optional = false)
    @Column(name = "resource_reference")
    private boolean resourceReference;// リソース参照権限

    @Basic(optional = false)
    @Column(name = "access_edit")
    private boolean accessEdit;// アクセス権編集権限
    
    @Basic(optional = false)
    @Column(name = "approve")
    private boolean approve;// 承認権限

    @Basic(optional = false)
    @Column(name = "workflow_edit")
    private boolean workflowEdit;           // 工程・工程順編集権限

    @Basic(optional = false)
    @Column(name = "workflow_reference")
    private boolean workflowReference;      // 工程・工程順参照権限

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * コンストラクタ
     */
    public RoleAuthorityEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param roleId 役割ID
     */
    public RoleAuthorityEntity(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * コンストラクタ
     *
     * @param roleId 役割ID
     * @param roleName 役割名
     */
    public RoleAuthorityEntity(Long roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    /**
     * 役割IDを取得する。
     *
     * @return 役割ID
     */
    public Long getRoleId() {
        return this.roleId;
    }

    /**
     * 役割IDを設定する。
     *
     * @param roleId 役割ID
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * 役割名を取得する。
     *
     * @return 役割名
     */
    public String getRoleName() {
        return this.roleName;
    }

    /**
     * 役割名を設定する。
     *
     * @param roleName 役割名
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * 実績削除権限を取得する。
     *
     * @return 実績削除権限
     */
    public boolean getActualDel() {
        return this.actualDel;
    }

    /**
     * 実績削除権限を設定する。
     *
     * @param authorityActualDel 実績削除権限
     */
    public void setActualDel(boolean authorityActualDel) {
        this.actualDel = authorityActualDel;
    }

    /**
     * リソース編集権限を取得する。
     *
     * @return リソース編集権限
     */
    public boolean getResourceEdit() {
        return this.resourceEdit;
    }

    /**
     * リソース編集権限を設定する。
     *
     * @param authorityResourceEdit リソース編集権限
     */
    public void setResourceEdit(boolean authorityResourceEdit) {
        this.resourceEdit = authorityResourceEdit;
    }

    /**
     * カンバン作成権限を取得する。
     *
     * @return カンバン作成権限
     */
    public boolean getKanbanCreate() {
        return this.kanbanCreate;
    }

    /**
     * カンバン作成権限を設定する。
     *
     * @param authorityKanbanCreate カンバン作成権限
     */
    public void setKanbanCreate(boolean authorityKanbanCreate) {
        this.kanbanCreate = authorityKanbanCreate;
    }

    /**
     * ライン管理権限を取得する。
     *
     * @return ライン管理権限
     */
    public boolean getLineManage() {
        return this.lineManage;
    }

    /**
     * ライン管理権限を設定する。
     *
     * @param authorityLineManage ライン管理権限
     */
    public void setLineManage(boolean authorityLineManage) {
        this.lineManage = authorityLineManage;
    }

    /**
     * 実績出力権限を取得する。
     *
     * @return 実績出力権限
     */
    public boolean getActualOutput() {
        return this.actualOutput;
    }

    /**
     * 実績出力権限を設定する。
     *
     * @param authorityActualOutput 実績出力権限
     */
    public void setActualOutput(boolean authorityActualOutput) {
        this.actualOutput = authorityActualOutput;
    }

    /**
     * カンバン参照権限を取得する。
     *
     * @return カンバン参照権限
     */
    public boolean getKanbanReference() {
        return this.kanbanReference;
    }

    /**
     * カンバン参照権限を設定する。
     *
     * @param authorityKanbanReference カンバン参照権限
     */
    public void setKanbanReference(boolean authorityKanbanReference) {
        this.kanbanReference = authorityKanbanReference;
    }

    /**
     * リソース参照権限を取得する。
     *
     * @return リソース参照権限
     */
    public boolean getResourceReference() {
        return this.resourceReference;
    }

    /**
     * リソース参照権限を設定する。
     *
     * @param authorityResourceReference リソース参照権限
     */
    public void setResourceReference(boolean authorityResourceReference) {
        this.resourceReference = authorityResourceReference;
    }

    /**
     * アクセス権編集権限を取得する。
     *
     * @return アクセス権編集権限
     */
    public boolean getAccessEdit() {
        return this.accessEdit;
    }

    /**
     * アクセス権編集権限を設定する。
     *
     * @param authorityAccessEdit アクセス権編集権限
     */
    public void setAccessEdit(boolean authorityAccessEdit) {
        this.accessEdit = authorityAccessEdit;
    }
    
    /**
     * 承認権限を取得する。
     *
     * @return 承認権限
     */
    public boolean getApprove() {
        return this.approve;
    }

    /**
     * 承認権限を設定する。
     *
     * @param approve 承認権限
     */
    public void setApprove(boolean approve) {
        this.approve = approve;
    }

    /**
     * 工程・工程順編集権限を取得する。
     * 
     * @return 工程・工程順編集権限 
     */
    public boolean getWorkflowEdit() {
        return workflowEdit;
    }

    /**
     * 工程・工程順編集権限を設定する。
     * 
     * @param workflowEdit 工程・工程順編集権限
     */
    public void setWorkflowEdit(boolean workflowEdit) {
        this.workflowEdit = workflowEdit;
    }

    /**
     * 工程・工程順参照権限を取得する。
     * 
     * @return 工程・工程順参照権限 
     */
    public boolean getWorkflowReference() {
        return workflowReference;
    }

    /**
     * 工程・工程順参照権限を設定する。
     * 
     * @param workflowReference 工程・工程順参照権限
     */
    public void setWorkflowReference(boolean workflowReference) {
        this.workflowReference = workflowReference;
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
     * ハッシュコードを取得する。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roleId != null ? roleId.hashCode() : 0);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true: 等しい(同値)、false: 異なる
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RoleAuthorityEntity other = (RoleAuthorityEntity) obj;
        if (!Objects.equals(this.roleName, other.roleName)) {
            return false;
        }
        return Objects.equals(this.roleId, other.roleId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("RoleAuthorityEntity{")
                .append("roleId=").append(this.roleId)
                .append(", roleName=").append(this.roleName)
                .append(", actualDel=").append(this.actualDel)
                .append(", resourceEdit=").append(this.resourceEdit)
                .append(", kanbanCreate=").append(this.kanbanCreate)
                .append(", lineManage=").append(this.lineManage)
                .append(", actualOutput=").append(this.actualOutput)
                .append(", kanbanReference=").append(this.kanbanReference)
                .append(", resourceReference=").append(this.resourceReference)
                .append(", accessEdit=").append(this.accessEdit)
                .append(", approve=").append(this.approve)
                .append(", workflowEdit=").append(this.workflowEdit)
                .append(", workflowReference=").append(this.workflowReference)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
