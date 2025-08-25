/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jp.adtekfuji.adfactoryserver.utility;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.postgresql.util.PGobject;

/**
 * JSONBに対応したデータに変換する。
 * 
 * @author s-heya
 */
@Converter
public class PgJsonbConverter implements AttributeConverter<String, PGobject>{

    /**
     * String から PGobject へ変換する。
     * 
     * @param str String
     * @return 
     */
    @Override
    public PGobject convertToDatabaseColumn(String str) {
        PGobject pgobject = new PGobject();
        pgobject.setType("jsonb");
        try {
            pgobject.setValue(str);
        } catch (SQLException ex) {
            Logger.getLogger(PgJsonbConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        return pgobject;
    }

    /**
     * PGobject から String へ変換する。
     * 
     * @param obj PGobject
     * @return 
     */
    @Override
    public String convertToEntityAttribute(PGobject obj) {
        return Objects.nonNull(obj) ? obj.getValue() : null;
    }

}
