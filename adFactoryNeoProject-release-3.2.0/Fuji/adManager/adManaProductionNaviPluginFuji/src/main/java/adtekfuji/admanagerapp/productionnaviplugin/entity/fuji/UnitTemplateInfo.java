/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.entity.fuji;

/**
 * ユニットテンプレート情報
 *
 * @author nar-nakamura
 */
public class UnitTemplateInfo {

    private String unitTemplateName;// ユニットテンプレート名
    private String workflowDiaglam;// ワークフロー図
    private Long outputKanbanHierarchyId;// カンバン出力先階層

    /**
     * コンストラクタ
     */
    public UnitTemplateInfo() {
    }

    /**
     * ユニットテンプレート名を取得する。
     *
     * @return ユニットテンプレート名
     */
    public String getUnitTemplateName() {
        return this.unitTemplateName;
    }

    /**
     * ユニットテンプレート名を設定する。
     *
     * @param unitTemplateName ユニットテンプレート名
     */
    public void setUnitTemplateName(String unitTemplateName) {
        this.unitTemplateName = unitTemplateName;
    }

    /**
     * ワークフロー図を取得する。
     *
     * @return ワークフロー図
     */
    public String getWorkflowDiaglam() {
        return this.workflowDiaglam;
    }

    /**
     * ワークフロー図を設定する。
     *
     * @param workflowDiaglam ワークフロー図
     */
    public void setWorkflowDiaglam(String workflowDiaglam) {
        this.workflowDiaglam = workflowDiaglam;
    }

    /**
     * カンバン出力先階層IDを取得する。
     *
     * @return カンバン出力先階層ID
     */
    public Long getOutputKanbanHierarchyId() {
        return this.outputKanbanHierarchyId;
    }

    /**
     * カンバン出力先階層IDを設定する。
     *
     * @param outputKanbanHierarchyId カンバン出力先階層ID
     */
    public void setOutputKanbanHierarchyId(Long outputKanbanHierarchyId) {
        this.outputKanbanHierarchyId = outputKanbanHierarchyId;
    }

    @Override
    public String toString() {
        return new StringBuilder("UnitTemplateInfo{")
                .append("unitTemplateName=").append(this.unitTemplateName)
                .append(", workflowDiaglam=").append(this.workflowDiaglam)
                .append(", outputKanbanHierarchyId=").append(this.outputKanbanHierarchyId)
                .append("}")
                .toString();
    }
}
