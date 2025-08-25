/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.dialog;

import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.utils.KanbanCheckerUtils;
import jp.adtekfuji.forfujiapp.utils.KanbanStartCompDate;
import jp.adtekfuji.forfujiapp.utils.UnitCheckerUtils;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.ProductInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.plugin.KanbanRegistPreprocessContainer;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adFactory.utility.KanbanRegistPreprocessResultEntity;
import jp.adtekfuji.forfujiapp.common.UIControlInterface;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.forfujiapp.utils.CheckerUtilEntity;
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
public class DialogController {

    private final static Logger logger = LogManager.getLogger();
    private final static SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();

    /**
     * 選択したテーブルツリーのデータを編集
     *
     * @param entity
     * @param uiInterface
     */
    public static void showEditUnit(UnitInfoEntity entity, UIControlInterface uiInterface) {
        logger.info(DialogController.class.getName() + ":showEditUnit start");
        // ユニットを選択していたらユニット編集画面
        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "UnitDetailDialog", entity);
        while (ret.equals(ButtonType.OK)) {
            CheckerUtilEntity check = UnitCheckerUtils.isEmptyUnit(entity);
            if (!check.isSuccsess()) {
                sc.showAlert(check.getAlertType(), LocaleUtils.getString(check.getErrTitle()), LocaleUtils.getString(check.getErrMessage()));
            } else {
                break;
            }
            ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "UnitDetailDialog", entity);
        }
        if (ret.equals(ButtonType.OK)) {
            // 生産ユニットの新規作成処理
            entity.setFkUpdatePersonId(loginUser.getId());
            entity.setUpdateDatetime(new Date());
            UpdateUnitThread(entity, uiInterface);
        }
        // カンバンを選択していたらカンバン編集画面
        logger.info(DialogController.class.getName() + ":showEditUnit end");
    }

    /**
     * ユニット更新処理
     *
     * @param entity
     */
    private static void UpdateUnitThread(UnitInfoEntity entity, UIControlInterface uiInterface) {
        sc.blockUI(true);
        final UnitInfoEntity updateData = entity;
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    entity.entityUpdate();
                    ResponseEntity rs = RestAPI.updateUnit(entity);
                    if (!rs.isSuccess()) {
                        // 詳細画面表示
                        Platform.runLater(() -> {
                            showEditUnit(entity, uiInterface);
                        });
                    }
                    uiInterface.updateUI();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
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
     * 選択したテーブルツリーのデータを編集
     *
     * @param entity
     * @param uiInterface
     */
    public static void showEditKanban(KanbanInfoEntity entity, UIControlInterface uiInterface) {
        logger.info(DialogController.class.getName() + ":editKanban start");
        // ユニットを選択していたらユニット編集画面
        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "KanbanDetailDialog", entity);
        while (ret.equals(ButtonType.OK)) {
            if (KanbanCheckerUtils.isEmptyKanban(entity)) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
            } else {
                break;
            }
            ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "KanbanDetailDialog", entity);
        }
        if (ret.equals(ButtonType.OK)) {
            entity.setFkUpdatePersonId(loginUser.getId());
            entity.setUpdateDatetime(new Date());
            // 更新処理
            UpdateKanbanThread(entity, uiInterface);
        }
        // カンバンを選択していたらカンバン編集画面
        logger.info(DialogController.class.getName() + ":editKanban end");
    }

    /**
     * カンバン更新処理
     *
     * @param entity
     */
    private static void UpdateKanbanThread(KanbanInfoEntity entity, UIControlInterface uiInterface) {
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

        List<KanbanPropertyInfoEntity> kanbanProps = new LinkedList();
        if (Objects.nonNull(oldKanban.getPropertyCollection())) {
            oldKanban.getPropertyCollection().stream().forEach(c -> kanbanProps.add(c.clone()));
        }
        kanban.setPropertyCollection(kanbanProps);

        kanban.setLotQuantity(oldKanban.getLotQuantity());              // ロット数量
        kanban.setModelName(oldKanban.getModelName());                  // モデル名

        // 製品情報一覧
        List<ProductInfoEntity> products = null;
        if (Objects.nonNull(oldKanban.getProducts())) {
            products = new LinkedList();
            for (ProductInfoEntity oldProduct : oldKanban.getProducts()) {
                ProductInfoEntity product = new ProductInfoEntity();

                product.setProductId(oldProduct.getProductId());
                product.setUniqueId(oldProduct.getUniqueId());
                product.setFkKanbanId(oldProduct.getFkKanbanId());
                product.setCompDatetime(oldProduct.getCompDatetime());
                product.setStatus(oldProduct.getStatus());
                product.setDefectType(oldProduct.getDefectType());
                product.setOrderNum(oldProduct.getOrderNum());
                product.setOldStatus(oldProduct.getOldStatus());
                product.setDefectWorkName(oldProduct.getDefectWorkName());

                products.add(product);
            }
        }
        kanban.setProducts(products);

        kanban.setRepairNum(oldKanban.getRepairNum());                  // 補修数
        kanban.setProductionType(oldKanban.getProductionType());        // 生産タイプ

        // 追加情報をJSONにする
        String kanbanAddInfo = JsonUtils.objectsToJson(oldKanban.getPropertyCollection());
        kanban.setKanbanAddInfo(kanbanAddInfo);                         // 追加情報(JSON)

        kanban.setServiceInfo(oldKanban.getServiceInfo());              // サービス情報(JSON)

        kanban.setVerInfo(oldKanban.getVerInfo());                      // 排他用バーション

        kanban.setProductionNumber(oldKanban.getProductionNumber());    // 製造番号
        kanban.setApproval(oldKanban.getApproval());                    // 承認(JSON)
        kanban.setKanbanLabel(oldKanban.getKanbanLabel());              // ラベル(JSON)
        kanban.setUpdatePerson(oldKanban.getUpdatePerson());            // 更新者
        kanban.setLedgerPath(oldKanban.getLedgerPath());                // 帳票テンプレートパス(JSON)

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
            work.setKanbanName(oldWork.getKanbanName());                    // カンバン名
            work.setWorkflowName(oldWork.getWorkflowName());                // 工程順名

            work.setSeparateWorkFlag(oldWork.getSeparateWorkFlag());        // 追加工程フラグ
            work.setImplementFlag(oldWork.getImplementFlag());              // 実施フラグ
            work.setSkipFlag(oldWork.getSkipFlag());                        // スキップフラグ
            work.setStartDatetime(oldWork.getStartDatetime());              // 開始予定日時
            work.setCompDatetime(oldWork.getCompDatetime());                // 完了予定日時
            work.setTaktTime(oldWork.getTaktTime());                        // タクトタイム[ms]
            work.setSumTimes(oldWork.getSumTimes());                        // 作業累計時間[ms]
            work.setFkUpdatePersonId(oldWork.getFkUpdatePersonId());        // 更新者(組織ID)
            work.setUpdateDatetime(oldWork.getUpdateDatetime());            // 更新日時
            work.setWorkStatus(oldWork.getWorkStatus());                    // 工程ステータス
            work.setFkInterruptReasonId(oldWork.getFkInterruptReasonId());  // 中断理由ID
            work.setFkDelayReasonId(oldWork.getFkDelayReasonId());          // 遅延理由ID
            work.setWorkKanbanOrder(oldWork.getWorkKanbanOrder());          // 表示順

            // 工程カンバンプロパティ一覧
            List<WorkKanbanPropertyInfoEntity> workProps = new LinkedList();
            if (Objects.nonNull(oldWork.getPropertyCollection())) {
                oldWork.getPropertyCollection().stream().forEach(c -> workProps.add(c.clone()));
            }
            work.setPropertyCollection(workProps);

            work.setEquipmentCollection(oldWork.getEquipmentCollection());          // 設備ID一覧
            work.setOrganizationCollection(oldWork.getOrganizationCollection());    // 組織ID一覧

            work.setSerialNumber(oldWork.getSerialNumber());    // シリアル番号
            work.setSyncWork(oldWork.isSyncWork());             // 同時作業フラグ
            work.setActualNum1(oldWork.getActualNum1());        // A品実績数
            work.setActualNum2(oldWork.getActualNum2());        // B品実績数
            work.setActualNum3(oldWork.getActualNum3());        // C品実績数

            // 追加情報をJSONにする
            String workKanbanAddInfo = JsonUtils.objectsToJson(work.getPropertyCollection());
            work.setWorkKanbanAddInfo(workKanbanAddInfo);       // 追加情報(JSON)

            work.setServiceInfo(oldWork.getServiceInfo());      // サービス情報(JSON)

            work.setLastActualId(oldWork.getLastActualId());    // 最終実績ID

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

            kanbanInfoEntity.setStartDatetime(KanbanStartCompDate.getWorkKanbanStartDateTime(workKanbans));
            kanbanInfoEntity.setCompDatetime(KanbanStartCompDate.getWorkKanbanCompDateTime(workKanbans));
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
        if (KanbanCheckerUtils.checkEmptyKanban(kanbanInfoEntity.getKanbanName(), kanbanInfoEntity.getPropertyCollection())) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
            return false;
        }
        switch (KanbanCheckerUtils.validItems(kanbanInfoEntity)) {
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
