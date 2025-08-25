/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import jp.adtekfuji.adFactory.entity.actual.DefectReasonEntity;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.utility.CsvFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 不良内容リスト管理クラス
 *
 * @author nar-nakamura
 */
public class DefectReasonManager {

    private final Logger logger = LogManager.getLogger();

    private static final String DATA_FILE_PATH = new StringBuilder(System.getenv("ADFACTORY_HOME")).append(File.separator).append("ext").append(File.separator).append("defect.csv").toString();

    private static final String PROP_FILE_PATH = new StringBuilder(System.getenv("ADFACTORY_HOME")).append(File.separator).append("conf").append(File.separator).append("adFactoryService.properties").toString();
    private static final String PROP_FILE_CHAR = "MS932";

    private static DefectReasonManager instance;

    private final List<DefectReasonEntity> defectReasons = new ArrayList();

    /**
     * コンストラクタ
     */
    public DefectReasonManager() {
    }

    /**
     * インスタンスを取得する。
     *
     * @return 
     */
    public static DefectReasonManager getInstance() {
        if (Objects.isNull(instance)) {
            instance = new DefectReasonManager();
        }
        return instance;
    }

    /**
     * 不良内容リストを取得する。
     *
     * @return 不良内容リスト
     */
    public List<DefectReasonEntity> getDefectReasons() {
        return this.defectReasons;
    }

    /**
     * 不良内容リストを読み込む。
     */
    public void load() {
        try {
            this.defectReasons.clear();

            // システム設定
            Properties serviceProps = this.loadServiceProperties();

            // CSVファイルのエンコード
            String csvEncode = Constants.CSV_ENCODE_DEFAULT.toUpperCase();
            if (Objects.nonNull(serviceProps)) {
                csvEncode = serviceProps.getProperty(Constants.CSV_ENCODE_KEY, Constants.CSV_ENCODE_DEFAULT).toUpperCase();
            }

            // シフトJISの場合はMS932を指定する。
            if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS").contains(csvEncode)) {
                csvEncode = "MS932";
            }

            List<List<String>> rows = CsvFileUtils.readCsv(DATA_FILE_PATH, 2, csvEncode);// 2行目から読み込み

            long index = 0;
            for (List<String> row : rows) {
                DefectReasonEntity defect = new DefectReasonEntity();
                defect.setDefectId(index++);
                defect.setDefectOrder(defect.getDefectId());
                defect.setDefectType(row.get(0));
                defect.setDefectClass(row.get(1));
                defect.setDefectValue(row.get(2));

                defectReasons.add(defect);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * サーバー設定を取得する。
     *
     * @return サーバー設定
     */
    private Properties loadServiceProperties() {
        Properties properties = new Properties();
        try {
            File file = new File(PROP_FILE_PATH);
            if (file.exists()) {
                properties.load(new InputStreamReader(new FileInputStream(PROP_FILE_PATH), PROP_FILE_CHAR));
            } else {
                logger.info("File does not exist: {}", properties);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return properties;
    }
}
