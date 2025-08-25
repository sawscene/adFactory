/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import jp.adtekfuji.addatabaseapp.postgres.PGContents;
import jp.adtekfuji.addatabaseapp.postgres.PostgresUtils;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Postgres管理クラス
 *
 * @author e-mori
 */
public class PostgresManager implements DatabaseManagerInterface {

    private static final Logger logger = LogManager.getLogger();

    // JDBC
    private static final String JDBC_DRIVER_FILE = "postgresql-42.5.6.jar";
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL_BASE = "jdbc:postgresql://%s:%s/%s";
    private static final String DB_USER = "user";
    private static final String DB_PASSWORD = "password";

    private Connection adFactoryDbConnection = null;
    private Statement adFactoryDbStatement = null;

    /**
     * テーブルを更新する
     *
     * @return 結果(true:成功, false:失敗)
     */
    @Override
    public Boolean updateTable() {
        Boolean result = false;
        try {
            logger.info("updateTable start.");

            // サービス起動確認
            if (!PostgresUtils.isStartPostgres()) {
                logger.warn("Postgres is not running.");
                return result;
            }

            // データベースをチェックする
            boolean exist = PostgresUtils.existDB(PGContents.PGPASSWORD);
            if (!exist) {
                // デフォルトパスワードでチェック
                exist = PostgresUtils.existDB(PGContents.USER);
                if (exist) {
                    // パスワードを変更する
                    PostgresUtils.changePassword();
                }
            }

            if (!exist) {
                // データベースを作成する
                PostgresUtils.createDB();
                PostgresUtils.changePassword();
            }

            // テーブルを更新
            result = PostgresUtils.updateTables();
            
            return result;
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("updateTable end.");
        }
        return null;
    }

    /**
     * データをエクスポートする
     *
     * @param filePath
     */
    @Override
    public void exportData(String filePath) {
    }

    /**
     * データをインポートする
     *
     * @param filePath
     */
    @Override
    public void importData(String filePath) {
    }

    @Override
    public Map<String, String> getDBInfo() {
        return PostgresUtils.getDBInfo();
    }

    /**
     * adFactoryDBのバックアップファイルを作成する。
     *
     * @param filePath バックアップファイルパス
     * @return 結果 (true:成功, false:失敗)
     */
    @Override
    public Boolean backupData(String filePath) {
        Boolean result = false;
        try {
            logger.info("backupData start.");

            // サービス起動確認
            if (!PostgresUtils.isStartPostgres()) {
                logger.warn("Postgres is not running.");
                return result;
            }

            // データベースをバックアップ
            result = PostgresUtils.backupDB(filePath);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("backupData end.");
        }
        return result;
    }

    /**
     * バックアップファイルからadFactoryDBをリストアする。
     *
     * @param filePath バックアップファイルパス
     * @return 結果 (true:成功, false:失敗)
     */
    @Override
    public Boolean restoreData(String filePath) {
        Boolean result = false;
        try {
            logger.info("restoreData start.");

            // サービス起動確認
            if (!PostgresUtils.isStartPostgres()) {
                logger.warn("Postgres is not running.");
                return result;
            }

            // データベースをリストア
            result = PostgresUtils.restoreDB(filePath);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("restoreData end.");
        }
        return result;
    }

    /**
     * データベースバージョンが古いか問い合わせる
     *
     * @return true:古い, false:同じか新しい
     */
    public Boolean isDBVerOld() {
        return PostgresUtils.isDBVerOld();
    }

    /**
     * データベースのメンテナンスを実行する
     *
     * @return 結果(true:成功, false:失敗)
     */
    public Boolean executeMaintenance() {
        Boolean result = false;
        try {
            logger.info("executeMaintenance start.");

            // サービス起動確認
            if (!PostgresUtils.isStartPostgres()) {
                logger.warn("Postgres is not running.");
                return result;
            }

            // メンテナンス実行
            result = PostgresUtils.excuteMaintenance();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("executeMaintenance end: {}", result);
        }
        return result;
    }

    /**
     * 実績のある年月リストを取得する
     *
     * @return 実績のある年月リスト
     */
    public List<String> getActualYearMonthList() {
        logger.info("getActualYearMonthList start.");
        List<String> yearMonthList = new ArrayList();
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(this.adFactoryDbStatement)) {
                this.adFactoryDbOpen();
            }

            String sql = "SELECT to_char(implement_datetime, 'yyyy/mm'::text) AS year_month FROM trn_actual_result GROUP BY year_month ORDER BY year_month;";

