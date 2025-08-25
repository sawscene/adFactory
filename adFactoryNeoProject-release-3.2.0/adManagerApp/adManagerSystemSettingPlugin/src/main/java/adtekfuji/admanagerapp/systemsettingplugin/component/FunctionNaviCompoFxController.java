/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.admanagerapp.systemsettingplugin.common.SystemSettingConfig;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;

/**
 * 設定画面切り替え
 *
 * @author e-mori
 */
@FxComponent(id = "FunctionNaviSystemCompo", fxmlPath = "/fxml/admanagersystemsettingplugin/function_navi_compo.fxml")
public class FunctionNaviCompoFxController implements Initializable {

    SceneContiner sc = SceneContiner.getInstance();

    private final Properties properties = AdProperty.getProperties();

    @FXML
    private Button editBreakTimeButton;
    @FXML
    private Button editDelayReasonButton;
    @FXML
    private Button editInterruptReasonButton;
    @FXML
    private Button editStatusButton;
    @FXML
    private Button editRoleButton;
    @FXML
    private Button editIndirectWorkButton;
    @FXML
    private Button editCallReasonButton;
    @FXML
    private Button editDefectReasonButton;
    @FXML
    private Button editLabelButton;
    @FXML
    private Button editApprovalRouteButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 間接作業一覧の編集 (enableDailyReport=true の場合のみ表示)
        if (!SystemSettingConfig.getEnableDailyReport()) {
            this.editIndirectWorkButton.setManaged(false);
        }

        // 不良理由の編集 (enableLotOut=true の場合のみ表示)
        if (!Boolean.parseBoolean(properties.getProperty("enableLotOut", "false"))) {
            this.editDefectReasonButton.setManaged(false);
        }

        // 承認ルート一覧の編集 (承認機能オプションが有効の場合のみ表示)
        if (!ClientServiceProperty.isLicensed(LicenseOptionType.ApprovalOption.getName())) {
            this.editApprovalRouteButton.setManaged(false);
        }

        final boolean isSystemSetting = ClientServiceProperty.isLicensed(LicenseOptionType.SystemSettingEditor.getName());
        final boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());
        if (!isSystemSetting && !isLiteOption) {
            // 何も表示しない
            this.editBreakTimeButton.setManaged(false);
            this.editDelayReasonButton.setManaged(false);
            this.editInterruptReasonButton.setManaged(false);
            this.editStatusButton.setManaged(false);
            this.editRoleButton.setManaged(false);
            this.editIndirectWorkButton.setManaged(false);
            this.editCallReasonButton.setManaged(false);
            this.editDefectReasonButton.setManaged(false);
            this.editLabelButton.setManaged(false);
            this.editApprovalRouteButton.setManaged(false);
        }
        if (!isSystemSetting && isLiteOption) {
            // Liteライセンスのみでは 休憩時間一覧の編集，中断理由一覧の編集，ステータス一覧の編集，間接作業一覧の編集 だけを表示
            this.editDelayReasonButton.setManaged(false);
            this.editRoleButton.setManaged(false);
            this.editCallReasonButton.setManaged(false);
            this.editDefectReasonButton.setManaged(false);
            this.editLabelButton.setManaged(false);
            this.editApprovalRouteButton.setManaged(false);
        }
    }

    // 未使用のため削除
//    @FXML
//    private void OnEditAutority(ActionEvent event) {
//        sc.setComponent("ContentNaviPane", "AuthorityTypeEditCompo");
//    }

    /**
     * 休憩時間
     * 
     * @param event 
     */
    @FXML
    private void onEditBreakTime(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "BreaktimeEditCompo");
    }

    /**
     * 遅延理由
     * 
     * @param event 
     */
    @FXML
    private void onEditDelayReason(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "ReasonEditCompo", ReasonTypeEnum.TYPE_DELAY);
    }

    /**
     * 中断理由
     * 
     * @param event 
     */
    @FXML
    private void onEditInterruptReason(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "ReasonEditCompo", ReasonTypeEnum.TYPE_INTERRUPT);
    }

    /**
     * ステータス表示
     * 
     * @param event 
     */
    @FXML
    private void onEditStatus(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "StatusEditCompo");
    }

    /**
     * 役割
     * 
     * @param event 
     */
    @FXML
    private void onEditRole(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "RoleEditCompo");
    }

    /**
     * 間接作業
     * 
     * @param event 
     */
    @FXML
    private void onEditIndirectWork(ActionEvent event) {
        //sc.setComponent("ContentNaviPane", "IndirectWorkEditCompo");
        sc.setComponent("ContentNaviPane", "WorkCategoryEditCompo");
    }

    /**
     * 呼出理由
     * 
     * @param event 
     */
    @FXML
    private void onEditCallReason(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "ReasonEditCompo", ReasonTypeEnum.TYPE_CALL);
    }

    /**
     * 不良理由
     *
     * @param event イベント
     */
    @FXML
    private void onEditDefectReason(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "DefectReasonEditCompo");
    }

    /**
     * ラベル
     *
     * @param event イベント
     */
    @FXML
    private void onEditLabel(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "LabelEditCompo");
    }

    /**
     * 承認ルート
     *
     * @param event イベント
     */
    @FXML
    private void onEditApprovalRoute(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "ApprovalRouteEditCompo");
    }
}
