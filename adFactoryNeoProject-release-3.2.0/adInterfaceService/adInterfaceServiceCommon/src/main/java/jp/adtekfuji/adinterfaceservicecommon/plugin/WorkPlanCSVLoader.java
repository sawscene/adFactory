/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanPropertyCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanStatusCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportProductCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportWorkKanbanCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportWorkKanbanPropertyCsv;
import jp.adtekfuji.adFactory.entity.importformat.ImportFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.KanbanFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.KanbanPropFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.KanbanStatusFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.ProductFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.WorkKanbanFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.WorkKanbanPropFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.WorkflowRegexInfo;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import static jp.adtekfuji.adinterfaceservicecommon.plugin.common.ProductionNaviPropertyConstants.PREFIX_TMP;
import jp.adtekfuji.adinterfaceservicecommon.plugin.common.UpdateWorkKanbanPropertyLoader;
import jp.adtekfuji.adinterfaceservicecommon.plugin.common.WorkPlanLoader;
import jp.adtekfuji.adinterfaceservicecommon.plugin.common.WorkPlanResultLogger;
import jp.adtekfuji.adinterfaceservicecommon.plugin.entity.WorkPlanInfo;
import jp.adtekfuji.adinterfaceservicecommon.plugin.util.DataParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * CSV形式の生産計画を読み込む
 *
 * @author fu-kato
 */
public class WorkPlanCSVLoader implements WorkPlanLoader, UpdateWorkKanbanPropertyLoader {

    private final Logger logger = LogManager.getLogger();

    private final WorkPlanResultLogger importLogger;
    private String folder;
    private ImportFormatInfo importFormatInfo;
    private boolean ignoreSameKanban;

    /**
     *
     * @param importLogger
     */
    public WorkPlanCSVLoader(WorkPlanResultLogger importLogger) {
        this.importLogger = importLogger;
    }

    /**
     * カンバンをインポートし読み込んだファイルの中身を解析する
     *
     * @param folder フォルダ
     * @param tabMode
     * @param importFormatInfo 設定情報
     * @param ignoreSameKanban 同名のカンバンを無視するか
     * @return
     * @throws Exception
     */
    @Override
    public WorkPlanInfo importKanban(String folder, int tabMode, ImportFormatInfo importFormatInfo, boolean ignoreSameKanban) throws Exception {

        this.folder = folder;
        this.importFormatInfo = importFormatInfo;
        this.ignoreSameKanban = ignoreSameKanban;

        List<ImportKanbanCsv> importKanbans = readKanban();
        List<ImportKanbanPropertyCsv> importKanbanProps = readKanbanProps();
        List<ImportWorkKanbanCsv> importWorkKanbans = readWorkKanbans();
        List<ImportWorkKanbanPropertyCsv> importWkKanbanProps = readWorkKanbanProps();
        List<ImportKanbanStatusCsv> importKanbanStatuss = readKanbanStatus();
        List<ImportProductCsv> importProduct = readProduct();

        return new WorkPlanInfo(importKanbans, importKanbanProps, importWorkKanbans, importWkKanbanProps, importKanbanStatuss, importProduct);
    }

    /**
     * 工程カンバンプロパティ更新ファイルを読み込む
     *
     * @param folder
     * @param tabMode
     * @param importFormatInfo
     * @param ignored
     * @return
     */
    @Override
    public List<ImportWorkKanbanPropertyCsv> importFile(String folder, int tabMode, ImportFormatInfo importFormatInfo, boolean ignored) throws Exception {
        // 名前のみ設定ファイルから取得し、それ以外の設定については工程カンバンプロパティ情報と同じものを取得する
        final WorkKanbanPropFormatInfo updateWorkKanbanPropFormatInfo = importFormatInfo.getUpdateWorkKanbanPropFormatInfo();
        final WorkKanbanPropFormatInfo workKanbanPropFormatInfo = importFormatInfo.getWorkKanbanPropFormatInfo();
        final String filename = updateWorkKanbanPropFormatInfo.getCsvFileName();
        final String workKanbanPropPath = folder + File.separator + PREFIX_TMP + filename;
        final List<ImportWorkKanbanPropertyCsv> importWkKanbanProps = readWorkKanbanPropertyCsv(workKanbanPropPath, workKanbanPropFormatInfo);

        return importWkKanbanProps;
    }

