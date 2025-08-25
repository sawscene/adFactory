/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.adlinkserverurlreceiver;

import java.util.Objects;
import javax.websocket.Session;

import adtekfuji.utility.StringUtils;
import jp.adtekfuji.adFactory.plugin.adLinkServiceInterface;
import jp.adtekfuji.adlinkservice.command.DevRequest;
import jp.adtekfuji.adlinkservice.command.DevResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import adtekfuji.property.AdProperty;

/**
 * FP Inspection プラグイン
 *
 * @author s-heya
 */
public class Plugin implements adLinkServiceInterface {

    public enum Command {
        APPLY;
    }

    private static final Logger logger = LogManager.getLogger();
    private static final String PLUGIN_NAME = "adLinkServerURLReceiver";


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
    }

    /**
     * 要求コマンドを受信した。
     *
     * @param command コマンド
     * @param value   セッション
     * @return DevResponse
     */
    @Override
    public DevResponse onRequest(DevRequest command, Object value) {
        Session session = (Session) value;

        if (Command.valueOf(command.getCmd()) == Command.APPLY) {
            // 検査コマンド情報を取得
            if (command.getArgs().isEmpty() || StringUtils.isEmpty(command.getArgs().get(0))) {
                return new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.INVALID_MESSAGE);
            }

            AdProperty.getProperties().setProperty("adFactoryServerURI", command.getArgs().get(0));
            try {
                AdProperty.store();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                return new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.ILLEGAL_OPERATION, null);
            }

            return new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.SUCCESSED, null);

        }
        return new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.UNKNOWN_COMMAND);
    }

    /**
     * クライアントが切断した。
     *
     * @param value セッション
     */
    @Override
    public void onClose(Object value) {
    }
}
