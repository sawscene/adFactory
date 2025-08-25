/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.dialog;

import adtekfuji.admanagerapp.warehouseplugin.entity.DeliveryInfo;
import adtekfuji.admanagerapp.warehouseplugin.entity.OutputSuppliedListInfo;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.warehouse.Location;
import jp.adtekfuji.adFactory.entity.warehouse.MstProductInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryItemInfo;
import jp.adtekfuji.adFactory.enumerate.WarehousePropertyEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 支給品リスト出力ダイアログ
 * FXML Controller class
 * 
 * @author nar-nakamura
 */
@FxComponent(id = "OutputSuppliedListDialog", fxmlPath = "/fxml/warehouseplugin/OutputSsuppliedListDialog.fxml")
public class OutputSuppliedListDialog implements Initializable, ArgumentDelivery, DialogHandler {
    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = ResourceBundle.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final Properties properties = AdProperty.getProperties();

    private static final char SEPARATOR = ',';
    private static final char QUOTE = '"';
    private static final String LINEEND = "\r\n";
    private static final String CHARSET = "UTF-8";
    private final SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final String ACCEPT_PLAN_CSV = "accept_plan.csv";
    private static final String MATERIAL_INFO_CSV = "material_info.csv";
    private static final String PARTS_TYPE = "02";
    private static final String CLASSIFY_FORMAT = "%02d";

    private final static String SUPPLIED_LIST_OUTPUT_DIR_KEY = "supplied_list_output_dir"; // 支給品リスト.出力先
    private final static String SUPPLIED_LIST_ACCEPT_CODE_KEY = "supplied_list_accept_code"; // 支給品リスト.受入予定の発注先コード
    private final static String SUPPLIED_LIST_ACCEPT_NAME_KEY = "supplied_list_accept_name"; // 支給品リスト.受入予定の発注先名

    private final long MAX_LOAD_SIZE = ClientServiceProperty.getRestRangeNum();

    @FXML
    private GridPane operationPane;
    @FXML
    private DatePicker scheduledDatePicker;
    @FXML
    private TextField outputDestinationField;
    @FXML
    private ListView resultList;
    @FXML
    private Pane progressPane;
    @FXML
    private Button choiceButton;
    @FXML
    private Button outputButton;

    private Dialog dialog;

    /**
     * 支給品リスト出力情報
     */
    private OutputSuppliedListInfo outputInfo;

    /**
     * 払出部品リスト
     */
    private final List<String> payoutParts = new ArrayList();

    /**
     * 支給品リスト.受入予定の発注先コード
     */
    private String acceptCode = "";

    /**
     * 支給品リスト.受入予定の発注先名
     */
    private String acceptName = "";

    /**
     *
     */
    public OutputSuppliedListDialog() {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);
        this.scheduledDatePicker.setValue(LocalDate.now());

        // 支給品リスト.出力先
        if (!this.properties.containsKey(SUPPLIED_LIST_OUTPUT_DIR_KEY)) {
            File outputDir = new File(System.getProperty("user.home"), "Documents");// マイドキュメント
            this.properties.setProperty(SUPPLIED_LIST_OUTPUT_DIR_KEY, outputDir.getPath());
        }
        this.outputDestinationField.setText(this.properties.getProperty(SUPPLIED_LIST_OUTPUT_DIR_KEY));

        // 支給品リスト.受入予定の発注先コード
        if (!this.properties.containsKey(SUPPLIED_LIST_ACCEPT_CODE_KEY)) {
            this.properties.setProperty(SUPPLIED_LIST_ACCEPT_CODE_KEY, "");
        }
        this.acceptCode = this.properties.getProperty(SUPPLIED_LIST_ACCEPT_CODE_KEY);

