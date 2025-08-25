/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.dialog;

import adtekfuji.admanagerapp.analysisplugin.common.ActualSearchSettingData;
import adtekfuji.admanagerapp.analysisplugin.common.WorkTableData;
import javafx.scene.control.*;
import jp.adtekfuji.forfujiapp.javafx.tree.edior.WorkHierarchyEditor;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 検索画面クラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.01.Mon
 */
@FxComponent(id = "ActualSearchDialog", fxmlPath = "/fxml/dialog/actualSearchDialog.fxml")
public class ActualSearchDialog extends Window implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static long ROOT_ID = 0;

    @FXML
    private AnchorPane base;
    @FXML
    private DatePicker workStartDatePicker;
    @FXML
    private DatePicker workEndDatePicker;
    @FXML
    private TreeView<WorkHierarchyInfoEntity> workTreeView;
    @FXML
    private TableView<WorkTableData> workTableView;
    @FXML
    private TableColumn<WorkTableData, Boolean> workSelectColumn;
    @FXML
    private TableColumn<WorkTableData, String> workNameColumn;
    @FXML
    private TableView<WorkTableData> selectedWorkTableView;
    @FXML
    private TableColumn<WorkTableData, Boolean> selectedWorkSelectColumn;
    @FXML
    private TableColumn<WorkTableData, String> selectedWorkNameColumn;
    @FXML
    private StackPane progressPane;

    /**
     * 初期処理
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info(ActualSearchDialog.class.getName() + ":initialize start");

        workTableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        selectedWorkTableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        // レイアウト設定        
        this.base.setPrefSize(sc.getStage().getWidth() - 128, sc.getStage().getHeight() - 128);
        
        CheckBox tableCheck = new CheckBox();
        tableCheck.setOnAction((ActionEvent ActionEvent) -> {
            if (tableCheck.isSelected()) {
                for (WorkTableData data : workTableView.getItems()) {
                    data.setIsSelect(Boolean.TRUE);
                }
            } else {
                for (WorkTableData data : workTableView.getItems()) {
                    data.setIsSelect(Boolean.FALSE);
                }
            }
        });
        this.workSelectColumn.setGraphic(tableCheck);
        this.workSelectColumn.setCellValueFactory(new PropertyValueFactory<>("isSelect"));
        this.workNameColumn.setCellValueFactory(new PropertyValueFactory<>("workName"));
        this.workSelectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(workSelectColumn));
        this.workSelectColumn.setEditable(true);
        this.workTableView.setEditable(true);

        CheckBox selectedTableCheck = new CheckBox();
        selectedTableCheck.setOnAction((ActionEvent ActionEvent) -> {
            if (selectedTableCheck.isSelected()) {
                for (WorkTableData data : selectedWorkTableView.getItems()) {
                    data.setIsSelect(Boolean.TRUE);
                }
            } else {
                for (WorkTableData data : selectedWorkTableView.getItems()) {
                    data.setIsSelect(Boolean.FALSE);
                }
            }
        });
        this.selectedWorkSelectColumn.setGraphic(selectedTableCheck);
        this.selectedWorkSelectColumn.setCellValueFactory(new PropertyValueFactory<>("isSelect"));
        this.selectedWorkNameColumn.setCellValueFactory(new PropertyValueFactory<>("workName"));
        this.selectedWorkSelectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedWorkSelectColumn));
        this.selectedWorkSelectColumn.setEditable(true);
        this.selectedWorkTableView.setEditable(true);

        //階層生成処理・選択時のイベントを注入
        this.createWorkTree();

        logger.info(ActualSearchDialog.class.getName() + ":initialize end");
    }

    /**
     * 検索条件の引き取り処理
     *
     * @param argument 検索条件
     */
    @Override
    public void setArgument(Object argument) {
        logger.info(ActualSearchDialog.class.getName() + ":setArgument start");
        if (argument instanceof ActualSearchSettingData) {
            ActualSearchSettingData data = (ActualSearchSettingData) argument;
            workStartDatePicker.valueProperty().bindBidirectional(data.startDatetimeProperty());
            workEndDatePicker.valueProperty().bindBidirectional(data.endDatetimeProperty());
            selectedWorkTableView.itemsProperty().bindBidirectional(data.workTabelDatasProperty());
        }

        // 設定ファイルと設定項目を作成して引継ぎ処理を実装
        // ウィンドウがいつ閉じるのか拾えないので設定項目と選択条件はバインディング必須
        logger.info(ActualSearchDialog.class.getName() + ":setArgument end");
    }

    /**
     * 検索対象の工程リストにアイテムを追加
     *
     * @param event
     */
    @FXML
    public void onWorkAdd(ActionEvent event) {
        Platform.runLater(() -> {
            logger.info(ActualSearchDialog.class.getName() + ":onWorkAdd start");
            List<WorkTableData> addItems = new ArrayList<>();
            for (WorkTableData data : this.workTableView.getItems()) {
                boolean isOrverLap = false;
                if (data.getIsSelect()) {
                    // 選択が重複していないか確認
                    for (WorkTableData selectData : selectedWorkTableView.getItems()) {
                        if (data.getItem().equals(selectData.getItem())) {
                            isOrverLap = true;
                        }
                    }
                    if (!isOrverLap) {
                        addItems.add(new WorkTableData(data.getWorkName(), data.getItem()));
                    }
                }
            }
            selectedWorkTableView.getItems().addAll(addItems);
            logger.info(ActualSearchDialog.class.getName() + ":onWorkAdd end");
        });
    }

    /**
     * 検索対象の工程リストからアイテムを削除
     *
     * @param event
     */
    @FXML
    public void onWorkDelete(ActionEvent event) {
        Platform.runLater(() -> {
            logger.info(ActualSearchDialog.class.getName() + ":onWorkDelete start");

            List<WorkTableData> items = new ArrayList<>();
            for (WorkTableData data : this.selectedWorkTableView.getItems()) {
                if (data.getIsSelect()) {
                    items.add(data);
                }
            }
            this.selectedWorkTableView.getItems().removeAll(items);

            logger.info(ActualSearchDialog.class.getName() + ":onWorkDelete end");
        });
    }

    /**
     * 工程階層の生成
     *
     */
    private void createWorkTree() {
        logger.info(ActualSearchDialog.class.getName() + ":createWorkTree start");

        //階層生成処理・選択時のイベントを注入
        new WorkHierarchyEditor(this.workTreeView, new TreeItem<>(new WorkHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.ProcessHierarch"))), progressPane).start();
        this.workTreeView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<WorkHierarchyInfoEntity>> observable, TreeItem<WorkHierarchyInfoEntity> oldValue, TreeItem<WorkHierarchyInfoEntity> newValue) -> {
            if (Objects.nonNull(newValue) && newValue.getValue().getWorkHierarchyId() != ROOT_ID) {
                // 工程リスト更新
                createWorkList(newValue.getValue().getWorkInfoCollection());
            } else {
                // 工程リストクリア
                Platform.runLater(() -> {
                    this.workTableView.getItems().clear();
                    this.workTableView.getSelectionModel().clearSelection();
                });
            }
        });

        logger.info(ActualSearchDialog.class.getName() + ":createWorkTree end");
    }

    /**
     * 工程リストの生成
     *
     * @param workInfoEntitys
     */
    private void createWorkList(List<WorkInfoEntity> workInfoEntitys) {
        logger.info(ActualSearchDialog.class.getName() + ":createWorkList start");

        List<WorkTableData> datas = new ArrayList<>();
        for (WorkInfoEntity entity : workInfoEntitys) {
            datas.add(new WorkTableData(entity.getWorkName(), entity));
        }

        Platform.runLater(() -> {
            ObservableList<WorkTableData> list = FXCollections.observableArrayList(datas);
            this.workTableView.setItems(list);
            this.workTableView.getSortOrder().add(this.workNameColumn);
        });

        logger.info(ActualSearchDialog.class.getName() + ":createWorkList end");
    }

}
