package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.target.MTLink;

import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualAdProduct.VirtualAdProduct;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker.WorkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.*;
import java.util.function.BooleanSupplier;

/**
 * 開始コマンド
 */
public class StartCounterContinueCommand extends StartCommand {
    static private final Logger logger = LogManager.getLogger(); // ログ出力用クラス

    /**
     * コンストラクタ
     * 
     * @param connect
     * @param disconnect
     * @param programNumber プログラム名
     */
    public StartCounterContinueCommand(BooleanSupplier connect, BooleanSupplier disconnect, Long programNumber, Long workNumber, Date startDateTime)
    {
        super(connect, disconnect, programNumber, workNumber, startDateTime);
    }

    // 作業中 -(開始)-> 作業中 (工程は切り替えない)
    @Override
    public WorkStatus applyWorkingState(VirtualAdProduct virtualAdProduct, List<Long> workKanbanIds)
    {
        logger.info("******** Work -(Start)-> Work {} {}", virtualAdProduct.getOrganizationIdentify(), virtualAdProduct.getEquipmentIdentify());
        return WorkStatus.getWorkingStatus(connect, disconnect, workKanbanIds);
    }
}
