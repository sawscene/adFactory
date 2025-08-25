/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.summaryreportplugin.component;

import adtekfuji.admanagerapp.summaryreportplugin.common.SendMailListFactory;
import adtekfuji.admanagerapp.summaryreportplugin.common.SummaryReportElementFactory;
import adtekfuji.clientservice.ResourceInfoFacade;
import adtekfuji.clientservice.SummaryReportFacade;
import adtekfuji.fxscene.SceneContiner;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.control.*;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.resource.ResourceInfoEntity;
import jp.adtekfuji.adFactory.entity.summaryreport.SummaryReportConfigElementEntity;
import jp.adtekfuji.adFactory.entity.summaryreport.SummaryReportConfigInfoEntity;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.ThreadUtils;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AggregateUnitEnum;
import jp.adtekfuji.adFactory.enumerate.ResourceTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.SendFrequencyEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.DetailRecordFactory;
import jp.adtekfuji.javafxcommon.PropertyBindEntity;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.treecell.SummaryReportTreeCell;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author okada
 */
@FxComponent(id = "SummaryReportCompo", fxmlPath = "/fxml/compo/summary_report_compo.fxml")
public class SummaryReportCompoFxController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();

    private final SummaryReportFacade summaryReportFacade = new SummaryReportFacade();
    private final ResourceInfoFacade resourceInfoFacade = new ResourceInfoFacade();

    private final SceneContiner sc = SceneContiner.getInstance();
    private final Map<String, PropertyBindEntity> detailProperties = new LinkedHashMap<>();
    // 全サマリーレポート設定情報
    List<SummaryReportConfigInfoEntity> entities = new ArrayList<>();
    ResourceInfoEntity resourceInfoEntity = null;

    // 画面に表示している情報(画面で入力した内容もこちらにセットされる)
    private SummaryReportConfigInfoEntity viewDisplayEntity;
    // ログインユーザー情報
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private TreeItem<SummaryReportConfigInfoEntity> rootItem;

    // 画面に表示した最初の情報　比較用
    private SummaryReportConfigInfoEntity initialDisplayEntity;

    // 編集ボタン無効フラグ (編集権限がない場合 true)
    private boolean isDisableEdit = false;
    // 保存確認ダイアログで取消を選択した場合の、階層ツリー移動キャンセルフラグ
    private boolean isCancelMove = false;
    private boolean isCancelMove2 = false;

    // 画面に表示せれたいるメール内容設定エリア（バインド済み）
    private LinkedList<SummaryReportConfigElementEntity> properties = new LinkedList<>();

    @FXML
    private SplitPane summaryReportPane;
    @FXML
    private TreeView<SummaryReportConfigInfoEntity> hierarchyTree;
    @FXML
    private ToolBar hierarchyBtnArea;
    @FXML
    private Button delButton;
    @FXML
    private Button copyButton;
    @FXML
    private Button createButton;
    @FXML
    private VBox detailPane;
    @FXML
    private Button sendTestMailButton;
    @FXML
    private Button registButton;
    @FXML
    private Pane Progress;

    /**
     * サマリーレポート設定編集コンポーネントを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        SplitPaneUtils.loadDividerPosition(summaryReportPane, getClass().getSimpleName());

        // リソース編集権限がない場合、編集関連のボタンを無効化する。
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            isDisableEdit = true;
            ctrlButtonDisable(isDisableEdit, true, true);
        }

        // 階層ツリーのフォーカス移動イベント
        hierarchyTree.getFocusModel().focusedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (isCancelMove) {
                // 移動をキャンセルしたら、元の場所を選択状態にする。
                hierarchyTree.getSelectionModel().select(oldValue.intValue());
            }else{
                hierarchyTree.getSelectionModel().select(newValue.intValue());
            }            
        });
        
        // 階層ツリーのノード選択イベント
        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<SummaryReportConfigInfoEntity>> observable, TreeItem<SummaryReportConfigInfoEntity> oldValue, TreeItem<SummaryReportConfigInfoEntity> newValue) -> {
            // 移動キャンセル中は何もしない。
            if (isCancelMove) {
                isCancelMove = false;
                return;
            }
            if (isCancelMove2) {
                return;
            }
            // 詳細情報を表示する。
            dispInfo(newValue);
        });

        hierarchyTree.setCellFactory((TreeView<SummaryReportConfigInfoEntity> o) -> new SummaryReportTreeCell());

        // ツリーのルートノードを生成する。
        createRoot(null, true);
    }

    /**
     * 詳細情報を表示する。
     *
     * @param treeItem 選択ノード
     */
    private void dispInfo(TreeItem<SummaryReportConfigInfoEntity> treeItem) {
        try {
            if (Objects.nonNull(treeItem)) {
                //変更を確認し保存
                if (this.isChanged()) {
                    int[] isCompleted = saveChanges(true);
                    if (isCompleted[0] == 2) {
                        // 移動をキャンセルする。(元の場所を選択状態にする操作は、後で発生するフォーカス移動イベントで行なう。)
                        isCancelMove = true;
                        return;
                    }else if(isCompleted[0] == 0 && isCompleted[1] == 0){
                        // 必須チェックでNGの場合は移動キャンセルとする。
                        isCancelMove = true;
                        return;
                    }
                    
                    if(isCompleted[1] == 1){
                        isCancelMove2 = true;
                        // 保存されている場合は情報再取得後、ツリーの情報を更新
                        createRoot(null, false);
                        isCancelMove2 = false;
                    }
                }

                //ルート以外を選んだ時組織の詳細を表示、ルートの場合クリアする
                if (!Objects.equals(treeItem, rootItem)) {
                    this.dispInfoDetail(treeItem.getValue());
                } else {
                    viewDisplayEntity = null;
                    this.clearDetailView();
                }

            } else {
                viewDisplayEntity = null;
                this.clearDetailView();
            }

            // ツリーの編集ボタン状態を戻す。
            ctrlButtonDisable(isDisableEdit, true, false);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        
        isCancelMove2 = false;
    }

    /**
     * サマリーレポート設定の詳細情報を表示する。
     *
     * @param entity 選択ノード
     */
    private void dispInfoDetail(SummaryReportConfigInfoEntity entity) {

        viewDisplayEntity = entity.clone();
        
        initialDisplayEntity = null;
        updateDetailView(viewDisplayEntity);
        //初期情報を一時保管
        initialDisplayEntity = viewDisplayEntity.clone();

    }

    /**
     * サマリーレポート設定削除
     *
     * @param event 削除ボタン押下
     */
    @FXML
    private void onDelete(ActionEvent event) {
        try {
            // ツリー未選択又はルートを選択している場合は処理を行わない。
            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                    || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getRootFlag()) {
                return;
            }

            // 確認ダイアログを表示
            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION,
                    LocaleUtils.getString("key.Delete"),
                    LocaleUtils.getString("key.DeleteSingleMessage"),
                    viewDisplayEntity.getTitle());
            if (ret.equals(ButtonType.CANCEL)) {
                return;
            }

            //変更を破棄
            initialDisplayEntity = null;

            /**
             * サマリーレポート設定情報から選択しているエンティティを削除
             * ※ツリーのIndexとentitiesにはルートの有無の関係で要素数に一つ差異がある。
             */
            List<SummaryReportConfigInfoEntity> infos = new ArrayList<>(entities);
            infos.remove(viewDisplayEntity.getId().intValue());

            // 登録後、ツリー表示(ルート選択)を更新する。
            if(registSummaryReportConfigInfo(infos)){
                createRoot(null, true);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * サマリーレポート設定コピー
     *
     * @param event コピーボタン押下
     */
    @FXML
    private void onCopy(ActionEvent event) {
        try {
            // ツリー未選択又はルートを選択している場合は処理を行わない。
            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                    || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getRootFlag()) {
                return;
            }

            int[] isCompleted = {0, 1};
            int treeIndex = getTreeSelectedIndex();
            Integer entityId = viewDisplayEntity.getId();

            //変更を調べる
            if (this.isChanged()) {
                isCompleted = saveChanges(false);
            }

            // YSEボタンを選択し情報が保存された場合たのみ処理
            if (isCompleted[0] == 0 && isCompleted[1] == 1) {
                //変更を破棄
                initialDisplayEntity = null;     
                
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION,
                        LocaleUtils.getString("key.Copy"),
                        LocaleUtils.getString("key.CopyMessage"),
                        viewDisplayEntity.getTitle());
                
                if (ret.equals(ButtonType.CANCEL)) {
                    // データの更新を行っている場合はツリーの更新を行う。
                    if (isCompleted[1] == 1) {
                        createRoot(treeIndex, true);
                    }
                    return;
                }

                // 保存を行った場合はサマリーレポート設定情報を取得
                if (isCompleted[1] == 1) {
                    createRoot(null, false);
                }
                
                /**
                 * サマリーレポート設定情報から選択しているエンティティを取得し、 編集後、entitiesに追加する。
                 * ※ツリーのIndexとentitiesにはルートの有無の関係で要素数に一つ差異がある。
                 */
                List<SummaryReportConfigInfoEntity> infos = new ArrayList<>(entities);
                SummaryReportConfigInfoEntity info = infos.get(entityId).clone();
                

                // サマリーレポート名に"-copy"を最後に付与
                info.setTitle(info.getTitle() + "-copy");

                int addTreeIndex = treeIndex + 1;
                int addinfosIndex = entityId + 1;

                // 画面の値をセット ※セットする要素番号が要素数を超えた場合は最後に追加
                if (entities.size() > addinfosIndex) {
                    infos.add(addinfosIndex, info);
                } else {
                    infos.add(info);
                }

                // 登録後、ツリー表示(追加情報を選択)を更新する。
                if(registSummaryReportConfigInfo(infos)){
                    createRoot(addTreeIndex, true);
                }

            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * サマリーレポート設定新規作成
     *
     * @param event 新規作成ボタン押下
     */
    @FXML
    private void onCreate(ActionEvent event) {
        try {
            int[] isCompleted = {0, 1};

            //変更を調べる
            if (this.isChanged()) {
                isCompleted = saveChanges(true);
                // データの更新を行っている場合はツリーの更新を行う。
                if (isCompleted[0] == 0 && isCompleted[1] == 1) {
                    initialDisplayEntity = null;    //変更を破棄
                    createRoot(null, true);
                }
            }

            // YSEボタンを選択し情報が保存された場合と
            // NOボタンを選択した場合の処理
            if ((isCompleted[0] == 0 && isCompleted[1] == 1)
                    || isCompleted[0] == 1) {
                viewDisplayEntity = new SummaryReportConfigInfoEntity(true);
                updateDetailView(viewDisplayEntity);
                initialDisplayEntity = new SummaryReportConfigInfoEntity(true);

                // 新規作成時はツリーの編集ボタンを無効にする。
                ctrlButtonDisable(true, true, false);

                // ボタン無効でフォーカスが移動するがその移動先がTextFieldだとIMEの座標が狂う。TextField以外に合わせる。
                detailPane.requestFocus();
            }
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * サマリーレポート設定登録
     *
     * @param event 登録ボタン押下
     */
    @FXML
    private void onRegist(ActionEvent event) {
        try {
            Integer selectedId = getTreeSelectedIndex();
            if(registViewSummaryReportConfigInfo()){
                //変更を破棄
                initialDisplayEntity = null;

                createRoot(selectedId, true);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 表示されているサマリーレポート設定情報を保存する。
     *
     * @return true:正常に保存処理完了
     */
    private boolean registViewSummaryReportConfigInfo() {

        try {
            SummaryReportConfigInfoEntity info = getRegistData();

            if (checkEmpty(info)) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return false;
            }
            
            List<SummaryReportConfigInfoEntity> infos = new ArrayList<>(entities);

            // 画面の値をセット
            if (info.getCreateFlag()) {
                // 新規作成時はツリーのエンティティに含まれないので追加する
                infos.add(info);
            } else {
                infos.set(info.getId(), info);
            }

            if(!registSummaryReportConfigInfo(infos)){
                return false;
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return true;
    }

    /**
     * サマリーレポート設定情報を保存する。
     *
     * @return true:正常に保存処理完了
     */
    private boolean registSummaryReportConfigInfo(List<SummaryReportConfigInfoEntity> infos) {

        try {
            ResourceInfoEntity entity = new ResourceInfoEntity(ResourceTypeEnum.SUMMARY_REPORT_CONFIG);
            entity.setResourceId(resourceInfoEntity.getResourceId());
            entity.setResourceKey("Default");
            entity.setResourceString(JsonUtils.objectsToJson(infos));

            ResponseEntity response;
            if (Objects.isNull(entity.getResourceId())) {
                response = resourceInfoFacade.add(entity);
            } else {
                response = resourceInfoFacade.update(entity);
            }
            
            if (Objects.nonNull(response) && ResponseAnalyzer.getAnalyzeResult(response)){
                if (Objects.isNull(entity.getResourceId())) {
                    resourceInfoEntity.setResourceId(response.getUriId());
                }                
            } else {
                return false;
            }

            summaryReportFacade.loadSummaryReportConfig();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return true;
    }

    /**
     * 各項目から登録に必要な情報を取得する
     *
     * @return 取得したSummaryReportConfigEntity
     */
    private SummaryReportConfigInfoEntity getRegistData() {

        // 新規の場合、各画面項目にviewDisplayEntityのプロパティがバインドされています。
        SummaryReportConfigInfoEntity info = viewDisplayEntity.clone();

        // メール送信日(メール頻度が8:毎月の場合には必須)
        if(SendFrequencyEnum.MONTHLY != info.getSendFrequency()){
            info.setSendDate("");
        }
        
        return info;
    }


    /**
     * ツリーのルートの表示を更新する。 ※ツリーの更新フラグがfalseの場合、サマリーレポート設定情報を取得のみを行う。
     *
     * @param treeIndex 更新後に選択状態にするノードのIndex(ルートの子ノートの要素番号) (nullの場合はルートを選択。)
     * @param isRefresh ツリーの更新フラグ(true:ツリーの更新を行う)
     */
    private void createRoot(Integer treeIndex, boolean isRefresh) {
        logger.debug("createRoot start.");
        logger.debug("createRoot パラメータ[treeIndex:{}]", treeIndex);
        
        try {
            blockUI(true);

            // ルートが存在しない場合は新規作成する。
            if (Objects.isNull(this.rootItem)) {
                this.rootItem = new TreeItem<>(new SummaryReportConfigInfoEntity(LocaleUtils.getString("key.SummaryReportTitle"), true));
            }

            this.rootItem.getChildren().clear();
            hierarchyTree.rootProperty().setValue(rootItem);

            // サマリーレポート設定情報を取得
            getSummaryReportConfigEntity();

            // ツリーにエンティティをセット
            this.rootItem.getChildren().addAll(
                    entities.stream()
                            .map(TreeItem::new)
                            .collect(Collectors.toList()));

            if(isRefresh){
                // TreeItemの子を表示
                this.rootItem.setExpanded(true);
                
                //　ツリーのノード選択
                if (Objects.nonNull(treeIndex)) {
                    // 指定されたノードを選択状態にする。
                    TreeItem<SummaryReportConfigInfoEntity> find = rootItem.getChildren().get(treeIndex);
                    hierarchyTree.getSelectionModel().select(find);
                } else {
                    // 選択ノードの指定がない場合は、ルートを選択状態にする。
                    hierarchyTree.getSelectionModel().select(rootItem);
                }

                // 選択ノードが見えるようスクロール
                this.hierarchyTree.scrollTo(this.hierarchyTree.getSelectionModel().getSelectedIndex());                
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
            logger.debug("createRoot end.");
        }
    }

    /**
     * サマリーレポート設定情報を行う。
     */
    private void getSummaryReportConfigEntity() {
        logger.debug("getSummaryReportConfigEntity start.");

        try {
            blockUI(true);

            // サマリーレポート設定情報を取得
            resourceInfoEntity = resourceInfoFacade.findByTypeKey(ResourceTypeEnum.SUMMARY_REPORT_CONFIG, "Default");

            if (Objects.isNull(resourceInfoEntity)) {
                resourceInfoEntity = new ResourceInfoEntity(ResourceTypeEnum.SUMMARY_REPORT_CONFIG);
                resourceInfoEntity.setResourceKey("Default");
                resourceInfoEntity.setResourceString("[]");

//                final String text = "["
//                        + "{\"title\":\"test1\",\"disable\":false,\"aggregateUnit\":\"0\",\"itemName\":\"項目名１\",\"sendFrequency\":\"1\",\"sendTime\":\"00:00\",\"mails\":[\"1\",\"2\"],\"SummaryReportElements\":[]},"
//                        + "{\"title\":\"test2\",\"disable\":true,\"aggregateUnit\":\"1\",\"itemName\":\"項目名２\",\"sendFrequency\":\"8\",\"sendTime\":\"09:00\",\"mails\":[\"1\",\"2\"],\"SummaryReportElements\":[]},"
//                        + "{\"title\":\"test2\",\"disable\":true,\"aggregateUnit\":\"1\",\"itemName\":\"項目名２\",\"sendFrequency\":\"8\",\"sendTime\":\"09:00\",\"mails\":[\"1\",\"2\"],\"SummaryReportElements\":[]},"
//                        + //                        "{\"title\":\"test3\",\"disable\":\"1\",\"aggregateUnit\":\"1\",\"itemName\":\"項目名３\",\"sendFrequency\":\"8\",\"sendDate\":\"10\",\"sendTime\":\"13:00\",\"mails\":[\"1\",\"2\"],\"SummaryReportElements\":[]}," +
//                        "{\"title\":\"test3\",\"disable\":true,\"aggregateUnit\":\"1\",\"itemName\":\"項目名３\",\"sendFrequency\":\"8\",\"sendDate\":\"10\",\"sendTime\":\"13:00\",\"mails\":[\"1\",\"2\"],\"SummaryReportElements\":[]}"
//                        + "]";
//                resourceInfoEntity.setResourceString(text);
            }

            // ツリーにセットするエンティティに取得したサマリーレポート設定情報をセット
            entities = JsonUtils.jsonToObjects(resourceInfoEntity.getResourceString(), SummaryReportConfigInfoEntity[].class);

            Integer id = 0;
            // 初期値を設定
            for (SummaryReportConfigInfoEntity info : entities) {
                    info.setCreateFlag(false);
                    info.setRootFlag(false);
                    info.setId(id++);       // 要素数をセット
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
            logger.debug("getSummaryReportConfigEntity end.");
        }
    }
    
    /**
     * テスト送信
     *
     * @param actionEvent 登録ボタン押下
     */
    @FXML
    private void onSendTestMail(ActionEvent actionEvent) {
        logger.debug("onSendTestMail start.");
        
        try {
            // 新規登録以外で、ツリー未選択又はルートを選択している場合は処理を行わない。
            if (Objects.isNull(viewDisplayEntity) || !viewDisplayEntity.getCreateFlag()) {
                if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())
                        || hierarchyTree.getSelectionModel().getSelectedItem().getValue().getRootFlag()) {
                    logger.debug("onSendTestMail 未処理");
                    return;
                }                
            }

            int[] isCompleted = {0, 1};
            int treeIndex = getTreeSelectedIndex();

            //変更を調べる、ただし、新規作成時は無条件で登録を行う。
            if (viewDisplayEntity.getCreateFlag() || this.isChanged()) {
                isCompleted = saveChanges(false);
            } else {
                SummaryReportConfigInfoEntity info = getRegistData();
                if (checkEmpty(info)) {
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                    return;
                }
            }

            // YSEボタンを選択し情報が保存された場合たのみ処理
            if (isCompleted[0] == 0 && isCompleted[1] == 1) {
                
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION,
                        LocaleUtils.getString("key.Sending"),
                        LocaleUtils.getString("key.SendMessage"));
                
                if (ret.equals(ButtonType.CANCEL)) {
                    // データの更新を行っている場合はツリーの更新を行う。
                    if (isCompleted[1] == 1) {
                        initialDisplayEntity = null;    //変更を破棄
                        createRoot(treeIndex, true);
                    }
                    return;
                }

                logger.debug("onSendTestMail sendMailにセットする引数:{}", treeIndex);
                
                // 引数には選択している設定お表示順をセット(0～)
                summaryReportFacade.sendMail(treeIndex);
                
                // 保存を行った場合はツリー表示(元情報を選択)を更新する。
                if (isCompleted[1] == 1) {
                    initialDisplayEntity = null;    //変更を破棄
                    createRoot(treeIndex, true);
                }

            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        
        logger.debug("onSendTestMail end.");
    }

    /**
     * 集計単位コンボボックスセル
     */
    static class AggregateUnitComboBoxCellFactory extends ListCell<AggregateUnitEnum> {

        @Override
        protected void updateItem(AggregateUnitEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getValue()));
            }
        }
    }

    /**
     * メール頻度コンボボックスセル
     */
    static class SendFrequencyComboBoxCellFactory extends ListCell<SendFrequencyEnum> {

        @Override
        protected void updateItem(SendFrequencyEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getValue()));
            }
        }
    }

    /**
     * 詳細画面更新処理
     *
     * @param entity ツリーで選択された情報
     */
    private void updateDetailView(SummaryReportConfigInfoEntity entity) {
        if (Objects.isNull(entity)) {
            return;
        }

        clearDetailView();

        // レポート名(名称)
        PropertyBindEntity pbnReportName = PropertyBindEntity.createRegerxString(
                LocaleUtils.getString("key.ReportName") + LocaleUtils.getString("key.RequiredMark"),
                entity.getTitle(),
                "^.{0,16}$"
        );
        pbnReportName.setPrefWidth(300);
        pbnReportName.setProperty(entity.titleProperty());
        detailProperties.put("ReportName", pbnReportName);

        // 無効
        detailProperties.put("InvalidItem",
                (PropertyBindEntity) PropertyBindEntity.createBoolean(
                                LocaleUtils.getString("key.false"),
                        "",
                        entity.getDisable()
                )
                        .setProperty(entity.disableProperty())
        );

        // 集計単位
        detailProperties.put("AggregateUnit",
                (PropertyBindEntity) PropertyBindEntity.createCombo(
                                LocaleUtils.getString("key.AggregateUnit") + LocaleUtils.getString("key.RequiredMark"),
                        Arrays.asList(AggregateUnitEnum.values()),
                        new AggregateUnitComboBoxCellFactory(),
                        (ListView<AggregateUnitEnum> param) -> new AggregateUnitComboBoxCellFactory(),
                        entity.getAggregateUnit()
                )
                        .setProperty(entity.aggregateUnitProperty())
        );

        // 項目名
        detailProperties.put("EquipmentName",
                (PropertyBindEntity) PropertyBindEntity.createRegerxString(
                                LocaleUtils.getString("key.PropertyName") + LocaleUtils.getString("key.RequiredMark"),
                        entity.getItemName(),
                        "^.{0,256}$"
                )
                        .setProperty(entity.itemNameProperty())
        );

        // メール頻度
        detailProperties.put("SendFrequency",
                (PropertyBindEntity) PropertyBindEntity.createCombo(
                                LocaleUtils.getString("key.MailFrequency") + LocaleUtils.getString("key.RequiredMark"),
                        Arrays.asList(SendFrequencyEnum.values()),
                        new SendFrequencyComboBoxCellFactory(),
                        (ListView<SendFrequencyEnum> param) -> new SendFrequencyComboBoxCellFactory(),
                        entity.getSendFrequency()
                )
                        .setProperty(entity.sendFrequencyProperty())
        );

        // メール送信日
        PropertyBindEntity propertyBindEntity
                = PropertyBindEntity.createRegerxString(
                LocaleUtils.getString("key.DateOfEmail") + LocaleUtils.getString("key.RequiredMark"),
                        Objects.isNull(entity.getSendDate()) ? "" : entity.getSendDate(),
                        "^([1-9]|[1-2][0-9]|3[0-1])?$"
                );
        propertyBindEntity.setPrefWidth(60.0);
        final BooleanBinding bb = Bindings.equal(SendFrequencyEnum.MONTHLY, entity.sendFrequencyProperty());
        propertyBindEntity.setNodeConsumer(node -> {
            Node item = (Node) node;
            item.managedProperty().bind(bb);
            item.visibleProperty().bind(bb);
        });
        propertyBindEntity.setProperty(entity.sendDateProperty());
        detailProperties.put("SendDate", propertyBindEntity);

        // 集計開始時間
        PropertyBindEntity pbeSendTime = PropertyBindEntity.createTimeHMStamp(
                LocaleUtils.getString("key.StartTimeForTally") + LocaleUtils.getString("key.RequiredMark"),
                entity.getSendTime()
        );
        pbeSendTime.setPrefWidth(120.0);
        pbeSendTime.setProperty(entity.sendTimeProperty());
        detailProperties.put("SendTime", pbeSendTime);

        // 送付先
        Table<SummaryReportConfigInfoEntity> summaryReportTable = new Table<>(this.detailPane.getChildren());
        summaryReportTable.setAbstractRecordFactory(new DetailRecordFactory(summaryReportTable, new LinkedList<>(detailProperties.values())));
        summaryReportTable.setAbstractRecordFactory(new SendMailListFactory(summaryReportTable, entity));

        // メール内容設定エリアと引数のエンティティとのバインド
        properties.clear();
        properties = entity.getSummaryReportElementEntities();

        // メール内容設定
        Table<SummaryReportConfigElementEntity> summaryReportElementTable = new Table<>(this.detailPane.getChildren());
        summaryReportElementTable.isAddRecord(true);
        summaryReportElementTable.isColumnTitleRecord(true);
        summaryReportElementTable.title(LocaleUtils.getString("key.MailContentSettings") + LocaleUtils.getString("key.RequiredMark"));
        summaryReportElementTable.styleClass("ContentTitleLabel");
        summaryReportElementTable.setAbstractRecordFactory(new SummaryReportElementFactory(summaryReportElementTable, properties));

    }

    /**
     * 詳細画面初期化
     */
    private void clearDetailView() {
        detailPane.getChildren().clear();
        detailProperties.clear();
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
     * @return true:内容が変更されている
     */
    private boolean isChanged() {

        // 編集権限なしは常に無変更
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            return false;
        }

        if (Objects.isNull(initialDisplayEntity) || Objects.isNull(viewDisplayEntity)) {
            return false;
        }

        // 現在の画面の値を取得
        SummaryReportConfigInfoEntity entity = getRegistData();
        if (Objects.isNull(entity)) {
            return false;
        }

        // 現在の画面の値と画面の初期表示時の値を比較
        if (entity.displayInfoEquals(initialDisplayEntity)) {
            return false;
        }

        return true;
    }

    /**
     * コンポーネントが破棄される前に呼び出される。破棄できない場合は、falseを返す。 ほかの画面に遷移するとき変更が存在するなら保存するか確認する
     *
     * @return true
     */
    @Override
    public boolean destoryComponent() {

        if (isChanged()) {
            int[] isCompleted = saveChanges(true);
            if (isCompleted[0] == 2) {
                // 移動をキャンセルする。
                return false;
            }else if(isCompleted[0] == 0 && isCompleted[1] == 0){
                // 必須チェックでNGの場合は移動キャンセルとする。
                return false;
            }
        }

        SplitPaneUtils.saveDividerPosition(summaryReportPane, getClass().getSimpleName());
        return true;
    }

    /**
     * 変更の有無を調べ、するなら保存する
     * 　CANCELボタンが非表示の場合、NOボタン押下でCANCELボタンと同じ処理を行う。
     *
     * @param cancelButtonDisplayFlag true:CANCELボタンを表示 / false:CANCELボタンを非表示
     * @return 0:0-ダイアログでYES、1-NO、2-CANCEL 1:0-保存されなかった、1-保存された
     */
    private int[] saveChanges(boolean cancelButtonDisplayFlag) {
        int[] ret = {2, 0};

        try {
            logger.info("saveChanges start");
            
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            try {
                return ThreadUtils.joinFXThread(() -> {
                    // 表示ボタン設定
                    ButtonType[] buttonTypes = new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL};
                    ButtonType defaultButton = ButtonType.CANCEL;
                    if(!cancelButtonDisplayFlag){
                        buttonTypes = new ButtonType[]{ButtonType.YES, ButtonType.NO};
                        defaultButton = ButtonType.NO;
                    }
                    
                    ButtonType buttonType = sc.showMessageBox(
                            Alert.AlertType.NONE,
                            title, message,
                            buttonTypes,
                            defaultButton);

                    if (ButtonType.YES == buttonType) {
                        //保存成功でそのまま閉じる。それ以外なら閉じさせない
                        ret[0] = 0;
                        if (registViewSummaryReportConfigInfo()) {
                            ret[1] = 1;
                        }
                    } else if (ButtonType.NO == buttonType) {
                        //いいえの場合何もせずに閉じる
                        ret[0] = 1;
                    }
                    return ret;
                });
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        } finally {
            // ツリーのボタンを有効にする。
//            ctrlButtonDisable(false, true, false);
            logger.info("saveChanges end");
        }

        return ret;
    }

    /**
     * 選択しているノードのIndexを取得(ルートの子ノートの要素番号) 
     * ただし、新規作成時はツリーの最後にセットされるのでsizeとなる
     *
     * @return ルートの子ノートのIndex(要素番号)
     */
    private Integer getTreeSelectedIndex() {
        // 画面の値をセット
        if (viewDisplayEntity.getCreateFlag()) {
            // 新規作成時はツリーの最後にセットされるのでsizeとする
            return this.rootItem.getChildren().size();
        }

        TreeItem<SummaryReportConfigInfoEntity> find = this.hierarchyTree.getSelectionModel().getSelectedItem();
        int index = this.rootItem.getChildren().indexOf(find);
        if (index < 0) {
            logger.debug("getTreeSelectedIndex ツリーで選択したノードが見つかりませんでした。");
            // getSelectedIndexだとルートも含めた表示順になるので-1を行う。
            index = this.hierarchyTree.getSelectionModel().getSelectedIndex() - 1;
        }
        return index;
    }

    /**
     * 画面のボタンの操作可不可制御
     *
     * @param flag true:操作不可へ / false:操作可へ
     */
    private void ctrlButtonDisable(boolean flag, boolean refreshTreeArea, boolean refreshEditingArea) {
        if (refreshTreeArea) {
            delButton.setDisable(flag);
            copyButton.setDisable(flag);
            createButton.setDisable(flag);
        }
        if (refreshEditingArea) {
            sendTestMailButton.setDisable(flag);
            registButton.setDisable(flag);
        }
    }

    /**
     * 未入力項目があるか
     *
     * @return (true : 未入力あり, false : 未入力なし)
     */
    private boolean checkEmpty(SummaryReportConfigInfoEntity entity) {

        // レポート名
        if(StringUtils.isEmpty(entity.getTitle())){
            return true;
        }
        // 項目名
        if(StringUtils.isEmpty(entity.getItemName())){
            return true;
        }
        // メール送信日(メール頻度が8:毎月の場合には必須)
        if(SendFrequencyEnum.MONTHLY == entity.getSendFrequency()
                && StringUtils.isEmpty(entity.getSendDate())){
            return true;
        }        
        // 集計開始時間
        if(StringUtils.isEmpty(entity.getSendTime())){
            return true;
        }
        // メールリスト
        List<Long> mails = entity.getMails();
        if(mails == null || mails.isEmpty()){
            return true;
        }

        final boolean disableEmailList
                = CacheUtils.getCacheOrganization(entity.getMails())
                .stream()
                .allMatch(OrganizationInfoEntity::getRemoveFlag);
        if (disableEmailList) {
            return true;
        }

        // メール内容設定エリア(件数確認)
        LinkedList<SummaryReportConfigElementEntity> entities = entity.getSummaryReportElementEntities();
        if(entities == null || entities.isEmpty()){
            return true;
        }
        
        return false;
    }
}
