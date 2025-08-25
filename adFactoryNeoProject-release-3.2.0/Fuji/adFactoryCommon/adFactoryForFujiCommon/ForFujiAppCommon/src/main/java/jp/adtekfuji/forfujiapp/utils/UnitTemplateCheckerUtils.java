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
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplatePropertyInfoEntity;

/**
 * ユニットテンプレート内の入力漏れチェッククラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTemplateCheckerUtils {

    private final static int REGEX_NAME_NUMBER = 256;

    /**
     * ユニットテンプレートのプロパティ情報で入力漏れがないか確認
     *
     * @param entitys ユニットテンプレートプロパティ
     * @return 成否 true:入力漏れ無/false:入力漏れ有
     * @since 2016.10.26.Wen
     */
    public static CheckerUtilEntity checkEmptyUnitTemplateProp(List<UnitTemplatePropertyInfoEntity> entitys) {
        if (checkUnitTemplateProperty(entitys)) {
            return new CheckerUtilEntity(Boolean.FALSE, Alert.AlertType.WARNING, "key.Warning", "key.NotInputMessage");
        }
        return new CheckerUtilEntity(Boolean.TRUE, null, null, "");
    }

    /**
     * ユニットテンプレート登録前の確認
     *
     * @param template 登録するユニットテンプレート
     * @param name 登録するユニットテンプレートの名前
     * @return 成否 true:入力漏れ無/false:入力漏れ有
     * @since 2017.01.25.Wen
     */
    public static CheckerUtilEntity checkRegistUnitTemplate(UnitTemplateInfoEntity template, String name) {
        if (checkUnitTmeplateName(name)) {
            return new CheckerUtilEntity(Boolean.FALSE, Alert.AlertType.WARNING, "key.warn.inputRequired", "key.warn.notInputUnitTmeplateName");
        }
        if (name.length() > REGEX_NAME_NUMBER) {
            return new CheckerUtilEntity(Boolean.FALSE, Alert.AlertType.WARNING, "key.Warning", "key.warn.enterCharacters256");
        }
        if (checkUnitTmeplateOutputKanbanHierarchyId(template.getFkOutputKanbanHierarchyId())) {
            return new CheckerUtilEntity(Boolean.FALSE, Alert.AlertType.WARNING, "key.warn.inputRequired", "key.warn.notInputOutputKanbanHierarchy");
        }
        if (checkUnitTemplateProperty(template.getUnitTemplatePropertyCollection())) {
            return new CheckerUtilEntity(Boolean.FALSE, Alert.AlertType.WARNING, "key.Warning", "key.NotInputMessage");
        }

        return new CheckerUtilEntity(Boolean.TRUE, null, null, "");
    }

    /**
     * 名前入力確認処理
     *
     * @param name 名前
     * @return 成否 true:入力漏れ有/false:入力漏れ無
     */
    private static Boolean checkUnitTmeplateOutputKanbanHierarchyId(Long id) {
        if (Objects.isNull(id)) {
            return true;
        } else if (id.equals(0l)) {
            return true;
        }

        return false;
    }

    /**
     * 名前入力確認処理
     *
     * @param name 名前
     * @return 成否 true:入力漏れ有/false:入力漏れ無
     */
    private static Boolean checkUnitTmeplateName(String name) {
        if (Objects.isNull(name)) {
            return true;
        } else if (name.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * プロパティ入力確認処理
     *
     * @param entitys プロパティ
     * @return 成否 true:入力漏れ有/false:入力漏れ無
     */
    private static Boolean checkUnitTemplateProperty(List<UnitTemplatePropertyInfoEntity> entitys) {
        for (UnitTemplatePropertyInfoEntity entity : entitys) {
            entity.updateMember();
            if (StringUtils.isEmpty(entity.getUnitTemplatePropertyName())
                    || Objects.isNull(entity.getUnitTemplatePropertyType())) {

                return true;
            }
        }
        return false;
    }

}
