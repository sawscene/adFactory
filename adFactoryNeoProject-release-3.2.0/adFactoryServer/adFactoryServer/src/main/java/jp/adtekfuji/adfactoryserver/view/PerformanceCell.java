/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.view;

import java.util.function.BiFunction;

/**
 * 表示データ
 * 
 * @author s-heya
 */
public class PerformanceCell {

    private Integer planNum = 0;
    private Integer actualNum = 0;
    private BiFunction<Integer, Integer, String> format = (val1, val2) -> val1 + "/" + val2;

    /**
     * コンストラクタ
     */
    public PerformanceCell() {
    }

    /**
     * コンストラクタ
     * 
     * @param planNum 計画数
     */
    public PerformanceCell(int planNum) {
        this.planNum = planNum;
    }

    /**
     * 表示値を取得する。
     * 
     * @return 表示値
     */
    public Object getValue() {
        return this.format.apply(this.actualNum, this.planNum);
    }

    /**
     * 計画数を取得する。
     * 
     * @return 計画数
     */
    public Integer getPlanNum() {
        return this.planNum;
    }

    /**
     * 計画数を設定する。
     * 
     * @param planNum 計画数
     */
    public void setPlanNum(Integer planNum) {
        this.planNum = planNum;
    }

    /**
     * 実績数を取得する。
     * 
     * @return 実績数
     */
    public Integer getActualNum() {
        return this.actualNum;
    }

    /**
     * 実績数を設定する。
     * 
     * @param actualNum 実績数
     */
    public void setActualNum(Integer actualNum) {
        this.actualNum = actualNum;
    }

    /**
     * 表示フォーマットを設定する。
     * 
     * @param format 表示フォーマット
     */
    public void setFormat(BiFunction<Integer, Integer, String> format) {
        this.format = format;
    }
}
