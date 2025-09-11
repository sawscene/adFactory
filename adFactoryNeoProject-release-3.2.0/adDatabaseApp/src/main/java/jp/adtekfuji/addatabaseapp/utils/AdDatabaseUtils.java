/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jp.adtekfuji.addatabase.common.AdDatabaseConfig;
import jp.adtekfuji.addatabase.common.FileNameComparator;
import jp.adtekfuji.addatabaseapp.postgres.PGContents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
public class AdDatabaseUtils {

    private static final Logger logger = LogManager.getLogger();
     private static final String LOCAL_BACKUP_DIR = System.getenv("ADFACTORY_HOME") + File.separator + "backup" + File.separator + "local";

    /**
     * バックアップファイル数が上限を超えた場合、古いバックアップファイルを削除する。
     *
     * @param backupDir バックアップフォルダパス
     */
    public static void deleteOldBackupFiles(String backupDir) {
        try {
            int backupMax = AdDatabaseConfig.getDatabaseBackupMax();// バックアップファイル数の上限
            if (backupMax < 1) {
                return;
            }

            File fol = new File(backupDir);
            if (Objects.isNull(fol.listFiles())) {
                return;
            }

            // ファイル名で対象を絞り込む。
            List<File> files = Arrays.asList(fol.listFiles()).stream().filter(p -> p.isFile()
                    && p.length() > 0
                    && p.getName().matches(PGContents.DB_BACKUP_FILE_FORMAT)
            ).collect(Collectors.toList());
            if (files.size() <= backupMax) {
                // バックアッファイル数が上限値以下の場合は何もしない。
                return;
            }

            // ファイル名の逆順にソートする。
            //      ※「<データベース名>_yyyyMMdd_HHmmss.backup」なので、逆順にソートすればバックアップ日時が新しい順になる。
            Collections.sort(files, new FileNameComparator().reversed());

            // 古いバックアップファイルを削除する。
            for (int i = files.size() - 1; i >= backupMax; i--) {
                FileUtils.delete(files.get(i));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * ローカルバックアップフォルダを取得する。
     *
     * @return ローカルバックアップフォルダ
     */
    public static File getLocalBackupDir() {
        File dir = new File(LOCAL_BACKUP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * ローカルフォルダにあるバックアップファイルを指定されたフォルダに転送する。
     *
     * @param destDir 転送先フォルダ
     */
    public static void transferLocalBackupFiles(File destDir) {
        try {
            File localDir = getLocalBackupDir();
            if (!localDir.exists() || Objects.isNull(localDir.listFiles()) || Objects.isNull(destDir)) {
                return;
            }
            for (File file : localDir.listFiles()) {
                if (!file.isFile()) {
                    continue;
                }
                File dest = new File(destDir, file.getName());
                try {
                    Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    FileUtils.delete(file);
                } catch (Exception ex) {
                    logger.warn("Failed to transfer staged backup file: {}", file, ex);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
