/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.reportoutplugin.component;

import adtekfuji.admanagerapp.reportoutplugin.common.Constants;
import adtekfuji.admanagerapp.reportoutplugin.entity.ReportOutSearchResult;
import adtekfuji.admanagerapp.reportoutplugin.entity.ReportOutputSaveSettingEntity;
import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualPropertyEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.InterruptReasonInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition.ReportOutSortEnum;
import jp.adtekfuji.adFactory.entity.view.ReportOutInfoEntity;
import jp.adtekfuji.adFactory.entity.view.ReportOutSummaryInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.dialog.MessageDialog;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;

/**
 * FXML Controller class
 *
 * @author e-mori
 */
@FxComponent(id = "ReportOutDetailCompo", fxmlPath = "/fxml/admanagerreportoutplugin/report_out_detail_compo.fxml")
public class ReportOutDetailCompoFxController implements Initializable, ComponentHandler {

    private final Properties properties = AdProperty.getProperties();
    private static final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final WorkKanbanInfoFacade workKanbanInfoFacade = new WorkKanbanInfoFacade();
    private final ActualResultInfoFacade actualResultInfoFacade = new ActualResultInfoFacade();

    private SimpleDateFormat formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));

    private static final Character COMMA = ',';
    private static final Character TAB = '\t';
    private static final Character LINE_FEED = '\n';
    private static final int RANGE_IDS = 100;
    private static final String UI_PROP_NAME = "reportOutList";// adManagerUI.properties の項目名
    private static final String UI_PROP_SUMMARY_NAME = "reportOutSummary";// adManagerUI.properties の項目名

    private static final String DISABLE_DATE_STYLE = "-fx-background-color: lightgray;";// カレンダーで選択不可な日のスタイル
    private static final String DISP_DATE_FORMAT = "yyyy/MM/dd";
    private static final List<String> SJIS_LIST = Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS");
    private static final String SJIS_ENCODE = "MS932";
    private static final String UTF8_ENCODE = "UTF-8";
    private static final String TSV_EXTENSION = "tsv";
    private static final String CONF_PATH = new StringBuilder(System.getenv("ADFACTORY_HOME")).append(File.separator).append("conf").toString();
    private static final String DEFAULT_DATA_COUNT = "0 / 0";// 未検索時の実績件数表示

    // 追加項目ファイル
    private static final String COLUMNS_FILE_PATH = new StringBuilder(CONF_PATH).append(File.separator).append("reportOutColumns.dat").toString();
    private static final String COLUMNS_FILE_ENCODE = "UTF-8";

    private final List<TableColumn<ReportOutInfoEntity, ?>> baseColumns = new ArrayList();
    private final List<TableColumn<ReportOutSummaryInfoEntity, ?>> baseSummaryColumns = new ArrayList();

    private Map<String, Boolean> additionalColumns = new LinkedHashMap();

    private final int SEARCH_MAX = StringUtils.parseInteger(properties.getProperty(Constants.REPORT_OUT_SEARCH_MAX_KEY, Constants.REPORT_OUT_SEARCH_MAX_DEFAULT));

    private boolean isInterruptedSearch = false;

    private final static String PICK_LITE_WORK_NAME_REGEX_KEY = "PickLiteWorkNameRegex";

    private final List<InterruptReasonInfoEntity> interruptReasons = new ArrayList();

    @FXML
    private SplitPane reportOutPane;
    @FXML
    private PropertySaveTableView<ReportOutSummaryInfoEntity> reportOutSummaryList;
    @FXML
    private TableColumn<ReportOutSummaryInfoEntity, String> kanbanNameSummaryColumn;// カンバン名
    @FXML
    private TableColumn<ReportOutSummaryInfoEntity, String> workflowNameSummaryColumn;// 工程順名
    @FXML
    private TableColumn<ReportOutSummaryInfoEntity, String> workNameSummaryColumn;// 工程名
    @FXML
    private TableColumn<ReportOutSummaryInfoEntity, String> statusSummaryColumn;// ステータス
    @FXML
    private TableColumn<ReportOutSummaryInfoEntity, Number> taktTimeSummaryColumn;// 標準時間
    @FXML
    private TableColumn<ReportOutSummaryInfoEntity, Number> totalWorkTimeSummaryColumn;// 作業時間
    @FXML
    private TableColumn<ReportOutSummaryInfoEntity, Number> totalSuspendTimeSummaryColumn;// 中断時間
    @FXML
    private TableColumn<ReportOutSummaryInfoEntity, String> startDatetimeSummaryColumn;// 開始時間
    @FXML
    private TableColumn<ReportOutSummaryInfoEntity, String> completeDatetimeSummaryColumn;// 完了時間
    @FXML
    private TableColumn<ReportOutSummaryInfoEntity, String> delayReasonSummaryColumn;// 遅延理由
    @FXML
    private TableColumn<ReportOutSummaryInfoEntity, String> productionNumberSummaryColumn;// 製造番号
    @FXML
    private TableColumn<ReportOutSummaryInfoEntity, String> modelNameSummaryColumn;// モデル名

    //@FXML
    //private AnchorPane MainScenePane;
    @FXML
    private SplitPane reportOutListPane;
    @FXML
    private PropertySaveTableView<ReportOutInfoEntity> reportOutList;
    @FXML
    private TableColumn<ReportOutInfoEntity, String> kanbanNameColumn;// カンバン名
    @FXML
    private TableColumn<ReportOutInfoEntity, String> workflowNameColumn;// 工程順名
    @FXML
    private TableColumn<ReportOutInfoEntity, String> workNameColumn;// 工程名
    @FXML
    private TableColumn<ReportOutInfoEntity, String> workingParsonColumn;// 作業者
    @FXML
    private TableColumn<ReportOutInfoEntity, String> workEquipmentColumn;// 設備名
    @FXML
    private TableColumn<ReportOutInfoEntity, String> interruptReasonColumn;// 中断理由
    @FXML
    private TableColumn<ReportOutInfoEntity, String> delayReasonColumn;// 遅延理由
    @FXML
    private TableColumn<ReportOutInfoEntity, String> implementDatetimeColumn;// 実施時間
    @FXML
    private TableColumn<ReportOutInfoEntity, String> actualStatusColumn;// 実績ステータス
    @FXML
    private TableColumn<ReportOutInfoEntity, Number> totalWorkTimeColumn;// 作業時間
    @FXML
    private TableColumn<ReportOutInfoEntity, String> defectReasonColumn;// 不良理由
    @FXML
    private TableColumn<ReportOutInfoEntity, Number> defectNumColumn;// 不良数
    @FXML
    private TableColumn<ReportOutInfoEntity, String> productionNumberColumn;// 製造番号
    @FXML
    private TableColumn<ReportOutInfoEntity, Number> taktTimeColumn;// 標準作業時間
    @FXML
    private TableColumn<ReportOutInfoEntity, Number> compNumColumn;// 完了数
    @FXML
    private TableColumn<ReportOutInfoEntity, String> serialNoColumn;

    @FXML
    private CheckBox dateCheckBox;
    @FXML
    private Pane fromToDatePane;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private CheckBox kanbanCheckBox;
    @FXML
    private TextField kanbanTextField;

    @FXML
    private VBox productionNumberPane;
    @FXML
    private CheckBox productionNumberCheckBox;
    @FXML
    private TextField productionNumberTextField;

    // TODO: 機種名を使用する場合、有効にする。
    @FXML
    private VBox modelNamePane;
    @FXML
    private CheckBox modelNameCheckBox;
    @FXML
    private TextField modelNameTextField;

    @FXML
    private SelectPane equipmentSelectPaneController;
    @FXML
    private SelectPane organizationSelectPaneController;
    @FXML
    private SelectPane workflowSelectPaneController;
    @FXML
    private VBox statusPane;
    @FXML
    private CheckBox statusCheckBox;
    @FXML
    private CheckListView<KanbanStatusEnum> statusListView;
    @FXML
    private Label dataCountLabel;
    @FXML
    private Pane progressPane;
    @FXML
    private CheckBox fixedColumnsCheckBox;
    @FXML
    private CheckBox summaryCheckBox;
    @FXML
    private Button changeButton;

    @Override
    public boolean destoryComponent() {
        logger.info("{} is started.", getClass().getSimpleName() + "::destoryComponent");

        SplitPaneUtils.saveDividerPosition(this.reportOutPane, getClass().getSimpleName());
        SplitPaneUtils.saveDividerPosition(this.reportOutListPane, UI_PROP_SUMMARY_NAME + "Pane");

        return true;
    }

    /**
     * 実績出力画面を初期化する。
     *
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        reportOutList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
        SplitPaneUtils.loadDividerPosition(this.reportOutPane, getClass().getSimpleName());
        SplitPaneUtils.loadDividerPosition(this.reportOutListPane, UI_PROP_SUMMARY_NAME + "Pane", 0.5);

        if (!this.properties.containsKey(Constants.CSV_FILENAME_KEY)) {
            this.properties.setProperty(Constants.CSV_FILENAME_KEY, Constants.CSV_FILENAME_DEFAULT);
        }
        if (!this.properties.containsKey(Constants.CSV_FILENAME_SUMMARY_KEY)) {
            this.properties.setProperty(Constants.CSV_FILENAME_SUMMARY_KEY, Constants.CSV_FILENAME_SUMMARY_DEFAULT);
        }
        if (!this.properties.containsKey(Constants.CSV_ENCODE_KEY)) {
            this.properties.setProperty(Constants.CSV_ENCODE_KEY, Constants.CSV_ENCODE_DEFAULT);
        }

        // 1回の検索件数
        if (!this.properties.containsKey(Constants.REPORT_OUT_SEARCH_MAX_KEY)) {
            properties.setProperty(Constants.REPORT_OUT_SEARCH_MAX_KEY, Constants.REPORT_OUT_SEARCH_MAX_DEFAULT);
        }
        if (!this.properties.containsKey(Constants.ACTUAL_PROP_SEARCH_MAX_KEY)) {
            properties.setProperty(Constants.ACTUAL_PROP_SEARCH_MAX_KEY, Constants.ACTUAL_PROP_SEARCH_MAX_DEFAULT);
        }

        // カンバン名
        this.kanbanNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, String> param) -> param.getValue().kanbanNameProperty());
        // 工程順名
        this.workflowNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, String> param)
                -> new SimpleStringProperty(formatWorkflowName(param.getValue().getWorkflowName(), param.getValue().getWorkflowRev())));
        // 工程名
        final boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());
        final String pickLiteWorkNameRegex = properties.getProperty(PICK_LITE_WORK_NAME_REGEX_KEY);
        this.workNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, String> param) -> {
            StringProperty workNameProperty = param.getValue().workNameProperty();
            if (isLiteOption) {
                // 工程名だけ抽出
                try {
                    Matcher m = Pattern.compile(pickLiteWorkNameRegex).matcher(workNameProperty.getValue());
                    if (m.find()) {
                        workNameProperty.setValue(m.group(1));
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
            return workNameProperty;
        });
        // 作業者
        this.workingParsonColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, String> param) -> param.getValue().organizationNameProperty());
        // 設備名
        this.workEquipmentColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, String> param) -> param.getValue().equipmentNameProperty());
        // 実績ステータス
        this.actualStatusColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, String> param) -> Bindings.createStringBinding(()
                -> LocaleUtils.getString(param.getValue().getActualStatus().getResourceKey())));
        // 中断理由
        this.interruptReasonColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, String> param) -> param.getValue().interruptReasonProperty());
        // 遅延理由
        this.delayReasonColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, String> param) -> param.getValue().delayReasonProperty());
        // 実施時間
        this.implementDatetimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, String> param) -> Bindings.createStringBinding(()
                -> Objects.isNull(param.getValue().getImplementDatetime()) ? "" : formatter.format(param.getValue().getImplementDatetime())));
        // 作業時間
        this.totalWorkTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, Number> param) -> Bindings.createIntegerBinding(()
                -> Objects.isNull(param.getValue().getWorkingTime()) ? 0 : param.getValue().getWorkingTime() / 1000));
        // 不良理由
        this.defectReasonColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, String> param) -> param.getValue().defectReasonProperty());
        // 不良数
        this.defectNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, Number> param) -> param.getValue().defectNumProperty());
        // 製造番号
        this.productionNumberColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, String> param) -> param.getValue().productionNumberProperty());
        // 標準作業時間
        this.taktTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, Number> param) -> Bindings.createIntegerBinding(()
                -> Objects.isNull(param.getValue().getTaktTime()) ? 0 : param.getValue().getTaktTime() / 1000));
        // 完了数
        this.compNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, Number> param) -> Bindings.createIntegerBinding(() -> param.getValue().getCompNum()));
        // シリアル番号
        this.serialNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutInfoEntity, String> param) -> param.getValue().serialNoProperty());

        // 作業実績(サマリー)
        reportOutSummaryList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        // カンバン名
        this.kanbanNameSummaryColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutSummaryInfoEntity, String> param) -> param.getValue().kanbanNameProperty());
        // 工程順名
        this.workflowNameSummaryColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutSummaryInfoEntity, String> param)
                -> new SimpleStringProperty(formatWorkflowName(param.getValue().getWorkflowName(), param.getValue().getWorkflowRev())));
        // 工程名
        this.workNameSummaryColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutSummaryInfoEntity, String> param) -> {
            StringProperty workNameProperty = param.getValue().workNameProperty();
            if (isLiteOption) {
                // 工程名だけ抽出
                try {
                    Matcher m = Pattern.compile(pickLiteWorkNameRegex).matcher(workNameProperty.getValue());
                    if (m.find()) {
                        workNameProperty.setValue(m.group(1));
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
            return workNameProperty;
        });
        // 実績ステータス
        this.statusSummaryColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutSummaryInfoEntity, String> param) -> Bindings.createStringBinding(()
                -> LocaleUtils.getString(param.getValue().getWorkStatus().getResourceKey())));
        // 標準時間
        this.taktTimeSummaryColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutSummaryInfoEntity, Number> param) -> Bindings.createIntegerBinding(()
                -> Objects.isNull(param.getValue().getTaktTime()) ? 0 : param.getValue().getTaktTime() / 1000));
        // 作業時間
        this.totalWorkTimeSummaryColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutSummaryInfoEntity, Number> param) -> Bindings.createLongBinding(()
                -> Objects.isNull(param.getValue().getSumTimes()) ? 0L : param.getValue().getSumTimes() / 1000L));
        // 中断時間
        this.totalSuspendTimeSummaryColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutSummaryInfoEntity, Number> param) -> Bindings.createIntegerBinding(()
                -> Objects.isNull(param.getValue().getInterruptTimes()) ? 0 : param.getValue().getInterruptTimes() / 1000));
        // 開始時間
        this.startDatetimeSummaryColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutSummaryInfoEntity, String> param) -> Bindings.createStringBinding(()
                -> Objects.isNull(param.getValue().getStartDatetime()) ? "" : formatter.format(param.getValue().getStartDatetime())));
        // 完了時間
        this.completeDatetimeSummaryColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutSummaryInfoEntity, String> param) -> Bindings.createStringBinding(()
                -> Objects.isNull(param.getValue().getCompDatetime()) ? "" : formatter.format(param.getValue().getCompDatetime())));
        // 遅延理由
        this.delayReasonSummaryColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutSummaryInfoEntity, String> param) -> param.getValue().fkDelayReasonProperty());
        // 製造番号
        this.productionNumberSummaryColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutSummaryInfoEntity, String> param) -> param.getValue().productionNumberProperty());
        // モデル名
        this.modelNameSummaryColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReportOutSummaryInfoEntity, String> param) -> param.getValue().modelNameProperty());

        // ステータス
        this.statusListView.setCellFactory(listView -> new CheckBoxListCell<KanbanStatusEnum>(statusListView::getItemBooleanProperty) {
            @Override
            public void updateItem(KanbanStatusEnum status, boolean empty) {
                super.updateItem(status, empty);
                setText(status == null ? "" : LocaleUtils.getString(status.getResourceKey()));
            }
        });

        ObservableList<KanbanStatusEnum> list = FXCollections.observableArrayList(KanbanStatusEnum.values());
        list.removeAll(Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.PLANNING));
        this.statusListView.setItems(list);
        
        // リストの標準項目を設定する。
        this.baseColumns.add(kanbanNameColumn);
        this.baseColumns.add(workflowNameColumn);
        this.baseColumns.add(workNameColumn);
        this.baseColumns.add(workingParsonColumn);
        this.baseColumns.add(workEquipmentColumn);
        this.baseColumns.add(actualStatusColumn);
        this.baseColumns.add(interruptReasonColumn);
        this.baseColumns.add(delayReasonColumn);
        this.baseColumns.add(implementDatetimeColumn);
        this.baseColumns.add(totalWorkTimeColumn);
        this.baseColumns.add(defectReasonColumn);
        this.baseColumns.add(defectNumColumn);
        this.baseColumns.add(productionNumberColumn);
        this.baseColumns.add(taktTimeColumn);
        this.baseColumns.add(compNumColumn);
        this.baseColumns.add(serialNoColumn);

        this.baseSummaryColumns.add(kanbanNameSummaryColumn);
        this.baseSummaryColumns.add(workflowNameSummaryColumn);
        this.baseSummaryColumns.add(workNameSummaryColumn);
        this.baseSummaryColumns.add(statusSummaryColumn);
        this.baseSummaryColumns.add(taktTimeSummaryColumn);
        this.baseSummaryColumns.add(totalWorkTimeSummaryColumn);
        this.baseSummaryColumns.add(totalSuspendTimeSummaryColumn);
        this.baseSummaryColumns.add(startDatetimeSummaryColumn);
        this.baseSummaryColumns.add(completeDatetimeSummaryColumn);
        this.baseSummaryColumns.add(delayReasonSummaryColumn);
        this.baseSummaryColumns.add(productionNumberSummaryColumn);
        this.baseSummaryColumns.add(modelNameSummaryColumn);

        // 追加項目ファイルを読み込む。
        this.readAdditionalColumnsFile();

        // カラムの初期化
        this.reportOutList.init(UI_PROP_NAME);
        this.reportOutSummaryList.init(UI_PROP_SUMMARY_NAME);

        // 対象日選択の初期化
        this.initFromToDatePicker();

        // 設備選択欄
        this.equipmentSelectPaneController.setLabelText(LocaleUtils.getString("key.Equipment"));
        this.equipmentSelectPaneController.setOnClickButtonListener(event -> {
            onEquipmentSelect(event);
        });

        // 組織選択欄
        this.organizationSelectPaneController.setLabelText(LocaleUtils.getString("key.Organization"));
        this.organizationSelectPaneController.setOnClickButtonListener(event -> {
            onOrganizationSelect(event);
        });

        // 工程順選択欄
        this.workflowSelectPaneController.setLabelText(LocaleUtils.getString("key.workflow"));
        this.workflowSelectPaneController.setOnClickButtonListener(event -> {
            onWorkflowSelect(event);
        });
        
        // 前回の検索条件を読み込む。
        this.loadProperties();

        this.dataCountLabel.setText(DEFAULT_DATA_COUNT);
        
        if (LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.DELETE_ACTUAL)) {
            this.reportOutList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ReportOutInfoEntity> observable, ReportOutInfoEntity oldValue, ReportOutInfoEntity newValue) -> {
                this.changeButton.setDisable(!Objects.nonNull(reportOutList.getSelectionModel().getSelectedItem()));
            });
        }

        this.changeButton.setDisable(true);        

        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                CacheUtils.createCacheEquipment(true);
                CacheUtils.createCacheOrganization(true);
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                blockUI(false);
            }

            @Override
            protected void failed() {
                super.failed();
                if (Objects.nonNull(this.getException())) {
                    logger.fatal(this.getException(), this.getException());
                }
                blockUI(false);
            }
        };
        new Thread(task).start();
    }

    /**
     * 対象日コントロールの設定を行なう。
     */
    private void initFromToDatePicker() {
        // 開始日には、終了日より後の日は選択できない。
        this.fromDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (Objects.isNull(date) || Objects.isNull(toDatePicker.getValue())) {
                            return;
                        }

                        if (date.isAfter(toDatePicker.getValue())) {
                            setDisable(true);
                            setStyle(DISABLE_DATE_STYLE);
                        }
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
                        if (Objects.isNull(date) || Objects.isNull(fromDatePicker.getValue())) {
                            return;
                        }

                        if (date.isBefore(fromDatePicker.getValue())) {
                            setDisable(true);
                            setStyle(DISABLE_DATE_STYLE);
                        }
                    }
                };
            }
        });
    }

    /**
     * 対象日 チェックイベント
     *
     * @param event
     */
    @FXML
    private void onCheckDate(ActionEvent event) {
        boolean isDisable = !this.dateCheckBox.isSelected();
        this.fromToDatePane.setDisable(isDisable);

        // 対象日が無効な場合、カンバン名検索以外はチェックを外して無効にする。
        if (isDisable) {
            this.productionNumberCheckBox.setSelected(false);
            this.modelNameCheckBox.setSelected(false);// TODO: 機種名を使用する場合、有効にする。
            this.equipmentSelectPaneController.setSelected(false);
            this.organizationSelectPaneController.setSelected(false);
            this.workflowSelectPaneController.setSelected(false);
            this.statusCheckBox.setSelected(false);
            this.onCheckStatus(null);
        }

        this.productionNumberPane.setDisable(isDisable);
        this.modelNamePane.setDisable(isDisable);// TODO: 機種名を使用する場合、有効にする。
        this.equipmentSelectPaneController.setDisable(isDisable);
        this.organizationSelectPaneController.setDisable(isDisable);
        this.workflowSelectPaneController.setDisable(isDisable);
        this.statusPane.setDisable(isDisable);
    }

    /**
     * カンバン チェックイベント
     *
     * @param event
     */
    @FXML
    private void onCheckKanbanName(ActionEvent event) {
        boolean isDisable = !this.kanbanCheckBox.isSelected();
        this.kanbanTextField.setDisable(isDisable);
    }

    /**
     * 製造番号 チェックイベント
     *
     * @param event
     */
    @FXML
    private void onCheckProductionNumber(ActionEvent event) {
        boolean isDisable = !this.productionNumberCheckBox.isSelected();
        this.productionNumberTextField.setDisable(isDisable);
    }

    // TODO: 機種名を使用する場合、有効にする。
    /**
     * 機種 チェックイベント
     *
     * @param event
     */
    @FXML
    private void onCheckModelName(ActionEvent event) {
        boolean isDisable = !this.modelNameCheckBox.isSelected();
        this.modelNameTextField.setDisable(isDisable);
    }

    /**
     * ステータス チェックイベント
     *
     * @param event
     */
    @FXML
    private void onCheckStatus(ActionEvent event) {
        boolean isDisable = !this.statusCheckBox.isSelected();
        this.statusListView.setDisable(isDisable);
    }

    /**
     * 設備 選択ボタンイベント
     *
     * @param event
     */
    private void onEquipmentSelect(ActionEvent event) {
        try {
            List<EquipmentInfoEntity> selectedItems = new ArrayList();

            if (Objects.nonNull(equipmentSelectPaneController.getChoiceDatas())) {
                equipmentSelectPaneController.getChoiceDatas().keySet().stream().forEach(id -> {
                    selectedItems.add(new EquipmentInfoFacade().find(id));
                });
            }

            SelectDialogEntity<EquipmentInfoEntity> selectDialog = new SelectDialogEntity().equipments(selectedItems);

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Equipment"), "EquipmentSelectionCompo", selectDialog, true);
            if (ButtonType.OK.equals(ret)) {
                if (!selectDialog.getEquipments().isEmpty()) {
                    // 選択された設備の情報を、設備選択ペインにセットする。
                    Map<Long, String> choiceDatas = new LinkedHashMap();
                    selectDialog.getEquipments().stream().forEach(dat -> {
                        choiceDatas.put(dat.getEquipmentId(), dat.getEquipmentName());
                    });

                    equipmentSelectPaneController.setChoiceDatas(choiceDatas);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 組織 選択ボタンイベント
     *
     * @param event
     */
    private void onOrganizationSelect(ActionEvent event) {
        try {
            List<OrganizationInfoEntity> selectedItems = new ArrayList();

            if (Objects.nonNull(organizationSelectPaneController.getChoiceDatas())) {
                organizationSelectPaneController.getChoiceDatas().keySet().stream().forEach(id -> {
                    selectedItems.add(new OrganizationInfoFacade().find(id));
                });
            }

            SelectDialogEntity<OrganizationInfoEntity> selectDialog = new SelectDialogEntity().organizations(selectedItems);

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialog, true);
            if (ButtonType.OK.equals(ret)) {
                if (!selectDialog.getOrganizations().isEmpty()) {
                    // 選択された設備の情報を、設備選択ペインにセットする。
                    Map<Long, String> choiceDatas = new LinkedHashMap();
                    selectDialog.getOrganizations().stream().forEach(dat -> {
                        choiceDatas.put(dat.getOrganizationId(), dat.getOrganizationName());
                    });

                    organizationSelectPaneController.setChoiceDatas(choiceDatas);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程順 選択ボタンイベント
     *
     * @param event
     */
    private void onWorkflowSelect(ActionEvent event) {
        try {
            List<WorkflowInfoEntity> selectedItems = new ArrayList();
            if (Objects.nonNull(workflowSelectPaneController.getChoiceDatas())) {
                workflowSelectPaneController.getChoiceDatas().keySet().stream().forEach(id -> {
                    WorkflowInfoEntity workflow = new WorkflowInfoFacade().find(id);
                    if (Objects.nonNull(workflow) && Objects.nonNull(workflow.getWorkflowId())) {
                        selectedItems.add(new WorkflowInfoFacade().find(id));
                    }
                });
            }

            SelectDialogEntity<WorkflowInfoEntity> selectDialog = new SelectDialogEntity().workflows(selectedItems);

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.OrderProcesses"), "WorkflowMultiSelectionCompo", selectDialog, true);
            if (ButtonType.OK.equals(ret)) {
                if (!selectDialog.getWorkflows().isEmpty()) {
                    // 選択された工程順の情報を、工程順選択ペインにセットする。
                    Map<Long, String> choiceDatas = new LinkedHashMap();
                    selectDialog.getWorkflows().stream().forEach(dat -> {
                        choiceDatas.put(dat.getWorkflowId(), formatWorkflowName(dat.getWorkflowName(), dat.getWorkflowRev()));
                    });

                    workflowSelectPaneController.setChoiceDatas(choiceDatas);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 検索ボタンイベント
     *
     * @param event
     */
    @FXML
    private void onSearch(ActionEvent event) {
        this.updateView();
    }

     /**
     * 実績時間の修正
     *
     * @param event
     */
    @FXML
    private void onChangeActualTime(ActionEvent event) {
        logger.info("onChangeActualTime start.");

        ReportOutInfoEntity report = this.reportOutList.getSelectionModel().getSelectedItem();
        
        try {
            this.blockUI(true);

            // 計画変更ダイアログを表示する。
            ButtonType ret = sc.showDialog(LocaleUtils.getString("changeActualTime"), "TimeChangeDialog", report);
            if (!ButtonType.OK.equals(ret)) {
                return;
            }
            
            ResponseEntity result = this.actualResultInfoFacade.updateTime(report.getActualId(), DateUtils.format(report.getImplementDatetime()));
            if (!result.isSuccess()) {
                if (ServerErrorTypeEnum.INVALID_ARGUMENT.equals(result.getErrorType())) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Error"), LocaleUtils.getString("errorChangeActualTime"));
                } else {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Error"), LocaleUtils.getString("key.alert.serverError"));
                }
            }

            this.updateView();
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);

        } finally {
            this.blockUI(false);
            logger.info("onChangeActualTime end.");
        }
    }
    
    /**
     * 画面更新処理
     */
    private void updateView() {
        logger.info("updateView");
        boolean isCancel = false;
        try {
            blockUI(true);

            // 対象日が有効で、日付がどちらも未入力の場合は検索不可。
            if (this.dateCheckBox.isSelected() && Objects.isNull(fromDatePicker.getValue()) && Objects.isNull(toDatePicker.getValue())) {
                isCancel = true;
                MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.OutReportTitle"), LocaleUtils.getString("key.warn.NotSpecifiedDate"),
                        MessageDialogEnum.MessageDialogType.Warning, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
                return;
            }

            this.isInterruptedSearch = true;
            this.reportOutList.getItems().clear();
            this.reportOutList.getSortOrder().clear();
            this.reportOutSummaryList.getItems().clear();
            this.reportOutSummaryList.getSortOrder().clear();
            this.dataCountLabel.setText(DEFAULT_DATA_COUNT);

            // 検索条件
            final ReportOutSearchCondition condition = createReportOutSearchCondition();

            Task task = new Task<Long>() {
                @Override
                protected Long call() throws Exception {
                    // 実績出力情報の件数を取得する。
                    return actualResultInfoFacade.reportOutSearchCount(condition);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        Long actualCount = this.getValue();

                        // 実績出力情報の件数を表示する。
                        dataCountLabel.setText(String.format("0 / %,d", actualCount));

                        if (actualCount > 0) {
                            // 実績出力情報を取得して、リスト表示を更新する。
                            updateViewSub(condition, actualCount, 0);
                        } else {
                            // 0件の場合は、検索条件を保存して検索処理を終了する。
                            updateInterruptReason(null);
                            isInterruptedSearch = false;
                            saveProperties();
                            blockUI(false);
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        blockUI(false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                        // エラー
                        MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.OutReportTitle"), LocaleUtils.getString("key.alert.systemError"),
                                MessageDialogEnum.MessageDialogType.Error, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
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
            isCancel = true;
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }
    }

    /**
     * 実績出力情報の検索条件を作成する。
     *
     * @return 実績出力情報の検索条件
     */
    private ReportOutSearchCondition createReportOutSearchCondition() {
        ReportOutSearchCondition condition = new ReportOutSearchCondition().sortType(ReportOutSortEnum.ACTUAL_ID);

        // 対象日
        if (dateCheckBox.isSelected()) {
            Date fromDate = Objects.isNull(fromDatePicker.getValue()) ? null : DateUtils.getBeginningOfDate(fromDatePicker.getValue());
            Date toDate = Objects.isNull(toDatePicker.getValue()) ? null : DateUtils.getEndOfDate(toDatePicker.getValue());

            condition.setFromDate(fromDate);
            condition.setToDate(toDate);
        }

        // カンバン名
        if (kanbanCheckBox.isSelected()) {
            condition.setKanbanName(kanbanTextField.getText());
        }

        // 製造番号
        if (productionNumberCheckBox.isSelected()) {
            condition.setProductionNumber(productionNumberTextField.getText());
        }

        // TODO: 機種名を使用する場合、有効にする。
        // 機種名
        if (modelNameCheckBox.isSelected()) {
            condition.setModelName(modelNameTextField.getText());
        }

        // 設備ID一覧
        if (equipmentSelectPaneController.isSelected()) {
            condition.setEquipmentIdCollection(new ArrayList(equipmentSelectPaneController.getChoiceDatas().keySet()));
        }

        // 組織ID一覧
        if (organizationSelectPaneController.isSelected()) {
            condition.setOrganizationIdCollection(new ArrayList(organizationSelectPaneController.getChoiceDatas().keySet()));
        }

        // 工程順ID一覧
        if (workflowSelectPaneController.isSelected()) {
            condition.setWorkflowIdCollection(new ArrayList(workflowSelectPaneController.getChoiceDatas().keySet()));
        }

        // ステータス
        if (statusCheckBox.isSelected()) {
            ObservableList<KanbanStatusEnum> items = statusListView.getCheckModel().getCheckedItems();
            if (!items.isEmpty()) {
                condition.setActualStatusCollection(items);
            } else {
                condition.setActualStatusCollection(Arrays.asList(KanbanStatusEnum.values()));
            }
        }

        return condition;
    }

    /**
     * 実績出力情報を取得して、リスト表示を更新する。
     *
     * @param condition 実績出力情報の検索条件
     * @param actualCount 実績出力情報の件数
     * @param actualsFrom 実績出力情報一覧取得範囲の先頭(from)
     */
    private void updateViewSub(ReportOutSearchCondition condition, Long actualCount, long actualsFrom) {
        logger.info("updateViewSub: condition={}, actualCount={}, actualsFrom={}", condition, actualCount, actualsFrom);
        try {
            blockUI(true);

            Task task = new Task<ReportOutSearchResult>() {
                @Override
                protected ReportOutSearchResult call() throws Exception {
                    return searchThread(condition, actualsFrom);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    boolean isEnd = true;
                    try {
                        ReportOutSearchResult result = this.getValue();

                        reportOutList.getItems().addAll(result.getActuals());
                        reportOutSummaryList.getItems().addAll(result.getWorkActuals());

                        dataCountLabel.setText(String.format("%,d / %,d", reportOutList.getItems().size(), actualCount));

                        // 実施時間順にソートする。
                        ComparatorChain comparator = new ComparatorChain();
                        comparator.addComparator(new BeanComparator("implementDatetime", new NullComparator()));
                        comparator.addComparator(new BeanComparator("actualId", new NullComparator()));
                        Collections.sort(reportOutList.getItems(), comparator);

                        long actualsFrom = result.getActualsTo() + 1;
                        if (actualsFrom < actualCount) {
                            // 残りがある場合、継続確認ダイアログを表示する。
                            String message = String.format(LocaleUtils.getString("key.overRangeSearchContinue"), SEARCH_MAX);
                            MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.OutReportTitle"), message,
                                    MessageDialogEnum.MessageDialogType.Question, MessageDialogEnum.MessageDialogButtons.YesNo, 1.0, "#000000", "#ffffff");
                            if (dialogResult.equals(MessageDialogEnum.MessageDialogResult.Yes)) {
                                // 実績出力情報を取得して、リスト表示を更新する。
                                updateViewSub(condition, actualCount, actualsFrom);
                                isEnd = false;
                            }
                        } else {
                            // 全件取得完了
                            isInterruptedSearch = false;
                        }

                        if (isEnd) {
                            // 現在の検索条件を保存する。
                            saveProperties();
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        if (isEnd) {
                            blockUI(false);
                        }
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                        // エラー
                        MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.OutReportTitle"), LocaleUtils.getString("key.alert.systemError"),
                                MessageDialogEnum.MessageDialogType.Error, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
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

    /**
     *
     * @param condition
     * @param actualsFrom
     * @return
     */
    private ReportOutSearchResult searchThread(ReportOutSearchCondition condition, long actualsFrom) {
        ReportOutSearchResult result = new ReportOutSearchResult();

        ObservableList<ReportOutInfoEntity> tableData = FXCollections.observableArrayList();
        ObservableList<ReportOutSummaryInfoEntity> workTableData = FXCollections.observableArrayList();
        long actualsTo = actualsFrom + SEARCH_MAX - 1;

        // 実績出力情報一覧を取得する。
        List<ReportOutInfoEntity> actuals;
        if (SEARCH_MAX > 0) {
            actuals = actualResultInfoFacade.reportOutSearch(condition, actualsFrom, actualsTo);
        } else {
            actuals = actualResultInfoFacade.reportOutSearch(condition);
            actualsTo = actuals.size() - 1;
        }

        // 実績プロパティ一覧の取得により、追加情報(JSON)からの変換を行なう。
        for (ReportOutInfoEntity actual : actuals) {
            actual.getPropertyCollection();
        }

        tableData.addAll(actuals);

        // 一覧に含まれる工程カンバン
        List<Long> ids = new ArrayList<>();
        tableData.stream().forEach((entity) -> {
            if (ids.indexOf(entity.getFkWorkKanbanId()) < 0) {
                ids.add(entity.getFkWorkKanbanId());
            }
        });

        List<WorkKanbanInfoEntity> workKanbanEntitiys = new ArrayList();
        for (int fromIndex = 0; fromIndex < ids.size(); fromIndex += RANGE_IDS) {
            int toIndex = fromIndex + RANGE_IDS;
            if (toIndex >= ids.size()) {
                toIndex = ids.size();
            }
            List<Long> findIds = ids.subList(fromIndex, toIndex);
            List<WorkKanbanInfoEntity> workKanbans = workKanbanInfoFacade.find(findIds);
            workKanbanEntitiys.addAll(workKanbans);
        }

        // 工程実績情報
        workKanbanEntitiys.stream()
                .forEach((entity) -> {
                    // 工程実績
                    ReportOutSummaryInfoEntity workReport = new ReportOutSummaryInfoEntity(entity);

                    List<ReportOutInfoEntity> reportKanbanInfo;
                    reportKanbanInfo = tableData.stream()
                            .filter((reportOutInfo) -> (reportOutInfo.getFkKanbanId().equals((entity.getFkKanbanId())) && reportOutInfo.getFkWorkId().equals(entity.getFkWorkId())))
                            .collect(Collectors.toList());
                    if (reportKanbanInfo.size() > 0) {
                        workReport.setKanbanName(reportKanbanInfo.get(0).getKanbanName());
                        workReport.setWorkflowName(reportKanbanInfo.get(0).getWorkflowName());
                        workReport.setWorkflowRev(reportKanbanInfo.get(0).getWorkflowRev());
                        workReport.setWorkName(reportKanbanInfo.get(0).getWorkName());
                        workReport.setProductionNumber(reportKanbanInfo.get(0).getProductionNumber());
                        workReport.setModelName(reportKanbanInfo.get(0).getModelName());

                        Optional<ReportOutInfoEntity> delayReason = reportKanbanInfo.stream().filter(report -> Objects.nonNull(report.getDelayReason())).findFirst();
                        if (delayReason.isPresent()) {
                            workReport.setDelayReason(delayReason.get().getDelayReason());
                        }

                        // 中断理由
                        List<ReportOutInfoEntity> interruptReasonsList = new ArrayList<>();
                        interruptReasonsList.addAll(reportKanbanInfo.stream()
                                .filter(o -> (!StringUtils.isEmpty(o.getInterruptReason()) && KanbanStatusEnum.WORKING.equals(o.getActualStatus())))
                                .collect(Collectors.toList()));

                        if (interruptReasonsList.size() > 0) {
                            // 中断理由毎に中断時間を取得する
                            Map<String, Integer> interruptTims = interruptReasonsList.stream()
                                    .collect(Collectors.groupingBy(ReportOutInfoEntity::getInterruptReason,
                                            Collectors.summingInt(ReportOutInfoEntity::getNonWorkTime)));
                            workReport.setInterruptReasonTimes(interruptTims);
                            logger.info("interruptTims:{}", interruptTims);

                            // 中断時間合計
                            int totalInterruptTime = interruptReasonsList.stream()
                                    .mapToInt(ReportOutInfoEntity::getNonWorkTime)
                                    .sum();
                            workReport.setInterruptTimes(totalInterruptTime);
                        }
                    }
                    // 表示条件
                    workTableData.add(workReport);
                });
        Platform.runLater(() -> {
            updateInterruptReason(workTableData);
        });

        tableData.stream()
                .filter((entity) -> (entity.getActualStatus().equals(KanbanStatusEnum.COMPLETION)))
                .forEach((entity) -> {
                    workKanbanEntitiys.stream()
                            .filter((workKanbanEntitiy) -> (workKanbanEntitiy.getWorkKanbanId().equals(entity.getFkWorkKanbanId())))
                            .forEach((workKanbanEntitiy) -> {
                                entity.getPropertyCollection().add(new ActualPropertyEntity(LocaleUtils.getString("key.WorkTimeTotal"), CustomPropertyTypeEnum.TYPE_STRING, Long.toString(workKanbanEntitiy.getSumTimes()), 0));
                            });
                });

        if (fixedColumnsCheckBox.isSelected() && workflowSelectPaneController.isSelected()) {
            // 固定列で出力する
            additionalColumns.clear();
            tableData.stream().forEach((resultEntity) -> {
                resultEntity.getPropertyCollection().stream().forEach((propertyEntity) -> {
                    additionalColumns.put(propertyEntity.getActualPropName(), true);
                });
            });

            // リストのカラム情報を更新
            updateColumns(false);

            // 追加項目ファイルを更新
            writeAdditionalColumnsFile();

        } else {
            // 追加情報リストを更新
            tableData.stream().forEach((resultEntity) -> {
                resultEntity.getPropertyCollection().stream().filter((propertyEntity) -> (!additionalColumns.keySet().contains(propertyEntity.getActualPropName()))).forEach((propertyEntity) -> {
                    additionalColumns.put(propertyEntity.getActualPropName(), false);
                });
            });
        }

        result.setActuals(tableData);
        result.setActualsTo(actualsTo);
        result.setWorkActuals(workTableData);

        return result;
    }

    /**
     * 追加項目ボタンイベント
     *
     * @param event
     */
    @FXML
    private void onAdditionalColumns(ActionEvent event) {
        logger.info("onAdditionalColumns: {}", event);
        try {
            // 追加項目リストのコピーを作成する。
            Map<String, Boolean> items = new LinkedHashMap(this.additionalColumns);

            // 追加項目ダイアログを表示する。
            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.AdditionalColumns"), "AdditionalColumnsDialog", items);
            if (ButtonType.OK.equals(ret)) {
                additionalColumns = items;

                // リストのカラム情報を更新する。
                updateColumns(true);

                // 追加項目ファイルを更新する。
                writeAdditionalColumnsFile();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * CSV出力ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onExport(ActionEvent event) {
        logger.info("onExport: {}", event);

        // 検索結果がない場合は出力不可。
        if (this.reportOutList.getItems().isEmpty()) {
            MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.PrintOutCSV"), LocaleUtils.getString("key.NoReportOutData"),
                    MessageDialogEnum.MessageDialogType.Warning, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
            return;
        }

        // 検索を中断していた場合は警告表示する。
        if (this.isInterruptedSearch) {
            MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(sc.getWindow(),
                    LocaleUtils.getString("key.PrintOutCSV"), LocaleUtils.getString("key.warn.WorkReport.InterruptedSearch"),
                    MessageDialogEnum.MessageDialogType.Question, MessageDialogEnum.MessageDialogButtons.YesNo, 1.0, "#000000", "#ffffff");
            if (!dialogResult.equals(MessageDialogEnum.MessageDialogResult.Yes)) {
                return;
            }
        }

        // 設定を保存する。
        try {
            Properties props = AdProperty.getProperties(Constants.REPORT_OUT_PROPETRY_NAME);
            props.setProperty(Constants.SEARCH_OUTPUT_SUMMARY, String.valueOf(this.summaryCheckBox.isSelected()));
            AdProperty.store(Constants.REPORT_OUT_PROPETRY_NAME);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        
        blockUI(true);
        writeThread(event);
    }

    /**
     * CSV出力処理
     *
     * @param event
     */
    private void writeThread(ActionEvent event) {
        logger.info("writeThread: {}", event);
        try {
            Node node = (Node) event.getSource();
            FileChooser fileChooser = new FileChooser();

            File desktopDir = new File(System.getProperty("user.home"), "Desktop");
            if (desktopDir.exists()) {
                fileChooser.setInitialDirectory(desktopDir);
            }

            fileChooser.setInitialFileName(properties.getProperty(Constants.CSV_FILENAME_KEY));
            fileChooser.setTitle(LocaleUtils.getString("key.OutReportTitle"));
            FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("TSV files (*.tsv)", "*.tsv");
            fileChooser.getExtensionFilters().addAll(extFilter1, extFilter2);

            File selectFile = fileChooser.showSaveDialog(node.getScene().getWindow());
            if (Objects.isNull(selectFile) && !this.summaryCheckBox.isSelected()) {
                return;
            }

            if (Objects.nonNull(selectFile)) {
                String encode = properties.getProperty(Constants.CSV_ENCODE_KEY).toUpperCase();
                if (SJIS_LIST.contains(encode)) {
                    encode = SJIS_ENCODE;
                }

                Character separate = (FilenameUtils.getExtension(selectFile.getPath()).equals(TSV_EXTENSION)) ? TAB : COMMA;

                Set<Map.Entry<String, Boolean>> items = additionalColumns.entrySet();

                // CSV出力
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(selectFile), encode))) {
                    // ヘッダー
                    StringBuilder headerSb = new StringBuilder();
                    for (TableColumn<ReportOutInfoEntity, ?> column : reportOutList.getVisibleLeafColumns()) {
                        headerSb.append(column.getText());
                        headerSb.append(separate);
                    }
                    headerSb.append(LINE_FEED);
                    writer.write(headerSb.toString());

                    // データ
                    for (int row = 0; row < reportOutList.getItems().size(); row++) {
                        StringBuilder rowSb = new StringBuilder();

                        for (TableColumn<ReportOutInfoEntity, ?> column : reportOutList.getVisibleLeafColumns()) {
                            // データ内の改行コードは文字列に変換して出力する。
                            String rowString = toString(column.getCellData(row)).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
                            rowSb.append(rowString);
                            rowSb.append(separate);
                        }
                        rowSb.append(LINE_FEED);
                        writer.write(rowSb.toString());
                    }
                } catch (Exception ex) {
                    throw ex;
                }
            }

            if (!this.summaryCheckBox.isSelected()) {
                MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.PrintOutCSV"), String.format(LocaleUtils.getString("key.Completed"), LocaleUtils.getString("key.OutReportTitle")),
                    MessageDialogEnum.MessageDialogType.Infomation, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
                return;
            }
        
            // 工程実績のCSV出力
            FileChooser fileChooserummartSummart = new FileChooser();

            File desktopDirSummart = new File(System.getProperty("user.home"), "Desktop");
            if (desktopDirSummart.exists()) {
                fileChooser.setInitialDirectory(desktopDirSummart);
            }

            fileChooserummartSummart.setInitialFileName(properties.getProperty(Constants.CSV_FILENAME_SUMMARY_KEY));
            fileChooserummartSummart.setTitle(LocaleUtils.getString("key.OutReportTitle"));
            fileChooserummartSummart.getExtensionFilters().addAll(extFilter1, extFilter2);
            File summaryFile = fileChooserummartSummart.showSaveDialog(node.getScene().getWindow());
            if (Objects.nonNull(summaryFile)) {
                String encode = properties.getProperty(Constants.CSV_ENCODE_KEY).toUpperCase();
                if (SJIS_LIST.contains(encode)) {
                    encode = SJIS_ENCODE;
                }

                Character separate = (FilenameUtils.getExtension(summaryFile.getPath()).equals(TSV_EXTENSION)) ? TAB : COMMA;
                // CSV出力
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(summaryFile), encode))) {
                    // ヘッダー
                    StringBuilder headerSb = new StringBuilder();
                    for (TableColumn<ReportOutSummaryInfoEntity, ?> column : reportOutSummaryList.getVisibleLeafColumns()) {
                        headerSb.append(column.getText());
                        headerSb.append(separate);
                    }
                    headerSb.append(LINE_FEED);
                    writer.write(headerSb.toString());

                    // データ
                    for (int row = 0; row < reportOutSummaryList.getItems().size(); row++) {
                        StringBuilder rowSb = new StringBuilder();

                        for (TableColumn<ReportOutSummaryInfoEntity, ?> column : reportOutSummaryList.getVisibleLeafColumns()) {
                            // データ内の改行コードは文字列に変換して出力する。
                            String rowString = toString(column.getCellData(row)).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
                            rowSb.append(rowString);
                            rowSb.append(separate);
                        }
                        rowSb.append(LINE_FEED);
                        writer.write(rowSb.toString());
                    }
                } catch (Exception ex) {
                    throw ex;
                }

                MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.PrintOutCSV"), String.format(LocaleUtils.getString("key.Completed"), LocaleUtils.getString("key.OutReportTitle")),
                    MessageDialogEnum.MessageDialogType.Infomation, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
            } else {
                if (Objects.nonNull(selectFile)) {
                    MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.PrintOutCSV"), String.format(LocaleUtils.getString("key.Completed"), LocaleUtils.getString("key.OutReportTitle")),
                    MessageDialogEnum.MessageDialogType.Infomation, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
                }
            }
                            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 設定保存ボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onSaveSetting(ActionEvent event) {
        logger.info("onSaveSetting: {}", event);

        FileChooser fileChooser = new FileChooser();

        // 初期フォルダ (前回のフォルダ)
        String folderPath = properties.getProperty(Constants.REPORT_OUT_SAVE_SETTING_FOLDER, "");
        File initDir = !StringUtils.isEmpty(folderPath) ? new File(folderPath) : new File(System.getProperty("user.home"), "Desktop");
        fileChooser.setInitialDirectory(initDir);
        fileChooser.setInitialFileName(Constants.SAVE_SETTING_INITIAL_FILENAME);
        fileChooser.setTitle(LocaleUtils.getString("key.SaveSetting"));
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().addAll(filter);

        Node node = (Node) event.getSource();
        File selectFile = fileChooser.showSaveDialog(node.getScene().getWindow());
        if (Objects.isNull(selectFile)) {
            // ファイル選択をキャンセルした
            return;
        }

        // 選択フォルダを記憶。
        properties.setProperty(Constants.REPORT_OUT_SAVE_SETTING_FOLDER, selectFile.getParent());

        try {
            blockUI(true);
            // 設定取得
            ReportOutputSaveSettingEntity saveEntity = new ReportOutputSaveSettingEntity();

            Map<String, Boolean> columns = additionalColumns.entrySet().stream().filter(p -> p.getValue()).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue(), (u, v) -> v, LinkedHashMap::new));
            saveEntity.setAdditionalColumns(columns);
            saveEntity.setColumnsOrder(reportOutList.getColumnsOrder());
            saveEntity.setColumnsWidth(reportOutList.getColumnsWidth());
            saveEntity.setColumnsVisible(reportOutList.getColumnsVisible());
            saveEntity.setSummaryColumnsOrder(reportOutSummaryList.getColumnsOrder());
            saveEntity.setSummaryColumnsWidth(reportOutSummaryList.getColumnsWidth());
            saveEntity.setSummaryColumnsVisible(reportOutSummaryList.getColumnsVisible());

            // JSON文字列に変換する。
            String saveStr = JsonUtils.objectToJson(saveEntity);

            // 設定をファイルに書き込み
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(selectFile), UTF8_ENCODE))) {
                writer.write(saveStr);
                // 完了ダイアログ表示
                MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.SaveSetting"), String.format(LocaleUtils.getString("key.Completed"), LocaleUtils.getString("key.SaveSetting")),
                        MessageDialogEnum.MessageDialogType.Infomation, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            // 失敗ダイアログ表示
            MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.SaveSetting"), LocaleUtils.getString("key.FaildToProcess"),
                    MessageDialogEnum.MessageDialogType.Infomation, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
        } finally {
            blockUI(false);
            logger.info("onSaveSetting end");
        }
    }

    /**
     * 設定読込ボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onLoadSetting(ActionEvent event) {
        logger.info("onLoadSetting: {}", event);

        FileChooser fileChooser = new FileChooser();

        // 初期フォルダ (前回のフォルダ)
        String folderPath = properties.getProperty(Constants.REPORT_OUT_LOAD_SETTING_FOLDER, "");
        File initDir = !StringUtils.isEmpty(folderPath) ? new File(folderPath) : new File(System.getProperty("user.home"), "Desktop");
        fileChooser.setInitialDirectory(initDir);
        fileChooser.setInitialFileName("");
        fileChooser.setTitle(LocaleUtils.getString("key.LoadSetting"));
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().addAll(filter);

        Node node = (Node) event.getSource();
        File selectFile = fileChooser.showOpenDialog(node.getScene().getWindow());
        if (Objects.isNull(selectFile)) {
            // ファイル選択をキャンセルした
            return;
        }

        // 選択フォルダを記憶。
        properties.setProperty(Constants.REPORT_OUT_LOAD_SETTING_FOLDER, selectFile.getParent());

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logger.info("Load Start.");
                blockUI(true);

                // 設定取得
                final ReportOutputSaveSettingEntity loadEntity;
                try {
                    List<String> lines = Files.readAllLines(selectFile.toPath(), Charset.forName(UTF8_ENCODE));
                    loadEntity = JsonUtils.jsonToObject(lines.get(0), ReportOutputSaveSettingEntity.class);
                    loadEntity.getAdditionalColumns().entrySet(); // nullならエラー発生
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    // ファイルが正しくありません
                    throw new Exception(LocaleUtils.getString("key.IncorrectFile"));
                }

                try {
                    Platform.runLater(() -> {
                        // 追加項目初期化
                        additionalColumns.entrySet().forEach(v -> v.setValue(false));
                        updateColumns(true);
                        // 追加項目復元
                        loadEntity.getAdditionalColumns().entrySet().forEach(v -> additionalColumns.put(v.getKey(), true));
                        writeAdditionalColumnsFile();
                        updateColumns(true);
                        // 並び順復元
                        reportOutList.storeColumnsOrder(loadEntity.getColumnsOrder());
                        reportOutList.restoreColumn();
                        reportOutSummaryList.storeColumnsOrder(loadEntity.getSummaryColumnsOrder());
                        reportOutSummaryList.restoreColumn();
                        // 横幅復元
                        reportOutList.storeColumnsWidth(loadEntity.getColumnsWidth());
                        reportOutList.restoreWidth();
                        reportOutSummaryList.storeColumnsWidth(loadEntity.getSummaryColumnsWidth());
                        reportOutSummaryList.restoreWidth();
                        // 表示プロパティ復元
                        reportOutList.storeColumnsVisible(loadEntity.getColumnsVisible());
                        reportOutList.restoreVisible();
                        reportOutSummaryList.storeColumnsVisible(loadEntity.getSummaryColumnsVisible());
                        reportOutSummaryList.restoreVisible();
                        // 画面更新
                        reportOutList.refresh();
                        reportOutSummaryList.refresh();
                    });
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    throw new Exception(LocaleUtils.getString("key.FaildToProcess"));
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                // 完了ダイアログ表示
                MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.LoadSetting"), String.format(LocaleUtils.getString("key.Completed"), LocaleUtils.getString("key.LoadSetting")),
                        MessageDialogEnum.MessageDialogType.Infomation, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
                blockUI(false);
                logger.info("Load Complete.");
            }

            @Override
            protected void failed() {
                super.failed();
                try {
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                        // 失敗ダイアログ表示
                        MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.LoadSetting"), this.getException().getMessage(),
                                MessageDialogEnum.MessageDialogType.Infomation, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                    logger.info("Load Failed.");
                }
            }
        };
        new Thread(task).start();

        logger.info("onLoadSetting end");
    }

    /**
     * 文字列を返す。
     *
     * @param value オブジェクト
     * @return 文字列
     */
    private String toString(Object value) {
        if (Objects.isNull(value)) {
            return "";
        }
        return Objects.toString(value);
    }

    /**
     * 数値を文字列に変換する。(nullの場合は空)
     *
     * @param value 数値
     * @return 数値の文字列
     */
    private String formatNumberValue(Integer value) {
        if (Objects.isNull(value)) {
            return "";
        }
        return String.valueOf(value);
    }

    /**
     * 表示用の工程順名を取得する。
     *
     * @param workflowName 工程順名
     * @param workflowRev 工程順の版数
     * @return 表示用の工程順名
     */
    private String formatWorkflowName(String workflowName, Integer workflowRev) {
        return String.format("%s : %d", workflowName, workflowRev);
    }

    /**
     * 更新中の画面処理
     *
     * @param flg 更新画面切り替えの有無
     */
    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            sc.blockUI("ContentNaviPane", flg);
            progressPane.setVisible(flg);
        });
    }

    /**
     * 追加項目ファイルを読み込む。
     */
    private void readAdditionalColumnsFile() {
        logger.info("readAdditionalColumnsFile");
        try {
            additionalColumns.clear();

            Path path = Paths.get(COLUMNS_FILE_PATH);
            if (Files.exists(path)) {
                List<String> rows = Files.readAllLines(path, Charset.forName(COLUMNS_FILE_ENCODE));
                for (String row : rows) {
                    additionalColumns.put(row, true);
                }
            }

            // リストのカラム情報を更新する。
            updateColumns(true);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 追加項目ファイルを更新する。
     */
    private void writeAdditionalColumnsFile() {
        logger.info("writeAdditionalColumnsFile");
        try {
            // 値がtrueのキーリストを取得する。
            Map<String, Boolean> map = additionalColumns.entrySet().stream().filter(p -> p.getValue()).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue(), (u, v) -> v, LinkedHashMap::new));
            List<String> rows = new ArrayList<>(map.keySet());

            // ファイルを更新する。
            Path path = Paths.get(COLUMNS_FILE_PATH);
            Files.write(path, rows, Charset.forName(COLUMNS_FILE_ENCODE),
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 中断理由のカラム情報を更新する
     */
    private void updateInterruptReason(List<ReportOutSummaryInfoEntity> interruptReasonTimes) {
        logger.info("addInterruptReason");
        try {

            // カラム(中断理由)を削除
            List<TableColumn<ReportOutSummaryInfoEntity, ?>> removeSummaryColumns = this.reportOutSummaryList.getColumns().stream()
                    .filter(p -> !(this.baseSummaryColumns.contains(p)))
                    .collect(Collectors.toList());
            if (!removeSummaryColumns.isEmpty()) {
                this.reportOutSummaryList.getColumns().removeAll(removeSummaryColumns);
            }
            
            if (Objects.isNull(interruptReasonTimes)) {
                return;
            }
            
            // カラム(中断理由)を追加
            List<TableColumn<ReportOutSummaryInfoEntity, ?>> addWorkColumns = new LinkedList<>();
            interruptReasonTimes.stream().forEach(workInfo -> {
                if (Objects.nonNull(workInfo.getInterruptReasonTimes())) {
                    Set<Map.Entry<String, Integer>> items = workInfo.getInterruptReasonTimes().entrySet();
                    Map<String, TableColumn<ReportOutSummaryInfoEntity, ?>> map = addWorkColumns.stream()
                            .collect(Collectors.toMap(TableColumn::getText, p -> p));

                    items.stream().forEach(item -> {
                        if (!map.containsKey(item.getKey())) {
                            TableColumn<ReportOutSummaryInfoEntity, Number> propertyColumn = new TableColumn<>(item.getKey());
                            propertyColumn.setCellValueFactory((param) -> Bindings.createIntegerBinding(()
                                    -> Objects.isNull(param.getValue().getPropertyValue(item.getKey())) ? 0 : param.getValue().getPropertyValue(item.getKey()) / 1000));
                            propertyColumn.setStyle("-fx-alignment: center-right;");
                            addWorkColumns.add(propertyColumn);
                        }
                    });
                }
            });

            // カラムを追加
            if (!addWorkColumns.isEmpty()) {
                this.reportOutSummaryList.getColumns().addAll(addWorkColumns);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * リストのカラム情報を更新する。
     *
     * @param restore 列幅と位置を復元するかどうか
     */
    private void updateColumns(boolean restore) {
        logger.info("updateColumns");
        try {
            // 削除するカラムを列挙
            List<TableColumn<ReportOutInfoEntity, ?>> removeColumns = this.reportOutList.getColumns().stream()
                    .filter(p -> !(this.baseColumns.contains(p)
                    || (this.additionalColumns.containsKey(p.getText()) && this.additionalColumns.get(p.getText()))))
                    .collect(Collectors.toList());

            // 追加するカラムを列挙
            List<TableColumn<ReportOutInfoEntity, ?>> addColumns = new LinkedList<>();
            if (!this.additionalColumns.isEmpty()) {
                Set<Map.Entry<String, Boolean>> items = this.additionalColumns.entrySet().stream()
                        .filter(p -> p.getValue())
                        .collect(Collectors.toCollection(LinkedHashSet::new));

                Map<String, TableColumn<ReportOutInfoEntity, ?>> map = this.reportOutList.getColumns().stream()
                        .collect(Collectors.toMap(TableColumn::getText, p -> p));

                for (Map.Entry<String, Boolean> item : items) {
                    if (!map.containsKey(item.getKey())) {
                        TableColumn<ReportOutInfoEntity, String> propertyColumn = new TableColumn<>(item.getKey());
                        propertyColumn.setCellValueFactory(p -> p.getValue().getPropertyValue(item.getKey()));
                        addColumns.add(propertyColumn);
                    }
                }
            }

            // カラムを削除
            if (!removeColumns.isEmpty()) {
                this.reportOutList.getColumns().removeAll(removeColumns);
            }

            // カラムを追加
            if (!addColumns.isEmpty()) {
                this.reportOutList.getColumns().addAll(addColumns);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 前回の検索条件を読み込む。
     */
    private void loadProperties() {
        logger.info("loadProperties");
        try {
            String confFile = new StringBuilder(Constants.REPORT_OUT_PROPETRY_NAME)
                    .append(Constants.PROPERTIES_EXT)
                    .toString();

            boolean isNewFile = false;
            File file = new File(CONF_PATH, confFile);
            if (!file.exists()) {
                isNewFile = true;
            }

            AdProperty.load(Constants.REPORT_OUT_PROPETRY_NAME, confFile);

            Properties props = AdProperty.getProperties(Constants.REPORT_OUT_PROPETRY_NAME);

            if (isNewFile) {
                // 対象日の選択
                props.setProperty(Constants.SEARCH_DATE_SELECTED, String.valueOf(true));

                // 対象日
                String propDate = "";
                LocalDate dateNow = LocalDate.now();
                if (Objects.nonNull(dateNow)) {
                    propDate = DateTimeFormatter.ofPattern(DISP_DATE_FORMAT).format(dateNow);
                }
                props.setProperty(Constants.SEARCH_FROM_DATE, propDate);
                props.setProperty(Constants.SEARCH_TO_DATE, propDate);

                // 保存する。
                AdProperty.store(Constants.REPORT_OUT_PROPETRY_NAME);
            }

            // 対象日の選択
            boolean dateSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_DATE_SELECTED, String.valueOf(true)));
            this.dateCheckBox.setSelected(dateSelected);
            this.onCheckDate(null);

            // 対象日
            String propFromDate = props.getProperty(Constants.SEARCH_FROM_DATE);
            if (!StringUtils.isEmpty(propFromDate)) {
                LocalDate date = LocalDate.parse(propFromDate, DateTimeFormatter.ofPattern(DISP_DATE_FORMAT));
                this.fromDatePicker.setValue(date);
            }

            String propToDate = props.getProperty(Constants.SEARCH_TO_DATE);
            if (!StringUtils.isEmpty(propToDate)) {
                LocalDate date = LocalDate.parse(propToDate, DateTimeFormatter.ofPattern(DISP_DATE_FORMAT));
                this.toDatePicker.setValue(date);
            }

            // カンバン名の選択
            boolean kanbanSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_KANBAN_NAME_SELECTED, String.valueOf(false)));
            this.kanbanCheckBox.setSelected(kanbanSelected);
            this.onCheckKanbanName(null);

            // カンバン名
            String kanbanName = props.getProperty(Constants.SEARCH_KANBAN_NAME);
            this.kanbanTextField.setText(kanbanName);

            // 製造番号の選択
            boolean productionNumberSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_PRODUCTION_NUMBER_SELECTED, String.valueOf(false)));
            this.productionNumberCheckBox.setSelected(productionNumberSelected);
            this.onCheckProductionNumber(null);

            // 製造番号
            String productinNumber = props.getProperty(Constants.SEARCH_PRODUCTION_NUMBER);
            this.productionNumberTextField.setText(productinNumber);

            // TODO: 機種名を使用する場合、有効にする。
            // 機種名の選択
            boolean modelNameSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_MODEL_NAME_SELECTED, String.valueOf(false)));
            this.modelNameCheckBox.setSelected(modelNameSelected);
            this.onCheckModelName(null);

            // 機種名
            String modelName = props.getProperty(Constants.SEARCH_MODEL_NAME);
            this.modelNameTextField.setText(modelName);
            // 設備の選択
            boolean equipmentSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_EQUIPMENT_SELECTED, String.valueOf(false)));
            this.equipmentSelectPaneController.setSelected(equipmentSelected);

            // 設備ID
            String propEquipmentIds = props.getProperty(Constants.SEARCH_EQUIPMENT_IDS, "");
            List<String> equipmentIds = Arrays.asList(propEquipmentIds.split(","));

            // 設備名
            String propEquipmentNames = props.getProperty(Constants.SEARCH_EQUIPMENT_NAME, "");
            List<String> equipmentNames = Arrays.asList(propEquipmentNames.split(","));

            Map<Long, String> equipments = new LinkedHashMap();
            for (int i = 0; i < equipmentIds.size(); i++) {
                String id = equipmentIds.get(i);
                if (id.isEmpty() || i >= equipmentNames.size()) {
                    break;
                }

                String name = equipmentNames.get(i);
                equipments.put(Long.valueOf(id), name);
            }
            this.equipmentSelectPaneController.setChoiceDatas(equipments);

            // 組織の選択
            boolean organizationSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_ORGANIZATION_SELECTED, String.valueOf(false)));
            this.organizationSelectPaneController.setSelected(organizationSelected);

            // 組織ID
            String propOrganizationIds = props.getProperty(Constants.SEARCH_ORGANIZATION_IDS, "");
            List<String> organizationIds = Arrays.asList(propOrganizationIds.split(","));

            // 組織名
            String propOrganizationNames = props.getProperty(Constants.SEARCH_ORGANIZATION_NAME, "");
            List<String> organizationNames = Arrays.asList(propOrganizationNames.split(","));

            Map<Long, String> organizations = new LinkedHashMap();
            for (int i = 0; i < organizationIds.size(); i++) {
                String id = organizationIds.get(i);
                if (id.isEmpty() || i >= organizationNames.size()) {
                    break;
                }

                String name = organizationNames.get(i);
                organizations.put(Long.valueOf(id), name);
            }
            this.organizationSelectPaneController.setChoiceDatas(organizations);

            // 工程順の選択
            boolean workflowSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_WORKFLOW_SELECTED, String.valueOf(false)));
            this.workflowSelectPaneController.setSelected(workflowSelected);

            // 工程順ID
            String propWorkflowIds = props.getProperty(Constants.SEARCH_WORKFLOW_IDS, "");
            List<String> workflowIds = Arrays.asList(propWorkflowIds.split(","));

            // 工程順名
            String propWorkflowNames = props.getProperty(Constants.SEARCH_WORKFLOW_NAME, "");
            List<String> workflowNames = Arrays.asList(propWorkflowNames.split(","));

            Map<Long, String> workflows = new LinkedHashMap();
            for (int i = 0; i < workflowIds.size(); i++) {
                String id = workflowIds.get(i);
                if (id.isEmpty() || i >= workflowNames.size()) {
                    break;
                }

                String name = workflowNames.get(i);
                workflows.put(Long.valueOf(id), name);
            }
            this.workflowSelectPaneController.setChoiceDatas(workflows);

            // ステータスの選択
            boolean statusSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_STATUS_SELECTED, String.valueOf(false)));
            this.statusCheckBox.setSelected(statusSelected);
            this.onCheckStatus(null);

            // ステータス
            String propStatus = props.getProperty(Constants.SEARCH_STATUS, "");
            String[] statuses = propStatus.split(",");
            for (String status : statuses) {
                if (!StringUtils.isEmpty(status)) {
                    this.statusListView.getCheckModel().check(KanbanStatusEnum.valueOf(status));
                }
            }

            boolean fixedColumnsSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_FIXED_COLUMNS_SELECTED, String.valueOf(false)));
            this.fixedColumnsCheckBox.setSelected(fixedColumnsSelected);
            
            boolean outputSummary = Boolean.valueOf(props.getProperty(Constants.SEARCH_OUTPUT_SUMMARY, String.valueOf(false)));
            this.summaryCheckBox.setSelected(outputSummary);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 検索条件を保存する。
     */
    private void saveProperties() {
        logger.info("saveProperties");
        try {
            Properties props = AdProperty.getProperties(Constants.REPORT_OUT_PROPETRY_NAME);

            // 対象日の選択
            props.setProperty(Constants.SEARCH_DATE_SELECTED, String.valueOf(this.dateCheckBox.isSelected()));

            // 対象日
            String propFromDate = "";
            LocalDate fromDate = this.fromDatePicker.getValue();
            if (Objects.nonNull(fromDate)) {
                propFromDate = DateTimeFormatter.ofPattern(DISP_DATE_FORMAT).format(fromDate);
            }
            props.setProperty(Constants.SEARCH_FROM_DATE, propFromDate);

            String propToDate = "";
            LocalDate toDate = this.toDatePicker.getValue();
            if (Objects.nonNull(toDate)) {
                propToDate = DateTimeFormatter.ofPattern(DISP_DATE_FORMAT).format(toDate);
            }
            props.setProperty(Constants.SEARCH_TO_DATE, propToDate);

            // カンバン名の選択
            props.setProperty(Constants.SEARCH_KANBAN_NAME_SELECTED, String.valueOf(this.kanbanCheckBox.isSelected()));

            // カンバン名
            String propKanbanName = this.kanbanTextField.getText();
            if (Objects.isNull(propKanbanName)) {
                propKanbanName = "";
            }
            props.setProperty(Constants.SEARCH_KANBAN_NAME, propKanbanName);

            // 製造番号の選択
            props.setProperty(Constants.SEARCH_PRODUCTION_NUMBER_SELECTED, String.valueOf(this.productionNumberCheckBox.isSelected()));

            // 製造番号
            String propProductionNumber = this.productionNumberTextField.getText();
            if (Objects.isNull(propProductionNumber)) {
                propProductionNumber = "";
            }
            props.setProperty(Constants.SEARCH_PRODUCTION_NUMBER, propProductionNumber);

            // TODO: 機種名を使用する場合、有効にする。
            // 機種名の選択
            props.setProperty(Constants.SEARCH_MODEL_NAME_SELECTED, String.valueOf(this.modelNameCheckBox.isSelected()));

            // 機種名
            String propModelName = this.modelNameTextField.getText();
            if (Objects.isNull(propModelName)) {
                propModelName = "";
            }
            props.setProperty(Constants.SEARCH_MODEL_NAME, propModelName);

            // 設備の選択
            props.setProperty(Constants.SEARCH_EQUIPMENT_SELECTED, String.valueOf(this.equipmentSelectPaneController.isSelected()));
            // 設備ID一覧
            props.setProperty(Constants.SEARCH_EQUIPMENT_IDS, equipmentSelectPaneController.getChoiceIdsString());
            // 設備名一覧
            props.setProperty(Constants.SEARCH_EQUIPMENT_NAME, equipmentSelectPaneController.getChoiceText());

            // 組織の選択
            props.setProperty(Constants.SEARCH_ORGANIZATION_SELECTED, String.valueOf(this.organizationSelectPaneController.isSelected()));
            // 組織ID一覧
            props.setProperty(Constants.SEARCH_ORGANIZATION_IDS, organizationSelectPaneController.getChoiceIdsString());
            // 組織名一覧
            props.setProperty(Constants.SEARCH_ORGANIZATION_NAME, organizationSelectPaneController.getChoiceText());

            // 工程順の選択
            props.setProperty(Constants.SEARCH_WORKFLOW_SELECTED, String.valueOf(this.workflowSelectPaneController.isSelected()));
            // 工程順ID一覧
            props.setProperty(Constants.SEARCH_WORKFLOW_IDS, workflowSelectPaneController.getChoiceIdsString());
            // 工程順名一覧
            props.setProperty(Constants.SEARCH_WORKFLOW_NAME, workflowSelectPaneController.getChoiceText());

            // ステータスの選択
            props.setProperty(Constants.SEARCH_STATUS_SELECTED, String.valueOf(this.statusCheckBox.isSelected()));

            // ステータス
            String propStatus = "";
            List<String> statuses = new ArrayList();
            ObservableList<KanbanStatusEnum> items = statusListView.getCheckModel().getCheckedItems();
            if (!items.isEmpty()) {
                items.stream().forEach(item -> {
                    statuses.add(item.name());
                });
                propStatus = String.join(",", statuses);
            }
            props.setProperty(Constants.SEARCH_STATUS, propStatus);

            // 固定列で出力する
            props.setProperty(Constants.SEARCH_FIXED_COLUMNS_SELECTED, String.valueOf(this.fixedColumnsCheckBox.isSelected()));

            // CSV出力時にサマリーを出力する
            props.setProperty(Constants.SEARCH_OUTPUT_SUMMARY, String.valueOf(this.summaryCheckBox.isSelected()));

            // 保存する。
            AdProperty.store(Constants.REPORT_OUT_PROPETRY_NAME);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
