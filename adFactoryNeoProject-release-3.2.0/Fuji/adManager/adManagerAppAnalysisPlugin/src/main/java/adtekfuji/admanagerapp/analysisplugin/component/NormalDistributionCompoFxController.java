/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.component;

import adtekfuji.admanagerapp.analysisplugin.common.ActualSearchSettingData;
import adtekfuji.admanagerapp.analysisplugin.common.ActualSearcher;
import adtekfuji.admanagerapp.analysisplugin.common.AnalysisWorkFilterData;
import adtekfuji.admanagerapp.analysisplugin.javafx.CheckTableData;
import adtekfuji.admanagerapp.analysisplugin.javafx.NomalDistribtionData;
import adtekfuji.admanagerapp.analysisplugin.javafx.NormalDistributionCreater;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.DirectoryChooser;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.utils.Staristics;
import jp.adtekfuji.javafxcommon.dialog.MessageDialog;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程作業時間を正規分布で表示する画面
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.07.29.Fri
 */
@FxComponent(id = "NormalDistributCompo", fxmlPath = "/fxml/compo/normaldistributionCompo.fxml")
public class NormalDistributionCompoFxController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final Properties properties = AdProperty.getProperties();
    private final Properties propertiesForFuji = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final ActualSearchSettingData searchSettingData = new ActualSearchSettingData();
    private AnalysisWorkFilterData analysisWorkFilterData;
    private List<ActualResultEntity> result;
    private List<NomalDistribtionData> distribtionDataList;// 正規分布表示用データ

    private static final String CSV_ENCODE_KEY = "report_csv_encode_type";// CSVファイルのエンコード (共通設定)
    private static final String CSV_ENCODE_DEFAULT = "Shift-JIS";
    private static final String ANALYSIS_REPORT_OUTPUT_DIR_KEY = "analysis_report_output_dir";// 工程分析結果ファイルの出力先のキー

    private static final String ANALYSIS_REPORT_CSV = "Analysis_report_%s.csv";// 分析結果ファイル
    private static final char SEPARATOR = ',';
    private static final char QUOTE = '"';
    private static final String LINEEND = "\r\n";

    private static final String SJIS_ENCODE = "MS932";
    private static final List<String> ms932List = Arrays.asList("SHIFT-JIS", "SJIS");// MS932に変更するエンコード
    private static String csvEncode;// CSVファイルのエンコード (共通設定)

    private String outputDir;// 工程分析結果ファイルの出力先
    private MessageDialogEnum.MessageDialogResult overwriteDialogResult;// CSVファイル出力の上書きダイアログの戻り値

    @FXML
    private Button outputCsvButton;
    @FXML
    private Label dateRangeField;
    @FXML
    private TilePane graphPane;
    @FXML
    private Pane progressPane;

    /**
     * CSVファイル出力の上書きダイアログの戻り値をセットする。
     *
     * @param value 上書きダイアログの戻り値
     */
    private void setOverwriteDialogResult(MessageDialogEnum.MessageDialogResult value) {
        this.overwriteDialogResult = value;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(NormalDistributionCompoFxController.class.getName() + ":initialize start");

        AnalysisSettingEventer.getInstance().setNormalDistribution(this);
        this.blockUI(Boolean.FALSE);

        // CSVファイルのエンコード (共通設定)
        if (this.properties.containsKey(CSV_ENCODE_KEY)) {
            csvEncode = this.properties.getProperty(CSV_ENCODE_KEY);
        } else {
            csvEncode = CSV_ENCODE_DEFAULT;
            this.properties.setProperty(CSV_ENCODE_KEY, csvEncode);
        }

        // Shift-JISが指定されている場合、MS932に変更する。(設定ファイルの値は変更しない)
        for (String encName : ms932List) {
            if (encName.equalsIgnoreCase(csvEncode)) {
                csvEncode = SJIS_ENCODE;
                break;
            }
        }

        // 工程分析結果CSVファイルの保存先フォルダ
        if (this.propertiesForFuji.containsKey(ANALYSIS_REPORT_OUTPUT_DIR_KEY)) {
            this.outputDir = this.propertiesForFuji.getProperty(ANALYSIS_REPORT_OUTPUT_DIR_KEY);
        } else {
            File dir = new File(System.getProperty("user.home"), "Documents");// マイドキュメント
            this.outputDir = dir.getPath();
            this.propertiesForFuji.setProperty(ANALYSIS_REPORT_OUTPUT_DIR_KEY, this.outputDir);
        }

        outputCsvButton.setDisable(true);// CSV出力ボタン 無効

        logger.info(NormalDistributionCompoFxController.class.getName() + ":initialize end");
    }

    /**
     * 検索項目に該当する実績情報を検索する
     *
     * @param event 検索ボタンが押されたとき
     */
    @FXML
    public void onSearch(ActionEvent event) {
        logger.info(NormalDistributionCompoFxController.class.getName() + ":onSearch start");

        this.blockUI(Boolean.TRUE);
        this.loadSetting();
        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Search"), "ActualSearchDialog", this.searchSettingData);
        if (ret.equals(ButtonType.OK) && this.searchSettingData.getworkTabelDatas().size() > 0) {
            // 検索するので操作制限は遷移先で解く
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        storeSetting();
                        result = ActualSearcher.search(searchSettingData);
                        Platform.runLater(() -> {
                            showGraph();
                        });
                    } finally {
                        Platform.runLater(() -> blockUI(false));
                    }
                    return null;
                }
            };
            new Thread(task).start();

        } else {
            // 検索しないので操作制限を解く
            this.blockUI(Boolean.FALSE);
        }

        logger.info(NormalDistributionCompoFxController.class.getName() + ":onSearch end");
    }

    /**
     * CSV出力
     *
     * @param event CSV出力ボタン押下
     */
    @FXML
    public void onOutputCsv(ActionEvent event) {
        try {
            blockUI(true);

            // 出力先フォルダ選択
            DirectoryChooser dc = new DirectoryChooser();
            File fol = new File(this.outputDir);
            if (fol.exists() && fol.isDirectory()) {
                dc.setInitialDirectory(fol);
            }

            File selectedFolder = dc.showDialog(sc.getStage().getScene().getWindow());
            if (selectedFolder == null) {
                return;
            }

            String folderPath = selectedFolder.getPath();
            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                return;
            }

            this.outputDir = folderPath;
            this.propertiesForFuji.setProperty(ANALYSIS_REPORT_OUTPUT_DIR_KEY, folderPath);

            List<NomalDistribtionData> distribtionDatas = this.distribtionDataList;

            Task task = new Task<OutputCsvResult>() {
                @Override
                protected OutputCsvResult call() throws Exception {
                    blockUI(true);
                    try {
                        return createAnalysisReportFiles(folderPath, distribtionDatas);
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        throw ex;
                    }
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    OutputCsvResult result = this.getValue();
                    if (result != null) {
                        String message = String.format(LocaleUtils.getString("key.info.OutputAnalysisReportResult"), result.outputCount, result.skipCount);
                        MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(
                                sc.getStage().getScene().getWindow(), LocaleUtils.getString("key.OutputAnalysisReport"), message,
                                MessageDialogEnum.MessageDialogType.Infomation, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
                    }
                    blockUI(false);
                }

                @Override
                protected void failed() {
                    super.failed();
                    blockUI(false);
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
     * グラフを表示する
     *
     */
    private void showGraph() {
        logger.info(NormalDistributionCompoFxController.class.getName() + ":showGraph start");

        NormalDistributionCreater creater = new NormalDistributionCreater(result, this.graphPane, this.analysisWorkFilterData);
        creater.createGraph();
        this.dateRangeField.setText(this.searchSettingData.getStartDate().format(DateTimeFormatter.ISO_DATE)
                + " - " + this.searchSettingData.getEndDate().format(DateTimeFormatter.ISO_DATE));

        // 工程分析データ
        distribtionDataList = new ArrayList(creater.getWorkActualData().values());
        if (distribtionDataList.size() > 0) {
            outputCsvButton.setDisable(false);// CSV出力ボタン 有効
        } else {
            outputCsvButton.setDisable(true);// CSV出力ボタン 無効
        }

        logger.info(NormalDistributionCompoFxController.class.getName() + ":showGraph end");
    }

    /**
     * 各種設定読み込み処理
     *
     */
    private void loadSetting() {
        logger.info(NormalDistributionCompoFxController.class.getName() + ":loadSearchSetting start");

        try {
            //設定を読み込んで検索条件保持用クラスにデータを入れる
            this.searchSettingData.setStartDate(LocalDate.parse(this.propertiesForFuji.getProperty(ActualSearchSettingData.KEY_SEARCH_START_DATE, LocalDate.now().toString()), DateTimeFormatter.ISO_DATE));
            this.searchSettingData.setEndDate(LocalDate.parse(this.propertiesForFuji.getProperty(ActualSearchSettingData.KEY_SEARCH_END_DATE, LocalDate.now().toString()), DateTimeFormatter.ISO_DATE));

            String[] delays = this.propertiesForFuji.getProperty(ClientPropertyConstants.KEY_DELAYREASON, "").split(",");
            ObservableList<CheckTableData> datas = FXCollections.observableArrayList();
            for (String delay : delays) {
                datas.add(new CheckTableData(delay, true));
            }

            this.analysisWorkFilterData = new AnalysisWorkFilterData(
                    Integer.parseInt(this.propertiesForFuji.getProperty(ClientPropertyConstants.KEY_TACTTIME_EARLIEST, "0")),
                    Integer.parseInt(this.propertiesForFuji.getProperty(ClientPropertyConstants.KEY_TACTTIME_SLOWEST, "0")),
                    datas, AnalysisWorkFilterData.TimeUnitEnum.getEnum(this.propertiesForFuji.getProperty(ClientPropertyConstants.KEY_TIME_UNIT, "SECOND")));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(NormalDistributionCompoFxController.class.getName() + ":loadSearchSetting end");
    }

    /**
     * 検索設定書き込み処理
     *
     */
    private void storeSetting() {
        logger.info(NormalDistributionCompoFxController.class.getName() + ":storeSearchSetting start");

        try {
            //設定を読み込んで検索条件保持用クラスにデータを入れる
            this.propertiesForFuji.setProperty(ActualSearchSettingData.KEY_SEARCH_START_DATE, this.searchSettingData.getStartDate().format(DateTimeFormatter.ISO_DATE));
            this.propertiesForFuji.setProperty(ActualSearchSettingData.KEY_SEARCH_END_DATE, this.searchSettingData.getEndDate().format(DateTimeFormatter.ISO_DATE));
            AdProperty.store(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(NormalDistributionCompoFxController.class.getName() + ":storeSearchSetting end");
    }

    /**
     * 工程分析結果のCSVファイルを出力する。
     *
     * @param folderPath 出力先フォルダ
     * @param distribtionDatas 正規分布表示用データリスト
     * @return CSV出力結果
     * @throws Exception 
     */
    private OutputCsvResult createAnalysisReportFiles(String folderPath, List<NomalDistribtionData> distribtionDatas) throws Exception {
        int outputCount = 0;// 出力件数
        int skipCount = 0;// スキップ件数
        boolean isCancel = false;// キャンセルしたか

        // 現在日時
        String outputDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        AnalysisWorkFilterData.TimeUnitEnum timeUnitRate = AnalysisWorkFilterData.TimeUnitEnum.getEnum(
                this.propertiesForFuji.getProperty(ClientPropertyConstants.KEY_TIME_UNIT, "SECOND")); // ミリ秒を分に変更
        Integer timeUnit = timeUnitRate.getTimeUnit();
        String timeUnitTag = "(" + AnalysisWorkFilterData.TimeUnitEnum.getLocale(rb, timeUnitRate) + ")";

        overwriteDialogResult = MessageDialogEnum.MessageDialogResult.Ok;// 上書き確認結果をリセットする。

        for (NomalDistribtionData distribtionData : distribtionDatas) {
            String fileNameSub = String.format("%s_%s", distribtionData.getGraphTitle(), outputDateTime);
            String fileName = String.format(ANALYSIS_REPORT_CSV, fileNameSub);
            File csvFile = new File(folderPath, fileName);
            if (csvFile.exists()) {
                // 上書き確認結果が「すべてはい」と「すべていいえ」を選択済の場合以外、上書き確認を行なう。
                if (!overwriteDialogResult.equals(MessageDialogEnum.MessageDialogResult.YesToAll)
                        && !overwriteDialogResult.equals(MessageDialogEnum.MessageDialogResult.NoToAll)) {
                    // %sは既に存在します。\n上書きしますか？
                    String message = String.format(LocaleUtils.getString("key.ExistOverwrite"), fileName);
                    CountDownLatch latch = new CountDownLatch(1);
                    Platform.runLater(() -> {
                        MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(
                                sc.getStage().getScene().getWindow(), LocaleUtils.getString("key.OutputAnalysisReport"), message,
                                MessageDialogEnum.MessageDialogType.Warning, MessageDialogEnum.MessageDialogButtons.YesToAllNoToAllCancel, 3.0, "#ff0000", "#ffffff");
                        this.setOverwriteDialogResult(dialogResult);
                        latch.countDown();
                    });

                    // 上書き確認ダイアログの結果待ち
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        Platform.exit();
                    }
                }

                if (overwriteDialogResult.equals(MessageDialogEnum.MessageDialogResult.Cancel)) {
                    // 出力を終了する。
                    isCancel = true;
                    break;
                } else if (!overwriteDialogResult.equals(MessageDialogEnum.MessageDialogResult.Yes)
                        && !overwriteDialogResult.equals(MessageDialogEnum.MessageDialogResult.YesToAll)) {
                    // スキップする。
                    skipCount++;
                    continue;
                }
            }

            // 工程分析結果のCSVファイルを出力する。
            createAnalysisReportFile(timeUnit, timeUnitTag, csvFile.getPath(), distribtionData);
            outputCount++;
        }
        return new OutputCsvResult(outputCount, skipCount, isCancel);
    }

    /**
     * 工程分析結果のCSVファイルを出力する。
     *
     * @param timeUnit
     * @param timeUnitTag
     * @param csvFilePath CSVファイルパス
     * @param distribtionData 正規分布表示用データ
     * @throws Exception 
     */
    private void createAnalysisReportFile(Integer timeUnit, String timeUnitTag, String csvFilePath, NomalDistribtionData distribtionData) throws Exception {
        OutputStream output = new FileOutputStream(csvFilePath);
        OutputStreamWriter owriter = new OutputStreamWriter(output, Charset.forName(csvEncode));
        CSVWriter writer = new CSVWriter(owriter, SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, LINEEND);

        // Integerをdoubleに変換
        double[] dataList = new double[distribtionData.getDatas().size()];
        int i = 0;
        for (Long data : distribtionData.getDatas()) {
            dataList[i] = (double) distribtionData.getTakttime() - data;
            i++;
        }

        // ----------
        // 1行目：分析結果項目名
        List<String> row = new ArrayList<>();

        row.add(LocaleUtils.getString("key.ProcessName"));// 工程名
        row.add(LocaleUtils.getString("key.TactTime") + timeUnitTag);// タクトタイム(分)
        row.add(LocaleUtils.getString("key.actualNum"));// 実績件数
        row.add(LocaleUtils.getString("key.itemMaximum") + timeUnitTag);// 最大値(分)
        row.add(LocaleUtils.getString("key.itemMinimum") + timeUnitTag);// 最小値(分)
        row.add(LocaleUtils.getString("key.itemMedium") + timeUnitTag);// 中央値(分)
        row.add(LocaleUtils.getString("key.itemsAverage") + timeUnitTag);// 平均(分)
        row.add(LocaleUtils.getString("key.itemsStandardDeviation"));// 標準偏差
        row.add(LocaleUtils.getString("key.itemsVariance"));// 分散

        writer.writeNext(row.toArray(new String[row.size()]));

        // ----------
        // 2行目：分析結果
        row.clear();

        row.add(String.valueOf(distribtionData.getGraphTitle()));// 分散
        row.add(Integer.toString(distribtionData.getTakttime()));// タクトタイム
        row.add(String.valueOf(distribtionData.getActuals().size()));// 実績件数
        row.add(String.valueOf(new Double(Staristics.maximum(dataList))));// 最大値
        row.add(String.valueOf(new Double(Staristics.minimum(dataList))));// 最小値
        row.add(String.valueOf(new Double(Staristics.medium(dataList))));// 中央値
        row.add(String.valueOf(new Double(Staristics.average(dataList))));// 平均
        row.add(String.valueOf(new Double(Staristics.standardDeviation(dataList))));// 標準偏差
        row.add(String.valueOf(new Double(Staristics.variance(dataList))));// 分散

        writer.writeNext(row.toArray(new String[row.size()]));

        // ----------
        // 3行目：工程実績項目名
        row.clear();

        row.add(LocaleUtils.getString("key.KanbanName"));// カンバン名
        row.add(LocaleUtils.getString("key.WorkflowName"));// 工程順名
        row.add(LocaleUtils.getString("key.ProcessName"));// 工程名
        row.add(LocaleUtils.getString("key.WorkingParson"));// 作業者
        row.add(LocaleUtils.getString("key.EquipmentName"));// 設備名
        row.add(LocaleUtils.getString("key.EditDelayReasonTitle"));// 遅延理由
        row.add(LocaleUtils.getString("key.ImplementTime"));// 実施時間
        row.add(LocaleUtils.getString("key.WorkTime") + timeUnitTag);// 作業時間(分)

        // 実績プロパティ
        List<ActualResultEntity> actuals = distribtionData.getActuals();
        List<String> propNames = new ArrayList();
        actuals.stream().forEach((resultEntity) -> {
            resultEntity.getPropertyCollection().stream().filter((propertyEntity) -> (!propNames.contains(propertyEntity.getActualPropName()))).forEach((propertyEntity) -> {
                propNames.add(propertyEntity.getActualPropName());
            });
        });
        if (!propNames.isEmpty()) {
            for (String propName : propNames) {
                row.add(propName);
            }
        }

        writer.writeNext(row.toArray(new String[row.size()]));

        // ----------
        // 4行目以降：工程実績
        actuals.stream().forEach((actual) -> {
            row.clear();
            row.add(actual.getKanbanName());// カンバン名
            row.add(actual.getWorkflowName());// 工程順名
            row.add(actual.getWorkName());// 工程名
            row.add(actual.getOrganizationName());// 作業者
            row.add(actual.getEquipmentName());// 設備名
            row.add(actual.getDelayReason());// 遅延理由
            row.add(new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat")).format(actual.getImplementDatetime()));// 実施時間
            row.add(Integer.toString(actual.getWorkingTime() / timeUnit));// 作業時間

            // 実績プロパティ
            if (!propNames.isEmpty()) {
                for (String propName : propNames) {
                    StringProperty propValue = actual.getPropertyValue(propName);
                    if (propValue != null) {
                        row.add(propValue.get());
                    } else {
                        row.add("");
                    }
                }
            }

            writer.writeNext(row.toArray(new String[row.size()]));
        });

        // ----------
        // ファイル書き込み
        writer.flush();
        writer.close();
    }

    /**
     * 操作制限を行う
     *
     * @param isBlock True:操作不可/False:操作可
     */
    private void blockUI(Boolean isBlock) {
        sc.blockUI("ContentNaviPane", isBlock);
        progressPane.setVisible(isBlock);
    }

    /**
     * 更新処理
     *
     */
    // インターフェースにした方がいい
    public void update() {
        if (Objects.nonNull(result)) {
            this.blockUI(Boolean.TRUE);
            this.loadSetting();
            Platform.runLater(() -> {
                showGraph();
                this.blockUI(Boolean.FALSE);
            });
        }
    }

    /**
     * CSV出力結果
     */
    private class OutputCsvResult {
        private int outputCount = 0;// 出力件数
        private int skipCount = 0;// スキップ件数
        private boolean isCancel = false;// キャンセルしたか

        /**
         * CSV出力結果
         *
         * @param outputCount 出力件数
         * @param skipCount スキップ件数
         * @param isCancel キャンセルしたか
         */
        public OutputCsvResult(int outputCount, int skipCount, boolean isCancel) {
            this.outputCount = outputCount;
            this.skipCount = skipCount;
            this.isCancel = isCancel;
        }

        /**
         * 出力件数を取得する。
         *
         * @return 出力件数
         */
        public int getOutputCount() {
            return this.outputCount;
        }

        /**
         * スキップ件数を取得する。
         *
         * @return スキップ件数
         */
        public int getSkipCount() {
            return this.skipCount;
        }

        /**
         * キャンセルしたかを取得する。
         *
         * @return キャンセルしたか (true:キャンセルした, false:キャンセルしていない)
         */
        public boolean getIsCancel() {
            return this.isCancel;
        }
    }
}
