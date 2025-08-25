package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker;

import adtekfuji.locale.LocaleUtils;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.mail.MailSender;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualAdProduct.VirtualAdProduct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.utility.MessageUtility;

/**
 * 作業の状態
 */
public abstract class WorkStatus {
    static protected final Logger logger = LogManager.getLogger(); // ログ出力用クラス
    static protected final Optional<MailSender> mailSender = MailSender.getInstance();

    final BooleanSupplier connect;
    final BooleanSupplier disconnect;


    protected WorkStatus(BooleanSupplier connect, BooleanSupplier disconnect) {
        this.connect = connect;
        this.disconnect = disconnect;
    }

    boolean connect(VirtualAdProduct virtualAdProduct) {
        return this.connect.getAsBoolean();
    }

    boolean disconnect(VirtualAdProduct virtualAdProduct) {
        return this.disconnect.getAsBoolean();
    }

    abstract WorkStatus command(VirtualAdProduct virtualAdProduct, IWorkStatusCommand command);

    /**
     * 作業中状態クラス
     */
    private static class WorkingStatus extends WorkStatus {
        List<Long> workKanbanIds;

        /**
         * 作業中状態クラスのコンストラクタ
         * 
         * @param connect
         * @param disconnect 
         */
        private WorkingStatus(BooleanSupplier connect, BooleanSupplier disconnect, List<Long> workKanbanIds) {
            super(connect, disconnect);
            this.workKanbanIds = workKanbanIds;
        }

        @Override
        public boolean disconnect(VirtualAdProduct virtualAdProduct)
        {
            logger.debug("WorkingStatus.disconnect Start {}", 
                    MessageUtility.getLoginToInformation(virtualAdProduct.getEquipmentIdentify(),
                                                         virtualAdProduct.getOrganizationIdentify()));
            
            // 作業中断処理
            final ActualProductReportResult result = virtualAdProduct.suspendWork(workKanbanIds);
            if (!ServerErrorTypeEnum.SUCCESS.equals(result.getResultType())) {
                String mailMsg = LocaleUtils.getString("key.MailMsg.kanbanInterruptionNg")
                        + MessageUtility.createAnalyzeActualProductReportResult(result, workKanbanIds, virtualAdProduct);
                
                logger.warn(mailMsg);
                mailSender.ifPresent(mail -> mail.send(LocaleUtils.getString("key.MailTitle.kanbanInterruptionNg"), mailMsg));
                // 中断異常
            }

            logger.debug("WorkingStatus.disconnect End {}", 
                    MessageUtility.getLoginToInformation(virtualAdProduct.getEquipmentIdentify(),
                                                         virtualAdProduct.getOrganizationIdentify()));

            return super.disconnect(virtualAdProduct);
        }

        @Override
        WorkStatus command(VirtualAdProduct virtualAdProduct, IWorkStatusCommand command) {
            return command.applyWorkingState(virtualAdProduct, workKanbanIds);
        }
    }

    /**
     * 新しい作業中状態クラスを取得
     * 
     * @param connect
     * @param disconnect
     * @param workKanbanIds
     * @return 新しい作業中状態クラス
     */
    public static WorkStatus getWorkingStatus(BooleanSupplier connect, BooleanSupplier disconnect, List<Long> workKanbanIds) {
        return new WorkingStatus(connect, disconnect, workKanbanIds);
    }

    /**
     * 指示待ち状態クラス
     */
    private static class WaitInstructionStatus extends WorkStatus {
        
        /**
         * 指示待ち状態クラスのコンストラクタ
         * 
         * @param connect
         * @param disconnect 
         */
        private WaitInstructionStatus(BooleanSupplier connect, BooleanSupplier disconnect) {
            super(connect, disconnect);
        }

        @Override
        WorkStatus command(VirtualAdProduct virtualAdProduct, IWorkStatusCommand command) {
            return command.applyWaitInstructionState(virtualAdProduct);
        }
    }

    /**
     * 新しい指示待ち状態クラスを取得
     * 
     * @param connect
     * @param disconnect
     * @return 新しい指示待ち状態クラス
     */
    public static WorkStatus getWaitInstructionStatus(BooleanSupplier connect, BooleanSupplier disconnect) {
        return new WaitInstructionStatus(connect, disconnect);
    }

    /**
     * 中断中状態クラス
     */
    private static class SuspendStatus extends WorkStatus {
        
        /**
         * 中断中状態クラスのコンストラクタ
         * 
         * @param connect
         * @param disconnect 
         */
        private SuspendStatus(BooleanSupplier connect, BooleanSupplier disconnect) {
            super(connect, disconnect);
        }

        @Override
        WorkStatus command(VirtualAdProduct virtualAdProduct, IWorkStatusCommand command) {
            return command.applySuspendState(virtualAdProduct);
        }
    }

    /**
     * 新しい中断中状態クラスを取得
     * 
     * @param connect
     * @param disconnect
     * @return 新しい中断中状態クラス
     */
    public static WorkStatus getSuspendStatus(BooleanSupplier connect, BooleanSupplier disconnect) {
        return new SuspendStatus(connect, disconnect);
    }
}
