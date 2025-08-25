/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model;

import adtekfuji.utility.StringUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.kanban.TraceabilityEntity;
import jp.adtekfuji.adFactory.entity.search.TraceabilitySearchCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.PGobject;

/**
 * トレーサビリティDBアクセス(JDBC)
 *
 * @author nar-nakamura
 */
public class TraceabilityJdbc {

    private final Logger logger = LogManager.getLogger();

    // JDBC
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL_BASE = "jdbc:postgresql://";
    private static final String DB_NAME = "adFactoryTraceDB";
    private static final int DB_PORT = 15432;
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "@dtek1977";

    // SQL文
    // カンバンIDでトレーサビリティ一覧を取得する。
    private static final String SQL_SELECT_KANBAN_TRACE = "SELECT * FROM trn_traceability WHERE kanban_id = ? ORDER BY work_kanban_id, trace_order, implement_datetime";
    private static final String SQL_SELECT_KANBAN_TRACE_LATEST = "SELECT t.* FROM trn_traceability t LEFT JOIN (SELECT t1.work_kanban_id, MAX(t1.implement_datetime) last_dt FROM trn_traceability t1 WHERE t1.kanban_id = ? GROUP BY t1.work_kanban_id ) t2 ON t2.work_kanban_id = t.work_kanban_id AND t2.last_dt = t.implement_datetime WHERE t.kanban_id = ? AND t2.last_dt IS NOT NULL ORDER BY t.work_kanban_id, t.trace_order, t.actual_id";
    // カンバンIDでトレーサビリティ一覧を削除する。
    private static final String SQL_DELETE_KANBAN_TRACE = "DELETE FROM trn_traceability WHERE kanban_id = ?";
    // 工程カンバンIDでトレーサビリティの最終フラグを全てfalseにする。
    private static final String SQL_UPDATE_WORKKANBAN_LATEST_FALSE = "UPDATE trn_traceability SET latest_flag = false WHERE work_kanban_id = ?";
    // トレーサビリティを追加する。
    private static final String SQL_INSERT_TRACE = "INSERT INTO trn_traceability (kanban_id, kanban_name, model_name, workflow_name, workflow_rev, work_kanban_id, actual_id, trace_name, trace_order, lower_limit, upper_limit, trace_value, trace_confirm, equipment_name, organization_name, implement_datetime, trace_tag, trace_props, latest_flag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true)";
    // 工程カンバンIDでトレーサビリティ一覧を取得する。
    private static final String SQL_SELECT_WORKKANBAN_TRACE = "SELECT * FROM trn_traceability WHERE work_kanban_id = ? ORDER BY trace_order, implement_datetime";
    private static final String SQL_SELECT_WORKKANBAN_TRACE_LATEST = "SELECT t.* FROM trn_traceability t WHERE t.work_kanban_id = ? AND implement_datetime = (SELECT MAX(t1.implement_datetime) FROM trn_traceability t1 WHERE t1.work_kanban_id = ?) ORDER BY t.trace_order, t.actual_id";

    private static final String SQL_SELECT_TRACE = "SELECT * FROM trn_traceability";
    private static final String SQL_WHERE = " WHERE";
    private static final String SQL_AND = " AND";
    private static final String SQL_IS_LATEST = " latest_flag = true";
    private static final String SQL_LIKE_ALL_KANBAN_NAME = " LOWER(kanban_name) LIKE ALL (string_to_array(?, ','))";
    private static final String SQL_LIKE_ALL_MODEL_NAME = " LOWER(model_name) LIKE ALL (string_to_array(?, ','))";
    private static final String SQL_ORDER = " ORDER BY kanban_name, model_name, workflow_name, trace_order, implement_datetime";

    private static final String SQL_SELECT = "SELECT ";
    private static final String SQL_GROUP = " GROUP BY ";
    private static final String SQL_FROM_TRACE = " FROM trn_traceability";

    private static final String SQL_TRACE_KANBAN_GROUP = "kanban_id, kanban_name, model_name, workflow_name";
    private static final String SQL_TRACE_KANBAN_ORDER = " ORDER BY kanban_name, model_name, workflow_name";

    private final String serverAddress;

    private Connection sqlConnection = null;

    private static TraceabilityJdbc instance;

    /**
     * コンストラクタ
     */
    public TraceabilityJdbc() {
        this.serverAddress = "localhost";
    }

    /**
     * インスタンスを取得する。
     *
     * @return 
     */
    public static TraceabilityJdbc getInstance() {
        if (Objects.isNull(instance)) {
            instance = new TraceabilityJdbc();
        }
        return instance;
    }

