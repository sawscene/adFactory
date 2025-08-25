/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.xmladapter;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Number型のJAXBアダプター
 *
 * @author s-heya
 */
public class NumberAdapter extends XmlAdapter<String, Number> {

    /**
     * データ非整列化 (XML→JAVA)
     *
     * @param value
     * @return
     * @throws Exception
     */
    @Override
    public Number unmarshal(String value) throws Exception {
        return new BigDecimal(value);
    }

    /**
     * データ整列化 (JAVA→XML)
     *
     * @param value
     * @return
     * @throws Exception
     */
    @Override
    public String marshal(Number value) throws Exception {
        return value.toString();
    }

}
