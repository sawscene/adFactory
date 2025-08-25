/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.master;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Objects;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;

/**
 * 検査情報
 *
 * @author nar-nakamura
 */
@JsonIgnoreProperties(ignoreUnknown = true)     // JSONに未知のプロパティが存在しても無視する
public class CheckInfoEntity implements Serializable {

    private String key;// プロパティ名
    private CustomPropertyTypeEnum type;// 型
    private String val;// 値
    private int disp;// 表示順

    private WorkPropertyCategoryEnum cat;// プロパティ種別
    private String opt;// 付加情報
    private Double min;// 基準値下限
    private Double max;// 基準値上限
    private String tag;// タグ
    private String rules;// 入力規則
    private Integer page;// 工程セクション表示順
    private Integer cp;// 進捗チェックポイント

    /**
     * コンストラクタ
     */
    public CheckInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param key プロパティ名
     * @param type 型
     * @param val 値
     * @param disp 表示順
     */
    public CheckInfoEntity(String key, CustomPropertyTypeEnum type, String val, int disp) {
        this.key = key;
        this.type = type;
        this.val = val;
        this.disp = disp;
    }

    /**
     * プロパティ名を取得する。
     *
     * @return プロパティ名
     */
    public String getKey() {
        return this.key;
    }

    /**
     * プロパティ名を設定する。
     *
     * @param key プロパティ名
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 型を取得する。
     *
     * @return 型
     */
    public CustomPropertyTypeEnum getType() {
        return this.type;
    }

    /**
     * 型を設定する。
     *
     * @param type 型
     */
    public void setType(CustomPropertyTypeEnum type) {
        this.type = type;
    }

    /**
     * 値を取得する。
     *
     * @return 値
     */
    public String getVal() {
        return this.val;
    }

    /**
     * 値を設定する。
     *
     * @param val 値
     */
    public void setVal(String val) {
        this.val = val;
    }

    /**
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public int getDisp() {
        return this.disp;
    }

    /**
     * 表示順を設定する。
     *
     * @param disp 表示順
     */
    public void setDisp(int disp) {
        this.disp = disp;
    }

    /**
     * プロパティ種別を取得する。
     *
     * @return プロパティ種別
     */
    public WorkPropertyCategoryEnum getCat() {
        return this.cat;
    }

    /**
     * プロパティ種別を設定する。
     *
     * @param cat プロパティ種別
     */
    public void setCat(WorkPropertyCategoryEnum cat) {
        this.cat = cat;
    }

    /**
     * 付加情報を取得する。
     *
     * @return 付加情報
     */
    public String getOpt() {
        return this.opt;
    }

    /**
     * 付加情報を設定する。
     *
     * @param opt 付加情報
     */
    public void setOpt(String opt) {
        this.opt = opt;
    }

    /**
     * 基準値下限を取得する。
     *
     * @return 基準値下限
     */
    public Double getMin() {
        return this.min;
    }

    /**
     * 基準値下限を設定する。
     *
     * @param min 基準値下限
     */
    public void setMin(Double min) {
        this.min = min;
    }

    /**
     * 基準値上限を取得する。
     *
     * @return 基準値上限
     */
    public Double getMax() {
        return this.max;
    }

    /**
     * 基準値上限を設定する。
     *
     * @param max 基準値上限
     */
    public void setMax(Double max) {
        this.max = max;
    }

    /**
     * タグを取得する。
     *
     * @return タグ
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * タグを設定する。
     *
     * @param tag タグ
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * 入力規則を取得する。
     *
     * @return 入力規則
     */
    public String getRules() {
        return this.rules;
    }

    /**
     * 入力規則を設定する。
     *
     * @param rules 入力規則
     */
    public void setRules(String rules) {
        this.rules = rules;
    }

    /**
     * 工程セクション表示順を取得する。
     *
     * @return 工程セクション表示順
     */
    public Integer getPage() {
        return this.page;
    }

    /**
     * 工程セクション表示順を設定する。
     *
     * @param page 工程セクション表示順
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * 進捗チェックポイントを取得する。
     *
     * @return 進捗チェックポイント
     */
    public Integer getCp() {
        return this.cp;
    }

    /**
     * 進捗チェックポイントを設定する。
     *
     * @param cp 進捗チェックポイント
     */
    public void setCp(Integer cp) {
        this.cp = cp;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.key);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CheckInfoEntity other = (CheckInfoEntity) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("CheckInfoEntity{")
                .append("key=").append(this.key)
                .append(", ")
                .append("type=").append(this.type)
                .append(", ")
                .append("val=").append(this.val)
                .append(", ")
                .append("disp=").append(this.disp)
                .append(", ")
                .append("cat=").append(this.cat)
                .append(", ")
                .append("opt=").append(this.opt)
                .append(", ")
                .append("min=").append(this.min)
                .append(", ")
                .append("max=").append(this.max)
                .append(", ")
                .append("tag=").append(this.tag)
                .append(", ")
                .append("rules=").append(this.rules)
                .append(", ")
                .append("page=").append(this.page)
                .append(", ")
                .append("cp=").append(this.cp)
                .append("}")
                .toString();
    }
}
