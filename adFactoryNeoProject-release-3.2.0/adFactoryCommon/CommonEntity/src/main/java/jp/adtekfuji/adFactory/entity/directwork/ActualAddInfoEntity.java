package jp.adtekfuji.adFactory.entity.directwork;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * 直接工数実績の追加情報
 * 
 * @author yu.nara
 */
public class ActualAddInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("workReportWorkNum")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    WorkReportWorkNumEntity workReportWorkNum;

    @JsonProperty("manuallyAdded")
    boolean manuallyAdded;

    @JsonProperty("remove")
    boolean remove;

    /**
     * コンストラクタ
     */
    public ActualAddInfoEntity() {
    }

    /**
     * 
     * @return 
     */
    public WorkReportWorkNumEntity getWorkReportWorkNum() {
        return workReportWorkNum;
    }

    public void setWorkReportWorkNum(WorkReportWorkNumEntity workReportWorkNumEntity) {
        this.workReportWorkNum = workReportWorkNumEntity;
    }

    public boolean isManuallyAdded() {
        return manuallyAdded;
    }

    public void setManuallyAdded(boolean manuallyAdded) {
        this.manuallyAdded = manuallyAdded;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }
}
