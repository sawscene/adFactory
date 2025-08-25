/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.dialog;

import adtekfuji.admanagerapp.warehouseplugin.enumerate.InventoryDialogType;
import java.util.List;

/**
 * 棚卸ダイアログの引数
 *
 * @author nar-nakamura
 */
public class InventoryDialogArgument {

    /**
     * ダイアログ種別
     */
    private final InventoryDialogType dialogType;

    /**
     * 区画名一覧
     */
    private final List<String> areaNames;

    /**
     * 区画名
     */
    private String selectedAreaName;

    /**
     * コンストラクタ
     *
     * @param dialogType ダイアログ種別
     * @param areaNames 区画名一覧
     */
    public InventoryDialogArgument(InventoryDialogType dialogType, List<String> areaNames) {
        this.dialogType = dialogType;
        this.areaNames = areaNames;
    }

    /**
     * ダイアログ種別を取得する。
     *
     * @return ダイアログ種別
     */
    public InventoryDialogType getDialogType() {
        return this.dialogType;
    }

    /**
     * 区画名一覧を取得する。
     *
     * @return 区画名一覧
     */
    public List<String> getAreaNames() {
        return this.areaNames;
    }

    /**
     * 選択区画名を取得する。
     *
     * @return 選択区画名
     */
    public String getSelectedAreaName() {
        return this.selectedAreaName;
    }

    /**
     * 選択区画名を設定する。
     *
     * @param selectedAreaName 選択区画名
     */
    public void setSelectedAreaName(String selectedAreaName) {
        this.selectedAreaName = selectedAreaName;
    }

    @Override
    public String toString() {
        return new StringBuilder("InventoryDialogArgument{")
                .append("dialogType=").append(this.dialogType)
                .append(", selectedAreaName=").append(this.selectedAreaName)
                .toString();
    }
}
