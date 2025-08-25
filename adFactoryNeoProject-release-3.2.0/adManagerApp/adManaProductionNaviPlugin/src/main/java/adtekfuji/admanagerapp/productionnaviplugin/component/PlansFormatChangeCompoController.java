/*
 * 予定表のフォーマット変更画面コントローラー
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ProductionNaviUtils;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 予定表フォーマット変更画面
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "PlansFormatChangeCompo", fxmlPath = "/fxml/compo/plans_format_compo.fxml")
public class PlansFormatChangeCompoController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
            
    private static final String CHARSET = "UTF-8";
    private static final String DELIMITER = "\\|";
    private static final int INPUT_MAX_SIZE = 3;
    
    /** CSV形式：入力エリア：エンコード **/
    @FXML
    private ComboBox<String> inputCsvEncode;
    /** CSV形式：入力エリア：開始行 **/
    @FXML
    private TextField inputCsvLineStart;
    /** CSV形式：入力エリア：組織識別名 **/
    @FXML
    private TextField inputCsvOrganization;
    /** CSV形式：入力エリア：予定開始日時 **/
    @FXML
    private TextField inputCsvStartDateTime;
    /** CSV形式：入力エリア：予定終了日時 **/
    @FXML
    private TextField inputCsvStopDateTime;
    /** CSV形式：入力エリア：予定 **/
    @FXML
    private TextField inputCsvPlansName;
    /** Excel形式：入力エリア：シート名 **/
    @FXML
    private TextField inputExcelSheetName;
    /** Excel形式：入力エリア：開始行 **/
    @FXML
    private TextField inputExcelLineStart;
    /** Excel形式：入力エリア：組織識別名 **/
    @FXML
    private TextField inputExcelOrganization;
    /** Excel形式：入力エリア：予定開始日時 **/
    @FXML
    private TextField inputExcelStartDateTime;
    /** Excel形式：入力エリア：予定終了日時 **/
    @FXML
    private TextField inputExcelStopDateTime;
    /** Excel形式：入力エリア：予定 **/
    @FXML
    private TextField inputExcelPlansName;
    
    /** 処理中 **/
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
        logger.info(":initialize start");

        //プロパティファイル読み込み.
        this.loadSetting();

        logger.info(":initialize end");
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        ProductionNaviUtils.setFieldNormal(this.inputCsvEncode);
        ProductionNaviUtils.setFieldNormal(this.inputCsvLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputCsvPlansName);
        ProductionNaviUtils.setFieldNormal(this.inputCsvStopDateTime);
        ProductionNaviUtils.setFieldNormal(this.inputCsvStartDateTime);
        ProductionNaviUtils.setFieldNormal(this.inputCsvOrganization);

        ProductionNaviUtils.setFieldNormal(this.inputExcelSheetName);
        ProductionNaviUtils.setFieldNormal(this.inputExcelLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputExcelStartDateTime);
        ProductionNaviUtils.setFieldNormal(this.inputExcelStopDateTime);
        ProductionNaviUtils.setFieldNormal(this.inputExcelPlansName);
        ProductionNaviUtils.setFieldNormal(this.inputExcelOrganization);
    }

    /**
     *
     * @param mode モード
     */
    private void blockUI(Boolean mode) {
        logger.info(":blockUI =>" + mode);
        progressPane.setVisible(mode);
    }

    /**
     * 登録ボタンのクリックイベント
     * 
     * @param enent 
     */
    @FXML
    private void onEntryAction(ActionEvent enent){
        logger.info(":onEntryAction start");
        
        // データ登録
        if(this.entryData()){
            sc.setComponent("ContentNaviPane", "PlansImportCompo");
        }
        
        logger.info(":onEntryAction end");
    }

    /**
     * キャンセルボタンのクリックイベント
     * 
     * @param enent 
     */
    @FXML
    private void onCancelAction(ActionEvent enent){
        logger.info(":onCancelAction start");
        boolean chancelFlg = false;
        
        // 入力データ変更チェック
        if(this.isData()){
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            // 保存して閉じる
            if (ButtonType.YES == buttonType) {
                // データ登録
                chancelFlg = !this.entryData();
            } else if (ButtonType.CANCEL == buttonType) {
                chancelFlg = true;
            }
        }
        
        if(!chancelFlg){
            sc.setComponent("ContentNaviPane", "PlansImportCompo");
        }
        logger.info(":onCancelAction end");
    }

    /**
     * データ登録処理
     * 
     * @return 
     */
    private boolean entryData(){
        boolean result = true;

        ProductionNaviUtils.setFieldNormal(this.inputCsvLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputCsvEncode);
        ProductionNaviUtils.setFieldNormal(this.inputCsvPlansName);
        ProductionNaviUtils.setFieldNormal(this.inputCsvStartDateTime);
        ProductionNaviUtils.setFieldNormal(this.inputCsvStopDateTime);
        ProductionNaviUtils.setFieldNormal(this.inputCsvOrganization);
        ProductionNaviUtils.setFieldNormal(this.inputExcelSheetName);
        ProductionNaviUtils.setFieldNormal(this.inputExcelPlansName);
        ProductionNaviUtils.setFieldNormal(this.inputExcelStartDateTime);
        ProductionNaviUtils.setFieldNormal(this.inputExcelStopDateTime);
        ProductionNaviUtils.setFieldNormal(this.inputExcelOrganization);

        Properties properties = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
        /** CSV形式：エンコード **/
        if(!ProductionNaviUtils.isNotNull(this.inputCsvEncode)){
            logger.warn("CSV形式：エンコード Error");
            result = false;
        }
        /** CSV形式：開始行 **/
        if(!ProductionNaviUtils.isNumber(this.inputCsvLineStart)){
            logger.warn("CSV形式：開始行 Error");
            result = false;
        }else{
            try{
                if(Integer.parseInt(this.inputCsvLineStart.getText()) < 1 || Integer.parseInt(this.inputCsvLineStart.getText()) > 999){
                    ProductionNaviUtils.setFieldError(this.inputCsvLineStart);
                    result = false;
                }else{
                    ProductionNaviUtils.setFieldNormal(this.inputCsvLineStart);
                }
            }catch(NumberFormatException e){
                ProductionNaviUtils.setFieldError(this.inputCsvLineStart);
                result = false;
            }
        }
        /** CSV形式:予定名 **/
        if(!ProductionNaviUtils.isNumber(this.inputCsvPlansName)){
            logger.warn("CSV形式：予定名 Error");
            result = false;
        }else{
            try{
                if(Integer.parseInt(this.inputCsvPlansName.getText()) < 1 || Integer.parseInt(this.inputCsvPlansName.getText()) > 999){
                    ProductionNaviUtils.setFieldError(this.inputCsvPlansName);
                    result = false;
                }else{
                    ProductionNaviUtils.setFieldNormal(this.inputCsvPlansName);
                }
            }catch(NumberFormatException e){
                ProductionNaviUtils.setFieldError(this.inputCsvPlansName);
                result = false;
            }
        }
        /** CSV形式:予定開始日時 **/
        if(!ProductionNaviUtils.isNumber(this.inputCsvStartDateTime)){
            logger.warn("CSV形式：予定開始日時 Error");
            result = false;
        }else{
            try{
                if(Integer.parseInt(this.inputCsvStartDateTime.getText()) < 1 || Integer.parseInt(this.inputCsvStartDateTime.getText()) > 999){
                    ProductionNaviUtils.setFieldError(this.inputCsvStartDateTime);
                    result = false;
                }else{
                    ProductionNaviUtils.setFieldNormal(this.inputCsvStartDateTime);
                }
            }catch(NumberFormatException e){
                ProductionNaviUtils.setFieldError(this.inputCsvStartDateTime);
                result = false;
            }
        }

        /** CSV形式:予定終了日時 **/
        if(!ProductionNaviUtils.isNumber(this.inputCsvStopDateTime)){
            logger.warn("CSV形式：予定終了日時 Error");
            result = false;
        }else{
            try{
                if(Integer.parseInt(this.inputCsvStopDateTime.getText()) < 1 || Integer.parseInt(this.inputCsvStopDateTime.getText()) > 999){
                    ProductionNaviUtils.setFieldError(this.inputCsvStopDateTime);
                    result = false;
                }else{
                    ProductionNaviUtils.setFieldNormal(this.inputCsvStopDateTime);
                }
            }catch(NumberFormatException e){
                ProductionNaviUtils.setFieldError(this.inputCsvStopDateTime);
                result = false;
            }
        }
        /** CSV形式:組織識別名 **/
        if(!ProductionNaviUtils.isNumber(this.inputCsvOrganization)){
            logger.warn("CSV形式：組織識別名 Error");
            result = false;
        }else{
            try{
                if(Integer.parseInt(this.inputCsvOrganization.getText()) < 1 || Integer.parseInt(this.inputCsvOrganization.getText()) > 999){
                    ProductionNaviUtils.setFieldError(this.inputCsvOrganization);
                    result = false;
                }else{
                    ProductionNaviUtils.setFieldNormal(this.inputCsvOrganization);
                }
            }catch(NumberFormatException e){
                ProductionNaviUtils.setFieldError(this.inputCsvOrganization);
                result = false;
            }
        }

        /** Excel形式：シート名 **/
        if(!ProductionNaviUtils.isNotNull(this.inputExcelSheetName)){
            logger.warn("Excel形式：シート名 Error");
            result = false;
        }
        /** Excel形式：開始行 **/
        if(!ProductionNaviUtils.isNumber(this.inputExcelLineStart)){
            logger.warn("Excel形式：開始行 Error");
            result = false;
        }else{
            try{
                if(Integer.parseInt(this.inputExcelLineStart.getText()) < 1 || Integer.parseInt(this.inputExcelLineStart.getText()) > 999){
                    ProductionNaviUtils.setFieldError(this.inputExcelLineStart);
                    result = false;
                }else{
                    ProductionNaviUtils.setFieldNormal(this.inputExcelLineStart);
                }
            }catch(NumberFormatException e){
                ProductionNaviUtils.setFieldError(this.inputExcelLineStart);
                result = false;
            }
        }
        /** Excel形式:予定名 **/
        if(!ProductionNaviUtils.isAlphabet(this.inputExcelPlansName, INPUT_MAX_SIZE)){
            logger.warn("Excel形式：予定名 Error");
            result = false;
        }
        
        /** Excel形式:予定開始日時 **/
        if(!ProductionNaviUtils.isAlphabet(this.inputExcelStartDateTime, INPUT_MAX_SIZE)){
            logger.warn("Excel形式：予定開始日時 Error");
            result = false;
        }
        /** Excel形式:予定終了日時 **/
        if(!ProductionNaviUtils.isAlphabet(this.inputExcelStopDateTime, INPUT_MAX_SIZE)){
            logger.warn("Excel形式：予定終了日時 Error");
            result = false;
        }
        /** Excel形式:組織識別名 **/
        if(!ProductionNaviUtils.isAlphabet(this.inputExcelOrganization, INPUT_MAX_SIZE)){
            logger.warn("Excel形式：組織識別名 Error");
            result = false;
        }

        // 入力値正常なら、項目のポジションに重複がないかチェック
        if(result){
            if(this.inputCsvPlansName.getText().equals(this.inputCsvStartDateTime.getText())){
                ProductionNaviUtils.setFieldError(this.inputCsvPlansName);
                ProductionNaviUtils.setFieldError(this.inputCsvStartDateTime);
                result = false;
            }
            if(this.inputCsvPlansName.getText().equals(this.inputCsvStopDateTime.getText())){
                ProductionNaviUtils.setFieldError(this.inputCsvPlansName);
                ProductionNaviUtils.setFieldError(this.inputCsvStopDateTime);
                result = false;
            }
            if(this.inputCsvPlansName.getText().equals(this.inputCsvOrganization.getText())){
                ProductionNaviUtils.setFieldError(this.inputCsvPlansName);
                ProductionNaviUtils.setFieldError(this.inputCsvOrganization);
                result = false;
            }
            if(this.inputCsvStartDateTime.getText().equals(this.inputCsvStopDateTime.getText())){
                ProductionNaviUtils.setFieldError(this.inputCsvStartDateTime);
                ProductionNaviUtils.setFieldError(this.inputCsvStopDateTime);
                result = false;
            }
            if(this.inputCsvStartDateTime.getText().equals(this.inputCsvOrganization.getText())){
                ProductionNaviUtils.setFieldError(this.inputCsvStartDateTime);
                ProductionNaviUtils.setFieldError(this.inputCsvOrganization);
                result = false;
            }
            if(this.inputCsvStopDateTime.getText().equals(this.inputCsvOrganization.getText())){
                ProductionNaviUtils.setFieldError(this.inputCsvStopDateTime);
                ProductionNaviUtils.setFieldError(this.inputCsvOrganization);
                result = false;
            }
            
            logger.debug("重複チェック(Excel):" + this.inputExcelPlansName.getText() + " ? " + this.inputExcelStartDateTime.getText() + " => " + this.inputExcelPlansName.getText().equals(this.inputExcelStartDateTime.getText()));
            logger.debug("重複チェック(Excel):" + this.inputExcelPlansName.getText() + " ? " + this.inputExcelStopDateTime.getText() + " => "  + this.inputExcelPlansName.getText().equals(this.inputExcelStopDateTime.getText()));
            logger.debug("重複チェック(Excel):" + this.inputExcelPlansName.getText() + " ? " + this.inputExcelOrganization.getText() + " => "  + this.inputExcelPlansName.getText().equals(this.inputExcelOrganization.getText()));
            logger.debug("重複チェック(Excel):" + this.inputExcelStartDateTime.getText() + " ? " + this.inputExcelStopDateTime.getText() + " => "  + this.inputExcelStartDateTime.getText().equals(this.inputExcelStopDateTime.getText()));
            logger.debug("重複チェック(Excel):" + this.inputExcelStartDateTime.getText() + " ? " + this.inputExcelOrganization.getText() + " => "  + this.inputExcelStartDateTime.getText().equals(this.inputExcelOrganization.getText()));
            logger.debug("重複チェック(Excel):" + this.inputExcelStopDateTime.getText() + " ? " + this.inputExcelOrganization.getText() + " => "  + this.inputExcelStopDateTime.getText().equals(this.inputExcelOrganization.getText()));
            if(this.inputExcelPlansName.getText().toUpperCase().equals(this.inputExcelStartDateTime.getText().toUpperCase())){
                ProductionNaviUtils.setFieldError(this.inputExcelPlansName);
                ProductionNaviUtils.setFieldError(this.inputExcelStartDateTime);
                result = false;
            }
            if(this.inputExcelPlansName.getText().equals(this.inputExcelStopDateTime.getText().toUpperCase())){
                ProductionNaviUtils.setFieldError(this.inputExcelPlansName);
                ProductionNaviUtils.setFieldError(this.inputExcelStopDateTime);
                result = false;
            }
            if(this.inputExcelPlansName.getText().toUpperCase().equals(this.inputExcelOrganization.getText().toUpperCase())){
                ProductionNaviUtils.setFieldError(this.inputExcelPlansName);
                ProductionNaviUtils.setFieldError(this.inputExcelOrganization);
                result = false;
            }
            if(this.inputExcelStartDateTime.getText().toUpperCase().equals(this.inputExcelStopDateTime.getText().toUpperCase())){
                ProductionNaviUtils.setFieldError(this.inputExcelStartDateTime);
                ProductionNaviUtils.setFieldError(this.inputExcelStopDateTime);
                result = false;
            }
            if(this.inputExcelStartDateTime.getText().toUpperCase().equals(this.inputExcelOrganization.getText().toUpperCase())){
                ProductionNaviUtils.setFieldError(this.inputExcelStartDateTime);
                ProductionNaviUtils.setFieldError(this.inputExcelOrganization);
                result = false;
            }
            if(this.inputExcelStopDateTime.getText().toUpperCase().equals(this.inputExcelOrganization.getText().toUpperCase())){
                ProductionNaviUtils.setFieldError(this.inputExcelStopDateTime);
                ProductionNaviUtils.setFieldError(this.inputExcelOrganization);
                result = false;
            }
        }
            
        if(result){
            try {
                properties.setProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ENCODE, this.inputCsvEncode.getSelectionModel().getSelectedItem());
                properties.setProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_LINE, this.inputCsvLineStart.getText());
                properties.setProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_PLANS_NAME, this.inputCsvPlansName.getText());
                properties.setProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_START_DT, this.inputCsvStartDateTime.getText());
                properties.setProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_STOP_DT, this.inputCsvStopDateTime.getText());
                properties.setProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ORGANIZATION, this.inputCsvOrganization.getText());

                properties.setProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_SHEET_NAME, this.inputExcelSheetName.getText());
                properties.setProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_LINE, this.inputExcelLineStart.getText());
                properties.setProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_PLANS_NAME, this.inputExcelPlansName.getText());
                properties.setProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_START_DT, this.inputExcelStartDateTime.getText());
                properties.setProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_STOP_DT, this.inputExcelStopDateTime.getText());
                properties.setProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_ORGANIZATION, this.inputExcelOrganization.getText());
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            } catch (IOException ex) {
                logger.fatal(ex, ex);
                result = false;
            }
        }
        
        return result;
    }
    
    /**
     * 入力データ変更チェック処理
     * 
     * @return 
     */
    private boolean isData(){
        boolean changeFlg = false;
        
        try{
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties properties = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

            if( !this.inputCsvEncode.getSelectionModel().getSelectedItem().equals(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ENCODE, ProductionNaviPropertyConstants.INIT_PLANS_CSV_ENCODE))){
                changeFlg = true;
            }
            if( !this.inputCsvLineStart.getText().equals(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_LINE, ProductionNaviPropertyConstants.INIT_PLANS_CSV_LINE))){
                changeFlg = true;
            }
            if( !this.inputCsvPlansName.getText().equals(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_PLANS_NAME, ProductionNaviPropertyConstants.INIT_PLANS_CSV_PLANS_NAME))){
                changeFlg = true;
            }
            if( !this.inputCsvStartDateTime.getText().equals(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_START_DT, ProductionNaviPropertyConstants.INIT_PLANS_CSV_START_DT))){
                changeFlg = true;
            }
            if( !this.inputCsvStopDateTime.getText().equals(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_STOP_DT, ProductionNaviPropertyConstants.INIT_PLANS_CSV_STOP_DT))){
                changeFlg = true;
            }
            if( !this.inputCsvOrganization.getText().equals(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ORGANIZATION, ProductionNaviPropertyConstants.INIT_PLANS_CSV_ORGANIZATION))){
                changeFlg = true;
            }
            if( !this.inputExcelSheetName.getText().equals(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_SHEET_NAME, ProductionNaviPropertyConstants.INIT_PLANS_XLS_SHEET_NAME))){
                changeFlg = true;
            }
            if( !this.inputExcelLineStart.getText().equals(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_LINE, ProductionNaviPropertyConstants.INIT_PLANS_XLS_LINE))){
                changeFlg = true;
            }
            if( !this.inputExcelPlansName.getText().equals(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_PLANS_NAME, ProductionNaviPropertyConstants.INIT_PLANS_XLS_PLANS_NAME))){
                changeFlg = true;
            }
            if( !this.inputExcelStartDateTime.getText().equals(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_START_DT, ProductionNaviPropertyConstants.INIT_PLANS_XLS_START_DT))){
                changeFlg = true;
            }
            if( !this.inputExcelStopDateTime.getText().equals(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_STOP_DT, ProductionNaviPropertyConstants.INIT_PLANS_XLS_STOP_DT))){
                changeFlg = true;
            }
            if( !this.inputExcelOrganization.getText().equals(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_ORGANIZATION, ProductionNaviPropertyConstants.INIT_PLANS_XLS_ORGANIZATION))){
                changeFlg = true;
            }
        }catch(Exception ex){
            logger.fatal(ex, ex);
            changeFlg = true;
        }        
        return changeFlg;
    }
    
    /**
     * 設定情報読み込み処理
     */
    private void loadSetting(){
        logger.info(":loadSetting start");
        this.blockUI(true);

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logger.info(":loadSetting task start");
                try {
                    loadPlansSetting();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    logger.info(":loadSetting task end");
                    blockUI(false);
                }
                return null;
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                blockUI(false);
            }
        };
        new Thread(task).start();

        logger.info(":loadSetting end");
    }
    
    /**
     * 入力エリアに設定
     */
    private void loadPlansSetting(){
        logger.info(":loadPlansSetting start");

        try{
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties properties = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            
            logger.debug(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ENCODE + "=>"+ properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ENCODE));
            logger.debug(ProductionNaviPropertyConstants.KEY_PLANS_CSV_LINE + "=>"+ properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_LINE));
            logger.debug(ProductionNaviPropertyConstants.KEY_PLANS_CSV_PLANS_NAME + "=>"+ properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_PLANS_NAME));
            logger.debug(ProductionNaviPropertyConstants.KEY_PLANS_CSV_START_DT + "=>"+ properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_START_DT));
            logger.debug(ProductionNaviPropertyConstants.KEY_PLANS_CSV_STOP_DT + "=>"+ properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_STOP_DT));
            logger.debug(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ORGANIZATION + "=>"+ properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ORGANIZATION));
            logger.debug(ProductionNaviPropertyConstants.KEY_PLANS_XLS_SHEET_NAME + "=>"+ properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_SHEET_NAME));
            logger.debug(ProductionNaviPropertyConstants.KEY_PLANS_XLS_LINE + "=>"+ properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_LINE));
            logger.debug(ProductionNaviPropertyConstants.KEY_PLANS_XLS_PLANS_NAME + "=>"+ properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_PLANS_NAME));
            logger.debug(ProductionNaviPropertyConstants.KEY_PLANS_XLS_START_DT + "=>"+ properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_START_DT));
            logger.debug(ProductionNaviPropertyConstants.KEY_PLANS_XLS_STOP_DT + "=>"+ properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_STOP_DT));
            logger.debug(ProductionNaviPropertyConstants.KEY_PLANS_XLS_ORGANIZATION + "=>"+ properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_ORGANIZATION));

            String encodeDatas = properties.getProperty(ProductionNaviPropertyConstants.KEY_MASTER_CSV_FILE_ENCODE, ProductionNaviPropertyConstants.MASTER_CSV_FILE_ENCODE);
            if(!encodeDatas.isEmpty()){
                String[] buff = encodeDatas.split(",");
                for(String encodeData : buff){
                    this.inputCsvEncode.getItems().add(encodeData);
                }
            }
            this.inputCsvEncode.getSelectionModel().select(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ENCODE, ProductionNaviPropertyConstants.INIT_PLANS_CSV_ENCODE));
            this.inputCsvLineStart.setText(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_LINE, ProductionNaviPropertyConstants.INIT_PLANS_CSV_LINE));
            this.inputCsvPlansName.setText(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_PLANS_NAME, ProductionNaviPropertyConstants.INIT_PLANS_CSV_PLANS_NAME));
            this.inputCsvStartDateTime.setText(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_START_DT, ProductionNaviPropertyConstants.INIT_PLANS_CSV_START_DT));
            this.inputCsvStopDateTime.setText(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_STOP_DT, ProductionNaviPropertyConstants.INIT_PLANS_CSV_STOP_DT));
            this.inputCsvOrganization.setText(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ORGANIZATION, ProductionNaviPropertyConstants.INIT_PLANS_CSV_ORGANIZATION));
            this.inputExcelSheetName.setText(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_SHEET_NAME, ProductionNaviPropertyConstants.INIT_PLANS_XLS_SHEET_NAME));
            this.inputExcelLineStart.setText(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_LINE, ProductionNaviPropertyConstants.INIT_PLANS_XLS_LINE));
            this.inputExcelPlansName.setText(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_PLANS_NAME, ProductionNaviPropertyConstants.INIT_PLANS_XLS_PLANS_NAME));
            this.inputExcelStartDateTime.setText(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_START_DT, ProductionNaviPropertyConstants.INIT_PLANS_XLS_START_DT));
            this.inputExcelStopDateTime.setText(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_STOP_DT, ProductionNaviPropertyConstants.INIT_PLANS_XLS_STOP_DT));
            this.inputExcelOrganization.setText(properties.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_ORGANIZATION, ProductionNaviPropertyConstants.INIT_PLANS_XLS_ORGANIZATION));
        }catch(Exception ex) {
            logger.error(ex.getMessage());
            logger.fatal(ex, ex);
        }finally{
            logger.info(":loadPlansSetting end");
        }
    }
    
            
}
