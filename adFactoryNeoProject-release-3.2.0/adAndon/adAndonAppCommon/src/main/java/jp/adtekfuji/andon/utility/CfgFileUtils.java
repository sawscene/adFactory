/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.utility;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * レイアウトファイル(.cfg)を扱う
 * 
 * @author fu-kato
 */
public class CfgFileUtils {

    // cfgファイルの中身
    // 初期状態ではDashboardBackup.iniが入っているがカスタマイズツールで保存されたものは
    public static final String CFG_LAYOUT_INI = "DashboardBackup.ini";
    public static final String CFG_LAYOUT2_INI = "Dashboard2Backup.ini";
    public static final String CFG_CUSTOMIZETOOL_LAYOUT_XML = "adAndonCustomizeToolLayoutInfo.xml";

    /**
     * レイアウト設定をcfgファイルに出力する
     *
     * @param path 出力先
     * @param layout レイアウト設定
     * @param customizeToolLayout カスタマイズツールレイアウト設定
     * @throws java.io.IOException
     */
    public static void createCfgFile(Path path, String layout, String customizeToolLayout) throws IOException {
        if (Objects.isNull(layout) || Objects.isNull(customizeToolLayout)) {
            return;
        }

        final ZipEntry layoutIni = new ZipEntry(CFG_LAYOUT_INI);
        final ZipEntry customizeLayoutXml = new ZipEntry(CFG_CUSTOMIZETOOL_LAYOUT_XML);

        final FileOutputStream fos = new FileOutputStream(path.toFile());
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {
            zos.putNextEntry(layoutIni);
            zos.write(layout.getBytes(Charset.forName("Shift-JIS")));
            zos.closeEntry();
            zos.putNextEntry(customizeLayoutXml);
            zos.write(customizeToolLayout.getBytes(Charset.forName("UTF-8")));
            zos.closeEntry();
        }
    }
    
    public static String loadInternal(Path path, String filename) throws IOException {
        final String charset = Objects.equals(filename, CFG_LAYOUT_INI) || Objects.equals(filename, CFG_LAYOUT2_INI) ? "Shift-JIS" : "UTF-8";
        return loadInternal(path, filename, charset);
    }
    
    /**
     * zipファイル内部にファイルが存在するか調べる
     *
     * @param path
     * @param filename
     * @return
     * @throws java.io.IOException
     */
    public static boolean isInternal(Path path, String filename) throws IOException {
        final ZipFile zipFile = new ZipFile(path.toFile());
        final ZipEntry entry = zipFile.getEntry(filename);
        return Objects.nonNull(entry);
    }

    /**
     * cfgファイルを解凍し内部のレイアウトファイルを文字列として取り出す
     *
     * @param path cfgファイルパス
     * @param filename cfg内部のファイル　CFG_LAYOUT_INI または CFG_CFG_CUSTOMIZETOOL_LAYOUT_XML
     * @param charset
     * @return ファイルの中身の文字列
     * @throws java.io.IOException
     */
    public static String loadInternal(Path path, String filename, String charset) throws IOException {
        final ZipFile zipFile = new ZipFile(path.toFile());
        final ZipEntry entry = zipFile.getEntry(filename);

        return convertInputStreamToString(zipFile.getInputStream(entry), charset);
    }

    /**
     * InputStreamをそのまま文字列として取り出す
     *
     * @param is
     * @param charset
     * @return
     * @throws IOException
     */
    public static String convertInputStreamToString(InputStream is, String charset) throws IOException {
        if (Objects.isNull(is)) {
            return null;
        }

        InputStreamReader isr = new InputStreamReader(is, Charset.forName(charset));
        StringBuilder sblayout = new StringBuilder();
        char[] buffer = new char[1024];
        for (;;) {
            int len = isr.read(buffer);
            if (len < 0) {
                break;
            }
            sblayout.append(buffer, 0, len);
        }
        return sblayout.toString();
    }
}
