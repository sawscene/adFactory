/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.dialog;

import jp.adtekfuji.forfujiapp.javafx.record.factory.UnitTemplatePropertyRecordFactory;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.dialog.entity.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * プロパティ画面
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
@FxComponent(id = "UnitTemplateSettingDialog", fxmlPath = "/fxml/compo/dialog/unittemplateSettingDialog.fxml")
public class UnitTemplateSettingDialog implements Initializable, ArgumentDelivery {

    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    
    private UnitTemplateSettingDialogEntity entity = null;

    @FXML
    private TextField kanbanHierarchyName;
    @FXML
    private VBox propertyFieldPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof UnitTemplateSettingDialogEntity) {
            entity = (UnitTemplateSettingDialogEntity) argument;
            // 選択されている階層の表示
            kanbanHierarchyName.setText(entity.getOutputKanbanHierarchy().getHierarchyName());
            kanbanHierarchyName.setDisable(true);

            // カスタムフィールド表示
            propertyFieldPane.getChildren().clear();
            Table table = new Table(propertyFieldPane.getChildren()).isAddRecord(Boolean.TRUE)
                    .isColumnTitleRecord(Boolean.TRUE).styleClass("ContentTitleLabel");
            entity.getPropertyEntity().getPropertys().sort((entity1, entity2) -> {
                if (Objects.isNull(entity1.getUnitTemplatePropertyOrder()) || Objects.isNull(entity2.getUnitTemplatePropertyOrder())) {
                    return 0;
                }
                return entity1.getUnitTemplatePropertyOrder().compareTo(entity2.getUnitTemplatePropertyOrder());
            });
            table.setAbstractRecordFactory(new UnitTemplatePropertyRecordFactory(table, entity.getPropertyEntity().getPropertys()));
        }
    }

    @FXML
    public void onSelectKanbanHierarchy(ActionEvent event) {
        SelectDialogEntity<KanbanHierarchyInfoEntity> settingDialogEntity = new SelectDialogEntity();
        settingDialogEntity.setItem(entity.getOutputKanbanHierarchy());
        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.SelectOutputKanbanHierarchy"), "KanbanHierarchySingleSelectionCompo", settingDialogEntity, (Stage) ((Node) event.getSource()).getScene().getWindow());
        if(ButtonType.OK.equals(ret)){
            entity.setOutputKanbanHierarchy(settingDialogEntity.getItem());
            kanbanHierarchyName.setText(settingDialogEntity.getItem().getHierarchyName());
        }
    }

}
