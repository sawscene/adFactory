/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.view;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 作業日報情報(VIEW)
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "view_work_report")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workReport")
@NamedNativeQueries({
        // 間接工数日報(カンバン, プロダクト兼用)
        @NamedNativeQuery(name = "WorkReportEntity.findIndirectWorkDaily", query = "SELECT 1 AS work_type, to_char(act.implement_datetime, 'yyyymmdd'::text) AS work_date, mo.organization_id, mo.organization_identify, mo.organization_name, act.indirect_actual_id, act.indirect_work_id AS work_id, wk.class_number, wk.work_number, wk.work_name, ''::character varying AS order_number, act.work_time AS work_time, '-1'::integer AS workflow_id, ''::character varying AS kanban_name, ''::character varying AS model_name, 0 AS actual_num, 3 AS work_type_order, COALESCE(act.production_number, ''::TEXT) AS production_number, ''::character varying AS class_key FROM (SELECT * FROM trn_indirect_actual tia WHERE to_char(tia.implement_datetime, 'yyyymmdd'::text) >= ?1 AND to_char(tia.implement_datetime, 'yyyymmdd'::text) <= ?2 ) act JOIN mst_organization mo ON mo.organization_id = act.organization_id JOIN mst_indirect_work wk ON wk.indirect_work_id = act.indirect_work_id ORDER BY wk.class_number, wk.work_number, wk.work_name, act.indirect_work_id;", resultClass = WorkReportEntity.class),

        @NamedNativeQuery(name = "WorkReportEntity.findIndirectWorkDaily2", query = "SELECT 1 AS work_type, to_char(act.implement_datetime, 'yyyymmdd'::text) AS work_date, mo.organization_id, mo.organization_identify, mo.organization_name, act.indirect_actual_id, act.indirect_work_id AS work_id, wk.class_number, wk.work_number, wk.work_name, ''::character varying AS order_number, act.work_time AS work_time, '-1'::integer AS workflow_id, ''::character varying AS kanban_name, ''::character varying AS model_name, 0 AS actual_num, 3 AS work_type_order, COALESCE(act.production_number, ''::TEXT) AS production_number, ''::character varying AS class_key FROM (SELECT * FROM trn_indirect_actual tia WHERE to_char(tia.implement_datetime, 'yyyymmdd'::text) >= ?1 AND to_char(tia.implement_datetime, 'yyyymmdd'::text) <= ?2 AND tia.organization_id = ANY(?3)) act JOIN mst_organization mo ON mo.organization_id = act.organization_id JOIN mst_indirect_work wk ON wk.indirect_work_id = act.indirect_work_id ORDER BY wk.class_number, wk.work_number, wk.work_name, act.indirect_work_id;", resultClass = WorkReportEntity.class),

        // 間接工数日報
        @NamedNativeQuery(name = "WorkReportEntity.findIndirectWorkDailyOrder", query = "SELECT 1 AS work_type, to_char(act.implement_datetime, 'yyyymmdd'::text) AS work_date, mo.organization_id, mo.organization_identify, mo.organization_name, act.indirect_actual_id, act.indirect_work_id AS work_id, wk.class_number, wk.work_number, wk.work_name, ''::character varying AS order_number, act.work_time AS work_time, '-1'::integer AS workflow_id, ''::character varying AS kanban_name, ''::character varying AS model_name, 0 AS actual_num, 3 AS work_type_order, COALESCE(act.production_number, ''::TEXT) AS production_number, ''::character varying AS class_key FROM (SELECT * FROM trn_indirect_actual tia WHERE to_char(tia.implement_datetime, 'yyyymmdd'::text) >= ?1 AND to_char(tia.implement_datetime, 'yyyymmdd'::text) <= ?2) act JOIN mst_organization mo ON mo.organization_id = act.organization_id JOIN mst_indirect_work wk ON wk.indirect_work_id = act.indirect_work_id ORDER BY wk.class_number, wk.work_number, wk.work_name, act.indirect_work_id;", resultClass = WorkReportEntity.class),
        // 間接工数日報
        @NamedNativeQuery(name = "WorkReportEntity.findIndirectWorkDailyOrder2", query = "SELECT 1 AS work_type, to_char(act.implement_datetime, 'yyyymmdd'::text) AS work_date, mo.organization_id, mo.organization_identify, mo.organization_name, act.indirect_actual_id, act.indirect_work_id AS work_id, wk.class_number, wk.work_number, wk.work_name, ''::character varying AS order_number, act.work_time AS work_time, '-1'::integer AS workflow_id, ''::character varying AS kanban_name, ''::character varying AS model_name, 0 AS actual_num, 3 AS work_type_order, COALESCE(act.production_number, ''::TEXT) AS production_number, ''::character varying AS class_key FROM (SELECT * FROM trn_indirect_actual tia WHERE to_char(tia.implement_datetime, 'yyyymmdd'::text) >= ?1 AND to_char(tia.implement_datetime, 'yyyymmdd'::text) <= ?2 AND tia.organization_id = ANY(?3)) act JOIN mst_organization mo ON mo.organization_id = act.organization_id JOIN mst_indirect_work wk ON wk.indirect_work_id = act.indirect_work_id ORDER BY wk.class_number, wk.work_number, wk.work_name, act.indirect_work_id;", resultClass = WorkReportEntity.class),

})
@NamedQueries({
    // 作業者の日報
    //      指定された作業者・日付の作業日報情報一覧を取得する。(作業日・作業者・工程順・注文番号(サブカンバン名)・工程で集計)
    @NamedQuery(name = "WorkReportEntity.findOrganizationIdDailyOrderNumber", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.view.WorkReportEntity(w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, w.orderNumber, SUM(w.workTime), w.workflowId, ' ', w.modelName, SUM(w.actualNum), w.workTypeOrder, ' ', ' ') FROM WorkReportEntity w WHERE w.organizationId = :organizationId AND w.workDate = :workDate AND w.workType IN :workTypes GROUP BY w.workTypeOrder, w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, w.orderNumber, w.workflowId, w.modelName ORDER BY w.workTypeOrder, w.workType, w.classNumber, w.workNumber, w.workName, w.orderNumber, w.modelName, w.workId, w.workflowId"),
    //      指定された作業者・日付の作業日報情報一覧を取得する。(作業日・作業者・工程順・カンバン名・工程で集計)
    @NamedQuery(name = "WorkReportEntity.findOrganizationIdDailyKanban", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.view.WorkReportEntity(w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, ' ', SUM(w.workTime), w.workflowId, w.kanbanName, w.modelName, SUM(w.actualNum), w.workTypeOrder, ' ', w.serialNumbers) FROM WorkReportEntity w WHERE w.organizationId = :organizationId AND w.workDate = :workDate AND w.workType IN :workTypes GROUP BY w.workTypeOrder, w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, w.workflowId, w.kanbanName, w.modelName, w.serialNumbers ORDER BY w.workTypeOrder, w.workType, w.classNumber, w.workNumber, w.workName, w.kanbanName, w.modelName, w.workId, w.workflowId"),
    //      指定された作業者・日付の作業日報情報一覧を取得する。(作業日・作業者・工程順・製造番号・工程で集計)
    @NamedQuery(name = "WorkReportEntity.findOrganizationIdDailyProduction", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.view.WorkReportEntity(w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, ' ', SUM(w.workTime), w.workflowId, ' ', w.modelName, SUM(w.actualNum), w.workTypeOrder, w.productionNumber, w.serialNumbers) FROM WorkReportEntity w WHERE w.organizationId = :organizationId AND w.workDate = :workDate AND w.workType IN :workTypes GROUP BY w.workTypeOrder, w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, w.productionNumber, w.workflowId, w.modelName, w.serialNumbers ORDER BY w.workTypeOrder, w.workType, w.classNumber, w.workNumber, w.workName, w.productionNumber, w.modelName, w.workId, w.workflowId"),

    // 指定期間の日報 (月報用)
    //      指定された期間の作業日報情報一覧を取得する。(作業日・作業者・工程順・注文番号(サブカンバン名)・工程で集計)
    @NamedQuery(name = "WorkReportEntity.findFromToDateOrderNumber", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.view.WorkReportEntity(w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, w.orderNumber, SUM(w.workTime), w.workflowId, ' ', w.modelName, SUM(w.actualNum), w.workTypeOrder, MIN(w.productionNumber), ' ') FROM WorkReportEntity w WHERE w.workDate >= :fromDate AND w.workDate <= :toDate AND w.workType IN :workTypes GROUP BY w.workTypeOrder, w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, w.orderNumber, w.workflowId, w.modelName ORDER BY w.workDate, w.organizationId, w.workTypeOrder, w.workType, w.classNumber, w.workNumber, w.workName, w.orderNumber, w.modelName, w.workId, w.workflowId"),
    //      指定された期間・作業者リストの作業日報情報一覧を取得する。(作業日・作業者・工程順・注文番号(サブカンバン名)・工程で集計)
    @NamedQuery(name = "WorkReportEntity.findFromToDateOrderNumber2", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.view.WorkReportEntity(w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, w.orderNumber, SUM(w.workTime), w.workflowId, ' ', w.modelName, SUM(w.actualNum), w.workTypeOrder, MIN(w.productionNumber), ' ') FROM WorkReportEntity w WHERE w.workDate >= :fromDate AND w.workDate <= :toDate AND w.organizationId IN :organizationIds AND w.workType IN :workTypes GROUP BY w.workTypeOrder, w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, w.orderNumber, w.workflowId, w.modelName ORDER BY w.workDate, w.organizationId, w.workTypeOrder, w.workType, w.classNumber, w.workNumber, w.workName, w.orderNumber, w.modelName, w.workId, w.workflowId"),
    //      指定された期間の作業日報情報一覧を取得する。(作業日・作業者・工程順・カンバン名・工程で集計)
    @NamedQuery(name = "WorkReportEntity.findFromToDateKanban", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.view.WorkReportEntity(w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, ' ', SUM(w.workTime), w.workflowId, w.kanbanName, w.modelName, SUM(w.actualNum), w.workTypeOrder, ' ', w.serialNumbers) FROM WorkReportEntity w WHERE w.workDate >= :fromDate AND w.workDate <= :toDate AND w.workType IN :workTypes GROUP BY w.workTypeOrder, w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, w.workflowId, w.kanbanName, w.modelName, w.serialNumbers ORDER BY w.workDate, w.organizationId, w.workTypeOrder, w.workType, w.classNumber, w.workNumber, w.workName, w.kanbanName, w.modelName, w.workId, w.workflowId"),
    //      指定された期間・作業者リストの作業日報情報一覧を取得する。(作業日・作業者・工程順・カンバン名・工程で集計)
    @NamedQuery(name = "WorkReportEntity.findFromToDateKanban2", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.view.WorkReportEntity(w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, ' ', SUM(w.workTime), w.workflowId, w.kanbanName, w.modelName, SUM(w.actualNum), w.workTypeOrder, ' ', w.serialNumbers) FROM WorkReportEntity w WHERE w.workDate >= :fromDate AND w.workDate <= :toDate AND w.organizationId IN :organizationIds AND w.workType IN :workTypes GROUP BY w.workTypeOrder, w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, w.workflowId, w.kanbanName, w.modelName, w.serialNumbers ORDER BY w.workDate, w.organizationId, w.workTypeOrder, w.workType, w.classNumber, w.workNumber, w.workName, w.kanbanName, w.modelName, w.workId, w.workflowId"),

    //      指定された期間の作業日報情報一覧を取得する。(作業日・作業者・工程順・製造番号・工程で集計)
    @NamedQuery(name = "WorkReportEntity.findFromToDateProduction", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.view.WorkReportEntity(w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, ' ', SUM(w.workTime), w.workflowId, ' ', w.modelName, SUM(w.actualNum), w.workTypeOrder, w.productionNumber, w.serialNumbers) FROM WorkReportEntity w WHERE w.workDate >= :fromDate AND w.workDate <= :toDate AND w.workType IN :workTypes GROUP BY w.workTypeOrder, w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, w.productionNumber, w.workflowId, w.modelName, w.serialNumbers ORDER BY w.workDate, w.organizationId, w.workTypeOrder, w.workType, w.classNumber, w.workNumber, w.workName, w.productionNumber, w.modelName, w.workId, w.workflowId"),
    //      指定された期間・作業者リストの作業日報情報一覧を取得する。(作業日・作業者・工程順・製造番号・工程で集計)
    @NamedQuery(name = "WorkReportEntity.findFromToDateProduction2", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.view.WorkReportEntity(w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, ' ', SUM(w.workTime), w.workflowId, ' ', w.modelName, SUM(w.actualNum), w.workTypeOrder, w.productionNumber, w.serialNumbers) FROM WorkReportEntity w WHERE w.workDate >= :fromDate AND w.workDate <= :toDate AND w.organizationId IN :organizationIds AND w.workType IN :workTypes GROUP BY w.workTypeOrder, w.workType, w.workDate, w.organizationId, w.organizationIdentify, w.organizationName, w.indirectActualId, w.workId, w.classNumber, w.workNumber, w.workName, w.productionNumber, w.workflowId, w.modelName, w.serialNumbers ORDER BY w.workDate, w.organizationId, w.workTypeOrder, w.workType, w.classNumber, w.workNumber, w.workName, w.productionNumber, w.modelName, w.workId, w.workflowId"),
})
public class WorkReportEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "work_type")
    @XmlElement()
    private Integer workType;// 作業種別

    @Id
    @Column(name = "work_date")
    @XmlElement()
    private String workDate;// 作業日

    @Id
    @Column(name = "organization_id")
    @XmlElement()
    private Long organizationId;// 組織ID

    @Size(max = 256)
    @Column(name = "organization_identify")
    @XmlElement()
    private String organizationIdentify;// 組織識別名

    @Size(max = 256)
    @Column(name = "organization_name")
    @XmlElement()
    private String organizationName;// 組織名

    @Column(name = "indirect_actual_id")
    @XmlElement()
    private Long indirectActualId;// 間接工数実績ID

    @Id
    @Column(name = "work_id")
    @XmlElement()
    private Long workId;// 作業ID

    @Size(max = 2147483647)
    @Column(name = "class_number")
    @XmlElement()
    private String classNumber;// 分類番号

    @Size(max = 64)
    @Column(name = "work_number")
    @XmlElement()
    private String workNumber;// 作業No

    @Size(max = 256)
    @Column(name = "work_name")
    @XmlElement()
    private String workName;// 作業内容

    @Id
    @Size(max = 2147483647)
    @Column(name = "order_number")
    @XmlElement()
    private String orderNumber;// 注文番号

    @Column(name = "work_time")
    @XmlElement()
    private Long workTime;// 工数(ms)

    @Id
    @Column(name = "workflow_id")
    @XmlElement()
    private Long workflowId;// 工程順ID

    @Id
    @Column(name = "kanban_name")
    @XmlElement()
    private String kanbanName;// カンバン名

    @Column(name = "model_name")
    @XmlElement()
    private String modelName;// モデル名

    @Column(name = "actual_num")
    @XmlElement()
    private Integer actualNum;// 実績数

    @Column(name = "work_type_order")
    @XmlElement()
    private Integer workTypeOrder;// 作業種別の順

    @Id
    @Column(name = "production_number")
    @XmlElement()
    private String productionNumber;// 製造番号

    @Column(name = "work_report_add_info")
    @XmlElement()
    private String workReprotAddInfo; // 追加情報

    @Column(name = "direct_actual_id")
    private Long directActualId;

    @Id
    @Column(name = "class_key")
    private String classKey; // 分類キー
    
    @Column(name = "serial_no")
    @XmlElement()
    private String serialNumbers;// シリアル番号

    /**
     * コンストラクタ
     */
    public WorkReportEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workType 作業種別 (0:直接作業, 1:間接作業, 2:中断時間)
     * @param workDate 作業日 ('yyyyMMdd')
     * @param organizationId 組織ID
     * @param organizationIdentify 組織識別名
     * @param organizationName 組織名
     * @param indirectActualId 間接工数実績ID
     * @param workId 作業ID
     * @param classNumber 分類番号
     * @param workNumber 作業No
     * @param workName 作業内容
     * @param orderNumber 注文番号
     * @param workTime 工数(ms)
     * @param workflowId 工程順ID
     * @param kanbanName カンバン名
     * @param modelName モデル名
     * @param actualNum 実績数
     * @param workTypeOrder 作業種別の順
     * @param productionNumber 製造番号
     * @param serialNumbers シリアル番号
     */
    public WorkReportEntity(Integer workType, String workDate, Long organizationId, String organizationIdentify, String organizationName, Long indirectActualId, Long workId, String classNumber, String workNumber, String workName, String orderNumber, Long workTime, Long workflowId, String kanbanName, String modelName, Long actualNum, Integer workTypeOrder, String productionNumber, String serialNumbers) {
        this.workType = workType;
        this.workDate = workDate;
        this.organizationId = organizationId;
        this.organizationIdentify = organizationIdentify;
        this.organizationName = organizationName;
        this.indirectActualId = indirectActualId;
        this.workId = workId;
        this.classNumber = classNumber;
        this.workNumber = workNumber;
        this.workName = workName;
        this.orderNumber = orderNumber;
        this.workTime = workTime;
        this.workflowId = workflowId;
        this.kanbanName = kanbanName;
        this.modelName = modelName;
        this.actualNum = Objects.nonNull(actualNum) ? actualNum.intValue() : 0;
        this.workTypeOrder = workTypeOrder;
        this.productionNumber = productionNumber;
        this.classKey = "";
        this.serialNumbers = serialNumbers;
    }

    /**
     * 作業種別を取得する。
     *
     * @return 作業種別 (0:直接作業、1:間接作業、2:中断時間、3:後戻り作業・赤作業)
     */
    public Integer getWorkType() {
        return this.workType;
    }

    /**
     * 作業種別を設定する。
     *
     * @param workType 作業種別 (0:直接作業, 1:間接作業, 2:中断時間、3:後戻り作業・赤作業)
     */
    public void setWorkType(Integer workType) {
        this.workType = workType;
    }

    /**
     * 作業日を取得する。
     *
     * @return 作業日 ('yyyyMMdd')
     */
    public String getWorkDate() {
        return this.workDate;
    }

    /**
     * 作業日を設定する。
     *
     * @param workDate 作業日 ('yyyyMMdd')
     */
    public void setWorkDate(String workDate) {
        this.workDate = workDate;
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
     * 間接工数実績IDを取得する。
     *
     * @return 間接工数実績ID
     */
    public Long getIndirectActualId() {
        return this.indirectActualId;
    }

    /**
     * 間接工数実績IDを設定する。
     *
     * @param indirectActualId 間接工数実績ID
     */
    public void setIndirectActualId(Long indirectActualId) {
        this.indirectActualId = indirectActualId;
    }

    /**
     * 作業IDを取得する。
     *
     * @return 作業ID
     */
    public Long getWorkId() {
        return this.workId;
    }

    /**
     * 作業IDを設定する。
     *
     * @param workId 作業ID
     */
    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    /**
     * 分類番号を取得する。
     *
     * @return 分類番号
     */
    public String getClassNumber() {
        return this.classNumber;
    }

    /**
     * 分類番号を設定する。
     *
     * @param classNumber 分類番号
     */
    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
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
     * 作業名を取得する。
     *
     * @return 作業名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 作業名を設定する。
     *
     * @param workName 作業名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * 注文番号を取得する。
     *
     * @return 注文番号
     */
    public String getOrderNumber() {
        return this.orderNumber;
    }

    /**
     * 注文番号を設定する。
     *
     * @param orderNumber 注文番号
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * 工数(ms)を取得する。
     *
     * @return 工数(ms)
     */
    public Long getWorkTime() {
        return this.workTime;
    }

    /**
     * 工数(ms)を設定する。
     *
     * @param workTime 工数(ms)
     */
    public void setWorkTime(Long workTime) {
        this.workTime = workTime;
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
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return modelName;
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
     * 実績数を取得する。
     *
     * @return 実績数
     */
    public Integer getActualNum() {
        return actualNum;
    }

    /**
     * 実績数を設定する。
     *
     * @param actualNum 実績数
     */
    public void setActualNum(Integer actualNum) {
        this.actualNum = actualNum;
    }

    /**
     * 作業種別の順を取得する。
     *
     * @return 作業種別の順
     */
    public Integer getWorkTypeOrder() {
        return this.workTypeOrder;
    }

    /**
     * 作業種別の順を設定する。
     *
     * @param workTypeOrder 作業種別の順
     */
    public void setWorkTypeOrder(Integer workTypeOrder) {
        this.workTypeOrder = workTypeOrder;
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getProductionNumber() {
        return this.productionNumber;
    }

    /**
     * 製造番号を設定する。
     *
     * @param productionNumber 製造番号
     */
    public void setProductionNumber(String productionNumber) {
        this.productionNumber = productionNumber;
    }

    /**
     * 追加情報を取得する
     * @return 追加情報
     */
    public String getWorkReprotAddInfo() {
        return workReprotAddInfo;
    }

    /**
     * 追加情報を設定する
     * @param workReprotAddInfo 追加情報
     */
    public void setWorkReprotAddInfo(String workReprotAddInfo) {
        this.workReprotAddInfo = workReprotAddInfo;
    }

    /**
     * 直接工数ID取得
     * @return 直接ID
     */
    public Long getDirectActualId() {
        return directActualId;
    }

    /**
     * 直接工数ID設定
     * @param directActualId 直接ID
     */
    public void setDirectActualId(Long directActualId) {
        this.directActualId = directActualId;
    }

    /**
     * 分類キーを取得する。
     * 
     * @return 分類キー 
     */
    public String getClassKey() {
        return classKey;
    }

    /**
     * 分類キーを設定する。
     * 
     * @param classKey 分類キー
     */
    public void setClassKey(String classKey) {
        this.classKey = classKey;
    }

    /**
     * シリアル番号を取得する。
     * 
     * @return シリアル番号
     */
    public String getSerialNumbers() {
        return serialNumbers;
    }

    /**
     * シリアル番号を設定する。
     * 
     * @param serialNumbers シリアル番号
     */
    public void setSerialNumbers(String serialNumbers) {
        this.serialNumbers = serialNumbers;
    }

    /**
     * ハッシュコードを取得する。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.workType);
        hash = 13 * hash + Objects.hashCode(this.workDate);
        hash = 13 * hash + Objects.hashCode(this.organizationId);
        hash = 13 * hash + Objects.hashCode(this.indirectActualId);
        hash = 13 * hash + Objects.hashCode(this.workId);
        hash = 13 * hash + Objects.hashCode(this.classNumber);
        hash = 13 * hash + Objects.hashCode(this.orderNumber);
        hash = 13 * hash + Objects.hashCode(this.workflowId);
        hash = 13 * hash + Objects.hashCode(this.kanbanName);
        hash = 13 * hash + Objects.hashCode(this.productionNumber);
        hash = 13 * hash + Objects.hashCode(this.classKey);
        hash = 13 * hash + Objects.hashCode(this.serialNumbers);
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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkReportEntity other = (WorkReportEntity) obj;
        if (!Objects.equals(this.workType, other.workType)) {
            return false;
        }
        if (!Objects.equals(this.workDate, other.workDate)) {
            return false;
        }
        if (!Objects.equals(this.organizationId, other.organizationId)) {
            return false;
        }
        if (!Objects.equals(this.indirectActualId, other.indirectActualId)) {
            return false;
        }
        if (!Objects.equals(this.workId, other.workId)) {
            return false;
        }
        if (!Objects.equals(this.classNumber, other.classNumber)) {
            return false;
        }
        if (!Objects.equals(this.orderNumber, other.orderNumber)) {
            return false;
        }
        if (!Objects.equals(this.workflowId, other.workflowId)) {
            return false;
        }
        if (!Objects.equals(this.kanbanName, other.kanbanName)) {
            return false;
        }
        if (!Objects.equals(this.productionNumber, other.productionNumber)) {
            return false;
        }
        if (!Objects.equals(this.classKey, other.classKey)) {
            return false;
        }
        return Objects.equals(this.serialNumbers, other.serialNumbers);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("WorkReportEntity{")
                .append("workType=").append(this.workType)
                .append(", workDate=").append(this.workDate)
                .append(", organizationId=").append(this.organizationId)
                .append(", organizationIdentify=").append(this.organizationIdentify)
                .append(", organizationName=").append(this.organizationName)
                .append(", indirectActualId=").append(this.indirectActualId)
                .append(", workId=").append(this.workId)
                .append(", classNumber=").append(this.classNumber)
                .append(", workNumber=").append(this.workNumber)
                .append(", workName=").append(this.workName)
                .append(", orderNumber=").append(this.orderNumber)
                .append(", workTime=").append(this.workTime)
                .append(", workflowId=").append(this.workflowId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", modelName=").append(this.modelName)
                .append(", actualNum=").append(this.actualNum)
                .append(", workTypeOrder=").append(this.workTypeOrder)
                .append(", productionNumber=").append(this.productionNumber)
                .append(", classKey=").append(this.classKey)
                .append(", serialNumbers=").append(this.serialNumbers)
                .append("}")
                .toString();
    }
}
