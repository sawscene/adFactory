/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * 出来高情報
 * 
 * @author s-heya
 */
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
    "model_name",
    "production_number",
    "work_id",
    "plan_num",
    "actual_num1",
    "actual_num2",
    "actual_num3"}))
@NamedNativeQueries({
    @NamedNativeQuery(name = "ProductionPerformanceEntity.sumByModelName",
            query = "SELECT (CASE WHEN LENGTH(g.model_name) > 0 THEN g.model_name ELSE '' END) model_name, '' AS production_number, g.work_id work_id, SUM(COALESCE(g.plan_num, 1)) plan_num, SUM(COALESCE(g.actual_num1, 0)) actual_num1, SUM(COALESCE(g.actual_num2, 0)) actual_num2, SUM(COALESCE(g.actual_num3, 0)) actual_num3 FROM (SELECT wkan.kanban_id, wkan.work_id, MAX(kan.model_name) model_name, SUM(kan.lot_quantity) plan_num, SUM(wkan.actual_num1) actual_num1, SUM(wkan.actual_num2) actual_num2, SUM(wkan.actual_num3) actual_num3 FROM trn_work_kanban wkan LEFT JOIN trn_kanban kan ON kan.kanban_id = wkan.kanban_id WHERE kan.kanban_status <> 'PLANNING' AND (wkan.work_status = ANY (ARRAY ['PLANNED', 'WORKING', 'SUSPEND', 'DEFECT']) OR (wkan.actual_start_datetime >= ?1 AND wkan.actual_comp_datetime <= ?2)) GROUP BY wkan.kanban_id, wkan.work_id) g GROUP BY model_name, work_id ORDER BY model_name;",
            resultClass = ProductionPerformanceEntity.class),
    @NamedNativeQuery(name = "ProductionPerformanceEntity.sumByModelNameAndProductionNumber",
            query = "SELECT (CASE WHEN LENGTH(g.model_name) > 0 THEN g.model_name ELSE '' END) model_name, (CASE WHEN LENGTH(g.production_number) > 0 THEN g.production_number ELSE '' END) production_number, g.work_id work_id, SUM(COALESCE(g.plan_num, 1)) plan_num, SUM(COALESCE(g.actual_num1, 0)) actual_num1, SUM(COALESCE(g.actual_num2, 0)) actual_num2, SUM(COALESCE(g.actual_num3, 0)) actual_num3 FROM (SELECT wkan.kanban_id, wkan.work_id, MAX(kan.model_name) model_name, MAX(kan.production_number) production_number, SUM(kan.lot_quantity) plan_num, SUM(wkan.actual_num1) actual_num1, SUM(wkan.actual_num2) actual_num2, SUM(wkan.actual_num3) actual_num3 FROM trn_work_kanban wkan LEFT JOIN trn_kanban kan ON kan.kanban_id = wkan.kanban_id WHERE kan.kanban_status <> 'PLANNING' AND (wkan.work_status = ANY (ARRAY ['PLANNED', 'WORKING', 'SUSPEND', 'DEFECT']) OR (wkan.actual_start_datetime >= ?1 AND wkan.actual_comp_datetime <= ?2)) GROUP BY wkan.kanban_id, wkan.work_id) g GROUP BY model_name, production_number, work_id ORDER BY model_name, production_number;",
            resultClass = ProductionPerformanceEntity.class)})
@Entity
public class ProductionPerformanceEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "model_name")
    private String modelName;
    @Id
    @Column(name = "production_number")
    private String productionNumber;
    @Id
    @Column(name = "work_id")
    private Long workId;
    @Column(name = "plan_num")
    private Integer planNum;
    @Column(name = "actual_num1")
    private Integer actualNum1;
    @Column(name = "actual_num2")
    private Integer actualNum2;
    @Column(name = "actual_num3")
    private Integer actualNum3;

    /**
     * モデル名を取得する。
     * 
     * @return モデル名 
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * 製造番号を取得する。
     * 
     * @return 製造番号 
     */
    public String getProductionNumber() {
        return productionNumber;
    }

    /**
     * 工程IDを取得する。
     * 
     * @return 工程ID
     */
    public Long getWorkId() {
        return workId;
    }

    /**
     * 計画数を取得する。
     * 
     * @return 計画数
     */
    public Integer getPlanNum() {
        return planNum;
    }

    /**
     * 良品数を取得する。
     * 
     * @return 良品数
     */
    public Integer getActualNum1() {
        return actualNum1;
    }

    /**
     * 再生品数を取得する。
     * 
     * @return 再生品数
     */
    public Integer getActualNum2() {
        return actualNum2;
    }

    /**
     * 不良品数を取得する。
     * 
     * @return 不良品数
     */
    public Integer getActualNum3() {
        return actualNum3;
    }

    /**
     * ハッシュコードを取得する。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.modelName);
        hash = 13 * hash + Objects.hashCode(this.productionNumber);
        hash = 13 * hash + Objects.hashCode(this.workId);
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
        final ProductionPerformanceEntity other = (ProductionPerformanceEntity) obj;
        if (!Objects.equals(this.modelName, other.modelName)) {
            return false;
        }
        if (!Objects.equals(this.productionNumber, other.productionNumber)) {
            return false;
        }
        return Objects.equals(this.workId, other.workId);
    }

    
    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */    @Override
    public String toString() {
        return new StringBuilder("ProductionPerformanceEntity{")
                .append("modelName=").append(modelName)
                .append(", productionNumber=").append(productionNumber)
                .append(", workId=").append(workId)
                .append(", planNum=").append(planNum)
                .append(", actualNum1=").append(actualNum1)
                .append(", actualNum2=").append(actualNum2)
                .append(", actualNum3=").append(actualNum3)
                .append("}").toString();
    }


}
