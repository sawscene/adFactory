/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.importformat.ImportFormatInfo;
import static jp.adtekfuji.adinterfaceservicecommon.plugin.common.ProductionNaviPropertyConstants.PREFIX_TMP;
import static jp.adtekfuji.adinterfaceservicecommon.plugin.common.ProductionNaviPropertyConstants.REGEX_PREFIX_TMP;
import static jp.adtekfuji.adinterfaceservicecommon.plugin.common.ProductionNaviPropertyConstants.SUFFIX_COMPLETED;
import static jp.adtekfuji.adinterfaceservicecommon.plugin.common.ProductionNaviPropertyConstants.SUFFIX_ERROR;
import static jp.adtekfuji.adinterfaceservicecommon.plugin.common.ProductionNaviPropertyConstants.SUFFIX_NONE;

/**
 * CSVファイルをを読み込む前にリネーム・古い削除の削除などを行う
 *
 * @author fu-kato
 */
public class ImportFileFormatInfo {

    private final ImportFormatInfo importFormatInfo;
    private final String folder;
    private final List<String> fileNameAll = new ArrayList<>();

    /**
     *
     * @param importFormatInfo
     * @param folder
     */
    public ImportFileFormatInfo(ImportFormatInfo importFormatInfo, String folder) {
        this.importFormatInfo = importFormatInfo;
        this.folder = folder;
        this.fileNameAll.addAll(new LinkedHashSet<>(Arrays.asList(
                importFormatInfo.getKanbanFormatInfo().getCsvFileName(),
                importFormatInfo.getKanbanPropFormatInfo().getCsvFileName(),
                importFormatInfo.getWorkKanbanFormatInfo().getCsvFileName(),
                importFormatInfo.getWorkKanbanPropFormatInfo().getCsvFileName(),
                importFormatInfo.getKanbanStatusFormatInfo().getCsvFileName(),
                importFormatInfo.getProductFormatInfo().getCsvFileName(),
                importFormatInfo.getUpdateWorkKanbanPropFormatInfo().getCsvFileName()
        )));
    }

    /**
     *
     * @return
     */
    public ImportFormatInfo getImportFormatInfo() {
        return this.importFormatInfo;
    }

    /**
     * 以前読み込んだファイル(*.completed または*.error)を削除する
     *
     */
    public void deleteOld() {

        String csvFolder = this.folder;
        this.fileNameAll.stream().map(
                csvFilename -> new File(csvFolder + File.separator + csvFilename + SUFFIX_COMPLETED))
                .filter((csvCompFile) -> csvCompFile.exists())
                .forEach((csvCompFile) -> csvCompFile.delete());

        this.fileNameAll.stream().map(
                csvFilename -> new File(csvFolder + File.separator + csvFilename + SUFFIX_ERROR))
                .filter((csvErrFile) -> csvErrFile.exists())
                .forEach((csvErrFile) -> csvErrFile.delete());

        this.fileNameAll.stream().map(
                csvFilename -> new File(csvFolder + File.separator + csvFilename + SUFFIX_NONE))
                .filter((csvErrFile) -> csvErrFile.exists())
                .forEach((csvErrFile) -> csvErrFile.delete());
    }

    /**
     * 登録の際の一時ファイル(これを読み込む)を作成する
     *
     */
    public void createTemp() {
        this.fileNameAll.stream().map(
                (csvFileName) -> new File(this.folder + File.separator + csvFileName))
                .filter((csvFile) -> (csvFile.exists() && csvFile.isFile()))
                .forEach((csvFile) -> {
                    File toFile = new File(
                            csvFile.getParent() + File.separator + PREFIX_TMP + csvFile.getName());
                    if (toFile.exists()) {
                        toFile.delete();
                    }
                    csvFile.renameTo(toFile);
                });
    }

    /**
     * 処理結果に応じてリネームする。
     *
     * @param result
     * @param fileNames
     */
    public void renameCompleted(Map<String, Integer> result, List<String> fileNames) {
        String suffix;
        if (Objects.nonNull(result)
                && (result.containsKey("procNum") && result.get("procNum") > 0)
                && (result.containsKey("successNum") && result.get("successNum") > 0)) {
            suffix = SUFFIX_COMPLETED;
        } else {
            suffix = SUFFIX_NONE;
        }

        fileNames.stream().map(
                (csvFileName) -> new File(this.folder + File.separator + PREFIX_TMP + csvFileName))
                .filter((csvFile) -> (csvFile.exists() && csvFile.isFile()))
                .forEach((csvFile) -> {
                    File toFile = new File(csvFile.getParent() + File.separator
                            + csvFile.getName().replaceFirst(REGEX_PREFIX_TMP, "") + suffix);
                    if (toFile.exists()) {
                        toFile.delete();
                    }
                    csvFile.renameTo(toFile);
                });
    }

    /**
     *
     */
    public void renameFailed() {
        this.fileNameAll.stream().map(
                (csvFileName) -> new File(this.folder + File.separator + PREFIX_TMP + csvFileName))
                .filter((csvFile) -> (csvFile.exists() && csvFile.isFile()))
                .forEach((csvFile) -> {
                    File toFile = new File(csvFile.getParent() + File.separator
                            + csvFile.getName().replaceFirst(REGEX_PREFIX_TMP, "") + SUFFIX_ERROR);
                    if (toFile.exists()) {
                        toFile.delete();
                    }
                    csvFile.renameTo(toFile);
                });
    }

    /**
     * 必須ファイル名の取得
     *
     * @return
     */
    public String getNecessityFilename() {
        return this.importFormatInfo.getKanbanFormatInfo().getCsvFileName();
    }

    /**
     * 更新ファイル名の取得
     *
     * @return
     */
    public String getUpdateFilename() {
        return this.importFormatInfo.getUpdateWorkKanbanPropFormatInfo().getCsvFileName();
    }
}
