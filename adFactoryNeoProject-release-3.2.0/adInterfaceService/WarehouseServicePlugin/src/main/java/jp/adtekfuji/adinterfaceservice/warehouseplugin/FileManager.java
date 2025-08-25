/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.warehouseplugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 共通インポート ファイル操作
 *
 * @author 14-0282
 */
public class FileManager {

    private final String importDirPath;
    private final String workDirPath;
    private String orgFileName;
    private String fileName;

    private final String PROC_EXTENTION = ".proc";
    private final String DONE_EXTENTION = ".done";
    private final String ERR_EXTENTION = ".error";
    private static final Logger logger = LogManager.getLogger();

    public FileManager(String importDirPath, String workDirPath, String importFileName) {
        this.importDirPath = importDirPath;
        this.workDirPath = workDirPath;

        File dir = new File(workDirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        this.orgFileName = importFileName;

        int point = orgFileName.lastIndexOf(".");
        this.fileName = orgFileName.substring(0, point);
    }

    public FileManager(String importDirPath, String workDirPath) {
        this.importDirPath = importDirPath;
        this.workDirPath = workDirPath;
    }

    /**
     * インポートファイルを作業フォルダにコピー
     *
     * @return true: 成功、false: 失敗
     */
    public boolean copyToWorkingDir() {
        String srcPath = this.importDirPath + this.orgFileName;
        String destPath = this.workDirPath + this.orgFileName;

        File file = new File(srcPath);
        if (!file.exists()) {
            // ファイルが存在しない
            logger.info("File does not exist: {}", this.importDirPath + this.orgFileName);
            return false;
        }
        
        logger.info("Copy file: (} -> {}", srcPath, destPath);
        
        this.deleteFile(destPath);
            
        try (FileInputStream fis = new FileInputStream(srcPath);
                FileOutputStream fos = new FileOutputStream(destPath)) {

            // 入力ファイルをそのまま出力ファイルに書き出す
            byte buf[] = new byte[1024];
            int len;
            while ((len = fis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }

            // ファイルに書き出す
            fos.flush();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
            return false;
        }
        return true;
    }

    /**
     * インポートファイルを検索して、作業フォルダにコピーする。
     * 
     * @return 見つかったインポートファイルのパス
     */
    public String findCopy() {
        File file = new File(this.importDirPath + this.orgFileName);

        if (!file.exists()) {

            File importDir = new File(this.importDirPath);
            File[] fileList = importDir.listFiles();
            if (Objects.isNull(fileList)) {
                return null;
            }

            boolean exist = false;
            
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].getName().startsWith(fileName)) {
                    if (fileList[i].getName().endsWith(PROC_EXTENTION)
                        || fileList[i].getName().endsWith(DONE_EXTENTION)
                        || fileList[i].getName().endsWith(ERR_EXTENTION)) {
                        continue;
                    }
                    
                    this.orgFileName = fileList[i].getName();
                    int point = this.orgFileName.lastIndexOf(".");
                    this.fileName = orgFileName.substring(0, point);
                    exist = true;
                    break;
                }
            }
            
            if (!exist) {
                // ファイルが存在しない
                logger.info("File does not exist: {}", this.importDirPath + this.orgFileName);
                return null;
            }
        }
      
        String srcPath = this.importDirPath + this.orgFileName;
        String destPath = this.workDirPath + this.orgFileName;
        logger.info("Copy file: (} -> {}", srcPath, destPath);
        
        this.deleteFile(destPath);
        
        try (FileInputStream fis = new FileInputStream(srcPath);
                FileOutputStream fos = new FileOutputStream(destPath)) {

            // 入力ファイルをそのまま出力ファイルに書き出す
            byte buf[] = new byte[1024];
            int len;
            while ((len = fis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }

            // ファイルに書き出す
            fos.flush();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
            return null;
        }
        return destPath;
    }

