/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.fxscene;

import java.util.Objects;
import java.util.Properties;
import javafx.stage.Stage;

/**
 * javafx.stage.Stageのプロパティの保存と復元.
 *
 * @author ke.yokoi
 */
public class StageProperties {

    private final Stage stage;
    private final Properties conf;

    public StageProperties(final Stage stage, final Properties conf) {
        this.stage = stage;
        this.conf = conf;
    }

    public void restoration() {
        String ownar = stage.getClass().getCanonicalName() + ".";
        String value = conf.getProperty(ownar + stage.fullScreenProperty().getName());
        if (Objects.isNull(value)) {
            stage.setFullScreen(false);
        } else {
            stage.setFullScreen(Boolean.valueOf(value));
        }
        value = conf.getProperty(ownar + stage.maximizedProperty().getName());
        if (Objects.isNull(value)) {
            stage.setMaximized(false);
        } else {
            stage.setMaximized(Boolean.valueOf(value));
        }
        value = conf.getProperty(ownar + stage.widthProperty().getName());
        if (Objects.isNull(value)) {
            stage.sizeToScene();
        } else {
            stage.setWidth(Double.valueOf(value));
        }
        value = conf.getProperty(ownar + stage.heightProperty().getName());
        if (Objects.isNull(value)) {
            stage.sizeToScene();
        } else {
            stage.setHeight(Double.valueOf(value));
        }
        value = conf.getProperty(ownar + stage.xProperty().getName());
        if (Objects.nonNull(value)) {
            stage.setX(Double.valueOf(value));
        }
        value = conf.getProperty(ownar + stage.yProperty().getName());
        if (Objects.nonNull(value)) {
            stage.setY(Double.valueOf(value));
        }
    }

    public void storation() {
        String ownar = stage.getClass().getCanonicalName() + ".";
        conf.setProperty(ownar + stage.fullScreenProperty().getName(), String.valueOf(stage.isFullScreen()));
        conf.setProperty(ownar + stage.maximizedProperty().getName(), String.valueOf(stage.isMaximized()));
        if (stage.isFullScreen() == false && stage.isMaximized() == false && stage.isIconified() == false) {
            conf.setProperty(ownar + stage.widthProperty().getName(), String.valueOf(stage.widthProperty().getValue()));
            conf.setProperty(ownar + stage.heightProperty().getName(), String.valueOf(stage.heightProperty().getValue()));
            conf.setProperty(ownar + stage.xProperty().getName(), String.valueOf(stage.xProperty().getValue()));
            conf.setProperty(ownar + stage.yProperty().getName(), String.valueOf(stage.yProperty().getValue()));
        }
    }
}
