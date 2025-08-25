/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.component;

import adtekfuji.admanagerapp.warehouseplugin.common.Constants;
import adtekfuji.admanagerapp.warehouseplugin.common.LotTraceComparators;
import adtekfuji.admanagerapp.warehouseplugin.common.LotTraceCsvFactory;
import adtekfuji.clientservice.WarehouseInfoFaced;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.search.LotTraceCondition;
import jp.adtekfuji.adFactory.entity.warehouse.TrnLotTraceInfo;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTreeTableView;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ロットトレース(作業者)画面 コントローラー
 *
 * @author nar-nakamura
 */
@FxComponent(id = "LotTracePerson", fxmlPath = "/fxml/warehouseplugin/lot_trace_person.fxml")
public class LotTracePersonController implements Initializable, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final WarehouseInfoFaced facade = new WarehouseInfoFaced();

    private final TreeItem<TrnLotTraceInfo> mainRoot = new TreeItem<>(new TrnLotTraceInfo());
    private final TreeItem<TrnLotTraceInfo> subRoot = new TreeItem<>(new TrnLotTraceInfo());
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final List<TrnLotTraceInfo> lotTraceList = new LinkedList();

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private CheckBox productNoCheck;
    @FXML
    private TextField productNoField;
    @FXML
    private CheckBox lotNoCheck;
    @FXML
    private TextField lotNoField;
    @FXML
    private CheckBox materialNoCheck;
    @FXML
    private TextField materialNoField;
    @FXML
    private CheckBox personNameCheck;
    @FXML
    private TextField personNameField;
    @FXML
    private SplitPane splitPane;

    @FXML
    private PropertySaveTreeTableView<TrnLotTraceInfo> mainTable;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> kanbanNameColumn;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> modelNameColumn;

    @FXML
    private PropertySaveTreeTableView<TrnLotTraceInfo> subTable;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> subProductNoColumn;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> subProductNameColumn;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> subLotNoColumn;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> subMaterialNoColumn;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> subQuantityColumn;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> subAssemblyDatetimeColumn;

    @FXML
    private Button outputCsvButton;
    @FXML
    private Pane progressPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        mainTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        subTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        // 日付範囲
        LocalDate now = LocalDate.now();
        this.startDatePicker.setValue(now);
        this.endDatePicker.setValue(now);

        // 入力欄を無効にする。
        this.productNoField.setDisable(true);
        this.lotNoField.setDisable(true);
        this.materialNoField.setDisable(true);
        this.personNameField.setDisable(true);

        // CSV出力ボタンを無効にする。
        this.outputCsvButton.setDisable(true);

        // 検索条件のチェックがONの場合、入力欄を無効にする。
        // 品目
        this.productNoCheck.selectedProperty().addListener((observable, newValue, oldValue) -> {
            this.productNoField.setDisable(newValue);
        });

        // 製造番号
        this.lotNoCheck.selectedProperty().addListener((observable, newValue, oldValue) -> {
            this.lotNoField.setDisable(newValue);
        });

        // 資材番号
        this.materialNoCheck.selectedProperty().addListener((observable, newValue, oldValue) -> {
            this.materialNoField.setDisable(newValue);
        });

        // 作業者名
        this.personNameCheck.selectedProperty().addListener((observable, newValue, oldValue) -> {
            this.personNameField.setDisable(newValue);
        });

        SplitPaneUtils.loadDividerPosition(this.splitPane, this.getClass().getSimpleName());

        this.mainTable.setRowFactory(value -> {
            TreeTableRow<TrnLotTraceInfo> row = new TreeTableRow<TrnLotTraceInfo>() {
                @Override
                protected void updateItem(TrnLotTraceInfo item, boolean empty) {
                    super.updateItem(item, empty);
                }
            };

            this.mainTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<TrnLotTraceInfo>> item) -> {
                if (row.isFocused()) {
                    row.setStyle(null);
                } else {
                    if (Objects.nonNull(row.getTreeItem())) {
                        row.setStyle(row.getTreeItem().isLeaf()? null : "-fx-background-color:#f2f2f2;");
                    } else {
                        row.setStyle(null);
                    }
                }
            });

            return row ;
        });

        // ロットトレース情報の右寄せセル
        Callback<TreeTableColumn<TrnLotTraceInfo, String>, TreeTableCell<TrnLotTraceInfo, String>> lotTraceCellRightFactory =
            (final TreeTableColumn<TrnLotTraceInfo, String> param) -> {
                TreeTableCell<TrnLotTraceInfo, String>cell = new TreeTableCell<TrnLotTraceInfo, String>() {
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
                cell.setAlignment(Pos.BASELINE_RIGHT);
                cell.setPadding(new Insets(0, 6, 0, 0));
                return cell;
        };

        // メインテーブルの行選択でサブテーブルの表示を更新する。
        this.mainTable.getSelectionModel().getSelectedItems().addListener(
            (ListChangeListener.Change<? extends TreeItem<TrnLotTraceInfo>> change) -> {
                this.updateSubTable();
            }
        );

        // ツリーのルートアイテムを非表示にする。
        this.mainTable.setShowRoot(false);
        this.subTable.setShowRoot(false);

        // カンバン名
        this.kanbanNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o) 
                -> new ReadOnlyStringWrapper(o.getValue().getValue().getKanbanName()));

        this.kanbanNameColumn.setCellFactory((TreeTableColumn<TrnLotTraceInfo, String> param) -> {
            TreeTableCell cell = new TreeTableCell<TrnLotTraceInfo, String>(){
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    try {
                        TreeTableRow<TrnLotTraceInfo> row = getTreeTableRow();
                        if (empty) {
                            setText(null);
                            row.setStyle(null);
                        } else {
                            if (Objects.isNull(row.getTreeItem()) || !row.getTreeItem().isLeaf()) {
                                row.setStyle(null);
                            } else if (isFocused() || isSelected()) {
                                row.setStyle("-fx-background-color: #0096c9; -fx-table-cell-border-color: #21a5d1; -fx-text-background-color: #ffffff;");
                            } else {
                                row.setStyle("-fx-background-color:#f2f2f2;");
                            }
                            
                            setText(item);
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }
            };
            return cell;
        });

        // モデル名
        this.modelNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o) 
                -> new ReadOnlyStringWrapper(o.getValue().getValue().getModelName()));

        // 品目
        this.subProductNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o) 
                -> new ReadOnlyStringWrapper(o.getValue().getValue().getProductNo()));
        // 品名
        this.subProductNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o) 
                -> new ReadOnlyStringWrapper(o.getValue().getValue().getProductName()));
        // ロット番号
        this.subLotNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o) 
                -> new ReadOnlyStringWrapper(o.getValue().getValue().getPartsNo()));
        // 資材番号
        this.subMaterialNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o) 
                -> new ReadOnlyStringWrapper(o.getValue().getValue().getMaterialNo()));
        // 数量
        this.subQuantityColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o) 
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getTraceNum()) ? String.valueOf(o.getValue().getValue().getTraceNum()) : null));
        this.subQuantityColumn.setCellFactory(lotTraceCellRightFactory);
        // 組付け日時
        this.subAssemblyDatetimeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o)
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getAssemblyDatetime()) ? 
                        formatter.format(o.getValue().getValue().getAssemblyDatetime()) : null));

        // 前回の検索条件を読み込む。
        this.loadSearchConditions();
    }

    @Override
    public boolean destoryComponent() {
        // 検索条件を保存する。
        this.saveSearchConditions();
        return true;
    }

    @FXML
    private void onSearch(ActionEvent event) {
        this.searchLotTrace();
    }

    @FXML
    private void onOutputCsv(ActionEvent event) {
        this.outputCsv();
    }

    /**
     * 検索条件を読み込む。
     */
    private void loadSearchConditions() {
        try {
            AdProperty.load(Constants.UI_PROPERTY_NAME, Constants.UI_PROPERTY_NAME + ".properties");

            String prefix = LotTracePersonController.class.getName() + ".";

            Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);

            // 日付範囲
            String startDateProp = properties.getProperty(prefix + "startDatePicker", "");
            if (!StringUtils.isEmpty(startDateProp)) {
                LocalDate startDate = LocalDate.parse(startDateProp, DateTimeFormatter.ISO_DATE);
                this.startDatePicker.setValue(startDate);
            }

            String endDateProp = properties.getProperty(prefix + "endDatePicker", "");
            if (!StringUtils.isEmpty(endDateProp)) {
                LocalDate endDate = LocalDate.parse(endDateProp, DateTimeFormatter.ISO_DATE);
                this.endDatePicker.setValue(endDate);
            }

            // 品目
            this.productNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "productNoCheck", String.valueOf(Boolean.FALSE))));
            this.productNoField.setText(properties.getProperty(prefix + "productNoField"));

            // 製造番号
            this.lotNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "lotNoCheck", String.valueOf(Boolean.FALSE))));
            this.lotNoField.setText(properties.getProperty(prefix + "lotNoField"));

            // 資材番号
            this.materialNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "materialNoCheck", String.valueOf(Boolean.FALSE))));
            this.materialNoField.setText(properties.getProperty(prefix + "materialNoField"));

            // 作業者名
            this.personNameCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "personNameCheck", String.valueOf(Boolean.FALSE))));
            this.personNameField.setText(properties.getProperty(prefix + "personNameField"));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 検索条件を保存する。
     */
    private void saveSearchConditions() {
        try {
            String prefix = LotTracePersonController.class.getName() + ".";

            Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);

            // 日付範囲
            LocalDate startDate = this.startDatePicker.getValue();
            if (Objects.nonNull(startDate)) {
                properties.setProperty(prefix + "startDatePicker", startDate.format(DateTimeFormatter.ISO_DATE));
            } else {
                properties.setProperty(prefix + "startDatePicker", "");
            }

            LocalDate endDate = this.endDatePicker.getValue();
            if (Objects.nonNull(endDate)) {
                properties.setProperty(prefix + "endDatePicker", endDate.format(DateTimeFormatter.ISO_DATE));
            } else {
                properties.setProperty(prefix + "endDatePicker", "");
            }

            // 品目
            properties.setProperty(prefix + "productNoCheck", String.valueOf(this.productNoCheck.isSelected()));
            if (Objects.nonNull(this.productNoField.getText())) {
                properties.setProperty(prefix + "productNoField", this.productNoField.getText());
            }

            // 製造番号
            properties.setProperty(prefix + "lotNoCheck", String.valueOf(this.lotNoCheck.isSelected()));
            if (Objects.nonNull(this.lotNoField.getText())) {
                properties.setProperty(prefix + "lotNoField", this.lotNoField.getText());
            }

            // 資材番号
            properties.setProperty(prefix + "materialNoCheck", String.valueOf(this.materialNoCheck.isSelected()));
            if (Objects.nonNull(this.materialNoField.getText())) {
                properties.setProperty(prefix + "materialNoField", this.materialNoField.getText());
            }

            // 作業者名
            properties.setProperty(prefix + "personNameCheck", String.valueOf(this.personNameCheck.isSelected()));
            if (Objects.nonNull(this.personNameField.getText())) {
                properties.setProperty(prefix + "personNameField", this.personNameField.getText());
            }

            AdProperty.store(Constants.UI_PROPERTY_NAME);

            SplitPaneUtils.saveDividerPosition(this.splitPane, this.getClass().getSimpleName());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ロットトレース情報を取得する。
     */
    private void searchLotTrace() {
        logger.info("searchLotTrace start.");
        try {
            final LotTraceCondition condition = new LotTraceCondition();

            // 組付け日
            if (Objects.nonNull(this.startDatePicker.getValue())) {
                condition.setFromDate(DateUtils.getBeginningOfDate(this.startDatePicker.getValue()));
            }

            if (Objects.nonNull(this.endDatePicker.getValue())) {
                condition.setToDate(DateUtils.getEndOfDate(this.endDatePicker.getValue()));
            }

            // 品目
            if (this.productNoCheck.isSelected()
                    && !StringUtils.isEmpty(this.productNoField.getText())) {
                condition.setProductNo(this.productNoField.getText());
            }

            // 製造番号(ロット番号)
            if (this.lotNoCheck.isSelected()
                    && !StringUtils.isEmpty(this.lotNoField.getText())) {
                condition.setPartsNo(this.lotNoField.getText());
            }

            // 資材番号
            if (this.materialNoCheck.isSelected()
                    && !StringUtils.isEmpty(this.materialNoField.getText())) {
                condition.setMaterialNos(Arrays.asList(this.materialNoField.getText()));
                condition.setEqualMaterialNo(false); // 中間一致で検索
            }

            // 作業者名
            if (this.personNameCheck.isSelected()
                    && !StringUtils.isEmpty(this.personNameField.getText())) {
                condition.setPersonName(this.personNameField.getText());
            }

            // 作業済のみ取得
            condition.setWorkedOnly(true);

            this.lotTraceList.clear();

            final int range = Integer.parseInt(AdProperty.getProperties().getProperty("lotTrace.range", "500"));

            this.blockUI(true);

            Task task = new Task<List<TrnLotTraceInfo>>() {
                @Override
                protected List<TrnLotTraceInfo> call() throws Exception {
                    List<TrnLotTraceInfo> result = new LinkedList();

                    // 条件を指定してロットトレース情報の件数を取得する。
                    int count = facade.countLotTrace(condition);

                    // 条件を指定してロットトレース情報一覧を取得する。(分割取得)
                    for (int from = 0; from < count; from += range) {
                        List<TrnLotTraceInfo> infos = facade.searchLotTrace(condition, from, from + range - 1);
                        if (!infos.isEmpty()) {
                            result.addAll(infos);
                        }
                    }

                    // ソートする。
                    Collections.sort(result, LotTraceComparators.lotTracePersonNoComparator
                            .thenComparing(LotTraceComparators.lotTraceKanbanNameComparator)
                            .thenComparing(LotTraceComparators.lotTraceModelNameComparator)
                            .thenComparing(LotTraceComparators.lotTraceKanbanIdComparator));

                    return result;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        lotTraceList.addAll(this.get());

                        // メインテーブルを更新する。
                        updateMainTable();
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        clearTable();
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
                        // テーブルをクリアする。
                        clearTable();
                    }
                }
            };

            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.blockUI(false);
        }
    }

    /**
     * メインテーブルを更新する。
     */
    private void updateMainTable() {
        List<TreeItem<TrnLotTraceInfo>> elements = FXCollections.observableArrayList();
        try {
            TrnLotTraceInfo parent = new TrnLotTraceInfo();
            TreeItem<TrnLotTraceInfo> parentItem = new TreeItem(parent);
            List<TreeItem<TrnLotTraceInfo>> subElements = new ArrayList<>();

            TrnLotTraceInfo item = new TrnLotTraceInfo();

            for (TrnLotTraceInfo lotTrace : lotTraceList) {
                if (!Objects.equals(parent.getPersonNo(), lotTrace.getPersonNo())
                        || !parent.isParent()) {
                    if (!subElements.isEmpty()) {
                        parentItem.getChildren().addAll(subElements);

                        // ルートツリーに追加
                        elements.add(parentItem);

                        subElements.clear();
                    }

                    // 親アイテム(社員番号, 作業者名)
                    parent = TrnLotTraceInfo.createPersonItem(null, lotTrace.getPersonNo(), lotTrace.getPersonName(), lotTrace.getPersonNo());
                    parentItem = new TreeItem(parent);
                    parentItem.setExpanded(true);
                }

                if (Objects.equals(item.getPersonNo(), lotTrace.getPersonNo())
                        && Objects.equals(item.getKanbanId(), lotTrace.getKanbanId())) {
                    continue;
                }

                item = TrnLotTraceInfo.createPersonItem(lotTrace.getKanbanId(), lotTrace.getKanbanName(), lotTrace.getModelName(), lotTrace.getPersonNo());
                subElements.add(new TreeItem(item));
            }

            if (!subElements.isEmpty()) {
                parentItem.getChildren().addAll(subElements);
                elements.add(parentItem);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            // サブテーブルをクリアする。
            this.subTable.getSelectionModel().clearSelection();
            this.subRoot.getChildren().clear();
            // メインテーブルをクリアする。
            this.mainTable.getSelectionModel().clearSelection();
            this.mainRoot.getChildren().clear();
            // メインテーブルを更新する。
            this.mainRoot.getChildren().addAll(elements);
            this.mainTable.setRoot(this.mainRoot);

            // CSV出力ボタンの無効状態を設定する。
            this.outputCsvButton.setDisable(lotTraceList.isEmpty());

            this.blockUI(false);
        }
    }

    /**
     * サブテーブルを更新する。
     */
    private void updateSubTable() {
        List<TreeItem<TrnLotTraceInfo>> elements = FXCollections.observableArrayList();
        try {
            this.blockUI(true);

            List<TreeItem<TrnLotTraceInfo>> items = this.mainTable.getSelectionModel().getSelectedItems();
            if (Objects.isNull(items) || items.isEmpty()) {
                return;
            }

            TrnLotTraceInfo parentInfo = items.get(0).getValue();
            if (Objects.isNull(parentInfo.getKanbanId())) {
                return;
            }

            List<TrnLotTraceInfo> lotTraces = this.lotTraceList.stream()
                    .filter(p -> Objects.equals(p.getKanbanId(), parentInfo.getKanbanId()))
                    .sorted(LotTraceComparators.lotTraceWorkNameComparator
                            .thenComparing(LotTraceComparators.lotTraceProductNoComparator)
                            .thenComparing(LotTraceComparators.lotTraceLotNoComparator)
                            .thenComparing(LotTraceComparators.lotTraceMaterialNoComparator)
                            .thenComparing(LotTraceComparators.lotTraceAssemblyDatetimeComparator)
                            .thenComparing(LotTraceComparators.lotTracePersonNameComparator))
                    .collect(Collectors.toList());

            TrnLotTraceInfo parent = new TrnLotTraceInfo();
            TreeItem<TrnLotTraceInfo> parentItem = new TreeItem(parent);
            List<TreeItem<TrnLotTraceInfo>> subElements = new ArrayList<>();

            for (TrnLotTraceInfo lotTrace : lotTraces) {
                if (!Objects.equals(parent.getWorkName(), lotTrace.getWorkName())) {
                    if (!subElements.isEmpty()) {
                        parentItem.getChildren().addAll(subElements);

                        // ルートツリーに追加
                        elements.add(parentItem);

                        subElements.clear();
                    }

                    // 親アイテム(工程名)
                    parent = TrnLotTraceInfo.createProductWorkItem(lotTrace.getWorkName());
                    parentItem = new TreeItem(parent);
                    parentItem.setExpanded(true);
                }

                subElements.add(new TreeItem(lotTrace));
            }

            if (!subElements.isEmpty()) {
                parentItem.getChildren().addAll(subElements);
                elements.add(parentItem);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            // サブテーブルをクリアする。
            this.subTable.getSelectionModel().clearSelection();
            this.subRoot.getChildren().clear();
            // サブテーブルを更新する。
            this.subRoot.getChildren().addAll(elements);
            this.subTable.setRoot(this.subRoot);

            this.blockUI(false);
        }
    }

    /**
     * テーブルをクリアする。
     */
    private void clearTable() {
        // サブテーブルをクリアする。
        this.subTable.getSelectionModel().clearSelection();
        this.subRoot.getChildren().clear();
        // メインテーブルをクリアする。
        this.mainTable.getSelectionModel().clearSelection();
        this.mainRoot.getChildren().clear();

        this.mainTable.setRoot(this.mainRoot);

        this.blockUI(false);
    }

    /**
     * CSVファイル出力する。
     */
    private void outputCsv() {
        logger.info("outputCsv start.");
        try {
            final LotTraceCsvFactory csvFactory = new LotTraceCsvFactory(this.lotTraceList);

            boolean isChoice = csvFactory.choiceFile();
            if (!isChoice) {
                return;
            }

            blockUI(true);

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    csvFactory.outputCsv();
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(LocaleUtils.getString("key.PrintOutCSV"));
                        alert.setHeaderText(LocaleUtils.getString("key.FileOutputCompleted"));
                        alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(LocaleUtils.getString("key.FileName") + ": " + csvFactory.getCsvFile().getName())));
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
     * 操作を無効にする。
     *
     * @param block true:操作無効, false:操作有効
     */
    private void blockUI(boolean block) {
        this.sc.blockUI("ContentNaviPane", block);
        this.progressPane.setVisible(block);
    }
}
