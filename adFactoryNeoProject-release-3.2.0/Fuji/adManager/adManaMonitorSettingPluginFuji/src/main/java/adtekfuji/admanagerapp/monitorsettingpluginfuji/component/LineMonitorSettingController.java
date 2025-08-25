/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingpluginfuji.component;

import adtekfuji.admanagerapp.monitorsettingpluginfuji.common.*;
import adtekfuji.admanagerapp.monitorsettingpluginfuji.dialog.LineSelectDialog;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import adtekfuji.locale.LocaleUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalTimeStringConverter;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.andon.enumerate.AndonMonitorTypeEnum;
import jp.adtekfuji.andon.property.MonitorSettingFuji;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import jp.adtekfuji.andon.property.WorkSetting;
import jp.adtekfuji.andon.utility.CfgFileUtils;
import jp.adtekfuji.forfujiapp.entity.monitor.CycleMelodyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.monitor.CycleMelodyTypeEnum;
import jp.adtekfuji.forfujiapp.entity.monitor.CycleTaktInfoEntity;
import jp.adtekfuji.javafxcommon.DetailRecordFactory;
import jp.adtekfuji.javafxcommon.PropertyBindEntityInterface;
import jp.adtekfuji.javafxcommon.SimplePropertyBindEntity;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 * 進捗モニター設定コントロール
 *
 * @author fu-kato
 */
public class LineMonitorSettingController extends AndonSettingController {

    private final Map<String, SimplePropertyBindEntity> layoutProperties = new LinkedHashMap<>();
    private final LinkedList<WeekDayData> weekdayCollection = new LinkedList<>();
    private final LinkedList<WorkEquipmentSetting> workEquipmentSettngCollection = new LinkedList<>();
    private final LinkedList<WorkSetting> workSettngCollection = new LinkedList<>();
    private final LinkedList<ReasonData> delayReasonDataCollection = new LinkedList<>();
    private final LinkedList<ReasonData> interruptReasonDataCollection = new LinkedList<>();

    private final LinkedList<CycleTaktInfoEntity> cycleTaktSettingCollection = new LinkedList<>();
    private final LinkedList<CycleMelodyInfoEntity> cycleMelodySettingCollection = new LinkedList<>();

    private final Map<String, SimplePropertyBindEntity> detailProperties = new LinkedHashMap<>();

    private static final String DETAIL_LINE_NAME = "lineName";
    private static final String DETAIL_SELECT_LINE = "selectLine";
    private static final String DETAIL_MODEL_NAME = "modelName";
    private static final String DETAIL_START_TIME = "startTime";
    private static final String DETAIL_END_TIME = "endTime";
    private static final String DETAIL_TAKT_TIME = "taktTime";
    private static final String DETAIL_LAYOUT_SETTING = "layoutSetting";

    private Table layoutTable;

    private final long monitorId;
    private final MonitorSettingFuji setting;

