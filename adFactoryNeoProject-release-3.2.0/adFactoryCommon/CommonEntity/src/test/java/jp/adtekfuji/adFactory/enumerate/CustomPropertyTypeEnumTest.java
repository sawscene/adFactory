package jp.adtekfuji.adFactory.enumerate;

import org.junit.Test;

import static org.junit.Assert.*;

public class CustomPropertyTypeEnumTest {

    @Test
    public void toEnum() {
        assertEquals(CustomPropertyTypeEnum.TYPE_STRING,CustomPropertyTypeEnum.toEnum("TYPE_STRING"));
        assertEquals(CustomPropertyTypeEnum.TYPE_BOOLEAN,CustomPropertyTypeEnum.toEnum("TYPE_BOOLEAN"));
        assertEquals(CustomPropertyTypeEnum.TYPE_INTEGER,CustomPropertyTypeEnum.toEnum("TYPE_INTEGER"));
        assertEquals(CustomPropertyTypeEnum.TYPE_NUMERIC,CustomPropertyTypeEnum.toEnum("TYPE_NUMERIC"));
        assertEquals(CustomPropertyTypeEnum.TYPE_DATE,CustomPropertyTypeEnum.toEnum("TYPE_DATE"));
        assertEquals(CustomPropertyTypeEnum.TYPE_IP4_ADDRESS,CustomPropertyTypeEnum.toEnum("TYPE_IP4_ADDRESS"));
        assertEquals(CustomPropertyTypeEnum.TYPE_MAC_ADDRESS,CustomPropertyTypeEnum.toEnum("TYPE_MAC_ADDRESS"));
        assertEquals(CustomPropertyTypeEnum.TYPE_PLUGIN,CustomPropertyTypeEnum.toEnum("TYPE_PLUGIN"));
        assertEquals(CustomPropertyTypeEnum.TYPE_IP4_ADDRESS,CustomPropertyTypeEnum.toEnum("TYPE_IP4_ADDRESS"));
        assertEquals(CustomPropertyTypeEnum.TYPE_TRACE,CustomPropertyTypeEnum.toEnum("TYPE_TRACE"));
        assertEquals(CustomPropertyTypeEnum.TYPE_DEFECT,CustomPropertyTypeEnum.toEnum("TYPE_DEFECT"));
        assertNull(CustomPropertyTypeEnum.toEnum("hogehoge"));
        assertNull(CustomPropertyTypeEnum.toEnum(null));

    }
}