    /**
     * カンバン情報を読み込む。
     *
     * @return
     * @throws Exception
     */
    private List<ImportKanbanCsv> readKanban() throws Exception {
        KanbanFormatInfo kanbanFormatInfo = importFormatInfo.getKanbanFormatInfo();

        // カンバン情報 読込
        String filename = kanbanFormatInfo.getCsvFileName();
        String kanbanPath = folder + File.separator + PREFIX_TMP + filename;
        addResult(LocaleUtils.getString("key.import.work.plan.kanban"));
        List importKanbans = readKanbanCsv(kanbanPath, kanbanFormatInfo);
        if (Objects.isNull(importKanbans) || importKanbans.isEmpty()) {
            addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), filename));// 指定フォルダにカンバン情報ファイルがない
            return null;
        }

        return importKanbans;
    }

    /**
     * カンバンプロパティー情報を読み込む。
     *
     * @return
     * @throws Exception
     */
    private List<ImportKanbanPropertyCsv> readKanbanProps() throws Exception {
        KanbanPropFormatInfo kanbanPropFormatInfo = importFormatInfo.getKanbanPropFormatInfo();

        // カンバンプロパティ情報 読込
        addResult(LocaleUtils.getString("key.import.work.plan.kanban.property"));
        String filename = kanbanPropFormatInfo.getCsvFileName();
        String kanbanPropPath = folder + File.separator + PREFIX_TMP + filename;
        List importKanbanProps = readKanbanPropertyCsv(kanbanPropPath, kanbanPropFormatInfo);
        if (Objects.isNull(importKanbanProps) || importKanbanProps.isEmpty()) {
            addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), filename));// カンバンプロパティ情報ファイル読み込み
        }
        return importKanbanProps;
    }

    /**
     * 工程カンバン情報を読み込む。
     *
     * @return
     * @throws Exception
     */
    private List<ImportWorkKanbanCsv> readWorkKanbans() throws Exception {
        WorkKanbanFormatInfo workKanbanFormatInfo = importFormatInfo.getWorkKanbanFormatInfo();

        // 工程カンバン情報 読込
        addResult(LocaleUtils.getString("key.import.work.plan.work.kanban"));
        String filename = workKanbanFormatInfo.getCsvFileName();
        String workKanbanPath = folder + File.separator + PREFIX_TMP + filename;
        List importWorkKanbans = readWorkKanbanCsv(workKanbanPath, workKanbanFormatInfo);
        if (Objects.isNull(importWorkKanbans) || importWorkKanbans.isEmpty()) {
            addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), filename));// 工程カンバン情報ファイル読み込み
        }
        return importWorkKanbans;
    }

    /**
     * 工程カンバンプロパティー情報を読み込む。
     *
     * @return
     * @throws Exception
     */
    private List<ImportWorkKanbanPropertyCsv> readWorkKanbanProps() throws Exception {
        WorkKanbanPropFormatInfo workKanbanPropFormatInfo = importFormatInfo.getWorkKanbanPropFormatInfo();

        // 工程カンバンプロパティ情報 読込
        addResult(LocaleUtils.getString("key.import.work.plan.work.kanban.property"));
        String filename = workKanbanPropFormatInfo.getCsvFileName();
        String workKanbanPropPath = folder + File.separator + PREFIX_TMP + filename;
        List importWkKanbanProps = readWorkKanbanPropertyCsv(workKanbanPropPath, workKanbanPropFormatInfo);
        if (Objects.isNull(importWkKanbanProps) || importWkKanbanProps.isEmpty()) {
            addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), filename));// 工程カンバンプロパティ情報ファイル読み込み
        }
        return importWkKanbanProps;
    }

    /**
     * 製品情報を読み込む。
     *
     * @return
     * @throws Exception
     */
    private List<ImportProductCsv> readProduct() throws Exception {

        ProductFormatInfo productFormatInfo = importFormatInfo.getProductFormatInfo();

        // 製品情報 読込
        addResult(LocaleUtils.getString("key.import.work.plan.product"));
        String filename = productFormatInfo.getCsvFileName();
        String productPath = folder + File.separator + PREFIX_TMP + filename;
        List importProduct = readProductCsv(productPath, productFormatInfo);
        if (Objects.isNull(importProduct) || importProduct.isEmpty()) {
            addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), filename));
        }
        return importProduct;
    }

    /**
     * カンバンステータス情報を読み込む。
     *
     * @return
     * @throws Exception
     */
    private List<ImportKanbanStatusCsv> readKanbanStatus() throws Exception {
        KanbanStatusFormatInfo kanbanStatusFormatInfo = importFormatInfo.getKanbanStatusFormatInfo();

        // カンバンステータス情報 読込
        addResult(LocaleUtils.getString("key.import.work.plan.kanban.status"));
        String filename = kanbanStatusFormatInfo.getCsvFileName();
        String kanbanStatusPath = folder + File.separator + PREFIX_TMP + filename;
        List importKanbanStatuss = readKanbanStatusCsv(kanbanStatusPath, kanbanStatusFormatInfo);
        if (Objects.isNull(importKanbanStatuss) || importKanbanStatuss.isEmpty()) {
            addResult(String.format("   > %s [%s]", LocaleUtils.getString("key.impprt.data.not"), filename));
        }

        return importKanbanStatuss;
    }

    /**
     * カンバン情報 CSVファイルを読み込む。
     *
     * @param fileName カンバン情報 インポート用ファイル名
     * @param formatInfo カンバンのフォーマット情報
     * @return カンバン情報 インポート用データ一覧
     */
    private List<ImportKanbanCsv> readKanbanCsv(String fileName, KanbanFormatInfo formatInfo) throws Exception {
        if (Files.notExists(Paths.get(fileName))) {
            return Collections.emptyList();
        }

        List<ImportKanbanCsv> importKanbans = new LinkedList();
        int count = 0;
        String kanbanName = "";

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // 読み込み設定情報
        int startRow = StringUtils.parseInteger(formatInfo.getCsvStartRow());// CSV: 読み込み開始行
        int colHierarchyName = StringUtils.parseInteger(formatInfo.getCsvHierarchyName());// CSV: カンバン階層
        int colKanbanName = StringUtils.parseInteger(formatInfo.getCsvKanbanName());// CSV: カンバン名
        int colWorkflowName = StringUtils.parseInteger(formatInfo.getCsvWorkflowName());// CSV: 工程順名
        int colWorkflowRev = StringUtils.parseInteger(formatInfo.getCsvWorkflowRev());// CSV: 工程順版数
        int colModelName = StringUtils.parseInteger(formatInfo.getCsvModelName());// CSV: モデル名
        int colStartDatetime = StringUtils.parseInteger(formatInfo.getCsvStartDateTime());// CSV: 開始予定日時
        int colProductionType = StringUtils.parseInteger(formatInfo.getCsvProductionType());// CSV: 生産タイプ
        int colLotQuantity = StringUtils.parseInteger(formatInfo.getCsvLotNum());// CSV: ロット数量
        int colProductionNumber = StringUtils.parseInteger(formatInfo.getCsvProductionNumber());// CSV: 製造番号

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            String datas[];
            while ((datas = csvReader.readNext()) != null) {
                count++;

                if (count < startRow) {
                    continue;
                }

                ImportKanbanCsv data = new ImportKanbanCsv();

                // カンバン階層
                if (formatInfo.getIsCheckKanbanHierarchy()) {
                    // カンバン階層を指定する場合、フォーマット情報のカンバン階層名をセットする。
                    data.setKanbanHierarchyName(formatInfo.getKanbanHierarchyName());
                }

                for (int i = 0; i < datas.length; i++) {
                    try {
                        String value = Objects.isNull(datas[i]) ? "" : datas[i];

                        if (i == (colHierarchyName - 1)) {
                            // カンバン階層名 (カンバン階層を指定しない場合、カンバン階層名をセットする)
                            if (!formatInfo.getIsCheckKanbanHierarchy()) {
                                data.setKanbanHierarchyName(value);
                            }
                        } else if (i == (colKanbanName - 1)) {
                            // カンバン名
                            data.setKanbanName(value);
                        } else if (i == (colWorkflowName - 1)) {
                            // 工程順名 (モデル名で工程順を指定しない場合、工程順名をセットする)
                            if (!formatInfo.getIsCheckWorkflowRegex()) {
                                data.setWorkflowName(value);
                            }
                        } else if (i == (colWorkflowRev - 1)) {
                            // 版数 (モデル名で工程順を指定しない場合、版数をセットする)
                            if (!formatInfo.getIsCheckWorkflowRegex()) {
                                data.setWorkflowRev(value);
                            }
                            data.setWorkflowRev(Objects.isNull(datas[i]) ? "" : datas[i]);
                        } else if (i == (colModelName - 1)) {
                            // モデル名
                            data.setModelName(value);

                            // モデル名で工程順を指定する場合、条件に合う工程順名をセットする。
                            if (formatInfo.getIsCheckWorkflowRegex()) {
                                for (WorkflowRegexInfo regexInfo : formatInfo.getWorkflowRegexInfos()) {
                                    if (data.getModelName().matches(regexInfo.getRegex())) {
                                        data.setWorkflowName(regexInfo.getWorkflowName());
                                        data.setWorkflowRev(String.valueOf(regexInfo.getWorkflowRev()));
                                        break;
                                    }
                                }
                            }
                        } else if (i == (colStartDatetime - 1)) {
                            // 計画開始日時
                            data.setStartDatetime(value);
                        } else if (i == (colProductionType - 1)) {
                            // 生産タイプ
                            int productionType = 0;

                            try {
                                productionType = DataParser.parseProductionType(value);
                            } catch (Exception ex) {
                                logger.fatal(ex, ex);
                                this.addResult(String.format("[%d] > %s [%s]", count, LocaleUtils.getString("key.invalid.value"), LocaleUtils.getString("key.ProductionType")));
                            }

                            data.setProductionType(String.valueOf(productionType));
                        } else if (i == (colLotQuantity - 1)) {
                            // ロット数量
                            int lotNum = 1;

                            try {
                                lotNum = DataParser.parseLotNum(value);
                            } catch (Exception ex) {
                                logger.fatal(ex, ex);
                                this.addResult(String.format("[%d] > %s [%s]", count, LocaleUtils.getString("key.invalid.value"), LocaleUtils.getString("key.LotQuantity")));
                            }

                            data.setLotQuantity(String.valueOf(lotNum));
                        } else if (i == (colProductionNumber - 1)) {
                            // 製造番号
                            data.setProductionNumber(value);
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        this.addResult(String.format("[%d] > %s", count, LocaleUtils.getString("key.import.production.error")));
                    }
                }

                // モデル名と工程順を組み合わせて検索する設定の場合は検索するときに結合させるためここでは設定のみ保持する
                data.setEnableConcat(formatInfo.getIsCheckWorkflowWithModel());

                if (this.ignoreSameKanban && Objects.equals(kanbanName, data.getKanbanName())) {
                    continue;
                }

                importKanbans.add(data);
                kanbanName = data.getKanbanName();
            }

            // ※.対象日指定は未対応。

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importKanbans = Collections.emptyList();
            // 読み込み中に、不明なエラーが発生しました。
            this.addResult("   > " + LocaleUtils.getString("key.import.production.error"));
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return importKanbans;
    }

    /**
     * カンバンプロパティ情報 CSVファイルを読み込む。
     *
     * @param fileName カンバンプロパティ情報 インポート用ファイル名
     * @param formatInfo カンバンプロパティのフォーマット情報
     * @return カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportKanbanPropertyCsv> readKanbanPropertyCsv(String fileName, KanbanPropFormatInfo formatInfo) throws Exception {
        if (Files.notExists(Paths.get(fileName))) {
            return Collections.emptyList();
        }

        if ("2".equals(formatInfo.getSelectedFormat())) {
            // フォーマットB
            return readKanbanPropertyF2Csv(fileName, formatInfo);
        } else {
            // フォーマットA
            return readKanbanPropertyF1Csv(fileName, formatInfo);
        }
    }

    /**
     * カンバンプロパティ情報(フォーマットA) CSVファイルを読み込む。
     *
     * @param fileName カンバンプロパティ情報 インポート用ファイル名
     * @param formatInfo カンバンプロパティのフォーマット情報
     * @return カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportKanbanPropertyCsv> readKanbanPropertyF1Csv(String fileName, KanbanPropFormatInfo formatInfo) throws Exception {
        List<ImportKanbanPropertyCsv> importKanbanProps = new LinkedList();
        int count = 0;

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // 読み込み設定情報
        int startRow = StringUtils.parseInteger(formatInfo.getCsvStartRow());// CSV: 読み込み開始行
        int colKanbanName = StringUtils.parseInteger(formatInfo.getCsvKanbanName());// CSV: カンバン名
        int colKanbanPropertyName = StringUtils.parseInteger(formatInfo.getCsvPropName());// CSV: プロパティ名
        int colKanbanPropertyType = StringUtils.parseInteger(formatInfo.getCsvPropType());// CSV: プロパティ型
        int colKanbanPropertyValue = StringUtils.parseInteger(formatInfo.getCsvPropValue());// CSV: プロパティ値

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            String datas[];
            while ((datas = csvReader.readNext()) != null) {
                count++;

                if (count < startRow) {
                    continue;
                }

                ImportKanbanPropertyCsv data = new ImportKanbanPropertyCsv();

                for (int i = 0; i < datas.length; i++) {
                    if (i == (colKanbanName - 1)) {
                        // カンバン名
                        data.setKanbanName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colKanbanPropertyName - 1)) {
                        // プロパティ名
                        data.setKanbanPropertyName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colKanbanPropertyType - 1)) {
                        // プロパティ型
                        data.setKanbanPropertyType(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colKanbanPropertyValue - 1)) {
                        // プロパティ値
                        data.setKanbanPropertyValue(Objects.isNull(datas[i]) ? "" : datas[i]);
                    }
                }

                importKanbanProps.add(data);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importKanbanProps = Collections.emptyList();
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return importKanbanProps;
    }

    /**
     * カンバンプロパティ情報(フォーマットB) CSVファイルを読み込む。
     *
     * @param fileName カンバンプロパティ情報 インポート用ファイル名
     * @param formatInfo カンバンプロパティのフォーマット情報
     * @return カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportKanbanPropertyCsv> readKanbanPropertyF2Csv(String fileName, KanbanPropFormatInfo formatInfo) throws Exception {
        List<ImportKanbanPropertyCsv> importKanbanProps = new ArrayList();

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // ヘッダー行
        int headerRow = StringUtils.parseInteger(formatInfo.getF2CsvHeaderRow());
        // 読み込み開始行
        int startRow = StringUtils.parseInteger(formatInfo.getF2CsvStartRow());
        // カンバン名
        int colKanbanName = StringUtils.parseInteger(formatInfo.getF2CsvKanbanName());

        // プロパティ
        List<Integer> colProps = new ArrayList();
        for (String propColumn : formatInfo.getF2CsvPropValues()) {
            int colProp = StringUtils.parseInteger(propColumn);
            colProps.add(colProp);
        }

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            List<List<String>> dataList = new ArrayList<>();
            String[] datas;
            while ((datas = csvReader.readNext()) != null) {
                dataList.add(Arrays.asList(datas));
            }

            // ヘッダー
            List<String> header = dataList.get(headerRow - 1);

            // データ
            for (int i = startRow - 1; i < dataList.size(); i++) {
                List<String> row = dataList.get(i);

                String kanbanName = row.get(colKanbanName - 1);

                for (Integer colProp : colProps) {
                    ImportKanbanPropertyCsv data = new ImportKanbanPropertyCsv();
                    data.setKanbanName(kanbanName);
                    if (!(colProp < 1 || colProp > row.size())) {
                        data.setKanbanPropertyName(header.get(colProp - 1));
                        data.setKanbanPropertyType(CustomPropertyTypeEnum.TYPE_STRING.name());
                        data.setKanbanPropertyValue(Objects.isNull(row.get(colProp - 1)) ? "" : row.get(colProp - 1));

                        importKanbanProps.add(data);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importKanbanProps = Collections.emptyList();
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return importKanbanProps;
    }

    /**
     * 工程カンバン情報 CSVファイルを読み込む。
     *
     * @param fileName 工程カンバン情報 インポート用ファイル名
     * @param formatInfo 工程カンバンのフォーマット情報
     * @return 工程カンバン情報 インポート用データ一覧
     */
    private List<ImportWorkKanbanCsv> readWorkKanbanCsv(String fileName, WorkKanbanFormatInfo formatInfo) throws Exception {
        if (Files.notExists(Paths.get(fileName))) {
            return Collections.emptyList();
        }

        List<ImportWorkKanbanCsv> importWorkKanbans = new ArrayList();
        int count = 0;

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // 読み込み設定情報
        int startRow = StringUtils.parseInteger(formatInfo.getCsvStartRow());// CSV: 読み込み開始行
        int colKanbanName = StringUtils.parseInteger(formatInfo.getCsvKanbanName());// CSV: カンバン名
        int colWorkNum = StringUtils.parseInteger(formatInfo.getCsvWorkNum());// CSV: 工程の番号
        int colSkipFlag = StringUtils.parseInteger(formatInfo.getCsvSkipFlag());// CSV: スキップ
        int colStartDatetime = StringUtils.parseInteger(formatInfo.getCsvStartDateTime());// CSV: 開始予定日時
        int colCompDatetime = StringUtils.parseInteger(formatInfo.getCsvCompDateTime());// CSV: 完了予定日時
        int colOrganizations = StringUtils.parseInteger(formatInfo.getCsvOrganizationIdentName());// CSV: 組織識別名
        int colEquipments = StringUtils.parseInteger(formatInfo.getCsvEquipmentIdentName());// CSV: 設備識別名
        int colWorkName = StringUtils.parseInteger(formatInfo.getCsvWorkName());// CSV: 工程名
        int colTactTime = StringUtils.parseInteger(formatInfo.getCsvTactTime());// CSV: タクトタイム

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            String datas[];
            while ((datas = csvReader.readNext()) != null) {
                count++;

                if (count < startRow) {
                    continue;
                }

                ImportWorkKanbanCsv data = new ImportWorkKanbanCsv();

                for (int i = 0; i < datas.length; i++) {
                    if (i == (colKanbanName - 1)) {
                        // カンバン名
                        data.setKanbanName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colWorkNum - 1)) {
                        // 工程の番号
                        data.setWorkNum(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colSkipFlag - 1)) {
                        // スキップフラグ
                        data.setSkipFlag(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colStartDatetime - 1)) {
                        // 開始予定日時
                        data.setStartDatetime(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colCompDatetime - 1)) {
                        // 完了予定日時
                        data.setCompDatetime(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colOrganizations - 1)) {
                        // 組織識別名
                        data.setOrganizations(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colEquipments - 1)) {
                        // 設備識別名
                        data.setEquipments(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colWorkName - 1)) {
                        // 工程名
                        data.setWorkName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colTactTime - 1)) {
                        // タクトタイム
                        data.setTactTime(StringUtils.isEmpty(datas[i]) ? "" : toSecond(datas[i]));
                    }
                }

                importWorkKanbans.add(data);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importWorkKanbans = Collections.emptyList();
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return importWorkKanbans;
    }

    /**
     * カンバンステータス情報 CSVファイルを読み込む。
     *
     * @param fileName カンバンステータス情報 インポート用ファイル名
     * @param formatInfo カンバンステータスのフォーマット情報
     * @return カンバンステータス情報 インポート用データ一覧
     */
    private List<ImportKanbanStatusCsv> readKanbanStatusCsv(String fileName, KanbanStatusFormatInfo formatInfo) throws Exception {
        if (Files.notExists(Paths.get(fileName))) {
            return Collections.emptyList();
        }

        List<ImportKanbanStatusCsv> datas = new ArrayList();
        int count = 0;

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // 読み込み設定情報
        int startRow = StringUtils.parseInteger(formatInfo.getCsvStartRow());// CSV: 読み込み開始行
        int colKanbanName = StringUtils.parseInteger(formatInfo.getCsvKanbanName());// CSV: カンバン名
        int colKanbanStatus = StringUtils.parseInteger(formatInfo.getCsvKanbanStatus());// CSV: ステータス

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            String csvDatas[];
            while ((csvDatas = csvReader.readNext()) != null) {
                count++;

                if (count < startRow) {
                    continue;
                }

                ImportKanbanStatusCsv data = new ImportKanbanStatusCsv();

                for (int i = 0; i < csvDatas.length; i++) {
                    if (i == (colKanbanName - 1)) {
                        // カンバン名
                        data.setKanbanName(Objects.isNull(csvDatas[i]) ? "" : csvDatas[i]);
                    } else if (i == (colKanbanStatus - 1)) {
                        // カンバンステータス
                        data.setKanbanStatus(Objects.isNull(csvDatas[i]) ? "" : csvDatas[i]);
                    }
                }
                datas.add(data);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            datas = Collections.emptyList();
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return datas;
    }

    /**
     * 工程カンバンプロパティ情報 CSVファイルを読み込む。
     *
     * @param fileName 工程カンバンプロパティ情報 インポート用ファイル名
     * @param formatInfo 工程カンバンプロパティのフォーマット情報
     * @return 工程カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportWorkKanbanPropertyCsv> readWorkKanbanPropertyCsv(String fileName, WorkKanbanPropFormatInfo formatInfo) throws Exception {
        if (Files.notExists(Paths.get(fileName))) {
            return Collections.emptyList();
        }

        List<ImportWorkKanbanPropertyCsv> importWkKanbanProps;
        if ("2".equals(formatInfo.getSelectedFormat())) {
            // フォーマットB
            importWkKanbanProps = readWorkKanbanPropertyF2Csv(fileName, formatInfo);
        } else {
            // フォーマットA
            importWkKanbanProps = readWorkKanbanPropertyF1Csv(fileName, formatInfo);
        }

        return importWkKanbanProps;
    }

    /**
     * 工程カンバンプロパティ情報(フォーマットA) CSVファイルを読み込む。
     *
     * @param fileName 工程カンバンプロパティ情報 インポート用ファイル名
     * @param formatInfo 工程カンバンプロパティのフォーマット情報
     * @return 工程カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportWorkKanbanPropertyCsv> readWorkKanbanPropertyF1Csv(String fileName, WorkKanbanPropFormatInfo formatInfo) throws Exception {
        List<ImportWorkKanbanPropertyCsv> importWkKanbanProps = new ArrayList();
        int count = 0;

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // 読み込み設定情報
        int startRow = StringUtils.parseInteger(formatInfo.getCsvStartRow());// CSV: 読み込み開始行
        int colKanbanName = StringUtils.parseInteger(formatInfo.getCsvKanbanName());// CSV: カンバン名
        int colWorkName = StringUtils.parseInteger(formatInfo.getCsvWorkName());// CSV: 工程名
        int colWorkNum = StringUtils.parseInteger(formatInfo.getCsvWorkNum());// CSV: 工程の番号
        int colPropName = StringUtils.parseInteger(formatInfo.getCsvPropName());// CSV: プロパティ名
        int colPropType = StringUtils.parseInteger(formatInfo.getCsvPropType());// CSV: プロパティ型
        int colPropValue = StringUtils.parseInteger(formatInfo.getCsvPropValue());// CSV: プロパティ値

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            String datas[];
            while ((datas = csvReader.readNext()) != null) {
                count++;

                if (count < startRow) {
                    continue;
                }

                ImportWorkKanbanPropertyCsv data = new ImportWorkKanbanPropertyCsv();

                for (int i = 0; i < datas.length; i++) {
                    if (i == (colKanbanName - 1)) {
                        // カンバン名
                        data.setKanbanName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colWorkName - 1)) {
                        // 工程名
                        data.setWorkName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colWorkNum - 1)) {
                        // 工程の番号
                        data.setWorkNum(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colPropName - 1)) {
                        // プロパティ名
                        data.setWkKanbanPropName(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colPropType - 1)) {
                        // プロパティ型
                        data.setWkKanbanPropType(Objects.isNull(datas[i]) ? "" : datas[i]);
                    } else if (i == (colPropValue - 1)) {
                        // プロパティ値
                        data.setWkKanbanPropValue(Objects.isNull(datas[i]) ? "" : datas[i]);
                    }
                }

                importWkKanbanProps.add(data);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importWkKanbanProps = null;
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return importWkKanbanProps;
    }

    /**
     * 工程カンバンプロパティ情報(フォーマットB) CSVファイルを読み込む。
     *
     * @param fileName 工程カンバンプロパティ情報 インポート用ファイル名
     * @param formatInfo 工程カンバンプロパティのフォーマット情報
     * @return 工程カンバンプロパティ情報 インポート用データ一覧
     */
    private List<ImportWorkKanbanPropertyCsv> readWorkKanbanPropertyF2Csv(String fileName, WorkKanbanPropFormatInfo formatInfo) throws Exception {

        List<ImportWorkKanbanPropertyCsv> importWorkKanbanProps = new ArrayList();

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // ヘッダー行
        int headerRow = StringUtils.parseInteger(formatInfo.getF2CsvHeaderRow());
        // 読み込み開始行
        int startRow = StringUtils.parseInteger(formatInfo.getF2CsvStartRow());
        // カンバン名
        int colKanbanName = StringUtils.parseInteger(formatInfo.getF2CsvKanbanName());
        // 工程名
        int colWorkName = StringUtils.parseInteger(formatInfo.getF2CsvWorkName());
        // 工程の番号
        int colWorkNum = StringUtils.parseInteger(formatInfo.getF2CsvWorkNo());

        // プロパティ
        List<Integer> colProps = new ArrayList();
        for (String propColumn : formatInfo.getF2CsvPropValues()) {
            int colProp = StringUtils.parseInteger(propColumn);
            colProps.add(colProp);
        }

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            List<List<String>> dataList = new ArrayList<>();
            String[] datas;
            while ((datas = csvReader.readNext()) != null) {
                dataList.add(Arrays.asList(datas));
            }

            // ヘッダー
            List<String> header = dataList.get(headerRow - 1);

            HashMap<String, Integer> unionPropCountMap = new HashMap<>();

            // データ
            for (int i = startRow - 1; i < dataList.size(); i++) {
                List<String> row = dataList.get(i);

                String unionPropLeft = null;
                String unionPropRight = null;

                String kanbanName = colKanbanName > 0 ? row.get(colKanbanName - 1) : "";
                String workName = colWorkName > 0 ? row.get(colWorkName - 1) : "";
                String workNum = colWorkNum > 0 ? row.get(colWorkNum - 1) : "";

                for (Integer colProp : colProps) {
                    if (!(colProp <= 0 || colProp > row.size())) {

                        String propName = header.get(colProp - 1);
                        String propValue = Objects.isNull(row.get(colProp - 1)) ? "" : row.get(colProp - 1);

                        // プロパティを組み合わせて読み込む
                        if (Objects.nonNull(formatInfo.getF2IsCheckUnionProp()) && formatInfo.getF2IsCheckUnionProp()) {
                            boolean targetPropFlg = false;
                            // 結合対象のプロパティなら一時格納
                            if (propName.equals(formatInfo.getF2UnionPropLeftName())) {
                                unionPropLeft = propValue;
                                targetPropFlg = true;
                            } else if (propName.equals(formatInfo.getF2UnionPropRightName())) {
                                unionPropRight = propValue;
                                targetPropFlg = true;
                            }

                            if (targetPropFlg) {
                                if (Objects.isNull(unionPropLeft) || Objects.isNull(unionPropRight)) {
                                    // 揃っていなければ次プロパティへ
                                    continue;
                                } else {
                                    // 組み合わせたプロパティの連番を記録
                                    String key = kanbanName + workName + workNum;
                                    int unionPropCount = 1;
                                    unionPropCountMap.get(key);
                                    if (unionPropCountMap.containsKey(key)) {
                                        unionPropCount += unionPropCountMap.get(key);
                                        unionPropCountMap.put(key, unionPropCount);
                                    } else {
                                        unionPropCountMap.put(key, unionPropCount);
                                    }
                                    // プロパティ名を新しいプロパティ名に変更(サフィックス：連番)
                                    propName = formatInfo.getF2UnionPropNewName() + unionPropCount;
                                    // プロパティ値を結合(デリミタ：\t)
                                    propValue = unionPropLeft + "\\t" + unionPropRight;
                                    unionPropLeft = unionPropRight = null;
                                }
                            }
                        }

                        ImportWorkKanbanPropertyCsv data = new ImportWorkKanbanPropertyCsv();
                        data.setKanbanName(kanbanName);
                        data.setWorkName(workName);
                        data.setWorkNum(workNum);
                        data.setWkKanbanPropName(propName);
                        data.setWkKanbanPropType(CustomPropertyTypeEnum.TYPE_STRING.name());
                        data.setWkKanbanPropValue(propValue);

                        importWorkKanbanProps.add(data);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importWorkKanbanProps = null;
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }

        return importWorkKanbanProps;
    }

    /**
     * 製品情報 CSVファイルを読み込む。
     *
     * @param fileName 製品情報 インポート用ファイル名
     * @param formatInfo 製品のフォーマット情報
     * @return 製品情報 インポート用データ一覧
     */
    private List<ImportProductCsv> readProductCsv(String fileName, ProductFormatInfo formatInfo) throws Exception {
        if (Files.notExists(Paths.get(fileName))) {
            return Collections.emptyList();
        }

        List<ImportProductCsv> datas = new ArrayList();
        int count = 0;

        // ファイルの文字コード
        String encode = this.encodeUpperCase(formatInfo.getCsvFileEncode());

        // 読み込み設定情報
        int startRow = StringUtils.parseInteger(formatInfo.getCsvStartRow());
        int colKanbanName = StringUtils.parseInteger(formatInfo.getCsvKanbanName());
        int colUniqueId = StringUtils.parseInteger(formatInfo.getCsvUniqueID());

        CSVReader csvReader = null;
        try {
            // ファイル読み込み
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(fileName), encode));

            String csvDatas[];
            while ((csvDatas = csvReader.readNext()) != null) {
                count++;

                if (count < startRow) {
                    continue;
                }

                ImportProductCsv data = new ImportProductCsv();

                for (int i = 0; i < csvDatas.length; i++) {
                    if (i == (colKanbanName - 1)) {
                        // カンバン名
                        data.setKanbanName(Objects.isNull(csvDatas[i]) ? "" : csvDatas[i]);
                    } else if (i == (colUniqueId - 1)) {
                        // 製品シリアル
                        data.setUniqueId(Objects.isNull(csvDatas[i]) ? "" : csvDatas[i]);
                    }
                }
                datas.add(data);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            datas = null;
            this.addResult(ex.getLocalizedMessage());
        } finally {
            if (Objects.nonNull(csvReader)) {
                try {
                    csvReader.close();
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        return datas;
    }

    /**
     * HH:mm:ss形式の文字列を秒数を表す文字列に変換する。
     *
     * @param value HH:mm:ss形式の文字列 ("12:23:34") or yyyy/MM/dd HH:mm:ss形式の文字列
     * @return 整数の文字列 ("44614")
     */
    private String toSecond(String value) {
        String result = "";
        int seconds = 0;
        Date date;
        try {
            try {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                date = format.parse(value);
            } catch (ParseException ex) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                date = format.parse(value);
            }
            Calendar cale = Calendar.getInstance();
            cale.setTime(date);
            seconds += cale.get(Calendar.HOUR) * 60 * 60;
            seconds += cale.get(Calendar.MINUTE) * 60;
            seconds += cale.get(Calendar.SECOND);
            result = Integer.toString(seconds);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * エンコード文字列を大文字に変換する。(SJISはMS932に変換する)
     *
     * @param fileEncode エンコード文字列
     * @return 大文字のエンコード文字列
     */
    private String encodeUpperCase(String fileEncode) {
        // ファイルの文字コード
        String encode = fileEncode.toUpperCase();
        // シフトJISの場合はMS932を指定する。
        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS", "S-JIS").contains(encode)) {
            encode = "MS932";
        }
        return encode;
    }

    /**
     * ログを出力する。
     *
     * @param string
     */
    private void addResult(String string) {
        importLogger.addResult(string);
    }
}
