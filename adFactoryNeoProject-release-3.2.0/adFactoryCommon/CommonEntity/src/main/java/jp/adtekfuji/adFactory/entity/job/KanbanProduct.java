/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * プロダクト情報
 *
 * @author y-harada
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class KanbanProduct implements Serializable {

    @JsonProperty("uid")
    private String uid;                             // UID(シリアル番号等)

    @JsonProperty("defect")
    private String defect;                          // 不良(理由)

    @JsonProperty("order")
    private Integer orderNumber;                    // 順番
    
    @JsonProperty("status")
    @JsonSerialize(using = KanbanStatusEnum.Serializer.class)
    @JsonDeserialize(using = KanbanStatusEnum.Deserializer.class)
    private KanbanStatusEnum status;                // ステータス
    
    @JsonProperty("implement")
    private Boolean implement;                      // 作業実施

    @JsonProperty("start")
    private String startTime;                       // 開始日時

    @JsonProperty("comp")
    private String compTime;                        // 完了日時
    
    /**
     * コンストラクタ
     */
    public KanbanProduct() {
    }

    /**
     * コンストラクタ
     *
     * @param uid
     * @param defect
     * @param orderNumber
     */
    public KanbanProduct(String uid, String defect, Integer orderNumber) {
        this.uid = uid;
        this.defect = defect;
        this.orderNumber = orderNumber;
        this.implement = false;
    }

    /**
     * コンストラクタ
     *
     * @param map マップ
     */
    public KanbanProduct(Map<String, Object> map) {
        this.uid = (String) map.get("uid");
        this.defect = (String) map.get("defect");
        this.orderNumber = (Integer) map.get("order");
        this.status = KanbanStatusEnum.getEnum(String.valueOf(map.get("status")));
        this.startTime = (String) map.getOrDefault("start", null);
        this.compTime = (String) map.getOrDefault("comp", null);
        this.implement = (Boolean) map.getOrDefault("implement", false);
    }

    /**
     * UIDを取得する。
     *
     * @return UID
     */
    public String getUid() {
        return this.uid;
    }

    /**
     * UIDを設定する。
     *
     * @param uid UID
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 不良を取得する。
     *
     * @return 不良
     */
    public String getDefect() {
        return this.defect;
    }

    /**
     * 不良を設定する。
     *
     * @param defect 不良
     */
    public void setDefect(String defect) {
        this.defect = defect;
    }

    /**
     * 順番を取得する。
     *
     * @return UID
     */
    public Integer getOrderNumber() {
        return this.orderNumber;
    }

    /**
     * 順番を設定する。
     *
     * @param orderNumber 順番
     */
    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * ステータスを取得する。
     * 
     * @return ステータス
     */
    public KanbanStatusEnum getStatus() {
        return status;
    }

    /**
     * ステータスを設定する。
     * 
     * @param status ステータス
     */
    public void setStatus(KanbanStatusEnum status) {
        this.status = status;
    }

    /**
     * 開始日時を取得する。
     * 
     * @return 開始日時
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * 開始日時を設定する。
     * 
     * @param startTime 開始日時
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * 完了日時を取得する。
     * 
     * @return 完了日時
     */
    public String getCompTime() {
        return compTime;
    }

    /**
     * 完了日時を設定する。
     * 
     * @param compTime 完了日時
     */
    public void setCompTime(String compTime) {
        this.compTime = compTime;
    }

    /**
     * 作業が実施可能かどうかを返す。
     * 
     * @return true: 実施可能、false: 実施不可
     */
    public Boolean getImplement() {
        return implement;
    }

    /**
     * 作業実施を設定する。
     * 
     * @param implement true: 実施可能、false: 実施不可
     */
    public void setImplement(Boolean implement) {
        this.implement = implement;
    }
    
    /**
     * サービス情報(JSON)からプロダクト情報一覧を取り出す
     *
     * @param serviceInfosStr サービス情報(JSON)
     * @return プロダクト情報一覧
     */
    public static List<KanbanProduct> lookupProductList(String serviceInfosStr) {
        try {
            return lookupProductList(JsonUtils.jsonToObjects(serviceInfosStr, ServiceInfoEntity[].class), ServiceInfoEntity.SERVICE_INFO_PRODUCT);
        } catch (Exception ex) {
            return new ArrayList();
        }
    }
    
    /**
     * サービス情報(JSON)からプロダクト情報一覧を取り出す
     *
     * @param serviceInfosStr サービス情報(JSON)
     * @param serviceName サービス名
     * @return プロダクト情報一覧
     */
    public static List<KanbanProduct> lookupProductList(String serviceInfosStr, String serviceName) {
        try {
            return lookupProductList(JsonUtils.jsonToObjects(serviceInfosStr, ServiceInfoEntity[].class), serviceName);
        } catch (Exception ex) {
            return new ArrayList();
        }
    }

    /**
     * サービス情報一覧からプロダクト情報一覧を取り出す。
     *
     * @param serviceInfos サービス情報一覧
     * @param serviceName サービス名
     * @return プロダクト情報一覧
     */
    public static List<KanbanProduct> lookupProductList(List<ServiceInfoEntity> serviceInfos, String serviceName) {
        try {
            List<KanbanProduct> products = null;
            for (ServiceInfoEntity serviceInfo : serviceInfos) {
                if (Objects.equals(serviceInfo.getService(), serviceName) && Objects.nonNull(serviceInfo.getJob())) {
                    products = toKanbanProducts(serviceInfo);
                    break;
                }
            }
            return Objects.nonNull(products) ? products : new ArrayList();
        } catch (Exception ex) {
            return new ArrayList();
        }
    }
    
    /**
     * サービス情報からプロダクト情報一覧に変換する。
     * 
     * @param serviceInfo サービス情報
     * @return プロダクト情報一覧
     */
    public static List<KanbanProduct> toKanbanProducts(ServiceInfoEntity serviceInfo) {
        List<KanbanProduct> products = new ArrayList();
        List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) serviceInfo.getJob();
        for (LinkedHashMap<String, Object> map : list) {
            products.add(new KanbanProduct(map));
        }
        return products;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("KanbanProduct{")
                .append("uid=").append(this.uid)
                .append(", defect=").append(this.defect)
                .append(", orderNumber=").append(this.orderNumber)
                .append(", status=").append(this.status)
                .append(", startTime=").append(this.startTime)
                .append(", compTime=").append(this.compTime)
                .append(", implement=").append(this.implement)
                .append("}")
                .toString();
    }
}
