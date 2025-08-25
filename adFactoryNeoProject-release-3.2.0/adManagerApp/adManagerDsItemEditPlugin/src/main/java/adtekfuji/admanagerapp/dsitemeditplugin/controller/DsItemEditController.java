/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adtekfuji.admanagerapp.dsitemeditplugin.controller;

import adtekfuji.admanagerapp.dsitemeditplugin.common.Constants;
import adtekfuji.admanagerapp.workfloweditplugin.model.WorkflowEditModel;
import adtekfuji.clientservice.DsItemFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.job.MstDsItemInfo;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.javafxcommon.common.Styles;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.event.ActionEventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 品番編集画面 コントローラー
 * 
 * @author s-heya
 */
@FxComponent(id = "DsItemEditCompo", fxmlPath = "/adtekfuji/admanagerapp/dsitemeditplugin/dsitem_edit_compo.fxml")
public class DsItemEditController implements Initializable, ArgumentDelivery, DialogHandler, ActionEventListener {
    
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final DsItemFacade facade = new DsItemFacade();
    private final WorkflowEditModel workflowEditModel = WorkflowEditModel.getInstance();

    private Dialog dialog;
    private MstDsItemInfo dsItem;
    private MstDsItemInfo oldDsItem;
    private WorkflowInfoEntity workflow1;
    private WorkflowInfoEntity workflow2;
    private boolean isEditMode;
    
    @FXML
    private GridPane contentsPane;
    @FXML
    private RowConstraints location1Row;
    @FXML
    private RowConstraints location2Row;
    @FXML
    private RowConstraints emptyRow;
    @FXML
    private RowConstraints bomRow;
    @FXML
    private RowConstraints workflow1Row;
    @FXML
    private RestrictedTextField productNoField;
    @FXML
    private RestrictedTextField productNameField;
    @FXML
    private RestrictedTextField specField;
    @FXML
    private Label location1Label;
    @FXML
    private RestrictedTextField location1Field;
    @FXML
    private Label location2Label;
    @FXML
    private RestrictedTextField location2Field;
    @FXML
    private Label bomLabel;
    @FXML
    private Label bomField;
    @FXML
    private Button registBomButton;
    //@FXML
    //private Button editBomButton;
    @FXML
    private Label workflow1Label;
    @FXML
    private Label workflow1Field;
    @FXML
    private Button registWorkflow1Button;
    @FXML
    private Button editWorkflow1Button;
    @FXML
    private Label workflow2Field;
    @FXML
    private Button registWorkflow2Button;
    @FXML
    private Button editWorkflow2Button;
    @FXML
    private Button registButton;

