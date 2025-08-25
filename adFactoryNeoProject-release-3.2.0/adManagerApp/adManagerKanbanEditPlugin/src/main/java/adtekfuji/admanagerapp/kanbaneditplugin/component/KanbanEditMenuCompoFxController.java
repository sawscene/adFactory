/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component;

import adtekfuji.admanagerapp.kanbaneditplugin.common.KanbanEditConfig;
import adtekfuji.admanagerapp.kanbaneditplugin.common.KanbanEditPermanenceData;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 * カンバン編集メニュー
 *
 * @author nar-nakamura
 */
@FxComponent(id = "KanbanEditMenuCompo", fxmlPath = "/fxml/compo/kanban_edit_menu.fxml")
public class KanbanEditMenuCompoFxController implements Initializable, ArgumentDelivery {

    private final SceneContiner sc = SceneContiner.getInstance();

    @FXML
    private VBox menuPane;
    @FXML
    private Button editKanbanButton;
    @FXML
    private Button kanbanImportButton;
    @FXML
    private Button editLiteKanbanButton;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 機能一覧から開いたときツリーを初期化する
        KanbanEditPermanenceData.getInstance().setKanbanHierarchyRootItem(null);
        KanbanEditPermanenceData.getInstance().setSelectedWorkHierarchy(null);

        // キャッシュする情報を取得する
        CacheUtils.createCacheData(EquipmentInfoEntity.class, true);
        CacheUtils.createCacheData(OrganizationInfoEntity.class, true);

        // メニューの表示を切り替える
        final boolean isKanbanEditor = ClientServiceProperty.isLicensed(LicenseOptionType.KanbanEditor.getName());
        final boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());
        if (!isKanbanEditor) {
            this.menuPane.getChildren().remove(this.editKanbanButton);
        }
        if (!KanbanEditConfig.isUseKanbanImport()) {
            // Ver.3.0.0 から生産計画の[生産計画の読み込み]を使用する 2024/10/15 s-heya
            this.menuPane.getChildren().remove(this.kanbanImportButton);
        }
        if (!isLiteOption) {
            this.menuPane.getChildren().remove(this.editLiteKanbanButton);
        }
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {

    }

    /**
     * カンバン編集
     *
     * @param event
     */
    @FXML
    public void onViewKanbanList(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "KanbanListCompo");
    }

    /**
     * Liteカンバン編集
     *
     * @param event
     */
    @FXML
    public void onViewLiteKanbanList(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "LiteKanbanListCompo");
    }

    /**
     * 生産計画読み込み
     *
     * @param event
     */
    @FXML
    public void onViewKanbanImport(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "KanbanImportCompo");
    }
}
