/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils;

import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanWorkSettingDialogEntity;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.BreaktimeInfoFacade;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringTime;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.utility.KanbanTimeUtils;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 *
 * @author e-mori
 */
public class WorkKanbanTimeReplaceUtils {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static String DEFAULT_TAKTTIME = "00:00:00";
    private final static String DEFAULT_OFFSETTIME = "00:00:00";
    private static final long RANGE = 20;

    /**
     * データ更新処理
     *
     * @param editEntitys 編集対象のデータ
     * @param timeReplaceData 時間リプレース用データ
     * @param dialogEntity 編集内容
     */
    public static void batchEditWorkKanban(List<WorkKanbanInfoEntity> editEntitys, WorkKanbanTimeReplaceData timeReplaceData, WorkPlanWorkSettingDialogEntity<WorkKanbanPropertyInfoEntity> dialogEntity) {
        for (WorkKanbanInfoEntity entity : editEntitys) {
            //タクトタイム及びオフセット時間の変更はスキップの設定次第でやるかやらないか判断
            if ((Objects.nonNull(dialogEntity.getTaktTime()) && !dialogEntity.getTaktTime().equals(DEFAULT_TAKTTIME))) {
                entity.taktTimeProperty().unbind();
                entity.setTaktTime(Long.valueOf(StringTime.convertStringTimeToMillis(dialogEntity.getTaktTime())).intValue());
            }
            if (!dialogEntity.getOrganizations().isEmpty()) {
                List<Long> orgIdList = new ArrayList<>();
                dialogEntity.getOrganizations().stream().forEach((organization) -> {
                    orgIdList.add(organization.getOrganizationId());
                });
                entity.setOrganizationCollection(orgIdList);
            }
            if (!dialogEntity.getEquipments().isEmpty()) {
                List<Long> equipIdList = new ArrayList<>();
                dialogEntity.getEquipments().stream().forEach((equipment) -> {
                    equipIdList.add(equipment.getEquipmentId());
                });
                entity.setEquipmentCollection(equipIdList);
            }
            if (editEntitys.size() <= 1) {
                //カスタムフィールド
                int order = 0;
                if (Objects.nonNull(entity.getWorkKanbanId())) {
                    for (WorkKanbanPropertyInfoEntity e : dialogEntity.getProperties()) {
                        e.setFkMasterId(entity.getWorkKanbanId());
                        e.updateMember();
                        e.setWorkKanbanPropOrder(order);
                        order = order + 1;
                    }
                } else {
                    for (WorkKanbanPropertyInfoEntity e : dialogEntity.getProperties()) {
                        e.updateMember();
                        e.setWorkKanbanPropOrder(order);
                        order = order + 1;
                    }
                }
                entity.setPropertyCollection(dialogEntity.getProperties());
            }
        }
        //休憩の設定
        if (!dialogEntity.getOrganizations().isEmpty()) {
            batchEditWorkKanbanOrganizationBreaktimeOffset(editEntitys, timeReplaceData);
        }
        //スキップの設定
        if (Objects.nonNull(dialogEntity.getSkip())) {
            batchEditWorkKanbanSkipOffset(editEntitys, timeReplaceData, dialogEntity);
        }
        //基準開始時間・オフセット処理
        if (dialogEntity.isStartTimeOffset() || Objects.nonNull(dialogEntity.getOffsetTime())) {
            if (!dialogEntity.getOffsetTime().equals(DEFAULT_OFFSETTIME)) {
                batchEditWorkKanbanStartTimeOffset(editEntitys, timeReplaceData, dialogEntity);
            }
        }
    }

