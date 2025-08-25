/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adreporter.beans;

import java.rmi.Remote;
import java.rmi.RemoteException;
import jp.adtekfuji.adFactory.adreporter.info.DisposalSlipInfo;
import jp.adtekfuji.adFactory.enumerate.OutputReportResultEnum;

/**
 * 帳票発行
 *
 * @author nar-nakamura
 */
public interface ReportBean extends Remote {

    /**
     * 廃棄伝票発行
     *
     * @param disposalSlipInfo 廃棄伝票情報
     * @return 出力結果
     * @throws RemoteException
     */
    public OutputReportResultEnum outputDisposal(DisposalSlipInfo disposalSlipInfo) throws RemoteException;
}
