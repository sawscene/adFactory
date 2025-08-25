/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanExcelFileUtils;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ProductionNaviUtils;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import adtekfuji.clientservice.ScheduleInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.locale.LocaleUtils;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Calendar;
import jp.adtekfuji.adFactory.entity.csv.ImportPlansCsv;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleInfoEntity;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.util.CellReference;

/**
 * 予定情報読み込み画面
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "PlansImportCompo", fxmlPath = "/fxml/compo/plans_import_compo.fxml")
public class PlansImportCompoController implements Initializable, ArgumentDelivery, ComponentHandler {

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
//    private final SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final ScheduleInfoFacade scheduleInfoFacade = new ScheduleInfoFacade();

    private static final String CHARSET = "UTF-8";
    private static final String DELIMITER = "\\|";
    private static final Long RANGE = 20l;

    private static final String SEPARATOR_COMMA = "";
    private static final String SEPARATOR_TAB = "";

    /** タブ **/
    @FXML
    private TabPane tabImportMode ;
    /** フォルダー選択テキストエリア **/
    @FXML
    private TextField importCsvFileField;
    /** Excelファイル選択テキストエリア **/
    @FXML
    private TextField importExcelFileField;
    /** 結果表示 **/
    @FXML
    private ListView resultList;
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

        final String path = System.getProperty("user.home") + File.separator + "Documents";
        try {
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            this.importCsvFileField.setText(prop.getProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_CSV_PATH, path));
            this.importExcelFileField.setText(prop.getProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_XLS_PATH, path));

            String tabIdx = prop.getProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_SELECT_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_CSV));
            try{
                switch(Integer.parseInt(tabIdx)){
                    case ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL:
                        tabImportMode.getSelectionModel().select(ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL);
                        break;
                    default:
                        tabImportMode.getSelectionModel().select(ProductionNaviUtils.IMPORT_TAB_IDX_CSV);
                        break;
                }
            }catch(NumberFormatException ex){
                tabImportMode.getSelectionModel().select(ProductionNaviUtils.IMPORT_TAB_IDX_CSV);
            }
        } catch (IOException ex) {
            logger.error(ex,ex);
        }
        blockUI(false);
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {

    }

    /**
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            sc.blockUI("ContentNaviPane", flg);
            progressPane.setVisible(flg);
        });
    }

    /**
     * 結果リストにメッセージを追加し、追加したメッセージが見えるようにスクロールする
     *
     * @param message
     */
    private void addResult(String message) {
        Platform.runLater(() -> {
            this.resultList.getItems().add(message);
            this.resultList.scrollTo(message);
        });
    }

    /**
     * フォルダ選択（CSV)ボタンのクリックイベント
     *
     * @param event
     */
    @FXML
    private void onSelectCsvFileAction(ActionEvent event) {
        blockUI(true);
        try {
            FileChooser fc = new FileChooser();
            File fol = new File(importCsvFileField.getText());
            if (fol.exists()) {
                if (fol.isDirectory()) {
                    fc.setInitialDirectory(fol);
                } else if (fol.isFile()) {
                    if (fol.getParentFile() != null) {
                        fc.setInitialDirectory(fol.getParentFile());
                    }
                }
            }

            fc.setTitle("CSVファイル選択");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSVファイル選択", "*.csv", "*.txt"));
            File selectedFile = fc.showOpenDialog(sc.getStage().getScene().getWindow());
            if (selectedFile != null) {
                importCsvFileField.setText(selectedFile.getPath());

                saveProperties();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * フォルダ選択（Excel)ボタンのクリックイベント
     *
     * @param event
     */
    @FXML
    private void onSelectExcelFileAction(ActionEvent event) {
        blockUI(true);
        try {
            FileChooser fc = new FileChooser();
            File fol = new File(importExcelFileField.getText());
            if (fol.exists()) {
                if (fol.isDirectory()) {
                    fc.setInitialDirectory(fol);
                } else if (fol.isFile()) {
                    if (fol.getParentFile() != null) {
                        fc.setInitialDirectory(fol.getParentFile());
                    }
                }
            }

            fc.setTitle("Excelファイル選択");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excelファイル選択", "*.xlsx", "*.xls"));

            File selectedFile = fc.showOpenDialog(sc.getStage().getScene().getWindow());
            if (selectedFile != null) {
                importExcelFileField.setText(selectedFile.getPath());

                saveProperties();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * フォーマット変更ボタン
     * 
     * @param event
     */
    @FXML
    private void onFormatChangeAction(ActionEvent enent){
        logger.info(":onFormatChangeAction start");
        sc.setComponent("ContentNaviPane", "PlansFormatChangeCompo");
        logger.info(":onFormatChangeAction end");
    }

    /**
     * キャンセルボタン
     * 
     * @param enent 
     */
    @FXML
    private void onCancelAction(ActionEvent enent){
        logger.info(":onFormatChangeAction start");
        try{
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_INIT_MODE, "true");
            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
        }catch(IOException ex){
            logger.fatal(ex, ex);
        }finally{
            sc.setComponent("ContentNaviPane", "RosterCompo");
        }
        logger.info(":onFormatChangeAction end");
    }
            
    /**
     * インポートボタンのクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onImportAction(ActionEvent event) {
        logger.info(":onImportAction start");
        int tabMode = -1;
        String filename = null;
        
        try {
            blockUI(true);
            
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            prop.setProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_DATE_FORMAT, ProductionNaviPropertyConstants.DEFAULT_DATE_FORMAT);

            // 選択タブに合せてフォーマット情報を取得する
            if(tabImportMode.getSelectionModel().isSelected(ProductionNaviUtils.IMPORT_TAB_IDX_CSV)){
                logger.debug(" TAB selected CSV");
                
                filename = this.importCsvFileField.getText();
                logger.debug(" filename=" + filename);
                if(!ProductionNaviUtils.isFileCheck(ProductionNaviUtils.IMPORT_TAB_IDX_CSV, filename)){
                    ProductionNaviUtils.setFieldError(this.importCsvFileField);
                    return;
                }
                
                ProductionNaviUtils.setFieldNormal(this.importCsvFileField);
                prop.setProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_CSV_PATH, filename);
                tabMode = ProductionNaviUtils.IMPORT_TAB_IDX_CSV;
                prop.setProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_SELECT_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_CSV));
            }
            else if(tabImportMode.getSelectionModel().isSelected(ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL)){
                logger.debug(" TAB selected Excel");
                
                filename = this.importExcelFileField.getText();
                logger.debug(" filename=" + filename);
                if(!ProductionNaviUtils.isFileCheck(ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL, filename)){
                    ProductionNaviUtils.setFieldError(this.importExcelFileField);
                    return;
                }

                ProductionNaviUtils.setFieldNormal(this.importExcelFileField);
                prop.setProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_XLS_PATH, filename);
                tabMode = ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL;
                prop.setProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_SELECT_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL));
            }
            else{
                logger.error(" TAB selected Error ");
                return;
            }
            
            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);

            this.resultList.getItems().clear();

            // インポート
            this.importPlansTask(filename, tabMode, prop);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
            logger.info(":onImportAction end");
         }
    }

    /**
     * 予定表インポートタスク
     *
     * @param filename ファイル名
     * @param tabMode Tabモード
     * @param prop 設定情報
     */
    private void importPlansTask(String filename, int tabMode, Properties prop) {
        logger.info(":importPlansTask start");
        logger.debug("  filename=" + filename);
        logger.debug("  tabMode=" + tabMode);

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Logger log = LogManager.getLogger();
                logger.debug(":task call start");
                
                blockUI(true);
                
                try {
                    addResult(String.format("%s %s [%s]", LocaleUtils.getString("key.PlansImport"), LocaleUtils.getString("key.importStart"), filename));
                    importPlans(filename, tabMode, prop);
               } catch (Exception ex) {
                    log.fatal(ex, ex);
                } finally {
                    addResult(String.format("%s %s ", LocaleUtils.getString("key.PlansImport"), LocaleUtils.getString("key.importEnd")));
                    blockUI(false);
                    logger.debug(":task call end");
                }
                return null;
            }
        };
        new Thread(task).start();
        
        logger.info(":importPlansTask end");
    }

    /**
     * 予定表インポート処理
     *
     * @param filename ファイル名
     * @param tabMode Tabモード
     * @param prop 設定情報
     */
    private void importPlans(String filename, int tabMode, Properties prop) throws Exception {
        logger.info(":importPlans start");
        logger.debug(" filename=" + filename);
        logger.debug(" tabMode=" + tabMode);
        
        List<ImportPlansCsv> importData = null;
        int lineStart = 0;

        // 予定表 読込
        switch(tabMode){
            case ProductionNaviUtils.IMPORT_TAB_IDX_CSV:
                importData = this.getCsvFile(filename, prop);
                lineStart = Integer.parseInt(prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_LINE, ProductionNaviPropertyConstants.INIT_PLANS_CSV_LINE));
                break;
            case ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL:
                importData = this.getExcelFile(filename, prop);
                lineStart = Integer.parseInt(prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_LINE, ProductionNaviPropertyConstants.INIT_PLANS_XLS_LINE));
                break;
        }

        // 予定表 書き込み
        this.save(importData, lineStart,  prop);
        
        logger.info(":importPlans end");
    }

    /**
     * 予定表登録処理
     * 
     * @param importData 予定表情報
     * @param lineStart 開始行
     * @param prop リソース
     */
    private void save(List<ImportPlansCsv> importData, int lineStart, Properties prop){
        
        if(importData != null && importData.size() > 0){
            ImportPlansCsv data;
            ScheduleInfoEntity scheduleInfoEntity;
            OrganizationInfoEntity organizationInfoEntity;
            ResponseEntity result;
            String dateFormat = prop.getProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_DATE_FORMAT,ProductionNaviPropertyConstants.DEFAULT_DATE_FORMAT);
            String timeFormat = prop.getProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_TIME_FORMAT,ProductionNaviPropertyConstants.DEFAULT_TIME_FORMAT);
            SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
            SimpleDateFormat datetimeFormatter = new SimpleDateFormat(dateFormat + " " + timeFormat);
            int failedNum = 0;
            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.set(Calendar.HOUR_OF_DAY,0);
            nowCalendar.set(Calendar.MINUTE, 0);
            nowCalendar.set(Calendar.SECOND, 0);
            nowCalendar.set(Calendar.MILLISECOND, 0);
