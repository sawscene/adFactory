/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

/**
 *
 * @author s-heya
 */
public class DateConverter implements Converter<Date> {
    private final static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.JAPAN);
    
    @Override
    public Date read(InputNode node) throws Exception {
        String value = node.getValue();
        return formatter.parse(value);
    }

    @Override
    public void write(OutputNode node, Date date) throws Exception {
        node.setValue(this.formatter.format(date));
    }
}
