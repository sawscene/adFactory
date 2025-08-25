/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.treecell;

import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
import javafx.scene.control.TreeCell;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;

/**
 * ツリーセル表示用クラス
 *
 * @author e-mori
 */
public class EquipmentTreeCell extends TreeCell<EquipmentInfoEntity> {
    
    private boolean showLicense = false;        // ライセンス数表示フラグ
    private boolean isLiteOption = false;       // Lite オプション
    private boolean isReporterOption = false;   // Reporter オプション

    /**
     * コンストラクタ
     */
    public EquipmentTreeCell() {
    }

    /**
     * コンストラクタ
     * 
     * @param showLicense ライセンス数表示フラグ
     * @param isLiteOption Lite オプション
     * @param isReporterOption Reporter オプション
     */
    public EquipmentTreeCell(boolean showLicense, boolean isLiteOption, boolean isReporterOption) {
        this.showLicense = showLicense;
        this.isLiteOption = isLiteOption;
        this.isReporterOption = isReporterOption;
    }

    @Override
    protected void updateItem(EquipmentInfoEntity item, boolean empty) {
        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
        if (empty) {
            setText(null);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
        }
    }

    /**
     * 
     * @return 
     */
    private String getString() {
        if (Objects.nonNull((getItem()))) {
            EquipmentInfoEntity equipment = this.getItem();
            
            if (equipment.getChildCount() > 0) {
                return equipment.getString(showLicense, this.isLiteOption, this.isReporterOption);
            }
        
            StringBuilder sb = new StringBuilder();
            sb.append(equipment.getEquipmentName());
            
            if (showLicense
                    && Objects.nonNull(equipment.getEquipmentTypeEntity())
                    && Objects.nonNull(equipment.getEquipmentTypeEntity().getName())) {
                
                switch (equipment.getEquipmentTypeEntity().getName()) {
                    case LITE:
                        if (!this.isLiteOption) {
                            return sb.toString();
                        }
                        break;
                    case REPORTER:
                        if (!this.isReporterOption) {
                            return sb.toString();
                        }
                        break;
                }
                
                sb.append(" - ");
                sb.append(LocaleUtils.getString(equipment.getEquipmentTypeEntity().getName().getResourceKey()));
            }

            return sb.toString();
        }
        
        return "";
    }
    
}
