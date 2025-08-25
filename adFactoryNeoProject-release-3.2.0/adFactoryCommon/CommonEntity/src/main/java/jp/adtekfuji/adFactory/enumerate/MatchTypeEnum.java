/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import jakarta.xml.bind.annotation.XmlEnum;

/**
 * 比較種類
 *
 * @author nar-nakamura
 */
@XmlEnum(String.class)
public enum MatchTypeEnum {
    MATCH,
    LIKE,
    NOT;
}
