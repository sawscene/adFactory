/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.work;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.TraceOptionTypeEnum;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * 品質トレーサビリティ設定情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "traceSetting")
@Root(name="traceSetting")
@Default(DefaultType.FIELD)
public class TraceSettingEntity {

    @XmlElementWrapper(name = "traceOptions", required = false)
    @XmlElement(name = "traceOption")
    @ElementList(name="traceOptions", required = false)
    private List<TraceOptionEntity> traceOptions = new ArrayList();

    @XmlElement(name = "traceCustom")
    @XmlElementWrapper(name = "traceCustoms", required = false)
    @ElementList(name="traceCustoms", required = false)
    private List<TraceCustomEntity> traceCustoms = new ArrayList();

    /**
     * コンストラクタ
     */
    public TraceSettingEntity() {
    }

    /**
     * 入力値リストを取得する。
     *
     * @return 入力値リスト
     */
    public List<TraceOptionEntity> getTraceOptions() {
        return this.traceOptions;
    }

    /**
     * 入力値リストを設定する。
     *
     * @param traceOptions 入力値リスト
     */
    public void setTraceOptions(List<TraceOptionEntity> traceOptions) {
        this.traceOptions = traceOptions;
    }

    /**
     * カスタム設定値リストを取得する。
     *
     * @return カスタム設定値リスト
     */
    public List<TraceCustomEntity> getTraceCustoms() {
        return this.traceCustoms;
    }

    /**
     * カスタム設定値リストを設定する。
     *
     * @param traceCustoms カスタム設定値リスト
     */
    public void setTraceCustoms(List<TraceCustomEntity> traceCustoms) {
        this.traceCustoms = traceCustoms;
    }

    /**
     * 入力値リストから値を取得する。
     *
     * @param key
     * @return
     */
    public String getValue(String key) {
        String value = null;
        for (TraceOptionEntity traceOption : this.traceOptions) {
            if (key.equals(traceOption.getKey())) {
                value = traceOption.getValue();
            }
        }
        return value;
    }

    /**
     * 入力値リストのすべてのキーを取得する。
     *
     * @return
     */
    public List<TraceOptionTypeEnum> getKeys() {
        List<TraceOptionTypeEnum> keys = new ArrayList();
        for (TraceOptionEntity traceOption : this.traceOptions) {
            keys.add(TraceOptionTypeEnum.valueOf(traceOption.getKey()));
        }
        return keys;
    }

    /**
     * 入力値リストにキーが存在するかどうかを返す。
     *
     * @param key
     * @return
     */
    public boolean containsKey(String key) {
        boolean isContains = false;
        for (TraceOptionEntity traceOption : this.traceOptions) {
            if (key.equals(traceOption.getKey())) {
                isContains = true;
            }
        }
        return isContains;
    }
    
    /**
     * TraceOptionEntityを取得する。
     *
     * @param key キー
     * @return TraceOptionEntity エンティティ
     */
    public TraceOptionEntity getTraceOption(String key) {
        TraceOptionEntity value = null;
        for (TraceOptionEntity traceOption : this.traceOptions) {
            if (key.equals(traceOption.getKey())) {
                value = traceOption;
                break;
            }
        }
        return value;
    }

    @Override
    public String toString() {
        return "TraceSettingEntity{" + "traceOptions=" + traceOptions + ", traceCustoms=" + traceCustoms + '}';
    }
}
