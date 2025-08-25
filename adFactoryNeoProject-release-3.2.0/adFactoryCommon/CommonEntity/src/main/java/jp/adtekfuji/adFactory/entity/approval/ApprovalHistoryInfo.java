/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.approval;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * 承認履歴情報
 *
 * @author nar-nakamura
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ApprovalHistoryInfo implements Serializable {

    /**
     * 操作
     */
    @JsonProperty("type")
    private String type;

    /**
     * 操作者(組織名)
     */
    @JsonProperty("person")
    private String person;

    /**
     * 操作日時
     */
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date date;

    /**
     * 承認ルート名
     */
    @JsonProperty("route")
    private String route;

    /**
     * 承認順(0:申請者, 1～:承認者)
     */
    @JsonProperty("order")
    private Integer order;

    /**
     * コメント
     */
    @JsonProperty("comment")
    private String comment;

    /**
     * コンストラクタ
     */
    public ApprovalHistoryInfo() {
    }

    /**
     * コンストラクタ
     *
     * @param type 操作
     * @param person 操作者(組織名)
     * @param date 操作日時
     * @param route 承認ルート名
     * @param order 承認順(0:申請者, 1～:承認者)
     * @param comment コメント
     */
    public ApprovalHistoryInfo(String type, String person, Date date, String route, Integer order, String comment) {
        this.type = type;
        this.person = person;
        this.date = date;
        this.route = route;
        this.order = order;
        this.comment = comment;
    }

    /**
     * 操作を取得する。
     *
     * @return 操作
     */
    public String getType() {
        return this.type;
    }

    /**
     * 操作を設定する。
     *
     * @param type 操作
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 操作者を取得する。
     *
     * @return 操作者(組織名)
     */
    public String getPerson() {
        return this.person;
    }

    /**
     * 操作者を設定する。
     *
     * @param person 操作者(組織名)
     */
    public void setPerson(String person) {
        this.person = person;
    }

    /**
     * 操作日時を取得する。
     *
     * @return 操作日時
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * 操作日時を設定する。
     *
     * @param date 操作日時
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * 承認ルート名を取得する。
     *
     * @return 承認ルート名
     */
    public String getRoute() {
        return this.route;
    }

    /**
     * 承認ルート名を設定する。
     *
     * @param route 承認ルート名
     */
    public void setRoute(String route) {
        this.route = route;
    }

    /**
     * 承認順を取得する。
     *
     * @return 承認順(0:申請者, 1～:承認者)
     */
    public Integer getOrder() {
        return this.order;
    }

    /**
     * 承認順を設定する。
     *
     * @param order 承認順(0:申請者, 1～:承認者)
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * コメントを取得する。
     *
     * @return コメント
     */
    public String getComment() {
        return this.comment;
    }

    /**
     * コメントを設定する。
     *
     * @param comment コメント
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return new StringBuilder("ApprovalHistoryInfo{")
                .append("type=").append(this.type)
                .append(", person=").append(this.person)
                .append(", date=").append(this.date)
                .append(", route=").append(this.route)
                .append(", order=").append(this.order)
                .append(", comment=").append(this.comment)
                .append("}")
                .toString();
    }
}
