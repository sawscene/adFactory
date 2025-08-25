package jp.adtekfuji.adFactory.entity;

import jp.adtekfuji.adFactory.entity.schedule.ScheduleConditionInfoEntity;
import jp.adtekfuji.adFactory.enumerate.NumberTypeEnum;
import jp.adtekfuji.adFactory.enumerate.SchedulePatternEnum;
import jp.adtekfuji.adFactory.enumerate.WeekTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.junit.*;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;


public class ScheduleConditionEntityTest {

    final static SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
    final static SimpleDateFormat sf_check = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public ScheduleConditionEntityTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDateProperty() throws ParseException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ScheduleConditionInfoEntity scheduleConditionInfoEntity = new ScheduleConditionInfoEntity();
        scheduleConditionInfoEntity.setStartDate(sf.parse("2023/03/28 00:00:00:000"));
        scheduleConditionInfoEntity.setDateTime("00:00");
        scheduleConditionInfoEntity.setSchedulePattern(SchedulePatternEnum.DAY);
        scheduleConditionInfoEntity.setDayPeriod(1);


        Method getNextScheduleImpl = ScheduleConditionInfoEntity.class.getDeclaredMethod("getNextScheduleImpl", ScheduleConditionInfoEntity.class, Calendar.class);
        getNextScheduleImpl.setAccessible(true);

        Method calcNextSchedule = ScheduleConditionInfoEntity.class.getDeclaredMethod("calcNextSchedule", ScheduleConditionInfoEntity.class, Calendar.class);
        calcNextSchedule.setAccessible(true);

        Method calcPreviousSchedule = ScheduleConditionInfoEntity.class.getDeclaredMethod("calcPreviousSchedule", ScheduleConditionInfoEntity.class, Calendar.class);
        calcPreviousSchedule.setAccessible(true);

