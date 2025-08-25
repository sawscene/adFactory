/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | s
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unitplugin.component;

import jp.adtekfuji.forfujiapp.entity.unit.UnitHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;

/**
 * ユニットインターフェース
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public interface UnitListCompoInterface {

    /**
     * 選択されているレコードの取得
     *
     * @return 選択されてるレコード
     */
    public UnitInfoEntity getSelectRecord();

    /**
     * 選択されている階層の取得
     *
     * @return 選択されてる階層
     */
    public UnitHierarchyInfoEntity getSelectTree();

    /**
     * テーブルのアップデート
     *
     * @param hierarchy
     */
    public void updateTable(UnitHierarchyInfoEntity hierarchy);

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
