/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common.agenda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleInfoEntity;

/**
 * ユニットの予実情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.11.24.thr
 */
public class WorkPlanCustomAgendaEntity {

    private static final long serialVersionUID = 1L;

    private String kanbanNameTitle;                // カンバン名
    private String modelNameTitle;        // モデル名
    private String workNoTitle;           // 工程順名
    private String orderNoTitle;          // オーダー番号
    private String cerialTitle;           // シリアル
    private String fontColor;
    private String backgraundColor;
    private Boolean isBlink;

    private List<WorkPlanCustomAgendaConcurrentEntity> planCollection = new ArrayList<>();
    private List<WorkPlanCustomAgendaConcurrentEntity> actualCollection = new ArrayList<>();
    private List<ScheduleInfoEntity> scheduleCollection = new ArrayList<>();
    
    private Long kanbanId;
    private Long organizationId;

    /**
     * コンストラクタ
     */
    public WorkPlanCustomAgendaEntity() {

    }

    /**
     * コンストラクタ
     * 
     * @param _title1 カンバン名
     * @param _fontColor 文字色
     * @param _backgraundColor 背景色
     */
//    public CustomAgendaEntity(String title1, String fontColor, String backgraundColor) {
//        this.title1 = title1;
//        this.fontColor = fontColor;
//        this.backgraundColor = backgraundColor;
//    }
    
    /**
     * コンストラクタ
     * 
     * @param _title1 カンバン名
     * @param _title2 モデル名
     * @param _title3 工程順名
     * @param _title4 オーダー番号
     * @param _title5 シリアル
     * @param _fontColor 文字色
     * @param _backgraundColor 背景色
     */
    public WorkPlanCustomAgendaEntity(String _title1, String _title2, String _title3, String _title4, String _title5, String _fontColor, String _backgraundColor) {
        this.kanbanNameTitle = _title1;
        this.modelNameTitle = _title2;
        this.workNoTitle = _title3;
        this.orderNoTitle = _title4;
        this.cerialTitle = _title5;
        this.fontColor = _fontColor;
        this.backgraundColor = _backgraundColor;
    }

    public String getKanbanNameTitle() {
        return kanbanNameTitle;
    }
    public void setKanbanNameTitle(String _title1) {
        this.kanbanNameTitle = _title1;
    }

    public String getModelNameTitle() {
        return modelNameTitle;
    }
    public void setModelNameTitle(String _title2) {
        this.modelNameTitle = _title2;
    }

    public String getWorkNoTitle() {
        return workNoTitle;
    }
    public void setWorkNoTitle(String _title3) {
        this.workNoTitle = _title3;
    }

    public String getOrderNoTitle() {
        return orderNoTitle;
    }
    public void setOrderNoTitle(String _title4) {
        this.orderNoTitle = _title4;
    }

    public String getCerialTitle() {
        return cerialTitle;
    }
    public void setCerialTitle(String _title5) {
        this.cerialTitle = _title5;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBackgraundColor() {
        return backgraundColor;
    }

    public void setBackgraundColor(String _backgraundColor) {
        this.backgraundColor = _backgraundColor;
    }

    public Boolean getIsBlink() {
        return isBlink;
    }

    public void setIsBlink(Boolean isBlink) {
        this.isBlink = isBlink;
    }
    // 計画

    public List<WorkPlanCustomAgendaConcurrentEntity> getPlanCollection() {
        return planCollection;
    }

    // 計画
    public void setPlanCollection(List<WorkPlanCustomAgendaConcurrentEntity> _planCollection) {
        this.planCollection = _planCollection;
    }

    // 計画
    public WorkPlanCustomAgendaEntity addPlan(WorkPlanCustomAgendaConcurrentEntity _concurrent) {
        this.planCollection.add(_concurrent);
        return this;
    }

    // 計画
    public WorkPlanCustomAgendaEntity addPlans(WorkPlanCustomAgendaConcurrentEntity... _concurrents) {
        this.planCollection.addAll(Arrays.asList(_concurrents));
        return this;
    }

    // 計画
    public WorkPlanCustomAgendaEntity addAllPlans(List<WorkPlanCustomAgendaConcurrentEntity> _concurrents) {
        this.planCollection.addAll(_concurrents);
        return this;
    }

    // 実績
    public List<WorkPlanCustomAgendaConcurrentEntity> getActualCollection() {
        return actualCollection;
    }

    // 実績
    public void setActualCollection(List<WorkPlanCustomAgendaConcurrentEntity> _actualCollection) {
        this.actualCollection = _actualCollection;
    }

    // 実績
    public WorkPlanCustomAgendaEntity addActual(WorkPlanCustomAgendaConcurrentEntity _concurrent) {
        this.actualCollection.add(_concurrent);
        return this;
    }

    // 実績
    public WorkPlanCustomAgendaEntity addActuals(WorkPlanCustomAgendaConcurrentEntity... _concurrents) {
        this.actualCollection.addAll(Arrays.asList(_concurrents));
        return this;
    }

    // 実績
    public WorkPlanCustomAgendaEntity addAllActuals(List<WorkPlanCustomAgendaConcurrentEntity> _concurrents) {
        this.actualCollection.addAll(_concurrents);
        return this;
    }

    // 予定
    public List<ScheduleInfoEntity> getScheduleCollection() {
        return scheduleCollection;
    }

    // 予定
    public void setScheduleCollection(List<ScheduleInfoEntity> _scheduleCollection) {
        this.scheduleCollection = _scheduleCollection;
    }
    
    public Long getKanbanId() {
        return kanbanId;
    }

    public void setKanbanId(Long _kanbanId) {
        this.kanbanId = _kanbanId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long _organizationId) {
        this.organizationId = _organizationId;
    }
}
