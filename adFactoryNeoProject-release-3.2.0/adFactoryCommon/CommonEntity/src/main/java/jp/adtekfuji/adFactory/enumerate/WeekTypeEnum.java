package jp.adtekfuji.adFactory.enumerate;


import java.util.Calendar;

public enum WeekTypeEnum {
    SUNDAY("key.Sunday", "key.calendar.week.sunday", Calendar.SUNDAY),
    MONDAY("key.Monday", "key.calendar.week.monday", Calendar.MONDAY),
    TUESDAY("key.Tuesday", "key.calendar.week.tuesday", Calendar.TUESDAY),
    WEDNESDAY("key.Wednesday", "key.calendar.week.wednesday", Calendar.WEDNESDAY),
    THURSDAY("key.Thursday", "key.calendar.week.thursday", Calendar.THURSDAY),
    FRIDAY("key.Friday", "key.calendar.week.friday", Calendar.FRIDAY),
    SATURDAY("key.Saturday", "key.calendar.week.saturday", Calendar.SATURDAY);

    public final String resourceKey;
    public final String shortResourceKey;
    public final int index;
    WeekTypeEnum(String resourceKey, String shortResourceKey, int index) {
        this.index = index;
        this.shortResourceKey = shortResourceKey;
        this.resourceKey = resourceKey;
    }
}
