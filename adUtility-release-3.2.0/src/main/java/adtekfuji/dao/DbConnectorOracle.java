/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Oracle 接続ラッパークラス
 *
 * @author s-heya
 */
public class DbConnectorOracle {

    private final static Logger logger = LogManager.getLogger();
    private static DbConnectorOracle instance;
    private Connection sqlConnection = null;
    private Statement sqlStatement = null;
    private ResultSet rset = null;

    /**
     * コンストラクタ
     */
    private DbConnectorOracle() {
    }

    /**
     * インスタンスを取得する。
     *
     * @return
     */
    public static DbConnectorOracle getInstance() {
        if (Objects.isNull(instance)) {
            instance = new DbConnectorOracle();
        }
        return instance;
    }

    /**
     * データベースに接続する。
     *
     * @param host
     * @param port
     * @param user
     * @param pass
     * @param sid
     * @throws Exception
     */
    public void openDB(String host, String port, String user, String pass, String sid) throws Exception {
        try {
            logger.info(DbConnectorOracle.class.getSimpleName() + "::openDB start.");

            //URL urlDriver = new File("C:/adFactory/adProductApp/bin/lib/ojdbc7.jar").toURI().toURL();
            //URL[] urls = { urlDriver };
            //URLClassLoader loader = URLClassLoader.newInstance(urls);

            //Class.forName("oracle.jdbc.driver.OracleDriver");
            //
            //@SuppressWarnings("unchecked")
            //Class<Driver> clazz = (Class<Driver>) loader.loadClass("oracle.jdbc.driver.OracleDriver");
            //
            //Driver driver = clazz.newInstance();
            //DriverManager.registerDriver(driver);

            Class.forName("oracle.jdbc.driver.OracleDriver");

            StringBuilder url = new StringBuilder();
            url.append("jdbc:oracle:thin:@");
            url.append(host);
            url.append(":").append(port).append(":");
            url.append(sid);

            logger.debug("OJBDC: " + url.toString());
            logger.debug("OJDBC: user=" + user);
            logger.debug("OJDBC: pass=" + pass);

            // タイムアウトを10秒に設定
            DriverManager.setLoginTimeout(10);

            // コネクションを取得
            this.sqlConnection = DriverManager.getConnection(url.toString(), user, pass);

            this.sqlStatement = this.sqlConnection.createStatement();

        } catch (Exception ex) {
            if (this.sqlStatement != null) {
                this.sqlStatement.close();
                this.sqlStatement = null;
            }

            if (this.sqlConnection != null) {
                this.sqlConnection.close();
                this.sqlConnection = null;
            }
            logger.error(ex, ex);

            throw ex;
        } finally {
            logger.info(DbConnectorOracle.class.getSimpleName() + "::openDB end.");
        }
    }

    /**
     * データベースを切断する。
     */
    public void closeDB() {
        try {
            logger.info(DbConnectorOracle.class.getSimpleName() + "::closeDB start.");

            if (this.rset != null) {
                this.rset.close();
                this.rset = null;
            }

            if (this.sqlStatement != null) {
                this.sqlStatement.close();
                this.sqlStatement = null;
            }

            if (this.sqlConnection != null) {
                this.sqlConnection.close();
                this.sqlConnection = null;
            }
        } catch (SQLException ex) {
            logger.fatal(ex);
        } finally {
            logger.info(DbConnectorOracle.class.getSimpleName() + "::closeDB start.");
        }
    }

    /**
     * クエリーを実行する。
     *
     * @param query
     * @return
     * @throws SQLException
     */
    public ResultSet execQuery(String query) throws SQLException {
        return this.sqlStatement.executeQuery(query);
    }

    /**
     * PreparedStatement を取得する。
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return this.sqlConnection.prepareStatement(sql);
    }

    /**
     * コミットを実行する。
     *
     * @throws SQLException
     */
    public void commit() throws SQLException {
        this.sqlConnection.commit();
    }

    /**
     * ロールバックを実行する。
     *
     * @throws SQLException
     */
    public void rollback() throws SQLException {
        this.sqlConnection.rollback();
    }

    /**
     * 自動コミットを無効化。
     *
     * @param autoCommit
     * @throws SQLException
     */
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.sqlConnection.setAutoCommit(autoCommit);
    }
}
