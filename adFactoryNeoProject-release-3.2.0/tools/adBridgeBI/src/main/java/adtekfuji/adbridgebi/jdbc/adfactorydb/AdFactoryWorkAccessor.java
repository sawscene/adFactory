/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.jdbc.adfactorydb;

import adtekfuji.adbridgebi.common.AdBridgeBIConfig;
import adtekfuji.adbridgebi.entity.AdFactoyWorkEntity;
import adtekfuji.adbridgebi.entity.WorkProgressEntity;
import adtekfuji.adbridgebi.jdbc.DbConnector;
import adtekfuji.adbridgebi.jdbc.DbUtils;
import adtekfuji.utility.StringTime;
import adtekfuji.utility.StringUtils;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
public class AdFactoryWorkAccessor {

    private final Logger logger = LogManager.getLogger();

    private final DbConnector adFactoryDb = AdFactoryDbConnector.getInstance();

    // データベースバージョン情報を取得する。
    private final static String SELECT_TVER = "SELECT sid, verno FROM t_ver";
  
    /**
     * コンストラクタ
     */
    public AdFactoryWorkAccessor() {
        
    }

    /**
     * データベースバージョンを取得する。
     *
     * @return データベースバージョン
     */
    public String getVersion() {
        logger.info("getVersion");
        String result = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(adFactoryDb.getConnection())) {
                adFactoryDb.openDB();
            }

            ps = adFactoryDb.getConnection().prepareStatement(SELECT_TVER);
            ps.clearParameters();
        
