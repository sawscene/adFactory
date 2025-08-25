package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.entity;

import adtekfuji.utility.StringUtils;

import java.util.Arrays;

public enum DeviceTypeEnum {
    MTLINK("MT-LINK");

    private String name;
    DeviceTypeEnum(String name) {
        this.name = name;
    }

    String getName(){
        return this.name;
    }

    static DeviceTypeEnum toEnum(String name)
    {
        return Arrays.stream(values())
                .filter(item-> StringUtils.equals(item.getName(), name))
                .findFirst()
                .orElse(null);
    }

}
