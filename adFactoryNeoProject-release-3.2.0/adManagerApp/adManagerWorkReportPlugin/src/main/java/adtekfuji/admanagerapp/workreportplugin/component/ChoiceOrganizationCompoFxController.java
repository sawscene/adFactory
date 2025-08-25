/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.component;

import adtekfuji.admanagerapp.workreportplugin.entity.ChoiceOrganizationEntity;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
@FxComponent(id = "ChoiceOrganizationCompo", fxmlPath = "/fxml/admanagerworkreportplugin/choice_organization_compo.fxml")
public class ChoiceOrganizationCompoFxController implements Initializable, ArgumentDelivery, DialogHandler {
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static final SceneContiner sc = SceneContiner.getInstance();

    private final ObservableList<OrganizationInfoEntity> organizations = FXCollections.observableArrayList();

    private ChoiceOrganizationEntity choiceOrganization;

    private Dialog dialog;

    @FXML
    private PropertySaveTableView<OrganizationInfoEntity> tableView;
    @FXML
    private TableColumn<OrganizationInfoEntity, String> organizationIdentifyColumn;
    @FXML
    private TableColumn<OrganizationInfoEntity, String> organizationNameColumn;
    @FXML
    private Pane progressPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);

        tableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        // 列幅保存
        this.tableView.init("ChoiceOrganizationCompo");

        // 組織識別名
        this.organizationIdentifyColumn.setCellValueFactory((TableColumn.CellDataFeatures<OrganizationInfoEntity, String> param) -> param.getValue().organizationIdentifyProperty());
        // 組織名
        this.organizationNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<OrganizationInfoEntity, String> param) -> param.getValue().organizationNameProperty());

        this.tableView.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            // リストで選択されたデータを戻り値にセットする。
            OrganizationInfoEntity selectedItem = (OrganizationInfoEntity) this.tableView.getItems().get(this.tableView.getSelectionModel().getSelectedIndex());
            this.choiceOrganization.setSelectedItem(selectedItem);
        });

        this.tableView.setItems(this.organizations);
    }

    @Override
    public void setArgument(Object argument) {
        this.tableView.getSelectionModel().clearSelection();
        this.organizations.clear();

        if (argument instanceof ChoiceOrganizationEntity) {
            this.choiceOrganization = (ChoiceOrganizationEntity) argument;

            // リストにデータをセットする。
            List<OrganizationInfoEntity> list = this.choiceOrganization.getIndirectWorks();
            if (Objects.isNull(list) || list.isEmpty()) {
                return;
            }
            //this.organizations.addAll(this.choiceOrganization.getIndirectWorks());
            addChildOrganizations(0L);

            // 組織識別名でソートする。
            this.organizations.sort(Comparator.comparing(item -> item.getOrganizationIdentify()));

            // 選択データが指定されていたら該当行を選択状態にして表示する。
            OrganizationInfoEntity selected = this.choiceOrganization.getSelectedItem();
            if (Objects.isNull(selected)) {
                return;
            }
            this.tableView.scrollTo(selected);
            this.tableView.getSelectionModel().select(selected);
        }
    }

    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.closeDialog();
        });
    }

    /**
     * 終了
     *
     * @param event 閉じるボタン押下
     */
    @FXML
    private void onCloseButton(ActionEvent event) {
        this.closeDialog();
    }

    private void addChildOrganizations(Long parentId) {
        for (OrganizationInfoEntity o : this.choiceOrganization.getIndirectWorks()) {
            if (Objects.nonNull(o.getParentId()) && o.parentIdProperty().getValue().equals(parentId)) {
                this.organizations.add(o);
                addChildOrganizations(o.getOrganizationId());
            }
        }
    }

    /**
     * 終了処理
     */
    private void closeDialog() {
        try {
            this.dialog.setResult(ButtonType.CLOSE);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    private void blockUI(boolean b) {
        sc.blockUI(b);
        progressPane.setVisible(b);
    }
}
