/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component;

import adtekfuji.admanagerapp.kanbaneditplugin.common.*;
import adtekfuji.admanagerapp.kanbaneditplugin.controls.CheckStatusList;
import adtekfuji.admanagerapp.kanbaneditplugin.controls.CheckTextField;
import adtekfuji.admanagerapp.kanbaneditplugin.dialog.FileOpenDialog;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.DisplayData;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.FujiMesData;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.FujiMesData.TagType;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.FujiMesOut;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.ReplaceTagResult;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.ReportOutputCondition;
import adtekfuji.admanagerapp.kanbaneditplugin.enumerate.LedgerProcResultType;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.IniFileLinked;
import adtekfuji.clientservice.AccessHierarchyInfoFacade;
import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.TraceabilityFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.PathUtils;
import adtekfuji.utility.StringUtils;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualPropertyEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.assemblyparts.AssemblyPartsInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.ApprovalEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanReportInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.PlanChangeCondition;
import jp.adtekfuji.adFactory.entity.kanban.TraceabilityEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.LabelInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition;
import jp.adtekfuji.adFactory.entity.search.TraceabilitySearchCondition;
import jp.adtekfuji.adFactory.entity.view.ReportOutInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.ReportTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adFactory.utility.PropertyUtils;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import jp.adtekfuji.javafxcommon.dialog.MessageDialog;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogButtons;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogResult;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogType;
import jp.adtekfuji.javafxcommon.selectcompo.AccessAuthSettingEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.IndexedCheckModel;

/**
 * カンバン画面コントローラー
 *
 * @author e.mori
 */
@FxComponent(id = "KanbanListCompo", fxmlPath = "/fxml/compo/kanban_list_compo.fxml")
public class KanbanListCompoFxController implements Initializable, ComponentHandler {

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static final KanbanHierarchyInfoFacade kanbanHierarchyInfoFacade = new KanbanHierarchyInfoFacade();
    private static final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();

    private static final TraceabilityFacade traceabilityFacade = new TraceabilityFacade();

    private static final int RANGE_IDS = 20;
    private static final long RANGE = 20;
    private static final int KANBAN_RANGE = 20;// カンバン情報取得の最大カンバン数(この数毎にREST呼び出し)
    private static final int ACTUAL_KANBAN_RANGE = 10;// 工程実績情報取得の最大カンバン数(この数毎にREST呼び出し)
    private static final long ROOT_ID = 0;
    private static final String URI_SPLIT = "/";
    private static final String NEW_LINE = "\n";
    private static final String CHARSET = "UTF-8";

    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final KanbanEditPermanenceData kanbanEditPermanenceData = KanbanEditPermanenceData.getInstance();
    private static final Map<Long, TreeItem> treeItems = new HashMap<Long, TreeItem>();
    private final ObjectProperty<TreeItem<KanbanHierarchyInfoEntity>> selectedProperty = new SimpleObjectProperty<>();

    private SimpleDateFormat formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
    private static final String SEARCH_FILTER_START_DATE = "search_filter_start_date";
    private static final String SEARCH_FILTER_END_DATE = "search_filter_end_date";
    private Boolean isDispDialog = false;

    private static final String KANBAN_CREATE_COMPO = "KanbanCreateCompo";
    private static final String KANBAN_DETAIL_COMPO = "KanbanDetailCompo";
    private static final String KANBAN_CREATE_COMPO_ELS = "KanbanCreateCompoELS";

    private final Set<Object> blockUIs = new HashSet();

    private String defaultCreateKanbanCompo;// デフォルトのカンバン作成画面

    private boolean isTraceabilityDBEnabled;// トレーサビリティDB使用フラグ (品質トレーサビリティの保存先)
    
    private Long selectedHierarchyId;
    
    private List<Long> selectedKanbanList; // 移動後に選択されるカンバンのリストを保存
    
    /**
     * 「拡張フラグを使用するか」（true：使用する/false：使用しない）
     */
    private boolean useExtensionTag = false;

    /**
     * 部品トレースを使用するか(true:使用する, false:使用しない)
     */
    private boolean enablePartsTrace = false;

    /**
     * QRコードを使用するか(true:使用する, false:使用しない)
     */
    private boolean useQRCode = false;

    private int approveNum; // 承認者の人数設定
    private static final SimpleDateFormat APPROVE_DATE_FORMAT = new SimpleDateFormat("M/d");
    private final Set<Integer> approveFiltOrder = new HashSet();

    private final List<DisplayData> kanbanMasterList = new LinkedList<>();

    private final List<Path> outputFilePathList = new ArrayList<>();
    private final List<KanbanReportInfoEntity> reportInfos = new ArrayList<>();

    private boolean isWorkflowSingle; // 工程順の単一選択
    private List<String> checkTemplateList = new ArrayList<>(); // 出力対象の帳票テンプレートファイル名リスト

    private TableColumn kanbanNameColumn = new TableColumn(LocaleUtils.getString("key.KanbanName"));
    private TableColumn workflowNameColumn = new TableColumn(LocaleUtils.getString("key.WorkflowName"));
    private TableColumn modelNameColumn = new TableColumn(LocaleUtils.getString("key.ModelName"));
    private TableColumn productNoColumn = new TableColumn(LocaleUtils.getString("key.ProductionNumber"));
    private TableColumn statusColumn = new TableColumn(LocaleUtils.getString("key.Status"));

    /**
     * カンバン一覧におけるラベル一覧カラム
     */
    private TableColumn labelsColumn = new TableColumn(LocaleUtils.getString("key.Label"));
    private TableColumn startTimeColumn = new TableColumn(LocaleUtils.getString("key.WorkStartTime"));
    private TableColumn endTimeColumn = new TableColumn(LocaleUtils.getString("key.WorkEndTime"));
    private TableColumn updatePersonColumn = new TableColumn(LocaleUtils.getString("key.UpdatedBy")); // 更新者カラム
    private TableColumn updateDatetimeColumn = new TableColumn(LocaleUtils.getString("key.UpdatedDate")); // 更新日時カラム

    // 実績
    private final ActualResultInfoFacade actualFacade = new ActualResultInfoFacade();
    
    // FUJI-MES工程ID
    private static final String FUJIMES_ID = "工程ID";
    // FUJI-MES設定キー
    public static final String FUJIMES_TAG = "TAG";
    public static final String FUJIMES_UTAG = "UTAG";

