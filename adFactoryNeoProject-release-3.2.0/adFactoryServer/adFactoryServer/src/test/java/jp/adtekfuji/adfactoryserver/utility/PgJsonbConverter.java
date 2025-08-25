/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jp.adtekfuji.adfactoryserver.utility;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JSONBに対応したデータに変換する。(ユニットテスト用のスタブ)
 * 
 * @author s-heya
 */
@Converter
public class PgJsonbConverter implements AttributeConverter<String, String>{

    /**
     * String から PGobject へ変換する。
     * 
     * @param str String
     * @return 
     */
    @Override
    public String convertToDatabaseColumn(String str) {
        return str;
    }

    /**
     * PGobject から String へ変換する。
     * 
     * @param obj PGobject
     * @return 
     */
    @Override
    public String convertToEntityAttribute(String obj) {
        return obj;
    }

}
