/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.component;

import adtekfuji.admanagerapp.workreportplugin.entity.ChoiceIndirectWorkEntity;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectWorkInfoEntity;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
@FxComponent(id = "ChoiceIndirectWorkCompo", fxmlPath = "/fxml/admanagerworkreportplugin/choice_indirect_work_compo.fxml")
public class ChoiceIndirectWorkCompoFxController implements Initializable, ArgumentDelivery, DialogHandler {
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static final SceneContiner sc = SceneContiner.getInstance();

    private final ObservableList<IndirectWorkInfoEntity> indirectWorks = FXCollections.observableArrayList();

    private ChoiceIndirectWorkEntity choiceIndirectWork;

    private Dialog dialog;

    @FXML
    private PropertySaveTableView<IndirectWorkInfoEntity> tableView;
    @FXML
    private TableColumn<IndirectWorkInfoEntity, String> workNumberColumn;
    @FXML
    private TableColumn<IndirectWorkInfoEntity, String> workNameColumn;
    @FXML
    private Pane progressPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);

        tableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        // 列幅保存
        this.tableView.init("ChoiceIndirectWorkCompo");

        // 作業No
        this.workNumberColumn.setCellValueFactory((TableColumn.CellDataFeatures<IndirectWorkInfoEntity, String> param) -> param.getValue().workNumberProperty());
        // 作業内容
        this.workNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<IndirectWorkInfoEntity, String> param) -> param.getValue().workNameProperty());

        this.tableView.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            // リストで選択されたデータを戻り値にセットする。
            IndirectWorkInfoEntity selectedItem = (IndirectWorkInfoEntity) this.tableView.getItems().get(this.tableView.getSelectionModel().getSelectedIndex());
            this.choiceIndirectWork.setSelectedItem(selectedItem);
        });

        this.tableView.setItems(this.indirectWorks);
    }

    @Override
    public void setArgument(Object argument) {
        this.tableView.getSelectionModel().clearSelection();
        this.indirectWorks.clear();

        if (argument instanceof ChoiceIndirectWorkEntity) {
            this.choiceIndirectWork = (ChoiceIndirectWorkEntity) argument;

            // リストにデータをセットする。
            List<IndirectWorkInfoEntity> list = this.choiceIndirectWork.getIndirectWorks();
            if (Objects.isNull(list) || list.isEmpty()) {
                return;
            }
            this.indirectWorks.addAll(this.choiceIndirectWork.getIndirectWorks());

            // 選択データが指定されていたら該当行を選択状態にして表示する。
            IndirectWorkInfoEntity selected = this.choiceIndirectWork.getSelectedItem();
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
