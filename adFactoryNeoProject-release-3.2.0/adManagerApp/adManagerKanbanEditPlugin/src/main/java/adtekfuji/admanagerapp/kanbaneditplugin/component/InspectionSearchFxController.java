/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component;

import adtekfuji.admanagerapp.kanbaneditplugin.controls.CheckTextField;
import adtekfuji.clientservice.TraceabilityFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.kanban.TraceabilityEntity;
import jp.adtekfuji.adFactory.entity.search.TraceabilitySearchCondition;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 検査データ検索ダイアログ
 *
 * @author nar-nakamura
 */
@FxComponent(id = "InspectionSearchDialog", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/inspection_search_dialog.fxml")
public class InspectionSearchFxController implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private Dialog dialog;

    private static final String CSV_LIMIT_VALUE_FORMAT = "%5.3f";
    private static final String CSV_OK_STRING = "OK";
    private static final String CSV_NG_STRING = "NG";
    private static final String CSV_DATE_FORMAT = "yyyy/MM/dd";

    @FXML
    private CheckTextField kanbanNameField;
    @FXML
    private CheckTextField modelNameField;

    @FXML
    private TableView kanbanList;
    @FXML
    private TableColumn<TraceabilityEntity, String> kanbanNameColumn;
    @FXML
    private TableColumn<TraceabilityEntity, String> modelNameColumn;
    @FXML
    private TableColumn<TraceabilityEntity, String> workflowNameColumn;

    @FXML
    private Button inspectionDataButton;

    @FXML
    private Pane progressPane;

    /**
     * コンストラクタ
     */
    public InspectionSearchFxController() {
    }

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);

        kanbanList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        // カンバン名
        this.kanbanNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<TraceabilityEntity, String> param) -> Bindings.createStringBinding(() -> param.getValue().getKanbanName()));
        // モデル名
        this.modelNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<TraceabilityEntity, String> param) -> Bindings.createStringBinding(() -> param.getValue().getModelName()));
        // 工程順名
        this.workflowNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<TraceabilityEntity, String> param) -> Bindings.createStringBinding(() -> param.getValue().getWorkflowName()));

        this.inspectionDataButton.setDisable(true);// 検査データボタン 無効

        // カンバン選択時の処理
        this.kanbanList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            List<TraceabilityEntity> items = kanbanList.getSelectionModel().getSelectedItems();
            // 選択時のみ検査データボタンを有効にする。
            if (Objects.isNull(items) || items.isEmpty()) {
                this.inspectionDataButton.setDisable(true);
            } else {
                this.inspectionDataButton.setDisable(false);
            }
        });
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof TraceabilitySearchCondition) {
            TraceabilitySearchCondition condition = (TraceabilitySearchCondition) argument;
            this.kanbanNameField.setText(condition.getKanbanName());
            this.modelNameField.setText(condition.getModelName());
        }
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        if (Objects.nonNull(this.dialog)) {
            this.dialog.getDialogPane().setDisable(flg);
        }
        this.progressPane.setVisible(flg);
    }

    /**
     *
     * @param dialog
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancelDialog();
        });
    }

    /**
     * 検査ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onSearch(ActionEvent event) {
        this.updateView();
    }

    /**
     * 検査データボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onInspectionData(ActionEvent event) {
        this.outputInspection();
    }

    /**
     * キャンセル処理
     */
    private void cancelDialog() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 検索してリストを更新する。
     */
    private void updateView() {
        logger.info("updateView");
        boolean isCancel = true;
        try {
            blockUI(true);

            kanbanList.getItems().clear();

            // 検索条件
            final TraceabilitySearchCondition condition = new TraceabilitySearchCondition();
            if (kanbanNameField.isSelected()) {
                condition.setKanbanName(kanbanNameField.getText());
            }
            if (modelNameField.isSelected()) {
                condition.setModelName(modelNameField.getText());
            }

            isCancel = false;

            Task task = new Task<List<TraceabilityEntity>>() {
                @Override
                protected List<TraceabilityEntity> call() throws Exception {
                    TraceabilityFacade facade = new TraceabilityFacade();
                    return facade.findSearchKanban(condition);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        kanbanList.getItems().addAll(this.getValue());
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
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

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }
    }

    /**
     * 検査データをCSVファイルに出力して、Excelで表示する。
     */
    private void outputInspection() {
        logger.info("outputInspection");
        boolean isCancel = true;
        try {
            blockUI(true);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");// 年時分秒

            TraceabilityEntity trace = (TraceabilityEntity) kanbanList.getSelectionModel().getSelectedItem();

            // CSVファイル名
            String fileName = new StringBuilder(LocaleUtils.getString("key.InspectionData"))
                    .append("_")
                    .append(trace.getKanbanName())
                    .append("_")
                    .append(sdf.format(new Date()))
                    .append(".csv")
                    .toString();
            // 保存先フォルダパス
            String folderPath = Paths.get(System.getenv("ADFACTORY_HOME"), "temp", "inspectionData").toString();
            // 保存ファイルパス
            String filePath = Paths.get(folderPath, fileName).toString();

            File folder = new File(folderPath);
            if (!folder.exists()) {
                // フォルダがない場合は作成する。
                folder.mkdirs();
            } else {
                // すでにフォルダがある場合はフォルダの中のファイルを削除する。
                FilenameFilter filter = new FilenameFilter() {
                    // 拡張子を指定する
                    public boolean accept(File file, String str) {
                        return str.endsWith(".csv");
                    }
                };
                File[] list = folder.listFiles(filter);
                Arrays.stream(list).forEach(file -> file.delete());
            }

            final File file = new File(filePath);

            // 検索条件
            final Long kanbanId = trace.getKanbanId();
            final TraceabilitySearchCondition condition = new TraceabilitySearchCondition();
            condition.setKanbanName(trace.getKanbanName());
            condition.setModelName(trace.getModelName());

            isCancel = false;

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    TraceabilityFacade facade = new TraceabilityFacade();

                    // 選択したカンバンのトレーサビリティを取得する。
                    List<TraceabilityEntity> entities;
                    if (Objects.isNull(kanbanId) || kanbanId < 0) {
                        // カンバンなしの場合、カンバン名とモデル名で検索する。
                        entities = facade.findSearch(condition);
                    } else {
                        // カンバンIDで直接取得する。
                        entities = facade.findKanbanTraceability(kanbanId);
                    }

                    SimpleDateFormat df = new SimpleDateFormat(CSV_DATE_FORMAT);
                    final Character separator = (FilenameUtils.getExtension(file.getPath()).equals("tsv")) ? '\t' : ',';

                    // CSVファイルに出力する。
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "MS932"))) {
                        // ヘッダーを出力する。
                        StringBuilder title = new StringBuilder();
                        title.append(LocaleUtils.getString("key.ProcessName"));// 工程名
                        title.append(separator);
                        title.append(LocaleUtils.getString("key.Standard"));// 規格
                        title.append(separator);
                        title.append(LocaleUtils.getString("key.equipmentMeasureType"));// 測定機器
                        title.append(separator);
                        title.append(LocaleUtils.getString("key.Result"));// 結果
                        title.append(separator);
                        title.append(LocaleUtils.getString("key.passOrFail"));// 合否
                        title.append(separator);
                        title.append(LocaleUtils.getString("key.WorkingDay"));// 実施日(作業日)
                        title.append(separator);
                        title.append(LocaleUtils.getString("key.workers"));// 作業者
                        title.append(separator);

                        writer.write(title.toString());
                        writer.newLine();

                        // データを出力する。
                        for (TraceabilityEntity entity : entities) {
                            StringBuilder sb = new StringBuilder();

                            // 工程名
                            sb.append(entity.getTraceName());
                            sb.append(separator);

                            // 規格
                            String lowerLimit = "";
                            if (Objects.nonNull(entity.getLowerLimit()) && !entity.getLowerLimit().isNaN()) {
                                lowerLimit = String.format(CSV_LIMIT_VALUE_FORMAT, entity.getLowerLimit());
                            }
                            String upperLimit = "";
                            if (Objects.nonNull(entity.getUpperLimit()) && !entity.getUpperLimit().isNaN()) {
                                upperLimit = String.format(CSV_LIMIT_VALUE_FORMAT, entity.getUpperLimit());
                            }

                            sb.append(lowerLimit);
                            if (!lowerLimit.isEmpty() || !upperLimit.isEmpty()) {
                                sb.append(" ").append(LocaleUtils.getString("key.FromSymbol")).append(" ");
                            }
                            sb.append(upperLimit);
                            sb.append(separator);

                            // 測定機器
                            String equipmentName = "";
                            if (Objects.nonNull(entity.getEquipmentName())) {
                                equipmentName = entity.getEquipmentName();
                            }
                            sb.append(equipmentName);
                            sb.append(separator);

                            // 結果
                            String traceValue = "";
                            if (Objects.nonNull(entity.getTraceValue())) {
                                traceValue = entity.getTraceValue();
                            }

                            sb.append(traceValue);
                            sb.append(separator);

                            // 合否
                            sb.append(entity.getTraceConfirm() ? CSV_OK_STRING : CSV_NG_STRING);
                            sb.append(separator);

                            // 実施日
                            sb.append(df.format(entity.getImplementDatetime()));
                            sb.append(separator);

                            // 作業者
                            sb.append(entity.getOrganizationName());

                            writer.write(sb.toString());
                            writer.newLine();

                            // 追加トレーサビリティの処理
                            if (Objects.nonNull(entity.getTraceProps()) && !entity.getTraceProps().isEmpty()) {
                                List<Map.Entry<String, String>> traceProps
                                        = new ArrayList<>(JsonUtils.jsonToMap(entity.getTraceProps()).entrySet());
                                traceProps.sort((entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()));

                                for (Map.Entry<String, String> prop : traceProps) {
                                    String propTag = prop.getKey();
                                    String propValue = prop.getValue();

                                    sb = new StringBuilder();

                                    // 工程名
                                    sb.append(propTag);
                                    sb.append(separator);

                                    // 規格
                                    sb.append("");
                                    sb.append(separator);

                                    // 測定機器
                                    sb.append("");
                                    sb.append(separator);

                                    // 結果
                                    String result = Objects.nonNull(propValue) ? propValue : "";
                                    sb.append(result);
                                    sb.append(separator);

                                    // 合否
                                    sb.append("");
                                    sb.append(separator);

                                    // 実施日
                                    sb.append("");
                                    sb.append(separator);

                                    // 作業者
                                    sb.append("");

                                    writer.write(sb.toString());
                                    writer.newLine();
                                }
                            }
                        }
                        writer.close();
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // エクセルを起動して、CSVファイルを表示する。
                        //ProcessBuilder pb = new ProcessBuilder("c:\\Program Files (x86)\\Microsoft Office\\Office14\\EXCEL.exe",saveFile);

                        // エクセルのフルパスを指定する必要があり、マネージャーインストールPCによりそのパスが色々なので
                        // csv ファイルに割り当てられたアプリ(通常はエクセル)をコマンドプロンプト経由で起動させる。
                        //ProcessBuilder pb = new ProcessBuilder("cmd", "/c", filePath);
                        //pb.start();
                        // CSVファイルを開く。
                        //      ※.ExcelがインストールされていればExcelが起動する。
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(file);

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

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(LocaleUtils.getString("key.InspectionData"));
                        alert.setHeaderText(String.format(LocaleUtils.getString("key.FailedToOutput"), LocaleUtils.getString("key.InspectionData")));
                        alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(LocaleUtils.getString("key.ErrorDetail") + ": " + this.getException().getLocalizedMessage())));
                        alert.showAndWait();

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }
    }
}
