/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.entity;

import adtekfuji.utility.StringUtils;
import java.util.Map;

/**
 * デバイス接続方法情報
 * 
 * @author okada
 */
public class DeviceConnectionEntity implements Cloneable {
    
    // -- デバイス接続方法情報のキー --
    // 情報はMap<String, String>型で定義
    // 有効フラグ 
    private final String ENABLE = "enable";
    // 装置名 
    private final String EQUIPMENT_IDENTIFY = "equipmentIdentify";
    // 仮想作業者名 
    private final String ORGANIZATION_IDENTIFY = "organizationIdentify";
    // ログインパスワード 
    private final String PASSWORD = "password";
    // デバイスタイプ 
    private final String DEVICE_TYPE = "deviceType";
    // 接続タイプ 
    private final String CONNECT_TYPE = "connectType";
    private final String MACHINE_NAME = "machineName";
    // プログラム信号名
    private final String PROGRAM_SIGNAL_NAME = "programSignalName";
    // ワーク数信号名
    private final String WORK_NUMBER_SIGNAL_NAME = "workNumberSignalName";
    // ステータス信号名
    private final String STATUS_SIGNAL_NAME = "statusSignalName";
    // カウンター信号名
    private final String COUNTER_SIGNAL_NAME = "counterSignalName";
    // 開始コマンドタイプ
    private final String START_COMMAND_TYPE = "startCommandType";

    // -- プロパティ --
    // 有効フラグ
    private boolean enable;
    // 装置名
    private String equipmentIdentify;
    // 仮想作業者名
    private String organizationIdentify;
    // ログインパスワード
    private String password;
    // デバイスタイプ
    private String deviceType;
    // 接続タイプ
    private String connectType;
    // 装置名
    private String machineName;
    // プログラム信号名
    private String programSignalName;
    // ワーク数信号名
    private String workNumberSignalName;
    // ステータス信号名
    private String statusSignalName;
    // カウンタ信号名
    private String counterSignalName;
    // 開始コマンド種
    private String startCommandType;

    /**
     * 有効フラグの取得
     *
     * @return the value of enable
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * 有効フラグをセット
     *
     * @param enable new value of enable
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * 装置名の取得
     *
     * @return the value of equipmentIdentify
     */
    public String getEquipmentIdentify() {
        return equipmentIdentify;
    }

    /**
     * 装置名をセット
     *
     * @param equipmentIdentify new value of equipmentIdentify
     */
    public void setEquipmentIdentify(String equipmentIdentify) {
        this.equipmentIdentify = equipmentIdentify;
    }

    /**
     * 仮想作業者名の取得
     *
     * @return the value of organizationIdentify
     */
    public String getOrganizationIdentify() {
        return organizationIdentify;
    }

    /**
     * 仮想作業者名をセット
     *
     * @param organizationIdentify new value of organizationIdentify
     */
    public void setOrganizationIdentify(String organizationIdentify) {
        this.organizationIdentify = organizationIdentify;
    }

    /**
     * ログインパスワードの取得
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * ログインパスワードをセット
     *
     * @param password new value of password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * デバイスタイプの取得
     *
     * @return the value of deviceType
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * デバイスタイプをセット
     *
     * @param deviceType new value of deviceType
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * 接続タイプの取得
     *
     * @return the value of connectType
     */
    public String getConnectType() {
        return connectType;
    }

    /**
     * 接続タイプをセット
     *
     * @param connectType new value of connectType
     */
    public void setConnectType(String connectType) {
        this.connectType = connectType;
    }


    /**
     * 装置名を取得
     * @return 装置名
     */
    public String getMachineName() {
        return machineName;
    }

    /**
     * 装置名を設定
     * @param machineName 装置名
     */
    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    /**
     * 開始コマンド種 取得
     * @return 開始コマンド種
     */
    public String getStartCommandType() {
        return startCommandType;
    }

    /**
     * 開始コマンド種
     * @param startCommandType
     */
    public void setStartCommandType(String startCommandType) {
        this.startCommandType = startCommandType;
    }

    /**
     * プログラム信号名取得
     * @return
     */
    public String getProgramSignalName() {
        return programSignalName;
    }

    /**
     * プログラム信号名設定
     * @param programSignalName プログラム信号名
     */
    public void setProgramSignalName(String programSignalName) {
        this.programSignalName = programSignalName;
    }

    /**
     * ワーク数信号名設定
     * @return ワーク数信号名設定
     */
    public String getWorkNumberSignalName() {
        return workNumberSignalName;
    }

    /**
     * ワーク数信号名種とy九
     * @return ワーク数信号名
     */
    public void setWorkNumberSignalName(String workNumberSignalName) {
        this.workNumberSignalName = workNumberSignalName;
    }

