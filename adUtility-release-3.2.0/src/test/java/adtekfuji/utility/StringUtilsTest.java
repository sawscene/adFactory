/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import javafx.scene.paint.Color;
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
public class StringUtilsTest {
    
    public StringUtilsTest() {
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
     * Test of isEmpty method, of class StringUtils.
     */
    @Ignore
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        String value = "";
        boolean expResult = false;
        boolean result = StringUtils.isEmpty(value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of colorToRGBCode method, of class StringUtils.
     */
    @Ignore
    @Test
    public void testColorToRGBCode() {
        System.out.println("colorToRGBCode");
        Color color = null;
        String expResult = "";
        String result = StringUtils.colorToRGBCode(color);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseBoolean method, of class StringUtils.
     */
    @Ignore
    @Test
    public void testParseBoolean() {
        System.out.println("parseBoolean");
        String value = "";
        boolean expResult = false;
        boolean result = StringUtils.parseBoolean(value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    @Test
    public void testParseLong() {
        System.out.println("parseLong");
        
        // 最大値が正常に変換されること
        {
            final String value = "9223372036854775807";
            assertEquals(StringUtils.parseLong(value), 9223372036854775807L);
        }
        
        //最小値が正常に変換されること
        {
            final String value = "-9223372036854775808";
            assertEquals(StringUtils.parseLong(value), -9223372036854775808L);
        }

        // 最大値超過が0となること
        {
            final String value = "9223372036854775808";
            assertEquals(StringUtils.parseLong(value), 0L);
        }

        // 最小値未満が0となること
        {
            final String value = "-9223372036854775809";
            assertEquals(StringUtils.parseLong(value), 0L);
        }

        // 数字以外が0となること
        {
            final String value = "adtek";
            assertEquals(StringUtils.parseLong(value), 0L);
        }

        // 数字以外が0となること(全角)
        {
            final String value = "アドテック";
            assertEquals(StringUtils.parseLong(value), 0L);
        }
    }

    /**
     * Test of trim2 method, of class StringUtils.
     */
    @Test
    public void testTrim2() {
        System.out.println("trim2");
        String value;
        String expResult;
        String result;

        value = " ";
        expResult = "";
        result = StringUtils.trim2(value);
        assertEquals(expResult, result);

        value = " " + " " + " " + " " + " ";
        expResult = "";
        result = StringUtils.trim2(value);
        assertEquals(expResult, result);

        value = "　";
        expResult = "";
        result = StringUtils.trim2(value);
        assertEquals(expResult, result);

        value = "　" + "　" + "　" + "　" + "　";
        expResult = "";
        result = StringUtils.trim2(value);
        assertEquals(expResult, result);

        value = "　" + " " + "　" + "　" + " ";
        expResult = "";
        result = StringUtils.trim2(value);
        assertEquals(expResult, result);

        value = "ab c　de";
        expResult = "ab c　de";
        result = StringUtils.trim2(value);
        assertEquals(expResult, result);

        value = "  ab c　de     ";
        expResult = "ab c　de";
        result = StringUtils.trim2(value);
        assertEquals(expResult, result);

        value = "あい う　えお";
        expResult = "あい う　えお";
        result = StringUtils.trim2(value);
        assertEquals(expResult, result);

        value = " 　 あい う　えお  　　";
        expResult = "あい う　えお";
        result = StringUtils.trim2(value);
        assertEquals(expResult, result);
    }
    

    @Test
    public void testEscapeLike() {
        String s = StringUtils.escapeLike(".*MODEL.*");
        assertEquals("%MODEL%", s);

        s = StringUtils.escapeLike(".*MO_DEL.*");
        assertEquals("%MO\\_DEL%", s);

        s = StringUtils.escapeLike("MODEL");
        assertEquals("MODEL", s);

        s = StringUtils.escapeLike("MODEL%");
        assertEquals("MODEL\\%", s);
    }

    @Test
    public void testLike() {
        boolean result = StringUtils.like("MODEL_001", "MODEL.*");
        assertEquals(result, true);

        result = StringUtils.like("MODEL_001", "_001.*");
        assertEquals(result, false);

        result = StringUtils.like("MODEL_001", ".*_001");
        assertEquals(result, true);

        result = StringUtils.like("MODEL_001", ".*MODEL");
        assertEquals(result, false);

        result = StringUtils.like("MODEL_001", "MOD.*001");
        assertEquals(result, false);

        result = StringUtils.like("MOD.*001", "MOD.*001");
        assertEquals(result, true);

        result = StringUtils.like("MODEL_001", ".*DEL.*");
        assertEquals(result, true);

        result = StringUtils.like("MODEL_001", "MODEL_001");
        assertEquals(result, true);

        result = StringUtils.like("MODEL_001", "MODEL");
        assertEquals(result, false);

        // 進捗モニタ設定のモデル名が空白またはnullなら常に一致
        result = StringUtils.like("MODEL_001", "");
        assertEquals(result, true);

        result = StringUtils.like("MODEL_001", null);
        assertEquals(result, true);

        result = StringUtils.like("", "MODEL");
        assertEquals(result, false);

        result = StringUtils.like("", "");
        assertEquals(result, true);

        result = StringUtils.like("", ".*");
        assertEquals(result, true);

        result = StringUtils.like(null, "MODEL");
        assertEquals(result, false);
    }
}
