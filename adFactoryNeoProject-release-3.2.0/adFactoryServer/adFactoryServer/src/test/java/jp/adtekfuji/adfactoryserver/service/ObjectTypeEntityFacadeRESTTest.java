/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.List;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jp.adtekfuji.adfactoryserver.entity.object.ObjectTypeEntity;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author nar-nakamura
 */
public class ObjectTypeEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static ObjectTypeEntityFacadeREST rest = null;
    private static ObjectEntityFacadeREST objectEntityFacadeREST = null;

    public ObjectTypeEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();
        objectEntityFacadeREST = new ObjectEntityFacadeREST();
        objectEntityFacadeREST.setEntityManager(em);
        rest = new ObjectTypeEntityFacadeREST();
        rest.setEntityManager(em);
        rest.setObjectRest(objectEntityFacadeREST);
    }

    @AfterClass
    public static void tearDownClass() {
        if (Objects.nonNull(em)) {
            em.close();
        }
        if (Objects.nonNull(emf)) {
            emf.close();
        }
    }

    @Before
    public void setUp() {
        tx = em.getTransaction();
    }

    @After
    public void tearDown() {
        DatabaseControll.reset(em, tx);
    }

    @Test
    public void testObjectTypeEntityFacadeREST() throws Exception {
        System.out.println("testObjectTypeEntityFacadeREST");

        ObjectTypeEntity objectType1 = new ObjectTypeEntity("objectTypeName1");
        ObjectTypeEntity objectType2 = new ObjectTypeEntity("objectTypeName2");
        ObjectTypeEntity objectType3 = new ObjectTypeEntity("objectTypeName3");
        ObjectTypeEntity objectType4 = new ObjectTypeEntity("objectTypeName4");

        // add
        tx.begin();
        rest.add(objectType1, null);
        rest.add(objectType2, null);
        rest.add(objectType3, null);
        rest.add(objectType4, null);
        tx.commit();
        em.clear();
        ObjectTypeEntity actuals1 = rest.find(objectType1.getObjectTypeId(), null);
        assertThat(objectType1, is(actuals1));
        ObjectTypeEntity actuals2 = rest.find(objectType2.getObjectTypeId(), null);
        assertThat(objectType2, is(actuals2));
        ObjectTypeEntity actuals3 = rest.find(objectType3.getObjectTypeId(), null);
        assertThat(objectType3, is(actuals3));
        ObjectTypeEntity actuals4 = rest.find(objectType4.getObjectTypeId(), null);
        assertThat(objectType4, is(actuals4));

        // update
        long targetTypeId;

        targetTypeId = objectType1.getObjectTypeId();
        objectType1.setObjectTypeName("objectTypeName1a");
        tx.begin();
        rest.update(objectType1, null);
        tx.commit();
        em.clear();
        actuals1 = rest.find(objectType1.getObjectTypeId(), null);
        assertThat(objectType1, is(actuals1));

//        targetTypeId = objectType2.getObjectTypeId();
//        objectType2.setObjectTypeId("objectType2a");
//        tx.begin();
//        rest.update(targetTypeId, objectType2);
//        tx.commit();
//        em.clear();
//        actuals2 = rest.find(objectType2.getObjectTypeId(), null);
//        assertThat(objectType2, is(actuals2));
//
//        targetTypeId = objectType3.getObjectTypeId();
//        objectType3.setObjectTypeId("objectTypeId3a");
//        objectType1.setObjectTypeName("objectTypeName3a");
//        tx.begin();
//        rest.update(targetTypeId, objectType3);
//        tx.commit();
//        em.clear();
//        actuals3 = rest.find(objectType3.getObjectTypeId()), null;
//        assertThat(objectType3, is(actuals3));
//
//        targetTypeId = objectType3.getObjectTypeId();
//        objectType3.setObjectTypeId("objectTypeId3");
//        tx.begin();
//        rest.update(targetTypeId, objectType3);
//        tx.commit();
//        em.clear();
//        actuals3 = rest.find(objectType3.getObjectTypeId(), null);
//        assertThat(objectType3, is(actuals3));

        List<ObjectTypeEntity> objectTypes = rest.findAll(null);
        assertThat(objectTypes.size(), is(4));

        // delete
        for (ObjectTypeEntity ent : objectTypes) {
            tx.begin();
            rest.remove(ent.getObjectTypeId(), null);
            tx.commit();
            em.clear();
        }
        objectTypes = rest.findAll(null);
        assertThat(objectTypes.size(), is(0));
    }
}
