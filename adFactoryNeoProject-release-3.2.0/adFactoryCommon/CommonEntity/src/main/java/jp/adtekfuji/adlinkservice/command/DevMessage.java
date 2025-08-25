/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * メッセージ
 * 
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "devMessage")
@JsonIgnoreProperties(ignoreUnknown=true)
public class DevMessage  implements Serializable {

    private static final long serialVersionUID = 1L;
  
    @XmlElement()
    private String plugin;
    
    @XmlElement()
    private String cmd;

    @XmlElement()
    private String equipIdent;

    @XmlElement()
    private String value;

    /**
     * コンストラクタ
     * 
     * @param plugin プラグイン名
     * @param value 値
     */
    public DevMessage(String plugin, String value) {
        this.plugin = plugin;
        this.cmd = "MESSAGE";
        this.value = value;
    }

    /**
     * コンストラクタ
     * 
     * @param plugin プラグイン名
     * @param cmd コマンド
     * @param value 値
     */
    public DevMessage(String plugin, String cmd, String value) {
        this.plugin = plugin;
        this.cmd = cmd;
        this.value = value;
    }

    /**
     * コンストラクタ
     * 
     * @param plugin プラグイン名
     * @param cmd コマンド
     * @param equipIdent 設備管理名
     * @param value 値
     */
    public DevMessage(String plugin, String cmd, String equipIdent, String value) {
        this.plugin = plugin;
        this.cmd = cmd;
        this.equipIdent = equipIdent;
        this.value = value;
    }
    
    /**
     * プラグイン名を取得する。
     * 
     * @return プラグイン名
     */
    public String getPlugin() {
        return plugin;
    }

    /**
     * プラグイン名を設定する。
     * 
     * @param plugin プラグイン名
     */
    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    /**
     * コマンドを取得する。
     * 
     * @return コマンド 
     */
    public String getCmd() {
        return cmd;
    }

    /**
     * コマンドを設定する。
     * 
     * @param cmd コマンド 
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * 設備管理名を取得する。
     * 
     * @return 設備管理名
     */
    public String getEquipIdent() {
        return equipIdent;
    }

    /**
     * 設備管理名を設定する。
     * 
     * @param equipIdent 設備管理名
     */
    public void setEquipIdent(String equipIdent) {
        this.equipIdent = equipIdent;
    }

    /**
     * 値を取得する。
     * 
     * @return 測
     */
    public String getValue() {
        return value;
    }

    /**
     * 値を設定する。
     * 
     * @param value 値
     */
    public void setValue(String value) {
        this.value = value;
    }
}
