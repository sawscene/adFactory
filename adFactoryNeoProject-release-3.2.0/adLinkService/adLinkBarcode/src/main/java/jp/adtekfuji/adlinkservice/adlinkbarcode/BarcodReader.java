/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.adlinkbarcode;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * バーコードリーダー
 * 
 * @author s-heya
 */
public class BarcodReader {
    
    /**
     * 改行コード列挙型
     */
    public enum LineEnding {
        CR("\r"),       // 復帰
        CRLF("\r\n"),   // 復帰+改行
        LF("\n");       // 改行

        private final String code;

        /**
         * コンストラクタ
         *
         * @param code
         */
        private LineEnding(String code) {
            this.code = code;
        }

        /**
         * 制御コードを取得する
         *
         * @return
         */
        public String getCode() {
            return this.code;
        }
    }

    private static final Logger logger = LogManager.getLogger();

    private static BarcodReader instance;
    private final String port;
    private boolean isOpend = false;
    private final int baudRate;
    private final int dataBit;
    private final int stopBit;
    private final int parityBit;
    private final int timeout = 5000;
    private final LineEnding terminator;
    private static boolean isDisable = false;

    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Reader reader = null;

    private SerialCommunicationListener listener = null;

    /**
     * コンストラクタ
     *
     * @param port ポート
     * @param baudRate ボーレート
     * @param dataBit データビット
     * @param stopBit ストップビット
     * @param parityBit パリティビット
     * @param terminator ターミネーター
     */
    private BarcodReader(String port, int baudRate, int dataBit, int stopBit, int parityBit, LineEnding terminator) {
        this.port = port;
        this.baudRate = baudRate;
        this.dataBit = dataBit;
        this.stopBit = stopBit;
        this.parityBit = parityBit;
        this.terminator = terminator;
    }

    /**
     * インスタンスを取得する。
     * 
     * @return インスタンス 
     */
    public static synchronized BarcodReader getIncetance() {
        return instance;
    }

    /**
     * インスタンスを生成する。
     *
     * @param listener
     * @param port ポート
     * @param baudRate ボーレート
     * @param dataBit データビット
     * @param stopBit ストップビット
     * @param parityBit パリティビット
     * @param terminator ターミネーター
     * @return インスタンス
     */
    public static BarcodReader createIncetance(SerialCommunicationListener listener, String port, Integer baudRate, Integer dataBit, Integer stopBit, Integer parityBit, LineEnding terminator) {
        if (instance == null) {
            instance = new BarcodReader(port, baudRate, dataBit, stopBit, parityBit, terminator);
            instance.setListener(listener);
        }
        return instance;
    }

