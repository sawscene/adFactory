/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentTypeEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 設備種別情報取得用RESTクラス
 *
 * @author e-mori
 */
public class EquipmentTypeInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String EQUIPMENT_TYPE_PATH = "/equipment-type";

    public EquipmentTypeInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * 設備種別情報一覧取得
     *
     * @return 組織種別情報一覧
     */
    public List<EquipmentTypeEntity> findAll() {
        logger.debug("findAll");
        try {
            return restClient.findAll(EQUIPMENT_TYPE_PATH, new GenericType<List<EquipmentTypeEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }



}
