package jp.adtekfuji.adFactory.entity.view;

import jakarta.xml.bind.annotation.*;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workReportList")
public class WorkReportInfoListEntity {

    @XmlElementWrapper(name = "workReportList")
    @XmlElement(name = "workReport")
    private List<WorkReportInfoEntity> workReportInfoEntityList; //作業日報リスト

    public WorkReportInfoListEntity() {
    }


    public WorkReportInfoListEntity(List<WorkReportInfoEntity> workReportInfoEntityList) {
        this.workReportInfoEntityList = workReportInfoEntityList;
    }

    /**
     * 作業日報リストを取得
     * @return 作業日報
     */
    public List<WorkReportInfoEntity> getWorkReportInfoEntityList() {
        return workReportInfoEntityList;
    }

    /**
     * 作業日報リストを設定
     * @param workReportInfoEntityList
     */
    public void setWorkReportInfoEntityList(List<WorkReportInfoEntity> workReportInfoEntityList) {
        this.workReportInfoEntityList = workReportInfoEntityList;
    }
}
