/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryfujiactualdataoutput;

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
public class OutputActualFacadeTest {

    public OutputActualFacadeTest() {
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
    public void testMath() throws Exception {
        System.out.println("testMath");

        int time = 1;    //1msec
        int out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 1000;    //1sec
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 10000;    //10sec
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 59999;
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 60000;
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 60001;
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(2));

    }

    @Test
    public void testRound() throws Exception {
        System.out.println("testRound");

        int time = 29999;    //29.999sec
        int out = (int) Math.round((double) time / 60.0 / 1000.0);
        assertThat(out, is(0));

        time = 30000;    //30.000sec
        out = (int) Math.round((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 30001;    //30.001sec
        out = (int) Math.round((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

    }

}
