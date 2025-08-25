/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.model.data;

import adtekfuji.andon.agenda.enumerate.PayoutOrderDisplayGroupEnum;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryInfo;
import jp.adtekfuji.andon.enumerate.DisplayModeEnum;
import jp.adtekfuji.andon.enumerate.DisplayOrderEnum;
import jp.adtekfuji.andon.enumerate.TimeScaleEnum;

/**
 * カレント情報
 *
 * @author s-heya
 */
public class CurrentData {

    private static CurrentData instance = null;

    private Map<Long, Agenda> agendas;
    private List<KanbanInfoEntity> kanbans;
    private List<OrganizationInfoEntity> organizations;
    private List<Long> equipmentIds;
    private List<Long> kanbanHierarchyIds;
    private DisplayModeEnum displayMode;
    private Date fromDate = null;
    private Date toDate = null;
    private TimeScaleEnum timeScale;
    private Date keepTargetDay;
    private Long displayPeriod;
    private DisplayOrderEnum displayOrder;
    /** 出庫指示情報 */
    private Map<PayoutOrderDisplayGroupEnum, List<TrnDeliveryInfo>> deliveryInfos;
    /** モデル名項目(検索項目) */
    private String modelName;
    /** 製番項目(検索項目) */
    private String orderNo;	

    public CurrentData() {
        this.kanbans = new ArrayList<>();
        this.organizations = new ArrayList<>();
        this.equipmentIds = new ArrayList<>();
        this.kanbanHierarchyIds = new ArrayList<>();
    }

    public static CurrentData getInstance() {
        if (Objects.isNull(instance)) {
            instance = new CurrentData();
        }

        return instance;
    }

    public List<KanbanInfoEntity> getKanbans() {
        return kanbans;
    }

    public void setKanbans(List<KanbanInfoEntity> kanbans) {
        this.kanbans = Collections.synchronizedList(new LinkedList<>(kanbans));
    }

    public List<OrganizationInfoEntity> getOrganizations() {
        return organizations;
    }

    public void seOrganizations(List<OrganizationInfoEntity> organizations) {
        this.organizations = Collections.synchronizedList(new LinkedList<>(organizations));
    }

    public List<Long> getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(List<Long> equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public List<Long> getKanbanHierarchyIds() {
        return kanbanHierarchyIds;
    }

    public void setKanbanHierarchyIds(List<Long> kanbanHierarchyIds) {
        this.kanbanHierarchyIds = kanbanHierarchyIds;
    }

    public DisplayModeEnum getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(DisplayModeEnum displayMode) {
        this.displayMode = displayMode;
    }
    
    /**
     * 時間軸スケール取得
     * 
     * @return 設定値
     */
    public TimeScaleEnum getTimeScale() {
        return this.timeScale;
    }
    
    /**
     * 時間軸スケール設定
     *
     * @param timeScale 設定値
     */
    public void setTimeScale(TimeScaleEnum timeScale) {
        this.timeScale = timeScale;
    }

    public Map<Long, Agenda> getAgendas() {
        return agendas;
    }

    public void setAgendas(Map<Long, Agenda> planAgendas) {
        this.agendas = planAgendas;
    }

    public boolean containsKanban(KanbanInfoEntity kanban) {
        if (kanbans != null) {
            for (KanbanInfoEntity currentKanban : kanbans) {
                if (Objects.equals(currentKanban.getKanbanId(), kanban.getKanbanId())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean containsOrganization(OrganizationInfoEntity organization) {
        if (organizations != null) {
            for (OrganizationInfoEntity currentOrganization : organizations) {
                if (Objects.equals(currentOrganization.getOrganizationId(), organization.getOrganizationId())) {
                    return true;
                }
            }
        }

        return false;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }


    public Long getDisplayPeriod() {
        return displayPeriod;
    }

    public void setDisplayPeriod(Long displayPeriod) {
        this.displayPeriod = displayPeriod;
    }

    public DisplayOrderEnum getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(DisplayOrderEnum displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Date getKeepTargetDay() {
        return this.keepTargetDay;
    }

    public void setKeepTargetDay(Date keepTargetDay) {
        this.keepTargetDay = keepTargetDay;
    }

    /**
     * 出庫指示情報を取得する
     * 
     * @return 出庫指示情報
     */
    public Map<PayoutOrderDisplayGroupEnum, List<TrnDeliveryInfo>> getDeliveryInfos() {
        return deliveryInfos;
    }

    /**
     * 出庫指示情報を設定する。
     * 
     * @param planAgendas 出庫指示情報
     */
    public void setDeliveryInfos(Map<PayoutOrderDisplayGroupEnum, List<TrnDeliveryInfo>> planAgendas) {
        this.deliveryInfos = planAgendas;
    }

    /**
     * モデル名(検索項目)を取得する
     * 
     * @return モデル名
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * モデル名(検索項目)を設定する。
     * 
     * @param value モデル名
     */
    public void setModelName(String value) {
        this.modelName = value;
    }

    /**
     * 製番(検索項目)を取得する
     * 
     * @return モデル名
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 製番(検索項目)を設定する。
     * 
     * @param value 製番
     */
    public void setOrderNo(String value) {
        this.orderNo = value;
    }

    /**
     * fromの00:00:00から23:59:59までの時間で日時を構築する
     *
     * @param from 開始基準
     */
    public void setDate(LocalDateTime from) {
        Instant instant = from.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        setFromDate(Date.from(instant));
        setToDate(new Date(Date.from(instant).getTime() + (23 * 3600 + 59 * 60 + 59) * 1000L));
    }
    
    /**
     * fromから日数分の範囲で日時を構築する
     *
     * @param from 開始基準
     * @param days 日数
     */
    public void setDays(LocalDateTime from, int days) {
        Instant fromInstant = from.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant toInstant = from.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).plusDays(days).minusDays(1).toInstant();
        setFromDate(Date.from(fromInstant));
        setToDate(new Date(Date.from(toInstant).getTime() + (23 * 3600 + 59 * 60 + 59) * 1000L));
    }
    
    /**
     * fromから月数分の範囲で日時を構築する
     *
     * @param from 開始基準
     * @param months 月数
     */
    public void setMonths(LocalDateTime from, int months) {
        Instant fromInstant = from.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant toInstant = from.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).plusMonths(months).minusDays(1).toInstant();
        setFromDate(Date.from(fromInstant));
        setToDate(new Date(Date.from(toInstant).getTime() + (23 * 3600 + 59 * 60 + 59) * 1000L));
    }
}
