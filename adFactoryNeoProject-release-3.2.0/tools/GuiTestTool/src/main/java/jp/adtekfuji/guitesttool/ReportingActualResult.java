/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.guitesttool;

import adtekfuji.clientservice.KanbanInfoFacade;
import java.util.Date;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class ReportingActualResult {

    private static ReportingActualResult instance = null;
    private final Logger logger = LogManager.getLogger();
    private final String applicationIdentName = "GUI_TEST_TOOL";
    private final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
    private Long tid = 0L;

    private ReportingActualResult() {
    }

    public static ReportingActualResult getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ReportingActualResult();
        }
        return instance;
    }

    public Boolean report(Long kanbanId, Long workKanbanId, KanbanStatusEnum status, Long equipmentId, Long organizationId) {
        logger.info("report:{},{},{}", kanbanId, workKanbanId, status);
        ActualProductReportEntity actual = new ActualProductReportEntity(tid, kanbanId, workKanbanId, applicationIdentName, new Date(), status, null, null);
        actual.setEquipmentId(equipmentId);
        actual.setOrganizationId(organizationId);
        ActualProductReportResult result = kanbanInfoFacade.report(actual);
        if (result.getResultType() != ServerErrorTypeEnum.SUCCESS) {
            logger.info("report err!! :{}", result);
            return false;
        }
        tid = result.getNextTransactionID();
        return true;
    }

}
