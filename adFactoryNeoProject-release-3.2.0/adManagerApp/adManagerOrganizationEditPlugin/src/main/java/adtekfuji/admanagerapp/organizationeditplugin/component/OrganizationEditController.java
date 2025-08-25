/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.organizationeditplugin.component;

import adtekfuji.admanagerapp.organizationeditplugin.common.BreakTimeIdData;
import adtekfuji.admanagerapp.organizationeditplugin.common.BreakTimePropertyRecordFactory;
import adtekfuji.admanagerapp.organizationeditplugin.common.ImportOrganizationData;
import adtekfuji.admanagerapp.organizationeditplugin.common.LocaleFileRecordFactory;
import adtekfuji.admanagerapp.organizationeditplugin.common.OrganizationPropertyRecordFactory;
import adtekfuji.admanagerapp.organizationeditplugin.common.ReasonCategoryRecordFactory;
import adtekfuji.admanagerapp.organizationeditplugin.common.RoleIdData;
import adtekfuji.admanagerapp.organizationeditplugin.common.RolePropertyRecordFactory;
import adtekfuji.admanagerapp.organizationeditplugin.common.SimpleRecordParam;
import adtekfuji.admanagerapp.organizationeditplugin.common.WorkCategoryRecordFactory;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ExcelFileUtils;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.*;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.PasswordEncoder;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.ThreadUtils;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
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
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.indirectwork.WorkCategoryInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.RoleAuthorityInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.LocaleFileInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.resource.ResourceInfoEntity;
import jp.adtekfuji.adFactory.entity.search.OrganizationSearchCondition;
import jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.enumerate.LocaleTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ResourceTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.DetailRecordFactory;
import jp.adtekfuji.javafxcommon.PropertyBindEntity;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.controls.ResettableTextField;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.AccessAuthSettingEntity;
import jp.adtekfuji.javafxcommon.treecell.OrganizationTreeCell;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author e-mori
 */
@FxComponent(id = "OrganizationEditCompo", fxmlPath = "/fxml/compo/organization_edit_compo.fxml")
public class OrganizationEditController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static OrganizationInfoFacade organizationFacade = new OrganizationInfoFacade();
    private final static CashManager cacheManager = CashManager.getInstance();

    private final static long MAX_ROLL_HIERARCHY_CNT = 30;
    private final static long ROOT_ID = 0;

    private final SceneContiner sc = SceneContiner.getInstance();
    private final Map<String, PropertyBindEntity> detailProperties = new LinkedHashMap<>();
    private final LinkedList<RoleIdData> roleProperties = new LinkedList<>();                       // 役割
    private final LinkedList<BreakTimeIdData> breaktimeProperties = new LinkedList<>();             // 休憩時間
    private final LinkedList<SimpleRecordParam> interruptCategoryProperties = new LinkedList<>();   // 中断理由区分
    private final LinkedList<SimpleRecordParam> delayCategoryProperties = new LinkedList<>();       // 遅延理由区分
    private final LinkedList<SimpleRecordParam> callCategoryProperties = new LinkedList<>();        // 呼出理由区分
    private final LinkedList<SimpleRecordParam> workCategoryProperties = new LinkedList<>();        // 作業区分
    private final LinkedList<OrganizationPropertyInfoEntity> customProperties = new LinkedList<>(); // 追加情報
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private OrganizationInfoEntity organization;
    private OrganizationInfoEntity oldOrganization;
    private TreeItem<OrganizationInfoEntity> rootItem;

    //以前選択していた項目　変更時前に選んでいたものの名前を変更するために使用
    private TreeItem<OrganizationInfoEntity> prevSelectedItem;

    private boolean isDisableEdit = false;  // 編集ボタン無効フラグ (編集権限がない場合 true)
    private boolean isCancelMove = false;   // 保存確認ダイアログで取消を選択した場合の、階層ツリー移動キャンセルフラグ

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
    private SplitPane organizationPane;
    @FXML
    private TreeView<OrganizationInfoEntity> hierarchyTree;
    @FXML
    private ToolBar hierarchyBtnArea;
    @FXML
    private Button delButton;
