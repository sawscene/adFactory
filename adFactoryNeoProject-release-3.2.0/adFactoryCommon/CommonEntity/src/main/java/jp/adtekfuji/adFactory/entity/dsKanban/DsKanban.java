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
 * 補給生産カンバン情報
 * 
 * @author s-heya
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DsKanban {
    
    @JsonProperty("category")
    private Integer category;

    @JsonProperty("productNo")
    private String productNo;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("spec")
    private String spec;
    
    @JsonProperty("package")
    private String packageCode;

    @JsonProperty("location1")
    private String location1;

    @JsonProperty("location2")
    private String location2;

    @JsonProperty("note")
    private String note;
    
    @JsonProperty("event")
    private String event;

    @JsonProperty("mainKanbanId")
    private Long mainKanbanId;

    @JsonProperty("actual")
    private List<DsActual> actuals;
    
    /**
     * コンストラクタ
     */
    public DsKanban() {
    }

    /**
     * コンストラクタ
     * 
     * @param category
     * @param productNo
     * @param productName
     * @param spec
     * @param packageCode
     * @param location1
     * @param location2
     * @param actuals 
     */
    public DsKanban(Integer category, String productNo, String productName, String spec, String packageCode, String location1, String location2, List<DsActual> actuals) {
        this.category = category;
        this.productNo = productNo;
        this.productName = productName;
        this.spec = spec;
        this.packageCode = packageCode;
        this.location1 = location1;
        this.location2 = location2;
        this.actuals = actuals;
    }
    
    /**
     * コンストラクタ
     * 
     * @param map 
     */
    public DsKanban(Map<String, Object> map) {
        this.category = (Integer) map.get("category");
        this.productNo = (String) map.get("productNo");
        this.productName = (String) map.get("productName");
        this.spec = (String) map.get("spec");
        this.packageCode = (String) map.get("package");
        this.location1 = (String) map.get("location1");
        this.location2 = (String) map.get("location2");
        this.note = (String) map.get("note");
        this.event = (String) map.get("event");
        this.mainKanbanId = Objects.nonNull(map.get("mainKanbanId")) ? ((Integer) map.get("mainKanbanId")).longValue() : null;

        ArrayList<Map<String, Object>> _actuals = (ArrayList) map.get("actual");
        if (Objects.nonNull(_actuals)) {
            this.actuals = _actuals.stream()
                    .map(o -> new DsActual(o))
                    .collect(Collectors.toList());
        }
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    /**
     * 組付治具台車Noを取得する。
     * 
     * @return 組付治具台車No
     */
    public String getLocation1() {
        return location1;
    }

    /**
     * 組付治具台車Noを設定する。
     * 
     * @param location1 組付治具台車No
     */
    public void setLocation1(String location1) {
        this.location1 = location1;
    }

    /**
     * 検査治具台車Noを取得する。
     * 
     * @return 検査治具台車No
     */
    public String getLocation2() {
        return location2;
    }

    /**
     * 検査治具台車Noを設定する。
     * 
     * @param location2 検査治具台車No
     */
    public void setLocation2(String location2) {
        this.location2 = location2;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Long getMainKanbanId() {
        return mainKanbanId;
    }

    public void setMainKanbanId(Long mainKanbanId) {
        this.mainKanbanId = mainKanbanId;
    }

    public List<DsActual> getActuals() {
        return actuals;
    }

    public void setActuals(List<DsActual> actuals) {
        this.actuals = actuals;
    }
    
    /**
     * JSON文字列からカンバン情報を取得する。
     * 
     * @param serviceInfosStr
     * @return 
     */
    public static DsKanban lookup(String serviceInfosStr) {
        try {
            Optional<ServiceInfoEntity> opt  = JsonUtils.jsonToObjects(serviceInfosStr, ServiceInfoEntity[].class).stream()
                    .filter(o -> ServiceInfoEntity.SERVICE_INFO_DSKANBAN.equals(o.getService()) && Objects.nonNull(o.getJob()))
                    .findFirst();
            if (opt.isPresent()) {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) opt.get().getJob();
                return new DsKanban(map);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * サービス情報からカンバン情報を取得する。
     * 
     * @param serviceInfo サービス情報
     * @return カンバン情報
     */
    public static DsKanban toDsKanban(ServiceInfoEntity serviceInfo) {
        List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) serviceInfo.getJob();
        if (list.isEmpty()) {
            return null;
        }
        return new DsKanban(list.get(0));
    }
}
