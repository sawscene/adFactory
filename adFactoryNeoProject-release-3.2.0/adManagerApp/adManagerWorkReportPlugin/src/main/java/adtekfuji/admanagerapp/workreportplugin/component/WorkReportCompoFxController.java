/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.component;

import adtekfuji.admanagerapp.workreportplugin.common.WorkReportConfig;
import adtekfuji.admanagerapp.workreportplugin.entity.ChoiceControlNoEntity;
import adtekfuji.admanagerapp.workreportplugin.entity.ChoiceIndirectWorkEntity;
import adtekfuji.admanagerapp.workreportplugin.entity.ChoiceOrganizationEntity;
import adtekfuji.admanagerapp.workreportplugin.entity.DailyReportToolSetting;
import adtekfuji.admanagerapp.workreportplugin.entity.WorkReportCsv;
import adtekfuji.admanagerapp.workreportplugin.entity.WorkReportRowInfo;
import adtekfuji.admanagerapp.workreportplugin.enumerate.WorkReportWorkTypeEnum;
import adtekfuji.admanagerapp.workreportplugin.tablecell.TableDateCell;
import adtekfuji.admanagerapp.workreportplugin.tablecell.TableDateTimeCell;
import adtekfuji.admanagerapp.workreportplugin.tablecell.TableNumberCell;
import adtekfuji.admanagerapp.workreportplugin.tablecell.TableTextCell;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.DirectActualInfoFacade;
import adtekfuji.clientservice.IndirectActualInfoFacade;
import adtekfuji.clientservice.IndirectWorkInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WorkReportInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringTime;
import adtekfuji.utility.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectActualInfoEntity;
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.view.WorkReportInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業日報
 *
 * @author nar-nakamura
 */
@FxComponent(id = "WorkReportCompo", fxmlPath = "/fxml/admanagerworkreportplugin/work_report_compo.fxml")
public class WorkReportCompoFxController implements Initializable, ComponentHandler {

    private static final Logger logger = LogManager.getLogger();
    private static final SceneContiner sc = SceneContiner.getInstance();
    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static final WorkReportInfoFacade workReportInfoFacade = new WorkReportInfoFacade();
    private static final OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
    private static final IndirectWorkInfoFacade indirectWorkInfoFacade = new IndirectWorkInfoFacade();
    private static final IndirectActualInfoFacade indirectActualInfoFacade = new IndirectActualInfoFacade();
    private static final DirectActualInfoFacade directActualInfoFacade = new DirectActualInfoFacade();
    private static final WorkflowInfoFacade workflowInfoFaced = new WorkflowInfoFacade();

    private static final String WORK_DATE_FORMAT = "yyyyMMdd";
    private static final DateTimeFormatter localdateFormatter = DateTimeFormatter.ofPattern(WORK_DATE_FORMAT);

    private static String formatStringMonthDay = LocaleUtils.getString("key.format.MonthDay");// MM/dd
    private static String formatStringDate = LocaleUtils.getString("key.DateFormat");// yyyy/MM/dd
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(formatStringDate);
    private static final DateTimeFormatter monthDayCheckFormatter = DateTimeFormatter.ofPattern(formatStringMonthDay + "yyyy");
    private static final DateTimeFormatter notDivideDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final long RANGE_NUM = ClientServiceProperty.getRestRangeNum();
    private static final String CLASS_NUMBER_NONE = "NONE";
    private static final String CSV_LF = "\r\n";// CSV出力時の改行コード
    private static final String FILE_CHAR = "MS932";

    private static final String SYS_WORK_REPORT_HEADER = "workReportHeader";
    private static final String SYS_WORK_REPORT_HEADER_DEFAULT = "日付,氏名,職番,部署コード,作業No,工数M,注番,,,";
    private static final String SYS_WORK_REPORT_SELECTED_ORGANIZATION = "workReportSelectedOrganization";
    private static final String SYS_WORK_REPORT_TIME_FORMAT_SEC = "ss";
    private static final String SYS_WORK_REPORT_TIME_FORMAT_MIN = "mm";
    private static final String SYS_WORK_REPORT_TIME_FORMAT_HOUR = "hh";

    private static final String WORK_REPORT_TYPE = WorkReportConfig.getWorkReportType();
    private static final boolean WORK_REPORT_WORK_NUM_VISIBLE = WorkReportConfig.getWorkReportWorkNumVisible();
    private static final String WORK_REPORT_HEADER = AdProperty.getProperties().getProperty(SYS_WORK_REPORT_HEADER, SYS_WORK_REPORT_HEADER_DEFAULT);
    private static final String WORK_REPORT_TIME_FORMAT = AdProperty.getProperties().getProperty("workReportTimeFormat", SYS_WORK_REPORT_TIME_FORMAT_MIN);
    private static final String WORK_REPORT_TIME_UNIT = AdProperty.getProperties().getProperty("workReportTimeUnit", WorkReportRowInfo.SYS_WORK_REPORT_TIME_UNIT_SEC);

    private final boolean isUnitTimeVisible = WorkReportConfig.getWorkReportUnitTimeVisible();
    private final boolean isActualNumVisible = WorkReportConfig.getWorkReportActualNumVisible();

    private static final String PROP_NAME_DEPARTMENT_CODE = "部署コード";

    private static String TOOL_TITLE = LocaleUtils.getString("key.WorkReportTitle") + LocaleUtils.getString("key.Tool");// ツールのタイトル

    private final long REST_RANGE_NUM = ClientServiceProperty.getRestRangeNum();

    private final static String PICK_LITE_WORK_NAME_REGEX_KEY = "PickLiteWorkNameRegex";
    private String pickLiteWorkNameRegex;

    private final static int ORDER_NUM_MAX_LENGTH = 32;
    
    @FXML
    private PropertySaveTableView<WorkReportRowInfo> listView;
    @FXML
    private TableColumn<WorkReportRowInfo, String> workDateColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> organizationIdentifyColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> organizationNameColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> workNumberColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> workNameColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> orderNumberColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> workTimeColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> unitTimeColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, Number> actualNumColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, Number> workNumColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> serialNoColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> controlNoColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> resourcesColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> finalNumColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> defectNumColumn;
    @FXML
    private TableColumn<WorkReportRowInfo, String> stopTimeColumn;

    @FXML
    private TableColumn<WorkReportRowInfo, String> remarks1Column; // 備考1
    @FXML
    private TableColumn<WorkReportRowInfo, String> remarks2Column; // 備考2

    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button registButton;
    @FXML
    private Pane progressPane;
    @FXML
    private ListView<String> organizationList;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button toolButton;

    // 間接作業
    private final List<IndirectWorkInfoEntity> indirectWorks = new ArrayList();
    // 作業日報
    private final ObservableList<WorkReportRowInfo> workReportList = FXCollections.observableArrayList();
    // 削除リスト
    private final List<Long> deleteIdList = new ArrayList<>();
    // 組織選択リスト
    private final List<OrganizationInfoEntity> selectedOrganizations = new ArrayList<>();

    private final CashManager cache = CashManager.getInstance();

    private LocalDate dispStartDate;
    private LocalDate dispEndDate;

    private Long transactionId = -1L;

    private ChangeListener<LocalDate> changeStartDate;
    private ChangeListener<LocalDate> changeEndDate;

