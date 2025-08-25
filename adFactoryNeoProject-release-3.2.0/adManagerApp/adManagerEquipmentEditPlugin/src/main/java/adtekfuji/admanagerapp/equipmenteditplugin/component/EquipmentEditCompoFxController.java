/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.equipmenteditplugin.component;

import adtekfuji.admanagerapp.equipmenteditplugin.common.CalibrationSettingRecordFactory;
import adtekfuji.admanagerapp.equipmenteditplugin.common.EquipmentPropertyRecordFactory;
import adtekfuji.admanagerapp.equipmenteditplugin.common.EquipmentSettingRecordFactory;
import adtekfuji.admanagerapp.equipmenteditplugin.common.LocaleFileRecordFactory;
import adtekfuji.admanagerapp.equipmenteditplugin.utils.ExcelFileUtils;
import adtekfuji.clientservice.*;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.ThreadUtils;
import java.io.*;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentImportEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentSettingInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentSettingTemplateInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentTypeEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.ConfigInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.LocaleFileInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.resource.NotifySetStringProperty;
import jp.adtekfuji.adFactory.entity.resource.ResourceInfoEntity;
import jp.adtekfuji.adFactory.entity.search.EquipmentSearchCondition;
import jp.adtekfuji.adFactory.enumerate.*;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.DetailRecordFactory;
import jp.adtekfuji.javafxcommon.PropertyBindEntity;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.controls.ResettableTextField;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.AccessAuthSettingEntity;
import jp.adtekfuji.javafxcommon.treecell.EquipmentTreeCell;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author e-mori
 */
