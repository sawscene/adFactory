/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.entity.response.ResponseWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.model.approval.ApprovalModel;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.adfactoryserver.utility.TestUtils;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.After;
import org.junit.AfterClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class WorkEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static WorkEntityFacadeREST workRest = null;
    private static ApprovalModel approvalModel = null;

    public WorkEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        approvalModel = new ApprovalModel();
        approvalModel.setEntityManager(em);

        workRest = new WorkEntityFacadeREST();
        workRest.setEntityManager(em);
        workRest.setApprovalModel(approvalModel);

        // 承認機能ライセンス有効
        TestUtils.setOptionLicense(LicenseOptionType.ApprovalOption, true);
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
    public void testWorkEntityFacadeREST() throws Exception {
        System.out.println("testWorkEntityFacadeREST");

        WorkEntity work1 = new WorkEntity(5L, "work1", 1, 500, "content", ContentTypeEnum.STRING, 0L, null, null, null);
        WorkEntity work2 = new WorkEntity(5L, "work2", 1, 500, "content", ContentTypeEnum.STRING, 0L, null, null, null);

        AddInfoEntity addInfo1 = new AddInfoEntity("propName1", CustomPropertyTypeEnum.TYPE_STRING, "propValue1", 1, null);
        AddInfoEntity addInfo2 = new AddInfoEntity("propName2", CustomPropertyTypeEnum.TYPE_STRING, "propValue2", 2, null);
        AddInfoEntity addInfo3 = new AddInfoEntity("propName3", CustomPropertyTypeEnum.TYPE_STRING, "propValue3", 3, null);
        List<AddInfoEntity> addInfos = new LinkedList();
        addInfos.addAll(Arrays.asList(addInfo1, addInfo2, addInfo3));

        // 追加情報一覧をJSON文字列に変換して工程の追加情報にセットする。
        String jsonProps = JsonUtils.objectsToJson(addInfos);
        work1.setWorkAddInfo(jsonProps);
        work2.setWorkAddInfo(jsonProps);

        tx.begin();
        workRest.add(work1, null);
        workRest.add(work2, null);
        tx.commit();

        WorkEntity actuals1 = workRest.find(work1.getWorkId(), false, null, null);
        assertThat(work1, is(actuals1));

        // 工程の追加情報のJSON文字列を追加情報一覧に変換する。
        List<AddInfoEntity> workProps1 = JsonUtils.jsonToObjects(actuals1.getWorkAddInfo(), AddInfoEntity[].class);

        int loop = 0;
        for (AddInfoEntity prop : workProps1) {
            assertThat(addInfos.get(loop), is(prop));
            loop++;
        }

        WorkEntity actuals2 = workRest.find(work2.getWorkId(), false, null, null);
        assertThat(work2, is(actuals2));

        // 工程の追加情報のJSON文字列を追加情報一覧に変換する。
        List<AddInfoEntity> workProps2 = JsonUtils.jsonToObjects(actuals2.getWorkAddInfo(), AddInfoEntity[].class);

        loop = 0;
        for (AddInfoEntity prop : workProps2) {
            assertThat(addInfos.get(loop), is(prop));
            loop++;
        }

        // 工程名を変更する。
        work1.setWorkName("work1-1");
        tx.begin();
        workRest.update(work1, null);
        tx.commit();

        actuals1 = workRest.find(work1.getWorkId(), false, null, null);
        assertThat(work1, is(actuals1));

        // 工程の追加情報のJSON文字列を追加情報一覧に変換する。
        workProps1 = JsonUtils.jsonToObjects(actuals1.getWorkAddInfo(), AddInfoEntity[].class);

        loop = 0;
        for (AddInfoEntity prop : workProps1) {
            assertThat(addInfos.get(loop), is(prop));
            loop++;
        }

        // 工程をコピーする。
        tx.begin();
        workRest.copy(work1.getWorkId(), null);
        tx.commit();

        List<WorkEntity> works = workRest.findRange(null, null, null);
        assertThat(works.size(), is(3));

        // 工程を削除する。
        for (WorkEntity w : works) {
            tx.begin();
            workRest.remove(w.getWorkId(), null);
            tx.commit();
        }

        works = workRest.findRange(null, null, null);
        assertThat(works.size(), is(0));
    }

    @Test
    public void testError() throws Exception {
        System.out.println("testError");

        Response response;
        ResponseEntity responseEntity;

        WorkEntity work1 = new WorkEntity(5L, "work1", 1, 500, "content", ContentTypeEnum.STRING, 0L, null, null, null);
        tx.begin();
        response = workRest.add(work1, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        WorkEntity work2 = new WorkEntity(5L, "work1", 1, 500, "content", ContentTypeEnum.STRING, 0L, null, null, null);
        tx.begin();
        response = workRest.add(work2, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));

        tx.begin();
        response = workRest.remove(work1.getWorkId(), null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(200));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
    }

    /**
     * 工程の改訂テスト
     *
     * @throws Exception 
     */
    @Test
    public void testRevise() throws Exception {
        System.out.println("revise");

        Long loginUserId = null;

        WorkEntity work = new WorkEntity(5L, "work", 1, 500, "content", ContentTypeEnum.STRING, 0L, null, null, null);

        AddInfoEntity addInfo1 = new AddInfoEntity("propName1", CustomPropertyTypeEnum.TYPE_STRING, "propValue1", 1, null);
        AddInfoEntity addInfo2 = new AddInfoEntity("propName2", CustomPropertyTypeEnum.TYPE_STRING, "propValue2", 2, null);
        AddInfoEntity addInfo3 = new AddInfoEntity("propName3", CustomPropertyTypeEnum.TYPE_STRING, "propValue3", 3, null);
        List<AddInfoEntity> addInfos = new LinkedList();
        addInfos.addAll(Arrays.asList(addInfo1, addInfo2, addInfo3));

        // 追加情報一覧をJSON文字列に変換して工程の追加情報にセットする。
        String jsonProps = JsonUtils.objectsToJson(addInfos);
        work.setWorkAddInfo(jsonProps);

        tx.begin();
        workRest.add(work, null);
        tx.commit();

        // 申請状態: 最終承認済
        work.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
        work.setApprovalId(1L);

        tx.begin();
        workRest.update(work, null);
        tx.commit();
        em.clear();

        WorkEntity targetWork = workRest.findByName("work", 1, true, loginUserId, null);

        int workRev = targetWork.getLatestRev();
        int testNum = 11;// 改訂テスト回数 (最後の1回前で最大値の登録を、最後に最大値オーバーのテストを実施するので、3以上を設定する)
        for (int i = 0; i < (testNum + 1); i++) {
            if (i == (testNum - 1)) {
                // 最後の1回前は、版数の最大値の登録確認のため、1小さい版数の工程順を登録
                workRev = Constants.WORKFLOW_REV_LIMIT - 1;
                WorkEntity workLast = new WorkEntity(targetWork);
                workLast.setWorkRev(workRev);
                tx.begin();
                workRest.add(workLast, null);
                tx.commit();
            }

            workRev++;

            tx.begin();
            Response res = workRest.revise(targetWork.getWorkId(), null);
            tx.commit();
            ResponseWorkEntity resWork = (ResponseWorkEntity) res.getEntity();
            if (workRev < 1000) {
                // 版数の最大値までのチェック
                assertThat(resWork.isSuccess(), is(true));

                WorkEntity newWork = resWork.getValue();

                // 工程順名
                assertThat(newWork.getWorkName(), is(targetWork.getWorkName()));
                // 版数
                assertThat(newWork.getWorkRev(), is(workRev));

                // 工程の追加情報のJSON文字列を追加情報一覧に変換する。
                List<AddInfoEntity> targetProps = JsonUtils.jsonToObjects(targetWork.getWorkAddInfo(), AddInfoEntity[].class);
                List<AddInfoEntity> newProps = JsonUtils.jsonToObjects(newWork.getWorkAddInfo(), AddInfoEntity[].class);

                assertThat(newProps.size(), is(targetProps.size()));
                for (AddInfoEntity prop : newProps) {
                    Optional<AddInfoEntity> opt = targetProps.stream().filter(p -> p.getKey().equals(prop.getKey())).findFirst();
                    assertThat(opt.isPresent(), is(true));

                    AddInfoEntity targetProp = opt.get();
                    assertThat(prop.getDisp(), is(targetProp.getDisp()));
                    assertThat(prop.getType(), is(targetProp.getType()));
                    assertThat(prop.getVal(), is(targetProp.getVal()));
                }

                // 改版後の申請状態は「未承認」で申請情報なし
                assertThat(newWork.getApprovalState(), is(ApprovalStatusEnum.UNAPPROVED));
                assertThat(newWork.getApprovalId(), is(nullValue()));
            } else {
                // 版数の最大値オーバーのチェック
                assertThat(resWork.isSuccess(), is(false));
                assertThat(resWork.getErrorType(), is(ServerErrorTypeEnum.OVER_MAX_VALUE));
            }
        }
    }
}
