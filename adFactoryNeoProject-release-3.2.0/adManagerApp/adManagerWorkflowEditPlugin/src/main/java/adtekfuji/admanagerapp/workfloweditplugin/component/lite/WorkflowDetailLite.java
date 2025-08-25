/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component.lite;

import adtekfuji.admanagerapp.workfloweditplugin.common.Constants;
import adtekfuji.admanagerapp.workfloweditplugin.common.PropertyTemplateLoader;
import adtekfuji.admanagerapp.workfloweditplugin.common.SelectedWorkflowAndHierarchy;
import adtekfuji.admanagerapp.workfloweditplugin.common.UriConvertUtils;
import adtekfuji.clientservice.WorkHierarchyInfoFacade;
import adtekfuji.clientservice.WorkInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toCollection;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.lite.LiteWorkflowInfo;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.DetailRecordFactory;
import jp.adtekfuji.javafxcommon.PropertyBindEntity;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.workflowmodel.CellBase;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelEndCell;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelStartCell;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkCell;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowModel;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

/**
 * 工程順編集画面
 *
 * @author kenji.yokoi
 */
@FxComponent(id = "WorkflowDetailLite", fxmlPath = "/fxml/admanagerworkfloweditplugin/lite/workflow_detail_lite.fxml")
public class WorkflowDetailLite implements Initializable, ArgumentDelivery, ComponentHandler {

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private final WorkHierarchyInfoFacade workHierarchyInfoFacade = new WorkHierarchyInfoFacade();
    private final WorkInfoFacade workInfoFacade = new WorkInfoFacade();
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
    private final Map<String, PropertyBindEntity> detailProperties = new LinkedHashMap<>();
    private final WorkflowModel model = new WorkflowModel();

    // 詳細表示の設定名
    private static final String DETAIL_WORKFLOW_NAME = "workflowName";
    private static final String DETAIL_MODEL_NAME = "modelName";
    private static final String DETAIL_WORK_NUMBER = "workNumber";

    private SelectedWorkflowAndHierarchy selected = null;
    private WorkHierarchyInfoEntity liteWorkHierarchy = null;
    private LinkedList<WorkInfoEntity> works = new LinkedList<>();
    private Table workTable;
    private WorkRecordFactoryLite workRecordFactory;

    //開いたときの最初に記述されていた情報
    private WorkflowInfoEntity cloneWorkflow;
    private LinkedList<WorkInfoEntity> cloneWorks = new LinkedList<>();

    public static final int WORKFLOW_REV_LIMIT = 999;// 工程順の版数の最大値
    private final long range = 100;

    private final static String LITE_HIERARCHY_TOP_KEY = "LiteHierarchyTop";
    private final static String LITE_WORK_NAME_FORMAT_KEY = "LiteWorkNameFormat";
    private final static String PICK_LITE_WORK_NAME_REGEX_KEY = "PickLiteWorkNameRegex";
    private String liteTreeName;
    private String liteWorkNameFormat;
    private String pickLiteWorkNameRegex;

    /**
     * 工程順名フィールドの有効/無効状態
     */
    private boolean disableWorkflowNameField = false;

    /**
     * 各種入力項目の有効/無効状態
     */
    private boolean disableOtherInputItems = true;

    @FXML
    private GridPane content;
    @FXML
    private Button cancelButton;
    @FXML
    private Button registButton;
    @FXML
    private VBox defaultFieldPane;
    @FXML
    private ComboBox prodWayCombo;
    @FXML
    private VBox workListPane;

