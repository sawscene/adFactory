/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.entity;

import java.util.List;
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectWorkInfoEntity;

/**
 * 間接作業選択ダイアログ用データ
 *
 * @author nar-nakamura
 */
public class ChoiceIndirectWorkEntity {

    private List<IndirectWorkInfoEntity> indirectWorks;
    private IndirectWorkInfoEntity selectedItem;

    /**
     * コンストラクタ
     *
     * @param indirectWorks 選択対象リスト
     * @param selectedItem 選択中のデータ
     */
    public ChoiceIndirectWorkEntity(List<IndirectWorkInfoEntity> indirectWorks, IndirectWorkInfoEntity selectedItem) {
        this.indirectWorks = indirectWorks;
        this.selectedItem = selectedItem;
    }

    /**
     * 選択対象リストを取得する。
     *
     * @return 選択対象リスト
     */
    public List<IndirectWorkInfoEntity> getIndirectWorks() {
        return this.indirectWorks;
    }

    /**
     * 選択中のデータを取得する。
     *
     * @return 選択中のデータ
     */
    public IndirectWorkInfoEntity getSelectedItem() {
        return this.selectedItem;
    }

    /**
     * 選択中のデータを設定する。
     *
     * @param selectedItem 選択中のデータ
     */
    public void setSelectedItem(IndirectWorkInfoEntity selectedItem) {
        this.selectedItem = selectedItem;
    }
}
