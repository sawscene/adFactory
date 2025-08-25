package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.utils;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ConnectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class Connector extends Thread implements Closeable {
    private static final Logger logger = LogManager.getLogger();
    final Socket socket;
    final InputStream inputStream;
    final OutputStream outputStream;
    final Function<byte[], byte[]> receiveFunc;
    final Consumer<IOException> ioExceptionConsumer;
    boolean execution = true;
    boolean complete = true;

    private Connector(String ip, int port, Function<byte[], byte[]> receiveFunc, Consumer<IOException> ioExceptionConsumer) throws IOException {
        try {
            this.socket = new Socket(ip, port);
            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();
            this.receiveFunc = receiveFunc;
            this.ioExceptionConsumer = ioExceptionConsumer;
            this.start();
        } catch (IOException ex) {
            throw ex;
        }
    }

    public interface ThrowableConsumer<T> {
        void accept(T t) throws IOException;
    }

    /**
     * 接続
     * @param ip IPアドレス
     * @param port ポート
     * @param consumer 接続中の関数
     */
    public static void connect(String ip, int port, ThrowableConsumer<Connector> sendConsumer, Function<byte[], byte[]> receiveFunction, Consumer<IOException> ioExceptionConsumer) throws IOException {
        logger.info("connect ip:{}, port:{}", ip, port);
        try (Connector connector = new Connector(ip, port, receiveFunction, ioExceptionConsumer)) {
            sendConsumer.accept(connector);
        } catch (IOException ioException) {
            logger.error(ioException, ioException);
            throw ioException;
        }
        logger.info("disconnect");
    }

    public void send(byte[] item) throws IOException {
        outputStream.write(item);
    }

    private byte[] accept(int timeout) throws IOException {
        this.socket.setSoTimeout(timeout);
        try {
            int c;
            byte[] buf = new byte[1028];//受信バイト列蓄え用
            int n = 0;
            while (n < buf.length && (c = inputStream.read()) != -1) {//ストリーム終端で終了
                if (c == 0x0A) break;// LF の改行なら終了
                buf[n++] = (byte) c;
            }
            return buf;
        } catch (SocketTimeoutException ex) {
            return null;
        }
    }

    @Override
    public void run() {
        complete = false;
        try {
            byte[] rest = null;
            int timeout = 1000;
            while (execution) {
                rest = this.receiveFunc.apply(ConnectionUtils.concat(rest, this.accept(timeout)));
            }
        } catch (IOException ioException) {
            logger.error(ioException, ioException);
            ioExceptionConsumer.accept(ioException);
        } finally {
            complete = true;
        }
    }

    @Override
    public void close() throws IOException {
        logger.info("disconnect");
        this.execution = false;

        if (Objects.nonNull(this.outputStream)) {
            this.outputStream.close();
        }

        if (Objects.nonNull(this.inputStream)) {
            this.inputStream.close();
        }

        if (Objects.nonNull(this.socket)) {
            this.socket.close();
        }
    }
};
