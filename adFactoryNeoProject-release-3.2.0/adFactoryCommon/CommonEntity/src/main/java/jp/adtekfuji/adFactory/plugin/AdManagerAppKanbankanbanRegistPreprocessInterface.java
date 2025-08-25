/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.plugin;

import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.utility.KanbanRegistPreprocessResultEntity;

/**
 * カンバン情報のプラグインインターフェース定義.
 *
 * @author e.mori
 */
public interface AdManagerAppKanbankanbanRegistPreprocessInterface {

    /**
     * プラグイン毎の初期処理呼出し
     */
    public default void pluginInitialize() {
    }

    /**
     * プラグイン毎の後処理呼出し
     */
    public default void pluginDestructor() {
    }

    /**
     * 登録処理時の前処理.
     *
     * @param registData
     * @return 
     */
    public KanbanRegistPreprocessResultEntity kanbanRegistPreprocess(KanbanInfoEntity registData);

}
