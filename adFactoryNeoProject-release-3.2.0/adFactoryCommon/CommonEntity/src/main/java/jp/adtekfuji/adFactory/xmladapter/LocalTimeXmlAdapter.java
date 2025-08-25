/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.xmladapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author ke.yokoi
 */
public class LocalTimeXmlAdapter extends XmlAdapter<XMLGregorianCalendar, LocalTime> {

    @Override
    public LocalTime unmarshal(XMLGregorianCalendar value) throws Exception {
        int timezone = value.getTimezone();
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezone * 60);
        return OffsetTime.of(value.getHour(), value.getMinute(),
                value.getSecond(), value.getMillisecond() * 1_000_000, offset)
                .toLocalTime();
    }

    @Override
    public XMLGregorianCalendar marshal(LocalTime value) throws Exception {
        ZoneOffset offset = OffsetDateTime.now().getOffset();
        int timezone = offset.getTotalSeconds() / 60;
        return DatatypeFactory.newInstance().newXMLGregorianCalendarTime(
                value.getHour(), value.getMinute(), value.getSecond(), value.getNano() / 1_000_000, timezone);
    }

}
