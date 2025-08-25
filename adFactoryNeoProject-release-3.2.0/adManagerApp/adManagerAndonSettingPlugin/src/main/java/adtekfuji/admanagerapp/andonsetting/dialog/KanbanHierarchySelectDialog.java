/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.dialog;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン階層選択ダイアログ
 *
 * @author yu.nara
 */
public class KanbanHierarchySelectDialog {

    /**
     * 
     * @param event
     * @param useLiteHierarchy Lite階層指定
     * @return 
     */
    public static ButtonType showDialog(ActionEvent event, boolean useLiteHierarchy) {
        ButtonType ret = ButtonType.CANCEL;
        try {
            Button eventSrc = (Button) event.getSource();
            List<Long> targets = (List) eventSrc.getUserData();

            List<KanbanHierarchyInfoEntity> kanbanHierarchies = CacheUtils.getCacheKanbanHierarchy(targets);

            SelectDialogEntity<KanbanHierarchyInfoEntity> selectDialogEntity = new SelectDialogEntity();

            if (Objects.nonNull(kanbanHierarchies)) {
                selectDialogEntity.kanbanHierarchies(kanbanHierarchies, useLiteHierarchy);
            }

            ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
            SceneContiner sc = SceneContiner.getInstance();

            ret = sc.showComponentDialog(LocaleUtils.getString("key.KanbanHierarch"), "KanbanHierarchySelectionCompo", selectDialogEntity, (Stage) ((Node) event.getSource()).getScene().getWindow(), true);
            if (ret.equals(ButtonType.OK)) {
                kanbanHierarchies.clear();

                if (!selectDialogEntity.getKanbanHierarchies().isEmpty()) {
                    kanbanHierarchies.addAll(selectDialogEntity.getKanbanHierarchies());
                }
                eventSrc.setUserData(kanbanHierarchies);
            }
        } catch (Exception ex) {
            Logger logger = LogManager.getLogger();
            logger.fatal(ex, ex);
        }
        return ret;
    }
}
