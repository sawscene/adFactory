/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.csv;

import adtekfuji.utility.StringUtils;
import com.opencsv.bean.CsvBindByName;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 休日表
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
public class ImportHolidayCsv {

    /**
     * 休日名
     */
    @CsvBindByName(column = "休日名称", required = true)
    private String name;

    /**
     * 休日
     */
    @CsvBindByName(column = "休日", required = true)
    private String holiday;

    /**
     * 休日名の取得
     *
     * @return 休日名
     */
    public String getName() {
        return this.name;
    }

    /**
     * 休日名の設定
     *
     * @param _value 休日名
     */
    public void setName(String _value) {
        this.name = _value;
    }

    /**
     * 休日の取得
     *
     * @return 休日
     */
    public String getHoliday() {
        return this.holiday;
    }

    /**
     * 休日の設定
     *
     * @param _value 休日
     */
    public void setHoliday(String _value) {
        this.holiday = _value;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "name=" + this.name + ", holiday=" + this.holiday;
    }

    /**
     *
     * @return
     */
    public String toStringCharacter() {
        return "休日名=" + this.name + ", 休日=" + this.holiday;
    }

    /**
     * カラム
     *
     * @param idxName 休日名名称のポジション
     * @param idxHoliday 休日のポジション
     * @return
     */
    public static String[] getColumns(int idxName, int idxHoliday) {
        final Logger logger = LogManager.getLogger();

        if (idxName < 0 || idxHoliday < 0) {
            logger.error(" Columns Idx Error");
            return null;
        }

        int maxIdx = idxName > idxHoliday ? idxName : idxHoliday;
        String[] value = new String[maxIdx];
        Arrays.fill(value, "");

        for (int i = 0; i < value.length; i++) {
            if (idxName - 1 == i) {
                value[i] = "name";
            } else if (idxHoliday - 1 == i) {
                value[i] = "holiday";
            }
        }

        logger.info("getColumns:" + Arrays.toString(value));
        return value;
    }

    /**
     * 全ての項目が存在するかチェック
     *
     * @return
     */
    public boolean isNotEmpty() {
        return (!StringUtils.isEmpty(this.name) && !StringUtils.isEmpty(this.holiday));
    }

    /**
     * 全ての項目が存在しないかチェック
     *
     * @return
     */
    public boolean isEmpty() {
        return (StringUtils.isEmpty(this.name) || StringUtils.isEmpty(this.holiday));
    }
}
