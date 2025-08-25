/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.fxscene;

import java.util.Objects;
import java.util.Properties;
import javafx.scene.control.SplitPane;

/**
 *
 * @author ke.yokoi
 */
public class SplitPaneProperties {

    private final SplitPane splitPane;
    private final Properties conf;

    public SplitPaneProperties(final SplitPane splitPane, final Properties conf) {
        this.splitPane = splitPane;
        this.conf = conf;
    }

    public void restoration() {
        String ownar = splitPane.getClass().getCanonicalName() + ".";
        String value = conf.getProperty(ownar + "dividerNum");
        if (Objects.nonNull(value)) {
            for (Integer i = 0; i < Integer.valueOf(value); i++) {
                value = conf.getProperty(ownar + "divider" + i);
                splitPane.setDividerPosition(i, Double.valueOf(value));
            }
        }
    }

    public void storation() {
        String ownar = splitPane.getClass().getCanonicalName() + ".";
        double[] pos = splitPane.getDividerPositions();
        conf.setProperty(ownar + "dividerNum", String.valueOf(pos.length));
        for (Integer i = 0; i < pos.length; i++) {
            conf.setProperty(ownar + "divider" + i, String.valueOf(pos[i]));
        }
    }

}
