/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingpluginels.common;

import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author e-mori
 */
public class BreakTimeIdData {

    private ObjectProperty<Long> idProperty;
    private Long id;

    public BreakTimeIdData() {
    }

    public BreakTimeIdData(Long id) {
        this.id = id;
    }

    public ObjectProperty<Long> getIdProperty() {
        if (Objects.isNull(idProperty)) {
            idProperty = new SimpleObjectProperty<>(id);
        }
        return idProperty;
    }

    public Long getId() {
        if (Objects.nonNull(idProperty)) {
            return idProperty.get();
        }
        return id;
    }

    public void setId(Long id) {
        if (Objects.nonNull(idProperty)) {
            idProperty.set(id);
        } else {
            this.id = id;
        }
    }
}
