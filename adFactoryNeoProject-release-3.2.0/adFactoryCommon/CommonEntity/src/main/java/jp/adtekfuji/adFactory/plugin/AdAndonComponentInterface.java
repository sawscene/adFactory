/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.plugin;

/**
 *
 * @author ke.yokoi
 */
public interface AdAndonComponentInterface<T> {

    public void updateDisplay(T msg);

    public void exitComponent();

}