        Calendar base = Calendar.getInstance();
        base.set(2023, Calendar.JANUARY, 1, 0, 0, 0);
        base.set(Calendar.MILLISECOND, 0);
        for (int y = 2023; y < 2033; ++y) {
            base.set(Calendar.YEAR, y);
            for (int m = 0; m < 12; ++m) {
                base.set(Calendar.MONTH, m);
                for (int p = 1; p < 7; ++p) {
                    scheduleConditionInfoEntity.setDayPeriod(p);
                    for (int s : Arrays.asList(-1, 0, 1)) {

                        Calendar secCalendar = (Calendar) base.clone();
                        secCalendar.add(Calendar.SECOND, s);

                        scheduleConditionInfoEntity.setStartDate(base.getTime());
                        scheduleConditionInfoEntity.setDateTime("00:00");
                        Optional<Calendar> date = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, secCalendar);

                        if (s == -1) {
                            assertEquals(base.getTime(), date.get().getTime());
                        }
//                        if (s == 0) {
//                            assertEquals(base.getTime(), date.get().getTime());
//                        }
                        if (s == 0 || s == 1) {
                            Calendar tmp = (Calendar) base.clone();
                            tmp.add(Calendar.DAY_OF_MONTH, p);
                            assertEquals(tmp.getTime(), date.get().getTime());
                        }

                        Optional<Calendar> nextDate = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, date.get());
                        Optional<Calendar> prevDate = (Optional<Calendar>)  calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
                        Optional<Calendar> prevDate2 = (Optional<Calendar>)  calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate.get());
                        Optional<Calendar> nextDate2 = (Optional<Calendar>)  calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate2.get());
                        assertEquals(nextDate2.get().getTime(), date.get().getTime());
                    }
                }
            }
        }


        scheduleConditionInfoEntity.setStartDate(sf.parse("2023/04/17 00:00:00:000"));
        scheduleConditionInfoEntity.setDateTime("12:25");
        scheduleConditionInfoEntity.setSchedulePattern(SchedulePatternEnum.DAY);
        scheduleConditionInfoEntity.setDayPeriod(3);

        Calendar inCalendar = Calendar.getInstance();
        Calendar expected = Calendar.getInstance();
        Calendar actual;
        scheduleConditionInfoEntity.setDayPeriod(1);
        {
            inCalendar.setTime(sf.parse("2023/04/21 12:00:00:000"));
            actual = ((Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, inCalendar)).get();
            expected.setTime(sf.parse("2023/04/21 12:25:00:000"));
            assertEquals(expected.getTime(), actual.getTime());
        }

        {
            inCalendar.setTime(sf.parse("2023/04/21 12:25:00:000"));
            actual = ((Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, inCalendar)).get();
            expected.setTime(sf.parse("2023/04/22 12:25:00:000"));
            assertEquals(expected.getTime(), actual.getTime());
        }

        scheduleConditionInfoEntity.setDayPeriod(3);
        {
            inCalendar.setTime(sf.parse("2023/04/21 12:00:00:000"));
            actual = ((Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, inCalendar)).get();
            expected.setTime(sf.parse("2023/04/23 12:25:00:000"));
            assertEquals(expected.getTime(), actual.getTime());
        }

        {
            inCalendar.setTime(sf.parse("2023/04/20 12:00:00:000"));
            actual = ((Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, inCalendar)).get();
            expected.setTime(sf.parse("2023/04/20 12:25:00:000"));
            assertEquals(expected.getTime(), actual.getTime());
        }

        {
            inCalendar.setTime(sf.parse("2023/04/20 12:26:00:000"));
            actual = ((Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, inCalendar)).get();
            expected.setTime(sf.parse("2023/04/23 12:25:00:000"));
            assertEquals(expected.getTime(), actual.getTime());
        }

        {
            inCalendar.setTime(sf.parse("2023/04/20 12:25:00:000"));
            actual = ((Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, inCalendar)).get();
            expected.setTime(sf.parse("2023/04/23 12:25:00:000"));
            assertEquals(expected.getTime(), actual.getTime());
        }

    }

    @Test
    public void testWeekProperty() throws ParseException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ScheduleConditionInfoEntity scheduleConditionInfoEntity = new ScheduleConditionInfoEntity();
        scheduleConditionInfoEntity.setStartDate(sf.parse("2023/03/28 00:00:00:000"));
        scheduleConditionInfoEntity.setDateTime("00:00");
        scheduleConditionInfoEntity.setSchedulePattern(SchedulePatternEnum.WEEK);
        scheduleConditionInfoEntity.setWeekPeriod(1);
        scheduleConditionInfoEntity.setWeeks(JsonUtils.objectsToJson(Arrays.asList(WeekTypeEnum.values())));


        Method getNextScheduleImpl = ScheduleConditionInfoEntity.class.getDeclaredMethod("getNextScheduleImpl", ScheduleConditionInfoEntity.class, Calendar.class);
        getNextScheduleImpl.setAccessible(true);

        Method calcNextSchedule = ScheduleConditionInfoEntity.class.getDeclaredMethod("calcNextSchedule", ScheduleConditionInfoEntity.class, Calendar.class);
        calcNextSchedule.setAccessible(true);

        Method calcPreviousSchedule = ScheduleConditionInfoEntity.class.getDeclaredMethod("calcPreviousSchedule", ScheduleConditionInfoEntity.class, Calendar.class);
        calcPreviousSchedule.setAccessible(true);

        Calendar base = Calendar.getInstance();
        base.set(2023, Calendar.JANUARY, 1, 0, 0, 0);
        base.set(Calendar.MILLISECOND, 0);
        for (int y = 2023; y < 2033; ++y) {
            base.set(Calendar.YEAR, y);
            for (int m = 0; m < 12; ++m) {
                base.set(Calendar.MONTH, m);
                for (int p = 1; p < 7; ++p) {
                    scheduleConditionInfoEntity.setDayPeriod(p);
                    for (int s : Arrays.asList(-1, 0, 1)) {
                        Calendar secCalendar = (Calendar) base.clone();
                        secCalendar.add(Calendar.SECOND, s);

                        scheduleConditionInfoEntity.setStartDate(base.getTime());
                        scheduleConditionInfoEntity.setDateTime("00:00");
                        Optional<Calendar> date = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, secCalendar);

                        if (s == -1) {
                            assertEquals(base.getTime(), date.get().getTime());
                        }
//                        if (s == 0) {
//                            assertEquals(base.getTime(), date.get().getTime());
//                        }
                        if (s == 0 || s == 1) {
                            Calendar tmp = (Calendar) base.clone();
                            tmp.add(Calendar.DAY_OF_MONTH, 1);
                            assertEquals(tmp.getTime(), date.get().getTime());
                        }

                        Optional<Calendar> nextDate = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, date.get());
                        Optional<Calendar> prevDate = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
                        Optional<Calendar> prevDate2 = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate.get());
                        Optional<Calendar> nextDate2 = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate2.get());
                        assertEquals(date.get().getTime(), nextDate2.get().getTime());
                    }
                }
            }
        }

        {
            // 設定開始時間 2022/03/28 12:00 (日曜日, 土曜日)
            scheduleConditionInfoEntity.setStartDate(sf.parse("2022/03/28 00:00:00:000"));
            scheduleConditionInfoEntity.setDateTime("12:00");
            scheduleConditionInfoEntity.setWeeks(JsonUtils.objectsToJson(Arrays.asList(WeekTypeEnum.SUNDAY, WeekTypeEnum.SATURDAY)));


            base.set(2023, Calendar.MARCH, 29, 12, 0, 0);
            base.set(Calendar.MILLISECOND, 0);
            Optional<Calendar> nextDate = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, base);
            assertEquals("2023/04/01 12:00", sf_check.format(nextDate.get().getTime()));

            Optional<Calendar> nextDate2 = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
            assertEquals("2023/04/02 12:00", sf_check.format(nextDate2.get().getTime()));

            Optional<Calendar> prevDate = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
            assertEquals("2023/03/26 12:00", sf_check.format(prevDate.get().getTime()));
        }


        {
            // 設定開始時間 2022/03/28 12:00 (水曜日)
            scheduleConditionInfoEntity.setStartDate(sf.parse("2022/03/28 00:00:00:000"));
            scheduleConditionInfoEntity.setDateTime("12:00");
            scheduleConditionInfoEntity.setWeeks(JsonUtils.objectsToJson(Collections.singletonList(WeekTypeEnum.WEDNESDAY)));

            // 現在の時間 2023/03/29 12:01
            base.set(2023, Calendar.MARCH, 29, 12, 1, 0);
            base.set(Calendar.MILLISECOND, 0);

            Optional<Calendar> nextDate = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, base);
            assertEquals("2023/04/05 12:00", sf_check.format(nextDate.get().getTime()));


            Optional<Calendar> nextDate2 = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
            assertEquals("2023/04/12 12:00", sf_check.format(nextDate2.get().getTime()));

            Optional<Calendar> prevDate = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
            assertEquals("2023/03/29 12:00", sf_check.format(prevDate.get().getTime()));
        }






    }

    @Test
    public void testMonthDayProperty() throws ParseException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ScheduleConditionInfoEntity scheduleConditionInfoEntity = new ScheduleConditionInfoEntity();
        scheduleConditionInfoEntity.setStartDate(sf.parse("2023/03/28 00:00:00:000"));
        scheduleConditionInfoEntity.setDateTime("00:00");
        scheduleConditionInfoEntity.setSchedulePattern(SchedulePatternEnum.MONTH);
        scheduleConditionInfoEntity.setMonthSchedulePattern(SchedulePatternEnum.DAY);
        scheduleConditionInfoEntity.setMonthDayMonth(1);
        scheduleConditionInfoEntity.setMonthDayDay(1);

        Method getNextScheduleImpl = ScheduleConditionInfoEntity.class.getDeclaredMethod("getNextScheduleImpl", ScheduleConditionInfoEntity.class, Calendar.class);
        getNextScheduleImpl.setAccessible(true);

        Method calcNextSchedule = ScheduleConditionInfoEntity.class.getDeclaredMethod("calcNextSchedule", ScheduleConditionInfoEntity.class, Calendar.class);
        calcNextSchedule.setAccessible(true);

        Method calcPreviousSchedule = ScheduleConditionInfoEntity.class.getDeclaredMethod("calcPreviousSchedule", ScheduleConditionInfoEntity.class, Calendar.class);
        calcPreviousSchedule.setAccessible(true);

        Calendar base = Calendar.getInstance();
        base.set(2023, Calendar.JANUARY, 1, 0, 0, 0);
        base.set(Calendar.MILLISECOND, 0);
        for (int y = 2023; y < 2033; ++y) {
            base.set(Calendar.YEAR, y);
            for (int m = 0; m < 12; ++m) {
                base.set(Calendar.MONTH, m);
                Calendar base_first = (Calendar) base.clone();
                Calendar base_last = (Calendar) base.clone();
                base_last.add(Calendar.MINUTE, -1);
                for (int p = 1; p < 12; ++p) {
                    scheduleConditionInfoEntity.setMonthDayMonth(p);
                    for (int s : Arrays.asList(-1, 0, 1)) {
                        // --------------------------- 月初 ---------------------------
                        scheduleConditionInfoEntity.setStartDate(base_first.getTime());
                        scheduleConditionInfoEntity.setDateTime("00:00");
                        scheduleConditionInfoEntity.setMonthDayDay(1);

                        Calendar  secCalendar_first = (Calendar) base_first.clone();
                        secCalendar_first.add(Calendar.SECOND, s);
                        Optional<Calendar>  date_first = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, secCalendar_first);

                        if (s == -1) {
                            assertEquals(base_first.getTime(), date_first.get().getTime());
                        }
//                        if (s == 0) {
//                            assertEquals(base_first.getTime(), date_first.get().getTime());
//                        }
                        if (s == 0 || s == 1) {
                            Calendar  tmp_first = (Calendar) base_first.clone();
                            tmp_first.add(Calendar.MONTH, p);
                            assertEquals(tmp_first.getTime(), date_first.get().getTime());
                        }

                        {
                            Optional<Calendar>  nextDate = (Optional<Calendar> ) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, date_first.get());
                            Optional<Calendar>  prevDate = (Optional<Calendar> ) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
                            Optional<Calendar>  prevDate2 = (Optional<Calendar> ) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate.get());
                            Optional<Calendar>  nextDate2 = (Optional<Calendar> ) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate2.get());
                            assertEquals(date_first.get().getTime(), nextDate2.get().getTime());
                        }

                        // --------------------------- 月末 ---------------------------
                        scheduleConditionInfoEntity.setStartDate(base_last.getTime());
                        scheduleConditionInfoEntity.setDateTime("23:59");
                        scheduleConditionInfoEntity.setMonthDayDay(31);
                        Calendar secCalendar_last = (Calendar) base_last.clone();
                        secCalendar_last.add(Calendar.SECOND, s);
                        Optional<Calendar>  date_last = (Optional<Calendar> ) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, secCalendar_last);

                        if (s == -1) {
                            assertEquals(base_last.getTime(), date_last.get().getTime());
                        }
