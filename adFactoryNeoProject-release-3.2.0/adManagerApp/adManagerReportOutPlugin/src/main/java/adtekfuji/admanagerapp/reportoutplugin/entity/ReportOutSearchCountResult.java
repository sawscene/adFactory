/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.reportoutplugin.entity;

/**
 * 実績出力情報の件数取得結果
 *
 * @author nar-nakamura
 */
public class ReportOutSearchCountResult {
    private long actualsCount;
    private long propsCount;

    /**
     * コンストラクタ
     */
    void ReportOutSearchCountResult() {
    }

    /**
     * 実績出力情報の件数を取得する。
     *
     * @return 実績出力情報の件数
     */
    public long getActualsCount() {
        return this.actualsCount;
    }

    /**
     * 実績出力情報の件数を設定する。
     *
     * @param actualsCount 実績出力情報の件数
     */
    public void setActualsCount(long actualsCount) {
        this.actualsCount = actualsCount;
    }

    /**
     * 工程実績プロパティの件数を取得する。
     *
     * @return 工程実績プロパティの件数
     */
    public long getPropsCount() {
        return this.propsCount;
    }

    /**
     * 工程実績プロパティの件数を設定する。
     *
     * @param propsCount 工程実績プロパティの件数
     */
    public void setPropsCount(long propsCount) {
        this.propsCount = propsCount;
    }

    @Override
    public String toString() {
        return new StringBuilder("ReportOutSearchCountResult{")
                .append("actualsCount=").append(this.actualsCount)
                .append(", ")
                .append("propsCount=").append(this.propsCount)
                .append("}")
                .toString();
    }
}
