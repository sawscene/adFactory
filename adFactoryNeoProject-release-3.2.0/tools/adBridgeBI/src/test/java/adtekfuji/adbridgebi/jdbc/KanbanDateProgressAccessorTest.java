/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.jdbc;

import adtekfuji.adbridgebi.jdbc.progressdb.KanbanDateProgressAccessor;
import adtekfuji.adbridgebi.entity.KanbanDateProgressEntity;
import adtekfuji.adbridgebi.jdbc.progressdb.ProgressDbConnector;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class KanbanDateProgressAccessorTest {
    
    public KanbanDateProgressAccessorTest() {
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
        KanbanDateProgressAccessor accessor = new KanbanDateProgressAccessor();
        try {
            if (Objects.isNull(progressDb.getConnection())) {
                progressDb.openDB();
            }

            progressDb.getConnection().setAutoCommit(false);// 自動コミット解除 (トランザクション開始)

            List<KanbanDateProgressEntity> entities;
            int result;

            // 全て削除
            accessor.removeAll();

            // 大量追加
            int createNum = 1000;// 作成数
            entities = new ArrayList();
            for (int progressNo = 1; progressNo <= createNum; progressNo++) {
                KanbanDateProgressEntity progress = new KanbanDateProgressEntity();

                progress.setProgressNo(String.valueOf(progressNo));
                progress.setKanbanName(String.format("カンバン %d", progressNo));

                int no = 1;
                for (int i = 0; i < KanbanDateProgressEntity.MAX_WORK; i++) {
                    progress.setWorkName(i, String.format("工程 %d", no));
                    progress.setStatus(i, "1");
                    no++;
                }

                entities.add(progress);
            }

            result = accessor.add(entities);
            assertEquals(result, createNum);

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
