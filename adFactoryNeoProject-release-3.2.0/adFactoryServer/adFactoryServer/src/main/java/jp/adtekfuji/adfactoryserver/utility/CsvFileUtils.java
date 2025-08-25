/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * CSVファイルユーティリティクラス
 * 注意: 文字列データにダブルコーテーション(")が入っていない場合は、CSVファイルを読み込み出来ません。
 *
 * @author nar-nakamura
 */
public class CsvFileUtils {

    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_QUOTE = "\"";
    private static final String CSV_LF = "\n";

    /**
     * CSVファイルを読み込む。
     *
     * @param path ファイルパス
     * @param readStartRow 何行目から読み込むか
     * @param encode 文字コード
     * @return
     * @throws Exception 
     */
    public static List<List<String>> readCsv(String path, int readStartRow, String encode) throws Exception {
        List<List<String>> rows = new ArrayList<>();

        // シフトJISの場合はMS932を指定する。
        String charsetName = encode.toUpperCase();
        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS", "S-JIS").contains(charsetName)) {
            charsetName = "MS932";
        }

        File file = new File(path);
        if (!file.exists()) {
            // ファイルが存在しない
            final Logger logger = LogManager.getLogger();
            logger.info("File does not exist:{}", path);
            return rows;
        }

        InputStream input = new FileInputStream(path);
        InputStreamReader ireader = new InputStreamReader(input, charsetName);
        try (BufferedReader br = new BufferedReader(ireader)) {
            Boolean isColConnect = false;
            Boolean isRowConnect = false;
            Boolean isTopCol = true;
            String col = "";
            String line;
            List<String> row = new ArrayList<>();
            int rowNum = 0;
            while ((line = br.readLine()) != null) {
                rowNum++;
                if (rowNum < readStartRow) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line, CSV_SEPARATOR);
                while (st.hasMoreTokens()) {
                    String buf = st.nextToken();
                    if (isRowConnect) {
                        col += CSV_LF + buf;
                        isRowConnect = false;
                    } else if (isColConnect) {
                        col += CSV_SEPARATOR + buf;
                    } else {
                        if (col.length() >= 2 && col.startsWith(CSV_QUOTE)) {
                            col = col.substring(1, col.length() - 1);
                            row.add(col);
                            col = buf;
                        } else if (isTopCol) {
                            col = buf;
                            isTopCol = false;
                        } else {
                            col += CSV_SEPARATOR + buf;
                        }
                    }
                    isColConnect = !(col.length() >= 2
                            && col.startsWith(CSV_QUOTE)
                            && col.endsWith(CSV_QUOTE));
                }
                if (!isColConnect) {
                    col = col.substring(1, col.length() - 1);
                    if (isTopCol) {
                        row = new ArrayList<>();
                    }
                    row.add(col);
                    rows.add(row);
                    row = new ArrayList<>();
                    isTopCol = true;
                }
                isRowConnect = isColConnect;
            }
        }
        return rows;
    }
}
