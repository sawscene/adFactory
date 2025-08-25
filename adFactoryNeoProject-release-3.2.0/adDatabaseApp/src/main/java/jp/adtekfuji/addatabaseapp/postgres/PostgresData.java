/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.postgres;

import adtekfuji.utility.StringUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.addatabaseapp.utils.FileUtils;

/**
 * Porstgres環境変数クラス
 *
 * @author e-mori
 */
public class PostgresData {

    private String HOME;
    private final String BIN_PATH;
    private final String DATA_PATH;
    private final File createDir;
    private final File updateDir;
    private final File createDBBat;
    private final File dropDBBat;
    private final File existDBBat;
    private final List<File> pathUqdateQuery;
    private final File pgpassPath;
    private final String pgpass;

    private static PostgresData postgresData = null;

    public static PostgresData getInstance() {
        if (Objects.isNull(postgresData)) {
            postgresData = new PostgresData();
        }
        return postgresData;
    }

    private PostgresData() {
        this.HOME = System.getenv("ADFACTORY_HOME");
        if (StringUtils.isEmpty(this.HOME)) {
            // カレントパスを取得
            this.HOME = new File(".").getAbsoluteFile().getParentFile().getParent();
        }

        String postgresHome = System.getenv("POSTGRES_HOME");
        if (StringUtils.isEmpty(postgresHome)) {
            // adFactory インストーラーからインストールされた場合
            this.BIN_PATH = HOME + File.separator + "3rd" + File.separator + PGContents.PG_DIR + File.separator + "bin";
            this.DATA_PATH = HOME + File.separator + "3rd" + File.separator + PGContents.PG_DIR + File.separator + "data";
        } else {
            // PostgreSQL インストーラーからインストールされた場合
            this.BIN_PATH = postgresHome + File.separator + "bin";
            this.DATA_PATH = postgresHome + File.separator + "data";
        }

        this.createDir = new File(HOME + File.separator + "db" + File.separator + "postgres" + File.separator + "create");
        this.updateDir = new File(HOME + File.separator + "db" + File.separator + "postgres" + File.separator + "update");
        this.createDBBat = new File(createDir + File.separator + "create_adfactorydb.bat");
        this.dropDBBat = new File(createDir + File.separator + "drop_adfactorydb.bat");
        this.existDBBat = new File(createDir + File.separator + "exist_adfactorydb.bat");

        if (updateDir.exists()) {
            // SQLファイルを検索
            final String ext = ".sql";
            this.pathUqdateQuery = Arrays.asList(this.updateDir.listFiles());
            this.pathUqdateQuery.sort((File left,  File right) -> {
               int leftVver = Integer.parseInt(left.getName().replaceFirst(ext, ""));
               int rightVer = Integer.parseInt(right.getName().replaceFirst(ext, ""));
               return leftVver - rightVer;
            });
        } else {
            this.pathUqdateQuery = new ArrayList<>();
        }

        this.pgpassPath = new File(System.getenv("USERPROFILE") + PGContents.PASS_PATH);
        this.pgpass = setPgPass();
    }

    public String getHOME() {
        return HOME;
    }

    public String getBIN_PATH() {
        return BIN_PATH;
    }

    public String getDATA_PATH() {
        return DATA_PATH;
    }

    public File getCreateDir() {
        return createDir;
    }

    public File getUpdateDir() {
        return updateDir;
    }

    public File getCreateDBBat() {
        return createDBBat;
    }

    public File getDropDBBat() {
        return dropDBBat;
    }

    public File getExistDBBat() {
        return existDBBat;
    }

    public List<File> getPathUqdateQuery() {
        return pathUqdateQuery;
    }

    public File getPgpassPath() {
        return pgpassPath;
    }

    public String getPgpass() {
        return pgpass;
    }

    /**
     * postgresパスワード抽出
     *
     */
    private String setPgPass() {
        String result = null;
        try {
            for (String content : FileUtils.read(pgpassPath)) {
                String[] userData = content.split(":", 0);
                if (PGContents.HOST.equals(userData[0]) && PGContents.USER.equals(userData[3])) {
                    result = userData[3];
                    break;
                }
            }
            return result;
        } catch (Exception ex) {
            return result;
        }
    }
}
