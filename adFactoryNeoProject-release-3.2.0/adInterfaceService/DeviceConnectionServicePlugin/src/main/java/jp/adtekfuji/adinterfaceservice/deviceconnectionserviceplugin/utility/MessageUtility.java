/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.utility;

import adtekfuji.locale.LocaleUtils;
import java.util.List;
import java.util.Objects;

import adtekfuji.utility.StringUtils;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginResult;
import jp.adtekfuji.adFactory.entity.search.AddInfoSearchCondition;
import jp.adtekfuji.adFactory.entity.search.ProducibleWorkKanbanCondition;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualAdProduct.VirtualAdProduct;

/**
 * メッセージクラス
 *
 * @author okada
 */
public class MessageUtility {

    /**
     * サーバーエラー種別のエラー内容を取得
     * @param errorType
     * @return 
     */
    public static String getServerErrorTypeResult(ServerErrorTypeEnum errorType) {
        String msg;

        switch (errorType) {
            case NAME_OVERLAP:
                msg = LocaleUtils.getString("key.AddErrNameOverLap");
                break;
            case IDENTNAME_OVERLAP:
                msg = LocaleUtils.getString("key.AddErrIdentNameOrverLap");
                break;
            case NOTFOUND_UPDATE:
                msg = LocaleUtils.getString("key.UpdateErrNotFoundUpdate");
                break;
            case NOTFOUND_DELETE:
                msg = LocaleUtils.getString("key.DeleteErrNotFountDelete");
                break;
            case NOTFOUND_PARENT:
                msg = LocaleUtils.getString("key.AddErrNotFoundParent");
                break;
            case EXIST_HIERARCHY_DELETE:
                msg = LocaleUtils.getString("key.DeleteErrExistHierarchDelete");
                break;
            case EXIST_CHILD_DELETE:
                msg = LocaleUtils.getString("key.DeleteErrExistChildDelete");
                break;
            case PROTCTED_DATA:
                msg = LocaleUtils.getString("key.DeleteErrProtectedData");
                break;
            //case NOT_PERMIT_EQUIPMENT:
            //  sc.showAlert(Alert.AlertType.WARNING, null, LocaleUtils.getString("key.LoginErrNotmatchPassword");
            //  break;
            case NOT_LOGINID_ORGANIZATION:
                msg = LocaleUtils.getString("key.alert.organizationLogin");
                break;
            case NOT_AUTH_ORGANIZATION:
                msg = LocaleUtils.getString("key.LoginErrNotmatchPassword");
                break;
            case NOT_PERMIT_ORGANIZATION:
                msg = LocaleUtils.getString("key.LoginErrNotPermitOrganization");
                break;
            case THERE_START_NON_EDITABLE:
                msg = LocaleUtils.getString("key.KanbanEditThereStart");
                break;
            case THERE_START_NON_DELETABLE:
                msg = LocaleUtils.getString("key.KanbanDeleteThereStart");
                break;
            case LICENSE_ERROR:
                msg = LocaleUtils.getString("key.alert.licenseError.details");
                break;
            case NOT_DELETE_SYSTEM_ADMIN:
                msg = LocaleUtils.getString("key.DeleteErrProtectedData");
                break;
            case OVER_MAX_VALUE:
                msg = LocaleUtils.getString("key.alert.overMaxValue");
                break;
            case LOGIN_LDAP_EXCEPTION:
                msg = LocaleUtils.getString("key.LoginLdapException");
                break;
            case INVALID_ARGUMENT:
                msg = LocaleUtils.getString("key.invalid.value");
                break;
            case NOTFOUND_PRODUCT:
                msg = LocaleUtils.getString("key.PartsNotFound");
                break;
            case PARTS_NO_OVERLAP:
                msg = LocaleUtils.getString("key.PartsNoOverlap");
                break;
            case MATERIAL_NON_EDITABLE:
                msg = "すでに出庫されているため、更新できません。";
                break;
            case NOTFOUND_KANBAN:
                msg = LocaleUtils.getString("key.alert.notfound.kanbanError.details");
                break;
            case ALREADY_WORKING_ORGANIZATION:
                msg = LocaleUtils.getString("key.alert.AlreadyWorkingOrganization");
                break;
            case NOTFOUND_WORKKANBAN:
                msg = LocaleUtils.getString("key.alert.notfound.workkanbanError.details");
                break;
            case ACTUAL_ADITION_ADD_ERROR:
                msg = LocaleUtils.getString("key.alert.ActualAditionAddError");
                break;
            case THERE_WORKING_NON_START:
                msg = LocaleUtils.getString("key.alert.ThereWorkingNonStart");
                break;
            case THERE_COMPLETED_NON_START:
                msg = LocaleUtils.getString("key.alert.ThereCompletedNonStart");
                break;
            case THERE_INTERRUPT_NON_START:
                msg = LocaleUtils.getString("key.alert.ThereInterruptNonStart");
                break;
            default:
                msg = LocaleUtils.getString("key.ServerProblemMessage");
                break;
        }
        return msg;
    }
    
