/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.reportoutplugin.entity;

import jp.adtekfuji.adFactory.entity.view.ReportOutSummaryInfoEntity;
import java.util.List;
import jp.adtekfuji.adFactory.entity.actual.ActualPropertyEntity;
import jp.adtekfuji.adFactory.entity.view.ReportOutInfoEntity;

/**
 * 実績出力情報の検索結果
 *
 * @author nar-nakamura
 */
public class ReportOutSearchResult {
    private List<ReportOutInfoEntity> actuals;
    private long actualsTo;
    private long propsTo;
    private List<ActualPropertyEntity> remainingProps;
    private List<ReportOutSummaryInfoEntity> workActuals;

    /**
     * コンストラクタ
     */
    void ReportOutSearchResult() {
    }

    /**
     * 実績出力情報一覧を取得する。
     *
     * @return 実績出力情報一覧
     */
    public List<ReportOutInfoEntity> getActuals() {
        return this.actuals;
    }

    /**
     * 実績出力情報一覧を設定する。
     *
     * @param actuals 実績出力情報一覧
     */
    public void setActuals(List<ReportOutInfoEntity> actuals) {
        this.actuals = actuals;
    }

    /**
     * 実績出力情報一覧取得範囲の末尾(to)を取得する。
     *
     * @return 実績出力情報一覧取得範囲の末尾(to)
     */
    public long getActualsTo() {
        return this.actualsTo;
    }

    /**
     * 実績出力情報一覧取得範囲の末尾(to)を設定する。
     *
     * @param actualsTo 実績出力情報一覧取得範囲の末尾(to)
     */
    public void setActualsTo(long actualsTo) {
        this.actualsTo = actualsTo;
    }

    /**
     * 工程実績プロパティ一覧取得範囲の末尾(to)を取得する。
     *
     * @return 工程実績プロパティ一覧取得範囲の末尾(to)
     */
    public long getPropsTo() {
        return this.propsTo;
    }

    /**
     * 工程実績プロパティ一覧取得範囲の末尾(to)を設定する。
     *
     * @param propsTo 工程実績プロパティ一覧取得範囲の末尾(to)
     */
    public void setPropsTo(long propsTo) {
        this.propsTo = propsTo;
    }

    /**
     * 実績出力情報と結合されずに残った、工程実績プロパティ一覧を取得する。
     *
     * @return 実績出力情報と結合されずに残った、工程実績プロパティ一覧
     */
    public List<ActualPropertyEntity> getRemainingProps() {
        return this.remainingProps;
    }

    /**
     * 実績出力情報と結合されずに残った、工程実績プロパティ一覧を設定する。
     *
     * @param remainingProps 実績出力情報と結合されずに残った、工程実績プロパティ一覧
     */
    public void setRemainingProps(List<ActualPropertyEntity> remainingProps) {
        this.remainingProps = remainingProps;
    }
    
    /**
     * 工程実績出力情報一覧を取得する。
     *
     * @return 工程実績出力情報一覧
     */
    public List<ReportOutSummaryInfoEntity> getWorkActuals() {
        return this.workActuals;
    }

    /**
     * 工程実績出力情報一覧を設定する。
     *
     * @param workActuals 工程実績出力情報一覧
     */
    public void setWorkActuals(List<ReportOutSummaryInfoEntity> workActuals) {
        this.workActuals = workActuals;
    }

    @Override
    public String toString() {
        return new StringBuilder("ReportOutSearchResult{")
                .append("actualsTo=").append(this.actualsTo)
                .append(", ")
                .append("propsTo=").append(this.propsTo)
                .append("}")
                .toString();
    }
}
