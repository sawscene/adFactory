/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * コマンドユーティリティクラス
 *
 * @author e-mori
 */
public class ExecUtils {

    private static final Logger logger = LogManager.getLogger();
    private static final Integer LIMIT = 30;

    public enum ExeProcessEnum {

        TIMER,
        WAIT_FOR,
        WAIT_FOR_RESULT;
    }

    /**
     * 実行ファイル起動
     *
     * @param execPass 実行ファイルのパス
     * @param argument 起動引数
     * @param processEnum 実行方法の切り替え
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Object exec(String execPass, String[] argument, ExeProcessEnum processEnum) throws IOException, InterruptedException {
        List<String> list = new ArrayList<>();
        Object result = null;
        try {
            list.add(execPass);
            if (Objects.nonNull(argument)) {
                list.addAll(Arrays.asList(argument));
            }
            logger.debug(list.toString());
            ProcessBuilder pb = new ProcessBuilder(list);

            Process process = pb.start();
            pb.redirectErrorStream(true);

            switch (processEnum) {
                case TIMER:
                    processTimer(process);
                    break;
                case WAIT_FOR:
                    // プロセスの終了まで待って、標準出力の内容を返す。
                    result = processForWait(process);
                    break;
                case WAIT_FOR_RESULT:
                    // プロセスの終了まで待って、プロセスの戻り値を返す。
                    processForWait(process);
                    result = process.exitValue();
                default:
                    break;
            }
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
        return result;
    }

    /**
     * プロセスが終了しない場合のタイムアウト処理
     *
     * @param p 実行するプロセス
     * @throws IOException
     */
    private static void processTimer(Process p) throws IOException {
        try {
            logger.info("processTimer start.");

            long begin = System.currentTimeMillis();
            Object result = null;
            for (;;) {
                TimeUnit.MILLISECONDS.sleep(100L);

                if (!p.isAlive()) {
                    int returnVal = p.exitValue();
                    logger.info("process completion:{} ", returnVal);
                    switch (returnVal) {
                        case 1:
                            logger.info("process completion fatal.");
                            break;
                        case 2:
                            logger.info("process result hava a value.");
                            break;
                        case 0:
                            logger.info("process completion sucess.");
                            break;
                    }
                    break;
                }

                long now = System.currentTimeMillis();
                // LIMIT is second.
                if (TimeUnit.MILLISECONDS.toSeconds(now - begin) >= LIMIT) {
                    logger.fatal("process time out:{}", new Date(now));
                    p.destroy();
                    break;
                }
            }
        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        } catch (Exception ex) {
            p.destroy();
            throw ex;
        } finally {
            logger.info("processTimer end.");
        }
    }

    /**
     * プロセスが終了するまで待つ
     *
     * @param p 実行するプロセス
     * @throws IOException
     * @throws InterruptedException
     */
    private static List<String> processForWait(Process p) throws IOException, InterruptedException {
        logger.info("processForWait start");
        List<String> ret = new ArrayList<>();
        final Process p2 = p;
        try {
            //エラー出力は別スレッドで吐き出す
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    String str;
                    BufferedReader brerr = new BufferedReader(new InputStreamReader(p2.getErrorStream()));
                    try {
                        while ((str = brerr.readLine()) != null) {
                            logger.warn("-> {}", str);
                        }
                    }
                    catch (IOException ex) {
                        logger.fatal(ex, ex);
                    }
                    finally {
                        try {
                            brerr.close();
                        }
                        catch (IOException ex) {
                            logger.fatal(ex, ex);
                        }
                    }
                }
            });
            th.start();

            String str;
            BufferedReader brstd = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((str = brstd.readLine()) != null) {
                ret.add(str);
                logger.debug("-> {}", str);
            }
            brstd.close();
            logger.debug("return:{}", p.waitFor());
            logger.info("processForWait end");
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
        return ret;
    }
}
