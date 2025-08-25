/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.admanagerapp.workfloweditplugin.entity.ApprovalDialogEntity;
import adtekfuji.clientservice.ApprovalFlowInfoFacade;
import adtekfuji.clientservice.ApprovalRouteInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
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
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.approval.ApprovalInfoEntity;
import jp.adtekfuji.adFactory.entity.approval.ApprovalRouteInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalDataTypeEnum;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 申請ダイアログ
 *
 * @author akihiro-yoshida
 */
@FxComponent(id = "ApprovalDialog", fxmlPath = "/fxml/compo/approval_dialog.fxml")
public class ApprovalDialog implements Initializable, ArgumentDelivery, DialogHandler {

    /**
     * ログ出力クラス
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Sceneコンテナ
     */
    private final SceneContiner sc = SceneContiner.getInstance();

    /**
     * リソースバンドル
     */
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * URIの区切り文字
     */
    private final static String URI_SPLIT = "/";

    /**
     * 項目名の接尾辞
     */
    private static final String TITLE_NAME_SUFFIX = " : ";

    /**
     * 申請ダイアログ情報
     */
    private ApprovalDialogEntity approvalDialog;

    /**
     * ログインユーザ情報
     */
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();

    /**
     * 工程情報
     */
    private WorkInfoEntity work;

    /**
     * 工程順情報
     */
    private WorkflowInfoEntity workflow;

    /**
     * 申請情報
     */
    private ApprovalInfoEntity approval;

    /**
     * ダイアログ
     */
    private Dialog dialog;

    /**
     * スタックペイン
     */
    @FXML
    private StackPane pane;

    /**
     * 操作説明
     */
    @FXML
    private Label dataTypeExplanation;

    /**
     * 申請者名ラベル(項目名)
     */
    @FXML
    private Label requestorNameLabel;

    /**
     * 申請者名ラベル(値)
     */
    @FXML
    private Label requestorNameTitleLabel;

    /**
     * データ種別名ラベル(項目名)
     */
    @FXML
    private Label dataTypeNameTitleLabel;

    /**
     * データ種別名ラベル(値)
     */
    @FXML
    private Label dataTypeNameLabel;

    /**
     * リビジョンラベル(項目名)
     */
    @FXML
    private Label revisionTitleLabel;

    /**
     * リビジョンラベル(値)
     */
    @FXML
    private Label revisionLabel;

    /**
     * コメントテキストエリア(値)
     */
    @FXML
    private TextArea commentTextArea;

    /**
     * 承認ルートコンボボックス
     */
    @FXML
    private ComboBox<ApprovalRouteInfoEntity> approvalRouteComboBox;

    /**
     * 申請/申請取消ボタン
     */
    @FXML
    private Button approvalButton;

    /**
     * プログレスインジケータ配置先のPane
     */
    @FXML
    private Pane progress;

    /**
     * 承認ルートコンボボックス用セルファクトリー
     *
     */
    private class ApprovalRouteComboBoxCellFactory extends ListCell<ApprovalRouteInfoEntity> {

