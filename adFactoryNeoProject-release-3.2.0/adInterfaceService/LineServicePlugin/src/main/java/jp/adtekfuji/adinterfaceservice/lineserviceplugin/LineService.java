/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.lineserviceplugin;

import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.utility.DateUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.search.EquipmentSearchCondition;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ライン制御サービス
 *
 * @author s-heya
 */
public class LineService implements AdInterfaceServiceInterface {

    private final String SERVICE_NAME = "LineService";

    private final Logger logger = LogManager.getLogger();
    private final AndonMonitorSettingFacade monitorSettingFacade = new AndonMonitorSettingFacade();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private final List<AndonMonitorLineProductSetting> settings = new ArrayList<>();
    private final Map<Long, Date> lines = new HashMap<>();

    private Timer timer;

    /**
     * サービスを開始する。
     *
     * @throws Exception
     */
    @Override
    public void startService() throws Exception {
        try {
            logger.info("startService start.");

            EquipmentInfoFacade equipmentFacade = new EquipmentInfoFacade();
            EquipmentSearchCondition condition = new EquipmentSearchCondition().equipmentType(EquipmentTypeEnum.MONITOR);
            List<EquipmentInfoEntity> monitors = equipmentFacade.findSearchRange(condition, 0L, 9999L);

            for (EquipmentInfoEntity monitor : monitors) {
                AndonMonitorLineProductSetting setting = (AndonMonitorLineProductSetting) monitorSettingFacade.getLineSetting(monitor.getEquipmentId(),AndonMonitorLineProductSetting.class);
                if (!Objects.equals(setting.getLineId(), 0L) && setting.isAutoCountdown() && !setting.isFollowStart()) {
                    settings.add(setting);
                }
            }

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        LocalDate now = LocalDate.now();
                        for (AndonMonitorLineProductSetting setting : settings) {
                            Date start = DateUtils.toDate(now, setting.getStartWorkTime());


                        }
                    }
                    catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }
            };

            this.timer = new Timer();
            this.timer.schedule(task, 1000L);
        }
        finally {
            logger.info("startService end.");
        }
    }

    /**
     * サービスを停止する。
     *
     * @throws Exception
     */
    @Override
    public void stopService() throws Exception {
        try {
            logger.info("stopService start.");

            this.timer.cancel();
        }
        finally {
            logger.info("stopService end.");
        }
    }

    /**
     * サービス名を取得する。
     *
     * @return
     */
    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}
