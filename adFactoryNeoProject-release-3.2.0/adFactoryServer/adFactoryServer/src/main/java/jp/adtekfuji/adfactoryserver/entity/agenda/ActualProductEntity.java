package jp.adtekfuji.adfactoryserver.entity.agenda;


import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;


@Entity
@XmlRootElement(name = "ActualProduct")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
        @NamedNativeQuery(name = "ActualProductEntity.find",
            query = "SELECT\n" +
                "    k2.kanban_id kanban_id\n" +
                "    , tk2.model_name model_name\n" +
                "    , k2.production_number production_number\n" +
                "    , k2.start_datetime start_datetime\n" +
                "    , k2.comp_datetime comp_datetime\n" +
                "    , k2.actual_start_datetime actual_start_datetime\n" +
                "    , k2.actual_comp_datetime actual_comp_datetime\n" +
                "    , k2.kanban_status kanban_status\n" +
                "    , k2.work_num work_num\n" +
                "    , k2.comp_work_num comp_work_num\n" +
                "    , tk2.kanban_add_info kanban_add_info\n" +
                "    , tar.interrupt_reason interrupt_reason\n" +
                "    , mr.font_color font_color\n" +
                "    , mr.back_color back_color\n" +
                "    , mr.light_pattern light_pattern\n" +
                "    , tar2.defect_reason defect_reason\n" +
                "    , k2.comp_num comp_num\n" +
                "    , k2.lot_quantity lot_quantity\n" +
                "    , k2.cycle_time cycle_time\n" +
                "FROM\n" +
                "    ( \n" +
                "        SELECT\n" +
                "            k.production_number\n" +
                "            , min(k.start_datetime) start_datetime\n" +
                "            , max(k.comp_datetime) comp_datetime\n" +
                "            , CASE min( \n" +
                "                    CASE \n" +
                "                        WHEN twk.work_status = 'DEFECT'  THEN 0 \n" +
                "                        WHEN twk.work_status = 'WORKING' AND ?2 > twk.comp_datetime THEN 1 \n" +
                "                        WHEN twk.work_status = 'WORKING' AND twk.start_datetime < twk.actual_start_datetime THEN 2 \n" +
                "                        WHEN twk.work_status = 'WORKING' THEN 3 \n" +
                "                        WHEN twk.work_status = 'SUSPEND' THEN 4 \n" +
                "                        WHEN twk.work_status = 'COMPLETION' AND twk.comp_datetime < twk.actual_comp_datetime THEN 5 \n" +
                "                        WHEN twk.work_status = 'COMPLETION' THEN 6 \n" +
                "                        WHEN twk.work_status = 'PLANNED' AND ?2 > twk.start_datetime THEN 7 \n" +
                "                        ELSE 8 \n" +
                "                        END\n" +
                "                ) \n" +
                "                WHEN 0 THEN 'DEFECT' \n" +
                "                WHEN 1 THEN 'WORK_DELAYCOMP' \n" +
                "                WHEN 2 THEN 'WORK_DELAYSTART' \n" +
                "                WHEN 3 THEN 'WORK_NORMAL' \n" +
                "                WHEN 4 THEN 'SUSPEND_NORMAL' \n" +
                "                WHEN 5 THEN 'COMP_DELAYCOMP' \n" +
                "                WHEN 6 THEN 'COMP_NORMAL' \n" +
                "                WHEN 7 THEN 'PLAN_DELAYSTART' \n" +
                "                ELSE 'PLAN_NORMAL' \n" +
                "                END kanban_status\n" +
                "            , count(twk.kanban_id) work_num\n" +
                "            , sum( \n" +
                "                CASE \n" +
                "                    WHEN twk.work_status = 'COMPLETION' THEN 1 \n" +
                "                    ELSE 0 \n" +
                "                    END\n" +
                "            ) comp_work_num\n" +
                "            , max(k.kanban_id) kanban_id\n" +
                "            , max( \n" +
                "                CASE \n" +
                "                    WHEN twk.work_status = 'SUSPEND' THEN twk.last_actual_id \n" +
                "                    ELSE null \n" +
                "                    END\n" +
                "            ) suspend_last_actual_id\n" +
                "            , max( \n" +
                "                CASE \n" +
                "                    WHEN twk.work_status = 'DEFECT' THEN twk.last_actual_id \n" +
                "                    ELSE null \n" +
                "                    END\n" +
                "            ) defect_last_actual_id\n" +
                "            , min(k.actual_start_datetime) actual_start_datetime\n" +
                "            , max(k.actual_comp_datetime) actual_comp_datetime\n" +
                "            , max(k.comp_num) comp_num\n" +
                "            , max(k.lot_quantity) lot_quantity\n" +
                "            , max(k.cycle_time) cycle_time\n" +
                "        FROM\n" +
                "            ( \n" +
                "                with recursive hierarchY_list AS ( \n" +
                "                    SELECT\n" +
                "                        tkh.child_id \n" +
                "                    FROM\n" +
                "                        tre_kanban_hierarchy tkh \n" +
                "                    WHERE\n" +
                "                        tkh.child_id = ANY (?3)\n" +
                "                    UNION ALL \n" +
                "                    SELECT\n" +
                "                        tkh1.child_id \n" +
                "                    FROM\n" +
                "                        tre_kanban_hierarchy tkh1\n" +
                "                        , hierarchy_list hl \n" +
                "                    WHERE\n" +
                "                        hl.child_id = tkh1.parent_id\n" +
                "                ) \n" +
                "                SELECT distinct\n" +
                "                    (tk.kanban_id) kanban_id\n" +
                "                    , tk.production_number\n" +
                "                    , tk.start_datetime\n" +
                "                    , tk.comp_datetime\n" +
                "                    , tk.actual_start_datetime\n" +
                "                    , tk.actual_comp_datetime\n" +
                "                    , tk.comp_num\n" +
                "                    , tk.lot_quantity\n" +
                "                    , tk.cycle_time\n" +
                "                FROM\n" +
                "                    con_kanban_hierarchy ckh\n" +
                "                JOIN hierarchy_list hl ON ckh.kanban_hierarchy_id = hl.child_id \n" +
                "                JOIN trn_kanban tk ON ckh.kanban_id = tk.kanban_id \n" +
                "                    AND tk.production_number NOTNULL \n" +
                "                    AND (tk.start_datetime < ?4 OR tk.actual_start_datetime < ?4) \n" +
                "                    AND (tk.comp_datetime > ?5 OR tk.actual_comp_datetime > ?5 OR ((tk.kanban_status ='WORKING' OR tk.kanban_status = 'SUSPEND') AND ?2 > ?5)) \n" +
                "                    AND (tk.kanban_status <> 'PLANNING' AND tk.kanban_status <> 'INTERRUPT')\n" +
                "            ) as k \n" +
                "            JOIN trn_work_kanban twk ON twk.skip_flag = false AND k.kanban_id = twk.kanban_id \n" +
                "                AND (twk.work_status <> 'PLANNING' AND twk.work_status <> 'INTERRUPT') \n" +
                "        GROUP BY k.production_number\n" +
                "    ) k2 \n" +
                "    JOIN trn_kanban tk2 ON (k2.work_num <> k2.comp_work_num OR k2.actual_comp_datetime > ?1) AND k2.kanban_id = tk2.kanban_id \n" +
                "    LEFT JOIN trn_actual_result tar ON tar.actual_id = k2.suspend_last_actual_id \n" +
                "    LEFT JOIN trn_actual_result tar2 ON tar2.actual_id = k2.defect_last_actual_id \n" +
                "    LEFT JOIN mst_reason mr ON mr.reason_type = 1 AND mr.reason = tar.interrupt_reason \n" +
                "WHERE\n" +
                "    tar2.actual_id IS NULL OR tar2.implement_datetime > ?1",
            resultClass = ActualProductEntity.class)
})
public class ActualProductEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "kanban_id")
    private Long kanbanId; // カンバンID

    @Column(name = "model_name")
    private String modelName; // モデル名

    @Column(name = "production_number")
    private String productNumber; // 製造番号

    @Column(name = "start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDatetime;// 開始予定日時

    @Column(name = "comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compDatetime;// 完了予定日時

    @Column(name = "actual_start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartDatetime; // 実開始時間

    @Column(name = "actual_comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualCompDatetime; // 実終了時間

    @Enumerated(EnumType.STRING)
    @Column(name = "kanban_status")
    private StatusPatternEnum kanbanStatus;// 工程ステータス

    @Column(name = "work_num")
    private Long workNum; // 工程数

    @Column(name = "comp_work_num")
    private Long compWorkNum; // 完了工程数

    @Column(name = "kanban_add_info")
    private String kanbanAdditionalInfo; //追加情報

    @Column(name = "interrupt_reason")
    private String interruptReason; // 中断理由

    @Column(name = "font_color")
    private String fontColor;// 中断時の文字色

    @Column(name = "back_color")
    private String backColor;// 中断時の背景色

    @Enumerated(EnumType.STRING)
    @Column(name = "light_pattern")
    private LightPatternEnum lightPattern; //点灯パターン

    @Column(name = "defect_reason")
    private String defectReason; //不良理由

    @Column(name = "comp_num")
    private Long compNum; // 完了数

    @Column(name = "lot_quantity")
    private Long lotQuantity; // ロット数量
    
    @Column(name = "cycle_time")
    private Long cycleTime; // 標準サイクルタイム
   
    public ActualProductEntity() {
    }

}
