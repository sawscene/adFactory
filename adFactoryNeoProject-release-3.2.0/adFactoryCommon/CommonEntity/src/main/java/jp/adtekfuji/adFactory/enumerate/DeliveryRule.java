/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * 払出ルール
 * 
 * @author s-heya
 */
public enum DeliveryRule {
    NORMAL(1),
    ISSUE(2);

    private final Integer id;

    /**
     * コンストラクタ
     * 
     * @param id
     */
    private DeliveryRule(int id) {
        this.id = id;
    }

    /**
     * IDを取得する。
     * 
     * @return 
     */
    public int getId() {
        return id;
    }
    
}
