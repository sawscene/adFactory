/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.common;

import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.property.AdProperty;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginRequest;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class AndonLoginFacade {

    private AndonLoginFacade() {
    }

    public static Long getMonitorId() {
        String equipmentIdName = AdProperty.getProperties().getProperty("equipmentIdName");

        if (Objects.nonNull(equipmentIdName) && !equipmentIdName.isEmpty()) {
            return getMonitorIdByIdName();
        } else {
            return getMonitorIdByIp();
        }
    }

    private static Long getMonitorIdByIdName() {
        final Logger logger = LogManager.getLogger();
        final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
        Long monitorId = 0L;

        String equipmentIdName = AdProperty.getProperties().getProperty("equipmentIdName");
        EquipmentLoginRequest request = EquipmentLoginRequest.identNameType(EquipmentTypeEnum.MONITOR, equipmentIdName);
        EquipmentLoginResult result = equipmentInfoFacade.login(request);
        logger.info("EquipmentLoginResult:{}", result);
        if (result.getErrorType() == ServerErrorTypeEnum.SUCCESS) {
            monitorId = result.getEquipmentId();
        }

        return monitorId;
    }

    private static Long getMonitorIdByIp() {
        final Logger logger = LogManager.getLogger();
        final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
        Long monitorId = 0L;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            EquipmentLoginRequest request = EquipmentLoginRequest.ip4AddressType(EquipmentTypeEnum.MONITOR, addr.getHostAddress());
            EquipmentLoginResult result = equipmentInfoFacade.login(request);
            logger.info("EquipmentLoginResult:{}", result);
            if (result.getErrorType() == ServerErrorTypeEnum.SUCCESS) {
                monitorId = result.getEquipmentId();
            }
        } catch (UnknownHostException ex) {
            logger.fatal(ex, ex);
        }
        return monitorId;
    }
}
