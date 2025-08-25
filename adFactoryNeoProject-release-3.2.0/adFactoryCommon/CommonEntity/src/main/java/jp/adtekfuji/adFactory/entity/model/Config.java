/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 設定情報
 *
 * @author kentarou.suzuki
 */
@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Config implements Serializable {

    /**
     * エンティティのバージョン
     */
    private static final long serialVersionUID = 1L;

    /**
     * モデル設定一覧
     */
    @XmlElementWrapper(name = "modelSettings")
    @XmlElement(name = "modelSetting")
    @JsonProperty("ModelSetting")
    private List<ModelSetting> modelSettings = new ArrayList<>();

    /**
     * コンストラクタ
     */
    public Config() {
    }

    /**
     * 進捗モニタ設定を初期化して作成する。
     *
     * @return 進捗モニタ設定
     */
    public static Config create() {
        Config setting = new Config();
        setting.setModelSettings(Arrays.asList(ModelSetting.create()));

        return setting;
    }

    /**
     * モデル設定一覧を取得する。
     *
     * @return モデル設定一覧
     */
    public List<ModelSetting> getModelSettings() {
        return modelSettings;
    }

    /**
     * モデル設定一覧を設定する。
     *
     * @param modelSettings モデル設定一覧
     */
    public void setModelSettings(List<ModelSetting> modelSettings) {
        this.modelSettings = modelSettings;
    }

    /**
     * ハッシュコードを取得する。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.modelSettings);
        return hash;
    }

    /**
     * オブジェクトが等しいかどうかを取得する。
     * 
     * @param obj 比較対象のオブジェクト
     * @return オブジェクトが等しい場合はtrue、それ以外の場合はfalse
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Config other = (Config) obj;
        return Objects.equals(this.getModelSettings(), other.getModelSettings());
    }

    /**
     * 文字列表現を取得する。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("Config{")
                .append("modelSettings=").append(this.modelSettings)
                .append("}")
                .toString();
    }

    /**
     * モデル設定一覧のコピーを新規作成する。
     *
     * @return モデル設定
     */
    @Override
    public Config clone() {
        Config setting = new Config();

        // モデル設定
        List<ModelSetting> settings = new LinkedList();
        this.getModelSettings().stream().forEach(c -> settings.add(c.clone()));
        setting.setModelSettings(settings);
        
        return setting;
    }
}
