package jp.adtekfuji.adFactory.enumerate;


public enum NumberTypeEnum {
    FIRST("key.First"),
    SECOND("key.Second"),
    THIRD("key.Third"),
    FOURTH("key.Fourth"),
    LAST("key.Last");

    public final String resourceKey;
    NumberTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }
};
