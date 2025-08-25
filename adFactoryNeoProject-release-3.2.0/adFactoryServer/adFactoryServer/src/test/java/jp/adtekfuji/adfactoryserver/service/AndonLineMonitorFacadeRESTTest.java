/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.DateUtils;
import java.io.File;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import jakarta.xml.bind.JAXB;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LineManagedCommandEnum;
import jp.adtekfuji.adFactory.enumerate.LineManagedStateEnum;
import jp.adtekfuji.adfactoryserver.entity.master.BreaktimeEntity;
import jp.adtekfuji.adfactoryserver.model.LineManager;
import jp.adtekfuji.adfactoryserver.utility.AggregateLineInfoFacade;
import jp.adtekfuji.adfactoryserver.utility.AggregateMonitorInfoFacade;
import jp.adtekfuji.adfactoryserver.utility.BreakTimeUtils;
import jp.adtekfuji.andon.entity.EstimatedTimeInfoEntity;
import jp.adtekfuji.andon.entity.LineTimerControlRequest;
import jp.adtekfuji.andon.entity.MonitorLineTimerInfoEntity;
import jp.adtekfuji.andon.entity.MonitorPlanDeviatedInfoEntity;
import jp.adtekfuji.andon.property.AgendaMonitorSetting;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * AndonLineMonitorFacadeRESTテスト
 *
 * @author s-heya
 */
public class AndonLineMonitorFacadeRESTTest {

