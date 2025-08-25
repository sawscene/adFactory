/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.dialog;

import javafx.scene.control.*;
import jp.adtekfuji.forfujiapp.dialog.entity.SelectedTableData;
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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.UIControlInterface;
import jp.adtekfuji.forfujiapp.dialog.entity.SelectDialogEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.forfujiapp.javafx.tree.edior.UnitTemplateTreeEditor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレート選択ダイアログ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.7.Wen
 */
@FxComponent(id = "UnitTemplateMultiSelectionCompo", fxmlPath = "/fxml/dialog/unittemplateMultiSelectionDialog.fxml")
public class UnittemplateMultiSelectionDialog implements Initializable, ArgumentDelivery, UIControlInterface {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle RB = LocaleUtils.getBundle("locale.locale");
    private final static long ROOT_ID = 0;

    @FXML
    private AnchorPane base;
    @FXML
    private TreeView<UnitTemplateHierarchyInfoEntity> unittemplateTreeView;
    @FXML
    private TableView<SelectedTableData<UnitTemplateInfoEntity>> unittemplateTableView;
    @FXML
    private TableColumn<SelectedTableData<UnitTemplateInfoEntity>, Boolean> unittemplateSelectColumn;
    @FXML
    private TableColumn<SelectedTableData<UnitTemplateInfoEntity>, String> unittemplateNameColumn;
    @FXML
    private TableView<SelectedTableData<UnitTemplateInfoEntity>> selectedUnittemplateTableView;
    @FXML
    private TableColumn<SelectedTableData<UnitTemplateInfoEntity>, Boolean> selectedUnittemplateSelectColumn;
    @FXML
    private TableColumn<SelectedTableData<UnitTemplateInfoEntity>, String> selectedUnittemplateNameColumn;
    @FXML
    private Pane progressPane;

    /**
     * 初期処理
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info(UnittemplateMultiSelectionDialog.class.getName() + ":initialize start");

        unittemplateTableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        selectedUnittemplateTableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        this.progressPane.setVisible(false);

        // レイアウト設定        
        this.base.setPrefSize(sc.getStage().getWidth() - 128, sc.getStage().getHeight() - 128);
        CheckBox tableCheck = new CheckBox();
        tableCheck.setOnAction((ActionEvent ActionEvent) -> {
            if (tableCheck.isSelected()) {
                for (SelectedTableData<UnitTemplateInfoEntity> data : unittemplateTableView.getItems()) {
                    data.setIsSelect(Boolean.TRUE);
                }
            } else {
                for (SelectedTableData<UnitTemplateInfoEntity> data : unittemplateTableView.getItems()) {
                    data.setIsSelect(Boolean.FALSE);
                }
            }
        });
        this.unittemplateSelectColumn.setGraphic(tableCheck);
        this.unittemplateSelectColumn.setCellValueFactory(new PropertyValueFactory<>("isSelect"));
        this.unittemplateNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.unittemplateSelectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(unittemplateSelectColumn));
        this.unittemplateSelectColumn.setEditable(true);
        this.unittemplateTableView.setEditable(true);

        CheckBox selectedTableCheck = new CheckBox();
        selectedTableCheck.setOnAction((ActionEvent ActionEvent) -> {
            if (selectedTableCheck.isSelected()) {
                for (SelectedTableData<UnitTemplateInfoEntity> data : selectedUnittemplateTableView.getItems()) {
                    data.setIsSelect(Boolean.TRUE);
                }
            } else {
                for (SelectedTableData<UnitTemplateInfoEntity> data : selectedUnittemplateTableView.getItems()) {
                    data.setIsSelect(Boolean.FALSE);
                }
            }
        });
        this.selectedUnittemplateSelectColumn.setGraphic(selectedTableCheck);
        this.selectedUnittemplateSelectColumn.setCellValueFactory(new PropertyValueFactory<>("isSelect"));
        this.selectedUnittemplateNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.selectedUnittemplateSelectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedUnittemplateSelectColumn));
        this.selectedUnittemplateSelectColumn.setEditable(true);
        this.selectedUnittemplateTableView.setEditable(true);

        //階層生成処理・選択時のイベントを注入
        this.createWorkTree();

        logger.info(UnittemplateMultiSelectionDialog.class.getName() + ":initialize end");
    }

    /**
     * 検索条件の引き取り処理
     *
     * @param argument 検索条件
     */
    @Override
    public void setArgument(Object argument) {
        logger.info(UnittemplateMultiSelectionDialog.class.getName() + ":setArgument start");
        if (argument instanceof SelectDialogEntity) {
            SelectDialogEntity<SelectedTableData<UnitTemplateInfoEntity>> settingDialogEntity = (SelectDialogEntity<SelectedTableData<UnitTemplateInfoEntity>>) argument;
            selectedUnittemplateTableView.itemsProperty().bindBidirectional(settingDialogEntity.multiSelectItemsProperty());
        }

        // 設定ファイルと設定項目を作成して引継ぎ処理を実装
        // ウィンドウがいつ閉じるのか拾えないので設定項目と選択条件はバインディング必須
        logger.info(UnittemplateMultiSelectionDialog.class.getName() + ":setArgument end");
    }

