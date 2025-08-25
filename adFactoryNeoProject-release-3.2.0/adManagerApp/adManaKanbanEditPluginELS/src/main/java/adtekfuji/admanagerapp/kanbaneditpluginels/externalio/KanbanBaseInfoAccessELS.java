/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.externalio;

import adtekfuji.admanagerapp.kanbaneditpluginels.entity.KanbanBaseInfoPropertyEntity;
import adtekfuji.admanagerapp.kanbaneditpluginels.entity.KanbanBaseInfoEntity;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
public class KanbanBaseInfoAccessELS {

    private final Logger logger = LogManager.getLogger();
    private final Properties properties = AdProperty.getProperties();

    // JDBC
    private static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String DB_URL_BASE = "jdbc:sqlserver://%s;instanceName=%s;databaseName=%s;";
    private static final String DB_USER = "TPICSVIEWER";
    private static final String DB_PASSWORD = "TPICSVIEWER";

    // SQL文
    private static final String SELECT_SQL = "SELECT XKJOB.HINMEI, XKJOB.KIKAKU_KATASIKI, XKJOB.SYANAI_ZUBAN, XKJOB.SYANAI_COMMENT, XKJOB.KVOL, KBUMON.NAME AS KBUMONAME FROM XKJOB LEFT JOIN XSECT AS KBUMON ON KBUMON.BUMO = XKJOB.KBUMO WHERE XKJOB.PORDER = :PORDER AND XKJOB.KBAN = :KBAN;";

    // 設定 項目
    private static final String DB_SERVER_ADDRESS = "ELS_DB_Server";
    private static final String DB_INSTANCE_NAME = "ELS_DB_Instance";
    private static final String DB_DATABESE_NAME = "ELS_DB_Database";
    // 設定 初期値
    private static final String DB_SERVER_ADDRESS_DEF = "localhost";
    private static final String DB_INSTANCE_NAME_DEF = "MSSQLSERVER";
    private static final String DB_DATABESE_NAME_DEF = "TxEDEC";

    private final String serverAddress;
    private final String instanceName;
    private final String databaseName;

    private Connection sqlConnection = null;
    private Statement sqlStatement = null;

    /**
     * カンバン基本情報アクセスクラス
     *
     */
    public KanbanBaseInfoAccessELS() {
        // SQLサーバーのアドレス
        if (!properties.containsKey(DB_SERVER_ADDRESS)) {
            properties.setProperty(DB_SERVER_ADDRESS, DB_SERVER_ADDRESS_DEF);
        }
        this.serverAddress = properties.getProperty(DB_SERVER_ADDRESS);

        // SQLサーバーのインスタンス名
        if (!properties.containsKey(DB_INSTANCE_NAME)) {
            properties.setProperty(DB_INSTANCE_NAME, DB_INSTANCE_NAME_DEF);
        }
        this.instanceName = properties.getProperty(DB_INSTANCE_NAME);

        // SQLサーバーのデータベース名
        if (!properties.containsKey(DB_DATABESE_NAME)) {
            properties.setProperty(DB_DATABESE_NAME, DB_DATABESE_NAME_DEF);
        }
        this.databaseName = properties.getProperty(DB_DATABESE_NAME);
    }

    /**
     * カンバン基本情報の取得機能が使用可能か？
     *
     *      ※SQLサーバーに接続・切断ができたら、カンバン基本情報の取得機能が使用可能とする。
     * @return 結果 (true:使用可能, false:使用不可) 
     */
    public boolean IsAvailable() {
        logger.info("IsAvailable start.");
        boolean ret = false;
        try {
            if (Objects.isNull(this.sqlStatement)) {
                this.openDB();
            }
            ret = true;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.closeDB();
            logger.info("IsAvailable end: {}", ret);
        }
        return ret;
    }

