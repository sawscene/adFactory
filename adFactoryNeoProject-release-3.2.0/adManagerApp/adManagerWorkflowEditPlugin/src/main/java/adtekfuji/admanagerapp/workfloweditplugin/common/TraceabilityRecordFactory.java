/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.common;

import adtekfuji.admanagerapp.workfloweditplugin.property.CellInputRule;
import adtekfuji.admanagerapp.workfloweditplugin.property.CellOption;
import adtekfuji.admanagerapp.workfloweditplugin.property.CellTolerance;
import adtekfuji.admanagerapp.workfloweditplugin.property.CellTraceabilityComboBox;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.util.*;
import java.util.function.Supplier;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.object.ObjectInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.adFactory.utility.DataFormatUtil;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellAutoResizeTextArea;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.CellTimeStampField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * トレーサビリティ情報ファクトリークラス
 *
 * @author s-heya
 */
public class TraceabilityRecordFactory extends AbstractRecordFactory<WorkPropertyInfoEntity> {

    /**
     * 種別セル
     */
    class CategoryCellFactory extends ListCell<WorkPropertyCategoryEnum> {

        @Override
        protected void updateItem(WorkPropertyCategoryEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                this.setText("");
            } else {
                this.setText(WorkPropertyCategoryEnum.getName(rb, item));
            }
        }
    }

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final ObservableList<ObjectInfoEntity> useParts;
    private final List<EquipmentInfoEntity> manufactureEquipments;
    private final List<EquipmentInfoEntity> measureEquipments;
    static private final SimpleBooleanProperty isPasteDisable = new SimpleBooleanProperty(true);

    /**
     * 入力項目の有効/無効状態(true：無効、false：有効)
     */
    private final boolean isItemDisabled;
    
    /**
     * 承認機能オプションが有効か
     */
    private final boolean isLicensedApproval = ClientServiceProperty.isLicensed(LicenseOptionType.ApprovalOption.getName());
    private final Logger logger = LogManager.getLogger();
    private WorkInfoEntity workInfo;

    /**
     * コンストラクタ
     *
     * @param table
     * @param list
     * @param useParts
     * @param manufactureEquipments
     * @param measureEquipments
     * @param isItemDisabled 入力項目の有効/無効状態(true：無効、false：有効)
     */
    public TraceabilityRecordFactory(Table table, LinkedList<WorkPropertyInfoEntity> list, ObservableList<ObjectInfoEntity> useParts, List<EquipmentInfoEntity> manufactureEquipments, List<EquipmentInfoEntity> measureEquipments, boolean isItemDisabled) {
        super(table, list);
        this.useParts = useParts;
        this.manufactureEquipments = manufactureEquipments;
        this.measureEquipments = measureEquipments;
        this.isItemDisabled = isItemDisabled;
    }
    
    /**
     * コンストラクタ(Overloading)
     *
     * @param table
     * @param list
     * @param useParts
     * @param manufactureEquipments
     * @param measureEquipments
     * @param isItemDisabled 入力項目の有効/無効状態(true：無効、false：有効)
     * @param workInfo 工程情報
     */
    public TraceabilityRecordFactory(Table table, LinkedList<WorkPropertyInfoEntity> list, ObservableList<ObjectInfoEntity> useParts, List<EquipmentInfoEntity> manufactureEquipments, List<EquipmentInfoEntity> measureEquipments, boolean isItemDisabled, WorkInfoEntity workInfo) {
        super(table, list);
        this.useParts = useParts;
        this.manufactureEquipments = manufactureEquipments;
        this.measureEquipments = measureEquipments;
        this.isItemDisabled = isItemDisabled;
        this.workInfo = workInfo;
    }

    /**
     * タイトルを生成する
     *
     * @return
     */
    @Override
    protected Record createCulomunTitleRecord() {
        Record record = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();

        // 種別
        cells.add(new CellLabel(record, LocaleUtils.getString("key.Category") + LocaleUtils.getString("key.RequiredMark")).setPrefWidth(120.0).addStyleClass("ResizeColumn").setResize(true));
        // 項目名
        cells.add(new CellLabel(record, LocaleUtils.getString("key.PropertyName") + LocaleUtils.getString("key.RequiredMark")).setPrefWidth(200.0).addStyleClass("ResizeColumn").setResize(true));
        // 値
        cells.add(new CellLabel(record, LocaleUtils.getString("key.PropertyContent")).setPrefWidth(200.0).addStyleClass("ResizeColumn").setResize(true));
        // 基準値
        cells.add(new CellLabel(record, LocaleUtils.getString("key.Tolerance")).setPrefWidth(200.0).addStyleClass("ResizeColumn").setResize(true));
        // 入力規則
        cells.add(new CellLabel(record, LocaleUtils.getString("key.ValidationRule")).setPrefWidth(180.0).addStyleClass("ResizeColumn").setResize(true));
        // 付加情報
        cells.add(new CellLabel(record, LocaleUtils.getString("key.Option")).setPrefWidth(200.0).addStyleClass("ResizeColumn").setResize(true));
        // チェックポイント
        cells.add(new CellLabel(record, LocaleUtils.getString("key.Checkpoint")).setPrefWidth(120.0).addStyleClass("ResizeColumn").setResize(true));
        // タグ
        cells.add(new CellLabel(record, LocaleUtils.getString("key.Tag")).setPrefWidth(200.0).addStyleClass("ResizeColumn").setResize(true));

        record.setTitleCells(cells);

        return record;
    }

    /**
     * 編集行を生成する
     *
     * @param entity
     * @return
     */
    @Override
    protected Record createRecord(WorkPropertyInfoEntity entity) {
        Record record = new Record(super.getTable(), true);
        record.setEditableOrder(!this.isItemDisabled);
        record.setIsRemoveRecord(!this.isItemDisabled);

        LinkedList<AbstractCell> cells = new LinkedList<>();

        // 種別
        Callback<ListView<WorkPropertyCategoryEnum>, ListCell<WorkPropertyCategoryEnum>> comboCellFactory = (ListView<WorkPropertyCategoryEnum> param) -> new CategoryCellFactory();
        List<WorkPropertyCategoryEnum> categories = Arrays.asList(WorkPropertyCategoryEnum.values());
        cells.add(new CellComboBox<>(record, categories, new CategoryCellFactory(), comboCellFactory, entity.workPropCategoryProperty(), this.isItemDisabled).setPrefWidth(120.0).addStyleClass("ContentComboBox"));

        // 項目名
        AbstractCell nameCell = new CellAutoResizeTextArea(record, entity.workPropNameProperty(), this.isItemDisabled).setLinesLimit(4).setPrefWidth(200.0).addStyleClass("ContentTextBox");
        cells.add(nameCell);

        // 値
        cells.add(new CellTraceabilityComboBox(record, entity, this.useParts, this.manufactureEquipments, this.measureEquipments, rb, this.isItemDisabled).setPrefWidth(200.0).addStyleClass("ContentComboBox"));

        // 基準値
        cells.add(new CellTolerance(record, entity, this.isItemDisabled).setPrefWidth(200.0).addStyleClass("ContentTextBox"));

        // 入力規則
        cells.add(new CellInputRule(record, entity, this.isItemDisabled).setPrefWidth(180.0).addStyleClass("ContentTextBox"));

        // オプション
        cells.add(new CellOption(record, entity, rb).setPrefWidth(200.0).addStyleClass("ContentTextBox"));

        // チェックポイント
        cells.add(new CellTimeStampField(record, entity.workPropCheckpointProperty(), this.isItemDisabled).setPrefWidth(120.0).addStyleClass("ContentTextBox"));

        // タグ
        cells.add(new CellTextField(record, entity.workPropTagProperty(), this.isItemDisabled).setPrefWidth(200.0).addStyleClass("ContentTextBox"));
        
        // コンテキストメニューのメニュー設定を行う
        Optional<ContextMenu> contextMenu = createContextMenu(record);
        record.setContextMenu(contextMenu.orElse(null));
        record.setCells(cells);

        try {
            boolean suggest = Boolean.valueOf(AdProperty.getProperties().getProperty("suggestTagName", "false"));
            if (suggest) {
                // 項目名にフォーカスイベントを設定する。
                nameCell.getNode().focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    // フォーカスが外れた時、項目名が入力されていてタグが未入力の場合、タグに「tag_<項目名の1行目>」をセットする。
                    if (!newValue
                            && Objects.nonNull(entity)
                            && !StringUtils.isEmpty(entity.getWorkPropName())
                            && StringUtils.isEmpty(entity.getWorkPropTag())) {
                        String tag = new StringBuilder("tag_")
                                .append(entity.getWorkPropName().split("\n")[0])
                                .toString();
                        entity.setWorkPropTag(tag);
                    }
                });
            }
        } catch (Exception ex) {
            LogManager.getLogger().warn(ex, ex);
        }

        return record;
    }

    /**
     * クラス情報を取得する
     *
     * @return
     */
    @Override
    public Class getEntityClass() {
        return WorkPropertyInfoEntity.class;
    }

    /**
     * コンテキストメニューのメニュー設定を取得
     *
     * @param record 対象行のRecordクラス
     * @return Lコンテキストメニューのメニュー設定(未設定の場合は0件)
     */
    @Override
    public Optional<ContextMenu> createContextMenu(Record record) {

        // メニュー内容の設定
        List<MenuItem> menuItems = new ArrayList<>();
        if (Objects.nonNull(record)) {
            // コピー設定
            MenuItem copyMenu = new MenuItem(LocaleUtils.getString("key.Copy"));
            copyMenu.setOnAction((ActionEvent) -> {
                final WorkPropertyInfoEntity entity = this.getEntity(record).clone();
                this.copyToClipBord(entity);
            });
            copyMenu.setDisable(false);
            menuItems.add(copyMenu);
        }

        // 貼付設定
        final Supplier<Integer> addIndex = Objects.nonNull(record)
                ? () -> this.getRowIndex(record)
                : this::getRecodeNum;

        MenuItem pastMenu = new MenuItem(LocaleUtils.getString("key.Pasting"));
        pastMenu.setOnAction((ActionEvent) -> {
            this.getEntityFromClipBoard()
                    .ifPresent(entity -> this.insert(addIndex.get(), entity));
        });
        pastMenu.disableProperty().bind(isPasteDisable);
        menuItems.add(pastMenu);

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(menuItems);
        contextMenu.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        contextMenu.setOnShown(event -> isPasteDisable.set(this.isPastMenuDisable()));

        return Optional.of(contextMenu);
    }

    /**
     * コンテキストメニューのコピー処理
     *
     * @param record 選択している品質トレーサビリティ設定
     * @param inserPasting コンテキストメニューの貼り付けに制御する
     */
    private boolean copyToClipBord(WorkPropertyInfoEntity entity) {
        try {
            logger.info("onTabInfoCopy:Start");

            // クリップボードにデータを保存
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            DataFormat dataFormat = DataFormatUtil.getDataFormat(WorkPropertyInfoEntity.class);
            content.put(dataFormat, entity);
            return clipboard.setContent(content);
        } finally {
            logger.info("onTabInfoCopy:end");
        }
    }

    /**
     * クリップボードからエンティティを取得
     *
     * @return 取得したエンティティ
     */
    private Optional<WorkPropertyInfoEntity> getEntityFromClipBoard() {
        try {
            logger.info("onTabInfoPasting:Start");
            Clipboard clipboard = Clipboard.getSystemClipboard();
            DataFormat dataFormat = DataFormatUtil.getDataFormat(WorkPropertyInfoEntity.class);

            // クリップボードにシートのデータ以外が保存されちる場合は処理を中断
            if (!clipboard.hasContent(dataFormat)) {
                logger.info("false : onTabInfoPasting");
                return Optional.empty();
            }

            // クリップボードからデータを取得
            return Optional.ofNullable((WorkPropertyInfoEntity) clipboard.getContent(dataFormat));
        } finally {
            logger.info("onTabInfoPasting:end");
        }
    }

    /**
     * エンティティを指定位置へ挿入
     *
     * @param addIndex 挿入する位置
     * @param entity 挿入するエンティティ
     * @return ture:成功 / false:失敗
     */
    private boolean insert(int addIndex, WorkPropertyInfoEntity entity) {
        if (Objects.isNull(entity)) {
            return true;
        }

        // 取得したデータをメンバー変数の最後に保存
        Record newRecord = this.createRecord(entity);
        if (Objects.isNull(newRecord)) {
            return true;
        }

        getEntities().add(addIndex, entity);
        getRecords().add(addIndex, newRecord);

        // 画面表示処理
        super.getTable().insertRecord(newRecord, addIndex);
        return false;
    }

    /**
     * コンテキストメニュー表示状態を設定する。
     *
     */
    private boolean isPastMenuDisable() {

        if(ApprovalStatusEnum.APPLY.equals(workInfo.getApprovalState()) // 申請中
                || ApprovalStatusEnum.APPROVE.equals(workInfo.getApprovalState())) { // 承認済み
            return true;
        }

        //システム・クリップボードを取得
        Clipboard clipboard = Clipboard.getSystemClipboard();
        DataFormat dataFormat = DataFormatUtil.getDataFormat(WorkPropertyInfoEntity.class);

        // クリップボードにコピー済みか?
        final boolean isCopied = !clipboard.hasContent(dataFormat);

        // 承認機能オプションが無効
        if (!this.isLicensedApproval) {
            return isCopied;
        }

        // 承認機能オプションが有効
        if (Objects.isNull(this.workInfo) || Objects.isNull(this.workInfo.getApprovalState())) {
            return isCopied;
        }

        switch (this.workInfo.getApprovalState()) {
            case UNAPPROVED: //未承認
            case CANCEL_APPLY: // 取り消し
            case REJECT: // 却下
                return isCopied;
            case APPLY: // 申請中
            case FINAL_APPROVE: // 最終承認済み
            case APPROVE: // 祖容認済み(承認フローで使用)
            default: // 在りえない
                return true;
        }
    }


}
