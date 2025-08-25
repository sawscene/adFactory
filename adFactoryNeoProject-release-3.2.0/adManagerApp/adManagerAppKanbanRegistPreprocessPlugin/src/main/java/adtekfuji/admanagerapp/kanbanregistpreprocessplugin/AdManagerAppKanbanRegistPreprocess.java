/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbanregistpreprocessplugin;

import adtekfuji.admanagerapp.kanbanregistpreprocessplugin.common.CollateKanbanPro;
import adtekfuji.admanagerapp.kanbanregistpreprocessplugin.common.CsvTypeEnum;
import adtekfuji.property.AdProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.plugin.AdManagerAppKanbankanbanRegistPreprocessInterface;
import jp.adtekfuji.adFactory.utility.KanbanRegistPreprocessResultEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;

/**
 *
 * @author e-mori
 */
public class AdManagerAppKanbanRegistPreprocess implements AdManagerAppKanbankanbanRegistPreprocessInterface {

    private static final Logger logger = LogManager.getLogger();
    private static final Properties properties = AdProperty.getProperties();
    private static final List<List<String[]>> mastarDataList = new ArrayList<>();

    private final static String COMMA_SPLIT = ",";
    private static final String KANBAN_REGIST_PRE_MASTAR_FILE_NAMES = "kanban_regist_pre_mastarfile_names";
    private static final String KANBAN_REGIST_PRE_MASTAR_FILE_TYPE = "kanban_regist_pre_mastarfile_file_type";
    private static final String KANBAN_REGIST_PRE_MASTAR_DOWNLOAD_TOOL = "kanban_regist_pre_mastarfile_download_tool";
    private static final String KANBAN_REGIST_PRE_MASTAR_DOWNLOAD_COMMAND = "kanban_regist_pre_mastarfile_download_command";
    private static final String DOWONLOAD = "downloads";
    private static final String BINARY = "bin";
    private static final String CHARSET = "UTF-8";

    /**
     * マスターデータをFTPサーバからダウンロードする
     *
     */
    private Boolean downloadMastarData() {
        try {
            String execPass = System.getenv("ADFACTORY_HOME") + File.separator + BINARY + File.separator + properties.getProperty(KANBAN_REGIST_PRE_MASTAR_DOWNLOAD_TOOL);
            if (!new File(execPass).exists()) {
                logger.warn("nothing exe");
                return false;
            }
            List<String> list = new ArrayList<>();
            list.add(execPass);
            String[] argument = properties.getProperty(KANBAN_REGIST_PRE_MASTAR_DOWNLOAD_COMMAND).split(",");
            list.addAll(Arrays.asList(argument));
//            ProcessBuilder pb = new ProcessBuilder(execPass, "-projectno=C:/adFactory/downloads/projectno.tsv","-order=C:/adFactory/downloads/order.tsv");
            ProcessBuilder pb = new ProcessBuilder(list);
            Process process = pb.start();
            int ret = process.waitFor();
            logger.info("戻り値:{}", ret);

            return true;
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex);
            return false;
        }
    }

    /**
     * ローカル環境にあるMastarファイルを取り込む
     *
     */
    private void getMastarData() {
        try {
            String[] fileNames = properties.getProperty(KANBAN_REGIST_PRE_MASTAR_FILE_NAMES).split(COMMA_SPLIT);
            final String TRAGET_MASTAR_CSV_TYPE = properties.getProperty(KANBAN_REGIST_PRE_MASTAR_FILE_TYPE);
            CsvTypeEnum csvTypeEnum = CsvTypeEnum.getEnum(TRAGET_MASTAR_CSV_TYPE);
            if (Objects.isNull(csvTypeEnum.getSeparateKey())) {
                logger.info("nothing separate data");
                return;
            }

            for (String fileName : fileNames) {
                File file = new File(System.getenv("ADFACTORY_HOME") + File.separator + DOWONLOAD + File.separator + fileName + "." + TRAGET_MASTAR_CSV_TYPE);
                if (!file.exists()) {
                    logger.info("nothing file={}", file);
                    continue;
                }

                logger.info("read start:file={}", file);
                InputStream input = new FileInputStream(file);
                InputStreamReader ireader = new InputStreamReader(input, CHARSET);
                CSVReader reader = new CSVReader(ireader, csvTypeEnum.getSeparateKey());
                mastarDataList.add(reader.readAll());
                logger.info("read end");
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバン登録前処理
     *
     * @param registData 登録するカンバンの情報
     * @return 登録前処理の実行結果
     */
    @Override
    public KanbanRegistPreprocessResultEntity kanbanRegistPreprocess(KanbanInfoEntity registData) {
        return CollateKanbanPro.collateKanbanProData(mastarDataList, registData);
    }

    @Override
    public void pluginInitialize() {
        if (downloadMastarData()) {
            getMastarData();
        }
    }
}
