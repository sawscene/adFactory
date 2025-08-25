/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.net;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

/**
 * バックグラウンドスレッドの状態を表示するためのインターフェイス
 * 
 * @author s-heya
 */
public interface UITask {
    StringProperty messageProperty();
    DoubleProperty progressProperty();
}