    /**
     * 検索対象の工程リストにアイテムを追加
     *
     * @param event
     */
    @FXML
    public void onUnittemplateAdd(ActionEvent event) {
        Platform.runLater(() -> {
            logger.info(UnittemplateMultiSelectionDialog.class.getName() + ":onUnittemplateAdd start");
            List<SelectedTableData<UnitTemplateInfoEntity>> addItems = new ArrayList<>();
            for (SelectedTableData<UnitTemplateInfoEntity> data : this.unittemplateTableView.getItems()) {
                boolean isOrverLap = false;
                if (data.getIsSelect()) {
                    // 選択が重複していないか確認
                    for (SelectedTableData<UnitTemplateInfoEntity> selectData : selectedUnittemplateTableView.getItems()) {
                        if (data.getItem().equals(selectData.getItem())) {
                            isOrverLap = true;
                        }
                    }
                    if (!isOrverLap) {
                        addItems.add(new SelectedTableData<>(data.getName(), data.getItem()));
                    }
                }
            }
            selectedUnittemplateTableView.getItems().addAll(addItems);
            logger.info(UnittemplateMultiSelectionDialog.class.getName() + ":onUnittemplateAdd end");
        });
    }

    /**
     * 検索対象の工程リストからアイテムを削除
     *
     * @param event
     */
    @FXML
    public void onUnittemplateDelete(ActionEvent event) {
        Platform.runLater(() -> {
            logger.info(UnittemplateMultiSelectionDialog.class.getName() + ":onUnittemplateDelete start");

            List<SelectedTableData<UnitTemplateInfoEntity>> items = new ArrayList<>();
            for (SelectedTableData<UnitTemplateInfoEntity> data : this.selectedUnittemplateTableView.getItems()) {
                if (data.getIsSelect()) {
                    items.add(data);
                }
            }
            this.selectedUnittemplateTableView.getItems().removeAll(items);

            logger.info(UnittemplateMultiSelectionDialog.class.getName() + ":onUnittemplateDelete end");
        });
    }

    /**
     * 工程階層の生成
     *
     */
    private void createWorkTree() {
        logger.info(UnittemplateMultiSelectionDialog.class.getName() + ":createWorkTree start");

        // 階層選択時
        unittemplateTreeView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<UnitTemplateHierarchyInfoEntity>> observable, TreeItem<UnitTemplateHierarchyInfoEntity> oldValue, TreeItem<UnitTemplateHierarchyInfoEntity> newValue) -> {
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        if (Objects.nonNull(newValue) && newValue.getValue().getUnitTemplateHierarchyId() != ROOT_ID) {
                            List<UnitTemplateInfoEntity> entities = RestAPI.getUnitTemplateByHierarchyId(newValue.getValue().getUnitTemplateHierarchyId());
                            Platform.runLater(() -> {
                                createUnitTemplateList(entities);
                            });
                        } else {
                            Platform.runLater(() -> {
                                unittemplateTableView.getItems().clear();
                                unittemplateTableView.getSelectionModel().clearSelection();
                            });
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    blockUI(false);
                }

                @Override
                protected void failed() {
                    super.failed();
                    blockUI(false);
                }
            };
            new Thread(task).start();
        });

        //ツリー生成用のクラスを初期化
        Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("/image/folder_top.png")));
        UnitTemplateTreeEditor unitTemplateTreeEditor = new UnitTemplateTreeEditor(
                unittemplateTreeView, new TreeItem<>(new UnitTemplateHierarchyInfoEntity(0L, null, LocaleUtils.getString("key.UnitTemplateHierarchy")), rootIcon), this);

        logger.info(UnittemplateMultiSelectionDialog.class.getName() + ":createWorkTree end");
    }

    /**
     * 工程リストの生成
     *
     * @param workInfoEntitys
     */
    private void createUnitTemplateList(List<UnitTemplateInfoEntity> workInfoEntitys) {
        logger.info(UnittemplateMultiSelectionDialog.class.getName() + ":createWorkList start");
        List<SelectedTableData<UnitTemplateInfoEntity>> datas = new ArrayList<>();
        for (UnitTemplateInfoEntity entity : workInfoEntitys) {
            datas.add(new SelectedTableData<>(entity.getUnitTemplateName(), entity));
        }
        ObservableList<SelectedTableData<UnitTemplateInfoEntity>> list = FXCollections.observableArrayList(datas);
        this.unittemplateTableView.setItems(list);
        this.unittemplateTableView.getSortOrder().add(this.unittemplateNameColumn);
        logger.info(UnittemplateMultiSelectionDialog.class.getName() + ":createWorkList end");
    }

    @Override
    public void updateUI() {
    }

    @Override
    public void blockUI(boolean isBlock) {
        sc.blockUI(isBlock);
        progressPane.setVisible(isBlock);
    }

}