        // 支給品リスト.受入予定の発注先名
        if (!this.properties.containsKey(SUPPLIED_LIST_ACCEPT_NAME_KEY)) {
            this.properties.setProperty(SUPPLIED_LIST_ACCEPT_NAME_KEY, "");
        }
        this.acceptName = this.properties.getProperty(SUPPLIED_LIST_ACCEPT_NAME_KEY);
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof OutputSuppliedListInfo) {
            this.outputInfo = (OutputSuppliedListInfo) argument;
        }
    }

    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.closeDialog();
        });
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            dialog.getDialogPane().setDisable(flg);
            progressPane.setVisible(flg);
        });
    }

    /**
     * 結果リストにメッセージを追加し、追加したメッセージが見えるようにスクロールする
     *
     * @param message メッセージ
     */
    private void addResult(String message) {
        Platform.runLater(() -> {
            this.resultList.getItems().add(message);
            this.resultList.scrollTo(message);
        });
    }

    /**
     * 出力先パス指定
     *
     * @param event 選択ボタン押下
     */
    @FXML
    private void onChoiceButton(ActionEvent event) {
        try {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            blockUI(true);
            DirectoryChooser dc = new DirectoryChooser();
            File fol = new File(this.outputDestinationField.getText());
            if (fol.exists() && fol.isDirectory()) {
                dc.setInitialDirectory(fol);
            }
            File selectedFile = dc.showDialog(stage);
            if (selectedFile != null) {
                this.outputDestinationField.setText(selectedFile.getPath());
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 支給品リスト出力
     *
     * @param event 出力ボタン押下
     */
    @FXML
    private void onOutputButton(ActionEvent event) {
        try {
            blockUI(true);
            this.resultList.getItems().clear();
            if (Objects.isNull(this.outputInfo.getDeliveryInfoList()) || this.outputInfo.getDeliveryInfoList().isEmpty()) {
                return;
            }

            // 支給予定日
            if (Objects.isNull(this.scheduledDatePicker.getValue())) {
                return;
            }
            String acceptDate = dateFormatter.format(
                    Date.from(scheduledDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

            // 出力先
            String folder = this.outputDestinationField.getText();
            if (Objects.isNull(folder) || folder.isEmpty()) {
                return;
            }

            // ファイル存在確認
            File acceptPlan = new File(folder, ACCEPT_PLAN_CSV);
            File materialInfo = new File(folder, MATERIAL_INFO_CSV);
            if (acceptPlan.exists() || materialInfo.exists()) {
                // 支給品リストは既に存在します。\n上書きしますか？
                ButtonType ret = sc.showOkCanselDialog(
                        Alert.AlertType.CONFIRMATION, rb.getString("key.OutputSuppliedPartsList"),
                        String.format(rb.getString("key.ExistOverwrite"), rb.getString("key.SuppliedPartsList")));
                if (ret.equals(ButtonType.CANCEL)) {
                    return;
                }
            }

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    blockUI(true);
                    try {
                        createFileThread(acceptDate, acceptPlan.getPath(), materialInfo.getPath());
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                    return null;
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
     * 終了
     *
     * @param event 閉じるボタン押下
     */
    @FXML
    private void onCloseButton(ActionEvent event) {
        this.closeDialog();
    }

    /**
     *
     */
    private void createFileThread(String acceptDate, String acceptPlanPath, String materialInfoPath) {
        this.payoutParts.clear();

        // 受入予定ファイル作成
        if (!this.createAcceptPlanFile(acceptDate, acceptPlanPath)) {
            // 支給品リストの出力に失敗しました。
            this.addResult(String.format(rb.getString("key.FailedToOutput"), rb.getString("key.SuppliedPartsList")));
            return;
        }
        // 部品情報ファイル作成
        if (!this.createMaterialInfoFile(materialInfoPath)) {
            // 支給品リストの出力に失敗しました。
            this.addResult(String.format(rb.getString("key.FailedToOutput"), rb.getString("key.SuppliedPartsList")));
            return;
        }

        // 完了メッセージ
        //      支給品リストを出力しました。
        //      このリストを、払出先に送ってください。
        this.addResult(String.format(rb.getString("key.OutputSuccess"), rb.getString("key.SuppliedPartsList")));
        this.addResult(rb.getString("key.PleaseSendPayoutDestination"));
    }

    /**
     * 払出カンバンの実績から受入予定ファイルを作成する。
     *
     * @param acceptDate 支給予定日
     * @param folder
     * @throws Exception
     */
    private boolean createAcceptPlanFile(String acceptDate, String path) {
        boolean ret = true;
        try {
            OutputStream output = new FileOutputStream(path);
            OutputStreamWriter owriter = new OutputStreamWriter(output, CHARSET);
            try (CSVWriter writer = new CSVWriter(owriter, SEPARATOR, QUOTE, LINEEND)) {
                List<DeliveryInfo> deliveries = this.outputInfo.getDeliveryInfoList();
                deliveries.sort(Comparator.comparing(item -> item.getValue().getDeliveryNo()));// 出庫番号順にソート
                deliveries.stream().forEach(delivery -> {
                    if (delivery.isSelected()) {
                        TrnDeliveryInfo trnDelivery = delivery.getValue();
                        trnDelivery.getDeliveryList().stream().forEach(deliveryItemInfo -> {
                            MstProductInfo product = deliveryItemInfo.getProduct();
                            
                            // 払出数・要求数チェック：払出数が要求数に合わない場合、結果に警告を表示する。
                            if (!deliveryItemInfo.getDeliveryNum().equals(deliveryItemInfo.getRequiredNum())) {
                                // 要求数と払出数が合わない
                                this.addResult(String.format("%s [%s]", rb.getString("key.NotMatchPayoutNum"), deliveryItemInfo.getMaterialNo()));
                                this.addResult("> " + product.getProductNo());// 図番
                            }

                            // 管理区分
                            String classify = (Objects.isNull(product) || Objects.isNull(product.getClassify())) ? "" : String.format(CLASSIFY_FORMAT, Integer.parseInt(product.getClassify()));
                            // 品目コード
                            String productNo = Objects.isNull(product) ? "" : product.getProductNo();
                            // 払出ラベルのQRコード
                            String deliveryQR = StringUtils.padBytesString(trnDelivery.getUnitNo(), 20)
                                    + StringUtils.padBytesString(trnDelivery.getSerialStart(), 6)
                                    + StringUtils.padBytesString(trnDelivery.getSerialEnd(), 6)
                                    + StringUtils.padBytesString("", 32)
                                    + StringUtils.padBytesString(Objects.isNull(classify) ? "" : classify, 2)
                                    + StringUtils.padBytesString(Objects.isNull(productNo) ? "" : productNo, 6);// 払出QRコード

                            List<String> row = new ArrayList<>();
                            row.add(datetimeFormatter.format(trnDelivery.getUpdateDate()));// 更新日時
                            row.add(deliveryQR);// 受入識別名
                            row.add(StringUtils.padBytesString(PARTS_TYPE + productNo, 20));// 部品識別名
                            row.add(acceptDate);// 支給予定日
                            row.add(Objects.toString(deliveryItemInfo.getDeliveryNum()));// 数量
                            row.add("2");

                            row.add("1");
                            row.add(WarehousePropertyEnum.ACCEPT_CODE.getName());
                            row.add("TYPE_STRING");
                            row.add("");// 発注先コード

                            row.add("2");
                            row.add(WarehousePropertyEnum.ACCEPT_NAME.getName());
                            row.add("TYPE_STRING");
                            row.add("");// 発注先名

                            writer.writeNext(row.toArray(new String[row.size()]));
                        });
                    }
                });

                writer.flush();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                ret = false;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            ret = false;
        }
        return ret;
    }

    /**
     * 払出部品リストから部品情報ファイルを作成する。
     *
     * @param path
     * @return
     * @throws Exception
     */
    private boolean createMaterialInfoFile(String path) {
        boolean ret = true;
        try {
            OutputStream output = new FileOutputStream(path);
            OutputStreamWriter owriter = new OutputStreamWriter(output, CHARSET);
            try (CSVWriter writer = new CSVWriter(owriter, SEPARATOR, QUOTE, LINEEND)) {
                this.outputInfo.getDeliveryInfoList().stream().forEach(deliveryInfo -> {
                    List<TrnDeliveryItemInfo> deliveryList = deliveryInfo.getValue().getDeliveryList();
                    deliveryList.stream().forEach(deliveryItem -> {
                        MstProductInfo product = deliveryItem.getProduct();
                        // 品目コード
                        String productNo = Objects.isNull(product) ? "" : product.getProductNo();
                        String partsIdName = StringUtils.padBytesString(Objects.isNull(productNo) ? "" : (PARTS_TYPE + productNo), 20);// 部品識別名

                        String standard = "";// 規格
                        String material = "";// 材質
                        String manufacturer = "";// メーカー
                        if (Objects.nonNull(deliveryItem.getProperty())) {
                            standard += deliveryItem.getProperty().get("Spec") + " ";
                            material += deliveryItem.getProperty().get("Material") + " ";
                            manufacturer += deliveryItem.getProperty().get("Vendor") + " ";
                        }

                        List<String> row = new ArrayList<>();
                        row.add(datetimeFormatter.format(deliveryInfo.getValue().getUpdateDate()));// 更新日時
                        row.add(partsIdName);// 部品識別名
                        row.add(Objects.isNull(product) ? "" : product.getProductNo());// 品目コード
                        row.add("5");

                        row.add("1");
                        row.add(WarehousePropertyEnum.MANAGEMENT.getName());
                        row.add("TYPE_STRING");
                        row.add(PARTS_TYPE);// 管理区分

                        row.add("2");
                        row.add(WarehousePropertyEnum.PRODUCT.getName());
                        row.add("TYPE_STRING");
                        row.add(Objects.isNull(product) ? "" : product.getProductName());// 品目名

                        row.add("3");
                        row.add(WarehousePropertyEnum.STANDARD.getName());
                        row.add("TYPE_STRING");
                        row.add(standard);// 規格

                        row.add("4");
                        row.add(WarehousePropertyEnum.MATERIAL.getName());
                        row.add("TYPE_STRING");
                        row.add(material);// 材質

                        row.add("5");
                        row.add(WarehousePropertyEnum.MANUFACTURER.getName());
                        row.add("TYPE_STRING");
                        row.add(manufacturer);//メーカ

                        row.add("6");
                        row.add(WarehousePropertyEnum.STORAGE_NAME.getName());
                        row.add("TYPE_STRING");
                        String loc = "";
                        if (Objects.nonNull(product)) {
                            for (Location location : product.getLocationList()) {
                                loc += location.getLocationNo() + " ";
                            }
                        }
                        row.add(loc);// 棚番

                        writer.writeNext(row.toArray(new String[row.size()]));
                    });
                });

                writer.flush();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                ret = false;
            } finally {
                logger.info("CSVWriter end");
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            ret = false;

        }
        return ret;
    }

    /**
     * 終了処理
     */
    private void closeDialog() {
        try {
            this.properties.replace(SUPPLIED_LIST_OUTPUT_DIR_KEY, this.outputDestinationField.getText());
            this.dialog.setResult(ButtonType.CLOSE);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
