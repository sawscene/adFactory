/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.jdbc.progressdb;

import adtekfuji.adbridgebi.entity.WorkProgressEntity;
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
 * 個別進捗情報テーブルの操作
 *
 * @author nar-nakamura
 */
public class WorkProgressAccessor {

    private final Logger logger = LogManager.getLogger();

    private final DbConnector progressDb = ProgressDbConnector.getInstance();

    // テーブル名
    private final String tableName = "trn_work_progress";
    // カラム名
    private final String columnNames[] = {"progress_no", "progress_type", "progress_order", "progress_date", "start_time", "comp_time", "work_name"};

    // 個別進捗情報を追加する。
    private final String SQL_INSERT;
    // 全ての個別進捗情報を削除する。
    private final String SQL_DELETE_ALL;
    // 指定したprogressNoの個別進捗情報を削除する。
    private final String SQL_DELETE_PROGRESS;
    // progressNoを更新する。
    private final String SQL_UPDATE_PROGRESS_NO;

    private final int MAX_SIZE = 100;

    /**
     * コンストラクタ
     */
    public WorkProgressAccessor() {
        String columnValues[] = new String[columnNames.length];
        Arrays.fill(columnValues, "?");

        // 個別進捗情報を追加する。
        SQL_INSERT = new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(String.join(",", columnNames))
                .append(") VALUES (")
                .append(String.join(",", columnValues))
                .append(")")
                .toString();

        // 全ての個別進捗情報を削除する。
        SQL_DELETE_ALL = new StringBuilder("DELETE FROM ")
                .append(tableName)
                .toString();

        // 指定したprogressNoの個別進捗情報を削除する。
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
     * 個別進捗情報を追加する。
     *
     * @param entities 個別進捗情報
     * @return 追加数
     * @throws Exception
     */
    public int add(List<WorkProgressEntity> entities) throws Exception {
        logger.info("add: size={}", entities.size());
        int result = 0;
        PreparedStatement ps = null;
        try {
            // 更新対象カンバン(progressNo)の個別進捗状況をすべて削除する。(MAX_SIZE件数ずつ削除)
            List<String> progressNos = entities.stream().map(p -> p.getProgressNo()).distinct().collect(Collectors.toList());
            this.remove(progressNos);

            // 個別進捗状況を追加する。
            for (WorkProgressEntity entity : entities) {
                ps = progressDb.getConnection().prepareStatement(SQL_INSERT);
                ps.clearParameters();

                ps.setString(1, entity.getProgressNo());// No
                ps.setString(2, entity.getProgressType());// 種別
                ps.setString(3, entity.getProgressOrder());// 順
                ps.setString(4, entity.getProgressDate());// 日付
                ps.setString(5, entity.getStartTime());// 開始時間
                ps.setString(6, entity.getCompTime());// 終了時間
                ps.setString(7, entity.getWorkName());// 工程名

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
     * 個別進捗状況を全て削除する。
     *
     * @return 削除数
     * @throws Exception
     */
    public int removeAll() throws Exception {
        logger.info("removeAll");
        return this.executeUpdate(SQL_DELETE_ALL);
    }

    /**
     * 個別進捗状況を削除する。
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