    /**
     * 作業日報画面を初期化する。
     *
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);

        listView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        formatStringMonthDay = LocaleUtils.getString("key.format.MonthDay");// MM/dd
        formatStringDate = LocaleUtils.getString("key.DateFormat");// yyyy/MM/dd
        TOOL_TITLE = LocaleUtils.getString("key.WorkReportTitle") + LocaleUtils.getString("key.Tool");// ツールのタイトル

        // 作業日(yyyy/MM/dd)
        this.workDateColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param)
                -> Bindings.createStringBinding(() -> Objects.isNull(param.getValue().getWorkDate()) ? "" : dateFormatter.format(param.getValue().getWorkDate())));
        // 組織識別名
        this.organizationIdentifyColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().getWorkReport().organizationIdentifyProperty());
        // 組織名
        this.organizationNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().getWorkReport().organizationNameProperty());
        // 作業No
        this.workNumberColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().getWorkReport().workNumberProperty());
        // 作業内容
        final boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());
        this.workNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> {
            StringProperty workNameProperty = param.getValue().getWorkReport().workNameProperty();
            if (isLiteOption) {
                // 工程名だけ抽出
                try {
                    Matcher m = Pattern.compile(this.pickLiteWorkNameRegex).matcher(workNameProperty.getValue());
                    if (m.find()) {
                        workNameProperty.setValue(m.group(1));
                    }
                } catch (Exception ex) {
                }
            }
            return workNameProperty;
        });

        //作業一覧の列幅を保存させる
        this.listView.init("WorkReport");

        if (WorkReportConfig.WORK_REPORT_TYPE_KANBAN.equals(WORK_REPORT_TYPE)) {
            // カンバン名
            this.orderNumberColumn.setText(LocaleUtils.getString("key.KanbanName"));
            this.orderNumberColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().getWorkReport().kanbanNameProperty());
        } else if (WorkReportConfig.WORK_REPORT_TYPE_PRODUCTION.equals(WORK_REPORT_TYPE)) {
            // 製造番号
            this.orderNumberColumn.setEditable(true);
            this.orderNumberColumn.setText(LocaleUtils.getString("key.ProductionNumber"));
            this.orderNumberColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().getWorkReport().productionNumberProperty());
        } else {
            // 注文番号
            this.orderNumberColumn.setEditable(true);
            this.orderNumberColumn.setText(LocaleUtils.getString("key.WorkReport.OrderNumber"));
            this.orderNumberColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().orderNumberProperty());
        }

        // 工数
        this.workTimeColumn.setText(LocaleUtils.getString("key.WorkReport.WorkTime") + "(" + WORK_REPORT_TIME_FORMAT + ")");
        this.workTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().workTimeProperty());

        // 工数/台
        this.unitTimeColumn.setText(this.unitTimeColumn.getText() + "(" + WORK_REPORT_TIME_UNIT + ")");
        this.unitTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().unitTimeProperty());
        this.unitTimeColumn.setVisible(isUnitTimeVisible);

        // 作業数
        this.actualNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, Number> param) -> param.getValue().actualNumProperty());
        this.actualNumColumn.setVisible(isActualNumVisible);

        // 作業数と製番の表示有無
        boolean workNumVisible = WORK_REPORT_WORK_NUM_VISIBLE && WorkReportConfig.WORK_REPORT_TYPE_KANBAN.equals(WORK_REPORT_TYPE);
        // 作業数
        this.workNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, Number> param) -> param.getValue().workNumProperty());
        this.workNumColumn.setVisible(workNumVisible);

        this.serialNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().serialNumbersProperty());
        this.serialNoColumn.setVisible(isUnitTimeVisible);

        // 製番
        this.controlNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().controlNoProperty());
        this.controlNoColumn.setVisible(workNumVisible);
        // 装置番号
        this.resourcesColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().resourcesProperty());
        this.resourcesColumn.setVisible(workNumVisible);
        // 完成数
        this.finalNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().finalNumProperty());
        this.finalNumColumn.setVisible(workNumVisible);
        // 不良数
        this.defectNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().defectNumProperty());
        this.defectNumColumn.setVisible(workNumVisible);
        // 装置停止
        this.stopTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().stopTimeProperty());
        this.stopTimeColumn.setVisible(workNumVisible);
        // 備考1
        this.remarks1Column.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().remarks1Property());
        this.remarks1Column.setVisible(workNumVisible);
        // 備考2
        this.remarks2Column.setCellValueFactory((TableColumn.CellDataFeatures<WorkReportRowInfo, String> param) -> param.getValue().remarks2Property());
        this.remarks2Column.setVisible(workNumVisible);

        //役割の権限によるボタン無効化.
        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            this.addButton.setDisable(true);
            this.deleteButton.setDisable(true);
            this.registButton.setDisable(true);
        } else {
            this.listView.setOnMouseClicked((MouseEvent event) -> {
                try {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (event.getClickCount() == 2) {
                            if (Objects.isNull(event.getSource())) {
                                return;
                            }
                            TableView<WorkReportRowInfo> table = (TableView) event.getSource();

                            if (Objects.isNull(table.getEditingCell())) {
                                return;
                            }

                            // 行情報が存在しない場合は何もしない。
                            WorkReportRowInfo rowInfo = table.getItems().get(table.getEditingCell().getRow());
                            if (Objects.isNull(rowInfo)) {
                                return;
                            }

                            TableColumn column = this.listView.getEditingCell().getTableColumn();
                            if (organizationNameColumn.equals(column)) {
                                // 組織選択
                                if (rowInfo.getWorkReport().getWorkType() == WorkReportWorkTypeEnum.INDIRECT_WORK.getValue()) {
                                    // 間接作業の実績のみ編集する
                                    this.editOrganization(rowInfo);
                                }
                            } else if (workNameColumn.equals(column)) {
                                // 間接作業選択
                                if (rowInfo.getWorkReport().getWorkType() == WorkReportWorkTypeEnum.INDIRECT_WORK.getValue()) {
                                    // 間接作業の実績のみ編集する
                                    this.editWork(rowInfo);
                                }
                            } else if (controlNoColumn.equals(column)) {
                                // 製番選択
                                this.editControlNo(rowInfo);
                            }
                        }
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });

            Callback<TableColumn<WorkReportRowInfo, String>, TableCell<WorkReportRowInfo, String>> dateCellFactory = (TableColumn<WorkReportRowInfo, String> p) -> new TableDateCell();
            Callback<TableColumn<WorkReportRowInfo, String>, TableCell<WorkReportRowInfo, String>> textCellFactory = (TableColumn<WorkReportRowInfo, String> p)
                    -> new TableTextCell(Arrays.asList(this.workTimeColumn, this.resourcesColumn));
            Callback<TableColumn<WorkReportRowInfo, String>, TableCell<WorkReportRowInfo, String>> numberCellFactory = (TableColumn<WorkReportRowInfo, String> p) -> new TableNumberCell();
            Callback<TableColumn<WorkReportRowInfo, String>, TableCell<WorkReportRowInfo, String>> datetimeCellFactory = (TableColumn<WorkReportRowInfo, String> p) -> new TableDateTimeCell();

            // 作業日 (yyyy/MM/dd)
            this.workDateColumn.setCellFactory(dateCellFactory);
            this.workDateColumn.setOnEditCommit((TableColumn.CellEditEvent<WorkReportRowInfo, String> event) -> {
                try {
                    String oldValue = event.getOldValue();
                    String newValue = event.getNewValue();

                    WorkReportRowInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    WorkReportInfoEntity workReport = item.getWorkReport();

                    LocalDate date = this.getDateFromMonthDay(newValue);
                    if (Objects.isNull(date)) {
                        // 元に戻す。
                        LocalDate oldDate = this.getDateFromMonthDay(oldValue);
                        item.setWorkDate(oldDate);
                    } else if (!newValue.equals(oldValue)) {
                        // 変更があって、他に重複する行がない場合、行のデータを更新する。
                        String workDate = String.format("%04d%02d%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
                        WorkReportRowInfo searchRow = getWorkReport(workDate, workReport.getOrganizationIdentify(), workReport.getClassNumber(), workReport.getWorkNumber(), workReport.getProductionNumber());
                        if (Objects.isNull(searchRow)) {
                            item.setIsEdit(true);
                            item.setIsEdit(true);
                            workReport.setWorkDate(workDate);
                        } else {
                            // 指定された作業は既に存在します。
                            DialogBox.warn("key.Warning", "key.IndirectWorkAlreadyExists");
                            LocalDate oldDate = this.getDateFromMonthDay(oldValue);
                            item.setWorkDate(oldDate);
                        }
                    }
                    event.getTableView().refresh();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });

            // 組織識別名
            this.organizationIdentifyColumn.setCellFactory(textCellFactory);
            this.organizationIdentifyColumn.setOnEditCommit((TableColumn.CellEditEvent<WorkReportRowInfo, String> event) -> {
                try {
                    String oldValue = event.getOldValue();
                    String newValue = event.getNewValue();

                    WorkReportRowInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    WorkReportInfoEntity workReport = item.getWorkReport();

                    OrganizationInfoEntity newOrganization = this.getOrganization(newValue);
                    if (Objects.isNull(newOrganization)) {
                        // 存在しない組織の場合、元に戻す。
                        workReport.setOrganizationIdentify("");// 描画更新のため、一旦クリア
                        workReport.setOrganizationIdentify(oldValue);
                    } else if (!newValue.equals(oldValue)) {
                        // 変更があって、他に重複する行がない場合、行のデータを更新する。
                        WorkReportRowInfo searchRow = getWorkReport(workReport.getWorkDate(), newOrganization.getOrganizationIdentify(), workReport.getClassNumber(), workReport.getWorkNumber(), workReport.getProductionNumber());
                        if (Objects.isNull(searchRow)) {
                            item.setIsEdit(true);
                            workReport.setOrganizationId(newOrganization.getOrganizationId());
                            workReport.setOrganizationIdentify(newOrganization.getOrganizationIdentify());
                            workReport.setOrganizationName(newOrganization.getOrganizationName());
                        } else {
                            // 指定された作業は既に存在します。
                            DialogBox.warn("key.Warning", "key.IndirectWorkAlreadyExists");
                            workReport.setOrganizationIdentify("");// 描画更新のため、一旦クリア
                            workReport.setOrganizationIdentify(oldValue);
                        }
                    }
                    event.getTableView().refresh();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });

            // 作業No
            this.workNumberColumn.setCellFactory(textCellFactory);
            this.workNumberColumn.setOnEditCommit((TableColumn.CellEditEvent<WorkReportRowInfo, String> event) -> {
                try {
                    String oldValue = event.getOldValue();
                    String newValue = event.getNewValue();

                    WorkReportRowInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    WorkReportInfoEntity workReport = item.getWorkReport();

                    IndirectWorkInfoEntity newWork = this.getIndirectWork(CLASS_NUMBER_NONE, newValue);
                    if (Objects.isNull(newWork)) {
                        // 存在しない間接作業の場合、元に戻す。
                        workReport.setWorkNumber("");// 描画更新のため、一旦クリア
                        workReport.setWorkNumber(oldValue);
                    } else if (!newValue.equals(oldValue)) {
                        // 変更があって、他に重複する行がない場合、行のデータを更新する。
                        WorkReportRowInfo searchRow = getWorkReport(workReport.getWorkDate(), workReport.getOrganizationIdentify(), newWork.getClassNumber(), newWork.getWorkNumber(), workReport.getProductionNumber());
                        if (Objects.isNull(searchRow)) {
                            item.setIsEdit(true);
                            workReport.setWorkId(newWork.getIndirectWorkId());
                            workReport.setClassNumber(newWork.getClassNumber());
                            workReport.setWorkNumber(newWork.getWorkNumber());
                            workReport.setWorkName(newWork.getWorkName());
                        } else {
                            // 指定された作業は既に存在します。
                            DialogBox.warn("key.Warning", "key.IndirectWorkAlreadyExists");
                            workReport.setWorkNumber("");// 描画更新のため、一旦クリア
                            workReport.setWorkNumber(oldValue);
                        }
                    }
                    event.getTableView().refresh();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });
            
            // 注文番号
            this.orderNumberColumn.setCellFactory(textCellFactory);
            this.orderNumberColumn.setOnEditCommit((TableColumn.CellEditEvent<WorkReportRowInfo, String> event) -> {
                try {
                    String oldValue = event.getOldValue();
                    String newValue = event.getNewValue();
                    WorkReportRowInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    WorkReportInfoEntity workReport = item.getWorkReport();

                    // 全角文字が含まれているかどうかをチェック
                    boolean isFullWidthChar = false;
                    char[] ordernum = newValue.toCharArray();
                    for (int count = 0; count < ordernum.length; count++) {
                        if (String.valueOf(ordernum[count]).getBytes().length > 1) {
                            isFullWidthChar = true;
                            break;
                        }
                    }

                    if (isFullWidthChar) {
                        //　全角文字が含まれている場合は前回の設定値に戻す
                        workReport.setProductionNumber("");// 描画更新のため、一旦クリア
                        workReport.setProductionNumber(oldValue);
                    } else if (!newValue.equals(oldValue) && newValue.length() <= ORDER_NUM_MAX_LENGTH) {

                        WorkReportRowInfo searchRow = getWorkReport(workReport.getWorkDate(), workReport.getOrganizationIdentify(), workReport.getClassNumber(), workReport.getWorkNumber(), newValue);
                        if (Objects.isNull(searchRow)) {
                            item.setIsEdit(true);
                            workReport.setProductionNumber(newValue);
                        } else {
                            // 指定された作業は既に存在します。
                            DialogBox.warn("key.Warning", "key.IndirectWorkAlreadyExists");
                            workReport.setProductionNumber("");// 描画更新のため、一旦クリア
                            workReport.setProductionNumber(oldValue);
                        }
                    }
                    event.getTableView().refresh();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });

            // 製造番号
            if (!WorkReportConfig.WORK_REPORT_TYPE_KANBAN.equals(WORK_REPORT_TYPE)) {
                this.orderNumberColumn.setCellFactory(textCellFactory);
                this.orderNumberColumn.setOnEditCommit((TableColumn.CellEditEvent<WorkReportRowInfo, String> event) -> {
                    try {
                        String oldValue = event.getOldValue();
                        String newValue = event.getNewValue();

                        WorkReportRowInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
                        WorkReportInfoEntity workReport = item.getWorkReport();

                        // 最大文字数チェック(半角32文字、全角16文字)
                        int byte_size = 0;
                        char[] prodnum = newValue.toCharArray();
                        for (int count = 0; count < prodnum.length; count++) {
                            if (String.valueOf(prodnum[count]).getBytes().length <= 1) {
                                byte_size += 1; //半角文字なら＋１
                            } else {
                                byte_size += 2; //全角文字なら＋２
                            }
                        }
                        if (byte_size > 32) {
                            // 32文字数超過は前回の設定値に戻す
                            item.getWorkReport().setProductionNumber("");// 描画更新のため、一旦クリア
                            item.getWorkReport().setProductionNumber(oldValue);
                        } else if (!newValue.equals(oldValue)) {
                            // 変更があって、他に重複する行がない場合、行のデータを更新する。
                            WorkReportRowInfo searchRow = getWorkReport(workReport.getWorkDate(), workReport.getOrganizationIdentify(), workReport.getClassNumber(), workReport.getWorkNumber(), newValue);
                            if (Objects.isNull(searchRow)) {
                                item.setIsEdit(true);
                                workReport.setProductionNumber(newValue);
                            } else {
                                // 指定された作業は既に存在します。
                                DialogBox.warn("key.Warning", "key.IndirectWorkAlreadyExists");
                                workReport.setProductionNumber("");// 描画更新のため、一旦クリア
                                workReport.setProductionNumber(oldValue);
                            }
                        }
                        event.getTableView().refresh();
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                });
            }

            // 工数
            List<String> timeFormat = Arrays.asList(WORK_REPORT_TIME_FORMAT.split(":"));
            if (timeFormat.size() == 3) {
                this.workTimeColumn.setCellFactory(textCellFactory);
            } else if (timeFormat.size() > 1) {
                this.workTimeColumn.setCellFactory(datetimeCellFactory);
            } else {
                this.workTimeColumn.setCellFactory(numberCellFactory);
            }

            this.workTimeColumn.setOnEditCommit((TableColumn.CellEditEvent<WorkReportRowInfo, String> event) -> {
                try {
                    WorkReportRowInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    item.setIsEdit(true);
                    String workTimeFomat;

                    // 入力値の範囲チェック
                    Long newWorkTime = 0L;
                    if (timeFormat.size() == 3) {
                        // hh:mm:ss
                        newWorkTime = StringTime.convertStringTimeToMillis(event.getNewValue());
                        workTimeFomat = "24:00:00";
                    } else if (timeFormat.size() == 2 && !event.getNewValue().isEmpty()) {
                        String[] split = event.getNewValue().split(":");
                        if (timeFormat.get(0).equals(SYS_WORK_REPORT_TIME_FORMAT_HOUR)) {
                            // hh:mm
                            newWorkTime = newWorkTime + TimeUnit.HOURS.toMillis(Long.parseLong(split[0]));
                            newWorkTime = newWorkTime + TimeUnit.MINUTES.toMillis(Long.parseLong(split[1]));
                            workTimeFomat = "24:00";

                        } else {
                            // mm:ss
                            newWorkTime = newWorkTime + TimeUnit.MINUTES.toMillis(Long.parseLong(split[0]));
                            newWorkTime = newWorkTime + TimeUnit.SECONDS.toMillis(Long.parseLong(split[1]));
                            workTimeFomat = "1440:00";
                        }
                    } else {
                        if (timeFormat.contains(SYS_WORK_REPORT_TIME_FORMAT_HOUR)) {
                            // hh
                            newWorkTime = TimeUnit.HOURS.toMillis(Long.parseLong(event.getNewValue()) - 1);
                            workTimeFomat = "24";
                        } else if (timeFormat.contains(SYS_WORK_REPORT_TIME_FORMAT_MIN)) {
                            // mm
                            newWorkTime = TimeUnit.MINUTES.toMillis(Long.parseLong(event.getNewValue()));
                            workTimeFomat = "1440";
                        } else {
                            // ss
                            newWorkTime = newWorkTime + TimeUnit.SECONDS.toMillis(Long.parseLong(event.getNewValue()));
                            workTimeFomat = "86400";
                        }
                    }

                    if (newWorkTime > 86400000) {
                        item.setWorkTime(workTimeFomat);
                    } else {
                        item.setWorkTime(event.getNewValue());
                    }
                    event.getTableView().refresh();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });
            // 装置番号
            this.resourcesColumn.setCellFactory(textCellFactory);
            this.resourcesColumn.setOnEditCommit((TableColumn.CellEditEvent<WorkReportRowInfo, String> event) -> {
                try {
                    WorkReportRowInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    item.setIsEdit(true);
                    String newResources = event.getNewValue();
                    if (newResources.length() <= 16) {
                        item.setResourcesProp(newResources);
                    }
                    event.getTableView().refresh();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });
            
            this.finalNumColumn.setCellFactory(numberCellFactory);
            this.finalNumColumn.setOnEditCommit((TableColumn.CellEditEvent<WorkReportRowInfo, String> event) -> {
                try {
                    WorkReportRowInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    item.setIsEdit(true);
                    Integer workCount = this.workNumColumn.getCellData(item).intValue();
                    Integer newFinalNum = Integer.parseInt(event.getNewValue());

                    if (workCount >= newFinalNum) {
                        Integer newDefectedNum = workCount - newFinalNum;
                        item.setFinalNum(newFinalNum.toString());
                        item.setDefectNum(newDefectedNum.toString());
                    }
                    event.getTableView().refresh();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });

            this.defectNumColumn.setCellFactory(numberCellFactory);
            this.defectNumColumn.setOnEditCommit((TableColumn.CellEditEvent<WorkReportRowInfo, String> event) -> {
                try {
                    WorkReportRowInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    item.setIsEdit(true);
                    Integer workCount = this.workNumColumn.getCellData(item).intValue();
                    Integer newDefectNum = Integer.parseInt(event.getNewValue());

                    if (workCount >= newDefectNum) {
                        Integer newFinalNum = workCount - newDefectNum;
                        item.setDefectNum(newDefectNum.toString());
                        item.setFinalNum(newFinalNum.toString());
                    }
                    event.getTableView().refresh();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });

            this.stopTimeColumn.setCellFactory(datetimeCellFactory);
            this.stopTimeColumn.setOnEditCommit((TableColumn.CellEditEvent<WorkReportRowInfo, String> event) -> {
                try {

                    WorkReportRowInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());
                    item.setIsEdit(true);

                    // 入力値の範囲チェック
                    Long newTime = 0L;
                    String[] split = event.getNewValue().split(":");
                    // hh:mm
                    newTime = newTime + TimeUnit.HOURS.toMillis(Long.parseLong(split[0]));
                    newTime = newTime + TimeUnit.MINUTES.toMillis(Long.parseLong(split[1]));

                    if (newTime > 86400000) {
                        item.setStopTime("24:00");
                    } else {
                        item.setStopTime(event.getNewValue());
                    }
                    event.getTableView().refresh();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });

        }

        this.startDatePicker.setValue(LocalDate.now());
        this.endDatePicker.setValue(LocalDate.now());

        // 終了日より後日は選択不可
        this.startDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (Objects.isNull(date) || Objects.isNull(endDatePicker.getValue())) {
                            return;
                        }

                        if (date.isAfter(endDatePicker.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: LightGrey;");
                        }
                    }
                };
            }
        });

        // 開始日より前日は選択不可
        this.endDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        if (Objects.isNull(date) || Objects.isNull(startDatePicker.getValue())) {
                            return;
                        }

                        super.updateItem(date, empty);
                        if (date.isBefore(startDatePicker.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: LightGrey;");
                        }
                    }
                };
            }
        });

        this.listView.setItems(this.workReportList);

        this.listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.listView.getSelectionModel().clearSelection();

        // 選択組織の初期化
        this.selectedOrganizations.clear();

        String selectedIds = AdProperty.getProperties().getProperty(SYS_WORK_REPORT_SELECTED_ORGANIZATION, "");
        if (!selectedIds.isEmpty()) {
            List<Long> ids = Arrays.asList(selectedIds.split(","))
                    .stream().map(Long::valueOf).collect(Collectors.toList());

            List<OrganizationInfoEntity> orgs = CacheUtils.getCacheOrganization(ids);
            this.selectedOrganizations.addAll(orgs);
        }

        //プロパティ読み込み
        this.loadProperties();

        this.changeStartDate = (Observable, oldValue, newValue) -> {
            try {
                blockUI(true);
                // 追加・更新がある場合は警告する。
                if (this.isChanged()) {
                    // データは変更されています。変更内容を破棄しますか？
                    String title = LocaleUtils.getString("key.Warning");
                    String message = LocaleUtils.getString("key.warn.HasBeenChanged") + "\n" + LocaleUtils.getString("key.warn.DiscardChanges");
                    ButtonType buttonType = sc.showMessageBox(Alert.AlertType.WARNING, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
                    if (ButtonType.YES != buttonType) {
                        // 期間開始日を元に戻す
                        this.startDatePicker.valueProperty().removeListener(this.changeStartDate);
                        this.startDatePicker.setValue(oldValue);
                        this.startDatePicker.valueProperty().addListener(this.changeStartDate);
                        return;
                    }
                }
                if (!Objects.equals(newValue, this.dispStartDate)) {
                    this.updateViewThread(false);
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            } finally {
                blockUI(false);
            }
        };

        this.changeEndDate = (observable, oldValue, newValue) -> {
            try {
                blockUI(true);
                // 追加・更新がある場合は警告する。
                if (this.isChanged()) {
                    // データは変更されています。変更内容を破棄しますか？
                    String title = LocaleUtils.getString("key.Warning");
                    String message = LocaleUtils.getString("key.warn.HasBeenChanged") + "\n" + LocaleUtils.getString("key.warn.DiscardChanges");
                    ButtonType buttonType = sc.showMessageBox(Alert.AlertType.WARNING, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
                    if (ButtonType.YES != buttonType) {
                        // 期間開始日を元に戻す
                        this.endDatePicker.valueProperty().removeListener(this.changeEndDate);
                        this.endDatePicker.setValue(oldValue);
                        this.endDatePicker.valueProperty().addListener(this.changeEndDate);
                        return;
                    }
                }
                if (!Objects.equals(newValue, this.dispEndDate)) {
                    this.updateViewThread(false);
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            } finally {
                blockUI(false);
            }
        };

        // 開始日に変更があった場合、即座に更新する。
        this.startDatePicker.valueProperty().addListener(this.changeStartDate);
        // 終了日に変更があった場合、即座に更新する。
        this.endDatePicker.valueProperty().addListener(this.changeEndDate);

        // 表示更新 (初期化中)
        this.updateViewThread(true);
    }

    /**
     * 最新の状態に更新する
     *
     * @param event
     */
    @FXML
    private void onUpdateAction(ActionEvent event) {
        logger.info("onUpdateAction start. {}, {}, {}", this.selectedOrganizations.stream().map(OrganizationInfoEntity::getOrganizationId).collect(Collectors.toList()), this.dispStartDate, this.dispEndDate);
        this.updateViewThread(true);
    }

