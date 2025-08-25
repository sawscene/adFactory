/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingpluginfuji.common;

import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author ke.yokoi
 */
public class ReasonData {

    private ObjectProperty<String> reasonProperty;
    private String reason;

    public ReasonData() {
    }

    public ReasonData(String dealyReason) {
        this.reason = dealyReason;
    }

    public ObjectProperty<String> reasonProperty() {
        if (Objects.isNull(reasonProperty)) {
            reasonProperty = new SimpleObjectProperty<>(reason);
        }
        return reasonProperty;
    }

    public String getReason() {
        if (Objects.nonNull(reasonProperty)) {
            return reasonProperty.get();
        }
        return reason;
    }

    public void setReason(String reason) {
        if (Objects.nonNull(reasonProperty)) {
            reasonProperty.set(reason);
        } else {
            this.reason = reason;
        }
    }
}
