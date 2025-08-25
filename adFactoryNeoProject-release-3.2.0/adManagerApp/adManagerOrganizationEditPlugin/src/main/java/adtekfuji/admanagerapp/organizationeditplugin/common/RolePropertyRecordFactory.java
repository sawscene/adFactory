/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.organizationeditplugin.common;

import adtekfuji.cash.CashManager;
import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.master.RoleAuthorityInfoEntity;
import jp.adtekfuji.javafxcommon.property.CellLabel;

/**
 *
 * @author e.mori
 */
public class RolePropertyRecordFactory extends AbstractRecordFactory<RoleIdData> {

    /**
     * プロパティ情報型表示用セルクラス
     *
     */
    class RoleComboBoxCellFactory extends ListCell<Long> {

        @Override
        protected void updateItem(Long item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else if (item.equals(0l)) {
                setText("");
            } else {
                roles.stream().filter((e) -> (item.equals(e.getRoleId()))).forEach((e) -> {
                    setText(e.getRoleName());
                });
            }
        }
    }

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final CashManager cashManager = CashManager.getInstance();
    private final List<RoleAuthorityInfoEntity> roles = cashManager.getItemList(RoleAuthorityInfoEntity.class, new ArrayList());

    public RolePropertyRecordFactory(Table table, LinkedList<RoleIdData> entitys) {
        super(table, entitys);
    }

    @Override
    protected Record createRecord(RoleIdData entity) {
        Record record = new Record(super.getTable(), true);
        Callback<ListView<Long>, ListCell<Long>> comboCellFactory = (ListView<Long> param) -> new RoleComboBoxCellFactory();

        LinkedList<AbstractCell> cells = new LinkedList<>();
        List<Long> idDatas = new ArrayList<>();
        roles.stream().forEach((e) -> {
            idDatas.add(e.getRoleId());
        });

        if (roles.size() >= 1) {
            if (Objects.isNull(entity.getId())) {
                entity.setId(roles.get(0).getRoleId());
            }
            cells.add(new CellComboBox<>(record, idDatas, new RoleComboBoxCellFactory(), comboCellFactory, entity.getIdProperty()).addStyleClass("ContentComboBox"));
        } else {
            cells.add(new CellLabel(record, new SimpleStringProperty(String.format(LocaleUtils.getString("key.NotData"), LocaleUtils.getString("key.EditRoleTitle")))));
        }

        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return RoleIdData.class;
    }
}
