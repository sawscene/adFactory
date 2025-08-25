/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.admanagerapp.workfloweditplugin.common.Constants;
import adtekfuji.admanagerapp.workfloweditplugin.common.KanbanPropertyTempRecordFactory;
import adtekfuji.admanagerapp.workfloweditplugin.common.PropertyTemplateLoader;
import adtekfuji.admanagerapp.workfloweditplugin.common.SelectedWorkflowAndHierarchy;
import adtekfuji.admanagerapp.workfloweditplugin.common.SeparateworkRecordFactory;
import adtekfuji.admanagerapp.workfloweditplugin.common.UriConvertUtils;
import adtekfuji.admanagerapp.workfloweditplugin.common.WorkflowEditConfig;
import adtekfuji.admanagerapp.workfloweditplugin.entity.ApprovalDialogEntity;
import adtekfuji.admanagerapp.workfloweditplugin.entity.WorkSettingDialogEntity;
import adtekfuji.admanagerapp.workfloweditplugin.model.WorkflowEditModel;
import adtekfuji.admanagerapp.workfloweditplugin.property.FilePathRecordFactory;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.WorkInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.approval.ApprovalInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.response.ResponseWorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowSeparateworkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.KanbanPropertyTemplateInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalDataTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.DetailRecordFactory;
import jp.adtekfuji.javafxcommon.PropertyBindEntity;
import jp.adtekfuji.javafxcommon.event.ActionEventListener;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.workflowmodel.CellBase;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkCell;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowModel;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程順編集画面
 *
 * @author ta.ito
 */
@FxComponent(id = "WorkflowDetailCompo", fxmlPath = "/fxml/compo/workflow_detail_compo.fxml")
public class WorkflowDetailCompoFxController implements Initializable, ArgumentDelivery, ComponentHandler {

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final WorkInfoFacade workInfoFacade = new WorkInfoFacade();
    private final WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
    private final Map<String, PropertyBindEntity> detailProperties = new LinkedHashMap<>();
    private final LinkedList<KanbanPropertyTemplateInfoEntity> customProperties = new LinkedList<>();
    private final LinkedList<ConWorkflowSeparateworkInfoEntity> separateWorks = new LinkedList<>();
    private final Map<String, String> templateRegexTexts = new HashMap<>();
    private final WorkflowModel model = new WorkflowModel();

    private final LinkedList<SimpleStringProperty> ledgerPaths = new LinkedList<>();// 帳票テンプレート

    // 詳細表示の設定名
    private static final String DETAIL_WORKFLOW_NAME = "workflowName";
    private static final String DETAIL_MODEL_NAME = "modelName";
    private static final String DETAIL_WORK_NUMBER = "workNumber";
    private static final String DETAIL_TIME_PERIODS = "timePeriods";
    private static final String DETAIL_SCHEDULE_POLICY = "schedulePolicy";

    //帳票テンプレートを開くときのデフォルトパス
    private final File defaultDir = new File(System.getProperty("user.home"), "Desktop");

    private SelectedWorkflowAndHierarchy selected = null;
    private Table separateWorkTable;

    //開いたときの最初に記述されていた情報
    private WorkflowInfoEntity cloneWorkflow;

    public static final int WORKFLOW_REV_LIMIT = 999;// 工程順の版数の最大値
    private final long range = 100;

    /**
     * 承認機能オプションが有効か
     */
    private final boolean isLicensedApproval = ClientServiceProperty.isLicensed(LicenseOptionType.ApprovalOption.getName());

    /**
     * 改訂ボタンの有効/無効状態
     */
    private boolean disableReviseButton = true;

    /**
     * 工程順名フィールドの有効/無効状態
     */
    private boolean disableWorkflowNameField = false;

    /**
     * 各種入力項目の有効/無効状態
     */
    private boolean disableOtherInputItems = true;
    /**
     * 各種入力項目のボタン有効/無効状態
     */
    private boolean disableOtherButtonItems = true;

    /**
     * ワークフロー図の拡大率
     */
    private double workflowScale = 1.0;

    private TextField revisionField;
    private Button reviseButton;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button cancelButton;
    @FXML
    private Button registButton;
    @FXML
    private Button applyButton;
    @FXML
    private Label parentNameLabel;
    @FXML
    private VBox defaultFieldPane;
    @FXML
    private VBox workflowViewPane;
    @FXML
    private VBox separateworkFieldPane;
    @FXML
    private VBox propertyFieldPane;
    @FXML
    private VBox ledgerPathFieldPane;
    @FXML
    private Slider zoomSlider;
    @FXML
    private ToggleButton visibleOrganizationToggleButton;
    @FXML
    private ToggleButton visibleEquipmentToggleButton;
    @FXML
    private ToggleButton visibleScaleToggleButton;
    @FXML
    private GridPane zoomPane;

    /**
     * 工程順の編集ボタン
     */
    @FXML
    private Button editOrderProcessesButton;

    /**
     * 申請中ラベル
     */
    @FXML
    private Label requestingLabel;

    /**
     * 申請者名ラベル
     */
    @FXML
    private Label requestorNameLabel;

    /**
     * 申請ボタン
     */
    @FXML
    private Button requestButton;

    /**
     * 申請取消ボタン
     */
    @FXML
    private Button requestCancelButton;

    /**
     * 作業順序セル
     */
    private class SchedulePolicyListCell extends ListCell<SchedulePolicyEnum> {

