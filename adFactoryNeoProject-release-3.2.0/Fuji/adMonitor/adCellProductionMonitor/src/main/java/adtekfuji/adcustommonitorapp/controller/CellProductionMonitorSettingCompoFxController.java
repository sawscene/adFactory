/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.controller;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.dialog.entity.SelectDialogEntity;
import jp.adtekfuji.forfujiapp.dialog.entity.SelectedTableData;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.forfujiapp.javafx.list.cell.UnitTemplateSelectedTabelDataListItemCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 設定画面クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.24.thr
 */
@FxComponent(id = "CellProductionMonitorSettingCompo", fxmlPath = "/fxml/adcustommonitorapp/cellProductionMonitorSettingCompo.fxml")
public class CellProductionMonitorSettingCompoFxController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);

    private final ObservableList<SelectedTableData<UnitTemplateInfoEntity>> selectedPanel = FXCollections.observableArrayList();
    private final ObservableList<SelectedTableData<UnitTemplateInfoEntity>> selectedGraph = FXCollections.observableArrayList();
    private final ObservableList<SelectedTableData<UnitTemplateInfoEntity>> selectedList = FXCollections.observableArrayList();
    private final ObservableList<SelectedTableData<UnitTemplateInfoEntity>> selectedList2 = FXCollections.observableArrayList();

    private final String COMMA_SPLIT = ",";

    @FXML
    private ComboBox<String> panelDisplayTitleCombo;
    @FXML
    private ComboBox<String> panelShowColumnNumCombo;
    @FXML
    private ComboBox<String> panelShowRowNumCombo;
    @FXML
    private ListView panelUnitTemplateList;
    @FXML
    private ComboBox<String> graphDisplayTitleCombo;
    @FXML
    private ComboBox<String> graphShowColumnNumCombo;
    @FXML
    private ComboBox<String> graphShowRowNumCombo;
    @FXML
    private ListView graphUnitTemplateList;
    @FXML
    private ComboBox<String> listMainTitleColumnCombo;
    @FXML
    private ComboBox<String> listSubTitleColumnCombo;
    @FXML
    private ListView listUnitTemplateList;
    @FXML
    private TextField list2TitleColumn;
    @FXML
    private TextField list2HolidayFile;
    @FXML
    private ListView list2UnitTemplateList;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":initialize start");
        Callback<ListView<SelectedTableData<UnitTemplateInfoEntity>>, ListCell<SelectedTableData<UnitTemplateInfoEntity>>> unittemlateCellFactory1 = (ListView<SelectedTableData<UnitTemplateInfoEntity>> param) -> new UnitTemplateSelectedTabelDataListItemCell();
        panelUnitTemplateList.setCellFactory(unittemlateCellFactory1);
        Callback<ListView<SelectedTableData<UnitTemplateInfoEntity>>, ListCell<SelectedTableData<UnitTemplateInfoEntity>>> unittemlateCellFactory2 = (ListView<SelectedTableData<UnitTemplateInfoEntity>> param) -> new UnitTemplateSelectedTabelDataListItemCell();
        graphUnitTemplateList.setCellFactory(unittemlateCellFactory2);
        Callback<ListView<SelectedTableData<UnitTemplateInfoEntity>>, ListCell<SelectedTableData<UnitTemplateInfoEntity>>> unittemlateCellFactory3 = (ListView<SelectedTableData<UnitTemplateInfoEntity>> param) -> new UnitTemplateSelectedTabelDataListItemCell();
        listUnitTemplateList.setCellFactory(unittemlateCellFactory3);
        Callback<ListView<SelectedTableData<UnitTemplateInfoEntity>>, ListCell<SelectedTableData<UnitTemplateInfoEntity>>> unittemlateCellFactory4 = (ListView<SelectedTableData<UnitTemplateInfoEntity>> param) -> new UnitTemplateSelectedTabelDataListItemCell();
        list2UnitTemplateList.setCellFactory(unittemlateCellFactory4);
        this.loadSetting();
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":initialize end");
    }

    /**
     * 登録処理を行う
     *
     * @param event
     */
    @FXML
    public void onSelectPanelUnitTemplate(ActionEvent event) {
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":onSelectPanelUnitTemplate start");
        sc.blockUI(Boolean.TRUE);
        try {
            SelectDialogEntity<SelectedTableData<UnitTemplateInfoEntity>> selectDialogEntity = new SelectDialogEntity().multiSelectItems(selectedPanel);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.unittemplate"), "UnitTemplateMultiSelectionCompo", selectDialogEntity);
            if (ret.equals(ButtonType.OK)) {

                Platform.runLater(() -> {
                    panelUnitTemplateList.getItems().clear();
                    panelUnitTemplateList.getItems().addAll(selectedPanel);
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            sc.blockUI(Boolean.FALSE);
        }
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":onSelectPanelUnitTemplate end");
    }

    /**
     * 登録処理を行う
     *
     * @param event
     */
    @FXML
    public void onSelectGraphUnitTemplate(ActionEvent event) {
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":onSelectGraphUnitTemplate start");
        sc.blockUI(Boolean.TRUE);
        try {
            SelectDialogEntity<SelectedTableData<UnitTemplateInfoEntity>> selectDialogEntity = new SelectDialogEntity().multiSelectItems(selectedGraph);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.unittemplate"), "UnitTemplateMultiSelectionCompo", selectDialogEntity);
            if (ret.equals(ButtonType.OK)) {

                Platform.runLater(() -> {
                    graphUnitTemplateList.getItems().clear();
                    graphUnitTemplateList.getItems().addAll(selectedGraph);
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            sc.blockUI(Boolean.FALSE);
        }
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":onSelectGraphUnitTemplate end");
    }

    /**
     * 登録処理を行う
     *
     * @param event
     */
    @FXML
    public void onSelectListUnitTemplate(ActionEvent event) {
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":onSelectListUnitTemplate start");
        sc.blockUI(Boolean.TRUE);
        try {
            SelectDialogEntity<SelectedTableData<UnitTemplateInfoEntity>> selectDialogEntity = new SelectDialogEntity().multiSelectItems(selectedList);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.unittemplate"), "UnitTemplateMultiSelectionCompo", selectDialogEntity);
            if (ret.equals(ButtonType.OK)) {

                Platform.runLater(() -> {
                    listUnitTemplateList.getItems().clear();
                    listUnitTemplateList.getItems().addAll(selectedList);
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            sc.blockUI(Boolean.FALSE);
        }
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":onSelectListUnitTemplate end");
    }

    /**
     * List2ユニットテンプレート選択を行う
     *
     * @param event
     */
    @FXML
    public void onSelectList2UnitTemplate(ActionEvent event) {
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":onSelectList2UnitTemplate start");
        sc.blockUI(Boolean.TRUE);
        try {
            SelectDialogEntity<SelectedTableData<UnitTemplateInfoEntity>> selectDialogEntity = new SelectDialogEntity().multiSelectItems(selectedList2);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.unittemplate"), "UnitTemplateMultiSelectionCompo", selectDialogEntity);
            if (ret.equals(ButtonType.OK)) {

                Platform.runLater(() -> {
                    list2UnitTemplateList.getItems().clear();
                    list2UnitTemplateList.getItems().addAll(selectedList2);
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            sc.blockUI(Boolean.FALSE);
        }
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":onSelectList2UnitTemplate end");
    }

    /**
     * 登録処理を行う
     *
     * @param event
     */
    @FXML
    public void onRegistButton(ActionEvent event) {
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":onRegistButton start");
        this.storeSetting();
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":onRegistButton end");
    }

    /**
     * 各種設定読み込み処理後で外だし
     *
     */
    private void loadSetting() {
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":loadSetting start");
        sc.blockUI(Boolean.TRUE);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    loadPanelSetting();
                    loadGraphSetting();
                    loadListSetting();
                    loadList2Setting();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    sc.blockUI(Boolean.FALSE);
                }
                return null;
            }
        };
        new Thread(task).start();
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":loadSetting end");
    }

    /**
     * 検索設定書き込み処理
     *
     */
    private void storeSetting() {
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":storeSetting start");
        try {
            storePanelSetting();
            storeGraphSetting();
            storeListSetting();
            storeList2Setting();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(CellProductionMonitorSettingCompoFxController.class.getName() + ":storeSetting end");
    }

    private void loadPanelSetting() {
        String masterTitles = properties.getProperty(ClientPropertyConstants.PROP_KEY_MASTER_TITLE, ClientPropertyConstants.DEFAULT_MASTER_TITLES);
        String[] masterTitleList = masterTitles.split(COMMA_SPLIT);
        ObservableList<String> titleList = FXCollections.observableArrayList(masterTitleList);
        panelDisplayTitleCombo.setItems(titleList);
        String masterColumns = properties.getProperty(ClientPropertyConstants.PROP_KEY_MASTER_COLUMN_NUM, ClientPropertyConstants.DEFAULT_MASTER_COLUMN_NUM);
        String[] masterColumnList = masterColumns.split(COMMA_SPLIT);
        ObservableList<String> columnList = FXCollections.observableArrayList(masterColumnList);
        panelShowColumnNumCombo.setItems(columnList);
        String masterRows = properties.getProperty(ClientPropertyConstants.PROP_KEY_MASTER_ROW_NUM, ClientPropertyConstants.DEFAULT_MASTER_ROW_NUM);
        String[] masterRowList = masterRows.split(COMMA_SPLIT);
        ObservableList<String> rowList = FXCollections.observableArrayList(masterRowList);
        panelShowRowNumCombo.setItems(columnList);
        String selectTitle = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_TITLE, ClientPropertyConstants.DEFAULT_SELECT_TITLE);
        String selectColumnNum = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_COLUMN_NUM, ClientPropertyConstants.DEFAULT_SELECT_COLUMN_NUM);
        String selectRowNum = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_ROW_NUM, ClientPropertyConstants.DEFAULT_SELECT_ROW_NUM);
        Platform.runLater(() -> {
            panelDisplayTitleCombo.setValue("");
            panelShowColumnNumCombo.setValue("");
            panelShowRowNumCombo.setValue("");
            for (String title : titleList) {
                if (selectTitle.equals(title)) {
                    panelDisplayTitleCombo.setValue(title);
                }
            }
            for (String column : columnList) {
                if (selectColumnNum.equals(column)) {
                    panelShowColumnNumCombo.setValue(column);
                }
            }
            for (String row : rowList) {
                if (selectRowNum.equals(row)) {
                    panelShowRowNumCombo.setValue(row);
                }
            }
        });

        String text = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_UNITTEMPLATE, "0");
        String[] values = text.split(",");
        List<Long> unitTemplateIds = new ArrayList<>();
        for (String value : values) {
            try {
                Long unitTemplateId = Long.parseLong(value);
                if (unitTemplateId != 0L) {
                    unitTemplateIds.add(unitTemplateId);
                }
            } catch (NumberFormatException e) {
                logger.info("not number:" + value);
            }
        }

        if (!unitTemplateIds.isEmpty()) {
            List<UnitTemplateInfoEntity> entities = RestAPI.getUnitTemplate(unitTemplateIds);
            for (UnitTemplateInfoEntity entity : entities) {
                this.selectedPanel.add(new SelectedTableData<>(entity.getUnitTemplateName(), entity));
            }
        }

        Platform.runLater(() -> {
            panelUnitTemplateList.getItems().clear();
            panelUnitTemplateList.getItems().addAll(selectedPanel);
        });
    }

    private void storePanelSetting() throws IOException {
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_TITLE, panelDisplayTitleCombo.getSelectionModel().getSelectedItem());
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_COLUMN_NUM, panelShowColumnNumCombo.getSelectionModel().getSelectedItem());
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_ROW_NUM, panelShowRowNumCombo.getSelectionModel().getSelectedItem());
        String colomunNum = properties.getProperty(ClientPropertyConstants.PROP_KEY_MASTER_COLUMN_NUM, "");
        if (colomunNum.isEmpty()) {
            AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_MASTER_COLUMN_NUM, ClientPropertyConstants.DEFAULT_MASTER_COLUMN_NUM);
        }
        String rowNum = properties.getProperty(ClientPropertyConstants.PROP_KEY_MASTER_ROW_NUM, "");
        if (rowNum.isEmpty()) {
            AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_MASTER_ROW_NUM, ClientPropertyConstants.DEFAULT_MASTER_ROW_NUM);
        }
        StringBuilder panelTempIds = new StringBuilder();
        for (int i = 0; i < selectedPanel.size(); i++) {
            panelTempIds.append(selectedPanel.get(i).getItem().getUnitTemplateId());
            if ((i + 1) < selectedPanel.size()) {
                panelTempIds.append(",");
            }
        }
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_UNITTEMPLATE, panelTempIds.toString());
        AdProperty.store(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
    }

    private void loadGraphSetting() {
        String masterTitles = properties.getProperty(ClientPropertyConstants.PROP_KEY_MASTER_TITLE, ClientPropertyConstants.DEFAULT_MASTER_TITLES);
        String[] masterTitleList = masterTitles.split(COMMA_SPLIT);
        ObservableList<String> titleList = FXCollections.observableArrayList(masterTitleList);
        graphDisplayTitleCombo.setItems(titleList);
        String masterColumns = properties.getProperty(ClientPropertyConstants.PROP_KEY_MASTER_COLUMN_NUM, ClientPropertyConstants.DEFAULT_MASTER_COLUMN_NUM);
        String[] masterColumnList = masterColumns.split(COMMA_SPLIT);
        ObservableList<String> columnList = FXCollections.observableArrayList(masterColumnList);
        graphShowColumnNumCombo.setItems(columnList);
        String masterRows = properties.getProperty(ClientPropertyConstants.PROP_KEY_MASTER_ROW_NUM, ClientPropertyConstants.DEFAULT_MASTER_ROW_NUM);
        String[] masterRowList = masterRows.split(COMMA_SPLIT);
        ObservableList<String> rowList = FXCollections.observableArrayList(masterRowList);
        graphShowRowNumCombo.setItems(columnList);
        String selectTitle = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_GRAPH_TITLE, ClientPropertyConstants.DEFAULT_SELECT_TITLE);
        String selectColumnNum = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_GRAPH_COLUMN_NUM, ClientPropertyConstants.DEFAULT_SELECT_COLUMN_NUM);
        String selectRowNum = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_GRAPH_ROW_NUM, ClientPropertyConstants.DEFAULT_SELECT_ROW_NUM);
        Platform.runLater(() -> {
            graphDisplayTitleCombo.setValue("");
            graphShowColumnNumCombo.setValue("");
            graphShowRowNumCombo.setValue("");
            for (String title : titleList) {
                if (selectTitle.equals(title)) {
                    graphDisplayTitleCombo.setValue(title);
                }
            }
            for (String column : columnList) {
                if (selectColumnNum.equals(column)) {
                    graphShowColumnNumCombo.setValue(column);
                }
            }
            for (String row : rowList) {
                if (selectRowNum.equals(row)) {
                    graphShowRowNumCombo.setValue(row);
                }
            }
        });

        String text = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_GRAPH_UNITTEMPLATE, "0");
        String[] values = text.split(",");
        List<Long> unitTemplateIds = new ArrayList<>();
        for (String value : values) {
            try {
                Long unitTemplateId = Long.parseLong(value);
                if (unitTemplateId != 0L) {
                    unitTemplateIds.add(unitTemplateId);
                }
            } catch (NumberFormatException e) {
                logger.info("not number:" + value);
            }
        }

        if (!unitTemplateIds.isEmpty()) {
            List<UnitTemplateInfoEntity> entities = RestAPI.getUnitTemplate(unitTemplateIds);
            for (UnitTemplateInfoEntity entity : entities) {
                this.selectedGraph.add(new SelectedTableData<>(entity.getUnitTemplateName(), entity));
            }
        }

        Platform.runLater(() -> {
            graphUnitTemplateList.getItems().clear();
            graphUnitTemplateList.getItems().addAll(selectedGraph);
        });
    }

    private void storeGraphSetting() throws IOException {
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_SELECT_GRAPH_TITLE, graphDisplayTitleCombo.getSelectionModel().getSelectedItem());
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_SELECT_GRAPH_COLUMN_NUM, graphShowColumnNumCombo.getSelectionModel().getSelectedItem());
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_SELECT_GRAPH_ROW_NUM, graphShowRowNumCombo.getSelectionModel().getSelectedItem());
        StringBuilder graphTempIds = new StringBuilder();
        for (int i = 0; i < selectedGraph.size(); i++) {
            graphTempIds.append(selectedGraph.get(i).getItem().getUnitTemplateId());
            if ((i + 1) < selectedGraph.size()) {
                graphTempIds.append(",");
            }
        }
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_SELECT_GRAPH_UNITTEMPLATE, graphTempIds.toString());
        AdProperty.store(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
    }

    private void loadListSetting() {
        String colomunTitles = properties.getProperty(ClientPropertyConstants.PROP_KEY_MASTER_TITLE, ClientPropertyConstants.DEFAULT_MASTER_TITLES);
        String[] colomunTitleList = colomunTitles.split(COMMA_SPLIT);
        ObservableList<String> titleList = FXCollections.observableArrayList(colomunTitleList);
        listMainTitleColumnCombo.setItems(titleList);
        listSubTitleColumnCombo.setItems(titleList);
        String selectMainTitle = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_MAIN_TITLE_COLUMN, ClientPropertyConstants.DEFAULT_SELECT_MAIN_TITLE);
        String selectSubTitle = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_SUB_TITLE_COLUMN, ClientPropertyConstants.DEFAULT_SELECT_SUB_TITLE);
        Platform.runLater(() -> {
            listMainTitleColumnCombo.setValue("");
            listSubTitleColumnCombo.setValue("");
            for (String title : titleList) {
                if (selectMainTitle.equals(title)) {
                    listMainTitleColumnCombo.setValue(title);
                }
                if (selectSubTitle.equals(title)) {
                    listSubTitleColumnCombo.setValue(title);
                }
            }
        });

        String text = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_UNITTEMPLATE, "0");
        String[] values = text.split(",");
        List<Long> unitTemplateIds = new ArrayList<>();
        for (String value : values) {
            try {
                Long unitTemplateId = Long.parseLong(value);
                if (unitTemplateId != 0L) {
                    unitTemplateIds.add(unitTemplateId);
                }
            } catch (NumberFormatException e) {
                logger.info("not number:" + value);
            }
        }

        if (!unitTemplateIds.isEmpty()) {
            List<UnitTemplateInfoEntity> entities = RestAPI.getUnitTemplate(unitTemplateIds);
            for (UnitTemplateInfoEntity entity : entities) {
                this.selectedList.add(new SelectedTableData<>(entity.getUnitTemplateName(), entity));
            }
        }

        Platform.runLater(() -> {
            listUnitTemplateList.getItems().clear();
            listUnitTemplateList.getItems().addAll(selectedList);
        });
    }

    private void storeListSetting() throws IOException {
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_MAIN_TITLE_COLUMN, listMainTitleColumnCombo.getSelectionModel().getSelectedItem());
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_SUB_TITLE_COLUMN, listSubTitleColumnCombo.getSelectionModel().getSelectedItem());
        String colomunTitles = properties.getProperty(ClientPropertyConstants.PROP_KEY_MASTER_TITLE, "");
        if (colomunTitles.isEmpty()) {
            AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_MASTER_TITLE, ClientPropertyConstants.DEFAULT_MASTER_TITLES);
        }
        StringBuilder listTempIds = new StringBuilder();
        for (int i = 0; i < selectedList.size(); i++) {
            listTempIds.append(selectedList.get(i).getItem().getUnitTemplateId());
            if ((i + 1) < selectedList.size()) {
                listTempIds.append(",");
            }
        }
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_UNITTEMPLATE, listTempIds.toString());
        AdProperty.store(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
    }

    private void loadList2Setting() {
        String colomunTitles = properties.getProperty(ClientPropertyConstants.PROP_KEY_LIST2_TITLE_COLUMN, "");
        String holiday = properties.getProperty(ClientPropertyConstants.PROP_KEY_LIST2_HOLIDAY_FILE, "");

        Platform.runLater(() -> {
            list2TitleColumn.setText(colomunTitles);
            list2HolidayFile.setText(holiday);
        });

        String text = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST2_UNITTEMPLATE, "0");
        String[] values = text.split(",");
        List<Long> unitTemplateIds = new ArrayList<>();
        for (String value : values) {
            try {
                Long unitTemplateId = Long.parseLong(value);
                if (unitTemplateId != 0L) {
                    unitTemplateIds.add(unitTemplateId);
                }
            } catch (NumberFormatException e) {
                logger.info("not number:" + value);
            }
        }

        if (!unitTemplateIds.isEmpty()) {
            List<UnitTemplateInfoEntity> entities = RestAPI.getUnitTemplate(unitTemplateIds);
            for (UnitTemplateInfoEntity entity : entities) {
                this.selectedList2.add(new SelectedTableData<>(entity.getUnitTemplateName(), entity));
            }
        }

        Platform.runLater(() -> {
            list2UnitTemplateList.getItems().clear();
            list2UnitTemplateList.getItems().addAll(selectedList2);
        });
    }

    private void storeList2Setting() throws IOException {
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_LIST2_TITLE_COLUMN, list2TitleColumn.getText());
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_LIST2_HOLIDAY_FILE, list2HolidayFile.getText());

        StringBuilder listTempIds = new StringBuilder();
        for (int i = 0; i < selectedList2.size(); i++) {
            listTempIds.append(selectedList2.get(i).getItem().getUnitTemplateId());
            if ((i + 1) < selectedList2.size()) {
                listTempIds.append(",");
            }
        }
        AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST2_UNITTEMPLATE, listTempIds.toString());
        AdProperty.store(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
    }

}
