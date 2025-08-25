/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.jdbc;

import adtekfuji.adbridgebi.jdbc.progressdb.WorkProgressAccessor;
import adtekfuji.adbridgebi.entity.WorkProgressEntity;
import adtekfuji.adbridgebi.jdbc.progressdb.ProgressDbConnector;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nar-nakamura
 */
public class WorkProgressAccessorTest {
    
    public WorkProgressAccessorTest() {
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

    // ※テストを実行すると、実際のデータベースを操作するので注意。
    /**
     * 全てのデータを削除した後、データを追加する。(結果はデータベースで確認)
     *
     * @throws java.lang.Exception
     */
    @Ignore
    @Test
    public void testAdd() throws Exception {
        System.out.println("testAdd");

        DbConnector progressDb = ProgressDbConnector.getInstance();
        WorkProgressAccessor accessor = new WorkProgressAccessor();
        try {
            if (Objects.isNull(progressDb.getConnection())) {
                progressDb.openDB();
            }

            progressDb.getConnection().setAutoCommit(false);// 自動コミット解除 (トランザクション開始)

            List<WorkProgressEntity> entities;
            int result;

            // 全て削除
            accessor.removeAll();

            // 大量追加
            int createNum = 1000;// 作成数
            entities = new ArrayList();
            for (int progressNo = 1; progressNo <= createNum; progressNo++) {
                for (int progressType = 1; progressType <= 2; progressType++) {
                    for (int progressOrder = 1; progressOrder <= 20; progressOrder++) {
                        WorkProgressEntity wp = new WorkProgressEntity();

                        wp.setProgressNo(String.valueOf(progressNo));
                        wp.setProgressType(String.valueOf(progressType));
                        wp.setProgressOrder(String.valueOf(progressOrder));
                        wp.setProgressDate("2019/07/25");
                        wp.setStartTime("10:00");
                        wp.setCompTime("11:00");
                        wp.setWorkName(String.format("工程 %d", progressOrder));

                        entities.add(wp);
                    }
                }
            }

            result = accessor.add(entities);
            assertEquals(result, createNum * 2 * 20);

            // 更新
            WorkProgressEntity wp1 = new WorkProgressEntity();
            wp1.setProgressNo("1");
            wp1.setProgressType("0");
            wp1.setProgressOrder("1");
            wp1.setProgressDate("2019/07/26");
            wp1.setStartTime("10:00");
            wp1.setCompTime("11:00");
            wp1.setWorkName("工程①");

            WorkProgressEntity wp2 = new WorkProgressEntity();
            wp2.setProgressNo("1");
            wp2.setProgressType("0");
            wp2.setProgressOrder("2");
            wp2.setProgressDate("2019/07/26");
            wp2.setStartTime("11:00");
            wp2.setCompTime("12:00");
            wp2.setWorkName("工程②");

            WorkProgressEntity wp3 = new WorkProgressEntity();
            wp3.setProgressNo("1");
            wp3.setProgressType("0");
            wp3.setProgressOrder("3");
            wp3.setProgressDate("2019/07/26");
            wp3.setStartTime("12:00");
            wp3.setCompTime("14:00");
            wp3.setWorkName("工程③");

            WorkProgressEntity wp4 = new WorkProgressEntity();
            wp4.setProgressNo("1");
            wp4.setProgressType("0");
            wp4.setProgressOrder("4");
            wp4.setProgressDate("2019/07/26");
            wp4.setStartTime("12:00");
            wp4.setCompTime("13:00");
            wp4.setWorkName("昼休憩");

            entities = Arrays.asList(wp1, wp2, wp3, wp4);

            result = accessor.add(entities);
            assertEquals(result, 4);

            progressDb.getConnection().commit();// コミット

        } catch (Exception ex) {
            try {
                progressDb.getConnection().rollback();// ロールバック
            } catch (SQLException sqlEx) {
            }
        } finally {
            progressDb.closeDB();
        }
    }
}