            resultSet = ps.executeQuery();
            if (Objects.nonNull(resultSet)) {
                resultSet.next();
                BigDecimal sid = resultSet.getBigDecimal("sid");
                String verno = resultSet.getString("verno");
                result = new StringBuilder()
                        .append(sid).append(".").append(verno).toString();
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
            adFactoryDb.closeDB();
        }
        return result;
    }

    /**
     * adFactoryの工程情報一覧を取得する。
     *
     * @param fromDate
     * @return adFactoryの工程情報一覧
     */
    public List<AdFactoyWorkEntity> getAdFactoryWorks(Date fromDate) {
        logger.info("getAdFactoryWorks");
        List<AdFactoyWorkEntity> entities = new ArrayList();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(adFactoryDb.getConnection())) {
                adFactoryDb.openDB();
            }

            //SimpleDateFormat dateFormat = new SimpleDateFormat(DbUtils.DATETIME_FORMAT_STRING);

            StringBuilder sql = new StringBuilder()
                    .append("SELECT")
                    .append(" kan.kanban_id,")
                    .append(" kan.kanban_name,")
                    .append(" kan.kanban_status,")
                    .append(" wk.work_name,")
                    .append(" wkan.work_kanban_order,")
                    .append(" wkan.work_status,")
                    .append(" wkan.start_datetime,")
                    .append(" wkan.comp_datetime,")
                    .append(" wkan.actual_start_datetime,")
                    .append(" wkan.actual_comp_datetime,")
                    .append(" kan.model_name,")                         // 機種
                    .append(" kan.kanban_add_info") // カンバン追加情報

                    // 工程カンバン
                    .append(" FROM trn_work_kanban wkan")
                    // カンバンを結合
                    .append(" LEFT JOIN trn_kanban kan ON kan.kanban_id = wkan.kanban_id")
                    // 工程を結合
                    .append(" LEFT JOIN mst_work wk ON wk.work_id = wkan.work_id")

                    // 検索条件
                    .append(" WHERE wkan.skip_flag = false")
                    .append(" AND kan.kanban_status IN ('PLANNING','PLANNED','WORKING','SUSPEND')");

            // 2019/10/03 IB-SKINの表示バーを伸ばすため、操作がない工程も出力対象とする。
            // 日時指定がある場合、更新日時が指定日時以降のカンバンのみ対象とする。
            //if (Objects.nonNull(fromDate)) {
            //    sql.append(" AND kan.update_datetime >= '").append(dateFormat.format(fromDate)).append("'");
            //}

            sql.append(" ORDER BY kan.kanban_name, kan.kanban_id, wkan.work_kanban_order");

            ps = adFactoryDb.getConnection().prepareStatement(sql.toString());
            ps.clearParameters();
        
            resultSet = ps.executeQuery();

            if (Objects.nonNull(resultSet)) {
                while (resultSet.next()){
                    AdFactoyWorkEntity entity = this.resultSetToEntity(resultSet);
                    if (entity.getGroupNo()!=0) {
                        entities.add(entity);
                    }
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
            adFactoryDb.closeDB();
        }
        return entities;
    }

    /**
     * 結果セットからadFactoryの工程情報を作成する。
     *
     * @param resultSet 結果セット
     * @return　adFactoryの工程情報
     * @throws Exception 
     */
    private AdFactoyWorkEntity resultSetToEntity(ResultSet resultSet) throws Exception {
        // カンバンプロパティのキー
        String propDeadLine = AdBridgeBIConfig.getInstance().getPropDeadLine(); // 完了予定日
        String propReserveDays = AdBridgeBIConfig.getInstance().getPropReserveDays(); // 予備日数
        String propGroupNo = AdBridgeBIConfig.getInstance().getPropGroupNo(); // グループNo
        String propInfo1 = AdBridgeBIConfig.getInstance().getPropInfo1(); // プロジェクトNo
        String propInfo2 = AdBridgeBIConfig.getInstance().getPropInfo2(); // ユーザー名

        AdFactoyWorkEntity entity = new AdFactoyWorkEntity();

        entity.setKanbanId(DbUtils.getLongColumn(resultSet, "kanban_id"));// カンバンID
        entity.setKanbanName(DbUtils.getStringColumn(resultSet, "kanban_name"));// カンバン名
        entity.setKanbanStatus(DbUtils.getKanbanStatusEnumColumn(resultSet, "kanban_status"));// カンバンステータス
        entity.setWorkName(DbUtils.getStringColumn(resultSet, "work_name"));// 工程名
        entity.setWorkKanbanOrder(DbUtils.getLongColumn(resultSet, "work_kanban_order"));// 工程表示順
        entity.setWorkStatus(DbUtils.getKanbanStatusEnumColumn(resultSet, "work_status"));// 工程ステータス
        entity.setStartDatetime(DbUtils.getTimestampColumn(resultSet, "start_datetime"));// 開始予定日時
        entity.setCompDatetime(DbUtils.getTimestampColumn(resultSet, "comp_datetime"));// 完了予定日時
        entity.setActualStartDatetime(DbUtils.getTimestampColumn(resultSet, "actual_start_datetime"));// 開始日時
        entity.setActualCompDatetime(DbUtils.getTimestampColumn(resultSet, "actual_comp_datetime"));// 完了日時
        entity.setModelName(DbUtils.getStringColumn(resultSet, "model_name"));// 機種

        // カンバン追加情報
        String kanbanAddInfoValue = DbUtils.getStringColumn(resultSet, "kanban_add_info");
        List<AddInfoEntity> kanbanAddInfos = JsonUtils.jsonToObjects(kanbanAddInfoValue, AddInfoEntity[].class);

        // 完了予定日
        Date deadLine;
        Optional<AddInfoEntity> optDeadLine = kanbanAddInfos.stream()
                .filter(p -> Objects.equals(p.getKey(), propDeadLine))
                .findFirst();

        String deadLineValue = null;
        if (optDeadLine.isPresent()) {
            deadLineValue = optDeadLine.get().getVal();
        }

        if (StringUtils.isEmpty(deadLineValue)) {
            deadLine = null;
        } else {
            deadLineValue = Normalizer.normalize(deadLineValue, Normalizer.Form.NFKC);// 半角に変換する。
            deadLine = StringTime.convertStringToDate(deadLineValue, "yyyy/MM/dd");// ※."yyyy/M/d" も変換される。
        }

        entity.setDeadLine(deadLine);

        // 予備日数
        Integer reserveDays;
        Optional<AddInfoEntity> optReserveDays = kanbanAddInfos.stream()
                .filter(p -> Objects.equals(p.getKey(), propReserveDays))
                .findFirst();

        String reserveDaysValue = null;
        if (optReserveDays.isPresent()) {
            reserveDaysValue = optReserveDays.get().getVal();
        }

        if (StringUtils.isEmpty(reserveDaysValue)) {
            reserveDays = 0;
        } else {
            reserveDaysValue = Normalizer.normalize(reserveDaysValue, Normalizer.Form.NFKC);// 半角に変換する。
            reserveDays = StringUtils.parseInteger(reserveDaysValue);
        }

        entity.setReserveDays(reserveDays);

        // グループNo
        Integer groupNo;
        Optional<AddInfoEntity> optGroupNo = kanbanAddInfos.stream()
                .filter(p -> Objects.equals(p.getKey(), propGroupNo))
                .findFirst();

        String groupNoValue = null;
        if (optGroupNo.isPresent()) {
            groupNoValue = optGroupNo.get().getVal();
        }

        if (StringUtils.isEmpty(groupNoValue)) {
            groupNo = 0;
        } else {
            groupNoValue = Normalizer.normalize(groupNoValue, Normalizer.Form.NFKC);// 半角に変換する。
            groupNo = StringUtils.parseInteger(groupNoValue);
            
            if (groupNo < 1000) {
                groupNo = 0;
            } else {
                groupNo = groupNo / 1000 * 1000;
            }
            
        }

        entity.setGroupNo(groupNo);

        // プロジェクトNo
        Optional<AddInfoEntity> optProjectNo = kanbanAddInfos.stream()
                .filter(p -> Objects.equals(p.getKey(), propInfo1))
                .findFirst();

        String projectNo = null;
        if (optProjectNo.isPresent()) {
            projectNo = optProjectNo.get().getVal();
        }

        entity.setProjectNo(projectNo);

        // ユーザー名
        Optional<AddInfoEntity> optUserName = kanbanAddInfos.stream()
                .filter(p -> Objects.equals(p.getKey(), propInfo2))
                .findFirst();

        String userName = null;
        if (optUserName.isPresent()) {
            userName = optUserName.get().getVal();
        }

        entity.setUserName(userName);

        return entity;
    }

    /**
     * 休憩名を指定して、adFactoryの休憩情報一覧を取得する。
     *
     * @param breaktimeNames 休憩時間名
     * @return adFactoryの休憩情報一覧
     */
    public List<BreakTimeInfoEntity> getBreakTimes(List<String> breaktimeNames) {
        logger.info("getBreakTimes");
        List<BreakTimeInfoEntity> entities = new ArrayList();
        if (Objects.isNull(breaktimeNames) || breaktimeNames.isEmpty()) {
            return entities;
        }

        StringBuilder names = new StringBuilder();
        for (String breaktimeName : breaktimeNames) {
            if (names.length() > 0) {
                names.append(",");
            }
            names.append(new StringBuilder("'").append(breaktimeName).append("'").toString());
        }

        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(adFactoryDb.getConnection())) {
                adFactoryDb.openDB();
            }

            String sql = new StringBuilder()
                    .append("SELECT")
                    .append(" breaktime_id,")
                    .append(" breaktime_name,")
                    .append(" starttime,")
                    .append(" endtime")
                    .append(" FROM mst_breaktime")
                    .append(" WHERE breaktime_name IN (").append(names.toString()).append(")")
                    .append(" ORDER BY starttime, endtime, breaktime_name")
                    .toString();

            ps = adFactoryDb.getConnection().prepareStatement(sql);
            ps.clearParameters();
        
            resultSet = ps.executeQuery();

            if (Objects.nonNull(resultSet)) {
                while (resultSet.next()){
                    BreakTimeInfoEntity entity = this.resultSetToBreakTimeEntity(resultSet);
                    entities.add(entity);
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
            adFactoryDb.closeDB();
        }
        return entities;
    }

    /**
     * 結果セットからadFactoryの休憩情報を作成する。
     *
     * @param resultSet 結果セット
     * @return adFactoryの休憩情報
     * @throws Exception 
     */
    private BreakTimeInfoEntity resultSetToBreakTimeEntity(ResultSet resultSet) throws Exception {
        BreakTimeInfoEntity entity = new BreakTimeInfoEntity();

        entity.setBreaktimeId(DbUtils.getLongColumn(resultSet, "breaktime_id"));
        entity.setBreaktimeName(DbUtils.getStringColumn(resultSet, "breaktime_name"));
        entity.setStarttime(DbUtils.getTimestampColumn(resultSet, "starttime"));
        entity.setEndtime(DbUtils.getTimestampColumn(resultSet, "endtime"));

        return entity;
    }

    /**
     * 指定日時以降に完了したカンバン(完了・中止・その他)のカンバンID一覧を取得する。
     *
     * @param fromDate 日時
     * @return カンバンID一覧
     */
    public List<Long> getCompKanbanIds(Date fromDate) {
        logger.info("getCompKanbanIds");
        List<Long> compKanbanIds = new ArrayList();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(adFactoryDb.getConnection())) {
                adFactoryDb.openDB();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(DbUtils.DATETIME_FORMAT_STRING);

            String sql = new StringBuilder()
                    .append("SELECT")
                    .append(" kanban_id")
                    .append(" FROM trn_kanban")
                    .append(" WHERE kanban_status IN ('COMPLETION','INTERRUPT','OTHER')")
                    .append(" AND update_datetime >= '").append(dateFormat.format(fromDate)).append("'")
                    .toString();

            ps = adFactoryDb.getConnection().prepareStatement(sql);
            ps.clearParameters();
        
            resultSet = ps.executeQuery();

            if (Objects.nonNull(resultSet)) {
                while (resultSet.next()){
                    compKanbanIds.add(DbUtils.getLongColumn(resultSet, "kanban_id"));
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
            adFactoryDb.closeDB();
        }
        return compKanbanIds;
    }
    
    /**
     * 個別進捗を取得する。
     * 
     * @param kanbanIds
     * @return 
     */
     public Map<Long, List<WorkProgressEntity>> getWorkProgress(List<Long> kanbanIds) {
        logger.info("getWorkProgress");

        Map<Long, List<WorkProgressEntity>> map = new HashMap<>();
        List<WorkProgressEntity> list = new LinkedList<>();
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            if (Objects.isNull(adFactoryDb.getConnection())) {
                adFactoryDb.openDB();
            }

            StringBuilder sql = new StringBuilder()
                    .append("SELECT ")
                    .append(" kanban_id, ")
                    .append(" work_name, ")
                    .append(" work_kanban_status, ")
                    .append(" actual_start_time, ")
                    .append(" actual_end_time ")
                    .append("FROM view_work_history ")
                    .append("WHERE kanban_id IN (");
            
            for (int i = 0; i < kanbanIds.size(); i++) {
                sql.append("?,");
            }
            
            sql.delete(sql.length()-1, sql.length());
            sql.append(") ORDER BY kanban_id, work_kanban_id, actual_id");

            ps = adFactoryDb.getConnection().prepareStatement(sql.toString());
            for (int i = 0; i < kanbanIds.size(); i++) {
                ps.setLong(i + 1, kanbanIds.get(i));
            }

            resultSet = ps.executeQuery();

            if (Objects.nonNull(resultSet)) {
                while (resultSet.next()) {
                    WorkProgressEntity entity = this.resultSetToWorkProgressEntity(resultSet);
                    if (map.containsKey(entity.getKanbanId())) {
                        list = map.get(entity.getKanbanId());
                    } else {
                        list = new LinkedList<>();
                        map.put(entity.getKanbanId(), list);
                    }
                    
                    list.add(entity);
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
            adFactoryDb.closeDB();
        }
        return map;
    }

    /**
     * 個別進捗情報を作成する。
     * 
     * @param resultSet
     * @return
     * @throws Exception 
     */
    private WorkProgressEntity resultSetToWorkProgressEntity(ResultSet resultSet) throws Exception {
        WorkProgressEntity entity = new WorkProgressEntity();
        entity.setWorkName(DbUtils.getStringColumn(resultSet, "work_name"));                    // 工程名
        entity.setProgressType("2");                                                            // 種別
        entity.setKanbanId(DbUtils.getLongColumn(resultSet, "kanban_id"));                      // カンバンID
        entity.setWorkKanbanStatus(DbUtils.getStringColumn(resultSet, "work_kanban_status"));   // 工程ステータス
        entity.setStartDatetime(DbUtils.getTimestampColumn(resultSet, "actual_start_time"));    // 開始日時
        entity.setCompDatetime(DbUtils.getTimestampColumn(resultSet, "actual_end_time"));       // 完了日時
        return entity;
    }
}
