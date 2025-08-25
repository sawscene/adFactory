/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils.fuji;

import adtekfuji.admanagerapp.productionnaviplugin.common.fuji.ProductionNaviPropertyConstantsFuji;
import adtekfuji.admanagerapp.productionnaviplugin.property.fuji.ImportFormatInfo;
import adtekfuji.admanagerapp.productionnaviplugin.property.fuji.BomFormatInfo;
import adtekfuji.admanagerapp.productionnaviplugin.property.fuji.OrderFormatInfo;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.xml.bind.JAXB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FUJI インポートフォーマット設定ファイル ユーティリティ
 *
 * @author nar-nakamura
 */
public class ImportFormatFileUtil {

    private static final Logger logger = LogManager.getLogger();

    private static final String CONF_PATH = new StringBuilder(System.getenv("ADFACTORY_HOME")).append(File.separator).append("conf").toString();
    private static final String IMPORT_FORMAT_FILE = "adFactoryImportFormatFuji.xml";

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATETIME_FORMAT_2 = "yyyy/MM/dd HH:mm:ss";
    private static final String DATETIME_FORMAT_3 = "yyyy/M/d H:m:s";
    private static final String DATETIME_FORMAT_4 = "yyyy/M/d H:m";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_FORMAT_2 = "yyyy/MM/dd";
    private static final String DATE_FORMAT_3 = "yyyy/M/d";

    /**
     * インポートフォーマット設定をファイルから読み込む。
     *
     * @return インポートフォーマット設定
     */
    public static ImportFormatInfo load() {
        logger.info("load");
        ImportFormatInfo info;
        try {
            File file = new File(CONF_PATH, IMPORT_FORMAT_FILE);
            if (file.exists()) {
                // 設定をファイルから読み込む。
                info = JAXB.unmarshal(file, ImportFormatInfo.class);
            } else {
                info = new ImportFormatInfo();
            }

            // 未設定の項目にデフォルト値をセットする。
            fillOrderFormatInfoValue(info);
            fillBomFormatInfoValue(info);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            info = new ImportFormatInfo();
        }
        return info;
    }

    /**
     * インポートフォーマット設定をファイルに保存する。
     *
     * @param info インポートフォーマット設定
     * @return 結果 (true:成功, false：失敗)
     */
    public static boolean save(ImportFormatInfo info) {
        logger.info("save:{}", info);
        boolean result = false;
        try {
            // 設定をファイルに保存する。
            JAXB.marshal(info, new File(CONF_PATH, IMPORT_FORMAT_FILE));

            result = true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return result;
    }

    /**
     * 計画情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillOrderFormatInfoValue(ImportFormatInfo info) {
        try {
            // 計画情報
            if (Objects.isNull(info.getOrderFormatInfo())) {
                info.setOrderFormatInfo(new OrderFormatInfo());
            }

            // CSV: ファイル名
            if (Objects.isNull(info.getOrderFormatInfo().getCsvFileName())) {
                info.getOrderFormatInfo().setCsvFileName(ProductionNaviPropertyConstantsFuji.INIT_PROD_ORDER_CSV_FILE);
            }
            // CSV: エンコード
            if (Objects.isNull(info.getOrderFormatInfo().getCsvFileEncode())) {
                info.getOrderFormatInfo().setCsvFileEncode(ProductionNaviPropertyConstantsFuji.INIT_PROD_ORDER_CSV_ENCODE);
            }

            // CSV: 読み込み開始行
            if (Objects.isNull(info.getOrderFormatInfo().getCsvStartRow())) {
                info.getOrderFormatInfo().setCsvStartRow(ProductionNaviPropertyConstantsFuji.INIT_PROD_ORDER_CSV_LINE);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * BOM情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillBomFormatInfoValue(ImportFormatInfo info) {
        try {
            // BOM情報
            if (Objects.isNull(info.getBomFormatInfo())) {
                info.setBomFormatInfo(new BomFormatInfo());
            }

            // CSV: ファイル名
            if (Objects.isNull(info.getBomFormatInfo().getCsvFileName())) {
                info.getBomFormatInfo().setCsvFileName(ProductionNaviPropertyConstantsFuji.INIT_PROD_BOM_CSV_FILE);
            }
            // CSV: エンコード
            if (Objects.isNull(info.getBomFormatInfo().getCsvFileEncode())) {
                info.getBomFormatInfo().setCsvFileEncode(ProductionNaviPropertyConstantsFuji.INIT_PROD_BOM_CSV_ENCODE);
            }

            // CSV: 読み込み開始行
            if (Objects.isNull(info.getBomFormatInfo().getCsvStartRow())) {
                info.getBomFormatInfo().setCsvStartRow(ProductionNaviPropertyConstantsFuji.INIT_PROD_BOM_CSV_LINE);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 文字列を日時に変換する。
     *
     * @param value 文字列
     * @return 日時
     */
    public static Date stringToDateTime(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
            return sdf.parse(value);
        } catch (ParseException e) {
            return stringToDateTime2(value);
        }
    }

    /**
     * 文字列(yyyy/MM/dd HH:mm:ss)を日時に変換する。
     *
     * @param value 文字列(yyyy/MM/dd HH:mm:ss)
     * @return 日時
     */
    private static Date stringToDateTime2(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_2);
            return sdf.parse(value);
        } catch (ParseException e) {
            return stringToDateTime3(value);
        }
    }

    /**
     * 文字列(yyyy/M/d H:m:s)を日時に変換する。
     *
     * @param value 文字列(yyyy/M/d H:m:s)
     * @return 日時
     */
    private static Date stringToDateTime3(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_3);
            return sdf.parse(value);
        } catch (ParseException e) {
            return stringToDateTime4(value);
        }
    }

    /**
     * 文字列(yyyy/M/d H:m)を日時に変換する。
     *
     * @param value 文字列(yyyy/M/d H:m)
     * @return 日時
     */
    private static Date stringToDateTime4(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_4);
            return sdf.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 文字列を日付に変換する。
     *
     * @param value 文字列
     * @return 日付
     */
    public static Date stringToDate(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            return sdf.parse(value);
        } catch (ParseException e) {
            return stringToDate2(value);
        }
    }

    /**
     * 文字列(yyyy/MM/dd)を日時に変換する。
     *
     * @param value 文字列(yyyy/MM/dd)
     * @return 日付
     */
    private static Date stringToDate2(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_2);
            return sdf.parse(value);
        } catch (ParseException e) {
            return stringToDate3(value);
        }
    }

    /**
     * 文字列(yyyy/M/d)を日時に変換する。
     *
     * @param value 文字列(yyyy/M/d)
     * @return 日付
     */
    private static Date stringToDate3(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_3);
            return sdf.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }
}
