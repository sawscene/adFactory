package jp.adtekfuji.adFactory.enumerate;


public enum SchedulePatternEnum {
    DAY("key.Day"),
    WEEK("key.Week"),
    MONTH("key.Month");

    public final String resourceKey;
    SchedulePatternEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }
};
