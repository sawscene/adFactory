/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.work;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.utility.PgJsonbConverter;

/**
 * 工程情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_work")
@XmlRootElement(name = "work")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
    // 指定したプロパティ名・値の追加情報を持つ工程ID一覧を取得する。
    @NamedNativeQuery(name = "WorkEntity.findByWorkPropValue",
            query = "SELECT w.work_id FROM mst_work w WHERE EXISTS (SELECT 1 FROM jsonb_array_elements(w.work_add_info) props(prop) WHERE (prop->>'key') = ?1 AND (prop->>'val') = ?2)"),
        @NamedNativeQuery(name = "WorkEntity.findWorkList", query = "WITH RECURSIVE equipment_id AS (SELECT CAST(?1 AS BIGINT) eid UNION ALL SELECT me2.parent_equipment_id FROM equipment_id, mst_equipment me2 WHERE equipment_id.eid = me2.equipment_id), organization_id AS (SELECT CAST(?2 AS BIGINT) oid UNION ALL SELECT mo2.parent_organization_id FROM organization_id, mst_organization mo2 WHERE organization_id.oid = mo2.organization_id), hierarchy AS (SELECT CAST(0 AS BIGINT) as hierarchy_id UNION ALL SELECT mh1.hierarchy_id FROM mst_hierarchy mh1, hierarchy WHERE mh1.parent_hierarchy_id = hierarchy.hierarchy_id AND mh1.hierarchy_type = 0 AND (?2 = 1 OR NOT EXISTS(SELECT * FROM trn_access_hierarchy tah WHERE mh1.hierarchy_id = tah.hierarchy_id AND NOT EXISTS(SELECT * FROM trn_access_hierarchy tah2 WHERE tah.hierarchy_id = tah2.hierarchy_id AND tah2.organization_id IN (SELECT oi.oid FROM organization_id oi))))) SELECT DISTINCT(mw.*) FROM mst_work mw WHERE mw.remove_flag = FALSE AND EXISTS(SELECT * FROM con_workflow_work cww WHERE cww.work_id = mw.work_id AND cww.workflow_id = ?3 AND cww.skip_flag = FALSE) AND (?2 = 1 OR EXISTS(SELECT * FROM con_work_equipment cwe, equipment_id WHERE cwe.work_id = mw.work_id AND cwe.equipment_id = equipment_id.eid AND cwe.workflow_id = ?3)) AND EXISTS(SELECT * FROM con_work_organization cwo, organization_id WHERE cwo.work_id = mw.work_id AND cwo.organization_id = organization_id.oid AND cwo.workflow_id = ?3) AND EXISTS(SELECT * FROM con_hierarchy ch, hierarchy hi WHERE ch.hierarchy_id = hi.hierarchy_id AND ch.work_workflow_id = mw.work_id)", resultClass = WorkEntity.class),
        @NamedNativeQuery(name = "WorkEntity.findWorkListNonEq", query = "WITH RECURSIVE organization_id AS (SELECT CAST(?1 AS BIGINT) oid UNION ALL SELECT mo2.parent_organization_id FROM organization_id, mst_organization mo2 WHERE organization_id.oid = mo2.organization_id), hierarchy AS (SELECT CAST(0 AS BIGINT) as hierarchy_id UNION ALL SELECT mh1.hierarchy_id FROM mst_hierarchy mh1, hierarchy WHERE mh1.parent_hierarchy_id = hierarchy.hierarchy_id AND mh1.hierarchy_type = 0 AND (?1 = 1 OR NOT EXISTS(SELECT * FROM trn_access_hierarchy tah WHERE mh1.hierarchy_id = tah.hierarchy_id AND NOT EXISTS(SELECT * FROM trn_access_hierarchy tah2 WHERE tah.hierarchy_id = tah2.hierarchy_id AND tah2.organization_id IN (SELECT oi.oid FROM organization_id oi))))) SELECT DISTINCT(mw.*) FROM mst_work mw WHERE mw.remove_flag = FALSE AND EXISTS(SELECT * FROM con_workflow_work cww WHERE cww.work_id = mw.work_id AND cww.workflow_id = ?2 AND cww.skip_flag = FALSE) AND NOT EXISTS(SELECT * FROM con_work_equipment cwe WHERE cwe.work_id = mw.work_id AND cwe.workflow_id = ?2) AND EXISTS(SELECT * FROM con_work_organization cwo, organization_id WHERE cwo.work_id = mw.work_id AND cwo.organization_id = organization_id.oid AND cwo.workflow_id = ?2) AND EXISTS(SELECT * FROM con_hierarchy ch, hierarchy hi WHERE ch.hierarchy_id = hi.hierarchy_id AND ch.work_workflow_id = mw.work_id) ", resultClass = WorkEntity.class),
        @NamedNativeQuery(name = "WorkEntity.findAllLatestRevByName", query = "SELECT mw.* FROM mst_work mw WHERE mw.work_id IN (SELECT DISTINCT(FIRST_VALUE(mw2.work_id) OVER (PARTITION BY mw2.work_name ORDER BY mw2.work_rev DESC)) FROM mst_work mw2 WHERE  mw2.remove_flag = false AND mw2.approval_state = 4 AND mw2.work_name = ANY(?1));", resultClass = WorkEntity.class),
})
@NamedQueries({
    // 工程IDを指定して、工程順・工程関連付け情報の件数を取得する。
    @NamedQuery(name = "WorkEntity.countWorkflowWorkAssociation", query = "SELECT COUNT(c.workflowId) FROM ConWorkflowWorkEntity c WHERE c.workId = :workId"),
    // 工程IDを指定して、工程カンバン情報の件数を取得する。
    @NamedQuery(name = "WorkEntity.countKanbanAssociation", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanEntity w WHERE w.workId = :workId"),

    // 指定した工程名の最大の版数を取得 ※.削除済・未承認も対象
    @NamedQuery(name = "WorkEntity.findLatestRev", query = "SELECT MAX(w.workRev) FROM WorkEntity w WHERE w.workName = :workName"),
    // 指定した工程名の最大の版数を取得 ※.削除済は対象外、未承認は対象
    @NamedQuery(name = "WorkEntity.findLatestRevNotRemove", query = "SELECT MAX(w.workRev) FROM WorkEntity w WHERE w.workName = :workName AND w.removeFlag = false"),
    // 指定した工程名の最大の版数を取得 ※.削除済も対象、未承認は対象外
    @NamedQuery(name = "WorkEntity.findLatestRevApprove", query = "SELECT MAX(w.workRev) FROM WorkEntity w WHERE w.workName = :workName AND w.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE"),
    // 指定した工程名の最大の版数を取得 ※.削除済・未承認は対象外
    @NamedQuery(name = "WorkEntity.findLatestRevApproveNotRemove", query = "SELECT MAX(w.workRev) FROM WorkEntity w WHERE w.workName = :workName AND w.removeFlag = false AND w.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE"),

    // 工程名を指定して、最終承認済の最新版数の工程IDを取得する。※.削除済は対象外
    @NamedQuery(name = "WorkEntity.findWorkIdLatestRevByName", query = "SELECT w.workId FROM WorkEntity w WHERE w.workName = :workName AND w.workRev = (SELECT MAX(w2.workRev) FROM WorkEntity w2 WHERE w2.removeFlag = false AND w2.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE AND w2.workName = :workName)"),
    // 工程名を指定して、最終承認済の最新版数の工程を取得する。※.削除済は対象外
    @NamedQuery(name = "WorkEntity.findLatestRevByName", query = "SELECT w FROM WorkEntity w WHERE w.workName = :workName AND w.workRev = (SELECT MAX(w2.workRev) FROM WorkEntity w2 WHERE w2.removeFlag = false AND w2.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE AND w2.workName = :workName)"),

    // 工程名の重複確認 (追加時) ※.削除済も対象
    @NamedQuery(name = "WorkEntity.checkAddByWorkName", query = "SELECT COUNT(w.workId) FROM WorkEntity w WHERE w.workName = :workName AND w.workRev = :workRev"),
    // 工程名の重複確認 (更新時) ※.削除済も対象
    @NamedQuery(name = "WorkEntity.checkUpdateByWorkName", query = "SELECT COUNT(w.workId) FROM WorkEntity w WHERE w.workName = :workName AND w.workRev = :workRev AND w.workId != :workId"),

    // 工程情報をすべて取得する。(削除済は対象外)
    @NamedQuery(name = "WorkEntity.findAll", query = "SELECT w FROM WorkEntity w WHERE w.removeFlag = false"),
    // 工程名・版数を指定して、工程情報を取得する。(削除済は対象外)
    @NamedQuery(name = "WorkEntity.findByNameAndRev", query = "SELECT w FROM WorkEntity w WHERE w.workName = :workName AND w.workRev = :workRev AND w.removeFlag = false"),

    // 階層IDを指定して、階層に属する工程情報一覧を取得する。
    @NamedQuery(name = "WorkEntity.findByHierarchyId", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.work.WorkEntity(w, r.latestRev) FROM WorkEntity w, ConHierarchyEntity c, (SELECT wf.workName, MAX(CASE WHEN wf.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE THEN wf.workRev ELSE 0 END) latestRev FROM WorkEntity wf WHERE wf.removeFlag = false GROUP BY wf.workName) r WHERE c.hierarchyId = :hierarchyId AND c.workWorkflowId = w.workId AND r.workName = w.workName ORDER BY w.workName, w.workRev, w.workId"),
    // 階層IDを指定して、階層に属する工程情報一覧を取得する。(承認済のみ)
    @NamedQuery(name = "WorkEntity.findByHierarchyIdApprove", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.work.WorkEntity(w, r.latestRev) FROM WorkEntity w, ConHierarchyEntity c, (SELECT wf.workName, MAX(wf.workRev) latestRev FROM WorkEntity wf WHERE wf.removeFlag = false AND wf.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE GROUP BY wf.workName) r WHERE c.hierarchyId = :hierarchyId AND c.workWorkflowId = w.workId AND r.workName = w.workName AND w.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE ORDER BY w.workName, w.workRev, w.workId"),

    // 工程順IDを指定して、工程順に属する工程の件数を取得する。 ※.削除済も対象
    @NamedQuery(name = "WorkEntity.countByWorkflowId", query = "SELECT COUNT(w.workId) FROM ConWorkflowWorkEntity c JOIN WorkEntity w ON c.workId = w.workId WHERE c.workflowId = :workflowId"),
    // 工程順IDを指定して、工程順に属する工程情報一覧を取得する。 ※.削除済も対象
    @NamedQuery(name = "WorkEntity.findByWorkflowId", query = "SELECT w FROM ConWorkflowWorkEntity c JOIN WorkEntity w ON c.workId = w.workId WHERE c.workflowId = :workflowId"),
    // 工程IDで工程名を取得する ※.削除済も対象
    @NamedQuery(name = "WorkEntity.findWorkName", query = "SELECT w.workName FROM WorkEntity w WHERE w.workId = :workId"),
    // 工程IDを指定して、工程名・コンテンツ・コンテンツ種別を取得する。 ※.削除済も対象
    @NamedQuery(name = "WorkEntity.findNameAndContent", query = "SELECT w.workName, w.content, w.contentType FROM WorkEntity w WHERE w.workId = :workId"),

    // 工程IDを指定して、追加情報を取得する。
    @NamedQuery(name = "WorkEntity.findAddInfoByWorkId", query = "SELECT w.workAddInfo FROM WorkEntity w WHERE w.workId = :workId"),

    // 申請IDを指定して、工程情報を取得する。
    @NamedQuery(name = "WorkEntity.findByApprovalId", query = "SELECT w FROM WorkEntity w WHERE w.approvalId = :approvalId"),
    // 工程ID群を指定して工程を取得する。
    @NamedQuery(name = "WorkEntity.findByWorkIds", query = "SELECT w FROM WorkEntity w WHERE w.workId IN :workIds"),
    // 工程名から工程情報を取得する
    @NamedQuery(name = "WorkEntity.findByName", query = "SELECT w FROM WorkEntity w WHERE w.workName = :workName"),
    // 工程階層から工程情報を取得する
    @NamedQuery(name = "WorkEntity.findLatestOnlyByHierarchyIdAndName", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.work.WorkEntity(w, c.hierarchyId, c.hierarchyId) FROM WorkEntity w, ConHierarchyEntity c WHERE c.hierarchyId IN :hierarchyIds AND c.workWorkflowId = w.workId AND LOWER(w.workName) LIKE :workName AND w.workRev = (SELECT MAX(w2.workRev) FROM WorkEntity w2 WHERE w2.removeFlag = false AND w2.workName = w.workName) ORDER BY w.workName, w.workId"),
    @NamedQuery(name = "WorkEntity.findByHierarchyIdAndName", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.work.WorkEntity(w, c.hierarchyId, c.hierarchyId) FROM WorkEntity w, ConHierarchyEntity c WHERE c.hierarchyId IN :hierarchyIds AND c.workWorkflowId = w.workId AND LOWER(w.workName) LIKE :workName ORDER BY w.workName, w.workId"),
    //
    @NamedQuery(name = "WorkEntity.findByNameAndWorkflowId", query = "SELECT w FROM ConWorkflowWorkEntity c JOIN WorkEntity w ON c.workId = w.workId WHERE c.workflowId = :workflowId AND w.workName = :workName"),
})
public class WorkEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "work_id")
    private Long workId;// 工程ID

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "work_name")
    private String workName;// 工程名

    @Column(name = "takt_time")
    private Integer taktTime;// タクトタイム[ms]

    @Size(max = 2147483647)
    @Column(name = "content")
    private String content;// コンテンツ

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentTypeEnum contentType;// コンテンツタイプ

    @Column(name = "update_person_id")
    private Long updatePersonId;// 更新者(組織ID)

    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;// 更新日時

    @XmlTransient
    @Column(name = "remove_flag")
    private Boolean removeFlag = false;// 論理削除フラグ

    @Size(max = 128)
    @Column(name = "font_color")
    private String fontColor;// 文字色

    @Size(max = 128)
    @Column(name = "back_color")
    private String backColor;// 背景色

    @Size(max = 2147483647)
    @Column(name = "use_parts")
    private String useParts;// 使用部品

    @Size(max = 64)
    @Column(name = "work_number")
    private String workNumber;// 作業番号

    @Column(name = "work_check_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String workCheckInfo;// 検査情報(JSON)

    @Column(name = "work_add_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String workAddInfo;// 追加情報(JSON)

    @Column(name = "service_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String serviceInfo;// サービス情報(JSON)

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション
    
    @Column(name = "display_items", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String displayItems;// 表示項目(JSON)

    /**
     * 版数
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_rev")
    private Integer workRev;

    /**
     * 申請ID
     */
    @Column(name = "approval_id")
    private Long approvalId;

    /**
     * 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    @Column(name = "approval_state")
    private ApprovalStatusEnum approvalState;

    @XmlElementWrapper(name = "workSections")
    @XmlElement(name = "workSection")
    @Transient
    private List<WorkSectionEntity> workSectionCollection;// 工程セクション一覧

    @XmlElementWrapper(name = "devices")
    @XmlElement(name = "device")
    @Transient
    private List<EquipmentEntity> deviceCollection;// デバイス一覧

    @Transient
    private Long parentId;// 親階層ID

    @Transient
    private Integer latestRev;// 最新版数

    /**
     * 申請情報
     */
    @Transient
    private ApprovalEntity approval;

    /**
     * コンストラクタ
     */
    public WorkEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param in 工程情報
     */
    public WorkEntity(WorkEntity in) {
        this.workName = in.workName;
        this.workRev = in.workRev;
        this.taktTime = in.taktTime;
        this.content = in.content;
        this.contentType = in.contentType;
        this.updatePersonId = in.updatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.removeFlag = in.removeFlag;
        this.fontColor = in.fontColor;
        this.backColor = in.backColor;
        this.workSectionCollection = new ArrayList<>();
        for (WorkSectionEntity section : in.getWorkSectionCollection()) {
            this.workSectionCollection.add(new WorkSectionEntity(section));
        }
        this.useParts = in.useParts;
        this.workNumber = in.workNumber;

        // 検査情報
        this.workCheckInfo = in.workCheckInfo;
        // 追加情報
        this.workAddInfo = in.workAddInfo;
        // サービス情報
        this.serviceInfo = in.serviceInfo;
        // 表示項目
        this.displayItems = in.displayItems;

        this.parentId = in.parentId;
        this.latestRev = in.latestRev;
    }

    /**
     * コンストラクタ
     * ※.NamedQuery (WorkEntity.findByFkWorkHierarchyId) で使用。
     *
     * @param in 工程情報
     * @param latestRev 最新版数
     */
    public WorkEntity(WorkEntity in, Integer latestRev) {
        this.workId = in.workId;
        this.workName = in.workName;
        this.workRev = in.workRev;
        this.taktTime = in.taktTime;
        this.content = in.content;
        this.contentType = in.contentType;
        this.updatePersonId = in.updatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.removeFlag = in.removeFlag;
        this.fontColor = in.fontColor;
        this.backColor = in.backColor;
        this.useParts = in.useParts;
        this.workNumber = in.workNumber;
        this.workCheckInfo = in.workCheckInfo;
        this.workAddInfo = in.workAddInfo;
        this.serviceInfo = in.serviceInfo;
        this.verInfo = in.verInfo;
        this.displayItems = in.displayItems;
        this.approvalId = in.approvalId;
        this.approvalState = in.approvalState;

        this.latestRev = latestRev;
    }

    /**
     * コンストラクタ
     * 
     * @param in 工程情報
     * @param parentId 階層ID
     * @param p1 ダミー
     */
    public WorkEntity(WorkEntity in, Long parentId, Long p1) {
        this.workId = in.workId;
        this.workName = in.workName;
        this.workRev = in.workRev;
        this.taktTime = in.taktTime;
        this.content = in.content;
        this.contentType = in.contentType;
        this.updatePersonId = in.updatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.removeFlag = in.removeFlag;
        this.fontColor = in.fontColor;
        this.backColor = in.backColor;
        this.useParts = in.useParts;
        this.workNumber = in.workNumber;
        this.workCheckInfo = in.workCheckInfo;
        this.workAddInfo = in.workAddInfo;
        this.serviceInfo = in.serviceInfo;
        this.verInfo = in.verInfo;
        this.displayItems = in.displayItems;
        this.approvalId = in.approvalId;
        this.approvalState = in.approvalState;

        this.parentId = parentId;
    }

    /**
     * コンストラクタ
     *
     * @param parentId 親階層ID
     * @param workName 工程名
     * @param workRev 版数
     * @param taktTime タクトタイム[ms]
     * @param content コンテンツ
     * @param contentType コンテンツタイプ
     * @param updatePersonId 更新者(組織ID)
     * @param updateDatetime 更新日時
     * @param fontColor 文字色
     * @param backColor 背景色
     */
    public WorkEntity(Long parentId, String workName, Integer workRev, Integer taktTime, String content, ContentTypeEnum contentType, Long updatePersonId, Date updateDatetime, String fontColor, String backColor) {
        this.parentId = parentId;
        this.workName = workName;
        this.workRev = workRev;
        this.taktTime = taktTime;
        this.content = content;
        this.contentType = contentType;
        this.updatePersonId = updatePersonId;
        this.updateDatetime = updateDatetime;
        this.removeFlag = false;
        this.fontColor = fontColor;
        this.backColor = backColor;
    }

    /**
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public Long getWorkId() {
        return this.workId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param workId 工程ID
     */
    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 工程名を設定する。
     *
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * タクトタイム[ms]を取得する。
     *
     * @return タクトタイム[ms]
     */
    public Integer getTaktTime() {
        return this.taktTime;
    }

    /**
     * タクトタイム[ms]を設定する。
     *
     * @param taktTime タクトタイム[ms]
     */
    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    /**
     * コンテンツを取得する。
     *
     * @return コンテンツ
     */
    public String getContent() {
        return this.content;
    }

    /**
     * コンテンツを設定する。
     *
     * @param content コンテンツ
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * コンテンツタイプを取得する。
     *
     * @return コンテンツタイプ
     */
    public ContentTypeEnum getContentType() {
        return this.contentType;
    }

    /**
     * コンテンツタイプを設定する。
     *
     * @param contentType コンテンツタイプ
     */
    public void setContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
    }

    /**
     * 更新者(組織ID)を取得する。
     *
     * @return 更新者(組織ID)
     */
    public Long getUpdatePersonId() {
        return this.updatePersonId;
    }

    /**
     * 更新者(組織ID)を設定する。
     *
     * @param updatePersonId 更新者(組織ID)
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
     * 文字色を取得する。
     *
     * @return 文字色
     */
    public String getFontColor() {
        return this.fontColor;
    }

    /**
     * 文字色を設定する。
     *
     * @param fontColor 文字色
     */
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * 背景色を取得する。
     *
     * @return 背景色
     */
    public String getBackColor() {
        return this.backColor;
    }

    /**
     * 背景色を設定する。
     *
     * @param backColor 背景色
     */
    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    /**
     * 使用部品を取得する。
     *
     * @return 使用部品
     */
    public String getUseParts() {
        return this.useParts;
    }

    /**
     * 使用部品を設定する。
     *
     * @param useParts 使用部品
     */
    public void setUseParts(String useParts) {
        this.useParts = useParts;
    }

    /**
     * 作業番号を取得する。
     *
     * @return 作業番号
     */
    public String getWorkNumber() {
        return this.workNumber;
    }

    /**
     * 作業番号を設定する。
     *
     * @param workNumber 作業番号
     */
    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    /**
     * 工程セクション一覧を取得する。
     *
     * @return 工程セクション一覧
     */
    public List<WorkSectionEntity> getWorkSectionCollection() {
        return workSectionCollection;
    }

    /**
     * 工程セクション一覧を設定する。
     *
     * @param workSectionCollection 工程セクション一覧
     */
    public void setWorkSectionCollection(List<WorkSectionEntity> workSectionCollection) {
        this.workSectionCollection = workSectionCollection;
    }

    /**
     * デバイス一覧を取得する。
     *
     * @return デバイス一覧
     */
    public List<EquipmentEntity> getDeviceCollection() {
        return deviceCollection;
    }

    /**
     * デバイス一覧を設定する。
     *
     * @param deviceCollection デバイス一覧
     */
    public void setDeviceCollection(List<EquipmentEntity> deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    /**
     * 検査情報(JSON)を取得する。
     *
     * @return 検査情報(JSON)
     */
    public String getWorkCheckInfo() {
        return this.workCheckInfo;
    }

    /**
     * 検査情報(JSON)を設定する。
     *
     * @param workCheckInfo 検査情報(JSON)
     */
    public void setWorkCheckInfo(String workCheckInfo) {
        this.workCheckInfo = workCheckInfo;
    }

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getWorkAddInfo() {
        return this.workAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param workAddInfo 追加情報(JSON)
     */
    public void setWorkAddInfo(String workAddInfo) {
        this.workAddInfo = workAddInfo;
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
     * 表示項目(JSON)を取得する。
     *
     * @return 表示項目(JSON)
     */
    public String getDisplayItems() {
        return this.displayItems;
    }

    /**
     * 表示項目(JSON)を設定する。
     *
     * @param displayItems 表示項目(JSON)
     */
    public void setDisplayItems(String displayItems) {
        this.displayItems = displayItems;
    }

    /**
     * 版数を取得する。
     *
     * @return 版数
     */
    public Integer getWorkRev() {
        return this.workRev;
    }

    /**
     * 版数を設定する。
     *
     * @param workRev 版数
     */
    public void setWorkRev(Integer workRev) {
        this.workRev = workRev;
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
     * 親階層IDを取得する。
     *
     * @return 親階層ID
     */
    public Long getParentId() {
        return this.parentId;
    }

    /**
     * 親階層IDを設定する。
     *
     * @param parentId 親階層ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 最新版数を取得する。
     *
     * @return 最新版数
     */
    public Integer getLatestRev() {
        return latestRev;
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
     * 申請情報を取得する。
     *
     * @return 申請情報
     */
    public ApprovalEntity getApproval() {
        return this.approval;
    }

    /**
     * 申請情報を設定する。
     *
     * @param approval 申請情報
     */
    public void setApproval(ApprovalEntity approval) {
        this.approval = approval;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workId != null ? workId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkEntity)) {
            return false;
        }
        WorkEntity other = (WorkEntity) object;
        return !((this.workId == null && other.workId != null) || (this.workId != null && !this.workId.equals(other.workId)));
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkEntity{")
                .append("workId=").append(this.workId)
                .append(", workName=").append(this.workName)
                .append(", workRev=").append(this.workRev)
                .append(", taktTime=").append(this.taktTime)
                .append(", content=").append(this.content)
                .append(", contentType=").append(this.contentType)
                .append(", updatePersonId=").append(this.updatePersonId)
                .append(", updateDatetime=").append(this.updateDatetime)
                .append(", removeFlag=").append(this.removeFlag)
                .append(", fontColor=").append(this.fontColor)
                .append(", backColor=").append(this.backColor)
                .append(", useParts=").append(this.useParts)
                .append(", workNumber=").append(this.workNumber)
                .append(", approvalId=").append(this.approvalId)
                .append(", approvalState=").append(this.approvalState)
                .append(", parentId=").append(this.parentId)
                .append(", latestRev=").append(this.latestRev)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }

    public WorkEntity clone() {
        WorkEntity ret = new WorkEntity();
        ret.workId = this.workId;
        ret.workName = this.workName;
        ret.taktTime = this.taktTime;
        ret.content = this.content;
        ret.contentType = this.contentType;
        ret.updatePersonId = this.updatePersonId;
        ret.updateDatetime = this.updateDatetime;
        ret.removeFlag = this.removeFlag;
        ret.fontColor = this.fontColor;
        ret.backColor = this.backColor;
        ret.useParts = this.useParts;
        ret.workNumber = this.workNumber;
        ret.workCheckInfo = this.workCheckInfo;
        ret.workAddInfo = this.workAddInfo;
        ret.serviceInfo = this.serviceInfo;
        ret.verInfo = this.verInfo;
        ret.displayItems = this.displayItems;
        ret.workRev = this.workRev;
        ret.approvalId = this.approvalId;
        ret.approvalState = this.approvalState;
        ret.workSectionCollection = Objects.isNull(this.workSectionCollection) ? null : new ArrayList<>(this.workSectionCollection);
        ret.deviceCollection = Objects.isNull(this.deviceCollection) ? null : new ArrayList<>(this.deviceCollection);
        ret.parentId = this.parentId;
        ret.latestRev = this.latestRev;
        ret.approval = this.approval;
        return ret;
    }
}
