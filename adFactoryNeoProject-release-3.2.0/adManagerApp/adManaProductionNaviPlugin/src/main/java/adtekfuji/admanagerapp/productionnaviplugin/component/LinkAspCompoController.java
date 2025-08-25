/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.KanbanEditPermanenceData;
import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkHierarchyEditor;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkflowEditPermanenceData;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkflowHierarchyEditor;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ImportFormatFileUtil;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ProductionNaviUtils;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WorkHierarchyInfoFacade;
import adtekfuji.clientservice.WorkInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowHierarchyInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import com.opencsv.CSVReader;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.MessageEntity;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.ResultResponse;
import jp.adtekfuji.adFactory.entity.asprova.ImportAsprovaBomCsv;
import jp.adtekfuji.adFactory.entity.asprova.ImportAsprovaPlanCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanPropertyCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportWorkKanbanCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportWorkKanbanPropertyCsv;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.importformat.ImportFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.WorkKanbanPropFormatInfo;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.LabelInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.productplan.ProductPlanInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.*;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.workflowmodel.CellBase;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelEndCell;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelStartCell;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkCell;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowModel;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 生産管理ナビ asprova連携画面
 *
 * @author (HN)y-harada
 * @version 2.1.7
 * @since 2021/03/11
 */
@FxComponent(id = "LinkAspCompo", fxmlPath = "/fxml/compo/link_asp_compo.fxml")
public class LinkAspCompoController implements Initializable, ArgumentDelivery, ComponentHandler {

    // 定数
    private static final String CHARSET_SJIS = "MS932";
    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String DELIMITER = "\\|";
    private static final Long RANGE = 20L;

    private final static WorkHierarchyInfoFacade workHierarchyInfoFacade = new WorkHierarchyInfoFacade();
    private final static WorkflowHierarchyInfoFacade workflowHierarchyInfoFacade = new WorkflowHierarchyInfoFacade();

    // フィールド
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final KanbanHierarchyInfoFacade kanbanHierarchyInfoFacade = new KanbanHierarchyInfoFacade();
    private final WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
    private final WorkKanbanInfoFacade workKanbanInfoFacade = new WorkKanbanInfoFacade();
    private final WorkInfoFacade workInfoFacade = new WorkInfoFacade();
    private final OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final List<String> kanbanCsvFilenames = new ArrayList<>();

    private Object argument = null;
    private boolean isImportProccessing = false;
    private ImportFormatInfo importFormatInfo = null;

    private final TreeView<WorkHierarchyInfoEntity> workHierarchyTree = new TreeView<>();
    private final TreeView<WorkflowHierarchyInfoEntity> workFlowHierarchyTree = new TreeView<>();
    private final TreeView<KanbanHierarchyInfoEntity> kanbanHierarchyTree = new TreeView<>();
    private final WorkflowEditPermanenceData permanenceData = WorkflowEditPermanenceData.getInstance();
    private final WorkflowEditPermanenceData permanenceFlowData = WorkflowEditPermanenceData.getInstance();
    private final KanbanEditPermanenceData kanbanEditPermanenceData = KanbanEditPermanenceData.getInstance();
    private WorkHierarchyEditor workHierarchyEditor;
    private WorkflowHierarchyEditor workflowHierarchyEditor;

    // タブ：生産計画読み込み
    public static final int TAB_IDX_PLAN = 0;
    // タブ：実績出力
    public static final int TAB_IDX_EXPORT = 1;
    // タブ：BOM読み込み
    public static final int TAB_IDX_BOM = 2;

    /**
     * タブ
     */
    @FXML
    private TabPane tabImportMode;

    @FXML
    private TextField importAspPlanFileField;
    @FXML
    private TextField kanbanHierarchyField;
    @FXML
    private TextField kanbanHierarchyFieldForExport;
    @FXML
    private TextField importAspMonthlyPlanFolderField;
    @FXML
    private TextField importExcelFileField;
    @FXML
    private TextField exportFolderField;
    @FXML
    private TextField importBomFileField;
    @FXML
    private TextField workHierarchyField;
    @FXML
    private TextField workflowHierarchyField;
    @FXML
    private ComboBox<WorkConnectionTypeEnum> importBomWorkConnectComboBox;
    @FXML
    private ListView<String> resultList;
    @FXML
    private Pane progressPane;
    @FXML
    private Button cancelButton;
    @FXML
    private Button selectKanbanHierarchyButton;
    @FXML
    private Button selectWorkHierarchyButton;
    @FXML
    private Button selectWorkflowHierarchyButton;
    @FXML
    private Button selectKanbanHierarchyButtonForExport;
    @FXML
    private CheckBox changeKanbansWorkflow;

    public void onSelectMonthlyPlanFolderButton(ActionEvent actionEvent) {
        blockUI(true);
        try {
            DirectoryChooser dc = new DirectoryChooser();
            File fol = new File(importAspMonthlyPlanFolderField.getText());
            if (fol.exists() && fol.isDirectory()) {
                dc.setInitialDirectory(fol);
            }

            dc.setTitle("CSV入力元フォルダ選択");
            File selectedFile = dc.showDialog(sc.getStage().getScene().getWindow());
            if (Objects.nonNull(selectedFile)) {
                this.importAspMonthlyPlanFolderField.setText(selectedFile.getPath());

                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_LINK_ASP_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_CSV));
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    boolean isCSVFile(File file) {
        String fileName = file.getName();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        return "csv".equals(ext);
    }

    /**
     * 設定フィールドをチェック
     *
     * @param kanbanHierarchy 階層名
     * @param fileName        ファイル名
     * @return チェック結果
     */
    private boolean checkMonthlyPlanImportTextField(final String folderName) {

        // 確認するフォルダとファイルのパスを指定する
        File folder = new File(folderName);

        if (Objects.isNull(exportFolderField.getText()) || exportFolderField.getText().isEmpty()) {
            addResult("フォルダを選択してください。");
            return false;
        }

        // フォルダの存在を確認する
        if (!folder.exists()) {
            addResult("正しいフォルダを選択してください。");
            return false;
        }

        File[] fileArray = folder.listFiles();
        if (Objects.isNull(fileArray)) {
            addResult("正しいフォルダを選択してください。");
            return false;
        }

        if (Stream.of(fileArray)
                .filter(File::exists)
                .filter(File::isFile)
                .filter(File::canRead)
                .noneMatch(this::isCSVFile)) {
            addResult("csvファイルが見つかりません");
            return false;
        }
        return true;
    }


    public void onAspMonthlyPlanImportButton(ActionEvent event) {
        final String folderName = this.importAspMonthlyPlanFolderField.getText();
        blockUI(true);
        try {
            // 初期化
            this.resultList.getItems().clear();
            ProductionNaviUtils.setFieldNormal(this.importAspMonthlyPlanFolderField);
            if (!checkMonthlyPlanImportTextField(folderName)) {
                return;
            }
        } finally {
            blockUI(false);
        }

        // インポート
        this.importAspMonthlyPlanTask(folderName);
    }


    /**
     * 生産計画インポート処理
     *
     * @param fileName フォルダ
     */
    private void importAspMonthlyPlanTask(String folderName) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                isImportProccessing = true;
                blockUI(true);
                addResult(String.format("%s [%s]", LocaleUtils.getString("key.ImportKanbanStart"), folderName));// 生産計画取り込み開始
                addResult((new Date()).toString());
                try {
                    importAspMonthlyPlan(folderName);
                } finally {
                    addResult(LocaleUtils.getString("key.ImportKanbanEnd"));// 生産計画取り込み終了
                    addResult((new Date()).toString());
                }

                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                blockUI(false);
                isImportProccessing = false;
            }

