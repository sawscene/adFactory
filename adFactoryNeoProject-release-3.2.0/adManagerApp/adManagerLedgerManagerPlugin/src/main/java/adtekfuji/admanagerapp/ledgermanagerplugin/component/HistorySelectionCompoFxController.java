/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.component;

import adtekfuji.admanagerapp.ledgermanagerplugin.entity.InspectionListTableDataEntity;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.LedgerInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateUtils;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerConditionEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerInfoEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerTargetEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LedgerTypeEnum;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

/**
 * カンバンステータス変更ダイアログ
 *
 * @author HN)y-harada
 */
@FxComponent(id = "InspectionHistorySelectionDialog", fxmlPath = "/fxml/compo/history_selection_dialog.fxml")
public class HistorySelectionCompoFxController implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static final String DISABLE_DATE_STYLE = "-fx-background-color: lightgray;";// カレンダーで選択不可な日のスタイル
    private final static LedgerInfoFacade ledgerInfoFacade = new LedgerInfoFacade();

    @FXML
    Pane AnchorPane;

    private Dialog dialog;

    @FXML
    SplitPane reportHistorySelectionSplitPane;
    @FXML
    private Pane Progress;

    @FXML
    private TableView<InspectionListTableDataEntity> reportListTableView;
    @FXML
    private TableColumn<InspectionListTableDataEntity, Boolean> checkColumn = new TableColumn<>("Selected"); // チェック
    @FXML
    private TableColumn<InspectionListTableDataEntity, String> dateColumn; // 作業日時
    @FXML
    private TableColumn<InspectionListTableDataEntity, String> workflowNameColumn; // 工程順名
    @FXML
    private TableColumn<InspectionListTableDataEntity, String> workNameColumn; // 工程
    @FXML
    private TableColumn<InspectionListTableDataEntity, String> equipmentNameColumn; // 設備
    @FXML
    private TableColumn<InspectionListTableDataEntity, String> workerNameColumn; // 作業者

    @FXML
    CheckBox dateCheckBox;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;

    @FXML
    private SelectPane equipmentSelectPaneController;
    @FXML
    private SelectPane organizationSelectPaneController;

    @FXML
    private Button ledgerOut;

    private LedgerInfoEntity ledgerInfoEntity;
    private LedgerConditionEntity ledgerConditionEntity;

    private String filePath = "";
    private List<LedgerTargetEntity> ledgerTargetEntities;
    private List<OrganizationInfoEntity> organizationInfoEntities;
    private List<EquipmentInfoEntity> equipmentInfoEntities;

    private EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private Map<Long, EquipmentInfoEntity> equipmentMap;

    /**
     * コンストラクタ
     */
    public HistorySelectionCompoFxController() {
    }

    /**
     * 全列チェック
     *
     * @pram check True=チェック False=チェックを外す
     */
    private void allCheck(boolean check) {
        try {
            this.reportListTableView
                    .getItems()
                    .stream()
                    .map(InspectionListTableDataEntity::selectedProperty)
                    .forEach(item->item.set(check));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }


    // グルーピングするキーを作成
    private String createGroupKey(ActualResultEntity actualResult) {
        return actualResult.getImplementDatetime() + "#####"
                + actualResult.getFkWorkflowId() + "#####"
                + actualResult.getFkEquipmentId() + "#####"
                + actualResult.getFkOrganizationId() + "####";
    }

    /**
     * 表示を更新する
    */
    void refreshLedgerFileList() {
        try {
            blockUI(true);
            // 表を更新
            this.reportListTableView.getColumns().clear();
            this.reportListTableView.getColumns().add(checkColumn);

            ledgerInfoEntity
                    .getLedgerCondition()
                    .getKeyTag()
                    .forEach(entity -> {
                        TableColumn<InspectionListTableDataEntity, String> addColumn = new TableColumn<>(entity.getName());
                        addColumn.setCellValueFactory(f -> f.getValue().addColumnProperty(entity));
                        addColumn.setStyle("-fx-alignment: center-left;");
                        this.reportListTableView.getColumns().add(addColumn);
                    });

            // 実績を取得
            List<ActualResultEntity> actualResultEntities = ledgerInfoFacade.getHistory(
                    this.ledgerInfoEntity.getLedgerId(),
                    this.equipmentInfoEntities.stream().map(EquipmentInfoEntity::getEquipmentId).collect(toList()),
                    this.organizationInfoEntities.stream().map(OrganizationInfoEntity::getOrganizationId).collect(toList()),
                    dateCheckBox.isSelected() ? DateUtils.getBeginningOfDate(fromDatePicker.getValue()) : null,
                    dateCheckBox.isSelected() ? DateUtils.getEndOfDate(toDatePicker.getValue()) : null
            );

            this.equipmentMap
                    = equipmentInfoFacade.findAll()
                    .stream()
                    .collect(toMap(EquipmentInfoEntity::getEquipmentId, Function.identity(), (a, b)->a));

            List<InspectionListTableDataEntity> inspectionListTableDataEntities
                    = actualResultEntities
                    .stream()
                    .collect(groupingBy(this::createGroupKey, toList()))
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .map(list -> new InspectionListTableDataEntity(list, this.equipmentMap))
                    .collect(toList());
            this.reportListTableView.setItems(FXCollections.observableArrayList(inspectionListTableDataEntities));

            this.reportListTableView.getColumns().add(dateColumn);
            this.reportListTableView.getColumns().add(workflowNameColumn);
            this.reportListTableView.getColumns().add(workNameColumn);
            this.reportListTableView.getColumns().add(equipmentNameColumn);
            this.reportListTableView.getColumns().add(workerNameColumn);
        } finally {
            blockUI(false);
        }
    }



    /**
     * カンバンステータス変更ダイアログを初期化する。
     * 
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            CheckBox allCheckBox = new CheckBox();
            allCheckBox.selectedProperty().addListener(b -> allCheck(allCheckBox.isSelected()));
            this.checkColumn.setGraphic(allCheckBox);
            this.checkColumn.setCellFactory(param -> new CheckBoxTableCell<>());




            this.dateColumn.setCellValueFactory(f -> f.getValue().dateTimeProperty());
            this.workflowNameColumn.setCellValueFactory(f -> f.getValue().workflowNameProperty());
            this.workNameColumn.setCellValueFactory(f -> f.getValue().workNameProperty());
            this.equipmentNameColumn.setCellValueFactory(f -> f.getValue().equipmentNameProperty());
            this.workerNameColumn.setCellValueFactory(f -> f.getValue().workerNameProperty());
            this.reportListTableView.setEditable(true);

            // 設備選択欄
            this.equipmentSelectPaneController.setLabelText(LocaleUtils.getString("key.Equipment"));
            this.equipmentSelectPaneController.setOnClickButtonListener(f -> this.onEquipmentSelect());

            // 組織選択欄
            this.organizationSelectPaneController.setLabelText(LocaleUtils.getString("key.Organization"));
            this.organizationSelectPaneController.setOnClickButtonListener(f-> this.onOrganizationSelect());
            this.dateCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (Objects.nonNull(newValue)) {
                    fromDatePicker.setDisable(!newValue);
                    toDatePicker.setDisable(!newValue);
                }
            });
            // 開始日には、終了日より後の日は選択できない。
            this.fromDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate date, boolean empty) {
                            super.updateItem(date, empty);
                            if (Objects.isNull(date)
                                    || Objects.isNull(toDatePicker.getValue())
                                    || !date.isAfter(toDatePicker.getValue())) {
                                return;
                            }
                            setDisable(true);
                            setStyle(DISABLE_DATE_STYLE);
                        }
                    };
                }
            });

            // 終了日には、開始日より前の日は選択できない。
            this.toDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate date, boolean empty) {
                            super.updateItem(date, empty);
                            if (Objects.isNull(date)
                                    || Objects.isNull(fromDatePicker.getValue())
                                    || !date.isBefore(fromDatePicker.getValue())) {
                                return;
                            }
                            setDisable(true);
                            setStyle(DISABLE_DATE_STYLE);
                        }
                    };
                }
            });
        } finally {
            blockUI(false);
        }
    }

    /**
     * 設備 選択ボタンイベント
     */
    private void onEquipmentSelect() {
        try {
            if (Objects.isNull(this.equipmentInfoEntities)) {
                this.equipmentInfoEntities = new ArrayList<>();
            }

            SelectDialogEntity<EquipmentInfoEntity> selectDialog = new SelectDialogEntity<>();
            selectDialog.equipments(this.equipmentInfoEntities);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Equipment"), "EquipmentSelectionCompo", selectDialog);
            if (ButtonType.OK.equals(ret)) {
                this.equipmentInfoEntities = selectDialog.getEquipments();
                Map<Long, String> choiceDatas
                        = equipmentInfoEntities
                        .stream()
                        .collect(toMap(EquipmentInfoEntity::getEquipmentId, EquipmentInfoEntity::getEquipmentName));
                equipmentSelectPaneController.setChoiceDatas(choiceDatas);
                equipmentSelectPaneController.setChoiceDatas(choiceDatas);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 組織 選択ボタンイベント
     */
    private void onOrganizationSelect() {
        try {
            if (Objects.isNull(this.organizationInfoEntities)) {
                this.organizationInfoEntities = new ArrayList<>();
            }

            SelectDialogEntity<OrganizationInfoEntity> selectDialog = new SelectDialogEntity<>();
            selectDialog.organizations(this.organizationInfoEntities);

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialog);
            if (ButtonType.OK.equals(ret)) {
                this.organizationInfoEntities = selectDialog.getOrganizations();
                Map<Long, String> choiceDatas
                        = organizationInfoEntities
                        .stream()
                        .collect(toMap(OrganizationInfoEntity::getOrganizationId, OrganizationInfoEntity::getOrganizationName));
                organizationSelectPaneController.setChoiceDatas(choiceDatas);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 外部出力機能の有効/無効状態を更新します。
     *
     * 条件: 日付チェックボックス、設備選択パネル、組織選択パネルのいずれかが選択されている場合は有効にし、
     * 全て未選択の場合は無効に設定します。
     */
    private void updateLedgerOutDisableState() {
        boolean shouldEnable = dateCheckBox.isSelected()
                || equipmentSelectPaneController.isSelected()
                || organizationSelectPaneController.isSelected();
        this.ledgerOut.setDisable(!shouldEnable);
    }


    /**
     * パラメーターを設定する。
     * 
     * @param argument パラメーター
     */
    @Override
    public void setArgument(Object argument) {
        if (!(argument instanceof  LedgerInfoEntity)) {
            return;
        }

        this.ledgerInfoEntity = (LedgerInfoEntity) argument;
        this.ledgerConditionEntity = ledgerInfoEntity.getLedgerCondition();

        LedgerTypeEnum ledgerType = this.ledgerConditionEntity.getLedgerType();
        if (ledgerType == LedgerTypeEnum.INDIVIDUAL) {
            // 個別の場合は実績にチェックした場合のみ実績出力を有効にしない
            this.ledgerOut.setDisable(true);
            InvalidationListener selectListener = (isSelected) -> {
                boolean isNoSelected
                        = reportListTableView
                        .getItems()
                        .stream()
                        .map(InspectionListTableDataEntity::selectedProperty)
                        .noneMatch(BooleanExpression::getValue);
                this.ledgerOut.setDisable(isNoSelected);
            };

            this.checkColumn.setCellValueFactory(f -> {
                f.getValue().selectedProperty().removeListener(selectListener);
                f.getValue().selectedProperty().addListener(selectListener);
                return f.getValue().selectedProperty();
            });
        } else {
            // 対象期間、設備、組織が有効の場合のみ実績出力を有効にする
            dateCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateLedgerOutDisableState());
            equipmentSelectPaneController.setOnCheckListener(isSelected -> updateLedgerOutDisableState());
            organizationSelectPaneController.setOnCheckListener(isSelected -> updateLedgerOutDisableState());

            this.checkColumn.setCellValueFactory(f -> {
                return f.getValue().selectedProperty();
            });

        }

        CashManager cache = CashManager.getInstance();
        CacheUtils.createCacheEquipment(true);
        CacheUtils.createCacheOrganization(true);

        // 設備情報を取得
        this.equipmentInfoEntities
                = this.ledgerConditionEntity
                .getEquipmentIds()
                .stream()
                .map(id -> cache.getItem(EquipmentInfoEntity.class, id))
                .filter(EquipmentInfoEntity.class::isInstance)
                .map(EquipmentInfoEntity.class::cast)
                .collect(toList());

        // 組織情報を取得
        this.organizationInfoEntities
                = this.ledgerConditionEntity
                .getOrganizationIds()
                .stream()
                .map(id -> cache.getItem(OrganizationInfoEntity.class, id))
                .filter(OrganizationInfoEntity.class::isInstance)
                .map(OrganizationInfoEntity.class::cast)
                .collect(toList());

        // 完了日を設定
        List<Date> schedule
                = this.ledgerConditionEntity
                .getScheduleConditionInfoEntity()
                .stream()
                .map(l -> l.getPrevSchedule(2))
                .flatMap(Collection::stream)
                .sorted(Comparator.reverseOrder())
                .collect(toList());

        if (schedule.size() >= 2) {
            dateCheckBox.setSelected(true);
            fromDatePicker.setValue(DateUtils.toLocalDate(schedule.get(1)));
            toDatePicker.setValue(DateUtils.toLocalDate(schedule.get(0)));
        }

        // 表示を更新
        refreshLedgerFileList();
        Platform.runLater(() -> {
            // 設備検索条件を更新
            if (!this.equipmentInfoEntities.isEmpty()) {
                Map<Long, String> choiceDatas
                        = this.equipmentInfoEntities
                        .stream()
                        .collect(toMap(EquipmentInfoEntity::getEquipmentId, EquipmentInfoEntity::getEquipmentName));
                equipmentSelectPaneController.setChoiceDatas(choiceDatas);
                equipmentSelectPaneController.setSelected(true);
            }

            // 組織検査条件を更新
            if (!this.organizationInfoEntities.isEmpty()) {
                Map<Long, String> choiceDatas
                        = this.organizationInfoEntities
                        .stream()
                        .collect(toMap(OrganizationInfoEntity::getOrganizationId, OrganizationInfoEntity::getOrganizationName));
                organizationSelectPaneController.setChoiceDatas(choiceDatas);
                organizationSelectPaneController.setSelected(true);
            }

            if (schedule.size() >= 2) {
                ((CheckBox) this.checkColumn.getGraphic()).setSelected(true);
            }
        });
    }

    /**
     * ダイアログを設定する。
     * 
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancelDialog();
        });
    }

    /**
     * 変更ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onOutLedger() {
        try {
            this.blockUI(true);

            List<Long> actualResultEntities
                    = this.reportListTableView
                    .getItems()
                    .stream()
                    .filter(item -> item.selectedProperty().get())
                    .map(InspectionListTableDataEntity::getActualResultEntity)
                    .flatMap(Collection::stream)
                    .map(ActualResultEntity::getActualId)
                    .collect(toList());

//            if (actualResultEntities.isEmpty()) {
//                return;
//            }

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.OutLedgerTitle"), LocaleUtils.getString("key.OutputReportFile") + System.lineSeparator() + LocaleUtils.getString("key.ConformMessage"), "");
            if (!ret.equals(ButtonType.OK)) {
                return;
            }

            ResponseEntity res = ledgerInfoFacade.reportOut(
                    ledgerInfoEntity.getLedgerId(),
                    this.equipmentSelectPaneController.isSelected() ? this.equipmentInfoEntities.stream().map(EquipmentInfoEntity::getEquipmentId).collect(toList()) : null,
                    this.organizationSelectPaneController.isSelected() ? this.organizationInfoEntities.stream().map(OrganizationInfoEntity::getOrganizationId).collect(toList()) : null,
                    dateCheckBox.isSelected() ? DateUtils.getBeginningOfDate(fromDatePicker.getValue()) : null,
                    dateCheckBox.isSelected() ? DateUtils.getEndOfDate(toDatePicker.getValue()) : null,
                    actualResultEntities
            );
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {

            } else {
                // アプリケーションに問題が発生したため帳票出力を中断します。
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.OutLedgerTitle"), LocaleUtils.getString("key.KanbanOutLedgerApplicationErr"));
            }

            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.blockUI(false);
        }
    }

    /**
     * キャンセルボタンのアクション
     * 
     * @param event アクションイベント
     */
    @FXML
    private void onCancel(ActionEvent event) {
        this.cancelDialog();
    }

    /**
     * キャンセル処理
     */
    private void cancelDialog() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    public void onSearchAction(ActionEvent actionEvent) {
        // 選択を解除
        ((CheckBox) this.checkColumn.getGraphic()).selectedProperty().set(false);
//        this.ledgerOut.setDisable(true);

        // 実績を取得
        List<ActualResultEntity> actualResultEntities = ledgerInfoFacade.getHistory(
                this.ledgerInfoEntity.getLedgerId(),
                this.equipmentSelectPaneController.isSelected() ? this.equipmentInfoEntities.stream().map(EquipmentInfoEntity::getEquipmentId).collect(toList()) : null,
                this.organizationSelectPaneController.isSelected() ? this.organizationInfoEntities.stream().map(OrganizationInfoEntity::getOrganizationId).collect(toList()) : null,
                dateCheckBox.isSelected() ? DateUtils.getBeginningOfDate(fromDatePicker.getValue()) : null,
                dateCheckBox.isSelected() ? DateUtils.getEndOfDate(toDatePicker.getValue()) : null
        );

        // 表を更新
        List<InspectionListTableDataEntity> inspectionListTableDataEntities
                = actualResultEntities
                .stream()
                .collect(groupingBy(this::createGroupKey, toList()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .map(list -> new InspectionListTableDataEntity(list, equipmentMap))
                .collect(toList());

        this.reportListTableView.getItems().clear();
        this.reportListTableView.setItems(FXCollections.observableArrayList(inspectionListTableDataEntities));
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        Progress.setVisible(flg);
    }
}
