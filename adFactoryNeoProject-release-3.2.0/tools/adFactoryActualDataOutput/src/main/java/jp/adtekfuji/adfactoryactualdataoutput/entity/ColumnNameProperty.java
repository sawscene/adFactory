/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryactualdataoutput.entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author ke.yokoi
 */
public class ColumnNameProperty {

    private final StringProperty inColumnNameProperty = new SimpleStringProperty();
    private final StringProperty outColumnNameProperty = new SimpleStringProperty();

    public ColumnNameProperty() {
    }

    public ColumnNameProperty(String columnName) {
        inColumnNameProperty.set(columnName);
        outColumnNameProperty.set(columnName);
    }

    public ColumnNameProperty(String inColumnName, String outColumnName) {
        inColumnNameProperty.set(inColumnName);
        outColumnNameProperty.set(outColumnName);
    }

    public StringProperty inColumnNameProperty() {
        return inColumnNameProperty;
    }

    public StringProperty outColumnNameProperty() {
        return outColumnNameProperty;
    }

    public String getInColumnName() {
        return inColumnNameProperty.get();
    }

    public void setInColumnName(String inColumnName) {
        inColumnNameProperty.set(inColumnName);
    }

    public String getOutColumnName() {
        return outColumnNameProperty.get();
    }

    public void setOutColumnName(String outColumnName) {
        outColumnNameProperty.set(outColumnName);
    }

}
