/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin.util;

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
 * Oracleデータベースコネクター
 *
 * @author ka.makihara
 */
public class DbConnectorOrcl {

    private static DbConnectorOrcl instance;

    private final Logger logger = LogManager.getLogger();
    private Connection sqlConnection = null;
    private Statement sqlStatement = null;
    private ResultSet rset = null;

    /**
     * コンストラクタ
     */
    public DbConnectorOrcl() {
    }

    /**
     * インスタンスを取得する。
     *
     * @return
     */
    public static DbConnectorOrcl getInstance() {
        if (Objects.isNull(instance)) {
            instance = new DbConnectorOrcl();
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
            logger.info("openDB(Oracle) start.");

            Class.forName("oracle.jdbc.driver.OracleDriver");

            //URI uri = URI.create(ClientServiceProperty.getServerUri());
            StringBuilder url = new StringBuilder();
            url.append("jdbc:oracle:thin:@");
            url.append(host);
            url.append(":").append(port).append(":");
            url.append(sid);

            logger.debug("OJBDC:" + url.toString());
            logger.debug("OJDBC:user=" + user);
            logger.debug("OJDBC:pass=" + pass);

            this.sqlConnection = DriverManager.getConnection(url.toString(), user, pass);

            this.sqlStatement = this.sqlConnection.createStatement();
        } catch (ClassNotFoundException | SQLException ex) {
            if (this.sqlStatement != null) {
                this.sqlStatement.close();
                this.sqlStatement = null;
            }

            if (this.sqlConnection != null) {
                this.sqlConnection.close();
                this.sqlConnection = null;
            }
            logger.info("OJDBC:connection error");

            throw ex;
        } finally {
            logger.info("openDB(Oracle) end.");
        }
    }

    /**
     * データベースを切断する。
     */
    public void closeDB() {
        try {
            logger.info("closeDB(Oracle) start.");

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
            logger.fatal(ex, ex);
        } finally {
            logger.info("closeDB(Oracle) end.");
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
