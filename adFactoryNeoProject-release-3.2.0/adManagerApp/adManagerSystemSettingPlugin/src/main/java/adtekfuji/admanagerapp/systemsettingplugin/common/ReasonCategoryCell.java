/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
import javafx.scene.control.TreeCell;
import jp.adtekfuji.adFactory.entity.master.ReasonCategoryInfoEntity;
import org.apache.logging.log4j.LogManager;

/**
 * 理由区分セル
 * 
 * @author s-heya
 */
public class ReasonCategoryCell extends TreeCell<ReasonCategoryInfoEntity> {

    /**
     * セルを更新する。
     * 
     * @param item
     * @param empty 
     */
    @Override
    protected void updateItem(ReasonCategoryInfoEntity item, boolean empty) {
        super.updateItem(item, empty);
        try {
            if (empty) {
                setText(null);
            } else {
                String name = item.getReasonCategoryName();
                if (Objects.nonNull(item.isDefaultReasonCategory()) && item.isDefaultReasonCategory()) {
                    name += " (" + LocaleUtils.getString("default") + ")";
                }
                setText(name);
                setGraphic(getTreeItem().getGraphic());
            }
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
    }
}