//            String today = dateFormatter.format(nowCalendar.getTime());

            dateFormatter.setLenient(false);
            datetimeFormatter.setLenient(false);
            for(int i=0 ; i<importData.size() ; i++){
                data = importData.get(i);

                if(data == null){
                    addResult(String.format("%d行目:%s", (lineStart+i), LocaleUtils.getString("key.NoRelevantData")));
                    continue;
                }
                
                // 必須チェック
                addResult(String.format("%d行目:%s", (lineStart+i), data.toStringCharacter()));
                if(data.isEmpty()){
                    addResult(String.format(" > %s", LocaleUtils.getString("key.warn.inputRequired")));
                    continue;
                }
        
                // 日付チェック
                scheduleInfoEntity = new ScheduleInfoEntity();
                scheduleInfoEntity.setScheduleName(data.getPlansName());
                try{
                    logger.debug(" 予定開始:" + data.getStartDatetime() + ", " + prop.getProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_DATE_FORMAT,ProductionNaviPropertyConstants.DEFAULT_DATE_FORMAT));
                    scheduleInfoEntity.setScheduleFromDate(datetimeFormatter.parse(data.getStartDatetime()));
                }catch(ParseException e){
                    logger.fatal(e, e);
                    try {
                        scheduleInfoEntity.setScheduleFromDate(dateFormatter.parse(data.getStartDatetime()));
                    } catch (ParseException ex) {
                        logger.fatal(e, e);
                        addResult(String.format(" > %s:%s", LocaleUtils.getString("key.DateTimeFormat"), data.getStartDatetime()));
                    }
                }
                try{
                    logger.debug(" 予定終了:" + data.getStopDatetime() + ", " + prop.getProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_DATE_FORMAT,ProductionNaviPropertyConstants.DEFAULT_DATE_FORMAT));
                    scheduleInfoEntity.setScheduleToDate(datetimeFormatter.parse(data.getStopDatetime()));
                }catch(ParseException e){
                    try {
                        scheduleInfoEntity.setScheduleToDate(dateFormatter.parse(data.getStopDatetime()));
                        scheduleInfoEntity.setScheduleToDate(DateUtils.addDays(scheduleInfoEntity.getScheduleToDate(), 1));
                        scheduleInfoEntity.setScheduleToDate(DateUtils.addSeconds(scheduleInfoEntity.getScheduleToDate(), -1));
                    } catch (ParseException ex) {
                        logger.fatal(ex, ex);
                        addResult(String.format(" > %s:%s", LocaleUtils.getString("key.DateTimeFormat"), data.getStopDatetime()));
                    }
                }
                
                if(scheduleInfoEntity.getScheduleFromDate() == null || scheduleInfoEntity.getScheduleToDate() == null){
                    logger.debug(" DateTime Error ? " + scheduleInfoEntity.getScheduleFromDate() + ":" + scheduleInfoEntity.getScheduleToDate());
                    continue;
                }

                // 日付逆転チェック
                logger.debug(" date check : ", scheduleInfoEntity.getScheduleFromDate().after(scheduleInfoEntity.getScheduleToDate()));
                if(scheduleInfoEntity.getScheduleFromDate().after(scheduleInfoEntity.getScheduleToDate()) ){
                    addResult(String.format(" > %s", LocaleUtils.getString("key.DateCompErrMessage")));
                    continue;
                }

                // 過去日は対象外
                logger.debug("　対象日:" + nowCalendar.getTime().toString());
                logger.debug("　開始日:" + scheduleInfoEntity.getScheduleFromDate().toString());
                logger.debug("　終了日:" + scheduleInfoEntity.getScheduleToDate().toString());
                logger.debug("　比較（開始日）:" + nowCalendar.getTime().compareTo(scheduleInfoEntity.getScheduleFromDate()));
                logger.debug("　比較（終了日）:" + nowCalendar.getTime().compareTo(scheduleInfoEntity.getScheduleToDate()));
                logger.debug("　比較（開始日）:" + scheduleInfoEntity.getScheduleFromDate().compareTo(nowCalendar.getTime()));
                logger.debug("　比較（終了日）:" + scheduleInfoEntity.getScheduleToDate().compareTo(nowCalendar.getTime()));
                if(nowCalendar.getTime().compareTo(scheduleInfoEntity.getScheduleFromDate()) > 0 && nowCalendar.getTime().compareTo(scheduleInfoEntity.getScheduleToDate()) > 0){
                    addResult(String.format(" > %s", LocaleUtils.getString("key.import.skip")));
                    continue;
                }
                
                // 組織識別名を分割
                String[] oranizationIdents = data.getOrganization().split(DELIMITER, 0);
                for (String oranizationIdent : oranizationIdents) {
                    if(oranizationIdent.equals(DELIMITER)){
                        logger.debug(" 組織識別名：continue:");
                        continue;
                    }

                    organizationInfoEntity = new OrganizationInfoEntity();
                    try{
                        this.logger.debug("組織識別名-検索:" + oranizationIdent);
                        organizationInfoEntity = organizationInfoFacade.findName(URLEncoder.encode(oranizationIdent, CHARSET));
                    }catch(Exception ex){
                        logger.fatal(ex, ex);
                        organizationInfoEntity = null;
                    }finally{
                        if (Objects.nonNull(organizationInfoEntity.getOrganizationId())) {
                            scheduleInfoEntity.setFkOrganizationId(organizationInfoEntity.getOrganizationId());
                        } else {
                            // 存在しない組織識別名
                            addResult(String.format(" > %s:%s", LocaleUtils.getString("key.ImportKanban_OrganizationNothing"), oranizationIdent));
                            continue;
                        }
                    }

                    // データ登録
                    try{
                        result = scheduleInfoFacade.regist(scheduleInfoEntity);
                    }catch(Exception ex){
                        logger.fatal(ex, ex);
                        result = null;
                    }

                    if (Objects.nonNull(result) && result.isSuccess()) {
                        // 追加成功
                        addResult(String.format("  > %s", LocaleUtils.getString("key.import.plans.regist.success")));
                    } else {
                        // 追加失敗
                        failedNum++;
                        addResult(String.format("  > %s", LocaleUtils.getString("key.import.plans.regist.failed")));
                    }
                }
            }
        }
    }
    
    /**
     * 予定表 CSVファイル読込
     *
     * @param filename ファイル名
     * @param prop 設定情報
     * @return 予定表
     */
    private List<ImportPlansCsv> getCsvFile(String filename, Properties prop) {
        logger.info(":getCsvFile start");
        logger.debug(" filename=" + filename);
        List<ImportPlansCsv> values = null;

        // 予定表(CSV)ファイルの文字コード
        String encode = prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ENCODE, ProductionNaviPropertyConstants.INIT_PLANS_CSV_ENCODE).toUpperCase();
        // シフトJISの場合はMS932を指定する。
        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS", "S-JIS").contains(encode)) {
            encode = "MS932";
        }
        logger.debug(" encode=" + encode);

        // 読み込み設定情報
        int lineStart = Integer.parseInt(prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_LINE, ProductionNaviPropertyConstants.INIT_PLANS_CSV_LINE));
        int idxName = Integer.parseInt(prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_PLANS_NAME, ProductionNaviPropertyConstants.INIT_PLANS_CSV_PLANS_NAME));
        int idxStartDate = Integer.parseInt(prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_START_DT, ProductionNaviPropertyConstants.INIT_PLANS_CSV_START_DT));
        int idxStopDate = Integer.parseInt(prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_STOP_DT, ProductionNaviPropertyConstants.INIT_PLANS_CSV_STOP_DT));
        int idxOrganization = Integer.parseInt(prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ORGANIZATION, ProductionNaviPropertyConstants.INIT_PLANS_CSV_ORGANIZATION));
        logger.debug(" lineStart=" + lineStart);
        logger.debug(" idxName=" + idxName);
        logger.debug(" idxStartDate=" + idxStartDate);
        logger.debug(" idxStopDate=" + idxStopDate);
        logger.debug(" idxOrganization=" + idxOrganization);

        addResult(String.format("%s %s:%s", LocaleUtils.getString("key.import.format.setting"), LocaleUtils.getString("key.import.read.encode"),prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_CSV_ENCODE, ProductionNaviPropertyConstants.INIT_PLANS_CSV_ENCODE).toUpperCase()));
        addResult(String.format(" %s:%s行目", LocaleUtils.getString("key.import.read.line.start"), String.valueOf(lineStart)));
        addResult(String.format(" %s:%s項目", LocaleUtils.getString("key.import.read.plans.name"), String.valueOf(idxName)));
        addResult(String.format(" %s:%s項目", LocaleUtils.getString("key.import.read.plans.start.datetime"), String.valueOf(idxStartDate)));
        addResult(String.format(" %s:%s項目", LocaleUtils.getString("key.import.read.plans.stop.datetime"), String.valueOf(idxStopDate)));
        addResult(String.format(" %s:%s項目", LocaleUtils.getString("key.OrganizationsManagementName"), String.valueOf(idxOrganization)));

        // マッピングの設定
        int maxIdx = idxName > idxStartDate ? idxName : idxStartDate;
        maxIdx = maxIdx > idxStartDate ? maxIdx : idxStartDate;
        maxIdx = maxIdx > idxStopDate ? maxIdx : idxStopDate;
        maxIdx = maxIdx > idxOrganization ? maxIdx : idxOrganization;
        logger.debug(" max Idx =" + maxIdx);

        CSVReader csvReader = null;
        try{
            //フィールド順
            logger.debug("フィールド設定");
            ColumnPositionMappingStrategy<ImportPlansCsv> columnPositionMappingStrategy = new ColumnPositionMappingStrategy<>();
            columnPositionMappingStrategy.setType(ImportPlansCsv.class);
            columnPositionMappingStrategy.setColumnMapping(ImportPlansCsv.getColumns(idxName, idxStartDate, idxStopDate, idxOrganization));
            logger.debug("columnPosition Name =" + columnPositionMappingStrategy.getColumnName(idxName-1));
            logger.debug("columnPosition StartDatetime =" + columnPositionMappingStrategy.getColumnName(idxStartDate-1));
            logger.debug("columnPosition StopDatetime =" + columnPositionMappingStrategy.getColumnName(idxStopDate-1));
            logger.debug("columnPosition Organization =" + columnPositionMappingStrategy.getColumnName(idxOrganization-1));

            // CSV 形式の設定
            logger.debug("CSV 形式の設定");
            CsvToBean<ImportPlansCsv> csvToBean = new CsvToBean<>();
            csvToBean.setMappingStrategy(columnPositionMappingStrategy);

            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(filename), encode));
            logger.debug("CSVReader");
            
            csvToBean.setCsvReader(csvReader);
            logger.debug("csvToBean.setCsvReader");
            values = csvToBean.parse();
            