@FxComponent(id = "EquipmentEditCompo", fxmlPath = "/fxml/compo/equipment_edit_compo.fxml")
public class EquipmentEditCompoFxController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final EquipmentTypeInfoFacade equipmentTypeFacade = new EquipmentTypeInfoFacade();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final Map<String, PropertyBindEntity> detailProperties = new LinkedHashMap<>();
    private final Map<String, PropertyBindEntity> detailColorThemeProperties = new LinkedHashMap<>();
    private final LinkedList<EquipmentSettingInfoEntity> settingProperties = new LinkedList<>();
    private final LinkedList<EquipmentPropertyInfoEntity> customProperties = new LinkedList<>();
    private final Map<String, Property> calibProperties = new LinkedHashMap<>();

    private EquipmentInfoEntity viewEquipmentInfoEntity;
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final Map<Long, EquipmentTypeEntity> equipmentTypeCollection = new HashMap<>();
    private final OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();

    private final static long MAX_ROLL_HIERARCHY_CNT = 30;
    private final static long ROOT_ID = 0;
    private TreeItem<EquipmentInfoEntity> rootItem;

    //開いたときに表示された情報　比較用
    private EquipmentInfoEntity cloneInitialEquipment;
    private Long currentEquipmentType;//設備タイプは変更時に検出する

    //前回選択していたツリーの項目　名前の変更に使用
    private TreeItem<EquipmentInfoEntity> prevSelectedItem;

    private boolean isDisableEdit = false;// 編集ボタン無効フラグ (編集権限がない場合 true)

    private boolean isCancelMove = false;// 保存確認ダイアログで取消を選択した場合の、階層ツリー移動キャンセルフラグ

    private boolean isLicenseCount = false;// ライセンス数表示フラグ
    
    private final long REST_RANGE_NUM = ClientServiceProperty.getRestRangeNum();

    private static File defaultDir = new File(System.getProperty("user.home"), "Desktop");

    private long licenseTerminal = 0l; // ライセンス数
    
    /**
     * ツリーノード開閉イベントリスナー
     */
    private final ChangeListener expandedListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            // ノードを開いたら子ノードの情報を再取得して表示する。
            if (Objects.nonNull(newValue) && newValue.equals(true)) {
                TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
                expand(treeItem, null);
            }
        }
    };

    @FXML
    private SplitPane equipmentPane;
    @FXML
    private TreeView<EquipmentInfoEntity> hierarchyTree;
    @FXML
    private ToolBar hierarchyBtnArea;
    @FXML
    private Button delButton;
    @FXML
    private Button copyButton;
    @FXML
    private Button createButton;
    @FXML
    private Button authButton;
    @FXML
    private Button moveButton;
    @FXML
    public VBox localePane;
    @FXML
    private VBox detailPane;
    @FXML
    private VBox propertyPane;
    @FXML
    private VBox settingPane;
    @FXML
    private VBox calibPane;
    @FXML
    private Button calibButton;
    @FXML
    private Button registButton;
    @FXML
    private Pane Progress;

    @FXML
    private Button importButton;
    @FXML
    private Button exportButton;
    @FXML
    private Button licenseCountButton;
    @FXML
    private ResettableTextField searchField;

    private final boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption .getName());
    private final boolean isReporterOption = ClientServiceProperty.isLicensed(LicenseOptionType.ReporterOption.getName());

    /**
     * 設備編集コンポーネントを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        SplitPaneUtils.loadDividerPosition(equipmentPane, getClass().getSimpleName());

        // キャッシュする情報を取得する
        CacheUtils.createCacheData(OrganizationInfoEntity.class, true);

        // リソース編集権限がない場合、編集関連のボタンを無効化する。
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            isDisableEdit = true;
            delButton.setDisable(true);
            copyButton.setDisable(true);
            createButton.setDisable(true);
            moveButton.setDisable(true);
            authButton.setDisable(true);
            importButton.setDisable(true);
            exportButton.setDisable(true);
            licenseCountButton.setDisable(true);
            registButton.setDisable(true);
            calibButton.setDisable(true);
        }

        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.RIGHT_ACCESS)) {
            hierarchyBtnArea.getItems().remove(authButton);
            moveButton.setDisable(true);
        }

        // 階層ツリーのフォーカス移動イベント
        hierarchyTree.getFocusModel().focusedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (isCancelMove) {
                // 移動をキャンセルしたら、元の場所を選択状態にする。
                hierarchyTree.getSelectionModel().select(oldValue.intValue());
            }
        });

        // 階層ツリーのノード選択イベント
        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<EquipmentInfoEntity>> observable, TreeItem<EquipmentInfoEntity> oldValue, TreeItem<EquipmentInfoEntity> newValue) -> {
            if (isCancelMove) {
                // 移動キャンセル中は何もしない。
                isCancelMove = false;
                return;
            }

            // 詳細情報を表示する。
            dispInfo(newValue);
        });

        hierarchyTree.setCellFactory((TreeView<EquipmentInfoEntity> o) -> new EquipmentTreeCell());

        // 設備種別一覧
        this.equipmentTypeCollection.put(null, new EquipmentTypeEntity());
        for (EquipmentTypeEntity type : equipmentTypeFacade.findAll()) {
            this.equipmentTypeCollection.put(type.getEquipmentTypeId(), type);
        }
        
        // 検索リセット
        this.searchField.getReseyButton().addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
            this.searchField.getTextField().clear();
            if (Objects.equals(hierarchyTree.rootProperty().getValue(), rootItem)) {
                return;
            }

            //フィルターが適用されていた場合
            TreeItem<EquipmentInfoEntity> selectedItem = hierarchyTree.getSelectionModel().getSelectedItem();
            rootItem.getChildren().clear();
            Optional<TreeItem<EquipmentInfoEntity>> selectedTreeItemOptional = getSelectedTreeItemAndExpand(rootItem, getTopLevelParentId(selectedItem), true);
            TreeItem<EquipmentInfoEntity> selectedTreeItem = selectedTreeItemOptional.orElse(null);
            if (Objects.isNull(selectedTreeItem) || Objects.isNull(selectedTreeItem.getValue().getParentId())) {
                createRoot(null, false);
                this.searchField.getTextField().clear();
                return;
            }

            TreeItem<EquipmentInfoEntity> parentTreeItem = selectedTreeItem.getParent();
            hierarchyTree.rootProperty().setValue(rootItem);
            selectedTreeItem(parentTreeItem, selectedTreeItem.getValue().getEquipmentId());
    });
        
        // 検索
        this.searchField.getTextField().addEventFilter(KeyEvent.KEY_RELEASED, (event) -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                this.onSearch(null);
            }
        });

        // ツリーのルートノードを生成する。
        createRoot(null, false);
    }

    /**
     * 詳細情報を表示する。
     *
     * @param treeItem 選択ノード
     */
    private void dispInfo(TreeItem<EquipmentInfoEntity> treeItem) {
        try {
            if (Objects.nonNull(treeItem)) {
                //変更を確認し保存
                if (this.isChanged()) {
                    boolean isCompleted = saveChanges(prevSelectedItem);
                    if (!isCompleted) {
                        // 移動をキャンセルする。(元の場所を選択状態にする操作は、後で発生するフォーカス移動イベントで行なう。)
                        isCancelMove = true;
                        return;
                    }
                }

                //ルート以外を選んだ時組織の詳細を表示、ルートの場合クリアする
                if (!treeItem.getValue().getEquipmentId().equals(ROOT_ID)) {
                    // 設備の詳細情報を表示する。
                    this.dispInfoDetail(treeItem);
                } else {
                    viewEquipmentInfoEntity = null;
                    this.clearDetailView();
                }

                prevSelectedItem = hierarchyTree.getSelectionModel().getSelectedItem();
            } else {
                viewEquipmentInfoEntity = null;
                this.clearDetailView();
            }

            // ツリーの編集ボタン状態を戻す。
            delButton.setDisable(isDisableEdit);
            copyButton.setDisable(isDisableEdit);
            createButton.setDisable(isDisableEdit);
            moveButton.setDisable(isDisableEdit || !loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.RIGHT_ACCESS));
            authButton.setDisable(isDisableEdit);
            importButton.setDisable(isDisableEdit);
            exportButton.setDisable(isDisableEdit);
            licenseCountButton.setDisable(isDisableEdit);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 設備の詳細情報を表示する。
     *
     * @param treeItem 選択ノード
     */
    private void dispInfoDetail(TreeItem<EquipmentInfoEntity> treeItem) {
        viewEquipmentInfoEntity = equipmentInfoFacade.find(treeItem.getValue().getEquipmentId());

        // 追加情報を取得する
        List<EquipmentSettingInfoEntity> viewEquipmentInfos = viewEquipmentInfoEntity.getSettingInfoCollection();

        EquipmentTypeEntity equipmentType = this.equipmentTypeCollection.get(viewEquipmentInfoEntity.getEquipmentType());
        if (Objects.nonNull(equipmentType) && Objects.nonNull(equipmentType.getName())) {
            // 追加情報にまとめるためにエンティティを用意する
            EquipmentSettingInfoEntity ipv4InfoAddress = new EquipmentSettingInfoEntity();
            EquipmentSettingInfoEntity workProgressFlag = new EquipmentSettingInfoEntity();
            EquipmentSettingInfoEntity pluginName = new EquipmentSettingInfoEntity();

            switch (equipmentType.getName()) {
                case TERMINAL:// 作業者端末
                    // IPvアドレスの情報をセット
                    ipv4InfoAddress.setFkMasterId(viewEquipmentInfoEntity.getEquipmentId());
                    ipv4InfoAddress.setEquipmentSettingName(CustomPropertyTypeEnum.TYPE_IP4_ADDRESS.name());
                    ipv4InfoAddress.setEquipmentSettingOrder(0);
                    ipv4InfoAddress.setEquipmentSettingType(CustomPropertyTypeEnum.TYPE_IP4_ADDRESS);
                    ipv4InfoAddress.setEquipmentSettingValue(viewEquipmentInfoEntity.getIpv4Address());

                    // 工程進捗フラグの情報をセット
                    workProgressFlag.setEquipmentSettingId(viewEquipmentInfoEntity.getEquipmentId());
                    workProgressFlag.setEquipmentSettingName(PropertyEnum.WORK_PROGRESS.name());
                    workProgressFlag.setEquipmentSettingOrder(1);
                    workProgressFlag.setEquipmentSettingType(CustomPropertyTypeEnum.TYPE_BOOLEAN);
                    workProgressFlag.setEquipmentSettingValue(viewEquipmentInfoEntity.getWorkProgressFlag().toString());

                    viewEquipmentInfos.add(ipv4InfoAddress);
                    viewEquipmentInfos.add(workProgressFlag);
                    break;

                case MONITOR:// 進捗モニタ
                    // IPvアドレスの情報をセット
                    ipv4InfoAddress.setFkMasterId(viewEquipmentInfoEntity.getEquipmentId());
                    ipv4InfoAddress.setEquipmentSettingName(CustomPropertyTypeEnum.TYPE_IP4_ADDRESS.name());
                    ipv4InfoAddress.setEquipmentSettingOrder(0);
                    ipv4InfoAddress.setEquipmentSettingType(CustomPropertyTypeEnum.TYPE_IP4_ADDRESS);
                    ipv4InfoAddress.setEquipmentSettingValue(viewEquipmentInfoEntity.getIpv4Address());

                    viewEquipmentInfos.add(ipv4InfoAddress);
                    break;

                case MANUFACTURE:// 製造設備
                case MEASURE:// 測定機器
                    // プラグイン名の情報をセット
                    pluginName.setEquipmentSettingId(viewEquipmentInfoEntity.getEquipmentId());
                    pluginName.setEquipmentSettingName(CustomPropertyTypeEnum.TYPE_PLUGIN.name());
                    pluginName.setEquipmentSettingOrder(0);
                    pluginName.setEquipmentSettingType(CustomPropertyTypeEnum.TYPE_PLUGIN);
                    pluginName.setEquipmentSettingValue(viewEquipmentInfoEntity.getPluginName());

                    viewEquipmentInfos.add(pluginName);
                    break;

                default:
                    break;
            }
        }

        // 追加情報をセットする
        viewEquipmentInfoEntity.setSettingInfoCollection(viewEquipmentInfos);

        // nullの場合の初期値設定　(校正期限対応)
        viewEquipmentInfoEntity.setCalFlag(Optional.ofNullable(viewEquipmentInfoEntity.getCalFlag()).orElse(false));
        viewEquipmentInfoEntity.setCalTermUnit(Optional.ofNullable(viewEquipmentInfoEntity.getCalTermUnit()).orElse(TermUnitEnum.DAYLY));
        viewEquipmentInfoEntity.setCalTerm(Optional.ofNullable(viewEquipmentInfoEntity.getCalTerm()).orElse(0));
        viewEquipmentInfoEntity.setCalWarningDays(Optional.ofNullable(viewEquipmentInfoEntity.getCalWarningDays()).orElse(0));
        calibButton.setDisable(!viewEquipmentInfoEntity.getCalFlag());

        cloneInitialEquipment = null;
        updateDetailView(viewEquipmentInfoEntity, treeItem.getParent().getValue().getEquipmentName());

        //初期情報を一時保管
        cloneInitialEquipment = viewEquipmentInfoEntity.clone();
        currentEquipmentType = cloneInitialEquipment.getEquipmentType();
    }

    /**
     * @param ke
     */
    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            logger.info("onKeyPressed: F5");
            this.hierarchyTree.getRoot().setExpanded(false);
            createRoot(null, true);
        }
    }

    /**
     * 設備削除
     *
     * @param event 削除ボタン押下
     */
    @FXML
    private void onDelete(ActionEvent event) {
        //削除
        try {
            final EquipmentInfoEntity equipment = hierarchyTree.getSelectionModel().getSelectedItem().getValue();

            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                    || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getEquipmentId().equals(ROOT_ID)) {
                return;
            }

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), equipment.getEquipmentName());
            if (ret.equals(ButtonType.CANCEL)) {
                return;
            }

            //変更を破棄
            cloneInitialEquipment = null;

            long equipId = equipment.getEquipmentId();

            ResponseEntity res = equipmentInfoFacade.delete(equipment);
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                // 設備のキャッシュを削除する。
                CacheUtils.removeCacheData(EquipmentInfoEntity.class);
                // ライセンス数を非表示に
                this.isLicenseCount = false;
                hierarchyTree.setCellFactory((TreeView<EquipmentInfoEntity> o) -> new EquipmentTreeCell(this.isLicenseCount, this.isLiteOption, this.isReporterOption));
                //ツリー更新
                this.updateTreeItem(hierarchyTree.getSelectionModel().getSelectedItem().getParent(), equipment.getEquipmentId());
            } else {
                //TODO:エラー時の処理
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 校正を実施する
     *
     * @param event
     */
    @FXML
    private void onCalibration(ActionEvent event) {
        final Date today = Calendar.getInstance().getTime();
        final EquipmentInfoEntity entity = getRegistData();

        final Map<String, Object> dateMap = new HashMap<>();
        dateMap.put("prevDate", DateUtils.toLocalDate(entity.getCalLastDate()));
        dateMap.put("today", today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        dateMap.put("localDate", dateMap.get("today"));
        dateMap.put("localTime", DateUtils.toLocalTime(today));
        
        try {
            // 前回校正日より後の日から当日までを選択させる
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Calibrate"), "CalibrationCompo", dateMap);
            if (Objects.equals(ret, ButtonType.OK)) {
                final Date selected = Date.from(((LocalDate)dateMap.get("localDate")).atStartOfDay(ZoneId.systemDefault()).toInstant());
                final Calendar nextDate = Calendar.getInstance();
                nextDate.setTime(selected);

                final int cycleValue = Objects.isNull(entity.getCalTerm()) ? 0 : entity.getCalTerm();
                switch (entity.getCalTermUnit()) {
                    case DAYLY:
                        nextDate.add(Calendar.DATE, cycleValue);
                        break;
                    case MONTHLY:
                        nextDate.add(Calendar.MONTH, cycleValue);
                        break;
                    case WEEKLY:
                        nextDate.add(Calendar.WEEK_OF_MONTH, cycleValue);
                        break;
                    case YEAR:
                        nextDate.add(Calendar.YEAR, cycleValue);
                        break;
                }
                // 校正者についてはbindされていないため直接値を更新する
                viewEquipmentInfoEntity.setCalPersonId(loginUserInfoEntity.getId());

                ((ObjectProperty<LocalDate>) calibProperties.get("calibDate")).setValue(nextDate.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                ((StringProperty) calibProperties.get("calibInspector")).setValue(findOrganizationName(viewEquipmentInfoEntity.getCalPersonId()));
                ((StringProperty) calibProperties.get("prevCalibDate")).setValue(DateUtils.format(((LocalDate) dateMap.get("localDate")).atTime((LocalTime)dateMap.get("localTime"))));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 設備コピー
     *
     * @param event コピーボタン押下
     */
    @FXML
    private void onCopy(ActionEvent event) {
        try {
            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                    || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getEquipmentId().equals(ROOT_ID)) {
                return;
            }

            boolean isCompleted = true;

            //変更を調べる
            if (this.isChanged()) {
                isCompleted = saveChanges(hierarchyTree.getSelectionModel().getSelectedItem());
                cloneInitialEquipment = null;
            }

            if (isCompleted) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Copy"), LocaleUtils.getString("key.CopyMessage"));
                if (ret.equals(ButtonType.CANCEL)) {
                    return;
                }

                ResponseEntity res = equipmentInfoFacade.copy(hierarchyTree.getSelectionModel().getSelectedItem().getValue());
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    // 設備のキャッシュを削除する。
                    CacheUtils.removeCacheData(EquipmentInfoEntity.class);

                    // ライセンス数を非表示に
                    this.isLicenseCount = false;
                    hierarchyTree.setCellFactory((TreeView<EquipmentInfoEntity> o) -> new EquipmentTreeCell(this.isLicenseCount, this.isLiteOption, this.isReporterOption));

                    //ツリー更新
                    this.updateTreeItem(hierarchyTree.getSelectionModel().getSelectedItem().getParent(), getUriToEquipmentId(res.getUri()));
                } else {
                    //TODO:エラー時の処理
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 設備新規作成
     *
     * @param event 新規作成ボタン押下
     */
    @FXML
    private void onCreate(ActionEvent event) {
        if (Objects.nonNull(hierarchyTree.getSelectionModel().getSelectedItem())) {
            boolean isCompleted = true;//保存に問題がなかった、あるい保存自体なかったときtrue　保存キャンセル時新規作成を行いたくないため

            //変更を調べて保存
            if (this.isChanged()) {
                isCompleted = saveChanges(hierarchyTree.getSelectionModel().getSelectedItem());
            }

            if (isCompleted) {
                viewEquipmentInfoEntity = new EquipmentInfoEntity();
                updateDetailView(viewEquipmentInfoEntity, hierarchyTree.getSelectionModel().getSelectedItem().getValue().getEquipmentName());
                cloneInitialEquipment = new EquipmentInfoEntity();

                // 新規作成時はツリーの編集ボタンを無効にする。
                delButton.setDisable(true);
                copyButton.setDisable(true);
                createButton.setDisable(true);
                moveButton.setDisable(true);
                authButton.setDisable(true);
                importButton.setDisable(true);
                exportButton.setDisable(true);
                licenseCountButton.setDisable(true);

                // ボタン無効でフォーカスが移動するがその移動先がTextFieldだとIMEの座標が狂う。TextField以外に合わせる。
                detailPane.requestFocus();
            }
        }
    }
    
    /**
     * 指定されたツリー項目を基に最上位の親設備IDのリストを取得します。
     *
     * @param selectedItem ツリー構造内の選択された設備情報エンティティ項目
     * @return 最上位の親設備IDを含むリスト、選択項目がnullの場合はnullを返します
     */
    private LinkedList<Long> getTopLevelParentId(TreeItem<EquipmentInfoEntity> selectedItem) {
        if (selectedItem == null) {
            return new LinkedList<>();
        }

        EquipmentInfoEntity currentItem = selectedItem.getValue();
        if (Objects.isNull(currentItem) || Objects.equals(ROOT_ID, currentItem.getEquipmentId())) {
            return new LinkedList<>();
        }

        LinkedList<Long> parentIds = new LinkedList<>();
        parentIds.add(currentItem.getEquipmentId());

        while (currentItem.getParentId() != 0) {
            EquipmentInfoEntity entity = equipmentInfoFacade.get(currentItem.getParentId());
            parentIds.add(entity.getEquipmentId());
            currentItem = entity;
        }
        return parentIds;
    }


    /**
     * 指定されたツリーアイテムの展開状態を監視するリスナーを設定します。
     *
     * @param target 展開状態のリスナーを設定する対象のツリーアイテム
     */
    private void setExpandedListener(TreeItem<EquipmentInfoEntity> target) {
        if (!target.getChildren().isEmpty()) {
            // 既に子要素が設定されている場合はリスナーは不要
            return;
        }

        EquipmentInfoEntity entity = target.getValue();
        if (entity.getChildCount() == 0) {
            // 子要素が存在しない場合は不要
            return;
        }
        target.expandedProperty().removeListener(expandedListener);
        target.getChildren().add(new TreeItem<>());
        target.expandedProperty().addListener(expandedListener);
    }
    
    /**
     * 指定された親項目を展開し、選択されたツリー項目を取得します。
     *
     * @param parent 展開する対象の親ツリー項目
     * @param parentIds 選択されたツリー項目の親IDのリスト
     * * @param isNeedChildren true: 子ノード強制再読み込み（キャッシュ破棄）/ false: 既存データを優先
     * @return 展開後に選択されたツリー項目が存在する場合はオプショナルで返されます。存在しない場合は空のオプショナルを返します。
     */
    Optional<TreeItem<EquipmentInfoEntity>> getSelectedTreeItemAndExpand(TreeItem<EquipmentInfoEntity> parent, LinkedList<Long> parentIds, boolean isNeedChildren) {
        if (parentIds.isEmpty()) {
            if (isNeedChildren) {
                setExpandedListener(parent);
            }
            return Optional.of(parent);
        }

        parent.setExpanded(true);

        long my = parentIds.removeLast();
        if (parent.getChildren().isEmpty() || Objects.isNull(parent.getChildren().get(0).getValue())) {
            long parentId = parent.getValue().getEquipmentId();
            long count = equipmentInfoFacade.getAffilationHierarchyCount(parentId);
            List<EquipmentInfoEntity> equipments = new ArrayList<>();
            for (long from = 0; from <= count; from += MAX_ROLL_HIERARCHY_CNT) {
                List<EquipmentInfoEntity> entities = equipmentInfoFacade.getAffilationHierarchyRange(parentId, from, from + MAX_ROLL_HIERARCHY_CNT - 1);
                equipments.addAll(entities);
            }

            Optional<TreeItem<EquipmentInfoEntity>> myItem = Optional.empty();
            for (EquipmentInfoEntity entity : equipments) {
                TreeItem<EquipmentInfoEntity> item = new TreeItem<>(entity);
                if (entity.getEquipmentId().equals(my)) {
                    myItem = getSelectedTreeItemAndExpand(item, parentIds, isNeedChildren);
                } else {
                    setExpandedListener(item);
                }
                parent.getChildren().add(item);
            }
            return myItem;
        }

        return parent.getChildren()
                .stream()
                .filter(item -> Objects.equals(item.getValue().getEquipmentId(), my))
                .findFirst()
                .flatMap(item -> getSelectedTreeItemAndExpand(item, parentIds, isNeedChildren));
    }
    
    /**
     * 設備移動
     *
     * @param event 移動ボタン押下
     */
    @FXML
    private void onMove(ActionEvent event) {
        try {
            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                    || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getEquipmentId().equals(ROOT_ID)
                    || Objects.isNull(viewEquipmentInfoEntity)) {
                return;
            }

            boolean isCompleted = true;

            if (this.isChanged()) {
                isCompleted = saveChanges(hierarchyTree.getSelectionModel().getSelectedItem());
            }

            if (isCompleted) {
                hierarchyTree.setVisible(false);
                detailPane.setVisible(false);
                propertyPane.setVisible(false);
                settingPane.setVisible(false);
                localePane.setVisible(false);
                TreeItem<EquipmentInfoEntity> selectedItem = hierarchyTree.getSelectionModel().getSelectedItem();
                Optional<TreeItem<EquipmentInfoEntity>> selectedTreeItemOptional = getSelectedTreeItemAndExpand(rootItem, getTopLevelParentId(selectedItem), false);
                TreeItem<EquipmentInfoEntity> selectedTreeItem = selectedTreeItemOptional.orElse(null);
                
                if (Objects.isNull(selectedTreeItem)) {
                    return;
                }

                TreeItem<EquipmentInfoEntity>  parentTreeItem = selectedTreeItem.getParent();
                EquipmentInfoEntity item = viewEquipmentInfoEntity;

                //下のremoveで消えてしまうため一時退避
                List<EquipmentPropertyInfoEntity> customs = new LinkedList<>(customProperties);
                List<EquipmentSettingInfoEntity> settings = new LinkedList<>(settingProperties);

                //移動先として自分を表示させないように一時削除
                int idx = parentTreeItem.getChildren().indexOf(selectedTreeItem);
                parentTreeItem.getChildren().remove(selectedTreeItem);

                // ライセンス数を非表示に
                this.isLicenseCount = false;
                hierarchyTree.setCellFactory((TreeView<EquipmentInfoEntity> o) -> new EquipmentTreeCell(this.isLicenseCount, this.isLiteOption, this.isReporterOption));
                
                //ダイアログに表示させるデータを設定
                TreeDialogEntity treeDialogEntity = new TreeDialogEntity(this.rootItem, LocaleUtils.getString("key.HierarchyName"));
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "EquipmentHierarchyTreeCompo", treeDialogEntity);
                if (ret.equals(ButtonType.OK) && treeDialogEntity.getTreeSelectedItem() != null) {
                    logger.debug(treeDialogEntity.getTreeSelectedItem());
                    TreeItem<EquipmentInfoEntity> hierarchy = (TreeItem<EquipmentInfoEntity>) treeDialogEntity.getTreeSelectedItem();
                    item.setParentId(hierarchy.getValue().getEquipmentId());
                    item.setUpdatePersonId(loginUserInfoEntity.getId());
                    item.setUpdateDateTime(new Date());
                    item.setPropertyInfoCollection(customs);//追加情報を復帰
                    item.setSettingInfoCollection(settings);//設備情報を復帰

                    ResponseEntity res = equipmentInfoFacade.update(item);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // 設備のキャッシュを削除する。
                        CacheUtils.removeCacheData(EquipmentInfoEntity.class);

                        //ツリー更新
                        this.updateTreeItem(hierarchy, item.getEquipmentId());
                        this.hierarchyTree.setRoot(this.rootItem);
                    } else if (ServerErrorTypeEnum.UNMOVABLE_HIERARCHY.equals(res.getErrorType())) {
                        // 移動不可能な階層だった場合、警告表示して階層ツリーを再取得して表示しなおす。
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.unmovableHierarchy"));
                        this.hierarchyTree.getRoot().setExpanded(false);
                        this.createRoot(null, true);
                    } else {
                        //一時削除したデータを元に戻す
                        parentTreeItem.getChildren().add(idx, selectedTreeItem);
                    }
                } else {
                    //一時削除したデータを元に戻す
                    parentTreeItem.getChildren().add(idx, selectedTreeItem);
                    }
                }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            hierarchyTree.setVisible(true);
            detailPane.setVisible(true);
            propertyPane.setVisible(true);
            settingPane.setVisible(true);
            localePane.setVisible(true);
            }
        }

    /**
     * 設備インポートボタン押下
     *
     * @param event ボタン押下
     */
    @FXML
    private void onImport(ActionEvent event) {
        try {

            boolean isCompleted = true;

            if (this.isChanged()) {
                isCompleted = saveChanges(hierarchyTree.getSelectionModel().getSelectedItem());
            }

            if (!isCompleted) {
                return;
            }

            // ファイル選択ダイアログを表示
            FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter(LocaleUtils.getString("key.import.excelFile"), "*.xlsx", "*.xls");
            FileChooser.ExtensionFilter allfilter = new FileChooser.ExtensionFilter(LocaleUtils.getString("key.allCategoryFile"), "*");

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(defaultDir);
            fileChooser.setTitle(LocaleUtils.getString("key.FileChoice"));
            fileChooser.getExtensionFilters().addAll(filter1, allfilter);

            File file = fileChooser.showOpenDialog(sc.getWindow());
            if (Objects.isNull(file)) {
                return;
            }

            boolean isCancel = true;
            try {
                blockUI(true);

                Task task = new Task<ResponseEntity>() {
                    int count = 0;

                    @Override
                    protected ResponseEntity call() throws Exception {

                        defaultDir = file.getParentFile();

                        // Excelファイルからインポート情報を読み込む
                        List<EquipmentImportEntity> list = readExcelInfo(file.getPath());
                        count = list.size();

                        // インポート情報からJSONファイルを生成
                        String jsonStr = JsonUtils.objectsToJson(list); // JSON文字列に変換
                        File jsonFile = createJsonFile(jsonStr); // ファイルに書き込み

                        // インポートAPI呼び出し
                        return equipmentInfoFacade.importFile(jsonFile.getPath());
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        try {

                            if (Objects.isNull(this.getValue())) {
                                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Import"), LocaleUtils.getString("key.alert.communicationServer"));
                                return;
                            }

                            switch (this.getValue().getErrorType()) {
                                case LICENSE_ERROR:
                                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Import"), LocaleUtils.getString("key.LicenseLimitError"));
                                    break;
                                default:
                                    sc.showMessageBox(Alert.AlertType.NONE, LocaleUtils.getString("key.Import"), String.format(LocaleUtils.getString("key.EquipmentImportResultMessage"), this.count), new ButtonType[]{ButtonType.OK}, ButtonType.OK);

                                    // 設備のキャッシュを削除する。
                                    CacheUtils.removeCacheData(EquipmentInfoEntity.class);

                                    // ライセンス数を非表示に
                                    isLicenseCount = false;
                                    hierarchyTree.setCellFactory((TreeView<EquipmentInfoEntity> o) -> new EquipmentTreeCell(isLicenseCount, isLiteOption, isReporterOption));

                                    // 表示更新
                                    createRoot(null, false);
                                    break;
                            }

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
                                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Import"), this.getException().getMessage());
                            } else {
                                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Import"), LocaleUtils.getString("key.alert.systemError"));
                            }
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                        } finally {
                            blockUI(false);
                        }
                    }
                };
                new Thread(task).start();

                isCancel = false;

            } catch (Exception ex) {
                logger.fatal(ex, ex);
                blockUI(false);
            } finally {
                if (isCancel) {
                    blockUI(false);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 設備エクスポートボタン押下
     *
     * @param event ボタン押下
     */
    @FXML
    private void onExport(ActionEvent event) {
        logger.info("onExportStart");
        try {
            blockUI(true);
            // ファイル選択ダイアログを表示
            FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter(LocaleUtils.getString("key.import.excelFile"), "*.xlsx", "*.xls");

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(defaultDir);
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
            fileChooser.setInitialFileName("equipment_" + sdf.format(now));
            fileChooser.setTitle(LocaleUtils.getString("key.FileChoice"));
            fileChooser.getExtensionFilters().addAll(filter1);

            File file = fileChooser.showSaveDialog(sc.getWindow());
            //キャンセルボタン押した場合
            if (Objects.isNull(file)) {
                return;
            }

            // 削除されていない測定機器の設備を取得
            EquipmentSearchCondition condition = new EquipmentSearchCondition();
            condition.setEquipmentType(EquipmentTypeEnum.MEASURE);
            condition.setRemoveFlag(false);
            List<EquipmentInfoEntity> equipmentTypeMeasure = equipmentInfoFacade.findSearchRange(condition, null, null);
            List<Long> parentIds = new ArrayList<>();
            List<Long> CalPersonIds = new ArrayList<>();

            // 空であればエラー
            if (equipmentTypeMeasure.isEmpty()) {
                sc.showMessageBox(Alert.AlertType.NONE, LocaleUtils.getString("key.MesureExport"), LocaleUtils.getString("key.MesureIsNotExist"), new ButtonType[]{ButtonType.OK}, ButtonType.OK);
                return;
            }

            // 親設備名と校正実施者（組織名）をListに格納
            for (EquipmentInfoEntity equipmentInfo : equipmentTypeMeasure) {
                if (Objects.nonNull(equipmentInfo.getParentId())) {
                    parentIds.add(equipmentInfo.getParentId());
                }
                if (Objects.nonNull(equipmentInfo.getCalPersonId())) {
                    CalPersonIds.add(equipmentInfo.getCalPersonId());
                }
            }

            // 親設備を取得しMapに格納
            List<EquipmentInfoEntity> parentEquipments = equipmentInfoFacade.find(parentIds);
            Map<Long, String> parentIdentifyMap = new HashMap<>();
            for (EquipmentInfoEntity parentEquipment : parentEquipments) {
                parentIdentifyMap.put(parentEquipment.getEquipmentId(), parentEquipment.getEquipmentIdentify());
            }

            // 組織情報を取得しMapに格納
            List<OrganizationInfoEntity> organizations = organizationInfoFacade.find(CalPersonIds);
            Map<Long, String> calPersonsMap = new HashMap<>();
            for (OrganizationInfoEntity organization : organizations) {
                calPersonsMap.put(organization.getOrganizationId(), organization.getOrganizationIdentify());
            }

            // ファイルパス取得
            String filepath = file.getPath();
            // ヘッダー設定
            List<String> header = new ArrayList<String>();
            header.add(LocaleUtils.getString("key.EquipmentName"));
            header.add(LocaleUtils.getString("key.deviceID"));
            header.add(LocaleUtils.getString("key.ParentEquipmentIdentify"));
            header.add(LocaleUtils.getString("key.RemoveFlg"));
            header.add(LocaleUtils.getString("key.CalFlg"));
            header.add(LocaleUtils.getString("key.CalibNextDate"));
            header.add(LocaleUtils.getString("key.CalibTerm"));
            header.add(LocaleUtils.getString("key.calTermUnit"));
            header.add(LocaleUtils.getString("key.CalibWarningDays"));
            header.add(LocaleUtils.getString("key.PluginName"));
            header.add(LocaleUtils.getString("key.CalibPerson"));
            header.add(LocaleUtils.getString("key.CalLastDate"));
            for (int i = 0; i < 10; i++) {
                header.add(LocaleUtils.getString("key.AddInfoName"));
                header.add(LocaleUtils.getString("key.AddInfoValue"));
                header.add(LocaleUtils.getString("key.AddInfoType"));
            }

            // エクセル出力
            boolean isSuccess = exportExcel(filepath, equipmentTypeMeasure, header, parentIdentifyMap, calPersonsMap);

            if (isSuccess) {
                sc.showMessageBox(Alert.AlertType.NONE, LocaleUtils.getString("key.MesureExport"), String.format(LocaleUtils.getString("key.MesureExportSucceeded"), equipmentTypeMeasure.size()), new ButtonType[]{ButtonType.OK}, ButtonType.OK);
            } else {
                sc.showMessageBox(Alert.AlertType.NONE, LocaleUtils.getString("key.MesureExport"), String.format(LocaleUtils.getString("key.MesureExportFailed"), LocaleUtils.getString("key.CloseExcelFile")), new ButtonType[]{ButtonType.OK}, ButtonType.OK);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.MesureExport"), LocaleUtils.getString("key.alert.systemError"));
        } finally {
            blockUI(false);
            logger.info("onExportEnd");
        }
    }

    /**
     * アクセス権設定
     *
     * @param event アクセス権設定ボタン押下
     */
    @FXML
    private void onAuth(ActionEvent event) {
        try {
            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                    || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getEquipmentId().equals(ROOT_ID)
                    || Objects.isNull(viewEquipmentInfoEntity)) {
                return;
            }

            boolean isCompleted = true;

            //変更を調べる　存在したとき保存
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    isCompleted = registEquipmentInfo(prevSelectedItem, true);
                } else if (ButtonType.NO == buttonType) {
                    //保存しないときは表示されてる情報をもとのやつに戻す
                    customProperties.clear();
                    customProperties.addAll(cloneInitialEquipment.getPropertyInfoCollection());
                    settingProperties.clear();
                    settingProperties.addAll(cloneInitialEquipment.getSettingInfoCollection());
                    cloneInitialEquipment = null;//下のremoveでChangeListenerが呼ばれる対策　変更を検出させない
                } else {
                    isCompleted = false;
                }
            }

            if (isCompleted) {
                TreeItem<EquipmentInfoEntity> selectedTreeItem = hierarchyTree.getSelectionModel().getSelectedItem();
                //ダイアログに表示させるデータを設定
                AccessHierarchyTypeEnum type = AccessHierarchyTypeEnum.EquipmentHierarchy;
                AccessHierarchyInfoFacade accessHierarchyInfoFacade = new AccessHierarchyInfoFacade();
                long id = selectedTreeItem.getValue().getEquipmentId();
                long count = accessHierarchyInfoFacade.getCount(type, id);
                long range = 100;
                List<OrganizationInfoEntity> deleteList = new ArrayList();
                for (long from = 0; from <= count; from += range) {
                    List<OrganizationInfoEntity> entities = accessHierarchyInfoFacade.getRange(type, id, from, from + range - 1);
                    deleteList.addAll(entities);
                }
                AccessAuthSettingEntity accessAuthSettingEntity
                        = new AccessAuthSettingEntity(selectedTreeItem.getValue().getEquipmentName(), deleteList);
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.EditedAuth"), "AccessAuthSettingCompo", accessAuthSettingEntity);
                if (ret.equals(ButtonType.OK)) {
                    List<OrganizationInfoEntity> registList = accessAuthSettingEntity.getAuthOrganizations();
                    for (int i = 0; i < registList.size(); i++) {
                        OrganizationInfoEntity o = registList.get(i);
                        if (deleteList.contains(o)) {
                            deleteList.remove(o);
                            registList.remove(o);
                            i--;
                        }
                    }
                    if (!deleteList.isEmpty()) {
                        accessHierarchyInfoFacade.delete(type, id, deleteList);
                    }
                    if (!registList.isEmpty()) {
                        accessHierarchyInfoFacade.regist(type, id, registList);
                    }

                    // 設備のキャッシュを削除する。
                    CacheUtils.removeCacheData(EquipmentInfoEntity.class);

                    // ツリー更新
                    this.updateTreeItem(hierarchyTree.getSelectionModel().getSelectedItem().getParent(), selectedTreeItem.getValue().getEquipmentId());
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ライセンス数表示
     *
     * @param event ライセンス数表示ボタン押下
     */
    @FXML
    private void onCountLicense(ActionEvent event) {
        logger.info("onCountLicense start");
        try {
            boolean isCompleted = true;
            if (this.isChanged()) {
                isCompleted = saveChanges(hierarchyTree.getSelectionModel().getSelectedItem());
            }

            if (isCompleted) {
                // ライセンス表示フラグをON
                this.isLicenseCount = true;
                hierarchyTree.setCellFactory((TreeView<EquipmentInfoEntity> o) -> new EquipmentTreeCell(this.isLicenseCount, this.isLiteOption, this.isReporterOption));
                CacheUtils.removeCacheData(EquipmentInfoEntity.class);
                // 設備ツリー／設備詳細を初期化
                viewEquipmentInfoEntity = null;
                this.clearDetailView();
                createRoot(null, false);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onCountLicense end");
        }
    }

    /**
     * 設備登録
     *
     * @param event 登録ボタン押下
     */
    @FXML
    private void onRegist(ActionEvent event) {
        registEquipmentInfo(hierarchyTree.getSelectionModel().getSelectedItem(), true);
    }

    /**
     * 設備情報を保存する。
     *
     * @param target   保存対象
     * @param isSelect
     * @return
     */
    private boolean registEquipmentInfo(TreeItem<EquipmentInfoEntity> target, boolean isSelect) {
        try {
            if (Objects.isNull(target)
                    || Objects.isNull(viewEquipmentInfoEntity)) {
                return false;
            }

            if (checkEmpty()) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return false;
            }

            // ライセンス数を非表示に
            this.isLicenseCount = false;
            hierarchyTree.setCellFactory((TreeView<EquipmentInfoEntity> o) -> new EquipmentTreeCell(this.isLicenseCount, this.isLiteOption, this.isReporterOption));

            EquipmentInfoEntity entity = getRegistData();

            //保存後再描写された設備を記憶
            cloneInitialEquipment = entity.clone();
            cloneInitialEquipment.setEquipmentType(currentEquipmentType);

            if (Objects.nonNull(entity.getEquipmentId()) && entity.getEquipmentId() != ROOT_ID) {
                // 設備情報を更新する。
                ResponseEntity res = equipmentInfoFacade.update(entity);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    // 設備の詳細情報を表示する。
                    this.dispInfoDetail(target);

                    // 設備のキャッシュを削除する。
                    CacheUtils.removeCacheData(EquipmentInfoEntity.class);

                    //ツリー更新
                    if (isSelect) {
                        this.updateTreeItem(target.getParent(), entity.getEquipmentId());
                    } else {
                        prevSelectedItem.getValue().setEquipmentName(entity.getEquipmentName());
                        hierarchyTree.refresh();
                    }
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                    // 排他バージョンが異なる。

                    // 設備のキャッシュを削除する。
                    CacheUtils.removeCacheData(EquipmentInfoEntity.class);

                    //ツリー更新
                    if (isSelect) {
                        this.updateTreeItem(target.getParent(), entity.getEquipmentId());
                    } else {
                        prevSelectedItem.getValue().setEquipmentName(entity.getEquipmentName());
                        hierarchyTree.refresh();
                    }

                    return false;
                } else {
                    //TODO:エラー時の処理
                    return false;
                }
            } else {
                //新規作成処理
                entity.setParentId(target.getValue().getEquipmentId());
                ResponseEntity res = equipmentInfoFacade.regist(entity);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    // 設備のキャッシュを削除する。
                    CacheUtils.removeCacheData(EquipmentInfoEntity.class);

                    //ツリー更新
                    if (isSelect) {
                        this.updateTreeItem(target, getUriToEquipmentId(res.getUri()));
                    } else {
                        this.updateTreeItem(target, null);
                    }
                } else {
                    //TODO:エラー時の処理
                    return false;
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return true;
    }

    /**
     * 各項目から登録に必要な情報を取得する
     *
     * @return 取得したEquipmentInfoEntity
     */
    private EquipmentInfoEntity getRegistData() {

        EquipmentInfoEntity info = viewEquipmentInfoEntity.clone();
        info.setEquipmentId(viewEquipmentInfoEntity.getEquipmentId());
        info.setParentId(viewEquipmentInfoEntity.getParentId());

        info.setEquipmentName(((StringProperty) detailProperties.get("equipmentName").getProperty()).get());
        info.setEquipmentIdentify(((StringProperty) detailProperties.get("equipmentIdentify").getProperty()).get());
        settingProperties.stream().forEach((entity) -> {
            entity.updateMember();
        });
        customProperties.stream().forEach((entity) -> {
            entity.updateMember();
        });

        if (ClientServiceProperty.isLicensed("@Traceability") && !calibProperties.isEmpty()) {
            String warningDaysStr = ((StringProperty) calibProperties.get("warningDays")).get();
            String cycleValueStr = ((StringProperty) calibProperties.get("cycleValue")).get();

            info.setCalWarningDays(warningDaysStr.isEmpty() ? null : Integer.valueOf(warningDaysStr));
            info.setCalTerm(cycleValueStr.isEmpty() ? 1 : Integer.valueOf(cycleValueStr));
            info.setCalFlag(((BooleanProperty) calibProperties.get("enableCalib")).get());
            info.setCalNextDate(DateUtils.toDate(((ObjectProperty<LocalDate>) calibProperties.get("calibDate")).get()));
            info.setCalTermUnit(((ObjectProperty<TermUnitEnum>) calibProperties.get("cycleType")).get());
            info.setCalLastDate(DateUtils.parse((((StringProperty) calibProperties.get("prevCalibDate")).get())));
        }

        ConfigInfoEntity config = null;
        // 文字色
        if(detailColorThemeProperties.containsKey("fontColor")) {
            if ( Objects.isNull(config)) { config = new ConfigInfoEntity(); }
            Color fontColor = ((ObjectProperty<Color>) detailColorThemeProperties.get("fontColor").getProperty()).get();
            config.setFontColor(StringUtils.colorToRGBCode(fontColor));
        }

        // 背景色設定
        if(detailColorThemeProperties.containsKey("backColor")) {
            if ( Objects.isNull(config)) { config = new ConfigInfoEntity(); }
            Color backColor = ((ObjectProperty<Color>) detailColorThemeProperties.get("backColor").getProperty()).get();
            config.setBackColor(StringUtils.colorToRGBCode(backColor));
        }

        if (Objects.nonNull(config)) {
            info.setConfig(JsonUtils.objectToJson(config));
        }

        //更新情報を設定
        info.setUpdatePersonId(loginUserInfoEntity.getId());
        info.setUpdateDateTime(new Date());

        // 表示順を設定
        int order = 0;
        for (EquipmentSettingInfoEntity entity : settingProperties) {
            entity.setEquipmentSettingOrder(order++);
        }
        info.setSettingInfoCollection(settingProperties);
        order = 0;
        for (EquipmentPropertyInfoEntity entity : customProperties) {
            entity.setEquipmentPropOrder(order++);
        }
        info.setPropertyInfoCollection(customProperties);

        return info;
    }

    /**
     * ツリーの表示を更新する。
     *
     * @param parentItem 表示更新するノード
     * @param selectedId 更新後に選択状態にするノードの設備ID
     *                   (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void updateTreeItem(TreeItem<EquipmentInfoEntity> parentItem, Long selectedId) {
        if (Objects.isNull(parentItem.getParent())) {
            //ROOT
            createRoot(selectedId, false);
        } else {
            //子階層
            expand(parentItem, selectedId);
        }
    }

    /**
     * 設備IDが一致するTreeItemを選択する (存在しない場合は親を選択する(削除後の選択用))
     *
     * @param parentItem 選択状態にするノーノの親ノード
     * @param selectedId 選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void selectedTreeItem(TreeItem<EquipmentInfoEntity> parentItem, Long selectedId) {
        if (Objects.equals(ROOT_ID, selectedId) || Objects.isNull(parentItem)) {
            this.hierarchyTree.getSelectionModel().select(this.rootItem);
        } else {
            Optional<TreeItem<EquipmentInfoEntity>> find = parentItem.getChildren().stream().
                    filter(p -> p.getValue().getEquipmentId().equals(selectedId)).findFirst();

            if (find.isPresent()) {
                this.hierarchyTree.getSelectionModel().select(find.get());
            } else {
                this.hierarchyTree.getSelectionModel().select(parentItem);
            }
        }
        this.hierarchyTree.scrollTo(this.hierarchyTree.getSelectionModel().getSelectedIndex());// 選択ノードが見えるようスクロール
    }

    /**
     * ツリーのルートの表示を更新する。
     *
     * @param selectedId 更新後に選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合はルートを選択。)
     * @param isRefresh
     */
    private void createRoot(Long selectedId, boolean isRefresh) {
        logger.debug("createRoot start.");

        final String KEY_LITE_LIC = "@LiteLic";
        final String KEY_REPORTER_LIC = "@ReporterLic";
    
        try {
            blockUI(true);

            // ルートが存在しない場合は新規作成する。
            if (Objects.isNull(this.rootItem)) {
                this.rootItem = new TreeItem<>(new EquipmentInfoEntity(ROOT_ID, null, LocaleUtils.getString("key.Equipment"), 0L));
                SystemResourceFacade systemResource = new SystemResourceFacade();
                final long[] licenseNum = systemResource.getLicenseNum();
                this.rootItem.getValue().setLicenseDisplay(count -> count + "/" + licenseNum[0]);
                this.rootItem.getValue().setLiteDisplay(count -> count + "/" + licenseNum[1]);
                this.rootItem.getValue().setReporterDisplay(count -> count + "/" + licenseNum[2]);
                this.licenseTerminal = licenseNum[0];
            }

            this.rootItem.getChildren().clear();

            Task task = new Task<List<EquipmentInfoEntity>>() {
                @Override
                protected List<EquipmentInfoEntity> call() throws Exception {
                    if (isRefresh) {
                        CacheUtils.removeCacheData(OrganizationInfoEntity.class);
                    }
                    CacheUtils.createCacheOrganization(true);

                    // 子組織の件数を取得する。
                    long count = equipmentInfoFacade.getTopHierarchyCount();

                    List<EquipmentInfoEntity> equipments = new ArrayList();
                    for (long from = 0; from <= count; from += MAX_ROLL_HIERARCHY_CNT) {
                        List<EquipmentInfoEntity> entities = equipmentInfoFacade.getTopHierarchyRange(from, from + MAX_ROLL_HIERARCHY_CNT - 1, isLicenseCount);
                        equipments.addAll(entities);
                    }
                    return equipments;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        long count = this.getValue().size();
                        rootItem.getValue().setChildCount(count);
                        rootItem.getValue().setLicenseCount(0L);
                        rootItem.getValue().setLiteCount(0L);
                        rootItem.getValue().setReporterCount(0L);

                        this.getValue().stream().forEach((entity) -> {
                            TreeItem<EquipmentInfoEntity> item = new TreeItem<>(entity);
                            if (entity.getChildCount() > 0) {
                                item.getChildren().add(new TreeItem());
                            }
                            item.expandedProperty().addListener(expandedListener);
                            rootItem.getChildren().add(item);

                            rootItem.getValue().setLicenseCount(rootItem.getValue().getLicenseCount() + item.getValue().getLicenseCount());
                            if (isLiteOption) {
                                rootItem.getValue().setLiteCount(rootItem.getValue().getLiteCount() + item.getValue().getLiteCount());
                            }
                            if (isReporterOption) {
                                rootItem.getValue().setReporterCount(rootItem.getValue().getReporterCount() + item.getValue().getReporterCount());
                            }
                        });

                        hierarchyTree.rootProperty().setValue(rootItem);

                        // 親ノードが閉じている場合、ノードを開状態にする。(開閉イベントは一旦削除して、開いた後で再登録)
                        if (!rootItem.isExpanded()) {
                            rootItem.expandedProperty().removeListener(expandedListener);
                            rootItem.setExpanded(true);
                            rootItem.expandedProperty().addListener(expandedListener);
                        }

                        if (Objects.nonNull(selectedId)) {
                            // 指定されたノードを選択状態にする。
                            selectedTreeItem(rootItem, selectedId);
                        } else {
                            // 選択ノードの指定がない場合は、ルートを選択状態にする。
                            hierarchyTree.getSelectionModel().select(rootItem);
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
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

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     * ツリーの指定したノードの表示を更新する。
     *
     * @param parentItem 表示更新するノード
     * @param selectedId 選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void expand(TreeItem<EquipmentInfoEntity> parentItem, Long selectedId) {
        logger.debug("expand: parentItem={}", parentItem.getValue());
        try {
            blockUI(true);

            parentItem.getChildren().clear();

            final long parentId = parentItem.getValue().getEquipmentId();

            Task task = new Task<List<EquipmentInfoEntity>>() {
                @Override
                protected List<EquipmentInfoEntity> call() throws Exception {
                    // 子組織の件数を取得する。
                    long count = equipmentInfoFacade.getAffilationHierarchyCount(parentId);

                    List<EquipmentInfoEntity> equipments = new ArrayList();
                    for (long from = 0; from <= count; from += MAX_ROLL_HIERARCHY_CNT) {
                        List<EquipmentInfoEntity> entities = equipmentInfoFacade.getAffilationHierarchyRange(parentId, from, from + MAX_ROLL_HIERARCHY_CNT - 1, isLicenseCount);
                        equipments.addAll(entities);
                    }
                    return equipments;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        this.getValue().stream().forEach((entity) -> {
                            entity.setEquipmentTypeEntity(equipmentTypeCollection.get(entity.getEquipmentType()));
                            
                            TreeItem<EquipmentInfoEntity> item = new TreeItem<>(entity);
                            if (entity.getChildCount() > 0) {
                                item.getChildren().add(new TreeItem());
                            }
                            item.expandedProperty().addListener(expandedListener);
                            parentItem.getChildren().add(item);
                        });

                        if (Objects.nonNull(selectedId)) {
                            // 親ノードが閉じている場合、ノードを開状態にする。(開閉イベントは一旦削除して、開いた後で再登録)
                            if (!parentItem.isExpanded()) {
                                parentItem.expandedProperty().removeListener(expandedListener);
                                parentItem.setExpanded(true);
                                parentItem.expandedProperty().addListener(expandedListener);
                            }
                            // 指定されたノードを選択状態にする。
                            selectedTreeItem(parentItem, selectedId);
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
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

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     *
     */
    class EquipmentTypeComboBoxCellFactory extends ListCell<EquipmentTypeEntity> {

        @Override
        protected void updateItem(EquipmentTypeEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || Objects.isNull(item.getName())) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getName().getResourceKey()));
            }
        }
    }

    private final Callback<ListView<EquipmentTypeEntity>, ListCell<EquipmentTypeEntity>> callbackEquipmentTypeCellFactory = (ListView<EquipmentTypeEntity> param) -> new EquipmentTypeComboBoxCellFactory();

    //設備種別を変更したら、付随する設定テンプレートをコピーするためのアクションリスナー
    private final ChangeListener<EquipmentTypeEntity> equipmentTypeActionListner = (ObservableValue<? extends EquipmentTypeEntity> observable, EquipmentTypeEntity oldValue, EquipmentTypeEntity newValue) -> {

        this.viewEquipmentInfoEntity.setEquipmentName(((StringProperty) this.detailProperties.get("equipmentName").getProperty()).get());
        this.viewEquipmentInfoEntity.setEquipmentIdentify(((StringProperty) this.detailProperties.get("equipmentIdentify").getProperty()).get());
        this.viewEquipmentInfoEntity.setEquipmentType(newValue.getEquipmentTypeId());
        this.viewEquipmentInfoEntity.setSettingInfoCollection(new ArrayList<>());

        // 設備設定
        if (Objects.nonNull(newValue.getSettingTemplateCollection()) 
                && !newValue.getSettingTemplateCollection().isEmpty()) {
            int order = this.settingProperties.size();

            for (EquipmentSettingTemplateInfoEntity template : newValue.getSettingTemplateCollection()) {
                boolean isFind = false;
                for (EquipmentSettingInfoEntity setting : settingProperties) {
                    if (setting.getEquipmentSettingName().equals(template.getSettingName())) {
                        isFind = true;
                        break;
                    }
                }
                if (!isFind) {
                    this.settingProperties.add(new EquipmentSettingInfoEntity(null, null, template.getSettingName(), template.getSettingType(), template.getSettingInitialValue(), ++order));
                }
            }

            this.settingProperties.stream().forEach((entity) -> {
                entity.updateMember();
            });

            this.viewEquipmentInfoEntity.setSettingInfoCollection(new ArrayList<>(this.settingProperties));
        }

        // 追加情報
        int orderNum = 0;
        for (EquipmentPropertyInfoEntity property : this.customProperties) {
            property.setEquipmentPropOrder(orderNum);
            orderNum++;
        }
        this.viewEquipmentInfoEntity.setPropertyInfoCollection(new ArrayList<>(this.customProperties));

        this.currentEquipmentType = this.viewEquipmentInfoEntity.getEquipmentType();

        updateDetailView(this.viewEquipmentInfoEntity, this.hierarchyTree.getSelectionModel().getSelectedItem().getValue().getEquipmentName());
    };

    /**
     * 詳細画面更新処理
     *
     * @param entity ツリーで選択された情報
     */
    private void updateDetailView(EquipmentInfoEntity entity, String parentName) {
        if (Objects.isNull(viewEquipmentInfoEntity)) {
            return;
        }

        clearDetailView();

        List<EquipmentTypeEntity> equipmentTypes = equipmentTypeCollection.values().stream()
            .filter(o -> {
                if (Objects.nonNull(o.getName())) {
                    switch (o.getName()) {
                        case TERMINAL:
                            return this.licenseTerminal > 0;
                        case LITE:
                            return isLiteOption;
                        case REPORTER:
                            return isReporterOption;
                        default:
                            break;
                    }
                }
                return true;
            })
            .sorted((EquipmentTypeEntity o1, EquipmentTypeEntity o2) -> EquipmentTypeEnum.getMessage(rb, o1.getName()).compareTo(EquipmentTypeEnum.getMessage(rb, o2.getName()))).collect(Collectors.toList());

        detailProperties.put("equipmentName", PropertyBindEntity.createRegerxString(LocaleUtils.getString("key.EquipmentName") + LocaleUtils.getString("key.RequiredMark"), entity.getEquipmentName(), "^.{0,255}$"));
        detailProperties.put("equipmentIdentify", PropertyBindEntity.createRegerxString(LocaleUtils.getString("key.EquipmentsManagementName") + LocaleUtils.getString("key.RequiredMark"), entity.getEquipmentIdentify(), "^.{0,255}$"));
        detailProperties.put("equipmentType", PropertyBindEntity.createCombo(LocaleUtils.getString("key.EquipmentType") + LocaleUtils.getString("key.RequiredMark"), 
                equipmentTypes, 
                new EquipmentTypeComboBoxCellFactory(), callbackEquipmentTypeCellFactory, 
                equipmentTypes.stream().anyMatch(o -> Objects.equals(o.getEquipmentTypeId(), entity.getEquipmentType())) ? equipmentTypeCollection.get(entity.getEquipmentType()) : null
            ).actionListner(equipmentTypeActionListner));

        // 設備詳細情報
        Table equipmentDetailTable = new Table(detailPane.getChildren()).isAddRecord(false).title(parentName).styleClass("ContentTitleLabel");
        equipmentDetailTable.setAbstractRecordFactory(new DetailRecordFactory(equipmentDetailTable, new LinkedList(detailProperties.values())));

        // テーマ色
        List<EquipmentTypeEnum> names = Arrays.asList(EquipmentTypeEnum.TERMINAL, EquipmentTypeEnum.LITE, EquipmentTypeEnum.REPORTER);
        final boolean isCorloEnable = equipmentTypeCollection.containsKey(entity.getEquipmentType())
                && names.contains(equipmentTypeCollection.get(entity.getEquipmentType()).getName());

        if (isCorloEnable) {
            Color backColor = Color.web(ConfigInfoEntity.defBackColor);
            Color fontColor = Color.web(ConfigInfoEntity.defFontColor);
            if(!StringUtils.isEmpty(entity.getConfig())) {
                final ConfigInfoEntity config = JsonUtils.jsonToObject(entity.getConfig(), ConfigInfoEntity.class);
                if (Objects.nonNull(config)) {
                    if (Objects.nonNull(config.getFontColor())) {
                        //背景色・文字色の表示
                        fontColor = Color.web(config.getFontColor());
                    }

                    if(Objects.nonNull(config.getBackColor())) {
                        //背景色・文字色の表示
                        backColor = Color.web(config.getBackColor());
                    }
                }
            }
            detailColorThemeProperties.put("fontColor", PropertyBindEntity.createColorPicker(LocaleUtils.getString("key.FontColor"), fontColor));
            detailColorThemeProperties.put("backColor", PropertyBindEntity.createColorPicker(LocaleUtils.getString("key.BackColor"), backColor));
            // 設備詳細情報
            Table colorThemeTable = new Table(detailPane.getChildren()).isAddRecord(false).title(LocaleUtils.getString("key.ColorTheme")).styleClass("ContentTitleLabel");
            colorThemeTable.setAbstractRecordFactory(new DetailRecordFactory(colorThemeTable, new LinkedList(detailColorThemeProperties.values())));
        }

        // 設備種別情報
        if (Objects.isNull(entity.getSettingInfoCollection())) {
            entity.setSettingInfoCollection(new ArrayList<>());
        }
        this.settingProperties.addAll(entity.getSettingInfoCollection());
        this.settingProperties.sort(Comparator.comparing(setting -> setting.getEquipmentSettingOrder()));

        Table equipmentSettingTable = new Table(settingPane.getChildren()).isAddRecord(false)
                .isColumnTitleRecord(true).title(LocaleUtils.getString("key.EquipmentTypeProperty")).styleClass("ContentTitleLabel");
        equipmentSettingTable.setAbstractRecordFactory(new EquipmentSettingRecordFactory(equipmentSettingTable, this.settingProperties, this::onChangedPlugin));

        // 進捗モニターの言語設定
        final boolean isLocaleEnable = equipmentTypeCollection.containsKey(entity.getEquipmentType()) 
                && EquipmentTypeEnum.MONITOR.equals(equipmentTypeCollection.get(entity.getEquipmentType()).getName());

        if (ClientServiceProperty.isLicensed("@LanguageOption") && isLocaleEnable) {
            Table localeTable = new Table<>(localePane.getChildren())
                    .isAddRecord(false)
                    .isColumnTitleRecord(true)
                    .title(LocaleUtils.getString("key.Language"))
                    .styleClass("ContentTitleLabel")
                    .bodyGap(null, 20.0);

            List<LocaleFileInfoEntity> localeList = JsonUtils.jsonToObjects(entity.getLangIds(), LocaleFileInfoEntity[].class);

            Map<LocaleTypeEnum, LocaleFileInfoEntity> localeFileInfoEntityMap = localeList
                    .stream()
                    .filter(locale->Objects.nonNull(locale.getLocaleType()))
                    .map(locale -> new LocaleFileInfoEntity(locale.getLocaleType(), new ResourceInfoEntity(locale.resource())))
                    .peek(localeFileInfoEntity->localeFileInfoEntity.resource().setResourceType(ResourceTypeEnum.LOCALE))
                    .collect(Collectors.toMap(LocaleFileInfoEntity::getLocaleType, Function.identity()));

            // 管理者用が無い場合は新規作成
            localeFileInfoEntityMap.computeIfAbsent(LocaleTypeEnum.ADMONITOR, key->new LocaleFileInfoEntity(key, new ResourceInfoEntity(ResourceTypeEnum.LOCALE)));
            //localeFileInfoEntityMap.computeIfAbsent(LocaleTypeEnum.CUSTUM, key->new LocaleFileInfoEntity(key, new ResourceInfoEntity(ResourceTypeEnum.LOCALE)));
            entity.setLocaleFileInfoCollection(new ArrayList<>(localeFileInfoEntityMap.values()));

            for (Map.Entry<LocaleTypeEnum,LocaleFileInfoEntity> fileInfoEntitySet : localeFileInfoEntityMap.entrySet()) {
                fileInfoEntitySet.getValue().resource().resourceKeyProperty().setValueListener(createLocaleListener(fileInfoEntitySet.getKey(), fileInfoEntitySet.getValue().resource()));
            }

            localeTable.setAbstractRecordFactory(new LocaleFileRecordFactory(localeTable, new LinkedList<>(localeFileInfoEntityMap.values())));
        }
        localePane.setManaged(isLocaleEnable);

        // 品質トレーサビリティライセンスが有効な場合、校正関連を編集可能にする
        if (ClientServiceProperty.isLicensed("@Traceability")) {
            // 校正情報
            calibProperties.put("enableCalib", new SimpleBooleanProperty(Objects.isNull(entity.getCalFlag()) ? false : entity.getCalFlag()));
            calibProperties.put("calibDate", new SimpleObjectProperty<LocalDate>(DateUtils.toLocalDate(entity.getCalNextDate())));
            calibProperties.put("warningDays", new SimpleStringProperty(Objects.isNull(entity.getCalWarningDays()) ? "" : String.valueOf(entity.getCalWarningDays())));
            calibProperties.put("cycleValue", new SimpleStringProperty(Objects.isNull(entity.getCalTerm()) ? "" : String.valueOf(entity.getCalTerm())));
            calibProperties.put("cycleType", new SimpleObjectProperty(Optional.ofNullable(entity.getCalTermUnit()).orElse(TermUnitEnum.DAYLY)));
            calibProperties.put("prevCalibDate", new SimpleStringProperty(DateUtils.format(entity.getCalLastDate())));
            calibProperties.put("calibInspector", new SimpleStringProperty(findOrganizationName(entity.getCalPersonId())));

            Table calibTable = new Table(calibPane.getChildren())
                    .isAddRecord(false).isColumnTitleRecord(true).title(LocaleUtils.getString("key.Calibration")).styleClass("ContentTitleLabel");
            // テーブルを作成するには各レコードのリストとして作成する必要があるため変換
            calibTable.setAbstractRecordFactory(new CalibrationSettingRecordFactory(calibTable,
                    calibProperties.entrySet().stream()
                            .map(entry -> {
                                Map<String, Property> pair = new HashMap<>();
                                pair.put(entry.getKey(), entry.getValue());
                                return pair;
                            })
                            .collect(Collectors.toCollection(LinkedList::new)),
                    calibButton,
                    loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)
            ));

            resetCalibPane(entity);
        }

        // 追加情報
        if (Objects.isNull(entity.getPropertyInfoCollection())) {
            entity.setPropertyInfoCollection(new ArrayList<>());
        }
        this.customProperties.addAll(entity.getPropertyInfoCollection());
        this.customProperties.sort(Comparator.comparing(property -> property.getEquipmentPropOrder()));

        Table equipmentCustomTable = new Table(propertyPane.getChildren()).isAddRecord(true)
                .isColumnTitleRecord(true).title(LocaleUtils.getString("key.CustomField")).styleClass("ContentTitleLabel");
        equipmentCustomTable.setAbstractRecordFactory(new EquipmentPropertyRecordFactory(equipmentCustomTable, this.customProperties));
    }

    /**
     * @param entity
     */
    private void resetCalibPane(EquipmentInfoEntity entity) {
        calibPane.visibleProperty().bind(calibButton.visibleProperty());
        calibPane.managedProperty().bind(calibButton.managedProperty());
        calibButton.setVisible(isCalibrationEquipment(entity));
        calibButton.setManaged(calibButton.isVisible());
    }

    /**
     * プラグイン名が変更された時のイベント
     *
     * @param ob
     * @param newValue
     * @param oldValue
     */
    private void onChangedPlugin(ObservableValue<? extends String> ob, String oldValue, String newValue) {
        resetCalibPane(viewEquipmentInfoEntity);
    }

    /**
     * 校正情報を表示する設備か判断する
     *
     * @param entity
     * @return 校正情報を表示する設備であった場合true
     */
    private boolean isCalibrationEquipment(EquipmentInfoEntity entity) {
        final Function<EquipmentTypeEnum, Boolean> isCalibrationInternal = x -> x == EquipmentTypeEnum.MEASURE || x == EquipmentTypeEnum.MANUFACTURE;

        final boolean isEnableType = Optional.ofNullable(equipmentTypeCollection.get(entity.getEquipmentType()))
                .map(EquipmentTypeEntity::getName)
                .map(isCalibrationInternal)
                .orElse(false);

        final boolean isEnablePlugin = !Arrays.asList("", "Simple").contains(getPluginName(entity));

        return isEnablePlugin && isEnableType;
    }

    /**
     * 設備のプラグイン名を検索する。設定されていない場合""を返す。
     *
     * @param entity
     * @return
     */
    private String getPluginName(EquipmentInfoEntity entity) {
        final Predicate<EquipmentSettingInfoEntity> isPlugin = s -> s.getEquipmentSettingType().equals(CustomPropertyTypeEnum.TYPE_PLUGIN);
        return entity.getSettingInfoCollection().stream()
                .filter(isPlugin)
                .findAny()
                .map(EquipmentSettingInfoEntity::getEquipmentSettingValue)
                .orElse("");
    }

    /**
     * 詳細画面初期化
     */
    private void clearDetailView() {
        detailPane.getChildren().clear();
        calibPane.getChildren().clear();
        detailProperties.clear();
        propertyPane.getChildren().clear();
        settingPane.getChildren().clear();
        localePane.getChildren().clear();
        customProperties.clear();
        settingProperties.clear();
        calibProperties.clear();
        calibButton.setVisible(false);
    }

    /**
     * 未入力項目があるか？
     *
     * @return (true : 未入力あり, false : 未入力なし)
     */
    private boolean checkEmpty() {
        String name = ((StringProperty) detailProperties.get("equipmentName").getProperty()).get();
        if (Objects.isNull(name) || name.isEmpty() || name.equals("")) {
            return true;
        }
        String identify = ((StringProperty) detailProperties.get("equipmentIdentify").getProperty()).get();
        if (Objects.isNull(identify) || identify.isEmpty() || name.equals("")) {
            return true;
        }

        return customProperties.stream().map((entity) -> {
            entity.updateMember();
            return entity;
        }).anyMatch((entity) -> (Objects.isNull(entity.getEquipmentPropName())
                || entity.getEquipmentPropName().isEmpty()
                || Objects.isNull(entity.getEquipmentPropType())));
    }

    /**
     * @param uri
     * @return
     */
    private long getUriToEquipmentId(String uri) {
        long ret = 0;
        try {
            String[] split = uri.split("/");
            if (split.length == 2) {
                ret = Long.parseLong(split[1]);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        Progress.setVisible(flg);
    }

    /**
     * 内容が変更されたかどうかを返す。
     *
     * @return
     */
    private boolean isChanged() {
        // 編集権限なしは常に無変更
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            return false;
        }

        if (Objects.isNull(cloneInitialEquipment) || Objects.isNull(viewEquipmentInfoEntity)) {
            return false;
        }

        EquipmentInfoEntity entity = getRegistData();
        if (Objects.isNull(entity)) {
            return false;
        }

        entity.setEquipmentType(currentEquipmentType);

        if (entity.displayInfoEquals(cloneInitialEquipment)) {
            return false;
        }

        return true;
    }

    /**
     * コンポーネントが破棄される前に呼び出される。破棄できない場合は、falseを返す。 ほかの画面に遷移するとき変更が存在するなら保存するか確認する
     *
     * @return
     */
    @Override
    public boolean destoryComponent() {
        boolean ret = true;

        if (isChanged()) {
            ret = saveChanges(hierarchyTree.getSelectionModel().getSelectedItem());
        }

        SplitPaneUtils.saveDividerPosition(equipmentPane, getClass().getSimpleName());

        return ret;
    }

    /**
     * 変更の有無を調べ、するなら保存する
     *
     * @param target 変更を適用する項目
     * @return 保存できなかった、あるいは保存がキャンセルされたらfalse
     */
    private boolean saveChanges(TreeItem<EquipmentInfoEntity> target) {
        try {
            logger.info("saveChanges start");

            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            try {
                return ThreadUtils.joinFXThread(() -> {
                    ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                    if (ButtonType.YES == buttonType) {
                        //保存成功でそのまま閉じる。それ以外なら閉じさせない
                        return registEquipmentInfo(target, false);
                    } else if (ButtonType.NO == buttonType) {//いいえの場合何もせずに閉じる
                        return true;
                    }
                    return false;
                });
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        } finally {
            // ツリーのボタンを有効にする。
            delButton.setDisable(false);
            copyButton.setDisable(false);
            createButton.setDisable(false);
            moveButton.setDisable(false || !loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.RIGHT_ACCESS));
            authButton.setDisable(false);
            importButton.setDisable(false);
            exportButton.setDisable(false);
            licenseCountButton.setDisable(false);
            logger.info("saveChanges end");
        }

        return false;
    }

    /**
     * キャッシュから組織IDを指定してその組織名を取得する
     *
     * @param calibInspector
     * @return
     */
    private String findOrganizationName(Long calibInspector) {
        if (Objects.isNull(calibInspector)) {
            return "";
        }
        final OrganizationInfoEntity organization = CacheUtils.getCacheOrganization(calibInspector);
        return Optional.ofNullable(organization)
                .map(OrganizationInfoEntity::getOrganizationName)
                .orElse("");
    }

    /**
     * LocalDateからyyyy/MM/dd形式の文字列への変換
     *
     * @param localDate
     * @return yyyy/MM/dd形式文字列　localDateがnullであった場合、常に空白の文字列を返す
     */
    private String localDateToString(LocalDate localDate) {
        if (Objects.isNull(localDate)) {
            return "";
        }
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return localDate.format(formatter);
    }

    /**
     * yyyy/MM/dd形式の文字列からLocalDateへの変換
     *
     * @param string
     * @return 変換したLocalDate　stringがnullまたは空白であった場合、常にnullを返す
     */
    private LocalDate stringToLocalDate(String string) {
        if (Objects.isNull(string) || string.isEmpty()) {
            return null;
        }
        return LocalDate.parse(string, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    /**
     * 設備インポート情報をExcelファイルから読み取る。
     *
     * @param filePath   Excelファイルパス
     * @param formatInfo カンバンのフォーマット情報
     * @return インポート用データ一覧
     */
    private List<EquipmentImportEntity> readExcelInfo(String filePath) throws Exception {
        logger.info("getExcelKanban start.");
        List<EquipmentImportEntity> values = null;

        // 開始行
        int startRow = 2;
        // 設備名
        int idxEquipmentName = 1;
        // 設備識別名
        int idxEquipmentIdentify = 2;
        // 親設備名
        int idxParentEquipmentName = 3;
        // 論理削除フラグ
        int idxrRmoveFlg = 4;
        // 機器校正有無
        int idxCalFlg = 5;
        // 次回校正日
        int idxCalNextDate = 6;
        // 校正間隔
        int idxCalTerm = 7;
        // 校正間隔単位
        int idxCalTermUnit = 8;
        // 警告表示日数
        int idxCalWarningDays = 9;
        // プラグイン名
        int idxPluginName = 10;
        // 校正実施者
        int idxcalperson = 11;
        // 最終校正日
        int idxCalLastDate = 12;
        // 追加情報
        int idxAddInfo = 13;

        int count = 0;
        List<String> cols = null;
        String formatErrorMessage = "";
        try {
            ExcelFileUtils excelFileUtils = new ExcelFileUtils();
            List<List<String>> rows = excelFileUtils.readExcel(filePath, startRow, null, null);
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
            // 測定機器の設備種別IDを取得
            Long equipmentTypeId = null;
            for (EquipmentTypeEntity equipmentType : equipmentTypeCollection.values()) {
                if (EquipmentTypeEnum.MEASURE.equals(equipmentType.getName())) {
                    equipmentTypeId = equipmentType.getEquipmentTypeId();
                    break;
                }
            }

            // プラグイン名を読み込む
            List<String> pluginNames = EquipmentSettingRecordFactory.getPluginNames();

            if (Objects.nonNull(rows)) {
                values = new ArrayList();

                for (count = 0; count < rows.size(); count++) {
                    cols = rows.get(count);
                    EquipmentImportEntity data = new EquipmentImportEntity();
                    // 論理削除フラグ 0ならデータをサーバに送信しない
                    if ("1".equals(cols.get(idxrRmoveFlg - 1))) {

                    } else {
                        data.setRemoveFlg(false);

                        // 設備名
                        data.setEquipmentName(Objects.isNull(cols.get(idxEquipmentName - 1)) ? "" : cols.get(idxEquipmentName - 1));
                        // 設備識別名
                        data.setEquipmentIdentify(Objects.isNull(cols.get(idxEquipmentIdentify - 1)) ? "" : cols.get(idxEquipmentIdentify - 1));
                        // 設備種別（測定機器のID） 
                        if (Objects.nonNull(equipmentTypeId)) {
                            data.setEquipmentTypeId(equipmentTypeId);
                        }
                        // 親設備名
                        data.setParentName(Objects.isNull(cols.get(idxParentEquipmentName - 1)) ? "" : cols.get(idxParentEquipmentName - 1));
                        
                        // 機器校正有無
                        if ("1".equals(cols.get(idxCalFlg - 1))) {
                            data.setCalFlg(true);
                        } else {
                            data.setCalFlg(false);
                        }

                        // 次回校正日
                        // デフォルトnull
                        if (!cols.get(idxCalNextDate - 1).isEmpty()) {
                            Date date = sdFormat.parse(cols.get(idxCalNextDate - 1));
                            data.setCalNextDate(date);
                        }

                        // 校正間隔
                        // デフォルトnull
                        if (!cols.get(idxCalTerm - 1).isEmpty()) {
                            data.setCalTerm(Integer.parseInt(cols.get(idxCalTerm - 1)));
                        }
                        // 校正間隔単位
                        // デフォルトnull
                        if (!cols.get(idxCalTermUnit - 1).isEmpty()) {
                            data.setCalTermUnit(cols.get(idxCalTermUnit - 1));
                        }
                        // 警告表示日数
                        // デフォルトnull
                        if (!cols.get(idxCalWarningDays - 1).isEmpty()) {
                            data.setCalWarnigDays(Integer.parseInt(cols.get(idxCalWarningDays - 1)));
                        }
                        // プラグイン名
                        // デフォルトSimple
                        if (!cols.get(idxPluginName - 1).isEmpty()) {
                            data.setPluginName(cols.get(idxPluginName - 1));
                        } else {
                            data.setPluginName("Simple");
                        }

                        // 最終校正日
                        // デフォルトnull
                        if (!cols.get(idxCalLastDate - 1).isEmpty()) {
                            Date date = sdFormat.parse(cols.get(idxCalLastDate - 1));
                            data.setCalLastDate(date);
                        }

                        // 校正実施者
                        // デフォルトnull
                        if (!cols.get(idxcalperson - 1).isEmpty()) {
                            data.setCalperson(cols.get(idxcalperson - 1));
                        }

                        // 追加情報
                        // デフォルトnull                  
                        List<EquipmentImportEntity.addInfoJson> addInfoList = new ArrayList<>();
                        int disp = 0;
                        for (int i = idxAddInfo - 1; i < cols.size(); i = i + 3) {
                            // 追加項目は最大10個まで
                            if (!cols.get(i).isEmpty() && disp < 10) {
                                EquipmentImportEntity.addInfoJson addInfo = new EquipmentImportEntity.addInfoJson();
                                addInfo.setKey(cols.get(i));
                                addInfo.setVal(cols.get(i + 1));
                                addInfo.setDisp(disp);
                                addInfo.setType(cols.get(i + 2));
                                addInfoList.add(addInfo);
                                disp++;
                            } else {
                                break;
                            }
                        }

                        data.setAddInfo(addInfoList);

                        // データがインポート可能かチェックし、エラーメッセージが返ってきたらダイアログにその内容を表示させる
                        formatErrorMessage = data.importableCheck(pluginNames);
                        if (!formatErrorMessage.isEmpty()) {
                            values = null;
                            break;
                        } else {
                            values.add(data);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            values = null;
        } finally {
            logger.info("getExcelKanban end.");
        }

        if (Objects.isNull(values) || values.isEmpty()) {
            if (Objects.isNull(formatErrorMessage) || formatErrorMessage.isEmpty()) {
                throw new Exception(LocaleUtils.getString("key.EquipmentImportFormatError"));
            } else {
                throw new Exception(LocaleUtils.getString("key.EquipmentImportFormatError") + "\r\n" + LocaleUtils.getString(formatErrorMessage));
            }
        }
        return values;
    }

    /**
     * JSONファイルを作成する。
     *
     * @param writeStr 書き込む文字列
     * @return 生成したJSONファイル
     */
    private File createJsonFile(String writeStr) {

        final String JSON_EXTENSION = ".json";
        final String TEMP_PATH = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator + "ImportEquipment";

        String now = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String fileName = "ImportEquipment" + "_" + now + JSON_EXTENSION;
        File file;

        try {
            // 一時フォルダからJSONファイルを削除する。
            Path path = Paths.get(TEMP_PATH);
            this.cleanupFolder(path, JSON_EXTENSION);

            file = new File(TEMP_PATH + File.separator + fileName);

            // JSONファイルに文字列を書き込む
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));) {
                writer.print(writeStr);
            }

        } catch (IOException ex) {
            logger.fatal(ex, ex);
            file = null;
        }

        return file;
    }

    /**
     * フォルダがなければ作成し、指定した拡張子のファイルがあれば削除する。
     *
     * @param path      フォルダパス
     * @param extension 削除する拡張子
     * @throws IOException
     */
    private void cleanupFolder(Path path, String extension) throws IOException {
        Files.createDirectories(path);
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, "*" + extension)) {
            for (Path deleteFilePath : ds) {
                Files.delete(deleteFilePath);
            }
        }
    }

    /**
     * Excelファイルを出力
     *
     * @param filepath         ファイルの絶対パス
     * @param equipmentInfos   設備情報
     * @param header           ヘッダ
     * @param parentEquipments 親設備情報
     * @param calPersons       　校正実施者情報
     * @return true = 出力成功　false = 出力失敗
     */
    public boolean exportExcel(String filepath, List<EquipmentInfoEntity> equipmentInfos, List<String> header, Map<Long, String> parentEquipments, Map<Long, String> calPersons) {
        logger.info("readExcel: filename={}, readStartRow={}, readCols={}", filepath);
        boolean isSuccess = false;
        if (filepath.toLowerCase().endsWith(".xls")) {
            isSuccess = this.createExcelXls(filepath, equipmentInfos, header, parentEquipments, calPersons);
        } else if (filepath.toLowerCase().endsWith(".xlsx") || filepath.toLowerCase().endsWith(".xlsm")) {
            isSuccess = this.createExcelXlsx(filepath, equipmentInfos, header, parentEquipments, calPersons);
        }
        return isSuccess;
    }

    /**
     * Excelファイルを出力
     *
     * @param filepath         ファイルの絶対パス
     * @param equipmentInfos   設備情報
     * @param header           ヘッダ
     * @param parentEquipments 親設備情報
     * @param calPersons       　校正実施者情報
     * @return true = 出力成功　false = 出力失敗
     */
    public boolean createExcelXls(String filepath, List<EquipmentInfoEntity> equipmentInfos, List<String> header, Map<Long, String> parentEquipments, Map<Long, String> calPersons) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();

        // Excelファイル出力
        try (FileOutputStream outputStream = new FileOutputStream(filepath)) {
            logger.debug("  sheet={}", sheet);

            HSSFRow row;
            HSSFCell cell;

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            row = sheet.createRow(0);
            // ヘッダを設定
            for (int headerColumn = 0; headerColumn < header.size(); headerColumn++) {
                cell = row.createCell(headerColumn);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(header.get(headerColumn));
            }

            for (int i = 0; equipmentInfos.size() > i; i++) {
                EquipmentInfoEntity equipmentInfo = equipmentInfos.get(i);
                String addinfo = equipmentInfo.getEquipmentAddInfo();
                String parentIdentify = "";
                String calPersonName = "";
                EquipmentImportEntity.addInfoJson[] addinfos = null;

                if (equipmentInfo.getParentId() != 0) {
                    parentIdentify = parentEquipments.get(equipmentInfo.getParentId());
                }

                if (Objects.nonNull(equipmentInfo.getCalPersonId())) {
                    calPersonName = calPersons.get(equipmentInfo.getCalPersonId());
                }

                if (Objects.nonNull(addinfo)) {
                    addinfos = JsonUtils.jsonToObject(addinfo, EquipmentImportEntity.addInfoJson[].class);
                }

                int n = 0;

                // データを設定
                logger.debug("{} 行目", i + 1);
                row = sheet.createRow(i + 1);

                for (int dataColumn = 0; dataColumn < header.size(); dataColumn++) {
                    logger.debug("{} 項目", dataColumn + 1);
                    boolean isBreak = false;
                    // セルの読み込み
                    cell = row.createCell(dataColumn);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    switch (dataColumn) {
                        case 0:
                            // 設備名
                            cell.setCellValue(equipmentInfo.getEquipmentName());
                            break;
                        case 1:
                            // 設備識別名
                            cell.setCellValue(equipmentInfo.getEquipmentIdentify());
                            break;
                        case 2:
                            // 親設備識別名
                            cell.setCellValue(parentIdentify);
                            break;
                        case 3:
                            // 削除フラグ
                            if (equipmentInfo.getRemoveFlag()) {
                                cell.setCellValue("1");
                            } else {
                                cell.setCellValue("0");
                            }
                            break;
                        case 4:
                            // 機器校正有無
                            if (Objects.nonNull(equipmentInfo.getCalFlag()) && equipmentInfo.getCalFlag()) {
                                cell.setCellValue("1");
                            } else {
                                cell.setCellValue("0");
                            }
                            break;
                        case 5:
                            // 次回校正日
                            if (Objects.nonNull(equipmentInfo.getCalNextDate())) {
                                cell.setCellValue(equipmentInfo.getCalNextDate());
                            }
                            break;
                        case 6:
                            // 校正間隔
                            if (Objects.nonNull(equipmentInfo.getCalTerm())) {
                                cell.setCellValue(String.valueOf(equipmentInfo.getCalTerm()));
                            }
                            break;
                        case 7:
                            // 校正間隔単位
                            if (Objects.nonNull(equipmentInfo.getCalTermUnit())) {
                                cell.setCellValue(String.valueOf(equipmentInfo.getCalTermUnit()));
                            }
                            break;
                        case 8:
                            // 警告表示日
                            if (Objects.nonNull(equipmentInfo.getCalWarningDays())) {
                                cell.setCellValue(String.valueOf(equipmentInfo.getCalWarningDays()));
                            }
                            break;
                        case 9:
                            // プラグイン名
                            if (Objects.nonNull(equipmentInfo.getPluginName())) {
                                cell.setCellValue(equipmentInfo.getPluginName());
                            }
                            break;
                        case 10:
                            // 校正実施者
                            cell.setCellValue(calPersonName);
                            break;
                        case 11:
                            // 最終校正日
                            if (Objects.nonNull(equipmentInfo.getCalLastDate())) {
                                cell.setCellValue(equipmentInfo.getCalLastDate());
                            }
                            break;
                        default:
                            // 追加項目
                            if (Objects.nonNull(addinfos) && addinfos.length > n) {
                                if (dataColumn % 3 == 0) {
                                    // 追加項目名
                                    cell.setCellValue(addinfos[n].getKey());
                                } else if (dataColumn % 3 == 1) {
                                    // 追加項目現在値
                                    cell.setCellValue(addinfos[n].getVal());
                                } else {
                                    // 追加項目タイプ
                                    cell.setCellValue(addinfos[n].getType());
                                    n++;
                                }
                            } else {
                                // 追加項目が存在しないのでfor文を抜けるフラグを立てる
                                isBreak = true;
                                break;
                            }
                    }
                    if (isBreak) {
                        break;
                    }
                }
            }
            // ファイル出力          
            workbook.write(outputStream);
        } catch (Exception e) {
            logger.fatal(e, e);
            return false;
        } finally {
            this.closeXml(workbook);
        }
        return true;
    }

    /**
     * Excelファイルを出力
     *
     * @param filepath         ファイルの絶対パス
     * @param equipmentInfos   設備情報
     * @param header           ヘッダ
     * @param parentEquipments 親設備情報
     * @param calPersons       　校正実施者情報
     * @return true = 出力成功　false = 出力失敗
     */
    public boolean createExcelXlsx(String filepath, List<EquipmentInfoEntity> equipmentInfos, List<String> header, Map<Long, String> parentEquipments, Map<Long, String> calPersons) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        // Excelファイル出力
        try (FileOutputStream outputStream = new FileOutputStream(filepath)) {
            logger.debug("  sheet={}", sheet);
            if (sheet == null) {
                return false;
            }


            XSSFRow row;
            XSSFCell cell;

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            row = sheet.createRow(0);
            // ヘッダを設定
            for (int headerColumn = 0; headerColumn < header.size(); headerColumn++) {
                cell = row.createCell(headerColumn);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(header.get(headerColumn));
            }

            for (int i = 0; equipmentInfos.size() > i; i++) {
                EquipmentInfoEntity equipmentInfo = equipmentInfos.get(i);
                String addinfo = equipmentInfo.getEquipmentAddInfo();
                String parentIdentify = "";
                String calPersonName = "";
                EquipmentImportEntity.addInfoJson[] addinfos = null;

                if (equipmentInfo.getParentId() != 0) {
                    parentIdentify = parentEquipments.get(equipmentInfo.getParentId());
                }

                if (Objects.nonNull(equipmentInfo.getCalPersonId())) {
                    calPersonName = calPersons.get(equipmentInfo.getCalPersonId());
                }

                if (Objects.nonNull(addinfo)) {
                    addinfos = JsonUtils.jsonToObject(addinfo, EquipmentImportEntity.addInfoJson[].class);
                }

                int n = 0;

                // データを設定
                logger.debug("{} 行目", i + 1);
                row = sheet.createRow(i + 1);

                for (int dataColumn = 0; dataColumn < header.size(); dataColumn++) {
                    logger.debug("{} 項目", dataColumn + 1);
                    boolean isBreak = false;
                    // セルの読み込み
                    cell = row.createCell(dataColumn);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    switch (dataColumn) {
                        case 0:
                            // 設備名
                            cell.setCellValue(equipmentInfo.getEquipmentName());
                            break;
                        case 1:
                            // 設備識別名
                            cell.setCellValue(equipmentInfo.getEquipmentIdentify());
                            break;
                        case 2:
                            // 親設備識別名
                            cell.setCellValue(parentIdentify);
                            break;
                        case 3:
                            // 削除フラグ
                            if (equipmentInfo.getRemoveFlag()) {
                                cell.setCellValue("1");
                            } else {
                                cell.setCellValue("0");
                            }
                            break;
                        case 4:
                            // 機器校正有無
                            if (Objects.nonNull(equipmentInfo.getCalFlag()) && equipmentInfo.getCalFlag()) {
                                cell.setCellValue("1");
                            } else {
                                cell.setCellValue("0");
                            }
                            break;
                        case 5:
                            // 次回校正日
                            if (Objects.nonNull(equipmentInfo.getCalNextDate())) {
                                cell.setCellValue(equipmentInfo.getCalNextDate());
                            }
                            break;
                        case 6:
                            // 校正間隔
                            if (Objects.nonNull(equipmentInfo.getCalTerm())) {
                                cell.setCellValue(String.valueOf(equipmentInfo.getCalTerm()));
                            }
                            break;
                        case 7:
                            // 校正間隔単位
                            if (Objects.nonNull(equipmentInfo.getCalTermUnit())) {
                                cell.setCellValue(String.valueOf(equipmentInfo.getCalTermUnit()));
                            }
                            break;
                        case 8:
                            // 警告表示日
                            if (Objects.nonNull(equipmentInfo.getCalWarningDays())) {
                                cell.setCellValue(String.valueOf(equipmentInfo.getCalWarningDays()));
                            }
                            break;
                        case 9:
                            // プラグイン名
                            if (Objects.nonNull(equipmentInfo.getPluginName())) {
                                cell.setCellValue(equipmentInfo.getPluginName());
                            }
                            break;
                        case 10:
                            // 校正実施者
                            cell.setCellValue(calPersonName);
                            break;
                        case 11:
                            // 最終校正日
                            if (Objects.nonNull(equipmentInfo.getCalLastDate())) {
                                cell.setCellValue(equipmentInfo.getCalLastDate());
                            }
                            break;
                        default:
                            // 追加項目
                            if (Objects.nonNull(addinfos) && addinfos.length > n) {
                                if (dataColumn % 3 == 0) {
                                    // 追加項目名
                                    cell.setCellValue(addinfos[n].getKey());
                                } else if (dataColumn % 3 == 1) {
                                    // 追加項目現在値
                                    cell.setCellValue(addinfos[n].getVal());
                                } else {
                                    // 追加項目タイプ
                                    cell.setCellValue(addinfos[n].getType());
                                    n++;
                                }
                            } else {
                                // 追加項目が存在しないのでfor文を抜けるフラグを立てる
                                isBreak = true;
                                break;
                            }
                    }
                    if (isBreak) {
                        break;
                    }
                }
            }
            // ファイル出力          
            workbook.write(outputStream);
        } catch (Exception e) {
            logger.fatal(e, e);
            return false;
        } finally {
            this.closeXmls(workbook);
        }
        return true;
    }

    /**
     * Excelクローズ(xml)
     *
     * @return 結果
     */
    private boolean closeXml(HSSFWorkbook workbook) {
        if (Objects.isNull(workbook)) {
            this.logger.warn(" 既にクローズしています。");
            return false;
        }

        boolean value = false;
        try {
            // ワークブッククローズ
            workbook.close();
            // Excelファイルクローズ
            workbook.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            if (Objects.isNull(workbook)) {
                value = true;
            }
        }

        return value;
    }

    /**
     * Excelクローズ(xmls)
     *
     * @return 結果
     */
    private boolean closeXmls(XSSFWorkbook workbook) {
        if (Objects.isNull(workbook)) {
            this.logger.warn(" 既にクローズしています。");
            return false;
        }

        boolean value = false;
        try {
            // Excelファイルクローズ
            workbook.close();
        } catch (IOException ex) {
            logger.warn(ex, ex);
        } finally {
            if (Objects.isNull(workbook)) {
                value = true;
            }
        }

        return value;
    }


    /**
     * 言語用リスナー作成
     * @param resourceInfoEntity
     * @return
     */
    private NotifySetStringProperty.OnSetValueListener createLocaleListener(LocaleTypeEnum localeType, ResourceInfoEntity resourceInfoEntity) {
        return (String oldValue, String newValue) -> {
            // ×ボタン押下時
            if (StringUtils.isEmpty(newValue)) {
                resourceInfoEntity.setResourceKey(null);
                resourceInfoEntity.setResourceString(null);
                return false;
            }

            try {
                blockUI(true);
                Properties prop = new Properties();
                try {
                    // ロード
                    prop.load(new FileInputStream(newValue));
                } catch (Exception e) {
                    // ファイルが読み込めませんでした
                    resourceInfoEntity.setResourceKey(null);
                    resourceInfoEntity.resourceKeyProperty().setValue(oldValue);
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.LoadPropertyExeption"));
                    return false;
                }

                if (chekLoadLocaleFile(localeType, prop)) return false;

                try (StringWriter writer = new StringWriter()) {
                    // properties情報をString形式に変換
                    prop.store(new PrintWriter(writer), null);
                    String str = writer.getBuffer().toString();
                    resourceInfoEntity.setResourceString(str);
                } catch (Exception e) {
                    // ファイルが読み込めませんでした
                    resourceInfoEntity.setResourceKey(null);

                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.LoadPropertyExeption"));
                    return false;
                }

                resourceInfoEntity.setResourceKey(newValue);
                return true;
            } finally {
                blockUI(false);
            }
        };
    }

    /**
     * プロパティファイルが正しく読み方をチェックする
     * @param localeType プロパティファイルタイプ
     * @param prop プロパティファイル
     * @return true 成功 / false 失敗
     */
    private boolean chekLoadLocaleFile(LocaleTypeEnum localeType, Properties prop) {
        final String value = prop.getProperty("key.LocaleFileTypeInfo");
        if (Objects.isNull(value)) {
            logger.fatal("not found key.LocaleFileTypeInfo");
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.IncorrectFileFromDedicatedTool"));
            return true;
        }

        final List<String> localFileInfos = Arrays.asList(value.split(","));
        if(localFileInfos.size()<2) {
            logger.fatal("key.LocaleFileTypeInfo size Error");
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.IncorrectFileFromDedicatedTool"));
            return true;
        }

        if(!StringUtils.equals(localeType.getLocaleFileType(), localFileInfos.get(0))) {
            logger.fatal("key.LocaleFileTypeInfo size Error");
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.IncorrectLocaleFileType"));
            return true;
        }
        return false;
    }

    /**
     * 設備を検索する。
     * 
     * @param event 
     */    
    @FXML
    private void onSearch(ActionEvent event) {
        if (StringUtils.isEmpty(searchField.getText())) {
            this.createRoot(null, false);
            return;
        }
        
        try {
            blockUI(true);
            
            this.rootItem.getChildren().clear();

            Task task = new Task<List<EquipmentInfoEntity>>() {
                @Override
                protected List<EquipmentInfoEntity> call() throws Exception {

                    EquipmentSearchCondition condition = new EquipmentSearchCondition();
                    condition.setEquipmentName(searchField.getText().trim());
                    condition.setRemoveFlag(false);
                    condition.setWithChildCount(true);
                    List<EquipmentInfoEntity> equipments = equipmentInfoFacade.findSearchRange(condition, null, null);

                    return equipments;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {                       
                        
                        TreeItem<EquipmentInfoEntity> _rootItem = new TreeItem<>(new EquipmentInfoEntity(ROOT_ID, null, LocaleUtils.getString("key.Equipment"), null));
                        hierarchyTree.rootProperty().setValue(_rootItem);
                        
                        long count = this.getValue().size();
                        _rootItem.getValue().setChildCount(count);

                        this.getValue().stream()
                                .filter(o -> Objects.nonNull(o.getEquipmentType()) && o.getEquipmentType() != 0)
                                .sorted((EquipmentInfoEntity o1, EquipmentInfoEntity o2) -> o1.getEquipmentName().compareTo(o2.getEquipmentName()))
                                .forEach(o -> {
                                    TreeItem<EquipmentInfoEntity> item = new TreeItem<>(o);
                                    //if (o.getChildCount() > 0) {
                                    //    item.getChildren().add(new TreeItem());
                                    //    item.expandedProperty().addListener(expandedListener);
                                    //}
                                    _rootItem.getChildren().add(item);
                                });

                        _rootItem.setExpanded(true);

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
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

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

}
