/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.organization;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.IndirectWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.master.ReasonMasterEntity;
import jp.adtekfuji.adfactoryserver.entity.resource.LocaleFileEntity;
import jp.adtekfuji.adfactoryserver.utility.PgJsonbConverter;

/**
 * 組織情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_organization")
@XmlRootElement(name = "organization")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
        // 指定した祖先の組織を取得
        @NamedNativeQuery(name = "OrganizationEntity.findAncestorsByIds", query = "WITH RECURSIVE organizations AS (SELECT * FROM mst_organization og WHERE og.parent_organization_id = ANY (?1) OR og.organization_id = ANY (?1) UNION DISTINCT SELECT og2.* FROM mst_organization og2, organizations WHERE organizations.parent_organization_id = og2.organization_id) SELECT * FROM organizations ogs", resultClass = OrganizationEntity.class),
})
@NamedQueries({
    // 組織識別名の重複確認 (追加時) ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.checkAddByIdent", query = "SELECT COUNT(o.organizationId) FROM OrganizationEntity o WHERE o.organizationIdentify = :organizationIdentify"),
    // 組織識別名の重複確認 (更新時) ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.checkUpdateByIdent", query = "SELECT COUNT(o.organizationId) FROM OrganizationEntity o WHERE o.organizationIdentify = :organizationIdentify AND o.organizationId != :organizationId"),

    // 組織情報をすべて取得する。 ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.findAll", query = "SELECT o FROM OrganizationEntity o ORDER BY o.organizationName, o.organizationIdentify, o.organizationId"),
    // 組織ID一覧を指定して、組織情報一覧を取得する。(削除済の組織は対象外)
    @NamedQuery(name = "OrganizationEntity.findByIdsNotRemove", query = "SELECT o FROM OrganizationEntity o WHERE o.organizationId IN :organizationIds AND o.removeFlag = false ORDER BY o.organizationName, o.organizationIdentify, o.organizationId"),
    // 組織ID一覧を指定して、組織情報一覧を取得する。 ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.findByIds", query = "SELECT o FROM OrganizationEntity o WHERE o.organizationId IN :organizationIds ORDER BY o.organizationName, o.organizationIdentify, o.organizationId"),

    // 組織識別名を指定して、組織情報を取得する。(削除済の組織は対象外)
    @NamedQuery(name = "OrganizationEntity.findByIdentNotRemove", query = "SELECT o FROM OrganizationEntity o WHERE o.organizationIdentify = :organizationIdentify AND o.removeFlag = false"),

    // 組織IDを指定して、親組織IDを取得する。 ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.findParentId", query = "SELECT o.parentOrganizationId FROM OrganizationEntity o WHERE o.organizationId = :organizationId"),
    // 組織IDを指定して、子組織ID一覧を取得する。 ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.findChildId", query = "SELECT o.organizationId FROM OrganizationEntity o WHERE o.parentOrganizationId = :organizationId"),

    // 組織IDを指定して、子組織の組織情報の件数を取得する。 ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.countChild", query = "SELECT COUNT(o.organizationId) FROM OrganizationEntity o WHERE o.parentOrganizationId = :organizationId"),
    // 組織IDを指定して、子組織の組織情報一覧を取得する。 ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.findChild", query = "SELECT o FROM OrganizationEntity o WHERE o.parentOrganizationId = :organizationId ORDER BY o.organizationName, o.organizationIdentify, o.organizationId"),

    // 組織ID・ユーザーID(組織ID)一覧を指定して、アクセス可能な子組織の組織情報の件数を取得する。 ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.countChildByUserId", query = "SELECT COUNT(DISTINCT(o.organizationId)) FROM OrganizationEntity o LEFT JOIN AccessHierarchyEntity a ON a.typeId = jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum.OrganizationHierarchy AND a.hierarchyId = o.organizationId WHERE o.parentOrganizationId = :organizationId AND (a.organizationId IS NULL OR a.organizationId IN :ancestors)"),
    // 組織ID・ユーザーID(組織ID)一覧を指定して、アクセス可能な子組織の組織情報一覧を取得する。 ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.findChildByUserId", query = "SELECT DISTINCT(o) FROM OrganizationEntity o LEFT JOIN AccessHierarchyEntity a ON a.typeId = jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum.OrganizationHierarchy AND a.hierarchyId = o.organizationId WHERE o.parentOrganizationId = :organizationId AND (a.organizationId IS NULL OR a.organizationId IN :ancestors) ORDER BY o.organizationName, o.organizationIdentify, o.organizationId"),

    // ユーザーID(組織ID)一覧を指定して、アクセス可能な組織情報の件数を取得する。 ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.countByUserId", query = "SELECT COUNT(DISTINCT(o.organizationId)) FROM OrganizationEntity o LEFT JOIN AccessHierarchyEntity a ON a.typeId = jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum.OrganizationHierarchy AND a.hierarchyId = o.organizationId WHERE (a.organizationId IS NULL OR a.organizationId IN :ancestors)"),
    // ユーザーID(組織ID)一覧を指定して、アクセス可能な組織情報一覧を取得する。 ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.findByUserId", query = "SELECT DISTINCT(o) FROM OrganizationEntity o LEFT JOIN AccessHierarchyEntity a ON a.typeId = jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum.OrganizationHierarchy AND a.hierarchyId = o.organizationId WHERE (a.organizationId IS NULL OR a.organizationId IN :ancestors) ORDER BY o.organizationName, o.organizationIdentify, o.organizationId"),

    // 指定した設備で作業可能な工程カンバンに割り当てられている組織ID一覧を取得する。 ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.findAssignedOrganization", query = "SELECT o.organizationId FROM OrganizationEntity o WHERE o.removeFlag = false AND o.organizationId IN (SELECT cwo.organizationId FROM ConWorkkanbanOrganizationEntity cwo WHERE cwo.workKanbanId IN (SELECT wk.workKanbanId FROM WorkKanbanEntity wk JOIN KanbanEntity ke ON wk.kanbanId = ke.kanbanId WHERE wk.skipFlag = false AND wk.implementFlag = true AND wk.workStatus IN :workStatuses AND ke.kanbanStatus IN :kanbanStatuses AND wk.workKanbanId IN (SELECT cwe.workKanbanId FROM ConWorkkanbanEquipmentEntity cwe WHERE cwe.equipmentId IN :equipmentIds)))"),
    // 組織IDを指定して、組織名を取得する。
    @NamedQuery(name = "OrganizationEntity.findNameById", query = "SELECT o.organizationName FROM OrganizationEntity o WHERE o.organizationId = :organizationId"),
    // 組織IDを指定して、組織識別名を取得する。
    @NamedQuery(name = "OrganizationEntity.findIdentifyById", query = "SELECT o.organizationIdentify FROM OrganizationEntity o WHERE o.organizationId = :organizationId"),

    @NamedQuery(name = "OrganizationEntity.findUnnecessary", query = "SELECT o FROM OrganizationEntity o WHERE o.updateDatetime < :updateDatetime AND o.removeFlag = false"),
})
public class OrganizationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "organization_id")
    private Long organizationId;// 組織ID

    @Basic(optional = false)
    //@NotNull
    @Size(max = 256)
    @Column(name = "organization_name")
    private String organizationName;// 組織名

    @Size(max = 256)
    @Column(name = "organization_identify")
    private String organizationIdentify;// 組織識別名

    @Enumerated(EnumType.STRING)
    @Column(name = "authority_type")
    private AuthorityEnum authorityType;// 権限

    @Column(name = "lang_Ids", length = 30000)
    private String langIds;// 言語

    @Size(max = 256)
    @Column(name = "pass_word")
    private String password;// パスワード

    @Size(max = 256)
    @Column(name = "mail_address")
    private String mailAddress;// メールアドレス

    @XmlElement(name = "updatePersonId")
    @Column(name = "update_person_id")
    private Long updatePersonId;// 更新者

    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;// 更新日時

    @Column(name = "remove_flag")
    private Boolean removeFlag = false;// 論理削除フラグ

    @Column(name = "parent_organization_id")
    @XmlElement(name = "parentId")
    private Long parentOrganizationId;// 親組織ID

    @Column(name = "organization_add_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String organizationAddInfo;// 追加情報(JSON)

    @Column(name = "service_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String serviceInfo;// サービス情報(JSON)

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    @Transient
    private Long childCount = 0L;// 子組織数

    @XmlElementWrapper(name = "breaktimes")
    @XmlElement(name = "breaktime")
    @Transient
    private List<Long> breaktimeCollection = null;// 休憩時間一覧

    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    @Transient
    private List<Long> roleCollection = null;// 役割一覧

    @XmlElementWrapper(name = "workCategories")
    @XmlElement(name = "workCategory")
    @Transient
    private List<Long> workCategoryCollection = null;// 作業区分一覧

    @XmlElementWrapper(name = "localeFileInfos")
    @XmlElement(name = "localeFileInfo")
    @Transient
    private List<LocaleFileEntity> localeFileInfos = null;// 言語ファイル

    @XmlElementWrapper(name = "interruptCategories")
    @XmlElement(name = "id")
    @Transient
    private List<Long> interruptCategoryCollection;
    
    @XmlElementWrapper(name = "delayCategories")
    @XmlElement(name = "id")
    @Transient
    private List<Long> delayCategoryCollection;

    @XmlElementWrapper(name = "callCategories")
    @XmlElement(name = "id")
    @Transient
    private List<Long> callCategoryCollection;
    
    @XmlElementWrapper(name = "interruptReasons")
    @XmlElement(name = "reason")
    @Transient
    private List<ReasonMasterEntity> interruptReasons;
    
    @XmlElementWrapper(name = "delayReasons")
    @XmlElement(name = "reason")
    @Transient
    private List<ReasonMasterEntity> delayReasons;

    @XmlElementWrapper(name = "callReasons")
    @XmlElement(name = "reason")
    @Transient
    private List<ReasonMasterEntity> callReasons;
        
    @XmlElementWrapper(name = "indirectWorks")
    @XmlElement(name = "indirectWork")
    @Transient
    private List<IndirectWorkEntity> indirectWorks;
    
    /**
     * コンストラクタ
     */
    public OrganizationEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param in 
     */
    public OrganizationEntity(OrganizationEntity in) {
        this.organizationName = in.organizationName;
        this.organizationIdentify = in.organizationIdentify;
        this.langIds = in.langIds;
        //this.password = in.password;
        this.authorityType = in.authorityType;
        this.mailAddress = in.mailAddress;
        this.updatePersonId = in.updatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.removeFlag = in.removeFlag;

        this.parentOrganizationId = in.parentOrganizationId;

        // 追加情報
        this.organizationAddInfo = in.organizationAddInfo;
        // サービス情報
        this.serviceInfo = in.serviceInfo;

        this.breaktimeCollection = new ArrayList<>();
        if (Objects.nonNull(in.getBreaktimeCollection())) {
            for (Long breaktime : in.getBreaktimeCollection()) {
                this.breaktimeCollection.add(breaktime);
            }
        }

        this.roleCollection = new ArrayList<>();
        if (Objects.nonNull(in.getRoleCollection())) {
            for (Long role : in.getRoleCollection()) {
                this.roleCollection.add(role);
            }
        }

        this.workCategoryCollection = new ArrayList<>();
        if (Objects.nonNull(in.getWorkCategoryCollection())) {
            for (Long workCategory : in.getWorkCategoryCollection()) {
                this.workCategoryCollection.add(workCategory);
            }
        }

        if (Objects.nonNull(in.getInterruptCategoryCollection())) {
            this.interruptCategoryCollection = new ArrayList<>(in.getInterruptCategoryCollection());
        }
        
        if (Objects.nonNull(in.getDelayCategoryCollection())) {
            this.delayCategoryCollection = new ArrayList<>(in.getDelayCategoryCollection());
        }

        if (Objects.nonNull(in.getCallCategoryCollection())) {
            this.callCategoryCollection = new ArrayList<>(in.getCallCategoryCollection());
        }
    }

    /**
     * コンストラクタ
     *
     * @param parentOrganizationId 親組織ID
     * @param organizationName 組織名
     * @param organizationIdentify 組織識別名
     * @param authorityType 権限
     * @param langIds 言語
     * @param password パスワード
     * @param mailAddress メールアドレス
     * @param updatePersonId 更新者(組織ID)
     * @param updateDatetime 更新日時
     */
    public OrganizationEntity(Long parentOrganizationId, String organizationName, String organizationIdentify, AuthorityEnum authorityType, String langIds, String password, String mailAddress, Long updatePersonId, Date updateDatetime) {
        this.parentOrganizationId = parentOrganizationId;
        this.organizationName = organizationName;
        this.organizationIdentify = organizationIdentify;
        this.authorityType = authorityType;
        this.langIds = langIds;
        this.password = password;
        this.mailAddress = mailAddress;
        this.updatePersonId = updatePersonId;
        this.updateDatetime = updateDatetime;
        this.removeFlag = false;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getOrganizationId() {
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 組織名を取得する。
     *
     * @return 組織名
     */
    public String getOrganizationName() {
        return this.organizationName;
    }

    /**
     * 組織名を設定する。
     *
     * @param organizationName 組織名
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * 組織識別名を取得する。
     *
     * @return 組織識別名
     */
    public String getOrganizationIdentify() {
        return this.organizationIdentify;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param organizationIdentify 組織識別名
     */
    public void setOrganizationIdentify(String organizationIdentify) {
        this.organizationIdentify = organizationIdentify;
    }

    /**
     * 権限を取得する。
     *
     * @return 権限
     */
    public AuthorityEnum getAuthorityType() {
        return this.authorityType;
    }

    /**
     * 権限を設定する。
     *
     * @param authorityType 権限
     */
    public void setAuthorityType(AuthorityEnum authorityType) {
        this.authorityType = authorityType;
    }

    /**
     * 言語を取得する。
     *
     * @return 言語
     */
    public String getLangIds() {
        return this.langIds;
    }

    /**
     * 言語を設定する。
     *
     * @param langIds 言語
     */
    public void setLangIds(String langIds) {
        this.langIds = langIds;
    }

    /**
     * パスワードを取得する。
     *
     * @return パスワード
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * パスワードを設定する。
     *
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * メールアドレスを取得する。
     *
     * @return メールアドレス
     */
    public String getMailAddress() {
        return this.mailAddress;
    }

    /**
     * メールアドレスを設定する。
     *
     * @param mailAddress メールアドレス
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    /**
     * 更新者を取得する。
     *
     * @return 更新者
     */
    public Long getUpdatePersonId() {
        return this.updatePersonId;
    }

    /**
     * 更新者を設定する。
     *
     * @param updatePersonId 更新者
     */
    public void setUpdatePersonId(Long updatePersonId) {
        this.updatePersonId = updatePersonId;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDatetime() {
        return this.updateDatetime;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDatetime 更新日時
     */
    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    /**
     * 論理削除フラグを取得する。
     *
     * @return 論理削除フラグ
     */
    public Boolean getRemoveFlag() {
        return this.removeFlag;
    }

    /**
     * 論理削除フラグを設定する。
     *
     * @param removeFlag 論理削除フラグ
     */
    public void setRemoveFlag(Boolean removeFlag) {
        this.removeFlag = removeFlag;
    }

    /**
     * 親組織IDを取得する。
     *
     * @return 親組織ID
     */
    public Long getParentOrganizationId() {
        return this.parentOrganizationId;
    }

    /**
     * 親組織IDを設定する。
     *
     * @param parentOrganizationId 親組織ID
     */
    public void setParentOrganizationId(Long parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
    }

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getOrganizationAddInfo() {
        return this.organizationAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param organizationAddInfo 追加情報(JSON)
     */
    public void setOrganizationAddInfo(String organizationAddInfo) {
        this.organizationAddInfo = organizationAddInfo;
    }

    /**
     * サービス情報を取得する。
     *
     * @return サービス情報
     */
    public String getServiceInfo() {
        return this.serviceInfo;
    }

    /**
     * サービス情報を設定する。
     *
     * @param serviceInfo サービス情報
     */
    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
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
     * 子組織数を取得する。
     *
     * @return 子組織数
     */
    public Long getChildCount() {
        return this.childCount;
    }

    /**
     * 子組織数を設定する。
     *
     * @param childCount 子組織数
     */
    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

    /**
     * 休憩時間一覧を取得する。
     *
     * @return 休憩時間一覧
     */
    public List<Long> getBreaktimeCollection() {
        return this.breaktimeCollection;
    }

    /**
     * 休憩時間一覧を設定する。
     *
     * @param breaktimeCollection 休憩時間一覧
     */
    public void setBreaktimeCollection(List<Long> breaktimeCollection) {
        this.breaktimeCollection = breaktimeCollection;
    }

    /**
     * 役割一覧を取得する。
     *
     * @return 役割一覧
     */
    public List<Long> getRoleCollection() {
        return this.roleCollection;
    }

    /**
     * 役割一覧を設定する。
     *
     * @param roleCollection 役割一覧
     */
    public void setRoleCollection(List<Long> roleCollection) {
        this.roleCollection = roleCollection;
    }

    /**
     * 作業区分一覧を取得する。
     *
     * @return 作業区分一覧
     */
    public List<Long> getWorkCategoryCollection() {
        return this.workCategoryCollection;
    }

    /**
     * 作業区分一覧を設定する。
     *
     * @param workCategoryCollection 作業区分一覧
     */
    public void setWorkCategoryCollection(List<Long> workCategoryCollection) {
        this.workCategoryCollection = workCategoryCollection;
    }

    /**
     * 言語ファイル情報を取得する。
     *
     * @return 言語ファイル情報
     */
    public List<LocaleFileEntity> getLocaleFileInfos() {
        return this.localeFileInfos;
    }

    /**
     * 言語ファイル情報を設定する。
     *
     * @param localeFileInfos 言語ファイル情報
     */
    public void setLocaleFileInfos(List<LocaleFileEntity> localeFileInfos) {
        this.localeFileInfos = localeFileInfos;
    }

    /**
     * 中断理由一覧を取得する。
     * 
     * @return 中断理由一覧
     */
    public List<Long> getInterruptCategoryCollection() {
        return interruptCategoryCollection;
    }

    /**
     * 中断理由一覧を設定する。
     * 
     * @param interruptCategoryCollection 中断理由一覧
     */
    public void setInterruptCategoryCollection(List<Long> interruptCategoryCollection) {
        this.interruptCategoryCollection = interruptCategoryCollection;
    }

    /**
     * 遅延理由一覧を取得する。
     * 
     * @return 遅延理由一覧
     */
    public List<Long> getDelayCategoryCollection() {
        return delayCategoryCollection;
    }

    /**
     * 遅延理由一覧を設定する。
     * 
     * @param delayReasonCollection 遅延理由一覧
     */
    public void setDelayCategoryCollection(List<Long> delayCategoryCollection) {
        this.delayCategoryCollection = delayCategoryCollection;
    }

    /**
     * 呼出理由一覧を取得する。
     * 
     * @return 呼出理由一覧
     */
    public List<Long> getCallCategoryCollection() {
        return callCategoryCollection;
    }

    /**
     * 呼出理由一覧を設定する。
     * 
     * @param callCategoryCollection 呼出理由一覧
     */
    public void setCallCategoryCollection(List<Long> callCategoryCollection) {
        this.callCategoryCollection = callCategoryCollection;
    }

    public List<ReasonMasterEntity> getInterruptReasons() {
        return interruptReasons;
    }

    public void setInterruptReasons(List<ReasonMasterEntity> interruptReasons) {
        this.interruptReasons = interruptReasons;
    }

    public List<ReasonMasterEntity> getDelayReasons() {
        return delayReasons;
    }

    public void setDelayReasons(List<ReasonMasterEntity> delayReasons) {
        this.delayReasons = delayReasons;
    }

    public List<ReasonMasterEntity> getCallReasons() {
        return callReasons;
    }

    public void setCallReasons(List<ReasonMasterEntity> callReasons) {
        this.callReasons = callReasons;
    }

    public List<IndirectWorkEntity> getIndirectWorks() {
        return indirectWorks;
    }

    public void setIndirectWorks(List<IndirectWorkEntity> indirectWorks) {
        this.indirectWorks = indirectWorks;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.organizationId != null ? this.organizationId.hashCode() : 0);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param object オブジェクト
     * @return true:同じである、false:異なる
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrganizationEntity)) {
            return false;
        }
        OrganizationEntity other = (OrganizationEntity) object;
        if ((this.organizationId == null && other.organizationId != null) || (this.organizationId != null && !this.organizationId.equals(other.organizationId))) {
            return false;
        }
        return true;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("OrganizationEntity{")
                .append("organizationId=").append(this.organizationId)
                .append(", organizationName=").append(this.organizationName)
                .append(", organizationIdentify=").append(this.organizationIdentify)
                .append(", authorityType=").append(this.authorityType)
                .append(", langIds=").append(this.langIds)
                .append(", password=").append(this.password)
                .append(", mailAddress=").append(this.mailAddress)
                .append(", updatePersonId=").append(this.updatePersonId)
                .append(", updateDatetime=").append(this.updateDatetime)
                .append(", removeFlag=").append(this.removeFlag)
                .append(", parentOrganizationId=").append(this.parentOrganizationId)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
