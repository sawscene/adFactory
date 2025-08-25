/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.warehouseservicetesttool.entity;

/**
 * BHT通信コマンド
 * @author nar-nakamura
 */
public class BhtCommand {
    public static final String BHT_STX = "\u0002";
    public static final String BHT_ETX = "\u0003";
    
    public static final String BHT_UPLOAD = "UPLOAD";
    public static final String BHT_DWNLOAD = "DWNLOAD";
    public static final String BHT_RESULT = "RESULT";
    public static final String BHT_RESPONSE = "RESPONSE";
    public static final String BHT_MANAGER = "MANAGER";
    
    public static final String BHT_HTCONF_MST = "HTCONF.MST";
    public static final String BHT_HTMENU_MST = "HTMENU.MST";
    public static final String BHT_WORKER_MST = "WORKER.MST";
    public static final String BHT_ACCEPT_MST = "ACCEPT.MST";
    public static final String BHT_STOCK_MST = "STOCK.MST";
    public static final String BHT_SHIPMENT_MST = "SHIPMENT.MST";
    public static final String BHT_SHIPPRT_MST = "SHIPPRT.MST";// 払出ラベル再発行
    public static final String BHT_STKTAKE_MST = "STKTAKE.MST";
    public static final String BHT_RACKMOVE_MST = "RACKMOVE.MST";
    public static final String BHT_PRINT_MST = "PRINT.MST";
    public static final String BHT_AFFILI_MST = "AFFILI.MST";// 部品所属
    
    public static final String BHT_ACCEPT_DAT = "ACCEPT.DAT";
    public static final String BHT_STOCK_DAT = "STOCK.DAT";
    public static final String BHT_SHIPMENT_DAT = "SHIPMENT.DAT";
    public static final String BHT_SHIPPRT_DAT = "SHIPPRT.DAT";// 払出ラベル再発行
    public static final String BHT_STKTAKE_DAT = "STKTAKE.DAT";
    public static final String BHT_RACKMOVE_DAT = "RACKMOVE.DAT";
    public static final String BHT_PRINT_DAT = "PRINT.DAT";
    public static final String BHT_WORKER_DAT = "WORKER.DAT";
    
    public static final String BHT_STKTAKE_RES = "STKTAKE.RES";
    public static final String BHT_RACKMOVE_RES = "RACKMOVE.RES";
    
    private String typeCode;
    private String serialNumber;
    private String ipAddress;
    private String modelCode;
    private String data1;
    private String data2;
    private String data3;
    private String data4;
    
    public BhtCommand() {
        
    }
    
    /**
     * BHT通信コマンド
     * @param typeCode
     * @param serialNumber
     * @param ipAddress
     * @param modelCode
     * @param data1
     * @param data2
     * @param data3
     * @param data4 
     */
    public BhtCommand(
            String typeCode, String serialNumber, String ipAddress, String modelCode,
            String data1, String data2, String data3, String data4) {
        this.typeCode = typeCode;
        this.serialNumber = serialNumber;
        this.ipAddress = ipAddress;
        this.modelCode = modelCode;
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
        this.data4 = data4;
    }
    
    /**
     * 種別コード (10)
     * @return 
     */
    public String getTypeCode() {
        return this.typeCode;
    }
    /**
     * 種別コード (10)
     * @param typeCode 
     */
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
    
    /**
     * 製品シリアル番号 (6)
     * @return 
     */
    public String getSerialNumber() {
        return this.serialNumber;
    }
    /**
     * 製品シリアル番号 (6)
     * @param serialNumber 
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    /**
     * IPアドレス (15)
     * @return 
     */
    public String getIpAddress() {
        return this.ipAddress;
    }
    /**
     * IPアドレス (15)
     * @param ipAddress 
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    /**
     * 機種コード (10)
     * @return 
     */
    public String getModelCode() {
        return this.modelCode;
    }
    /**
     * 機種コード (10)
     * @param modelCode 
     */
    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }
    
    /**
     * データ1 (20)
     * @return 
     */
    public String getData1() {
        return this.data1;
    }
    /**
     * データ1 (20)
     * @param data1 
     */
    public void setData1(String data1) {
        this.data1 = data1;
    }
    
    /**
     * データ2 (20)
     * @return 
     */
    public String getData2() {
        return this.data2;
    }
    /**
     * データ2 (20)
     * @param data2 
     */
    public void setData2(String data2) {
        this.data2 = data2;
    }
    
    /**
     * データ3 (30)
     * @return 
     */
    public String getData3() {
        return this.data3;
    }
    /**
     * データ3 (30)
     * @param data3 
     */
    public void setData3(String data3) {
        this.data3 = data3;
    }
    
    /**
     * データ4 (1275)   ※.UPLOAD のみ
     * @return 
     */
    public String getData4() {
        return this.data4;
    }
    /**
     * データ4 (1275)   ※.UPLOAD のみ
     * @param data4 
     */
    public void setData4(String data4) {
        this.data4 = data4;
    }
    
    @Override
    public String toString() {
        return "BhtCommand{" +
                "typeCode=" + this.typeCode +
                ", serialNumber=" + this.serialNumber +
                ", ipAddress=" + this.ipAddress +
                ", modelCode=" + this.modelCode +
                ", data1=" + this.data1 +
                ", data2=" + this.data2 +
                ", data3=" + this.data3 +
                ", data4=" + this.data4 +
                "}";
    }    
}
