/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice;

import adtekfuji.plugin.PluginLoader;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.websocket.Session;
import jp.adtekfuji.adFactory.plugin.adLinkServiceInterface;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adlinkservice.command.DevRequest;
import jp.adtekfuji.adlinkservice.command.DevResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *  プラグインコンテナー
 * 
 * @author s-heya
 */
public class PluginContainer {

    private static final Logger logger = LogManager.getLogger();
    private final Map<String, adLinkServiceInterface> plugins = new HashMap<>();
    private static PluginContainer instance = null;

    /**
     * コンストラクタ
     */
    private PluginContainer() {
        PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");

        // プラグインをロード
        PluginLoader.load(adLinkServiceInterface.class).forEach(plugin -> {
            if (!StringUtils.isEmpty(plugin.getPluginName())) {
                logger.info("Plugin: " + plugin.getPluginName());
                this.plugins.put(plugin.getPluginName(), plugin);
            }
        });
    }

    /**
     * インスタンスを生成する。
     * 
     * @return PluginContainer インスタンス
     */
    public static PluginContainer createInstance() {
        if (Objects.isNull(instance)) {
            instance = new PluginContainer();
        }
        return instance;
    }

    /**
     * インスタンスを取得する。
     * 
     * @return PluginContainer インスタンス
     */
    public static PluginContainer getInstance() {
        if (Objects.isNull(instance)) {
            return createInstance();
        }
        return instance;
    }
    
    /**
     * クライアントが接続した。
     * 
     * @param session セッション
     */
    public void onOpen(Session session) {
        this.plugins.values().forEach(plugin -> plugin.onOpen(session));
    }
    
    /**
     * コマンドを受信した。
     * 
     * @param command コマンド
     * @param session セッション
     */
    public void onCommand(String command, Session session) {
        DevResponse response = null;

        try {
            DevRequest request = JsonUtils.jsonToObject(command, DevRequest.class);
            
            if (StringUtils.isEmpty(request.getPlugin())
                    || StringUtils.isEmpty(request.getCmd())) {
                response = new DevResponse("", "", DevResponse.Result.INVALID_MESSAGE);
                return;
            }

            if (!this.plugins.containsKey(request.getPlugin())) {
                response = new DevResponse("", "", DevResponse.Result.MISSING_PLUGIN);
                return;
            }

            response = this.plugins.get(request.getPlugin()).onRequest(request, session);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            response = new DevResponse("", "", DevResponse.Result.SYSTEM_ERROR);

        } finally {
            if (Objects.nonNull(response)) {
                try {
                    String jsonText = JsonUtils.objectToJson(response);
                    session.getBasicRemote().sendText(jsonText);           
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
    }
    
    /**
     * クライアントが切断した。
     * 
     * @param session セッション
     */
    public void onClose(Session session) {
        this.plugins.values().stream().forEach(plugin -> plugin.onClose(session));
    }
}
