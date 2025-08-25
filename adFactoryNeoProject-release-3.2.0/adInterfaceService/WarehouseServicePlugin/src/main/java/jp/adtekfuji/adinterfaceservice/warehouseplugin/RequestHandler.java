/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.warehouseplugin;

import java.util.Date;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * リクエストハンドラー
 *
 * @author ke.yokoi
 */
public class RequestHandler {
    
    private static RequestHandler instance;
    
    private final Logger logger = LogManager.getLogger();

    private static final String COMMA = ",";
    private static final String GET_STATUS = "GET_STATUS";
    private static final String RESPONCE_RUNNING = "SYNC_RUNNING";
    private static final String START_IMPORT = "START_IMPORT";

    /**
     * コンストラクタ
     */
    private RequestHandler() {
    }
    
    /**
     * リクエストハンドラーのインスタンスを取得する。
     * 
     * @return RequestHandler
     */
    public static RequestHandler getInstance() {
        if (Objects.isNull(instance)) {
            instance = new RequestHandler();
        }
        return instance;
    }

    /**
     * リクエストを処理する。
     * 
     * @param request リクエストメッセージ
     * @return 処理結果
     */
    public String handle(String request) {
        String message[] = request.split(",");
        String response = null;

        switch (message[1].trim()) {
            case GET_STATUS:
                response = "";
                break;
            case START_IMPORT:
                response = this.startImport(request, message[2].trim(), message[3].trim(), message[4].trim());
                break;
        }

        logger.debug("response: " + response);
        return response;
    }

    /**
     * インポートを開始する。
     * 
     * @param request リクエストメッセージ
     * @param path パス
     * @param auto 自動インポート
     * @param interval ポーリング間隔
     * @return 
     */
    private String startImport(String request, String path, String autoImpott, String interval) {
        boolean auto = Boolean.valueOf(autoImpott);

        WarehouseScheduler scheduler = WarehouseScheduler.getInstance();
        scheduler.runTask(new Date(), path, auto, interval);

        return RESPONCE_RUNNING + COMMA + request;
    }
}
