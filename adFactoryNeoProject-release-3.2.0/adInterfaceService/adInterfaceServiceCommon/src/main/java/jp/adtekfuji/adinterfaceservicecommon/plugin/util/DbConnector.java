/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin.util;

import adtekfuji.clientservice.ClientServiceProperty;
import java.net.URI;
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
 *
 * @author s-heya
 */
public class DbConnector {

    private final String USER = "postgres";
    private final String PASSWORD = "@dtek1977";
    private final String SELECT_BY_KANBANNAME = "SELECT k.kanban_id FROM trn_kanban k WHERE k.kanban_name = ?";

    private static DbConnector instance;

    private final Logger logger = LogManager.getLogger();
    private Connection sqlConnection = null;
    private Statement sqlStatement = null;

    /**
     * コンストラクタ
     */
    private DbConnector() {
    }

    /**
     * インスタンスを取得する。
     *
     * @return
     */
    public static DbConnector getInstance() {
        if (Objects.isNull(instance)) {
            instance = new DbConnector();
        }
        return instance;
    }

    /**
     * データベースに接続する。
     *
     * @throws Exception
     */
    public void openDB() throws Exception {
        try {
            logger.info("openDB start.");

            Class.forName("org.postgresql.Driver");

            URI uri = URI.create(ClientServiceProperty.getServerUri());
            StringBuilder url = new StringBuilder();
            url.append("jdbc:postgresql://");
            url.append(uri.getHost());
            url.append(":15432/adFactoryDB2");

            this.sqlConnection = DriverManager.getConnection(url.toString(), USER, PASSWORD);
            //this.sqlConnection.setAutoCommit(false);
            //this.sqlStatement = this.sqlConnection.createStatement();
        } catch (ClassNotFoundException | SQLException ex) {
            if (this.sqlStatement != null) {
                this.sqlStatement.close();
                this.sqlStatement = null;
            }

            if (this.sqlConnection != null) {
                this.sqlConnection.close();
                this.sqlConnection = null;
            }

            throw ex;
        } finally {
            logger.info("openDB end.");
        }
    }

    /**
     * コネクションを閉じる。
     */
    public void closeDB() {
        try {
            logger.info("closeDB start.");

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
            logger.info("closeDB end.");
        }
    }

    /**
     * カンバンが存在するかどうかを返す。
     *
     * @param kanbanName
     * @return
     * @throws Exception
     */
    public boolean exsitKanban(String kanbanName) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        if (Objects.isNull(this.sqlConnection)) {
            this.openDB();
        }

        preparedStatement = this.sqlConnection.prepareStatement(SELECT_BY_KANBANNAME);
        preparedStatement.setString(1, kanbanName);
        resultSet = preparedStatement.executeQuery();

        return resultSet.next();
    }
}
