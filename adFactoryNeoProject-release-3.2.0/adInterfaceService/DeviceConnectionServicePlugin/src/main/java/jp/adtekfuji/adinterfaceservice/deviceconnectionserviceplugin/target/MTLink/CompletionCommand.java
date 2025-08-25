package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.target.MTLink;

import adtekfuji.locale.LocaleUtils;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.mail.MailSender;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualAdProduct.VirtualAdProduct;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker.IWorkStatusCommand;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker.WorkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.utility.MessageUtility;

/**
 * 完了コマンド
 */
public class CompletionCommand implements IWorkStatusCommand {
    static private final Logger logger = LogManager.getLogger(); // ログ出力用クラス
    static private final Optional<MailSender> mailSender = MailSender.getInstance();

    final BooleanSupplier connect;
    final BooleanSupplier disconnect;
    final Date compDateTime;

    /**
     * コンストラクタ
     * 
     * @param connect
     * @param disconnect 
     */
    public CompletionCommand(BooleanSupplier connect, BooleanSupplier disconnect, Date compDateTime) {
        this.connect = connect;
        this.disconnect = disconnect;
        this.compDateTime = compDateTime;
    }

    /**
     * 作業中 -(完了)-> 指示待ち
     *
     * @param virtualAdProduct 仮想adProduct
     * @param workKanbanIds    工程カンバンID群
     * @return 工程状態
     */
    @Override
    public WorkStatus applyWorkingState(VirtualAdProduct virtualAdProduct, List<Long> workKanbanIds) {
        logger.info("******** Work -(Comp)-> Wait {} {}", virtualAdProduct.getOrganizationIdentify(), virtualAdProduct.getEquipmentIdentify());

        logger.debug("CompletionCommand.applyWorkingState Start {}", 
                MessageUtility.getLoginToInformation(virtualAdProduct.getEquipmentIdentify(),
                                                     virtualAdProduct.getOrganizationIdentify()));

        // 前回の作業を完了させる。
        ActualProductReportResult result = virtualAdProduct.compWork(workKanbanIds, compDateTime);
        if (!ServerErrorTypeEnum.SUCCESS.equals(result.getResultType())) {
            String mailMsg = LocaleUtils.getString("key.MailMsg.kanbanEndNg")
                    + MessageUtility.createAnalyzeActualProductReportResult(result, workKanbanIds, virtualAdProduct);

            logger.warn(mailMsg);
            mailSender.ifPresent(mail -> mail.send(LocaleUtils.getString("key.MailTitle.kanbanEndNg"), mailMsg));
            logger.info("******** Wait -(Start)-x-> Waite {} {}", virtualAdProduct.getOrganizationIdentify(), virtualAdProduct.getEquipmentIdentify());
        }

        logger.debug("CompletionCommand.applyWorkingState End {}", 
                MessageUtility.getLoginToInformation(virtualAdProduct.getEquipmentIdentify(),
                                                     virtualAdProduct.getOrganizationIdentify()));
        
        // 自動対処不可能の為、完了の成否にかかわらず完了にする。
        return WorkStatus.getWaitInstructionStatus(connect, disconnect);
    }

    /**
     * 指示待ち -(完了)-> 指示待ち
     *
     * @param virtualAdProduct 仮想adProduct
     * @return 工程状態
     */
    @Override
    public WorkStatus applyWaitInstructionState(VirtualAdProduct virtualAdProduct) {
        logger.info("******** Wait -(Comp)-> Wait {} {}", virtualAdProduct.getOrganizationIdentify(), virtualAdProduct.getEquipmentIdentify());
        // ワーニングログを残す
        String mailMsg = LocaleUtils.getString("key.MailMsg.kanbanEndWarning")
                + MessageUtility.createAnalyzeActualProductReportResult(null, null, virtualAdProduct);
        logger.warn(mailMsg);

        return WorkStatus.getWaitInstructionStatus(connect, disconnect);
    }

    /**
     * 指示待ち -(中断)-> 指示待ち (在りえない)
     *
     * @param virtualAdProduct 仮想adProduct
     * @return 工程状態
     */
    @Override
    public WorkStatus applySuspendState(VirtualAdProduct virtualAdProduct) {
        return null;
    }
}