    /**
     * 設備ログインのエラーメッセージを取得
     * 
     * @param equipmentLoginResult 設備ログイン結果情報
     * @param virtualAdProduct 仮想adProduct
     * @return エラーメッセージ
     */
    public static String getAnalyzeEquipmentLoginResult(EquipmentLoginResult equipmentLoginResult, VirtualAdProduct virtualAdProduct) {
                
        return String.format("%s【ErrorType】%s：%s",
                getLoginToInformation(virtualAdProduct.getEquipmentIdentify(),""),
                Objects.isNull(equipmentLoginResult) ? null : equipmentLoginResult.getErrorType().getCode(),
                Objects.isNull(equipmentLoginResult) ? null : getServerErrorTypeResult(equipmentLoginResult.getErrorType()));
    }
        
    /**
     * 組織ログインのエラーメッセージを取得
     * 
     * @param organizationLoginResult 組織ログイン結果情報
     * @param virtualAdProduct 仮想adProduct
     * @return エラーメッセージ
     */
    public static String getAnalyzeOrganizationLoginResult(OrganizationLoginResult organizationLoginResult, VirtualAdProduct virtualAdProduct) {
        
        return String.format("%s【ErrorType】%s：%s",
                getLoginToInformation("", virtualAdProduct.getOrganizationIdentify()),
                Objects.isNull(organizationLoginResult) ? null : organizationLoginResult.getErrorType().getCode(),
                Objects.isNull(organizationLoginResult) ? null : getServerErrorTypeResult(organizationLoginResult.getErrorType()));
    }

    /**
     * ログインステータスの初期化のエラーメッセージを取得
     * 
     * @param virtualAdProduct 仮想adProduct
     * @return エラーメッセージ
     */
    public static String getAnalyzeLoginStatusInitialize(VirtualAdProduct virtualAdProduct) {

        return getLoginToInformation(virtualAdProduct.getEquipmentIdentify(), virtualAdProduct.getOrganizationIdentify());        
    }
    
    /**
     * 作業カンバンに対する処理のエラーメッセージを取得
     *
     * @param actualProductReportResult 実績通知結果
     * @param workKanbanIds 処理した工程カンバンID
     * @param virtualAdProduct 仮想adProduct
     * @return エラーメッセージ
     */
    public static String createAnalyzeActualProductReportResult(
            ActualProductReportResult actualProductReportResult,
            List<Long> workKanbanIds,
            VirtualAdProduct virtualAdProduct) {

        StringBuilder sb = new StringBuilder();

        sb.append(getLoginToInformation(virtualAdProduct.getEquipmentIdentify(),virtualAdProduct.getOrganizationIdentify()));
        
        if (Objects.nonNull(workKanbanIds) && !workKanbanIds.isEmpty()) {
            sb.append("【WorkKanbanIds】").append(workKanbanIds);
        }

        if (Objects.nonNull(actualProductReportResult)) {
            sb.append("【ErrorInfo】").append(actualProductReportResult.getDetails());
            sb.append(String.format("【ErrorType】%s：%s",
                    actualProductReportResult.getResultType().getCode(),
                    getServerErrorTypeResult(actualProductReportResult.getResultType())
            ));
        }

        return sb.toString();
    }    
    
    /**
     * 作業カンバンに対する処理のエラーメッセージを取得
     *
     * @param virtualAdProduct 仮想adProduct
     * @param condition 工程カンバンの検索条件指定
     * @return エラーメッセージ
     */
    public static String createAnalyzeActualProductReportResult(
            VirtualAdProduct virtualAdProduct,
            ProducibleWorkKanbanCondition condition) {

        StringBuilder sb = new StringBuilder();

        sb.append(getLoginToInformation(virtualAdProduct.getEquipmentIdentify(),virtualAdProduct.getOrganizationIdentify()));
        
        if (Objects.nonNull(condition)) {
            List<AddInfoSearchCondition> infos =  condition.getAddInfoSearchConditions();
            sb.append("【addInfoSearchCondition】");

            if(Objects.nonNull(infos)) {
                infos.forEach((info) -> {
                    sb.append(info.toString());
                });
            }
        }
        return sb.toString();
    }    
    
    /**
     * ログイン先情報
     * 
     * @param equipmentIdentify 設備管理名(装置名)
     * @param organizationIdentify 組織識別名(仮想作業者名)
     * @return ログイン先情報の情報
     */
    public static String getLoginToInformation(String equipmentIdentify, String organizationIdentify)
    {
        StringBuilder sb = new StringBuilder();

        if (!StringUtils.isEmpty(equipmentIdentify)) {
            sb.append("【EquipmentIdentify】").append(equipmentIdentify);
        }

        if (!StringUtils.isEmpty(organizationIdentify)) {
            sb.append("【OrganizationIdentify】").append(organizationIdentify);
        }
        return sb.toString();
    }

}
