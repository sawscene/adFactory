/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.component.bpmn;

import adtekfuji.admanagerapp.unittemplateplugin.common.PropertyTemplateLoader;
import adtekfuji.admanagerapp.unittemplateplugin.common.UnitTemplatePaneEditor;
import adtekfuji.admanagerapp.unittemplateplugin.common.UnitTemplateCell;
import adtekfuji.admanagerapp.unittemplateplugin.common.UnitTemplateModel;
import adtekfuji.admanagerapp.unittemplateplugin.common.UnitTemplatePane;
import adtekfuji.admanagerapp.unittemplateplugin.component.UnitTemplateDetailCompoInterface;
import adtekfuji.admanagerapp.unittemplateplugin.dialog.PropertySettingDialogEntity;
import adtekfuji.admanagerapp.unittemplateplugin.dialog.UnitTemplateAssociationSettingDialogEntity;
import adtekfuji.admanagerapp.unittemplateplugin.dialog.UnitTemplateSettingDialogEntity;
import jp.adtekfuji.forfujiapp.utils.UnitTemplateCheckerUtils;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringTime;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.UIControlInterface;
import jp.adtekfuji.forfujiapp.entity.unittemplate.ConUnitTemplateAssociateInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplatePropertyInfoEntity;
import jp.adtekfuji.forfujiapp.utils.CheckerUtilEntity;
import jp.adtekfuji.javafxcommon.workflowmodel.CellBase;
import jp.adtekfuji.javafxcommon.validator.StringValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレートBPMN編集画面
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.31.Mon
 */
public class UnitTemplateBPMNController implements Initializable, UIControlInterface {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final UnitTemplateDetailCompoInterface detailCompoInterface;
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final UnitTemplateModel model = new UnitTemplateModel();
    private boolean isChangeData = false;
    private boolean isNewCreate = false;
    private final static int REGEX_NAME_NUMBER = 256;

    @FXML
    private TextField unittemplateNameField;
    @FXML
    private ScrollPane bpmnScrollPane;

    public UnitTemplateBPMNController(UnitTemplateDetailCompoInterface detailCompoInterface) {
        this.detailCompoInterface = detailCompoInterface;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(UnitTemplateBPMNController.class.getName() + ":initialize start");
        logger.info(UnitTemplateBPMNController.class.getName() + ":initialize end");
    }

