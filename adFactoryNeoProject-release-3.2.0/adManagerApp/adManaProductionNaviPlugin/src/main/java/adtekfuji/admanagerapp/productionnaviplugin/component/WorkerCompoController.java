/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.clientservice.WorkPlanRestAPI;
import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanDateTimeConstants;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleCellSizeTypeEnum;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleConstants;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleShowConfig;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleTypeEnum;
import adtekfuji.admanagerapp.productionnaviplugin.common.agenda.WorkPlanCustomAgendaEntity;
import adtekfuji.admanagerapp.productionnaviplugin.common.agenda.WorkPlanCustomAgendaItemEntity;
import adtekfuji.admanagerapp.productionnaviplugin.schedule.WorkPlanScheduleInjecter;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanDisplayStatusSelector;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.agenda.KanbanTopicInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanTopicSearchCondition;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import jp.adtekfuji.javafxcommon.treecell.OrganizationTreeCell;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業者管理画面コントローラー
 *
 * @author (TST)min
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "WorkerCompo", fxmlPath = "/fxml/compo/worker_compo.fxml")
public class WorkerCompoController implements Initializable, ComponentHandler {

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    // データフォーマット
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");

    private TreeItem<OrganizationInfoEntity> rootItem;
    private List<WorkPlanCustomAgendaEntity> agendaEntitys = new ArrayList<>();
    private final WorkPlanScheduleTypeEnum scheduleType = WorkPlanScheduleTypeEnum.ORGANIZATION_WP_SCHEDULE;
    
    private SimpleDateFormat formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
    private static final String SEARCH_FILTER_START_DATE = "search_filter_start_date";
    private static final String SEARCH_FILTER_END_DATE = "search_filter_end_date";
    
    private static final long ROOT_ID = 0;
    
    private final WorkPlanRestAPI REST_API = new WorkPlanRestAPI();

    private Date selectStartDate = new Date();
    private Date selectStopDate = new Date();
    private List<DisplayedStatusInfoEntity> statuses;
    private List<BreakTimeInfoEntity> breaktimes;
    
    private boolean isCancelMove = false;// 保存確認ダイアログで取消を選択した場合の、階層ツリー移動キャンセルフラグ
    
    @FXML
    private SplitPane workerPane;
    /** 組織階層 **/
    @FXML
    private TreeView<OrganizationInfoEntity> hierarchyTree;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private HBox timePane;
    @FXML
    private ScrollPane timeScrollPane;
    @FXML
    private VBox serialPane;
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

    /** プログレス **/
    @FXML
    private Pane Progress;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(":initialize() start");
        
        formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));

        SplitPaneUtils.loadDividerPosition(workerPane, getClass().getSimpleName());

        statuses = REST_API.searchDisplayedStatuses();
        breaktimes = REST_API.searchBreaktimes();

        try{
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

            // 工程の追従
            String mode = prop.getProperty(ProductionNaviPropertyConstants.SELECT_WORKER_FOLLOWING);
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
            
            // 表示期間
            try {
                selectStartDate = dateFormatter.parse(prop.getProperty(ProductionNaviPropertyConstants.SEARCH_WORKER_START_DATE));
            } catch (NullPointerException | ParseException ex) {
                selectStartDate = new Date();
            }
            try {
                selectStopDate = dateFormatter.parse(prop.getProperty(ProductionNaviPropertyConstants.SEARCH_WORKER_STOP_DATE));
            } catch (NullPointerException | ParseException ex) {
                selectStopDate = new Date();
            }
            
            //スライドバー
            String slideValue = prop.getProperty(ProductionNaviPropertyConstants.SELECT_WORKER_SCALE_SIZE, "50");
            dateWidthSizeSlider.setValue(Double.parseDouble(slideValue));
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        // 階層ツリーのフォーカス移動イベント
//        hierarchyTree.getFocusModel().focusedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
//            logger.info("階層ツリーのフォーカス移動イベント");
//            if (isCancelMove) {
//                // 移動をキャンセルしたら、元の場所を選択状態にする。
//                hierarchyTree.getSelectionModel().select(oldValue.intValue());
//            }
//        });

        // 表示期間
        startDatePicker.setValue(Instant.ofEpochMilli(selectStartDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
        endDatePicker.setValue(Instant.ofEpochMilli(selectStopDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
//        if (Objects.nonNull(properties.getProperty(SEARCH_FILTER_START_DATE)) && !"".equals(properties.getProperty(SEARCH_FILTER_START_DATE))) {
//            Calendar calendarStartDate = Calendar.getInstance();
//            calendarStartDate.setTime(new Date());
//            calendarStartDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(properties.getProperty(SEARCH_FILTER_START_DATE)));
//            startDatePicker.setValue(LocalDate.of(calendarStartDate.get(Calendar.YEAR), calendarStartDate.get(Calendar.MONTH) + 1, calendarStartDate.get(Calendar.DAY_OF_MONTH)));
//        }
////        startDatePicker.setValue(LocalDate.now());
//        if (Objects.nonNull(properties.getProperty(SEARCH_FILTER_END_DATE)) && !"".equals(properties.getProperty(SEARCH_FILTER_END_DATE))) {
//            Calendar calendarEndDate = Calendar.getInstance();
//            calendarEndDate.setTime(new Date());
//            calendarEndDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(properties.getProperty(SEARCH_FILTER_END_DATE)));
//            endDatePicker.setValue(LocalDate.of(calendarEndDate.get(Calendar.YEAR), calendarEndDate.get(Calendar.MONTH) + 1, calendarEndDate.get(Calendar.DAY_OF_MONTH)));
//        }
////        endDatePicker.setValue(LocalDate.now());
        
        // 階層ツリーのノード選択イベント
        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<OrganizationInfoEntity>> observable, TreeItem<OrganizationInfoEntity> oldValue, TreeItem<OrganizationInfoEntity> newValue) -> {
            logger.debug("階層ツリーのノード選択イベント");
            if (isCancelMove) {
                // 移動キャンセル中は何もしない。
                isCancelMove = false;
                return;
            }
            if (Objects.nonNull(newValue) && newValue.getValue().getOrganizationId() != ROOT_ID) {
                //kanbanEditPermanenceData.setSelectedWorkHierarchy(newValue);
                // 別スレッドでカンバンを検索して、カンバンリストを更新する。
                //searchOrganizationDataTask();
            } else {
                // リストクリア
                //kanbanEditPermanenceData.setSelectedWorkHierarchy(null);
                //clearKanbanList();
                //agendaEntitys.clear();
                //showSchedule(agendaEntitys, createScheduleShowConfig(true));
            }

        });

        this.schedulePane.setOnMouseReleased((MouseEvent event) -> {
            try{
                String isDragged = properties.getProperty(ProductionNaviPropertyConstants.IS_DRAGGED);
                String draggedId = properties.getProperty(ProductionNaviPropertyConstants.DRAGGED_ID);
                if(Objects.nonNull(isDragged) && isDragged.equals("true") && Objects.nonNull(draggedId) && !draggedId.equals("")) {
                    for(WorkPlanCustomAgendaEntity agenda : getOrganizationAgenda(Long.parseLong(draggedId), selectStartDate, selectStopDate)) {
                        for(int i=0; i<agendaEntitys.size(); i++) {
                            if(agendaEntitys.get(i).getOrganizationId().equals(agenda.getOrganizationId())) {
                                agendaEntitys.set(i, agenda);
                            }
                        }
                    }
                    AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
                    Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                    showSchedule(agendaEntitys, createScheduleShowConfig(false), prop);
                }
            } catch(Exception ex) {
                logger.fatal(ex);
            } finally {
                properties.setProperty(ProductionNaviPropertyConstants.IS_DRAGGED, "false");
            }
        });
        
        hierarchyTree.setCellFactory((TreeView<OrganizationInfoEntity> o) -> new OrganizationTreeCell());

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
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_WORKER_SCALE_SIZE, String.valueOf(dateWidthSizeSlider.getValue()));
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
    
                showSchedule(agendaEntitys, createScheduleShowConfig(true), prop);
            }catch(IOException e){
                logger.fatal(e, e);
            }finally{
                event.consume();
            }
        });
        
