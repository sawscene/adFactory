/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.component.lite;

import adtekfuji.admanagerapp.andonsetting.component.*;
import adtekfuji.admanagerapp.andonsetting.dialog.KanbanHierarchySelectDialog;
import adtekfuji.admanagerapp.andonsetting.dialog.KanbanSelectDialog;
import adtekfuji.admanagerapp.andonsetting.dialog.LineSelectDialog;
import adtekfuji.admanagerapp.andonsetting.dialog.OrganizationSelectDialog;
import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.andon.enumerate.*;
import jp.adtekfuji.andon.property.AgendaMonitorSetting;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.controls.SwitchButton;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 * アジェンダモニター設定画面
 *
 * @author kenji.yokoi
 */
public class LiteMonitorSettingController extends AndonSettingController {

    final int MIN_MONITOR_NUMBER = 1;
    final int MAX_MONITOR_NUMBER = 5;

    @FXML
    private ComboBox<DisplayModeEnum> targetLine;
    @FXML
    private Button selectTarget;
    @FXML
    private RestrictedTextField updateInterval;
    @FXML
    private Label modelNameLabel;
    @FXML
    private TextField modelNameField;

    // 表示設定
    @FXML
    private ComboBox<Integer> displayNumber;
    @FXML
    private SwitchButton fullScrren;
    @FXML
    private ComboBox<ContentTypeEnum> contentType;
    @FXML
    private SwitchButton togglePages;
    @FXML
    private TextField togglePageTime;
    @FXML
    private TextField processDisplayColumns;
    @FXML
    private TextField processDisplayRows;
    @FXML
    private TextField processPageSwitchingInterval;
    @FXML
    private TextField callSoundSetting;

    @FXML
    private TextField titleText;
    @FXML
    private TextField columnText;
    @FXML
    private TextField itemText;

    private final long monitorId;
    private final AgendaMonitorSetting agenda;

    // 親ウィンドウ
    private Stage parent;