    /**
     * データベースに接続する。
     *
     * @throws Exception 
     */
    private void openDB() throws Exception {
        logger.info("openDB");
        try {
            Class.forName(JDBC_DRIVER);

            String dbUrl = new StringBuilder(DB_URL_BASE)
                    .append(serverAddress)
                    .append(":")
                    .append(DB_PORT)
                    .append("/")
                    .append(DB_NAME)
                    .toString();

            this.sqlConnection = DriverManager.getConnection(dbUrl, DB_USER, DB_PASSWORD);

        } catch (ClassNotFoundException | SQLException ex) {
            if (this.sqlConnection != null) {
                this.sqlConnection.close();
                this.sqlConnection = null;
            }
            throw ex;
        }
    }

    /**
     * データベースを切断する。
     */
    private void closeDB() {
        logger.info("closeDB");
        try {
            if (Objects.nonNull(this.sqlConnection)) {
                this.sqlConnection.close();
                this.sqlConnection = null;
            }
        } catch (SQLException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * トレーサビリティを追加する。
     *
     * @param actualId 工程実績ID
     * @param traces トレーサビリティ一覧
     * @return 
     */
    public boolean addTraceability(long actualId, List<TraceabilityEntity> traces) {
        logger.info("addTraceability");
        boolean result = false;
        PreparedStatement ps = null;
        try {
            if (Objects.isNull(this.sqlConnection)) {
                this.openDB();
            }

            this.sqlConnection.setAutoCommit(false);// 自動コミット解除 (トランザクション開始)

            // 該当する工程カンバンの、過去のトレーサビリティの最終フラグを全てfalseにする。
            ps = this.sqlConnection.prepareStatement(SQL_UPDATE_WORKKANBAN_LATEST_FALSE);
            ps.clearParameters();
            ps.setLong(1, traces.get(0).getWorkKanbanId());// 工程カンバンID
            ps.executeUpdate();

            // トレーサビリティを追加する。
            for (TraceabilityEntity trace : traces) {
                ps = this.sqlConnection.prepareStatement(SQL_INSERT_TRACE);
                ps.clearParameters();

                java.sql.Timestamp implementDatetime = new java.sql.Timestamp(trace.getImplementDatetime().getTime());

                PGobject traceProps = new PGobject();
                traceProps.setType("json");
                traceProps.setValue(trace.getTraceProps());

                ps.setLong(1, trace.getKanbanId());// カンバンID
                ps.setString(2, trace.getKanbanName());// カンバン名
                ps.setString(3, trace.getModelName());// モデル名
                ps.setString(4, trace.getWorkflowName());// 工程順名
                ps.setInt(5, trace.getWorkflowRev());// 版数
                ps.setLong(6, trace.getWorkKanbanId());// 工程カンバンID
                ps.setLong(7, actualId);// 工程実績ID
                ps.setString(8, trace.getTraceName());// 項目名
                ps.setInt(9, trace.getTraceOrder());// 順
                ps.setDouble(10, trace.getLowerLimit());// 規格下限
                ps.setDouble(11, trace.getUpperLimit());// 規格上限
                ps.setString(12, trace.getTraceValue());// 値
                ps.setBoolean(13, trace.getTraceConfirm());// 確認
                ps.setString(14, trace.getEquipmentName());// 設備名
                ps.setString(15, trace.getOrganizationName());// 組織名
                ps.setTimestamp(16, implementDatetime);// 作業日時
                ps.setString(17, trace.getTraceTag());// タグ
                ps.setObject(18, traceProps);// 追加トレーサビリティ一覧

                ps.executeUpdate();
            }

            this.sqlConnection.commit();// コミット

            result = true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            try {
                this.sqlConnection.rollback();// ロールバック
            } catch (SQLException sqlEx) {
                logger.fatal(sqlEx, sqlEx);
            }
        } finally {
            try {
                if (Objects.nonNull(ps)) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
            this.closeDB();
        }
        return result;
    }

    /**
     * カンバンIDを指定して、トレーサビリティ一覧を取得する。
     *
     * @param kanbanId カンバンID
     * @param isAll 全て取得？(true:全て, false:最新のみ)
     * @return トレーサビリティ一覧
     */
    public List<TraceabilityEntity> getKanbanTraceability(long kanbanId, boolean isAll) {
        logger.info("getKanbanTraceability: {}, {}", kanbanId, isAll);
        List<TraceabilityEntity> traces = new ArrayList();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(this.sqlConnection)) {
                this.openDB();
            }

            if (isAll) {
                ps = this.sqlConnection.prepareStatement(SQL_SELECT_KANBAN_TRACE);
                ps.setLong(1, kanbanId);
            } else {
                ps = this.sqlConnection.prepareStatement(SQL_SELECT_KANBAN_TRACE_LATEST);
                ps.setLong(1, kanbanId);
                ps.setLong(2, kanbanId);
            }
            resultSet = ps.executeQuery();

            if (Objects.nonNull(resultSet)) {
                while (resultSet.next()) {
                    TraceabilityEntity trace = this.resultSetToEntity(resultSet);
                    traces.add(trace);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            try {
                if (Objects.nonNull(resultSet)) {
                    resultSet.close();
                    resultSet = null;
                }
                if (Objects.nonNull(ps)) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
            this.closeDB();
        }
        return traces;
    }

    /**
     * カンバンIDを指定して、トレーサビリティを削除する。
     *
     * @param kanbanId カンバンID
     * @return 
     */
    public boolean deleteKanbanTraceability(long kanbanId) {
        logger.info("deleteKanbanTraceability: {}", kanbanId);
        boolean result = false;
        PreparedStatement ps = null;
        try {
            if (Objects.isNull(this.sqlConnection)) {
                this.openDB();
            }

            ps = this.sqlConnection.prepareStatement(SQL_DELETE_KANBAN_TRACE);
            ps.setLong(1, kanbanId);

            this.sqlConnection.setAutoCommit(false);// 自動コミット解除 (トランザクション開始)

            ps.executeUpdate();

            this.sqlConnection.commit();// コミット

            result = true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            try {
                this.sqlConnection.rollback();// ロールバック
            } catch (SQLException sqlEx) {
                logger.fatal(sqlEx, sqlEx);
            }
        } finally {
            try {
                if (Objects.nonNull(ps)) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
            this.closeDB();
        }
        return result;
    }

    /**
     * 工程カンバンIDを指定して、トレーサビリティ一覧を取得する。
     *
     * @param workKanbanId 工程カンバンID
     * @param isAll 全て取得？(true:全て, false:最新のみ)
     * @return トレーサビリティ一覧
     */
    public List<TraceabilityEntity> getWorkKanbanTraceability(long workKanbanId, boolean isAll) {
        logger.info("getWorkKanbanTraceability: {}, {}", workKanbanId, isAll);
        List<TraceabilityEntity> traces = new ArrayList();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(this.sqlConnection)) {
                this.openDB();
            }

            if (isAll) {
                ps = this.sqlConnection.prepareStatement(SQL_SELECT_WORKKANBAN_TRACE);
                ps.setLong(1, workKanbanId);
            } else {
                ps = this.sqlConnection.prepareStatement(SQL_SELECT_WORKKANBAN_TRACE_LATEST);
                ps.setLong(1, workKanbanId);
                ps.setLong(2, workKanbanId);
            }
            resultSet = ps.executeQuery();

            if (Objects.nonNull(resultSet)) {
                while (resultSet.next()) {
                    TraceabilityEntity trace = this.resultSetToEntity(resultSet);
                    traces.add(trace);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            try {
                if (Objects.nonNull(resultSet)) {
                    resultSet.close();
                    resultSet = null;
                }
                if (Objects.nonNull(ps)) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
            this.closeDB();
        }
        return traces;
    }

    /**
     * 検索条件を指定して、トレーサビリティ一覧を取得する。
     *
     * @param condition 検索条件
     * @return トレーサビリティ一覧
     */
    public List<TraceabilityEntity> searchTraceability(TraceabilitySearchCondition condition) {
        logger.info("searchTraceability");
        List<TraceabilityEntity> traces = new ArrayList();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(this.sqlConnection)) {
                this.openDB();
            }

            // カンバン名の検索パラメータ
            String kanbanParam = this.createLikeParam(condition.getKanbanName());
            // モデル名の検索パラメータ
            String modelParam = this.createLikeParam(condition.getModelName());

            List<String> params = new ArrayList();

            StringBuilder sql = new StringBuilder(SQL_SELECT_TRACE);

            StringBuilder where = new StringBuilder();

            if (Objects.isNull(condition.getIsAll()) || !condition.getIsAll()) {
                where.append(SQL_WHERE);
                where.append(SQL_IS_LATEST);
            }

            if (Objects.nonNull(kanbanParam)) {
                if (where.length() > 0) {
                    where.append(SQL_AND);
                } else {
                    where.append(SQL_WHERE);
                }
                where.append(SQL_LIKE_ALL_KANBAN_NAME);
                params.add(kanbanParam);
            }

            if (Objects.nonNull(modelParam)) {
                if (where.length() > 0) {
                    where.append(SQL_AND);
                } else {
                    where.append(SQL_WHERE);
                }
                where.append(SQL_LIKE_ALL_MODEL_NAME);
                params.add(modelParam);
            }

            sql.append(where);

            sql.append(SQL_ORDER);
            
            ps = this.sqlConnection.prepareStatement(sql.toString());

            // パラメータをセット
            int paramNo = 1;
            for (String param : params) {
                ps.setString(paramNo, param);
                paramNo++;
            }

            resultSet = ps.executeQuery();

            if (Objects.nonNull(resultSet)) {
                while (resultSet.next()) {
                    TraceabilityEntity trace = this.resultSetToEntity(resultSet);
                    traces.add(trace);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            try {
                if (Objects.nonNull(resultSet)) {
                    resultSet.close();
                    resultSet = null;
                }
                if (Objects.nonNull(ps)) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
            this.closeDB();
        }
        return traces;
    }

    /**
     * 検索条件を指定して、カンバンでグルーピングしたトレーサビリティ一覧を取得する。
     *
     * @param condition 検索条件
     * @return カンバンでグルーピングしたトレーサビリティ一覧
     */
    public List<TraceabilityEntity> searchKanban(TraceabilitySearchCondition condition) {
        logger.info("searchKanban");
        List<TraceabilityEntity> traces = new ArrayList();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(this.sqlConnection)) {
                this.openDB();
            }

            // カンバン名の検索パラメータ
            String kanbanParam = this.createLikeParam(condition.getKanbanName());
            // モデル名の検索パラメータ
            String modelParam = this.createLikeParam(condition.getModelName());

            List<String> params = new ArrayList();

            StringBuilder sql = new StringBuilder(SQL_SELECT)
                    .append(SQL_TRACE_KANBAN_GROUP)
                    .append(SQL_FROM_TRACE);

            StringBuilder where = new StringBuilder();

            if (Objects.isNull(condition.getIsAll()) || !condition.getIsAll()) {
                where.append(SQL_WHERE);
                where.append(SQL_IS_LATEST);
            }

            if (Objects.nonNull(kanbanParam)) {
                if (where.length() > 0) {
                    where.append(SQL_AND);
                } else {
                    where.append(SQL_WHERE);
                }
                where.append(SQL_LIKE_ALL_KANBAN_NAME);
                params.add(kanbanParam);
            }

            if (Objects.nonNull(modelParam)) {
                if (where.length() > 0) {
                    where.append(SQL_AND);
                } else {
                    where.append(SQL_WHERE);
                }
                where.append(SQL_LIKE_ALL_MODEL_NAME);
                params.add(modelParam);
            }

            sql.append(where);

            sql.append(SQL_GROUP);
            sql.append(SQL_TRACE_KANBAN_GROUP);
            sql.append(SQL_TRACE_KANBAN_ORDER);
            
            ps = this.sqlConnection.prepareStatement(sql.toString());

            // パラメータをセット
            int paramNo = 1;
            for (String param : params) {
                ps.setString(paramNo, param);
                paramNo++;
            }

            resultSet = ps.executeQuery();

            if (Objects.nonNull(resultSet)) {
                while (resultSet.next()) {
                    TraceabilityEntity trace = this.resultSetToEntity(resultSet);
                    traces.add(trace);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            try {
                if (Objects.nonNull(resultSet)) {
                    resultSet.close();
                    resultSet = null;
                }
                if (Objects.nonNull(ps)) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
            this.closeDB();
        }
        return traces;
    }

    /**
     * ResultSetをTraceabilityEntityに変換する。
     *
     * @param resultSet ResultSet
     * @return TraceabilityEntity
     * @throws Exception 
     */
    private TraceabilityEntity resultSetToEntity(ResultSet resultSet) throws Exception {
        TraceabilityEntity trace = new TraceabilityEntity();

        trace.setKanbanId(getLongColumn(resultSet, "kanban_id"));// カンバンID
        trace.setKanbanName(getStringColumn(resultSet, "kanban_name"));// カンバン名
        trace.setModelName(getStringColumn(resultSet, "model_name"));// モデル名
        trace.setWorkflowName(getStringColumn(resultSet, "workflow_name"));// 工程順名
        trace.setWorkflowRev(getIntColumn(resultSet, "workflow_rev"));// 版数
        trace.setWorkKanbanId(getLongColumn(resultSet, "work_kanban_id"));// 工程カンバンID
        trace.setWorkKanbanId(getLongColumn(resultSet, "actual_id"));// 工程実績ID
        trace.setTraceName(getStringColumn(resultSet, "trace_name"));// 項目名
        trace.setTraceOrder(getIntColumn(resultSet, "trace_order"));// 順
        trace.setLowerLimit(getDoubleColumn(resultSet, "lower_limit"));// 規格下限
        trace.setUpperLimit(getDoubleColumn(resultSet, "upper_limit"));// 規格上限
        trace.setTraceValue(getStringColumn(resultSet, "trace_value"));// 値
        trace.setTraceConfirm(getBooleanColumn(resultSet, "trace_confirm"));// 確認
        trace.setEquipmentName(getStringColumn(resultSet, "equipment_name"));// 設備名
        trace.setOrganizationName(getStringColumn(resultSet, "organization_name"));// 組織名
        trace.setImplementDatetime(getDateColumn(resultSet, "implement_datetime"));// 作業日時
        trace.setTraceTag(getStringColumn(resultSet, "trace_tag"));// タグ
        trace.setTraceProps(getStringColumn(resultSet, "trace_props"));// 追加トレーサビリティ一覧

        return trace;
    }

    /**
     * レコードから指定カラムの値を取得する。
     *
     * @param resultSet レコード
     * @param columnName カラム名
     * @return 該当カラムの値 (結果セットに該当カラムが無い、または型が異なる場合はnull)
     */
    private Integer getIntColumn(ResultSet resultSet, String columnName) {
        try {
            return resultSet.getInt(columnName);
        } catch (SQLException ex) {
            // カラムなし、または型違い
            return null;
        }
    }

    /**
     * レコードから指定カラムの値を取得する。
     *
     * @param resultSet レコード
     * @param columnName カラム名
     * @return 該当カラムの値 (結果セットに該当カラムが無い、または型が異なる場合はnull)
     */
    private Long getLongColumn(ResultSet resultSet, String columnName) {
        try {
            return resultSet.getLong(columnName);
        } catch (SQLException ex) {
            // カラムなし、または型違い
            return null;
        }
    }

    /**
     * レコードから指定カラムの値を取得する。
     *
     * @param resultSet レコード
     * @param columnName カラム名
     * @return 該当カラムの値 (結果セットに該当カラムが無い、または型が異なる場合はnull)
     */
    private Double getDoubleColumn(ResultSet resultSet, String columnName) {
        try {
            return resultSet.getDouble(columnName);
        } catch (SQLException ex) {
            // カラムなし、または型違い
            return null;
        }
    }

    /**
     * レコードから指定カラムの値を取得する。
     *
     * @param resultSet レコード
     * @param columnName カラム名
     * @return 該当カラムの値 (結果セットに該当カラムが無い、または型が異なる場合はnull)
     */
    private String getStringColumn(ResultSet resultSet, String columnName) {
        try {
            return resultSet.getString(columnName);
        } catch (SQLException ex) {
            // カラムなし、または型違い
            return null;
        }
    }

    /**
     * レコードから指定カラムの値を取得する。
     *
     * @param resultSet レコード
     * @param columnName カラム名
     * @return 該当カラムの値 (結果セットに該当カラムが無い、または型が異なる場合はnull)
     */
    private Boolean getBooleanColumn(ResultSet resultSet, String columnName) {
        try {
            return resultSet.getBoolean(columnName);
        } catch (SQLException ex) {
            // カラムなし、または型違い
            return null;
        }
    }

    /**
     * レコードから指定カラムの値を取得する。
     *
     * @param resultSet レコード
     * @param columnName カラム名
     * @return 該当カラムの値 (結果セットに該当カラムが無い、または型が異なる場合はnull)
     */
    private java.sql.Date getDateColumn(ResultSet resultSet, String columnName) {
        try {
            return resultSet.getDate(columnName);
        } catch (SQLException ex) {
            // カラムなし、または型違い
            return null;
        }
    }

    /**
     * 検索文字列から検索パラメータを生成する。
     *
     * @param searchString 検索文字列
     * @return 検索パラメータ
     */
    private String createLikeParam(String searchString) {
        if (Objects.isNull(searchString) || searchString.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;

        for (String item : searchString.split(" ")) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",");
            }
            sb.append("%").append(StringUtils.toLowerCase(item)).append("%");
        }

        return sb.toString();
    }
}
