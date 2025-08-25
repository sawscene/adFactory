/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.clientservice.WorkPlanRestAPI;
import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanDateTimeConstants;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanKanbanHierarchyTreeCell;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanKanbanLedgerPermanenceData;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanLedgerSheetFactory;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleCellSizeTypeEnum;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleConstants;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleShowConfig;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleTypeEnum;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanSelectedKanbanAndHierarchy;
import adtekfuji.admanagerapp.productionnaviplugin.common.agenda.WorkPlanCustomAgendaEntity;
import adtekfuji.admanagerapp.productionnaviplugin.common.agenda.WorkPlanCustomAgendaItemEntity;
import adtekfuji.admanagerapp.productionnaviplugin.schedule.WorkPlanScheduleInjecter;
import adtekfuji.admanagerapp.productionnaviplugin.schedule.cell.WorkPlanSerialCell;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanDisplayStatusSelector;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanStyleInjecter;
import adtekfuji.clientservice.AccessHierarchyInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.agenda.KanbanTopicInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.PlanChangeCondition;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanTopicSearchCondition;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.plugin.KanbanRegistPreprocessContainer;
import jp.adtekfuji.adFactory.utility.KanbanRegistPreprocessResultEntity;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import jp.adtekfuji.javafxcommon.dialog.MessageDialog;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogButtons;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogResult;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogType;
import jp.adtekfuji.javafxcommon.selectcompo.AccessAuthSettingEntity;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.IndexedCheckModel;

