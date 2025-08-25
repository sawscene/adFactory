/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.entity;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringTime;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;

/**
 *
 * @author ta-ito
 */
public class WorkListTableDataEntity {

    private WorkInfoEntity workInfoEntity = new WorkInfoEntity();
    private String workName;
    private String updatePersonName;
    private String updateDatetime;

    public WorkListTableDataEntity(WorkInfoEntity entity) {
        this.workInfoEntity = entity;
        this.workName = entity.getWorkName();
        this.updateDatetime = Objects.isNull(entity.getUpdateDatetime()) ? "" : StringTime.convertDateToString(entity.getUpdateDatetime(), LocaleUtils.getString("key.DateTimeFormat"));
    }

    public WorkInfoEntity getWorkInfoEntity() {
        return workInfoEntity;
    }

    public void setWorkInfoEntity(WorkInfoEntity workInfoEntity) {
        this.workInfoEntity = workInfoEntity;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public void setUpdatePersonName(String updatePersonName) {
        this.updatePersonName = updatePersonName;
    }

    public String getUpdatePersonName() {
        return updatePersonName;
    }

    public void setUpdateDatetime(String updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public String getUpdateDatetime() {
        return updateDatetime;
    }

}
