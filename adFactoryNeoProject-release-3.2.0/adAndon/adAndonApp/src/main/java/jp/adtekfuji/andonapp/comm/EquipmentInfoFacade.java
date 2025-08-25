/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andonapp.comm;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.property.AdProperty;
import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import java.net.InetAddress;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginRequest;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 設備情報取得用RESTクラス
 *
 * @author e-mori
 */
public class EquipmentInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String LOGIN_PATH = "/equipment/login";

    public EquipmentInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * 設備ログイン
     *
     * @return 設備ログイン結果
     * @throws java.lang.Exception
     */
    public EquipmentLoginResult login() throws Exception {
        String equipmentIdName = AdProperty.getProperties().getProperty("equipmentIdName");
        
        if (Objects.nonNull(equipmentIdName) && !equipmentIdName.isEmpty()) {
            return loginByIdName();
        } else {
            return loginByAddress();
        }
    }

    /**
     * 設備識別名で設備ログイン
     *
     * @return 設備ログイン結果
     * @throws java.lang.Exception
     */
    private EquipmentLoginResult loginByIdName() throws Exception {
        String equipmentIdName = AdProperty.getProperties().getProperty("equipmentIdName");
        EquipmentLoginRequest request = EquipmentLoginRequest.identNameType(EquipmentTypeEnum.MONITOR, equipmentIdName);
        logger.info("equipment login:{}", request);
        return (EquipmentLoginResult) restClient.put(LOGIN_PATH, request, EquipmentLoginResult.class);
    }

    /**
     * IPアドレスで設備ログイン
     *
     * @return 設備ログイン結果
     * @throws java.lang.Exception
     */
    private EquipmentLoginResult loginByAddress() throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        EquipmentLoginRequest request = EquipmentLoginRequest.ip4AddressType(EquipmentTypeEnum.MONITOR, addr.getHostAddress());
        logger.info("equipment login:{}", request);
        return (EquipmentLoginResult) restClient.put(LOGIN_PATH, request, EquipmentLoginResult.class);
    }

}
