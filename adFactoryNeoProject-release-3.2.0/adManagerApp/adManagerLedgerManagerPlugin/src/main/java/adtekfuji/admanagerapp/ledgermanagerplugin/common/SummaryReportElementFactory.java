/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.common;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.clientservice.ClientServiceProperty;
import java.util.*;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.object.ObjectInfoEntity;
import jp.adtekfuji.adFactory.entity.summaryreport.SummaryReportConfigElementEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CategoryEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.property.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jp.adtekfuji.javafxcommon.property.Record;

import static jp.adtekfuji.javafxcommon.enumeration.Verifier.DECIMAL_NUMBER_ONLY;

/**
 * メール内容設定のファクトリークラス
 *
 * @author s-heya
 */
public class SummaryReportElementFactory extends AbstractRecordFactory<SummaryReportConfigElementEntity> {

    /**
     * 種別セル
     */
    class CategoryCellFactory extends ListCell<CategoryEnum> {

        @Override
        protected void updateItem(CategoryEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                this.setText("");
            } else {
                this.setText(LocaleUtils.getString(item.getValue()));
            }
        }
    }
   
    /**
     * 承認機能オプションが有効か
     */
    private final boolean isLicensedApproval = ClientServiceProperty.isLicensed(LicenseOptionType.ApprovalOption.getName());
    private final Logger logger = LogManager.getLogger();
    private WorkInfoEntity workInfo;

    /**
     * コンストラクタ
     *
     * @param table メール内容設定
     * @param list 紐付けるエンティティ
     */
    public SummaryReportElementFactory(Table<SummaryReportConfigElementEntity> table, LinkedList<SummaryReportConfigElementEntity> list){
        super(table, list);
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
    public SummaryReportElementFactory(Table table, LinkedList<SummaryReportConfigElementEntity> list, ObservableList<ObjectInfoEntity> useParts, List<EquipmentInfoEntity> manufactureEquipments, List<EquipmentInfoEntity> measureEquipments, boolean isItemDisabled, WorkInfoEntity workInfo) {
        super(table, list);
    }

    /**
     * タイトルを生成する
     *
     * @return タイトルをセットしたRecord
     */
    @Override
    protected Record createCulomunTitleRecord() {
        Record record = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();

        // 種別
        cells.add(new CellLabel(record, LocaleUtils.getString("key.Category") + LocaleUtils.getString("key.RequiredMark")).setPrefWidth(60.0).addStyleClass("ContentTitleLabel"));
        // 基準値
        cells.add(new CellLabel(record, LocaleUtils.getString("key.Tolerance")).addStyleClass("ContentTitleLabel"));
        // 閾値
        cells.add(new CellLabel(record, LocaleUtils.getString("key.Threshold")).addStyleClass("ContentTitleLabel"));
        // 異常色
        cells.add(new CellLabel(record, new SimpleStringProperty(LocaleUtils.getString("key.AbnormallyColored"))).addStyleClass("ContentTitleLabel"));

        record.setTitleCells(cells);

        return record;
    }

    /**
     * 目標値、閾値プロパティへ設定関数
     */
    private final static Consumer<Node> nodeConsumer = (node) -> {
        if (!(node instanceof RestrictedTextField)) {
            return;
        }
        RestrictedTextField textField = (RestrictedTextField) node;
        textField.setVerifier(DECIMAL_NUMBER_ONLY);
        textField.setMaxLimit(99999.9);
        textField.setMinLimit(-99999.9);
        textField.formatProperty().set("%.1f");
    };

    /**
     * 編集行を生成する
     *
     * @param entity 紐づけるエンティティ
     * @return 編集行をセットしたRecord
     */
    @Override
    protected Record createRecord(SummaryReportConfigElementEntity entity) {
        Record record = new Record(super.getTable(), true);
        record.setEditableOrder(true);

        LinkedList<AbstractCell> cells = new LinkedList<>();

        // 種別
        cells.add(
                new CellComboBox<>(record,
                        Arrays.asList(CategoryEnum.values()),
                        new CategoryCellFactory(),
                        (ListView<CategoryEnum> param) -> new CategoryCellFactory(),
                        entity.sendElementTypeProperty(),
                        false
                ).addStyleClass("ContentComboBox"));

        final BooleanBinding disableProperty = Bindings.equal(CategoryEnum.IN_PROCESS_WORK_VARIATION, entity.sendElementTypeProperty())
                .or(Bindings.equal(CategoryEnum.VARIATION_AMONG_WORKERS, entity.sendElementTypeProperty()))
                .or(Bindings.equal(CategoryEnum.VARIATION_IN_EQUIPMENT_COMPLETION, entity.sendElementTypeProperty()))
                .or(Bindings.equal(CategoryEnum.LINE_BALANCE, entity.sendElementTypeProperty()))
                .or(Bindings.equal(CategoryEnum.INTER_PROCESS_WAITING_TIME, entity.sendElementTypeProperty()))
                .or(Bindings.equal(CategoryEnum.DELAY_RANKING, entity.sendElementTypeProperty()))
                .or(Bindings.equal(CategoryEnum.INTERRUPT_RANKING, entity.sendElementTypeProperty()))
                .or(Bindings.equal(CategoryEnum.CALL_RANKING, entity.sendElementTypeProperty()));

        // 基準値
        AbstractCell cellTextField =
                new CellTextField(record, entity.targetValueProperty())
                        .setPrefWidth(120.0)
                        .addStyleClass("ContentTextBox")
                        .setNodeConsumer(nodeConsumer);
        cellTextField.getNode().disableProperty().bind(disableProperty);
        cells.add(cellTextField);


        // 閾値
        AbstractCell cellThresholdTextField =
                new CellTextField(record, entity.thresholdProperty())
                        .setPrefWidth(120.0)
                        .addStyleClass("ContentTextBox")
                        .setNodeConsumer(nodeConsumer);
        cellThresholdTextField.getNode().disableProperty().bind(disableProperty);
        cells.add(cellThresholdTextField);

        // 背景色
        AbstractCell cellColorPicker = new CellColorPicker(record, entity.warningBackColorProperty()).addStyleClass("ContentTextBox");
        cellColorPicker.getNode().disableProperty().bind(disableProperty);
        cells.add(cellColorPicker);
        
        // コンテキストメニューのメニュー設定を行う
        Optional<ContextMenu> contextMenu = createContextMenu(record);
        record.setContextMenu(contextMenu.orElse(null));
        record.setCells(cells);

        return record;
    }

    /**
     * クラス情報を取得する
     *
     * @return 自身のClass オブジェクト
     */
    @Override
    public Class getEntityClass() {
        return SummaryReportConfigElementEntity.class;
    }

}