            resultSet = this.adFactoryDbStatement.executeQuery(sql);
            if (Objects.nonNull(resultSet)) {
                while (resultSet.next()){
                    yearMonthList.add(resultSet.getString("year_month"));
                }
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
            this.adFactoryDbClose();
            logger.info("getActualYearMonthList end.");
        }
        return yearMonthList;
    }

    /**
     * 指定した年月の実績データを取得する
     *
     * @param year 対象年
     * @param month 対象月
     * @return 実績リスト
     */
    public List<Map<String, String>> getMonthlyActualResults(int year, int month) {
        logger.info("getActualResults start.");
        List<Map<String, String>> actualResults = new ArrayList();
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(this.adFactoryDbStatement)) {
                this.adFactoryDbOpen();
            }

            int year2 = year;
            int month2 = month + 1;
            if (month2 > 12) {
                year2++;
                month2 = 1;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("SELECT");

            sb.append(" act.actual_id,");// 実績ID
            sb.append(" kanh.hierarchy_name AS kanban_hierarchy_name,");//カンバン階層
            sb.append(" kan.kanban_name,");// カンバン名
            sb.append(" kan.kanban_subname,"); // カンバンサブ名
            sb.append(" wfh.hierarchy_name AS workflow_hierarchy_name,");// 工程順階層
            sb.append(" wf.workflow_name,");// 工程順名
            sb.append(" wkh.hierarchy_name AS work_hierarchy_name,");// 工程階層
            sb.append(" wk.work_name,");// 工程名
            sb.append(" wkan.separate_work_flag,");// 追加工程
            sb.append(" porg.organization_name AS parent_organization_name,");// 親組織名
            sb.append(" porg.organization_identify AS parent_organization_identify,");// 親組織識別名
            sb.append(" org.organization_name,");// 組織名
            sb.append(" org.organization_identify,");// 組織識別名
            sb.append(" pequ.equipment_name AS parent_equipment_name,");// 親設備名
            sb.append(" pequ.equipment_identify AS parent_equipment_identify,");// 親設備識別名
            sb.append(" equ.equipment_name,");// 設備名
            sb.append(" equ.equipment_identify,");// 設備識別名
            sb.append(" act.actual_status,");// ステータス
            sb.append(" act.interrupt_reason,");// 中断理由
            sb.append(" act.delay_reason,");// 遅延理由
            sb.append(" act.implement_datetime,");// 実施時間
            sb.append(" wkan.takt_time,");// タクトタイム
            sb.append(" act.work_time,");// 作業時間
            sb.append(" act.actual_add_info");// 検査結果

            // 工程実績
            sb.append(" FROM trn_actual_result AS act");

            // カンバン・カンバン階層
            sb.append(" LEFT JOIN trn_kanban AS kan ON kan.kanban_id = act.kanban_id");
            sb.append(" LEFT JOIN con_kanban_hierarchy AS ckanh ON ckanh.kanban_id = kan.kanban_id");
            sb.append(" LEFT JOIN mst_kanban_hierarchy AS kanh ON kanh.kanban_hierarchy_id = ckanh.kanban_hierarchy_id");

            // 工程カンバン
            sb.append(" LEFT JOIN trn_work_kanban AS wkan ON wkan.work_kanban_id = act.work_kanban_id");

            // 工程順・工程順階層
            sb.append(" LEFT JOIN mst_workflow AS wf ON wf.workflow_id = act.workflow_id");
            sb.append(" LEFT JOIN con_hierarchy AS cwfh ON cwfh.hierarchy_type = 1 AND cwfh.work_workflow_id = wf.workflow_id");
            sb.append(" LEFT JOIN mst_hierarchy AS wfh ON wfh.hierarchy_id = cwfh.hierarchy_id");

            // 工程・工程階層
            sb.append(" LEFT JOIN mst_work AS wk ON wk.work_id = act.work_id");
            sb.append(" LEFT JOIN con_hierarchy AS cwkh ON cwkh.hierarchy_type = 0 AND cwkh.work_workflow_id = wk.work_id");
            sb.append(" LEFT JOIN mst_hierarchy AS wkh ON wkh.hierarchy_id = cwkh.hierarchy_id");

            // 組織・親組織
            sb.append(" LEFT JOIN mst_organization AS org ON org.organization_id = act.organization_id");
            sb.append(" LEFT JOIN mst_organization AS porg ON porg.organization_id = org.parent_organization_id");

            // 設備・親設備
            sb.append(" LEFT JOIN mst_equipment AS equ ON equ.equipment_id = act.equipment_id");
            sb.append(" LEFT JOIN mst_equipment AS pequ ON pequ.equipment_id = equ.parent_equipment_id");

            sb.append(String.format(" WHERE act.implement_datetime >= '%d-%02d-01 00:00:00' AND act.implement_datetime < '%d-%02d-01 00:00:00'", year, month, year2, month2));
            sb.append(" ORDER BY act.implement_datetime, act.actual_id;");

            List<String> columnNames = null;

            resultSet = this.adFactoryDbStatement.executeQuery(sb.toString());
            if (Objects.nonNull(resultSet)) {
                while (resultSet.next()){
                    // 初回のみ項目名を取得する
                    if (columnNames == null) {
                        columnNames = new ArrayList<>();
                        ResultSetMetaData metaData = resultSet.getMetaData();
                        for (int i = 0; i < metaData.getColumnCount(); i++) {
                            columnNames.add(metaData.getColumnName(i + 1));
                        }
                    }

                    Map<String, String> actualResult = new LinkedMap();
                    for (String columnName : columnNames) {
                        Object column = resultSet.getObject(columnName);
                        String columnValue;
                        if (Objects.nonNull(column)) {
                            columnValue = column.toString();
                        } else {
                            columnValue = "";
                        }
                        actualResult.put(columnName, columnValue);
                    }

                    actualResults.add(actualResult);
                }
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
            this.adFactoryDbClose();
            logger.info("getActualResults end.");
        }
        return actualResults;
    }

    /**
     * 指定した年月の実績データを削除する
     *
     * @param year 対象年
     * @param month 対象月
     * @return 削除結果
     */
    public boolean deleteMonthlyActualResults(int year, int month) {
        logger.info("deleteMonthlyActualResults start.");
        boolean result = false;
        try {
            if (Objects.isNull(this.adFactoryDbStatement)) {
                this.adFactoryDbOpen();
            }

            int year2 = year;
            int month2 = month + 1;
            if (month2 > 12) {
                year2++;
                month2 = 1;
            }

            String whereSql = String.format(" WHERE implement_datetime >= '%d-%02d-01 00:00:00' AND implement_datetime < '%d-%02d-01 00:00:00'", year, month, year2, month2);

            this.adFactoryDbConnection.setAutoCommit(false);// トランザクション開始

            // 工程実績を削除する。
            StringBuilder sb2 = new StringBuilder();
            sb2.append("DELETE FROM trn_actual_result");
            sb2.append(whereSql);
            this.adFactoryDbStatement.executeUpdate(sb2.toString());

            // 工程実績_yyyy_MM が存在する場合は削除する。
            String dropTableQuery = String.format("DROP TABLE IF EXISTS trn_actual_result_%d_%02d CASCADE;", year, month);
            this.adFactoryDbStatement.executeUpdate(dropTableQuery);

            this.adFactoryDbConnection.commit();// コミット

            result = true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            result = false;
        } finally {
            try {
                if (!result) {
                    this.adFactoryDbConnection.rollback();// ロールバック
                }
                this.adFactoryDbConnection.setAutoCommit(true);// トランザクション終了
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
            this.adFactoryDbClose();
            logger.info("deleteMonthlyActualResults end.");
        }
        return result;
    }

    /**
     * adFactoryDBに接続する
     *
     * @throws Exception 
     */
    private void adFactoryDbOpen() throws Exception {
        try {
            File drivarDir = new File(System.getenv("JRE_HOME"), "lib/ext");

            // JDBCドライバを読み込む。
            URL fileUrl = new File(drivarDir, JDBC_DRIVER_FILE).toURI().toURL();
            URL[] urls = { fileUrl };
            URLClassLoader loader = URLClassLoader.newInstance(urls);
            Class<Driver> cd = (Class<Driver>) loader.loadClass(JDBC_DRIVER);
            Driver driver = cd.newInstance();

            String dbUrl = String.format(DB_URL_BASE, PGContents.HOST, PGContents.PORT, PGContents.ADFACTORY_DB);

            Properties info = new Properties();
            info.put(DB_USER, PGContents.USER);
            info.put(DB_PASSWORD, PGContents.PGPASSWORD);

            this.adFactoryDbConnection = driver.connect(dbUrl, info);
            this.adFactoryDbConnection.setAutoCommit(false);
            this.adFactoryDbStatement = this.adFactoryDbConnection.createStatement();
        } catch (SQLException ex) {
            this.adFactoryDbClose();
            throw ex;
        }
    }

    /**
     * adFactoryDBを切断する
     */
    private void adFactoryDbClose() {
        try {
            if (this.adFactoryDbStatement != null) {
                this.adFactoryDbStatement.close();
                this.adFactoryDbStatement = null;
            }
            if (this.adFactoryDbConnection != null) {
                    this.adFactoryDbConnection.close();
                    this.adFactoryDbConnection = null;
            }
        } catch (SQLException ex) {
            logger.fatal(ex, ex);
        }
    }
}
