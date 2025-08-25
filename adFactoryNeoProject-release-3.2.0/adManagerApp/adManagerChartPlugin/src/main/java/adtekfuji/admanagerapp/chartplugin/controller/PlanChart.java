/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.chartplugin.controller;

import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業計画チャート
 *
 * @author fu-kato
 */
public class PlanChart {

    //計画チャートに共通の名前
    public static final String PLAN_CHART_NAME = "PLAN_CHART_NAME";

    private final WorkKanbanInfoFacade workKanbanInfoFacade = new WorkKanbanInfoFacade();
    private final Logger logger = LogManager.getLogger();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");;

    private final List<XYChart.Series<Number, Number>> planSeriesList = new ArrayList<>();
    private final List<Long> prevKanbanIds = new ArrayList();
    private final Map<Long, List<WorkKanbanInfoEntity>> workKanbans = new HashMap();

    private final BooleanProperty enableProperty = new SimpleBooleanProperty(false);

    /**
     * 計画が有効か無効か<br>
     * 有効になった後に初めて呼ばれたupdateで計画を取得する
     *
     * @return
     */
    public final BooleanProperty enableProperty() {
        return enableProperty;
    }

    /**
     * 作成したSeriesを取得する
     *
     * @return
     */
    public List<XYChart.Series<Number, Number>> getSeries() {
        return planSeriesList;
    }

    /**
     * 接頭辞を付けた名前を取得
     *
     * @param name
     * @return
     */
    public static String createPrefixed(String name) {
        return PLAN_CHART_NAME + name;
    }

    /**
     * カンバンIDをもとにSeriesを更新する
     *
     * @param kanbanIds
     * @param from
     * @param works 工程ID => リストのインデックス
     */
    public void update(List<Long> kanbanIds, Long from, Map<Long, Integer> works) {
        logger.info("PlanChart::update start. kanbansIds={}", kanbanIds);

        // 有効状態以外更新を行わない
        if (!enableProperty.get()) {
            return;
        }

        List<Long> additionalKanbanIds = kanbanIds.stream().filter(id -> !prevKanbanIds.contains(id)).collect(Collectors.toList());
        List<Long> reducedKanbanIds = prevKanbanIds.stream().filter(id -> !kanbanIds.contains(id)).collect(Collectors.toList());

        if (additionalKanbanIds.size() > 0) {
            Map adds = addWorkKanbans(additionalKanbanIds);
            workKanbans.putAll(adds);
        }

        if (reducedKanbanIds.size() > 0) {
            workKanbans.entrySet().removeIf(workKanban -> reducedKanbanIds.contains(workKanban.getKey()));
        }

        createSeries(from, works);

        prevKanbanIds.clear();
        prevKanbanIds.addAll(kanbanIds);

        logger.info("PlanChart::update end.");
    }

    /**
     * 工程プロパティから工程リストでチェックの入っているもののみSeriesに構築する
     *
     * @param from
     * @param works
     */
    private void createSeries(Long from, Map<Long, Integer> works) {
        logger.info("PlanChart::createSeries start. from={}, works={}", from, works);

        //工程リストにチェックが入っているもののみ構築
        List<List<WorkKanbanInfoEntity>> viewWorkKanbans = workKanbans.entrySet().stream()
                .map(map -> map.getValue())
                .map(workKanban -> workKanban.stream()
                        .filter(workKanbanInfo -> Objects.nonNull(works.get(workKanbanInfo.getFkWorkId())))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        //工程の開始からタクトタイム経過分までの線
        //スキップの場合飛ばす
        Function<WorkKanbanInfoEntity, Stream<XYChart.Data>> workTakt = workKanban -> {
            if (!workKanban.getSkipFlag()) {
                return Stream.of(
                        new XYChart.Data(works.get(workKanban.getFkWorkId()), from - workKanban.getStartDatetime().getTime(), workKanban),
                        new XYChart.Data(works.get(workKanban.getFkWorkId()), from - (workKanban.getStartDatetime().getTime() + workKanban.getTaktTime()), workKanban)
                );
            } else {
                return Stream.of();
            }
        };

        //Seriesを構築
        clearSeries();

        for (List<WorkKanbanInfoEntity> workKanban : viewWorkKanbans) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();

            //NodeはXYChartに追加しないかぎりnullでUserDataが使えない。そのための暫定処置
            //凡例には計画チャートを表示したくないので適当な名前を付ける
            //またカンバン名も一緒につけてツールチップに使用する
            series.setName(createPrefixed(workKanban.stream().findFirst().map(WorkKanbanInfoEntity::getKanbanName).orElse("")));

            List datum = workKanban.stream()
                    .flatMap(workTakt)
                    .collect(Collectors.toList());

            series.getData().addAll(datum);

            planSeriesList.add(series);
        }

        logger.info("PlanChart::createSeries end.");
    }

