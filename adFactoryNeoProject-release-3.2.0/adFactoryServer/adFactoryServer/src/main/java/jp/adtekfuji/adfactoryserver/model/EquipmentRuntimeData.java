/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 設備ランタイムデータ
 *
 * @author s-heya
 */
public class EquipmentRuntimeData {

    private static EquipmentRuntimeData instance;
    private final Map<Long, Long> callCollection = new HashMap<>();// 呼び出し一覧

    /**
     * コンストラクタ
     */
    private EquipmentRuntimeData() {
    }

    /**
     * インスタンスを取得する。
     *
     * @return 
     */
    public static EquipmentRuntimeData getInstance() {
        if (Objects.isNull(instance)) {
            instance = new EquipmentRuntimeData();
        }
        return instance;
    }

    /**
     * 呼び出し一覧を取得する。
     *
     * @return 呼び出し一覧
     */
    public Map<Long, Long> getCollections() {
        return this.callCollection;
    }

    /**
     * 設備IDを指定して、呼び出し中かどうかを取得する。
     *
     * @param equipmentId 設備ID
     * @return 呼び出し中かどうか (true:呼び出し中, false:呼び出し中ではない)
     */
    public boolean checkCall(long equipmentId) {
        return this.callCollection.containsKey(equipmentId);
    }

    /**
     * 呼び出し中状態を更新する。
     *
     * @param equipmentId 設備ID
     * @param organizationId 組織ID
     * @param isCall 呼び出し中かどうか (true:呼び出し中, false:呼び出し中ではない)
     */
    public void updateCall(long equipmentId, long organizationId, boolean isCall) {
        if (isCall) {
            this.callCollection.put(equipmentId, organizationId);
        } else {
            this.callCollection.remove(equipmentId);
        }
    }
}