        /**
         * セルの内容を更新する。
         *
         * @param entity 更新エンティティ
         * @param isEmpty 空か
         */
        @Override
        protected void updateItem(ApprovalRouteInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(item.getRouteName());
            }
        }
    }

    /**
     * コンストラクタ
     */
    public ApprovalDialog() {
    }

    /**
     * 初期化
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.progress.setVisible(false);
        this.approvalButton.setDisable(true);

        Callback<ListView<ApprovalRouteInfoEntity>, ListCell<ApprovalRouteInfoEntity>> comboCellFactory = (ListView<ApprovalRouteInfoEntity> param) -> new ApprovalRouteComboBoxCellFactory();
        this.approvalRouteComboBox.setButtonCell(new ApprovalRouteComboBoxCellFactory());
        this.approvalRouteComboBox.setCellFactory(comboCellFactory);
        this.approvalRouteComboBox.setVisible(true);

        // 承認ルートコンボボックス選択値変更時の処理
        this.approvalRouteComboBox.valueProperty().addListener((ObservableValue<? extends ApprovalRouteInfoEntity> observable, ApprovalRouteInfoEntity oldValue, ApprovalRouteInfoEntity newValue) -> {
            if (Objects.nonNull(newValue)) {
                if (this.approvalDialog.getIsRequestTypeApproval()) {
                    this.approvalButton.setDisable(false);
                }
            } else {
                this.approvalButton.setDisable(true);
            }
        });
    }

    /**
     * 引数取得
     *
     * @param argument 引数
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof ApprovalDialogEntity) {
            this.approvalDialog = (ApprovalDialogEntity) argument;

            Platform.runLater(() -> {
                // 画面更新
                this.updateView();
            });
        }
    }

    /**
     * ダイアログ設定
     *
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        Stage stage = (Stage) this.dialog.getDialogPane().getScene().getWindow();
        stage.setMinWidth(700.0);
        stage.setMinHeight(430.0);
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
     * 画面更新処理
     */
    private void updateView() {
        logger.info("updateView start");
        boolean isCancel = false;

        try {
            blockUI(true);

            // 項目名ラベルを設定
            this.requestorNameTitleLabel.setText(LocaleUtils.getString("key.ApprovalRequestorName") + TITLE_NAME_SUFFIX);
            this.revisionTitleLabel.setText(LocaleUtils.getString("key.ApprovalRevision") + TITLE_NAME_SUFFIX);

            String dataTypeName;
            if (ApprovalDataTypeEnum.WORK.equals(this.approvalDialog.getApprovalDataType())) {
                // データ種別が工程
                this.work = this.approvalDialog.getWork();
                dataTypeName = LocaleUtils.getString("key.Process");
                this.dataTypeNameLabel.setText(this.work.getWorkName());
                this.revisionLabel.setText(String.valueOf(this.work.getWorkRev()));
                this.approval = work.getApproval();
            } else {
                // データ種別が工程以外
                this.workflow = this.approvalDialog.getWorkflow();
                dataTypeName = LocaleUtils.getString("key.OrderProcesses");
                this.dataTypeNameLabel.setText(this.workflow.getWorkflowName());
                this.revisionLabel.setText(String.valueOf(this.workflow.getWorkflowRev()));
                this.approval = workflow.getApproval();
            }
            this.dataTypeNameTitleLabel.setText(dataTypeName + TITLE_NAME_SUFFIX);

            // 申請者名の設定
            String organizationName = "";
            if (Objects.nonNull(this.approval)) {
                // 申請情報あり
                Long requestorId;
                if (this.approvalDialog.getIsRequestTypeApproval()) {
                    // 申請時
                    requestorId = this.loginUser.getId();
                } else {
                    // 申請取消時
                    requestorId = this.approval.getRequestorId();
                }
                OrganizationInfoEntity organization = CacheUtils.getCacheOrganization(requestorId);
                if (Objects.nonNull(organization)) {
                    organizationName = organization.getOrganizationName();
                }
                this.commentTextArea.setText(this.approval.getComment());
            } else {
                // 申請情報なし(初回申請の場合)
                organizationName = this.loginUser.getName();
                this.commentTextArea.setText("");
            }
            this.requestorNameLabel.setText(organizationName);

            // 承認ルート情報一覧を承認ルートコンボボックスに設定
            ApprovalRouteInfoFacade approvalRouteInfoFacade = new ApprovalRouteInfoFacade();
            List<ApprovalRouteInfoEntity> approvalRoutes = approvalRouteInfoFacade.findRange(null, null);
            if (!approvalRoutes.isEmpty()) {
                // 承認ルートコンボボックス設定
                this.approvalRouteComboBox.setItems(FXCollections.observableArrayList(approvalRoutes));
            }

            if (this.approvalDialog.getIsRequestTypeApproval()) {
                // 申請区分が申請
                this.dataTypeExplanation.setText(String.format(LocaleUtils.getString("key.ApprovalDataType"), dataTypeName));
                this.approvalButton.setText(LocaleUtils.getString("key.Approval"));
                this.approvalRouteComboBox.setDisable(false);
            } else {
                // 申請区分が申請取消
                this.dataTypeExplanation.setText(String.format(LocaleUtils.getString("key.ApprovalCancelDataType"), dataTypeName));
                this.approvalButton.setText(LocaleUtils.getString("key.ApprovalCancel"));
                this.approvalRouteComboBox.setDisable(true);
                this.approvalButton.setDisable(false);

                // 申請中の承認ルートを初期選択
                if (Objects.nonNull(this.approval)) {
                    Optional<ApprovalRouteInfoEntity> opt = approvalRoutes.stream()
                            .filter(p -> p.getRouteId() == this.approval.getRouteId())
                            .findFirst();
                    if (opt.isPresent()) {
                        this.approvalRouteComboBox.setValue(opt.get());
                    }
                }
            }

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
     * 申請/申請取消ボタン押下時のアクション
     *
     * @param event イベント
     */
    @FXML
    private void onApprovalButton(ActionEvent event) {
        this.doApprovalRequest();
    }

    /**
     * 申請/申請取消処理
     *
     * @return 処理結果(true：処理成功/false：処理失敗)
     */
    private boolean doApprovalRequest() {
        logger.info("approval start");
        try {
            this.blockUI(true);
            Task task = new Task<ResponseEntity>() {

                /**
                 * Taskが実行されるときに呼び出される。
                 *
                 * <pre>
                 * 申請/申請取消を処理する。
                 * </pre>
                 *
                 * @return バックグラウンド処理の結果(サーバからの応答)
                 * @throws Exception バックグラウンド操作中に発生した未処理の例外
                 */
                @Override
                protected ResponseEntity call() throws Exception {
                    ResponseEntity res = null;
                    ApprovalFlowInfoFacade approvalFlowInfoFacade = new ApprovalFlowInfoFacade();
                    if (approvalDialog.getIsRequestTypeApproval()) {
                        // 申請操作

                        // 申請情報の設定
                        if (Objects.isNull(approval)) {
                            approval = new ApprovalInfoEntity();
                        }

                        approval.setRouteId(approvalRouteComboBox.getValue().getRouteId());
                        approval.setRequestDatetime(new Date());
                        approval.setRequestorId(loginUser.getId());
                        approval.setDataType(approvalDialog.getApprovalDataType());
                        approval.setComment(commentTextArea.getText());
                        if (ApprovalDataTypeEnum.WORK.equals(approval.getDataType())) {
                            approval.setNewData(work.getWorkId());
                        } else {
                            approval.setNewData(workflow.getWorkflowId());
                        }

                        // 申請
                        res = approvalFlowInfoFacade.apply(approval, loginUser.getId());
                    } else {
                        // 申請取消操作

                        // 申請情報の設定
                        approval.setComment(commentTextArea.getText());

                        // 申請取消
                        res = approvalFlowInfoFacade.cancelApply(approval, loginUser.getId());
                    }

                    return res;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        ResponseEntity res = this.getValue();

                        if (approvalDialog.getIsRequestTypeApproval()) {
                            // 申請結果を表示する。
                            showApplyResultMessage(res);
                        } else {
                            // 申請取消結果を表示する。
                            showCancelResultMessage(res);
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        blockUI(false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    blockUI(false);
                }
            };
            new Thread(task).start();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        } finally {
            logger.info("approval end");
        }
        return true;
    }

    /**
     * 申請結果を表示する。
     *
     * @param res 申請結果
     */
    private void showApplyResultMessage(ResponseEntity res) {
        boolean isOk = false;
        try {
            Alert.AlertType alertType = Alert.AlertType.INFORMATION;
            String message = null;

            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                // 変更が申請されました。
                message = LocaleUtils.getString("key.ApprovalApplyComplete");
                isOk = true;
            } else {
                alertType = Alert.AlertType.ERROR;
                if (Objects.nonNull(res.getErrorType())) {
                    switch (res.getErrorType()) {
                        case REQUEST_LIMIT_EXCEEDED:
                            // 申請数の上限を超えたため、申請できませんでした。
                            message = LocaleUtils.getString("key.ApprovalApplyFailed");
                            break;
                        case SERVER_FETAL:
                            // 申請に失敗しました。
                            message = String.format(LocaleUtils.getString("key.FaildToApprove"), LocaleUtils.getString("key.Approval"));
                            break;
                        case MAIL_AUTHENTICATION_FAILED:
                            // 変更申請メールの送信に失敗しました。
                            message = LocaleUtils.getString("key.ApprovalApplyNotificationFailed");
                            isOk = true;
                            break;
                        case RELATED_WORKFLOW_APPLYING:
                            // 対象の工程を使用している工程順が申請中のため、申請できませんでした。
                            message = LocaleUtils.getString("key.alert.relatedWorkflowApplying");
                            break;
                        case RELATED_WORK_APPLYING:
                            // 対象の工程順で使用している工程が申請中のため、申請できませんでした。
                            message = LocaleUtils.getString("key.alert.relatedWorkApplying");
                            break;
                    }
                }
            }

            if (!StringUtils.isEmpty(message)) {
                sc.showAlert(alertType, LocaleUtils.getString("key.Approval"), message);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
            if (isOk) {
                dialog.setResult(ButtonType.OK);
                dialog.close();
            }
        }
    }

    /**
     * 申請取消結果を表示する。
     *
     * @param res 申請取消結果
     */
    private void showCancelResultMessage(ResponseEntity res) {
        boolean isOk = false;
        try {
            Alert.AlertType alertType = Alert.AlertType.INFORMATION;
            String message = null;

            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                // 変更申請を取り消しました。
                message = LocaleUtils.getString("key.ApprovalCancelComplete");
                isOk = true;
            } else {
                alertType = Alert.AlertType.ERROR;
                if (Objects.nonNull(res.getErrorType())) {
                    switch (res.getErrorType()) {
                        case SERVER_FETAL:
                            // 申請取消に失敗しました。
                            message = String.format(LocaleUtils.getString("key.FaildToApprove"), LocaleUtils.getString("key.ApprovalCancel"));
                            break;
                        case MAIL_AUTHENTICATION_FAILED:
                            // 変更申請取消メールの送信に失敗しました。
                            message = LocaleUtils.getString("key.ApprovalApplyCancelNotificationFailed");
                            isOk = true;
                            break;
                    }
                }
            }

            if (!StringUtils.isEmpty(message)) {
                sc.showAlert(alertType, LocaleUtils.getString("key.ApprovalCancel"), message);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
            if (isOk) {
                dialog.setResult(ButtonType.OK);
                dialog.close();
            }
        }
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

        if (updateFlg) {
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            if (ButtonType.YES == buttonType) {
                return this.doApprovalRequest();
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
    private void blockUI(boolean block) {
        this.pane.setDisable(block);
        this.progress.setVisible(block);
    }
}
