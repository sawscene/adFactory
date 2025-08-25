/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.xmladapter;

import javafx.scene.paint.Color;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author ke.yokoi
 */
public class ColorXmlAdapter extends XmlAdapter<String, Color> {

    @Override
    public Color unmarshal(String value) throws Exception {
        return Color.valueOf(value);
    }

    @Override
    public String marshal(Color color) throws Exception {
        return color.toString();
    }

}
