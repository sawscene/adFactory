/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.postgres;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.adtekfuji.addatabaseapp.utils.CMDContents;
import jp.adtekfuji.addatabaseapp.utils.ExecUtils;
import jp.adtekfuji.addatabaseapp.utils.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Postgresユーティリティクラス
 *
 * @author e-mori
 */
public class PostgresUtils {

    private static final Logger logger = LogManager.getLogger();
    private static final PostgresData POSTGRES_DATA = PostgresData.getInstance();

    /**
     * posgresが開始しているか確認
     *
     * @return true:起動している false:起動していない
     */
    public static Boolean isStartPostgres() {
        Boolean isStartPostgres = Boolean.FALSE;
        try {
            isStartPostgres = FileUtils.exists(new File(POSTGRES_DATA.getDATA_PATH() + File.separator + PGContents.FILE_PID));
            logger.info("isStartPostgres:{}", isStartPostgres);
            return isStartPostgres;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return isStartPostgres;
        }
    }

    /**
     * postgresがインストールされているか確認
     *
     * @return
     */
    private static Boolean isPostgresInstalled() {
        //To change body of generated methods, choose Tools | Templates.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * DBが存在しているか確認
     *
     * @param password
     * @return true:存在する false:存在しない
     */
    public static Boolean existDB(String password) {
        File bat = POSTGRES_DATA.getExistDBBat();
        bat.deleteOnExit();

        try {
            logger.info("existDB start.");

            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: postgreSQLのパスワードを変数にセットする。
            if (Objects.isNull(POSTGRES_DATA.getPgpass())) {
                contents.add(String.format(PGContents.SET_PGPASSWORD, password));
            }
            contents.add(new StringBuilder(CMDContents.SET_PATH).append(POSTGRES_DATA.getBIN_PATH()).toString());
            // バッチ処理: バッチ処理の画面表示をONにする。
            contents.add(CMDContents.ARG_ECHO_ON);
            // バッチ処理: クエリを実行する。(データベース一覧取得)
            contents.add(new StringBuilder(PGContents.PSQL_POSTGRES)
                    .append(" -lqt")
                    .toString());
            // バッチ処理: バッチ処理を終了する。
            contents.add(CMDContents.ARG_EXIT);

            // バッチファイルを作成する。
            if (bat.exists()) {
                FileUtils.delete(bat);
            }
            FileUtils.create(bat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            List<String> results = (List<String>) ExecUtils.exec(bat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR);
            Pattern p = Pattern.compile(PGContents.ADFACTORY_DB);
            for (String dbName : results) {
                Matcher m = p.matcher(dbName);
                if (m.find()) {
                    return true;
                }
            }
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(bat);
            logger.info("existDB end.");
        }
        return false;
    }

    /**
     * DB作成
     *
     */
    public static void createDB() {
        File bat = POSTGRES_DATA.getCreateDBBat();
        bat.deleteOnExit();

        try {
            logger.info("createDB start.");

            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: postgreSQLのパスワードを変数にセットする。
            if (Objects.isNull(POSTGRES_DATA.getPgpass())) {
                contents.add(String.format(PGContents.SET_PGPASSWORD, PGContents.USER));
            }
            contents.add(new StringBuilder(CMDContents.SET_PATH).append(POSTGRES_DATA.getBIN_PATH()).toString());
            // バッチ処理: バッチ処理の画面表示をONにする。
            contents.add(CMDContents.ARG_ECHO_ON);
            // バッチ処理: クエリを実行する。(adFactoryデータベース作成)
            contents.add(new StringBuilder(PGContents.PSQL_POSTGRES)
                    .append(PGContents.ARG_f).append(POSTGRES_DATA.getHOME()).append("\\db\\postgres\\create\\create_adfactorydb.sql")
                    .toString());
            // バッチ処理: クエリを実行する。(adFactoryテーブル作成)
            contents.add(new StringBuilder(PGContents.PSQL_ADFACTORYDB)
                    .append(PGContents.ARG_f).append(POSTGRES_DATA.getHOME()).append("\\db\\postgres\\create\\create_adfactorydb_tables.sql")
                    .toString());
            // バッチ処理: クエリを実行する。(DBバージョン初期値登録)
            contents.add(new StringBuilder(PGContents.PSQL_ADFACTORYDB)
                    .append(PGContents.ARG_c).append(PGContents.INSERT_VER)
                    .toString());
            // バッチ処理: バッチ処理を終了する。
            contents.add(CMDContents.ARG_EXIT);

            // バッチファイルを作成する。
            if (bat.exists()) {
                FileUtils.delete(bat);
            }
            FileUtils.create(bat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            ExecUtils.exec(bat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR);

        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(bat);
            logger.info("createDB end.");
        }
    }

    /**
     * 更新クエリー適用
     *
     * @param query
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void updateTable(File query) throws IOException, InterruptedException {
        File bat = new File(POSTGRES_DATA.getCreateDir() + File.separator + "update.bat");
        bat.deleteOnExit();

        try {
            logger.info("updateTable start.");
            logger.info("Update version:{}", query.getName());

            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: postgreSQLのパスワードを変数にセットする。
            if (Objects.isNull(POSTGRES_DATA.getPgpass())) {
                contents.add(String.format(PGContents.SET_PGPASSWORD, PGContents.PGPASSWORD));
            }
            contents.add(new StringBuilder(CMDContents.SET_PATH).append(POSTGRES_DATA.getBIN_PATH()).toString());
            // バッチ処理: バッチ処理の画面表示をONにする。
            contents.add(CMDContents.ARG_ECHO_ON);
            // バッチ処理: クエリファイルを実行する。
            contents.add(new StringBuilder(PGContents.PSQL_ADFACTORYDB)
                    .append(PGContents.ARG_f).append(query.getPath())
                    .toString());
            // バッチ処理: バッチ処理を終了する。
            contents.add(CMDContents.ARG_EXIT);

            // バッチファイルを作成する。
            if (bat.exists()) {
                FileUtils.delete(bat);
            }
            FileUtils.create(bat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            ExecUtils.exec(bat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR);

        } finally {
            FileUtils.delete(bat);
            logger.info("updateTable end.");
        }
    }

    /**
     * 更新クエリー適用
     *
     * @return
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static Boolean updateTables() throws IOException, InterruptedException {
        try {
            logger.info("updateTables start.");

            final String ext = ".sql";

            // DBバージョン確認
            int newVer = 1;
            int ver = Integer.parseInt(PostgresUtils.getDBversion());
            // テーブルにバージョン情報が存在しない場合
            if (newVer > ver) {
                if (!PostgresUtils.dropDB()) {
                    logger.warn("Ther is other session using the database.");
                    logger.fatal("Failed to update the database.");
                    return false;
                }
                PostgresUtils.createDB();
                PostgresUtils.changePassword();
            }

            List<File> files = POSTGRES_DATA.getPathUqdateQuery();
            files.forEach(o -> logger.info("File: " + o.getName()));

            for (File file : files) {
                int updateVer = Integer.parseInt(file.getName().replaceFirst(ext, ""));
                if (ver < updateVer) {
                    logger.info("Exec: " + file.getName());
                    newVer = updateVer;
                    updateTable(file);
                }
            }

            // 現在のバージョンよりも更新するバージョン情報が新しい場合更新処理実施
            if (ver < newVer) {
                putDBVersion(newVer);
            }
        } finally {
            logger.info("updateTables end.");
        }
        return true;
    }

    /**
     * テーブル削除クエリー適用
     *
     * @return true:成功 false：失敗
     */
    public static Boolean dropDB() {
        File bat = POSTGRES_DATA.getDropDBBat();
        bat.deleteOnExit();

        try {
            logger.info("dropTable start.");

            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: postgreSQLのパスワードを変数にセットする。
            if (Objects.isNull(POSTGRES_DATA.getPgpass())) {
                contents.add(String.format(PGContents.SET_PGPASSWORD, PGContents.PGPASSWORD));
            }
            contents.add(new StringBuilder(CMDContents.SET_PATH).append(POSTGRES_DATA.getBIN_PATH()).toString());
            // バッチ処理: バッチ処理の画面表示をONにする。
            contents.add(CMDContents.ARG_ECHO_ON);
            // バッチ処理: クエリを実行する。(adFactoryデータベース削除)
            contents.add(new StringBuilder(PGContents.PSQL_POSTGRES)
                    .append(PGContents.ARG_f).append(POSTGRES_DATA.getHOME()).append("\\db\\postgres\\create\\drop_adfactorydb.sql")
                    .toString());
            // バッチ処理: バッチ処理を終了する。
            contents.add(CMDContents.ARG_EXIT);

            // バッチファイルを作成する。
            if (bat.exists()) {
                FileUtils.delete(bat);
            }
            FileUtils.create(bat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            List<String> results = (List<String>) ExecUtils.exec(bat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR);
            String tag = "";
            for (String ver : results) {
                if (tag.equals("DROP DATABASE")) {
                    return true;
                }
                tag = ver;
            }
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(bat);
            logger.info("dropTable end.");
        }
        return false;
    }

    /**
     * DBバージョン取得用クエリー適用
     *
     * @return
     */
    public static String getDBversion() {
        File bat = new File(POSTGRES_DATA.getCreateDir() + File.separator + "getTVer.bat");
        bat.deleteOnExit();

        try {
            logger.info("getDBversion start.");
            String ret = "0";

            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: postgreSQLのパスワードを変数にセットする。
            if (Objects.isNull(POSTGRES_DATA.getPgpass())) {
                contents.add(String.format(PGContents.SET_PGPASSWORD, PGContents.PGPASSWORD));
            }
            contents.add(new StringBuilder(CMDContents.SET_PATH).append(POSTGRES_DATA.getBIN_PATH()).toString());
            // バッチ処理: バッチ処理の画面表示をONにする。
            contents.add(CMDContents.ARG_ECHO_ON);
            // バッチ処理: クエリを実行する。(adFactoryのDBバージョン取得)
            contents.add(new StringBuilder(PGContents.PSQL_ADFACTORYDB)
                    .append(PGContents.ARG_c).append(PGContents.SELECT_VER)
                    .toString());
            // バッチ処理: バッチ処理を終了する。
            contents.add(CMDContents.ARG_EXIT);

            // バッチファイルを作成する。
            if (bat.exists()) {
                FileUtils.delete(bat);
            }
            FileUtils.create(bat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            List<String> results = (List<String>) ExecUtils.exec(bat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR);

            String tag = "";
            for (String ver : results) {
                if (tag.equals("-------")) {
                    ret = ver.substring(1);
                    break;
                }
                tag = ver;
            }

            logger.info("result:{}", ret);
            return ret;
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
            return "";
        } finally {
            FileUtils.delete(bat);
            logger.info("getDBversion end.");
        }
    }

    /**
     * DBバージョン更新クエリー適用
     *
     * @param ver
     */
    public static void putDBVersion(Integer ver) {
        File bat = new File(POSTGRES_DATA.getCreateDir() + File.separator + "putTVer.bat");
        bat.deleteOnExit();

        try {
            logger.info("putDBVersion start.");
            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: postgreSQLのパスワードを変数にセットする。
            if (Objects.isNull(POSTGRES_DATA.getPgpass())) {
                contents.add(String.format(PGContents.SET_PGPASSWORD, PGContents.PGPASSWORD));
            }
            contents.add(new StringBuilder(CMDContents.SET_PATH).append(POSTGRES_DATA.getBIN_PATH()).toString());
            // バッチ処理: バッチ処理の画面表示をONにする。
            contents.add(CMDContents.ARG_ECHO_ON);
            // バッチ処理: クエリを実行する。(adFactoryのDBバージョン更新)
            contents.add(new StringBuilder(PGContents.PSQL_ADFACTORYDB)
                    .append(PGContents.ARG_c).append(String.format(PGContents.UPDATE_VER, ver))
                    .toString());
            // バッチ処理: バッチ処理を終了する。
            contents.add(CMDContents.ARG_EXIT);

            // バッチファイルを作成する。
            if (bat.exists()) {
                FileUtils.delete(bat);
            }
            FileUtils.create(bat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            ExecUtils.exec(bat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR);

        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(bat);
            logger.info("putDBVersion end.");
        }
    }

    /**
     * スーパーユーザーのパスワードを変更する
     */
    public static void changePassword() {
        File bat = new File(POSTGRES_DATA.getCreateDir() + File.separator + "update.bat");
        bat.deleteOnExit();

        try {
            logger.info("changePassword start.");

            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: postgreSQLのパスワードを変数にセットする。
            if (Objects.isNull(POSTGRES_DATA.getPgpass())) {
                contents.add(String.format(PGContents.SET_PGPASSWORD, PGContents.USER));
            }
            contents.add(new StringBuilder(CMDContents.SET_PATH).append(POSTGRES_DATA.getBIN_PATH()).toString());
            // バッチ処理: バッチ処理の画面表示をONにする。
            contents.add(CMDContents.ARG_ECHO_ON);
            // バッチ処理: クエリを実行する。(postgresのパスワード変更)
            contents.add(new StringBuilder(PGContents.PSQL_ADFACTORYDB)
                    .append(PGContents.ARG_c).append(String.format(PGContents.ALTER_PASSWORD, PGContents.PGPASSWORD, "=adfAct0ry1"))
                    .toString());
            // バッチ処理: バッチ処理を終了する。
            contents.add(CMDContents.ARG_EXIT);

            // バッチファイルを作成する。
            if (bat.exists()) {
                FileUtils.delete(bat);
            }
            FileUtils.create(bat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            ExecUtils.exec(bat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR);

        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(bat);
            logger.info("changePassword end.");
        }
    }

    /**
     * adFactoryDBの情報を取得する。
     *
     * @return
     */
    public static Map<String, String> getDBInfo() {
        logger.info("getDBInfo start.");
        Map<String, String> dbInfos = new LinkedHashMap();

        File bat = new File(POSTGRES_DATA.getCreateDir() + File.separator + "getDBInfo.bat");
        bat.deleteOnExit();

        try {
            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: postgreSQLのパスワードを変数にセットする。
            if (Objects.isNull(POSTGRES_DATA.getPgpass())) {
                contents.add(String.format(PGContents.SET_PGPASSWORD, PGContents.PGPASSWORD));
            }
            contents.add(new StringBuilder(CMDContents.SET_PATH).append(POSTGRES_DATA.getBIN_PATH()).toString());
            // バッチ処理: バッチ処理の画面表示をONにする。
            contents.add(CMDContents.ARG_ECHO_ON);
            // バッチ処理: クエリを実行する。(adFactoryのDBバージョン取得)
            contents.add(new StringBuilder(PGContents.PSQL_ADFACTORYDB)
                    .append(PGContents.ARG_c).append(PGContents.SELECT_VER)
                    .toString());
            // バッチ処理: クエリを実行する。(adFactoryデータベースのサイズ取得)
            contents.add(new StringBuilder(PGContents.PSQL_ADFACTORYDB)
                    .append(PGContents.ARG_c).append(PGContents.SELECT_ADFACTORY_DB_SIZE)
                    .toString());
            // バッチ処理: クエリを実行する。(adFactoryデータベースのデッドタプル総数取得)
            contents.add(new StringBuilder(PGContents.PSQL_ADFACTORYDB)
                    .append(PGContents.ARG_c).append(PGContents.SELECT_DEAD_TUPLE_COUNT)
                    .toString());
            // バッチ処理: バッチ処理を終了する。
            contents.add(CMDContents.ARG_EXIT);

            // バッチファイルを作成する。
            if (bat.exists()) {
                FileUtils.delete(bat);
            }
            FileUtils.create(bat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            List<String> rows = (List<String>) ExecUtils.exec(bat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR);

            String name = "";
            boolean isGet = false;
            for (String row : rows) {
                if (isGet) {
                    if (!dbInfos.containsKey(name)) {
                        dbInfos.put(name, row.trim());
                    }
                    isGet = false;
                } else if (row.isEmpty()) {
                    name = row;
                } else if (!name.isEmpty() && row.matches("-*")) {
                    isGet = true;
                } else {
                    name = row.trim();
                }
            }

        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(bat);
            logger.info("getDBInfo end.");
        }
        return dbInfos;
    }

    /**
     * adFactoryDBのバックアップファイルを作成する。(localhost)
     *
     * @param filePath バックアップファイルパス
     * @return 結果 (true:成功, false:失敗)
     */
    public static Boolean backupDB(String filePath) {
        return backupDB(filePath, PGContents.HOST, PGContents.PORT, PGContents.ADFACTORY_DB, PGContents.USER, PGContents.PGPASSWORD);
    }

    /**
     * データベースのバックアップファイルを作成する。
     *
     * @param filePath バックアップファイルパス
     * @param host ホスト名
     * @param port 接続ポート (標準:5432)
     * @param dname データベース名
     * @param username ユーザー
     * @param password パスワード
     * @return 結果 (true:成功, false:失敗)
     */
    private static Boolean backupDB(String filePath, String host, String port, String dname, String username, String password) {
        logger.info("backupDB start: {}", filePath);
        Boolean result = false;

        File backupDBBat = new File(POSTGRES_DATA.getCreateDir() + File.separator + "backup_adfactorydb.bat");
        backupDBBat.deleteOnExit();

        try {
            // バックアップフォルダ
            Path folderPath = FileSystems.getDefault().getPath(new File(filePath).getParent());
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: postgreSQLのパスワードを変数にセットする。
            if (Objects.isNull(POSTGRES_DATA.getPgpass())) {
                contents.add(String.format(PGContents.SET_PGPASSWORD, password));
            }
            contents.add(new StringBuilder(CMDContents.SET_PATH).append(POSTGRES_DATA.getBIN_PATH()).toString());
            // バッチ処理: バッチ処理の画面表示をONにする。
            contents.add(CMDContents.ARG_ECHO_ON);
            // バッチ処理: データベースのバックアップファイルを作成する。
            contents.add(String.format("pg_dump -h %s -p %s -U %s -f \"%s\" -Fc %s", host, port, username, filePath, dname));
            // バッチ処理: バッチ処理を終了する。
            contents.add(CMDContents.ARG_EXIT);

            // バッチファイルを作成する。
            if (backupDBBat.exists()) {
                FileUtils.delete(backupDBBat);
            }
            FileUtils.create(backupDBBat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            ExecUtils.exec(backupDBBat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR);

            // バックアップファイルが作成されていないか、サイズが0の場合は失敗
            //      ※パスワードが無効等でpg_dumpに失敗した場合、サイズ0のファイルが作成される。
            File backupFile = new File(filePath);
            if (backupFile.exists() && backupFile.isFile() && backupFile.length() > 0) {
                if (backupFile.length() > 0) {
                    result = true;
                } else {
                    FileUtils.delete(backupFile);// pg_dump失敗時の空ファイルは削除する。
                }
            }
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(backupDBBat);
            logger.info("backupDB end: {}", result);
        }
        return result;
    }

    /**
     * バックアップファイルからadFactoryDBをリストアする。(localhost)
     *
     * @param filePath バックアップファイルパス
     * @return 結果 (true:成功, false:失敗)
     */
    public static Boolean restoreDB(String filePath) {
        return restoreDB(filePath, PGContents.HOST, PGContents.PORT, PGContents.ADFACTORY_DB, PGContents.USER, PGContents.PGPASSWORD);
    }

    /**
     * バックアップファイルからデータベースをリストアする。
     *
     * @param filePath バックアップファイルパス
     * @param host ホスト名
     * @param port 接続ポート (標準:5432)
     * @param dname データベース名
     * @param username ユーザー
     * @param password パスワード
     * @return 結果 (true:成功, false:失敗)
     */
    private static Boolean restoreDB(String filePath, String host, String port, String dname, String username, String password) {
        logger.info("restoreDB start: {}", filePath);
        Boolean result = false;

        // TomEEを停止する。
        if (!serviceStop(PGContents.TOMEE, PGContents.TOMEE_SERVICE)) {
            return result;
        }
        // adInterfaceServiceを停止する。
        if (!serviceStop(PGContents.ADINTERFACE, PGContents.ADINTERFACE_SERVICE)) {
            return result;
        }

        File restoreDBBat = new File(POSTGRES_DATA.getCreateDir() + File.separator + "restore_adfactorydb.bat");
        restoreDBBat.deleteOnExit();

        try {
            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: postgreSQLのパスワードを変数にセットする。
            if (Objects.isNull(POSTGRES_DATA.getPgpass())) {
                contents.add(String.format(PGContents.SET_PGPASSWORD, password));
            }
            contents.add(new StringBuilder(CMDContents.SET_PATH).append(POSTGRES_DATA.getBIN_PATH()).toString());
            // バッチ処理: バッチ処理の画面表示をONにする。
            contents.add(CMDContents.ARG_ECHO_ON);
            // バッチ処理: データベースの存在確認する。(存在しない場合は削除をスキップ)
            contents.add(String.format("psql -l -h %s -p %s -U %s | findstr \"%s\"", host, port, username, dname));
            contents.add("if %ERRORLEVEL% neq 0 goto EXEC_RESTORE");
            // バッチ処理: データベースを削除する。(失敗時は処理を終了して、戻り値として 1 を返す)
            contents.add(String.format("dropdb -h %s -p %s -U %s %s", host, port, username, dname));
            contents.add("if %ERRORLEVEL% neq 0 exit /b 1");
            // バッチ処理: データベースのリストアを実行する。(失敗時は処理を終了して、戻り値として 2 を返す)
            contents.add(":EXEC_RESTORE");
            contents.add(String.format("pg_restore -h %s -p %s -U %s -C -d postgres \"%s\"", host, port, username, filePath));
            contents.add("if %ERRORLEVEL% neq 0 exit /b 2");
            // バッチ処理: バッチ処理を終了する。(成功: 戻り値として 0 を返す)
            contents.add("exit /b 0");

            // バッチファイルを作成する。
            if (restoreDBBat.exists()) {
                FileUtils.delete(restoreDBBat);
            }
            FileUtils.create(restoreDBBat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            Object execRet = ExecUtils.exec(restoreDBBat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR_RESULT);

            // adInterfaceServiceを開始する。
            if (!serviceStart(PGContents.ADINTERFACE, PGContents.ADINTERFACE_SERVICE)) {
                return result;
            }
            // TomEEを開始する。
            if (!serviceStart(PGContents.TOMEE, PGContents.TOMEE_SERVICE)) {
                return result;
            }

            if (execRet instanceof Integer && (Integer) execRet == 0) {
                // adFactoryDBが消えている場合は失敗
                //      ※不正な、あるいは破損したバックアップファイルが指定された場合に起こりうる
                result = existDB(password);
            }
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(restoreDBBat);
            logger.info("restoreDB end: {}", result);
        }
        return result;
    }

    /**
     * データベースバージョンが古いか確認する
     *
     * @return
     */
    public static Boolean isDBVerOld() {
        try {
            logger.info("isDBVerOld start.");

            final String ext = ".sql";

            // DBバージョン確認
            int ver = Integer.parseInt(PostgresUtils.getDBversion());

            // 更新クエリバージョン確認
            List<File> files = POSTGRES_DATA.getPathUqdateQuery();
            int latestVer = Integer.parseInt(files.get(files.size() - 1).getName().replaceFirst(ext, ""));

            // 現在のDBバージョンよりも新しい更新クエリが存在する場合、trueを返す
            return (ver < latestVer);
        } finally {
            logger.info("isDBVerOld end.");
        }
    }

    /**
     * DBのメンテナンスを実行する(VACUUM FULL, CLUSTER を実行) (localhost)
     *
     * @return 
     */
    public static Boolean excuteMaintenance() {
        return excuteMaintenance(PGContents.HOST, PGContents.PORT, PGContents.ADFACTORY_DB, PGContents.USER, PGContents.PGPASSWORD);
    }

    /**
     * DBのメンテナンスを実行する(VACUUM FULL, CLUSTER を実行) (localhost)
     *
     * @param host ホスト名
     * @param port 接続ポート (標準:5432)
     * @param dname データベース名
     * @param username ユーザー
     * @param password パスワード
     * @return 結果 (true:成功, false:失敗)
     */
    private static Boolean excuteMaintenance(String host, String port, String dname, String username, String password) {
        logger.info("excuteMaintenance start:");
        Boolean result = false;

        // TomEEを停止する。
        if (!serviceStop(PGContents.TOMEE, PGContents.TOMEE_SERVICE)) {
            return result;
        }
        // adInterfaceServiceを停止する。
        if (!serviceStop(PGContents.ADINTERFACE, PGContents.ADINTERFACE_SERVICE)) {
            return result;
        }

        File maintenanceDBBat = new File(POSTGRES_DATA.getCreateDir() + File.separator + "maintenance_adfactorydb.bat");
        maintenanceDBBat.deleteOnExit();

        try {
            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: postgreSQLのパスワードを変数にセットする。
            if (Objects.isNull(POSTGRES_DATA.getPgpass())) {
                contents.add(String.format(PGContents.SET_PGPASSWORD, password));
            }
            contents.add(new StringBuilder(CMDContents.SET_PATH).append(POSTGRES_DATA.getBIN_PATH()).toString());
            // バッチ処理: バッチ処理の画面表示をONにする。
            contents.add(CMDContents.ARG_ECHO_ON);
            // バッチ処理: VACUUM FULL を実行する。(失敗時は処理を終了して、戻り値として 1 を返す)
            contents.add(String.format("psql -h %s -p %s -U %s -d %s -c \"VACUUM FULL\"", host, port, username, dname));
            contents.add("if %ERRORLEVEL% neq 0 exit /b 1");
            // バッチ処理: CLUSTER を実行する。(失敗時は処理を終了して、戻り値として 2 を返す)
            contents.add(String.format("psql -h %s -p %s -U %s -d %s -c \"CLUSTER\"", host, port, username, dname));
            contents.add("if %ERRORLEVEL% neq 0 exit /b 2");
            // バッチ処理: ANALYZE を実行する。(失敗時は処理を終了して、戻り値として 3 を返す)
            contents.add(String.format("psql -h %s -p %s -U %s -d %s -c \"ANALYZE\"", host, port, username, dname));
            contents.add("if %ERRORLEVEL% neq 0 exit /b 3");
            // バッチ処理: バッチ処理を終了する。(成功: 戻り値として 0 を返す)
            contents.add("exit /b 0");

            // バッチファイルを作成する。
            if (maintenanceDBBat.exists()) {
                FileUtils.delete(maintenanceDBBat);
            }
            FileUtils.create(maintenanceDBBat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            Object execRet = ExecUtils.exec(maintenanceDBBat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR_RESULT);

            // adInterfaceServiceを開始する。
            if (!serviceStart(PGContents.ADINTERFACE, PGContents.ADINTERFACE_SERVICE)) {
                return result;
            }
            // TomEEを開始する。
            if (!serviceStart(PGContents.TOMEE, PGContents.TOMEE_SERVICE)) {
                return result;
            }

            if (execRet instanceof Integer && (Integer) execRet == 0) {
                result = true;
            }
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(maintenanceDBBat);
            logger.info("excuteMaintenance end: {}", result);
        }
        return result;
    }

    /**
     * サービスを開始する。
     *
     * @param dispName サービスの表示名
     * @param serviceName サービス名
     * @return 結果 (true:成功, false:失敗)
     */
    private static Boolean serviceStart(String dispName, String serviceName) {
        logger.info("serviceStart start:");
        Boolean result = false;

        File bat = new File(POSTGRES_DATA.getCreateDir() + File.separator + "service_start.bat");
        bat.deleteOnExit();

        try {
            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: 対象サービスが起動中のサービスリストに存在するか確認する。
            contents.add(String.format("net start | findstr \"%s\"", dispName));
            // バッチ処理: ある場合は成功扱いにする。
            contents.add("if %ERRORLEVEL% equ 0 goto SUCCESS");
            // バッチ処理: ない場合は対象サービスを起動する。
            contents.add(String.format("net start \"%s\"", serviceName));
            // バッチ処理: 開始できたら成功。
            contents.add("if %ERRORLEVEL% equ 0 goto SUCCESS");
            // バッチ処理: バッチ処理を終了する。(失敗: 戻り値として 1 を返す)
            contents.add("exit /b 1");
            // バッチ処理: バッチ処理を終了する。(成功: 戻り値として 0 を返す)
            contents.add(":SUCCESS");
            contents.add("exit /b 0");
  
            // バッチファイルを作成する。
            if (bat.exists()) {
                FileUtils.delete(bat);
            }
            FileUtils.create(bat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            Object execRet = ExecUtils.exec(bat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR_RESULT);

            if (execRet instanceof Integer && (int)execRet == 0) {
                result = true;
            }
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(bat);
            logger.info("serviceStart end: {}", result);
        }
        return result;
    }

    /**
     * サービスを停止する。
     *
     * @param dispName サービスの表示名
     * @param serviceName サービス名
     * @return 結果 (true:成功, false:失敗)
     */
    private static Boolean serviceStop(String dispName, String serviceName) {
        logger.info("serviceStop start:");
        Boolean result = false;

        File bat = new File(POSTGRES_DATA.getCreateDir() + File.separator + "service_stop.bat");
        bat.deleteOnExit();

        try {
            List<String> contents = new LinkedList();
            // バッチ処理: バッチ処理の画面表示をOFFにする。
            contents.add(CMDContents.ARG_ECHO_OFF);
            // バッチ処理: 対象サービスが起動中のサービスリストに存在するか確認する。
            contents.add(String.format("net start | findstr \"%s\"", dispName));
            // バッチ処理: ない場合は成功扱いにする。
            contents.add("if %ERRORLEVEL% neq 0 goto SUCCESS");
            // バッチ処理: 起動していたら対象サービスを停止する。
            contents.add(String.format("net stop \"%s\"", serviceName));
            // バッチ処理: 停止できたら成功。
            contents.add("if %ERRORLEVEL% equ 0 goto SUCCESS");
            // バッチ処理: バッチ処理を終了する。(失敗: 戻り値として 1 を返す)
            contents.add("exit /b 1");
            // バッチ処理: バッチ処理を終了する。(成功: 戻り値として 0 を返す)
            contents.add(":SUCCESS");
            contents.add("exit /b 0");
  
            // バッチファイルを作成する。
            if (bat.exists()) {
                FileUtils.delete(bat);
            }
            FileUtils.create(bat, contents);

            // バッチファイルを実行する。
            String[] pgCtlArg = {};
            Object execRet = ExecUtils.exec(bat.getPath(), pgCtlArg, ExecUtils.ExeProcessEnum.WAIT_FOR_RESULT);

            if (execRet instanceof Integer && (int)execRet == 0) {
                result = true;
            }
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(bat);
            logger.info("serviceStop end: {}", result);
        }
        return result;
    }
}