//                        if (s == 0) {
//                            assertEquals(base_last.getTime(), date_last.get().getTime());
//                        }
                        if (s == 0 || s == 1) {
                            Calendar tmp_last = (Calendar) base_first.clone();
                            tmp_last.add(Calendar.MONTH, p);
                            tmp_last.add(Calendar.MINUTE, -1);
                            assertEquals(tmp_last.getTime(), date_last.get().getTime());
                        }

                        {
                            Optional<Calendar>  nextDate = (Optional<Calendar> ) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, date_last.get());
                            Optional<Calendar>  prevDate = (Optional<Calendar> ) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
                            Optional<Calendar>  prevDate2 = (Optional<Calendar> ) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate.get());
                            Optional<Calendar>  nextDate2 = (Optional<Calendar> ) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate2.get());
                            assertEquals(date_last.get().getTime(), nextDate2.get().getTime());
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testMonthWeekProperty() throws ParseException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ScheduleConditionInfoEntity scheduleConditionInfoEntity = new ScheduleConditionInfoEntity();
        scheduleConditionInfoEntity.setStartDate(sf.parse("2023/03/28 00:00:00:000"));
        scheduleConditionInfoEntity.setDateTime("00:00");
        scheduleConditionInfoEntity.setSchedulePattern(SchedulePatternEnum.MONTH);
        scheduleConditionInfoEntity.setMonthSchedulePattern(SchedulePatternEnum.WEEK);
        scheduleConditionInfoEntity.setMonthWeekMonth(1);
        scheduleConditionInfoEntity.setMonthWeekWeek(NumberTypeEnum.FIRST);
        scheduleConditionInfoEntity.setMonthWeekDay(WeekTypeEnum.SUNDAY);

        Method getNextScheduleImpl = ScheduleConditionInfoEntity.class.getDeclaredMethod("getNextScheduleImpl", ScheduleConditionInfoEntity.class, Calendar.class);
        getNextScheduleImpl.setAccessible(true);

        Method calcNextSchedule = ScheduleConditionInfoEntity.class.getDeclaredMethod("calcNextSchedule", ScheduleConditionInfoEntity.class, Calendar.class);
        calcNextSchedule.setAccessible(true);

        Method calcPreviousSchedule = ScheduleConditionInfoEntity.class.getDeclaredMethod("calcPreviousSchedule", ScheduleConditionInfoEntity.class, Calendar.class);
        calcPreviousSchedule.setAccessible(true);

        Calendar base = Calendar.getInstance();

        {
            // 設定開始時間 2022/03/29 12:00 (日曜日)
            scheduleConditionInfoEntity.setStartDate(sf.parse("2022/03/29 00:00:00:000"));
            scheduleConditionInfoEntity.setDateTime("12:00");
            scheduleConditionInfoEntity.setMonthWeekMonth(1);
            scheduleConditionInfoEntity.setMonthWeekWeek(NumberTypeEnum.FIRST);
            scheduleConditionInfoEntity.setMonthWeekDay(WeekTypeEnum.SUNDAY);

            // 現在の時間 2023/03/29 12:00
            base.set(2023, Calendar.MARCH, 29, 12, 0, 0);
            base.set(Calendar.MILLISECOND, 0);

            Optional<Calendar> date = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, base);
            assertEquals("2023/04/30 12:00", sf_check.format(date.get().getTime()));

            Optional<Calendar> nextDate = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, date.get());
            Optional<Calendar> prevDate = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
            Optional<Calendar> prevDate2 = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate.get());
            Optional<Calendar> nextDate2 = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate2.get());
            assertEquals(date.get().getTime(), nextDate2.get().getTime());
        }

        {
            // 設定開始時間 2022/03/29 12:00 (水曜日)
            scheduleConditionInfoEntity.setStartDate(sf.parse("2022/03/29 00:00:00:000"));
            scheduleConditionInfoEntity.setDateTime("12:00");
            scheduleConditionInfoEntity.setMonthWeekMonth(1);
            scheduleConditionInfoEntity.setMonthWeekWeek(NumberTypeEnum.FIRST);
            scheduleConditionInfoEntity.setMonthWeekDay(WeekTypeEnum.WEDNESDAY);

            // 現在の時間 2023/03/29 12:00
            base.set(2023, Calendar.MARCH, 29, 12, 0, 0);
            base.set(Calendar.MILLISECOND, 0);
            Optional<Calendar> date = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, base);
            assertEquals("2023/05/03 12:00", sf_check.format(date.get().getTime()));

            Optional<Calendar> nextDate = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, date.get());
            Optional<Calendar> prevDate = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
            Optional<Calendar> prevDate2 = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate.get());
            Optional<Calendar> nextDate2 = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate2.get());
            assertEquals(date.get().getTime(), nextDate2.get().getTime());
        }

        {
            // 設定開始時間 2022/03/29 12:00 (水曜日)
            scheduleConditionInfoEntity.setStartDate(sf.parse("2022/03/29 00:00:00:000"));
            scheduleConditionInfoEntity.setDateTime("12:00");
            scheduleConditionInfoEntity.setMonthWeekMonth(1);
            scheduleConditionInfoEntity.setMonthWeekWeek(NumberTypeEnum.FIRST);
            scheduleConditionInfoEntity.setMonthWeekDay(WeekTypeEnum.SATURDAY);

            // 現在の時間 2023/03/29 12:00
            base.set(2023, Calendar.MARCH, 29, 12, 0, 0);
            base.set(Calendar.MILLISECOND, 0);
            Optional<Calendar> date = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, base);
            assertEquals("2023/04/01 12:00", sf_check.format(date.get().getTime()));

            Optional<Calendar> nextDate = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, date.get());
            Optional<Calendar> prevDate = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
            Optional<Calendar> prevDate2 = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate.get());
            Optional<Calendar> nextDate2 = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate2.get());
            assertEquals(date.get().getTime(), nextDate2.get().getTime());
        }

        {
            // 設定開始時間 2022/03/29 12:00 (日曜日)
            scheduleConditionInfoEntity.setStartDate(sf.parse("2022/03/29 00:00:00:000"));
            scheduleConditionInfoEntity.setDateTime("12:00");
            scheduleConditionInfoEntity.setMonthWeekMonth(1);
            scheduleConditionInfoEntity.setMonthWeekWeek(NumberTypeEnum.LAST);
            scheduleConditionInfoEntity.setMonthWeekDay(WeekTypeEnum.SUNDAY);

            // 現在の時間 2023/03/29 12:00
            base.set(2023, Calendar.MARCH, 29, 12, 0, 0);
            base.set(Calendar.MILLISECOND, 0);

            Optional<Calendar> date = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, base);
            assertEquals("2023/04/30 12:00", sf_check.format(date.get().getTime()));

            Optional<Calendar> nextDate = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, date.get());
            Optional<Calendar> prevDate = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
            Optional<Calendar> prevDate2 = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate.get());
            Optional<Calendar> nextDate2 = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate2.get());
            assertEquals(date.get().getTime(), nextDate2.get().getTime());
        }

        {
            // 設定開始時間 2022/03/29 12:00 (日曜日)
            scheduleConditionInfoEntity.setStartDate(sf.parse("2022/03/29 00:00:00:000"));
            scheduleConditionInfoEntity.setDateTime("12:00");
            scheduleConditionInfoEntity.setMonthWeekMonth(1);
            scheduleConditionInfoEntity.setMonthWeekWeek(NumberTypeEnum.LAST);
            scheduleConditionInfoEntity.setMonthWeekDay(WeekTypeEnum.WEDNESDAY);

            // 現在の時間 2023/03/29 12:00
            base.set(2023, Calendar.MARCH, 29, 12, 0, 0);
            base.set(Calendar.MILLISECOND, 0);

            Optional<Calendar> date = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, base);
            assertEquals("2023/05/03 12:00", sf_check.format(date.get().getTime()));

            Optional<Calendar> nextDate = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, date.get());
            Optional<Calendar> prevDate = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
            Optional<Calendar> prevDate2 = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate.get());
            Optional<Calendar> nextDate2 = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate2.get());
            assertEquals(date.get().getTime(), nextDate2.get().getTime());
        }

        {
            // 設定開始時間 2022/03/29 12:00 (日曜日)
            scheduleConditionInfoEntity.setStartDate(sf.parse("2022/03/29 00:00:00:000"));
            scheduleConditionInfoEntity.setDateTime("12:00");
            scheduleConditionInfoEntity.setMonthWeekMonth(2);
            scheduleConditionInfoEntity.setMonthWeekWeek(NumberTypeEnum.FIRST);
            scheduleConditionInfoEntity.setMonthWeekDay(WeekTypeEnum.SUNDAY);

            // 現在の時間 2023/03/29 12:00
            base.set(2023, Calendar.MARCH, 29, 12, 0, 0);
            base.set(Calendar.MILLISECOND, 0);

            Optional<Calendar> date = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, base);
            assertEquals("2023/04/30 12:00", sf_check.format(date.get().getTime()));

            Optional<Calendar> nextDate = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, date.get());
            Optional<Calendar> prevDate = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
            Optional<Calendar> prevDate2 = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate.get());
            Optional<Calendar> nextDate2 = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate2.get());
            assertEquals(date.get().getTime(), nextDate2.get().getTime());
        }

        {
            // 設定開始時間 2022/03/29 12:00 (日曜日)
            scheduleConditionInfoEntity.setStartDate(sf.parse("2022/03/29 00:00:00:000"));
            scheduleConditionInfoEntity.setDateTime("12:00");
            scheduleConditionInfoEntity.setMonthWeekMonth(3);
            scheduleConditionInfoEntity.setMonthWeekWeek(NumberTypeEnum.FIRST);
            scheduleConditionInfoEntity.setMonthWeekDay(WeekTypeEnum.SUNDAY);

            // 現在の時間 2023/03/29 12:00
            base.set(2023, Calendar.MARCH, 29, 12, 0, 0);
            base.set(Calendar.MILLISECOND, 0);

            Optional<Calendar> date = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, base);
            assertEquals("2023/05/28 12:00", sf_check.format(date.get().getTime()));

            Optional<Calendar> nextDate = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, date.get());
            Optional<Calendar> prevDate = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
            Optional<Calendar> prevDate2 = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate.get());
            Optional<Calendar> nextDate2 = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate2.get());
            assertEquals(date.get().getTime(), nextDate2.get().getTime());
        }

        base.set(2023, Calendar.JANUARY, 1, 0, 0, 0);
        base.set(Calendar.MILLISECOND, 0);
        for (int y = 2020; y < 2033; ++y) {
            base.set(Calendar.YEAR, y);
            for (int m = 0; m < 12; ++m) {
                base.set(Calendar.MONTH, m);
                for (NumberTypeEnum type : Arrays.asList(NumberTypeEnum.FIRST, NumberTypeEnum.FOURTH, NumberTypeEnum.LAST)) {
                    for (int p = 1; p < 12; ++p) {
                        for (WeekTypeEnum s : Arrays.asList(WeekTypeEnum.SUNDAY, WeekTypeEnum.WEDNESDAY, WeekTypeEnum.SATURDAY)) {
                            // 設定開始時間 2022/03/29 12:00 (日曜日)
                            scheduleConditionInfoEntity.setStartDate(sf.parse("2022/03/29 00:00:00:000"));
                            scheduleConditionInfoEntity.setDateTime("12:00");
                            scheduleConditionInfoEntity.setMonthWeekMonth(p);
                            scheduleConditionInfoEntity.setMonthWeekWeek(type);
                            scheduleConditionInfoEntity.setMonthWeekDay(s);

                            Optional<Calendar> date = (Optional<Calendar>) getNextScheduleImpl.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, base);
                            Optional<Calendar> nextDate = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, date.get());
                            Optional<Calendar> prevDate = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, nextDate.get());
                            Optional<Calendar> prevDate2 = (Optional<Calendar>) calcPreviousSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate.get());
                            Optional<Calendar> nextDate2 = (Optional<Calendar>) calcNextSchedule.invoke(scheduleConditionInfoEntity, scheduleConditionInfoEntity, prevDate2.get());
                            assertEquals(date.get().getTime(), nextDate2.get().getTime());
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testNextDateProperty() throws ParseException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Calendar base = Calendar.getInstance();
        Calendar inCalendar = Calendar.getInstance();
        Calendar expected = Calendar.getInstance();
        Calendar actual;

        {
            ScheduleConditionInfoEntity scheduleConditionInfoEntity = new ScheduleConditionInfoEntity();
            scheduleConditionInfoEntity.setStartDate(sf.parse("2023/04/25 00:00:00:000"));
            scheduleConditionInfoEntity.setDateTime("19:00");
            scheduleConditionInfoEntity.setSchedulePattern(SchedulePatternEnum.DAY);
            scheduleConditionInfoEntity.setDayPeriod(1);

            // 現在の時間 2023/03/29 12:00
            base.setTime(sf.parse("2023/04/25 13:00:00:000"));

            Optional<Date> result = scheduleConditionInfoEntity.getNextSchedule(base.getTime());
            assertEquals("2023/04/25 19:00", sf_check.format(result.get().getTime()));
        }
    }

}
