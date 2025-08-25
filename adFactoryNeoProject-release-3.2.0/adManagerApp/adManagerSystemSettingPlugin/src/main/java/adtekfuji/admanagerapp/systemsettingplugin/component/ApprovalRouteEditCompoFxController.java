/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.admanagerapp.systemsettingplugin.common.ApprovalButtonTableCell;
import adtekfuji.admanagerapp.systemsettingplugin.common.ApprovalTableRow;
import adtekfuji.admanagerapp.systemsettingplugin.entity.ApprovalTableData;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.ApprovalRouteInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.approval.ApprovalOrderInfoEntity;
import jp.adtekfuji.adFactory.entity.approval.ApprovalRouteInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 承認ルート一覧編集画面のコントローラ
 *
 * @author shizuka.hirano
 */
@FxComponent(id = "ApprovalRouteEditCompo",
        fxmlPath = "/fxml/admanagersystemsettingplugin/approval_route_edit_compo.fxml")
public class ApprovalRouteEditCompoFxController implements Initializable, ComponentHandler {

    /**
     * ログ出力クラス
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * リソースバンドル
     */
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * 承認ルート取得用RESTクラス
     */
    private final static ApprovalRouteInfoFacade approvalRouteInfoFacade = new ApprovalRouteInfoFacade();

    /**
     * Sceneコンテナ
     */
    private final SceneContiner sc = SceneContiner.getInstance();

    /**
     * キャッシュ管理クラス
     */
    CashManager cache = CashManager.getInstance();

    /**
     * URIの区切り文字
     */
    private final static String URI_SPLIT = "/";

    /**
     * 変更保存時データベースからの読み込みを待機させるラッチ
     */
    private CountDownLatch latch;

    /**
     * 承認者一覧のテーブルデータ
     */
    private final ObservableList<ApprovalTableData> tableData = FXCollections.observableArrayList();

    /**
     * 承認者一覧のテーブルデータ(変更前)
     */
    private final ObservableList<ApprovalTableData> tableDataCache = FXCollections.observableArrayList();

    /**
     * 承認者一覧
     */
    private List<OrganizationInfoEntity> selectedApprovers = new ArrayList<>();

    /**
     * 選択中の承認ルート情報
     */
    private ApprovalRouteInfoEntity selectedApprovalRoute;

    /**
     * 登録成功判定
     */
    private boolean isRegisterSucceed = true;

    /**
     * スプリットペイン
     */
    @FXML
    private SplitPane approvalRoutePane;

    /**
     * 承認ルート一覧
     */
    @FXML
    private ListView<ApprovalRouteInfoEntity> approvalRouteList;

    /**
     * 削除ボタン
     */
    @FXML
    private Button deleteListButton;

    /**
     * コピーボタン
     */
    @FXML
    private Button copyListButton;

    /**
     * 編集ボタン
     */
    @FXML
    private Button editListButton;

    /**
     * 新規作成ボタン
     */
    @FXML
    private Button createListButton;

    /**
     * 承認順一覧
     */
    @FXML
    private PropertySaveTableView<ApprovalTableData> approvalList;

    /**
     * 承認順
     */
    @FXML
    private TableColumn<ApprovalTableData, String> approvalOrderNameColumn;

    /**
     * 組織識別名
     */
    @FXML
    private TableColumn<ApprovalTableData, String> approverIdentifyColumn;

    /**
     * 組織名
     */
    @FXML
    private TableColumn<ApprovalTableData, String> approverNameColumn;

    /**
     * メールアドレス
     */
    @FXML
    private TableColumn<ApprovalTableData, String> mailAddressColumn;

    /**
     * ▲ボタン
     */
    @FXML
    private TableColumn<ApprovalTableData, Button> upButtonColummn;

    /**
     * ▼ボタン
     */
    @FXML
    private TableColumn<ApprovalTableData, Button> downButtonColumn;

    /**
     * 選択ボタン
     */
    @FXML
    private Button selectApprovalButton;

    /**
     * 登録ボタン
     */
    @FXML
    private Button registApprovalButton;

    /**
     * プログレスインジケータ配置先のPane
     */
    @FXML
    private Pane Progress;

