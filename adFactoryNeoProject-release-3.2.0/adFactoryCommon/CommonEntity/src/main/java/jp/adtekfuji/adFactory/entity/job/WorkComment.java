/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jp.adtekfuji.adFactory.entity.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Objects;

/**
 * 作業コメント
 * 
 * @author s-heya
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class WorkComment {
    public enum Type {
        Txt,            // テキスト
        Image           // イメージ
    }

    @JsonProperty("id")
    private Long id;

    @JsonProperty("data")
    private String data;

    @JsonProperty("date")
    private String date;

    @JsonProperty("type")
    private Type type;

    @JsonProperty("name")
    private String name;
    
    @JsonProperty("orgId")
    private Long orgId;

    @JsonProperty("work")
    private String work;

    @JsonProperty("del")
    private Boolean del;

    /**
     * コンストラクタ
     */
    public WorkComment() {
        this.type = Type.Txt;
    }

    /**
     * コンストラクタ
     * 
     * @param map 
     */
    public WorkComment(Map<String, Object> map) {
        this.id = (Long) ((Integer) map.get("id")).longValue();
        this.data = (String) map.get("data");
        this.date = (String) map.get("date");
        this.type = Type.valueOf((String) map.getOrDefault("type", "Txt"));
        this.name = (String) map.get("name");
        this.orgId = (Long) ((Integer) map.get("orgId")).longValue();
        this.work = (String) map.get("work");
        this.del = (Boolean) map.get("del");
    }

    /**
     * コメントIDを取得する。
     * 
     * @return コメントID
     */
    public Long getId() {
        return id;
    }

    /**
     * コメントIDを設定する。
     * 
     * @param id コメントID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * データを取得する。
     * 
     * @return データ
     */
    public String getData() {
        return data;
    }

    /**
     * データを設定する。
     * 
     * @param data データ
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * 入力日時を取得する。
     * 
     * @return 入力日時
     */
    public String getDate() {
        return date;
    }

    /**
     * 入力日時を設定する。
     * 
     * @param date 入力日時
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * コメント種類を取得する。
     * 
     * @return コメント種類
     */
    public Type getType() {
        return type;
    }

    /**
     * コメント種類を設定する。
     * 
     * @param type コメント種類
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * 入力者を取得する。
     * 
     * @return 入力者
     */
    public String getName() {
        return name;
    }

    /**
     * 入力者を設定する。
     * 
     * @param name 入力者
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 入力者の組織IDを取得する。
     * 
     * @return 入力者の組織ID
     */
    public Long getOrgId() {
        return orgId;
    }

    /**
     * 入力者の組織IDを設定する。
     * 
     * @param orgId 
     */
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    /**
     * 削除フラグを取得する。
     * 
     * @return 
     */
    public Boolean getDel() {
        return del;
    }

    /**
     * 工程名を取得する。
     * 
     * @return 工程名
     */
    public String getWork() {
        return work;
    }

    /**
     * 工程名を設定する。
     * 
     * @param work 工程名
     */
    public void setWork(String work) {
        this.work = work;
    }

    /**
     * 削除フラグを設定する。
     * 
     * @param del 
     */
    public void setDel(Boolean del) {
        this.del = del;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.id);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj
     * @return 
     */
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
        final WorkComment other = (WorkComment) obj;
        return Objects.equals(this.id, other.id);
    }
    
    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("WorkComment{")
            .append("id=").append(this.id)
            .append(", data=").append(this.data)
            .append(", date=").append(this.date)
            .append(", type=").append(this.type)
            .append(", name=").append(this.name)
            .append(", orgId=").append(this.orgId)
            .append(", work=").append(this.work)
            .append(", del=").append(this.del)
            .append("}")
            .toString();
    }
    
}