/**
 * 作業計画画面コントローラー
 *
 * @author (TST)min
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "WorkPlanChartCompo", fxmlPath = "/fxml/compo/work_plan_chart_compo.fxml")
public class WorkPlanChartCompoController implements Initializable, ComponentHandler {

    /**
     * カンバンリスト表示用データ
     */
    public class DisplayData {

        private final Long id;
        private final Long workflowId;
        private String kanbanName = "";
        private String workflowName = "";
        private String status = "";
        private String startDate = "";
        private String endDate = "";
        private final KanbanInfoEntity entity;

        /**
         * カンバンリスト表示用データ
         *
         * @param entity
         */
        public DisplayData(KanbanInfoEntity entity) {
            this.id = entity.getKanbanId();
            this.workflowId = entity.getFkWorkflowId();
            this.kanbanName = entity.getKanbanName();
            if (Objects.nonNull(entity.getWorkflowName())) {
                this.workflowName = entity.getWorkflowName();
            }
            if (Objects.nonNull(entity.getWorkflowRev())) {
                this.workflowName = this.workflowName + " : " + entity.getWorkflowRev().toString();
            }
            this.status = LocaleUtils.getString(entity.getKanbanStatus().getResourceKey());
            if (Objects.nonNull(entity.getStartDatetime())) {
                this.startDate = formatter.format(entity.getStartDatetime());
            }
            if (Objects.nonNull(entity.getCompDatetime())) {
                this.endDate = formatter.format(entity.getCompDatetime());
            }
            this.entity = entity;
        }

        public Long getId() {
            return id;
        }

        public Long getWorkflowId() {
            return workflowId;
        }

        public String getKanbanName() {
            return kanbanName;
        }

        public String getWorkflowName() {
            return workflowName;
        }

        public String getStatus() {
            return status;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public KanbanInfoEntity getEntity() {
            return entity;
        }
    }

    private enum LedgerProcResultType {

        SUCCESS(0),
        SUCCESS_INCOMPLETE(1),
        ERROR_OCCURED(2),
        TEMPLATE_NONE(3),
        GET_INFO_FAILURED(4),
        TEMPLATE_UNREGISTERED(5),
        KANBAN_INCOMPLETED(6),
        FAILD_OTHER(99);

        private final Integer type;

        private LedgerProcResultType(Integer type) {
            this.type = type;
        }

        public Integer getType() {
            return type;
        }
    }

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final WorkPlanScheduleTypeEnum workPlanScheduleType = WorkPlanScheduleTypeEnum.KANBAN_WP_SCHEDULE;

    private final WorkPlanRestAPI REST_API = new WorkPlanRestAPI();

    private List<WorkPlanCustomAgendaEntity> agendaEntitys = new ArrayList<>();

    private final long ROOT_ID = 0;
    private static final String URI_SPLIT = "/";
    private final String COMMA_SPLIT = ",";
    private final String TITLE_ORDER_NO = "オーダー番号";
    private final String TITLE_CEREAL = "シリアル";

    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    // データフォーマット
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
    
    private TreeItem<KanbanHierarchyInfoEntity> rootItem;
    
    private SimpleDateFormat formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
//    private static final String SEARCH_FILTER_START_DATE = "search_filter_start_date";
//    private static final String SEARCH_FILTER_END_DATE = "search_filter_end_date";
//    private static final String SEARCH_FILTER_STATUS = "search_filter_status";
    private Boolean isDispDialog = false;

    private static final String KANBAN_CREATE_COMPO = "WorkPlanCreateCompo";
    private static final String KANBAN_DETAIL_COMPO = "WorkPlanDetailCompo";
//    private static final String KANBAN_CREATE_COMPO_ELS = "WorkPlanCreateCompoELS";

    private String defaultCreateKanbanCompo;// デフォルトのカンバン作成画面
    private List<DisplayData> selectedKanbans = new ArrayList<DisplayData>();
    private Map<Long, WorkPlanSerialCell> serialItems = new HashMap<>();
    private Map<Long, VBox> scheduleItems = new HashMap<>();
    private List<Long> kanbanIds = new ArrayList<>();
    
    private Date selectStartDate = new Date();
    private Date selectStopDate = new Date();
    private List<DisplayedStatusInfoEntity> statuses;
    private List<BreakTimeInfoEntity> breaktimes;


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
    private SplitPane workPlanPane;
    // カンバン階層ツリー
    @FXML
    private TreeView<KanbanHierarchyInfoEntity> hierarchyTree;
    // 処理中
    @FXML
    private Pane Progress;
    // カンバン名入力
    @FXML
    private TextField kanbanNameField;
    // モデル名入力
    @FXML
    private TextField ModelNameField;
    // カンバンステータスチェックボックス
    @FXML
    private CheckListView<String> statusList;
    // 表示開始日
    @FXML
    private DatePicker startDatePicker;
    // 表示終了日
    @FXML
    private DatePicker endDatePicker;
    // カンバン階層ツリー削除ボタン
    @FXML
    private ToolBar hierarchyBtnArea;
    @FXML
    private Button delTreeButton;
    // カンバン階層ツリー編集ボタン
    @FXML
    private Button editTreeButton;
    // カンバン階層ツリー権限編集ボタン
    @FXML
    private Button authButton;
    // カンバン階層ツリー新規作成ボタン
    @FXML
    private Button createTreeButton;
    // カンバン削除ボタン
    @FXML
    private Button deleteKanbanButton;
    // カンバン移動ボタン
    @FXML
    private Button moveKanbanButton;
    // カンバン編集ボタン
    @FXML
    private Button editKanbanButton;
    // カンバン新規作成ボタン
    @FXML
    private Button createKanbanButton;
    // 帳票出力ボタン
    @FXML
    private Button ledgerOutButton;
    // 計画変更ボタン
    @FXML
    private Button changePlansButton;
    // 時間スケールパネル
    @FXML
    private HBox timePane;
    // 時間スケールスクロール
    @FXML
    private ScrollPane timeScrollPane;
    // ガントチャートパネル
    @FXML
    private VBox serialPane;
    // ガントチャートスクロール
    @FXML
    private ScrollPane serialScrollPane;
    
    @FXML
    private VBox schedulePane;
    @FXML
    private ScrollPane scheduleScrollPane;
    @FXML
    private Label timeLine;
    @FXML
    private Slider dateWidthSizeSlider;
    @FXML
    private AnchorPane holidayPane;
    @FXML
    private ToggleGroup trackingFlg;
    @FXML
    private ToggleButton trackingON;
    @FXML
    private ToggleButton trackingOFF;

    /**
     * 初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(":initialize start");
        formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
        SplitPaneUtils.loadDividerPosition(workPlanPane, getClass().getSimpleName());

        //役割の権限によるボタン無効化.
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            this.delTreeButton.setDisable(true);
            this.editTreeButton.setDisable(true);
            this.authButton.setDisable(true);
            this.createTreeButton.setDisable(true);
            this.createKanbanButton.setDisable(true);
        }
        
        // 未選択
        this.editKanbanButton.setDisable(true);
        this.deleteKanbanButton.setDisable(true);
        this.moveKanbanButton.setDisable(true);
        this.ledgerOutButton.setDisable(true);
        this.changePlansButton.setDisable(true);

        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.RIGHT_ACCESS)) {
            hierarchyBtnArea.getItems().remove(authButton);
        }

        statuses = REST_API.searchDisplayedStatuses();
        breaktimes = REST_API.searchBreaktimes();

        //検索条件のステータス項目を描画
        ObservableList<String> stateList = FXCollections.observableArrayList(KanbanStatusEnum.getMessages(rb));
        statusList.setItems(stateList);

        //カンバン一覧の列設定を自動保存
//        kanbanList.init("kanbanList");

//        long selectKanbanHierarchy = ROOT_ID;
        String selectKanbanName = "";
        String selectModelName = "";
        String selectKanbanStatus = "";

        try{
            // 条件をプロパティファイルに保存
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

            // 工程の追従
            String mode = prop.getProperty(ProductionNaviPropertyConstants.SELECT_WP_FOLLOWING, "trackingOFF");
            logger.debug("  > 工程の追従:" + mode);
            if(Objects.nonNull(mode) ){
                if(mode.equals("trackingON")){
                    this.setProcessFollow(true);
                    this.trackingFlg.selectToggle(trackingON);
                    this.trackingON.setSelected(true);
                }else{
                    this.setProcessFollow(false);
                    this.trackingFlg.selectToggle(trackingOFF);
                    this.trackingOFF.setSelected(true);
                }
            }
            logger.debug(" 工程の追従:" + this.trackingFlg.getSelectedToggle().isSelected());

            // カンバン名
            selectKanbanName = prop.getProperty(ProductionNaviPropertyConstants.SEARCH_WP_KANBA_NAME);
            this.kanbanNameField.setText(selectKanbanName);
            // モデル名
            selectModelName = prop.getProperty(ProductionNaviPropertyConstants.SEARCH_WP_MODEL_NAME);
            this.ModelNameField.setText(selectModelName);
            // カンバンステータス
            selectKanbanStatus = prop.getProperty(ProductionNaviPropertyConstants.SEARCH_WP_KANBAN_STATUS);
            logger.debug("  > カンバンステータス:" + selectKanbanStatus);
            //検索条件の読み込みエラー処理がない(設定値がない場合、日付無、ステータス無)
            if(Objects.nonNull(selectKanbanStatus) && !selectKanbanStatus.equals("")){
                String[] statusDatas = selectKanbanStatus.split(COMMA_SPLIT);
                IndexedCheckModel<String> cm = statusList.getCheckModel();
                for (String statusData : statusDatas) {
                    cm.check(LocaleUtils.getString(KanbanStatusEnum.getEnum(statusData).getResourceKey()));
                }
            }

            try {
                selectStartDate = dateFormatter.parse(prop.getProperty(ProductionNaviPropertyConstants.SEARCH_WP_START_DATE));
            } catch (NullPointerException | ParseException ex) {
                selectStartDate = new Date();
            }
            try {
                selectStopDate = dateFormatter.parse(prop.getProperty(ProductionNaviPropertyConstants.SEARCH_WP_STOP_DATE));
            } catch (NullPointerException | ParseException ex) {
                selectStopDate = new Date();
            }
        
            // 表示開始日
            try{
                startDatePicker.setValue(Instant.ofEpochMilli(selectStartDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
            }catch(Exception ex){
                startDatePicker.setValue(LocalDate.now());
            }
            // 表示終了日
            try{
                endDatePicker.setValue(Instant.ofEpochMilli(selectStopDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
            }catch(Exception ex){
                endDatePicker.setValue(LocalDate.now());
            }
            
            //スライドバー
            String slideValue = prop.getProperty(ProductionNaviPropertyConstants.SELECT_WP_SCALE_SIZE, "50");
            dateWidthSizeSlider.setValue(Double.parseDouble(slideValue));
            
        } catch (IOException ex) {
            logger.fatal(ex);
        }

        //階層ツリー選択時処理
//        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<KanbanHierarchyInfoEntity>> observable, TreeItem<KanbanHierarchyInfoEntity> oldValue, TreeItem<KanbanHierarchyInfoEntity> newValue) -> {
//            if (Objects.nonNull(newValue) && newValue.getValue().getKanbanHierarchyId() != ROOT_ID) {
//                kanbanEditPermanenceData.setSelectedWorkHierarchy(newValue);
//                // 別スレッドでカンバンを検索して、カンバンリストを更新する。
//                //searchKanbanDataTask();
//            } else {
//                // リストクリア
//                kanbanEditPermanenceData.setSelectedWorkHierarchy(null);
//                //clearKanbanList();
//            }
//        });

        // タイトル側：カンバン選択時の処理
        this.serialPane.setOnMouseClicked((MouseEvent event) -> {onRowClicked();});

        // ガントチャート側：カンバン選択時の処理、Dragイベント
        this.schedulePane.setOnMouseReleased((MouseEvent event) -> {
            try{
                String isDragged = properties.getProperty(ProductionNaviPropertyConstants.IS_DRAGGED);
                String draggedId = properties.getProperty(ProductionNaviPropertyConstants.DRAGGED_ID);
                if(Objects.nonNull(isDragged) && isDragged.equals("true") && Objects.nonNull(draggedId) && !draggedId.equals("")) {
                    // Dragイベント
                    for(WorkPlanCustomAgendaEntity agenda : getKanbanAgenda(Long.parseLong(draggedId), selectStartDate, selectStopDate)) {
                        for(int i=0; i<agendaEntitys.size(); i++) {
                            if(agendaEntitys.get(i).getKanbanId().equals(agenda.getKanbanId())) {
                                agendaEntitys.set(i, agenda);
                            }
                        }
                    }
                    AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
                    Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                    showSchedule(agendaEntitys, createScheduleShowConfig(false), prop);
                } else {
                    // カンバン選択
                    Platform.runLater(() -> {onRowClicked();});
                }
            } catch(Exception ex) {
                logger.fatal(ex);
            } finally {
                properties.setProperty(ProductionNaviPropertyConstants.IS_DRAGGED, "false");
            }
        });

//            if (KANBAN_CREATE_COMPO_ELS.equals(this.properties.getProperty(Config.COMPO_CREATE_KANBAN))) {
//                this.defaultCreateKanbanCompo = KANBAN_CREATE_COMPO_ELS;
//            } else {
//                // スケジュールオプションが有効な場合、カンバン新規作成ボタンの右クリックメニューを作成する。
//                if (ClientServiceProperty.isLicensed(LicenseOptionType.Scheduling.getName())) {
//                    this.defaultCreateKanbanCompo = KANBAN_DETAIL_COMPO;
//                    this.createKanbanCompoContextMenu();
//                } else {
//                    this.defaultCreateKanbanCompo = KANBAN_CREATE_COMPO;
//                }
//            }
        this.defaultCreateKanbanCompo = KANBAN_DETAIL_COMPO;

        hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new WorkPlanKanbanHierarchyTreeCell());

        // スクロールバーを拘束
        scheduleScrollPane.vvalueProperty().bindBidirectional(serialScrollPane.vvalueProperty());
        scheduleScrollPane.hvalueProperty().bindBidirectional(timeScrollPane.hvalueProperty());
        timePane.prefWidthProperty().bindBidirectional(schedulePane.prefWidthProperty());

        //画面拡大時のイベント処理
        dateWidthSizeSlider.setOnMouseReleased((MouseEvent event) -> {
            // リソースに保存
            try{
                AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                logger.debug(" スライダー(SET):" + dateWidthSizeSlider.getValue());
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_WP_SCALE_SIZE, String.valueOf(dateWidthSizeSlider.getValue()));
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
                
                showSchedule(agendaEntitys, createScheduleShowConfig(true), prop);
            } catch(IOException e){
                logger.fatal(e, e);
            } finally{
                event.consume();
            }
        });


//        blockUI(true);

        Task task = new Task<Long>() {
            @Override
            protected Long call() throws Exception {
                long selectedKanbanHierarchyId = ROOT_ID;
                try {
//                        AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
                    Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

                    selectedKanbanHierarchyId = Long.parseLong(prop.getProperty(ProductionNaviPropertyConstants.SEARCH_WP_KANBAN_HIERARCHY_ID, String.valueOf(ROOT_ID)));
                    logger.debug("●カンバンツリーの表示処理.call()  カンバン階層ID:" + selectedKanbanHierarchyId);

                    // キャッシュに情報を読み込む。
//                    createCashDataThread();// MainApp または AdManagerAppProductionNaviPluginMenu で取得するため削除
                } catch(NumberFormatException e){
                    logger.fatal(e, e);
                }
                return selectedKanbanHierarchyId;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                createRoot(this.getValue());
            }
        };
        new Thread(task).start();

        // 日時エリア移動イベント
        timeScrollPane.hvalueProperty().addListener((ObservableValue, oldValue, newValue) ->{
            logger.debug("★ timeScrollPane.hvalueProperty()");
            logger.debug(" ObservableValue:" + ObservableValue);
            logger.debug(" oldValue:" + oldValue);
            logger.debug(" newValue:" + newValue);
            // リソースに保存
            try{
                AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                logger.debug(" 日付エリア(SET):" + newValue.doubleValue());
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_WP_SCALE_POSITION, String.valueOf(newValue.doubleValue()));
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }catch(IOException e){
                logger.fatal(e, e);
            }
        });
        
        logger.info(":initialize end");
    }

    /**
     * カンバン新規作成ボタンの右クリックメニューを作成する。
     */
    private void createKanbanCompoContextMenu() {
        Map<String, String> compoMap = new LinkedHashMap();
        compoMap.put(LocaleUtils.getString("key.KanbanCreateCompo"), KANBAN_CREATE_COMPO);
        compoMap.put(LocaleUtils.getString("key.KanbanDetailCompo"), KANBAN_DETAIL_COMPO);

        String selectedCompo = this.defaultCreateKanbanCompo;
//        String selectedCompo = this.properties.getProperty(Config.COMPO_CREATE_KANBAN, this.defaultCreateKanbanCompo);

        ContextMenu menu = new ContextMenu();
        final ToggleGroup menuGroup = new ToggleGroup();

        for(Map.Entry<String, String> compo : compoMap.entrySet()) {
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
     * (カンバン階層ツリー) キー押下のアクション
     *
     * @param ke
     */
    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // キャッシュに情報を読み込む。
//                        createCashDataThread();
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    createRoot(null);
                }
            };
            new Thread(task).start();
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
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Edit"), "WorkPlanHierarchyEditDialogCompo", newValue);
                if (ret != ButtonType.OK) {
                    return;
                }
                if (newValue.getHierarchyName().isEmpty()) {
                    String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                } else if (!oldName.equals(newValue.getHierarchyName()) || (oldFlag != newValue.getPartitionFlag())) {
                    item.getValue().setHierarchyName(newValue.getHierarchyName());
                    item.getValue().setPartitionFlag(newValue.getPartitionFlag());
                    
                    ResponseEntity res = REST_API.updateKanbanHierarchy(item.getValue());

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
                    for(int i=0; i<registList.size(); i++) {
                        OrganizationInfoEntity o = registList.get(i);
                        if(deleteList.contains(o)) {
                            deleteList.remove(o);
                            registList.remove(o);
                            i--;
                        }
                    }
                    if(!deleteList.isEmpty()) {
                        accessHierarchyInfoFacade.delete(type, id, deleteList);
                    }
                    if(!registList.isEmpty()) {
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
                    ResponseEntity res = REST_API.deleteKanbanHierarchy(item.getValue());

                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // ツリー更新
                        this.updateTreeItem(parentItem, null);
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
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.NewCreate"), "WorkPlanHierarchyEditDialogCompo", hierarchy);
                if (ret != ButtonType.OK) {
                    return;
                }

                if (Objects.isNull(hierarchy.getHierarchyName()) && hierarchy.getHierarchyName().isEmpty()) {
                    String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                } else {
                    hierarchy.setParentId(item.getValue().getKanbanHierarchyId());

                    ResponseEntity res = REST_API.registKanbanHierarchy(hierarchy);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // ツリー更新
                        this.updateTreeItem(item, getUriToHierarcyId(res.getUri()));
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
        //DisplayData selectedData = kanbanList.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(selectedKanbans) && selectedKanbans.size() == 1) {
            KanbanInfoEntity entity = new KanbanInfoEntity(selectedKanbans.get(0).getId(), null, selectedKanbans.get(0).getKanbanName(), null);
            WorkPlanSelectedKanbanAndHierarchy selected = new WorkPlanSelectedKanbanAndHierarchy(entity,
                    hierarchyTree.selectionModelProperty().getName());
            selected.setWorkPlanScheduleType(workPlanScheduleType);
            sc.setComponent("ContentNaviPane", "WorkPlanDetailCompo", selected);
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

            //DisplayData selectedData = kanbanList.getSelectionModel().getSelectedItem();
            TreeItem<KanbanHierarchyInfoEntity> selectedHierarchy = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedKanbans) || selectedKanbans.size() > 1 || Objects.isNull(selectedHierarchy)) {
                isCancel = true;
                return;
            }

            //hierarchyTree.setVisible(false);
            //kanbanList.setVisible(false);
            isDispDialog = true;

            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(hierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"));
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "WorkPlanHierarchyTreeCompo", treeDialogEntity);

            TreeItem<KanbanHierarchyInfoEntity> hierarchy = (TreeItem<KanbanHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (ret.equals(ButtonType.OK) && Objects.nonNull(hierarchy)) {

                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            KanbanInfoEntity item = selectedKanbans.get(0).getEntity();
                            KanbanInfoEntity kanban = REST_API.searchKanban(item.getKanbanId());
                            kanban.setParentId(hierarchy.getValue().getKanbanHierarchyId());
                            kanban.setFkUpdatePersonId(loginUserInfoEntity.getId());
                            kanban.setUpdateDatetime(new Date());

                            ResponseEntity res = REST_API.updateKanban(kanban);
                            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                                hierarchyTree.getSelectionModel().select(hierarchy);
                                searchKanbanData(true);
                            }
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                        }
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        hierarchyTree.setVisible(true);
                        //kanbanList.setVisible(true);
                        searchKanbanDataTask();
                        isDispDialog = false;
                        blockUI(false);
                    }
                };
                new Thread(task).start();
            } else {
                isCancel = true;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            hierarchyTree.setVisible(true);
            //kanbanList.setVisible(true);
            searchKanbanDataTask();
            isDispDialog = false;
            blockUI(false);
        } finally {
            if (isCancel) {
                hierarchyTree.setVisible(true);
                //kanbanList.setVisible(true);
                searchKanbanDataTask();
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
            WorkPlanSelectedKanbanAndHierarchy selected = new WorkPlanSelectedKanbanAndHierarchy(entity, item.getValue().getHierarchyName());
            sc.setComponent("ContentNaviPane", "WorkPlanDetailCompo", selected);
        }
    }

    /**
     * 削除ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onDeleteKanban(ActionEvent event) {
        try {
            logger.info("onDeleteKanban start.");

            if (Objects.isNull(selectedKanbans) || selectedKanbans.isEmpty()) {
                return;
            }

            final String messgage = selectedKanbans.size() > 1
                    ? LocaleUtils.getString("key.DeleteMultipleMessage")
                    : LocaleUtils.getString("key.DeleteSingleMessage");
            final String content = selectedKanbans.size() > 1
                    ? null
                    : selectedKanbans.get(0).getKanbanName();

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), messgage, content);
            if (!ret.equals(ButtonType.OK)) {
                return;
            }

            // カンバンを削除する。
            if (selectedKanbans.size() == 1) {
                deleteKanban(selectedKanbans.get(0));
            } else {
                deleteKanbans(selectedKanbans);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onDeleteKanban end.");
        }
    }

    /**
     * 複数のカンバンを削除する。
     *
     * @param datas 対象データ
     */
    private void deleteKanbans(List<DisplayData> datas) {
        blockUI(true);
        Task task = new Task<List<Long>>() {
            @Override
            protected List<Long> call() throws Exception {
                List<Long> skipList = new ArrayList();

                for (DisplayData data : datas) {
                    ResponseEntity responce = REST_API.deleteKanban(data.getId());
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
//                        boolean isFirstSkip = true;
//                        for (Long kanbanId : this.getValue()) {
//                            Optional<DisplayData> opt = kanbanList.getItems().stream().filter(p -> kanbanId.equals(p.getId())).findFirst();
//                            if (opt.isPresent()) {
//                                kanbanList.getSelectionModel().select(opt.get());
//                                if (isFirstSkip) {
//                                    kanbanList.scrollTo(opt.get());
//                                    isFirstSkip = false;
//                                }
//                            }
//                        }
                        searchKanbanData(false);

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
                return REST_API.deleteKanban(data.getId());
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
                if (loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
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
                return REST_API.deleteKanbanForced(data.getId());
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
        logger.debug("★ガントチャートの幅:" + this.scheduleScrollPane.getMinWidth() + "～" + this.scheduleScrollPane.getMaxWidth());
        
        // 選択階層
        TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
        // 階層未選択の場合は何もしない。
        if (Objects.isNull(item) || Objects.isNull(item.getValue())) {
            return;
        }else{
            // カンバン階層がルートなら抜ける
            if (item.getValue().getKanbanHierarchyId().equals(ROOT_ID)) {
                return;
            }
        }

        // 別スレッドでカンバンを検索して、カンバンリストを更新する。
        searchKanbanDataTask();
    }

    /**
     * 新規作成ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onCreateKanban(ActionEvent event) {
        // カンバンの新規作成画面を表示する。
        dispCompoCreateKanban(defaultCreateKanbanCompo);
    }

    /**
     * インポートボタンのクリックイベント
     * 
     * @param event イベント
     */
    @FXML
    private void onImportKanban(ActionEvent event){
        this.logger.info(":onImportKanban start");
        
        sc.setComponent("ContentNaviPane", "WorkPlanImportCompo");
//        ButtonType result = sc.showComponentDialog(LocaleUtils.getString("key.ProductionNavi.Import"), "KanbanImportCompo", new ButtonType[] { ButtonType.CANCEL } );
//        if (result.equals(ButtonType.CANCEL)) {
//           logger.info(" input cancel");
//        }

        this.logger.info(":onImportKanban end");
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
//            compoCreateKanban = this.properties.getProperty(Config.COMPO_CREATE_KANBAN, this.defaultCreateKanbanCompo);
            compoCreateKanban = this.defaultCreateKanbanCompo;
        }

//        this.properties.setProperty(Config.COMPO_CREATE_KANBAN, compoCreateKanban);

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

        WorkPlanSelectedKanbanAndHierarchy selected = new WorkPlanSelectedKanbanAndHierarchy(null, hierarchyName, LocaleUtils.getString("key.KanbanContinuousCreate"), hierarchyId);

        // カンバン作成画面を表示する。
        sc.setComponent("ContentNaviPane", "WorkPlanDetailCompo", selected);
//        sc.setComponent("ContentNaviPane", compoCreateKanban, selected);
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onRegularlyCreate(ActionEvent event) {
        TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(item) && item.getValue().getKanbanHierarchyId() != ROOT_ID) {
            WorkPlanSelectedKanbanAndHierarchy selected = new WorkPlanSelectedKanbanAndHierarchy(null, item.getValue().getHierarchyName(),
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
        try {
            blockUI(true);

            Path outputDir;
            try {
                outputDir = showOutputDialog(event);
            } catch (NullPointerException ex) {
                return;
            }
            Path resultFile = Paths.get(outputDir.toString(), "result.txt");
            Boolean isCompleteSuccess = true;
            int cntSuccess = 0;
            int cntFailure = 0;
            int cntSkip = 0;

            if (Files.exists(resultFile)) {
                Files.delete(resultFile);
            }

            double startTime = System.nanoTime();

            for (DisplayData data : selectedKanbans) {
                LedgerProcResultType result = createLedger(data, outputDir, event);
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
            showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.KanbanOutLedger"), sb.toString());

        } catch (IOException ex) {
            logger.fatal(ex, ex);

        } finally {
            blockUI(false);
        }
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
     * 帳票出力
     *
     * @param data
     * @param event
     */
    private LedgerProcResultType createLedger(DisplayData data, Path outputDir, ActionEvent event) {
        Path resultFile = Paths.get(outputDir.toString(), "result.txt");
        String message = "";
        LedgerProcResultType ret = LedgerProcResultType.FAILD_OTHER;

        try {
            if (Objects.isNull(data)) {
                return ret;
            } else if (!data.getStatus().equals(LocaleUtils.getString("key.KanbanStatusCompletion"))) {
                // カンバン 未完了
                message = LocaleUtils.getString("key.KanbanOutLedgerWorkUnfinished");
                ret = LedgerProcResultType.KANBAN_INCOMPLETED;
                return ret;
            }
            //TODO: MVCに沿って通信処理(エラー対応含)を検討すること.他の通信処理も同様.
            WorkflowInfoEntity workflowInfoEntity = REST_API.searchWorkflow(data.getWorkflowId());
            if (Objects.isNull(workflowInfoEntity)) {
                // 工程順 取得失敗
                message = LocaleUtils.getString("key.ServerReconnectMessage");
                ret = LedgerProcResultType.GET_INFO_FAILURED;
                return ret;
            } else if (Objects.isNull(workflowInfoEntity.getLedgerPath())) {
                // 帳票テンプレートファイルパス 未登録
                message = LocaleUtils.getString("key.KanbanOutLedgerPassNothing");
                ret = LedgerProcResultType.TEMPLATE_UNREGISTERED;
                return ret;
            }
            //帳票データ取得処理
            WorkPlanKanbanLedgerPermanenceData ledgerData = new WorkPlanKanbanLedgerPermanenceData();
            ledgerData.setLedgerFilePass(workflowInfoEntity.getLedgerPath());
            ledgerData.setKanbanInfoEntity(REST_API.searchKanban(data.getId()));
            if (Objects.isNull(ledgerData.getKanbanInfoEntity())) {
                // カンバンなし
                message = LocaleUtils.getString("key.ServerReconnectMessage");
                ret = LedgerProcResultType.GET_INFO_FAILURED;
                return ret;
            }
            ledgerData.setWorkKanbanInfoEntitys(REST_API.searchWorkKanbans(data.getId()));
            if (Objects.isNull(ledgerData.getWorkKanbanInfoEntitys())) {
                // 工程カンバンなし
                message = LocaleUtils.getString("key.ServerReconnectMessage");
                ret = LedgerProcResultType.GET_INFO_FAILURED;
                return ret;
            }
            ledgerData.setSeparateworkWorkKanbanInfoEntitys(REST_API.searchSeparateWorkKanbans(data.getId()));
            if (Objects.isNull(ledgerData.getSeparateworkWorkKanbanInfoEntitys())) {
                // 工程カンバンなし
                message = LocaleUtils.getString("key.ServerReconnectMessage");
                ret = LedgerProcResultType.GET_INFO_FAILURED;
                return ret;
            }
            ledgerData.setActualResultInfoEntitys(REST_API.searchActualResult(new ActualSearchCondition().kanbanId(data.getId())));
            if (Objects.isNull(ledgerData.getActualResultInfoEntitys())) {
                // 実績なし
                message = LocaleUtils.getString("key.ServerReconnectMessage");
                ret = LedgerProcResultType.GET_INFO_FAILURED;
                return ret;
            }

            //帳票生成用ファクトリークラスに生成処理を譲渡
            //出力終了の通知
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String ledgerFileName = ledgerData.getKanbanInfoEntity().getKanbanName() + "_"
                    + ledgerData.getKanbanInfoEntity().getWorkflowName() + "_" + df.format(new Date());
            WorkPlanLedgerSheetFactory ledgerSheetFactory = new WorkPlanLedgerSheetFactory(ledgerData, outputDir, ledgerFileName);
            switch (ledgerSheetFactory.writeLedgerSheet(event)) {
                case FAILD_NOTPASS:
                    // 帳票テンプレートファイルがない
                    message = LocaleUtils.getString("key.KanbanOutLedgerPassErr");
                    ret = LedgerProcResultType.TEMPLATE_NONE;
                    break;
                case SUCCESS:
                    List<String> faildReplaceTags = ledgerSheetFactory.isReplaceCheck();
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
                        ret = LedgerProcResultType.SUCCESS_INCOMPLETE;
                        break;
                    }
                    message = LocaleUtils.getString("key.KanbanOutLedgerSuccess");
                    ret = LedgerProcResultType.SUCCESS;
                    break;
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            message = LocaleUtils.getString("key.KanbanOutLedgerApplicationErr") + " - " + ex.getMessage();
            ret = LedgerProcResultType.ERROR_OCCURED;

        } finally {
            try (BufferedWriter bw = Files.newBufferedWriter(resultFile, Charset.defaultCharset(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                bw.append(data.kanbanName + "(" + data.workflowName + "):");
                bw.append(message);
                bw.newLine();
            } catch (IOException ex) {
                logger.fatal(ex, ex);
            }
        }
        return ret;
    }

    /**
     * 警告メッセージを表示する
     *
     * @param type
     * @param title
     * @param message
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            sc.showAlert(type, title, message);
        });
    }

    /**
     * ツリーの表示を更新する。
     *
     * @param parentItem 表示更新するノード
     * @param selectedId 更新後に選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void updateTreeItem(TreeItem<KanbanHierarchyInfoEntity> parentItem, Long selectedId) {
        if (Objects.isNull(parentItem.getParent())) {
            //ROOT
            createRoot(selectedId);
        } else {
            //子階層
            expand(parentItem, selectedId);
        }
    }

    /**
     * ツリーの親階層生成
     *
     */
    private void createRoot(Long selectedId) {
        logger.debug("createRoot start.");
        try {
            blockUI(true);

            // ルートが存在しない場合は新規作成する。
            if (Objects.isNull(this.rootItem)) {
                this.rootItem = new TreeItem<>(new KanbanHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.Kanban")));
            }

            this.rootItem.getChildren().clear();
            
            Task task = new Task<List<KanbanHierarchyInfoEntity>>() {
                @Override
                protected List<KanbanHierarchyInfoEntity> call() throws Exception {
                    // 子組織の件数を取得する。
                    return REST_API.searchTopKanbanHierarchys();
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        long count = this.getValue().size();
                        rootItem.getValue().setChildCount(count);

                        // 第一階層を設定する。
                        for(KanbanHierarchyInfoEntity data : this.getValue()){
                            TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(data);
                            if (data.getChildCount() > 0) {
                                item.getChildren().add(new TreeItem());
                            }
                            item.expandedProperty().addListener(expandedListener);
                            rootItem.getChildren().add(item);
                        }
                        
                        TreeItem<KanbanHierarchyInfoEntity> selectNode = rootItem;
                        // ルート以外の選択
                        if(Objects.nonNull(selectedId) && selectedId > ROOT_ID){
                            // 前回選択した親階層の情報を取得し、上位順にする。
                            List<List<KanbanHierarchyInfoEntity>> hierarchyAddIds = new ArrayList<>();
                            
                            hierarchyAddIds = getKanbanHierarchy(this.getValue(), selectedId);
                            logger.debug("------------------------------------------------");
                            for(List<KanbanHierarchyInfoEntity> list : hierarchyAddIds){
                                logger.debug(" ★" + list.size() + "件 ");
                                for(KanbanHierarchyInfoEntity data : list){
                                    logger.debug("   ■" + data.getHierarchyName() + ", " + data.getKanbanHierarchyId() + ", " + data.getParentId() + ", " + data.getChildCount());
                                }
                            }
 
                            // 再帰的に選択にする
                            TreeItem<KanbanHierarchyInfoEntity> item = treeForExpanding(rootItem, hierarchyAddIds, selectedId);
                            if(Objects.nonNull(item)){
                                selectNode = item;
                            }
                        }
                        
                        hierarchyTree.rootProperty().setValue(rootItem);
                        hierarchyTree.refresh();

                        // 親ノードが閉じている場合、ノードを開状態にする。(開閉イベントは一旦削除して、開いた後で再登録)
                        if (!rootItem.isExpanded()) {
                            rootItem.expandedProperty().removeListener(expandedListener);
                            rootItem.setExpanded(true);
                            rootItem.expandedProperty().addListener(expandedListener);
                        }

                        if (Objects.nonNull(selectedId) && !selectedId.equals(ROOT_ID)) {
                            // 指定されたノードを選択状態にする。
                            selectedTreeItem(selectNode, selectedId);
                            searchKanbanDataTask();
                        } else {
                            // 選択ノードの指定がない場合は、ルートを選択状態にする。
                            hierarchyTree.getSelectionModel().select(rootItem);
                            blockUI(false);
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
//                        blockUI(false);
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

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     * ツリーを作成（再帰的）
     * 
     * @param _item アイテム
     * @param _list 子階層のリスト
     * @param _selectId カンバンID
     */
    private TreeItem<KanbanHierarchyInfoEntity> treeForExpanding(final TreeItem<KanbanHierarchyInfoEntity> _item, List<List<KanbanHierarchyInfoEntity>> _list, Long _selectId){
        logger.info(":treeForExpanding start");
        if( Objects.isNull(_list) || _list.isEmpty() || _list.get(0).isEmpty()){
            logger.warn(" リストデータなし");
            return null;
        }

        TreeItem<KanbanHierarchyInfoEntity> value = null;
        logger.debug("  ●追加する子の階層件数:" + _list.size() );
        for(TreeItem<KanbanHierarchyInfoEntity> item : _item.getChildren() ){
            logger.trace("  > ツリー情報:" + item.getValue().toString() );
            // データ無ければ次へ
            if(Objects.isNull(item) || _list.isEmpty() || _list.get(0).isEmpty()){
                break;
            }
           
            // リストの情報なし
            if(_list.isEmpty() || _list.get(0).isEmpty()){
                break;
            }

            if(item.getValue().getKanbanHierarchyId().equals(_list.get(0).get(0).getParentId())){
                logger.trace("   >> 階層一致:" + item.getValue().getKanbanHierarchyId() );
                
                value = item;
                item.expandedProperty().removeListener(expandedListener);
                
                // 子の階層を削除
                item.getChildren().clear();
                
                // ツリーを選択状態にする
                item.setExpanded(true);

                for(KanbanHierarchyInfoEntity data : _list.get(0)){
                    logger.trace("     >>> 子の追加情報:" + data.toString());
                    TreeItem<KanbanHierarchyInfoEntity> itemChilde = new TreeItem<>(data);
                    if(data.getChildCount() > 0){
                        itemChilde.getChildren().add(new TreeItem());
                    }
                    itemChilde.expandedProperty().addListener(expandedListener);
                    item.getChildren().add(itemChilde);
                    
                    if(data.getKanbanHierarchyId().equals(_selectId)){
                        itemChilde.setExpanded(true);
                    }
                }

                item.expandedProperty().addListener(expandedListener);
                
                if(item.getValue().getKanbanHierarchyId().equals(_list.get(0).get(0).getKanbanHierarchyId())){
                    value = item;
                }

                // 先頭のリストを削除
                logger.info(" list count Before:" + _list.size());
                _list.remove(0);
                logger.info(" list count After:" + _list.size());

                // 再帰的に呼び出す
                if(Objects.nonNull(_list) && _list.size() > 0){
                    TreeItem<KanbanHierarchyInfoEntity> value1 = treeForExpanding(item, _list, _selectId);
                    if(Objects.nonNull(value1)){
                        value = value1;
                    }
                }
            }
        }
        
        logger.info(":treeForExpanding end");
        
        return value;
    }

    /**
     * ツリー展開
     *
     * @param parentItem 親階層
     */
    private void expand(TreeItem<KanbanHierarchyInfoEntity> parentItem, Long selectedId) {
        logger.info("expand: parentItem={}", parentItem.getValue());
        try {
            if (!isDispDialog) {
                //blockUI(true);
            }

            parentItem.getChildren().clear();

            final long parentId = parentItem.getValue().getKanbanHierarchyId();

            Task task = new Task<List<KanbanHierarchyInfoEntity>>() {
                @Override
                protected List<KanbanHierarchyInfoEntity> call() throws Exception {
                    // 子階層の件数を取得する。
                    return REST_API.searchKanbanHierarchys(parentId);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        this.getValue().stream().forEach((entity) -> {
                            TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);
                            if (entity.getChildCount() > 0) {
                                item.getChildren().add(new TreeItem());
                            }
                            item.expandedProperty().addListener(expandedListener);
                            parentItem.getChildren().add(item);
                        });

                        if (Objects.nonNull(selectedId)) {
                            // 親ノードが閉じている場合、ノードを開状態にする。(開閉イベントは一旦削除して、開いた後で再登録)
                            if (!parentItem.isExpanded()) {
                                parentItem.expandedProperty().removeListener(expandedListener);
                                parentItem.setExpanded(true);
                                parentItem.expandedProperty().addListener(expandedListener);
                            }
                            // 指定されたノードを選択状態にする。
                            selectedTreeItem(parentItem, selectedId);
                        }

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        if (!isDispDialog) {
                            //blockUI(false);
                        }
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    //blockUI(false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //blockUI(false);
        }
    }

    /**
     * IDが一致するTreeItemを選択する。(存在しない場合は親を選択)
     *
     * @param parentItem 選択状態にするノーノの親ノード
     * @param selectedId 選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void selectedTreeItem(TreeItem<KanbanHierarchyInfoEntity> parentItem, Long selectedId) {
        Optional<TreeItem<KanbanHierarchyInfoEntity>> find = parentItem.getChildren().stream().
                filter(p -> p.getValue().getKanbanHierarchyId().equals(selectedId)).findFirst();

        if (find.isPresent()) {
            this.hierarchyTree.getSelectionModel().select(find.get());
        } else {
            this.hierarchyTree.getSelectionModel().select(parentItem);
        }
        this.hierarchyTree.scrollTo(this.hierarchyTree.getSelectionModel().getSelectedIndex());// 選択ノードが見えるようスクロール
    }

    /**
     * 別スレッドでカンバンを検索して、カンバンリストを更新する。
     */
    private void searchKanbanDataTask() {
        logger.debug("searchKanbanDataTask start.");
        try {
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    searchKanbanData(true);
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

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
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

            // 条件をプロパティファイルに保存
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

            // カンバン階層
            long kanbanHierarchyId = hierarchyTree.getSelectionModel().getSelectedItem().getValue().getKanbanHierarchyId();
            prop.setProperty(ProductionNaviPropertyConstants.SEARCH_WP_KANBAN_HIERARCHY_ID, String.valueOf(kanbanHierarchyId));

//            String parentName = hierarchyTree.getSelectionModel().getSelectedItem().getValue().getHierarchyName();
//            prop.setProperty(ProductionNaviPropertyConstants.SEARCH_WP_KANBAN_HIERARCHY_ID, parentName);

            // カンバン名
            String kanbanName = (Objects.isNull(kanbanNameField.getText()) || "".equals(kanbanNameField.getText())) ? null : kanbanNameField.getText();
            prop.setProperty(ProductionNaviPropertyConstants.SEARCH_WP_KANBA_NAME, Objects.isNull(kanbanName) ? "" : kanbanName);
            // モデル名
            String modelName = (Objects.isNull(ModelNameField.getText()) || "".equals(ModelNameField.getText())) ? null : ModelNameField.getText();
            prop.setProperty(ProductionNaviPropertyConstants.SEARCH_WP_MODEL_NAME, Objects.isNull(modelName) ? "" : modelName);

            // カンバンステータス
            List<KanbanStatusEnum> selectStatusData = new ArrayList<>();
            ObservableList<Integer> tmpSelectStatus = statusList.getCheckModel().getCheckedIndices();
            logger.debug("   > check :" + statusList.getSelectionModel().getSelectedItems());
            if (statusList.getSelectionModel().getSelectedItems() != null && !statusList.getCheckModel().getCheckedIndices().isEmpty()) {
                logger.debug("    >> check count:" + statusList.getCheckModel().getCheckedIndices().size());
                for (int i = 0; i < statusList.getCheckModel().getCheckedIndices().size(); i++) {
                    String status = KanbanStatusEnum.getValueText(tmpSelectStatus.get(i));
                    logger.trace("    >> status Name:" + status);
                    selectStatusData.add(KanbanStatusEnum.getEnum(status));
                }
            } else {
                selectStatusData.addAll(Arrays.asList(KanbanStatusEnum.values()));
            }
            logger.debug(" チェック件数:" + selectStatusData.size());

            StringBuilder selectStatuDatas = new StringBuilder();
            for(KanbanStatusEnum status : selectStatusData){
                selectStatuDatas.append(status.toString());
                selectStatuDatas.append(COMMA_SPLIT);
            }
            logger.debug(" 保持データ:" + selectStatuDatas.toString());
            prop.setProperty(ProductionNaviPropertyConstants.SEARCH_WP_KANBAN_STATUS, selectStatuDatas.toString());

            // 表示期間
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
            prop.setProperty(ProductionNaviPropertyConstants.SEARCH_WP_START_DATE, dateFormatter.format(scheduleStartDay));
            prop.setProperty(ProductionNaviPropertyConstants.SEARCH_WP_STOP_DATE, dateFormatter.format(scheduleEndDay));

            agendaEntitys.clear();
            
            KanbanSearchCondition search = new KanbanSearchCondition();
            search.setKanbanName(kanbanName);
            search.setModelName(modelName);
            search.setFromDate(scheduleStartDay);
            search.setToDate(scheduleEndDay);
            search.setKanbanStatusCollection(selectStatusData);
            List<KanbanInfoEntity> kanbans = REST_API.searchKanbansOfKanbanHierarchys(new ArrayList<>(), kanbanHierarchyId, search);

            agendaEntitys.addAll(getKanbansAgenda(kanbans, scheduleStartDay, scheduleEndDay));
            showSchedule(agendaEntitys, createScheduleShowConfig(false), prop);

//            Platform.runLater(() -> {
//                clearKanbanList();
//                kanbanList.setItems(tableData);
//                kanbanList.getSortOrder().add(kanbanNameColumn);
//            });

//            //フィルター条件の更新
//            if (Objects.nonNull(scheduleStartDay)) {
//                Integer differenceStateDate = DateUtils.differenceOfDate(formatter.format(scheduleStartDay), formatter.format(new Date()));
//                properties.setProperty(SEARCH_FILTER_START_DATE, differenceStateDate.toString());
//            } else {
//                properties.setProperty(SEARCH_FILTER_START_DATE, "");
//            }
//            if (Objects.nonNull(scheduleEndDay)) {
//                Integer differenceEndDate = DateUtils.differenceOfDate(formatter.format(scheduleEndDay), formatter.format(new Date()));
//                properties.setProperty(SEARCH_FILTER_END_DATE, differenceEndDate.toString());
//            } else {
//                prop.setProperty(ProductionNaviPropertyConstants.SEARCH_WP_STOP_DATE, "");
//                properties.setProperty(SEARCH_FILTER_END_DATE, "");
//            }
//
//            StringBuilder sb = new StringBuilder();
//            for (KanbanStatusEnum status : selectStatusData) {
//                sb.append(status.toString());
//                sb.append(COMMA_SPLIT);
//            }

            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);

//            properties.setProperty(SEARCH_FILTER_STATUS, sb.toString());
        } catch (ParseException | IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバン操作ボタンの有効状態を設定する。
     */
    private void setStateKanbanButtons() {
        if (Objects.nonNull(selectedKanbans) && !selectedKanbans.isEmpty()) {
            boolean isDisabledSingle = false;
            boolean isDisabledMulti = false;

            if (selectedKanbans.size() == 1) {
                // 1件選択
                if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                    isDisabledSingle = true;
                    isDisabledMulti = true;
                }
            } else {
                // 複数選択
                isDisabledSingle = true;
                if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                    isDisabledMulti = true;
                }
            }

            if (isDisabledSingle) {
                Platform.runLater(() -> {
                    this.editKanbanButton.setDisable(true);
                    this.moveKanbanButton.setDisable(true);
                    this.changePlansButton.setDisable(true);
                });
            } else {
                Platform.runLater(() -> {
                    this.editKanbanButton.setDisable(false);
                    this.moveKanbanButton.setDisable(false);
                    this.changePlansButton.setDisable(false);
                });
            }

            if (isDisabledMulti) {
                Platform.runLater(() -> {
                    this.deleteKanbanButton.setDisable(true);
                    this.ledgerOutButton.setDisable(true);
                });
            } else {
                Platform.runLater(() -> {
                    this.ledgerOutButton.setDisable(false);
                    this.deleteKanbanButton.setDisable(false);
                });
            }
        } else {
            // 未選択
            Platform.runLater(() -> {
                this.editKanbanButton.setDisable(true);
                this.deleteKanbanButton.setDisable(true);
                this.moveKanbanButton.setDisable(true);
                this.changePlansButton.setDisable(true);
                this.ledgerOutButton.setDisable(true);
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
     * 再接続処理
     *
     */
    public void reconnection() {
        Platform.runLater(() -> {
            sc.showAlert(Alert.AlertType.ERROR, null, LocaleUtils.getString("key.ServerReconnectMessage"));
            sc.setComponent("ContentNaviPane", "WorkPlanChartCompo");
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
        try {
            blockUI(true);

            if (Objects.isNull(selectedKanbans) || selectedKanbans.isEmpty()) {
                return;
            }

            final PlanChangeCondition condition = new PlanChangeCondition(new Date(), DateUtils.min(), DateUtils.min());

            // 計画変更ダイアログを表示する。
            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.ChangePlans"), "WorkPlanPlanChangeDialog", condition);
            if (!ButtonType.OK.equals(ret)) {
                return;
            }

            // 選択したカンバン一覧から、カンバンID一覧を作成する。
            final List<Long> kanbanIds = selectedKanbans.stream().map(p -> p.getId()).collect(Collectors.toList());

            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
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
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ChangePlans"), LocaleUtils.getString("key.alert.systemError"));
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
        } finally {
            if (isCancel) {
                blockUI(false);
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
        int RANGE_IDS = 20;

        for (int fromIndex = 0; fromIndex < kanbanIds.size(); fromIndex += RANGE_IDS) {
            int toIndex = fromIndex + RANGE_IDS;
            if (toIndex > kanbanIds.size()) {
                toIndex = kanbanIds.size();
            }

            List<Long> ids = kanbanIds.subList(fromIndex, toIndex);

            // カンバンの計画時間を変更する。
            result = REST_API.kanbanPlanChange(condition, ids);
            if (!result.isSuccess()) {
                break;
            }
        }

        // カンバン一覧表示を更新する。
        searchKanbanData(false);

        return result;
    }

    /**
     * スケジュール表示
     *
     * @param agendaEntity スケジュールデータ
     * @param config
     * @param prop 
     */
    private void showSchedule(List<WorkPlanCustomAgendaEntity> agendaEntity, WorkPlanScheduleShowConfig config, Properties prop) {
        logger.info(":screenUpdate start.");

        Platform.runLater(() -> {
            try {
    //            String scaleSize = prop.getProperty(ProductionNaviPropertyConstants.SELECT_WP_SCALE_SIZE);

                // カンバン選択初期化
                properties.setProperty(ProductionNaviPropertyConstants.SELECTED_KANBAN, "");
                selectedKanbans.clear();
                setStateKanbanButtons();

                WorkPlanScheduleInjecter injector = new WorkPlanScheduleInjecter(config);
                injector.injectDate(timePane, timeScrollPane);
                injector.injectSerial(serialPane, schedulePane, agendaEntity, workPlanScheduleType, prop);
                injector.injectHolidays(holidayPane, REST_API.searchHolidays(config.getBaseStartDate(), config.getBaseEndDate()));

                //　TimeLineを現在の日時まで移動
                injector.setTimeLine(timeLine);
        //            Platform.runLater(() -> {
        //                ScheduleInjecter.setScheduleDateNowPoint(timeScrollPane, startDate, endDate);
        //            });

                serialItems = injector.getSerialItems();
                scheduleItems = injector.getScheduleItems();

                double scalePosition = 0;
                try {
                    scalePosition = Double.valueOf(prop.getProperty(ProductionNaviPropertyConstants.SELECT_WP_SCALE_POSITION, String.valueOf(scalePosition)));
                } catch (NumberFormatException ex) {
                    logger.fatal(ex, ex);
                } finally {
                    logger.debug(" スケールの位置:" + scalePosition);
                    logger.trace("   min:" + timeScrollPane.getVmin() + ", max:" + timeScrollPane.getVmax());
                    logger.trace("   min:" + timeScrollPane.getHmin() + ", max:" + timeScrollPane.getHmax());
                    logger.trace("   X:" + timeScrollPane.getTranslateX() + ", Y:" + timeScrollPane.getTranslateY() + ", Z:" + timeScrollPane.getTranslateZ());

                    this.setScalePosition(scalePosition);
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
            logger.info(":screenUpdate end.");
        });
    }
    
    /**
     * 時間のスクロール設定
     * 
     * @param _scalePosition 
     */
    private void setScalePosition(double _scalePosition){
        Platform.runLater(() -> {
            timeScrollPane.setHvalue(_scalePosition);
        });
    }
    
    /**
     * スケジュール表示の設定情報を作成する
     *
     * @param isChangeSchduleSize サイズ変更を行う
     * @return 表示設定
     */
    private WorkPlanScheduleShowConfig createScheduleShowConfig(boolean isChangeSchduleSize) {
        try {
            WorkPlanScheduleShowConfig config = new WorkPlanScheduleShowConfig();
            // サイズ変更の変更か初期表示か設定
            if (isChangeSchduleSize) {
                config.setScheduleSize(null);
            } else {
                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                // スケールの調整
                String scale = prop.getProperty(ProductionNaviPropertyConstants.SELECT_WP_SCALE_SIZE);
                logger.trace(" スライダー(GET):" + scale);

                if(Objects.isNull(scale) || scale.isEmpty()){
                    config.setScheduleSize(WorkPlanScheduleCellSizeTypeEnum.getEnum(prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_SCEDULE_CELL_SIZE, WorkPlanScheduleCellSizeTypeEnum.MONTHLY.name())));
                    switch (config.getScheduleSize()) {
                        case DAILY:
                            dateWidthSizeSlider.setValue(WorkPlanScheduleConstants.DEFAULT_DATE_MAGNIFICATION_DAILY * WorkPlanScheduleConstants.DEFAULT_MAGNIFICATION);
                            break;
                        case WEEKLY:
                            dateWidthSizeSlider.setValue(WorkPlanScheduleConstants.DEFAULT_DATE_MAGNIFICATION_WEEKLY * WorkPlanScheduleConstants.DEFAULT_MAGNIFICATION);
                            break;
                        case MONTHLY:
                            dateWidthSizeSlider.setValue(WorkPlanScheduleConstants.DEFAULT_DATE_MAGNIFICATION_MONTHLY * WorkPlanScheduleConstants.DEFAULT_MAGNIFICATION);
                            break;
                    }
                }else{
                    config.setScheduleSize(null);
                    dateWidthSizeSlider.setValue(Double.valueOf(scale));
                }
            }
            logger.trace(" スライダー(VALUE):" + dateWidthSizeSlider.getValue() + ", "+ dateWidthSizeSlider.getMin() + "～" + dateWidthSizeSlider.getMax());

            // 表示倍率を設定
            config.setDailyWidthMagnification(dateWidthSizeSlider.getValue() / WorkPlanScheduleConstants.DEFAULT_MAGNIFICATION);
            // 表示する日付の範囲を設定
            config.setBaseMonthlyDate(DateUtils.differenceOfDate(DateUtils.toDate(endDatePicker.getValue()),
                    DateUtils.toDate(startDatePicker.getValue()),
                    WorkPlanDateTimeConstants.FORMAT_DATE) + 1);
            config.setBaseStartDate(DateUtils.toDate(startDatePicker.getValue()));
            config.setBaseEndDate(DateUtils.toDate(endDatePicker.getValue()));
//            timeScrollPane.setHvalue(dateWidthSizeSlider.getValue());
            return config;

        } catch (NumberFormatException | ParseException ex) {
            logger.fatal(ex, ex);
            return new WorkPlanScheduleShowConfig();
        }
    }
    
    /**
     * 実際に保存を実施する
     *
     * @param isTrans
     */
    private void registThread(boolean isTrans, KanbanInfoEntity kanban) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    logger.info("registThread:Start");
                    //保存前処理
                    KanbanRegistPreprocessContainer plugin = KanbanRegistPreprocessContainer.getInstance();
                    if (Objects.nonNull(plugin)) {
                        KanbanRegistPreprocessResultEntity resultEntity = plugin.kanbanRegistPreprocess(kanban);
                        if (!resultEntity.getResult()) {
                            Platform.runLater(() -> {
                                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanbanTitle"), LocaleUtils.getString(resultEntity.getResultMessage()));
                            });
                            return null;
                        }
                    }

                    //保存処理実行
                    ResponseEntity responseEntity = REST_API.updateKanban(kanban);
                    
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    logger.info("registThread:End");
                    Platform.runLater(() -> {
                        blockUI(false);
                        //更新時カンバンステータスが変更可能なものになったら有効にする
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    

    /**
     * 工程の追従　ON
     * @param event 
     */
    @FXML
    private void onProcessFollowOn(ActionEvent event) {
        logger.info(":onProcessFollowOn start");
        this.setProcessFollow(true);
        logger.info(":onProcessFollowOn end");
    }
    /**
     * 工程の追従　OFF
     * @param event 
     */
    @FXML
    private void onProcessFollowOff(ActionEvent event) {
        logger.info(":onProcessFollowOff start");
        this.setProcessFollow(false);
        logger.info(":onProcessFollowOff end");
    }
    
    /**
     * 
     * @param mode 
     */
    private void setProcessFollow(boolean mode){
        try{
            // 条件をプロパティファイルに保存
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            prop.setProperty(ProductionNaviPropertyConstants.SELECT_WP_FOLLOWING, mode ? this.trackingON.getId() : this.trackingOFF.getId());
            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            
            if(mode){
                trackingON.setStyle("-fx-text-fill:white;-fx-background-color:#1565c0;-fx-border-width: 0.3; -fx-border-color: black;");
                trackingOFF.setStyle("-fx-text-fill:black;-fx-background-color:#e5e5e5;-fx-border-width: 0.3; -fx-border-color: black;");
            }else{
                trackingOFF.setStyle("-fx-text-fill:white;-fx-background-color:#1565c0;-fx-border-width: 0.3; -fx-border-color: black;");
                trackingON.setStyle("-fx-text-fill:black;-fx-background-color:#e5e5e5;-fx-border-width: 0.3; -fx-border-color: black;");
            }
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 表示期間の開始日
     * 
     * @param event 
     */
    @FXML
    private void onStartDate(ActionEvent event) {
        logger.debug(":onStartDate start");
        Date startDate = DateUtils.toDate(this.startDatePicker.getValue());
        Date stopDate = DateUtils.toDate(this.endDatePicker.getValue());
        logger.debug(" 期間:" + startDate.toString() + "～" + stopDate.toString());

        Date date = org.apache.commons.lang3.time.DateUtils.addMonths(startDate, 1);
        date = org.apache.commons.lang3.time.DateUtils.addDays(date, -1);
        int day = stopDate.compareTo(date);
        logger.debug(" 期間:" + startDate.toString() + "～" + stopDate.toString() + " = " + stopDate.compareTo(date) + ", " + day);
        if(day > 0){
            this.endDatePicker.setValue(DateUtils.toLocalDate(date));
        }

        logger.debug(":onStartDate end");
    }

    /**
     * 表示期間の終了日
     * 
     * @param event 
     */
    @FXML
    private void onStopDate(ActionEvent event) {
        logger.info(":onStopDate start");

        Date startDate = DateUtils.toDate(this.startDatePicker.getValue());
        Date stopDate = DateUtils.toDate(this.endDatePicker.getValue());
        logger.debug(" 期間:" + startDate.toString() + "～" + stopDate.toString());

        Date date = org.apache.commons.lang3.time.DateUtils.addMonths(stopDate, -1);
        date = org.apache.commons.lang3.time.DateUtils.addDays(date, 1);
        int day = startDate.compareTo(date);
        logger.debug(" 期間:" + startDate.toString() + "～" + stopDate.toString() + " = " + startDate.compareTo(date) + ", " + day);
        if(day < 0){
            this.startDatePicker.setValue(DateUtils.toLocalDate(date));
        }
        
        logger.info(":onStopDate end");
    }
    
    /**
     * 選択中のカンバン階層リスト作成
     * 　カンバン階層のみカンバン階層の上位
     * 
     * @param _datas カンバン階層リスト
     * @param _selectId カンバン階層ID
     * @return カンバン階層リスト
     */
    private List<List<KanbanHierarchyInfoEntity>> getKanbanHierarchy(List<KanbanHierarchyInfoEntity> _datas, Long _selectId){
        List<List<KanbanHierarchyInfoEntity>> value = new ArrayList();

        for(KanbanHierarchyInfoEntity data : _datas){
            logger.debug(" 階層名:" + data.getHierarchyName() + ", 階層ID:" + data.getKanbanHierarchyId() + ", 階層ID:" + data.getParentId() + ", 子の件数:" + data.getChildCount());
            if(data.getKanbanHierarchyId().equals(_selectId)) {
                value.add(_datas);
                break;
            } else if(data.getChildCount() > 0){
                // 子のカンバン階層を取得
                List<KanbanHierarchyInfoEntity> kanbanHierarchyInfoEntityDatas = REST_API.searchKanbanHierarchys(data.getKanbanHierarchyId());

                List<List<KanbanHierarchyInfoEntity>> datas = getKanbanHierarchy(kanbanHierarchyInfoEntityDatas, _selectId);
                if(Objects.nonNull(datas) && !datas.isEmpty() && datas.size() > 0){
                    if(!data.getParentId().equals(ROOT_ID)){
                        value.add(_datas);
                    }
                    for(List<KanbanHierarchyInfoEntity> entities: datas){
                        value.add(entities);
                    }
                    break;
                }
            }
        }
        return value;
    }

    /**
     * 作業計画実績を取得する
     *
     * @param kanbans
     * @param startDate
     * @param endDate
     * @return
     */
    private List<WorkPlanCustomAgendaEntity> getKanbansAgenda(List<KanbanInfoEntity> kanbans, Date startDate, Date endDate) {
        try {
            List<KanbanTopicInfoEntity> topics = searchKanbansAgenda(kanbans, startDate, endDate);

            //予定件数取得
            List<Long> listOrganizationId = new ArrayList<>();
            topics.stream().forEach(i -> {if(!listOrganizationId.contains(i.getOrganizationId())) listOrganizationId.add(i.getOrganizationId());});
            List<ScheduleInfoEntity> schedules = REST_API.searchSchedules(listOrganizationId, startDate, endDate);

            List<WorkPlanCustomAgendaEntity> agendas = new LinkedList<>();
            for (KanbanInfoEntity kanban : kanbans) {
                List<KanbanTopicInfoEntity> topicList = topics.stream().filter(o -> kanban.getKanbanId().equals(o.getKanbanId())).collect(Collectors.toList());
                if(Objects.nonNull(topicList) && topicList.size() > 0) {
                    Map<Long, List<Long>> workKanbanOrgaIdsMap = REST_API.getWorkKanbanOrgaIdsMap(topicList);
                    List<KanbanTopicInfoEntity> topic = new ArrayList<>();
                    for(Long workKanbanId : workKanbanOrgaIdsMap.keySet()) {
                        Optional<KanbanTopicInfoEntity> data = topicList.stream().filter(o->workKanbanId.equals(o.getWorkKanbanId())).findFirst();
                        if(data.isPresent()) {
                            KanbanTopicInfoEntity entity = data.get();
                            entity.setOrganizationName("");
                            topic.add(entity);
                        }
                    }
                    WorkPlanCustomAgendaEntity agenda = convertKanbanToAgenda(topic, kanban, startDate, endDate, schedules, workKanbanOrgaIdsMap);
                    agendas.add(agenda);
                }
            }

            return agendas;
        } catch (Exception ex) {
            DialogBox.alert(ex);
            logger.fatal(ex);
            return new ArrayList<>();
        } finally {
        }
    }
    
    /**
     * 作業計画実績を取得する
     *
     * @param kanbans
     * @param startDate
     * @param endDate
     * @return
     */
    private List<WorkPlanCustomAgendaEntity> getKanbanAgenda(Long kanbanId, Date startDate, Date endDate) {
        try {
            List<KanbanTopicInfoEntity> topics = searchKanbanIdAgenda(kanbanId, startDate, endDate);

            //予定件数取得
            List<Long> listOrganizationId = new ArrayList<>();
            topics.stream().forEach(i -> {if(!listOrganizationId.contains(i.getOrganizationId())) listOrganizationId.add(i.getOrganizationId());});
            List<ScheduleInfoEntity> schedules = REST_API.searchSchedules(listOrganizationId, startDate, endDate);
                    
            List<WorkPlanCustomAgendaEntity> agendas = new LinkedList<>();
            Map<Long, List<Long>> workKanbanOrgaIdsMap = REST_API.getWorkKanbanOrgaIdsMap(topics);
            List<KanbanTopicInfoEntity> topic = new ArrayList<>();
            for(Long workKanbanId : workKanbanOrgaIdsMap.keySet()) {
                Optional<KanbanTopicInfoEntity> data = topics.stream().filter(o->workKanbanId.equals(o.getWorkKanbanId())).findFirst();
                if(data.isPresent()) {
                    KanbanTopicInfoEntity entity = data.get();
                    entity.setOrganizationName("");
                    topic.add(entity);    
                }
            }
            WorkPlanCustomAgendaEntity agenda = convertKanbanToAgenda(topic, REST_API.searchKanban(kanbanId)
                                                                    , startDate, endDate, schedules, workKanbanOrgaIdsMap);
            agendas.add(agenda);
            return agendas;
        } catch (Exception ex) {
            DialogBox.alert(ex);
            logger.fatal(ex);
            return new ArrayList<>();
        } finally {
        }
    }

    /**
     * 予実データをカンバン情報に合わせてに変更する
     *
     * @param topics 予実データ
     * @param ...
     */
    private WorkPlanCustomAgendaEntity convertKanbanToAgenda(List<KanbanTopicInfoEntity> topics, KanbanInfoEntity kanban
            , Date startDate, Date endDate, List<ScheduleInfoEntity> listSchedule, Map<Long, List<Long>> workKanbanOrgaIdsMap) {
        WorkPlanCustomAgendaEntity kanbanAgenda = new WorkPlanCustomAgendaEntity();
        kanbanAgenda.setKanbanId(kanban.getKanbanId());
        kanbanAgenda.setKanbanNameTitle(kanban.getKanbanName());     // カンバン名

        kanbanAgenda.setModelNameTitle(Objects.isNull(kanban.getModelName()) ? "" : kanban.getModelName());         // モデル名
        kanbanAgenda.setWorkNoTitle(Objects.isNull(kanban.getWorkflowName()) ? "" : kanban.getWorkflowName());   // 工程順名
        // オーダー番号
        kanbanAgenda.setOrderNoTitle(Objects.isNull(kanban.getPropertyValue(TITLE_ORDER_NO)) ? "" : kanban.getPropertyValue(TITLE_ORDER_NO).getValue());              
        // シリアル
        kanbanAgenda.setCerialTitle(Objects.isNull(kanban.getPropertyValue(TITLE_CEREAL)) ? "" : kanban.getPropertyValue(TITLE_CEREAL).getValue());              
//        工程順名      … KanbanInfoEntity から取得
//        モデル名      … KanbanInfoEntity から取得
//        オーダー番号  … KanbanPropertyInfoEntity から取得
//        シリアル      … KanbanPropertyInfoEntity から取得

        List<WorkPlanCustomAgendaItemEntity> plans = new ArrayList<>();
        List<WorkPlanCustomAgendaItemEntity> actuals = new ArrayList<>();

        for (KanbanTopicInfoEntity topic : topics) {
            List<Long> listOrganization = workKanbanOrgaIdsMap.get(topic.getWorkKanbanId());

            // 作業者情報
            List<OrganizationInfoEntity> organizationDatas = new ArrayList();
            for(Long id : listOrganization){
                OrganizationInfoEntity data = REST_API.searchOrganization(id);
                if(Objects.nonNull(data)){
                    organizationDatas.add(data);
                    if(Objects.nonNull(data.getOrganizationName())) {
                        String orgaNames = "";
                        if(!topic.getOrganizationName().equals("")) orgaNames = topic.getOrganizationName().concat(",");
                        topic.setOrganizationName(orgaNames.concat(data.getOrganizationName()));
                    }
                }
            }

            List<ScheduleInfoEntity> scheduleData = listSchedule.stream()
                                                                .filter(s->listOrganization.contains(s.getFkOrganizationId()))
                                                                .collect(Collectors.toList());
            long scheduleCount = scheduleData.stream()
                                            .filter(s-> (topic.getPlanStartTime().before(s.getScheduleFromDate()) && topic.getPlanEndTime().after(s.getScheduleFromDate()))
                                                    || (topic.getPlanStartTime().after(s.getScheduleFromDate()) && topic.getPlanEndTime().before(s.getScheduleToDate()))
                                                    || (topic.getPlanStartTime().before(s.getScheduleToDate()) && topic.getPlanEndTime().after(s.getScheduleToDate()))
                                            ).count();

            WorkPlanDisplayStatusSelector displayStatusSelector = new WorkPlanDisplayStatusSelector(statuses);
            DisplayedStatusInfoEntity statusPlan = displayStatusSelector.getPlanDisplayStatus(topic.getWorkKanbanStatus()
                                                                    , topic.getPlanStartTime(), topic.getPlanStartTime()
                                                                    , topic.getActualStartTime(), topic.getActualEndTime()
                                                                    , breaktimes, organizationDatas);

            WorkPlanCustomAgendaItemEntity plan = new WorkPlanCustomAgendaItemEntity();
            plan.createKanbanPlanData(topic, statusPlan, scheduleCount);
            plans.add(plan);

            DisplayedStatusInfoEntity statusActual = displayStatusSelector.getActualDisplayStatus(topic.getWorkKanbanStatus()
                                                                        , topic.getPlanStartTime(), topic.getPlanStartTime()
                                                                        , topic.getActualStartTime(), topic.getActualEndTime()
                                                                        , breaktimes, organizationDatas);

            WorkPlanCustomAgendaItemEntity actual = new WorkPlanCustomAgendaItemEntity();
            actual.createKanbanActualData(topic, statusActual);
            actuals.add(actual);
        }

        // 計画
        REST_API.createPlan(kanbanAgenda, plans, startDate, endDate);
        // 実績
        REST_API.createActual(kanbanAgenda, actuals, startDate, endDate);

        return kanbanAgenda;
    }

    /**
     * 指定した期間のカンバンの作業予定を取得
     *
     * @param kanbans
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    private List<KanbanTopicInfoEntity> searchKanbansAgenda(List<KanbanInfoEntity> kanbans, Date startDate, Date endDate) throws Exception {
        kanbanIds = new ArrayList<>();
        for (KanbanInfoEntity kanban : kanbans) {
            kanbanIds.add(kanban.getKanbanId());
        }
        KanbanTopicSearchCondition condition = new KanbanTopicSearchCondition(KanbanTopicSearchCondition.ContentType.DAYS_KANBAN);
        condition.setPrimaryKeys(kanbanIds);
        condition.setFromDate(startDate);
        condition.setToDate(endDate);

        List<KanbanTopicInfoEntity> topics = REST_API.searchKanbanTopic(condition);
        return topics;
    }

    /**
     * 指定した期間のカンバンの作業予定を取得
     *
     * @param kanbanId
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    private List<KanbanTopicInfoEntity> searchKanbanIdAgenda(Long kanbanId, Date startDate, Date endDate) throws Exception {
        List<Long> kanbanIds = new ArrayList<>();
        kanbanIds.add(kanbanId);
        
        KanbanTopicSearchCondition condition = new KanbanTopicSearchCondition(KanbanTopicSearchCondition.ContentType.DAYS_KANBAN);
        condition.setPrimaryKeys(kanbanIds);
        condition.setFromDate(startDate);
        condition.setToDate(endDate);

        List<KanbanTopicInfoEntity> topics = REST_API.searchKanbanTopic(condition);
        return topics;
    }

    /**
     * 行クリックのアクション
     * 
     */
    private void onRowClicked() {
        String selectedKanban = properties.getProperty(ProductionNaviPropertyConstants.SELECTED_KANBAN);
        if(selectedKanban.equals("")){
            return;
        }
        Long selectedKanbanId = Long.parseLong(selectedKanban);
        boolean isSelected = true;
        for(DisplayData kanban : selectedKanbans) {
            if(kanban.id.equals(selectedKanbanId)) {
                isSelected = false;
                selectedKanbans.remove(kanban);
                break;
            }
        }
        if(isSelected) {
            KanbanInfoEntity e = REST_API.searchKanban(selectedKanbanId);
            if(Objects.nonNull(e)) {
                selectedKanbans.add(new DisplayData(e));
            }
        }
        changeRowStyle(selectedKanbanId, isSelected);
        setStateKanbanButtons();
    }

    /**
     * 行の選択状態を変更する。
     *
     * @param kanbanId カンバンID
     */
    private void changeRowStyle(long kanbanId, Boolean isSelected) {
        WorkPlanSerialCell serialCell = serialItems.get(kanbanId);
        VBox scheduleBase = scheduleItems.get(kanbanId);

        StackPane stackPane = (StackPane) scheduleBase.getParent();
        Optional<Node> opt = stackPane.getChildren().stream().filter(p -> "bgPane".equals(p.getId())).findFirst();
        Pane bgPane = null;
        if (opt.isPresent()) {
            bgPane = (Pane) opt.get();
        } else {
            return;
        }

        String borderColor;
        String bgPaneStyle;
        if (isSelected) {
            // 選択状態のスタイル
            borderColor = "#0096c9";
            bgPaneStyle = "-fx-background-color: #0096c9; -fx-opacity: 0.3;";
        } else {
            // 非選択状態のスタイル
            borderColor = "lightgray";
            bgPaneStyle = "-fx-background-color: transparent;";
        }

        serialCell.setSelectedStyle(isSelected);

        WorkPlanStyleInjecter.setBorderColorStyle(scheduleBase, borderColor);

        if (Objects.nonNull(bgPane)) {
            bgPane.setStyle(bgPaneStyle);
        }
    }

    @Override
    public boolean destoryComponent() {
        SplitPaneUtils.saveDividerPosition(workPlanPane, getClass().getSimpleName());
        return true;
    }
}
