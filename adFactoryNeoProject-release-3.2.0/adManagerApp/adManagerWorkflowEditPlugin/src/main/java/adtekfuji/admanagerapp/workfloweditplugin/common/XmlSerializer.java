/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.common;

import java.io.ByteArrayOutputStream;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * シリアライズユーティリティ
 *
 * @author nar-nakamura
 */
public class XmlSerializer {
    /**
     * オブジェクトからXMLに書き込む。
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static String serialize(Object obj)  throws Exception {
        String xml = null;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Persister serializer = new Persister();
            serializer.write(obj, os, "MS932");
            xml = os.toString("MS932");
        }
        return xml;
    }

    /**
     *  XMLからオブジェクトを読む込む。
     *
     * @param cls
     * @param xml
     * @throws Exception
     * @return
     */
    public static Object deserialize(Class cls, String xml) throws Exception {
        Serializer serializer = new Persister();
        return serializer.read(cls, xml);
    }
}
