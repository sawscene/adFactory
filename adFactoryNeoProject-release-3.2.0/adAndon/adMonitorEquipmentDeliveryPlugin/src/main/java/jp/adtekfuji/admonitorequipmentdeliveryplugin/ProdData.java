/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorequipmentdeliveryplugin;

import java.util.ArrayList;
import java.util.List;
import jp.adtekfuji.andon.entity.MonitorEquipmentStatusInfoEntity;
import static jp.adtekfuji.andon.enumerate.MonitorStatusEnum.READY;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;

/**
 * 生産データ
 *
 * @author s-heya
 */
public class ProdData {
    private WorkEquipmentSetting setting;
    private int prodCount;
    private final long taktTime;
    private final List<MonitorEquipmentStatusInfoEntity> statuses = new ArrayList<>();
    private boolean called;

    public ProdData(WorkEquipmentSetting setting, long workTime) {
        this.setting = setting;
        this.taktTime = 0 < this.setting.getPlanNum() ? workTime / this.setting.getPlanNum() : 0;
    }

    public WorkEquipmentSetting getSetting() {
        return setting;
    }

    public void setSetting(WorkEquipmentSetting setting) {
        this.setting = setting;
    }

    public int getProdCount() {
        return prodCount;
    }

    public void setProdCount(int prodCount) {
        this.prodCount = prodCount;
    }

    public long getTaktTime() {
        return this.taktTime;
    }

    public List<MonitorEquipmentStatusInfoEntity> getStatuses() {
        return statuses;
    }

    /**
     * 設備ステータスを取得する。
     *
     * @return
     */
    public MonitorEquipmentStatusInfoEntity getDisplayStatus() {
        if (this.statuses.isEmpty()) {
            return new MonitorEquipmentStatusInfoEntity().status(READY).fontColor("#FFFFFF").backColor("#000000");
        }
        MonitorEquipmentStatusInfoEntity.sort(this.statuses);
        return this.statuses.get(0);
    }

    public boolean isCalled() {
        return called;
    }

    public void setCalled(boolean called) {
        this.called = called;
    }

    @Override
    public String toString() {
        return "ProdData{" + "setting=" + setting + ", prodCount=" + prodCount + ", taktTime=" + taktTime + ", statuses=" + statuses + ", called=" + called + '}';
    }
}