    /**
     * コンストラクタ
     *
     * @param settingController
     * @param monitorId
     * @param setting
     * @param parent
     */
    public LiteMonitorSettingController(AndonSettingCompoFxController settingController, long monitorId, AgendaMonitorSetting setting, Stage parent) {

        super.setComponentController(settingController);

        this.monitorId = monitorId;
        this.agenda = setting;
        this.parent = Objects.isNull(parent) ? sc.getStage() : parent;

        URL url = getClass().getResource("/fxml/admanagerapp/andonsetting/lite/lite_monitor_setting.fxml");
        FXMLLoader loader = new FXMLLoader(url, rb);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();

            // 各コントロールの初期設定
            this.initControl();

            // 表示条件
            this.targetLine.getSelectionModel().select(this.agenda.getMode());
            this.targetLine.getSelectionModel().select(Objects.isNull(this.agenda.getMode()) ? DisplayModeEnum.LINE : this.agenda.getMode());
            this.selectTarget.setText(getTargetName(this.agenda));

            // 表示対象
            this.agenda.modeProperty().bind(this.targetLine.getSelectionModel().selectedItemProperty());
            // 更新間隔(分)
            this.updateInterval.textProperty().bindBidirectional(this.agenda.updateIntervalProperty(), new NumberStringConverter());
            // モデル名
            this.modelNameField.textProperty().bindBidirectional(this.agenda.modelNameProperty());

            // 画面設定

            // (縦軸)ディスプレイ番号
            this.displayNumber.getSelectionModel().select(Objects.isNull(this.agenda.getDisplayNumber()) ? 0 : this.agenda.getDisplayNumber() - 1);
            this.agenda.displayNumberProperty().bind(this.displayNumber.getSelectionModel().selectedItemProperty());
            // (縦軸)フルスクリーン表示
            Bindings.bindBidirectional(this.fullScrren.switchOnProperty(), this.agenda.fullScreenProperty());
            // (縦軸)詳細表示
            this.contentType.getSelectionModel().select(Objects.isNull(this.agenda.getContent()) ? ContentTypeEnum.WORKFLOW_NAME : this.agenda.getContent());
            this.agenda.contentProperty().bind(this.contentType.getSelectionModel().selectedItemProperty());
            // (縦軸)ページ切り替え
            Bindings.bindBidirectional(this.togglePages.switchOnProperty(), this.agenda.togglePagesProperty());
            // (縦軸)ページ切り替え間隔(秒)
            Bindings.bindBidirectional(this.togglePageTime.textProperty(), this.agenda.pageToggleTimeProperty(), new NumberStringConverter());
            // 表示列数
            Bindings.bindBidirectional(this.processDisplayColumns.textProperty(), this.agenda.processDisplayColumnsProperty(), new NumberStringConverter());
            // 表示行数
            Bindings.bindBidirectional(this.processDisplayRows.textProperty(), this.agenda.processDisplayRowsProperty(), new NumberStringConverter());
            // 工程ページ切替間隔
            Bindings.bindBidirectional(this.processPageSwitchingInterval.textProperty(), this.agenda.processPageSwitchingIntervalProperty(), new NumberStringConverter());
            // 呼出音設定
            this.callSoundSetting.textProperty().bindBidirectional(this.agenda.callSoundSettingProperty());

            // フォントサイズ

            // タイトルのフォントサイズ
            Bindings.bindBidirectional(this.titleText.textProperty(), this.agenda.titleSizeProperty(), new NumberStringConverter());
            // カラムのフォントサイズ
            Bindings.bindBidirectional(this.columnText.textProperty(), this.agenda.columnSizeProperty(), new NumberStringConverter());
            // アイテムのフォントサイズ
            Bindings.bindBidirectional(this.itemText.textProperty(), this.agenda.itemSizeProperty(), new NumberStringConverter());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 設定内容を取得する。
     *
     * @return
     */
    @Override
    public AndonMonitorLineProductSetting getInputResult() {
        logger.info("getAndonSetting: " + this.agenda);

        AndonMonitorLineProductSetting result = AndonMonitorLineProductSetting.create();

        result.setMonitorType(AndonMonitorTypeEnum.LITE_MONITOR);
        result.setMonitorId(this.monitorId);
        result.setAgendaMonitorSetting(this.agenda);

        return result;
    }

    /**
     * 入力した項目が有効なものか調べる。
     *
     * @return 全て正常に入力された場合true
     */
    @Override
    public boolean isValidItems() {
        return true;
    }

    /**
     * 表示対象選択ダイアログの表示 表示対象コンボボックスによって表示するダイアログを選択し表示する
     *
     * @param event
     */
    @FXML
    public void onSelectLine(Event event) {
        Button eventSrc = (Button) event.getSource();

        ButtonType ret;
        switch (this.targetLine.getSelectionModel().selectedItemProperty().getValue()) {
            case KANBAN:
                LocalDate now = (new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Instant instant = now.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                Date date = Date.from(instant);

                List<Long> kanbanIds = this.agenda.getLiteKanbanIds();
                eventSrc.setUserData(kanbanIds);

                ret = KanbanSelectDialog.showDialog((ActionEvent) event, date, null); // すべてのカンバンを選択可
                if (ret.equals(ButtonType.OK)) {
                    List<KanbanInfoEntity> recv = (List) eventSrc.getUserData();

                    this.agenda.setLiteKanbanIds(recv.stream().map(KanbanInfoEntity::getKanbanId).collect(Collectors.toList()));
                    eventSrc.setText(getTargetName(recv.stream().map(KanbanInfoEntity::getKanbanName).collect(Collectors.toList())));
                }
                break;
            case LINE:
                List<Long> lineIds = this.agenda.getLineIds();
                eventSrc.setUserData(lineIds);

                ret = LineSelectDialog.showDialogLineEntity(event);
                if (ret.equals(ButtonType.OK)) {
                    List<EquipmentInfoEntity> recv = (List) eventSrc.getUserData();

                    this.agenda.setLineIds(recv.stream().map(EquipmentInfoEntity::getEquipmentId).collect(Collectors.toList()));
                    eventSrc.setText(getTargetName(recv.stream().map(EquipmentInfoEntity::getEquipmentName).collect(Collectors.toList())));
                }
                break;
            case WORKER:
                List<Long> orhanizationIds = this.agenda.getOrganizationIds();
                eventSrc.setUserData(orhanizationIds);

                ret = OrganizationSelectDialog.showDialog((ActionEvent) event);
                if (ret.equals(ButtonType.OK)) {
                    List<OrganizationInfoEntity> recv = (List) eventSrc.getUserData();

                    this.agenda.setOrganizationIds(recv.stream().map(OrganizationInfoEntity::getOrganizationId).collect(Collectors.toList()));
                    eventSrc.setText(getTargetName(recv.stream().map(OrganizationInfoEntity::getOrganizationName).collect(Collectors.toList())));
                }
                break;
            case PRODUCT_PROGRESS:
                List<Long> kanbanHierarchyIds = this.agenda.getKanbanHierarchyIds();
                eventSrc.setUserData(kanbanHierarchyIds);
                ret = KanbanHierarchySelectDialog.showDialog((ActionEvent) event, true);
                if (ret.equals(ButtonType.OK)) {
                    List<KanbanHierarchyInfoEntity> recv = (List) eventSrc.getUserData();
                    this.agenda.setKanbanHierarchyIds(recv.stream().map(KanbanHierarchyInfoEntity::getKanbanHierarchyId).collect(Collectors.toList()));
                    eventSrc.setText(getTargetName(recv.stream().map(KanbanHierarchyInfoEntity::getHierarchyName).collect(Collectors.toList())));
                }

                break;
        }
    }

    /**
     * 各コントロールを初期化する
     */
    private void initControl() {
        // 表示条件
        this.targetLine.setItems(FXCollections.observableArrayList(DisplayModeEnum.values()));
        this.targetLine.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.isNull(oldValue)) {
                return;
            }

            if (oldValue != newValue) {
                this.selectTarget.setText(this.getTargetName(this.agenda));
            }
        });

