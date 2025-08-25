/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.javafx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * 工程テーブル用データクラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.01.Mon
 */
public class CheckTableData {

    private SimpleBooleanProperty isSelect = new SimpleBooleanProperty(Boolean.FALSE);
    private String name;

    public CheckTableData(String name) {
        this.name = name;
    }

    public CheckTableData(String name, Boolean isSelect) {
        this.name = name;
        this.isSelect.setValue(isSelect);
    }

    public BooleanProperty isSelectProperty() {
        return isSelect;
    }

    public void setIsSelectProperty(SimpleBooleanProperty property) {
        this.isSelect = property;
    }

    public Boolean getIsSelect() {
        return isSelect.getValue();
    }

    public void setIsSelect(Boolean isSelect) {
        this.isSelect.setValue(isSelect);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
