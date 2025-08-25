package jp.adtekfuji.adFactory.enumerate;


public enum LedgerTypeEnum {
    AGGREGATION("key.Aggregation"), // 集約
    KEY_AGREGATION("key.KeyAggregation"), // キー集約
    INDIVIDUAL("key.Individual"); // 個別


    public final String resourceKey;

    LedgerTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }


};
