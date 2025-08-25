/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adtekfuji.admanagerapp.dsitemeditplugin.controller;

import adtekfuji.admanagerapp.dsitemeditplugin.common.Constants;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.dsKanban.DsParts;
import jp.adtekfuji.adFactory.entity.job.MstDsItemInfo;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.enumeration.Verifier;
import jp.adtekfuji.javafxcommon.tablecell.TableNumberCell;
import jp.adtekfuji.javafxcommon.tablecell.TableRestrictedTextCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 構成部品編集画面コントローラー
 * 
 * @author s-heya
 */
@FxComponent(id = "BomEditCompo", fxmlPath = "/adtekfuji/admanagerapp/dsitemeditplugin/bom_edit_compo.fxml")
public class BomEditController implements Initializable, ArgumentDelivery, DialogHandler {
    
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    private final String LAST_BOM_PATH = "LAST_BOM_PATH";
    
    private Dialog dialog;
    private MstDsItemInfo dsItem;
    private boolean isEditMode;
    private final ObservableList<DsParts> dsPartsList = FXCollections.observableArrayList();
    private List<DsParts> oldDsPartsList;

    @FXML
    private Label productNoLabel;
    @FXML
    private PropertySaveTableView<DsParts> dsPartsTable;
    @FXML
    private TableColumn<DsParts, String> productNoColumn;
    @FXML
    private TableColumn<DsParts, String> productNameColumn;
    @FXML
    private TableColumn<DsParts, String> quantityColumn;
    @FXML
    private TableColumn<DsParts, String> preProcessColumn;
    @FXML
    private Button importButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button registButton;
    @FXML
    private Pane progressPane;

