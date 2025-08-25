/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component;

import adtekfuji.admanagerapp.kanbaneditplugin.entity.ImportKanbanEntity;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.ImportKanbanPropertyEntity;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.ImportWorkKanbanEntity;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.ImportWorkKanbanPropertyEntity;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.CsvFileUtils;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.WorkKanbanTimeReplaceUtils;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.WorkflowProcess;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 生産計画読み込み画面
 *
 * @author nar-nakamura
 */
@FxComponent(id = "KanbanImportCompo", fxmlPath = "/fxml/compo/kanban_import_compo.fxml")
public class KanbanImportCompoFxController implements Initializable, ArgumentDelivery {

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final KanbanHierarchyInfoFacade kanbanHierarchyInfoFacade = new KanbanHierarchyInfoFacade();
    private final WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
    private final WorkKanbanInfoFacade workKanbanInfoFacade = new WorkKanbanInfoFacade();
    private final OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private static final String IMPORT_PATH = "kanbanImportPath";// インポートパス設定

    private static final String KANBAN_CSV = "kanban.csv";// カンバン情報ファイル
    private static final String KANBAN_PROPERTY_CSV = "kanban_property.csv";// カンバンプロパティファイル
    private static final String WORK_KANBAN_CSV = "work_kanban.csv";// 工程カンバンファイル
    private static final String WORK_KANBAN_PROPERTY_CSV = "work_kanban_property.csv";// 工程カンバンプロパティファイル
    private static final String KANBAN_STATUS_CSV = "kanban_status.csv";// カンバンステータスファイル
    private static final String CHARSET = "UTF-8";
    private static final String DELIMITER = "\\|";
    private static final Long RANGE = 20l;

    private static final String KANBAN_CSV_ENCODE_KEY = "kanban_csv_encode";// 生産計画ファイルの文字コード設定
    private static final String KANBAN_CSV_ENCODE_DEFAULT = "UTF-8";

    private String kanbanCsvEncode;// 生産計画ファイルの文字コード

    /**
     * カンバン情報 - カンバン階層名
     */
    private static final Integer NUM_KAN_HIERARCHY_NAME = 0;
    /**
     * カンバン情報 - カンバン名
     */
    private static final Integer NUM_KAN_KANBAN_NAME = 1;
    /**
     * カンバン情報 - 工程順名
     */
    private static final Integer NUM_KAN_WORKFLOW_NAME = 2;
    /**
     * カンバン情報 - 工程順版数
     */
    private static final Integer NUM_KAN_WORKFLOW_REV = 3;

    /**
     * カンバン情報 - モデル名
     */
    private static final Integer NUM_KAN_MODEL_NAME = 4;

    /**
     * カンバン情報 - 開始予定日時
     */
    private static final Integer NUM_KAN_START_DATETIME = 5;

    /**
     * カンバンプロパティ情報 - カンバン名
     */
    private static final Integer NUM_KANP_KANBAN_NAME = 0;
    /**
     * カンバンプロパティ情報 - プロパティ名
     */
    private static final Integer NUM_KANP_PROP_NAME = 1;
    /**
     * カンバンプロパティ情報 - 型
     */
    private static final Integer NUM_KANP_PROP_TYPE = 2;
    /**
     * カンバンプロパティ情報 - 値
     */
    private static final Integer NUM_KANP_PROP_VALUE = 3;

    /**
     * 工程カンバン情報 - カンバン名
     */
    private static final Integer NUM_WKAN_KANBAN_NAME = 0;
    /**
     * 工程カンバン情報 - 工程の番号
     */
    private static final Integer NUM_WKAN_WORK_NUM = 1;
    /**
     * 工程カンバン情報 - スキップフラグ
     */
    private static final Integer NUM_WKAN_SKIP_FLAG = 2;
    /**
     * 工程カンバン情報 - 開始予定日時
     */
    private static final Integer NUM_WKAN_START_DATETIME = 3;
    /**
     * 工程カンバン情報 - 完了予定日時
     */
    private static final Integer NUM_WKAN_COMP_DATETIME = 4;
    /**
     * 工程カンバン情報 - 組織識別名
     */
    private static final Integer NUM_WKAN_ORGANIZATIONS = 5;
    /**
     * 工程カンバン情報 - 設備識別名
     */
    private static final Integer NUM_WKAN_EQUIPMENTS = 6;

    /**
     * カンバンステータス情報 - カンバン名
     */
    private static final Integer NUM_KANS_KANBAN_NAME = 0;
    /**
     * カンバンステータス情報 - ステータス
     */
    private static final Integer NUM_KANS_STATUS_NAME = 1;

