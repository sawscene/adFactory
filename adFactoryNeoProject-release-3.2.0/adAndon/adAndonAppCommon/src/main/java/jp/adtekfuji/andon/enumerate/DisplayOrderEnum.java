package jp.adtekfuji.andon.enumerate;

public enum DisplayOrderEnum {
    DISPLAY_ORDER_BY_NAME("key.DisplayOrderByName"),
    DISPLAY_ORDER_BY_CREATE("key.DisplayOrderByCreate"),
    DISPLAY_ORDER_BY_START_TIME("key.DisplayByStartTime"),
    DISPLAY_ORDER_BY_COMP_TIME("key.DisplayOrderByCompTime");

    DisplayOrderEnum(String name) {
        this.name = name;
    }
    private final String name;

    public String getName() {
        return name;
    }
}
