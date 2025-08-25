/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.entity;

import java.util.List;

/**
 * 製番選択ダイアログ用データ
 *
 * @author kentarou.suzuki
 */
public class ChoiceControlNoEntity {

    private final List<String> targetItems;
    private List<String> selectedItems;

    /**
     * コンストラクタ
     *
     * @param targetItems 選択対象リスト
     * @param selectedItems 選択中のデータ
     */
    public ChoiceControlNoEntity(List<String> targetItems, List<String> selectedItems) {
        this.targetItems = targetItems;
        this.selectedItems = selectedItems;
    }

    /**
     * 選択対象リストを取得する。
     *
     * @return 選択対象リスト
     */
    public List<String> getTargetItems() {
        return this.targetItems;
    }

    /**
     * 選択中のデータを取得する。
     *
     * @return 選択中のデータ
     */
    public List<String> getSelectedItems() {
        return this.selectedItems;
    }

    /**
     * 選択中のデータを設定する。
     *
     * @param selectedItems 選択中のデータ
     */
    public void setSelectedItems(List<String> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