    /**
     * 品番編集画面を初期化する。
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("initialize start.");
        
        LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
        if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            this.registBomButton.setDisable(true);
            //this.editBomButton.setDisable(true);
            this.registWorkflow1Button.setDisable(true);
            this.editWorkflow1Button.setDisable(true);
            this.registWorkflow2Button.setDisable(true);
            this.editWorkflow2Button.setDisable(true);
            this.registButton.setDisable(true);
        }

        //this.editBomButton.disableProperty().bind(Bindings.greaterThanOrEqual(bomLabel.textProperty(), "未登録"));
        this.editWorkflow1Button.disableProperty().bind(Bindings.equal(workflow1Field.textProperty(), "未登録"));
        this.editWorkflow2Button.disableProperty().bind(Bindings.equal(workflow2Field.textProperty(), "未登録"));
    }
    
    /**
     * パラメータを設定する。
     * 
     * @param argument パラメータ 
     */
    @Override
    public void setArgument(Object argument) {
        
        Tuple<String, MstDsItemInfo> tuple = (Tuple) argument;
        
        int category = Constants.LINE_PRODUCTION.equals(tuple.getLeft()) ? 1 : 2;
        if (2 == category) {
            this.location1Row.setMinHeight(0.0);
            this.location1Label.setManaged(false);
            this.location1Field.setManaged(false);
            
            this.location2Row.setMinHeight(0.0);
            this.location2Label.setManaged(false);
            this.registBomButton.setManaged(false);

            this.emptyRow.setMinHeight(0.0);
        
            this.bomRow.setMinHeight(0.0);
            this.bomLabel.setManaged(false);
            this.bomField.setManaged(false);
            this.location2Field.setManaged(false);

            this.workflow1Row.setMinHeight(0.0);
            this.workflow1Label.setManaged(false);
            this.workflow1Field.setManaged(false);
            this.registWorkflow1Button.setManaged(false);
            this.editWorkflow1Button.setManaged(false);
        }

        if (Objects.isNull(tuple.getRight())) {
            this.oldDsItem = new MstDsItemInfo(category);
            this.dsItem = this.oldDsItem.clone();

            this.bomField.setStyle(Styles.LABEL_STYLE_UNREGISTERED);
            this.workflow1Field.setStyle(Styles.LABEL_STYLE_UNREGISTERED);
            this.workflow2Field.setStyle(Styles.LABEL_STYLE_UNREGISTERED);
            return;
        }

        this.oldDsItem = tuple.getRight();
        this.dsItem = this.oldDsItem.clone();
        this.isEditMode = true;
        
        this.productNoField.setText(this.dsItem.getProductNo());
        this.productNameField.setText(this.dsItem.getProductName());
        this.specField.setText(this.dsItem.getSpec());
        this.location1Field.setText(this.dsItem.getLocation1());
        this.location2Field.setText(this.dsItem.getLocation2());
        
        if (!this.dsItem.getDsParts().isEmpty()) {
           this.bomField.setText("登録");
           this.bomField.setStyle(Styles.LABEL_STYLE_REGISTERED);
        } else {
           this.bomField.setStyle(Styles.LABEL_STYLE_UNREGISTERED);
        }
        
        final WorkflowInfoFacade workflowFacade = new WorkflowInfoFacade();
        if (Objects.nonNull(this.dsItem.getWorkflow1())) {
            WorkflowInfoEntity workflow = workflowFacade.find(this.dsItem.getWorkflow1(), true);
            if (Objects.nonNull(workflow.getWorkflowId())) {
                this.workflow1 = workflow;
                this.workflow1Field.setStyle(Styles.LABEL_STYLE_REGISTERED);
                this.workflow1Field.setText(workflow.getWorkflowName());
            } else {
                this.workflow1Field.setStyle(Styles.LABEL_STYLE_UNREGISTERED);
                this.workflow1Field.setText("工程順が見つかりません");
                this.editWorkflow1Button.setDisable(true);
            }
        } else {
            this.workflow1Field.setStyle(Styles.LABEL_STYLE_UNREGISTERED);
        }

        if (Objects.nonNull(this.dsItem.getWorkflow2())) {
            WorkflowInfoEntity workflow = workflowFacade.find(this.dsItem.getWorkflow2(), true);
            if (Objects.nonNull(workflow.getWorkflowId())) {
                this.workflow2 = workflow;
                this.workflow2Field.setStyle(Styles.LABEL_STYLE_REGISTERED);
                this.workflow2Field.setText(workflow.getWorkflowName());
            } else {
                this.workflow2Field.setStyle(Styles.LABEL_STYLE_UNREGISTERED);
                this.workflow2Field.setText("工程順が見つかりません");
                this.editWorkflow2Button.setDisable(true);
            }
        } else {
            this.workflow2Field.setStyle(Styles.LABEL_STYLE_UNREGISTERED);
        }
    }

