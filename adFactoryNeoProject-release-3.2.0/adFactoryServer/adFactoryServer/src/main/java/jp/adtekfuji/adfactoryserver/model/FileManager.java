/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import jp.adtekfuji.adfactoryserver.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ファイルマネージャ(ファイル管理)クラス
 *
 * @author s-heya
 */
public class FileManager {

    private static final String FILE_CHAR = "MS932";
    private static final Properties systemProperties = new Properties();
    private final Logger logger = LogManager.getLogger();

    /**
     * データ種類
     */
    public enum Data {
        Manual("3rd" + File.separator + "apache-ftpserver-1.0.6" + File.separator + "res" + File.separator + "home" + File.separator + "data" + File.separator + "pdoc"),
        Import("import"),
        Config("conf"),
        REPORT("3rd" + File.separator + "apache-ftpserver-1.0.6" + File.separator + "res" + File.separator + "home" + File.separator + "data" + File.separator + "report"),
        COMMENTS("3rd" + File.separator + "apache-ftpserver-1.0.6" + File.separator + "res" + File.separator + "home" + File.separator + "data" + File.separator + "comments");

        private final String path;

        /**
         * コンストラクタ
         *
         * @param path
         */
        private Data(String path) {
            this.path = path;
        }

        /**
         * パスを取得する。
         *
         * @return
         */
        public String getPath() {
            return this.path;
        }
    }

    private static FileManager instance;

    /**
     * コンストラクタ
     */
    private FileManager() {
    }

    /**
     * インスタンスを取得する。
     *
     * @return
     */
    public static FileManager getInstance() {
        if (Objects.isNull(instance)) {
            instance = new FileManager();
            instance.initialize();
        }
        return instance;
    }

    /**
     * ファイルマネージャを初期化する。
     */
    private void initialize() {
        try {
            for (Data data : Data.values()) {
                File dir = new File(this.getLocalePath(data));
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }

            String filePath = this.getLocalePath("conf" + File.separator + "adFactory.properties");
            File file = new File(filePath);
            if (file.exists()) {
                systemProperties.load(new InputStreamReader(new FileInputStream(filePath), FILE_CHAR));

                for (Object key : systemProperties.keySet()) {
                    logger.info("System Properties: {}, {}", key, systemProperties.getProperty(key.toString(), ""));
                }
            } else {
                logger.info("File does not exist: " + filePath);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ディレクトリを作成する。
     *
     * @param data
     * @param target
     */
    public void createDirectory(FileManager.Data data, String target) {
        try {
            logger.info("createDirectory: {} {} {}", data.getPath(), target);

            File file = new File(this.getLocalePath(data, target));
            if (!file.exists()) {
                Files.createDirectories(file.toPath());
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ファイルをコピーする。
     *
     * @param data
     * @param source
     * @param target
     */
    public void copy(FileManager.Data data, String source, String target) {
        try {
            logger.info("Copy: {} {} {}", data.getPath(), source, target);

            File input = new File(this.getLocalePath(data, source));
            File output = new File(this.getLocalePath(data, target));
            Files.copy(input.toPath(), output.toPath());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ファイルを削除する。
     *
     * @param data
     * @param fileName
     */
    public void remove(FileManager.Data data, String fileName) {
        try {
            logger.info("Remove: {} {}", data.getPath(), fileName);

            File file = new File(this.getLocalePath(data, fileName));
            if (file.exists()) {
                Files.delete(file.toPath());
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ローカスパスを取得する。
     *
     * @param data
     * @return
     */
    private String getLocalePath(FileManager.Data data) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getenv(Constants.ADFACTORY_HOME));
        sb.append(File.separator);
        sb.append(data.getPath());
        return sb.toString();
    }

    /**
     * ローカルパスを取得する。
     *
     * @param data
     * @param fileName
     * @return
     */
    public String getLocalePath(FileManager.Data data, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getenv(Constants.ADFACTORY_HOME));
        sb.append(File.separator);
        sb.append(data.getPath());
        sb.append(File.separator);
        sb.append(fileName);
        return sb.toString();
    }

    /**
     * ローカルパスを取得する。
     *
     * @param filePath
     * @return
     */
    public String getLocalePath(String filePath) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getenv(Constants.ADFACTORY_HOME));
        sb.append(File.separator);
        sb.append(filePath);
        return sb.toString();
    }

    /**
     * 拡張子を取得する。
     *
     * @param fileName
     * @return
     */
    public String getSuffix(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1);
        }
        return fileName;
    }

    /**
     * システム設定を取得する。
     *
     * @return
     */
    public Properties getSystemProperties() {
        return systemProperties;
    }

    /**
     * ファイル名を列挙する。
     *
     * @param data
     * @param path
     * @return
     */
    public List<String> listFileName(FileManager.Data data, String path) {
        File dir = new File(this.getLocalePath(data, path));
        if (dir.exists()) {
            List<String> fileNames = new ArrayList<>();
            for (File file : dir.listFiles()) {
                fileNames.add(file.getName());
            }
            return fileNames;
        }
        return new ArrayList<>();
    }

    /**
     * ファイルを列挙する。
     *
     * @param data
     * @param path
     * @return
     */
    public List<File> listFile(FileManager.Data data, String path) {
        File dir = new File(this.getLocalePath(data, path));
        if (dir.exists()) {
            return Arrays.asList(dir.listFiles());
        }
        return new ArrayList<>();
    }

    /**
     * 一時ファイルを作成する。
     * 
     * @param prefix ファイル名の接頭辞
     * @param extension ファイルの拡張子
     * @param inputStream 入力ストリーム
     * @return
     * @throws Exception 
     */
    public String createTempFile(String prefix, String extension, InputStream inputStream) throws Exception {
        final Date now = new Date();

        String fileName = prefix + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now) + extension;
        String filePath = instance.getLocalePath(FileManager.Data.Import, fileName);

        BufferedInputStream bf = new BufferedInputStream(inputStream);
        byte[] buff = new byte[1024];

        try (OutputStream out = new FileOutputStream(new File(filePath))) {
            while (bf.read(buff) >= 0) {
                out.write(buff);
            }
            out.flush();
        }
        
        return filePath;
    }
}
