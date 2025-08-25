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
import jp.adtekfuji.adfactoryserver.entity.object.ObjectEntity;
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
public class ObjectEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static ObjectEntityFacadeREST rest = null;

    public ObjectEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();
        rest = new ObjectEntityFacadeREST();
        rest.setEntityManager(em);
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
    public void testObjectEntityFacadeREST() throws Exception {
        System.out.println("testObjectEntityFacadeREST");

        ObjectEntity object1 = new ObjectEntity("objectId1", 1L, "objectName1");
        ObjectEntity object2 = new ObjectEntity("objectId2", 1L, "objectName2");
        ObjectEntity object3 = new ObjectEntity("objectId1", 2L, "objectName3");
        ObjectEntity object4 = new ObjectEntity("objectId3", 2L, "objectName4");

        // add
        tx.begin();
        rest.add(object1, null);
        rest.add(object2, null);
        rest.add(object3, null);
        rest.add(object4, null);
        tx.commit();
        em.clear();
        ObjectEntity actuals1 = rest.find(object1.getObjectId(), object1.getObjectTypeId(), null);
        assertThat(object1, is(actuals1));
        ObjectEntity actuals2 = rest.find(object2.getObjectId(), object2.getObjectTypeId(), null);
        assertThat(object2, is(actuals2));
        ObjectEntity actuals3 = rest.find(object3.getObjectId(), object3.getObjectTypeId(), null);
        assertThat(object3, is(actuals3));
        ObjectEntity actuals4 = rest.find(object4.getObjectId(), object4.getObjectTypeId(), null);
        assertThat(object4, is(actuals4));

        // update
        String targetId;
        long targetTypeId;

        targetId = object1.getObjectId();
        targetTypeId = object1.getObjectTypeId();
        object1.setObjectName("objectName1a");
        tx.begin();
        rest.update(targetId, targetTypeId, object1, null);
        tx.commit();
        em.clear();
        actuals1 = rest.find(object1.getObjectId(), object1.getObjectTypeId(), null);
        assertThat(object1, is(actuals1));

        targetId = object2.getObjectId();
        targetTypeId = object2.getObjectTypeId();
        object2.setObjectId("objectId3");
        tx.begin();
        rest.update(targetId, targetTypeId, object2, null);
        tx.commit();
        em.clear();
        actuals2 = rest.find(object2.getObjectId(), object2.getObjectTypeId(), null);
        assertThat(object2, is(actuals2));

        targetId = object3.getObjectId();
        targetTypeId = object3.getObjectTypeId();
        object3.setObjectTypeId(3L);
        tx.begin();
        rest.update(targetId, targetTypeId, object3, null);
        tx.commit();
        em.clear();
        actuals3 = rest.find(object3.getObjectId(), object3.getObjectTypeId(), null);
        assertThat(object3, is(actuals3));

        targetId = object3.getObjectId();
        targetTypeId = object3.getObjectTypeId();
        object3.setObjectTypeId(2L);
        tx.begin();
        rest.update(targetId, targetTypeId, object3, null);
        tx.commit();
        em.clear();
        actuals3 = rest.find(object3.getObjectId(), object3.getObjectTypeId(), null);
        assertThat(object3, is(actuals3));

        targetId = object4.getObjectId();
        targetTypeId = object4.getObjectTypeId();
        object4.setObjectId("objectId3a");
        object4.setObjectTypeId(3L);
        tx.begin();
        rest.update(targetId, targetTypeId, object4, null);
        tx.commit();
        em.clear();
        actuals4 = rest.find(object4.getObjectId(), object4.getObjectTypeId(), null);
        assertThat(object4, is(actuals4));

        // copy
        tx.begin();
        rest.copy(object1.getObjectId(), object1.getObjectTypeId(), null);
        em.flush();
        rest.copy(object1.getObjectId(), object1.getObjectTypeId(), null);
        tx.commit();
        em.clear();
        List<ObjectEntity> objects = rest.findAll(null);
        assertThat(objects.size(), is(6));

        // delete
        for (ObjectEntity ent : objects) {
            tx.begin();
            rest.remove(ent.getObjectId(), ent.getObjectTypeId(), null);
            tx.commit();
            em.clear();
        }
        objects = rest.findAll(null);
        assertThat(objects.size(), is(0));
    }
}
