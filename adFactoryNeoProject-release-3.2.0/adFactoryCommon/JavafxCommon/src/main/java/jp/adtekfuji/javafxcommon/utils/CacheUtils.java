/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.utils;

import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.concurrent.Task;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.indirectwork.WorkCategoryInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DelayReasonInfoEntity;
import jp.adtekfuji.adFactory.entity.master.InterruptReasonInfoEntity;
import jp.adtekfuji.adFactory.entity.master.LabelInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ReasonCategoryInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * キャッシュユーティリティ
 *
 * @author nar-nakamura
 */
public class CacheUtils {

    private static final long RANGE = 100;
    private static final long RANGE2 = 200;// 休日情報 読み込み時のレンジ

    private static final int ID_RANGE = 20;

    private static final List<Class> readTasks = new ArrayList();// キャッシュ読み込み中タスクリスト
    private static final List<Class> readClasses = new ArrayList();// キャッシュ読み込み中クラスリスト

    /**
     * 指定したクラスがキャッシュに読み込み中かどうかを取得する。
     *
     * @param classData キャッシュ対象
     * @return 読み込み中？ (true:読み込み中, false:読み込み中ではない)
     */
    public static boolean isReadNow(Class classData) {
        return readTasks.contains(classData) || readClasses.contains(classData);
    }

    /**
     * 指定したクラスがキャッシュに読み込み中の場合、完了まで待機する。
     *
     * @param classData キャッシュ対象
     * @return 待機したか？ (true:読み込み中で待機した, false:読み込み中ではなかった)
     */
    public static boolean waitComp(Class classData) {
        if (!readClasses.contains(classData)) {
            return false;
        }

        while (readClasses.contains(classData)) {
            Thread.yield();
        }
        return true;
    }

    /**
     * キャッシュから情報を削除する。
     *
     * @param classData キャッシュ削除対象
     */
    public static void removeCacheData(Class classData) {
        Logger logger = LogManager.getLogger();
        logger.debug("removeCacheData: {}", classData);

        CashManager cache = CashManager.getInstance();
        cache.removeList(classData);
    }

