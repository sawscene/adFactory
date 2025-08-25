/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import java.util.GregorianCalendar;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author e-mori
 */
public class BreaktimeInfoEntityTest {

    public BreaktimeInfoEntityTest() {
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
    public void testXml() throws Exception {
        System.out.println("testXml");

        BreakTimeInfoEntity entity = new BreakTimeInfoEntity(1l, "breaktimeName");
        entity.setStarttime(new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime());
        entity.setStarttime(new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime());
        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"breaktime"};
        String[] expectedNode2 = {"breaktimeId", "breaktimeName", "starttime", "endtime"};
        String[] expectedValue2 = {"1", "breaktimeName", "2015-11-30T14:47:06+09:00", "2015-11-30T14:47:06+09:00"};

        Integer level1 = 0;
        Integer level2 = 0;
        for (Node node1 = xml.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
            if (node1 instanceof Element) {
                System.out.print(node1.getNodeName() + ":" + node1.getTextContent() + CR);
                assertThat(node1.getNodeName(), is(expectedNode1[level1]));
                level1++;
            }
            level2 = 0;
            for (Node node2 = node1.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if (node2 instanceof Element) {
                    System.out.print(node2.getNodeName() + ":" + node2.getTextContent() + CR);
                    assertThat(node2.getNodeName(), is(expectedNode2[level2]));
                    assertThat(node2.getTextContent(), is(expectedValue2[level2]));
                    level2++;
                }
            }
        }
    }

    @Test
    public void testBindProperty() throws ConfigurationException, IOException {
        System.out.println("testBindProperty");

        BreakTimeInfoEntity breaktime1 = new BreakTimeInfoEntity(1l, "breaktimeName1");
        breaktime1.setStarttime(new GregorianCalendar(2015, 10, 25, 14, 47, 6).getTime());
        breaktime1.setEndtime(new GregorianCalendar(2015, 10, 26, 14, 47, 6).getTime());
        BreakTimeInfoEntity breaktime2 = new BreakTimeInfoEntity(2l, "breaktimeName2");
        breaktime2.setStarttime(new GregorianCalendar(2015, 10, 27, 14, 47, 6).getTime());
        breaktime2.setEndtime(new GregorianCalendar(2015, 10, 28, 14, 47, 6).getTime());
        assertThat(breaktime1.getBreaktimeId(), is(1l));
        assertThat(breaktime1.getBreaktimeName(), is("breaktimeName1"));
        assertThat(breaktime1.getStarttime(), is(new GregorianCalendar(2015, 10, 25, 14, 47, 6).getTime()));
        assertThat(breaktime1.getEndtime(), is(new GregorianCalendar(2015, 10, 26, 14, 47, 6).getTime()));
        assertThat(breaktime2.getBreaktimeId(), is(2l));
        assertThat(breaktime2.getBreaktimeName(), is("breaktimeName2"));
        assertThat(breaktime2.getStarttime(), is(new GregorianCalendar(2015, 10, 27, 14, 47, 6).getTime()));
        assertThat(breaktime2.getEndtime(), is(new GregorianCalendar(2015, 10, 28, 14, 47, 6).getTime()));

        breaktime2.breaktimeIdProperty().bind(breaktime1.breaktimeIdProperty());
        breaktime2.breaktimeNameProperty().bind(breaktime1.breaktimeNameProperty());
        breaktime2.starttimeProperty().bind(breaktime1.starttimeProperty());
        breaktime2.endtimeProperty().bind(breaktime1.endtimeProperty());
        breaktime1.setBreaktimeId(3l);
        breaktime1.setBreaktimeName("breaktimeName3");
        breaktime1.setStarttime(new GregorianCalendar(2015, 10, 29, 14, 47, 6).getTime());
        breaktime1.setEndtime(new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime());
        assertThat(breaktime1.getBreaktimeId(), is(3l));
        assertThat(breaktime1.getBreaktimeName(), is("breaktimeName3"));
        assertThat(breaktime1.getStarttime(), is(new GregorianCalendar(2015, 10, 29, 14, 47, 6).getTime()));
        assertThat(breaktime1.getEndtime(), is(new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime()));
        assertThat(breaktime2.getBreaktimeId(), is(3l));
        assertThat(breaktime2.getBreaktimeName(), is("breaktimeName3"));
        assertThat(breaktime2.getStarttime(), is(new GregorianCalendar(2015, 10, 29, 14, 47, 6).getTime()));
        assertThat(breaktime2.getEndtime(), is(new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime()));
    }
}
