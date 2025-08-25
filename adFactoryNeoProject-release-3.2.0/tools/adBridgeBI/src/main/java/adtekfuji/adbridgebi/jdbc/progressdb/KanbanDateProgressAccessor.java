/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.jdbc.progressdb;

import adtekfuji.adbridgebi.entity.KanbanDateProgressEntity;
import adtekfuji.adbridgebi.jdbc.DbConnector;
import adtekfuji.adbridgebi.jdbc.DbUtils;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン進捗情報(日付基準)テーブルの操作
 *
 * @author nar-nakamura
 */
public class KanbanDateProgressAccessor {

    private final Logger logger = LogManager.getLogger();

    private final DbConnector progressDb = ProgressDbConnector.getInstance();

    // テーブル名
    private final String tableName = "trn_kanban_date_progress";
    // カラム名
    private final String columnNames[] = {
        "progress_no", "kanban_name",
        "work_name_1", "status_1",
        "work_name_2", "status_2",
        "work_name_3", "status_3",
        "work_name_4", "status_4",
        "work_name_5", "status_5",
        "work_name_6", "status_6",
        "work_name_7", "status_7",
        "work_name_8", "status_8",
        "work_name_9", "status_9",
        "work_name_10", "status_10",
        "work_name_11", "status_11",
        "work_name_12", "status_12",
        "work_name_13", "status_13",
        "work_name_14", "status_14",
        "work_name_15", "status_15",
        "work_name_16", "status_16",
        "work_name_17", "status_17",
        "work_name_18", "status_18",
        "work_name_19", "status_19",
        "work_name_20", "status_20",
        "kanban_status", "model_name",
        "info_1", "info_2",
    };

    // カンバン進捗情報(日付基準)を追加する。
    private final String SQL_INSERT;
    // 全てのカンバン進捗情報(日付基準)を削除する。
    private final String SQL_DELETE_ALL;
    // 指定したprogressNoのカンバン進捗情報(日付基準)を削除する。
    private final String SQL_DELETE_PROGRESS;
    // progressNoを更新する。
    private final String SQL_UPDATE_PROGRESS_NO;

    private final int MAX_SIZE = 100;

    /**
     * コンストラクタ
     */
    public KanbanDateProgressAccessor() {
        String columnValues[] = new String[columnNames.length];
        Arrays.fill(columnValues, "?");

        // カンバン進捗情報(日付基準)を追加する。
        SQL_INSERT = new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(String.join(",", columnNames))
                .append(") VALUES (")
                .append(String.join(",", columnValues))
                .append(")")
                .toString();

        // 全てのカンバン進捗情報(日付基準)を削除する。
        SQL_DELETE_ALL = new StringBuilder("DELETE FROM ")
                .append(tableName)
                .toString();

        // 指定したprogressNoのカンバン進捗情報(日付基準)を削除する。
        SQL_DELETE_PROGRESS = new StringBuilder("DELETE FROM ")
                .append(tableName)
                .append(" WHERE progress_no IN :progressNos")
                .toString();

        // progressNoを更新する。
        SQL_UPDATE_PROGRESS_NO = new StringBuilder("UPDATE ")
                .append(tableName)
                .append(" SET ")
                .append("progress_no = ?")
                .append(" WHERE progress_no = ?")
                .toString();
    }