    /**
     * 工程編集ダイアログ基準開始時間オフセット処理
     *
     * @param editEntitys
     * @param timeReplaceData
     * @param dialogEntity
     */
    private static void batchEditWorkKanbanStartTimeOffset(List<WorkKanbanInfoEntity> editEntitys, WorkKanbanTimeReplaceData timeReplaceData, WorkPlanWorkSettingDialogEntity<WorkKanbanPropertyInfoEntity> dialogEntity) {
        //全体に休憩時間減算処理
        KanbanTimeUtils.batchInvalidBreakTimeWorkKanbanOffsetTimes(timeReplaceData.getWorkKanbanInfoEntitys(), timeReplaceData.getBreakTimeInfoEntitys());
        //全体にスキップオフセット無効
        KanbanTimeUtils.batchInvalidSkipWorkKanbanOffsetTimes(timeReplaceData.getWorkKanbanInfoEntitys());
        //選択された工程に基準時間オフセット処理

        List<WorkKanbanInfoEntity> offsetsData = new ArrayList<>();
        if (dialogEntity.isStartTimeOffset()) {
            KanbanTimeUtils.batchStartOffsetTime(editEntitys, dialogEntity.getOffsetTime(), timeReplaceData.getDateFormat());
        } else if (!dialogEntity.isStartTimeOffset() && Objects.nonNull(dialogEntity.getOffsetTime())) {
            if (!dialogEntity.getOffsetTime().equals(DEFAULT_OFFSETTIME)) {
                for (WorkKanbanInfoEntity entity : editEntitys) {
                    entity.setStartDatetime(StringTime.getFixedDate(entity.getStartDatetime(), dialogEntity.getOffsetTime()));
                    entity.setCompDatetime(StringTime.getFixedDate(entity.getCompDatetime(), dialogEntity.getOffsetTime()));
                }
            }
        }
        for (WorkKanbanInfoEntity edit : editEntitys) {
            WorkKanbanInfoEntity offsetData = new WorkKanbanInfoEntity();
            offsetData.setStartDatetime(edit.getStartDatetime());
            offsetData.setCompDatetime(edit.getCompDatetime());
            offsetData.setWorkKanbanOrder(edit.getWorkKanbanOrder());
            offsetsData.add(offsetData);
        }

        //選択対象外にスキップを反映
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(timeReplaceData.getWorkKanbanInfoEntitys());
        //選択対象外に休憩を反映
        KanbanTimeUtils.batchValidBreakTimeWorkKanbanOffsetTimes(timeReplaceData.getWorkKanbanInfoEntitys(), timeReplaceData.getBreakTimeInfoEntitys());
        for (WorkKanbanInfoEntity edit : editEntitys) {
            for (WorkKanbanInfoEntity offset : offsetsData) {
                if (edit.getWorkKanbanOrder().equals(offset.getWorkKanbanOrder())) {
                    edit.setStartDatetime(offset.getStartDatetime());
                    edit.setCompDatetime(offset.getCompDatetime());
                }
            }
        }
        //選択対象にスキップを反映
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(editEntitys);
        //選択対象に休憩を反映
        KanbanTimeUtils.batchValidBreakTimeWorkKanbanOffsetTimes(editEntitys, timeReplaceData.getBreakTimeInfoEntitys());
    }

    /**
     * 計画時間の再計算
     *
     * @param workKanbans
     * @param replaceData
     */
    private static void batchEditWorkKanbanOrganizationBreaktimeOffset(List<WorkKanbanInfoEntity> workKanbans, WorkKanbanTimeReplaceData replaceData) {
        // 計画時間から休憩を取り除く
        if (!replaceData.getBreakTimeInfoEntitys().isEmpty()) {
            KanbanTimeUtils.batchInvalidBreakTimeWorkKanbanOffsetTimes(replaceData.getWorkKanbanInfoEntitys(), replaceData.getBreakTimeInfoEntitys());
        }

        // 休憩を取得
        List<BreakTimeInfoEntity> breakTimes = new ArrayList<>(WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(workKanbans));
        replaceData.setBreakTimeInfoEntitys(breakTimes);

        // 計画時間から休憩を入れる
        if (!replaceData.getBreakTimeInfoEntitys().isEmpty()) {
            KanbanTimeUtils.batchValidBreakTimeWorkKanbanOffsetTimes(replaceData.getWorkKanbanInfoEntitys(), replaceData.getBreakTimeInfoEntitys());
        }
    }

