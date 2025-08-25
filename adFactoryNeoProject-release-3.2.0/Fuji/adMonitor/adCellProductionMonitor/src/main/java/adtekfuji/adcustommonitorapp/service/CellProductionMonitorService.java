/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.service;

import jp.adtekfuji.forfujiapp.clientnativeservice.AdInterfaceMonitorClientService;
import jp.adtekfuji.forfujiapp.clientnativeservice.NoticeCommandListner;
import adtekfuji.fxscene.SceneContiner;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * セル生産進捗モニタネイティブサービス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.8.thr
 */
public class CellProductionMonitorService implements NoticeCommandListner {

    private static final Logger logger = LogManager.getLogger();
    private static CellProductionMonitorService instance = null;
    private final AdInterfaceMonitorClientService adInterfaceClientService = new AdInterfaceMonitorClientService();
    private List<CellProductionMonitorServiceInterface> monitorServiceInterfaces = new ArrayList<>();

    private CellProductionMonitorService() {
        adInterfaceClientService.getHandler().setNoticeListner(this);
    }

    public static CellProductionMonitorService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new CellProductionMonitorService();
        }
        return instance;
    }

    @Override
    public void notice(Object command) {
        logger.info("notice:{}", command);
        if (command instanceof ActualNoticeCommand) {
            ActualNoticeCommand actualNoticeCommand = (ActualNoticeCommand) command;
            for(CellProductionMonitorServiceInterface service:monitorServiceInterfaces){
                service.receivedActualDataKanbanId(actualNoticeCommand.getKanbanId());
            }
        }
        SceneContiner sc = SceneContiner.getInstance();
    }

    public void startService() {
        logger.info("startService");
        adInterfaceClientService.startService();
    }

    public void stopService() throws InterruptedException {
        logger.info("stopService start.");
        adInterfaceClientService.stopService();
        SceneContiner sc = SceneContiner.getInstance();
        logger.info("stopService end.");
    }

    public void setCellProductionMonitorServiceInterfaces(List<CellProductionMonitorServiceInterface> monitorServiceInterfaces) {
        this.monitorServiceInterfaces.clear();
        this.monitorServiceInterfaces = monitorServiceInterfaces;
    }

    public void addCellProductionMonitorServiceInterface(CellProductionMonitorServiceInterface monitorServiceInterface) {
        this.monitorServiceInterfaces.add(monitorServiceInterface);
    }

    public void addCellProductionMonitorServiceInterfaces(List<CellProductionMonitorServiceInterface> monitorServiceInterfaces) {
        this.monitorServiceInterfaces.addAll(monitorServiceInterfaces);
    }

    public void clearCellProductionMonitorServiceInterfaces() {
        this.monitorServiceInterfaces.clear();
    }

    public List<CellProductionMonitorServiceInterface> getMonitorServiceInterfaces() {
        return monitorServiceInterfaces;
    }

}
