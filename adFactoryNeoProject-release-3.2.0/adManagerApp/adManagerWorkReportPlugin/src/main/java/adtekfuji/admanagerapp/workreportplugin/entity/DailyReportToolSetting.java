/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.entity;

/**
 *　作業日報ツール設定
 *
 * @author nar-nakamura
 */
public class DailyReportToolSetting {

    private Integer workMin = 0;// 作業時間(分)
    private String workTimeBgColor;// 作業時間の背景色

    /**
     * コンストラクタ
     */
    public DailyReportToolSetting() {
    }

    /**
     * 作業時間(分)を取得する。
     *
     * @return 作業時間(分)
     */
    public Integer getWorkMin() {
        return this.workMin;
    }

    /**
     * 作業時間(分)を設定する。
     *
     * @param workMin 作業時間(分)
     */
    public void setWorkMin(Integer workMin) {
        this.workMin = workMin;
    }

    /**
     * 作業時間の背景色を取得する。
     *
     * @return 作業時間の背景色
     */
    public String getWorkTimeBgColor() {
        return this.workTimeBgColor;
    }

    /**
     * 作業時間の背景色を設定する。
     *
     * @param workTimeBgColor 作業時間の背景色
     */
    public void setWorkTimeBgColor(String workTimeBgColor) {
        this.workTimeBgColor = workTimeBgColor;
    }

    @Override
    public String toString() {
        return new StringBuilder("DailyRepotToolEntity{")
                .append("workMin =").append(this.workMin)
                .append(", workTimeBgColor=").append(this.workTimeBgColor)
                .append("}")
                .toString();
    }
}
