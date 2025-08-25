/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;

/**
 * 権限設定画面
 *
 * @author e-mori
 */
@FxComponent(id = "AuthorityTypeEditCompo", fxmlPath = "/fxml/compo/authority_type_edit_compo.fxml")
public class AuthorityTypeEditCompoFxController implements Initializable {

    @FXML
    private Button registButton;
    @FXML
    private VBox propertyPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //役割の権限によるボタン無効化.
        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            registButton.setDisable(true);
        }
    }

    @FXML
    private void OnRegist(ActionEvent event) {
    }

}
