/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adinterface.command;

import java.io.Serializable;

/**
 * 実績詳細データ
 *
 * @author s-heya
 */
public class WorkDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer order;
    private final String name;
    private final String tag;
    private final String value;

    /**
     * コンストラクタ
     *
     * @param order
     * @param name
     * @param tag
     * @param value
     */
    public WorkDetail(Integer order, String name, String tag, String value) {
        this.order = order;
        this.name = name;
        this.tag = tag;
        this.value = value;
    }

    /**
     * 順序を取得する。
     *
     * @return
     */
    public Integer getOrder() {
        return this.order;
    }

    /**
     * 項目名を取得する。
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * タグを取得する。
     *
     * @return
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * 値を取得する。
     *
     * @return
     */
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkDetail{")
                .append("order=").append(this.order)
                .append(", name=").append(this.name)
                .append(", tag=").append(this.tag)
                .append(", value=").append(this.value)
                .append("}")
                .toString();
    }
}
