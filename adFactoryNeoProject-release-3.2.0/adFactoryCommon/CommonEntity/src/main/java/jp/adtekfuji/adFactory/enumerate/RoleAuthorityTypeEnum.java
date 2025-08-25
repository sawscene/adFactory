/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 機能権限
 *
 * @author ke.yokoi
 */
public enum RoleAuthorityTypeEnum implements RoleAuthorityType {

    DELETE_ACTUAL("DELETE_ACTUAL", "key.DeleteActual"),
    EDITED_RESOOURCE("EDITED_RESOOURCE", "key.EditedResource"),
    MAKED_KANBAN("MAKED_KANBAN", "key.MakedKanban"),
    EDITED_WORKFLOW("EDITED_WORKFLOW", "edited_workflow"),
    MANAGED_LINE("MANAGED_LINE", "key.ManagedLine"),
    OUTPUT_ACTUAL("OUTPUT_ACTUAL", "key.OutputActual"),
    REFERENCE_WORKFLOW("REFERENCE_WORKFLOW", "reference_workflow"),
    REFERENCE_KANBAN("REFERENCE_KANBAN", "key.ReferenceKanban"),
    REFERENCE_RESOOURCE("REFERENCE_RESOOURCE", "key.ReferenceResource"),
    RIGHT_ACCESS("RIGHT_ACCESS","key.EditedAuth"),
    APPROVAL_KANBAN("APPROVAL_KANBAN","key.Approve");

    private final String name;
    private final String resourceKey;
    private final static Map<String, RoleAuthorityType> map = new LinkedHashMap();
    static {
        for (RoleAuthorityType type : values()) {
            map.put(type.getName(), type);
        }
    }

    /**
     * コンストラクタ
     *
     * @param name
     * @param resourceKey
     */
    private RoleAuthorityTypeEnum(String name, String resourceKey) {
        this.name = name;
        this.resourceKey = resourceKey;
    }

    /**
     * 機能権限名を取得する、
     *
     * @return
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * リソースキーを取得する。
     *
     * @return
     */
    @Override
    public String getResourceKey() {
        return this.resourceKey;
    }

    public static String getValueText(int idx) {
        String value = "";
        // 列挙型を中身の並び順に取得する
        Collection<RoleAuthorityType> collection = RoleAuthorityTypeEnum.map.values();
        // 引数の数値が並び順のMAX数より大きいか判定
        if (collection.size() > idx) {
            value = ((RoleAuthorityType) collection.toArray()[idx]).getName();
        }
        return value;
    }

    public static RoleAuthorityType get(String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        return null;
    }

    public static String getMessage(ResourceBundle rb, RoleAuthorityTypeEnum value) {
        for (RoleAuthorityType type : RoleAuthorityTypeEnum.map.values()) {
            if (value.equals(type)) {
                return LocaleUtils.getString(type.getResourceKey());
            }
        }
        return "";
    }

    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        for (RoleAuthorityType type : RoleAuthorityTypeEnum.map.values()) {
            messages.add(LocaleUtils.getString(type.getResourceKey()));
        }
        return messages;
    }

    public static Collection<RoleAuthorityType> types() {
        return map.values();
    }

    /**
     * RoleAuthorityTypeを追加する。
     *
     * @param type
     */
    public static void add(RoleAuthorityType type) {
        if (!map.containsKey(type.getName())) {
            map.put(type.getName(), type);
        }
    }
}
