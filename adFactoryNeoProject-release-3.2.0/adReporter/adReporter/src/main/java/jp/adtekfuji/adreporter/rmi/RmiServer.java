/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adreporter.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import jp.adtekfuji.adFactory.adreporter.beans.ReportBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * RMIサーバー
 *
 * @author nar-nakamura
 */
public class RmiServer {

    private final Logger logger = LogManager.getLogger();

    private Registry rmiRegistry = null;

    private final String RMI_REGISTRY_NAME = "adReporter";
    private final ReportBeanImpl reportBeanImpl = new ReportBeanImpl();

    private int rmiPort = Registry.REGISTRY_PORT;

    private static RmiServer instance;

    /**
     * RMIサーバーのインスタンスを取得する。
     *
     * @return RMIサーバー
     */
    public static RmiServer getInstance() {
        if (Objects.isNull(instance)) {
            instance = new RmiServer();
        }
        return instance;
    }

    /**
     * RMIサーバーのポート番号を取得する。
     *
     * @return ポート番号
     */
    public int getRmiPort() {
        return this.rmiPort;
    }

    /**
     * RMIサーバーのポート番号を設定する。
     *
     * @param rmiPort ポート番号
     */
    public void setRmiPort(int rmiPort) {
        this.rmiPort = rmiPort;
    }

    /**
     * RMIサーバーを開始する。
     */
    public void start() {
        logger.info("start.");
        try {
            ReportBean bean = exportDisposalSlipBean();

            this.rmiRegistry = LocateRegistry.createRegistry(this.rmiPort);
            this.rmiRegistry.rebind(RMI_REGISTRY_NAME, bean);

            logger.info("RMI Server ready.");

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.unexportDisposalSlipBean();
        }
    }

    /**
     * RMIサーバーを停止する。
     */
    public void stop() {
        logger.info("stop.");
        try {
            unexportDisposalSlipBean();

            if (Objects.nonNull(this.rmiRegistry)) {
                this.rmiRegistry.unbind(RMI_REGISTRY_NAME);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 帳票発行ビーンをエクスポートする。
     *
     * @return 
     */
    private ReportBean exportDisposalSlipBean() {
        ReportBean bean = null;
        try {
            bean = (ReportBean) UnicastRemoteObject.exportObject(this.reportBeanImpl, this.rmiPort);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return bean;
    }

    /**
     * 帳票発行ビーンのエクスポートを解除する。
     */
    private void unexportDisposalSlipBean() {
        try {
            if (Objects.nonNull(this.reportBeanImpl)) {
                UnicastRemoteObject.unexportObject(this.reportBeanImpl, true);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
