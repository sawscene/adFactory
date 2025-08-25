/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.utils;

import adtekfuji.admanagerapp.warehouseplugin.common.ExcelReportFactory;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;
import adtekfuji.admanagerapp.warehouseplugin.entity.AcceptanceInfo;
import adtekfuji.admanagerapp.warehouseplugin.enumerate.OutputReportResultEnum;
import adtekfuji.admanagerapp.warehouseplugin.common.ReporterConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 帳票発行
 *
 * @author s-morita
 */
public class OutputForm implements Serializable {

    private static final Logger logger = LogManager.getLogger();

    private static final String CURRENT_DIR = new File(System.getenv("ADFACTORY_HOME")).getPath();// adフォルダ

    private static final String EXT_DIR = Paths.get(CURRENT_DIR, "template").toString();// adFactory/extフォルダ
    private static final String TEMP_DIR = Paths.get(CURRENT_DIR, "temp/warehouseAcceptance").toString();// adFactory/temp/warehouseAcceptanceフォルダ
    private static final String PRINT_SCRIPT_PATH = Paths.get(CURRENT_DIR, "bin", "print_excel.vbs").toString();// Excelワークブック印刷スクリプト

    /**
     * 現品票発行
     *
     * @param acceptanceInfo 現品票情報
     * @return 出力結果
     * @throws RemoteException
     */
    public static OutputReportResultEnum outputAcceptLabel(AcceptanceInfo acceptanceInfo) throws RemoteException {
        logger.info("outputSpotTiket: {}", acceptanceInfo);
        try {
            String templateFileName = ReporterConfig.getAcceptanceTemplate();// 現品票テンプレートファイル名

            File templateFile = new File(EXT_DIR, templateFileName);
            if (!templateFile.exists()) {
                // テンプレートファイルがない
                logger.warn("template file not found.({})", templateFile);
                return OutputReportResultEnum.TEMPLATE_NOT_FOUND;
            }

            File folder = new File(TEMP_DIR);
            if (!folder.exists()) {
                // フォルダがない場合は作成する。
                if (!folder.mkdirs()) {
                    return OutputReportResultEnum.FATAL;
                }
            }

            String templateName = templateFile.getName().substring(0, templateFile.getName().lastIndexOf('.'));

            String inFileExt = templateFile.toString().substring(templateFile.toString().lastIndexOf("."));

            String outputFileName = new StringBuilder()
                    .append(templateName)
                    .append("_")
                    .append("Replace")
                    .append(inFileExt)
                    .toString();

            Path outputFile = Paths.get(TEMP_DIR, outputFileName);
            if (outputFile.toFile().exists()) {
                outputFile.toFile().delete();
            }

            ExcelReportFactory excelReport = new ExcelReportFactory();

            // テンプレートを読み込む。
            boolean loadResult = excelReport.loadTemplateWorkbook(templateFile);
            if (!loadResult) {
                // 読込失敗
                logger.warn("template file load failed.({})", templateFile);
                return OutputReportResultEnum.TEMPLATE_LOAD_FAILED;
            }

            // タグを置換する。
            boolean replaceResult = excelReport.replaceAcceptanceSlipTags(templateName, acceptanceInfo);
            if (!replaceResult) {
                // 置換失敗
                logger.warn("tag replace failed.({})", templateFile);
                return OutputReportResultEnum.REPLACE_FATAL;
            }

            // ワークブックを保存する。
            boolean saveResult = excelReport.saveWorkbook(templateName, outputFile.toFile());
            if (!saveResult) {
                // 保存失敗
                logger.warn("warkbook save failed.({})", outputFile);
                return OutputReportResultEnum.WORKBOOK_SAVE_FAILED;
            }

            // ワークブックを印刷する。
            printWorkbook(outputFile.toString(), acceptanceInfo.getPrintNum());

            return OutputReportResultEnum.SUCCESS;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return OutputReportResultEnum.FATAL;
    }

    /**
     * Excelワークブックを印刷する。
     *
     * @param workbookPath Excelワークブックのファイルパス
     */
    private static void printWorkbook(String workbookPath, int printNum) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", PRINT_SCRIPT_PATH, workbookPath);
            for(int i = 0; i < printNum; i++){
                Process process = pb.start();
                process.waitFor(10, TimeUnit.SECONDS);
            }
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }
}