    /**
     * コンストラクタ
     */
    public ApprovalRouteEditCompoFxController() {
    }

    /**
     * 承認ルート一覧リストのセルファクトリー
     */
    class ApprovalRouteTypeComboBoxCellFactory extends ListCell<ApprovalRouteInfoEntity> {

        /**
         * セルの内容を更新する。
         *
         * @param item 更新エンティティ
         * @param empty 空か
         */
        @Override
        protected void updateItem(ApprovalRouteInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getRouteName()));
            }
        }
    }

    /**
     * 承認ルート一覧リストカラムの設定
     */
    Callback<ListView<ApprovalRouteInfoEntity>, ListCell<ApprovalRouteInfoEntity>> comboCellFactory = (ListView<ApprovalRouteInfoEntity> param) -> new ApprovalRouteTypeComboBoxCellFactory();

    /**
     * 初期化
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        approvalList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        SplitPaneUtils.loadDividerPosition(approvalRoutePane, getClass().getSimpleName());

        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            //役割の権限によるボタン無効化.
            deleteListButton.setDisable(true);
            copyListButton.setDisable(true);
            editListButton.setDisable(true);
            createListButton.setDisable(true);
            selectApprovalButton.setDisable(true);
            registApprovalButton.setDisable(true);
            approvalList.setEditable(false);
        }

        approvalList.init("approvalList");

        approvalList.setRowFactory((TableView<ApprovalTableData> r) -> new ApprovalTableRow());
        approvalList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        approvalList.getSelectionModel().setCellSelectionEnabled(false);

        // 承認ルート一覧のセルを生成する
        approvalRouteList.setCellFactory((ListView<ApprovalRouteInfoEntity> param) -> {
            ListCell<ApprovalRouteInfoEntity> cell = new ListCell<ApprovalRouteInfoEntity>() {
                /**
                 * セルの内容を更新する。
                 *
                 * @param e 更新エンティティ
                 * @param empty 空か
                 */
                @Override
                protected void updateItem(ApprovalRouteInfoEntity e, boolean empty) {
                    try {
                        super.updateItem(e, empty);
                        if (!empty) {
                            this.setText(e.getRouteName());
                        } else {
                            this.setText("");
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }
            };
            return cell;
        });

        // 承認ルートリストの選択行変更時の処理
        approvalRouteList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ApprovalRouteInfoEntity> observable, ApprovalRouteInfoEntity oldValue, ApprovalRouteInfoEntity newValue) -> {
            try {
                if (Objects.nonNull(newValue) && oldValue != newValue) {
                    logger.info("select:{}", newValue);

                    this.registConfirm(false);

                    //変更を保存中のとき終わるまで待機する
                    if (Objects.nonNull(latch)) {
                        try {
                            latch.await();
                        } catch (Exception e) {
                            logger.fatal(e, e);
                        }
                    }

                    selectedApprovalRoute = newValue;
                    Platform.runLater(() -> {
                        updateDetailView(selectedApprovalRoute, true);
                    });
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });

        //承認ルート行ダブルクリック時処理
        approvalRouteList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                this.onListEdit(new ActionEvent());
            }
        });

        // 承認者 列
        approvalOrderNameColumn.setCellValueFactory((
                TableColumn.CellDataFeatures<ApprovalTableData, String> param)
                -> param.getValue().approvalOrderNameProperty());
        approvalOrderNameColumn.setSortable(false);

        // 組織識別名 列
        approverIdentifyColumn.setCellValueFactory((
                TableColumn.CellDataFeatures<ApprovalTableData, String> param)
                -> param.getValue().organizationIdentifyProperty());
        approverIdentifyColumn.setSortable(false);

        // 組織名 列
        approverNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ApprovalTableData, String> param)
                -> param.getValue().organizationNameProperty());
        approverNameColumn.setSortable(false);

        // メールアドレス 列
        mailAddressColumn.setCellValueFactory((TableColumn.CellDataFeatures<ApprovalTableData, String> param)
                -> param.getValue().mailAddressProperty());
        mailAddressColumn.setSortable(false);

        // ▲ボタン　列
        upButtonColummn.setCellFactory(ApprovalButtonTableCell.<ApprovalTableData>forTableColumn("▲", (ApprovalTableData approval) -> {
            upButtonAction(approval);
            return approval;
        }));
        upButtonColummn.setSortable(false);

        // ▼ボタン　列
        downButtonColumn.setCellFactory(ApprovalButtonTableCell.<ApprovalTableData>forTableColumn("▼", (ApprovalTableData approval) -> {
            downButtonAction(approval);
            return approval;
        }));
        downButtonColumn.setSortable(false);

        // キャッシュに組織情報を読み込む。(未キャッシュの場合のみ)
        CacheUtils.createCacheOrganization(true);

        updateApprovalRouteList(null);
    }

    /**
     * 承認ルート 新規作成
     *
     * @param event 新規作成ボタン押下
     */
    @FXML
    private void onListCreate(ActionEvent event) {
        try {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registApprovalOrder()) {
                        return;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }

            ApprovalRouteInfoEntity approvalRoute = new ApprovalRouteInfoEntity();

            if (this.DispApprovalRouteDialog(approvalRoute)) {
                //承認ルートを追加
                ResponseEntity res = approvalRouteInfoFacade.add(approvalRoute);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    //リスト更新
                    updateApprovalRouteList(getUriToRegistedItemId(res.getUri()));
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ApprovalRouteEdit"),
                            String.format(LocaleUtils.getString("key.FailedToCreate"), LocaleUtils.getString("key.ApprovalRoute")));
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 承認ルート コピー
     *
     * @param event コピーボタン押下
     */
    @FXML
    private void onListCopy(ActionEvent event) {
        try {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registApprovalOrder()) {
                        return;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }

            ApprovalRouteInfoEntity item = approvalRouteList.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getRouteId())) {
                return;
            }

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION,
                    LocaleUtils.getString("key.Copy"), LocaleUtils.getString("key.CopyMessage"), item.getRouteName());
            if (ret.equals(ButtonType.CANCEL)) {
                return;
            }

            // 承認ルートをコピー
            ResponseEntity res = approvalRouteInfoFacade.copy(item);
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                //リスト更新
                updateApprovalRouteList(getUriToRegistedItemId(res.getUri()));
            } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ApprovalRouteEdit"),
                        String.format(LocaleUtils.getString("key.FailedToCreate"), LocaleUtils.getString("key.ApprovalRoute")));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 承認ルート 編集
     *
     * @param event 編集ボタン押下
     */
    @FXML
    private void onListEdit(ActionEvent event) {
        try {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registApprovalOrder()) {
                        return;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }

            ApprovalRouteInfoEntity item = approvalRouteList.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getRouteId())) {
                return;
            }

            if (this.DispApprovalRouteDialog(item)) {
                //承認ルートを更新
                ResponseEntity res = approvalRouteInfoFacade.update(item);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    //リスト更新
                    updateApprovalRouteList(item.getRouteId());
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ApprovalRouteEdit"),
                            String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.ApprovalRoute")));
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 承認ルート 削除
     *
     * @param event 削除ボタン押下
     */
    @FXML
    private void onListDelete(ActionEvent event) {
        try {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registApprovalOrder()) {
                        return;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }

            ApprovalRouteInfoEntity item = approvalRouteList.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getRouteId())) {
                return;
            }
            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION,
                    LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getRouteName());
            if (ret.equals(ButtonType.CANCEL)) {
                return;
            }

            // 承認ルートを削除
            ResponseEntity res = approvalRouteInfoFacade.remove(item.getRouteId());
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                //リスト更新
                int selectedIndex = approvalRouteList.getSelectionModel().getSelectedIndex();
                ApprovalRouteInfoEntity targetItem;
                if (selectedIndex == 0) {
                    targetItem = approvalRouteList.getItems().get(selectedIndex + 1);
                } else {
                    targetItem = approvalRouteList.getItems().get(selectedIndex - 1);
                }
                updateApprovalRouteList(targetItem.getRouteId());
            } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ApprovalRouteEdit"),
                        String.format(LocaleUtils.getString("key.FailedToDelete"), LocaleUtils.getString("key.ApprovalRoute")));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 承認ルート 選択
     *
     * @param event 選択ボタン押下
     */
    @FXML
    private void onOrganizationSelect(ActionEvent event) {
        try {
            ApprovalRouteInfoEntity item = approvalRouteList.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getRouteId())) {
                return;
            }

            SelectDialogEntity<OrganizationInfoEntity> selectDialog = new SelectDialogEntity();
            selectDialog.organizations(selectedApprovers);
            selectDialog.setApprovalAuthorityOnly(true);

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialog);
            if (ButtonType.OK.equals(ret)) {
                // 選択された組織の情報を、承認者一覧にセットする。
                this.selectedApprovers = selectDialog.getOrganizations();
                updateDetailView(item, false);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 承認ルート編集ダイアログを表示する。
     *
     * @param approvalRoute 承認ルート情報
     * @return OKボタンを押し、かつ、テキストが変更された場合はtrue、それ以外はfalse
     */
    private boolean DispApprovalRouteDialog(ApprovalRouteInfoEntity approvalRoute) {
        try {
            String orgName = approvalRoute.getRouteName();
            String title;
            if (Objects.isNull(orgName)) {
                orgName = "";
                title = LocaleUtils.getString("key.createNew");
            } else {
                title = LocaleUtils.getString("key.Edit");
            }
            String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.ApprovalRouteName"));
            String newName = sc.showTextInputDialog(
                    title, message, LocaleUtils.getString("key.ApprovalRouteName"), orgName);

            if (Objects.nonNull(newName)) {
                newName = adtekfuji.utility.StringUtils.trim2(newName);
                if (newName.isEmpty()) {
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                } else if (!Objects.equals(newName, orgName)) {
                    approvalRoute.setRouteName(newName);
                    return true;
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return false;
    }

    /**
     * 承認ルート 登録
     *
     * @param event 登録ボタン押下
     */
    @FXML
    private void onRegistApproval(ActionEvent event) {
        registApprovalOrder();
    }

    /**
     * 承認ルートの登録・更新・削除
     *
     * @return 登録・更新・削除が成功した場合はtrue、それ以外はfalse
     */
    private boolean registApprovalOrder() {
        logger.info("registApprovalOrder start");
        isRegisterSucceed = true;
        try {
            blockUI(true);
            this.latch = new CountDownLatch(1);
            Task task = new Task<ResponseEntity>() {
                /**
                 * Taskが実行されるときに呼び出される。
                 *
                 * <pre>
                 * 承認ルートを更新する。
                 * </pre>
                 *
                 * @return バックグラウンド処理の結果(サーバからの応答)
                 * @throws Exception バックグラウンド操作中に発生した未処理の例外
                 */
                @Override
                protected ResponseEntity call() throws Exception {
                    ResponseEntity res = new ResponseEntity();
                    try {
                        if (isChanged()) {
                            // 更新対象データ設定
                            List<ApprovalOrderInfoEntity> approvalOrders = new ArrayList<>();
                            tableData.stream().forEach(row -> {
                                ApprovalOrderInfoEntity approvalOrder = new ApprovalOrderInfoEntity();
                                approvalOrder.setRouteId(row.getRouteId());
                                approvalOrder.setOrganizationId(row.getOrganizationId());
                                approvalOrder.setApprovalOrder(row.getApprovalOrder());
                                approvalOrder.setApprovalFinal(row.getApprovalFinal());
                                approvalOrders.add(approvalOrder);
                            });
                            selectedApprovalRoute.setApprovalOrders(approvalOrders);

                            // 承認ルート更新
                            res = approvalRouteInfoFacade.update(selectedApprovalRoute);
                        } else {
                            res = ResponseEntity.success();
                        }
                    } finally {
                        latch.countDown();
                    }
                    return res;
                }

                /**
                 * Taskの状態がSUCCEEDED状態に遷移するたびに呼び出される。
                 *
                 * <pre>
                 * UIロックを解除し、画面表示を更新する。
                 * </pre>
                 */
                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        ResponseEntity res = this.getValue();
                        if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                            ApprovalRouteInfoEntity newApprovalRoute = approvalRouteInfoFacade.find(selectedApprovalRoute.getRouteId());
                            updateDetailView(newApprovalRoute, true);
                            // リストを更新する。
                            approvalRouteList.refresh();
                        } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ApprovalRouteEdit"),
                                    String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.Authorizer")));
                            isRegisterSucceed = false;
                        } else {
                            isRegisterSucceed = false;
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        isRegisterSucceed = false;
                    } finally {
                        blockUI(false);
                        logger.info("updateApprovalRouteList end.");
                    }
                }

                /**
                 * Taskの状態がFAILED状態に遷移するたびに呼び出される。
                 *
                 * <pre>
                 * UIロックを解除し、画面表示を更新する。
                 * </pre>
                 */
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
                        blockUI(false);
                        isRegisterSucceed = false;
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
            isRegisterSucceed = false;
        }
        return isRegisterSucceed;
    }

    /**
     * 変更を確認して保存する
     *
     * @param 保存確認ダイアログにキャンセルボタンを表示する場合はtrue、それ以外はfalse
     * @return キャンセルを押した、または保存に失敗した場合false
     */
    private boolean registConfirm(boolean isDispCancel) {
        String title = null;
        String message = null;
        if (isChanged()) {
            // 入力内容が保存されていません。保存しますか?
            title = LocaleUtils.getString("key.confirm");
            message = LocaleUtils.getString("key.confirm.destroy");
        }

        if (Objects.nonNull(message)) {
            ButtonType buttonType;
            if (isDispCancel) {
                buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            } else {
                buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
            }

            if (ButtonType.YES == buttonType) {
                return registApprovalOrder();
            } else if (ButtonType.CANCEL == buttonType) {
                return false;
            }
        }
        return true;
    }

    /**
     * 承認ルートリストを更新する。
     *
     * @param 承認ルートリストで初期選択する承認ルートのルートID。nullの場合は初期選択なし
     */
    private void updateApprovalRouteList(Long routeId) {
        logger.info("updateApprovalRouteList start.");
        Object obj = new Object();
        try {
            blockUI(true);

            Task task = new Task<List<ApprovalRouteInfoEntity>>() {
                /**
                 * Taskが実行されるときに呼び出される。
                 *
                 * <pre>
                 * 承認ルート一覧を取得する。
                 * </pre>
                 *
                 * @return バックグラウンド処理の結果(サーバからの応答)
                 * @throws Exception バックグラウンド操作中に発生した未処理の例外
                 */
                @Override
                protected List<ApprovalRouteInfoEntity> call() throws Exception {
                    // 承認ルート一覧取得
                    List<ApprovalRouteInfoEntity> approvalRoutes = approvalRouteInfoFacade.findRange(null, null).stream()
                            .sorted(Comparator.comparing(ApprovalRouteInfoEntity::getRouteName))
                            .collect(Collectors.toList());
                    return approvalRoutes;
                }

                /**
                 * Taskの状態がSUCCEEDED状態に遷移するたびに呼び出される。
                 *
                 * <pre>
                 * UIロックを解除し、画面表示を更新する。
                 * </pre>
                 */
                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // リストを更新する。
                        approvalRouteList.getItems().clear();
                        approvalRouteList.getItems().addAll(this.get());
                        if (Objects.nonNull(routeId)) {
                            Optional<ApprovalRouteInfoEntity> opt = this.get().stream().filter(p -> p.getRouteId().equals(routeId)).findFirst();
                            if (opt.isPresent()) {
                                approvalRouteList.getSelectionModel().select(opt.get());
                            }
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                        logger.info("updateApprovalRouteList end.");
                    }
                }

                /**
                 * Taskの状態がFAILED状態に遷移するたびに呼び出される。
                 *
                 * <pre>
                 * UIロックを解除し、画面表示を更新する。
                 * </pre>
                 */
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
     * 詳細画面更新処理
     *
     * @param approvalRoute 承認ルート
     * @param isInitialize 初期化(承認ルートリスト行選択)時はtrue、再描画時はfalse
     */
    private void updateDetailView(ApprovalRouteInfoEntity approvalRoute, Boolean isInitialize) {
        if (Objects.isNull(approvalRoute)) {
            return;
        }

        if (approvalRoute.getRouteId() == 0L) {
            return;
        }

        if (isInitialize) {
            if (Objects.nonNull(approvalRoute.getApprovalOrders())) {
                List<Long> organizationIds = new ArrayList<>();
                approvalRoute.getApprovalOrders().sort(Comparator.comparing(o -> o.getApprovalOrder()));
                approvalRoute.getApprovalOrders().forEach(o -> organizationIds.add(o.getOrganizationId()));

                // 選択済みの承認者一覧を更新
                this.selectedApprovers.clear();
                for (Long organizationId : organizationIds) {
                    Optional<OrganizationInfoEntity> opt = ((List<OrganizationInfoEntity>) cache.getItemList(OrganizationInfoEntity.class, new ArrayList<>()))
                            .stream().filter(o -> organizationId.equals(o.getOrganizationId()))
                            .findFirst();
                    if (opt.isPresent()) {
                        this.selectedApprovers.add(opt.get());
                    }
                }
            } else {
                this.selectedApprovers = new ArrayList<>();
            }
        }

        reRenderDetailView(isInitialize);
    }

    /**
     * 詳細画面再描画処理
     *
     * @param isInitialize 初期化(承認ルートリスト行選択)時はtrue、再描画時はfalse
     */
    private void reRenderDetailView(Boolean isInitialize) {
        ApprovalRouteInfoEntity item = approvalRouteList.getSelectionModel().getSelectedItem();
        if (Objects.isNull(item)) {
            return;
        }

        this.tableData.clear();
        approvalList.getSelectionModel().clearSelection();

        int index = 1;
        for (OrganizationInfoEntity info : selectedApprovers) {
            this.tableData.add(new ApprovalTableData(info, item.getRouteId(), index, this.selectedApprovers.size()));
            index++;
        }

        if (isInitialize) {
            tableDataCache.clear();
            tableData.stream().forEach(p -> tableDataCache.add(p.clone()));
        }

        List<ApprovalTableData> filteredData = this.tableData.filtered(o -> !o.getOrganizationIdentify().isEmpty());
        ObservableList<ApprovalTableData> dispData = FXCollections.observableArrayList(filteredData);
        dispData.sort(Comparator.comparing(info -> info.getApprovalOrder()));

        this.approvalList.setItems(dispData);
    }

    /**
     * UIロック
     *
     * @param flg ロックする場合はtrue、ロック解除する場合はfalse
     */
    private void blockUI(boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        Progress.setVisible(flg);
    }

    /**
     * コンポーネントが破棄される前に呼び出される。
     *
     * @return 変更内容がないか、変更内容があり保存成功した場合はtrue、保存失敗または保存しなかった場合はfalse
     */
    @Override
    public boolean destoryComponent() {
        try {
            logger.info("destoryComponent start.");

            SplitPaneUtils.saveDividerPosition(approvalRoutePane, getClass().getSimpleName());

            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registApprovalOrder()) {
                        return false;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return false;
                }
            }

            return true;
        } finally {
            logger.info("destoryComponent end.");
        }
    }

    /**
     * 内容が変更されたかどうかを返す。
     *
     * @return 内容が変更された場合はtrue、それ以外はfalse
     */
    private boolean isChanged() {
        boolean isChanged = false;

        if (tableData.size() != tableDataCache.size()) {
            isChanged = true;
        } else {
            for (int index = 0; index < tableData.size(); index++) {
                ApprovalTableData cacheOrder = tableDataCache.get(index);
                ApprovalTableData operateOrder = tableData.get(index);
                if (!cacheOrder.equals(operateOrder)) {
                    isChanged = true;
                    break;
                }
            }
        }

        return isChanged;
    }

    /**
     * 登録結果URIから登録したデータのIDを取得する
     *
     * @param uri 登録結果URI
     * @return 登録したデータのID
     */
    public static long getUriToRegistedItemId(String uri) {
        long ret = 0;
        try {
            String[] split = uri.split(URI_SPLIT);
            if (split.length == 2) {
                ret = Long.parseLong(split[1]);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * 表の▲ボタン押下時の処理
     *
     * @param clickedRowData クリックされた行データ
     */
    private void upButtonAction(ApprovalTableData clickedRowData) {
        logger.info("push up button.");
        // クリック行の順序を一つ上げる
        int clickedIndex = this.approvalList.getItems().indexOf(clickedRowData);
        if (clickedIndex > 0) {
            ApprovalTableData upRowData = this.tableData.get(clickedIndex);
            ApprovalTableData downRowData = this.tableData.get(clickedIndex - 1);
            OrganizationInfoEntity upOrganizationData = this.selectedApprovers.get(clickedIndex);
            OrganizationInfoEntity downOrganizationData = this.selectedApprovers.get(clickedIndex - 1);

            // 承認順名を入れ替え
            upRowData.setApprovalOrder(clickedIndex);
            upRowData.setApprovalOrderName(LocaleUtils.getString("key.Authorizer") + clickedIndex);
            downRowData.setApprovalOrder(clickedIndex + 1);
            if (clickedIndex == this.approvalList.getItems().size() - 1) {
                upRowData.setApprovalFinal(false);
                downRowData.setApprovalFinal(true);
                downRowData.setApprovalOrderName(LocaleUtils.getString("key.FinalApprover"));
            } else {
                upRowData.setApprovalFinal(false);
                downRowData.setApprovalFinal(false);
                downRowData.setApprovalOrderName(LocaleUtils.getString("key.Authorizer") + (clickedIndex + 1));
            }

            // 承認者テーブルデータ、選択された承認者一覧データの順番を入れ替え
            this.approvalList.getItems().remove(clickedIndex);
            this.approvalList.getItems().add(clickedIndex - 1, upRowData);
            this.selectedApprovers.remove(clickedIndex);
            this.selectedApprovers.add(clickedIndex - 1, upOrganizationData);

            this.approvalList.getItems().remove(clickedIndex);
            this.approvalList.getItems().add(clickedIndex, downRowData);
            this.selectedApprovers.remove(clickedIndex);
            this.selectedApprovers.add(clickedIndex, downOrganizationData);

            this.tableData.clear();
            this.tableData.addAll(this.approvalList.getItems());

            this.approvalList.getSelectionModel().select(upRowData);
        }
    }

    /**
     * 表の▼ボタン押下時の処理
     *
     * @param clickedRowData クリックされた行データ
     */
    private void downButtonAction(ApprovalTableData clickedRowData) {
        logger.info("push down button.");
        // クリック行の順序を一つ下げる
        int clickedIndex = this.approvalList.getItems().indexOf(clickedRowData);
        if (clickedIndex < this.approvalList.getItems().size() - 1) {
            ApprovalTableData downRowData = this.tableData.get(clickedIndex);
            ApprovalTableData upRowData = this.tableData.get(clickedIndex + 1);
            OrganizationInfoEntity downOrganizationData = this.selectedApprovers.get(clickedIndex);
            OrganizationInfoEntity upOrganizationData = this.selectedApprovers.get(clickedIndex + 1);

            // 承認順名を入れ替え
            downRowData.setApprovalOrder(clickedIndex + 2);
            if (clickedIndex == this.approvalList.getItems().size() - 2) {
                downRowData.setApprovalOrderName(LocaleUtils.getString("key.FinalApprover"));
                downRowData.setApprovalFinal(true);
                upRowData.setApprovalFinal(false);
            } else {
                downRowData.setApprovalOrderName(LocaleUtils.getString("key.Authorizer") + (clickedIndex + 2));
                downRowData.setApprovalFinal(false);
                upRowData.setApprovalFinal(false);
            }
            upRowData.setApprovalOrder(clickedIndex + 1);
            upRowData.setApprovalOrderName(LocaleUtils.getString("key.Authorizer") + (clickedIndex + 1));

            // 承認者テーブルデータ、選択された承認者一覧データの順番を入れ替え
            this.approvalList.getItems().remove(clickedIndex);
            this.approvalList.getItems().add(clickedIndex, upRowData);
            this.selectedApprovers.remove(clickedIndex);
            this.selectedApprovers.add(clickedIndex, upOrganizationData);

            this.approvalList.getItems().remove(clickedIndex);
            this.approvalList.getItems().add(clickedIndex + 1, downRowData);
            this.selectedApprovers.remove(clickedIndex);
            this.selectedApprovers.add(clickedIndex + 1, downOrganizationData);

            this.tableData.clear();
            this.tableData.addAll(this.approvalList.getItems());

            this.approvalList.getSelectionModel().select(downRowData);
        }
    }
}
