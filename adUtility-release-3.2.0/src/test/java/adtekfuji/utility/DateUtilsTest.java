/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ke.yokoi
 */
public class DateUtilsTest {

    private static final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public DateUtilsTest() {
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

    /**
     * Test of getBeginningOfMonth method, of class DateUtils.
     */
    @Test
    public void testGetBeginningOfMonth() throws Exception {
        System.out.println("getBeginningOfMonth");

        Date date = df.parse("2016/2/16 13:35:40");
        Calendar start = Calendar.getInstance();
        start.setTime(DateUtils.getBeginningOfMonth(date));
        assertThat(start.get(Calendar.YEAR), is(2016));
        assertThat(start.get(Calendar.MONTH), is(1));
        assertThat(start.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(start.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(start.get(Calendar.MINUTE), is(0));
        assertThat(start.get(Calendar.SECOND), is(0));

        date = df.parse("2017/2/16 13:35:40");
        start = Calendar.getInstance();
        start.setTime(DateUtils.getBeginningOfMonth(date));
        assertThat(start.get(Calendar.YEAR), is(2017));
        assertThat(start.get(Calendar.MONTH), is(1));
        assertThat(start.get(Calendar.DAY_OF_MONTH), is(1));
        assertThat(start.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(start.get(Calendar.MINUTE), is(0));
        assertThat(start.get(Calendar.SECOND), is(0));
    }

    /**
     * Test of getEndOfMonth method, of class DateUtils.
     */
    @Test
    public void testGetEndOfMonth() throws Exception {
        System.out.println("getEndOfMonth");

        Date date = df.parse("2016/2/16 13:35:40");
        Calendar end = Calendar.getInstance();
        end.setTime(DateUtils.getEndOfMonth(date));
        assertThat(end.get(Calendar.YEAR), is(2016));
        assertThat(end.get(Calendar.MONTH), is(1));
        assertThat(end.get(Calendar.DAY_OF_MONTH), is(29));
        assertThat(end.get(Calendar.HOUR_OF_DAY), is(23));
        assertThat(end.get(Calendar.MINUTE), is(59));
        assertThat(end.get(Calendar.SECOND), is(59));

        date = df.parse("2017/2/16 13:35:40");
        end = Calendar.getInstance();
        end.setTime(DateUtils.getEndOfMonth(date));
        assertThat(end.get(Calendar.YEAR), is(2017));
        assertThat(end.get(Calendar.MONTH), is(1));
        assertThat(end.get(Calendar.DAY_OF_MONTH), is(28));
        assertThat(end.get(Calendar.HOUR_OF_DAY), is(23));
        assertThat(end.get(Calendar.MINUTE), is(59));
        assertThat(end.get(Calendar.SECOND), is(59));
    }

    /**
     * Test of toDate method, of class DateUtils.
     */
    @Test
    public void testToDate() throws Exception {
        System.out.println("toDate");

        Date date;
        date = DateUtils.toDate(LocalDate.of(2016, 3, 1), LocalTime.of(8, 45, 0));
        assertThat(date, is(df.parse("2016/3/1 8:45:00")));

        date = DateUtils.toDate(LocalDateTime.of(2016, 10, 26, 23, 59, 59));
        assertThat(date, is(df.parse("2016/10/26 23:59:59")));
    }

    /**
     * Test of toDate method, of class DateUtils.
     */
    @Test
    public void testToLocalDateTime() throws Exception {
        System.out.println("toLocalDateTime");

        LocalDateTime dateTime;
        dateTime = DateUtils.toLocalDateTime(df.parse("2016/3/1 8:45:00"));
        assertThat(dateTime, is(LocalDateTime.of(2016, 3, 1, 8, 45, 0)));
    }

}
