/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.selectcompo;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.javafxcommon.WorkHierarchyEditor;
import jp.adtekfuji.javafxcommon.WorkflowEditPermanenceData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程選択クラス
 *
 * @author yu.kikukawa
 */
@FxComponent(id = "WorksSelectionCompo", fxmlPath = "/fxml/compo/works_selection_compo.fxml")
public class WorksSelectionCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();

    private final WorkflowEditPermanenceData permanenceData = WorkflowEditPermanenceData.getInstance();
    private WorkHierarchyEditor workHierarchyEditor;
    private TreeItem<WorkHierarchyInfoEntity> selectedWorkHierarchy;
    private SelectDialogEntity settingDialogEntity;
    private final static  Map<Long, TreeItem> treeItems = new HashMap<>();

    private boolean showLatestOnly = true;
    private boolean finalApproveOnly = false;

    @FXML
    private TreeView<WorkHierarchyInfoEntity> hierarchyTree;
    @FXML
    private StackPane stackPane;
    @FXML
    private ListView<WorkInfoEntity> workList;
    @FXML
    private ListView<WorkInfoEntity> itemList;
    @FXML
    private Pane progressPane;

    @FXML
    private RadioButton showLatestOnlyRButton;
    @FXML
    private ToggleGroup showingToggleGroup;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.workList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.itemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        permanenceData.updateTitle();
        Callback<ListView<WorkInfoEntity>, ListCell<WorkInfoEntity>> cellFactory1 = (ListView<WorkInfoEntity> param) -> new ListItemCell();
        this.workList.setCellFactory(cellFactory1);
        Callback<ListView<WorkInfoEntity>, ListCell<WorkInfoEntity>> cellFactory2 = (ListView<WorkInfoEntity> param) -> new ListItemCell();
        this.itemList.setCellFactory(cellFactory2);
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectDialogEntity) {
            this.settingDialogEntity = (SelectDialogEntity) argument;

            this.itemList.getItems().addAll(settingDialogEntity.getWorks());
            this.settingDialogEntity.worksProperty().bind(itemList.itemsProperty());

            this.hierarchyTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (Objects.nonNull(newValue) && newValue.getValue().getWorkHierarchyId() != 0) {
                    this.updateListView(newValue.getValue().getWorkInfoCollection());
                } else {
                    this.clearWorkList();
                }
                this.selectedWorkHierarchy = newValue;
            });

            this.showingToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                this.showLatestOnly = newValue.equals(this.showLatestOnlyRButton);
                this.updateListView();
            });

            boolean isLicensedApproval = ClientServiceProperty.isLicensed(LicenseOptionType.ApprovalOption.getName());
            if (isLicensedApproval) {
                // 承認機能が有効な場合、未承認は非表示。
                this.finalApproveOnly = true;

                // Ctrl + Shift + L or M キーで未承認の表示/非表示を切り替える。
                this.stackPane.setOnKeyPressed((KeyEvent event) -> {
                    if (event.isControlDown() && event.isShiftDown()) {
                        if (Objects.equals(event.getCode(), KeyCode.L)) {
                            // 未承認を表示する。
                            logger.info("Pressed CTRL + SHIFT + L Key.");
                            this.finalApproveOnly = false;
                            this.updateListView();
                        } else if (Objects.equals(event.getCode(), KeyCode.M)) {
                            // 未承認を非表示にする。
                            logger.info("Pressed CTRL + SHIFT + M Key.");
                            this.finalApproveOnly = true;
                            this.updateListView();
                        } 
                    }
                });
            } else {
                // 承認機能が無効の場合、未承認も表示する。
                this.finalApproveOnly = false;
            }

            this.blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        workHierarchyEditor = new WorkHierarchyEditor(hierarchyTree, permanenceData.getWorkHierarchyRootItem(), progressPane, treeItems);
                        workHierarchyEditor.createRoot(null);
                    } finally {
                        Platform.runLater(() -> blockUI(false));
                    }
                    return null;
                }
            };
            new Thread(task).start();
        }
    }

    /**
     * ListView表示用セル
     *
     */
    class ListItemCell extends ListCell<WorkInfoEntity> {

        @Override
        protected void updateItem(WorkInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setText(item.getWorkName() + " : " + item.getWorkRev().toString());
            } else {
                setText("");
            }
        }
    }

    /**
     * リスト更新
     */
    private void updateListView() {
        if (Objects.isNull(this.selectedWorkHierarchy)) {
            return;
        }

        this.updateListView(this.selectedWorkHierarchy.getValue().getWorkInfoCollection());
    }

    /**
     * リスト更新
     *
     * @param entitys
     */
    private void updateListView(List<WorkInfoEntity> works) {
        List<WorkInfoEntity> showItems;

        workList.getItems().clear();

        List<WorkInfoEntity> targetWorks;
        if (finalApproveOnly) {
            targetWorks = works.stream()
                    .filter(p -> Objects.equals(p.getApprovalState(), ApprovalStatusEnum.FINAL_APPROVE))
                    .collect(Collectors.toList());
        } else {
            targetWorks = works;
        }

        if (showLatestOnly) {
            Map<String, Optional<WorkInfoEntity>> latestRevMap = targetWorks.stream()
                    .collect(Collectors.groupingBy(WorkInfoEntity::getWorkName,
                            Collectors.maxBy(Comparator.comparingLong(WorkInfoEntity::getWorkRev))));
            showItems = latestRevMap.values().stream()
                    .filter(p -> p.isPresent())
                    .map(p -> p.get())
                    .collect(Collectors.toList());
        } else {
            showItems = targetWorks;
        }

        showItems.sort(Comparator.comparing(workflow -> workflow.getWorkName()));
        workList.setItems(FXCollections.observableArrayList(showItems));
    }

    /**
     * リストの初期化
     *
     */
    private void clearWorkList() {
        workList.getItems().clear();
        workList.getSelectionModel().clearSelection();
        itemList.getItems().clear();
        itemList.getSelectionModel().clearSelection();
    }

    /**
     * 「常に最新版を使用する」チェックボックス変更イベント
     *
     * @param o
     * @param oldValue
     * @param newValue 
     */
    private void changedEnableLatestOnly(ObservableValue o, Boolean oldValue, Boolean newValue) {
        settingDialogEntity.setUseLatestRev(newValue);
    }

    @FXML
    private void OnAdd(ActionEvent event) {
        for (WorkInfoEntity item : this.workList.getSelectionModel().getSelectedItems()){
            if (!this.itemList.getItems().contains(item)) {
                this.itemList.getItems().add(item);
            }
        }
    }

    @FXML
    private void OnRemove(ActionEvent event) {
        if (Objects.nonNull(this.itemList.getSelectionModel().getSelectedIndices())) {
            this.itemList.getSelectionModel()
                         .getSelectedIndices()
                         .stream()
                         .sorted(Comparator.reverseOrder())
                         .mapToInt(Integer::intValue)
                         .forEach(index -> this.itemList.getItems().remove(index));
                    
        }
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        stackPane.setDisable(flg);
        progressPane.setVisible(flg);
    }
}
