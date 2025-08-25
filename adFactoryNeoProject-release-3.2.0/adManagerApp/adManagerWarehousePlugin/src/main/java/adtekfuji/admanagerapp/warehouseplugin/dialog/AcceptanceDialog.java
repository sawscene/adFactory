/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.dialog;

import adtekfuji.clientservice.WarehouseInfoFaced;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.search.MaterialCondition;
import jp.adtekfuji.adFactory.entity.warehouse.Location;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.enumeration.Verifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 受入ダイアログ
 *
 * @author s-morita
 */
@FxComponent(id = "AcceptanceDialog", fxmlPath = "/fxml/warehouseplugin/acceptance_dialog.fxml")
public class AcceptanceDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private final static String DEFAULT_UI_PROPERTY_NAME = "adManagerUI";

    private static final long MAX_STOCK_NUM = 999999;

    private Dialog dialog;
    private final Logger logger = LogManager.getLogger();
    private final WarehouseInfoFaced warehouseFaced = new WarehouseInfoFaced();
    private final LoginUserInfoEntity loginUserInfo = LoginUserInfoEntity.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();

    private long minStockNum = 1;

    @FXML
    private TextField supplyNo;
    @FXML
    private Label productNo;
    @FXML
    private Label productName;
    @FXML
    private Label locationNo;
    @FXML
    private Label lotNo;
    @FXML
    private Label arrivalNum;
    @FXML
    private Label unitLabel1;
    @FXML
    private TextField lotNoTextField;
    @FXML
    private RestrictedTextField stockNum;
    @FXML
    private Label unitLabel2;
    @FXML
    private ComboBox areaName;
    @FXML
    private CheckBox printLabelFlg;
    @FXML
    private Pane progressPane;
    @FXML
    private Button registButton;

    private TrnMaterialInfo materialInfo;
    private boolean isUpdate;
    private List<Location> locationList;

    // 資材情報が登録された場合に、在庫モニター画面の一覧を更新する
    private static boolean requestUpdate = false;
    
    /**
     * コンストラクタ
     */
    public AcceptanceDialog() {

    }

    /**
     * 初期化
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("initialize start.");

        this.stockNum.setVerifier(Verifier.NUMBER_ONLY);
        this.stockNum.setMaxLimit(MAX_STOCK_NUM);
        this.stockNum.textProperty().addListener((observable, oldValue, newValue) -> {
            this.enableRegistButton();
        });
        this.areaName.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.enableRegistButton();
        });
        
        requestUpdate = false;

        this.supplyNo.requestFocus();
    }

    /**
     * パラメータを設定する。
     *
     * @param argument パラメータ
     */
    @Override
    public void setArgument(Object argument) {

        try {
            AdProperty.load(DEFAULT_UI_PROPERTY_NAME, DEFAULT_UI_PROPERTY_NAME + ".properties");
            Properties properties = AdProperty.getProperties(DEFAULT_UI_PROPERTY_NAME);

            String defaultAreaName = properties.getProperty(Config.WAREHOUSE_AREA_NAME_DEFAULT, "");
            boolean defaultPrintFlg = Boolean.valueOf(properties.getProperty(Config.WAREHOUSE_PRINT_CHECK_DEFAULT, "true"));
            this.printLabelFlg.setSelected(defaultPrintFlg);

            // 区画名一覧
            List<String> areaNameList = this.warehouseFaced.findAllAreaName();
            this.areaName.getItems().addAll(areaNameList);
            if (areaNameList.contains(defaultAreaName)) {
                this.areaName.getSelectionModel().select(defaultAreaName);
            }
            
            if (Objects.isNull(argument)) {
                this.isUpdate = false;

                this.materialInfo = new TrnMaterialInfo();
                this.stockNum.setMinLimit(this.minStockNum);

            } else {

                List<TreeItem<TrnMaterialInfo>> list = (List<TreeItem<TrnMaterialInfo>>) argument;
                this.materialInfo = list.get(0).getValue();

                if (0 == this.materialInfo.getArrivalNum() && 0 < this.materialInfo.getBranchNo()) {
                    // 発注情報が存在しない場合
                    this.isUpdate = false;
                    this.materialInfo = new TrnMaterialInfo();
                    this.stockNum.setMinLimit(this.minStockNum);
                    this.enableRegistButton();
                    return;
                }

                this.isUpdate = true;
            
                this.supplyNo.setDisable(true);
                this.supplyNo.setText(this.materialInfo.getSupplyNo());
                this.productNo.setText(this.materialInfo.getProduct().getProductNo());
                this.wrapProductName(this.materialInfo.getProduct().getProductName());
                this.lotNo.setText(this.materialInfo.getPartsNo());
                // 発注残を表示
                this.arrivalNum.setText(String.valueOf(this.materialInfo.getArrivalNum() - (Objects.nonNull(this.materialInfo.getStockNum()) ? this.materialInfo.getStockNum() : 0)));
                this.unitLabel1.setText(this.materialInfo.getProduct().getUnit());
                //this.stockNum.setText(String.valueOf(this.materialInfo.getStockNum()));
                this.stockNum.setText("0");
                this.stockNum.setMinLimit(this.minStockNum);
                this.unitLabel2.setText(this.materialInfo.getProduct().getUnit());
                this.areaName.setValue(this.materialInfo.getLocation().getAreaName());
        
                //if (Objects.isNull(this.materialInfo.getArrivalDate())
                //        && StringUtils.isEmpty(this.materialInfo.getPartsNo())) {
                //
                //    // 受入されていない場合
                //    String partsNo = this.warehouseFaced.nextPartsNo();
                //    if (!StringUtils.isEmpty(partsNo)) {
                //        this.lotNo.setText(partsNo);
                //    }
                //
                //    this.areaName.setDisable(false);
                //}
            }

            this.enableRegistButton();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
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
            this.cancelDialog();
        });
    }

    /**
     * 発注番号が入力された。
     *
     * @param event キーイベント
     */
    @FXML
    public void onSupplyNo(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            this.initContents();
         
            List<TrnMaterialInfo> materials = this.warehouseFaced.searchMaterials(MaterialCondition.supplyNo(this.supplyNo.getText()), 0, 1);
            if (materials.isEmpty()) {
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Error"), LocaleUtils.getString("key.PartsNotFound"));
                this.supplyNo.clear();
                return;
            }

            TrnMaterialInfo material = materials.get(0);
            this.productNo.setText(material.getProduct().getProductNo());
            this.wrapProductName(material.getProduct().getProductName());
            this.locationList = material.getProduct().getLocationList();
            this.updateLocationNo();
            this.stockNum.setText(String.valueOf(material.getArrivalNum()));

            if (!this.isUpdate) {
                // ロット番号を取得
                String partsNo = this.warehouseFaced.nextPartsNo();
                if (!StringUtils.isEmpty(partsNo)) {
                    this.lotNo.setText(partsNo);

                } else {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Error"), LocaleUtils.getString("key.LotNoNotGet"));
                    return;
                }
            }
            
            this.enableRegistButton();

            this.stockNum.requestFocus();
        }
    }

    /**
     * 入力内容を初期化する。
     * 
     */
    private void initContents() {
        this.productNo.setText("");
        this.productName.setText("");
        this.locationNo.setText("");
        this.lotNoTextField.clear();
        this.lotNo.setText ("");
        this.stockNum.setText("");
        this.locationList = null;
    }
    
    /**
     * ロット番号フィールドにてEnterキーが押下された。
     *
     * @param event キーイベント
     */
    @FXML
    private void onLotNo(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            this.stockNum.requestFocus();
        }
    }

    /**
     * 入庫数フィールドにてEnterキーが押下された。
     *
     * @param event キーイベント
     */
    @FXML
    private void onStockNum(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            this.registButton.requestFocus();
        }
    }

    /**
     * 登録ボタン押下時処理
     *
     * @param event イベント
     */
    @FXML
    private void onRegistButton(ActionEvent event) {
        logger.info("onRegistrationButton start.");
        registProcess();
    }

    /**
     * 登録ボタン(Enter)押下時処理
     *
     * @param event イベント
     */
    @FXML
    private void onRegistButtonEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            logger.info("onRegistrationButtonEnter start.");
            registProcess();
        }
    }
    
    /**
     * 登録処理
     * 
     * @param owner 親画面 
     */
    private void registProcess() {
        logger.info("registProcess start.");
        ResponseEntity response = new ResponseEntity();

        try {
            int num = materialInfo.getStockNum() + Integer.valueOf(this.stockNum.getText());
            if (Objects.nonNull(this.materialInfo.getArrivalNum()) && this.materialInfo.getArrivalNum() > 0) {
                if (num > this.materialInfo.getArrivalNum()) {
                    if (ButtonType.CANCEL.equals(sc.showMessageBox(Alert.AlertType.WARNING, 
                            LocaleUtils.getString("key.Warning"), 
                            LocaleUtils.getString("exceedOrder"), 
                            new ButtonType[]{ButtonType.OK, ButtonType.CANCEL}, ButtonType.CANCEL))) {
                        return;
                    }
                }
            }
            
            response = this.warehouseFaced.reciveWarehouse(this.loginUserInfo.getLoginId(), this.supplyNo.getText(), Integer.valueOf(this.stockNum.getText()), 
                    this.areaName.getValue().toString(), this.locationNo.getText(), Objects.isNull(this.materialInfo.getArrivalDate()), 
                    num != this.materialInfo.getArrivalNum());
            
            if (response.isSuccess()) {
                logger.info("reciveWarehouse successed.");
                
                requestUpdate = true;
            
                try {
                    AdProperty.getProperties(DEFAULT_UI_PROPERTY_NAME).setProperty(Config.WAREHOUSE_AREA_NAME_DEFAULT, this.areaName.getValue().toString());
                    AdProperty.getProperties(DEFAULT_UI_PROPERTY_NAME).setProperty(Config.WAREHOUSE_PRINT_CHECK_DEFAULT, String.valueOf(this.printLabelFlg.isSelected()));
                    AdProperty.store(DEFAULT_UI_PROPERTY_NAME);
                } catch (IOException ex) {
                    logger.fatal(ex);
                }
                        
                // 印刷処理
                if (this.printLabelFlg.isSelected()) {
                    String _materialNo = Objects.nonNull(response.getResources()) ? (String) response.getResources() : this.materialInfo.getMaterialNo();
                    
                    List<TrnMaterialInfo> materialList = new ArrayList<>();
                    List<TrnMaterialInfo> list = warehouseFaced.searchMaterials(MaterialCondition.materialNo(_materialNo), null, null);
                    list.stream()
                            .filter(o -> StringUtils.equals(_materialNo, o.getMaterialNo()))
                            .findFirst()
                            .ifPresent(o -> materialList.add(o));
                    
                    ButtonType buttonType = sc.showDialog(LocaleUtils.getString("key.Printed"), "PrintDialog", materialList, 
                            (Stage)this.dialog.getDialogPane().getScene().getWindow());
                }

                if (!this.isUpdate) {
                    this.supplyNo.clear();

                    this.initContents();
                    this.enableRegistButton();

                    this.supplyNo.requestFocus();

                } else {
                    this.dialog.setResult(ButtonType.OK);
                    this.dialog.close();
                }

            } else {
                ResponseAnalyzer.getAnalyzeResult(response);
            }

        } catch (NumberFormatException ex) {
            ResponseAnalyzer.getAnalyzeResult(response);
            logger.fatal(ex, ex);

        } finally {
            logger.info("registProcess end.");
        }

    }
    
    /**
     * 品名の折り返し
     *
     * @param productName 品名
     */
    private void wrapProductName(String productName) {
        if (StringUtils.isEmpty(productName) || productName.length() <= 20) {
            this.productName.setText(productName);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(productName.substring(0, 20));
            sb.append("\r\n");
            sb.append(productName.substring(21));
            this.productName.setText(sb.toString());
        }
    }

    /**
     * 登録ボタンを有効化する。
     *
     * @return true:登録ボタンが有効化、false:登録ボタンが無効化
     */
    private boolean enableRegistButton() {
        try {
            if (StringUtils.isEmpty(this.supplyNo.getText())
                    || StringUtils.isEmpty(this.stockNum.getText())) {
                this.registButton.setDisable(true);
                return false;
            }

            int value = Integer.valueOf(this.stockNum.getText());
            if (value < this.minStockNum) {
                this.registButton.setDisable(true);
                return false;
            }
            
            if (Objects.isNull(this.areaName.getValue())
                    || StringUtils.isEmpty(this.areaName.getValue().toString())) {
                this.registButton.setDisable(true);
                return false;
            }

            this.registButton.setDisable(false);
            return true;

        } catch (NumberFormatException ex) {
            logger.fatal(ex, ex);
            this.registButton.setDisable(true);
            return false;
        }
    }

    /**
     * キャンセルボタン押下時処理
     *
     * @param event イベント
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
        this.cancelDialog();
    }

    /**
     * キャンセル処理
     *
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
     * 操作を無効にする。
     *
     * @param block true:操作を無効にする、false:操作を有効にする
     */
    private void blockUI(boolean block) {
        Platform.runLater(() -> {
            sc.getInstance().blockUI("ContentNaviPane", block);
            progressPane.setVisible(block);
        });
    }

    /**
     * 棚番号を更新する。
     */
    public void updateLocationNo() {
        this.locationNo.setText("");
        if (Objects.isNull(this.locationList)) {
            return;
        }

        for (Location loc : this.locationList) {
            if (StringUtils.equals(this.areaName.getValue().toString(), loc.getAreaName())) {
                this.locationNo.setText(loc.getLocationNo());
                break;
            }
        }
    }
    
    /**
     * 在庫モニター画面の一覧を更新するかどうかを返す。
     * 
     * @return true: 一覧を更新する、false: 一覧を更新しない
     */
    public static boolean isRequestUpdate() {
        return requestUpdate;
    }
}
