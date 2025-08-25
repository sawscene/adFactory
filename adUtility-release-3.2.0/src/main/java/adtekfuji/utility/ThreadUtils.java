/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import javafx.application.Platform;

/**
 * スレッドユーティリティクラス
 *
 * @author s-heya
 */
public class ThreadUtils {

    /**
     * 指定時間、待機する。
     *
     * @param target
     * @param milliseconds
     * @return
     * @throws InterruptedException
     */
    public static int waitFor(Object target, int milliseconds) throws InterruptedException {
        synchronized (target) {
            target.wait(milliseconds);
        }
        return milliseconds;
    }
    
    /**
     * JavaFX アプリケーションスレッド以外のスレッドからUIを表示・更新する。
     *
     * @param <V>
     * @param callable
     * @return
     * @throws Exception
     */
    public static <V> V joinFXThread(Callable<? extends V> callable) throws Exception {
        if (Platform.isFxApplicationThread()) {
            return callable.call();
        }
        RunnableFuture<V> future = new FutureTask(callable);
        Platform.runLater(future);
        return future.get();
    }

    /**
     * JUnit から実行されれているかどうかを返す。
     * 
     * @return true: JUnit から実行されている、false: JUnit 以外から実行されている
     */
    public static boolean isRunningUnderJUnit() {
        try {
            // JUnitのクラスがロードされているか
            Class.forName("org.junit.runner.Result");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