//        blockUI(true);

        // 組織ツリーの表示処理
        Task task = new Task<Long>() {
            @Override
            protected Long call() throws Exception {
                long selectedOrganizationId = ROOT_ID;
                try {
//                    AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
                    Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

                    selectedOrganizationId = Long.parseLong(prop.getProperty(ProductionNaviPropertyConstants.SEARCH_WORKER_ORGANIZATION_ID, String.valueOf(ROOT_ID)));
                    logger.debug("●組織ツリーの表示処理.call()  組織階層ID:" + selectedOrganizationId);
                } catch (NumberFormatException ex) {
                    logger.fatal(ex, ex);
                }
                return selectedOrganizationId;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                // ツリーのルートノードを生成する。
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
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_WORKER_SCALE_POSITION, String.valueOf(newValue.doubleValue()));
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }catch(IOException e){
                logger.fatal(e, e);
            }
        });
        
        logger.info(":initialize() end");
    }
    
    /**
     * ガントチャート更新
     *
     * @param event
     */
    @FXML
    private void onUpdateFilter(ActionEvent event)  {
        // 組織が選択されている？
        if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId().equals(ROOT_ID)) {
            logger.warn(" > Please select an organization.");
            return;
        }
        if(Objects.isNull(startDatePicker.getValue()) || Objects.isNull(endDatePicker.getValue()) ){
            logger.warn(" > Please select startDate or endDate .");
            return;
        }
        
        // 表示期間
        Date scheduleStartDay = Date.from(startDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date scheduleEndDay  = Date.from(endDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        if( scheduleStartDay.compareTo(scheduleEndDay) > 0){
            Platform.runLater(() -> {
                // 開始日時が終了日時より遅い。
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.KanbanSearch"), LocaleUtils.getString("key.DateCompErrMessage"));
            });
            return;
        }

        // 選択した組織IDを取得
        long parentId = hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId();
        logger.debug(" >> 組織ID=" + parentId);

        // 検索条件を保持する
        try{
//            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            
            prop.setProperty(ProductionNaviPropertyConstants.SEARCH_WORKER_ORGANIZATION_ID, String.valueOf(parentId));
            prop.setProperty(ProductionNaviPropertyConstants.SEARCH_WORKER_START_DATE, dateFormatter.format(scheduleStartDay));
            prop.setProperty(ProductionNaviPropertyConstants.SEARCH_WORKER_STOP_DATE, dateFormatter.format(scheduleEndDay));

            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        Task task = new Task<List<ScheduleInfoEntity>>() {
            @Override
            protected List<ScheduleInfoEntity> call() throws Exception {
                // 子組織の件数を取得する。
                
                // 対象組織情報を取得（子の組織も）
                List<Long> organizationId = new ArrayList();
                REST_API.searchOrganizationHierarchys(parentId).stream().forEach(o->{organizationId.add(o.getOrganizationId());});
                return null;
            }
 
            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    searchOrganizationDataTask();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
//                    blockUI(false);
                }
            }
            @Override
            protected void failed() {
                super.failed();
                if (Objects.nonNull(this.getException())) {
                    logger.fatal(this.getException(), this.getException());
                }
//                blockUI(false);
            }

        };
        new Thread(task).start();

 
    }
    
    /**
     * ツリーのルートの表示を更新する。
     *
     * @param selectedId 更新後に選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合はルートを選択。)
     */
    private void createRoot(Long selectedId) {
        logger.info(":createRoot start() ");
        logger.info("  selectedId=" + selectedId);
        try {
            blockUI(true);

            // ルートが存在しない場合は新規作成する。
            if (Objects.isNull(this.rootItem)) {
                this.rootItem = new TreeItem<>(new OrganizationInfoEntity(ROOT_ID, null, LocaleUtils.getString("key.Organization"), null));
            }

            this.rootItem.getChildren().clear();

            Task task = new Task<List<OrganizationInfoEntity>>() {
                @Override
                protected List<OrganizationInfoEntity> call() throws Exception {
                    // 子組織の件数を取得する。
                    return REST_API.searchTopOrganizationHierarchys();
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        logger.info("  > selectedId:" + selectedId);
                        long count = this.getValue().size();
                        logger.info(" > 件数:" + this.getValue().size());
                        rootItem.getValue().setChildCount(count);

                        // 第一階層を設定する。
                        for(OrganizationInfoEntity data : this.getValue()){
                            TreeItem<OrganizationInfoEntity> item = new TreeItem<>(data);
                            if (data.getChildCount() > 0) {
                                item.getChildren().add(new TreeItem());
                            }
                            item.expandedProperty().addListener(expandedListener);
                            rootItem.getChildren().add(item);
                        }

                        TreeItem<OrganizationInfoEntity> selectNode = rootItem;
                        // ルート以外の選択
                        if(selectedId > ROOT_ID){
                            // 前回選択した親階層の情報を取得し、上位順にする。
                            List<List<OrganizationInfoEntity>> hierarchyAddIds = new ArrayList<>();
                            long id = selectedId;
                            while(true){
                                OrganizationInfoEntity data = REST_API.searchOrganization(id);
                                logger.debug("  > 今のID:" + id + ", 親のID:" + data.getParentId());

                                if(data.getParentId().equals(ROOT_ID)){
                                    logger.debug("  >> ルートIDのため抜ける");
                                    break;
                                }

                                List<OrganizationInfoEntity> datas = REST_API.searchOrganizationHierarchys(data.getParentId());
                                id = data.getParentId();
                                hierarchyAddIds.add(0,datas);
                            }
 
                            // 再帰的に選択にする
                            TreeItem<OrganizationInfoEntity> item = treeForExpanding(rootItem, hierarchyAddIds, selectedId);
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
                        } else {
                            // 選択ノードの指定がない場合は、ルートを選択状態にする。
                            hierarchyTree.getSelectionModel().select(rootItem);
                            blockUI(false);
                        }
                        
                        // 表示処理
                        onUpdateFilter(null);
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
        logger.info(":createRoot end.");
    }

    /**
     * ツリーを作成（再帰的）
     * 
     * @param _item アイテム
     * @param _list 子階層のリスト
     * @param _selectId 組織ID
     */
    private TreeItem<OrganizationInfoEntity> treeForExpanding(final TreeItem<OrganizationInfoEntity> _item, List<List<OrganizationInfoEntity>> _list, Long _selectId){
        logger.debug(":treeForExpanding start");
        if( Objects.isNull(_list) || _list.isEmpty() || _list.get(0).isEmpty()){
            logger.debug(" リストデータなし");
            return null;
        }
        
        TreeItem<OrganizationInfoEntity> value = null;

        logger.debug("  ●追加する子の階層件数:" + _list.size() );
        for(TreeItem<OrganizationInfoEntity> item : _item.getChildren() ){
            logger.debug("  > ツリー情報:" + item.getValue().toString() );
            // データ無ければ次へ
            if(Objects.isNull(item) || _list.isEmpty() || _list.get(0).isEmpty()){
                break;
            }
           
            // リストの情報なし
            if(_list.isEmpty() || _list.get(0).isEmpty()){
                break;
            }

            if(item.getValue().getOrganizationId().equals(_list.get(0).get(0).getParentId())){
                logger.debug("   >> 階層一致:" + item.getValue().getOrganizationId() );
                
                value = item;
                item.expandedProperty().removeListener(expandedListener);
                
                // 子の階層を削除
                item.getChildren().clear();
                
                // ツリーを選択状態にする
                item.setExpanded(true);

                for(OrganizationInfoEntity data : _list.get(0)){
                    logger.debug("     >>> 子の追加情報:" + data.toString());
                    TreeItem<OrganizationInfoEntity> itemChilde = new TreeItem<>(data);
                    if(data.getChildCount() > 0){
                        itemChilde.getChildren().add(new TreeItem());
                    }
                    itemChilde.expandedProperty().addListener(expandedListener);
                    item.getChildren().add(itemChilde);
                    
                    if(data.getOrganizationId().equals(_selectId)){
                        itemChilde.setExpanded(true);
                    }
                }

                item.expandedProperty().addListener(expandedListener);
                
                if(item.getValue().getOrganizationId().equals(_list.get(0).get(0).getOrganizationId())){
                    value = item;
                }

                // 先頭のリストを削除
                logger.debug(" list count Before:" + _list.size());
                _list.remove(0);
                logger.debug(" list count After:" + _list.size());

                // 再帰的に呼び出す
                if(Objects.nonNull(_list) && _list.size() > 0){
                    TreeItem<OrganizationInfoEntity> value1 = treeForExpanding(item, _list, _selectId);
                    if(Objects.nonNull(value1)){
                        value = value1;
                    }
                }
            }
        }
        
        logger.debug(":treeForExpanding end");
        
        return value;
    }
    
    /**
     * ツリーノード開閉イベントリスナー
     */
    private final ChangeListener expandedListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            logger.info(":expandedListener.changed start");
            // ノードを開いたら子ノードの情報を再取得して表示する。
            if (Objects.nonNull(newValue) && newValue.equals(true)) {
                TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
                expand(treeItem, null);
            }
            logger.info(":expandedListener.changed end");
        }
    };

    /**
     * 組織IDが一致するTreeItemを選択する (存在しない場合は親を選択する(削除後の選択用))
     *
     * @param parentItem 選択状態にするノーノの親ノード
     * @param selectedId 選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void selectedTreeItem(TreeItem<OrganizationInfoEntity> parentItem, Long selectedId) {
        logger.info(":selectedTreeItem start");
        Optional<TreeItem<OrganizationInfoEntity>> find = parentItem.getChildren().stream().
                filter(p -> p.getValue().getOrganizationId().equals(selectedId)).findFirst();

        if (find.isPresent()) {
            this.hierarchyTree.getSelectionModel().select(find.get());
        } else {
            this.hierarchyTree.getSelectionModel().select(parentItem);
        }
        this.hierarchyTree.scrollTo(this.hierarchyTree.getSelectionModel().getSelectedIndex());// 選択ノードが見えるようスクロール
        logger.info(":selectedTreeItem end");
    }

    /**
     * ツリーの指定したノードの表示を更新する。
     *
     * @param parentItem 表示更新するノード
     * @param selectedId 選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void expand(TreeItem<OrganizationInfoEntity> parentItem, Long selectedId) {
        logger.info("expand: parentItem={}", parentItem.getValue());
        try {
            //blockUI(true);

            parentItem.getChildren().clear();

            final long parentId = parentItem.getValue().getOrganizationId();

            Task task = new Task<List<OrganizationInfoEntity>>() {
                @Override
                protected List<OrganizationInfoEntity> call() throws Exception {
                    // 子組織の件数を取得する。
                    return REST_API.searchOrganizationHierarchys(parentId);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        this.getValue().stream().forEach((entity) -> {
                            TreeItem<OrganizationInfoEntity> item = new TreeItem<>(entity);
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
                        //blockUI(false);
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
     * 別スレッドでカンバンを検索して、カンバンリストを更新する。
     */
    private void searchOrganizationDataTask() {
        logger.debug("searchOrganizationDataTask start.");
        try {
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    searchOrganizationData(true);
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
    private void searchOrganizationData(boolean isDispWarning) {
        try {
            // 組織階層ID
            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())) {
                return;
            }
            long organizationId = hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId();

            // 条件をプロパティファイルに保存
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

            // 作業予定日
            Date scheduleStartDay = (Objects.isNull(startDatePicker.getValue())
                    ? null : adtekfuji.utility.DateUtils.getBeginningOfDate(Date.from(startDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
            Date scheduleEndDay = (Objects.isNull(endDatePicker.getValue())
                    ? null : adtekfuji.utility.DateUtils.getEndOfDate(Date.from(endDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));

            if (Objects.nonNull(scheduleStartDay) && Objects.nonNull(scheduleEndDay)) {
                if (0 > adtekfuji.utility.DateUtils.differenceOfDate(formatter.format(scheduleEndDay), formatter.format(scheduleStartDay))) {
                    if (isDispWarning) {
                        Platform.runLater(() -> {
                            // 開始日時が終了日時より遅い。
                            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.OrganizationSearch"), LocaleUtils.getString("key.DateCompErrMessage"));
                        });
                    }
                    return;
                }
            }

            agendaEntitys.clear();
            List<OrganizationInfoEntity> organizations = REST_API.searchOrganizationsOfOrganizationHierarchys(new ArrayList<>(), organizationId);

            agendaEntitys.addAll(getOrganizationsAgenda(organizations, scheduleStartDay, scheduleEndDay));
            showSchedule(agendaEntitys, createScheduleShowConfig(false), prop);
            
            //フィルター条件の更新
            if (Objects.nonNull(scheduleStartDay)) {
                Integer differenceStateDate = adtekfuji.utility.DateUtils.differenceOfDate(formatter.format(scheduleStartDay), formatter.format(new Date()));
                properties.setProperty(SEARCH_FILTER_START_DATE, differenceStateDate.toString());
            } else {
                properties.setProperty(SEARCH_FILTER_START_DATE, "");
            }
            if (Objects.nonNull(scheduleEndDay)) {
                Integer differenceEndDate = adtekfuji.utility.DateUtils.differenceOfDate(formatter.format(scheduleEndDay), formatter.format(new Date()));
                properties.setProperty(SEARCH_FILTER_END_DATE, differenceEndDate.toString());
            } else {
                properties.setProperty(SEARCH_FILTER_END_DATE, "");
            }

        } catch (IOException | ParseException ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * スケジュール表示
     *
     * @param agendaEntity スケジュールデータ
     */
    private void showSchedule(List<WorkPlanCustomAgendaEntity> agendaEntity, WorkPlanScheduleShowConfig config, Properties prop) {
        
        Platform.runLater(() -> {
            logger.info(":screenUpdate start.");

            WorkPlanScheduleInjecter injector = new WorkPlanScheduleInjecter(config);
            injector.injectDate(timePane, timeScrollPane);
            injector.injectSerial(serialPane, schedulePane, agendaEntity, scheduleType);
            injector.injectHolidays(holidayPane, REST_API.searchHolidays(config.getBaseStartDate(), config.getBaseEndDate()));
            
            //　TimeLineを現在の日時まで移動
            injector.setTimeLine(timeLine);
//            Platform.runLater(() -> {
//                ScheduleInjecter.setScheduleDateNowPoint(timeScrollPane, startDate, endDate);
//            });
            double scalePosition = 0;
            try{
                scalePosition = Double.valueOf(prop.getProperty(ProductionNaviPropertyConstants.SELECT_WORKER_SCALE_POSITION,"0"));
            }catch(NumberFormatException ex){
                logger.fatal(ex, ex);
            }finally{
                logger.debug(" スケールの位置:" + scalePosition);
                logger.debug("   min:" + timeScrollPane.getVmin() + ", max:" + timeScrollPane.getVmax());
                logger.debug("   min:" + timeScrollPane.getHmin() + ", max:" + timeScrollPane.getHmax());
                logger.debug("   X:" + timeScrollPane.getTranslateX() + ", Y:" + timeScrollPane.getTranslateY() + ", Z:" + timeScrollPane.getTranslateZ());
                
                this.setScalePosition(scalePosition);
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
                String scale = prop.getProperty(ProductionNaviPropertyConstants.SELECT_WORKER_SCALE_SIZE);
                logger.debug(" スライダー(GET):" + scale);
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
            logger.debug(" スライダー(VALUE):" + dateWidthSizeSlider.getValue() + ", "+ dateWidthSizeSlider.getMin() + "～" + dateWidthSizeSlider.getMax());

            // 表示倍率を設定
            config.setDailyWidthMagnification(dateWidthSizeSlider.getValue() / WorkPlanScheduleConstants.DEFAULT_MAGNIFICATION);
            // 表示する日付の範囲を設定
            config.setBaseMonthlyDate(adtekfuji.utility.DateUtils.differenceOfDate(adtekfuji.utility.DateUtils.toDate(endDatePicker.getValue()),
                    adtekfuji.utility.DateUtils.toDate(startDatePicker.getValue()),
                    WorkPlanDateTimeConstants.FORMAT_DATE) + 1);
            config.setBaseStartDate(adtekfuji.utility.DateUtils.toDate(startDatePicker.getValue()));
            config.setBaseEndDate(adtekfuji.utility.DateUtils.toDate(endDatePicker.getValue()));
            return config;

        } catch (NumberFormatException | ParseException ex) {
            logger.fatal(ex, ex);
            return new WorkPlanScheduleShowConfig();
        }
    }
    
    /**
     * UIロック
     *
     * @param flg 表示フラグ
     */
    private void blockUI(Boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        Progress.setVisible(flg);
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
     * 工程の追従　ON
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
//            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            prop.setProperty(ProductionNaviPropertyConstants.SELECT_WORKER_FOLLOWING, mode ? this.trackingON.getId() : this.trackingOFF.getId());
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
        logger.debug(":onStopDate start");

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
        
        logger.debug(":onStopDate end");
    }

    /**
     * 月内に該当する作業者の予定を取得
     *
     * @param organizations
     * @param startDate
     * @param endDate
     * @param statuses
     * @return
     */
    private List<WorkPlanCustomAgendaEntity> getOrganizationsAgenda(List<OrganizationInfoEntity> organizations, Date startDate, Date endDate) {
        List<WorkPlanCustomAgendaEntity> agendas = new LinkedList<>();

        try {
            List<Long> organizationIds = new ArrayList<>();
            for (OrganizationInfoEntity organization : organizations) {
                organizationIds.add(organization.getOrganizationId());
            }
            List<KanbanTopicInfoEntity> topics = searchOrganizationAgenda(organizationIds, startDate, endDate);

            List<Long> kanbanIds = new ArrayList<>();
            List<Long> allOrgaIds = new ArrayList<>();
            for(KanbanTopicInfoEntity topic : topics) {
                if(!kanbanIds.contains(topic.getKanbanId())) kanbanIds.add(topic.getKanbanId());
            }
            List<KanbanTopicInfoEntity> kanbanTopics = searchKanbanIdAgenda(kanbanIds, startDate, endDate);
            Map<Long, List<Long>> workKanbanOrgaIdsMap = new HashMap<>();
            for (Long kanbanId : kanbanIds) {
                List<KanbanTopicInfoEntity> topicList = kanbanTopics.stream().filter(o -> kanbanId.equals(o.getKanbanId())).collect(Collectors.toList());
                Map<Long, List<Long>> tmp = REST_API.getWorkKanbanOrgaIdsMap(topicList);
                for(Long key : tmp.keySet()) {
                    List<Long> orgaIds = tmp.get(key);
                    workKanbanOrgaIdsMap.put(key, orgaIds);
                    for(Long orgaId : orgaIds) {
                        if(!allOrgaIds.contains(orgaId)) allOrgaIds.add(orgaId);
                    }
                }
            }
            List<ScheduleInfoEntity> listSchedule = REST_API.searchSchedules(allOrgaIds, startDate, endDate);
            
            for (OrganizationInfoEntity organization : organizations) {
                List<KanbanTopicInfoEntity> temp = topics.stream().filter(o -> o.getOrganizationId().equals(organization.getOrganizationId())).collect(Collectors.toList());
                
                if(Objects.nonNull(temp) && temp.size() > 0){
                    WorkPlanCustomAgendaEntity agenda = convertOrganizationToAgenda(temp, organization, startDate, endDate, listSchedule, workKanbanOrgaIdsMap);
                    agendas.add(agenda);
                }
            }
        } catch (Exception ex) {
            DialogBox.alert(ex);
            logger.fatal(ex, ex);
        } finally {
        }

        return agendas;
    }
    
    /**
     * 月内に該当する作業者の予定を取得
     *
     * @param organization
     * @param startDate
     * @param endDate
     * @param statuses
     * @return
     */
    private List<WorkPlanCustomAgendaEntity> getOrganizationAgenda(Long organizationId, Date startDate, Date endDate) {
        List<WorkPlanCustomAgendaEntity> agendas = new LinkedList<>();

        try {
            List<Long> listOrganizationId = new ArrayList<>();
            listOrganizationId.add(organizationId);
            List<KanbanTopicInfoEntity> topics = searchOrganizationAgenda(listOrganizationId, startDate, endDate);

            List<Long> kanbanIds = new ArrayList<>();
            List<Long> allOrgaIds = new ArrayList<>();
            for(KanbanTopicInfoEntity topic : topics) {
                if(!kanbanIds.contains(topic.getKanbanId())) kanbanIds.add(topic.getKanbanId());
            }
            List<KanbanTopicInfoEntity> kanbanTopics = searchKanbanIdAgenda(kanbanIds, startDate, endDate);
            Map<Long, List<Long>> workKanbanOrgaIdsMap = new HashMap<>();
            for (Long kanbanId : kanbanIds) {
                List<KanbanTopicInfoEntity> topicList = kanbanTopics.stream().filter(o -> kanbanId.equals(o.getKanbanId())).collect(Collectors.toList());
                Map<Long, List<Long>> tmp = REST_API.getWorkKanbanOrgaIdsMap(topicList);
                for(Long key : tmp.keySet()) {
                    List<Long> orgaIds = tmp.get(key);
                    workKanbanOrgaIdsMap.put(key, orgaIds);
                    for(Long orgaId : orgaIds) {
                        if(!allOrgaIds.contains(orgaId)) allOrgaIds.add(orgaId);
                    }
                }
            }
            List<ScheduleInfoEntity> listSchedule = REST_API.searchSchedules(allOrgaIds, startDate, endDate);
            
            if(Objects.nonNull(topics) && topics.size() > 0){
                    WorkPlanCustomAgendaEntity agenda = convertOrganizationToAgenda(topics, REST_API.searchOrganization(organizationId)
                                                                                    , startDate, endDate, listSchedule, workKanbanOrgaIdsMap);
                    agendas.add(agenda);
            }
            
        } catch (Exception ex) {
            DialogBox.alert(ex);
            logger.fatal(ex, ex);
        } finally {
        }

        return agendas;
    }
    
    /**
     * 予実データをユニット情報に合わせてに変更する
     *
     * @param agendaEntitys 予実データ
     * @param ...
     */
    private WorkPlanCustomAgendaEntity convertOrganizationToAgenda(List<KanbanTopicInfoEntity> topics, OrganizationInfoEntity organization
            , Date startDate, Date endDate, List<ScheduleInfoEntity> listSchedule, Map<Long, List<Long>> workKanbanOrgaIdsMap) {
        WorkPlanCustomAgendaEntity organizationAgenda = new WorkPlanCustomAgendaEntity();
        if (!topics.isEmpty()) {
            organizationAgenda.setKanbanNameTitle(organization.getOrganizationName());
            organizationAgenda.setOrganizationId(organization.getOrganizationId());

            organizationAgenda.setScheduleCollection(listSchedule.stream()
                                                        .filter(s -> s.getFkOrganizationId().equals(organization.getOrganizationId()))
                                                        .collect(Collectors.toList())
                                                    );
            
            // カンバン予実情報を予実モニターに表示する末端の情報に変換
            List<WorkPlanCustomAgendaItemEntity> plans = new ArrayList<>();
            List<WorkPlanCustomAgendaItemEntity> actuals = new ArrayList<>();
            List<OrganizationInfoEntity> listOrganization = new ArrayList<>();
            
            for (KanbanTopicInfoEntity topic : topics) {
                
                List<Long> workKanbanOrgaIds = workKanbanOrgaIdsMap.get(topic.getWorkKanbanId());
                listOrganization.clear();
                workKanbanOrgaIds.stream().forEach(i-> {listOrganization.add(REST_API.searchOrganization(i));} );
                List<ScheduleInfoEntity> scheduleData = listSchedule.stream()
                                            .filter(s-> workKanbanOrgaIds.contains(s.getFkOrganizationId()))
                                            .filter(s-> (topic.getPlanStartTime().before(s.getScheduleFromDate())&& topic.getPlanEndTime().after(s.getScheduleFromDate()))                    
                                                    || (topic.getPlanStartTime().after(s.getScheduleFromDate()) && topic.getPlanEndTime().before(s.getScheduleToDate()))
                                                    || (topic.getPlanStartTime().before(s.getScheduleToDate()) && topic.getPlanEndTime().after(s.getScheduleToDate()))
                                            ).collect(Collectors.toList());
                
                WorkPlanDisplayStatusSelector displayStatusSelector = new WorkPlanDisplayStatusSelector(statuses);
                DisplayedStatusInfoEntity statusPlan = new DisplayedStatusInfoEntity();
                statusPlan = displayStatusSelector.getPlanDisplayStatus(topic.getWorkKanbanStatus()
                                                        , topic.getPlanStartTime(), topic.getPlanEndTime()
                                                        , topic.getActualStartTime(), topic.getActualEndTime(), breaktimes, listOrganization);
            
                WorkPlanCustomAgendaItemEntity plan = new WorkPlanCustomAgendaItemEntity();
                plan.createOrganizationPlanData(topic, statusPlan, scheduleData);
                plans.add(plan);
                
                WorkPlanCustomAgendaItemEntity actual = new WorkPlanCustomAgendaItemEntity();
                
                DisplayedStatusInfoEntity statusActual = new DisplayedStatusInfoEntity();
                statusPlan = displayStatusSelector.getActualDisplayStatus(topic.getWorkKanbanStatus()
                                                        , topic.getPlanStartTime(), topic.getPlanEndTime()
                                                        , topic.getActualStartTime(), topic.getActualEndTime(), breaktimes, listOrganization);
                actual.createOrganizationActualData(topic, statusActual);
                actuals.add(actual);
            }
            REST_API.createPlan(organizationAgenda, plans, startDate, endDate);
            REST_API.createActual(organizationAgenda, actuals, startDate, endDate);
        }
        
        return organizationAgenda;
    }
    
    /**
     * 指定した期間の作業者の作業予定を取得
     *
     * @param organizationIds
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    private List<KanbanTopicInfoEntity> searchOrganizationAgenda(List<Long> organizationIds, Date startDate, Date endDate) throws Exception {
        KanbanTopicSearchCondition condition = new KanbanTopicSearchCondition(KanbanTopicSearchCondition.ContentType.DAYS_ORGANIZATION);
        condition.setPrimaryKeys(organizationIds);
        condition.setFromDate(startDate);
        condition.setToDate(endDate);

        List<KanbanTopicInfoEntity> topics = REST_API.searchKanbanTopic(condition);
        
        return topics;
    }
    
    /**
     * 指定した期間のカンバンの作業予定を取得
     *
     * @param kanbanIds
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    private List<KanbanTopicInfoEntity> searchKanbanIdAgenda(List<Long> kanbanIds, Date startDate, Date endDate) throws Exception {
        KanbanTopicSearchCondition condition = new KanbanTopicSearchCondition(KanbanTopicSearchCondition.ContentType.DAYS_KANBAN);
        condition.setPrimaryKeys(kanbanIds);
        condition.setFromDate(startDate);
        condition.setToDate(endDate);

        List<KanbanTopicInfoEntity> topics = REST_API.searchKanbanTopic(condition);
        
        return topics;
    }

    @Override
    public boolean destoryComponent() {
        SplitPaneUtils.saveDividerPosition(workerPane, getClass().getSimpleName());
        return true;
    }
}