    /**
     * 作業追加ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onAddAction(ActionEvent event) {
        try {
            WorkReportInfoEntity workReport = new WorkReportInfoEntity();
            workReport.setWorkType(WorkReportWorkTypeEnum.INDIRECT_WORK.getValue());// 間接作業
            workReport.setWorkTypeOrder(WorkReportWorkTypeEnum.INDIRECT_WORK.getSortOrder());// 作業種別の順
            workReport.setActualNum(0);

            WorkReportRowInfo newRow = new WorkReportRowInfo(workReport, WORK_REPORT_TIME_FORMAT, WORK_REPORT_TIME_UNIT);
            newRow.getWorkReport().setProductionNumber("");
            newRow.setIsNew(true);

            // 新規行を追加する。
            this.workReportList.add(newRow);
            this.listView.refresh();

            // 追加した行に移動する。
            this.listView.scrollTo(newRow);
            this.listView.getSelectionModel().select(newRow);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 削除ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onDeleteAction(ActionEvent event) {
        try {
            this.blockUI(true);

            if (this.listView.getSelectionModel().getSelectedItems().isEmpty()) {
                return;
            }

            List<WorkReportRowInfo> deleteRows = new ArrayList();
            List<WorkReportRowInfo> undeleteRows = new ArrayList();

            this.listView.getSelectionModel().getSelectedItems().stream().forEach(row -> {
                // 対象行が間接作業の場合は削除する。
                if (row.getWorkReport().getWorkType() == WorkReportWorkTypeEnum.INDIRECT_WORK.getValue()) {
                    // 対象行がデータベースに登録済の場合、データベース削除リストに追加する。
                    if (!row.getIsNew()) {
                        this.deleteIdList.add(row.getWorkReport().getIndirectActualId());
                    }

                    deleteRows.add(row);
                } else {
                    undeleteRows.add(row);
                }
            });

            // 選択行をクリア
            this.listView.getSelectionModel().clearSelection();

            // 削除対象の行を削除
            this.workReportList.removeAll(deleteRows);

            // 選択されていたが削除対象外だった行を再選択
            undeleteRows.stream().forEach(row -> {
                this.listView.getSelectionModel().select(row);
            });

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.listView.refresh();
            this.blockUI(false);
        }
    }

    /**
     * データ出力ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onOutputAction(ActionEvent event) {
        try {
            this.blockUI(true);

            // 日報リストが空の場合は出力しない。
            if (this.workReportList.isEmpty()) {
                return;
            }

            // 間接作業の未入力・重複がある場合は出力しない。
            if (!this.checkData()) {
                // 未入力の項目があります。必須入力項目を入力してください。
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return;
            }

            // 追加・更新がある場合は警告する。
            if (this.isChanged()) {
                // データは変更されています。データを出力しますか？
                String title = LocaleUtils.getString("key.Warning");
                String message = LocaleUtils.getString("key.warn.HasBeenChanged") + "\n" + LocaleUtils.getString("key.warn.WantToOutputData");
                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.WARNING, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
                if (ButtonType.YES != buttonType) {
                    return;
                }
            }

            Node node = (Node) event.getSource();

            String path = WorkReportConfig.getWorkReportOutputPath();
            File dir = Paths.get(path).toFile();
            if (!dir.isDirectory() || !dir.exists()) {
                dir = new File(System.getProperty("user.home"), "Documents");
            }

            String fileName = String.format("%s_%s-%s.csv", WorkReportConfig.getWorkReportBaseFilename(),
                    this.dispStartDate.format(notDivideDateFormatter), this.dispEndDate.format(notDivideDateFormatter));

            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(dir);
            chooser.setInitialFileName(fileName);
            chooser.setTitle(LocaleUtils.getString("key.WorkReportTitle"));
            chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"), new FileChooser.ExtensionFilter("TSV files (*.tsv)", "*.tsv"));
            File file = chooser.showSaveDialog(node.getScene().getWindow());
            if (Objects.isNull(file)) {
                return;
            }

            // CSVファイルのヘッダー情報
            String header = WORK_REPORT_HEADER;

            // データ出力
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // CSVファイルを出力する。
                    outputFile(header, workReportList, file);
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
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
        } finally {
            this.blockUI(false);
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onToolAction(ActionEvent event) {
        this.blockUI(true);
        try {
            // 作業時間
            int sumWorkTime = 0;
            for (WorkReportRowInfo row : this.workReportList) {
                WorkReportInfoEntity workReport = row.getWorkReport();
                if ((0 == workReport.getWorkType() || 3 == workReport.getWorkType())
                        || Objects.isNull(workReport.getWorkTime())) {
                    continue;
                }

                int workTime = workReport.getWorkTime() == null ? 0 : workReport.getWorkTime();
                int workTimeMin = workTime / 60000;
                int workTimeSec = (workTime % 60000) / 1000;

                // 分 (端数の秒は切り上げ)
                sumWorkTime += workTimeMin;
                if (workTimeSec > 0) {
                    sumWorkTime++;
                }
            }

            // 作業日報ツールの設定
            DailyReportToolSetting toolSetting = new DailyReportToolSetting();
            toolSetting.setWorkMin(sumWorkTime);
            toolSetting.setWorkTimeBgColor(WorkReportConfig.getWorkReportRowColorDirectWork());

            try {
                sc.showComponentDialog(TOOL_TITLE, "DailyReportTool", toolSetting, new ButtonType[]{ButtonType.OK});
            } catch (NoSuchElementException e) {
                // [x]ボタンで閉じると、ButtonType が未設定で NoSuchElementException で返ってくるので無視する。
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.blockUI(false);
        }
    }

    /**
     * 登録ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onRegistAction(ActionEvent event) {
        try {
            this.blockUI(true);

            // 間接作業の未入力がある場合は出力しない。
            if (!this.checkData()) {
                // 未入力の項目があります。必須入力項目を入力してください。
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                this.blockUI(false);
                return;
            }

            // 保存して画面を更新。
            if (this.save()) {
                // 日報年月を表示しているデータに合わせてから更新する。
                this.startDatePicker.setValue(this.dispStartDate);
                this.endDatePicker.setValue(this.dispEndDate);

                // 表示更新
                this.updateViewThread(false);
            }
        } catch (Exception ex) {
            logger.fatal(ex);
            this.blockUI(false);
        }
    }

    /**
     * 組織選択ボタンのアクション
     */
    @FXML
    private void onSelectAction(ActionEvent event) {
        boolean isUpdate = false;
        try {
            this.blockUI(true);

            // 追加・更新がある場合は警告する。
            if (this.isChanged()) {
                // データは変更されています。変更内容を破棄しますか？
                String title = LocaleUtils.getString("key.Warning");
                String message = LocaleUtils.getString("key.warn.HasBeenChanged") + "\n" + LocaleUtils.getString("key.warn.DiscardChanges");
                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.WARNING, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
                if (ButtonType.YES != buttonType) {
                    // 日報年月を表示しているデータに合わせる。
                    this.startDatePicker.setValue(this.dispStartDate);
                    this.endDatePicker.setValue(this.dispEndDate);
                    return;
                }
            }

            SelectDialogEntity selectDialogEntity = new SelectDialogEntity().organizations(selectedOrganizations);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialogEntity, true);
            if (ret.equals(ButtonType.OK)) {
                selectedOrganizations.clear();
                selectedOrganizations.addAll(selectDialogEntity.getOrganizations());

                StringBuilder sb = new StringBuilder();
                selectedOrganizations.forEach((sel) -> {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(sel.getOrganizationId().toString());
                });
                AdProperty.getProperties().setProperty(SYS_WORK_REPORT_SELECTED_ORGANIZATION, sb.toString());

                // 表示更新
                isUpdate = true;
                this.updateViewThread(false);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            if (!isUpdate) {
                this.blockUI(false);
            }
        }
    }

