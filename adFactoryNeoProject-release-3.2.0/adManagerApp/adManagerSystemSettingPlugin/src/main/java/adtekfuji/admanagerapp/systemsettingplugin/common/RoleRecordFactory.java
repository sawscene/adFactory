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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import jp.adtekfuji.adFactory.entity.master.RoleAuthorityInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellCheckBox;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 *
 * @author e.mori
 */
public class RoleRecordFactory extends AbstractRecordFactory<RoleAuthorityInfoEntity> {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    public RoleRecordFactory(Table table, LinkedList<RoleAuthorityInfoEntity> entitys) {
        super(table, entitys);
    }

    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.EditRoleTitle") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));
        for (RoleAuthorityType type : RoleAuthorityTypeEnum.types()) {
            cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString(type.getResourceKey()))).addStyleClass("ContentTitleLabel"));
        }
        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    @Override
    protected Record createRecord(RoleAuthorityInfoEntity entity) {
        Record record = new Record(super.getTable(), true);
        record.setRemoveButtonInsets(new Insets(0, 0, 0, 20)); // 削除ボタンのマージンを設定する
        List<AbstractCell> cells = new ArrayList<>();
        cells.add(new CellTextField(record, entity.roleNameProperty()).addStyleClass("ContentTextBox"));
        for (RoleAuthorityType type : RoleAuthorityTypeEnum.types()) {
            
            switch(type.getName()){
            
                case "REFERENCE_RESOOURCE":
                    cells.add(new CellCheckBox(record, "", entity.resourceReferenceProperty(), HPos.CENTER));
                        break;
                case "EDITED_RESOOURCE":
                    cells.add(new CellCheckBox(record, "", entity.resourceEditProperty(), HPos.CENTER));
                        break;
                case "REFERENCE_KANBAN":
                    cells.add(new CellCheckBox(record, "", entity.kanbanReferenceProperty(), HPos.CENTER));
                        break;
                case "MAKED_KANBAN":
                    cells.add(new CellCheckBox(record, "", entity.kanbanCreateProperty(), HPos.CENTER));
                        break;                        
                case "REFERENCE_WORKFLOW":
                    cells.add(new CellCheckBox(record, "", entity.workflowReferenceProperty(), HPos.CENTER));
                        break;
                case "EDITED_WORKFLOW":
                    cells.add(new CellCheckBox(record, "", entity.workflowEditProperty(), HPos.CENTER));
                        break;
                case "OUTPUT_ACTUAL":
                    cells.add(new CellCheckBox(record, "", entity.actualOutputProperty(), HPos.CENTER));
                        break;                        
                case "MANAGED_LINE":
                    cells.add(new CellCheckBox(record, "", entity.lineManageProperty(), HPos.CENTER));
                        break;                        
                case "DELETE_ACTUAL":
                    cells.add(new CellCheckBox(record, "", entity.actualDelProperty(), HPos.CENTER));
                        break; 
                case "RIGHT_ACCESS":
                    cells.add(new CellCheckBox(record, "", entity.accessEditProperty(), HPos.CENTER));
                        break;
                case "APPROVAL_KANBAN": // 承認権限
                    cells.add(new CellCheckBox(record, "", entity.approveProperty(), HPos.CENTER));
                        break;  
                default:
                    break;
            }
        }                              
        record.setCells(cells);
        return record;
    }

    @Override
    public Class getEntityClass() {
        return RoleAuthorityInfoEntity.class;
    }
}
