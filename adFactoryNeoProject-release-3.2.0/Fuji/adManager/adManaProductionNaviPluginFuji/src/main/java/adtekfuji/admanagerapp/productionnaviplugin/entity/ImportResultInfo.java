/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.entity;

import java.util.Date;

/**
 * インポート結果情報
 *
 * @author nar-nakamura
 */
public class ImportResultInfo {

    private int successNum = 0;// 成功数
    private int skipNum = 0;// スキップ数
    private int failedNum = 0;// 失敗数
    private Date lastDate;// 最後の完了予定日時

    /**
     * コンストラクタ
     */
    public ImportResultInfo() {
    }

    /**
     * コンストラクタ
     *
     * @param successNum 成功数
     * @param skipNum スキップ数
     * @param failedNum 失敗数
     * @param lastDate 最後の完了予定日時
     */
    public ImportResultInfo(int successNum, int skipNum, int failedNum, Date lastDate) {
        this.successNum = successNum;
        this.skipNum = skipNum;
        this.failedNum = failedNum;
        this.lastDate = lastDate;
    }

    /**
     * 成功数を取得する。
     *
     * @return 成功数
     */
    public int getSuccessNum() {
        return this.successNum;
    }

    /**
     * 成功数を設定する。
     *
     * @param successNum 成功数
     */
    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    /**
     * スキップ数を取得する。
     *
     * @return スキップ数
     */
    public int getSkipNum() {
        return this.skipNum;
    }

    /**
     * スキップ数を設定する。
     *
     * @param skipNum スキップ数
     */
    public void setSkipNum(int skipNum) {
        this.skipNum = skipNum;
    }

    /**
     * 失敗数を取得する。
     *
     * @return 失敗数
     */
    public int getFailedNum() {
        return this.failedNum;
    }

    /**
     * 失敗数を設定する。
     *
     * @param failedNum 失敗数
     */
    public void setFailedNum(int failedNum) {
        this.failedNum = failedNum;
    }

    /**
     * 最後の完了予定日時を取得する。
     *
     * @return 最後の完了予定日時
     */
    public Date getLastDate() {
        return this.lastDate;
    }

    /**
     * 最後の完了予定日時を設定する。
     *
     * @param lastDate 最後の完了予定日時
     */
    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    @Override
    public String toString() {
        return new StringBuilder("ImportResultInfo{")
                .append("successNum=").append(this.successNum)
                .append(", skipNum=").append(this.skipNum)
                .append(", failedNum=").append(this.failedNum)
                .append(", lastDate=").append(this.lastDate)
                .append("}")
                .toString();
    }
}
