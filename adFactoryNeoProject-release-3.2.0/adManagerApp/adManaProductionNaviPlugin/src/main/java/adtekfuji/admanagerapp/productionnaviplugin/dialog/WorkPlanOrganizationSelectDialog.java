/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.dialog;

import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 組織選択
 * 
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
public class WorkPlanOrganizationSelectDialog {
    
    private static final OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();

    public static List<OrganizationInfoEntity> getOrganizations(List<Long> targets) {
        return targets.stream()
                .map(id -> organizationInfoFacade.find(id))
                .collect(Collectors.toList());
    }

    public static ButtonType showDialog(ActionEvent event) {
        ButtonType ret = ButtonType.CANCEL;
        try {
            Button eventSrc = (Button) event.getSource();
//            List<Long> targets = (List) eventSrc.getUserData();
//            List<OrganizationInfoEntity> organizations = getOrganizations(targets);

            List<OrganizationInfoEntity> organizations = (List) eventSrc.getUserData();
            SelectDialogEntity<OrganizationInfoEntity> selectDialogEntity = new SelectDialogEntity();

            if (Objects.nonNull(organizations)) {
                selectDialogEntity.organizations(organizations);
            }

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
