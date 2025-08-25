/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.controls;

import adtekfuji.locale.LocaleUtils;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.enumerate.WarehouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.IndexedCheckModel;

/**
 * チェックリスト
 *
 * @author s-heya
 */
public class CheckList extends VBox {

    protected final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private CheckListView<String> listView;
    @FXML
    private CheckBox checkBox;

    /**
     * コンストラクタ
     */
    public CheckList() {
        try {
            URL url = getClass().getResource("/fxml/controls/check_list.fxml");
            FXMLLoader loader = new FXMLLoader(url, rb);
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            setSpacing(8.0);
            setPadding(new Insets(8.0, 8.0, 8.0, 8.0));

            ObservableList<String> stateList = FXCollections.observableArrayList(WarehouseEvent.getMessages(rb));
            listView.setItems(stateList);
            listView.disableProperty().bind(checkBox.selectedProperty().not());
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * チェックのついているステータスをリストの形で取得する。<br>
     * ラベルにチェックが入ってない場合、常にすべてのステータスを返す。
     *
     * @return
     */
    public List<WarehouseEvent> getCheckedItems() {
        return listView.getCheckModel().getCheckedIndices().stream()
                .map(index -> WarehouseEvent.get(index))
                .collect(Collectors.toList());
    }

    public boolean isSelected() {
        return checkBox.isSelected();
    }

    public void setSelected(boolean value) {
        checkBox.setSelected(value);
    }

    public BooleanProperty selectedProperty() {
        return checkBox.selectedProperty();
    }

    public IndexedCheckModel<String> getCheckModel() {
        return listView.getCheckModel();
    }

}
