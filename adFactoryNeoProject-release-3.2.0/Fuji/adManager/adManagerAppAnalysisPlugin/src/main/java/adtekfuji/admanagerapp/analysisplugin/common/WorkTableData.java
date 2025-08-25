/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.common;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;

/**
 * 工程テーブル用データクラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.01.Mon
 */
public class WorkTableData {

    private SimpleBooleanProperty isSelect = new SimpleBooleanProperty(Boolean.FALSE);;
    private String workName;
    private WorkInfoEntity item;

    public WorkTableData(String workName, WorkInfoEntity item) {
        this.workName = workName;
        this.item = item;
    }
    
    public BooleanProperty isSelectProperty(){
        return isSelect;
    }
    
    public void setIsSelectProperty(SimpleBooleanProperty property){
        this.isSelect = property;
    }

    public Boolean getIsSelect() {
        return isSelect.getValue();
    }

    public String getWorkName() {
        return workName;
    }

    public WorkInfoEntity getItem() {
        return item;
    }

    public void setIsSelect(Boolean isSelect) {
        this.isSelect.setValue(isSelect);
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public void setItem(WorkInfoEntity item) {
        this.item = item;
    }

}
