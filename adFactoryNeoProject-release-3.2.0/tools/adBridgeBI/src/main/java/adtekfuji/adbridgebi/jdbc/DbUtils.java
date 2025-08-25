/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.jdbc;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * データベース関連ユーティリティ
 *
 * @author nar-nakamura
 */
public class DbUtils {

    public static final String DATETIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * レコードから指定カラムの値を取得する。
     *
     * @param resultSet レコード
     * @param columnName カラム名
     * @return 該当カラムの値 (結果セットに該当カラムが無い、または型が異なる場合はnull)
     */
    public static Integer getIntColumn(ResultSet resultSet, String columnName) {
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
    public static Long getLongColumn(ResultSet resultSet, String columnName) {
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
    public static Double getDoubleColumn(ResultSet resultSet, String columnName) {
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
    public static String getStringColumn(ResultSet resultSet, String columnName) {
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
    public static Boolean getBooleanColumn(ResultSet resultSet, String columnName) {
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
    public static Date getTimestampColumn(ResultSet resultSet, String columnName) {
        try {
            java.sql.Timestamp sqlTimestamp = resultSet.getTimestamp(columnName);
            if (Objects.isNull(sqlTimestamp)) {
                return null;
            }
            LocalDateTime localDt = sqlTimestamp.toLocalDateTime();
            return DateUtils.toDate(localDt);
        } catch (SQLException ex) {
            // カラムなし、または型違い
            return null;
        }
    }

    /**
     * 
     * @param resultSet レコード
     * @param columnName カラム名
     * @return 
     */
    public static KanbanStatusEnum getKanbanStatusEnumColumn(ResultSet resultSet, String columnName) {
        try {
            String name = resultSet.getString(columnName);
            if (StringUtils.isEmpty(name)) {
                return null;
            }
            return KanbanStatusEnum.valueOf(name);
        } catch (SQLException ex) {
            // カラムなし、または型違い
            return null;
        }
    }

    /**
     * IN句のパラメータ代入部を作成する。
     *
     * @param length パラメータ数
     * @return IN句のパラメータ代入部
     */
    public static String createInParamSql(int length) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < length; i++) {
            sb.append("?,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }
}
