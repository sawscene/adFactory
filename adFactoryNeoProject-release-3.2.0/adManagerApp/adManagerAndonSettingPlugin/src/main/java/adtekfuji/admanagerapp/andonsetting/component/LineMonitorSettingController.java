/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.component;

import adtekfuji.admanagerapp.andonsetting.common.BreakTimeIdData;
import adtekfuji.admanagerapp.andonsetting.common.BreakTimeRecordFactory;
import static adtekfuji.admanagerapp.andonsetting.common.Constants.*;
import adtekfuji.admanagerapp.andonsetting.common.CountdownMelodyRecordFactory;
import adtekfuji.admanagerapp.andonsetting.common.ReasonData;
import adtekfuji.admanagerapp.andonsetting.common.ReasonRecordFactory;
import adtekfuji.admanagerapp.andonsetting.common.WeekDayData;
import adtekfuji.admanagerapp.andonsetting.common.WeekDayRecordFactory;
import adtekfuji.admanagerapp.andonsetting.common.WorkEquipmentRecordFactory;
import adtekfuji.admanagerapp.andonsetting.common.WorkRecordFactory;
import adtekfuji.admanagerapp.andonsetting.dialog.LineSelectDialog;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalTimeStringConverter;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CompCountTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ProductionTypeEnum;
import jp.adtekfuji.andon.entity.CountdownMelodyInfoEntity;
import jp.adtekfuji.andon.enumerate.AndonMonitorTypeEnum;
import jp.adtekfuji.andon.enumerate.CountdownMelodyInfoTypeEnum;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import jp.adtekfuji.andon.property.WorkSetting;
import jp.adtekfuji.andon.utility.CfgFileUtils;
import jp.adtekfuji.javafxcommon.DetailRecordFactory;
import jp.adtekfuji.javafxcommon.SimplePropertyBindEntity;
import jp.adtekfuji.javafxcommon.controls.TimeTextField;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 * 進捗モニター設定コントロール
 *
 * @author fu-kato
 */
public class LineMonitorSettingController extends AndonSettingController {

    private final Map<String, SimplePropertyBindEntity> detailProperties = new LinkedHashMap<>();
    private final Map<String, SimplePropertyBindEntity> layoutProperties = new LinkedHashMap<>();
    private final LinkedList<WeekDayData> weekdayCollection = new LinkedList<>();
    private final LinkedList<WorkEquipmentSetting> workEquipmentSettngCollection = new LinkedList<>();
    private final LinkedList<WorkSetting> workSettngCollection = new LinkedList<>();
    private final LinkedList<ReasonData> delayReasonDataCollection = new LinkedList<>();
    private final LinkedList<ReasonData> interruptReasonDataCollection = new LinkedList<>();
    private final LinkedList<CountdownMelodyInfoEntity> countdownMelodySettings = new LinkedList<>();

    private static final String DETAIL_LINE_NAME = "lineName";
    private static final String DETAIL_SELECT_LINE = "selectLine";
    private static final String DETAIL_MODEL_NAME = "modelName";
    private static final String DETAIL_START_TIME = "startTime";
    private static final String DETAIL_END_TIME = "endTime";
    private static final String DETAIL_TAKT_TIME = "taktTime";
    private static final String DETAIL_LAYOUT_SETTING = "layoutSetting";

    private static final int MIN_MONITOR_NUMBER = 1;
    private static final int MAX_MONITOR_NUMBER = 5;

    private final LocalTimeStringConverter converter = new LocalTimeStringConverter(DateTimeFormatter.ISO_LOCAL_TIME, DateTimeFormatter.ISO_LOCAL_TIME);

    private Table detailTable1;
    private Table layoutTable;

    // 作業終了時刻が自動で計算した場合そのことを伝える
    private Label calculatedLabel;

    private AndonMonitorLineProductSetting setting;
    private Long monitorId;

