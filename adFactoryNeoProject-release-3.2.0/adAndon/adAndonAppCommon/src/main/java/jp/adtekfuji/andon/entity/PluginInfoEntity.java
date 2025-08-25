/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

/**
 * プラグイン情報
 *
 * @author nar-nakamura
 */
public class PluginInfoEntity {

    private String dispName;// 表示名
    private String componentName;// コンポーネント名
    private String pluginName;// プラグイン名

    /**
     * プラグイン情報
     */
    public PluginInfoEntity() {
    }

    /**
     * 表示名を取得する。
     *
     * @return 表示名
     */
    public String getDispName() {
        return this.dispName;
    }

    /**
     * 表示名を設定する。
     *
     * @param value 表示名
     */
    public void setDispName(String value) {
        this.dispName = value;
    }

    /**
     * コンポーネント名を取得する。
     *
     * @return コンポーネント名
     */
    public String getComponentName() {
        return this.componentName;
    }

    /**
     * コンポーネント名を設定する。
     *
     * @param value コンポーネント名
     */
    public void setComponentName(String value) {
        this.componentName = value;
    }

    /**
     * プラグイン名を取得する。
     *
     * @return プラグイン名
     */
    public String getPluginName() {
        return this.pluginName;
    }

    /**
     * プラグイン名を設定する。
     *
     * @param value プラグイン名
     */
    public void setPluginName(String value) {
        this.pluginName = value;
    }

    @Override
    public String toString() {
        return new StringBuilder("PluginInfoEntity{")
                .append("dispName").append(this.dispName)
                .append(", ")
                .append("componentName=").append(this.componentName)
                .append(", ")
                .append("pluginName=").append(this.pluginName)
                .append("}")
                .toString();
    }
}
