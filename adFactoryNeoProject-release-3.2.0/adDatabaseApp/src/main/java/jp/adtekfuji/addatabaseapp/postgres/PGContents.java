/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.postgres;

/**
 * PSQLコマンド定義クラス
 *
 * @author e-mori
 */
public class PGContents {

    private static final String T_VER_SID = "2";// DBバージョンID

    public static final String PASS_PATH = "\\AppData\\Roaming\\postgresql\\pgpass.conf";
    public static final String PG_DIR = "postgreSQL11";
    public static final String ADFACTORY_DB = "adFactoryDB2";
    public static final String USER = "postgres";
    public static final String ADFACTORY_USER = "adfactory";
    public static final String HOST = "localhost";
    public static final String PORT = "15432";
    public static final String FILE_PID = "postmaster.pid";
    public static final String EXE_CTL = "pg_ctl.exe";
    public static final String EXE_PSQL = "psql.exe";
    public static final String ARG_U = " -U ";// 接続ユーザー名を指定する。
    public static final String ARG_c = " -c ";// コマンド文字列を実行する。
    public static final String ARG_d = " -d ";// 接続するデータベース名を指定する。
    public static final String ARG_f = " -f ";// ファイルをコマンドのソースとして使用する。
    public static final String ARG_p = " -p ";// ポート番号を指定する。
    public static final String ARG_START = "start";
    public static final String ARG_STOP = "stop";
    public static final String ARG_STATUS = "status";

    public static final String PSQL_POSTGRES = new StringBuilder(PGContents.EXE_PSQL)
            .append(PGContents.ARG_p).append(PGContents.PORT)
            .append(PGContents.ARG_U).append(PGContents.USER)
            .toString();

    public static final String PSQL_ADFACTORYDB = new StringBuilder(PGContents.EXE_PSQL)
            .append(PGContents.ARG_p).append(PGContents.PORT)
            .append(PGContents.ARG_U).append(PGContents.USER)
            .append(PGContents.ARG_d).append(PGContents.ADFACTORY_DB)
            .toString();

    // DBバージョンを取得する。
    public static final String SELECT_VER = new StringBuilder("\"")
            .append("SELECT t.verno FROM t_ver t WHERE sid=")
            .append(T_VER_SID)
            .append(";\"")
            .toString();

    // DBバージョンの初期値を登録する。
    public static final String INSERT_VER = new StringBuilder("\"")
            .append("INSERT INTO t_ver VALUES (")
            .append(T_VER_SID)
            .append(",'1')")
            .append(";\"")
            .toString();

    // DBバージョンを更新する。
    public static final String UPDATE_VER = new StringBuilder("\"")
            .append("UPDATE t_ver SET verno=%d WHERE sid=")
            .append(T_VER_SID)
            .append(";\"")
            .toString();

    // postgresのパスワードを変更する。
    public static final String ALTER_PASSWORD =new StringBuilder("\"")
            .append("ALTER ROLE ")
            .append(USER)
            .append(" WITH PASSWORD '%s';")
            .append("ALTER ROLE ")
            .append(ADFACTORY_USER)
            .append(" WITH PASSWORD '%s'")
            .append(";\"")
            .toString();

    public static final String SET_PGPASSWORD = "set PGPASSWORD=%s";

    // adFactoryDBのサイズを取得する。
    public static final String SELECT_ADFACTORY_DB_SIZE = new StringBuilder("\"")
            .append("SELECT pg_size_pretty(pg_database_size('")
            .append(ADFACTORY_DB)
            .append("')) db_size")
            .append(";\"")
            .toString();

    // デッドタプルの総数を取得する。
    public static final String SELECT_DEAD_TUPLE_COUNT = "\"SELECT SUM(n_dead_tup) dead_tup FROM pg_stat_user_tables;\"";

    // データベース情報のキー
    public static final String KEY_DB_INFO_VERSION = "verno";
    public static final String KEY_DB_INFO_TABLE_SIZE = "db_size";
    public static final String KEY_DB_INFO_DEAD_TUPLE = "dead_tup";

    public static final String PGPASSWORD = "@dtek1977";

    public static final String TOMEE = "TomEE";
    public static final String TOMEE_SERVICE = "TomEE_10";

    public static final String ADINTERFACE = "adInterface";
    public static final String ADINTERFACE_SERVICE = "adInterfaceService";
    
    // DBバックアップファイルの書式
    public static final String DB_BACKUP_FILE_FORMAT = new StringBuilder(PGContents.ADFACTORY_DB).append("_\\d{8}_\\d{6}.backup").toString();
}
