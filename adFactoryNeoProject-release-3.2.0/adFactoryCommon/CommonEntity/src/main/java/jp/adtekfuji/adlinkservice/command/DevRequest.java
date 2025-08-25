/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 要求コマンド
 * 
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "devRequest")
@JsonIgnoreProperties(ignoreUnknown=true)
public class DevRequest  implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @XmlElement()
    private String plugin;

    @XmlElement()
    private String cmd;
    
    @XmlElement()
    private String equipIdent;

    @XmlElement()
    private List<String> args; 

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
     * パラメータを取得する。
     * 
     * @return パラメータ
     */
    public List<String> getArgs() {
        return args;
    }

    /**
     * パラメータを設定する。
     * 
     * @param args パラメータ 
     */
    public void setArgs(List<String> args) {
        this.args = args;
    }
}
