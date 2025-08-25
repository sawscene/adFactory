package jp.adtekfuji.adfactoryserver.entity.view;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workReportList")
public class WorkReportListEntity implements Serializable {


    @XmlElementWrapper(name = "workReportList")
    @XmlElement(name = "workReport")
    private List<WorkReportEntity> workReportEntityList; // 作業日報リスト

    public WorkReportListEntity() {
    }

    /**
     * 作業日報リストを取得
     * @return
     */
    public List<WorkReportEntity> getWorkReportEntityList() {
        return workReportEntityList;
    }

    /**
     * 作業日報リストを設定
     * @param workReportEntityList
     */
    public void setWorkReportEntityList(List<WorkReportEntity> workReportEntityList) {
        this.workReportEntityList = workReportEntityList;
    }
}
