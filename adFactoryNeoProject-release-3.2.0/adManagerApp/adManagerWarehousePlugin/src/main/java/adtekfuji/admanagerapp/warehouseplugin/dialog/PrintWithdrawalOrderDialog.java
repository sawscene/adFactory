/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.dialog;

import adtekfuji.admanagerapp.warehouseplugin.entity.DeliveryInfo;
import adtekfuji.barcode.Barcode;
import adtekfuji.clientservice.WarehouseInfoFaced;
import adtekfuji.clientservice.common.Paths;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.warehouse.MstProductInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryItemInfo;
import jp.adtekfuji.excelreplacer.ExcelReplacer;
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
 * 払出票印刷ダイアログ
 * 
 * @author s-heya
 */
@FxComponent(id = "PrintWithdrawalOrderDialog", fxmlPath = "/fxml/warehouseplugin/PrintWithdrawalOrderDialog.fxml")
public class PrintWithdrawalOrderDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private Dialog dialog;
    private final Logger logger = LogManager.getLogger();
    private final WarehouseInfoFaced facade = new WarehouseInfoFaced();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private  List<DeliveryInfo> deliveries;

    @FXML
    private ComboBox copiesComboBox;
    @FXML
    private Button printButton;
    @FXML
    private Pane progressPane;
    
    /**
     * 払出票印刷ダイアログを初期化する。
     * 
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.copiesComboBox.getItems().addAll(1,2,3,4,5,6,7,8,9,10);
        this.copiesComboBox.getSelectionModel().select(0);
    }

    /**
     * パラメータを設定する。
     *
     * @param argument パラメータ
     */
    @Override
    public void setArgument(Object argument) {
        this.deliveries = (List<DeliveryInfo>) argument;
        if (Objects.isNull(this.deliveries) || this.deliveries.isEmpty()) {
            printButton.setDisable(true);
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
     * 印刷
     *
     * @param event イベント
     */
    @FXML
    private void onPrint(ActionEvent event) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                doPrint();
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 印刷処理
     */
    private void doPrint() {
        try {
            String templatePath = System.getenv("ADFACTORY_HOME") + File.separator + "template" + File.separator + "delivery_form.xlsx";
            final String vbScriptPath = System.getenv("ADFACTORY_HOME") + File.separator + "bin" + File.separator + "print_excel.vbs";

            for (DeliveryInfo delivery : this.deliveries) {
                TrnDeliveryInfo trnDelivery = delivery.getValue();
                logger.info("printed:{}", trnDelivery);

                String tempPath = new StringBuilder()
                    .append(Paths.ADFACTORY_HOME)
                    .append(File.separator)
                    .append("temp")
                    .append(File.separator)
                    .append("delivery_form_")
                    .append(trnDelivery.getDeliveryNo())
                    .append(".xlsx")
                    .toString();

                String dstPath = new StringBuilder()
                    .append(Paths.ADFACTORY_HOME)
                    .append(File.separator)
                    .append("temp")
                    .append(File.separator)
                    .append(trnDelivery.getDeliveryNo())
                    .append(".xlsx")
                    .toString();
            
                Map<String, Object> tagMap = new HashMap<>();
                tagMap.put("TAG_DELIVERY_NO", trnDelivery.getDeliveryNo());
                tagMap.put("TAG_MODEL_NAME", trnDelivery.getModelName());
                tagMap.put("TAG_ORDER_NO", trnDelivery.getOrderNo());
                tagMap.put("TAG_UNIT_NO", trnDelivery.getUnitNo());
                tagMap.put("TAG_DUE_DATE", trnDelivery.getDueDate());
                tagMap.put("TAG_DEST_NAME", trnDelivery.getDestName());

                // QRコード
                String contents = trnDelivery.getDeliveryNo();
                BufferedImage qrCodeImage;
                try {
                    qrCodeImage = Barcode.createQRCodeImage(contents, ErrorCorrectionLevel.Q, "MS932", 900);
                    tagMap.put("TAG_QRCODE_IMAGE", qrCodeImage);
                } catch (WriterException ex) {
                    logger.fatal(ex, ex);
                }

                try {
                    File file = new File(templatePath);
                    if (!file.exists()) {
                        // テンプレートファイルがない
                        logger.warn("Not found template file: {}", file);
                        showAlert(LocaleUtils.getString("deliveryTemplateNotFound"));
                        return;
                    }

                    File tempFile = new File(tempPath);
                    Function<String, String> tagConverter = string -> string;
                    ExcelReplacer.replace(file, tempFile, tagMap, tagConverter);

                    if (this.outputPickingList(trnDelivery, tempPath, dstPath)) {
                        // ファイルを開く場合
                        //Desktop.getDesktop().open(new File(dstPath));

                        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", vbScriptPath, dstPath, this.copiesComboBox.getValue().toString());
                        Process process = pb.start();
                        process.waitFor(10, TimeUnit.SECONDS);
                    }
                    
                } catch (IOException | InterruptedException ex) {
                    logger.fatal(ex, ex);
                    showAlert(LocaleUtils.getString("key.import.error.rename"));
                }
            }
            
             this.closelDialog();

        } catch(Exception ex) {
            logger.fatal(ex, ex);
            showAlert(LocaleUtils.getString("key.FileOutputErrorOccured"));
        } finally {
            blockUI(false);
        }
    }

    /**
     * エラーダイアログを表示する
     * 
     * @param msg エラー内容
     */
    private void showAlert(String msg) {
        Platform.runLater(() -> {
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Error"), msg);
        });
    }

    /**
     * ピッキングリストを出力する
     * 
     * @param trnDelivery
     * @param srcPath
     * @param dstPath
     * @return 
     */
    private boolean outputPickingList(TrnDeliveryInfo trnDelivery, String srcPath, String dstPath) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        Workbook workbook = null;

        try {
            List<TrnDeliveryItemInfo> deliveryItems = trnDelivery.getDeliveryList();
            
            // 棚番号、明細番号順に並び替え
            //Comparator<TrnDeliveryItemInfo> comparator = Comparator.comparing(TrnDeliveryItemInfo::getDefaultLocationNo)
            //        .thenComparing(TrnDeliveryItemInfo::getItemNo);

            // 明細番号順に並び替え
            Comparator<TrnDeliveryItemInfo> comparator = Comparator.comparing(TrnDeliveryItemInfo::getItemNo);
            deliveryItems.sort(comparator);
            
            inputStream = new FileInputStream(srcPath);
            workbook = (XSSFWorkbook)WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // セルスタイル
            CellStyle whiteStyle = workbook.createCellStyle();
            whiteStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            whiteStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

            CellStyle grayStyle = workbook.createCellStyle();
            grayStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            grayStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

            // 日付型セルスタイル
            CreationHelper createHelper = workbook.getCreationHelper();
            CellStyle dateStyle = workbook.createCellStyle();
            short style = createHelper.createDataFormat().getFormat("yyyy/mm/dd");
            dateStyle.setDataFormat(style);
            //dateStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
            //dateStyle.setBorderBottom(CellStyle.BORDER_DOTTED);
           
            int rowIndex = 0; // 11行目から開始
            for (TrnDeliveryItemInfo item : deliveryItems) {

                CellStyle cellStyle = rowIndex % 2 == 0 ? whiteStyle : grayStyle;
                
                Row row = sheet.getRow(rowIndex + 10);
                if (Objects.isNull(row)) {
                    row = sheet.createRow(rowIndex + 10);
                }
                row.setRowStyle(cellStyle);
                
                Map<String, String> map = Objects.nonNull(item.getProduct().getProperty()) ? item.getProduct().getProperty() : new HashMap<>();

                // No
                writeCell(row, 1, rowIndex + 1, cellStyle);
                
                // 品目
                writeCell(row, 3, item.getProduct().getProductNo(), cellStyle);

                // 品名
                writeCell(row, 5, item.getProduct().getProductName(), cellStyle);
                
                // 型式
                writeCell(row, 6, map.get(MstProductInfo.SPEC), cellStyle);

                // 材質・メーカー
                writeCell(row, 7, map.get(MstProductInfo.MATERIAL), cellStyle);

                // 数量
                writeCell(row, 8, item.getRequiredNum(), cellStyle);

                // 払出数
                writeCell(row, 9, "", cellStyle);

                // 単位
                writeCell(row, 10, item.getProduct().getUnit(), cellStyle);

                // 棚番号
                writeCell(row, 11, item.getDefaultLocationNo(), cellStyle);

                // 不足数
                int value = item.getRequiredNum() - item.getReservedNum();
                writeCell(row, 12, value, cellStyle);
                
                rowIndex++;
            }
            
            // エクセルファイルを保存
            outputStream = new FileOutputStream(dstPath);
            workbook.write(outputStream);
            workbook.close();
            
            return true;

        } catch(IOException | EncryptedDocumentException | InvalidFormatException ex) {
            logger.fatal(ex, ex);
            return false;

        } finally {
            try {
                if (Objects.nonNull(inputStream)) {
                    inputStream.close();
                }
                if (Objects.nonNull(outputStream)) {
                    outputStream.close();
                }
                if (Objects.nonNull(workbook)) {
                    workbook.close();
                }
            }catch(IOException e) {
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

        if (Objects.nonNull(cellStyle)) {
            cell.setCellStyle(cellStyle);
        }

        cell.setCellValue(value);
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

        if (Objects.nonNull(cellStyle)) {
            cell.setCellStyle(cellStyle);
        }

        cell.setCellValue(value);
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

        if (Objects.nonNull(cellStyle)) {
            cell.setCellStyle(cellStyle);
        }

        cell.setCellValue(value);
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
        Platform.runLater(() -> {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        });
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