    /**
     * シリアルポートを開く。
     *
     * @throws Exception
     */
    public void connect() throws Exception {
        try {
            logger.info(BarcodReader.class.getSimpleName() + "::connect start: " + this.port);

            SerialPort[] serialPorts = SerialPort.getCommPorts();

            for (SerialPort s : serialPorts) {
                if (Objects.equals(s.getSystemPortName(), this.port)) {
                    this.serialPort = s;
                    break;
                }
            }

            if (Objects.isNull(this.serialPort)) {
                String log = "Error: Failed to acquire communication port: " + this.port;
                logger.fatal(log);
                throw new Exception(log);
            }

            logger.info("Communication port was acquired: " + this.port);

            if (this.isOpend == true) {
                if (this.listener != reader.getInnerListener()) {
                    reader.setInnerListener(this.listener);
                }
                return;
            }

            try {
                logger.info("Open the communication port.");

                int count = 0;
                while (!serialPort.openPort(0)) {
                    if (count >= 3) {
                        throw new Exception("Unable to open serial port.");
                    }
                    count++;
                    logger.info("Unable to open serial port.");
                    TimeUnit.MILLISECONDS.sleep(100L);
                }

                if (serialPort instanceof SerialPort) {
                    logger.info("Opened the communication port: " + " baudRate=" + baudRate + ", dataBit=" + dataBit + ", stopBit=" + stopBit + ", parityBit=" + parityBit);

                    serialPort.setComPortParameters(baudRate, dataBit, stopBit, parityBit);
                    serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
                    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, timeout, timeout);

                    isOpend = true;

                    inputStream = serialPort.getInputStream();
                    outputStream = serialPort.getOutputStream();

                    (new Thread(reader = new Reader(inputStream, terminator.getCode(), listener))).start();
                } else {
                    logger.warn("Error: Only serial ports are handled by this example.");
                }
            } catch (Exception ex) {
                logger.fatal("Failed to open the serial port.");
                disconect();
                throw ex;
            }
        } finally {
            logger.info(BarcodReader.class.getSimpleName() + "::connect end.");
        }
    }

    /**
     * シリアルポートを閉じる。
     */
    public void disconect() {
        if (!isOpend) {
            instance = null;
            logger.info("Don't connect Serial port.");
            return;
        }

        try {
            logger.info(BarcodReader.class.getSimpleName() + "::disconect start.");

            if (reader != null) {
                reader.stopRunning();
                reader.setInnerListener(null);
                reader = null;
            }

            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }

            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }

            if (serialPort != null) {
                serialPort.closePort();
                serialPort = null;
            }

            listener = null;
            instance = null;
            isOpend = false;

        } catch (Exception ex) {
            logger.fatal(ex);
        } finally {
            logger.info(BarcodReader.class.getSimpleName() + "::disconect end.");
        }
    }

    /**
     * バイトデータを送信する。
     *
     * @param message
     * @param len
     * @return
     * @throws IOException
     */
    public Boolean send(byte[] message, int len) throws IOException {
        logger.info("Serial send size: {0}", len);
        if (isOpend == false) {
            logger.info("Don't connect Serial port.");
            return false;
        }
        outputStream.write(message, 0, len);
        outputStream.flush();
        return true;
    }

    /**
     * 受信スレッドクラス
     */
    public static class Reader implements Runnable {

        private static final Logger logger = LogManager.getLogger();
    
        private SerialCommunicationListener listener = null;
        private boolean running = true;
        private final Integer buff = 1024;
        private String terminator = "\r";
        InputStream reader = null;
        String message = "";

        public Reader(InputStream reader, String terminator, SerialCommunicationListener listner) {
            this.listener = listner;
            this.reader = reader;
            if (terminator != null) {
                this.terminator = terminator;
            }
        }

        @Override
        public void run() {
            logger.info("Start Read thread.");

            byte[] buffer = new byte[buff];
            int len = -1;
            while (running) {
                try {
                    while ((len = this.reader.read(buffer)) > 0) {
                        logger.debug("Read: " + buffer);
                        message += new String(buffer, 0, len);
                        if (message.endsWith(terminator)) {
                            if (Objects.nonNull(listener) && !BarcodReader.isDisable) {
                                message = message.substring(0, message.length() - terminator.length()); // 終端文字が\r\nの場合2文字取り除かなければならないためlengthだけ引く
                                this.listener.onReceive(message);
                            }
                            message = "";
                        }
                    }
                } catch (IOException ex) {
                    logger.fatal(ex);
                    try {
                        TimeUnit.SECONDS.sleep(2L);
                    } catch (Exception ex1) {
                    }
                }
            }
        }

        public void stopRunning() {
            running = false;
        }

        public SerialCommunicationListener getInnerListener() {
            return this.listener;
        }

        public void setInnerListener(SerialCommunicationListener listener) {
            this.listener = listener;
        }
    }

    /**
     * リスナーを設定する。
     * 
     * @param listener 
     */
    public void setListener(SerialCommunicationListener listener) {
        this.listener = listener;
    }

    /**
     * 読み取り操作を無効にする。
     *
     * @param isDisable
     */
    public void setDisable(Boolean isDisable) {
        BarcodReader.isDisable = isDisable;
    }

    /**
     * シリアルポートが開かれているかどうかを返す。
     *
     * @return
     */
    public boolean isOpend() {
        return this.isOpend;
    }
}
