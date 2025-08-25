/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.dialog;

import java.util.List;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;

/**
 * 棚卸確認ダイアログの引数
 *
 * @author nar-nakamura
 */
public class InventoryConfirmArgument {

    /**
     * 区画名
     */
    final private String areaName;

    /**
     * 資材情報一覧
     */
    final private List<TrnMaterialInfo> materials;

    /**
     * コンストラクタ
     *
     * @param areaName 区画名
     * @param materials 資材情報一覧
     */
    public InventoryConfirmArgument(String areaName, List<TrnMaterialInfo> materials) {
        this.areaName = areaName;
        this.materials = materials;
    }

    public String getAreaName() {
        return this.areaName;
    }

    public List<TrnMaterialInfo> getMaterials() {
        return this.materials;
    }

    @Override
    public String toString() {
        return new StringBuilder("InventoryCheckDialogArgument{")
                .append("areaName=").append(this.areaName)
                .append(", materials=").append(this.materials)
                .append("}")
                .toString();
    }
}
