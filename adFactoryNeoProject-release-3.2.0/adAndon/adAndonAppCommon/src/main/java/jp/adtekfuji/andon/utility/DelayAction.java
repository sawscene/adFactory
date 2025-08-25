/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.utility;

import adtekfuji.property.AdProperty;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import jp.adtekfuji.andon.common.Constants;

/**
 * 遅延アクションクラス
 *
 * @author fu-kato
 */
public class DelayAction {

    private DelayTask delayTask;

    /**
     * 遅延タスク<br>
     * タイマーで待機中にenableDelayActionが呼ばれた場合のみactionを実行する。<br>
     * 待機中enableDelayActionが有効にならなかった場合actionは実行されない。
     */
    private class DelayTask extends TimerTask {

        private boolean enableDelayAction;
        private boolean isRunning;

        private final Runnable action;

        DelayTask(Runnable action) {
            this.action = action;
        }

        public void enableDelayAction() {
            enableDelayAction = true;
        }

        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void run() {
            if (enableDelayAction) {
                action.run();
            }
            isRunning = true;
        }
    }

    final private Timer timer;
    private long delayTime;

    public DelayAction() {
        timer = new Timer();
        this.delayTime = Long.parseLong(AdProperty.getProperties().getProperty(Constants.DELAY_TIME, "60000"));
    }

    /**
     * 即時実行した後、1分後に再度同じアクションを実行する。<br>
     * ただし再度アクションが実施されるためには追加でこのメソッドを実行する必要がある。
     *
     * @param action
     */
    public void run(Runnable action) {

        if (Objects.nonNull(delayTask) && !delayTask.isRunning()) {
            delayTask.enableDelayAction();
            return;
        }

        action.run();

        delayTask = new DelayTask(action);
        timer.schedule(delayTask, delayTime);
    }

    /**
     * 遅延アクションをキャンセルする。
     */
    public void cancel() {
        this.timer.cancel();
    }
}
