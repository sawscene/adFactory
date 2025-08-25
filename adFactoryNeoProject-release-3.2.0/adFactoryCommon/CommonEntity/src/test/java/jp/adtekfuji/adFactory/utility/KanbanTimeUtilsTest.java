/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.utility;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.naming.ConfigurationException;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author e-mori
 */
public class KanbanTimeUtilsTest {

    private final static String dateFormat = "yyyy/MM/dd HH:mm:SS";
    private final static DateFormat df = new SimpleDateFormat(dateFormat);

    public KanbanTimeUtilsTest() {
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
     * 直列工程テスト モデル一覧 TS:スキップ対象の開始時間 TC：スキップ対象の終了時間 NS:スキップ対象以降の開始時間
     * NC：スキップ対象以降の終了時間
     *
     * @throws ConfigurationException
     * @throws IOException
     */
    @Ignore
    @Test
    public void testSeriesSkipTimeOffset1() throws ConfigurationException, IOException {
        System.out.println("testSkipTimeOffset");

        ArrayList<WorkKanbanInfoEntity> entitys = new ArrayList<>();
        entitys.addAll(createSkipWorkKanbanData1());
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(entitys);
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));

        entitys.clear();
        entitys.addAll(createSkipWorkKanbanData2());
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(entitys);
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime()));

        entitys.clear();
        entitys.addAll(createSkipWorkKanbanData3());
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(entitys);
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime()));

        entitys.clear();
        entitys.addAll(createSkipWorkKanbanData4());
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(entitys);
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime()));

        entitys.clear();
        entitys.addAll(createSkipWorkKanbanData5());
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(entitys);
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 15, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));

        entitys.clear();
        entitys.addAll(createSkipWorkKanbanData6());
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(entitys);
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 45, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 00, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
    }

    @Test
    public void testSeriesSkipTimeOffset2() throws ConfigurationException, IOException {
        System.out.println("testSkipTimeOffset");

        ArrayList<WorkKanbanInfoEntity> entitys = new ArrayList<>();
        entitys.addAll(createSkipWorkKanbanData1());
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(entitys);
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime()));

        entitys.clear();
        entitys.addAll(createSkipWorkKanbanData2());
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(entitys);
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime()));

        entitys.clear();
        entitys.addAll(createSkipWorkKanbanData3());
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(entitys);
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime()));

        entitys.clear();
        entitys.addAll(createSkipWorkKanbanData4());
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(entitys);
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 45, 0).getTime()));

        entitys.clear();
        entitys.addAll(createSkipWorkKanbanData5());
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(entitys);
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 45, 0).getTime()));

        entitys.clear();
        entitys.addAll(createSkipWorkKanbanData6());
        KanbanTimeUtils.batchValidSkipWorkKanbanOffsetTimes(entitys);
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 45, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 45, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 10, 0, 0).getTime()));
    }

    /**
     * TS = NS TS < NC TC > NS TC = NC
     *
     * @return
     */
    private List<WorkKanbanInfoEntity> createSkipWorkKanbanData1() {

        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        WorkKanbanInfoEntity wKanban = new WorkKanbanInfoEntity(1L, 1L, 1L, 4L, 1L, "work1");
        wKanban.setSeparateWorkFlag(false);
        wKanban.setSkipFlag(false);
        wKanban.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime());
        wKanban.setCompDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanbans.add(wKanban);
        WorkKanbanInfoEntity wKanban2 = new WorkKanbanInfoEntity(2L, 1L, 1L, 4L, 2L, "work2");
        wKanban2.setSeparateWorkFlag(false);
        wKanban2.setSkipFlag(true);
        wKanban2.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanban2.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime());
        wKanbans.add(wKanban2);
        WorkKanbanInfoEntity wKanban3 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban3.setSeparateWorkFlag(false);
        wKanban3.setSkipFlag(false);
        wKanban3.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanban3.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime());
        wKanbans.add(wKanban3);
        WorkKanbanInfoEntity wKanban4 = new WorkKanbanInfoEntity(4L, 1L, 1L, 4L, 4L, "work4");
        wKanban4.setSeparateWorkFlag(false);
        wKanban4.setSkipFlag(false);
        wKanban4.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime());
        wKanban4.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime());
        wKanbans.add(wKanban4);

        return wKanbans;
    }

    /**
     * TS < NS TS < NC TC = NS TC < NC
     *
     * @return
     */
    private List<WorkKanbanInfoEntity> createSkipWorkKanbanData2() {

        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        WorkKanbanInfoEntity wKanban = new WorkKanbanInfoEntity(1L, 1L, 1L, 4L, 1L, "work1");
        wKanban.setSeparateWorkFlag(false);
        wKanban.setSkipFlag(false);
        wKanban.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime());
        wKanban.setCompDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanbans.add(wKanban);
        WorkKanbanInfoEntity wKanban2 = new WorkKanbanInfoEntity(2L, 1L, 1L, 4L, 2L, "work2");
        wKanban2.setSeparateWorkFlag(false);
        wKanban2.setSkipFlag(true);
        wKanban2.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanban2.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime());
        wKanbans.add(wKanban2);
        WorkKanbanInfoEntity wKanban3 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban3.setSeparateWorkFlag(false);
        wKanban3.setSkipFlag(false);
        wKanban3.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime());
        wKanban3.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime());
        wKanbans.add(wKanban3);
        WorkKanbanInfoEntity wKanban4 = new WorkKanbanInfoEntity(4L, 1L, 1L, 4L, 4L, "work4");
        wKanban4.setSeparateWorkFlag(false);
        wKanban4.setSkipFlag(false);
        wKanban4.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime());
        wKanban4.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime());
        wKanbans.add(wKanban4);

        return wKanbans;
    }

    /**
     * TS < NS TS < NC TC < NS TC < NC
     *
     * @return
     */
    private List<WorkKanbanInfoEntity> createSkipWorkKanbanData3() {

        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        WorkKanbanInfoEntity wKanban = new WorkKanbanInfoEntity(1L, 1L, 1L, 4L, 1L, "work1");
        wKanban.setSeparateWorkFlag(false);
        wKanban.setSkipFlag(false);
        wKanban.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime());
        wKanban.setCompDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanbans.add(wKanban);
        WorkKanbanInfoEntity wKanban2 = new WorkKanbanInfoEntity(2L, 1L, 1L, 4L, 2L, "work2");
        wKanban2.setSeparateWorkFlag(false);
        wKanban2.setSkipFlag(true);
        wKanban2.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanban2.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime());
        wKanbans.add(wKanban2);
        WorkKanbanInfoEntity wKanban3 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban3.setSeparateWorkFlag(false);
        wKanban3.setSkipFlag(false);
        wKanban3.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime());
        wKanban3.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime());
        wKanbans.add(wKanban3);
        WorkKanbanInfoEntity wKanban4 = new WorkKanbanInfoEntity(4L, 1L, 1L, 4L, 4L, "work4");
        wKanban4.setSeparateWorkFlag(false);
        wKanban4.setSkipFlag(false);
        wKanban4.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime());
        wKanban4.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime());
        wKanbans.add(wKanban4);

        return wKanbans;
    }

    /**
     * TS < NS TS < NC TC > NS TC < NC
     *
     * @return
     */
    private List<WorkKanbanInfoEntity> createSkipWorkKanbanData4() {

        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        WorkKanbanInfoEntity wKanban = new WorkKanbanInfoEntity(1L, 1L, 1L, 4L, 1L, "work1");
        wKanban.setSeparateWorkFlag(false);
        wKanban.setSkipFlag(false);
        wKanban.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime());
        wKanban.setCompDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanbans.add(wKanban);
        WorkKanbanInfoEntity wKanban2 = new WorkKanbanInfoEntity(2L, 1L, 1L, 4L, 2L, "work2");
        wKanban2.setSeparateWorkFlag(false);
        wKanban2.setSkipFlag(true);
        wKanban2.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanban2.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 15, 0).getTime());
        wKanbans.add(wKanban2);
        WorkKanbanInfoEntity wKanban3 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban3.setSeparateWorkFlag(false);
        wKanban3.setSkipFlag(false);
        wKanban3.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime());
        wKanban3.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime());
        wKanbans.add(wKanban3);
        WorkKanbanInfoEntity wKanban4 = new WorkKanbanInfoEntity(4L, 1L, 1L, 4L, 4L, "work4");
        wKanban4.setSeparateWorkFlag(false);
        wKanban4.setSkipFlag(false);
        wKanban4.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime());
        wKanban4.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 45, 0).getTime());
        wKanbans.add(wKanban4);

        return wKanbans;
    }

    /**
     * TS < NS TS < NC TC > NS TC = NC
     *
     * @return
     */
    private List<WorkKanbanInfoEntity> createSkipWorkKanbanData5() {

        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        WorkKanbanInfoEntity wKanban = new WorkKanbanInfoEntity(1L, 1L, 1L, 4L, 1L, "work1");
        wKanban.setSeparateWorkFlag(false);
        wKanban.setSkipFlag(false);
        wKanban.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime());
        wKanban.setCompDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanbans.add(wKanban);
        WorkKanbanInfoEntity wKanban2 = new WorkKanbanInfoEntity(2L, 1L, 1L, 4L, 2L, "work2");
        wKanban2.setSeparateWorkFlag(false);
        wKanban2.setSkipFlag(true);
        wKanban2.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanban2.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime());
        wKanbans.add(wKanban2);
        WorkKanbanInfoEntity wKanban3 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban3.setSeparateWorkFlag(false);
        wKanban3.setSkipFlag(false);
        wKanban3.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime());
        wKanban3.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime());
        wKanbans.add(wKanban3);
        WorkKanbanInfoEntity wKanban4 = new WorkKanbanInfoEntity(4L, 1L, 1L, 4L, 4L, "work4");
        wKanban4.setSeparateWorkFlag(false);
        wKanban4.setSkipFlag(false);
        wKanban4.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime());
        wKanban4.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 45, 0).getTime());
        wKanbans.add(wKanban4);

        return wKanbans;
    }

    /**
     * TS < NS TS < NC TC > NS TC > NC
     *
     * @return
     */
    private List<WorkKanbanInfoEntity> createSkipWorkKanbanData6() {

        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        WorkKanbanInfoEntity wKanban = new WorkKanbanInfoEntity(1L, 1L, 1L, 4L, 1L, "work1");
        wKanban.setSeparateWorkFlag(false);
        wKanban.setSkipFlag(false);
        wKanban.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime());
        wKanban.setCompDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanbans.add(wKanban);
        WorkKanbanInfoEntity wKanban2 = new WorkKanbanInfoEntity(2L, 1L, 1L, 4L, 2L, "work2");
        wKanban2.setSeparateWorkFlag(false);
        wKanban2.setSkipFlag(true);
        wKanban2.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 45, 0).getTime());
        wKanban2.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 45, 0).getTime());
        wKanbans.add(wKanban2);
        WorkKanbanInfoEntity wKanban3 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban3.setSeparateWorkFlag(false);
        wKanban3.setSkipFlag(false);
        wKanban3.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime());
        wKanban3.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime());
        wKanbans.add(wKanban3);
        WorkKanbanInfoEntity wKanban4 = new WorkKanbanInfoEntity(4L, 1L, 1L, 4L, 4L, "work4");
        wKanban4.setSeparateWorkFlag(false);
        wKanban4.setSkipFlag(false);
        wKanban4.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 45, 0).getTime());
        wKanban4.setCompDatetime(new GregorianCalendar(2015, 1, 1, 10, 0, 0).getTime());
        wKanbans.add(wKanban4);

        return wKanbans;
    }

    /**
     *
     * @throws javax.naming.ConfigurationException
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    @Test
    public void testGetEmploymentOutTime() throws ConfigurationException, IOException, ParseException {
        System.out.print("GetEmploymentOutTime start");

        //3日間
        List<BreakTimeInfoEntity> entitys = KanbanTimeUtils.getEmploymentOutTime(
                new GregorianCalendar(2015, 1, 1, 9, 45, 0).getTime(),
                new GregorianCalendar(2015, 1, 3, 12, 45, 0).getTime(),
                new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime(),
                new GregorianCalendar(2015, 1, 1, 17, 00, 0).getTime(),
                dateFormat);

        assertThat(entitys.size(), is(3));
        assertThat(entitys.get(0).getStarttime(), is(new GregorianCalendar(2015, 1, 1, 17, 00, 0).getTime()));
        assertThat(entitys.get(0).getEndtime(), is(new GregorianCalendar(2015, 1, 2, 8, 30, 0).getTime()));
        assertThat(entitys.get(1).getStarttime(), is(new GregorianCalendar(2015, 1, 2, 17, 00, 0).getTime()));
        assertThat(entitys.get(1).getEndtime(), is(new GregorianCalendar(2015, 1, 3, 8, 30, 0).getTime()));
        assertThat(entitys.get(2).getStarttime(), is(new GregorianCalendar(2015, 1, 3, 17, 00, 0).getTime()));
        assertThat(entitys.get(2).getEndtime(), is(new GregorianCalendar(2015, 1, 4, 8, 30, 0).getTime()));

        //1か月
        List<BreakTimeInfoEntity> entitys1 = KanbanTimeUtils.getEmploymentOutTime(
                new GregorianCalendar(2015, 1, 1, 9, 45, 0).getTime(),
                new GregorianCalendar(2015, 1, 29, 12, 45, 0).getTime(),
                new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime(),
                new GregorianCalendar(2015, 1, 1, 17, 00, 0).getTime(),
                dateFormat);

        assertThat(entitys1.size(), is(29));
        assertThat(entitys1.get(0).getStarttime(), is(new GregorianCalendar(2015, 1, 1, 17, 00, 0).getTime()));
        assertThat(entitys1.get(0).getEndtime(), is(new GregorianCalendar(2015, 1, 2, 8, 30, 0).getTime()));
        assertThat(entitys1.get(28).getStarttime(), is(new GregorianCalendar(2015, 2, 1, 17, 00, 0).getTime()));
        assertThat(entitys1.get(28).getEndtime(), is(new GregorianCalendar(2015, 2, 2, 8, 30, 0).getTime()));

        //1か月
        List<BreakTimeInfoEntity> entitys3 = KanbanTimeUtils.getEmploymentOutTime(
                new GregorianCalendar(2015, 2, 1, 9, 45, 0).getTime(),
                new GregorianCalendar(2015, 6, 29, 12, 45, 0).getTime(),
                new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime(),
                new GregorianCalendar(2015, 1, 1, 17, 00, 0).getTime(),
                dateFormat);

        assertThat(entitys3.size(), is(151));
        assertThat(entitys3.get(0).getStarttime(), is(new GregorianCalendar(2015, 2, 1, 17, 00, 0).getTime()));
        assertThat(entitys3.get(0).getEndtime(), is(new GregorianCalendar(2015, 2, 2, 8, 30, 0).getTime()));
        assertThat(entitys3.get(150).getStarttime(), is(new GregorianCalendar(2015, 6, 29, 17, 00, 0).getTime()));
        assertThat(entitys3.get(150).getEndtime(), is(new GregorianCalendar(2015, 6, 30, 8, 30, 0).getTime()));
    }

    /**
     *
     * @throws java.text.ParseException
     */
    @Test
    public void testbatchBreakTimeOffsetTimes() throws ParseException {
        System.out.print("testbatchWorkKanbanReferenceTime start\n");
        List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList<>();

        breaktimeCollection.add(new BreakTimeInfoEntity("1", df.parse("2000/1/1 12:00:00"), df.parse("2000/1/1 12:30:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("2", df.parse("2000/1/1 12:15:00"), df.parse("2000/1/1 12:45:00")));
        breaktimeCollection.addAll(KanbanTimeUtils.getEmploymentOutTime(df.parse("2015/2/1 9:45:00"), df.parse("2015/2/2 12:45:00"), df.parse("2015/1/1 8:30:00"), df.parse("2015/1/1 17:00:00"), "yyyy/MM/dd HH:mm:ss"));
        breaktimeCollection.add(new BreakTimeInfoEntity("3", df.parse("2000/1/1 15:00:00"), df.parse("2000/1/1 15:30:00")));
        breaktimeCollection.add(new BreakTimeInfoEntity("4", df.parse("2000/1/1 18:45:00"), df.parse("2000/1/1 19:00:00")));

        ArrayList<WorkKanbanInfoEntity> entitys = new ArrayList<>();
        entitys.addAll(createReferenceWorkKanbanData());
        KanbanTimeUtils.batchValidBreakTimeWorkKanbanOffsetTimes(entitys, breaktimeCollection);
        for (WorkKanbanInfoEntity entity : entitys) {
            System.out.print(entity + "\n");
        }
        System.out.print("end\n");
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 11, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 13, 30, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 13, 30, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 15, 45, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 15, 45, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 2, 9, 00, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 2, 9, 00, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 2, 13, 00, 0).getTime()));
    }

    /**
     * TS < NS TS < NC TC > NS TC > NC
     *
     * @return
     */
    private List<WorkKanbanInfoEntity> createReferenceWorkKanbanData() {

        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        WorkKanbanInfoEntity wKanban = new WorkKanbanInfoEntity(1L, 1L, 1L, 4L, 1L, "work1");
        wKanban.setSeparateWorkFlag(false);
        wKanban.setSkipFlag(false);
        wKanban.setStartDatetime(new GregorianCalendar(2015, 1, 1, 11, 30, 0).getTime());
        wKanban.setCompDatetime(new GregorianCalendar(2015, 1, 1, 12, 45, 0).getTime());
        wKanbans.add(wKanban);
        WorkKanbanInfoEntity wKanban2 = new WorkKanbanInfoEntity(2L, 1L, 1L, 4L, 2L, "work2");
        wKanban2.setSeparateWorkFlag(false);
        wKanban2.setSkipFlag(false);
        wKanban2.setStartDatetime(new GregorianCalendar(2015, 1, 1, 12, 45, 0).getTime());
        wKanban2.setCompDatetime(new GregorianCalendar(2015, 1, 1, 14, 30, 0).getTime());
        wKanbans.add(wKanban2);
        WorkKanbanInfoEntity wKanban3 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban3.setSeparateWorkFlag(false);
        wKanban3.setSkipFlag(false);
        wKanban3.setStartDatetime(new GregorianCalendar(2015, 1, 1, 14, 30, 0).getTime());
        wKanban3.setCompDatetime(new GregorianCalendar(2015, 1, 1, 16, 15, 0).getTime());
        wKanbans.add(wKanban3);
        WorkKanbanInfoEntity wKanban4 = new WorkKanbanInfoEntity(4L, 1L, 1L, 4L, 4L, "work4");
        wKanban4.setSeparateWorkFlag(false);
        wKanban4.setSkipFlag(false);
        wKanban4.setStartDatetime(new GregorianCalendar(2015, 1, 1, 16, 15, 0).getTime());
        wKanban4.setCompDatetime(new GregorianCalendar(2015, 1, 1, 19, 30, 0).getTime());
        wKanbans.add(wKanban4);

        return wKanbans;
    }

    /**
     *
     * @throws java.text.ParseException
     */
    @Test
    public void testbatchBreakTimeInvalidTime() throws ParseException {
        System.out.print("testbatchBreakTimeInvalidTime start\n");
        List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList<>();

        breaktimeCollection.add(new BreakTimeInfoEntity("1", df.parse("2000/1/1 9:00:00"), df.parse("2000/1/1 9:30:00")));

        ArrayList<WorkKanbanInfoEntity> entitys = new ArrayList<>();
        entitys.addAll(createBreakTimeSubtractionTimeData1());
        KanbanTimeUtils.batchInvalidBreakTimeWorkKanbanOffsetTimes(1, entitys, breaktimeCollection);
        entitys.stream().forEach((entity) -> {
            System.out.print(entity + "\n");
        });
        System.out.print("end\n");
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 10, 00, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 10, 00, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 10, 30, 0).getTime()));

        entitys.clear();
        entitys.addAll(createBreakTimeSubtractionTimeData2());
        KanbanTimeUtils.batchInvalidBreakTimeWorkKanbanOffsetTimes(2, entitys, breaktimeCollection);
        entitys.stream().forEach((entity) -> {
            System.out.print(entity + "\n");
        });
        System.out.print("end\n");
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 0, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 10, 00, 0).getTime()));
    }

    private List<WorkKanbanInfoEntity> createBreakTimeSubtractionTimeData1() {
        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        WorkKanbanInfoEntity wKanban = new WorkKanbanInfoEntity(1L, 1L, 1L, 4L, 1L, "work1");
        wKanban.setSeparateWorkFlag(false);
        wKanban.setSkipFlag(false);
        wKanban.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime());
        wKanban.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime());
        wKanbans.add(wKanban);
        WorkKanbanInfoEntity wKanban2 = new WorkKanbanInfoEntity(2L, 1L, 1L, 4L, 2L, "work2");
        wKanban2.setSeparateWorkFlag(false);
        wKanban2.setSkipFlag(false);
        wKanban2.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime());
        wKanban2.setCompDatetime(new GregorianCalendar(2015, 1, 1, 10, 00, 00).getTime());
        wKanbans.add(wKanban2);
        WorkKanbanInfoEntity wKanban3 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban3.setSeparateWorkFlag(false);
        wKanban3.setSkipFlag(false);
        wKanban3.setStartDatetime(new GregorianCalendar(2015, 1, 1, 10, 00, 00).getTime());
        wKanban3.setCompDatetime(new GregorianCalendar(2015, 1, 1, 10, 30, 00).getTime());
        wKanbans.add(wKanban3);
        WorkKanbanInfoEntity wKanban4 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban4.setSeparateWorkFlag(false);
        wKanban4.setSkipFlag(false);
        wKanban4.setStartDatetime(new GregorianCalendar(2015, 1, 1, 10, 30, 00).getTime());
        wKanban4.setCompDatetime(new GregorianCalendar(2015, 1, 1, 11, 00, 00).getTime());
        wKanbans.add(wKanban4);

        return wKanbans;
    }

    private List<WorkKanbanInfoEntity> createBreakTimeSubtractionTimeData2() {
        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        WorkKanbanInfoEntity wKanban = new WorkKanbanInfoEntity(1L, 1L, 1L, 4L, 1L, "work1");
        wKanban.setSeparateWorkFlag(false);
        wKanban.setSkipFlag(false);
        wKanban.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime());
        wKanban.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime());
        wKanbans.add(wKanban);
        WorkKanbanInfoEntity wKanban2 = new WorkKanbanInfoEntity(2L, 1L, 1L, 4L, 2L, "work2");
        wKanban2.setSeparateWorkFlag(false);
        wKanban2.setSkipFlag(false);
        wKanban2.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime());
        wKanban2.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 00).getTime());
        wKanbans.add(wKanban2);
        WorkKanbanInfoEntity wKanban3 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban3.setSeparateWorkFlag(false);
        wKanban3.setSkipFlag(false);
        wKanban3.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime());
        wKanban3.setCompDatetime(new GregorianCalendar(2015, 1, 1, 10, 00, 00).getTime());
        wKanbans.add(wKanban3);
        WorkKanbanInfoEntity wKanban4 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban4.setSeparateWorkFlag(false);
        wKanban4.setSkipFlag(false);
        wKanban4.setStartDatetime(new GregorianCalendar(2015, 1, 1, 10, 00, 00).getTime());
        wKanban4.setCompDatetime(new GregorianCalendar(2015, 1, 1, 10, 30, 00).getTime());
        wKanbans.add(wKanban4);

        return wKanbans;
    }

    /**
     *
     * @throws java.text.ParseException
     */
    @Test
    public void testbatchInvalidSkipWorkKanbanOffsetTime() throws ParseException {
        System.out.print("testbatchInvalidSkipWorkKanbanOffsetTime start\n");
        ArrayList<WorkKanbanInfoEntity> entitys = new ArrayList<>();
        entitys.addAll(createbatchBreakTimeInvalidTimeData());
        KanbanTimeUtils.batchInvalidSkipWorkKanbanOffsetTime(1, entitys);
        entitys.stream().forEach((entity) -> {
            System.out.print(entity + "\n");
        });
        System.out.print("end\n");
        assertThat(entitys.get(0).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime()));
        assertThat(entitys.get(0).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));
        assertThat(entitys.get(1).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime()));
        assertThat(entitys.get(1).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(2).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime()));
        assertThat(entitys.get(2).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 10, 00, 0).getTime()));
        assertThat(entitys.get(3).getStartDatetime(), is(new GregorianCalendar(2015, 1, 1, 10, 00, 0).getTime()));
        assertThat(entitys.get(3).getCompDatetime(), is(new GregorianCalendar(2015, 1, 1, 10, 30, 0).getTime()));
    }

    private List<WorkKanbanInfoEntity> createbatchBreakTimeInvalidTimeData() {
        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        WorkKanbanInfoEntity wKanban = new WorkKanbanInfoEntity(1L, 1L, 1L, 4L, 1L, "work1");
        wKanban.setSeparateWorkFlag(false);
        wKanban.setSkipFlag(false);
        wKanban.setStartDatetime(new GregorianCalendar(2015, 1, 1, 8, 30, 0).getTime());
        wKanban.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime());
        wKanbans.add(wKanban);
        WorkKanbanInfoEntity wKanban2 = new WorkKanbanInfoEntity(2L, 1L, 1L, 4L, 2L, "work2");
        wKanban2.setSeparateWorkFlag(false);
        wKanban2.setSkipFlag(false);
        wKanban2.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime());
        wKanban2.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 00).getTime());
        wKanbans.add(wKanban2);
        WorkKanbanInfoEntity wKanban3 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban3.setSeparateWorkFlag(false);
        wKanban3.setSkipFlag(false);
        wKanban3.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 00, 0).getTime());
        wKanban3.setCompDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime());
        wKanbans.add(wKanban3);
        WorkKanbanInfoEntity wKanban4 = new WorkKanbanInfoEntity(3L, 1L, 1L, 4L, 3L, "work3");
        wKanban4.setSeparateWorkFlag(false);
        wKanban4.setSkipFlag(false);
        wKanban4.setStartDatetime(new GregorianCalendar(2015, 1, 1, 9, 30, 0).getTime());
        wKanban4.setCompDatetime(new GregorianCalendar(2015, 1, 1, 10, 00, 00).getTime());
        wKanbans.add(wKanban4);

        return wKanbans;
    }

}
