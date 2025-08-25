/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.dialog;


import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanUIControlInterface;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanKanbanCheckerUtils;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanKanbanStartCompDate;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.plugin.KanbanRegistPreprocessContainer;
import jp.adtekfuji.adFactory.utility.KanbanRegistPreprocessResultEntity;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ダイアログ表示用クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.24.Wen
 */
public class WorkPlanDialogController {

    private final static Logger logger = LogManager.getLogger();
    private final static SceneContiner sc = SceneContiner.getInstance();
    private final static LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();

    /**
     * 選択したテーブルツリーのデータを編集
     *
     * @param entity
     * @param uiInterface
     */
//    public static void showEditUnit(UnitInfoEntity entity, UIControlInterface uiInterface) {
//        logger.info(DialogController.class.getName() + ":showEditUnit start");
//        // ユニットを選択していたらユニット編集画面
//        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "UnitDetailDialog", entity);
//        while (ret.equals(ButtonType.OK)) {
//            CheckerUtilEntity check = UnitCheckerUtils.isEmptyUnit(entity);
//            if (!check.isSuccsess()) {
//                sc.showAlert(check.getAlertType(), LocaleUtils.getString(check.getErrTitle()), LocaleUtils.getString(check.getErrMessage()));
//            } else {
//                break;
//            }
//            ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "UnitDetailDialog", entity);
//        }
//        if (ret.equals(ButtonType.OK)) {
//            // 生産ユニットの新規作成処理
//            entity.setFkUpdatePersonId(loginUser.getId());
//            entity.setUpdateDatetime(new Date());
//            UpdateUnitThread(entity, uiInterface);
//        }
//        // カンバンを選択していたらカンバン編集画面
//        logger.info(DialogController.class.getName() + ":showEditUnit end");
//    }

    /**
     * ユニット更新処理
     *
     * @param entity
     */
//    private static void UpdateUnitThread(UnitInfoEntity entity, UIControlInterface uiInterface) {
//        sc.blockUI(true);
//        final UnitInfoEntity updateData = entity;
//        Task task = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                try {
//                    entity.entityUpdate();
//                    ResponseEntity rs = RestAPI.updateUnit(entity);
//                    if (!rs.isSuccess()) {
//                        // 詳細画面表示
//                        Platform.runLater(() -> {
//                            showEditUnit(entity, uiInterface);
//                        });
//                    }
//                    uiInterface.updateUI();
//                } catch (Exception ex) {
//                    logger.fatal(ex, ex);
//                } finally {
//                    sc.blockUI(false);
//                }
//                return null;
//            }
//        };
//        //new Thread(task).start();
//        try {
//            Thread thread = new Thread(task);
//            thread.start();
//            thread.join();
//        } catch (InterruptedException ex) {
//            logger.fatal(ex, ex);
//        }
//    }

    /**
     * 選択したテーブルツリーのデータを編集
     *
     * @param entity
     * @param uiInterface
     */
    public static void showEditKanban(KanbanInfoEntity entity, WorkPlanUIControlInterface uiInterface) {
        logger.info(WorkPlanDialogController.class.getName() + ":editKanban start");
        // ユニットを選択していたらユニット編集画面
        ButtonType ret = sc.showComponentDialog("ContentNaviPane", "WorkPlanDetailCompo", entity);
        while (ret.equals(ButtonType.OK)) {
            if (WorkPlanKanbanCheckerUtils.isEmptyKanban(entity)) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
            } else {
                break;
            }
            ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkPlanDetailCompo", entity);
        }
        if (ret.equals(ButtonType.OK)) {
            entity.setFkUpdatePersonId(loginUser.getId());
            entity.setUpdateDatetime(new Date());
            // 更新処理
            UpdateKanbanThread(entity, uiInterface);
        }
        
