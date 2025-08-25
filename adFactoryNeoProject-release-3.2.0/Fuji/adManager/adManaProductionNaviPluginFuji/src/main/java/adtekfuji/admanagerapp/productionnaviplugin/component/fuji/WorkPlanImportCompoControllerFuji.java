/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component.fuji;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.entity.ImportResultInfo;
import adtekfuji.admanagerapp.productionnaviplugin.entity.fuji.ImportBom;
import adtekfuji.admanagerapp.productionnaviplugin.entity.fuji.ImportOrder;
import adtekfuji.admanagerapp.productionnaviplugin.enumerate.ImportResultEnum;
import adtekfuji.admanagerapp.productionnaviplugin.jdbc.adfactorydb.AdFactoryDbAccessor;
import adtekfuji.admanagerapp.productionnaviplugin.jdbc.adfactoryforfujidb.AdFactoryForFujiDbAccessor;
import adtekfuji.admanagerapp.productionnaviplugin.property.fuji.BomFormatInfo;
import adtekfuji.admanagerapp.productionnaviplugin.property.fuji.ImportFormatInfo;
import adtekfuji.admanagerapp.productionnaviplugin.property.fuji.OrderFormatInfo;
import adtekfuji.admanagerapp.productionnaviplugin.utils.fuji.ImportFormatFileUtil;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ProductionNaviUtils;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkKanbanTimeReplaceUtils;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanWorkflowProcess;
import adtekfuji.admanagerapp.productionnaviplugin.utils.fuji.UnitWorkflowProcess;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanCreateCondition;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.bpmn.model.entity.BpmnEndEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnSequenceFlow;
import jp.adtekfuji.bpmn.model.entity.BpmnStartEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 生産管理ナビ(FUJI) 読み込み画面
 *
 * @author nar-nakamura
 */
@FxComponent(id = "WorkPlanImportCompoFuji", fxmlPath = "/fxml/admanagerapp/productionnaviplugin/fuji/work_plan_import_compo_fuji.fxml")
public class WorkPlanImportCompoControllerFuji implements Initializable, ArgumentDelivery, ComponentHandler {

    // 定数
    private static final Long RANGE = 20l;
    private static final String DELIMITER = "\\|";
    private final String PREFIX_TMP = "tmp_";
    private final String SUFFIX_ERROR = ".error";
    private final String REGEX_PREFIX_TMP = "^" + PREFIX_TMP;

    private final List<Integer> WORK_START_VALUES = Arrays.asList(1, 3);// 着工を含む着工完工の値
    private final List<Integer> WORK_COMP_VALUES = Arrays.asList(2, 3);// 完工を含む着工完工の値

    // フィールド
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
    private final WorkKanbanInfoFacade workKanbanInfoFacade = new WorkKanbanInfoFacade();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final List<String> kanbanCsvFilenames = new ArrayList<>();
    private final List<String> updateFilenames = new ArrayList<>();
    private final Map<String, String> importFileMap = new HashMap<>();

    private final Map<Long, WorkflowInfoEntity> workflowMap = new HashMap();
    private final Map<String, Long> organizationMap = new HashMap();

    private boolean isImportProccessing = false;
    private boolean enableDbOutput;
    private ImportFormatInfo importFormatInfo = null;

    private List<ImportOrder> importOrderList;// 計画情報
    private List<ImportBom> importBomList;// BOM情報

    private final AdFactoryDbAccessor adFactoryDb = new AdFactoryDbAccessor();
    private final AdFactoryForFujiDbAccessor forFujiDb = new AdFactoryForFujiDbAccessor();

    /**
     * タブ
     */
    @FXML
    private TabPane tabImportMode;

    @FXML
    private TextField importCsvFileField;
    @FXML
    private TextField orderFileField;
    @FXML
    private TextField bomFileField;
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
            
            this.enableDbOutput = Boolean.valueOf(AdProperty.getProperties().getProperty("enableDbOutput", "false"));
            
            this.importCsvFileField.setText(properties.getProperty(ProductionNaviPropertyConstants.SELECT_PROD_CSV_PATH, path));

            // タブを復元する
            String value = properties.getProperty(ProductionNaviPropertyConstants.SELECT_PROD_TAB);
            if (!StringUtils.isEmpty(value)) {
                try {
                    int index = Integer.parseInt(value);
                    SingleSelectionModel<Tab> selectionModel = this.tabImportMode.getSelectionModel();
                    selectionModel.select(index);
                } catch (Exception ex) {
                }
            }

            // インポートフォーマット設定を読み込む。
            this.importFormatInfo = ImportFormatFileUtil.load();

            // CSVファイル名を読み込む。
            this.kanbanCsvFilenames.addAll(new LinkedHashSet<>(Arrays.asList(
                    this.importFormatInfo.getOrderFormatInfo().getCsvFileName(),
                    this.importFormatInfo.getBomFormatInfo().getCsvFileName()
            )));

