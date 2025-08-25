/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin.common;

import adtekfuji.cash.CashManager;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javafx.scene.control.ProgressIndicator;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.common.agenda.CustomAgendaEntity;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 * 生産予定取得用クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.14.Wen
 */
public class ScheduleSearcher {

    public static Boolean isInterrpt = Boolean.FALSE;

    /**
     * ユニットのスケジュールデータを取得
     *
     * @param startDate
     * @param endDate
     * @param downloadProgress
     * @return
     */
    public static List<CustomAgendaEntity> getUnitSchedule(Date startDate, Date endDate, ProgressIndicator downloadProgress) {
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        String text = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_UNITTEMPLATE, "0");
        String[] values = text.split(",");
        if (values.length == 0) {
            return new ArrayList<>();
        }

        List<Long> unitTemplateIds = new ArrayList<>();
        for (String value : values) {
            Long unitTemplateId = Long.parseLong(value);
            if (unitTemplateId != 0L) {
                unitTemplateIds.add(unitTemplateId);
            }
        }

        // 検索条件
        UnitSearchCondition condition = new UnitSearchCondition(null, unitTemplateIds, DateUtils.getBeginningOfDate(startDate), DateUtils.getEndOfDate(endDate));
        condition.setWithAssociate(true);

        // 表示ステータス
        CashManager cache = CashManager.getInstance();
        List<DisplayedStatusInfoEntity> displayedStatuses = new ArrayList<>();
        displayedStatuses = cache.getItemList(DisplayedStatusInfoEntity.class, displayedStatuses);

        List<UnitInfoEntity> units = RestAPI.searchUnit(condition);
        List<CustomAgendaEntity> unitAgendas = RestAPI.getUnitAgenda(units, startDate, endDate, displayedStatuses);

        //int unitCnt = 1;
        //for (UnitInfoEntity unit : units) {
        //    downloadProgress.setProgress((double) unitCnt / units.size());
        //    unitCnt++;
        //    if (isInterrpt) {
        //        downloadProgress.setProgress(100d);
        //        isInterrpt = Boolean.FALSE;
        //        break;
        //    }
        //}

        return unitAgendas;
    }

    /**
     * 組織のスケジュールデータを取得
     *
     * @param startDate
     * @param endDate
     * @param downloadProgress
     * @return
     */
    public static List<CustomAgendaEntity> getOrganizationSchedule(Date startDate, Date endDate, ProgressIndicator downloadProgress) {
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        String text = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_ORGANIZATION, "");
        String[] values = text.split(",");
        if (values.length == 0) {
            return new ArrayList<>();
        }

        List<Long> organizationIds = Arrays.stream(values).map(Long::valueOf).collect(Collectors.toList());
        List<OrganizationInfoEntity> organizations = CacheUtils.getCacheOrganization(organizationIds);

        CashManager cache = CashManager.getInstance();
        List<DisplayedStatusInfoEntity> displayedStatuses = new ArrayList<>();
        displayedStatuses = cache.getItemList(DisplayedStatusInfoEntity.class, displayedStatuses);

        List<CustomAgendaEntity> agendas = RestAPI.getOrganizationAgenda(organizations, startDate, endDate, displayedStatuses);
        return agendas;
    }

    public static void setIsInterrpt(Boolean isInterrpt) {
        ScheduleSearcher.isInterrpt = isInterrpt;
    }

    public static KanbanInfoEntity getKanban(Long id){
        return RestAPI.getKanban(id);
    }

    public static UnitTemplateInfoEntity getUnitTemplate(Long id) {
        return RestAPI.getUnitTemplate(id);
    }

    public static OrganizationInfoEntity getOrganization(Long id) {
        return RestAPI.getOrganization(id);
    }

    public static List<OrganizationInfoEntity> getOrganizations() {
        return RestAPI.getOrganizations();
    }

    public static List<EquipmentInfoEntity> getEquipments() {
        return RestAPI.getEquipments();
    }

    public static List<DisplayedStatusInfoEntity> getDisplayedStatus() {
        return RestAPI.getDisplayedStatuses();
    }
}
