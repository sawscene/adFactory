/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.jdbc;

import adtekfuji.adbridgebi.jdbc.adfactorydb.AdFactoryWorkAccessor;
import adtekfuji.property.AdProperty;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author nar-nakamura
 */
public class KanbanAccessorTest {
    
    public KanbanAccessorTest() {
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
    @Ignore
    @Test
    public void testGetVersion() throws Exception {
        System.out.println("testAdd");

        AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
        AdProperty.load("adBridgeBI.properties");

        AdFactoryWorkAccessor accessor = new AdFactoryWorkAccessor();

        String dbVersion = accessor.getVersion();
        assertEquals(dbVersion, "2.10");// ※現在のデータベースバージョン
    }
}
