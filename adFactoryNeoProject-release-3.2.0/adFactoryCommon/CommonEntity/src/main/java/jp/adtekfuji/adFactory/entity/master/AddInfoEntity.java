/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.master;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportPropertyMemoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;

/**
 * 追加情報
 *
 * @author nar-nakamura
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AddInfoEntity implements Serializable {

    private String key;// プロパティ名
    private CustomPropertyTypeEnum type;// 型
    private String val;// 値
    private Integer disp;// 表示順

    private Long accessoryId;// アクセサリID
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ActualProductReportPropertyMemoEntity> memo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String m;

    /**
     * コンストラクタ
     */
    public AddInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param key プロパティ名
     * @param type 型
     * @param val 値
     * @param disp 表示順
     */
    public AddInfoEntity(String key, CustomPropertyTypeEnum type, String val, Integer disp, List<ActualProductReportPropertyMemoEntity> memo) {
        this.key = key;
        this.type = type;
        this.val = val;
        this.disp = disp;
        this.accessoryId = null;
        this.memo = memo;
    }
    
    /**
     * コンストラクタ
     *
     * @param key プロパティ名
     * @param type 型
     * @param val 値
     * @param accessoryId 工程プロパティ表示順
     */
    public AddInfoEntity(String key, CustomPropertyTypeEnum type, String val, Integer disp, Long accessoryId, List<ActualProductReportPropertyMemoEntity> memo) {
        this.key = key;
        this.type = type;
        this.val = val;
        this.disp = disp;
        this.accessoryId = accessoryId;
        this.memo = memo;
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
    public Integer getDisp() {
        return this.disp;
    }

    /**
     * 表示順を設定する。
     *
     * @param disp 表示順
     */
    public void setDisp(Integer disp) {
        this.disp = disp;
    }

    /**
     * アクセサリIDを取得する。
     *
     * @return アクセサリID
     */
    public Long getAccessoryId() {
        return this.accessoryId;
    }

    /**
     * アクセサリIDを設定する。
     *
     * @param accessoryId アクセサリID
     */
    public void setAccessoryId(Long accessoryId) {
        this.accessoryId = accessoryId;
    }

    public List<ActualProductReportPropertyMemoEntity> getMemo() {
        return memo;
    }

    public void setMemo(List<ActualProductReportPropertyMemoEntity> memo) {
        this.memo = memo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.key);
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
        final AddInfoEntity other = (AddInfoEntity) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("AddInfoEntity{")
                .append("key=").append(this.key)
                .append(", type=").append(this.type)
                .append(", val=").append(this.val)
                .append(", disp=").append(this.disp)
                .append(", accessoryId=").append(this.accessoryId)
                .append("}")
                .toString();
    }
}