    /**
     * ツリーノード開閉イベントリスナー
     */
    private final ChangeListener expandedListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            // ノードを開いたら子ノードの情報を再取得して表示する。
            if (Objects.nonNull(newValue) && newValue.equals(true)) {
                TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
                expand(treeItem, null);
            }
        }
    };

    @FXML
    private SplitPane kanbanPane;
    @FXML
    private TreeView<KanbanHierarchyInfoEntity> hierarchyTree;
    @FXML
    private Pane Progress;
    @FXML
    private Pane approveFilterPane;
    @FXML
    private Button approveButton;
    @FXML
    private PropertySaveTableView<DisplayData> kanbanList;
    @FXML
    private CheckTextField kanbanNameField;
    @FXML
    private CheckTextField productNoField;
    @FXML
    private CheckTextField modelNameField;
    @FXML
    private CheckTextField workflowNameField;
    @FXML
    private CheckTextField workNameField;
    @FXML
    private CheckStatusList statusList;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ToolBar hierarchyBtnArea;
    @FXML
    private Button authButton;
    @FXML
    private Button delTreeButton;
    @FXML
    private Button editTreeButton;
    @FXML
    private Button moveTreeButton;
    @FXML
    private Button createTreeButton;
    @FXML
    private Button deleteKanbanButton;
    @FXML
    private Button moveKanbanButton;
    @FXML
    private Button editKanbanButton;
    @FXML
    private Button createKanbanButton;
    @FXML
    private Button searchPartsIDButton;
    /** 検査データ出力ボタンコントロール */
    @FXML
    private Button repairButton;
    @FXML
    private Button checkDataOutButton;
    @FXML
    private Button ledgerOutButton;
    @FXML
    private Button collectivelyOutputButton;// まとめて帳票出力
    @FXML
    private Button changePlansButton;
    @FXML
    private Button changeStatusButton; // 2019/12/25 カンバン操作
    @FXML
    private Button reportListButton;
    @FXML
    private Button changeTracebilityButton;
    @FXML
    private ToolBar inspectionToolBar;// 検査データのツールバー
    @FXML
    private VBox searchPane;
    @FXML
    private SplitPane leftPane;

    /**
     * 初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
        kanbanNameColumn.setText(LocaleUtils.getString("key.KanbanName"));
        workflowNameColumn.setText(LocaleUtils.getString("key.WorkflowName"));
        modelNameColumn.setText(LocaleUtils.getString("key.ModelName"));
        productNoColumn.setText(LocaleUtils.getString("key.ProductionNumber"));
        statusColumn.setText(LocaleUtils.getString("key.Status"));
        labelsColumn.setText(LocaleUtils.getString("key.Label"));
        startTimeColumn.setText(LocaleUtils.getString("key.WorkStartTime"));
        endTimeColumn.setText(LocaleUtils.getString("key.WorkEndTime"));
        updatePersonColumn.setText(LocaleUtils.getString("key.UpdatedBy")); // 更新者カラム
        updateDatetimeColumn.setText(LocaleUtils.getString("key.UpdatedDate")); // 更新日時カラム

        kanbanList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        SplitPaneUtils.loadDividerPosition(kanbanPane, getClass().getSimpleName());

        // 部品トレースを使用するか
        this.enablePartsTrace = Boolean.parseBoolean(properties.getProperty(Constants.ENABLE_PARTS_TRACE, Constants.ENABLE_PARTS_TRACE_DEF));
        // QRコードを使用するか
        this.useQRCode = Boolean.parseBoolean(properties.getProperty(Constants.USE_QRCODE, Constants.USE_QRCODE_DEF));

        this.approveNum = Integer.parseInt(properties.getProperty(Constants.APPROVE_NUM, Constants.APPROVE_NUM_DEF)); // 承認者の人数設定を取得
        this.approveNum = this.approveNum <= 0 ? 0 // 0以下は0
                : this.approveNum >= 3 ? 3 : this.approveNum; // 3以上は3

        leftPane.widthProperty().addListener((obj, o, n) -> {
            searchPane.setPrefWidth((double)n - 8);
        });

        deleteKanbanButton.addEventHandler( MouseEvent.MOUSE_PRESSED , this::onDeleteKanban );
        // カンバン階層で選択されている階層IDを初期化
        this.selectedHierarchyId = Long.valueOf(0);

        //役割の権限によるボタン無効化.
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            this.delTreeButton.setDisable(true);
            this.editTreeButton.setDisable(true);
            this.moveTreeButton.setDisable(true);
            this.authButton.setDisable(true);
            this.createTreeButton.setDisable(true);
            this.createKanbanButton.setDisable(true);
        }

        this.editKanbanButton.setDisable(true);
        this.deleteKanbanButton.setDisable(true);
        this.moveKanbanButton.setDisable(true);
        this.collectivelyOutputButton.setDisable(true);
        this.repairButton.setDisable(true);
        this.checkDataOutButton.setDisable(true);
        this.ledgerOutButton.setDisable(true);
        this.changePlansButton.setDisable(true);
        this.changeStatusButton.setDisable(true);  // 2019/12/25 カンバン操作
        this.reportListButton.setDisable(true);
        this.changeTracebilityButton.setDisable(true);
        this.approveButton.setDisable(true);

        // トレーサビリティDBを使用しない場合、検査データのツールバーを非表示にする。
        this.isTraceabilityDBEnabled = Boolean.valueOf(AdProperty.getProperties().getProperty(Constants.PROP_TRACEABILITY_DB_ENABLED, Constants.TRACEABILITY_DB_ENABLED_DEF));
        if (!this.isTraceabilityDBEnabled) {
            this.inspectionToolBar.setVisible(false);
            this.inspectionToolBar.setManaged(false);
        } else {
            this.changeTracebilityButton.setVisible(false);
            this.changeTracebilityButton.setManaged(false);
        }

        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.RIGHT_ACCESS)) {
            hierarchyBtnArea.getItems().remove(authButton);
        }

        // 承認権限ないか承認カラムが0以下の場合、承認ボタンを非表示にする
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.APPROVAL_KANBAN)
                || this.approveNum <= 0) {
            this.approveButton.setVisible(false);
            this.approveButton.setManaged(false);
        }
        
        // カンバン作成権限がないかパーツID編集を使用しない場合、パーツID検索ボタンを非表示にする
        boolean editingPartsID = Boolean.valueOf(AdProperty.getProperties().getProperty(Constants.PROP_EDITING_PARTS_ID, Constants.EDITING_PARTS_ID_DEF));
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)
                || !editingPartsID) {
            this.searchPartsIDButton.setVisible(false);
            this.searchPartsIDButton.setManaged(false);
        }
        
        // FUJI-MES連携オプションライセンスが未導入の場合、検査データ出力ボタンを非表示にする
        if (!isEnableFujiMes()) {
            this.repairButton.setVisible(false);
            this.repairButton.setManaged(false);
            this.checkDataOutButton.setVisible(false);
            this.checkDataOutButton.setManaged(false);

        } else {
            if (isEnableRepairButton()) {
                this.checkDataOutButton.setText("新規検査データ出力");
            } else {
                this.repairButton.setVisible(false);
                this.repairButton.setManaged(false);
            }
        }


        Progress.setVisible(false);

        this.loadProperties();

        // カンバン一覧の列幅を設定する。
        kanbanNameColumn.setPrefWidth(180.0);
        workflowNameColumn.setPrefWidth(180.0);
        modelNameColumn.setPrefWidth(120.0);
        productNoColumn.setPrefWidth(120.0);
        statusColumn.setPrefWidth(100.0);
        labelsColumn.setPrefWidth(150.0);
        startTimeColumn.setPrefWidth(150.0);
        endTimeColumn.setPrefWidth(150.0);
        updatePersonColumn.setPrefWidth(100.0); // 更新者
        updateDatetimeColumn.setPrefWidth(150.0); // 更新日時

        // カンバン一覧に列をを追加する。
        this.kanbanList.getColumns().add(kanbanNameColumn);
        this.kanbanList.getColumns().add(workflowNameColumn);
        this.kanbanList.getColumns().add(modelNameColumn);
        this.kanbanList.getColumns().add(productNoColumn);
        this.kanbanList.getColumns().add(statusColumn);
        this.kanbanList.getColumns().add(labelsColumn);

        // 承認列追加
        for (int i = 1; i <= approveNum; i++) {
            final int order = i;
            TableColumn<DisplayData, String> approveColumns = new TableColumn(LocaleUtils.getString("key.Approve") + i);
            approveColumns.setPrefWidth(180.0);
            this.kanbanList.getColumns().add(approveColumns);
            approveColumns.setCellValueFactory((CellDataFeatures<DisplayData, String> item) -> item.getValue().approveInfoProperty(order));
        }

        this.kanbanList.getColumns().add(startTimeColumn);
        this.kanbanList.getColumns().add(endTimeColumn);
        this.kanbanList.getColumns().add(updatePersonColumn); // 更新者
        this.kanbanList.getColumns().add(updateDatetimeColumn); // 更新日時

        //カンバン一覧の列設定を自動保存
        kanbanList.init("kanbanList");

        if (Objects.nonNull(properties.getProperty(SEARCH_FILTER_START_DATE)) && !"".equals(properties.getProperty(SEARCH_FILTER_START_DATE))) {
            Calendar calendarStartDate = Calendar.getInstance();
            calendarStartDate.setTime(new Date());
            calendarStartDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(properties.getProperty(SEARCH_FILTER_START_DATE)));
            startDatePicker.setValue(LocalDate.of(calendarStartDate.get(Calendar.YEAR), calendarStartDate.get(Calendar.MONTH) + 1, calendarStartDate.get(Calendar.DAY_OF_MONTH)));
        }

        if (Objects.nonNull(properties.getProperty(SEARCH_FILTER_END_DATE)) && !"".equals(properties.getProperty(SEARCH_FILTER_END_DATE))) {
            Calendar calendarEndDate = Calendar.getInstance();
            calendarEndDate.setTime(new Date());
            calendarEndDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(properties.getProperty(SEARCH_FILTER_END_DATE)));
            endDatePicker.setValue(LocalDate.of(calendarEndDate.get(Calendar.YEAR), calendarEndDate.get(Calendar.MONTH) + 1, calendarEndDate.get(Calendar.DAY_OF_MONTH)));
        }

        //エンティティメンバーとバインド
        kanbanNameColumn.setCellValueFactory(new PropertyValueFactory<>("kanbanName"));
        workflowNameColumn.setCellValueFactory(new PropertyValueFactory<>("workflowName"));
        modelNameColumn.setCellValueFactory(new PropertyValueFactory<>("modelName"));
        productNoColumn.setCellValueFactory(new PropertyValueFactory<>("productNo"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        labelsColumn.setCellValueFactory(new PropertyValueFactory<>("labelIds"));
        labelsColumn.setCellFactory(column -> createKanbanTableLabelCell());
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        updatePersonColumn.setCellValueFactory(new PropertyValueFactory<>("updatePerson")); // 更新者
        updateDatetimeColumn.setCellValueFactory(new PropertyValueFactory<>("updateDatetime")); // 更新日時

        kanbanList.setRowFactory(tv -> new TableRow<DisplayData>() {

            /**
             * セルの内容を更新する
             *
             * @param item セルの新しいアイテム
             * @param empty このセルに割り当てられるデータが空かどうか（空:true、空でない:false）
             */
            @Override
            protected void updateItem(DisplayData item, boolean empty) {
                super.updateItem(item, empty);
                if (Objects.nonNull(item)) {
                    // カンバンの詳細表示ポップアップ   // TODO: この処理があると、リストのスクロール時にエラーが発生する。
                    buildTooltip(this, item);
                    this.setContextMenu(createContextMenu(item));
                } else {
                    if (Objects.nonNull(this.getTooltip())) {
                        this.setTooltip(null);
                    }
                    if (Objects.nonNull(this.getContextMenu())) {
                        this.setContextMenu(null);
                    }
                }
            }
        });

        // 未承認フィルター追加
        for (int i = 1; i <= approveNum; i++) {
            final int order = i;
            CheckBox approveCheck = new CheckBox(LocaleUtils.getString("key.UnapprovalStatus") + i);
            approveCheck.setOnAction(((ActionEvent) -> {
                if (approveCheck.isSelected()) {
                    approveFiltOrder.add(order);
                } else {
                    approveFiltOrder.remove(order);
                }

                // 未承認フィルターの適用
                ObservableList<DisplayData> tableData = FXCollections.observableArrayList();
                tableData.addAll(createFilteredList(this.kanbanMasterList));

                ArrayList<TableColumn<DisplayData, ?>> sortOrder = new ArrayList<>(this.kanbanList.getSortOrder());
                this.kanbanList.getSortOrder().clear();
                clearKanbanList();
                kanbanList.setItems(tableData);
                this.kanbanList.getSortOrder().addAll(sortOrder);
            }));
            this.approveFilterPane.getChildren().add(approveCheck);
        }

        //階層ツリー選択時処理
        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<KanbanHierarchyInfoEntity>> observable, TreeItem<KanbanHierarchyInfoEntity> oldValue, TreeItem<KanbanHierarchyInfoEntity> newValue) -> {
            if (Objects.nonNull(newValue)) {
                kanbanEditPermanenceData.setSelectedWorkHierarchy(newValue);
                // 別スレッドでカンバンを検索して、カンバンリストを更新する。
                searchKanbanDataTask();
            } else {
                // リストクリア
                kanbanEditPermanenceData.setSelectedWorkHierarchy(null);
                clearKanbanList();
            }
        });

        // カンバン選択時の処理
        //      ※.複数件選択状態で、alt + クリックで選択解除や再選択した時はイベント発生しない
        this.kanbanList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends DisplayData> observable, DisplayData oldValue, DisplayData newValue) -> {
            if (Objects.isNull(kanbanList.getSelectionModel().getSelectedItems())
                    || kanbanList.getSelectionModel().getSelectedItems().size() < 1) {
                // 未選択
                this.editKanbanButton.setDisable(true);
                this.deleteKanbanButton.setDisable(true);
                this.moveKanbanButton.setDisable(true);
                this.collectivelyOutputButton.setDisable(true);
                this.repairButton.setDisable(true);
                this.checkDataOutButton.setDisable(true);
                this.ledgerOutButton.setDisable(true);
                this.changePlansButton.setDisable(true);
                this.changeStatusButton.setDisable(true);  // 2019/12/25 カンバン操作
                this.reportListButton.setDisable(true);
                this.changeTracebilityButton.setDisable(true);
                this.approveButton.setDisable(true);
            }else{
                setStateKanbanButtons();
            }
        });

        //カンバンクリック
        this.kanbanList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (Objects.nonNull(event.getSource()) && event.getClickCount() == 2) {
                    this.onEditKanban(new ActionEvent());
                } else {
                    // カンバン操作ボタンの有効状態を設定する。
                    setStateKanbanButtons();
                }
            }
        });

        if (KANBAN_CREATE_COMPO_ELS.equals(this.properties.getProperty(Config.COMPO_CREATE_KANBAN))) {
            this.defaultCreateKanbanCompo = KANBAN_CREATE_COMPO_ELS;
        } else {
            // スケジュールオプションが有効な場合、カンバン新規作成ボタンの右クリックメニューを作成する。
            if (ClientServiceProperty.isLicensed(LicenseOptionType.Scheduling.getName())) {
                this.defaultCreateKanbanCompo = KANBAN_DETAIL_COMPO;
                this.createKanbanCompoContextMenu();
            } else {
                this.defaultCreateKanbanCompo = KANBAN_CREATE_COMPO;
            }
        }

        hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new KanbanHierarchyTreeCell());

        // 階層ツリー表示
        this.updateTree(false);
    }

    /**
     * 画面破棄時に内容に変更がないか調べて変更があるなら保存する
     *
     * @return 保存に成功したとき、または変更が存在しなかった場合true<br>ダイアログでキャンセルが押された場合false
     */
    @Override
    public boolean destoryComponent() {
        this.saveProperties();

        SplitPaneUtils.saveDividerPosition(kanbanPane, getClass().getSimpleName());

        return true;
    }

    /**
     * カンバン新規作成ボタンの右クリックメニューを作成する。
     */
    private void createKanbanCompoContextMenu() {
        Map<String, String> compoMap = new LinkedHashMap();
        compoMap.put(LocaleUtils.getString("key.KanbanCreateCompo"), KANBAN_CREATE_COMPO);
        compoMap.put(LocaleUtils.getString("key.KanbanDetailCompo"), KANBAN_DETAIL_COMPO);

        String selectedCompo = this.properties.getProperty(Config.COMPO_CREATE_KANBAN, this.defaultCreateKanbanCompo);

        ContextMenu menu = new ContextMenu();
        final ToggleGroup menuGroup = new ToggleGroup();

        for (Map.Entry<String, String> compo : compoMap.entrySet()) {
            RadioMenuItem menuItem = new RadioMenuItem(compo.getKey());
            menuItem.setUserData(compo.getValue());
            menuItem.setToggleGroup(menuGroup);

            if (compo.getValue().equals(selectedCompo)) {
                menuItem.setSelected(true);
            } else {
                menuItem.setSelected(false);
            }

            menu.getItems().add(menuItem);

            // メニューアイテムの選択イベント
            menuItem.setOnAction((ActionEvent event) -> {
                if (event.getSource() instanceof RadioMenuItem) {
                    RadioMenuItem eventMenuItem = (RadioMenuItem) event.getSource();
                    String compoName = (String) eventMenuItem.getUserData();

                    // カンバンの新規作成画面を表示する。
                    dispCompoCreateKanban(compoName);
                }
            });
        }

        this.createKanbanButton.setContextMenu(menu);
    }

    /**
     * カンバンテーブルのラベル列セルを生成する。
     *
     * @return カンバンテーブルのラベル列セル
     */
    private TableCell createKanbanTableLabelCell() {
        return new TableCell() {

            {
                this.setMinHeight(Region.USE_PREF_SIZE);
                this.setPrefHeight(Constants.KANBAN_LABELS_CELL_HEIGHT);
                this.setPadding(Constants.KANBAN_LABEL_CELL_PADDING);
            }

            /**
             * セルの内容を更新する
             *
             * @param item セルの新しいアイテム
             * @param empty このセルに割り当てられるデータが空かどうか（空:true、空でない:false）
             */
            @Override
            public void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                // テキストには常にnullを指定する
                this.setText(null);

                // データがなければ何も表示しない
                if (empty) {
                    this.setGraphic(null);
                    return;
                }

                // 項目がカンバンラベルID一覧かつ件数が1件以上でなければ何も表示しない
                final List<Long> kanbanLabelIds = (List<Long>) item;
                if (Objects.isNull(kanbanLabelIds) || kanbanLabelIds.isEmpty()) {
                    this.setGraphic(null);
                    return;
                }

                // ラベルマスタとカンバンラベルID一覧をもとにカンバンラベル一覧を作成する
                // 項目件数が1件以上でなければ何も表示しない
                final List<LabelInfoEntity> kanbanLabels = kanbanLabelIds.stream()
                        .map((labelId) -> CacheUtils.getCacheLabel(labelId))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                if (kanbanLabels.isEmpty()) {
                    this.setGraphic(null);
                    return;
                }

                // セルに表示するカンバンラベル一覧を作成
                // 色のフォーマットが不正等、カンバンラベル一覧のデータに問題があれば何も表示しない
                final List<Label> kanbanLabelLabels;
                try {
                    kanbanLabelLabels = kanbanLabels.stream()
                            .sorted(Comparator.comparingLong(LabelInfoEntity::getLabelPriority))
                            .map(entity -> {
                                Label kanbanLabelLabel = new Label(entity.getLabelName());
                                kanbanLabelLabel.setPadding(Constants.KANBAN_LABEL_PADDING_IN_CELL);
                                kanbanLabelLabel.setTextFill(Paint.valueOf(entity.getFontColor()));
                                kanbanLabelLabel.setBackground(new Background(new BackgroundFill(
                                        Paint.valueOf(entity.getBackColor()),
                                        Constants.KANBAN_LABEL_CORNER_RADIUS_IN_CELL,
                                        Insets.EMPTY)));
                                kanbanLabelLabel.setBorder(new Border(new BorderStroke(
                                        Color.WHITE,
                                        BorderStrokeStyle.NONE,
                                        Constants.KANBAN_LABEL_CORNER_RADIUS_IN_CELL,
                                        BorderWidths.EMPTY)));
                                return kanbanLabelLabel;
                            })
                            .collect(Collectors.toList());
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    this.setGraphic(null);
                    return;
                }

                // 省略表示を作成する
                Label ellipsisLabel = new Label(Constants.KANBAN_LABELS_ELLIPSIS_IN_CELL);
                ellipsisLabel.setStyle(Constants.KANBAN_LABELS_ELLIPSIS_FONT_STYLE_IN_CELL);

                // セルに表示するグラフィックを生成し、セルに設定する
                FlowPane graphicPane = new FlowPane();
                graphicPane.setMinWidth(0.0);
                graphicPane.setMinHeight(Region.USE_PREF_SIZE);
                graphicPane.setPrefHeight(Constants.KANBAN_LABELS_CELL_HEIGHT
                        - (Constants.KANBAN_LABEL_CELL_PADDING.getTop() + Constants.KANBAN_LABEL_CELL_PADDING.getBottom()));
                graphicPane.setPadding(Insets.EMPTY);
                graphicPane.setHgap(Constants.KANBAN_LABELS_HGAP_IN_CELL);
                graphicPane.setVgap(Constants.KANBAN_LABELS_VGAP_IN_CELL);
                graphicPane.getChildren().addAll(kanbanLabelLabels);
                graphicPane.getChildren().add(ellipsisLabel);
                this.setGraphic(graphicPane);

                // 省略表示を更新する
                // 省略表示を更新するにはセルとラベルの境界を知る必要があるが、セル生成時点では境界は空なので、一度レイアウトして各境界を更新する
                this.layout();
                final Consumer<Bounds> updateEllipsis = cellBounds -> {
                    // 省略表示はいずれかのカンバンラベルがセル境界外にならなければ非表示とする
                    ellipsisLabel.setVisible(false);

                    // セル境界外のカンバンラベルは、それ以降のカンバンラベルを含め非表示にし、レイアウトもされないようにする
                    final Bounds cellBoundsInScene = this.localToScene(cellBounds);
                    Iterator<Label> kanbanLabelLabelsIterator = kanbanLabelLabels.iterator();
                    while (kanbanLabelLabelsIterator.hasNext()) {
                        Label nextLabel = kanbanLabelLabelsIterator.next();

                        final Bounds labelBoundsInScene = nextLabel.localToScene(nextLabel.getLayoutBounds());
                        if (cellBoundsInScene.contains(labelBoundsInScene)) {
                            continue;
                        }

                        nextLabel.setVisible(false);
                        nextLabel.setManaged(false);
                        kanbanLabelLabelsIterator.forEachRemaining(outOfBoundsLabel -> {
                            outOfBoundsLabel.setVisible(false);
                            outOfBoundsLabel.setManaged(false);
                        });
                        ellipsisLabel.setVisible(true);
                    }

                    // 省略表示をしない場合は、これ以降の処理は不要
                    if (!ellipsisLabel.isVisible()) {
                        return;
                    }

                    // 省略表示の境界を知るため、レイアウトを更新する
                    this.layout();

                    // 省略表示がセル境界外の場合は、省略表示がセル境界内に含まれるよう、現在表示されているラベルの内一番最後のカンバンラベルも非表示にしてレイアウトされないようにする
                    // ※表示されているラベルがなければ、省略表示のみ表示・レイアウトされる
                    final Bounds ellipsisBoundsInScene = ellipsisLabel.localToScene(ellipsisLabel.getLayoutBounds());
                    if (!cellBoundsInScene.contains(ellipsisBoundsInScene)) {
                        Optional<Label> lastVisibleLabel = kanbanLabelLabels.stream()
                                .filter(Node::isVisible)
                                .reduce((first, second) -> second);
                        lastVisibleLabel.ifPresent(label -> {
                            label.setVisible(false);
                            label.setManaged(false);
                        });
                    }
                };
                updateEllipsis.accept(this.getLayoutBounds());

                // レイアウトが変わるたびに省略表示を更新するようにする
                this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
                    // 幅だけが0以上の境界が指定されることがあるが、これは無視する
                    if (newValue.getWidth() <= 0.0 || newValue.getHeight() <= 0.0) {
                        return;
                    }

                    // 全てのラベルが表示されている前提でラベルの境界を計算するため、全てのラベルが表示かつレイアウトされるよう変更
                    kanbanLabelLabels.forEach(label -> {
                        label.setVisible(true);
                        label.setManaged(true);
                    });

                    // レイアウトしてセルとラベルの境界を更新する
                    this.layout();

                    // 省略表示を更新する
                    updateEllipsis.accept(newValue);
                });
            }
        };
    }

    /**
     * コンテキストメニューを生成する
     *
     * @param displayData カンバンリスト表示用データ
     * @return コンテキストメニュー
     */
    private ContextMenu createContextMenu(DisplayData displayData) {
        ContextMenu menu = new ContextMenu ();
        menu.getItems().add(createKanbanLabelMenu(displayData));
        if (this.useQRCode) {
            menu.getItems().add(createQRCodePrintMenu());
        }
        return menu;
    }

    /**
     * カンバンラベルメニューを生成する
     *
     * @param displayData カンバンリスト表示用データ
     * @return カンバンラベルメニュー
     */
    private Menu createKanbanLabelMenu(DisplayData displayData) {
        Menu labelMenu = new Menu();
       
        Label labelMenuNameLabel = new Label(LocaleUtils.getString("key.Label"));
        labelMenuNameLabel.setMinWidth(Constants.KANBAN_LABEL_MENU_NAME_MIN_WIDTH);
        labelMenu.setGraphic(labelMenuNameLabel);

        Stream<LabelInfoEntity> labelMenuItemsStream = CacheUtils.getCacheLabels().stream()
                .sorted(Comparator.comparingLong(LabelInfoEntity::getLabelPriority));
        final List<MenuItem> labelMenuItems = labelMenuItemsStream.map((masterLabel) -> {
            final CheckMenuItem menuItem = new CheckMenuItem(masterLabel.getLabelName());

            // マスタとIDが合致するラベルのみチェックを付ける
            if (displayData.getLabelIds().contains(masterLabel.getLabelId())) {
                menuItem.setSelected(true);
            }
            menuItem.setOnAction((event) -> {
                try {
                    // カンバンのラベル情報を更新する
                    // （チェックされていない場合は追加、チェックされている場合は削除を行う）
                    List<Long> labelIds = new ArrayList<>(displayData.getLabelIds());
                    if (menuItem.isSelected()) {
                        labelIds.add(masterLabel.getLabelId());
                    } else {
                        labelIds.remove(masterLabel.getLabelId());
                    }
                    final KanbanInfoEntity kanban = displayData.getEntity();
                    final ResponseEntity response = kanbanInfoFacade.updateLabel(kanban.getKanbanId(), labelIds, kanban.getVerInfo(), loginUserInfoEntity.getId());

                    // 成功した場合は、該当するカンバンのみ更新する。
                    // 失敗した場合は、ラベルマスタとカンバン情報一覧との整合性が取れていないものと判断し、画面全体を更新する。
                    if (Objects.nonNull(response) && ResponseAnalyzer.getAnalyzeResult(response)) {
                        // カンバンラベル情報更新時に指定する排他用バーションを更新するため、該当するカンバンの情報を更新する
                        KanbanInfoEntity newEntity = kanbanInfoFacade.find(displayData.getId());

                        if (Objects.nonNull(newEntity.getFkUpdatePersonId())) {
                            OrganizationInfoEntity person = CacheUtils.getCacheOrganization(newEntity.getFkUpdatePersonId());
                            if (Objects.nonNull(person)) {
                                newEntity.setUpdatePerson(person.getOrganizationName());
                            }
                        }

                        DisplayData newDisplayData = new DisplayData(newEntity, false);
                        this.kanbanMasterList.set(this.kanbanMasterList.indexOf(displayData), newDisplayData);
                        this.kanbanList.getItems().set(this.kanbanList.getItems().indexOf(displayData), newDisplayData);
                        this.kanbanList.refresh();
                    } else {
                        this.logger.error("Failed kanbanInfoFacade.updateLabel(id, labelIds, verInfo): id={}, labelIds={}, verInfo={}, result={}",
                                kanban.getKanbanId(), labelIds, kanban.getVerInfo(), response);
                        CacheUtils.removeCacheData(LabelInfoEntity.class);
                        CacheUtils.createCacheLabel(true);
                        this.searchKanbanDataTask();
                    }
                } catch (Exception ex) {
                    this.logger.fatal(ex, ex);
                }
            });

            return menuItem;
        }).collect(Collectors.toList());
        labelMenu.getItems().addAll(labelMenuItems);

        return labelMenu;
    }

    /**
     * QRコード印刷メニューを生成する
     *
     * @return QRコード印刷メニュー
     */
    private MenuItem createQRCodePrintMenu() {
        final MenuItem QRCodePrint = new MenuItem();
        Label QRCodeMenuNameLabel = new Label(LocaleUtils.getString("key.QRCodePrint"));
        QRCodeMenuNameLabel.setMinWidth(Constants.KANBAN_LABEL_MENU_NAME_MIN_WIDTH);
        QRCodePrint.setGraphic(QRCodeMenuNameLabel);
        QRCodePrint.setOnAction(event -> {
            this.onQRCodePrintMenu(event);
        });
        return QRCodePrint;
    }

    /**
     * QRコード印刷メニューのアクション
     *
     * @param event
     */
    private void onQRCodePrintMenu(ActionEvent event) {
        logger.info("onChangeTracebilityButton start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);
            DisplayData data = kanbanList.getSelectionModel().getSelectedItem();
            if (Objects.isNull(data)) {
                return;
            }
            KanbanInfoEntity kanban = getKanban(data.getId());

            // QRコード印刷ダイアログを表示する。
            sc.showDialog(LocaleUtils.getString("key.QRCodePrint"), "QRCodePrintDialog", kanban, sc.getStage(), false);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(obj, false);
        }
    }

    /**
     * (カンバン階層ツリー) キー押下のアクション
     *
     * @param ke
     */
    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            this.updateTree(true);
        }
    }

    /**
     * (カンバン階層ツリー) 編集ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onTreeEdit(ActionEvent event) {
        try {
            TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && !item.getValue().getKanbanHierarchyId().equals(ROOT_ID)) {
                String oldName = item.getValue().getHierarchyName();
                boolean oldFlag = item.getValue().getPartitionFlag();

                KanbanHierarchyInfoEntity newValue = new KanbanHierarchyInfoEntity();
                newValue.setHierarchyName(oldName);
                newValue.setPartitionFlag(oldFlag);
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Edit"), "KanbanHierarchyEditDialogCompo", newValue);
                if (ret != ButtonType.OK) {
                    return;
                }
                if (newValue.getHierarchyName().isEmpty()) {
                    String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                } else if (!oldName.equals(newValue.getHierarchyName()) || (oldFlag != newValue.getPartitionFlag())) {
                    item.getValue().setHierarchyName(newValue.getHierarchyName());
                    item.getValue().setPartitionFlag(newValue.getPartitionFlag());

                    ResponseEntity res = kanbanHierarchyInfoFacade.update(item.getValue());

                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {

                        KanbanHierarchyInfoEntity value = item.getValue();
                        item.setValue(null);
                        item.setValue(value);

                        this.selectedTreeItem(item, null);

                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanban"),
                                String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.KanbanHierarch")));
                        // データを戻す
                        item.getValue().setHierarchyName(oldName);
                        item.getValue().setPartitionFlag(oldFlag);
                    } else {
                        // データを戻す
                        item.getValue().setHierarchyName(oldName);
                        item.getValue().setPartitionFlag(oldFlag);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

    }

    /**
     * (カンバン階層ツリー) 移動ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onTreeMove(ActionEvent event) {
        try {
            blockUI(true);
            TreeItem<KanbanHierarchyInfoEntity> selectedItem = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedItem)) {
                return;
            } else if (selectedItem.getValue().getKanbanHierarchyId().equals(ROOT_ID)) {
                return;
            }

            KanbanHierarchyInfoEntity selected = selectedItem.getValue();
            hierarchyTree.setVisible(false);
            kanbanList.setVisible(false);
            isDispDialog = true;
            // カンバン階層で選択されている階層IDを保持
            this.selectedHierarchyId = selected.getKanbanHierarchyId();

            TreeItem<KanbanHierarchyInfoEntity> parentTreeItem = selectedItem.getParent();
            //移動先として自分を表示させないように一時削除
            int idx = parentTreeItem.getChildren().indexOf(selectedItem);
            parentTreeItem.getChildren().remove(selectedItem);

            // ダイアログに表示するデータを設定
            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(this.hierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"), selectedItem);
            treeDialogEntity.setIsUseHierarchy(Boolean.TRUE);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "KanbanHierarchyTreeCompo", treeDialogEntity);

            TreeItem<KanbanHierarchyInfoEntity> hierarchy = (TreeItem<KanbanHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            this.selectedHierarchyId = Long.valueOf(0);
            if (ret.equals(ButtonType.OK) && Objects.nonNull(hierarchy)) {
                selected.setParentId(hierarchy.getValue().getKanbanHierarchyId());

                // カンバン階層を更新
                ResponseEntity res = kanbanHierarchyInfoFacade.update(selected);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    // ツリー更新し、移動したカンバン階層を選択する
                    if (!hierarchy.isExpanded()) {
                        hierarchy.setExpanded(true);
                    }
                    this.createRoot(selected.getKanbanHierarchyId());
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    // サーバー処理エラーの場合、エラー表示して一時削除したデータを元に戻す
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Error"),
                            String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.KanbanHierarch")));
                    parentTreeItem.getChildren().add(idx, selectedItem);
                } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)) {
                    // 移動不可能な階層だった場合、警告表示して階層ツリーを再取得して表示しなおす
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.unmovableHierarchy"));
                    this.hierarchyTree.getRoot().setExpanded(false);
                    this.hierarchyTree.getRoot().getChildren().clear();
                    this.createRoot(null);
                } else {
                    // 一時削除したデータを元に戻す
                    parentTreeItem.getChildren().add(idx, selectedItem);
                }
            } else {
                // 一時削除したデータを元に戻す
                parentTreeItem.getChildren().add(idx, selectedItem);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            hierarchyTree.setVisible(true);
            kanbanList.setVisible(true);
            isDispDialog = false;
            blockUI(false);
        }
    }

    /**
     * (カンバン階層ツリー) 権限ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onAuth(ActionEvent event) {
        try {
            TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && !item.getValue().getKanbanHierarchyId().equals(ROOT_ID)) {
                //ダイアログに表示させるデータを設定
                AccessHierarchyTypeEnum type = AccessHierarchyTypeEnum.KanbanHierarchy;
                long id = item.getValue().getKanbanHierarchyId();
                AccessHierarchyInfoFacade accessHierarchyInfoFacade = new AccessHierarchyInfoFacade();
                long count = accessHierarchyInfoFacade.getCount(type, id);
                long range = 100;
                List<OrganizationInfoEntity> deleteList = new ArrayList();
                for (long from = 0; from <= count; from += range) {
                    List<OrganizationInfoEntity> entities = accessHierarchyInfoFacade.getRange(type, id, from, from + range - 1);
                    deleteList.addAll(entities);
                }
                AccessAuthSettingEntity accessAuthSettingEntity
                        = new AccessAuthSettingEntity(item.getValue().getHierarchyName(), deleteList);
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.EditedAuth"), "AccessAuthSettingCompo", accessAuthSettingEntity);
                if (ret.equals(ButtonType.OK)) {
                    List<OrganizationInfoEntity> registList = accessAuthSettingEntity.getAuthOrganizations();
                    for (int i = 0; i < registList.size(); i++) {
                        OrganizationInfoEntity o = registList.get(i);
                        if (deleteList.contains(o)) {
                            deleteList.remove(o);
                            registList.remove(o);
                            i--;
                        }
                    }
                    if (!deleteList.isEmpty()) {
                        accessHierarchyInfoFacade.delete(type, id, deleteList);
                    }
                    if (!registList.isEmpty()) {
                        accessHierarchyInfoFacade.regist(type, id, registList);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * (カンバン階層ツリー) 削除ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onTreeDelete(ActionEvent event) {
        try {
            TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && !item.getValue().getKanbanHierarchyId().equals(ROOT_ID)) {
                TreeItem<KanbanHierarchyInfoEntity> parentItem = item.getParent();

                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getValue().getHierarchyName());
                if (ret.equals(ButtonType.OK)) {
                    ResponseEntity res = kanbanHierarchyInfoFacade.delete(item.getValue());

                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // ツリー更新し、親階層を選択する
                        createRoot(parentItem.getValue().getKanbanHierarchyId());
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanban"),
                                String.format(LocaleUtils.getString("key.FailedToDelete"), LocaleUtils.getString("key.KanbanHierarch")));
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * (カンバン階層ツリー) 新規作成ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onTreeCreate(ActionEvent event) {
        try {
            TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item)) {
                // カンバン階層編集ダイアログを表示する。
                KanbanHierarchyInfoEntity hierarchy = new KanbanHierarchyInfoEntity();
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.NewCreate"), "KanbanHierarchyEditDialogCompo", hierarchy);
                if (ret != ButtonType.OK) {
                    return;
                }

                if (hierarchy.getHierarchyName().isEmpty()) {
                    String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                } else {
                    hierarchy.setParentId(item.getValue().getKanbanHierarchyId());

                    ResponseEntity res = kanbanHierarchyInfoFacade.regist(hierarchy);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // ツリー更新し、新規作成したカンバン階層を選択する
                        if (!item.isExpanded()) {
                            item.setExpanded(true);
                        }
                        this.createRoot(getUriToHierarcyId(res.getUri()));
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanban"),
                                String.format(LocaleUtils.getString("key.FailedToCreate"), LocaleUtils.getString("key.KanbanHierarch")));
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 編集ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onEditKanban(ActionEvent event) {
        // 休日情報がキャッシュされていない場合は読み込む。
        CacheUtils.createCacheHoliday(true);

        DisplayData data = kanbanList.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(data)) {
            KanbanInfoEntity entity = new KanbanInfoEntity(data.getId(), null, data.getKanbanName(), null);
            SelectedKanbanAndHierarchy selected = new SelectedKanbanAndHierarchy(entity,
                    hierarchyTree.selectionModelProperty().getName());
            KanbanInfoEntity kanban = getKanban(data.getId());
            kanbanEditPermanenceData.setSelectedKanban(kanban.getKanbanId());
            // 生産タイプが「2」(ロット生産カンバン)かどうかチェックする
            if (kanban.getProductionType() == 2) {
                // ロット生産カンバン
                sc.setComponent("ContentNaviPane", "KanbanCreateCompoELS", kanban);
            } else {
                // その他のカンバン
                sc.setComponent("ContentNaviPane", "KanbanDetailCompo", selected);
            }
        }
    }

    /**
     * 移動ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onMoveKanban(ActionEvent event) {
        boolean isCancel = false;
        try {
            blockUI(true);

            List<DisplayData> selectedKanbans = kanbanList.getSelectionModel().getSelectedItems();
            TreeItem<KanbanHierarchyInfoEntity> selectedHierarchy = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedKanbans) || Objects.isNull(selectedHierarchy)) {
                isCancel = true;
                return;
            }
            
            hierarchyTree.setVisible(false);
            kanbanList.setVisible(false);
            isDispDialog = true;

            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(hierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"));
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "KanbanHierarchyTreeCompo", treeDialogEntity);

            TreeItem<KanbanHierarchyInfoEntity> hierarchy = (TreeItem<KanbanHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (ret.equals(ButtonType.OK) && Objects.nonNull(hierarchy)) {

                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            for( DisplayData selectedKanban: selectedKanbans) {
                                KanbanInfoEntity item = selectedKanban.getEntity();
                                KanbanInfoEntity kanban = kanbanInfoFacade.find(item.getKanbanId());
                                kanban.setParentId(hierarchy.getValue().getKanbanHierarchyId());
                                kanban.setFkUpdatePersonId(loginUserInfoEntity.getId());
                                kanban.setUpdateDatetime(new Date());

                                ResponseEntity res = kanbanInfoFacade.update(kanban);
                            }
                            hierarchyTree.getSelectionModel().select(hierarchy);
                            selectedKanbanList = selectedKanbans.stream().map(o -> o.getId()).collect(Collectors.toList());
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                        }
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        hierarchyTree.setVisible(true);
                        kanbanList.setVisible(true);
                        isDispDialog = false;
                        blockUI(false);
                    }
                };
                new Thread(task).start();
            } else {
                isCancel = true;
                selectedKanbanList = null; 
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            hierarchyTree.setVisible(true);
            kanbanList.setVisible(true);
            isDispDialog = false;
            blockUI(false);
        } finally {
            if (isCancel) {
                hierarchyTree.setVisible(true);
                kanbanList.setVisible(true);
                isDispDialog = false;
                blockUI(false);
            }
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onListCreate(ActionEvent event) {
        TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
        if (!((Objects.isNull(item) || (item.getValue().getKanbanHierarchyId() == ROOT_ID)))) {
            KanbanInfoEntity entity = new KanbanInfoEntity();
            entity.setParentId(item.getValue().getKanbanHierarchyId());
            entity.setPropertyCollection(new ArrayList<>());
            entity.setKanbanStatus(KanbanStatusEnum.PLANNING);
            SelectedKanbanAndHierarchy selected = new SelectedKanbanAndHierarchy(entity, item.getValue().getHierarchyName());
            sc.setComponent("ContentNaviPane", "KanbanDetailCompo", selected);
        }
    }

    /**
     * 削除ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onDeleteKanban( MouseEvent event) {
        try {
            logger.info("onDeleteKanban start.");

            List<DisplayData> datas = kanbanList.getSelectionModel().getSelectedItems();
            if (Objects.isNull(datas) || datas.isEmpty()) {
                return;
            }

            final String messgage = datas.size() > 1
                    ? LocaleUtils.getString("key.DeleteMultipleMessage")
                    : LocaleUtils.getString("key.DeleteSingleMessage");
            final String content = datas.size() > 1
                    ? null
                    : datas.get(0).getKanbanName();

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), messgage, content);
            if (!ret.equals(ButtonType.OK)) {
                return;
            }

            // カンバンを削除する。
            if (datas.size() == 1) {
                deleteKanban(datas.get(0));
            } else {
                boolean isForced = event.isAltDown() && event.isControlDown() && event.isShiftDown();
                deleteKanbans(datas, isForced);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onDeleteKanban end.");
        }
    }



//    KanbanInfoEntity kanban = kanbanData.getEntity();
//                if (!loginUserInfoEntity.getRoleAuthCollection()
//                        .contains(RoleAuthorityTypeEnum.DELETE_ACTUAL.getName())) {
//        // 実績削除権限がない場合は、メッセージを表示して終了(権限の種別がSYSTEM_ADMINであっても例外扱いしない)。
//        String message = LocaleUtils.getPatternText("key.warn.noPermitionDeleteActual");
//        sc.showMessageBox(Alert.AlertType.ERROR, LocaleUtils.getPatternText("key.KanbanDelete"),
//                message, new ButtonType[]{ButtonType.OK}, ButtonType.OK);
//
//    } else if (loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
//        String message;
//        if (kanban.getKanbanStatus() == KanbanStatusEnum.WORKING || kanban.getKanbanStatus() == KanbanStatusEnum.SUSPEND) {
//            message = String.format(LocaleUtils.getPatternText("key.warn.forcedDeleteKanban1"), kanbanData.getKanbanName(), kanbanData.getStatus());
//        } else {
//            message = String.format(LocaleUtils.getPatternText("key.warn.forcedDeleteKanban2"), kanbanData.getKanbanName(), kanbanData.getStatus());
//        }
//
//        MessageDialogResult dialogResult = MessageDialog.show(sc.getWindow(), LocaleUtils.getPatternText("key.KanbanDelete"), message,
//                MessageDialogType.Warning, MessageDialogButtons.YesNo, 3.0, "#ff0000", "#ffffff");
//        if (!dialogResult.equals(MessageDialogResult.Yes)) {
//            return ret;
//        }
//        ret = false;
//
//        // カンバンを実績ごと削除する。
//        forcedDeleteKanban(kanbanData);
//    } else {
//        // リソース編集権限がない場合、工程実績のあるカンバンは削除できない。
//        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getPatternText("key.KanbanDelete"),
//                String.format(LocaleUtils.getPatternText("key.KanbanDeleteFailed"), kanbanData.getKanbanName()));
//    }
    /**
     * 複数のカンバンを削除する。
     *
     * @param datas 対象データ
     */
    private void deleteKanbans(List<DisplayData> datas, boolean isForced) {
        blockUI(true);

        if (!loginUserInfoEntity.getRoleAuthCollection()
                .contains(RoleAuthorityTypeEnum.DELETE_ACTUAL.getName())) {
            // 実績削除権限がない場合は、メッセージを表示して終了(権限の種別がSYSTEM_ADMINであっても例外扱いしない)。
            String message = LocaleUtils.getString("key.warn.noPermitionDeleteActual");
            sc.showMessageBox(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanDelete"),
                    message, new ButtonType[]{ButtonType.OK}, ButtonType.OK);
            blockUI(false);
            return;
        }

        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            // リソース編集権限がない場合、カンバンは削除できない。
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanDelete"),
                    String.format(LocaleUtils.getString("key.KanbanDeleteFailed"), "Any" + LocaleUtils.getString("key.Kanban")));
            blockUI(false);
            return;
        }


        Task task = new Task<List<Long>>() {
            @Override
            protected List<Long> call() throws Exception {
                List<Long> skipList = new ArrayList();



                for (DisplayData data : datas) {
                    ResponseEntity responce =
                            isForced ? kanbanInfoFacade.deleteForced(data.getId())
                                    : kanbanInfoFacade.delete(data.getId());
                    if (Objects.isNull(responce.getErrorType())) {
                        continue;
                    }

                    switch (responce.getErrorType()) {
                        case SUCCESS:// 成功
                        case NOTFOUND_DELETE:// 存在しない
                            break;
                        case THERE_START_NON_DELETABLE:// 実績あり、削除不可
                            skipList.add(data.getId());
                            break;
                        default:// その他エラー
                            throw new Exception(responce.getErrorType().name());
                    }
                }

                searchKanbanData(false);

                return skipList;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    if (!this.getValue().isEmpty()) {
                        // 削除できなかったカンバンを選択状態にする。
                        kanbanList.getSelectionModel().clearSelection();
                        boolean isFirstSkip = true;
                        for (Long kanbanId : this.getValue()) {
                            Optional<DisplayData> opt = kanbanList.getItems().stream().filter(p -> kanbanId.equals(p.getId())).findFirst();
                            if (opt.isPresent()) {
                                kanbanList.getSelectionModel().select(opt.get());
                                if (isFirstSkip) {
                                    kanbanList.scrollTo(opt.get());
                                    isFirstSkip = false;
                                }
                            }
                        }

                        // カンバン操作ボタンの有効状態を設定する。
                        setStateKanbanButtons();

                        // 選択したカンバンのうち、ｎ件は作業実績があるため削除できませんでした。
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanDelete"),
                                String.format(LocaleUtils.getString("key.warn.FailedDeleteKanbans"), this.getValue().size()));
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
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
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanDelete"), LocaleUtils.getString("key.alert.systemError"));
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
            }
        };
        new Thread(task).start();
    }

    /**
     * カンバンを削除する。
     *
     * @param data 対象データ
     */
    private void deleteKanban(DisplayData data) {
        blockUI(true);
        Task task = new Task<ResponseEntity>() {
            @Override
            protected ResponseEntity call() throws Exception {
                return kanbanInfoFacade.delete(data.getId());
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                boolean isEnd = true;
                try {
                    // カンバン削除の結果処理
                    isEnd = deleteKanbanResultProcess(data, this.getValue());
                } finally {
                    if (isEnd) {
                        blockUI(false);
                    }
                }
            }

            @Override
            protected void failed() {
                super.failed();
                blockUI(false);
            }
        };
        new Thread(task).start();
    }

    /**
     * カンバン削除の結果処理を行なう。
     *
     * @param data 削除対象
     * @param responce 削除結果
     */
    private boolean deleteKanbanResultProcess(DisplayData data, ResponseEntity responce) {
        boolean ret = true;
        if (null == responce.getErrorType()) {
            return ret;
        }

        switch (responce.getErrorType()) {
            case THERE_START_NON_DELETABLE:
                KanbanInfoEntity kanban = data.getEntity();
                if (!loginUserInfoEntity.getRoleAuthCollection()
                        .contains(RoleAuthorityTypeEnum.DELETE_ACTUAL.getName())) {
                    // 実績削除権限がない場合は、メッセージを表示して終了(権限の種別がSYSTEM_ADMINであっても例外扱いしない)。
                    String message = LocaleUtils.getString("key.warn.noPermitionDeleteActual");
                    sc.showMessageBox(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanDelete"),
                            message, new ButtonType[]{ButtonType.OK}, ButtonType.OK);

                } else if (loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
                    String message;
                    if (kanban.getKanbanStatus() == KanbanStatusEnum.WORKING || kanban.getKanbanStatus() == KanbanStatusEnum.SUSPEND) {
                        message = String.format(LocaleUtils.getString("key.warn.forcedDeleteKanban1"), data.getKanbanName(), data.getStatus());
                    } else {
                        message = String.format(LocaleUtils.getString("key.warn.forcedDeleteKanban2"), data.getKanbanName(), data.getStatus());
                    }

                    MessageDialogResult dialogResult = MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.KanbanDelete"), message,
                            MessageDialogType.Warning, MessageDialogButtons.YesNo, 3.0, "#ff0000", "#ffffff");
                    if (!dialogResult.equals(MessageDialogResult.Yes)) {
                        return ret;
                    }
                    ret = false;

                    // カンバンを実績ごと削除する。
                    forcedDeleteKanban(data);
                } else {
                    // リソース編集権限がない場合、工程実績のあるカンバンは削除できない。
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanDelete"),
                            String.format(LocaleUtils.getString("key.KanbanDeleteFailed"), data.getKanbanName()));
                }
                break;
            case NOTFOUND_DELETE:
            default:
                // リストを更新する。
                onUpdateFilter(null);
                break;
        }
        return ret;
    }

    /**
     * カンバンを強制削除する。(工程実績も削除する)
     *
     * @param data 削除対象
     */
    private void forcedDeleteKanban(DisplayData data) {
        Task task = new Task<ResponseEntity>() {
            @Override
            protected ResponseEntity call() throws Exception {
                return kanbanInfoFacade.deleteForced(data.getId());
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    // カンバン強制削除の結果処理
                    forcedDeleteKanbanResultProcess(data, this.getValue());
                } finally {
                    blockUI(false);
                }
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
     * カンバン強制削除の結果処理を行なう。
     *
     * @param data 削除対象
     * @param responce 削除結果
     */
    private void forcedDeleteKanbanResultProcess(DisplayData data, ResponseEntity responce) {
        if (Objects.isNull(responce.getErrorType())) {
            return;
        }

        switch (responce.getErrorType()) {
            case THERE_START_NON_DELETABLE:
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanDelete"),
                        String.format(LocaleUtils.getString("key.KanbanDeleteFailed"), data.getKanbanName()));
                break;
            case NOTFOUND_DELETE:
            default:
                // リストを更新する。
                onUpdateFilter(null);
                break;
        }
    }

    /**
     * リスト更新
     *
     * @param event
     */
    @FXML
    private void onUpdateFilter(ActionEvent event) {
        // 別スレッドでカンバンを検索して、カンバンリストを更新する。
        searchKanbanDataTask();
    }

    /**
     * 承認ボタンのアクション
     *
     * @param event 押下イベント
     */
    @FXML
    private void onApproveButton(ActionEvent event) {
        // 承認ダイアログ表示
        logger.info("onReportListButton start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);

            List<DisplayData> datas = kanbanList.getSelectionModel().getSelectedItems();
            if (Objects.isNull(datas) || datas.isEmpty()) {
                return;
            }

            // 選択したカンバン一覧から、カンバン一覧を作成する。
            final List<KanbanInfoEntity> kanbans = datas.stream().map(p -> p.getEntity()).collect(Collectors.toList());
            if (Objects.isNull(kanbans)) {
                return;
            }

            // 承認ダイアログを表示する。
            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.Approve"), "ApproveDialog", kanbans);
            if (!ButtonType.OK.equals(ret)) {
                return;
            }

            // カンバン一覧表示を更新する。
            searchKanbanData(false);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(obj, false);
        }
    }

    /**
     * パーツID検索ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onPartsIDSearch(ActionEvent event) {
        // パーツID検索ダイアログ表示
        logger.info("onPartsIDSearch start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);
            
            // パーツID検索ダイアログを表示する。
            sc.showDialog(LocaleUtils.getString("key.PartsIDSearch"), "PartsIDSearchDialog", null, sc.getStage(), true);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(obj, false);
        }
    }

    /**
     * 新規作成ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onCreateKanban(ActionEvent event) {
        // 休日情報がキャッシュされていない場合は読み込む。
        CacheUtils.createCacheHoliday(true);

        // カンバンの新規作成画面を表示する。
        dispCompoCreateKanban(null);
    }

    /**
     * カンバンの新規作成画面を表示する。
     *
     * @param compoCreateKanban カンバン作成画面
     */
    private void dispCompoCreateKanban(String compoCreateKanban) {
        logger.info("dispCompoCreateKanban: {}", compoCreateKanban);

        // カンバン作成画面の指定がない場合は設定から取得する。
        if (Objects.isNull(compoCreateKanban) || compoCreateKanban.isEmpty()) {
            compoCreateKanban = this.properties.getProperty(Config.COMPO_CREATE_KANBAN, this.defaultCreateKanbanCompo);
        }

        this.properties.setProperty(Config.COMPO_CREATE_KANBAN, compoCreateKanban);

        // 選択階層
        TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();

        // 階層未選択の場合は何もしない。
        if (Objects.isNull(item) || Objects.isNull(item.getValue())) {
            return;
        }

        // ルートを選択している場合は何もしない。
        KanbanHierarchyInfoEntity hierarchy = item.getValue();
        if (ROOT_ID == hierarchy.getKanbanHierarchyId()) {
            return;
        }

        String hierarchyName = hierarchy.getHierarchyName();
        long hierarchyId = hierarchy.getKanbanHierarchyId();

        SelectedKanbanAndHierarchy selected = new SelectedKanbanAndHierarchy(null, hierarchyName, LocaleUtils.getString("key.KanbanContinuousCreate"), hierarchyId);

        // カンバン作成画面を表示する。
        sc.setComponent("ContentNaviPane", compoCreateKanban, selected);
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onRegularlyCreate(ActionEvent event) {
        TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(item) && item.getValue().getKanbanHierarchyId() != ROOT_ID) {
            SelectedKanbanAndHierarchy selected = new SelectedKanbanAndHierarchy(null, item.getValue().getHierarchyName(),
                    LocaleUtils.getString("key.KanbanRegularlyCreate"), item.getValue().getKanbanHierarchyId());
            sc.setComponent("ContentNaviPane", "KanbanMultipleRegistCompo", selected);
        }
    }

    /**
     * 帳票出力ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onCreateLedger(ActionEvent event) {
        logger.info("onCreateLedger");
        boolean isCancel = true;
        try {
            blockUI(true);

            Properties props = AdProperty.getProperties(Constants.PROPERTY_NAME);

            // 出力先
            String reportFolder = props.getProperty(Constants.REPORT_FOLDER, null);
            // 置換されなかったタグを残す
            boolean reportLeaveTags = Boolean.valueOf(props.getProperty(Constants.REPORT_LEAVE_TAGS, "true"));
            // pdfにて出力する
            boolean exprotAsPdf = Boolean.valueOf(props.getProperty(Constants.REPORT_AS_PDF_TAGS, "false"));

            // 帳票出力条件
            ReportOutputCondition condition = new ReportOutputCondition();
            condition.setOutputFolder(reportFolder);
            condition.setLeaveTags(reportLeaveTags);
            condition.setExportAsPdf(exprotAsPdf);

            // 工程順が単一か混在かをチェックする
            // 選択しているカンバンリスト
            List<DisplayData> kanbanSelected = kanbanList.getSelectionModel().getSelectedItems();
            // 工程順単一判定フラグを初期化
            this.isWorkflowSingle = true;
            // リストの先頭の工程順IDを取得
            Long tempWorkflowId = kanbanSelected.get(0).getWorkflowId();

            for (DisplayData data : kanbanSelected) {
                if (!tempWorkflowId.equals(data.getWorkflowId())) {
                    this.isWorkflowSingle = false;
                    break;
                }
            }
            if (this.isWorkflowSingle) {
                // 帳票出力ダイアログ(帳票選択あり)を表示
                WorkflowInfoEntity workflow = CacheUtils.getCacheWorkflow(tempWorkflowId);
                List<String> fileList = new ArrayList<>();
                
                for (SimpleStringProperty pathProp : workflow.getLedgerPathPropertyCollection()) {
                    File templateFile = new File(pathProp.getValue());
                    if (!this.useQRCode && StringUtils.equals(templateFile.getName(), "qrcode_template.xlsx")) {
                        continue;
                    }
                    fileList.add(templateFile.getName());
                }
                condition.setTemplateNames(fileList);
                
                ButtonType ret = sc.showDialog(LocaleUtils.getString("key.OutLedgerTitle"), "ReportOutputChoiceDialog", condition);
                if (!ButtonType.OK.equals(ret)) {
                    return;
                }
            } else {
                // 帳票出力ダイアログ(帳票選択なし)を表示
                condition.setTemplateNames(null);
                ButtonType ret = sc.showDialog(LocaleUtils.getString("key.OutLedgerTitle"), "ReportOutputDialog", condition);
                if (!ButtonType.OK.equals(ret)) {
                    return;
                }
            }

            props.setProperty(Constants.REPORT_FOLDER, condition.getOutputFolder());
            props.setProperty(Constants.REPORT_LEAVE_TAGS, String.valueOf(condition.isLeaveTags()));
            props.setProperty(Constants.REPORT_AS_PDF_TAGS, String.valueOf(condition.isExportAsPdf()));

            final Path outputDir = Paths.get(condition.getOutputFolder());
            final boolean isRemoveTag = !condition.isLeaveTags();
            final boolean isExportAsPdf = condition.isExportAsPdf();

            // 出力対象のテンプレートファイル名リストをセットする 
            this.checkTemplateList = condition.getTemplateNames();
            final List<DisplayData> selected = kanbanList.getSelectionModel().getSelectedItems();

            Task task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    return createLedgerThread(outputDir, selected, isRemoveTag, isExportAsPdf);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // 帳票ファイルを開ける結果ダイアログを表示する。
                        FileOpenDialog fileOpenDialog = new FileOpenDialog(LocaleUtils.getString("key.KanbanOutLedger"), this.getValue(), outputFilePathList, reportInfos);
                        fileOpenDialog.start(new Stage());
                    } finally {
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
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanOutLedger"), LocaleUtils.getString("key.alert.systemError"));
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };
            new Thread(task).start();

            isCancel = false;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }
    }

    /**
     *
     * @param outputDir
     * @param selected
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @return
     * @throws Exception
     */
    private String createLedgerThread(Path outputDir, List<DisplayData> selected, boolean isRemoveTag, boolean isExportAsPdf) throws Exception {
        Path resultFile = Paths.get(outputDir.toString(), "result.txt");
        Boolean isCompleteSuccess = true;
        int cntSuccess = 0;
        int cntFailure = 0;
        int cntSkip = 0;

        if (Files.exists(resultFile)) {
            Files.delete(resultFile);
        }

        double startTime = System.nanoTime();

        // 保持している出力帳票のリストをクリアする 
        outputFilePathList.clear();
        // 保持している出力帳票情報のリストをクリアする
        reportInfos.clear();

        // 選択したカンバンID一覧
        List<Long> kanbanIds = selected.stream()
                .map(p -> p.getId())
                .collect(Collectors.toList());

        // カンバンID一覧を指定して、カンバン情報一覧を取得する。
        List<KanbanInfoEntity> kanbans = this.getKanbans(kanbanIds);

        // カンバンID一覧を指定して、工程実績情報一覧を取得する。
        List<ActualResultEntity> actuals = this.getActualResults(kanbanIds);

        final Comparator<ActualResultEntity> dateComparator = (p1, p2) -> p1.getImplementDatetime().compareTo(p2.getImplementDatetime());
        final Comparator<ActualResultEntity> actualIDComparator = (p1, p2) -> p1.getActualId().compareTo(p2.getActualId());
        for (KanbanInfoEntity kanban : kanbans) {
            List<ActualResultEntity> kanbanActuals = actuals.stream()
                    .filter(p -> Objects.equals(p.getFkKanbanId(), kanban.getKanbanId()))
                    .sorted(dateComparator.thenComparing(actualIDComparator))
                    .collect(Collectors.toList());

            kanban.setActualResultCollection(kanbanActuals);

            LedgerProcResultType result = createLedger(kanban, outputDir, isRemoveTag, isExportAsPdf);
            switch (result) {
                case KANBAN_INCOMPLETED:
                case TEMPLATE_UNREGISTERED:
                    isCompleteSuccess &= false;
                    cntSkip++;
                    break;

                case GET_INFO_FAILURED:
                case TEMPLATE_NONE:
                case ERROR_OCCURED:
                    isCompleteSuccess &= false;
                    cntFailure++;
                    break;

                case SUCCESS_INCOMPLETE:
                    isCompleteSuccess &= false;
                case SUCCESS:
                    cntSuccess++;
                    break;
            }
        }

        double elapsedSec = (System.nanoTime() - startTime) / Math.pow(10, 9);
        double elapsedMinute = Math.floor(elapsedSec / 60);
        elapsedSec -= elapsedMinute * 60;
        double elapsedHour = Math.floor(elapsedMinute / 60);
        elapsedMinute -= elapsedHour * 60;

        try (BufferedWriter bw = Files.newBufferedWriter(resultFile, Charset.defaultCharset(),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            bw.append("(" + String.format("%dh %dm %.2fs", (long) elapsedHour, (long) elapsedMinute, elapsedSec) + ")");
            bw.newLine();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(LocaleUtils.getString("key.KanbanOutLedgerComplete"), cntSuccess, cntFailure, cntSkip));
        if (isCompleteSuccess) {
            Files.delete(resultFile);
        } else {
            sb.append("\r\n\r\n");
            sb.append(String.format(LocaleUtils.getString("key.KanbanOutLedgerResultInfo"), resultFile.toAbsolutePath().toString()));
        }

        return sb.toString();
    }

    /**
     * 帳票出力先ディレクトリ選択ダイアログを表示する
     *
     * @param event
     * @return
     */
    private Path showOutputDialog(ActionEvent event) {
        Node node = (Node) event.getSource();

        Path desktopDir = Paths.get(System.getProperty("user.home"), "Desktop");
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(desktopDir.toFile());

        return Paths.get(dirChooser.showDialog(node.getScene().getWindow()).getAbsolutePath());
    }


    /**
     * 指定されたExcelファイルをPDFファイルに変換します。
     *
     * @param excelPath 変換元のExcelファイルのパス
     * @param pdfPath 変換先のPDFファイルのパス
     * @return 変換が成功した場合はtrue、失敗した場合はfalseを返します
     */
    public boolean convertExcelToPdf(String excelPath, String pdfPath) {
        try {
            // PowerShellコマンドを構築
//            static String scriptPath = System.getenv("ADFACTORY_HOME") + File.separator + "bin" + File.separator + "xlsx2pdf.ps1";
//            ProcessBuilder processBuilder = new ProcessBuilder(
//                    "pwsh",
//                    "-NoProfile",
//                    "-ExecutionPolicy", "Unrestricted",
//                    "-File", scriptPath,
//                    "-from", excelPath,
//                    "-to", pdfPath
//            );

            String batPath = System.getenv("ADFACTORY_HOME") + File.separator + "bin" + File.separator + "xlsx2pdf.bat";
            ProcessBuilder processBuilder = new ProcessBuilder(
                    batPath,
                    excelPath,
                    pdfPath
            );

            // プロセスのエラーストリームと標準ストリームを結合
            processBuilder.redirectErrorStream(true);
            // プロセスを実行
            Process process = processBuilder.start();

            // 標準出力を非同期で処理
            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.info(line);  // 出力をログに記録
                    }
                } catch (Exception ex) {
                    logger.error(ex, ex);
                }
            });

            // エラー出力の読み取りスレッド
            Thread stderrReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.error(line);
                    }
                } catch (Exception ex) {
                    logger.error(ex, ex);
                }
            });

            outputReader.start();
            stderrReader.start();

            // プロセスの終了を待機
            int exitCode = process.waitFor();
            outputReader.join();
            stderrReader.join();

            // 終了コードを確認 (0は成功)
            return exitCode == 0;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        }
    }

    /**
     * 帳票出力
     *
     * @param kanban カンバン情報
     * @param outputDir
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @return
     */
    private LedgerProcResultType createLedger(KanbanInfoEntity kanban, Path outputDir, boolean isRemoveTag, boolean isExportAsPdf) {
        Path resultFile = Paths.get(outputDir.toString(), "result.txt");
        String message = "";
        LedgerProcResultType ret = LedgerProcResultType.FAILD_OTHER;

        boolean isOutputFailed = false;
        try {
            // 帳票テンプレートファイルパス
            List<SimpleStringProperty> pathProps = PropertyUtils.stringToPropertyList(kanban.getLedgerPath(), "\\|");
            if (Objects.isNull(pathProps) || pathProps.isEmpty()) {
                // 未登録
                message = LocaleUtils.getString("key.KanbanOutLedgerPassNothing");
                ret = LedgerProcResultType.TEMPLATE_UNREGISTERED;
                return ret;
            }

            // 帳票データ取得処理
            KanbanLedgerPermanenceData ledgerData = new KanbanLedgerPermanenceData();
            ledgerData.setLedgerFilePass(kanban.getLedgerPath());

            ledgerData.setKanbanInfoEntity(kanban);

            // 工程カンバンをセットする。
            ledgerData.setWorkKanbanInfoEntities(kanban.getWorkKanbanCollection());
            if (Objects.isNull(ledgerData.getWorkKanbanInfoEntities())) {
                // 工程カンバンなし
                message = LocaleUtils.getString("key.ServerReconnectMessage");
                ret = LedgerProcResultType.GET_INFO_FAILURED;
                return ret;
            }

            // 追加工程カンバンをセットする。
            ledgerData.setSeparateworkWorkKanbanInfoEntities(kanban.getSeparateworkKanbanCollection());
            if (Objects.isNull(ledgerData.getSeparateworkWorkKanbanInfoEntities())) {
                // 工程カンバンなし
                message = LocaleUtils.getString("key.ServerReconnectMessage");
                ret = LedgerProcResultType.GET_INFO_FAILURED;
                return ret;
            }

            // 実績をセットする。
            ledgerData.setActualResultInfoEntities(kanban.getActualResultCollection());
            if (Objects.isNull(ledgerData.getActualResultInfoEntities())) {
                // 実績なし
                message = LocaleUtils.getString("key.ServerReconnectMessage");
                ret = LedgerProcResultType.GET_INFO_FAILURED;
                return ret;
            }

            // トレーサビリティDB使用時は、トレーサビリティDBからデータを取得する。
            if (this.isTraceabilityDBEnabled) {
                List<TraceabilityEntity> traces = traceabilityFacade.findKanbanTraceability(kanban.getKanbanId());
                ledgerData.setTraceabilityEntities(traces);
            }

            // 拡張タグ使用するか設定をセットする。
            ledgerData.setUseExtensionTag(this.useExtensionTag);

            // 部品トレース
            ledgerData.setEnablePartsTrace(this.enablePartsTrace);
            if (this.enablePartsTrace) {
                // 使用部品一覧
                List<AssemblyPartsInfoEntity> partsInfos = this.findAssemblyParts(kanban.getKanbanName());
                ledgerData.setAssemblyPartsInfos(partsInfos);
            }

            // QRコードを使用するか設定をセットする。
            ledgerData.setUseQRCodeTag(this.useQRCode);

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Date now = new Date();
            String dateString = df.format(now);

            // 帳票テンプレートごとに帳票ファイルを出力する。
            for (SimpleStringProperty pathProp : pathProps) {
                File templateFile = new File(pathProp.getValue());
                String templateName = templateFile.getName().substring(0, templateFile.getName().lastIndexOf('.'));

                // 工程順の単一選択時は帳票テンプレート出力可否チェックを実施する。
                if (!this.workflowSingleOutputCheck(templateFile.getName())) {
                    // 結果がfalseの場合は出力せずにスキップ
                    continue;
                }

                // ロット一個流し生産はシリアル毎に出力する。
                int outputNum;
                if (ledgerData.getKanbanInfoEntity().getProductionType() == 1) {
                    outputNum = ledgerData.getKanbanInfoEntity().getLotQuantity();
                } else {
                    outputNum = 1;
                }

                for (int workNo = 1; workNo <= outputNum; workNo++) {
                    StringBuilder filename = new StringBuilder()
                            .append(ledgerData.getKanbanInfoEntity().getKanbanName())
                            .append("_")
                            .append(ledgerData.getKanbanInfoEntity().getWorkflowName())
                            .append("_")
                            .append(dateString)
                            .append("(")
                            .append(templateName)
                            .append(")");

                    if (outputNum > 1) {
                        filename.append("_#");
                        filename.append(workNo);
                    }

                    String ledgerFileName = filename.toString();

                    LedgerSheetFactory ledgerFactory = new LedgerSheetFactory();

                    Path inFilePath = Paths.get(templateFile.getPath());
                    if (!Files.exists(inFilePath)) {
                        // 帳票テンプレートファイルがない
                        isOutputFailed = true;
                        message = LocaleUtils.getString("key.KanbanOutLedgerPassErr");
                        ret = LedgerProcResultType.TEMPLATE_NONE;

                        // 帳票出力結果ファイルを出力する。
                        outputRelustFile(resultFile, kanban.getKanbanName(), kanban.getWorkflowName(), templateFile.getPath(), message);
                        continue;
                    }

                    String inFileExt = templateFile.toString().substring(templateFile.toString().lastIndexOf("."));
                    Path outputFile = Paths.get(outputDir.toString(), PathUtils.replacePath(ledgerFileName) + inFileExt);
                    for (int cnt = 2; Files.exists(outputFile); cnt++) {
                        outputFile = Paths.get(outputDir.toString(), PathUtils.replacePath(ledgerFileName) + "_" + cnt + inFileExt);
                    }

                    String workbookName = "workbook";

                    // 帳票テンプレートを読み込む。
                    ledgerFactory.mergeTemplateWorkbook(workbookName, inFilePath.toFile());

                    final LedgerTagCase ledgerTagCase
                            = StringUtils.equals(AdProperty.getProperties().getProperty(LedgerTagCase.name, InsensitiveCaseLedgerTag.name), NaturalCaseLedgerTag.name)
                            ? NaturalCaseLedgerTag.instance
                            : InsensitiveCaseLedgerTag.instance;

                    // タグを置換する。
                    List<String> faildReplaceTags = ledgerFactory.replaceTags(workbookName, ledgerData, workNo, false, isRemoveTag, ledgerTagCase);

                    // ワークブックを保存する。
                    if (ledgerFactory.saveWorkbook(workbookName, outputFile.toFile())) {
                        if (!faildReplaceTags.isEmpty()) {
                            // 置換できなかったタグがある
                            StringBuilder sb = new StringBuilder();
                            sb.append(LocaleUtils.getString("key.KanbanOutLedgerNoReplaceComp"));
                            sb.append(" - ");
                            int showTagSize = faildReplaceTags.size();
                            for (int index = 0; showTagSize > index; index++) {
                                if (index > 0) {
                                    sb.append(",");
                                }
                                sb.append(faildReplaceTags.get(index));
                            }
                            message = sb.toString();

                            if (!isOutputFailed) {
                                isOutputFailed = true;
                                ret = LedgerProcResultType.SUCCESS_INCOMPLETE;
                            }

                            // 帳票出力結果ファイルを出力する。
                            outputRelustFile(resultFile, kanban.getKanbanName(), kanban.getWorkflowName(), outputFile.toString(), message);
                        }

                        if (isExportAsPdf) {
                            // excelファイルからpdfへ変換
                            String excelPath = outputFile.toString();
                            String pdfPath = excelPath.substring(0, excelPath.lastIndexOf('.')) + ".pdf";
                            if (convertExcelToPdf(excelPath, pdfPath)) {
                                // pdfの変換に成功した場合はexcelを削除する
                                Files.delete(outputFile);
                                outputFile = Paths.get(pdfPath);
                            } else {
                                // pofへの変換が失敗した場合はエラーにする
                                outputRelustFile(resultFile, kanban.getKanbanName(), kanban.getWorkflowName(), outputFile.toString(),
                                        "\n\r" + LocaleUtils.getString("key.KanbanOutLedgerPdfFail"));
                                isOutputFailed = true;
                                ret = LedgerProcResultType.ERROR_OCCURED;
                                continue;
                            }
                        }

                    } else {
                        isOutputFailed = true;
                    }

                    // 2019/12/06 作業完了以外の帳票出力対応 ダイアログで表示するため、出力帳票のパスを格納しておく 
                    if (!outputFilePathList.contains(outputFile.toAbsolutePath())) {
                        outputFilePathList.add(outputFile.toAbsolutePath());
                    }

                    // カンバン帳票情報作成
                    KanbanReportInfoEntity info = new KanbanReportInfoEntity();
                    info.setKanbanId(kanban.getKanbanId());
                    info.setFilePath(outputFile.toAbsolutePath().toString());
                    info.setOutputDate(now);
                    info.setTemplateName(templateName);
                    info.setReportType(ReportTypeEnum.KANBAN_REPORT);
                    reportInfos.add(info);
                }
            }

            if (!isOutputFailed) {
                ret = LedgerProcResultType.SUCCESS;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            message = LocaleUtils.getString("key.KanbanOutLedgerApplicationErr") + " - " + ex.getMessage();
            ret = LedgerProcResultType.ERROR_OCCURED;
        } finally {
            if (!isOutputFailed) {
                // 帳票出力結果ファイルを出力する。
                outputRelustFile(resultFile, kanban.getKanbanName(), kanban.getWorkflowName(), null, message);
            }
        }
        return ret;
    }

    /**
     * カンバンIDでカンバンを取得する
     *
     * @param kanbanId
     * @return
     */
    private KanbanInfoEntity getKanban(Long kanbanId) {
        KanbanInfoFacade facade = new KanbanInfoFacade();
        return facade.find(kanbanId);
    }

    /**
     * カンバンIDで工程カンバンを取得する
     *
     * @param kanbanId
     * @return
     */
    private List<WorkKanbanInfoEntity> getWorkKanbans(Long kanbanId) {
        try {
            List<WorkKanbanInfoEntity> workKanbans = new ArrayList<>();
            WorkKanbanInfoFacade facade = new WorkKanbanInfoFacade();
            Long workkanbanCnt = facade.countFlow(kanbanId);
            for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                workKanbans.addAll(facade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanbanId));
            }
            return workKanbans;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * カンバンIDで工程カンバンを取得する
     *
     * @param kanbanId
     * @return
     */
    private List<WorkKanbanInfoEntity> getSeparateWorkKanbans(Long kanbanId) {
        try {
            List<WorkKanbanInfoEntity> workKanbans = new ArrayList<>();
            WorkKanbanInfoFacade facade = new WorkKanbanInfoFacade();
            Long workkanbanCnt = facade.countSeparate(kanbanId);
            for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                workKanbans.addAll(facade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanbanId));
            }
            return workKanbans;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * カンバンID一覧を指定して、工程実績情報一覧を取得する。
     *
     * @param kanbanIds カンバンID一覧
     * @return 工程実績情報一覧
     */
    private List<ActualResultEntity> getActualResults(List<Long> kanbanIds) {
        try {
            List<ActualResultEntity> actuals = new ArrayList<>();
            ActualResultInfoFacade facade = new ActualResultInfoFacade();

            for (int rangeFrom = 0; rangeFrom < kanbanIds.size(); rangeFrom += ACTUAL_KANBAN_RANGE) {
                int rangeTo = rangeFrom + ACTUAL_KANBAN_RANGE;
                if (rangeTo > kanbanIds.size()) {
                    rangeTo = kanbanIds.size();
                }

                List<Long> rangeIds = kanbanIds.subList(rangeFrom, rangeTo);
                actuals.addAll(facade.find(rangeIds, true));
            }

            return actuals;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * ツリーの親階層生成
     * 
     *@param selectedId 生成後に選択状態にするカンバン階層ID
     */
    private synchronized void createRoot(Long selectedId) {
        logger.debug("createRoot start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);

            kanbanEditPermanenceData.setKanbanHierarchyRootItem(new TreeItem<>(new KanbanHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.Kanban"))));
            TreeItem<KanbanHierarchyInfoEntity> rootItem = kanbanEditPermanenceData.getKanbanHierarchyRootItem();

            Task task = new Task<List<KanbanHierarchyInfoEntity>>() {
                @Override
                protected List<KanbanHierarchyInfoEntity> call() throws Exception {
                    // 子組織の件数を取得する。
                    long count = kanbanHierarchyInfoFacade.getTopHierarchyCount();

                    List<KanbanHierarchyInfoEntity> hierarchies = new ArrayList();
                    for (long from = 0; from <= count; from += RANGE) {
                        List<KanbanHierarchyInfoEntity> entities = kanbanHierarchyInfoFacade.getTopHierarchyRange(from, from + RANGE - 1);
                        hierarchies.addAll(entities);
                    }
                    return hierarchies;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        long count = this.getValue().size();
                        rootItem.getValue().setChildCount(count);

                        this.getValue().stream().forEach((entity) -> {
                            TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);

                            // ツリー展開状態を復元
                            if (treeItems.containsKey(entity.getKanbanHierarchyId())) {
                                item.setExpanded(treeItems.get(entity.getKanbanHierarchyId()).isExpanded());
                            }
                            treeItems.put(entity.getKanbanHierarchyId(), item);

                            if (entity.getChildCount() > 0) {
                                item.getChildren().add(new TreeItem());
                            }
                            item.expandedProperty().addListener(expandedListener);
                            rootItem.getChildren().add(item);

                            if (entity.getKanbanHierarchyId().equals(selectedId)) {
                                selectedProperty.setValue(item);
                            }

                            if (item.isExpanded()) {
                                expand(item, selectedId);
                            }
                        });

                        hierarchyTree.rootProperty().setValue(rootItem);

                        Platform.runLater(() -> {
                            hierarchyTree.rootProperty().setValue(rootItem);
                            rootItem.setExpanded(true);
                            hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new KanbanHierarchyTreeCell());

                            if (Objects.nonNull(selectedProperty.get())) {
                                hierarchyTree.getSelectionModel().select(selectedProperty.get());
                                hierarchyTree.requestFocus();
                                return;
                            }

                            hierarchyTree.getSelectionModel().select(rootItem);
                        });

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    blockUI(obj, false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
    }

    /**
     * ツリー展開
     *
     * @param parentItem 親階層
     * @param selectedId 展開後に選択状態にするカンバン階層ID
     */
    private synchronized void expand(TreeItem<KanbanHierarchyInfoEntity> parentItem, Long selectedId) {
        logger.debug("expand: parentItem={}", parentItem.getValue());
        Object obj = new Object();
        try {
            if (!isDispDialog) {
                blockUI(obj, true);
            }

            parentItem.getChildren().clear();

            final long parentId = parentItem.getValue().getKanbanHierarchyId();

            Task task = new Task<List<KanbanHierarchyInfoEntity>>() {
                @Override
                protected List<KanbanHierarchyInfoEntity> call() throws Exception {
                    // 子階層の件数を取得する。
                    long count = kanbanHierarchyInfoFacade.getAffilationHierarchyCount(parentId);

                    List<KanbanHierarchyInfoEntity> hierarchies = new ArrayList();
                    for (long from = 0; from <= count; from += RANGE) {
                        List<KanbanHierarchyInfoEntity> entities = kanbanHierarchyInfoFacade.getAffilationHierarchyRange(parentId, from, from + RANGE - 1);
                        hierarchies.addAll(entities);
                    }
                    return hierarchies;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        this.getValue().stream().forEach((entity) -> {
                            TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);
                            if (selectedHierarchyId.equals(item.getValue().getKanbanHierarchyId())) {
                                // カンバン階層で選択されている階層は移動ダイアログには表示しない
                                return;
                            }

                            if (treeItems.containsKey(entity.getKanbanHierarchyId())) {
                                // ツリー展開状態を復元
                                item.setExpanded(treeItems.get(entity.getKanbanHierarchyId()).isExpanded());
                            }
                            treeItems.put(entity.getKanbanHierarchyId(), item);

                            if (entity.getChildCount() > 0) {
                                item.getChildren().add(new TreeItem());
                            }
                            item.expandedProperty().addListener(expandedListener);
                            parentItem.getChildren().add(item);

                            if (entity.getKanbanHierarchyId().equals(selectedId)) {
                                selectedProperty.setValue(item);
                            }

                            if (item.isExpanded()) {
                                expand(item, selectedId);
                            }
                        });

                        Platform.runLater(() -> {
                            hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new KanbanHierarchyTreeCell());
                            if (Objects.nonNull(selectedProperty.get())) {
                                hierarchyTree.getSelectionModel().select(selectedProperty.get());
                                hierarchyTree.requestFocus();
                            }
                        });

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        if (!isDispDialog) {
                            blockUI(obj, false);
                        }
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    blockUI(obj, false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
    }

    /**
     * IDが一致するTreeItemを選択する。(存在しない場合は親を選択)
     *
     * @param parentItem 選択状態にするノードの親ノード
     * @param selectedId 選択状態にするノードのカンバン階層ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void selectedTreeItem(TreeItem<KanbanHierarchyInfoEntity> parentItem, Long selectedId) {
        if (Objects.isNull(parentItem)) {
            parentItem = this.hierarchyTree.getRoot();
        }

        Optional<TreeItem<KanbanHierarchyInfoEntity>> find = parentItem.getChildren().stream().
                filter(p -> Objects.nonNull(p.getValue())
                && Objects.nonNull(p.getValue().getKanbanHierarchyId())
                && p.getValue().getKanbanHierarchyId().equals(selectedId)).findFirst();

        if (find.isPresent()) {
            this.hierarchyTree.getSelectionModel().select(find.get());
        } else {
            this.hierarchyTree.getSelectionModel().select(parentItem);
        }
        this.hierarchyTree.scrollTo(this.hierarchyTree.getSelectionModel().getSelectedIndex());// 選択ノードが見えるようスクロール
    }

    private ScrollBar findScrollBar() {
        Node scrollBar = this.kanbanList.lookup(".scroll-bar:vertical");
        if (scrollBar instanceof ScrollBar) {
            return (ScrollBar) scrollBar;
        }
        return null;
    }

    private void selectedKanbanItem() {
        final Long kanbanId = kanbanEditPermanenceData.getSelectedKanbanId();
        if (Objects.isNull(kanbanId)) {
            this.moveKanbanButton.setDisable(true);
            return;
        }

        Optional<DisplayData> displayData
                = this.kanbanList.getItems()
                .stream()
                .filter(p -> Objects.equals(p.getEntity().getKanbanId(), kanbanId))
                .findFirst();

        if (!displayData.isPresent()) {
            return;
        }

        this.kanbanList.getSelectionModel().select(displayData.get());
        this.kanbanList.scrollTo(displayData.get());
        kanbanEditPermanenceData.setSelectedKanban(null);
    }

    /**
     * 別スレッドでカンバンを検索して、カンバンリストを更新する。
     */
    private void searchKanbanDataTask() {
        logger.debug("searchKanbanDataTask start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    searchKanbanData(true);
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    blockUI(obj, false);
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    blockUI(obj, false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
    }

    /**
     * カンバン検索
     *
     * @param isDispWarning 警告メッセージ表示 (true:する, false:しない)
     */
    private void searchKanbanData(boolean isDispWarning) {
        try {
            // カンバン階層ID
            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())) {
                return;
            }
            long parentId = hierarchyTree.getSelectionModel().getSelectedItem().getValue().getKanbanHierarchyId();

            // カンバン名
            String kanbanName = (StringUtils.isEmpty(kanbanNameField.getText())) ? null : kanbanNameField.getText();

            // モデル名
            String modelName = (StringUtils.isEmpty(modelNameField.getText())) ? null : modelNameField.getText();

            // 製造番号
            String productNo =  (StringUtils.isEmpty(productNoField.getText())) ? null : productNoField.getText();

            // 工程順名
            String workflowName = (StringUtils.isEmpty(workflowNameField.getText())) ? null : workflowNameField.getText();
            // 工程名
            String workName = (StringUtils.isEmpty(workNameField.getText())) ? null : workNameField.getText();

            // カンバンステータス
            List<KanbanStatusEnum> selectStatusData =  statusList.isSelected() ? statusList.getStatus() : new ArrayList<>();

            if (parentId == ROOT_ID
                && StringUtils.isEmpty(kanbanName)
                && StringUtils.isEmpty(modelName)
                && StringUtils.isEmpty(productNo)
                && StringUtils.isEmpty(workflowName)
                && StringUtils.isEmpty(workName)
                && selectStatusData.isEmpty()) {
                // トップ階層の場合、検索条件が指定されていないと更新しない
                kanbanEditPermanenceData.setSelectedWorkHierarchy(null);
                this.clearKanbanList();
                return;
            }
            
            // 作業予定日
            Date scheduleStartDay = (Objects.isNull(startDatePicker.getValue())
                    ? null : DateUtils.getBeginningOfDate(Date.from(startDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
            Date scheduleEndDay = (Objects.isNull(endDatePicker.getValue())
                    ? null : DateUtils.getEndOfDate(Date.from(endDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
            if (Objects.nonNull(scheduleStartDay) && Objects.nonNull(scheduleEndDay)) {
                if (0 > DateUtils.differenceOfDate(formatter.format(scheduleEndDay), formatter.format(scheduleStartDay))) {
                    if (isDispWarning) {
                        Platform.runLater(() -> {
                            // 開始日時が終了日時より遅い。
                            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.KanbanSearch"), LocaleUtils.getString("key.DateCompErrMessage"));
                        });
                    }
                    return;
                }
            }

            ObservableList<DisplayData> tableData = FXCollections.observableArrayList();

            // 指定されたカンバン階層IDのカンバン情報一覧を取得する。
            List<KanbanInfoEntity> kanbans = kanbanInfoFacade.findByParentId(parentId, kanbanName, modelName, productNo, workflowName, workName, scheduleStartDay, scheduleEndDay, selectStatusData);

            List<DisplayData> displayDatas = new LinkedList();
            if (!kanbans.isEmpty()) {
                kanbans.stream().forEach((e) -> {
                    if (Objects.nonNull(e.getFkUpdatePersonId())) {
                        OrganizationInfoEntity person = CacheUtils.getCacheOrganization(e.getFkUpdatePersonId());
                        if (Objects.nonNull(person)) {
                            e.setUpdatePerson(person.getOrganizationName());
                        }
                    }

                    displayDatas.add(new DisplayData(e, false));
                });
            }

            // 未承認フィルターの適用
            List<DisplayData> filteredList = createFilteredList(displayDatas);
            tableData.addAll(filteredList);

            Platform.runLater(() -> {
                this.kanbanMasterList.clear();
                this.kanbanMasterList.addAll(filteredList);

                clearKanbanList();
                kanbanList.setItems(tableData);
                kanbanList.getSortOrder().add(kanbanNameColumn);
                selectedKanbanItem();
                if (!Objects.isNull(selectedKanbanList)) {
                    selectedListKanbanItem();
                    kanbanList.requestFocus();
                }              
            });

            //フィルター条件の更新
            //this.saveProperties();
            if (Objects.nonNull(scheduleStartDay)) {
                Integer differenceStateDate = DateUtils.differenceOfDate(formatter.format(scheduleStartDay), formatter.format(new Date()));
                properties.setProperty(SEARCH_FILTER_START_DATE, differenceStateDate.toString());
            } else {
                properties.setProperty(SEARCH_FILTER_START_DATE, "");
            }
            if (Objects.nonNull(scheduleEndDay)) {
                Integer differenceEndDate = DateUtils.differenceOfDate(formatter.format(scheduleEndDay), formatter.format(new Date()));
                properties.setProperty(SEARCH_FILTER_END_DATE, differenceEndDate.toString());
            } else {
                properties.setProperty(SEARCH_FILTER_END_DATE, "");
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンリストの初期化
     *
     */
    private void clearKanbanList() {
        kanbanList.getItems().clear();
        kanbanList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        kanbanList.getSelectionModel().clearSelection();
    }

    /**
     * カンバン操作ボタンの有効状態を設定する。
     */
    private void setStateKanbanButtons() {
        List<DisplayData> datas = kanbanList.getSelectionModel().getSelectedItems();
        if (Objects.nonNull(datas) && !datas.isEmpty()) {
            boolean isDisabledSingle = false;
            boolean isDisabledMulti = false;

            boolean isEdit = loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN);
        
            boolean isMulti;
            if (datas.size() == 1) {
                // 1件選択
                isMulti = false;
                if (!isEdit) {
                    isDisabledSingle = true;
                    isDisabledMulti = true;
                }
            } else {
                // 複数選択
                isMulti = true;
                isDisabledSingle = true;
                if (!isEdit) {
                    isDisabledMulti = true;
                }
            }
            
            // 作業完了ステータス以外を含んているかを確認
            boolean workEndStatus = checkKanbanStatus(datas, "作業完了");

            if (isDisabledSingle) {
                Platform.runLater(() -> {
                    this.editKanbanButton.setDisable(true);
                    this.moveKanbanButton.setDisable(false);
                    this.changeTracebilityButton.setDisable(true);
                    this.repairButton.setDisable(true);
                });
            } else {
                Platform.runLater(() -> {
                    this.editKanbanButton.setDisable(false);
                    this.moveKanbanButton.setDisable(false);
                    this.changeTracebilityButton.setDisable(false);

                    if (this.repairButton.isVisible()) {
                        List<KanbanStatusEnum> statuses = Arrays.asList(KanbanStatusEnum.PLANNING, KanbanStatusEnum.PLANNED);
                        this.repairButton.setDisable(statuses.contains(datas.get(0).getEntity().getKanbanStatus()));
                    }
                });
            }

            if (isDisabledMulti) {
                Platform.runLater(() -> {
                    this.deleteKanbanButton.setDisable(true);
                    this.checkDataOutButton.setDisable(!workEndStatus);
                    this.ledgerOutButton.setDisable(false); // カンバンが選択されていれば許可する 2023/11/27
                    this.changePlansButton.setDisable(true);
                    this.changeStatusButton.setDisable(true);
                    this.reportListButton.setDisable(false); // カンバンが選択されていれば許可する 2023/11/27
                    this.collectivelyOutputButton.setDisable(!isMulti); // カンバンが選択されていれば許可する 2023/11/27
                    this.approveButton.setDisable(true);
                });
            } else {
                Platform.runLater(() -> {
                    this.deleteKanbanButton.setDisable(false);
                    this.checkDataOutButton.setDisable(!workEndStatus);
                    this.ledgerOutButton.setDisable(false);
                    this.changePlansButton.setDisable(false);
                    this.changeStatusButton.setDisable(false);
                    this.reportListButton.setDisable(false);
                    this.approveButton.setDisable(false);

                    if (isMulti) {
                        this.collectivelyOutputButton.setDisable(false);
                    } else {
                        this.collectivelyOutputButton.setDisable(true);
                    }
                });
            }
        } else {
            // 未選択
            Platform.runLater(() -> {
                this.editKanbanButton.setDisable(true);
                this.deleteKanbanButton.setDisable(true);
                this.moveKanbanButton.setDisable(true);
                this.collectivelyOutputButton.setDisable(true);
                this.repairButton.setDisable(true);
                this.checkDataOutButton.setDisable(true);
                this.ledgerOutButton.setDisable(true);
                this.changeStatusButton.setDisable(true);  // 2019/12/25 カンバン操作対応 ステータス変更ボタンの追加
                this.reportListButton.setDisable(true);
                this.changeTracebilityButton.setDisable(true);
                this.approveButton.setDisable(true);
            });
        }
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

    /**
     *
     * @param obj
     * @param flg
     */
    private void blockUI(Object obj, Boolean flg) {
        if (flg) {
            blockUIs.add(obj);
        } else {
            blockUIs.remove(obj);
        }

        blockUI(!blockUIs.isEmpty());
    }

    /**
     * 再接続処理
     *
     */
    public void reconnection() {
        Platform.runLater(() -> {
            sc.showAlert(Alert.AlertType.ERROR, null, LocaleUtils.getString("key.ServerReconnectMessage"));
            sc.setComponent("ContentNaviPane", "KanbanListCompo");
        });
    }

    /**
     *
     * @param uri
     * @return
     */
    public static long getUriToHierarcyId(String uri) {
        long ret = 0;
        try {
            String[] split = uri.split(URI_SPLIT);
            if (split.length == 3) {
                ret = Long.parseLong(split[2]);
            }
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return ret;
    }

    /**
     *
     * @param uri
     * @return
     */
    public static long getUriToKanbanId(String uri) {
        long ret = 0;
        try {
            String[] split = uri.split(URI_SPLIT);
            if (split.length == 2) {
                ret = Long.parseLong(split[1]);
            }
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return ret;
    }

    /**
     * 計画変更ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onChangePlans(ActionEvent event) {
        logger.info("onChangePlans start.");
        boolean isCancel = true;
        Object obj = new Object();
        try {
            blockUI(obj, true);

            List<DisplayData> datas = kanbanList.getSelectionModel().getSelectedItems();
            if (Objects.isNull(datas) || datas.isEmpty()) {
                return;
            }

            final PlanChangeCondition condition = new PlanChangeCondition(new Date(), DateUtils.min(), DateUtils.min());

            // 計画変更ダイアログを表示する。
            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.ChangePlans"), "PlanChangeDialog", condition);
            if (!ButtonType.OK.equals(ret)) {
                return;
            }

            // 選択したカンバン一覧から、カンバンID一覧を作成する。
            final List<Long> kanbanIds = datas.stream().map(p -> p.getId()).collect(Collectors.toList());

            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
                    // 休日情報がキャッシュされていない場合は読み込む。
                    CacheUtils.createCacheHoliday(true);

                    // カンバンの計画時間を変更する。
                    return kanbanPlanChange(condition, kanbanIds);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // 処理結果
                        ResponseEntity res = this.getValue();
                        if (Objects.isNull(res) || Objects.isNull(res.getErrorType())) {
                            return;
                        }

                        if (res.isSuccess()) {
                            sc.showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.ChangePlans"), LocaleUtils.getString("key.ChangedKanbanPlan"));
                        } else {
                            DialogBox.alert(res.getErrorType());
                        }
                    } finally {
                        blockUI(obj, false);
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
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ChangePlans"), LocaleUtils.getString("key.alert.systemError"));
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                    }
                }
            };
            new Thread(task).start();

            isCancel = false;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            if (isCancel) {
                blockUI(obj, false);
            }
        }
    }

    /**
     * カンバンの計画時間を変更する。(規定件数づつ処理)
     *
     * @param condition
     * @param kanbanIds
     * @return
     */
    private ResponseEntity kanbanPlanChange(PlanChangeCondition condition, List<Long> kanbanIds) {
        ResponseEntity result = null;

        for (int fromIndex = 0; fromIndex < kanbanIds.size(); fromIndex += RANGE_IDS) {
            int toIndex = fromIndex + RANGE_IDS;
            if (toIndex > kanbanIds.size()) {
                toIndex = kanbanIds.size();
            }

            List<Long> ids = kanbanIds.subList(fromIndex, toIndex);

            // カンバンの計画時間を変更する。
            result = kanbanInfoFacade.planChange(condition, ids, loginUserInfoEntity.getId());
            if (!result.isSuccess()) {
                break;
            }
        }

        // カンバン一覧表示を更新する。
        searchKanbanData(false);

        return result;
    }

    // 2019/12/25 カンバン操作対応 ステータス変更ボタンの追加
    /**
     * ステータス変更ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onChangeStatus(ActionEvent event) {
        logger.info("onChangeStatus start.");
        boolean isCancel = true;
        Object obj = new Object();
        try {
            blockUI(obj, true);

            List<DisplayData> datas = kanbanList.getSelectionModel().getSelectedItems();
            if (Objects.isNull(datas) || datas.isEmpty()) {
                return;
            }

            //  ダイアログで選択したステータス設定用
            StatusChange statusChange = new StatusChange();

            // ステータス変更ダイアログを表示する。
            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.ChangeStatus"), "StatusChangeDialog", statusChange);
            if (!ButtonType.OK.equals(ret)) {
                return;
            }

            // 選択したカンバン一覧から、カンバンID一覧を作成する。
            final List<Long> kanbanIds = datas.stream().map(p -> p.getId()).collect(Collectors.toList());

            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
                    // カンバンのステータスを変更する。
                    return kanbanStatusChange(statusChange, kanbanIds);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // 処理結果
                        ResponseEntity res = this.getValue();
                        if (Objects.isNull(res) || Objects.isNull(res.getErrorType())) {
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ChangeStatus"), String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.Status")));
                            return;
                        }

                        if (res.isSuccess()) {
                            if (ServerErrorTypeEnum.NOT_SOME_UPDATED.equals(res.getErrorType())) {
                                // 「一部の情報が更新されませんでした。」
                                sc.showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.ChangeStatus"), LocaleUtils.getString("key.SomeNotUpdated"));
                            } else {
                                sc.showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.ChangeStatus"), String.format(LocaleUtils.getString("key.Changed"), LocaleUtils.getString("key.Status")));
                            }
                        } else {
                            DialogBox.alert(res.getErrorType());
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ChangeStatus"), String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.Status")));
                        }
                    } finally {
                        blockUI(obj, false);
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
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ChangeStatus"), String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.Status")));
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                    }
                }
            };
            new Thread(task).start();

            isCancel = false;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            if (isCancel) {
                blockUI(obj, false);
            }
        }
    }

    /**
     * カンバンのステータスを変更する。
     *
     * @param statusChange ステータス変更パラメーター
     * @param kanbanIds ステータスを変更するカンバンのIDリスト
     * @param forced true:強制的にステータスを変更する、false:安全にステータスを変更する
     * @return 処理結果
     */
    private ResponseEntity kanbanStatusChange(StatusChange statusChange, List<Long> kanbanIds) {
        logger.info("kanbanStatusChange: statusChange={}", statusChange);

        if (Objects.isNull(statusChange.getNewStatus())) {
            return null;
        }

        if (KanbanStatusEnum.PLANNING.equals(statusChange.getNewStatus()) && !statusChange.isForced()) {
            // 計画中の場合、強制フラグがオンになっている必要性がある
            return null;
        }

        ResponseEntity result = null;
        for (int fromIndex = 0; fromIndex < kanbanIds.size(); ) {
            final int toIndex = Math.min(kanbanIds.size(), fromIndex+RANGE_IDS);

            // カンバンのステータスを変更する。
            result = kanbanInfoFacade.updateStatus(kanbanIds.subList(fromIndex, toIndex), statusChange.getNewStatus(), statusChange.isForced(), loginUserInfoEntity.getId());
            if (!result.isSuccess()) {
                break;
            }

            fromIndex=toIndex;
        }

        // カンバン一覧表示を更新する。
        searchKanbanData(false);

        return result;
    }

    /**
     * 帳票一覧ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onReportListButton(ActionEvent event) {
        logger.info("onReportListButton start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);

            List<DisplayData> datas = kanbanList.getSelectionModel().getSelectedItems();
            if (Objects.isNull(datas) || datas.isEmpty()) {
                return;
            }
            // 選択したカンバン一覧から、カンバン一覧を作成する。
            final List<KanbanInfoEntity> kanbans = datas.stream().map(p -> p.getEntity()).collect(Collectors.toList());
            if (Objects.isNull(kanbans)) {
                return;
            }

            // 帳票一覧ダイアログを表示する。
            sc.showDialog(LocaleUtils.getString("key.ReportList"), "ReportListDialog", kanbans);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(obj, false);
        }
    }

    /**
     * 品質データボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onChangeTracebilityButton(ActionEvent event) {
        logger.info("onChangeTracebilityButton start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);
            DisplayData data = kanbanList.getSelectionModel().getSelectedItem();
            if (Objects.isNull(data)) {
                return;
            }
            KanbanInfoEntity kanban = getKanban(data.getId());

            // 品質データダイアログを表示する。
            sc.showDialog(LocaleUtils.getString("key.QualityData"), "TraceabilityChangeDialog", kanban, sc.getStage(), true);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(obj, false);
        }
    }

    /**
     * まとめて帳票出力ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onCollectivelyOutput(ActionEvent event) {
        logger.info("onCollectivelyOutput");
        boolean isCancel = true;
        try {
            blockUI(true);

            Properties props = AdProperty.getProperties(Constants.PROPERTY_NAME);

            // 出力先
            String reportFolder = props.getProperty(Constants.COLLECTIVE_REPORT_FOLDER, null);
            // 置換されなかったタグを残す
            boolean reportLeaveTags = Boolean.valueOf(props.getProperty(Constants.COLLECTIVE_REPORT_LEAVE_TAGS, "true"));
            // pdfにて出力する
            boolean exportAsPdf = Boolean.valueOf(props.getProperty(Constants.EXPORT_AS_PDF_TAGS, "false"));

            // 帳票出力条件
            ReportOutputCondition condition = new ReportOutputCondition();
            condition.setOutputFolder(reportFolder);
            condition.setLeaveTags(reportLeaveTags);
            condition.setExportAsPdf(exportAsPdf);

            // 工程順が単一か混在かをチェックする
            // 選択しているカンバンリスト
            List<DisplayData> kanbanSelected = kanbanList.getSelectionModel().getSelectedItems();
            // 工程順単一判定フラグを初期化
            this.isWorkflowSingle = true;
            // リストの先頭の工程順IDを取得
            Long tempWorkflowId = kanbanSelected.get(0).getWorkflowId();;

            for (DisplayData data : kanbanSelected) {
                if (!tempWorkflowId.equals(data.getWorkflowId())) {
                    this.isWorkflowSingle = false;
                    break;
                }
            }
            if (this.isWorkflowSingle) {
                // 帳票出力ダイアログ(帳票選択あり)を表示
                ArrayList<String> fileList = new ArrayList<>();
                WorkflowInfoEntity workflow = CacheUtils.getCacheWorkflow(tempWorkflowId);
                for (SimpleStringProperty pathProp : workflow.getLedgerPathPropertyCollection()) {
                    File templateFile = new File(pathProp.getValue());
                    fileList.add(templateFile.getName());
                }
                condition.setTemplateNames(fileList);
                ButtonType ret = sc.showDialog(LocaleUtils.getString("key.OutLedgerTitle"), "ReportOutputChoiceDialog", condition);
                if (!ButtonType.OK.equals(ret)) {
                    return;
                }
            } else {
                // 帳票出力ダイアログ(帳票選択なし)を表示
                condition.setTemplateNames(null);
                ButtonType ret = sc.showDialog(LocaleUtils.getString("key.OutLedgerTitle"), "ReportOutputDialog", condition);
                if (!ButtonType.OK.equals(ret)) {
                    return;
                }
            }

            props.setProperty(Constants.COLLECTIVE_REPORT_FOLDER, condition.getOutputFolder());
            props.setProperty(Constants.COLLECTIVE_REPORT_LEAVE_TAGS, String.valueOf(condition.isLeaveTags()));
            props.setProperty(Constants.EXPORT_AS_PDF_TAGS, String.valueOf(exportAsPdf));

            final Path outputDir = Paths.get(condition.getOutputFolder());
            final boolean isRemoveTag = !condition.isLeaveTags();
            final boolean isExportAsPdf = condition.isExportAsPdf();

            // 出力対象のテンプレートファイル名リストをセットする 
            this.checkTemplateList = condition.getTemplateNames();
            final List<DisplayData> selected = new ArrayList(kanbanList.getSelectionModel().getSelectedItems());

            // 対象をカンバン名順にソートする。
            selected.sort(Comparator.comparing(item -> item.getKanbanName()));

            Task task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    return collectivelyOutputThread(outputDir, selected, isRemoveTag, isExportAsPdf);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // 帳票ファイルを開ける結果ダイアログを表示する。
                        FileOpenDialog fileOpenDialog = new FileOpenDialog(LocaleUtils.getString("key.CollectivelyOutput"), this.getValue(), outputFilePathList, reportInfos);
                        fileOpenDialog.start(new Stage());
                    } finally {
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
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.CollectivelyOutput"), LocaleUtils.getString("key.alert.systemError"));
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };
            new Thread(task).start();

            isCancel = false;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }
    }

    /**
     * 検査結果の検索ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onInspectionSearch(ActionEvent event) {
        try {
            blockUI(true);

            TraceabilitySearchCondition condition = new TraceabilitySearchCondition();
            condition.setKanbanName(this.kanbanNameField.getText());
            condition.setModelName(this.modelNameField.getText());

            // 検査結果の検索ダイアログを表示する。
            sc.showDialog(LocaleUtils.getString("key.InspectionSearch"), "InspectionSearchDialog", condition);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * まとめて帳票出力
     *
     * @param outputDir 出力フォルダ
     * @param selected 選択データ一覧
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @return 結果文字列
     * @throws Exception
     */
    private String collectivelyOutputThread(Path outputDir, List<DisplayData> selected, boolean isRemoveTag, boolean isExportAsPdf) throws Exception {
        Path resultFile = Paths.get(outputDir.toString(), "result.txt");
        Boolean isCompleteSuccess = true;
        String message;

        if (Files.exists(resultFile)) {
            Files.delete(resultFile);
        }

        double startTime = System.nanoTime();

        ReportTagFactory reportTagFactory = new ReportTagFactory();

        // 出力日時
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        String dateString = df.format(now);

        this.outputFilePathList.clear();
        // 保持している出力帳票情報のリストをクリアする
        reportInfos.clear();

        // テンプレートとカンバンNoリストのマップ
        Map<File, List<Integer>> templateKanbanMap = new HashMap();
        // カンバンNoとタグ一覧のマップ
        Map<Integer, Map<String, Object>> kanbanTagMaps = new HashMap();
        // カンバンNoとカンバンIDのマップ
        Map<Integer, Long> kanbanIdMap = new HashMap();

        // 選択したカンバンのカンバンID一覧
        List<Long> kanbanIds = selected.stream()
                .map(p -> p.getId())
                .collect(Collectors.toList());

        final Comparator<ActualResultEntity> dateComparator = (p1, p2) -> p1.getImplementDatetime().compareTo(p2.getImplementDatetime());
        final Comparator<ActualResultEntity> actualIDComparator = (p1, p2) -> p1.getActualId().compareTo(p2.getActualId());
        try {
            // 選択したカンバンの工程実績をまとめて取得する。
            List<ActualResultEntity> actuals = this.getActualResults(kanbanIds);

            if (Objects.nonNull(actuals)) {
                // 選択したカンバンのタグマップを作成する。
                int kanbanNo = 1;
                for (DisplayData data : selected) {
                    if (Objects.isNull(data)) {
                        continue;
                    }

                    // カンバンを取得する。
                    KanbanInfoEntity kanban = this.getKanban(data.getId());
                    if (Objects.isNull(kanban)) {
                        // 帳票出力結果ファイルを出力する。
                        message = LocaleUtils.getString("key.ServerReconnectMessage");
                        this.outputRelustFile(resultFile, data.getKanbanName(), data.getWorkflowName(), null, message);
                        continue;
                    }

                    if (Objects.isNull(kanban.getWorkKanbanCollection())) {
                        kanban.setWorkKanbanCollection(this.getWorkKanbans(kanban.getKanbanId()));
                    }

                    if (Objects.isNull(kanban.getSeparateworkKanbanCollection())) {
                        kanban.setWorkKanbanCollection(this.getSeparateWorkKanbans(kanban.getKanbanId()));
                    }

                    if (Objects.isNull(kanban.getWorkKanbanCollection())
                            || Objects.isNull(kanban.getSeparateworkKanbanCollection())) {
                        // 帳票出力結果ファイルを出力する。
                        message = LocaleUtils.getString("key.ServerReconnectMessage");
                        this.outputRelustFile(resultFile, data.getKanbanName(), data.getWorkflowName(), null, message);
                        continue;
                    }

                    // 工程順を取得する。
                    WorkflowInfoEntity workflow = CacheUtils.getCacheWorkflow(kanban.getFkWorkflowId());
                    if (Objects.isNull(workflow)) {
                        // 帳票出力結果ファイルを出力する。
                        message = LocaleUtils.getString("key.ServerReconnectMessage");
                        this.outputRelustFile(resultFile, data.getKanbanName(), data.getWorkflowName(), null, message);
                        continue;
                    }

                    // 帳票テンプレートファイルパスを取得する。
                    List<SimpleStringProperty> templatePaths = workflow.getLedgerPathPropertyCollection();
                    if (Objects.isNull(templatePaths) || templatePaths.isEmpty()) {
                        continue;
                    }

                    // カンバンの工程実績
                    List<ActualResultEntity> kanbanActuals = actuals.stream()
                            .filter(p -> Objects.equals(p.getFkKanbanId(), kanban.getKanbanId()))
                            .sorted(dateComparator.thenComparing(actualIDComparator))
                            .collect(Collectors.toList());
                    kanban.setActualResultCollection(kanbanActuals);

                    // トレーサビリティDB使用時は、トレーサビリティDBからデータを取得する。
                    List<TraceabilityEntity> traces = null;
                    if (this.isTraceabilityDBEnabled) {
                        traces = this.traceabilityFacade.findKanbanTraceability(data.getId());
                    }
 
                    kanbanIdMap.put(kanbanNo, data.getId());

                    final LedgerTagCase ledgerTagCase
                            = StringUtils.equals(AdProperty.getProperties().getProperty(LedgerTagCase.name, InsensitiveCaseLedgerTag.name), NaturalCaseLedgerTag.name)
                            ? NaturalCaseLedgerTag.instance
                            : InsensitiveCaseLedgerTag.instance;

                    // カンバンのタグマップを取得する。
                    Map<String, Object> tagMap = reportTagFactory.createTagMap(kanban, workflow, traces, useExtensionTag, useQRCode, ledgerTagCase);

                    // 部品トレース
                    if (this.enablePartsTrace) {
                        // 使用部品一覧
                        List<AssemblyPartsInfoEntity> partsInfos = this.findAssemblyParts(kanban.getKanbanName());
                        tagMap.putAll(reportTagFactory.createReplaceDataAssemblyParts(partsInfos, ledgerTagCase));
                    }

                    kanbanTagMaps.put(kanbanNo, tagMap);

                    for (SimpleStringProperty templatePath : templatePaths) {
                        File file = new File(templatePath.getValue());
                        if (!file.exists() || !file.isFile()) {
                            isCompleteSuccess = false;

                            // 帳票出力結果ファイルを出力する。
                            message = LocaleUtils.getString("key.KanbanOutLedgerPassErr");
                            this.outputRelustFile(resultFile, data.getKanbanName(), data.getWorkflowName(), file.getPath(), message);
                            continue;
                        }
                        // 工程順の単一選択時は帳票テンプレート出力可否チェックを実施する。
                        if (!this.workflowSingleOutputCheck(file.getName())) {
                            // 結果がfalseの場合は出力せずにスキップ
                            continue;
                        }

                        if (templateKanbanMap.containsKey(file)) {
                            templateKanbanMap.get(file).add(kanbanNo);
                        } else {
                            templateKanbanMap.put(file, new ArrayList(Arrays.asList(kanbanNo)));
                        }
                    }

                    kanbanNo++;
                }

                // 帳票テンプレート毎にタグを置換してファイル出力する。
                for (Map.Entry<File, List<Integer>> item : templateKanbanMap.entrySet()) {
                    File templateFile = item.getKey();

                    // 帳票テンプレート名(拡張子なし)
                    String templateName = templateFile.getName().substring(0, templateFile.getName().lastIndexOf('.'));
                    // 帳票テンプレートの拡張子(.～)
                    String templateExt = templateFile.toString().substring(templateFile.toString().lastIndexOf("."));

                    // 出力ファイル名
                    String outputFileName = new StringBuilder()
                            .append(templateName)
                            .append("_")
                            .append(dateString)
                            .append(templateExt)
                            .toString();

                    // 出力ファイルパス
                    Path outputFile = Paths.get(outputDir.toString(), outputFileName);

                    final LedgerTagCase ledgerTagCase
                            = StringUtils.equals(AdProperty.getProperties().getProperty(LedgerTagCase.name, InsensitiveCaseLedgerTag.name), NaturalCaseLedgerTag.name)
                            ? NaturalCaseLedgerTag.instance
                            : InsensitiveCaseLedgerTag.instance;


                    // タグを置換してファイル出力する。
                    ReplaceTagResult replaceTagResult = reportTagFactory.replaceTags(templateFile, outputFile.toFile(), item.getValue(), kanbanTagMaps, kanbanIdMap, isRemoveTag, ledgerTagCase);

                    isCompleteSuccess &= replaceTagResult.isSuccess();

                    if (replaceTagResult.isSuccess()) {
                        // 置換できなかったタグがある場合、結果ファイルに出力する。
                        List<String> faildReplaceTags = new LinkedList(replaceTagResult.getFaildReplaceTags());
                        if (!faildReplaceTags.isEmpty()) {
                            isCompleteSuccess = false;

                            StringBuilder sb = new StringBuilder();
                            sb.append(LocaleUtils.getString("key.KanbanOutLedgerNoReplaceComp"));
                            sb.append(" - ");
                            int showTagSize = replaceTagResult.getFaildReplaceTags().size();
                            for (int index = 0; showTagSize > index; index++) {
                                if (index > 0) {
                                    sb.append(",");
                                }
                                sb.append(faildReplaceTags.get(index));
                            }
                            message = sb.toString();

                            // 帳票出力結果ファイルを出力する。
                            this.outputRelustFile(resultFile, null, null, outputFile.toString(), message);
                        }

                        if (isExportAsPdf) {
                            String excelPath = outputFile.toString();
                            String pdfPath = excelPath.substring(0, excelPath.lastIndexOf('.')) + ".pdf";
                            if (convertExcelToPdf(excelPath, pdfPath)) {
                                Files.delete(outputFile);
                                outputFile = Paths.get(pdfPath);
                            } else {
                                outputRelustFile(resultFile, null, null, outputFile.toString(), LocaleUtils.getString("key.KanbanOutLedgerPdfFail"));
                                isCompleteSuccess = false;
                                continue;
                            }
                        }
                    }

                    // 結果ダイアログからExcelファイルを開くため、出力したファイルパスを記憶しておく。
                    if (!this.outputFilePathList.contains(outputFile.toAbsolutePath())) {
                        this.outputFilePathList.add(outputFile.toAbsolutePath());
                    }

                    for (Long kanbanId : kanbanIdMap.values()) {
                        // カンバン帳票情報作成
                        KanbanReportInfoEntity info = new KanbanReportInfoEntity();
                        info.setKanbanId(kanbanId);
                        info.setFilePath(outputFile.toAbsolutePath().toString());
                        info.setOutputDate(now);
                        info.setTemplateName(templateName);
                        info.setReportType(ReportTypeEnum.MULTIPLE_REPORT);
                        reportInfos.add(info);
                    }
                }
            } else {
                // 帳票出力結果ファイルを出力する。
                message = LocaleUtils.getString("key.ServerReconnectMessage");
                this.outputRelustFile(resultFile, null, null, null, message);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            // 帳票出力結果ファイルを出力する。
            message = new StringBuilder(LocaleUtils.getString("key.KanbanOutLedgerApplicationErr"))
                    .append(" - ")
                    .append(ex.getMessage())
                    .toString();
            this.outputRelustFile(resultFile, null, null, null, message);
        }

        // 処理時間を結果ファイルに出力する。
        double elapsedSec = (System.nanoTime() - startTime) / Math.pow(10, 9);
        double elapsedMinute = Math.floor(elapsedSec / 60);
        elapsedSec -= elapsedMinute * 60;
        double elapsedHour = Math.floor(elapsedMinute / 60);
        elapsedMinute -= elapsedHour * 60;

        try (BufferedWriter bw = Files.newBufferedWriter(resultFile, Charset.defaultCharset(),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            bw.append("(" + String.format("%dh %dm %.2fs", (long) elapsedHour, (long) elapsedMinute, elapsedSec) + ")");
            bw.newLine();
        }

        // 結果メッセージ
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(LocaleUtils.getString("key.Completed"), LocaleUtils.getString("key.CollectivelyOutput")));
        if (isCompleteSuccess) {
            Files.delete(resultFile);
        } else {
            sb.append("\r\n\r\n");
            sb.append(String.format(LocaleUtils.getString("key.KanbanOutLedgerResultInfo"), resultFile.toAbsolutePath().toString()));
        }

        return sb.toString();
    }

    /**
     * 帳票出力結果ファイルを出力する。
     *
     * @param path 帳票出力結果ファイルパス
     * @param kanbanName カンバン名
     * @param workflowName 工程順名
     * @param template 帳票テンプレート
     * @param message メッセージ
     */
    private void outputRelustFile(Path path, String kanbanName, String workflowName, String template, String message) {
        try (BufferedWriter bw = Files.newBufferedWriter(path, Charset.defaultCharset(),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            if (Objects.nonNull(kanbanName) && Objects.nonNull(workflowName)) {
                bw.append(kanbanName);
                bw.append("(").append(workflowName).append("):");
            }

            if (Objects.nonNull(template) && !template.isEmpty()) {
                bw.append(" [").append(template).append("] ");
            }

            bw.append(message);
            bw.newLine();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 前回の検索条件を読み込む。
     */
    private void loadProperties() {
        logger.info("loadProperties");
        try {
            String confFile = new StringBuilder(Constants.PROPERTY_NAME)
                    .append(Constants.PROPERTIES_EXT)
                    .toString();

            String path = new StringBuilder(System.getenv("ADFACTORY_HOME")).append(File.separator).append("conf").toString();

            boolean isNewFile = false;
            File file = new File(path, confFile);
            if (!file.exists()) {
                isNewFile = true;
            }

            AdProperty.load(Constants.PROPERTY_NAME, confFile);
            Properties props = AdProperty.getProperties(Constants.PROPERTY_NAME);

            if (isNewFile) {
                // 保存する。
                AdProperty.store(Constants.PROPERTY_NAME);
            }

            // カンバン名の選択
            boolean kanbanSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_KANBAN_NAME_SELECTED, String.valueOf(false)));
            this.kanbanNameField.setSelected(kanbanSelected);

            // カンバン名
            String kanbanName = props.getProperty(Constants.SEARCH_KANBAN_NAME);
            this.kanbanNameField.setText(kanbanName);

            // 機種名の選択
            boolean modelNameSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_MODEL_NAME_SELECTED, String.valueOf(false)));
            this.modelNameField.setSelected(modelNameSelected);

            // 機種名
            String modelName = props.getProperty(Constants.SEARCH_MODEL_NAME);
            this.modelNameField.setText(modelName);

            // 製造番号の選択
            boolean productNumSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_PRODUCT_NUM_SELECTED, String.valueOf(false)));
            this.productNoField.setSelected(productNumSelected);

            // 製造番号
            String productNum = props.getProperty(Constants.SEARCH_PRODUCT_NUM);
            this.productNoField.setText(productNum);

            // 工程順名の選択
            boolean workflowNameSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_WORKFLOW_NAME_SELECTED, String.valueOf(false)));
            this.workflowNameField.setSelected(workflowNameSelected);

            // 工程順名
            String workflowName = props.getProperty(Constants.SEARCH_WORKFLOW_NAME);
            this.workflowNameField.setText(workflowName);

            // 工程名の選択
            boolean workNameSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_WORK_NAME_SELECTED, String.valueOf(false)));
            this.workNameField.setSelected(workNameSelected);

            // 工程名
            String workName = props.getProperty(Constants.SEARCH_WORK_NAME);
            this.workNameField.setText(workName);

            // ステータスの選択
            boolean statusSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_STATUS_SELECTED, String.valueOf(false)));
            this.statusList.setSelected(statusSelected);

            // ステータス
            String propStatus = props.getProperty(Constants.SEARCH_STATUS, "");
            String[] statuses = propStatus.split(",");
            IndexedCheckModel<String> cm = this.statusList.getCheckModel();
            for (String status : statuses) {
                if (status.trim().isEmpty()) {
                    continue;
                }
                cm.check(LocaleUtils.getString(KanbanStatusEnum.getEnum(status).getResourceKey()));
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 検索条件を保存する。
     */
    private void saveProperties() {
        logger.info("seveProperties");
        try {
            Properties props = AdProperty.getProperties(Constants.PROPERTY_NAME);

            // カンバン名の選択
            props.setProperty(Constants.SEARCH_KANBAN_NAME_SELECTED, String.valueOf(this.kanbanNameField.isSelected()));

            // カンバン名
            String propKanbanName = this.kanbanNameField.getText();
            if (Objects.isNull(propKanbanName)) {
                propKanbanName = "";
            }
            props.setProperty(Constants.SEARCH_KANBAN_NAME, propKanbanName);

            // 機種名の選択
            props.setProperty(Constants.SEARCH_MODEL_NAME_SELECTED, String.valueOf(this.modelNameField.isSelected()));

            // 機種名
            String propModelName = this.modelNameField.getText();
            if (Objects.isNull(propModelName)) {
                propModelName = "";
            }
            props.setProperty(Constants.SEARCH_MODEL_NAME, propModelName);

            // 製造番号の選択
            props.setProperty(Constants.SEARCH_PRODUCT_NUM_SELECTED, String.valueOf(this.productNoField.isSelected()));

            // 製造番号
            String propProductNum = this.productNoField.getText();
            if (Objects.isNull(propProductNum)) {
                propProductNum = "";
            }
            props.setProperty(Constants.SEARCH_PRODUCT_NUM, propProductNum);

            // 工程順名の選択
            props.setProperty(Constants.SEARCH_WORKFLOW_NAME_SELECTED, String.valueOf(this.workflowNameField.isSelected()));

            // 工程順名
            String propWorkflowName = this.workflowNameField.getText();
            if (Objects.isNull(propWorkflowName)) {
                propWorkflowName = "";
            }
            props.setProperty(Constants.SEARCH_WORKFLOW_NAME, propWorkflowName);

            // 工程名の選択
            props.setProperty(Constants.SEARCH_WORK_NAME_SELECTED, String.valueOf(this.workNameField.isSelected()));

            // 工程順名
            String propWorkName = this.workNameField.getText();
            if (Objects.isNull(propWorkName)) {
                propWorkName = "";
            }
            props.setProperty(Constants.SEARCH_WORK_NAME, propWorkName);


            // ステータスの選択
            props.setProperty(Constants.SEARCH_STATUS_SELECTED, String.valueOf(this.statusList.isSelected()));

            // ステータス
            String propStatus = "";
            List<String> statuses = new ArrayList();
            ObservableList<Integer> indices = this.statusList.getCheckModel().getCheckedIndices();
            if (Objects.nonNull(this.statusList.getStatus())) {
                indices.stream().forEach(item -> {
                    statuses.add(KanbanStatusEnum.getValueText(item));
                });
                propStatus = String.join(",", statuses);
            }
            props.setProperty(Constants.SEARCH_STATUS, propStatus);

            // 保存する。
            AdProperty.store(Constants.PROPERTY_NAME);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * キャッシュに情報を読み込み、組織ツリーを更新する。
     */
    private void updateTree(boolean isRefresh) {
        logger.info("updateTree start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if (isRefresh) {
                        CacheUtils.removeCacheData(EquipmentInfoEntity.class);
                        CacheUtils.removeCacheData(OrganizationInfoEntity.class);
                        CacheUtils.removeCacheData(HolidayInfoEntity.class);
                        CacheUtils.removeCacheData(LabelInfoEntity.class);
                    }

                    CacheUtils.createCacheEquipment(true);
                    CacheUtils.createCacheOrganization(true);
                    CacheUtils.createCacheHoliday(true);
                    CacheUtils.createCacheLabel(true);
                    CacheUtils.createCacheKanbanHierarchy(true);

                    // 「拡張フラグを使用するか」設定値の取得
                    useExtensionTag = Boolean.valueOf(AdProperty.getProperties().getProperty(Constants.USE_EXTENSION_TAG, Constants.USE_EXTENSION_TAG_DEFAULT));

                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        if (Objects.isNull(kanbanEditPermanenceData.getKanbanHierarchyRootItem())) {
                            createRoot(null);
                        } else {
                            hierarchyTree.rootProperty().setValue(kanbanEditPermanenceData.getKanbanHierarchyRootItem());
                            if (isRefresh) {
                                TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
                                createRoot(item.getValue().getKanbanHierarchyId());
                            } else {
                                selectedTreeItem(kanbanEditPermanenceData.getSelectedKanbanHierarchy(), null);
                            }
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                        logger.info("updateTree end.");
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
                        blockUI(obj, false);
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
    }

    /**
     * フィルタリスト作成
     */
    private List<DisplayData> createFilteredList(List<DisplayData> list) {

        List<DisplayData> filteredList = new ArrayList<>();
        for (DisplayData entity : list) {
            if (filteredApprove(entity.getApprove())) {
                filteredList.add(entity);
            }
        }

        return filteredList;
    }

    /**
     * 未承認フィルタ
     *
     * @param approves 承認情報
     * @return True = 未承認あり
     */
    private boolean filteredApprove(List<ApprovalEntity> approves) {
        if (approves.isEmpty() || approveFiltOrder.isEmpty()) {
            return true;
        }

        for (Integer order : approveFiltOrder) {
            ApprovalEntity orderApprove = approves.stream().filter(p -> Objects.equals(p.getOrder(), order)).findFirst().orElse(null);
            if (Objects.isNull(orderApprove) || Objects.isNull(orderApprove.getApprove())) {
                // 承認情報がない → 未承認
                return true;
            }
        }
        return false;
    }

    /**
     * カンバン詳細表示ツールチップの表示Pane作成
     *
     * @param data カンバン表示エンティティ
     * @return 表示Pane
     */
    private Pane createKanbanInfo(DisplayData data) {
        VBox vbox = new VBox();
        final double vboxWidth = 500.0;
        vbox.setMaxWidth(vboxWidth);

        // カンバン名
        vbox.getChildren().add(new Label(data.getKanbanName()));

        // ラベル名
        List<LabelInfoEntity> availableLabels = data.getLabelIds().stream()
                .map(labelId -> CacheUtils.getCacheLabel(labelId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!availableLabels.isEmpty()) {
            final String labelNames = availableLabels.stream()
                    .sorted(Comparator.comparingLong(LabelInfoEntity::getLabelPriority))
                    .map(label -> label.getLabelName())
                    .collect(Collectors.joining(Constants.KANBAN_LABELS_DELIMITER_IN_TOOLTIP));
            Label labelNamesLabel = new Label(labelNames);
            labelNamesLabel.setMinWidth(vboxWidth);
            labelNamesLabel.setMaxWidth(vboxWidth);
            labelNamesLabel.setWrapText(true);
            vbox.getChildren().add(labelNamesLabel);
        }

        // 承認情報
        try {
            List<ApprovalEntity> approves = data.getApprove();
            for (int i = 1; i <= approveNum; i++) {
                final int order = i;
                ApprovalEntity approve = approves.stream()
                        .filter(p -> Objects.equals(p.getOrder(), order))
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(approve)) {
                    continue;
                }
                if (Objects.isNull(approve.getApprove()) && StringUtils.isEmpty(approve.getReason())) {
                    // 理由なし取り消しの場合は承認情報を表示しない
                    continue;
                }

                boolean wrapFlg = false;

                HBox hbox = new HBox();
                hbox.setAlignment(Pos.TOP_LEFT);

                StringBuilder approveHeaderInfo = new StringBuilder();
                approveHeaderInfo.append(LocaleUtils.getString("key.Approve"));
                approveHeaderInfo.append(i);

                Label approveHeader = new Label(approveHeaderInfo.toString());
                approveHeader.setMinWidth(60);
                hbox.getChildren().add(approveHeader);

                StringBuilder approveInfo = new StringBuilder();
                if (Objects.isNull(approve.getApprove())) {
                    // 取り消し時
                    approveInfo.append(LocaleUtils.getString("key.Revocation"));
                } else if (approve.getApprove()) {
                    // 可
                    approveInfo.append(LocaleUtils.getString("key.ApprovalStatus"));
                } else {
                    // 否
                    approveInfo.append(LocaleUtils.getString("key.DisapprovalStatus"));
                }
                approveInfo.append(NEW_LINE);

                if (!StringUtils.isEmpty(approve.getReason())) {
                    approveInfo.append(approve.getReason());
                    approveInfo.append(NEW_LINE);
                    if (approve.getReason().length() >= 30) {
                        wrapFlg = true;
                    }
                }
                approveInfo.append(approve.getApprover());
                approveInfo.append(" ");
                approveInfo.append(APPROVE_DATE_FORMAT.format(approve.getDate()));

                Label approveData = new Label(approveInfo.toString());
                if (wrapFlg) {
                    approveData.setMinWidth(379);
                    approveData.setWrapText(true);
                }
                approveData.setMaxWidth(380);

                hbox.getChildren().add(approveData);
                vbox.getChildren().add(hbox);
            }
        } catch (Exception ex) {
        }

        return vbox;
    }

    /**
     * ツールチップを構築する。
     *
     * @param control 構築先
     * @param pane 表示内容
     */
    private void buildTooltip(Control control, DisplayData item) {
        try {
            Tooltip toolTip = TooltipBuilder.build(control);
            toolTip.setMaxWidth(500);
            toolTip.setGraphic(createKanbanInfo(item));
        } catch (Exception ex) {
        }
    }

    /**
     * カンバンID一覧を指定して、カンバン情報一覧(詳細情報付き)を取得する。
     *
     * @param kanbanIds カンバンID一覧
     * @return カンバン情報一覧
     */
    private List<KanbanInfoEntity> getKanbans(List<Long> kanbanIds) {
        try {
            List<KanbanInfoEntity> kanbans = new LinkedList();

            for (int rangeFrom = 0; rangeFrom < kanbanIds.size(); rangeFrom += KANBAN_RANGE) {
                int rangeTo = rangeFrom + KANBAN_RANGE;
                if (rangeTo > kanbanIds.size()) {
                    rangeTo = kanbanIds.size();
                }

                List<Long> rangeIds = kanbanIds.subList(rangeFrom, rangeTo);
                kanbans.addAll(kanbanInfoFacade.find(rangeIds, true));
            }

            return kanbans;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 工程順の単一選択時の帳票テンプレート出力可否チェック
     *
     * @param targetTemplate チェック対象の帳票テンプレートファイル名
     * @return 出力可の時はtrue、出力不可の時はfalse
     */
    private boolean workflowSingleOutputCheck(String targetTemplate) {
        if (!this.isWorkflowSingle) {
            return true;
        }
        for (String checkTemplate : this.checkTemplateList) {
            if (checkTemplate.equals(targetTemplate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 使用部品一覧を取得する。
     *
     * @param kanbanName カンバン名
     * @return 使用部品一覧
     * @throws Exception 
     */
    private List<AssemblyPartsInfoEntity> findAssemblyParts(String kanbanName) throws Exception {
        return kanbanInfoFacade.findPartsByKanbanName(URLEncoder.encode(kanbanName, CHARSET), null, null, null);
    }

    /**
     * 検査データ出力ボタンのアクション。
     *
     * @param event
     */
    @FXML
    private void onCheckDataOutButton(ActionEvent event) {
        logger.info("onCheckDataOutButton");

        File dstFile = this.showSaveDialog();
        if (Objects.isNull(dstFile)) {
            // sc.showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getPatternText("key.KbList.CheckDataOutName"), LocaleUtils.getPatternText("key.KbList.DialogCancel"));
            return;
        }

        this.outputFujiMes(dstFile, false);
    }
    
    /**
     * リペア作業後の検査データ出力のアクション
     * 
     * @param event 
     */
    @FXML
    private void onRepairButton(ActionEvent event) {
        logger.info("onRepairButton");

        File dstFile = this.showSaveDialog();
        if (Objects.isNull(dstFile)) {
            // sc.showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getPatternText("key.KbList.CheckDataOutName"), LocaleUtils.getPatternText("key.KbList.DialogCancel"));
            return;
        }

        this.outputFujiMes(dstFile, true);
    }
    
    /**
     * 検査データを出力する。
     * 
     * @param dstFile 出力先ファイル
     * @param isRepair リペア作業後の検査データ出力かどうかを指定する
     */
    private void outputFujiMes(File dstFile, boolean isRepair) {
        logger.info("outputFujiMes: dstFile={} isRepair={}", dstFile, isRepair);

        final int kanbanMax = StringUtils.parseInteger(properties.getProperty("kanbanMaxFujiMes", "100"));
        final int reportMax = StringUtils.parseInteger(properties.getProperty("reportMaxFujiMes", "1000"));

        // プログレスバーを表示
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(300.0);
        progressBar.setVisible(true);

        VBox pane = new VBox();
        pane.setPadding(new Insets(16.0, 16.0, 24.0, 16.0));
        pane.setSpacing(8.0);
        pane.getChildren().addAll(progressBar);

        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(sc.getWindow());
        stage.setTitle(LocaleUtils.getString("processing"));
        stage.setScene(new Scene(pane));
        //stage.setAlwaysOnTop(true);
        stage.setOnCloseRequest(event -> event.consume());
        stage.show();

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String message = "";
                StringBuilder resultText = new StringBuilder("");      // 処理結果メッセージ

                try {
                    logger.info("outputFujiMes Start.");
        
                    blockUI(true);

                    List<DisplayData> kanbans = kanbanList.getSelectionModel().getSelectedItems();
                    List<ReportOutInfoEntity> reports = new ArrayList<>();
                    final int progressMax = kanbans.size() * 2;
                    
                    updateProgress(0, progressMax);

                    for (int formIndex = 0, toIndex = Math.min(kanbans.size(), kanbanMax); formIndex < kanbans.size(); formIndex += kanbanMax) {
                        logger.info("reportOutSearch: formIndex={} toIndex={}", formIndex, toIndex);
                    
                        List<DisplayData> list = kanbans.subList(formIndex, toIndex);
                        List<Long> kanbanIds = list.stream().map(o -> o.getId()).collect(Collectors.toList());
                        Date fromDate = list.stream().map(o -> o.getEntity().getActualStartTime()).min(Date::compareTo).get();
                        Date toDate = list.stream().map(o -> o.getEntity().getActualCompTime()).max(Date::compareTo).get();

                        ReportOutSearchCondition condition = new ReportOutSearchCondition()
                                .kanbanIdList(kanbanIds)
                                .fromDate(fromDate)
                                .toDate(toDate);

                        long count = actualFacade.reportOutSearchCount(condition);

                        for (long from = 0; from < count; from += reportMax) {
                            reports.addAll(actualFacade.reportOutSearch(condition, from, from + reportMax - 1));
                        }

                        updateProgress(toIndex, progressMax);

                        toIndex = Math.min(toIndex + kanbanMax, toIndex + (kanbans.size() - toIndex));
                    }

                    logger.info("reportOutSearch: kanbans={} reports={}", kanbans.size(), reports.size());

                    Map<Long, List<ReportOutInfoEntity>> resMap = reports.stream().collect(
                        Collectors.groupingBy(ReportOutInfoEntity::getFkKanbanId)
                    );

                    // 検査データの出力データ
                    List<FujiMesOut> outDataList = new ArrayList<>();
                    // 処理結果  (キー：カンバン名／値：処理結果)
                    Map<String, String> resultInfos = new LinkedHashMap<>();
                    // 最初の工程ID
                    String fastWorkId = "";

                    int progress = kanbans.size();

                    // 選択しているカンバンリストから出力データを取得
                    for (DisplayData kanbanData : kanbans) {
                        updateProgress(++progress, progressMax);

                        // カンバンリストから取得できるx情報を出力データにセット
                        FujiMesOut outData = new FujiMesOut(kanbanData.getId(), kanbanData.getKanbanName(), kanbanData.getWorkflowId(), "");
                        outData.setErrorFlag(true);
                        // 処理結果
                        resultInfos.put(outData.getKanbanName(), LocaleUtils.getString("key.KbList.OK"));

                        // ** 工程順情報から工程IDを取得 **
                        WorkflowInfoEntity workflow = CacheUtils.getCacheWorkflow(outData.getWorkflowId());
                        String workId = workflow.getKanbanPropertyTemplateInfoCollection().stream()
                                                    .filter(info -> FUJIMES_ID.equals(info.getKanbanPropName()))
                                                    .map(info -> info.getKanbanPropInitialValue())
                                                    .findFirst().orElse(null);

                        if (StringUtils.isEmpty(workId)) {
                            // 工程IDが未入力の場合はエラー
                            resultInfos.put(outData.getKanbanName(), LocaleUtils.getString("key.KbList.WorkIdNot"));
                            continue;
                        } else if (!fastWorkId.isEmpty() && !fastWorkId.equals(workId)) {
                            // 選択したカンバンの２つ目以降は、工程IDが最初の工程IDと違う場合はエラー
                            resultInfos.put(outData.getKanbanName(), LocaleUtils.getString("key.KbList.WorkIdMiss"));
                            continue;
                        }

                        // 工程IDをセット
                        outData.setWorkId(workId);
                        if (fastWorkId.isEmpty()) {
                            fastWorkId = workId;
                        }

                        List<ReportOutInfoEntity> actuals = resMap.containsKey(kanbanData.getId()) ? resMap.get(kanbanData.getId()) : new ArrayList<>();
                        List<ActualPropertyEntity> actualAddInfos = new ArrayList<>();
                        actuals.forEach(entity -> {
                            actualAddInfos.addAll(entity.getPropertyCollection());
                        });

                        // ** 工程IDに紐づくFUJI-MES連携設定ファイルより検査データの検査値情報を読み込む。 **
                        Object[] retData = createCheckValue(outData.getWorkId());

                        switch ((Integer)retData[0]) {
                            case 1: // ファイルが存在しない。
                                resultInfos.put(outData.getKanbanName(), LocaleUtils.getString("key.KbList.IniFileNot"));
                                continue;
                            case 2: // ファイルが空ファイル。or セッションが存在しない。
                            case 3: // 「TAG」キーが存在しない。or 「TAG」キーの値が未設定。
                            case 4: // 例外エラー
                                resultInfos.put(outData.getKanbanName(), LocaleUtils.getString("key.KbList.IniFileDeficiency"));
                                continue;
                        }
                        // 検査データの検査値情報をセット
                        outData.setCheckValueInfos((List<FujiMesData>) retData[1]);

                        // カンバンに紐づく工程の工程情報のトレサ項目を取得
                        List<WorkPropertyInfoEntity> traceItems = getTraceabilitySettings(outData.getKanbanId(), workflow);

                        List<String> errMsg = new ArrayList<>();

                        if (!isRepair) {
                            // ** 区分IDに紐づけれたタグをキーにして、実績データから検査値を取得する。 **
                            for (FujiMesData data : outData.getCheckValueInfos()) {
                                List<ActualPropertyEntity> actualData = actualAddInfos.stream()
                                                            .filter(info -> data.getTagName().equals(info.getActualPropName()))
                                                            .collect(Collectors.toList());

                                // 実績データから値を取得
                                data.setMeasuredValue(actualData.isEmpty() ? "" :  actualData.get(actualData.size() - 1).getActualPropValue());

                                if (TagType.MEASURER != data.getTagType()) {
                                    if (data.isVerify()) {
                                        // トレサ項目から基準値・入力規則を取得
                                        List<WorkPropertyInfoEntity> workProperties = traceItems.stream()
                                                                    .filter(info -> StringUtils.equals(data.getUniqueTag(), info.getWorkPropTag()))
                                                                    .collect(Collectors.toList());

                                        if (workProperties.isEmpty()) {
                                            errMsg.add(createResultMsg(LocaleUtils.getString("key.KbList.TresaItemNot"), data.getCategoryId()));
                                            continue;
                                        }

                                        // タグが重複する場合があるは後勝ちのデータを抽出
                                        WorkPropertyInfoEntity workProperty = workProperties.get(workProperties.size() - 1);
                                        data.setWorkPropLowerTolerance(workProperty.getWorkPropLowerTolerance());
                                        data.setWorkPropUpperTolerance(workProperty.getWorkPropUpperTolerance());
                                        data.setWorkPropValidationRule(workProperty.getWorkPropValidationRule());
                                    }

                                    // 実績データから完了判定を取得
                                    actualData = actualAddInfos.stream()
                                            .filter(info -> (data.getUniqueTag() + "_OK").equals(info.getActualPropName()))
                                            .collect(Collectors.toList());

                                    // タグが重複する場合があるにで後勝ちのデータを抽出
                                    if (actualData.isEmpty() || StringUtils.isEmpty(actualData.get(actualData.size() - 1).getActualPropValue())) {
                                        // 実績データが存在しない 又は 値がnull／空白の場合
                                        data.setOkCheck(false);
                                    } else if ("0".equals(actualData.get(actualData.size() - 1).getActualPropValue())) {
                                        // 0の場合
                                        data.setOkCheck(false);
                                    } else {
                                        data.setOkCheck(true);                        
                                    }
                                }

                                // 実績データを検証
                                int retCheck = validateData(data);
                                switch (retCheck) {
                                    case 1: // タグが未入力見つからない or  測定値が未入力
                                    case 2: // 完了チェックが未入力
                                        errMsg.add(createResultMsg(LocaleUtils.getString("key.KbList.CheckResultNot"), data.getCategoryId()));
                                        break;
                                    case 3: // 入力された測定値が基準範囲外
                                        errMsg.add(createResultMsg(LocaleUtils.getString("key.KbList.OutOfRange"), data.getCategoryId()));
                                        break;
                                    case 4: // 入力された測定値が入力規則(正規表現)に従っていない
                                        errMsg.add(createResultMsg(LocaleUtils.getString("key.KbList.EntryRule"), data.getCategoryId()));
                                        break;
                                    case 5: // 例外エラー
                                        errMsg.add(createResultMsg(LocaleUtils.getString("key.KbList.OutDataChkEx"), data.getCategoryId()));
                                        break;
                                    default:
                                        break;
                                }
                            }

                        } else {
                            // リペア作業後の検査データ出力の場合、入力されたデータのみをTSVファイルに出力する (データチェックなし)
                            List<FujiMesData> removed = new ArrayList<>();
                            
                            for (FujiMesData data : outData.getCheckValueInfos()) {
                                List<ActualPropertyEntity> actualData = actualAddInfos.stream()
                                                            .filter(info -> data.getTagName().equals(info.getActualPropName()))
                                                            .collect(Collectors.toList());

                                // 実績データから値を取得
                                data.setMeasuredValue(actualData.isEmpty() ? "" :  actualData.get(actualData.size() - 1).getActualPropValue());
                                
                                if (StringUtils.isEmpty(data.getMeasuredValue())) {
                                    removed.add(data);
                                }
                            }

                            if (!removed.isEmpty()) {
                                outData.getCheckValueInfos().removeAll(removed);
                            }
                        }

                        if (errMsg.size() > 0) {
                            // エラーが有る場合、エラー内容を登録
                            resultInfos.put(outData.getKanbanName(), createResultMsg(errMsg, outData.getKanbanName()));
                        } else {
                            // エラーが無い場合、エラーフラグをOFFへ
                            outData.setErrorFlag(false);
                        }

                        // 作成した検査データを保存
                        outDataList.add(outData);
                    }

                    if (resultText.length() == 0 && !createCheckDataFile(dstFile.getAbsolutePath(), outDataList)) {
                        message = LocaleUtils.getString("key.KbList.FileOutNg");
                    }

                    // 正常処理時にメッセージ編集
                    if (resultText.length() == 0) {
                        message = LocaleUtils.getString("key.KbList.FileOutKo");
                        // 処理結果を編集
                        resultInfos.entrySet().forEach(entry -> {
                            resultText.append(entry.getKey()).append("：").append(entry.getValue()).append(NEW_LINE).append(NEW_LINE);
                        });
                    }
                    
                } catch (Exception ex) {
                    logger.fatal(ex, ex);

                } finally {
                    logger.info("outputFujiMes End.");

                    String _message = message;
                    Platform.runLater(() -> {
                        stage.close();
                        sc.showDialog(LocaleUtils.getString("key.Result"), "OperationReportDialog", new String[]{_message, resultText.toString()}, sc.getStage(), true);
                    });
                    blockUI(false);
                }
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }
   
    /**
     * FUJI-MES連携オプションライセンスの導入状況確認。
     * 
     * @return true:ライセンスの導入済 / false:ライセンスの未導入
     */
    private Boolean isEnableFujiMes() {
        String flag = AdProperty.getProperties().getProperty("enableFujiMes", "false");
        return "true".equals(flag);
    }

    /**
     * 設定ファイルから検査データ出力 リペア用ボタンが有効かどうかを返す。
     * 
     * @return true;有効、false:無効
     */
    private Boolean isEnableRepairButton() {
        String flag = AdProperty.getProperties().getProperty("enableRepairButton", "false");
        return "true".equals(flag);
    }

    /**
     * FUJI-MES連携設定ファイルパスを取得。
     * 
     * @param workId 工程ID
     * @return 
     */
    private String getFujiMesIniFilePath(String workId) {
        return System.getenv("ADFACTORY_HOME") + File.separator + "ext" +  File.separator + workId + ".ini";
    }
    
    /**
     * FUJI-MES連携設定ファイルより検査データの検査値情報を作成。
     * 
     * @param workId 工程ID
     * @return Object[0]:処理結果／Object[1]:検査データの検査値情報
     * <pre>
     * 処理結果 0:正常
     *          1:ファイルが存在しない。
     *          2:ファイルが空ファイル。
     *          2:セッションが存在しない。
     *          3:「TAG」キーが存在しない。
     *          3:「TAG」キーの値が未設定。
     *          4:例外エラー
     * </pre>
     */
    private Object[] createCheckValue(String workId) {
        int retCode = 0;
        List<FujiMesData> checkValueList = new ArrayList<>();

        try {
            // ファイルの存在チェック
            String path = getFujiMesIniFilePath(workId);
            File verIni = new File(path);
            if (!verIni.exists()) {
                //ファイルが存在しない。
                return new Object[] { 1, checkValueList };
            }

            IniFileLinked iniFile = new IniFileLinked(path);

            // 全セクション名を取得
            List<String> sectionKeys = iniFile.getSectionKeys();
            
            if (sectionKeys.isEmpty()) {
                //セッションが存在しない。
                return new Object[] { 2, checkValueList };
            }

            // 区分IDと区分IDに紐づくタグを取得
            for (String sectionKey : sectionKeys) {
                String keyValue = iniFile.getString(sectionKey, FUJIMES_TAG, "");
                if (StringUtils.isEmpty(keyValue)) {
                    //「TAG」キーが存在しない。 or //「TAG」キーの値が未設定。
                    return new Object[] { 3, checkValueList };
                } 

                //String verify = iniFile.getString(sectionKey, FUJIMES_VERIFY, "");
                //boolean isVerify = !StringUtils.equals(verify, "0");

                FujiMesData data = new FujiMesData(sectionKey, keyValue);

                String uTag = iniFile.getString(sectionKey, FUJIMES_UTAG, "");
                if (!StringUtils.isEmpty(uTag)) {
                    data.setUniqueTag(uTag);
                    data.setVerify(false);
                }

                checkValueList.add(data);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            retCode = 4;
        }

        return new Object[] { retCode, checkValueList };
    }
    
    /**
     * 工程IDで工程を取得する。
     *
     * @param kanbanId 工程順ID
     * @return 工程
     */
    private WorkInfoEntity getWorkInfo(Long workId, boolean withDevice) {
        try {
            // 工程を取得
            WorkInfoEntity entity = CacheUtils.getCacheWork(workId);
            return entity;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }    

    /**
     * 工程プロパティ一覧を取得する。 
     *
     * @return 工程プロパティ一覧
     */
    private List<WorkPropertyInfoEntity> getProperties(WorkInfoEntity entity) {
        // リストがnullの場合、追加情報のJSON文字列を工程プロパティ一覧に変換してセットする。
        if (Objects.isNull(entity.getPropertyInfoCollection())) {
            entity.setPropertyInfoCollection(JsonUtils.jsonToObjects(entity.getWorkAddInfo(), WorkPropertyInfoEntity[].class));

            List<WorkPropertyInfoEntity> checkInfos = JsonUtils.jsonToObjects(entity.getWorkCheckInfo(), WorkPropertyInfoEntity[].class);
            if (!checkInfos.isEmpty()) {
                entity.getPropertyInfoCollection().addAll(checkInfos);
            }
            
            // ソート
            entity.getPropertyInfoCollection().sort(Comparator.comparing(o -> o.getWorkPropOrder()));
        }
        return entity.getPropertyInfoCollection();
    }
    
    /**
     * カンバンに紐づく全工程の工程情報の品質トレーサビリテ設定を取得。
     * 
     * @param kanbanId カンバンID
     * @param workflow 工程順情報
     * @return 品質トレーサビリテ設定
     */
    private List<WorkPropertyInfoEntity> getTraceabilitySettings(Long kanbanId, WorkflowInfoEntity workflow) {
        
        // 工程情報
        Set<WorkInfoEntity> works = new HashSet<>();

        // カンバンにに紐づく全ての工程情報を取得
        workflow.getConWorkflowWorkInfoCollection().stream().forEach(o -> works.add(this.getWorkInfo(o.getFkWorkId(), true)));
        workflow.getConWorkflowSeparateworkInfoCollection().stream().forEach(o -> works.add(this.getWorkInfo(o.getFkWorkId(), true)));
        
        // 取得した工程情報から品質トレーサビリテ設定情報を取得
        List<WorkPropertyInfoEntity> workProps = new ArrayList<>();
        works.stream().forEach(entity -> workProps.addAll(this.getProperties(entity)));

        return workProps;
    }

    private  static List<TagType> EXCLUDED = Arrays.asList(TagType.MEASUREMENT, TagType.PARTS, TagType.COMPLETION_JUDGMENT);

    /**
     * 基準値、入力規則より実績データをチェック。
     * 
     * @param data 検査データの検査値情報
     * @return 処理結果
     * <pre>
     * 処理結果 0:正常
     *          1:タグが未入力見つからない or  測定値が未入力
     *          2:完了チェックが未入力
     *          3:入力された測定値が基準範囲外
     *          4:入力された測定値が入力規則(正規表現)に従っていない
     *          5:例外エラー     
     * </pre>
     */
    private int validateData(FujiMesData data) {
        int retCode;
        
        try {
            // タグが未入力見つからない or  測定値が未入力
            if (StringUtils.isEmpty(data.getMeasuredValue())) {
                return 1;
            }
            
            // タグの種類が完了判定の場合、0(false)も未入力とする。
            if (TagType.COMPLETION_JUDGMENT == data.getTagType()
                    && "0".equals(data.getMeasuredValue())) {
                return 1;
            }

            // 測定者の場合、以下のチェックは行わない。
            if (TagType.MEASURER == data.getTagType()) {
                return 0;
            }

            // 完了チェックが未入力
            if (!data.isOkCheck()) {
                return 2;
            }

            if (EXCLUDED.contains(data.getTagType())) {
                return 0;
            }
            
            if (!data.isVerify()) {
                // 値の検証を実施しない
                return 0;
            }

            // 入力された測定値が基準範囲外(上限下限が両方未設定時は処理しない)
            if (!Double.isNaN(data.getWorkPropLowerTolerance())
                    || !Double.isNaN(data.getWorkPropUpperTolerance())) {
                boolean propFlag = false;
                Double measureValue = Double.parseDouble(data.getMeasuredValue());

                // 下限のチェック
                if (!Double.isNaN(data.getWorkPropLowerTolerance()) && measureValue < data.getWorkPropLowerTolerance() ) {
                    propFlag = true;
                }
                
                // 上限のチェック
                if (!propFlag && !Double.isNaN(data.getWorkPropUpperTolerance()) && measureValue > data.getWorkPropUpperTolerance()) {
                    propFlag = true;                    
                }

                if (propFlag) {
                    return 3;
                }
            }

            // 入力された測定値が入力規則(正規表現)に従っていない
            if (!StringUtils.isEmpty(data.getWorkPropValidationRule()) && 
                    !Pattern.matches(data.getWorkPropValidationRule(), data.getMeasuredValue())) {
                return 4;
            }
            
            // チェックでNGとならなかったので正常を設定
            retCode = 0;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            retCode = 5;
        }

        return retCode;
    }

    /**
     * 検査データファイルを作成。
     * ※TSV形式
     * 
     * @param filePath   出力ファイルパス(フルパス)
     * @param checkDatas 検査データ
     * @return 処理結果(true:ファイル作成に成功 / false:ファイル作成に失敗)
     */
    private boolean createCheckDataFile(String filePath, List<FujiMesOut> checkDatas) {
        boolean retFlag = true;
        boolean headerOutFlag = false;

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(new File(filePath)), "Shift-JIS"))) {

            for (FujiMesOut checkData : checkDatas) {
                
                // ** ヘッダ部分 **
                /**
                 * 最初に検査データの検査値情報が存在するカンバンの検査データの検査値情報を利用してヘッダを作成
                 * 特にチェック結果を考慮しない。
                 * 検査値情報が無い＝工程IDが未設定、最初の工程IDと違う、設定ファイル計のエラー
                 */
                if (!headerOutFlag && checkData.getCheckValueInfos() != null) {
                    StringBuilder headerStr = new StringBuilder();
                    headerStr.append("Serial").append("\t");
                    headerStr.append("process").append("\t");
                    /* 出力単位が工程IDとなるので、それに紐づくヘッダ（区分ID）も同じとなるので、
                       データ部に出力する最初の行に登録している検査データの検査値情報を利用してヘッダを作成 */
                    checkData.getCheckValueInfos().stream().forEach(entity -> headerStr.append(entity.getCategoryId()).append("\t"));
                    // 最後のタブを削除してファイルへ出力
                    writer.write(headerStr.toString().replaceAll("\t$", ""));
                    writer.newLine(); // 改行
                    headerOutFlag = true;
                }              
                
                // ** データ部分 **
                // エラーが有る場合は出力しない。
                if (checkData.getErrorFlag()) {
                    continue;
                }

                // 出力内容を作成
                StringBuilder dataStr = new StringBuilder();
                dataStr.append(checkData.getKanbanName()).append("\t");
                dataStr.append(checkData.getWorkId()).append("\t");
                checkData.getCheckValueInfos().stream().forEach(entity -> dataStr.append(entity.getFileOutValue()).append("\t"));
                // 最後のタブを削除してファイルへ出力
                writer.write(dataStr.toString().replaceAll("\t$", ""));
                writer.newLine(); // 改行
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            retFlag = false;
        }
        
        return retFlag;
    }

    /**
     * 処理結果ダイアログに表示する文言を作成。
     * 
     * @param msg      メッセージ
     * @param addInfo 追加情報
     * @return 処理結果ダイアログに表示する文言
     */
    private String createResultMsg(String msg, String addInfo) {

        StringBuilder outMsg = new StringBuilder(msg);
        
        if (!addInfo.isEmpty()) {
            outMsg.append("(").append(addInfo).append(")");
        }
        
        return outMsg.toString();
    }

    /**
     * 処理結果ダイアログに表示する文言を作成。
     * 
     * @param errMsg     エラーメッセージ
     * @param kanbanName カンバン名
     * @return 処理結果ダイアログに表示する文言
     */
    private String createResultMsg(List<String> errMsg, String kanbanName) {
        
        if (errMsg.isEmpty()) {
            return "";
        }
        
        StringBuilder outMsg = new StringBuilder();
        int cnt = 0;
        
        for (String tmpString : errMsg) {
            cnt++;
            
            if (cnt % 11 == 0) { 
                outMsg.append(NEW_LINE);
                cnt = 1;
            }
            
            outMsg.append(tmpString);
            outMsg.append(",");
        }
        
        outMsg.setLength(outMsg.length() - 1);  // 最後の「、」を削除
        
        return outMsg.toString();
    }

    /**
     * 検査データ出力のファイル保存ダイアログを表示。
     * 
     * @return 選択したファイルのオブジェクト
     */
    private File showSaveDialog() {
        
        // ファイル保存ダイアログの初期値取得
        File initialDirectory = null;
        Properties props = AdProperty.getProperties(Constants.PROPERTY_NAME);
        String downloadDirectory = props.getProperty(Constants.TRACEABILITY_DOWNLOAD_DIRECTORY, null);
        if (Objects.nonNull(downloadDirectory)) {
            initialDirectory = new File(downloadDirectory);
        } else {
            initialDirectory = new File(System.getProperty("user.home"), "Desktop");
        }

        String name = LocaleUtils.getString("key.KbList.CheckDataOutName");
        
        // ファイル保存ダイアログの初期値設定
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(initialDirectory);
        chooser.setInitialFileName(name + ".tsv");
        chooser.setTitle(name);

        // ファイルの種類を設定
        FileChooser.ExtensionFilter ext1 = new FileChooser.ExtensionFilter("All files (*.*)", "*.*");
        FileChooser.ExtensionFilter ext2 = new FileChooser.ExtensionFilter("TSV files (*.tsv)", "*.tsv");
        chooser.getExtensionFilters().add(ext1);
        chooser.getExtensionFilters().add(ext2);

        // ファイル保存ダイアログ表示
        File filePath = chooser.showSaveDialog(sc.getWindow());
        
        return filePath;
    }

    /**
     * カンバン情報群のステータスに引数のステータス以外がふくまれているかを確認。
     * 
     * @param kanbanSelecteds カンバン情報群
     * @param status ステータス
     * @return true:ステータス以外が含まれない / false:ステータス以外が含まれる
     */
    private boolean checkKanbanStatus(List<DisplayData> kanbanSelecteds, String status) {
        
        boolean retValue = false;

        long count = kanbanSelecteds.stream()
                                    .filter(info -> !status.equals(info.getStatus()))
                                    .count();
        
        if (count == 0) {
            retValue = true;
        }
        
        return retValue;
    }

    /**
     * 選択されたカンバンアイテムをリストから選択します。
     * 
     * このメソッドは、選択されたカンバンアイテムのIDをリストから取得し、
     * そのIDを持つアイテムをカンバンリストから選択します。
     * その後、選択されたアイテムのIDリストをリセットし、
     * 「カンバンを移動」ボタンを有効にします。
     */
    private void selectedListKanbanItem() {
        if(Objects.isNull(selectedKanbanList) || selectedKanbanList.isEmpty()){
            return;
        }
        
        Set<Long> KanbanIdSet = new HashSet<>(selectedKanbanList);
        // カンバンリストから、選択されたIDを持つアイテムを選択します。
        this.kanbanList
                .getItems()
                .stream()
                .filter(p -> KanbanIdSet.contains(p.getEntity().getKanbanId()))
                .forEach(item -> this.kanbanList.getSelectionModel().select(item)); 
        
        // 選択されたアイテムのIDリストをリセットします。
        selectedKanbanList = null;

        // 「カンバンを移動」ボタンを有効にします。
        this.moveKanbanButton.setDisable(false);
    }
}