/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.component;

import adtekfuji.admanagerapp.workreportplugin.entity.ChoiceControlNoEntity;
import adtekfuji.admanagerapp.workreportplugin.entity.ControlNoRowInfo;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 製番選択ダイアログ
 * 
 * @author kentarou.suzuki
 */
@FxComponent(id = "ChoiceControlNoCompo", fxmlPath = "/fxml/admanagerworkreportplugin/choice_control_no_compo.fxml")
public class ChoiceControlNoCompoFxController implements Initializable, ArgumentDelivery, DialogHandler {
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static final SceneContiner sc = SceneContiner.getInstance();

    private final ObservableList<ControlNoRowInfo> controlNoRows = FXCollections.observableArrayList();

    private ChoiceControlNoEntity choiceControlNo;

    private Dialog dialog;

    @FXML
    private PropertySaveTableView<ControlNoRowInfo> tableView;
    @FXML
    private TableColumn selectedColumn;
    @FXML
    private TableColumn<ControlNoRowInfo, String> controlNoColumn;
    @FXML
    private Pane progressPane;

    /**
     * 製番選択ダイアログを初期化する。
     * 
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);

        tableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        // 列幅保存
        this.tableView.init("ChoiceControlNoCompo");

        // 選択状態
        this.selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        this.selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn((Integer param) -> {
            ControlNoRowInfo row = tableView.getItems().get(param);
            String item = row.getControlNo();
            BooleanProperty selectedProperty = row.selectedProperty();
            
            // 製番選択チェックボックス変更時の処理
            selectedProperty.addListener((observable, oldValue, newValue) -> {
                if (Objects.isNull(newValue)) {
                    return;
                }
                
                List<String> selectedItems = new LinkedList<>(choiceControlNo.getSelectedItems());
                if (newValue) {
                    // チェックON
                    if (!selectedItems.contains(item)) {
                        selectedItems.add(item);
                    }
                } else {
                    // チェックOFF
                    boolean existsCheckedItem = tableView.getItems().stream().filter(p -> p.isSelected()).count() > 0;
                    if (existsCheckedItem) {
                        if (selectedItems.contains(item)) {
                            selectedItems.remove(item);
                        }
                    } else {
                        // 「製番が選択されていません 少なくとも1つ以上の製番を選択してください」
                        sc.showMessageBox(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("no_production_number"), LocaleUtils.getString("no_production_number_details"), new ButtonType[]{ButtonType.OK}, ButtonType.OK);
                        row.setSelected(true);
                        return;
                    }
                }

                selectedItems.sort(Comparator.naturalOrder());
                choiceControlNo.setSelectedItems(selectedItems);
            });

            return selectedProperty;
        }));
        
        // 製番
        this.controlNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<ControlNoRowInfo, String> param) -> param.getValue().controlNoProperty());

        this.tableView.setItems(this.controlNoRows);
    }

    /**
     * 引数を設定する。
     * 
     * @param argument 引数
     */
    @Override
    public void setArgument(Object argument) {
        this.tableView.getSelectionModel().clearSelection();
        this.controlNoRows.clear();

        if (argument instanceof ChoiceControlNoEntity) {
            this.choiceControlNo = (ChoiceControlNoEntity) argument;

            // リストにデータをセットする。
            List<ControlNoRowInfo> list = new ArrayList<>();
            this.choiceControlNo.getTargetItems().forEach(controlNo -> {
                ControlNoRowInfo row = new ControlNoRowInfo(controlNo, this.choiceControlNo.getSelectedItems().contains(controlNo));
                list.add(row);
            });
            if (Objects.isNull(list) || list.isEmpty()) {
                return;
            }
            this.controlNoRows.addAll(list);
        }
    }

    /**
     * ダイアログを設定する。
     * 
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.closeDialog();
        });
    }

    /**
     * 閉じるボタン押下時の処理
     *
     * @param event 閉じるボタン押下
     */
    @FXML
    private void onCloseButton(ActionEvent event) {
        this.closeDialog();
    }

    /**
     * ダイアログを終了する。
     */
    private void closeDialog() {
        try {
            this.dialog.setResult(ButtonType.CLOSE);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 操作をロックする。
     * 
     * @param block 表示フラグ
     */
    private void blockUI(boolean block) {
        sc.blockUI(block);
        progressPane.setVisible(block);
    }
}
