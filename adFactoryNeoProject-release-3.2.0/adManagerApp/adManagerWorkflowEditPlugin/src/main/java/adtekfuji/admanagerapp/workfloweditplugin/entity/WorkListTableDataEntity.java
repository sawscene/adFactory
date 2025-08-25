/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.entity;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.entity.approval.ApprovalFlowInfoEntity;
import jp.adtekfuji.adFactory.entity.approval.ApprovalInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;

/**
 *
 * @author ta-ito
 */
public class WorkListTableDataEntity {

    private WorkInfoEntity workInfoEntity = new WorkInfoEntity();
    private String workName;
    private String updatePersonName;
    private String updateDatetime;
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * 承認
     */
    private String approvalState;

    /**
     * 承認日時
     */
    private String approvalDatetime;

    /**
     * コンストラクタ
     *
     * @param entity 工程情報
     */
    public WorkListTableDataEntity(WorkInfoEntity entity) {
        this.workInfoEntity = entity;
        this.workName = entity.getWorkName() + " : " + entity.getWorkRev().toString();
        this.updateDatetime = Objects.isNull(entity.getUpdateDatetime()) ? "" : StringTime.convertDateToString(entity.getUpdateDatetime(), LocaleUtils.getString("key.DateTimeFormat"));

        if (ClientServiceProperty.isLicensed(LicenseOptionType.ApprovalOption.getName())) {
            this.approvalState = getApprovalStateName(entity.getApprovalState());
            if (Objects.nonNull(entity.getApprovalId())) {
                ApprovalInfoEntity approvalInfo = entity.getApproval();
                Date lastApprovalDatetime = getLastApprovalDatetime(approvalInfo);
                this.approvalDatetime = Objects.isNull(lastApprovalDatetime) ? "" : StringTime.convertDateToString(lastApprovalDatetime, LocaleUtils.getString("key.DateTimeFormat"));
            }
        } else {
            this.approvalState = "";
            this.approvalDatetime = "";
        }
    }

    public WorkInfoEntity getWorkInfoEntity() {
        return workInfoEntity;
    }

    public void setWorkInfoEntity(WorkInfoEntity workInfoEntity) {
        this.workInfoEntity = workInfoEntity;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public void setUpdatePersonName(String updatePersonName) {
        this.updatePersonName = updatePersonName;
    }

    public String getUpdatePersonName() {
        return updatePersonName;
    }

    public void setUpdateDatetime(String updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public String getUpdateDatetime() {
        return updateDatetime;
    }

    /**
     * 承認状態を取得する。
     *
     * @return 承認状態
     */
    public String getApprovalState() {
        return approvalState;
    }

    /**
     * 承認状態を設定する。
     *
     * @param approvalState 承認状態
     */
    public void setApprovalState(String approvalState) {
        this.approvalState = approvalState;
    }

    /**
     * 承認日時を取得する。
     *
     * @return 承認日時
     */
    public String getApprovalDatetime() {
        return approvalDatetime;
    }

    /**
     * 承認日時を設定する。
     *
     * @param approvalDatetime 承認日時
     */
    public void setApprovalDatetime(String approvalDatetime) {
        this.approvalDatetime = approvalDatetime;
    }

    /**
     * 最後に承認された承認日時を取得する。
     *
     * @param approvalInfo 申請情報
     * @return 最後に承認された承認日時
     */
    private Date getLastApprovalDatetime(ApprovalInfoEntity approvalInfo) {
        if (Objects.isNull(approvalInfo)) {
            return null;
        }

        if (!ApprovalStatusEnum.APPLY.equals(approvalInfo.getApprovalState())
                && !ApprovalStatusEnum.FINAL_APPROVE.equals(approvalInfo.getApprovalState())) {
            return null;
        }

        List<ApprovalFlowInfoEntity> approvalFlows = approvalInfo.getApprovalFlows();
        if (Objects.isNull(approvalFlows) || approvalFlows.isEmpty()) {
            return null;
        }

        // 承認済み、かつ、承認順が最大の承認フロー情報を取得
        Optional<ApprovalFlowInfoEntity> opt = approvalFlows.stream()
                .filter(p -> ApprovalStatusEnum.APPROVE.equals(p.getApprovalState()) || ApprovalStatusEnum.FINAL_APPROVE.equals(p.getApprovalState()))
                .collect(Collectors.maxBy(Comparator.comparing(ApprovalFlowInfoEntity::getApprovalOrder)));

        if (!opt.isPresent()) {
            return null;
        } else {
            return opt.get().getApprovalDatetime();
        }
    }

    /**
     * 承認状態名を取得する。
     *
     * @param approvalStatus 承認状態
     * @return 承認状態名
     */
    private String getApprovalStateName(ApprovalStatusEnum approvalStatus) {
        String result = "";

        switch (approvalStatus) {
            case UNAPPROVED:
                result = LocaleUtils.getString("key.StateUnapproved");
                break;
            case APPLY:
                result = LocaleUtils.getString("key.StateRequesting");
                break;
            case CANCEL_APPLY:
                result = LocaleUtils.getString("key.StateCanceled");
                break;
            case REJECT:
                result = LocaleUtils.getString("key.StateRejected");
                break;
            case FINAL_APPROVE:
                result = LocaleUtils.getString("key.StateFinalApproved");
                break;
        }

        return result;
    }
}
