/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.chartplugin.controller;

import java.text.Format;
import org.gillius.jfxutils.chart.FixedFormatTickFormatter;

/**
 * 時間軸の目盛をフォーマット
 *
 * @author s-heya
 */
public class TimeLineTickFormatter extends FixedFormatTickFormatter {

    private long delta = 0L;

    /**
     * コンストラクタ
     *
     * @param format
     */
    public TimeLineTickFormatter(Format format) {
        super(format);
    }

    public void setDelta(long delta) {
        this.delta = delta;
    }

    /**
     * 表示文字を返す。
     *
     * @param value
     * @return
     */
    @Override
    public String format(Number value) {
        Number newValue = - (value.longValue()) + this.delta;
        if (newValue.longValue() <= 0) {
            return "";
        }
        return super.format(newValue);
    }
}