    /**
     * 表示チャートクリア
     */
    private void clearSeries() {
        planSeriesList.stream().forEach(series -> series.getData().clear());
        planSeriesList.clear();
    }

    /**
     * 追加された工程カンバンのリストを作成する
     *
     * @param kanbanIds
     * @return 追加された工程カンバンのリスト
     */
    private Map<Long, List<WorkKanbanInfoEntity>> addWorkKanbans(List<Long> kanbanIds) {
        logger.info("PlanChart::addWorkKanbans start.");

        Map<Long, List<WorkKanbanInfoEntity>> adds = new HashMap();

        //工程カンバン取得
        for (Long kanbanId : kanbanIds) {
            List<WorkKanbanInfoEntity> workKanban = workKanbanInfoFacade.getWorkKanbans(kanbanId);

            adds.put(kanbanId, workKanban);
        }

        logger.info("PlanChart::addWorkKanbans end.");

        return adds;
    }

    /**
     * 計画チャートにツールチップを表示させる
     * <b>Chartに追加した後に行うこと</b>
     */
    public void createTooltip() {
        this.eachSeries(series -> {
            try {
                String kanbanName = series.getName().replaceFirst(PLAN_CHART_NAME, "");

                Tooltip tooltip = TooltipBuilder.build(series.getNode());

                HBox hBox = new HBox();
                Label label1 = new Label();
                label1.setStyle("-fx-background-color:transparent; -fx-padding: 0 8 0 8; -fx-border-color: white transparent white white;");
                Label label2 = new Label();
                label2.setStyle("-fx-background-color:transparent; -fx-padding: 0 8 0 8; -fx-border-color: white;");

                StringBuilder sb1 = new StringBuilder();
                sb1.append(LocaleUtils.getString("key.Kanban"));

                StringBuilder sb2 = new StringBuilder();
                sb2.append(kanbanName);

                label1.setText(sb1.toString());
                label2.setText(sb2.toString());
                hBox.getChildren().addAll(label1, label2);
                tooltip.setGraphic(hBox);
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });
    }

    /**
     * チャートを構成する各Dataに対して処理を適用する<br>
     *
     * @param consumer
     */
    public void eachSeries(Consumer<? super XYChart.Series<Number, Number>> consumer) {
        planSeriesList.stream().forEach(consumer);
    }

    /**
     * チャートを構成する各Nodeに対して処理を適用する<br>
     * <b>Chartに追加した後に行うこと</b>
     *
     * @param consumer
     */
    public void eachNode(Consumer<? super Node> consumer) {
        planSeriesList.stream()
                .map(XYChart.Series::getNode)
                .forEach(consumer);
    }

    /**
     * シンボルの表示非表示を設定する<br>
     * <b>Chartに追加した後に行うこと</b>
     *
     * @param visible trueの場合シンボルを表示
     */
    public void setSymbolVisible(boolean visible) {
        planSeriesList.stream()
                .map(XYChart.Series<Number, Number>::getData)
                .forEach(data -> {
                    data.stream()
                            .map(d -> (StackPane) d.getNode())
                            .forEach(sp -> {
                                sp.setVisible(visible);
                            });
                });
    }
}
