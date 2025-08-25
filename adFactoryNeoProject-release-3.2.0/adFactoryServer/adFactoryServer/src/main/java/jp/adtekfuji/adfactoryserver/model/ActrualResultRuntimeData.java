/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportEntity;
import jp.adtekfuji.adFactory.entity.operation.OperateAppEnum;

/**
 *
 * @author ke.yokoi
 */
public class ActrualResultRuntimeData {

    class Key {

        private final Long equipmentId;
        private final Long organizationId;
        private final String terminalIdentName;

        public Key(Long equipmentId, Long organizationId, String terminalIdentName) {
            this.equipmentId = equipmentId;
            this.organizationId = organizationId;
            this.terminalIdentName = terminalIdentName;
        }

        public long getEquipmentId() {
            return equipmentId;
        }

        public long getOrganizationId() {
            return organizationId;
        }

        public String getTerminalIdentName() {
            return terminalIdentName;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 13 * hash + Objects.hashCode(this.equipmentId);
            hash = 13 * hash + Objects.hashCode(this.organizationId);
            hash = 13 * hash + Objects.hashCode(this.terminalIdentName);
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
            final Key other = (Key) obj;
            if (!Objects.equals(this.equipmentId, other.equipmentId)) {
                return false;
            }
            if (!Objects.equals(this.organizationId, other.organizationId)) {
                return false;
            }
            if (!Objects.equals(this.terminalIdentName, other.terminalIdentName)) {
                return false;
            }
            return true;
        }

    }

    private static ActrualResultRuntimeData instance;
    private final Map<Key, Long> tidCollection = new HashMap<>();

    private ActrualResultRuntimeData() {
    }

    public static ActrualResultRuntimeData getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ActrualResultRuntimeData();
        }
        return instance;
    }

    /**
     * トランザクションIDを照合する
     * 
     * @param report 実績通知
     * @return true: 照合OK、false: 照合NG
     */
    public boolean checkTransactionId(ActualProductReportEntity report) {
        Key key = OperateAppEnum.ADPRODUCTLITE.equals(report.getOperateApp())
                ? new Key(report.getEquipmentId(), 0L, report.getTerminalIdentName())
                : new Key(report.getEquipmentId(), report.getOrganizationId(), report.getTerminalIdentName());
        if (tidCollection.containsKey(key)) {
            long now = tidCollection.get(key);
            if (report.getTransactionId() != 0 && now > report.getTransactionId()) {
                return false;
            }
        }
        return true;
    }

    public long forwardTransactionId(ActualProductReportEntity report) {
        Key key = OperateAppEnum.ADPRODUCTLITE.equals(report.getOperateApp())
                ? new Key(report.getEquipmentId(), 0L, report.getTerminalIdentName())
                : new Key(report.getEquipmentId(), report.getOrganizationId(), report.getTerminalIdentName());
        long next = report.getTransactionId() + 1;
        tidCollection.put(key, next);
        return next;
    }

    public long getNextTransactionId(ActualProductReportEntity report) {
        Key key = OperateAppEnum.ADPRODUCTLITE.equals(report.getOperateApp())
                ? new Key(report.getEquipmentId(), 0L, report.getTerminalIdentName())
                : new Key(report.getEquipmentId(), report.getOrganizationId(), report.getTerminalIdentName());
        return tidCollection.get(key);
    }

}
