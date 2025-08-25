@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(type = LocalDateTime.class, value = LocalDateTimeXmlAdapter.class),
    @XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateXmlAdapter.class),
    @XmlJavaTypeAdapter(type = LocalTime.class, value = LocalTimeXmlAdapter.class),
    @XmlJavaTypeAdapter(type = Color.class, value = ColorXmlAdapter.class)
})
package jp.adtekfuji.andon.property;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.scene.paint.Color;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import jp.adtekfuji.adFactory.xmladapter.ColorXmlAdapter;
import jp.adtekfuji.adFactory.xmladapter.LocalDateTimeXmlAdapter;
import jp.adtekfuji.adFactory.xmladapter.LocalDateXmlAdapter;
import jp.adtekfuji.adFactory.xmladapter.LocalTimeXmlAdapter;
