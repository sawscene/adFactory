package jp.adtekfuji.adFactory.entity.schedule;


import adtekfuji.utility.Tuple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jp.adtekfuji.adFactory.enumerate.NumberTypeEnum;
import jp.adtekfuji.adFactory.enumerate.SchedulePatternEnum;
import jp.adtekfuji.adFactory.enumerate.WeekTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

import java.io.Serializable;
import java.util.*;
import static java.util.stream.Collectors.*;

public class ScheduleConditionInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date startDate;

    @JsonProperty("date_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dateTime;

    @JsonProperty("schedule_pattern")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SchedulePatternEnum schedulePattern;

    @JsonProperty("day_period")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer dayPeriod;

    @JsonProperty("week_period")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer weekPeriod;

    @JsonProperty("weeks")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String weeks;

    @JsonProperty("month_schedule_pattern")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SchedulePatternEnum monthSchedulePattern;

    @JsonProperty("month_day_month")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer monthDayMonth;

    @JsonProperty("month_day_day")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer monthDayDay;

    @JsonProperty("month_week_month")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer monthWeekMonth;

    @JsonProperty("month_week_week")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private NumberTypeEnum monthWeekWeek;

    @JsonProperty("month_week_day")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private WeekTypeEnum monthWeekDay;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public SchedulePatternEnum getSchedulePattern() {
        return schedulePattern;
    }

    public void setSchedulePattern(SchedulePatternEnum schedulePattern) {
        this.schedulePattern = schedulePattern;
    }

    public Integer getDayPeriod() {
        return dayPeriod;
    }

    public void setDayPeriod(Integer dayPeriod) {
        this.dayPeriod = dayPeriod;
    }

    public Integer getWeekPeriod() {
        return weekPeriod;
    }

    public void setWeekPeriod(Integer weekPeriod) {
        this.weekPeriod = weekPeriod;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public SchedulePatternEnum getMonthSchedulePattern() {
        return monthSchedulePattern;
    }

    public void setMonthSchedulePattern(SchedulePatternEnum monthSchedulePattern) {
        this.monthSchedulePattern = monthSchedulePattern;
    }

    public Integer getMonthDayMonth() {
        return monthDayMonth;
    }

    public void setMonthDayMonth(Integer monthDayMonth) {
        this.monthDayMonth = monthDayMonth;
    }

    public Integer getMonthDayDay() {
        return monthDayDay;
    }

    public void setMonthDayDay(Integer monthDayDay) {
        this.monthDayDay = monthDayDay;
    }

    public Integer getMonthWeekMonth() {
        return monthWeekMonth;
    }

    public void setMonthWeekMonth(Integer monthWeekMonth) {
        this.monthWeekMonth = monthWeekMonth;
    }

    public NumberTypeEnum getMonthWeekWeek() {
        return monthWeekWeek;
    }

    public void setMonthWeekWeek(NumberTypeEnum monthWeekWeek) {
        this.monthWeekWeek = monthWeekWeek;
    }

    public WeekTypeEnum getMonthWeekDay() {
        return monthWeekDay;
    }

    public void setMonthWeekDay(WeekTypeEnum monthWeekDay) {
        this.monthWeekDay = monthWeekDay;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return Objects.isNull(this.schedulePattern);
    }

    @JsonIgnore
    public ScheduleConditionInfoEntity getOrganizeData() {
        ScheduleConditionInfoEntity scheduleConditionInfoEntity = new ScheduleConditionInfoEntity();
        scheduleConditionInfoEntity.startDate = startDate;
        scheduleConditionInfoEntity.dateTime = dateTime;
        scheduleConditionInfoEntity.schedulePattern = this.schedulePattern;
        switch(this.schedulePattern) {
            case WEEK:
                scheduleConditionInfoEntity.weekPeriod = this.weekPeriod;
                scheduleConditionInfoEntity.weeks = this.weeks;
                break;
            case MONTH:
                scheduleConditionInfoEntity.monthSchedulePattern = this.monthSchedulePattern;
                switch (this.monthSchedulePattern) {
                    case WEEK:
                        scheduleConditionInfoEntity.monthWeekMonth = this.monthWeekMonth;
                        scheduleConditionInfoEntity.monthWeekWeek = this.monthWeekWeek;
                        scheduleConditionInfoEntity.monthWeekDay = this.monthWeekDay;
                        break;
                    case DAY:
                    default:
                        scheduleConditionInfoEntity.monthDayMonth = this.monthDayMonth;
                        scheduleConditionInfoEntity.monthDayDay = this.monthDayDay;
                        break;
                }
                break;
            case DAY:
            default:
                scheduleConditionInfoEntity.dayPeriod = this.dayPeriod;
                break;
        }
        return scheduleConditionInfoEntity;
    }

    @JsonIgnore
    public ScheduleConditionInfoEntity clone() {
        ScheduleConditionInfoEntity scheduleConditionInfoEntity = new ScheduleConditionInfoEntity();
        scheduleConditionInfoEntity.startDate = this.startDate;
        scheduleConditionInfoEntity.dateTime = this.dateTime;

        scheduleConditionInfoEntity.schedulePattern = this.schedulePattern;
        scheduleConditionInfoEntity.dayPeriod = this.dayPeriod;
        scheduleConditionInfoEntity.weekPeriod = this.weekPeriod;
        scheduleConditionInfoEntity.weeks = this.weeks;
        scheduleConditionInfoEntity.monthSchedulePattern = this.monthSchedulePattern;
        scheduleConditionInfoEntity.monthDayMonth = this.monthDayMonth;
        scheduleConditionInfoEntity.monthDayDay = this.monthDayDay;
        scheduleConditionInfoEntity.monthWeekMonth = this.monthWeekMonth;
        scheduleConditionInfoEntity.monthWeekWeek = this.monthWeekWeek;
        scheduleConditionInfoEntity.monthWeekDay = this.monthWeekDay;
        return scheduleConditionInfoEntity;
    }

    @JsonIgnore
    public void apply(ScheduleConditionInfoEntity scheduleConditionInfoEntity) {
        this.startDate =scheduleConditionInfoEntity.startDate;
        this.dateTime = scheduleConditionInfoEntity.dateTime;
        this.schedulePattern = scheduleConditionInfoEntity.schedulePattern;
        this.dayPeriod = scheduleConditionInfoEntity.dayPeriod;
        this.weekPeriod = scheduleConditionInfoEntity.weekPeriod;
        this.weeks = scheduleConditionInfoEntity.weeks;
        this.monthSchedulePattern = scheduleConditionInfoEntity.monthSchedulePattern;
        this.monthDayMonth = scheduleConditionInfoEntity.monthDayMonth;
        this.monthDayDay = scheduleConditionInfoEntity.monthDayDay;
        this.monthWeekMonth = scheduleConditionInfoEntity.monthWeekMonth;
        this.monthWeekWeek = scheduleConditionInfoEntity.monthWeekWeek;
        this.monthWeekDay = scheduleConditionInfoEntity.monthWeekDay;
    }


    final static int DAY_PER_WEEK = 7; // 1週間.
    final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000; // 1日のミリ秒
    final static long MILLIS_PER_WEEK = MILLIS_PER_DAY*DAY_PER_WEEK; // 1週間のミリ秒

    @JsonIgnore
    static private Calendar calcStartDateTime(ScheduleConditionInfoEntity scheduleConditionInfoEntity) {
        Calendar startDateTime = Calendar.getInstance();
        startDateTime.setTime(scheduleConditionInfoEntity.getStartDate());

        String[] times = scheduleConditionInfoEntity.dateTime.split(":");
        try {
            startDateTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
        } catch (Exception ex) {
            startDateTime.set(Calendar.HOUR_OF_DAY, 0);
        }

        try {
            startDateTime.set(Calendar.MINUTE, Integer.parseInt(times[1]));
        } catch (Exception ex) {
            startDateTime.set(Calendar.MINUTE, 0);
        }
        startDateTime.set(Calendar.SECOND, 0);
        startDateTime.set(Calendar.MILLISECOND, 0);
        return startDateTime;
    }

    /**
     * 基準日の算出
     * @param scheduleConditionInfoEntity スケジュール情報
     * @param today 今日
     * @return 基準日
     */
    @JsonIgnore
    static private Calendar getBaseDateTime(ScheduleConditionInfoEntity scheduleConditionInfoEntity, Calendar now) {

        Calendar today = (Calendar) now.clone();
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.SECOND, 59);
        today.set(Calendar.MILLISECOND, 999);

        Calendar base = Calendar.getInstance();
        if (Objects.nonNull(scheduleConditionInfoEntity.getStartDate())) {
            base.setTime(scheduleConditionInfoEntity.getStartDate());
        } else {
            base.setTime(new Date());
        }
        base.set(Calendar.HOUR_OF_DAY, 0);
        base.set(Calendar.MINUTE, 0);
        base.set(Calendar.SECOND, 0);
        base.set(Calendar.MILLISECOND, 0);

        switch (scheduleConditionInfoEntity.getSchedulePattern()) {
            case WEEK: {
                base.add(Calendar.DAY_OF_MONTH, Calendar.SUNDAY - base.get(Calendar.DAY_OF_WEEK));
                final long diff = today.getTimeInMillis() - base.getTimeInMillis();
                if (diff < 0) {
                    return base;
                }
                base.add(Calendar.DAY_OF_MONTH, (int) (diff/(MILLIS_PER_WEEK*scheduleConditionInfoEntity.getWeekPeriod()))*scheduleConditionInfoEntity.getWeekPeriod() * DAY_PER_WEEK);
                return base;
            }
            case MONTH: {
                base.set(Calendar.DAY_OF_MONTH, 1);
                final long diff = today.getTimeInMillis() - base.getTimeInMillis();
                if (diff < 0) {
                    return base;
                }
                Calendar lastMonth = (Calendar) today.clone();
                switch(scheduleConditionInfoEntity.getMonthSchedulePattern()) {
                    case WEEK:
                        lastMonth.add(Calendar.MONTH, -scheduleConditionInfoEntity.getMonthWeekMonth());
                        while (!base.after(lastMonth)) {
                            base.add(Calendar.MONTH, scheduleConditionInfoEntity.getMonthWeekMonth());
                        }
                        return base;
                    case DAY:
                    default:
                        lastMonth.add(Calendar.MONTH, -scheduleConditionInfoEntity.getMonthDayMonth());
                        while (!base.after(lastMonth)) {
                            base.add(Calendar.MONTH, scheduleConditionInfoEntity.getMonthDayMonth());
                        }
                        return base;
                }
            }
            case DAY:
            default: {
                final long diff = today.getTimeInMillis() - base.getTimeInMillis();
                if (diff < 0) {
                    return base;
                }
                base.add(Calendar.DAY_OF_MONTH, (int) (diff / (MILLIS_PER_DAY * scheduleConditionInfoEntity.getDayPeriod()))*scheduleConditionInfoEntity.getDayPeriod());
                return base;
            }
        }
    }


    /**
     * targetの前回の実施(予定)日
     * targetは必ず実施(予定)日にする必要がある為、外部より呼出禁止
     * @param scheduleConditionInfoEntity 実施情報
     * @param target 実施日
     * @return 前回の実施日
     */
    @JsonIgnore
    private static Optional<Calendar> calcPreviousSchedule(ScheduleConditionInfoEntity scheduleConditionInfoEntity, Calendar target) {
        if (Objects.isNull(target)) {
            return Optional.empty();
        }

        Calendar calendar = (Calendar) target.clone();
        switch (scheduleConditionInfoEntity.getSchedulePattern()) {
            case WEEK: {
                List<WeekTypeEnum> weekTypeEnums = JsonUtils.jsonToObjects(scheduleConditionInfoEntity.getWeeks(), WeekTypeEnum[].class);
                if (weekTypeEnums.isEmpty()) {
                    weekTypeEnums.add(WeekTypeEnum.SUNDAY);
                }
                int weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                Optional<Integer> nextWeek
                        = weekTypeEnums
                        .stream()
                        .mapToInt(WeekTypeEnum::ordinal)
                        .boxed()
                        .sorted(Comparator.reverseOrder())
                        .filter(i -> i < weekNum)
                        .findFirst();
                if (nextWeek.isPresent()) {
                    calendar.add(Calendar.DAY_OF_MONTH, nextWeek.get() - weekNum);
                    return Optional.of(calendar);
                }
                calendar.add(Calendar.DAY_OF_MONTH, Calendar.SUNDAY - calendar.get(Calendar.DAY_OF_WEEK));
                calendar.add(Calendar.DAY_OF_MONTH, -(DAY_PER_WEEK * scheduleConditionInfoEntity.getWeekPeriod()) + weekTypeEnums.get(weekTypeEnums.size()-1).ordinal());
                return Optional.of(calendar);
            }
            case MONTH:
                switch(scheduleConditionInfoEntity.getMonthSchedulePattern()) {
                    case WEEK:
                    {
                        if (calendar.get(Calendar.WEEK_OF_MONTH) != (scheduleConditionInfoEntity.getMonthWeekWeek().ordinal()+1)) {
                            if (NumberTypeEnum.LAST.equals(scheduleConditionInfoEntity.getMonthWeekWeek())) {
                                // 月末は翌月と重なる可能性がある為、判定処理
                                if (calendar.get(Calendar.DAY_OF_MONTH) < 15) {
                                    calendar.add(Calendar.MONTH, -1);
                                }
                            } else {
                                // 月初は前月と重なる可能性がある為、判定処理
                                if (calendar.get(Calendar.DAY_OF_MONTH) > 15) {
                                    calendar.add(Calendar.MONTH, 1);
                                }
                            }
                        }

                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.add(Calendar.MONTH, -scheduleConditionInfoEntity.getMonthWeekMonth());
                        if (NumberTypeEnum.LAST.equals(scheduleConditionInfoEntity.getMonthWeekWeek())) {
                            calendar.add(Calendar.MONTH, 1);
                            calendar.add(Calendar.DAY_OF_MONTH, -1);
                        } else {
                            calendar.set(Calendar.WEEK_OF_MONTH, scheduleConditionInfoEntity.getMonthWeekWeek().ordinal()+1);
                        }
                        calendar.add(Calendar.DAY_OF_MONTH, Calendar.SUNDAY - calendar.get(Calendar.DAY_OF_WEEK) + scheduleConditionInfoEntity.getMonthWeekDay().ordinal());
                        return Optional.of(calendar);
                    }
                    case DAY:
                    default:
                    {
                        calendar.add(Calendar.MONTH, -scheduleConditionInfoEntity.getMonthDayMonth());
                        calendar.set(Calendar.DAY_OF_MONTH, scheduleConditionInfoEntity.getMonthDayDay());
                        if (scheduleConditionInfoEntity.getMonthDayDay() != calendar.get(Calendar.DAY_OF_MONTH)) {
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            calendar.add(Calendar.DAY_OF_MONTH, -1);
                        }
                        return Optional.of(calendar);
                    }
                }
            case DAY:
            default:
                calendar.add(Calendar.DAY_OF_MONTH, -scheduleConditionInfoEntity.getDayPeriod());
                return Optional.of(calendar);
        }
    }

    /**
     * targetの次回の実施(予定)日
     * targetは必ず実施(予定)日にする必要がある為、外部より呼出禁止
     * @param scheduleConditionInfoEntity 実施情報
     * @param target 実施日
     * @return 次回の実施日
     */
    @JsonIgnore
    private static Optional<Calendar> calcNextSchedule(ScheduleConditionInfoEntity scheduleConditionInfoEntity, Calendar target) {
        if (Objects.isNull(target)) {
            return Optional.empty();
        }

        Calendar calendar = (Calendar) target.clone();
        switch (scheduleConditionInfoEntity.getSchedulePattern()) {
            case WEEK: {
                List<WeekTypeEnum> weekTypeEnums = JsonUtils.jsonToObjects(scheduleConditionInfoEntity.getWeeks(), WeekTypeEnum[].class);
                if (weekTypeEnums.isEmpty()) {
                    weekTypeEnums.add(WeekTypeEnum.SUNDAY);
                }
                int weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                Optional<Integer> nextWeek
                        = weekTypeEnums
                        .stream()
                        .mapToInt(WeekTypeEnum::ordinal)
                        .boxed()
                        .sorted()
                        .filter(i -> i > weekNum)
                        .findFirst();
                if (nextWeek.isPresent()) {
                    calendar.add(Calendar.DAY_OF_MONTH, nextWeek.get() - weekNum);
                    return Optional.of(calendar);
                }
                calendar.add(Calendar.DAY_OF_MONTH, Calendar.SUNDAY - calendar.get(Calendar.DAY_OF_WEEK));
                calendar.add(Calendar.DAY_OF_MONTH, DAY_PER_WEEK * scheduleConditionInfoEntity.getWeekPeriod() + weekTypeEnums.get(0).ordinal());
                return Optional.of(calendar);
            }
            case MONTH:
                switch(scheduleConditionInfoEntity.getMonthSchedulePattern()) {
                    case WEEK:
                    {
                        if (calendar.get(Calendar.WEEK_OF_MONTH) != (scheduleConditionInfoEntity.getMonthWeekWeek().ordinal()+1)) {
                            if (NumberTypeEnum.LAST.equals(scheduleConditionInfoEntity.getMonthWeekWeek())) {
                                // 月末は翌月と重なる可能性がある為、判定処理
                                if (calendar.get(Calendar.DAY_OF_MONTH) < 15) {
                                    calendar.add(Calendar.MONTH, -1);
                                }
                            } else {
                                // 月初は前月と重なる可能性がある為、判定処理
                                if (calendar.get(Calendar.DAY_OF_MONTH) > 15) {
                                    calendar.add(Calendar.MONTH, 1);
                                }
                            }
                        }
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.add(Calendar.MONTH, scheduleConditionInfoEntity.getMonthWeekMonth());
                        if (NumberTypeEnum.LAST.equals(scheduleConditionInfoEntity.getMonthWeekWeek())) {
                            calendar.add(Calendar.MONTH, 1);
                            calendar.add(Calendar.DAY_OF_MONTH, -1);
                        } else {
                            calendar.set(Calendar.WEEK_OF_MONTH, scheduleConditionInfoEntity.getMonthWeekWeek().ordinal()+1);
                        }
                        calendar.add(Calendar.DAY_OF_MONTH, Calendar.SUNDAY - calendar.get(Calendar.DAY_OF_WEEK) + scheduleConditionInfoEntity.getMonthWeekDay().ordinal());
                        return Optional.of(calendar);
                    }
                    case DAY:
                    default:
                    {
                        calendar.add(Calendar.MONTH, scheduleConditionInfoEntity.getMonthDayMonth());
                        calendar.set(Calendar.DAY_OF_MONTH, scheduleConditionInfoEntity.getMonthDayDay());
                        if (scheduleConditionInfoEntity.getMonthDayDay() != calendar.get(Calendar.DAY_OF_MONTH)) {
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            calendar.add(Calendar.DAY_OF_MONTH, -1);
                        }
                        return Optional.of(calendar);
                    }
                }
            case DAY:
            default:
                calendar.add(Calendar.DAY_OF_MONTH, scheduleConditionInfoEntity.getDayPeriod());
                return Optional.of(calendar);
        }
    }

    /**
     * 次回実施日を取得
     * @return 次回実施予定日
     */
    @JsonIgnore
    public Optional<Date> getNextSchedule() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return getNextScheduleImpl(this, calendar).map(Calendar::getTime);
    }

    @JsonIgnore
    public Optional<Date> getNextSchedule(Date date) {
        if (Objects.isNull(date)) {
            if (Objects.isNull(this.startDate)) {
                return Optional.empty();
            }
            return getNextSchedule(this.startDate);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getNextScheduleImpl(this, calendar).map(Calendar::getTime);
    }

    /**
     * 次回実施日を取得(実装)
     * 単体テストの為に実装とIFを切り分け
     * @param scheduleConditionInfoEntity 実施情報
     * @param now 今の時刻
     * @return 次回の実施日
     */
    @JsonIgnore
    private static Optional<Calendar> getNextScheduleImpl(ScheduleConditionInfoEntity scheduleConditionInfoEntity, Calendar now) {

        Calendar base = getBaseDateTime(scheduleConditionInfoEntity, now);
        String[] times = scheduleConditionInfoEntity.getDateTime().split(":");
        try {
            base.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
        } catch (Exception ex) {
            base.set(Calendar.HOUR_OF_DAY, 0);
        }

        try {
            base.set(Calendar.MINUTE, Integer.parseInt(times[1]));
        } catch (Exception ex) {
            base.set(Calendar.MINUTE, 0);
        }
        base.set(Calendar.SECOND, 0);
        base.set(Calendar.MILLISECOND, 0);

        switch (scheduleConditionInfoEntity.schedulePattern) {
            case WEEK:
            {
                List<Integer> weekOrder
                        = JsonUtils.jsonToObjects(scheduleConditionInfoEntity.getWeeks(), WeekTypeEnum[].class)
                        .stream()
                        .mapToInt(WeekTypeEnum::ordinal)
                        .sorted()
                        .boxed()
                        .collect(toList());
                if (weekOrder.isEmpty()) {
                    weekOrder.add(0);
                }

                base.add(Calendar.DAY_OF_MONTH, weekOrder.get(0));
                if (base.getTimeInMillis() > now.getTimeInMillis()) {
                    return Optional.of(base);
                }
                for (int i=1; i<weekOrder.size(); ++i) {
                    base.add(Calendar.DAY_OF_MONTH, weekOrder.get(i) - weekOrder.get(i-1));
                    if (base.getTimeInMillis() > now.getTimeInMillis()) {
                        return Optional.of(base);
                    }
                }
                return calcNextSchedule(scheduleConditionInfoEntity, base);
            }
            case MONTH:
                switch(scheduleConditionInfoEntity.getMonthSchedulePattern()) {
                    case WEEK:
                    {
                        Calendar tmp = (Calendar) base.clone();
                        if (NumberTypeEnum.LAST.equals(scheduleConditionInfoEntity.getMonthWeekWeek())) {
                            tmp.add(Calendar.MONTH,1);
                            tmp.set(Calendar.DAY_OF_MONTH, 1);
                            tmp.add(Calendar.DAY_OF_MONTH, -1);
                        } else {
                            tmp.set(Calendar.WEEK_OF_MONTH, scheduleConditionInfoEntity.getMonthWeekWeek().ordinal()+1);
                        }

                        tmp.add(Calendar.DAY_OF_MONTH, Calendar.SUNDAY - tmp.get(Calendar.DAY_OF_WEEK) + scheduleConditionInfoEntity.getMonthWeekDay().ordinal());
                        if (tmp.getTimeInMillis()>now.getTimeInMillis()) {
                            return Optional.of(tmp);
                        }

                        Optional<Calendar> next = calcNextSchedule(scheduleConditionInfoEntity, base);
                        if (!next.isPresent()) {
                            return next;
                        }

                        // 最終週の場合、終わっている可能性がある為、再度確認
                        if (next.get().getTimeInMillis()>now.getTimeInMillis()) {
                            return next;
                        }
                        return calcNextSchedule(scheduleConditionInfoEntity, next.get());
                    }
                    case DAY:
                    default:
                    {
                        base.set(Calendar.DAY_OF_MONTH, scheduleConditionInfoEntity.getMonthDayDay());
                        if (scheduleConditionInfoEntity.getMonthDayDay() != base.get(Calendar.DAY_OF_MONTH)) {
                            base.set(Calendar.DAY_OF_MONTH, 1);
                            base.add(Calendar.DAY_OF_MONTH, -1);
                        }

                        if (base.getTimeInMillis()>now.getTimeInMillis()) {
                            return Optional.of(base);
                        }
                        return calcNextSchedule(scheduleConditionInfoEntity, base);
                    }
                }
            default:
            case DAY:
                if (base.getTimeInMillis()>now.getTimeInMillis()) {
                    return Optional.of(base);
                }
                return calcNextSchedule(scheduleConditionInfoEntity, base);
        }
    }

    /**
     * 次回実施日を取得
     * @return 次回実施予定日
     */
    @JsonIgnore
    public Optional<Date> getPrevSchedule() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        return getNextScheduleImpl(this, calendar)
                .flatMap(next -> calcPreviousSchedule(this, next))
                .map(Calendar::getTime);
    }

    /**
     * 前回実施日を取得
     * @param num 回数
     * @return 前回の予定実施日
     */
    @JsonIgnore
    public List<Date> getPrevSchedule(int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Optional<Calendar> next = getNextScheduleImpl(this, calendar);
        if (!next.isPresent()) {
            return new ArrayList<>();
        }

        final Calendar startDate = calcStartDateTime(this);
        Optional<Calendar> previousSchedule = calcPreviousSchedule(this, next.get());
        if (!previousSchedule.isPresent() || startDate.getTimeInMillis() > previousSchedule.get().getTimeInMillis()) {
            return new ArrayList<>();
        }

        List<Calendar> ret = new ArrayList<>();
        ret.add(previousSchedule.get());
        for (int n=1; n<num; ++n) {
            previousSchedule = calcPreviousSchedule(this, ret.get(ret.size()-1));
            if (!previousSchedule.isPresent() || startDate.getTimeInMillis() > previousSchedule.get().getTimeInMillis()) {
                break;
            }
            ret.add(previousSchedule.get());
        }
        return ret.stream().map(Calendar::getTime).collect(toList());
    }

}
