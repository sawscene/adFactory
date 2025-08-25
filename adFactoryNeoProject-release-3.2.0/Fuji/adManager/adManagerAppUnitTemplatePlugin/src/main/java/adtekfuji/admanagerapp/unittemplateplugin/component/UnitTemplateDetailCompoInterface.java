/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.component;

import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;

/**
 * ユニットテンプレート詳細画面インターフェース
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.28.Fri
 */
public interface UnitTemplateDetailCompoInterface {

    /**
     * 詳細画面で使用しているユニットテンプレートの情報を取得
     *
     * @return
     */
    public UnitTemplateInfoEntity getUnitTemplateInfoEntity();

    /**
     * ブロックUI操作
     *
     * @param isBlock ブロックUIの有無
     */
    public void blockUI(boolean isBlock);

    /**
     * 直列にワークを挿入する
     *
     */
    public void addSerial();

    /**
     * 並列にワークを挿入する
     *
     */
    public void addParallel();

}
