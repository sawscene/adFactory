/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.utils;

import java.util.Objects;

/**
 *
 * @author yu.kikukawa
 */
public class SwitchCompoSubject {

    private static SwitchCompoSubject instance = null;
    private SwitchCompoObserver observer = null;

    private SwitchCompoSubject() {
    }

    public static SwitchCompoSubject getInstance() {
        if (Objects.isNull(instance)) {
            instance = new SwitchCompoSubject();
        }
        return instance;
    }

    public void setObserver(SwitchCompoObserver observer) {
        this.observer = observer;
    }
    
    public void deleteObserver() {
        this.observer = null;
    }
    
    public void switchCompo() {
        if (Objects.nonNull(observer)) {
            observer.switchCompo();
        }
    }
}
