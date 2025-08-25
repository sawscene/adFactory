/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.dialog;

import adtekfuji.clientservice.WarehouseInfoFaced;
import adtekfuji.clientservice.common.Paths;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateUtils;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.warehouse.LogStockInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;
import jp.adtekfuji.adFactory.enumerate.WarehouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 払出品リスト出力ダイアログ
 * 
 * @author s-heya
 */
@FxComponent(id = "OutputDeliveryListDialog", fxmlPath = "/fxml/warehouseplugin/OutputDeliveryListDialog.fxml")
public class OutputDeliveryListDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private Dialog dialog;
    private final Logger logger = LogManager.getLogger();
    private final WarehouseInfoFaced facade = new WarehouseInfoFaced();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private List<TrnMaterialInfo> materials;

    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private Button outputButton;
    @FXML
    private Pane progressPane;
    
    /**
     * 払出品リスト出力ダイアログを初期化する。
     * 
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.fromDatePicker.setEditable(false);
        this.fromDatePicker.setValue(LocalDate.now());
        this.toDatePicker.setEditable(false);
        this.toDatePicker.setValue(LocalDate.now());
    }

    /**
     * パラメータを設定する。
     *
     * @param argument パラメータ
     */
    @Override
    public void setArgument(Object argument) {
        this.materials = (List<TrnMaterialInfo>) argument;
        if (Objects.isNull(this.materials) || this.materials.isEmpty()) {
            outputButton.setDisable(true);
        }
    }

    /**
     * ダイアログを設定する。
     *
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.closelDialog();
        });
    }    
    
    /**
     * 出力処理
     *
     * @param event イベント
     */
    @FXML
    private void onOutput(ActionEvent event) {
        try {
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    blockUI(true);

                   // 入出庫履歴を取得
                    List<String> materialNos = materials.stream().map(TrnMaterialInfo::getMaterialNo).collect(Collectors.toList());
                    
                    List<LogStockInfo> logList = new ArrayList<>();
                    int ii = 0; 
                    while (ii < materialNos.size()) {
                        int count = ii + ((materialNos.size() - ii) < 200 ? materialNos.size() - ii : 200);
                        // subListはインデックスの終わりを含まない
                        List<String> list = materialNos.subList(ii, count);
                        logList.addAll(facade.findLogStock(list));
                        ii = count;
                    }
                    
                    logger.info("Count of Material: " + materialNos.size());
                    logger.info("Count of LogStock: " + logList.size());

                    // 入出庫履歴をエクセルファイルに出力
                    outputExcel(logList);

                    return null;
                }
                
                @Override
                protected void succeeded() {
                    super.succeeded();
                    // ダイアログを閉じる
                    Platform.runLater(() -> {
                        dialog.setResult(ButtonType.OK);
                        dialog.close();
                    });
                    blockUI(false);
                }
               
                @Override
                protected void failed() {
                    super.failed();
                    blockUI(false);
                    logger.fatal("Failed output the delivery list.");
                }
            };
            new Thread(task).start();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 入出庫履歴をエクセルファイルに出力する。
     * 
     * @param logList 入出庫履歴一覧
     */
    private void outputExcel(List<LogStockInfo> logList) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        Workbook workbook = null;

        try {
            Map<String, TrnMaterialInfo> map = this.materials.stream().collect(Collectors.toMap(o -> o.getMaterialNo(), o -> o));
           
            List<LogStockInfo> list = new ArrayList<>(logList.size());
            
            Date fromDate = Objects.isNull(fromDatePicker.getValue()) ? DateUtils.getBeginningOfDate(new Date()) : DateUtils.getBeginningOfDate(fromDatePicker.getValue());
            Date toDate = Objects.isNull(toDatePicker.getValue()) ? DateUtils.getEndOfDate(new Date()) : DateUtils.getEndOfDate(toDatePicker.getValue());

            logList.stream().forEach(o -> {
                if (WarehouseEvent.LEAVE.equals(o.getEventKind())) {
                    Date date = DateUtils.toDate(o.getEventDate());
                    if (date.compareTo(fromDate) >= 0 && date.compareTo(toDate) <= 0) {
                        TrnMaterialInfo material = map.get(o.getMaterialNo());
                        if (Objects.nonNull(material)) {
                            o.setMaterial(material);
                            o.setUnitNo(material.getUnitNo());
                            list.add(o);
                        }
                    }
                }
            });
            
            Comparator<LogStockInfo> comparator = Comparator.comparing(LogStockInfo::getOrderNo)
                    .thenComparing(LogStockInfo::getUnitNo)
                    .thenComparing(LogStockInfo::getProductNo);
            list.sort(comparator);

            // ファイル名
            String templateFilePath = Paths.ADFACTORY_HOME + File.separator + "template" + File.separator + "delivery_list.xlsx";
            String outputFilePath = new StringBuilder()
                    .append(Paths.ADFACTORY_HOME)
                    .append(File.separator)
                    .append("temp")
                    .append(File.separator)
                    .append("delivery_list_")
                    .append((new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()))
                    .append(".xlsx")
                    .toString();
            
            // テンプレートファイルを開く
            inputStream = new FileInputStream(templateFilePath);
            workbook = (XSSFWorkbook)WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheet("List");

            // セルスタイル
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
            cellStyle.setBorderBottom(CellStyle.BORDER_DOTTED);  // 下罫線（点線）

            // 日付型セルスタイル
            CreationHelper createHelper = workbook.getCreationHelper();
            CellStyle dateStyle = workbook.createCellStyle();
            short style = createHelper.createDataFormat().getFormat("yyyy/mm/dd");
            dateStyle.setDataFormat(style);
            dateStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
            dateStyle.setBorderBottom(CellStyle.BORDER_DOTTED);

            // 対象日を出力
            Row row = sheet.getRow(0);
            if (Objects.isNull(row)) {
                row = sheet.createRow(0);
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            String targetDate = df.format(fromDate) + " - " + df.format(toDate);
            Cell cell = row.getCell(7);
            if (Objects.isNull(cell)) {
                cell = row.createCell(7);
            }
            cell.setCellValue(targetDate);
        
            // 出庫履歴を出力
            int rowIndex = 2;
            for (LogStockInfo log : list) {
                
                if (!WarehouseEvent.LEAVE.equals(log.getEventKind())) {
                    continue;
                }
                
                row = sheet.getRow(rowIndex);
                if (Objects.isNull(row)) {
                    row = sheet.createRow(rowIndex);
                }

                int columnIndex = 0;
                
                // No
                writeCell(row, columnIndex, rowIndex - 1, cellStyle);
                columnIndex++;
                
                // 製番
                writeCell(row, columnIndex, log.getOrderNo(), cellStyle);
                columnIndex++;
                
                // ユニット番号
                writeCell(row, columnIndex, log.getUnitNo(), cellStyle);
                columnIndex++;
                
                // 品目
                writeCell(row, columnIndex, log.getProductNo(), cellStyle);
                columnIndex++;

                // 品名
                writeCell(row, columnIndex, log.getMaterial().getProduct().getProductName(), cellStyle);
                columnIndex++;
                
                // 型式
                writeCell(row, columnIndex, log.getMaterial().getSepc(), cellStyle);
                columnIndex++;

                // 数量                
                writeCell(row, columnIndex, log.getEventNum(), cellStyle);
                columnIndex++;

                // 払出日
                writeCell(row, columnIndex, DateUtils.toDate(log.getEventDate()), dateStyle);
                columnIndex++;

                // 担当者
                writeCell(row, columnIndex, log.getPersonNo(), cellStyle);
                columnIndex++;
                
                rowIndex++;
            }
            
            // エクセルファイルを保存
            outputStream = new FileOutputStream(outputFilePath);
            workbook.write(outputStream);
            workbook.close();
            
            // エクセルファイルを開く
            Desktop.getDesktop().open(new File(outputFilePath));
            // 印刷する場合
            //Desktop.getDesktop().print(new File(outputFilePath));

        } catch(IOException | EncryptedDocumentException | InvalidFormatException ex) {
            logger.fatal(ex, ex);

        } finally {
            if (Objects.nonNull(inputStream)) {
                try {
                    inputStream.close();
                }catch(IOException e) {
                }
            }
            if (Objects.nonNull(outputStream)) {
                try {
                    outputStream.close();
                } catch(IOException e) {
                }
            }
            if (Objects.nonNull(workbook)) {
                try {
                    workbook.close();
                } catch(IOException e) {
                }
            }
        }
    }
    
    /**
     * セルに値を書き込む。
     * 
     * @param row 行
     * @param index カラムインデックス
     * @param value 値
     * @param cellStyle セルスタイル 
     */
    private void writeCell(Row row, int index, double value, CellStyle cellStyle) {
        Cell cell = row.getCell(index);
        if (Objects.isNull(cell)) {
            cell = row.createCell(index);
        }
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
    }

    /**
     * セルに値を書き込む。
     * 
     * @param row 行
     * @param index カラムインデックス
     * @param value 値
     * @param cellStyle セルスタイル 
     */
    private void writeCell(Row row, int index, String value, CellStyle cellStyle) {
        Cell cell = row.getCell(index);
        if (Objects.isNull(cell)) {
            cell = row.createCell(index);
        }
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
    }
    
    /**
     * セルに値を書き込む。
     * 
     * @param row 行
     * @param index カラムインデックス
     * @param value 値
     * @param cellStyle セルスタイル 
     */
    private void writeCell(Row row, int index, Date value, CellStyle cellStyle) {
        Cell cell = row.getCell(index);
        if (Objects.isNull(cell)) {
            cell = row.createCell(index);
        }
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
    }
     
    /**
     * キャンセルボタン
     *
     * @param event イベント
     */
    @FXML
    private void onCancel(ActionEvent event) {
        this.closelDialog();
    }

    /**
     * キャンセル処理
     *
     */
    private void closelDialog() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 操作を無効にする。
     *
     * @param block true:操作無効、false:操作有効
     */
    private void blockUI(boolean block) {
        Platform.runLater(() -> {
            this.sc.blockUI("ContentNaviPane", block);
            this.progressPane.setVisible(block);
        });
    }
}