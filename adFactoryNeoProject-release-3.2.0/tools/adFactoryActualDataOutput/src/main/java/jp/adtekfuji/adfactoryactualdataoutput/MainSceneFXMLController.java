package jp.adtekfuji.adfactoryactualdataoutput;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DateTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import jp.adtekfuji.adfactoryactualdataoutput.entity.ColumnNameProperty;
import jp.adtekfuji.adfactoryactualdataoutput.entity.OutSettingProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainSceneFXMLController implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    private final OutSettingProperty property = new OutSettingProperty();

    private final ToggleGroup outTimeGroup = new ToggleGroup();
    @FXML
    private RadioButton pastTimeSelect;
    @FXML
    private TextField pastTimeField;
    @FXML
    private RadioButton rangeTimeSelect;
    @FXML
    private TextField fromRangeTimeField;
    @FXML
    private TextField toRangeTimeField;

    @FXML
    private ChoiceBox fileEncodeChoice;
    @FXML
    private CheckBox firstRowColumnCheck;
    @FXML
    private CheckBox doubleMarkCheck;
    @FXML
    private CheckBox addLastStringCheck;
    @FXML
    private TextField lastStringField;

    private final ToggleGroup outPathGroup = new ToggleGroup();
    @FXML
    private RadioButton outLocalPathSelect;
    @FXML
    private TextField localPathField;
    @FXML
    private RadioButton outSharedPathSelect;
    @FXML
    private TextField sharedPathField;
    @FXML
    private TextField sharedUserField;
    @FXML
    private PasswordField sharedPassField;
    @FXML
    private RadioButton outFtpPathSelect;
    @FXML
    private TextField ftpPathField;
    @FXML
    private TextField ftpPortField;
    @FXML
    private TextField ftpUserField;
    @FXML
    private PasswordField ftpPassField;
    @FXML
    private TextField filePathField;

    @FXML
    private TableView<ColumnNameProperty> columnNameTable;
    @FXML
    private TableColumn<ColumnNameProperty, String> changeInColumn;
    @FXML
    private TableColumn<ColumnNameProperty, String> changeOutColumn;

    @FXML
    private TableView filterTable;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            property.load();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        //出力時間設定.
        setOutTimeGruopData();
        pastTimeField.textProperty().bindBidirectional(property.pastTimeProperty(), new NumberStringConverter());
        fromRangeTimeField.textProperty().bindBidirectional(property.fromRangeTimeProperty(), new DateTimeStringConverter());
        toRangeTimeField.textProperty().bindBidirectional(property.toRangeTimeProperty(), new DateTimeStringConverter());
        //出力形式設定.
        fileEncodeChoice.setItems(FXCollections.observableArrayList(OutSettingProperty.FileEncodeSettingEnum.values()));
        fileEncodeChoice.valueProperty().bindBidirectional(property.fileEncodeProperty());
        firstRowColumnCheck.selectedProperty().bindBidirectional(property.firstRowColumnCheckProperty());
        doubleMarkCheck.selectedProperty().bindBidirectional(property.doubleMarkCheckProperty());
        addLastStringCheck.selectedProperty().bindBidirectional(property.addLastStringCheckProperty());
        lastStringField.textProperty().bindBidirectional(property.lastStringProperty());
        //出力フォルダパス設定.
        setOutPathGruopData();
        localPathField.textProperty().bindBidirectional(property.localPathProperty());
        sharedPathField.textProperty().bindBidirectional(property.sharedPathProperty());
        sharedUserField.textProperty().bindBidirectional(property.sharedUserProperty());
        sharedPassField.textProperty().bindBidirectional(property.sharedPassProperty());
        ftpPathField.textProperty().bindBidirectional(property.ftpPathProperty());
        ftpPortField.textProperty().bindBidirectional(property.ftpPortProperty(), new NumberStringConverter());
        ftpUserField.textProperty().bindBidirectional(property.ftpUserProperty());
        ftpPassField.textProperty().bindBidirectional(property.ftpPassProperty());
        //出力ファイルパス設定.
        filePathField.textProperty().bindBidirectional(property.lastStringProperty());
        //出力カラム名変換設定.
        changeInColumn.setCellValueFactory((TableColumn.CellDataFeatures<ColumnNameProperty, String> param) -> param.getValue().inColumnNameProperty());
        changeOutColumn.setCellValueFactory((TableColumn.CellDataFeatures<ColumnNameProperty, String> param) -> param.getValue().outColumnNameProperty());
        changeOutColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        ObservableList<ColumnNameProperty> table = FXCollections.observableArrayList();
        table.addAll(property.getColumnNameList());
        columnNameTable.setItems(table);
        //実績データフィルター設定.
    }

    private void setOutTimeGruopData() {
        pastTimeSelect.setToggleGroup(outTimeGroup);
        pastTimeSelect.setUserData(OutSettingProperty.TimeSettingEnum.PAST_TIME);
        rangeTimeSelect.setToggleGroup(outTimeGroup);
        rangeTimeSelect.setUserData(OutSettingProperty.TimeSettingEnum.RANGE_TIME);
        switch (property.getTimeSetting()) {
            case PAST_TIME:
                outTimeGroup.selectToggle(pastTimeSelect);
            case RANGE_TIME:
                outTimeGroup.selectToggle(rangeTimeSelect);
        }
        outTimeGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            property.setTimeSetting((OutSettingProperty.TimeSettingEnum) outTimeGroup.getSelectedToggle().getUserData());
        });
    }

    private void setOutPathGruopData() {
        outLocalPathSelect.setToggleGroup(outPathGroup);
        outLocalPathSelect.setUserData(OutSettingProperty.OutFolderSettingEnum.LOCAL);
        outSharedPathSelect.setToggleGroup(outPathGroup);
        outSharedPathSelect.setUserData(OutSettingProperty.OutFolderSettingEnum.SHARE);
        outFtpPathSelect.setToggleGroup(outPathGroup);
        outFtpPathSelect.setUserData(OutSettingProperty.OutFolderSettingEnum.FTP);
        switch (property.getOutFolder()) {
            case LOCAL:
                outPathGroup.selectToggle(outLocalPathSelect);
            case SHARE:
                outPathGroup.selectToggle(outSharedPathSelect);
            case FTP:
                outPathGroup.selectToggle(outFtpPathSelect);
        }
        outPathGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            property.setOutFolder((OutSettingProperty.OutFolderSettingEnum) outPathGroup.getSelectedToggle().getUserData());
        });
    }

    @FXML
    private void onSaveSetting(ActionEvent event) {
        try {
            property.save();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    @FXML
    private void onOutputCSV(ActionEvent event) {
    }

}
