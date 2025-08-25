/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.selectcompo;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import adtekfuji.locale.LocaleUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author j.min
 */
@FxComponent(id = "AccessAuthSettingCompo", fxmlPath = "/fxml/compo/access_auth_setting_compo.fxml")
public class AccessAuthSettingCompoFxController implements Initializable, ArgumentDelivery {

    private final static Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private AccessAuthSettingEntity settingEntity = null;
    @FXML
    private Label hierarchyNameLabel;
    @FXML
    private VBox authOrganizationPane;

    /**
     * 組織編集コンポーネントを初期化する。
     *
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.authOrganizationPane.setSpacing(2);
    }

    @Override
    public void setArgument(Object argument) {
        if(argument instanceof AccessAuthSettingEntity) {
            settingEntity = (AccessAuthSettingEntity) argument;
            setHierarchyName(settingEntity.getHierarchyName());
            setAuthOrganizationPane();
        }
    }
    
    /**
     * 組織追加
     *
     * @param event 追加ボタン押下
     */
    @FXML
    private void onAdd(ActionEvent event) {
        try {
            //List<OrganizationInfoEntity> org = new ArrayList(settingDialogEntity.getOrganizations());
            SelectDialogEntity selectDialogEntity = new SelectDialogEntity().organizations(settingEntity.getAuthOrganizations());
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialogEntity, true);
            if (ret.equals(ButtonType.OK)) {
                settingEntity.setAuthOrganizations(selectDialogEntity.getOrganizations());
                setAuthOrganizationPane();
            } else {
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    private void setHierarchyName(String _hierarchyName) {
        String hierarchyName = LocaleUtils.getString("key.HierarchyName");
        hierarchyName = hierarchyName.concat(LocaleUtils.getString("key.separation"));
        hierarchyName = hierarchyName.concat(_hierarchyName);
        this.hierarchyNameLabel.setText(hierarchyName);
        this.hierarchyNameLabel.getStyleClass().add("ContentTitleLabel");
    }
    
    private void setAuthOrganizationPane() {
        this.authOrganizationPane.getChildren().clear();
        for(OrganizationInfoEntity o : settingEntity.getAuthOrganizations()) {
            this.authOrganizationPane.getChildren().add(createAuthOrganization(o));
        }
    }
    
    private HBox createAuthOrganization(OrganizationInfoEntity organization) {
        HBox authOrganization = new HBox();
        authOrganization.setSpacing(4);
        authOrganization.setPadding(new Insets(0, 2, 0, 4));
        authOrganization.setAlignment(Pos.CENTER);
        Label orgaLabel = new Label(organization.getOrganizationName());
        authOrganization.setStyle("-fx-font-size:" + 12 + ";" + "-fx-text-fill: black;" + "-fx-background-color: white;"
                                        + "-fx-border-width: 0.5; -fx-border-radius:3; -fx-border-color: grey;");
        orgaLabel.setAlignment(Pos.CENTER_LEFT);
        orgaLabel.setPrefSize(173, 27);
        orgaLabel.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        orgaLabel.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        Button delButton = new Button("-");
        delButton.setOnAction((ActionEvent e) -> {
            this.authOrganizationPane.getChildren().remove(authOrganization);
            List<OrganizationInfoEntity> result = new ArrayList<>();
            for(OrganizationInfoEntity init : settingEntity.getAuthOrganizations()) {
                if(!init.equals(organization)) result.add(init);
            }
            settingEntity.setAuthOrganizations(result);
        });
        String[] colors = {"tomato","pink","darkorange","tan","limegreen","mediumseagreen","mediumturquoise","royalblue","steelblue","plum","mediumpurple","dimgrey","deeppink"};
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(700), new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                authOrganization.setStyle("-fx-font-size:" + 12 + ";" + "-fx-text-fill: black;" + "-fx-background-color: white;"
                                        + "-fx-border-width: 0.5; -fx-border-radius:3; -fx-border-color: grey;");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        authOrganization.setOnMouseEntered((MouseEvent e)->{
            Random r = new Random();
            int i = r.nextInt(13);
            authOrganization.setStyle("-fx-font-size:" + 12 + ";" + "-fx-text-fill: black;" + "-fx-background-color: white;"
                                        + "-fx-border-width: 0.5; -fx-border-radius:3; -fx-border-color:"+colors[i]+";");
            timeline.play();
        });
        authOrganization.getChildren().addAll(orgaLabel, delButton);
        return authOrganization;
    }
}
