package adtekfuji.admanagerapp.ledgermanagerplugin.utils;


import adtekfuji.cash.CashManager;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.directwork.ActualAddInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LedgerTagUtils {
    static LedgerTagUtils ledgerTagUtils = null;
    CashManager cache = CashManager.getInstance();

    LedgerTagUtils() {
        cache = CashManager.getInstance();
        CacheUtils.createCacheOrganization(true);
    }

    public static LedgerTagUtils getInstance() {
        if (Objects.isNull(ledgerTagUtils)) {
            ledgerTagUtils = new LedgerTagUtils();
        }
        return ledgerTagUtils;
    }
    static Pattern p1 = Pattern.compile("\\(.*\\)");
    private static String convertDate(String format, Date date) {
        Matcher m1 = p1.matcher(format);
        SimpleDateFormat sdf;
        if (m1.find()) {
            String text = m1.group();
            sdf = new SimpleDateFormat(text.substring(1, text.length() - 1));
        } else {
            sdf = new SimpleDateFormat();
        }
        return sdf.format(date);
    }


    public String getVale(List<ActualResultEntity> actualResultEntity, String tag) {
        switch (tag) {
            case "TAG_WORK_NAME":
                if (actualResultEntity.size() == 1) {
                    return actualResultEntity.get(0).getWorkName();
                } else {
                    return String.format("%s(+%d)", actualResultEntity.get(0).getWorkName(), actualResultEntity.size()-1);
                }
            case "TAG_WORK_ORGANIZATION":
                return actualResultEntity.get(0).getOrganizationName();
            case "TAG_WORK_EQUIPMENT":
                return actualResultEntity.get(0).getEquipmentName();
            case "TAG_WORKFLOW_NAME": {
                WorkflowInfoEntity wf = (WorkflowInfoEntity) cache.getItem(WorkflowInfoEntity.class, actualResultEntity.get(0).getFkWorkflowId());
                return Objects.isNull(wf) ? "" : wf.getWorkflowName();
            }
            case "TAG_WORKFLOW_REVISION": {
                WorkflowInfoEntity wf = (WorkflowInfoEntity) cache.getItem(WorkflowInfoEntity.class, actualResultEntity.get(0).getFkWorkflowId());
                return Objects.isNull(wf) ? "" : wf.getWorkflowRevision();
            }
        }

        if (tag.contains("TAG_WORK_ACTUAL_END")) {
            return convertDate(tag, actualResultEntity.get(0).getImplementDatetime());
        }

        if (tag.contains("TAG_WORKFLOW_UPDATE_DATE")) {
            WorkflowInfoEntity wf = (WorkflowInfoEntity) cache.getItem(WorkflowInfoEntity.class, actualResultEntity.get(0).getFkWorkflowId());
            if (Objects.isNull(wf)) {
                return "";
            }
            return convertDate(tag, wf.getUpdateDatetime());
        }

        return "";
    }
}