    /**
     * 作業指示コードでカンバン基本情報を取得する。
     *
     *      ※SQLサーバーに接続して、工程計画を取得してカンバン基本情報を作成する。
     * @param instructionCode 作業指示コード (注番 + '-' + 工程番号)
     * @return
     */
    public KanbanBaseInfoEntity GetKanbanBaseInfo(String instructionCode) {
        logger.info("GetKanbanBaseInfo start: {}", instructionCode);
        KanbanBaseInfoEntity kanbanBase = null;
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(this.sqlStatement)) {
                this.openDB();
            }

            // 作業指示書コードを注番と工程番号に分解する。
            int sepPos = instructionCode.indexOf("-");
            if (sepPos < 0) {
                return kanbanBase;
            }

            String porder = instructionCode.substring(0, sepPos);// 注番
            String kban = instructionCode.substring(sepPos + 1);// 工程番号

            String sql = SELECT_SQL.replaceAll(":PORDER", "'" + porder + "'").replaceAll(":KBAN", kban);

            resultSet = this.sqlStatement.executeQuery(sql);
            if (Objects.nonNull(resultSet)) {
                resultSet.next();

                String hinmei = resultSet.getString("HINMEI");// 品名
                String kikakuKatasiki = resultSet.getString("KIKAKU_KATASIKI");// 規格・型式
                String syanaiZuban = resultSet.getString("SYANAI_ZUBAN");// 図番
                String syanaiComment = resultSet.getString("SYANAI_COMMENT");// 社内コメント
                String kvol = resultSet.getString("KVOL");// 生産数
                String kbumonName = resultSet.getString("KBUMONAME");// 部門名

                // カンバン基本情報
                kanbanBase = new KanbanBaseInfoEntity();

                // 工程順名 (規格・型式 - 部門名)
                kanbanBase.setWorkflowName(kikakuKatasiki + "-" + kbumonName);
                // ロット数量 (生産数)
                kanbanBase.setLotQuantity(StringUtils.parseInteger(kvol));

                // プロパティ
                List<KanbanBaseInfoPropertyEntity> props = new ArrayList<>();
                // プロパティ - 注番
                props.add(new KanbanBaseInfoPropertyEntity("注番", CustomPropertyTypeEnum.TYPE_STRING, porder, 1));
                // プロパティ - 品名
                props.add(new KanbanBaseInfoPropertyEntity("品名", CustomPropertyTypeEnum.TYPE_STRING, hinmei, 2));
                // プロパティ - 規格・型式
                props.add(new KanbanBaseInfoPropertyEntity("規格・型式", CustomPropertyTypeEnum.TYPE_STRING, kikakuKatasiki, 3));
                // プロパティ - 図番
                props.add(new KanbanBaseInfoPropertyEntity("図番", CustomPropertyTypeEnum.TYPE_STRING, syanaiZuban, 4));
                // プロパティ - 社内コメント
                props.add(new KanbanBaseInfoPropertyEntity("社内コメント", CustomPropertyTypeEnum.TYPE_STRING, syanaiComment, 5));
                // プロパティ - 工程番号
                props.add(new KanbanBaseInfoPropertyEntity("工程番号", CustomPropertyTypeEnum.TYPE_STRING, kban, 6));

                kanbanBase.setPropertyCollection(props);
            }
            resultSet.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    resultSet = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
            this.closeDB();
            logger.info("GetKanbanBaseInfo end: {}", kanbanBase);
        }
        return kanbanBase;
    }

    /**
     * データベース接続
     *
     * @throws Exception 
     */
    private void openDB() throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            String dbUrl = String.format(DB_URL_BASE, serverAddress, instanceName, databaseName);
            this.sqlConnection = DriverManager.getConnection(dbUrl, DB_USER, DB_PASSWORD);
            this.sqlConnection.setAutoCommit(false);
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
            throw ex;
        }
    }

    /**
     * データベース切断
     *
     */
    private void closeDB() {
        try {
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
        }
    }
}
