/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.externalio;

import adtekfuji.admanagerapp.kanbaneditplugin.entity.KanbanBaseInfoEntity;

/**
 * データベース・アクセス・インターフェース
 * 
 * @author s-heya
 */
public interface DbAccess {

    /**
     * カンバン基本情報を取得する。
     * 
     * @param id 識別コード 
     * @return カンバン基本情報
     */
    KanbanBaseInfoEntity GetKanbanBaseInfo(String id);

    /**
     * データベースにアクセスできるかどうかを返す。
     * 
     * @return true: アクセス可, false: アクセス不可 
     */
    boolean IsAvailable();
}