    private static AndonLineMonitorFacadeREST lineMonitorRest = null;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public AndonLineMonitorFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        lineMonitorRest = new AndonLineMonitorFacadeREST();
        lineMonitorRest.setUpTest();
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
    public void testPostDailyTimerControlRequest() throws Exception {
        System.out.println("postDailyTimerControlRequest");

        final long monitorId = 1L;
        final long startCount = 30;
        final long taktTime = 5 * 60;
        Date now;
        LineTimerControlRequest request;
        MonitorLineTimerInfoEntity ret;

        AndonMonitorLineProductSetting setting = JAXB.unmarshal(new File("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\service\\MonitorLineProductSetting_line001.xml").getAbsolutePath(), AndonMonitorLineProductSetting.class);

        final BreakTimeInfoEntity breaktime1 = new BreakTimeInfoEntity("休憩1", format.parse("2000/01/01 12:00:00"), format.parse("2000/01/01 12:45:00"));
        final BreakTimeInfoEntity breaktime2 = new BreakTimeInfoEntity("休憩2", format.parse("2000/01/01 17:00:00"), format.parse("2000/01/01 17:15:00"));

        final AggregateLineInfoFacade facade = new AggregateLineInfoFacade();
        facade.setAdIntefaceClientFacade(new MockAdIntefaceClientFacade());

        LineManager.getInstance().setUpTest();

        //まずは取得.
        now = format.parse("2016/02/22 10:00:00");
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.START_WAIT));
        assertThat(ret.getStartTime(), is(nullValue()));
        assertThat(ret.getLeftTimeSec(), is(0L));
        assertThat(ret.getTaktTime(), is(0L));
        //開始.
        now = format.parse("2016/02/22 10:00:00");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.START, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.STARTCOUNT));
        assertThat(ret.getStartTime(), is(format.parse("2016/02/22 10:00:00")));
        assertThat(ret.getLeftTimeSec(), is(startCount));
        assertThat(ret.getTaktTime(), is(taktTime));
        //スタートカウント一時停止.
        now = format.parse("2016/02/22 10:00:05");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.PAUSE, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.STARTCOUNT_PAUSE));
        assertThat(ret.getStartTime(), is(format.parse("2016/02/22 10:00:00")));
        assertThat(ret.getLeftTimeSec(), is(25L));
        assertThat(ret.getTaktTime(), is(taktTime));
        //スタートカウント再開.
        now = format.parse("2016/02/22 10:30:00");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.RESTART, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.STARTCOUNT));
        assertThat(ret.getStartTime(), is(format.parse("2016/02/22 10:30:00")));
        assertThat(ret.getLeftTimeSec(), is(25L));
        assertThat(ret.getTaktTime(), is(taktTime));
        //スタートカウント一時停止.
        now = format.parse("2016/02/22 10:30:05");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.PAUSE, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.STARTCOUNT_PAUSE));
        assertThat(ret.getStartTime(), is(format.parse("2016/02/22 10:30:00")));
        assertThat(ret.getLeftTimeSec(), is(20L));
        assertThat(ret.getTaktTime(), is(taktTime));
        //スタートカウント再開.
        now = format.parse("2016/02/22 10:30:10");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.RESTART, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.STARTCOUNT));
        assertThat(ret.getStartTime(), is(format.parse("2016/02/22 10:30:10")));
        assertThat(ret.getLeftTimeSec(), is(20L));
        assertThat(ret.getTaktTime(), is(taktTime));
        //タクトカウント開始.
        now = format.parse("2016/02/22 10:30:30");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.START, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.TAKTCOUNT));
        assertThat(ret.getStartTime(), is(format.parse("2016/02/22 10:30:30")));
        assertThat(ret.getLeftTimeSec(), is(taktTime));
        assertThat(ret.getTaktTime(), is(taktTime));
        //タクトカウント一時停止.
        now = format.parse("2016/02/22 10:31:30");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.PAUSE, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.TAKTCOUNT_PAUSE));
        assertThat(ret.getStartTime(), is(format.parse("2016/02/22 10:30:30")));
        assertThat(ret.getLeftTimeSec(), is(4 * 60L));
        assertThat(ret.getTaktTime(), is(taktTime));
        //タクトカウント再開.
        now = format.parse("2016/02/22 10:32:30");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.RESTART, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.TAKTCOUNT));
        assertThat(ret.getStartTime(), is(format.parse("2016/02/22 10:32:30")));
        assertThat(ret.getLeftTimeSec(), is(4 * 60L));
        assertThat(ret.getTaktTime(), is(taktTime));
        //タクトカウント一時停止.
        now = format.parse("2016/02/22 10:33:30");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.PAUSE, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.TAKTCOUNT_PAUSE));
        assertThat(ret.getStartTime(), is(format.parse("2016/02/22 10:32:30")));
        assertThat(ret.getLeftTimeSec(), is(3 * 60L));
        assertThat(ret.getTaktTime(), is(taktTime));
        //タクトカウント再開.
        now = format.parse("2016/02/22 10:34:30");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.RESTART, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.TAKTCOUNT));
        assertThat(ret.getStartTime(), is(format.parse("2016/02/22 10:34:30")));
        assertThat(ret.getLeftTimeSec(), is(3 * 60L));
        assertThat(ret.getTaktTime(), is(taktTime));
        //停止.
        now = format.parse("2016/02/22 10:35:30");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.STOP, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.STOP));
        assertThat(ret.getStartTime(), is(format.parse("2016/02/22 10:34:30")));
        assertThat(ret.getLeftTimeSec(), is(2 * 60L));
        assertThat(ret.getTaktTime(), is(taktTime));
        //リセット.
        now = format.parse("2016/02/22 11:00:00");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.RESET, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        ret = facade.getDailyTimerInfo(monitorId, now);
        assertThat(ret.getLineTimerState(), is(LineManagedStateEnum.START_WAIT));
        assertThat(ret.getStartTime(), is(nullValue()));
        assertThat(ret.getLeftTimeSec(), is(0L));
        assertThat(ret.getTaktTime(), is(taktTime));
    }

    /**
     * 自動カウントダウンのテスト
     *
     * @throws Exception
     */
    @Test
    public void testAutoCountdown() throws Exception {
        final long monitorId = 1L;
        final long startCount = 0L;
        final long taktTime = 300;
        MonitorLineTimerInfoEntity linTimer;
        LineTimerControlRequest request;
        Date now;

        final AggregateLineInfoFacade facade = new AggregateLineInfoFacade();
        facade.setAdIntefaceClientFacade(new MockAdIntefaceClientFacade());

        LineManager.getInstance().setUpTest();

        AndonMonitorLineProductSetting setting = JAXB.unmarshal(new File("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\service\\MonitorLineProductSetting_line001.xml").getAbsolutePath(), AndonMonitorLineProductSetting.class);
        setting.setAgendaMonitorSetting(AgendaMonitorSetting.create());
        assertThat(setting.hasAllAttributes(), is(true));

        // 休憩時間
        //   昼休憩 12:00:00 - 13:00:00
        //   リフレッシュタイム1 10:00:00 - 10:05:00
        //   リフレッシュタイム2 15:00:00 - 15:08:00

        String settingString;
        try (StringWriter sw = new StringWriter()) {
            JAXB.marshal(setting, sw);
            settingString = sw.toString();
        }
        lineMonitorRest.updateConfig(monitorId, settingString, null);

        now = format.parse("2016/02/22 10:00:00");
        linTimer = lineMonitorRest.getDailyTimerInfo(monitorId, now, null);
        assertThat(linTimer.getLineTimerState(), is(LineManagedStateEnum.START_WAIT));
        assertThat(linTimer.getStartTime(), is(nullValue()));
        assertThat(linTimer.getLeftTimeSec(), is(0L));
        assertThat(linTimer.getTaktTime(), is(0L));

        now = format.parse("2016/02/22 10:00:00");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.SETUP, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        linTimer = lineMonitorRest.getDailyTimerInfo(monitorId, now, null);
        assertThat(linTimer.getCycle(), is(0));
        assertThat(linTimer.delivered(), hasEntry(225L, 0));
        assertThat(linTimer.delivered(), hasEntry(226L, 0));

        // 開始
        now = format.parse("2016/02/22 10:00:00");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.START, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        linTimer = lineMonitorRest.getDailyTimerInfo(monitorId, now, null);
        assertThat(linTimer.getLineTimerState(), is(LineManagedStateEnum.TAKTCOUNT));
        assertThat(linTimer.getStartTime(), is(format.parse("2016/02/22 10:00:00")));
        assertThat(linTimer.getLeftTimeSec(), is(taktTime));
        assertThat(linTimer.getTaktTime(), is(taktTime));

        // 取得
        now = format.parse("2016/02/22 10:05:30");
        linTimer = facade.getDailyTimerInfo(monitorId, now);
        assertThat(linTimer.getLineTimerState(), is(LineManagedStateEnum.TAKTCOUNT));
        assertThat(linTimer.getStartTime(), is(format.parse("2016/02/22 10:00:00")));
        assertThat(linTimer.getLeftTimeSec(), is(600L)); // 休憩を挟んでいるため300秒の加算される
        assertThat(linTimer.getTaktTime(), is(taktTime));

        // 停止
        now = format.parse("2016/02/22 10:10:00");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.STOP, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        linTimer = facade.getDailyTimerInfo(monitorId, now);
        assertThat(linTimer.getLineTimerState(), is(LineManagedStateEnum.STOP));
        //assertThat(linTimer.getStartTime(), is(format.parse("2016/02/22 10:00:00")));
        //assertThat(linTimer.getLeftTimeSec(), is(-300L)); // 休憩を挟んでいるため300秒の遅延
        assertThat(linTimer.getStartTime(), is(format.parse("2016/02/22 10:10:00")));
        assertThat(linTimer.getLeftTimeSec(), is(0L));
        assertThat(linTimer.getTaktTime(), is(taktTime));

        // リセット
        now = format.parse("2016/02/22 11:00:00");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.RESET, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        linTimer = facade.getDailyTimerInfo(monitorId, now);
        assertThat(linTimer.getLineTimerState(), is(LineManagedStateEnum.START_WAIT));
        assertThat(linTimer.getStartTime(), is(nullValue()));
        assertThat(linTimer.getLeftTimeSec(), is(0L));
        assertThat(linTimer.getTaktTime(), is(taktTime));

        // 開始
        now = format.parse("2016/02/22 12:30:00");
        request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.START, now, startCount, taktTime);
        facade.postDailyTimerControlRequest(monitorId, request, now, setting);
        linTimer = lineMonitorRest.getDailyTimerInfo(monitorId, now, null);
        assertThat(linTimer.getLineTimerState(), is(LineManagedStateEnum.TAKTCOUNT));
        assertThat(linTimer.getStartTime(), is(format.parse("2016/02/22 12:30:00")));
        assertThat(linTimer.getLeftTimeSec(), is(300L + 1800)); // 休憩中のため1800秒が加算される
        assertThat(linTimer.getTaktTime(), is(taktTime));

        // 取得
        now = format.parse("2016/02/22 15:05:30");
        linTimer = facade.getDailyTimerInfo(monitorId, now);
        assertThat(linTimer.getLineTimerState(), is(LineManagedStateEnum.TAKTCOUNT));
        assertThat(linTimer.getStartTime(), is(format.parse("2016/02/22 12:30:00")));
        assertThat(linTimer.getLeftTimeSec(), is(300L + 1800L + 480L)); // 休憩時間が加算される
        assertThat(linTimer.getTaktTime(), is(taktTime));

        lineMonitorRest.deleteConfig(monitorId, null);
    }

    /**
     * 作業終了予想時間のテスト
     *
     * @throws Exception
     */
    @Test
    public void testGetEstimaedTime() throws Exception {
        final String paramStrings[] = { "08:15","08:30","09:30","10:00","10:03","10:05","11:15","12:00","12:40","13:00","14:00","15:00","15:05","15:10","15:35","16:24","17:05","17:30","17:45","17:50" };

        // 残り12
        final String[] resultsStrings12 = { "17:30","17:45","18:45","19:15","19:15","19:15","20:25","21:10","21:10","21:10","22:10","23:10","23:10","23:10","23:35","00:24","01:05","01:30","01:45","01:50" };
        // 残り8
        final String[] resultsStrings8 = { "14:40","14:55","16:05","16:35","16:35","16:35","17:45","18:30","18:30","18:30","19:30","20:30","20:30","20:30","20:55","21:44","22:25","22:50","23:05","23:10" };
        // 残り2
        final String[] resultsStrings4 = { "11:00","11:15","13:15","13:45","13:45","13:45","14:55","15:50","15:50","15:50","16:50","17:50","17:50","17:50","18:15","19:04","19:45","20:10","20:25","20:30" };
        // 残り3
        final String resultsStrings3[] = { "10:20","10:35","11:35","13:05","13:05","13:05","14:15","15:00","15:00","15:00","16:10","17:10","17:10","17:10","17:35","18:24","19:05","19:30","19:45","19:50" };
        // 残り2
        final String[] resultsStrings2 = { "09:35","09:50","10:55","11:25","11:25","11:25","13:35","14:20","14:20","14:20","15:30","16:30","16:30","16:30","16:55","17:44","18:25","18:50","19:05","19:10" };
        // 残り1
        final String[] resultsStrings1 = { "08:55","09:10","10:15","10:45","10:45","10:45","11:55","13:40","13:40","13:40","14:40","15:50","15:50","15:50","16:15","17:04","17:45","18:10","18:25","18:30" };


        final long monitorId = 2L;
        final LocalDate date = LocalDate.now();
        EstimatedTimeInfoEntity entity;

        final AggregateMonitorInfoFacade facade = new AggregateMonitorInfoFacade();

        LineManager lineManager = LineManager.getInstance();
        lineManager.setUpTest();

        AndonMonitorLineProductSetting setting = JAXB.unmarshal(new File("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\service\\MonitorLineProductSetting_line002.xml").getAbsolutePath(), AndonMonitorLineProductSetting.class);
        setting.setMonitorId(monitorId);
        setting.setAgendaMonitorSetting(AgendaMonitorSetting.create());
        assertThat(setting.hasAllAttributes(), is(true));

        List<Date> params = new ArrayList<>();
        for (String paramString : paramStrings) {
            params.add(DateUtils.toDate(date, LocalTime.parse(paramString)));
        }

        // 残り12の場合
        List<Date> results12 = new ArrayList<>();
        for (String resultsString : resultsStrings12) {
            LocalTime time = LocalTime.parse(resultsString);
            if (setting.getStartWorkTime().isBefore(time)) {
                results12.add(DateUtils.toDate(date, time));
            } else {
                results12.add(DateUtils.toDate(date.plusDays(1), time));
            }
        }

        MonitorLineTimerInfoEntity lineTimer = new MonitorLineTimerInfoEntity();
        lineTimer.setCycle(0);
        lineManager.setLineTimer(monitorId, lineTimer);

        for (int i = 0; i < params.size(); i++) {
            entity = facade.getEstimatedTime(setting, params.get(i));
            assertThat(entity.getEstimatedTime(), is(results12.get(i)));
            assertThat(entity.getRemaining(), is(12));
        }

        // 残り8の場合
        List<Date> results8 = new ArrayList<>();
        for (String resultsString : resultsStrings8) {
            LocalTime time = LocalTime.parse(resultsString);
            if (setting.getStartWorkTime().isBefore(time)) {
                results8.add(DateUtils.toDate(date, time));
            } else {
                results8.add(DateUtils.toDate(date.plusDays(1), time));
            }
        }

        lineTimer.setCycle(4);
        lineManager.setLineTimer(monitorId, lineTimer);

        for (int i = 0; i < params.size(); i++) {
            entity = facade.getEstimatedTime(setting, params.get(i));
            assertThat(entity.getEstimatedTime(), is(results8.get(i)));
            assertThat(entity.getRemaining(), is(8));
        }

        // 残り4の場合
        List<Date> results4 = new ArrayList<>();
        for (String resultsString : resultsStrings4) {
            LocalTime time = LocalTime.parse(resultsString);
            if (setting.getStartWorkTime().isBefore(time)) {
                results4.add(DateUtils.toDate(date, time));
            } else {
                results4.add(DateUtils.toDate(date.plusDays(1), time));
            }
        }

        lineTimer.setCycle(8);
        lineManager.setLineTimer(monitorId, lineTimer);

        for (int i = 0; i < params.size(); i++) {
            entity = facade.getEstimatedTime(setting, params.get(i));
            assertThat(entity.getEstimatedTime(), is(results4.get(i)));
            assertThat(entity.getRemaining(), is(4));
        }

        // 残り3の場合
        List<Date> results3 = new ArrayList<>();
        for (String resultsString : resultsStrings3) {
            LocalTime time = LocalTime.parse(resultsString);
            if (setting.getStartWorkTime().isBefore(time)) {
                results3.add(DateUtils.toDate(date, time));
            } else {
                results3.add(DateUtils.toDate(date.plusDays(1), time));
            }
        }

        lineTimer.setCycle(9);
        lineManager.setLineTimer(monitorId, lineTimer);

        for (int i = 0; i < params.size(); i++) {
            entity = facade.getEstimatedTime(setting, params.get(i));
            assertThat(entity.getEstimatedTime(), is(results3.get(i)));
            assertThat(entity.getRemaining(), is(3));
        }

        // 残り2の場合
        List<Date> results2 = new ArrayList<>();
        for (String resultsString : resultsStrings2) {
            LocalTime time = LocalTime.parse(resultsString);
            if (setting.getStartWorkTime().isBefore(time)) {
                results2.add(DateUtils.toDate(date, time));
            } else {
                results2.add(DateUtils.toDate(date.plusDays(1), time));
            }
        }

        lineTimer.setCycle(10);
        lineManager.setLineTimer(monitorId, lineTimer);

        for (int i = 0; i < params.size(); i++) {
            entity = facade.getEstimatedTime(setting, params.get(i));
            assertThat(entity.getEstimatedTime(), is(results2.get(i)));
            assertThat(entity.getRemaining(), is(2));
        }

        // 残り1の場合
        List<Date> results1 = new ArrayList<>();
        for (String resultsString : resultsStrings1) {
            LocalTime time = LocalTime.parse(resultsString);
            if (setting.getStartWorkTime().isBefore(time)) {
                results1.add(DateUtils.toDate(date, time));
            } else {
                results1.add(DateUtils.toDate(date.plusDays(1), time));
            }
        }

        lineTimer.setCycle(11);
        lineManager.setLineTimer(monitorId, lineTimer);

        for (int i = 0; i < params.size(); i++) {
            entity = facade.getEstimatedTime(setting, params.get(i));
            assertThat(entity.getEstimatedTime(), is(results1.get(i)));
            assertThat(entity.getRemaining(), is(1));
        }

        // 残り0の場合
        lineTimer.setCycle(12);
        lineManager.setLineTimer(monitorId, lineTimer);

        for (int i = 0; i < params.size(); i++) {
            entity = facade.getEstimatedTime(setting, params.get(i));
            assertThat(entity.getEstimatedTime(), is(nullValue()));
            assertThat(entity.getRemaining(), is(0));
        }
    }

    /**
     * 当日進捗のテスト
     *
     * @throws Exception
     */
    @Test
    public void testGetDailyDeviatedInfo()  throws Exception {
        final long monitorId = 2L;
        final AggregateMonitorInfoFacade facade = new AggregateMonitorInfoFacade();
        MonitorPlanDeviatedInfoEntity entity;

        LineManager lineManager = LineManager.getInstance();
        lineManager.setUpTest();

        AndonMonitorLineProductSetting setting = JAXB.unmarshal(new File("src\\test\\resources\\jp\\adtekfuji\\adfactoryserver\\service\\MonitorLineProductSetting_line002.xml").getAbsolutePath(), AndonMonitorLineProductSetting.class);
        setting.setAgendaMonitorSetting(AgendaMonitorSetting.create());
        setting.setMonitorId(monitorId);
        assertThat(setting.hasAllAttributes(), is(true));

        //Date now = null;
        //entity = facade.getDailyDeviatedInfo(setting, now);
    }

    /**
     * 時間帯から休憩時間を取得するテスト
     *
     * @throws Exception
     */
    @Test
    public void testGetBreakTime() throws Exception {
        Date start;
        Date end;
        List<BreaktimeEntity> breakInWork;
        long breakTime;

        final BreakTimeInfoEntity breaktime1 = new BreakTimeInfoEntity("休憩1", format.parse("2000/01/01 10:00:00"), format.parse("2000/01/01 10:10:00"));
        final BreakTimeInfoEntity breaktime2 = new BreakTimeInfoEntity("休憩2", format.parse("2000/01/01 12:00:00"), format.parse("2000/01/01 13:00:00"));
        final BreakTimeInfoEntity breaktime3 = new BreakTimeInfoEntity("休憩1", format.parse("2000/01/01 15:00:00"), format.parse("2000/01/01 15:10:00"));

        start = format.parse("2018/03/01 09:55:00");
        end = format.parse("2018/03/01 10:05:00");
        breakInWork = BreakTimeUtils.getBreakInWork2(Arrays.asList(breaktime1, breaktime2, breaktime3), start, end);
        breakTime = BreakTimeUtils.getBreakTime(breakInWork, start, end) / 1000;
        assertThat(breakTime, is(300L));

        start = format.parse("2018/03/01 10:05:00");
        end = format.parse("2018/03/01 13:00:00");
        breakInWork = BreakTimeUtils.getBreakInWork2(Arrays.asList(breaktime1, breaktime2, breaktime3), start, end);
        breakTime = BreakTimeUtils.getBreakTime(breakInWork, start, end) / 1000;
        assertThat(breakTime, is(3900L));

        start = format.parse("2018/03/01 15:00:00");
        end = format.parse("2018/03/01 15:15:00");
        breakInWork = BreakTimeUtils.getBreakInWork2(Arrays.asList(breaktime1, breaktime2, breaktime3), start, end);
        breakTime = BreakTimeUtils.getBreakTime(breakInWork, start, end) / 1000;
        assertThat(breakTime, is(600L));
    }
}
