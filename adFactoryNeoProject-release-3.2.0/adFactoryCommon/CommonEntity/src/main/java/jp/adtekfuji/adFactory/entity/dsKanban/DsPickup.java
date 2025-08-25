/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.dsKanban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 部品集荷情報
 * 
 * @author s-heya
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DsPickup {
    
    @JsonProperty("category")
    private Integer category;

    @JsonProperty("productNo")
    private String productNo;

    @JsonProperty("pickup")
    private List<DsParts> partsList;

    /**
     * コンストラクタ
     */
    public DsPickup() {
    }

    /**
     * コンストラクタ
     * 
     * @param category
     * @param productNo 
     */
    public DsPickup(Integer category, String productNo) {
        this.category = category;
        this.productNo = productNo;
    }

    /**
     * コンストラクタ
     * 
     * @param map 
     */
    public DsPickup(Map<String, Object> map) {
        this.category = (Integer) map.get("category");
        this.productNo = (String) map.get("productNo");

        ArrayList<Map<String, Object>> _partsList = (ArrayList) map.get("pickup");
        this.partsList = _partsList.stream()
                .map(o -> new DsParts(o))
                .collect(Collectors.toList());
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public List<DsParts> getPartsList() {
        return partsList;
    }

    public void setPartsList(List<DsParts> partsList) {
        this.partsList = partsList;
    }
    
    /**
     * JSON文字列から集荷実績情報を取得する。
     * 
     * @param serviceInfosStr
     * @return 
     */
    public static DsPickup lookup(String serviceInfosStr) {
        try {
            Optional<ServiceInfoEntity> opt  = JsonUtils.jsonToObjects(serviceInfosStr, ServiceInfoEntity[].class).stream()
                    .filter(o -> ServiceInfoEntity.SERVICE_INFO_DSPICKUP.equals(o.getService()) && Objects.nonNull(o.getJob()))
                    .findFirst();
            if (opt.isPresent()) {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) opt.get().getJob();
                return new DsPickup(map);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
