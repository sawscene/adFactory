/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.fxscene;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * SceneContiner用プロパティクラス
 *
 * @author ke.yokoi
 */
public class SceneProperties {

    private final Stage stage;
    private final Properties properties;
    private String appTitle = "";
    private Image appIcon = null;
    private final List<String> csspathes = new ArrayList<>();
    private Double minHeight = 0.0;
    private Double minWidth = 0.0;

    public SceneProperties(final Stage stage, final Properties properties) {
        this.stage = stage;
        this.properties = properties;
        csspathes.clear();
    }

    public Stage getStage() {
        return stage;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public Image getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Image appIcon) {
        this.appIcon = appIcon;
    }

    public void addCssPath(String csspath) {
        csspathes.add(csspath);
    }

    public List<String> getCsspathes() {
        return csspathes;
    }

    public Double getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(Double minHeight) {
        this.minHeight = minHeight;
    }

    public Double getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(Double minWidth) {
        this.minWidth = minWidth;
    }

}
