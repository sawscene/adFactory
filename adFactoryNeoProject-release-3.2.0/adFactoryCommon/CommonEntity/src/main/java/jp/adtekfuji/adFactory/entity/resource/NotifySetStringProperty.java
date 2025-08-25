/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.resource;

import jakarta.xml.bind.annotation.XmlTransient;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;


/**
 * StringProperty(Set時に発火するリスナーを持つ)
 *
 * @author HN)y-harada
 */
public class NotifySetStringProperty extends SimpleStringProperty {
    @XmlTransient
    private OnSetValueListener valueListener;

    /**
     * コンストラクタ
     *
     * @param initStr
     */
    public NotifySetStringProperty(String initStr) {
        super(initStr);
    }

    /**
     * set
     *
     * @param newValue set値
     */
    @Override
    public void set(String newValue) {
        String oldVlue = super.get();
        if (Objects.nonNull(valueListener)) {
            if (valueListener.onValueSet(oldVlue, newValue)) {
                super.set(newValue);
            };
        } else {
            super.set(newValue);
        }
    }

    /**
     * valueListenerを設定する
     *
     * @param valueListener set時に発火するリスナー
     */
    public void setValueListener(OnSetValueListener valueListener) {
        this.valueListener = valueListener;
    }

    /**
     * OnSetValueListener
     */
    public interface OnSetValueListener {
        boolean onValueSet(String oldVlue, String newValue);
    }
}