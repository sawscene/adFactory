/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.jdbc;

import adtekfuji.adbridgebi.jdbc.progressdb.KanbanWorkProgressAccessor;
import adtekfuji.adbridgebi.entity.KanbanWorkProgressEntity;
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
public class KanbanWorkProgressAccessorTest {
    
    public KanbanWorkProgressAccessorTest() {
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
        KanbanWorkProgressAccessor accessor = new KanbanWorkProgressAccessor();
        try {
            if (Objects.isNull(progressDb.getConnection())) {
                progressDb.openDB();
            }

            progressDb.getConnection().setAutoCommit(false);// 自動コミット解除 (トランザクション開始)

            List<KanbanWorkProgressEntity> entities;
            int result;

            // 全て削除
            accessor.removeAll();

            // 大量追加
            int createNum = 1000;// 作成数
            entities = new ArrayList();
            for (int progressNo = 1; progressNo <= createNum; progressNo++) {
                KanbanWorkProgressEntity progress = new KanbanWorkProgressEntity();

                progress.setProgressNo(String.valueOf(progressNo));
                progress.setKanbanName(String.format("カンバン %d", progressNo));

                int no = 1;
                for (int i = 0; i < KanbanWorkProgressEntity.MAX_WORK; i++) {
                    progress.setWorkName(i, String.format("工程 %d", no));
                    progress.setStartDate(i, "2019/07/25");
                    progress.setStatus(i, "1");
                    progress.setTodayFlg(i, "0");
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

    // ※テストを実行すると、実際のデータベースを操作するので注意。
    /**
     * 全てのカンバンの進捗情報(工程基準)の本日フラグを更新する。(結果はデータベースで確認)
     *
     * @throws java.lang.Exception
     */
    @Ignore
    @Test
    public void testUpdateTodayFlags() throws Exception {
        System.out.println("testUpdateTodayFlags");

//        KanbanWorkProgressAccessor accessor = new KanbanWorkProgressAccessor();
//
//        // 全てのカンバンの進捗情報(工程基準)の本日フラグを更新する。
//        boolean result = accessor.updateTodayFlgs();
//        assertEquals(result, true);
    }
}