    /**
     * 別スレッドでキャッシュに情報を読み込む。
     *
     * @param classData キャッシュ対象
     * @param isNewOnly 新規キャッシュのみ？ (true:キャッシュ済みの場合は何もしない, false:キャッシュ済みでも上書きで読み込む)
     */
    public static void createCacheData(Class classData, boolean isNewOnly) {
        Logger logger = LogManager.getLogger();
        logger.debug("createCacheData start: {}", classData);
        try {
            if (Objects.isNull(classData)) {
                return;
            }

            // 読み込み中の場合は何もしない。
            if (isReadNow(classData)) {
                return;
            }
            readTasks.add(classData);

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if (classData.equals(EquipmentInfoEntity.class)) {
                        // 設備
                        createCacheEquipment(isNewOnly);
                    } else if (classData.equals(OrganizationInfoEntity.class)) {
                        // 組織
                        createCacheOrganization(isNewOnly);
                    } else if (classData.equals(BreakTimeInfoEntity.class)) {
                        // 休憩時間
                        createCacheBreakTime(isNewOnly);
                    } else if (classData.equals(DelayReasonInfoEntity.class)) {
                        // 遅延理由
                        createCacheDelayReason(isNewOnly);
                    } else if (classData.equals(InterruptReasonInfoEntity.class)) {
                        // 中断理由
                        createCacheInterruptReason(isNewOnly);
                    } else if (classData.equals(HolidayInfoEntity.class)) {
                        // 休日
                        createCacheHoliday(isNewOnly);
                    } else if (classData.equals(LabelInfoEntity.class)) {
                        // ラベル
                        createCacheLabel(isNewOnly);
                    } else if (classData.equals(KanbanHierarchyInfoEntity.class)) {
                        // カンバン階層
                        createCacheKanbanHierarchy(isNewOnly);
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    readTasks.remove(classData);
                    logger.debug("createCashData end: {}", classData);
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * キャッシュに設備情報を読み込む。
     *
     * @param isNewOnly 新規キャッシュのみ？ (true:キャッシュ済みの場合は何もしない, false:キャッシュ済みでも上書きで読み込む)
     */
    public static void createCacheEquipment(boolean isNewOnly) {
        // 読み込み中の場合は完了を待つだけにする。
        if (waitComp(EquipmentInfoEntity.class)) {
            return;
        }

        CashManager cache = CashManager.getInstance();
        if (isNewOnly && cache.isExist(EquipmentInfoEntity.class)) {
            return;
        }

        Logger logger = LogManager.getLogger();
        logger.debug("createCacheEquipment start.");
        try {
            readClasses.add(EquipmentInfoEntity.class);

            cache.setNewCashList(EquipmentInfoEntity.class);
            cache.clearList(EquipmentInfoEntity.class);

            EquipmentInfoFacade facade = new EquipmentInfoFacade();
            Long infoCount = facade.count();
            for (long count = 0; count < infoCount; count += RANGE) {
                List<EquipmentInfoEntity> entities = facade.findRange(count, count + RANGE - 1);
                entities.stream().forEach((entity) -> {
                    cache.setItem(EquipmentInfoEntity.class, entity.getEquipmentId(), entity);
                });
            }
        } finally {
            readClasses.remove(EquipmentInfoEntity.class);
            logger.debug("createCacheEquipment end.");
        }
    }

    /**
     * キャッシュに組織情報を読み込む。
     *
     * @param isNewOnly 新規キャッシュのみ？ (true:キャッシュ済みの場合は何もしない, false:キャッシュ済みでも上書きで読み込む)
     */
    public static void createCacheOrganization(boolean isNewOnly) {
        // 読み込み中の場合は完了を待つだけにする。
        if (waitComp(OrganizationInfoEntity.class)) {
            return;
        }

        CashManager cache = CashManager.getInstance();
        if (isNewOnly && cache.isExist(OrganizationInfoEntity.class)) {
            return;
        }

        Logger logger = LogManager.getLogger();
        logger.debug("createCacheOrganization start.");
        try {
            readClasses.add(OrganizationInfoEntity.class);

            cache.setNewCashList(OrganizationInfoEntity.class);
            cache.clearList(OrganizationInfoEntity.class);

            OrganizationInfoFacade facade = new OrganizationInfoFacade();
            Long infoCount = facade.count();
            for (long count = 0; count < infoCount; count += RANGE) {
                List<OrganizationInfoEntity> entities = facade.findRange(count, count + RANGE - 1);
                entities.stream().forEach((entity) -> {
                    cache.setItem(OrganizationInfoEntity.class, entity.getOrganizationId(), entity);
                });
            }
        } finally {
            readClasses.remove(OrganizationInfoEntity.class);
            logger.debug("createCacheOrganization end.");
        }
    }

    /**
     * キャッシュに組織情報を読み込む。
     *
     * @param isNewOnly 新規キャッシュのみ？ (true:キャッシュ済みの場合は何もしない, false:キャッシュ済みでも上書きで読み込む)
     */
    public static void createCacheKanbanHierarchy(boolean isNewOnly) {
        final long MAX_LOAD_SIZE = 20;

        // 読み込み中の場合は完了を待つだけにする。
        if (waitComp(KanbanHierarchyInfoEntity.class)) {
            return;
        }

        CashManager cache = CashManager.getInstance();
        if (isNewOnly && cache.isExist(KanbanHierarchyInfoEntity.class)) {
            return;
        }

        Logger logger = LogManager.getLogger();
        logger.debug("createCacheKanbanHierarchy start.");
        try {
            readClasses.add(KanbanHierarchyInfoEntity.class);

            cache.setNewCashList(KanbanHierarchyInfoEntity.class);
            cache.clearList(KanbanHierarchyInfoEntity.class);

            KanbanHierarchyInfoFacade facade = new KanbanHierarchyInfoFacade();
            Long count = facade.getTopHierarchyCount();
            for (long from = 0; from < count; from += MAX_LOAD_SIZE) {
                List<KanbanHierarchyInfoEntity> entities = facade.getTopHierarchyRange(from, from + MAX_LOAD_SIZE - 1);
                entities.forEach((entity) -> {
                    cache.setItem(KanbanHierarchyInfoEntity.class, entity.getKanbanHierarchyId(), entity);
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            readClasses.remove(KanbanHierarchyInfoEntity.class);
            logger.debug("createCacheKanbanHierarchy end.");
        }
    }


    /**
     * キャッシュに休憩時間情報を読み込む。
     *
     * @param isNewOnly 新規キャッシュのみ？ (true:キャッシュ済みの場合は何もしない, false:キャッシュ済みでも上書きで読み込む)
     */
    public static void createCacheBreakTime(boolean isNewOnly) {
        // 読み込み中の場合は完了を待つだけにする。
        if (waitComp(BreakTimeInfoEntity.class)) {
            return;
        }

        CashManager cache = CashManager.getInstance();
        if (isNewOnly && cache.isExist(BreakTimeInfoEntity.class)) {
            return;
        }

        Logger logger = LogManager.getLogger();
        logger.debug("createCacheBreakTime start.");
        try {
            readClasses.add(BreakTimeInfoEntity.class);

            cache.setNewCashList(BreakTimeInfoEntity.class);
            cache.clearList(BreakTimeInfoEntity.class);

            BreaktimeInfoFacade facade = new BreaktimeInfoFacade();
            List<BreakTimeInfoEntity> entities = facade.findAll();
            entities.stream().forEach((entity) -> {
                cache.setItem(BreakTimeInfoEntity.class, entity.getBreaktimeId(), entity);
            });
        } finally {
            readClasses.remove(BreakTimeInfoEntity.class);
            logger.debug("createCacheBreakTime end.");
        }
    }

    /**
     * キャッシュに遅延理由情報を読み込む。
     *
     * @param isNewOnly 新規キャッシュのみ？ (true:キャッシュ済みの場合は何もしない, false:キャッシュ済みでも上書きで読み込む)
     */
    public static void createCacheDelayReason(boolean isNewOnly) {
        // 読み込み中の場合は完了を待つだけにする。
        if (waitComp(DelayReasonInfoEntity.class)) {
            return;
        }

        CashManager cache = CashManager.getInstance();
        if (isNewOnly && cache.isExist(DelayReasonInfoEntity.class)) {
            return;
        }

        Logger logger = LogManager.getLogger();
        logger.debug("createCacheDelayReason start.");
        try {
            readClasses.add(DelayReasonInfoEntity.class);

            cache.setNewCashList(DelayReasonInfoEntity.class);
            cache.clearList(DelayReasonInfoEntity.class);

            DelayReasonInfoFacade facade = new DelayReasonInfoFacade();
            List<DelayReasonInfoEntity> entities = facade.findAll();
            entities.stream().forEach((entity) -> {
                cache.setItem(DelayReasonInfoEntity.class, entity.getDelaytId(), entity);
            });
        } finally {
            readClasses.remove(DelayReasonInfoEntity.class);
            logger.debug("createCacheDelayReason end.");
        }
    }

    /**
     * キャッシュに中断理由情報を読み込む。
     *
     * @param isNewOnly 新規キャッシュのみ？ (true:キャッシュ済みの場合は何もしない, false:キャッシュ済みでも上書きで読み込む)
     */
    public static void createCacheInterruptReason(boolean isNewOnly) {
        // 読み込み中の場合は完了を待つだけにする。
        if (waitComp(InterruptReasonInfoEntity.class)) {
            return;
        }

        CashManager cache = CashManager.getInstance();
        if (isNewOnly && cache.isExist(InterruptReasonInfoEntity.class)) {
            return;
        }

        Logger logger = LogManager.getLogger();
        logger.debug("createCacheInterruptReason start.");
        try {
            readClasses.add(InterruptReasonInfoEntity.class);

            cache.setNewCashList(InterruptReasonInfoEntity.class);
            cache.clearList(InterruptReasonInfoEntity.class);

            InterruptReasonInfoFacade facade = new InterruptReasonInfoFacade();
            List<InterruptReasonInfoEntity> entities = facade.findAll();
            entities.stream().forEach((entity) -> {
                cache.setItem(InterruptReasonInfoEntity.class, entity.getInterruptId(), entity);
            });
        } finally {
            readClasses.remove(InterruptReasonInfoEntity.class);
            logger.debug("createCacheInterruptReason end.");
        }
    }

    /**
     * キャッシュに休日情報を読み込む。
     *
     * @param isNewOnly 新規キャッシュのみ？ (true:キャッシュ済みの場合は何もしない, false:キャッシュ済みでも上書きで読み込む)
     */
    public static void createCacheHoliday(boolean isNewOnly) {
        // 読み込み中の場合は完了を待つだけにする。
        if (waitComp(HolidayInfoEntity.class)) {
            return;
        }

        CashManager cache = CashManager.getInstance();
        if (isNewOnly && cache.isExist(HolidayInfoEntity.class)) {
            return;
        }

        Logger logger = LogManager.getLogger();
        logger.debug("createCacheHoliday start.");
        try {
            readClasses.add(HolidayInfoEntity.class);

            cache.setNewCashList(HolidayInfoEntity.class);
            cache.clearList(HolidayInfoEntity.class);

            HolidayInfoFacade facade = new HolidayInfoFacade();
            Long infoCount = facade.count();
            for (long count = 0; count < infoCount; count += RANGE2) {
                List<HolidayInfoEntity> entities = facade.findRange(count, count + RANGE2 - 1);
                entities.stream().forEach((entity) -> {
                    cache.setItem(HolidayInfoEntity.class, entity.getHolidayId(), entity);
                });
            }
        } finally {
            readClasses.remove(HolidayInfoEntity.class);
            logger.debug("createCacheHoliday end.");
        }
    }

    /**
     * キャッシュにラベル情報を読み込む。
     *
     * @param isNewOnly 新規キャッシュのみ？ (true:キャッシュ済みの場合は何もしない, false:キャッシュ済みでも上書きで読み込む)
     */
    public static void createCacheLabel(boolean isNewOnly) {
        // 読み込み中の場合は完了を待つだけにする。
        if (waitComp(LabelInfoEntity.class)) {
            return;
        }

        CashManager cache = CashManager.getInstance();
        if (isNewOnly && cache.isExist(LabelInfoEntity.class)) {
            return;
        }

        Logger logger = LogManager.getLogger();
        logger.debug("createCacheLabel start.");
        try {
            readClasses.add(LabelInfoEntity.class);

            cache.setNewCashList(LabelInfoEntity.class);
            cache.clearList(LabelInfoEntity.class);

            LabelInfoFacade facade = new LabelInfoFacade();
            List<LabelInfoEntity> entities = facade.findRange(null, null);
            entities.stream().forEach((entity) -> {
                cache.setItem(LabelInfoEntity.class, entity.getLabelId(), entity);
            });
        } finally {
            readClasses.remove(LabelInfoEntity.class);
            logger.debug("createCacheLabel end.");
        }
    }

    /**
     * 指定した設備IDの設備情報を取得する。
     *
     * @param equipmentId 設備ID
     * @return 設備情報
     */
    public static EquipmentInfoEntity getCacheEquipment(Long equipmentId) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheEquipment: {}", equipmentId);

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(EquipmentInfoEntity.class)) {
            cache.setNewCashList(EquipmentInfoEntity.class);
            createCacheData(EquipmentInfoEntity.class, false);
        }

        // キャッシュにない場合は、取得してキャッシュに追加する。
        if (!cache.isKey(EquipmentInfoEntity.class, equipmentId)) {
            EquipmentInfoFacade facade = new EquipmentInfoFacade();
            EquipmentInfoEntity entity = facade.find(equipmentId);
            if (Objects.nonNull(entity.getEquipmentId())) {
                cache.setItem(EquipmentInfoEntity.class, entity.getEquipmentId(), entity);
            }
        }

        return (EquipmentInfoEntity) cache.getItem(EquipmentInfoEntity.class, equipmentId);
    }

    /**
     * 指定した設備IDの設備情報一覧を取得する。
     *
     * @param equipmentIds 設備ID一覧
     * @return 設備情報一覧
     */
    public static List<EquipmentInfoEntity> getCacheEquipment(List<Long> equipmentIds) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheEquipment: {}", equipmentIds);

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(EquipmentInfoEntity.class)) {
            cache.setNewCashList(EquipmentInfoEntity.class);
            createCacheData(EquipmentInfoEntity.class, false);
        }

        // キャッシュにない設備ID
        List<Long> ids = equipmentIds.stream()
                .filter(id -> !cache.isKey(EquipmentInfoEntity.class, id))
                .collect(Collectors.toList());

        // キャッシュにない設備を取得してキャッシュに追加する。
        if (!ids.isEmpty()) {
            EquipmentInfoFacade facade = new EquipmentInfoFacade();

            for (int count = 0; count < ids.size(); count += ID_RANGE) {
                int toId = count + ID_RANGE;
                if (toId > (ids.size())) {
                    toId = ids.size();
                }

                List<Long> rangeIds = ids.subList(count, toId);

                List<EquipmentInfoEntity> entities = facade.find(rangeIds);
                entities.stream().forEach((entity) -> {
                    cache.setItem(EquipmentInfoEntity.class, entity.getEquipmentId(), entity);
                });
            }
        }

        return equipmentIds.stream()
                .map(id -> (EquipmentInfoEntity) cache.getItem(EquipmentInfoEntity.class, id))
                .filter(equipment -> Objects.nonNull(equipment))// 存在しない設備IDの設備情報がnullで入っているので除外する。
                .collect(Collectors.toList());
    }

    /**
     * 指定した組織IDの組織情報を取得する。
     *
     * @param organizationId 組織ID
     * @return 組織情報
     */
    public static OrganizationInfoEntity getCacheOrganization(Long organizationId) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheOrganization: {}", organizationId);

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(OrganizationInfoEntity.class)) {
            cache.setNewCashList(OrganizationInfoEntity.class);
            createCacheData(OrganizationInfoEntity.class, false);
        }

        // キャッシュにない場合は、取得してキャッシュに追加する。
        if (!cache.isKey(OrganizationInfoEntity.class, organizationId)) {
            OrganizationInfoFacade facade = new OrganizationInfoFacade();
            OrganizationInfoEntity entity = facade.find(organizationId);
            if (Objects.nonNull(entity.getOrganizationId())) {
                cache.setItem(OrganizationInfoEntity.class, entity.getOrganizationId(), entity);
            }
        }

        return (OrganizationInfoEntity) cache.getItem(OrganizationInfoEntity.class, organizationId);
    }

