/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbanregistpreprocessplugin.common;

import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 *
 * @author e-mori 表示カテゴリー定義.
 */
public enum CsvTypeEnum {

    csv(','),
    tsv('\t');

    private final Character separateKey;

    private CsvTypeEnum(Character separateKey) {
        this.separateKey = separateKey;
    }

    public Character getSeparateKey() {
        return separateKey;
    }
    
    public static Character getValueText(int idx) {
        Character value = null;

        // 列挙型を中身の並び順に取得する
        CsvTypeEnum[] enumArray = CsvTypeEnum.values();
        // 引数の数値が並び順のMAX数より大きいか判定
        if (enumArray.length > idx) {
            value = CsvTypeEnum.values()[idx].getSeparateKey();
        }

        return value;
    }

    public static CsvTypeEnum getEnum(Character str) {
        CsvTypeEnum[] enumArray = CsvTypeEnum.values();
        for (CsvTypeEnum enumStr : enumArray) {
            if (str.equals(enumStr.getSeparateKey())) {
                return enumStr;
            }
        }
        return null;
    }
    
    public static CsvTypeEnum getEnum(String str) {
        CsvTypeEnum[] enumArray = CsvTypeEnum.values();
        for (CsvTypeEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }
}
