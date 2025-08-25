/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import static adtekfuji.admanagerapp.workfloweditplugin.common.Constants.*;
import adtekfuji.admanagerapp.workfloweditplugin.common.PropertyTemplateLoader;
import adtekfuji.admanagerapp.workfloweditplugin.common.SelectedWorkAndHierarchy;
import adtekfuji.admanagerapp.workfloweditplugin.common.WorkPropertyRecordFactory;
import adtekfuji.admanagerapp.workfloweditplugin.common.WorkflowEditConfig;
import adtekfuji.admanagerapp.workfloweditplugin.common.XmlSerializer;
import adtekfuji.admanagerapp.workfloweditplugin.entity.ApprovalDialogEntity;
import adtekfuji.admanagerapp.workfloweditplugin.entity.TraceOptionEntity;
import adtekfuji.admanagerapp.workfloweditplugin.entity.TraceSettingEntity;
import adtekfuji.admanagerapp.workfloweditplugin.net.HttpStorage;
import adtekfuji.admanagerapp.workfloweditplugin.net.RemoteStorage;
import adtekfuji.admanagerapp.workfloweditplugin.net.UITask;
import adtekfuji.admanagerapp.workfloweditplugin.property.WorkRecordFactory;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.ObjectInfoFacade;
import adtekfuji.clientservice.WorkInfoFacade;
import adtekfuji.clientservice.common.Paths;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringTime;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.approval.ApprovalInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.object.ObjectInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.response.ResponseWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.search.EquipmentSearchCondition;
import jp.adtekfuji.adFactory.entity.work.DispAddInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkSectionInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalDataTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.TraceOptionTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.adFactory.utility.DataFormatUtil;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.PropertyBindEntity;
import jp.adtekfuji.javafxcommon.controls.RichTab;
import jp.adtekfuji.javafxcommon.controls.RichTabPane;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * タブ付き工程設定画面のコントローラー
 *
 * @author s-heya
 */