    /**
     * 生産タイプコンボボックスセル
     */
    class ProductionTypeComboBoxCellFactory extends ListCell<ProductionTypeEnum> {
        @Override
        protected void updateItem(ProductionTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
            }
        }
    }

    /**
     * 当日実績数のカウント方法コンボボックスセル
     */
    class CompCountTypeComboBoxCellFactory extends ListCell<CompCountTypeEnum> {
        @Override
        protected void updateItem(CompCountTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
            }
        }
    }
    
    /**
     * ディスプレイ番号コンボボックスセル
     */
    class DisplayNumberComboBoxCellFactory extends ListCell<Integer> {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("1");
            } else {
                setText(item.toString());
            }
        }
    }

    /**
     * 
     * @param settingController
     * @param monitorId
     * @param setting 
     */
    public LineMonitorSettingController(AndonSettingCompoFxController settingController, long monitorId, AndonMonitorLineProductSetting setting) {
        super();

        this.setComponentController(settingController);

        this.monitorId = monitorId;

        isDeletedItems = false;
        this.setting = setting;

        this.calculatedLabel = new Label(LocaleUtils.getString("key.calcEndTime"));
        this.calculatedLabel.setVisible(false);

        EquipmentInfoEntity line = null;
        String lineName = LocaleUtils.getString("key.AndonLineSettingNotAllocate");
        if (Objects.nonNull(this.setting.getLineId()) && this.setting.getLineId() != 0) {
            line = CacheUtils.getCacheEquipment(this.setting.getLineId());
            if (Objects.nonNull(line) && Objects.nonNull(line.getEquipmentId())) {
                lineName = line.getEquipmentName();
            } else {
                // 削除された設備
                isDeletedItems = true;
                line = null;
                this.setting.setLineId(0L);
            }
        }
        // ディスプレイ番号
        detailProperties.put("targetMonitor", SimplePropertyBindEntity.createCombo(LocaleUtils.getString("key.DisplayNumber"),
                FXCollections.observableArrayList(IntStream.rangeClosed(MIN_MONITOR_NUMBER, MAX_MONITOR_NUMBER).boxed().collect(Collectors.toList())),
                new DisplayNumberComboBoxCellFactory(),
                (ListView<Integer> param) -> new DisplayNumberComboBoxCellFactory(),
                this.setting.targetMonitorProperty()
        ));
        // 表示タイトル
        detailProperties.put(DETAIL_LINE_NAME, SimplePropertyBindEntity.createString(LocaleUtils.getString("key.AndonLineSettingTitle"), this.setting.titleProperty()));
        // 対象ライン
        detailProperties.put(DETAIL_SELECT_LINE, SimplePropertyBindEntity.createButton(LocaleUtils.getString("key.AndonLineSettingSelectLine"), new SimpleStringProperty(lineName), this::onSelectLine, line));
        // モデル名
        detailProperties.put(DETAIL_MODEL_NAME, SimplePropertyBindEntity.createString(LocaleUtils.getString("key.ModelName"), this.setting.modelNameProperty()));
        // 作業開始時刻
        detailProperties.put(DETAIL_START_TIME, SimplePropertyBindEntity.createLocalTime(LocaleUtils.getString("key.AndonLineSettingStartTime"), this.setting.startWorkTimeProperty())
                .actionListner((ObservableValue observable, Object oldValue, Object newValue) -> {
                    updateEndTime(this.setting.getDailyPlanNum());
                }));
        // 作業終了時刻
        detailProperties.put(DETAIL_END_TIME, SimplePropertyBindEntity.createLocalTime(LocaleUtils.getString("key.AndonLineSettingEndTime"), this.setting.endWorkTimeProperty()));
        // タクトタイム
        detailProperties.put(DETAIL_TAKT_TIME, SimplePropertyBindEntity.createLocalTime(LocaleUtils.getString("key.TactTime"), this.setting.lineTaktProperty())
                .actionListner((ObservableValue observable, Object oldValue, Object newValue) -> {
                    updateEndTime(this.setting.getDailyPlanNum());
                }));
        if (Boolean.valueOf(AdProperty.getProperties().getProperty("followStart", "false"))) {
            // 作業開始を追従する
            detailProperties.put("followStart", SimplePropertyBindEntity.createBoolean(LocaleUtils.getString("key.FollowStart"), "", this.setting.followStartProperty()));
        }

        // レイアウト
        layoutProperties.put(DETAIL_LAYOUT_SETTING, SimplePropertyBindEntity.createBoolean(LocaleUtils.getString("key.LayoutFromServer"), "", this.setting.remoteLayoutProperty()));

        // 休憩時間
        breaktimeIdCollection.clear();
        for (BreakTimeInfoEntity b : this.setting.getBreaktimes()) {
            BreakTimeInfoEntity breakEntity = CacheUtils.getCacheBreakTime(b.getBreaktimeId());
            if (Objects.isNull(breakEntity)) {
                // 削除された休憩時間
                isDeletedItems = true;
                continue;
            }

            breaktimeIdCollection.add(new BreakTimeIdData(b.getBreaktimeId()));
        }

        // 休日
        weekdayCollection.clear();
        for (DayOfWeek d : this.setting.getWeekdays()) {
            weekdayCollection.add(new WeekDayData(d));
        }

        // 対象設備
        workEquipmentSettngCollection.clear();
        for (WorkEquipmentSetting w : this.setting.getWorkEquipmentCollection()) {
            List<Long> ids = w.getEquipmentIds();
            List<EquipmentInfoEntity> equipments = CacheUtils.getCacheEquipment(ids);
            if (equipments.size() < ids.size()) {
                // 削除された設備がある。
                isDeletedItems = true;

                List<Long> removeIds = new ArrayList();
                for (Long id : ids) {
                    if (equipments.stream().filter(p -> p.getEquipmentId().equals(id)).count() == 0) {
                        removeIds.add(id);
                    }
                }
                ids.removeAll(removeIds);
            }
            workEquipmentSettngCollection.add(w);
        }

        // 対象工程
        workSettngCollection.clear();
        for (WorkSetting w : this.setting.getWorkCollection()) {
            workSettngCollection.add(w);
        }

        // 遅延理由
        delayReasonDataCollection.clear();
        for (String s : this.setting.getDelayReasonCollection()) {
            delayReasonDataCollection.add(new ReasonData(s));
        }

        // 中断理由
        interruptReasonDataCollection.clear();
        for (String s : this.setting.getInterruptReasonCollection()) {
            interruptReasonDataCollection.add(new ReasonData(s));
        }

        // サイクルカウントダウンメロディ (サイクルカウントダウンフレーム用)
        countdownMelodySettings.clear();
        if (Boolean.valueOf(AdProperty.getProperties().getProperty("@LineManaged", "false"))) {
            // 自動カウントダウン
            detailProperties.put("autoCountdown", SimplePropertyBindEntity.createBoolean(LocaleUtils.getString("key.AutoCountdown"), "", this.setting.autoCountdownProperty()));

            countdownMelodySettings.add(getCountdownMelodyInfoEntity(CountdownMelodyInfoTypeEnum.PATH_BEFORE_COUNTDOWN));          // カウントダウン開始前
            countdownMelodySettings.add(getCountdownMelodyInfoEntity(CountdownMelodyInfoTypeEnum.PATH_COUNTDOWN_START));           // カウントダウン開始
            countdownMelodySettings.add(getCountdownMelodyInfoEntity(CountdownMelodyInfoTypeEnum.PATH_BEFORE_END_OF_COUNTDOWN));   // カウントダウン終了前
            countdownMelodySettings.add(getCountdownMelodyInfoEntity(CountdownMelodyInfoTypeEnum.TIME_RING_TIMING_END_OF_COUNTDOWN));// カウントダウン終了前鳴動タイミング
            countdownMelodySettings.add(getCountdownMelodyInfoEntity(CountdownMelodyInfoTypeEnum.PATH_WORK_DELAYED));              // 作業遅延中
            countdownMelodySettings.add(getCountdownMelodyInfoEntity(CountdownMelodyInfoTypeEnum.PATH_BREAKTIME_START));           // 休憩開始
            countdownMelodySettings.add(getCountdownMelodyInfoEntity(CountdownMelodyInfoTypeEnum.PATH_BEFORE_END_OF_BREAKTIME));   // 休憩終了前
            countdownMelodySettings.add(getCountdownMelodyInfoEntity(CountdownMelodyInfoTypeEnum.TIME_RING_TIMING_END_OF_BREAKTIME));// 休憩終了前終了前鳴動タイミング
        }

        // 生産タイプ
        boolean isEnabledProductionType = false;
        if (isEnabledProductionType) {
            detailProperties.put("productionType", SimplePropertyBindEntity.createCombo(
                    LocaleUtils.getString("key.ProductionType"),
                    Arrays.asList(ProductionTypeEnum.values()),
                    new ProductionTypeComboBoxCellFactory(),
                    (ListView<ProductionTypeEnum> param) -> new ProductionTypeComboBoxCellFactory(),
                    this.setting.productionTypeProperty()
            ));
        }

        // 当日実績数のカウント方法
        detailProperties.put("compCountType", SimplePropertyBindEntity.createCombo(
                LocaleUtils.getString("key.CompCountType"),
                Arrays.asList(CompCountTypeEnum.values()),
                new CompCountTypeComboBoxCellFactory(),
                (ListView<CompCountTypeEnum> param) -> new CompCountTypeComboBoxCellFactory(),
                this.setting.compCountTypeProperty()
        ));

        // 当日計画数
        detailProperties.put("dailyPlanNum", SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AndonLineSettingDailyPlan"), this.setting.dailyPlanNumProperty())
                .actionListner((ObservableValue observable, Object oldValue, Object newValue) -> {
                    if (!StringUtils.isEmpty((String) newValue)) {
                        updateEndTime(Integer.parseInt((String) newValue));
                    }
                }));
        // 当月計画数
        detailProperties.put("montlyPlanNum", SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AndonLineSettingMontlyPlan"), this.setting.montlyPlanNumProperty()));
        // 注意
        detailProperties.put("cautionRetention", SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AndonLineSettingCautionParcent"), this.setting.cautionRetentionParcentProperty()));
        detailProperties.put("cautionFontColor", SimplePropertyBindEntity.createColorPicker(LocaleUtils.getString("key.AndonLineSettingCautionFontColor"), this.setting.cautionFontColorProperty()));
        detailProperties.put("cautionBackColor", SimplePropertyBindEntity.createColorPicker(LocaleUtils.getString("key.AndonLineSettingCautionBackColor"), this.setting.cautionBackColorProperty()));
        // 警告
        detailProperties.put("warningRetention", SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AndonLineSettingWarningParcent"), this.setting.warningRetentionParcentProperty()));
        detailProperties.put("warningFontColor", SimplePropertyBindEntity.createColorPicker(LocaleUtils.getString("key.AndonLineSettingWarningFontColor"), this.setting.warningFontColorProperty()));
        detailProperties.put("warningBackColor", SimplePropertyBindEntity.createColorPicker(LocaleUtils.getString("key.AndonLineSettingWarningBackColor"), this.setting.warningBackColorProperty()));
        // 表示単位
        detailProperties.put("unit", SimplePropertyBindEntity.createRegerxString(LocaleUtils.getString("key.AndonLineSettingUnit"), this.setting.unitProperty(), "([^ -~｡-ﾟ]{0,2})|([ -~｡-ﾟ]{0,4})"));

        Platform.runLater(() -> {
            Object obj = new Object();
            try {
                getComponentController().blockUI(obj, true);

                getChildren().clear();
                //
                detailTable1 = new Table(getChildren()).isAddRecord(false);
                detailTable1.setAbstractRecordFactory(new DetailRecordFactory(detailTable1, new LinkedList(detailProperties.values())));

                // レイアウトテーブル
                layoutTable = new Table(getChildren()).styleClass("ContentTitleLabel");
                layoutTable.setAbstractRecordFactory(new DetailRecordFactory(layoutTable, new LinkedList(layoutProperties.values())));

                // Tableに追加でフィールドを設定する
                Platform.runLater(() -> {
                    detailTable1.findLabelRow(LocaleUtils.getString("key.AndonLineSettingEndTime")).ifPresent(index -> {
                        detailTable1.addNodeToBody(calculatedLabel, 2, (int) index);
                    });
                    layoutTable.findLabelRow(LocaleUtils.getString("key.LayoutFromServer")).ifPresent(index -> {
                        Button button = new Button(LocaleUtils.getString("key.EditLayout"));
                        button.disableProperty().bind(Bindings.not(this.setting.remoteLayoutProperty()));
                        button.setOnAction(this::onLayoutSetting);
                        button.getStyleClass().add("ContentTextBox");
                        layoutTable.addNodeToBody(button, 2, (int) index);
                    });
                });

                // 休憩の追加削除と変更ではそれぞれ別の処理なためそれぞれにリスナーを付ける
                Table breaktimeTable = new Table(getChildren()).title(LocaleUtils.getString("key.AndonLineSettingBreakTime"))
                        .isAddRecord(true).styleClass("ContentTitleLabel")
                        .addRecordListener(event -> {
                            updateEndTime(LineMonitorSettingController.this.setting.getDailyPlanNum());
                        });
                breaktimeTable.setAbstractRecordFactory(new BreakTimeRecordFactory(breaktimeTable, cache.getItemList(BreakTimeInfoEntity.class, new ArrayList()), breaktimeIdCollection)
                        .actionListner((ObservableValue<? extends Long> observable, Long oldValue, Long newValue) -> {
                            updateEndTime(setting.getDailyPlanNum());
                        }));

                //
                Table weekdayTable = new Table(getChildren()).title(LocaleUtils.getString("key.AndonLineSettingWeekday"))
                        .isAddRecord(true).isColumnTitleRecord(true).styleClass("ContentTitleLabel");
                weekdayTable.setAbstractRecordFactory(new WeekDayRecordFactory(weekdayTable, Arrays.asList(DayOfWeek.values()), weekdayCollection));
                //
                Table delayReasonTable = new Table(getChildren()).isAddRecord(true).title(LocaleUtils.getString("key.EditDelayReasonTitle"))
                        .isAddRecord(true).isColumnTitleRecord(true).styleClass("ContentTitleLabel");
                delayReasonTable.setAbstractRecordFactory(new ReasonRecordFactory(delayReasonTable, delayReasonCollection, delayReasonDataCollection));
                //
                Table interruptReasonTable = new Table(getChildren()).isAddRecord(true).title(LocaleUtils.getString("key.EditInterruptReasonTitle"))
                        .isAddRecord(true).isColumnTitleRecord(true).styleClass("ContentTitleLabel");
                interruptReasonTable.setAbstractRecordFactory(new ReasonRecordFactory(interruptReasonTable, interruptReasonCollection, interruptReasonDataCollection));

                // 設備
                Table equipmentTable = new Table(getChildren()).title(LocaleUtils.getString("key.AndonLineSettingSelectEquipment"))
                        .isAddRecord(true).styleClass("ContentTitleLabel").maxRecord(50).isColumnTitleRecord(true);
                equipmentTable.setAbstractRecordFactory(new WorkEquipmentRecordFactory(equipmentTable, workEquipmentSettngCollection));

                // 対象工程選択
                // タイトルと実テーブルの間に設定を追加するため分離して作成
                final VBox workTables = new VBox();
                workTables.setPadding(new Insets(10, 0, 0, 0));
                workTables.setSpacing(10);
                getChildren().add(workTables);

                // タイトルと当日計画数
                final Table workTable1 = new Table(workTables.getChildren()).title(LocaleUtils.getString("key.AndonLineSettingSelectWork")).styleClass("ContentTitleLabel")
                        .footerManaged(false).bodyPadding(new Insets(0, 0, 0, 10));

                // 各工程レコード
                final Table workTable2 = new Table(workTables.getChildren()).headerManaged(false)
                        .isAddRecord(true).styleClass("ContentTitleLabel").maxRecord(24).isColumnTitleRecord(true);
                final WorkRecordFactory workFactory = new WorkRecordFactory(workTable2, workSettngCollection);

                // 工程追加時に設定の無効に合わせる
                workTable2.addRecordListener(event -> {
                    Platform.runLater(() -> {
                        workFactory.setDisablePlanNum(setting.getUseDailyPlanNum());
                    });
                });

                final SimplePropertyBindEntity<Boolean> useLinePlan = SimplePropertyBindEntity.createBoolean(LocaleUtils.getString("key.AndonLineSettingUseDailyPlan"), "", setting.useDailyPlanNumProperty())
                        .actionListner(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                workFactory.setDisablePlanNum(newValue);
                            }
                        });

                workTable1.setAbstractRecordFactory(new DetailRecordFactory(workTable1, new LinkedList(Arrays.asList(useLinePlan))));
                workTable2.setAbstractRecordFactory(workFactory);

                // 各工程の当日計画数とその使用有無のbindが難しいため起動時に使用しないなら各工程の当日計画数も無効にする
                Platform.runLater(() -> {
                    workFactory.setDisablePlanNum(setting.getUseDailyPlanNum());
                });

                if (!countdownMelodySettings.isEmpty()) {
                    // サイクルカウントダウンメロディ (サイクルカウントダウンフレーム用)
                    Table countdownMelodyTable = new Table(getChildren()).title(LocaleUtils.getString("key.CountdownMelodySettingLabel"))
                            .isAddRecord(false).styleClass("ContentTitleLabel");
                    countdownMelodyTable.setAbstractRecordFactory(new CountdownMelodyRecordFactory(countdownMelodyTable, countdownMelodySettings));
                }

                visibleCalculatedLabel(true);

            } catch (Exception ex) {
                logger.fatal(ex, ex);
            } finally {
                getComponentController().blockUI(obj, false);
            }
        });

        logger.info("end LineMonitorSettingControl; id={}, equipment={}", this.monitorId, this.setting);
    }

    /**
     * 作業終了時刻の右に変更された旨を表示する(設定が有効の場合のみ)
     *
     * @param visible
     */
    private void visibleCalculatedLabel(boolean visible) {
        if (Boolean.valueOf(AdProperty.getProperties().getProperty(AUTO_ENDTIME, "false"))) {
            this.calculatedLabel.setVisible(visible);
        }
    }

    /**
     * 終了時刻の再計算を行う
     *
     * @param daily 当日計画数 当日計画数自体が変更された場合はnewValueを取らないと古い値になるため呼出側から指定する。
     */
    private void updateEndTime(int daily) {
        if (!Boolean.valueOf(AdProperty.getProperties().getProperty(AUTO_ENDTIME, "false"))) {
            return;
        }

        LocalTime takt = converter.fromString(((SimpleStringProperty) detailProperties.get(DETAIL_TAKT_TIME).getProperty()).get());
        LocalTime startTime = converter.fromString(((SimpleStringProperty) detailProperties.get(DETAIL_START_TIME).getProperty()).get());

        if (takt.equals(LocalTime.of(0, 0, 0))) {
            return;
        }

        LocalTime endTime = breaktimeIdCollection.stream()
                .map(id -> CacheUtils.getCacheBreakTime(id.getId()))
                .sorted(Comparator.comparing(BreakTimeInfoEntity::getStarttime))
                .sequential() // 並列実行だとおそらくうまくいかない
                .reduce(startTime.plusSeconds(daily * takt.toSecondOfDay()),
                        (workTime, entity) -> {
                            final LocalTime breakStart = DateUtils.toLocalTime(entity.getStarttime());
                            final LocalTime breakEnd = DateUtils.toLocalTime(entity.getEndtime());
                            final long total = Duration.between(breakStart, breakEnd).getSeconds();

                            // 作業開始時刻以降の休憩時間で、
                            // 前の休憩時間を考慮した作業終了時刻にこの休憩時間が収まっている場合のみ休憩を加算
                            return startTime.isBefore(breakStart) && breakStart.isBefore(workTime)
                                    ? workTime.plusSeconds(total)
                                    : workTime;
                        },
                        (sum1, sum2) -> {
                            return LocalTime.of(1, 1); // sequentialでは呼ばれないためでたらめ
                        });

        // 終了時刻は直接bindされてないためdetailPropertiesを変えても意味がない。直接変更する。
        detailTable1.getNodeFromBody(5, 1).ifPresent(node -> {
            ((TimeTextField) node).setText(converter.toString(endTime));
        });
    }

    /**
     * カスタマイズツールを起動する。
     * カスタマイズツール終了時に保存された設定を読み込む。
     *
     * @param event
     */
    private void onLayoutSetting(ActionEvent event) {
        logger.info("onLayoutSetting:{},{}", monitorId);
        Object obj = new Object();
        try {
            getComponentController().blockUI(obj, true);

            Path path = Files.createTempFile("custom_layout_", ".cfg");

            new Thread(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // 現在の設定をTempフォルダに保存しそれを読み込ませる
                        CfgFileUtils.createCfgFile(path, setting.getLayout(), setting.getCustomizeToolLayout());

                        final String customizeToolPath = System.getenv("ADFACTORY_HOME") + File.separator + "bin" + File.separator + "DashboardCustomizeTool.exe";
                        final File customizeToolFile = new File(customizeToolPath);
                        if (customizeToolFile.exists()) {
                            ProcessBuilder updaterProcessBuilder = new ProcessBuilder(customizeToolPath, "-f", path.toString());
                            Process process = updaterProcessBuilder.start();
                            process.waitFor();
                        }
                    } catch (IOException | InterruptedException ex) {
                        logger.fatal(ex, ex);
                    }
                    return null;
                }

                @Override
                protected void failed() {
                    logger.fatal("failed to execute DashboardCustomizeTool");
                    path.toFile().deleteOnExit();
                    getComponentController().blockUI(obj, false);
                }

                @Override
                protected void succeeded() {
                    logger.info("succeeded to execute DashboardCustomizeTool");

                    try {
                        // カスタマイズツールで保存するとcfgの中のファイル名が変わるので確認
                        String layoutFilename = CfgFileUtils.isInternal(path, CfgFileUtils.CFG_LAYOUT_INI) ? CfgFileUtils.CFG_LAYOUT_INI : CfgFileUtils.CFG_LAYOUT2_INI;
                        setting.setLayout(CfgFileUtils.loadInternal(path, layoutFilename));
                        setting.setCustomizeToolLayout(CfgFileUtils.loadInternal(path, CfgFileUtils.CFG_CUSTOMIZETOOL_LAYOUT_XML));
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }

                    path.toFile().deleteOnExit();
                    getComponentController().blockUI(obj, false);
                }
            }).start();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            getComponentController().blockUI(obj, false);
        }
    }

    /**
     * ライン選択ダイアログを表示しラインIDを設定する
     *
     * @param event
     */
    public void onSelectLine(Event event) {
        Button eventSrc = (Button) event.getSource();
        List<Long> targets = setting.getLineId() != 0
                ? Arrays.asList(setting.getLineId())
                : Collections.emptyList();

        eventSrc.setUserData(targets);

        ButtonType ret = LineSelectDialog.showDialogLineEntity((ActionEvent) event);
        if (ret.equals(ButtonType.OK)) {

            String lineName = LocaleUtils.getString("key.AndonLineSettingNotAllocate");
            Long lineId = 0L;

            List<EquipmentInfoEntity> lines = (List) eventSrc.getUserData();

            if (Objects.nonNull(lines) && !lines.isEmpty()) {
                EquipmentInfoEntity line = lines.get(0);

                //ボタンの表示を選択したラインに変更する
                lineName = line.getEquipmentName();
                lineId = line.getEquipmentId();
            }
            setting.setLineId(lineId);
            eventSrc.setText(lineName);
        }
    }

    /**
     * 入力された設定をEntityとして取得する
     *
     * @return 入力した設定エンティティ
     */
    @Override
    public AndonMonitorLineProductSetting getInputResult() {
        logger.info("onRegistAction:{},{}", monitorId, setting);

        if (Objects.isNull(monitorId) || Objects.isNull(setting)) {
            return null;
        }

        // 当日計画数などはbindしてあるためコピーして取得
        AndonMonitorLineProductSetting result = setting.clone();
        result.setMonitorType(AndonMonitorTypeEnum.LINE_PRODUCT);

        try {
            // 表示タイトル
            result.setTitle(((SimpleStringProperty) detailProperties.get(DETAIL_LINE_NAME).getProperty()).get());
            // 対象ライン
            //      本来ならここにLineIDが入るがonActionEventにて行っているため省略
            // モデル名
            result.setModelName(((SimpleStringProperty) detailProperties.get(DETAIL_MODEL_NAME).getProperty()).get());
            // 作業開始時刻
            result.setStartWorkTime(converter.fromString(((SimpleStringProperty) detailProperties.get(DETAIL_START_TIME).getProperty()).get()));
            // 作業終了時刻
            result.setEndWorkTime(converter.fromString(((SimpleStringProperty) detailProperties.get(DETAIL_END_TIME).getProperty()).get()));
            // タクトタイム
            result.setLineTakt(converter.fromString(((SimpleStringProperty) detailProperties.get(DETAIL_TAKT_TIME).getProperty()).get()));

            if (detailProperties.containsKey("autoCountdown")) {
                result.setAutoCountdown(((BooleanProperty) detailProperties.get("autoCountdown").getProperty()).getValue());
            }

            if (detailProperties.containsKey("followStart")) {
                result.setFollowStart(((BooleanProperty) detailProperties.get("followStart").getProperty()).getValue());
            }

            // レイアウト設定(layout, customizeToolLayout)の取得はカスタマイズツール終了時(onLayoutSetting)に行うためここでは有効無効のみ
            result.setRemoteLayout(((SimpleBooleanProperty) layoutProperties.get(DETAIL_LAYOUT_SETTING).getProperty()).get());

            List<BreakTimeInfoEntity> breaktimes = new ArrayList<>();
            for (BreakTimeIdData id : breaktimeIdCollection) {
                BreakTimeInfoEntity breakTime = CacheUtils.getCacheBreakTime(id.getId());
                if (Objects.isNull(breakTime)) {
                    continue;
                }
                breaktimes.add(breakTime);
            }
            result.setBreaktimes(breaktimes);

            List<DayOfWeek> dayweeks = new ArrayList<>();
            for (WeekDayData data : weekdayCollection) {
                dayweeks.add(data.getWeekDay());
            }
            result.setWeekdays(dayweeks);

            int order = 1;
            List<WorkEquipmentSetting> workEquipmentSettngs = new ArrayList<>();
            for (WorkEquipmentSetting data : workEquipmentSettngCollection) {
                data.setOrder(order++);
                workEquipmentSettngs.add(data);
            }
            result.setWorkEquipmentCollection(workEquipmentSettngs);

            int orderWork = 1;
            List<WorkSetting> workSettngs = new ArrayList<>();
            for (WorkSetting data : workSettngCollection) {
                data.setOrder(orderWork);
                data.setPluginName(String.format(LocaleUtils.getString("key.DailyActualNumPerWork"), orderWork));
                orderWork++;
                workSettngs.add(data);
            }
            result.setWorkCollection(workSettngs);

            List<String> delays = new ArrayList<>();
            for (ReasonData reason : delayReasonDataCollection) {
                delays.add(reason.getReason());
            }
            result.setDelayReasonCollection(delays);

            List<String> interrupts = new ArrayList<>();
            for (ReasonData reason : interruptReasonDataCollection) {
                interrupts.add(reason.getReason());
            }
            result.setInterruptReasonCollection(interrupts);

            result.setCountdownMelodyInfoCollection(countdownMelodySettings);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return result;
    }

    /**
     * 読み込んだ設定からサイクルカウントダウンメロディ情報のエンティティを取得する。 無い場合は新規作成して返す。
     *
     * @param melodyInfoType サイクルメロディ情報種別
     * @return サイクルメロディ
     */
    private CountdownMelodyInfoEntity getCountdownMelodyInfoEntity(CountdownMelodyInfoTypeEnum melodyInfoType) {
        Optional<CountdownMelodyInfoEntity> melody = setting.getCountdownMelodyInfoCollection().stream()
                .filter(p -> melodyInfoType.equals(p.getMelodyInfoType())).findFirst();
        if (melody.isPresent()) {
            return melody.get();
        } else {
            return new CountdownMelodyInfoEntity(melodyInfoType, "");
        }
    }

    /**
     * 入力した項目が有効なものか調べる。
     *
     * @return 全て正常に入力された場合true
     */
    @Override
    public boolean isValidItems() {

        // 表示タイトル
        if (Objects.nonNull(this.setting.getTitle())
                && this.setting.getTitle().getBytes().length > 256) {
            String message = new StringBuilder(LocaleUtils.getString("key.warn.enterCharacters256"))
                    .append("\r(").append(LocaleUtils.getString("key.AndonLineSettingTitle")).append(")")
                    .toString();
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message);
            return false;
        }

        // モデル名
        if (Objects.nonNull(this.setting.getModelName())
                && this.setting.getModelName().getBytes().length > 256) {
            String message = new StringBuilder(LocaleUtils.getString("key.warn.enterCharacters256"))
                    .append("\r(").append(LocaleUtils.getString("key.ModelName")).append(")")
                    .toString();
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message);
            return false;
        }

        // 作業開始時刻, 作業終了時刻
        if (this.setting.getStartWorkTime().isAfter(this.setting.getEndWorkTime())) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DateCompErrMessage"));
            return false;
        }

        // 休憩時間
        Set<Long> set1 = new HashSet<>();
        for (BreakTimeIdData breakTimeId : breaktimeIdCollection) {
            if (set1.contains(breakTimeId.getId())) {
                BreakTimeInfoEntity breakTime = CacheUtils.getCacheBreakTime(breakTimeId.getId());
                if (Objects.isNull(breakTime)) {
                    continue;
                }
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), breakTime.getBreaktimeName()));
                return false;
            } else {
                set1.add(breakTimeId.getId());
            }
        }

        // 休日
        Set<DayOfWeek> set2 = new HashSet<>();
        for (WeekDayData data : weekdayCollection) {
            if (set2.contains(data.getWeekDay())) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), data.getWeekDay().getDisplayName(TextStyle.FULL, Locale.JAPANESE)));
                return false;
            } else {
                set2.add(data.getWeekDay());
            }
        }

        // 遅延理由
        Set<String> set3 = new HashSet<>();
        for (ReasonData data : delayReasonDataCollection) {
            if (set3.contains(data.getReason())) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), data.getReason()));
                return false;
            } else {
                set3.add(data.getReason());
            }
        }

        // 中断理由
        Set<String> set4 = new HashSet<>();
        for (ReasonData data : interruptReasonDataCollection) {
            if (set4.contains(data.getReason())) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), data.getReason()));
                return false;
            } else {
                set4.add(data.getReason());
            }
        }

        // 対象設備
        for (WorkEquipmentSetting workEquipment : this.setting.getWorkEquipmentCollection()) {
            if (Objects.nonNull(workEquipment.getTitle()) && workEquipment.getTitle().getBytes().length > 256) {
                String message = new StringBuilder(LocaleUtils.getString("key.warn.enterCharacters256"))
                        .append("\r(").append(LocaleUtils.getString("key.AndonLineSettingSelectEquipment"))
                        .append(" - ").append(LocaleUtils.getString("key.DisplayName")).append(")")
                        .toString();
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message);
                return false;
            }
        }

        // 対象工程
        for (WorkSetting work : this.setting.getWorkCollection()) {
            if (Objects.nonNull(work.getTitle()) && work.getTitle().getBytes().length > 256) {
                String message = new StringBuilder(LocaleUtils.getString("key.warn.enterCharacters256"))
                        .append("\r(").append(LocaleUtils.getString("key.AndonLineSettingSelectWork"))
                        .append(" - ").append(LocaleUtils.getString("key.DisplayName")).append(")")
                        .toString();
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message);
                return false;
            }
        }

        return true;
    }

    public void clear() {
        breaktimeIdCollection.clear();
        weekdayCollection.clear();
        workEquipmentSettngCollection.clear();
        workSettngCollection.clear();
        delayReasonDataCollection.clear();
        interruptReasonDataCollection.clear();
        countdownMelodySettings.clear();
    }
}
