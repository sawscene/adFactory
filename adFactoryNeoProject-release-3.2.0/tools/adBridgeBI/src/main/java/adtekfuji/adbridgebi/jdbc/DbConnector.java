/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * データベース接続
 *
 * @author nar-nakamura
 */
public class DbConnector {

    private final Logger logger = LogManager.getLogger();

    // JDBC
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL_BASE = "jdbc:postgresql://";

    private final String serverAddress;
    private final int dbPort;
    private final String dbName;
    private final String dbUser;
    private final String dbPassword;

    private Connection connection = null;

    /**
     * コンストラクタ
     *
     * @param serverAddress サーバーアドレス
     * @param dbPort データベースのポート番号
     * @param dbName データベース名
     * @param dbUser データベースのユーザー
     * @param dbPassword データベースのパスワード
     */
    public DbConnector(String serverAddress, int dbPort, String dbName, String dbUser, String dbPassword) {
        this.serverAddress = serverAddress;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    /**
     * データベースに接続する。
     *
     * @throws Exception 
     */
    public void openDB() throws Exception {
        logger.info("openDB");
        try {
            Class.forName(JDBC_DRIVER);

            String dbUrl = new StringBuilder(DB_URL_BASE)
                    .append(this.serverAddress)
                    .append(":")
                    .append(this.dbPort)
                    .append("/")
                    .append(this.dbName)
                    .toString();

            this.connection = DriverManager.getConnection(dbUrl, this.dbUser, this.dbPassword);

        } catch (ClassNotFoundException | SQLException ex) {
            if (this.connection != null) {
                this.connection.close();
                this.connection = null;
            }
            throw ex;
        }
    }

    /**
     * データベースを切断する。
     */
    public void closeDB() {
        logger.info("closeDB");
        try {
            if (Objects.nonNull(this.connection)) {
                this.connection.close();
                this.connection = null;
            }
        } catch (SQLException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * データベース接続を取得する。
     *
     * @return データベース接続
     */
    public Connection getConnection() {
        return this.connection;
    }
}
