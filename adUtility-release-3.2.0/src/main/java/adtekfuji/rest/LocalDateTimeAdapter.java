/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.rest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * LocalDateTime のシリアライズ／デシリアライズ
 * 
 * @author s-heya
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public LocalDateTime unmarshal(String value) throws Exception {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    @Override
    public String marshal(LocalDateTime localDate) throws Exception {
        return DateTimeFormatter.ISO_ZONED_DATE_TIME.format(localDate);
    }
}