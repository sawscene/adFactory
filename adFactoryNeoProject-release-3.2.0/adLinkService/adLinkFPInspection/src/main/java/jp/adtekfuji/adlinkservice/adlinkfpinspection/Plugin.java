/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.adlinkfpinspection;

import adtekfuji.utility.IniFile;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.websocket.Session;
import jp.adtekfuji.adFactory.plugin.adLinkServiceInterface;
import jp.adtekfuji.adlinkservice.command.DevRequest;
import jp.adtekfuji.adlinkservice.command.DevResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FP Inspection プラグイン
 *
 * @author s-heya
 */
public class Plugin implements adLinkServiceInterface {

    public enum Command {
        INSPECTION;
    }

    private static final Logger logger = LogManager.getLogger();
    private static final String PLUGIN_NAME = "adLinkFPInspection";
    private Session current;
    private IniFile iniFile;

    /**
     * プラグイン名を取得する。
     *
     * @return プラグイン名
     */
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    /**
     * クライアントが接続した。
     *
     * @param value セッション
     */
    @Override
    public void onOpen(Object value) {
        this.loadConfig();
    }

    /**
     * 要求コマンドを受信した。
     *
     * @param command コマンド
     * @param value セッション
     * @return DevResponse
     */
    @Override
    public DevResponse onRequest(DevRequest command, Object value) {
        Session session = (Session) value;
        DevResponse response;

        switch (Command.valueOf(command.getCmd())) {
            case INSPECTION:
                try {
                    current = session;
                    ProcessWorker worker = ProcessWorker.createIncetance(this.iniFile);
                    worker.startInspectionResult(command);
                    InspectionCommand inspectionResult = worker.getInspectionResult();

                    if (Objects.isNull(inspectionResult.getInspectionError())) {
                        int result = Integer.valueOf(inspectionResult.getResult(), 16);
                        if (Objects.equals(result, 0)) {
                            // 検査成功
                            response = new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.SUCCESSED, String.join(",", worker.getInspectionValues()));
                        } else {
                            // 検査失敗
                            response = new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.FAILED, String.format("(0x%s)", inspectionResult.getResult()));
                        }
                    } else {
                        // 検査ファイル更新エラー又は検査結果待ちタイムアウト
                        response = new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.FAILED, inspectionResult.getInspectionError().name());
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    response = new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.FAILED);
                }
                break;

            default:
                response = new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.UNKNOWN_COMMAND);
                break;
        }

        return response;
    }

    /**
     * クライアントが切断した。
     *
     * @param value セッション
     */
    @Override
    public void onClose(Object value) {
        if (Objects.equals(current, value)) {
            current = null;
        }
    }

    /**
     * 設定ファイルを読み込む。
     */
    public void loadConfig() {
        logger.info("loadConfig start.");
        try {
            String filePath = System.getenv("ADFACTORY_HOME") + File.separator + "adLinkService\\conf\\adLinkFPInspection.ini";
            this.iniFile = new IniFile(filePath);
        } catch (IOException ex) {
            logger.fatal(ex);
        } finally {
            logger.info("loadConfig end.");
        }
    }
}
