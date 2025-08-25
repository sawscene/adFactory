/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.entity;

import java.util.List;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;

/**
 * 組織選択ダイアログ用データ
 *
 * @author nar-nakamura
 */
public class ChoiceOrganizationEntity {

    private List<OrganizationInfoEntity> organizations;
    private OrganizationInfoEntity selectedItem;

    /**
     * コンストラクタ
     *
     * @param organizations 選択対象リスト
     * @param selectedItem 選択中のデータ
     */
    public ChoiceOrganizationEntity(List<OrganizationInfoEntity> organizations, OrganizationInfoEntity selectedItem) {
        this.organizations = organizations;
        this.selectedItem = selectedItem;
    }

    /**
     * 選択対象リストを取得する。
     *
     * @return 選択対象リスト
     */
    public List<OrganizationInfoEntity> getIndirectWorks() {
        return this.organizations;
    }

    /**
     * 選択中のデータを取得する。
     *
     * @return 選択中のデータ
     */
    public OrganizationInfoEntity getSelectedItem() {
        return this.selectedItem;
    }

    /**
     * 選択中のデータを設定する。
     *
     * @param selectedItem 選択中のデータ
     */
    public void setSelectedItem(OrganizationInfoEntity selectedItem) {
        this.selectedItem = selectedItem;
    }
}
