/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.component;

import adtekfuji.admanagerapp.andonsetting.component.lite.LiteMonitorSettingController;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.search.EquipmentSearchCondition;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.enumerate.AndonMonitorTypeEnum;
import jp.adtekfuji.andon.property.AgendaMonitorSetting;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.utility.CfgFileUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 進捗モニタ設定コンポーネント
 *
 * @author e-mori
 */
@FxComponent(id = "AndonSettingCompo", fxmlPath = "/fxml/compo/andon_setting_compo.fxml")
public class AndonSettingCompoFxController implements Initializable, ArgumentDelivery, ComponentHandler {

    class AndonMonitorTypeComboBoxCellFactory extends ListCell<AndonMonitorTypeEnum> {

        @Override
        protected void updateItem(AndonMonitorTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
            }
        }
    }

    Callback<ListView<AndonMonitorTypeEnum>, ListCell<AndonMonitorTypeEnum>> comboCellFactory = (ListView<AndonMonitorTypeEnum> param) -> new AndonMonitorTypeComboBoxCellFactory();

    private final Logger logger = LogManager.getLogger();
    private final long maxLoad = ClientServiceProperty.getRestRangeNum();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final AndonMonitorSettingFacade monitorSettingFacade = new AndonMonitorSettingFacade();
    private long monitorId;
    private final boolean isMonitorSetting = ClientServiceProperty.isLicensed(LicenseOptionType.MonitorSettingEditor.getName());
    private final boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());

    private AndonSettingController controller;
    private AndonMonitorLineProductSetting setting = null;

    // 最初に表示された情報
    private AndonMonitorLineProductSetting cloneSetting;

    // 変更保存時データベースからの読み込みを待機させるラッチ
    private CountDownLatch latch;

    private final Set<Object> blockUIs = new HashSet();

    @FXML
    private SplitPane andonSettingPane;
    @FXML
    private ListView<EquipmentInfoEntity> monitorList;
    @FXML
    private ComboBox<AndonMonitorTypeEnum> monitorTypeCombo;
    @FXML
    private Pane progressPane;
    @FXML
    private ScrollPane settingPane;

    /**
     * モニター種別のリスナー
     */
    private final ChangeListener<AndonMonitorTypeEnum> monitorTypeListener = (ObservableValue<? extends AndonMonitorTypeEnum> observable, AndonMonitorTypeEnum oldValue, AndonMonitorTypeEnum newValue) -> {
        try {
            if (0 != monitorId && !Objects.equals(oldValue, newValue)) {
                this.registConfirm(false);
                this.createScene(monitorId, newValue, true);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(true);

        SplitPaneUtils.loadDividerPosition(andonSettingPane, getClass().getSimpleName());

        monitorList.setCellFactory((ListView<EquipmentInfoEntity> param) -> {
            ListCell<EquipmentInfoEntity> cell = new ListCell<EquipmentInfoEntity>() {
                @Override
                protected void updateItem(EquipmentInfoEntity e, boolean bln) {
                    try {
                        super.updateItem(e, bln);
                        if (Objects.nonNull(e)) {
                            setText(e.getEquipmentName() + "(" + e.getEquipmentIdentify() + ")");
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }
            };
            return cell;
        });

        monitorList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends EquipmentInfoEntity> observable, EquipmentInfoEntity oldValue, EquipmentInfoEntity newValue) -> {
            try {
                if (Objects.nonNull(newValue) && oldValue != newValue) {
                    logger.info("select:{}", newValue);

                    this.registConfirm(false);

                    //変更を保存中のとき終わるまで待機する
                    if (Objects.nonNull(latch)) {
                        try {
                            latch.await();
                        } catch (Exception e) {
                            logger.fatal(e, e);
                        }
                    }

                    monitorId = newValue.getEquipmentId();

                    this.clearDetailView();

                    // 進捗モニター設定を読み込む
                    this.createScene(monitorId, null, true);
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });

        // モニター種別設定 ライセンスによって選択肢を変える
        if (isMonitorSetting) {
            // 進捗モニター設定オプションが有効
            this.monitorTypeCombo.setItems(FXCollections.observableArrayList(Arrays.asList(AndonMonitorTypeEnum.LINE_PRODUCT, AndonMonitorTypeEnum.AGENDA, AndonMonitorTypeEnum.LITE_MONITOR)));
        } else if (isLiteOption) {
            // Lite オプションのみが有効
            this.monitorTypeCombo.setItems(FXCollections.observableArrayList(Arrays.asList(AndonMonitorTypeEnum.LITE_MONITOR)));
        }

        this.monitorTypeCombo.setButtonCell(new AndonMonitorTypeComboBoxCellFactory());
        this.monitorTypeCombo.setCellFactory(comboCellFactory);
        this.monitorTypeCombo.setEditable(false);

        // 進捗モニタ設備リストを更新する。
        updateMonitorList();
    }

    /**
     * 変更を確認して保存する
     *
     * @return キャンセルを押した、または保存に失敗した場合false
     */
    private boolean registConfirm(boolean isDispCancel) {
        String title = null;
        String message = null;
        if (isChanged()) {
            // 入力内容が保存されていません。保存しますか?
            title = LocaleUtils.getString("key.confirm");
            message = LocaleUtils.getString("key.confirm.destroy");
        } else if (isDeletedItems()) {
            // 存在しない設備・休憩時間の割当が削除されています。保存しますか?
            title = LocaleUtils.getString("key.confirm");
            message = LocaleUtils.getString("key.confirm.monitorsetting2");
        }

        if (Objects.nonNull(message)) {
            ButtonType buttonType;
            if (isDispCancel) {
                buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            } else {
                buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
            }

            if (ButtonType.YES == buttonType) {
                return registData();
            } else if (ButtonType.CANCEL == buttonType) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setArgument(Object argument) {
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        progressPane.setVisible(flg);
    }

    /**
     *
     * @param obj
     * @param flg
     */
    public void blockUI(Object obj, Boolean flg) {
        if (flg) {
            blockUIs.add(obj);
        } else {
            blockUIs.remove(obj);
        }

        blockUI(!blockUIs.isEmpty());
    }

    /**
     * 進捗モニタ設備リストを更新する。
     */
    private void updateMonitorList() {
        logger.info("updateMonitorList start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);

            Task task = new Task<SortedList<EquipmentInfoEntity>>() {
                @Override
                protected SortedList<EquipmentInfoEntity> call() throws Exception {
                    EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();

                    // 進捗モニター設備取得
                    List<EquipmentInfoEntity> monitors = new ArrayList<>();
                    EquipmentSearchCondition condition = new EquipmentSearchCondition().equipmentType(EquipmentTypeEnum.MONITOR);
                    long max = equipmentInfoFacade.countSearch(condition);
                    for (long count = 0; count <= max; count += maxLoad) {
                        monitors.addAll(equipmentInfoFacade.findSearchRange(condition, count, count + maxLoad - 1));
                    }

                    SortedList<EquipmentInfoEntity> sortedList = new SortedList<>(FXCollections.observableArrayList(monitors));
                    sortedList.setComparator((EquipmentInfoEntity o1, EquipmentInfoEntity o2) -> o1.getEquipmentName().compareTo(o2.getEquipmentName()));

                    return sortedList;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // リストを更新する。
                        monitorList.setItems(this.get());

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                        logger.info("updateMonitorList end.");
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
    }

    /**
     * 画面をクリアする
     *
     */
    private void clearDetailView() {
        cloneSetting = null;//クローンをクリア　何も表示されてないことを明確にする。
    }

    /**
     * モニター種別に応じた設定画面を表示する
     *
     * @param equipmentId 作成する進捗モニター設備ID
     * @param monitorType
     * @param isUpdateClone
     */
    private void createScene(long equipmentId, AndonMonitorTypeEnum monitorType, boolean isUpdateClone) {
        logger.info("createScene: {}", equipmentId);
        Object obj = new Object();
        try {
            blockUI(obj, true);

            // ラインモニターまたはアジェンダモニター画面で画面blockを利用するため自分自身を渡す
            final AndonSettingCompoFxController self = this;

            Task task = new Task<AndonSettingController>() {
                @Override
                protected AndonSettingController call() throws Exception {

                    AndonMonitorTypeEnum type = monitorType;

                    if (Objects.isNull(monitorType)) {
                        // リスト変更時あるいはインポート時 新しくエンティティを取得
                        setting = (AndonMonitorLineProductSetting) monitorSettingFacade
                                .getLineSetting(equipmentId, AndonMonitorLineProductSetting.class);
                        type = Objects.nonNull(setting.getMonitorType()) ? setting.getMonitorType() : AndonMonitorTypeEnum.LINE_PRODUCT;
                    } else {
                        // モニター種別変更時 取得済みのエンティティを使用
                        setting.setMonitorType(monitorType);
                    }

                    // レイアウト設定が存在しないなら初期値を設定する
                    final String initialLayoutCfg = System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + "InitialLayoutInfo";
                    if (Objects.isNull(setting.getLayout())) {
                        setting.setLayout(CfgFileUtils.loadInternal(Paths.get(initialLayoutCfg), CfgFileUtils.CFG_LAYOUT2_INI));
                    }
                    if (Objects.isNull(setting.getCustomizeToolLayout())) {
                        setting.setCustomizeToolLayout(CfgFileUtils.loadInternal(Paths.get(initialLayoutCfg), CfgFileUtils.CFG_CUSTOMIZETOOL_LAYOUT_XML));
                    }

                    switch (type) {
                        case LINE_PRODUCT:
                            return new LineMonitorSettingController(self, equipmentId, setting);
                        case AGENDA:
                            if (Objects.isNull(setting.getAgendaMonitorSetting())) {
                                setting.setAgendaMonitorSetting(AgendaMonitorSetting.create());
                            }
                            return new AgendaMonitorSettingController(self, equipmentId, setting.getAgendaMonitorSetting(), null, true);
                        default:
                        case LITE_MONITOR:
                            if (Objects.isNull(setting.getAgendaMonitorSetting())) {
                                setting.setAgendaMonitorSetting(AgendaMonitorSetting.create());
                            }
                            return new LiteMonitorSettingController(self, equipmentId, setting.getAgendaMonitorSetting(), null);
                    }
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        controller = this.getValue();
                        settingPane.setContent(controller);

                        monitorTypeCombo.valueProperty().removeListener(monitorTypeListener);
                        monitorTypeCombo.getSelectionModel().select(setting.getMonitorType());
                        monitorTypeCombo.valueProperty().addListener(monitorTypeListener);

                        // 最初に表示された情報をコピー
                        // インポートした場合は単なる記入と同じ扱いのためcloneを作成しない
                        if (isUpdateClone) {
                            AndonMonitorLineProductSetting setting = getRegistData();
                            if (Objects.nonNull(setting)) {
                                cloneSetting = setting.clone();
                            }
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                        logger.info("createScene end.");
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
    }

    @FXML
    private void onImportAction(ActionEvent event) {
        logger.info("onImportAction:{}", monitorId);
        importSettings();
    }

    /**
     * モニター設定をインポートする
     *
     * @return
     */
    private boolean importSettings() {
        logger.info("importSettings:{}", monitorId);

        if (Objects.isNull(monitorId)) {
            return false;
        }

        // インポートダイアログ表示
        EquipmentInfoEntity importSourceEntity = new EquipmentInfoEntity();
        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Import"), "ImportMonitorSelectionCompo", importSourceEntity);

        // 選択中のものと、インポート元の装置IDが異なる場合のみ、設定を読み込む
        if (ret.equals(ButtonType.OK) && Objects.nonNull(importSourceEntity.getEquipmentId())
                && !Objects.equals(monitorId, importSourceEntity.getEquipmentId())) {
            createScene(importSourceEntity.getEquipmentId(), null, false);
        }

        return true;
    }

    /**
     * 登録
     *
     * @param event
     */
    @FXML
    private void onRegistAction(ActionEvent event) {
        registData();
    }

    /**
     * 保存を実施する
     *
     * @return 保存を実施したらtrue　保存が実施できなかったときfalse
     */
    private boolean registData() {
        logger.info("registData start: {}", this.monitorId);
        boolean isCancel = true;
        Object obj = new Object();
        try {
            blockUI(obj, true);

            if (Objects.isNull(this.monitorId) || Objects.isNull(controller) || !controller.isValidItems()) {
                return false;
            }

            this.latch = new CountDownLatch(1);

            final long targetMonitorId = this.monitorId;
            final AndonMonitorLineProductSetting targetSetting = controller.getInputResult();

            cloneSetting = targetSetting.clone();

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        monitorSettingFacade.setLineSetting(targetMonitorId, targetSetting);
                        return null;
                    } finally {
                        latch.countDown();
                    }
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    blockUI(obj, false);
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                    }
                }
            };
            new Thread(task).start();

            isCancel = false;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        } finally {
            if (isCancel) {
                blockUI(obj, false);
            }
        }

        return true;
    }

    /**
     * 現在フィールドに表示されている情報を取得する
     *
     * @return
     */
    private AndonMonitorLineProductSetting getRegistData() {
        logger.info("getRegistData:{},{}", monitorId, setting);

        if (Objects.isNull(monitorId) || Objects.isNull(controller)) {
            return null;
        }

        try {
            return controller.getInputResult();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return null;
    }

    /**
     * 最初に表示した情報から変更がないか調べる
     *
     * @return
     */
    private boolean isChanged() {
        AndonMonitorLineProductSetting currentSetting = getRegistData();

        if (Objects.isNull(currentSetting) || Objects.isNull(cloneSetting)) {
            return false;
        }

        if (currentSetting.equalsDisplayInfo(cloneSetting)) {
            return false;
        }

        return true;
    }

    /**
     * 設備マスター 又は、休憩時間マスターが削除されているかを返す。
     *
     * @return
     */
    private boolean isDeletedItems() {
        if (Objects.isNull(monitorId) || Objects.isNull(controller)) {
            return false;
        }

        return controller.isDeletedItems();
    }

    /**
     * コンポーネントが破棄される前に呼び出される。破棄できない場合は、falseを返す。 ほかの画面に遷移するとき変更が存在するなら保存するか確認する
     *
     * @return
     */
    @Override
    public boolean destoryComponent() {
        try {
            logger.info("destoryComponent start.");

            SplitPaneUtils.saveDividerPosition(andonSettingPane, getClass().getSimpleName());

            return registConfirm(true);
        } finally {
            logger.info("destoryComponent end.");
        }
    }
}
