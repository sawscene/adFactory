/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.dialog;

import java.util.LinkedList;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;

/**
 * プロパティ画面用エンティティ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class PropertySettingDialogEntity<E> {

    private ObjectProperty<LinkedList<E>> propertysProperty;

    private LinkedList<E> propertys = new LinkedList<>();

    public PropertySettingDialogEntity() {
    }

    public PropertySettingDialogEntity(LinkedList<E> propertyList) {
        this.propertys = propertyList;
    }

    public LinkedList<E> getPropertys() {
        if (Objects.nonNull(propertysProperty)) {
            return propertysProperty.get();
        }
        return propertys;
    }

    public void setPropertys(LinkedList<E> propertyList) {
        if (Objects.nonNull(propertysProperty)) {
            propertysProperty.set(propertys);
        } else {
            this.propertys = propertyList;
        }
        this.propertys = propertyList;
    }

    public void Update() {
        this.propertys = getPropertys();
    }
}
