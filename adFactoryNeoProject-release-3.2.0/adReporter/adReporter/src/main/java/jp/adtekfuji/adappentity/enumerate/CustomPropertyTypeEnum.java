/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adappentity.enumerate;

/**
 * プロパティ種別
 *
 * @author s-heya
 */
public enum CustomPropertyTypeEnum {

    TYPE_STRING("key.propertyStringType"),
    TYPE_BOOLEAN("key.propertyBooleanType"),
    TYPE_INTEGER("key.propertyIntegerType"),
    TYPE_NUMERIC("key.propertyNumericType"),
    TYPE_IP4_ADDRESS("key.propertyIp4AddressType"),
    TYPE_MAC_ADDRESS("key.propertyMacAddressType"),
    TYPE_PLUGIN("key.pluginName"),
    TYPE_TRACE("key.traceInfo");

    private final String resourceKey;

    private CustomPropertyTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }
}