    /**
     * BPMNの編集画面の生成
     *
     * @param unitTemplate
     */
    public void createBPMN(UnitTemplateInfoEntity unitTemplate) {
        logger.info(UnitTemplateBPMNController.class.getName() + ":createBPMN start");
        UnitTemplatePane pane = model.getUnitTemplatePane();
        pane.setUnitTemplateEntity(unitTemplate);
        model.createWorkflowDiaglam(pane);

        try {
            if (Objects.isNull(unitTemplate.getUnitTemplateName())) {
                unitTemplate.setUnitTemplatePropertyCollection(PropertyTemplateLoader.getUnitTemplateProperties());
                this.unittemplateNameField.setText(LocaleUtils.getString("key.NotName"));
                this.unittemplateNameField.setDisable(true);
                this.isNewCreate = true;
            }
            this.unittemplateNameField.setText(unitTemplate.getUnitTemplateName());
            StringValidator.bindValidator(unittemplateNameField, new SimpleStringProperty(unitTemplate.getUnitTemplateName())).setMaxChars(REGEX_NAME_NUMBER);
            this.bpmnScrollPane.setContent(pane);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(UnitTemplateBPMNController.class.getName() + ":createBPMN end");
    }

    /**
     * 編集のキャンセル処理
     *
     * @param event
     */
    @FXML
    public void onCancelButton(ActionEvent event) {
        logger.info(UnitTemplateBPMNController.class.getName() + ":onCancelButton start");

        if (this.isChangeData) {
            try {
                String unittemplateName = this.detailCompoInterface.getUnitTemplateInfoEntity().getUnitTemplateName();
                if (Objects.isNull(unittemplateName)) {
                    unittemplateName = LocaleUtils.getString("key.NewUnitTemplateName");
                }
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.CheckSave"),
                        String.format(LocaleUtils.getString("key.CheckSaveMessage"), unittemplateName));
                if (ret.equals(ButtonType.OK)) {
                    if (this.unittemplateRegist()) {
                        this.backToListCompo();
                    }
                } else {
                    this.backToListCompo();
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
        this.backToListCompo();

        logger.info(UnitTemplateBPMNController.class.getName() + ":onCancelButton end");
    }

    /**
     * ユニットテンプレートの登録処理
     *
     * @param event
     */
    @FXML
    public void onRegistButton(ActionEvent event) {
        logger.info(UnitTemplateBPMNController.class.getName() + ":onRegistButton start");

        this.blockUI(true);
        if (unittemplateRegist()) {
            backToListCompo();
        }
        blockUI(false);

        logger.info(UnitTemplateBPMNController.class.getName() + ":onRegistButton end");
    }

    /**
     * 登録処理
     *
     * @return 結果
     */
    private boolean unittemplateRegist() {
        try {
            // 工程順オーダーを更新
            UnitTemplatePaneEditor.updateWorkflowOrder(model);
            UnitTemplateInfoEntity unittemplate = UnitTemplatePaneEditor.getUnitTemplateInfoEntity(model);
            unittemplate.setFkUpdatePersonId(loginUserInfoEntity.getId());
            unittemplate.setUpdateDatetime(new Date());
            if (isNewCreate) {
                String name = sc.showTextInputDialog(
                        LocaleUtils.getString("key.NewCreate"), LocaleUtils.getString("key.warn.notInputUnitTmeplateName"), LocaleUtils.getString("key.unittemplateName"), "");
                CheckerUtilEntity check = UnitTemplateCheckerUtils.checkRegistUnitTemplate(unittemplate, name);
                if (check.isSuccsess()) {
                    unittemplate.setUnitTemplateName(name);
                    if (!RestAPI.registUnitTemplate(unittemplate).isSuccess()) {
                        return false;
                    }
                } else {
                    sc.showAlert(check.getAlertType(), LocaleUtils.getString(check.getErrTitle()), LocaleUtils.getString(check.getErrMessage()));
                    return false;
                }
            } else {
                CheckerUtilEntity check = UnitTemplateCheckerUtils.checkRegistUnitTemplate(unittemplate, unittemplateNameField.getText());
                if (check.isSuccsess()) {
                    unittemplate.setUnitTemplateName(unittemplateNameField.getText());
                    if (!RestAPI.updateUnitTemplate(unittemplate).isSuccess()) {
                        return false;
                    }
                } else {
                    sc.showAlert(check.getAlertType(), LocaleUtils.getString(check.getErrTitle()), LocaleUtils.getString(check.getErrMessage()));
                    return false;
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return true;
    }

    /**
     * 選択したワークの削除処理
     *
     * @param event
     */
    @FXML
    public void onDeleteWorkButton(ActionEvent event) {
        logger.info(UnitTemplateBPMNController.class.getName() + ":onDeleteWorkButton start");
        UnitTemplatePaneEditor.delete(model, this.detailCompoInterface.getUnitTemplateInfoEntity());
        this.isChangeData = true;
        logger.info(UnitTemplateBPMNController.class.getName() + ":onDeleteWorkButton end");

    }

    /**
     * 選択したワークの編集処理
     *
     * @param event
     */
    @FXML
    public void onSettingWorkButton(ActionEvent event) {
        logger.info(UnitTemplateBPMNController.class.getName() + ":onSettingWorkButton start");
        List<CellBase> checkList = model.getUnitTemplatePane().getCellList().stream().filter(p -> p.isChecked()).collect(Collectors.toList());
        if (checkList.size() > 1) {
            UnitTemplateAssociationSettingDialogEntity dialogEntity = new UnitTemplateAssociationSettingDialogEntity();
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "UnitTemplateAssociationSettingDialog", dialogEntity);
            if (ret.equals(ButtonType.OK)) {
                blockUI(true);
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            if (dialogEntity.getOffset() != 0) {
                                // オフセットが設定された場合
                                checkList.stream().filter(p -> p instanceof UnitTemplateCell).forEach((cell) -> {
                                    ConUnitTemplateAssociateInfoEntity workflowWork = ((UnitTemplateCell) cell).getUnitTemplateAssociate();
                                    workflowWork.setStandardStartTime(StringTime.getFixedDate(workflowWork.getStandardStartTime(), dialogEntity.getOffsetTime()));
                                    workflowWork.setStandardEndTime(StringTime.getFixedDate(workflowWork.getStandardEndTime(), dialogEntity.getOffsetTime()));
                                });
                            }

                            checkList.stream().filter(p -> p instanceof UnitTemplateCell).forEach((cell) -> {
                                applyUnitTemplateAssociate((UnitTemplateCell) cell, dialogEntity);
                            });
                        } finally {
                            Platform.runLater(() -> blockUI(false));
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        } else if (checkList.size() == 1) {
            UnitTemplateCell workCell = (UnitTemplateCell) checkList.get(0);
            ConUnitTemplateAssociateInfoEntity associate = workCell.getUnitTemplateAssociate();

            UnitTemplateAssociationSettingDialogEntity dialogEntity;
            if (Objects.nonNull(associate.getFkUnitTemplateId())) {
                // タクトタイム取得
                Long tactTime = RestAPI.getUnitTemplateTactTime(associate.getFkUnitTemplateId());
                dialogEntity = new UnitTemplateAssociationSettingDialogEntity(tactTime, associate.getStandardStartTime(),
                        associate.getStandardEndTime(), true);
            } else {
                // タクトタイム取得
                Long tactTime = RestAPI.getWorkflowTactTime(associate.getFkWorkflowId());
                dialogEntity = new UnitTemplateAssociationSettingDialogEntity(tactTime, associate.getStandardStartTime(),
                        associate.getStandardEndTime(), true);
            }
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "UnitTemplateAssociationSettingDialog", dialogEntity);
            if (ret.equals(ButtonType.OK)) {
                blockUI(true);
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            applyUnitTemplateAssociate(workCell, dialogEntity);
                        } finally {
                            Platform.runLater(() -> blockUI(false));
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        }
        this.isChangeData = true;
        logger.info(UnitTemplateBPMNController.class.getName() + ":onSettingWorkButton end");

    }

    /**
     * ユニットテンプレートの設定画面を開く
     *
     * @param event
     */
    @FXML
    public void onUnitTemplateSetting(ActionEvent event) {
        logger.info(UnitTemplateBPMNController.class.getName() + ":onUnitTemplateSetting start");
        UnitTemplateInfoEntity entity = UnitTemplatePaneEditor.getUnitTemplateInfoEntity(model);
        PropertySettingDialogEntity<UnitTemplatePropertyInfoEntity> propertyEntity = new PropertySettingDialogEntity(
                (Objects.nonNull(entity.getUnitTemplatePropertyCollection())) ? new LinkedList<>(entity.getUnitTemplatePropertyCollection()) : new LinkedList<>());
        KanbanHierarchyInfoEntity hierachy = (Objects.nonNull(entity.getOutputKanbanHierarchyName()))
                ? new KanbanHierarchyInfoEntity(entity.getFkOutputKanbanHierarchyId(), entity.getOutputKanbanHierarchyName()) : new KanbanHierarchyInfoEntity();
        UnitTemplateSettingDialogEntity dialogEntity = new UnitTemplateSettingDialogEntity(hierachy, propertyEntity);

        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.UnitTemplateSettingDialog"), "UnitTemplateSettingDialog", dialogEntity);
        if (ret.equals(ButtonType.OK)) {
            //各工程に設定された工程情報を上書きする
            //出力先階層
            entity.setFkOutputKanbanHierarchyId(dialogEntity.getOutputKanbanHierarchy().getKanbanHierarchyId());
            entity.setOutputKanbanHierarchyName(dialogEntity.getOutputKanbanHierarchy().getHierarchyName());

            //カスタムフィールド
            int order = 0;
            if (Objects.nonNull(entity.getUnitTemplateId())) {
                for (UnitTemplatePropertyInfoEntity e : dialogEntity.getPropertyEntity().getPropertys()) {
                    e.setFkMasterId(entity.getUnitTemplateId());
                    e.updateMember();
                    e.setUnitTemplatePropertyOrder(order);
                    order = order + 1;
                }
            } else {
                for (UnitTemplatePropertyInfoEntity e : dialogEntity.getPropertyEntity().getPropertys()) {
                    e.updateMember();
                    e.setUnitTemplatePropertyOrder(order);
                    order = order + 1;
                }
            }
            entity.setUnitTemplatePropertyCollection(dialogEntity.getPropertyEntity().getPropertys());
        }
        logger.info(UnitTemplateBPMNController.class.getName() + ":onUnitTemplateSetting end");
    }

    /**
     * 工程の設定内容を適用する
     *
     * @param editEntitys 編集対象のデータ
     * @param dialogEntity 編集内容
     */
    private void applyUnitTemplateAssociate(UnitTemplateCell workCell, UnitTemplateAssociationSettingDialogEntity dialogEntity) {
        try {
            ConUnitTemplateAssociateInfoEntity associate = workCell.getUnitTemplateAssociate();
            boolean isUpdate = false;

            // 開始時間・終了時間
            if (dialogEntity.isEditSingle()) {
                if (Objects.nonNull(dialogEntity.getStartTime()) && Objects.nonNull(dialogEntity.getEndTime())) {
                    associate.setStandardStartTime(dialogEntity.getStartTime());
                    associate.setStandardEndTime(dialogEntity.getEndTime());
                    isUpdate = true;
                }
            }

            associate.updateMember();

            if (isUpdate) {
                this.model.updateTimetable(workCell, 0, true, false);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ワークの全選択
     *
     * @param event
     */
    @FXML
    public void onAllCheckButton(ActionEvent event) {
        logger.info(UnitTemplateBPMNController.class.getName() + ":onAllCheckButton start");
        UnitTemplatePaneEditor.allCheck(model);
        logger.info(UnitTemplateBPMNController.class.getName() + ":onAllCheckButton end");

    }

    /**
     * ワークの選択解除
     *
     * @param event
     */
    @FXML
    public void onAllUncheckButton(ActionEvent event) {
        logger.info(UnitTemplateBPMNController.class.getName() + ":onAllUncheckButton start");
        UnitTemplatePaneEditor.allUncheck(model);
        logger.info(UnitTemplateBPMNController.class.getName() + ":onAllUncheckButton end");

    }

    /**
     * 直列にワークを挿入する
     *
     * @param entity
     */
    public void onAddSerial(Object entity) {
        logger.info(UnitTemplateBPMNController.class.getName() + ":onAddSerial start");
        if (entity instanceof UnitTemplateInfoEntity) {
            UnitTemplatePaneEditor.addSerial(model, (UnitTemplateInfoEntity) entity);
        } else if (entity instanceof WorkflowInfoEntity) {
            UnitTemplatePaneEditor.addSerial(model, (WorkflowInfoEntity) entity);
        }
        this.isChangeData = true;
        logger.info(UnitTemplateBPMNController.class.getName() + ":onAddSerial end");
    }

    /**
     * 直列にワークを挿入する
     *
     * @param entity
     */
    public void onAddParallel(Object entity) {
        logger.info(UnitTemplateBPMNController.class.getName() + ":onAddParallel start");
        if (entity instanceof UnitTemplateInfoEntity) {
            UnitTemplatePaneEditor.addParallel(model, (UnitTemplateInfoEntity) entity, false);
        } else if (entity instanceof WorkflowInfoEntity) {
            UnitTemplatePaneEditor.addParallel(model, (WorkflowInfoEntity) entity, false);
        }
        this.isChangeData = true;
        logger.info(UnitTemplateBPMNController.class.getName() + ":onAddParallel end");
    }

    /**
     * 前の画面に戻る
     *
     */
    private void backToListCompo() {
        Platform.runLater(() -> {
            sc.setComponent("ContentNaviPane", "UnittemplateListComp");
        });
    }

    /**
     * 画面に使用制限をかける
     *
     * @param isBlock
     */
    @Override
    public void blockUI(boolean isBlock) {
        this.detailCompoInterface.blockUI(isBlock);
    }

    @Override
    public void updateUI() {
    }
}
