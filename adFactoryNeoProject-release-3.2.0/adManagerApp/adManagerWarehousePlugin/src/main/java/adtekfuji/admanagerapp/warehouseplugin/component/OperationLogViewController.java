/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.component;

import adtekfuji.admanagerapp.warehouseplugin.common.Constants;
import adtekfuji.admanagerapp.warehouseplugin.common.WarehouseConfig;
import adtekfuji.admanagerapp.warehouseplugin.controls.CheckList;
import adtekfuji.admanagerapp.warehouseplugin.csv.jcm.OperationCsvFactory;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WarehouseInfoFaced;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.MaterialCondition;
import jp.adtekfuji.adFactory.entity.search.OperationLogCondition;
import jp.adtekfuji.adFactory.entity.warehouse.LogStockInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;
import jp.adtekfuji.adFactory.enumerate.WarehouseEvent;
import jp.adtekfuji.adFactory.enumerate.WarehouseMode;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.dialog.MessageDialog;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.IndexedCheckModel;

/**
 * 作業ログ出力画面
 * 
 * @author s-heya
 */
@FxComponent(id = "OperationLogView", fxmlPath = "/fxml/warehouseplugin/OperationLogView.fxml")
public class OperationLogViewController implements Initializable, ComponentHandler {

    private static final String DISABLE_DATE_STYLE = "-fx-background-color: lightgray;";// カレンダーで選択不可な日のスタイル
    private static final String DISP_DATE_FORMAT = "yyyy/MM/dd";

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private final WarehouseInfoFaced facade = new WarehouseInfoFaced();
    
    private List<String> areaNameList;
    private int searchMax;
    private boolean abort = false;
        
    @FXML
    private SplitPane operationLogPane;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private CheckBox areaNameCheck;
    @FXML
    private ComboBox areaNameField;
    @FXML
    private SelectPane organizationSelectPaneController; // 変数名に"Controller"を付ける必要がある
    @FXML
    private CheckBox productNoCheck;
    @FXML
    private TextField productNoField;
    @FXML
    private CheckBox orderNoCheck;
    @FXML
    private TextField orderNoField;
    @FXML
    private CheckBox deliveryNoCheck;
    @FXML
    private TextField deliveryNoField;
    @FXML
    private CheckList categoryList;
    @FXML
    private VBox customOutPane;
    @FXML
    private CheckBox customOutCheck;
    @FXML
    private ComboBox<WarehouseEvent> customOutField;
    @FXML
    private PropertySaveTableView<LogStockInfo> operationLogTable;
    @FXML
    private Pane progress;

    @FXML
    private TableColumn<LogStockInfo, String> dateColumn;
    @FXML
    private TableColumn<LogStockInfo, String> categoryColumn;
    @FXML
    private TableColumn<LogStockInfo, String> productNoColumn;
    @FXML
    private TableColumn<LogStockInfo, String> productNameColumn;
    @FXML
    private TableColumn<LogStockInfo, String> supplyNoColumn;
    @FXML
    private TableColumn<LogStockInfo, String> deliveryNoColumn;
    @FXML
    private TableColumn<LogStockInfo, String> partsNoColumn;
    @FXML
    private TableColumn<LogStockInfo, String> orderNoColumn;
    @FXML
    private TableColumn<LogStockInfo, String> quantityColumn;
    @FXML
    private TableColumn<LogStockInfo, String> inStockNumColumn;
    @FXML
    private TableColumn<LogStockInfo, String> unitColumn;
    @FXML
    private TableColumn<LogStockInfo, String> areaNameColumn;
    @FXML
    private TableColumn<LogStockInfo, String> locationNoColumn;
    @FXML
    private TableColumn<LogStockInfo, String> personNoColumn;
    @FXML
    private TableColumn<LogStockInfo, String> infoColumn;
    
