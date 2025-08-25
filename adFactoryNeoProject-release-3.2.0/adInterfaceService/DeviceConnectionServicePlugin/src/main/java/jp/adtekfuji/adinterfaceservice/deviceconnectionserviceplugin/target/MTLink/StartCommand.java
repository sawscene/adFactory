package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.target.MTLink;

import adtekfuji.locale.LocaleUtils;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.entity.search.AddInfoSearchCondition;
import jp.adtekfuji.adFactory.entity.search.ProducibleWorkKanbanCondition;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.mail.MailSender;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualAdProduct.VirtualAdProduct;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker.IWorkStatusCommand;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker.WorkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.*;
import java.util.function.BooleanSupplier;
import static java.util.stream.Collectors.*;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.utility.MessageUtility;

/**
 * 開始コマンド
 */
public class StartCommand implements IWorkStatusCommand {
    static private final Logger logger = LogManager.getLogger(); // ログ出力用クラス
    static private final Optional<MailSender> mailSender = MailSender.getInstance();

    final BooleanSupplier connect;
    final BooleanSupplier disconnect;
    final Long programNumber;           // プログラム名

    final Long workNumber; // 同時加工数

    final Date startDateTime;

    /**
     * コンストラクタ
     * 
     * @param connect
     * @param disconnect
     * @param programNumber プログラム名
     */
    public StartCommand(BooleanSupplier connect, BooleanSupplier disconnect, Long programNumber, Long workNumber, Date startDateTime)
    {
        this.connect = connect;
        this.disconnect = disconnect;
        this.programNumber = programNumber;
        this.startDateTime = startDateTime;
        this.workNumber = (Objects.isNull(workNumber) || workNumber == 0) ? 1L : workNumber;
    }

    // 作業中 -(開始)-> 作業中
    @Override
    public WorkStatus applyWorkingState(VirtualAdProduct virtualAdProduct, List<Long> workKanbanIds)
    {
        logger.info("******** Work -(Start)-> Work {} {}", virtualAdProduct.getOrganizationIdentify(), virtualAdProduct.getEquipmentIdentify());

        logger.debug("StartCommand.applyWorkingState Start {}",
                MessageUtility.getLoginToInformation(virtualAdProduct.getEquipmentIdentify(),
                                                     virtualAdProduct.getOrganizationIdentify()));

        // 前回の作業を完了させる。
        ActualProductReportResult result = virtualAdProduct.compWork(workKanbanIds, this.startDateTime);
        if (!ServerErrorTypeEnum.SUCCESS.equals(result.getResultType())) {
            String mailMsg = LocaleUtils.getString("key.MailMsg.kanbanEndNg")
                    + MessageUtility.createAnalyzeActualProductReportResult(result, workKanbanIds, virtualAdProduct);

            logger.warn(mailMsg);
            mailSender.ifPresent(mail -> mail.send(LocaleUtils.getString("key.MailTitle.kanbanEndNg"), mailMsg));
        }

        logger.debug("StartCommand.applyWorkingState End {}", 
                MessageUtility.getLoginToInformation(virtualAdProduct.getEquipmentIdentify(),
                                                     virtualAdProduct.getOrganizationIdentify()));
        
        // 新しいカンバンを実行
        return applyWaitInstructionState(virtualAdProduct);
    }

    // 指示待ち -(開始)-> 作業中
    @Override
    public WorkStatus applyWaitInstructionState(VirtualAdProduct virtualAdProduct)
    {
        logger.info("******** Wait -(Start)-> Work {} {}", virtualAdProduct.getOrganizationIdentify(), virtualAdProduct.getEquipmentIdentify());

        logger.debug("StartCommand.applyWaitInstructionState Start {}", 
                MessageUtility.getLoginToInformation(virtualAdProduct.getEquipmentIdentify(),
                                                     virtualAdProduct.getOrganizationIdentify()));
        
        // 工程カンバンの検索条件指定
        ProducibleWorkKanbanCondition condition = new ProducibleWorkKanbanCondition();
        condition.setEquipmentCollection(Collections.singletonList(virtualAdProduct.getLoginEquipmentId()));
        condition.setOrganizationCollection(Collections.singletonList(virtualAdProduct.getLoginOrganizationId()));
        condition.setAddInfoSearchConditions(Collections.singletonList(
                new AddInfoSearchCondition(
                        "プログラム番号",
                        String.format("O0*%d(;|$)", this.programNumber),
                        AddInfoSearchCondition.SEARCH_TYPE.REGX
                )));

        // 作業するカンバンIDを取得
        List<WorkKanbanInfoEntity>  workKanbanInfoEntities = virtualAdProduct.searchProductWorkKanban(condition, 0, this.workNumber-1);
        if (workKanbanInfoEntities.isEmpty()) {
            String mailMsg = LocaleUtils.getString("key.MailMsg.kanbanIdNot")
                    + MessageUtility.createAnalyzeActualProductReportResult(virtualAdProduct, condition);
            logger.warn(mailMsg);
            mailSender.ifPresent(mail -> mail.send(LocaleUtils.getString("key.MailTitle.kanbanIdNot"), mailMsg));
            logger.info("******** Wait -(Start)-xWork-> Wait {} {}", virtualAdProduct.getOrganizationIdentify(), virtualAdProduct.getEquipmentIdentify());
            // 作業カンバン無し
            return WorkStatus.getWaitInstructionStatus(connect, disconnect);
        }
        
        List<Long> nextWorkKanbanIds = workKanbanInfoEntities.stream().map(WorkKanbanInfoEntity::getWorkKanbanId).collect(toList());
        ActualProductReportResult productReportResult = virtualAdProduct.startWork(nextWorkKanbanIds, this.startDateTime);
        if (!ServerErrorTypeEnum.SUCCESS.equals(productReportResult.getResultType())) {
            // 開始失敗
            String mailMsg = LocaleUtils.getString("key.MailMsg.kanbanStartNg")
                    + MessageUtility.createAnalyzeActualProductReportResult(productReportResult, nextWorkKanbanIds, virtualAdProduct);

            logger.warn(mailMsg);
            mailSender.ifPresent(mail -> mail.send(LocaleUtils.getString("key.MailTitle.kanbanStartNg"), mailMsg));
            logger.info("******** Wait -(Start)-xWork-> Wait {} {}", virtualAdProduct.getOrganizationIdentify(), virtualAdProduct.getEquipmentIdentify());
            return WorkStatus.getWaitInstructionStatus(connect, disconnect);
        }

        logger.debug("StartCommand.applyWaitInstructionState End {}", 
                MessageUtility.getLoginToInformation(virtualAdProduct.getEquipmentIdentify(),
                                                     virtualAdProduct.getOrganizationIdentify()));

        // 作業中状態に遷移
        return WorkStatus.getWorkingStatus(connect, disconnect, nextWorkKanbanIds);
    }

    // 作業中 -(中断)-> 作業中 (在りえない)
    @Override
    public WorkStatus applySuspendState(VirtualAdProduct virtualAdProduct)
    {
        return null;
    }
}
