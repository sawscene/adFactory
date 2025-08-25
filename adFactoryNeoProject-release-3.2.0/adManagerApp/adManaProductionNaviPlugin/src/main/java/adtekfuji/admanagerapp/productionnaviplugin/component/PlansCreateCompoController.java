/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.dialog.WorkPlanOrganizationSelectDialog;
import adtekfuji.admanagerapp.productionnaviplugin.entity.PlansInfoEntity;
import adtekfuji.admanagerapp.productionnaviplugin.javafx.list.cell.WorkPlanOrganizationListItemCell;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ProductionNaviUtils;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.javafxcommon.controls.TimeTextField;
import jp.adtekfuji.javafxcommon.validator.DateTimeValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 予定表登録ダイアログ
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "PlansCreateCompo", fxmlPath = "/fxml/compo/plans_edit_dialog.fxml")
public class PlansCreateCompoController implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
//    private final OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
//    private final ScheduleInfoFacade scheduleInfoFacade = new ScheduleInfoFacade();

    private PlansInfoEntity entity;

    private Dialog dialog;

    /** 組織の選択 **/
    private final List<OrganizationInfoEntity> selectOrganizations = new ArrayList<>();

//    private final static String DEFAULT_TIME_START = "00:00:00";
//    private final static String DEFAULT_TIME_STOP = "23:59:59";
//    private final static String DATETIME_REGEX = "\\d|:|/|-|\\s";

    /** 予定開始日時と終了日時 **/
    private final ObjectProperty<LocalDate> startDateProperty;
    private final ObjectProperty<Date> startTimeProperty;
    private final ObjectProperty<LocalDate> stopDateProperty;
    private final ObjectProperty<Date> stopTimeProperty;

    /** タイトル **/
    @FXML
    private Label titleName;

    /** 予定開始日 **/
    @FXML
    private DatePicker inputStartDate;
    /** 予定開始時間 **/
    @FXML
    private TimeTextField inputStartTime;
    /** 予定終了日 **/
    @FXML
    private DatePicker inputStopDate;
    /** 予定終了時間 **/
    @FXML
    private TimeTextField inputStopTime;
    /** 予定名 **/
    @FXML
    private ComboBox inputPlansName;
    /** 組織一覧 **/
    @FXML
    private ListView<OrganizationInfoEntity> inputOrganizations;
    @FXML
    private Button selectButton;

    /**
     * コンストラクタ
     */
    public PlansCreateCompoController(){
        this.startDateProperty = new SimpleObjectProperty();
        this.startTimeProperty = new SimpleObjectProperty();
        this.stopDateProperty = new SimpleObjectProperty();
        this.stopTimeProperty = new SimpleObjectProperty();
    }
    
    /**
     * 初期化処理
     * 
     * @param url URL
     * @param rb リソース
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(":initialize start");
        try{
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            String masterDatas = prop.getProperty(ProductionNaviPropertyConstants.MASTER_HOLIDAY_NAME);
            if(Objects.isNull(masterDatas) || masterDatas.isEmpty()){
                masterDatas = ProductionNaviPropertyConstants.INIT_MASTER_HOLIDAY_NAME;
                prop.setProperty(ProductionNaviPropertyConstants.MASTER_HOLIDAY_NAME, masterDatas);
            }

            String[] masterData = masterDatas.split(","); 
            ObservableList<String> masterList = FXCollections.observableArrayList(masterData);
            this.logger.debug(" stateList -->" + masterList.size());
            this.inputPlansName.setItems(masterList);

            prop.setProperty(ProductionNaviPropertyConstants.MASTER_HOLIDAY_NAME, masterDatas);
            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        Callback<ListView<OrganizationInfoEntity>, ListCell<OrganizationInfoEntity>> organizationCellFactory = (ListView<OrganizationInfoEntity> param) -> new WorkPlanOrganizationListItemCell();
        inputOrganizations.setCellFactory(organizationCellFactory);

        logger.info(":initialize end");
    }

    /**
     * 
     * @param argument 
     */
    @Override
    public void setArgument(Object argument) {
        logger.info(":setArgument start");
        if (!(argument instanceof PlansInfoEntity)) {
            this.logger.debug("not data");
            return;
        }
        
        this.entity = (PlansInfoEntity) argument;
        this.titleName.setText(this.entity.getTitle());
        logger.debug(" entity:" + this.entity.toString());
        if(Objects.isNull(this.entity.getScheduleName())){
            this.entity.setScheduleName("");
        }
        logger.debug(" entity.getScheduleName:" + this.entity.getScheduleName());
        logger.debug(" entity.getScheduleFromDate:" + (Objects.isNull(this.entity.getScheduleFromDate()) ? null : this.entity.getScheduleFromDate().toString()));
        logger.debug(" entity.getScheduleFromDate:" + (Objects.isNull(this.entity.getScheduleToDate()) ? null : this.entity.getScheduleToDate().toString()));
        logger.debug(" entity.getScheduleId:" + this.entity.getScheduleId());
        logger.debug(" entity.getFkOrganization:" + (Objects.isNull(this.entity.getFkOrganization()) ? null : this.entity.getFkOrganization().size()));
    
        if(this.entity.getFkOrganization() != null && this.entity.getFkOrganization().size() > 0){
            logger.debug(" entity.getOrganizationName:" + this.entity.getFkOrganization().get(0).toString());
        }
        
        Callback<ListView<OrganizationInfoEntity>, ListCell<OrganizationInfoEntity>> organizationCellFactory = (ListView<OrganizationInfoEntity> param) -> new WorkPlanOrganizationListItemCell();
        inputOrganizations.setCellFactory(organizationCellFactory);

        SimpleDateFormat timeFormat = new SimpleDateFormat(LocaleUtils.getString("key.TimeFormat"));

        // 入力エリアに設定
        this.inputPlansName.setValue(this.entity.getScheduleName());
        
        // 予定開始日時
        this.startDateProperty.set(DateUtils.toLocalDate(Objects.nonNull(this.entity.getScheduleFromDate()) ? this.entity.getScheduleFromDate() : new Date()) );
        this.startTimeProperty.set(Objects.nonNull(this.entity.getScheduleFromDate()) ? this.entity.getScheduleFromDate() : new Date());
        this.inputStartDate.valueProperty().bindBidirectional(this.startDateProperty);
        DateTimeValidator.bindValidator(this.inputStartTime, this.startTimeProperty, timeFormat);

        // 予定終了日時
        this.stopDateProperty.set(DateUtils.toLocalDate(Objects.nonNull(this.entity.getScheduleToDate()) ? this.entity.getScheduleToDate() : new Date()) );
        this.stopTimeProperty.set(Objects.nonNull(this.entity.getScheduleToDate()) ? this.entity.getScheduleToDate() : new Date());
        this.inputStopDate.valueProperty().bindBidirectional(this.stopDateProperty);
        DateTimeValidator.bindValidator(this.inputStopTime, this.stopTimeProperty, timeFormat);

        // 組織
        this.selectOrganizations.clear();
        this.inputOrganizations.getItems().clear();
        logger.debug(" 組織:" + String.valueOf(Objects.isNull(this.entity.getFkOrganization()) ? -1 : this.entity.getFkOrganization().size()) );
        if (this.entity.getFkOrganization() != null){
            this.selectOrganizations.addAll(this.entity.getFkOrganization());
            this.inputOrganizations.getItems().addAll(this.entity.getFkOrganization());
        }

        // 編集時は組織の変更は不可
        if (Objects.nonNull(entity.getScheduleId())) {
            this.selectButton.setDisable(true);
        }
        
        ProductionNaviUtils.setFieldNormal(this.inputStartDate);
        ProductionNaviUtils.setFieldNormal(this.inputStopDate);
        ProductionNaviUtils.setFieldNormal(this.inputPlansName);
        ProductionNaviUtils.setFieldNormal(this.inputOrganizations);

        logger.info(":setArgument end");
    }
    
    /**
     * 
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog){
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancelDialog(false);
        });
    }

    /**
     * 組織の選択ボタン
     * 
     * @param event イベント
     */
    @FXML
    public void onSelectOrganization(ActionEvent event) {
        logger.info(":onSelectOrganization start");

        sc.blockUI(Boolean.TRUE);
        try {
            List<OrganizationInfoEntity> org = new ArrayList(this.selectOrganizations);
//            if(Objects.nonNull(this.selectOrganizations)){
//                for(OrganizationInfoEntity data : this.selectOrganizations){
//                    logger.info(" dats:" + data.toString());
//                }
//            }
//            SelectDialogEntity selectDialogEntity = new SelectDialogEntity().organizations(this.selectOrganizations);
//            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialogEntity);
//            logger.info(" check => " + selectDialogEntity.getOrganizations().size());
            Button eventSrc = (Button) event.getSource();
            eventSrc.setUserData(org);
            
            ButtonType ret = WorkPlanOrganizationSelectDialog.showDialog((ActionEvent) event);
            this.logger.debug(" >>> ret:" + ret.toString());

            if (ret.equals(ButtonType.OK)) {
                this.selectOrganizations.clear();
                this.inputOrganizations.getItems().clear();
                this.selectOrganizations.addAll((List) eventSrc.getUserData());
                this.inputOrganizations.getItems().addAll(this.selectOrganizations);
            }else{
                this.selectOrganizations.clear();
                this.selectOrganizations.addAll(org);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            sc.blockUI(Boolean.FALSE);
        }
        logger.info(":onSelectOrganization end");
    }

    /**
     * OKボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onOkButton(ActionEvent event) {
        logger.info(":onOkButton start");
        this.save();
        logger.info(":onOkButton end");
    }
    

    /**
     * キャンセルボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
        logger.info(":onCancelButton start");
        this.cancelDialog(false);
        logger.info(":onCancelButton end");
    }
    
    /**
     * データチェック処理
     * 
     * @return 
     */
    private boolean save(){
        
        // 入力チェック
        if(!this.isInputCheck()){
            logger.debug(" input error");
            return false;
        }

        try {
            Date plansFromDateTime = DateUtils.toDate(this.startDateProperty.get(), DateUtils.toLocalTime(this.startTimeProperty.get()));
            Date plansToDateTime = DateUtils.toDate(this.stopDateProperty.get(), DateUtils.toLocalTime(this.stopTimeProperty.get()));
            logger.debug(" plansFromDateTime:" + plansFromDateTime.toString());
            logger.debug(" plansToDateTime:" + plansToDateTime.toString());
            
            this.entity.setScheduleName((String) this.inputPlansName.getValue());
            this.entity.setScheduleFromDate(plansFromDateTime);
            this.entity.setScheduleToDate(plansToDateTime);
            this.entity.setFkOrganization(this.inputOrganizations.getItems());

            logger.debug(" data Check");
            logger.debug(" inputStartDate Date:" + DateUtils.toDate(this.inputStartDate.getValue()));
            logger.debug(" inputStopDate Date:" + DateUtils.toDate(this.inputStopDate.getValue()));

            logger.debug("   setScheduleName:" + this.entity.getScheduleName());
            logger.debug("   getScheduleFromDate:" + this.entity.getScheduleFromDate().toString());
            logger.debug("   getScheduleToDate:" + this.entity.getScheduleToDate().toString());
            logger.debug("   setFkOrganization:" + this.entity.getFkOrganization().size());
            
//            logger.info(" input data");
//            logger.info("   inputPlansName:" + this.inputPlansName.getValue());
//            logger.info("   inputStartDate:" + this.inputStartDate.toString());
//            logger.info("   inputStopDate:" + this.inputStopDate.toString());
//            logger.info("   inputStartTime Time:" + this.inputStartTime.getText());
//            logger.info("   inputStopTime Time:" + this.inputStopTime.getText());
//            logger.info("   startTimeProperty:" + this.startTimeProperty.get().toString());
//            logger.info("   stopTimeProperty:" + this.stopTimeProperty.get().toString());
//            logger.info("   inputOrganizations:" + this.inputOrganizations.getItems().size());

            try{
                AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
                Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_INIT_MODE, "true");
                AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
            }catch(IOException ex){
                logger.fatal(ex, ex);
            }finally{
                this.dialog.setResult(ButtonType.OK);
                this.dialog.close();
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        
        return true;
    }

    /**
     * キャンセル処理
     * 
     * @param mode 強制終了モード
     */
    private void cancelDialog(boolean mode) {
        logger.info(":cancelDialog start");
        try {
            logger.debug(" check mode=" + mode + ", change=" + this.isChange());
            if(!mode && this.isChange()){
                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE
                        , LocaleUtils.getString("key.confirm"), LocaleUtils.getString("key.confirm.destroy")
                        , new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                
                if (ButtonType.YES == buttonType) {
                    this.save();
                } else if (ButtonType.NO == buttonType) {
                    try{
                        AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
                        Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
                        prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_INIT_MODE, "true");
                        AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
                    }catch(IOException ex){
                        logger.fatal(ex, ex);
                    }finally{
                        this.dialog.setResult(ButtonType.CANCEL);
                        this.dialog.close();
                    }
                }
            }else{
                this.dialog.setResult(ButtonType.CANCEL);
                this.dialog.close();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(":cancelDialog end");
    }

    /**
     * 入力チェック
     * 
     * @return 結果
     */
    private boolean isInputCheck(){
        logger.info(":isInput start");
        boolean value = true;
        
        ProductionNaviUtils.setFieldNormal(this.inputStartDate);
        ProductionNaviUtils.setFieldNormal(this.inputStopDate);
        ProductionNaviUtils.setFieldNormal(this.inputPlansName);
        ProductionNaviUtils.setFieldNormal(this.inputOrganizations);

        logger.debug(" > inputStartDate:" + (Objects.isNull(this.inputStartDate.getValue()) ? null : this.inputStartDate.getValue()));
        if( Objects.isNull(this.inputStartDate.getValue()) ){
            logger.debug(" > 予定開始日:未入力");
            if(value){
                this.inputStartDate.requestFocus();
            }
            
            ProductionNaviUtils.setFieldError(this.inputStartDate);
            value = false;
        }else{
            ProductionNaviUtils.setFieldNormal(this.inputStartDate);
        }

        logger.debug(" > inputStopDate:" + (Objects.isNull(this.inputStopDate.getValue()) ? null : this.inputStopDate.getValue()));
        if( Objects.isNull(this.inputStopDate.getValue()) ){
            logger.debug(" > 予定終了日:未入力");
            if(value){
                this.inputStopDate.requestFocus();
            }
            
            ProductionNaviUtils.setFieldError(this.inputStopDate);
            value = false;
        }else{
            ProductionNaviUtils.setFieldNormal(this.inputStartDate);
        }

        logger.debug(" > inputPlansName:" + (Objects.isNull(this.inputPlansName) ? null : this.inputPlansName.getValue()));
        if( Objects.isNull(this.inputPlansName.getValue()) || this.inputPlansName.getValue().equals("")){
            logger.debug(" > 予定名:未入力");
            if(value){
                this.inputPlansName.requestFocus();
            }
            
            ProductionNaviUtils.setFieldError(this.inputPlansName);
            value = false;
        }else{
            if(this.inputPlansName.getValue().toString().length() > 256){
                logger.debug(" > 予定名:文字数オーバー");
                ProductionNaviUtils.setFieldError(this.inputPlansName);
                if(value){
                    this.inputPlansName.requestFocus();
                }
                value = false;
            }else{
                ProductionNaviUtils.setFieldNormal(this.inputPlansName);
            }
        }
        
        logger.debug(" > inputOrganizations:" + (Objects.isNull(this.inputOrganizations) ? null : this.inputOrganizations.getItems().size()));
        if( this.inputOrganizations.getItems().isEmpty()){
            logger.debug(" > 組織:未選択");
            if(value){
                this.inputOrganizations.requestFocus();
            }
            ProductionNaviUtils.setFieldError(this.inputOrganizations);
            value = false;
        }
        
        if(Objects.nonNull(this.inputStartDate.getValue()) && Objects.nonNull(this.inputStopDate.getValue())){
            Date dateStart = DateUtils.toDate(this.startDateProperty.get(), DateUtils.toLocalTime(this.startTimeProperty.get()));
            Date dateStop = DateUtils.toDate(this.stopDateProperty.get(), DateUtils.toLocalTime(this.stopTimeProperty.get()));
            logger.debug(" start:" + dateStart.toString() + ", stop:" + dateStop.toString());
            logger.debug(" compare:" + dateStart.compareTo(dateStop));
            
            // 日時の逆転チェック
            logger.debug(" start:" + dateStart.toString() + ", stop:" + dateStop.toString());
            logger.trace(" dateStart.compareTo:" + dateStart.compareTo(dateStop));
            logger.trace(" dateStop.compareTo:" + dateStop.compareTo(dateStart));
            logger.trace(" dateStart.before:" + dateStart.before(dateStop));
            logger.trace(" dateStart.after:" + dateStart.after(dateStop));
            logger.trace(" dateStop.before:" + dateStop.before(dateStart));
            logger.trace(" dateStop.after:" + dateStop.after(dateStart));
            if( dateStop.compareTo(dateStart) < 1 ){
                logger.debug("日時の逆転");
                value = false;
                ProductionNaviUtils.setFieldError(this.inputStartDate);
                ProductionNaviUtils.setFieldError(this.inputStartTime);
                ProductionNaviUtils.setFieldError(this.inputStopDate);
                ProductionNaviUtils.setFieldError(this.inputStopTime);
                this.inputStartDate.requestFocus();
            }else{
                // 過去日のチェック
                Date dateNow = org.apache.commons.lang3.time.DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
                logger.trace(" 過去日チェック(開始日) C:" + dateNow.compareTo(org.apache.commons.lang3.time.DateUtils.truncate(dateStart, Calendar.DAY_OF_MONTH)));
                logger.trace(" 過去日チェック(終了日) C:" + dateNow.compareTo(org.apache.commons.lang3.time.DateUtils.truncate(dateStop, Calendar.DAY_OF_MONTH)));
                logger.trace(" 過去日チェック(開始日) A:" + dateNow.after(org.apache.commons.lang3.time.DateUtils.truncate(dateStart, Calendar.DAY_OF_MONTH)));
                logger.trace(" 過去日チェック(終了日) A:" + dateNow.after(org.apache.commons.lang3.time.DateUtils.truncate(dateStop, Calendar.DAY_OF_MONTH)));
                logger.trace(" 過去日チェック(開始日) B:" + dateNow.before(org.apache.commons.lang3.time.DateUtils.truncate(dateStart, Calendar.DAY_OF_MONTH)));
                logger.trace(" 過去日チェック(終了日) B:" + dateNow.before(org.apache.commons.lang3.time.DateUtils.truncate(dateStop, Calendar.DAY_OF_MONTH)));
                if( dateNow.after(org.apache.commons.lang3.time.DateUtils.truncate(dateStart, Calendar.DAY_OF_MONTH)) && dateNow.after(org.apache.commons.lang3.time.DateUtils.truncate(dateStop, Calendar.DAY_OF_MONTH))){
                    logger.trace("日時の過去日");
                    value = false;
                    ProductionNaviUtils.setFieldError(this.inputStartDate);
                    ProductionNaviUtils.setFieldError(this.inputStartTime);
                    ProductionNaviUtils.setFieldError(this.inputStopDate);
                    ProductionNaviUtils.setFieldError(this.inputStopTime);
                }else{
                    ProductionNaviUtils.setFieldNormal(this.inputStartDate);
                    ProductionNaviUtils.setFieldNormal(this.inputStartTime);
                    ProductionNaviUtils.setFieldNormal(this.inputStopDate);
                    ProductionNaviUtils.setFieldNormal(this.inputStopTime);
                }
            }
        }
        
        logger.info(":isInput end");
        return value;
    }
    
    /**
     * 変更チェック
     * 
     * @return True:変更あり
     */
    private boolean isChange(){
        boolean value = false;
        logger.debug(":isChange() start");
        SimpleDateFormat timeFormat = new SimpleDateFormat(LocaleUtils.getString("key.TimeFormat"));

        // 予定名
        try{
            logger.debug(" > 予定名 :" + this.entity.getScheduleName() + "?" + this.inputPlansName.getValue());
            if(!this.entity.getScheduleName().equals((String) this.inputPlansName.getValue())){
                logger.debug("   > 予定名-不一致 :" + this.entity.getScheduleName() + "?" + this.inputPlansName.getValue());
                value = true;
            }
        }catch(NullPointerException ex){
            logger.debug("   > 予定名-不一致 :" + this.entity.getScheduleName() + "?" + this.inputPlansName.getValue());
            value = true;
        }
        // 予定開始日時
        try{
            Date dateStart = DateUtils.toDate(this.startDateProperty.get(), DateUtils.toLocalTime(this.startTimeProperty.get()));
            logger.debug(" > 予定開始日 :" + this.entity.getScheduleFromDate().toString() + "?" + dateStart.toString());
            logger.debug(" > 予定開始日 :" + this.entity.getScheduleFromDate().equals(dateStart) );
            logger.debug(" > 予定開始日 :" + this.entity.getScheduleFromDate().compareTo(dateStart) );
            if(!this.entity.getScheduleFromDate().equals(dateStart)){
                logger.debug("   > 予定開始日-不一致 :" + this.entity.getScheduleFromDate().toString() + "?" + dateStart.toString());
                value = true;
            }
        }catch(NullPointerException ex){
            logger.debug("   > 予定開始日-不一致 :" + this.entity.getScheduleFromDate().toString() + "?" + this.startDateProperty);
            value = true;
        }

        // 予定終了日時
        try{
            Date dateStop = DateUtils.toDate(this.stopDateProperty.get(), DateUtils.toLocalTime(this.stopTimeProperty.get()));
            logger.debug(" > 予定終了日 :" + this.entity.getScheduleToDate().toString() + "?" + dateStop.toString());
            if(!this.entity.getScheduleToDate().equals(dateStop)){
                logger.debug("   > 予定終了日 :" + this.entity.getScheduleToDate().toString() + "?" + dateStop.toString());
                value = true;
            }
        }catch(NullPointerException ex){
            logger.debug("   > 予定終了日-不一致 :" + this.entity.getScheduleToDate().toString() + "?" + this.stopTimeProperty);
            value = true;
        }
        
        // 組織
        try{
            logger.debug(" > 組織 :" + this.entity.getFkOrganization().size() + "?" + this.inputOrganizations.getItems().size());
            if(this.entity.getFkOrganization().size() == this.inputOrganizations.getItems().size()){
                for(int i=0 ; i<this.entity.getFkOrganization().size() ; i++){
                    if(!Objects.equals(this.entity.getFkOrganization().get(i).getOrganizationId(), this.inputOrganizations.getItems().get(i).getOrganizationId()) ){
                        logger.debug("   > 組織-不一致 :" + this.entity.getFkOrganization().get(i).getOrganizationId() + "?" + this.inputOrganizations.getItems().get(i).getOrganizationId());
                        value = true;
                    }
                }
            }
        }catch(NullPointerException ex){
            logger.debug("   > 組織-不一致 :" + this.entity.getFkOrganization().size() + "?" + this.inputOrganizations.getItems().size());
            value = true;
        }

        logger.info(":isChange() end");
        return value;
    }

}
