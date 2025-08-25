/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.ledger.NameValueEntity;

/**
 *
 * @author ta-ito
 */
public class NameAndTagEntity {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty value = new SimpleStringProperty();

    public NameAndTagEntity() {
    }

    public NameAndTagEntity(NameValueEntity entity) {
        name.setValue(entity.getName());
        value.setValue(entity.getValue());
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty tagProperty() {
        return value;
    }

    public NameValueEntity getKeyTagEntity() {
        return new NameValueEntity(nameProperty().getValue(), tagProperty().getValue());
    }
}