    /**
     * 作業ログ出力画面を初期化する。
     * 
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.searchMax = StringUtils.parseInteger(AdProperty.getProperties().getProperty("operationLogSearchMax", "10000"));

        SplitPaneUtils.loadDividerPosition(this.operationLogPane, getClass().getSimpleName());

        this.initSearchCondition();
        this.initOperationLogTable();
        this.createCache();
    }

    /**
     * コンポーネントを破棄する。
     * 
     * @return true:遷移可、false:遷移不可
     */
    @Override
    public boolean destoryComponent() {
        try {
            Properties props = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);
            String prefix = OperationLogViewController.class.getName() + ".";

            // 対象日
            LocalDate fromDate = this.fromDatePicker.getValue();
            if (Objects.nonNull(fromDate)) {
                props.setProperty(prefix + "fromDatePicker", DateTimeFormatter.ofPattern(DISP_DATE_FORMAT).format(fromDate));
            } else {
                props.setProperty(prefix + "fromDatePicker", "");
            }
            
            LocalDate toDate = this.toDatePicker.getValue();
            if (Objects.nonNull(toDate)) {
                props.setProperty(prefix + "toDatePicker", DateTimeFormatter.ofPattern(DISP_DATE_FORMAT).format(toDate));
            } else {
                props.setProperty(prefix + "toDatePicker", "");
            }

            // 区画
            props.setProperty(prefix + "areaNameCheck", String.valueOf(this.areaNameCheck.isSelected()));
            if (Objects.nonNull(areaNameField.getValue())) {
                props.setProperty(prefix + "areaNameField", String.valueOf(areaNameField.getValue()));
            } else {
                props.setProperty(prefix + "areaNameField", "");
            }
            
            // 組織
            props.setProperty(prefix + "organizationSelected", String.valueOf(this.organizationSelectPaneController.isSelected()));
            props.setProperty(prefix + "organizationIds", organizationSelectPaneController.getChoiceIdsString());
            props.setProperty(prefix + "organizationNames", organizationSelectPaneController.getChoiceText());

            // 品目
            props.setProperty(prefix + "productNoCheck", String.valueOf(this.productNoCheck.isSelected()));
            if (Objects.nonNull(productNoField.getText())) {
                props.setProperty(prefix + "productNoField", productNoField.getText());
            } else {
                props.setProperty(prefix + "productNoField", "");
            }

            // 製造番号
            props.setProperty(prefix + "orderNoCheck", String.valueOf(this.orderNoCheck.isSelected()));
            if (Objects.nonNull(orderNoField.getText())) {
                props.setProperty(prefix + "orderNoField", orderNoField.getText());
            } else {
                props.setProperty(prefix + "orderNoField", "");
            }

            // 払出指示番号
            props.setProperty(prefix + "deliveryNoCheck", String.valueOf(this.deliveryNoCheck.isSelected()));
            if (Objects.nonNull(deliveryNoField.getText())) {
                props.setProperty(prefix + "deliveryNoField", deliveryNoField.getText());
            } else {
                props.setProperty(prefix + "deliveryNoField", "");
            }
            
            // 種別
            props.setProperty(prefix + "categoryListCheck", String.valueOf(this.categoryList.isSelected()));
            List<WarehouseEvent> items = this.categoryList.getCheckedItems();
            if (!items.isEmpty()) {
                List<String> list = items.stream().map(o -> o.name()).collect(Collectors.toList());
                props.setProperty(prefix + "categoryList", String.join(",", list));
            } else {
                props.setProperty(prefix + "categoryList", "");
            }

            props.setProperty(prefix + "customOutCheck", String.valueOf(this.customOutCheck.isSelected()));
            if (Objects.nonNull(this.customOutField.getValue())) {
                props.setProperty(prefix + "customOutField", customOutField.getValue().name());
            } else {
                props.setProperty(prefix + "customOutField", WarehouseEvent.RECIVE.name());
            }
            
            AdProperty.store();

            SplitPaneUtils.saveDividerPosition(this.operationLogPane, this.getClass().getSimpleName());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return true;
    }
 
    /**
     * 更新
     * 
     * @param event 
     */
    @FXML
    private void onUpdate(ActionEvent event) {
        logger.info("onUpdate: {}", event);
        this.updateView();
    }

    /**
     * CSV出力
     * 
     * @param event 
     */
    @FXML
    private void onExport(ActionEvent event) {
        logger.info("onExport: {}", event);
        
        // 日本金銭様向けカスタムが無効の場合は標準出力
        if (this.customOutCheck.isSelected()) {
            outputCustomCsv();
        } else {
            // 検索結果がない場合は出力不可。
            if (this.operationLogTable.getItems().isEmpty()) {
                MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.PrintOutCSV"), LocaleUtils.getString("key.NoOperationLogData"),
                        MessageDialogEnum.MessageDialogType.Warning, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");
                return;
            }
            
            outputCsv(event);
        }
    }

