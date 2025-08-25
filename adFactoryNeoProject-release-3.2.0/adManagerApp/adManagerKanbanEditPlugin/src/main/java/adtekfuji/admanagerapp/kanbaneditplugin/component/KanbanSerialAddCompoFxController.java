/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * シリアル番号追加ダイアログ
 *
 * @author ta-ito
 */
@FxComponent(id = "KanbanSerialAddCompo", fxmlPath = "/fxml/compo/kanban_serial_add_compo.fxml")
public class KanbanSerialAddCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    @FXML
    private ListView<String> serialListView;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TextField serialTextField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        serialTextField.setOnAction((ActionEvent actionEvent) -> {
            onAdd(actionEvent);
            actionEvent.consume();
        });
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof ObservableList<?>) {
            try {
                serialListView.setItems((ObservableList<String>) argument);
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    @FXML
    private void onAdd(ActionEvent event) {
        if (!serialTextField.getText().isEmpty()
                && serialListView.getItems().stream().noneMatch(serialName -> serialName.equals(serialTextField.getText()))) {
            serialListView.getItems().add(serialTextField.getText());
        }
    }

    @FXML
    private void onRemove(ActionEvent event) {
        String item = serialListView.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(item)) {
            serialListView.getItems().remove(item);
        }
    }
}
