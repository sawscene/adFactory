/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.warehouseservicetesttool.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
public class FileUtility {

    private static final Logger logger = LogManager.getLogger();

    /**
     * テキストファイルを読み込む。(Shift-JIS)
     *
     * @param file
     * @return
     */
    public static final List<String> readTextFile(String file) {
        return readTextFile(file, Charset.forName("MS932"));
    }

    /**
     * テキストファイルを読み込む。
     *
     * @param file
     * @param cs
     * @return
     */
    public static List<String> readTextFile(String file, Charset cs) {
        List<String> list = null;
        try {
            Path path = FileSystems.getDefault().getPath(file);
            if (Files.exists(path)) {
                list = Files.readAllLines(path, cs);
            }
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return list;
    }

    /**
     * テキストファイルに書き込む。(Shift-JIS)
     *
     * @param file
     * @param list
     * @return
     */
    public static final Boolean writeTextFile(String file, List<String> list) {
        return writeTextFile(file, list, Charset.forName("MS932"));
    }

    /**
     * テキストファイルに書き込む。
     *
     * @param file
     * @param list
     * @param cs
     * @return
     */
    public static final Boolean writeTextFile(String file, List<String> list, Charset cs) {
        Boolean ret = false;
        try {
            Path path = FileSystems.getDefault().getPath(file);
            Files.write(path, list, cs,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            ret = true;
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * フォルダを作成する。
     *
     * @param folder
     * @return
     */
    public static final Boolean createFolder(String folder) {
        Boolean ret = false;
        try {
            Path folderPath = FileSystems.getDefault().getPath(folder);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            ret = true;
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * ファイルを削除する。
     *
     * @param file
     * @return
     */
    public static final Boolean deleteFile(String file) {
        Boolean ret = false;
        try {
            Path path = FileSystems.getDefault().getPath(file);
            Files.deleteIfExists(path);
            ret = true;
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * ファイルをコピーする。
     *
     * @param srcFile
     * @param dstFile
     * @return
     */
    public static final Boolean copyFile(String srcFile, String dstFile) {
        Boolean ret = false;
        try {
            Path srcPath = FileSystems.getDefault().getPath(srcFile);
            Path dstPath = FileSystems.getDefault().getPath(dstFile);
            Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
            ret = true;
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * ファイルを移動(リネーム)する。
     *
     * @param srcFile
     * @param dstFile
     * @return
     */
    public static final Boolean moveFile(String srcFile, String dstFile) {
        Boolean ret = false;
        try {
            File src = new File(srcFile);
            File dst = new File(dstFile);
            ret = src.renameTo(dst);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * 半角を1バイト、全角を2バイトとして、文字列が指定サイズになるよう末尾を空白埋めする。
     *
     * @param src
     * @param length
     * @return
     */
    public static final String padBytesString(String src, int length) {
        byte[] base = src.getBytes();
        Integer formatLen = length + src.length() - base.length;
        if (formatLen < src.length()) {
            formatLen = src.length();
        }
        String formatString = "%-" + formatLen.toString() + "s";
        String ret = String.format(formatString, src);
        return getBytesString(ret, 0, length);
    }

    /**
     * 半角を1バイト、全角を2バイトとして、文字列の一部を抽出する。 末尾の文字が全角文字の半分の場合、空白に置き換える。
     *
     * @param src
     * @param start
     * @param length
     * @return
     */
    public static final String getBytesString(String src, Integer start, Integer length) {
        byte[] base = src.getBytes();
        String ret = new String(base, start, length);

        // 末尾の文字が全角文字の半分の場合、空白に置き換える。
        String buf = new String(base, start, base.length - start);
        int lastIndex = ret.length() - 1;
        String lastString1 = buf.substring(lastIndex, lastIndex + 1);
        String lastString2 = ret.substring(lastIndex, lastIndex + 1);
        if (!lastString1.equals(lastString2)) {
            ret = ret.substring(0, lastIndex) + " ";
        }

        return ret;
    }
}