    /**
     * ステータス信号名
     * @return ステータス信号名
     */
    public String getStatusSignalName() {
        return statusSignalName;
    }

    /**
     * ステータス信号名設定
     * @param statusSignalName ステータス信号名
     */
    public void setStatusSignalName(String statusSignalName) {
        this.statusSignalName = statusSignalName;
    }

    /**
     * カウンタ信号名取得
     * @return カウンタ信号名
     */
    public String getCounterSignalName() {
        return counterSignalName;
    }

    /**
     * カウンタ信号名設定
     * @param counterSignalName カウンタ信号名
     */
    public void setCounterSignalName(String counterSignalName) {
        this.counterSignalName = counterSignalName;
    }


    // -- コンストラクタ --
    /**
     * コンストラクタ
     * 
     * @param enable 有効フラグ
     * @param equipmentIdentify 装置名
     * @param organizationIdentify 仮想作業者名
     * @param password ログインパスワード
     * @param deviceType デバイスタイプ
     * @param connectType 接続タイプ
     */
    public DeviceConnectionEntity(boolean enable, String equipmentIdentify, String organizationIdentify, String password, String deviceType, String connectType, String machineName, String programSignalName, String statusSignalName, String counterSignalName, String startCommandType) {
        this.enable = enable;
        this.equipmentIdentify = equipmentIdentify;
        this.organizationIdentify = organizationIdentify;
        this.password = password;
        this.deviceType = deviceType;
        this.connectType = connectType;
        this.machineName = machineName;
        this.programSignalName = programSignalName;
        this.statusSignalName = statusSignalName;
        this.counterSignalName = counterSignalName;
        this.startCommandType = startCommandType;
    }

    /**
     * コンストラクタ
     */
    public DeviceConnectionEntity() {
        this(false,"","","","","","","","", "", "");
    }

    /**
     * コンストラクタ
     * @param info デバイス接続方法情報
     */
    public DeviceConnectionEntity(Map<String, String> info) {
        this();
        this.setDeviceConnectionInfo(info);
    }

    // -- パブリックメソッド --
    /**
     * デバイス接続方法情報をセット
     * 
     * @param info デバイス接続方法情報
     */
    public void setDeviceConnectionInfo(Map<String, String> info) {
        this.enable = chkBooleanNull(info.get(ENABLE));
        this.equipmentIdentify = chkStringNull(info.get(EQUIPMENT_IDENTIFY));
        this.organizationIdentify = chkStringNull(info.get(ORGANIZATION_IDENTIFY));
        this.password = chkStringNull(info.get(PASSWORD));
        this.deviceType = chkStringNull(info.get(DEVICE_TYPE));
        this.connectType = chkStringNull(info.get(CONNECT_TYPE));
        this.machineName = info.get(MACHINE_NAME);
        this.workNumberSignalName = chkStringNull(info.get(WORK_NUMBER_SIGNAL_NAME));
        this.programSignalName = chkStringNull(info.get(PROGRAM_SIGNAL_NAME));
        this.statusSignalName = chkStringNull(info.get(STATUS_SIGNAL_NAME));
        this.counterSignalName = chkStringNull(info.get(COUNTER_SIGNAL_NAME));
        this.startCommandType = info.get(START_COMMAND_TYPE);
    }
    
    /**
     * クローン作成
     * 
     * @return クローンエンティティ
     * @throws CloneNotSupportedException 
     */
    @Override
    public DeviceConnectionEntity clone() throws CloneNotSupportedException {
        return (DeviceConnectionEntity) super.clone();
    }
    
    /**
     * 必須チェック
     * 
     * @return true:チェックＯＫ
     */
    public boolean checkRequired(){
        return !(StringUtils.isEmpty(this.equipmentIdentify)
                || StringUtils.isEmpty(this.organizationIdentify)
                || StringUtils.isEmpty(this.deviceType));
    }

    /**
     * 付与情報取得
     * 
     * @return 付与情報
     */
    public String getAddInfo(){
        return String.format("【設備管理名】%s【組織識別名】%s",
                this.getEquipmentIdentify(),
                this.getOrganizationIdentify()
                );
    }
    // -- プライベートメソッド --
    /**
     * String型のNULLチェック
     * 
     * @param str チェック値
     * @return NULLの場合はブランクを、それ以外の場合はチェック値を返す
     */
    private String chkStringNull(String str) {
        if(str == null) {
            return "";
        }else {
            return str;
        }
    }
    
    /**
     * String型のNULLチェック
     * 
     * @param str チェック値
     * @return NULLの場合はfalseを
     */
    private boolean chkBooleanNull(String str) {
        return Boolean.parseBoolean(str);
    }
    
}
