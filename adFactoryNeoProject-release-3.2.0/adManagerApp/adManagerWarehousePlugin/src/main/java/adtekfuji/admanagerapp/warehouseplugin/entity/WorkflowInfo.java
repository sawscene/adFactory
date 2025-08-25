/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.entity;

import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.locale.LocaleUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.ResourceBundle;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class WorkflowInfo {

    private static WorkflowInfo instance = null;
    private static final Logger logger = LogManager.getLogger();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private static final String KEY_ACCEPTED = "key.Accepted";
    private static final String KEY_WAREHOUSING = "key.Warehousing";
    private static final String KEY_PAYOUT = "key.Payout";
    private static final String KEY_INVENTORIES = "key.Inventories";
    private static final String KEY_SHELFMOVE = "key.ShelfMove";
    private Long acceptedId = null;
    private Long qarehousingId = null;
    private Long payoutId = null;
    private Long inventoriesId = null;
    private Long shelfMoveId = null;

    private WorkflowInfo() {
    }

    public static WorkflowInfo getInstance() {
        if (Objects.isNull(instance)) {
            instance = new WorkflowInfo();
        }
        return instance;
    }

    private Long updateData(String key) {
        try {
            WorkflowInfoEntity workflow = workflowInfoFacade.findName(URLEncoder.encode(LocaleUtils.getString(key), "UTF-8"));
            if (Objects.nonNull(workflow)) {
                return workflow.getWorkflowId();
            }
        } catch (UnsupportedEncodingException ex) {
            logger.info(ex, ex);
        }
        return null;
    }

    /**
     * 受入工程
     *
     * @return
     */
    public Long getAcceptedId() {
        if (Objects.isNull(acceptedId)) {
            acceptedId = updateData(KEY_ACCEPTED);
        }
        return acceptedId;
    }

    /**
     * 入庫工程
     *
     * @return
     */
    public Long getWarehousingId() {
        if (Objects.isNull(qarehousingId)) {
            qarehousingId = updateData(KEY_WAREHOUSING);
        }
        return qarehousingId;
    }

    /**
     * 払出工程
     *
     * @return
     */
    public Long getPayoutId() {
        if (Objects.isNull(payoutId)) {
            payoutId = updateData(KEY_PAYOUT);
        }
        return payoutId;
    }

    /**
     * 棚卸工程
     *
     * @return
     */
    public Long getInventoriesId() {
        if (Objects.isNull(inventoriesId)) {
            inventoriesId = updateData(KEY_INVENTORIES);
        }
        return inventoriesId;
    }

    /**
     * 棚移動工程
     *
     * @return
     */
    public Long getShelfMoveId() {
        if (Objects.isNull(shelfMoveId)) {
            shelfMoveId = updateData(KEY_SHELFMOVE);
        }
        return shelfMoveId;
    }

}
