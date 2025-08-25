/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
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
 * @author ta.ito
 */
public class WorkInfoEntityTest {

    public WorkInfoEntityTest() {
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

        WorkInfoEntity entity = new WorkInfoEntity(1L, 2L, "workName", 100, "content", ContentTypeEnum.STRING, 3L, new GregorianCalendar(2015, 10, 15, 14, 47, 6).getTime(), null, null);
        entity.setUpdateDatetime(new GregorianCalendar(2015, 10, 15, 14, 47, 6).getTime());
        List<WorkPropertyInfoEntity> properties = new ArrayList<>();
        properties.add(new WorkPropertyInfoEntity(1L, 1L, "name", CustomPropertyTypeEnum.TYPE_STRING, "", null));
        entity.setPropertyInfoCollection(properties);
        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"work"};
        String[] expectedNode2 = {"workId", "parentId", "workName", "taktTime", "content", "contentType", "updatePersonId", "updateDatetime", "workPropertys"};
        String[] expectedValue2 = {"1", "2", "workName", "100", "content", ContentTypeEnum.STRING.toString(), "3", "2015-11-15T14:47:06+09:00", "11name" + CustomPropertyTypeEnum.TYPE_STRING.toString()};

        Integer level1 = 0;
        Integer level2 = 0;
        Integer level3 = 0;
        Integer level4 = 0;
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

        WorkInfoEntity work1 = new WorkInfoEntity(1L, 0L, "work1", 10, "workcontent1", ContentTypeEnum.STRING, null, null, "#00FF00", "#FF0000");
        WorkInfoEntity work2 = new WorkInfoEntity(2L, 0L, "work2", 20, "workcontent2", ContentTypeEnum.STRING, null, null, "#000000", "#FFFFFF");
        assertThat(work1.getWorkId(), is(1l));
        assertThat(work1.getWorkName(), is("work1"));
        assertThat(work1.getTaktTime(), is(10));
        assertThat(work1.getContent(), is("workcontent1"));
        assertThat(work1.getFontColor(), is("#00FF00"));
        assertThat(work1.getBackColor(), is("#FF0000"));
        assertThat(work2.getWorkId(), is(2l));
        assertThat(work2.getWorkName(), is("work2"));
        assertThat(work2.getTaktTime(), is(20));
        assertThat(work2.getContent(), is("workcontent2"));
        assertThat(work2.getFontColor(), is("#000000"));
        assertThat(work2.getBackColor(), is("#FFFFFF"));
        work2.workIdProperty().bind(work1.workIdProperty());
        work2.workNameProperty().bind(work1.workNameProperty());
        work2.taktTimeProperty().bind(work1.taktTimeProperty());
        work2.contentProperty().bind(work1.contentProperty());
        work2.fontColorProperty().bind(work1.fontColorProperty());
        work2.backColorProperty().bind(work1.backColorProperty());
        work1.setWorkId(3L);
        work1.setWorkName("work3");
        work1.setTaktTime(30);
        work1.setContent("workcontent3");
        work1.setFontColor("#0000FF");
        work1.setBackColor("#00FF00");
        assertThat(work1.getWorkId(), is(3l));
        assertThat(work1.getWorkName(), is("work3"));
        assertThat(work1.getTaktTime(), is(30));
        assertThat(work1.getContent(), is("workcontent3"));
        assertThat(work1.getFontColor(), is("#0000FF"));
        assertThat(work1.getBackColor(), is("#00FF00"));
        assertThat(work2.getWorkId(), is(3l));
        assertThat(work2.getWorkName(), is("work3"));
        assertThat(work2.getTaktTime(), is(30));
        assertThat(work2.getContent(), is("workcontent3"));
        assertThat(work2.getFontColor(), is("#0000FF"));
        assertThat(work2.getBackColor(), is("#00FF00"));
    }
}