            // 計画ファイル
            this.orderFileField.setText(this.importFormatInfo.getOrderFormatInfo().getCsvFileName());
            // BOMファイル
            this.bomFileField.setText(this.importFormatInfo.getBomFormatInfo().getCsvFileName());

        } catch (IOException ex) {
            logger.error(ex, ex);
        }
        blockUI(false);
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        ProductionNaviUtils.setFieldNormal(this.importCsvFileField);
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
                logger.debug(" TAB selected CSV");

                Date date = new Date();
                addResult(String.format("%tY/%tm/%td %tT", date, date, date, date));

                // 計画ファイル
                this.importFormatInfo.getOrderFormatInfo().setCsvFileName(this.orderFileField.getText().trim());
                // BOMファイル
                this.importFormatInfo.getBomFormatInfo().setCsvFileName(this.bomFileField.getText().trim());

                // CSVファイル名を読み込む。
                this.kanbanCsvFilenames.clear();
                this.kanbanCsvFilenames.addAll(new LinkedHashSet<>(Arrays.asList(
                        this.importFormatInfo.getOrderFormatInfo().getCsvFileName(),
                        this.importFormatInfo.getBomFormatInfo().getCsvFileName()
                )));

                // 出力先
                if (StringUtils.isEmpty(folder)) {
                    ProductionNaviUtils.setFieldError(this.importCsvFileField);
                    isCancel = true;
                    return;
                }

                File file = new File(folder);
                if (!file.exists() || !file.isDirectory()) {
                    ProductionNaviUtils.setFieldError(this.importCsvFileField);
                    isCancel = true;
                    return;
                }

                // 計画情報のファイル名
                String orderFileName = this.importFormatInfo.getOrderFormatInfo().getCsvFileName();
                String orderFilePath = folder + File.separator + orderFileName;
                File orderFile = new File(orderFilePath);

                existsFile = orderFile.exists() && orderFile.isFile();
                if (!existsFile) {
                    orderFileName = this.findCsvFile(orderFile);
                    if (!StringUtils.isEmpty(orderFileName)) {
                        existsFile = true;
                        this.importFileMap.put(this.importFormatInfo.getOrderFormatInfo().getCsvFileName(), orderFileName);
                    } else {
                        // 計画ファイルがない。
                        addResult(new StringBuilder("   > ")
                                .append(LocaleUtils.getString("key.OrderFile.notExist"))
                                .append("[")
                                .append(this.importFormatInfo.getOrderFormatInfo().getCsvFileName())
                                .append("]")
                                .toString()
                        );
                        isCancel = true;
                        return;
                    }
                }
                
                // BOM情報のファイル名
                String bomFileName = this.importFormatInfo.getBomFormatInfo().getCsvFileName();
                String bomFilePath = folder + File.separator + bomFileName;
                File bomFile = new File(bomFilePath);

                boolean existsBomFile = bomFile.exists() && bomFile.isFile();
                if (!existsBomFile) {
                    bomFileName = this.findCsvFile(bomFile);
                    if (!StringUtils.isEmpty(bomFileName)) {
                        existsBomFile = true;
                        this.importFileMap.put(this.importFormatInfo.getBomFormatInfo().getCsvFileName(), bomFileName);
                    } else {
                        // BOMファイルがない。
                        addResult(new StringBuilder("   > ")
                                .append(LocaleUtils.getString("key.BomFile.notExist"))
                                .append("[")
                                .append(this.importFormatInfo.getBomFormatInfo().getCsvFileName())
                                .append("]")
                                .toString()
                        );
                    }
                }

                this.importFormatInfo.getOrderFormatInfo().setFileName(orderFileName);
                this.importFormatInfo.getBomFormatInfo().setFileName(bomFileName);

                this.kanbanCsvFilenames.addAll(this.importFileMap.values());

                ProductionNaviUtils.setFieldNormal(this.importCsvFileField);
                
                tabMode = ProductionNaviUtils.IMPORT_TAB_IDX_CSV;
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_CSV));
                prop.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_CSV_PATH, folder);

            } else {
                logger.error(" TAB selected Error ");
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
     * @param existsEssential 必須ファイルの有無　存在しない場合(false)カンバンの読み込みは行われず工程カンバンプロパティの更新のみ行われる
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
                    // 必須ファイルが存在する時のみ実施する
                    if (existsEssential) {
                        Map<String, Integer> result = importKanban(folder, tabMode, importFormatInfo);
                        renameCompleted(tabMode, result, folder, kanbanCsvFilenames);
                    }
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
        int procNum = 0;
        int successNum = 0;
        int skipNum = 0;
        int failedNum = 0;

        OrderFormatInfo orderFormatInfo = importFormatInfo.getOrderFormatInfo();
        BomFormatInfo bomFormatInfo = importFormatInfo.getBomFormatInfo();

        // 計画情報を読み込む。
        String orderFileName = orderFormatInfo.getFileName();
        String orderPath = folder + File.separator + PREFIX_TMP + orderFileName;

        // 計画情報を読み込み中
        addResult(LocaleUtils.getString("key.import.work.plan.order") + " [" + orderFileName + "]");
        this.importOrderList = this.readOrderCsv(orderPath, orderFormatInfo);
        if (Objects.isNull(this.importOrderList)) {
            // ファイルが読み込めませんでした。
            addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readFile"), orderFileName));
            return null;
        } else if (this.importOrderList.isEmpty()) {
            // データがありません。
            addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), orderFileName));
            return null;
        }

        // BOM情報を読み込む。
        String bomFileName = bomFormatInfo.getFileName();
        if (StringUtils.isEmpty(bomFileName)) {
            this.importBomList = new ArrayList();
        } else {
            // BOM情報を読み込み中
            addResult(LocaleUtils.getString("key.import.work.plan.bom") + " [" + bomFileName + "]");

            String bomPath = folder + File.separator + PREFIX_TMP + bomFileName;
            this.importBomList = this.readBomCsv(bomPath, bomFormatInfo);
            if (Objects.isNull(this.importBomList)) {
                // ファイルが読み込めませんでした。
                addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.import.error.readFile"), bomFileName));
                this.importBomList = new ArrayList();
            } else if (this.importBomList.isEmpty()) {
                // データがありません。
                addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), bomFileName));
            }
        }

        // カンバン追加
        addResult(LocaleUtils.getString("key.ImportKanban_CreateKanban"));

        for (ImportOrder importOrder : this.importOrderList) {
            procNum++;

            UnitWorkflowProcess process = new UnitWorkflowProcess(procNum, importOrder);

            process.readData();

            if (Objects.isNull(process.getUnitTemplateInfo())) {
                // ユニットテンプレートがありません。
                addResult(String.format("[%d] > %s [%s]", procNum, LocaleUtils.getString("key.import.unitTemplate.notExist"), process.getUnitTemplateName()));
                continue;
            }

            Date startDate = ImportFormatFileUtil.stringToDateTime(process.getImportOrder().getStartDatetime());

            for (BpmnStartEvent node : process.getStartEventCollection()) {
                ImportResultInfo res = this.createKanbans(process, node.getId(), null, startDate);

                successNum += res.getSuccessNum();
                skipNum += res.getSkipNum();
                failedNum += res.getFailedNum();
            }
        }

        addResult(String.format("%s: %s (%s %s: %s, %s: %s, %s: %s)",
                LocaleUtils.getString("key.ImportKanban_ProccessNum"), procNum,
                LocaleUtils.getString("key.MakedKanban"),
                LocaleUtils.getString("key.ImportKanban_SuccessNum"), successNum,
                LocaleUtils.getString("key.ImportKanban_SkipNum"), skipNum,
                LocaleUtils.getString("key.ImportKanban_FailedNum"), failedNum));

        Map<String, Integer> ret = new HashMap<>();
        ret.put("procNum", procNum);
        ret.put("successNum", successNum);
        ret.put("skipKanbanNum", skipNum);
        ret.put("failedNum", failedNum);

        return ret;
    }

    /**
     * 
     * @return 
     */
    @Override
    public boolean destoryComponent() {
        try {
            this.forFujiDb.closeDb();
            this.adFactoryDb.closeDb();

            if (this.isImportProccessing) {
                // 「読み込み処理中のため、操作は無効です。」を表示
                String title = LocaleUtils.getString("key.KanbanImportTitle");
                String message = LocaleUtils.getString("key.alert.import.proccessing");

                sc.showAlert(Alert.AlertType.ERROR, title, message);
                return false;
            }
 
            // プロパティを保存する。
            this.saveProperties();
            // フォーマット情報を保存する。
            this.saveFormatInfo();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return true;
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
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
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
        String suffix = "";

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
            default:
                break;
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
                    loginUserInfoEntity.getName(),
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

            properties.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_TAB, String.valueOf(selectionModel.getSelectedIndex()));
            properties.setProperty(ProductionNaviPropertyConstants.SELECT_PROD_CSV_PATH, this.importCsvFileField.getText());

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
        if (!base.isFile()) {
            return fileName;
        }

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
     * 計画情報のCSVファイルを読み込む。
     *
     * @param filename ファイル名
     * @param formatInfo 計画情報のフォーマット情報
     * @return 計画情報一覧
     */
    private List<ImportOrder> readOrderCsv(String filename, OrderFormatInfo formatInfo) {
        if (Files.notExists(Paths.get(filename))) {
            return null;
        }

        List<ImportOrder> importOrders = new LinkedList();
        int count = 0;

        // 予定表(CSV)ファイルの文字コード
        String encode = formatInfo.getCsvFileEncode();
        // シフトJISの場合はMS932を指定する。
        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS", "S-JIS").contains(encode)) {
            encode = "MS932";
        }
        logger.debug(" encode=" + encode);

        // 読み込み設定情報
        int lineStart = StringUtils.parseInteger(formatInfo.getCsvStartRow());

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(filename), encode));
            String columns[];
            while ((columns = csvReader.readNext()) != null) {
                count++;

                if (count < lineStart) {
                    continue;
                }

                ImportOrder importOrder = new ImportOrder();

                importOrder.setWorkCenter(columns[0]);// 1.ワークセンター
                importOrder.setModel(columns[1]);// 2.機種
                importOrder.setSn(columns[2]);// 3.S/N
                importOrder.setDeliveryDate(columns[3]);// 4.納期日
                importOrder.setQuantity(columns[4]);// 5.数量
                importOrder.setOrderNo(columns[5]);// 6.オーダー番号
                importOrder.setProductionOrderNo(columns[6]);// 7.製造オーダー番号
                importOrder.setSerialNumber(columns[7]);// 8.シリアル番号
                importOrder.setProcessCode(columns[8]);// 9.工程コード
                importOrder.setProcessName(columns[9]);// 10.工程名
                importOrder.setTactTime(columns[10]);// 11.タクトタイム
                importOrder.setPartNo(columns[11]);// 12.品目コード
                importOrder.setProductName(columns[12]);// 13.品名
                importOrder.setStartDatetime(columns[13]);// 14.開始日時
                importOrder.setCompDatetime(columns[14]);// 15.終了日時
                importOrder.setWorkerId(columns[15]);// 16.作業員ID
                importOrder.setProcedure(columns[16]);// 17.手順

                importOrders.add(importOrder);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importOrders = null;
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
        return importOrders;
    }

    /**
     * BOM情報のCSVファイルを読み込む。
     *
     * @param filename ファイル名
     * @param formatInfo BOM情報のフォーマット情報
     * @return BOM情報一覧
     */
    private List<ImportBom> readBomCsv(String filename, BomFormatInfo formatInfo) {
        if (Files.notExists(Paths.get(filename))) {
            return null;
        }

        List<ImportBom> importBoms = new LinkedList();
        int count = 0;

        // 予定表(CSV)ファイルの文字コード
        String encode = formatInfo.getCsvFileEncode();
        // シフトJISの場合はMS932を指定する。
        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS", "S-JIS").contains(encode)) {
            encode = "MS932";
        }
        logger.debug(" encode=" + encode);

        // 読み込み設定情報
        int lineStart = StringUtils.parseInteger(formatInfo.getCsvStartRow());

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(filename), encode));
            String columns[];
            while ((columns = csvReader.readNext()) != null) {
                count++;

                if (count < lineStart) {
                    continue;
                }

                ImportBom importBom = new ImportBom();

                importBom.setOrderNo(columns[0]);// 1.オーダー番号
                importBom.setProductionOrderNo(columns[1]);// 2.製造オーダー番号
                importBom.setSerialNumber(columns[2]);// 3.シリアル番号
                importBom.setOrderPartNo(columns[3]);// 4.親品目コード
                importBom.setRequiredPartNo(columns[4]);// 5.子品目コード
                importBom.setProcessNo(columns[5]);// 6.工程No
                importBom.setProcessCode(columns[6]);// 7.工程コード
                importBom.setProcessName(columns[7]);// 8.工程名
                importBom.setRequiredQuantity(columns[8]);// 9.必要量

                importBoms.add(importBom);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importBoms = null;
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
        return importBoms;
    }

    /**
     * カンバンを作成する。
     *
     * @param process ユニットテンプレートのワークフロープロセス
     * @param id 対象のノードId
     * @param endId 終了ノードID
     * @param baseDate カンバン開始予定の基準日時
     * @return インポート結果
     */
    private ImportResultInfo createKanbans(UnitWorkflowProcess process, String id, String endId, Date baseDate) {
        Date startDate = baseDate;
        Date lastDate = baseDate;

        int successNum = 0;
        int skipNum = 0;
        int failedNum = 0;

        if (Objects.equals(id, endId)) {
            return new ImportResultInfo(successNum, skipNum, failedNum, baseDate);
        }

        final BpmnNode bpmnNode = process.getNode(id);

        if (bpmnNode instanceof BpmnEndEvent) {
            return new ImportResultInfo(successNum, skipNum, failedNum, baseDate);
        } else if (bpmnNode instanceof BpmnTask) {
            // カンバンを作成する。
            long workflowId = Long.valueOf(bpmnNode.getId());

            int progressFlag = process.getProgressFlag(id);

            Tuple<ImportResultEnum, Date> createResult = this.createKanban(process, workflowId, startDate, progressFlag);
            switch (createResult.getLeft()) {
                case SUCCESS:
                    successNum++;
                    break;
                case SKIP:
                    skipNum++;
                    break;
                case FAILED:
                    failedNum++;
                    break;
            }

            lastDate = createResult.getRight();

        } else if (bpmnNode instanceof BpmnParallelGateway) {
            BpmnParallelGateway gateway = (BpmnParallelGateway) bpmnNode;
            endId = gateway.getPairedId();
        }

        startDate = lastDate;

        // 並列作業終了まで処理する。
        List<BpmnSequenceFlow> flows = process.getNextFlows(id);
        for (BpmnSequenceFlow flow : flows) {
            ImportResultInfo res = this.createKanbans(process, flow.getTargetRef(), endId, startDate);
            if (lastDate.before(res.getLastDate())) {
                lastDate = res.getLastDate();
            }

            successNum += res.getSuccessNum();
            skipNum += res.getSkipNum();
            failedNum += res.getFailedNum();
        }

        startDate = lastDate;

        if (bpmnNode instanceof BpmnParallelGateway) {
            // 並列作業終了の続きを処理する。
            flows = process.getNextFlows(endId);
            for (BpmnSequenceFlow flow : flows) {
                ImportResultInfo res = this.createKanbans(process, flow.getTargetRef(), null, startDate);
                if (lastDate.before(res.getLastDate())) {
                    lastDate = res.getLastDate();
                }

                successNum += res.getSuccessNum();
                skipNum += res.getSkipNum();
                failedNum += res.getFailedNum();
            }
        }

        return new ImportResultInfo(successNum, skipNum, failedNum, lastDate);
    }

    /**
     * カンバンを作成する。
     *
     * @param process ユニットテンプレートのワークフロープロセス
     * @param workflowId 工程順ID
     * @param startDate カンバン開始予定日時
     * @param progressFlag 進捗フラグ(0:なし, 1:着工, 2:完工, 3:着工完工)
     * @return 結果とカンバン完了予定日時
     */
    private Tuple<ImportResultEnum, Date> createKanban(UnitWorkflowProcess process, long workflowId, Date startDate, int progressFlag) {
        try {
            // カンバン名
            String kanbanName = process.getImportOrder().getOrderNo();
            // カンバン階層ID
            long kanbanHierarchyId = process.getUnitTemplateInfo().getOutputKanbanHierarchyId();

            // 工程順
            WorkflowInfoEntity workflow;
            if (this.workflowMap.containsKey(workflowId)) {
                workflow = this.workflowMap.get(workflowId);
            } else {
                workflow = workflowInfoFacade.find(workflowId);
                if (Objects.isNull(workflow.getWorkflowId())) {
                    // 存在しない工程順のためスキップ
                    addResult(String.format("[%d] > %s [%s]", process.getLineNo(), LocaleUtils.getString("key.ImportKanban_WorkflowNothing"), workflowId));
                    return new Tuple(ImportResultEnum.SKIP, startDate);
                }

                this.workflowMap.put(workflowId, workflow);
            }

            if (Objects.isNull(workflow.getConWorkflowWorkInfoCollection()) || workflow.getConWorkflowWorkInfoCollection().isEmpty()) {
                // 工程順に工程が未登録のためスキップ
                addResult(String.format("[%d] > %s [%s]", process.getLineNo(), LocaleUtils.getString("key.import.skip.WorkNotRegistered"), workflow.getWorkflowName()));
                return new Tuple(ImportResultEnum.SKIP, startDate);
            }

            // カンバン作成
            KanbanInfoEntity kanban;
            KanbanSearchCondition condition = new KanbanSearchCondition()
                    .kanbanName(kanbanName)
                    .workflowId(workflowId);

            List<KanbanInfoEntity> kanbans = null;

            boolean exsitKanban = this.adFactoryDb.exsitKanban(kanbanName, workflowId);

            if (!exsitKanban) {
                kanban = new KanbanInfoEntity();
            } else {
                kanbans = this.kanbanInfoFacade.findSearch(condition);

                Optional<KanbanInfoEntity> findKanban = kanbans.stream()
                        .filter(k -> Objects.equals(kanbanName, k.getKanbanName()))
                        .findFirst();
                if (findKanban.isPresent()) {
                    kanban = findKanban.get();

                    List<WorkKanbanInfoEntity> workKanbans = new ArrayList<>();
                    Long workkanbanCnt = this.workKanbanInfoFacade.countFlow(kanban.getKanbanId());
                    for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                        workKanbans.addAll(this.workKanbanInfoFacade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
                    }
                    kanban.setWorkKanbanCollection(workKanbans);

                    List<WorkKanbanInfoEntity> sepWorkKanbans = new ArrayList<>();
                    Long separateCnt = this.workKanbanInfoFacade.countSeparate(kanban.getKanbanId());
                    for (long nowCnt = 0; nowCnt < separateCnt; nowCnt += RANGE) {
                        sepWorkKanbans.addAll(this.workKanbanInfoFacade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
                    }
                    kanban.setSeparateworkKanbanCollection(sepWorkKanbans);
                } else {
                    kanban = new KanbanInfoEntity();
                }
            }

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
                    //addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_NotUpdateKanban"), LocaleUtils.getString(kanbanStatus.getResourceKey())));
                    return new Tuple(ImportResultEnum.SKIP, startDate);
                }

                if (!kanbanStatus.equals(KanbanStatusEnum.PLANNING)) {
                    kanban.setKanbanStatus(KanbanStatusEnum.PLANNING);
                    ResponseEntity updateStatusRes = this.kanbanInfoFacade.update(kanban);
                    if (Objects.isNull(updateStatusRes) || !updateStatusRes.isSuccess()) {
                        // 更新できないカンバンのためスキップ (ステータス変更できない状態だった)
                        //addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_NotUpdateKanban"), LocaleUtils.getString(kanbanStatus.getResourceKey())));
                        return new Tuple(ImportResultEnum.SKIP, startDate);
                    }
                }
            } else {
                kanbanStatus = KanbanStatusEnum.PLANNED;
            }

            // 開始予定日時
            boolean isChangeStartDate = false;
            if (!startDate.equals(kanban.getStartDatetime())) {
                isChangeStartDate = true;
                kanban.setStartDatetime(startDate);
            }

            kanban.setKanbanName(kanbanName);
            kanban.setParentId(kanbanHierarchyId);
            kanban.setFkWorkflowId(workflowId);

            // モデル名
            kanban.setModelName(process.getImportOrder().getProductName());
            // 生産タイプ
            kanban.setProductionType(0);
            // ロット数量
            kanban.setLotQuantity(1);

            // カンバンを新規追加
            if (Objects.isNull(kanban.getKanbanId())) {
                ResponseEntity createRes = registKanban(kanban);
                if (Objects.nonNull(createRes) && createRes.isSuccess()) {
                    // 追加成功
                    this.addResult(new StringBuilder("[")
                            .append(process.getLineNo())
                            .append("] > ")
                            .append(LocaleUtils.getString("key.ImportKanban_RegistSuccess"))
                            .append(" [")
                            .append(kanban.getKanbanName())
                            .append(" (")
                            .append(workflow.getWorkflowName())
                            .append(")]")
                            .toString()
                    );
                } else {
                    // 追加失敗
                    this.addResult(new StringBuilder("[")
                            .append(process.getLineNo())
                            .append("] > ")
                            .append(LocaleUtils.getString("key.ImportKanban_RegistFailed"))
                            .append(" [")
                            .append(kanban.getKanbanName())
                            .append(" (")
                            .append(workflow.getWorkflowName())
                            .append(")]")
                            .toString()
                    );
                    return new Tuple(ImportResultEnum.FAILED, startDate);
                }
                kanban = this.kanbanInfoFacade.findURI(createRes.getUri());
            } else {
                // 更新
                this.addResult(new StringBuilder("[")
                        .append(process.getLineNo())
                        .append("] > [")
                        .append(kanban.getKanbanName())
                        .append(" (")
                        .append(workflow.getWorkflowName())
                        .append(")]")
                        .toString()
                );
            }

            // 工程カンバンをオーダー順にソートする。
            kanban.getWorkKanbanCollection().sort(Comparator.comparing(p -> p.getWorkKanbanOrder()));

            // カンバンプロパティ
            Map<String, String> importKanbanProps = new LinkedHashMap();

            // オーダー番号
            importKanbanProps.put("オーダー番号", process.getImportOrder().getProductionOrderNo());
            // S/N
            importKanbanProps.put("SN", process.getImportOrder().getSn());
            // シリアル
            importKanbanProps.put("シリアル", process.getImportOrder().getSerialNumber());
            // 工数連携
            //      変更なし
            // 上位連携フラグ
            if (this.enableDbOutput) {
                importKanbanProps.put("上位連携フラグ", "TRUE");
            }
            // 品目コード
            importKanbanProps.put("品目コード", process.getImportOrder().getPartNo());
            // 品名
            importKanbanProps.put("品名", process.getImportOrder().getProductName());
            // 工程
            importKanbanProps.put("工程", process.getImportOrder().getProcessCode());
            // 手順
            importKanbanProps.put("手順", process.getImportOrder().getProcedure());
            // ワークセンター
            importKanbanProps.put("ワークセンター", process.getImportOrder().getWorkCenter());

            for (Map.Entry<String, String> importKanbanProp : importKanbanProps.entrySet()) {
                List<KanbanPropertyInfoEntity> props = kanban.getPropertyCollection().stream()
                        .filter(p -> Objects.equals(p.getKanbanPropertyName(), importKanbanProp.getKey()))
                        .collect(Collectors.toList());
                if (Objects.isNull(props) || props.isEmpty()) {
                    // 存在しない場合はプロパティを追加する。
                    KanbanPropertyInfoEntity prop = new KanbanPropertyInfoEntity();
                    prop.setKanbanPropertyName(importKanbanProp.getKey());
                    prop.setKanbanPropertyType(CustomPropertyTypeEnum.TYPE_STRING);
                    prop.setKanbanPropertyValue(importKanbanProp.getValue());

                    // プロパティ表示順の最大値から、次に追加するプロパティの表示順を設定
                    int propOrder = 1;
                    Optional<KanbanPropertyInfoEntity> lastProp = kanban.getPropertyCollection().stream()
                            .max(Comparator.comparingInt(p -> p.getKanbanPropertyOrder()));
                    if (lastProp.isPresent()) {
                        propOrder = lastProp.get().getKanbanPropertyOrder() + 1;
                    }
                    prop.setKanbanPropertyOrder(propOrder);

                    kanban.getPropertyCollection().add(prop);
                } else {
                    // 存在する場合は値を更新する。
                    for (KanbanPropertyInfoEntity prop : props) {
                        prop.setKanbanPropertyValue(importKanbanProp.getValue());
                    }
                }
            }

            // カンバンのBOMを取得する。
            List<ImportBom> kanbanBoms = this.importBomList.stream()
                    .filter(p -> Objects.equals(p.getOrderNo(), kanbanName)
                            && Objects.equals(p.getProcessName(), process.getImportOrder().getProcessName()))
                    .collect(Collectors.toList());

            // 工程カンバンをオーダー順にソートする。
            kanban.getWorkKanbanCollection().sort(Comparator.comparing(p -> p.getWorkKanbanOrder()));

            // 工程カンバンが1個か？
            boolean isSingle = false;
            if (kanban.getWorkKanbanCollection().size() == 1) {
                isSingle = true;
            }

            // 工程カンバンを更新する。
            int workKanbanNum = 0;
            boolean isAddWorker = false;
            for (WorkKanbanInfoEntity workKanban : kanban.getWorkKanbanCollection()) {
                workKanbanNum++;
                String orderWorkerId = process.getImportOrder().getWorkerId();

                // 組織識別名
                if (!StringUtils.isEmpty(orderWorkerId)) {
                    String[] workerIds = orderWorkerId.split(DELIMITER, 0);

                    for (String workerId : workerIds) {
                        if (workerId.equals(DELIMITER)) {
                            continue;
                        }

                        // 組織情報を取得する。
                        Long organizationId;
                        if (this.organizationMap.containsKey(workerId)) {
                            organizationId = this.organizationMap.get(workerId);
                        } else {
                            organizationId = adFactoryDb.getOrganizationId(workerId);
                            if (Objects.nonNull(organizationId)) {
                                this.organizationMap.put(workerId, organizationId);
                            }
                        }

                        if (Objects.nonNull(organizationId)) {
                            if (Objects.isNull(workKanban.getOrganizationCollection())) {
                                // 工程カンバンに組織が未指定の場合、新規に設定する。
                                workKanban.setOrganizationCollection(Arrays.asList(organizationId));
                                isAddWorker = true;
                            } else if (!workKanban.getOrganizationCollection().contains(organizationId)) {
                                // 工程カンバンに組織が指定されているが、該当組織が含まれていない場合、組織を追加する。
                                workKanban.getOrganizationCollection().add(organizationId);
                                isAddWorker = true;
                            }
                        }
                    }
                }

                // 着工完工(1:着工, 2:完工, 3:着工完工)
                String progress = "";
                if (progressFlag == 3 && isSingle) {
                    // ユニット先頭かつ最終の工程順で、カンバンの先頭かつ最終工程の場合、「3:着工完工」をセットする。
                    progress = "3";
                } else if (workKanbanNum == 1 && WORK_START_VALUES.contains(progressFlag)) {
                    // ユニット先頭の工程順で、カンバンの先頭工程の場合、「1:着工」をセットする。
                    progress = "1";
                } else if (workKanbanNum == kanban.getWorkKanbanCollection().size() && WORK_COMP_VALUES.contains(progressFlag)) {
                    // ユニット最終の工程順で、カンバンの最終工程の場合、「2:完工」をセットする。
                    progress = "2";
                }

                Optional<WorkKanbanPropertyInfoEntity> optWorkKanbanProp = workKanban.getPropertyCollection().stream()
                        .filter(p -> Objects.equals(p.getWorkKanbanPropName(), "着工完工"))
                        .findFirst();
                if (optWorkKanbanProp.isPresent()) {
                    optWorkKanbanProp.get().setWorkKanbanPropValue(progress);
                } else {
                    WorkKanbanPropertyInfoEntity workKanbanProp = new WorkKanbanPropertyInfoEntity();
                    workKanbanProp.setWorkKanbanPropName("着工完工");
                    workKanbanProp.setWorkKanbanPropType(CustomPropertyTypeEnum.TYPE_STRING);
                    workKanbanProp.setWorkKanbanPropValue(progress);

                    // 工程カンバン追加項目をオーダー順にソートする。
                    workKanban.getPropertyCollection().sort(Comparator.comparing(p -> p.getWorkKanbanPropOrder()));

                    int propOrder = 0;
                    for (WorkKanbanPropertyInfoEntity prop : workKanban.getPropertyCollection()) {
                        propOrder++;
                        prop.setWorkKanbanPropOrder(propOrder);
                    }

                    workKanbanProp.setWorkKanbanPropOrder(propOrder);

                    workKanban.getPropertyCollection().add(workKanbanProp);
                }

                // BOMから使用部品取得して工程カンバン追加項目に登録する。
                if (Objects.nonNull(kanbanBoms) && !kanbanBoms.isEmpty()) {
                    // 使用部品<番号>を削除する。
                    for (int i = workKanban.getPropertyCollection().size() - 1; i >= 0; i--) {
                        WorkKanbanPropertyInfoEntity prop = workKanban.getPropertyCollection().get(i);
                        if (prop.getWorkKanbanPropName().startsWith("使用部品")) {
                            workKanban.getPropertyCollection().remove(i);
                        }
                    }

                    // 工程カンバン追加項目をオーダー順にソートする。
                    workKanban.getPropertyCollection().sort(Comparator.comparing(p -> p.getWorkKanbanPropOrder()));

                    int propOrder = 0;
                    for (WorkKanbanPropertyInfoEntity prop : workKanban.getPropertyCollection()) {
                        propOrder++;
                        prop.setWorkKanbanPropOrder(propOrder);
                    }

                    // 工程の使用部品
                    List<WorkKanbanPropertyInfoEntity> workKanbanProps = new LinkedList();

                    int usePartsId = 0;
                    for (ImportBom workBom : kanbanBoms) {
                        usePartsId++;
                        propOrder++;

                        String propName = new StringBuilder("使用部品").append(usePartsId).toString();
                        String propValue = new StringBuilder(workBom.getRequiredPartNo())
                                .append("\\t")
                                .append(workBom.getRequiredQuantity())
                                .toString();

                        WorkKanbanPropertyInfoEntity prop = new WorkKanbanPropertyInfoEntity();
                        prop.setWorkKanbanPropName(propName);
                        prop.setWorkKanbanPropType(CustomPropertyTypeEnum.TYPE_STRING);
                        prop.setWorkKanbanPropValue(propValue);
                        prop.setWorkKanbanPropOrder(propOrder);

                        workKanbanProps.add(prop);
                    }

                    workKanban.getPropertyCollection().addAll(workKanbanProps);
                }
            }

            // 計画ファイルで、計画日時の変更か、組織の割り当てが追加された場合、工程カンバンの計画日時を更新する。
            if (isChangeStartDate || isAddWorker) {
                List<BreakTimeInfoEntity> breakTimes = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanban.getWorkKanbanCollection());
                List<HolidayInfoEntity> holidays = WorkKanbanTimeReplaceUtils.getHolidays(kanban.getStartDatetime());
                WorkPlanWorkflowProcess workflowProcess = new WorkPlanWorkflowProcess(workflow);
                workflowProcess.setBaseTime(kanban, breakTimes, kanban.getStartDatetime(), holidays);
            }

            // 更新日時
            Date updateDateTime = DateUtils.toDate(LocalDateTime.now());
            kanban.setUpdateDatetime(updateDateTime);

            // 更新者
            kanban.setFkUpdatePersonId(loginUserInfoEntity.getId());

            // カンバン更新
            //addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_UpdateKanban")));
            ResponseEntity updateRes = kanbanInfoFacade.update(kanban);

            ImportResultEnum importResult;
            if (Objects.nonNull(updateRes) && updateRes.isSuccess()) {
                // 更新成功

                // 更新したカンバンを再取得する。
                kanban = kanbanInfoFacade.find(kanban.getKanbanId());

                // カンバンステータスを元の状態に戻す
                kanban.setKanbanStatus(kanbanStatus);

                kanbanInfoFacade.update(kanban);

                importResult = ImportResultEnum.SUCCESS;
                startDate = kanban.getCompDatetime();
                //addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_UpdateSuccess")));
            } else {
                // 更新失敗
                importResult = ImportResultEnum.FAILED;
                addResult(String.format("[%d] > %s", process.getLineNo(), LocaleUtils.getString("key.ImportKanban_UpdateFailed")));
            }

            return new Tuple(importResult, startDate);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new Tuple(ImportResultEnum.FAILED, startDate);
        }
    }

    /**
     * フォーマット情報を保存する。
     */
    private void saveFormatInfo() {
        try {
            // 計画ファイル
            this.importFormatInfo.getOrderFormatInfo().setCsvFileName(this.orderFileField.getText());
            // BOMファイル
            this.importFormatInfo.getBomFormatInfo().setCsvFileName(this.bomFileField.getText());

            ImportFormatFileUtil.save(this.importFormatInfo);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}

