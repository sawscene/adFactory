/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingpluginfuji.component;

import adtekfuji.admanagerapp.monitorsettingpluginfuji.common.BreakTimeIdData;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.BreaktimeInfoFacade;
import adtekfuji.clientservice.DelayReasonInfoFacade;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.InterruptReasonInfoFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.master.DelayReasonInfoEntity;
import jp.adtekfuji.adFactory.entity.master.InterruptReasonInfoEntity;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.property.MonitorSettingFuji;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author fu-kato
 */
public abstract class AndonSettingController extends VBox {

    protected final Logger logger = LogManager.getLogger();
    protected final SceneContiner sc = SceneContiner.getInstance();
    protected final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    protected final CashManager cache = CashManager.getInstance();
    protected final EquipmentInfoFacade equipmentFacade = new EquipmentInfoFacade();
    protected final BreaktimeInfoFacade breaktimeFacade = new BreaktimeInfoFacade();
    protected final DelayReasonInfoFacade delayReasonFacade = new DelayReasonInfoFacade();
    protected final InterruptReasonInfoFacade interruptReasonFacade = new InterruptReasonInfoFacade();
    protected final AndonMonitorSettingFacade andonMonitorSettingFacade = new AndonMonitorSettingFacade();

    protected final List<String> delayReasonCollection = new ArrayList<>();
    protected final List<String> interruptReasonCollection = new ArrayList<>();
    protected final LinkedList<BreakTimeIdData> breaktimeIdCollection = new LinkedList<>();

    protected boolean isDeletedItems;

    private MonitorSettingCompoFxController componentController;

    /**
     * コンストラクタ
     */
    public AndonSettingController() {
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setSpacing(10.0);

        // 休憩時間一覧
        CacheUtils.createCacheBreakTime(true);

        // 遅延理由一覧
        CacheUtils.createCacheDelayReason(true);
        delayReasonCollection.clear();
        List<DelayReasonInfoEntity> delayReasons = cache.getItemList(DelayReasonInfoEntity.class, new ArrayList());
        if (Objects.nonNull(delayReasons) && !delayReasons.isEmpty()) {
            List<String> reasons = delayReasons.stream().map(p -> p.getDelayReason()).collect(Collectors.toList());
            Collections.sort(reasons);
            delayReasonCollection.addAll(reasons);
        }

        // 中案理由一覧
        CacheUtils.createCacheInterruptReason(true);
        interruptReasonCollection.clear();
        List<InterruptReasonInfoEntity> interruptReasons = cache.getItemList(InterruptReasonInfoEntity.class, new ArrayList());
        if (Objects.nonNull(interruptReasons) && !interruptReasons.isEmpty()) {
            List<String> reasons = interruptReasons.stream().map(p -> p.getInterruptReason()).collect(Collectors.toList());
            Collections.sort(reasons);
            interruptReasonCollection.addAll(reasons);
        }
    }

    /**
     * 設備マスター 又は、休憩時間マスターが削除されているかを返す。
     *
     * @return
     */
    public boolean isDeletedItems() {
        return this.isDeletedItems;
    }

    public abstract MonitorSettingFuji getInputResult();

    public abstract boolean isValidItems();

    public MonitorSettingCompoFxController getComponentController() {
        return componentController;
    }

    public void setComponentController(MonitorSettingCompoFxController componentController) {
        this.componentController = componentController;
    }
}
