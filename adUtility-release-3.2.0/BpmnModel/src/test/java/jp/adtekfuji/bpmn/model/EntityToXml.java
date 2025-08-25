/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/**
 *
 * @author ke.yokoi
 */
public class EntityToXml {

    public final static String CR = System.getProperty("line.separator");

    private EntityToXml() {
    }

    public static <T> Document getXml(T t) throws Exception {
        //entityをxmlに変換.
        JAXBContext jc = JAXBContext.newInstance(t.getClass());
        Marshaller mu = jc.createMarshaller();
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        mu.marshal(t, outstream);
        //Documentに出力.
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
        ByteArrayInputStream instream = new ByteArrayInputStream(outstream.toByteArray());
        Document xml = docbuilder.parse(instream);
        //コンソール表示.
        byte b[] = outstream.toByteArray();
        for (int x = 0; x < b.length; x++) {
            System.out.print((char) b[x]);
        }
        System.out.print(CR);
        return xml;
    }

}
