/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.dialog;

import adtekfuji.admanagerapp.warehouseplugin.entity.AcceptanceInfo;
import adtekfuji.admanagerapp.warehouseplugin.enumerate.OutputReportResultEnum;
import adtekfuji.admanagerapp.warehouseplugin.utils.OutputForm;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
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
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.SampleResponse;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.warehouse.Location;
import jp.adtekfuji.adFactory.entity.warehouse.MstProductInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.ClientResponse;

/**
 * ラベル発行ダイアログ
 *
 * @author s-morita
 */
@FxComponent(id = "PrintDialog", fxmlPath = "/fxml/warehouseplugin/print_dialog.fxml")
public class PrintDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private Dialog dialog;
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    @FXML
    private ComboBox printNum;
    @FXML
    private Pane progressPane;
    @FXML
    private StackPane printPane;
    @FXML
    private GridPane waitPane;
    @FXML
    private Label waitLabel;
    @FXML
    private Button printButton;
    @FXML
    private Button cancelButton;

    private AcceptanceInfo acceptanceInfo;
    private List<TrnMaterialInfo> materialList;
    private List<String> failureSupplyNos;
    private OutputReportResultEnum printResult;

    /**
     * コンストラクタ
     */
    public PrintDialog() {

    }

    /**
     * 初期化
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("PrintDialog start.");

        for (int i = 1; i <= 10; i++) {
            if (!this.printNum.getItems().contains(i)) {
                this.printNum.getItems().add(i);
            }
        }
        this.printNum.getSelectionModel().select(0);
    }

    /**
     * 引数取得
     *
     * @param argument 引数
     */
    @Override
    public void setArgument(Object argument) {
        this.materialList = (List<TrnMaterialInfo>) argument;
    }

    /**
     * ダイアログ設定
     *
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancel();
        });
    }

    /**
     * 印刷ボタン処理
     *
     * @param event イベント
     */
    @FXML
    private void onPrintButton(ActionEvent event) {
        blockUI(true);

        try {
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        if (multiplePrint()) {
                            Platform.runLater(() -> {
                                dialog.setResult(ButtonType.OK);
                                dialog.close();
                            });
                        }

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
        }
    }

    /**
     * キャンセルボタン
     *
     * @param event イベント
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
        this.cancel();
    }

    /**
     * プリント処理を実行する。
     *
     */
    private void print() {
        try {
            logger.info("print start.");

            this.acceptanceInfo.setPrintNum(Integer.valueOf(this.printNum.getValue().toString()));
            this.printResult = OutputForm.outputAcceptLabel(this.acceptanceInfo);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("print end.");
        }
    }

    /**
     * 複数枚プリント処理を実行する。
     *
     * @return true: プリント成功、false:プリント失敗
     */
    private boolean multiplePrint() {
        try {
            logger.info("multiplePrint start.");

            this.failureSupplyNos = new ArrayList<>();

            for (TrnMaterialInfo material : this.materialList) {

                if (!StringUtils.isEmpty(material.getMaterialNo())) {
                    
                    if (!this.printToSmaPri(material)) {
                        return false;
                    }
                }
            }

            return true;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        } finally {
            logger.info("multiplePrint end.");
        }
    }

    /**
     * 印刷結果判定
     *
     */
    private void judgePrintResult() {

        // テンプレートファイルがない場合
        if (OutputReportResultEnum.TEMPLATE_NOT_FOUND.equals(this.printResult)) {
            showAlertDialg(LocaleUtils.getString("key.TemplateNotFound"));
        }

        // テンプレートファイルの読み込みに失敗した場合
        if (OutputReportResultEnum.TEMPLATE_LOAD_FAILED.equals(this.printResult)) {
            showAlertDialg(LocaleUtils.getString("key.TemplateLoadFailed"));
        }

        // ワークブックの保存に失敗した場合
        if (OutputReportResultEnum.WORKBOOK_SAVE_FAILED.equals(this.printResult)) {
            showAlertDialg(LocaleUtils.getString("key.WorkbookSaveFailed"));
        }

        // タグ置換に失敗した場合
        if (OutputReportResultEnum.REPLACE_FATAL.equals(this.printResult)) {
            showAlertDialg(LocaleUtils.getString("key.KanbanOutLedgerNoReplaceComp"));
        }

        // アプリケーションエラー場合
        if (OutputReportResultEnum.FATAL.equals(this.printResult)) {
            showAlertDialg(LocaleUtils.getString("key.KanbanOutLedgerApplicationErr"));
        }
    }

    /**
     * 棚番号を検索する。
     *
     * @param locationList
     * @param areaName
     * @return
     */
    public String searchLocationNo(List<Location> locationList, String areaName) {
        if (Objects.isNull(locationList)) {
            return "";
        }

        for (Location loc : locationList) {
            if (StringUtils.equals(areaName, loc.getAreaName())) {
                return loc.getLocationNo();
            }
        }
        return "";
    }

    /**
     * キャンセル処理
     *
     */
    private void cancel() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 警告ダイアログを表示する。
     *
     * @param message メッセージ
     */
    private void showAlertDialg(String message) {
        Platform.runLater(() -> {
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Error"), message);
        });
    }
    
    /**
     * プリント処理中の操作を無効にする。
     *
     * @param block true:操作を無効にする、false:操作を有効にする
     */
    private void blockUI(boolean block) {
        Platform.runLater(() -> {
            waitPane.setVisible(block);
            waitLabel.setVisible(block);
            printButton.setDisable(block);
            cancelButton.setDisable(block);
            sc.blockUI("ContentNaviPane", block);
            printPane.setDisable(block);
            progressPane.setVisible(block);
        });
    }

    /**
     * SATO ラべうプリンターにプリントコマンドを送信する。
     * 
     * @param material 資材情報
     */
    private boolean printToSmaPri(TrnMaterialInfo material) {

        final String UTF_8 = "UTF-8";

        try {
            Date date = new Date();
           
            // 区画名、棚番号
            String area = "";
            String location = "";
            if (Objects.nonNull(material.getLocation())) {
                area = URLEncoder.encode(material.getLocation().getAreaName(), UTF_8);
                location = URLEncoder.encode(material.getLocation().getLocationNo(), UTF_8);
            }
           
            MstProductInfo product = material.getProduct();

            // SmaPri 印刷コマンドを生成
            String serverAddr = AdProperty.getProperties().getProperty("serverAddress");

            StringBuilder printUrl = new StringBuilder("http://localhost:8081");
            printUrl.append("/Format/Print?__format_archive_url=");
            printUrl.append(serverAddr);
            printUrl.append("/adFactoryServer/deploy/SmaPri.spfmtz");
            // adFactory サーバーに保存されているテンプレート(SmaPri.spfmtz)が更新されていた場合、
            // SmaPri ドライバーがテンプレートのダウンロードを行う
            printUrl.append("&__format_archive_update=update");
            printUrl.append("&__format_id_number=");
            printUrl.append(AdProperty.getProperties().getProperty("wh_printFormat", "5"));
            printUrl.append("&order=");
            printUrl.append(adtekfuji.utility.StringUtils.isEmpty(material.getOrderNo()) ? "" : URLEncoder.encode(material.getOrderNo(), UTF_8));
            printUrl.append("&note=");
            printUrl.append(adtekfuji.utility.StringUtils.isEmpty(material.getNote()) ? "" : URLEncoder.encode(material.getNote(), UTF_8));
            printUrl.append("&material=");
            printUrl.append(URLEncoder.encode(material.getSupplyNo(), UTF_8));
            printUrl.append("&product=");
            printUrl.append(URLEncoder.encode(product.getProductNo(), UTF_8));
            printUrl.append("&name=");
            printUrl.append(adtekfuji.utility.StringUtils.isEmpty(product.getProductName()) ? "" : URLEncoder.encode(product.getProductName(), UTF_8));
            printUrl.append("&spec=");
            printUrl.append(adtekfuji.utility.StringUtils.isEmpty(material.getSepc()) ? "" : URLEncoder.encode(material.getSepc(), UTF_8));
            printUrl.append("&lotNo=");
            printUrl.append(adtekfuji.utility.StringUtils.isEmpty(material.getPartsNo()) ? "" : URLEncoder.encode(material.getPartsNo(), UTF_8));
            printUrl.append("&area=");
            printUrl.append(area);
            printUrl.append("&location=");
            printUrl.append(location);
            printUrl.append("&stock=");
            printUrl.append(material.getInStockNum());
            printUrl.append("&person=");
            printUrl.append(URLEncoder.encode(LoginUserInfoEntity.getInstance().getLoginId(), UTF_8));
            printUrl.append("&date=");
            printUrl.append(URLEncoder.encode(new SimpleDateFormat("yy/MM/dd").format(date), UTF_8));
            printUrl.append("&time=");
            printUrl.append(URLEncoder.encode(new SimpleDateFormat("HH:mm").format(date), UTF_8));
            printUrl.append("&(");
            printUrl.append(URLEncoder.encode("発行枚数", UTF_8));
            printUrl.append(")=");
            printUrl.append(this.printNum.getValue());

            Client client = ClientBuilder.newClient();
            client.property(ClientProperties.CONNECT_TIMEOUT, 3000);
            client.property(ClientProperties.READ_TIMEOUT, 3000);
            
            WebTarget target = client.target(printUrl.toString());
            ClientResponse response = target.request(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);

            if (response.getStatus() == 200) {
                SampleResponse res = response.readEntity(SampleResponse.class);
                if ("NG".equals(res.getResult())) {
                    String message = res.getMessage();
                    logger.fatal(message);
                    showAlertDialg(message);
                    return false;
                }
                return true;
            }

            String message = String.format("Error: %s %s", response.getStatus(), response.getStatusInfo().toString());
            logger.fatal(message);
            showAlertDialg(message);
            return false;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            showAlertDialg(LocaleUtils.getString("errorPrintLabel"));
            return false;
        }
    }
}
