/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.view;

/**
 * テーブルカラム
 * 
 * @author s-heya
 */
public class DataTableColumn {
    private final Integer id;
    private final String header;
    private final Object userData;
    
    /**
     * コンストラクタ
     * 
     * @param id
     * @param header
     * @param userData 
     */
    public DataTableColumn(Integer id, String header, Object userData) {
        this.id = id;
        this.header = header;
        this.userData = userData;
    }

    /**
     * IDを取得する。
     * 
     * @return 
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * タイトル名を取得する。
     * 
     * @return 
     */
    public String getHeader() {
        return header;
    }
    
    /**
     * ユーザーデータを取得する。
     * 
     * @return ユーザーデータ
     */
    public Object getUserData() {
        return userData;
    }     
}