    /**
     * 構成部品編集画面を初期化する。
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("initialize start.");

        this.deleteButton.setDisable(true);

        LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
        if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            this.importButton.setDisable(true);
            this.registButton.setDisable(true);
        }

        Callback<TableColumn<DsParts, String>, TableCell<DsParts, String>> numberCellFactory = (TableColumn<DsParts, String> p) 
                -> new TableNumberCell<>(1.0, 9999.0);
        Callback<TableColumn<DsParts, String>, TableCell<DsParts, String>> productNoCellFactory = (TableColumn<DsParts, String> p) 
                -> new TableRestrictedTextCell<>(Verifier.CHARACTER_ONLY, 256, "^[a-zA-Z0-9]{6}-[a-zA-Z0-9]{4}$");
        Callback<TableColumn<DsParts, String>, TableCell<DsParts, String>> textCellFactory = (TableColumn<DsParts, String> p) 
                -> new TableRestrictedTextCell<>(Verifier.DEFAULT, 256);

        SortedList<DsParts> sortedList = new SortedList<>(this.dsPartsList);

        Comparator<DsParts> comparator = Comparator.comparing(o -> Long.parseLong(o.getKey()));
        sortedList.setComparator(comparator);

        this.dsPartsTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        this.dsPartsTable.init("bomTable");
        this.dsPartsTable.setItems(sortedList);
        this.dsPartsTable.setEditable(true);
        this.dsPartsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.dsPartsTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                List<DsParts> selected = this.dsPartsTable.getSelectionModel().getSelectedItems();
                if (loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
                    this.deleteButton.setDisable(selected.isEmpty());
                }
            }
        });

        // 品番
        this.productNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<DsParts, String> o) 
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getProductNo()) ? o.getValue().getProductNo() : ""));
        this.productNoColumn.setCellFactory(productNoCellFactory);
        this.productNoColumn.setOnEditCommit((TableColumn.CellEditEvent<DsParts, String> event) -> {
            try {
                String oldValue = event.getOldValue();
                String newValue = event.getNewValue();

                DsParts item = event.getTableView().getItems().get(event.getTablePosition().getRow());

                if (org.apache.commons.lang3.StringUtils.isEmpty(newValue)) {
                    item.setProductNo("");
                } else if (!newValue.equals(oldValue)) {
                    item.setProductNo(newValue);
                }

                event.getTableView().refresh();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });
        
        // 品名
        this.productNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<DsParts, String> o) 
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getProductName()) ? o.getValue().getProductName() : ""));
        this.productNameColumn.setCellFactory(textCellFactory);
        this.productNameColumn.setOnEditCommit((TableColumn.CellEditEvent<DsParts, String> event) -> {
            try {
                String oldValue = event.getOldValue();
                String newValue = event.getNewValue();

                DsParts item = event.getTableView().getItems().get(event.getTablePosition().getRow());

                if (org.apache.commons.lang3.StringUtils.isEmpty(newValue)) {
                    item.setProductName("");
                } else if (!newValue.equals(oldValue)) {
                    item.setProductName(newValue);
                }

                event.getTableView().refresh();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });

        // 数量
        this.quantityColumn.setCellValueFactory((TableColumn.CellDataFeatures<DsParts, String> o) 
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getQuantity()) ? o.getValue().getQuantity().toString() : "0"));
        this.quantityColumn.setCellFactory(numberCellFactory);
        this.quantityColumn.setOnEditCommit((TableColumn.CellEditEvent<DsParts, String> event) -> {
            try {
                String oldValue = event.getOldValue();
                String newValue = event.getNewValue();

                DsParts item = event.getTableView().getItems().get(event.getTablePosition().getRow());

                if (org.apache.commons.lang3.StringUtils.isEmpty(newValue)) {
                    item.setQuantity(0);
                } else if (!newValue.equals(oldValue)) {
                    item.setQuantity(Integer.parseInt(newValue));
                }

                event.getTableView().refresh();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });

        // 前区
        this.preProcessColumn.setCellValueFactory((TableColumn.CellDataFeatures<DsParts, String> o) 
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getPreProcess()) ? o.getValue().getPreProcess() : ""));
        this.preProcessColumn.setCellFactory(textCellFactory);
        this.preProcessColumn.setOnEditCommit((TableColumn.CellEditEvent<DsParts, String> event) -> {
            try {
                String oldValue = event.getOldValue();
                String newValue = event.getNewValue();

                DsParts item = event.getTableView().getItems().get(event.getTablePosition().getRow());

                if (org.apache.commons.lang3.StringUtils.isEmpty(newValue)) {
                    item.setPreProcess("");
                } else if (!newValue.equals(oldValue)) {
                    item.setPreProcess(newValue);
                }

                event.getTableView().refresh();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });

        this.blockUI(false);
    }

    /**
     * パラメータを設定する。
     * 
     * @param argument パラメータ 
     */
    @Override
    public void setArgument(Object argument) {
        if (Objects.isNull(argument)) {
            return;
        }

        this.dsItem = (MstDsItemInfo) argument;
        
        this.productNoLabel.setText(this.dsItem.getProductNo());
        if (!StringUtils.isEmpty(this.dsItem.getBom())) {
            this.oldDsPartsList = this.dsItem.getDsParts();
            this.oldDsPartsList.forEach(o -> {
                this.dsPartsList.add(o.clone());
            });
        }
    }