    /**
     * Dialog を設定する。
     * 
     * @param dialog 
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> {
            this.applyData();

            if (!this.dsItem.equalsData(this.oldDsItem)) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, null,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL, this.dialog.getDialogPane().getScene().getWindow());

                if (ButtonType.YES == buttonType) {
                    if (this.regist()) {
                        this.dialog.setResult(ButtonType.OK);
                        this.dialog.close();
                        return;
                    }

                    event.consume();
                    return;
    
                } else if (ButtonType.CANCEL == buttonType) {
                    event.consume();
                    return;
                }
                this.cancelDialog();
            }
        });
    }

    /**
     * アクションイベント通知
     * 
     * @param event アクションイベント
     * @param param パラメーター
     */
    @Override
    public void onNotification(ActionEventListener.SceneEvent event, Object param) {
        switch (event) {
            case Close:
                Platform.runLater(() -> {
                    this.sc.visibleArea("WorkflowEditPane", false);
                    
                    if (param instanceof WorkflowInfoEntity) {
                        if ("workflow1".equals(this.workflowEditModel.getParam())) {
                            this.workflow1 = (WorkflowInfoEntity) param;
                            this.dsItem.setWorkflow1(this.workflow1.getWorkflowId());
                            this.workflow1Field.setStyle(Styles.LABEL_STYLE_REGISTERED);
                            this.workflow1Field.setText(this.workflow1.getWorkflowName());
                        } else if ("workflow2".equals(this.workflowEditModel.getParam())) {
                            this.workflow2 = (WorkflowInfoEntity) param;
                            this.dsItem.setWorkflow2(this.workflow2.getWorkflowId());
                            this.workflow2Field.setStyle(Styles.LABEL_STYLE_REGISTERED);
                            this.workflow2Field.setText(this.workflow2.getWorkflowName());
                        }
                    }
                    
                    workflowEditModel.setInnerMode(false);
                    sc.setComponent("AppBarPane", "AppBarCompo");
        
                    Stage stage = (Stage) this.dialog.getDialogPane().getScene().getWindow();
                    stage.show();
                    
                });
                break;
            default:
                break;
        }
    }
    
    /**
     * 工程・工程順編集画面を開く。
     * 
     * @param param パラメーター
     * @param workflow 工程順情報
     */
    private void openWorlflowEditor(String param, WorkflowInfoEntity workflow) {
        try {
            this.dialog.getDialogPane().getScene().getWindow().hide();

            workflowEditModel.addListener(this);
            workflowEditModel.setInnerMode(true);
            workflowEditModel.setParam(param);
        
            sc.setComponent("SideNaviPane", "WorkflowNaviCompo", workflow);
            sc.visibleArea("WorkflowEditPane", true); // 工程・工程順編集画面

        } catch (Exception e) {
            logger.fatal(e, e);
        } finally {
        }
    }
    
