package adtekfuji.admanagerapp.component;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import adtekfuji.admanagerapp.MainMenuContainer;
import adtekfuji.clientservice.SystemResourceFacade;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.system.SystemOptionEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 * FXML Controller class
 *
 * @author e-mori
 */
@FxComponent(id = "MainMenuCompo", fxmlPath = "/fxml/compo/main_menu_compo.fxml")
public class MainMenuCompoController implements Initializable {

    private final SceneContiner sc = SceneContiner.getInstance();
    private final SystemResourceFacade systemResourceFacade = new SystemResourceFacade();

    @FXML
    private VBox mainMenuVbox;
    @FXML
    private VBox mainMenuButtonVbox;
    @FXML
    private Button objectEditButton;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        MainMenuContainer container = MainMenuContainer.getInstance();

        List<SystemOptionEntity> optionLicenses = systemResourceFacade.getLicenseOptions();
        container.makeMenuButton(this.mainMenuButtonVbox, optionLicenses);

        // 品質トレーサビリティが無効の場合は、モノ編集を消去する
        Optional<SystemOptionEntity> find = optionLicenses.stream().filter((o) -> "@Traceability".equals(o.getOptionName())).findFirst();
        if (!find.isPresent() || !find.get().getEnable()) {
            this.mainMenuVbox.getChildren().remove(this.objectEditButton);
        }

        // キャッシュする情報を取得する
        CacheUtils.createCacheData(EquipmentInfoEntity.class, true);
        CacheUtils.createCacheData(OrganizationInfoEntity.class, true);
    }

    @FXML
    private void onLogOutButton(ActionEvent event) {
        sc.trans("LoginScene");
    }

    @FXML
    private void onCloseButton(ActionEvent event) {
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
    }

    @FXML
    private void onObjectEditButton(ActionEvent event) {
        if (!sc.trans("ObjectEditScene")) {
            return;
        }
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("AppBarPane", "AppBarCompo");
        sc.setComponent("ContentNaviPane", "ObjectEditCompo");
    }
}
