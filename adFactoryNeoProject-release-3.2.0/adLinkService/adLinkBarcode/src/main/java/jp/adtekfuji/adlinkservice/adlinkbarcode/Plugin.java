/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.adlinkbarcode;

import adtekfuji.utility.IniFile;
import com.fazecast.jSerialComm.SerialPort;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.websocket.Session;
import jp.adtekfuji.adFactory.plugin.adLinkServiceInterface;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adlinkservice.command.DevMessage;
import jp.adtekfuji.adlinkservice.command.DevRequest;
import jp.adtekfuji.adlinkservice.command.DevResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * バーコードリーダー プラグイン
 * 
 * @author s-heya
 */
public class Plugin implements adLinkServiceInterface, SerialCommunicationListener {

    public enum Command {
       OPEN,
       CLOSE;
    }

    private static final Logger logger = LogManager.getLogger();
    private static final String PLUGIN_NAME = "adLinkBarcode";
    private final List<Session> sessionList = Collections.synchronizedList(new ArrayList<>());
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
        Session session = (Session)value;
        DevResponse response;

        switch (Command.valueOf(command.getCmd())) {
            case OPEN:
                String port = this.iniFile.getString("1", "Port", null);
                int baudRate = this.iniFile.getInt("1", "BaudRate", 9600);
                int dataBit = this.iniFile.getInt("1", "DataBit", 8);
                int stopBit = this.iniFile.getInt("1", "StopBit", SerialPort.ONE_STOP_BIT);
                int parityBit = this.iniFile.getInt("1", "ParityBit", SerialPort.NO_PARITY);
                BarcodReader.LineEnding terminator = BarcodReader.LineEnding.valueOf(this.iniFile.getString("1", "Terminator", "CR"));

                try {
                    BarcodReader reader = BarcodReader.createIncetance(this, port, baudRate, dataBit, stopBit, parityBit, terminator);

                    if (!reader.isOpend()) {
                        reader.connect();
                    }

                    this.sessionList.add(session);
                    
                    response = new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.SUCCESSED);

                    
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    response = new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.FAILED);
                }
                break;

            case CLOSE:
                if (this.sessionList.contains(session)) {
                    response = new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.SUCCESSED);
                    this.sessionList.remove(session);

                    if (this.sessionList.isEmpty() && Objects.nonNull(BarcodReader.getIncetance())) {
                        BarcodReader.getIncetance().disconect();
                    }
                } else {
                    response = new DevResponse(PLUGIN_NAME, command.getCmd(), DevResponse.Result.ILLEGAL_OPERATION);
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
        Session session = (Session)value;
        
        if (this.sessionList.contains(session)) {
            this.sessionList.remove(session);

            if (this.sessionList.isEmpty() && Objects.nonNull(BarcodReader.getIncetance())) {
                BarcodReader.getIncetance().disconect();
            }
        }
    }

    /**
     * テキストデータを受信した。
     * 
     * @param text テキストデータ
     */
    @Override
    public void onReceive(String text) {
        DevMessage message = new DevMessage(PLUGIN_NAME, text);
        String jsonText = JsonUtils.objectToJson(message);
        this.sessionList.forEach(session -> {
            try {
                session.getBasicRemote().sendText(jsonText);
            } catch (IOException ex) {
                logger.fatal(ex, ex);
            } 
        });
    }

    /**
     * 設定ファイルを読み込む。
     */
    public void loadConfig() {
        logger.info("loadConfig start.");
        try {
            String filePath = System.getenv("ADFACTORY_HOME") + File.separator + "conf\\adLinkBarcode.ini";
            this.iniFile = new IniFile(filePath);
        } catch (IOException ex) {
            logger.fatal(ex);
        } finally {
            logger.info("loadConfig end.");
        }
    }
}
