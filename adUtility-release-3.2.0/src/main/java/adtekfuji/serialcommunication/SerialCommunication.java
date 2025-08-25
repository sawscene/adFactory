/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.serialcommunication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * シリアル通信 (未実装)
 * 
 * @author s-heya
 */
public class SerialCommunication {

    private static final Logger logger = LogManager.getLogger();
    private static SerialCommunication instance;

    private final String commPortNum;
    private final Integer baudRate;
    private final Integer dataBit;
    private final Integer stopBit;
    private final Integer parityBit;
    private final Integer timeout = 5000;
    private boolean connectPort = false;
    private String suffix = "\r";

    private SerialCommunicationListener controllerListener = null;

    private SerialCommunication(String commPortNum, Integer baudRate, Integer dataBit, Integer stopBit, Integer parityBit) {
        this.commPortNum = commPortNum;
        this.baudRate = baudRate;
        this.dataBit = dataBit;
        this.stopBit = stopBit;
        this.parityBit = parityBit;
    }

    public static SerialCommunication getIncetance() {
        return instance;
    }

    public static SerialCommunication getIncetance(String commPortNum, Integer baudRate, Integer dataBit, Integer stopBit, Integer parityBit) {
        if (instance == null) {
            instance = new SerialCommunication(commPortNum, baudRate, dataBit, stopBit, parityBit);
        }
        return instance;
    }

    /**
     * シリアル通信ポート開
     *
     * @throws java.lang.Exception
     */
    public void connect() throws Exception {
        logger.info("Serial connect port:{} ", commPortNum);
    }

    /**
     * シリアル通信ポート閉
     */
    public void disconect() {
    }

    /**
     * データ送信
     */
    public Boolean send(byte[] message, int len) throws IOException {
        logger.info("Serial send size:{}", len);
        return true;
    }

    /**
     * データ受信. 受信処理 バーコード用の受信処理になっているため汎用的な改変の必要有
     */
    public static class Reader implements Runnable {

        private SerialCommunicationListener controllerListener = null;
        private boolean running = true;
        private final Logger threadLogger = LogManager.getLogger();
        private final Integer buff = 1024;
        private String innerSuffix = "\r";
        InputStream reader = null;
        String receivedMessage = "";
        String receiveBuffer = "";

        public Reader(InputStream reader, String suffix, SerialCommunicationListener listner) {
            this.controllerListener = listner;
            this.reader = reader;
            if (suffix != null) {
                this.innerSuffix = suffix;
            }
        }

        @Override
        public void run() {
            threadLogger.info("Start Read thread.");

            byte[] buffer = new byte[buff];
            int len = -1;
            while (running) {
                try {
                    while ((len = this.reader.read(buffer)) > 0) {
                        receiveBuffer = new String(buffer, 0, len);
                        receivedMessage += receiveBuffer;
                        if (receivedMessage.endsWith(innerSuffix)) {
                            System.out.println(receivedMessage);
                            if (Objects.nonNull(controllerListener)) {
                                receivedMessage = receivedMessage.substring(0, receivedMessage.length() - 1);
                                this.controllerListener.listner(receivedMessage);
                            }
                            receivedMessage = "";
                        }
                    }
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                    try {
                        Thread.sleep(2000);
                    } catch (Exception ex1) {
                    }
                }
            }
        }

        public void stopRunning() {
            running = false;
        }

        public SerialCommunicationListener getInnerListener() {
            return this.controllerListener;
        }

        public void setInnerListener(SerialCommunicationListener controller) {
            this.controllerListener = controller;
        }

        public void setInnerSuffix(String suffix) {
            this.innerSuffix = suffix;
        }
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setListener(SerialCommunicationListener controller) {
        this.controllerListener = controller;
    }
}
