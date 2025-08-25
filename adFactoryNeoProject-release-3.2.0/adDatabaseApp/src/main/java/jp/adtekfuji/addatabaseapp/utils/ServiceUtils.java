/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.utils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * サービスユーティリティクラス
 *
 * @author nar-nakamura
 */
public class ServiceUtils {

    private static final Logger logger = LogManager.getLogger();

    /**
     * サービスの状態を取得する。
     *
     * @param serviceName サービス名
     * @return 状態 (true:開始, false:開始以外)
     */
    public static boolean getServiceState(String serviceName) {
        boolean result = false;
        try {
            String command = String.format("sc query \"%s\" | findstr -i state", serviceName);

            String[] arg = { CMDContents.ARG_C, command };
            Object lines = ExecUtils.exec(CMDContents.EXE, arg, ExecUtils.ExeProcessEnum.WAIT_FOR);
            if (Objects.nonNull(lines) && lines instanceof List) {
                for (String line : (List<String>) lines) {
                    if (line.contains("STATE") && line.contains("RUNNING")) {
                        result = true;
                    }
                }
            }
        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * サービスの状態を設定する。(管理者権限が必要)
     *
     * @param serviceName サービス名
     * @param isStart 状態 (true:開始する, false:停止する)
     */
    public static void setServiceState(String serviceName, boolean isStart) {
        logger.info("setServiceState start");
        try {
            String state = isStart ? CMDContents.ARG_START : CMDContents.ARG_STOP;
            String command = String.format("net %s \"%s\"", state, serviceName);

            String[] arg = { CMDContents.ARG_C, command };
            Object lines = ExecUtils.exec(CMDContents.EXE, arg, ExecUtils.ExeProcessEnum.WAIT_FOR);
            if (Objects.nonNull(lines)) {
                logger.info(lines);
            }

        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("setServiceState end");
        }
    }
}
