/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adreporter.rmi;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;
import jp.adtekfuji.adFactory.adreporter.beans.ReportBean;
import jp.adtekfuji.adFactory.adreporter.info.DisposalSlipInfo;
import jp.adtekfuji.adFactory.enumerate.OutputReportResultEnum;
import jp.adtekfuji.adappentity.ProductEntity;
import jp.adtekfuji.adreporter.common.ExcelReportFactory;
import jp.adtekfuji.adreporter.common.ReporterConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 帳票発行
 *
 * @author nar-nakamura
 */
public class ReportBeanImpl implements ReportBean, Serializable {

    private final Logger logger = LogManager.getLogger();

    private static final String CURRENT_DIR = new File(System.getProperty("user.dir")).getParent();// adReporterフォルダ

    private static final String EXT_DIR = Paths.get(CURRENT_DIR, "ext").toString();// adReporter/extフォルダ
    private static final String TEMP_DIR = Paths.get(CURRENT_DIR, "temp").toString();// adReporter/tempフォルダ
    private static final String PRINT_SCRIPT_PATH = Paths.get(CURRENT_DIR, "bin", "print_excel.vbs").toString();// Excelワークブック印刷スクリプト

    /**
     * 廃棄伝票発行
     *
     * @param disposalSlipInfo 廃棄伝票情報
     * @return 出力結果
     * @throws RemoteException
     */
    @Override
    public OutputReportResultEnum outputDisposal(DisposalSlipInfo disposalSlipInfo) throws RemoteException {
        logger.info("outputDisposal: {}", disposalSlipInfo);
        try {
            String templateFileName = ReporterConfig.getDisposalTemplate();// 廃棄伝票テンプレートファイル名

            File templateFile = new File(EXT_DIR, templateFileName);
            if (!templateFile.exists()) {
                // テンプレートファイルがない
                logger.warn("template file not found.({})", templateFile);
                return OutputReportResultEnum.TEMPLATE_NOT_FOUND;
            }

            int index = 0;
            for (ProductEntity product : disposalSlipInfo.getProducts()) {
                index++;
                
                String templateName = templateFile.getName().substring(0, templateFile.getName().lastIndexOf('.'));

                String inFileExt = templateFile.toString().substring(templateFile.toString().lastIndexOf("."));

                String outputFileName = new StringBuilder()
                        .append(templateName)
                        .append("_")
                        .append(disposalSlipInfo.getEquipmentIdentName())// 発行元の作業者端末
                        .append("_")
                        .append(index)
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
                boolean replaceResult = excelReport.replaceDisposalSlipTags(templateName, disposalSlipInfo, product);
                if (!replaceResult) {
                    // 置換失敗
                    logger.warn("tag replace failed.({})", templateFile);
                    return OutputReportResultEnum.FATAL;
                }

                // ワークブックを保存する。
                boolean saveResult = excelReport.saveWorkbook(templateName, outputFile.toFile());
                if (!saveResult) {
                    // 保存失敗
                    logger.warn("warkbook save failed.({})", outputFile);
                    return OutputReportResultEnum.WORKBOOK_SAVE_FAILED;
                }

                // ワークブックを印刷する。
                this.printWorkbook(outputFile.toString());
            }
            
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
    private void printWorkbook(String workbookPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", PRINT_SCRIPT_PATH, workbookPath);
            Process process = pb.start();
            process.waitFor(10, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }
}
