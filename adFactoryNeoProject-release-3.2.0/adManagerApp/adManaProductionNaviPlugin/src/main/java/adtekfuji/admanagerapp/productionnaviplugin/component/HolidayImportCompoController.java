/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ImportFormatFileUtil;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ProductionNaviUtils;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanExcelFileUtils;
import adtekfuji.clientservice.HolidayInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.exceptions.CsvBadConverterException;
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
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.csv.ImportHolidayCsv;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.importformat.HolidayFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.ImportFormatInfo;
import jp.adtekfuji.adFactory.entity.search.HolidaySearchCondition;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.util.CellReference;
/**
 * 休日表読み込み画面
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "HolidayImportCompo", fxmlPath = "/fxml/compo/holiday_import_compo.fxml")
public class HolidayImportCompoController implements Initializable, ArgumentDelivery, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
    private final HolidayInfoFacade holidayInfoFacade = new HolidayInfoFacade();

    private static final Long RANGE = 100l;

    /** タブ **/
    @FXML
    private TabPane tabImportMode ;
    @FXML
    private Tab tabCsvMode ;
    @FXML
    private Tab tabExcelMode ;
    /** フォルダー選択テキストエリア **/
    @FXML
    private TextField importCsvFileField;
    /** Excelファイル選択テキストエリア **/
    @FXML
    private TextField importExcelFileField;
    /** インポートボタン **/
    @FXML
    private Button ImportButton;
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
        this.dateFormatter.setLenient(false);

        final String path = System.getProperty("user.home") + File.separator + "Documents";
        try {
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            this.importCsvFileField.setText(prop.getProperty(ProductionNaviPropertyConstants.IMPORT_HOLIDAY_CSV_PATH, path));
            this.importExcelFileField.setText(prop.getProperty(ProductionNaviPropertyConstants.IMPORT_HOLIDAY_XLS_PATH, path));

            String tabIdx = prop.getProperty(ProductionNaviPropertyConstants.IMPORT_HOLIDAY_SELECT_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_CSV));
            try {
                switch (Integer.parseInt(tabIdx)) {
                    case ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL:
                        tabImportMode.getSelectionModel().select(ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL);
                        break;
                    default:
                        tabImportMode.getSelectionModel().select(ProductionNaviUtils.IMPORT_TAB_IDX_CSV);
                        break;
                }
            } catch (NumberFormatException ex) {
                tabImportMode.getSelectionModel().select(ProductionNaviUtils.IMPORT_TAB_IDX_CSV);
            }
        } catch (IOException ex) {
            logger.error(ex, ex);
        }
        blockUI(false);
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        ProductionNaviUtils.setFieldNormal(this.importCsvFileField);
        ProductionNaviUtils.setFieldNormal(this.importExcelFileField);
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
                
                // todo
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

                // todo
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
    private void onFormatChangeAction(ActionEvent event) {
        logger.info(":onFormatChangeAction start");
        sc.setComponent("ContentNaviPane", "HolidayFormatChangeCompo");
        logger.info(":onFormatChangeAction end");
    }

    /**
     * キャンセルボタン
     *
     * @param enent
     */
    @FXML
    private void onCancelAction(ActionEvent enent) {
        logger.info(":onFormatChangeAction start");
        try {
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_INIT_MODE, "true");
            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        } finally {
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
            prop.setProperty(ProductionNaviPropertyConstants.IMPORT_HOLIDAY_DATE_FORMAT, ProductionNaviPropertyConstants.DEFAULT_DATE_FORMAT);

            // インポートフォーマット設定を読み込む。
            ImportFormatInfo importFormatInfo = ImportFormatFileUtil.load();
            HolidayFormatInfo holidayFormatInfo = importFormatInfo.getHolidayFormatInfo();

            // 選択タブに合せてフォーマット情報を取得する
            if (tabImportMode.getSelectionModel().isSelected(ProductionNaviUtils.IMPORT_TAB_IDX_CSV)) {
                // CSVインポート
                logger.debug(" TAB selected CSV");

                filename = this.importCsvFileField.getText();
                if (!ProductionNaviUtils.isFileCheck(ProductionNaviUtils.IMPORT_TAB_IDX_CSV, filename)) {
                    ProductionNaviUtils.setFieldError(this.importCsvFileField);
                    return;
                }

                ProductionNaviUtils.setFieldNormal(this.importCsvFileField);
                prop.setProperty(ProductionNaviPropertyConstants.IMPORT_HOLIDAY_CSV_PATH, filename);
                tabMode = ProductionNaviUtils.IMPORT_TAB_IDX_CSV;
                prop.setProperty(ProductionNaviPropertyConstants.IMPORT_HOLIDAY_SELECT_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_CSV));
            } else if (tabImportMode.getSelectionModel().isSelected(ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL)) {
                // EXCELインポート
                logger.debug(" TAB selected Excel");

                filename = this.importExcelFileField.getText();
                logger.debug(" filename=" + filename);
                if (!ProductionNaviUtils.isFileCheck(ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL, filename)) {
                    ProductionNaviUtils.setFieldError(this.importExcelFileField);
                    return;
                }

                ProductionNaviUtils.setFieldNormal(this.importExcelFileField);
                prop.setProperty(ProductionNaviPropertyConstants.IMPORT_HOLIDAY_XLS_PATH, filename);
                tabMode = ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL;
                prop.setProperty(ProductionNaviPropertyConstants.IMPORT_HOLIDAY_SELECT_TAB, String.valueOf(ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL));
            } else {
                logger.error(" TAB selected Error");
                return;
            }

            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);

            // 結果クリア
            this.resultList.getItems().clear();

            // インポート
            this.importHolidayTask(filename, tabMode, holidayFormatInfo);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
            logger.info(":onImportAction end");
        }
    }

    /**
     * 休日表インポートタスク
     *
     * @param filename ファイル名
     * @param tabMode Tabモード
     * @param holidayFormatInfo 設定情報
     */
    private void importHolidayTask(String filename, int tabMode, HolidayFormatInfo holidayFormatInfo) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                blockUI(true);
                try {
                    addResult(String.format("%s %s [%s]", LocaleUtils.getString("key.HolidayImport"), LocaleUtils.getString("key.importStart"), filename));
                    importHoliday(filename, tabMode, holidayFormatInfo);
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    addResult(String.format("%s %s ", LocaleUtils.getString("key.HolidayImport"), LocaleUtils.getString("key.importEnd")));
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 休日表インポート処理
     *
     * @param folder
     * @throws Exception
     */
    private void importHoliday(String filename, int tabMode, HolidayFormatInfo holidayFormatInfo) throws Exception {
        logger.info(":importHoliday start");
        logger.debug(" filename=" + filename);
        logger.debug(" tabMode=" + tabMode);

        List<ImportHolidayCsv> importData = null;
        int lineStart = 0;

        switch (tabMode) {
            case ProductionNaviUtils.IMPORT_TAB_IDX_CSV:
                importData = this.getCsvFile(filename, holidayFormatInfo);
                lineStart = Integer.valueOf(holidayFormatInfo.getCsvStartRow());
                break;
            case ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL:
                importData = this.getExcelFile(filename, holidayFormatInfo);
                lineStart = Integer.valueOf(holidayFormatInfo.getXlsStartRow());
                break;
        }

        // 予定表 読込
        this.save(importData, lineStart);

        // キャッシュデータを再取得する。
        CacheUtils.removeCacheData(HolidayInfoEntity.class);
        CacheUtils.createCacheHoliday(true);

        logger.info(":importHoliday end");
    }

    /**
     * 休日表の登録処理
     *
     * @param importData 休日情報
     */
    private void save(List<ImportHolidayCsv> importData, int lineStart) {
        if (importData != null && importData.size() > 0) {
            ImportHolidayCsv data;
            HolidayInfoEntity entity;
            ResponseEntity result;
            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.set(Calendar.HOUR_OF_DAY, 0);
            nowCalendar.set(Calendar.MINUTE, 0);
            nowCalendar.set(Calendar.SECOND, 0);
            nowCalendar.set(Calendar.MILLISECOND, 0);
            boolean errFlg = false;

            // 未来日のデータ削除（本日含む）
            HolidaySearchCondition search = new HolidaySearchCondition();
            search.setFromDate(nowCalendar.getTime());

            List<HolidayInfoEntity> deleteDatas = new ArrayList();
            long holidayCount = holidayInfoFacade.searchCount(search);
            for (long count = 0; count <= holidayCount; count += RANGE) {
                List<HolidayInfoEntity> entities = holidayInfoFacade.searchRange(search, count, count + RANGE - 1);
                deleteDatas.addAll(entities);
            }

            if (Objects.nonNull(deleteDatas) && deleteDatas.size() > 0) {
                List<Long> deleteIds = new ArrayList();
                addResult(String.format(LocaleUtils.getString("key.import.holiday.delete"), dateFormatter.format(nowCalendar.getTime())));
                for (int i = 0; i < deleteDatas.size(); i++) {
                    deleteIds.add(deleteDatas.get(i).getHolidayId());
                }
                // データ削除
                try {
                    result = null;
                    for (int from = 0; from <= deleteIds.size(); from += 100) {
                        int to = from + 100;
                        if (to > deleteIds.size()) {
                            to = deleteIds.size();
                        }
                        List<Long> ids = deleteIds.subList(from, to);
                        result = holidayInfoFacade.delete(ids);
                        if (!result.isSuccess()) {
                            break;
                        }
                    }
                } catch (Exception ex) {
                    result = null;
                }

                if (Objects.nonNull(result) && result.isSuccess()) {
                    // 削除成功
                    addResult("  > " + String.format(LocaleUtils.getString("key.import.holiday.delete.success"), deleteDatas.size()));
                } else {
                    // 削除失敗
                    addResult("  > " + String.format(LocaleUtils.getString("key.import.holiday.delete.failed")));
                    return;
                }
            }

            dateFormatter.setLenient(false);
            for (int i = 0; i < importData.size(); i++) {
                errFlg = false;
                entity = new HolidayInfoEntity();
                data = importData.get(i);
                if (data == null) {
                    addResult(String.format("%d%s:%s", (lineStart + i), LocaleUtils.getString("key.ListLine"), LocaleUtils.getString("key.NoRelevantData")));
                    continue;
                }

                addResult(String.format("%d%s:%s", (lineStart + i), LocaleUtils.getString("key.ListLine"), data.toStringCharacter()));
                if (data.isNotEmpty()) {
                    entity.setHolidayName(data.getName());
                    // 文字サイズ
                    if (data.getName().length() > 256) {
                        addResult(String.format(" > %s:%s", LocaleUtils.getString("key.HolidayName"), LocaleUtils.getString("key.warn.enterCharacters256")));
                        errFlg = true;
                    }
                } else {
                    addResult(String.format(" > %s:%s", LocaleUtils.getString("key.HolidayName"), LocaleUtils.getString("key.warn.inputRequired")));
                    errFlg = true;
                }

                try {
                    entity.setHolidayDate(ImportFormatFileUtil.stringToDate(data.getHoliday()));
                    // 過去日の休日はスキップ
                    if (entity.getHolidayDate().before(nowCalendar.getTime())) {
                        addResult(String.format(" > %s", LocaleUtils.getString("key.import.skip")));
                        errFlg = true;
                    }
                } catch (Exception e) {
                    logger.fatal(e, e);
                    addResult(String.format(" > %s:%s", LocaleUtils.getString("key.HolidayDate"), LocaleUtils.getString("key.DateFormat")));
                    errFlg = true;
                }

                if (errFlg) {
                    continue;
                }

                // データ登録
                try {
                    result = holidayInfoFacade.regist(entity);
                } catch (Exception ex) {
                    result = null;
                }

                if (Objects.nonNull(result) && result.isSuccess()) {
                    // 追加成功
                    addResult(String.format("  > %s", LocaleUtils.getString("key.import.holiday.regist.success")));
                } else {
                    // 追加失敗
                    addResult(String.format("  > %s", LocaleUtils.getString("key.import.holiday.regist.failed")));
                }
            }
        }
    }

    /**
     * 休日表 CSVファイル読込
     *
     * @param filename ファイル名
     * @param holidayFormatInfo
     * @return 休日表
     */
    private List<ImportHolidayCsv> getCsvFile(String filename, HolidayFormatInfo holidayFormatInfo) {
        List<ImportHolidayCsv> values = null;

        // 予定表(CSV)ファイルの文字コード
        String encode = holidayFormatInfo.getCsvFileEncode();
        // シフトJISの場合はMS932を指定する。
        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS", "S-JIS").contains(encode)) {
            encode = "MS932";
        }
        logger.debug(" encode=" + encode);

        // 読み込み開始行取得
        int lineStart = StringUtils.parseInteger(holidayFormatInfo.getCsvStartRow());
        int idxName = StringUtils.parseInteger(holidayFormatInfo.getCsvHolidayName());
        int idxDate = StringUtils.parseInteger(holidayFormatInfo.getCsvHolidayDate());
        logger.debug(" lineStart=" + lineStart);
        logger.debug(" idxName=" + idxName);
        logger.debug(" idxDate=" + idxDate);

        addResult(String.format("%s %s:%s", LocaleUtils.getString("key.import.format.setting"), LocaleUtils.getString("key.import.read.encode"), encode));
        addResult(String.format(" %s %s%s", LocaleUtils.getString("key.import.read.line.start"), String.valueOf(lineStart), LocaleUtils.getString("key.ListLine")));
        addResult(String.format(" %s %s%s", LocaleUtils.getString("key.HolidayName"), String.valueOf(idxName), LocaleUtils.getString("key.PropertyItem")));
        addResult(String.format(" %s %s%s", LocaleUtils.getString("key.HolidayDate"), String.valueOf(idxDate), LocaleUtils.getString("key.PropertyItem")));

        // マッピングの設定
        int maxIdx = idxName > idxDate ? idxName : idxDate;
        logger.debug(" max Idx =" + maxIdx);

        CSVReader csvReader = null;
        try {
            //フィールド順
            logger.debug("フィールド設定");
            ColumnPositionMappingStrategy<ImportHolidayCsv> columnPositionMappingStrategy = new ColumnPositionMappingStrategy<>();
            columnPositionMappingStrategy.setType(ImportHolidayCsv.class);
            columnPositionMappingStrategy.setColumnMapping(ImportHolidayCsv.getColumns(idxName, idxDate));

            logger.debug("columnPosition Name =" + columnPositionMappingStrategy.getColumnName(idxName - 1));
            logger.debug("columnPosition Holiday =" + columnPositionMappingStrategy.getColumnName(idxDate - 1));

            // CSV 形式の設定
            logger.debug("CSV 形式の設定");
            CsvToBean<ImportHolidayCsv> csvToBean = new CsvToBean<>();
            csvToBean.setMappingStrategy(columnPositionMappingStrategy);

            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(filename), encode));
            logger.debug("CSVReader");

            csvToBean.setCsvReader(csvReader);
            values = csvToBean.parse();

            // 開始行まで削除
            if (values != null && values.size() > 0 && lineStart < values.size()) {
                for (int i = lineStart - 1 - 1; i >= 0; i--) {
                    logger.debug((i + 1) + "行目-削除情報 : " + (Objects.isNull(values.get(i)) ? values.get(i) : values.get(i).toString()));
                    ImportHolidayCsv data = values.remove(i);
                    logger.debug((i + 1) + "行目-削除情報 : " + (Objects.isNull(data) ? data : data.toString()));
                }
            }
        } catch (CsvBadConverterException | FileNotFoundException | UnsupportedEncodingException | IllegalStateException ex) {
            logger.fatal(ex, ex);
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (csvReader != null) {
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
     * 休日表 Excelファイル読込
     *
     * @param filename ファイル名
     * @param holidayFormatInfo
     * @return 休日表
     */
    private List<ImportHolidayCsv> getExcelFile(String filename, HolidayFormatInfo holidayFormatInfo) {
        logger.info(":getExcelFile start");
        logger.debug(" filename=" + filename);

        List<ImportHolidayCsv> values = null;
        int count = 0;
        List<String> cols = null;

        // シート名
        String sheetname = holidayFormatInfo.getXlsSheetName();
        // 読み込み開始行
        int lineStart = StringUtils.parseInteger(holidayFormatInfo.getXlsStartRow());
        // 休日
        String strDateIdx = holidayFormatInfo.getXlsHolidayDate();
        int idxDate = CellReference.convertColStringToIndex(strDateIdx) + 1;
        // 休日名
        String strNameIdx = holidayFormatInfo.getXlsHolidayName();
        int idxName = CellReference.convertColStringToIndex(strNameIdx) + 1;

        addResult(String.format("%s %s:%s", LocaleUtils.getString("key.import.format.setting"), LocaleUtils.getString("key.import.read.excel.sheet.name"), sheetname));
        addResult(String.format(" %s:%s%s", LocaleUtils.getString("key.import.read.line.start"), String.valueOf(lineStart), LocaleUtils.getString("key.ListLine")));
        addResult(String.format(" %s:%s%s", LocaleUtils.getString("key.HolidayName"), strNameIdx, LocaleUtils.getString("key.PropertyItem")));
        addResult(String.format(" %s:%s%s", LocaleUtils.getString("key.HolidayDate"), strDateIdx, LocaleUtils.getString(("key.PropertyItem"))));

        int maxIdx = idxName > idxDate ? idxName : idxDate;

        try {
            WorkPlanExcelFileUtils excelFileUtils = new WorkPlanExcelFileUtils(dateFormatter);
            List<List<String>> rows = excelFileUtils.readExcel(filename, sheetname, lineStart, maxIdx, null);

            values = new ArrayList<>();

            logger.debug(" data Count:" + rows.size());
            for (count = 0; count < rows.size(); count++) {
                cols = rows.get(count);
                logger.debug(" col Count:" + cols.size());

                ImportHolidayCsv data = new ImportHolidayCsv();
                if (idxName <= cols.size()) {
                    data.setName(Objects.isNull(cols.get(idxName - 1)) ? "" : cols.get(idxName - 1));
                }
                if (idxDate <= cols.size()) {
                    data.setHoliday(Objects.isNull(cols.get(idxDate - 1)) ? "" : cols.get(idxDate - 1));
                }
                values.add(data);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.addResult(ex.getLocalizedMessage());
            this.addResult("  >  " + String.format(LocaleUtils.getString("key.SheetNotExist"), sheetname));
        } finally {
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

        prop.setProperty(ProductionNaviPropertyConstants.IMPORT_HOLIDAY_SELECT_TAB, String.valueOf(tabImportMode.getSelectionModel().getSelectedIndex()));
        prop.setProperty(ProductionNaviPropertyConstants.IMPORT_HOLIDAY_CSV_PATH, this.importCsvFileField.getText());
        prop.setProperty(ProductionNaviPropertyConstants.IMPORT_HOLIDAY_XLS_PATH, this.importExcelFileField.getText());

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
