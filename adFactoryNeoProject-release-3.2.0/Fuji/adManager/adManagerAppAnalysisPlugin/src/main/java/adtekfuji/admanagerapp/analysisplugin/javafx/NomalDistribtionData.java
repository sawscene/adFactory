/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.javafx;

import adtekfuji.locale.LocaleUtils;
import java.util.List;
import java.util.ResourceBundle;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;

/**
 * 正規分布表示用のデータクラス 実績の管理とかもここでやらないとまずい
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.3.Fri
 */
public class NomalDistribtionData {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final String graphTitle;
    private final List<Long> datas;
    private final Integer takttime;
    private final List<ActualResultEntity> actuals;
    private Integer outOfRangeNum = 0;

    public NomalDistribtionData(String graphTitle, String xAxisTitle, String yAxisTitle, List<Long> datas, Integer takttime, List<ActualResultEntity> actuals) {
        this.graphTitle = graphTitle;
        this.datas = datas;
        this.takttime = takttime;
        this.actuals = actuals;
    }

    public String getGraphTitle() {
        return graphTitle;
    }

    public List<Long> getDatas() {
        return datas;
    }

    public Integer getTakttime() {
        return takttime;
    }

    public List<ActualResultEntity> getActuals() {
        return actuals;
    }

    public Integer getOutOfRangeNum() {
        return outOfRangeNum;
    }

    public void setOutOfRangeNum(Integer outOfRangeNum) {
        this.outOfRangeNum = outOfRangeNum;
    }

}
