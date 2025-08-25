/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.common;

import adtekfuji.locale.LocaleUtils;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import java.util.LinkedList;
import java.util.ResourceBundle;
import jp.adtekfuji.adFactory.entity.organization.AuthenticationInfoEntity;

/**
 *
 * @author e.mori
 */
public class AutorityTypeRecordFactory extends AbstractRecordFactory<AuthenticationInfoEntity>{
       
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    public AutorityTypeRecordFactory(Table table, LinkedList<AuthenticationInfoEntity> entitys) {
        super(table, entitys);
    }

    @Override
    protected Record createRecord(AuthenticationInfoEntity entity) {
        Record record = new Record(super.getTable(), true);

        return record; 
    }

    @Override
    public Class getEntityClass() {
        return AuthenticationInfoEntity.class;
    }
}
