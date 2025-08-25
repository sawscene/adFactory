/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.warehouseservicetesttool.utils;

import jp.adtekfuji.warehouseservicetesttool.entity.BhtCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
@ChannelHandler.Sharable
public class WarehouseClientHandler extends SimpleChannelInboundHandler<String> {

    private final Logger logger = LogManager.getLogger();

    private Channel channel = null;
    private String rcvBuf = "";

    private String modelCode= "BHT13QWB";
    private String serialNumber = "502342";
    private String ipAddress = "127.0.0.1";
    private String ftpUser = "adtek";
    private String ftpPassword = "adtek";

    private String execCommand = null;
    private String ftpFileName = null;

    private static final int TIMEOUT = 30 * 1000;
    private final Object lock = new Object();

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public WarehouseClientHandler(String serialNumber, String ipAddress) {
        this.serialNumber = serialNumber;
        this.ipAddress = ipAddress;
    }

    /**
     * 
     * @param chc
     * @throws Exception 
     */
    @Override
    public void channelActive(ChannelHandlerContext chc) throws Exception {
        logger.info("connect:{},{}", chc.channel().hashCode(), chc.channel());
        this.channel = chc.channel();
    }

    /**
     * 
     * @param chc
     * @throws Exception 
     */
    @Override
    public void channelInactive(ChannelHandlerContext chc) throws Exception {
        logger.info("unconnect:{},{}", chc.channel().hashCode(), chc.channel());
        this.closeAllChannel();
    }

    /**
     * 
     * @param chc
     * @param message
     * @throws Exception 
     */
    @Override
    protected void channelRead0(ChannelHandlerContext chc, String message) throws Exception {
        logger.debug("channelRead0:{},{},{}", chc.channel().hashCode(), chc.channel(), message);
        synchronized (lock) {
            String msg;
            msg = rcvBuf + message;

            Integer stxPos = msg.indexOf(BhtCommand.BHT_STX);
            if (stxPos < 0) {
                rcvBuf = "";
                logger.info("*** wait stx");
                return;
            }

            Integer etxPos = msg.indexOf(BhtCommand.BHT_ETX, stxPos + 1);
            if (etxPos < 0) {
                rcvBuf = msg;
                logger.info("*** wait etx");
                return;
            }

            if (msg.length() <= etxPos + 1) {
                rcvBuf = "";
            } else {
                rcvBuf = msg.substring(etxPos + 1);
            }

            String bhtCommandString = msg.substring(stxPos + 1, etxPos);
            logger.info("recieve command:{}", bhtCommandString);
            BhtCommand rcvCmd = BhtCommandConverter.BhtCommandEncoder(bhtCommandString);

            if (rcvCmd.getTypeCode().equals(BhtCommand.BHT_RESPONSE)
                    && rcvCmd.getModelCode().equals(modelCode)
                    && rcvCmd.getSerialNumber().equals(serialNumber)) {
                logger.info("***** rcvFlg.notify");
                lock.notify();
//                lock.notifyAll();
            }
        }
    }

    /**
     * 
     * @return 
     */
    public boolean isExistChannel() {
        return Objects.nonNull(channel);
    }

    /**
     * ファイルをアップロードする。
     *
     * @param srcDir アップロード元フォルダ
     * @param fileName アップロードファイル名
     * @return 結果 (0:成功)
     */
    public int uploadFile(String srcDir, String fileName) {
        int ret = -1;
        logger.info("uploadFile:{}", fileName);
        if (Objects.isNull(this.channel)) {
            logger.warn("channel is null.");
            return -2;
        }

        this.execCommand = BhtCommand.BHT_UPLOAD;
        this.ftpFileName = fileName;
        BhtCommand cmd;

        try {
            // UPLOAD 要求を送信する。
            cmd = new BhtCommand(this.execCommand, this.serialNumber, this.ipAddress, this.modelCode, this.ftpFileName, "0", "0", "");

            String bhtCommandString = BhtCommandConverter.BhtCommandDecoder(cmd);

            StringBuilder sb1 = new StringBuilder();
            sb1.append(BhtCommand.BHT_STX);
            sb1.append(bhtCommandString);
            sb1.append(BhtCommand.BHT_ETX);
            synchronized (lock) {
                this.send(sb1.toString());
                try {
                    lock.wait(TIMEOUT);
                } catch (InterruptedException e) {
                    logger.warn("responce time out. (1)");
                    return -3;
                }
            }

            // FTPでファイルを送信する。
            String resultValue = "1";
            FTPUtil ftpUtil = new FTPUtil();
            boolean con = ftpUtil.connect(this.ipAddress, this.ftpUser, this.ftpPassword, "SJIS");
            if (con) {
                String destDir = this.serialNumber;
                if (ftpUtil.put(srcDir, this.ftpFileName, destDir, this.ftpFileName)) {
                    resultValue = "0";
                }
                ftpUtil.close();
            }

            // RESULT を返す。
            cmd = new BhtCommand(BhtCommand.BHT_RESULT, this.serialNumber, this.ipAddress, this.modelCode, resultValue, "", "", "");

            String bhtResultString = BhtCommandConverter.BhtCommandDecoder(cmd);

            StringBuilder sb2 = new StringBuilder();
            sb2.append(BhtCommand.BHT_STX);
            sb2.append(bhtResultString);
            sb2.append(BhtCommand.BHT_ETX);
            synchronized (lock) {
                this.send(sb2.toString());
                try {
                    lock.wait(TIMEOUT);
                } catch (InterruptedException e) {
                    logger.warn("responce time out. (2)");
                    return -4;
                }
            }

            ret = Integer.valueOf(resultValue);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            execCommand = null;
        }
        return ret;
    }

