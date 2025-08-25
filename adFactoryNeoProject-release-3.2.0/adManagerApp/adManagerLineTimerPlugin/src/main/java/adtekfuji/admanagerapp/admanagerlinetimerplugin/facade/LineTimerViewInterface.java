/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.admanagerlinetimerplugin.facade;

import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;

/**
 *
 * @author ke.yokoi
 */
public interface LineTimerViewInterface {

    public void updateInfo(EquipmentInfoEntity monitor, AndonMonitorLineProductSetting setting);

    public void setStartWait();

    public void setStartCount();

    public void setStartCountPause();

    public void setTaktCount();

    public void setTaktCountPause();

    public void setStop();

}
