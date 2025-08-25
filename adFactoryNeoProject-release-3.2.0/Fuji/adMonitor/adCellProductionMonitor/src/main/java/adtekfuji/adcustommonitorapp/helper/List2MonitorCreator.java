/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.helper;

import adtekfuji.adcustommonitorapp.service.CellProductionMonitorServiceInterface;
import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.property.AdProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorList2InfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.ConUnitAssociateInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * リストモニタ成用クラス
 *
 * @author yu.kikukawa
 * @version 1.7.3
 * @since 2017.07.26
 */
public class List2MonitorCreator implements CellProductionMonitorServiceInterface {

    private final static Logger logger = LogManager.getLogger();
    private TableView<MonitorList2InfoEntity> transitionTable;
    private Label notStarted;
    private Label workmanship;
    private Label shipments;
    private final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade(AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).getProperty(ClientPropertyConstants.ADFACTORY_SERVICE_URI, "http://localhost:8080/adFactoryServer/rest"));
    private final ActualResultInfoFacade actualResultInfoFacade = new ActualResultInfoFacade(AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).getProperty(ClientPropertyConstants.ADFACTORY_SERVICE_URI, "http://localhost:8080/adFactoryServer/rest"));

    List<UnitInfoEntity> unitEntitys = new ArrayList<>();
    List<OrganizationInfoEntity> organizationEntitys = new ArrayList<>();
    List<DisplayedStatusInfoEntity> displayedStatusInfoEntitys = new ArrayList<>();
    Comparator<MonitorList2InfoEntity> comparator = new Comparator<MonitorList2InfoEntity>() {
        @Override
        public int compare(MonitorList2InfoEntity a, MonitorList2InfoEntity b) {
            if (getStatus(a.getKanban1Status(), a.getKanban2Status()).equals(StatusPatternEnum.COMP_NORMAL)
                    && !getStatus(b.getKanban1Status(), b.getKanban2Status()).equals(StatusPatternEnum.COMP_NORMAL)) {
                return 1;
            } else if (!getStatus(a.getKanban1Status(), a.getKanban2Status()).equals(StatusPatternEnum.COMP_NORMAL)
                    && getStatus(b.getKanban1Status(), b.getKanban2Status()).equals(StatusPatternEnum.COMP_NORMAL)) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public void setUnitList(List<UnitInfoEntity> list) {
        unitEntitys.clear();
        unitEntitys.addAll(list);
    }

    public void setOrganizationList(List<OrganizationInfoEntity> list) {
        organizationEntitys.clear();
        organizationEntitys.addAll(list);
    }

    public void setDisplayedStatusList(List<DisplayedStatusInfoEntity> list) {
        displayedStatusInfoEntitys.clear();
        displayedStatusInfoEntitys.addAll(list);
    }

    public void setDate(List<DisplayedStatusInfoEntity> list) {
        displayedStatusInfoEntitys.clear();
        displayedStatusInfoEntitys.addAll(list);
    }

    /**
     * リスト生成処理
     *
     * @param transitionTable リストの一覧を表示する親の画面
     */
    public void createList(TableView<MonitorList2InfoEntity> transitionTable) {
        logger.info(List2MonitorCreator.class.getName() + ":createList2 start");
        this.transitionTable = transitionTable;

        try {
            List<Long> kanbanIds = new ArrayList<>();
            List<Long> subkanbanIds = new ArrayList<>();
            final int block = 100;
            unitEntitys.stream().forEach((unitEntity) -> {
                unitEntity.getConUnitAssociateCollection().stream().forEach((conUnitAssociateEntity) -> {
                    kanbanIds.add(conUnitAssociateEntity.getFkKanbanId());
                });
            });

            // カンバン,実績の取得.
            ArrayList<KanbanInfoEntity> kanbanEntitys = new ArrayList<>();
            ArrayList<ActualResultEntity> actualResultEntitys = new ArrayList<>();
            if (!kanbanIds.isEmpty()) {
                int fromIndex = 0;
                int toIndex = 0;
                for (int i = 0; i <= kanbanIds.size() / block; i++) {
                    fromIndex = i * block;
                    toIndex = Math.min(fromIndex + block, kanbanIds.size());
                    subkanbanIds = kanbanIds.subList(fromIndex, toIndex);
                    kanbanEntitys.addAll(kanbanInfoFacade.find(subkanbanIds));
                    actualResultEntitys.addAll(actualResultInfoFacade.find(subkanbanIds));
                }
            }

            // テーブルに設定.
            List<ConUnitAssociateInfoEntity> conUnitAssociateEntitys = new ArrayList<>();
            ConUnitAssociateInfoEntity conUnitAssociateEntity1;
            ConUnitAssociateInfoEntity conUnitAssociateEntity2;
            Long kanban1Id;
            Long kanban2Id;
            Long organizationId1;
            Long organizationId2;
            Date actualDate1 = new Date(0);
            Date actualDate2 = new Date(0);
            for (UnitInfoEntity unitEntity : unitEntitys) {
                kanban1Id = 0L;
                kanban2Id = 0L;
                // 工程1のカンバンid取得.
                conUnitAssociateEntitys.clear();
                conUnitAssociateEntitys.addAll(unitEntity.getConUnitAssociateCollection());
                if (!conUnitAssociateEntitys.isEmpty()) {
                    conUnitAssociateEntity1 = conUnitAssociateEntitys.stream().min((a, b) -> a.getUnitAssociateOrder() - b.getUnitAssociateOrder()).get();
                    kanban1Id = conUnitAssociateEntity1.getFkKanbanId();
                    conUnitAssociateEntitys.remove(conUnitAssociateEntity1);
                }
                // 工程2のカンバンid取得.
                if (!conUnitAssociateEntitys.isEmpty()) {
                    conUnitAssociateEntity2 = conUnitAssociateEntitys.stream().min((a, b) -> a.getUnitAssociateOrder() - b.getUnitAssociateOrder()).get();
                    kanban2Id = conUnitAssociateEntity2.getFkKanbanId();
                }

                MonitorList2InfoEntity list2Entity = new MonitorList2InfoEntity();
                list2Entity.setKanban1Id(kanban1Id);
                list2Entity.setKanban2Id(kanban2Id);
                for (KanbanInfoEntity kanbanEntity : kanbanEntitys) {
                    // 工程ステータス設定.
                    if (kanban1Id.equals(kanbanEntity.getKanbanId())) {
                        list2Entity.setKanban1Status(kanbanEntity.getKanbanStatus());
                    }
                    if (kanban2Id.equals(kanbanEntity.getKanbanId())) {
                        list2Entity.setKanban2Status(kanbanEntity.getKanbanStatus());
                    }
                }

                organizationId1 = 0L;
                organizationId2 = 0L;
                actualDate1.setTime(0);
                actualDate2.setTime(0);
                // 実績から作業者idを取得.
                for (ActualResultEntity actualResultEntity : actualResultEntitys) {
                    if (kanban1Id.equals(actualResultEntity.getFkKanbanId())) {
                        if (actualResultEntity.getImplementDatetime().after(actualDate1)) {
                            organizationId1 = actualResultEntity.getFkOrganizationId();
                            actualDate1 = (Date) actualResultEntity.getImplementDatetime().clone();
                        }
                    }
                    if (kanban2Id.equals(actualResultEntity.getFkKanbanId())) {
                        if (actualResultEntity.getImplementDatetime().after(actualDate2)) {
                            organizationId2 = actualResultEntity.getFkOrganizationId();
                            actualDate2 = (Date) actualResultEntity.getImplementDatetime().clone();
                        }
                    }
                }
                for (OrganizationInfoEntity organizationEntity : organizationEntitys) {
                    if (organizationId1.equals(organizationEntity.getOrganizationId())) {
                        list2Entity.setOrganization1(organizationEntity);
                    }
                    if (organizationId2.equals(organizationEntity.getOrganizationId())) {
                        list2Entity.setOrganization2(organizationEntity);
                    }
                }
                list2Entity.setUnitPropertyCollection(unitEntity.getUnitPropertyCollection());
                list2Entity.setUnitId(kanban2Id);
                this.transitionTable.getItems().add(list2Entity);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(List2MonitorCreator.class.getName() + ":createList end");
    }

    @Override
    public void receivedActualDataKanbanId(long kanbanId) {
        MonitorList2InfoEntity updateItem = checkCarryKanbanId(kanbanId);
        if (Objects.nonNull(updateItem)) {
            updateThread(updateItem);
        }
    }

    /**
     * カンバンIDを保有しているか確認
     *
     * @param kanbanId 確認するカンバンID
     * @return MonitorList2InfoEntity カンバンIDが一致するエンティティ.
     */
    private MonitorList2InfoEntity checkCarryKanbanId(long kanbanId) {
        logger.info(List2MonitorCreator.class.getName() + ":checkCarryKanbanId start");
        if (Objects.isNull(transitionTable) || Objects.isNull(transitionTable.getItems())) {
            return null;
        }
        for (MonitorList2InfoEntity item : transitionTable.getItems()) {
            if (item.getKanban1Id().equals(kanbanId) || item.getKanban2Id().equals(kanbanId)) {
                return item;
            }
        }
        logger.info(List2MonitorCreator.class.getName() + ":checkCarryKanbanId end");
        return null;
    }

    /**
     * 更新処理
     *
     */
    private void updateThread(MonitorList2InfoEntity item) {
        logger.info(List2MonitorCreator.class.getName() + ":updateThread start");
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // カンバン,実績の取得.
                    List<Long> kanbanIds = Arrays.asList(item.getKanban1Id(), item.getKanban2Id());
                    List<Long> subkanbanIds = new ArrayList<>();
                    final int block = 100;
                    //List<KanbanInfoEntity> kanbanEntitys = kanbanInfoFacade.find(kanbanIds);
                    //List<ActualResultEntity> actualResultEntitys = actualResultInfoFacade.find(kanbanIds);
                    ArrayList<KanbanInfoEntity> kanbanEntitys = new ArrayList<>();
                    ArrayList<ActualResultEntity> actualResultEntitys = new ArrayList<>();
                    if (!kanbanIds.isEmpty()) {
                        int fromIndex = 0;
                        int toIndex = 0;
                        for (int i = 0; i <= kanbanIds.size() / block; i++) {
                            fromIndex = i * block;
                            toIndex = Math.min(fromIndex + block, kanbanIds.size());
                            subkanbanIds = kanbanIds.subList(fromIndex, toIndex);
                            kanbanEntitys.addAll(kanbanInfoFacade.find(subkanbanIds));
                            actualResultEntitys.addAll(actualResultInfoFacade.find(subkanbanIds));
                        }
                    }

                    for (KanbanInfoEntity kanbanEntity : kanbanEntitys) {
                        // 工程ステータス設定.
                        if (item.getKanban1Id().equals(kanbanEntity.getKanbanId())) {
                            item.setKanban1Status(kanbanEntity.getKanbanStatus());
                        }
                        if (item.getKanban2Id().equals(kanbanEntity.getKanbanId())) {
                            item.setKanban2Status(kanbanEntity.getKanbanStatus());
                        }
                    }
                    Long organizationId1 = 0L;
                    Long organizationId2 = 0L;
                    Date actualDate1 = new Date(0);
                    Date actualDate2 = new Date(0);

                    // 実績から作業者idを取得.
                    for (ActualResultEntity actualResultEntity : actualResultEntitys) {
                        if (item.getKanban1Id().equals(actualResultEntity.getFkKanbanId())) {
                            if (actualResultEntity.getImplementDatetime().after(actualDate1)) {
                                organizationId1 = actualResultEntity.getFkOrganizationId();
                                actualDate1 = (Date) actualResultEntity.getImplementDatetime().clone();
                            }
                        }
                        if (item.getKanban2Id().equals(actualResultEntity.getFkKanbanId())) {
                            if (actualResultEntity.getImplementDatetime().after(actualDate2)) {
                                organizationId2 = actualResultEntity.getFkOrganizationId();
                                actualDate2 = (Date) actualResultEntity.getImplementDatetime().clone();
                            }
                        }
                    }
                    for (OrganizationInfoEntity organizationEntity : organizationEntitys) {
                        if (organizationId1.equals(organizationEntity.getOrganizationId())) {
                            item.setOrganization1(organizationEntity);
                        }
                        if (organizationId2.equals(organizationEntity.getOrganizationId())) {
                            item.setOrganization2(organizationEntity);
                        }
                    }

                    Platform.runLater(() -> {
                        if (Objects.isNull(transitionTable) || Objects.isNull(transitionTable.getItems()) || transitionTable.getItems().isEmpty()) {
                            return;
                        }
                        // 表の値を変えても表示は更新されない. 1行目を差替えてやると更新されるので無意味だけど1行目を入れ直し.
                        transitionTable.getItems().set(0, new MonitorList2InfoEntity().copy(transitionTable.getItems().get(0)));

                        // テーブルの基本のソート.
                        transitionTable.sort();

                        // 完了したユニットは下に持っていくソート.
                        sort();

                        // 残工程更新.
                        drawWork();
                    });
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                logger.info(List2MonitorCreator.class.getName() + ":updateThread end");
                return null;
            }
        };
        new Thread(task).start();
    }

    private void blockUI(Boolean isBlockUI) {
        if (Objects.isNull(transitionTable)) {
            return;
        }
        transitionTable.setDisable(isBlockUI);
    }

    /**
     * ユニットのステータス
     *
     * @param status1
     * @param status2
     * @return
     */
    public static StatusPatternEnum getStatus(KanbanStatusEnum status1, KanbanStatusEnum status2) {

        if (Objects.isNull(status1) && Objects.isNull(status2)) {
            return StatusPatternEnum.PLAN_NORMAL;
        }

        if (Objects.isNull(status1)) {
            return getStatus(status2);
        }

        if (Objects.isNull(status2)) {
            return getStatus(status1);
        }

        // 中断、その他.
        if (status1.equals(KanbanStatusEnum.SUSPEND) || status2.equals(KanbanStatusEnum.SUSPEND) || status1.equals(KanbanStatusEnum.OTHER) || status2.equals(KanbanStatusEnum.OTHER)) {
            return StatusPatternEnum.SUSPEND_NORMAL;
        }

        // 作業中.
        if (status1.equals(KanbanStatusEnum.WORKING) || status2.equals(KanbanStatusEnum.WORKING)) {
            return StatusPatternEnum.WORK_NORMAL;
        }

        // 計画中、計画済み.
        if (status1.equals(KanbanStatusEnum.PLANNING) || status2.equals(KanbanStatusEnum.PLANNING) || status1.equals(KanbanStatusEnum.PLANNED) || status2.equals(KanbanStatusEnum.PLANNED)) {
            return StatusPatternEnum.PLAN_NORMAL;
        }

        // 完了、中止.
        return StatusPatternEnum.COMP_NORMAL;
    }

    /**
     * ユニットのステータス
     *
     * @param status
     * @return
     */
    public static StatusPatternEnum getStatus(KanbanStatusEnum status) {
        if (Objects.isNull(status)) {
            return StatusPatternEnum.PLAN_NORMAL;
        }
        // 中断、その他.
        if (status.equals(KanbanStatusEnum.SUSPEND) || status.equals(KanbanStatusEnum.OTHER)) {
            return StatusPatternEnum.SUSPEND_NORMAL;
        }

        // 作業中.
        if (status.equals(KanbanStatusEnum.WORKING)) {
            return StatusPatternEnum.WORK_NORMAL;
        }

        // 計画中、計画済み.
        if (status.equals(KanbanStatusEnum.PLANNING) || status.equals(KanbanStatusEnum.PLANNED)) {
            return StatusPatternEnum.PLAN_NORMAL;
        }

        // 完了、中止.
        return StatusPatternEnum.COMP_NORMAL;
    }

    public void sort() {
        if (Objects.isNull(transitionTable) || Objects.isNull(transitionTable.getItems()) || transitionTable.getItems().isEmpty()) {
            return;
        }
        FXCollections.sort(transitionTable.getItems(), comparator);
    }

    public void createHeader(Label notStarted, Label workmanship, Label shipments) {
        this.notStarted = notStarted;
        this.workmanship = workmanship;
        this.shipments = shipments;
        drawWork();
    }

    /**
     * 残工程描画
     *
     */
    private void drawWork() {
        if (Objects.isNull(transitionTable) || Objects.isNull(transitionTable.getItems()) || transitionTable.getItems().isEmpty()) {
            return;
        }

        Platform.runLater(() -> {

            int countNotStart = 0;
            int countStart = 0;
            for (MonitorList2InfoEntity entity : transitionTable.getItems()) {
                if (Objects.nonNull(entity.getKanban1Status()) && (Objects.isNull(entity.getKanban2Status()))) {
                    // カンバン1あり、カンバン2なし
                    // 未着手 カンバン1が計画中または計画済み
                    // 仕掛り カンバン1が完了でない
                    if (entity.getKanban1Status().equals(KanbanStatusEnum.PLANNING) || entity.getKanban1Status().equals(KanbanStatusEnum.PLANNED)) {
                        countNotStart++;
                    } else if (!entity.getKanban1Status().equals(KanbanStatusEnum.COMPLETION)) {
                        countStart++;
                    }
                } else if (Objects.isNull(entity.getKanban1Status()) && (Objects.nonNull(entity.getKanban2Status()))) {
                    // カンバン1なし、カンバン2あり
                    // 未着手 カンバン2が計画中または計画済み
                    // 仕掛り カンバン2が完了でない
                    if (entity.getKanban2Status().equals(KanbanStatusEnum.PLANNING) || entity.getKanban2Status().equals(KanbanStatusEnum.PLANNED)) {
                        countNotStart++;
                    } else if (!entity.getKanban2Status().equals(KanbanStatusEnum.COMPLETION)) {
                        countStart++;
                    }
                } else if (Objects.nonNull(entity.getKanban1Status()) && (Objects.nonNull(entity.getKanban2Status()))) {
                    // カンバン1あり、カンバン2あり
                    // 未着手 カンバン1、2の両方が計画中または計画済み
                    if ((entity.getKanban1Status().equals(KanbanStatusEnum.PLANNING) || entity.getKanban1Status().equals(KanbanStatusEnum.PLANNED))
                            && (entity.getKanban2Status().equals(KanbanStatusEnum.PLANNING) || entity.getKanban2Status().equals(KanbanStatusEnum.PLANNED))) {
                        countNotStart++;
                    } else if (!(entity.getKanban1Status().equals(KanbanStatusEnum.COMPLETION) && entity.getKanban2Status().equals(KanbanStatusEnum.COMPLETION))) {
                        countStart++;
                    }
                }
            }
            notStarted.setText(" " + String.valueOf(countNotStart));
            workmanship.setText(" " + String.valueOf(countStart));
            // 未着手 カンバン1、2の両方が計画中または計画済み
            //notStarted.setText(" " + String.valueOf(transitionTable.getItems().stream().filter((entity)
            //        -> ((entity.getKanban1Status().equals(KanbanStatusEnum.PLANNING) || entity.getKanban1Status().equals(KanbanStatusEnum.PLANNED))
            //        && (entity.getKanban2Status().equals(KanbanStatusEnum.PLANNING) || entity.getKanban2Status().equals(KanbanStatusEnum.PLANNED)))
            //).count()));
            // 仕掛り カンバン1、2の両方が計画中または計画済みでない。かつ、カンバン1、2の両方が完了でない
            //workmanship.setText(" " + String.valueOf(transitionTable.getItems().stream().filter((entity)
            //        -> !((entity.getKanban1Status().equals(KanbanStatusEnum.PLANNING) || entity.getKanban1Status().equals(KanbanStatusEnum.PLANNED))
            //        && (entity.getKanban2Status().equals(KanbanStatusEnum.PLANNING) || entity.getKanban2Status().equals(KanbanStatusEnum.PLANNED)))
            //        && !(entity.getKanban1Status().equals(KanbanStatusEnum.COMPLETION) && entity.getKanban2Status().equals(KanbanStatusEnum.COMPLETION))
            //).count()));
            // 出荷件数 テーブルに含まれるユニットの数
            shipments.setText(" " + String.valueOf(transitionTable.getItems().size()));
        });
    }
}