//    @FXML
//    private Button copyButton;
    @FXML
    private Button createButton;
    @FXML
    private Button moveButton;
    @FXML
    private Button authButton;
    @FXML
    private VBox detailPane;
    @FXML
    private VBox rolePane;
    @FXML
    private VBox breakTimePane;
    @FXML
    private VBox settingPane;
    @FXML
    private VBox localePane;
    @FXML
    private VBox propertyPane;
    @FXML
    private Button passwordInitializationButton;
    @FXML
    private Button registButton;
    @FXML
    private Pane Progress;
    @FXML
    private Button importButton;
    @FXML
    private ResettableTextField searchField;
    
    /**
     * 組織編集コンポーネントを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        SplitPaneUtils.loadDividerPosition(organizationPane, getClass().getSimpleName());

        // リソース編集権限がない場合、編集関連のボタンを無効化する。
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            isDisableEdit = true;
            delButton.setDisable(true);
//            copyButton.setDisable(true);
            createButton.setDisable(true);
            moveButton.setDisable(true);
            authButton.setDisable(true);
            importButton.setDisable(true);
            passwordInitializationButton.setDisable(true);
            registButton.setDisable(true);
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
        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<OrganizationInfoEntity>> observable, TreeItem<OrganizationInfoEntity> oldValue, TreeItem<OrganizationInfoEntity> newValue) -> {
            if (isCancelMove) {
                // 移動キャンセル中は何もしない。
                isCancelMove = false;
                return;
            }

            // 詳細情報を表示する。
            dispInfo(newValue);
        });

        hierarchyTree.setCellFactory((TreeView<OrganizationInfoEntity> o) -> new OrganizationTreeCell());

        // 検索リセット
        this.searchField.getReseyButton().addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
            this.searchField.getTextField().clear();
            if (Objects.equals(hierarchyTree.rootProperty().getValue(), rootItem)) {
                return;
            }

            //フィルターが適用されていた場合
            TreeItem<OrganizationInfoEntity> selectedItem = hierarchyTree.getSelectionModel().getSelectedItem();
            rootItem.getChildren().clear();
            Optional<TreeItem<OrganizationInfoEntity>> selectedTreeItemOptional = getSelectedTreeItemAndExpand(rootItem, getTopLevelParentId(selectedItem), true);
            TreeItem<OrganizationInfoEntity> selectedTreeItem = selectedTreeItemOptional.orElse(null);
            if (Objects.isNull(selectedTreeItem) || Objects.isNull(selectedTreeItem.getValue().getParentId())) {
                createRoot(null);
                this.searchField.getTextField().clear();
                return;
            }

            TreeItem<OrganizationInfoEntity> parentTreeItem = selectedTreeItem.getParent();
            hierarchyTree.rootProperty().setValue(rootItem);
            selectedTreeItem(parentTreeItem, selectedTreeItem.getValue().getOrganizationId());
       });
        
        // 検索
        this.searchField.getTextField().addEventFilter(KeyEvent.KEY_RELEASED, (event) -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                this.onSearch(null);
            }
        });

        blockUI(true);

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // キャッシュに情報を読み込む。
                    initializeThread();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                // ツリーのルートノードを生成する。
                createRoot(null);
            }
        };
        new Thread(task).start();
    }

    /**
     * 詳細情報を表示する。
     *
     * @param treeItem 選択ノード
     */
    private void dispInfo(TreeItem<OrganizationInfoEntity> treeItem) {
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
                if (!treeItem.getValue().getOrganizationId().equals(ROOT_ID)) {
                    organization = organizationFacade.find(treeItem.getValue().getOrganizationId());

                    oldOrganization = null;//素早くでたらめに選ぶと古い情報が残ってしまうためクリア。新規作成に影響を及ぼさないようにここで行う。
                    updateView(treeItem.getParent().getValue().getOrganizationName());
                    //最初に表示される情報のコピー
                    oldOrganization = organization.clone();
                } else {
                    organization = null;
                    clearDetailView();
                }

                prevSelectedItem = hierarchyTree.getSelectionModel().getSelectedItem();
            } else {
                organization = null;
                clearDetailView();
            }

            // ツリーの編集ボタン状態を戻す。
            delButton.setDisable(isDisableEdit);
            createButton.setDisable(isDisableEdit);
            moveButton.setDisable(isDisableEdit || !loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.RIGHT_ACCESS));
            authButton.setDisable(isDisableEdit);
            importButton.setDisable(isDisableEdit);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 
     * @param key 
     */
    @FXML
    private void onKeyPressed(KeyEvent key) {
        if (key.getCode().equals(KeyCode.F5)) {
            this.hierarchyTree.getRoot().setExpanded(false);
            createRoot(null);
        }
    }

    /**
     * 組織削除
     *
     * @param event 削除ボタン押下
     */
    @FXML
    private void onDelete(ActionEvent event) {
        //削除
        try {
            final OrganizationInfoEntity _organization = hierarchyTree.getSelectionModel().getSelectedItem().getValue();

            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                    || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId().equals(ROOT_ID)) {
                return;
            }

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), _organization.getOrganizationName());
            if (ret.equals(ButtonType.CANCEL)) {
                return;
            }

            //変更を破棄
            oldOrganization = null;

            ResponseEntity res = organizationFacade.delete(hierarchyTree.getSelectionModel().getSelectedItem().getValue());
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                // 組織のキャッシュを削除する。
                CacheUtils.removeCacheData(OrganizationInfoEntity.class);

                //ツリー更新
                this.updateTreeView(hierarchyTree.getSelectionModel().getSelectedItem().getParent(), _organization.getOrganizationId());
            } else {
                //TODO:エラー時の処理
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

//    /**
//     * 組織コピー
//     *
//     * @param event コピーボタン押下
//     */
//    @FXML
//    private void onCopy(ActionEvent event) {
//        try {
//            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
//                    || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId().equals(ROOT_ID)) {
//                return;
//            }
//
//            boolean isCompleted = true;
//
//            //変更を調べる　変更が存在したとき保存
//            if (this.isChanged()) {
//                isCompleted = saveChanges(hierarchyTree.getSelectionModel().getSelectedItem());
//                cloneInitialOrganizationInfo = null;
//            }
//
//            if (isCompleted) {
//                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Copy"), LocaleUtils.getString("key.CopyMessage"));
//                if (ret.equals(ButtonType.CANCEL)) {
//                    return;
//                }
//
//                ResponseEntity res = organizationInfoFacade.copy(hierarchyTree.getSelectionModel().getSelectedItem().getValue());
//                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
//                    //ツリー更新
//                    this.updateTreeItem(hierarchyTree.getSelectionModel().getSelectedItem().getParent(), getUriToOrganizationId(res.getUri()));
//                } else {
//                    //TODO:エラー時の処理
//                }
//            }
//        } catch (Exception ex) {
//            logger.fatal(ex, ex);
//        }
//    }
    /**
     * 組織新規作成
     *
     * @param event 新規作成ボタン押下
     */
    @FXML
    private void onCreate(ActionEvent event) {
        if (Objects.nonNull(hierarchyTree.getSelectionModel().getSelectedItem())) {
            boolean isCompleted = true;//保存に問題がなかった、あるい保存自体なかったときtrue　保存キャンセル時新規作成を行いたくないため

            //変更を調べる　変更が存在したとき保存
            if (this.isChanged()) {
                isCompleted = saveChanges(hierarchyTree.getSelectionModel().getSelectedItem());
            }

            if (isCompleted) {
                organization = new OrganizationInfoEntity();
                //親から休憩情報を引き継ぐ.
                OrganizationInfoEntity parent = organizationFacade.find(hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId());
                organization.setBreakTimeInfoCollection(new ArrayList<>(parent.getBreakTimeInfoCollection()));
                //表示へ.
                updateView(parent.getOrganizationName());
                oldOrganization = new OrganizationInfoEntity();

                // 新規作成時はツリーの編集ボタンを無効にする。
                delButton.setDisable(true);
//                copyButton.setDisable(true);
                createButton.setDisable(true);
                moveButton.setDisable(true);
                authButton.setDisable(true);
                importButton.setDisable(true);

                // ボタン無効でフォーカスが移動するがその移動先がTextFieldだとIMEの座標が狂う。TextField以外に合わせる。
                detailPane.requestFocus();
            }
        }
    }

    /**
     * 指定されたツリー項目を基に最上位の親組織IDのリストを取得します。
     *
     * @param selectedItem ツリー構造内の選択された組織情報エンティティ項目
     * @return 最上位の親組織IDを含むリスト、選択項目がnullの場合はnullを返します
     */
    private LinkedList<Long> getTopLevelParentId(TreeItem<OrganizationInfoEntity> selectedItem) {
        if (Objects.isNull(selectedItem)) {
            return new LinkedList<>();
        }

        OrganizationInfoEntity currentItem = selectedItem.getValue();
        if (Objects.isNull(currentItem) || Objects.equals(ROOT_ID, currentItem.getOrganizationId())) {
            return new LinkedList<>();
        }

        LinkedList<Long> parentIds = new LinkedList<>();
        parentIds.add(currentItem.getOrganizationId());

        while (currentItem.getParentId() != 0) {
            OrganizationInfoEntity entity = organizationFacade.get(currentItem.getParentId());
            parentIds.add(entity.getOrganizationId());
            currentItem = entity;
        }
        return parentIds;
    }

    /**
     * 指定されたツリーアイテムの展開状態を監視するリスナーを設定します。
     *
     * @param target 展開状態のリスナーを設定する対象のツリーアイテム
     */
    private void setExpandedListener(TreeItem<OrganizationInfoEntity> target) {
        if (!target.getChildren().isEmpty()) {
            // 既に子要素が設定されている場合はリスナーは不要
            return;
        }

        OrganizationInfoEntity entity = target.getValue();
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
     * @param isNeedChildren true: 子ノード強制再読み込み（キャッシュ破棄）/ false: 既存データを優先
     * @return 展開後に選択されたツリー項目が存在する場合はオプショナルで返されます。存在しない場合は空のオプショナルを返します。
     */
    Optional<TreeItem<OrganizationInfoEntity>> getSelectedTreeItemAndExpand(TreeItem<OrganizationInfoEntity> parent, LinkedList<Long> parentIds, boolean isNeedChildren) {
        if (parentIds.isEmpty()) {
            if (isNeedChildren) {
                setExpandedListener(parent);
            }
            return Optional.of(parent);
        }
        parent.setExpanded(true);

        long my = parentIds.removeLast();
        if (parent.getChildren().isEmpty() || Objects.isNull(parent.getChildren().get(0).getValue())) {
            long parentId = parent.getValue().getOrganizationId();
            long count = organizationFacade.getAffilationHierarchyCount(parentId);
            List<OrganizationInfoEntity> organizations = new ArrayList<>();
            for (long from = 0; from <= count; from += MAX_ROLL_HIERARCHY_CNT) {
                List<OrganizationInfoEntity> entities = organizationFacade.getAffilationHierarchyRange(parentId, from, from + MAX_ROLL_HIERARCHY_CNT - 1);
                organizations.addAll(entities);
            }

            Optional<TreeItem<OrganizationInfoEntity>> myItem = Optional.empty();
            for (OrganizationInfoEntity entity : organizations) {
                TreeItem<OrganizationInfoEntity> item = new TreeItem<>(entity);
                if (entity.getOrganizationId().equals(my)) {
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
          .filter(item -> Objects.equals(item.getValue().getOrganizationId(), my))
          .findFirst()
          .flatMap(item -> getSelectedTreeItemAndExpand(item, parentIds, isNeedChildren));

    }
    
    /**
     * 組織移動
     *
     * @param event 移動ボタン押下
     */
    @FXML
    private void onMove(ActionEvent event) {
        try {
            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                    || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId().equals(ROOT_ID)
                    || Objects.isNull(organization)) {
                return;
            }

            boolean isCompleted = true;

            if (this.isChanged()) {
                isCompleted = saveChanges(hierarchyTree.getSelectionModel().getSelectedItem());
            }

            if (isCompleted) {
                hierarchyTree.setVisible(false);
                detailPane.setVisible(false);
                localePane.setVisible(false);
                propertyPane.setVisible(false);
                rolePane.setVisible(false);
                breakTimePane.setVisible(false);
                settingPane.setVisible(false);
                                              
                TreeItem<OrganizationInfoEntity> selectedItem = hierarchyTree.getSelectionModel().getSelectedItem();                
                Optional<TreeItem<OrganizationInfoEntity>> selectedTreeItemOptional = getSelectedTreeItemAndExpand(rootItem, getTopLevelParentId(selectedItem), false);
                TreeItem<OrganizationInfoEntity> selectedTreeItem = selectedTreeItemOptional.orElse(null);
                if (Objects.isNull(selectedTreeItem)) {
                    return;
                }

                TreeItem<OrganizationInfoEntity>  parentTreeItem = selectedTreeItem.getParent();
                OrganizationInfoEntity item = organization;

                //下のremoveで追加情報が消えるので退避
                List<OrganizationPropertyInfoEntity> customs = new LinkedList<>(customProperties);

                //移動先として自分を表示させないように一時削除
                int idx = parentTreeItem.getChildren().indexOf(selectedTreeItem);
                parentTreeItem.getChildren().remove(selectedTreeItem);

                //ダイアログに表示させるデータを設定
                TreeDialogEntity treeDialogEntity = new TreeDialogEntity(this.rootItem, LocaleUtils.getString("key.HierarchyName"));
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "OrganizationHierarchyTreeCompo", treeDialogEntity);
                if (ret.equals(ButtonType.OK) && treeDialogEntity.getTreeSelectedItem() != null) {
                    logger.debug(treeDialogEntity.getTreeSelectedItem());
                    TreeItem<OrganizationInfoEntity> hierarchy = (TreeItem<OrganizationInfoEntity>) treeDialogEntity.getTreeSelectedItem();
                    item.setParentId(hierarchy.getValue().getOrganizationId());
                    item.setUpdatePersonId(loginUserInfoEntity.getId());
                    item.setUpdateDateTime(new Date());
                    item.setPropertyInfoCollection(customs);//追加情報を復帰

                    ResponseEntity res = organizationFacade.update(item);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // 組織のキャッシュを削除する。
                        CacheUtils.removeCacheData(OrganizationInfoEntity.class);
                        //ツリー更新
                        this.updateTreeView(hierarchy, item.getOrganizationId());
                        this.hierarchyTree.setRoot(this.rootItem);
                    } else if (ServerErrorTypeEnum.UNMOVABLE_HIERARCHY.equals(res.getErrorType())) {
                        // 移動不可能な階層だった場合、警告表示して階層ツリーを再取得して表示しなおす。
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.unmovableHierarchy"));
                        this.hierarchyTree.getRoot().setExpanded(false);
                        this.createRoot(null);
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
            localePane.setVisible(true);
            propertyPane.setVisible(true);
            rolePane.setVisible(true);
            breakTimePane.setVisible(true);
            settingPane.setVisible(true);
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
                    || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId().equals(ROOT_ID)
                    || Objects.isNull(organization)) {
                return;
            }
            
            boolean isCompleted = true;

            //変更を確認し保存
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    isCompleted = regist(prevSelectedItem, true);
                } else if (ButtonType.NO == buttonType) {
                    //追加情報は画面に表示されてるものと結びついてるため元に戻す
                    customProperties.clear();
                    customProperties.addAll(oldOrganization.getPropertyInfoCollection());
                    oldOrganization = null;//下のremoveでChangeListenerが呼ばれる対策　変更を検出させない
                } else {
                    isCompleted = false;
                }
            }

            if (isCompleted) {
                TreeItem<OrganizationInfoEntity> selectedTreeItem = hierarchyTree.getSelectionModel().getSelectedItem();
                //ダイアログに表示させるデータを設定
                AccessHierarchyTypeEnum type = AccessHierarchyTypeEnum.OrganizationHierarchy;
                long id = selectedTreeItem.getValue().getOrganizationId();
                AccessHierarchyInfoFacade accessHierarchyInfoFacade = new AccessHierarchyInfoFacade();
                long count = accessHierarchyInfoFacade.getCount(type, id);
                long range = 100;
                List<OrganizationInfoEntity> deleteList = new ArrayList();
                for (long from = 0; from <= count; from += range) {
                    List<OrganizationInfoEntity> entities = accessHierarchyInfoFacade.getRange(type, id, from, from + range - 1);
                    deleteList.addAll(entities);
                }
                AccessAuthSettingEntity accessAuthSettingEntity 
                        = new AccessAuthSettingEntity(selectedTreeItem.getValue().getOrganizationName(), deleteList);
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.EditedAuth"), "AccessAuthSettingCompo", accessAuthSettingEntity);
                if (ret.equals(ButtonType.OK)) {
                    List<OrganizationInfoEntity> registList = accessAuthSettingEntity.getAuthOrganizations();
                    for(int i=0; i<registList.size(); i++) {
                        OrganizationInfoEntity o = registList.get(i);
                        if(deleteList.contains(o)) {
                            deleteList.remove(o);
                            registList.remove(o);
                            i--;
                        }
                    }
                    if(!deleteList.isEmpty()) {
                        accessHierarchyInfoFacade.delete(type, id, deleteList);
                    }
                    if(!registList.isEmpty()) {
                        accessHierarchyInfoFacade.regist(type, id, registList);
                    }

                    // 組織のキャッシュを削除する。
                    CacheUtils.removeCacheData(OrganizationInfoEntity.class);

                    //ツリー更新
                    this.updateTreeView(hierarchyTree.getSelectionModel().getSelectedItem().getParent(), selectedTreeItem.getValue().getOrganizationId());
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * インポート
     *
     * @param event 
     */
    @FXML
    private void onImport(ActionEvent event) {
        this.importOrganization();
    }

    /**
     * 組織登録
     *
     * @param event 登録ボタン押下
     */
    @FXML
    private void onRegist(ActionEvent event) {
        regist(hierarchyTree.getSelectionModel().getSelectedItem(), true);
    }

    /**
     * 組織情報を保存する。
     *
     * @param target 保存対象
     * @param isSelect
     * @return
     */
    private boolean regist(TreeItem<OrganizationInfoEntity> target, boolean isSelect) {
        try {
            if (Objects.isNull(target)
                    || Objects.isNull(organization)) {
                return false;
            }

            //保存するデータの作成
            if (!createRegistData()) {
                return false;
            }

            //登録データを初期値にコピー
            oldOrganization = organization.clone();

            if (Objects.nonNull(organization.getOrganizationId()) && organization.getOrganizationId() != ROOT_ID) {
                // 組織情報を更新する。
                ResponseEntity res = organizationFacade.update(organization);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    // 組織のキャッシュを削除する。
                    CacheUtils.removeCacheData(OrganizationInfoEntity.class);
                    // 入力されたパスワードをクリア
                    ((StringProperty) detailProperties.get("password").getProperty()).set("");

                    //ツリー更新
                    if (isSelect) {
                        this.updateTreeView(target.getParent(), organization.getOrganizationId());
                    } else {
                        prevSelectedItem.getValue().setOrganizationName(organization.getOrganizationName());
                        hierarchyTree.refresh();
                    }
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                    // 排他バージョンが異なる。

                    // 組織のキャッシュを削除する。
                    CacheUtils.removeCacheData(OrganizationInfoEntity.class);

                    //ツリー更新
                    if (isSelect) {
                        this.updateTreeView(target.getParent(), organization.getOrganizationId());
                    } else {
                        prevSelectedItem.getValue().setOrganizationName(organization.getOrganizationName());
                        hierarchyTree.refresh();
                    }

                    return false;
                } else {
                    //TODO:エラー時の処理
                    return false;
                }
            } else {
                //新規作成処理
                organization.setParentId(target.getValue().getOrganizationId());
                ResponseEntity res = organizationFacade.regist(organization);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    // 組織のキャッシュを削除する。
                    CacheUtils.removeCacheData(OrganizationInfoEntity.class);
                    // 入力されたパスワードをクリア
                    ((StringProperty) detailProperties.get("password").getProperty()).set("");

                    //ツリー更新
                    this.updateTreeView(target, getUriToOrganizationId(res.getUri()));
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
     * パスワード初期化処理
     *
     * @param event
     */
    @FXML
    private void onPasswordInitialization(ActionEvent event) {
        try {
            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                    || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getOrganizationId().equals(ROOT_ID)
                    || Objects.isNull(organization.getOrganizationId())) {
                return;
            }

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.PasswordInitialization"), LocaleUtils.getString("key.PasswordInitializationMessage"));
            if (ret.equals(ButtonType.CANCEL)) {
                return;
            }

            if (!createRegistData()) {
                return;
            }

            organization.setPassword("");

            //更新処理
            ResponseEntity res = organizationFacade.update(organization);
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                //ツリー更新
                this.updateTreeView(hierarchyTree.getSelectionModel().getSelectedItem().getParent(), organization.getOrganizationId());
            } else {
                //TODO:エラー時の処理
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ツリーの表示を更新する。
     *
     * @param parentItem 表示更新するノード
     * @param selectedId 更新後に選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void updateTreeView(TreeItem<OrganizationInfoEntity> parentItem, Long selectedId) {
        if (Objects.isNull(parentItem.getParent())) {
            //ROOT
            createRoot(selectedId);
        } else {
            //子階層
            expand(parentItem, selectedId);
        }
    }

    /**
     * 組織IDが一致するTreeItemを選択する (存在しない場合は親を選択する(削除後の選択用))
     *
     * @param parentItem 選択状態にするノーノの親ノード
     * @param selectedId 選択状態にするノードの設備ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void selectedTreeItem(TreeItem<OrganizationInfoEntity> parentItem, Long selectedId) {
        if (Objects.equals(ROOT_ID, selectedId) || Objects.isNull(parentItem)) {
            this.hierarchyTree.getSelectionModel().select(this.rootItem);
        } else {
            Optional<TreeItem<OrganizationInfoEntity>> find
                    = parentItem
                    .getChildren()
                    .stream()
                    .filter(p -> p.getValue().getOrganizationId().equals(selectedId)).findFirst();

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
     */
    private void createRoot(Long selectedId) {
        logger.debug("createRoot start.");
        try {
            blockUI(true);

            // ルートが存在しない場合は新規作成する。
            if (Objects.isNull(this.rootItem)) {
                this.rootItem = new TreeItem<>(new OrganizationInfoEntity(ROOT_ID, null, LocaleUtils.getString("key.Organization"), null));
            }

            this.rootItem.getChildren().clear();

            Task task = new Task<List<OrganizationInfoEntity>>() {
                @Override
                protected List<OrganizationInfoEntity> call() throws Exception {
                    // 子組織の件数を取得する。
                    long count = organizationFacade.getTopHierarchyCount();

                    List<OrganizationInfoEntity> organizations = new ArrayList();
                    for (long from = 0; from <= count; from += MAX_ROLL_HIERARCHY_CNT) {
                        List<OrganizationInfoEntity> entities = organizationFacade.getTopHierarchyRange(from, from + MAX_ROLL_HIERARCHY_CNT - 1);
                        organizations.addAll(entities);
                    }
                    return organizations;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        long count = this.getValue().size();
                        rootItem.getValue().setChildCount(count);

                        this.getValue().stream().forEach((entity) -> {
                            TreeItem<OrganizationInfoEntity> item = new TreeItem<>(entity);
                            if (entity.getChildCount() > 0) {
                                item.getChildren().add(new TreeItem());
                            }
                            item.expandedProperty().addListener(expandedListener);
                            rootItem.getChildren().add(item);
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
    private void expand(TreeItem<OrganizationInfoEntity> parentItem, Long selectedId) {
        logger.debug("expand: parentItem={}", parentItem.getValue());
        try {
            blockUI(true);

            parentItem.getChildren().clear();

            final long parentId = parentItem.getValue().getOrganizationId();

            Task task = new Task<List<OrganizationInfoEntity>>() {
                @Override
                protected List<OrganizationInfoEntity> call() throws Exception {
                    // 子組織の件数を取得する。
                    long count = organizationFacade.getAffilationHierarchyCount(parentId);

                    List<OrganizationInfoEntity> organizations = new ArrayList();
                    for (long from = 0; from <= count; from += MAX_ROLL_HIERARCHY_CNT) {
                        List<OrganizationInfoEntity> entities = organizationFacade.getAffilationHierarchyRange(parentId, from, from + MAX_ROLL_HIERARCHY_CNT - 1);
                        organizations.addAll(entities);
                    }
                    return organizations;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        this.getValue().stream().forEach((entity) -> {
                            TreeItem<OrganizationInfoEntity> item = new TreeItem<>(entity);
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
     * 詳細画面起動時の役割・休憩処理読み出し
     *
     */
    private void initializeThread() {
        CacheUtils.createCacheBreakTime(true);
        CacheUtils.createReasonCetegory(true);
    
        RoleInfoFacade roleInfoFacade = new RoleInfoFacade();
        cacheManager.setNewCashList(RoleAuthorityInfoEntity.class);
        cacheManager.clearList(RoleAuthorityInfoEntity.class);
        roleInfoFacade.findAll().stream().forEach(o -> cacheManager.setItem(RoleAuthorityInfoEntity.class, o.getRoleId(), o));
        
        WorkCategoryInfoFacade workCategoryInfoFacade = new WorkCategoryInfoFacade();
        cacheManager.setNewCashList(WorkCategoryInfoEntity.class);
        cacheManager.clearList(WorkCategoryInfoEntity.class);
        workCategoryInfoFacade.findAll().stream().forEach(o -> cacheManager.setItem(WorkCategoryInfoEntity.class, o.getWorkCategoryId(), o));
    }

    /**
     * プロパティ情報型表示用セルクラス
     *
     */
    class AuthorityEnumComboBoxCellFactory extends ListCell<AuthorityEnum> {

        @Override
        protected void updateItem(AuthorityEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
            }
        }
    }

    /**
     * 詳細画面更新処理
     *
     * @param entity ツリーで選択された情報
     */
    private void updateView(String parentName) {
        if (Objects.isNull(organization)) {
            return;
        }

        clearDetailView();

        //選択されているIDの権限ID
        AuthorityEnum auth = organization.authorityTypeProperty().get();

        //組織名
        detailProperties.put("organizationName", PropertyBindEntity.createRegerxString(LocaleUtils.getString("key.OrganizationName") + LocaleUtils.getString("key.RequiredMark"), organization.getOrganizationName(), "^.{0,255}$"));

        //組織識別名
        PropertyBindEntity orgIdentProp = PropertyBindEntity.createRegerxString(LocaleUtils.getString("key.OrganizationsManagementName") + LocaleUtils.getString("key.RequiredMark"), organization.getOrganizationIdentify(), "^.{0,255}$");
        if( Objects.nonNull(auth) && auth.equals(AuthorityEnum.SYSTEM_ADMIN) ){
            //SYSTEM_ADMIN の場合は変更不可とする
            orgIdentProp.isDisable(Boolean.TRUE);
        }
        detailProperties.put("organizationIdentify", orgIdentProp);

	//パスワード
        if (loginUserInfoEntity.getAuthorityType() == AuthorityEnum.SYSTEM_ADMIN
                || Objects.equals(loginUserInfoEntity.getId(), organization.getOrganizationId())
                || Objects.isNull(organization.getOrganizationId())) {
            detailProperties.put("password", (PropertyBindEntity.createPassword(LocaleUtils.getString("key.Password"), organization.getPassword())).isDisable(Boolean.FALSE));
        } else {
            detailProperties.put("password", (PropertyBindEntity.createPassword(LocaleUtils.getString("key.Password"), organization.getPassword())).isDisable(Boolean.TRUE));
        }

        // メールアドレス
        PropertyBindEntity mailAddressProp = PropertyBindEntity.createRegerxString(LocaleUtils.getString("key.MailAdress"), organization.getMailAddress(), "[\\.\\-_@A-Za-z0-9]*");
        mailAddressProp.setPrefWidth(360.0);
        detailProperties.put("mailAddress", mailAddressProp);
        
        // 組織詳細情報
        Table organizationDetailTable = new Table(detailPane.getChildren()).isAddRecord(false).title(parentName).styleClass("ContentTitleLabel");
        organizationDetailTable.setAbstractRecordFactory(new DetailRecordFactory(organizationDetailTable, new LinkedList(detailProperties.values())));

        // 役割一覧
        this.roleProperties.clear();
        this.organization.getRoleCollection().stream().forEach(id -> this.roleProperties.add(new RoleIdData(id)));

        if( Objects.nonNull(auth) && auth.equals(AuthorityEnum.SYSTEM_ADMIN) ){
            //admin の場合は権限設定できない
            //変更があるかどうかをチェックするため、 roleProperties へは admin でもデータはセットしておく必要がある
            //  --> admin の場合は設定用の表示のみ行わないようにしている
        } else {
            // 役割設定
            Table roleTable = new Table(rolePane.getChildren()).isAddRecord(Boolean.TRUE).title(LocaleUtils.getString("key.EditRoleTitle")).styleClass("ContentTitleLabel");
            roleTable.setAbstractRecordFactory(new RolePropertyRecordFactory(roleTable, roleProperties));
        }

        // 休憩
        this.breaktimeProperties.clear();
        this.organization.getBreakTimeInfoCollection().stream().forEach(id -> {
            BreakTimeInfoEntity entity = CacheUtils.getCacheBreakTime(id);
            if (Objects.nonNull(entity)) {
                breaktimeProperties.add(new BreakTimeIdData(id, entity.getStarttime()));
            }
        });
        this.breaktimeProperties.sort(Comparator.comparing(item -> item.getStarttime()));// 開始時間順にソート

        Table breaktimePropertyTable = new Table(breakTimePane.getChildren()).isAddRecord(Boolean.TRUE).title(LocaleUtils.getString("key.BreakTimeInfomation")).styleClass("ContentTitleLabel");
        breaktimePropertyTable.setAbstractRecordFactory(new BreakTimePropertyRecordFactory(breaktimePropertyTable, breaktimeProperties));

        // 中断理由
        this.interruptCategoryProperties.clear();
        this.organization.getInterruptCategoryCollection().stream().forEach(id -> this.interruptCategoryProperties.add(new SimpleRecordParam(id)));
        this.interruptCategoryProperties.sort(Comparator.comparing(item -> item.getKey()));
        Table interruptTable = new Table(settingPane.getChildren())
                .isAddRecord(Boolean.TRUE)
                .title(LocaleUtils.getString("key.suspendedReasons"))
                .styleClass("ContentTitleLabel");
        interruptTable.setAbstractRecordFactory(new ReasonCategoryRecordFactory(interruptTable, this.interruptCategoryProperties, ReasonTypeEnum.TYPE_INTERRUPT));
        
        // 遅延理由
        this.delayCategoryProperties.clear();
        this.organization.getDelayCategoryCollection().stream().forEach(id -> this.delayCategoryProperties.add(new SimpleRecordParam(id)));
        this.delayCategoryProperties.sort(Comparator.comparing(item -> item.getKey()));
        Table delayTable = new Table(settingPane.getChildren())
                .isAddRecord(Boolean.TRUE)
                .title(LocaleUtils.getString("key.delayReasons"))
                .styleClass("ContentTitleLabel");
        delayTable.setAbstractRecordFactory(new ReasonCategoryRecordFactory(delayTable, this.delayCategoryProperties, ReasonTypeEnum.TYPE_DELAY));
        
        // 呼出理由
        this.callCategoryProperties.clear();
        this.organization.getCallCategoryCollection().stream().forEach(id -> this.callCategoryProperties.add(new SimpleRecordParam(id)));
        this.callCategoryProperties.sort(Comparator.comparing(item -> item.getKey()));
        Table callTable = new Table(settingPane.getChildren())
                .isAddRecord(Boolean.TRUE)
                .title(LocaleUtils.getString("key.callReasons"))
                .styleClass("ContentTitleLabel");
        callTable.setAbstractRecordFactory(new ReasonCategoryRecordFactory(callTable, this.callCategoryProperties, ReasonTypeEnum.TYPE_CALL));
        
        // 作業区分
        this.workCategoryProperties.clear();
        this.organization.getWorkCategoryCollection().stream().forEach(id -> {
            WorkCategoryInfoEntity entity = (WorkCategoryInfoEntity) cacheManager.getItem(WorkCategoryInfoEntity.class, id);
            if (Objects.nonNull(entity)) {
                workCategoryProperties.add(new SimpleRecordParam(id));
            }
        });
        this.workCategoryProperties.sort(Comparator.comparing(item -> item.getKey()));

        Table workCategoryTable = new Table(settingPane.getChildren())
                .isAddRecord(Boolean.TRUE)
                .title(LocaleUtils.getString("key.WorkClassification"))
                .styleClass("ContentTitleLabel");
        workCategoryTable.setAbstractRecordFactory(new WorkCategoryRecordFactory(workCategoryTable, workCategoryProperties));

        if (ClientServiceProperty.isLicensed("@LanguageOption")) {
            localePane.setManaged(true);

            // 言語
            LinkedList<LocaleFileInfoEntity> localeList = new LinkedList();
            if (Objects.nonNull(organization.getLangIds())) {
                try {
                    localeList = (LinkedList) JsonUtils.jsonToObjects(organization.getLangIds(), LocaleFileInfoEntity[].class);
                } catch (Exception ex) {
                    logger.error(ex, ex);
                }
            }

            LocaleFileInfoEntity admanagerInfo = new LocaleFileInfoEntity(LocaleTypeEnum.ADMANAGER, new ResourceInfoEntity(ResourceTypeEnum.LOCALE));
            LocaleFileInfoEntity adproductInfo = new LocaleFileInfoEntity(LocaleTypeEnum.ADPRODUCT, new ResourceInfoEntity(ResourceTypeEnum.LOCALE));
            // カスタム
            // LocaleFileInfoEntity custumInfo = new LocaleFileInfoEntity(LocaleTypeEnum.CUSTUM, new ResourceInfoEntity(ResourceTypeEnum.LOCALE));
            for (LocaleFileInfoEntity localeInfo : localeList) {
                if (Objects.equals(LocaleTypeEnum.ADMANAGER, localeInfo.getLocaleType())) {
                    admanagerInfo.setLocaleType(localeInfo.getLocaleType());
                    admanagerInfo.resource().setResourceId(localeInfo.resource().getResourceId());
                    admanagerInfo.resource().setResourceKey(localeInfo.resource().getResourceKey());
                } else if (Objects.equals(LocaleTypeEnum.ADPRODUCT, localeInfo.getLocaleType())) {
                    adproductInfo.setLocaleType(localeInfo.getLocaleType());
                    adproductInfo.resource().setResourceId(localeInfo.resource().getResourceId());
                    adproductInfo.resource().setResourceKey(localeInfo.resource().getResourceKey());
                // カスタム    
                //} else if (Objects.equals(LocaleTypeEnum.CUSTUM, localeInfo.getLocaleType())) {
                //    custumInfo.setLocaleType(localeInfo.getLocaleType());
                //    custumInfo.resource().setResourceId(localeInfo.resource().getResourceId());
                //    custumInfo.resource().setResourceKey(localeInfo.resource().getResourceKey());
                }
            }

            localeList.clear();
            Collections.addAll(localeList, admanagerInfo, adproductInfo);//, custumInfo);
            organization.setLocaleFileInfoCollection(localeList);


            for (LocaleFileInfoEntity info : localeList) {
                if (Objects.isNull(info)) {
                    continue;
                }
                // 言語ファイル選択時の処理
                info.resource().resourceKeyProperty().setValueListener((String oldValue, String newValue) -> {
                    // ×ボタン押下時
                    if (StringUtils.isEmpty(newValue)) {
                        info.resource().setResourceKey(null);
                        info.resource().setResourceString(null);
                        return false;
                    }

                    try {
                        blockUI(true);

                        Properties prop = new Properties();
                        try {
                            // ロード
                            prop.load(new FileInputStream(newValue));
                        } catch (Exception e1) {
                            // ファイルが読み込めませんでした
                            info.resource().setResourceKey(null);
                            info.resource().resourceKeyProperty().setValue(oldValue);

                            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.LoadPropertyExeption"));
                            return false;
                        }

                        if (checkLocaleFile(info.getLocaleType(), prop)) return false;

                        try (StringWriter writer = new StringWriter()) {
                            // properties情報をString形式に変換
                            prop.store(new PrintWriter(writer), null);
                            String str = writer.getBuffer().toString();
                            info.resource().setResourceString(str);
                        } catch (Exception e) {
                            // ファイルが読み込めませんでした
                            info.resource().setResourceKey(null);
                            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.LoadPropertyExeption"));
                            return false;
                        }
                        info.resource().setResourceKey(newValue);
                        return true;
                    } finally {
                        blockUI(false);
                    }
                });
            }

            Table localeTable = new Table(localePane.getChildren()).isAddRecord(false)
                    .isColumnTitleRecord(true).title(LocaleUtils.getString("key.Language")).styleClass("ContentTitleLabel").bodyGap(null, 20.0);
            localeTable.setAbstractRecordFactory(new LocaleFileRecordFactory(localeTable, localeList));
        } else {
            localePane.setManaged(false);
        } // if (ClientServiceProperty.isLicensed("@LanguageOption")) {

        // 組織プロパティ
        if (!Objects.nonNull(organization.getPropertyInfoCollection())) {
            organization.setPropertyInfoCollection(new ArrayList<>());
        }

        customProperties.clear();
        customProperties.addAll(organization.getPropertyInfoCollection());
        customProperties.sort(Comparator.comparing(property -> property.getOrganizationPropOrder()));

        Table customPropertyTable = new Table(propertyPane.getChildren()).isAddRecord(Boolean.TRUE)
                .isColumnTitleRecord(true).title(LocaleUtils.getString("key.CustomField")).styleClass("ContentTitleLabel");
        customPropertyTable.setAbstractRecordFactory(new OrganizationPropertyRecordFactory(customPropertyTable, customProperties));
    }

    /**
     * ロケールファイルをチェックする
     * @param info
     * @param prop
     * @return
     */
    private boolean checkLocaleFile(LocaleTypeEnum localeType, Properties prop) {
        final String value = prop.getProperty("key.LocaleFileTypeInfo");
        if (Objects.isNull(value)) {
            logger.fatal("not found key.LocaleFileTypeInfo");
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.IncorrectFileFromDedicatedTool"));
            return true;
        }

        final List<String> localFileInfos = Arrays.asList(value.split(","));
        if (localFileInfos.size() < 2) {
            logger.fatal("key.LocaleFileTypeInfo size Error");
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.IncorrectFileFromDedicatedTool"));
            return true;
        }

        if (!StringUtils.equals(localeType.getLocaleFileType(), localFileInfos.get(0))) {
            logger.fatal("key.LocaleFileTypeInfo size Error");
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.IncorrectLocaleFileType"));
            return true;
        }
        return false;
    }

    /**
     * 詳細画面初期化
     */
    private void clearDetailView() {
        detailPane.getChildren().clear();
        detailProperties.clear();
        localePane.getChildren().clear();
        propertyPane.getChildren().clear();
        customProperties.clear();
        rolePane.getChildren().clear();
        roleProperties.clear();
        breakTimePane.getChildren().clear();
        breaktimeProperties.clear();
        
        interruptCategoryProperties.clear();
        
        settingPane.getChildren().clear();
        workCategoryProperties.clear();
    }

    /**
     * フィールドから情報を取得しエンティティに構築する。
     *
     * @return
     */
    private boolean createRegistData() {
        // 未入力チェック
        if (this.checkEmpty()) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
            return false;
        }

        // 役割重複チェック
        List<Long> roleIds = new ArrayList<>();
        for (RoleIdData entity : roleProperties) {
            if (roleIds.stream().anyMatch(id -> Objects.equals(id, entity.getId()))) {
                StringBuilder sb = new StringBuilder();
                sb.append(LocaleUtils.getString("key.EditRoleTitle"));
                sb.append("(");
                sb.append(((RoleAuthorityInfoEntity) cacheManager.getItem(RoleAuthorityInfoEntity.class, entity.getId())).getRoleName());
                sb.append(")");
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), sb.toString()));
                return false;
            } else {
                roleIds.add(entity.getId());
            }
        }

        // 休憩時間重複チェック
        List<Long> breaktimeIds = new ArrayList<>();
        for (BreakTimeIdData entity : breaktimeProperties) {
            if (breaktimeIds.stream().anyMatch(id -> Objects.equals(id, entity.getId()))) {
                BreakTimeInfoEntity breakTime = CacheUtils.getCacheBreakTime(entity.getId());

                StringBuilder sb = new StringBuilder();
                sb.append(LocaleUtils.getString("key.BreakTimeInfomation"));
                sb.append("(");
                sb.append(breakTime.getBreaktimeName());
                sb.append(")");
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), sb.toString()));
                return false;
            } else {
                breaktimeIds.add(entity.getId());
            }
        }

        // 作業区分重複チェック
        List<Long> workCategoryIds = new ArrayList<>();
        for (SimpleRecordParam entity : workCategoryProperties) {
            if (workCategoryIds.stream().anyMatch(id -> Objects.equals(id, entity.getKey()))) {
                StringBuilder sb = new StringBuilder();
                sb.append(LocaleUtils.getString("key.WorkClassification"));
                sb.append("(");
                sb.append(((WorkCategoryInfoEntity) cacheManager.getItem(
                        WorkCategoryInfoEntity.class, entity.getKey())).getWorkCategoryName());
                sb.append(")");
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"),
                        String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), sb.toString()));
                return false;
            } else {
                workCategoryIds.add(entity.getKey());
            }
        }

        customProperties.stream().forEach((entity) -> {
            entity.updateMember();
        });

        // 表示順を設定
        int order = 0;
        for (OrganizationPropertyInfoEntity entity : customProperties) {
            entity.setOrganizationPropOrder(order++);
        }

        PasswordEncoder encoder = new PasswordEncoder();
        organization.setOrganizationIdentify(((StringProperty) detailProperties.get("organizationIdentify").getProperty()).get());
        organization.setOrganizationName(((StringProperty) detailProperties.get("organizationName").getProperty()).get());
        organization.setPassword(encoder.encode(((StringProperty) detailProperties.get("password").getProperty()).get()));

        //役割を設定.
        organization.getRoleCollection().clear();
        organization.setRoleCollection(roleIds);

        organization.setMailAddress(((StringProperty) detailProperties.get("mailAddress").getProperty()).get());

        //休憩割り当て
        organization.getBreakTimeInfoCollection().clear();
        organization.setBreakTimeInfoCollection(breaktimeIds);

        // 中断理由
        organization.setInterruptCategoryCollection(new ArrayList<>(this.interruptCategoryProperties.stream().map(o -> o.getKey()).collect(Collectors.toSet())));
        
        // 遅延理由
        organization.setDelayCategoryCollection(new ArrayList<>(this.delayCategoryProperties.stream().map(o -> o.getKey()).collect(Collectors.toSet())));
        
        // 呼出理由
        organization.setCallCategoryCollection(new ArrayList<>(this.callCategoryProperties.stream().map(o -> o.getKey()).collect(Collectors.toSet())));
        
        //作業区分割り当て
        organization.getWorkCategoryCollection().clear();
        organization.setWorkCategoryCollection(workCategoryIds);

        //更新情報を設定
        organization.setUpdatePersonId(loginUserInfoEntity.getId());
        organization.setUpdateDateTime(new Date());

        //プロパティ設定
        organization.setPropertyInfoCollection(customProperties);

        //言語ファイル
        if (Objects.nonNull(oldOrganization)) {
            // 差異がある場合は更新フラグがON
            LocaleFileInfoEntity.updateFlags(organization.getLocaleFileInfoCollection(), oldOrganization.getLocaleFileInfoCollection());
        }

        return true;
    }

    /**
     * 未入力チェック
     *
     * @return 未入力有無
     */
    private boolean checkEmpty() {
        String name = ((StringProperty) detailProperties.get("organizationName").getProperty()).get();
        if (Objects.isNull(name) || name.isEmpty() || name.equals("")) {
            return true;
        }
        String identify = ((StringProperty) detailProperties.get("organizationIdentify").getProperty()).get();
        if (Objects.isNull(identify) || identify.isEmpty() || identify.equals("")) {
            return true;
        }

        return customProperties.stream().map((entity) -> {
            entity.updateMember();
            return entity;
        }).anyMatch((entity) -> (Objects.isNull(entity.getOrganizationPropName())
                || entity.getOrganizationPropName().isEmpty()
                || Objects.isNull(entity.getOrganizationPropType())));
    }

    /**
     *
     * @param uri
     * @return
     */
    private long getUriToOrganizationId(String uri) {
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
     * フィールドから情報を取得する
     *
     * @return
     */
    private OrganizationInfoEntity getRegistData() {
        // やってることは基本的にcreateRegistDataと同じ

        if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                || Objects.isNull(organization)) {
            return null;
        }

        OrganizationInfoEntity ret = new OrganizationInfoEntity();

        //役割重複チェック
        List<Long> roleIds = new ArrayList<>();
        for (RoleIdData entity : roleProperties) {
            roleIds.add(entity.getId());
        }

        //休憩時間重複チェック
        List<Long> breaktimeIds = new ArrayList<>();
        breaktimeProperties.sort(Comparator.comparing(item -> item.getStarttime()));// 開始時間順にソート

        for (BreakTimeIdData entity : breaktimeProperties) {
            breaktimeIds.add(entity.getId());
        }

        List<Long> workCategoryIds;
        //作業区分重複チェック
        workCategoryIds = new ArrayList<>();
        workCategoryProperties.sort(Comparator.comparing(item -> item.getKey()));// ID順にソート
        workCategoryProperties.stream().forEach(o -> workCategoryIds.add(o.getKey()));

        customProperties.stream().forEach(o -> o.updateMember());

        // 表示順を設定
        int order = 0;
        for (OrganizationPropertyInfoEntity entity : customProperties) {
            entity.setOrganizationPropOrder(order++);
        }

        //パスワードについてはもともとの内容が取得不可能なため比較しない
        ret.setOrganizationIdentify(((StringProperty) detailProperties.get("organizationIdentify").getProperty()).get());
        ret.setOrganizationName(((StringProperty) detailProperties.get("organizationName").getProperty()).get());
        ret.setMailAddress(((StringProperty) detailProperties.get("mailAddress").getProperty()).get());

        //役割を設定.
        ret.getRoleCollection().clear();
        ret.setRoleCollection(roleIds);
 
        //休憩割り当て
        ret.getBreakTimeInfoCollection().clear();
        ret.setBreakTimeInfoCollection(breaktimeIds);

        ret.getWorkCategoryCollection().clear();
        ret.setWorkCategoryCollection(workCategoryIds);

        //更新情報を設定
        ret.setUpdatePersonId(loginUserInfoEntity.getId());
        ret.setUpdateDateTime(new Date());

        //プロパティ設定
        ret.setPropertyInfoCollection(customProperties);
        // 言語ファイル設定
        ret.setLocaleFileInfoCollection(organization.getLocaleFileInfoCollection());

        return ret;
    }

    /**
     * コンポーネントが破棄される前に呼び出される。破棄できない場合は、falseを返す。 ほかの画面に遷移するとき変更が存在するなら保存するか確認する
     *
     * @return
     */
    @Override
    public boolean destoryComponent() {
        boolean ret = true;

        //変更を調べて保存。変更がないならそのまま閉じる
        if (this.isChanged()) {
            ret = saveChanges(hierarchyTree.getSelectionModel().getSelectedItem());
        }

        SplitPaneUtils.saveDividerPosition(organizationPane, getClass().getSimpleName());

        return ret;
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

        if (Objects.isNull(oldOrganization) || Objects.isNull(organization)) {
            return false;
        }
        
        if (!StringUtils.isEmpty(((StringProperty) detailProperties.get("password").getProperty()).get())) {
            // パスワードが入力された場合
            return true;
        }

        OrganizationInfoEntity current = getRegistData();
        if (Objects.isNull(current) || current.displayInfoEquals(oldOrganization)) {
            return false;
        }
        return true;
    }

    /**
     * 変更の有無を調べ、するなら保存する
     *
     * @param target 変更を適用する項目
     * @return 保存できなかった、あるいは保存がキャンセルされたらfalse
     */
    private boolean saveChanges(TreeItem<OrganizationInfoEntity> target) {
        try {
            logger.info("saveChanges start");

            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            try {
                return ThreadUtils.joinFXThread(() -> {
                    ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                    if (ButtonType.YES == buttonType) {
                        return regist(target, false);
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
//            copyButton.setDisable(false);
            createButton.setDisable(false);
            moveButton.setDisable(false || !loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.RIGHT_ACCESS));
            authButton.setDisable(false);
            importButton.setDisable(false);
            logger.info("saveChanges end");
        }

        return false;
    }

    /**
     * 組織情報をインポートする。
     */
    private void importOrganization() {
        try {
            File desktopDir = new File(System.getProperty("user.home"), "Desktop");

            // ファイル選択ダイアログを表示する。
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(LocaleUtils.getString("key.FileChoice"));
            fileChooser.setInitialDirectory(desktopDir);

            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(LocaleUtils.getString("key.import.excelFile"), "*.xlsx", "*.xls", "*.xlsm"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(LocaleUtils.getString("key.allCategoryFile"), "*.*"));

            File file = fileChooser.showOpenDialog(sc.getWindow());
            if (Objects.isNull(file)) {
                return;
            }

            blockUI(true);
            Task task = new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    // Excelファイルを読み込み、組織インポートファイルを作成する。
                    String folderPath = Paths.get(System.getenv("ADFACTORY_HOME"), "temp", "organizationEdit").toString();
                    String filePath = Paths.get(folderPath, "organization.tsv").toString();

                    File folder = new File(folderPath);
                    if (!folder.exists()) {
                        // フォルダがない場合は作成する。
                        folder.mkdirs();
                    } else {
                        // すでにフォルダがある場合はフォルダの中のファイルを削除する。
                        FilenameFilter filter = new FilenameFilter() {
                            // 拡張子を指定する
                            public boolean accept(File file, String str) {
                                return str.endsWith(".tsv");
                            }
                        };
                        File[] list = folder.listFiles(filter);
                        Arrays.stream(list).forEach(file -> file.delete());
                    }

                    int count = createImportFile(file.getPath(), filePath);
                    if (count < 1) {
                        return -1;// 読み込みエラー
                    }

                    // 組織情報をインポートする。
                    ResponseEntity response = organizationFacade.importFile(filePath);
                    if (Objects.isNull(response)) {
                        return null;// 通信エラー
                    }

                    if (response.isSuccess()) {
                        return count;// インポート成功(件数)
                    } else {
                        return 0;// インポート失敗
                    }
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // 処理結果
                        if (Objects.isNull(this.getValue())) {
                            // 通信エラー
                            showAlert(LocaleUtils.getString("key.Import"), LocaleUtils.getString("key.alert.communicationServer"), Alert.AlertType.ERROR);
                            return;
                        }

                        int count = this.getValue();

                        Alert.AlertType alertType;
                        String message;
                        if (count > 0) {
                            // インポート成功
                            alertType = Alert.AlertType.INFORMATION;
                            message = String.format(LocaleUtils.getString("key.import.organization.success"), count);
                        } else if (count == -1) {
                            // 読み込みエラー
                            alertType = Alert.AlertType.ERROR;
                            message = LocaleUtils.getString("key.EquipmentImportFormatError");
                        } else {
                            // インポート失敗
                            alertType = Alert.AlertType.ERROR;
                            message = LocaleUtils.getString("key.import.organization.failed");
                        }

                        showAlert(LocaleUtils.getString("key.Import"), message, alertType);

                        // 組織のキャッシュを削除する。
                        CacheUtils.removeCacheData(OrganizationInfoEntity.class);

                        // 表示を更新する。
                        createRoot(null);
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

                        // インポート失敗
                        showAlert(LocaleUtils.getString("key.Import"), LocaleUtils.getString("key.import.organization.failed"), Alert.AlertType.ERROR);
                    } finally {
                        blockUI(false);
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 組織インポートファイル(TSV)を作成する。
     *
     * @param excelFilePath Excelファイルパス
     * @return 読み込み件数
     */
    private int createImportFile(String excelFilePath, String filePath) throws Exception {
        int result = -1;
        char separator = '\t';
        char quotechar = CSVWriter.NO_QUOTE_CHARACTER;
        String lineEnd = "\r\n";

        List<ImportOrganizationData> datas = this.readExcelFile(excelFilePath);
        if (Objects.isNull(datas)) {
            return result;
        }

        OutputStream outputStream = new FileOutputStream(filePath);
        OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        try (CSVWriter writer = new CSVWriter(outputWriter, separator, quotechar, lineEnd)) {
            for (ImportOrganizationData data : datas) {
                List<String> row = new ArrayList<>();

                row.add(data.getOrganizationIdentify());    // 組織識別名
                row.add(data.getOrganizationName());        // 組織名
                row.add(data.getParentIdentify());          // 親組織識別名
                row.add(data.getMailAddress());             // メールアドレス

                writer.writeNext(row.toArray(new String[row.size()]));
            }

            writer.flush();
        }

        return datas.size();
    }

    /**
     * Excelファイルを読み込む。
     *
     * @param path Excelファイルのパス
     * @return 組織インポート情報一覧
     */
    private List<ImportOrganizationData> readExcelFile(String path) {
        logger.info("readExcelFile: {}", path);
        List<ImportOrganizationData> values = null;

        final int startRow = 2;           // 開始行
        final int idxName = 1;            // 組織名
        final int idxIdentify = 2;        // 組織識別名
        final int idxParent = 3;          // 親組織識別名
        final int idxMailAddress = 4;     // メールアドレス

        List<Integer> colIds = Arrays.asList(
                idxName,
                idxIdentify,
                idxParent,
                idxMailAddress
        );

        final int maxIdx = colIds.stream().mapToInt(p -> p).max().getAsInt();

        int count;
        List<String> cols;

        try {
            ExcelFileUtils excelFileUtils = new ExcelFileUtils();
            List<List<String>> rows = excelFileUtils.readExcel(path, startRow, maxIdx, null);

            if (Objects.nonNull(rows)) {
                values = new ArrayList();
                for (count = 0; count < rows.size(); count++) {
                    cols = rows.get(count);

                    ImportOrganizationData data = new ImportOrganizationData();

                    // 組織名
                    String organizationName = cols.get(idxName - 1);
                    if (StringUtils.isEmpty(organizationName)) {
                        // 必須項目が空
                        return null;
                    }
                    data.setOrganizationName(organizationName);

                    // 組織識別名
                    String organizationIdentify = cols.get(idxIdentify - 1);
                    if (StringUtils.isEmpty(organizationIdentify)) {
                        // 必須項目が空
                        return null;
                    }
                    data.setOrganizationIdentify(organizationIdentify);

                    // 親組織識別名
                    String parentIdentify = cols.get(idxParent - 1);
                    if (StringUtils.isEmpty(parentIdentify)) {
                        parentIdentify = "";
                    }
                    data.setParentIdentify(parentIdentify);
                    
                    // メールアドレス
                    String mailAddress = cols.get(idxMailAddress - 1);
                    if (StringUtils.isEmpty(mailAddress)) {
                        mailAddress = "";
                    }
                    data.setMailAddress(mailAddress);

                    values.add(data);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            values = null;
        }

        return values;
    }

    /**
     * メッセージダイアログを表示する。
     *
     * @param title タイトル
     * @param message メッセージ
     * @param type ダイアログ種別
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 設備を検索する。
     * 
     * @param event 
     */    
    @FXML
    private void onSearch(ActionEvent event) {
        if (StringUtils.isEmpty(searchField.getText())) {
            this.createRoot(null);
            return;
        }

        try {
            blockUI(true);
            
            this.rootItem.getChildren().clear();

            Task task = new Task<List<OrganizationInfoEntity>>() {
                @Override
                protected List<OrganizationInfoEntity> call() throws Exception {

                    OrganizationSearchCondition condition = new OrganizationSearchCondition();
                    condition.setOrganizationName(searchField.getText().trim());
                    condition.setRemoveFlag(false);
                    condition.setWithChildCount(true);
                    List<OrganizationInfoEntity> organizations = organizationFacade.search(condition);

                    return organizations;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {

                        TreeItem<OrganizationInfoEntity> _rootItem = new TreeItem<>(new OrganizationInfoEntity(ROOT_ID, null, LocaleUtils.getString("key.Organization"), null));
                        hierarchyTree.rootProperty().setValue(_rootItem);

                        long count = this.getValue().size();
                        _rootItem.getValue().setChildCount(count);

                        this.getValue()
                                .stream()
                                .filter(o -> o.getChildCount() == 0)
                                .sorted((OrganizationInfoEntity o1, OrganizationInfoEntity o2) -> o1.getOrganizationName().compareTo(o2.getOrganizationName()))
                                .forEach(o -> {
                                    TreeItem<OrganizationInfoEntity> item = new TreeItem<>(o);
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
