/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import jp.adtekfuji.addatabase.common.AdDatabaseConfig;
import jp.adtekfuji.addatabase.utils.LocaleUtils;
import jp.adtekfuji.addatabaseapp.controller.PostgresManager;
import jp.adtekfuji.addatabaseapp.entity.AddInfoEntity;
import jp.adtekfuji.addatabaseapp.utils.CMDContents;
import jp.adtekfuji.addatabaseapp.utils.JsonUtils;
import jp.adtekfuji.addatabaseapp.utils.ServiceUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 実績出力・削除ダイアログ
 *
 * @author nar-nakamura
 */
public class DeleteActualFxController implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    private final static Character COMMA = ',';
    private final static Character TAB = '\t';

    @FXML
    private Pane progressPane;
    @FXML
    private TableView<MonthlyActualInfo> tableView;
    @FXML
    private TableColumn selectedColumn;
    @FXML
    private TableColumn<MonthlyActualInfo, String> yearMonthColumn;
    @FXML
    private CheckBox deleteCheckBox;

    private Dialog dialog;
    private String backupDir;

    private final ObservableList<MonthlyActualInfo> rows = FXCollections.observableArrayList();

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);

        tableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        this.selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        this.selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        this.yearMonthColumn.setCellValueFactory((TableColumn.CellDataFeatures<MonthlyActualInfo, String> param) -> param.getValue().yearMonthProperty());

        this.rows.clear();
        this.tableView.setItems(this.rows);
    }

    /**
     *
     * @param backupDir
     */
    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
        this.dispMonthlyActualList();
    }

    /**
     *
     * @param dialog
     */
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.closeDialog();
        });
    }

    /**
     * UIをロックする。
     *
     * @param flg (true:ロック, false:ロック解除)
     */
    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            this.dialog.getDialogPane().setDisable(flg);
            this.progressPane.setVisible(flg);
        });
    }

    /**
     * 実行ボタン
     *
     * @param event
     */
    @FXML
    private void onExecuteButton(ActionEvent event) {
        try {
            blockUI(true);

            // 削除を選択している場合は警告ダイアログで確認する。
            boolean isDelete = deleteCheckBox.isSelected();
            if (isDelete) {
                ButtonType btn = showAlert(LocaleUtils.getString("key.DeleteActual"), LocaleUtils.getString("key.AcrualReport.Confirmation"), Alert.AlertType.CONFIRMATION);
                if (!ButtonType.OK.equals(btn)) {
                    return;
                }
            }

            // 選択年月リスト
            List<MonthlyActualInfo> infos = new ArrayList();
            for (MonthlyActualInfo item : tableView.getItems()) {
                if (item.getSelected()) {
                    String repotCsvFilename = AdDatabaseConfig.getReportCsvFilename();
                    String baseName;
                    String suffix;
                    int pos = repotCsvFilename.lastIndexOf(".");
                    if (pos >= 0) {
                        baseName = repotCsvFilename.substring(0, pos);
                        suffix = repotCsvFilename.substring(pos + 1);
                    } else {
                        baseName = repotCsvFilename;
                        suffix = "csv";
                    }

                    Character separate = suffix.toUpperCase().equals("TSV") ? TAB : COMMA;// 区切り文字

                    String fileName = String.format("%s_%d%02d.%s", baseName, item.getYear(), item.getMonth(), suffix);
                    File file = new File(backupDir, fileName);
                    if (file.exists()) {
                        String message = String.format("%s\r\r%s", LocaleUtils.getString("key.AcrualReport.Overwrite"), fileName);
                        ButtonType btn = showAlert(LocaleUtils.getString("key.DeleteActual"), message, Alert.AlertType.CONFIRMATION);
                        if (!ButtonType.OK.equals(btn)) {
                            return;
                        }
                    }

                    item.setFilePath(file.getPath());
                    item.setSeparate(separate);

                    infos.add(item);
                }
            }

            // 未選択の場合は何もしない。
            if (infos.isEmpty()) {
                return;
            }

            Task task = new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    blockUI(true);
                    int result = 0;
                    try {
                        for (MonthlyActualInfo info : infos) {
                            // 実績出力
                            boolean outputResult = outputFile(info.getYear(), info.getMonth(), info.getFilePath(), info.getSeparate());
                            if (!outputResult) {
                                result = -1;
                                break;
                            }

                            if (isDelete) {
                                // 実績削除
                                boolean deleteResult = deleteActualResult(info.getYear(), info.getMonth());
                                if (!deleteResult) {
                                    result = -2;
                                    break;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                    return result;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // 処理結果
                        Integer result = this.getValue();

                        Alert.AlertType alertType;
                        String message;
                        switch (result) {
                            case 0:// 成功
                                alertType = Alert.AlertType.INFORMATION;
                                message = LocaleUtils.getString("key.AcrualReport.Success");
                                break;
                            case -1:// 実績出力失敗
                                alertType = Alert.AlertType.ERROR;
                                message = LocaleUtils.getString("key.AcrualReport.OutputFailure");
                                break;
                            case -2:// 実績削除失敗
                                alertType = Alert.AlertType.ERROR;
                                message = LocaleUtils.getString("key.AcrualReport.DeleteFailure");
                                break;
                            default:
                                alertType = Alert.AlertType.ERROR;
                                message = LocaleUtils.getString("key.AcrualReport.Error");
                        }
                        showAlert(LocaleUtils.getString("key.DeleteActual"), message, alertType);

                        if (isDelete) {
                            dispMonthlyActualList();
                        }
                    } finally {
                        blockUI(false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    blockUI(false);
                    showAlert(LocaleUtils.getString("key.DeleteActual"), LocaleUtils.getString("key.AcrualReport.Error"), Alert.AlertType.ERROR);
                }
            };
            new Thread(task).start();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * キャンセルボタン
     *
     * @param event
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
        this.closeDialog();
    }

    /**
     * 月別実績を表示する。
     */
    private void dispMonthlyActualList() {
        try {
            this.rows.clear();

            PostgresManager postgresManager = new PostgresManager();

            // 実績のある年月を抽出する。
            List<String> yearMonthList = postgresManager.getActualYearMonthList();
            if (Objects.isNull(yearMonthList)) {
                return;
            }

            for (String yearMonth : yearMonthList) {
                this.rows.add(new MonthlyActualInfo(yearMonth));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 実績ファイル出力する。
     *
     * @param year 対象年
     * @param month 対象月
     * @param filePath 出力ファイルパス
     * @param separate 区切り文字
     * @return 処理結果 (true: 成功, false:失敗)
     */
    private boolean outputFile(int year, int month, String filePath, Character separate) {
        boolean result = false;
        try {
            PostgresManager postgresManager = new PostgresManager();
            ResourceBundle rb = LocaleUtils.getResourceBundle();

            List<String> actualPropNames = new ArrayList();// カンバンプロパティ名リスト

            // 実績を取得する。
            List<Map<String, String>> actualResults = postgresManager.getMonthlyActualResults(year, month);
            if (Objects.isNull(actualResults) || actualResults.isEmpty()) {
                return true;
            }

            // カンバンプロパティ名リストを作成する。
            for (Map<String, String> actualResult : actualResults) {
                String actualAddInfo = actualResult.get("actual_add_info");// 検査結果
                if (Objects.isNull(actualAddInfo)) {
                    continue;
                }

                List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(actualAddInfo, AddInfoEntity[].class);
                if (Objects.isNull(addInfos)) {
                    continue;
                }
                
                for (AddInfoEntity addInfo : addInfos) {
                    if (actualPropNames.indexOf(addInfo.getKey()) < 0) {
                        actualPropNames.add(addInfo.getKey());
                    }
                }
            }

            // 文字エンコード
            String encode = AdDatabaseConfig.getReportCsvEncode().toUpperCase();
            if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS").contains(encode)) {
                encode = "MS932";
            }

            //CSV出力
            File file = new File(filePath);
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getPath()), encode))) {
                StringBuilder sb = new StringBuilder();

                // ヘッダー
                sb.append(LocaleUtils.getString("key.AcrualReport.KanbanHierarch")).append(separate);// カンバン階層
                sb.append(LocaleUtils.getString("key.AcrualReport.KanbanName")).append(separate);// カンバン名
                sb.append(LocaleUtils.getString("key.AcrualReport.KanbanSubname")).append(separate);// カンバンサブ名
                sb.append(LocaleUtils.getString("key.AcrualReport.OrderProcessesHierarch")).append(separate);// 工程順階層
                sb.append(LocaleUtils.getString("key.AcrualReport.OrderProcessesName")).append(separate);// 工程順名
                sb.append(LocaleUtils.getString("key.AcrualReport.ProcessHierarch")).append(separate);// 工程階層
                sb.append(LocaleUtils.getString("key.AcrualReport.ProcessName")).append(separate);// 工程名
                sb.append(LocaleUtils.getString("key.AcrualReport.AdditionalProcess")).append(separate);// 追加工程
                sb.append(LocaleUtils.getString("key.AcrualReport.OrganizationParentName")).append(separate);//親組織名
                sb.append(LocaleUtils.getString("key.AcrualReport.OrganizationParentIdentName")).append(separate);// 親組織識別名
                sb.append(LocaleUtils.getString("key.AcrualReport.OrganizationName")).append(separate);// 組織名
                sb.append(LocaleUtils.getString("key.AcrualReport.OrganizationsManagementName")).append(separate);// 組織識別名
                sb.append(LocaleUtils.getString("key.AcrualReport.EquipmentParentName")).append(separate);// 親設備名
                sb.append(LocaleUtils.getString("key.AcrualReport.EquipmentsParentIdentName")).append(separate);// 親設備管理名
                sb.append(LocaleUtils.getString("key.AcrualReport.EquipmentName")).append(separate);// 設備名
                sb.append(LocaleUtils.getString("key.AcrualReport.EquipmentsManagementName")).append(separate);// 設備管理名
                sb.append(LocaleUtils.getString("key.AcrualReport.Status")).append(separate);// ステータス
                sb.append(LocaleUtils.getString("key.AcrualReport.EditInterruptReasonTitle")).append(separate);// 中断理由
                sb.append(LocaleUtils.getString("key.AcrualReport.EditDelayReasonTitle")).append(separate);// 遅延理由
                sb.append(LocaleUtils.getString("key.AcrualReport.ImplementTime")).append(separate);// 実施時間
                sb.append(LocaleUtils.getString("key.AcrualReport.TactTime")).append(separate);// タクトタイム
                sb.append(LocaleUtils.getString("key.AcrualReport.WorkTime")).append(separate);// 作業時間

                // プロパティ (ヘッダー)
                for (String propName : actualPropNames) {
                    sb.append(propName).append(separate);
                }

                sb.append("\n");
                writer.write(sb.toString());

                // データ
                for (Map<String, String> actualResult : actualResults) {
                    sb = new StringBuilder();

                    sb.append(actualResult.get("kanban_hierarchy_name")).append(separate);//カンバン階層
                    sb.append(actualResult.get("kanban_name")).append(separate);// カンバン名
                    sb.append(actualResult.get("kanban_subname")).append(separate);// カンバンサブ名
                    sb.append(actualResult.get("workflow_hierarchy_name")).append(separate);// 工程順階層
                    sb.append(actualResult.get("workflow_name")).append(separate);// 工程順名
                    sb.append(actualResult.get("work_hierarchy_name")).append(separate);// 工程階層
                    sb.append(actualResult.get("work_name")).append(separate);// 工程名
                    sb.append(actualResult.get("separate_work_flag")).append(separate);// 追加工程
                    sb.append(actualResult.get("parent_organization_name")).append(separate);//親組織名
                    sb.append(actualResult.get("parent_organization_identify")).append(separate);// 親組織識別名
                    sb.append(actualResult.get("organization_name")).append(separate);// 組織名
                    sb.append(actualResult.get("organization_identify")).append(separate);// 組織識別名
                    sb.append(actualResult.get("parent_equipment_name")).append(separate);// 親設備名
                    sb.append(actualResult.get("parent_equipment_identify")).append(separate);// 親設備識別名
                    sb.append(actualResult.get("equipment_name")).append(separate);// 設備名
                    sb.append(actualResult.get("equipment_identify")).append(separate);// 設備識別名
                    sb.append(actualResult.get("actual_status")).append(separate);// ステータス
                    sb.append(actualResult.get("interrupt_reason")).append(separate);// 中断理由
                    sb.append(actualResult.get("delay_reason")).append(separate);// 遅延理由
                    sb.append(actualResult.get("implement_datetime")).append(separate);// 実施時間
                    sb.append(actualResult.get("takt_time")).append(separate);// タクトタイム
                    sb.append(actualResult.get("work_time")).append(separate);// 作業時間

                    // プロパティ
                    String actualAddInfo = actualResult.get("actual_add_info");// 検査結果
                    if (Objects.nonNull(actualAddInfo)) {
                        List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(actualAddInfo, AddInfoEntity[].class);
                        if (Objects.nonNull(addInfos)) {
                            for (String propName : actualPropNames) {
                                Optional<AddInfoEntity> opt = addInfos.stream()
                                        .filter(p -> Objects.equals(p.getKey(), propName))
                                        .findFirst();
                                if (opt.isPresent()) {
                                    AddInfoEntity prop = opt.get();
                                    sb.append(prop.getVal());
                                }
                                sb.append(separate);
                            }
                        }
                    }

                    sb.append("\n");
                    writer.write(sb.toString());
                }
            }

            result = true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 実績を削除する。
     *
     * @param year 対象年
     * @param month 対象月
     * @return 処理結果 (true: 成功, false:失敗)
     */
    private boolean deleteActualResult(int year, int month) {
        boolean result = false;
        boolean isRunTomEE = false;
        boolean isRunAdInterface = false;
        try {
            PostgresManager postgresManager = new PostgresManager();

            // adFactoryServer が起動していたら終了する。
            isRunTomEE = ServiceUtils.getServiceState(CMDContents.SERVICE_TOMEE);
            if (isRunTomEE) {
                ServiceUtils.setServiceState(CMDContents.SERVICE_TOMEE, false);
            }
            // adInterfaceService が起動していたら終了する。
            isRunAdInterface = ServiceUtils.getServiceState(CMDContents.SERVICE_ADINTERFACE);
            if (isRunAdInterface) {
                ServiceUtils.setServiceState(CMDContents.SERVICE_ADINTERFACE, false);
            }

            // 実績・実績プロパティを削除する。
            result = postgresManager.deleteMonthlyActualResults(year, month);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            // 処理前に adInterfaceService が起動していたら起動する。
            if (isRunAdInterface) {
                ServiceUtils.setServiceState(CMDContents.SERVICE_ADINTERFACE, true);
            }
            // 処理前に adFactoryServer が起動していたら起動する。
            if (isRunTomEE) {
                ServiceUtils.setServiceState(CMDContents.SERVICE_TOMEE, true);
            }
        }
        return result;
    }

    /**
     * メッセージダイアログを表示する。
     *
     * @param title タイトル
     * @param message メッセージ
     * @param type ダイアログ種別
     * @Return ボタン
     */
    private ButtonType showAlert(String title, String message, Alert.AlertType type) {
        ButtonType result = ButtonType.CANCEL;
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            result = alert.getResult();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 終了処理
     */
    private void closeDialog() {
        try {
            this.dialog.getDialogPane().getScene().getWindow().hide();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 月別実績情報
     */
    public class MonthlyActualInfo {

        private String yearMonth;
        private String filePath;
        private Character separate;

        private final BooleanProperty selected = new SimpleBooleanProperty();
        private StringProperty yearMonthProperty;

        public MonthlyActualInfo() {
            this.selected.set(false);
        }

        public MonthlyActualInfo(String yearMonth) {
            this.selected.set(false);
            this.yearMonth = yearMonth;
        }

        public BooleanProperty selectedProperty() {
            return this.selected;
        }

        public Boolean getSelected() {
            return this.selected.get();
        }

        public void setSelected(Boolean value) {
            this.selected.set(value);
        }

        public StringProperty yearMonthProperty() {
            if (Objects.isNull(this.yearMonthProperty)) {
                this.yearMonthProperty = new SimpleStringProperty(yearMonth);
            }
            return this.yearMonthProperty;
        }

        public int getYear() {
            return Integer.valueOf(yearMonth.substring(0, 4));
        }

        public int getMonth() {
            return Integer.valueOf(yearMonth.substring(5, 7));
        }

        public String getFilePath() {
            return this.filePath;
        }

        public void setFilePath(String value) {
            this.filePath = value;
        }

        public Character getSeparate() {
            return this.separate;
        }

        public void setSeparate(Character value) {
            this.separate = value;
        }
    };
}
