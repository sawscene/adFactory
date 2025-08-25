/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity;

import java.util.Map;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 *
 * @author s-heya
 */
class MapElements {
    @XmlAttribute
    public String key;
    @XmlAttribute
    public String value;

    private MapElements() {
    } //Required by JAXB

    public MapElements(String key, String value) {
        this.key = key;
        this.value = value;
    }
}

/**
 *
 * @author s-heya
 */
public class MapAdapter extends XmlAdapter<String, Map<String, String>> {
    public MapAdapter() {
    }

    @Override
    public String marshal(Map<String, String> map) throws Exception {
        return JsonUtils.mapToJson(map);
    }

    @Override
    public Map<String, String> unmarshal(String josn) throws Exception {
        return JsonUtils.jsonToMap(josn);
    }
}