    /**
     * 区画名にフォーカスが当たったときに、区画名一覧を取得する
     */
    private final ChangeListener<Boolean> focusedListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if (newValue) {
            this.areaNameList = this.facade.findAllAreaName();
            for (String areaName : this.areaNameList) {
                if (!this.areaNameField.getItems().contains(areaName)) {
                    this.areaNameField.getItems().add(areaName);
                }
            }
            this.areaNameField.focusedProperty().removeListener(OperationLogViewController.this.focusedListener);
        }
    };

    /**
     * 検索条件を初期化する。
     */
    private void initSearchCondition() {
        try {
            AdProperty.load(Constants.UI_PROPERTY_NAME, Constants.UI_PROPERTY_NAME + ".properties");

            Properties props = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);
            String prefix = OperationLogViewController.class.getName() + ".";
            
            // 対象日
            String propFromDate = props.getProperty(prefix + "fromDatePicker");
            if (!StringUtils.isEmpty(propFromDate)) {
                LocalDate date = LocalDate.parse(propFromDate, DateTimeFormatter.ofPattern(DISP_DATE_FORMAT));
                this.fromDatePicker.setValue(date);
            }

            String propToDate = props.getProperty(prefix + "toDatePicker");
            if (!StringUtils.isEmpty(propToDate)) {
                LocalDate date = LocalDate.parse(propToDate, DateTimeFormatter.ofPattern(DISP_DATE_FORMAT));
                this.toDatePicker.setValue(date);
            }

            this.initDatePicker();

            // 組織
            this.organizationSelectPaneController.setSelected(Boolean.valueOf(props.getProperty(prefix + "organizationSelected", String.valueOf(Boolean.FALSE))));
            List<String> organizationIds = Arrays.asList(props.getProperty(prefix + "organizationIds", "").split(","));
            List<String> organizationNames = Arrays.asList(props.getProperty(prefix + "organizationNames", "").split(","));

            Map<Long, String> organizations = new LinkedHashMap();
            for (int i = 0; i < organizationIds.size(); i++) {
                String id = organizationIds.get(i);
                if (id.isEmpty() || i >= organizationNames.size()) {
                    break;
                }
                String name = organizationNames.get(i);
                organizations.put(Long.valueOf(id), name);
            }
            this.organizationSelectPaneController.setChoiceDatas(organizations);
            this.organizationSelectPaneController.setLabelText(LocaleUtils.getString("key.Organization"));
            this.organizationSelectPaneController.setOnClickButtonListener(event -> {
                showOrganizationSelectionDialog(event);
            });    

            // 区画
            String areaName = props.getProperty(prefix + "areaNameField");
            if (!StringUtils.isEmpty(areaName)) {
                this.areaNameField.getItems().add(areaName);
                this.areaNameField.setValue(areaName);
            }
            
            this.areaNameCheck.setSelected(Boolean.parseBoolean(props.getProperty(prefix + "areaNameCheck", String.valueOf(Boolean.FALSE))));
            this.areaNameField.disableProperty().bind(areaNameCheck.selectedProperty().not());
            this.areaNameField.focusedProperty().addListener(this.focusedListener);
                        
            // 品目
            this.productNoCheck.setSelected(Boolean.parseBoolean(props.getProperty(prefix + "productNoCheck", String.valueOf(Boolean.FALSE))));
            this.productNoField.setText(props.getProperty(prefix + "productNoField"));
            this.productNoField.disableProperty().bind(productNoCheck.selectedProperty().not());

            // 製造番号
            this.orderNoCheck.setSelected(Boolean.parseBoolean(props.getProperty(prefix + "orderNoCheck", String.valueOf(Boolean.FALSE))));
            this.orderNoField.setText(props.getProperty(prefix + "orderNoField"));
            this.orderNoField.disableProperty().bind(orderNoCheck.selectedProperty().not());

            // 払出指示番号
            this.deliveryNoCheck.setSelected(Boolean.parseBoolean(props.getProperty(prefix + "deliveryNoCheck", String.valueOf(Boolean.FALSE))));
            this.deliveryNoField.setText(props.getProperty(prefix + "deliveryNoField"));
            this.deliveryNoField.disableProperty().bind(deliveryNoCheck.selectedProperty().not());

            // 種別
            this.categoryList.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    this.customOutCheck.setSelected(false);
                }
            });
            this.categoryList.setSelected(Boolean.parseBoolean(props.getProperty(prefix + "categoryListCheck", String.valueOf(Boolean.FALSE))));
            String[] events =  props.getProperty(prefix + "categoryList", "").split(",");
            IndexedCheckModel<String> cm = this.categoryList.getCheckModel();
            for (String event : events) {
                if (event.trim().isEmpty()) {
                    continue;
                }
                cm.check(WarehouseEvent.valueOf(event).getMessage(rb));
            }
            
            this.customOutPane.setVisible(WarehouseMode.JCM.equals(WarehouseConfig.getWarehouseMode()));
            this.customOutCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    this.categoryList.setSelected(false);
                }
            });
            this.customOutCheck.setSelected(Boolean.parseBoolean(props.getProperty(prefix + "customOutCheck", String.valueOf(Boolean.FALSE))));
            this.customOutField.disableProperty().bind(customOutCheck.selectedProperty().not());
            this.customOutField.getItems().addAll(Arrays.asList(WarehouseEvent.RECIVE, WarehouseEvent.INSPECTION, WarehouseEvent.RECEIPT_PRODUCTION, WarehouseEvent.SHIPPING));
            this.customOutField.setCellFactory(param -> new ListCell<WarehouseEvent>() {
                @Override
                protected void updateItem(WarehouseEvent item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setText(item.getMessage(rb));
                    } else {
                        setText(null);
                    }
                }
            });
            this.customOutField.setButtonCell(new ListCell<WarehouseEvent>(){
                @Override
                protected void updateItem(WarehouseEvent item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setText(item.getMessage(rb));
                    } else {
                        setText(null);
                    }
                }
            });
            String customOut = props.getProperty(prefix + "customOutField", WarehouseEvent.RECIVE.name());
            this.customOutField.setValue(WarehouseEvent.valueOf(customOut));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 対象日を初期化する。
     */
    private void initDatePicker() {
        // 開始日には、終了日より後の日は選択できない。
        this.fromDatePicker.setDayCellFactory((DatePicker datePicker) -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (Objects.isNull(date) || Objects.isNull(toDatePicker.getValue())) {
                    return;
                }
                
                if (date.isAfter(toDatePicker.getValue())) {
                    setDisable(true);
                    setStyle(DISABLE_DATE_STYLE);
                }
            }
        });

        // 終了日には、開始日より前の日は選択できない。
        this.toDatePicker.setDayCellFactory((DatePicker datePicker) -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (Objects.isNull(date) || Objects.isNull(fromDatePicker.getValue())) {
                    return;
                }
                
                if (date.isBefore(fromDatePicker.getValue())) {
                    setDisable(true);
                    setStyle(DISABLE_DATE_STYLE);
                }
            }
        });
    }

    /**
     * 作業ログ一覧を初期化する。
     * 
     */
    private void initOperationLogTable() {
        Callback<TableColumn<LogStockInfo, String>, TableCell<LogStockInfo, String>> rightCellFactory =
            (final TableColumn<LogStockInfo, String> param) -> {
                TableCell<LogStockInfo, String>cell = new TableCell<LogStockInfo, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            return;
                        }
                        this.setText(item);
                    }
                };
                cell.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
                cell.setAlignment(Pos.CENTER_RIGHT);
                cell.setPadding(new Insets(0, 6, 0, 0));
                return cell;
        };

        // 日時
        this.dateColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(dateTimeFormat.format(o.getValue().getEventDate())));
        // 種別
        this.categoryColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(WarehouseEvent.valueOf(o.getValue().getEventKind()).getMessage(rb)));
        // 品目
        this.productNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getProductNo()));
        // 品名
        this.productNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getProductName()));
        // 発注番号
        this.supplyNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getSupplyNo()));
        // 払出指示番号
        this.deliveryNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getDeliveryNo()));
        // ロット番号
        this.partsNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getPartsNo()));
        // 製造番号
        this.orderNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getOrderNo()));
        // 数量
        this.quantityColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getEventNum()) ? String.valueOf(o.getValue().getEventNum()) : null));
        this.quantityColumn.setCellFactory(rightCellFactory);
        // 在庫数
        this.inStockNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getInStockNum()) ? String.valueOf(o.getValue().getInStockNum()) : null));
        this.inStockNumColumn.setCellFactory(rightCellFactory);
        // 単位
        //this.unitColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
        //        -> new ReadOnlyStringWrapper(o.getValue().getValue().getProduct().getUnit()));
        // 区画名
        this.areaNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getAreaName()));
        // 棚番号
        this.locationNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getLocationNo()));
        // 担当者
        this.personNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getPersonNo()));
        // 追加情報
        this.infoColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getNote()));

        this.operationLogTable.init(OperationLogViewController.class.getSimpleName() + ".operationLogList");
        this.operationLogTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
    }
    
    /**
     * キャッシュを初期化する。
     */
    private void createCache() {
        this.blockUI(true);

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                CacheUtils.createCacheOrganization(true);
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
                if (Objects.nonNull(this.getException())) {
                    logger.fatal(this.getException(), this.getException());
                }
                blockUI(false);
            }
        };
        new Thread(task).start();
    }
        /**
     * 組織選択ダイアログを表示する。
     *
     * @param event
     */
    private void showOrganizationSelectionDialog(ActionEvent event) {
        try {
            List<OrganizationInfoEntity> selectedItems = new ArrayList();

            if (Objects.nonNull(organizationSelectPaneController.getChoiceDatas())) {
                organizationSelectPaneController.getChoiceDatas().keySet().stream().forEach(id -> {
                    selectedItems.add(new OrganizationInfoFacade().find(id));
                });
            }

            SelectDialogEntity<OrganizationInfoEntity> selectDialog = new SelectDialogEntity().organizations(selectedItems);

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialog);
            if (ButtonType.OK.equals(ret)) {
                if (!selectDialog.getOrganizations().isEmpty()) {
                    // 選択された設備の情報を、設備選択ペインにセットする。
                    Map<Long, String> choiceDatas = new LinkedHashMap();
                    selectDialog.getOrganizations().stream().forEach(dat -> {
                        choiceDatas.put(dat.getOrganizationId(), dat.getOrganizationName());
                    });

                    organizationSelectPaneController.setChoiceDatas(choiceDatas);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 操作を無効にする。
     * 
     * @param block true:操作を無効にする、false:操作を有効にする
     */
    private void blockUI(boolean block) {
        Platform.runLater(() -> {
            SceneContiner.getInstance().blockUI("ContentNaviPane", block);
            progress.setVisible(block);
        });
    }

    /**
     * 検索条件を作成する。
     * 
     * @return 
     */
    private OperationLogCondition createOperationLogCondition() {
        OperationLogCondition condition = new OperationLogCondition();

        // 対象日
        Date fromDate = Objects.isNull(fromDatePicker.getValue()) ? null : DateUtils.getBeginningOfDate(fromDatePicker.getValue());
        Date toDate = Objects.isNull(toDatePicker.getValue()) ? null : DateUtils.getEndOfDate(toDatePicker.getValue());
        condition.setFromDate(fromDate);
        condition.setToDate(toDate);

        // 区画
        if (this.areaNameCheck.isSelected()) {
            condition.setAreaName(String.valueOf(areaNameField.getValue()));
        }

        // 組織ID一覧
        if (organizationSelectPaneController.isSelected()) {
            
            List<Long> ids = organizationSelectPaneController.getChoiceDatas().keySet().stream()
                    .map(o -> {
                        OrganizationInfoEntity organization = CacheUtils.getCacheOrganization(o);
                        if (Objects.nonNull(organization) && Objects.nonNull(organization.getOrganizationId())) {
                            return organization.getOrganizationId();
                        }
                        return 0L;
                     })
                    .filter(o -> o != 0L)
                    .collect(Collectors.toList());
            
            condition.setOrganizationIds(ids);
        }

        // 品目
        if (this.productNoCheck.isSelected()) {
            condition.setProductNo(this.productNoField.getText());
        }

        // 製造番号
        if (this.orderNoCheck.isSelected()) {
            condition.setOrderNo(this.orderNoField.getText());
        }

        // 払出指示番号
        if (this.deliveryNoCheck.isSelected()) {
            condition.setDeliveryNo(this.deliveryNoField.getText());
        }
        
        // ステータス
        if (this.categoryList.isSelected()) {
            List<WarehouseEvent> items = categoryList.getCheckedItems();
            if (!items.isEmpty()) {
                List<String> values = items.stream().map(o -> o.name()).collect(Collectors.toList());
                condition.setCategories(values);
            }
        }

        if (this.customOutCheck.isSelected()) {
            WarehouseEvent event = (WarehouseEvent) this.customOutField.getValue();
            if (Objects.nonNull(event)) {
                condition.setCategories(Arrays.asList(event.name()));
            }
        }

        return condition;
    }
    
    /**
     * 検索条件を作成する。
     * 
     * @return 
     */
    private MaterialCondition createMaterialCondition() {
        MaterialCondition condition = new MaterialCondition();

        // 対象日
        Date fromDate = Objects.isNull(fromDatePicker.getValue()) ? null : DateUtils.getBeginningOfDate(fromDatePicker.getValue());
        Date toDate = Objects.isNull(toDatePicker.getValue()) ? null : DateUtils.getEndOfDate(toDatePicker.getValue());
        condition.setFromDate(DateUtils.toLocalDate(fromDate));
        condition.setToDate(DateUtils.toLocalDate(toDate));

        // 検査実施済
        condition.setInspected(true);

        return condition;
    }
    
    /**
     * 画面を更新する。
     */
    private void updateView() {
        logger.info("updateView");
        boolean isCancel = false;

        try {
            blockUI(true);

            this.abort = true;
            this.operationLogTable.getItems().clear();
  

            // 検索条件
            final OperationLogCondition condition = this.createOperationLogCondition();

            Task task = new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    return facade.countOperationLog(condition);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        Integer count = this.getValue();

                        if (count > 0) {
                            // 実績出力情報を取得して、リスト表示を更新する。
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

    private void updateViewSub(OperationLogCondition condition, Integer count, long from) {
        logger.info("updateViewSub: condition={}, count={}, from={}", condition, count, from);
       
        try {
            blockUI(true);

            Task task = new Task<List<LogStockInfo>>() {
                @Override
                protected List<LogStockInfo> call() throws Exception {
                    return facade.searchOperationLog(condition, from, from + searchMax - 1);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    boolean isEnd = true;
                    try {
                        List<LogStockInfo> list = this.getValue();

                        operationLogTable.getItems().addAll(list);

                        long _from = from + list.size();
                        if (_from < count) {
                            // 残りがある場合、継続確認ダイアログを表示する。
                            String message = String.format(LocaleUtils.getString("key.overRangeSearchContinue"), searchMax);
                            MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(sc.getWindow(), LocaleUtils.getString("operationLog"), message,
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
                        MessageDialog.show(sc.getWindow(), LocaleUtils.getString("operationLog"), LocaleUtils.getString("key.alert.systemError"),
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
     * CSVファイルを出力する。(日本金銭様向けカスタム)
     */
    private void outputCustomCsv() {
        logger.info("outputCsv start.");
        try {
            final OperationCsvFactory factory = new OperationCsvFactory((WarehouseEvent) this.customOutField.getValue(), this.operationLogTable.getItems());

            boolean isChoice = factory.choiceFile();
            if (!isChoice) {
                return;
            }

            blockUI(true);

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if (WarehouseEvent.INSPECTION.equals(customOutField.getValue())) {
                        List<TrnMaterialInfo> materials = searchMaterials(createMaterialCondition()); 
                        factory.setMaterials(materials);
                    }
                    
                    factory.outputCsv();
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(LocaleUtils.getString("key.PrintOutCSV"));
                        alert.setHeaderText(LocaleUtils.getString("key.FileOutputCompleted"));
                        alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(LocaleUtils.getString("key.FileName") + ": " + factory.getCsvFile().getName())));
                        alert.showAndWait();

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
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

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(LocaleUtils.getString("key.PrintOutCSV"));
                        alert.setHeaderText(LocaleUtils.getString("key.FileOutputErrorOccured"));
                        alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(LocaleUtils.getString("key.ErrorDetail") + ": " + this.getException().getLocalizedMessage())));
                        alert.showAndWait();

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
     * CSV出力処理
     *
     * @param event
     */
    private void outputCsv(ActionEvent event) {
        logger.info("outputCsv: {}", event);
        blockUI(true);
        
        try {
            Node node = (Node) event.getSource();
            FileChooser fileChooser = new FileChooser();
            
            AdProperty.load(Constants.UI_PROPERTY_NAME, Constants.UI_PROPERTY_NAME + ".properties");
            Properties props = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);
            
            String lastExportDirPath = props.getProperty(Constants.LAST_EXPORT_DIR);
            File lastExportDir;
            if (!StringUtils.isEmpty(lastExportDirPath)) {
                lastExportDir = new File(lastExportDirPath);
                // 前回の出力先のディレクトリが存在しない場合、デスクトップフォルダを設定
                if (!lastExportDir.exists()) {
                    lastExportDirPath = System.getProperty("user.home") + "/Desktop";
                    // デスクトップフォルダの取得に失敗した場合、Cドライブに設定
                    if (StringUtils.isEmpty(lastExportDirPath)) {
                        lastExportDirPath = "C:/";
                    }
                    lastExportDir = new File(lastExportDirPath);
                }
            } else {
                // プロパティファイルに前回の出力先のディレクトリの設定が存在しない場合、デスクトップフォルダを設定
                lastExportDirPath = System.getProperty("user.home") + "/Desktop";
                // デスクトップフォルダの取得に失敗した場合、Cドライブに設定
                if (StringUtils.isEmpty(lastExportDirPath)) {
                    lastExportDirPath = "C:/";
                }
                lastExportDir = new File(lastExportDirPath);
            }
            
            if (lastExportDir.exists()) {
                fileChooser.setInitialDirectory(lastExportDir);
            }
            
            fileChooser.setTitle(LocaleUtils.getString("operationLog"));
            FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("TSV files (*.tsv)", "*.tsv");
            fileChooser.getExtensionFilters().addAll(extFilter1, extFilter2);

            File selectFile = fileChooser.showSaveDialog(node.getScene().getWindow());
            if (Objects.isNull(selectFile)) {
                return;
            }
            
            props.setProperty(Constants.LAST_EXPORT_DIR, selectFile.getParent());
            AdProperty.store(Constants.UI_PROPERTY_NAME);

            String encode = AdProperty.getProperties().getProperty(Constants.EXPORT_CHARSET).toUpperCase();
            if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS").contains(encode)) {
                encode = "MS932";
            }

            Character separate = (FilenameUtils.getExtension(selectFile.getPath()).equals("tsv")) ? '\t' : ',';

            // CSV出力
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(selectFile), encode))) {
                // ヘッダー
                StringBuilder headerSb = new StringBuilder();
                for (TableColumn<LogStockInfo, ?> column : operationLogTable.getVisibleLeafColumns()) {
                    headerSb.append(column.getText());
                    headerSb.append(separate);
                }
                headerSb.append('\n');
                writer.write(headerSb.toString());

                // データ
                for (int row = 0; row < operationLogTable.getItems().size(); row++) {
                    StringBuilder rowSb = new StringBuilder();

                    for (TableColumn<LogStockInfo, ?> column : operationLogTable.getVisibleLeafColumns()) {
                        // データ内の改行コードは文字列に変換して出力する。
                        String rowString = toString(column.getCellData(row)).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
                        rowSb.append(rowString);
                        rowSb.append(separate);
                    }
                    rowSb.append('\n');
                    writer.write(rowSb.toString());
                }
            } catch (Exception ex) {
                throw ex;
            }

            MessageDialog.show(sc.getWindow(), LocaleUtils.getString("key.PrintOutCSV"), String.format(LocaleUtils.getString("key.SaveFileCompleted"), LocaleUtils.getString("key.CurrentInformation")),
                MessageDialogEnum.MessageDialogType.Infomation, MessageDialogEnum.MessageDialogButtons.OK, 1.0, "#000000", "#ffffff");

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }
    
    /**
     * 文字列を返す。
     *
     * @param value オブジェクト
     * @return 文字列
     */
    private String toString(Object value) {
        if (Objects.isNull(value)) {
            return "";
        }
        return Objects.toString(value);
    }

    /**
     * 資材情報を取得する。
     *
     * @param condition 検索条件
     * @return 資材情報一覧
     */
    private List<TrnMaterialInfo> searchMaterials(MaterialCondition condition) {
        List<TrnMaterialInfo> infos = new LinkedList();

        int range = Integer.parseInt(AdProperty.getProperties().getProperty(StockViewController.class.getName() + ".range", "500"));

        int count = this.facade.countMaterials(condition);
        for (int ii = 0; ii < count; ii += range) {
            List<TrnMaterialInfo> materialList = this.facade.searchMaterials(condition, ii, ii + range - 1);
            infos.addAll(materialList);
        }

        return infos;
    }
}