    /**
     * 間接作業情報一覧を取得する。
     */
    private void readIndirectWorks() {
        try {
            this.indirectWorks.clear();

            long max = indirectWorkInfoFacade.count();
            if (max > 0) {
                for (long count = 0; count <= max; count += RANGE_NUM) {
                    List<IndirectWorkInfoEntity> entities = indirectWorkInfoFacade.findRange(count, count + RANGE_NUM - 1);
                    if (!entities.isEmpty()) {
                        this.indirectWorks.addAll(entities);
                    }
                }
                this.indirectWorks.sort(Comparator.comparing(indirectWork -> indirectWork.getWorkNumber()));
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 表示を更新する
     *
     * @param isInit 初期化中？
     */
    private void updateViewThread(boolean isInit) {
        blockUI(true);

        try {
            // 選択組織リストを更新する。
            this.organizationList.getItems().clear();
            this.selectedOrganizations.stream().forEach(org -> organizationList.getItems().add(org.getOrganizationName()));

            // 作業日報リストの選択を解除する。
            this.listView.getSelectionModel().clearSelection();

            // 作業日報リストをクリアする。
            this.workReportList.clear();

            // 作業日
            LocalDate startDate = this.startDatePicker.getValue();
            LocalDate endDate = this.endDatePicker.getValue();
            if (startDate.isAfter(endDate)) {
                this.dispStartDate = endDate;
                this.dispEndDate = startDate;

                this.startDatePicker.setValue(this.dispStartDate);
                this.endDatePicker.setValue(this.dispEndDate);
            } else {
                this.dispStartDate = startDate;
                this.dispEndDate = endDate;
            }

            final String fromDate = this.dispStartDate.format(notDivideDateFormatter);
            final String toDate = this.dispEndDate.format(notDivideDateFormatter);

            final List<Long> organizationIds = new ArrayList<>();
            this.selectedOrganizations.forEach(org -> organizationIds.add(org.getOrganizationId()));

            Task task = new Task<List<WorkReportInfoEntity>>() {
                @Override
                protected List<WorkReportInfoEntity> call() throws Exception {
                    if (isInit) {
                        // キャッシュする情報を取得する。
                        CacheUtils.createCacheOrganization(true);
                        // 間接作業情報を取得する。
                        readIndirectWorks();
                    }

                    // 作業実績を取得する。
                    List<WorkReportInfoEntity> workReports;
                    if (WorkReportConfig.WORK_REPORT_TYPE_KANBAN.equals(WORK_REPORT_TYPE)) {
                        // カンバン名
                        workReports = workReportInfoFacade.findFromToDateKanban(fromDate, toDate, organizationIds);
                    } else if (WorkReportConfig.WORK_REPORT_TYPE_PRODUCTION.equals(WORK_REPORT_TYPE)) {
                        // 製造番号
                        workReports = workReportInfoFacade.findFromToDateProduction(fromDate, toDate, organizationIds);
                    } else {
                        // 注文番号
                        workReports = workReportInfoFacade.findFromToDate(fromDate, toDate, organizationIds);
                    }

                    return workReports;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // リストを更新する。
                        List<WorkReportInfoEntity> workReports = this.getValue();
                        for (WorkReportInfoEntity workReport : workReports) {
                            workReportList.add(new WorkReportRowInfo(workReport, WORK_REPORT_TIME_FORMAT, WORK_REPORT_TIME_UNIT));
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
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
     * 指定IDの組織、およびその子孫のIDのリストを再帰的に取得する
     *
     * @param orgId
     * @return
     */
    private List<Long> getDescendantOrgIdList(Long orgId) {
        final List<Long> orgIds = new ArrayList<>();

        // 自己のIDを登録
        orgIds.add(orgId);

        // 子のIDを取得・登録
        long childCnt = organizationInfoFacade.getAffilationHierarchyCount(orgId);
        for (long i = 0; i < childCnt; i += REST_RANGE_NUM) {
            List<OrganizationInfoEntity> entitys
                    = organizationInfoFacade.getAffilationHierarchyRange(orgId, i, i + REST_RANGE_NUM - 1);
            entitys.forEach((entity) -> {
                orgIds.addAll(getDescendantOrgIdList(entity.getOrganizationId()));
            });
        }

        return orgIds;
    }

    /**
     * 月日の文字列から日付を取得する。
     *
     * @param value
     * @return
     */
    private LocalDate getDateFromMonthDay(String value) {
        LocalDate date = null;
        try {
            if (Objects.nonNull(value)) {
                if (value.length() == formatStringDate.length()) {
                    date = LocalDate.parse(value, dateFormatter);
                } else if (value.length() == formatStringMonthDay.length()) {
                    date = LocalDate.parse(value + String.format("%04d", this.dispStartDate.getYear()), monthDayCheckFormatter);
                }
            }
        } catch (Exception ex) {
        }
        return date;
    }

    /**
     * 間接工数実績を変更したか？
     *
     * @return
     */
    private boolean isChanged() {
        return this.workReportList.stream().filter(p -> p.getIsNew() || p.getIsEdit()).count() > 0 || !this.deleteIdList.isEmpty();
    }

    /**
     * コンポーネントが破棄される前に行う処理
     *
     */
    @Override
    public boolean destoryComponent() {
        try {
            logger.info("destoryComponent start.");

            this.saveProperties();
            
            if (isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    return save();
                } else if (ButtonType.CANCEL == buttonType) {
                    return false;
                } else if (ButtonType.NO == buttonType) {
                    // 何もしない
                }
            }

            return true;
        } finally {
            logger.info("destoryComponent end.");
        }
    }

    /**
     * プロパティを読み込む。
     */
    private void loadProperties() {
        try {
            logger.info("loadProperties start.");

            Properties properties = AdProperty.getProperties();

            String value;

            // 対象日
            value = properties.getProperty(WorkReportConfig.WORK_REPORT_START_DATE);
            if (!StringUtils.isEmpty(value)) {
                LocalDate fromDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                this.startDatePicker.setValue(fromDate);
            }

            value = properties.getProperty(WorkReportConfig.WORK_REPORT_END_DATE);
            if (!StringUtils.isEmpty(value)) {
                LocalDate toDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                this.endDatePicker.setValue(toDate);
            }

            // Lite版の工程名抽出用正規表現
            this.pickLiteWorkNameRegex = properties.getProperty(PICK_LITE_WORK_NAME_REGEX_KEY);

            logger.info("Properties: " + properties.toString());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("loadProperties end.");
        }
    }

    /**
     * プロパティを保存する。
     */
    private void saveProperties() {
        try {
            logger.info("seveProperties start.");

            Properties properties = AdProperty.getProperties();

            properties.setProperty(WorkReportConfig.WORK_REPORT_START_DATE, DateTimeFormatter.ofPattern("yyyy/MM/dd").format(this.startDatePicker.getValue()));
            properties.setProperty(WorkReportConfig.WORK_REPORT_END_DATE, DateTimeFormatter.ofPattern("yyyy/MM/dd").format(this.endDatePicker.getValue()));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("seveProperties end.");
        }
    }

    /**
     * 組織選択ダイアログを表示して、選択された作業を行のデータにセットする。
     *
     * @param rowInfo
     */
    private void editOrganization(WorkReportRowInfo rowInfo) {
        blockUI(true);
        try {
            // 行のデータ
            WorkReportInfoEntity workReport = rowInfo.getWorkReport();

            // 現在の作業者
            OrganizationInfoEntity oldValue = this.getOrganization(workReport.getOrganizationIdentify());

            // 削除済みのデータを取り除く。
            List<OrganizationInfoEntity> organizations = ((List<OrganizationInfoEntity>) cache.getItemList(OrganizationInfoEntity.class, new ArrayList<>()))
                    .stream().filter(p -> Objects.isNull(p.getRemoveFlag()) || !p.getRemoveFlag())
                    .collect(Collectors.toList());

            // 作業者選択ダイアログを表示する。
            ChoiceOrganizationEntity choiceOrganization = new ChoiceOrganizationEntity(organizations, oldValue);

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.workers"), "ChoiceOrganizationCompo", choiceOrganization, true);
            if (ret.equals(ButtonType.OK)) {
                // 選択された組織を取得する。
                OrganizationInfoEntity newValue = choiceOrganization.getSelectedItem();

                // 変更があって、他に重複する行がない場合、行のデータを更新する。
                if (Objects.nonNull(newValue) && !newValue.equals(oldValue)) {
                    WorkReportRowInfo searchRow = getWorkReport(workReport.getWorkDate(), newValue.getOrganizationIdentify(), workReport.getClassNumber(), workReport.getWorkNumber(), workReport.getProductionNumber());
                    if (Objects.isNull(searchRow)) {
                        rowInfo.setIsEdit(true);
                        workReport.setOrganizationId(newValue.getOrganizationId());
                        workReport.setOrganizationIdentify(newValue.getOrganizationIdentify());
                        workReport.setOrganizationName(newValue.getOrganizationName());
                    } else {
                        // 指定された作業は既に存在します。
                        DialogBox.warn("key.Warning", "key.IndirectWorkAlreadyExists");
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 間接作業選択ダイアログを表示して、選択された作業を行のデータにセットする。
     *
     * @param rowInfo
     */
    private void editWork(WorkReportRowInfo rowInfo) {
        blockUI(true);
        try {
            // 行のデータ
            WorkReportInfoEntity workReport = rowInfo.getWorkReport();

            // 現在の間接作業
            IndirectWorkInfoEntity oldValue = this.getIndirectWork(workReport.getClassNumber(), workReport.getWorkNumber());

            // 間接作業選択ダイアログを表示する。
            ChoiceIndirectWorkEntity choiceIndirectWork = new ChoiceIndirectWorkEntity(this.indirectWorks, oldValue);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.work"), "ChoiceIndirectWorkCompo", choiceIndirectWork, true);
            if (ret.equals(ButtonType.OK)) {
                // 選択された間接作業を取得する。
                IndirectWorkInfoEntity newValue = choiceIndirectWork.getSelectedItem();

                // 変更があって、他に重複する行がない場合、行のデータを更新する。
                if (Objects.nonNull(newValue) && !newValue.equals(oldValue)) {
                    WorkReportRowInfo searchRow = getWorkReport(workReport.getWorkDate(), workReport.getOrganizationIdentify(), newValue.getClassNumber(), newValue.getWorkNumber(), workReport.getProductionNumber());
                    if (Objects.isNull(searchRow)) {
                        rowInfo.setIsEdit(true);
                        workReport.setWorkId(newValue.getIndirectWorkId());
                        workReport.setClassNumber(newValue.getClassNumber());
                        workReport.setWorkNumber(newValue.getWorkNumber());
                        workReport.setWorkName(newValue.getWorkName());
                    } else {
                        // 指定された作業は既に存在します。
                        DialogBox.warn("key.Warning", "key.IndirectWorkAlreadyExists");
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 製番選択ダイアログを表示して、選択された製番を行のデータにセットする。
     *
     * @param rowInfo 作業日報の行情報
     */
    private void editControlNo(WorkReportRowInfo rowInfo) {
        blockUI(true);
        try {
            // 選択対象の製番リスト
            List<String> controlNos = rowInfo.getInitialControlNos();
            if (controlNos.size() <= 1) {
                // リスト件数が1件以下の場合は編集不可
                return;
            }

            // 選択中の製番リスト
            List<String> oldValue = rowInfo.getSelectedControlNos();

            // 製番選択ダイアログを表示する。
            ChoiceControlNoEntity choiceControlNo = new ChoiceControlNoEntity(controlNos, oldValue);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.ProductionNumber"), "ChoiceControlNoCompo", choiceControlNo);
            if (ret.equals(ButtonType.OK)) {
                // 選択された製番を取得する。
                List<String> newValue = choiceControlNo.getSelectedItems();

                // 変更があって、他に重複する行がない場合、行のデータを更新する。
                if (Objects.nonNull(newValue) && !newValue.equals(oldValue)) {
                    if (newValue.isEmpty()) {
                        // 全ての製番を未選択は許可しない
                        return;
                    }

                    rowInfo.setIsEdit(true);
                    rowInfo.setSelectedControlNos(newValue);
                    rowInfo.setWorkNum(rowInfo.getWorkNum());
                    rowInfo.setControlNo(rowInfo.getControlNo());
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     *
     * @param organizationIdentify
     * @return
     */
    private OrganizationInfoEntity getOrganization(String organizationIdentify) {
        Optional<OrganizationInfoEntity> item = ((List<OrganizationInfoEntity>) cache.getItemList(OrganizationInfoEntity.class, new ArrayList())).stream()
                .filter(p -> p.getOrganizationIdentify().equals(organizationIdentify))
                .findFirst();
        if (item.isPresent()) {
            return item.get();
        } else {
            return null;
        }
    }

    /**
     *
     * @param classNumber
     * @param workNumber
     * @return
     */
    private IndirectWorkInfoEntity getIndirectWork(String classNumber, String workNumber) {
        Optional<IndirectWorkInfoEntity> item = this.indirectWorks.stream().filter(p -> p.getClassNumber().equals(classNumber) && p.getWorkNumber().equals(workNumber)).findFirst();
        if (item.isPresent()) {
            return item.get();
        } else {
            return null;
        }
    }

    /**
     *
     * @param workDate
     * @param organizationIdentify
     * @param classNumber
     * @param workNumber
     * @param productionNumber 製造番号・注文番号
     * @return
     */
    private WorkReportRowInfo getWorkReport(String workDate, String organizationIdentify, String classNumber, String workNumber, String productionNumber) {
        if (Objects.isNull(workDate) || Objects.isNull(organizationIdentify) || Objects.isNull(classNumber) || Objects.isNull(workNumber)) {
            return null;
        }

        return this.workReportList.stream()
                .filter(p -> p.getWorkReport().getWorkType().equals(WorkReportWorkTypeEnum.INDIRECT_WORK.getValue())
                && classNumber.equals(p.getWorkReport().getClassNumber())
                && workNumber.equals(p.getWorkReport().getWorkNumber())
                && organizationIdentify.equals(p.getWorkReport().getOrganizationIdentify())
                && workDate.equals(p.getWorkReport().getWorkDate())
                && (productionNumber.isEmpty()
                ? p.getWorkReport().getProductionNumber().isEmpty()
                : productionNumber.equals(p.getWorkReport().getProductionNumber())))
                .findFirst()
                .orElse(null);
    }

    /**
     * データ保存
     *
     * @return
     */
    private boolean save() {
        boolean result = false;
        try {
            SimpleDateFormat workDateFormatter = new SimpleDateFormat(WORK_DATE_FORMAT);

            // 削除リストの間接作業実績を削除する。
            List<Long> deleteSuccessIds = new ArrayList();
            for (Long id : this.deleteIdList) {
                IndirectActualInfoEntity actual = new IndirectActualInfoEntity();
                actual.setIndirectActualId(id);
                ResponseEntity res = indirectActualInfoFacade.delete(actual);
                if (res.isSuccess()) {
                    deleteSuccessIds.add(id);
                } else {
                    // 削除失敗
                }
            }
            this.deleteIdList.removeAll(deleteSuccessIds);

            // 更新対象の間接作業実績を更新する。
            boolean isDirectWorkEdited = false;
            for (WorkReportRowInfo row : this.workReportList) {
                if (!row.getIsNew() && row.getIsEdit()) {
                    WorkReportInfoEntity workReport = row.getWorkReport();
                    Date workDate = workDateFormatter.parse(row.getWorkDate().format(localdateFormatter));

                    if (workReport.getWorkType() == WorkReportWorkTypeEnum.INDIRECT_WORK.getValue()) {
                        // 間接作業は1件ずつ更新
                        IndirectActualInfoEntity actual = new IndirectActualInfoEntity();
                        actual.setIndirectActualId(workReport.getIndirectActualId());
                        actual.setFkIndirectWorkId(workReport.getWorkId());
                        actual.setImplementDatetime(workDate);
                        actual.setTransactionId(this.getTransactionId());
                        actual.setFkOrganizationId(workReport.getOrganizationId());
                        actual.setWorkTime(workReport.getWorkTime());
                        actual.setProductionNum(workReport.getProductionNumber());

                        ResponseEntity res = indirectActualInfoFacade.update(actual);
                        if (res.isSuccess()) {
                            row.setIsNew(false);
                            row.setIsEdit(false);
                        } else {
                            // 更新失敗
                        }
                    } else {
                        // 直接作業は一括更新のため、更新要否のみ保存
                        isDirectWorkEdited = true;
                    }
                }
            }

            // 新規追加した間接作業実績を登録する。
            for (WorkReportRowInfo row : this.workReportList) {
                if (row.getIsNew()) {
                    WorkReportInfoEntity workReport = row.getWorkReport();
                    Date workDate = workDateFormatter.parse(row.getWorkDate().format(localdateFormatter));

                    IndirectActualInfoEntity actual = new IndirectActualInfoEntity();
                    actual.setFkIndirectWorkId(workReport.getWorkId());
                    actual.setImplementDatetime(workDate);
                    actual.setTransactionId(this.getTransactionId());
                    actual.setFkOrganizationId(workReport.getOrganizationId());
                    actual.setWorkTime(workReport.getWorkTime());
                    actual.setProductionNum(workReport.getProductionNumber());

                    ResponseEntity res = indirectActualInfoFacade.regist(actual);
                    if (res.isSuccess()) {
                        row.setIsNew(false);
                        row.setIsEdit(false);
                    } else {
                        // 追加失敗
                    }
                }
            }

            // 直接作業実績を更新する。
            if (isDirectWorkEdited) {
                // 直接作業の一覧を取得(未編集行も含む)
                List<WorkReportRowInfo> directReportRows = this.workReportList.stream()
                        .filter(row -> WorkReportWorkTypeEnum.DIRECT_WORK.getValue() == row.getWorkReport().getWorkType()
                        || WorkReportWorkTypeEnum.REWORK.getValue() == row.getWorkReport().getWorkType())
                        .collect(Collectors.toList());

                // 作業日報情報に編集内容を適用する
                List<WorkReportInfoEntity> directReportInfos = directReportRows.stream()
                            .map(row -> row.apply(WORK_REPORT_WORK_NUM_VISIBLE)).collect(Collectors.toList());
                
                ResponseEntity res;
                switch (WORK_REPORT_TYPE) {
                    case WorkReportConfig.WORK_REPORT_TYPE_KANBAN:
                        // カンバン名
                        res = directActualInfoFacade.updateKanbanActual(directReportInfos);
                        break;
                    case WorkReportConfig.WORK_REPORT_TYPE_PRODUCTION:
                        // 製造番号
                        res = directActualInfoFacade.updateProductionActual(directReportInfos);
                        break;
                    default:
                        // 注文番号
                        res = directActualInfoFacade.updateOrderActual(directReportInfos);
                        break;
                }

                if (res.isSuccess()) {
                    // 一括更新成功
                    directReportRows.stream()
                            .filter(row -> row.getIsEdit())
                            .forEach(row -> row.setIsEdit(false));
                } else {
                    // 一括更新失敗
                    logger.fatal("Update direct work failed. ErrorType:{}", res.getErrorType());
                }
            }

            result = true;
        } catch (Exception ex) {
            logger.fatal(ex);
        }
        return result;
    }

    /**
     * CSVファイルを出力する。
     *
     * @param header ヘッダー文字列
     * @param workReportRows 出力データ
     * @param path 出力ファイルパス
     */
    private void outputFile(String header, List<WorkReportRowInfo> workReportRows, File file) {
        if (WorkReportConfig.getWorkReportCsvType().equals("hamai")) {
            this.outputForHamai(workReportRows, file);
            return;
        }

        if (WorkReportConfig.getWorkReportCsvType().equals("mjc")) {
            this.outputForMjc(header, workReportRows, file);
            return;
        }

        this.output(header, workReportRows, file);
    }

    /**
     *
     * @param property
     * @param length
     * @return
     */
    private boolean isValidProperty(StringProperty property, int length) {
        return !StringUtils.isEmpty(property.get()) && property.get().length() >= length;
    }

    /**
     * CSVファイルを出力する。(浜井産業様向け)
     *
     * @param workReportRows 出力データ
     * @param path 出力ファイルパス
     */
    private void outputForHamai(List<WorkReportRowInfo> workReportRows, File file) {
        logger.info("outputForHamai start.");
        try {
            final boolean isCsv = FilenameUtils.getExtension(file.getPath()).equals("csv");
            final Character sp = isCsv ? ',' : '\t';
            final String header = "作業計上日,作業日,事業部コード,部門コード,担当者コード,社内指示番号,社内指示行番号,製番,リスト番号,作業区分,作業内容コード,作業時間,作業理由コード,作業原価科目コード,資源区分,資源コード,資源時間,資源理由コード,資源原価科目コード,備考";
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            final String PLANT_CODE = "100";
            final String KEY_DEPARTMENT_CODE = "部門コード";
            //final String KEY_JOB_CLASS = "作業区分";
            final String KEY_COST_ACCOUNT = "作業原価科目コード";
            //final String KEY_RESOURCE_CLASS = "資源区分";

            // キャッシュ
            cache.setNewCashList(WorkflowInfoEntity.class);

            StringBuilder sb = new StringBuilder();
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), FILE_CHAR))) {
                // ヘッダー
                String column[] = header.split(",");

                if (isCsv) {
                    sb.append(header);
                } else {
                    sb.append(header.replaceAll(",", "\t"));
                }
                sb.append(CSV_LF);

                // データ
                for (WorkReportRowInfo row : workReportRows) {
                    // 作業計上日、作業日
                    String workDate;
                    if (Objects.nonNull(row.getWorkDate())) {
                        workDate = formatter.format(row.getWorkDate());
                    } else {
                        workDate = "";
                    }

                    // 社員コード
                    String organizationIdentify = row.getWorkReport().getOrganizationIdentify();
                    if (Objects.isNull(organizationIdentify)) {
                        organizationIdentify = "";
                    }

                    String workType = "";
                    String oderNo = "";
                    String lineNo = "";
                    String productionNumber = "";
                    String listNo = "";
                    String workNumber = "";
                    String workTime = "";
                    String reasonCode = "";
                    String resourceType = "";
                    String resourceTime = "";
                    String workNo = "";
                    String costItem = "";

                    int time = Objects.nonNull(row.getWorkReport().getWorkTime())? row.getWorkReport().getWorkTime() / 60000 : 0;

                    if (WorkReportWorkTypeEnum.INDIRECT_WORK.getValue() == row.getWorkType()) {
                        // 間接作業
                        workNumber = "4" + row.getWorkReport().getWorkNumber();
                        workTime = String.valueOf(time);

                    } else if (Objects.nonNull(row.getWorkReportWorkNum())) {
                        
                        workType = row.getWorkReportWorkNum().getResourceType();
                        listNo = !StringUtils.isEmpty(row.getWorkReportWorkNum().getListNo()) ? row.getWorkReportWorkNum().getListNo() : "";

                        if ("2".equals(workType)) {
                            // 加工
                            productionNumber = row.getWorkReport().getProductionNumber();

                            String[] values = row.getWorkReport().getKanbanName().split("\\+");
                            if (2 == values.length) {
                                oderNo = values[0];
                                lineNo = values[1];
                            } else {
                                oderNo = row.getWorkReport().getKanbanName();
                            }

                            resourceType = "1";
                            resourceTime = String.valueOf(time);                // 資源時間
                            workNo = row.getWorkReportWorkNum().getWorkNo();    // 資源理由コード
                            costItem = "311";                                   // 資源原価科目コード

                        } else  {
                            // 組立
                            productionNumber = row.getWorkReport().getKanbanName(); // 製番
                            int index = productionNumber.indexOf("[");
                            if (index > 0) {
                                productionNumber = productionNumber.substring(0, index).trim();
                            }
                            
                            workTime = String.valueOf(time);                    // 作業時間

                            if (this.isValidProperty(row.remarks1Property(), 2)
                                    && this.isValidProperty(row.remarks2Property(), 2)) {
                                // 赤作業
                                workNumber = "2" + row.remarks1Property().get().substring(0, 2);
                                reasonCode = row.remarks2Property().get().substring(0, 2);
                            } else {
                                WorkflowInfoEntity workflow = (WorkflowInfoEntity) cache.getItem(WorkflowInfoEntity.class, row.getWorkReport().getWorkflowId());
                                if (Objects.isNull(workflow)) {
                                    workflow = workflowInfoFaced.find(row.getWorkReport().getWorkflowId());
                                    cache.setItem(WorkflowInfoEntity.class, workflow.getWorkflowId(), workflow);
                                }

                                workNumber = Objects.isNull(workflow) ? "" : StringUtils.isEmpty(workflow.getWorkflowNumber()) ? "101" : workflow.getWorkflowNumber();
                                if (workNumber.equals("101") && row.getWorkReport().getWorkName().length() >= 2) {
                                    // 工程名から作業理由コードを抽出
                                    reasonCode = row.getWorkReport().getWorkName().substring(0, 2);
                                } else {
                                    reasonCode = "00";
                                }
                            }

                            resourceType = "2";
                        }
                    }

                    sb.append(workDate);            // 作業計上日
                    sb.append(sp);
                    sb.append(workDate);            // 作業日
                    sb.append(sp);
                    sb.append(PLANT_CODE);          // 事業部コード                    
                    sb.append(sp);
                    sb.append(findOraganizationPropertyValue(row.getWorkReport().getOrganizationId(), KEY_DEPARTMENT_CODE).orElse(""));  // 部署コード
                    sb.append(sp);
                    sb.append(organizationIdentify);// 担当者コード
                    sb.append(sp);
                    sb.append(oderNo);              // 社内指示番号
                    sb.append(sp);
                    sb.append(lineNo);              // 社内指示行番号
                    sb.append(sp);
                    sb.append(productionNumber);    // 製番
                    sb.append(sp);
                    sb.append(listNo);              // リスト番号
                    sb.append(sp);
                    sb.append(workType);                                        // 作業区分
                    sb.append(sp);
                    sb.append(workNumber);                                      // 作業内容コード
                    sb.append(sp);
                    sb.append(workTime);                                        // 作業時間
                    sb.append(sp);
                    sb.append(reasonCode);                                      // 作業理由コード
                    sb.append(sp);
                    sb.append(findOraganizationPropertyValue(row.getWorkReport().getOrganizationId(), KEY_COST_ACCOUNT).orElse("")); // 作業原価科目コード
                    sb.append(sp);
                    sb.append(resourceType);                                    // 資源区分
                    sb.append(sp);
                    sb.append(row.resourcesProperty().get());                   // 資源コード
                    sb.append(sp);
                    sb.append(resourceTime);                                    // 資源時間
                    sb.append(sp);
                    sb.append(workNo);                                          // 資源理由コード
                    sb.append(sp);
                    sb.append(costItem);                                        // 資源原価科目コード
                    sb.append(sp);                                              

                    sb.append(CSV_LF);
                }

                writer.append(sb.toString());
                writer.flush();
            }

            Platform.runLater(() -> {
                sc.showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.WorkReportTitle"), String.format(LocaleUtils.getString("key.OutputSuccess"), LocaleUtils.getString("key.WorkReportTitle")));
            });

        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * CSVファイルを出力する。
     *
     * @param header ヘッダー文字列
     * @param workReportRows 出力データ
     * @param path 出力ファイルパス
     */
    private void outputForMjc(String header, List<WorkReportRowInfo> workReportRows, File file) {
        try {
            final Character sp = ',';
            List<String> timeFormat = Arrays.asList(WORK_REPORT_TIME_FORMAT.split(":"));

            List<WorkReportCsv> list = new ArrayList<>();

            for (WorkReportRowInfo row : workReportRows) {

                if (Objects.isNull(row.getSerialNumbers())) {
                    list.add(new WorkReportCsv(row, null));
                    continue;
                }

                for (String serialNumber : row.getSerialNumbers()) {
                    WorkReportCsv entity = new WorkReportCsv(row, serialNumber);
                    list.add(entity);
                }
            }

            StringBuilder sb = new StringBuilder();
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), FILE_CHAR))) {

                sb.append(header);
                sb.append(CSV_LF);

                for (WorkReportCsv csv : list) {

                    // 日付
                    String workDate;
                    if (Objects.nonNull(csv.getWorkDate())) {
                        workDate = dateFormatter.format(csv.getWorkDate());
                    } else {
                        workDate = "";
                    }

                    // 氏名
                    String organizationName = csv.getWorkReport().getOrganizationName();
                    if (Objects.isNull(organizationName)) {
                        organizationName = "";
                    }

                    // 職番
                    String organizationIdentify = csv.getWorkReport().getOrganizationIdentify();
                    if (Objects.isNull(organizationIdentify)) {
                        organizationIdentify = "";
                    }

                    // 部署コード
                    String departmentCode = findOraganizationPropertyValue(csv.getWorkReport().getOrganizationId(), PROP_NAME_DEPARTMENT_CODE).orElse("");

                    // 作業No
                    String workNumber = csv.getWorkReport().getWorkNumber();
                    if (Objects.isNull(workNumber)) {
                        workNumber = "";
                    }

                    // 工数
                    String workTimeMin = csv.getWorkTime();
                    if (Objects.isNull(workTimeMin)) {
                        workTimeMin = "";
                    }

                    sb.append(workDate);// 日付
                    sb.append(sp);
                    sb.append(organizationName);// 氏名
                    sb.append(sp);
                    sb.append(organizationIdentify);// 職番
                    sb.append(sp);
                    sb.append(departmentCode);// 部署コード
                    sb.append(sp);
                    sb.append(workNumber);// 作業No
                    sb.append(sp);

                    if (csv.getWorkReport().getWorkType() == WorkReportWorkTypeEnum.DIRECT_WORK.getValue()) {
                        sb.append(StringUtils.isEmpty(csv.getSerialNumber()) ? "" : csv.getUnitTime()); // 工数/台
                        sb.append(sp);
                        sb.append(csv.getWorkNum());
                        sb.append(sp);
                        //sb.append(csv.getWorkReport().getKanbanName());// カンバン名
                        sb.append(StringUtils.isEmpty(csv.getWorkReport().getProductionNumber()) ? "" : csv.getWorkReport().getProductionNumber());// 製造番号
                        sb.append(sp);
                        sb.append(StringUtils.isEmpty(csv.getSerialNumber()) ? "" : csv.getSerialNumber());
                        sb.append(sp);
                    } else {
                        sb.append(workTimeMin);// 工数
                        sb.append(sp);
                        sb.append(sp);
                        sb.append(sp);
                        sb.append(sp);
                    }

                    sb.append(CSV_LF);
                }

                writer.append(sb.toString());
                writer.flush();
            }

            Platform.runLater(() -> {
                sc.showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.WorkReportTitle"), String.format(LocaleUtils.getString("key.OutputSuccess"), LocaleUtils.getString("key.WorkReportTitle")));
            });

        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * CSVファイルを出力する。
     *
     * @param header ヘッダー文字列
     * @param workReportRows 出力データ
     * @param path 出力ファイルパス
     */
    private void output(String header, List<WorkReportRowInfo> workReportRows, File file) {
        try {
            boolean isCsv = FilenameUtils.getExtension(file.getPath()).equals("csv");
            Character sp = isCsv ? ',' : '\t';
            List<String> timeFormat = Arrays.asList(WORK_REPORT_TIME_FORMAT.split(":"));
            boolean isWorkNumVisible = WORK_REPORT_WORK_NUM_VISIBLE && WorkReportConfig.WORK_REPORT_TYPE_KANBAN.equals(WORK_REPORT_TYPE);

            // 作業数情報の表示がある場合は、製造番号単位でファイル出力する
            List<WorkReportRowInfo> outputRows;
            if (isWorkNumVisible) {
                outputRows = getRowsByProductionNumber(workReportRows);
            } else {
                outputRows = workReportRows;
            }

            StringBuilder sb = new StringBuilder();
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), FILE_CHAR))) {
                // ヘッダー
                if (timeFormat.contains(SYS_WORK_REPORT_TIME_FORMAT_HOUR) || !timeFormat.contains(SYS_WORK_REPORT_TIME_FORMAT_MIN)) {
                    header = header.replace("工数M", "工数");
                }

                String column[] = header.split(",");

                if (isCsv) {
                    sb.append(header);
                } else {
                    sb.append(header.replaceAll(",", "\t"));
                }
                sb.append(CSV_LF);

                // データ
                for (WorkReportRowInfo row : outputRows) {

                    // 日付
                    String workDate;
                    if (Objects.nonNull(row.getWorkDate())) {
                        workDate = dateFormatter.format(row.getWorkDate());
                    } else {
                        workDate = "";
                    }
                    // 氏名
                    String organizationName = row.getWorkReport().getOrganizationName();
                    if (Objects.isNull(organizationName)) {
                        organizationName = "";
                    }
                    // 職番
                    String organizationIdentify = row.getWorkReport().getOrganizationIdentify();
                    if (Objects.isNull(organizationIdentify)) {
                        organizationIdentify = "";
                    }
                    // 部署コード
                    String departmentCode = findOraganizationPropertyValue(row.getWorkReport().getOrganizationId(), PROP_NAME_DEPARTMENT_CODE).orElse("");
                    // 作業No
                    String workNumber = row.getWorkReport().getWorkNumber();
                    if (Objects.isNull(workNumber)) {
                        workNumber = "";
                    }

                    // 工数
                    String workTimeMin = row.getWorkTime();
                    if (Objects.isNull(workTimeMin)) {
                        workTimeMin = "";
                    }

                    // 注番 or カンバン or 製造番号
                    String orderNumber;
                    if (WorkReportConfig.WORK_REPORT_TYPE_KANBAN.equals(WORK_REPORT_TYPE)) {
                        orderNumber = row.getWorkReport().getKanbanName();// カンバン名
                    } else if (WorkReportConfig.WORK_REPORT_TYPE_PRODUCTION.equals(WORK_REPORT_TYPE)) {
                        orderNumber = row.getWorkReport().getProductionNumber();// 製造番号
                    } else {
                        orderNumber = row.getOrderNumProp();// 注番
                    }
                    // nullだった場合、空文字列に置き換える
                    orderNumber = Objects.isNull(orderNumber) ? "" : orderNumber;

                    sb.append(workDate);// 日付
                    sb.append(sp);
                    sb.append(organizationName);// 氏名
                    sb.append(sp);
                    sb.append(organizationIdentify);// 職番
                    sb.append(sp);
                    sb.append(departmentCode);// 部署コード
                    sb.append(sp);
                    sb.append(workNumber);// 作業No
                    sb.append(sp);

                    // 工数
                    sb.append(workTimeMin);
                    sb.append(sp);

                    sb.append(orderNumber);// 注番 or カンバン名 or 製造番号
                    sb.append(sp);

                    // 中断理由
                    String interruptReason = "";
                    if (row.getWorkType() == WorkReportWorkTypeEnum.NON_WORK_TIME.getValue()) {
                        interruptReason = row.getWorkReport().getWorkName();
                    }
                    sb.append(interruptReason);
                    sb.append(sp);

                    if (isWorkNumVisible) {
                        // 機種
                        String modelName = !StringUtils.isEmpty(row.getWorkReport().getModelName()) ? row.getWorkReport().getModelName() : "";
                        sb.append(modelName);
                        sb.append(sp);

                        // 工程名
                        String workName = !StringUtils.isEmpty(row.getWorkReport().getWorkName()) ? row.getWorkReport().getWorkName() : "";
                        sb.append(workName);
                        sb.append(sp);

                        // 作業数
                        String workNum = Objects.nonNull(row.getWorkNum()) ? String.valueOf(row.getWorkNum()) : "";
                        sb.append(workNum);
                        sb.append(sp);

                        // 製造番号
                        String controlNo = !StringUtils.isEmpty(row.getControlNo()) ? row.getControlNo() : "";
                        sb.append(controlNo);
                        sb.append(sp);

                        // 備考1
                        sb.append(!StringUtils.isEmpty(row.remarks1Property().get()) ? row.remarks1Property().get() : "");
                        sb.append(sp);

                        // 備考2
                        sb.append(!StringUtils.isEmpty(row.remarks2Property().get()) ? row.remarks2Property().get() : "");
                        sb.append(sp);
                    }

                    sb.append(CSV_LF);
                }

                writer.append(sb.toString());
                writer.flush();
            }

            Platform.runLater(() -> {
                sc.showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.WorkReportTitle"), String.format(LocaleUtils.getString("key.OutputSuccess"), LocaleUtils.getString("key.WorkReportTitle")));
            });

        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 製造番号単位の工数データを取得する。
     *
     * @param rows 作業日報の行データ
     * @return 作業日報の行データ(製造番号単位に分割)
     */
    private List<WorkReportRowInfo> getRowsByProductionNumber(List<WorkReportRowInfo> rows) {
        List<WorkReportRowInfo> newRows = new LinkedList<>();

        for (WorkReportRowInfo row : rows) {
            int workNum = row.getSelectedControlNos().size();
            if (workNum <= 1) {
                newRows.add(row);
            } else {
                int workTime = row.getWorkReport().getWorkTime() / workNum;
                for (String controlNo : row.getSelectedControlNos()) {
                    WorkReportInfoEntity workReport = new WorkReportInfoEntity();
                    WorkReportRowInfo entity = new WorkReportRowInfo(workReport, WORK_REPORT_TIME_FORMAT, WORK_REPORT_TIME_UNIT);
                    entity.setWorkDate(LocalDate.parse(row.getWorkReport().getWorkDate(), DateTimeFormatter.ofPattern(WORK_DATE_FORMAT)));
                    entity.setControlNo(controlNo);
                    entity.setWorkNum(1);
                    entity.setSelectedControlNos(Arrays.asList(controlNo));
                    entity.remarks1Property().set(row.remarks1Property().get());
                    entity.remarks2Property().set(row.remarks2Property().get());

                    workReport.setOrganizationName(row.getWorkReport().getOrganizationName());
                    workReport.setOrganizationIdentify(row.getWorkReport().getOrganizationIdentify());
                    workReport.setOrganizationId(row.getWorkReport().getOrganizationId());
                    workReport.setWorkNumber(row.getWorkReport().getWorkNumber());
                    workReport.setKanbanName(row.getWorkReport().getKanbanName());
                    workReport.setProductionNumber(row.getWorkReport().getProductionNumber());
                    workReport.setOrderNumber(row.getWorkReport().getOrderNumber());
                    workReport.setWorkName(row.getWorkReport().getWorkName());
                    workReport.setModelName(row.getWorkReport().getModelName());
                    workReport.setWorkTime(workTime);
                    entity.setWorkReport(workReport);

                    newRows.add(entity);
                }
            }
        }

        return newRows;
    }

    /**
     * 未入力チェック
     *
     * @return ture:未入力・重複なし, false:未入力・重複あり
     */
    private boolean checkData() {
        for (WorkReportRowInfo row : this.workReportList) {
            // 間接工数以外はチェック不要。
            if (row.getWorkType() != WorkReportWorkTypeEnum.INDIRECT_WORK.getValue()) {
                continue;
            }

            // 未入力チェック
            WorkReportInfoEntity workReport = row.getWorkReport();
            if (Objects.isNull(workReport.getWorkDate())) {
                return false;
            }
            if (Objects.isNull(workReport.getOrganizationId())) {
                return false;
            }
            if (Objects.isNull(workReport.getWorkId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 実績登録時に指定するTransactionIdを取得する。
     *
     * @return TransactionId
     */
    private Long getTransactionId() {
        if (transactionId < Long.MAX_VALUE) {
            transactionId++;
        } else {
            transactionId = 0L;
        }
        return transactionId;
    }

    /**
     * 操作禁止にする。
     *
     * @param block
     */
    private void blockUI(boolean block) {
        Platform.runLater(() -> {
            sc.blockUI(block);
            progressPane.setVisible(block);
        });
    }

    /**
     * 組織の追加情報の項目名からその値を取得する。
     *
     * @param organzationId 組織ID
     * @param propName 追加情報の項目名
     * @return 組織が存在し、その組織の追加情報が存在するならpropNameに対応する値を返す
     */
    private Optional<String> findOraganizationPropertyValue(Long organzationId, String propName) {
        OrganizationInfoEntity org = (OrganizationInfoEntity) cache.getItem(OrganizationInfoEntity.class, organzationId);

        if (Objects.isNull(org)) {
            return Optional.empty();
        }

        return org.getPropertyInfoCollection().stream()
                .filter(prop -> StringUtils.equals(prop.getOrganizationPropName(), propName))
                .map(OrganizationPropertyInfoEntity::getOrganizationPropValue)
                .findFirst();
    }
}
