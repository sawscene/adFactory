/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import jakarta.xml.bind.JAXB;
import jp.adtekfuji.andon.property.AgendaMonitorSetting;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Monitorライン設定情報クラステスト
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.8.3.Wen
 */
public class AndonMonitorLineProductSettingTest {

    public AndonMonitorLineProductSettingTest() {
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
     * testXml
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testhasAllAttributes() throws Exception {
        System.out.println("testhasAllAttributes");

        AndonMonitorLineProductSetting create = AndonMonitorLineProductSetting.create();
        create.setAgendaMonitorSetting(AgendaMonitorSetting.create());
        assertThat(create.hasAllAttributes(), is(true));

        AndonMonitorLineProductSetting load = JAXB.unmarshal(new File("src\\test\\resources\\TestLineMonitorSetting.xml").getAbsolutePath(), AndonMonitorLineProductSetting.class);
        load.setAgendaMonitorSetting(AgendaMonitorSetting.create());
        assertThat(load.hasAllAttributes(), is(true));
    }

    @Override
    public String toString() {
        return AndonMonitorLineProductSettingTest.class.getName();
    }
}