//            if(values != null && values.size() > 0){
//                logger.info("list=" + values.size());
//                
//                for(int i=0 ; i<values.size() ; i++){
//                    if(values.get(i) == null){
//                        logger.info("データなし");
//                        continue;
//                    }
//                    logger.info((i+1) + "行目 : " + values.get(i).toString());
//                }
//            }else{
//                logger.info("データ0件");
//            }

            // 開始行まで削除
            if(values != null && values.size() > 0 && lineStart < values.size()){
                for(int i=lineStart-1-1 ; i>=0 ; i--){
                    logger.debug((i+1) + "行目-削除情報 : " + (Objects.isNull(values.get(i)) ? values.get(i) : values.get(i).toString()));
                    ImportPlansCsv data = values.remove(i);
                    logger.debug((i+1) + "行目-削除情報 : " + (Objects.isNull(data) ? data : data.toString()));
                }
            }

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            logger.fatal(ex, ex);
            this.addResult(ex.getLocalizedMessage());
        }finally{
            if(csvReader != null){
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
               }
            }
        }
        return values;
    }
    
    /**
     * 予定表 Excelファイル読込
     *
     * @param filename ファイル名
     * @param properties 設定情報
     * @return 予定表
     */
    private List<ImportPlansCsv> getExcelFile(String filename, Properties prop){
        logger.info(":getExcelFile start");
        logger.debug(" filename=" + filename);

        List<ImportPlansCsv> values = null;
        int count=0;
        List<String> cols = null;
        
        // 予定表(CSV)ファイルの文字コード
        String sheetname = prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_SHEET_NAME, ProductionNaviPropertyConstants.INIT_PLANS_XLS_SHEET_NAME);
        // 読み込み設定情報
        int lineStart = Integer.parseInt(prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_LINE, ProductionNaviPropertyConstants.INIT_PLANS_XLS_LINE));

        String strNameIdx = prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_PLANS_NAME, ProductionNaviPropertyConstants.INIT_PLANS_XLS_PLANS_NAME);
        String strStartIdx = prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_START_DT, ProductionNaviPropertyConstants.INIT_PLANS_XLS_START_DT);
        String strStopIdx = prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_STOP_DT, ProductionNaviPropertyConstants.INIT_PLANS_XLS_STOP_DT);
        String strOrganizationIdx = prop.getProperty(ProductionNaviPropertyConstants.KEY_PLANS_XLS_ORGANIZATION, ProductionNaviPropertyConstants.INIT_PLANS_XLS_ORGANIZATION);
        int idxName = CellReference.convertColStringToIndex(strNameIdx) + 1;
        int idxStartDate = CellReference.convertColStringToIndex(strStartIdx) + 1;
        int idxStopDate = CellReference.convertColStringToIndex(strStopIdx) + 1;
        int idxOrganization = CellReference.convertColStringToIndex(strOrganizationIdx) + 1;

        addResult(String.format("%s %s:%s", LocaleUtils.getString("key.import.format.setting"), LocaleUtils.getString("key.import.read.excel.sheet.name"), sheetname));
        addResult(String.format(" %s:%s行目", LocaleUtils.getString("key.import.read.line.start"), String.valueOf(lineStart)));
        addResult(String.format(" %s:%s項目", LocaleUtils.getString("key.import.read.plans.name"), strNameIdx));
        addResult(String.format(" %s:%s項目", LocaleUtils.getString("key.import.read.plans.start.datetime"), strStartIdx));
        addResult(String.format(" %s:%s項目", LocaleUtils.getString("key.import.read.plans.stop.datetime"), strStopIdx));
        addResult(String.format(" %s:%s項目", LocaleUtils.getString("key.OrganizationsManagementName"), strOrganizationIdx));

        logger.debug(String.format(" %s:%s項目:%d", LocaleUtils.getString("key.import.read.plans.name"), strNameIdx, idxName));
        logger.debug(String.format(" %s:%s項目:%d", LocaleUtils.getString("key.import.read.plans.start.datetime"), strStartIdx, idxStartDate));
        logger.debug(String.format(" %s:%s項目:%d", LocaleUtils.getString("key.import.read.plans.stop.datetime"), strStopIdx, idxStopDate));
        logger.debug(String.format(" %s:%s項目:%d", LocaleUtils.getString("key.OrganizationsManagementName"), strOrganizationIdx, idxOrganization));
        
        int maxIdx = idxName > idxStartDate ? idxName : idxStartDate;
        maxIdx = maxIdx > idxStopDate ? maxIdx : idxStopDate;
        maxIdx = maxIdx > idxOrganization ? maxIdx : idxOrganization;
        
        try{
            SimpleDateFormat datetimeFormatter = new SimpleDateFormat(prop.getProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_DATE_FORMAT));
            WorkPlanExcelFileUtils excelFileUtils = new WorkPlanExcelFileUtils(datetimeFormatter);
            List<List<String>> rows = excelFileUtils.readExcel(filename, sheetname, lineStart, maxIdx, null);
            
            OrganizationInfoEntity organizationInfoEntity = null;
            String OrganizationName;
            values = new ArrayList<>();

            logger.debug(" data Count:" + rows.size());
            for(count=0 ; count < rows.size(); count++){
                cols = rows.get(count);
                logger.debug(" col Count:" + cols.size());

                ImportPlansCsv data = new ImportPlansCsv();
                if(idxName <= cols.size()){
                    data.setPlansName(Objects.isNull(cols.get(idxName-1)) ? "" : cols.get(idxName-1));
                }
                if(idxStartDate <= cols.size()){
                    data.setStartDatetime(Objects.isNull(cols.get(idxStartDate-1)) ? "" : cols.get(idxStartDate-1));
                }
                if(idxStopDate <= cols.size()){
                    data.setStopDatetime(Objects.isNull(cols.get(idxStopDate-1)) ? "" : cols.get(idxStopDate-1));
                }
                if(idxOrganization <= cols.size()){
                    data.setOrganization(Objects.isNull(cols.get(idxOrganization-1)) ? "" : cols.get(idxOrganization-1));
                }
                values.add(data);
            }
        }catch(Exception ex){
            logger.fatal(ex, ex);
            this.addResult(ex.getLocalizedMessage());
            this.addResult("  >  " + sheetname + "シートが存在しません。");
        }finally{
            logger.info(":getExcelFile end");
        }
        
        return values;
    }

    /**
     * プロパティを保存する。保存する対象は次の通り。
     * <pre>
     * 　現在開いているタブ
     * 　CSV形式読み込みファイルパス
     * 　Excel形式読み込みファイルパス
     * </pre>
     */
    private void saveProperties() throws IOException {
        Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

        prop.setProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_SELECT_TAB, String.valueOf(tabImportMode.getSelectionModel().getSelectedIndex()));
        prop.setProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_CSV_PATH, this.importCsvFileField.getText());
        prop.setProperty(ProductionNaviPropertyConstants.IMPORT_PLANS_XLS_PATH, this.importExcelFileField.getText());

        AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
    }

    @Override
    public boolean destoryComponent() {
        try {
            saveProperties();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return true;
    }
}