    /**
     * 指定した組織IDの組織情報一覧を取得する。
     *
     * @param organizationIds 組織ID一覧
     * @return 組織情報一覧
     */
    public static List<OrganizationInfoEntity> getCacheOrganization(List<Long> organizationIds) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheOrganization: {}", organizationIds);

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(OrganizationInfoEntity.class)) {
            cache.setNewCashList(OrganizationInfoEntity.class);
            createCacheData(OrganizationInfoEntity.class, false);
        }

        // キャッシュにない組織ID
        List<Long> ids = organizationIds.stream()
                .filter(id -> !cache.isKey(OrganizationInfoEntity.class, id))
                .collect(Collectors.toList());

        // キャッシュにない組織を取得してキャッシュに追加する。
        if (!ids.isEmpty()) {
            OrganizationInfoFacade facade = new OrganizationInfoFacade();

            for (int count = 0; count < ids.size(); count += ID_RANGE) {
                int toId = count + ID_RANGE;
                if (toId > (ids.size())) {
                    toId = ids.size();
                }

                List<Long> rangeIds = ids.subList(count, toId);

                List<OrganizationInfoEntity> entities = facade.find(rangeIds);
                entities.stream().forEach((entity) -> {
                    cache.setItem(OrganizationInfoEntity.class, entity.getOrganizationId(), entity);
                });
            }
        }

        return organizationIds.stream()
                .map(id -> (OrganizationInfoEntity) cache.getItem(OrganizationInfoEntity.class, id))
                .filter(equipment -> Objects.nonNull(equipment))// 存在しない組織IDの組織情報がnullで入っているので除外する。
                .collect(Collectors.toList());
    }

    /**
     * 指定したカンバン階層IDのカンバン階層情報一覧を取得する。
     *
     * @param kanbanHierarchyIds カンバン階層ID一覧
     * @return カンバン階層情報一覧
     */
    public static List<KanbanHierarchyInfoEntity> getCacheKanbanHierarchy(List<Long> kanbanHierarchyIds) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheOrganization: {}", kanbanHierarchyIds);

        CashManager cache = CashManager.getInstance();
        if (!cache.isExist(KanbanHierarchyInfoEntity.class)) {
            cache.setNewCashList(KanbanHierarchyInfoEntity.class);
            createCacheData(KanbanHierarchyInfoEntity.class, false);
        }

        // キャッシュにない組織ID
        List<Long> ids = kanbanHierarchyIds.stream()
                .filter(id -> !cache.isKey(KanbanHierarchyInfoEntity.class, id))
                .collect(Collectors.toList());

        // キャッシュにない組織を取得してキャッシュに追加する。
        if (!ids.isEmpty()) {
            KanbanHierarchyInfoFacade facade = new KanbanHierarchyInfoFacade();
            List<KanbanHierarchyInfoEntity> kanbanHierarchyInfoEntities = facade.findHierarchyIds(ids);
            kanbanHierarchyInfoEntities.forEach(entity->
                    cache.setItem(KanbanHierarchyInfoEntity.class, entity.getKanbanHierarchyId(), entity));
        }

        return kanbanHierarchyIds.stream()
                .map(id -> (KanbanHierarchyInfoEntity) cache.getItem(KanbanHierarchyInfoEntity.class, id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 指定した休憩時間IDの休憩時間情報を取得する。
     *
     * @param breaktimeId 休憩時間ID
     * @return 休憩時間情報
     */
    public static BreakTimeInfoEntity getCacheBreakTime(Long breaktimeId) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheBreakTime: {}", breaktimeId);

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(BreakTimeInfoEntity.class)) {
            cache.setNewCashList(BreakTimeInfoEntity.class);
            createCacheData(BreakTimeInfoEntity.class, false);
        }

        // キャッシュにない場合は、取得してキャッシュに追加する。
        if (!cache.isKey(BreakTimeInfoEntity.class, breaktimeId)) {
            BreaktimeInfoFacade facade = new BreaktimeInfoFacade();
            BreakTimeInfoEntity entity = facade.find(breaktimeId);
            if (Objects.nonNull(entity.getBreaktimeId())) {
                cache.setItem(BreakTimeInfoEntity.class, entity.getBreaktimeId(), entity);
            }
        }

        return (BreakTimeInfoEntity) cache.getItem(BreakTimeInfoEntity.class, breaktimeId);
    }

    /**
     * 指定した遅延理由IDの遅延理由情報を取得する。
     *
     * @param delayId 遅延理由ID
     * @return 遅延理由情報
     */
    public static DelayReasonInfoEntity getCacheDelayReason(Long delayId) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheDelayReason: {}", delayId);

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(DelayReasonInfoEntity.class)) {
            cache.setNewCashList(DelayReasonInfoEntity.class);
            createCacheData(DelayReasonInfoEntity.class, false);
        }

        // キャッシュにない場合は、取得してキャッシュに追加する。
        if (!cache.isKey(DelayReasonInfoEntity.class, delayId)) {
            DelayReasonInfoFacade facade = new DelayReasonInfoFacade();
            DelayReasonInfoEntity entity = facade.find(delayId);
            if (Objects.nonNull(entity.getDelaytId())) {
                cache.setItem(DelayReasonInfoEntity.class, entity.getDelaytId(), entity);
            }
        }

        return (DelayReasonInfoEntity) cache.getItem(DelayReasonInfoEntity.class, delayId);
    }

    /**
     * 指定した中断理由IDの中断理由情報を取得する。
     *
     * @param interruptId 中断理由ID
     * @return 中断理由情報
     */
    public static InterruptReasonInfoEntity getCacheInterruptReason(Long interruptId) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheInterruptReason: {}", interruptId);

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(InterruptReasonInfoEntity.class)) {
            cache.setNewCashList(InterruptReasonInfoEntity.class);
            createCacheData(InterruptReasonInfoEntity.class, false);
        }

        // キャッシュにない場合は、取得してキャッシュに追加する。
        if (!cache.isKey(InterruptReasonInfoEntity.class, interruptId)) {
            InterruptReasonInfoFacade facade = new InterruptReasonInfoFacade();
            InterruptReasonInfoEntity entity = facade.find(interruptId);
            if (Objects.nonNull(entity.getInterruptId())) {
                cache.setItem(InterruptReasonInfoEntity.class, entity.getInterruptId(), entity);
            }
        }

        return (InterruptReasonInfoEntity) cache.getItem(InterruptReasonInfoEntity.class, interruptId);
    }

    /**
     * 指定したラベルIDのラベル情報を取得する。
     *
     * @param labelId ラベルID
     * @return ラベル情報
     */
    public static LabelInfoEntity getCacheLabel(Long labelId) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheLabel: {}", labelId);

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(LabelInfoEntity.class)) {
            cache.setNewCashList(LabelInfoEntity.class);
            createCacheData(LabelInfoEntity.class, false);
        }

        // キャッシュにない場合は、取得してキャッシュに追加する。
        if (!cache.isKey(LabelInfoEntity.class, labelId)) {
            LabelInfoFacade facade = new LabelInfoFacade();
            LabelInfoEntity entity = facade.find(labelId);
            if (Objects.nonNull(entity.getLabelId())) {
                cache.setItem(LabelInfoEntity.class, entity.getLabelId(), entity);
            }
        }

        return (LabelInfoEntity) cache.getItem(LabelInfoEntity.class, labelId);
    }
    
    /**
     * 全てのラベル情報を取得する。
     * 
     * @return ラベル情報
     */
    public static List<LabelInfoEntity> getCacheLabels() {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheLabels.");

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(LabelInfoEntity.class)) {
            cache.setNewCashList(LabelInfoEntity.class);
            createCacheData(LabelInfoEntity.class, false);
        }
        
        return cache.getItemList(LabelInfoEntity.class, new ArrayList<>());
    }

    /**
     * 指定した工程順IDの工程順情報を取得する。
     *
     * @param workflowId 工程順ID
     * @return 工程順情報
     */
    public static WorkflowInfoEntity getCacheWorkflow(Long workflowId) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheWorkflow: {}", workflowId);

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(WorkflowInfoEntity.class)) {
            cache.setNewCashList(WorkflowInfoEntity.class);
        }

        // キャッシュにない場合は、取得してキャッシュに追加する。
        if (!cache.isKey(WorkflowInfoEntity.class, workflowId)) {
            WorkflowInfoFacade facade = new WorkflowInfoFacade();
            WorkflowInfoEntity entity = facade.find(workflowId);
            if (Objects.nonNull(entity.getWorkflowId())) {
                cache.setItem(WorkflowInfoEntity.class, entity.getWorkflowId(), entity);
            }
        }

        return (WorkflowInfoEntity) cache.getItem(WorkflowInfoEntity.class, workflowId);
    }

    /**
     * 指定した工程IDの工程情報を取得する。
     *
     * @param workId 工程ID
     * @return 工程情報
     */
    public static WorkInfoEntity getCacheWork(Long workId) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheWork: {}", workId);

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(WorkInfoEntity.class)) {
            cache.setNewCashList(WorkInfoEntity.class);
        }

        // キャッシュにない場合は、取得してキャッシュに追加する。
        if (!cache.isKey(WorkInfoEntity.class, workId)) {
            WorkInfoFacade facade = new WorkInfoFacade();
            WorkInfoEntity entity = facade.find(workId);
            if (Objects.nonNull(entity.getWorkId())) {
                cache.setItem(WorkInfoEntity.class, entity.getWorkId(), entity);
            }
        }

        return (WorkInfoEntity) cache.getItem(WorkInfoEntity.class, workId);
    }


    /**
     * 指定した作業区分IDの作業区分情報を取得する。
     *
     * @param workCategoryId 作業区分ID
     * @return 作業区分情報
     */
    public static WorkCategoryInfoEntity getCacheWorkCategory(Long workCategoryId) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheWorkCategory: {}", workCategoryId);

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(WorkCategoryInfoEntity.class)) {
            cache.setNewCashList(WorkCategoryInfoEntity.class);
        }

        // キャッシュにない場合は、取得してキャッシュに追加する。
        if (!cache.isKey(WorkCategoryInfoEntity.class, workCategoryId)) {
            WorkCategoryInfoFacade facade = new WorkCategoryInfoFacade();
            WorkCategoryInfoEntity entity = facade.find(workCategoryId);
            if (Objects.nonNull(entity.getWorkCategoryId())) {
                cache.setItem(WorkCategoryInfoEntity.class, entity.getWorkCategoryId(), entity);
            }
        }

        return (WorkCategoryInfoEntity) cache.getItem(WorkCategoryInfoEntity.class, workCategoryId);
    }

    /**
     * 指定した間接作業IDの間接作業情報を取得する。
     *
     * @param indirectWorkId 間接作業ID
     * @return 間接作業情報
     */
    public static IndirectWorkInfoEntity getCacheIndirectWork(Long indirectWorkId) {
        Logger logger = LogManager.getLogger();
        logger.debug("getCacheIndirectWork: {}", indirectWorkId);

        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(IndirectWorkInfoEntity.class)) {
            cache.setNewCashList(IndirectWorkInfoEntity.class);
        }

        // キャッシュにない場合は、取得してキャッシュに追加する。
        if (!cache.isKey(IndirectWorkInfoEntity.class, indirectWorkId)) {
            IndirectWorkInfoFacade facade = new IndirectWorkInfoFacade();
            IndirectWorkInfoEntity entity = facade.find(indirectWorkId);
            if (Objects.nonNull(entity.getIndirectWorkId())) {
                cache.setItem(IndirectWorkInfoEntity.class, entity.getIndirectWorkId(), entity);
            }
        }

        return (IndirectWorkInfoEntity) cache.getItem(IndirectWorkInfoEntity.class, indirectWorkId);
    }
    
    /**
     * 理由区分のキャッシュを作成する。
     * 
     * @param isNewOnly 
     */
    public static void createReasonCetegory(boolean isNewOnly) {
        if (waitComp(ReasonCategoryInfoEntity.class)) {
            return;
        }

        CashManager cache = CashManager.getInstance();
        if (isNewOnly && cache.isExist(ReasonCategoryInfoEntity.class)) {
            return;
        }

        Logger logger = LogManager.getLogger();
        logger.debug("createCacheReasonCetegory start.");
        try {
            readClasses.add(ReasonCategoryInfoEntity.class);
            cache.setNewCashList(ReasonCategoryInfoEntity.class);
            cache.clearList(ReasonCategoryInfoEntity.class);

            ReasonCategoryInfoFacede facade = new ReasonCategoryInfoFacede();
            
            long count = facade.count();
            for (int ii = 0; ii < count; ii += RANGE) {
                List<ReasonCategoryInfoEntity> entities = facade.findRange(ii, ii + (int)RANGE - 1);
                entities.forEach(o -> cache.setItem(ReasonCategoryInfoEntity.class, o.getId(), o));
            }
  
        } finally {
            readClasses.remove(ReasonCategoryInfoEntity.class);
            logger.debug("createCacheReasonCetegory end.");
        }
    }

    public static ReasonCategoryInfoEntity getReasonCetegory(Long id) {
        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(ReasonCategoryInfoEntity.class)) {
            cache.setNewCashList(ReasonCategoryInfoEntity.class);
        }

        if (!cache.isKey(ReasonCategoryInfoEntity.class, id)) {
            ReasonCategoryInfoFacede facade = new ReasonCategoryInfoFacede();
            ReasonCategoryInfoEntity entity = facade.find(id);
            if (Objects.nonNull(entity.getId())) {
                cache.setItem(ReasonCategoryInfoEntity.class, entity.getId(), entity);
            }
        }

        return (ReasonCategoryInfoEntity) cache.getItem(ReasonCategoryInfoEntity.class, id);
    }

    public static List<ReasonCategoryInfoEntity> getReasonCetegory(ReasonTypeEnum reasonType) {
        CashManager cache = CashManager.getInstance();

        if (!cache.isExist(ReasonCategoryInfoEntity.class)) {
            return null;
        }
        
        return null;
    }
}
