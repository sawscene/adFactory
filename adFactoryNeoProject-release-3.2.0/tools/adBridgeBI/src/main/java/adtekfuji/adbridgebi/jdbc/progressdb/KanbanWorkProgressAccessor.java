/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.jdbc.progressdb;

import adtekfuji.adbridgebi.entity.KanbanWorkProgressEntity;
import adtekfuji.adbridgebi.jdbc.DbConnector;
import adtekfuji.adbridgebi.jdbc.DbUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン進捗情報(工程基準)テーブルの操作
 *
 * @author nar-nakamura
 */
public class KanbanWorkProgressAccessor {

    private final Logger logger = LogManager.getLogger();

    private final DbConnector progressDb = ProgressDbConnector.getInstance();

    // テーブル名
    private final String tableName = "trn_kanban_work_progress";
    // カラム名
    private final String columnNames[] = {
        "progress_no", "kanban_name",
        "work_name_1", "start_date_1", "status_1", "today_flg_1",
        "work_name_2", "start_date_2", "status_2", "today_flg_2",
        "work_name_3", "start_date_3", "status_3", "today_flg_3",
        "work_name_4", "start_date_4", "status_4", "today_flg_4",
        "work_name_5", "start_date_5", "status_5", "today_flg_5",
        "work_name_6", "start_date_6", "status_6", "today_flg_6",
        "work_name_7", "start_date_7", "status_7", "today_flg_7",
        "work_name_8", "start_date_8", "status_8", "today_flg_8",
        "work_name_9", "start_date_9", "status_9", "today_flg_9",
        "work_name_10", "start_date_10", "status_10", "today_flg_10",
        "work_name_11", "start_date_11", "status_11", "today_flg_11",
        "work_name_12", "start_date_12", "status_12", "today_flg_12",
        "work_name_13", "start_date_13", "status_13", "today_flg_13",
        "work_name_14", "start_date_14", "status_14", "today_flg_14",
        "work_name_15", "start_date_15", "status_15", "today_flg_15",
        "work_name_16", "start_date_16", "status_16", "today_flg_16",
        "work_name_17", "start_date_17", "status_17", "today_flg_17",
        "work_name_18", "start_date_18", "status_18", "today_flg_18",
        "work_name_19", "start_date_19", "status_19", "today_flg_19",
        "work_name_20", "start_date_20", "status_20", "today_flg_20",
        "group_no", "seq_no", "kanban_id", "kanban_status", 
        "model_name", "info_1", "info_2",
    };

    // カラム名
    private final String columnNames2[] = {
        "progress_no", "kanban_name", "group_no", "seq_no", "kanban_id",
    };

    // カンバン進捗情報(工程基準)を追加する。
    private final String SQL_INSERT;
    // ダミーデータを追加する。
    private final String SQL_INSERT_DUMMY;
    // 全てのカンバン進捗情報(工程基準)を削除する。
    private final String SQL_DELETE_ALL;
    // 指定したprogressNoのカンバン進捗情報(工程基準)を削除する。
    private final String SQL_DELETE_PROGRESS;
    // 全てのカンバンの進捗情報(工程基準)の本日フラグを更新する。
    private final String SQL_UPDATE_TODAY_FLGS;
    // progressNoを更新する。
    private final String SQL_UPDATE_PROGRESS_NO;

    // 歯抜けの連番の中で最も小さい番号を取得する。
    private final static String SQL_SELECT_SEQNO = "SELECT Min(a.seq_no) + 1 AS seq_no "
            + "FROM (SELECT seq_no FROM trn_kanban_work_progress WHERE group_no = ? "
            + " UNION ALL "
            + " SELECT 0 AS 番号) a "
            + "LEFT JOIN trn_kanban_work_progress b ON a.seq_no + 1 = b.seq_no AND group_no = ? "
            + "WHERE b.seq_no IS NULL;";

    private final int MAX_SIZE = 100;

