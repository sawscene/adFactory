/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.javafx;

import adtekfuji.admanagerapp.analysisplugin.common.AnalysisWorkFilterData;
import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 正規分布生グラフ成用クラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.07.29.Fri
 */
public class NormalDistributionCreater {

    private final Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final Map<String, NomalDistribtionData> workActualData = new HashMap<>();
    private final List<ActualResultEntity> actualResultEntitys = new ArrayList<>();
    private final TilePane graphPane;
    private final AnalysisWorkFilterData settingData;

    /**
     * 正規分布表示用データを取得する。
     *
     * @return 
     */
    public Map<String, NomalDistribtionData> getWorkActualData() {
        return this.workActualData;
    }

    /**
     * 
     * @param actualResultEntitys
     * @param graphPane
     * @param settingData 
     */
    public NormalDistributionCreater(List<ActualResultEntity> actualResultEntitys, TilePane graphPane, AnalysisWorkFilterData settingData) {
        this.settingData = settingData;
        if (Objects.nonNull(actualResultEntitys)) {
            this.actualResultEntitys.addAll(actualResultEntitys);
            this.sortWorkActualData();
        }
        this.graphPane = graphPane;
    }

    /**
     * グラフ生成処理
     *
     */
    public void createGraph() {
        logger.info(NormalDistributionCreater.class.getName() + ":createGraph start");

        try {
            this.graphPane.getChildren().clear();
            for (Map.Entry<String, NomalDistribtionData> entrySet : workActualData.entrySet()) {
                NormaldistributionController controller = new NormaldistributionController();
                FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/fxml/fx/normaldistribution.fxml"), rb);
                fXMLLoader.setController(controller);
                AnchorPane root = fXMLLoader.load();
                controller.setArgument(entrySet.getValue());
                graphPane.getChildren().add(root);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(NormalDistributionCreater.class.getName() + ":createGraph end");
    }

    /**
     * 工程ごとに実績をソートする
     *
     * @param actualResultEntitys
     */
    private void sortWorkActualData() {
        logger.info(NormalDistributionCreater.class.getName() + ":sortWorkActualData start");

        // 中断と完了の実績時間を足してして一つの実績にする
        // 作業時間が作業状態によって別々で記録されるため下記の処理を追加する
        Map<String, ActualResultEntity> results = new HashMap<>();
        for (ActualResultEntity result : actualResultEntitys) {
            if (!results.containsKey(result.getKanbanName() + result.getWorkName())) {
                result.setSumTime((long) result.getWorkingTime());
            } else {
                ActualResultEntity target = results.get(result.getKanbanName() + result.getWorkName());
                if (result.getFkWorkId().equals(target.getFkWorkId())
                        && result.getFkKanbanId().equals(target.getFkKanbanId())
                        && result.getFkWorkKanbanId().equals(target.getFkWorkKanbanId())
                        && !result.getActualId().equals(target.getActualId())) {
                    ActualResultEntity actualResultEntity
                            = new ActualResultEntity(target.getFkKanbanId(), target.getFkWorkKanbanId(), target.getImplementDatetime(), target.getTransactionId(),
                                    target.getFkEquipmentId(), target.getFkOrganizationId(), target.getFkWorkflowId(), target.getFkWorkId(), KanbanStatusEnum.COMPLETION, null, null);
                    actualResultEntity.setKanbanName(target.getKanbanName());
                    actualResultEntity.setWorkflowName(target.getWorkflowName());
                    actualResultEntity.setWorkName(target.getWorkName());
                    actualResultEntity.setOrganizationName(target.getOrganizationName());
                    actualResultEntity.setEquipmentName(target.getEquipmentName());
                    actualResultEntity.setTaktTime(target.getTaktTime());
                    if (!target.getOrganizationName().contains(result.getOrganizationName())) {
                        actualResultEntity.setOrganizationName(target.getOrganizationName() + "," + result.getOrganizationName());
                    }
                    if (!target.getEquipmentName().contains(result.getEquipmentName())) {
                        actualResultEntity.setEquipmentName(target.getEquipmentName() + "," + result.getEquipmentName());
                    }
                    if (Objects.nonNull(target.getInterruptReason())) {
                        if (Objects.nonNull(result.getInterruptReason())) {
                            if (!target.getInterruptReason().contains(result.getInterruptReason())) {
                                actualResultEntity.setInterruptReason(target.getInterruptReason() + "," + result.getInterruptReason());
                            }
                        }
                    } else {
                        if (Objects.nonNull(result.getInterruptReason())) {
                            actualResultEntity.setInterruptReason(result.getInterruptReason());
                        }
                    }
                    if (Objects.nonNull(target.getDelayReason())) {
                        if (Objects.nonNull(result.getDelayReason())) {
                            if (!target.getDelayReason().contains(result.getDelayReason())) {
                                actualResultEntity.setDelayReason(target.getDelayReason() + "," + result.getDelayReason());
                            }
                        }
                    } else {
                        if (Objects.nonNull(result.getDelayReason())) {
                            actualResultEntity.setDelayReason(result.getDelayReason());
                        }
                    }
                    if (actualResultEntity.getImplementDatetime().before(result.getImplementDatetime())) {
                        actualResultEntity.setImplementDatetime(result.getImplementDatetime());
                    }
                    if (actualResultEntity.getTransactionId() < result.getTransactionId()) {
                        actualResultEntity.setTransactionId(result.getTransactionId());
                    }
                    actualResultEntity.setSumTime(actualResultEntity.getSumTime() + result.getWorkingTime());
                    actualResultEntity.setPropertyCollection(target.getPropertyCollection());
                    results.put(actualResultEntity.getKanbanName() + actualResultEntity.getWorkName(), actualResultEntity);
                }
            }
        }

        this.actualResultEntitys.clear();
        this.actualResultEntitys.addAll(results.values());

        for (ActualResultEntity entity : this.actualResultEntitys) {
            String workName = entity.getWorkName();
            // フィルタリングはコントローラ側に移植
            if (this.filterWorkActualData(entity)) {
                // 工程がマップになかった場合、工程名をマップに登録して作業時間を管理する。
                if (!this.workActualData.containsKey(workName)) {
                    this.workActualData.put(workName, new NomalDistribtionData(workName,
                            LocaleUtils.getString("key.deviationValue"), LocaleUtils.getString("key.probabilityDestiny"),
                            new ArrayList<>(), entity.getTaktTime() / settingData.getTimeUnit().getTimeUnit(), new ArrayList<>()));
                    this.workActualData.get(workName).getDatas().add(entity.getSumTime() / settingData.getTimeUnit().getTimeUnit());
                    this.workActualData.get(workName).getActuals().add(entity);
                } else {
                    this.workActualData.get(workName).getDatas().add(entity.getSumTime() / settingData.getTimeUnit().getTimeUnit());
                    this.workActualData.get(workName).getActuals().add(entity);
                }
            } else {
                // 工程がマップになかった場合、工程名をマップに登録して作業時間を管理する。
                if (!this.workActualData.containsKey(workName)) {
                    this.workActualData.put(workName, new NomalDistribtionData(workName,
                            LocaleUtils.getString("key.deviationValue"), LocaleUtils.getString("key.probabilityDestiny"),
                            new ArrayList<>(), entity.getTaktTime() / settingData.getTimeUnit().getTimeUnit(), new ArrayList<>()));
                    Integer outOfRangeCount = this.workActualData.get(workName).getOutOfRangeNum() + 1;
                    this.workActualData.get(workName).setOutOfRangeNum(outOfRangeCount);
                } else {
                    Integer outOfRangeCount = this.workActualData.get(workName).getOutOfRangeNum() + 1;
                    this.workActualData.get(workName).setOutOfRangeNum(outOfRangeCount);
                }
            }
        }

        logger.info(NormalDistributionCreater.class.getName() + ":sortWorkActualData end");
    }

    /**
     * 実績情報フィルター処理
     *
     * @param data 実績情報
     * @return フィルターの有無 true:表示する false:表示しない
     */
    private Boolean filterWorkActualData(ActualResultEntity data) {
        logger.debug(NormalDistributionCreater.class.getName() + ":sortWorkActualData start");

        if (this.settingData.getFilterTactTimeEarliest() > 0) {
            Integer tactFilterEarliest = data.getTaktTime() - this.settingData.getFilterTactTimeEarliest();
            if (tactFilterEarliest > data.getSumTime()) {
                return Boolean.FALSE;
            }
        }

        if (this.settingData.getFilterTactTimeSlowest() > 0) {
            Integer tactFilterSlowest = data.getTaktTime() + this.settingData.getFilterTactTimeSlowest();
            if (tactFilterSlowest < data.getSumTime()) {
                return Boolean.FALSE;
            }
        }
        List<CheckTableData> delayList = new ArrayList(this.settingData.getFilterDelayReason());
        if (!this.settingData.getFilterDelayReason().isEmpty() && Objects.nonNull(data.getDelayReason())) {
            for (CheckTableData delay : delayList) {
                if (data.getDelayReason().equals(delay.getName()) && delay.getIsSelect()) {
                    return Boolean.FALSE;
                }
            }
        }

        logger.debug(NormalDistributionCreater.class.getName() + ":sortWorkActualData end");

        return Boolean.TRUE;
    }
}
