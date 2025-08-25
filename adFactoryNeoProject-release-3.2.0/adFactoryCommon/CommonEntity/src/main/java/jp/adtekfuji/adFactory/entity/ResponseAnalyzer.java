/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import static jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum.DIFFERENT_VER_INFO;
import static jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum.IDENTNAME_OVERLAP;

/**
 *
 * @author e-mori
 */
public class ResponseAnalyzer {

    private final static SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale");

    private ResponseAnalyzer() {
    }

    /**
     * getAnalyzeResult
     *
     * @param response
     * @return
     */
    public static boolean getAnalyzeResult(ResponseEntity response) {
        if (Objects.isNull(response) || Objects.isNull(response.isSuccess())) {
            String message = Objects.nonNull(response.getException()) ? "理由: " + response.getException().getMessage() : "サーバーの状態を確認してください";
            sc.showAlert(Alert.AlertType.ERROR, "エラー", LocaleUtils.getString("key.alert.communicationServer") + "\r\n\r\n" + message);
            return false;
        }
        
        if (response.isSuccess()) {
            return true;
        }
        
        Platform.runLater(() -> {

            switch (response.getErrorType()) {
                case NAME_OVERLAP:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.AddErrNameOverLap"));
                    break;
                case IDENTNAME_OVERLAP:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.AddErrIdentNameOrverLap"));
                    break;
                case NOTFOUND_UPDATE:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.UpdateErrNotFoundUpdate"));
                    break;
                case NOTFOUND_DELETE:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DeleteErrNotFountDelete"));
                    break;
                case NOTFOUND_PARENT:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.AddErrNotFoundParent"));
                    break;
                case EXIST_HIERARCHY_DELETE:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DeleteErrExistHierarchDelete"));
                    break;
                case EXIST_CHILD_DELETE:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DeleteErrExistChildDelete"));
                    break;
                case PROTCTED_DATA:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DeleteErrProtectedData"));
                    break;
                //case NOT_PERMIT_EQUIPMENT:
                //  sc.showAlert(Alert.AlertType.WARNING, null, LocaleUtils.getString("key.LoginErrNotmatchPassword"));
                //  break;
                case NOT_LOGINID_ORGANIZATION:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.organizationLogin"));
                    break;
                case NOT_AUTH_ORGANIZATION:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.LoginErrNotmatchPassword"));
                    break;
                case NOT_PERMIT_ORGANIZATION:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.LoginErrNotPermitOrganization"));
                    break;
                case THERE_START_NON_EDITABLE:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.KanbanEditThereStart"));
                    break;
                case THERE_START_NON_DELETABLE:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.KanbanDeleteThereStart"));
                    break;
                case LICENSE_ERROR:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.licenseError"), LocaleUtils.getString("key.alert.licenseError.details"));
                    break;
                case NOT_DELETE_SYSTEM_ADMIN:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DeleteErrProtectedData"));
                    break;
                case OVER_MAX_VALUE:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.overMaxValue"));
                    break;
                case LOGIN_LDAP_EXCEPTION:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.LoginLdapException"));
                    break;
                case INVALID_ARGUMENT:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.invalid.value"));
                    break;
                case NOTFOUND_PRODUCT:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.PartsNotFound"));
                    break;
                case PARTS_NO_OVERLAP:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.PartsNoOverlap"));
                    break;
                case MATERIAL_NON_EDITABLE:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), "すでに出庫されているため、更新できません。");
                    break;
                case DIFFERENT_VER_INFO:
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.differentVerInfo"));
                    break;
                default:
                    break;
            }
        });
        return false;
    }

    /**
     * getAnalyzeActualProductReportResult
     *
     * @param result
     * @return
     */
    public static boolean getAnalyzeActualProductReportResult(ActualProductReportResult result) {
        if (Objects.isNull(result) || Objects.isNull(result.getResultType())) {
            sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.OutReportTitle"), rb.getString("key.ServerProblemMessage"));
            return false;
        }
        if (result.getResultType().equals(ServerErrorTypeEnum.SUCCESS)) {
            return true;
        }
        
        Platform.runLater(() -> {
            switch (result.getResultType()) {
                case NOTFOUND_KANBAN:
                    sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.OutReportTitle"), rb.getString("key.alert.notfound.kanbanError.details"));
                    break;
                case ALREADY_WORKING_ORGANIZATION:
                    sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.OutReportTitle"), rb.getString("key.alert.AlreadyWorkingOrganization"));
                    break;
                case NOTFOUND_WORKKANBAN:
                    sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.OutReportTitle"), rb.getString("key.alert.notfound.workkanbanError.details"));
                    break;
                case ACTUAL_ADITION_ADD_ERROR:
                    sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.OutReportTitle"), rb.getString("key.alert.ActualAditionAddError"));
                    break;
                case THERE_WORKING_NON_START:
                    sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.OutReportTitle"), rb.getString("key.alert.ThereWorkingNonStart"));
                    break;
                case THERE_COMPLETED_NON_START:
                    sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.OutReportTitle"), rb.getString("key.alert.ThereCompletedNonStart"));
                    break;
                case THERE_INTERRUPT_NON_START:
                    sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.OutReportTitle"), rb.getString("key.alert.ThereInterruptNonStart"));
                    break;
                default:
                    sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.OutReportTitle"), rb.getString("key.ServerProblemMessage"));
                    break;
            }
        });
        return false;
    }
}