@FxComponent(id = "TabbedWorkCompo", fxmlPath = "/fxml/admanagerworkfloweditplugin/tabbed_work_compo.fxml")
public class TabbedWorkCompoController implements Initializable, ArgumentDelivery, ListChangeListener<Tab>, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final WorkInfoFacade workInfoFacade = new WorkInfoFacade();
    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final ObjectInfoFacade objectInfoFacede = new ObjectInfoFacade();
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();

    private final Map<String, PropertyBindEntity> properties = new LinkedHashMap<>();
    private final LinkedList<WorkPropertyInfoEntity> workProperties = new LinkedList<>();
    private final ObservableList<ObjectInfoEntity> useParts = FXCollections.observableArrayList();
    private final List<EquipmentInfoEntity> manufactureEquipments = new ArrayList<>();
    private final List<EquipmentInfoEntity> measureEquipments = new ArrayList<>();
    private final Map<String, String> templateRegexTexts = new HashMap<>();

    private SelectedWorkAndHierarchy selected;
    private WorkInfoEntity workInfo;
    private Integer oldTaktTime;
    private static String style = "-fx-font-size: 20; -fx-font-weight: bold;";
    private Tooltip tooltip;
    private EventHandler pasteImageHandler;

    // 開いたときに表示された情報
    private WorkInfoEntity cloneWorkInfo;

    private boolean downloading;
    
    /**
     * 工程セクションマップ
     */
    private Map<Integer, WorkSectionInfoEntity> workSections = new HashMap<>();

    /**
     * 承認機能オプションが有効か
     */
    private final boolean isLicensedApproval = ClientServiceProperty.isLicensed(LicenseOptionType.ApprovalOption.getName());

    /**
     * 工程名テキストボックス
     */
    private TextField workNameField;

    /**
     * 版数テキストボックス
     */
    private TextField revisionField;

    /**
     * 改訂ボタン
     */
    private Button reviseButton;

    /**
     * 改訂ボタンの有効/無効状態
     */
    private boolean disableReviseButton = true;

    /**
     * 工程名フィールドの有効/無効状態
     */
    private boolean disableWorkNameField = false;

    /**
     * 各種コントロール格納用のTopテーブル
     */
    private Table topTable;

    /**
     * 各種入力項目の有効/無効状態
     */
    private boolean disableOtherInputItems = true;
    
    @FXML
    private SplitPane reportOutListPane;
    @FXML
    private Button registButton;
    @FXML
    private Button applyButton;
    @FXML
    private Label parentNameLabel;
    @FXML
    private VBox topSidePane;
    @FXML
    private TextArea usePartsTextArea;
    @FXML
    private VBox propertyPane;
    @FXML
    private RichTabPane tabPane;
    @FXML
    private Pane progressPane;

    /**
     * キャンセルボタン
     */
    @FXML
    private Button cancelButton;

    /**
     * 申請中ラベル
     */
    @FXML
    private Label requestingLabel;

    /**
     * 申請者名ラベル
     */
    @FXML
    private Label requestorNameLabel;

    /**
     * 申請ボタン
     */
    @FXML
    private Button requestButton;

    /**
     * 申請取消ボタン
     */
    @FXML
    private Button requestCancelButton;
    
    /**
     * 工程順へ適用
     */
    @FXML
    private Button applyWorkflowButton;

    /**
     * 使用部品HBox
     */
    @FXML
    private HBox usePartsPane;

    private TextArea contentTextArea;
    private final static SimpleBooleanProperty isPasteDisable = new SimpleBooleanProperty(true);

    private final static SimpleBooleanProperty isDeleteDisable = new SimpleBooleanProperty(true);

    /**
     * imgタグの挿入
     *
     * @param event
     */
    private void onInsertAddress(ActionEvent event) {
        insertTag(LocaleUtils.getString("key.InsertImage"));
    }

    /**
     * aタグの挿入
     *
     * @param event
     */
    private void onInsertLink(ActionEvent event) {
        insertTag(LocaleUtils.getString("key.InsertLink"));
    }

    /**
     * ファイルを選択してタグを挿入する
     *
     * @param event
     */
    private void insertTag(String title) {
        InsertTagCompoController.Data arg = new InsertTagCompoController.Data(title);

        ButtonType ret = sc.showComponentDialog(title, "InsertTagCompo", arg);
        if (ret != ButtonType.OK) {
            return;
        }

        String insert = "";

        if (arg.getTitle().equals(LocaleUtils.getString("key.InsertImage"))) {
            String addr = Objects.isNull(arg.getAddr()) ? "" : arg.getAddr();

            insert = String.format(IMG_TAG, addr);
        } else if (arg.getTitle().equals(LocaleUtils.getString("key.InsertLink"))) {
            String addr = Objects.isNull(arg.getAddr()) ? "" : arg.getAddr();
            String value = Objects.isNull(arg.getValue()) ? "" : arg.getValue();

            insert = String.format(A_TAG, addr, value);
        }

        contentTextArea.insertText(contentTextArea.getCaretPosition(), insert);
    }

    /**
     * タブ付き工程設定画面を初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 機能制限
        if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
            this.applyWorkflowButton.setDisable(true);
            this.registButton.setDisable(true);
            this.applyButton.setDisable(true);
        }
        
        SplitPaneUtils.loadDividerPosition(reportOutListPane, getClass().getSimpleName());
            
        this.tabPane.setPrefixName("Sheet");

        this.tabPane.setContextMenuCreator((richTab) -> {
            List<MenuItem> menuItems = new ArrayList<>();
            
            // 新規挿入メニュー
            if(!RichTabPane.isInsertTab(richTab)) {
                MenuItem insertMenu = new MenuItem(LocaleUtils.getString("key.InsertTab"));
                insertMenu.setOnAction(e -> {
                    final int insertIndex = this.tabPane.getTabs().indexOf(richTab);
                    WorkSectionInfoEntity newWorkSection = new WorkSectionInfoEntity();
                    RichTab newTab = this.tabPane.addTab(insertIndex);
                    newTab.setEditable(!disableOtherInputItems);
                    newTab.setClosable(!disableOtherInputItems);
                    this.setupTab(newTab, newWorkSection);
                    WorkSectionPane pane = (WorkSectionPane) ((ScrollPane) newTab.getContent()).getContent();
                    pane.showDocumentFile();
                });
                insertMenu.setDisable(false);
                menuItems.add(insertMenu);
            }

            // コピーメニュー
            if (!RichTabPane.isInsertTab(richTab)) {
                MenuItem copyMenu = new MenuItem(LocaleUtils.getString("key.Copy"));
                copyMenu.setOnAction(e -> {
                    List<WorkSectionInfoEntity> workSectionInfoEntities
                            = this.tabPane
                            .getSelectedTabs()
                            .stream()
                            .map(RichTab::getUserData)
                            .map(WorkSectionInfoEntity.class::cast)
                            .map(WorkSectionInfoEntity::clone)
                            .peek(entity -> {
                                LinkedList<WorkPropertyInfoEntity> traceablilities = entity.getTraceabilityCollection();
                                for (int n=0; n<traceablilities.size(); ++n) {
                                    traceablilities.get(n).setWorkPropOrder(n+1);
                                }
                            })
                            .collect(Collectors.toList());
                    copyToClipBord(workSectionInfoEntities);
                });
                copyMenu.setDisable(false);
                menuItems.add(copyMenu);
            }

            // 貼付けメニュー
            MenuItem pastMenu = new MenuItem(LocaleUtils.getString("key.Pasting"));
            pastMenu.setOnAction(e -> {
                getEntityFromClipBoard().ifPresent(entities -> {
                    this.tabPane.selectedTabClear();
                    // 画像のアップロードの為、ドキュメントの更新フラグをオンとする。
                    entities.forEach(entity -> {
                        entity.setChenged(true);
                        final int insertIndex = this.tabPane.getTabs().indexOf(richTab);
                        String name = this.tabPane.generateSheetName(entity.getDocumentTitle());
                        entity.setDocumentTitle(name);
                        RichTab tab = this.tabPane.addTab(name, insertIndex);
                        tab.setEditable(!this.disableOtherInputItems);
                        tab.setClosable(!this.disableOtherInputItems);
                        this.setupTab(tab, entity);
                        WorkSectionPane pane = (WorkSectionPane)((ScrollPane) tab.getContent()).getContent();
                        pane.showDocumentFile();
                    });
                });
            });
            pastMenu.disableProperty().bind(isPasteDisable);
            menuItems.add(pastMenu);
            
            if (!RichTabPane.isDeleteTab(richTab)) {
                MenuItem deleteMenu = new MenuItem(LocaleUtils.getString("key.Delete"));
                deleteMenu.setOnAction(e -> {
                    this.tabPane.removeTab();
                    this.tabPane.selectFirst();
                });
                deleteMenu.disableProperty().bind(isDeleteDisable);
                menuItems.add(deleteMenu);
            }
                  
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.setStyle(style);
            contextMenu.getItems().addAll(menuItems);

            contextMenu.setOnShown(event-> {
                isPasteDisable.set(this.isPastMenuDisable());
                isDeleteDisable.set(this.isDeleteMenuDisable());
            });
            return Optional.of(contextMenu);
        });
             
        // イベントハンドラーの設定
        // 画像の貼り付け（Ctrl + V）
        KeyCodeCombination pasteKey = new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN);
        pasteImageHandler = (EventHandler<KeyEvent>) (KeyEvent e) -> {
            Image image = Clipboard.getSystemClipboard().getImage();
            if (pasteKey.match(e) && image != null) {
                // ダイアログ表示
                ButtonType result = sc.showDialog("", "WorkImagePasteDialog", sc.getWindow());

                if (result.equals(ButtonType.CANCEL)) {
                    // キャンセルの場合は何もしない
                    return;
                }

                if (result.equals(ButtonType.OK)) {
                    // OK：現在のシートに貼り付け
                    Tab tab = tabPane.getSelectedTabs().get(0);
                    WorkSectionPane pane = (WorkSectionPane) ((ScrollPane) tab.getContent()).getContent();
                    pane.pasteImage(image);
                }
                else if (result.equals(ButtonType.YES)) {
                    // YES：すべてのシートに貼り付け
                    List<Tab> tabs = tabPane.getTabs()
                            .stream()
                            // [+]タブを除外
                            .filter(tab -> (ScrollPane) tab.getContent() != null)
                            .toList();
                    tabs.forEach(tab -> {
                        WorkSectionPane pane = (WorkSectionPane) ((ScrollPane) tab.getContent()).getContent();
                        pane.pasteImage(image);
                    });
                }
            }
        };
        sc.getWindow().addEventHandler(KeyEvent.KEY_PRESSED, pasteImageHandler);

        //this.stackPane.widthProperty().addListener((observable, oldValue, newValue) -> {
        //    double width = newValue.doubleValue();
        //    if (width != Double.NaN) {
        //        this.tabPane.setMaxWidth(width - 60.0);
        //    }
        //});
    }
    
    /**
     * パラメータを設定する。
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {

        if (argument instanceof SelectedWorkAndHierarchy) {
            this.selected = (SelectedWorkAndHierarchy) argument;

            this.templateRegexTexts.clear();
            this.templateRegexTexts.putAll(PropertyTemplateLoader.getWorkTemplateRegexTexts());

            if (Objects.nonNull(this.selected.getWorkInfo().getWorkId())) {
                this.oldTaktTime = this.selected.getWorkInfo().getTaktTime();
                this.workInfo = this.workInfoFacade.find(this.selected.getWorkInfo().getWorkId(), false, true);
                this.workInfo.getWorkSectionCollection().sort((a, b) -> a.getWorkSectionOrder() - b.getWorkSectionOrder());
            } else {
                this.oldTaktTime = null;
                this.workInfo = this.selected.getWorkInfo();
                this.workInfo.setPropertyInfoCollection(PropertyTemplateLoader.getWorkProperties());
            }

            Platform.runLater(() -> this.show());

            this.cleanCache();
        }
    }

    /**
     * タブが変更された。
     *
     * @param change
     */
    @Override
    public void onChanged(Change<? extends Tab> change) {
        try {
            while (change.next()) {
                if (change.wasAdded()) {
                    // タブが追加された
                    for (Tab tab : change.getAddedSubList()) {
                        if (tab instanceof RichTab && Objects.isNull(tab.getUserData())) {
                            WorkSectionInfoEntity workSection = new WorkSectionInfoEntity();
                            this.setupTab((RichTab) tab, workSection);
                        }
                    }
                } else if (change.wasRemoved()) {
                    // タブが削除された
                    for (Tab tab : change.getRemoved()) {
                        if (tab instanceof RichTab && !((RichTab) tab).isDragging()) {
                            // キャッシュデータを削除する
                            WorkSectionPane pane = (WorkSectionPane)((ScrollPane) tab.getContent()).getContent();
                            logger.info("Removed Tab: " + pane.getCacheName());
                            pane.deleteCacheData();
                        }
                    }
                    if (this.tabPane.getSelectionModel().getSelectedIndex() == 0) {
                        if (0 < this.tabPane.getTabs().size() - 1) {
                            Tab tab = this.tabPane.getTabs().get(0);
                            WorkSectionPane pane = (WorkSectionPane)((ScrollPane) tab.getContent()).getContent();
                            if (Objects.nonNull(pane)) {
                                pane.loadCacheData();
                            }
                        }
                    }
                }
            }
        } finally {
        }
    }

    /**
     * 使用部品選択ダイアログを表示する。
     *
     * @param event
     */
    @FXML
    private void onChoice(ActionEvent event) {
        try {
            logger.info("onChoice start.");

            SelectDialogEntity selectDialogEntity = new SelectDialogEntity().objects(this.useParts);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Object"), "ObjectSelectionCompo", selectDialogEntity, true);

            if (ButtonType.OK == ret) {
                List<ObjectInfoEntity> objects = selectDialogEntity.getObjects();

                this.useParts.clear();
                this.useParts.addAll(objects);

                StringBuilder sb = new StringBuilder();
                for (ObjectInfoEntity objectInfoEntity : objects) {
                    sb.append(objectInfoEntity.getObjectKey());
                    sb.append(",");
                }

                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }

                this.usePartsTextArea.setText(sb.toString());
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onChoice end.");
        }
    }

    /**
     * 表示項目変更ボタンのアクション
     *
     * @param event イベント
     */
    private void onChangeDispAddInfo(ActionEvent event) {
        try {
            logger.info("onChangeDispAddInfo start.");

            // 工程マスタの表示項目カラムから情報を取得
            String displayItems = this.workInfo.getDisplayItems();
            boolean isExist = Objects.nonNull(displayItems);
            List<DispAddInfoEntity> dispAddInfos = isExist ? JsonUtils.jsonToObjects(displayItems, DispAddInfoEntity[].class)
                                                           : new ArrayList<>();

            ObservableList<DispAddInfoEntity> list = FXCollections.observableArrayList(dispAddInfos);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DispAddInfo"), "DispAddInfoListDialog", list, new ButtonType[]{ButtonType.OK, ButtonType.CANCEL});
            if (ButtonType.OK.equals(ret)) {
                list.stream().forEach(i -> i.setOrder(list.indexOf(i)));
                String saveDisplayItems = JsonUtils.objectsToJson(list);
                this.workInfo.setDisplayItems(saveDisplayItems);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onChangeDispAddInfo end.");
        }
    }

    /**
     * 変更内容を破棄して、工程編集画面に戻る。
     *
     * @param event
     */
    @FXML
    private void onCancel(ActionEvent event) {
        logger.info("onCancel:start");
        if (destoryComponent()) {
            this.cloneWorkInfo = this.updateData().clone();
            sc.setComponent("ContentNaviPane", "WorkEditCompo");
        }
    }

    /**
     * 工程データを更新する
     *
     * @param event
     */
    @FXML
    private void onApply(ActionEvent event) {
        try {
            logger.info("onApply start.");

            this.store(false);
        } finally {
            logger.info("onApply end.");
        }
    }

    /**
     * 工程データを登録する。
     *
     * @param event
     */
    @FXML
    private void onRegist(ActionEvent event) {
        try {
            logger.info("onRegist start.");

            this.store(true);
        } finally {
            logger.info("onRegist end.");
        }
    }

    /**
     * 申請ボタン押下時のアクション
     *
     * @param event イベント
     */
    @FXML
    private void onRequest(ActionEvent event) {
        try {
            logger.info("onRequest Start");

            if (!this.store(false)) {
                return;
            }

            // 引数を設定
            ApprovalDialogEntity argument = new ApprovalDialogEntity();
            argument.setApprovalDataType(ApprovalDataTypeEnum.WORK);
            argument.setIsRequestTypeApproval(true);
            argument.setWork(this.workInfo);

            // 申請ダイアログを表示
            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.Approval"), "ApprovalDialog", argument, sc.getStage(), true);
            if (ret.equals(ButtonType.OK)) {
                // 工程マスタのキャッシュを削除する。
                CacheUtils.removeCacheData(WorkInfoEntity.class);

                // 画面を再描画
                this.updateView();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onRequest end.");
        }
    }

    /**
     * 申請取消ボタン押下時のアクション
     *
     * @param event イベント
     */
    @FXML
    private void onRequestCancel(ActionEvent event) {
        try {
            logger.info("onRequestCancel:Start");

            String message = LocaleUtils.getString("key.confirm.approvalRequestCancel");
            ButtonType confirmRet = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.confirm"), message);
            if (!confirmRet.equals(ButtonType.OK)) {
                return;
            }

            // 引数を設定
            ApprovalDialogEntity argument = new ApprovalDialogEntity();
            argument.setApprovalDataType(ApprovalDataTypeEnum.WORK);
            argument.setIsRequestTypeApproval(false);
            argument.setWork(this.workInfo);

            // 申請取消ダイアログを表示
            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.ApprovalCancel"), "ApprovalDialog", argument, sc.getStage(), true);
            if (ret.equals(ButtonType.OK)) {
                // 工程マスタのキャッシュを削除する。
                CacheUtils.removeCacheData(WorkInfoEntity.class);

                // 画面を再描画
                this.updateView();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程情報を保存する。
     * 処理の完了を待たずに制御を返す。
     *
     * @param closing
     * @return
     */
    private boolean store(boolean closing) {
        try {
            logger.info("store start.");

            // 未入力チェック
            if (!this.checkValue()) {
                return false;
            }

            // フォーマット確認
            if (!isValidItems()) {
                return false;
            }

            // ドキュメントがダウンロードされていない場合、タブが移動された時の再アップロードに備えるため、ダウンロードしておく
            //for (int ii = 0; ii < tabPane.getTabs().size() - 1; ii++) {
            //    Tab tab = tabPane.getTabs().get(ii);
            //    WorkSectionInfoEntity workSection = (WorkSectionInfoEntity) tab.getUserData();
            //    WorkSectionPane pane = (WorkSectionPane)((ScrollPane) tab.getContent()).getContent();
            //    if (workSection.hasDocument() && !pane.isLoaded()) {
            //        pane.download();
            //    }
            //}
            this.updateData();

            // 工程を追加
            String workId;
            if (Objects.isNull(this.workInfo.getWorkId()) || this.workInfo.getWorkId() == 0) {
                ResponseEntity res = workInfoFacade.registWork(this.workInfo);

                if (Objects.nonNull(res) && !ResponseAnalyzer.getAnalyzeResult(res)) {
                    Platform.runLater(() -> blockUI(false));
                    return false;
                }

                workId = res.getUri().substring(res.getUri().lastIndexOf("/") + 1);

                // 工程を再取得する。
                this.workInfo = workInfoFacade.find(Long.parseLong(workId), false, true);
            } else {
                workId = String.valueOf(this.workInfo.getWorkId());
            }

            Map<String, String> transfers = new HashMap<>();
            Set<String> deletes = new HashSet<>();

            // アップロードするファイルを抽出
            for (int ii = 0; ii < tabPane.getTabs().size() - 1; ii++) {
                Tab tab = tabPane.getTabs().get(ii);
                WorkSectionInfoEntity workSection = (WorkSectionInfoEntity) tab.getUserData();
                if (workSection.isChenged()) {
                    if (workSection.hasDocument()) {
                        transfers.put(workSection.getPhysicalName(), workSection.getSourcePath());
                        Thread.sleep(10L);
                    }
                }
            }

            if (!transfers.isEmpty() || !deletes.isEmpty()) {
                sc.blockUI(true);
                Stage stage = new Stage(StageStyle.UTILITY);

                try {
                    Label updateLabel = new Label();
                    updateLabel.setPrefWidth(300.0);
                    updateLabel.setText(LocaleUtils.getString("key.UploadDocuments"));

                    ProgressBar progress = new ProgressBar();
                    progress.setPrefWidth(300.0);
                    progress.setVisible(true);

                    VBox pane = new VBox();
                    pane.setPadding(new Insets(16.0, 16.0, 24.0, 16.0));
                    pane.setSpacing(8.0);
                    pane.getChildren().addAll(updateLabel, progress);

                    stage.setTitle("adManagerApp");
                    stage.setScene(new Scene(pane));
                    stage.setAlwaysOnTop(true);
                    stage.setOnCloseRequest(event -> {
                        logger.info("Closing the window...");
                        event.consume();
                    });
                    stage.show();

                    // ドキュメントをアップロード
                    URL url = new URL(ClientServiceProperty.getServerUri());
                    RemoteStorage storage = new HttpStorage();
                    storage.configuration(url.getHost(), null, null);

                    Task task = storage.newUploader(Paths.SERVER_UPLOAD_PDOC + "/" + String.valueOf(workId), transfers, deletes);
                    updateLabel.textProperty().bind(task.messageProperty());
                    progress.progressProperty().bind(task.progressProperty());

                    // アップロード成功時
                    task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
                        try {
                            logger.info("Upload succeeded.");

                            stage.close();

                            ResponseEntity res = this.workInfoFacade.updateWork(this.workInfo);
                            if (Objects.nonNull(res) && !ResponseAnalyzer.getAnalyzeResult(res)) {
                                if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                                    // 排他バージョンが異なる。
                                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistProcess"), LocaleUtils.getString("key.alert.differentVerInfo"));
                                }
                                return;
                            }

                            // 工程マスタのキャッシュを削除する。
                            CacheUtils.removeCacheData(WorkInfoEntity.class);

                            // 保存した工程を使用している工程順に、タクトタイムの変更を反映する
                            this.updateTaktTime(this.workInfo);

                            this.cloneWorkInfo = this.updateData().clone();

                            if (closing) {
                                this.downloading = false;
                                sc.setComponent("ContentNaviPane", "WorkEditCompo");
                                return;
                            }

                            // 適用ボタンが押された場合は、工程情報を読み直す
                            this.updateView();

                            // 開いているシートのドキュメントを読み直す
                            Tab tab = this.tabPane.getSelectionModel().getSelectedItem();
                            if (Objects.nonNull(tab)) {
                                WorkSectionPane workSectionPane = (WorkSectionPane)((ScrollPane) tab.getContent()).getContent();
                                workSectionPane.loadCacheData();
                            }

                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                        } finally {
                            this.downloading = false;
                            sc.blockUI(false);
                        }
                    });

                    // アップロード失敗
                    task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event -> {
                        logger.info("Upload failed.");

                        stage.close();
                        sc.blockUI(false);

                        this.showAlertUpload(((UITask) storage).messageProperty().get());
                        this.downloading = false;
                    });

                    ExecutorService executor = Executors.newSingleThreadExecutor();

                    // アップロードスレッドを実行
                    this.downloading = true;
                    executor.submit(task);
                    executor.shutdown();

                } catch (Exception ex) {
                    logger.fatal(ex, ex);

                    stage.close();
                    sc.blockUI(false);

                    this.showAlertUpload(ex.getMessage());
                    this.downloading = false;
                    return false;
                }

            } else {
                ResponseEntity res = this.workInfoFacade.updateWork(this.workInfo);
                if (Objects.nonNull(res) && !ResponseAnalyzer.getAnalyzeResult(res)) {
                    if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                        // 排他バージョンが異なる。
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistProcess"), LocaleUtils.getString("key.alert.differentVerInfo"));
                    }
                    return false;
                }

                // 工程マスタのキャッシュを削除する。
                CacheUtils.removeCacheData(WorkInfoEntity.class);

                this.cloneWorkInfo = this.updateData().clone();

                // 保存した工程を使用している工程順に、タクトタイムの変更を反映する
                this.updateTaktTime(this.workInfo);

                if (closing) {
                    sc.setComponent("ContentNaviPane", "WorkEditCompo");
                    return true;
                }

                // 適用ボタンが押された場合は、工程情報を読み直す
                this.updateView();
            }

            return true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            DialogBox.alert(ex);
            Platform.runLater(() -> blockUI(false));
            return false;

        } finally {
            logger.info("store end.");
        }
    }

    /**
     * 工程情報を保存する。
     * 処理が完了するまで制御を返さない。
     * 
     * @return 
     */
    private boolean waitForSaving() {
        try {
            logger.info("waitForSaving start.");

            // 未入力チェック
            if (!this.checkValue()) {
                return false;
            }

            // フォーマット確認
            if (!isValidItems()) {
                return false;
            }

            this.updateData();

            // 工程を追加
            String workId;
            if (Objects.isNull(this.workInfo.getWorkId()) || this.workInfo.getWorkId() == 0) {
                ResponseEntity res = workInfoFacade.registWork(this.workInfo);

                if (Objects.nonNull(res) && !ResponseAnalyzer.getAnalyzeResult(res)) {
                    Platform.runLater(() -> blockUI(false));
                    return false;
                }

                workId = res.getUri().substring(res.getUri().lastIndexOf("/") + 1);

                // 工程を再取得する。
                this.workInfo = workInfoFacade.find(Long.parseLong(workId), false, true);
            } else {
                workId = String.valueOf(this.workInfo.getWorkId());
            }

            Map<String, String> transfers = new HashMap<>();
            Set<String> deletes = new HashSet<>();

            // アップロードするファイルを抽出
            for (int ii = 0; ii < tabPane.getTabs().size() - 1; ii++) {
                Tab tab = tabPane.getTabs().get(ii);
                WorkSectionInfoEntity workSection = (WorkSectionInfoEntity) tab.getUserData();
                if (workSection.isChenged()) {
                    if (workSection.hasDocument()) {
                        transfers.put(workSection.getPhysicalName(), workSection.getSourcePath());
                        Thread.sleep(10L);
                    }
                }
            }

            if (!transfers.isEmpty() || !deletes.isEmpty()) {
                sc.blockUI(true);
                Stage stage = new Stage(StageStyle.UTILITY);

                try {
                    Label updateLabel = new Label();
                    updateLabel.setPrefWidth(300.0);
                    updateLabel.setText(LocaleUtils.getString("key.UploadDocuments"));

                    VBox pane = new VBox();
                    pane.setPadding(new Insets(16.0, 16.0, 24.0, 16.0));
                    pane.setSpacing(8.0);
                    pane.getChildren().add(updateLabel);

                    stage.setTitle("adManagerApp");
                    stage.setScene(new Scene(pane));
                    stage.setAlwaysOnTop(true);
                    stage.setOnCloseRequest(event -> {
                        logger.info("Closing the window...");
                        event.consume();
                    });
                    stage.show();

                    // ドキュメントをアップロード
                    URL url = new URL(ClientServiceProperty.getServerUri());
                    RemoteStorage storage = new HttpStorage();
                    storage.configuration(url.getHost(), null, null);

                    Object task = storage.createUploader(Paths.SERVER_UPLOAD_PDOC + "/" + String.valueOf(workId), transfers, deletes);

                    ExecutorService executor = Executors.newSingleThreadExecutor();

                    // アップロードスレッドを実行して、終了するまで待機する
                    Future<Boolean> future = executor.submit((Callable<Boolean>) task);
                    executor.shutdown();
                    Boolean succeeded = future.get();
                    logger.info("Upload thread end: " + succeeded);

                    stage.close();
                    sc.blockUI(false);

                    if (!succeeded) {
                        this.showAlertUpload(((UITask) task).messageProperty().get());
                        return false;
                    }

                } catch (Exception ex) {
                    logger.fatal(ex, ex);

                    stage.close();
                    sc.blockUI(false);

                    this.showAlertUpload(ex.getMessage());
                    return false;
                }
            }

            ResponseEntity res = workInfoFacade.updateWork(this.workInfo);
            if (Objects.nonNull(res) && !ResponseAnalyzer.getAnalyzeResult(res)) {
                if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                    // 排他バージョンが異なる。
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistProcess"), LocaleUtils.getString("key.alert.differentVerInfo"));
                }
                return false;
            }

            // 工程マスタのキャッシュを削除する。
            CacheUtils.removeCacheData(WorkInfoEntity.class);

            this.cloneWorkInfo = this.workInfo.clone();

            // 保存した工程を使用している工程順に、タクトタイムの変更を反映する
            this.updateTaktTime(this.workInfo);

            // 適用ボタンが押された場合は、工程情報を読み直す
            this.updateView();

            return true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            DialogBox.alert(ex);
            return false;

        } finally {
            Platform.runLater(() -> blockUI(false));
            logger.info("waitForSaving end.");
        }
    }

    /**
     * アップロードエラーを表示する。
     *
     * @param message 
     */
    private void showAlertUpload(String message) {
        sc.showAlert(Alert.AlertType.ERROR, "エラー", "ドキュメントをサーバーにアップロードできませんでした\r\n\r\n理由: " + message);
    }

    /**
     * 画面を更新する。
     */
    private void updateView() {
        this.workInfo = this.workInfoFacade.find(this.workInfo.getWorkId(), false, true);
        this.workInfo.getWorkSectionCollection().sort((a, b) -> a.getWorkSectionOrder() - b.getWorkSectionOrder());

        // 申請者名ラベル
        ApprovalInfoEntity approval = this.workInfo.getApproval();
        if (Objects.nonNull(approval)) {
            OrganizationInfoEntity organization = CacheUtils.getCacheOrganization(approval.getRequestorId());
            if (Objects.nonNull(organization)) {
                this.requestorNameLabel.setText(organization.getOrganizationName());
            }
        }
        
        // 各種入力項目の表示状態設定
        setInputItemViewState(topTable);

        for (int ii = 0; ii < this.workInfo.getWorkSectionCollection().size(); ii++) {
            WorkSectionInfoEntity section = this.workInfo.getWorkSectionCollection().get(ii);
            Tab tab = tabPane.getTabs().get(section.getWorkSectionOrder() - 1);

            // ドキュメントを読み直すため、リセットする
            WorkSectionPane workSectionPane = (WorkSectionPane)((ScrollPane) tab.getContent()).getContent();
            workSectionPane.reset();
            workSectionPane.setInputItemViewState(this.disableOtherInputItems);

            WorkSectionInfoEntity workSection = (WorkSectionInfoEntity) tab.getUserData();
            workSection.setFkWorkId(section.getFkWorkId());
            workSection.setWorkSectionId(section.getWorkSectionId());
            workSection.setChenged(false);
        }

        // タブ名の編集可否、削除ボタンの有効/無効状態を切り替え
        tabPane.getTabs().stream().forEachOrdered((tab) -> {
            RichTab richTab = (RichTab) tab;
            richTab.setEditable(!this.disableOtherInputItems);
            richTab.setClosable(!this.disableOtherInputItems);
        });
        
        // タブの+ボタンの有効/無効状態を切り替え
        Tab addTab = this.tabPane.getTabs().get(this.tabPane.getTabs().size() - 1);
        addTab.setDisable(this.disableOtherInputItems);
        this.cloneWorkInfo = this.workInfo.clone();
    }

    /**
     * 作業内容の表示ライン数を計算
     * @param str 入力文字列
     * @return 表示ライン数
     */
    private static final Pattern pattern = Pattern.compile("\r\n|\r|\n");
    private static int calculateContentLineNum(String str, int offset) {
        if (StringUtils.isEmpty(str)) {
            return 2;
        }

        int count = 0;
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            ++count;
        }
        return Math.min(Math.max(count+offset, 2), 10);
    }

    /**
     * 工程データを表示する。
     */
    private void show() {
        try {
            final long range = ClientServiceProperty.getRestRangeNum();

            // 工程セクションマップを取得
            workSections = getWorkSections(true);

            this.parentNameLabel.setText(this.selected.getHierarchyName());

            // 申請者中ラベル
            this.requestingLabel.setTextFill(Color.RED);

            // 申請者名ラベル
            ApprovalInfoEntity approval = this.workInfo.getApproval();
            if (Objects.nonNull(approval)) {
                OrganizationInfoEntity organization = CacheUtils.getCacheOrganization(approval.getRequestorId());
                if (Objects.nonNull(organization)) {
                    this.requestorNameLabel.setText(organization.getOrganizationName());
                }
            }

            this.topSidePane.getChildren().clear();

            String taktTime = StringTime.convertMillisToStringTime(this.workInfo.getTaktTime());

            this.properties.clear();
            this.properties.put("workName", PropertyBindEntity.createString(LocaleUtils.getString("key.ProcessName") + LocaleUtils.getString("key.RequiredMark"), this.workInfo.getWorkName()));

            // 作業番号 (enableDailyReport=true の場合のみ表示)
            if (WorkflowEditConfig.getEnableDailyReport()) {
                this.properties.put("workNumber", PropertyBindEntity.createString(LocaleUtils.getString("key.IndirectWorkNumber"), this.workInfo.getWorkNumber()));
            }

            this.properties.put("taktTime", PropertyBindEntity.createTimeStamp(LocaleUtils.getString("key.TactTime") + LocaleUtils.getString("key.TimeTitle"), taktTime));
            this.properties.put("content", PropertyBindEntity.createTextArea(LocaleUtils.getString("key.WorkContent"), this.workInfo.getContent()));

            //背景色・文字色の表示
            Color backColor = null;
            Color fontColor = null;
            try {
                backColor = Color.web(this.workInfo.getBackColor());
                fontColor = Color.web(this.workInfo.getFontColor());
            } catch (IllegalArgumentException | NullPointerException ex) {
                logger.fatal(ex, ex);
            }
            this.properties.put("backColor", PropertyBindEntity.createColorPicker(LocaleUtils.getString("key.BackColor"), backColor));
            this.properties.put("fontColor", PropertyBindEntity.createColorPicker(LocaleUtils.getString("key.FontColor"), fontColor));

            // 追加情報表示設定
            EventHandler<ActionEvent> onChangeDispAddInfoHandler = (e) -> {
                this.onChangeDispAddInfo(e);
            };
            this.properties.put("dispAddInfo", PropertyBindEntity.createButton(LocaleUtils.getString("key.DispAddInfo"), LocaleUtils.getString("key.Change"), onChangeDispAddInfoHandler, null));

            // １カラム目の横幅を設定
            List<ColumnConstraints> constraints = new ArrayList<>();
            constraints.add(new ColumnConstraints(175.0, -1.0, -1.0));

            topTable = new Table(this.topSidePane.getChildren()).isAddRecord(false).footerPadding(new Insets(0, 0, 0, 0)).bodyColumnConstraints(constraints);
            topTable.setAbstractRecordFactory(new WorkRecordFactory(topTable, new LinkedList(properties.values())));

            // テーブルに版数のコントロールを追加
            createAdditionalColumn(topTable);

            // 作業内容の右クリックメニューに「リンクの挿入」と「画像の挿入」を追加
            Platform.runLater(() -> {
                topTable.findLabelRow(LocaleUtils.getString("key.WorkContent")).ifPresent(index -> {
                    contentTextArea = (TextArea) topTable.getNodeFromBody((int) index, 1).get();
                    contentTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
                        contentTextArea.setPrefRowCount(calculateContentLineNum(newValue,1));
                    });
                   
                    // スクロールバーがあると正しく計算が出来ない
                    contentTextArea.setPrefRowCount(calculateContentLineNum(contentTextArea.getText(),2));
                    
                    MenuItem insertLink = new MenuItem(LocaleUtils.getString("key.InsertLink"));
                    insertLink.setOnAction(this::onInsertLink);

                    MenuItem insertImage = new MenuItem(LocaleUtils.getString("key.InsertImage"));
                    insertImage.setOnAction(this::onInsertAddress);
                    
                    ContextMenu contextMenu = new ContextMenu();
                    contextMenu.getItems().addAll(insertLink, insertImage);
                    contentTextArea.setContextMenu(contextMenu);
                });
            });

            // 各種入力項目の表示状態設定
            setInputItemViewState(topTable);
           
            // カスタムフィールド表示
            if (!Objects.nonNull(this.workInfo.getPropertyInfoCollection())) {
                this.workInfo.setPropertyInfoCollection(new ArrayList<>());
            }

            this.workProperties.sort(Comparator.comparing(property -> property.getWorkPropOrder()));

            // トレーサビリティ
            if (ClientServiceProperty.isLicensed("@Traceability")) {
                EquipmentSearchCondition condition = new EquipmentSearchCondition();

                // 製造設備
                condition.setEquipmentType(EquipmentTypeEnum.MANUFACTURE);
                long count = this.equipmentInfoFacade.countSearch(condition);
                for (long ii = 0; ii <= count; ii += range) {
                    manufactureEquipments.addAll(this.equipmentInfoFacade.findSearchRange(condition, ii, ii + range - 1));
                }

                // 測定機器
                condition.setEquipmentType(EquipmentTypeEnum.MEASURE);
                count = this.equipmentInfoFacade.countSearch(condition);
                for (long ii = 0; ii <= count; ii += range) {
                    measureEquipments.addAll(this.equipmentInfoFacade.findSearchRange(condition, ii, ii + range - 1));
                }

                // 使用部品
                Pattern pattern = Pattern.compile("\\(\\d+\\)");
                String objectIds = this.workInfo.getUseParts();
                if (!StringUtils.isEmpty(objectIds)) {
                    for (String objectId : objectIds.split(",")) {
                        Matcher matcher = pattern.matcher(objectId);
                        String objectType = null;
                        int index = 0;
                        while (matcher.find()) {
                            objectType = matcher.group();
                            index = objectId.lastIndexOf(objectType);
                            objectType = objectType.substring(1, objectType.length() - 1);
                        }
                        objectId = objectId.substring(0, index);
                        Long objectTypeId = StringUtils.isEmpty(objectType) ? null : Long.parseLong(objectType);
                        ObjectInfoEntity objectInfo = objectInfoFacede.get(objectId, objectTypeId);
                        this.useParts.add(objectInfo);
                    }
                }

                this.usePartsTextArea.setText(objectIds);

                // 追加情報
                Table table = new Table(propertyPane.getChildren());
                table.isAddRecord(true);
                table.isColumnTitleRecord(true);
                table.title(LocaleUtils.getString("key.CustomField"));
                table.styleClass("ContentTitleLabel");
                table.setAbstractRecordFactory(new WorkPropertyRecordFactory(table, workProperties));
                
                // タブの生成
                CreateTabs(true);

                this.tabPane.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    if (newValue.intValue() < this.tabPane.getTabs().size() - 1) {
                        Tab tab = this.tabPane.getTabs().get(newValue.intValue());
                        WorkSectionPane pane = (WorkSectionPane)((ScrollPane) tab.getContent()).getContent();
                        if (Objects.nonNull(pane)) {
                            pane.loadCacheData();
                            pane.adjustLayout();
                        }
                    }
                });

                // 初期値をコピー
                this.cloneWorkInfo = this.updateData().clone();
            } else {
                this.tabPane.setVisible(false);
            }
        } finally {
            this.tabPane.getTabs().addListener(this);
        }
    }

    /**
     * クリップボードへエンティティをコピー
     *
     * @param entity コピー対象
     * @return true:成功 / false:失敗
     */
    private boolean copyToClipBord(List<WorkSectionInfoEntity> entity) {
        try {
            logger.info("onTabInfoCopy:Start");
            // クリップボードにデータを保存
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();

            DataFormat dataFormat = DataFormatUtil.getDataFormat(WorkSectionInfoEntity[].class);
            content.put(dataFormat, entity.toArray());
            return clipboard.setContent(content);
        } finally {
            logger.info("onTabInfoCopy:end");
        }
    }

    /**
     * クリップボードからエンティティを取得
     *
     * @return
     */
    private Optional<List<WorkSectionInfoEntity>> getEntityFromClipBoard() {
        try {
            logger.info("onTabInfoPasting:Start");

            //システム・クリップボードを取得
            Clipboard clipboard = Clipboard.getSystemClipboard();
            DataFormat dataFormat = DataFormatUtil.getDataFormat(WorkSectionInfoEntity[].class);

            // クリップボードにシートのデータ以外が保存されちる場合は処理を中断
            if (!clipboard.hasContent(dataFormat)) {
                logger.info("false onTabInfoPasting:end");
                return Optional.empty();
            }

            // クリップボードからデータを取得
            Object obj = clipboard.getContent(dataFormat);
            int length = Array.getLength(obj);
            List<WorkSectionInfoEntity> ret = new ArrayList<>();
            for(int n=0; n<length; ++n) {
                ret.add((WorkSectionInfoEntity) Array.get(obj, n));
            }
            return Optional.of(ret);
        } finally {
            logger.info("onTabInfoPasting:end");
        }
    }

    /**
     * 工程セクションマップを取得する。
     *
     * @param isInitialize 初期表示処理かどうか
     * @return 工程セクションマップ
     */
    private Map<Integer, WorkSectionInfoEntity> getWorkSections(boolean isInitialize) {
        final Map<Integer, WorkSectionInfoEntity> workSections = new HashMap<>();

        for (WorkSectionInfoEntity workSection : this.workInfo.getWorkSectionCollection()) {
            workSections.put(workSection.getWorkSectionOrder(), workSection);
        }

        for (WorkPropertyInfoEntity property : this.workInfo.getPropertyInfoCollection()) {
            WorkPropertyCategoryEnum category = property.getWorkPropCategory();
            if (Objects.isNull(category) || WorkPropertyCategoryEnum.INFO.equals(category)) {
                if (isInitialize) {
                    workProperties.add(property);
                }
            } else {
                WorkSectionInfoEntity workSection;
                Integer workSectionOrder = Objects.nonNull(property.getWorkSectionOrder()) ? property.getWorkSectionOrder() : 0;

                if (workSections.containsKey(workSectionOrder)) {
                    workSection = workSections.get(workSectionOrder);
                } else {
                    workSection = new WorkSectionInfoEntity();
                    workSections.put(workSectionOrder, workSection);
                }

                workSection.getTraceabilityCollection().add(property);
            }
        }

        return workSections;
    }

    /**
     * タブを生成する。
     *
     * @param isInitialize 初期表示処理かどうか
     */
    private void CreateTabs(boolean isInitialize) {
        if (workSections.isEmpty()) {
            // 新規作成の場合
            WorkSectionInfoEntity workSection = new WorkSectionInfoEntity();
            RichTab tab = this.tabPane.addTab();
            this.setupTab(tab, workSection);
        } else {
            workSections.values().stream().map((workSection) -> workSection.clone()).forEachOrdered((clone) -> {
                RichTab tab = this.tabPane.addTab(clone.getDocumentTitle());
                tab.setEditable(!this.disableOtherInputItems);
                tab.setClosable(!this.disableOtherInputItems);
                this.setupTab(tab, clone);
            });

            if (isInitialize) {
                this.tabPane.selectFirst();
            }

            Tab tab = this.tabPane.getTabs().get(0);
            WorkSectionPane pane = (WorkSectionPane)((ScrollPane) tab.getContent()).getContent();
            pane.loadCacheData();

            // タブ追加ボタンの有効/無効を切り替える
            Tab addTab = this.tabPane.getTabs().get(this.tabPane.getTabs().size() - 1);
            addTab.setDisable(this.disableOtherInputItems);
        }
    }

    /**
     * タブを初期化する。
     *
     * @param tab
     * @param workSection
     */
    private void setupTab(RichTab tab, WorkSectionInfoEntity workSection) {
        
        //工程情報workInfoパラメータをセット
        WorkSectionPane pane = WorkSectionPane.newInstance(workSection, this.useParts, this.manufactureEquipments, this.measureEquipments, rb, this.disableOtherInputItems, this.workInfo);

        // ページ分割チェックボックスの状態を復元
        if (!workSection.getTraceabilityCollection().isEmpty()) {
            WorkPropertyInfoEntity firstProperty = workSection.getTraceabilityCollection().get(0);
            boolean pageBreakValue = Boolean.TRUE.equals(firstProperty.getPageBreakEnabled());
            pane.getPageBreakCheckBox().setSelected(pageBreakValue);
        }

        tab.setUserData(workSection);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(pane);
        scrollPane.setUserData(workSection);
        tab.setContent(scrollPane);
        
        // ドキュメント名
        if (StringUtils.isEmpty(workSection.getDocumentTitle())) {
            workSection.setDocumentTitle(tab.getName());
        } else {
            tab.setName(workSection.getDocumentTitle());
        }

        tab.getLabel().textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            workSection.setDocumentTitle(newValue);
        });

        // TabPaneをリサイズ
        pane.getPane().heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            Platform.runLater(() -> this.tabPane.requestLayout());
        });

        tab.selectedProperty().addListener((observable -> {
            pane.reflesh();
        }));

    }

    /**
     * 版数フィールドと改訂ボタンを追加する
     *
     * @param detailTable 詳細テーブル
     */
    private void createAdditionalColumn(Table detailTable) {

        //「版数」フィールドの作成
        Integer rev = this.workInfo.getWorkRev();
        this.revisionField = Objects.isNull(this.revisionField) ? new TextField() : this.revisionField;
        this.revisionField.setText(Objects.nonNull(rev) ? rev.toString() : "1");
        this.revisionField.getStyleClass().add("ContentTextBox");
        this.revisionField.setPrefWidth(60);
        this.revisionField.setDisable(true);

        //「改訂」ボタンの作成
        this.reviseButton = Objects.isNull(this.reviseButton)
                ? new Button(LocaleUtils.getString("key.revise")) : this.reviseButton;
        this.reviseButton.getStyleClass().add("ContentTextBox");
        this.reviseButton.setOnAction(this::onRevise);

        // runLater内で追加しないと表示しないため注意
        Platform.runLater(() -> {
            detailTable.findLabelRow(LocaleUtils.getString("key.ProcessName") + LocaleUtils.getString("key.RequiredMark")).ifPresent(index -> {
                detailTable.addNodeToBody(revisionField, 2, (int) index);
                detailTable.addNodeToBody(reviseButton, 3, (int) index);
            });
        });
    }

    /**
     * 改訂ボタン押下時のアクション
     *
     * @param event イベント
     */
    private void onRevise(ActionEvent event) {
        try {
            logger.info("onRevise:Start");

            if (!destoryComponent()) {
                return;
            }

            WorkInfoEntity work = this.workInfo;

            String message = String.format(LocaleUtils.getString("key.revise.inquiryMessage"), LocaleUtils.getString("key.Process"), work.getLatestRev());
            ButtonType ret = sc.showOkCanselDialog(AlertType.CONFIRMATION, LocaleUtils.getString("key.Process"), message);
            if (ret.equals(ButtonType.OK)) {
                ResponseWorkInfoEntity response = workInfoFacade.revise(work.getWorkId());
                if (response.isSuccess()) {
                    work = response.getValue();
                    this.revisionField.setText(work.getWorkRev().toString());
                    this.workInfo.setWorkId(work.getWorkId());

                    // 画面を再描画
                    this.updateView();
                } else {
                    ResponseAnalyzer.getAnalyzeResult(response);
                }
            }
        } finally {
            logger.info("onRevise:end");
        }
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
        DataFormat dataFormat = DataFormatUtil.getDataFormat(WorkSectionInfoEntity[].class);

        // クリップボードにコピー済みか?
        final boolean isCopied = !clipboard.hasContent(dataFormat);

        // 承認機能のライセンスが無効か?
        if (!this.isLicensedApproval) {
            return isCopied;
        }

        // 状態の取得が可能か?
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

    /**
     *
     * @return
     */
    private boolean isDeleteMenuDisable() {

        if(ApprovalStatusEnum.APPLY.equals(workInfo.getApprovalState()) // 申請中
                || ApprovalStatusEnum.APPROVE.equals(workInfo.getApprovalState())) { // 承認済み
            return true;
        }

        // クリップボードにコピー済みか?
        final boolean isSelected = this.tabPane.isSelected();
        logger.info("aaaa" + isSelected);

        // 承認機能のライセンスが無効か?
        if (!this.isLicensedApproval) {
            return !isSelected;
        }

        // 状態の取得が可能か?
        if (Objects.isNull(this.workInfo) || Objects.isNull(this.workInfo.getApprovalState())) {
            return !isSelected;
        }

        switch (this.workInfo.getApprovalState()) {
            case UNAPPROVED: //未承認
            case CANCEL_APPLY: // 取り消し
            case REJECT: // 却下
                return !isSelected;
            case APPLY: // 申請中
            case FINAL_APPROVE: // 最終承認済み
            case APPROVE: // 祖容認済み(承認フローで使用)
            default: // 在りえない
                return true;
        }
    }

    /**
     * 各種入力項目の表示状態を設定する。
     * 
     * @param detailTable 詳細テーブル 
     */
    private void setInputItemViewState(Table detailTable) {
        // 申請ボタン、申請取消ボタン、申請中ラベル、申請者名ラベルの初期値は非表示
        boolean visibleRequestButton = false;
        boolean visibleRequestCancelButton = false;
        boolean visibleRequestingLabel = false;
        boolean visibleRequesorNameLabel = false;

        // 申請ボタン、申請取消ボタン、登録ボタン、適用ボタン、改訂ボタン、その他入力項目の初期値は無効
        // 工程名フィールドの初期値は有効
        boolean disableRequestButton = true;
        boolean disableRequestCancelButton = true;
        boolean disableRegistButton = true;
        boolean disableApplyButton = true;
        this.disableReviseButton = true;
        this.disableOtherInputItems = true;
        this.disableWorkNameField = false;

        Integer latestRev = this.workInfo.getLatestRev();

        // 各種入力項目の表示状態(表示/非表示、有効/無効)を判定
        if (!isLicensedApproval) {
            // 承認機能オプションが無効
            if (this.loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
                // 工程・工程順編集権限あり
                disableRegistButton = false;
                disableApplyButton = false;
                this.disableOtherInputItems = false;

                if (Objects.nonNull(this.workInfo.getWorkId()) && this.workInfo.getWorkRev().equals(latestRev)) {
                    // 選択した工程順が最新版数
                    this.disableReviseButton = false;
                }
                if (Objects.nonNull(latestRev)) {
                    if (latestRev > 1) {
                        this.disableWorkNameField = true;
                    }
                }
            } else {
                // 工程・工程順編集権限なし
                this.disableOtherInputItems = false;
            }
        } else {
            // 承認機能オプションが有効
            visibleRequestButton = true;
            visibleRequestCancelButton = true;

            if (!this.loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
                // 工程・工程順編集権限なし
                this.disableOtherInputItems = false;
                if (Objects.nonNull(this.workInfo.getApprovalId())) {
                    // 申請情報あり
                    if (ApprovalStatusEnum.APPLY.equals(this.workInfo.getApprovalState())) {
                        // 申請中
                        visibleRequestingLabel = true;
                        visibleRequesorNameLabel = true;
                    }
                }
            } else {
                // 工程・工程順編集権限あり
                if (Objects.nonNull(this.workInfo.getApprovalState())) {
                    // 編集モード
                    switch (this.workInfo.getApprovalState()) {
                        case UNAPPROVED:
                            disableRequestButton = false;
                            disableRegistButton = false;
                            disableApplyButton = false;
                            this.disableOtherInputItems = false;
                            if (this.workInfo.getWorkRev() > 1) {
                                this.disableWorkNameField = true;
                            }
                            break;
                        case APPLY:
                            visibleRequestingLabel = true;
                            visibleRequesorNameLabel = true;
                            if (this.loginUser.getId().equals(this.workInfo.getApproval().getRequestorId())) {
                                disableRequestCancelButton = false;
                            }
                            this.disableWorkNameField = true;
                            break;
                        case CANCEL_APPLY:
                            disableRequestButton = false;
                            disableRegistButton = false;
                            disableApplyButton = false;
                            this.disableOtherInputItems = false;
                            if (this.workInfo.getWorkRev() > 1) {
                                this.disableWorkNameField = true;
                            }
                            break;
                        case REJECT:
                            disableRequestButton = false;
                            disableRegistButton = false;
                            disableApplyButton = false;
                            this.disableOtherInputItems = false;
                            if (this.workInfo.getWorkRev() > 1) {
                                this.disableWorkNameField = true;
                            }
                            break;
                        case FINAL_APPROVE:
                            if (this.workInfo.getWorkRev().equals(latestRev)) {
                                // 選択した工程順が最新版数
                                this.disableReviseButton = false;
                            }
                            this.disableWorkNameField = true;
                            break;
                        default:
                            break;
                    }
                } else {
                    // 新規作成モード
                    disableRequestButton = false;
                    disableRegistButton = false;
                    disableApplyButton = false;
                    this.disableOtherInputItems = false;
                }
            }
        }

        // 申請ボタン、申請取消ボタン、申請中ラベル、申請者名ラベルの表示/非表示を切り替える
        requestButton.setVisible(visibleRequestButton);
        requestButton.setManaged(visibleRequestButton);
        requestCancelButton.setVisible(visibleRequestCancelButton);
        requestCancelButton.setManaged(visibleRequestCancelButton);
        applyWorkflowButton.setVisible(!isLicensedApproval);
        applyWorkflowButton.setManaged(!isLicensedApproval);
        requestingLabel.setVisible(visibleRequestingLabel);
        requestorNameLabel.setVisible(visibleRequesorNameLabel);

        // 申請ボタン、申請取消ボタン、登録ボタン、適用ボタンの有効/無効を切り替える
        requestButton.setDisable(disableRequestButton);
        requestCancelButton.setDisable(disableRequestCancelButton);
        registButton.setDisable(disableRegistButton);
        applyButton.setDisable(disableApplyButton);
        cancelButton.setDisable(false);

        // 上記以外の入力項目の有効/無効を切り替える
        Platform.runLater(() -> {
            // 工程名フィールド
            detailTable.findLabelRow(LocaleUtils.getString("key.ProcessName") + LocaleUtils.getString("key.RequiredMark")).ifPresent(index -> {
                // 工程名フィールド
                TextField textField = (TextField) detailTable.getNodeFromBody((int) index, 1).get();
                textField.setDisable(this.disableWorkNameField);

                // 改訂ボタン
                Button button = (Button) detailTable.getNodeFromBody((int) index, 3).get();
                button.setDisable(this.disableReviseButton);
            });
            // 作業番号フィールド
            detailTable.findLabelRow(LocaleUtils.getString("key.IndirectWorkNumber")).ifPresent(index -> {
                TextField textField = (TextField) detailTable.getNodeFromBody((int) index, 1).get();
                textField.setDisable(this.disableOtherInputItems);
            });
            // タクトタイムフィールド
            detailTable.findLabelRow(LocaleUtils.getString("key.TactTime") + LocaleUtils.getString("key.TimeTitle")).ifPresent(index -> {
                TextField textField = (TextField) detailTable.getNodeFromBody((int) index, 1).get();
                textField.setDisable(this.disableOtherInputItems);
            });
            // 作業内容フィールド
            detailTable.findLabelRow(LocaleUtils.getString("key.WorkContent")).ifPresent(index -> {
                TextArea textArea = (TextArea) detailTable.getNodeFromBody((int) index, 1).get();
                textArea.setDisable(this.disableOtherInputItems);
            });
            // 背景色カラーピッカー
            detailTable.findLabelRow(LocaleUtils.getString("key.BackColor")).ifPresent(index -> {
                ColorPicker colorPicker = (ColorPicker) detailTable.getNodeFromBody((int) index, 1).get();
                colorPicker.setDisable(this.disableOtherInputItems);
            });
            // 文字色カラーピッカー
            detailTable.findLabelRow(LocaleUtils.getString("key.FontColor")).ifPresent(index -> {
                ColorPicker colorPicker = (ColorPicker) detailTable.getNodeFromBody((int) index, 1).get();
                colorPicker.setDisable(this.disableOtherInputItems);
            });
        });

        // 使用部品HBOX、追加情報VBOX
        usePartsPane.setDisable(this.disableOtherInputItems);
        propertyPane.setDisable(this.disableOtherInputItems);
    }

    /**
     * 「工程順にタクトタイムの変更を反映する」の確認ダイアログ表示
     *
     * @param entity 工程順に反映する工程
     */
    private void updateTaktTime(WorkInfoEntity entity) {
        if (Objects.isNull(entity.getWorkId())) {
            return;
        } else if (Objects.isNull(oldTaktTime) || Objects.equals(oldTaktTime, entity.getTaktTime())) {
            // タクトタイムが変更されていない、またはタクトタイムがNULL(新規作成)の場合スケジュール変更確認のダイアログを表示しない
            return;
        }

        logger.info(WorkDetailCompoFxController.class.getName() + ":showRescheduleDialog start");

        ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.WorkflowReschedule"), LocaleUtils.getString("key.WorkflowRescheduleMessage"));
        if (ret.equals(ButtonType.OK)) {
            List<WorkflowInfoEntity> entitys = workInfoFacade.reschedule(entity, Boolean.valueOf(AdProperty.getProperties().getProperty("work_reschedule_isShift", "true")));
            if (Objects.isNull(entitys)) {
                // エラーダイアログ
            }
            // 工程順マスタのキャッシュを削除する。
            CacheUtils.removeCacheData(WorkflowInfoEntity.class);

            //適用ボタン押下時にタクトタイムを変更したとき登録ボタン押下で再表示されるのを防ぐ
            this.oldTaktTime = entity.getTaktTime();
        }

        logger.info(WorkDetailCompoFxController.class.getName() + ":showRescheduleDialog end");
    }

    /**
     * 未入力の項目があるかチェックする。
     *
     * @return true: 未入力の項目あり、 false: 未入力の項目なし
     */
    private boolean checkValue() {
        String workName = ((StringProperty) this.properties.get("workName").getProperty()).get();
        if (Objects.isNull(workName) || workName.isEmpty()) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
            return false;
        }

        for (WorkPropertyInfoEntity entity : this.workProperties) {
            entity.updateMember();
            if (Objects.isNull(entity.getWorkPropName())
                    || entity.getWorkPropName().isEmpty()
                    || Objects.isNull(entity.getWorkPropType())) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return false;
            }
        }

        if (ClientServiceProperty.isLicensed("@Traceability")) {
            
            for (int ii = 0; ii < tabPane.getTabs().size() - 1; ii++) {
                WorkSectionInfoEntity workSection = (WorkSectionInfoEntity) tabPane.getTabs().get(ii).getUserData();

                for (WorkPropertyInfoEntity entity : workSection.getTraceabilityCollection()) {
                    if (Objects.isNull(entity.getWorkPropCategory()) || Objects.isNull(entity.getWorkPropName()) || entity.getWorkPropName().isEmpty()) {
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                        return false;
                    }
                    
                    if (!StringUtils.isEmpty(entity.getWorkPropValidationRule())) {
                        try {
                            Pattern.compile(entity.getWorkPropValidationRule());
                        } catch (PatternSyntaxException e) {
                            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("patternSyntaxError"), e.getLocalizedMessage());
                            return false;
                        }
                    }
                    
                    try {
                        // プラグイン名が「ColorInspect」の場合、現在値に部品No(1～2桁の数字)が設定されているか
                        if (WorkPropertyCategoryEnum.CUSTOM.equals(entity.getWorkPropCategory())
                            && !StringUtils.isEmpty(entity.getWorkPropOption())) {

                            TraceSettingEntity setting = (TraceSettingEntity) XmlSerializer.deserialize(TraceSettingEntity.class, entity.getWorkPropOption());
                            Optional<TraceOptionEntity> opt = setting.getTraceOptions().stream().filter(o -> TraceOptionTypeEnum.PLUGIN.equals(o.getKey())).findFirst();

                            if (opt.isPresent() && "ColorInspect".equalsIgnoreCase(opt.get().getValue())) {
                                if (!Pattern.compile("^[1-9]$|^[1-9][0-9]$").matcher(entity.getWorkPropValue()).matches()) {
                                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), "現在値に部品No (1～2桁の数字)を入力してください", entity.getWorkPropName());
                                    return false;
                                }
                            }

                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }
            }
        }

        return true;
    }
   
    /**
     * 現在表示されている情報を取得する
     *
     * @return
     */
    private WorkInfoEntity updateData() {
        this.workInfo.setWorkName(((StringProperty) properties.get("workName").getProperty()).get());

        // 作業番号 (enableDailyReport=true の場合のみ)
        if (WorkflowEditConfig.getEnableDailyReport()) {
            String workNumber = null;
            if (Objects.nonNull(properties.get("workNumber").getProperty())) {
                workNumber = ((StringProperty) properties.get("workNumber").getProperty()).get();
            }
            this.workInfo.setWorkNumber(workNumber);
        }

        Long taktTime = StringTime.convertStringTimeToMillis(((StringProperty) properties.get("taktTime").getProperty()).get());
        this.workInfo.setTaktTime(taktTime.intValue());
        this.workInfo.setContent(((StringProperty) properties.get("content").getProperty()).get());
        this.workInfo.setContentType(ContentTypeEnum.STRING);
        Color backColor = ((ObjectProperty<Color>) properties.get("backColor").getProperty()).get();
        this.workInfo.setBackColor(StringUtils.colorToRGBCode(backColor));
        Color fontColor = ((ObjectProperty<Color>) properties.get("fontColor").getProperty()).get();
        this.workInfo.setFontColor(StringUtils.colorToRGBCode(fontColor));
        this.workInfo.setUpdatePersonId(loginUser.getId());
        this.workInfo.setUpdateDatetime(new Date());
        this.workInfo.setUseParts(usePartsTextArea.getText());

        int workPropOrder = 1;
        List<WorkPropertyInfoEntity> newWorkProperties = new LinkedList<>();
        List<WorkSectionInfoEntity> newWorkSections = new LinkedList<>();

        for (WorkPropertyInfoEntity entity : workProperties) {
            entity.setWorkPropOrder(workPropOrder);
            newWorkProperties.add(entity);
            workPropOrder++;
        }

        for (int ii = 0; ii < tabPane.getTabs().size() - 1; ii++) {
            Tab tab = tabPane.getTabs().get(ii);

            int sectionOrder = ii + 1;
            WorkSectionInfoEntity workSection = (WorkSectionInfoEntity) tab.getUserData();

            // シート順序が変わったか
            //if (Objects.nonNull(workSection.getWorkSectionOrder()) && workSection.getWorkSectionOrder() != sectionOrder) {
            //    workSection.setChenged(true);
            //    workSection.setFileUpdated(this.workInfo.getUpdateDatetime());
            //}
            workSection.setWorkSectionOrder(sectionOrder);
            newWorkSections.add(workSection);

            // ページ分割値を保存する。
            WorkSectionPane pane = (WorkSectionPane) ((ScrollPane) tab.getContent()).getContent();
            CheckBox pageBreakCheckBox = pane.getPageBreakCheckBox();
            boolean pageBreakValue = Boolean.TRUE.equals(pageBreakCheckBox != null ? pageBreakCheckBox.isSelected() : false);

            for (WorkPropertyInfoEntity entity : workSection.getTraceabilityCollection()) {
                entity.setWorkPropId(null);
                entity.setWorkPropType(CustomPropertyTypeEnum.TYPE_STRING);
                entity.setWorkPropOrder(workPropOrder);
                entity.setWorkSectionOrder(workSection.getWorkSectionOrder());
                entity.setPageBreakEnabled(pageBreakValue);
                entity.updateMember();
                newWorkProperties.add(entity);
                workPropOrder++;
            }
        }

        newWorkSections.sort((a, b) -> a.getWorkSectionOrder() - b.getWorkSectionOrder());
        this.workInfo.setPropertyInfoCollection(newWorkProperties);
        this.workInfo.setWorkSectionCollection(newWorkSections);

        return this.workInfo;
    }

    /**
     * 操作をロックする。
     *
     * @param block
     */
    private void blockUI(Boolean block) {
        sc.blockUI("ContentNaviPane", block);
        this.progressPane.setVisible(block);
    }

    /**
     * 変更内容があるかを返す。
     *
     */
    private boolean isChanged() {
        // 編集権限なし、または、編集不可モード時は常に無変更
        if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW) || this.disableOtherInputItems) {
            return false;
        }

        return !this.updateData().displayInfoEquals(this.cloneWorkInfo);
    }

    /**
     * 画面破棄時に内容に変更がないか調べて変更があるなら保存する
     *
     * @return
     */
    @Override
    public boolean destoryComponent() {
        try {
            logger.info("destoryComponent start.");

            if (this.downloading) {
                return false;
            }

            boolean ret = true;
            
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");
                
                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    ret = this.waitForSaving();
                } else if (ButtonType.CANCEL == buttonType) {
                    ret = false;
                }
            }

            if (ret) {
                // イベントハンドラーの破棄
                sc.getWindow().removeEventHandler(KeyEvent.KEY_PRESSED, pasteImageHandler);
            }
            
            SplitPaneUtils.saveDividerPosition(reportOutListPane, getClass().getSimpleName());


            return ret;

        } finally {
            logger.info("destoryComponent end.");
        }
    }

    /**
     * 入力項目チェック
     *
     */
    private boolean isValidItems() {
         // 正規表現チェック
        for (WorkPropertyInfoEntity entity : workProperties) {
            String propVal = Objects.nonNull(entity.getWorkPropValue()) ? entity.getWorkPropValue() : "";
            if (templateRegexTexts.containsKey(entity.getWorkPropName())
                    && !Pattern.matches(templateRegexTexts.get(entity.getWorkPropName()), propVal)) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"),
                        String.format(LocaleUtils.getString("key.PropValueFormatErrMessage"), entity.getWorkPropName()));
                return false;
            }
        }

        return true;
    }

    /**
     * キャッシュの中の不要なデータを削除する。
     */
    private void cleanCache() {

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logger.info("cleanCache start.");

                File dir = new File(Paths.CLIENT_CACHE_PDOC + File.separator + String.valueOf(workInfo.getWorkId()));
                File[] list = dir.listFiles();
                for (File file : list) {

                    boolean delete = true;
                    for (WorkSectionInfoEntity workSection : workInfo.getWorkSectionCollection()) {
                        if (workSection.hasDocument()) {
                            if (file.getName().equals(workSection.getPhysicalName())) {
                                delete = false;
                                break;
                            }
                        }
                    }

                    if (delete) {
                        logger.info("Delete the file: " + file.getName());
                        if (!file.delete()) {
                            logger.info("Could not delete the file: " + file.getName());
                        }
                    }
                }

                logger.info("cleanCache end.");
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }
    
    /**
     * 工程順へ適用
     * 
     * @param event 
     */
    @FXML
    private void onApplyWorkflow(ActionEvent event) {
        sc.showDialog(LocaleUtils.getString("applyWorkflowTitle"), "ApplyWorkflowDialog", this.workInfo, sc.getStage(), true);
    }
}
