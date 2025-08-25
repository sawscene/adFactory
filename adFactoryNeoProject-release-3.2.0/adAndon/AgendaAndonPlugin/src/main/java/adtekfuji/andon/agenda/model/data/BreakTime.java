/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.model.data;

import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * 休憩時間
 *
 * @author s-heya
 */
public class BreakTime {

    private ObjectProperty<Long> idProperty;
    private Long id;

    public BreakTime() {
    }

    public BreakTime(Long id) {
        this.id = id;
    }

    public ObjectProperty<Long> idProperty() {
        if (Objects.isNull(idProperty)) {
            idProperty = new SimpleObjectProperty<>(id);
        }
        return idProperty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }
}
