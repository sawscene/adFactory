/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.summaryreportplugin.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adFactory.entity.model.SummaryReportSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * サマリーレポート設定ファイル処理 (設定をjson形式で保存する)
 *
 * @author kentarou.suzuki
 */
public class SummaryReportSettingFileAccessor {

    /**
     * ログ出力クラス
     */
    private static final Logger logger = LogManager.getLogger();
    
    /**
     * サマリーレポート設定ファイル名 (デフォルト)
     */
    private final static String SETTING_DEFAULT_NAME = "SummaryReportSetting.json";

    /**
     * コンストラクタ
     */
    public SummaryReportSettingFileAccessor() {
    }

    /**
     * デフォルトのサマリーレポート設定ファイルのパスを取得する。
     *
     * @return 設定ファイルパス
     */
    public String getFilePath() {
        return System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + String.format(SETTING_DEFAULT_NAME);
    }

    /**
     * デフォルトのサマリーレポート設定ファイルを読み込む。
     *
     * @return サマリーレポート設定
     */
    public List<SummaryReportSetting> load() {
        List<SummaryReportSetting> properties = new ArrayList<>();
        try {
            Path path = Paths.get(this.getFilePath());
            if (Files.exists(path)) {
                String jsonString = Files.lines(path, StandardCharsets.UTF_8)
                        .collect(Collectors.joining(System.getProperty("line.separator")));
                properties = JsonUtils.jsonToObjects(jsonString, SummaryReportSetting[].class);
            } else {
                properties = Arrays.asList(SummaryReportSetting.create());
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return properties;
    }

    /**
     * デフォルトのサマリーレポート設定を保存する。
     *
     * @param properties サマリーレポート設定
     */
    public void save(List<SummaryReportSetting> properties) {
        try {
            String jsonString = JsonUtils.objectToJson(properties);
            Path path = Paths.get(this.getFilePath());
            Files.write(path, jsonString.getBytes());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
