/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.guitesttool.autotest;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;

/**
 * FXML Controller class
 *
 * @author ke.yokoi
 */
@FxComponent(id = "AutoTestCompo", fxmlPath = "/fxml/auto_test_compo.fxml")
public class AutoTestCompoFXController implements Initializable {

    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = ResourceBundle.getBundle("locale.locale");
    private final LineFacade lineFacade = new LineFacade();

    @FXML
    private Button kanbanHierarchyButton;
    @FXML
    private TextField kanbanIntervalField;
    @FXML
    private TextArea makeKanbanLogArea;
    @FXML
    private Button selectLineButton;
    @FXML
    private TextField workIntervalField;
    @FXML
    private TextArea workLogArea;
    @FXML
    private TableView equipWorkTable;
    @FXML
    private AnchorPane lineStatusPane;
    @FXML
    private AnchorPane equipmentStatusPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //アンドンモニタフレームの表示.
        //利用するには、自PCをアンドンモニタとして設備登録すること.
        sc.setComponent(lineStatusPane, "DailyLineStatus");
        sc.setComponent(equipmentStatusPane, "DailyEquipmentStatus");

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                lineFacade.initialRead();
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void onSelectKanbanHierarchy(ActionEvent event) {
    }

    @FXML
    private void onStartCreateKanban(ActionEvent event) {
    }

    @FXML
    private void onPauseCreateKanban(ActionEvent event) {
    }

    @FXML
    private void onStopCreateKanban(ActionEvent event) {
    }

    @FXML
    private void onSelectLine(ActionEvent event) {
        EquipmentInfoEntity selectLine = lineFacade.getSelectLine();
        SelectDialogEntity<EquipmentInfoEntity> selectDialogEntity = new SelectDialogEntity();
        if (Objects.nonNull(selectLine)) {
            selectDialogEntity.equipments(Arrays.asList(selectLine));
        }
        ButtonType ret = sc.showComponentDialog(rb.getString("key.Equipment"), "EquipmentSelectionCompo", selectDialogEntity);
        if (ret.equals(ButtonType.OK)) {
            if (!selectDialogEntity.getEquipments().isEmpty()) {
                selectLine = selectDialogEntity.getEquipments().get(0);
                lineFacade.setSelectLine(selectLine);
                selectLineButton.setText(selectLine.getEquipmentName());
            }
        }
    }

    @FXML
    private void onStartAutoWork(ActionEvent event) {
    }

    @FXML
    private void onPauseAutoWork(ActionEvent event) {
    }

    @FXML
    private void onStopAutoWork(ActionEvent event) {
    }

}
