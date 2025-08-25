/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.admanagerapp.workfloweditplugin.common.Constants;
import static adtekfuji.admanagerapp.workfloweditplugin.common.Constants.*;
import adtekfuji.admanagerapp.workfloweditplugin.common.PropertyTemplateLoader;
import adtekfuji.admanagerapp.workfloweditplugin.common.SelectedWorkAndHierarchy;
import adtekfuji.admanagerapp.workfloweditplugin.common.TraceabilityRecordFactory;
import adtekfuji.admanagerapp.workfloweditplugin.common.WorkPropertyRecordFactory;
import adtekfuji.admanagerapp.workfloweditplugin.common.WorkflowEditConfig;
import adtekfuji.admanagerapp.workfloweditplugin.entity.ApprovalDialogEntity;
import adtekfuji.admanagerapp.workfloweditplugin.net.HttpStorage;
import adtekfuji.admanagerapp.workfloweditplugin.net.RemoteStorage;
import adtekfuji.admanagerapp.workfloweditplugin.net.UITask;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
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
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.DetailRecordFactory;
import jp.adtekfuji.javafxcommon.PropertyBindEntity;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程設定画面のコントローラー
 *
 * @author ta.ito
 */
@FxComponent(id = "WorkDetailCompo", fxmlPath = "/fxml/compo/work_detail_compo.fxml")
public class WorkDetailCompoFxController implements Initializable, ArgumentDelivery, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final WorkInfoFacade workInfoFacade = new WorkInfoFacade();
    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final ObjectInfoFacade objectInfoFacede = new ObjectInfoFacade();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final Map<String, PropertyBindEntity> detailProperties = new LinkedHashMap<>();
    private final LinkedList<WorkPropertyInfoEntity> properties = new LinkedList<>();
    private final LinkedList<WorkPropertyInfoEntity> traceabilityProperties = new LinkedList<>();
    private SelectedWorkAndHierarchy selectedWorkAndHierarchy = null;
    private final ObservableList<ObjectInfoEntity> useParts = FXCollections.observableArrayList();
    private final List<EquipmentInfoEntity> manufactureEquipments = new ArrayList<>();
    private final List<EquipmentInfoEntity> measureEquipments = new ArrayList<>();
    private final Map<String, String> templateRegexTexts = new HashMap<>();

    private final long maxRest = ClientServiceProperty.getRestRangeNum();
    private Integer defaultTaktTime;

    private WorkInfoEntity workInfo;
    private WorkInfoEntity cloneWorkInfo;
    private WorkSectionInfoEntity workSection = null;
    private WorkSectionPane workSectionPane = null;
    private boolean downloading;
    private Tooltip tooltip;

    /**
     * 承認機能オプションが有効か
     */
    private final boolean isLicensedApproval = ClientServiceProperty.isLicensed(LicenseOptionType.ApprovalOption.getName());

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
    private Table detailTable;

    /**
     * 各種入力項目の有効/無効状態
     */
    private boolean disableOtherInputItems = true;

    @FXML
    private Button registButton;
    @FXML
    private Button applyButton;
    @FXML
    private Label parentNameLabel;
    @FXML
    private VBox topSidePane;
    @FXML
    private HBox usePartsPane;
    @FXML
    private TextArea usePartsTextArea;
    @FXML
    private VBox bottomSidePane;
    @FXML
    private VBox docPane;

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

    private TextArea contentTextArea;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //役割の権限によるボタン無効化.
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
            registButton.setDisable(true);
            applyButton.setDisable(true);
        }
    }

    /**
     * 工程の変更内容を適用する
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

    @FXML
    private void onCancel(ActionEvent event) {
        logger.info("onCancel:start");
        if (destoryComponent()) {
            cloneWorkInfo = updateData().clone();
            sc.setComponent("ContentNaviPane", "WorkEditCompo");
        }
    }

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

                this.cloneWorkInfo = this.workInfo.clone();
                    
                // 画面を再描画
                updateView();
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

                this.cloneWorkInfo = this.workInfo.clone();
                    
                // 画面を再描画
                updateView();
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
            //未入力判定
            if (checkEmpty()) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return false;
            }

            //フォーマット確認
            if (!isValidItems()) {
                return false;
            }

            // ドキュメントがダウンロードされていない場合、タブが移動された時の再アップロードに備えるため、ダウンロードしておく
            if (this.workSection.hasDocument() && !this.workSectionPane.isLoaded()) {
                workSectionPane.download();
            }

            //工程の更新
            this.updateData();

            // 工程を追加
            String workId;
            if (Objects.isNull(this.workInfo.getWorkId()) || this.workInfo.getWorkId() == 0) {
                ResponseEntity res = this.workInfoFacade.registWork(this.workInfo);
                if (Objects.nonNull(res) && !ResponseAnalyzer.getAnalyzeResult(res)) {
                    return false;
                }

                workId = res.getUri().substring(res.getUri().lastIndexOf("/") + 1);

                // 工程を再取得する。
                this.workInfo = workInfoFacade.find(Long.parseLong(workId), false, true);
            } else {
                workId = String.valueOf(this.workInfo.getWorkId());
            }

            // アップロードするファイルを抽出
            Map<String, String> transfers = new HashMap<>();
            Set<String> deletes = new HashSet<>();

            if (this.workSection.isChenged() && this.workSection.hasDocument()) {
                transfers.put(workSection.getPhysicalName(), this.workSection.getSourcePath());
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
                    stage.show();

                    // ドキュメントをアップロード
                    URL url = new URL(ClientServiceProperty.getServerUri());
                    RemoteStorage storage = new HttpStorage();
                    storage.configuration(url.getHost(), null, null);

                    Task<Boolean> task = storage.newUploader(Paths.SERVER_UPLOAD_PDOC + "/" + String.valueOf(workId), transfers, deletes);
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

                            this.cloneWorkInfo = this.workInfo.clone();

                            if (closing) {
                                this.downloading = false;
                                sc.setComponent("ContentNaviPane", "WorkEditCompo");
                                return;
                            }

                            // 適用ボタンが押された場合は、工程情報を読み直す
                            this.updateView();

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

                // 保存した工程を使用している工程順に、タクトタイムの変更を反映する
                this.updateTaktTime(this.workInfo);

                this.cloneWorkInfo = this.workInfo.clone();

                if (closing) {
                    sc.setComponent("ContentNaviPane", "WorkEditCompo");
                    return true;
                }
                            
                this.updateView();
            }
            
            return true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            Platform.runLater(() -> DialogBox.alert(ex));
            return false;

        } finally {
            logger.info("registWorkDetail end.");
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
            
            // 未入力判定
            if (checkEmpty()) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return false;
            }

            //フォーマット確認
            if (!isValidItems()) {
                return false;
            }

            // ドキュメントがダウンロードされていない場合、タブが移動された時の再アップロードに備えるため、ダウンロードしておく
            if (this.workSection.hasDocument() && !this.workSectionPane.isLoaded()) {
                workSectionPane.download();
            }

            //工程の更新
            this.updateData();

            // 工程を追加
            String workId;
            if (Objects.isNull(this.workInfo.getWorkId()) || this.workInfo.getWorkId() == 0) {
                ResponseEntity res = this.workInfoFacade.registWork(this.workInfo);
                if (Objects.nonNull(res) && !ResponseAnalyzer.getAnalyzeResult(res)) {
                    return false;
                }

                workId = res.getUri().substring(res.getUri().lastIndexOf("/") + 1);

                // 工程を再取得する。
                this.workInfo = workInfoFacade.find(Long.parseLong(workId), false, true);
            } else {
                workId = String.valueOf(this.workInfo.getWorkId());
            }

            // アップロードするファイルを抽出
            Map<String, String> transfers = new HashMap<>();
            Set<String> deletes = new HashSet<>();

            if (this.workSection.isChenged() && this.workSection.hasDocument()) {
                transfers.put(workSection.getPhysicalName(), this.workSection.getSourcePath());
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

            // 保存した工程を使用している工程順に、タクトタイムの変更を反映する
            this.updateTaktTime(this.workInfo);

            this.cloneWorkInfo = this.workInfo.clone();

            this.updateView();
            
            return true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            Platform.runLater(() -> DialogBox.alert(ex));
            return false;

        } finally {
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
        if (!this.workInfo.getWorkSectionCollection().isEmpty()) {
            WorkSectionInfoEntity section = this.workInfo.getWorkSectionCollection().get(0).clone();
            this.workSection.setWorkSectionId(section.getWorkSectionId());
            this.workSection.setChenged(false);
        }

        // 申請者名ラベル
        ApprovalInfoEntity approval = this.workInfo.getApproval();
        if (Objects.nonNull(approval)) {
            OrganizationInfoEntity organization = CacheUtils.getCacheOrganization(approval.getRequestorId());
            if (Objects.nonNull(organization)) {
                this.requestorNameLabel.setText(organization.getOrganizationName());
            }
        }
        
        // 各種入力項目の表示状態設定
        setInputItemViewState(detailTable);
        workSectionPane.setInputItemViewState(this.disableOtherInputItems);
        
        this.cloneWorkInfo = this.workInfo.clone();
    }

    /**
     * 使用部品の選択ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onChoice(ActionEvent event) {
        try {
            logger.info("onChoice start.");

            SelectDialogEntity selectDialogEntity = new SelectDialogEntity().objects(this.useParts);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Object"), "ObjectSelectionCompo", selectDialogEntity);

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
   
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectedWorkAndHierarchy) {
            selectedWorkAndHierarchy = (SelectedWorkAndHierarchy) argument;
            this.templateRegexTexts.clear();
            this.templateRegexTexts.putAll(PropertyTemplateLoader.getWorkTemplateRegexTexts());

            // 工程の新規作成時、工程プロパティのテンプレートデータを追加する
            if (Objects.nonNull(selectedWorkAndHierarchy.getWorkInfo().getWorkId())) {
                this.defaultTaktTime = selectedWorkAndHierarchy.getWorkInfo().getTaktTime();
                this.workInfo = this.workInfoFacade.find(this.selectedWorkAndHierarchy.getWorkInfo().getWorkId(), false, true);
            } else {
                this.defaultTaktTime = null;
                this.workInfo = selectedWorkAndHierarchy.getWorkInfo();
                this.workInfo.setPropertyInfoCollection(PropertyTemplateLoader.getWorkProperties());
            }

            Platform.runLater(() -> {
                this.createDetailView();
            });
        }
    }

    /**
     * 詳細表示処理
     *
     * @param selectedInfo
     */
    private void createDetailView() {
        parentNameLabel.setText(this.selectedWorkAndHierarchy.getHierarchyName());
        //final WorkInfoEntity work = this.selectedWorkAndHierarchy.getWorkInfo();

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

        // 標準フィールド表示
        topSidePane.getChildren().clear();
        bottomSidePane.getChildren().clear();

        detailProperties.clear();
        detailProperties.put("workName", PropertyBindEntity.createString(LocaleUtils.getString("key.ProcessName") + LocaleUtils.getString("key.RequiredMark"), this.workInfo.getWorkName()));

        // 作業番号 (enableDailyReport=true の場合のみ表示)
        if (WorkflowEditConfig.getEnableDailyReport()) {
            detailProperties.put("workNumber", PropertyBindEntity.createString(LocaleUtils.getString("key.IndirectWorkNumber"), this.workInfo.getWorkNumber()));
        }

        String taktTime = StringTime.convertMillisToStringTime(this.workInfo.getTaktTime());
        detailProperties.put("taktTime", PropertyBindEntity.createTimeStamp(LocaleUtils.getString("key.TactTime") + LocaleUtils.getString("key.TimeTitle"), taktTime));

        // 作業内容
        detailProperties.put("content", PropertyBindEntity.createTextArea(LocaleUtils.getString("key.WorkContent"), this.workInfo.getContent()));

        //背景色・文字色の表示
        Color backColor = null;
        Color fontColor = null;
        try {
            backColor = Color.web(this.workInfo.getBackColor());
            fontColor = Color.web(this.workInfo.getFontColor());
        } catch (IllegalArgumentException | NullPointerException ex) {
            logger.fatal(ex, ex);
        }
        detailProperties.put("backColor", PropertyBindEntity.createColorPicker(LocaleUtils.getString("key.BackColor"), backColor));
        detailProperties.put("fontColor", PropertyBindEntity.createColorPicker(LocaleUtils.getString("key.FontColor"), fontColor));
        
        // 追加情報表示設定
        EventHandler<ActionEvent> onChangeDispAddInfoHandler = ( e ) -> {            
            this.onChangeDispAddInfo(e);
        };
        detailProperties.put("dispAddInfo", PropertyBindEntity.createButton(LocaleUtils.getString("key.DispAddInfo"), LocaleUtils.getString("key.Change"), onChangeDispAddInfoHandler, null));

        // １カラム目の横幅を設定
        List<ColumnConstraints> constraints = new ArrayList<>();
        constraints.add(new ColumnConstraints(175.0, -1.0, -1.0));

        detailTable = new Table(this.topSidePane.getChildren()).isAddRecord(false).footerPadding(new Insets(0, 0 ,0, 0)).bodyColumnConstraints(constraints);
        detailTable.setAbstractRecordFactory(new DetailRecordFactory(detailTable, new LinkedList(detailProperties.values()), Constants.TAKT_TIME_MAX_MILLIS));

        // テーブルに版数のコントロールを追加
        createAdditionalColumn(detailTable);

        // 作業内容の右クリックメニューに「リンクの挿入」を追加
        Platform.runLater(() -> {
            detailTable.findLabelRow(LocaleUtils.getString("key.WorkContent")).ifPresent(index -> {
                contentTextArea = (TextArea) detailTable.getNodeFromBody((int) index, 1).get();

                MenuItem insertLink = new MenuItem(LocaleUtils.getString("key.InsertLink"));
                insertLink.setOnAction(this::onInsertLink);

                ContextMenu contextMenu = new ContextMenu();
                contextMenu.getItems().addAll(insertLink);
                contentTextArea.setContextMenu(contextMenu);
            });
        });

        // 各種入力項目の表示状態設定
        setInputItemViewState(detailTable);

        // カスタムフィールド表示
        if (!Objects.nonNull(this.workInfo.getPropertyInfoCollection())) {
            this.workInfo.setPropertyInfoCollection(new ArrayList<>());
        }

        properties.clear();
        traceabilityProperties.clear();

        for (WorkPropertyInfoEntity property : this.workInfo.getPropertyInfoCollection()) {
            WorkPropertyCategoryEnum category = property.getWorkPropCategory();
            if (Objects.isNull(category) || WorkPropertyCategoryEnum.INFO.equals(category)) {
                properties.add(property);
            } else {
                traceabilityProperties.add(property);
            }
        }

        properties.sort(Comparator.comparing(property -> property.getWorkPropOrder()));
        traceabilityProperties.sort(Comparator.comparing(property -> property.getWorkPropOrder()));

        // 追加情報
        Table customTable = new Table(bottomSidePane.getChildren());
        customTable.isAddRecord(true);
        customTable.isColumnTitleRecord(true);
        customTable.title(LocaleUtils.getString("key.CustomField"));
        customTable.styleClass("ContentTitleLabel");
        customTable.setAbstractRecordFactory(new WorkPropertyRecordFactory(customTable, properties));

        // ドキュメント表示
        if (this.workInfo.getWorkSectionCollection().size() < 1) {
            // 新規作成の場合
            this.workSection = new WorkSectionInfoEntity();
            this.workSection.setDocumentTitle("Sheet 1");
            this.workSectionPane = WorkSectionPane.newInstance(this.workSection, this.useParts, this.manufactureEquipments, this.measureEquipments, rb, this.disableOtherInputItems);
            this.docPane.getChildren().add(this.workSectionPane);
        } else {
            this.workSection = this.workInfo.getWorkSection(1).clone();
            this.workSectionPane = WorkSectionPane.newInstance(this.workSection, this.useParts, this.manufactureEquipments, this.measureEquipments, rb, this.disableOtherInputItems);
            this.docPane.getChildren().add(this.workSectionPane);

            this.workSectionPane.loadCacheData();
        }

        // トレーサビリティ
        if (ClientServiceProperty.isLicensed("@Traceability")) {
            EquipmentSearchCondition condition = new EquipmentSearchCondition();

            // 製造設備
            condition.setEquipmentType(EquipmentTypeEnum.MANUFACTURE);
            long count = this.equipmentInfoFacade.countSearch(condition);
            for (long ii = 0; ii <= count; ii += maxRest) {
                manufactureEquipments.addAll(this.equipmentInfoFacade.findSearchRange(condition, ii, ii + maxRest - 1));
            }

            // 測定機器
            condition.setEquipmentType(EquipmentTypeEnum.MEASURE);
            count = this.equipmentInfoFacade.countSearch(condition);
            for (long ii = 0; ii <= count; ii += maxRest) {
                measureEquipments.addAll(this.equipmentInfoFacade.findSearchRange(condition, ii, ii + maxRest - 1));
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

            this.usePartsPane.setVisible(true);
            this.usePartsTextArea.setText(objectIds);

            // トレーサビリティ設定
            Table traceabilityTable = new Table(bottomSidePane.getChildren());
            traceabilityTable.isAddRecord(!this.disableOtherInputItems);
            traceabilityTable.isColumnTitleRecord(true);
            traceabilityTable.title(LocaleUtils.getString("key.TraceabilitySettings"));
            traceabilityTable.styleClass("ContentTitleLabel");
            traceabilityTable.setAbstractRecordFactory(new TraceabilityRecordFactory(traceabilityTable, this.traceabilityProperties, this.useParts, manufactureEquipments, measureEquipments, this.disableOtherInputItems));
        }

        cloneWorkInfo = this.workInfo.clone();
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
            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Process"), message);
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
            if (this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
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
                this.disableWorkNameField = true;
            }
        } else {
            // 承認機能オプションが有効
            visibleRequestButton = true;
            visibleRequestCancelButton = true;

            if (!this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
                // リソース編集権限なし
                this.disableWorkNameField = true;
                if (Objects.nonNull(this.workInfo.getApprovalId())) {
                    // 申請情報あり
                    if (ApprovalStatusEnum.APPLY.equals(this.workInfo.getApprovalState())) {
                        // 申請中
                        visibleRequestingLabel = true;
                        visibleRequesorNameLabel = true;
                    }
                }
            } else {
                // リソース編集権限あり
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
                            if (this.loginUserInfoEntity.getId().equals(this.workInfo.getApproval().getRequestorId())) {
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
        requestCancelButton.setVisible(visibleRequestCancelButton);
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
        bottomSidePane.setDisable(this.disableOtherInputItems);
    }

    /**
     * 未入力チェック
     *
     * @return 未入力:true
     */
    private boolean checkEmpty() {
        String workName = ((StringProperty) detailProperties.get("workName").getProperty()).get();
        if (Objects.isNull(workName) || workName.isEmpty()) {
            return true;
        }

        for (WorkPropertyInfoEntity entity : properties) {
            entity.updateMember();
            if (Objects.isNull(entity.getWorkPropName())
                    || entity.getWorkPropName().isEmpty()
                    || Objects.isNull(entity.getWorkPropType())) {
                return true;
            }
        }

        if (ClientServiceProperty.isLicensed("@Traceability")) {
            for (WorkPropertyInfoEntity entity : this.traceabilityProperties) {
                entity.setWorkPropType(CustomPropertyTypeEnum.TYPE_STRING);
                entity.updateMember();
                if (Objects.isNull(entity.getWorkPropCategory()) || Objects.isNull(entity.getWorkPropName()) || entity.getWorkPropName().isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 「工程順にタクトタイムの変更を反映する」の確認ダイアログ表示
     *
     * @author e-mori
     * @version 1.4.2
     * @since 2016.08.01.Mon
     * @param entity 工程順に反映する工程
     */
    private void updateTaktTime(WorkInfoEntity entity) {
        if (Objects.isNull(entity.getWorkId())) {
            return;
        } else if (Objects.isNull(defaultTaktTime) || Objects.equals(defaultTaktTime, entity.getTaktTime())) {
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

            //適用ボタン押下時にタクトタイムを変更した場合、その後の登録でタクトタイム変更ダイアログが出現するのを防ぐ
            this.defaultTaktTime = entity.getTaktTime();
        }

        logger.info(WorkDetailCompoFxController.class.getName() + ":showRescheduleDialog end");
    }

    /**
     * 入力項目チェック
     *
     * @return 入力データ有効:true, 入力エラー:false
     *
     */
    private boolean isValidItems() {
        //タクトタイム確認
        String taktTime = ((StringProperty) detailProperties.get("taktTime").getProperty()).get();
        if (!StringTime.validStringTime(taktTime)
                || StringTime.convertStringTimeToMillis(taktTime) > Integer.MAX_VALUE) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.TimeFormatErrMessage"));
            return false;
        }

        // 追加情報の正規表現チェック
        for (WorkPropertyInfoEntity entity : properties) {
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
     * 入力されたフィールドの情報を取得する
     *
     * @return
     */
    private WorkInfoEntity updateData() {

        this.workInfo.setWorkName(((StringProperty) detailProperties.get("workName").getProperty()).get());

        // 作業番号 (enableDailyReport=true の場合のみ)
        if (WorkflowEditConfig.getEnableDailyReport()) {
            String workNumber = null;
            if (Objects.nonNull(detailProperties.get("workNumber").getProperty())) {
                workNumber = ((StringProperty) detailProperties.get("workNumber").getProperty()).get();
            }
            this.workInfo.setWorkNumber(workNumber);
        }

        Long taktTime = StringTime.convertStringTimeToMillis(((StringProperty) detailProperties.get("taktTime").getProperty()).get());
        this.workInfo.setTaktTime(taktTime.intValue());
        this.workInfo.setContent(((StringProperty) detailProperties.get("content").getProperty()).get());
        this.workInfo.setContentType(ContentTypeEnum.STRING);
        Color backColor = ((ObjectProperty<Color>) detailProperties.get("backColor").getProperty()).get();
        Color fontColor = ((ObjectProperty<Color>) detailProperties.get("fontColor").getProperty()).get();
        this.workInfo.setBackColor(StringUtils.colorToRGBCode(backColor));
        this.workInfo.setFontColor(StringUtils.colorToRGBCode(fontColor));
        this.workInfo.setUpdatePersonId(loginUserInfoEntity.getId());
        this.workInfo.setUpdateDatetime(new Date());
        this.workInfo.setUseParts(this.usePartsTextArea.getText());

        // プロパティの更新
        int order = 1;
        this.workInfo.getPropertyInfoCollection().clear();
        for (WorkPropertyInfoEntity entity : properties) {
            entity.setWorkPropOrder(order);
            this.workInfo.getPropertyInfoCollection().add(entity);
            order = order + 1;
        }

        this.workInfo.getWorkSectionCollection().clear();
        workSection.setWorkSectionOrder(1);
        this.workInfo.getWorkSectionCollection().add(workSection);

        if (ClientServiceProperty.isLicensed("@Traceability")) {
            for (WorkPropertyInfoEntity entity : this.traceabilityProperties) {
                entity.setWorkPropOrder(order);
                entity.setWorkSectionOrder(workSection.getWorkSectionOrder());
                entity.updateMember();
                this.workInfo.getPropertyInfoCollection().add(entity);
                order = order + 1;
            }
        }

        return this.workInfo;
    }

    /**
     * 開いたときに表示されたものから変更があるか確認
     *
     */
    private boolean isChanged() {
        // 編集権限なし、または、編集不可モード時は常に無変更
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW) || this.disableOtherInputItems) {
            return false;
        }

        return !this.updateData().displayInfoEquals(cloneWorkInfo);
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
        
            return ret;
 
        } finally {
            logger.info("destoryComponent end.");
        }
    }
}
