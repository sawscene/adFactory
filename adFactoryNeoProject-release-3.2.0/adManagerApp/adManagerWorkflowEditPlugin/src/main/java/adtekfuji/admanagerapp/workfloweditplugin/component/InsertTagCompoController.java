/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * リンクあるいは画像を挿入するダイアログ
 *
 * @author fu-kato
 */
@FxComponent(id = "InsertTagCompo", fxmlPath = "/fxml/compo/insert_tag_compo.fxml")
public class InsertTagCompoController implements ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    @FXML
    Label valueLabel;

    @FXML
    TextField valueText;

    @FXML
    TextField addrText;

    private static File defaultDir = new File(System.getProperty("user.home"), "Desktop");

    /*
     * データ受渡し用クラス 
     */
    public static class Data {

        private final String title;
        private final StringProperty label = new SimpleStringProperty();
        private final StringProperty addr = new SimpleStringProperty();

        public Data(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public StringProperty valueProperty() {
            return label;
        }

        public StringProperty addrProperty() {
            return addr;
        }

        public String getValue() {
            return label.get();
        }

        public String getAddr() {
            return addr.get();
        }
    }

    // リンクとして挿入可能な画像ファイルのリスト
    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(LocaleUtils.getString("key.ImageFile"), "*.bmp", "*.jpg", "*.jpeg", "*.jpe", "*.gif", "*.png");

    private final List<FileChooser.ExtensionFilter> filters = new ArrayList<>();

    @Override
    public void setArgument(Object argument) {
        Data data = (Data) argument;

        if (Objects.equals(data.getTitle(), LocaleUtils.getString("key.InsertImage"))) {
            // 表示の設定はリンク設定でのみ行う
            valueLabel.setVisible(false);
            valueLabel.setManaged(false);
            valueText.setVisible(false);
            valueText.setManaged(false);

            filters.add(filter);
        } else if (Objects.equals(data.getTitle(), LocaleUtils.getString("key.InsertLink"))) {

        } else {
            logger.fatal("unexpected argument");
        }

        valueText.textProperty().bindBidirectional(data.valueProperty());
        addrText.textProperty().bindBidirectional(data.addrProperty());
    }

    /**
     * ファイル選択ダイアログを表示しファイルを選択する
     *
     * @param event
     */
    @FXML
    private void onOpenFile(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(LocaleUtils.getString("key.FileChoice"));
            fileChooser.getExtensionFilters().addAll(filters);
            fileChooser.setInitialDirectory(defaultDir);

            File file = fileChooser.showOpenDialog(sc.getWindow());
            if (Objects.nonNull(file)) {
                defaultDir = file.getParentFile();
                addrText.setText("file://" + file.getPath().replaceAll("\\\\", "/").replaceFirst("^//(.*)", "$1"));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