    /**
     * ファイルをダウンロードする。
     *
     * @param dstDir ダウンロード先フォルダ
     * @param fileName ダウンロードファイル名
     * @return 結果 (0:成功)
     */
    public int downloadFile(String dstDir, String fileName) {
        int ret = -1;
        logger.info("downloadFile:{}", fileName);
        if (Objects.isNull(channel)) {
            logger.warn("channel is null.");
            return -2;
        }

        this.execCommand = BhtCommand.BHT_DWNLOAD;
        this.ftpFileName = fileName;
        BhtCommand cmd;

        try {
            // DWNLOAD 要求を送信する。
            cmd = new BhtCommand(this.execCommand, this.serialNumber, this.ipAddress, this.modelCode, this.ftpFileName, "0", "0", "");

            String bhtCommandString = BhtCommandConverter.BhtCommandDecoder(cmd);

            StringBuilder sb1 = new StringBuilder();
            sb1.append(BhtCommand.BHT_STX);
            sb1.append(bhtCommandString);
            sb1.append(BhtCommand.BHT_ETX);
            synchronized (lock) {
                this.send(sb1.toString());
                try {
                    lock.wait(TIMEOUT);
                } catch (InterruptedException e) {
                    logger.warn("responce time out.");
                    return -3;
                }
            }

            // FTPでファイルを受信する。
            String resultValue = "1";
            FTPUtil ftpUtil = new FTPUtil();
            boolean con = ftpUtil.connect(this.ipAddress, this.ftpUser, this.ftpPassword, "SJIS");
            if (con) {
                String srcDir = this.serialNumber;
                if (ftpUtil.exists(srcDir, this.ftpFileName)) {
                    if (ftpUtil.get(srcDir, this.ftpFileName, dstDir, this.ftpFileName)) {
                        resultValue = "0";
                    }
                }
                ftpUtil.close();
            }

            // RESULT を返す。
            cmd = new BhtCommand(BhtCommand.BHT_RESULT, this.serialNumber, this.ipAddress, this.modelCode, resultValue, "", "", "");

            String bhtResultString = BhtCommandConverter.BhtCommandDecoder(cmd);

            StringBuilder sb2 = new StringBuilder();
            sb2.append(BhtCommand.BHT_STX);
            sb2.append(bhtResultString);
            sb2.append(BhtCommand.BHT_ETX);
            this.send(sb2.toString());

            ret = Integer.valueOf(resultValue);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            execCommand = null;
        }
        return ret;
    }

    /**
     * メッセージを送信する。
     *
     * @param message メッセージ
     * @throws Exception 
     */
    private void send(String message) throws Exception {
        logger.info("send:{}", message);
        this.channel.writeAndFlush(message);
    }

    /**
     * 
     * @param chc
     * @param cause 
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext chc, Throwable cause) {
        logger.info("exceptionCaught:{},{},{}", chc.channel().hashCode(), chc.channel(), cause);
        chc.close();
    }

    /**
     * 
     */
    public void closeAllChannel() {
        logger.info("close all channels");
        if (Objects.nonNull(this.channel)) {
            this.channel.eventLoop().shutdownGracefully();
            this.channel = null;
        }
    }
}
