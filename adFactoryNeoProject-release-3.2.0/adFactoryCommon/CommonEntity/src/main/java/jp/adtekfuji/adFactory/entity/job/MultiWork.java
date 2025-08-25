/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 同時作業
 * 
 * @author s-heya
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MultiWork implements Serializable {
    
    public enum MultiWorkType {
        Sequential,         // 複数の作業をまとめて着完
        Simultaneous,       // 複数の作業を並行して実施
        Parallel            // 作業を並行して実施
    }

    /**
     * 同時作業種別
     */
    @JsonProperty("multiWorkType")
    private MultiWorkType multiWorkType;

    /**
     * 作業開始時間
     */
    @JsonProperty("startDate")
    private String startDate;

    /**
     * コンストラクタ
     */
    public MultiWork() {
        multiWorkType = MultiWorkType.Simultaneous;
    }
    
    /**
     * コンストラクタ
     *
     * @param map マップ
     */
    public MultiWork(Map<String, Object> map) {
        multiWorkType = MultiWorkType.valueOf((String) map.getOrDefault("multiWorkType", "Sequential"));
        startDate = (String) map.get("startDate");
    }

    /**
     * 同時作業種別を取得する。
     * 
     * @return 同時作業種別 
     */
    public MultiWorkType getMultiWorkType() {
        return multiWorkType;
    }

    /**
     * 作業開始時間を取得する。
     * 
     * @return 作業開始時間
     */
    public String getStartDate() {
        return startDate;
    }

    /*
     * サービス情報(JSON)から同時作業情報を取り出す
     *
     * @param serviceInfosStr サービス情報(JSON)
     * @return プロダクト情報一覧
     */
    public static MultiWork lookup(String serviceInfosStr) {
        try {
            Optional<ServiceInfoEntity> opt  = JsonUtils.jsonToObjects(serviceInfosStr, ServiceInfoEntity[].class).stream()
                    .filter(o -> ServiceInfoEntity.SERVICE_INFO_MULTI.equals(o.getService()) && Objects.nonNull(o.getJob()))
                    .findFirst();
            if (opt.isPresent()) {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) opt.get().getJob();
                return new MultiWork(map);
            }
            return new MultiWork();
        } catch (Exception ex) {
            return new MultiWork();
        }
    }
    
    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現 
     */
    @Override
    public String toString() {
        return new StringBuilder("MultiWork{")
            .append("multiWorkType=").append(this.multiWorkType)
            .append("startDate=").append(this.startDate)
            .append("}")
            .toString();
    }

    
}
