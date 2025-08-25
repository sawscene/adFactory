/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.guitesttool.autotest;

import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.EquipmentInfoFacade;
import java.util.List;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class LineFacade {

    private static final Logger logger = LogManager.getLogger();
    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final long MAX_LOAD_SIZE = ClientServiceProperty.getRestRangeNum();
    private final CashManager cashManager = CashManager.getInstance();
    private EquipmentInfoEntity selectLine = null;

    public LineFacade() {
    }

    public void initialRead() {
        //設備一覧取得.
        long max = equipmentInfoFacade.count();
        cashManager.setNewCashList(EquipmentInfoEntity.class);
        for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
            List<EquipmentInfoEntity> entitys = equipmentInfoFacade.findRange(count, count + MAX_LOAD_SIZE - 1);
            entitys.stream().forEach((entity) -> {
                cashManager.setItem(EquipmentInfoEntity.class, entity.getEquipmentId(), entity);
            });
        }
    }

    public EquipmentInfoEntity getSelectLine() {
        return selectLine;
    }

    public void setSelectLine(EquipmentInfoEntity selectLine) {
        this.selectLine = selectLine;
    }


}
