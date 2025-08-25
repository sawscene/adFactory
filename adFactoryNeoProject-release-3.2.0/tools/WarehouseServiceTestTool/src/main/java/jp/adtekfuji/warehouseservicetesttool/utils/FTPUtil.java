/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.warehouseservicetesttool.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FTP
 *
 * @author nar-nakamura
 */
public class FTPUtil {

    private final Logger logger = LogManager.getLogger();
    private final DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");
    private final FTPClient ftpClient = new FTPClient();

    /**
     * FTPサーバーに接続する。
     * 
     * @param hostname ホストネーム
     * @param user ユーザー
     * @param pass パスワード
     * @param enc  エンコード用文字コード
     * @return 結果 (true:成功, false:失敗)
     */
    public boolean connect(String hostname, String user, String pass, String enc) {
        boolean result = false;
        try {
            ftpClient.setControlEncoding(enc);
            ftpClient.connect(hostname);
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                throw new IOException("Ftp not Connected ReplyCode=" + ftpClient.getReplyCode());
            }

            // ログイン
            if (!ftpClient.login(user, pass)) {
                throw new IOException("login failed");
            }

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            result = true;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * FTPサーバーからログアウトする。
     *
     * @return 結果 (true:成功, false:失敗)
     */
    public boolean close() {
        boolean result = false;
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
            result = true;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * FTPサーバーからファイルを取得する。
     *
     * @param srcDirName FTPのディレクトリ名
     * @param srcFileName ファイル名
     * @param destDirName 保存先ディレクトリ名
     * @param destFileName 保存先ファイル名
     * @return 成否
     */
    public boolean get(String srcDirName, String srcFileName, String destDirName, String destFileName) {
        boolean result = false;

        File dest = new File(destDirName + "/" + destFileName);

        try (FileOutputStream out = new FileOutputStream(dest)) {
            String srcFullPath = srcDirName + "/" + srcFileName;
            ftpClient.setBufferSize(1024 * 1024);
            result = ftpClient.retrieveFile(srcFullPath, out);
            if (result) {
                // タイムスタンプを維持
                String timestamp = ftpClient.getModificationTime(srcFullPath);
                try {
                    Date date = localDateToDate(LocalDateTime.parse(timestamp, sdf));
                    dest.setLastModified(date.getTime());
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * FTPサーバにファイルを保存する
     * @param srcDirName ディレクトリ名
     * @param srcFileName ファイル名
     * @param destDirName FTPの保存先ディレクトリ名
     * @param destFileName FTPの保存先ファイル名
     * @return 成否
     */
    public boolean put(String srcDirName, String srcFileName, String destDirName, String destFileName) {
        boolean result = false;

        File source = new File(srcDirName + "/" + srcFileName);

        try (FileInputStream in = new FileInputStream(source)) {
            result = ftpClient.storeFile(destDirName + "/" + destFileName, in);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * ファイルの存在確認
     * @param srcDirName ディレクトリ名
     * @param srcFileName ファイル名
     * @return true: 存在する
     */
    public boolean exists(String srcDirName, String srcFileName) {
        boolean result = false;
        try {
            FTPFile[] files = ftpClient.listFiles(srcDirName + "/" + srcFileName);
            result = files.length != 0;
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * FTPサーバのファイルサイズを取得する
     * 返り値は「byte」単位
     * @param srcDirName ディレクトリ名
     * @param srcFileName ファイル名
     * @return fileSize ファイルサイズ
     */
    public long getFileSize(String srcDirName, String srcFileName) {
        try {
            String fullPath = srcDirName + "/" + srcFileName;
            FTPFile[] files = ftpClient.listFiles(fullPath);
            if (files.length == 0) {
                throw new IOException("no such file : " + fullPath);
            }
            return files[0].getSize();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * ローカル日時を日時に変換する。
     *
     * @param dateTime ローカル日時
     * @return 日時
     */
    private Date localDateToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
