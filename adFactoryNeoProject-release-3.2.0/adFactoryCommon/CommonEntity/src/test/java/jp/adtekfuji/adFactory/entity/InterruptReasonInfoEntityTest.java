/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.master.InterruptReasonInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author e-mori
 */
public class InterruptReasonInfoEntityTest {

    public InterruptReasonInfoEntityTest() {
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

        InterruptReasonInfoEntity entity = new InterruptReasonInfoEntity(1l, "name1");
        entity.setBackColor("#0000FF");
        entity.setFontColor("#00FF00");
        entity.setLightPattern(LightPatternEnum.LIGHTING);

        Document xml = EntityToXml.getXml(entity);
        String[] expectedNode1 = {"interruptReason"};
        String[] expectedNode2 = {"interruptId", "interruptReason", "fontColor", "backColor", "lightPattern"};
        String[] expectedValue2 = {"1", "name1", "#00FF00", "#0000FF", "LIGHTING"};

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

        InterruptReasonInfoEntity entity1 = new InterruptReasonInfoEntity(1l, "name1");
        entity1.setFontColor("#FFFFFF");
        entity1.setBackColor("#000000");
        entity1.setLightPattern(LightPatternEnum.LIGHTING);
        InterruptReasonInfoEntity entity2 = new InterruptReasonInfoEntity(2l, "name2");
        entity2.setFontColor("#000000");
        entity2.setBackColor("#FFFFFF");
        entity2.setLightPattern(LightPatternEnum.BLINK);

        assertThat(entity1.getInterruptId(), is(1l));
        assertThat(entity1.getInterruptReason(), is("name1"));
        assertThat(entity1.getFontColor(), is("#FFFFFF"));
        assertThat(entity1.getBackColor(), is("#000000"));
        assertThat(entity1.getLightPattern(), is(LightPatternEnum.LIGHTING));
        assertThat(entity2.getInterruptId(), is(2l));
        assertThat(entity2.getInterruptReason(), is("name2"));
        assertThat(entity2.getFontColor(), is("#000000"));
        assertThat(entity2.getBackColor(), is("#FFFFFF"));
        assertThat(entity2.getLightPattern(), is(LightPatternEnum.BLINK));

        entity2.interruptIdProperty().bind(entity1.interruptIdProperty());
        entity2.interruptReasonProperty().bind(entity1.interruptReasonProperty());
        entity2.fontColorProperty().bind(entity1.fontColorProperty());
        entity2.backColorProperty().bind(entity1.backColorProperty());
        entity2.lightPatternProperty().bind(entity1.lightPatternProperty());
        entity1.setInterruptId(3l);
        entity1.setInterruptReason("name3");
        entity1.setFontColor("#FF00FF");
        entity1.setBackColor("#00FF00");
        entity1.setLightPattern(LightPatternEnum.LIGHTING);

        assertThat(entity1.getInterruptId(), is(3l));
        assertThat(entity1.getInterruptReason(), is("name3"));
        assertThat(entity1.getFontColor(), is("#FF00FF"));
        assertThat(entity1.getBackColor(), is("#00FF00"));
        assertThat(entity1.getLightPattern(), is(LightPatternEnum.LIGHTING));
        assertThat(entity2.getInterruptId(), is(3l));
        assertThat(entity2.getInterruptReason(), is("name3"));
        assertThat(entity2.getFontColor(), is("#FF00FF"));
        assertThat(entity2.getBackColor(), is("#00FF00"));
        assertThat(entity2.getLightPattern(), is(LightPatternEnum.LIGHTING));
    }
}
