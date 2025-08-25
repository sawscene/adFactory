/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabase.common;

import java.io.File;
import java.util.Comparator;
import java.util.Objects;

/**
 * Fileをファイル名順にソートするためのコンパレータ
 *
 * @author nar-nakamura
 */
public class FileNameComparator implements Comparator<File> {

    /**
     * ファイル名順に並び替える。
     *
     * @param dat1
     * @param dat2
     * @return 
     */
    @Override
    public int compare(File dat1, File dat2) {
        if (Objects.isNull(dat1) && Objects.isNull(dat2)) {
            return 0;
        } else if (Objects.isNull(dat1)) {
            return 1;
        } else if (Objects.isNull(dat2)) {
            return -1;
        } else if (dat1.getName().isEmpty() && dat2.getName().isEmpty()) {
            return 0;
        } else if (dat1.getName().isEmpty()) {
            return 1;
        } else if (dat2.getName().isEmpty()) {
            return -1;
        }
        return dat1.getName().compareTo(dat2.getName());
    }
}
