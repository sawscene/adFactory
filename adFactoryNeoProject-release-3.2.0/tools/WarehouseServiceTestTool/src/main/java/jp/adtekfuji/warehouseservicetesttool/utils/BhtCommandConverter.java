/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.warehouseservicetesttool.utils;

import jp.adtekfuji.warehouseservicetesttool.entity.BhtCommand;

/**
 *
 * @author nar-nakamura
 */
public class BhtCommandConverter {
    
//<editor-fold defaultstate="collapsed" desc="BHTコマンド変換">
    /**
     * BHTコマンド文字列をBHTコマンドクラスに変換
     * @param bhtCommandString
     * @return
     */
    public static BhtCommand BhtCommandEncoder(String bhtCommandString) {
        String typeCode = FileUtility.getBytesString(bhtCommandString, 0, 10).trim();//bhtCommandString.substring(0, 10).trim();
        String serialNumber = FileUtility.getBytesString(bhtCommandString, 10, 6).trim();//bhtCommandString.substring(10, 16).trim();
        String ipAddress = FileUtility.getBytesString(bhtCommandString, 16, 15).trim();//bhtCommandString.substring(16, 31).trim();
        String modelCode = FileUtility.getBytesString(bhtCommandString, 31, 10).trim();//bhtCommandString.substring(31, 41).trim();
        String data1 = FileUtility.getBytesString(bhtCommandString, 41, 20).trim();//bhtCommandString.substring(41, 61).trim();
        String data2 = FileUtility.getBytesString(bhtCommandString, 61, 20).trim();//bhtCommandString.substring(61, 81).trim();
        String data3 = FileUtility.getBytesString(bhtCommandString, 81, 30).trim();//bhtCommandString.substring(81, 111).trim();
        String data4 = "";
        if (typeCode.equals(BhtCommand.BHT_UPLOAD)) {
            data4 = FileUtility.getBytesString(bhtCommandString, 111, 1275).trim();//bhtCommandString.substring(111, 1386).trim();
        }
        
        return new BhtCommand(typeCode, serialNumber, ipAddress, modelCode,
                data1, data2, data3, data4);
    }
    
    /**
     * BHTコマンドクラスをBHTコマンド文字列に変換
     * @param bhtCommand
     * @return
     */
    public static String BhtCommandDecoder(BhtCommand bhtCommand) {
        String bhtCommandString;
        String typeCode = bhtCommand.getTypeCode();
        if (typeCode.equals(BhtCommand.BHT_UPLOAD)) {
            bhtCommandString =
                    FileUtility.padBytesString(typeCode, 10) +
                    FileUtility.padBytesString(bhtCommand.getSerialNumber(), 6) +
                    FileUtility.padBytesString(bhtCommand.getIpAddress(), 15) +
                    FileUtility.padBytesString(bhtCommand.getModelCode(), 10) +
                    FileUtility.padBytesString(bhtCommand.getData1(), 20) +
                    FileUtility.padBytesString(bhtCommand.getData2(), 20) +
                    FileUtility.padBytesString(bhtCommand.getData3(), 30) +
                    FileUtility.padBytesString(bhtCommand.getData4(), 1275);
        }
        else {
            bhtCommandString =
                    FileUtility.padBytesString(typeCode, 10) +
                    FileUtility.padBytesString(bhtCommand.getSerialNumber(), 6) +
                    FileUtility.padBytesString(bhtCommand.getIpAddress(), 15) +
                    FileUtility.padBytesString(bhtCommand.getModelCode(), 10) +
                    FileUtility.padBytesString(bhtCommand.getData1(), 20) +
                    FileUtility.padBytesString(bhtCommand.getData2(), 20) +
                    FileUtility.padBytesString(bhtCommand.getData3(), 30);
        }
        
        return bhtCommandString;
    }
//</editor-fold>
}
