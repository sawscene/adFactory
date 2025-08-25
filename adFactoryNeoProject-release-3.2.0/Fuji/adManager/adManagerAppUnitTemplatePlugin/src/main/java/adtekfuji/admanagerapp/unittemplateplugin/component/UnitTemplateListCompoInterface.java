/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.component;

import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;

/**
 * ユニットテンプレートインターフェース
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public interface UnitTemplateListCompoInterface {

    /**
     * 選択されているレコードの取得
     *
     * @return 選択されてるレコード
     */
    public UnitTemplateInfoEntity getSelectRecord();

    /**
     * 選択されている階層の取得
     *
     * @return 選択されてる階層
     */
    public UnitTemplateHierarchyInfoEntity getSelectTree();

    /**
     * テーブルのアップデート
     *
     * @param hierarchy
     */
    public void updateTable(UnitTemplateHierarchyInfoEntity hierarchy);

    /**
     * テーブルの削除
     *
     */
    public void clearTable();

    /**
     * テーブルのアップデート
     *
     */
    public void updateTree();
    
    /**
     * ブロックUI操作
     *
     * @param isBlock ブロックUIの有無
     */
    public void blockUI(boolean isBlock);
}
