package jp.adtekfuji.adfactoryserver.service;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class LedgerEntityFacadeRESTTest {


    @Test
    public void test() throws Exception {

        Pattern pattern = LedgerEntityFacadeREST.tagPattern;
        String test1 = "TAG_AAAA";
        String test2 = "TAG_BBBB(123)";
        String test3 = "TAG_CCCC=123";
        String test4 = "TAG_DDDD(123)=456";
        String test5 = "TAG_EEEE$123";
        String test6 = "TAG_FFFF(123)$456";
        String test7 = "TAG_GGGG(123)=456$789";
        String test8 = "TAG_HHHH(123)$456=789";


        Matcher matcher1 = pattern.matcher(test1);
        assertTrue(matcher1.find());
        assertEquals("TAG_AAAA", matcher1.group("format"));
        assertEquals("TAG_AAAA", matcher1.group("tag"));
        assertNull(matcher1.group("predicate"));
        assertNull(matcher1.group("value"));
        assertNull(matcher1.group("index"));

        Matcher matcher2 = pattern.matcher(test2);
        assertTrue(matcher2.find());
        assertEquals("TAG_BBBB(123)", matcher2.group("format"));
        assertEquals("TAG_BBBB", matcher2.group("tag"));
        assertEquals("123", matcher2.group("predicate"));
        assertNull(matcher2.group("value"));
        assertNull(matcher2.group("index"));

        Matcher matcher3 = pattern.matcher(test3);
        assertTrue(matcher3.find());
        assertEquals("TAG_CCCC", matcher3.group("format"));
        assertEquals("TAG_CCCC", matcher3.group("tag"));
        assertNull(matcher3.group("predicate"));
        assertEquals("123", matcher3.group("value"));
        assertNull(matcher3.group("index"));

        Matcher matcher4 = pattern.matcher(test4);
        assertTrue(matcher4.find());
        assertEquals("TAG_DDDD(123)", matcher4.group("format"));
        assertEquals("TAG_DDDD", matcher4.group("tag"));
        assertEquals("123", matcher4.group("predicate"));
        assertEquals("456", matcher4.group("value"));
        assertNull(matcher4.group("index"));

        Matcher matcher5 = pattern.matcher(test5);
        assertTrue(matcher5.find());
        assertEquals("TAG_EEEE", matcher5.group("format"));
        assertEquals("TAG_EEEE", matcher5.group("tag"));
        assertNull(matcher5.group("value"));
        assertEquals("123", matcher5.group("index"));


        Matcher matcher6 = pattern.matcher(test6);
        assertTrue(matcher6.find());
        assertEquals("TAG_FFFF(123)", matcher6.group("format"));
        assertEquals("TAG_FFFF", matcher6.group("tag"));
        assertEquals("123", matcher6.group("predicate"));
        assertNull(matcher6.group("value"));
        assertEquals("456", matcher6.group("index"));

        Matcher matcher7 = pattern.matcher(test7);
        assertTrue(matcher7.find());
        assertEquals("TAG_GGGG(123)", matcher7.group("format"));
        assertEquals("TAG_GGGG", matcher7.group("tag"));
        assertEquals("123", matcher7.group("predicate"));
        assertEquals("456", matcher7.group("value"));
        assertEquals("789", matcher7.group("index"));

        Matcher matcher8 = pattern.matcher(test8);
        assertFalse(matcher8.find());
    }

}
