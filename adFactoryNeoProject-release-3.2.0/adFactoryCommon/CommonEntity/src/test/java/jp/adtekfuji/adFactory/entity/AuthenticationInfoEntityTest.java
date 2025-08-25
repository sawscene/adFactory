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
import jp.adtekfuji.adFactory.entity.organization.AuthenticationInfoEntity;
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
public class AuthenticationInfoEntityTest {

    public AuthenticationInfoEntityTest() {
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

        AuthenticationInfoEntity entity = new AuthenticationInfoEntity(1L, 2L, "type", "value");
        entity.setAuthenticationPeriod(new GregorianCalendar(2015, 10, 15, 14, 47, 6).getTime());
        entity.setUseLock(Boolean.FALSE);
        Document xml = EntityToXml.getXml(entity);
        String[] expectedNode1 = {"authenticationInfo"};
        String[] expectedNode2 = {"authenticationId", "fkMasterId", "authenticationPeriod", "authenticationType", "authenticationData", "useLock",};
        String[] expectedValue2 = {"1", "2", "2015-11-15T14:47:06+09:00", "type", "value", "false"};

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

        AuthenticationInfoEntity authentication1 = new AuthenticationInfoEntity(1L, 1L, "type1", "value1");
        AuthenticationInfoEntity authentication2 = new AuthenticationInfoEntity(2L, 2L, "type2", "value2");
        assertThat(authentication1.getAuthenticationId(), is(1L));
        assertThat(authentication1.getFkMasterId(), is(1L));
        assertThat(authentication1.getAuthenticationType(), is("type1"));
        assertThat(authentication1.getAuthenticationData(), is("value1"));
        assertThat(authentication2.getAuthenticationId(), is(2L));
        assertThat(authentication2.getFkMasterId(), is(2L));
        assertThat(authentication2.getAuthenticationType(), is("type2"));
        assertThat(authentication2.getAuthenticationData(), is("value2"));

        authentication2.authenticationIdProperty().bind(authentication1.authenticationIdProperty());
        authentication2.fkMasterIdProperty().bind(authentication1.fkMasterIdProperty());
        authentication2.authenticationTypeProperty().bind(authentication1.authenticationTypeProperty());
        authentication2.authenticationDataProperty().bind(authentication1.authenticationDataProperty());
        authentication1.setAuthenticationId(3L);
        authentication1.setFkMasterId(3L);
        authentication1.setAuthenticationType("type3");
        authentication1.setAuthenticationData("value3");
        assertThat(authentication1.getAuthenticationId(), is(3L));
        assertThat(authentication1.getFkMasterId(), is(3L));
        assertThat(authentication1.getAuthenticationType(), is("type3"));
        assertThat(authentication1.getAuthenticationData(), is("value3"));
        assertThat(authentication2.getAuthenticationId(), is(3L));
        assertThat(authentication2.getFkMasterId(), is(3L));
        assertThat(authentication2.getAuthenticationType(), is("type3"));
        assertThat(authentication2.getAuthenticationData(), is("value3"));
    }
}