    /**
     * 生産方式表示用セルクラス
     *
     */
    private final Callback<ListView<SchedulePolicyEnum>, ListCell<SchedulePolicyEnum>> schedulePolicyeCellFactory = new Callback() {
        @Override
        public Object call(Object param) {
            return new ListCell<SchedulePolicyEnum>() {
                @Override
                protected void updateItem(SchedulePolicyEnum item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText("");
                    } else {
                        switch(item) {
                            case PriorityParallel:
                                setText(rb.getString("key.Parallel"));
                                break;
                            case PrioritySerial:
                                setText(rb.getString("key.Series"));
                                break;
                        }
                    }
                }
            };
        }
    };

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
            this.prodWayCombo.setDisable(true);
        }

        //プロパティから取得
        this.liteTreeName = properties.getProperty(LITE_HIERARCHY_TOP_KEY);
        this.liteWorkNameFormat = properties.getProperty(LITE_WORK_NAME_FORMAT_KEY);
        this.pickLiteWorkNameRegex = properties.getProperty(PICK_LITE_WORK_NAME_REGEX_KEY);
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onCancel(ActionEvent event) {
        logger.info("onCancel:Start");
        if (destoryComponent()) {
            //変更が検出されないようコピー
            this.cloneWorkflow = createRegistWorkflow().clone();
            this.works.clear();
            this.works = (LinkedList<WorkInfoEntity>)this.cloneWorks.clone();
            this.selected.getRefreshCallback().onRefresh();
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onRegist(ActionEvent event) {
        try {
            logger.info("onRegist:Start");
            if (this.registWorkflow()) {
                // 変更が検出されないようコピー
                this.cloneWorkflow = createRegistWorkflow().clone();
                this.works.clear();
                this.works = (LinkedList<WorkInfoEntity>)this.cloneWorks.clone();
                this.content.visibleProperty().set(false);
                this.selected.getRefreshCallback().onRefresh();
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
            // 工程の新規作成時、工程プロパティのテンプレートデータを追加する
            if (!Objects.nonNull(selected.getWorkflowInfo().getWorkflowId())) {
                selected.getWorkflowInfo().setKanbanPropertyTemplateInfoCollection(PropertyTemplateLoader.getWorkflowProperties());
            }
            Platform.runLater(() -> {
                getWorkList(selected);
                createDetailView(selected);
                //最初に表示された情報をコピー
                cloneWorkflow = createRegistWorkflow().clone();
            });
        }
    }
    
    /**
     * 工程順に紐づている工程を取得
     */
    private void getWorkList(SelectedWorkflowAndHierarchy selectedInfo) {
        this.works.clear();
        this.cloneWorks.clear();
        
        // Lite工程階層を取得.
        this.liteWorkHierarchy = this.workHierarchyInfoFacade.findHierarchyName(this.liteTreeName);
        if (Objects.isNull(liteWorkHierarchy.getWorkHierarchyId())) {
            sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.alert.errorInfo"), rb.getString("key.alert.serverError"));
            return;
        }
        
        // 工程を取得.
        LinkedList<WorkInfoEntity> workList = new LinkedList<>();
        Long workflowId = selectedInfo.getWorkflowInfo().getWorkflowId();
        if (!Objects.isNull(workflowId)) {
            long count = workInfoFacade.getWorkCountByWorkflow(workflowId);
            for (long from = 0; from < count; from += this.range) {
                List<WorkInfoEntity> result = workInfoFacade.getWorkRangeByWorkflow(selectedInfo.getWorkflowInfo().getWorkflowId(), from, from + this.range - 1);
                workList.addAll(result);
            }
        }
        // オーダ順にする
        List<ConWorkflowWorkInfoEntity> conWorkflowWork = selectedInfo.getWorkflowInfo().getConWorkflowWorkInfoCollection();
        if (conWorkflowWork != null) {
            conWorkflowWork.sort(Comparator.comparing(s -> s.getWorkflowOrder()));        
            conWorkflowWork.forEach((entity) -> {
                Optional<WorkInfoEntity> findWork = workList.stream().filter(w -> w.getWorkId().equals(entity.getFkWorkId())).findFirst();
                if (findWork.isPresent()) {
                    WorkInfoEntity work = findWork.get();
                    //工程名だけ抽出
                    try
                    {
                        Matcher m = Pattern.compile(this.pickLiteWorkNameRegex).matcher(work.getWorkName());
                        if (m.find()) {
                            work.setWorkName(m.group(1));
                        }
                    } catch(Exception ex) {
                    }
                    this.works.add(work);
                    this.cloneWorks.add(work.clone());
                }
            });
        } 
        //this.cloneWorks = (LinkedList<WorkInfoEntity>)this.works.clone();
    }

    /**
     * 詳細表示処理
     *
     * @param selectedInfo
     */
    private void createDetailView(SelectedWorkflowAndHierarchy selectedInfo) {
        this.defaultFieldPane.getChildren().clear();
        this.detailProperties.clear();
        WorkflowInfoEntity workflowInfo = selectedInfo.getWorkflowInfo();
        // 工程順名
        this.detailProperties.put(DETAIL_WORKFLOW_NAME, (PropertyBindEntity) PropertyBindEntity.createString(rb.getString("key.OrderProcessesName") + rb.getString("key.RequiredMark"), selectedInfo.getWorkflowInfo().getWorkflowName()).setPrefWidth(300.0).setMaxLength(120));
        // モデル名
        this.detailProperties.put(DETAIL_MODEL_NAME, (PropertyBindEntity) PropertyBindEntity.createString(rb.getString("key.ModelName"), workflowInfo.getModelName()).setPrefWidth(300.0).setMaxLength(256));
        // 作業番号
        this.detailProperties.put(DETAIL_WORK_NUMBER, (PropertyBindEntity) PropertyBindEntity.createString(LocaleUtils.getString("key.IndirectWorkNumber"), selectedInfo.getWorkflowInfo().getWorkflowNumber()).setPrefWidth(300.0).setMaxLength(64));
        // 生産方式
        this.prodWayCombo.setItems(Stream.of(SchedulePolicyEnum.values()).collect(toCollection(FXCollections::observableArrayList)));
        this.prodWayCombo.setButtonCell(schedulePolicyeCellFactory.call(null));
        this.prodWayCombo.setCellFactory(schedulePolicyeCellFactory);
        this.prodWayCombo.setValue(workflowInfo.getSchedulePolicy());

        Table detailTable = new Table(defaultFieldPane.getChildren()).isAddRecord(false);
        detailTable.setAbstractRecordFactory(new DetailRecordFactory(detailTable, new LinkedList(detailProperties.values())));

        // 工程リスト作成
        this.createWorkList();

        // 工程/追加工程
        if (Objects.isNull(selectedInfo.getWorkflowInfo().getConWorkflowWorkInfoCollection())) {
            selectedInfo.getWorkflowInfo().setConWorkflowWorkInfoCollection(new ArrayList<>());
        }
        if (Objects.isNull(selectedInfo.getWorkflowInfo().getConWorkflowSeparateworkInfoCollection())) {
            selectedInfo.getWorkflowInfo().setConWorkflowSeparateworkInfoCollection(new ArrayList<>());
        }

        // 各種入力項目の表示状態設定
        setInputItemViewState(detailTable);
    }

    /**
     * 工程リストの生成
     */
    private void createWorkList() {
        this.workListPane.getChildren().clear();
        this.workTable = new Table(this.workListPane.getChildren())
                .styleClass("ContentTitleLabel")
                .isColumnTitleRecord(true)
                .isChangeDataRowColor(true)
                .isAddRecord(true);
        this.workRecordFactory = new WorkRecordFactoryLite(this.workTable, this.works);
        this.workTable.setAbstractRecordFactory(this.workRecordFactory);
    }

    /**
     * 各種入力項目の表示状態を設定する。
     *
     * @param detailTable 詳細テーブル
     */
    private void setInputItemViewState(Table detailTable) {

        // 登録ボタン、適用ボタン、改訂ボタン、版数フィールド、その他入力項目の初期値は無効
        // 工程順名フィールドの初期値は有効
        boolean disableApplyButton = true;
        this.disableOtherInputItems = true;
        this.disableWorkflowNameField = false;

        WorkflowInfoEntity workflow = selected.getWorkflowInfo();
        Integer latestRev = workflow.getLatestRev();

        if (this.loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
            // 工程順編集権限あり
            disableApplyButton = false;
            this.disableOtherInputItems = false;
            if (Objects.nonNull(latestRev)) {
                if (latestRev > 1) {
                    this.disableWorkflowNameField = true;
                }
            }
        } else {
            // 工程順編集権限なし
            this.disableWorkflowNameField = true;
        }

        // カンバンで使われている場合は無効にする
        if (Objects.nonNull(workflow.getWorkflowId()) && workflowInfoFacade.existAssignedKanban(workflow.getWorkflowId(), true)) {
            // disableApplyButton = true;
            disableWorkflowNameField = true;
            disableOtherInputItems = true;
        }

        // 登録ボタン、適用ボタンの有効/無効を切り替える
        registButton.setDisable(disableApplyButton);
        cancelButton.setDisable(false);

        // 上記以外の入力項目の有効/無効を切り替える
        Platform.runLater(() -> {
            // 工程順名フィールド
            detailTable.findLabelRow(rb.getString("key.OrderProcessesName") + rb.getString("key.RequiredMark")).ifPresent(index -> {
                // 工程順名フィールド
                TextField textField = (TextField) detailTable.getNodeFromBody((int) index, 1).get();
                textField.setDisable(this.disableWorkflowNameField);
            });
            // モデル名フィールド
            //detailTable.findLabelRow(rb.getString("key.ModelName")).ifPresent(index -> {
            //    TextField textField = (TextField) detailTable.getNodeFromBody((int) index, 1).get();
            //    textField.setDisable(this.disableOtherInputItems);
            //});
            // 生産順ボタン
            this.prodWayCombo.setDisable(this.disableOtherInputItems);
            // 工程フィールド
            //this.workListPane.setDisable(this.disableOtherInputItems);
            this.workRecordFactory.setDisable(this.disableOtherInputItems);
            this.workTable.setDisable(this.disableOtherInputItems);
        });
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
            sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.Warning"), rb.getString("key.NotInputMessage"));
            return false;
        }

        if (!validItems()) {
            return false;
        }

        //工程順の更新
        WorkflowInfoEntity workflow = this.createRegistWorkflow();
        if (Objects.isNull(workflow)) {
            return false;
        }
        
        // ワークフロー図の準備
        workflow.setWorkflowDiaglam(null);
        SchedulePolicyEnum policy = workflow.getSchedulePolicy();
        WorkflowModel workflowModel = new WorkflowModel();
        WorkflowPane workflowPane = workflowModel.getWorkflowPane();
        workflowPane.setWorkflowEntity(workflow);
        workflowModel.createWorkflow(workflowPane);
        workflowPane.getCellList().stream().forEach(w -> w.setSelected(true));   //ここでStartCellを選択状態にしておかないとWorkCellが追加できない.
        CellBase cellBase = workflowModel.getWorkflowPane().getSelectedCellBase();
        ParallelStartCell parallelStartCell = workflowModel.createParallelStartCell();
        ParallelEndCell parallelEndCell = workflowModel.createParallelEndCell(parallelStartCell);
        if (policy == SchedulePolicyEnum.PriorityParallel && this.works.size() > 0) {
            workflowModel.addGateway(cellBase, parallelStartCell, parallelEndCell);
        }
        // 新規に追加した工程に他の情報をセットする
        for (int loop = 0; loop < this.works.size(); loop++) {
            // ワークフローの設定
            WorkCell workCell = workflowModel.createWorkCell(this.works.get(loop));
            workCell.setSelected(true);
            if (policy == SchedulePolicyEnum.PrioritySerial) {
                workflowModel.add(cellBase, workCell);
                cellBase = workCell;
            } else {
                workflowModel.add(parallelStartCell, workCell);
            }
        }
        workflowModel.updateWorkflowOrder();
        workflow.setWorkflowDiaglam(workflowModel.getWorkflowDiaglam());
        
        // 工程名に工程順名を付ける
        List<WorkInfoEntity> workList = new LinkedList<>();
        this.works.stream().forEach(work ->{
            work.updateMember();
            WorkInfoEntity w = work.clone();
            w.setWorkId(work.getWorkId());
            w.setWorkName(String.format(this.liteWorkNameFormat, workflow.getWorkflowName(), work.getWorkName()));
            w.setParentId(work.getParentId());
            w.setTaktTime(work.getTaktTime());
            w.setContentType(work.getContentType());
            w.setUpdatePersonId(work.getUpdatePersonId());
            w.setUpdateDatetime(work.getUpdateDatetime());
            workList.add(w);
        });

        LiteWorkflowInfo liteWorkflow = new LiteWorkflowInfo();
        liteWorkflow.setWorkflow(workflow);
        liteWorkflow.setWorks(workList);

        ResponseEntity res;
        if (Objects.nonNull(workflow.getWorkflowId()) && workflow.getWorkflowId() != 0) {
            res = workflowInfoFacade.updateLiteWork(liteWorkflow);
        } else {
            res = workflowInfoFacade.registLiteWork(liteWorkflow);
        }

        if (Objects.nonNull(res) && !ResponseAnalyzer.getAnalyzeResult(res)) {
            if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.ServerErrTitle"), rb.getString("key.ServerProblemMessage"));
            }
            else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                // 排他バージョンが異なる。
                sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.RegistOrderProcesses"), rb.getString("key.alert.differentVerInfo"));
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
        this.cloneWorks.clear();
        this.cloneWorks = (LinkedList<WorkInfoEntity>)this.works.clone();

        logger.info("registWorkflow:End");

        return true;
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
        
        for (WorkInfoEntity work : this.works) {
            if (Strings.isEmpty(work.getWorkName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 入力項目チェック
     *
     * @return 入力データ有効:true, 入力エラー:false
     */
    private boolean validItems() {
        // 工程数のチェック
        if (this.works.isEmpty()) {
            sc.showAlert(AlertType.WARNING, rb.getString("key.DialogWorkSettiong"), rb.getString("key.RequiredWorkProcess"));
            return  false;
        }
        // 工程名の重複チェック
        for (int i = 0; i < this.works.size(); i++) {
            for (int j = 0; j < this.works.size(); j++) {
                if (i != j && this.works.get(i).getWorkName().equals(this.works.get(j).getWorkName())) {
                    sc.showAlert(AlertType.WARNING, rb.getString("key.DialogWorkSettiong"), rb.getString("key.AddErrNameOverLap"));
                    return  false;
                }
            }
        }
        return true;
    }

    /**
     * 現在のフィールドに記入された情報を構築する
     *
     * @return WorkflowInfoEntity
     */
    private WorkflowInfoEntity createRegistWorkflow() {
        Date now = new Date();

        // 工程順の更新
        WorkflowInfoEntity workflow = selected.getWorkflowInfo();
        workflow.setWorkflowName(((StringProperty) detailProperties.get(DETAIL_WORKFLOW_NAME).getProperty()).get());
        // モデル名
        workflow.setModelName(((StringProperty) detailProperties.get(DETAIL_MODEL_NAME).getProperty()).get());
        // 作業番号
        String workNumber = null;
        if (Objects.nonNull(detailProperties.get(DETAIL_WORK_NUMBER).getProperty())) {
            workNumber = ((StringProperty) detailProperties.get(DETAIL_WORK_NUMBER).getProperty()).get();
        }
        workflow.setWorkflowNumber(workNumber);
        // 生産方式        
        workflow.setSchedulePolicy((SchedulePolicyEnum)prodWayCombo.getValue());

        SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
        try {
            workflow.setOpenTime(sd.parse("00:00"));
            workflow.setCloseTime(sd.parse("00:00"));
        } catch (ParseException ex) {
        }

        workflow.setFkUpdatePersonId(loginUser.getId());
        workflow.setUpdateDatetime(now);
        //workflow.setVerInfo(1);

        // 工程リスト
        for (int loop = 0; loop < this.works.size(); loop++) {
            WorkInfoEntity work = this.works.get(loop);
            work.setWorkId((long)loop + 1); //工程IDは 1から振っていく
            work.updateMember();
            work.setParentId(this.liteWorkHierarchy.getWorkHierarchyId());
            if (!Boolean.valueOf(properties.getProperty(Constants.ENABLE_LITE_TAKT_TIME, Constants.ENABLE_LITE_TAKT_TIME_DEFAULT))) {
                // Lite工程設定で標準作業時間を使用しない場合は、
                // タクトタイムに値が入らないため、明示的にタクトタイムに０をセットする
                work.setTaktTime(0);
            }
            work.setContentType(ContentTypeEnum.STRING);
            work.setUpdatePersonId(loginUser.getId());
            work.setUpdateDatetime(now);
            // 既存の工程を削除後に同名の工程を追加した場合は、削除前の同名の工程の排他用バージョンを引き継ぐ
            if (Objects.isNull(work.getVerInfo())) {
                work.setVerInfo(getWorkVerInfoFromClone(work.getWorkName()));
            }
        }

        return workflow;
    }
    
    /**
     * 最初に表示された同名の工程情報から排他用バージョンを取得する。
     * @param workName 工程名
     * @return 排他用バージョン
     */
    private Integer getWorkVerInfoFromClone(String workName) {
        Optional<WorkInfoEntity> targetWork = this.cloneWorks.stream().filter(p -> p.getWorkName().equals(workName)).findFirst();
        if (targetWork.isPresent()) {
            return targetWork.get().getVerInfo();
        } else {
            return null;
        }
    }

    /**
     * 最初に表示された情報から変更があったか調べる
     *
     * @return
     */
    private boolean isChanged() {
        // 編集権限なし、または、編集不可モード時は常に無変更
        if (Objects.isNull(selected)
                || detailProperties.isEmpty()
                || !loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
            return false;
        }

        //新規作成時の対応
        //本来cloneする必要はないが工程順はこのメソッドでは取得できずnullになるためclone(内部でnullのとき空配列にしてる)
        //このEntityは工程順WorkflowAssemblyでも使っているがそっちではnullにならないため暫定処置
        WorkflowInfoEntity entity = createRegistWorkflow().clone();
        //キャンセル以外の画面遷移で変更を検査
        if (!entity.equalsDisplayInfo(cloneWorkflow)) {
            return true;
        }
        
        //工程リストを確認する
        boolean bRet = false;
        if (this.works.size() == this.cloneWorks.size()) {
            for (int loop = 0; loop < this.works.size(); loop++) {
                WorkInfoEntity work = this.works.get(loop).clone();
                if (!work.displayInfoEquals(this.cloneWorks.get(loop))) {
                    bRet = true;
                    break;
                }
            }
        } else {
            bRet = true;
        }
        
        return bRet;
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
            String title = rb.getString("key.confirm");
            String message = rb.getString("key.confirm.destroy");

            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            if (ButtonType.YES == buttonType) {
                boolean ret = registWorkflow();
                if (ret) {
                    this.content.visibleProperty().set(false);
                    this.selected.getRefreshCallback().onRefresh();
                }
                return ret;
            } else if (ButtonType.CANCEL == buttonType) {
                return false;
            } else if (ButtonType.NO == buttonType) {
                    this.content.visibleProperty().set(false);
                    this.selected.getRefreshCallback().onRefresh();
            }
        }

        return true;
    }

    /**
     * 設定を設定ファイルに保存する
     */
    private void saveProperties() {
        try {

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
