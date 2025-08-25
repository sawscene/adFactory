/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

import adtekfuji.property.AdProperty;
import adtekfuji.serialcommunication.SerialCommunication;
import adtekfuji.serialcommunication.SerialCommunicationListener;
import java.util.Objects;
import java.util.Properties;
import javafx.scene.control.TreeItem;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ta.ito
 */
public class WorkPlanKanbanEditPermanenceData {

    private final Logger logger = LogManager.getLogger();
    private final Properties properties = AdProperty.getProperties();
    private static WorkPlanKanbanEditPermanenceData instance = null;
    private SerialCommunication serialComm = null;

    private TreeItem<KanbanHierarchyInfoEntity> kanbanHierarchyRootItem;
    private TreeItem<KanbanHierarchyInfoEntity> selectedKanbanHierarchy;

    private WorkPlanKanbanEditPermanenceData() {
    }

    public static WorkPlanKanbanEditPermanenceData getInstance() {
        if (Objects.isNull(instance)) {
            instance = new WorkPlanKanbanEditPermanenceData();
        }
        return instance;
    }

    public void updateWorkHierarchy() {
    }

    public TreeItem<KanbanHierarchyInfoEntity> getKanbanHierarchyRootItem() {
        return kanbanHierarchyRootItem;
    }

    public void setKanbanHierarchyRootItem(TreeItem<KanbanHierarchyInfoEntity> kanbanRootItem) {
        this.kanbanHierarchyRootItem = kanbanRootItem;
    }

    public TreeItem<KanbanHierarchyInfoEntity> getSelectedKanbanHierarchy() {
        return selectedKanbanHierarchy;
    }

    public void setSelectedWorkHierarchy(TreeItem<KanbanHierarchyInfoEntity> selectedKanbanHierarchy) {
        this.selectedKanbanHierarchy = selectedKanbanHierarchy;
    }

    public SerialCommunication getSerialComm() {
        return serialComm;
    }

    public void setSerialComm(SerialCommunication serialComm) {
        this.serialComm = serialComm;
    }

    private static final String SERIAL_PORT_KEY = "serialPort";
    private static final String SERIAL_BANDRATE_KEY = "serialBaudRate";
    private static final String SERIAL_DATABIT_KEY = "serialDataBit";
    private static final String SERIAL_STOPBIT_KEY = "serialStopBit";
    private static final String SERIAL_PARITYBIT_KEY = "serialParityBit";

    /**
     * COMポート接続
     *
     * @param listener
     * @throws Exception
     */
    public void connectSerialComm(SerialCommunicationListener listener) throws Exception {
        try {

            if (!properties.containsKey(SERIAL_PORT_KEY)) {
                properties.setProperty(SERIAL_PORT_KEY, "COM1");
            }
            if (!properties.containsKey(SERIAL_BANDRATE_KEY)) {
                properties.setProperty(SERIAL_BANDRATE_KEY, "9600");
            }
            if (!properties.containsKey(SERIAL_DATABIT_KEY)) {
                properties.setProperty(SERIAL_DATABIT_KEY, "8");
            }
            if (!properties.containsKey(SERIAL_STOPBIT_KEY)) {
                properties.setProperty(SERIAL_STOPBIT_KEY, "1");
            }
            if (!properties.containsKey(SERIAL_PARITYBIT_KEY)) {
                properties.setProperty(SERIAL_PARITYBIT_KEY, "0");
            }

            String serialPort = properties.getProperty(SERIAL_PORT_KEY);
            if (Objects.nonNull(serialPort) && !serialPort.isEmpty()) {
                serialComm = SerialCommunication.getIncetance(serialPort, Integer.parseInt(properties.getProperty(SERIAL_BANDRATE_KEY)),
                        Integer.parseInt(properties.getProperty(SERIAL_DATABIT_KEY)), Integer.parseInt(properties.getProperty(SERIAL_STOPBIT_KEY)),
                        Integer.parseInt(properties.getProperty(SERIAL_PARITYBIT_KEY)));
            }
            if (Objects.nonNull(serialComm)) {
                //接続開始
                logger.info("SerialPort Connect:" + getClass().getName());
                serialComm.setListener(listener);
                serialComm.connect();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            serialComm.disconect();
            serialComm = null;
            throw ex;
        }
    }

    /**
     * COMポート切断
     *
     * @throws Exception
     */
    public void disconnectSerialComm() throws Exception {
        try {
            if (Objects.nonNull(serialComm)) {
                logger.info("SerialPort Disconnect:" + getClass().getName());
                serialComm.disconect();
                serialComm = null;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }
}
