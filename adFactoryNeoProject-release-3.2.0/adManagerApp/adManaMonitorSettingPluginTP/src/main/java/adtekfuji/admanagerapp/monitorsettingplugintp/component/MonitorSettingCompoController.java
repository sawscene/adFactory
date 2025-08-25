/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingplugintp.component;

import adtekfuji.clientservice.BreaktimeInfoFacade;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.DelayReasonInfoFacade;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.InterruptReasonInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.converter.LocalTimeStringConverter;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DelayReasonInfoEntity;
import jp.adtekfuji.adFactory.entity.master.InterruptReasonInfoEntity;
import jp.adtekfuji.adFactory.entity.search.EquipmentSearchCondition;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.enumerate.AndonMonitorTypeEnum;
import jp.adtekfuji.andon.property.MonitorSettingTP;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import jp.adtekfuji.andon.property.WorkSetting;
import jp.adtekfuji.javafxcommon.DetailRecordFactory;
import jp.adtekfuji.javafxcommon.PropertyBindEntityInterface;
import jp.adtekfuji.javafxcommon.SimplePropertyBindEntity;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 進捗モニタ設定コンポーネント
 *
 * @author s-heya
 */
@FxComponent(id = "MonitorSettingCompo", fxmlPath = "/fxml/monitorsettingplugintp/monitor_setting_compo.fxml")
public class MonitorSettingCompoController implements Initializable, ComponentHandler {

    class AndonMonitorTypeComboBoxCellFactory extends ListCell<AndonMonitorTypeEnum> {