    /**
     * 工程カンバンプロパティ情報 - カンバン名
     */
    private static final Integer NUM_WKANP_KANBAN_NAME = 0;
    /**
     * 工程カンバンプロパティ情報 - 工程の番号
     */
    private static final Integer NUM_WKANP_WORK_NUM = 1;
    /**
     * 工程カンバンプロパティ情報 - プロパティ名
     */
    private static final Integer NUM_WKANP_PROP_NAME = 2;
    /**
     * 工程カンバンプロパティ情報 - 型
     */
    private static final Integer NUM_WKANP_PROP_TYPE = 3;
    /**
     * 工程カンバンプロパティ情報 - 値
     */
    private static final Integer NUM_WKANP_PROP_VALUE = 4;

    @FXML
    private TextField importFolderField;
    @FXML
    private Button ImportButton;
    @FXML
    private ListView resultList;
    @FXML
    private Pane progressPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 生産計画ファイルの文字コード
        this.kanbanCsvEncode = this.properties.getProperty(KANBAN_CSV_ENCODE_KEY, "").toUpperCase();
        if (this.kanbanCsvEncode.isEmpty()) {
            this.properties.setProperty(KANBAN_CSV_ENCODE_KEY, KANBAN_CSV_ENCODE_DEFAULT);
            this.kanbanCsvEncode = KANBAN_CSV_ENCODE_DEFAULT;
        }
        // シフトJISの場合はMS932を指定する。
        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS").contains(this.kanbanCsvEncode)) {
            this.kanbanCsvEncode = "MS932";
        }

        // 役割の権限によるボタン無効化.
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            ImportButton.setDisable(true);
        }

        final String path = System.getProperty("user.home") + File.separator + "Documents";
        String importPath = AdProperty.getProperties().getProperty(IMPORT_PATH, path);
        this.importFolderField.setText(importPath);
        blockUI(false);
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {

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
     * フォルダ選択ボタン Action
     *
     * @param event
     */
    @FXML
    private void onSelectFolderAction(ActionEvent event) {
        blockUI(true);
        DirectoryChooser dc = new DirectoryChooser();
        File fol = new File(importFolderField.getText());
        if (fol.exists() && fol.isDirectory()) {
            dc.setInitialDirectory(fol);
        }
        File selectedFile = dc.showDialog(sc.getStage().getScene().getWindow());
        if (selectedFile != null) {
            importFolderField.setText(selectedFile.getPath());
        }
        blockUI(false);
    }

    /**
     * インポートボタン Action
     *
     * @param event
     */
    @FXML
    private void onImportAction(ActionEvent event) {
        try {
            blockUI(true);
            this.resultList.getItems().clear();

            // 出力先
            String folder = this.importFolderField.getText();
            if (Objects.isNull(folder) || folder.isEmpty()) {
                return;
            }

            File file = new File(folder);
            if (!file.exists() || !file.isDirectory()) {
                return;
            }

            AdProperty.getProperties().setProperty(IMPORT_PATH, folder);

            // インポート
            this.importKanbanTask(folder);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * インポート処理
     *
     * @param folder
     */
    private void importKanbanTask(String folder) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                blockUI(true);
                try {
                    addResult(String.format("%s [%s]", LocaleUtils.getString("key.ImportKanbanStart"), folder));// 生産計画取り込み開始
                    importKanban(folder);
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    addResult(LocaleUtils.getString("key.ImportKanbanEnd"));// 生産計画取り込み終了
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * カンバンインポート
     *
     * @param folder
     * @throws Exception
     */
    private void importKanban(String folder) throws Exception {
        // カンバン情報 読込
        String kanbanPath = folder + File.separator + KANBAN_CSV;
        List<ImportKanbanEntity> importKanbans = readKanbanCsv(kanbanPath);
        if (importKanbans.isEmpty()) {
            addResult(LocaleUtils.getString("key.ImportKanban_FileNothing"));// 指定フォルダにカンバン情報ファイルがない
            return;
        }
        addResult(LocaleUtils.getString("key.ImportKanban_ReadKanbanCsv"));// カンバン情報ファイル読み込み

        // カンバンプロパティ情報 読込
        String kanbanPropPath = folder + File.separator + KANBAN_PROPERTY_CSV;
        List<ImportKanbanPropertyEntity> importKanbanProps = readKanbanPropertyCsv(kanbanPropPath);
        if (!importKanbanProps.isEmpty()) {
            addResult(LocaleUtils.getString("key.ImportKanban_ReadKanbanPropertyCsv"));// カンバンプロパティ情報ファイル読み込み
        }

        // 工程カンバン情報 読込
        String workKanbanPath = folder + File.separator + WORK_KANBAN_CSV;
        List<ImportWorkKanbanEntity> importWorkKanbans = readWorkKanbanCsv(workKanbanPath);
        if (!importWorkKanbans.isEmpty()) {
            addResult(LocaleUtils.getString("key.ImportKanban_ReadWorkKanbanCsv"));// 工程カンバン情報ファイル読み込み
        }

        // 工程カンバンプロパティ情報 読込
        String workKanbanPropPath = folder + File.separator + WORK_KANBAN_PROPERTY_CSV;
        List<ImportWorkKanbanPropertyEntity> importWkKanbanProps = readWorkKanbanPropertyCsv(workKanbanPropPath);
        if (!importWkKanbanProps.isEmpty()) {
            addResult(LocaleUtils.getString("key.ImportKanban_ReadWorkKanbanPropertyCsv"));// 工程カンバンプロパティ情報ファイル読み込み
        }

        // カンバンステータス情報 読込
        String kanbanStatusPath = folder + File.separator + KANBAN_STATUS_CSV;
        Map<String, KanbanStatusEnum> statusMap = readKanbanStatusCsv(kanbanStatusPath);

        Map<String, KanbanHierarchyInfoEntity> kanbanHierarchyMap = new HashMap();
        Map<String, WorkflowInfoEntity> workflowMap = new HashMap();
        Map<String, OrganizationInfoEntity> organizationMap = new HashMap();
        Map<String, EquipmentInfoEntity> equipmentMap = new HashMap();

        int procNum = 0;
        int skipKanbanNum = 0;
        int successNum = 0;
        int failedNum = 0;
        for (ImportKanbanEntity importKanban : importKanbans) {

            logger.info("Import the kanban: " + importKanban);

            procNum++;
            String kanbanName = importKanban.getKanbanName();// カンバン名
            if (Objects.isNull(kanbanName) || kanbanName.isEmpty()) {
                continue;
            }
            addResult(String.format("%s: %s", LocaleUtils.getString("key.ImportKanban_TargetKanbanName"), kanbanName));// 読み込みカンバン

            // カンバン階層
            KanbanHierarchyInfoEntity kanbanHierarchy;
            if (kanbanHierarchyMap.containsKey(importKanban.getKanbanHierarchyName())) {
                kanbanHierarchy = kanbanHierarchyMap.get(importKanban.getKanbanHierarchyName());
            } else {
                kanbanHierarchy = kanbanHierarchyInfoFacade.findHierarchyName(URLEncoder.encode(importKanban.getKanbanHierarchyName(), CHARSET));
                kanbanHierarchyMap.put(importKanban.getKanbanHierarchyName(), kanbanHierarchy);
            }
            Long kanbanHierarchyId = kanbanHierarchy.getKanbanHierarchyId();
            if (Objects.isNull(kanbanHierarchyId)) {
                // 存在しないカンバン階層のためスキップ
                skipKanbanNum++;
                addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_HierarchyNothing"), importKanban.getKanbanHierarchyName()));
                continue;
            }

            // 工程順
            WorkflowInfoEntity workflow;
            String workflowNameAndRev = Objects.nonNull(importKanban.getWorkflowRev()) ? importKanban.getWorkflowName() + " : " + importKanban.getWorkflowRev().toString() : importKanban.getWorkflowName();
            if (workflowMap.containsKey(workflowNameAndRev)) {
                workflow = workflowMap.get(workflowNameAndRev);
            } else {
                workflow = workflowInfoFacade.findName(URLEncoder.encode(importKanban.getWorkflowName(), CHARSET), importKanban.getWorkflowRev());
                workflowMap.put(workflowNameAndRev, workflow);
            }
            Long workflowId = workflow.getWorkflowId();
            if (Objects.isNull(workflowId)) {
                // 存在しない工程順のためスキップ
                skipKanbanNum++;
                addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_WorkflowNothing"), workflowNameAndRev));
                continue;
            }

            // 開始予定日時
            Date startDatetime = null;
            if (Objects.nonNull(importKanban.getStartDatetime())) {
                startDatetime = importKanban.getStartDatetime();
            }

            // カンバン作成
            KanbanInfoEntity kanban;
            KanbanSearchCondition condition = new KanbanSearchCondition()
                    .kanbanName(kanbanName)
                    .workflowId(workflowId);
            List<KanbanInfoEntity> kanbans = kanbanInfoFacade.findSearch(condition);
            if (kanbans.isEmpty()) {
                kanban = new KanbanInfoEntity();
                if (Objects.isNull(startDatetime)) {
                    startDatetime = new Date();
                }
            } else {
                Optional<KanbanInfoEntity> findKanban = kanbans.stream().filter(k -> kanbanName.equals(k.getKanbanName())).findFirst();
                if (findKanban.isPresent()) {
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
                    skipKanbanNum++;
                    addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_NotUpdateKanban"), LocaleUtils.getString(kanbanStatus.getResourceKey())));
                    continue;
                }

                if (!kanbanStatus.equals(KanbanStatusEnum.PLANNING)) {
                    // 計画中に一旦変更
                    ResponseEntity updateStatusRes = kanbanInfoFacade.updateStatus(Arrays.asList(kanban.getKanbanId()), KanbanStatusEnum.PLANNING, loginUserInfoEntity.getId());
                    if (Objects.isNull(updateStatusRes) || !updateStatusRes.isSuccess()) {
                        // 更新できないカンバンのためスキップ (ステータス変更できない状態だった)
                        skipKanbanNum++;
                        addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_NotUpdateKanban"), LocaleUtils.getString(kanbanStatus.getResourceKey())));
                        continue;
                    }
                    
                    KanbanInfoEntity newKanban = kanbanInfoFacade.find(kanban.getKanbanId());
                    kanban.setVerInfo(newKanban.getVerInfo());
                    kanban.setKanbanStatus(newKanban.getKanbanStatus());
                }
            } else {
                kanbanStatus = KanbanStatusEnum.PLANNED;
            }

            // 開始予定日時
            if (Objects.nonNull(startDatetime)) {
                kanban.setStartDatetime(startDatetime);
            }

            kanban.setKanbanName(kanbanName);
            kanban.setParentId(kanbanHierarchyId);
            kanban.setFkWorkflowId(workflowId);

            // 新規作成分は一旦登録
            if (Objects.isNull(kanban.getKanbanId())) {
                // モデル名
                kanban.setModelName(workflow.getModelName());

                // カンバン追加
                addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_CreateKanban")));
                ResponseEntity createRes = kanbanInfoFacade.regist(kanban);
                if (Objects.nonNull(createRes) && createRes.isSuccess()) {
                    // 追加成功
                    addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_RegistSuccess")));
                } else {
                    // 追加失敗
                    failedNum++;
                    addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_RegistFailed")));
                    continue;
                }
                kanban = kanbanInfoFacade.findURI(createRes.getUri());
            } else {
                if (Objects.nonNull(kanban.getStartDatetime())) {
                    List<BreakTimeInfoEntity> breakTimes = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanban.getWorkKanbanCollection());
                    List<HolidayInfoEntity> holidays = WorkKanbanTimeReplaceUtils.getHolidays(kanban.getStartDatetime());
                    WorkflowProcess workflowProcess = new WorkflowProcess(workflow);
                    workflowProcess.setBaseTime(kanban, breakTimes, kanban.getStartDatetime(), holidays);
                }
            }

            // モデル名
            if (Objects.nonNull(importKanban.getModelName())
                    && !importKanban.getModelName().isEmpty()) {
                kanban.setModelName(importKanban.getModelName());
            }

            // 工程カンバンをオーダー順にソートする。
            kanban.getWorkKanbanCollection().sort(Comparator.comparing(p -> p.getWorkKanbanOrder()));

            // カンバンプロパティ
            List<ImportKanbanPropertyEntity> props = importKanbanProps.stream().filter(p -> kanbanName.equals(p.getKanbanName())).collect(Collectors.toList());
            for (ImportKanbanPropertyEntity prop : props) {
                String propName = prop.getKanbanPropertyName();
                if (Objects.isNull(propName) || propName.isEmpty()) {
                    continue;
                }

                // プロパティ型
                CustomPropertyTypeEnum propType = null;
                if (Objects.nonNull(prop.getKanbanPropertyType()) && !prop.getKanbanPropertyType().isEmpty()) {
                    String propTypeString = prop.getKanbanPropertyType();
                    if (CustomPropertyTypeEnum.TYPE_STRING.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_STRING;
                    } else if (CustomPropertyTypeEnum.TYPE_BOOLEAN.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_BOOLEAN;
                    } else if (CustomPropertyTypeEnum.TYPE_INTEGER.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_INTEGER;
                    } else if (CustomPropertyTypeEnum.TYPE_NUMERIC.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_NUMERIC;
                    } else if (CustomPropertyTypeEnum.TYPE_IP4_ADDRESS.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_IP4_ADDRESS;
                    } else if (CustomPropertyTypeEnum.TYPE_MAC_ADDRESS.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_MAC_ADDRESS;
                    } else if (CustomPropertyTypeEnum.TYPE_PLUGIN.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_PLUGIN;
                    } else if (CustomPropertyTypeEnum.TYPE_TRACE.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_TRACE;
                    }
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
                    if (Objects.nonNull(prop.getKanbanPropertyValue()) && !prop.getKanbanPropertyValue().isEmpty()) {
                        kanbanProp.setKanbanPropertyValue(prop.getKanbanPropertyValue());
                    }
                } else // 存在しないプロパティの場合は、プロパティ名とプロパティ値が入っていたら追加する。
                 if (Objects.nonNull(propType)) {
                        KanbanPropertyInfoEntity kanbanProp = new KanbanPropertyInfoEntity();
                        kanbanProp.setKanbanPropertyName(propName);
                        kanbanProp.setKanbanPropertyType(propType);

                        // プロパティ値
                        if (Objects.nonNull(prop.getKanbanPropertyValue()) && !prop.getKanbanPropertyValue().isEmpty()) {
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

            if (ClientServiceProperty.isLicensed("@Scheduling")) {
                // 工程カンバン
                List<ImportWorkKanbanEntity> works = importWorkKanbans.stream().filter(p -> kanbanName.equals(p.getKanbanName())).collect(Collectors.toList());
                for (ImportWorkKanbanEntity work : works) {
                    Integer workNum = null;
                    if (Objects.nonNull(work.getWorkNum()) && !work.getWorkNum().isEmpty()) {
                        workNum = StringUtils.parseInteger(work.getWorkNum());
                    }
                    if (Objects.isNull(workNum) || workNum < 1 || workNum > kanban.getWorkKanbanCollection().size()) {
                        // 工程の番号が異常なためスキップ
                        addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_WorkNumFailed"), workNum));
                        continue;
                    }

                    // 更新対象の工程カンバン
                    int workKanbanIndex = workNum - 1;
                    WorkKanbanInfoEntity workKanban = kanban.getWorkKanbanCollection().get(workKanbanIndex);

                    // 工程カンバンステータスが「計画中(Planning)」，「計画済み(Planned)」以外の場合は更新しない
                    KanbanStatusEnum workStatus = workKanban.getWorkStatus();
                    if (!workStatus.equals(KanbanStatusEnum.PLANNING)
                            && !workStatus.equals(KanbanStatusEnum.PLANNED)) {
                        // 更新できない工程カンバンのためスキップ
                        addResult(String.format("  > %s: %s (%s: %s)",
                                LocaleUtils.getString("key.ImportKanban_NotUpdateWorkKanban"), LocaleUtils.getString(workStatus.getResourceKey()),
                                LocaleUtils.getString("key.ProcessName"), workKanban.getWorkName()));
                        continue;
                    }

                    // スキップフラグ
                    if (Objects.nonNull(work.getSkipFlag()) && !work.getSkipFlag().isEmpty()) {
                        if (work.getSkipFlag().equals("1")) {
                            workKanban.setSkipFlag(true);
                        } else {
                            workKanban.setSkipFlag(false);
                        }
                    }

                    // 開始予定日時
                    if (Objects.nonNull(work.getStartDatetime()) && !work.getStartDatetime().isEmpty()) {
                        Date workStartDateTime = datetimeFormatter.parse(work.getStartDatetime());
                        workKanban.setStartDatetime(workStartDateTime);
                    }

                    // 完了予定日時
                    if (Objects.nonNull(work.getCompDatetime()) && !work.getCompDatetime().isEmpty()) {
                        Date workCompDateTime = datetimeFormatter.parse(work.getCompDatetime());
                        workKanban.setCompDatetime(workCompDateTime);
                    }

                    // 組織
                    String organizations = work.getOrganizations();
                    if (Objects.nonNull(organizations) && !organizations.isEmpty()) {
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
                                addResult(String.format("  > %s: %s (%s: %s)",
                                        LocaleUtils.getString("key.ImportKanban_OrganizationNothing"), oranizationIdent,
                                        LocaleUtils.getString("key.ProcessName"), workKanban.getWorkName()));
                            }
                        }

                        workKanban.setOrganizationCollection(orgIdList);
                    }

                    // 設備
                    String equipments = work.getEquipments();
                    if (Objects.nonNull(equipments) && !equipments.isEmpty()) {
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
                                addResult(String.format("  > %s: %s (%s: %s)",
                                        LocaleUtils.getString("key.ImportKanban_EquipmentNothing"), equipmentIdent,
                                        LocaleUtils.getString("key.ProcessName"), workKanban.getWorkName()));
                            }
                        }

                        workKanban.setEquipmentCollection(equipIdList);
                    }

                    // 工程カンバンプロパティ
                    List<ImportWorkKanbanPropertyEntity> wkKanbanProps = importWkKanbanProps.stream().
                            filter(p -> kanbanName.equals(p.getKanbanName()) && work.getWorkNum().equals(p.getWorkNum())).collect(Collectors.toList());
                    for (ImportWorkKanbanPropertyEntity wkKanbanProp : wkKanbanProps) {
                        String propName = wkKanbanProp.getWkKanbanPropName();
                        if (Objects.isNull(propName) || propName.isEmpty()) {
                            continue;
                        }

                        // プロパティ型
                        CustomPropertyTypeEnum propType = null;
                        if (Objects.nonNull(wkKanbanProp.getWkKanbanPropType()) && !wkKanbanProp.getWkKanbanPropType().isEmpty()) {
                            String propTypeString = wkKanbanProp.getWkKanbanPropType();
                            if (CustomPropertyTypeEnum.TYPE_STRING.name().equals(propTypeString)) {
                                propType = CustomPropertyTypeEnum.TYPE_STRING;
                            } else if (CustomPropertyTypeEnum.TYPE_BOOLEAN.name().equals(propTypeString)) {
                                propType = CustomPropertyTypeEnum.TYPE_BOOLEAN;
                            } else if (CustomPropertyTypeEnum.TYPE_INTEGER.name().equals(propTypeString)) {
                                propType = CustomPropertyTypeEnum.TYPE_INTEGER;
                            } else if (CustomPropertyTypeEnum.TYPE_NUMERIC.name().equals(propTypeString)) {
                                propType = CustomPropertyTypeEnum.TYPE_NUMERIC;
                            } else if (CustomPropertyTypeEnum.TYPE_IP4_ADDRESS.name().equals(propTypeString)) {
                                propType = CustomPropertyTypeEnum.TYPE_IP4_ADDRESS;
                            } else if (CustomPropertyTypeEnum.TYPE_MAC_ADDRESS.name().equals(propTypeString)) {
                                propType = CustomPropertyTypeEnum.TYPE_MAC_ADDRESS;
                            } else if (CustomPropertyTypeEnum.TYPE_PLUGIN.name().equals(propTypeString)) {
                                propType = CustomPropertyTypeEnum.TYPE_PLUGIN;
                            } else if (CustomPropertyTypeEnum.TYPE_TRACE.name().equals(propTypeString)) {
                                propType = CustomPropertyTypeEnum.TYPE_TRACE;
                            }
                        }

                        // 更新対象のカンバンプロパティ
                        Optional<WorkKanbanPropertyInfoEntity> findWkKanbanProps
                                = workKanban.getPropertyCollection().stream().filter(p -> propName.equals(p.getWorkKanbanPropName())).findFirst();
                        if (findWkKanbanProps.isPresent()) {
                            WorkKanbanPropertyInfoEntity wkKanbanPropEntity = findWkKanbanProps.get();

                            // プロパティ型
                            if (Objects.nonNull(propType)) {
                                wkKanbanPropEntity.setWorkKanbanPropType(propType);
                            }

                            // プロパティ値
                            if (Objects.nonNull(wkKanbanProp.getWkKanbanPropValue()) && !wkKanbanProp.getWkKanbanPropValue().isEmpty()) {
                                wkKanbanPropEntity.setWorkKanbanPropValue(wkKanbanProp.getWkKanbanPropValue());
                            }
                        } else if (Objects.nonNull(propType)) {
                            // 存在しないプロパティの場合は、プロパティ名とプロパティ値が入っていたら追加する。
                            WorkKanbanPropertyInfoEntity wkKanbanPropEntity = new WorkKanbanPropertyInfoEntity();
                            wkKanbanPropEntity.setWorkKanbanPropName(propName);
                            wkKanbanPropEntity.setWorkKanbanPropType(propType);

                            // プロパティ値
                            if (Objects.nonNull(wkKanbanProp.getWkKanbanPropValue()) && !wkKanbanProp.getWkKanbanPropValue().isEmpty()) {
                                wkKanbanPropEntity.setWorkKanbanPropValue(wkKanbanProp.getWkKanbanPropValue());
                            }

                            // プロパティ表示順の最大値から、次に追加するプロパティの表示順を設定
                            int propOrder = 1;
                            Optional<WorkKanbanPropertyInfoEntity> lastProp
                                    = workKanban.getPropertyCollection().stream().max(Comparator.comparingInt(p -> p.getWorkKanbanPropOrder()));
                            if (lastProp.isPresent()) {
                                propOrder = lastProp.get().getWorkKanbanPropOrder() + 1;
                            }
                            wkKanbanPropEntity.setWorkKanbanPropOrder(propOrder);

                            workKanban.getPropertyCollection().add(wkKanbanPropEntity);
                        }
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

            // 更新日時
            Date updateDateTime = DateUtils.toDate(LocalDateTime.now());
            kanban.setUpdateDatetime(updateDateTime);

            // 更新者
            kanban.setFkUpdatePersonId(loginUserInfoEntity.getId());

            // カンバン更新
            addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_UpdateKanban")));
            logger.info("import kanban:{}", kanban);
            ResponseEntity updateRes = kanbanInfoFacade.update(kanban);

            boolean isSuccess = false;
            if (Objects.nonNull(updateRes) && updateRes.isSuccess()) {
                // verInfoが更新されているため、カンバン情報を再取得する。
                kanban = kanbanInfoFacade.find(kanban.getKanbanId());

                // kanban_status.csvの値を反映する.
                if (statusMap.containsKey(kanbanName)) {
                    kanban.setKanbanStatus(statusMap.get(kanbanName));
                } else {
                    // カンバンステータスを元の状態に戻す
                    kanban.setKanbanStatus(kanbanStatus);
                }

                // カンバンステータスを更新する。
                updateRes = kanbanInfoFacade.update(kanban);
                if (Objects.nonNull(updateRes) && updateRes.isSuccess()) {
                    isSuccess = true;
                }
            }

            if (isSuccess) {
                // 更新成功
                successNum++;
                addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_UpdateSuccess")));
            } else {
                // 更新失敗
                failedNum++;
                addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_UpdateFailed")));
            }
        }

        addResult(String.format("%s: %s (%s: %s, %s: %s, %s: %s)",
                LocaleUtils.getString("key.ImportKanban_ProccessNum"), procNum,
                LocaleUtils.getString("key.ImportKanban_SuccessNum"), successNum,
                LocaleUtils.getString("key.ImportKanban_SkipNum"), skipKanbanNum,
                LocaleUtils.getString("key.ImportKanban_FailedNum"), failedNum));
    }

    /**
     * カンバン情報 CSVファイル読込
     *
     * @param path ファイルパス
     * @return
     */
    private List<ImportKanbanEntity> readKanbanCsv(String path) {
        List<ImportKanbanEntity> importKanbans = null;
        List<String> row = null;
        int count = 0;

        try {
            List<List<String>> kanbanRows = CsvFileUtils.readCsv(path, 2, this.kanbanCsvEncode);// 2行目から読み込み

            importKanbans = new ArrayList<>();
            for (count = 0; count < kanbanRows.size(); count++) {
                row = kanbanRows.get(count);
                for (int i = 0; i < row.size(); i++) {
                    row.set(i, CsvFileUtils.repraceEscapeString(row.get(i)));
                }

                ImportKanbanEntity importKanban = new ImportKanbanEntity();
                importKanban.setKanbanHierarchyName(row.get(NUM_KAN_HIERARCHY_NAME));
                importKanban.setKanbanName(row.get(NUM_KAN_KANBAN_NAME));
                importKanban.setWorkflowName(row.get(NUM_KAN_WORKFLOW_NAME));

                if (row.size() >= NUM_KAN_WORKFLOW_REV + 1) {
                    if (!StringUtils.isEmpty(row.get(NUM_KAN_WORKFLOW_REV))) {
                        int rev = Integer.valueOf(row.get(NUM_KAN_WORKFLOW_REV));
                        importKanban.setWorkflowRev(rev);
                    }
                }

                if (row.size() >= NUM_KAN_MODEL_NAME + 1) {
                    importKanban.setModelName(row.get(NUM_KAN_MODEL_NAME));
                }

                if (row.size() >= NUM_KAN_START_DATETIME + 1) {
                    if (!StringUtils.isEmpty(row.get(NUM_KAN_START_DATETIME))) {
                        Date startDatetime = datetimeFormatter.parse(row.get(NUM_KAN_START_DATETIME));
                        importKanban.setStartDatetime(startDatetime);
                    }
                }

                importKanbans.add(importKanban);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.addResult(ex.getLocalizedMessage());
            this.addResult("  >  " + (count + 1) + ", " + String.join(", ", row));
        }
        return importKanbans;
    }

    /**
     * カンバンプロパティ情報 CSVファイル読込
     *
     * @param path
     * @return
     */
    private List<ImportKanbanPropertyEntity> readKanbanPropertyCsv(String path) {
        List<ImportKanbanPropertyEntity> importKanbanProps = null;
        try {
            List<List<String>> kanbanPropRows = CsvFileUtils.readCsv(path, 2, this.kanbanCsvEncode);// 2行目から読み込み

            importKanbanProps = new ArrayList<>();
            for (List<String> row : kanbanPropRows) {
                for (int i = 0; i < row.size(); i++) {
                    row.set(i, CsvFileUtils.repraceEscapeString(row.get(i)));
                }

                ImportKanbanPropertyEntity importKanbanProp = new ImportKanbanPropertyEntity();
                importKanbanProp.setKanbanName(row.get(NUM_KANP_KANBAN_NAME));
                importKanbanProp.setKanbanPropertyName(row.get(NUM_KANP_PROP_NAME));
                importKanbanProp.setKanbanPropertyType(row.get(NUM_KANP_PROP_TYPE));
                importKanbanProp.setKanbanPropertyValue(row.get(NUM_KANP_PROP_VALUE));

                importKanbanProps.add(importKanbanProp);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return importKanbanProps;
    }

    /**
     * 工程カンバン情報 CSVファイル読込
     *
     * @param path
     * @return
     */
    private List<ImportWorkKanbanEntity> readWorkKanbanCsv(String path) {
        List<ImportWorkKanbanEntity> importWorkKanbans = null;
        try {
            List<List<String>> workKanbanRows = CsvFileUtils.readCsv(path, 2, this.kanbanCsvEncode);// 2行目から読み込み

            importWorkKanbans = new ArrayList<>();
            for (List<String> row : workKanbanRows) {
                for (int i = 0; i < row.size(); i++) {
                    row.set(i, CsvFileUtils.repraceEscapeString(row.get(i)));
                }

                ImportWorkKanbanEntity importWorkKanban = new ImportWorkKanbanEntity();
                importWorkKanban.setKanbanName(row.get(NUM_WKAN_KANBAN_NAME));
                importWorkKanban.setWorkNum(row.get(NUM_WKAN_WORK_NUM));
                importWorkKanban.setSkipFlag(row.get(NUM_WKAN_SKIP_FLAG));
                importWorkKanban.setStartDatetime(row.get(NUM_WKAN_START_DATETIME));
                importWorkKanban.setCompDatetime(row.get(NUM_WKAN_COMP_DATETIME));
                importWorkKanban.setOrganizations(row.get(NUM_WKAN_ORGANIZATIONS));
                importWorkKanban.setEquipments(row.get(NUM_WKAN_EQUIPMENTS));

                importWorkKanbans.add(importWorkKanban);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return importWorkKanbans;
    }

    /**
     * カンバンステータス情報 CSVファイル読込
     *
     * @param path
     * @return
     */
    private Map<String, KanbanStatusEnum> readKanbanStatusCsv(String path) {
        Map<String, KanbanStatusEnum> importKanbanStatus = null;
        try {
            List<List<String>> kanbanStatusRows = CsvFileUtils.readCsv(path, 2, this.kanbanCsvEncode);// 2行目から読み込み

            importKanbanStatus = new HashMap();
            for (List<String> row : kanbanStatusRows) {
                for (int i = 0; i < row.size(); i++) {
                    row.set(i, CsvFileUtils.repraceEscapeString(row.get(i)));
                }
                importKanbanStatus.put(row.get(NUM_KANS_KANBAN_NAME), KanbanStatusEnum.getEnum(row.get(NUM_KANS_STATUS_NAME)));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return importKanbanStatus;
    }

    /**
     * 工程カンバンプロパティ情報 CSVファイル読込
     *
     * @param path
     * @return
     */
    private List<ImportWorkKanbanPropertyEntity> readWorkKanbanPropertyCsv(String path) {
        List<ImportWorkKanbanPropertyEntity> importWkKanbanProps = null;
        try {
            List<List<String>> workKanbanPropertyRows = CsvFileUtils.readCsv(path, 2, this.kanbanCsvEncode);// 2行目から読み込み

            importWkKanbanProps = new ArrayList<>();
            for (List<String> row : workKanbanPropertyRows) {
                for (int i = 0; i < row.size(); i++) {
                    row.set(i, CsvFileUtils.repraceEscapeString(row.get(i)));
                }

                ImportWorkKanbanPropertyEntity importWorkKanbanProperty = new ImportWorkKanbanPropertyEntity();
                importWorkKanbanProperty.setKanbanName(row.get(NUM_WKANP_KANBAN_NAME));
                importWorkKanbanProperty.setWorkNum(row.get(NUM_WKANP_WORK_NUM));
                importWorkKanbanProperty.setWkKanbanPropName(row.get(NUM_WKANP_PROP_NAME));
                importWorkKanbanProperty.setWkKanbanPropType(row.get(NUM_WKANP_PROP_TYPE));
                importWorkKanbanProperty.setWkKanbanPropValue(row.get(NUM_WKANP_PROP_VALUE));

                importWkKanbanProps.add(importWorkKanbanProperty);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return importWkKanbanProps;
    }
}