    /**
     * コンストラクタ
     */
    public KanbanWorkProgressAccessor() {
        // カンバン進捗情報(工程基準)を追加する。
        String columnValues[] = new String[columnNames.length];
        Arrays.fill(columnValues, "?");
        SQL_INSERT = new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(String.join(",", columnNames))
                .append(") VALUES (")
                .append(String.join(",", columnValues))
                .append(")")
                .toString();
        
        // ダミーデータを追加する。
        String columnValues2[] = new String[columnNames2.length];
        Arrays.fill(columnValues2, "?");
        SQL_INSERT_DUMMY = new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(String.join(",", columnNames2))
                .append(") VALUES (")
                .append(String.join(",", columnValues2))
                .append(")")
                .toString();

        // 全てのカンバン進捗情報(工程基準)を削除する。
        SQL_DELETE_ALL = new StringBuilder("DELETE FROM ")
                .append(tableName)
                .toString();

        // 指定したprogressNoのカンバン進捗情報(工程基準)を削除する。
        SQL_DELETE_PROGRESS = new StringBuilder("DELETE FROM ")
                .append(tableName)
                .append(" WHERE progress_no IN :progressNos")
                .toString();

        // 全てのカンバンの進捗情報(工程基準)の本日フラグを更新する。
        List<String> todayFlgQueries = new ArrayList();
        for (int no = 1; no <= KanbanWorkProgressEntity.MAX_WORK; no++) {
            String todayFlgQuery = new StringBuilder()
                    .append("today_flg_").append(no)
                    .append(" = CASE WHEN ")
                    .append("start_date_").append(no)
                    .append(" = to_char(CURRENT_DATE, 'YYYY/MM/DD') THEN '1' ELSE '0' END")
                    .toString();

            todayFlgQueries.add(todayFlgQuery);
        }

        SQL_UPDATE_TODAY_FLGS = new StringBuilder("UPDATE ")
                .append(tableName)
                .append(" SET ")
                .append(String.join(",", todayFlgQueries))
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
     * カンバンの進捗情報(工程基準)を追加する。
     *
     * @param entities カンバンの進捗情報(工程基準)
     * @return 追加数
     * @throws Exception
     */
    public int add(List<KanbanWorkProgressEntity> entities) throws Exception {
        logger.info("add: size={}", entities.size());
        int result = 0;
        PreparedStatement ps = null;
        try {
            // 更新対象カンバン(progressNo)のカンバン進捗情報(工程基準)をすべて削除する。(MAX_SIZE件数ずつ削除)
            List<String> progressNos = entities.stream().map(p -> p.getProgressNo()).distinct().collect(Collectors.toList());
            this.remove(progressNos);

            // カンバン進捗情報(工程基準)を追加する。
            for (KanbanWorkProgressEntity entity : entities) {
                ps = progressDb.getConnection().prepareStatement(SQL_INSERT);
                ps.clearParameters();

                ps.setString(1, entity.getProgressNo());// No
                ps.setString(2, entity.getKanbanName());// カンバン名

                int columnNo = 3;
                for (int i = 0; i < KanbanWorkProgressEntity.MAX_WORK; i++) {
                    ps.setString(columnNo, entity.getWorkName(i));// 工程_名称
                    columnNo++;
                    ps.setString(columnNo, entity.getStartDate(i));// 工程_計画開始日付
                    columnNo++;
                    ps.setString(columnNo, entity.getStatus(i));// 工程_ステータス
                    columnNo++;
                    ps.setString(columnNo, entity.getTodayFlg(i));// 工程_本日フラグ
                    columnNo++;
                }
                
                ps.setInt(columnNo, entity.getGroupNo());// グループNo
                columnNo++;
                ps.setInt(columnNo, entity.getSeqNo());// 連番
                columnNo++;
                ps.setLong(columnNo, entity.getKanbanId());// カンバンID
                columnNo++;
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
     * カンバンの進捗情報(工程基準)を全て削除する。
     *
     * @return 削除数
     * @throws Exception
     */
    public int removeAll() throws Exception {
        logger.info("removeAll");
        return this.executeUpdate(SQL_DELETE_ALL);
    }

    /**
     * ダミーデータを削除する。
     * 
     * @return
     * @throws Exception 
     */
    public int removeDummy() throws Exception {
        logger.info("removeDummy");

        String sql = new StringBuilder("DELETE FROM ")
                .append(tableName)
                .append(" WHERE kanban_id = 0")
                .toString();

        return this.executeUpdate(sql);
    }

    /**
     * カンバンの進捗情報(工程基準)を削除する。
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
     * 全てのカンバンの進捗情報(工程基準)の本日フラグを更新する。
     *
     * @return 更新数
     * @throws Exception
     */
    public int updateTodayFlgs() throws Exception {
        logger.info("updateTodayFlgs");
        return this.executeUpdate(SQL_UPDATE_TODAY_FLGS);
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

    /**
     * 工程基準進捗テーブルを読み込み、Key:カンバンIDとValue:プログレスNoのマップを生成する。
     * 
     * @return 
     */    
    public Map<Long, Integer> createKanbanProgressNoMap() {
        logger.info("createKanbanProgressNoMap");

        Map<Long, Integer> map = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        
        try {
            if (Objects.isNull(progressDb.getConnection())) {
                progressDb.openDB();
            }
            
            String sql = "SELECT w.progress_no, w.kanban_id FROM trn_kanban_work_progress w";
            ps = progressDb.getConnection().prepareStatement(sql);
            ps.clearParameters();
        
            resultSet = ps.executeQuery();          

            if (Objects.nonNull(resultSet)) {
                while (resultSet.next()) {
                    Long kanbanId = DbUtils.getLongColumn(resultSet, "kanban_id");
                    Integer progress_no = Integer.parseInt(DbUtils.getStringColumn(resultSet, "progress_no"));
                    map.put(kanbanId, progress_no);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally{
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
            progressDb.closeDB();
        }
        return map;
    }

    /**
     * 連番を生成する。
     * 
     * @param groupNo
     * @return 
     */
    public Integer createSeqNo(int groupNo) {
        logger.info("createSeqNo");

        Integer seqNo = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            if (Objects.isNull(progressDb.getConnection())) {
                progressDb.openDB();
            }

            ps = progressDb.getConnection().prepareStatement(SQL_SELECT_SEQNO);
            ps.clearParameters();
            ps.setInt(1, groupNo);
            ps.setInt(2, groupNo);
        
            resultSet = ps.executeQuery();

            if (Objects.nonNull(resultSet) && resultSet.next()) {
                seqNo = resultSet.getInt("seq_no");

                if (Objects.nonNull(seqNo)) {
                    // ダミーデータを追加する。
                    ps = progressDb.getConnection().prepareStatement(SQL_INSERT_DUMMY);
                    ps.clearParameters();

                    ps.setString(1, String.valueOf(groupNo + seqNo));
                    ps.setString(2, "Dummy");
                    ps.setInt(3, groupNo);
                    ps.setInt(4, seqNo);
                    ps.setLong(5, 0);

                    ps.executeUpdate();
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally{
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
            progressDb.closeDB();
        }
        return seqNo;
    }
}
