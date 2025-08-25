/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.WarehousePropertyEnum;

/**
 *
 * @author ke.yokoi
 */
public class PayoutPlanInfo {

    private final BooleanProperty printed = new SimpleBooleanProperty();
    private KanbanInfoEntity kanban;

    private final Map<String, StringProperty> properties = new HashMap<>();

    public PayoutPlanInfo() {
    }

    public BooleanProperty printedProperty() {
        return printed;
    }

    public Boolean getPrinted() {
        return printed.get();
    }

    public void setPrinted(Boolean labelPrinted) {
        this.printed.set(labelPrinted);
    }

    public KanbanInfoEntity getKanban() {
        return kanban;
    }

    public void setKanban(KanbanInfoEntity kanban) {
        if (Objects.nonNull(kanban.getPropertyCollection())) {
             for (KanbanPropertyInfoEntity prop : kanban.getPropertyCollection()) {
                this.properties.put(prop.getKanbanPropertyName(), prop.kanbanPropValueProperty());
            }
        }

        this.kanban = kanban;
    }

    /**
     * プロパティを取得する
     *
     * @param key
     * @return
     */
    public StringProperty getPropertyValue(WarehousePropertyEnum key) {
        String keyName = key.getName();

        if (this.properties.containsKey(keyName)) {
            return properties.get(keyName);
        }

        return null;
    }
}
