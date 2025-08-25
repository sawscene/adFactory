/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.access.AccessHierarchyEntity;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;

/**
 *
 * @author
 */
public class AccessHierarchyEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static AccessHierarchyEntityFacadeREST rest = null;
    private static EquipmentEntityFacadeREST equipmentRest = null;
    private static RoleEntityFacadeREST roleRest = null;
    private static OrganizationEntityFacadeREST organizationRest = null;
    private static Long loginUserId = null;
    private static OrganizationEntity team1 = null;
    private static OrganizationEntity team2 = null;
    private static OrganizationEntity team3 = null;

    public AccessHierarchyEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();
        rest = new AccessHierarchyEntityFacadeREST();
        rest.setEntityManager(em);
        equipmentRest = new EquipmentEntityFacadeREST();
        equipmentRest.setEntityManager(em);
        equipmentRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());
        roleRest = new RoleEntityFacadeREST();
        roleRest.setEntityManager(em);
        organizationRest = new OrganizationEntityFacadeREST();
        organizationRest.setEntityManager(em);
        organizationRest.setEquipmentRest(equipmentRest);
        organizationRest.setRoleRest(roleRest);
        organizationRest.setAuthRest(rest);
        organizationRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());
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
    public void setUp() throws URISyntaxException {
        tx = em.getTransaction();

        Long updatePersonId = 678L;
        Date updateDateTime = new GregorianCalendar(2019, 1, 9, 14, 47, 6).getTime();
        OrganizationEntity team11 = new OrganizationEntity(0L, "team1", "team1", AuthorityEnum.SYSTEM_ADMIN, "", "password", "mail", updatePersonId, updateDateTime);
        OrganizationEntity team22 = new OrganizationEntity(0L, "team2", "team2", AuthorityEnum.ADMINISTRATOR, "", "password", "mail", updatePersonId, updateDateTime);
        OrganizationEntity team33 = new OrganizationEntity(0L, "team3", "team3", AuthorityEnum.WORKER, "", "password", "mail", updatePersonId, updateDateTime);
        tx.begin();
        organizationRest.add(team11, null);
        organizationRest.add(team22, null);
        organizationRest.add(team33, null);
        team1 = organizationRest.findByName("team1", loginUserId, null);
        team2 = organizationRest.findByName("team2", loginUserId, null);
        team3 = organizationRest.findByName("team3", loginUserId, null);
        tx.commit();
    }

    @After
    public void tearDown() {
        tx.begin();
        organizationRest.remove(team1.getOrganizationId(), null);
        organizationRest.remove(team2.getOrganizationId(), null);
        organizationRest.remove(team3.getOrganizationId(), null);
        tx.commit();

        DatabaseControll.reset(em, tx);
    }

    @Test
    public void testAccessHierarchyEntityFacadeREST() throws Exception {
        AccessHierarchyEntity access1 = new AccessHierarchyEntity();
        access1.setTypeId(AccessHierarchyTypeEnum.OrganizationHierarchy);
        access1.setHierarchyId(1L);
        access1.setOrganizationId(team1.getOrganizationId());
        AccessHierarchyEntity access2 = new AccessHierarchyEntity();
        access2.setTypeId(AccessHierarchyTypeEnum.EquipmentHierarchy);
        access2.setHierarchyId(2L);
        access2.setOrganizationId(team1.getOrganizationId());
        AccessHierarchyEntity access3 = new AccessHierarchyEntity();
        access3.setTypeId(AccessHierarchyTypeEnum.WorkHierarchy);
        access3.setHierarchyId(3L);
        access3.setOrganizationId(team1.getOrganizationId());
        AccessHierarchyEntity access4 = new AccessHierarchyEntity();
        access4.setTypeId(AccessHierarchyTypeEnum.WorkflowHierarchy);
        access4.setHierarchyId(4L);
        access4.setOrganizationId(team1.getOrganizationId());
        AccessHierarchyEntity access5 = new AccessHierarchyEntity();
        access5.setTypeId(AccessHierarchyTypeEnum.KanbanHierarchy);
        access5.setHierarchyId(5L);
        access5.setOrganizationId(team1.getOrganizationId());
        AccessHierarchyEntity access6 = new AccessHierarchyEntity();
        access6.setTypeId(AccessHierarchyTypeEnum.KanbanHierarchy);
        access6.setHierarchyId(5L);
        access6.setOrganizationId(team2.getOrganizationId());
        AccessHierarchyEntity access7 = new AccessHierarchyEntity();
        access7.setTypeId(AccessHierarchyTypeEnum.KanbanHierarchy);
        access7.setHierarchyId(5L);
        access7.setOrganizationId(team3.getOrganizationId());

        // 追加
        tx.begin();
        rest.add(access1, null);
        rest.add(access2, null);
        rest.add(access3, null);
        rest.add(access4, null);
        rest.add(access5, null);
        rest.add(access6, null);
        rest.add(access7, null);
        tx.commit();

        // 取得
        List<OrganizationEntity> find1 = rest.findHierarchyRange(AccessHierarchyTypeEnum.OrganizationHierarchy, 1L, null, null, null);
        assertThat(find1.contains(team1), is(true));
        List<OrganizationEntity> find2 = rest.findHierarchyRange(AccessHierarchyTypeEnum.EquipmentHierarchy, 2L, null, null, null);
        assertThat(find2.contains(team1), is(true));
        List<OrganizationEntity> find3 = rest.findHierarchyRange(AccessHierarchyTypeEnum.WorkHierarchy, 3L, null, null, null);
        assertThat(find3.contains(team1), is(true));
        List<OrganizationEntity> find4 = rest.findHierarchyRange(AccessHierarchyTypeEnum.WorkflowHierarchy, 4L, null, null, null);
        assertThat(find4.contains(team1), is(true));
        List<OrganizationEntity> find5 = rest.findHierarchyRange(AccessHierarchyTypeEnum.KanbanHierarchy, 5L, null, null, null);
        assertThat(find5.contains(team1), is(true));
        assertThat(find5.contains(team2), is(true));
        assertThat(find5.contains(team3), is(true));

        // 件数取得
        Long count1 = rest.countHierarchy(AccessHierarchyTypeEnum.OrganizationHierarchy, 1L, null);
        assertThat(count1, is(1L));
        Long count2 = rest.countHierarchy(AccessHierarchyTypeEnum.EquipmentHierarchy, 2L, null);
        assertThat(count2, is(1L));
        Long count3 = rest.countHierarchy(AccessHierarchyTypeEnum.WorkHierarchy, 3L, null);
        assertThat(count3, is(1L));
        Long count4 = rest.countHierarchy(AccessHierarchyTypeEnum.WorkflowHierarchy, 4L, null);
        assertThat(count4, is(1L));
        Long count5 = rest.countHierarchy(AccessHierarchyTypeEnum.KanbanHierarchy, 5L, null);
        assertThat(count5, is(3L));

        // 範囲取得
        find1 = rest.findHierarchyRange(AccessHierarchyTypeEnum.OrganizationHierarchy, 1L, 0, count1.intValue(), null);
        assertThat(find1.contains(team1), is(true));
        find2 = rest.findHierarchyRange(AccessHierarchyTypeEnum.EquipmentHierarchy, 2L, 0, count2.intValue(), null);
        assertThat(find2.contains(team1), is(true));
        find3 = rest.findHierarchyRange(AccessHierarchyTypeEnum.WorkHierarchy, 3L, 0, count3.intValue(), null);
        assertThat(find3.contains(team1), is(true));
        find4 = rest.findHierarchyRange(AccessHierarchyTypeEnum.WorkflowHierarchy, 4L, 0, count4.intValue(), null);
        assertThat(find4.contains(team1), is(true));
        find5 = rest.findHierarchyRange(AccessHierarchyTypeEnum.KanbanHierarchy, 5L, 0, count5.intValue(), null);
        assertThat(find5.contains(team1), is(true));
        assertThat(find5.contains(team2), is(true));
        assertThat(find5.contains(team3), is(true));

        // 削除
        tx.begin();
        rest.remove(AccessHierarchyTypeEnum.OrganizationHierarchy, 1L, Arrays.asList(team1.getOrganizationId()), null);
        rest.remove(AccessHierarchyTypeEnum.EquipmentHierarchy, 2L, Arrays.asList(team1.getOrganizationId()), null);
        rest.remove(AccessHierarchyTypeEnum.WorkHierarchy, 3L, Arrays.asList(team1.getOrganizationId()), null);
        rest.remove(AccessHierarchyTypeEnum.WorkflowHierarchy, 4L, Arrays.asList(team1.getOrganizationId()), null);
        rest.remove(AccessHierarchyTypeEnum.KanbanHierarchy, 5L, Arrays.asList(team1.getOrganizationId(), team2.getOrganizationId(), team3.getOrganizationId()), null);
        tx.commit();
        find1 = rest.findHierarchyRange(AccessHierarchyTypeEnum.OrganizationHierarchy, 1L, null, null, null);
        assertThat(find1.size(), is(0));
        find2 = rest.findHierarchyRange(AccessHierarchyTypeEnum.EquipmentHierarchy, 2L, null, null, null);
        assertThat(find2.size(), is(0));
        find3 = rest.findHierarchyRange(AccessHierarchyTypeEnum.WorkHierarchy, 3L, null, null, null);
        assertThat(find3.size(), is(0));
        find4 = rest.findHierarchyRange(AccessHierarchyTypeEnum.WorkflowHierarchy, 4L, null, null, null);
        assertThat(find4.size(), is(0));
        find5 = rest.findHierarchyRange(AccessHierarchyTypeEnum.KanbanHierarchy, 5L, null, null, null);
        assertThat(find5.size(), is(0));
    }

    @Test
    public void testError() throws Exception {
        System.out.println("testError");

        Response response;
        ResponseEntity responseEntity;

        AccessHierarchyEntity access1 = new AccessHierarchyEntity();
        access1.setTypeId(AccessHierarchyTypeEnum.OrganizationHierarchy);
        access1.setHierarchyId(1L);
        access1.setOrganizationId(team1.getOrganizationId());
        tx.begin();
        response = rest.add(access1, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        AccessHierarchyEntity access2 = new AccessHierarchyEntity();
        access2.setTypeId(AccessHierarchyTypeEnum.OrganizationHierarchy);
        access2.setHierarchyId(1L);
        access2.setOrganizationId(team1.getOrganizationId());
        tx.begin();
        response = rest.add(access2, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));

        tx.begin();
        response = rest.remove(AccessHierarchyTypeEnum.OrganizationHierarchy, 1L, Arrays.asList(team1.getOrganizationId()), null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(200));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
    }
}
