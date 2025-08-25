/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimerTask;
import jp.adtekfuji.adFactory.entity.csv.ImportWorkKanbanPropertyCsv;
import jp.adtekfuji.adFactory.entity.importformat.ImportFormatInfo;
import jp.adtekfuji.adinterfaceservicecommon.plugin.ImportFileFormatInfo;
import jp.adtekfuji.adinterfaceservicecommon.plugin.WorkPlanCSVLoader;
import jp.adtekfuji.adinterfaceservicecommon.plugin.WorkPlanRegister;
import jp.adtekfuji.adinterfaceservicecommon.plugin.common.ProductionNaviPropertyConstants;
import jp.adtekfuji.adinterfaceservicecommon.plugin.common.UpdateWorkKanbanPropertyLoader;
import jp.adtekfuji.adinterfaceservicecommon.plugin.common.WorkPlanLoader;
import jp.adtekfuji.adinterfaceservicecommon.plugin.common.WorkPlanResultLogger;
import jp.adtekfuji.adinterfaceservicecommon.plugin.entity.WorkPlanInfo;
import jp.adtekfuji.adinterfaceservicecommon.plugin.util.ImportFormatFileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 読み込みと登録を行うタスク
 *
 * @author fu-kato
 */
public class ImportTask extends TimerTask {

    private static final Logger logger = LogManager.getLogger();
    private final ResourceBundle rb = ResourceBundle.getBundle("locale.locale");

    private final boolean ignoreSameKanban;
    private final String defaultPath;
    private final ImportFileFormatInfo importFileFormat;
    private final WorkPlanLoader loader;
    private final WorkPlanRegister register;

    /**
     * コンストラクタ
     *
     * @param defaultPath
     * @param importFormatInfo
     */
    ImportTask(String defaultPath, ImportFormatInfo importFormatInfo) {
        this(defaultPath, importFormatInfo, false);
    }

    /**
     * コンストラクタ
     *
     * @param defaultPath
     * @param importFormatInfo
     * @param ignoreSameKanban
     */
    ImportTask(String defaultPath, ImportFormatInfo importFormatInfo, boolean ignoreSameKanban) {
        this.ignoreSameKanban = ignoreSameKanban;
        this.defaultPath = defaultPath;

        this.importFileFormat = new ImportFileFormatInfo(importFormatInfo, defaultPath);

        final WorkPlanResultLogger resultLogger = (String message) -> {
            logger.info(message);
        };

        this.loader = new WorkPlanCSVLoader(resultLogger);
        this.register = new WorkPlanRegister(resultLogger);
    }

    /**
     * タイマータスクにより生産計画の読み込みを実行する。
     */
    @Override
    public void run() {

        try {
            // 読み込みフォルダーのパス
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            final Properties properties = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            String path = properties.getProperty(ProductionNaviPropertyConstants.SELECT_PROD_CSV_PATH, defaultPath);

            if (!this.existsNecessities(path, false) && !existsUpdateFile(path)) {
                return;
            }

            importFileFormat.deleteOld();
            importFileFormat.createTemp();

            // 生産計画の読み込み画面で設定されたファイルフォーマットを読み込む
            ImportFormatInfo importFormat = ImportFormatFileUtil.load();

            if (this.existsNecessities(path, true)) {
                // 生産計画ファイルを読み込む
                WorkPlanInfo workPlan = loader.importKanban(path, 0, importFormat, ignoreSameKanban);

                // カンバンをサーバーに登録する
                Map<String, Integer> result = register.update(ignoreSameKanban, workPlan);

                List<String> fileNames = Arrays.asList(
                        importFormat.getKanbanFormatInfo().getCsvFileName(),
                        importFormat.getKanbanPropFormatInfo().getCsvFileName(),
                        importFormat.getWorkKanbanFormatInfo().getCsvFileName(),
                        importFormat.getWorkKanbanPropFormatInfo().getCsvFileName(),
                        importFormat.getKanbanStatusFormatInfo().getCsvFileName(),
                        importFormat.getProductFormatInfo().getCsvFileName());

                importFileFormat.renameCompleted(result, fileNames);
            }

            if (loader instanceof UpdateWorkKanbanPropertyLoader) {
                // update_work_kanban_property.csvを読み込む
                List<ImportWorkKanbanPropertyCsv> workProps = ((UpdateWorkKanbanPropertyLoader) loader).importFile(defaultPath, 0, importFileFormat.getImportFormatInfo(), true);
                Map<String, Integer> result = register.updateWorkKanbanProperty(ignoreSameKanban, workProps);

                importFileFormat.renameCompleted(result, Arrays.asList(importFormat.getUpdateWorkKanbanPropFormatInfo().getCsvFileName()));
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            importFileFormat.renameFailed();

        } finally {
            logger.info(LocaleUtils.getString("key.ImportKanbanEnd"));
        }
    }

    /**
     * 必須ファイル(デフォルト：kanban.csv)が存在するかどうか
     *
     * @param renamed trueの場合 tmp_のついたファイルが存在するか調べる
     * @return
     */
    private boolean existsNecessities(String path, boolean renamed) {
        // 必須ファイルの確認
        String filename = importFileFormat.getNecessityFilename();
        String kanbanPath = path + File.separator + (renamed ? ProductionNaviPropertyConstants.PREFIX_TMP : "") + filename;
        File file = new File(kanbanPath);
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        return true;
    }

    /**
     * update_work_kanban_property.csv が存在するか調べる
     *
     * @param path
     * @return
     */
    private boolean existsUpdateFile(String path) {
        String updatefilename = path + File.separator + importFileFormat.getUpdateFilename();
        File updateFile = new File(updatefilename);
        return updateFile.exists() && updateFile.isFile();
    }

    /**
     * 結果を結合する。同じ文字列をキーとしている場合、値を加算する。値が異なる場合、新しく追加する。
     *
     * @param v1
     * @param v2
     * @return
     */
    static public Map<String, Integer> concatResult(Map<String, Integer> v1, Map<String, Integer> v2) {

        if (Objects.isNull(v1) && Objects.isNull(v2)) {
            return null;
        } else if (Objects.isNull(v1)) {
            return v2;
        } else if (Objects.isNull(v2)) {
            return v1;
        }

        Map<String, Integer> ret = new HashMap(v1);

        for (Map.Entry<String, Integer> v : v2.entrySet()) {
            if (ret.containsKey(v.getKey())) {
                ret.put(v.getKey(), ret.get(v.getKey()) + v.getValue());
            } else {
                ret.put(v.getKey(), v.getValue());
            }
        }

        return ret;
    }
}
