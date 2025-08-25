package jp.adtekfuji.adfactoryserver.entity.directwork;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adfactoryserver.entity.view.WorkReportEntity;
import jp.adtekfuji.adfactoryserver.utility.PgJsonbConverter;

@Entity
@Table(name = "trn_direct_actual")
@XmlRootElement(name = "directactual")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({

        @NamedNativeQuery(name="DirectActualEntity.findDirectWorkDailyKanban", query = "SELECT tda2.work_type AS work_type, act.work_date, mo.organization_id, mo.organization_identify, mo.organization_name, NULL ::bigint AS indirect_actual_id, tda2.work_id, NULL ::text AS class_number, mw.work_number, mw.work_name, act.work_time, COALESCE(tda2.order_number, '' ::character varying) AS order_number, tda2.workflow_id, tda2.kanban_name, tda2.model_name, act.comp_num AS actual_num, 1 AS work_type_order, COALESCE(tda2.product_number, '' ::TEXT) AS production_number, tda2.actual_add_info work_report_add_info, tda2.class_key, tda2.direct_actual_id FROM (SELECT to_char(tda.implement_datetime, 'yyyymmdd' ::text) AS work_date, sum(tda.work_time) work_time, sum(tda.actual_number) comp_num, min(tda.direct_actual_id) direct_actual_id FROM trn_direct_actual tda WHERE tda.remove_flag = FALSE AND (tda.work_type = 0 OR tda.work_type = 3) AND to_char(tda.implement_datetime, 'yyyymmdd' ::text) >= ?1 AND to_char(tda.implement_datetime, 'yyyymmdd' ::text) <= ?2 GROUP BY to_char(tda.implement_datetime, 'yyyymmdd' ::text), tda.work_type, tda.kanban_name, tda.workflow_id, tda.work_id, tda.organization_id, tda.class_key) act JOIN trn_direct_actual tda2 ON tda2.direct_actual_id = act.direct_actual_id LEFT JOIN mst_work mw ON mw.work_id = tda2.work_id LEFT JOIN mst_organization mo ON mo.organization_id = tda2.organization_id ORDER BY act.work_date, mo.organization_identify, mw.work_number, mw.work_name, tda2.work_id, tda2.kanban_name, tda2.work_type, tda2.class_key;", resultClass = WorkReportEntity.class),

        @NamedNativeQuery(name="DirectActualEntity.findDirectWorkDailyKanban2", query = "SELECT tda2.work_type AS work_type, act.work_date, mo.organization_id, mo.organization_identify, mo.organization_name, NULL ::bigint AS indirect_actual_id, tda2.work_id, NULL ::text AS class_number, mw.work_number, mw.work_name, act.work_time, COALESCE(tda2.order_number, '' ::character varying) AS order_number, tda2.workflow_id, tda2.kanban_name, tda2.model_name, act.comp_num AS actual_num, 1 AS work_type_order, COALESCE(tda2.product_number, '' ::TEXT) AS production_number, tda2.actual_add_info work_report_add_info, tda2.class_key, tda2.direct_actual_id FROM (SELECT to_char(tda.implement_datetime, 'yyyymmdd' ::text) AS work_date, sum(tda.work_time) work_time, sum(tda.actual_number) comp_num, min(tda.direct_actual_id) direct_actual_id FROM trn_direct_actual tda WHERE tda.remove_flag = FALSE AND (tda.work_type = 0 OR tda.work_type = 3) AND to_char(tda.implement_datetime, 'yyyymmdd' ::text) >= ?1 AND to_char(tda.implement_datetime, 'yyyymmdd' ::text) <= ?2 AND tda.organization_id = ANY (?3) GROUP BY to_char(tda.implement_datetime, 'yyyymmdd' ::text), tda.work_type, tda.kanban_name, tda.workflow_id, tda.work_id, tda.organization_id, tda.class_key) act JOIN trn_direct_actual tda2 ON tda2.direct_actual_id = act.direct_actual_id LEFT JOIN mst_work mw ON mw.work_id = tda2.work_id LEFT JOIN mst_organization mo ON mo.organization_id = tda2.organization_id ORDER BY mw.work_number, mw.work_name, tda2.kanban_name, tda2.work_id, tda2.class_key;", resultClass = WorkReportEntity.class),

        @NamedNativeQuery(name="DirectActualEntity.findDirectWorkDailyProduct", query = "SELECT 0 AS work_type, act.work_date, mo.organization_id, mo.organization_identify, mo.organization_name, NULL::bigint AS indirect_actual_id, act.work_id, NULL::text AS class_number, mw.work_number, mw.work_name, act.work_time, ' ' AS order_number, act.workflow_id, ' ' AS kanban_name, act.model_name, act.comp_num AS actual_num, 1 AS work_type_order, COALESCE(act.production_number, ''::TEXT) AS production_number FROM (SELECT to_char(tda.implement_datetime, 'yyyymmdd'::text) AS work_date, tda.model_name, tda.work_id, tda.workflow_id, tda.product_number production_number, tda.organization_id, sum(tda.work_time) work_time, sum(tda.actual_number) comp_num FROM trn_direct_actual tda WHERE tda.remove_flag = FALSE AND tda.work_type = 0 AND to_char(tda.implement_datetime, 'yyyymmdd'::text) >= ?1 AND to_char(tda.implement_datetime, 'yyyymmdd'::text) <= ?2 GROUP BY tda.model_name, tda.workflow_id, tda.work_id, tda.product_number, to_char(tda.implement_datetime, 'yyyymmdd'::text), tda.organization_id) act LEFT JOIN mst_work mw ON mw.work_id = act.work_id LEFT JOIN mst_organization mo ON mo.organization_id = act.organization_id ORDER BY mw.work_number, mw.work_name, act.production_number, act.model_name, act.work_id, workflow_id;", resultClass = WorkReportEntity.class),

        @NamedNativeQuery(name="DirectActualEntity.findDirectWorkDailyProduct2", query = "SELECT 0 AS work_type, act.work_date, mo.organization_id, mo.organization_identify, mo.organization_name, NULL::bigint AS indirect_actual_id, act.work_id, NULL::text AS class_number, mw.work_number, mw.work_name, act.work_time, ' ' AS order_number, act.workflow_id, ' ' AS kanban_name, act.model_name, act.comp_num AS actual_num, 1 AS work_type_order, COALESCE(act.production_number, ''::TEXT) AS production_number FROM (SELECT to_char(tda.implement_datetime, 'yyyymmdd'::text) AS work_date, tda.model_name, tda.work_id, tda.workflow_id, tda.product_number production_number, tda.organization_id, sum(tda.work_time) work_time, sum(tda.actual_number) comp_num FROM trn_direct_actual tda WHERE tda.remove_flag = FALSE AND tda.work_type = 0 AND to_char(tda.implement_datetime, 'yyyymmdd'::text) >= ?1 AND to_char(tda.implement_datetime, 'yyyymmdd'::text) <= ?2 AND tda.organization_id = ANY(?3) GROUP BY tda.model_name, tda.workflow_id, tda.work_id, tda.product_number, to_char(tda.implement_datetime, 'yyyymmdd'::text), tda.organization_id) act LEFT JOIN mst_work mw ON mw.work_id = act.work_id LEFT JOIN mst_organization mo ON mo.organization_id = act.organization_id ORDER BY mw.work_number, mw.work_name, act.production_number, act.model_name, act.work_id, workflow_id;", resultClass = WorkReportEntity.class),

        @NamedNativeQuery(name="DirectActualEntity.findDirectWorkDailyOrder", query = "SELECT 0 AS work_type, act.work_date, mo.organization_id, mo.organization_identify, mo.organization_name, NULL::bigint AS indirect_actual_id, act.work_id, NULL::text AS class_number, mw.work_number, mw.work_name, act.work_time, COALESCE(act.order_number, ''::character varying) AS order_number, act.workflow_id, ' ' AS kanban_name, act.model_name, act.comp_num AS actual_num, 1 AS work_type_order, ' ' AS production_number FROM (SELECT tda.model_name, tda.work_id, tda.workflow_id, tda.order_number, tda.organization_id, (to_char(tda.implement_datetime, 'yyyymmdd'::text)) work_date, sum(tda.work_time) work_time, sum(tda.actual_number) comp_num FROM trn_direct_actual tda WHERE tda.remove_flag = FALSE AND tda.work_type = 0 AND (to_char(tda.implement_datetime, 'yyyymmdd'::text)) >= ?1 AND (to_char(tda.implement_datetime, 'yyyymmdd'::text)) <= ?2 GROUP BY (to_char(tda.implement_datetime, 'yyyymmdd'::text)), tda.organization_id, tda.work_id, tda.order_number, tda.workflow_id, tda.model_name) act LEFT JOIN mst_work mw ON mw.work_id = act.work_id LEFT JOIN mst_organization mo ON mo.organization_id = act.organization_id ORDER BY act.work_date, act.organization_id, mw.work_number, mw.work_name, act.order_number, act.model_name, act.work_id, workflow_id;", resultClass = WorkReportEntity.class),

        @NamedNativeQuery(name="DirectActualEntity.findDirectWorkDailyOrder2", query = "SELECT 0 AS work_type, act.work_date, mo.organization_id, mo.organization_identify, mo.organization_name, NULL::bigint AS indirect_actual_id, act.work_id, NULL::text AS class_number, mw.work_number, mw.work_name, act.work_time, COALESCE(act.order_number, ''::character varying) AS order_number, act.workflow_id, ' ' AS kanban_name, act.model_name, act.comp_num AS actual_num, 1 AS work_type_order, ' ' AS production_number FROM (SELECT tda.model_name, tda.work_id, tda.workflow_id, tda.order_number, tda.organization_id, (to_char(tda.implement_datetime, 'yyyymmdd'::text)) work_date, sum(tda.work_time) work_time, sum(tda.actual_number) comp_num FROM trn_direct_actual tda WHERE tda.remove_flag = FALSE AND tda.work_type = 0 AND (to_char(tda.implement_datetime, 'yyyymmdd'::text)) >= ?1 AND (to_char(tda.implement_datetime, 'yyyymmdd'::text)) <= ?2 AND tda.organization_id = ANY(?3) GROUP BY (to_char(tda.implement_datetime, 'yyyymmdd'::text)), tda.organization_id, tda.work_id, tda.order_number, tda.workflow_id, tda.model_name) act LEFT JOIN mst_work mw ON mw.work_id = act.work_id LEFT JOIN mst_organization mo ON mo.organization_id = act.organization_id ORDER BY act.work_date, act.organization_id, mw.work_number, mw.work_name, act.order_number, act.model_name, act.work_id, workflow_id;", resultClass = WorkReportEntity.class),

        @NamedNativeQuery(name="DirectActualEntity.findInterruptDaily", query = "SELECT 2 AS work_type, act.work_date, mo.organization_id, mo.organization_identify, mo.organization_name, NULL::bigint AS indirect_actual_id, row_number() over () AS work_id, NULL::text AS class_number, ''::character varying AS work_number, act.work_name, ''::character varying AS order_number, act.work_time, 0 AS workflow_id, ''::character varying AS kanban_name, ''::character varying AS model_name, act.actual_num, 2 AS work_type_order, ''::character varying AS production_number FROM (SELECT to_char(tda.implement_datetime, 'yyyymmdd'::text) work_date, tda.work_name, tda.organization_id, sum(tda.work_time) work_time, count(*) actual_num FROM trn_direct_actual tda WHERE tda.remove_flag = FALSE AND tda.work_type = 2 AND to_char(tda.implement_datetime, 'yyyymmdd'::text) >= ?1 AND to_char(tda.implement_datetime, 'yyyymmdd'::text) <= ?2 GROUP BY tda.work_name, to_char(tda.implement_datetime, 'yyyymmdd'::text), tda.organization_id) act LEFT JOIN mst_organization mo ON mo.organization_id = act.organization_id ORDER BY act.work_name;", resultClass = WorkReportEntity.class),

        @NamedNativeQuery(name="DirectActualEntity.findInterruptDaily2", query = "SELECT 2 AS work_type, act.work_date, mo.organization_id, mo.organization_identify, mo.organization_name, NULL::bigint AS indirect_actual_id, row_number() over () AS work_id, NULL::text AS class_number, ''::character varying AS work_number, act.work_name, ''::character varying AS order_number, act.work_time, 0 AS workflow_id, ''::character varying AS kanban_name, ''::character varying AS model_name, act.actual_num, 2 AS work_type_order, ''::character varying AS production_number FROM (SELECT to_char(tda.implement_datetime, 'yyyymmdd'::text) work_date, tda.work_name, tda.organization_id, sum(tda.work_time) work_time, count(*) actual_num FROM trn_direct_actual tda WHERE tda.remove_flag = FALSE AND tda.work_type = 2 AND to_char(tda.implement_datetime, 'yyyymmdd'::text) >= ?1 AND to_char(tda.implement_datetime, 'yyyymmdd'::text) <= ?2 AND tda.organization_id = ANY(?3) GROUP BY tda.work_name, to_char(tda.implement_datetime, 'yyyymmdd'::text), tda.organization_id) act LEFT JOIN mst_organization mo ON mo.organization_id = act.organization_id ORDER BY act.work_name;", resultClass = WorkReportEntity.class),

        @NamedNativeQuery(name="DirectActualEntity.findInterruptDailyOrder", query = "SELECT 2 AS work_type, act.work_date AS work_date, mo.organization_id, mo.organization_identify, mo.organization_name, NULL::bigint AS indirect_actual_id, row_number() over () AS work_id, NULL::text AS class_number, ''::character varying AS work_number, act.work_name, ''::character varying AS order_number, act.work_time, 0 AS workflow_id, ''::character varying AS kanban_name, ''::character varying AS model_name, act.actual_num, 2 AS work_type_order, ''::character varying AS production_number FROM (SELECT tda.work_name, tda.organization_id, (to_char(tda.implement_datetime, 'yyyymmdd'::text)) work_date, sum(tda.work_time) work_time, count(*) actual_num FROM trn_direct_actual tda WHERE tda.remove_flag = FALSE AND tda.work_type = 2 AND (to_char(tda.implement_datetime, 'yyyymmdd'::text)) >= ?1 AND (to_char(tda.implement_datetime, 'yyyymmdd'::text)) <= ?2 GROUP BY (to_char(tda.implement_datetime, 'yyyymmdd'::text)), tda.work_name, tda.organization_id) act LEFT JOIN mst_organization mo ON mo.organization_id = act.organization_id ORDER BY act.work_date, act.organization_id, act.work_name;;", resultClass = WorkReportEntity.class),

        @NamedNativeQuery(name="DirectActualEntity.findInterruptDailyOrder2", query = "SELECT 2 AS work_type, act.work_date AS work_date, mo.organization_id, mo.organization_identify, mo.organization_name, NULL::bigint AS indirect_actual_id, row_number() over () AS work_id, NULL::text AS class_number, ''::character varying AS work_number, act.work_name, ''::character varying AS order_number, act.work_time, 0 AS workflow_id, ''::character varying AS kanban_name, ''::character varying AS model_name, act.actual_num, 2 AS work_type_order, ''::character varying AS production_number FROM (SELECT tda.work_name, tda.organization_id, (to_char(tda.implement_datetime, 'yyyymmdd'::text)) work_date, sum(tda.work_time) work_time, count(*) actual_num FROM trn_direct_actual tda WHERE tda.remove_flag = FALSE AND tda.work_type = 2 AND (to_char(tda.implement_datetime, 'yyyymmdd'::text)) >= ?1 AND (to_char(tda.implement_datetime, 'yyyymmdd'::text)) <= ?2 AND tda.organization_id = ANY (?3) GROUP BY (to_char(tda.implement_datetime, 'yyyymmdd'::text)), tda.work_name, tda.organization_id) act LEFT JOIN mst_organization mo ON mo.organization_id = act.organization_id ORDER BY act.work_date, act.organization_id, act.work_name;;", resultClass = WorkReportEntity.class),

        @NamedNativeQuery(name="DirectActualEntity.findSameDirectActual", query = "SELECT tda.* FROM trn_direct_actual tda WHERE tda.work_type = ?1 AND tda.organization_id = ?2 AND (to_char(tda.implement_datetime, 'yyyymmdd'::TEXT)) = ?3 AND tda.kanban_name = ?4 AND tda.order_number = ?5 AND tda.model_name = ?6 AND tda.workflow_id =?7 AND tda.work_id = ?8 AND tda.product_number = ?9 AND COALESCE(CAST(tda.actual_add_info AS TEXT), '') = ?10;", resultClass = DirectActualEntity.class),

        @NamedNativeQuery(name="DirectActualEntity.removeDirectActual", query = "DELETE FROM trn_direct_actual tda WHERE tda.work_type = ?1 AND (to_char(tda.implement_datetime, 'yyyymmdd'::TEXT)) = ?2 AND tda.kanban_name = ?3 AND tda.workflow_id = ?4 AND tda.work_id = ?5 AND tda.organization_id = ?6 AND tda.class_key = ?7;"),

})
public class DirectActualEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "direct_actual_id")
    private Long indirectActualId;// 間接工数実績ID

    //@NotNull
    @Column(name = "work_type")
    private Integer workType;// 作業種別

    //@NotNull
    @Column(name = "implement_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date implementDatetime;// 実施日時

    //@NotNull
    @Column(name = "organization_id")
    private Long organizationId;// 組織ID

    @Column(name = "work_id")
    private Long workId;// 作業ID

    @Size(max = 256)
    @Column(name = "work_name")
    private String workName;// 作業内容

    @Size(max = 2147483647)
    @Column(name = "order_number")
    private String orderNumber;// 注文番号

    @Column(name = "work_time")
    private Integer workTime;// 工数(ms)

    //@NotNull
    @Column(name = "workflow_id")
    private Long workflowId;// 工程順ID

    //@NotNull
    @Column(name = "kanban_name")
    private String kanbanName;// カンバン名

    @Column(name = "model_name")
    private String modelName;// モデル名

    @Column(name = "actual_number")
    private Integer actualNumber;// 実績数

    @Column(name = "work_type_order")
    private Integer workTypeOrder;// 作業種別の順

    @Column(name = "product_number")
    private String productionNum;// 製造番号

    //@NotNull
    @Column(name = "class_key")
    private String classKey; // 分類キー

    @Column(name = "actual_add_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String actualAddInfo;                   // 追加情報(JSON)

    @Column(name = "remove_flag")
    private Boolean removeFlag; // 論理削除

    //@NotNull
    @Column(name = "update_person_id")
    private Long updatePersonId;// 更新者(組織ID)

    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * コンストラクタ
     */
    public DirectActualEntity() {
    }

    /**
     * コンストラクタ
     * 
     * @param workType
     * @param implementDatetime
     * @param organizationId
     * @param workId
     * @param workName
     * @param orderNumber
     * @param workTime
     * @param workflowId
     * @param kanbanName
     * @param modelName
     * @param actualNumber
     * @param workTypeOrder
     * @param productionNum
     * @param classKey
     * @param updatePersonId 
     */
    public DirectActualEntity(Integer workType, Date implementDatetime, Long organizationId, Long workId, String workName, String orderNumber, Integer workTime, Long workflowId, String kanbanName, String modelName, Integer actualNumber, Integer workTypeOrder, String productionNum, String classKey, Long updatePersonId) {
        this.workType = workType;
        this.implementDatetime = implementDatetime;
        this.organizationId = organizationId;
        this.workId = workId;
        this.workName = workName;
        this.orderNumber = orderNumber;
        this.workTime = workTime;
        this.workflowId = workflowId;
        this.kanbanName = kanbanName;
        this.modelName = modelName;
        this.actualNumber = actualNumber;
        this.workTypeOrder = workTypeOrder;
        this.removeFlag = false;
        this.productionNum = productionNum;
        this.classKey = classKey;
        this.updatePersonId = updatePersonId;
    }

    /**
     * 間接工数実績IDを取得する
     * @return 間接工数実績ID
     */
    public Long getIndirectActualId() {
        return indirectActualId;
    }

    /**
     * 間接工数実績IDを設定する
     * @param indirectActualId 間接工数実績IDを設定
     */
    public void setIndirectActualId(Long indirectActualId) {
        this.indirectActualId = indirectActualId;
    }

    /**
     * 作業種別を取得
     * @return
     */
    public Integer getWorkType() {
        return workType;
    }

    /**
     * 作業種別を設定
     * @param workType 作業種別
     */
    public void setWorkType(Integer workType) {
        this.workType = workType;
    }

    /**
     * 実施日時を取得
     * @return 作業日時
     */
    public Date getImplementDatetime() {
        return implementDatetime;
    }

    /**
     * 実施日時を設定
     * @param implementDatetime 実績日時を設定
     */
    public void setImplementDatetime(Date implementDatetime) {
        this.implementDatetime = implementDatetime;
    }

    /**
     * 組織IDを取得
     * @return 組織ID
     */
    public Long getOrganizationId() {
        return organizationId;
    }

    /**
     * 組織IDを設定
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 作業IDを取得
     * @return 作業ID
     */
    public Long getWorkId() {
        return workId;
    }

    /**
     * 作業IDを設定
     * @param workId 作業ID
     */
    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    /**
     * 作業名を取得
     * @return 作業名
     */
    public String getWorkName() {
        return workName;
    }

    /**
     * 作業名を設定
     * @param workName 作業名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * 注文番号を取得
     * @return 注文番号
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * 注文番号を設定
     * @param orderNumber 注文番号
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * 工数を取得
     * @return 工数
     */
    public Integer getWorkTime() {
        return workTime;
    }

    /**
     * 工数を設定
     * @param workTime 工数
     */
    public void setWorkTime(Integer workTime) {
        this.workTime = workTime;
    }

    /**
     * 工程順IDを取得
     * @return 工程順ID
     */
    public Long getWorkflowId() {
        return workflowId;
    }

    /**
     * 工程順IDを設定
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * カンバン名取得
     * @return カンバン名
     */
    public String getKanbanName() {
        return kanbanName;
    }

    /**
     * カンバン名を設定
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    /**
     * モデル名を取得
     * @return モデル名
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * モデル名を設定
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 実績数を取得
     * @return 実績数
     */
    public Integer getActualNumber() {
        return actualNumber;
    }

    /**
     * 実績数を設定
     * @param actualNumber 実績数
     */
    public void setActualNumber(Integer actualNumber) {
        this.actualNumber = actualNumber;
    }

    /**
     * 作業種別の順を取得
     * @return 作業種別の順
     */
    public Integer getWorkTypeOrder() {
        return workTypeOrder;
    }

    /**
     * 作業種別の順を設定
     * @param workTypeOrder 作業種別の順
     */
    public void setWorkTypeOrder(Integer workTypeOrder) {
        this.workTypeOrder = workTypeOrder;
    }

    /**
     * 製造番号を取得
     * @return 製造番号
     */
    public String getProductionNum() {
        return productionNum;
    }

    /**
     * 製造番号を設定
     * @param productionNum 製造番号
     */
    public void setProductionNum(String productionNum) {
        this.productionNum = productionNum;
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
     * 追加情報を取得
     * @return 追加情報
     */
    public String getActualAddInfo() {
        return actualAddInfo;
    }

    /**
     * 追加情報を設定
     * @param actualAddInfo 追加情報
     */
    public void setActualAddInfo(String actualAddInfo) {
        this.actualAddInfo = actualAddInfo;
    }

    /**
     * 削除フラグを取得
     * @return 削除フラグ
     */
    public Boolean getRemoveFlag() {
        return removeFlag;
    }

    /**
     * 削除フラグを設定
     * @param removeFlag 削除フラグ
     */
    public void setRemoveFlag(Boolean removeFlag) {
        this.removeFlag = removeFlag;
    }

    /**
     * 更新者を取得する。
     * 
     * @return 更新者 
     */
    public Long getUpdatePersonId() {
        return updatePersonId;
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
     * 排他用バージョンを取得
     * @return 排他バージョンを取得
     */
    public Integer getVerInfo() {
        return verInfo;
    }

    /**
     * 排他バージョンを設定
     * @param verInfo 排他バージョン
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現 
     */
    @Override
    public String toString() {
        return "DirectActualEntity{" +
                "indirectActualId=" + indirectActualId +
                ", workType=" + workType +
                ", implementDatetime=" + implementDatetime +
                ", organizationId=" + organizationId +
                ", workId=" + workId +
                ", workName='" + workName + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", workTime=" + workTime +
                ", workflowId=" + workflowId +
                ", kanbanName='" + kanbanName + '\'' +
                ", modelName='" + modelName + '\'' +
                ", actualNumber=" + actualNumber +
                ", workTypeOrder=" + workTypeOrder +
                ", productionNum='" + productionNum + '\'' +
                ", classKey='" + classKey + '\'' +
                ", actualAddInfo='" + actualAddInfo + '\'' +
                ", removeFlag=" + removeFlag +
                ", updatePersonId=" + updatePersonId +
                ", verInfo=" + verInfo +
                '}';
    }
}