        @Override
        protected void updateItem(SchedulePolicyEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || Objects.isNull(item)) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
            }
        }
    }

    private final Callback<ListView<SchedulePolicyEnum>, ListCell<SchedulePolicyEnum>> schedulePolicyCallback = (ListView<SchedulePolicyEnum> param) -> new SchedulePolicyListCell();

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //役割の権限によるボタン無効化.
        if (!this.loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
            this.registButton.setDisable(true);
            this.applyButton.setDisable(true);
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onCancel(ActionEvent event) {
        logger.info("onCancel:Start");
        if (destoryComponent()) {
            this.cloneWorkflow = createRegistWorkflow().clone();//変更が検出されないようコピー
            
            //WorkflowEditModel workflowEditModel = WorkflowEditModel.getInstance();
            //if (workflowEditModel.isInnerMode()) {
            //    workflowEditModel.raiseEvent(ActionEventListener.SceneEvent.Close, null);
            //    return;
            //}

            this.sc.setComponent("ContentNaviPane", "WorkflowEditCompo");
        }
    }

    /**
     * 登録ボタン
     * 
     * @param event
     */
    @FXML
    private void onRegist(ActionEvent event) {
        try {
            logger.info("onRegist:Start");
            if (this.registWorkflow()) {

                WorkflowEditModel workflowEditModel = WorkflowEditModel.getInstance();
                if (workflowEditModel.isInnerMode()) {
                    workflowEditModel.raiseEvent(ActionEventListener.SceneEvent.Close, this.cloneWorkflow);
                    return;
                }

                sc.setComponent("ContentNaviPane", "WorkflowEditCompo");
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 作業工程の変更を適用する
     *
     * @param event
     */
    @FXML
    private void onApply(ActionEvent event) {
        try {
            logger.info("onApply:Start");
            if (registWorkflow()) {
                //適用では元の画面に戻らない

                // 追加工程にリビジョン追加
                addRevToSeparateWorkName();

                // 画面を再描画
                createDetailView(selected);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 申請ボタン押下時のアクション
     *
     * @param event イベント
     */
    @FXML
    private void onRequest(ActionEvent event) {
        try {
            logger.info("onRequest:Start");

            if (!registWorkflow()) {
                return;
            }

            // 追加工程にリビジョン追加
            addRevToSeparateWorkName();
            // 画面を再描画
            createDetailView(selected);

            // ワークフロー図の拡大率を復元
            model.setScale(workflowScale);

            // 引数を設定
            WorkflowInfoEntity workflow = selected.getWorkflowInfo();
            ApprovalDialogEntity argument = new ApprovalDialogEntity();
            argument.setApprovalDataType(ApprovalDataTypeEnum.WORKFLOW);
            argument.setIsRequestTypeApproval(true);
            argument.setWorkflow(workflow);

            // 申請ダイアログを表示
            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.Approval"), "ApprovalDialog", argument, sc.getStage(), true);
            if (ret.equals(ButtonType.OK)) {
                // データを再取得
                WorkflowInfoEntity newWorkflow = workflowInfoFacade.find(workflow.getWorkflowId(), true);
                if (Objects.nonNull(newWorkflow)) {
                    selected.setWorkflow(newWorkflow);
                    cloneWorkflow = selected.getWorkflowInfo().clone();
                }

                // 工程順マスタのキャッシュを削除する。
                CacheUtils.removeCacheData(WorkflowInfoEntity.class);

                // 追加工程にリビジョン追加
                addRevToSeparateWorkName();
                // 画面を再描画
                createDetailView(selected);

                // ワークフロー図の拡大率を復元
                model.setScale(workflowScale);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onRequest:end.");
        }
    }

    /**
     * 申請取消ボタン押下時のアクション
     *
     * @param event イベント
     */
    @FXML
    private void onRequestCancel(ActionEvent event) {
        try {
            logger.info("onRequestCancel:Start");

            String message = LocaleUtils.getString("key.confirm.approvalRequestCancel");
            ButtonType confirmRet = sc.showOkCanselDialog(AlertType.CONFIRMATION, LocaleUtils.getString("key.confirm"), message);
            if (!confirmRet.equals(ButtonType.OK)) {
                return;
            }

            // 引数を設定
            WorkflowInfoEntity workflow = selected.getWorkflowInfo();
            ApprovalDialogEntity argument = new ApprovalDialogEntity();
            argument.setApprovalDataType(ApprovalDataTypeEnum.WORKFLOW);
            argument.setIsRequestTypeApproval(false);
            argument.setWorkflow(workflow);

            // 申請取消ダイアログを表示
            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.ApprovalCancel"), "ApprovalDialog", argument, sc.getStage(), true);
            if (ret.equals(ButtonType.OK)) {
                // データを再取得
                WorkflowInfoEntity newWorkflow = workflowInfoFacade.find(workflow.getWorkflowId(), true);
                if (Objects.nonNull(newWorkflow)) {
                    selected.setWorkflow(newWorkflow);
                    cloneWorkflow = selected.getWorkflowInfo().clone();
                }

                // 工程順マスタのキャッシュを削除する。
                CacheUtils.removeCacheData(WorkflowInfoEntity.class);

                // 追加工程にリビジョン追加
                addRevToSeparateWorkName();
                // 画面を再描画
                createDetailView(selected);

                // ワークフロー図の拡大率を復元
                model.setScale(workflowScale);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onWorkflowEdit(ActionEvent event) {
        try {
            //役割によって編集不可の場合は、保存せずに詳細画面へ.
            if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
                WorkflowInfoEntity workflow = workflowInfoFacade.find(selected.getWorkflowInfo().getWorkflowId());
                selected.setWorkflow(workflow);
                sc.setComponent("ContentNaviPane", "WorkflowAssemblyCompo", selected);
            } else {
                //ワークフロー編集画面に移動する前に一度保存する
                String message = String.format(LocaleUtils.getString("key.MoveFromRegistMessage"), LocaleUtils.getString("key.RegistOrderProcesses"));
                ButtonType ret = sc.showOkCanselDialog(AlertType.CONFIRMATION, LocaleUtils.getString("key.Regist"), message);
                if (ret.equals(ButtonType.OK) && registWorkflow()) {
                    WorkflowInfoEntity workflow = workflowInfoFacade.find(selected.getWorkflowInfo().getWorkflowId());
                    selected.setWorkflow(workflow);
                    addRevToSeparateWorkName();
                    sc.setComponent("ContentNaviPane", "WorkflowAssemblyCompo", selected);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 帳票テンプレートパスをファイルダイアログを用いて取得する
     *
     * @param event
     */
    private void onChoice(ActionEvent event) {
        try {
            logger.info("onChoice:Start");

            Button cellButton = (Button) event.getSource();
            SimpleStringProperty prop = (SimpleStringProperty) cellButton.getUserData();

            File dir = defaultDir;
            if (Objects.nonNull(prop.getValue())) {
                File file = new File(prop.getValue());
                if (file.exists()) {
                    if (file.isFile()) {
                        dir = file.getParentFile();
                    } else {
                        dir = file;
                    }
                }
            }

            FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter(LocaleUtils.getString("key.LedgerSheet"), "*.xlsx", "*.xlsm");

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(dir);
            fileChooser.setTitle(LocaleUtils.getString("key.FileChoice"));
            fileChooser.getExtensionFilters().addAll(filter1);

            File file = fileChooser.showOpenDialog(sc.getWindow());
            if (Objects.nonNull(file)) {
                prop.setValue(file.getPath());
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectedWorkflowAndHierarchy) {
            selected = (SelectedWorkflowAndHierarchy) argument;
            this.templateRegexTexts.clear();
            this.templateRegexTexts.putAll(PropertyTemplateLoader.getWorkflowTemplateRegexText());
            // 工程の新規作成時、工程プロパティのテンプレートデータを追加する
            if (!Objects.nonNull(selected.getWorkflowInfo().getWorkflowId())) {
                selected.getWorkflowInfo().setKanbanPropertyTemplateInfoCollection(PropertyTemplateLoader.getWorkflowProperties());
            }
            Platform.runLater(() -> {
                createDetailView(selected);
                loadProperties();
                //最初に表示された情報をコピー
                WorkflowInfoEntity tmp = createRegistWorkflow();
                cloneWorkflow = createRegistWorkflow().clone();
            });
        }
    }

    /**
     * 詳細表示処理
     *
     * @param selectedInfo
     */
    private void createDetailView(SelectedWorkflowAndHierarchy selectedInfo) {
        // 工程順階層名
        this.parentNameLabel.setText(selectedInfo.getHierarchyName());

        // 申請者中ラベル
        this.requestingLabel.setTextFill(Color.RED);

        // 申請者名ラベル
        ApprovalInfoEntity approval = selectedInfo.getWorkflowInfo().getApproval();
        if (Objects.nonNull(approval)) {
            OrganizationInfoEntity organization = CacheUtils.getCacheOrganization(approval.getRequestorId());
            if (Objects.nonNull(organization)) {
                this.requestorNameLabel.setText(organization.getOrganizationName());
            }
        }

        this.defaultFieldPane.getChildren().clear();
        this.detailProperties.clear();

        // 工程順名
        this.detailProperties.put(DETAIL_WORKFLOW_NAME, (PropertyBindEntity) PropertyBindEntity.createString(LocaleUtils.getString("key.OrderProcessesName") + LocaleUtils.getString("key.RequiredMark"), selectedInfo.getWorkflowInfo().getWorkflowName()).setPrefWidth(480.0).setMaxLength(256));

        // モデル名
        this.detailProperties.put(DETAIL_MODEL_NAME, (PropertyBindEntity) PropertyBindEntity.createString(LocaleUtils.getString("key.ModelName"), selectedInfo.getWorkflowInfo().getModelName()).setPrefWidth(480.0).setMaxLength(256));

        // 作業番号 (enableDailyReport=true の場合のみ表示)
        if (WorkflowEditConfig.getEnableDailyReport()) {
            this.detailProperties.put(DETAIL_WORK_NUMBER, (PropertyBindEntity) PropertyBindEntity.createString(LocaleUtils.getString("key.IndirectWorkNumber"), selectedInfo.getWorkflowInfo().getWorkflowNumber()).setPrefWidth(220.0));
        }

        String openTime = "00:00";
        String closeTime = "00:00";
        try {
            SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
            openTime = sd.format(selectedInfo.getWorkflowInfo().getOpenTime());
            closeTime = sd.format(selectedInfo.getWorkflowInfo().getCloseTime());
        } catch (Exception ex) {
        }

        // 作業時間枠
        this.detailProperties.put(DETAIL_TIME_PERIODS, (PropertyBindEntity) PropertyBindEntity.createTimePeriods(LocaleUtils.getString("key.WorkingTime"), openTime, closeTime).setPrefWidth(220.0));

        // 作業順序
        PropertyBindEntity schedulePolicy = PropertyBindEntity.createCombo(LocaleUtils.getString("key.SchedulePolicy"), Arrays.asList(SchedulePolicyEnum.values()), new SchedulePolicyListCell(), schedulePolicyCallback, selectedInfo.getWorkflowInfo().getSchedulePolicy());
        schedulePolicy.setPrefWidth(220.0);
        ((SimpleObjectProperty) schedulePolicy.getProperty()).addListener((observable, oldValue, newValue) -> {
            this.model.getWorkflowPane().getWorkflowEntity().setSchedulePolicy((SchedulePolicyEnum) newValue);

            // ワークフローを再構築
            List<CellBase> cells = this.model.getWorkflowPane().getCellList();
            Optional<CellBase> optional = cells.stream().filter(p -> p instanceof WorkCell).findFirst();
            if (optional.isPresent()) {
                WorkCell workCell = (WorkCell) optional.get();
                this.model.updateTimetable(workCell, workCell.getWorkflowWork().getTaktTime(), true);
                WorkflowPane pane = this.craeteWorkflowPane(this.selected.getWorkflowInfo(), false);
                this.scrollPane.setContent(pane);
            }
        });
        this.detailProperties.put(DETAIL_SCHEDULE_POLICY, schedulePolicy);

        Table detailTable = new Table(defaultFieldPane.getChildren()).isAddRecord(false);
        detailTable.setAbstractRecordFactory(new DetailRecordFactory(detailTable, new LinkedList(detailProperties.values())));

        // 帳票テンプレートパス
        this.ledgerPathFieldPane.getChildren().clear();

        this.ledgerPaths.clear();
        this.ledgerPaths.addAll(selectedInfo.getWorkflowInfo().getLedgerPathPropertyCollection());

        Table ledgerPathTable = new Table(this.ledgerPathFieldPane.getChildren())
                .title(LocaleUtils.getString("key.LedgerSheetPath"))
                .styleClass("ContentTitleLabel")
                .isAddRecord(true)
                .maxRecord(99);

        FilePathRecordFactory pathRecordFactory = new FilePathRecordFactory(ledgerPathTable, this.ledgerPaths);
        pathRecordFactory.setOnActionEventListener(event -> {
            onChoice(event);
        });

        ledgerPathTable.setAbstractRecordFactory(pathRecordFactory);

        // テーブルに版数のコントロールを追加
        createAdditionalColumn(detailTable, selectedInfo);

        createWorkflowPane();

        // 追加工程
        if (Objects.isNull(selectedInfo.getWorkflowInfo().getConWorkflowSeparateworkInfoCollection())) {
            selectedInfo.getWorkflowInfo().setConWorkflowSeparateworkInfoCollection(new ArrayList<>());
        }
        this.createSeparateWrokTable(selectedInfo.getWorkflowInfo().getConWorkflowSeparateworkInfoCollection());

        // カスタムプロパティ
        propertyFieldPane.getChildren().clear();
        if (Objects.isNull(selectedInfo.getWorkflowInfo().getKanbanPropertyTemplateInfoCollection())) {
            selectedInfo.getWorkflowInfo().setKanbanPropertyTemplateInfoCollection(new ArrayList<>());
        }

        customProperties.clear();
        customProperties.addAll(selectedInfo.getWorkflowInfo().getKanbanPropertyTemplateInfoCollection());
        customProperties.sort(Comparator.comparing(property -> property.getKanbanPropOrder()));

        Table customTable = new Table(propertyFieldPane.getChildren()).isAddRecord(true).isColumnTitleRecord(true).title(LocaleUtils.getString("key.CustomField")).styleClass("ContentTitleLabel");
        customTable.setAbstractRecordFactory(new KanbanPropertyTempRecordFactory(customTable, customProperties));

        // 各種入力項目の表示状態設定
        setInputItemViewState(detailTable);
    }

    /**
     * 工程順フロー表示ペインの構築
     *
     */
    private void createWorkflowPane() {
        // ワークフロー図の表示
        WorkflowPane flowPane = this.craeteWorkflowPane(selected.getWorkflowInfo(), false);
        this.scrollPane.setContent(flowPane);
        this.scrollPane.setPannable(true);
        this.scrollPane.setFocusTraversable(false);

        zoomPane.visibleProperty().bind(visibleScaleToggleButton.selectedProperty());

        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            model.setScale(newValue.doubleValue());
            workflowScale = newValue.doubleValue();
        });

        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                double value = zoomSlider.getValue();
                double unit = zoomSlider.getMajorTickUnit();
                zoomSlider.setValue(event.getDeltaY() > 0 ? value + unit : value - unit);
                event.consume();
            }
        });

        visibleEquipmentToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            model.setVisibleEquipment(newValue);
        });

        visibleOrganizationToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            model.setVisibleOrganization(newValue);
        });
    }

    /**
     * 版数フィールドと改訂ボタンを追加する
     *
     */
    private void createAdditionalColumn(Table detailTable, SelectedWorkflowAndHierarchy selectedInfo) {

        //「版数」フィールドの作成
        Integer rev = selectedInfo.getWorkflowInfo().getWorkflowRev();
        this.revisionField = Objects.isNull(this.revisionField) ? new TextField() : this.revisionField;
        this.revisionField.setText(Objects.nonNull(rev) ? rev.toString() : "1");
        this.revisionField.getStyleClass().add("ContentTextBox");
        this.revisionField.setPrefWidth(60);
        this.revisionField.setDisable(true);

        //「改訂」ボタンの作成
        this.reviseButton = Objects.isNull(this.reviseButton)
                ? new Button(LocaleUtils.getString("key.revise")) : this.reviseButton;
        this.reviseButton.getStyleClass().add("ContentTextBox");
        this.reviseButton.setOnAction(this::onRevise);

        // runLater内で追加しないと表示しないため注意
        Platform.runLater(() -> {
            detailTable.findLabelRow(LocaleUtils.getString("key.OrderProcessesName") + LocaleUtils.getString("key.RequiredMark")).ifPresent(index -> {
                detailTable.addNodeToBody(revisionField, 2, (int) index);
                detailTable.addNodeToBody(reviseButton, 3, (int) index);
            });
        });
    }

    /**
     * 各種入力項目の表示状態を設定する。
     *
     * @param detailTable 詳細テーブル
     */
    private void setInputItemViewState(Table detailTable) {
        // 申請ボタン、申請取消ボタン、申請中ラベル、申請者名ラベルの初期値は非表示
        boolean visibleRequestButton = false;
        boolean visibleRequestCancelButton = false;
        boolean visibleRequestingLabel = false;
        boolean visibleRequesorNameLabel = false;

        // 申請ボタン、申請取消ボタン、登録ボタン、適用ボタン、改訂ボタン、版数フィールド、その他入力項目の初期値は無効
        // 工程順名フィールドの初期値は有効
        boolean disableRequestButton = true;
        boolean disableRequestCancelButton = true;
        boolean disableRegistButton = true;
        boolean disableApplyButton = true;
        this.disableReviseButton = true;
        this.disableOtherInputItems = true;
        this.disableOtherButtonItems = true;
        this.disableWorkflowNameField = false;

        WorkflowInfoEntity workflow = selected.getWorkflowInfo();
        Integer latestRev = workflow.getLatestRev();

        // 各種入力項目の表示状態(表示/非表示、有効/無効)を判定
        if (!isLicensedApproval) {
            // 承認機能オプションが無効
            if (this.loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
                // 工程・工程順編集権限あり
                disableRegistButton = false;
                disableApplyButton = false;
                this.disableOtherInputItems = false;
                this.disableOtherButtonItems = false;

                if (Objects.nonNull(workflow.getWorkflowId()) && workflow.getWorkflowRev().equals(latestRev)) {
                    // 選択した工程順が最新版数
                    this.disableReviseButton = false;
                }
                if (Objects.nonNull(latestRev)) {
                    if (latestRev > 1) {
                        this.disableWorkflowNameField = true;
                    }
                }
            } else {
                // 工程・工程順編集権限なし
                this.disableOtherInputItems = false;
            }
        } else {
            // 承認機能オプションが有効
            visibleRequestButton = true;
            visibleRequestCancelButton = true;

            if (!this.loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
                // 工程・工程順編集権限なし
                this.disableOtherInputItems = false;
                if (this.selected.getWorkflowInfo().getWorkflowRev() > 1) {
                    this.disableWorkflowNameField = true;
                }
                if (Objects.nonNull(workflow.getApprovalId())) {
                    // 申請情報あり
                    if (ApprovalStatusEnum.APPLY.equals(workflow.getApprovalState())) {
                        // 申請中
                        visibleRequestingLabel = true;
                        visibleRequesorNameLabel = true;
                    }
                }
            } else {
                // リソース編集権限あり
                if (Objects.nonNull(workflow.getApprovalState())) {
                    // 編集モード
                    switch (workflow.getApprovalState()) {
                        case UNAPPROVED:
                            disableRequestButton = false;
                            disableRegistButton = false;
                            disableApplyButton = false;
                            this.disableOtherInputItems = false;
                            this.disableOtherButtonItems = false;
                            if (this.selected.getWorkflowInfo().getWorkflowRev() > 1) {
                                this.disableWorkflowNameField = true;
                            }
                            break;
                        case APPLY:
                            visibleRequestingLabel = true;
                            visibleRequesorNameLabel = true;
                            if (this.loginUser.getId().equals(workflow.getApproval().getRequestorId())) {
                                disableRequestCancelButton = false;
                            }
                            this.disableWorkflowNameField = true;
                            break;
                        case CANCEL_APPLY:
                            disableRequestButton = false;
                            disableRegistButton = false;
                            disableApplyButton = false;
                            this.disableOtherInputItems = false;
                            this.disableOtherButtonItems = false;
                            if (this.selected.getWorkflowInfo().getWorkflowRev() > 1) {
                                this.disableWorkflowNameField = true;
                            }
                            break;
                        case REJECT:
                            disableRequestButton = false;
                            disableRegistButton = false;
                            disableApplyButton = false;
                            this.disableOtherInputItems = false;
                            this.disableOtherButtonItems = false;
                            if (this.selected.getWorkflowInfo().getWorkflowRev() > 1) {
                                this.disableWorkflowNameField = true;
                            }
                            break;
                        case FINAL_APPROVE:
                            if (workflow.getWorkflowRev().equals(latestRev)) {
                                // 選択した工程順が最新版数
                                this.disableReviseButton = false;
                            }
                            this.disableWorkflowNameField = true;
                            break;
                        default:
                            break;
                    }
                } else {
                    // 新規作成モード
                    disableRequestButton = false;
                    disableRegistButton = false;
                    disableApplyButton = false;
                    this.disableOtherInputItems = false;
                    this.disableOtherButtonItems = false;
                }
            }
        }

        // 申請ボタン、申請取消ボタン、申請中ラベル、申請者名ラベルの表示/非表示を切り替える
        requestButton.setVisible(visibleRequestButton);
        requestCancelButton.setVisible(visibleRequestCancelButton);
        requestingLabel.setVisible(visibleRequestingLabel);
        requestorNameLabel.setVisible(visibleRequesorNameLabel);

        // 申請ボタン、申請取消ボタン、登録ボタン、適用ボタンの有効/無効を切り替える
        requestButton.setDisable(disableRequestButton);
        requestCancelButton.setDisable(disableRequestCancelButton);
        registButton.setDisable(disableRegistButton);
        applyButton.setDisable(disableApplyButton);
        cancelButton.setDisable(false);

        // 上記以外の入力項目の有効/無効を切り替える
        Platform.runLater(() -> {
            // 工程順名フィールド
            detailTable.findLabelRow(LocaleUtils.getString("key.OrderProcessesName") + LocaleUtils.getString("key.RequiredMark")).ifPresent(index -> {
                // 工程順名フィールド
                TextField textField = (TextField) detailTable.getNodeFromBody((int) index, 1).get();
                textField.setDisable(this.disableWorkflowNameField);

                // 改訂ボタン
                Button button = (Button) detailTable.getNodeFromBody((int) index, 3).get();
                button.setDisable(this.disableReviseButton);
            });
            // モデル名フィールド
            detailTable.findLabelRow(LocaleUtils.getString("key.ModelName")).ifPresent(index -> {
                TextField textField = (TextField) detailTable.getNodeFromBody((int) index, 1).get();
                textField.setDisable(this.disableOtherInputItems);
            });
            // 作業番号フィールド
            detailTable.findLabelRow(LocaleUtils.getString("key.IndirectWorkNumber")).ifPresent(index -> {
                TextField textField = (TextField) detailTable.getNodeFromBody((int) index, 1).get();
                textField.setDisable(this.disableOtherInputItems);
            });
            // 作業時間枠
            detailTable.findLabelRow(LocaleUtils.getString("key.WorkingTime")).ifPresent(index -> {
                HBox hBox = (HBox) detailTable.getNodeFromBody((int) index, 1).get();

                ObservableList<Node> nodes = hBox.getChildren();
                TextField startTextField = (TextField) nodes.get(0);
                startTextField.setDisable(this.disableOtherInputItems);

                TextField endTextFieldField = (TextField) nodes.get(2);
                endTextFieldField.setDisable(this.disableOtherInputItems);
            });
            // 作業順序コンボボックス
            detailTable.findLabelRow(LocaleUtils.getString("key.SchedulePolicy")).ifPresent(index -> {
                ComboBox comboBox = (ComboBox) detailTable.getNodeFromBody((int) index, 1).get();
                comboBox.setDisable(this.disableOtherButtonItems);
            });
        });

        // 帳票テンプレートパスVBOX、工程順の編集ボタン、追加工程VBOX、追加情報VBOX
        ledgerPathFieldPane.setDisable(this.disableOtherInputItems);
        editOrderProcessesButton.setDisable(this.disableOtherButtonItems);
        separateworkFieldPane.setDisable(this.disableOtherInputItems);
        propertyFieldPane.setDisable(this.disableOtherInputItems);
    }

    /**
     *
     * @param event
     */
    private void onRevise(ActionEvent event) {
        try {
            logger.info("onRevise:Start");

            if (!destoryComponent()) {
                return;
            }

            WorkflowInfoEntity workflow = selected.getWorkflowInfo();

            String message = String.format(LocaleUtils.getString("key.revise.inquiryMessage"), LocaleUtils.getString("key.workflow"), workflow.getLatestRev());
            ButtonType ret = sc.showOkCanselDialog(AlertType.CONFIRMATION, LocaleUtils.getString("key.workflow"), message);
            if (ret.equals(ButtonType.OK)) {
                ResponseWorkflowInfoEntity response = workflowInfoFacade.revise(workflow.getWorkflowId());
                if (response.isSuccess()) {
                    workflow = response.getValue();

                    // 最新の工程順を再取得
                    WorkflowInfoEntity newWorkflow = workflowInfoFacade.find(workflow.getWorkflowId(), true);
                    if (Objects.nonNull(newWorkflow)) {
                        this.selected.setWorkflow(newWorkflow);
                        this.cloneWorkflow = workflow.clone();
                    }

                    // 追加工程にリビジョン追加
                    addRevToSeparateWorkName();
                    // 画面を再描画
                    createDetailView(selected);
                } else {
                    ResponseAnalyzer.getAnalyzeResult(response);
                }
            }
        } finally {
            logger.info("onRevise:end");
        }
    }

    /**
     * ワークフロー図の生成
     *
     * @param editable
     * @return
     */
    private WorkflowPane craeteWorkflowPane(WorkflowInfoEntity workflow, boolean editable) {
        WorkflowPane flow = null;
        try {
            logger.info("craeteWorkFlowView start.");

            flow = model.getWorkflowPane();

            if (Objects.isNull(workflow.getConWorkflowWorkInfoCollection())) {
                workflow.setConWorkflowWorkInfoCollection(new ArrayList<>());
            }

            flow.setWorkflowEntity(workflow);
            model.createWorkflow(flow, editable);

            List<CellBase> cells = model.getWorkflowPane().getCellList();
            List<WorkInfoEntity> works = new ArrayList();

            if (Objects.nonNull(workflow.getWorkflowId())) {
                // 登録済みの工程順の場合 工程を取得
                long count = workInfoFacade.getWorkCountByWorkflow(workflow.getWorkflowId());
                for (long from = 0; from < count; from += this.range) {
                    works.addAll(workInfoFacade.getWorkRangeByWorkflow(workflow.getWorkflowId(), from, from + this.range - 1));
                }

                // 工程名を反映
                cells.stream().filter(p -> p instanceof WorkCell).forEach((entity) -> {
                    WorkCell workCell = (WorkCell) entity;
                    Long workId = workCell.getWorkflowWork().getFkWorkId();
                    Optional<WorkInfoEntity> opt = works.stream().filter(o -> o.getWorkId().equals(workId)).findFirst();
                    if (opt.isPresent()) {
                        String workName = opt.get().getWorkName() + " : " + opt.get().getWorkRev();
                        workCell.getWorkflowWork().setWorkName(workName);
                        workCell.setWorkNameLabelText(workName);
                    }

                    // 設備名を反映
                    StringBuilder equipmentNames = new StringBuilder();
                    workCell.getWorkflowWork().getEquipmentCollection().forEach((id) -> {
                        EquipmentInfoEntity equipment = CacheUtils.getCacheEquipment(id);
                        if (Objects.nonNull(equipment)) {
                            equipmentNames.append(equipment.getEquipmentName());
                            equipmentNames.append(' ');
                        }
                    });
                    workCell.setEquipmentLabelText(equipmentNames.toString());

                    // 組織名を反映
                    StringBuilder organizationNames = new StringBuilder();
                    workCell.getWorkflowWork().getOrganizationCollection().forEach((id) -> {
                        OrganizationInfoEntity organization = CacheUtils.getCacheOrganization(id);
                        if (Objects.nonNull(organization)) {
                            organizationNames.append(organization.getOrganizationName());
                            organizationNames.append(' ');
                        }
                    });
                    workCell.setOrganizationLabelText(organizationNames.toString());
                });
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("craeteWorkFlowView end.");
        }

        return flow;
    }

    /**
     * バラ工程テーブル作成
     *
     * @param list
     */
    private void createSeparateWrokTable(List<ConWorkflowSeparateworkInfoEntity> list) {
        this.separateworkFieldPane.getChildren().clear();
        this.separateWorks.clear();
        this.separateWorks.addAll(list);
        this.separateWorks.sort(Comparator.comparing(property -> property.getWorkflowOrder()));

        this.separateWorkTable = new Table(this.separateworkFieldPane.getChildren()).isSelectCheckRecord(true).isColumnTitleRecord(true)
                .title(LocaleUtils.getString("key.AdditionalProcess")).customFooterItem(this.createSeparateWorkButton()).styleClass("ContentTitleLabel");
        this.separateWorkTable.setAbstractRecordFactory(new SeparateworkRecordFactory(this.separateWorkTable, this.separateWorks));
    }

    /**
     * 保存を実施する
     *
     * @return 保存に失敗したときfalse　それ以外の時true
     */
    private boolean registWorkflow() {
        logger.info("registWorkflow:Start");
        //未入力判定
        if (checkEmpty()) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
            return false;
        }

        if (!validItems()) {
            return false;
        }

        //工程順の更新
        WorkflowInfoEntity workflow = createRegistWorkflow();

        if (Objects.isNull(workflow)) {
            return false;
        }

        ResponseEntity res;
        if (Objects.nonNull(workflow.getWorkflowId()) && workflow.getWorkflowId() != 0) {
            res = workflowInfoFacade.updateWork(workflow);
        } else {
            res = workflowInfoFacade.registWork(workflow);
        }

        if (Objects.nonNull(res) && !ResponseAnalyzer.getAnalyzeResult(res)) {
            if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                // 排他バージョンが異なる。
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistOrderProcesses"), LocaleUtils.getString("key.alert.differentVerInfo"));
            }
            return false;
        }

        // 工程順マスタのキャッシュを削除する。
        CacheUtils.removeCacheData(WorkflowInfoEntity.class);

        //データを再取得する
        Long workflowId = workflow.getWorkflowId();
        if (Objects.nonNull(res.getUri())) {
            workflowId = UriConvertUtils.getUriToWorkflowId(res.getUri());
        }

        if (Objects.nonNull(workflowId)) {
            WorkflowInfoEntity newWorkflow = workflowInfoFacade.find(workflowId, true);
            if (Objects.nonNull(newWorkflow)) {
                selected.setWorkflow(newWorkflow);
            }
        }

        //登録された時の情報を最新の情報に。destroyで確認される。
        cloneWorkflow = selected.getWorkflowInfo().clone();

        logger.info("registWorkflow:End");

        return true;
    }

    /**
     * 個別編集ボタン作成
     *
     * @return
     */
    private List<Node> createSeparateWorkButton() {

        Button editButton = new Button(LocaleUtils.getString("key.EditCheckProcess"));
        editButton.getStyleClass().add("ContentButton");

        Button addButton = new Button(LocaleUtils.getString("key.AddAdditionalWork"));
        addButton.getStyleClass().add("ContentButton");

        Button deleteButton = new Button(LocaleUtils.getString("key.DeleteAdditionalWork"));
        deleteButton.getStyleClass().add("ContentButton");

        HBox pene = new HBox();
        pene.getChildren().addAll(addButton, deleteButton, editButton);
        pene.getStyleClass().add("ContentHBox");

        List<Node> nodes = new ArrayList<>();
        nodes.add(pene);

        //役割の権限によるボタン無効化.
        if (!this.loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
            editButton.setDisable(true);
            addButton.setDisable(true);
            deleteButton.setDisable(true);
        }

        //バラ工程を編集
        editButton.setOnAction((ActionEvent actionEvent) -> {
            try {
                List<ConWorkflowSeparateworkInfoEntity> editEntitys = separateWorkTable.getCheckedRecordItems();
                editSeparatework(editEntitys);
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });

        // 追加工程を追加
        addButton.setOnAction((ActionEvent actionEvent) -> {
            try {
                SelectDialogEntity selectDialogEntity = new SelectDialogEntity();
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkSingleSelectionCompo", selectDialogEntity, true);
                if (ret.equals(ButtonType.OK)
                        && Objects.nonNull(selectDialogEntity.getWorks())
                        && !selectDialogEntity.getWorks().isEmpty()) {
                    List<WorkInfoEntity> works = selectDialogEntity.getWorks();
                    WorkInfoEntity work = works.get(0);

                    if (selected.getWorkflowInfo().getConWorkflowSeparateworkInfoCollection().stream().
                            anyMatch(separatework -> separatework.getFkWorkId().equals(work.getWorkId()))) {
                        String msg = String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), work.getWorkName() + " : " + work.getWorkRev());
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), msg);
                        return;
                    }

                    ConWorkflowSeparateworkInfoEntity separateWork = new ConWorkflowSeparateworkInfoEntity(null, this.selected.getWorkflowInfo().getWorkflowId(), work.getWorkId(), false, 0);

                    separateWork.setWorkName(work.getWorkName() + " : " + work.getWorkRev());
                    separateWork.setEquipmentCollection(new ArrayList<>());
                    separateWork.setOrganizationCollection(new ArrayList<>());
                    separateWork.setStandardStartTime(DateUtils.min());
                    separateWork.setStandardEndTime(new Date(separateWork.getStandardStartTime().getTime() + (Objects.nonNull(work.getTaktTime()) ? work.getTaktTime() : 0)));

                    // 表示順
                    Optional<ConWorkflowSeparateworkInfoEntity> max = separateWorks.stream().max(Comparator.comparing(separatework -> separatework.getWorkflowOrder()));
                    if (max.isPresent()) {
                        separateWork.setWorkflowOrder(max.get().getWorkflowOrder() + 1);
                    }
                    this.selected.getWorkflowInfo().getConWorkflowSeparateworkInfoCollection().add(separateWork);
                    this.createSeparateWrokTable(this.selected.getWorkflowInfo().getConWorkflowSeparateworkInfoCollection());
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });

        //バラ工程の削除
        deleteButton.setOnAction((ActionEvent event) -> {
            try {
                List<ConWorkflowSeparateworkInfoEntity> editEntitys = separateWorkTable.getCheckedRecordItems();
                if (!editEntitys.isEmpty()) {
                    deleteSeparatework(editEntitys);
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });

        return nodes;
    }

    /**
     *
     * @param editEntitys
     */
    public void editSeparatework(List<ConWorkflowSeparateworkInfoEntity> editEntitys) {
        if (!editEntitys.isEmpty()) {
            if (editEntitys.size() <= 1) {
                //個別編集
                ConWorkflowSeparateworkInfoEntity workflowSeparatework = editEntitys.get(0);
                List<EquipmentInfoEntity> equipments = new ArrayList<>();
                List<OrganizationInfoEntity> organizations = new ArrayList<>();

                if (Objects.nonNull(workflowSeparatework.getEquipmentCollection())) {
                    workflowSeparatework.getEquipmentCollection().stream().forEach((entity) -> {
                        equipments.add(CacheUtils.getCacheEquipment(entity));
                    });
                }

                if (Objects.nonNull(workflowSeparatework.getOrganizationCollection())) {
                    workflowSeparatework.getOrganizationCollection().stream().forEach((entity) -> {
                        organizations.add(CacheUtils.getCacheOrganization(entity));
                    });
                }

                WorkSettingDialogEntity dialogEntity = new WorkSettingDialogEntity(0, workflowSeparatework.getStandardStartTime(), workflowSeparatework.getStandardEndTime(), workflowSeparatework.getSkipFlag(), equipments, organizations, true, true, null);
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkflowAssociationSettingCompo", dialogEntity);
                if (ret.equals(ButtonType.OK)) {
                    batchEditSeparateworks(editEntitys, dialogEntity);
                    createSeparateWrokTable(selected.getWorkflowInfo().getConWorkflowSeparateworkInfoCollection());
                }
            } else {
                //複数編集
                WorkSettingDialogEntity dialogEntity = new WorkSettingDialogEntity();
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkflowAssociationSettingCompo", dialogEntity);
                if (ret.equals(ButtonType.OK)) {
                    batchEditSeparateworks(editEntitys, dialogEntity);
                    createSeparateWrokTable(selected.getWorkflowInfo().getConWorkflowSeparateworkInfoCollection());
                }
            }
        }
    }

    /**
     * 追加工程の削除を行う
     *
     * @param selectEntitys
     */
    private void deleteSeparatework(List<ConWorkflowSeparateworkInfoEntity> selectEntitys) {
        final String messgage = selectEntitys.size() > 1
                ? LocaleUtils.getString("key.DeleteMultipleMessage")
                : LocaleUtils.getString("key.DeleteSingleMessage");
        final String content = selectEntitys.size() > 1
                ? null
                : selectEntitys.get(0).getWorkName();
        ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), messgage, content);
        if (!ret.equals(ButtonType.OK)) {
            return;
        }
        //
        List<ConWorkflowSeparateworkInfoEntity> separeteWorks = selected.getWorkflowInfo().getConWorkflowSeparateworkInfoCollection();
        for (ConWorkflowSeparateworkInfoEntity con : selectEntitys) {
            separeteWorks.remove(con);
        }
        createSeparateWrokTable(separeteWorks);
    }

    /**
     * データ更新処理
     *
     * @param editEntitys 編集対象のデータ
     * @param dialogEntity 編集内容
     */
    private void batchEditSeparateworks(List<ConWorkflowSeparateworkInfoEntity> editEntitys, WorkSettingDialogEntity dialogEntity) {
        editEntitys.stream().map((entity) -> {
            if (dialogEntity.isEditSingle()) {
                if (Objects.nonNull(dialogEntity.getStandardDay())) {
                    // TODO 2018/06/15 s-heya
                }

                if (Objects.nonNull(dialogEntity.getStartTime()) && Objects.nonNull(dialogEntity.getEndTime())) {
                    entity.setStandardStartTime(dialogEntity.getStartTime());
                    entity.setStandardEndTime(dialogEntity.getEndTime());
                }
            }

            return entity;
        }).map((entity) -> {
            //スキップ
            if (Objects.nonNull(dialogEntity.getSkip())) {
                entity.setSkipFlag(dialogEntity.getSkip());
            }
            return entity;
        }).map((entity) -> {
            //設備
            if (dialogEntity.isEditSingle()
                    || !dialogEntity.getOrganizations().isEmpty()) {
                List<Long> orgIdList = new ArrayList<>();
                dialogEntity.getOrganizations().stream().forEach((organization) -> {
                    orgIdList.add(organization.getOrganizationId());
                });
                entity.setOrganizationCollection(orgIdList);
            }
            return entity;
        }).forEach((entity) -> {
            //組織
            if (dialogEntity.isEditSingle()
                    || !dialogEntity.getEquipments().isEmpty()) {
                List<Long> equipIdList = new ArrayList<>();
                dialogEntity.getEquipments().stream().forEach((equipment) -> {
                    equipIdList.add(equipment.getEquipmentId());
                });
                entity.setEquipmentCollection(equipIdList);
            }
        });
    }

    /**
     * 必須入力項目のチェック
     *
     * @return
     */
    private boolean checkEmpty() {
        String workflowName = ((StringProperty) detailProperties.get(DETAIL_WORKFLOW_NAME).getProperty()).get();
        if (Objects.isNull(workflowName) || workflowName.isEmpty()) {
            return true;
        }

        for (KanbanPropertyTemplateInfoEntity entity : customProperties) {
            entity.updateMember();
            if (Objects.isNull(entity.getKanbanPropName())
                    || entity.getKanbanPropName().isEmpty()
                    || Objects.isNull(entity.getKanbanPropType())) {
                return true;
            }
        }

        if (ledgerPaths.stream().filter(p -> Objects.isNull(p.getValue()) || p.getValue().trim().isEmpty()).count() > 0) {
            return true;
        }

        return false;
    }

    /**
     * 入力項目チェック
     *
     * @return 入力データ有効:true, 入力エラー:false
     *
     */
    private boolean validItems() {
        //開始時間、終了時間フォーマット判定
        if (separateWorks.stream().anyMatch(separatework -> (Objects.isNull(separatework.getStandardStartTime()) || Objects.isNull(separatework.getStandardEndTime())))) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.TimeFormatErrMessage"));
            return false;
        }

        //開始時間<終了時間判定
        if (separateWorks.stream().anyMatch(separatework -> (separatework.getStandardStartTime().getTime() > separatework.getStandardEndTime().getTime()))) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DateCompErrMessage"));
            return false;
        }

        // 帳票テンプレートのファイル名重複判定
        List<String> templateNames = new ArrayList();
        for (SimpleStringProperty ledgerPath : ledgerPaths) {
            File ledgerFile = new File(ledgerPath.getValue());
            String templateName = new File(ledgerPath.getValue()).getName();
            if (templateNames.stream().filter(p -> p.toLowerCase().equals(templateName.toLowerCase()) || p.equals(templateName)).count() > 0) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), ledgerFile.getName()));
                return false;
            }
            templateNames.add(templateName);
        }

        // 追加工程の型チェック
        for (KanbanPropertyTemplateInfoEntity entity : customProperties) {
            // 値が空欄の場合はチェックしない。
            if (StringUtils.isEmpty(entity.getKanbanPropInitialValue())) {
                continue;
            }

            // 型に応じてチェックを行なう。
            switch (entity.getKanbanPropType()) {
                case TYPE_INTEGER:
                    if (!StringUtils.isInteger(entity.getKanbanPropInitialValue())) {
                        // 追加情報の値が項目のタイプと異なります
                        String message = new StringBuilder(LocaleUtils.getString("key.alert.differentTypeValue"))
                                .append(" (").append(entity.getKanbanPropName()).append(")")
                                .toString();
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message);
                        return false;
                    }
                    break;
                case TYPE_DATE:
                    if (!StringUtils.isDate(entity.getKanbanPropInitialValue(), "uuuu/M/d", true)) {
                        // 追加情報の値が項目のタイプと異なります
                        String message = new StringBuilder(LocaleUtils.getString("key.alert.differentTypeValue"))
                                .append(" (").append(entity.getKanbanPropName()).append(")")
                                .toString();
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message);
                        return false;
                    }
                    break;
            }
        }

        return true;
    }

    /**
     * 現在のフィールドに記入された情報を構築する
     *
     * @return
     */
    private WorkflowInfoEntity createRegistWorkflow() {
        //工程順の更新
        WorkflowInfoEntity workflow = selected.getWorkflowInfo();
        workflow.setWorkflowName(((StringProperty) detailProperties.get(DETAIL_WORKFLOW_NAME).getProperty()).get());
        // 使いどころがないため、版名を削除
        //workflow.setWorkflowRevision(((StringProperty) detailProperties.get("workflowRevision").getProperty()).get());

        // モデル名
        workflow.setModelName(((StringProperty) detailProperties.get(DETAIL_MODEL_NAME).getProperty()).get());

        // 作業番号 (enableDailyReport=true の場合のみ)
        if (WorkflowEditConfig.getEnableDailyReport()) {
            String workNumber = null;
            if (Objects.nonNull(detailProperties.get(DETAIL_WORK_NUMBER).getProperty())) {
                workNumber = ((StringProperty) detailProperties.get(DETAIL_WORK_NUMBER).getProperty()).get();
            }
            workflow.setWorkflowNumber(workNumber);
        }

        // 作業時間枠
        List<StringProperty> timePeriods = detailProperties.get(DETAIL_TIME_PERIODS).getProperties();

        try {
            SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
            workflow.setOpenTime(sd.parse(timePeriods.get(0).get()));
            workflow.setCloseTime(sd.parse(timePeriods.get(1).get()));
        } catch (Exception ex) {
        }

        // 作業順序
        SchedulePolicyEnum schedulePolicy = (SchedulePolicyEnum) ((ObjectProperty) detailProperties.get(DETAIL_SCHEDULE_POLICY).getProperty()).get();
        workflow.setSchedulePolicy(schedulePolicy);

        // 帳票テンプレートパス
        workflow.setLedgerPathPropertyCollection(ledgerPaths);

        workflow.setFkUpdatePersonId(loginUser.getId());
        workflow.setUpdateDatetime(new Date());

        //追加工程カンバン更新
        workflow.getConWorkflowSeparateworkInfoCollection().clear();
        separateWorks.sort(Comparator.comparing(separatework -> separatework.getStandardStartTime()));
        int order = 0;
        for (ConWorkflowSeparateworkInfoEntity entity : separateWorks) {
            entity.updateMember();
            entity.setWorkflowOrder(order);
            workflow.getConWorkflowSeparateworkInfoCollection().add(entity);
            order = order + 1;
        }

        //カンバンプロパティテンプレートの更新
        order = 0;
        workflow.getKanbanPropertyTemplateInfoCollection().clear();
        for (KanbanPropertyTemplateInfoEntity entity : customProperties) {
            String propVal = Objects.nonNull(entity.getKanbanPropInitialValue()) ? entity.getKanbanPropInitialValue() : "";
            if (templateRegexTexts.containsKey(entity.getKanbanPropName())
                    && !Pattern.matches(templateRegexTexts.get(entity.getKanbanPropName()), propVal)) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"),
                        String.format(LocaleUtils.getString("key.PropValueFormatErrMessage"), entity.getKanbanPropName()));
                return null;
            }

            entity.setKanbanPropOrder(order);
            workflow.getKanbanPropertyTemplateInfoCollection().add(entity);
            order = order + 1;
        }

        return workflow;
    }

    /**
     * 最初に表示された情報から変更があったか調べる
     *
     * @return
     */
    private boolean isChanged() {
        // 編集権限なし、または、編集不可モード時は常に無変更
        if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW) || this.disableOtherInputItems) {
            return false;
        }

        //新規作成時の対応
        //本来cloneする必要はないが工程順はこのメソッドでは取得できずnullになるためclone(内部でnullのとき空配列にしてる)
        //このEntityは工程順WorkflowAssemblyでも使っているがそっちではnullにならないため暫定処置
        WorkflowInfoEntity entity = createRegistWorkflow().clone();
        //キャンセル以外の画面遷移で変更を検査
        if (entity.equalsDisplayInfo(cloneWorkflow)) {
            return false;
        }

        return true;
    }

    /**
     * 画面破棄時に内容に変更がないか調べて変更があるなら保存する
     *
     * @return
     */
    @Override
    public boolean destoryComponent() {

        saveProperties();

        if (isChanged()) {
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            if (ButtonType.YES == buttonType) {
                return registWorkflow();
            } else if (ButtonType.CANCEL == buttonType) {
                return false;
            } else if (ButtonType.NO == buttonType) {
                WorkflowEditModel workflowEditModel = WorkflowEditModel.getInstance();
                if (workflowEditModel.isInnerMode()) {
                    workflowEditModel.setWorkflow(null);
                }

                Platform.runLater(() -> {
                    selected.setWorkflow(cloneWorkflow.clone());
                    createDetailView(selected);
                });
            }
        }

        return true;
    }

    /**
     * 設定を設定ファイルに保存する
     */
    private void saveProperties() {
        try {
            boolean isSelectedEquip = this.visibleEquipmentToggleButton.isSelected();
            boolean isSelectedOrg = this.visibleOrganizationToggleButton.isSelected();
            boolean isVisibleScale = this.visibleScaleToggleButton.isSelected();
            double scale = this.zoomSlider.getValue();

            properties.setProperty(Constants.VISIBLE_EQUIPMENT_KEY, String.valueOf(isSelectedEquip));
            properties.setProperty(Constants.VISIBLE_ORGANIZATION_KEY, String.valueOf(isSelectedOrg));
            properties.setProperty(Constants.VISIBLE_WORKFLOW_SCALE_KEY, String.valueOf(isVisibleScale));
            properties.setProperty(Constants.WORKFLOW_SCALE_KEY, String.valueOf(scale));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 設定を設定ファイルから読み込む
     */
    private void loadProperties() {
        try {
            String showEquipStr = properties.getProperty(Constants.VISIBLE_EQUIPMENT_KEY, Constants.VISIBLE_EQUIPMENT_DEFAULT);
            String showOrgStr = properties.getProperty(Constants.VISIBLE_ORGANIZATION_KEY, Constants.VISIBLE_ORGANIZATION_DEFAULT);
            String showScaleStr = properties.getProperty(Constants.VISIBLE_WORKFLOW_SCALE_KEY, Constants.VISIBLE_WORKFLOW_SCALE_DEFAULT);
            String scaleStr = properties.getProperty(Constants.WORKFLOW_SCALE_KEY, Constants.WORKFLOW_SCALE_DEFAULT);

            double scale = Double.valueOf(scaleStr);
            scale = scale > 10.0 ? 10.0 : scale < 1.0 ? 1.0 : scale;
            workflowScale = scale;

            this.visibleEquipmentToggleButton.setSelected(Boolean.valueOf(showEquipStr));
            this.visibleOrganizationToggleButton.setSelected(Boolean.valueOf(showOrgStr));
            this.visibleScaleToggleButton.setSelected(Boolean.valueOf(showScaleStr));
            this.zoomSlider.setValue(Double.valueOf(scaleStr));

            // 現状bindしていないため最初のみ手動で設定
            this.model.setVisibleEquipment(Boolean.valueOf(showEquipStr));
            this.model.setVisibleOrganization(Boolean.valueOf(showOrgStr));
            this.model.setScale(scale);

        } catch (Exception ex) {
            logger.fatal(ex, ex);

            this.model.setVisibleEquipment(true);
            this.model.setVisibleOrganization(true);
            this.model.setScale(1.0);
        }
    }

    /**
     * 編集前の追加工程名にリビジョンを追加する
     */
    private void addRevToSeparateWorkName() {
        selected.getWorkflowInfo().getConWorkflowSeparateworkInfoCollection().forEach((separateWork) -> {
            WorkInfoEntity work = CacheUtils.getCacheWork(separateWork.getFkWorkId());
            separateWork.setWorkName(separateWork.getWorkName() + " : " + work.getWorkRev());
        });
        cloneWorkflow.getConWorkflowSeparateworkInfoCollection().forEach((separateWork) -> {
            WorkInfoEntity work = CacheUtils.getCacheWork(separateWork.getFkWorkId());
            separateWork.setWorkName(separateWork.getWorkName() + " : " + work.getWorkRev());
        });
        selected.getWorkflowInfo().getConWorkflowWorkInfoCollection().forEach((workInfoWork) -> {
            WorkInfoEntity work = CacheUtils.getCacheWork(workInfoWork.getFkWorkId());
            workInfoWork.setWorkName(workInfoWork.getWorkName() + " : " + work.getWorkRev());
        });
         cloneWorkflow.getConWorkflowWorkInfoCollection().forEach((workInfoWork) -> {
            WorkInfoEntity work = CacheUtils.getCacheWork(workInfoWork.getFkWorkId());
            workInfoWork.setWorkName(workInfoWork.getWorkName() + " : " + work.getWorkRev());
        });
    }
    

}
