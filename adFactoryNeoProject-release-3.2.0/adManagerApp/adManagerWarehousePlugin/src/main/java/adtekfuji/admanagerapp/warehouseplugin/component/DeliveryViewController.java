/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.component;

import adtekfuji.admanagerapp.warehouseplugin.common.Constants;
import adtekfuji.admanagerapp.warehouseplugin.common.WarehouseConfig;
import adtekfuji.admanagerapp.warehouseplugin.entity.DeliveryInfo;
import adtekfuji.admanagerapp.warehouseplugin.entity.OutputSuppliedListInfo;
import adtekfuji.admanagerapp.warehouseplugin.services.ImportDelivery;
import adtekfuji.clientservice.WarehouseInfoFaced;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.SampleResponse;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.search.DeliveryCondition;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryInfo;
import jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WarehouseMode;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import jp.adtekfuji.javafxcommon.dialog.MessageDialog;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 払出モニター画面
 *
 * @author 
 */
@FxComponent(id = "DeliveryView", fxmlPath = "/fxml/warehouseplugin/DeliveryView.fxml")
public class DeliveryViewController implements Initializable, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final WarehouseInfoFaced faced = new WarehouseInfoFaced();
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(LocaleUtils.getString("key.DateFormat"));

    private int searchMax;
    private boolean abort = false;

    @FXML
    private PropertySaveTableView<DeliveryInfo> deliveryList;
    @FXML
    private TableColumn<DeliveryInfo, Boolean> printedChekcColumn;
    @FXML
    private TableColumn<DeliveryInfo, String> deliveryStatusColumn;
    @FXML
    private TableColumn<DeliveryInfo, String> deliveryNoColumn;
    @FXML
    private TableColumn<DeliveryInfo, Number> missingColumn;
    @FXML
    private TableColumn<DeliveryInfo, String> modelNameColumn;
    @FXML
    private TableColumn<DeliveryInfo, String> unitNoColumn;
    @FXML
    private TableColumn<DeliveryInfo, String> planDateColumn;
    @FXML
    private TableColumn<DeliveryInfo, String> deliveryDateColumn;
    @FXML
    private TableColumn<DeliveryInfo, String> startNumberColumn;
    @FXML
    private TableColumn<DeliveryInfo, String> compNumberColumn;
    @FXML
    private TableColumn<DeliveryInfo, String> destNameColumn;

    @FXML
    private CheckBox deliveryNoCheck;
    @FXML
    private CheckBox modelNameCheck;
    @FXML
    private CheckBox unitNoCheck;
    @FXML
    private CheckBox deliveryPlanDateCheck;
    @FXML
    private CheckBox deliveryStatusCheck;

    @FXML
    private TextField deliveryNoField;
    @FXML
    private TextField modelNameField;
    @FXML
    private TextField unitNoField;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private ChoiceBox deliveryStatusChoice;
    @FXML
    private Pane progressPane;
    @FXML
    private Button pickingListImportButton;
    @FXML
    private Button scheduleImportButton;
    @FXML
    private Button printButton;
    @FXML
    private Button outputSuppliedListButton;
    @FXML
    private Button changePlansButton;
    @FXML
    private Button detailButton;
    @FXML
    private Button deleteButton;

    private final CheckBox selectAllCheckBox = new CheckBox();// 全て選択
    private final ObservableList<DeliveryInfo> rows = FXCollections.observableArrayList();
    private final Map<String, DeliveryStatusEnum> deliveryStatusMap = new LinkedHashMap<>();

    /**
     * 画面初期化
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.deliveryList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        this.searchMax = StringUtils.parseInteger(AdProperty.getProperties().getProperty("deliverySearchMax", "10000"));
            
        try {
            // 初期値を設定する
            AdProperty.load(Constants.UI_PROPERTY_NAME, Constants.UI_PROPERTY_NAME + ".properties");
            Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);
            String prefix = DeliveryViewController.class.getName() + ".";

            this.deliveryNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "deliveryNoCheck", String.valueOf(Boolean.FALSE))));
            this.modelNameCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "modelNameCheck", String.valueOf(Boolean.FALSE))));
            this.unitNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "unitCodeCheck", String.valueOf(Boolean.FALSE))));
            this.deliveryPlanDateCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "deliveryPlanDateCheck", String.valueOf(Boolean.FALSE))));
            this.deliveryStatusCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "deliveryStatusCheck", String.valueOf(Boolean.FALSE))));
            
            this.deliveryNoField.setText(properties.getProperty(prefix + "deliveryNoField"));
            this.modelNameField.setText(properties.getProperty(prefix + "modelNameField"));
            this.unitNoField.setText(properties.getProperty(prefix + "unitCodeField"));

            String fromDate = properties.getProperty(prefix + "fromDatePicker");
            if (!StringUtils.isEmpty(fromDate)) {
                try {
                    this.fromDatePicker.setValue(LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE));
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
            
            String toDate = properties.getProperty(prefix + "toDatePicker");
            if (!StringUtils.isEmpty(toDate)) {
                try {
                    this.toDatePicker.setValue(LocalDate.parse(toDate, DateTimeFormatter.ISO_DATE));
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
            
            if (WarehouseMode.HAMAI.equals(WarehouseConfig.getWarehouseMode())) {
                this.deliveryStatusMap.put(LocaleUtils.getString(DeliveryStatusEnum.CONFIRM.getResourceKey()), DeliveryStatusEnum.CONFIRM);
            }
            this.deliveryStatusMap.put(LocaleUtils.getString(DeliveryStatusEnum.WAITING.getResourceKey()), DeliveryStatusEnum.WAITING);
            this.deliveryStatusMap.put(LocaleUtils.getString(DeliveryStatusEnum.WORKING.getResourceKey()), DeliveryStatusEnum.WORKING);
            this.deliveryStatusMap.put(LocaleUtils.getString(DeliveryStatusEnum.SUSPEND.getResourceKey()), DeliveryStatusEnum.SUSPEND);
            if (WarehouseMode.HAMAI.equals(WarehouseConfig.getWarehouseMode())) {
                this.deliveryStatusMap.put(LocaleUtils.getString(DeliveryStatusEnum.PICKED.getResourceKey()), DeliveryStatusEnum.PICKED);
            }
            this.deliveryStatusMap.put(LocaleUtils.getString(DeliveryStatusEnum.COMPLETED.getResourceKey()), DeliveryStatusEnum.COMPLETED);
            this.deliveryStatusChoice.getItems().addAll(this.deliveryStatusMap.keySet());
            this.deliveryStatusChoice.setValue(properties.getProperty(prefix + "deliveryStatusChoice", ""));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            this.pickingListImportButton.setDisable(true);  // ピッキングリスト取込
            this.scheduleImportButton.setDisable(true);     // 作業計画取込
            this.changePlansButton.setDisable(true);        // 計画変更
            this.deleteButton.setDisable(true);             // 削除
        }
        
        // コントロールを初期化する
        this.deliveryNoCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.deliveryNoField.setDisable(!newValue);
        });

        this.modelNameCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.modelNameField.setDisable(!newValue);
        });

        this.unitNoCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.unitNoField.setDisable(!newValue);
        });

        this.deliveryPlanDateCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.fromDatePicker.setDisable(!newValue);
            this.toDatePicker.setDisable(!newValue);
        });

        this.deliveryStatusCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.deliveryStatusChoice.setDisable(!newValue);
        });

        // 検索条件テキストフィールドの有効・無効
        this.deliveryNoField.setDisable(!this.deliveryNoCheck.isSelected()); // 払出識別名
        this.modelNameField.setDisable(!this.modelNameCheck.isSelected()); // ユニットコード
        this.unitNoField.setDisable(!this.unitNoCheck.isSelected()); // ユニットコード
        this.fromDatePicker.setDisable(!this.deliveryPlanDateCheck.isSelected()); // 払出予定日(開始日)
        this.toDatePicker.setDisable(!this.deliveryPlanDateCheck.isSelected()); // 払出予定日(終了日)
        this.deliveryStatusChoice.setDisable(!this.deliveryStatusCheck.isSelected()); // 払出状況

        this.printButton.setDisable(true);
        this.outputSuppliedListButton.setDisable(true);
        this.printedChekcColumn.setCellValueFactory(new PropertyValueFactory<>("printed"));
        this.printedChekcColumn.setCellFactory(column -> {
            // CheckBoxTableCellの共同を定義
            CheckBoxTableCell<DeliveryInfo, Boolean> cell = new CheckBoxTableCell<>(index -> {
                BooleanProperty selected = new SimpleBooleanProperty(this.deliveryList.getItems().get(index).isSelected());
                selected.addListener((observable, oldValue, newValue) -> {
                    this.deliveryList.getItems().get(index).setSelected(newValue);
                    // 払出指示情報が選択された場合のみ、ボタンを有効
                    List<DeliveryInfo> deliveryInfoList = this.deliveryList.getItems().stream().filter(p -> p.isSelected()).collect(Collectors.toList());
                    this.printButton.setDisable(deliveryInfoList.isEmpty());
                    this.outputSuppliedListButton.setDisable(deliveryInfoList.isEmpty());
                });
                
                return selected;
            });
            return cell;
        });
        
        // 払出指示番号
        this.deliveryNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<DeliveryInfo, String> param)
                -> param.getValue().getValue().getDeliveryNoProperty());
        // 機種名
        this.modelNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<DeliveryInfo, String> param)
                -> param.getValue().getValue().getModelNameProperty());
        // ユニット番号
        this.unitNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<DeliveryInfo, String> param)
                -> param.getValue().getValue().getUnitNoProperty());
        // 開始番号
        this.startNumberColumn.setCellValueFactory((TableColumn.CellDataFeatures<DeliveryInfo, String> param)
                -> param.getValue().getValue().getSerialStartProperty());
        // 終了番号
        this.compNumberColumn.setCellValueFactory((TableColumn.CellDataFeatures<DeliveryInfo, String> param)
                -> param.getValue().getValue().getSerialEndProperty());
        // 払出予定日
        this.planDateColumn.setCellValueFactory((TableColumn.CellDataFeatures<DeliveryInfo, String> param) -> Bindings.createStringBinding(()
                -> Objects.isNull(param.getValue().getValue().getDueDate()) ? "" : dateFormatter.format(param.getValue().getValue().getDueDate())));
        // 払出完了日
        this.deliveryDateColumn.setCellValueFactory((TableColumn.CellDataFeatures<DeliveryInfo, String> param) -> Bindings.createStringBinding(()
                -> Objects.isNull(param.getValue().getValue().getDeliveryDate()) ? "" : dateFormatter.format(param.getValue().getValue().getDeliveryDate())));
        // 払出先
        this.destNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<DeliveryInfo, String> param)
                -> param.getValue().getValue().getDestNameProperty());
        // 払出状況
        this.deliveryStatusColumn.setCellValueFactory((TableColumn.CellDataFeatures<DeliveryInfo, String> param) -> Bindings.createStringBinding(()
                -> this.getDeliveryStatusString(param.getValue().getValue().getStatus())));
        // 欠品数
        this.missingColumn.setCellValueFactory((TableColumn.CellDataFeatures<DeliveryInfo, Number> param)
                -> param.getValue().getValue().stockOutNumProperty());
        
        // 全て選択
        printedChekcColumn.setGraphic(selectAllCheckBox);
        selectAllCheckBox.setOnAction(e -> onSelectAllCheckBox(e));

        this.deliveryList.setItems(rows);
        this.deliveryList.init("DeliveryView");
        this.deliveryList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        this.deliveryList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            List<DeliveryInfo> selected = deliveryList.getSelectionModel().getSelectedItems();
            this.printButton.setDisable(Objects.isNull(selected) || selected.isEmpty());
            this.detailButton.setDisable(Objects.isNull(selected) || selected.size() != 1);
            if (loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
                this.changePlansButton.setDisable(Objects.isNull(selected) || selected.isEmpty());
                this.deleteButton.setDisable(Objects.isNull(selected) || selected.isEmpty());
            }
        });
        
        this.deliveryList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                List<DeliveryInfo> selected = deliveryList.getSelectionModel().getSelectedItems();
                if (event.getClickCount() == 2 && !selected.isEmpty()) {
                    onDetail(null);
                    return;
                }
                this.printButton.setDisable(selected.isEmpty());
                this.detailButton.setDisable(selected.size() != 1);
                if (loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
                    this.changePlansButton.setDisable(selected.isEmpty());
                    this.deleteButton.setDisable(selected.isEmpty());
                }
            }
        });
    }

    /**
     * 払出状況の表示文字列を取得する。
     *
     * @param deliveryStatus　払出状況
     * @return 払出状況の表示文字列
     */
    private String getDeliveryStatusString(DeliveryStatusEnum deliveryStatus) {
        String status;
        switch (deliveryStatus) {
            case CONFIRM:
                status = LocaleUtils.getString("waitingConfirm");
                break;
            case WAITING:
                status = LocaleUtils.getString("waitingDelivery");
                break;
            case WORKING:
                status = LocaleUtils.getString("key.PayoutStatusWorking");
                break;
            case SUSPEND:
                status = LocaleUtils.getString("key.PayoutStatusSuspend");
                break;
            case PICKED:
                status = LocaleUtils.getString("picked");
                break;
            case COMPLETED:
                status = LocaleUtils.getString("key.PayoutEnd");
                break;
            default:
                status = "";
        }
        return status;
    }

    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            sc.blockUI("ContentNaviPane", flg);
            progressPane.setVisible(flg);
        });
    }

    /**
     * 検索条件を作成する。
     * 
     * @return 
     */
    private DeliveryCondition createDeliveryCondition() {
        String deliveryNo = (this.deliveryNoCheck.isSelected() && !StringUtils.isEmpty(this.deliveryNoField.getText()))
                ? deliveryNoField.getText() : null;

        String modelName = (this.modelNameCheck.isSelected() && !StringUtils.isEmpty(this.modelNameField.getText()))
                ? this.modelNameField.getText() : null;

        String unitNo = (this.unitNoCheck.isSelected() && !StringUtils.isEmpty(this.unitNoField.getText()))
                ? this.unitNoField.getText() : null;

        LocalDate startDay = !this.deliveryPlanDateCheck.isSelected() || Objects.isNull(fromDatePicker.getValue())
                ? null : fromDatePicker.getValue();

        LocalDate endDay = !this.deliveryPlanDateCheck.isSelected() || Objects.isNull(toDatePicker.getValue())
                ? null : toDatePicker.getValue();

        List<DeliveryStatusEnum> statusList = null;
        if (this.deliveryStatusCheck.isSelected() && Objects.nonNull(this.deliveryStatusChoice.getValue()) && this.deliveryStatusMap.containsKey(this.deliveryStatusChoice.getValue().toString())) {
            statusList = Arrays.asList(this.deliveryStatusMap.get(this.deliveryStatusChoice.getValue().toString()));
        }

        DeliveryCondition condition = new DeliveryCondition();
        condition.setDeliveryNo(deliveryNo);
        condition.setModelName(modelName);
        condition.setUnitNo(unitNo);
        condition.setFromDate(startDay);
        condition.setToDate(endDay);
        condition.setStatuses(statusList);

        return condition;
    }

    /**
     * 
     */
    private void updateView() {
        logger.info("updateView");
        boolean isCancel = false;

        try {
            this.blockUI(true);

            this.abort = true;
            this.rows.clear();

            final DeliveryCondition condition = this.createDeliveryCondition();

            Task task = new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    return faced.countDelivery(condition);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        Integer count = this.getValue();

                        if (count > 0) {
                            updateViewSub(condition, count, 0);
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

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            isCancel = true;
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }
    }

    /**
     * 
     * 
     * @param condition
     * @param count
     * @param from 
     */
    private void updateViewSub(DeliveryCondition condition, Integer count, long from) {
        logger.info("updateViewSub: condition={}, count={}, from={}", condition, count, from);
       
        try {
            blockUI(true);

            Task task = new Task<List<TrnDeliveryInfo>>() {
                @Override
                protected List<TrnDeliveryInfo> call() throws Exception {
                    return faced.searchDeliveryRange(condition, from, from + searchMax - 1);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    boolean isEnd = true;
                    try {
                        List<TrnDeliveryInfo> list = this.getValue();
                        
                        rows.addAll(list.stream().map(o -> new DeliveryInfo(o)).collect(Collectors.toList()));

                        long _from = from + list.size();
                        if (_from < count) {
                            // 残りがある場合、継続確認ダイアログを表示する。
                            String message = String.format(LocaleUtils.getString("key.overRangeSearchContinue"), searchMax);
                            MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.OutReportTitle"), message,
                                    MessageDialogEnum.MessageDialogType.Question, MessageDialogEnum.MessageDialogButtons.YesNo, 1.0, "#000000", "#ffffff");

                            if (dialogResult.equals(MessageDialogEnum.MessageDialogResult.Yes)) {
                                // 実績出力情報を取得して、リスト表示を更新する。
                                updateViewSub(condition, count, _from);
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

    /**
     * エラーダイアログを表示する
     * 
     * @param msg エラー内容
     */
    private void showErrorDialog(String msg) {
        Platform.runLater(() -> {
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Error"), msg);
        });
    }

    /**
     * チェックが設定・解除された。
     *
     * @param event
     */
    public void onSelectAllCheckBox(ActionEvent event) {
        boolean isSelected = ((CheckBox) event.getSource()).isSelected();
        this.selectAllCheckBox(isSelected);
        this.printButton.setDisable(!isSelected);
        this.outputSuppliedListButton.setDisable(!isSelected);
    }

    /**
     * チェックを設定・解除する。
     *
     * @param isSelected
     */
    private void selectAllCheckBox(boolean isSelected) {
        this.rows.forEach(row -> {
            row.setSelected(isSelected);
        });
    }

    /**
     * 支給品リスト出力ダイアログを表示する。
     */
    private void dispOutputSuppliedListDialog() {
        try {
            List<DeliveryInfo> deliveryCollection = deliveryList.getItems();
            if (Objects.isNull(deliveryCollection) || deliveryCollection.isEmpty()) {
                return;
            }

            List<DeliveryInfo> deliveryInfoList = deliveryCollection.stream().filter(o -> o.isSelected()).collect(Collectors.toList());
            if (deliveryInfoList.isEmpty()) {
                return;
            }

            // 支給品リスト出力画面
            OutputSuppliedListInfo outputInfo = new OutputSuppliedListInfo();
            outputInfo.setDeliveryInfoList(deliveryInfoList);
            sc.showDialog(LocaleUtils.getString("key.OutputSuppliedPartsList"), "OutputSuppliedListDialog", outputInfo);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * コンポーネントを破棄する。
     * 
     * @return true:遷移可、false:遷移不可
     */
    @Override
    public boolean destoryComponent() {
        try {
            String prefix = DeliveryViewController.class.getName() + ".";

            Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);
            properties.setProperty(prefix + "deliveryNoCheck", String.valueOf(this.deliveryNoCheck.isSelected()));
            properties.setProperty(prefix + "modelNameCheck", String.valueOf(this.modelNameCheck.isSelected()));
            properties.setProperty(prefix + "unitCodeCheck", String.valueOf(this.unitNoCheck.isSelected()));
            properties.setProperty(prefix + "deliveryPlanDateCheck", String.valueOf(this.deliveryPlanDateCheck.isSelected()));
            properties.setProperty(prefix + "deliveryStatusCheck", String.valueOf(this.deliveryStatusCheck.isSelected()));

            if (Objects.nonNull(deliveryNoField.getText())) {
                properties.setProperty(prefix + "deliveryNoField", deliveryNoField.getText());
            }
            
            if (Objects.nonNull(modelNameField.getText())) {
                properties.setProperty(prefix + "modelNameField", modelNameField.getText());
            }

            if (Objects.nonNull(unitNoField.getText())) {
                properties.setProperty(prefix + "unitCodeField", unitNoField.getText());
            }

            if (Objects.nonNull(fromDatePicker.getValue())) {
                properties.setProperty(prefix + "fromDatePicker", fromDatePicker.getValue().format(DateTimeFormatter.ISO_DATE));
            } else {
                properties.setProperty(prefix + "fromDatePicker", "");
            }

            if (Objects.nonNull(toDatePicker.getValue())) {
                properties.setProperty(prefix + "toDatePicker", toDatePicker.getValue().format(DateTimeFormatter.ISO_DATE));
            } else {
                properties.setProperty(prefix + "toDatePicker", "");
            }
            
            if (Objects.nonNull(this.deliveryStatusChoice.getValue())) {
                properties.setProperty(prefix + "deliveryStatusChoice", this.deliveryStatusChoice.getValue().toString());
            }

            AdProperty.store(Constants.UI_PROPERTY_NAME);

        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return true;
    }

    @FXML
    private void onSearch(ActionEvent event) {
        updateView();
        this.selectAllCheckBox.setSelected(false);
    }

    @FXML
    private void onExportSuppliedList(ActionEvent event) {
        this.dispOutputSuppliedListDialog();
    }

    /**
     * 払出票の印刷
     * 
     * @param event 
     */
    @FXML
    private void onPrint(ActionEvent event) {
        List<DeliveryInfo> deliveries = this.deliveryList.getSelectionModel().getSelectedItems();
        ButtonType buttonType = sc.showDialog(LocaleUtils.getString("key.PayoutPrinting"), "PrintWithdrawalOrderDialog", 
                deliveries, (Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    /**
     * ピッキングリスト取込
     * 
     * @param event 
     */
    @FXML
    private void onImportPickingList(ActionEvent event) {
        try {
            this.blockUI(true);

            Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);
            String path = properties.getProperty(Constants.PICKING_LIST_DIR);

            FileChooser fileChooser = new FileChooser();
            
            if (!StringUtils.isEmpty(path)) {
                File dir = new File(path);
                if (dir.exists()) {
                    fileChooser.setInitialDirectory(dir);
                }
            }

            // ファイル選択
            fileChooser.setTitle(LocaleUtils.getString("key.FileChoice"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    LocaleUtils.getString("key.import.excelFile"), "*.xlsx", "*.xls", "*.xlsm"));

            File selectedFile = fileChooser.showOpenDialog(sc.getStage().getScene().getWindow());
            if (Objects.isNull(selectedFile)) {
                return;
            }
            
            properties.setProperty(Constants.PICKING_LIST_DIR, selectedFile.getParentFile().getPath());
            AdProperty.store(Constants.UI_PROPERTY_NAME);

            ImportDelivery importDelivery = new ImportDelivery();
            List<TrnDeliveryInfo> deliveries = importDelivery.importData(selectedFile);
            if (Objects.isNull(deliveries)) {
                return;
            }
        
            Object[] argument = new Object[2];
            argument[0] = deliveries;
        
            sc.showDialog(LocaleUtils.getString("key.PayoutManagement"), "DeliveryDialog", argument, sc.getStage(), true);

            this.updateView();
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);

        } finally {
            this.blockUI(false);
        }
        
    }

    /**
     * 作業計画取込
     * 
     * @param event 
     */
    @FXML
    private void onImportSchedule(ActionEvent event) {
    }

    /**
     * 詳細
     * 
     * @param event 
     */
    @FXML
    private void onDetail(ActionEvent event) {
        List<DeliveryInfo> items = this.deliveryList.getSelectionModel().getSelectedItems();
        if (Objects.isNull(items) || items.isEmpty()) {
            return;
        }

        DeliveryInfo item = items.get(0);
        DeliveryCondition condition = new DeliveryCondition();
        condition.setModelName(item.getValue().getModelName());
        condition.setOrderNo(item.getValue().getOrderNo());
        List<TrnDeliveryInfo> deliveries = faced.searchDeliveryRange(condition, null, null);

        Object[] argument = new Object[2];
        argument[0] = deliveries;
        argument[1] = item;
        
        sc.showDialog(LocaleUtils.getString("key.PayoutManagement"), "DeliveryDialog", argument, sc.getStage(), true);

        this.updateView();
    }

    /**
     * 削除
     * 
     * @param event 
     */
    @FXML
    private void onDelete(ActionEvent event) {
        try {

            List<DeliveryInfo> items = this.deliveryList.getSelectionModel().getSelectedItems();
            if (Objects.isNull(items) || items.isEmpty()) {
                return;
            }
            
            String messgage = items.size() > 1
                    ? LocaleUtils.getString("key.DeleteMultipleMessage")
                    : LocaleUtils.getString("key.DeleteSingleMessage");
            String content = items.size() > 1
                    ? null
                    : items.get(0).getValue().getDeliveryNo();

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), messgage, content);
            if (!ret.equals(ButtonType.OK)) {
                return;
            }
            
            SampleResponse response = this.faced.delete(items.stream().map(o -> o.getValue()).collect(Collectors.toList()));
            if (ServerErrorTypeEnum.SUCCESS.equals(ServerErrorTypeEnum.valueOf(response.getStatus()))) {
                if (!response.getDataList().isEmpty()) {
                    // 「削除できなかった払出指示があります。」
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("nonDeletabledItems"));
                }
                this.updateView();
            } else {
                 DialogBox.alert(ServerErrorTypeEnum.valueOf(response.getStatus()));
            }
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 計画変更ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onChangePlans(ActionEvent event) {
        this.blockUI(true);

        try {
            List<DeliveryInfo> items = this.deliveryList.getSelectionModel().getSelectedItems();
            if (Objects.isNull(items) || items.isEmpty()) {
                return;
            }
            
            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.ChangePlans"), "WarehousePlanChangeDialog", items);
            if (!ButtonType.OK.equals(ret)) {
                return;
            }
            
            this.updateView();
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);

        } finally {
            this.blockUI(false);
        }
    }
}
