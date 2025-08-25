/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.enumerate;

/**
 * 予実モニター　詳細表示タイプ
 *
 * @author fu-kato
 */
public enum ContentTypeEnum {
    WORKFLOW_NAME("key.OrderProcessesName"),
    MODEL("key.ModelName");

    private final String name;

    private ContentTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
