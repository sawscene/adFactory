/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ユニットの予実情報並列
 * 
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.11.24.thr
 */
public class UnitAgendaConcurrentEntity {

    private List<UnitAgendaItemEntity> itemCollection = new ArrayList<>();

    public UnitAgendaConcurrentEntity() {
    }

    public List<UnitAgendaItemEntity> getItemCollection() {
        return itemCollection;
    }

    public void setItemCollection(List<UnitAgendaItemEntity> itemCollection) {
        this.itemCollection = itemCollection;
    }

    public UnitAgendaConcurrentEntity addItem(UnitAgendaItemEntity item) {
        this.itemCollection.add(item);
        return this;
    }

    public UnitAgendaConcurrentEntity addItems(UnitAgendaItemEntity... items) {
        this.itemCollection.addAll(Arrays.asList(items));
        return this;
    }
}