        // モデル名は対象がラインの時のみ機能するためそれ以外では無効にしておく
        final BooleanBinding bb = Bindings.equal(DisplayModeEnum.LINE, this.targetLine.getSelectionModel().selectedItemProperty());
        this.modelNameField.managedProperty().bind(bb);
        this.modelNameLabel.managedProperty().bind(bb);
        this.modelNameField.visibleProperty().bind(bb);
        this.modelNameLabel.visibleProperty().bind(bb);

        this.targetLine.setConverter(new StringConverter<DisplayModeEnum>() {
            @Override
            public String toString(DisplayModeEnum object) {
                return rb.getString(object.getName());
            }

            @Override
            public DisplayModeEnum fromString(String string) {
                return null;
            }
        });

        // 製品進捗は非表示
        this.targetLine.getItems().remove(DisplayModeEnum.PRODUCT_PROGRESS);

        // 画面設定
        this.displayNumber.setItems(FXCollections.observableArrayList(IntStream.rangeClosed(MIN_MONITOR_NUMBER, MAX_MONITOR_NUMBER).boxed().collect(Collectors.toList())));
        this.contentType.setItems(FXCollections.observableArrayList(ContentTypeEnum.values()));
        this.contentType.setConverter(new StringConverter<ContentTypeEnum>() {
            @Override
            public String toString(ContentTypeEnum object) {
                return rb.getString(object.getName());
            }

            @Override
            public ContentTypeEnum fromString(String string) {
                return null;
            }
        });
    }

    /**
     * 表示対象に表示する項目を取得する
     *
     * @param setting
     * @return ,でつながれた項目
     */
    private String getTargetName(AgendaMonitorSetting setting) {

        switch (setting.getMode()) {
            case KANBAN: {
                List<KanbanInfoEntity> recv = KanbanSelectDialog.getKanbans(setting.getLiteKanbanIds());
                return getTargetName(recv.stream().map(KanbanInfoEntity::getKanbanName).collect(Collectors.toList()));
            }
            case LINE: {
                List<EquipmentInfoEntity> recv = LineSelectDialog.getLines(setting.getLineIds());
                return getTargetName(recv.stream().map(EquipmentInfoEntity::getEquipmentName).collect(Collectors.toList()));
            }
            case WORKER: {
                List<OrganizationInfoEntity> recv = CacheUtils.getCacheOrganization(setting.getOrganizationIds());
                return getTargetName(recv.stream().map(OrganizationInfoEntity::getOrganizationName).collect(Collectors.toList()));
            }
            case PRODUCT_PROGRESS:
                List<KanbanHierarchyInfoEntity> recv = CacheUtils.getCacheKanbanHierarchy(setting.getKanbanHierarchyIds());
                return getTargetName(recv.stream().map(KanbanHierarchyInfoEntity::getHierarchyName).collect(Collectors.toList()));
            default:
                return getTargetName(Collections.emptyList());
        }
    }

    /**
     * 空じゃないリストの名前を,でつないた文字を返す<br>
     * 空の場合「割り当てなし」を返す
     *
     * @param names
     * @return ,でつながれた項目
     */
    private String getTargetName(List<String> names) {
        if (names.isEmpty()) {
            return rb.getString("key.AndonLineSettingNotAllocate");
        }
        return names.stream().collect(Collectors.joining(", "));
    }

    /**
     * 呼出音を選択する
     *
     * @param event
     */
    @FXML
    public void onSelectMelody(Event event) {
        File defaultDir = new File(System.getProperty("user.home"), "Desktop");
        List<FileChooser.ExtensionFilter> filter = Arrays.asList(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"), new FileChooser.ExtensionFilter("All Files", "*.*"));

        Node node = (Node) event.getSource();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(defaultDir);
        fileChooser.setTitle(rb.getString("key.FileChoice"));
        fileChooser.getExtensionFilters().addAll(filter);
        File file = fileChooser.showOpenDialog(node.getScene().getWindow());
        if (Objects.nonNull(file)) {
            this.callSoundSetting.textProperty().set(file.getPath());
        }
    }

    /**
     * 呼出音を削除する
     *
     * @param event
     */
    @FXML
    public void onCancelMelody(Event event) {
        this.callSoundSetting.textProperty().set("");
    }
}
