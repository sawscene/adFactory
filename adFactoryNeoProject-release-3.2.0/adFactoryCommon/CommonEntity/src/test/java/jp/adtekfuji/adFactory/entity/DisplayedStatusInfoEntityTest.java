/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
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
public class DisplayedStatusInfoEntityTest {

    public DisplayedStatusInfoEntityTest() {
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

        DisplayedStatusInfoEntity entity = new DisplayedStatusInfoEntity(1l, StatusPatternEnum.PLAN_NORMAL);
        entity.setBackColor("#0000FF");
        entity.setFontColor("#00FF00");
        entity.setLightPattern(LightPatternEnum.LIGHTING);
        entity.setNotationName("notation");
        entity.setMelodyPath("path");
        entity.setMelodyRepeat(true);

        Document xml = EntityToXml.getXml(entity);
        String[] expectedNode1 = {"displayedStatus"};
        String[] expectedNode2 = {"statusId", "statusName", "fontColor", "backColor", "lightPattern", "notationName", "melodyPath", "melodyRepeat"};
        String[] expectedValue2 = {"1", StatusPatternEnum.PLAN_NORMAL.toString(), "#00FF00", "#0000FF", "LIGHTING", "notation", "path", Boolean.TRUE.toString()};

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

        DisplayedStatusInfoEntity entity1 = new DisplayedStatusInfoEntity(1l, StatusPatternEnum.PLAN_NORMAL);
        entity1.setFontColor("#FFFFFF");
        entity1.setBackColor("#000000");
        entity1.setLightPattern(LightPatternEnum.LIGHTING);
        DisplayedStatusInfoEntity entity2 = new DisplayedStatusInfoEntity(2l, StatusPatternEnum.PLAN_DELAYSTART);
        entity2.setFontColor("#000000");
        entity2.setBackColor("#FFFFFF");
        entity2.setLightPattern(LightPatternEnum.BLINK);

        assertThat(entity1.getStatusId(), is(1l));
        assertThat(entity1.getStatusName(), is(StatusPatternEnum.PLAN_NORMAL));
        assertThat(entity1.getFontColor(), is("#FFFFFF"));
        assertThat(entity1.getBackColor(), is("#000000"));
        assertThat(entity1.getLightPattern(), is(LightPatternEnum.LIGHTING));
        assertThat(entity2.getStatusId(), is(2l));
        assertThat(entity2.getStatusName(), is(StatusPatternEnum.PLAN_DELAYSTART));
        assertThat(entity2.getFontColor(), is("#000000"));
        assertThat(entity2.getBackColor(), is("#FFFFFF"));
        assertThat(entity2.getLightPattern(), is(LightPatternEnum.BLINK));

        entity2.statusIdProperty().bind(entity1.statusIdProperty());
        entity2.statusNameProperty().bind(entity1.statusNameProperty());
        entity2.fontColorProperty().bind(entity1.fontColorProperty());
        entity2.backColorProperty().bind(entity1.backColorProperty());
        entity2.lightPatternProperty().bind(entity1.lightPatternProperty());
        entity1.setStatusId(3l);
        entity1.setStatusName(StatusPatternEnum.WORK_NORMAL);
        entity1.setFontColor("#FF00FF");
        entity1.setBackColor("#00FF00");
        entity1.setLightPattern(LightPatternEnum.LIGHTING);

        assertThat(entity1.getStatusId(), is(3l));
        assertThat(entity1.getStatusName(), is(StatusPatternEnum.WORK_NORMAL));
        assertThat(entity1.getFontColor(), is("#FF00FF"));
        assertThat(entity1.getBackColor(), is("#00FF00"));
        assertThat(entity1.getLightPattern(), is(LightPatternEnum.LIGHTING));
        assertThat(entity2.getStatusId(), is(3l));
        assertThat(entity2.getStatusName(), is(StatusPatternEnum.WORK_NORMAL));
        assertThat(entity2.getFontColor(), is("#FF00FF"));
        assertThat(entity2.getBackColor(), is("#00FF00"));
        assertThat(entity2.getLightPattern(), is(LightPatternEnum.LIGHTING));
    }

}
