/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.controller;

import java.util.Map;

/**
 * データベースマネージャ インターフェイス
 *
 * @author e-mori
 */
public interface DatabaseManagerInterface {

    /**
     * テーブルを更新する
     */
    Boolean updateTable();

    /**
     * データをエクスポートする
     *
     * @param filePath 
     */
    void exportData(String filePath);

    /**
     * データをインポートする
     *
     * @param filePath 
     */
    void importData(String filePath);

    Map<String, String> getDBInfo();

    /**
     * adFactoryDBのバックアップファイルを作成する。
     *
     * @param filePath バックアップファイルパス
     * @return 結果 (true:成功, false:失敗)
     */
    Boolean backupData(String filePath);

    /**
     * バックアップファイルからadFactoryDBをリストアする。
     *
     * @param filePath バックアップファイルパス
     * @return 結果 (true:成功, false:失敗)
     */
    Boolean restoreData(String filePath);
}