    /**
     * 
     * @param settingController
     * @param monitorId
     * @param setting 
     */
    LineMonitorSettingController(MonitorSettingCompoFxController settingController, long monitorId, MonitorSettingFuji setting) {
        super();

        this.setComponentController(settingController);

        this.monitorId = monitorId;

        isDeletedItems = false;
        this.setting = setting;

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

        // 表示タイトル
        detailProperties.put(DETAIL_LINE_NAME, SimplePropertyBindEntity.createString(LocaleUtils.getString("key.AndonLineSettingTitle"), this.setting.titleProperty()));
        // 対象ライン
        detailProperties.put(DETAIL_SELECT_LINE, SimplePropertyBindEntity.createButton(LocaleUtils.getString("key.AndonLineSettingSelectLine"), new SimpleStringProperty(lineName), this::onSelectLine, line));
        // モデル名
        detailProperties.put(DETAIL_MODEL_NAME, SimplePropertyBindEntity.createString(LocaleUtils.getString("key.ModelName"), this.setting.modelNameProperty()));
        // 作業開始時刻
        detailProperties.put(DETAIL_START_TIME, SimplePropertyBindEntity.createLocalTime(LocaleUtils.getString("key.AndonLineSettingStartTime"), this.setting.startWorkTimeProperty()));
        // 作業終了時刻
        detailProperties.put(DETAIL_END_TIME, SimplePropertyBindEntity.createLocalTime(LocaleUtils.getString("key.AndonLineSettingEndTime"), this.setting.endWorkTimeProperty()));
        // タクトタイム
        detailProperties.put(DETAIL_TAKT_TIME, SimplePropertyBindEntity.createLocalTime(LocaleUtils.getString("key.TactTime"), this.setting.lineTaktProperty()));

        // レイアウト
        layoutProperties.put(DETAIL_LAYOUT_SETTING, SimplePropertyBindEntity.createBoolean(LocaleUtils.getString("key.LayoutFromServer"), "", this.setting.remoteLayoutProperty()));

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

        LinkedList<PropertyBindEntityInterface> settings2 = new LinkedList<>();
        // 当日計画数
        settings2.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AndonLineSettingDailyPlan"), this.setting.dailyPlanNumProperty()));
        // 当月計画数
        settings2.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AndonLineSettingMontlyPlan"), this.setting.montlyPlanNumProperty()));
        // 注意
        settings2.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AndonLineSettingCautionParcent"), this.setting.cautionRetentionParcentProperty()));
        settings2.add(SimplePropertyBindEntity.createColorPicker(LocaleUtils.getString("key.AndonLineSettingCautionFontColor"), this.setting.cautionFontColorProperty()));
        settings2.add(SimplePropertyBindEntity.createColorPicker(LocaleUtils.getString("key.AndonLineSettingCautionBackColor"), this.setting.cautionBackColorProperty()));
        // 警告
        settings2.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AndonLineSettingWarningParcent"), this.setting.warningRetentionParcentProperty()));
        settings2.add(SimplePropertyBindEntity.createColorPicker(LocaleUtils.getString("key.AndonLineSettingWarningFontColor"), this.setting.warningFontColorProperty()));
        settings2.add(SimplePropertyBindEntity.createColorPicker(LocaleUtils.getString("key.AndonLineSettingWarningBackColor"), this.setting.warningBackColorProperty()));

        // サイクルタクトタイム (サイクルタクトタイムフレーム用)
        cycleTaktSettingCollection.clear();
        cycleTaktSettingCollection.addAll(this.setting.getCycleTaktCollection());

        // サイクルメロディ (サイクルタクトタイムフレーム用)
        cycleMelodySettingCollection.clear();
        cycleMelodySettingCollection.add(this.getCycleMelodyInfoEntity(CycleMelodyTypeEnum.WORK_START_30SEC));// 作業開始時刻 30秒前
        cycleMelodySettingCollection.add(this.getCycleMelodyInfoEntity(CycleMelodyTypeEnum.CYCLE_START));// サイクル 開始
        cycleMelodySettingCollection.add(this.getCycleMelodyInfoEntity(CycleMelodyTypeEnum.CYCLE_60SEC));// サイクル 終了1分前
        cycleMelodySettingCollection.add(this.getCycleMelodyInfoEntity(CycleMelodyTypeEnum.CYCLE_30SEC));// サイクル 終了30秒前
        cycleMelodySettingCollection.add(this.getCycleMelodyInfoEntity(CycleMelodyTypeEnum.CYCLE_5SEC));// サイクル 終了5秒前
        cycleMelodySettingCollection.add(this.getCycleMelodyInfoEntity(CycleMelodyTypeEnum.LUNCH_TIME_START));// 昼休憩 開始
        cycleMelodySettingCollection.add(this.getCycleMelodyInfoEntity(CycleMelodyTypeEnum.LUNCH_TIME_30SEC));// 昼休憩 終了30秒前
        cycleMelodySettingCollection.add(this.getCycleMelodyInfoEntity(CycleMelodyTypeEnum.REFRESH_TIME_START));// リフレッシュタイム 開始
        cycleMelodySettingCollection.add(this.getCycleMelodyInfoEntity(CycleMelodyTypeEnum.REFRESH_TIME_30SEC));// リフレッシュタイム 終了30秒前
        cycleMelodySettingCollection.add(this.getCycleMelodyInfoEntity(CycleMelodyTypeEnum.WORK_END));// 作業終了

        Platform.runLater(() -> {
            Object obj = new Object();
            try {
                getComponentController().blockUI(obj, true);

                getChildren().clear();
                //
                Table detailTable1 = new Table(getChildren()).isAddRecord(false);
                detailTable1.setAbstractRecordFactory(new DetailRecordFactory(detailTable1, new LinkedList(detailProperties.values())));

                // レイアウトテーブル
                layoutTable = new Table(getChildren()).styleClass("ContentTitleLabel");
                layoutTable.setAbstractRecordFactory(new DetailRecordFactory(layoutTable, new LinkedList(layoutProperties.values())));

                // Tableに追加でフィールドを設定する
                Platform.runLater(() -> {
                    layoutTable.findLabelRow(LocaleUtils.getString("key.LayoutFromServer")).ifPresent(index -> {
                        Button button = new Button(LocaleUtils.getString("key.EditLayout"));
                        button.disableProperty().bind(Bindings.not(this.setting.remoteLayoutProperty()));
                        button.setOnAction(this::onLayoutSetting);
                        button.getStyleClass().add("ContentTextBox");
                        layoutTable.addNodeToBody(button, 2, (int) index);
                    });
                });

                //
                Table breaktimeTable = new Table(getChildren()).title(LocaleUtils.getString("key.AndonLineSettingBreakTime"))
                        .isAddRecord(true).styleClass("ContentTitleLabel");
                breaktimeTable.setAbstractRecordFactory(new BreakTimeRecordFactory(breaktimeTable, cache.getItemList(BreakTimeInfoEntity.class, new ArrayList()), breaktimeIdCollection));
                //
                Table weekdayTable = new Table(getChildren()).title(LocaleUtils.getString("key.AndonLineSettingWeekday"))
                        .isAddRecord(true).isColumnTitleRecord(true).styleClass("ContentTitleLabel");
                weekdayTable.setAbstractRecordFactory(new WeekDayRecordFactory(weekdayTable, Arrays.asList(DayOfWeek.values()), weekdayCollection));
                //
                Table detailTable2 = new Table(getChildren()).isAddRecord(false);
                detailTable2.setAbstractRecordFactory(new DetailRecordFactory(detailTable2, settings2));
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

                // サイクルタクトタイム (サイクルタクトタイムフレーム用)
                Table cycleTaktTable = new Table(getChildren()).title(LocaleUtils.getString("key.ForFuji.CycleTaktTimeSettingLabel"))
                        .isAddRecord(true).styleClass("ContentTitleLabel").maxRecord(99);
                cycleTaktTable.setAbstractRecordFactory(new CycleTaktRecordFactory(cycleTaktTable, cycleTaktSettingCollection));

                // サイクルメロディ (サイクルタクトタイムフレーム用)
                Table cycleMelodyTable = new Table(getChildren()).title(LocaleUtils.getString("key.ForFuji.CycleMelodySettingLabel"))
                        .isAddRecord(false).styleClass("ContentTitleLabel");
                cycleMelodyTable.setAbstractRecordFactory(new CycleMelodyRecordFactory(cycleMelodyTable, cycleMelodySettingCollection));
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            } finally {
                getComponentController().blockUI(obj, false);
            }
        });

        logger.info("end LineMonitorSettingControl; id={}, equipment={}", this.monitorId, this.setting);
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

            EquipmentInfoEntity line = ((List<EquipmentInfoEntity>) eventSrc.getUserData()).get(0);

            //ボタンの表示を選択したラインに変更する
            if (Objects.nonNull(line)) {
                lineName = line.getEquipmentName();
            }
            eventSrc.setText(lineName);
            setting.setLineId(line.getEquipmentId());
        }
    }

    /**
     * 読み込んだ設定からサイクルメロディのエンティティを取得する。 無い場合は新規作成して返す。
     *
     * @param melodyType サイクルメロディ種別
     * @return サイクルメロディ
     */
    private CycleMelodyInfoEntity getCycleMelodyInfoEntity(CycleMelodyTypeEnum melodyType) {
        Optional<CycleMelodyInfoEntity> melody = this.setting.getCycleMelodyCollection().stream()
                .filter(p -> melodyType.equals(p.getMelodyType())).findFirst();
        if (melody.isPresent()) {
            return melody.get();
        } else {
            return new CycleMelodyInfoEntity(melodyType, "");
        }
    }

    @Override
    public MonitorSettingFuji getInputResult() {
        logger.info("onRegistAction:{},{}", monitorId, setting);

        if (Objects.isNull(monitorId) || Objects.isNull(setting)) {
            return null;
        }

        // 当日計画数などはbindしてあるためそれを再利用する
        MonitorSettingFuji result = setting.clone();
        result.setMonitorType(AndonMonitorTypeEnum.LINE_PRODUCT);

        try {

            LocalTimeStringConverter converter = new LocalTimeStringConverter(DateTimeFormatter.ISO_LOCAL_TIME, DateTimeFormatter.ISO_LOCAL_TIME);

            // 表示タイトル
            result.setTitle(((SimpleStringProperty) detailProperties.get(DETAIL_LINE_NAME).getProperty()).get());
            // モデル名
            result.setModelName(((SimpleStringProperty) detailProperties.get(DETAIL_MODEL_NAME).getProperty()).get());
            // 作業開始時刻
            result.setStartWorkTime(converter.fromString(((SimpleStringProperty) detailProperties.get(DETAIL_START_TIME).getProperty()).get()));
            // 作業終了時刻
            result.setEndWorkTime(converter.fromString(((SimpleStringProperty) detailProperties.get(DETAIL_END_TIME).getProperty()).get()));
            // タクトタイム
            result.setLineTakt(converter.fromString(((SimpleStringProperty) detailProperties.get(DETAIL_TAKT_TIME).getProperty()).get()));


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

            // サイクルタクトタイム (サイクルタクトタイムフレーム用)
            long cycleNo = 1;
            List<CycleTaktInfoEntity> cycleTakts = new ArrayList<>();
            for (CycleTaktInfoEntity data : cycleTaktSettingCollection) {
                data.setCycleNo(cycleNo++);
                cycleTakts.add(data);
            }
            result.setCycleTaktCollection(cycleTakts);

            // サイクルメロディ (サイクルタクトタイムフレーム用)
            result.setCycleMelodyCollection(cycleMelodySettingCollection);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return result;
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

}
