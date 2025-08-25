package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker;

import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualAdProduct.VirtualAdProduct;

import java.util.List;

/**
 * ステータスコマンド
 */
public interface IWorkStatusCommand {
    /**
     * 作業中状態処理実施
     * @param virtualAdProduct 仮想adProduct
     * @param workKanbanIds 工程カンバンID群
     * @return 次の状態
     */
    WorkStatus applyWorkingState(VirtualAdProduct virtualAdProduct, List<Long> workKanbanIds);

    /**
     * 待ち状態処理実施
     * @param virtualAdProduct 仮想adProduct
     * @return 次の状態
     */
    WorkStatus applyWaitInstructionState(VirtualAdProduct virtualAdProduct);

    /**
     * 中断中処理実施
     * @param virtualAdProduct 仮想adProduct
     * @return 次の状態
     */
    WorkStatus applySuspendState(VirtualAdProduct virtualAdProduct);
}
