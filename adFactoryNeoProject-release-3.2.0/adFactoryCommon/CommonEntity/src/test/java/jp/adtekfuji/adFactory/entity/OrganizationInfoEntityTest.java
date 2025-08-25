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
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
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
public class OrganizationInfoEntityTest {

    public OrganizationInfoEntityTest() {
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

        OrganizationInfoEntity entity = new OrganizationInfoEntity(1L, "OrganizationIdentName", "OrganizationName", AuthorityEnum.SYSTEM_ADMIN);
        entity.setParentId(0L);
        entity.setLangIds("JP");
        entity.setMailAddress("xxx@adtekfuji.co.jp");
        entity.setUpdatePersonId(1L);
        entity.setUpdateDateTime(new GregorianCalendar(2015, 10, 15, 14, 47, 6).getTime());
        List<OrganizationPropertyInfoEntity> properties1 = new ArrayList<>();
        properties1.add(new OrganizationPropertyInfoEntity(1L, 1L, "name", "TYPE_STRING", "value", 3));
        entity.setPropertyInfoCollection(properties1);
        List<Long> properties2 = new ArrayList<>();
        properties2.add(1L);
        entity.setBreakTimeInfoCollection(properties2);
        entity.setRoleCollection(properties2);
        entity.setWorkCategoryCollection(properties2);
        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"organization"};

        String[] expectedNode2 = {"organizationId", "parentId", "organizationIdentify", "organizationName",
                "authorityType", "langIds", "mailAddress", "updatePersonId", "updateDatetime", "childCount", //"organizationPropertys",
                "breaktimes", "roles", "workCategories"
        };

        String[] expectedValue2 = {"1", "0", "OrganizationIdentName", "OrganizationName",
                "SYSTEM_ADMIN", "JP", "xxx@adtekfuji.co.jp", "1", "2015-11-15T14:47:06+09:00", "0", //"11nameTYPE_STRINGvalue3",
                "1", "1", "1"
        };

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

        OrganizationInfoEntity organizationInfoEntity1 = new OrganizationInfoEntity(1L, "OrganizationIdentName1", "OrganizationName1", AuthorityEnum.SYSTEM_ADMIN);
        OrganizationInfoEntity organizationInfoEntity2 = new OrganizationInfoEntity(2L, "OrganizationIdentName2", "OrganizationName2", AuthorityEnum.WORKER);
        assertThat(organizationInfoEntity1.getOrganizationId(), is(1L));
        assertThat(organizationInfoEntity1.getOrganizationName(), is("OrganizationName1"));
        assertThat(organizationInfoEntity2.getOrganizationId(), is(2L));
        assertThat(organizationInfoEntity2.getOrganizationName(), is("OrganizationName2"));

        organizationInfoEntity2.organizationIdProperty().bind(organizationInfoEntity1.organizationIdProperty());
        organizationInfoEntity2.organizationNameProperty().bind(organizationInfoEntity1.organizationNameProperty());
        organizationInfoEntity1.setOrganizationId(3L);
        organizationInfoEntity1.setOrganizationName("OrganizationName3");
        assertThat(organizationInfoEntity1.getOrganizationId(), is(3L));
        assertThat(organizationInfoEntity1.getOrganizationName(), is("OrganizationName3"));
        assertThat(organizationInfoEntity2.getOrganizationId(), is(3L));
        assertThat(organizationInfoEntity2.getOrganizationName(), is("OrganizationName3"));
    }
}
