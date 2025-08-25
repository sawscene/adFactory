/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.statistics;

import jp.adtekfuji.forfujiapp.utils.Staristics;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;

/**
 * 統計計算処理テストクラス
 *
 * @author e-mori
 * @version Fver
 * @since 2016.08.03Wed
 */
public class StaristicTest {

    private final double[] values = new double[10];

    public StaristicTest() {

    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {

        // テストデータ作成
        this.values[0] = 80;
        this.values[1] = 50;
        this.values[2] = 40;
        this.values[3] = 20;
        this.values[4] = 30;
        this.values[5] = 70;
        this.values[6] = 55;
        this.values[7] = 50;
        this.values[8] = 45;
        this.values[9] = 55;
    }

    @After
    public void tearDown() {
    }

    /**
     * 平均の算出
     *
     * @throws Exception
     */
    @Test
    public void testAverage() throws Exception {
        assertThat(Staristics.average(this.values), is(49.5));
    }

    /**
     * 最大値の算出
     *
     * @throws Exception
     */
    @Test
    public void testMaximum() throws Exception {
        assertThat(Staristics.maximum(this.values), is(80.0));
    }

    /**
     * 最小値の算出
     *
     * @throws Exception
     */
    @Test
    public void testMinimum() throws Exception {
        assertThat(Staristics.minimum(this.values), is(20.0));
    }

    /**
     * 分散の算出
     *
     * @throws Exception
     */
    @Test
    public void testVariance() throws Exception {
        assertThat(Staristics.variance(this.values), is(277.25));
    }

    /**
     * 標準偏差の算出
     *
     * @throws Exception
     */
    @Test
    public void testStandardDeviation() throws Exception {
        assertThat(Math.round(Staristics.standardDeviation(this.values)), is(17L));

        double variance = Staristics.variance(values);
        assertThat(Math.round(Staristics.standardDeviation(variance)), is(17L));
    }

    /**
     * 偏差値の算出
     *
     * @throws Exception
     */
    @Test
    public void testDeviationValue() throws Exception {
        assertThat(Math.round(Staristics.deviationValue(values[0], this.values)), is(68L));
        assertThat(Math.round(Staristics.deviationValue(values[1], this.values)), is(50L));
        assertThat(Math.round(Staristics.deviationValue(values[2], this.values)), is(44L));
        assertThat(Math.round(Staristics.deviationValue(values[3], this.values)), is(32L));
        assertThat(Math.round(Staristics.deviationValue(values[4], this.values)), is(38L));
        assertThat(Math.round(Staristics.deviationValue(values[5], this.values)), is(62L));
        assertThat(Math.round(Staristics.deviationValue(values[6], this.values)), is(53L));
        assertThat(Math.round(Staristics.deviationValue(values[7], this.values)), is(50L));
        assertThat(Math.round(Staristics.deviationValue(values[8], this.values)), is(47L));
        assertThat(Math.round(Staristics.deviationValue(values[9], this.values)), is(53L));
    }

    /**
     * 確率密度の算出
     *
     * @throws Exception
     */
    @Test
    public void testProbabilityDestiny() throws Exception {
        assertThat(Math.round(Staristics.probabilityDestiny(values[0], this.values) * 100), is(0L));
        assertThat(Math.round(Staristics.probabilityDestiny(values[1], this.values) * 100), is(2L));
        assertThat(Math.round(Staristics.probabilityDestiny(values[2], this.values) * 100), is(2L));
        assertThat(Math.round(Staristics.probabilityDestiny(values[3], this.values) * 100), is(0L));
        assertThat(Math.round(Staristics.probabilityDestiny(values[4], this.values) * 100), is(1L));
        assertThat(Math.round(Staristics.probabilityDestiny(values[5], this.values) * 100), is(1L));
        assertThat(Math.round(Staristics.probabilityDestiny(values[6], this.values) * 100), is(2L));
        assertThat(Math.round(Staristics.probabilityDestiny(values[7], this.values) * 100), is(2L));
        assertThat(Math.round(Staristics.probabilityDestiny(values[8], this.values) * 100), is(2L));
        assertThat(Math.round(Staristics.probabilityDestiny(values[9], this.values) * 100), is(2L));
    }

}