            @Override
            protected void failed() {
                super.failed();
                try {
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                        // 読み込み中に、不明なエラーが発生しました。
                        addResult("   > " + LocaleUtils.getString("key.import.production.error"));
                        logger.fatal("   > " + LocaleUtils.getString("key.import.production.error"));
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                    isImportProccessing = false;
                }
            }
        };
        new Thread(task).start();
    }


    /**
     * 月初計画読込
     *
     * @param fileName ファイル名
     * @return 読込結果
     */
    private Optional<List<ImportAsprovaPlanCsv>> readMonthlyPlanCsv(String fileName) {
        // 生産計画ファイル 読込
        addResult(LocaleUtils.getString("key.import.work.plan.kanban") + " [" + fileName + "]");
        Optional<List<ImportAsprovaPlanCsv>> optImportPlans = readAspKanbanCsv(fileName);
        if (!optImportPlans.isPresent()) {
            return Optional.empty();
        }
        List<ImportAsprovaPlanCsv> importPlans = optImportPlans.get();

        // データチェック
        List<String> errorMessage =
                importPlans
                        .stream()
                        .map(ImportAsprovaPlanCsv::checkData)
                        .flatMap(Collection::stream)
                        .filter(msg -> !StringUtils.isEmpty(msg))
                        .collect(toList());

        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        return optImportPlans;
    }

    /**
     * 日にち変換
     *
     * @param time
     * @return
     */
    Optional<Date> convetDateCategory(String time) {
        Optional<Date> optDate = stringToDateTime(time);
        if (!optDate.isPresent()) {
            Optional.empty();
        }

        Date date = optDate.get();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int minute = cal.get(Calendar.MINUTE);
        if (minute < 30) {
            cal.set(Calendar.MINUTE, 0);
        } else {
            cal.set(Calendar.MINUTE, 30);
        }
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return Optional.of(cal.getTime());
    }


    /**
     * 月初計画読込
     *
     * @param folderName フォルダ名
     * @return 戻り値
     * @throws Exception
     */
    private Map<String, Integer> importAspMonthlyPlan(String folderName) {
        // 確認するフォルダとファイルのパスを指定する
        File folder = new File(folderName);
        File[] fileArray = folder.listFiles();
        if (Objects.isNull(fileArray)) {
            addResult("正しいフォルダを選択してください。");
            return null;
        }

        // ファイルチェック
        List<String> fileNames
                = Stream.of(fileArray)
                .filter(File::exists)
                .filter(File::isFile)
                .filter(File::canRead)
                .map(File::getAbsolutePath)
                .collect(toList());
        if (fileNames.isEmpty()) {
            addResult("ファイルが存在しません。");
            return null;
        }

        // ファイル読込
        List<ImportAsprovaPlanCsv> importAsprovaPlanCsvs = new ArrayList<>();
        for (String fileName : fileNames) {
            Optional<List<ImportAsprovaPlanCsv>> optImportPlans = readMonthlyPlanCsv(fileName);
            if (!optImportPlans.isPresent()) {
                return null;
            }
            importAsprovaPlanCsvs.addAll(optImportPlans.get());
        }

        final List<ImportAsprovaPlanCsv.ASP_COL> groupingList = Arrays.asList(
                ImportAsprovaPlanCsv.ASP_COL.ITEM_NAME, // 品名
                ImportAsprovaPlanCsv.ASP_COL.ITEM_CODE, // 品目
                ImportAsprovaPlanCsv.ASP_COL.NICK_NAME, // ニックネーム
                ImportAsprovaPlanCsv.ASP_COL.WORK_NUMBER, // 工程番号
                ImportAsprovaPlanCsv.ASP_COL.MAIN_RESOUCE, //設備
                ImportAsprovaPlanCsv.ASP_COL.ASSERT_NUMBER, // 資産番号
                ImportAsprovaPlanCsv.ASP_COL.SEGMENT // セグメント
        );

        List<ProductPlanInfoEntity> productPlanInfoEntities
                = new ArrayList<>(
                importAsprovaPlanCsvs
                        .stream()
                        .collect(groupingBy(imp ->
                                        groupingList.stream().map(val -> imp.getOrDefault(val, "null")).collect(joining("@_@")) // 追加情報
                                                + "@_@WORK_NAME" + imp.getWorkName()
                                                + "@_@TIME" + convetDateCategory(imp.get(ImportAsprovaPlanCsv.ASP_COL.WORK_END_TIME)).orElse(null),
                                collectingAndThen(
                                        toList(),
                                        list -> {
                                            final Long lotCount = list.stream().map(imp -> imp.get(ImportAsprovaPlanCsv.ASP_COL.LOT_COUNT)).mapToLong(Long::valueOf).sum();
                                            return new ProductPlanInfoEntity(
                                                    list.get(0).get(ImportAsprovaPlanCsv.ASP_COL.ITEM_NAME),
                                                    list.get(0).get(ImportAsprovaPlanCsv.ASP_COL.ITEM_CODE),
                                                    list.get(0).get(ImportAsprovaPlanCsv.ASP_COL.NICK_NAME),
                                                    list.get(0).get(ImportAsprovaPlanCsv.ASP_COL.WORK_NUMBER),
                                                    "",
                                                    list.get(0).getWorkName(),
                                                    list.get(0).get(ImportAsprovaPlanCsv.ASP_COL.MAIN_RESOUCE),
                                                    list.get(0).get(ImportAsprovaPlanCsv.ASP_COL.ASSERT_NUMBER),
                                                    convetDateCategory(list.get(0).get(ImportAsprovaPlanCsv.ASP_COL.WORK_END_TIME)).orElse(null),
                                                    list.get(0).get(ImportAsprovaPlanCsv.ASP_COL.SEGMENT),
                                                    lotCount
                                            );
                                        })))
                        .values());


        String jsonStr = JsonUtils.objectsToJson(productPlanInfoEntities); // JSON文字列に変換
        File jsonFile = createJsonFile(jsonStr); // ファイルに書き込み
        // インポートAPI呼び出し
        ResponseEntity ret = this.kanbanInfoFacade.importPlanInfo(jsonFile.getPath());
        return null;
    }


    class WorkConnectionTypeComboBoxCellFactory extends ListCell<WorkConnectionTypeEnum> {

        @Override
        protected void updateItem(WorkConnectionTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
            }
        }
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        final String path = System.getProperty("user.home") + File.separator + "Documents";

        try {
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties properties = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            // フォルダ
            this.importAspPlanFileField.setText(properties.getProperty(ProductionNaviPropertyConstants.SELECT_ASP_PLAN_FOLDER_PATH, path));
            this.exportFolderField.setText(properties.getProperty(ProductionNaviPropertyConstants.SELECT_ASP_EXPORT_FOLDER_PATH, path));
            this.importAspMonthlyPlanFolderField.setText(properties.getProperty(ProductionNaviPropertyConstants.SELECT_ASP_MONTHLY_PLAN_FOLDER_PATH, path));
            this.importBomFileField.setText(properties.getProperty(ProductionNaviPropertyConstants.SELECT_ASP_BOM_FOLDER_PATH, path));
            // 階層
            this.kanbanHierarchyField.setText(properties.getProperty(ProductionNaviPropertyConstants.SELECT_ASP_PLAN_KANBAN_HIER, ""));
            this.kanbanHierarchyFieldForExport.setText(properties.getProperty(ProductionNaviPropertyConstants.SELECT_ASP_EXPORT_KANBAN_HIER, ""));
            this.workHierarchyField.setText(properties.getProperty(ProductionNaviPropertyConstants.SELECT_ASP_BOM_WORK_HIER, ""));
            this.workflowHierarchyField.setText(properties.getProperty(ProductionNaviPropertyConstants.SELECT_ASP_BOM_WORKFLOW_HIER, ""));
            this.selectKanbanHierarchyButton.setDisable(true);
            this.selectWorkHierarchyButton.setDisable(true);
            this.selectWorkflowHierarchyButton.setDisable(true);
            this.selectKanbanHierarchyButtonForExport.setDisable(true);

            // タブを復元する
            String value = properties.getProperty(ProductionNaviPropertyConstants.SELECT_LINK_ASP_TAB);
            if (!StringUtils.isEmpty(value)) {
                try {
                    int index = Integer.parseInt(value);
                    SingleSelectionModel<Tab> selectionModel = this.tabImportMode.getSelectionModel();
                    selectionModel.select(index);
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }

            // インポートフォーマット設定を読み込む。
            this.importFormatInfo = ImportFormatFileUtil.load();

            // CSVファイル名を読み込む。
            this.kanbanCsvFilenames.addAll(new LinkedHashSet<>(Arrays.asList(
                    this.importFormatInfo.getKanbanFormatInfo().getCsvFileName(),
                    this.importFormatInfo.getKanbanPropFormatInfo().getCsvFileName(),
                    this.importFormatInfo.getWorkKanbanFormatInfo().getCsvFileName(),
                    this.importFormatInfo.getWorkKanbanPropFormatInfo().getCsvFileName(),
                    this.importFormatInfo.getKanbanStatusFormatInfo().getCsvFileName(),
                    this.importFormatInfo.getProductFormatInfo().getCsvFileName()
            )));

            for (WorkKanbanPropFormatInfo format : this.importFormatInfo.getWorkKanbanPropFormats()) {
                if (!this.kanbanCsvFilenames.contains((format.getCsvFileName()))) {
                    this.kanbanCsvFilenames.add(format.getCsvFileName());
                }
            }

            // 工程接続コンボボックス
            Callback<ListView<WorkConnectionTypeEnum>, ListCell<WorkConnectionTypeEnum>> comboCellFactory = (ListView<WorkConnectionTypeEnum> param) -> new WorkConnectionTypeComboBoxCellFactory();
            this.importBomWorkConnectComboBox.setButtonCell(new WorkConnectionTypeComboBoxCellFactory());
            this.importBomWorkConnectComboBox.setCellFactory(comboCellFactory);
            this.importBomWorkConnectComboBox.setItems(FXCollections.observableArrayList(WorkConnectionTypeEnum.values()));
            this.importBomWorkConnectComboBox.getSelectionModel().select(WorkConnectionTypeEnum.SERIAL);

        } catch (IOException ex) {
            logger.error(ex, ex);
        }

        // ツリー情報表示
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    TreeItem<WorkHierarchyInfoEntity> rootItem = permanenceData.getWorkHierarchyRootItem();
                    workHierarchyEditor = new WorkHierarchyEditor(workHierarchyTree, rootItem, null);
                    rootItem.getChildren().clear();
                    workHierarchyEditor.createRoot();

                } finally {
                    selectWorkHierarchyButton.setDisable(false);
                }
                return null;
            }
        };
        new Thread(task).start();

        // キャッシュ情報取得 ツリー情報表示
        Task<Void> taskFlow = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    TreeItem<WorkflowHierarchyInfoEntity> rootItem = permanenceFlowData.getWorkflowHierarchyRootItem();
                    workflowHierarchyEditor = new WorkflowHierarchyEditor(workFlowHierarchyTree, rootItem, null);
                    rootItem.getChildren().clear();
                    workflowHierarchyEditor.createRoot();

                } finally {
                    selectWorkflowHierarchyButton.setDisable(false);
                }

                return null;
            }
        };
        new Thread(taskFlow).start();

        // カンバン階層
        this.updateKanbanTree(true);

        blockUI(false);
    }

    /**
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        this.argument = argument;
        ProductionNaviUtils.setFieldNormal(this.importAspPlanFileField);
        ProductionNaviUtils.setFieldNormal(this.importExcelFileField);
        if (Objects.nonNull(argument)) {
            // ナビメニューのボタンでの遷移の場合、キャンセルボタンを削除する
            if ((argument instanceof String) && argument.equals("fromNaviMenu")) {
                cancelButton.setVisible(false);
                cancelButton.setManaged(false);
            }
        }
    }

    /**
     * @param flg
     */
    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            sc.blockUI("ContentNaviPane", flg);
            progressPane.setVisible(flg);
        });
    }

    /**
     * 結果リストにメッセージを追加し、追加したメッセージが見えるようにスクロールする
     *
     * @param message
     */
    private void addResult(String message) {
        logger.info(message);
        Platform.runLater(() -> {
            this.resultList.getItems().add(message);
            this.resultList.scrollTo(message);
        });
    }

    /**
     * フォルダ選択（CSV)ボタンのクリックイベント
     *
     * @param event
     */
    @FXML
    private void onSelectExportFolder(ActionEvent event) {
        blockUI(true);
        try {
            DirectoryChooser dc = new DirectoryChooser();
            File fol = new File(exportFolderField.getText());
            if (fol.exists() && fol.isDirectory()) {
                dc.setInitialDirectory(fol);
            }

            dc.setTitle("CSV出力先フォルダ選択");
            File selectedFile = dc.showDialog(sc.getStage().getScene().getWindow());
            if (Objects.nonNull(selectedFile)) {
                this.exportFolderField.setText(selectedFile.getPath());

                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_LINK_ASP_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_CSV));
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * フォルダ選択(生産計画読み込み)ボタンのクリックイベント
     *
     * @param event
     */
    @FXML
    private void onSelectPlanFileButton(ActionEvent event) {
        blockUI(true);
        try {
            FileChooser fc = new FileChooser();
            File fol = new File(importAspPlanFileField.getText());
            if (fol.exists()) {
                final File selectDirectory = fol.isDirectory() ? fol : fol.getParentFile();
                fc.setInitialDirectory(selectDirectory);
            }

            fc.setTitle("csvファイル選択");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("csvファイル選択", "*.csv"));

            File selectedFile = fc.showOpenDialog(sc.getStage().getScene().getWindow());
            if (Objects.isNull(selectedFile)) {
                return;
            }

            importAspPlanFileField.setText(selectedFile.getPath());
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            prop.setProperty(ProductionNaviPropertyConstants.SELECT_ASP_PLAN_FOLDER_PATH, selectedFile.getPath());
            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }


    /**
     * 設定フィールドをチェック
     *
     * @param kanbanHierarchy 階層名
     * @param fileName        ファイル名
     * @return チェック結果
     */
    private List<Tuple<TextField, String>> checkPlanImportTextField(final String kanbanHierarchy, final String fileName) {
        List<Tuple<TextField, String>> ret = new ArrayList<>();

        if (fileName.isEmpty()) {
            ret.add(new Tuple<>(this.importAspPlanFileField, "入力ファイルを指定してください。"));
        } else {
            File file = new File(fileName);
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (!file.exists()
                    || !file.isFile()   // ファイルではない
                    || !file.canRead()  // 読み込めない
                    || !"csv".equals(ext)) { // 拡張子が間違っている
                ret.add(new Tuple<>(this.kanbanHierarchyField, String.format("%s [%s]", "正しい入力ファイルを指定してください。", fileName)));
            }
        }

        if (kanbanHierarchy.isEmpty()) {
            ret.add(new Tuple<>(this.importAspPlanFileField, "カンバン階層を指定してください。"));
        }
        return ret;
    }

    /**
     * 生産計画読み込みインポートボタン
     *
     * @param event イベント
     */
    @FXML
    private void onAspPlanImportButton(ActionEvent event) {
        final String fileName = this.importAspPlanFileField.getText();
        final String kanbanHierarchy = this.kanbanHierarchyField.getText();

        blockUI(true);
        try {
            // 初期化
            this.resultList.getItems().clear();
            ProductionNaviUtils.setFieldNormal(this.importAspPlanFileField);
            ProductionNaviUtils.setFieldNormal(this.kanbanHierarchyField);

            // 設定フィールドのチェック
            final List<Tuple<TextField, String>> checkResult = checkPlanImportTextField(kanbanHierarchy, fileName);
            if (!checkResult.isEmpty()) {
                // エラーが発生した場合はエラー処理
                checkResult.forEach(elem -> {
                    ProductionNaviUtils.setFieldError(elem.getLeft());
                    addResult(elem.getRight());
                });
                return;
            }
        } finally {
            blockUI(false);
        }

        // インポート
        this.importAspPlanTask(fileName);
    }

    /**
     * 設定フィールドをチェック
     *
     * @param workHierarchy     工程階層名
     * @param workflowHierarchy 工程順階層名
     * @param fileName          ファイル名
     * @return
     */
    private List<Tuple<TextField, String>> CheckBomImportTextField(final String workHierarchy, final String workflowHierarchy, final String fileName) {
        List<Tuple<TextField, String>> ret = new ArrayList<>();

        if (fileName.isEmpty()) {
            ret.add(new Tuple<>(this.importBomFileField, "入力ファイルを指定してください。"));
        } else {
            File file = new File(fileName);
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (!file.exists()
                    || !file.isFile()   // ファイルではない
                    || !file.canRead()  // 読み込めない
                    || !"csv".equals(ext)) { // 拡張子が間違っている
                ret.add(new Tuple<>(this.importBomFileField, String.format("%s [%s]", "正しい入力ファイルを指定してください。", fileName)));
            }
        }

        if (workHierarchy.isEmpty()) {
            ret.add(new Tuple<>(this.importAspPlanFileField, "工程階層を指定してください。"));
            ProductionNaviUtils.setFieldError(this.workHierarchyField);
        }

        if (workflowHierarchy.isEmpty()) {
            ret.add(new Tuple<>(this.workflowHierarchyField, "工程順階層を指定してください。"));
        }

        return ret;
    }


    /**
     * 製造BOM読み込みインポートボタン
     *
     * @param event
     */
    @FXML
    private void onAspBomImportButton(ActionEvent event) {
        final String workHierarchy = this.workHierarchyField.getText();
        final String workflowHierarchy = this.workflowHierarchyField.getText();
        final String fileName = this.importBomFileField.getText();

        try {
            blockUI(true);

            this.resultList.getItems().clear();
            ProductionNaviUtils.setFieldNormal(this.importBomFileField);
            ProductionNaviUtils.setFieldNormal(this.workHierarchyField);
            ProductionNaviUtils.setFieldNormal(this.workflowHierarchyField);

            // 設定フィールドのチェック
            final List<Tuple<TextField, String>> checkResult = CheckBomImportTextField(workHierarchy, workflowHierarchy, fileName);
            if (!checkResult.isEmpty()) {
                // エラーが発生した場合はエラー処理
                checkResult.forEach(elem -> {
                    ProductionNaviUtils.setFieldError(elem.getLeft());
                    addResult(elem.getRight());
                });
                return;
            }
        } finally {
            blockUI(false);
        }

        // インポート
        this.importAspBomTask(fileName);
    }

    /**
     * 生産計画インポート処理
     *
     * @param fileName フォルダ
     */
    private void importAspPlanTask(String fileName) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                isImportProccessing = true;
                blockUI(true);
                addResult(String.format("%s [%s]", LocaleUtils.getString("key.ImportKanbanStart"), fileName));// 生産計画取り込み開始
                addResult((new Date()).toString());
                try {
                    importAspPlan(fileName);
                } finally {
                    addResult(LocaleUtils.getString("key.ImportKanbanEnd"));// 生産計画取り込み終了
                    addResult((new Date()).toString());
                }

                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                blockUI(false);
                isImportProccessing = false;
            }

            @Override
            protected void failed() {
                super.failed();
                try {
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                        // 読み込み中に、不明なエラーが発生しました。
                        addResult("   > " + LocaleUtils.getString("key.import.production.error"));
                        logger.fatal("   > " + LocaleUtils.getString("key.import.production.error"));
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                    isImportProccessing = false;
                }
            }
        };
        new Thread(task).start();
    }

    /**
     * 製造Bomファイルインポート処理
     *
     * @param folder フォルダ
     */
    private void importAspBomTask(String folder) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                isImportProccessing = true;
                blockUI(true);
                addResult("製造BOMファイル取り込み開始");
                addResult((new Date()).toString());
                try {
                    importAspBom(folder);
                } finally {
                    addResult("製造BOMファイル取り込み終了");
                    addResult((new Date()).toString());
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                blockUI(false);
                isImportProccessing = false;
            }

            @Override
            protected void failed() {
                super.failed();
                try {
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                        addResult("   > 不明なエラーが発生しました。");
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                    isImportProccessing = false;
                }
            }
        };
        new Thread(task).start();
    }


    /**
     * カンバン情報を作成
     *
     * @param importKanban カンバン情報
     * @param log          　ロガー
     * @return カンバン情報
     */
    private Optional<KanbanInfoEntity> createKanbanInfoEntity(final ImportKanbanCsv importKanban, Consumer<String> log) {
        KanbanInfoEntity kanban = new KanbanInfoEntity();

        // 親階層名
        final String hierarchyName = importKanban.getKanbanHierarchyName();
        if (StringUtils.isEmpty(hierarchyName)) {
            log.accept("階層が指定されていません");
            return Optional.empty();
        }
        kanban.setParentName(hierarchyName);

        // カンバン名
        final String kanbanName = importKanban.getKanbanName();
        if (StringUtils.isEmpty(kanbanName)) {
            log.accept(String.format("%s [%s: %s]", LocaleUtils.getString("key.import.skip.noData"), LocaleUtils.getString("key.import.production.kanba"), LocaleUtils.getString("key.KanbanName")));
            return Optional.empty();
        }
        kanban.setKanbanName(kanbanName);

        // 工程順名
        final String workflowName = importKanban.getWorkflowName();
        if (StringUtils.isEmpty(workflowName)) {
            log.accept(String.format("%s", LocaleUtils.getString("key.ImportKanban_WorkflowNothing")));
            return Optional.empty();
        }
        kanban.setWorkflowName(workflowName);

        // 工程順リビジョン
        final String workflowRev = importKanban.getWorkflowRev();
        if (!StringUtils.isEmpty(workflowRev)) {
            try {
                kanban.setWorkflowRev(Integer.parseInt(importKanban.getWorkflowRev()));
            } catch (NumberFormatException ex) {
                logger.fatal(ex, ex);
                log.accept(String.format("%s [%s]", "工程順リビジョンが不正", workflowRev));
                return Optional.empty();
            }
        }

        kanban.setModelName(importKanban.getModelName()); // モデル名

        // 開始予定日時
        final Date startDatetime = stringToDateTime(importKanban.getStartDatetime()).orElse(new Date());
        kanban.setStartDatetime(startDatetime); // 開始日時

        int productType = 0;
        try {
            productType = Integer.parseInt(importKanban.getProductionType());
        } catch (NumberFormatException ex) {
            logger.fatal(ex, ex);
        }
        kanban.setProductionType(productType); //生産タイプ
        importKanban.setProductionType(String.valueOf(productType));

        // ロット数量
        kanban.setLotQuantity(Integer.parseInt("1"));

        // カンバンの状態
        kanban.setKanbanStatus(KanbanStatusEnum.PLANNED);

        // 製造番号
        kanban.setProductionNumber(importKanban.getProductionNumber());

        // 更新日時
        Date updateDateTime = DateUtils.toDate(LocalDateTime.now());
        kanban.setUpdateDatetime(updateDateTime);

        // 更新者
        kanban.setFkUpdatePersonId(loginUserInfoEntity.getId());

        return Optional.of(kanban);
    }


    /**
     * カンバンプロパティ情報を作成
     *
     * @param data 　カンバンプロパティ情報
     * @return カンバンプロパティ情報
     */
    private Optional<KanbanPropertyInfoEntity> createKanbanPropertyInfoEntity(ImportKanbanPropertyCsv data) {
        final String name = data.getKanbanPropertyName();
        final CustomPropertyTypeEnum type = CustomPropertyTypeEnum.toEnum(data.getKanbanPropertyType());
        final String value = data.getKanbanPropertyValue();
        if (StringUtils.isEmpty(name) || Objects.isNull(type) || StringUtils.isEmpty(value)) {
            return Optional.empty();
        }
        KanbanPropertyInfoEntity tmp = new KanbanPropertyInfoEntity();
        tmp.setKanbanPropertyName(name);   // 名称
        tmp.setKanbanPropertyType(type);   // データ種
        tmp.setKanbanPropertyValue(value); // 値
        return Optional.of(tmp);
    }

    /**
     * 工程カンバン情報を作成
     *
     * @param data csvファイル読込データ
     * @param log  ロガー
     * @return 工程カンバン情報
     */
    private Optional<WorkKanbanInfoEntity> createWorkKanbanInfoEntity(ImportWorkKanbanCsv data, Consumer<String> log) {
        WorkKanbanInfoEntity workKanban = new WorkKanbanInfoEntity();

        // 工程名
        final String workName = data.getWorkName();
        if (StringUtils.isEmpty(workName)) {
            log.accept("ワークオーダ名が未設定");
            return Optional.empty();
        }
        workKanban.setWorkName(workName);

        // スキップフラグ
        workKanban.setSkipFlag("1".equals(data.getSkipFlag()));

        // タクトタイム
        int tactTime = 0;
        try {
            tactTime = StringUtils.isEmpty(data.getTactTime()) ? 0 : Integer.parseInt(data.getTactTime()) * 1000;
        } catch (NumberFormatException ex) {
            logger.fatal(ex, ex);
        }
        workKanban.setTaktTime(tactTime);

        // 開始時間
        Date workStartDateTime = stringToDateTime(data.getStartDatetime()).orElse(null);
        workKanban.setStartDatetime(workStartDateTime);

        // 終了時間
        Date workCompDateTime = stringToDateTime(data.getCompDatetime()).orElse(null);
        workKanban.setCompDatetime(workCompDateTime);

        // 設備識別名
        List<String> organizations
                = StringUtils.nonEmpty(data.getOrganizations())
                ? Arrays.asList(data.getOrganizations().split(DELIMITER, 0))
                : null;
        workKanban.setOrganizationIdentifyCollection(organizations);

        // 組織識別名(ASPROVA連携は組織識別名と同じにしておく)
        List<String> equipments
                = StringUtils.nonEmpty(data.getEquipments())
                ? Arrays.asList(data.getEquipments().split(DELIMITER, 0))
                : null;
        workKanban.setEquipmentIdentifyCollection(equipments);

        workKanban.setWorkStatus(data.getWorkStatus());

        return Optional.of(workKanban);
    }

    /**
     * ワークカンバンプロパティを作成
     *
     * @param data データ
     * @return ワークカンバンプロパティ
     */
    private Optional<WorkKanbanPropertyInfoEntity> createWorkKanbanPropertyInfoEntity(ImportWorkKanbanPropertyCsv data) {
        // 工程カンバンプロパティ
        final String name = data.getWkKanbanPropName();
        final CustomPropertyTypeEnum type = CustomPropertyTypeEnum.toEnum(data.getWkKanbanPropType());
        final String value = data.getWkKanbanPropValue();
        if (StringUtils.isEmpty(name) || Objects.isNull(type) || StringUtils.isEmpty(value)) {
            return Optional.empty();
        }
        WorkKanbanPropertyInfoEntity tmp = new WorkKanbanPropertyInfoEntity();
        tmp.setWorkKanbanPropName(name);
        tmp.setWorkKanbanPropType(type);
        tmp.setWorkKanbanPropValue(value);
        return Optional.of(tmp);
    }

    /**
     * フォルダがなければ作成し、指定した拡張子のファイルがあれば削除する。
     *
     * @param path      フォルダパス
     * @param extension 削除する拡張子
     * @throws IOException
     */
    private void cleanupFolder(Path path, String extension) throws IOException {
        Files.createDirectories(path);
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, "*" + extension)) {
            for (Path deleteFilePath : ds) {
                Files.delete(deleteFilePath);
            }
        }
    }

    /**
     * JSONファイルを作成する。
     *
     * @param writeStr 書き込む文字列
     * @return 生成したJSONファイル
     */
    private File createJsonFile(String writeStr) {

        final String JSON_EXTENSION = ".json";
        final String TEMP_PATH = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator + "ImportKanban";

        String now = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String fileName = "ImportEquipment" + "_" + now + JSON_EXTENSION;
        File file;

        try {
            // 一時フォルダからJSONファイルを削除する。
            Path path = Paths.get(TEMP_PATH);
            this.cleanupFolder(path, JSON_EXTENSION);

            file = new File(TEMP_PATH + File.separator + fileName);

            // JSONファイルに文字列を書き込む
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));) {
                writer.print(writeStr);
            }

        } catch (IOException ex) {
            logger.fatal(ex, ex);
            file = null;
        }

        return file;
    }

    /**
     * カンバン内で最も早い工程開始時間を取得する
     *
     * @param kanbanInfoEntity カンバンエンティティ
     * @return 最も早い工程の開始時間
     */
    Date GetFirstWorkKanbanStartDate(KanbanInfoEntity kanbanInfoEntity) {
        return kanbanInfoEntity.getWorkKanbanCollection().stream()
                .filter(l -> !l.getSkipFlag())
                .map(WorkKanbanInfoEntity::getStartDatetime)
                .filter(Objects::nonNull).sorted()
                .findFirst()
                .orElseGet(kanbanInfoEntity::getStartDatetime);
    }

    /**
     * 生産計画インポート
     *
     * @param fileName ファイルネーム
     * @throws Exception
     */
    private Map<String, Integer> importAspPlan(String fileName) throws Exception {
        List<ImportWorkKanbanCsv> importWorkKanbans;
        List<ImportWorkKanbanPropertyCsv> importWkKanbanProps;

        // 生産計画ファイル 読込
        addResult(LocaleUtils.getString("key.import.work.plan.kanban") + " [" + fileName + "]");
        Optional<List<ImportAsprovaPlanCsv>> optImportPlans = readAspKanbanCsv(fileName);
        if (!optImportPlans.isPresent()) {
            return null;
        }
        List<ImportAsprovaPlanCsv> importPlans = optImportPlans.get();

        // データチェック
        List<String> errorMessage =
                importPlans
                        .stream()
                        .map(ImportAsprovaPlanCsv::checkData)
                        .flatMap(Collection::stream)
                        .filter(msg -> !StringUtils.isEmpty(msg))
                        .collect(toList());

        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return null;
        }

        // カンバン情報 作成
        Optional<List<ImportKanbanCsv>> optImportKanbans = createKanbanCsv(importPlans);
        if (!optImportKanbans.isPresent()) {
            return null;
        }
        List<ImportKanbanCsv> importKanbans = optImportKanbans.get();

        // カンバンプロパティ情報 作成
        List<ImportKanbanPropertyCsv> importKanbanProps = createKanbanPropertyCsv(importPlans);

        // 工程カンバン情報 作成
        Optional<List<ImportWorkKanbanCsv>> importWorkKanbansOp = createWorkKanbanCsv(importPlans);
        if (!importWorkKanbansOp.isPresent()) {
            return null;
        }
        importWorkKanbans = importWorkKanbansOp.get();

        if (importWorkKanbans.isEmpty()) {
            // データがありません。
            addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), fileName));
            logger.error(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), fileName));
        }

        // 工程カンバンプロパティ情報 作成
        importWkKanbanProps = this.createWorkKanbanPropertyCsv(importPlans);

        // カンバン追加
        addResult(LocaleUtils.getString("key.ImportKanban_CreateKanban"));
        logger.info(LocaleUtils.getString("key.ImportKanban_CreateKanban"));

        int kanbanLine = 0;

        List<ResultResponse> result = new ArrayList<>();
        List<KanbanInfoEntity> kanbanInfoEntityList = new ArrayList<>();
        for (ImportKanbanCsv importKanban : importKanbans) {
            logger.debug("Import the kanban: " + importKanban);

            final int finalKanbanLine = kanbanLine;
            Consumer<String> log = msg -> {
                final String str = String.format("[%d] > %s", finalKanbanLine, msg);
                addResult(str);
                logger.error(str);
            };

            // カンバン作成
            Optional<KanbanInfoEntity> kanbanOp = createKanbanInfoEntity(importKanban, log);
            if (!kanbanOp.isPresent()) {
                ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
                ret.result(String.format("KanbanName=%s", importKanban.getKanbanName()));
                result.add(ret);
                continue;
            }
            KanbanInfoEntity kanban = kanbanOp.get();
            final String kanbanName = kanban.getKanbanName();

            // カンバンプロパティ
            kanban.setPropertyCollection(
                    importKanbanProps
                            .stream()
                            .filter(p -> kanbanName.equals(p.getKanbanName()))
                            .map(this::createKanbanPropertyInfoEntity)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(toList()));

            // 工程カンバン
            kanban.setWorkKanbanCollection(
                    importWorkKanbans
                            .stream()
                            .filter(p -> kanbanName.equals(p.getKanbanName()))
                            .map(p -> createWorkKanbanInfoEntity(p, log))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(toList()));

            // 工程カンバンプロパティ
            kanban.getWorkKanbanCollection()
                    .forEach(work -> work.setPropertyCollection(
                            importWkKanbanProps.stream()
                                    .filter(p -> kanbanName.equals(p.getKanbanName()))
                                    .filter(p -> work.getWorkName().equals(p.getWorkName()))
                                    .map(this::createWorkKanbanPropertyInfoEntity)
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .collect(toList())));
            kanbanInfoEntityList.add(kanban);

        }

        // 仕掛の早い順序にソートする
        kanbanInfoEntityList = kanbanInfoEntityList.stream().sorted(Comparator.comparing(this::GetFirstWorkKanbanStartDate)).collect(toList());

        final Integer timeout = workflowInfoFacade.getReadTimeout();
        kanbanInfoFacade.setReadTimeout(60 * 10 * 1000); // 10分

        for (int n = 0; n < kanbanInfoEntityList.size(); ) {
            int next = Math.min(100 + n, kanbanInfoEntityList.size());
            // カンバン一括登録
            addResult(String.format("%s %s %s",
                    LocaleUtils.getString("key.import.production.kanba"),
                    "[" + n + "]～[" + (next - 1) + "]",
                    LocaleUtils.getString("key.Regist")));

            logger.info("import start");
            // インポート情報からJSONファイルを生成
            String jsonStr = JsonUtils.objectsToJson(kanbanInfoEntityList.subList(n, next)); // JSON文字列に変換
            File jsonFile = createJsonFile(jsonStr); // ファイルに書き込み
            // インポートAPI呼び出し
            List<ResultResponse> responses = kanbanInfoFacade.importFile(jsonFile.getPath());
            if (Objects.isNull(responses)) {
                logger.info("timeout");
                addResult("タイムアウトが発生しました。 再度読込を実施して下さい。");
                return new HashMap<>();
            }
            result.addAll(responses);
            n = next;
        }
        // タイムアウト時間を元に戻す
        kanbanInfoFacade.setReadTimeout(timeout);

        Map<ServerErrorTypeEnum, List<ResultResponse>> resultMap
                = result
                .stream()
                .collect(groupingBy(ResultResponse::getErrorType));

        final int processNum = result.size();
        final int successNum = resultMap.getOrDefault(ServerErrorTypeEnum.SUCCESS, new ArrayList<>()).size();
        final int skipNum = resultMap.getOrDefault(ServerErrorTypeEnum.THERE_START_NON_DELETABLE, new ArrayList<>()).size()
                + resultMap.getOrDefault(ServerErrorTypeEnum.THERE_START_NON_EDITABLE, new ArrayList<>()).size();
        final int failedNum = processNum - successNum - skipNum;

        result.stream()
                .map(ResultResponse::getResult)
                .filter(Objects::nonNull)
                .map(l -> JsonUtils.jsonToObjects(l, MessageEntity[].class))
                .flatMap(Collection::stream)
                .forEach(l -> {
                    List<String> args = l.getArgs().stream().map(LocaleUtils::getString).collect(toList());
                    addResult(String.format(l.getFormat(), args.toArray()));
                });


        String resultMessage = String.format("%s: %s (%s: %s, %s: %s, %s: %s)",
                LocaleUtils.getString("key.ImportKanban_ProccessNum"), processNum,
                LocaleUtils.getString("key.ImportKanban_SuccessNum"), successNum,
                LocaleUtils.getString("key.ImportKanban_SkipNum"), skipNum,
                LocaleUtils.getString("key.ImportKanban_FailedNum"), failedNum);

        addResult(resultMessage);
        logger.info(resultMessage);

