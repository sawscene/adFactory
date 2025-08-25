/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.dsitemeditplugin.controller;

import adtekfuji.admanagerapp.dsitemeditplugin.common.Constants;
import adtekfuji.clientservice.DsItemFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.Tuple;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.SampleResponse;
import jp.adtekfuji.adFactory.entity.job.MstDsItemInfo;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import jp.adtekfuji.javafxcommon.dialog.MessageDialog;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 品番一覧画面
 * 
 * @author s-heya
 */
@FxComponent(id = "DsItemListCompo", fxmlPath = "/adtekfuji/admanagerapp/dsitemeditplugin/dsitem_list_compo.fxml")
public class DsItemListController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final DsItemFacade facade = new DsItemFacade();
    private final ObservableList<MstDsItemInfo> rows = FXCollections.observableArrayList();
    private final Map<String, Integer> lineMap = new LinkedHashMap<>();
    private boolean abort = false;

    @FXML
    private RestrictedTextField productNoFiled;
    @FXML
    private ComboBox<String> lineComboBox;
    @FXML
    private PropertySaveTableView<MstDsItemInfo> dsItemTable;
    @FXML
    private TableColumn<MstDsItemInfo, String> productNoColumn;
    @FXML
    private TableColumn<MstDsItemInfo, String> productNameColumn;
    @FXML
    private TableColumn<MstDsItemInfo, String> specColumn;
    @FXML
    private TableColumn<MstDsItemInfo, String> location1Column;
    @FXML
    private TableColumn<MstDsItemInfo, String> location2Column;
    @FXML
    private TableColumn<MstDsItemInfo, String> bomColumn;
    @FXML
    private TableColumn<MstDsItemInfo, String> workflow1Column;
    @FXML
    private TableColumn<MstDsItemInfo, String> workflow2Column;
    @FXML
    private Pane progressPane;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
  
    
    /**
     * 品番一覧画面を初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("initialize start.");
        try {
        
            AdProperty.load(Constants.UI_PROPERTY_NAME, Constants.UI_PROPERTY_NAME + ".properties");
            Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);
            //String prefix = DsItemListController.class.getName() + ".";

            this.editButton.setDisable(true);
            this.deleteButton.setDisable(true);

            LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
            if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
                this.addButton.setDisable(true);
            }
            
            this.lineMap.put("補給生産", 1);
            this.lineMap.put("検査", 2);

            this.lineComboBox.getItems().addAll(this.lineMap.keySet());
            this.lineComboBox.setValue(properties.getProperty(Constants.PROPERTY_LINE_NAME, Constants.LINE_PRODUCTION));
            this.lineComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    logger.info("Selected Line: {}", newValue);
                    properties.setProperty(Constants.PROPERTY_LINE_NAME, newValue);
                    AdProperty.store(Constants.UI_PROPERTY_NAME);
                } catch (Exception ex) {
                    logger.fatal(ex);
                }
            });
            
            this.dsItemTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
            this.dsItemTable.init("dsItemTable");
            this.dsItemTable.setItems(rows);
            this.dsItemTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            //this.dsItemTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //    List<MstDsItemInfo> selected = dsItemTable.getSelectionModel().getSelectedItems();
            //    if (loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            //        this.editButton.setDisable(selected.isEmpty());
            //        this.deleteButton.setDisable(selected.isEmpty());
            //    }
            //});

            this.dsItemTable.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    List<MstDsItemInfo> selected = this.dsItemTable.getSelectionModel().getSelectedItems();
                    if (event.getClickCount() == 2 && !selected.isEmpty()) {
                        onEdit(null);
                        return;
                    }
                    this.editButton.setDisable(selected.size() != 1);
                    if (loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
                        this.deleteButton.setDisable(selected.isEmpty());
                    }
                }
            });

            this.productNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<MstDsItemInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getProductNo()) ? o.getValue().getProductNo() : ""));
            this.productNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<MstDsItemInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getProductName()) ? o.getValue().getProductName() : ""));
            this.specColumn.setCellValueFactory((TableColumn.CellDataFeatures<MstDsItemInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getSpec()) ? o.getValue().getSpec() : ""));
            this.location1Column.setCellValueFactory((TableColumn.CellDataFeatures<MstDsItemInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getLocation1()) ? o.getValue().getLocation1() : ""));
            this.location2Column.setCellValueFactory((TableColumn.CellDataFeatures<MstDsItemInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getLocation2()) ? o.getValue().getLocation2() : ""));
            this.bomColumn.setCellValueFactory((TableColumn.CellDataFeatures<MstDsItemInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getBom()) ? "登録" : "未登録"));
            this.workflow1Column.setCellValueFactory((TableColumn.CellDataFeatures<MstDsItemInfo, String> o)
                    -> new ReadOnlyStringWrapper(getWorkflowName(o.getValue().getWorkflow1())));
            this.workflow2Column.setCellValueFactory((TableColumn.CellDataFeatures<MstDsItemInfo, String> o)
                    -> new ReadOnlyStringWrapper(getWorkflowName(o.getValue().getWorkflow2())));

            this.updateTable();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程順名を取得する。
     * 
     * @param workflowId 工程順ID
     * @return 
     */
    private String getWorkflowName(Long workflowId) {
        if (Objects.nonNull(workflowId)) {
            WorkflowInfoEntity workflow = CacheUtils.getCacheWorkflow(workflowId);
            return Objects.nonNull(workflow) ? workflow.getWorkflowName() : "";
        }
        return "";
    }
    
    @Override
    public boolean destoryComponent() {
        return true;
    }

    /**
     * 画面をロック状態にする。
     *
     * @param block
     */
    private void blockUI(Boolean block) {
        sc.blockUI("DsItemEditPane", block);
        this.progressPane.setVisible(block);
    }

    /**
     * 検索ボタン
     * 
     * @param event 
     */
    @FXML
    private void onSearch(ActionEvent event) {
        this.updateTable();
    }

    /**
     * 追加ボタン
     * 
     * @param event 
     */
    @FXML
    private void onAdd(ActionEvent event) {
        logger.info("onAdd start.");

        try {
            Tuple<String, MstDsItemInfo> tuple = new Tuple(this.lineComboBox.getSelectionModel().getSelectedItem(), null);
            
            Dialog dlg = sc.showModelessDialog("", "DsItemEditCompo", tuple, (Stage) sc.getStage(), false);
            dlg.getDialogPane().getScene().getWindow().setOnHidden(e -> {
                ButtonType buttonType = (ButtonType) dlg.getResult();
                if (ButtonType.OK.equals(buttonType)) {
                    this.updateTable();
                }
            });
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 編集ボタン
     * 
     * @param event 
     */
    @FXML
    private void onEdit(ActionEvent event) {
        logger.info("onAdd start.");
        List<MstDsItemInfo> selected = this.dsItemTable.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) {
            return;
        }  

        try {
            Tuple<String, MstDsItemInfo> tuple = new Tuple(this.lineComboBox.getSelectionModel().getSelectedItem(), selected.get(0));
            
            Dialog dlg = sc.showModelessDialog("", "DsItemEditCompo", tuple, (Stage) sc.getStage(), false);
            dlg.getDialogPane().getScene().getWindow().setOnHidden(e -> {
                ButtonType buttonType = (ButtonType) dlg.getResult();
                if (ButtonType.OK.equals(buttonType)) {
                    this.updateTable();
                }
            });
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 削除ボタン
     * 
     * @param event 
     */
    @FXML
    private void onDelete(ActionEvent event) {
        logger.info("onDelete start.");
        List<MstDsItemInfo> selected = this.dsItemTable.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) {
            return;
        }  

        try {
            String messgage = selected.size() > 1
                    ? LocaleUtils.getString("key.DeleteMultipleMessage")
                    : LocaleUtils.getString("key.DeleteSingleMessage");
            String content = selected.size() > 1
                    ? null
                    : selected.get(0).getProductNo();

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), messgage, content);
            if (!ret.equals(ButtonType.OK)) {
                return;
            }
            
            SampleResponse response = this.facade.delete(selected);
            if (ServerErrorTypeEnum.SUCCESS.equals(ServerErrorTypeEnum.valueOf(response.getStatus()))) {
                //if (!response.getDataList().isEmpty()) {
                //    // 「削除できなかった払出指示があります。」
                //    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("nonDeletabledItems"));
                //}
                this.updateTable();
            } else {
                 DialogBox.alert(ServerErrorTypeEnum.valueOf(response.getStatus()));
            }
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 品番一覧を更新する。
     */
    private void updateTable() {
        logger.info("updateTable");
        boolean isCancel = false;
        
        this.blockUI(true);

        this.editButton.setDisable(true);
        this.deleteButton.setDisable(true);

        this.rows.clear();
        this.abort = true;

        String productNo = this.productNoFiled.getText();
        String lineName = this.lineComboBox.getValue();
        int category = this.lineMap.get(lineName);

        Task task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                try {
                    return facade.count(category, productNo);
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    Integer count = this.getValue();

                    if (count > 0) {
                        updateTableSub(category, productNo, count, 0);
                    } else {
                        // 0件の場合は、検索条件を保存して検索処理を終了する。
                        abort = false;
                        blockUI(false);
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    blockUI(false);
                }
            }

            @Override
            protected void failed() {
                super.failed();
                try {
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
            }
        };
        new Thread(task).start();
    }

    /**
     * 品番一覧を更新する。
     * 
     * @param category 区分
     * @param productNo 品番
     * @param count 取得件数
     * @param from 取得範囲(開始)
     */
    private void updateTableSub(Integer category, String productNo, Integer count, long from) {
        logger.info("updateViewSub: category={}, productNo={}, count={}, from={}", category, productNo, count, from);
       
        try {
            blockUI(true);

            Task task = new Task<List<MstDsItemInfo>>() {
                @Override
                protected List<MstDsItemInfo> call() throws Exception {
                    return facade.findRange(category, productNo, from, from + Constants.SEARCH_MAX - 1);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    boolean isEnd = true;
                    try {
                        List<MstDsItemInfo> list = this.getValue();
                        
                        rows.addAll(list);

                        long _from = from + list.size();
                        if (_from < count) {
                            // 残りがある場合、継続確認ダイアログを表示する。
                            String message = String.format(LocaleUtils.getString("key.overRangeSearchContinue"), Constants.SEARCH_MAX);
                            MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.OutReportTitle"), message,
                                    MessageDialogEnum.MessageDialogType.Question, MessageDialogEnum.MessageDialogButtons.YesNo, 1.0, "#000000", "#ffffff");

                            if (dialogResult.equals(MessageDialogEnum.MessageDialogResult.Yes)) {
                                updateTableSub(category, productNo, count, _from);
                                isEnd = false;
                            }
                        } else {
                            // 全件取得完了
                            abort = false;
                        }

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        if (isEnd) {
                            blockUI(false);
                        }
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                        // エラー
                        MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.OutReportTitle"), LocaleUtils.getString("key.alert.systemError"),
                                MessageDialogEnum.MessageDialogType.Error, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };
            new Thread(task).start();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

}
