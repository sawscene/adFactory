/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingpluginfuji.dialog;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業者選択ダイアログ
 *
 * @author e-mori
 */
public class OrganizationSelectDialog {

    public static ButtonType showDialog(ActionEvent event) {
        ButtonType ret = ButtonType.CANCEL;
        try {
            Button eventSrc = (Button) event.getSource();
            List<Long> targets = (List) eventSrc.getUserData();

            List<OrganizationInfoEntity> organizations = CacheUtils.getCacheOrganization(targets);

            SelectDialogEntity<OrganizationInfoEntity> selectDialogEntity = new SelectDialogEntity();

            if (Objects.nonNull(organizations)) {
                selectDialogEntity.organizations(organizations);
            }

            ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
            SceneContiner sc = SceneContiner.getInstance();

            ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialogEntity, (Stage) ((Node) event.getSource()).getScene().getWindow());
            if (ret.equals(ButtonType.OK)) {
                organizations.clear();

                if (!selectDialogEntity.getOrganizations().isEmpty()) {
                    organizations.addAll(selectDialogEntity.getOrganizations());
                }
                eventSrc.setUserData(organizations);
            }
        } catch (Exception ex) {
            Logger logger = LogManager.getLogger();
            logger.fatal(ex, ex);
        }
        return ret;
    }
}
