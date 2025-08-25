/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.utils;

import adtekfuji.utility.StringUtils;
import java.util.List;
import java.util.Objects;
import javafx.scene.control.Alert;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitPropertyInfoEntity;

/**
 * ユニット内の入力漏れチェッククラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.10.Thr
 */
public class UnitCheckerUtils {

    /**
     * 生産ユニット内に抜け漏れがないか確認する
     *
     * @param unit 確認したいユニット
     * @return true:抜け漏れなし/false:抜け漏れ有
     */
    public static CheckerUtilEntity isEmptyUnit(UnitInfoEntity unit) {
        // インスタンスの存在確認
        if (Objects.isNull(unit)) {
            return new CheckerUtilEntity(Boolean.FALSE, Alert.AlertType.WARNING, "key.Warning", "key.NotInputMessage");
        }
        if ((Objects.isNull(unit.getFkUnitTemplateId()) || (0L == unit.getFkUnitTemplateId()))
                || (Objects.isNull(unit.getUnitName()) || "".equals(unit.getUnitName()))
                || (Objects.isNull(unit.getStartDatetime()) || Objects.isNull(unit.getCompDatetime()))
                || checkUnitProperty(unit.getUnitPropertyCollection())) {
            return new CheckerUtilEntity(Boolean.FALSE, Alert.AlertType.WARNING, "key.Warning", "key.NotInputMessage");
        }

        return new CheckerUtilEntity(Boolean.TRUE, null, null, null);
    }

    /**
     * ユニットのプロパティ情報で入力漏れがないか確認
     *
     * @param entitys ユニットプロパティ
     * @return true:抜け漏れ有/false:抜け漏れ無
     */
    private static Boolean checkUnitProperty(List<UnitPropertyInfoEntity> entitys) {
        for (UnitPropertyInfoEntity entity : entitys) {
            entity.updateMember();
            if (StringUtils.isEmpty(entity.getUnitPropertyName())
                    || Objects.isNull(entity.getUnitPropertyType())) {
                return true;
            }
        }
        return false;
    }
}
