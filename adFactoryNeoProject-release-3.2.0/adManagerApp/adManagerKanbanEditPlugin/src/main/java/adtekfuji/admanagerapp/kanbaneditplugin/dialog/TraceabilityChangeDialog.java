/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.dialog;

import adtekfuji.admanagerapp.kanbaneditplugin.common.Constants;
import adtekfuji.admanagerapp.kanbaneditplugin.common.XmlSerializer;
import adtekfuji.admanagerapp.kanbaneditplugin.controls.FixValueCell;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.TracebilityItem;
import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.WorkInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualPropertyEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.work.TraceOptionEntity;
import jp.adtekfuji.adFactory.entity.work.TraceSettingEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.TraceOptionTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 品質データ修正ダイアログ
 *
 * @author y-harada
 */
@FxComponent(id = "TraceabilityChangeDialog", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/traceability_change_dialog.fxml")
public class TraceabilityChangeDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final Properties properties = AdProperty.getProperties();
    
    private static final long RANGE = 20;
    private static final String TAG_COLUMN_VISIBILE = "changeTraceTagColumnVisible";
    /**
     * FILE タグ
     */
    private static final String TAG_FILE = "_FILE_";
    /**
     * PIC タグ
     */
    private static final String TAG_PIC = "_PIC_";
    
    private KanbanInfoEntity kanban;
    private final List<WorkInfoEntity> works = new ArrayList<>();   
    private final Map<Long, ActualResultEntity> actualMap = new TreeMap<>() ;
    private final NavigableMap<String, IdEntitySet> actualTagMap = new TreeMap<>();
    private Dialog dialog;
    
    private final List<TracebilityItem> masterList = new ArrayList<>();
    private final List<TracebilityItem> filteredList = new ArrayList<>();
    private boolean workComboChangeCancel = false;
    
    @FXML
    private StackPane pane;
    @FXML
    private Label kanbanNameLabel;
    @FXML
    private ComboBox<WorkKanbanInfoEntity> workComboBox;
    @FXML
    private TextField searchTextField;
    @FXML
    private PropertySaveTableView<TracebilityItem> tracebilityList;
    @FXML
    private TableColumn<TracebilityItem, String> nameColumn; // 項目
    @FXML
    private TableColumn<TracebilityItem, String> tagColumn; // タグ
    @FXML
    private TableColumn<TracebilityItem, String> currentValueColumn; // 現在値
    @FXML
    private TableColumn<TracebilityItem, String> fixValueColumn; // 修正値
    /**
     * ダウンロードボタン
     */
    @FXML
    private Button downloadButton;
    
    @FXML
    private Pane progress;

    /**
     * 工程実績IDと工程実績プロパティのセット
     */
    private class IdEntitySet {

        private final Long actualId;
        private final ActualPropertyEntity propEntity;

        /**
         * 初期化
         * @param actualId 工程実績ID 
         * @param entity 工程実績プロパティ
         */
        public IdEntitySet(long actualId, ActualPropertyEntity propEntity) {
            this.actualId = actualId;
            this.propEntity = propEntity;
        }

        /**
         * 工程実績ID取得
         * @return ID
         */
        public Long getActualId() {
            return this.actualId;
        }

        /**
         * 工程実績プロパティ取得
         * @return プロパティ
         */
        public ActualPropertyEntity getEntity() {
            return this.propEntity;
        }
    }
    
    /**
     * 工程コンボボックス用セルファクトリー
     *
     */
    private class WorkComboBoxCellFactory extends ListCell<WorkKanbanInfoEntity> {
        @Override
        protected void updateItem(WorkKanbanInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(item.getWorkName());
            }
        }
    }
    
    /**
     * 現在値セルファクトリー
     *
     */
    private class currentValueCell extends TableCell<TracebilityItem, String> {
        @Override
        protected void updateItem(String value, boolean empty) {
            super.updateItem(value, empty);
            this.setText(null);
            if (empty) {
                return;
            }
            TracebilityItem item = tracebilityList.getItems().get(this.getIndex());
            switch(item.getInputType()){
                // チェックボックス
                case CHECKBOX:
                    if (value.equals("1")) {
                        this.setText("✔");
                    }
                    break;
                default:
                    this.setText(value);
                    break;
            }
        }
    }
    
    /**
     * コンストラクタ
     */
    public TraceabilityChangeDialog() {

    }

    /**
     * 初期化
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {


        tracebilityList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        this.progress.setVisible(false);
        
        Callback<ListView<WorkKanbanInfoEntity>, ListCell<WorkKanbanInfoEntity>> comboCellFactory = (ListView<WorkKanbanInfoEntity> param) -> new WorkComboBoxCellFactory();
        this.workComboBox.setButtonCell(new WorkComboBoxCellFactory());
        this.workComboBox.setCellFactory(comboCellFactory);
        
        this.searchTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            updateFilteredList();
        });
        
        this.tracebilityList.setEditable(true);
        // 項目
        this.nameColumn.setCellValueFactory((TableColumn.CellDataFeatures<TracebilityItem, String> param) -> param.getValue().nameProperty());
        // タグ
        this.tagColumn.setCellValueFactory((TableColumn.CellDataFeatures<TracebilityItem, String> param) -> param.getValue().tagProperty());
        this.tagColumn.setVisible(Boolean.valueOf(properties.getProperty(TAG_COLUMN_VISIBILE, "true")));
        // 現在値
        this.currentValueColumn.setCellValueFactory((TableColumn.CellDataFeatures<TracebilityItem, String> param) -> param.getValue().currentValueProperty());
        this.currentValueColumn.setCellFactory(new Callback<TableColumn<TracebilityItem, String>, TableCell<TracebilityItem, String>>() {
            @Override
            public TableCell<TracebilityItem, String> call(TableColumn<TracebilityItem, String> arg0) {
                return new currentValueCell();
            }
        });
        // 修正値
        this.fixValueColumn.setCellValueFactory((TableColumn.CellDataFeatures<TracebilityItem, String> param) -> param.getValue().inputValueProperty());
        this.fixValueColumn.setCellFactory(new Callback<TableColumn<TracebilityItem, String>, TableCell<TracebilityItem, String>>() {
            @Override
            public TableCell<TracebilityItem, String> call(TableColumn<TracebilityItem, String> arg0) {
                return new FixValueCell(tracebilityList);
            }
        });
        
        // カラムの初期化
        this.tracebilityList.init("TraceabilityChangeDialog");
        
    }

    /**
     * 引数取得
     * @param argument 引数
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof KanbanInfoEntity) {
            this.kanban = (KanbanInfoEntity) argument;
            this.kanbanNameLabel.setText(kanban.getKanbanName());
            
            this.kanban.getWorkKanbanCollection().stream().forEach(entity -> this.works.add(this.getWorkInfo(entity.getFkWorkId(), true)));
            this.kanban.getSeparateworkKanbanCollection().stream().forEach(entity -> this.works.add(this.getWorkInfo(entity.getFkWorkId(), true)));
            
            List<WorkKanbanInfoEntity> workKanbans = new ArrayList<>();
            workKanbans.addAll(this.kanban.getWorkKanbanCollection());
            workKanbans.addAll(this.kanban.getSeparateworkKanbanCollection());
            
            // 工程コンボボックス設定
            this.workComboBox.setItems(FXCollections.observableArrayList(workKanbans));
            this.workComboBox.setVisible(true);
            this.workComboBox.getSelectionModel().select(0);
            this.workComboBox.valueProperty().addListener((ObservableValue<? extends WorkKanbanInfoEntity> observable, WorkKanbanInfoEntity oldValue, WorkKanbanInfoEntity newValue) -> {
                if (Objects.nonNull(newValue)) {
                    // 変更取り消し時
                    if (this.workComboChangeCancel){
                        this.workComboChangeCancel = false;
                        return;
                    }
                    
                    // 変更されている場合は保存確認ダイアログを表示する
                    if (destoryComponent()) {
                        Task task = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                updateView();
                                return null;
                            }
                        };
                        new Thread(task).start();
                    } else { 
                        // 変更取り消し
                        this.workComboChangeCancel = true;
                        this.workComboBox.setValue(oldValue);
                    }
                }
            });
            
            // 品質データの行選択時の処理
            this.tracebilityList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TracebilityItem> observable, TracebilityItem oldValue, TracebilityItem newValue) -> {
                // ダウンロードボタンの活性状態制御
                TracebilityItem selectedItem = tracebilityList.getSelectionModel().getSelectedItem();
                if (Objects.nonNull(selectedItem)) {
                    if (selectedItem.getTag().contains(TAG_FILE) || selectedItem.getTag().contains(TAG_PIC)) {
                        this.downloadButton.setDisable(false);
                    } else {
                        this.downloadButton.setDisable(true);
                    }
                }
            });

            blockUI(true);
            Platform.runLater(() -> {
                try {
                    // 画面更新
                    this.updateView();
                } catch(Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }

            });
        }
    }

    /**
     * ダイアログ設定
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        Stage stage = (Stage) this.dialog.getDialogPane().getScene().getWindow();
        stage.setMinWidth(600.0);
        stage.setMinHeight(400.0);
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            this.pane.requestLayout();
        });
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            this.pane.requestLayout();
        });
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent e) -> {
            this.cancelDialog(e);
        });
    }
    
    /**
     * フィルタリスト更新
     */
    private void updateFilteredList() {
        this.filteredList.clear();
        for (TracebilityItem entity : this.masterList) {
            if (matchesFilter(entity)) {
                this.filteredList.add(entity);
            }
        }
        
        ArrayList<TableColumn<TracebilityItem, ?>> sortOrder = new ArrayList<>(this.tracebilityList.getSortOrder());
        this.tracebilityList.getSortOrder().clear();
        this.tracebilityList.getItems().clear();
        this.tracebilityList.getItems().addAll(this.filteredList);
        this.tracebilityList.getSortOrder().addAll(sortOrder);
        
        // ダウンロードボタンの活性状態初期化
        this.downloadButton.setDisable(true);
    }
    
    /**
     * 検索テキストフィールドに入力された文字列にマッチするか
     */
    private boolean matchesFilter(TracebilityItem entity) {
        String filterString = this.searchTextField.getText();
        if (filterString == null || filterString.isEmpty()) {
            return true;
        }

        String lowerCaseFilterString = filterString.toLowerCase();
        if (entity.getName().toLowerCase().contains(lowerCaseFilterString)) {
            return true;
        }
        return false;
    }
    
    /**
     * 実績MAP更新
     * @param workKanbanId 工程カンバンID
     */
    private void updateActual(Long workKanbanId) {
        
        this.actualMap.clear();
        this.actualTagMap.clear();
        
        List<ActualResultEntity> entities = new ArrayList<>();
        ActualResultInfoFacade facade = new ActualResultInfoFacade();
        ActualSearchCondition condition = new ActualSearchCondition().workKanbanList(Arrays.asList(workKanbanId));

        Long count = facade.searchCount(condition);
        for (long i = 0; i <= count; i += RANGE) {
            entities.addAll(facade.searchRange(condition, i, i + RANGE - 1));
        }

        for (ActualResultEntity actual : entities) {
            this.actualMap.put(actual.getActualId(), actual);
            for (ActualPropertyEntity actProp : actual.getPropertyCollection()) {
                String actPropTag = actProp.getActualPropName();
                IdEntitySet set = new IdEntitySet(actual.getActualId(), actProp);
                // Mapにputで回しきるので同一タグは最新の値が入る
                this.actualTagMap.put(actPropTag, set);
            }
        }
    }

    /**
     * 画面更新処理
     */
    private void updateView() {
        logger.info("updateView start");
        boolean isCancel = false;

        try {
            blockUI(true);
            this.tracebilityList.getItems().clear();
            this.tracebilityList.getSortOrder().clear();
            this.masterList.clear();
            
            Optional<WorkInfoEntity> opt = this.works.stream().filter(o -> Objects.equals(o.getWorkId(), this.workComboBox.getValue().getFkWorkId())).findFirst();
            if (!opt.isPresent()) {
               return; 
            }

            WorkInfoEntity work = opt.get();

            updateActual(this.workComboBox.getValue().getWorkKanbanId());

            List<WorkPropertyInfoEntity> workProps = this.getProperties(work);
            for (Entry<String, IdEntitySet> actual: this.actualTagMap.entrySet()) {

                if (!CustomPropertyTypeEnum.TYPE_TRACE.equals(actual.getValue().getEntity().getActualPropType())) {
                    continue;
                }
                
                WorkPropertyInfoEntity workProperty = null;
                for (WorkPropertyInfoEntity prop : workProps) {
                    if (!StringUtils.isEmpty(prop.getWorkPropTag())) {
                        String regex = Pattern.quote(prop.getWorkPropTag());
                        if (actual.getKey().matches(regex + "|" + regex + "_(?!ED\\().*|.*\\(" + regex + "\\)$")) {
                            workProperty = prop;
                            break;
                        }
                    }                        
                }

                if (Objects.nonNull(workProperty)) {
                    List<String> inputList = null;
                    TraceSettingEntity traceSetting = this.getTraceSetting(workProperty);
      
                    if (actual.getKey().endsWith("_EQUIPMENT")) {
                        // 管理番号リストを取得
                        if (traceSetting.containsKey(TraceOptionTypeEnum.REFERENCE_NUMBER.name())) {
                            TraceOptionEntity traceOption = traceSetting.getTraceOption(TraceOptionTypeEnum.REFERENCE_NUMBER.name());
                            List<EquipmentInfoEntity> device = work.getDeviceCollection();
                            inputList = this.createEquipmentList(device, traceOption.getValues());
                        }

                    } else if (actual.getKey().endsWith("_OK")) {
                        // 処理なし

                    } else if (actual.getKey().endsWith("_COMMENT")) {
                        // 処理なし
                        
                    } else if (!actual.getKey().equals("TAG_ED(" + workProperty.getWorkPropTag() + ")")) {
                        if (traceSetting.containsKey(TraceOptionTypeEnum.VALUE_LIST.name())) {
                            // 色付き入力値リスト
                            TraceOptionEntity option = traceSetting.getTraceOption(TraceOptionTypeEnum.VALUE_LIST.name());
                            inputList = option.getValues();
                        } else if (traceSetting.containsKey(TraceOptionTypeEnum.COLOR_VALUE_LIST.name())) {
                            // 色付きリスト
                            TraceOptionEntity option = traceSetting.getTraceOption(TraceOptionTypeEnum.COLOR_VALUE_LIST.name());
                            inputList = option.getColorTextBkValues().stream().map(value -> value.getText()).collect(Collectors.toList());
                        }                        
                    }

                    TracebilityItem entity = new TracebilityItem(workProperty.getWorkPropName(),
                            workProperty.getWorkPropCategory(),
                            actual.getKey(),
                            actual.getValue().getEntity().getActualPropValue(),
                            actual.getValue().getActualId(),
                            inputList,
                            workProperty.getWorkPropOrder(),
                            true);

                    this.masterList.add(entity);

                } else {
                    // タグの未設定により、WorkPropertyInfoEntityを特定できない場合、不明な項目として表示する
                    TracebilityItem entity = new TracebilityItem(LocaleUtils.getString("key.UnknownItem"), 
                        WorkPropertyCategoryEnum.CUSTOM,
                        actual.getKey(),
                        actual.getValue().getEntity().getActualPropValue(),
                        actual.getValue().getActualId(),
                        null,
                        Integer.MAX_VALUE,
                        true);
                
                    this.masterList.add(entity);
                }
            }
            
            Collections.sort(this.masterList, (TracebilityItem a, TracebilityItem b) -> a.getOrder() - b.getOrder());            
            
            this.updateFilteredList();
            isCancel = true;
        
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
     * ダウンロードボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onDownloadButton(ActionEvent event) {
        this.download();
    }
    
    /**
     * ダウンロード処理
     */
    private void download() {
        logger.info("download start");
        try {
            TracebilityItem selectedItem = this.tracebilityList.getSelectionModel().getSelectedItem();
            Long kanbanId = this.kanban.getKanbanId();
            
            // ファイル保存ダイアログの初期値取得
            File initialDirectory = null;
            Properties props = AdProperty.getProperties(Constants.PROPERTY_NAME);
            String downloadDirectory = props.getProperty(Constants.TRACEABILITY_DOWNLOAD_DIRECTORY, null);
            if (Objects.nonNull(downloadDirectory)) {
                initialDirectory = new File(downloadDirectory);
            } else {
                initialDirectory = new File(System.getProperty("user.home"), "Desktop");
            }
            String fileName = FilenameUtils.getBaseName(selectedItem.getInputValue());
            String extName = FilenameUtils.getExtension(selectedItem.getInputValue());
            if (StringUtils.isEmpty(extName)) {
                extName = "*";
            }
            String extFormat = String.format("*.%s", extName);
            FileChooser.ExtensionFilter ext1 = new FileChooser.ExtensionFilter(String.format("Attach files (%s)", extFormat), extFormat);

            // ファイル保存ダイアログの初期値設定
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(initialDirectory);
            chooser.setInitialFileName(fileName);
            chooser.setTitle(LocaleUtils.getString("key.QualityData"));
            chooser.getExtensionFilters().add(ext1);

            // ファイル保存ダイアログ表示
            File file = chooser.showSaveDialog(sc.getWindow());
            if (Objects.isNull(file)) {
                return;
            }
            props.setProperty(Constants.TRACEABILITY_DOWNLOAD_DIRECTORY, file.getParent());
            
            Task task = new Task<Void>() {
                /**
                 * Taskが実行されるときに呼び出される。
                 * 
                 * <pre>
                 * ファイル保存ダイアログで指定したファイルに、ダウンロードしたデータを書き込む。
                 * </pre>
                 * 
                 * @throws Exception バックグラウンド操作中に発生した未処理の例外
                 */
                @Override
                protected Void call() throws Exception {
                    try {
                        Platform.runLater(() -> {
                            blockUI(true);
                        });
                        
                        // 添付ファイルデータ取得
                        ActualResultInfoFacade facade = new ActualResultInfoFacade();
                        byte[] fileData = facade.downloadFileData(kanbanId, selectedItem.getTag());
                        if (Objects.isNull(fileData)) {
                            Platform.runLater(() -> {
                                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.QualityData"), LocaleUtils.getString("key.FileNotExist"));
                            });
                            return null;
                        }

                        // 添付ファイルデータ書き込み
                        try (OutputStream out = new FileOutputStream(file)) {
                            out.write(fileData);
                            out.flush();
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        Platform.runLater(() -> {
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.QualityData"), LocaleUtils.getString("key.FileDownloadErrorOccured"));
                        });
                    } finally {
                        Platform.runLater(() -> {
                            blockUI(false);
                        });
                    }
                    return null;
                }
            };
            new Thread(task).start();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.QualityData"), LocaleUtils.getString("key.FileDownloadErrorOccured"));
        } finally {
            logger.info("download end");
        }
    }
    
    /**
     * 保存ボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onSaveButton(ActionEvent event) {
        this.save();
    }
    
    /**
     * 保存処理
     */
    private boolean save() {
        logger.info("save start");
        try {
            Map<Long, ActualResultEntity> updateMap = new TreeMap<>();
            for (TracebilityItem entity : this.masterList) {
                if (!entity.isChanged()) {
                    // 更新対象のみ処理
                    continue;
                }
                ActualResultEntity actual;
                if (updateMap.containsKey(entity.getActualId())) {
                    actual = updateMap.get(entity.getActualId());
                } else {
                    actual = this.actualMap.get(entity.getActualId());
                    updateMap.put(entity.getActualId(), actual);
                }
                // 工程実績の該当のタグを更新
                actual.setPropertyValue(entity.getTag(), entity.getInputValue());
            }
            
            for (ActualResultEntity update : updateMap.values()) {
                Long id = update.getActualId();
                String addInfo = JsonUtils.objectToJson(update.getPropertyCollection());

                Task task = new Task<ResponseEntity>() {
                    @Override
                    protected ResponseEntity call() throws Exception {
                        // 更新RESTに送る
                        return updateAddInfo(id, addInfo);
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        
                        if (!this.getValue().isSuccess()) {
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.QualityData"), LocaleUtils.getString("key.FaildToProcess"));
                            return;
                        }
                        
                        // 画面更新
                        updateView();
                    }

                    @Override
                    protected void failed() {
                        super.failed();
                        try {
                            if (Objects.nonNull(this.getException())) {
                                logger.fatal(this.getException(), this.getException());
                            }

                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.QualityData"), LocaleUtils.getString("key.FaildToProcess"));
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                        } finally {
                            blockUI(false);
                        }
                    }
                };
                new Thread(task).start();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        } finally {
            logger.info("save end");
        }
        return true;
    }
    /**
     * キャンセルボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
        this.cancelDialog(event);
    }

    /**
     * キャンセル処理
     */
    private void cancelDialog(Event event) {
        try {
            if (this.destoryComponent()) {
                this.dialog.setResult(ButtonType.CANCEL);
                this.dialog.close();
            } else {
                event.consume();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 画面破棄時に内容に変更がないか調べて変更があるなら保存する
     *
     * @return 保存に成功したとき、または変更が存在しなかった場合true<br>ダイアログでキャンセルが押された場合false
     */
    public boolean destoryComponent() {
        
        // 更新がないかチェック
        boolean updateFlg = false;
        for (TracebilityItem entity : this.masterList) {
            if (entity.isChanged()) {
                updateFlg = true;
                break;
            }
        }
        
        if (updateFlg) {
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            if (ButtonType.YES == buttonType) {
                return this.save();
            } else if (ButtonType.CANCEL == buttonType) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * インジケーターを表示して、画面操作を禁止する。
     *
     * @param block true:ロック、false:ロック解除
     */
    private void blockUI(Boolean block) {
        this.pane.setDisable(block);
        this.progress.setVisible(block);
    }
    
    /**
     * 指定した工程カンバンの実績を取得する。
     *
     * @param workKanbanId 工程カンバンID
     * @return 実績
     */
    private List<ActualResultEntity> getActualResults(Long workKanbanId) {
        try {
            List<ActualResultEntity> entities = new ArrayList<>();
            ActualResultInfoFacade facade = new ActualResultInfoFacade();
            ActualSearchCondition condition = new ActualSearchCondition().workKanbanList(Arrays.asList(workKanbanId));

            Long actualMax = facade.searchCount(condition);
            for (long nowCnt = 0; nowCnt <= actualMax; nowCnt += RANGE) {
                entities.addAll(facade.searchRange(condition, nowCnt, nowCnt + RANGE - 1));
            }
            return entities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }
    
    /**
     * 工程IDで工程を取得する
     *
     * @param kanbanId 工程順ID
     * @return 工程
     */
    private WorkInfoEntity getWorkInfo(Long workId, boolean withDevice) {
        try {
            // 工程を取得
            WorkInfoFacade facade = new WorkInfoFacade();
            WorkInfoEntity entity = facade.find(workId, withDevice);
            return entity;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }
    
    /**
     * 工程実績IDで品質データを更新する
     *
     * @param id 工程実績ID
     * @param addInfo 品質データ
     * 
     * @return 
     */
    private ResponseEntity updateAddInfo(Long id, String addInfo) {
        ResponseEntity res = null;
        try {
            ActualResultInfoFacade facade = new ActualResultInfoFacade();
            res = facade.updateAddInfo(id, addInfo);
            return res;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return res;
        }
    }
    
    /**
     * 工程プロパティ一覧を取得する。
     *
     * @return 工程プロパティ一覧
     */
    private List<WorkPropertyInfoEntity> getProperties(WorkInfoEntity entity) {
        // リストがnullの場合、追加情報のJSON文字列を工程プロパティ一覧に変換してセットする。
        if (Objects.isNull(entity.getPropertyInfoCollection())) {
            entity.setPropertyInfoCollection(JsonUtils.jsonToObjects(entity.getWorkAddInfo(), WorkPropertyInfoEntity[].class));

            List<WorkPropertyInfoEntity> checkInfos = JsonUtils.jsonToObjects(entity.getWorkCheckInfo(), WorkPropertyInfoEntity[].class);
            if (!checkInfos.isEmpty()) {
                entity.getPropertyInfoCollection().addAll(checkInfos);
            }
            
            // ソート
            entity.getPropertyInfoCollection().sort(Comparator.comparing(o -> o.getWorkPropOrder()));
        }
        return entity.getPropertyInfoCollection();
    }
    
    /**
     * 工程プロパティからトレース設定を取得する。
     *
     * @param workProperty 工程プロパティ
     * @return トレース設定
     */
    private TraceSettingEntity getTraceSetting(WorkPropertyInfoEntity workProperty) {
        TraceSettingEntity traceSetting = new TraceSettingEntity();
        if (Objects.nonNull(workProperty) && !StringUtils.isEmpty(workProperty.getWorkPropOption())) {
            try {
                traceSetting = (TraceSettingEntity) XmlSerializer.deserialize(TraceSettingEntity.class, workProperty.getWorkPropOption());
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
        return traceSetting;
    }
    
    /**
     * 設備名リストを作成
     *
     * @param src 検索元
     * @param equipmentIdentifyList この設備(階層)リストの親設備を子設備に置換
     * @return 親設備を子設備に置換した設備名リスト
     */
    private List<String> createEquipmentList(List<EquipmentInfoEntity> src, List<String> equipmentIdentifyList) {
        List<String> ret = new ArrayList();
        
        // 管理番号に設定されている設備名一覧の設備エンティティを取得
        List<EquipmentInfoEntity> parents = new ArrayList();
        for (String equipIdentify : equipmentIdentifyList) {
            for (EquipmentInfoEntity equip : src) {
                if (Objects.equals(equip.getEquipmentIdentify(), equipIdentify)
                        && !parents.contains(equip)) {
                    parents.add(equip);
                    break;
                }
            }
        }

        if (parents.isEmpty()) {
            return equipmentIdentifyList;
        }

        // 子設備の検索
        for (EquipmentInfoEntity equip : parents) {
            boolean parentFlg = false;
            for (EquipmentInfoEntity child : src) {
                // 孫設備のある設備は追加しない。
                if (child.getChildCount() > 0) {
                    continue;
                }

                if (Objects.equals(child.getParentId(), equip.getEquipmentId())
                        && !ret.contains(child.getEquipmentIdentify())) {
                    // 子設備があれば子設備を追加
                    ret.add(child.getEquipmentIdentify());
                    parentFlg = true;
                }
            }
            // 子設備がなければそのままリストに追加
            if (!parentFlg && !ret.contains(equip.getEquipmentIdentify())) {
                ret.add(equip.getEquipmentIdentify());
            }
        }
        
        Collections.sort(ret);
        return ret;
    }
}
