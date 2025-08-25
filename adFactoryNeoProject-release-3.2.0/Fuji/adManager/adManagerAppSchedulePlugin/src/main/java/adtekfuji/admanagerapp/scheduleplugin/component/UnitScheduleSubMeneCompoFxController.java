package adtekfuji.admanagerapp.scheduleplugin.component;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleSearcher;
import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleTypeEnum;
import adtekfuji.cash.CashManager;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 計画画面サブメニュークラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.24.Mon
 */
@FxComponent(id = "UnitScheduleSubMeneCompo", fxmlPath = "/fxml/compo/unitScheduleSubMeneCompo.fxml")
public class UnitScheduleSubMeneCompoFxController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(UnitScheduleSubMeneCompoFxController.class.getName() + ":initialize start");
        this.createCashDataThread();
        logger.info(UnitScheduleSubMeneCompoFxController.class.getName() + ":initialize end");
    }

    @FXML
    public void onShowUnitSuchedule(ActionEvent event) {
        logger.info(UnitScheduleSubMeneCompoFxController.class.getName() + ":onShowUnitSuchedule start");
        sc.setComponent("ContentNaviPane", "UnitScheduleCompo", ScheduleTypeEnum.UNIT_SCHEDULE);
        logger.info(UnitScheduleSubMeneCompoFxController.class.getName() + ":onShowUnitSuchedule end");
    }

    @FXML
    public void onShowWorkerSuchedule(ActionEvent event) {
        logger.info(UnitScheduleSubMeneCompoFxController.class.getName() + ":onShowWorkerSuchedule start");
        sc.setComponent("ContentNaviPane", "UnitScheduleCompo", ScheduleTypeEnum.ORGANIZATION_SCHEDULE);
        logger.info(UnitScheduleSubMeneCompoFxController.class.getName() + ":onShowWorkerSuchedule end");
    }

    @FXML
    public void onSetting(ActionEvent event) {
        logger.info(UnitScheduleSubMeneCompoFxController.class.getName() + ":onSetting start");
        sc.setComponent("ContentNaviPane", "UnitScheduleSettingCompo");
        logger.info(UnitScheduleSubMeneCompoFxController.class.getName() + ":onSetting end");
    }

    /**
     * キャッシュデータ読み込み用スレッド起動
     *
     */
    private void createCashDataThread() {
        sc.blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    CashManager cache = CashManager.getInstance();

                    // 組織をキャッシュ
                    CacheUtils.createCacheOrganization(true);
                    // 設備をキャッシュ
                    CacheUtils.createCacheEquipment(true);

                    // 生産ステータスの色をキャッシュ
                    cache.setNewCashList(DisplayedStatusInfoEntity.class);
                    List<DisplayedStatusInfoEntity> displaystatuses = ScheduleSearcher.getDisplayedStatus();
                    displaystatuses.stream().forEach((displaystatus) -> {
                        cache.setItem(DisplayedStatusInfoEntity.class, displaystatus.getStatusId(), displaystatus);
                    });
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    Platform.runLater(() -> {
                        sc.blockUI(false);
                    });
                }
                return null;
            }
        };
        new Thread(task).start();

    }
}