        // カンバンを選択していたらカンバン編集画面
        logger.info(WorkPlanDialogController.class.getName() + ":editKanban end");
    }

    /**
     * カンバン更新処理
     *
     * @param entity
     */
    private static void UpdateKanbanThread(KanbanInfoEntity entity, WorkPlanUIControlInterface uiInterface) {
        sc.blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // カンバンと工程カンバンの複製
                    KanbanInfoEntity updateData = getKanban(entity);
                    if (!preRegist(updateData)) {
                        // 詳細画面表示
                        Platform.runLater(() -> {
                            showEditKanban(updateData, uiInterface);
                        });
                        return null;
                    }
                    //                    ResponseEntity entity = RestAPI.updateKanban(updateData);
                    KanbanInfoFacade KANBAN_REST = new KanbanInfoFacade();
                    ResponseEntity entity = KANBAN_REST.update(updateData);

                    if (!entity.isSuccess()) {
                        // 詳細画面表示
                        Platform.runLater(() -> {
                            showEditKanban(updateData, uiInterface);
                        });
                        return null;
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    uiInterface.updateUI();
                    sc.blockUI(false);
                }
                return null;
            }

        };
        //new Thread(task).start();
        try {
            Thread thread = new Thread(task);
            thread.start();
            thread.join();
        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンの複製(プロパティバインドを削除するため)
     *
     * @param oldKanban
     * @return
     */
    private static KanbanInfoEntity getKanban(KanbanInfoEntity oldKanban) {
        // カンバンを複製
        KanbanInfoEntity kanban = new KanbanInfoEntity(oldKanban.getKanbanId(), oldKanban.getParentId(), oldKanban.getKanbanName(), oldKanban.getKanbanSubname());
        kanban.setFkWorkflowId(oldKanban.getFkWorkflowId());
        kanban.setWorkflowName(oldKanban.getWorkflowName());
        kanban.setWorkflowRev(oldKanban.getWorkflowRev());
        kanban.setStartDatetime(oldKanban.getStartDatetime());
        kanban.setCompDatetime(oldKanban.getCompDatetime());
        kanban.setFkUpdatePersonId(oldKanban.getFkUpdatePersonId());
        kanban.setUpdateDatetime(oldKanban.getUpdateDatetime());
        kanban.setKanbanStatus(oldKanban.getKanbanStatus());
        kanban.setFkInterruptReasonId(oldKanban.getFkInterruptReasonId());
        kanban.setFkDelayReasonId(oldKanban.getFkDelayReasonId());
        kanban.setWorkKanbanCollection(getWorkKanbans(oldKanban.getWorkKanbanCollection()));
        kanban.setSeparateworkKanbanCollection(getWorkKanbans(oldKanban.getSeparateworkKanbanCollection()));
        kanban.setLotQuantity(oldKanban.getLotQuantity());
        kanban.setModelName(oldKanban.getModelName());
        List<KanbanPropertyInfoEntity> kanbanProps = new ArrayList<>();
        for (KanbanPropertyInfoEntity oldProp : oldKanban.getPropertyCollection()) {
            KanbanPropertyInfoEntity kanbanProp = new KanbanPropertyInfoEntity(oldProp.getKanbanPropId(), oldProp.getFkKanbanId(),
                    oldProp.getKanbanPropertyName(), oldProp.getKanbanPropertyType(), oldProp.getKanbanPropertyValue(), oldProp.getKanbanPropertyOrder());
            kanbanProps.add(kanbanProp);
        }
        kanban.setPropertyCollection(kanbanProps);

        return kanban;
    }

    /**
     * 工程カンバンの複製(プロパティバインドを削除するため)
     *
     * @param oldWorks
     * @return
     */
    private static List<WorkKanbanInfoEntity> getWorkKanbans(List<WorkKanbanInfoEntity> oldWorks) {
        List<WorkKanbanInfoEntity> works = new ArrayList<>();
        for (WorkKanbanInfoEntity oldWork : oldWorks) {
            WorkKanbanInfoEntity work = new WorkKanbanInfoEntity(oldWork.getWorkKanbanId(), oldWork.getParentId(), oldWork.getFkKanbanId(), oldWork.getFkWorkflowId(), oldWork.getFkWorkId(), oldWork.getWorkName());
            work.setSeparateWorkFlag(oldWork.getSeparateWorkFlag());
            work.setImplementFlag(oldWork.getImplementFlag());
            work.setSkipFlag(oldWork.getSkipFlag());
            work.setStartDatetime(oldWork.getStartDatetime());
            work.setCompDatetime(oldWork.getCompDatetime());
            work.setTaktTime(oldWork.getTaktTime());
            work.setFkUpdatePersonId(oldWork.getFkUpdatePersonId());
            work.setUpdateDatetime(oldWork.getUpdateDatetime());
            work.setWorkStatus(oldWork.getWorkStatus());
            work.setFkInterruptReasonId(oldWork.getFkInterruptReasonId());
            work.setFkDelayReasonId(oldWork.getFkDelayReasonId());
            work.setWorkKanbanOrder(oldWork.getWorkKanbanOrder());
            work.setSumTimes(oldWork.getSumTimes());
            work.setEquipmentCollection(oldWork.getEquipmentCollection());
            work.setOrganizationCollection(oldWork.getOrganizationCollection());
            List<WorkKanbanPropertyInfoEntity> workProps = new ArrayList<>();
            for (WorkKanbanPropertyInfoEntity oldProp : oldWork.getPropertyCollection()) {
                WorkKanbanPropertyInfoEntity workProp = new WorkKanbanPropertyInfoEntity(oldProp.getWorkKanbanPropId(), oldProp.getFkMasterId(),
                        oldProp.getWorkKanbanPropName(), oldProp.getWorkKanbanPropType(), oldProp.getWorkKanbanPropValue(), oldProp.getWorkKanbanPropOrder());
                workProps.add(oldProp);
            }
            work.setPropertyCollection(workProps);
            works.add(work);
        }
        return works;
    }

    /**
     * 保存前処理
     *
     * @param kanbanInfoEntity
     */
    private static Boolean preRegist(KanbanInfoEntity kanbanInfoEntity) {
        try {
            //未入力判定
            if (!registPreCheck(kanbanInfoEntity)) {
                return false;
            }

            int order = 0;
            for (KanbanPropertyInfoEntity entity : kanbanInfoEntity.getPropertyCollection()) {
                entity.setFkKanbanId(kanbanInfoEntity.getKanbanId());
                entity.setKanbanPropertyOrder(order);
                entity.updateMember();
                order = order + 1;
            }
            //工程順更新
            for (WorkKanbanInfoEntity entity : kanbanInfoEntity.getWorkKanbanCollection()) {
                entity.updateMember();
                for (WorkKanbanPropertyInfoEntity proEntity : entity.getPropertyCollection()) {
                    proEntity.updateMember();
                }
            }
            //追加工程更新
            for (WorkKanbanInfoEntity entity : kanbanInfoEntity.getSeparateworkKanbanCollection()) {
                entity.updateMember();
                for (WorkKanbanPropertyInfoEntity proEntity : entity.getPropertyCollection()) {
                    proEntity.updateMember();
                }
            }

            //工程カンバン更新
            List<WorkKanbanInfoEntity> workKanbans = new ArrayList<>();;
            workKanbans.addAll(kanbanInfoEntity.getWorkKanbanCollection());
            workKanbans.addAll(kanbanInfoEntity.getSeparateworkKanbanCollection());

            kanbanInfoEntity.setStartDatetime(WorkPlanKanbanStartCompDate.getWorkKanbanStartDateTime(workKanbans));
            kanbanInfoEntity.setCompDatetime(WorkPlanKanbanStartCompDate.getWorkKanbanCompDateTime(workKanbans));
            kanbanInfoEntity.updateMember();

            KanbanRegistPreprocessContainer plugin = KanbanRegistPreprocessContainer.getInstance();
            if (Objects.nonNull(plugin)) {
                KanbanRegistPreprocessResultEntity resultEntity = plugin.kanbanRegistPreprocess(kanbanInfoEntity);
                if (!resultEntity.getResult()) {
                    Platform.runLater(() -> {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanbanTitle"), LocaleUtils.getString(resultEntity.getResultMessage()));
                    });
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            DialogBox.alert(ex);
            return false;
        }
    }

    /**
     * 保存前処理
     *
     * @return
     */
    private static Boolean registPreCheck(KanbanInfoEntity kanbanInfoEntity) {
        //未入力判定
        if (WorkPlanKanbanCheckerUtils.checkEmptyKanban(kanbanInfoEntity.getKanbanName(), kanbanInfoEntity.getPropertyCollection())) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
            return false;
        }
        switch (WorkPlanKanbanCheckerUtils.validItems(kanbanInfoEntity)) {
            case TIME_COMP_ERR:
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.TimeFormatErrMessage"));
                return false;
            case DATE_COMP_ERR:
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DateCompErrMessage"));
                return false;
            case SUCCSESS:
                logger.info("Clear KanbanData Validation.");
                break;
        }
        return true;
    }
}
