/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.PartsInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.PartsRemoveCondition;
import jp.adtekfuji.adFactory.entity.kanban.SerialNoInfo;
import jp.adtekfuji.adfactoryserver.entity.kanban.PartsEntity;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author nar-nakamura
 */
public class PartsEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static PartsEntityFacadeREST rest = null;

    public PartsEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();
        rest = new PartsEntityFacadeREST();
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
    public void test() throws Exception {
        System.out.println("test");

        Long workKanbanId = 0L;
        Date now = new Date();

        int partsCount = 4;

        List<PartsInfoEntity> partsInfos = new LinkedList();
        for (int num = 1; num <= partsCount; num++) {
            PartsInfoEntity parts = new PartsInfoEntity();

            String serialNoInfo = JsonUtils.objectsToJson(this.createSerialNoInfos(num, 5));

            parts.setPartsId(new StringBuilder("パーツ(H01)").append(num).toString());
            parts.setSerialNoInfo(serialNoInfo);
            parts.setWorkKanbanId(null);
            parts.setCompDatetime(null);

            partsInfos.add(parts);
        }

        // add
        String jsonStr = JsonUtils.objectsToJson(partsInfos);

        tx.begin();
        rest.registParts(jsonStr, workKanbanId, now);
        tx.commit();
        em.clear();

        // find
        for (int i = 0; i < partsCount; i++) {
            PartsInfoEntity target = partsInfos.get(i);
            PartsEntity parts = rest.findParts(target.getPartsId(), null);

            assertThat(parts.getPartsId(), is(target.getPartsId()));
            assertThat(parts.getSerialNoInfo(), is(target.getSerialNoInfo()));
            assertThat(parts.getWorkKanbanId(), is(workKanbanId));
            assertThat(parts.getCompDatetime(), is(now));
        }

        // find (該当なし)
        PartsEntity errParts = rest.findParts("errSerial", null);

        assertThat(errParts.getPartsId(), is(nullValue()));
        assertThat(errParts.getWorkKanbanId(), is(nullValue()));
        assertThat(errParts.getSerialNoInfo(), is(nullValue()));

        List<PartsEntity> list1 = rest.searchParts("H01", null);
        assertThat(list1.size(), is(4));

        tx.begin();
        PartsRemoveCondition condition = new PartsRemoveCondition(Arrays.asList(partsInfos.get(0), partsInfos.get(1)));
        Response res = rest.removeForced(condition, null);
        tx.commit();
        
        assertThat(res.getStatus(), is(HttpURLConnection.HTTP_OK));
        ResponseEntity responseEntity = (ResponseEntity) res.getEntity();
        assertThat(responseEntity.getUserData(), is(2));

        List<PartsEntity> list2 = rest.searchParts("パーツ", null);
        assertThat(list2.size(), is(2));

        //PartsEntity parts = rest.findParts(partsInfos.get(0).getPartsId(), null);
        //assertThat(parts.getRemoveFlag(), is(true));
        //assertThat(parts.getDestWorkKanbanId(), is(0L));
    }

    /**
     * 指定した件数のシリアル番号情報一覧を作成する。
     *
     * @param id 品名・シリアルに付ける番号 (「品名{id}-{n}」のようになる)
     * @param count 作成する件数
     * @return シリアル番号情報一覧
     */
    private List<SerialNoInfo> createSerialNoInfos(int id, int count) {
        List<SerialNoInfo> infos = new LinkedList();
        for (int num = 1; num < (count + 1); num++) {
            SerialNoInfo info = new SerialNoInfo();

            String productName = new StringBuilder("品名").append(id).append("-").append(num).toString();
            String serialNo = new StringBuilder("シリアル").append(id).append("-").append(num).toString();

            info.setProductName(productName);
            info.setSerialNo(serialNo);

            infos.add(info);
        }
        return infos;
    }
}