    /**
     * 工程編集ダイアログスキップ変更処理
     *
     * @param editEntitys
     * @param timeReplaceData
     */
    private static void batchEditWorkKanbanSkipOffset(List<WorkKanbanInfoEntity> editEntitys, WorkKanbanTimeReplaceData timeReplaceData, WorkPlanWorkSettingDialogEntity<WorkKanbanPropertyInfoEntity> dialogEntity) {
        //全体に休憩時間減算処理
//        KanbanTimeUtils.batchInvalidBreakTimeWorkKanbanOffsetTimes(timeReplaceData.getWorkKanbanInfoEntitys(), timeReplaceData.getBreakTimeInfoEntitys());
        //スキップ情報が変わった工程を抽出してオフセット処理を実行
        //editEntitys.sort(Comparator.comparing(work -> work.getWorkKanbanOrder()));
        for (WorkKanbanInfoEntity editEntity : editEntitys) {
            editEntity.setSkipFlag(dialogEntity.getSkip());
            //for (int baseIndex = 0; timeReplaceData.getWorkKanbanInfoEntitys().size() > baseIndex; baseIndex++) {
            //    //編集する情報が一致したときのリストのIndexをもとにスキップの有効または無効処理を実施する
            //    if (editEntity.getWorkKanbanOrder().equals(timeReplaceData.getWorkKanbanInfoEntitys().get(baseIndex).getWorkKanbanOrder())) {
            //        if (dialogEntity.getSkip()) {
            //            if (!editEntity.getSkipFlag().equals(dialogEntity.getSkip())) {
            //                workKanbanEnableSkipOffset(editEntity, timeReplaceData);
            //                timeReplaceData.getWorkKanbanInfoEntitys().get(baseIndex).setSkipFlag(dialogEntity.getSkip());
            //            }
            //        } else {
            //            if (!editEntity.getSkipFlag().equals(dialogEntity.getSkip())) {
            //                workKanbanDisenableSkipOffset(editEntity, timeReplaceData);
            //                timeReplaceData.getWorkKanbanInfoEntitys().get(baseIndex).setSkipFlag(dialogEntity.getSkip());
            //            }
            //        }
            //    }
            //}
        }
        //全体に休憩時間を加算
//        KanbanTimeUtils.batchValidBreakTimeWorkKanbanOffsetTimes(timeReplaceData.getWorkKanbanInfoEntitys(), timeReplaceData.getBreakTimeInfoEntitys());
    }

    /**
     * 基準時間,スキップ情報,休憩時間の反映処理
     *
     * @param data
     * @throws ParseException
     */
    public static void createKanbanRefernceTime(WorkKanbanTimeReplaceData data) throws ParseException {
        if (Objects.nonNull(data.getReferenceStartTime()) && Objects.nonNull(data.getWorkKanbanInfoEntitys()) && Objects.nonNull(data.getDateFormat())) {
            KanbanTimeUtils.batchStartOffsetTime(data.getWorkKanbanInfoEntitys(), data.getReferenceStartTime(), data.getDateFormat());
        }
        if (Objects.nonNull(data.getWorkKanbanInfoEntitys())) {
            KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(data.getWorkKanbanInfoEntitys());
        }
        if (Objects.nonNull(data.getBreakTimeInfoEntitys())) {
            KanbanTimeUtils.batchValidBreakTimeWorkKanbanOffsetTimes(data.getWorkKanbanInfoEntitys(), data.getBreakTimeInfoEntitys());
        }
    }

    /**
     * 基準時間オフセット処理
     *
     * @param data
     */
    public static void workKanbanStartTimeOffset(WorkKanbanTimeReplaceData data) {
        if (Objects.isNull(data.getWorkKanbanInfoEntitys()) || Objects.isNull(data.getReferenceStartTime())) {
            return;
        }
        ArrayList<WorkKanbanInfoEntity> workKanbanInfoEntitys = new ArrayList<>(data.getWorkKanbanInfoEntitys());
        KanbanTimeUtils.batchStartOffsetTime(workKanbanInfoEntitys, data.getReferenceStartTime(), data.getDateFormat());
    }

    /**
     * スキップ情報が有効になったときの時間再計算処理
     *
     * @param entity
     * @param timeReplaceData
     */
    public static void workKanbanEnableSkipOffset(WorkKanbanInfoEntity entity, WorkKanbanTimeReplaceData timeReplaceData) {
        entity.setSkipFlag(Boolean.FALSE);
        KanbanTimeUtils.batchInvalidBreakTimeWorkKanbanOffsetTimes(timeReplaceData.getWorkKanbanInfoEntitys(), timeReplaceData.getBreakTimeInfoEntitys());
        entity.setSkipFlag(Boolean.TRUE);
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTime(entity.getWorkKanbanOrder(), new ArrayList<>(timeReplaceData.getWorkKanbanInfoEntitys()));
        KanbanTimeUtils.batchValidBreakTimeWorkKanbanOffsetTimes(timeReplaceData.getWorkKanbanInfoEntitys(), timeReplaceData.getBreakTimeInfoEntitys());
    }

    /**
     * スキップ情報が無効になったときの時間再計算処理
     *
     * @param entity
     * @param timeReplaceData
     */
    public static void workKanbanDisenableSkipOffset(WorkKanbanInfoEntity entity, WorkKanbanTimeReplaceData timeReplaceData) {
        entity.setSkipFlag(Boolean.TRUE);
        KanbanTimeUtils.batchInvalidBreakTimeWorkKanbanOffsetTimes(timeReplaceData.getWorkKanbanInfoEntitys(), timeReplaceData.getBreakTimeInfoEntitys());
        entity.setSkipFlag(Boolean.FALSE);
        KanbanTimeUtils.batchInvalidSkipWorkKanbanOffsetTime(entity.getWorkKanbanOrder(), new ArrayList<>(timeReplaceData.getWorkKanbanInfoEntitys()));
        KanbanTimeUtils.batchValidBreakTimeWorkKanbanOffsetTimes(timeReplaceData.getWorkKanbanInfoEntitys(), timeReplaceData.getBreakTimeInfoEntitys());
    }

