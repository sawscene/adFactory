/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.dialog;

import adtekfuji.admanagerapp.andonsetting.utils.KanbanUtils;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン選択ダイアログ
 *
 * @author e-mori
 */
public class KanbanSelectDialog {

    private static final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();

    public static List<KanbanInfoEntity> getKanbans(List<Long> targets) {
        List<KanbanInfoEntity> base = kanbanInfoFacade.find(targets);

        return KanbanUtils.sortByIdList(base, targets);
    }

    /**
     * カンバン選択ダイアログを表示する
     *
     * @param event
     * @param selectedDate
     * @param useLiteHierarchy Lite階層指定
     * @return ボタン種類
     */
    public static ButtonType showDialog(ActionEvent event, Date selectedDate, Boolean useLiteHierarchy) {
        ButtonType ret = ButtonType.CANCEL;
        try {
            Button eventSrc = (Button) event.getSource();
            List<Long> targets = (List) eventSrc.getUserData();

            List<KanbanInfoEntity> kanbans = getKanbans(targets);

            // ダイアログ側に表示するカンバンの取得
            SelectDialogEntity<KanbanInfoEntity> selectDialogEntity = new SelectDialogEntity();

            if (Objects.nonNull(kanbans)) {
                selectDialogEntity.kanbans(kanbans, null, useLiteHierarchy);
            }

            // 検索条件はカンバン選択ダイアログ側で行う
            selectDialogEntity.enableInternalCondition(true);

            // ダイアログ側で設定したカンバンのIDを設定する
            SceneContiner sc = SceneContiner.getInstance();
            ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
            ret = sc.showComponentDialog(LocaleUtils.getString("key.Kanban"), "KanbanSelectionCompo", selectDialogEntity, (Stage) ((Node) event.getSource()).getScene().getWindow(), true);
            if (ret.equals(ButtonType.OK)) {
                kanbans.clear();
                if (!selectDialogEntity.getKanbans().isEmpty()) {
                    kanbans.addAll(selectDialogEntity.getKanbans());
                }
                eventSrc.setUserData(kanbans);
            }
        } catch (Exception ex) {
            Logger logger = LogManager.getLogger();
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * カンバンの検索条件を決定する
     *
     * @param selectedDate 検索期間
     * @return
     */
    private static KanbanSearchCondition createKanbanSearchConditon(Date selectedDate) {
        // 検索条件を決めるが現在はすべてのステータス・選択した日付の0時から24時までを条件としている

        KanbanSearchCondition condition = new KanbanSearchCondition();
        ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

        List<KanbanStatusEnum> selectStatusData = new ArrayList<>();
        ObservableList<String> stateList = FXCollections.observableArrayList(KanbanStatusEnum.getMessages(rb));
        selectStatusData.addAll(Arrays.asList(KanbanStatusEnum.values()));

        condition.setKanbanStatusCollection(selectStatusData);

        if (Objects.nonNull(selectedDate)) {
            Long selectedDateTimeMillis;
            selectedDateTimeMillis = selectedDate.getTime();
            condition.setFromDate(new Date(selectedDateTimeMillis));
            condition.setToDate(new Date(selectedDateTimeMillis + (23 * 60 * 60 + 59 * 60 + 59) * 1000));
        }

        return condition;
    }
}
