package jp.adtekfuji.adFactory.entity.directwork;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

/**
 * 作業情報
 * 
 * @author yu.nara
 */
public class WorkReportWorkNumEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("controlNo")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> controlNo = null; // 管理番号

    @JsonProperty("selectedControlNo")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> selectedControlNo = null; // 有効管理番号

    @JsonProperty("digit")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer digit = null;   // 管理番号の桁数

    @JsonProperty("remarks1")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String remarks1;        // 備考1
                                    // 浜井産業様の場合、不具合箇所が設定されます。
    @JsonProperty("remarks2")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String remarks2;        // 備考2
                                    // 浜井産業様の場合、作業理由が設定されます。
    @JsonProperty("resources")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resources;       // 装置番号
    
    @JsonProperty("order")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double orderNum;        // 指示数

    @JsonProperty("final")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double finalNum;        // 完成数

    @JsonProperty("defect")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double defectNum;       // 不良数

    @JsonProperty("stop")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer stopTime;       // 装置停止時間
    
    @JsonProperty("listNo")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String listNo;          // リスト番号
    
    @JsonProperty("resourceType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resourceType;    // 資源タイプ

    @JsonProperty("workNo")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String workNo;          // 工程コード
   
    @JsonProperty("workSetup")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean workSetup;      // 段取り
    
    /**
     * コンストラクタ
     */
    public WorkReportWorkNumEntity() {}

    /**
     * 製造番号一覧を取得する。
     * 
     * @return 製造番号
     */
    public List<String> getControlNo() {
        return controlNo;
    }

    /**
     * 管理番号を設定する。
     * 
     * @param controlNo 管理番号 
     */
    public void setControlNo(List<String> controlNo) {
        this.controlNo = controlNo;
    }

    /**
     * 有効管理番号を取得する。
     * 
     * @return 有効管理番号
     */
    public List<String> getSelectedControlNo() {
        return selectedControlNo;
    }

    /**
     * 有効管理番号を設定する。
     * 
     * @param selectedControlNo 有効管理番号
     */
    public void setSelectedControlNo(List<String> selectedControlNo) {
        this.selectedControlNo = selectedControlNo;
    }

    /**
     * 桁数を取得する。
     * 
     * @return 桁数 
     */
    public Integer getDigit() {
        return digit;
    }

    /**
     * 桁数を設定する。
     * 
     * @param digit 桁数
     */
    public void setDigit(Integer digit) {
        this.digit = digit;
    }

    /**
     * 備考1を取得する。
     * 
     * @return 備考1 
     */
    public String getRemarks1() {
        return remarks1;
    }

    /**
     * 備考1を設定する。
     * 
     * @param remarks1 備考1 
     */
    public void setRemarks1(String remarks1) {
        this.remarks1 = remarks1;
    }

    /**
     * 備考2を取得する。
     * 
     * @return 備考2
     */
    public String getRemarks2() {
        return remarks2;
    }

    /**
     * 備考2を設定する。
     * 
     * @param remarks2 備考2
     */
    public void setRemarks2(String remarks2) {
        this.remarks2 = remarks2;
    }

    /**
     * 装置番号を取得する。
     * 
     * @return 装置番号
     */
    public String getResources() {
        return resources;
    }

    /**
     * 装置番号を設定する。
     * 
     * @param resources 装置番号
     */
    public void setResources(String resources) {
        this.resources = resources;
    }

    /**
     * 指示数を取得する。
     * 
     * @return 
     */
    public Double getOrderNum() {
        return orderNum;
    }

    /**
     * 指示数を設定する。
     * 
     * @param orderNum 
     */
    public void setOrderNum(Double orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * 完成数を取得する。
     * 
     * @return 完成数
     */
    public Double getFinalNum() {
        return finalNum;
    }

    /**
     * 完成数を設定する。
     * 
     * @param finalNum 完成数
     */
    public void setFinalNum(Double finalNum) {
        this.finalNum = finalNum;
    }

    /**
     * 不良数を取得する。
     * 
     * @return 不良数
     */
    public Double getDefectNum() {
        return defectNum;
    }

    /**
     * 不良数を設定する。
     * 
     * @param defectNum 不良数
     */
    public void setDefectNum(Double defectNum) {
        this.defectNum = defectNum;
    }

    /**
     * 装置停止時間を取得する。
     * 
     * @return 装置停止時間
     */
    public Integer getStopTime() {
        return stopTime;
    }

    /**
     * 装置停止時間を設定する。
     * 
     * @param stopTime 装置停止時間
     */
    public void setStopTime(Integer stopTime) {
        this.stopTime = stopTime;
    }

    /**
     * リスト番号を取得する。
     * 
     * @return リスト番号
     */
    public String getListNo() {
        return listNo;
    }

    /**
     * リスト番号を設定する。
     * 
     * @param listNo リスト番号
     */
    public void setListNo(String listNo) {
        this.listNo = listNo;
    }

    /**
     * 資源タイプを取得する。
     * 
     * @return 資源タイプ
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * 資源タイプを設定する。
     * 
     * @param resourceType 資源タイプ
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * 工程コードを取得する。
     * 
     * @return 工程コード
     */
    public String getWorkNo() {
        return workNo;
    }

    /**
     * 工程コードを設定する。
     * 
     * @param workNo 工程コード
     */
    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    /**
     * 段取りを取得する。
     * 
     * @return 
     */
    public Boolean getWorkSetup() {
        return workSetup;
    }

    /**
     * 段取りを設定する。
     * 
     * @param workSetup 
     */
    public void setWorkSetup(Boolean workSetup) {
        this.workSetup = workSetup;
    }

    
}
