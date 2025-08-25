/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.excelreplacer;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class ExcelReplacerTest {

    public ExcelReplacerTest() {
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
     * Test of replace method, of class ExcelReplacer.
     *
     * @throws java.lang.Exception
     */
    @Ignore
    @Test
    public void testReplace() throws Exception {
        System.out.println("replace");
//        File inFile = new File(".\\ExcelReplacer\\src\\test\\resources\\payout_kanban.xlsx");
//        File outFile = new File(".\\ExcelReplacer\\src\\test\\resources\\payout_kanban_out.xlsx");
        File inFile = Paths.get(getClass().getResource("/payout_kanban.xlsx").toURI()).toFile();
        File outFile = Paths.get(getClass().getResource("/payout_kanban_out.xlsx").toURI()).toFile();
        Map<String, Object> replaceWords = new HashMap<>();
        replaceWords.put("TAG_PAYOUT_IDENT_NAME", "部品AA");
        replaceWords.put("TAG_START_NUMBER", "5060");
        replaceWords.put("TAG_COMP_NUMER", "6680");
        replaceWords.put("TAG_UNIT_CODE", "aaaaaa");
        replaceWords.put("TAG_UNIT_NAME", "bbdsabb");
        replaceWords.put("TAG_PAYOUT_DAY", new Date());
        replaceWords.put("TAG_PAYOUT_NAME", "bcvb6744");
        replaceWords.put("TAG_DATETIME", new Date());
        replaceWords.put("TAG_DATE", new Date());
        replaceWords.put("TAG_TIME", new Date());

        BufferedImage image = ImageIO.read(Paths.get(getClass().getResource("/sample.png").toURI()).toFile());
        replaceWords.put("TAG_QRCODE_IMAGE", image);

        ExcelReplacer.replace(inFile, outFile, replaceWords, ExcelReplacer::toUpperString);

        Desktop desktop = Desktop.getDesktop();
        desktop.print(outFile);
    }

}
