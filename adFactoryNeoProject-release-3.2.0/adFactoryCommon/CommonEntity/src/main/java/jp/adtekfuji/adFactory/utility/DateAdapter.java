/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author s-heya
 */
public class DateAdapter extends XmlAdapter<String, Date> {
    DateFormat f = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

    @Override
    public Date unmarshal(String value) throws Exception {
        return f.parse(value);
    }

    @Override
    public String marshal(Date value) throws Exception {
        return f.format(value);
    }
}