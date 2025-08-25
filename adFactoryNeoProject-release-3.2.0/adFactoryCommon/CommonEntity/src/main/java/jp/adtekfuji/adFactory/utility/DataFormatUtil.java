/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.utility;

import javafx.scene.input.DataFormat;

/**
 *
 * @author s-maeda
 */
public class DataFormatUtil {
    
    public static DataFormat getDataFormat(Class dataClass) {
        DataFormat dataFormat = DataFormat.lookupMimeType(dataClass.getName());
        if (dataFormat == null) {
            dataFormat = new DataFormat(dataClass.getName());
        }
        return dataFormat;
    }
}
