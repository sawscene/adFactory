/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.entity;

import adtekfuji.locale.LocaleUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.kanban.ApprovalEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * カンバンリスト表示用データ
 *
 * @author nar-nakamura
 */
public class DisplayData {

    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static final SimpleDateFormat APPROVE_DATE_FORMAT = new SimpleDateFormat("M/d");

    private final Long id;
    private final Long workflowId;

    private String kanbanName = "";
    private String workflowName = "";
    private String modelName = "";
    private String productNo = "";
    private String status = "";
    private String updatePerson = ""; // 更新者
    private String updateDatetime = ""; // 更新日時

    /**
     * ラベルID一覧
     */
    private List<Long> labelIds = new ArrayList<>();

    private String startDate = "";
    private String endDate = "";
    private final KanbanInfoEntity entity;
    private List<ApprovalEntity> approve = new ArrayList<>();

    /**
     * カンバンリスト表示用データ
     *
     * @param entity カンバン情報
     * @param isLiteMode true: [Lite]カンバン編集データ、false: カンバン編集データ
     */
    public DisplayData(KanbanInfoEntity entity, boolean isLiteMode) {
        SimpleDateFormat formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
      
        this.id = entity.getKanbanId();
        this.workflowId = entity.getFkWorkflowId();

        // カンバン名
        this.kanbanName = entity.getKanbanName();

        // 工程順名
        if (Objects.nonNull(entity.getWorkflowName())) {
            this.workflowName = entity.getWorkflowName();
        }

        // 工程順 : 版数
        if (Objects.nonNull(entity.getWorkflowRev()) && !isLiteMode) {
            this.workflowName = this.workflowName + " : " + entity.getWorkflowRev().toString();
        }

        // モデル名
        if (Objects.nonNull(entity.getModelName())) {
            this.modelName = entity.getModelName();
        }

        // 製造番号
        if (Objects.nonNull(entity.getProductionNumber())) {
            this.productNo = entity.getProductionNumber();
        }

        // ステータス
        this.status = LocaleUtils.getString(entity.getKanbanStatus().getResourceKey());

        // カンバンラベルID一覧(JSON)
        if (Objects.nonNull(entity.getKanbanLabel())) {
            this.labelIds.addAll(JsonUtils.jsonToObjects(entity.getKanbanLabel(), Long[].class));
        }

        // 作業開始日時
        if (Objects.nonNull(entity.getStartDatetime())) {
            this.startDate = formatter.format(entity.getStartDatetime());
        }

        // 作業終了日時
        if (Objects.nonNull(entity.getCompDatetime())) {
            this.endDate = formatter.format(entity.getCompDatetime());
        }

        // 承認(JSON)
        if (Objects.nonNull(entity.getApproval())) {
            this.approve.addAll(JsonUtils.jsonToObjects(entity.getApproval(), ApprovalEntity[].class));
        }

        // 更新者
        if (Objects.nonNull(entity.getUpdatePerson())) {
            this.updatePerson = entity.getUpdatePerson();
        }

        // 更新日時
        if (Objects.nonNull(entity.getUpdateDatetime())) {
            this.updateDatetime = formatter.format(entity.getUpdateDatetime());
        }

        this.entity = entity;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getId() {
        return this.id;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getWorkflowId() {
        return this.workflowId;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getProductNo() {
        return this.productNo;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
        return this.workflowName;
    }

    /**
     * ステータスを取得する。
     *
     * @return ステータス
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * カンバンラベルID一覧を取得する。
     *
     * @return カンバンラベルID一覧
     */
    public List<Long> getLabelIds() {
        return this.labelIds;
    }

    /**
     * 作業開始日時を取得する。
     *
     * @return 作業開始日時
     */
    public String getStartDate() {
        return this.startDate;
    }

    /**
     * 作業終了日時を取得する。
     *
     * @return 作業終了日時
     */
    public String getEndDate() {
        return this.endDate;
    }

    /**
     * 承認情報を取得する。
     *
     * @return 承認情報
     */
    public List<ApprovalEntity> getApprove() {
        return this.approve;
    }

    /**
     * 指定した順の承認情報プロパティを取得する。
     *
     * @param order 順
     * @return 承認情報
     */
    public StringProperty approveInfoProperty(int order) {
        try {
            ApprovalEntity item = this.approve.stream()
                    .filter(p -> Objects.equals(p.getOrder(), order))
                    .findFirst()
                    .orElse(null);

            StringBuilder sb = new StringBuilder();
            if (Objects.isNull(item.getApprove())) {
                //　取り消した場合
            } else if (item.getApprove()) {
                // 可承認
                sb.append(item.getApprover());
                sb.append(" ");
                sb.append(APPROVE_DATE_FORMAT.format(item.getDate()));
            } else {
                // 否承認
                sb.append(LocaleUtils.getString("key.DisapprovalStatus"));
            }

            return new SimpleStringProperty(sb.toString());

        } catch (Exception ex) {
            return new SimpleStringProperty();
        }
    }

    /**
     * 更新者を取得する。
     *
     * @return 更新者
     */
    public String getUpdatePerson() {
        return this.updatePerson;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public String getUpdateDatetime() {
        return this.updateDatetime;
    }

    /**
     * カンバン情報を取得する。
     *
     * @return カンバン情報
     */
    public KanbanInfoEntity getEntity() {
        return this.entity;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DisplayData other = (DisplayData) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("DisplayData{")
                .append("id=").append(this.id)
                .append(", workflowId=").append(this.workflowId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", workflowName=").append(this.workflowName)
                .append(", modelName=").append(this.modelName)
                .append(", productNo=").append(this.productNo)
                .append(", status=").append(this.status)
                .append(", startDate=").append(this.startDate)
                .append(", endDate=").append(this.endDate)
                .append(", updatePerson=").append(this.updatePerson)
                .append(", updateDatetime=").append(this.updateDatetime)
                .toString();
    }
}