//        resultMap.getOrDefault(ServerErrorTypeEnum.SUCCESS, new ArrayList<>())
//                .stream()
//                .map(ResultResponse::getResult)
//                .filter(Objects::nonNull)
//                .forEach(this::addResult);

        Map<String, Integer> ret = new HashMap<>();
        ret.put("procNum", processNum);
        ret.put("successNum", successNum);
        ret.put("skipKanbanNum", skipNum);
        ret.put("failedNum", failedNum);

        return ret;
    }


    /**
     * 製造BOMインポート
     *
     * @param folder フォルダ
     * @throws Exception
     */
    private boolean importAspBom(String folder) throws Exception {

        List<ImportAsprovaBomCsv> importBom;
        // 工程階層取得
        String workHierarchyName = this.workHierarchyField.getText();
        WorkHierarchyInfoEntity workHierarchy = workHierarchyInfoFacade.findHierarchyName(URLEncoder.encode(workHierarchyName, CHARSET_UTF8));
        if (Objects.isNull(workHierarchy.getWorkHierarchyId())) {
            addResult("   > 存在しない工程階層、またはアクセス権がないため別の工程階層を選択してください。");
            ProductionNaviUtils.setFieldError(this.workHierarchyField);
            return false;
        }

        // 工程順階層取得
        String workflowHierarchyName = this.workflowHierarchyField.getText();
        WorkflowHierarchyInfoEntity workflowHierarchy = workflowHierarchyInfoFacade.findHierarchyName(URLEncoder.encode(workflowHierarchyName, CHARSET_UTF8));
        if (Objects.isNull(workflowHierarchy.getWorkflowHierarchyId())) {
            addResult("   > 存在しない工程順階層、またはアクセス権がないため別の工程順階層を選択してください。");
            ProductionNaviUtils.setFieldError(this.workflowHierarchyField);
            return false;
        }

        // 製造BOMファイル 読込
        addResult("製造BOMファイルを読み込み中" + " [" + folder + "]");
        Optional<List<ImportAsprovaBomCsv>> optImportBom = readAspBomCsv(folder);
        if (!optImportBom.isPresent()) {
            return false;
        }

        importBom = optImportBom.get();


        // 作業中かをチェックする
        if (changeKanbansWorkflow.isSelected()) {
            List<String> workflowNames =
                    importBom
                            .stream()
                            .map(ImportAsprovaBomCsv::getWorkflowName)
                            .distinct()
                            .collect(toList());
            Long count = workflowInfoFacade.countWorkingWorkKanbanByWorkflowNames(workflowNames);
            if (count != 0) {
                List<KanbanInfoEntity> kanbans = kanbanInfoFacade.getKanbanWithWorkingWorkByWorkflowNames(workflowNames);
                addResult("  > 作業中の工程があります。 作業を中止して下さい。");
                addResult(String.format("    > %s: %s", LocaleUtils.getString("key.KanbanName"), kanbans.stream().map(KanbanInfoEntity::getKanbanName).collect(joining(", "))));
                return false;
            }
        }

        addResult("登録する工程を作成中");

        // 工程情報 作成
        List<WorkInfoEntity> importWorks = createWorkList(importBom, loginUserInfoEntity.getId(), workHierarchy.getWorkHierarchyId());
        if (importWorks.isEmpty()) {
            addResult(String.format("   > %s ", "取込ファイルを確認してください。"));
            return false;
        }

        addResult("工程を登録中");

        // タイムアウト対策の為分割する
        Map<String, List<WorkInfoEntity>> importWorkList = new LinkedHashMap<>();
        for (int n = 0; n < importWorks.size(); ) {
            int next = Math.min(n + 50, importWorks.size());
            importWorkList.put("[" + n + "]～[" + (next - 1) + "]", importWorks.subList(n, next));
            n = next;
        }

        Map<String, String> workIdMap = new HashMap<>();
        // ---------- 工程一括更新
        for (Map.Entry<String, List<WorkInfoEntity>> entities : importWorkList.entrySet()) {
            addResult("工程 " + entities.getKey() + "を登録");
            ResponseEntity workUpdateRes = workInfoFacade.addAll(entities.getValue(), true);
            if (Objects.nonNull(workUpdateRes) && !ResponseAnalyzer.getAnalyzeResult(workUpdateRes)) {
                // 失敗処理
                addResult("   > 工程の登録に失敗しました。通信状態を確認した後、取り込みを再度実行してください。");
                return false;
            }
            workIdMap.putAll(JsonUtils.jsonToMap(workUpdateRes.getUri()));
        }

        long processNum = importWorks.size();
        long successNum = workIdMap.size();
        long failedNum = processNum - successNum;

        // 返値からログを出力
        String resultMessage = String.format("%s: %s (%s: %s, %s: %s)",
                LocaleUtils.getString("key.RegistProcess"), processNum,
                "成功", successNum,
                "失敗", failedNum);

        addResult(resultMessage);
        logger.info(resultMessage);

        if (failedNum > 0) {
            addResult("   > 登録に失敗、もしくは代替工程がありません。その為工程順の登録をスキップ");
            return false;
        }

        addResult("登録する工程順を作成中");

        // 直列・並列
        boolean isSerial = Objects.equals(this.importBomWorkConnectComboBox.getSelectionModel().getSelectedItem(), WorkConnectionTypeEnum.SERIAL);

        // 工程順情報 作成
        List<WorkflowInfoEntity> importWorkflows = createWorkflowList(importBom, workIdMap, workflowHierarchy.getWorkflowHierarchyId(), loginUserInfoEntity.getId(), isSerial);
        if (importWorkflows.isEmpty()) {
            addResult(String.format("   > %s ", "ファイルを確認してください。"));
            return false;
        }

        // --------- 工程順を登録
        addResult("工程順を登録中");
        // タイムアウト対策の為分割
        Map<String, List<WorkflowInfoEntity>> importWorkflowList = new LinkedHashMap<>();
        for (int n = 0; n < importWorkflows.size(); ) {
            int next = Math.min(n + 50, importWorkflows.size());
            importWorkflowList.put("[" + n + "]～[" + (next - 1) + "]", importWorkflows.subList(n, next));
            n = next;
        }

        Map<String, String> workflowIdMap = new HashMap<>();
        // def 60 * 1000
        final Integer timeout = workflowInfoFacade.getReadTimeout();
        workflowInfoFacade.setReadTimeout(3600 * 1000); // 60分
        List<ResultResponse> result = new ArrayList<>();
        for (Map.Entry<String, List<WorkflowInfoEntity>> entities : importWorkflowList.entrySet()) {
            // 工程順一括登録
            addResult("工程順 " + entities.getKey() + "を登録");
            List<ResultResponse> workflowUpdateRes = workflowInfoFacade.addAll(entities.getValue(), changeKanbansWorkflow.isSelected());
            if (Objects.isNull(workflowUpdateRes)) {
                addResult("取込タイムアウトが発生しました。");
                return false;
            }

            result.addAll(workflowUpdateRes);
            //            if (Objects.nonNull(workflowUpdateRes) && !ResponseAnalyzer.getAnalyzeResult(workflowUpdateRes)) {
//                // 失敗処理
//                addResult("   > 工程順の登録に失敗しました。通信状態、カンバン状態、工程カンバン状態を確認して下さい");
//                return false;
//            }
//            workflowIdMap.putAll(JsonUtils.jsonToMap(workflowUpdateRes.getUri()));
        }
        // タイムアウト時間を元に戻す
        workflowInfoFacade.setReadTimeout(timeout);

        Map<String, Map<ServerErrorTypeEnum, List<Tuple<ResultResponse, List<MessageEntity>>>>> resultMap =
                result.stream()
                        .filter(entity -> Objects.nonNull(entity.getResult()))
                        .map(entity -> new Tuple<>(entity, JsonUtils.jsonToObjects(entity.getResult(), MessageEntity[].class)))
                        .filter(entity -> !entity.getRight().isEmpty())
                        .collect(
                                Collectors.groupingBy(entity -> entity.getRight().get(0).getAddInfo(),
                                        Collectors.groupingBy(entity -> entity.getLeft().getErrorType()))
                        );

        logger.info(result);

        // 工程登録用メッセージ表示
        Map<ServerErrorTypeEnum, List<Tuple<ResultResponse, List<MessageEntity>>>> workflowResultMap = resultMap.get("workflow");
        if (Objects.nonNull(workflowResultMap)) {
            // メッセージ表示
            workflowResultMap
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .map(Tuple::getRight)
                    .flatMap(Collection::stream)
                    .filter(l -> !StringUtils.isEmpty(l.getFormat()))
                    .map(l -> {
                        List<String> args = l.getArgs().stream().map(LocaleUtils::getString).collect(toList());
                        return String.format(l.getFormat(), args.toArray());
                    })
                    .forEach(this::addResult);

            // 結果表示
            final int workflowProcessNum = importWorkflows.size();
            final int workflowSuccessNum = workflowResultMap.getOrDefault(ServerErrorTypeEnum.SUCCESS, new ArrayList<>()).size();
            final int workflowFailedNum = workflowProcessNum - workflowSuccessNum;
            final String workflowResultMessage = String.format("%s: %s (%s: %s, %s: %s)",
                    LocaleUtils.getString("key.RegistOrderProcesses"), workflowProcessNum,
                    LocaleUtils.getString("key.ImportKanban_SuccessNum"), workflowSuccessNum,
                    LocaleUtils.getString("key.ImportKanban_FailedNum"), workflowFailedNum);
            addResult(workflowResultMessage);
        }

        Map<ServerErrorTypeEnum, List<Tuple<ResultResponse, List<MessageEntity>>>> kanbanResultMap = resultMap.get("kanban");
        if (Objects.nonNull(kanbanResultMap)) {
            // メッセージ表示
            kanbanResultMap
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .map(Tuple::getRight)
                    .flatMap(Collection::stream)
                    .filter(l -> !StringUtils.isEmpty(l.getFormat()))
                    .map(l -> {
                        List<String> args = l.getArgs().stream().map(LocaleUtils::getString).collect(toList());
                        return String.format(l.getFormat(), args.toArray());
                    })
                    .forEach(this::addResult);

            final int kanbanProcessNum = kanbanResultMap.values().stream().mapToInt(List::size).sum();
            final int kanbanSuccessNum = kanbanResultMap.getOrDefault(ServerErrorTypeEnum.SUCCESS, new ArrayList<>()).size();
            final int kanbanFailedNum = kanbanProcessNum - kanbanSuccessNum;
            final String kanbanResultMessage = String.format("%s: %s (%s: %s, %s: %s)",
                    LocaleUtils.getString("key.ImportKanban_UpdateKanban"), kanbanProcessNum,
                    LocaleUtils.getString("key.ImportKanban_SuccessNum"), kanbanSuccessNum,
                    LocaleUtils.getString("key.ImportKanban_FailedNum"), kanbanFailedNum);
            addResult(kanbanResultMessage);
        }

        return true;
    }

    /**
     * 製造BOMのデータチェック
     *
     * @param row
     * @return エラーメッセージ
     */
    private List<String> checkBomCsvData(ImportAsprovaBomCsv row) {
        List<String> ret = row.checkData();

        if (this.countByteLength(row.getWorkName()) > 256) {
            // 工程番号_工程コードの文字数が半角256文字を超えています
            ret.add(String.format("   > %s [%d行目][%s]", "工程番号_工程コードの文字数が半角256文字を超えています。", row.getRowNo(), row.getWorkName()));
        }

        if (this.countByteLength(row.getWorkflowName()) > 256) {
            // 品目の文字数が半角256文字を超えています
            ret.add(String.format("   > %s [%d行目][%s]", "品目の文字数が半角256文字を超えています。", row.getRowNo(), row.getWorkflowName()));
        }
        return ret;
    }

    private Optional<Set<String>> getWorkNameListInWorkflow(String workflowName) {
        WorkflowInfoEntity workflowInfo = workflowInfoFacade.findName(workflowName);
        if (Objects.isNull(workflowInfo.getWorkflowId())) {
            return Optional.empty();
        }

        return Optional.of(workflowInfo
                .getConWorkflowWorkInfoCollection()
                .stream()
                .map(ConWorkflowWorkInfoEntity::getWorkName)
                .collect(toSet()));
    }

    /**
     * Asprova生産計画CSVファイルを読み込む。
     *
     * @param fileName Asprova生産計画 インポート用ファイル名
     * @return Asprova生産計画インポート用データ一覧
     */
    private Optional<List<ImportAsprovaPlanCsv>> readAspKanbanCsv(String fileName) {
        if (Files.notExists(Paths.get(fileName))) {
            this.addResult("   > 生産計画ファイルが存在しません。");
            return Optional.empty();
        }

        try (FileInputStream fis = new FileInputStream(fileName);
             InputStreamReader isr = new InputStreamReader(fis, CHARSET_SJIS);
             CSVReader csvReader = new CSVReader(isr)) {

            // ヘッダ行
            List<String> header = Arrays.asList(csvReader.readNext());
            if (header.isEmpty()) {
                this.addResult("   > ファイルが空です。");
                return Optional.empty();
            }

            if (header.size() < ImportAsprovaPlanCsv.columnNum) {
                this.addResult("  > ヘッダの項目数が正しくありません。ファイルを見直して下さい。");
                return Optional.empty();
            }


            Map<String, Set<String>> workNameMap = new HashMap<>();
            int count = 1;
            String[] line;
            List<ImportAsprovaPlanCsv> importAsprovaPlans = new ArrayList<>();
            while ((line = csvReader.readNext()) != null) {
                ++count;

                ImportAsprovaPlanCsv data = new ImportAsprovaPlanCsv();
                if (!data.setValue(count, header, Arrays.asList(line))) {
                    this.addResult(String.format("[%d] > %s", count, "データの要素数が異常です"));
                    return Optional.empty();
                }

                if (ImportAsprovaPlanCsv.NOT_SPLIT_TYPE.equals(data.getType())) {
                    // 種別がS(分割前)はリストに追加しない
                    continue;
                }

                Set<String> workNames = workNameMap.get(data.getWorkflowName());
                if (Objects.isNull(workNames)) {
                    Optional<Set<String>> optWorkNames = getWorkNameListInWorkflow(data.getWorkflowName());
                    if (!optWorkNames.isPresent()) {
                        this.addResult(String.format("[%d] > %s は未登録な工程順です", count, data.getWorkflowName()));
                        return Optional.empty();
                    }
                    workNames = optWorkNames.get();
                    workNameMap.put(data.getWorkflowName(), workNames);
                }

                // 前段取り
                String preSetupName = data.createPreSetupName();
                if (workNames.contains(preSetupName)) {
                    data.createPreSetupPlan().ifPresent(importAsprovaPlans::add);
                } else if (!StringUtils.isEmpty(data.getAlternativeSetupName())) {
                    this.addResult(String.format("[%d] > %s は %s(工程順)に未登録です", count, preSetupName, data.getWorkflowName()));
                    return Optional.empty();
                }


                // 本作業
                importAsprovaPlans.add(data);

                if (!workNames.contains(data.getWorkName())) {
                    this.addResult(String.format("[%d] > %s は %s(工程順)に未登録です", count, data.getWorkName(), data.getWorkflowName()));
                    return Optional.empty();
                }


                // 後段取り (2022/04/29 伊里さんの要望にて後段取りに不要な情報が入るので無効)
//                String postSetupName = data.createPostSetupName();
//                if (workNames.contains(postSetupName)) {
//                data.createPostSetupPlan().ifPresent(importAsprovaPlans::add);
//                }
            }

            return Optional.of(importAsprovaPlans);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            // 読み込み中に、不明なエラーが発生しました。
            this.addResult("   > " + LocaleUtils.getString("key.import.production.error"));
            return Optional.empty();
        }
    }

    /**
     * 生産計画ファイルからカンバン情報を作成
     *
     * @param importPlans 生産計画ファイル情報
     * @return 成功時 = カンバン情報 失敗時 = null
     */
    private Optional<List<ImportKanbanCsv>> createKanbanCsv(List<ImportAsprovaPlanCsv> importPlans) {
        logger.info("createKanbanCsv start.");
        List<ImportKanbanCsv> importKanbans = null;
        String kanbanHierarchy = this.kanbanHierarchyField.getText();

        Map<String, List<ImportAsprovaPlanCsv>> kanbanMap = importPlans.stream()
                .collect(Collectors.groupingBy(ImportAsprovaPlanCsv::getKanbanName));

        // データチェック
        List<String> errorMessage = new ArrayList<>();
        kanbanMap.values()
                .forEach(entries -> {
                    String workflowName = entries.get(0).getWorkflowName();
                    if (!entries.stream().allMatch(entry -> StringUtils.equals(workflowName, entry.getWorkflowName()))) {
                        errorMessage.add(String.format("  > %s [%d行目]", "同一製造オーダに対して複数の工程順が指定されています", entries.get(0).getRowNo()));
                        return;
                    }

                    String lotQuantity = entries.get(0).getLotQuantity();
                    if (!entries.stream().allMatch(entry -> StringUtils.equals(lotQuantity, entry.getLotQuantity()))) {
                        errorMessage.add(String.format("  > %s [%d行目]", "製造オーダに対してロット数が異なります", entries.get(0).getRowNo()));
                        return;
                    }

                    String modelName = entries.get(0).getModelName();
                    if (!entries.stream().allMatch(entry -> StringUtils.equals(modelName, entry.getModelName()))) {
                        errorMessage.add(String.format("  > %s [%d行目]", "製造オーダに対してモデル名が異なります", entries.get(0).getRowNo()));
                        return;
                    }
                });

        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        List<ImportKanbanCsv> ret
                = kanbanMap
                .values()
                .stream()
                .map(entries -> {
                    ImportAsprovaPlanCsv plan = entries.get(0);
                    ImportKanbanCsv kanban = new ImportKanbanCsv();
                    kanban.setKanbanHierarchyName(kanbanHierarchy);
                    kanban.setKanbanName(plan.getKanbanName());
                    kanban.setWorkflowName(plan.getWorkflowName());

                    entries.stream()
                            .map(ImportAsprovaPlanCsv::getWorkStartTime)
                            .map(LinkAspCompoController::stringToDateTime)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .min(Comparator.comparing(Function.identity()))
                            .flatMap(LinkAspCompoController::dateTimeToString)
                            .ifPresent(kanban::setStartDatetime);

                    // kanban.setProductionType("1");
                    kanban.setLotQuantity(plan.getLotQuantity());
                    kanban.setModelName(plan.getModelName());
                    return kanban;
                })
                .collect(Collectors.toList());


        if (ret.isEmpty()) {
            this.addResult("   > " + LocaleUtils.getString("   > データがありません"));
            return Optional.empty();
        }

        return Optional.of(ret);
    }

    /**
     * 生産計画ファイルからカンバンプロパティ情報を作成
     *
     * @param importPlans 生産計画ファイル情報
     * @return 成功時 = カンバンプロパティ情報 失敗時 = null
     */
    private List<ImportKanbanPropertyCsv> createKanbanPropertyCsv(List<ImportAsprovaPlanCsv> importPlans) {
        logger.info("createKanbanPropertyCsv start.");

        Map<String, ImportKanbanPropertyCsv> kanbanProperyMap = new LinkedHashMap<>();
        for (ImportAsprovaPlanCsv plan : importPlans) {
            String kanbanName = plan.getKanbanName();

            // ニックネーム
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.NICKNAME).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.NICKNAME, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getNickName()));
            // 品名
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.ITEM_NAME).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.ITEM_NAME, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getItemName()));
            // 納期
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.DELIVARY_DATE).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.DELIVARY_DATE, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getDeliveryDate()));
            // 製造オーダ
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.ORDER_CODE).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.ORDER_CODE, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getOrderCode()));
            // 製造オーダシリアル
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.SERIAL_NUMBER).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.SERIAL_NUMBER, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getSerialNumber()));
            // 品目
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.ITEM_CODE).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.ITEM_CODE, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getItemCode()));
            // 数量
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.LOT_QUANTITY).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.LOT_QUANTITY, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getLotQuantity()));
            // 受注番号
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.ORDER_NUMBER).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.ORDER_NUMBER, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getOrderNumber()));
            // 納入先
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.DELIVERY_DESTINATION).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.DELIVERY_DESTINATION, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getDeliveryDestination()));
            // 材質
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.MATERIAL).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.MATERIAL, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getMaterial()));
            // 寸法
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.SIZE_DATA).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.SIZE_DATA, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getSizeData()));
            // Asprova連携フラグ
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.LINK_ASP_FLG).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.LINK_ASP_FLG, CustomPropertyTypeEnum.TYPE_BOOLEAN.name(), Boolean.TRUE.toString()));
            // 分割番号
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.SPLIT_NUMBER).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.SPLIT_NUMBER, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getSplitNo()));
            // セグメント
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.SEGMENT).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.SEGMENT, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getSegment()));
            // オーダ総数
            kanbanProperyMap.put(new StringBuilder(kanbanName).append(ImportAsprovaPlanCsv.ORDER_SUM_NUMBER).toString(), new ImportKanbanPropertyCsv(kanbanName, ImportAsprovaPlanCsv.ORDER_SUM_NUMBER, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getOrderSumNumber()));
        }

        logger.info("createKanbanPropertyCsv end.");
        return new ArrayList<>(kanbanProperyMap.values());
    }

    /**
     * 生産計画ファイルから工程カンバン情報を作成
     *
     * @param importPlans 生産計画ファイル情報
     * @return 成功時 = カンバン情報 失敗時 = null
     */


    private Optional<List<ImportWorkKanbanCsv>> createWorkKanbanCsv(List<ImportAsprovaPlanCsv> importPlans) {
        logger.info("createWorkKanbanCsv start.");

        // デフォルト組織
        String aspOrganization = AdProperty.getProperties().getProperty(ProductionNaviPropertyConstants.ASP_PLAN_ORGANIZATION, "admin");

        // for内で照合を行うための組織一覧を取得
        Map<String, String> allorg =
                this.organizationInfoFacade.findAll()
                        .stream()
                        .collect(toMap(OrganizationInfoEntity::getOrganizationIdentify, OrganizationInfoEntity::getOrganizationIdentify));

        if (!allorg.containsKey(aspOrganization)) {
            addResult(String.format("   > %s [%s]", "デフォルト組織が見つからない、もしくはアクセス権がありません", aspOrganization));
            return Optional.empty();
        }


        Map<String, String> equipmentMap =
                this.equipmentInfoFacade.findAll()
                        .stream()
                        .collect(toMap(EquipmentInfoEntity::getEquipmentIdentify, EquipmentInfoEntity::getEquipmentIdentify));

        List<ImportAsprovaPlanCsv> notFindEquipmentList =
                importPlans
                        .stream()
                        .filter(plan -> !ImportAsprovaPlanCsv.COMPLETE_WORK_STATUS.equals(plan.getWorkStatus()))
                        .filter(plan -> !plan.getSkipFlag())
                        .filter(plan -> !equipmentMap.containsKey(plan.getMainResource()))
                        .collect(toList());

        if (!notFindEquipmentList.isEmpty()) {
            notFindEquipmentList
                    .stream()
                    .map(importPlan -> String.format("   > 資源が未登録です。 [%d行目][%s][%s]", importPlan.getRowNo(), importPlan.getWorkName(), importPlan.getMainResource()))
                    .forEach(this::addResult);
            return Optional.empty();
        }

        Map<String, ImportWorkKanbanCsv> kanbanMap = new LinkedHashMap<>();
        for (ImportAsprovaPlanCsv plan : importPlans) {

            String kanbanName = plan.getKanbanName();
            // 主資源が組織にいれば主資源 いなければ設定ファイルから固定のものを設定
            String organization = allorg.computeIfAbsent(plan.getMainResource(), key -> aspOrganization);

            // カンバンインポート用エンティティ作成
            ImportWorkKanbanCsv workKanban = new ImportWorkKanbanCsv();
            workKanban.setKanbanName(kanbanName);                 // カンバン名
            workKanban.setWorkName(plan.getWorkName());           // 工程名
            workKanban.setTactTime(plan.getTactTime());           // タクトタイム
            workKanban.setStartDatetime(plan.getWorkStartTime()); // 作業開始日時
            workKanban.setCompDatetime(plan.getWorkEndTime());    // 作業終了日時
            workKanban.setOrganizations(organization);            // 組織
            workKanban.setEquipments(plan.getMainResource());     // 設備
            workKanban.setSkipFlag(plan.getSkipFlag() ? "1" : "0"); // スキップフラグ

            // スキップ (工程が完了していた場合はスキップにする)
            workKanban.setWorkStatus(plan.getWorkKanbanStatus());
            kanbanMap.put(kanbanName + workKanban.getWorkName(), workKanban);
        }

        Map<String, ImportAsprovaPlanCsv> plans
                = importPlans
                .stream()
                .collect(groupingBy(plan -> plan.getKanbanName() + "###" + plan.getWorkflowName(), collectingAndThen(toList(), list -> list.get(0))));

        Map<String, List<ConWorkflowWorkInfoEntity>> map = new HashMap<>();
        for (ImportAsprovaPlanCsv plan : plans.values()) {
            if (!map.containsKey(plan.getWorkflowName())) {
                WorkflowInfoEntity workflowInfoEntity = workflowInfoFacade.findName(plan.getWorkflowName());
                map.put(plan.getWorkflowName(), workflowInfoEntity.getConWorkflowWorkInfoCollection());
            }

            map.get(plan.getWorkflowName())
                    .stream()
                    .filter(ConWorkflowWorkInfoEntity::getSkipFlag)
                    .forEach(entity -> {
                        String name = plan.getKanbanName() + entity.getWorkName();
                        if (kanbanMap.containsKey(plan.getKanbanName() + entity.getWorkName())) {
                            return;
                        }
                        ImportWorkKanbanCsv workKanban = new ImportWorkKanbanCsv();
                        workKanban.setKanbanName(plan.getKanbanName());                 // カンバン名
                        workKanban.setWorkName(entity.getWorkName());           // 工程名
                        workKanban.setSkipFlag("1");
                        // スキップ (工程が完了していた場合はスキップにする)
                        kanbanMap.put(name, workKanban);
                    });
        }

        logger.info("createWorkKanbanCsv end.");
        return Optional.of(new LinkedList<>(kanbanMap.values()));
    }

    /**
     * 生産計画ファイルから工程カンバンプロパティ情報を作成
     *
     * @param importPlans 生産計画ファイル情報
     * @return 成功時 = 工程カンバンプロパティ情報 失敗時 = null
     */
    private List<ImportWorkKanbanPropertyCsv> createWorkKanbanPropertyCsv(List<ImportAsprovaPlanCsv> importPlans) {
        logger.info("createWorkKanbanPropertyCsv start.");
        List<ImportWorkKanbanPropertyCsv> importKanbanPropertys = null;

        try {
            Map<String, ImportWorkKanbanPropertyCsv> kanbanProperyMap = new LinkedHashMap<>();
            for (ImportAsprovaPlanCsv plan : importPlans) {
                String kanbanName = plan.getKanbanName();
                String workName = plan.getWorkName();
                // コード
                kanbanProperyMap.put(new StringBuilder(kanbanName).append(workName).append(ImportAsprovaPlanCsv.CODE).toString(), new ImportWorkKanbanPropertyCsv(kanbanName, plan.getWorkName(), null, ImportAsprovaPlanCsv.CODE, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getCode()));
                // 番号
                kanbanProperyMap.put(new StringBuilder(kanbanName).append(workName).append(ImportAsprovaPlanCsv.WORK_NUMBER).toString(), new ImportWorkKanbanPropertyCsv(kanbanName, plan.getWorkName(), null, ImportAsprovaPlanCsv.WORK_NUMBER, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getWorkNumber()));
                // 機械番号
                kanbanProperyMap.put(new StringBuilder(kanbanName).append(workName).append(ImportAsprovaPlanCsv.MACHINE_CODE).toString(), new ImportWorkKanbanPropertyCsv(kanbanName, plan.getWorkName(), null, ImportAsprovaPlanCsv.MACHINE_CODE, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getMachineCode()));
                // ワークセンタ
                kanbanProperyMap.put(new StringBuilder(kanbanName).append(workName).append(ImportAsprovaPlanCsv.WORK_CENTER).toString(), new ImportWorkKanbanPropertyCsv(kanbanName, plan.getWorkName(), null, ImportAsprovaPlanCsv.WORK_CENTER, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getWorkCenter()));
                // Asprova連携フラグ
                kanbanProperyMap.put(new StringBuilder(kanbanName).append(workName).append(ImportAsprovaPlanCsv.LINK_ASP_FLG).toString(), new ImportWorkKanbanPropertyCsv(kanbanName, plan.getWorkName(), null, ImportAsprovaPlanCsv.LINK_ASP_FLG, CustomPropertyTypeEnum.TYPE_BOOLEAN.name(), Boolean.TRUE.toString()));
                // 数量
                kanbanProperyMap.put(new StringBuilder(kanbanName).append(workName).append(ImportAsprovaPlanCsv.LOT_QUANTITY).toString(), new ImportWorkKanbanPropertyCsv(kanbanName, plan.getWorkName(), null, ImportAsprovaPlanCsv.LOT_QUANTITY, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getLotQuantity()));

                // 資産番号
                kanbanProperyMap.put(new StringBuffer(kanbanName).append(workName).append(ImportAsprovaPlanCsv.ASSERT_NUMBER).toString(), new ImportWorkKanbanPropertyCsv(kanbanName, plan.getWorkName(), null, ImportAsprovaPlanCsv.ASSERT_NUMBER, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getAssertNumber()));
                // プログラム番号
                kanbanProperyMap.put(new StringBuilder(kanbanName).append(workName).append(ImportAsprovaPlanCsv.PROGRAM_NUMBER).toString(), new ImportWorkKanbanPropertyCsv(kanbanName, plan.getWorkName(), null, ImportAsprovaPlanCsv.PROGRAM_NUMBER, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getProgramNumber()));

                // 前段取り時間
                if (Objects.nonNull(plan.getPreSetupTime())) {
                    kanbanProperyMap.put(new StringBuilder(kanbanName).append(workName).append(ImportAsprovaPlanCsv.PRE_SETUP_TIME).toString(), new ImportWorkKanbanPropertyCsv(kanbanName, plan.getWorkName(), null, ImportAsprovaPlanCsv.PRE_SETUP_TIME, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getPreSetupTime()));
                }

                // 後段取り時間
                if (Objects.nonNull(plan.getPostSetupTime())) {
                    kanbanProperyMap.put(new StringBuilder(kanbanName).append(workName).append(ImportAsprovaPlanCsv.POST_SETUP_TIME).toString(), new ImportWorkKanbanPropertyCsv(kanbanName, plan.getWorkName(), null, ImportAsprovaPlanCsv.POST_SETUP_TIME, CustomPropertyTypeEnum.TYPE_STRING.name(), plan.getPostSetupTime()));
                }
            }
            importKanbanPropertys = new LinkedList<>(kanbanProperyMap.values());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("createWorkKanbanPropertyCsv end.");
        }

        return importKanbanPropertys;
    }


    /**
     * Asprova製造BomCSVファイルを読み込む。
     *
     * @param fileName Asprova製造Bom インポート用ファイル名
     * @return Asprova製造Bom インポート用データ一覧
     */
    private Optional<List<ImportAsprovaBomCsv>> readAspBomCsv(String fileName) {
        if (Files.notExists(Paths.get(fileName))) {
            this.addResult("   > BOMファイルが存在しません。");
            return Optional.empty();
        }

        // ファイルの文字コード
        try (FileInputStream fis = new FileInputStream(fileName);
             InputStreamReader isr = new InputStreamReader(fis, CHARSET_SJIS);
             CSVReader csvReader = new CSVReader(isr)) {  // ファイル読み込み

            // ヘッダ行
            List<String> header = Arrays.asList(csvReader.readNext());
            if (header.isEmpty()) {
                this.addResult("   > ファイルが空です。");
                return Optional.empty();
            }

            if (ImportAsprovaBomCsv.ColumnNumber > header.size()) {
                this.addResult("  > ヘッダの項目数が正しくありません。ファイルを見直して下さい。");
                return Optional.empty();
            }

            int count = 1;
            String[] line;
            List<String> errorMessages = new ArrayList<>();
            List<ImportAsprovaBomCsv> importAsprovaWorks = new LinkedList<>();
            while ((line = csvReader.readNext()) != null) {
                ++count;
                ImportAsprovaBomCsv data = new ImportAsprovaBomCsv();
                final String errorMessage = data.setValue(count, header, Arrays.asList(line));
                if (!StringUtils.isEmpty(errorMessage)) {
                    errorMessages.add(errorMessage);
                    continue;
                }

                // 前段取り
                data.createPreSetupBom().ifPresent(importAsprovaWorks::add);
                // 実作業
                importAsprovaWorks.add(data);
                // 後段取り (2022/4/29 伊里さんの要望にて削除)
                //data.createPostSetupBom().ifPresent(importAsprovaWorks::add);
            }

            // データチェック
            errorMessages.addAll(importAsprovaWorks.stream()
                    .map(this::checkBomCsvData)
                    .flatMap(Collection::stream)
                    .filter(msg -> !StringUtils.isEmpty(msg))
                    .collect(toList()));

            if (!errorMessages.isEmpty()) {
                errorMessages.forEach(this::addResult);
                return Optional.empty();
            }

            // 工程順名、工程番号、工程名でグループ化, 並列工程はマージする
            Map<String, Map<String, Map<String, ImportAsprovaBomCsv>>> importWorksGroup
                    = importAsprovaWorks
                    .stream()
                    .collect(groupingBy(ImportAsprovaBomCsv::getWorkflowName,
                            groupingBy(ImportAsprovaBomCsv::getWorkNumber,
                                    groupingBy(ImportAsprovaBomCsv::getWorkName,
                                            collectingAndThen(
                                                    toList(),
                                                    list -> {
                                                        List<String> parallelSetupWorkName
                                                                = list
                                                                .stream()
                                                                .map(ImportAsprovaBomCsv::getParallelSetupWorkName)
                                                                .flatMap(Collection::stream)
                                                                .distinct()
                                                                .collect(toList());
                                                        ImportAsprovaBomCsv csv = list.get(0);
                                                        csv.setParallelSetupWorkName(parallelSetupWorkName);
                                                        return csv;
                                                    })))));

            List<ImportAsprovaBomCsv> ret = importWorksGroup
                    .values()
                    .parallelStream()
                    .flatMap(workflowGroup -> workflowGroup.entrySet()
                            .stream()
                            // 工程番号でソート
                            .sorted(Comparator.comparing(entry -> Integer.parseInt(entry.getKey())))
                            .map(Map.Entry::getValue)
                            .map(Map::values)
                            .flatMap(workNumberGroup -> workNumberGroup
                                    .stream()
                                    // 前段取り、後段取りでソート
                                    .sorted((a, b) -> {
                                        if (!StringUtils.equals(a.getBaseWorkCode(), b.getBaseWorkCode())) {
                                            return a.getRowNo() < b.getRowNo() ? -1 : 1;
                                        }

                                        if (ImportAsprovaBomCsv.WORK_TYPE.FD.equals(a.getWorkType())) return -1;
                                        if (ImportAsprovaBomCsv.WORK_TYPE.BD.equals(a.getWorkType())) return 1;
                                        if (ImportAsprovaBomCsv.WORK_TYPE.FD.equals(b.getWorkType())) return 1;
                                        if (ImportAsprovaBomCsv.WORK_TYPE.BD.equals(b.getWorkType())) return -1;
                                        return 0;
                                    })
                            )
                    )
                    .collect(toList());

            return Optional.of(ret);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            // 読み込み中に、不明なエラーが発生しました。
            this.addResult("   > " + LocaleUtils.getString("key.import.production.error"));
            return Optional.empty();
        }
    }

    /**
     * 製造BOMファイルから工程情報一覧を作成
     *
     * @param importBoms      製造Bomファイル情報
     * @param userId          ユーザーID
     * @param workHierarchyId 工程順階層ID
     * @return 成功時 = 工程情報 失敗時 = null
     */
    private List<WorkInfoEntity> createWorkList(List<ImportAsprovaBomCsv> importBoms, Long userId, Long workHierarchyId) {
        logger.info("createWorkList start.");
        final Date now = new Date();

        Set<String> unique = new HashSet<>();
        List<WorkInfoEntity> ret
                = importBoms
                .stream()
                .filter(bom -> unique.add(bom.getWorkName()))
                .map(bom -> createWorkInfoEntity(userId, workHierarchyId, now, bom))
                .collect(toList());

        // 並列項目を追加
        ret.addAll(importBoms
                .stream()
                .flatMap(bom -> bom.getParallelSetupWorkName()
                        .stream()
                        .filter(unique::add)
                        .map(workName -> {
                            WorkInfoEntity parallelWorkInfoEntity = createWorkInfoEntity(userId, null, now, bom);
                            parallelWorkInfoEntity.setWorkName(workName);
                            return parallelWorkInfoEntity;
                        }))
                .collect(toList()));
        return ret;
    }

    /**
     * 工程情報エンティティを作成
     *
     * @param userId          ユーザID
     * @param workHierarchyId 工程階層
     * @param now             現在の時刻
     * @param bom             工程情報
     * @return 工程情報エンティティ
     */
    private WorkInfoEntity createWorkInfoEntity(Long userId, Long workHierarchyId, Date now, ImportAsprovaBomCsv bom) {
        // 工程情報作成
        WorkInfoEntity work = new WorkInfoEntity();
        work.setParentId(workHierarchyId);
        work.setWorkName(bom.getWorkName());
        work.setTaktTime(0);
        work.setContentType(ContentTypeEnum.STRING);
        work.setUpdatePersonId(userId);
        work.setUpdateDatetime(now);

        // プロパティの更新
        WorkPropertyInfoEntity prop = new WorkPropertyInfoEntity(null, null, "工程コード", CustomPropertyTypeEnum.TYPE_STRING, bom.getWorkCode(), 1);
        work.getPropertyInfoCollection().add(prop);

        WorkPropertyInfoEntity workType = new WorkPropertyInfoEntity(null, null, "種別", CustomPropertyTypeEnum.TYPE_STRING, bom.getWorkType().name, 2);
        work.getPropertyInfoCollection().add(workType);

        // 表示項目の更新
        String dispItems = AdProperty.getProperties().getProperty(ProductionNaviPropertyConstants.ASP_BOM_ADD_DISPLAYITEMS, "");
        if (StringUtils.isEmpty(dispItems)) {
            dispItems = "[]";
        }
        work.setDisplayItems(dispItems);

        return work;
    }


    /**
     * デフォルトの日付を生成
     *
     * @return デフォルトの日付
     */
    private static Date createDefaultDate() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1970-01-01 00:00:00+00");
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ゼロ時間生成
     *
     * @return ゼロ時間
     */
    private static Date createZeroDate() {
        try {
            return new SimpleDateFormat("HH:mm").parse("00:00");
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final Date defaultDate = createDefaultDate();
    private static final Date zero = createZeroDate();

    /**
     * 製造BOMファイルから工程順情報一覧を作成
     *
     * @param importBoms          製造BOMファイル情報
     * @param workIdMap           工程名／工程ID
     * @param workflowHierarchyId 工程順階層ID
     * @param updateUserId        更新者ID
     * @param isSerial            直列かどうか
     * @return 成功時 = 工程情報 失敗時 = null
     */
    private List<WorkflowInfoEntity> createWorkflowList(List<ImportAsprovaBomCsv> importBoms, Map<String, String> workIdMap, Long workflowHierarchyId, Long updateUserId, Boolean isSerial) {
        logger.info("createWorkflowList start.");

        Date updateTime = new Date();

        Map<String, Map<String, List<ImportAsprovaBomCsv>>> bomList
                = importBoms
                .stream()
                .collect(Collectors.groupingBy(ImportAsprovaBomCsv::getWorkflowName, // 工程順名 でグルーピング
                        Collectors.groupingBy(ImportAsprovaBomCsv::getWorkName, LinkedHashMap::new, Collectors.toList())));   // 工程名 でグルーピング

        // 工程順情報エンティティ作成
        List<WorkflowInfoEntity> workflowList = new ArrayList<>();
        for (Map.Entry<String, Map<String, List<ImportAsprovaBomCsv>>> workflowEntry : bomList.entrySet()) {
            WorkflowInfoEntity workflow = createWorkflowInfoEntity(workflowHierarchyId, updateUserId, updateTime, workflowEntry.getKey());
            WorkflowModel workflowModel = new WorkflowModel();
            WorkflowPane pane = workflowModel.getWorkflowPane();
            pane.setWorkflowEntity(workflow);
            workflowModel.createWorkflow(pane, true, true);

            CellBase selectedCell = workflowModel.getWorkflowPane().getCellList().get(0);
            selectedCell.setSelected(true);

            BiFunction<List<WorkCell>, CellBase, CellBase> applyParallelWork = getApplyParallelWork(workflowModel);

            if (isSerial) {
                // 直列
                List<List<ConWorkflowWorkInfoEntity>> conWorkflowWorkInfoEntitiesList
                        = workflowEntry.getValue()
                        .values()
                        .stream()
                        .map(item -> {
                            List<ConWorkflowWorkInfoEntity> conWorkflowWorkInfoEntities
                                    = item
                                    .stream()
                                    .map(ImportAsprovaBomCsv::getWorkName)
                                    .map(workName -> createConWorkflowWorkInfoEntity(workName, Long.parseLong(workIdMap.get(workName))))
                                    .collect(toList());

                            List<String> parallelSetWorkNames
                                    = item
                                    .stream()
                                    .map(ImportAsprovaBomCsv::getParallelSetupWorkName)
                                    .flatMap(Collection::stream)
                                    .distinct()
                                    .collect(toList());

                            if (!parallelSetWorkNames.isEmpty()) {
                                // 並列工程がある場合は本工程をスキップする
                                conWorkflowWorkInfoEntities.addAll(
                                        parallelSetWorkNames
                                                .stream()
                                                .map(workName -> createConWorkflowWorkInfoEntity(workName, Long.parseLong(workIdMap.get(workName))))
                                                .collect(toList()));
                                conWorkflowWorkInfoEntities.forEach(conWorkflowWorkInfoEntity -> conWorkflowWorkInfoEntity.setSkipFlag(true));
                            }
                            return conWorkflowWorkInfoEntities;
                        })
                        .collect(toList());

                workflow.setConWorkflowWorkInfoCollection(conWorkflowWorkInfoEntitiesList.stream().flatMap(Collection::stream).collect(toList()));

                // グラフを作成
                for (List<ConWorkflowWorkInfoEntity> conWorkflowWorkInfoEntities : conWorkflowWorkInfoEntitiesList) {
                    List<WorkCell> workCells
                            = conWorkflowWorkInfoEntities
                            .stream()
                            .map(workflowModel::createWorkCell)
                            .collect(toList());
                    selectedCell = applyParallelWork.apply(workCells, selectedCell);
                }
            } else {
                // 並列
                List<ConWorkflowWorkInfoEntity> conWorkflowWorkInfoEntityList
                        = workflowEntry.getValue()
                        .values()
                        .stream()
                        .flatMap(item -> {
                            List<ConWorkflowWorkInfoEntity> conWorkflowWorkInfoEntities
                                    = item
                                    .stream()
                                    .map(ImportAsprovaBomCsv::getWorkName)
                                    .map(workName -> createConWorkflowWorkInfoEntity(workName, Long.parseLong(workIdMap.get(workName))))
                                    .collect(toList());

                            List<String> parallelSetWorkNames
                                    = item
                                    .stream()
                                    .map(ImportAsprovaBomCsv::getParallelSetupWorkName)
                                    .flatMap(Collection::stream)
                                    .distinct()
                                    .collect(toList());

                            if (!parallelSetWorkNames.isEmpty()) {
                                // 並列工程がある場合は本工程をスキップする
                                conWorkflowWorkInfoEntities.addAll(
                                        parallelSetWorkNames
                                                .stream()
                                                .map(workName -> createConWorkflowWorkInfoEntity(workName, Long.parseLong(workIdMap.get(workName))))
                                                .collect(toList()));
                                conWorkflowWorkInfoEntities.forEach(conWorkflowWorkInfoEntity -> conWorkflowWorkInfoEntity.setSkipFlag(true));
                            }
                            return conWorkflowWorkInfoEntities.stream();
                        })
                        .collect(toList());

                workflow.setConWorkflowWorkInfoCollection(conWorkflowWorkInfoEntityList);

                // グラフを作成
                List<WorkCell> workCells
                        = conWorkflowWorkInfoEntityList
                        .stream().map(workflowModel::createWorkCell)
                        .collect(toList());
                applyParallelWork.apply(workCells, selectedCell);
            }
            workflowModel.getWorkflow();
            workflowList.add(workflow);
        }

        logger.info("createWorkflowList end.");
        return workflowList;
    }

    /**
     * 並列ワークを生成関数取得
     *
     * @param works  WorkCellリスト
     * @param select 選択中のCellリスト
     * @return 次のCellリスト
     */
    private static BiFunction<List<WorkCell>, CellBase, CellBase> getApplyParallelWork(WorkflowModel workflowModel) {
        BiFunction<WorkCell, CellBase, WorkCell> applySerialWork = (WorkCell work, CellBase select) -> {
            workflowModel.add(select, work);
            work.setSelected(true);
            workflowModel.updateTimetable(work, 0, false);
            workflowModel.updateWorkflowOrder();
            return work;
        };

        // 並列
        return (List<WorkCell> works, CellBase select) -> {
            if (works.size() == 1) {
                return applySerialWork.apply(works.get(0), select);
            }

            ParallelStartCell parallelStartCell = workflowModel.createParallelStartCell();
            ParallelEndCell parallelEndCell = workflowModel.createParallelEndCell(parallelStartCell);
            workflowModel.addGateway(select, parallelStartCell, parallelEndCell);

            for (WorkCell work : works) {
                if (workflowModel.add(parallelStartCell, work)) {
                    workflowModel.updateTimetable(work, 0, false);
                    workflowModel.updateWorkflowOrder();
                }
            }
            return parallelEndCell;
        };
    }


    /**
     * 工程順・工程関連情報エンティティ生成
     *
     * @param workName 工程名
     * @param workId   工程ID
     * @return 工程順・工程関連情報エンティティ
     */
    private ConWorkflowWorkInfoEntity createConWorkflowWorkInfoEntity(String workName, long workId) {
        // 含まれていなければ工程順工程関連付けを追加
        ConWorkflowWorkInfoEntity workflowWork = new ConWorkflowWorkInfoEntity();
        workflowWork.setWorkName(workName);
        workflowWork.setFkWorkId(workId);
        workflowWork.setOrganizationCollection(new ArrayList<>());
        workflowWork.setEquipmentCollection(new ArrayList<>());
        workflowWork.setStandardStartTime(defaultDate);
        workflowWork.setStandardEndTime(defaultDate);
        return workflowWork;
    }

    /**
     * 工程順エンティティ作成
     *
     * @param workflowHierarchyId 階層ID
     * @param updateUserId        ユーザID
     * @param zero                ゼロ
     * @param updateTime
     * @param workflowName
     * @return
     */
    private WorkflowInfoEntity createWorkflowInfoEntity(Long workflowHierarchyId, Long updateUserId, Date updateTime, String workflowName) {
        WorkflowInfoEntity workflow;
        // 工程順インポート用エンティティ作成
        workflow = new WorkflowInfoEntity();
        workflow.setWorkflowName(workflowName);
        workflow.setParentId(workflowHierarchyId);
        workflow.setModelName("");

        workflow.setOpenTime(zero);
        workflow.setCloseTime(zero);
        workflow.setUpdateDatetime(updateTime);
        workflow.setFkUpdatePersonId(updateUserId);
        workflow.setConWorkflowWorkInfoCollection(new ArrayList<>());
        return workflow;
    }

    /**
     * コンポーネント破棄
     *
     * @return 結果
     */
    @Override
    public boolean destoryComponent() {
        try {
            if (this.isImportProccessing) {
                // 「読み込み処理中のため、操作は無効です。」を表示
                String title = LocaleUtils.getString("key.KanbanImportTitle");
                String message = LocaleUtils.getString("key.alert.import.proccessing");

                sc.showAlert(Alert.AlertType.ERROR, title, message);
                return false;
            }

            // プロパティを保存する。
            this.saveProperties();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return true;
    }


    /**
     * プロパティを保存する。
     */
    private void saveProperties() {
        try {
            SingleSelectionModel<Tab> selectionModel = this.tabImportMode.getSelectionModel();
            Properties properties = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

            properties.setProperty(ProductionNaviPropertyConstants.SELECT_LINK_ASP_TAB, String.valueOf(selectionModel.getSelectedIndex()));
            properties.setProperty(ProductionNaviPropertyConstants.SELECT_ASP_PLAN_FOLDER_PATH, this.importAspPlanFileField.getText());
            properties.setProperty(ProductionNaviPropertyConstants.SELECT_ASP_EXPORT_FOLDER_PATH, this.exportFolderField.getText());
            properties.setProperty(ProductionNaviPropertyConstants.SELECT_ASP_BOM_FOLDER_PATH, this.importBomFileField.getText());
            properties.setProperty(ProductionNaviPropertyConstants.SELECT_ASP_MONTHLY_PLAN_FOLDER_PATH, this.importAspMonthlyPlanFolderField.getText());

            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 実績出力
     *
     * @param event 　イベント
     */
    @FXML
    private void onExportButton(ActionEvent event) {

        blockUI(true);
        this.resultList.getItems().clear();

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    isImportProccessing = true;
                    //開始
                    addResult("生産実績取り出しを開始します。");
                    logger.info("生産実績取り出しを開始します");

                    // 確認するフォルダとファイルのパスを指定する
                    File folder = new File(exportFolderField.getText());

                    if (Objects.isNull(exportFolderField.getText()) || exportFolderField.getText().isEmpty()) {
                        addResult("出力フォルダを選択してください。");
                        return null;
                    }

                    // フォルダの存在を確認する
                    if (!folder.exists()) {
                        addResult("正しい出力フォルダを選択してください。");
                        return null;
                    }

                    SimpleDateFormat formerFileName = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    Date now = new Date();
                    String nowDate = formerFileName.format(now);
                    String fileName = "actualReport_" + kanbanHierarchyFieldForExport.getText() + "_" + nowDate;

                    // カンバン階層
                    KanbanHierarchyInfoEntity kanbanHierarchy = null;
                    Long kanbanHierarchyId = null;

                    // カンバン階層の値チェック
                    if (Objects.isNull(kanbanHierarchyFieldForExport.getText()) || kanbanHierarchyFieldForExport.getText().isEmpty()) {
                        addResult("カンバン階層を選択してください");
                        return null;
                    }

                    logger.debug(" カンバン階層名の検索:" + kanbanHierarchyFieldForExport.getText());
                    kanbanHierarchy = kanbanHierarchyInfoFacade.findHierarchyName(URLEncoder.encode(kanbanHierarchyFieldForExport.getText(), CHARSET_UTF8));
                    kanbanHierarchyId = kanbanHierarchy.getKanbanHierarchyId();
                    if (Objects.isNull(kanbanHierarchyId)) {
                        // 存在しないカンバン階層のためスキップ
                        addResult("存在しないカンバン階層、またはアクセス権がないため別のカンバン階層を選択してください。");
                        return null;
                    }

                    // 工程カンバンの取得
                    KanbanSearchCondition condition = new KanbanSearchCondition();
                    List<KanbanStatusEnum> statusCondition = Arrays.asList(
                            KanbanStatusEnum.COMPLETION,
                            KanbanStatusEnum.WORKING,
                            KanbanStatusEnum.SUSPEND
                    );
                    condition.setKanbanStatusCollection(statusCondition);
                    condition.setHierarchyId(kanbanHierarchyId);
                    condition.setOutputFlag(true);
                    List<WorkKanbanInfoEntity> workKanbans = workKanbanInfoFacade.findSearchRange(condition, null, null);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                    if (workKanbans.isEmpty()) {
                        addResult("出力対象の工程カンバンが存在しないため処理を終了します。");
                        return null;
                    }

                    // ファイル作成
                    String TEMP_PATH = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator + "actualReport";
                    File tempExportCsvFolder = new File(TEMP_PATH);
                    if (!tempExportCsvFolder.exists()) {
                        if (!tempExportCsvFolder.mkdir()) {
                            logger.fatal("生産実績フォルダの作成に失敗しました。" + TEMP_PATH);
                            addResult("生産実績ファイルの作成に失敗しました。もう一度実績取り出しを行ってください。");
                            return null;
                        }
                    }

                    File tempExportCsvFile = new File(TEMP_PATH + "\\" + fileName + ".csv");
                    File exportCsvFile = new File(exportFolderField.getText() + "\\" + fileName + ".csv");
                    try {
                        tempExportCsvFile.createNewFile();
                    } catch (IOException ex) {
                        logger.fatal("生産実績ファイルの作成に失敗しました。" + tempExportCsvFile.getPath());
                        addResult("生産実績ファイルの作成に失敗しました。もう一度実績取り出しを行ってください。");
                        return null;
                    }

                    StringBuilder rowSb = new StringBuilder();

                    // ヘッダの設定
                    rowSb.append("コード,ステータス,実績開始日時,実績終了日時,実績数量,種別").append(System.lineSeparator());

                    List<Long> updateWorkKanbanList = new ArrayList<>();
                    int count = 0;
                    for (WorkKanbanInfoEntity workKanban : workKanbans) {
                        boolean workKanbanLinkAspFlg = false;
                        String workKanbanLotQuantity = "";
                        String workKanbanCode = "";
                        String workType = "P";

                        boolean isAddPostSetupActual = false;
                        for (WorkKanbanPropertyInfoEntity addInfo : workKanban.getPropertyCollection()) {
                            final String workKanbanPropName = addInfo.getWorkKanbanPropName();
                            // LINK_ASP_FLG
                            if (ImportAsprovaPlanCsv.LINK_ASP_FLG.equals(workKanbanPropName)) {
                                if (addInfo.getWorkKanbanPropValue().equals("true")) {
                                    workKanbanLinkAspFlg = true;
                                } else {
                                    break;
                                }
                            } else if (ImportAsprovaPlanCsv.LOT_QUANTITY.equals(workKanbanPropName)) {
                                workKanbanLotQuantity = addInfo.getWorkKanbanPropValue();
                            } else if (ImportAsprovaPlanCsv.CODE.equals(workKanbanPropName)) {
                                workKanbanCode = addInfo.getWorkKanbanPropValue();
                            } else if (ImportAsprovaBomCsv.workTypeName.equals(workKanbanPropName)) {
                                final String val = addInfo.getWorkKanbanPropValue();
                                workType = Arrays
                                        .stream(ImportAsprovaBomCsv.WORK_TYPE.values())
                                        .filter(type -> type.name.equals(val))
                                        .map(type -> type.actualType)
                                        .findFirst()
                                        .orElse("");
                            } else if (ImportAsprovaPlanCsv.POST_SETUP_TIME.equals(workKanbanPropName)) {
                                final Long val = Long.parseLong(addInfo.getWorkKanbanPropValue());
                                isAddPostSetupActual = Objects.nonNull(val) && val <= 1;
                            }
                        }

                        if (!workKanbanLinkAspFlg) {
                            // 対象外のため次の工程カンバンに移る
                            continue;
                        }

                        String status = workKanban.getWorkStatus().toString();
                        if (KanbanStatusEnum.WORKING.toString().equals(status) || KanbanStatusEnum.SUSPEND.toString().equals(status)) {
                            status = "T";
                        } else if (KanbanStatusEnum.COMPLETION.toString().equals(status)) {
                            status = "B";
                        }

                        rowSb.append(workKanbanCode); // 追加情報の「コード」
                        rowSb.append(",");
                        rowSb.append(status); // ステータス
                        rowSb.append(",");
                        rowSb.append(sdf.format(workKanban.getActualStartTime())); // 開始時刻
                        rowSb.append(",");
                        if (status.equals("B")) { //　ステータス完了なら終了時刻を出力し、出力フラグfalseに更新
                            rowSb.append(sdf.format(workKanban.getActualCompTime())); // 終了時刻
                        } else {
                            rowSb.append("");
                        }
                        rowSb.append(",");
                        if (status.equals("B")) { //　ステータス完了なら終了時刻を出力し、出力フラグfalseに更新
                            rowSb.append(workKanbanLotQuantity); // 追加情報の「数量」
                        } else {
                            rowSb.append("");
                        }
                        rowSb.append(",");
                        rowSb.append(workType);
                        rowSb.append(System.lineSeparator());

                        // 強制的に後段取りを追加
                        if (isAddPostSetupActual && StringUtils.equals("B", status)) {
                            rowSb.append(workKanbanCode); // 追加情報の「コード」
                            rowSb.append(",");
                            rowSb.append("B"); // ステータス
                            rowSb.append(",");
                            rowSb.append(sdf.format(workKanban.getActualCompTime())); // 開始時刻
                            rowSb.append(",");
                            rowSb.append(sdf.format(workKanban.getActualCompTime())); // 終了時刻
                            rowSb.append(",");
                            rowSb.append(workKanbanLotQuantity); // 追加情報の「数量」
                            rowSb.append(",");
                            rowSb.append("T"); // 後段取り
                            rowSb.append(System.lineSeparator());
                        }

                        // アプデリストに格納
                        updateWorkKanbanList.add(workKanban.getWorkKanbanId());

                        count++;

                        if (count == 1 || count % 50 == 0) {
                            addResult(count + "件目の処理が完了しました。");
                        }
                    }

                    // ファイル出力
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempExportCsvFile), CHARSET_SJIS))) {
                        writer.write(rowSb.toString());
                    } catch (IOException ex) {
                        logger.info(ex);
                        addResult("生産実績ファイルの作成に失敗しました。もう一度実績取り出しを行ってください。");
                        return null;
                    }

                    if (!tempExportCsvFile.renameTo(exportCsvFile)) {
                        addResult("生産実績ファイルの作成に失敗しました。対象フォルダのファイル作成権限を確認してください。");
                    }

                    // 要実績出力フラグを更新する
                    if (!updateWorkKanbanList.isEmpty()) {
                        for (int n = 0; n < updateWorkKanbanList.size(); ) {
                            int next = Math.min(n + 50, updateWorkKanbanList.size());
                            ResponseEntity entity = workKanbanInfoFacade.updateOutputFlg(updateWorkKanbanList.subList(n, next), false, now);
                            if (!entity.isSuccess()) {
                                try {
                                    tempExportCsvFile.delete();
                                } catch (Exception e) {
                                    logger.fatal(e);
                                    addResult("サーバとの通信中にエラーが発生しました。もう一度実績取り出しを行ってください。");
                                    return null;
                                }
                                logger.fatal("要実績出力フラグの更新時にエラーが発生しました。");
                                addResult("サーバとの通信中にエラーが発生しました。もう一度実績取り出しを行ってください。");
                                return null;
                            }
                            n = next;
                        }
                    }

                    // 完了
                    addResult("処理件数:" + String.valueOf(count) + "\n" + "生産実績取り出し終了" + "[" + exportCsvFile.getPath() + "]");
                    logger.info("生産実績取り出し終了");
                } catch (Exception ex) {
                    logger.fatal(ex);
                    addResult("原因不明なエラーが発生しました。");
                    return null;
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                blockUI(false);
                isImportProccessing = false;
            }
        };

        new Thread(task).start();
    }

    /**
     * カンバン階層選択
     *
     * @param event
     */
    @FXML
    private void onSelectKanbanHierarchyButton(ActionEvent event) {
        blockUI(true);
        try {
            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(kanbanHierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"));
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Kanban"), "KanbanHierarchyTreeCompo", treeDialogEntity);

            TreeItem<KanbanHierarchyInfoEntity> hierarchy = (TreeItem<KanbanHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (ret.equals(ButtonType.OK) && Objects.nonNull(hierarchy)) {
                String hierarchyName = hierarchy.getValue().getHierarchyName();
                this.kanbanHierarchyField.setText(hierarchyName);

                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_ASP_PLAN_KANBAN_HIER, hierarchyName);
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * カンバン階層選択
     *
     * @param event
     */
    @FXML
    private void onSelectKanbanHierarchyButtonForExport(ActionEvent event) {
        blockUI(true);
        try {
            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(kanbanHierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"));
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Kanban"), "KanbanHierarchyTreeCompo", treeDialogEntity);

            TreeItem<KanbanHierarchyInfoEntity> hierarchy = (TreeItem<KanbanHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (ret.equals(ButtonType.OK) && Objects.nonNull(hierarchy)) {
                String hierarchyName = hierarchy.getValue().getHierarchyName();
                this.kanbanHierarchyFieldForExport.setText(hierarchyName);

                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_ASP_EXPORT_KANBAN_HIER, hierarchyName);
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 工程階層選択
     *
     * @param event
     */
    @FXML
    private void onSelectWorkHierarchyButton(ActionEvent event) {
        blockUI(true);
        try {
            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(workHierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"));
            ButtonType ret = sc.showComponentDialog("工程階層選択", "WorkHierarchyTreeCompo", treeDialogEntity);
            TreeItem<WorkHierarchyInfoEntity> hierarchy = (TreeItem<WorkHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (ret.equals(ButtonType.OK) && Objects.nonNull(hierarchy)) {
                String hierarchyName = hierarchy.getValue().getHierarchyName();
                this.workHierarchyField.setText(hierarchyName);

                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_ASP_BOM_WORK_HIER, hierarchyName);
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 工程順階層選択
     *
     * @param event
     */
    @FXML
    private void onSelectWorkflowHierarchyButton(ActionEvent event) {
        blockUI(true);
        try {
            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(this.workFlowHierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"));
            treeDialogEntity.setIsUseHierarchy(Boolean.TRUE);
            ButtonType ret = sc.showComponentDialog("工程順階層選択", "WorkflowHierarchyTreeCompo", treeDialogEntity);
            TreeItem<WorkflowHierarchyInfoEntity> dest = (TreeItem<WorkflowHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();

            if (ret.equals(ButtonType.OK) && Objects.nonNull(dest)) {
                String hierarchyName = dest.getValue().getHierarchyName();
                this.workflowHierarchyField.setText(hierarchyName);

                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_ASP_BOM_WORKFLOW_HIER, hierarchyName);
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * フォルダ選択（csv)ボタンのクリックイベント
     *
     * @param event
     */
    @FXML
    private void onSelectBomFileAction(ActionEvent event) {
        blockUI(true);
        try {
            FileChooser fc = new FileChooser();
            File fol = new File(importBomFileField.getText());
            if (fol.exists()) {
                if (fol.isDirectory()) {
                    fc.setInitialDirectory(fol);
                } else if (fol.isFile()) {
                    if (fol.getParentFile() != null) {
                        fc.setInitialDirectory(fol.getParentFile());
                    }
                }
            }

            fc.setTitle("csvファイル選択");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("csvファイル選択", "*.csv"));

            File selectedFile = fc.showOpenDialog(sc.getStage().getScene().getWindow());
            if (selectedFile != null) {
                importBomFileField.setText(selectedFile.getPath());

                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_LINK_ASP_TAB, String.valueOf(TAB_IDX_EXPORT));
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_ASP_BOM_FOLDER_PATH, selectedFile.getPath());
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * キャッシュに情報を読み込み、カンバン階層ツリーを更新する。
     *
     * @param isRefresh
     */
    private void updateKanbanTree(boolean isRefresh) {
        logger.info("updateTree start.");
        try {
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

                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        if (isRefresh || Objects.isNull(kanbanEditPermanenceData.getKanbanHierarchyRootItem())) {
                            createRoot(null);
                        } else {
                            kanbanHierarchyTree.rootProperty().setValue(kanbanEditPermanenceData.getKanbanHierarchyRootItem());
                            selectedTreeItem(kanbanEditPermanenceData.getSelectedKanbanHierarchy(), null);
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
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
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ツリーの親階層生成
     */
    private void createRoot(Long selectedId) {
        logger.debug("createRoot start.");
        try {

            kanbanEditPermanenceData.setKanbanHierarchyRootItem(new TreeItem<>(new KanbanHierarchyInfoEntity(0L, LocaleUtils.getString("key.Kanban"))));
            TreeItem<KanbanHierarchyInfoEntity> rootItem = kanbanEditPermanenceData.getKanbanHierarchyRootItem();

            Task task = new Task<List<KanbanHierarchyInfoEntity>>() {
                @Override
                protected List<KanbanHierarchyInfoEntity> call() throws Exception {
                    // 子組織の件数を取得する。
                    long count = kanbanHierarchyInfoFacade.getTopHierarchyCount();

                    List<KanbanHierarchyInfoEntity> hierarchyies = new ArrayList();
                    for (long from = 0; from <= count; from += RANGE) {
                        List<KanbanHierarchyInfoEntity> entities = kanbanHierarchyInfoFacade.getTopHierarchyRange(from, from + RANGE - 1);
                        hierarchyies.addAll(entities);
                    }
                    return hierarchyies;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        long count = this.getValue().size();
                        rootItem.getValue().setChildCount(count);

                        this.getValue().forEach((entity) -> {
                            TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);
                            if (entity.getChildCount() > 0) {
                                item.getChildren().add(new TreeItem());
                            }
                            item.expandedProperty().addListener(expandedListener);
                            rootItem.getChildren().add(item);
                        });

                        kanbanHierarchyTree.rootProperty().setValue(rootItem);

                        // 親ノードが閉じている場合、ノードを開状態にする。(開閉イベントは一旦削除して、開いた後で再登録)
                        if (!rootItem.isExpanded()) {
                            rootItem.expandedProperty().removeListener(expandedListener);
                            rootItem.setExpanded(true);
                            rootItem.expandedProperty().addListener(expandedListener);
                        }

                        if (Objects.nonNull(selectedId)) {
                            // 指定されたノードを選択状態にする。
                            selectedTreeItem(rootItem, selectedId);
                        } else {
                            // 選択ノードの指定がない場合は、ルートを選択状態にする。
                            kanbanHierarchyTree.getSelectionModel().select(rootItem);
                        }

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        selectKanbanHierarchyButton.setDisable(false);
                        selectKanbanHierarchyButtonForExport.setDisable(false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

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

    /**
     * ツリー展開
     *
     * @param parentItem 親階層
     */
    private void expand(TreeItem<KanbanHierarchyInfoEntity> parentItem, Long selectedId) {
        logger.debug("expand: parentItem={}", parentItem.getValue());
        try {

            parentItem.getChildren().clear();

            final long parentId = parentItem.getValue().getKanbanHierarchyId();

            Task task = new Task<List<KanbanHierarchyInfoEntity>>() {
                @Override
                protected List<KanbanHierarchyInfoEntity> call() {
                    // 子階層の件数を取得する。
                    long count = kanbanHierarchyInfoFacade.getAffilationHierarchyCount(parentId);

                    List<KanbanHierarchyInfoEntity> hierarchyies = new ArrayList();
                    for (long from = 0; from <= count; from += RANGE) {
                        List<KanbanHierarchyInfoEntity> entities = kanbanHierarchyInfoFacade.getAffilationHierarchyRange(parentId, from, from + RANGE - 1);
                        hierarchyies.addAll(entities);
                    }
                    return hierarchyies;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        this.getValue().forEach((entity) -> {
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
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * IDが一致するTreeItemを選択する。(存在しない場合は親を選択)
     *
     * @param parentItem 選択状態にするノーノの親ノード
     * @param selectedId 選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void selectedTreeItem(TreeItem<KanbanHierarchyInfoEntity> parentItem, Long selectedId) {
        if (Objects.isNull(parentItem)) {
            parentItem = this.kanbanHierarchyTree.getRoot();
        }

        Optional<TreeItem<KanbanHierarchyInfoEntity>> find = parentItem.getChildren().stream().
                filter(p -> Objects.nonNull(p.getValue())
                        && Objects.nonNull(p.getValue().getKanbanHierarchyId())
                        && p.getValue().getKanbanHierarchyId().equals(selectedId)).findFirst();

        if (find.isPresent()) {
            this.kanbanHierarchyTree.getSelectionModel().select(find.get());
        } else {
            this.kanbanHierarchyTree.getSelectionModel().select(parentItem);
        }
        this.kanbanHierarchyTree.scrollTo(this.kanbanHierarchyTree.getSelectionModel().getSelectedIndex());
    }

    /**
     * 文字列を日時に変換する。
     *
     * @param value 文字列
     * @return 日時
     */
    public static Optional<Date> stringToDateTime(String value) {
        if (StringUtils.isEmpty(value)) {
            return Optional.empty();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return Optional.of(sdf.parse(value));
        } catch (ParseException e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                return Optional.of(sdf.parse(value));
            } catch (ParseException ex) {
                return Optional.empty();
            }
        }
    }

    /**
     * dateからString型へ変換
     *
     * @param date 時間
     * @return 文字列
     */
    public static Optional<String> dateTimeToString(Date date) {
        if (Objects.isNull(date)) {
            return Optional.empty();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return Optional.of(sdf.format(date));
    }


    /**
     * 文字列が半角で何文字かカウントする
     *
     * @param targetStr 文字列
     * @return 半角文字数
     */
    private int countByteLength(String targetStr) {
        int count = 0;
        char[] chars = targetStr.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if ((c <= '\u007e') || // 英数字 
                    (c == '\u00a5') || // \記号
                    (c == '\u203e') || // ~記号
                    (c >= '\uff61' && c <= '\uff9f') // 半角カナ
            ) {
                count += 1;
            } else {
                count += 2;
            }
        }
        return count;
    }

}
