/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author nar-nakamura
 */
public class TestUtils {
    /**
     * 日時文字列(yyyy/MM/dd HH:mm:ss)を日時に変換する。
     *
     * @param value 日時文字列(yyyy/MM/dd HH:mm:ss)
     * @return 日時
     * @throws Exception 
     */
    public static Date parseDatetime(String value) throws Exception {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return datetimeFormat.parse(value);
    }

    /**
     * カンバン・工程カンバンの開始・完了日時をチェックする。
     *
     * @param kanban カンバン
     * @param startDates 比較用の開始日時一覧
     * @param compDates 比較用の完了日時一覧
     * @param baseTime 比較用の基準開始日時
     * @throws Exception 
     */
    public static void checkPlanDate(KanbanEntity kanban, List<String> startDates, List<String> compDates, Date baseTime) throws Exception {
        // 工程カンバンの開始・完了日時
        for (int i = 0; i < kanban.getWorkKanbanCollection().size(); i++) {
            WorkKanbanEntity workKanban = kanban.getWorkKanbanCollection().get(i);
            assertThat(workKanban.getStartDatetime(), is(parseDatetime(startDates.get(i))));
            assertThat(workKanban.getCompDatetime(), is(parseDatetime(compDates.get(i))));
        }

        // カンバンの開始・完了日時
        assertThat(kanban.getStartDatetime(), is(baseTime));
        assertThat(kanban.getCompDatetime(), is(parseDatetime(compDates.get(kanban.getWorkKanbanCollection().size() - 1))));
    }

    /**
     * 工程順の工程開始・完了日時をチェックする。
     *
     * @param workflow 工程順
     * @param startDates 比較用の開始日時一覧
     * @param compDates 比較用の完了日時一覧
     * @throws Exception 
     */
    public static void checkUpdateTimetablePlanDate(WorkflowEntity workflow, List<String> startDates, List<String> compDates) throws Exception {
        for (int i = 0; i < workflow.getConWorkflowWorkCollection().size(); i++) {
            ConWorkflowWorkEntity conWorkflowWork = workflow.getConWorkflowWorkCollection().get(i);
            assertThat(conWorkflowWork.getStandardStartTime(), is(parseDatetime(startDates.get(i))));
            assertThat(conWorkflowWork.getStandardEndTime(), is(parseDatetime(compDates.get(i))));
        }
    }

    /**
     * 直列の工程順ダイアグラムを作成する。
     *
     * @param works 工程一覧
     * @return 工程順ダイアグラム
     */
    public static String createWorkflowDiaglam(List<WorkEntity> works) {
        StringBuilder sb = new StringBuilder()
            .append("<?xml version=\"1.0\" encoding=\"Shift_JIS\" standalone=\"yes\"?>")
            .append("<definitions targetNamespace=\"http://www.adtek-fuji.co.jp/adfactory\">")
            .append("<process isExecutable=\"true\">")
            .append("<startEvent id=\"start_id\" name=\"start\"/>")
            .append("<endEvent id=\"end_id\" name=\"end\"/>");

        for (WorkEntity work : works) {
            sb.append("<task id=\"").append(work.getWorkId()).append("\" name=\"").append(work.getWorkName()).append("\"/>");
        }

        sb.append("<sequenceFlow sourceRef=\"start_id\"");

        for (WorkEntity work : works) {
            String ref = String.valueOf(work.getWorkId());
            String id = String.format("id_%d", work.getWorkId());
            sb.append(" targetRef=\"").append(ref).append("\" id=\"").append(id).append("\" name=\"\"/>");
            sb.append("<sequenceFlow sourceRef=\"").append(ref).append("\"");
        }

        sb.append(" targetRef=\"end_id\" id=\"").append("").append("\" name=\"\"/>");
        sb.append("</process>");
        sb.append("</definitions>");

        return sb.toString();
    }

    /**
     * ライセンス状態を変更する。
     *
     * @param type オプションタイプ
     * @param isEnabled ライセンス状態(true:有効, false:無効, null:設定なし)
     */
    public static void setOptionLicense(LicenseOptionType type, Boolean isEnabled) {
        if (Objects.nonNull(isEnabled)) {
            LicenseManager.getInstance().getLicenseOptions().put(type.getName(), isEnabled);
        } else if (LicenseManager.getInstance().getLicenseOptions().containsKey(type.getName())) {
            LicenseManager.getInstance().getLicenseOptions().remove(type.getName());
        }
    }
}
