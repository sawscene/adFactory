/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.entity.PlansInfoEntity;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.ScheduleInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ScheduleSearchCondition;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.treecell.OrganizationTreeCell;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 勤務表画面コントローラー
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "RosterCompo", fxmlPath = "/fxml/compo/roster_compo.fxml")
public class RosterCompoController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final static OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
    private final static ScheduleInfoFacade scheduleInfoFacade = new ScheduleInfoFacade();
    private static final CashManager cache = CashManager.getInstance();

    private final SceneContiner sc = SceneContiner.getInstance();
    // 休日表
    private List<HolidayInfoEntity> holidayDatas;
    // 組織ツリー
    private TreeItem<OrganizationInfoEntity> rootItem;
    // 対象組織ID
    private List<Long> selectOrganization = new ArrayList();

    private final static long MAX_ROLL_HIERARCHY_CNT = 30;
    // 組織ツリーのルートID
    private final static long ROOT_ID = 0;
    // 読み込み件数
    private final static long READ_COUNT = 300;

    // 保存確認ダイアログで取消を選択した場合の、階層ツリー移動キャンセルフラグ
    private boolean isCancelMove = false;

    // データフォーマット
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
    private SimpleDateFormat dateCharFormatter;

    // カレンダーボタンの配列
    private final List<List<ToggleButton>> calButton = new ArrayList();

    // 対象年月
    private Calendar targetDate = Calendar.getInstance();
    private Calendar targetDay ;

    // 休日・予定あり・予定なしの背景色と文字色
    private Color colorHolidayChar = Color.web(ProductionNaviPropertyConstants.INIT_SETTING_HOLIDAY_COLOR_CHAR);
    private Color colorHolidayBack = Color.web(ProductionNaviPropertyConstants.INIT_SETTING_HOLIDAY_COLOR_BACK);
    private Color colorPlansChar = Color.web(ProductionNaviPropertyConstants.INIT_SETTING_PLANS_COLOR_CHAR);
    private Color colorPlansBack = Color.web(ProductionNaviPropertyConstants.INIT_SETTING_PLANS_COLOR_BACK);
    private Color colorNoneChar = Color.web(ProductionNaviPropertyConstants.INIT_SETTING_NONE_COLOR_CHAR);
    private Color colorNoneBack = Color.web(ProductionNaviPropertyConstants.INIT_SETTING_NONE_COLOR_BACK);

    // 日にちの非選択時の枠色
    private final Border selectCalOff = new Border(new BorderStroke(Color.GAINSBORO, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
    // 日にちの選択時の枠色
    private final Border selectCalOn = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4, 4, 4, 4)));

    private final Set<Object> blockUIs = new HashSet();

    private boolean isUpdateTree = false;

    @FXML
    private SplitPane rosterPane;
    /** 組織階層 **/
    @FXML
    private TreeView<OrganizationInfoEntity> hierarchyTree;
    /** 予定表のインポートボタン **/
    @FXML
    private Button importPlansButton;
    /** 休日表のインポートボタン **/
    @FXML
    private Button importHolidayButton;
    /** 予定表の削除ボタン **/
    @FXML
    private Button deletePlansButton;
    /** 予定表の編集ボタン **/
    @FXML
    private Button updatePlansButton;
    /** 予定表の新規作成ボタン **/
    @FXML
    private Button createPlansButton;
    /** 指定日 **/
    @FXML
    private Label targetDayText;

    /** 予定表一覧 **/
    @FXML
    private PropertySaveTableView<PlansData> plansList;
    /** 予定表の名称 */
    @FXML
    private TableColumn listColumnPlansName;
    /** 予定表の開始日時 */
    @FXML
    private TableColumn listColumnStartDateTime;
    /** 予定表の終了日時 */
    @FXML
    private TableColumn listColumnStopDateTime;
    /** 組織識別名 */
    @FXML
    private TableColumn listColumnOrganizationName;

    /** 休日の背景色 **/
    @FXML
    private Label LegendHolidayColorBack ;
    /** 予定ありの背景色 **/
    @FXML
    private Label LegendPlansColorBack ;
    /** 予定なしの背景色 **/
    @FXML
    private Label LegendNoneColorBack ;

    /** 今月 */
    @FXML
    private Label linkTargetMonthNow;
    /** 今年 */
    @FXML
    private Label linkTargetYearNow;

    /** プログレス */
    @FXML
    private Pane progressPane;

    @FXML
    private Label labelWeekSubday;
    @FXML
    private Label labelWeekMonday;
    @FXML
    private Label labelWeekTuesday;
    @FXML
    private Label labelWeekWednesday;
    @FXML
    private Label labelWeekThursday;
    @FXML
    private Label labelWeekFriday;
    @FXML
    private Label labelWeekSaturday;

    @FXML
    private ToggleButton calSubday1;
    @FXML
    private ToggleButton calSubday2;
    @FXML
    private ToggleButton calSubday3;
    @FXML
    private ToggleButton calSubday4;
    @FXML
    private ToggleButton calSubday5;
    @FXML
    private ToggleButton calSubday6;
    @FXML
    private ToggleButton calMonday1;
    @FXML
    private ToggleButton calMonday2;
    @FXML
    private ToggleButton calMonday3;
    @FXML
    private ToggleButton calMonday4;
    @FXML
    private ToggleButton calMonday5;
    @FXML
    private ToggleButton calMonday6;
    @FXML
    private ToggleButton calTuesday1;
    @FXML
    private ToggleButton calTuesday2;
    @FXML
    private ToggleButton calTuesday3;
    @FXML
    private ToggleButton calTuesday4;
    @FXML
    private ToggleButton calTuesday5;
    @FXML
    private ToggleButton calTuesday6;
    @FXML
    private ToggleButton calWednesday1;
    @FXML
    private ToggleButton calWednesday2;
    @FXML
    private ToggleButton calWednesday3;
    @FXML
    private ToggleButton calWednesday4;
    @FXML
    private ToggleButton calWednesday5;
    @FXML
    private ToggleButton calWednesday6;
    @FXML
    private ToggleButton calThursday1;
    @FXML
    private ToggleButton calThursday2;
    @FXML
    private ToggleButton calThursday3;
    @FXML
    private ToggleButton calThursday4;
    @FXML
    private ToggleButton calThursday5;
    @FXML
    private ToggleButton calThursday6;
    @FXML
    private ToggleButton calFriday1;
    @FXML
    private ToggleButton calFriday2;
    @FXML
    private ToggleButton calFriday3;
    @FXML
    private ToggleButton calFriday4;
    @FXML
    private ToggleButton calFriday5;
    @FXML
    private ToggleButton calFriday6;
    @FXML
    private ToggleButton calSaturday1;
    @FXML
    private ToggleButton calSaturday2;
    @FXML
    private ToggleButton calSaturday3;
    @FXML
    private ToggleButton calSaturday4;
    @FXML
    private ToggleButton calSaturday5;
    @FXML
    private ToggleButton calSaturday6;

    /**
     * 初期化処理
     *
     * @param url URL
     * @param rb リソース
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(":initialize()");

        dateCharFormatter = new SimpleDateFormat(LocaleUtils.getString("key.ScheduledForDate"));

        plansList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        SplitPaneUtils.loadDividerPosition(rosterPane, getClass().getSimpleName());

        // プログレス非表示
        this.progressPane.setVisible(false);

        this.importHolidayButton.setDisable(false);
        this.importPlansButton.setDisable(false);
        this.createPlansButton.setDisable(false);
        this.updatePlansButton.setDisable(true);
        this.deletePlansButton.setDisable(true);

        // 予定表一覧の初期化
        this.initPlansList();

        try {
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

            this.createCalButton(prop);

            String initFlag = prop.getProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_INIT_MODE, "false");
            if ("true".equals(initFlag)) {
                // 前回値
                try {
                    Date date = this.dateFormatter.parse(prop.getProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_DAY, this.dateFormatter.format(new Date())));
                    this.targetDay = Calendar.getInstance();
                    this.targetDay.setTime(date);

                    String buff = prop.getProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_DATE);
                    logger.trace(" save:" + buff);
                    if (Objects.isNull(buff) || buff.isEmpty()) {
                        buff = this.dateFormatter.format(new Date());
                    }
                    this.targetDate.setTime(dateFormatter.parse(buff));
                } catch (ParseException ex) {
                    logger.fatal(ex, ex);
                    this.targetDay = Calendar.getInstance();
                    this.targetDate = Calendar.getInstance();
                }
            } else {
                this.targetDay = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
                this.targetDate = DateUtils.truncate(Calendar.getInstance(), Calendar.DAY_OF_MONTH);
                prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_DATE, this.dateFormatter.format(this.targetDate.getTime()));
                prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_DAY, this.dateFormatter.format(this.targetDay.getTime()));
            }
            prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_INIT_MODE, "false");

            // カレンダーの設定
            this.setCalendar(this.targetDate);

            // 凡例の設定
            String buff = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_HOLIDAY_COLOR_CHAR, ProductionNaviPropertyConstants.INIT_SETTING_HOLIDAY_COLOR_CHAR);
            this.colorHolidayChar = Color.web(buff);
            buff = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_HOLIDAY_COLOR_BACK, ProductionNaviPropertyConstants.INIT_SETTING_HOLIDAY_COLOR_BACK);
            this.colorHolidayBack = Color.web(buff);
            buff = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_PLANS_COLOR_CHAR, ProductionNaviPropertyConstants.INIT_SETTING_PLANS_COLOR_CHAR);
            this.colorPlansChar = Color.web(buff);
            buff = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_PLANS_COLOR_BACK, ProductionNaviPropertyConstants.INIT_SETTING_PLANS_COLOR_BACK);
            this.colorPlansBack = Color.web(buff);
            buff = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_NONE_COLOR_CHAR, ProductionNaviPropertyConstants.INIT_SETTING_NONE_COLOR_CHAR);
            this.colorNoneChar = Color.web(buff);
            buff = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_NONE_COLOR_BACK, ProductionNaviPropertyConstants.INIT_SETTING_NONE_COLOR_BACK);
            this.colorNoneBack = Color.web(buff);

            this.LegendHolidayColorBack.setTextFill(colorHolidayBack);
            this.LegendPlansColorBack.setTextFill(colorPlansBack);
            this.LegendNoneColorBack.setTextFill(colorNoneBack);

            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        //エンティティメンバーとバインド
        this.listColumnStartDateTime.setCellValueFactory(new PropertyValueFactory<>("scheduleFromDateStr"));
        this.listColumnStopDateTime.setCellValueFactory(new PropertyValueFactory<>("scheduleToDateStr"));
        this.listColumnPlansName.setCellValueFactory(new PropertyValueFactory<>("scheduleName"));
        this.listColumnOrganizationName.setCellValueFactory(new PropertyValueFactory<>("organizationName"));

        // 階層ツリーのフォーカス移動イベント
        hierarchyTree.getFocusModel().focusedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            logger.debug("階層ツリーのフォーカス移動イベント");
            if (isCancelMove) {
                // 移動をキャンセルしたら、元の場所を選択状態にする。
                hierarchyTree.getSelectionModel().select(oldValue.intValue());
            }
        });

        // 階層ツリーのノード選択イベント
        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<OrganizationInfoEntity>> observable, TreeItem<OrganizationInfoEntity> oldValue, TreeItem<OrganizationInfoEntity> newValue) -> {
            logger.debug("階層ツリーのノード選択イベント");
            if (isUpdateTree) {
                // ツリーの表示更新中は何もしない。
                isUpdateTree = false;
                return;
            }
            if (isCancelMove) {
                // 移動キャンセル中は何もしない。
                isCancelMove = false;
                return;
            }

            // 詳細情報を表示する。
            logger.debug("カレンダー更新：組織B");
            setCalendar();
        });

        // 予定表一覧選択時の処理
        //      ※.複数件選択状態で、alt + クリックで選択解除や再選択した時はイベント発生しない
        this.plansList.getSelectionModel().selectedItemProperty().addListener(expandedListener);
        this.plansList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends PlansData> observable, PlansData oldValue, PlansData newValue) -> {
            if (Objects.isNull(plansList.getSelectionModel().getSelectedItems())
                    || plansList.getSelectionModel().getSelectedItems().size() < 1) {
                // 未選択
                this.deletePlansButton.setDisable(true);
                this.updatePlansButton.setDisable(true);
            }
        });

        //予定表一覧クリック
        this.plansList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 2) {
                    this.onPlansEdit(new ActionEvent());
                } else {
                    // カンバン操作ボタンの有効状態を設定する。
                    List<PlansData> datas = plansList.getSelectionModel().getSelectedItems();
                    if (Objects.nonNull(datas) && !datas.isEmpty()) {
                        if (datas.size() == 1) {
                            // 1件選択
                            this.deletePlansButton.setDisable(false);
                            this.updatePlansButton.setDisable(false);
                        } else {
                            // 複数選択
                            this.deletePlansButton.setDisable(false);
                            this.updatePlansButton.setDisable(true);
                        }
                    }
                }
            } else {
                // 未選択
                this.deletePlansButton.setDisable(true);
                this.updatePlansButton.setDisable(true);
            }
        });

        hierarchyTree.setCellFactory((TreeView<OrganizationInfoEntity> o) -> new OrganizationTreeCell());

        // 組織ツリーの表示処理
        this.updateTree(false);
    }

    /**
     * 予定表一覧の初期化
     */
    private void initPlansList() {
        this.plansList.getItems().clear();
        this.plansList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.plansList.getSelectionModel().clearSelection();
    }

    /**
     * カレンダーボタンの生成
     */
    private void createCalButton(Properties prop) {
        List<ToggleButton> list1 = new ArrayList();
        list1.add(this.calSubday1);
        list1.add(this.calMonday1);
        list1.add(this.calTuesday1);
        list1.add(this.calWednesday1);
        list1.add(this.calThursday1);
        list1.add(this.calFriday1);
        list1.add(this.calSaturday1);
        calButton.add(list1);
        List<ToggleButton> list2 = new ArrayList();
        list2.add(this.calSubday2);
        list2.add(this.calMonday2);
        list2.add(this.calTuesday2);
        list2.add(this.calWednesday2);
        list2.add(this.calThursday2);
        list2.add(this.calFriday2);
        list2.add(this.calSaturday2);
        calButton.add(list2);
        List<ToggleButton> list3 = new ArrayList();
        list3.add(this.calSubday3);
        list3.add(this.calMonday3);
        list3.add(this.calTuesday3);
        list3.add(this.calWednesday3);
        list3.add(this.calThursday3);
        list3.add(this.calFriday3);
        list3.add(this.calSaturday3);
        calButton.add(list3);
        List<ToggleButton> list4 = new ArrayList();
        list4.add(this.calSubday4);
        list4.add(this.calMonday4);
        list4.add(this.calTuesday4);
        list4.add(this.calWednesday4);
        list4.add(this.calThursday4);
        list4.add(this.calFriday4);
        list4.add(this.calSaturday4);
        calButton.add(list4);
        List<ToggleButton> list5 = new ArrayList();
        list5.add(this.calSubday5);
        list5.add(this.calMonday5);
        list5.add(this.calTuesday5);
        list5.add(this.calWednesday5);
        list5.add(this.calThursday5);
        list5.add(this.calFriday5);
        list5.add(this.calSaturday5);
        calButton.add(list5);
        List<ToggleButton> list6 = new ArrayList();
        list6.add(this.calSubday6);
        list6.add(this.calMonday6);
        list6.add(this.calTuesday6);
        list6.add(this.calWednesday6);
        list6.add(this.calThursday6);
        list6.add(this.calFriday6);
        list6.add(this.calSaturday6);
        calButton.add(list6);

        // 初期化
        this.initCalButton();

        // 曜日タイトルの設定
        this.setColorWeek(this.labelWeekSubday, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_SUNDAY_BACK), Color.HOTPINK, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_SUNDAY_CHAR), Color.BLACK);
        this.setColorWeek(this.labelWeekMonday, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_MONDAY_BACK), Color.LIGHTCYAN, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_MONDAY_CHAR), Color.BLACK);
        this.setColorWeek(this.labelWeekTuesday, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_TUESDAY_BACK), Color.LIGHTCYAN, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_TUESDAY_CHAR), Color.BLACK);
        this.setColorWeek(this.labelWeekWednesday, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_WEDNESDAY_BACK), Color.LIGHTCYAN, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_WEDNESDAY_CHAR), Color.BLACK);
        this.setColorWeek(this.labelWeekThursday, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_THURSDAY_BACK), Color.LIGHTCYAN, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_THURSDAY_CHAR), Color.BLACK);
        this.setColorWeek(this.labelWeekFriday, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_FRIDAY_BACK), Color.LIGHTCYAN, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_FRIDAY_CHAR), Color.BLACK);
        this.setColorWeek(this.labelWeekSaturday, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_SATURDAY_BACK), Color.LAVENDER, prop.getProperty(ProductionNaviPropertyConstants.KEY_ROSTER_COLOR_SATURDAY_CHAR), Color.BLACK);
    }

    /**
     * 曜日タイトルの色の設定
     *
     * @param _label ラベル
     * @param _colorBack 背景色(256)
     * @param _defultBack 背景色(デフォルト)
     * @param _colorChar 文字色(256)
     * @param _defultChar 文字色(デフォルト)
     */
    private void setColorWeek(Label _label, String _colorBack, Color _defultBack, String _colorChar, Color _defultChar) {

        // 背景色
        Color color = _defultBack;
        try {
            color = Color.web(_colorBack);
        } catch (Exception e) {
            color = _defultBack;
        } finally {
            _label.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        }

        // 文字色
        color = _defultChar;
        try {
            color = Color.web(_colorChar);
        } catch (Exception e) {
            color = _defultChar;
        } finally {
            _label.setTextFill(color);
        }
    }

    /**
     * カレンダーボタンの初期化
     */
    private void initCalButton() {
        // 月の第何週の配列
        for (int i = 0; i < this.calButton.size(); i++) {
            // 曜日の配列
            for (int j = 0; j < this.calButton.get(i).size(); j++) {
                this.calButton.get(i).get(j).setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
                this.setCalendarSelect(i, j, false);
                this.calButton.get(i).get(j).setVisible(true);
                this.calButton.get(i).get(j).setText("");
                // 非表示にする
                this.calButton.get(i).get(j).setDisable(true);
                this.calButton.get(i).get(j).setSelected(false);
                this.calButton.get(i).get(j).setTooltip(new Tooltip());
            }
            logger.debug(" day count" + this.calButton.get(i).size());
        }
        logger.debug(" week count:" + this.calButton.size());
        targetDayText.setText("");
    }

    /**
     * カレンダーの表示 ※：組織の変更
     */
    private void setCalendar() {
        logger.info(":setCalendar()");
        Object obj = new Object();
        try {
            blockUI(obj, true);

            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())) {
                logger.info("  no selected");
                return;
            }
            // 予定表一覧初期化
            this.plansList.getItems().clear();

            final Long selectId = hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId();
            logger.info("  selected Id:" + selectId);

            final Calendar target = this.getDate();

            // カレンダー表示処理
            Task task = new Task<List<CalendarData>>() {

                @Override
                protected List<CalendarData> call() throws Exception {
                    logger.info("Task<List<CalendarData>>().call()");
                    List<CalendarData> datas;
                    try {
                        // 組織情報の取得
                        this.getOrganization(selectId);
                    } catch (NumberFormatException e) {
                        logger.fatal(e, e);
                    } finally {
                        // カレンダーの表示処理
                        logger.debug(" selected count C:" + selectOrganization.size());
                        datas = this.showCalendar((Calendar) targetDate.clone());
                    }
                    return datas;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        logger.info("Task<List<CalendarDasata>>().succeeded()");
                        logger.trace("  select ID:" + selectId + "");
                        logger.trace("  count:" + selectOrganization.size());
                        logger.trace(" cal count=" + this.getValue().size());

                        this.getValue().stream().forEach((_day) -> {
                            int col = _day.getDay().get(Calendar.DAY_OF_WEEK) - 1;
                            int row = _day.getDay().get(Calendar.WEEK_OF_MONTH) - 1;

                            logger.debug("date: " + _day.getDay().getTime().toString() + ", mode:" + _day.getMode() + ", lock:" + _day.getLock());
                            calButton.get(_day.getDay().get(Calendar.WEEK_OF_MONTH) - 1).get(col).setText(String.valueOf(_day.getDay().get(Calendar.DAY_OF_MONTH)));
                            logger.debug("  > 日付設定");
                            switch (_day.getMode()) {
                                case CalendarData.MODE_PLANS:
                                    calButton.get(row).get(col).setBackground(new Background(new BackgroundFill(colorPlansBack, CornerRadii.EMPTY, Insets.EMPTY)));
                                    logger.debug("  > 背景色設定(予定あり)");
                                    calButton.get(row).get(col).setTextFill(colorPlansChar);
                                    logger.debug("  > 文字色設定(予定あり)");
                                    break;
                                case CalendarData.MODE_HOLIDAY:
                                    calButton.get(row).get(col).setBackground(new Background(new BackgroundFill(colorHolidayBack, CornerRadii.EMPTY, Insets.EMPTY)));
                                    logger.debug("  > 背景色設定(休日)");
                                    calButton.get(row).get(col).setTextFill(colorHolidayChar);
                                    logger.debug("  > 文字色設定(休日)");
                                    break;
                                default:
                                    calButton.get(row).get(col).setBackground(new Background(new BackgroundFill(colorNoneBack, CornerRadii.EMPTY, Insets.EMPTY)));
                                    logger.debug("  > 背景色設定(予定なし)");
                                    calButton.get(row).get(col).setTextFill(colorNoneChar);
                                    logger.debug("  > 文字色設定(予定なし)");
                                    break;
                            }

                            // 今回の日にちを選択表示
                            if (Objects.nonNull(target) && target.equals(_day.getDay())) {
                                setCalendarSelect(row, col, true);
                            } else {
                                setCalendarSelect(row, col, false);
                            }
                            logger.debug("　> 線設定");
                            calButton.get(row).get(col).setTooltip(_day.getTooltip());
                            logger.debug("  > ツールチップ設定");
                            calButton.get(row).get(col).setDisable(_day.getLock());
                        });

                        // 一覧表示
                        if (targetDate.get(Calendar.YEAR) == targetDay.get(Calendar.YEAR) && targetDate.get(Calendar.MONTH) == targetDay.get(Calendar.MONTH)) {
                            // 一覧の表示
                            setTargetDay(targetDay, targetDay.get(Calendar.WEEK_OF_MONTH), targetDay.get(Calendar.DAY_OF_WEEK));
                        } else {
                            targetDayText.setText("");
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    logger.info(" Task<List<CalendarData>>().failed()");
                    blockUI(obj, false);
                }

                // 組織情報の取得
                private void getOrganization(long organizationId) {
                    // 予定表の検索条件設定
                    ScheduleSearchCondition scheduleSearchCondition = new ScheduleSearchCondition();
                    // 選択した組織IDを取得
                    selectOrganization = new ArrayList();
                    if (organizationId > 0) {
                        logger.info(" 組織ID=" + organizationId);
                        List<OrganizationInfoEntity> organizationDatas = new ArrayList();
                        selectOrganization.add(organizationId);

                        // 子組織の件数を取得する。
                        long count = organizationInfoFacade.getAffilationHierarchyCount(organizationId);
                        logger.trace(" 子組織の件数:" + count);

                        // 対象組織情報を取得（子の組織も）
                        for (long from = 0; from <= count; from += MAX_ROLL_HIERARCHY_CNT) {
                            organizationDatas = organizationInfoFacade.getAffilationHierarchyRange(organizationId, from, from + MAX_ROLL_HIERARCHY_CNT - 1);
                            organizationDatas.stream().forEach((e) -> {
                                selectOrganization.add(e.getOrganizationId());
                            });
                        }
                        logger.trace(" 子組織の件数:" + selectOrganization.size());

                        scheduleSearchCondition.setOrganizationIdCollection(selectOrganization);

                        // 検索条件の保持
                        try {
                            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
                            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

                            prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_DATE, dateFormatter.format(targetDate.getTime()));
                            prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_ORGANIZATION, String.valueOf(organizationId));

                            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
                        } catch (IOException ex) {
                            logger.fatal(ex, ex);
                        }
                    }
                }

                // カレンダーの表示処理
                private List<CalendarData> showCalendar(Calendar _calendar) {
                    List<CalendarData> value = new ArrayList();
                    try {
                        boolean holidayFlg = false;             // 休日フラグ
                        boolean plansFlg = false;               // 予定ありフラグ
                        long scheduleCount = 0;                 // 予定表件数

                        int dateMax = _calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        logger.debug(" max:" + dateMax);

                        Date startDate = _calendar.getTime();
                        startDate = DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
                        startDate = DateUtils.setDays(startDate, 1);
                        Date stopDate = _calendar.getTime();
                        stopDate = DateUtils.truncate(stopDate, Calendar.DAY_OF_MONTH);
                        stopDate = DateUtils.setDays(stopDate, 1);
                        stopDate = DateUtils.addMonths(stopDate, 1);
                        stopDate = DateUtils.addSeconds(stopDate, -1);

                        // 1日に設定し、時間を00:00:00にする。
                        _calendar.set(Calendar.DAY_OF_MONTH, 1);
                        logger.debug(" date:" + _calendar.get(Calendar.YEAR) + "/" + (_calendar.get(Calendar.MONTH) + 1) + "/" + _calendar.get(Calendar.DAY_OF_MONTH));

                        // 予定表の検索条件設定
                        ScheduleSearchCondition scheduleSearchCondition = new ScheduleSearchCondition();
                        scheduleSearchCondition.setOrganizationIdCollection(selectOrganization);

                        boolean lock = false;

                        if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())) {
                            lock = true;
                        }

                        // 予定表の取得
                        List<ScheduleInfoEntity> plansDatas = new ArrayList();
                        if (Objects.nonNull(selectOrganization) && selectOrganization.size() > 0) {
                            scheduleSearchCondition.fromDate(startDate);
                            scheduleSearchCondition.toDate(stopDate);
                            logger.debug(" 予定表の検索期間:" + startDate.toString() + "～" + stopDate.toString());

                            // 予定表の件数取得（１ヶ月分）
                            Long count = scheduleInfoFacade.searchCount(scheduleSearchCondition);
                            for (long nowCount = 0l; nowCount < count; nowCount += READ_COUNT) {
                                plansDatas.addAll(scheduleInfoFacade.searchRange(scheduleSearchCondition, nowCount, nowCount + READ_COUNT - 1));
                            }
                        }
                        logger.debug(" 予定表:" + plansDatas.size());

                        for (int i = 1; i <= dateMax; i++) {
                            startDate = DateUtils.setDays(startDate, i);
                            stopDate = DateUtils.setDays(stopDate, i);
                            _calendar.setTime(startDate);
                            final Date targetDate = startDate;
                            scheduleCount = 0;
                            holidayFlg = false;
                            plansFlg = false;

                            logger.debug(" --------------------------------------------------");
                            logger.trace(" --> target Date Start:" + startDate);
                            logger.trace(" --> target Date Stop:" + stopDate);

                            // 休日検索
                            Optional<HolidayInfoEntity> holidayList = holidayDatas.stream()
                                    .filter(item -> item.getHolidayDate().equals(targetDate))
                                    .findFirst();
                            try {
                                if (holidayList.isPresent() && Objects.nonNull(holidayList.get())) {
                                    HolidayInfoEntity data = holidayList.get();
                                    holidayFlg = true;
                                    logger.debug(" 休日情報:" + data.toString());
                                }
                            } catch (Exception ex) {
                                holidayFlg = false;
                                logger.fatal(ex, ex);
                            }

                            // 予定表検索
                            final Date s = startDate;
                            final Date e = stopDate;
                            logger.trace(" date check:" + s.toString() + " - " + e.toString());

                            List<ScheduleInfoEntity> plansList = plansDatas.stream()
                                    .filter(_item
                                            -> (DateUtils.truncate(_item.getScheduleFromDate(), Calendar.DAY_OF_MONTH).compareTo(s) <= 0
                                            && DateUtils.truncate(_item.getScheduleToDate(), Calendar.DAY_OF_MONTH).compareTo(s) >= 0)
                                            || (DateUtils.truncate(_item.getScheduleFromDate(), Calendar.DAY_OF_MONTH).compareTo(e) >= 0
                                            && DateUtils.truncate(_item.getScheduleToDate(), Calendar.DAY_OF_MONTH).compareTo(e) <= 0))
                                    //                            .filter(_item -> (_item.getScheduleFromDate().after(s) && _item.getScheduleToDate().before(e)) || (_item.getScheduleFromDate().before(s) && _item.getScheduleToDate().after(e)) )
                                    .collect(Collectors.toList());
                            try {
                                if (Objects.nonNull(plansList) && plansList.size() > 0) {
                                    logger.debug(" plansList ==>" + plansFlg + ", " + plansList + ", " + plansList.size());
                                    plansFlg = true;
                                }
                            } catch (Exception ex) {
                                plansFlg = false;
                            }

                            logger.debug(" flg  ==>" + holidayFlg + ", " + plansFlg);
                            logger.debug(" idx  ==>" + _calendar.get(Calendar.WEEK_OF_MONTH) + ", " + _calendar.get(Calendar.DAY_OF_WEEK) + ", " + _calendar.get(Calendar.DAY_OF_MONTH));

                            Calendar day = Calendar.getInstance();
                            day.setTimeInMillis(_calendar.getTimeInMillis());
                            Tooltip tooltip;
                            int mode;
                            String text = String.valueOf(_calendar.get(Calendar.DAY_OF_MONTH));

                            if (holidayFlg) {
                                // 休日
                                mode = CalendarData.MODE_HOLIDAY;
                                tooltip = createTooltip(_calendar, (holidayList.isPresent()) ? holidayList.get() : null, plansList);
                            } else if (plansFlg) {
                                // 予定あり
                                mode = CalendarData.MODE_PLANS;
                                tooltip = createTooltip(_calendar, (holidayList.isPresent()) ? holidayList.get() : null, plansList);
                            } else {
                                // 予定なし
                                mode = CalendarData.MODE_NONE;
                                tooltip = createTooltip(_calendar, null, scheduleCount);
                            }

                            CalendarData data = new CalendarData(day, lock, mode, text, tooltip);
                            value.add(data);

                            if (dateMax == _calendar.get(Calendar.DAY_OF_MONTH)) {
                                logger.debug(" 抜ける:" + _calendar.get(Calendar.YEAR) + "/" + (_calendar.get(Calendar.MONTH) + 1) + "/" + _calendar.get(Calendar.DAY_OF_MONTH));
                                break;
                            }

                            logger.debug(" 日付設定:" + calButton.get(_calendar.get(Calendar.WEEK_OF_MONTH) - 1).get(_calendar.get(Calendar.DAY_OF_WEEK) - 1).getText());
                            _calendar.add(Calendar.DAY_OF_MONTH, 1);

                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                    return value;
                }

            };
            new Thread(task).start();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
    }

    /**
     * 指定日の取得
     *
     * @return 指定日
     */
    private Calendar getDate() {
        Calendar value = null;
        if (Objects.nonNull(this.targetDay)) {
            value = Calendar.getInstance();
            value.setTime(this.targetDay.getTime());
        }
        return value;
    }

    /**
     * ツールチップの作成
     *
     * @param calendar 対象日
     * @param holidayInfo 休日情報
     * @param scheduleInfo 予定表情報
     * @return ツールチップ
     */
    private Tooltip createTooltip(Date calendar, HolidayInfoEntity holidayInfo, List<ScheduleInfoEntity> scheduleInfo) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(calendar);
        return createTooltip(cal, holidayInfo, Objects.nonNull(scheduleInfo) ? (long) scheduleInfo.size() : (long) 0);
    }

    /**
     * ツールチップの作成
     *
     * @param calendar 対象日
     * @param holidayInfo 休日情報
     * @param scheduleInfo 予定表情報
     * @return ツールチップ
     */
    private Tooltip createTooltip(Calendar calendar, HolidayInfoEntity holidayInfo, List<ScheduleInfoEntity> scheduleInfo) {
        return createTooltip(calendar, holidayInfo, Objects.nonNull(scheduleInfo) ? (long) scheduleInfo.size() : (long) 0);
    }

    /**
     * ツールチップの作成
     *
     * @param calendar 対象日
     * @param holidayInfo 休日情報
     * @param scheduleInfo 予定表件数
     * @return ツールチップ
     */
    private Tooltip createTooltip(Calendar calendar, HolidayInfoEntity holidayInfo, long scheduleCount) {
        StringBuilder buff = new StringBuilder();

        buff.append(calendar.get(Calendar.DAY_OF_MONTH)).append("日");

        if (Objects.nonNull(holidayInfo) && !holidayInfo.getHolidayName().isEmpty()) {
            buff.append("\r\n");
            buff.append(holidayInfo.getHolidayName());
        }
        if (scheduleCount > 0) {
            buff.append("\r\n");
            buff.append("予定あり：").append(scheduleCount).append("件");
        } else {
            buff.append("\r\n");
            buff.append("予定なし：");
        }

        return new Tooltip(buff.toString());
    }

    /**
     * カレンダーの表示処理 ※：年月を変更した場合
     *
     * @param calendar 対象年月
     */
    private void setCalendar(Calendar calendar) {
        logger.info(":setTargetDate start");
        this.targetDate = calendar;

        // 組織が選択されている？
        if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId().equals(ROOT_ID)) {
            logger.warn(" > Please select an organization.");
        } else {
            logger.debug(" >> select Organization Id:" + hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId());
        }

        this.initCalButton();

        this.linkTargetYearNow.setText(String.format(LocaleUtils.getString("key.Nen"), String.valueOf(calendar.get(Calendar.YEAR))));
        this.linkTargetMonthNow.setText(LocaleUtils.getString("key."+String.valueOf(calendar.get(Calendar.MONTH) + 1)+"Gatsu"));

        // 休日表
        this.holidayDatas = cache.getItemList(HolidayInfoEntity.class, new ArrayList());

        logger.debug(" holidayInfoFacade.search count=" + this.holidayDatas == null ? null : this.holidayDatas.size());

        logger.info(":setTargetDate end");
    }

    /**
     * ツリーのルートの表示を更新する。
     *
     * @param selectedId 更新後に選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合はルートを選択。)
     */
    private void createRoot(Long selectedId) {
        logger.info(":createRoot start (" + selectedId + ")");
        Object obj = new Object();
        try {
            blockUI(obj, true);

            // ルートが存在しない場合は新規作成する。
            if (Objects.isNull(this.rootItem)) {
                this.rootItem = new TreeItem<>(new OrganizationInfoEntity(ROOT_ID, null, LocaleUtils.getString("key.Organization"), null));
            }

            this.rootItem.getChildren().clear();

            Task task = new Task<List<OrganizationInfoEntity>>() {
                @Override
                protected List<OrganizationInfoEntity> call() throws Exception {
                    logger.debug(" > ツリーのルートの表示を更新");
                    // 子組織の件数を取得する。
                    long count = organizationInfoFacade.getTopHierarchyCount();
                    logger.debug(" > 子組織の件数:" + count);

                    // 第一階層の取得
                    List<OrganizationInfoEntity> organizations = new ArrayList();
                    for (long from = 0; from <= count; from += MAX_ROLL_HIERARCHY_CNT) {
                        List<OrganizationInfoEntity> entities = organizationInfoFacade.getTopHierarchyRange(from, from + MAX_ROLL_HIERARCHY_CNT - 1);
                        organizations.addAll(entities);
                    }

                    return organizations;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        logger.debug("  > selectedId:" + selectedId);
                        long count = this.getValue().size();
                        logger.debug(" > 件数:" + this.getValue().size());
                        rootItem.getValue().setChildCount(count);

                        // 第一階層を設定する。
                        for (OrganizationInfoEntity data : this.getValue()) {
                            TreeItem<OrganizationInfoEntity> item = new TreeItem<>(data);
                            if (data.getChildCount() > 0) {
                                item.getChildren().add(new TreeItem());
                            }
                            item.expandedProperty().addListener(expandedListener);
                            rootItem.getChildren().add(item);
                        }

                        TreeItem<OrganizationInfoEntity> selectNode = rootItem;
                        // ルート以外選択
                        if (selectedId > ROOT_ID) {
                            // 前回選択した親階層の情報を取得し、上位順にする。
                            List<List<OrganizationInfoEntity>> hierarchyAddIds = new ArrayList<>();
                            long id = selectedId;
                            while (true) {
                                List<OrganizationInfoEntity> datas = new ArrayList<>();
                                OrganizationInfoEntity data = CacheUtils.getCacheOrganization(id);
                                if (Objects.isNull(data) || Objects.isNull(data.getParentId())) {
                                    break;
                                }
                                
                                if (Objects.equals(data.getParentId(), ROOT_ID)) {
                                    break;
                                }

                                count = organizationInfoFacade.getAffilationHierarchyCount(data.getParentId());
                                for (long from = 0; from <= count; from += MAX_ROLL_HIERARCHY_CNT) {
                                    List<OrganizationInfoEntity> entities = organizationInfoFacade.getAffilationHierarchyRange(data.getParentId(), from, from + MAX_ROLL_HIERARCHY_CNT - 1);
                                    datas.addAll(entities);
                                }

                                id = data.getParentId();
                                hierarchyAddIds.add(0, datas);
                            }

                            // 再帰的に選択にする
                            TreeItem<OrganizationInfoEntity> item = treeForExpanding(rootItem, hierarchyAddIds, selectedId);
                            if (Objects.nonNull(item)) {
                                selectNode = item;
                            }
                        }

                        hierarchyTree.rootProperty().setValue(rootItem);
                        logger.debug(" 組織ツリーの件数:" + hierarchyTree.getRoot().getChildren().size() + ", " + rootItem.getChildren().size());
                        hierarchyTree.refresh();

                        // 親ノードが閉じている場合、ノードを開状態にする。(開閉イベントは一旦削除して、開いた後で再登録)
                        if (!rootItem.isExpanded()) {
                            rootItem.expandedProperty().removeListener(expandedListener);
                            rootItem.setExpanded(true);
                            rootItem.expandedProperty().addListener(expandedListener);
                        }

                        // 選択ノードが見えるようスクロール
                        hierarchyTree.scrollTo(hierarchyTree.getSelectionModel().getSelectedIndex());

                        logger.debug("  > 選択状態 selectedId:" + selectedId);
                        logger.debug("   >>  " + selectNode.getValue().toString());
                        isUpdateTree = false;
                        if (Objects.nonNull(selectedId) && !selectedId.equals(ROOT_ID)) {
                            // 指定されたノードを選択状態にする。
                            selectedTreeItem(selectNode, selectedId);
                        } else {
                            // 選択ノードの指定がない場合は、ルートを選択状態にする。
                            hierarchyTree.getSelectionModel().select(rootItem);
                        }

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        // 選択状態
                        setCalendarSelect(targetDay, true);
                        blockUI(obj, false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    blockUI(obj, false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
        logger.info(":createRoot end.");
    }

    /**
     * ツリーを作成（再帰的）
     *
     * @param _item アイテム
     * @param _list 子階層のリスト
     * @param _selectId 組織ID
     */
    private TreeItem<OrganizationInfoEntity> treeForExpanding(final TreeItem<OrganizationInfoEntity> _item, List<List<OrganizationInfoEntity>> _list, Long _selectId) {
        logger.debug(":treeForExpanding start");
        if (Objects.isNull(_list) || _list.isEmpty() || _list.get(0).isEmpty()) {
            logger.debug(" リストデータなし");
            return null;
        }

        TreeItem<OrganizationInfoEntity> value = null;

        logger.debug("  ●追加する子の階層件数:" + _list.size());
        for (TreeItem<OrganizationInfoEntity> item : _item.getChildren()) {
            logger.debug("  > ツリー情報:" + item.getValue().toString());
            // データ無ければ次へ
            if (Objects.isNull(item) || _list.isEmpty() || _list.get(0).isEmpty()) {
                logger.debug("   >> データ無ければ次へ");
                continue;
            }

            // リストの情報なし
            if (_list.isEmpty() || _list.get(0).isEmpty()) {
                logger.debug("   >> リストの情報なし");
                break;
            }

            logger.debug("   >> ID check:" + item.getValue().getOrganizationId() + ", " + _list.get(0).get(0).getParentId());
            if (item.getValue().getOrganizationId().equals(_list.get(0).get(0).getParentId())) {
                logger.debug("   >> 階層一致:" + item.getValue().getOrganizationId());

                value = item;
                item.expandedProperty().removeListener(expandedListener);

                // 子の階層を削除
                item.getChildren().clear();

                // ツリーを選択状態にする
                item.setExpanded(true);

                for (OrganizationInfoEntity data : _list.get(0)) {
                    logger.debug("     >>> 子の追加情報:" + data.toString());
                    TreeItem<OrganizationInfoEntity> itemChilde = new TreeItem<>(data);
                    if (data.getChildCount() > 0) {
                        itemChilde.getChildren().add(new TreeItem());
                    }
                    itemChilde.expandedProperty().addListener(expandedListener);
                    item.getChildren().add(itemChilde);

                    if (data.getOrganizationId().equals(_selectId)) {
                        itemChilde.setExpanded(true);
                    }
                }

                item.expandedProperty().addListener(expandedListener);

                if (item.getValue().getOrganizationId().equals(_list.get(0).get(0).getOrganizationId())) {
                    value = item;
                }

                // 先頭のリストを削除
                logger.debug(" list count Before:" + _list.size());
                _list.remove(0);
                logger.debug(" list count After:" + _list.size());

                // 再帰的に呼び出す
                if (Objects.nonNull(_list) && _list.size() > 0) {
                    TreeItem<OrganizationInfoEntity> value1 = treeForExpanding(item, _list, _selectId);
                    if (Objects.nonNull(value1)) {
                        value = value1;
                    }
                }
            }

        }

        logger.debug(":treeForExpanding end");
        return value;
    }

    /**
     * 組織IDが一致するTreeItemを選択する (存在しない場合は親を選択する(削除後の選択用))
     *
     * @param parentItem 選択状態にするノーノの親ノード
     * @param selectedId 選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void selectedTreeItem(TreeItem<OrganizationInfoEntity> parentItem, Long selectedId) {
        logger.info(":selectedTreeItem start");
        Optional<TreeItem<OrganizationInfoEntity>> find = parentItem.getChildren().stream().
                filter(p -> p.getValue().getOrganizationId().equals(selectedId)).findFirst();

        if (find.isPresent()) {
            this.hierarchyTree.getSelectionModel().select(find.get());
        } else {
            this.hierarchyTree.getSelectionModel().select(parentItem);
        }
        this.hierarchyTree.scrollTo(this.hierarchyTree.getSelectionModel().getSelectedIndex());// 選択ノードが見えるようスクロール
        logger.info(":selectedTreeItem end");
    }

    /**
     * ツリーノード開閉イベントリスナー
     */
    private final ChangeListener expandedListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            logger.info(":expandedListener.changed start");
            // ノードを開いたら子ノードの情報を再取得して表示する。
            if (Objects.nonNull(newValue) && newValue.equals(true)) {
                TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
                expand(treeItem, null, false);
            }
            logger.info(":expandedListener.changed end");
        }
    };

    /**
     * ツリーの指定したノードの表示を更新する。
     *
     * @param parentItem 表示更新するノード
     * @param selectedId 選択状態にするノードの組織ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     * @param calFlg カレンダー更新フラグ
     */
    private void expand(TreeItem<OrganizationInfoEntity> parentItem, Long selectedId, boolean calFlg) {
        logger.debug("expand: parentItem={}", parentItem.getValue());
        Object obj = new Object();
        try {
            blockUI(obj, true);

            parentItem.getChildren().clear();

            final long parentId = parentItem.getValue().getOrganizationId();
            logger.debug("  組織ID:" + parentId);

            Task task = new Task<List<OrganizationInfoEntity>>() {
                @Override
                protected List<OrganizationInfoEntity> call() throws Exception {
                    // 子組織の件数を取得する。
                    long count = organizationInfoFacade.getAffilationHierarchyCount(parentId);
                    logger.debug("  組織ID:" + parentId + ", 子の組織の件数:" + count);

                    List<OrganizationInfoEntity> organizations = new ArrayList();
                    for (long from = 0; from <= count; from += MAX_ROLL_HIERARCHY_CNT) {
                        List<OrganizationInfoEntity> entities = organizationInfoFacade.getAffilationHierarchyRange(parentId, from, from + MAX_ROLL_HIERARCHY_CNT - 1);
                        organizations.addAll(entities);
                    }
                    logger.debug("  組織ID:" + parentId + ", 子の組織の件数:" + count + ", " + organizations.size());
                    return organizations;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        this.getValue().stream().forEach((entity) -> {
                            logger.debug("  組織情報:" + entity.toString());
                            TreeItem<OrganizationInfoEntity> item = new TreeItem<>(entity);
                            logger.debug("  組織の子の件数:" + entity.getChildCount());
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

                            // カレンダー更新
                            if (calFlg) {
                                logger.debug("カレンダー更新：全体A");
                                setCalendar(targetDate);
                            }
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    blockUI(obj, false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
    }

    /**
     * UIロック
     *
     * @param flg 表示フラグ
     */
    private void blockUI(Boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        progressPane.setVisible(flg);
    }

    /**
     * 
     * @param obj
     * @param flg 
     */
    private void blockUI(Object obj, Boolean flg) {
        if (flg) {
            blockUIs.add(obj);
        } else {
            blockUIs.remove(obj);
        }

        blockUI(!blockUIs.isEmpty());
    }

    /**
     * 休日表のインポートボタンのクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onImportHoliday(ActionEvent event) {
        logger.info(":onImportHoliday start");
        sc.setComponent("ContentNaviPane", "HolidayImportCompo");
        logger.info(":onImportHoliday end");
    }

    /**
     * 予定表のインポートボタンのクリックイベント
     *
     * @param event
     */
    @FXML
    private void onImportPlans(ActionEvent event) {
        logger.info(":onImportPlans start");
        sc.setComponent("ContentNaviPane", "PlansImportCompo");
        logger.info(":onImportPlans end");
    }

    /**
     * 予定表の新規作成ボタンのクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onPlansCreate(ActionEvent event) {
        logger.info(":onPlansCreate start");
        Object obj = new Object();
        try {
            blockUI(obj, true);

            Date startDate = this.targetDate.getTime();
            Date stopDate = this.targetDate.getTime();
            stopDate = DateUtils.truncate(stopDate, Calendar.DAY_OF_MONTH);
            stopDate = DateUtils.addDays(stopDate, 1);
            stopDate = DateUtils.addSeconds(stopDate, -1);

            ScheduleInfoEntity entity = new ScheduleInfoEntity();
            entity.setScheduleFromDate(startDate);
            entity.setScheduleToDate(stopDate);
            logger.trace(" fromDate:" + entity.getScheduleFromDate().toString());
            logger.trace(" toDate:" + entity.getScheduleToDate().toString());

            List<OrganizationInfoEntity> organizationDatas = new ArrayList();
            logger.debug(" select organization :" + hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId());
            if (!hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId().equals(ROOT_ID)) {
                organizationDatas.add(hierarchyTree.getSelectionModel().getSelectedItem().getValue());
            }
            logger.trace(" select organization :" + organizationDatas.size());
            logger.trace(" select organization :" + hierarchyTree.getSelectionModel().getSelectedItem().getValue());

            final PlansInfoEntity data = new PlansInfoEntity(LocaleUtils.getString("key.PlansCreate"), entity, organizationDatas);
            logger.trace(" Set Data:" + data.toString() + ", count:" + organizationDatas.size());

            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.PlansCreate"), "PlansCreateCompo", data);
            if (ButtonType.OK.equals(ret)) {
                logger.trace(" == input ScheduleName:" + (Objects.isNull(data.getScheduleName()) ? null : data.getScheduleName()));
                logger.trace(" == input getScheduleFromDate:" + (Objects.isNull(data.getScheduleFromDate()) ? null : data.getScheduleFromDate().toString()));
                logger.trace(" == input getScheduleToDate:" + (Objects.isNull(data.getScheduleToDate()) ? null : data.getScheduleToDate().toString()));
                logger.trace(" == input getFkOrganizationId:" + (Objects.isNull(data.getFkOrganization()) ? null : data.getFkOrganization().size()));

                // データ登録
                ScheduleInfoEntity inputScheduleInfoEntity = new ScheduleInfoEntity();
                inputScheduleInfoEntity.setScheduleName(data.getScheduleName());
                inputScheduleInfoEntity.setScheduleFromDate(data.getScheduleFromDate());
                inputScheduleInfoEntity.setScheduleToDate(data.getScheduleToDate());

                ResponseEntity res;
                for (OrganizationInfoEntity id : data.getFkOrganization()) {
                    inputScheduleInfoEntity.setFkOrganizationId(id.getOrganizationId());
                    res = scheduleInfoFacade.regist(inputScheduleInfoEntity);

                    if (Objects.isNull(res) || !ResponseAnalyzer.getAnalyzeResult(res)) {
                        //TODO:エラー時の処理
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.PlansCreate"),
                                String.format(LocaleUtils.getString("key.FailedToCreate"), LocaleUtils.getString("key.Plans")));
                        break;
                    }
                }

                //リスト更新
                this.setCalendar();
                if (Objects.nonNull(this.targetDay)) {
                    this.setTargetDay(this.targetDay);
                }
            } else {
                logger.info(" input cancel");
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(obj, false);
        }

        logger.info(":onPlansCreate end");
    }

    /**
     * 予定表の編集ボタンのクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onPlansEdit(ActionEvent event) {
        logger.info(":onPlansUpdate start.");

        Object obj = new Object();
        try {
            blockUI(obj, true);

            if (Objects.isNull(plansList.getSelectionModel().getSelectedItem())) {
                return;
            }

            PlansData plansData = plansList.getSelectionModel().getSelectedItem();
            logger.debug(" target ScheduleId:" + plansData.getScheduleId() + ", " + plansData.getEntity().getFkOrganizationId());

            logger.trace(" 現在日以降？  予定開始日:" + DateUtils.truncate(plansData.getEntity().getScheduleFromDate(), Calendar.DAY_OF_MONTH).toString() + ",  現在日:" + new Date().toString());
            logger.trace(" 現在日以降？  予定開始日:" + DateUtils.truncate(plansData.getEntity().getScheduleFromDate(), Calendar.DAY_OF_MONTH).toString() + ",  現在日:" + new Date().toString());
            if (DateUtils.truncate(plansData.getEntity().getScheduleFromDate(), Calendar.DAY_OF_MONTH).compareTo(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) < 0 && DateUtils.truncate(plansData.getEntity().getScheduleToDate(), Calendar.DAY_OF_MONTH).compareTo(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) < 0) {
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.PlansUpdate"), String.format(LocaleUtils.getString("key.alert.past.day"), LocaleUtils.getString("key.PlansUpdate")));
                return;
            }

            OrganizationInfoEntity organizationData = organizationInfoFacade.find(plansData.fkOrganizationId);

            final PlansInfoEntity data = new PlansInfoEntity(LocaleUtils.getString("key.PlansUpdate"), plansData.getEntity(), organizationData);
            logger.debug(" Set Data:" + data.toString() + ", count:" + plansData.getFkOrganizationId());

            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.PlansUpdate"), "PlansCreateCompo", data);
            if (ret.equals(ButtonType.OK)) {
                logger.trace(" == input ScheduleID:" + (Objects.isNull(data.getScheduleId()) ? null : data.getScheduleId()));
                logger.trace(" == input ScheduleName:" + (Objects.isNull(data.getScheduleName()) ? null : data.getScheduleName()));
                logger.trace(" == input getScheduleFromDate:" + (Objects.isNull(data.getScheduleFromDate()) ? null : data.getScheduleFromDate().toString()));
                logger.trace(" == input getScheduleToDate:" + (Objects.isNull(data.getScheduleToDate()) ? null : data.getScheduleToDate().toString()));
                logger.trace(" == input getFkOrganizationId:" + (Objects.isNull(data.getFkOrganization()) ? null : data.getFkOrganization().size()));

                // データ更新
                ScheduleInfoEntity inputScheduleInfoEntity = new ScheduleInfoEntity();
                inputScheduleInfoEntity.setScheduleId(data.getScheduleId());
                inputScheduleInfoEntity.setScheduleName(data.getScheduleName());
                inputScheduleInfoEntity.setScheduleFromDate(data.getScheduleFromDate());
                inputScheduleInfoEntity.setScheduleToDate(data.getScheduleToDate());
                inputScheduleInfoEntity.setVerInfo(data.getVerInfo());

                logger.debug(" 組織件数:" + data.getFkOrganization().size());
                ResponseEntity res;
                for (OrganizationInfoEntity id : data.getFkOrganization()) {
                    inputScheduleInfoEntity.setFkOrganizationId(id.getOrganizationId());
                    logger.debug(" 登録データ:" + data.toString());
                    res = scheduleInfoFacade.update(inputScheduleInfoEntity);

                    if (Objects.isNull(res) || !ResponseAnalyzer.getAnalyzeResult(res)) {
                        if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                            // 排他バージョンが異なる。
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.PlansUpdate"), LocaleUtils.getString("key.alert.differentVerInfo"));
                        } else {
                            // 過去日の編集はできません。
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.PlansUpdate"),
                                    String.format(LocaleUtils.getString("key.plans.pastday"), LocaleUtils.getString("key.Edit")));
                        }
                        break;
                    }
                }

                //リスト更新
                this.setCalendar();
                if (Objects.nonNull(this.targetDay)) {
                    this.setTargetDay(this.targetDay);
                }
            } else {
                logger.debug(" input cancel");
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(obj, false);
        }

        logger.debug(":onPlansUpdate end.");
    }

    /**
     * 予定表の削除ボタンのクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onPlansDelete(ActionEvent event) {
        logger.debug(":onPlansDelete start.");
        Object obj = new Object();
        try {
            this.blockUI(obj, true);

            if (Objects.isNull(plansList.getSelectionModel().getSelectedItems())) {
                return;
            }

            List<PlansData> datas = plansList.getSelectionModel().getSelectedItems();
            logger.debug(" target ScheduleId count:" + (Objects.isNull(datas.size()) ? null : datas.size()));
            if (Objects.isNull(datas)) {
                return;
            }

            for (PlansData data : datas) {
                logger.debug(" 現在日以降？  予定開始日:" + DateUtils.truncate(data.getEntity().getScheduleFromDate(), Calendar.DAY_OF_MONTH).toString() + ",  現在日:" + new Date().toString());
                logger.debug(" 現在日以降？  予定開始日:" + DateUtils.truncate(data.getEntity().getScheduleFromDate(), Calendar.DAY_OF_MONTH).toString() + ",  現在日:" + new Date().toString());
                if (DateUtils.truncate(data.getScheduleFromDate(), Calendar.DAY_OF_MONTH).compareTo(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) < 0 && DateUtils.truncate(data.getScheduleToDate(), Calendar.DAY_OF_MONTH).compareTo(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) < 0) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.PlansDelete"), String.format(LocaleUtils.getString("key.plans.pastday"), LocaleUtils.getString("key.Remove")));
                    return;
                }
            }

            final String messgage = datas.size() > 1
                    ? LocaleUtils.getString("key.DeleteMultipleMessage")
                    : LocaleUtils.getString("key.DeleteSingleMessage");
            final String content = datas.size() > 1
                    ? null
                    : datas.get(0).getScheduleName();

            // 確認
            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), messgage, content);
            if (ret.equals(ButtonType.CANCEL)) {
                return;
            }

            // 予定表を削除する。
            for (PlansData data : datas) {
                ResponseEntity res = scheduleInfoFacade.delete(data.getScheduleId());

                if (Objects.isNull(res) || !ResponseAnalyzer.getAnalyzeResult(res)) {
                    //TODO:エラー時の処理
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.PlansDelete"),
                            String.format(LocaleUtils.getString("key.FailedToDelete"), LocaleUtils.getString("key.Plans")));
                    break;
                }
            }

            //リスト更新
            this.setCalendar();
            if (Objects.nonNull(this.targetDay)) {
                this.setTargetDay(this.targetDay);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.blockUI(obj, false);
            logger.debug(":onPlansDelete end.");
        }
    }

    /**
     * 前月クリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onMonthBack(MouseEvent event) {
        logger.info(":onMonthBack start ");
        Object obj = new Object();
        try {
            this.blockUI(obj, true);

            logger.debug(" now date:" + this.dateFormatter.format(this.targetDate.getTime()));
            this.targetDate.add(Calendar.MONTH, -1);
            logger.debug(" change date:" + this.dateFormatter.format(this.targetDate.getTime()));
            logger.debug("カレンダー更新：全体B");

            this.targetDayText.setText("");
            this.setCalendar(this.targetDate);
            this.setCalendar();

            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            logger.debug(" save date:" + this.dateFormatter.format(this.targetDate.getTime()));
            prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_DATE, this.dateFormatter.format(this.targetDate.getTime()));

            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.blockUI(obj, false);
        }

        logger.info(":onMonthBack end ");
    }

    /**
     * 次月クリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onMonthNext(MouseEvent event) {
        logger.debug(":onMonthNext start ");
        Object obj = new Object();
        try {
            this.blockUI(obj, true);

            logger.debug(" now date:" + this.dateFormatter.format(this.targetDate.getTime()));
            this.targetDate.add(Calendar.MONTH, 1);
            logger.debug(" change date:" + this.dateFormatter.format(this.targetDate.getTime()));
            logger.debug("カレンダー更新：全体C");
            this.targetDayText.setText("");

            this.setCalendar(this.targetDate);
            this.setCalendar();

            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            logger.debug(" save date:" + this.dateFormatter.format(this.targetDate.getTime()));
            prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_DATE, this.dateFormatter.format(this.targetDate.getTime()));

            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.blockUI(obj, false);
        }

        logger.debug(":onMonthNext end ");
    }

    /**
     * 前年クリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onYearBack(MouseEvent event) {
        logger.debug(":onYearBack start ");
        Object obj = new Object();
        try {
            this.blockUI(obj, true);

            logger.debug(" now date:" + this.dateFormatter.format(this.targetDate.getTime()));
            this.targetDate.add(Calendar.YEAR, -1);
            logger.debug(" change date:" + this.dateFormatter.format(this.targetDate.getTime()));

            this.targetDayText.setText("");

            this.setCalendar(this.targetDate);
            this.setCalendar();

            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            logger.debug(" save date:" + this.dateFormatter.format(this.targetDate.getTime()));
            prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_DATE, this.dateFormatter.format(this.targetDate.getTime()));

            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.blockUI(obj, false);
        }

        logger.debug(":onYearBack end ");
    }

    /**
     * 次年クリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onYearNext(MouseEvent event) {
        logger.debug(":onYearNext start ");
        Object obj = new Object();
        try {
            this.blockUI(obj, true);

            logger.debug(" now date:" + this.dateFormatter.format(this.targetDate.getTime()));
            this.targetDate.add(Calendar.YEAR, 1);
            logger.debug(" change date:" + this.dateFormatter.format(this.targetDate.getTime()));
            logger.debug("カレンダー更新：全体E");

            this.targetDayText.setText("");

            this.setCalendar(this.targetDate);
            this.setCalendar();

            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            logger.debug(" save date:" + this.dateFormatter.format(this.targetDate.getTime()));
            prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_DATE, this.dateFormatter.format(this.targetDate.getTime()));

            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.blockUI(obj, false);
        }

        logger.debug(":onYearNext end ");
    }

    /**
     * 組織ツリーのキー押下のイベント
     *
     * @param ke キーイベント
     */
    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            this.updateTree(true);
        }
    }

    /**
     * 指定日の表示処理
     *
     * @param _calendar 指定日
     */
    private void setTargetDay(Calendar _calendar) {
        this.setTargetDay(_calendar, _calendar.get(Calendar.WEEK_OF_MONTH), _calendar.get(Calendar.DAY_OF_WEEK));
    }

    /**
     * 指定日の表示処理
     *
     * @param _calendar 指定日
     * @param weekRow 月の週
     * @param weekCol 曜日IDX
     */
    private void setTargetDay(Calendar _calendar, int weekRow, int weekCol) {
        logger.info(":setTargetDay start");
        logger.debug("   calendar:" + _calendar.getTime());
        logger.debug("   weekRow:" + weekRow);
        logger.debug("   weekCol:" + weekCol);

        // 前回の日にちを非選択表示
        this.setCalendarSelect(this.targetDay, false);

        logger.debug(" this.targetDate:" + _calendar.getTime());
        _calendar.set(Calendar.DAY_OF_WEEK, weekCol);
        _calendar.set(Calendar.WEEK_OF_MONTH, weekRow);

        // 今回の日にちを選択表示
        this.setCalendarSelect(_calendar, true);

        this.targetDate = _calendar;
        Date _targetDay = _calendar.getTime();
        logger.debug(" save date:" + this.dateCharFormatter.format(_calendar.getTime()));
        this.targetDayText.setText(this.dateCharFormatter.format(_calendar.getTime()));
        this.targetDay = Calendar.getInstance();
        this.targetDay.setTime(_targetDay);

        Date startDate = DateUtils.truncate(_calendar.getTime(), Calendar.DAY_OF_MONTH);
        Date stopDate = DateUtils.truncate(_calendar.getTime(), Calendar.DAY_OF_MONTH);
        stopDate = DateUtils.addDays(stopDate, 1);
        stopDate = DateUtils.addSeconds(stopDate, -1);
        logger.debug(" startDate:" + startDate);
        logger.debug(" stopDate:" + stopDate);

        // 休日検索
        Optional<HolidayInfoEntity> holidayList = holidayDatas.stream()
                .filter(item -> item.getHolidayDate().equals(_calendar))
                .findFirst();
        try {
            if (holidayList.isPresent() && Objects.nonNull(holidayList.get())) {
                HolidayInfoEntity data = holidayList.get();
                logger.debug(" 休日情報:" + data.toString());
            }
            logger.debug(" holidayList ==>" + holidayList + ", " + holidayList.get().toString());
        } catch (Exception ex) {
        }

        // 予定表の検索条件設定
        ScheduleSearchCondition scheduleSearchCondition = new ScheduleSearchCondition();
        scheduleSearchCondition.fromDate(startDate);
        scheduleSearchCondition.toDate(stopDate);
        logger.debug(" 予定表の検索期間:" + startDate.toString() + "～" + stopDate.toString());
        scheduleSearchCondition.setOrganizationIdCollection(this.selectOrganization);

        // 予定表の検索
        List<ScheduleInfoEntity> datas = new ArrayList();
        if (Objects.nonNull(selectOrganization) && selectOrganization.size() > 0) {
            // 予定表の件数取得（１日分）
            Long count = scheduleInfoFacade.searchCount(scheduleSearchCondition);
            for (long nowCount = 0l; nowCount < count; nowCount += READ_COUNT) {
                datas.addAll(scheduleInfoFacade.searchRange(scheduleSearchCondition, nowCount, nowCount + READ_COUNT - 1));
            }
        }
        logger.debug(" 予定表:" + datas.size());

        ObservableList<RosterCompoController.PlansData> tableData = FXCollections.observableArrayList();

        datas.stream().forEach((_item) -> {
            OrganizationInfoEntity data2 = organizationInfoFacade.find(_item.getFkOrganizationId());

            if (Objects.nonNull(data2)) {
                tableData.add(new PlansData(_targetDay, _item, data2.getOrganizationName()));
            } else {
                tableData.add(new PlansData(_targetDay, _item, "(対象者なし)"));
            }
            logger.debug(" 予定表:" + _item.toString());
        });

        // 予定表一覧の初期化
        this.initPlansList();
        ObservableList<PlansData> oList = FXCollections.observableArrayList(tableData);
        this.plansList.setItems(oList);
        this.plansList.itemsProperty().setValue(oList);
        this.plansList.layout();
        this.plansList.refresh();

        // リソースに保存
        try {
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            prop.setProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_DAY, this.dateFormatter.format(_targetDay));

            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
        }

        this.targetDate = _calendar;
        logger.debug(":setTargetDay end");
    }

    /**
     * 日にちの選択設定
     *
     * @param _calendar 対象日
     * @param mode モード
     */
    private void setCalendarSelect(Calendar _calendar, boolean mode) {
        if (Objects.isNull(_calendar)) {
            return;
        }

        int col = _calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int row = _calendar.get(Calendar.WEEK_OF_MONTH) - 1;

        this.setCalendarSelect(row, col, mode);
    }

    /**
     * 日にちの選択設定
     *
     * @param _row
     * @param _col
     * @param _mode
     * @param mode モード
     */
    private void setCalendarSelect(int _row, int _col, boolean _mode) {
        // 日にち選択の対象外
        logger.debug(" 月:" + _row + ", 週:" + _col + ", モード:" + _mode);
        if (_row < 0 || _col < 0) {
            return;
        }

        if (calButton.get(_row).get(_col).getText().isEmpty()) {
            calButton.get(_row).get(_col).setBorder(this.selectCalOff);
            return;
        }

        if (_mode) {
            calButton.get(_row).get(_col).setBorder(this.selectCalOn);
        } else {
            calButton.get(_row).get(_col).setBorder(this.selectCalOff);
        }
    }

    /**
     * 第１日曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onSunday1(ActionEvent event) {
        logger.debug(":onSunday1 start");

        this.setTargetDay(this.targetDate, 1, Calendar.SUNDAY);

        logger.debug(":onSunday1 end");
    }

    /**
     * 第２日曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onSunday2(ActionEvent event) {
        logger.debug(":onSunday2 start");

        this.setTargetDay(this.targetDate, 2, Calendar.SUNDAY);

        logger.debug(":onSunday2 end");
    }

    /**
     * 第３日曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onSunday3(ActionEvent event) {
        logger.debug(":onSunday3 start");

        this.setTargetDay(this.targetDate, 3, Calendar.SUNDAY);

        logger.debug(":onSunday3 end");
    }

    /**
     * 第４日曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onSunday4(ActionEvent event) {
        logger.debug(":onSunday4 start");

        this.setTargetDay(this.targetDate, 4, Calendar.SUNDAY);

        logger.debug(":onSunday4 end");
    }

    /**
     * 第５日曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onSunday5(ActionEvent event) {
        logger.debug(":onSunday5 start");

        this.setTargetDay(this.targetDate, 5, Calendar.SUNDAY);

        logger.debug(":onSunday5 end");
    }

    /**
     * 第６日曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onSunday6(ActionEvent event) {
        logger.debug(":onSunday6 start");

        this.setTargetDay(this.targetDate, 6, Calendar.SUNDAY);

        logger.debug(":onSunday6 end");
    }

    /**
     * 第１月曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onMonday1(ActionEvent event) {
        logger.debug(":onMonday1 start");

        this.setTargetDay(this.targetDate, 1, Calendar.MONDAY);

        logger.debug(":onMonday1 end");
    }

    /**
     * 第２月曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onMonday2(ActionEvent event) {
        logger.debug(":onMonday2 start");

        this.setTargetDay(this.targetDate, 2, Calendar.MONDAY);

        logger.debug(":onMonday2 end");
    }

    /**
     * 第３月曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onMonday3(ActionEvent event) {
        logger.debug(":onMonday3 start");

        this.setTargetDay(this.targetDate, 3, Calendar.MONDAY);

        logger.debug(":onMonday3 end");
    }

    /**
     * 第４月曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onMonday4(ActionEvent event) {
        logger.debug(":onMonday4 start");

        this.setTargetDay(this.targetDate, 4, Calendar.MONDAY);

        logger.debug(":onMonday4 end");
    }

    /**
     * 第５月曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onMonday5(ActionEvent event) {
        logger.debug(":onMonday5 start");

        this.setTargetDay(this.targetDate, 5, Calendar.MONDAY);

        logger.debug(":onMonday5 end");
    }

    /**
     * 第６月曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onMonday6(ActionEvent event) {
        logger.debug(":onMonday6 start");

        this.setTargetDay(this.targetDate, 6, Calendar.MONDAY);

        logger.debug(":onMonday6 end");
    }

    /**
     * 第１火曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onTuesday1(ActionEvent event) {
        logger.debug(":onTuesday1 start");

        this.setTargetDay(this.targetDate, 1, Calendar.TUESDAY);

        logger.debug(":onTuesday1 end");
    }

    /**
     * 第２火曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onTuesday2(ActionEvent event) {
        logger.debug(":onTuesday2 start");

        this.setTargetDay(this.targetDate, 2, Calendar.TUESDAY);

        logger.debug(":onTuesday2 end");
    }

    /**
     * 第３火曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onTuesday3(ActionEvent event) {
        logger.debug(":onTuesday3 start");

        this.setTargetDay(this.targetDate, 3, Calendar.TUESDAY);

        logger.debug(":onTuesday3 end");
    }

    /**
     * 第４火曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onTuesday4(ActionEvent event) {
        logger.debug(":onTuesday4 start");

        this.setTargetDay(this.targetDate, 4, Calendar.TUESDAY);

        logger.debug(":onTuesday4 end");
    }

    /**
     * 第５火曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onTuesday5(ActionEvent event) {
        logger.debug(":onTuesday5 start");

        this.setTargetDay(this.targetDate, 5, Calendar.TUESDAY);

        logger.debug(":onTuesday5 end");
    }

    /**
     * 第６火曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onTuesday6(ActionEvent event) {
        logger.debug(":onTuesday6 start");

        this.setTargetDay(this.targetDate, 6, Calendar.TUESDAY);

        logger.debug(":onTuesday6 end");
    }

    /**
     * 第１水曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onWednesday1(ActionEvent event) {
        logger.debug(":onWednesday1 start");

        this.setTargetDay(this.targetDate, 1, Calendar.WEDNESDAY);

        logger.debug(":onWednesday1 end");
    }

    /**
     * 第２水曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onWednesday2(ActionEvent event) {
        logger.debug(":onWednesday2 start");

        this.setTargetDay(this.targetDate, 2, Calendar.WEDNESDAY);

        logger.debug(":onWednesday2 end");
    }

    /**
     * 第３水曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onWednesday3(ActionEvent event) {
        logger.debug(":onWednesday3 start");

        this.setTargetDay(this.targetDate, 3, Calendar.WEDNESDAY);

        logger.debug(":onWednesday3 end");
    }

    /**
     * 第４水曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onWednesday4(ActionEvent event) {
        logger.debug(":onWednesday4 start");

        this.setTargetDay(this.targetDate, 4, Calendar.WEDNESDAY);

        logger.debug(":onWednesday4 end");
    }

    /**
     * 第５水曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onWednesday5(ActionEvent event) {
        logger.debug(":onWednesday5 start");

        this.setTargetDay(this.targetDate, 5, Calendar.WEDNESDAY);

        logger.debug(":onWednesday5 end");
    }

    /**
     * 第６水曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onWednesday6(ActionEvent event) {
        logger.debug(":onWednesday6 start");

        this.setTargetDay(this.targetDate, 6, Calendar.WEDNESDAY);

        logger.debug(":onWednesday6 end");
    }

    /**
     * 第１木曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onThursday1(ActionEvent event) {
        logger.debug(":onThursday1 start");

        this.setTargetDay(this.targetDate, 1, Calendar.THURSDAY);

        logger.debug(":onThursday1 end");
    }

    /**
     * 第２木曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onThursday2(ActionEvent event) {
        logger.debug(":onThursday2 start");

        this.setTargetDay(this.targetDate, 2, Calendar.THURSDAY);

        logger.debug(":onThursday2 end");
    }

    /**
     * 第３木曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onThursday3(ActionEvent event) {
        logger.debug(":onThursday3 start");

        this.setTargetDay(this.targetDate, 3, Calendar.THURSDAY);

        logger.debug(":onThursday3 end");
    }

    /**
     * 第４木曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onThursday4(ActionEvent event) {
        logger.debug(":onThursday4 start");

        this.setTargetDay(this.targetDate, 4, Calendar.THURSDAY);

        logger.debug(":onThursday4 end");
    }

    /**
     * 第５木曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onThursday5(ActionEvent event) {
        logger.debug(":onThursday5 start");

        this.setTargetDay(this.targetDate, 5, Calendar.THURSDAY);

        logger.debug(":onThursday5 end");
    }

    /**
     * 第６木曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onThursday6(ActionEvent event) {
        logger.debug(":onThursday6 start");

        this.setTargetDay(this.targetDate, 6, Calendar.THURSDAY);

        logger.debug(":onThursday6 end");
    }

    /**
     * 第１金曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onFryday1(ActionEvent event) {
        logger.debug(":onFryday1 start");

        this.setTargetDay(this.targetDate, 1, Calendar.FRIDAY);

        logger.debug(":onFryday1 end");
    }

    /**
     * 第２金曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onFryday2(ActionEvent event) {
        logger.debug(":onFryday2 start");

        this.setTargetDay(this.targetDate, 2, Calendar.FRIDAY);

        logger.debug(":onFryday2 end");
    }

    /**
     * 第３金曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onFryday3(ActionEvent event) {
        logger.debug(":onFryday3 start");

        this.setTargetDay(this.targetDate, 3, Calendar.FRIDAY);

        logger.debug(":onFryday3 end");
    }

    /**
     * 第４金曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onFryday4(ActionEvent event) {
        logger.debug(":onFryday4 start");

        this.setTargetDay(this.targetDate, 4, Calendar.FRIDAY);

        logger.debug(":onFryday4 end");
    }

    /**
     * 第５金曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onFryday5(ActionEvent event) {
        logger.debug(":onFryday5 start");

        this.setTargetDay(this.targetDate, 5, Calendar.FRIDAY);

        logger.debug(":onFryday5 end");
    }

    /**
     * 第６金曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onFryday6(ActionEvent event) {
        logger.debug(":onFryday6 start");

        this.setTargetDay(this.targetDate, 6, Calendar.FRIDAY);

        logger.debug(":onFryday6 end");
    }

    /**
     * 第１土曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onSaturday1(ActionEvent event) {
        logger.debug(":onSaturday1 start");

        this.setTargetDay(this.targetDate, 1, Calendar.SATURDAY);

        logger.debug(":onSaturday1 end");
    }

    /**
     * 第２土曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onSaturday2(ActionEvent event) {
        logger.debug(":onSaturday2 start");

        this.setTargetDay(this.targetDate, 2, Calendar.SATURDAY);

        logger.debug(":onSaturday2 end");
    }

    /**
     * 第３土曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onSaturday3(ActionEvent event) {
        logger.debug(":onSaturday3 start");

        this.setTargetDay(this.targetDate, 3, Calendar.SATURDAY);

        logger.debug(":onSaturday3 end");
    }

    /**
     * 第４土曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onSaturday4(ActionEvent event) {
        logger.debug(":onSaturday4 start");

        this.setTargetDay(this.targetDate, 4, Calendar.SATURDAY);

        logger.debug(":onSaturday4 end");
    }

    /**
     * 第５土曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onSaturday5(ActionEvent event) {
        logger.debug(":onSaturday5 start");

        this.setTargetDay(this.targetDate, 5, Calendar.SATURDAY);

        logger.debug(":onSaturday5 end");
    }

    /**
     * 第６土曜日のクリックイベント
     *
     * @param event イベント
     */
    @FXML
    private void onSaturday6(ActionEvent event) {
        logger.debug(":onSaturday6 start");

        this.setTargetDay(this.targetDate, 6, Calendar.SATURDAY);

        logger.debug(":onSaturday6 end");
    }

    /**
     * キャッシュに情報を読み込み、組織ツリーを更新する。
     */
    private void updateTree(boolean isRefresh) {
        logger.info("updateTree start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);

            isUpdateTree = true;

            Task task = new Task<Long>() {
                @Override
                protected Long call() throws Exception {
                    Long selectedOrganizationId = ROOT_ID;
                    if (!isRefresh) {
                        AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
                        Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

                        try {
                            selectedOrganizationId = Long.valueOf(prop.getProperty(ProductionNaviPropertyConstants.SET_ROSTER_TARGET_ORGANIZATION, String.valueOf(RosterCompoController.ROOT_ID)));
                        } catch (NumberFormatException ex) {
                            logger.fatal(ex, ex);
                            selectedOrganizationId = ROOT_ID;
                        }
                        logger.debug("●組織ツリーの表示処理.call()  組織階層ID:" + selectedOrganizationId);
                    }

                    if (isRefresh) {
                        CacheUtils.removeCacheData(OrganizationInfoEntity.class);
                        CacheUtils.removeCacheData(HolidayInfoEntity.class);
                    }
                    CacheUtils.createCacheOrganization(true);
                    CacheUtils.createCacheHoliday(true);

                    return selectedOrganizationId;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // ツリーのルートノードを生成する。
                        logger.debug("●組織ツリーの表示処理.succeeded()  組織階層ID:" + this.getValue());
                        createRoot(this.getValue());
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                        logger.info("updateTree end.");
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
                        blockUI(obj, false);
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            isUpdateTree = false;
            blockUI(obj, false);
        }
    }

    @Override
    public boolean destoryComponent() {
        SplitPaneUtils.saveDividerPosition(rosterPane, getClass().getSimpleName());
        return true;
    }

    /**
     * 
     */
    public class CalendarData {

        public static final int MODE_HOLIDAY = 2;
        public static final int MODE_PLANS = 1;
        public static final int MODE_NONE = 0;
        private final Calendar day;
        private final Tooltip tooltip;
        private boolean lock;
        private int mode = 0;       // 0:予定なし、1:予定あり、2:休日
        private String text;

        public CalendarData(Calendar _day, boolean _lock, int _mode, String _text, Tooltip _tooltip) {
            this.day = _day;
            this.tooltip = _tooltip;
            this.mode = _mode;
            this.text = _text;
            this.lock = _lock;
        }

        public Calendar getDay() {
            return this.day;
        }

        public Tooltip getTooltip() {
            return this.tooltip;
        }

        public boolean getLock() {
            return this.lock;
        }

        public void setLock(boolean _value) {
            this.lock = _value;
        }

        public int getMode() {
            return this.mode;
        }

        public void setMode(int _value) {
            this.mode = _value;
        }

        public String getText() {
            return this.text;
        }

        public void setText(String _value) {
            this.text = _value;
        }
    }

    /**
     * 予定表リストデータ
     */
    public class PlansData {

        private final Long scheduleId;
        private final Long fkOrganizationId;
        private Date scheduleFromDate;
        private Date scheduleToDate;
        private String scheduleFromDateStr = "";
        private String scheduleToDateStr = "";
        private String scheduleName = "";
        private String organizationName = "";
        private final ScheduleInfoEntity entity;

        private final SimpleDateFormat timeFormatter = new SimpleDateFormat("MM/dd HH:mm");

        public PlansData(Date _nowDate, ScheduleInfoEntity _entity, String _organizationName) {
            this.entity = _entity;
            this.scheduleId = _entity.getScheduleId();
            this.scheduleName = _entity.getScheduleName();
            this.scheduleFromDate = _entity.getScheduleFromDate();
            this.scheduleToDate = _entity.getScheduleToDate();
            this.fkOrganizationId = _entity.getFkOrganizationId();
            this.organizationName = _organizationName;

            this.scheduleFromDateStr = timeFormatter.format(_entity.getScheduleFromDate());
            this.scheduleToDateStr = timeFormatter.format(_entity.getScheduleToDate());
        }

        /**
         * 予定IDの取得
         *
         * @return 予定ID
         */
        public Long getScheduleId() {
            return this.scheduleId;
        }

        /**
         * 予定名の取得
         *
         * @return 予定名
         */
        public String getScheduleName() {
            return this.scheduleName;
        }

        /**
         * 予定名の設定
         *
         * @param _value 予定名
         */
        public void setScheduleName(String _value) {
            this.scheduleName = _value;
        }

        /**
         * 予定開始日時の取得
         *
         * @return 予定開始日時
         */
        public Date getScheduleFromDate() {
            return this.scheduleFromDate;
        }

        /**
         * 予定開始日時の設定
         *
         * @param _value 開始日時
         */
        public void setScheduleFromDate(Date _value) {
            this.scheduleFromDate = _value;
        }

        /**
         * 予定終了日時の取得
         *
         * @return 予定終了日時
         */
        public Date getScheduleToDate() {
            return this.scheduleToDate;
        }

        /**
         * 予定終了日時の設定
         *
         * @param _value 予定終了日時
         */
        public void setScheduleToDate(Date _value) {
            this.scheduleToDate = _value;
        }

        /**
         * 組織名の取得
         *
         * @return 組織名
         */
        public String getOrganizationName() {
            return this.organizationName;
        }

        /**
         * 組織名の設定
         *
         * @param _value 組織名
         */
        public void setOrganizationName(String _value) {
            this.organizationName = _value;
        }

        /**
         * 組織IDの取得
         *
         * @return 組織ID
         */
        public Long getFkOrganizationId() {
            return this.fkOrganizationId;
        }

        /**
         * 予定表情報の取得
         *
         * @return 予定表情報
         */
        public ScheduleInfoEntity getEntity() {
            return this.entity;
        }

        /**
         * 予定開始日時の文字列の取得
         *
         * @return 予定開始日時の文字列
         */
        public String getScheduleFromDateStr() {
            return this.scheduleFromDateStr;
        }

        /**
         * 予定開始日時の文字列の設定
         *
         * @param _value 予定開始日時の文字列
         */
        public void setScheduleFromDateStr(String _value) {
            this.scheduleFromDateStr = _value;
        }

        /**
         * 予定終了日時の文字列の取得
         *
         * @return 終了日時の文字列
         */
        public String getScheduleToDateStr() {
            return this.scheduleToDateStr;
        }

        /**
         * 予定終了日時の文字列の設定
         *
         * @param _value 終了日時の文字列
         */
        public void setScheduleToDateStr(String _value) {
            this.scheduleToDateStr = _value;
        }
    }
}

