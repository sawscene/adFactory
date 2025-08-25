/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.controls.FromToPicker;
import adtekfuji.admanagerapp.productionnaviplugin.utils.*;
import adtekfuji.clientservice.*;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringTime;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import com.opencsv.CSVReader;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.entity.MessageEntity;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.ResultResponse;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanPropertyCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanStatusCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportProductCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportWorkKanbanCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportWorkKanbanPropertyCsv;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.importformat.*;
import jp.adtekfuji.adFactory.entity.job.KanbanProduct;
import jp.adtekfuji.adFactory.entity.kanban.KanbanCreateCondition;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.ProductInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.KanbanPropertyTemplateInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.workflowmodel.CellBase;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelEndCell;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelStartCell;
import jp.adtekfuji.javafxcommon.workflowmodel.StartCell;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkCell;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowModel;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowPane;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.util.CellReference;

/**
 * 生産管理ナビ 読み込み画面
 *
 * @author (TST)H.Nishimrua
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "WorkPlanImportCompo", fxmlPath = "/fxml/compo/work_plan_import_compo.fxml")
public class WorkPlanImportCompoController implements Initializable, ArgumentDelivery, ComponentHandler {

    Boolean isCutItemNameOver;

    // 定数
    private static final String CHARSET = "UTF-8";
    private static final String DELIMITER = "\\|";
    private static final Long RANGE = 20L;
    private final double POLLING_TIME_MIN = 3.0;
    private final String PREFIX_TMP = "tmp_";
    private final String SUFFIX_COMPLETED = ".completed";
    private final String SUFFIX_NONE = ".none";
    private final String SUFFIX_ERROR = ".error";
    private final String REGEX_PREFIX_TMP = "^" + PREFIX_TMP;
    private final int MAX_CHAR = 256;
    private final int MAX_PRODUCTION_NUM = 64;
    private final String ROW_NUM_COLUMN = "####ROWNUM####";
    private final String PROP_ENABLE_SHOW_IMPORT_BY_HEADER = "enableImportPlanByHeader";
    private final int TAKT_TIME_MAX_SECONDS = 1727999;

    // フィールド
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    private final KanbanHierarchyInfoFacade kanbanHierarchyFacade = new KanbanHierarchyInfoFacade();
    private final static WorkHierarchyInfoFacade workHierarchyInfoFacade = new WorkHierarchyInfoFacade();
    private final static WorkflowHierarchyInfoFacade workflowHierarchyInfoFacade = new WorkflowHierarchyInfoFacade();

    private final WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
    private final WorkInfoFacade workInfoFacade = new WorkInfoFacade();
    private final WorkKanbanInfoFacade workKanbanInfoFacade = new WorkKanbanInfoFacade();
    private final OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final List<String> kanbanCsvFilenames = new ArrayList<>();
    private List<String> kanbanHeaderCsvFilenames = new ArrayList<>();

    private final List<String> updateFilenames = new ArrayList<>();
    private final List<String> updateHeaderFilenames = new ArrayList<>();
    private final Map<String, String> importFileMap = new HashMap<>();

    private Object argument = null;
    private Double pollingTime;
    private Timeline timer = null;
    private boolean isImportProccessing = false;
    private boolean isAutoImportStandby = false;
    private boolean enableDbOutput;
    private ImportFormatInfo importFormatInfo = null;
    private ImportHeaderFormatInfo importHeaderFormatInfo = null;

    /**
     * タブ
     */
    @FXML
    private TabPane tabImportMode;
    @FXML
    private Tab tabHeaderMode;

    @FXML
    private Button autoImportButton;
    @FXML
    private TextField importCsvFileField;
    @FXML
    private TextField importExcelFileField;
    @FXML
    private TextField importHeaderFileField;
    @FXML
    private FromToPicker fromToPicker;
    @FXML
    private CheckBox ignoreCheck;
    @FXML
    private ListView resultList;
    @FXML
    private Pane progressPane;
    @FXML
    private ToolBar mainToolBar;
    @FXML
    private Button cancelButton;

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

            this.isCutItemNameOver = Boolean.valueOf(AdProperty.getProperties().getProperty("cutItemNameOver", "false"));
            this.enableDbOutput = Boolean.valueOf(AdProperty.getProperties().getProperty("enableDbOutput", "false"));

            this.importCsvFileField.setText(properties.getProperty(ProductionNaviPropertyConstants.SELECT_PROD_CSV_PATH, path));
            this.importExcelFileField.setText(properties.getProperty(ProductionNaviPropertyConstants.SELECT_PROD_XLS_PATH, path));
            this.importHeaderFileField.setText(properties.getProperty(ProductionNaviPropertyConstants.SELECT_PROD_HEADER_PATH, path));
            this.pollingTime = Double.valueOf(properties.getProperty(ProductionNaviPropertyConstants.PROD_AUTO_IMPORT_POLLING_TIME, ProductionNaviPropertyConstants.DEF_PROD_AUTO_IMPORT_POLLING_TIME));
            this.pollingTime = this.pollingTime < POLLING_TIME_MIN ? POLLING_TIME_MIN : this.pollingTime;
            this.fromToPicker.load(properties);
            this.ignoreCheck.setSelected(Boolean.valueOf(properties.getProperty(ProductionNaviPropertyConstants.SELECT_PROD_IGNORE_EXISTING, "false")));


            // 非表示設定
            Boolean isShowtabHeader = Boolean.valueOf(AdProperty.getProperties().getProperty(PROP_ENABLE_SHOW_IMPORT_BY_HEADER, "false"));
            if (!isShowtabHeader) {
                tabImportMode.getTabs().remove(tabHeaderMode);
            }

            // タブを復元する
            String value = properties.getProperty(ProductionNaviPropertyConstants.SELECT_PROD_TAB);
            if (!StringUtils.isEmpty(value)) {
                try {
                    int index = Integer.parseInt(value);
                    SingleSelectionModel<Tab> selectionModel = this.tabImportMode.getSelectionModel();
                    selectionModel.select(index);
                    if (ProductionNaviUtils.IMPORT_TAB_IDX_HEADER == index) {
                        this.setVisibleContent(false);
                    }
                } catch (Exception ex) {
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

            this.updateFilenames.add(this.importFormatInfo.getUpdateWorkKanbanPropFormatInfo().getCsvFileName());

            // ********************** ヘッダ形式 ***********************
            this.importHeaderFormatInfo = ImportHeaderFormatFileUtil.load();
            Set<String> kanbanHeaderCsvFilenamesSet = new TreeSet<>(Arrays.asList(
                    this.importHeaderFormatInfo.getKanbanHeaderFormatInfo().getHeaderCsvFileName()
            ));

//            this.importHeaderFormatInfo.getWorkKanbanPropHeaderFormats()
//                    .stream()
//                    .map(WorkKanbanPropHeaderFormatInfo::getCsvFileName)
//                    .forEach(kanbanHeaderCsvFilenamesSet::add);
            this.kanbanHeaderCsvFilenames = new ArrayList<>(kanbanHeaderCsvFilenamesSet);
//            this.updateHeaderFilenames.add(this.importHeaderFormatInfo.getUpdateWorkKanbanPropHeaderFormatInfo().getCsvFileName());

            //　タブ変更イベント
            tabImportMode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (Objects.equals(newValue.getId(), tabHeaderMode.getId())) {
                    this.setVisibleContent(false);
                } else {
                    this.setVisibleContent(true);
                }
            });

            this.timer = new Timeline(
                    new KeyFrame(Duration.ZERO, (ActionEvent ev) -> AutoImportStandby()),
                    new KeyFrame(Duration.seconds(this.pollingTime)));
            this.timer.setCycleCount(Timeline.INDEFINITE);

        } catch (IOException ex) {
            logger.error(ex, ex);
        }
        blockUI(false);
    }

    /**
     * 選択タブでコンテンツの表示を切り替え
     * 
     * @param isVisible 表示するか
     */
    private void setVisibleContent(boolean isVisible) {
        if (isVisible) {
            this.fromToPicker.setVisible(true);
            this.ignoreCheck.setVisible(true);
            this.autoImportButton.setVisible(true);
        } else {
            this.fromToPicker.setVisible(false);
            this.ignoreCheck.setVisible(false);
            this.autoImportButton.setVisible(false);
        }
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        this.argument = argument;
        ProductionNaviUtils.setFieldNormal(this.importCsvFileField);
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
     *
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
    private void onSelectCsvFileAction(ActionEvent event) {
        blockUI(true);
        try {
            DirectoryChooser dc = new DirectoryChooser();
            File fol = new File(importCsvFileField.getText());
            if (fol.exists() && fol.isDirectory()) {
                dc.setInitialDirectory(fol);
            }

            dc.setTitle("CSVフォルダ選択");
            File selectedFile = dc.showDialog(sc.getStage().getScene().getWindow());
            if (selectedFile != null) {
                this.importCsvFileField.setText(selectedFile.getPath());

                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_CSV));
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_CSV_PATH, selectedFile.getPath());
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * フォルダ選択（Excel)ボタンのクリックイベント
     *
     * @param event
     */
    @FXML
    private void onSelectExcelFileAction(ActionEvent event) {
        blockUI(true);
        try {
            FileChooser fc = new FileChooser();
            File fol = new File(importExcelFileField.getText());
            if (fol.exists()) {
                if (fol.isDirectory()) {
                    fc.setInitialDirectory(fol);
                } else if (fol.isFile()) {
                    if (fol.getParentFile() != null) {
                        fc.setInitialDirectory(fol.getParentFile());
                    }
                }
            }

            // ファイル選択
            fc.setTitle(LocaleUtils.getString("key.FileChoice"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    LocaleUtils.getString("key.import.excelFile"),
                    "*.xlsx", "*.xls", "*.xlsm"));

            File selectedFile = fc.showOpenDialog(sc.getStage().getScene().getWindow());
            if (selectedFile != null) {
                importExcelFileField.setText(selectedFile.getPath());

                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL));
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_XLS_PATH, selectedFile.getPath());
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * フォルダ選択（CSV形式（ヘッダ名指定））ボタンのクリックイベント
     *
     * @param event
     */
    @FXML
    private void onSelectHeaderFileAction(ActionEvent event) {
        blockUI(true);
        try {
            DirectoryChooser dc = new DirectoryChooser();
            File fol = new File(importHeaderFileField.getText());
            if (fol.exists() && fol.isDirectory()) {
                dc.setInitialDirectory(fol);
            }

            dc.setTitle("CSVフォルダ選択");
            File selectedFile = dc.showDialog(sc.getStage().getScene().getWindow());
            if (selectedFile != null) {
                this.importHeaderFileField.setText(selectedFile.getPath());

                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_HEADER));
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_HEADER_PATH, selectedFile.getPath());
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * フォーマット変更ボタン
     *
     * @param event イベント
     */
    @FXML
    private void onFormatChangeAction(ActionEvent event) {
        this.logger.info(":onFormatChangeAction start");

        if (tabImportMode.getSelectionModel().isSelected(ProductionNaviUtils.IMPORT_TAB_IDX_HEADER)) {
            sc.setComponent("ContentNaviPane", "WorkPlanHeaderFormatChangeCompo", argument);
        } else {
            sc.setComponent("ContentNaviPane", "WorkPlanFormatChangeCompo", argument);
        }

        this.logger.info(":onFormatChangeAction end");
    }

    /**
     * キャンセルボタン
     *
     * @param event イベント
     */
    @FXML
    private void onCancelAction(ActionEvent event) {
        logger.info(":onCancelAction start");
        sc.setComponent("ContentNaviPane", "WorkPlanChartCompo");
        logger.info(":onCancelAction end");
    }

    /**
     * インポートボタン Action
     *
     * @param event
     */
    @FXML
    private void onImportAction(ActionEvent event) {
        int tabMode;
        String fileName;
        boolean existsFile;
        boolean isCancel = false;

        try {
            blockUI(true);

            this.importFileMap.clear();
            this.resultList.getItems().clear();

            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

            String folder = this.importCsvFileField.getText();
            if (tabImportMode.getSelectionModel().isSelected(ProductionNaviUtils.IMPORT_TAB_IDX_CSV)) {
                // CSVインポート
                if (StringUtils.isEmpty(folder)) {
                    ProductionNaviUtils.setFieldError(this.importCsvFileField);
                    isCancel = true;
                    return;
                }

                File file = new File(folder);
                if (!file.exists() || !file.isDirectory()) {
                    ProductionNaviUtils.setFieldError(this.importCsvFileField);
                    addResult(String.format(" %s [%s]", LocaleUtils.getString("key.import.error.noFolder"), file));// 指定フォルダがない
                    isCancel = true;
                    return;
                }

                // カンバンのファイル名
                fileName = this.importFormatInfo.getKanbanFormatInfo().getCsvFileName();
                String filePath = folder + File.separator + fileName;
                file = new File(filePath);

                existsFile = file.exists() && file.isFile();
                if (!existsFile) {
                    fileName = this.findCsvFile(file);
                    if (!StringUtils.isEmpty(fileName)) {
                        existsFile = true;
                        this.importFileMap.put(this.importFormatInfo.getKanbanFormatInfo().getCsvFileName(), fileName);
                    }
                }

                // update_work_kanban_property.csvがあれば更新だけでも動かす
                final boolean existsUpdateFile = existsUpdateFile();
                if (!existsFile && !existsUpdateFile) {
                    ProductionNaviUtils.setFieldError(this.importCsvFileField);
                    addResult(String.format(" > %s [%s]", LocaleUtils.getString("key.ImportKanban_FileNothing"), filePath));// 指定フォルダにカンバン情報ファイルがない
                    isCancel = true;
                    return;
                }

                this.importFormatInfo.getKanbanFormatInfo().setFileName(fileName);

                // カンバンプロパティのファイル名
                fileName = this.importFormatInfo.getKanbanPropFormatInfo().getCsvFileName();
                if (this.importFileMap.containsKey(fileName)) {
                    fileName = this.importFileMap.get(fileName);
                } else {
                    filePath = folder + File.separator + fileName;
                    file = new File(filePath);

                    if (!(file.exists() && file.isFile())) {
                        fileName = this.findCsvFile(file);
                        if (!StringUtils.isEmpty(fileName)) {
                            this.importFileMap.put(this.importFormatInfo.getKanbanPropFormatInfo().getCsvFileName(), fileName);
                        }
                    }
                }

                this.importFormatInfo.getKanbanPropFormatInfo().setFileName(fileName);

                // 工程カンバンのファイル名
                fileName = this.importFormatInfo.getWorkKanbanFormatInfo().getCsvFileName();
                if (this.importFileMap.containsKey(fileName)) {
                    fileName = this.importFileMap.get(fileName);
                } else {
                    filePath = folder + File.separator + fileName;
                    file = new File(filePath);

                    if (!(file.exists() && file.isFile())) {
                        fileName = this.findCsvFile(file);
                        if (!StringUtils.isEmpty(fileName)) {
                            this.importFileMap.put(this.importFormatInfo.getWorkKanbanFormatInfo().getCsvFileName(), fileName);
                        }
                    }
                }

                this.importFormatInfo.getWorkKanbanFormatInfo().setFileName(fileName);

                // 工程カンバンプロパティのファイル名
                this.updateCsvFilename(folder, this.importFormatInfo.getWorkKanbanPropFormatInfo());

                for (WorkKanbanPropFormatInfo format : this.importFormatInfo.getWorkKanbanPropFormats()) {
                    this.updateCsvFilename(folder, format);
                }

                this.kanbanCsvFilenames.addAll(this.importFileMap.values());

                ProductionNaviUtils.setFieldNormal(this.importCsvFileField);

                tabMode = ProductionNaviUtils.IMPORT_TAB_IDX_CSV;
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_CSV));
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_CSV_PATH, folder);

            } else if (tabImportMode.getSelectionModel().isSelected(ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL)) {
                // Excelインポート
                fileName = this.importExcelFileField.getText();

                existsFile = ProductionNaviUtils.isFileCheck(ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL, fileName);

                if (!existsFile) {
                    ProductionNaviUtils.setFieldError(this.importExcelFileField);
                    // ファイルがありません。
                    addResult(String.format("%s [%s]", LocaleUtils.getString("key.import.error.noFile"), fileName));
                    isCancel = true;
                    return;
                }

                ProductionNaviUtils.setFieldNormal(this.importExcelFileField);

                tabMode = ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL;
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL));
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_XLS_PATH, fileName);
                folder = fileName;
            } else if (tabImportMode.getSelectionModel().isSelected(ProductionNaviUtils.IMPORT_TAB_IDX_HEADER)) {
                folder = this.importHeaderFileField.getText();
                // CSVインポート
                if (StringUtils.isEmpty(folder)) {
                    ProductionNaviUtils.setFieldError(this.importHeaderFileField);
                    isCancel = true;
                    return;
                }

                File file = new File(folder);
                if (!file.exists() || !file.isDirectory()) {
                    ProductionNaviUtils.setFieldError(this.importHeaderFileField);
                    isCancel = true;
                    return;
                }

                importKanbanTask(folder, this.importHeaderFormatInfo);

                // カンバンのファイル名
//                fileName = this.importHeaderFormatInfo.getKanbanHeaderFormatInfo().getFileName();
//                String filePath = folder + File.separator + fileName;
//                file = new File(filePath);
//
//                existsFile = file.exists() && file.isFile();
//                if (!existsFile) {
//                    fileName = this.findCsvFile(file);
//                    if (!StringUtils.isEmpty(fileName)) {
//                        existsFile = true;
//                        this.importFileMap.put(this.importFormatInfo.getKanbanFormatInfo().getCsvFileName(), fileName);
//                    }
//                }
                return;
            } else {
                logger.error(" Tab selected error.");
                isCancel = true;
                return;
            }

            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);

            // インポート
            this.importKanbanTask(existsFile, folder, tabMode, this.importFormatInfo);

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
     * インポート処理
     *
     * @param existsEssential 必須ファイルの有無
     * 存在しない場合(false)カンバンの読み込みは行われず工程カンバンプロパティの更新のみ行われる
     * @param folder フォルダ
     * @param tabMode Tabモード
     * @param importFormatInfo 設定情報
     */
    private void importKanbanTask(boolean existsEssential, String folder, int tabMode, ImportFormatInfo importFormatInfo) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                isImportProccessing = true;
                blockUI(true);

                addResult(String.format("%s [%s]", LocaleUtils.getString("key.ImportKanbanStart"), folder));// 生産計画取り込み開始
                // インポートファイルを「tmp_～」にリネームする。
                if (!renameImportFiles(folder, tabMode, true, null)) {
                    addResult(LocaleUtils.getString("key.ImportKanbanEnd"));// 生産計画取り込み終了
                    return null;// リネーム失敗時は終了する。
                }

                try {
                    // 必須ファイル(kanban.csv)が存在する時のみ実施する
                    if (existsEssential) {
                        Map<String, Integer> result = importKanban(folder, tabMode, importFormatInfo);
                        renameCompleted(tabMode, result, folder, kanbanCsvFilenames);
                    }

                    Map<String, Integer> result = importUpdateWorkKanbanProp(folder, tabMode, importFormatInfo);
                    renameCompleted(tabMode, result, folder, updateFilenames);

                } catch (Exception ex) {
                    logger.fatal(ex, ex);

                    renameFaild(tabMode, folder);

                } finally {
                    kanbanCsvFilenames.removeAll(importFileMap.values());
                    if (!importFileMap.values().isEmpty()) {
                        // ファイル削除
                        deleteFiles(folder, new ArrayList(importFileMap.values()));
                    }

                    addResult(LocaleUtils.getString("key.ImportKanbanEnd"));// 生産計画取り込み終了
                }

                if (isAutoImportStandby) {
                    timer.play();
                }

                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                blockUI(isAutoImportStandby);
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
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(isAutoImportStandby);
                    isImportProccessing = false;
                }
            }
        };
        new Thread(task).start();
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
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)));) {
                writer.print(writeStr);
            }

        } catch (IOException ex) {
            logger.fatal(ex, ex);
            file = null;
        }

        return file;
    }



    /**
     * インポート処理
     *
     * @param folder フォルダ
     * @param importHeaderFormatInfo 設定情報
     */
    private void importKanbanTask(String folder, ImportHeaderFormatInfo importHeaderFormatInfo) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                isImportProccessing = true;
                blockUI(true);

                addResult(String.format("%s [%s]", LocaleUtils.getString("key.ImportKanbanStart"), folder));// 生産計画取り込み開始

                // 工程情報＋工程プロパティ情報の取り込み
                Optional<Map<String, WorkInfoEntity>> optWorkIdMap = importWork(folder, importHeaderFormatInfo);
                if (!optWorkIdMap.isPresent()) {
                    addResult(String.format(" > %s", LocaleUtils.getString("key.ImportKanban_WorkRegistFailed")));
                    return null;
                }
                Map<String, WorkInfoEntity> workMap = optWorkIdMap.get();

                // for内で照合を行うための組織一覧を取得
                Map<String, Long> allOrganizationIdentify =
                        organizationInfoFacade.findAll()
                                .stream()
                                .collect(toMap(OrganizationInfoEntity::getOrganizationIdentify, OrganizationInfoEntity::getOrganizationId));

                Map<String, Long> allEquipmentIdentify =
                        equipmentInfoFacade.findAll()
                                .stream()
                                .collect(toMap(EquipmentInfoEntity::getEquipmentIdentify, EquipmentInfoEntity::getEquipmentId));

                // 工程順情報＋工程順プロパティ情報の取り込み
                boolean importWorkflowSuccess = importWorkflow(folder, importHeaderFormatInfo, workMap, allOrganizationIdentify, allEquipmentIdentify);
                if (!importWorkflowSuccess) {
                    addResult(String.format(" > %s", LocaleUtils.getString("key.ImportKanban_WorkflowRegistFailed")));
                    return null;
                }

                // カンバン＋カンバンプロパティ+工程カンバン+工程カンバンプロパティ情報の取り込み
                boolean importKanbanSuccess =  importKanban(folder, importHeaderFormatInfo, allOrganizationIdentify, allEquipmentIdentify);
                if (!importKanbanSuccess) {
                    addResult(String.format(" > %s", LocaleUtils.getString("key.ImportKanban_RegistFailed")));
                    return null;
                }

                addResult(LocaleUtils.getString("key.ImportKanbanEnd"));// 生産計画取り込み終了

                if (isAutoImportStandby) {
                    timer.play();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                blockUI(isAutoImportStandby);
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
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(isAutoImportStandby);
                    isImportProccessing = false;
                }
            }
        };
        new Thread(task).start();
    }

    /**
     * 工程カンバンプロパティ更新ファイルをインポートし適用する
     *
     * @param folder
     * @param tabMode
     * @param importFormatInfo
     * @return
     * @throws Exception
     */
    private Map<String, Integer> importUpdateWorkKanbanProp(String folder, int tabMode, ImportFormatInfo importFormatInfo) throws Exception {
        // 現状アップデートファイルはcsvで固定
        if (!Objects.equals(tabMode, ProductionNaviUtils.IMPORT_TAB_IDX_CSV)) {
            return null;
        }

        // 名前のみ設定ファイルから取得し、それ以外の設定については工程カンバンプロパティ情報と同じものを取得する
        final WorkKanbanPropFormatInfo updateWorkKanbanPropFormatInfo = importFormatInfo.getUpdateWorkKanbanPropFormatInfo();
        final WorkKanbanPropFormatInfo workKanbanPropFormatInfo = importFormatInfo.getWorkKanbanPropFormatInfo();
        final String filename = updateWorkKanbanPropFormatInfo.getCsvFileName();
        final String workKanbanPropPath = folder + File.separator + PREFIX_TMP + filename;
        final List<ImportWorkKanbanPropertyCsv> importWkKanbanProps = readWorkKanbanPropertyCsv(workKanbanPropPath, workKanbanPropFormatInfo);

        if (Objects.isNull(importWkKanbanProps) || importWkKanbanProps.isEmpty()) {
            return null;
        }

        // 擬似的なカンバン情報のリストを作成
        final Set<String> importKanbanNames = importWkKanbanProps.stream()
                .map(workKanbanProp -> workKanbanProp.getKanbanName())
                .collect(Collectors.toSet());

        final boolean dbConnect = Boolean.parseBoolean(AdProperty.getProperties().getProperty("dbConnect", "false"));
        final DbConnector con = DbConnector.getInstance();

        final Map<String, OrganizationInfoEntity> organizationMap = new HashMap();
        final Map<String, EquipmentInfoEntity> equipmentMap = new HashMap();

        int procNum = 0;
        int skipKanbanNum = 0;
        int successNum = 0;
        int failedNum = 0;

        for (String kanbanName : importKanbanNames) {
            procNum++;

            if (StringUtils.isEmpty(kanbanName)) {
                skipKanbanNum++;
                continue;
            }

            // カンバン名をもとにカンバン取得
            boolean exsitKanban = false;
            List<KanbanInfoEntity> kanbans = null;

            if (dbConnect) {
                exsitKanban = con.exsitKanban(kanbanName);
            } else {
                KanbanSearchCondition condition = new KanbanSearchCondition().kanbanName(kanbanName);
                kanbans = kanbanInfoFacade.findSearch(condition);
                exsitKanban = !kanbans.isEmpty();
            }

            if (!exsitKanban) {
                skipKanbanNum++;
                continue;
            }

            KanbanInfoEntity kanban = kanbanInfoFacade.findName(kanbanName);

            List<WorkKanbanInfoEntity> workKanbans = new ArrayList<>();
            Long workkanbanCnt = workKanbanInfoFacade.countFlow(kanban.getKanbanId());
            for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                workKanbans.addAll(workKanbanInfoFacade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
            }
            kanban.setWorkKanbanCollection(workKanbans);

            List<WorkKanbanInfoEntity> sepWorkKanbans = new ArrayList<>();
            Long separateCnt = workKanbanInfoFacade.countSeparate(kanban.getKanbanId());
            for (long nowCnt = 0; nowCnt < separateCnt; nowCnt += RANGE) {
                sepWorkKanbans.addAll(workKanbanInfoFacade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
            }
            kanban.setSeparateworkKanbanCollection(sepWorkKanbans);

            // 登録済のカンバンの場合は、カンバンステータスを一旦「計画中(Planning)」に戻す。
            KanbanStatusEnum kanbanStatus;
            if (Objects.nonNull(kanban.getKanbanId())) {
                kanbanStatus = kanban.getKanbanStatus();

                // カンバンステータスが「中止(Suspend)」，「その他(Other)」，「完了(Completion)」の場合は更新しない
                //      ※．KanbanStatusEnumは、「中止(Suspend)」が INTERRUPT で、「一時中断(Interrupt)」が SUSPEND なので注意。
                if (kanbanStatus.equals(KanbanStatusEnum.INTERRUPT)
                        || kanbanStatus.equals(KanbanStatusEnum.OTHER)
                        || kanbanStatus.equals(KanbanStatusEnum.COMPLETION)) {
                    // 更新できないカンバンのためスキップ
                    skipKanbanNum++;
                    //addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_NotUpdateKanban"), LocaleUtils.getString(kanbanStatus.getResourceKey())));
                    continue;
                }

                if (!kanbanStatus.equals(KanbanStatusEnum.PLANNING)) {
                    kanban.setKanbanStatus(KanbanStatusEnum.PLANNING);
                    ResponseEntity updateStatusRes = kanbanInfoFacade.update(kanban);
                    if (Objects.isNull(updateStatusRes) || !updateStatusRes.isSuccess()) {
                        // 更新できないカンバンのためスキップ (ステータス変更できない状態だった)
                        skipKanbanNum++;
                        //addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_NotUpdateKanban"), LocaleUtils.getString(kanbanStatus.getResourceKey())));
                        continue;
                    }

                    // 排他用バージョンを取得しなおす。
                    KanbanInfoEntity newKanban = kanbanInfoFacade.find(kanban.getKanbanId());
                    kanban.setVerInfo(newKanban.getVerInfo());
                }
            } else {
                kanbanStatus = KanbanStatusEnum.PLANNED;
            }

            kanban.setKanbanName(kanbanName);

            this.updateWorkKanbanProperty(kanban, kanbanName, importWkKanbanProps, procNum);

            // 更新日時
            Date updateDateTime = DateUtils.toDate(LocalDateTime.now());
            kanban.setUpdateDatetime(updateDateTime);

            // 更新者
            kanban.setFkUpdatePersonId(loginUserInfoEntity.getId());

            // カンバン更新
            ResponseEntity updateRes = kanbanInfoFacade.update(kanban);
            if (Objects.nonNull(updateRes) && updateRes.isSuccess()) {
                // 排他用バージョンを取得しなおす。
                KanbanInfoEntity newKanban = kanbanInfoFacade.find(kanban.getKanbanId());
                kanban.setVerInfo(newKanban.getVerInfo());

                // カンバンステータスを元の状態に戻す
                kanban.setKanbanStatus(kanbanStatus);
                kanbanInfoFacade.update(kanban);
                successNum++;
            } else {
                failedNum++;
                addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_UpdateFailed")));
            }
        }

        if (dbConnect) {
            con.closeDB();
        }

        addResult(String.format("%s: %s (%s: %s, %s: %s, %s: %s)",
                LocaleUtils.getString("key.ImportKanban_ProccessNum"), procNum,
                LocaleUtils.getString("key.ImportKanban_SuccessNum"), successNum,
                LocaleUtils.getString("key.ImportKanban_SkipNum"), skipKanbanNum,
                LocaleUtils.getString("key.ImportKanban_FailedNum"), failedNum));

        Map<String, Integer> ret = new HashMap<>();
        ret.put("procNum", procNum);
        ret.put("successNum", successNum);
        ret.put("skipKanbanNum", skipKanbanNum);
        ret.put("failedNum", failedNum);

        return ret;
    }

    /**
     * 工程カンバンプロパティを更新する
     *
     * @param kanban 更新対象のカンバン。このカンバンの中身を書き換えるため注意。
     * @param kanbanName
     * @param importWkKanbanProps
     */
    private void updateWorkKanbanProperty(KanbanInfoEntity kanban, String kanbanName, List<ImportWorkKanbanPropertyCsv> importWkKanbanProps, int kanbanLine) {

        // 工程カンバンプロパティ
        List<WorkKanbanInfoEntity> wKanList = kanban.getWorkKanbanCollection();
        List<ImportWorkKanbanPropertyCsv> csvList = importWkKanbanProps.stream().filter(
                p -> kanbanName.equals(p.getKanbanName())).collect(Collectors.toList());

        csvList.stream().forEach((wkKanbanProp) -> {
            List<WorkKanbanInfoEntity> workKanbans = findWorkKanban(wKanList, wkKanbanProp);
            if (workKanbans.isEmpty()) {
                // 該当する工程カンバンがなければスキップ
                addResult(String.format("[%d] > [%s] [%s]", kanbanLine, LocaleUtils.getString("key.alert.notfound.workkanbanError"), wkKanbanProp.getWorkNum()));
                return;
            }

            String propName = wkKanbanProp.getWkKanbanPropName();

            if (!StringUtils.isEmpty(propName)) {

                // プロパティ型
                CustomPropertyTypeEnum propType = CustomPropertyTypeEnum.toEnum(wkKanbanProp.getWkKanbanPropType());
                if(null==propType) {
                    addResult(String.format("[%d] > [%s] [%s] [%s]", kanbanLine, LocaleUtils.getString("key.import.production.work.kanba.property"), LocaleUtils.getString("key.type"), wkKanbanProp.getWkKanbanPropType()));
                }

                for (WorkKanbanInfoEntity workKanban : workKanbans) {
                    Optional<WorkKanbanPropertyInfoEntity> propOpt = workKanban.getPropertyCollection().stream().filter(p -> propName.equals(p.getWorkKanbanPropName())).findFirst();
                    if (propOpt.isPresent()) {
                        // 工程カンバンプロパティを上書き
                        WorkKanbanPropertyInfoEntity wkKanbanPropEntity = propOpt.get();
                        if (Objects.nonNull(propType)) {
                            wkKanbanPropEntity.setWorkKanbanPropType(propType);
                        }
                        wkKanbanPropEntity.setWorkKanbanPropValue(wkKanbanProp.getWkKanbanPropValue());

                    } else if (Objects.nonNull(propType)) {
                        // 工程カンバンプロパティを追加
                        WorkKanbanPropertyInfoEntity wkKanbanPropEntity = new WorkKanbanPropertyInfoEntity();
                        wkKanbanPropEntity.setWorkKanbanPropName(propName);
                        wkKanbanPropEntity.setWorkKanbanPropType(propType);
                        wkKanbanPropEntity.setWorkKanbanPropValue(wkKanbanProp.getWkKanbanPropValue());

                        int propOrder = 1;
                        Optional<WorkKanbanPropertyInfoEntity> lastProp = workKanban.getPropertyCollection().stream().max(Comparator.comparingInt(p -> p.getWorkKanbanPropOrder()));
                        if (lastProp.isPresent()) {
                            propOrder = lastProp.get().getWorkKanbanPropOrder() + 1;
                        }
                        wkKanbanPropEntity.setWorkKanbanPropOrder(propOrder);

                        workKanban.getPropertyCollection().add(wkKanbanPropEntity);
                    }
                }
            }
        });
    }

    /**
     * ファイル名を変更する。
     *
     * @param folder フォルダパス
     * @param fileNames ベースファイル名一覧
     * @param isAddTmp TMP追加？ (true: TMP追加 false:TMP削除)
     * @param suffix 結果文字列 (null以外の場合、指定された文字列をファイル名の末尾に追加する)
     * @return 結果
     */
    private boolean renameTempFiles(String folder, List<String> fileNames, boolean isAddTmp, String suffix) {
        boolean result = true;
        for (String fileName : fileNames) {
            if (StringUtils.isEmpty(fileName)) {
                continue;
            }
            if (!this.renameTempFile(folder, fileName, isAddTmp, suffix)) {
                result = false;
            }
        }
        return result;
    }

    /**
     * ファイル名を変更する。
     *
     * @param folder フォルダパス
     * @param fileName ベースファイル名
     * @param isAddTmp TMP追加？ (true: TMP追加 false:TMP削除)
     * @param suffix 結果文字列 (null以外の場合、指定された文字列をファイル名の末尾に追加する)
     * @return 結果
     */
    private boolean renameTempFile(String folder, String fileName, boolean isAddTmp, String suffix) {
        File file;
        String toFileName;
        if (isAddTmp) {
            file = Paths.get(folder, fileName).toFile();
            toFileName = PREFIX_TMP + fileName;
        } else {
            file = Paths.get(folder, PREFIX_TMP + fileName).toFile();
            toFileName = fileName;
        }

        if (!file.exists() || !file.isFile()) {
            // ファイルが存在しない場合は成功扱いにする。
            return true;
        }

        if (Objects.nonNull(suffix)) {
            toFileName = toFileName.replaceFirst(REGEX_PREFIX_TMP, "") + suffix;
        }

        File toFile = Paths.get(folder, toFileName).toFile();
        if (toFile.exists()) {
            toFile.delete();
        }

        if (file.renameTo(toFile)) {
            return true;
        } else {
            // ファイルが開かれている可能性があります。ファイルを閉じてから再実行してください。
            addResult(String.format("  > %s [%s]", LocaleUtils.getString("key.import.error.rename"), file.getName()));
            return false;
        }
    }

    /**
     * カンバンインポート
     *
     * @param folder フォルダ
     * @param tabMode Tabモード
     * @param importFormatInfo 設定情報
     * @throws Exception
     */
    private Map<String, Integer> importKanban(String folder, int tabMode, ImportFormatInfo importFormatInfo) throws Exception {
        KanbanFormatInfo kanbanFormatInfo = importFormatInfo.getKanbanFormatInfo();
        KanbanPropFormatInfo kanbanPropFormatInfo = importFormatInfo.getKanbanPropFormatInfo();
        WorkKanbanFormatInfo workKanbanFormatInfo = importFormatInfo.getWorkKanbanFormatInfo();
        WorkKanbanPropFormatInfo workKanbanPropFormatInfo = importFormatInfo.getWorkKanbanPropFormatInfo();
        KanbanStatusFormatInfo kanbanStatusFormatInfo = importFormatInfo.getKanbanStatusFormatInfo();
        ProductFormatInfo productFormatInfo = importFormatInfo.getProductFormatInfo();

        List<ImportKanbanCsv> importKanbans = new ArrayList();
        List<ImportKanbanPropertyCsv> importKanbanProps = new ArrayList();
        List<ImportWorkKanbanCsv> importWorkKanbans = new ArrayList();
        List<ImportWorkKanbanPropertyCsv> importWkKanbanProps = new ArrayList();
        List<ImportKanbanStatusCsv> importKanbanStatuss = new ArrayList();
        List<ImportProductCsv> importProduct = new ArrayList();

        int line = 0;
        switch (tabMode) {
            case ProductionNaviUtils.IMPORT_TAB_IDX_CSV:
                // カンバン情報 読込
                String filename = kanbanFormatInfo.getFileName();
                String kanbanPath = folder + File.separator + PREFIX_TMP + filename;
                addResult(LocaleUtils.getString("key.import.work.plan.kanban") + " [" + filename + "]");
                importKanbans = readKanbanCsv(kanbanPath, kanbanFormatInfo);
                if (Objects.isNull(importKanbans)) {
                    // ファイルが読み込めませんでした。
                    addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readFile"), filename));
                    return null;
                } else if (importKanbans.isEmpty()) {
                    // データがありません。
                    addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), filename));
                    return null;
                }
                line = StringUtils.parseInteger(kanbanFormatInfo.getCsvStartRow());

                // カンバンプロパティ情報 読込
                filename = kanbanPropFormatInfo.getFileName();
                if (!StringUtils.isEmpty(filename)) {
                    addResult(LocaleUtils.getString("key.import.work.plan.kanban.property"));

                    String kanbanPropPath = folder + File.separator + PREFIX_TMP + filename;
                    importKanbanProps = readKanbanPropertyCsv(kanbanPropPath, kanbanPropFormatInfo);
                    if (Objects.isNull(importKanbanProps)) {
                        // ファイルが読み込めませんでした。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readFile"), filename));
                        importKanbanProps = new ArrayList();
                    } else if (importKanbanProps.isEmpty()) {
                        // データがありません。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), filename));
                    } else {
                        int kanbanProcLine = StringUtils.parseInteger(kanbanPropFormatInfo.getCsvStartRow());
                        // データチェック
                        importKanbanProps = this.checkImportKanbanProps(importKanbans, importKanbanProps, kanbanProcLine);
                        if (importKanbanProps.isEmpty()) {
                            addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), filename));
                        }
                    }
                }

                // 工程カンバン情報 読込
                filename = workKanbanFormatInfo.getFileName();
                if (!StringUtils.isEmpty(filename)) {
                    addResult(LocaleUtils.getString("key.import.work.plan.work.kanban") + " [" + filename + "]");

                    String workKanbanPath = folder + File.separator + PREFIX_TMP + filename;
                    importWorkKanbans = readWorkKanbanCsv(workKanbanPath, workKanbanFormatInfo);
                    if (Objects.isNull(importWorkKanbans)) {
                        // ファイルが読み込めませんでした。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readFile"), filename));
                        importWorkKanbans = new ArrayList();
                    } else if (importWorkKanbans.isEmpty()) {
                        // データがありません。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), filename));
                    }
                }

                // 工程カンバンプロパティ情報 読込
                importWkKanbanProps = this.importWorkKanbanPropCsv(folder, workKanbanPropFormatInfo);

                for (WorkKanbanPropFormatInfo format : this.importFormatInfo.getWorkKanbanPropFormats()) {
                    importWkKanbanProps.addAll(this.importWorkKanbanPropCsv(folder, format));
                }

                // カンバンステータス情報 読込
                filename = kanbanStatusFormatInfo.getCsvFileName();
                if (!StringUtils.isEmpty(filename)) {
                    addResult(LocaleUtils.getString("key.import.work.plan.kanban.status") + " [" + filename + "]");

                    String kanbanStatusPath = folder + File.separator + PREFIX_TMP + filename;
                    importKanbanStatuss = readKanbanStatusCsv(kanbanStatusPath, kanbanStatusFormatInfo);
                    if (Objects.isNull(importKanbanStatuss)) {
                        // ファイルが読み込めませんでした。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readFile"), filename));
                        importKanbanStatuss = new ArrayList();
                    } else if (importKanbanStatuss.isEmpty()) {
                        // データがありません。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), filename));
                    }
                }

                // 製品情報 読込
                filename = productFormatInfo.getCsvFileName();
                if (!StringUtils.isEmpty(filename)) {
                    addResult(LocaleUtils.getString("key.import.work.plan.product") + " [" + filename + "]");

                    String productPath = folder + File.separator + PREFIX_TMP + filename;
                    importProduct = readProductCsv(productPath, productFormatInfo);
                    if (Objects.isNull(importProduct)) {
                        // ファイルが読み込めませんでした。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readFile"), filename));
                        importProduct = new ArrayList();
                    } else if (importProduct.isEmpty()) {
                        // データがありません。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), filename));
                    }
                }

                break;
            case ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL:
                File xlsFile = new File(folder);
                String xlsPath = xlsFile.getParent() + File.separator + PREFIX_TMP + xlsFile.getName();
                // カンバン情報
                addResult(LocaleUtils.getString("key.import.work.plan.kanban"));
                String sheetName = kanbanFormatInfo.getXlsSheetName();
                importKanbans = this.getExcelKanban(xlsPath, kanbanFormatInfo);
                if (Objects.isNull(importKanbans)) {
                    // Excelファイルのシートが読み込めませんでした。
                    addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readSheet"), sheetName));
                    return null;
                } else if (importKanbans.isEmpty()) {
                    // データがありません。
                    addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), sheetName));
                    return null;
                }
                line = StringUtils.parseInteger(kanbanFormatInfo.getXlsStartRow());

                // カンバンプロパティ情報
                sheetName = kanbanPropFormatInfo.getXlsSheetName();
                if (!StringUtils.isEmpty(sheetName)) {
                    addResult(LocaleUtils.getString("key.import.work.plan.kanban.property"));

                    importKanbanProps = this.getExcelKanbanProps(xlsPath, kanbanPropFormatInfo);
                    if (Objects.isNull(importKanbanProps)) {
                        // Excelファイルのシートが読み込めませんでした。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readSheet"), sheetName));
                        importKanbanProps = new ArrayList();
                    } else if (importKanbanProps.isEmpty()) {
                        // データがありません。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), sheetName));
                    } else {
                        int kanbanProcLine = StringUtils.parseInteger(kanbanPropFormatInfo.getXlsStartRow());
                        // データチェック
                        importKanbanProps = this.checkImportKanbanProps(importKanbans, importKanbanProps, kanbanProcLine);
                        if (importKanbanProps.isEmpty()) {
                            addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), sheetName));
                        }
                    }
                }

                // 工程カンバン情報
                sheetName = workKanbanFormatInfo.getXlsSheetName();
                if (!StringUtils.isEmpty(sheetName)) {
                    addResult(LocaleUtils.getString("key.import.work.plan.work.kanban"));

                    importWorkKanbans = this.getExcelWorkKanbans(xlsPath, workKanbanFormatInfo);
                    if (Objects.isNull(importWorkKanbans)) {
                        // Excelファイルのシートが読み込めませんでした。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readSheet"), sheetName));
                        importWorkKanbans = new ArrayList();
                    } else if (importWorkKanbans.isEmpty()) {
                        // データがありません。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), sheetName));
                    }
                }

                // 工程カンバンプロパティ情報
                sheetName = workKanbanPropFormatInfo.getXlsSheetName();
                if (!StringUtils.isEmpty(sheetName)) {
                    addResult(LocaleUtils.getString("key.import.work.plan.work.kanban.property"));

                    importWkKanbanProps = this.getExcelWorkKanbanProperty(xlsPath, workKanbanPropFormatInfo);
                    if (Objects.isNull(importWkKanbanProps)) {
                        // Excelファイルのシートが読み込めませんでした。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readSheet"), sheetName));
                        importWkKanbanProps = new ArrayList();
                    } else if (importWkKanbanProps.isEmpty()) {
                        // データがありません。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), sheetName));
                    }
                }

                // カンバンステータス情報
                sheetName = kanbanStatusFormatInfo.getXlsSheetName();
                if (!StringUtils.isEmpty(sheetName)) {
                    addResult(LocaleUtils.getString("key.import.work.plan.kanban.status"));

                    importKanbanStatuss = this.getExcelKanbanStatus(xlsPath, kanbanStatusFormatInfo);
                    if (Objects.isNull(importKanbanStatuss)) {
                        // Excelファイルのシートが読み込めませんでした。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readSheet"), sheetName));
                        importKanbanStatuss = new ArrayList();
                    } else if (importKanbanStatuss.isEmpty()) {
                        // データがありません。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), sheetName));
                    }
                }

                // Excelファイルから製品情報を読み込む。
                sheetName = productFormatInfo.getXlsSheetName();
                if (!StringUtils.isEmpty(sheetName)) {
                    addResult(LocaleUtils.getString("key.import.work.plan.product"));

                    importProduct = this.getExcelProduct(xlsPath, productFormatInfo);
                    if (Objects.isNull(importProduct)) {
                        // Excelファイルのシートが読み込めませんでした。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readSheet"), sheetName));
                        importProduct = new ArrayList();
                    } else if (importProduct.isEmpty()) {
                        // データがありません。
                        addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), sheetName));
                    }
                }

                break;
        }

        int procNum = 0;
        int skipKanbanNum = 0;
        int successNum = 0;
        int failedNum = 0;

        Map<String, KanbanHierarchyInfoEntity> kanbanHierarchyMap = new HashMap();
        Map<String, WorkflowInfoEntity> workflowMap = new HashMap();
        Map<String, OrganizationInfoEntity> organizationMap = new HashMap();
        Map<String, EquipmentInfoEntity> equipmentMap = new HashMap();

        Map<String, KanbanStatusEnum> statusMap = importKanbanStatuss.stream().collect(Collectors.toMap(ImportKanbanStatusCsv::getKanbanName, i->KanbanStatusEnum.getEnum(i.getKanbanStatus())));

        boolean dbConnect = Boolean.parseBoolean(AdProperty.getProperties().getProperty("dbConnect", "false"));
        DbConnector con = DbConnector.getInstance();

        String targetPropName = LocaleUtils.getString("key.InstructionNumber");// 指示数

        // カンバン追加
        addResult(LocaleUtils.getString("key.ImportKanban_CreateKanban"));

        boolean errFlg = false;
        int kanbanLine = (procNum + line - 1);
        for (ImportKanbanCsv importKanban : importKanbans) {
            logger.debug("Import the kanban: " + importKanban);
            kanbanLine++;

            procNum++;
            errFlg = false;
            //addResult(LocaleUtils.getString("key.import.production.kanba") + ":" + (procNum + line - 1) + "行目");

            String kanbanName = importKanban.getKanbanName();
            if (StringUtils.isEmpty(kanbanName)) {
                skipKanbanNum++;
                // 必須項目が空のためスキップ (カンバン情報: カンバン名)
                addResult(String.format("[%d] > %s [%s: %s]", kanbanLine, LocaleUtils.getString("key.import.skip.noData"), LocaleUtils.getString("key.import.production.kanba"), LocaleUtils.getString("key.KanbanName")));
                continue;
            }

            // カンバン階層
            KanbanHierarchyInfoEntity kanbanHierarchy = null;
            Long parentId = 0L;
            if (!StringUtils.isEmpty(importKanban.getKanbanHierarchyName())) {
                String seprator = AdProperty.getProperties().getProperty("hierarchySeprator", "[/|\\\\]");

                List<String> hierarchyNames
                        = Arrays.stream(importKanban
                                .getKanbanHierarchyName()
                                .split(seprator))
                        .filter(StringUtils::nonEmpty)
                        .collect(toList());

                for (String hierarchyName : hierarchyNames) {
                    kanbanHierarchy = kanbanHierarchyMap.get(hierarchyName);
                    if (Objects.isNull(kanbanHierarchy)) {
                        kanbanHierarchy = kanbanHierarchyFacade.findHierarchyName(URLEncoder.encode(hierarchyName, CHARSET));
                        kanbanHierarchyMap.put(hierarchyName, kanbanHierarchy);
                    }

                    Long kanbanHierarchyId = kanbanHierarchy.getKanbanHierarchyId();
                    if (Objects.isNull(kanbanHierarchyId)) {
                        // ルート階層に子階層を作成
                        kanbanHierarchy = new KanbanHierarchyInfoEntity();
                        kanbanHierarchy.setHierarchyName(hierarchyName);
                        kanbanHierarchy.setParentId(parentId);

                        ResponseEntity response = kanbanHierarchyFacade.regist(kanbanHierarchy);
                        if (response.isSuccess() && response.getUriId() != 0) {
                            kanbanHierarchyId = response.getUriId();
                            kanbanHierarchy.setKanbanHierarchyId(kanbanHierarchyId);
                            kanbanHierarchyMap.put(hierarchyName, kanbanHierarchy);
                        } else {
                            errFlg = true;
                            // 「存在しないカンバン階層、またはアクセス権がないためスキップ」
                            addResult(String.format("[%d] > %s [%s: %s]", kanbanLine, LocaleUtils.getString("key.ImportKanban_HierarchyNothing"), importKanban.getKanbanHierarchyName(), response.getErrorType().name()));
                        }
                    } else if (parentId != 0L && !Objects.equals(kanbanHierarchy.getParentId(), parentId)){
                        kanbanHierarchy.setParentId(parentId);
                        ResponseEntity response = kanbanHierarchyFacade.update(kanbanHierarchy);
                        if (response.isSuccess()) {
                            kanbanHierarchyId = response.getUriId();
                            kanbanHierarchyMap.put(hierarchyName, kanbanHierarchy);
                        } else {
                            errFlg = true;
                            // 「存在しないカンバン階層、またはアクセス権がないためスキップ」
                            addResult(String.format("[%d] > %s [%s: %s]", kanbanLine, LocaleUtils.getString("key.ImportKanban_HierarchyNothing"), importKanban.getKanbanHierarchyName(), response.getErrorType().name()));
                        }
                    }
                    parentId = kanbanHierarchy.getKanbanHierarchyId();
                }

            }

            if (parentId == 0L){
                errFlg = true;
                // 必須項目が空のためスキップ (カンバン情報: カンバン階層)
                addResult(String.format("[%d] > %s [%s: %s]", kanbanLine, LocaleUtils.getString("key.import.skip.noData"), LocaleUtils.getString("key.import.production.kanba"), LocaleUtils.getString("key.KanbanHierarch")));
            }

            // 工程順の確認
            WorkflowInfoEntity workflow = null;
            Long workflowId = null;
            if (!StringUtils.isEmpty(importKanban.getWorkflowName())) {
                String workflowNameAndRev = Objects.nonNull(importKanban.getWorkflowRev()) ? importKanban.getWorkflowName() + " : " + importKanban.getWorkflowRev() : importKanban.getWorkflowName();
                if (workflowMap.containsKey(workflowNameAndRev)) {
                    workflow = workflowMap.get(workflowNameAndRev);
                } else {
                    String workflowName;
                    if (importKanban.getEnableConcat()) {
                        workflowName = importKanban.getModelName() + importKanban.getWorkflowName();
                    } else {
                        workflowName = importKanban.getWorkflowName();
                    }
                    workflow = findWorkflow(workflowName, importKanban.getWorkflowRev());
                    // モデル名と工程順名を結合させる場合は見つからないときに工程順名だけで再検索を行う
                    if (importKanban.getEnableConcat() && (Objects.isNull(workflow) || Objects.isNull(workflow.getWorkflowId()))) {
                        workflow = findWorkflow(importKanban.getWorkflowName(), importKanban.getWorkflowRev());
                    }
                    workflowMap.put(workflowNameAndRev, workflow);
                }
                workflowId = workflow.getWorkflowId();
                if (Objects.isNull(workflowId)) {
                    errFlg = true;
                    // 存在しない工程順のためスキップ
                    addResult(String.format("[%d] > %s [%s]", kanbanLine, LocaleUtils.getString("key.ImportKanban_WorkflowNothing"), workflowNameAndRev));
                }

                if (Objects.isNull(workflow.getConWorkflowWorkInfoCollection()) || workflow.getConWorkflowWorkInfoCollection().isEmpty()) {
                    errFlg = true;
                    // 工程順に工程が未登録のためスキップ
                    addResult(String.format("[%d] > %s [%s]", kanbanLine, LocaleUtils.getString("key.import.skip.WorkNotRegistered"), workflowNameAndRev));
                }
            } else {
                errFlg = true;
                // 必須項目が空のためスキップ (カンバン情報: 工程順)
                addResult(String.format("[%d] > %s [%s: %s]", kanbanLine, LocaleUtils.getString("key.import.skip.noData"), LocaleUtils.getString("key.import.production.kanba"), LocaleUtils.getString("key.WorkflowName")));
            }

            // 開始予定日時
            Date startDatetime = null;
            if (!StringUtils.isEmpty(importKanban.getStartDatetime())) {
                startDatetime = ImportFormatFileUtil.stringToDateTime(importKanban.getStartDatetime());
                if (Objects.isNull(startDatetime)) {
                    errFlg = true;
                    addResult(String.format("[%d] > %s [%s: %s]", kanbanLine, LocaleUtils.getString("key.error.format.datetime"), LocaleUtils.getString("key.import.production.kanba"), LocaleUtils.getString("key.import.read.plans.start.datetime")));
                }
            }
            
            // 標準サイクルタイム
            Integer cycleTime = null;
            if (!StringUtils.isEmpty(importKanban.getCycleTime())) {
                cycleTime = (int) Math.ceil(Double.parseDouble(importKanban.getCycleTime()));
            }

            if (errFlg) {
                skipKanbanNum++;
                continue;
            }

            // カンバン作成
            KanbanInfoEntity kanban;
            KanbanSearchCondition condition = new KanbanSearchCondition()
                    .kanbanName(kanbanName)
                    .workflowId(workflowId);

            boolean exsitKanban;
            List<KanbanInfoEntity> kanbans = null;

            if (dbConnect) {
                exsitKanban = con.exsitKanban(kanbanName);
            } else {
                kanbans = kanbanInfoFacade.findSearch(condition);
                exsitKanban = !kanbans.isEmpty();
            }

            if (!exsitKanban) {
                kanban = new KanbanInfoEntity();
                if (Objects.isNull(startDatetime)) {
                    startDatetime = new Date();
                }
            } else {
                if (dbConnect) {
                    if (this.ignoreCheck.isSelected()) {
                        skipKanbanNum++;
                        continue;
                    }

                    kanbans = kanbanInfoFacade.findSearch(condition);
                }

                Optional<KanbanInfoEntity> findKanban = kanbans.stream().filter(k -> kanbanName.equals(k.getKanbanName())).findFirst();
                if (findKanban.isPresent()) {

                    if (ignoreCheck.isSelected()) {
                        skipKanbanNum++;
                        continue;
                    }

                    kanban = findKanban.get();

                    List<WorkKanbanInfoEntity> workKanbans = new ArrayList<>();
                    Long workkanbanCnt = workKanbanInfoFacade.countFlow(kanban.getKanbanId());
                    for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                        workKanbans.addAll(workKanbanInfoFacade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
                    }
                    kanban.setWorkKanbanCollection(workKanbans);

                    List<WorkKanbanInfoEntity> sepWorkKanbans = new ArrayList<>();
                    Long separateCnt = workKanbanInfoFacade.countSeparate(kanban.getKanbanId());
                    for (long nowCnt = 0; nowCnt < separateCnt; nowCnt += RANGE) {
                        sepWorkKanbans.addAll(workKanbanInfoFacade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
                    }
                    kanban.setSeparateworkKanbanCollection(sepWorkKanbans);
                } else {
                    kanban = new KanbanInfoEntity();
                    exsitKanban = false;
                }
            }

            // 新規登録の場合は計画済みにする
            final KanbanStatusEnum kanbanStatus = Objects.nonNull(kanban.getKanbanId())
                    ? kanban.getKanbanStatus()
                    : KanbanStatusEnum.PLANNED;

            /////////////////////////////
            // カンバンステータスが「中止(Suspend)」，「その他(Other)」，「完了(Completion)」の場合は更新しない
            //      ※．KanbanStatusEnumは、「中止(Suspend)」が INTERRUPT で、「一時中断(Interrupt)」が SUSPEND なので注意。
            if (!kanbanStatus.isKanbanUpdatableStatus) {
                // 更新できないカンバンのためスキップ
                ++skipKanbanNum;
                addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_NotUpdateKanban"), LocaleUtils.getString(kanbanStatus.getResourceKey())));
                continue;
            }

            if (Objects.nonNull(kanban.getKanbanId())) {
                ResponseEntity updateStatusRes = kanbanInfoFacade.updateStatus(Arrays.asList(kanban.getKanbanId()), KanbanStatusEnum.INTERRUPT, loginUserInfoEntity.getId());
                if (Objects.isNull(updateStatusRes) || !updateStatusRes.isSuccess()) {
                    // 更新できないカンバンのためスキップ (ステータス変更できない状態だった)
                    ++skipKanbanNum;
                    continue;
                }
            }

            // 排他用バージョンを取得しなおす。
            KanbanInfoEntity newKanban = Objects.nonNull(kanban.getKanbanId())
                    ?kanbanInfoFacade.find(kanban.getKanbanId())
                    : new KanbanInfoEntity();
            kanban.setVerInfo(newKanban.getVerInfo());
            kanban.setKanbanStatus(newKanban.getKanbanStatus());

            // 開始予定日時
            if (Objects.nonNull(startDatetime)) {
                kanban.setStartDatetime(startDatetime);
            }

            if(!exsitKanban) {
                // カンバンの新規追加
                kanban.setParentId(parentId); // 親階層
                kanban.setKanbanName(kanbanName);      // カンバン名
                kanban.setFkWorkflowId(workflowId);    // 工程順

                // モデル名
                if (StringUtils.isEmpty(importKanban.getModelName())) {
                    // カンバン情報にモデル名が設定さていない場合、工程順のモデル名を設定する
                    kanban.setModelName(workflow.getModelName());
                } else {
                    kanban.setModelName(importKanban.getModelName());
                }

                 // 生産タイプ
                kanban.setProductionType(Integer.parseInt(importKanban.getProductionType()));
            }

            if (!StringUtils.isEmpty(importKanban.getLotQuantity()) && StringUtils.isInteger(importKanban.getLotQuantity())) {
                kanban.setLotQuantity(Integer.parseInt(importKanban.getLotQuantity()));
            } else {
                kanban.setLotQuantity(1);
            }

            // カンバンを新規追加
            if (Objects.isNull(kanban.getKanbanId())) {

                ResponseEntity createRes = registKanban(kanban);
                if (Objects.nonNull(createRes) && createRes.isSuccess()) {
                    // 追加成功
                    this.addResult(String.format("[%d] > %s [%s]", kanbanLine, LocaleUtils.getString("key.ImportKanban_RegistSuccess"), kanban.getKanbanName()));
                } else {
                    // 追加失敗
                    failedNum++;
                    this.addResult(String.format("[%d] > %s [%s]", kanbanLine, LocaleUtils.getString("key.ImportKanban_RegistFailed"), kanban.getKanbanName()));
                    continue;
                }
                kanban = kanbanInfoFacade.findURI(createRes.getUri());

            } else if (Objects.nonNull(kanban.getStartDatetime())) {
                List<BreakTimeInfoEntity> breakTimes = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanban.getWorkKanbanCollection());
                List<HolidayInfoEntity> holidays = WorkKanbanTimeReplaceUtils.getHolidays(kanban.getStartDatetime());
                WorkPlanWorkflowProcess workflowProcess = new WorkPlanWorkflowProcess(workflow);
                workflowProcess.setBaseTime(kanban, breakTimes, kanban.getStartDatetime(), holidays);
            }

            kanban.setParentId(parentId);

            // モデル名
            if(!StringUtils.isEmpty(importKanban.getModelName())) {
                kanban.setModelName(importKanban.getModelName());
            }

            // 製造番号
            if(!StringUtils.isEmpty(importKanban.getProductionNumber())) {
                kanban.setProductionNumber(importKanban.getProductionNumber());
            }
            
            // シリアル番号
            List<String> serials = this.generateSerialNumbers(importKanban.getStartSerial(), importKanban.getEndSerial());
            if (Objects.nonNull(serials)) {
                List<KanbanProduct> kanbanProducts = IntStream.range(0, serials.size())
                    .mapToObj(index -> {
                        KanbanProduct kanbanProduct = new KanbanProduct();
                        kanbanProduct.setUid(serials.get(index));
                        kanbanProduct.setDefect(null);
                        kanbanProduct.setOrderNumber(index + 1);
                        kanbanProduct.setStatus(KanbanStatusEnum.PLANNED);
                        kanbanProduct.setImplement(false);
                        return kanbanProduct;
                    })
                    .collect(Collectors.toList());

                ServiceInfoEntity serviceInfo = new ServiceInfoEntity();
                serviceInfo.setService(ServiceInfoEntity.SERVICE_INFO_PRODUCT);
                serviceInfo.setJob(kanbanProducts);

                kanban.setServiceInfo(JsonUtils.objectsToJson(Arrays.asList(serviceInfo))); // サービス情報 (JSON)
                kanban.setLotQuantity(kanbanProducts.size());
            }

            // 標準サイクルタイム
            kanban.setCycleTime(cycleTime);

            // 工程カンバンをオーダー順にソートする。
            kanban.getWorkKanbanCollection().sort(Comparator.comparing(p -> p.getWorkKanbanOrder()));

            // カンバンプロパティ
            List<ImportKanbanPropertyCsv> props = importKanbanProps.stream().filter(p -> StringUtils.equals(kanbanName, p.getKanbanName())).collect(Collectors.toList());
            for (ImportKanbanPropertyCsv prop : props) {
                String propName = prop.getKanbanPropertyName();
                String workflowName = prop.getWorkflowName();
                if (!StringUtils.isEmpty(workflowName)
                        && !StringUtils.equals(workflowName, kanban.getWorkflowName())) {
                    // カンバンプロパティ情報に工程順名が読み込まれている かつ カンバン情報の工程順名と一致しない
                    continue;
                }

                // プロパティ型
                final CustomPropertyTypeEnum propType = CustomPropertyTypeEnum.toEnum(prop.getKanbanPropertyType());
                if (Objects.isNull(propType)) {
                    addResult(String.format("[%d] > [%s] [%s] [%s]", kanbanLine, LocaleUtils.getString("key.import.production.kanba.property"), LocaleUtils.getString("key.type"), prop.getKanbanPropertyType()));
                }

                // 更新対象のカンバンプロパティ
                Optional<KanbanPropertyInfoEntity> findKanbanProp = kanban.getPropertyCollection().stream().filter(p -> propName.equals(p.getKanbanPropertyName())).findFirst();
                if (findKanbanProp.isPresent()) {
                    KanbanPropertyInfoEntity kanbanProp = findKanbanProp.get();

                    // プロパティ型
                    if (Objects.nonNull(propType)) {
                        kanbanProp.setKanbanPropertyType(propType);
                    }

                    // プロパティ値
                    if (!StringUtils.isEmpty(prop.getKanbanPropertyValue())) {
                        kanbanProp.setKanbanPropertyValue(prop.getKanbanPropertyValue());
                    }
                } else if (Objects.nonNull(propType)) {
                    // 存在しないプロパティの場合は、プロパティ名とプロパティ値が入っていたら追加する。
                    KanbanPropertyInfoEntity kanbanProp = new KanbanPropertyInfoEntity();
                    kanbanProp.setKanbanPropertyName(propName);
                    kanbanProp.setKanbanPropertyType(propType);

                    // プロパティ値
                    if (!StringUtils.isEmpty(prop.getKanbanPropertyValue())) {
                        kanbanProp.setKanbanPropertyValue(prop.getKanbanPropertyValue());
                    }

                    // プロパティ表示順の最大値から、次に追加するプロパティの表示順を設定
                    int propOrder = 1;
                    Optional<KanbanPropertyInfoEntity> lastProp = kanban.getPropertyCollection().stream().max(Comparator.comparingInt(p -> p.getKanbanPropertyOrder()));
                    if (lastProp.isPresent()) {
                        propOrder = lastProp.get().getKanbanPropertyOrder() + 1;
                    }
                    kanbanProp.setKanbanPropertyOrder(propOrder);

                    kanban.getPropertyCollection().add(kanbanProp);
                }
            }

            // 製品情報をインポートする。
            List<ImportProductCsv> products = importProduct.stream().filter(p -> kanbanName.equals(p.getKanbanName())).collect(Collectors.toList());
            Integer order = 1;
            // 更新対象の製品情報
            if (Objects.isNull(kanban.getProducts())) {
                kanban.setProducts(new ArrayList());
            }
            for (ImportProductCsv product : products) {
                // ユニークID
                String uniqueId = product.getUniqueId();
                if (StringUtils.isEmpty(uniqueId)) {
                    // 必須項目が空のためスキップ (製品情報: 製品シリアル)
                    addResult(String.format("[%d] > %s [%s: %s]", kanbanLine, LocaleUtils.getString("key.import.skip.noData"), LocaleUtils.getString("key.ProductInformation"), LocaleUtils.getString("key.Product.UniqueID")));
                    continue;
                }

                Optional<ProductInfoEntity> findProduct
                        = kanban.getProducts().stream().filter(p -> uniqueId.equals(p.getUniqueId())).findFirst();
                if (findProduct.isPresent()) {
                    // 存在する場合、オーダーを更新する。
                    findProduct.get().setOrderNum(order++);
                } else if (Objects.nonNull(uniqueId)) {
                    // 存在しない場合は追加する。
                    ProductInfoEntity productEntity = new ProductInfoEntity();
                    productEntity.setUniqueId(uniqueId);// ユニークID
                    productEntity.setOrderNum(order++);

                    kanban.getProducts().add(productEntity);
                }
            }

            // ロット数量
            if (kanban.getProductionType() != 1) {
                if (!kanban.getProducts().isEmpty()) {
                    kanban.setLotQuantity(kanban.getProducts().size());
                }
            }

            // 2020/2/20 MES連携 上位連携フラグをカンバンプロパティに追加する
            if (this.enableDbOutput) {
                String upLinkString = "上位連携フラグ";
                Optional<KanbanPropertyInfoEntity> opt = kanban.getPropertyCollection().stream()
                        .filter(p -> p.getKanbanPropertyName().equals(upLinkString)).findFirst();

                KanbanPropertyInfoEntity kanbanProp;
                if (opt.isPresent()) {
                    kanbanProp = opt.get();
                } else {
                    kanbanProp = new KanbanPropertyInfoEntity();

                    kanbanProp.setKanbanPropertyName(upLinkString);
                    kanbanProp.setKanbanPropertyType(CustomPropertyTypeEnum.TYPE_BOOLEAN);
                    kanban.getPropertyCollection().add(kanbanProp);
                }
                kanbanProp.setKanbanPropertyValue(Boolean.TRUE.toString());

                // プロパティ表示順は最後尾にする
                int propOrder = 1;
                Optional<KanbanPropertyInfoEntity> lastProp = kanban.getPropertyCollection().stream()
                        .filter(p -> !p.getKanbanPropertyName().equals(upLinkString))
                        .max(Comparator.comparingInt(p -> p.getKanbanPropertyOrder()));
                if (lastProp.isPresent()) {
                    propOrder = lastProp.get().getKanbanPropertyOrder() + 1;
                }
                kanbanProp.setKanbanPropertyOrder(propOrder);
            }

            // 製品情報がある場合、カンバンプロパティの最後に指示数を追加する。
            if (!kanban.getProducts().isEmpty()) {

                Optional<KanbanPropertyInfoEntity> opt = kanban.getPropertyCollection().stream()
                        .filter(p -> p.getKanbanPropertyName().equals(targetPropName)).findFirst();

                KanbanPropertyInfoEntity kanbanProp;
                if (opt.isPresent()) {
                    kanbanProp = opt.get();
                } else {
                    kanbanProp = new KanbanPropertyInfoEntity();

                    kanbanProp.setKanbanPropertyName(targetPropName);
                    kanbanProp.setKanbanPropertyType(CustomPropertyTypeEnum.TYPE_INTEGER);

                    kanban.getPropertyCollection().add(kanbanProp);
                }

                kanbanProp.setKanbanPropertyValue(String.valueOf(kanban.getProducts().size()));

                // 指示数のプロパティ表示順が最後になるようにする。
                int propOrder = 1;
                Optional<KanbanPropertyInfoEntity> lastProp = kanban.getPropertyCollection().stream()
                        .filter(p -> !p.getKanbanPropertyName().equals(targetPropName))
                        .max(Comparator.comparingInt(p -> p.getKanbanPropertyOrder()));
                if (lastProp.isPresent()) {
                    propOrder = lastProp.get().getKanbanPropertyOrder() + 1;
                }
                kanbanProp.setKanbanPropertyOrder(propOrder);
            }

            // 更新日時・更新者
            kanban.setUpdateDatetime(DateUtils.toDate(LocalDateTime.now()));
            kanban.setFkUpdatePersonId(loginUserInfoEntity.getId());

            // 工程カンバンの更新
            updateWorkKanban(kanban, kanbanName, importWorkKanbans, organizationMap, equipmentMap, kanbanLine);
            
            // 工程カンバンプロパティの更新
            updateWorkKanbanProperty(kanban, kanbanName, importWkKanbanProps, kanbanLine);

            // カンバン更新
            ResponseEntity updateRes = kanbanInfoFacade.update(kanban);

            boolean isSuccess = false;
            if (Objects.nonNull(updateRes) && updateRes.isSuccess()) {
                // verInfoが更新されているため、カンバン情報を再取得する。
                kanban = kanbanInfoFacade.find(kanban.getKanbanId());

                // kanban_status.csvの値を反映する.
                kanban.setKanbanStatus(statusMap.getOrDefault(kanbanName, kanbanStatus));
                kanban.setParentId(parentId);

                // カンバンステータスを更新する。
                updateRes = kanbanInfoFacade.update(kanban);
                if (Objects.nonNull(updateRes) && updateRes.isSuccess()) {
                    isSuccess = true;
                }
            }

            if (isSuccess) {
                successNum++;
            } else {
                // 更新失敗
                failedNum++;
                addResult(String.format("[%d] > %s", kanbanLine, LocaleUtils.getString("key.ImportKanban_UpdateFailed")));
            }
        }

        if (dbConnect) {
            con.closeDB();
        }

        addResult(String.format("%s: %s (%s: %s, %s: %s, %s: %s)",
                LocaleUtils.getString("key.ImportKanban_ProccessNum"), procNum,
                LocaleUtils.getString("key.ImportKanban_SuccessNum"), successNum,
                LocaleUtils.getString("key.ImportKanban_SkipNum"), skipKanbanNum,
                LocaleUtils.getString("key.ImportKanban_FailedNum"), failedNum));

        Map<String, Integer> ret = new HashMap<>();
        ret.put("procNum", procNum);
        ret.put("successNum", successNum);
        ret.put("skipKanbanNum", skipKanbanNum);
        ret.put("failedNum", failedNum);

        return ret;
    }


    private Integer integerValueOf(String val) {
        try {
            return Integer.valueOf(val);
        } catch (Exception ex) {
            logger.error(ex, ex);
        }
        return null;
    }

    /**
     * シリアル番号を生成する。
     * 
     * @param startSerial 開始シリアル番号
     * @param endSerial 終了シリアル番号
     * @return シリアル番号一覧
     */
    private List<String> generateSerialNumbers(String startSerial, String endSerial) {
        
        if (StringUtils.isEmpty(startSerial) && StringUtils.isEmpty(endSerial)) {
            return null;
        }
        
        if (StringUtils.isEmpty(startSerial)) {
            startSerial = endSerial;
        } else if (StringUtils.isEmpty(endSerial)) {
            endSerial = startSerial;
        }

        if (startSerial.equals(endSerial)) {
            return Arrays.asList(startSerial);
        }
        
        // 文字列を比較し異なる部分の開始位置
        int index = org.apache.commons.lang3.StringUtils.indexOfDifference(startSerial, endSerial);

        String prefix = startSerial.substring(0, index);
        String start = startSerial.substring(index).trim();
        String end = endSerial.substring(index).trim();

        String pattern = "[+-]?\\d+";
        if (!start.matches(pattern) || !end.matches(pattern)) {
            // 数字以外の場合は中止
            return null;
        }
        
        List<String> serialNumbers = new ArrayList<>();
        String length = String.valueOf(end.length());
        int startNum = Integer.parseInt(start);
        int endNum = Integer.parseInt(end);

        for (; startNum <= endNum; startNum++) {
            serialNumbers.add(prefix + String.format ("%0" + length + "d", startNum));
        }

        return serialNumbers;
    }
    
    /**
     * CSVファイルの読み込み。
     *
     * @param folder フォルダパス
     * @param fileName ファイル名
     * @param encode エンコード
     * @param headerRow ヘッダ行
     * @param startRow 読み込み開始行
     */
    private Optional<List<Map<String, String>>> readCsvFile(String folder, String fileName, String encode, int headerRow, int startRow) {
        String fullFileName = folder + File.separator + fileName;
        if (Files.notExists(Paths.get(fullFileName))) {
            addResult(String.format(" > %s [%s]", LocaleUtils.getString("key.import.error.noFile"), fileName));
            return Optional.empty();
        }

        try (FileInputStream fi = new FileInputStream(fullFileName);
            InputStreamReader isr = new InputStreamReader(fi, this.encodeUpperCase(encode));
            CSVReader csvReader = new CSVReader(isr)) {
            int line = 1;
            String[] header;
            while ((header = csvReader.readNext()) != null) {
                if (line >= headerRow) {
                    break;
                }
                ++line;
            }

            if (Objects.isNull(header)) {
                addResult(String.format(" > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), fileName));
                return Optional.empty();
            }

            for (; line < startRow - 1; ++line) {
                if (Objects.isNull(csvReader.readNext())) {
                    addResult(String.format(" > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), fileName));
                    return Optional.empty();
                }
            }

            List<Map<String, String>> csvDataList = new ArrayList<>();
            String[] data;
            int rowNum = startRow;
            while ((data = csvReader.readNext()) != null) {
                Map<String, String> csvMap = new HashMap<>();
                if (data.length != header.length) {
                    continue;
                }

                for (int n = 0; n < data.length; ++n) {
                    csvMap.put(header[n], data[n]);
                }
                // 行番号を追加
                csvMap.put(ROW_NUM_COLUMN, String.valueOf(rowNum));
                csvDataList.add(csvMap);
                ++rowNum;
            }

            if (csvDataList.isEmpty()) {
                addResult(String.format(" > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), fileName));
                return Optional.empty();
            }

            return Optional.of(csvDataList);

        } catch (Exception ex) {
            logger.error(ex, ex);
            addResult(String.format(" > %s [%s]", LocaleUtils.getString("key.import.error.readFile"), fileName));
            return Optional.empty();
        }
    }

    /**
     * 関数型インターフェース作成
     *
     * @param headerList ヘッダリスト
     * @param biConsumer 関数型インターフェース
     * @return BiConsumer データ取得関数
     */
    private<T> BiConsumer<Map<String, String>, T> createConsumer(final List<String> headerList, final String delimiter, final String defVal, BiConsumer<T, String> biConsumer)
    {
        final String delimiterVal = Objects.isNull(delimiter) ? "" : delimiter;
        final List<String> impl = headerList.stream().filter(StringUtils::nonEmpty).collect(Collectors.toList());
        if (impl.isEmpty()) {
            return createDefaultConsumer(defVal, biConsumer);
        }

        return (csvMap, entity) -> {
            final String val
                    = impl
                    .stream()
                    .map(csvMap::get)
                    .filter(StringUtils::nonEmpty)
                    .collect(Collectors.joining(delimiterVal));
            if (StringUtils.isEmpty(val)) {
                biConsumer.accept(entity, defVal);
            } else {
                biConsumer.accept(entity, val);
            }
        };
    }

    /**
     * 関数型インターフェース作成
     *
     * @param header ヘッダ
     * @param biConsumer 関数型インターフェース
     * @return BiConsumer
     */
    private static<T> BiConsumer<Map<String, String>, T> createConsumer(final String header, final String defVal, BiConsumer<T, String> biConsumer)
    {
        if (StringUtils.isEmpty(header)) {
            return (csvMap, entity) -> {};
        }
        return (csvMap, entity) -> biConsumer.accept(entity, csvMap.getOrDefault(header, defVal));
    }

    /**
     * 関数型インターフェース作成
     *
     * @param biConsumer 関数型インターフェース
     * @return BiConsumer
     */
    private static<T> BiConsumer<Map<String, String>, T> createConsumer(BiConsumer<T, Map<String, String>> biConsumer)
    {
        return (csvMap, entity) -> biConsumer.accept(entity, csvMap);
    }

    /**
     * 関数型インターフェース作成
     *
     * @param val 値
     * @param biConsumer 関数型インターフェース
     * @return BiConsumer
     */
    private static<T, V> BiConsumer<Map<String, String>, T> createDefaultConsumer(V val, BiConsumer<T, V> biConsumer)
    {
        return (csvMap, entity) -> biConsumer.accept(entity, val);
    }

    static Optional<Integer> toInteger(String val) {
        if (StringUtils.isEmpty(val)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.valueOf(val));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    static Optional<Date> toDate(final String val, SimpleDateFormat sf) {
        try {
            return Optional.of(sf.parse(val));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    static final SimpleDateFormat defaultDateFormat  = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    static Optional<Date> toDate(final String val) {
        if (StringUtils.isEmpty(val)) {
            return Optional.empty();
        }

        List<SimpleDateFormat> sfLists
                = Arrays.asList(
                defaultDateFormat,
                new SimpleDateFormat("yyyy/MM/dd HH:mm")
        );

        return sfLists
                .stream()
                .map(sf -> toDate(val, sf))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }


    /**
     * 工程csvファイル読込
     *
     * @param folder フォルダ
     * @param workHeaderFormatInfo 設定情報
     */
    private Optional<Map<String, WorkInfoEntity>> loadWorkCsvFile(String folder, WorkHeaderFormatInfo workHeaderFormatInfo) {

        if (Objects.isNull(workHeaderFormatInfo)) {
            return Optional.empty();
        }

        // エンコード
        final String csvEncode = workHeaderFormatInfo.getHeaderCsvFileEncode();
        if (StringUtils.isEmpty(csvEncode)) {
            return Optional.empty();
        }
        // ファイル名
        final String csvFileName = workHeaderFormatInfo.getHeaderCsvFileName();
        if (StringUtils.isEmpty(csvFileName)) {
            return Optional.of(new HashMap<>());
        }
        // ヘッダ行
        final Integer headerRow = integerValueOf(workHeaderFormatInfo.getHeaderCsvHeaderRow());
        if (Objects.isNull(headerRow)) {
            return Optional.empty();
        }
        // 読込開始行数
        final Integer startRow = integerValueOf(workHeaderFormatInfo.getHeaderCsvStartRow());
        if (Objects.isNull(startRow)) {
            return Optional.empty();
        }
        if (headerRow >= startRow) {
            return Optional.empty();
        }

        List<String> headerList = new ArrayList<>();

        // 工程階層
        final List<String> hierarchyNameHeader
                = workHeaderFormatInfo.getHeaderCsvProcessHierarchyNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (hierarchyNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(hierarchyNameHeader);

        // 工程名
        final List<String> processNameHeader
                = workHeaderFormatInfo
                .getHeaderCsvProcessNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (processNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(processNameHeader);

        // タクトタイム
        final String tactTimeHeader = workHeaderFormatInfo.getHeaderCsvTactTime();
        headerList.add(tactTimeHeader);

        // 単位
        final String tactTimeUnit = workHeaderFormatInfo.getHeaderCsvTactTimeUnit();
        final int timeUnit = StringUtils.equals(LocaleUtils.getString("key.time.second"), tactTimeUnit) ? 1000 : 60000;

        // 作業内容
        final List<String> workContentHeader
                = Stream.of(
                        workHeaderFormatInfo.getHeaderCsvWorkContent1(),
                        workHeaderFormatInfo.getHeaderCsvWorkContent2(),
                        workHeaderFormatInfo.getHeaderCsvWorkContent3())
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        headerList.addAll(workContentHeader);

        addResult(LocaleUtils.getString("key.import.work.plan.work") + " [" + csvFileName + "]");
        // csvファイル読込
        final Optional<List<Map<String, String>>> optWorkCsvData = readCsvFile(folder, csvFileName, csvEncode, headerRow, startRow);
        if (!optWorkCsvData.isPresent()) {
            return Optional.empty();
        }
        final List<Map<String, String>> workCsvData = optWorkCsvData.get();
        if (workCsvData.isEmpty()) {
            return Optional.of(new HashMap<>());
        }

        final Map<String, String> firstWorkCsvData = workCsvData.get(0);
        final List<String> checkIncludeHeader
                = headerList
                .stream()
                .filter(StringUtils::nonEmpty)
                .filter(header -> !firstWorkCsvData.containsKey(header))
                .collect(toList());

        if (!checkIncludeHeader.isEmpty()) {
            checkIncludeHeader
                    .forEach(header -> {
                        this.addResult(String.format(LocaleUtils.getString("key.NotFoundHeaderColumn"), LocaleUtils.getString("key.import.production.work"), header));
                    });
            return Optional.empty();
        }


        List<String> warningMessage = new ArrayList<>();
        List<String> errorMessage = new ArrayList<>();
        final Date now = new Date();
        final List<BiConsumer<Map<String, String>, WorkInfoEntity>> consumers
                = Arrays.asList
                (       // 工程階層名
                        createConsumer(hierarchyNameHeader, workHeaderFormatInfo.getHeaderCsvHierarchyDelimiter(), "", WorkInfoEntity::setParentName),
                        // 工程名
                        createConsumer(processNameHeader, workHeaderFormatInfo.getHeaderCsvProcessDelimiter(), "", WorkInfoEntity::setWorkName),
                        // タクトタイム
                        createConsumer((workInfoEntity, item) -> {
                            Optional<Integer> optTactTime = toInteger(item.getOrDefault(tactTimeHeader, "0"));
                            if (optTactTime.isPresent()) {
                                workInfoEntity.setTaktTime(timeUnit * optTactTime.get());
                            } else {
                                // タクトタイムに数値以外が入力されている場合
                                errorMessage.add(String.format("[%s] > %s",
                                        item.get(ROW_NUM_COLUMN),
                                        LocaleUtils.getString("key.ImportKanban_TactTimeIsNotNum")));
                            }
                        }),
                        // 作業内容
                        createConsumer((workInfoEntity, item) ->
                                {
                                    // 最初に見つかった内容を取込む
                                    workContentHeader
                                            .stream()
                                            .map(item::get)
                                            .filter(StringUtils::nonEmpty)
                                            .findFirst()
                                            .ifPresent(workInfoEntity::setContent);
                                }),
                        // コンテンツタイプ
                        createDefaultConsumer(ContentTypeEnum.STRING, WorkInfoEntity::setContentType),
                        // ユーザ名
                        createDefaultConsumer(loginUserInfoEntity.getId(), WorkInfoEntity::setUpdatePersonId),
                        // 更新日
                        createDefaultConsumer(now, WorkInfoEntity::setUpdateDatetime)
                );

        final List<WorkInfoEntity> workInfoEntities
                = workCsvData
                .stream()
                .map(item -> {
                    WorkInfoEntity workInfoEntity = new WorkInfoEntity();
                    consumers.forEach(consumer -> consumer.accept(item, workInfoEntity));
                    return workInfoEntity;
                })
                .collect(toList());

        int line = startRow;
        for (WorkInfoEntity workInfoEntity : workInfoEntities) {
            // 工程階層名のチェック
            if (StringUtils.isEmpty(workInfoEntity.getParentName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.ProcessHierarch")));
            } else {
                if (this.countByteLength(workInfoEntity.getParentName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.ProcessHierarch")));
                        workInfoEntity.setParentName(this.cutOver(workInfoEntity.getParentName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.ProcessHierarch")));
                    }
                }
            }
            // 工程名のチェック
            if (StringUtils.isEmpty(workInfoEntity.getWorkName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.ProcessName")));
            } else {
                if (this.countByteLength(workInfoEntity.getWorkName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.ProcessName")));
                        workInfoEntity.setWorkName(this.cutOver(workInfoEntity.getWorkName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.ProcessName")));
                    }
                }
            }
            ++line;
        }

        warningMessage.forEach(this::addResult);

        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        // 重複削除(工程名)
        Map<String, Map<String, List<WorkInfoEntity>>> workGroupList =
                workInfoEntities
                        .stream()
                        .collect(
                                Collectors.groupingBy(WorkInfoEntity::getWorkName,
                                        Collectors.groupingBy(WorkInfoEntity::getParentName,
                                                toList())));

        Map<String, WorkInfoEntity> workElementsList = new HashMap<>();
        for (Map.Entry<String, Map<String, List<WorkInfoEntity>>> entry : workGroupList.entrySet()) {
            List<List<WorkInfoEntity>> items = new ArrayList<>(entry.getValue().values());
            if (items.size() != 1) {
                errorMessage.add(String.format(" > %s [%s: %s]",
                        LocaleUtils.getString("key.ImportKanban_DuplicationDataDif"),
                        LocaleUtils.getString("key.ProcessName"), items.get(0).get(0).getWorkName()));
                continue;
            }
            workElementsList.put(entry.getKey(), items.get(0).get(0));
        }

        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        return Optional.of(workElementsList);
    }

    /**
     * 工程プロパティcsvファイル読込
     *
     * @param folder フォルダ
     * @param workPropHeaderFormatInfo 設定情報
     */
    private Optional<Map<String, List<WorkPropertyInfoEntity>>> loadWorkPropCsvFile(String folder, WorkPropHeaderFormatInfo workPropHeaderFormatInfo) {
        if (Objects.isNull(workPropHeaderFormatInfo)) {
            return Optional.empty();
        }

        // エンコード
        final String csvEncode = workPropHeaderFormatInfo.getHeaderCsvFileEncode();
        if (StringUtils.isEmpty(csvEncode)) {
            return Optional.empty();
        }
        // ファイル名
        final String csvFileName = workPropHeaderFormatInfo.getHeaderCsvFileName();
        if (StringUtils.isEmpty(csvFileName)) {
            return Optional.of(new HashMap<>());
        }
        // ヘッダ行
        final Integer headerRow = integerValueOf(workPropHeaderFormatInfo.getHeaderCsvHeaderRow());
        if (Objects.isNull(headerRow)) {
            return Optional.empty();
        }
        // 読込開始行数
        final Integer startRow = integerValueOf(workPropHeaderFormatInfo.getHeaderCsvStartRow());
        if (Objects.isNull(startRow)) {
            return Optional.empty();
        }
        if (headerRow >= startRow) {
            return Optional.empty();
        }

        List<String> headerList = new ArrayList<>();
        // 工程名
        final List<String> workNameHeader
                = workPropHeaderFormatInfo.getHeaderCsvProcessNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (workNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(workNameHeader);

        // プロパティ
        final List<PropHeaderFormatInfo> propValueHeaders = workPropHeaderFormatInfo.getHeaderCsvPropValues();
        headerList.addAll(propValueHeaders.stream().map(PropHeaderFormatInfo::getPropValue).collect(toList()));

        addResult(LocaleUtils.getString("key.import.work.plan.work.prop") + " [" + csvFileName + "]");
        // csvファイル読込
        final Optional<List<Map<String, String>>> optCsvData = readCsvFile(folder, csvFileName, csvEncode, headerRow, startRow);
        if (!optCsvData.isPresent()) {
            return Optional.empty();
        }
        final List<Map<String, String>> csvData = optCsvData.get();

        if (csvData.isEmpty()) {
            return Optional.of(new HashMap<>());
        }

        final Map<String, String> firstCsvData = csvData.get(0);
        final List<String> checkIncludeHeader
                = headerList
                .stream()
                .filter(StringUtils::nonEmpty)
                .filter(header -> !firstCsvData.containsKey(header))
                .collect(toList());

        if (!checkIncludeHeader.isEmpty()) {
            checkIncludeHeader
                    .forEach(header -> {
                        this.addResult(String.format(LocaleUtils.getString("key.NotFoundHeaderColumn"), LocaleUtils.getString("key.import.production.work.property"), header));
                    });
            return Optional.empty();
        }

        // 各種情報を取出し
        final List<BiConsumer<Map<String, String>, WorkInfoEntity>> consumers = new ArrayList<>();
        consumers.addAll(
                Arrays.asList
                (       // 工程名
                        createConsumer(workNameHeader, workPropHeaderFormatInfo.getHeaderCsvProcessDelimiter(), "", WorkInfoEntity::setWorkName)
                )
        );

        // プロパティ設定
        propValueHeaders.forEach(header -> {
            consumers.add(createConsumer(header.getPropValue(), "", (entity, val) -> {
                WorkPropertyInfoEntity tmp = new WorkPropertyInfoEntity();
                // 名称
                if (StringUtils.isEmpty(header.getPropName())) {
                    tmp.setWorkPropName(header.getPropValue());
                } else {
                    tmp.setWorkPropName(header.getPropName());
                }
                tmp.setWorkPropType(header.getPropertyType());   // データ種
                tmp.setWorkPropValue(val); // 値
                entity.addPropertyInfo(tmp);
            }));
        });

        final List<WorkInfoEntity> workInfoEntities
                = csvData
                .stream()
                .map(item -> {
                    WorkInfoEntity workInfoEntity = new WorkInfoEntity();
                    consumers.forEach(consumer -> consumer.accept(item, workInfoEntity));
                    return workInfoEntity;
                })
                .collect(toList());

        int line = startRow;
        List<String> errorMessage = new ArrayList<>();
        List<String> warningMessage = new ArrayList<>();
        for (WorkInfoEntity workInfoEntity : workInfoEntities) {
            // 工程名のチェック
            if (StringUtils.isEmpty(workInfoEntity.getWorkName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.ProcessName")));
            } else {
                if (this.countByteLength(workInfoEntity.getWorkName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.ProcessName")));
                        workInfoEntity.setWorkName(this.cutOver(workInfoEntity.getWorkName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.ProcessName")));
                    }
                }
            }
            ++line;
        }

        warningMessage.forEach(this::addResult);
        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }
        
        // 重複削除(工程名)
        Map<String, List<WorkInfoEntity>> workGroupList =
                workInfoEntities
                        .stream()
                        .collect(
                                Collectors.groupingBy(WorkInfoEntity::getWorkName,
                                        toList()));

        Map<String, List<WorkPropertyInfoEntity>> workPropertyElementsList = new HashMap<>();
        for (Map.Entry<String, List<WorkInfoEntity>> entry : workGroupList.entrySet()) {
            List<WorkPropertyInfoEntity> workPropertyElements
                    = new ArrayList<>(entry.getValue()
                    .stream()
                    .map(WorkInfoEntity::getPropertyInfoCollection)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(WorkPropertyInfoEntity::getWorkPropName, Function.identity(), (a,b) -> a, LinkedHashMap::new))
                    .values());

            for(int n = 0; n < workPropertyElements.size(); ++n) {
                workPropertyElements.get(n).setWorkPropOrder(n);
            }
            workPropertyElementsList.put(entry.getKey(), workPropertyElements);
        }

        return Optional.of(workPropertyElementsList);
    }

    /**
     * 工程順csvファイル読込
     *
     * @param folder フォルダ
     * @param workflowHeaderFormatInfo 設定情報
     */
    private Optional<Map<String, WorkflowInfoEntity>> loadWorkflowCsvFile(String folder, WorkflowHeaderFormatInfo workflowHeaderFormatInfo) {
        if (Objects.isNull(workflowHeaderFormatInfo)) {
            return Optional.empty();
        }

        // ファイル名
        final String workflowFileName = workflowHeaderFormatInfo.getHeaderCsvFileName();
        if (StringUtils.isEmpty(workflowFileName)) {
            return Optional.of(new HashMap<>());
        }

        // エンコード
        final String workflowEncode = workflowHeaderFormatInfo.getHeaderCsvFileEncode();
        if (StringUtils.isEmpty(workflowEncode)) {
            return Optional.empty();
        }

        // ヘッダ行数
        final Integer headerRow = integerValueOf(workflowHeaderFormatInfo.getHeaderCsvHeaderRow());
        if (Objects.isNull(headerRow)) {
            return Optional.empty();
        }

        // 読込開始行数
        final Integer startRow = integerValueOf(workflowHeaderFormatInfo.getHeaderCsvStartRow());
        if (Objects.isNull(startRow)) {
            return Optional.empty();
        }

        if (headerRow >= startRow) {
            return Optional.empty();
        }

        List<String> headerList = new ArrayList<>();

        // 工程順階層
        final List<String> workflowHierarchyNameHeader
                = workflowHeaderFormatInfo
                .getHeaderCsvWorkflowHierarchyNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(toList());
        if (workflowHierarchyNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(workflowHierarchyNameHeader);

        // 工程順名
        final List<String> workflowNameHeader
                = workflowHeaderFormatInfo
                .getHeaderCsvWorkflowNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (workflowNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(workflowNameHeader);

        // モデル名
        final List<String> modelNameHeader
                = workflowHeaderFormatInfo
                .getHeaderCsvModelNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        headerList.addAll(modelNameHeader);

        // 工程名
        final List<String> workNameHeader
                = workflowHeaderFormatInfo
                .getHeaderCsvProcessNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (workNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(workNameHeader);

        headerList.add(workflowHeaderFormatInfo.getHeaderCsvOrganization()); // 設備
        headerList.add(workflowHeaderFormatInfo.getHeaderCsvEquipment()); // 組織
        headerList.add(workflowHeaderFormatInfo.getHeaderCsvProcOrder()); // 並び順


        addResult(LocaleUtils.getString("key.import.work.plan.workflow") + " [" + workflowFileName + "]");
        // csvファイル読込
        final Optional<List<Map<String, String>>> optWorkflowCsvData = readCsvFile(folder, workflowFileName, workflowEncode, headerRow, startRow);
        if (!optWorkflowCsvData.isPresent()) {
            return Optional.empty();
        }
        final List<Map<String, String>> workflowCsvData = optWorkflowCsvData.get();
        if (workflowCsvData.isEmpty()) {
            return Optional.of(new HashMap<>());
        }

        final Map<String, String> firstWorkCsvData = workflowCsvData.get(0);
        final List<String> checkIncludeHeader
                = headerList
                .stream()
                .filter(StringUtils::nonEmpty)
                .filter(header -> !firstWorkCsvData.containsKey(header))
                .collect(toList());

        if (!checkIncludeHeader.isEmpty()) {
            checkIncludeHeader
                    .forEach(header -> {
                        this.addResult(String.format(LocaleUtils.getString("key.NotFoundHeaderColumn"), LocaleUtils.getString("key.import.production.workflow"), header));
                    });
            return Optional.empty();
        }


        Date defaultDate = null;
        try {
            defaultDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1970-01-01 00:00:00+00");
        } catch(Exception ex) {
            return Optional.empty();
        }
        final List<BiConsumer<Map<String, String>, ConWorkflowWorkInfoEntity>> coWorkflowConsumers
                = Arrays.asList
                (
                        createConsumer(workNameHeader, workflowHeaderFormatInfo.getHeaderCsvProcessNameDelimiter(), "", ConWorkflowWorkInfoEntity::setWorkName), // 工程名
                        createConsumer(workflowHeaderFormatInfo.getHeaderCsvOrganization(), "", ConWorkflowWorkInfoEntity::addOrganizationIdentify), // 組織
                        createConsumer(workflowHeaderFormatInfo.getHeaderCsvEquipment(), "", ConWorkflowWorkInfoEntity::addEquipmentIdentify), // 設備
                        createDefaultConsumer(defaultDate, ConWorkflowWorkInfoEntity::setStandardStartTime), // 標準開始時間
                        createDefaultConsumer(defaultDate, ConWorkflowWorkInfoEntity::setStandardEndTime) // 標準終了時間
                );

        Date zero = null;
        try {
            zero =  new SimpleDateFormat("HH:mm").parse("00:00");
        } catch (ParseException e) {
            return Optional.empty();
        }

        final Date now = new Date();
        final List<BiConsumer<Map<String, String>, WorkflowInfoEntity>> workflowConsumers
                = Arrays.asList
                (
                        createConsumer(workflowHierarchyNameHeader, workflowHeaderFormatInfo.getHeaderCsvHierarchyDelimiter(), "", WorkflowInfoEntity::setParentName),// 工程順階層
                        createConsumer(workflowNameHeader, workflowHeaderFormatInfo.getHeaderCsvWorkflowDelimiter(), "", WorkflowInfoEntity::setWorkflowName),  // 工程順名
                        createConsumer(modelNameHeader, workflowHeaderFormatInfo.getHeaderCsvModelDelimiter(), "", WorkflowInfoEntity::setModelName), // モデル名
                        createDefaultConsumer(zero, WorkflowInfoEntity::setOpenTime),
                        createDefaultConsumer(zero, WorkflowInfoEntity::setCloseTime),
                        createDefaultConsumer(now, WorkflowInfoEntity::setUpdateDatetime),
                        createDefaultConsumer(loginUserInfoEntity.getId(), WorkflowInfoEntity::setFkUpdatePersonId)
                );

        final List<Tuple<Map<String, String>, WorkflowInfoEntity>> workflowInfoEntities
                = workflowCsvData
                .stream()
                .sorted(Comparator.comparing(item->item.getOrDefault(workflowHeaderFormatInfo.getHeaderCsvProcOrder(), ""))) // 並び替え
                .map(item -> {
                    WorkflowInfoEntity workflowInfoEntity = new WorkflowInfoEntity();
                    workflowConsumers.forEach(consumer -> consumer.accept(item, workflowInfoEntity));

                    ConWorkflowWorkInfoEntity conWorkflowWorkInfoEntity = new ConWorkflowWorkInfoEntity();
                    coWorkflowConsumers.forEach(consumer -> consumer.accept(item, conWorkflowWorkInfoEntity));

                    workflowInfoEntity.setConWorkflowWorkInfoCollection(Collections.singletonList(conWorkflowWorkInfoEntity));
                    return new Tuple<>(item, workflowInfoEntity);
                })
                .collect(toList());

        List<String> warningMessage = new ArrayList<>();
        List<String> errorMessage = new ArrayList<>();
        for (Tuple<Map<String, String>, WorkflowInfoEntity> workflowInfoEntityPair : workflowInfoEntities) {
            WorkflowInfoEntity workflowInfoEntity = workflowInfoEntityPair.getRight();
            Map<String, String> item = workflowInfoEntityPair.getLeft();
            String line = workflowInfoEntityPair.getLeft().get(ROW_NUM_COLUMN);
            // 工程順階層名のチェック
            if (StringUtils.isEmpty(workflowInfoEntity.getParentName())) {
                errorMessage.add(String.format("[%s] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.OrderProcessesHierarch")));
            } else {
                if (this.countByteLength(workflowInfoEntity.getParentName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%s] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.OrderProcessesHierarch")));
                        workflowInfoEntity.setParentName(this.cutOver(workflowInfoEntity.getParentName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%s] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.OrderProcessesHierarch")));
                    }
                }
            }
            // 工程順名のチェック
            if (StringUtils.isEmpty(workflowInfoEntity.getWorkflowName())) {
                errorMessage.add(String.format("[%s] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.OrderProcessesName")));
            } else {
                if (this.countByteLength(workflowInfoEntity.getWorkflowName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%s] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.OrderProcessesName")));
                        workflowInfoEntity.setWorkflowName(this.cutOver(workflowInfoEntity.getWorkflowName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%s] > %s [%s]", line,
                            LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.OrderProcessesName")));
                    }
                }
            }
            // モデル名のチェック
            if (this.countByteLength(workflowInfoEntity.getModelName()) > MAX_CHAR) {
                if (isCutItemNameOver) {
                    warningMessage.add(String.format("[%s] > %s [%s]", line,
                            String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.ModelName")));
                    workflowInfoEntity.setModelName(this.cutOver(workflowInfoEntity.getModelName(), MAX_CHAR));
                } else {
                    errorMessage.add(String.format("[%s] > %s [%s]", line,
                            LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.ModelName")));
                }
            }
            // 工程名のチェック
            if (StringUtils.isEmpty(workflowInfoEntity.getConWorkflowWorkInfoCollection().get(0).getWorkName())) {
                errorMessage.add(String.format("[%s] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.ProcessName")));
            } else {
                if (this.countByteLength(workflowInfoEntity.getConWorkflowWorkInfoCollection().get(0).getWorkName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%s] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.ProcessName")));
                        workflowInfoEntity.getConWorkflowWorkInfoCollection().get(0).setWorkName(this.cutOver(workflowInfoEntity.getConWorkflowWorkInfoCollection().get(0).getWorkName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%s] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.ProcessName")));
                    }
                }
            }
        }

        warningMessage.forEach(this::addResult);
        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        // 重複削除(カンバン名 + 工程順 + 版数)
        Map<String, Map<String, List<WorkflowInfoEntity>>> workflowGroupList =
                workflowInfoEntities
                        .stream()
                        .map(Tuple::getRight)
                        .collect(
                                Collectors.groupingBy(WorkflowInfoEntity::getWorkflowName, // 工程順名
                                        LinkedHashMap::new,
                                        Collectors.groupingBy(entity ->
                                                        entity.getParentName() + "####" + // 階層名
                                                                entity.getModelName(), // モデル名
                                                LinkedHashMap::new,
                                                Collectors.toList())));

        for (Map.Entry<String, Map<String, List<WorkflowInfoEntity>>> entry : workflowGroupList.entrySet()) {
            final List<List<WorkflowInfoEntity>> items = new ArrayList<>(entry.getValue().values());
            if (items.size() != 1) {
                errorMessage.add(String.format(" > %s [%s: %s]",
                        LocaleUtils.getString("key.ImportKanban_DuplicationDataDif"),
                        LocaleUtils.getString("key.OrderProcessesName"), items.get(0).get(0).getWorkflowName()));
                continue;
            }
        }

        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        Map<String, WorkflowInfoEntity> workflowInfoEntityList
                = workflowGroupList.values()
                .stream()
                .map(items -> {
                            List<ConWorkflowWorkInfoEntity> conWorkflowWorkInfoCollection
                                    = new ArrayList<>(items
                                    .values()
                                    .stream()
                                    .flatMap(Collection::stream)
                                    .map(WorkflowInfoEntity::getConWorkflowWorkInfoCollection)
                                    .filter(Objects::nonNull)
                                    .flatMap(Collection::stream)
                                    .collect(groupingBy(ConWorkflowWorkInfoEntity::getWorkName,
                                            LinkedHashMap::new,
                                            collectingAndThen(
                                                    toList(),
                                                    list -> {
                                                        // 設備, 組織を設定
                                                        Set<String> organizations
                                                                = list
                                                                .stream()
                                                                .map(ConWorkflowWorkInfoEntity::getOrganizationIdentifyCollection)
                                                                .filter(Objects::nonNull)
                                                                .flatMap(Collection::stream)
                                                                .filter(StringUtils::nonEmpty)
                                                                .collect(toSet());
                                                        Set<String> equipments
                                                                = list
                                                                .stream()
                                                                .map(ConWorkflowWorkInfoEntity::getEquipmentIdentifyCollection)
                                                                .filter(Objects::nonNull)
                                                                .flatMap(Collection::stream)
                                                                .filter(StringUtils::nonEmpty)
                                                                .collect(toSet());

                                                        list.get(0).setOrganizationIdentifyCollection(new ArrayList<>(organizations));
                                                        list.get(0).setEquipmentIdentifyCollection(new ArrayList<>(equipments));
                                                        return list.get(0);
                                                    })))
                                    .values());

                            Optional<WorkflowInfoEntity> optWorkflowInfoEntity = items.values().stream().flatMap(Collection::stream).findFirst();
                            if (!optWorkflowInfoEntity.isPresent()) {
                                // 在りえない
                                return null;
                            }
                            // 工程順工程関連付け情報を設定
                            WorkflowInfoEntity workflowInfoEntity = optWorkflowInfoEntity.get();
                            workflowInfoEntity.setConWorkflowWorkInfoCollection(conWorkflowWorkInfoCollection);
                            return workflowInfoEntity;
                        }
                )
                .filter(Objects::nonNull)
                .collect(toMap(WorkflowInfoEntity::getWorkflowName, Function.identity(), (a, b)->a, LinkedHashMap::new));

        return Optional.of(workflowInfoEntityList);
    }

    /**
     * 工程順プロパティcsvファイル読込
     *
     * @param folder フォルダ
     * @param workflowPropHeaderFormatInfo 設定情報
     */
    private Optional<Map<String, List<KanbanPropertyTemplateInfoEntity>>> loadWorkflowPropCsvFile(String folder, WorkflowPropHeaderFormatInfo workflowPropHeaderFormatInfo) {
        if (Objects.isNull(workflowPropHeaderFormatInfo)) {
            return Optional.empty();
        }

        // ファイル名
        final String workflowFileName = workflowPropHeaderFormatInfo.getHeaderCsvFileName();
        if (StringUtils.isEmpty(workflowFileName)) {
            return Optional.of(new HashMap<>());
        }

        // エンコード
        final String workflowEncode = workflowPropHeaderFormatInfo.getHeaderCsvFileEncode();
        if (StringUtils.isEmpty(workflowEncode)) {
            return Optional.empty();
        }

        // ヘッダ行数
        final Integer headerRow = integerValueOf(workflowPropHeaderFormatInfo.getHeaderCsvHeaderRow());
        if (Objects.isNull(headerRow)) {
            return Optional.empty();
        }

        // 読込開始行数
        final Integer startRow = integerValueOf(workflowPropHeaderFormatInfo.getHeaderCsvStartRow());
        if (Objects.isNull(startRow)) {
            return Optional.empty();
        }

        if (headerRow >= startRow) {
            return Optional.empty();
        }

        List<String> headerList = new ArrayList<>();
        // 工程順名
        final List<String> workflowNameHeader
                = workflowPropHeaderFormatInfo
                .getHeaderCsvWorkflowNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (workflowNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(workflowNameHeader);

        // プロパティ
        final List<PropHeaderFormatInfo> propValueHeaders = workflowPropHeaderFormatInfo.getHeaderCsvPropValues();
        headerList.addAll(propValueHeaders.stream().map(PropHeaderFormatInfo::getPropValue).collect(toList()));

        addResult(LocaleUtils.getString("key.import.work.plan.workflow.prop") + " [" + workflowFileName + "]");
        // csvファイル読込
        final Optional<List<Map<String, String>>> optWorkflowCsvData = readCsvFile(folder, workflowFileName, workflowEncode, headerRow, startRow);
        if (!optWorkflowCsvData.isPresent()) {
            return Optional.empty();
        }
        final List<Map<String, String>> workflowCsvData = optWorkflowCsvData.get();

        if (workflowCsvData.isEmpty()) {
            return Optional.of(new HashMap<>());
        }

        final Map<String, String> firstWorkCsvData = workflowCsvData.get(0);
        final List<String> checkIncludeHeader
                = headerList
                .stream()
                .filter(StringUtils::nonEmpty)
                .filter(header -> !firstWorkCsvData.containsKey(header))
                .collect(toList());

        if (!checkIncludeHeader.isEmpty()) {
            checkIncludeHeader
                    .forEach(header -> {
                        this.addResult(String.format(LocaleUtils.getString("key.NotFoundHeaderColumn"), LocaleUtils.getString("key.import.production.workflow.property"), header));
                    });
            return Optional.empty();
        }

        // 各種情報を取出し
        final List<BiConsumer<Map<String, String>, WorkflowInfoEntity>> consumers
                = new ArrayList<>(Arrays.asList
                (
                        createConsumer(workflowNameHeader, workflowPropHeaderFormatInfo.getHeaderCsvWorkflowDelimiter(), "", WorkflowInfoEntity::setWorkflowName)            // 工程順名
                ));

        // プロパティ設定
        propValueHeaders.forEach(header -> {
            consumers.add(createConsumer(header.getPropValue(), "", (entity, val) -> {
                KanbanPropertyTemplateInfoEntity tmp = new KanbanPropertyTemplateInfoEntity();
                // 名称
                if (StringUtils.isEmpty(header.getPropName())) {
                    tmp.setKanbanPropName(header.getPropValue());
                } else {
                    tmp.setKanbanPropName(header.getPropName());
                }
                tmp.setKanbanPropType(header.getPropertyType());   // データ種
                tmp.setKanbanPropInitialValue(val); // 値
                entity.addKanbanPropertyTemplateInfo(tmp);
            }));
        });

        final List<WorkflowInfoEntity> workflowInfoEntities
                = workflowCsvData
                .stream()
                .map(item -> {
                    WorkflowInfoEntity workflowInfoEntity = new WorkflowInfoEntity();
                    consumers.forEach(consumer -> consumer.accept(item, workflowInfoEntity));
                    return workflowInfoEntity;
                })
                .collect(toList());

        int line = startRow;
        List<String> warningMessage = new ArrayList<>();
        List<String> errorMessage = new ArrayList<>();
        for (WorkflowInfoEntity workflowInfoEntity : workflowInfoEntities) {
            // 工程順名のチェック
            if (StringUtils.isEmpty(workflowInfoEntity.getWorkflowName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.OrderProcessesName")));
            } else {
                if (this.countByteLength(workflowInfoEntity.getWorkflowName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.OrderProcessesName")));
                        workflowInfoEntity.setWorkflowName(this.cutOver(workflowInfoEntity.getWorkflowName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.OrderProcessesName")));
                    }
                }
            }

            ++line;
        }

        warningMessage.forEach(this::addResult);
        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        Map<String, List<WorkflowInfoEntity>> workflowGroupList
                = workflowInfoEntities
                .stream()
                .collect(groupingBy(WorkflowInfoEntity::getWorkflowName));

        Map<String, List<KanbanPropertyTemplateInfoEntity>> workflowInfoEntitiesList = new HashMap<>();
        for (Map.Entry<String, List<WorkflowInfoEntity>> entry : workflowGroupList.entrySet()) {
            List<KanbanPropertyTemplateInfoEntity> workflowPropertyElements
                    = new ArrayList<>(entry.getValue()
                    .stream()
                    .map(WorkflowInfoEntity::getKanbanPropertyTemplateInfoCollection)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(KanbanPropertyTemplateInfoEntity::getKanbanPropName, Function.identity(), (a, b) -> a, LinkedHashMap::new))
                    .values());

            AtomicInteger i = new AtomicInteger(0);
            workflowPropertyElements.forEach(kanbanPropertyTemplateInfoEntity -> kanbanPropertyTemplateInfoEntity.setKanbanPropOrder(i.getAndIncrement()));

            workflowInfoEntitiesList.put(entry.getKey(), workflowPropertyElements);
        }

        return Optional.of(workflowInfoEntitiesList);
    }

    /**
     * 工程カンバンcsvファイル読込
     *
     * @param folder フォルダ
     * @param workKanbanHeaderFormatInfo 設定情報
     */
    private Optional<Map<String, Map<String, WorkKanbanInfoEntity>>> loadWorkKanbanCsvFile(String folder, WorkKanbanHeaderFormatInfo workKanbanHeaderFormatInfo) {
        if (Objects.isNull(workKanbanHeaderFormatInfo)) {
            return Optional.empty();
        }

        // エンコード
        final String encode = workKanbanHeaderFormatInfo.getHeaderCsvFileEncode();
        if (StringUtils.isEmpty(encode)) {
            return Optional.empty();
        }
        // ファイル名
        final String fileName = workKanbanHeaderFormatInfo.getHeaderCsvFileName();
        if (StringUtils.isEmpty(fileName)) {
            return Optional.of(new HashMap<>());
        }
        // ヘッダ行数
        final Integer headerRow = integerValueOf(workKanbanHeaderFormatInfo.getHeaderCsvHeaderRow());
        if (Objects.isNull(headerRow)) {
            return Optional.empty();
        }
        // 読込開始行数
        final Integer startRow = integerValueOf(workKanbanHeaderFormatInfo.getHeaderCsvStartRow());
        if (Objects.isNull(startRow)) {
            return Optional.empty();
        }
        if (headerRow >= startRow) {
            return Optional.empty();
        }

        List<String> headerList = new ArrayList<>();
        // カンバン名
        final List<String> kanbanNameHeader
                = workKanbanHeaderFormatInfo
                .getHeaderCsvKanbanNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (kanbanNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(kanbanNameHeader);

        // 工程順名
        final List<String> workflowNameHeader
                = workKanbanHeaderFormatInfo
                .getHeaderCsvWorkflowNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (workflowNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(workflowNameHeader);

        // 工程順リビジョン
        final String workflowRevHeader = workKanbanHeaderFormatInfo.getHeaderCsvWorkflowRev();
        headerList.add(workflowRevHeader);

        // 工程名
        final List<String> workNameHeader
                = workKanbanHeaderFormatInfo
                .getHeaderCsvWorkNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (workNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(workNameHeader);

        // タクトタイム
        final String tactTimeHeader = workKanbanHeaderFormatInfo.getHeaderCsvTactTime();
        headerList.add(tactTimeHeader);

        // 単位
        final String tactTimeUnit = workKanbanHeaderFormatInfo.getHeaderCsvTactTimeUnit();
        final int timeUnit = StringUtils.equals(LocaleUtils.getString("key.time.second"), tactTimeUnit) ? 1000 : 60000;

        // 製造開始日
        final String startDateTimeHeader = workKanbanHeaderFormatInfo.getHeaderCsvStartDateTime();
        headerList.add(startDateTimeHeader);

        // 製造完了日
        final String endDateTimeHeader = workKanbanHeaderFormatInfo.getHeaderCsvEndDateTime();
        headerList.add(endDateTimeHeader);

        headerList.add(workKanbanHeaderFormatInfo.getHeaderCsvOrganization()); // 組織
        headerList.add(workKanbanHeaderFormatInfo.getHeaderCsvEquipment()); // 設備

        addResult(LocaleUtils.getString("key.import.work.plan.work.kanban") + " [" + fileName + "]");
        // csvファイル読込
        final Optional<List<Map<String, String>>> optWorkKanbanCsvData = readCsvFile(folder, fileName, encode, headerRow, startRow);
        if (!optWorkKanbanCsvData.isPresent()) {
            return Optional.empty();
        }
        final List<Map<String, String>> workKanbanCsvData = optWorkKanbanCsvData.get();
        if (workKanbanCsvData.isEmpty()) {
            return Optional.of(new HashMap<>());
        }

        final Map<String, String> firstWorkCsvData = workKanbanCsvData.get(0);
        final List<String> checkIncludeHeader
                = headerList
                .stream()
                .filter(StringUtils::nonEmpty)
                .filter(header -> !firstWorkCsvData.containsKey(header))
                .collect(toList());

        if (!checkIncludeHeader.isEmpty()) {
            checkIncludeHeader
                    .forEach(header -> {
                        this.addResult(String.format(LocaleUtils.getString("key.NotFoundHeaderColumn"), LocaleUtils.getString("key.import.production.work.kanba"), header));
                    });
            return Optional.empty();
        }

        Date nowDate = new Date();
        List<String> warningMessage = new ArrayList<>();
        List<String> errorMessage = new ArrayList<>();
        final List<BiConsumer<Map<String, String>, WorkKanbanInfoEntity>> consumers
                = Arrays.asList
                (
                        createConsumer(kanbanNameHeader, workKanbanHeaderFormatInfo.getHeaderCsvKanbanDelimiter(), "", WorkKanbanInfoEntity::setKanbanName),                // カンバン名
                        createConsumer(workflowNameHeader, workKanbanHeaderFormatInfo.getHeaderCsvWorkflowDelimiter(), "", WorkKanbanInfoEntity::setWorkflowName),          // 工程順名
                        createConsumer((workKanbanInfoEntity, item) -> {    // 工程順リビジョン
                            if (StringUtils.isEmpty(item.get(workflowRevHeader))) {
                                return;
                            }
                            Optional<Integer> optWorkflowRev = toInteger(item.get(workflowRevHeader));
                            if (optWorkflowRev.isPresent()) {
                                workKanbanInfoEntity.setWorkflowRev(optWorkflowRev.get());
                            } else {
                                // 工程順リビジョンに数値以外が入力されている場合
                                errorMessage.add(String.format("[%s] > %s",
                                        item.get(ROW_NUM_COLUMN),
                                        LocaleUtils.getString("key.ImportKanban_WorkflowRevIsNotNum")));
                            }
                        }),
                        createConsumer(workNameHeader, workKanbanHeaderFormatInfo.getHeaderCsvWorkDelimiter(), "", WorkKanbanInfoEntity::setWorkName),          // 工程名
                        createConsumer((workKanbanInfoEntity, item) -> {    // タクトタイム
                            if (StringUtils.isEmpty(item.get(tactTimeHeader))) {
                                return;
                            }
                            Optional<Integer> optTactTime = toInteger(item.getOrDefault(tactTimeHeader, "0"));
                            if (optTactTime.isPresent()) {
                                workKanbanInfoEntity.setTaktTime(timeUnit * optTactTime.get());
                            } else {
                                // タクトタイムに数値以外が入力されている場合
                                errorMessage.add(String.format("[%s] > %s",
                                        item.get(ROW_NUM_COLUMN),
                                        LocaleUtils.getString("key.ImportKanban_TactTimeIsNotNum")));
                            }
                        }),
                        createConsumer((workKanbanInfoEntity, item) -> {    // 開始予定日時
                            if (StringUtils.isEmpty(item.get(startDateTimeHeader))) {
                                return;
                            }

                            Optional<Date> optStartDateTime = toDate(item.get(startDateTimeHeader));
                            if (optStartDateTime.isPresent()) {
                                workKanbanInfoEntity.setStartDatetime(optStartDateTime.get());
                            } else {
                                // 開始予定日時に日付以外が入力されている場合
                                errorMessage.add(String.format("[%s] > %s",
                                        item.get(ROW_NUM_COLUMN),
                                        LocaleUtils.getString("key.ImportKanban_StartDateTimeIsNotDate")));
                            }
                        }),
                        createConsumer((workKanbanInfoEntity, item) -> {    // 完了予定日時
                            if (StringUtils.isEmpty(item.get(endDateTimeHeader))) {
                                return;
                            }

                            Optional<Date> optCompDateTime = toDate(item.get(endDateTimeHeader));
                            if (optCompDateTime.isPresent()) {
                                workKanbanInfoEntity.setCompDatetime(optCompDateTime.get());
                            } else {
                                // 完了予定日時に日付以外が入力されている場合
                                errorMessage.add(String.format("[%s] > %s",
                                        item.get(ROW_NUM_COLUMN),
                                        LocaleUtils.getString("key.ImportKanban_CompDateTimeIsNotDate")));
                            }
                        }),
                        createConsumer(workKanbanHeaderFormatInfo.getHeaderCsvOrganization(), "", WorkKanbanInfoEntity::addOrganizationIdentify), // 組織
                        createConsumer(workKanbanHeaderFormatInfo.getHeaderCsvEquipment(), "", WorkKanbanInfoEntity::addEquipmentIdentify), // 設備
                        createDefaultConsumer(false, WorkKanbanInfoEntity::setSkipFlag) // スキップしない
                );

        // 各種情報を取出し
        final List<WorkKanbanInfoEntity> workKanbanInfoEntities
                = workKanbanCsvData
                .stream()
                .map(item -> {
                    WorkKanbanInfoEntity workKanbanInfoEntity = new WorkKanbanInfoEntity();
                    consumers.forEach(consumer -> consumer.accept(item, workKanbanInfoEntity));
                    return workKanbanInfoEntity;
                })
                .collect(toList());

        int line = startRow;
        for (WorkKanbanInfoEntity workKanbanInfoEntity : workKanbanInfoEntities) {
            // カンバン名のチェック
            if (StringUtils.isEmpty(workKanbanInfoEntity.getKanbanName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.KanbanName")));
            } else {
                if (this.countByteLength(workKanbanInfoEntity.getKanbanName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.KanbanName")));
                        workKanbanInfoEntity.setKanbanName(this.cutOver(workKanbanInfoEntity.getKanbanName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.KanbanName")));
                    }
                }
            }
            // 工程順名のチェック
            if (StringUtils.isEmpty(workKanbanInfoEntity.getWorkflowName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.OrderProcessesName")));
            } else {
                if (this.countByteLength(workKanbanInfoEntity.getWorkflowName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.OrderProcessesName")));
                        workKanbanInfoEntity.setWorkflowName(this.cutOver(workKanbanInfoEntity.getWorkflowName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.OrderProcessesName")));
                    }
                }
            }
            // 工程名のチェック
            if (StringUtils.isEmpty(workKanbanInfoEntity.getWorkName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.ProcessName")));
            } else {
                if (this.countByteLength(workKanbanInfoEntity.getWorkName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.ProcessName")));
                        workKanbanInfoEntity.setWorkName(this.cutOver(workKanbanInfoEntity.getWorkName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.ProcessName")));
                    }
                }
            }
            ++line;
        }

        warningMessage.forEach(this::addResult);
        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        // 重複削除(カンバン名 + 工程順 + 版数)
        Map<String, Map<String, Map<String, List<WorkKanbanInfoEntity>>>> workKanbanGroupList
                = workKanbanInfoEntities
                .stream()
                .collect(
                        Collectors.groupingBy(item
                                        -> item.getKanbanName() + "####"
                                        + item.getWorkflowName() + "####"
                                        + item.getWorkflowRev(),
                                Collectors.groupingBy(
                                        item -> item.getWorkName(),
                                Collectors.groupingBy(item
                                                -> Objects.isNull(item.getTaktTime()) ? "" : item.getTaktTime().toString(),
                                        Collectors.toList()))));

        Map<String, Map<String, WorkKanbanInfoEntity>> workKanbanElementsListByKanban = new HashMap<>();
        // カンバン+工程順毎
        for (Map.Entry<String, Map<String, Map<String, List<WorkKanbanInfoEntity>>>> kanbanEntry : workKanbanGroupList.entrySet()) {
            // 工程毎
            Map<String, WorkKanbanInfoEntity> workKanbanElementsMap = new HashMap<>();
            for (Map.Entry<String, Map<String, List<WorkKanbanInfoEntity>>> workEntry : kanbanEntry.getValue().entrySet()) {
                // 重複エラー
                List<List<WorkKanbanInfoEntity>> items = new ArrayList<>(workEntry.getValue().values());
//                if (items.size() != 1) {
//                    errorMessage.add(String.format(" > %s [%s: %s][%s: %s][%s: %s]",
//                        LocaleUtils.getString("key.ImportKanban_DuplicationDataDif"),
//                        LocaleUtils.getString("key.KanbanName"), items.get(0).get(0).getKanbanName(),
//                        LocaleUtils.getString("key.WorkflowName"), items.get(0).get(0).getWorkflowName(),
//                        LocaleUtils.getString("key.WorkflowRev"), items.get(0).get(0).getWorkflowRev()));
//                    continue;
//                }

                // 開始予定日
                Date startDate
                        = items
                        .get(0)
                        .stream()
                        .map(WorkKanbanInfoEntity::getStartDatetime)
                        .filter(Objects::nonNull)
                        .min(Comparator.comparing(Function.identity()))
                        .orElse(null);

                // 完了予定日
                Date compDate
                        = items
                        .get(0)
                        .stream()
                        .map(WorkKanbanInfoEntity::getCompDatetime)
                        .filter(Objects::nonNull)
                        .max(Comparator.comparing(Function.identity()))
                        .orElse(null);

                // 組織
                List<String> organizationIdentifies
                        = items
                        .get(0)
                        .stream()
                        .map(WorkKanbanInfoEntity::getOrganizationIdentifyCollection)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(toList());

                // 設備
                List<String> equipmentIdentifies
                        = items
                        .get(0)
                        .stream()
                        .map(WorkKanbanInfoEntity::getEquipmentIdentifyCollection)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(toList());

                WorkKanbanInfoEntity workKanbanInfoEntity = items.get(0).get(0);
                workKanbanInfoEntity.setStartDatetime(startDate);
                workKanbanInfoEntity.setCompDatetime(compDate);
                workKanbanInfoEntity.setEquipmentIdentifyCollection(equipmentIdentifies);
                workKanbanInfoEntity.setOrganizationIdentifyCollection(organizationIdentifies);

                workKanbanElementsMap.put(workEntry.getKey(), workKanbanInfoEntity);
            }
            workKanbanElementsListByKanban.put(kanbanEntry.getKey(), workKanbanElementsMap);
        }

        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        return Optional.of(workKanbanElementsListByKanban);
    }

    /**
     * 工程カンバンプロパティ情報csvファイル読込
     *
     * @param folder フォルダ
     * @param workKanbanPropHeaderFormatInfo 設定情報
     */
    private Optional<Map<String, Map<String, List<WorkKanbanPropertyInfoEntity>>>> loadWorkKanbanPropCsvFile(String folder, WorkKanbanPropHeaderFormatInfo workKanbanPropHeaderFormatInfo) {
        if (Objects.isNull(workKanbanPropHeaderFormatInfo)) {
            return Optional.empty();
        }

        // エンコード
        final String encode = workKanbanPropHeaderFormatInfo.getHeaderCsvFileEncode();
        if (StringUtils.isEmpty(encode)) {
            return Optional.empty();
        }
        // ファイル名
        final String fileName = workKanbanPropHeaderFormatInfo.getHeaderCsvFileName();
        if (StringUtils.isEmpty(fileName)) {
            return Optional.of(new HashMap<>());
        }
        // ヘッダ行数
        final Integer headerRow = integerValueOf(workKanbanPropHeaderFormatInfo.getHeaderCsvHeaderRow());
        if (Objects.isNull(headerRow)) {
            return Optional.empty();
        }
        // 読込開始行数
        final Integer startRow = integerValueOf(workKanbanPropHeaderFormatInfo.getHeaderCsvStartRow());
        if (Objects.isNull(startRow)) {
            return Optional.empty();
        }
        if (headerRow >= startRow) {
            return Optional.empty();
        }

        List<String> headerList = new ArrayList<>();

        // カンバン名
        final List<String> kanbanNameHeader
                = workKanbanPropHeaderFormatInfo
                .getHeaderCsvKanbanNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (kanbanNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(kanbanNameHeader);

        // 工程順名
        final List<String> workflowNameHeader
                = workKanbanPropHeaderFormatInfo
                .getHeaderCsvWorkflowNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (workflowNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(workflowNameHeader);

        // 工程順リビジョン
        final String workflowRevHeader = workKanbanPropHeaderFormatInfo.getHeaderCsvWorkflowRev();
        headerList.add(workflowRevHeader);

        // 工程名
        final List<String> workNameHeader
                = workKanbanPropHeaderFormatInfo
                .getHeaderCsvWorkNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (workNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(workNameHeader);

        // プロパティ
        final List<PropHeaderFormatInfo> propValueHeaders = workKanbanPropHeaderFormatInfo.getHeaderCsvPropValues();
        headerList.addAll(propValueHeaders.stream().map(PropHeaderFormatInfo::getPropValue).collect(toList()));

        addResult(LocaleUtils.getString("key.import.work.plan.work.kanban.property") + " [" + fileName + "]");
        // csvファイル読込
        final Optional<List<Map<String, String>>> optWorkKanbanCsvData = readCsvFile(folder, fileName, encode, headerRow, startRow);
        if (!optWorkKanbanCsvData.isPresent()) {
            return Optional.empty();
        }
        final List<Map<String, String>> workKanbanCsvData = optWorkKanbanCsvData.get();
        if (workKanbanCsvData.isEmpty()) {
            return Optional.of(new HashMap<>());
        }

        final Map<String, String> firstWorkCsvData = workKanbanCsvData.get(0);
        final List<String> checkIncludeHeader
                = headerList
                .stream()
                .filter(StringUtils::nonEmpty)
                .filter(header -> !firstWorkCsvData.containsKey(header))
                .collect(toList());

        if (!checkIncludeHeader.isEmpty()) {
            checkIncludeHeader
                    .forEach(header -> {
                        this.addResult(String.format(LocaleUtils.getString("key.NotFoundHeaderColumn"), LocaleUtils.getString("key.import.production.work.kanba"), header));
                    });
            return Optional.empty();
        }


        // 各種情報を取出し
        List<String> warningMessage = new ArrayList<>();
        List<String> errorMessage = new ArrayList<>();
        final List<BiConsumer<Map<String, String>, WorkKanbanInfoEntity>> consumers = new ArrayList<>(Arrays.asList
                (
                        createConsumer(kanbanNameHeader, workKanbanPropHeaderFormatInfo.getHeaderCsvKanbanDelimiter(), "", WorkKanbanInfoEntity::setKanbanName),  // カンバン名
                        createConsumer(workflowNameHeader, workKanbanPropHeaderFormatInfo.getHeaderCsvWorkflowDelimiter(), "", WorkKanbanInfoEntity::setWorkflowName), // 工程順名
                        createConsumer((workKanbanInfoEntity, item) -> {    // 工程順リビジョン
                            if (StringUtils.isEmpty(item.get(workflowRevHeader))) {
                                return;
                            }
                            Optional<Integer> optWorkflowRev = toInteger(item.get(workflowRevHeader));
                            if (optWorkflowRev.isPresent()) {
                                workKanbanInfoEntity.setWorkflowRev(optWorkflowRev.get());
                            } else {
                                // 工程順リビジョンに数値以外が入力されている場合
                                errorMessage.add(String.format("[%s] > %s",
                                        item.get(ROW_NUM_COLUMN),
                                        LocaleUtils.getString("key.ImportKanban_WorkflowRevIsNotNum")));
                            }
                        }),
                        createConsumer(workNameHeader, workKanbanPropHeaderFormatInfo.getHeaderCsvWorkDelimiter(), "", WorkKanbanInfoEntity::setWorkName)          // 工程名
                ));

        // プロパティ設定
        propValueHeaders.forEach(header -> {
            consumers.add(createConsumer(header.getPropValue(), "", (entity, val) -> {
                WorkKanbanPropertyInfoEntity tmp = new WorkKanbanPropertyInfoEntity();
                // 名称
                if (StringUtils.isEmpty(header.getPropName())) {
                    tmp.setWorkKanbanPropName(header.getPropValue());
                } else {
                    tmp.setWorkKanbanPropName(header.getPropName());
                }
                tmp.setWorkKanbanPropType(header.getPropertyType());   // データ種
                tmp.setWorkKanbanPropValue(val); // 値
                entity.addProperty(tmp);
            }));
        });

        final List<WorkKanbanInfoEntity> workKanbanInfoEntities
                = workKanbanCsvData
                .stream()
                .map(item -> {
                    WorkKanbanInfoEntity workKanbanInfoEntity = new WorkKanbanInfoEntity();
                    consumers.forEach(consumer -> consumer.accept(item, workKanbanInfoEntity));
                    return workKanbanInfoEntity;
                })
                .collect(toList());

        int line = startRow;
        for (WorkKanbanInfoEntity workKanbanInfoEntity : workKanbanInfoEntities) {
            // カンバン名のチェック
            if (StringUtils.isEmpty(workKanbanInfoEntity.getKanbanName())) {
                errorMessage.add(String.format("[%s] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.KanbanName")));
            } else {
                if (this.countByteLength(workKanbanInfoEntity.getKanbanName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.KanbanName")));
                        workKanbanInfoEntity.setKanbanName(this.cutOver(workKanbanInfoEntity.getKanbanName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.KanbanName")));
                    }
                }
            }
            // 工程順名のチェック
            if (StringUtils.isEmpty(workKanbanInfoEntity.getWorkflowName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.OrderProcessesName")));
            } else {
                if (this.countByteLength(workKanbanInfoEntity.getWorkflowName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.OrderProcessesName")));
                        workKanbanInfoEntity.setWorkflowName(this.cutOver(workKanbanInfoEntity.getWorkflowName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.OrderProcessesName")));
                    }
                }
            }
            // 工程名のチェック
            if (StringUtils.isEmpty(workKanbanInfoEntity.getWorkName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.ProcessName")));
            } else {
                if (this.countByteLength(workKanbanInfoEntity.getWorkName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.ProcessName")));
                        workKanbanInfoEntity.setWorkName(this.cutOver(workKanbanInfoEntity.getWorkName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.ProcessName")));
                    }
                }
            }
            ++line;
        }

        warningMessage.forEach(this::addResult);
        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        // 重複削除(カンバン名 + 工程順 + 版数)
        Map<String, Map<String, List<WorkKanbanInfoEntity>>> workKanbanGroupList
                = workKanbanInfoEntities
                .stream()
                .collect(
                        Collectors.groupingBy(item
                                        -> item.getKanbanName() + "####"
                                        + item.getWorkflowName() + "####"
                                        + item.getWorkflowRev(),
                                Collectors.groupingBy(item
                                                -> item.getWorkName(),
                                        LinkedHashMap::new,
                                        Collectors.toList())));

        Map<String, Map<String, List<WorkKanbanPropertyInfoEntity>>> workKanbanPropertyElementsList = new HashMap<>();
        // カンバン + 工程順毎
        for (Map.Entry<String, Map<String, List<WorkKanbanInfoEntity>>> kanbanEntry : workKanbanGroupList.entrySet()) {
            Map<String, List<WorkKanbanPropertyInfoEntity>> workKanbanPropElementsMap = new HashMap<>();
            // 工程毎
            for (Map.Entry<String, List<WorkKanbanInfoEntity>> workEntry : kanbanEntry.getValue().entrySet()) {
                List<WorkKanbanPropertyInfoEntity> workKanbanPropertyElements
                        = new ArrayList<>(workEntry.getValue()
                        .stream()
                        .map(WorkKanbanInfoEntity::getPropertyCollection)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(WorkKanbanPropertyInfoEntity::getWorkKanbanPropName, Function.identity(), (a, b) -> a, LinkedHashMap::new))
                        .values());

                workKanbanPropElementsMap.put(workEntry.getKey(), workKanbanPropertyElements);
            }
            workKanbanPropertyElementsList.put(kanbanEntry.getKey(), workKanbanPropElementsMap);
        }

        return Optional.of(workKanbanPropertyElementsList);
    }

    /**
     * カンバンcsvファイル読込
     *
     * @param folder フォルダ
     * @param kanbanHeaderFormatInfo 設定情報
     */
    private Optional<Map<String, KanbanInfoEntity>> loadKanbanCsvFile(String folder, KanbanHeaderFormatInfo kanbanHeaderFormatInfo) {
        if (Objects.isNull(kanbanHeaderFormatInfo)) {
            return Optional.empty();
        }

        // エンコード
        final String kanbanEncode = kanbanHeaderFormatInfo.getHeaderCsvFileEncode();
        if (StringUtils.isEmpty(kanbanEncode)) {
            return Optional.empty();
        }
        // ファイル名
        final String kanbanFileName = kanbanHeaderFormatInfo.getHeaderCsvFileName();
        if (StringUtils.isEmpty(kanbanFileName)) {
            return Optional.of(new HashMap<>());
        }
        // ヘッダ行数
        final Integer headerRow = integerValueOf(kanbanHeaderFormatInfo.getHeaderCsvHeaderRow());
        if (Objects.isNull(headerRow)) {
            return Optional.empty();
        }
        // 読込開始行数
        final Integer startRow = integerValueOf(kanbanHeaderFormatInfo.getHeaderCsvStartRow());
        if (Objects.isNull(startRow)) {
            return Optional.empty();
        }
        if (headerRow >= startRow) {
            return Optional.empty();
        }

        List<String> headerList = new ArrayList<>();

        // カンバン階層
        final List<String> hierarchyNameHeader
                = kanbanHeaderFormatInfo
                .getHeaderCsvKanbanHierarchyNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (hierarchyNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(hierarchyNameHeader);

        // カンバン名
        final List<String> kanbanNameHeader
                = kanbanHeaderFormatInfo
                .getHeaderCsvKanbanNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (kanbanNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(kanbanNameHeader);

        // 工程順名
        final List<String> workflowNameHeader
                = kanbanHeaderFormatInfo
                .getHeaderCsvWorkflowNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (workflowNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(workflowNameHeader);

        // 工程順リビジョン
        final String workflowRevHeader = kanbanHeaderFormatInfo.getHeaderCsvWorkflowRev();
        headerList.add(workflowRevHeader);

        // モデル名
        final List<String> modelNameHeader
                = kanbanHeaderFormatInfo
                .getHeaderCsvModelNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        headerList.addAll(modelNameHeader);

        // 製造番号
        final List<String> productionNumberHeader
                = kanbanHeaderFormatInfo
                .getHeaderCsvProductNumNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        headerList.addAll(productionNumberHeader);

        // 製造開始日
        final String startDateTimeHeader = kanbanHeaderFormatInfo.getHeaderCsvStartDateTime();
        headerList.add(startDateTimeHeader);

        // 生産タイプ
        final String productionTypeHeader = kanbanHeaderFormatInfo.getHeaderCsvProductionType();
        headerList.add(productionTypeHeader);

        // ロット数量
        final String lotNumHeader = kanbanHeaderFormatInfo.getHeaderCsvLotNum();
        headerList.add(lotNumHeader);

        // カンバンステータス（初期値）
        final KanbanStatusEnum kanbanStatus = kanbanHeaderFormatInfo.getKanbanInitStatus();

        addResult(LocaleUtils.getString("key.import.work.plan.kanban") + " [" + kanbanFileName + "]");
        // csvファイル読込
        final Optional<List<Map<String, String>>> optKanbanCsvData = readCsvFile(folder, kanbanFileName, kanbanEncode, headerRow, startRow);
        if (!optKanbanCsvData.isPresent()) {
            return Optional.empty();
        }
        final List<Map<String, String>> kanbanCsvData = optKanbanCsvData.get();
        if (kanbanCsvData.isEmpty()) {
            return Optional.of(new HashMap<>());
        }

        final Map<String, String> firstWorkCsvData = kanbanCsvData.get(0);
        final List<String> checkIncludeHeader
                = headerList
                .stream()
                .filter(StringUtils::nonEmpty)
                .filter(header -> !firstWorkCsvData.containsKey(header))
                .collect(toList());

        if (!checkIncludeHeader.isEmpty()) {
            checkIncludeHeader
                    .forEach(header -> {
                        this.addResult(String.format(LocaleUtils.getString("key.NotFoundHeaderColumn"), LocaleUtils.getString("key.import.production.kanba"), header));
                    });
            return Optional.empty();
        }



        List<String> warningMessage = new ArrayList<>();
        List<String> errorMessage = new ArrayList<>();
        Date now = new Date();
        String nowString = defaultDateFormat.format(now);
        final List<BiConsumer<Map<String, String>, KanbanInfoEntity>> consumers
                = Arrays.asList
                (
                        createConsumer(hierarchyNameHeader, kanbanHeaderFormatInfo.getHeaderCsvHierarchyDelimiter(), "", KanbanInfoEntity::setParentName),          // カンバン階層
                        createConsumer(kanbanNameHeader, kanbanHeaderFormatInfo.getHeaderCsvKanbanDelimiter(), "", KanbanInfoEntity::setKanbanName),                // カンバン名
                        createConsumer(workflowNameHeader, kanbanHeaderFormatInfo.getHeaderCsvWorkflowDelimiter(), "", KanbanInfoEntity::setWorkflowName),          // 工程順名
                        createConsumer((kanbanInfoEntity, item) -> {    // 工程順リビジョン
                            if (StringUtils.isEmpty(item.get(workflowRevHeader))) {
                                return;
                            }
                            Optional<Integer> optWorkflowRev = toInteger(item.get(workflowRevHeader));
                            if (optWorkflowRev.isPresent()) {
                                kanbanInfoEntity.setWorkflowRev(optWorkflowRev.get());
                            } else {
                                // 工程順リビジョンに数値以外が入力されている場合
                                errorMessage.add(String.format("[%s] > %s",
                                        item.get(ROW_NUM_COLUMN),
                                        LocaleUtils.getString("key.ImportKanban_WorkflowRevIsNotNum")));
                            }
                        }),
                        createConsumer(modelNameHeader, kanbanHeaderFormatInfo.getHeaderCsvModelDelimiter(), "", KanbanInfoEntity::setModelName),                   // モデル名
                        createConsumer(productionNumberHeader, kanbanHeaderFormatInfo.getHeaderCsvProductDelimiter(), "", KanbanInfoEntity::setProductionNumber),   // 製造番号
                        createConsumer((kanbanInfoEntity, item) -> {    // 開始予定日時
                            if (StringUtils.isEmpty(item.get(startDateTimeHeader))) {
                                return;
                            }

                            Optional<Date> optStartDateTime = toDate(item.getOrDefault(startDateTimeHeader, nowString));
                            if (optStartDateTime.isPresent()) {
                                kanbanInfoEntity.setStartDatetime(optStartDateTime.get());
                            } else {
                                // 開始予定日時に日付以外が入力されている場合
                                errorMessage.add(String.format("[%s] > %s",
                                        item.get(ROW_NUM_COLUMN),
                                        LocaleUtils.getString("key.ImportKanban_StartDateTimeIsNotDate")));
                            }
                        }),
                        createConsumer((kanbanInfoEntity, item) -> {    // 生産タイプ
                            Optional<Integer> optProductionType = toInteger(item.getOrDefault(productionTypeHeader, "0"));
                            if (optProductionType.isPresent()) {
                                kanbanInfoEntity.setProductionType(optProductionType.get());
                            } else {
                                // 生産タイプに数値以外が入力されている場合
                                errorMessage.add(String.format("[%s] > %s",
                                        item.get(ROW_NUM_COLUMN),
                                        LocaleUtils.getString("key.ImportKanban_ProductTypeIsNotNum")));
                            }
                        }),
                        createConsumer((kanbanInfoEntity, item) -> {    // ロット数量
                            Optional<Integer> optLotQuantity = toInteger(item.getOrDefault(lotNumHeader, "1"));
                            if (optLotQuantity.isPresent()) {
                                kanbanInfoEntity.setLotQuantity(optLotQuantity.get());
                            } else {
                                // ロット数量に数値以外が入力されている場合
                                errorMessage.add(String.format("[%s] > %s",
                                        item.get(ROW_NUM_COLUMN),
                                        LocaleUtils.getString("key.ImportKanban_LotQuantityIsNotNum")));
                            }
                        }),
                        createDefaultConsumer(kanbanStatus, KanbanInfoEntity::setKanbanStatus),  // カンバンステータス（初期値）
                        createDefaultConsumer(loginUserInfoEntity.getId(), KanbanInfoEntity::setFkUpdatePersonId),
                        createDefaultConsumer(now, KanbanInfoEntity::setUpdateDatetime)
                );


        // 各種情報を取出し
        final List<KanbanInfoEntity> kanbanInfoEntities
                = kanbanCsvData
                .stream()
                .map(item -> {
                    KanbanInfoEntity kanbanInfoEntity = new KanbanInfoEntity();
                    consumers.forEach(consumer -> consumer.accept(item, kanbanInfoEntity));
                    return kanbanInfoEntity;
                })
                .collect(toList());

        int line = startRow;
        for (KanbanInfoEntity kanbanInfoEntity : kanbanInfoEntities) {
            // カンバン階層名のチェック
            if (StringUtils.isEmpty(kanbanInfoEntity.getParentName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.KanbanHierarch")));
            } else {
                if (this.countByteLength(kanbanInfoEntity.getParentName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.KanbanHierarch")));
                        kanbanInfoEntity.setParentName(this.cutOver(kanbanInfoEntity.getParentName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.KanbanHierarch")));
                    }
                }
            }
            // カンバン名のチェック
            if (StringUtils.isEmpty(kanbanInfoEntity.getKanbanName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.KanbanName")));
            } else {
                if (this.countByteLength(kanbanInfoEntity.getKanbanName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.KanbanName")));
                        kanbanInfoEntity.setKanbanName(this.cutOver(kanbanInfoEntity.getKanbanName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.KanbanName")));
                    }
                }
            }
            // 工程順名のチェック
            if (StringUtils.isEmpty(kanbanInfoEntity.getWorkflowName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.OrderProcessesName")));
            } else {
                if (this.countByteLength(kanbanInfoEntity.getWorkflowName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.OrderProcessesName")));
                        kanbanInfoEntity.setWorkflowName(this.cutOver(kanbanInfoEntity.getWorkflowName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.OrderProcessesName")));
                    }
                }
            }

            if (this.countByteLength(kanbanInfoEntity.getModelName()) > MAX_CHAR) {
                if (isCutItemNameOver) {
                    warningMessage.add(String.format("[%d] > %s [%s]", line,
                            String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.ModelName")));
                    kanbanInfoEntity.setModelName(this.cutOver(kanbanInfoEntity.getModelName(), MAX_CHAR));
                } else {
                    errorMessage.add(String.format("[%d] > %s [%s]", line,
                            LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.ModelName")));
                }
            }

            if (this.countByteLength(kanbanInfoEntity.getProductionNumber()) > MAX_PRODUCTION_NUM) {
                if (isCutItemNameOver) {
                    warningMessage.add(String.format("[%d] > %s [%s]", line,
                            String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_PRODUCTION_NUM), LocaleUtils.getString("key.ProductionNumber")));
                    kanbanInfoEntity.setProductionNumber(this.cutOver(kanbanInfoEntity.getProductionNumber(), MAX_PRODUCTION_NUM));
                } else {
                    errorMessage.add(String.format("[%d] > %s [%s]", line,
                            String.format(LocaleUtils.getString("key.warn.enterCharacters"), MAX_PRODUCTION_NUM), LocaleUtils.getString("key.ProductionNumber")));
                }
            }

            ++line;
        }

        warningMessage.forEach(this::addResult);
        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        // 重複削除(カンバン名 + 工程順 + 版数)
        Map<String, Map<String, List<KanbanInfoEntity>>> kanbanGroupList =
                kanbanInfoEntities
                        .stream()
                        .collect(
                                Collectors.groupingBy(item
                                                -> item.getKanbanName() + "####"
                                                + item.getWorkflowName() + "####"
                                                + item.getWorkflowRev(),
                                        Collectors.groupingBy(item
                                                        -> item.getParentName() + "####"
                                                        + item.getModelName() + "####"
                                                        + item.getProductionType() + "####"
                                                        + item.getLotQuantity() + "####"
                                                        + item.getProductionNumber(),
                                                Collectors.toList())));

        Map<String, KanbanInfoEntity> kanbanElementsList = new HashMap<>();
        for (Map.Entry<String, Map<String, List<KanbanInfoEntity>>> entry : kanbanGroupList.entrySet()) {
            List<List<KanbanInfoEntity>> items = new ArrayList<>(entry.getValue().values());
            if (items.size() != 1) {
                errorMessage.add(String.format(" > %s [%s: %s][%s: %s][%s: %s]",
                        LocaleUtils.getString("key.ImportKanban_DuplicationDataDif"),
                        LocaleUtils.getString("key.KanbanName"), items.get(0).get(0).getKanbanName(),
                        LocaleUtils.getString("key.WorkflowName"), items.get(0).get(0).getWorkflowName(),
                        LocaleUtils.getString("key.WorkflowRev"), items.get(0).get(0).getWorkflowRev()));
                continue;
            }

            KanbanInfoEntity kanbanInfoEntity
                    = items.get(0)
                    .stream()
                    .filter(item -> Objects.nonNull(item.getStartDatetime()))
                    .min(Comparator.comparing(KanbanInfoEntity::getStartDatetime))
                    .orElse(items.get(0).get(0));

            kanbanElementsList.put(entry.getKey(), kanbanInfoEntity);
        }

        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        return Optional.of(kanbanElementsList);

    }

    /**
     * カンバンプロパティ情報csvファイル読込
     *
     * @param folder フォルダ
     * @param kanbanPropHeaderFormatInfo 設定情報
     */
    private Optional<Map<String, List<KanbanPropertyInfoEntity>>> loadKanbanPropCsvFile(String folder, KanbanPropHeaderFormatInfo kanbanPropHeaderFormatInfo) {
        if (Objects.isNull(kanbanPropHeaderFormatInfo)) {
            return Optional.empty();
        }

        // エンコード
        final String kanbanEncode = kanbanPropHeaderFormatInfo.getHeaderCsvFileEncode();
        if (StringUtils.isEmpty(kanbanEncode)) {
            return Optional.empty();
        }
        // ファイル名
        final String kanbanFileName = kanbanPropHeaderFormatInfo.getHeaderCsvFileName();
        if (StringUtils.isEmpty(kanbanFileName)) {
            return Optional.of(new HashMap<>());
        }
        // ヘッダ行数
        final Integer headerRow = integerValueOf(kanbanPropHeaderFormatInfo.getHeaderCsvHeaderRow());
        if (Objects.isNull(headerRow)) {
            return Optional.empty();
        }
        // 読込開始行数
        final Integer startRow = integerValueOf(kanbanPropHeaderFormatInfo.getHeaderCsvStartRow());
        if (Objects.isNull(startRow)) {
            return Optional.empty();
        }
        if (headerRow >= startRow) {
            return Optional.empty();
        }

        List<String> headerList = new ArrayList<>();

        // カンバン名
        final List<String> kanbanNameHeader
                = kanbanPropHeaderFormatInfo
                .getHeaderCsvKanbanNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (kanbanNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(kanbanNameHeader);

        // 工程順名
        final List<String> workflowNameHeader
                = kanbanPropHeaderFormatInfo
                .getHeaderCsvWorkflowNames()
                .stream()
                .filter(StringUtils::nonEmpty)
                .collect(Collectors.toList());
        if (workflowNameHeader.isEmpty()) {
            return Optional.empty();
        }
        headerList.addAll(workflowNameHeader);

        // 工程順リビジョン
        final String workflowRevHeader = kanbanPropHeaderFormatInfo.getHeaderCsvWorkflowRev();
        headerList.add(workflowRevHeader);

        // プロパティ
        final List<PropHeaderFormatInfo> propValueHeaders = kanbanPropHeaderFormatInfo.getHeaderCsvPropValues();
        headerList.addAll(propValueHeaders.stream().map(PropHeaderFormatInfo::getPropValue).collect(toList()));

        addResult(LocaleUtils.getString("key.import.work.plan.kanban.property") + " [" + kanbanFileName + "]");
        // csvファイル読込
        final Optional<List<Map<String, String>>> optKanbanCsvData = readCsvFile(folder, kanbanFileName, kanbanEncode, headerRow, startRow);
        if (!optKanbanCsvData.isPresent()) {
            return Optional.empty();
        }
        final List<Map<String, String>> kanbanCsvData = optKanbanCsvData.get();
        if (kanbanCsvData.isEmpty()) {
            return Optional.of(new HashMap<>());
        }

        final Map<String, String> firstWorkCsvData = kanbanCsvData.get(0);
        final List<String> checkIncludeHeader
                = headerList
                .stream()
                .filter(StringUtils::nonEmpty)
                .filter(header -> !firstWorkCsvData.containsKey(header))
                .collect(toList());

        if (!checkIncludeHeader.isEmpty()) {
            checkIncludeHeader
                    .forEach(header -> {
                        this.addResult(String.format(LocaleUtils.getString("key.NotFoundHeaderColumn"), LocaleUtils.getString("key.import.production.kanba.property"), header));
                    });
            return Optional.empty();
        }

        // 各種情報を取出し
        List<String> warningMessage = new ArrayList<>();
        List<String> errorMessage = new ArrayList<>();
        final List<BiConsumer<Map<String, String>, KanbanInfoEntity>> consumers = new ArrayList<BiConsumer<Map<String, String>, KanbanInfoEntity>>();
        consumers.addAll(
                Arrays.asList
                (       // カンバン名
                        createConsumer(kanbanNameHeader, kanbanPropHeaderFormatInfo.getHeaderCsvKanbanDelimiter(), "", KanbanInfoEntity::setKanbanName),
                        // 工程順名
                        createConsumer(workflowNameHeader, kanbanPropHeaderFormatInfo.getHeaderCsvWorkflowDelimiter(), "", KanbanInfoEntity::setWorkflowName),
                        // 工程順リビジョン
                        createConsumer((kanbanInfoEntity, item) -> {
                            if (StringUtils.isEmpty(item.get(workflowRevHeader))) {
                                return;
                            }
                            Optional<Integer> optWorkflowRev = toInteger(item.get(workflowRevHeader));
                            if (optWorkflowRev.isPresent()) {
                                kanbanInfoEntity.setWorkflowRev(optWorkflowRev.get());
                            } else {
                                // 工程順リビジョンに数値以外が入力されている場合
                                errorMessage.add(String.format("[%s] > %s",
                                        item.get(ROW_NUM_COLUMN),
                                        LocaleUtils.getString("key.ImportKanban_WorkflowRevIsNotNum")));
                            }
                        })
                )
        );

        // プロパティ設定
        propValueHeaders
                .stream()
                .filter(header -> StringUtils.nonEmpty(header.getPropValue()))
                .forEach(header -> {
                    consumers.add(createConsumer(header.getPropValue(), "", (entity, val) -> {
                        KanbanPropertyInfoEntity tmp = new KanbanPropertyInfoEntity();
                        // 名称
                        if (StringUtils.isEmpty(header.getPropName())) {
                            tmp.setKanbanPropertyName(header.getPropValue());
                        } else {
                            tmp.setKanbanPropertyName(header.getPropName());
                        }
                        tmp.setKanbanPropertyType(header.getPropertyType());   // データ種
                        tmp.setKanbanPropertyValue(val); // 値
                        entity.addProperty(tmp);
                    }));
                });

        final List<KanbanInfoEntity> kanbanInfoEntities
                = kanbanCsvData
                .stream()
                .map(item -> {
                    KanbanInfoEntity kanbanInfoEntity = new KanbanInfoEntity();
                    consumers.forEach(consumer -> consumer.accept(item, kanbanInfoEntity));
                    return kanbanInfoEntity;
                })
                .collect(toList());

        int line = startRow;
        for (KanbanInfoEntity kanbanInfoEntity : kanbanInfoEntities) {
            // カンバン名のチェック
            if (StringUtils.isEmpty(kanbanInfoEntity.getKanbanName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.KanbanName")));
            } else {
                if (this.countByteLength(kanbanInfoEntity.getKanbanName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.KanbanName")));
                        kanbanInfoEntity.setKanbanName(this.cutOver(kanbanInfoEntity.getKanbanName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.KanbanName")));
                    }
                }
            }

            // 工程順名のチェック
            if (StringUtils.isEmpty(kanbanInfoEntity.getWorkflowName())) {
                errorMessage.add(String.format("[%d] > %s [%s]", line,
                        LocaleUtils.getString("key.RequiredItemNotExist"), LocaleUtils.getString("key.OrderProcessesName")));
            } else {
                if (this.countByteLength(kanbanInfoEntity.getWorkflowName()) > MAX_CHAR) {
                    if (isCutItemNameOver) {
                        warningMessage.add(String.format("[%d] > %s [%s]", line,
                                String.format(LocaleUtils.getString("key.warn.CutOverCharacters"), MAX_CHAR), LocaleUtils.getString("key.OrderProcessesName")));
                        kanbanInfoEntity.setWorkflowName(this.cutOver(kanbanInfoEntity.getWorkflowName(), MAX_CHAR));
                    } else {
                        errorMessage.add(String.format("[%d] > %s [%s]", line,
                                LocaleUtils.getString("key.warn.enterCharacters256"), LocaleUtils.getString("key.OrderProcessesName")));
                    }
                }
            }
            ++line;
        }

        warningMessage.forEach(this::addResult);
        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return Optional.empty();
        }

        // 重複削除(カンバン名 + 工程順 + 版数)
        Map<String, List<KanbanInfoEntity>> kanbanGroupList =
                kanbanInfoEntities
                        .stream()
                        .collect(
                                Collectors.groupingBy(item
                                                -> item.getKanbanName() + "####"
                                                + item.getWorkflowName() + "####"
                                                + item.getWorkflowRev(),
                                                Collectors.toList()));

        Map<String, List<KanbanPropertyInfoEntity>> kanbanPropertyElementsList = new HashMap<>();
        for (Map.Entry<String, List<KanbanInfoEntity>> entry : kanbanGroupList.entrySet()) {
            List<KanbanPropertyInfoEntity> kanbanPropertyElements
                    = new ArrayList<>(entry.getValue()
                    .stream()
                    .map(KanbanInfoEntity::getPropertyCollection)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(KanbanPropertyInfoEntity::getKanbanPropertyName, Function.identity(), (a,b) -> a, LinkedHashMap::new))
                    .values());

            kanbanPropertyElementsList.put( entry.getKey(), kanbanPropertyElements);
        }

        return Optional.of(kanbanPropertyElementsList);
    }

    /**
     * カンバン情報をExcelファイルから取得する。
     *
     * @param fileName Excelファイル名
     * @param formatInfo カンバンのフォーマット情報
     * @return カンバン情報 インポート用データ一覧
     */
    private List<ImportKanbanCsv> getExcelKanban(String fileName, KanbanFormatInfo formatInfo) {
        logger.info("getExcelKanban start.");
        List<ImportKanbanCsv> values = null;
        SimpleDateFormat datetimeFormatter = new SimpleDateFormat(ProductionNaviPropertyConstants.DEFAULT_DATETIME_FORMAT);

        // シート名
        String sheetName = formatInfo.getXlsSheetName();

        // 開始行
        int startRow = StringUtils.parseInteger(formatInfo.getXlsStartRow());
        // カンバン階層名
        String strHierarchyIdx = formatInfo.getXlsHierarchyName();
        int idxKanbanHierarchy = StringUtils.isEmpty(strHierarchyIdx) ? -1 : CellReference.convertColStringToIndex(strHierarchyIdx) + 1;
        // カンバン名
        String strNameIdx = formatInfo.getXlsKanbanName();
        int idxKanbanName = StringUtils.isEmpty(strNameIdx) ? -1 : CellReference.convertColStringToIndex(strNameIdx) + 1;
        // 工程順名
        String strWorkIdx = formatInfo.getXlsWorkflowName();
        int idxWorkName = StringUtils.isEmpty(strWorkIdx) ? -1 : CellReference.convertColStringToIndex(strWorkIdx) + 1;
        // 工程順版数
        String strCountIdx = formatInfo.getXlsWorkflowRev();
        int idxWorkCount = StringUtils.isEmpty(strCountIdx) ? -1 : CellReference.convertColStringToIndex(strCountIdx) + 1;
        // モデル名
        String strModelIdx = formatInfo.getXlsModelName();
        int idxModelName = StringUtils.isEmpty(strModelIdx) ? -1 : CellReference.convertColStringToIndex(strModelIdx) + 1;
        // 開始日時
        String strStartIdx = formatInfo.getXlsStartDateTime();
        int idxStartDateTime = StringUtils.isEmpty(strStartIdx) ? -1 : CellReference.convertColStringToIndex(strStartIdx) + 1;
        // 生産タイプ
        String strProductionTypeIdx = formatInfo.getXlsProductionType();
        int idxProdType = StringUtils.isEmpty(strProductionTypeIdx) ? -1 : CellReference.convertColStringToIndex(strProductionTypeIdx) + 1;
        // ロット数量
        String strLotNumIdx = formatInfo.getXlsLotNum();
        int idxLotNum = StringUtils.isEmpty(strLotNumIdx) ? -1 : CellReference.convertColStringToIndex(strLotNumIdx) + 1;
        // 製造番号
        String strProductionNumberIdx = formatInfo.getXlsProductionNumber();
        int idxProductionNumber = StringUtils.isEmpty(strProductionNumberIdx) ? -1 : CellReference.convertColStringToIndex(strProductionNumberIdx) + 1;
        // 開始シリアル番号
        String strStartSerialIdx = formatInfo.getXlsStartSerial();
        int idxStartSerial = StringUtils.isEmpty(strStartSerialIdx) ? -1 : CellReference.convertColStringToIndex(strStartSerialIdx) + 1;
        // 終了シリアル番号
        String strEndSerialIdx = formatInfo.getXlsEndSerial();
        int idxEndSerial = StringUtils.isEmpty(strEndSerialIdx) ? -1 : CellReference.convertColStringToIndex(strEndSerialIdx) + 1;
        // 標準作業時間
        int idxCycleTime = Boolean.valueOf(AdProperty.getProperties().getProperty("enableCycleTimeImport", "false")) 
                ? StringUtils.isEmpty(formatInfo.getXlsCycleTime()) ? -1 : CellReference.convertColStringToIndex(formatInfo.getXlsCycleTime()) + 1 
                : -1;

        List<Integer> colIds = Arrays.asList(
                idxKanbanHierarchy,
                idxKanbanName,
                idxWorkName,
                idxWorkCount,
                idxModelName,
                idxStartDateTime,
                idxProdType,
                idxLotNum,
                idxProductionNumber,
                idxStartSerial,
                idxEndSerial,
                idxCycleTime
        );

        int maxIdx = colIds.stream().mapToInt(p -> p).max().getAsInt();

        int count = 0;
        List<String> cols = null;
        try {
            WorkPlanExcelFileUtils excelFileUtils = new WorkPlanExcelFileUtils(datetimeFormatter);
            List<List<String>> rows = excelFileUtils.readExcel(fileName, sheetName, startRow, maxIdx, null);

            if (Objects.nonNull(rows)) {
                values = new ArrayList();
                for (count = 0; count < rows.size(); count++) {
                    cols = rows.get(count);

                    ImportKanbanCsv data = new ImportKanbanCsv();

                    // カンバン階層
                    if (formatInfo.getIsCheckKanbanHierarchy()) {
                        // カンバン階層を指定する場合、フォーマット情報のカンバン階層名をセットする。
                        data.setKanbanHierarchyName(formatInfo.getKanbanHierarchyName());
                    } else if (idxKanbanHierarchy > 0 && idxKanbanHierarchy <= cols.size()) {
                        data.setKanbanHierarchyName(Objects.isNull(cols.get(idxKanbanHierarchy - 1)) ? "" : cols.get(idxKanbanHierarchy - 1));
                    }
                    // カンバン名
                    if (idxKanbanName > 0 && idxKanbanName <= cols.size()) {
                        data.setKanbanName(Objects.isNull(cols.get(idxKanbanName - 1)) ? "" : cols.get(idxKanbanName - 1));
                    }
                    // 工程順名
                    if (idxWorkName > 0 && idxWorkName <= cols.size()) {
                        // モデル名で工程順を指定しない場合、工程順名をセットする。
                        if (!formatInfo.getIsCheckWorkflowRegex()) {
                            data.setWorkflowName(Objects.isNull(cols.get(idxWorkName - 1)) ? "" : cols.get(idxWorkName - 1));
                        }
                    }
                    // 工程順版数
                    if (idxWorkCount > 0 && idxWorkCount <= cols.size()) {
                        // モデル名で工程順を指定しない場合、版数をセットする。
                        if (!formatInfo.getIsCheckWorkflowRegex()) {
                            // colの値はDouble型を文字列にした値なので、整数の文字列に変換してセットする。
                            data.setWorkflowRev(StringUtils.isEmpty(cols.get(idxWorkCount - 1)) ? "" : doubleStringToIntString(cols.get(idxWorkCount - 1)));
                        }
                    }
                    // モデル名
                    if (idxModelName > 0 && idxModelName <= cols.size()) {
                        data.setModelName(Objects.isNull(cols.get(idxModelName - 1)) ? "" : cols.get(idxModelName - 1));

                        // モデル名で工程順を指定する場合、条件に合う工程順名と版数をセットする。
                        if (formatInfo.getIsCheckWorkflowRegex()) {
                            for (WorkflowRegexInfo regexInfo : formatInfo.getWorkflowRegexInfos()) {
                                if (data.getModelName().matches(regexInfo.getRegex())) {
                                    data.setWorkflowName(regexInfo.getWorkflowName());
                                    data.setWorkflowRev(String.valueOf(regexInfo.getWorkflowRev()));
                                    break;
                                }
                            }
                        }
                    }
                    // 作業開始日時
                    if (idxStartDateTime > 0 && idxStartDateTime <= cols.size()) {
                        data.setStartDatetime(Objects.isNull(cols.get(idxStartDateTime - 1)) ? "" : cols.get(idxStartDateTime - 1));
                    }

                    // 生産タイプ
                    if (idxProdType > 0 && idxProdType <= cols.size()) {
                        String value = cols.get(idxProdType - 1);
                        int productionType = 0;

                        try {
                            productionType = DataParser.parseProductionType(value);
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                            this.addResult(String.format("[%d] > %s [%s]", count, LocaleUtils.getString("key.invalid.value"), LocaleUtils.getString("key.ProductionType")));
                        }

                        data.setProductionType(String.valueOf(productionType));
                    }

                    // ロット数量
                    if (idxLotNum > 0 && idxLotNum <= cols.size()) {
                        String value = cols.get(idxLotNum - 1);
                        int lotNum = 1;

                        try {
                            lotNum = DataParser.parseLotNum(value);
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                            this.addResult(String.format("[%d] > %s [%s]", count, LocaleUtils.getString("key.invalid.value"), LocaleUtils.getString("key.LotQuantity")));
                        }

                        data.setLotQuantity(String.valueOf(lotNum));
                    }

                    // 製造番号
                    if (idxProductionNumber > 0 && idxProductionNumber <= cols.size()) {
                        data.setProductionNumber(Objects.isNull(cols.get(idxProductionNumber - 1)) ? "" : cols.get(idxProductionNumber - 1));
                    }

                    // 開始シリアル番号
                    if (idxStartSerial > 0 && idxStartSerial <= cols.size() && !StringUtils.isEmpty(cols.get(idxStartSerial - 1))) {
                        data.setStartSerial(cols.get(idxStartSerial - 1));
                    }

                    // 終了シリアル番号
                    if (idxEndSerial > 0 && idxEndSerial <= cols.size() && !StringUtils.isEmpty(cols.get(idxEndSerial - 1))) {
                        data.setEndSerial(cols.get(idxEndSerial - 1));
                    }

                    // 標準作業時間
                    if (idxCycleTime > 0 && idxCycleTime <= cols.size() && !StringUtils.isEmpty(cols.get(idxCycleTime - 1))) {
                        if (NumberUtils.isNumber(cols.get(idxCycleTime - 1))) {
                            data.setCycleTime(cols.get(idxCycleTime - 1));
                        } else {
                            this.addResult(String.format("[%d] > %s [%s]", count, LocaleUtils.getString("key.invalid.value"), LocaleUtils.getString("key.StandardTime")));
                        }
                    }

                    // モデル名と工程順を組み合わせて検索する設定の場合は検索するときに結合させるためここでは設定のみ保持する
                    data.setEnableConcat(formatInfo.getIsCheckWorkflowWithModel());

                    values.add(data);
                }
            }

            // 読み込み日が指定されているならそれだけ読み込む
            if (Objects.nonNull(values)) {
                values = values.stream()
                        .filter(row -> isEnableDate(row.getStartDatetime()))
                        .collect(Collectors.toList());
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            values = null;
            this.addResult(ex.getLocalizedMessage());
            this.addResult("  >  " + (count + 1) + ", " + String.join(", ", cols));
        } finally {
            logger.info("getExcelKanban end.");
        }

        return values;
    }

    /**
     * カンバンプロパティ情報をExcelファイルから取得する。
     *
     * @param fileName Excelファイル名
     * @param formatInfo カンバンプロパティのフォーマット情報
     * @return カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportKanbanPropertyCsv> getExcelKanbanProps(String fileName, KanbanPropFormatInfo formatInfo) {
        if (Files.notExists(Paths.get(fileName))) {
            return null;
        }
        List<ImportKanbanPropertyCsv> importKanbanProps;

        if ("2".equals(formatInfo.getSelectedFormat())) {
            importKanbanProps = getExcelKanbanPropsF2(fileName, formatInfo);
        } else {
            importKanbanProps = getExcelKanbanPropsF1(fileName, formatInfo);
        }

        return importKanbanProps;
    }

    /**
     * カンバンプロパティ情報をExcelファイルから取得する。(フォーマットA)
     *
     * @param fileName Excelファイル名
     * @param formatInfo カンバンプロパティのフォーマット情報
     * @return カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportKanbanPropertyCsv> getExcelKanbanPropsF1(String fileName, KanbanPropFormatInfo formatInfo) {
        logger.info("getExcelKanbanPropsF1 start.");
        List<ImportKanbanPropertyCsv> values = null;
        SimpleDateFormat datetimeFormatter = new SimpleDateFormat(ProductionNaviPropertyConstants.DEFAULT_DATETIME_FORMAT);

        // シート名
        String sheetName = formatInfo.getXlsSheetName();

        // 開始行
        int lineStart = StringUtils.parseInteger(formatInfo.getXlsStartRow());
        // カンバン名
        String strNameIdx = formatInfo.getXlsKanbanName();
        int idxKanbanName = CellReference.convertColStringToIndex(strNameIdx) + 1;
        // 工程順名
        String strWorkflowNameIdx = formatInfo.getXlsWorkflowName();
        int idxWorkflowName = CellReference.convertColStringToIndex(strWorkflowNameIdx) + 1;
        // プロパティ名
        String strPropertyIdx = formatInfo.getXlsPropName();
        int idxProperty = CellReference.convertColStringToIndex(strPropertyIdx) + 1;
        // 型
        String strTypeIdx = formatInfo.getXlsPropType();
        int idxType = CellReference.convertColStringToIndex(strTypeIdx) + 1;
        // 値
        String strValueIdx = formatInfo.getXlsPropValue();
        int idxValue = CellReference.convertColStringToIndex(strValueIdx) + 1;

        int maxIdx = idxKanbanName > idxWorkflowName ? idxKanbanName : idxWorkflowName;
        maxIdx = maxIdx > idxProperty ? maxIdx : idxProperty;
        maxIdx = maxIdx > idxType ? maxIdx : idxType;
        maxIdx = maxIdx > idxValue ? maxIdx : idxValue;

        int count = 0;
        List<String> cols = null;
        try {
            WorkPlanExcelFileUtils excelFileUtils = new WorkPlanExcelFileUtils(datetimeFormatter);
            List<List<String>> rows = excelFileUtils.readExcel(fileName, sheetName, lineStart, maxIdx, null);

            if (Objects.nonNull(rows)) {
                values = new ArrayList();
                for (count = 0; count < rows.size(); count++) {
                    cols = rows.get(count);

                    ImportKanbanPropertyCsv data = new ImportKanbanPropertyCsv();
                    if (idxKanbanName > 0 && idxKanbanName <= cols.size()) {
                        data.setKanbanName(Objects.isNull(cols.get(idxKanbanName - 1)) ? "" : cols.get(idxKanbanName - 1));
                    }
                    if (idxWorkflowName > 0 && idxWorkflowName <= cols.size()) {
                        data.setWorkflowName(Objects.isNull(cols.get(idxWorkflowName - 1)) ? "" : cols.get(idxWorkflowName - 1));
                    }
                    if (idxProperty > 0 && idxProperty <= cols.size()) {
                        data.setKanbanPropertyName(Objects.isNull(cols.get(idxProperty - 1)) ? "" : cols.get(idxProperty - 1));
                    }
                    if (idxType > 0 && idxType <= cols.size()) {
                        data.setKanbanPropertyType(Objects.isNull(cols.get(idxType - 1)) ? "" : cols.get(idxType - 1));
                    }
                    if (idxValue > 0 && idxValue <= cols.size()) {
                        data.setKanbanPropertyValue(Objects.isNull(cols.get(idxValue - 1)) ? "" : cols.get(idxValue - 1));
                    }
                    values.add(data);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            values = null;
            this.addResult(ex.getLocalizedMessage());
            this.addResult("  >  " + (count + 1) + ", " + String.join(", ", cols));
        } finally {
            logger.info("getExcelKanbanPropsF1 end.");
        }

        return values;
    }

    /**
     * カンバンプロパティ情報をExcelファイルから取得する。(フォーマットB)
     *
     * @param fileName Excelファイル名
     * @param formatInfo カンバンプロパティのフォーマット情報
     * @return カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportKanbanPropertyCsv> getExcelKanbanPropsF2(String fileName, KanbanPropFormatInfo formatInfo) {
        logger.info("getExcelKanbanPropsF2 start.");
        List<ImportKanbanPropertyCsv> values = null;
        SimpleDateFormat datetimeFormatter = new SimpleDateFormat(ProductionNaviPropertyConstants.DEFAULT_DATETIME_FORMAT);

        // シート名
        String sheetName = formatInfo.getXlsSheetName();

        // ヘッダー行
        int headerRow = StringUtils.parseInteger(formatInfo.getF2XlsHeaderRow());

        // 読み込み開始行
        int lineStart = StringUtils.parseInteger(formatInfo.getF2XlsStartRow());

        // カンバン名
        String strNameIdx = formatInfo.getF2XlsKanbanName();
        int idxKanbanName = CellReference.convertColStringToIndex(strNameIdx) + 1;

        // 工程順名
        String strWorkflowNameIdx = formatInfo.getF2XlsWorkflowName();
        int idxWorkflowName = CellReference.convertColStringToIndex(strWorkflowNameIdx) + 1;

        int maxIdx = headerRow > lineStart ? headerRow : lineStart;
        maxIdx = maxIdx > idxKanbanName ? maxIdx : idxKanbanName;
        maxIdx = maxIdx > idxWorkflowName ? maxIdx : idxWorkflowName;

        // プロパティ
        List<Integer> idxProps = new ArrayList();
        for (String propColumn : formatInfo.getF2XlsPropValues()) {
            int idxProp = CellReference.convertColStringToIndex(propColumn) + 1;
            idxProps.add(idxProp);

            maxIdx = maxIdx > idxProp ? maxIdx : idxProp;
        }

        int count = 0;
        List<String> cols = null;
        try {
            WorkPlanExcelFileUtils excelFileUtils = new WorkPlanExcelFileUtils(datetimeFormatter);

            // ヘッダー行
            List<String> header = null;
            List<List<String>> rows = excelFileUtils.readExcel(fileName, sheetName, headerRow, maxIdx, 1);
            if (Objects.nonNull(rows)) {
                header = rows.get(0);
            }

            // データ行
            rows = excelFileUtils.readExcel(fileName, sheetName, lineStart, maxIdx, null);

            if (Objects.nonNull(rows)) {
                values = new ArrayList();
                for (count = 0; count < rows.size(); count++) {
                    cols = rows.get(count);

                    // カンバン名
                    if (idxKanbanName <= 0 || idxKanbanName > cols.size()) {
                        continue;
                    }
                    String kanbanName = Objects.isNull(cols.get(idxKanbanName - 1)) ? "" : cols.get(idxKanbanName - 1);

                    // 工程順名
                    String workflowName = "";
                    if (idxWorkflowName > 0 && idxWorkflowName <= cols.size()) {
                        workflowName = Objects.isNull(cols.get(idxWorkflowName - 1)) ? "" : cols.get(idxWorkflowName - 1);
                    }

                    for (Integer idxProp : idxProps) {
                        ImportKanbanPropertyCsv data = new ImportKanbanPropertyCsv();
                        data.setKanbanName(kanbanName);
                        data.setWorkflowName(workflowName);

                        if (idxProp <= 0 || idxProp > cols.size()) {
                            continue;
                        }

                        data.setKanbanPropertyName(header.get(idxProp - 1));
                        data.setKanbanPropertyType(CustomPropertyTypeEnum.TYPE_STRING.name());
                        data.setKanbanPropertyValue(Objects.isNull(cols.get(idxProp - 1)) ? "" : cols.get(idxProp - 1));

                        values.add(data);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            values = null;
            this.addResult(ex.getLocalizedMessage());
            this.addResult("  >  " + (count + 1) + ", " + String.join(", ", cols));
        } finally {
            logger.info("getExcelKanbanPropsF2 end.");
        }

        return values;
    }

    /**
     * 工程カンバン情報をExcelファイルから取得する。
     *
     * @param fileName Excelファイル名
     * @param formatInfo 工程カンバンのフォーマット情報
     * @return 工程カンバン情報 インポート用データ一覧
     */
    private List<ImportWorkKanbanCsv> getExcelWorkKanbans(String fileName, WorkKanbanFormatInfo formatInfo) {
        logger.info("getExcelWorkKanbans start.");
        List<ImportWorkKanbanCsv> values = null;
        SimpleDateFormat datetimeFormatter = new SimpleDateFormat(ProductionNaviPropertyConstants.DEFAULT_DATETIME_FORMAT);

        // シート名
        String sheetName = formatInfo.getXlsSheetName();

        // 開始行
        int lineStart = StringUtils.parseInteger(formatInfo.getXlsStartRow());
        // カンバン名
        String strNameIdx = formatInfo.getXlsKanbanName();
        int idxKanbanName = CellReference.convertColStringToIndex(strNameIdx) + 1;
        // 工程の番号
        String strNoIdx = formatInfo.getXlsWorkNum();
        int idxWorkNo = CellReference.convertColStringToIndex(strNoIdx) + 1;
        // スキップフラグ
        String strSkipIdx = formatInfo.getXlsSkipFlag();
        int idxSkip = CellReference.convertColStringToIndex(strSkipIdx) + 1;
        // 開始予定日時
        String strStartIdx = formatInfo.getXlsStartDateTime();
        int idxStartDateTime = CellReference.convertColStringToIndex(strStartIdx) + 1;
        // 完了予定日時
        String strStopIdx = formatInfo.getXlsCompDateTime();
        int idxStopDateTime = CellReference.convertColStringToIndex(strStopIdx) + 1;
        // 組織識別名
        String strOrganizationIdx = formatInfo.getXlsOrganizationIdentName();
        int idxOrganizations = CellReference.convertColStringToIndex(strOrganizationIdx) + 1;
        // 設備識別名
        String strEquipmentIdx = formatInfo.getXlsEquipmentIdentName();
        int idxEquipments = CellReference.convertColStringToIndex(strEquipmentIdx) + 1;
        // 工程名
        String strWorkNameIdx = formatInfo.getXlsWorkName();
        int idxWorkName = CellReference.convertColStringToIndex(strWorkNameIdx) + 1;
        // タクトタイム
        String strTactTimeIdx = formatInfo.getXlsTactTime();
        int idxTactTime = CellReference.convertColStringToIndex(strTactTimeIdx) + 1;

        int maxIdx = idxKanbanName > idxWorkNo ? idxKanbanName : idxWorkNo;
        maxIdx = maxIdx > idxSkip ? maxIdx : idxSkip;
        maxIdx = maxIdx > idxStartDateTime ? maxIdx : idxStartDateTime;
        maxIdx = maxIdx > idxStopDateTime ? maxIdx : idxStopDateTime;
        maxIdx = maxIdx > idxOrganizations ? maxIdx : idxOrganizations;
        maxIdx = maxIdx > idxEquipments ? maxIdx : idxEquipments;
        maxIdx = maxIdx > idxWorkName ? maxIdx : idxWorkName;
        maxIdx = maxIdx > idxTactTime ? maxIdx : idxTactTime;

        int count = 0;
        List<String> cols = null;
        try {
            WorkPlanExcelFileUtils excelFileUtils = new WorkPlanExcelFileUtils(datetimeFormatter);
            List<List<String>> rows = excelFileUtils.readExcel(fileName, sheetName, lineStart, maxIdx, null);

            if (Objects.nonNull(rows)) {
                values = new ArrayList();
                for (count = 0; count < rows.size(); count++) {
                    cols = rows.get(count);

                    ImportWorkKanbanCsv data = new ImportWorkKanbanCsv();
                    if (idxKanbanName > 0 && idxKanbanName <= cols.size()) {
                        data.setKanbanName(Objects.isNull(cols.get(idxKanbanName - 1)) ? "" : cols.get(idxKanbanName - 1));
                    }
                    if (idxWorkNo > 0 && idxWorkNo <= cols.size()) {
                        // colの値はDouble型を文字列にした値なので、整数の文字列に変換してセットする。
                        data.setWorkNum(StringUtils.isEmpty(cols.get(idxWorkNo - 1)) ? "" : doubleStringToIntString(cols.get(idxWorkNo - 1)));
                    }
                    if (idxSkip > 0 && idxSkip <= cols.size()) {
                        // colの値はDouble型を文字列にした値なので、整数の文字列に変換してセットする。
                        data.setSkipFlag(StringUtils.isEmpty(cols.get(idxSkip - 1)) ? "" : doubleStringToIntString(cols.get(idxSkip - 1)));
                    }
                    if (idxStartDateTime > 0 && idxStartDateTime <= cols.size()) {
                        data.setStartDatetime(Objects.isNull(cols.get(idxStartDateTime - 1)) ? "" : cols.get(idxStartDateTime - 1));
                    }

                    if (idxStopDateTime > 0 && idxStopDateTime <= cols.size()) {
                        data.setCompDatetime(Objects.isNull(cols.get(idxStopDateTime - 1)) ? "" : cols.get(idxStopDateTime - 1));
                    }
                    if (idxOrganizations > 0 && idxOrganizations <= cols.size()) {
                        data.setOrganizations(Objects.isNull(cols.get(idxOrganizations - 1)) ? "" : cols.get(idxOrganizations - 1));
                    }
                    if (idxEquipments > 0 && idxEquipments <= cols.size()) {
                        data.setEquipments(Objects.isNull(cols.get(idxEquipments - 1)) ? "" : cols.get(idxEquipments - 1));
                    }
                    if (idxWorkName > 0 && idxWorkName <= cols.size()) {
                        data.setWorkName(Objects.isNull(cols.get(idxWorkName - 1)) ? "" : cols.get(idxWorkName - 1));
                    }
                    if (idxTactTime > 0 && idxTactTime <= cols.size()) {
                        // colの値はHH:mm:ssを文字列にした値なので、秒数の文字列に変換してセットする。
                        data.setTactTime(StringUtils.isEmpty(cols.get(idxTactTime - 1)) ? "" : toSecond(cols.get(idxTactTime - 1)));
                    }
                    values.add(data);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            values = null;
            this.addResult(ex.getLocalizedMessage());
            this.addResult("  >  " + (count + 1) + ", " + String.join(", ", cols));
        } finally {
            logger.info("getExcelWorkKanbans end.");
        }

        return values;
    }

    /**
     * 工程カンバンプロパティ情報をExcelファイルから取得する。
     *
     * @param fileName Excelファイル名
     * @param formatInfo 工程カンバンプロパティのフォーマット情報
     * @return 工程カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportWorkKanbanPropertyCsv> getExcelWorkKanbanProperty(String fileName, WorkKanbanPropFormatInfo formatInfo) {
        if (Files.notExists(Paths.get(fileName))) {
            return null;
        }
        List<ImportWorkKanbanPropertyCsv> importWkKanbanProps;

        if ("2".equals(formatInfo.getSelectedFormat())) {
            importWkKanbanProps = getExcelWorkKanbanPropertyF2(fileName, formatInfo);
        } else {
            importWkKanbanProps = getExcelWorkKanbanPropertyF1(fileName, formatInfo);
        }

        return importWkKanbanProps;
    }

    /**
     * 工程カンバンプロパティ情報をExcelファイルから取得する。(フォーマットA)
     *
     * @param fileName Excelファイル名
     * @param formatInfo 工程カンバンプロパティのフォーマット情報
     * @return 工程カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportWorkKanbanPropertyCsv> getExcelWorkKanbanPropertyF1(String fileName, WorkKanbanPropFormatInfo formatInfo) {
        logger.info("getExcelWorkKanbanPropertyF1 start.");
        List<ImportWorkKanbanPropertyCsv> values = null;
        SimpleDateFormat datetimeFormatter = new SimpleDateFormat(ProductionNaviPropertyConstants.DEFAULT_DATETIME_FORMAT);

        // シート名
        String sheetName = formatInfo.getXlsSheetName();

        // 開始行
        int lineStart = StringUtils.parseInteger(formatInfo.getXlsStartRow());
        // カンバン名
        String strNameIdx = formatInfo.getXlsKanbanName();
        int idxKanbanName = CellReference.convertColStringToIndex(strNameIdx) + 1;
        // 工程名
        String strWorkkNameIdx = formatInfo.getXlsWorkName();
        int idxWorkName = CellReference.convertColStringToIndex(strWorkkNameIdx) + 1;
        // 工程の番号
        String strNoIdx = formatInfo.getXlsWorkNum();
        int idxWorkNo = CellReference.convertColStringToIndex(strNoIdx) + 1;
        // プロパティ名
        String strPropIdx = formatInfo.getXlsPropName();
        int idxProperty = CellReference.convertColStringToIndex(strPropIdx) + 1;
        // 型
        String strTypeIdx = formatInfo.getXlsPropType();
        int idxType = CellReference.convertColStringToIndex(strTypeIdx) + 1;
        // 値
        String strValueIdx = formatInfo.getXlsPropValue();
        int idxValue = CellReference.convertColStringToIndex(strValueIdx) + 1;

        int maxIdx = idxKanbanName > idxWorkName ? idxKanbanName : idxWorkName;
        maxIdx = maxIdx > idxWorkNo ? maxIdx : idxWorkNo;
        maxIdx = maxIdx > idxProperty ? maxIdx : idxProperty;
        maxIdx = maxIdx > idxType ? maxIdx : idxType;
        maxIdx = maxIdx > idxValue ? maxIdx : idxValue;

        int count = 0;
        List<String> cols = null;
        try {
            WorkPlanExcelFileUtils excelFileUtils = new WorkPlanExcelFileUtils(datetimeFormatter);
            List<List<String>> rows = excelFileUtils.readExcel(fileName, sheetName, lineStart, maxIdx, null);

            if (Objects.nonNull(rows)) {
                values = new ArrayList();
                for (count = 0; count < rows.size(); count++) {
                    cols = rows.get(count);

                    ImportWorkKanbanPropertyCsv data = new ImportWorkKanbanPropertyCsv();
                    if (idxKanbanName > 0 && idxKanbanName <= cols.size()) {
                        data.setKanbanName(Objects.isNull(cols.get(idxKanbanName - 1)) ? "" : cols.get(idxKanbanName - 1));
                    }
                    if (idxWorkName > 0 && idxWorkName <= cols.size()) {
                        data.setWorkName(Objects.isNull(cols.get(idxWorkName - 1)) ? "" : cols.get(idxWorkName - 1));
                    }
                    if (idxWorkNo > 0 && idxWorkNo <= cols.size()) {
                        // colの値はDouble型を文字列にした値なので、整数の文字列に変換してセットする。
                        data.setWorkNum(StringUtils.isEmpty(cols.get(idxWorkNo - 1)) ? "" : doubleStringToIntString(cols.get(idxWorkNo - 1)));
                    }
                    if (idxProperty > 0 && idxProperty <= cols.size()) {
                        data.setWkKanbanPropName(Objects.isNull(cols.get(idxProperty - 1)) ? "" : cols.get(idxProperty - 1));
                    }
                    if (idxType > 0 && idxType <= cols.size()) {
                        data.setWkKanbanPropType(Objects.isNull(cols.get(idxType - 1)) ? "" : cols.get(idxType - 1));
                    }
                    if (idxValue > 0 && idxValue <= cols.size()) {
                        data.setWkKanbanPropValue(Objects.isNull(cols.get(idxValue - 1)) ? "" : cols.get(idxValue - 1));
                    }
                    values.add(data);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            values = null;
            this.addResult(ex.getLocalizedMessage());
            this.addResult("  >  " + (count + 1) + ", " + String.join(", ", cols));
        } finally {
            logger.info("getExcelWorkKanbanPropertyF1 end.");
        }

        return values;
    }

    /**
     * 工程カンバンプロパティ情報をExcelファイルから取得する。(フォーマットB)
     *
     * @param fileName Excelファイル名
     * @param formatInfo 工程カンバンプロパティのフォーマット情報
     * @return 工程カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportWorkKanbanPropertyCsv> getExcelWorkKanbanPropertyF2(String fileName, WorkKanbanPropFormatInfo formatInfo) {
        logger.info("getExcelWorkKanbanPropertyF2 start.");
        List<ImportWorkKanbanPropertyCsv> values = null;
        SimpleDateFormat datetimeFormatter = new SimpleDateFormat(ProductionNaviPropertyConstants.DEFAULT_DATETIME_FORMAT);

        // シート名
        String sheetName = formatInfo.getXlsSheetName();

        // ヘッダー行
        int headerRow = StringUtils.parseInteger(formatInfo.getF2XlsHeaderRow());

        // 読み込み開始行
        int lineStart = StringUtils.parseInteger(formatInfo.getF2XlsStartRow());

        // カンバン名
        String strNameIdx = formatInfo.getF2XlsKanbanName();
        int idxKanbanName = CellReference.convertColStringToIndex(strNameIdx) + 1;

        // 工程名
        String strWorkNameIdx = formatInfo.getF2XlsWorkName();
        int idxWorkName = CellReference.convertColStringToIndex(strWorkNameIdx) + 1;

        // 工程の番号
        String strWorkNumIdx = formatInfo.getF2XlsWorkNo();
        int idxWorkNum = CellReference.convertColStringToIndex(strWorkNumIdx) + 1;

        int maxIdx = headerRow > lineStart ? headerRow : lineStart;
        maxIdx = maxIdx > idxKanbanName ? maxIdx : idxKanbanName;
        maxIdx = maxIdx > idxWorkName ? maxIdx : idxWorkName;
        maxIdx = maxIdx > idxWorkNum ? maxIdx : idxWorkNum;

        // プロパティ
        List<Integer> idxProps = new ArrayList();
        for (String propColumn : formatInfo.getF2XlsPropValues()) {
            int idxProp = CellReference.convertColStringToIndex(propColumn) + 1;
            idxProps.add(idxProp);

            maxIdx = maxIdx > idxProp ? maxIdx : idxProp;
        }

        int count = 0;
        List<String> cols = null;
        HashMap<String, Integer> unionPropCountMap = new HashMap<String, Integer>();
        try {
            WorkPlanExcelFileUtils excelFileUtils = new WorkPlanExcelFileUtils(datetimeFormatter);

            // ヘッダー行
            List<String> header = null;
            List<List<String>> rows = excelFileUtils.readExcel(fileName, sheetName, headerRow, maxIdx, 1);
            if (Objects.nonNull(rows)) {
                header = rows.get(0);
            }

            // データ行
            rows = excelFileUtils.readExcel(fileName, sheetName, lineStart, maxIdx, null);

            if (Objects.nonNull(rows)) {
                values = new ArrayList();
                for (count = 0; count < rows.size(); count++) {
                    cols = rows.get(count);

                    String unionPropLeft = null;
                    String unionPropRight = null;

                    // カンバン名
                    if (idxKanbanName <= 0 || idxKanbanName > cols.size()) {
                        continue;
                    }
                    String kanbanName = Objects.isNull(cols.get(idxKanbanName - 1)) ? "" : cols.get(idxKanbanName - 1);

                    // 工程名
                    if (idxWorkName <= 0 || idxWorkName > cols.size()) {
                        continue;
                    }
                    String workName = Objects.isNull(cols.get(idxWorkName - 1)) ? "" : cols.get(idxWorkName - 1);

                    // 工程番号
                    if (idxWorkNum <= 0 || idxWorkNum > cols.size()) {
                        continue;
                    }
                    String workNum = Objects.isNull(cols.get(idxWorkNum - 1)) ? "" : cols.get(idxWorkNum - 1);

                    for (Integer idxProp : idxProps) {
                        //プロパティ名・プロパティ値
                        if (idxProp <= 0 || idxProp > cols.size()) {
                            continue;
                        }
                        String propName = header.get(idxProp - 1);
                        String propValue = Objects.isNull(cols.get(idxProp - 1)) ? "" : cols.get(idxProp - 1);

                        // プロパティを組み合わせて読み込む
                        if (formatInfo.getF2IsCheckUnionProp()) {
                            boolean targetPropFlg = false;
                            // 結合対象のプロパティなら一時格納
                            if (propName.equals(formatInfo.getF2UnionPropLeftName())) {
                                unionPropLeft = propValue;
                                targetPropFlg = true;
                            } else if (propName.equals(formatInfo.getF2UnionPropRightName())) {
                                unionPropRight = propValue;
                                targetPropFlg = true;
                            }

                            if (targetPropFlg) {
                                if (Objects.isNull(unionPropLeft) || Objects.isNull(unionPropRight)) {
                                    // 揃っていなければ次プロパティへ
                                    continue;
                                } else {
                                    // 組み合わせたプロパティの連番を記録
                                    String key = kanbanName + workName + workNum;
                                    int unionPropCount = 1;
                                    unionPropCountMap.get(key);
                                    if (unionPropCountMap.containsKey(key)) {
                                        unionPropCount += unionPropCountMap.get(key);
                                        unionPropCountMap.put(key, unionPropCount);
                                    } else {
                                        unionPropCountMap.put(key, unionPropCount);
                                    }
                                    // プロパティ名を新しいプロパティ名に変更(サフィックス：連番)
                                    propName = formatInfo.getF2UnionPropNewName() + unionPropCount;
                                    // プロパティ値を結合(デリミタ：\t)
                                    propValue = unionPropLeft + "\\t" + unionPropRight;
                                    unionPropLeft = unionPropRight = null;
                                }
                            }
                        }

                        ImportWorkKanbanPropertyCsv data = new ImportWorkKanbanPropertyCsv();
                        data.setKanbanName(kanbanName);
                        data.setWorkName(workName);
                        data.setWorkNum(workNum);
                        data.setWkKanbanPropName(propName);
                        data.setWkKanbanPropType(CustomPropertyTypeEnum.TYPE_STRING.name());
                        data.setWkKanbanPropValue(propValue);

                        values.add(data);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            values = null;
            this.addResult(ex.getLocalizedMessage());
            this.addResult("  >  " + (count + 1) + ", " + String.join(", ", cols));
        } finally {
            logger.info("getExcelWorkKanbanPropertyF2 end.");
        }

        return values;
    }

    /**
     * カンバンステータス情報をExcelファイルから取得する。
     *
     * @param fileName Excelファイル名
     * @param formatInfo カンバンステータスのフォーマット情報
     * @return カンバンステータス情報 インポート用データ一覧
     */
    private List<ImportKanbanStatusCsv> getExcelKanbanStatus(String fileName, KanbanStatusFormatInfo formatInfo) {
        logger.info("getExcelKanbanStatus start.");
        List<ImportKanbanStatusCsv> values = null;
        SimpleDateFormat datetimeFormatter = new SimpleDateFormat(ProductionNaviPropertyConstants.DEFAULT_DATETIME_FORMAT);

        // シート名
        String sheetName = formatInfo.getXlsSheetName();

        // 開始行
        int lineStart = StringUtils.parseInteger(formatInfo.getXlsStartRow());

        // カンバン名
        String strNameIdx = formatInfo.getXlsKanbanName();
        int idxKanbanName = CellReference.convertColStringToIndex(strNameIdx) + 1;
        // カンバンステータス
        String strStatusIdx = formatInfo.getXlsKanbanStatus();
        int idxStatus = CellReference.convertColStringToIndex(strStatusIdx) + 1;

        int maxIdx = idxKanbanName > idxStatus ? idxKanbanName : idxStatus;

        int count = 0;
        List<String> cols = null;
        try {
            WorkPlanExcelFileUtils excelFileUtils = new WorkPlanExcelFileUtils(datetimeFormatter);
            List<List<String>> rows = excelFileUtils.readExcel(fileName, sheetName, lineStart, maxIdx, null);

            if (Objects.nonNull(rows)) {
                values = new ArrayList();
                for (count = 0; count < rows.size(); count++) {
                    cols = rows.get(count);

                    ImportKanbanStatusCsv data = new ImportKanbanStatusCsv();
                    if (idxKanbanName > 0 && idxKanbanName <= cols.size()) {
                        data.setKanbanName(Objects.isNull(cols.get(idxKanbanName - 1)) ? "" : cols.get(idxKanbanName - 1));
                    }
                    if (idxStatus > 0 && idxStatus <= cols.size()) {
                        data.setKanbanStatus(Objects.isNull(cols.get(idxStatus - 1)) ? "" : cols.get(idxStatus - 1));
                    }
                    values.add(data);
                }
            }

            excelFileUtils.close();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            values = null;
            this.addResult(ex.getLocalizedMessage());
            this.addResult("  >  " + (count + 1) + ", " + String.join(", ", cols));
        } finally {
            logger.info("getExcelKanbanStatus end.");
        }

        return values;
    }

    /**
     * 製品情報をExcelファイルから取得する。
     *
     * @param fileName Excelファイル名
     * @param formatInfo 製品のフォーマット情報
     * @return 製品情報 インポート用データ一覧
     */
    private List<ImportProductCsv> getExcelProduct(String fileName, ProductFormatInfo formatInfo) {
        logger.info("getExcelProduct start.");
        List<ImportProductCsv> values = null;
        SimpleDateFormat datetimeFormatter = new SimpleDateFormat(ProductionNaviPropertyConstants.DEFAULT_DATETIME_FORMAT);

        // シート名
        String sheetName = formatInfo.getXlsSheetName();

        // 読み込み開始行
        int lineStart = StringUtils.parseInteger(formatInfo.getXlsStartRow());

        // カンバン名
        String strNameIdx = formatInfo.getXlsKanbanName();
        int idxKanbanName = CellReference.convertColStringToIndex(strNameIdx) + 1;
        // ユニークID
        String strUniqueIdIdx = formatInfo.getXlsUniqueID();
        int idxUniqueId = CellReference.convertColStringToIndex(strUniqueIdIdx) + 1;

        int maxIdx = idxKanbanName > idxUniqueId ? idxKanbanName : idxUniqueId;

        int count = 0;
        List<String> cols = null;
        try {
            WorkPlanExcelFileUtils excelFileUtils = new WorkPlanExcelFileUtils(datetimeFormatter);
            List<List<String>> rows = excelFileUtils.readExcel(fileName, sheetName, lineStart, maxIdx, null);

            if (Objects.nonNull(rows)) {
                values = new ArrayList();
                for (count = 0; count < rows.size(); count++) {
                    cols = rows.get(count);

                    ImportProductCsv data = new ImportProductCsv();
                    if (idxKanbanName > 0 && idxKanbanName <= cols.size()) {
                        data.setKanbanName(Objects.isNull(cols.get(idxKanbanName - 1)) ? "" : cols.get(idxKanbanName - 1));
                    }
                    if (idxUniqueId > 0 && idxUniqueId <= cols.size()) {
                        data.setUniqueId(Objects.isNull(cols.get(idxUniqueId - 1)) ? "" : cols.get(idxUniqueId - 1));
                    }
                    values.add(data);
                }
            }

            excelFileUtils.close();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            values = null;
            this.addResult(ex.getLocalizedMessage());
            this.addResult("  >  " + (count + 1) + ", " + String.join(", ", cols));
        } finally {
            logger.info("getExcelProduct end.");
        }

        return values;
    }

    /**
     * カンバン情報 CSVファイルを読み込む。
     *
     * @param fileName カンバン情報 インポート用ファイル名
     * @param formatInfo カンバンのフォーマット情報
     * @return カンバン情報 インポート用データ一覧
     */
    private List<ImportKanbanCsv> readKanbanCsv(String fileName, KanbanFormatInfo formatInfo) {
        if (Files.notExists(Paths.get(fileName))) {
            return null;
        }

        List<ImportKanbanCsv> importKanbans = new LinkedList();
        int count = 0;
        String kanbanName = "";

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // 読み込み設定情報
        int startRow = StringUtils.parseInteger(formatInfo.getCsvStartRow());// CSV: 読み込み開始行
        int colHierarchyName = StringUtils.parseInteger(formatInfo.getCsvHierarchyName());// CSV: カンバン階層
        int colKanbanName = StringUtils.parseInteger(formatInfo.getCsvKanbanName());// CSV: カンバン名
        int colWorkflowName = StringUtils.parseInteger(formatInfo.getCsvWorkflowName());// CSV: 工程順名
        int colWorkflowRev = StringUtils.parseInteger(formatInfo.getCsvWorkflowRev());// CSV: 工程順版数
        int colModelName = StringUtils.parseInteger(formatInfo.getCsvModelName());// CSV: モデル名
        int colStartDatetime = StringUtils.parseInteger(formatInfo.getCsvStartDateTime());// CSV: 開始予定日時
        int colProductionType = StringUtils.parseInteger(formatInfo.getCsvProductionType());// CSV: 生産タイプ
        int colLotQuantity = StringUtils.parseInteger(formatInfo.getCsvLotNum());// CSV: ロット数量
        int colProductionNumber = StringUtils.parseInteger(formatInfo.getCsvProductionNumber());// CSV: 製造番号
        int colStartSerial = StringUtils.parseInteger(formatInfo.getCsvStartSerial());// CSV: 開始シリアル番号
        int colEndSerial = StringUtils.parseInteger(formatInfo.getCsvEndSerial());// CSV: 終了シリアル番号
        int colCycleTime = Boolean.valueOf(AdProperty.getProperties().getProperty("enableCycleTimeImport", "false")) 
                ? StringUtils.parseInteger(formatInfo.getCsvCycleTime()) : -1;

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            String datas[];
            while ((datas = csvReader.readNext()) != null) {
                count++;

                if (count < startRow) {
                    continue;
                }

                ImportKanbanCsv data = new ImportKanbanCsv();

                // カンバン階層
                if (formatInfo.getIsCheckKanbanHierarchy()) {
                    // カンバン階層を指定する場合、フォーマット情報のカンバン階層名をセットする。
                    data.setKanbanHierarchyName(formatInfo.getKanbanHierarchyName());
                }

                for (int i = 0; i < datas.length; i++) {
                    try {
                        String value = Objects.isNull(datas[i]) ? "" : datas[i];

                        if (i == (colHierarchyName - 1)) {
                            // カンバン階層名 (カンバン階層を指定しない場合、カンバン階層名をセットする)
                            if (!formatInfo.getIsCheckKanbanHierarchy()) {
                                data.setKanbanHierarchyName(value);
                            }
                        } else if (i == (colKanbanName - 1)) {
                            // カンバン名
                            data.setKanbanName(value);
                        } else if (i == (colWorkflowName - 1)) {
                            // 工程順名 (モデル名で工程順を指定しない場合、工程順名をセットする)
                            if (!formatInfo.getIsCheckWorkflowRegex()) {
                                data.setWorkflowName(value);
                            }
                        } else if (i == (colWorkflowRev - 1)) {
                            // 版数 (モデル名で工程順を指定しない場合、版数をセットする)
                            if (!formatInfo.getIsCheckWorkflowRegex()) {
                                data.setWorkflowRev(value);
                            }
                            data.setWorkflowRev(Objects.isNull(datas[i]) ? "" : datas[i]);
                        } else if (i == (colModelName - 1)) {
                            // モデル名
                            data.setModelName(value);

                            // モデル名で工程順を指定する場合、条件に合う工程順名をセットする。
                            if (formatInfo.getIsCheckWorkflowRegex()) {
                                for (WorkflowRegexInfo regexInfo : formatInfo.getWorkflowRegexInfos()) {
                                    if (data.getModelName().matches(regexInfo.getRegex())) {
                                        data.setWorkflowName(regexInfo.getWorkflowName());
                                        data.setWorkflowRev(String.valueOf(regexInfo.getWorkflowRev()));
                                        break;
                                    }
                                }
                            }
                        } else if (i == (colStartDatetime - 1)) {
                            // 計画開始日時
                            data.setStartDatetime(value);
                        } else if (i == (colProductionType - 1)) {
                            // 生産タイプ
                            int productionType = 0;

                            try {
                                productionType = DataParser.parseProductionType(value);
                            } catch (Exception ex) {
                                logger.fatal(ex, ex);
                                this.addResult(String.format("[%d] > %s [%s]", count, LocaleUtils.getString("key.invalid.value"), LocaleUtils.getString("key.ProductionType")));
                            }

                            data.setProductionType(String.valueOf(productionType));
                        } else if (i == (colLotQuantity - 1)) {
                            // ロット数量
                            int lotNum = 1;

                            try {
                                lotNum = DataParser.parseLotNum(value);
                            } catch (Exception ex) {
                                logger.fatal(ex, ex);
                                this.addResult(String.format("[%d] > %s [%s]", count, LocaleUtils.getString("key.invalid.value"), LocaleUtils.getString("key.LotQuantity")));
                            }

                            data.setLotQuantity(String.valueOf(lotNum));

                        } else if (i == (colProductionNumber - 1)) {
                            // 製造番号
                            data.setProductionNumber(value);

                        } else if (i == (colStartSerial - 1)) {
                            // 開始シリアル番号
                            data.setStartSerial(value);
                            
                        } else if (i == (colEndSerial - 1)) {
                            // 終了シリアル番号
                            data.setEndSerial(value);
                            
                        } else if (i == (colCycleTime - 1)) {
                            if (NumberUtils.isNumber(value)) {
                                data.setCycleTime(value);
                            } else {
                                this.addResult(String.format("[%d] > %s [%s]", count, LocaleUtils.getString("key.invalid.value"), LocaleUtils.getString("key.StandardTime")));
                            }                            
                        }

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        this.addResult(String.format("[%d] > %s", count, LocaleUtils.getString("key.import.production.error")));
                    }
                }

                // モデル名と工程順を組み合わせて検索する設定の場合は検索するときに結合させるためここでは設定のみ保持する
                data.setEnableConcat(formatInfo.getIsCheckWorkflowWithModel());

                if (this.ignoreCheck.isSelected() && kanbanName.equals(data.getKanbanName())) {
                    continue;
                }

                importKanbans.add(data);
                kanbanName = data.getKanbanName();
            }

            // 読み込み日が指定されているならそれだけ読み込む
            importKanbans = importKanbans.stream()
                    .filter(csv -> isEnableDate(csv.getStartDatetime()))
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importKanbans = null;
            // 読み込み中に、不明なエラーが発生しました。
            this.addResult("   > " + LocaleUtils.getString("key.import.production.error"));
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return importKanbans;
    }

    /**
     * カンバンプロパティ情報 CSVファイルを読み込む。
     *
     * @param fileName カンバンプロパティ情報 インポート用ファイル名
     * @param formatInfo カンバンプロパティのフォーマット情報
     * @return カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportKanbanPropertyCsv> readKanbanPropertyCsv(String fileName, KanbanPropFormatInfo formatInfo) {
        if (Files.notExists(Paths.get(fileName))) {
            return null;
        }

        if ("2".equals(formatInfo.getSelectedFormat())) {
            // フォーマットB
            return readKanbanPropertyF2Csv(fileName, formatInfo);
        } else {
            // フォーマットA
            return readKanbanPropertyF1Csv(fileName, formatInfo);
        }
    }

    /**
     * カンバンプロパティ情報(フォーマットA) CSVファイルを読み込む。
     *
     * @param fileName カンバンプロパティ情報 インポート用ファイル名
     * @param formatInfo カンバンプロパティのフォーマット情報
     * @return カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportKanbanPropertyCsv> readKanbanPropertyF1Csv(String fileName, KanbanPropFormatInfo formatInfo) {
        List<ImportKanbanPropertyCsv> importKanbanProps = new LinkedList();
        int count = 0;

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // 読み込み設定情報
        int startRow = StringUtils.parseInteger(formatInfo.getCsvStartRow());// CSV: 読み込み開始行
        int colKanbanName = StringUtils.parseInteger(formatInfo.getCsvKanbanName());// CSV: カンバン名
        int colWorkflowName = StringUtils.parseInteger(formatInfo.getCsvWorkflowName());// CSV: 工程順名
        int colKanbanPropertyName = StringUtils.parseInteger(formatInfo.getCsvPropName());// CSV: プロパティ名
        int colKanbanPropertyType = StringUtils.parseInteger(formatInfo.getCsvPropType());// CSV: プロパティ型
        int colKanbanPropertyValue = StringUtils.parseInteger(formatInfo.getCsvPropValue());// CSV: プロパティ値

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            String datas[];
            while ((datas = csvReader.readNext()) != null) {
                count++;

                if (count < startRow) {
                    continue;
                }

                ImportKanbanPropertyCsv data = new ImportKanbanPropertyCsv();

                for (int i = 0; i < datas.length; i++) {
                    if (i == (colKanbanName - 1)) {
                        // カンバン名
                        data.setKanbanName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colWorkflowName - 1)) {
                        // 工程順名
                        data.setWorkflowName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colKanbanPropertyName - 1)) {
                        // プロパティ名
                        data.setKanbanPropertyName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colKanbanPropertyType - 1)) {
                        // プロパティ型
                        data.setKanbanPropertyType(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colKanbanPropertyValue - 1)) {
                        // プロパティ値
                        data.setKanbanPropertyValue(Objects.isNull(datas[i]) ? "" : datas[i]);
                    }
                }

                importKanbanProps.add(data);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importKanbanProps = null;
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return importKanbanProps;
    }

    /**
     * カンバンプロパティ情報(フォーマットB) CSVファイル読込
     *
     * @param fileName カンバンプロパティ情報 インポート用ファイル名
     * @param formatInfo カンバンプロパティのフォーマット情報
     * @return カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportKanbanPropertyCsv> readKanbanPropertyF2Csv(String fileName, KanbanPropFormatInfo formatInfo) {
        List<ImportKanbanPropertyCsv> importKanbanProps = new ArrayList();

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // ヘッダー行
        int headerRow = StringUtils.parseInteger(formatInfo.getF2CsvHeaderRow());
        // 読み込み開始行
        int startRow = StringUtils.parseInteger(formatInfo.getF2CsvStartRow());
        // カンバン名
        int colKanbanName = StringUtils.parseInteger(formatInfo.getF2CsvKanbanName());
        // 工程順名
        int colWorkflowName = StringUtils.parseInteger(formatInfo.getF2CsvWorkflowName());

        // プロパティ
        List<Integer> colProps = new ArrayList();
        for (String propColumn : formatInfo.getF2CsvPropValues()) {
            int colProp = StringUtils.parseInteger(propColumn);
            colProps.add(colProp);
        }

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            List<List<String>> dataList = new ArrayList<>();
            String[] datas;
            while ((datas = csvReader.readNext()) != null) {
                dataList.add(Arrays.asList(datas));
            }

            // ヘッダー
            List<String> header = dataList.get(headerRow - 1);

            // データ
            for (int i = startRow - 1; i < dataList.size(); i++) {
                List<String> row = dataList.get(i);

                String kanbanName = row.get(colKanbanName - 1);
                String workflowName = 0 < colWorkflowName ? row.get(colWorkflowName - 1) : "";
//                String workflowName = row.get(colWorkflowName - 1);

                for (Integer colProp : colProps) {
                    ImportKanbanPropertyCsv data = new ImportKanbanPropertyCsv();
                    data.setKanbanName(kanbanName);
                    data.setWorkflowName(workflowName);
                    if (!(colProp < 1 || colProp > row.size())) {
                        data.setKanbanPropertyName(header.get(colProp - 1));
                        data.setKanbanPropertyType(CustomPropertyTypeEnum.TYPE_STRING.name());
                        data.setKanbanPropertyValue(Objects.isNull(row.get(colProp - 1)) ? "" : row.get(colProp - 1));

                        importKanbanProps.add(data);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importKanbanProps = null;
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return importKanbanProps;
    }

    /**
     * 工程カンバン情報 CSVファイルを読み込む。
     *
     * @param fileName 工程カンバン情報 インポート用ファイル名
     * @param formatInfo 工程カンバンのフォーマット情報
     * @return 工程カンバン情報 インポート用データ一覧
     */
    private List<ImportWorkKanbanCsv> readWorkKanbanCsv(String fileName, WorkKanbanFormatInfo formatInfo) {
        if (Files.notExists(Paths.get(fileName))) {
            return null;
        }

        List<ImportWorkKanbanCsv> importWorkKanbans = new ArrayList();
        int count = 0;

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // 読み込み設定情報
        int startRow = StringUtils.parseInteger(formatInfo.getCsvStartRow());// CSV: 読み込み開始行
        int colKanbanName = StringUtils.parseInteger(formatInfo.getCsvKanbanName());// CSV: カンバン名
        int colWorkNum = StringUtils.parseInteger(formatInfo.getCsvWorkNum());// CSV: 工程の番号
        int colSkipFlag = StringUtils.parseInteger(formatInfo.getCsvSkipFlag());// CSV: スキップ
        int colStartDatetime = StringUtils.parseInteger(formatInfo.getCsvStartDateTime());// CSV: 開始予定日時
        int colCompDatetime = StringUtils.parseInteger(formatInfo.getCsvCompDateTime());// CSV: 完了予定日時
        int colOrganizations = StringUtils.parseInteger(formatInfo.getCsvOrganizationIdentName());// CSV: 組織識別名
        int colEquipments = StringUtils.parseInteger(formatInfo.getCsvEquipmentIdentName());// CSV: 設備識別名
        int colWorkName = StringUtils.parseInteger(formatInfo.getCsvWorkName());// CSV: 工程名
        int colTactTime = StringUtils.parseInteger(formatInfo.getCsvTactTime());// CSV: タクトタイム

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            String datas[];
            while ((datas = csvReader.readNext()) != null) {
                count++;

                if (count < startRow) {
                    continue;
                }

                ImportWorkKanbanCsv data = new ImportWorkKanbanCsv();

                for (int i = 0; i < datas.length; i++) {
                    if (i == (colKanbanName - 1)) {
                        // カンバン名
                        data.setKanbanName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colWorkNum - 1)) {
                        // 工程の番号
                        data.setWorkNum(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colSkipFlag - 1)) {
                        // スキップフラグ
                        data.setSkipFlag(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colStartDatetime - 1)) {
                        // 開始予定日時
                        data.setStartDatetime(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colCompDatetime - 1)) {
                        // 完了予定日時
                        data.setCompDatetime(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colOrganizations - 1)) {
                        // 組織識別名
                        data.setOrganizations(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colEquipments - 1)) {
                        // 設備識別名
                        data.setEquipments(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colWorkName - 1)) {
                        // 工程名
                        data.setWorkName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colTactTime - 1)) {
                        // タクトタイム
                        data.setTactTime(StringUtils.isEmpty(datas[i]) ? "" : toSecond(datas[i]));
                    }
                }

                importWorkKanbans.add(data);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importWorkKanbans = null;
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return importWorkKanbans;
    }

    /**
     * カンバンステータス情報 CSVファイルを読み込む。
     *
     * @param fileName カンバンステータス情報 インポート用ファイル名
     * @param formatInfo カンバンステータスのフォーマット情報
     * @return カンバンステータス情報 インポート用データ一覧
     */
    private List<ImportKanbanStatusCsv> readKanbanStatusCsv(String fileName, KanbanStatusFormatInfo formatInfo) {
        if (Files.notExists(Paths.get(fileName))) {
            return null;
        }

        List<ImportKanbanStatusCsv> datas = new ArrayList();
        int count = 0;

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // 読み込み設定情報
        int startRow = StringUtils.parseInteger(formatInfo.getCsvStartRow());// CSV: 読み込み開始行
        int colKanbanName = StringUtils.parseInteger(formatInfo.getCsvKanbanName());// CSV: カンバン名
        int colKanbanStatus = StringUtils.parseInteger(formatInfo.getCsvKanbanStatus());// CSV: ステータス

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            String csvDatas[];
            while ((csvDatas = csvReader.readNext()) != null) {
                count++;

                if (count < startRow) {
                    continue;
                }

                ImportKanbanStatusCsv data = new ImportKanbanStatusCsv();

                for (int i = 0; i < csvDatas.length; i++) {
                    if (i == (colKanbanName - 1)) {
                        // カンバン名
                        data.setKanbanName(Objects.isNull(csvDatas[i]) ? "" : csvDatas[i]);
                    } else if (i == (colKanbanStatus - 1)) {
                        // カンバンステータス
                        data.setKanbanStatus(Objects.isNull(csvDatas[i]) ? "" : csvDatas[i]);
                    }
                }
                datas.add(data);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            datas = null;
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return datas;
    }

    /**
     * 工程カンバンプロパティ情報 CSVファイルを読み込む。
     *
     * @param fileName 工程カンバンプロパティ情報 インポート用ファイル名
     * @param formatInfo 工程カンバンプロパティのフォーマット情報
     * @return 工程カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportWorkKanbanPropertyCsv> readWorkKanbanPropertyCsv(String fileName, WorkKanbanPropFormatInfo formatInfo) {
        if (Files.notExists(Paths.get(fileName))) {
            return null;
        }

        List<ImportWorkKanbanPropertyCsv> importWkKanbanProps;
        if ("2".equals(formatInfo.getSelectedFormat())) {
            // フォーマットB
            importWkKanbanProps = readWorkKanbanPropertyF2Csv(fileName, formatInfo);
        } else {
            // フォーマットA
            importWkKanbanProps = readWorkKanbanPropertyF1Csv(fileName, formatInfo);
        }

        return importWkKanbanProps;
    }

    /**
     * 工程カンバンプロパティ情報(フォーマットA) CSVファイルを読み込む。
     *
     * @param fileName 工程カンバンプロパティ情報 インポート用ファイル名
     * @param formatInfo 工程カンバンプロパティのフォーマット情報
     * @return 工程カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportWorkKanbanPropertyCsv> readWorkKanbanPropertyF1Csv(String fileName, WorkKanbanPropFormatInfo formatInfo) {
        List<ImportWorkKanbanPropertyCsv> importWkKanbanProps = new ArrayList();
        int count = 0;

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // 読み込み設定情報
        int startRow = StringUtils.parseInteger(formatInfo.getCsvStartRow());// CSV: 読み込み開始行
        int colKanbanName = StringUtils.parseInteger(formatInfo.getCsvKanbanName());// CSV: カンバン名
        int colWorkName = StringUtils.parseInteger(formatInfo.getCsvWorkName());// CSV: 工程名
        int colWorkNum = StringUtils.parseInteger(formatInfo.getCsvWorkNum());// CSV: 工程の番号
        int colPropName = StringUtils.parseInteger(formatInfo.getCsvPropName());// CSV: プロパティ名
        int colPropType = StringUtils.parseInteger(formatInfo.getCsvPropType());// CSV: プロパティ型
        int colPropValue = StringUtils.parseInteger(formatInfo.getCsvPropValue());// CSV: プロパティ値

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            String datas[];
            while ((datas = csvReader.readNext()) != null) {
                count++;

                if (count < startRow) {
                    continue;
                }

                ImportWorkKanbanPropertyCsv data = new ImportWorkKanbanPropertyCsv();

                for (int i = 0; i < datas.length; i++) {
                    if (i == (colKanbanName - 1)) {
                        // カンバン名
                        data.setKanbanName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colWorkName - 1)) {
                        // 工程名
                        data.setWorkName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colWorkNum - 1)) {
                        // 工程の番号
                        data.setWorkNum(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colPropName - 1)) {
                        // プロパティ名
                        data.setWkKanbanPropName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colPropType - 1)) {
                        // プロパティ型
                        data.setWkKanbanPropType(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colPropValue - 1)) {
                        // プロパティ値
                        data.setWkKanbanPropValue(Objects.isNull(datas[i]) ? "" : datas[i]);
                    }
                }

                importWkKanbanProps.add(data);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importWkKanbanProps = null;
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return importWkKanbanProps;
    }

    /**
     * 工程カンバンプロパティ情報(フォーマットB) CSVファイルを読み込む。
     *
     * @param fileName 工程カンバンプロパティ情報 インポート用ファイル名
     * @param formatInfo 工程カンバンプロパティのフォーマット情報
     * @return 工程カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportWorkKanbanPropertyCsv> readWorkKanbanPropertyF2Csv(String fileName, WorkKanbanPropFormatInfo formatInfo) {
        List<ImportWorkKanbanPropertyCsv> importWorkKanbanProps = new ArrayList();

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // ヘッダー行
        int headerRow = StringUtils.parseInteger(formatInfo.getF2CsvHeaderRow());
        // 読み込み開始行
        int startRow = StringUtils.parseInteger(formatInfo.getF2CsvStartRow());
        // カンバン名
        int colKanbanName = StringUtils.parseInteger(formatInfo.getF2CsvKanbanName());
        // 工程名
        int colWorkName = StringUtils.parseInteger(formatInfo.getF2CsvWorkName());
        // 工程の番号
        int colWorkNum = StringUtils.parseInteger(formatInfo.getF2CsvWorkNo());

        // プロパティ
        List<Integer> colProps = new ArrayList();
        for (String propColumn : formatInfo.getF2CsvPropValues()) {
            int colProp = StringUtils.parseInteger(propColumn);
            colProps.add(colProp);
        }

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            List<List<String>> dataList = new ArrayList<>();
            String[] datas;
            while ((datas = csvReader.readNext()) != null) {
                dataList.add(Arrays.asList(datas));
            }

            // ヘッダー
            List<String> header = dataList.get(headerRow - 1);

            HashMap<String, Integer> unionPropCountMap = new HashMap<>();

            // データ
            for (int i = startRow - 1; i < dataList.size(); i++) {
                List<String> row = dataList.get(i);

                String unionPropLeft = null;
                String unionPropRight = null;

                String kanbanName = colKanbanName > 0 ? row.get(colKanbanName - 1) : "";
                String workName = colWorkName > 0 ? row.get(colWorkName - 1) : "";
                String workNum = colWorkNum > 0 ? row.get(colWorkNum - 1) : "";

                for (Integer colProp : colProps) {
                    if (!(colProp <= 0 || colProp > row.size())) {

                        String propName = header.get(colProp - 1);
                        String propValue = Objects.isNull(row.get(colProp - 1)) ? "" : row.get(colProp - 1);

                        // プロパティを組み合わせて読み込む
                        if (Objects.nonNull(formatInfo.getF2IsCheckUnionProp()) && formatInfo.getF2IsCheckUnionProp()) {
                            boolean targetPropFlg = false;
                            // 結合対象のプロパティなら一時格納
                            if (propName.equals(formatInfo.getF2UnionPropLeftName())) {
                                unionPropLeft = propValue;
                                targetPropFlg = true;
                            } else if (propName.equals(formatInfo.getF2UnionPropRightName())) {
                                unionPropRight = propValue;
                                targetPropFlg = true;
                            }

                            if (targetPropFlg) {
                                if (Objects.isNull(unionPropLeft) || Objects.isNull(unionPropRight)) {
                                    // 揃っていなければ次プロパティへ
                                    continue;
                                } else {
                                    // 組み合わせたプロパティの連番を記録
                                    String key = kanbanName + workName + workNum;
                                    int unionPropCount = 1;
                                    unionPropCountMap.get(key);
                                    if (unionPropCountMap.containsKey(key)) {
                                        unionPropCount += unionPropCountMap.get(key);
                                        unionPropCountMap.put(key, unionPropCount);
                                    } else {
                                        unionPropCountMap.put(key, unionPropCount);
                                    }
                                    // プロパティ名を新しいプロパティ名に変更(サフィックス：連番)
                                    propName = formatInfo.getF2UnionPropNewName() + unionPropCount;
                                    // プロパティ値を結合(デリミタ：\t)
                                    propValue = unionPropLeft + "\\t" + unionPropRight;
                                    unionPropLeft = unionPropRight = null;
                                }
                            }
                        }

                        ImportWorkKanbanPropertyCsv data = new ImportWorkKanbanPropertyCsv();
                        data.setKanbanName(kanbanName);
                        data.setWorkName(workName);
                        data.setWorkNum(workNum);
                        data.setWkKanbanPropName(propName);
                        data.setWkKanbanPropType(CustomPropertyTypeEnum.TYPE_STRING.name());
                        data.setWkKanbanPropValue(propValue);

                        importWorkKanbanProps.add(data);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importWorkKanbanProps = null;
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }

        return importWorkKanbanProps;
    }

    /**
     * 製品情報 CSVファイルを読み込む。
     *
     * @param fileName 製品情報 インポート用ファイル名
     * @param formatInfo 製品のフォーマット情報
     * @return 製品情報 インポート用データ一覧
     */
    private List<ImportProductCsv> readProductCsv(String fileName, ProductFormatInfo formatInfo) {
        if (Files.notExists(Paths.get(fileName))) {
            return null;
        }

        List<ImportProductCsv> datas = new ArrayList();
        int count = 0;

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // 読み込み設定情報
        int startRow = StringUtils.parseInteger(formatInfo.getCsvStartRow());
        int colKanbanName = StringUtils.parseInteger(formatInfo.getCsvKanbanName());
        int colUniqueId = StringUtils.parseInteger(formatInfo.getCsvUniqueID());

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            String csvDatas[];
            while ((csvDatas = csvReader.readNext()) != null) {
                count++;

                if (count < startRow) {
                    continue;
                }

                ImportProductCsv data = new ImportProductCsv();

                for (int i = 0; i < csvDatas.length; i++) {
                    if (i == (colKanbanName - 1)) {
                        // カンバン名
                        data.setKanbanName(Objects.isNull(csvDatas[i]) ? "" : csvDatas[i]);
                    } else if (i == (colUniqueId - 1)) {
                        // 製品シリアル
                        data.setUniqueId(Objects.isNull(csvDatas[i]) ? "" : csvDatas[i]);
                    }
                }
                datas.add(data);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            datas = null;
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return datas;
    }

    /**
     * Double型の文字列を、整数の文字列に変換する。
     *
     * @param value Double型の文字列 ("1.0")
     * @return 整数の文字列 ("1")
     */
    private String doubleStringToIntString(String value) {
        String result = "";
        try {
            result = String.valueOf(Double.valueOf(value).intValue());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * HH:mm:ss形式の文字列を秒数を表す文字列に変換する。
     *     
     * @param value HH:mm:ss形式の文字列 ("12:23:34") or yyyy/MM/dd HH:mm:ss形式の文字列
     * @return 整数の文字列 ("44614")
     */
    private String toSecond(String value) {
        String result = "";
        long seconds = 0;
        Date date;
        try {
            if ((StringTime.validStringTime(value))) {
                String[] split = value.split(":");
                seconds = seconds + TimeUnit.HOURS.toSeconds(Long.parseLong(split[0]));
                seconds = seconds + TimeUnit.MINUTES.toSeconds(Long.parseLong(split[1]));
                seconds = seconds + TimeUnit.SECONDS.toSeconds(Long.parseLong(split[2]));
                
                if (seconds > TAKT_TIME_MAX_SECONDS) {
                    //480時間以上の場合
                    result = Integer.toString(TAKT_TIME_MAX_SECONDS);
                } else {
                    result = String.valueOf(seconds);
                }
                
            } else {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    date = format.parse(value);
                    
                    Calendar cale = Calendar.getInstance();
                    cale.setTime(date);
                    seconds += cale.get(Calendar.HOUR) * 60 * 60;
                    seconds += cale.get(Calendar.MINUTE) * 60;
                    seconds += cale.get(Calendar.SECOND);
                    result = String.valueOf(seconds);
                    
                } catch (ParseException ex) {
                    result = "0";
                }
            }
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

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
     * 入力した日時が設定の日付の00:00から23:59の範囲に収まっているか調べる
     *
     * @param startDatetime 入力した日時の文字列
     * @return
     */
    private boolean isEnableDate(String startDatetime) {

        // 設定に日付が入力されてないならすべての行が対象
        if (fromToPicker.isSelectAll()) {
            return true;
        }

        // 行に日時がないなら無条件で無視
        boolean emptyStartDatetime = StringUtils.isEmpty(startDatetime);
        if (emptyStartDatetime) {
            return false;
        }

        LocalDateTime current = ImportFormatFileUtil.stringToDateTime(startDatetime).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (!fromToPicker.isBetween(current)) {
            return false;
        }

        return true;
    }

    /**
     * 自動読込ボタン Action
     *
     * @param event
     */
    @FXML
    private void onAutoImportButton(ActionEvent event) {
        try {
            logger.info(":onAutoImportButton start");
            blockUI(true);

            // 「フォルダ監視中...」を表示
            String title = LocaleUtils.getString("key.WorkPlan.AutoImport.standby");
            String message = LocaleUtils.getString("key.WorkPlan.AutoImport.standby.message");

            this.isAutoImportStandby = true;
            this.timer.play();
            sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.FINISH}, ButtonType.FINISH);

        } finally {
            this.timer.stop();
            this.isAutoImportStandby = false;
            blockUI(this.isImportProccessing);
            logger.info(":onAutoImportButton end");
        }
    }

    /**
     * 工程カンバンプロパティ更新ファイルが存在するか調べる
     *
     * @return
     */
    private boolean existsUpdateFile() {
        final String folder = this.importCsvFileField.getText();
        final String updatefilename = folder + File.separator + this.importFormatInfo.getUpdateWorkKanbanPropFormatInfo().getCsvFileName();
        final File updateFile = new File(updatefilename);

        return updateFile.exists() && updateFile.isFile();
    }

    /**
     * 必須ファイルが存在するかどうか
     *
     * @return CSV形式の場合"kanban.csv"(変更可能)、Excel形式の場合はそのexcelファイルが存在する場合true
     */
    private boolean existsEssentialFiles(Integer selectedMode) {
        KanbanFormatInfo kanbanFormatInfo = this.importFormatInfo.getKanbanFormatInfo();
        String kanbanInfoFilePath = "";
        switch (selectedMode) {
            case ProductionNaviUtils.IMPORT_TAB_IDX_CSV:
                kanbanInfoFilePath
                        = this.importCsvFileField.getText() + File.separator + kanbanFormatInfo.getCsvFileName();
                break;

            case ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL:
                kanbanInfoFilePath = this.importExcelFileField.getText();
                break;

        }

        File file = new File(kanbanInfoFilePath);

        return file.exists() && file.isFile();
    }

    /**
     * 自動取込待ち受け処理
     */
    private void AutoImportStandby() {
        try {
            logger.info("AutoImportStandby start.");
            Integer selectedMode = this.tabImportMode.getSelectionModel().getSelectedIndex();

            final boolean existsFiles = Objects.equals(selectedMode, ProductionNaviUtils.IMPORT_TAB_IDX_CSV)
                    ? existsEssentialFiles(selectedMode) || existsUpdateFile() // CSV形式の場合カンバンcsvか工程カンバンプロパティ更新csvのどっちかがあればOK
                    : existsEssentialFiles(selectedMode);
            if (!existsFiles) {
                return;
            }

            this.timer.stop();

            deleteCompleted(selectedMode);

            onImportAction(null);

        } finally {
            logger.info("AutoImportStandby end.");
        }
    }

    private WorkflowInfoEntity findWorkflow(final String workflowName, final String rev) throws UnsupportedEncodingException {
        WorkflowInfoEntity workflow;
        if (Objects.nonNull(rev) && !rev.isEmpty()) {
            workflow = workflowInfoFacade.findName(URLEncoder.encode(workflowName, CHARSET), Integer.valueOf(rev));
        } else {
            workflow = workflowInfoFacade.findName(URLEncoder.encode(workflowName, CHARSET));
        }
        return workflow;
    }

    /**
     * 結果を結合する。同じ文字列をキーとしている場合、値を加算する。値が異なる場合、新しく追加する。
     *
     * @param v1
     * @param v2
     * @return
     */
    static public Map<String, Integer> concatResult(Map<String, Integer> v1, Map<String, Integer> v2) {
        if (Objects.isNull(v1) && Objects.isNull(v2)) {
            return null;
        } else if (Objects.isNull(v1)) {
            return v2;
        } else if (Objects.isNull(v2)) {
            return v1;
        }

        Map<String, Integer> ret = new HashMap(v1);

        for (Map.Entry<String, Integer> v : v2.entrySet()) {
            if (ret.containsKey(v.getKey())) {
                ret.put(v.getKey(), ret.get(v.getKey()) + v.getValue());
            } else {
                ret.put(v.getKey(), v.getValue());
            }
        }

        return ret;
    }

    private void updateWorkKanban(KanbanInfoEntity kanban, String kanbanName, List<ImportWorkKanbanCsv> importWorkKanbans, Map<String, OrganizationInfoEntity> organizationMap, Map<String, EquipmentInfoEntity> equipmentMap, int kanbanLine) throws UnsupportedEncodingException {

        List<ImportWorkKanbanCsv> works = importWorkKanbans.stream().filter(p -> kanbanName.equals(p.getKanbanName())).collect(Collectors.toList());
        for (ImportWorkKanbanCsv work : works) {

            // 2019/12/18 工程名項目の追加対応 工程カンバンの検索処理を工程名を考慮したものに変更
            // 更新対象の工程カンバン
            List<WorkKanbanInfoEntity> workKanbans = findWorkKanban(kanban.getWorkKanbanCollection(), work);

            // 該当する工程カンバンがなければスキップ
            if (workKanbans.isEmpty()) {
                if (!StringUtils.isEmpty(work.getWorkName())) {
                    addResult(String.format("[%d] > [%s] [%s]", kanbanLine, LocaleUtils.getString("key.alert.notfound.workkanbanError"), work.getWorkName()));
                } else {
                    addResult(String.format("[%d] > [%s] [%s]", kanbanLine, LocaleUtils.getString("key.alert.notfound.workkanbanError"), work.getWorkNum()));
                }
                continue;
            }

            for (WorkKanbanInfoEntity workKanban : workKanbans) {
                // 工程カンバンステータスが「計画中(Planning)」，「計画済み(Planned)」以外の場合は更新しない
                KanbanStatusEnum workStatus = workKanban.getWorkStatus();
                if (!workStatus.isWorkKanbanUpdatableStatus) {
                    // 更新できない工程カンバンのためスキップ
                    addResult(String.format("[%d] > [%s] [%s] [%s]", kanbanLine,
                            LocaleUtils.getString("key.import.work.plan.work.kanban"), LocaleUtils.getString("key.ImportKanban_NotUpdateWorkKanban"), LocaleUtils.getString(workStatus.getResourceKey())));
                    continue;
                }

                // スキップフラグ
                if (!StringUtils.isEmpty(work.getSkipFlag())) {
                    if (work.getSkipFlag().equals("1")) {
                        workKanban.setSkipFlag(true);
                    } else {
                        workKanban.setSkipFlag(false);
                    }
                }

                // タクトタイム 2020/02/20 MES連携 タクトタイム追加
                if (!StringUtils.isEmpty(work.getTactTime())) {
                    int tactTime = StringUtils.parseInteger(work.getTactTime());
                    if (tactTime == 0) {
                        addResult(String.format("[%d] > [%s] [%s] [%s]", kanbanLine,
                                LocaleUtils.getString("key.import.work.plan.work.kanban"), LocaleUtils.getString("key.error.format.datetime"), LocaleUtils.getString(work.getTactTime())));
                    } else {
                        // 秒→ミリ秒に変換
                        tactTime = tactTime * 1000;
                        workKanban.setTaktTime(tactTime);
                    }
                }

                // 開始予定日時
                if (!StringUtils.isEmpty(work.getStartDatetime())) {
                    Date workStartDateTime = ImportFormatFileUtil.stringToDateTime(work.getStartDatetime());
                    if (Objects.isNull(workStartDateTime)) {
                        addResult(String.format("[%d] > [%s] [%s] [%s]", kanbanLine,
                                LocaleUtils.getString("key.import.work.plan.work.kanban"), LocaleUtils.getString("key.error.format.datetime"), LocaleUtils.getString(work.getStartDatetime())));
                        continue;
                    }
                    workKanban.setStartDatetime(workStartDateTime);
                }

                // 完了予定日時
                if (!StringUtils.isEmpty(work.getCompDatetime())) {
                    Date workCompDateTime = ImportFormatFileUtil.stringToDateTime(work.getCompDatetime());
                    if (Objects.isNull(workCompDateTime)) {
                        addResult(String.format("[%d] > [%s] [%s] [%s]", kanbanLine,
                                LocaleUtils.getString("key.import.work.plan.work.kanban"), LocaleUtils.getString("key.error.format.datetime"), LocaleUtils.getString(work.getCompDatetime())));
                        continue;
                    }
                    workKanban.setCompDatetime(workCompDateTime);
                }

                // 組織
                String organizations = work.getOrganizations();
                if (!StringUtils.isEmpty(organizations)) {
                    String[] oranizationIdents = organizations.split(DELIMITER, 0);

                    List<Long> orgIdList = new ArrayList<>();
                    for (String oranizationIdent : oranizationIdents) {
                        if (oranizationIdent.equals(DELIMITER)) {
                            continue;
                        }
                        OrganizationInfoEntity entity;
                        if (organizationMap.containsKey(oranizationIdent)) {
                            entity = organizationMap.get(oranizationIdent);
                        } else {
                            entity = organizationInfoFacade.findName(URLEncoder.encode(oranizationIdent, CHARSET));
                            organizationMap.put(oranizationIdent, entity);
                        }

                        if (Objects.nonNull(entity.getOrganizationId())) {
                            orgIdList.add(entity.getOrganizationId());
                        } else {
                            // 存在しない組織識別名
                            addResult(String.format("[%d] > [%s] [%s] [%s]", kanbanLine,
                                    LocaleUtils.getString("key.import.work.plan.work.kanban"), LocaleUtils.getString("key.ImportKanban_OrganizationNothing"), oranizationIdent));
                        }
                    }

                    workKanban.setOrganizationCollection(orgIdList);
                }

                // 設備
                String equipments = work.getEquipments();
                if (!StringUtils.isEmpty(equipments)) {
                    String[] equipmentIdents = equipments.split(DELIMITER, 0);

                    List<Long> equipIdList = new ArrayList<>();
                    for (String equipmentIdent : equipmentIdents) {
                        if (equipmentIdent.equals(DELIMITER)) {
                            continue;
                        }
                        
                        EquipmentInfoEntity entity;
                        if (equipmentMap.containsKey(equipmentIdent)) {
                            entity = equipmentMap.get(equipmentIdent);
                        } else {
                            entity = equipmentInfoFacade.findName(equipmentIdent);
                            equipmentMap.put(equipmentIdent, entity);
                        }

                        if (Objects.nonNull(entity.getEquipmentId())) {
                            equipIdList.add(entity.getEquipmentId());
                        } else {
                            // 存在しない設備識別名
                            addResult(String.format("[%d] > [%s] [%s] [%s]", kanbanLine,
                                    LocaleUtils.getString("key.import.work.plan.work.kanban"), LocaleUtils.getString("key.ImportKanban_EquipmentNothing"), equipmentIdent));
                        }
                    }

                    workKanban.setEquipmentCollection(equipIdList);
                }
            }

            if (!works.isEmpty()) {
                // 開始予定日時
                kanban.getWorkKanbanCollection().sort(Comparator.comparing(p -> p.getStartDatetime()));
                Date startDateTime = kanban.getWorkKanbanCollection().get(0).getStartDatetime();
                kanban.setStartDatetime(startDateTime);

                // 完了予定日時
                kanban.getWorkKanbanCollection().sort(Comparator.comparing(p -> p.getCompDatetime()));
                Date compDateTime = kanban.getWorkKanbanCollection().get(kanban.getWorkKanbanCollection().size() - 1).getCompDatetime();
                kanban.setCompDatetime(compDateTime);
            }
        }

        kanban.getWorkKanbanCollection().forEach(p -> {
            p.setServiceInfo(kanban.getServiceInfo());
            p.setFkUpdatePersonId(kanban.getFkUpdatePersonId());
            p.setUpdateDatetime(kanban.getUpdateDatetime());
        });
        
        kanban.getSeparateworkKanbanCollection().forEach(p -> {
            p.setServiceInfo(kanban.getServiceInfo());
            p.setFkUpdatePersonId(kanban.getFkUpdatePersonId());
            p.setUpdateDatetime(kanban.getUpdateDatetime());
        });
    }

    /**
     * 2019/12/18 工程名項目の追加対応 工程カンバン情報をもとに指定した工程カンバンリストから一致する工程カンバンを探し出す
     *
     * @param wkKanbans
     * @param wkKanban
     * @return
     */
    private List<WorkKanbanInfoEntity> findWorkKanban(List<WorkKanbanInfoEntity> wkKanbans, ImportWorkKanbanCsv wkKanban) {
        WorkKanbanInfoEntity workKanban = null;

        if (!StringUtils.isEmpty(wkKanban.getWorkName())) {
            // 工程名があるとき、工程名で工程カンバン検索
            return findWorkKanbanByWorkName(wkKanbans, wkKanban.getWorkName());
        } else {
            // 工程名がないとき、工程の番号で工程カンバン検索
            return findWorkKanbanByWorkNum(wkKanbans, wkKanban.getWorkNum());
        }
    }

    /**
     * 工程カンバンプロパティ情報をもとに指定した工程カンバンリストから一致する工程カンバンを探し出す
     *
     * @param wkKanbans
     * @param wkKanbanProp
     * @return
     */
    private List<WorkKanbanInfoEntity> findWorkKanban(List<WorkKanbanInfoEntity> wkKanbans, ImportWorkKanbanPropertyCsv wkKanbanProp) {
        WorkKanbanInfoEntity workKanban = null;

        if (!StringUtils.isEmpty(wkKanbanProp.getWorkName())) {
            // 工程名があるとき、工程名で工程カンバン検索
            return findWorkKanbanByWorkName(wkKanbans, wkKanbanProp.getWorkName());
        } else {
            // 工程名がないとき、工程の番号で工程カンバン検索
            return findWorkKanbanByWorkNum(wkKanbans, wkKanbanProp.getWorkNum());
        }
    }

    /**
     * 2019/12/18 工程名項目の追加対応 工程名をもとに指定した工程カンバンリストから一致する工程カンバンを探し出す
     *
     * @param wkKanbans
     * @param workName
     * @return
     */
    private List<WorkKanbanInfoEntity> findWorkKanbanByWorkName(List<WorkKanbanInfoEntity> wkKanbans, String workName) {
        WorkKanbanInfoEntity workKanban = null;

        //　工程名で工程カンバン検索
        return wkKanbans.stream().filter(
                p -> workName.equals(p.getWorkName())).collect(Collectors.toList());
    }

    /**
     * 2019/12/18 工程名項目の追加対応 工程順をもとに指定した工程カンバンリストから一致する工程カンバンを探し出す
     *
     * @param wkKanbans
     * @param workNum
     * @return
     */
    private List<WorkKanbanInfoEntity> findWorkKanbanByWorkNum(List<WorkKanbanInfoEntity> wkKanbans, String workNum) {
        WorkKanbanInfoEntity workKanban = null;

        // 工程の番号で工程カンバン検索
        return  wkKanbans.stream().filter(
                p -> workNum.equals(String.valueOf(1 + wkKanbans.indexOf(p)))).collect(Collectors.toList());
    }

    /**
     * 読み込むCSVファイル全てのファイル名を取得する
     *
     * @return
     */
    private List<String> getCsvFilenames() {
        final List<String> ret = new ArrayList();
        ret.addAll(kanbanCsvFilenames);
        ret.addAll(updateFilenames);
        return ret;
    }

    /**
     * インポートファイルのファイル名を変更する。
     *
     * @param folder フォルダパス
     * @param tabMode ベースファイル名一覧
     * @param isAddTmp TMP追加？ (true: TMP追加 false:TMP削除)
     * @param suffix 結果文字列 (null以外の場合、指定された文字列をファイル名の末尾に追加する)
     * @return 結果
     */
    private boolean renameImportFiles(String folder, int tabMode, boolean isAddTmp, String suffix) {
        boolean result = false;
        try {
            switch (tabMode) {
                case ProductionNaviUtils.IMPORT_TAB_IDX_CSV:
                    result = renameTempFiles(folder, kanbanCsvFilenames, isAddTmp, suffix);
                    break;
                case ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL:
                    File file = new File(folder);
                    result = renameTempFile(file.getParent(), file.getName(), isAddTmp, suffix);
                    break;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 読み込みを行うファイルに対しtmp_の接頭辞を付ける
     *
     * @param tabMode
     * @param folder
     */
    private void createTemp(int tabMode, String folder) {
        switch (tabMode) {
            case ProductionNaviUtils.IMPORT_TAB_IDX_CSV:
                getCsvFilenames().stream().map(
                        (csvFileName) -> new File(folder + File.separator + csvFileName))
                        .filter((csvFile) -> (csvFile.exists() && csvFile.isFile()))
                        .forEach((csvFile) -> {
                            File toFile = new File(
                                    csvFile.getParent() + File.separator + PREFIX_TMP + csvFile.getName());
                            if (toFile.exists()) {
                                toFile.delete();
                            }
                            csvFile.renameTo(toFile);
                        });
                break;

            case ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL:
                File file = new File(folder);
                if (file.exists() && file.isFile()) {
                    File toFile = new File(
                            file.getParent() + File.separator + PREFIX_TMP + file.getName());
                    if (toFile.exists()) {
                        toFile.delete();
                    }
                    file.renameTo(toFile);
                }
                break;

            default:
        }
    }

    /**
     * 読み込みが完了したファイルに対し、その読み込み結果に応じた拡張子を付けてリネームする
     *
     * <pre>
     * 一つでも読み込めた場合： .completed を付ける
     * 一つも読み込めなかった場合： .none を付ける
     * </pre>
     *
     * @param tabMode
     * @param result 読み込み結果が格納されたMap
     * @param folder
     * @param filenames 読み込みが完了したファイル名のリスト
     */
    private void renameCompleted(int tabMode, Map<String, Integer> result, String folder, List<String> filenames) {
        // 自動取込ON時の終了処理
        String suffix;
        if (isAutoImportStandby) {
            if (Objects.nonNull(result)
                    && (result.get("procNum") > 0) && (result.get("successNum") > 0)) {
                suffix = SUFFIX_COMPLETED;
            } else {
                suffix = SUFFIX_NONE;
            }
        } else {
            suffix = "";
        }

        try {
            switch (tabMode) {
                case ProductionNaviUtils.IMPORT_TAB_IDX_CSV:
                    filenames.stream().map(
                            (csvFileName) -> new File(folder + File.separator + PREFIX_TMP + csvFileName))
                            .filter((csvFile) -> (csvFile.exists() && csvFile.isFile()))
                            .forEach((csvFile) -> {
                                File toFile = new File(csvFile.getParent() + File.separator
                                        + csvFile.getName().replaceFirst(REGEX_PREFIX_TMP, "") + suffix);
                                if (toFile.exists()) {
                                    toFile.delete();
                                }
                                csvFile.renameTo(toFile);
                            });
                    break;

                case ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL:
                    File file = new File(folder);
                    file = new File(file.getParent() + File.separator + PREFIX_TMP + file.getName());
                    if (file.exists() && file.isFile()) {
                        File toFile = new File(file.getParent() + File.separator
                                + file.getName().replaceFirst(REGEX_PREFIX_TMP, "") + suffix);
                        if (toFile.exists()) {
                            toFile.delete();
                        }
                        file.renameTo(toFile);
                    }
                    break;

                default:
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 読み込みファイルに対し読み込みが失敗したことを表す拡張子(.error)を付ける
     *
     * @param tabMode
     * @param folder
     */
    private void renameFaild(int tabMode, String folder) {
        switch (tabMode) {
            case ProductionNaviUtils.IMPORT_TAB_IDX_CSV:
                getCsvFilenames().stream().map(
                        (csvFileName) -> new File(folder + File.separator + PREFIX_TMP + csvFileName))
                        .filter((csvFile) -> (csvFile.exists() && csvFile.isFile()))
                        .forEach((csvFile) -> {
                            File toFile = new File(csvFile.getParent() + File.separator
                                    + csvFile.getName().replaceFirst(REGEX_PREFIX_TMP, "") + SUFFIX_ERROR);
                            if (toFile.exists()) {
                                toFile.delete();
                            }
                            csvFile.renameTo(toFile);
                        });
                break;

            case ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL:
                File file = new File(folder);
                file = new File(file.getParent() + File.separator + PREFIX_TMP + file.getName());
                if (file.exists() && file.isFile()) {
                    File toFile = new File(file.getParent() + File.separator
                            + file.getName().replaceFirst(REGEX_PREFIX_TMP, "") + SUFFIX_ERROR);
                    if (toFile.exists()) {
                        toFile.delete();
                    }
                    file.renameTo(toFile);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 読み込みを実施したファイル(.completed または.error もしくは.noneのついたファイル)を削除する
     *
     * @param selectedMode
     */
    private void deleteCompleted(Integer selectedMode) {
        final List<String> csvFilenames = getCsvFilenames();

        switch (selectedMode) {
            case ProductionNaviUtils.IMPORT_TAB_IDX_CSV:
                String csvFolder = this.importCsvFileField.getText();
                csvFilenames.stream().map(
                        csvFilename -> new File(csvFolder + File.separator + csvFilename + SUFFIX_COMPLETED))
                        .filter((csvCompFile) -> csvCompFile.exists())
                        .forEach((csvCompFile) -> csvCompFile.delete());
                csvFilenames.stream().map(
                        csvFilename -> new File(csvFolder + File.separator + csvFilename + SUFFIX_ERROR))
                        .filter((csvErrFile) -> csvErrFile.exists())
                        .forEach((csvErrFile) -> csvErrFile.delete());
                csvFilenames.stream().map(
                        csvFilename -> new File(csvFolder + File.separator + csvFilename + SUFFIX_NONE))
                        .filter((csvErrFile) -> csvErrFile.exists())
                        .forEach((csvErrFile) -> csvErrFile.delete());
                break;

            case ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL:
                String xlsxFilename = this.importExcelFileField.getText();
                File xlsxCompFile = new File(xlsxFilename + SUFFIX_COMPLETED);
                if (xlsxCompFile.exists()) {
                    xlsxCompFile.delete();
                }
                File xlsxErrFile = new File(xlsxFilename + SUFFIX_ERROR);
                if (xlsxErrFile.exists()) {
                    xlsxErrFile.delete();
                }
                File xlsxNoneFile = new File(xlsxFilename + SUFFIX_NONE);
                if (xlsxNoneFile.exists()) {
                    xlsxNoneFile.delete();
                }
                break;
        }
    }

    /**
     * インポートの後処理を行なう。
     *
     * @param suffix リネーム拡張子
     * @param folder フォルダ
     * @param tabMode Tabモード
     */
    private void importPostProc(String suffix, String folder, int tabMode) {
        // インポートファイルをリネームする。
        renameImportFiles(folder, tabMode, false, suffix);

        addResult(LocaleUtils.getString("key.ImportKanbanEnd"));// 生産計画取り込み終了

        if (isAutoImportStandby) {
            timer.play();
        }
    }

    /**
     * カンバンを登録する。
     *
     * @param kanban 登録するカンバン
     * @param importKanban ロット生産フラグ, ロット数量情報を含むカンバンインポート情報
     * @return
     */
    private ResponseEntity registKanban(KanbanInfoEntity kanban) throws Exception {
        if (kanban.getProductionType() == 1) {
            // ロット一個流し生産のカンバンを登録
            KanbanCreateCondition cond = new KanbanCreateCondition(
                    kanban.getKanbanName(),
                    kanban.getFkWorkflowId(),
                    kanban.getParentId(),
                    loginUserInfoEntity.getLoginId(),
                    true,
                    kanban.getLotQuantity(),
                    kanban.getStartDatetime(),
                    new ArrayList<>(),
                    kanban.getProductionType()
            );
            return kanbanInfoFacade.createConditon(cond);
        } else {
            // 一個流し生産 or ロット生産のカンバンを登録
            return kanbanInfoFacade.regist(kanban);
        }
    }

    /**
     * プロパティを保存する。
     */
    private void saveProperties() {
        try {
            SingleSelectionModel<Tab> selectionModel = this.tabImportMode.getSelectionModel();
            Properties properties = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

            this.fromToPicker.save(properties);

            properties.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_TAB, String.valueOf(selectionModel.getSelectedIndex()));
            properties.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_CSV_PATH, this.importCsvFileField.getText());
            properties.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_XLS_PATH, this.importExcelFileField.getText());
            properties.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_IGNORE_EXISTING, String.valueOf(this.ignoreCheck.isSelected()));

            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * <basename>_yyyyMMddhhmmss.csvのファイルを検索する。
     *
     * @param base
     * @return
     */
    private String findCsvFile(File base) {
        String fileName = null;
        String basename = base.getName().substring(0, base.getName().lastIndexOf('.'));
        File[] files = base.getParentFile().listFiles((File file) -> file.getName().matches(basename + "_[0-9]{14}.csv"));
        if (Objects.nonNull(files)) {
            if (files.length < 1) {
                return fileName;
            }

            if (files.length == 1) {
                fileName = files[0].getName();
            } else {
                List<File> list = Arrays.asList(files);
                Collections.sort(list, (File f1, File f2) -> f1.getName().compareTo(f2.getName()));
                fileName = list.get(files.length - 1).getName();
            }
        }
        return fileName;
    }

    /**
     * ファイルを削除する。
     *
     * @param folder
     * @param fileNames
     * @return
     */
    private boolean deleteFiles(String folder, List<String> fileNames) {
        boolean result = true;
        for (String fileName : fileNames) {
            if (StringUtils.isEmpty(fileName)) {
                continue;
            }

            String path = folder + File.separator + fileName;
            File file = new File(path);
            file.delete();
            logger.info("Deleted file: " + path);
        }
        return result;
    }

    /**
     * 工程カンバンプロパティを読み込む。
     *
     * @param format
     * @return
     */
    private List<ImportWorkKanbanPropertyCsv> importWorkKanbanPropCsv(String folder, WorkKanbanPropFormatInfo format) {
        List<ImportWorkKanbanPropertyCsv> result = null;

        if (!StringUtils.isEmpty(format.getFileName())) {
            this.addResult(LocaleUtils.getString("key.import.work.plan.work.kanban.property") + " [" + format.getFileName() + "]");

            String path = folder + File.separator + PREFIX_TMP + format.getFileName();
            result = this.readWorkKanbanPropertyCsv(path, format);

            if (Objects.isNull(result)) {
                // ファイルが読み込めませんでした。
                this.addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readFile"), format.getCsvFileName()));
                result = new ArrayList();
            }
        } else {
            result = new ArrayList();
        }

        return result;
    }

    /**
     * CSVファイルを検索して、ファイル名を設定する。
     *
     * @param folder
     * @param format
     */
    private void updateCsvFilename(String folder, WorkKanbanPropFormatInfo format) {
        String fileName = format.getCsvFileName();

        if (this.importFileMap.containsKey(fileName)) {
            fileName = this.importFileMap.get(fileName);
        } else {
            String filePath = folder + File.separator + fileName;
            File file = new File(filePath);

            if (!(file.exists() && file.isFile())) {
                fileName = this.findCsvFile(file);
                if (!StringUtils.isEmpty(fileName)) {
                    this.importFileMap.put(format.getCsvFileName(), fileName);
                }
            }
        }

        format.setFileName(fileName);
    }

    /**
     * エンコード文字列を大文字に変換する。(SJISはMS932に変換する)
     *
     * @param fileEncode エンコード文字列
     * @return 大文字のエンコード文字列
     */
    private String encodeUpperCase(String fileEncode) {
        // ファイルの文字コード
        String encode = fileEncode.toUpperCase();
        // シフトJISの場合はMS932を指定する。
        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS", "S-JIS").contains(encode)) {
            encode = "MS932";
        }
        return encode;
    }

    /**
     * 工程階層を追加
     * @param hierarchyName 工程階層名
     * @return 追加した工程階層ID
     */
    private Optional<Long> registWorkHierarchy(String hierarchyName, Long parentId) {
        if (StringUtils.isEmpty(hierarchyName)) {
            return Optional.empty();
        }

        WorkHierarchyInfoEntity hierarchy = new WorkHierarchyInfoEntity();
        hierarchy.setHierarchyName(hierarchyName);
        hierarchy.setParentId(parentId);

        ResponseEntity res = workHierarchyInfoFacade.registHierarchy(hierarchy);
        if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
            Long workHierarchywId = Long.parseLong(res.getUri().substring(res.getUri().lastIndexOf("/") + 1));
            return Optional.of(workHierarchywId);
        }
        return Optional.empty();
    }

    /**
     * 工程の階層を取得 無ければ　生成
     * @param hierarchyName
     * @return
     */
    private Optional<Long> getOrRegistWorkHierarchyId(String hierarchyName) {
        try {
            String seprator = AdProperty.getProperties().getProperty("hierarchySeprator", "[/|\\\\]");

            List<String> hierarchyNames
                    = Arrays.stream(hierarchyName.split(seprator))
                    .filter(StringUtils::nonEmpty)
                    .distinct()
                    .collect(toList());

            Long parentId = 0L;
            for (String name : hierarchyNames) {
                WorkHierarchyInfoEntity workHierarchyInfo = workHierarchyInfoFacade.findHierarchyName(URLEncoder.encode(name, CHARSET));
                if (Objects.nonNull(workHierarchyInfo.getWorkHierarchyId())) {
                    if (parentId != 0 && !parentId.equals(workHierarchyInfo.getParentId())) {
                        workHierarchyInfo.setParentId(parentId);
                        workHierarchyInfoFacade.updateHierarchy(workHierarchyInfo);
                    }
                    parentId = workHierarchyInfo.getWorkHierarchyId();
                    continue;
                }
                Optional<Long> optParentId = registWorkHierarchy(name, parentId);
                if (!optParentId.isPresent()) {
                    return Optional.empty();
                }
                parentId = optParentId.get();
            }
            return Optional.of(parentId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Optional.empty();
        }
    }


    /**
     * 工程順階層を追加
     * @param hierarchyName 工程順階層名
     * @return 工程順階層ID
     */
    private Optional<Long> registWorkflowHierarchy(String hierarchyName, Long parentId) {
        if (StringUtils.isEmpty(hierarchyName)) {
            return Optional.empty();
        }

        WorkflowHierarchyInfoEntity hierarchy = new WorkflowHierarchyInfoEntity();
        hierarchy.setHierarchyName(hierarchyName);
        hierarchy.setParentId(parentId);

        ResponseEntity res = workflowHierarchyInfoFacade.registHierarchy(hierarchy);
        if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
            Long workflowHierarchywId = Long.parseLong(res.getUri().substring(res.getUri().lastIndexOf("/") + 1));
            return Optional.of(workflowHierarchywId);
        }
        return Optional.empty();
    }

    /**
     * 工程階層を取得
     * @param hierarchyName 階層名
     * @return 階層
     */
    private Optional<Long> getOrRegistWorkflowHierarchyId(String hierarchyName) {
        try {
            String seprator = AdProperty.getProperties().getProperty("hierarchySeprator", "[/|\\\\]");

            List<String> hierarchyNames
                    = Arrays.stream(hierarchyName.split(seprator))
                    .filter(StringUtils::nonEmpty)
                    .distinct()
                    .collect(toList());

            Long parentId = 0L;
            for (String name : hierarchyNames) {
                WorkflowHierarchyInfoEntity workflowHierarchyInfo = workflowHierarchyInfoFacade.findHierarchyName(URLEncoder.encode(name, CHARSET));
                if (Objects.nonNull(workflowHierarchyInfo.getWorkflowHierarchyId())) {
                    if (parentId != 0 && !parentId.equals(workflowHierarchyInfo.getParentId())) {
                        workflowHierarchyInfo.setParentId(parentId);
                        workflowHierarchyInfoFacade.updateHierarchy(workflowHierarchyInfo);
                    }
                    parentId = workflowHierarchyInfo.getWorkflowHierarchyId();
                    continue;
                }
                Optional<Long> optParentId = registWorkflowHierarchy(name, parentId);
                if (!optParentId.isPresent()) {
                    return Optional.empty();
                }
                parentId = optParentId.get();
            }
            return Optional.of(parentId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Optional.empty();
        }
    }


    /**
     * カンバン階層を登録
     * @param hierarchyName カンバン階層名
     * @return カンバン階層ID
     */
    private Optional<Long> registKanbanHierarchy(String hierarchyName, Long parentId) {
        if (StringUtils.isEmpty(hierarchyName)) {
            return Optional.empty();
        }

        KanbanHierarchyInfoEntity hierarchy = new KanbanHierarchyInfoEntity();
        hierarchy.setHierarchyName(hierarchyName);
        hierarchy.setParentId(parentId);
        ResponseEntity res = kanbanHierarchyFacade.regist(hierarchy);
        if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
            Long kanbanHierarchyId = Long.parseLong(res.getUri().substring(res.getUri().lastIndexOf("/") + 1));
            return Optional.of(kanbanHierarchyId);
        }

        return Optional.empty();
    }

    /**
     * カンバン階層を
     * @param hierarchyName
     * @return
     */
    public Optional<Tuple<String, String>> getOrRegistKanbanHierarchy(String hierarchyName) {
        try {
            String seprator = AdProperty.getProperties().getProperty("hierarchySeprator", "[/|\\\\]");
            
            List<String> hierarchyNames
                    = Arrays.stream(hierarchyName.split(seprator))
                    .filter(StringUtils::nonEmpty)
                    .distinct()
                    .collect(toList());

            Long parentId = 0L;
            for (String name : hierarchyNames) {
                KanbanHierarchyInfoEntity kanbanHierarchyInfo = kanbanHierarchyFacade.findHierarchyName(URLEncoder.encode(name, CHARSET));
                if (Objects.nonNull(kanbanHierarchyInfo.getKanbanHierarchyId())) {
                    if (parentId != 0 && !parentId.equals(kanbanHierarchyInfo.getParentId())) {
                        kanbanHierarchyInfo.setParentId(parentId);
                        kanbanHierarchyFacade.update(kanbanHierarchyInfo);
                    }
                    parentId = kanbanHierarchyInfo.getKanbanHierarchyId();
                    continue;
                }
                Optional<Long> optParentId = registKanbanHierarchy(name, parentId);
                if (!optParentId.isPresent()) {
                    return Optional.empty();
                }
                parentId = optParentId.get();
            }
            return Optional.of(new Tuple<>(hierarchyName, hierarchyNames.get(hierarchyNames.size() - 1)));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Optional.empty();
        }
    }



    /**
     * カンバンプロパティ情報チェック
     *
     * @param importKanbans カンバン情報
     * @param importKanbanProps カンバンプロパティ情報
     * @param line 読み出し開始行
     * @return 登録対象のカンバンプロパティ情報
     */
    private List<ImportKanbanPropertyCsv> checkImportKanbanProps(List<ImportKanbanCsv> importKanbans, List<ImportKanbanPropertyCsv> importKanbanProps, Integer line) {
        if (Objects.isNull(importKanbans) || importKanbans.isEmpty()) {
            return importKanbanProps;
        }

        List<ImportKanbanPropertyCsv> retList = new ArrayList();
        int rowNum = line - 1;
        for (ImportKanbanPropertyCsv prop : importKanbanProps) {
            ++rowNum;
            String propName = prop.getKanbanPropertyName();
            if (StringUtils.isEmpty(propName)) {
                // プロパティ名が設定されていない場合
                addResult(String.format("[%d] > %s",
                        rowNum,
                        LocaleUtils.getString("key.import.skip.propertyNameNotInput")));
                continue;
            }

            boolean isKanbanName = importKanbans
                            .stream()
                            .anyMatch(p -> StringUtils.equals(prop.getKanbanName(), p.getKanbanName()));
            if (!isKanbanName) {
                // カンバン情報に一致するカンバン名が無い
                addResult(String.format("[%d] > %s [%s: %s]",
                        rowNum,
                        LocaleUtils.getString("key.import.skip.kanbanNameNotMatch"),
                        LocaleUtils.getString("key.KanbanName"),
                        prop.getKanbanName()));
                continue;
            }

            if (StringUtils.isEmpty(prop.getWorkflowName())) {
                retList.add(prop);
                continue;
            }

            boolean isWorkflowName = importKanbans
                    .stream()
                    .filter(p -> StringUtils.equals(prop.getKanbanName(), p.getKanbanName()))
                    .anyMatch(p -> StringUtils.equals(prop.getWorkflowName(), p.getWorkflowName()));
            if (!isWorkflowName) {
                // カンバン情報に一致する工程順名が無い場合
                addResult(String.format("[%d] > %s [%s: %s]",
                        rowNum,
                        LocaleUtils.getString("key.import.skip.workflowNameNotMatch"),
                        LocaleUtils.getString("key.WorkflowName"),
                        prop.getWorkflowName()));
            } else {
                retList.add(prop);
            }
        }
        return retList;
    }

    /**
     * 工程情報インポート
     *
     * @param folder フォルダ
     * @param workHeaderFormatInfo 設定情報
     */
    private Optional<Map<String, WorkInfoEntity>> importWork(String folder, ImportHeaderFormatInfo workHeaderFormatInfo) {

        logger.info("importWork Start");
        // 工程csvファイル読込
        Optional<Map<String, WorkInfoEntity>> optWorkInfoEntities = loadWorkCsvFile(folder, workHeaderFormatInfo.getWorkHeaderFormatInfo());
        if (!optWorkInfoEntities.isPresent()) {
            return Optional.empty();
        }

        if(optWorkInfoEntities.get().size() == 0) {
            return Optional.of(new HashMap<>());
        }

        // 工程プロパティcsvファイル読込
        Optional<Map<String, List<WorkPropertyInfoEntity>>> optWorkPropInfoEntities = loadWorkPropCsvFile(folder, workHeaderFormatInfo.getWorkPropHeaderFormatInfo());
        if (!optWorkPropInfoEntities.isPresent()) {
            return Optional.empty();
        }

        if (optWorkPropInfoEntities.get().size() != 0) {
            for (Map.Entry<String, WorkInfoEntity> workInfoEntityEntry : optWorkInfoEntities.get().entrySet()) {
                List<WorkPropertyInfoEntity> workPropertyInfo = optWorkPropInfoEntities.get().get(workInfoEntityEntry.getKey());
                if (Objects.nonNull(workPropertyInfo)) {
                    workInfoEntityEntry.getValue().setPropertyInfoCollection(workPropertyInfo);
                } else {
                    // エラーメッセージ
                    addResult(String.format(" > %s [%s: %s]",
                            LocaleUtils.getString("key.import.skip.processNameNotMatch"),
                            LocaleUtils.getString("key.ProcessName"),
                            workInfoEntityEntry.getKey()));
                }
            }
        }

        List<WorkInfoEntity> workInfoEntityList = new ArrayList<>(optWorkInfoEntities.get().values());

        logger.info("get WorkHierarchy Start");
        // 工程階層IDを設定
        List<String> parentNames
                = workInfoEntityList
                .stream()
                .map(WorkInfoEntity::getParentName)
                .distinct()
                .collect(toList());

        Map<String, Long> hierarchyMap
                = parentNames
                .stream()
                .map(parentName -> new Tuple<>(parentName, getOrRegistWorkHierarchyId(parentName)))// 階層を取得, 無ければ生成
                .filter(pair -> pair.getRight().isPresent())
                .collect(toMap(Tuple::getLeft, pair->pair.getRight().get()));

        // 階層がない
        if (parentNames.size() != hierarchyMap.size()) {
            parentNames
                    .stream()
                    .filter(parentName -> !hierarchyMap.containsKey(parentName))
                    .forEach(parentName -> {
                        addResult(String.format("  > %s [%s]",
                                LocaleUtils.getString("key.ImportKanban_WorkHierarchyNothing"),
                                parentName));
                    });
            return Optional.empty();
        }

        workInfoEntityList.forEach(workInfoEntity -> workInfoEntity.setParentId(hierarchyMap.get(workInfoEntity.getParentName())));

        // タイムアウト対策の為分割する
        Map<String, List<WorkInfoEntity>> importWorkList = new LinkedHashMap<>();
        for (int n = 0; n < workInfoEntityList.size(); ) {
            int next = Math.min(n + 100, workInfoEntityList.size());
            importWorkList.put("[" + n + "]～[" + (next-1) + "]", workInfoEntityList.subList(n, next));
            n = next;
        }

        Map<String, String> workIdMap = new HashMap<>();
        for (Map.Entry<String, List<WorkInfoEntity>> entities : importWorkList.entrySet()) {
            addResult(String.format("%s %s %s",
                            LocaleUtils.getString("key.import.production.work"),
                            entities.getKey(),
                            LocaleUtils.getString("key.Regist")));

            ResponseEntity workUpdateRes = workInfoFacade.addAll(entities.getValue(), true);
            if (Objects.nonNull(workUpdateRes) && !ResponseAnalyzer.getAnalyzeResult(workUpdateRes)) {
                // 失敗処理
                return Optional.empty();
            }
            workIdMap.putAll(JsonUtils.jsonToMap(workUpdateRes.getUri()));
        }

        long processNum = workInfoEntityList.size();
        long successNum = workIdMap.size();
        long failedNum = processNum - successNum;

        // 返値からログを出力
        String resultMessage = String.format("%s: %s (%s: %s, %s: %s)",
                LocaleUtils.getString("key.RegistProcess"), processNum,
                LocaleUtils.getString("key.ImportKanban_SuccessNum"), successNum,
                LocaleUtils.getString("key.ImportKanban_FailedNum"), failedNum);

        addResult(resultMessage);
        logger.info(resultMessage);


        return Optional.of(
                workInfoEntityList
                        .stream()
                        .peek(entity -> entity.setWorkId(Long.parseLong(workIdMap.getOrDefault(entity.getWorkName(), "0"))))
                        .collect(toMap(WorkInfoEntity::getWorkName, Function.identity())));
    }

    /**
     * 工程順情報インポート
     *
     * @param folder フォルダ
     * @param workHeaderFormatInfo 設定情報
     * @param workMap 工程情報
     * @param organizationIdentifyMap 組織一覧
     * @param equipmentIdentifyMap 設備一覧
     * @return boolean
     */
    private boolean importWorkflow(String folder, ImportHeaderFormatInfo workHeaderFormatInfo, Map<String, WorkInfoEntity> workMap, Map<String, Long> allOrganizationIdentify, Map<String, Long> allEquipmentIdentify) {
        // 工程順csvファイル読込
        Optional<Map<String, WorkflowInfoEntity>> optWorkflowInfoEntities = loadWorkflowCsvFile(folder, workHeaderFormatInfo.getWorkflowHeaderFormatInfo());
        if (!optWorkflowInfoEntities.isPresent()) {
            return false;
        }

        if(optWorkflowInfoEntities.get().size()==0) {
            return true;
        }
        Map<String, WorkflowInfoEntity> workflowInfoEntities = optWorkflowInfoEntities.get();

        // 工程順プロパティcsvファイル読込
        Optional<Map<String, List<KanbanPropertyTemplateInfoEntity>>> optWorkflowPropInfoEntities = loadWorkflowPropCsvFile(folder, workHeaderFormatInfo.getWorkflowPropHeaderFormatInfo());
        if (!optWorkflowPropInfoEntities.isPresent()) {
            return false;
        }
        Map<String, List<KanbanPropertyTemplateInfoEntity>> workflowPropInfoEntities = optWorkflowPropInfoEntities.get();

        if (optWorkflowPropInfoEntities.get().size()!=0) {
            for (Map.Entry<String, WorkflowInfoEntity> workflowInfoEntityEntry : workflowInfoEntities.entrySet()) {
                List<KanbanPropertyTemplateInfoEntity> workPropertyInfo = workflowPropInfoEntities.get(workflowInfoEntityEntry.getKey());
                if (Objects.nonNull(workPropertyInfo)) {
                    workflowInfoEntityEntry.getValue().setKanbanPropertyTemplateInfoCollection(workPropertyInfo);
                } else {
                    // エラーメッセージ
                    addResult(String.format(" > %s [%s: %s]",
                            LocaleUtils.getString("key.import.skip.workflowDataNotMatch"),
                            LocaleUtils.getString("key.OrderProcessesName"),
                            workflowInfoEntityEntry.getKey()));
                }
            }
        }

        // 工程階層IDを設定
        List<String> parentNames
                = workflowInfoEntities
                .values()
                .stream()
                .map(WorkflowInfoEntity::getParentName)
                .distinct()
                .collect(toList());

        Map<String, Long> hierarchyMap
                = parentNames
                .stream()
                .map(parentName -> new Tuple<>(parentName, getOrRegistWorkflowHierarchyId(parentName))) // 工程順IDを取得　なければ　追加
                .filter(pair -> pair.getRight().isPresent())
                .collect(toMap(Tuple::getLeft, pair -> pair.getRight().get()));

        if (parentNames.size() != hierarchyMap.size()) {
            parentNames
                    .stream()
                    .filter(parentName -> !hierarchyMap.containsKey(parentName))
                    .forEach( parentName -> {
                        addResult(String.format("  > %s [%s]",
                                LocaleUtils.getString("key.ImportKanban_WorkflowHierarchyNothing"),
                                parentName));
                    });
            return false;
        }

        workflowInfoEntities
                .values()
                .forEach(workflowInfoEntity -> workflowInfoEntity.setParentId(hierarchyMap.get(workflowInfoEntity.getParentName())));

        List<String> errorMessage = new ArrayList<>();
        // workIdの無い工程
        List<String> searchWorkName
                = workflowInfoEntities.values()
                .stream()
                .map(WorkflowInfoEntity::getConWorkflowWorkInfoCollection)
                .flatMap(Collection::stream)
                .map(ConWorkflowWorkInfoEntity::getWorkName)
                .filter(workName -> !workMap.containsKey(workName))
                .distinct()
                .collect(toList());

        if (!searchWorkName.isEmpty()) {
            List<WorkInfoEntity> workInfoEntities = workInfoFacade.findAllByName(searchWorkName);
            workInfoEntities.forEach(entity-> workMap.put(entity.getWorkName(), entity));

            if (searchWorkName.size() != workInfoEntities.size()) {
                // 未登録の工程がある。
                searchWorkName
                        .stream()
                        .filter(workName -> !workMap.containsKey(workName))
                        .forEach(workName -> errorMessage.add(String.format(" > %s [%s: %s]",
                                String.format(LocaleUtils.getString("key.NotData"), LocaleUtils.getString("key.Process")),
                                LocaleUtils.getString("key.ProcessName"),
                                workName)));
            }
        }

        // 検索する組織ID
        List<String> searchOrganizationIdentify
                = workflowInfoEntities.values()
                .stream()
                .map(WorkflowInfoEntity::getConWorkflowWorkInfoCollection)
                .flatMap(Collection::stream)
                .map(ConWorkflowWorkInfoEntity::getOrganizationIdentifyCollection)
                .flatMap(Collection::stream)
                .distinct()
                .filter(organizationIdentify -> !allOrganizationIdentify.containsKey(organizationIdentify))
                .filter(StringUtils::nonEmpty)
                .collect(toList());

        if (!searchOrganizationIdentify.isEmpty()) {
            Map<String, Long> foundOrganizationIdentifyMap
                = searchOrganizationIdentify
                    .stream()
                    .map(organizationInfoFacade::findName)
                    .filter(Objects::nonNull)
                    .filter(entity->StringUtils.nonEmpty(entity.getOrganizationIdentify()))
                    .collect(toMap(OrganizationInfoEntity::getOrganizationIdentify, OrganizationInfoEntity::getOrganizationId, (a,b)->a));

            if (searchOrganizationIdentify.size() != foundOrganizationIdentifyMap.size()) {
                // 未登録組織
                searchOrganizationIdentify
                        .stream()
                        .filter(organizationIdentify -> !foundOrganizationIdentifyMap.containsKey(organizationIdentify))
                        .forEach( organizationIdentify -> errorMessage.add(String.format(" > %s [%s: %s]",
                                LocaleUtils.getString("key.NoRegistOrganization"),
                                LocaleUtils.getString("key.OrganizationName"),
                                organizationIdentify)));
            }
            allOrganizationIdentify.putAll(foundOrganizationIdentifyMap);
        }
        // 検索する設備ID
        List<String> searchEquipmentIdentify
                = workflowInfoEntities.values()
                .stream()
                .map(WorkflowInfoEntity::getConWorkflowWorkInfoCollection)
                .flatMap(Collection::stream)
                .map(ConWorkflowWorkInfoEntity::getEquipmentIdentifyCollection)
                .flatMap(Collection::stream)
                .distinct()
                .filter(equipmentIdentify -> !allEquipmentIdentify.containsKey(equipmentIdentify))
                .filter(StringUtils::nonEmpty)
                .collect(toList());

        if (!searchEquipmentIdentify.isEmpty()) {
            Map<String, Long> foundEquipmentIdentifyMap
                    = searchEquipmentIdentify
                    .stream()
                    .map(equipmentInfoFacade::findName)
                    .filter(Objects::nonNull)
                    .filter(entity->StringUtils.nonEmpty(entity.getEquipmentIdentify()))
                    .collect(toMap(EquipmentInfoEntity::getEquipmentIdentify, EquipmentInfoEntity::getEquipmentId));

            if (searchEquipmentIdentify.size() != foundEquipmentIdentifyMap.size()) {
                // 未登録の設備
                searchEquipmentIdentify
                        .stream()
                        .filter(equipmentIdentify -> !foundEquipmentIdentifyMap.containsKey(equipmentIdentify))
                        .forEach( equipmentIdentify -> errorMessage.add(String.format(" > %s [%s: %s]",
                                LocaleUtils.getString("key.NoRegistEquipment"),
                                LocaleUtils.getString("key.EquipmentName"),
                                equipmentIdentify)));
            }
            allEquipmentIdentify.putAll(foundEquipmentIdentifyMap);
        }

        if (!errorMessage.isEmpty()) {
            errorMessage.forEach(this::addResult);
            return false;
        }

        // 作業中かをチェックする
        if (workHeaderFormatInfo.getWorkflowHeaderFormatInfo().getIsReschedule()) {
            List<String> workflowNames =
                    workflowInfoEntities
                            .values()
                            .stream()
                            .map(WorkflowInfoEntity::getWorkflowName)
                            .distinct()
                            .filter(StringUtils::nonEmpty)
                            .collect(toList());
            Long count = workflowInfoFacade.countWorkingWorkKanbanByWorkflowNames(workflowNames);
            if (count != 0) {
                List<KanbanInfoEntity> kanbans = kanbanInfoFacade.getKanbanWithWorkingWorkByWorkflowNames(workflowNames);
                addResult(String.format("  > %s", LocaleUtils.getString("key.NeedStopWorking")));
                addResult(String.format("    > %s: %s", LocaleUtils.getString("key.KanbanName"), kanbans.stream().map(KanbanInfoEntity::getKanbanName).collect(joining(", "))));
                return false;
            }
        }


        // 工程順工程関連付け情報にIDを設定
        for (WorkflowInfoEntity workflowInfoEntity : workflowInfoEntities.values()) {
            for (ConWorkflowWorkInfoEntity conWorkflowWorkInfoEntity: workflowInfoEntity.getConWorkflowWorkInfoCollection()) {
                final WorkInfoEntity entity = workMap.get(conWorkflowWorkInfoEntity.getWorkName());
                if (Objects.isNull(entity)) {
                    continue;
                }
                final Long workId = entity.getWorkId();
                if (Objects.isNull(workId)) {
                    continue;
                }
                conWorkflowWorkInfoEntity.setFkWorkId(workId);

                final List<Long> organizationIds
                        = conWorkflowWorkInfoEntity
                        .getOrganizationIdentifyCollection()
                        .stream()
                        .map(allOrganizationIdentify::get)
                        .filter(Objects::nonNull)
                        .collect(toList());
                conWorkflowWorkInfoEntity.setOrganizationCollection(organizationIds);


                final List<Long> equipmentIds
                        = conWorkflowWorkInfoEntity
                        .getEquipmentIdentifyCollection()
                        .stream()
                        .map(allEquipmentIdentify::get)
                        .filter(Objects::nonNull)
                        .collect(toList());
                conWorkflowWorkInfoEntity.setEquipmentCollection(equipmentIds);
            }
        }

        // 工程を直列で取り込むか?
        final boolean isSerial = Objects.equals(WorkflowHeaderFormatInfo.PROCESS_TYPE.SERIAL, workHeaderFormatInfo.getWorkflowHeaderFormatInfo().getHeaderCsvProcCon());
        Date zero = null;
        try {
            zero =  new SimpleDateFormat("HH:mm").parse("00:00");
        } catch (ParseException e) {
            return false;
        }


        // 標準時間の更新
        for (WorkflowInfoEntity workflowInfoEntity : workflowInfoEntities.values()) {
            long time = zero.getTime();
            Date startTIme = zero;
            for (ConWorkflowWorkInfoEntity work : workflowInfoEntity.getConWorkflowWorkInfoCollection()) {
                WorkInfoEntity workInfoEntity = workMap.get(work.getWorkName());
                if (Objects.nonNull(workInfoEntity)) {
                    final Long taktTime = Long.valueOf(workInfoEntity.getTaktTime());
                    if (Objects.nonNull(taktTime)) {
                        time += workInfoEntity.getTaktTime();
                    }
                }
                Date endTime = new Date(time);
                work.setStandardStartTime(startTIme);
                work.setStandardEndTime(endTime);
                startTIme = endTime;
            }
        }


        // ダイアグラム生成
        for (WorkflowInfoEntity workflowInfoEntity : workflowInfoEntities.values()) {
            WorkflowModel workflowModel = new WorkflowModel();
            WorkflowPane pane = workflowModel.getWorkflowPane();
            pane.setWorkflowEntity(workflowInfoEntity);
            workflowModel.createWorkflow(pane, true, true);

            CellBase selectedCell = workflowModel.getWorkflowPane().getCellList().get(0);
            selectedCell.setSelected(true);

            for (ConWorkflowWorkInfoEntity work : workflowInfoEntity.getConWorkflowWorkInfoCollection()) {


                if (selectedCell instanceof StartCell || isSerial) {
                    // 直列 or 並列の1工程目
                    WorkCell workCell = workflowModel.createWorkCell(work);
                    workflowModel.add(selectedCell, workCell);
                    workCell.setSelected(true);
                    workflowModel.updateTimetable(workCell, 0, false);
                    workflowModel.updateWorkflowOrder();
                    selectedCell = workCell;
                } else {
                    // 並列
                    ParallelStartCell parallelStartCell = workflowModel.createParallelStartCell();
                    ParallelEndCell parallelEndCell = workflowModel.createParallelEndCell(parallelStartCell);
                    WorkCell workCell = workflowModel.createWorkCell(work);
                    CellBase previousCell = workflowModel.getWorkflowPane().getPreviousCell((WorkCell) selectedCell);
                    if (Objects.nonNull(previousCell)
                            && workflowModel.remove(selectedCell)
                            && workflowModel.addGateway(previousCell, parallelStartCell, parallelEndCell)
                            && workflowModel.add(parallelStartCell, (WorkCell) selectedCell)
                            && workflowModel.add(parallelStartCell, workCell)) {
                        workflowModel.updateTimetable(workCell, 0, false);
                        workflowModel.updateWorkflowOrder();
                    } else {
                        // 挿入
                        parallelStartCell = workflowModel.getParallelStartCell((WorkCell) selectedCell);
                        List<CellBase> cells = parallelStartCell.getFirstRow();
                        int index = cells.indexOf(selectedCell) + 1;
                        if (workflowModel.addWithUpdateTimetable(parallelStartCell, index, workCell)) {
                            workflowModel.updateTimetable(workCell, 0, false);
                            workflowModel.updateWorkflowOrder();
                        }
                    }
                }
            }
            // getWorkflow内でWorkflowDiaglamのセットを行う
            workflowModel.getWorkflow();
        }

        List<WorkflowInfoEntity> importWorkflows = new ArrayList<>(workflowInfoEntities.values());

        // --------- 工程順を登録
        // タイムアウト対策の為分割
        Map<String, List<WorkflowInfoEntity>> importWorkflowList = new LinkedHashMap<>();
        for (int n = 0; n < importWorkflows.size(); ) {
            int next = Math.min(n + 100, importWorkflows.size());
            importWorkflowList.put("[" + n + "]～[" + (next-1) + "]", importWorkflows.subList(n, next));
            n = next;
        }

        Map<String, String> workflowIdMap = new HashMap<>();
        // def 60 * 1000
        final Integer timeout = workflowInfoFacade.getReadTimeout();
        workflowInfoFacade.setReadTimeout(3600*1000); // 60分
        List<ResultResponse> result = new ArrayList<>();
        for (Map.Entry<String, List<WorkflowInfoEntity>> entities : importWorkflowList.entrySet()) {
            // 工程順一括登録
            addResult(String.format("%s %s %s",
                            LocaleUtils.getString("key.import.production.workflow"),
                            entities.getKey(),
                            LocaleUtils.getString("key.Regist")));
            List<ResultResponse> workflowUpdateRes = workflowInfoFacade.addAll(entities.getValue(), workHeaderFormatInfo.getWorkflowHeaderFormatInfo().getIsReschedule());
            if (Objects.isNull(workflowUpdateRes)) {
//                addResult("取込タイムアウトが発生しました。");
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
        
        long processNum = importWorkflows.size();
        long successNum = result.size();
        long failedNum = processNum - successNum;

        // 返値からログを出力
//        String resultMessage = String.format("%s: %s (%s: %s, %s: %s)",
//                LocaleUtils.getString("key.RegistOrderProcesses"), processNum,
//                LocaleUtils.getString("key.ImportKanban_SuccessNum"), successNum,
//                LocaleUtils.getString("key.ImportKanban_FailedNum"), failedNum);
//
//        addResult(resultMessage);
//        logger.info(resultMessage);


        Map<String, Map<ServerErrorTypeEnum, List<Tuple<ResultResponse, List<MessageEntity>>>>> resultMap =
                result.stream()
                        .filter(entity -> Objects.nonNull(entity.getResult()))
                        .map(entity -> new Tuple<>(entity, JsonUtils.jsonToObjects(entity.getResult(), MessageEntity[].class)))
                        .filter(entity -> !entity.getRight().isEmpty())
                        .collect(
                                Collectors.groupingBy(entity -> entity.getRight().get(0).getAddInfo(),
                                        Collectors.groupingBy(entity -> entity.getLeft().getErrorType()))
                        );

        // 工程登録用メッセージ表示
        boolean isSuccessRegistWorkflow = true;
        Map<ServerErrorTypeEnum, List<Tuple<ResultResponse, List<MessageEntity>>>> workflowResultMap = resultMap.get("workflow");
        if(Objects.nonNull(workflowResultMap)) {
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
            isSuccessRegistWorkflow = workflowFailedNum <= 0;
        }

        boolean isSuccessRegistKanban = true;
        Map<ServerErrorTypeEnum, List<Tuple<ResultResponse, List<MessageEntity>>>> kanbanResultMap = resultMap.get("kanban");
        if(Objects.nonNull(kanbanResultMap)) {
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
            isSuccessRegistKanban = kanbanFailedNum <= 0;
        }
        return isSuccessRegistKanban && isSuccessRegistWorkflow;
    }

    /**
     * カンバン情報インポート
     *
     * @param folder フォルダ
     * @param kanbanHeaderFormatInfo 設定情報
     * @throws Exception
     */
    private boolean importKanban(String folder, ImportHeaderFormatInfo importHeaderFormatInfo,  Map<String, Long> allOrganizationIdentify, Map<String, Long> allEquipmentIdentify) {
        // カンバンcsvファイルの読み込み
        Optional<Map<String, KanbanInfoEntity>> optKanbanInfoEntities = loadKanbanCsvFile(folder, importHeaderFormatInfo.getKanbanHeaderFormatInfo());
        if (!optKanbanInfoEntities.isPresent()) {
            return false;
        }
        Map<String, KanbanInfoEntity> kanbanInfoEntityMap = optKanbanInfoEntities.get();

        if (kanbanInfoEntityMap.isEmpty()) {
            return true;
        }

        // カンバンプロパティファイル読込
        Optional<Map<String, List<KanbanPropertyInfoEntity>>> optKanbanPropertyInfoEntities = loadKanbanPropCsvFile(folder, importHeaderFormatInfo.getKanbanPropHeaderFormatInfo()) ;
        if (!optKanbanPropertyInfoEntities.isPresent()) {
            return false;
        }
        Map<String, List<KanbanPropertyInfoEntity>> kanbanPropertyInfoEntities = optKanbanPropertyInfoEntities.get();

        // 工程カンバンcsvファイル読込
        Optional<Map<String, Map<String, WorkKanbanInfoEntity>>> optWorkKanbanInfoEntities = loadWorkKanbanCsvFile(folder, importHeaderFormatInfo.getWorkKanbanHeaderFormatInfo()) ;
        if (!optWorkKanbanInfoEntities.isPresent()) {
            return false;
        }
        Map<String, Map<String, WorkKanbanInfoEntity>> workKanbanInfoEntities = optWorkKanbanInfoEntities.get();

        List<String> errorMessages = new ArrayList<>();
        List<WorkKanbanInfoEntity> workKanbanInfoEntityList
                = workKanbanInfoEntities
                .values()
                .stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .collect(toList());

        // 組織の有無確認
        workKanbanInfoEntityList
                .stream()
                .map(WorkKanbanInfoEntity::getOrganizationIdentifyCollection)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .filter(StringUtils::nonEmpty)
                .filter(organization -> !allOrganizationIdentify.containsKey(organization))
                .forEach(organization -> {
                    errorMessages.add(String.format(" > %s [%s]", LocaleUtils.getString("key.NoRegistOrganization"), organization));
                });

        workKanbanInfoEntityList
                .stream()
                .map(WorkKanbanInfoEntity::getEquipmentIdentifyCollection)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .filter(StringUtils::nonEmpty)
                .filter(equipment -> !allEquipmentIdentify.containsKey(equipment))
                .forEach(equipment -> {
                    errorMessages.add(String.format(" > %s [%s]", LocaleUtils.getString("key.NoRegistEquipment"), equipment));
                });

        if (!errorMessages.isEmpty()) {
            errorMessages.stream().forEach(this::addResult);
            return false;
        }


        // 工程カンバンプロパティcsv読込
        Optional<Map<String, Map<String, List<WorkKanbanPropertyInfoEntity>>>> optWorkKanbanPropertyInfoEntities = loadWorkKanbanPropCsvFile(folder, importHeaderFormatInfo.getWorkKanbanPropHeaderFormatInfo()) ;
        if (!optWorkKanbanPropertyInfoEntities.isPresent()) {
            return false;
        }
        Map<String, Map<String, List<WorkKanbanPropertyInfoEntity>>> workKanbanPropertyInfoEntities = optWorkKanbanPropertyInfoEntities.get();

        // カンバン階層の取得
        List<String> parentNames
                = kanbanInfoEntityMap
                .values()
                .stream()
                .map(KanbanInfoEntity::getParentName)
                .distinct()
                .collect(toList());

        Map<String, String> hierarchyMap
                = parentNames
                .stream()
                .map(this::getOrRegistKanbanHierarchy)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toMap(Tuple::getLeft, Tuple::getRight, (a, b) -> a));

        if (parentNames.size() != hierarchyMap.size()) {
            parentNames
                    .stream()
                    .filter(parentName -> !hierarchyMap.containsKey(parentName))
                    .forEach(parentName -> {
                        addResult(String.format("  > %s [%s]",
                                LocaleUtils.getString("key.ImportKanban_KanbanHierarchyNothing"),
                                parentName));
                    });
            return false;
        }

        // カンバンプロパティ
        List<String> warningMessages = new ArrayList<>();
        for (Map.Entry<String, List<KanbanPropertyInfoEntity>> kanbanPropertyInfoList : kanbanPropertyInfoEntities.entrySet()) {
            KanbanInfoEntity kanbanInfoEntityEntry = kanbanInfoEntityMap.get(kanbanPropertyInfoList.getKey());
            if (Objects.nonNull(kanbanInfoEntityEntry)) {
                kanbanInfoEntityEntry.setPropertyCollection(kanbanPropertyInfoList.getValue());
            } else {
                String[] keyValues = kanbanPropertyInfoList.getKey().split("####");
                warningMessages.add(String.format(" > %s [%s: %s][%s: %s][%s: %s]",
                        LocaleUtils.getString("key.import.skip.kanbanDataNotMatch"),
                        LocaleUtils.getString("key.KanbanName"), keyValues[0],
                        LocaleUtils.getString("key.WorkflowName"), keyValues[1],
                        LocaleUtils.getString("key.WorkflowRev"), keyValues[2]));
            }
        }

        // 工程カンバン
        for (Map.Entry<String, Map<String, WorkKanbanInfoEntity>> workKanbanInfoList : workKanbanInfoEntities.entrySet()) {
            KanbanInfoEntity kanbanInfoEntityEntry = kanbanInfoEntityMap.get(workKanbanInfoList.getKey());
            if (Objects.nonNull(kanbanInfoEntityEntry)) {
                kanbanInfoEntityEntry.setWorkKanbanCollection(new ArrayList<>(workKanbanInfoList.getValue().values()));
            } else {
                String[] keyValues = workKanbanInfoList.getKey().split("####");
                warningMessages.add(String.format(" > %s [%s: %s][%s: %s][%s: %s]",
                        LocaleUtils.getString("key.import.skip.workKanbanDataNotMatch"),
                        LocaleUtils.getString("key.KanbanName"), keyValues[0],
                        LocaleUtils.getString("key.WorkflowName"), keyValues[1],
                        LocaleUtils.getString("key.WorkflowRev"), keyValues[2]));
            }
        }

        for (Map.Entry<String, Map<String, List<WorkKanbanPropertyInfoEntity>>> workKanbanPropertyInfoEntityMapEntry : workKanbanPropertyInfoEntities.entrySet()) {
            KanbanInfoEntity kanbanInfoEntityEntry = kanbanInfoEntityMap.get(workKanbanPropertyInfoEntityMapEntry.getKey());
            if (Objects.isNull(kanbanInfoEntityEntry)) {
                // カンバンが存在しない
                String[] keyValues = workKanbanPropertyInfoEntityMapEntry.getKey().split("####");
                warningMessages.add(String.format(" > %s [%s: %s][%s: %s][%s: %s]",
                        LocaleUtils.getString("key.import.skip.workKanbanDataNotMatch"),
                        LocaleUtils.getString("key.KanbanName"), keyValues[0],
                        LocaleUtils.getString("key.WorkflowName"), keyValues[1],
                        LocaleUtils.getString("key.WorkflowRev"), keyValues[2]));
                continue;
            }

            Map<String, WorkKanbanInfoEntity> workKanbanInfoMap = workKanbanInfoEntities.get(workKanbanPropertyInfoEntityMapEntry.getKey());
            if (Objects.isNull(workKanbanInfoMap)) {
                String[] keyValues = workKanbanPropertyInfoEntityMapEntry.getKey().split("####");
                warningMessages.add(String.format(" > %s [%s: %s][%s: %s][%s: %s]",
                        LocaleUtils.getString("key.import.skip.workKanbanPropDataNotMatch"),
                        LocaleUtils.getString("key.KanbanName"), keyValues[0],
                        LocaleUtils.getString("key.WorkflowName"), keyValues[1],
                        LocaleUtils.getString("key.WorkflowRev"), keyValues[2]));
                continue;
            }
            for (Map.Entry<String, List<WorkKanbanPropertyInfoEntity>> workKanbanPropertyInfoEntry : workKanbanPropertyInfoEntityMapEntry.getValue().entrySet()) {
                WorkKanbanInfoEntity workKanbanInfo = workKanbanInfoMap.get(workKanbanPropertyInfoEntry.getKey());
                if (Objects.nonNull(workKanbanInfo)) {
                    workKanbanInfo.setPropertyCollection(workKanbanPropertyInfoEntry.getValue());
                } else {
                    String[] keyValues = workKanbanPropertyInfoEntityMapEntry.getKey().split("####");
                    warningMessages.add(String.format(" > %s [%s: %s][%s: %s][%s: %s]",
                            LocaleUtils.getString("key.import.skip.workKanbanPropDataNotMatch"),
                            LocaleUtils.getString("key.KanbanName"), keyValues[0],
                            LocaleUtils.getString("key.WorkflowName"), keyValues[1],
                            LocaleUtils.getString("key.WorkflowRev"), keyValues[2]));
                }
            }
        }


        if (!warningMessages.isEmpty()) {
            warningMessages.forEach(this::addResult);
        }

        addResult(LocaleUtils.getString("key.ImportKanban_CreateKanban"));
        // 登録処理
        List<KanbanInfoEntity> kanbanInfoEntityList
                = new ArrayList<>(kanbanInfoEntityMap.values());

        // 親階層の設定
        kanbanInfoEntityMap
                .values()
                .forEach(kanbanInfoEntity -> {
                    kanbanInfoEntity.setParentName(hierarchyMap.get(kanbanInfoEntity.getParentName()));
                });

        List<ResultResponse> result = new ArrayList<>();
        for (int n = 0; n < kanbanInfoEntityList.size(); ) {
            int next = Math.min(100 + n, kanbanInfoEntityList.size());

            // カンバン一括登録
            addResult(String.format("%s %s %s",
                    LocaleUtils.getString("key.import.production.kanba"),
                    "[" + n + "]～[" + (next-1) + "]",
                    LocaleUtils.getString("key.Regist")));

            // インポート情報からJSONファイルを生成
            String jsonStr = JsonUtils.objectsToJson(kanbanInfoEntityList.subList(n, next)); // JSON文字列に変換
            File jsonFile = createJsonFile(jsonStr); // ファイルに書き込み
            // インポートAPI呼び出し
            List<ResultResponse> responses = kanbanInfoFacade.importFile(jsonFile.getPath());
            result.addAll(responses);
            n = next;
        }

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
                    addResult(String.format(l.getFormat(), l.getArgs().stream().map(LocaleUtils::getString).toArray()));
                });

        String resultMessage = String.format("%s: %s (%s: %s, %s: %s, %s: %s)",
                LocaleUtils.getString("key.ImportKanban_ProccessNum"), processNum,
                LocaleUtils.getString("key.ImportKanban_SuccessNum"), successNum,
                LocaleUtils.getString("key.ImportKanban_SkipNum"), skipNum,
                LocaleUtils.getString("key.ImportKanban_FailedNum"), failedNum);

        addResult(resultMessage);
        logger.info(resultMessage);
        
        return true;
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

    private String cutOver(String targetStr, int limit) {
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
            if (count >= limit) {
                return targetStr.substring(0, i-1);
            }
        }
        return targetStr;
    }
}
