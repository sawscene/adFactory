/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.fxscene;

import java.util.Objects;
import java.util.Properties;
import javafx.scene.control.Dialog;

/**
 * ダイアログ属性を保存・復元する。.
 *
 * @author s-heya
 */
public class DialogProperties {

    private final Dialog dlg;
    private final Properties properties;
    private final String componentName;

    /**
     * コンストラクタ
     * 
     * @param dlg ダイアログ
     * @param properties プロパティ
     * @param componentName コンポーネント名
     */
    public DialogProperties(Dialog dlg, Properties properties, String componentName) {
        this.dlg = dlg;
        this.properties = properties;
        this.componentName = componentName;
    }

    /**
     * ダイアログ属性を復元する。
     */
    public void restoration() {
        String value = properties.getProperty(componentName + dlg.getDialogPane().widthProperty().getName());
        if (Objects.nonNull(value)) {
            dlg.getDialogPane().setPrefWidth(Double.parseDouble(value));
        }

        value = properties.getProperty(componentName + dlg.getDialogPane().heightProperty().getName());
        if (Objects.nonNull(value)) {
            dlg.getDialogPane().setPrefHeight(Double.parseDouble(value));
        }

        value = properties.getProperty(componentName + dlg.xProperty().getName());
        if (Objects.nonNull(value)) {
            double x = Double.parseDouble(value);
            dlg.setX(x >= 0 ? x : 0);
        }

        value = properties.getProperty(componentName + dlg.yProperty().getName());
        if (Objects.nonNull(value)) {
            double y = Double.parseDouble(value);
            dlg.setY(y >= 0 ? y : 0);
        }
    }

    /**
     * ダイアログ属性を保存する。
     */
    public void storation() {
        properties.setProperty(componentName + dlg.getDialogPane().widthProperty().getName(), String.valueOf(dlg.getDialogPane().widthProperty().getValue()));
        properties.setProperty(componentName + dlg.getDialogPane().heightProperty().getName(), String.valueOf(dlg.getDialogPane().heightProperty().getValue()));
        properties.setProperty(componentName + dlg.xProperty().getName(), String.valueOf(dlg.xProperty().getValue()));
        properties.setProperty(componentName + dlg.yProperty().getName(), String.valueOf(dlg.yProperty().getValue()));
    }
}