    /**
     * カンバン進捗情報(日付基準)を追加する。
     *
     * @param entities カンバン進捗情報(日付基準)
     * @return 追加数
     * @throws Exception
     */
    public int add(List<KanbanDateProgressEntity> entities) throws Exception {
        logger.info("add: size={}", entities.size());
        int result = 0;
        PreparedStatement ps = null;
        try {
            // 更新対象カンバン(progressNo)のカンバン進捗情報(日付基準)をすべて削除する。(MAX_SIZE件数ずつ削除)
            List<String> progressNos = entities.stream().map(p -> p.getProgressNo()).distinct().collect(Collectors.toList());
            this.remove(progressNos);

            // カンバン進捗情報(日付基準)を追加する。
            for (KanbanDateProgressEntity entity : entities) {
                ps = progressDb.getConnection().prepareStatement(SQL_INSERT);
                ps.clearParameters();

                ps.setString(1, entity.getProgressNo());// No
                ps.setString(2, entity.getKanbanName());// カンバン名

                int columnNo = 3;
                for (int i = 0; i < KanbanDateProgressEntity.MAX_WORK; i++) {
                    ps.setString(columnNo, entity.getWorkName(i));// 工程名
                    columnNo++;
                    ps.setString(columnNo, entity.getStatus(i));// 工程ステータス
                    columnNo++;
                }

                ps.setString(columnNo, entity.getKanbanStatus());// 中日程ステータス
                columnNo++;
                ps.setString(columnNo, entity.getModelName());  // 機種
                columnNo++;
                ps.setString(columnNo, entity.getProjectNo());  // プロジェクトNo
                columnNo++;
                ps.setString(columnNo, entity.getUserName());   // ユーザー名
                columnNo++;                

                result += ps.executeUpdate();
            }
        } finally{
            try {
                if (Objects.nonNull(ps)) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
        }
        return result;
    }

    /**
     * カンバン進捗情報(日付基準)を全て削除する。
     *
     * @return 削除数
     * @throws Exception
     */
    public int removeAll() throws Exception {
        logger.info("removeAll");
        return this.executeUpdate(SQL_DELETE_ALL);
    }

    /**
     * カンバン進捗情報(日付基準)を削除する。
     *
     * @param progressNos progressNo一覧
     * @return 削除数
     * @throws Exception
     */
    public int remove(List<String> progressNos) throws Exception {
        logger.info("remove: size={}", progressNos.size());
        int result = 0;
        PreparedStatement ps = null;
        try {
            for (int i = 0; i < progressNos.size(); i += MAX_SIZE) {
                List<String> targetNos = new ArrayList<>(progressNos.subList(i, Integer.min(i + MAX_SIZE, progressNos.size())));

                String inParamSql = DbUtils.createInParamSql(targetNos.size());
                String sql = SQL_DELETE_PROGRESS.replace(":progressNos", inParamSql);

                ps = progressDb.getConnection().prepareStatement(sql);
                ps.clearParameters();

                for (int id = 0; id < targetNos.size(); id++) {
                    ps.setString(id + 1, targetNos.get(id));// No
                }

                result = ps.executeUpdate();
            }
        } finally{
            try {
                if (Objects.nonNull(ps)) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
        }
        return result;
    }

    /**
     * progressNoを更新する。
     *
     * @param renums progressNoの更新情報一覧
     * @return 更新したprogressNoの数
     * @throws Exception 
     */
    public int updateProgressNos(LinkedHashMap<Integer, Integer> renums) throws Exception {
        logger.info("updateProgressNos: size={}", renums.size());
        int result = 0;
        PreparedStatement ps = null;
        try {
            for (Integer progressNo : renums.keySet().stream().sorted().collect(Collectors.toList())) {
                Integer newNo = renums.get(progressNo);

                ps = progressDb.getConnection().prepareStatement(SQL_UPDATE_PROGRESS_NO);
                ps.clearParameters();

                ps.setString(1, String.valueOf(newNo));// 新
                ps.setString(2, String.valueOf(progressNo));// 旧

                if (ps.executeUpdate() > 0) {
                    result++;
                }
            }
        } finally{
            try {
                if (Objects.nonNull(ps)) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
        }
        return result;
    }

    /**
     * 更新クエリを実行する。
     *
     * @param sql クエリ
     * @throws Exception 
     */
    private int executeUpdate(String sql) throws Exception {
        int result = 0;
        PreparedStatement ps = null;
        try {
            ps = progressDb.getConnection().prepareStatement(sql);

            result = ps.executeUpdate();

        } finally{
            try {
                if (Objects.nonNull(ps)) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
        }
        return result;
    }
}
