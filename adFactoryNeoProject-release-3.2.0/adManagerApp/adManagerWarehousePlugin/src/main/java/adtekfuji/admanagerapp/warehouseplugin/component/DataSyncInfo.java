/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.component;

import java.util.Objects;
import java.util.Timer;

/**
 *
 * @author ke.yokoi
 */
public class DataSyncInfo {

    private static DataSyncInfo instance = null;
    private Timer timer = null;

    private DataSyncInfo() {
    }

    public static DataSyncInfo getInstance() {
        if (Objects.isNull(instance)) {
            instance = new DataSyncInfo();
        }
        return instance;
    }

    public Timer getTimer() {
        stopTimer();
        timer = new Timer(true);
        return timer;
    }

    public void stopTimer() {
        if (Objects.nonNull(timer)) {
            timer.cancel();
            timer = null;
        }
    }

}
