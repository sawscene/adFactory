/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package adtekfuji.admanagerapp.systemsettingplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import jp.adtekfuji.adFactory.entity.master.ReasonInfoEntity;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * 
 * @author
 */
public class ReasonRecordFactory extends AbstractRecordFactory<ReasonInfoEntity>{

	private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    
    public ReasonRecordFactory(Table table, LinkedList<ReasonInfoEntity> entitys) {
        super(table, entitys);
    }
	@Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();

        //タイトルに該当するカラムを追加する
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.EditCallReasonTitle") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));

        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    @Override
    protected Record createRecord(ReasonInfoEntity entity) {
        Record record = new Record(super.getTable(), true);
        List<AbstractCell> cells = new ArrayList<>();
		//Callback<ListView<ReasonTypeEnum>, ListCell<ReasonTypeEnum>> comboCellFactory = (ListView<ReasonTypeEnum> param) -> new ReasonTypeComboBoxCellFactory();

		record.setEditableOrder(true);
        //理由
        cells.add(new CellTextField(record, entity.reasonProperty()).addStyleClass("ContentTextBox"));

        record.setCells(cells);

        return record;
    }

    @Override

    public Class getEntityClass() {
        return ReasonInfoEntity.class;
    }
}