    /**
     * 不要なファイルを削除する。
     */
    public void cleanup() {
        File importDir = new File(this.importDirPath);
        File[] fileList = importDir.listFiles();
        if (Objects.isNull(fileList)) {
            return;
        }

        List<String> deleteFiles = new ArrayList<>();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].getName().startsWith(fileName) 
                    && (fileList[i].getName().endsWith(PROC_EXTENTION)
                    || fileList[i].getName().endsWith(DONE_EXTENTION)
                    || fileList[i].getName().endsWith(ERR_EXTENTION))) {

                deleteFiles.add(fileList[i].getPath());
            }
        }
    
        for (String filePath : deleteFiles) {
            this.deleteFile(filePath);
        }
    }


    /**
     * .procファイル削除
     *
     */
    public void deleteProcFile() {

        String deleteFileName = fileName + PROC_EXTENTION;
        String deleteFilePath = importDirPath + deleteFileName;
        deleteFile(deleteFilePath);
    }

    /**
     * .doneファイル削除
     *
     */
    public void deleteDoneFile() {
        String deleteFileName = fileName + ERR_EXTENTION;
        String deleteFilePath = importDirPath + deleteFileName;
        deleteFile(deleteFilePath);
    }

    /**
     * .errorファイル削除
     *
     */
    public void deleteErrFile() {

        String deleteFileName = fileName + DONE_EXTENTION;
        String deleteFilePath = importDirPath + deleteFileName;
        deleteFile(deleteFilePath);
    }

    /**
     * 一時ファイルを削除
     */
    public void deleteWorkFile() {
        String deleteFilePath = workDirPath + orgFileName;
        deleteFile(deleteFilePath);
    }

    /**
     * Jsonファイルを削除
     *
     * @param jsonFilePath
     */
    public void deleteJsonFile(String jsonFilePath) {
        deleteFile(jsonFilePath);
    }

    /**
     * 指定ファイルを削除
     *
     * @param deleteFilePath
     */
    public void deleteFile(String deleteFilePath) {
        File file = new File(deleteFilePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * オリジナルファイルを.procへリネーム
     */
    public void renameOrgToProc() {

        File fOld = new File(this.importDirPath + this.orgFileName);
        File fNew = new File(this.importDirPath + this.fileName + PROC_EXTENTION);

        if (fOld.exists()) {
            fOld.renameTo(fNew);
        }
    }

    /**
     * .procファイルを.errorファイルにリネーム
     */
    public void renameProcToError() {

        File fOld = new File(this.importDirPath + this.fileName + PROC_EXTENTION);
        File fNew = new File(this.importDirPath + this.fileName + ERR_EXTENTION);

        if (fOld.exists()) {
            fOld.renameTo(fNew);
        }
    }

    /**
     * .procファイルを.doneファイルにリネーム
     */
    public void renameProcToDone() {

        File fOld = new File(this.importDirPath + this.fileName + PROC_EXTENTION);
        File fNew = new File(this.importDirPath + this.fileName + DONE_EXTENTION);

        if (fOld.exists()) {
            fOld.renameTo(fNew);
        }
    }

    /**
     * Jsonファイルを出力
     *
     * @param jsonString　json文字列
     * @param jsonFileName　出力ファイル名
     * @return
     */
    public Boolean outputJsonFileToWorkingDir(String jsonString, String jsonFileName) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.workDirPath + jsonFileName), "UTF8"))) {
            writer.write(jsonString);

        } catch (IOException ex) {
            logger.fatal(ex, ex);
            return true;
        }
        return false;
    }

    /**
     * オリジナルファイル名 設定
     *
     * @param orgFileName
     */
    public void setOrgFileName(String orgFileName) {
        this.orgFileName = orgFileName;

        int point = orgFileName.lastIndexOf(".");
        this.fileName = orgFileName.substring(0, point);
    }

    /**
     * オリジナルファイル名 取得
     *
     * @return
     */
    public String getOrgFileName() {
        return orgFileName;
    }
}