        @Override
        protected void updateItem(AndonMonitorTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
            }
        }
    }
    Callback<ListView<AndonMonitorTypeEnum>, ListCell<AndonMonitorTypeEnum>> comboCellFactory = (ListView<AndonMonitorTypeEnum> param) -> new AndonMonitorTypeComboBoxCellFactory();
 
    private static final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final BreaktimeInfoFacade breaktimeInfoFacade = new BreaktimeInfoFacade();
    private final DelayReasonInfoFacade delayReasonInfoFacade = new DelayReasonInfoFacade();
    private final InterruptReasonInfoFacade interruptReasonInfoFacade = new InterruptReasonInfoFacade();
    private final AndonMonitorSettingFacade monitorSettingFacade = new AndonMonitorSettingFacade();
    private final long MAX_LOAD_SIZE = ClientServiceProperty.getRestRangeNum();
    private Long selectedMonitorId = null;
    private MonitorSettingTP monitorSetting = null;
    private final Map<Long, BreakTimeInfoEntity> breaktimeCollection = new HashMap<>();
    private final List<String> delayReasonCollection = new ArrayList<>();
    private final List<String> interruptReasonCollection = new ArrayList<>();
    private final LinkedList<BreakTimeIdData> breaktimeIdCollection = new LinkedList<>();
    private final LinkedList<WeekDayData> weekdayCollection = new LinkedList<>();
    private final LinkedList<WorkEquipmentSetting> workEquipmentSettngCollection = new LinkedList<>();
    private final LinkedList<WorkSetting> workSettngCollection = new LinkedList<>();
    private final LinkedList<ReasonData> delayReasonDataCollection = new LinkedList<>();
    private final LinkedList<ReasonData> interruptReasonDataCollection = new LinkedList<>();
    private final Map<String, SimplePropertyBindEntity> detailProperties = new LinkedHashMap<>();

    private LinkedList<WorkSetting> groupWorkCollection;
    private LinkedList<WorkEquipmentSetting> workActualCollection;
    private LinkedList<WorkSetting> suspendedWorkCollection;

    private static final String DETAIL_LINE_NAME = "lineName";
    private static final String DETAIL_SELECT_LINE = "selectLine";
    private static final String DETAIL_MODEL_NAME = "modelName";
    private static final String DETAIL_START_TIME = "startTime";
    private static final String DETAIL_END_TIME = "endTime";
    private static final String DETAIL_TAKT_TIME = "taktTime";

    @FXML
    private ListView<EquipmentInfoEntity> monitorList;
    @FXML
    private ComboBox<AndonMonitorTypeEnum> monitorTypeCombo;
    @FXML
    private VBox propertyPane;
    @FXML
    private Pane progressPane;

    //最初に表示された情報
    private MonitorSettingTP cloneInitialSetting;

    //変更保存時データベースからの読み込みを待機させるラッチ
    private CountDownLatch latch;

    private boolean isDeleteEquipment;// 削除された設備が使用されていた？

    
    /**
     * 進捗モニター設定画面を初期化する。
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(true);

        monitorList.setCellFactory((ListView<EquipmentInfoEntity> param) -> {
            ListCell<EquipmentInfoEntity> cell = new ListCell<EquipmentInfoEntity>() {
                @Override
                protected void updateItem(EquipmentInfoEntity e, boolean bln) {
                    super.updateItem(e, bln);
                    if (e != null) {
                        setText(e.getEquipmentName() + "(" + e.getEquipmentIdentify() + ")");
                    }
                }
            };
            return cell;
        });

        monitorList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends EquipmentInfoEntity> observable, EquipmentInfoEntity oldValue, EquipmentInfoEntity newValue) -> {
            if ((Objects.nonNull(newValue) && oldValue != newValue)
                && !Objects.equals(selectedMonitorId, newValue.getEquipmentId())) {

                if (!registConfirm(false)) {
                    // 保存に失敗した
                    int index = monitorList.getItems().indexOf(oldValue);
                    monitorList.getSelectionModel().clearAndSelect(index);
                    return;
                }

                // 保存されるまで待機
                if (Objects.nonNull(latch)) {
                    try {
                        latch.await();
                    } catch (Exception e) {
                        logger.fatal(e, e);
                    }
                }

                updateMonitorSetting(newValue.getEquipmentId());
            }
        });

        // モニター種別設定
        this.monitorTypeCombo.setItems(FXCollections.observableArrayList(AndonMonitorTypeEnum.values()));
        this.monitorTypeCombo.getSelectionModel().select(AndonMonitorTypeEnum.LINE_PRODUCT);
        this.monitorTypeCombo.setButtonCell(new AndonMonitorTypeComboBoxCellFactory());
        this.monitorTypeCombo.setCellFactory(comboCellFactory);
        this.monitorTypeCombo.setEditable(false);

        // 進捗モニタ設備リストを更新する。
        updateMonitorList();
    }

    /**
     * 変更を確認して保存する
     *
     * @return キャンセルを押した、または保存に失敗した場合false
     */
    private boolean registConfirm(boolean isDispCancel) {
        String title = null;
        String message = null;
        if (isChanged()) {
            // 入力内容が保存されていません。保存しますか?
            title = LocaleUtils.getString("key.confirm");
            message = LocaleUtils.getString("key.confirm.destroy");
        } else if (isDeleteEquipment) {
            // 存在しない設備・休憩時間の割当が削除されています。保存しますか?
            title = LocaleUtils.getString("key.confirm");
            message = LocaleUtils.getString("key.confirm.monitorsetting2");
        }

        if (Objects.nonNull(message)) {
            ButtonType buttonType;
            if (isDispCancel) {
                buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            } else {
                buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
            }

            if (ButtonType.YES == buttonType) {
                return registData();
            } else if (ButtonType.CANCEL == buttonType) {
                return false;
            }
        }
        return true;
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        progressPane.setVisible(flg);
    }

    private void updateMonitorList() {
        logger.info("updateMonitorList start.");
        try {
            blockUI(true);

            Task task = new Task<SortedList<EquipmentInfoEntity>>() {
                @Override
                protected SortedList<EquipmentInfoEntity> call() throws Exception {
                    return readMonitorList();
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // リストを更新する。
                        monitorList.setItems(this.get());

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                        logger.info("updateMonitorList end.");
                    }
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
                    } finally {
                        blockUI(false);
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    private SortedList<EquipmentInfoEntity> readMonitorList() {
        //アンドンモニタ設備一覧取得.
        List<EquipmentInfoEntity> monitors = new ArrayList<>();
        EquipmentSearchCondition condition = new EquipmentSearchCondition().equipmentType(EquipmentTypeEnum.MONITOR);
        long max = equipmentInfoFacade.countSearch(condition);
        for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
            monitors.addAll(equipmentInfoFacade.findSearchRange(condition, count, count + MAX_LOAD_SIZE - 1));
        }

        //設定で使用する休憩パターン一覧取得.
        breaktimeCollection.clear();
        for (BreakTimeInfoEntity breakTime : breaktimeInfoFacade.findAll()) {
            breaktimeCollection.put(breakTime.getBreaktimeId(), breakTime);
        }

        //遅延理由一覧取得.
        delayReasonCollection.clear();
        for (DelayReasonInfoEntity delayReason : delayReasonInfoFacade.findAll()) {
            delayReasonCollection.add(delayReason.getDelayReason());
        }

        //中断理由一覧取得.
        interruptReasonCollection.clear();
        for (InterruptReasonInfoEntity interruptReason : interruptReasonInfoFacade.findAll()) {
            interruptReasonCollection.add(interruptReason.getInterruptReason());
        }

        SortedList<EquipmentInfoEntity> sortedList = new SortedList<>(FXCollections.observableArrayList(monitors));
        sortedList.setComparator((EquipmentInfoEntity o1, EquipmentInfoEntity o2) -> o1.getEquipmentName().compareTo(o2.getEquipmentName()));

        return sortedList;
    }

    /**
     * 画面をクリアする
     *
     */
    private void clearDetailView() {
        cloneInitialSetting = null;//クローンをクリア　何も表示されてないことを明確にする。

        breaktimeIdCollection.clear();
        weekdayCollection.clear();
        workEquipmentSettngCollection.clear();
        workSettngCollection.clear();
        delayReasonDataCollection.clear();
        interruptReasonDataCollection.clear();

        if (Objects.nonNull(groupWorkCollection)) {
            groupWorkCollection.clear();
        }
        if (Objects.nonNull(workActualCollection)) {
            workActualCollection.clear();
        }
        if (Objects.nonNull(suspendedWorkCollection)) {
            suspendedWorkCollection.clear();
        }
    }

    private void updateMonitorSetting(Long monitorId) {
        //表示されてる情報をクリア
        clearDetailView();

        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                readMonitorSetting(monitorId);
                return null;
            }
        };
        new Thread(task).start();
    }

    private final EventHandler onActionEvent = (EventHandler) (Event event) -> {
        //
        Button cellButton = (Button) event.getSource();
        EquipmentInfoEntity line = (EquipmentInfoEntity) cellButton.getUserData();
        //
        SelectDialogEntity<EquipmentInfoEntity> selectDialogEntity = new SelectDialogEntity();
        if (Objects.nonNull(line)) {
            selectDialogEntity.equipments(Arrays.asList(line));
        }
        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Equipment"), "EquipmentSelectionCompo", selectDialogEntity);
        if (ret.equals(ButtonType.OK)) {
            String lineName = LocaleUtils.getString("key.AndonLineSettingNotAllocate");
            if (!selectDialogEntity.getEquipments().isEmpty()) {
                line = selectDialogEntity.getEquipments().get(0);
                monitorSetting.setLineId(line.getEquipmentId());
                lineName = line.getEquipmentName();
            } else {
                // 割り当てなし
                line = null;
                monitorSetting.setLineId(0L);
            }
            cellButton.setText(lineName);
            cellButton.setUserData(line);
        }
    };

    private void readMonitorSetting(Long monitorId) {
        logger.info("readMonitorSetting:{}", monitorId);
        try {
            isDeleteEquipment = false;

            if (this.monitorTypeCombo.getSelectionModel().getSelectedItem() == AndonMonitorTypeEnum.LINE_PRODUCT) {
                selectedMonitorId = monitorId;
                monitorSetting = (MonitorSettingTP) monitorSettingFacade.getLineSetting(monitorId, MonitorSettingTP.class);

                //LinkedList<PropertyBindEntityInterface> settings1 = new LinkedList<>();
                EquipmentInfoEntity line = null;
                String lineName = LocaleUtils.getString("key.AndonLineSettingNotAllocate");
                if (Objects.nonNull(monitorSetting.getLineId()) && monitorSetting.getLineId() != 0) {
                    line = CacheUtils.getCacheEquipment(monitorSetting.getLineId());
                    if (Objects.nonNull(line) && Objects.nonNull(line.getEquipmentId())) {
                        lineName = line.getEquipmentName();
                    } else {
                        // 削除された設備
                        isDeleteEquipment = true;
                        line = null;
                        monitorSetting.setLineId(0L);
                    }
                }

                //settings1.add(SimplePropertyBindEntity.createString(LocaleUtils.getString("key.AndonLineSettingTitle"), monitorSetting.titleProperty()));
                //settings1.add(SimplePropertyBindEntity.createButton(LocaleUtils.getString("key.AndonLineSettingSelectLine"), new SimpleStringProperty(lineName), onActionEvent, line));
                //settings1.add(SimplePropertyBindEntity.createLocalTime(LocaleUtils.getString("key.AndonLineSettingStartTime"), monitorSetting.startWorkTimeProperty()));
                //settings1.add(SimplePropertyBindEntity.createLocalTime(LocaleUtils.getString("key.AndonLineSettingEndTime"), monitorSetting.endWorkTimeProperty()));
                // 表示タイトル
                detailProperties.put(DETAIL_LINE_NAME, SimplePropertyBindEntity.createString(LocaleUtils.getString("key.AndonLineSettingTitle"), monitorSetting.titleProperty()));
                // 対象ライン
                detailProperties.put(DETAIL_SELECT_LINE, SimplePropertyBindEntity.createButton(LocaleUtils.getString("key.AndonLineSettingSelectLine"), new SimpleStringProperty(lineName), onActionEvent, line));
                // モデル名
                detailProperties.put(DETAIL_MODEL_NAME, SimplePropertyBindEntity.createString(LocaleUtils.getString("key.ModelName"), monitorSetting.modelNameProperty()));
                // 作業開始時刻
                detailProperties.put(DETAIL_START_TIME, SimplePropertyBindEntity.createLocalTime(LocaleUtils.getString("key.AndonLineSettingStartTime"), monitorSetting.startWorkTimeProperty()));
                // 作業終了時刻
                detailProperties.put(DETAIL_END_TIME, SimplePropertyBindEntity.createLocalTime(LocaleUtils.getString("key.AndonLineSettingEndTime"), monitorSetting.endWorkTimeProperty()));
                // タクトタイム
                detailProperties.put(DETAIL_TAKT_TIME, SimplePropertyBindEntity.createLocalTime(LocaleUtils.getString("key.TactTime"), monitorSetting.lineTaktProperty()));

                breaktimeIdCollection.clear();
                for (BreakTimeInfoEntity b : monitorSetting.getBreaktimes()) {
                    breaktimeIdCollection.add(new BreakTimeIdData(b.getBreaktimeId()));
                }
                weekdayCollection.clear();
                for (DayOfWeek d : monitorSetting.getWeekdays()) {
                    weekdayCollection.add(new WeekDayData(d));
                }
                workEquipmentSettngCollection.clear();
                for (WorkEquipmentSetting w : monitorSetting.getWorkEquipmentCollection()) {
                    List<Long> ids = w.getEquipmentIds();
                    List<EquipmentInfoEntity> equipments = CacheUtils.getCacheEquipment(ids);
                    if (equipments.size() < ids.size()) {
                        // 削除された設備がある。
                        isDeleteEquipment = true;

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
                workSettngCollection.clear();
                for (WorkSetting w : monitorSetting.getWorkCollection()) {
                    workSettngCollection.add(w);
                }
                delayReasonDataCollection.clear();
                for (String s : monitorSetting.getDelayReasonCollection()) {
                    delayReasonDataCollection.add(new ReasonData(s));
                }
                interruptReasonDataCollection.clear();
                for (String s : monitorSetting.getInterruptReasonCollection()) {
                    interruptReasonDataCollection.add(new ReasonData(s));
                }

                this.groupWorkCollection = new LinkedList<>(monitorSetting.getGroupWorkCollection());
                this.workActualCollection = new LinkedList<>(monitorSetting.getWorkActualCollection());
                this.suspendedWorkCollection = new LinkedList<>(monitorSetting.getSuspendedWorkCollection());

                LinkedList<PropertyBindEntityInterface> settings2 = new LinkedList<>();
                // 当日計画数
                settings2.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AndonLineSettingDailyPlan"), monitorSetting.dailyPlanNumProperty()));
                // 当月計画数
                settings2.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AndonLineSettingMontlyPlan"), monitorSetting.montlyPlanNumProperty()));
                // 注意
                settings2.add(SimplePropertyBindEntity.createColorPicker(LocaleUtils.getString("key.AndonLineSettingCautionFontColor"), monitorSetting.cautionFontColorProperty()));
                settings2.add(SimplePropertyBindEntity.createColorPicker(LocaleUtils.getString("key.AndonLineSettingCautionBackColor"), monitorSetting.cautionBackColorProperty()));
                // 警告
                settings2.add(SimplePropertyBindEntity.createColorPicker(LocaleUtils.getString("key.AndonLineSettingWarningFontColor"), monitorSetting.warningFontColorProperty()));
                settings2.add(SimplePropertyBindEntity.createColorPicker(LocaleUtils.getString("key.AndonLineSettingWarningBackColor"), monitorSetting.warningBackColorProperty()));
                // 滞留量
                settings2.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AndonLineSettingCautionParcent"), monitorSetting.cautionRetentionParcentProperty()));
                settings2.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AndonLineSettingWarningParcent"), monitorSetting.warningRetentionParcentProperty()));

                // グループ進捗
                LinkedList<PropertyBindEntityInterface> groupSettings = new LinkedList<>();
                groupSettings.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AttentionThreshold") + "[台]", monitorSetting.groupAttenThresholdProperty()));
                groupSettings.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.WarningThreshold") + "[台]", monitorSetting.groupWarnThresholdProperty()));
                groupSettings.add(SimplePropertyBindEntity.createInteger("差異表示の対象工程", monitorSetting.yieldDiffProperty()));

                // 工程実績進捗
                LinkedList<PropertyBindEntityInterface> workActualSettings = new LinkedList<>();
                workActualSettings.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AttentionThreshold") + "[台]", monitorSetting.workAttenThresholdProperty()));
                workActualSettings.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.WarningThreshold") + "[台]", monitorSetting.workWarnThresholdProperty()));

                // 中断発生率
                LinkedList<PropertyBindEntityInterface> suspendedSettings = new LinkedList<>();
                suspendedSettings.add(SimplePropertyBindEntity.createString(LocaleUtils.getString("key.AndonLineSettingEquipTitle"), monitorSetting.suspendedTitleProperty()));
                suspendedSettings.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.AttentionThreshold") + "[%]", monitorSetting.suspendedAttenThresholdProperty()));
                suspendedSettings.add(SimplePropertyBindEntity.createInteger(LocaleUtils.getString("key.WarningThreshold") + "[%]", monitorSetting.suspendedWarnThresholdProperty()));

                Platform.runLater(() -> {
                    propertyPane.getChildren().clear();
                    //
                    Table detailTable1 = new Table(propertyPane.getChildren()).isAddRecord(false);
                    //detailTable1.setAbstractRecordFactory(new DetailRecordFactory(detailTable1, settings1));
                    detailTable1.setAbstractRecordFactory(new DetailRecordFactory(detailTable1, new LinkedList(detailProperties.values())));

                    //
                    Table breaktimeTable = new Table(propertyPane.getChildren()).title(LocaleUtils.getString("key.AndonLineSettingBreakTime"))
                            .isAddRecord(true).styleClass("ContentTitleLabel");
                    breaktimeTable.setAbstractRecordFactory(new BreakTimeRecordFactory(breaktimeTable, new ArrayList(breaktimeCollection.values()), breaktimeIdCollection));
                    //
                    Table weekdayTable = new Table(propertyPane.getChildren()).title(LocaleUtils.getString("key.AndonLineSettingWeekday"))
                            .isAddRecord(true).isColumnTitleRecord(true).styleClass("ContentTitleLabel");
                    weekdayTable.setAbstractRecordFactory(new WeekDayRecordFactory(weekdayTable, Arrays.asList(DayOfWeek.values()), weekdayCollection));
                    //
                    Table detailTable2 = new Table(propertyPane.getChildren()).isAddRecord(false);
                    detailTable2.setAbstractRecordFactory(new DetailRecordFactory(detailTable2, settings2));
                    //
                    Table delayReasonTable = new Table(propertyPane.getChildren()).isAddRecord(true).title(LocaleUtils.getString("key.EditDelayReasonTitle"))
                            .isAddRecord(true).isColumnTitleRecord(true).styleClass("ContentTitleLabel");
                    delayReasonTable.setAbstractRecordFactory(new ReasonRecordFactory(delayReasonTable, delayReasonCollection, delayReasonDataCollection));
                    //
                    Table interruptReasonTable = new Table(propertyPane.getChildren()).isAddRecord(true).title(LocaleUtils.getString("key.EditInterruptReasonTitle"))
                            .isAddRecord(true).isColumnTitleRecord(true).styleClass("ContentTitleLabel");
                    interruptReasonTable.setAbstractRecordFactory(new ReasonRecordFactory(interruptReasonTable, interruptReasonCollection, interruptReasonDataCollection));

                    // 設備
                    Table equipmentTable = new Table(propertyPane.getChildren()).isAddRecord(true).maxRecord(50);
                    equipmentTable.title(LocaleUtils.getString("key.AndonLineSettingSelectEquipment")).styleClass("ContentTitleLabel");
                    equipmentTable.isColumnTitleRecord(true);
                    equipmentTable.setAbstractRecordFactory(new WorkEquipmentRecordFactory(equipmentTable, workEquipmentSettngCollection));

                    // 対象工程選択
                    // タイトルと実テーブルの間に設定を追加するため分離して作成
                    final VBox workTables = new VBox();
                    workTables.setPadding(new Insets(10, 0, 0, 0));
                    workTables.setSpacing(10);
                    propertyPane.getChildren().add(workTables);

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
                            workFactory.setDisablePlanNum(monitorSetting.getUseDailyPlanNum());
                        });
                    });

                    final SimplePropertyBindEntity<Boolean> useLinePlan = SimplePropertyBindEntity.createBoolean(LocaleUtils.getString("key.AndonLineSettingUseDailyPlan"), "", monitorSetting.useDailyPlanNumProperty())
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
                        workFactory.setDisablePlanNum(monitorSetting.getUseDailyPlanNum());
                    });

                    // グループ進捗
                    Table groupTable1 = new Table(propertyPane.getChildren()).isAddRecord(false).title(LocaleUtils.getString("key.GroupProgress")).styleClass("text-llarge-bold");
                    groupTable1.setAbstractRecordFactory(new DetailRecordFactory(groupTable1, groupSettings));
                    Table groupTable2 = new Table(propertyPane.getChildren()).isAddRecord(true).maxRecord(10);
                    //groupTable2.title(LocaleUtils.getString("key.AndonLineSettingSelectWork")).styleClass("ContentTitleLabel");
                    groupTable2.setTitle(false);
                    groupTable2.isColumnTitleRecord(true);
                    groupTable2.setAbstractRecordFactory(new WorkRecordFactory(groupTable2, this.groupWorkCollection));

                    // 工程実績進捗
                    Table workActualTable1 = new Table(propertyPane.getChildren()).isAddRecord(false).title(LocaleUtils.getString("key.WorkActual")).styleClass("text-llarge-bold");
                    workActualTable1.setAbstractRecordFactory(new DetailRecordFactory(workActualTable1, workActualSettings));
                    Table workActualTable2 = new Table(propertyPane.getChildren()).isAddRecord(true).maxRecord(24);
                    //workActualTable2.title(LocaleUtils.getString("key.AndonLineSettingSelectEquipment")).styleClass("ContentTitleLabel");
                    workActualTable2.setTitle(false);
                    workActualTable2.isColumnTitleRecord(true);
                    workActualTable2.setAbstractRecordFactory(new WorkActualRecordFactory(workActualTable2, this.workActualCollection));

                    // 中断発生率
                    Table suspendedTable1 = new Table(propertyPane.getChildren()).isAddRecord(false).title(LocaleUtils.getString("key.SuspendedRate")).styleClass("text-llarge-bold");
                    suspendedTable1.setAbstractRecordFactory(new DetailRecordFactory(suspendedTable1, suspendedSettings));
                    Table suspendedTable2 = new Table(propertyPane.getChildren()).isAddRecord(true).maxRecord(10);
                    //suspendedTable2.title(LocaleUtils.getString("key.AndonLineSettingSelectWork")).styleClass("ContentTitleLabel");
                    suspendedTable2.setTitle(false);
                    suspendedTable2.isColumnTitleRecord(true);
                    suspendedTable2.setAbstractRecordFactory(new SuspendedRecordFactory(suspendedTable2, this.suspendedWorkCollection));

                    //最初に表示された情報をコピー
                    cloneInitialSetting = getRegistData().clone();
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 保存を実施する
     *
     * @return 保存を実施したらtrue　保存が実施できなかったときfalse
     */
    private boolean registData() {
        logger.info("registData:{},{}", selectedMonitorId, monitorSetting);
        
        if (Objects.isNull(selectedMonitorId) || Objects.isNull(monitorSetting)) {
            return false;
        }
        
        if (!validItems()) {
            return false;
        }

        //保存終了までリストの移動を止める
        latch = new CountDownLatch(1);

        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    LocalTimeStringConverter converter = new LocalTimeStringConverter(DateTimeFormatter.ISO_LOCAL_TIME, DateTimeFormatter.ISO_LOCAL_TIME);
                    Function<String, SimpleStringProperty> getSSP = (s) -> {
                        return (SimpleStringProperty) detailProperties.get(s).getProperty();
                    };

                    // 表示タイトル
//                    monitorSetting.setTitle(getSSP.apply(DETAIL_LINE_NAME).get());
                    monitorSetting.setTitle(((SimpleStringProperty) detailProperties.get(DETAIL_LINE_NAME).getProperty()).get());
                    // モデル名
//                    monitorSetting.setModelName(getSSP.apply(DETAIL_MODEL_NAME).get());
                    monitorSetting.setModelName(((SimpleStringProperty) detailProperties.get(DETAIL_MODEL_NAME).getProperty()).get());
                    // 作業開始時刻
//                    monitorSetting.setStartWorkTime(converter.fromString(getSSP.apply(DETAIL_START_TIME).get()));
                    monitorSetting.setStartWorkTime(converter.fromString(((SimpleStringProperty) detailProperties.get(DETAIL_START_TIME).getProperty()).get()));
                    // 作業終了時刻
//                    monitorSetting.setEndWorkTime(converter.fromString(getSSP.apply(DETAIL_END_TIME).get()));
                    monitorSetting.setEndWorkTime(converter.fromString(((SimpleStringProperty) detailProperties.get(DETAIL_END_TIME).getProperty()).get()));
                    // タクトタイム
//                    monitorSetting.setLineTakt(converter.fromString(getSSP.apply(DETAIL_TAKT_TIME).get()));
                    monitorSetting.setLineTakt(converter.fromString(((SimpleStringProperty) detailProperties.get(DETAIL_TAKT_TIME).getProperty()).get()));

                    List<BreakTimeInfoEntity> breaktimes = new ArrayList<>();
                    for (BreakTimeIdData id : breaktimeIdCollection) {
                        breaktimes.add(breaktimeCollection.get(id.getId()));
                    }
                    monitorSetting.setBreaktimes(breaktimes);

                    List<DayOfWeek> dayweeks = new ArrayList<>();
                    for (WeekDayData data : weekdayCollection) {
                        dayweeks.add(data.getWeekDay());
                    }
                    monitorSetting.setWeekdays(dayweeks);

                    int order = 1;
                    List<WorkEquipmentSetting> workEquipmentSettngs = new ArrayList<>();
                    for (WorkEquipmentSetting data : workEquipmentSettngCollection) {
                        data.setOrder(order++);
                        workEquipmentSettngs.add(data);
                    }
                    monitorSetting.setWorkEquipmentCollection(workEquipmentSettngs);

                    int orderWork = 1;
                    List<WorkSetting> workSettngs = new ArrayList<>();
                    for (WorkSetting data : workSettngCollection) {
                        data.setOrder(orderWork);
                        data.setPluginName(String.format(LocaleUtils.getString("key.DailyActualNumPerWork"), orderWork));
                        orderWork++;
                        workSettngs.add(data);
                    }
                    monitorSetting.setWorkCollection(workSettngs);

                    List<String> delays = new ArrayList<>();
                    for (ReasonData reason : delayReasonDataCollection) {
                        delays.add(reason.getReason());
                    }
                    monitorSetting.setDelayReasonCollection(delays);

                    List<String> interrupts = new ArrayList<>();
                    for (ReasonData reason : interruptReasonDataCollection) {
                        interrupts.add(reason.getReason());
                    }
                    monitorSetting.setInterruptReasonCollection(interrupts);

                    // グループ情報
                    order = 1;
                    monitorSetting.getGroupWorkCollection().clear();
                    for (WorkSetting workSetting : groupWorkCollection) {
                        workSetting.setOrder(order++);
                        monitorSetting.getGroupWorkCollection().add(workSetting);
                    }

                    // 工程実績情報
                    order = 1;
                    monitorSetting.getWorkActualCollection().clear();
                    for (WorkEquipmentSetting workEquipmentSetting : workActualCollection) {
                        workEquipmentSetting.setOrder(order++);
                        monitorSetting.getWorkActualCollection().add(workEquipmentSetting);
                    }

                    // 中断発生率情報
                    order = 1;
                    monitorSetting.getSuspendedWorkCollection().clear();
                    for (WorkSetting workSetting : suspendedWorkCollection) {
                        workSetting.setOrder(order++);
                        monitorSetting.getSuspendedWorkCollection().add(workSetting);
                    }

                    //登録した値を初期値に
                    cloneInitialSetting = monitorSetting.clone();
                    isDeleteEquipment = false;

                    monitorSettingFacade.setLineSetting(selectedMonitorId, monitorSetting);
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                    latch.countDown();
                }
                return null;
            }
        };
        new Thread(task).start();

        return true;
    }

    /**
     * 登録
     *
     * @param event
     */
    @FXML
    private void onRegistAction(ActionEvent event) {
        logger.info("onRegistAction:{},{}", selectedMonitorId, monitorSetting);
        registData();
    }

    /**
     * 入力した項目が有効なものか調べる。
     *
     * @return 全て正常に入力された場合true
     */
    private boolean validItems() {

        // 表示タイトル
        if (Objects.nonNull(monitorSetting.getTitle())
                && monitorSetting.getTitle().getBytes().length > 256) {
            String message = new StringBuilder(LocaleUtils.getString("key.warn.enterCharacters256"))
                    .append("\r(").append(LocaleUtils.getString("key.AndonLineSettingTitle")).append(")")
                    .toString();
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message);
            return false;
        }

        // モデル名
        if (Objects.nonNull(monitorSetting.getModelName())
                && monitorSetting.getModelName().getBytes().length > 256) {
            String message = new StringBuilder(LocaleUtils.getString("key.warn.enterCharacters256"))
                    .append("\r(").append(LocaleUtils.getString("key.ModelName")).append(")")
                    .toString();
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message);
            return false;
        }

        // 作業開始時刻, 作業終了時刻
        if (monitorSetting.getStartWorkTime().isAfter(monitorSetting.getEndWorkTime())) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DateCompErrMessage"));
            return false;
        }

        // 休憩時間
        Set<Long> set1 = new HashSet<>();
        for (BreakTimeIdData breakTimeId : breaktimeIdCollection) {
            if (set1.contains(breakTimeId.getId())) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), breaktimeCollection.get(breakTimeId.getId()).getBreaktimeName()));
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
        for (WorkEquipmentSetting workEquipment : monitorSetting.getWorkEquipmentCollection()) {
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
        for (WorkSetting work : monitorSetting.getWorkCollection()) {
            if (Objects.nonNull(work.getTitle()) && work.getTitle().getBytes().length > 256) {
                String message = new StringBuilder(LocaleUtils.getString("key.warn.enterCharacters256"))
                        .append("\r(").append(LocaleUtils.getString("key.AndonLineSettingSelectWork"))
                        .append(" - ").append(LocaleUtils.getString("key.DisplayName")).append(")")
                        .toString();
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message);
                return false;
            }
        }

        // 中断発生率
        for (WorkSetting suspendedWork : suspendedWorkCollection) {
            if (Objects.nonNull(suspendedWork.getTitle()) && suspendedWork.getTitle().getBytes().length > 256) {
                String message = new StringBuilder(LocaleUtils.getString("key.warn.enterCharacters256"))
                        .append("\r(").append(LocaleUtils.getString("key.SuspendedRate"))
                        .append(" - ").append(LocaleUtils.getString("key.ProcessName")).append(")")
                        .toString();
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message);
                return false;
            }
        }
        
        if (Objects.nonNull(monitorSetting.yieldDiffProperty().getValue())) {
            if (0 > monitorSetting.yieldDiffProperty().getValue()
                || groupWorkCollection.size() < monitorSetting.yieldDiffProperty().getValue()) {
                // 誤った差異表示の対象工程が指定されています
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), "誤った差異表示の対象工程が指定されています");
                return false;
            }
        }

        return true;
    }

    /**
     * 現在フィールドに表示されている情報を取得する
     *
     * @return
     */
    private MonitorSettingTP getRegistData() {
        logger.info("getRegistData:{},{}", selectedMonitorId, monitorSetting);

        if (Objects.isNull(selectedMonitorId) || Objects.isNull(monitorSetting)) {
            return null;
        }

        MonitorSettingTP setting = monitorSetting.clone();

        try {
            List<BreakTimeInfoEntity> breaktimes = new ArrayList<>();
            for (BreakTimeIdData id : breaktimeIdCollection) {
                breaktimes.add(breaktimeCollection.get(id.getId()));
            }
            setting.setBreaktimes(breaktimes);

            List<DayOfWeek> dayweeks = new ArrayList<>();
            for (WeekDayData data : weekdayCollection) {
                dayweeks.add(data.getWeekDay());
            }
            setting.setWeekdays(dayweeks);

            int order = 1;
            List<WorkEquipmentSetting> workEquipmentSettngs = new ArrayList<>();
            for (WorkEquipmentSetting data : workEquipmentSettngCollection) {
                data.setOrder(order++);
                workEquipmentSettngs.add(data);
            }
            setting.setWorkEquipmentCollection(workEquipmentSettngs);

            int orderWork = 1;
            List<WorkSetting> workSettngs = new ArrayList<>();
            for (WorkSetting data : workSettngCollection) {
                data.setOrder(orderWork);
                data.setPluginName(String.format(LocaleUtils.getString("key.DailyActualNumPerWork"), orderWork));
                orderWork++;
                workSettngs.add(data);
            }
            setting.setWorkCollection(workSettngs);

            List<String> delays = new ArrayList<>();
            for (ReasonData reason : delayReasonDataCollection) {
                delays.add(reason.getReason());
            }
            setting.setDelayReasonCollection(delays);

            List<String> interrupts = new ArrayList<>();
            for (ReasonData reason : interruptReasonDataCollection) {
                interrupts.add(reason.getReason());
            }
            setting.setInterruptReasonCollection(interrupts);

            // グループ情報
            order = 1;
            setting.getGroupWorkCollection().clear();
            for (WorkSetting workSetting : groupWorkCollection) {
                workSetting.setOrder(order++);
                setting.getGroupWorkCollection().add(workSetting);
            }

            // 工程実績情報
            order = 1;
            setting.getWorkActualCollection().clear();
            for (WorkEquipmentSetting workEquipmentSetting : workActualCollection) {
                workEquipmentSetting.setOrder(order++);
                setting.getWorkActualCollection().add(workEquipmentSetting);
            }

            // 中断発生率情報
            order = 1;
            setting.getSuspendedWorkCollection().clear();
            for (WorkSetting workSetting : suspendedWorkCollection) {
                workSetting.setOrder(order++);
                setting.getSuspendedWorkCollection().add(workSetting);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return setting;
    }

    /**
     * 最初に表示した情報から変更がないか調べる
     *
     * @return
     */
    private boolean isChanged() {
        MonitorSettingTP currentSetting = getRegistData();

        if (Objects.isNull(currentSetting) || Objects.isNull(cloneInitialSetting)) {
            return false;
        }

        if (currentSetting.equalsDisplayInfo(cloneInitialSetting)) {
            return false;
        }

        return true;
    }

    /**
     * コンポーネントが破棄される前に呼び出される。破棄できない場合は、falseを返す。 ほかの画面に遷移するとき変更が存在するなら保存するか確認する
     *
     * @return
     */
    @Override
    public boolean destoryComponent() {
        try {
            logger.info("destoryComponent start.");

            return registConfirm(true);
        } finally {
            logger.info("destoryComponent end.");
        }
    }
}
