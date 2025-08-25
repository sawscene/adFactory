/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ファイルユーティリティクラス
 *
 * @author e-mori
 */
public class FileUtils {

    private static final Logger logger = LogManager.getLogger();

    /**
     * ファイル作成
     *
     * @param file 作成するファイル情報
     * @param contents 書き込む内容
     */
    public static void create(File file, List<String> contents) throws IOException {
        if (file.createNewFile()) {
            logger.info("Created the file:{}", file);
            write(file, contents);
        } else {
            logger.info("Failed to create the file:{}", file);
        }
    }

    /**
     * ファイルの書き込み
     *
     * @param file 書き込み対象のファイル
     * @param contents 書き込む内容
     * @throws java.io.IOException
     */
    public static void write(File file, List<String> contents) throws IOException {
        if (exists(file)) {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "Shift_JIS"));
            //BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (String text : contents) {
                bw.write(text);
                bw.newLine();
            }
            bw.close();
        } else {
            logger.warn("Failed to write the file:{}", file);
        }
    }

    /**
     * ファイルの読み込み
     *
     * @param file 読み込み対象のファイル
     * @return 読み込んだ文字列
     * @throws java.io.FileNotFoundException
     */
    public static List<String> read(File file) throws FileNotFoundException, IOException {
        List<String> contents = new ArrayList<>();
        if (exists(file)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "Shift_JIS"));
            //BufferedReader br = new BufferedReader(new FileReader(file));
            String content;
            while ((content = br.readLine()) != null) {
                contents.add(content);
            }
            br.close();
        } else {
            logger.warn("Failed to open the file:{}", file);
        }
        return contents;
    }

    /**
     * ファイルの削除
     *
     * @param file 削除対象のファイル
     */
    public static void delete(File file) {
        if (exists(file)) {
            if (file.delete()) {
                logger.info("Deleted the file:{}", file);
            }
        } else {
            logger.warn("Failed to delete the file:{}", file);
        }
    }

    /**
     * ファイルの存在を確認する
     *
     * @param file 対象のファイル
     * @return 存在結果 true:存在する false:存在しない
     */
    public static Boolean exists(File file) {
        if (file.exists()) {
            if (file.isFile() && file.canWrite() && file.canRead()) {
                return true;
            }
        }
        return false;
    }

}