    /**
     * 構成部品の登録・編集
     * 
     * @param event 
     */
    @FXML
    public void onEditBom(ActionEvent event) {
        logger.info("onEditBom start.");

        try {
            this.dsItem.setProductNo(this.productNoField.getText());
            
            Dialog dlg = sc.showModelessDialog("", "BomEditCompo", this.dsItem, (Stage) this.dialog.getDialogPane().getScene().getWindow(), true);
            dlg.setOnHidden(e -> {
                ButtonType buttonType = (ButtonType) dlg.getResult();
                if (ButtonType.OK.equals(buttonType)) {
                    logger.info("BomEditCompo closed.");
                    if (this.dsItem.getDsParts().isEmpty()) {
                        this.bomField.setText("未登録");
                        this.bomField.setStyle(Styles.LABEL_STYLE_UNREGISTERED);
                    } else {
                        this.bomField.setText("登録");
                        this.bomField.setStyle(Styles.LABEL_STYLE_REGISTERED);
                    }
                }
            });
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 組付工程の登録
     * 
     * @param event 
     */
    @FXML
    public void onRegistWorkflow1(ActionEvent event) {
        this.openWorlflowEditor("workflow1", new WorkflowInfoEntity());
    }
    
    /**
     * 組付工程の編集
     * 
     * @param event 
     */
    @FXML
    public void onEditWorkflow1(ActionEvent event) {
        this.openWorlflowEditor("workflow1", this.workflow1);
    }

    /**
     * 検査工程の登録
     * 
     * @param event 
     */
    @FXML
    public void onRegistWorkflow2(ActionEvent event) {
        this.openWorlflowEditor("workflow2", new WorkflowInfoEntity());
    }

    /**
     * 検査工程の編集
     * 
     * @param event 
     */
    @FXML
    public void onEditWorkflow2(ActionEvent event) {
        this.openWorlflowEditor("workflow2", this.workflow2);
    }

    /**
     * 登録ボタン処理
     * 
     * @param event 
     */
    @FXML
    public void onRegist(ActionEvent event) {
        logger.info("onRegist");

        this.applyData();

        if (!this.regist()) {
            return;
        }

        this.dialog.setResult(ButtonType.OK);
        this.dialog.close();
    }

    /**
     * キャンセルボタン処理
     * 
     * @param event 
     */
    @FXML
    public void onCancel(ActionEvent event) {
        logger.info("onCancel");

        this.applyData();

        if (!this.dsItem.equalsData(this.oldDsItem)) {
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, null,
                    new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL, this.dialog.getDialogPane().getScene().getWindow());

            if (ButtonType.YES == buttonType) {
                if (this.regist()) {
                    this.dialog.setResult(ButtonType.OK);
                    this.dialog.close();
                    return;
                }
                return;

            } else if (ButtonType.CANCEL == buttonType) {
                return ;
            }
        }
        this.cancelDialog();
    }

    /**
     * 品番編集画面を閉じる。
     *
     */
    private void cancelDialog() {
        try {
            this.workflowEditModel.removeListener(this);

            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
 
    /**
     * 入力された値をデータに適用する。
     * 
     */
    private void applyData() {
        this.dsItem.setProductNo(StringUtils.isEmpty(this.productNoField.getText()) ? null : this.productNoField.getText());
        this.dsItem.setProductName(StringUtils.isEmpty(this.productNameField.getText()) ? null : this.productNameField.getText());
        this.dsItem.setSpec(StringUtils.isEmpty(this.specField.getText()) ? null : this.specField.getText());
        this.dsItem.setLocation1(StringUtils.isEmpty(this.location1Field.getText()) ? null : this.location1Field.getText());
        this.dsItem.setLocation2(StringUtils.isEmpty(this.location2Field.getText()) ? null : this.location2Field.getText());
    }
    
    /**
     * 品番マスタ情報を登録する。
     * 
     * @return 
     */
    private boolean regist() {
        logger.info("regist");

        if (!this.checkEmpty()) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
            return false;
        }
        
        if (Objects.nonNull(this.dsItem.getWorkflow1()) && Objects.nonNull(this.dsItem.getWorkflow2())
                && Objects.equals(this.dsItem.getWorkflow1(), this.dsItem.getWorkflow2())) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), "組付工程と検査工程には、同一の工程を設定できません");
            return false;
        }
        
        
        // 品番をチェック
        Pattern _attern = Pattern.compile("^[a-zA-Z0-9]{6}-[a-zA-Z0-9]{4}$");
        if (!_attern.matcher(this.productNoField.getText()).matches()) {
            this.productNoField.requestFocus();
            return false;
        }

        ResponseEntity response;
        if (!this.isEditMode) {
            response = facade.regist(this.dsItem);
        } else {
            response = facade.update(this.dsItem);
        }

        if (Objects.nonNull(response) || !response.isSuccess()) {
            switch (response.getErrorType()) {
                case IDENTNAME_OVERLAP:
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("この品番は、既に登録されています"));
                    return false;
                default:
                    break;
            }
        }

        return ResponseAnalyzer.getAnalyzeResult(response);
    }
    
    /**
     * 未入力チェック
     * 
     * @return 
     */
    private boolean checkEmpty() {
        if (StringUtils.isEmpty(this.productNoField.getText())
            || StringUtils.isEmpty(this.productNameField.getText())
            || StringUtils.isEmpty(this.specField.getText())) {
            return false;
        }
        return true;
    }
}
