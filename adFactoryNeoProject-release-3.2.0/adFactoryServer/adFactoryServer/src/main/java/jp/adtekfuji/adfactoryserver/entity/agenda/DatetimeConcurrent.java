/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.agenda;

import java.util.Date;
import jp.adtekfuji.adFactory.entity.agenda.AgendaConcurrentEntity;

/**
 * 日時範囲・並列予実情報
 * (AgendaFacadeRESTから分離)
 *
 * @author nar-nakamura
 */
public class DatetimeConcurrent {

    private Date start;// 開始日時
    private Date end;// 完了日時
    private AgendaConcurrentEntity concurrent;// 並列予実情報

    /**
     * コンストラクタ
     */
    public DatetimeConcurrent() {
    }

    /**
     * コンストラクタ
     *
     * @param start 開始日時
     * @param end 完了日時
     * @param concurrent 並列予実情報
     */
    public DatetimeConcurrent(Date start, Date end, AgendaConcurrentEntity concurrent) {
        this.start = start;
        this.end = end;
        this.concurrent = concurrent;
    }

    /**
     * 開始日時を取得する。
     *
     * @return 開始日時
     */
    public Date getStart() {
        return this.start;
    }

    /**
     * 開始日時を設定する。
     *
     * @param start 開始日時
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * 完了日時を取得する。
     *
     * @return 完了日時
     */
    public Date getEnd() {
        return this.end;
    }

    /**
     * 完了日時を設定する。
     *
     * @param end 完了日時
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * 並列予実情報を取得する。
     *
     * @return 並列予実情報
     */
    public AgendaConcurrentEntity getConcurrent() {
        return this.concurrent;
    }

    /**
     * 並列予実情報を設定する。
     *
     * @param concurrent 並列予実情報
     */
    public void setConcurrent(AgendaConcurrentEntity concurrent) {
        this.concurrent = concurrent;
    }

    @Override
    public String toString() {
        return new StringBuilder("DatetimeConcurrent{")
                .append("start=").append(this.start)
                .append(", ")
                .append("end=").append(this.end)
                .append(", ")
                .append("concurrent=").append(this.concurrent)
                .append("}")
                .toString();
    }
}
