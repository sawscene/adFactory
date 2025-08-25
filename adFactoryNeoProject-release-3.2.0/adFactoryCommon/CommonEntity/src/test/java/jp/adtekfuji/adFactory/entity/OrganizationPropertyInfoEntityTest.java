/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.organization.OrganizationPropertyInfoEntity;
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
public class OrganizationPropertyInfoEntityTest {

    public OrganizationPropertyInfoEntityTest() {
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

        OrganizationPropertyInfoEntity entity = new OrganizationPropertyInfoEntity(1L, 1L, "name", "TYPE_STRING", "value", 3);
        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"organizationProperty"};
        String[] expectedNode2 = {"organizationPropId", "fkMasterId", "organizationPropName", "organizationPropType",
            "organizationPropValue", "organizationPropOrder"};
        String[] expectedValue2 = {"1", "1", "name", "TYPE_STRING", "value", "3"};

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

        OrganizationPropertyInfoEntity workProp1 = new OrganizationPropertyInfoEntity(1L, 1L, "name1", "TYPE_STRING", "value1", 1);
        OrganizationPropertyInfoEntity workProp2 = new OrganizationPropertyInfoEntity(2L, 2L, "name2", "TYPE_BOOLEAN", "value2", 3);
        assertThat(workProp1.getOrganizationPropId(), is(1L));
        assertThat(workProp1.getFkMasterId(), is(1L));
        assertThat(workProp1.getOrganizationPropName(), is("name1"));
        assertThat(workProp1.getOrganizationPropType(), is("TYPE_STRING"));
        assertThat(workProp2.getOrganizationPropId(), is(2L));
        assertThat(workProp2.getFkMasterId(), is(2L));
        assertThat(workProp2.getOrganizationPropName(), is("name2"));
        assertThat(workProp2.getOrganizationPropType(), is("TYPE_BOOLEAN"));

        workProp2.organizationPropIdProperty().bind(workProp1.organizationPropIdProperty());
        workProp2.fkMasterIdProperty().bind(workProp1.fkMasterIdProperty());
        workProp2.organizationPropNameProperty().bind(workProp1.organizationPropNameProperty());
        workProp2.organizationPropTypeProperty().bind(workProp1.organizationPropTypeProperty());
        workProp1.setOrganizationPropId(3L);
        workProp1.setFkMasterId(3L);
        workProp1.setOrganizationPropName("name3");
        workProp1.setOrganizationPropType("TYPE_INTEGER");
        assertThat(workProp1.getOrganizationPropId(), is(3L));
        assertThat(workProp1.getFkMasterId(), is(3L));
        assertThat(workProp1.getOrganizationPropName(), is("name3"));
        assertThat(workProp1.getOrganizationPropType(), is("TYPE_INTEGER"));
        assertThat(workProp2.getOrganizationPropId(), is(3L));
        assertThat(workProp2.getFkMasterId(), is(3L));
        assertThat(workProp2.getOrganizationPropName(), is("name3"));
        assertThat(workProp2.getOrganizationPropType(), is("TYPE_INTEGER"));
    }
}
