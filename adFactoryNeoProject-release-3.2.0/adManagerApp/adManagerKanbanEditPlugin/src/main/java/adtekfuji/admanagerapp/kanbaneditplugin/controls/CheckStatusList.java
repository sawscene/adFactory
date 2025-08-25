/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.controls;

import adtekfuji.locale.LocaleUtils;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
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
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.IndexedCheckModel;

/**
 * チェックによる有効無効の切り替えつきステータスリスト
 *
 * @author fu-kato
 */
public class CheckStatusList extends VBox {

    protected final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private CheckListView<String> statusList;
    @FXML
    private CheckBox statusCheckBox;

    public CheckStatusList() {
        URL url = getClass().getResource("/fxml/control/check_status_list.fxml");
        FXMLLoader loader = new FXMLLoader(url, rb);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();

            setSpacing(8.0);
            setPadding(new Insets(8.0, 8.0, 8.0, 8.0));

            // 検索条件のステータス項目を設定する。
            ObservableList<String> stateList = FXCollections.observableArrayList(KanbanStatusEnum.getMessages(rb));
            statusList.setItems(stateList);

            statusList.disableProperty().bind(statusCheckBox.selectedProperty().not());
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
    public List<KanbanStatusEnum> getStatus() {

        return statusCheckBox.isSelected()
                ? statusList.getCheckModel().getCheckedIndices().stream()
                        .map(index -> KanbanStatusEnum.getValueText(index))
                        .map(str -> KanbanStatusEnum.getEnum(str))
                        .collect(Collectors.toList())
                : Arrays.asList(KanbanStatusEnum.values());
    }
    
    

    public boolean isSelected() {
        return statusCheckBox.isSelected();
    }

    public void setSelected(boolean value) {
        statusCheckBox.setSelected(value);
    }

    public BooleanProperty selectedProperty() {
        return statusCheckBox.selectedProperty();
    }

    public IndexedCheckModel<String> getCheckModel() {
        return statusList.getCheckModel();
    }

}