    /**
     * 工程カンバンにある休憩時間情報の取得
     *
     * @param works
     * @return 工程カンバンが内包する休憩情報の取得
     */
    public static List<BreakTimeInfoEntity> getWorkKanbanBreakTimes(List<WorkKanbanInfoEntity> works) {
        Set<Long> breaktimeIds = new HashSet();

        for (WorkKanbanInfoEntity work : works) {
            List<OrganizationInfoEntity> organizations = CacheUtils.getCacheOrganization(work.getOrganizationCollection());
            for (OrganizationInfoEntity organization : organizations) {
                if (Objects.isNull(organization.getBreakTimeInfoCollection())) {
                    continue;
                }

                breaktimeIds.addAll(organization.getBreakTimeInfoCollection());
            }
        }

        BreaktimeInfoFacade breakTimeInfoFacade = new BreaktimeInfoFacade();
        List<BreakTimeInfoEntity> breakTimes = new ArrayList<>();
        for (Long breaktimeId : breaktimeIds) {
            BreakTimeInfoEntity breaktime = breakTimeInfoFacade.find(breaktimeId);
            breakTimes.add(breaktime);
        }

        return breakTimes;
    }

    /**
     * 工程カンバンに割り当てられた組織の休憩時間ID一覧を取得する。
     *
     * @param workKanban 工程カンバン
     * @return 休憩時間ID一覧
     */
    public static List<Long> getWorkKanbanBreaktimeIds(WorkKanbanInfoEntity workKanban) {
        if (Objects.isNull(workKanban.getOrganizationCollection()) || workKanban.getOrganizationCollection().isEmpty()) {
            return new ArrayList();
        }

        List<OrganizationInfoEntity> organizations = CacheUtils.getCacheOrganization(workKanban.getOrganizationCollection());
        if (Objects.isNull(organizations) || organizations.isEmpty()) {
            return new ArrayList();
        }

        Set<Long> breaktimeIds = new HashSet();
        organizations.stream()
                .filter(p -> Objects.nonNull(p.getBreakTimeInfoCollection()) && !p.getBreakTimeInfoCollection().isEmpty())
                .forEach(organization -> {
            breaktimeIds.addAll(organization.getBreakTimeInfoCollection());
        });

        return new ArrayList(breaktimeIds);
    }

    /**
     * 就業外時間の取得処理
     *
     * @param defaultOffsetData
     * @return 就業外時間
     */
    public static BreakTimeInfoEntity getEmploymentOutTime(WorkPlanKanbanDefaultOffsetData defaultOffsetData) {
        if (Objects.nonNull(defaultOffsetData)) {
            Calendar start = new Calendar.Builder().setInstant(defaultOffsetData.getStartOffsetTime()).build();
            Calendar open = new Calendar.Builder().setInstant(defaultOffsetData.getOpeningTime()).build();
            Calendar close = new Calendar.Builder().setInstant(defaultOffsetData.getClosingTime()).build();
            open.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DATE) + 1);
            close.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DATE));

            return new BreakTimeInfoEntity("EmploymentOutTime", close.getTime(), open.getTime());
        }
        return null;
    }

    /**
     * 基準日時以降の休日一覧を取得する。(基準日時の当日を含む)
     *
     * @param baseTime 基準日時
     * @return 休日一覧
     */
    public static List<HolidayInfoEntity> getHolidays(Date baseTime) {
        CashManager cache = CashManager.getInstance();
        List<HolidayInfoEntity> holidayList = cache.getItemList(HolidayInfoEntity.class, new ArrayList<>());

        List<HolidayInfoEntity> holidays = holidayList.stream()
                .filter(p -> p.getHolidayDate().after(DateUtils.getBeginningOfDate(baseTime)) || p.getHolidayDate().equals(DateUtils.getBeginningOfDate(baseTime)))
                .collect(Collectors.toList());
        Collections.sort(holidays, (HolidayInfoEntity left, HolidayInfoEntity right) -> {
            return left.getHolidayDate().compareTo(right.getHolidayDate());
        });

        return holidays;
    }
}