    /**
     * 取込ボタン処理
     * 
     * @param event 
     */
    @FXML
    public void onImport(ActionEvent event) {
        try {
            File dir = new File(System.getProperty("user.home"), "Desktop");

            String path = AdProperty.getProperties(Constants.UI_PROPERTY_NAME).getProperty(LAST_BOM_PATH);
            if (!StringUtils.isEmpty(path)) {
                File file = new File(path);
                if (file.exists()) {
                    dir = file.isFile() ? file.getParentFile() : file;
                }
            }
            
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("部品表", "*.xlsx", "*.xls", "*.xlsm");

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(dir);
            fileChooser.setTitle(LocaleUtils.getString("key.FileChoice"));
            fileChooser.getExtensionFilters().addAll(filter);

            File file = fileChooser.showOpenDialog(this.dialog.getDialogPane().getScene().getWindow());
            if (Objects.nonNull(file)) {
                ImportBomService service = new ImportBomService(file);

                this.dsPartsList.clear();
                this.dsPartsList.addAll(service.importBom());
                        
                AdProperty.getProperties(Constants.UI_PROPERTY_NAME).setProperty(LAST_BOM_PATH, file.getPath());
                AdProperty.store(Constants.UI_PROPERTY_NAME);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 削除ボタン処理
     * 
     * @param event 
     */
    @FXML
    public void onDelete(ActionEvent event) {
        List<DsParts> items = this.dsPartsTable.getSelectionModel().getSelectedItems();
        if (Objects.isNull(items) || items.isEmpty()) {
            return;
        }

        String messgage = items.size() > 1
                ? LocaleUtils.getString("key.DeleteMultipleMessage")
                : LocaleUtils.getString("key.DeleteSingleMessage");
        String content = items.size() > 1
                ? null
                : items.get(0).getProductNo();

        ButtonType ret = sc.showMessageBox(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), messgage, content, 
                new ButtonType[]{ButtonType.OK, ButtonType.CANCEL}, ButtonType.CANCEL, this.dialog.getDialogPane().getScene().getWindow());
        if (!ret.equals(ButtonType.OK)) {
            return;
        }
        
        this.dsPartsList.removeAll(items);
    }

    /**
     * 登録ボタン処理
     * 
     * @param event 
     */
    @FXML
    public void onRegist(ActionEvent event) {
        try {
            // 未入力チェック
            if (this.dsPartsList.stream()
                    .filter(o -> StringUtils.isEmpty(o.getProductNo()) || StringUtils.isEmpty(o.getProductName()))
                    .findFirst()
                    .isPresent()) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"), 
                        (Stage) this.dialog.getDialogPane().getScene().getWindow());
                return;
            }
            
            this.dsItem.setBom(null);
            this.dsItem.setBom(null);
            
            // キーの再設定
            int count = 1;
            for (DsParts dsParts : this.dsPartsList) {
                dsParts.setKey(String.valueOf(count++));
            }
            
            this.dsItem.setBom(JsonUtils.objectsToJson(this.dsPartsList));
            
            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * キャンセルボタン処理
     * 
     * @param event 
     */
    @FXML
    public void onCancel(ActionEvent event) {
        if (this.isChanged()) {
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, null,
                    new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL, this.dialog.getDialogPane().getScene().getWindow());
            if (ButtonType.YES == buttonType) {
                onRegist(event);
                return;
            } else if (ButtonType.CANCEL == buttonType) {
                return ;
            }
        }
        this.cancelDialog();
    }

    /**
     * 構成部品編集画面を閉じる。
     *
     */
    private void cancelDialog() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * Dialog を設定する。
     * 
     * @param dialog 
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent event) -> {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, null,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL, this.dialog.getDialogPane().getScene().getWindow());
                if (ButtonType.YES == buttonType) {
                    onRegist(null);
                    return;
                } else if (ButtonType.CANCEL == buttonType) {
                    event.consume();
                    return ;
                }
            }
            this.cancelDialog();
        });
    }

    /**
     * データが変更されているかを返す。
     * 
     * @return 
     */
    private boolean isChanged() {
        if (Objects.isNull(this.oldDsPartsList)) {
            this.oldDsPartsList = new ArrayList<>();
        }

        List<DsParts> p1 = this.oldDsPartsList.stream()
                .sorted(DsParts.keyComparator)
                .collect(Collectors.toList());

        List<DsParts> p2 = this.dsPartsList.stream()
                .sorted(DsParts.keyComparator)
                .collect(Collectors.toList());
        
        return !p1.equals(p2);
    }

    /**
     * 画面をロック状態にする。
     *
     * @param block
     */
    private void blockUI(Boolean block) {
        this.progressPane.setVisible(block);
    }
}
