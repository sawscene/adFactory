/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adinterface.command;

import java.io.Serializable;
import jp.adtekfuji.adFactory.enumerate.LineManagedCommandEnum;

/**
 *
 * @author ke.yokoi
 */
public class LineTimerNoticeCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private LineManagedCommandEnum command;
    private Long monitorId;
    private Long lineId;
    private String message;
    private String modelName;

    public LineTimerNoticeCommand() {
    }

    public LineTimerNoticeCommand(LineManagedCommandEnum command, Long monitorId, Long equipmentId, String modelName) {
        this.command = command;
        this.monitorId = monitorId;
        this.lineId = equipmentId;
        this.modelName = modelName;
    }

    public LineManagedCommandEnum getCommand() {
        return command;
    }

    public void setCommand(LineManagedCommandEnum command) {
        this.command = command;
    }

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LineTimerNoticeCommand other = (LineTimerNoticeCommand) obj;
        return true;
    }

    @Override
    public String toString() {
        return "LineTimerNoticeCommand{" + "command=" + command + ", monitorId=" + monitorId + ", lineId=" + lineId + ", message=" + message + 
                ", modelName=" + modelName + '}';
    }
}
