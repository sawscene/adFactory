/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import jakarta.xml.bind.annotation.XmlEnum;

/**
 * データ形式
 * 
 * @author s-heya
 */
@XmlEnum(String.class)
public enum DataTypeEnum {
    XML,
    JSON,
    CSV;
}
