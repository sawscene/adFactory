/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin.component;

import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleCellSizeTypeEnum;
import adtekfuji.admanagerapp.scheduleplugin.schedule.cell.ScheduleCellSizeCellFactory;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.dialog.entity.SelectDialogEntity;
import jp.adtekfuji.forfujiapp.dialog.entity.SelectedTableData;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.forfujiapp.javafx.list.cell.OrganizationListItemCell;
import jp.adtekfuji.forfujiapp.javafx.list.cell.UnitTemplateSelectedTabelDataListItemCell;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.IndexedCheckModel;

/**
 * 設定画面クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.24.thr
 */
@FxComponent(id = "UnitScheduleSettingCompo", fxmlPath = "/fxml/compo/unitScheduleSettingCompo.fxml")
public class UnitScheduleSettingCompoFxController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle RB = LocaleUtils.getBundle("locale.locale");
    private final Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);

    private final ObservableList<SelectedTableData<UnitTemplateInfoEntity>> selectTemplates = FXCollections.observableArrayList();
    private List<OrganizationInfoEntity> selectOrganizations = new ArrayList<>();

    private final String COMMA_SPLIT = ",";

    @FXML
    private ComboBox<ScheduleCellSizeTypeEnum> scheduleCellSizeCombo;
    @FXML
    private CheckListView<String> selectTitleList;
    @FXML
    private ListView<SelectedTableData<UnitTemplateInfoEntity>> unitTemplateList;
    @FXML
    private ListView<OrganizationInfoEntity> organizationList;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(UnitScheduleSettingCompoFxController.class.getName() + ":initialize start");
        Callback<ListView<OrganizationInfoEntity>, ListCell<OrganizationInfoEntity>> organizationCellFactory = (ListView<OrganizationInfoEntity> param) -> new OrganizationListItemCell();
        organizationList.setCellFactory(organizationCellFactory);
        Callback<ListView<SelectedTableData<UnitTemplateInfoEntity>>, ListCell<SelectedTableData<UnitTemplateInfoEntity>>> unittemlateCellFactory3 = (ListView<SelectedTableData<UnitTemplateInfoEntity>> param) -> new UnitTemplateSelectedTabelDataListItemCell();
        unitTemplateList.setCellFactory(unittemlateCellFactory3);
        this.loadSetting();
        logger.info(UnitScheduleSettingCompoFxController.class.getName() + ":initialize end");
    }

    /**
     * ユニット選択用ダイアログの表示
     *
     * @param event
     */
    @FXML
    public void onSelectUnitTemplate(ActionEvent event) {
        logger.info(UnitScheduleSettingCompoFxController.class.getName() + ":onSelectUnitTemplate start");
        sc.blockUI(Boolean.TRUE);
        try {
            SelectDialogEntity<SelectedTableData<UnitTemplateInfoEntity>> selectDialogEntity = new SelectDialogEntity().multiSelectItems(selectTemplates);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.unittemplate"), "UnitTemplateMultiSelectionCompo", selectDialogEntity);
            if (ret.equals(ButtonType.OK)) {

                Platform.runLater(() -> {
                    unitTemplateList.getItems().clear();
                    unitTemplateList.getItems().addAll(selectTemplates);
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            sc.blockUI(Boolean.FALSE);
        }
        logger.info(UnitScheduleSettingCompoFxController.class.getName() + ":onSelectUnitTemplate end");
    }

    /**
     * 作業者選択ダイアログの表示
     *
     * @param event
     */
    @FXML
    public void onSelectOrganization(ActionEvent event) {
        logger.info(UnitScheduleSettingCompoFxController.class.getName() + ":onSelectOrganization start");
        sc.blockUI(Boolean.TRUE);
        try {
            List<OrganizationInfoEntity> org = selectOrganizations;
            jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity selectDialogEntity
                    = new jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity().organizations(selectOrganizations);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialogEntity);
            if (ret.equals(ButtonType.OK)) {
                selectOrganizations = selectDialogEntity.getOrganizations();

                Platform.runLater(() -> {
                    organizationList.getItems().clear();
                    organizationList.getItems().addAll(selectOrganizations);
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            sc.blockUI(Boolean.FALSE);
        }
        logger.info(UnitScheduleSettingCompoFxController.class.getName() + ":onSelectOrganization end");
    }

    /**
     * 登録処理を行う
     *
     * @param event
     */
    @FXML
    public void onRegistButton(ActionEvent event) {
        logger.info(UnitScheduleSettingCompoFxController.class.getName() + ":onRegistButton start");
        this.storeSetting();
        logger.info(UnitScheduleSettingCompoFxController.class.getName() + ":onRegistButton end");
    }

    /**
     * 各種設定読み込み処理後で外だし
     *
     */
    private void loadSetting() {
        logger.info(UnitScheduleSettingCompoFxController.class.getName() + ":loadSearchSetting start");
        sc.blockUI(Boolean.TRUE);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    loadSchduleCellSizeSetting();
                    loadTitleDisplaySetting();
                    loadShowUnitTemplateSetting();
                    loadShowOrganizationSetting();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    sc.blockUI(Boolean.FALSE);
                }
                return null;
            }
        };
        new Thread(task).start();
        logger.info(UnitScheduleSettingCompoFxController.class.getName() + ":loadSearchSetting end");
    }

    /**
     * 検索設定書き込み処理
     *
     */
    private void storeSetting() {
        logger.info(UnitScheduleSettingCompoFxController.class.getName() + ":storeSearchSetting start");

        try {
            this.properties.setProperty(ClientPropertyConstants.PROP_KEY_SCEDULE_CELL_SIZE, scheduleCellSizeCombo.getSelectionModel().getSelectedItem().name());
            ObservableList<String> selectTempTitle = selectTitleList.getCheckModel().getCheckedItems();
            StringBuilder selectTempTitles = new StringBuilder();
            for (int i = 0; i < selectTempTitle.size(); i++) {
                selectTempTitles.append(selectTempTitle.get(i));
                if ((i + 1) < selectTempTitle.size()) {
                    selectTempTitles.append(",");
                }
            }
            this.properties.setProperty(ClientPropertyConstants.PROP_KEY_SELECT_TITLE, selectTempTitles.toString());
            StringBuilder tempIds = new StringBuilder();
            for (int i = 0; i < selectTemplates.size(); i++) {
                tempIds.append(selectTemplates.get(i).getItem().getUnitTemplateId());
                if ((i + 1) < selectTemplates.size()) {
                    tempIds.append(",");
                }
            }
            this.properties.setProperty(ClientPropertyConstants.PROP_KEY_SELECT_UNITTEMPLATE, tempIds.toString());
            StringBuilder orgIds = new StringBuilder();
            for (int i = 0; i < selectOrganizations.size(); i++) {
                orgIds.append(selectOrganizations.get(i).getOrganizationId());
                if ((i + 1) < selectOrganizations.size()) {
                    orgIds.append(",");
                }
            }
            this.properties.setProperty(ClientPropertyConstants.PROP_KEY_SELECT_ORGANIZATION, orgIds.toString());
            AdProperty.store(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(UnitScheduleSettingCompoFxController.class.getName() + ":storeSearchSetting end");
    }

    /**
     * 表示するスケジュールの日付サイズの読み込み
     *
     */
    private void loadSchduleCellSizeSetting() {
        //設定を読み込んで検索条件保持用クラスにデータを入れる
        Callback<ListView<ScheduleCellSizeTypeEnum>, ListCell<ScheduleCellSizeTypeEnum>> comboCellFactory = (ListView<ScheduleCellSizeTypeEnum> param) -> new ScheduleCellSizeCellFactory();
        scheduleCellSizeCombo.setButtonCell(new ScheduleCellSizeCellFactory());
        scheduleCellSizeCombo.setCellFactory(comboCellFactory);
        scheduleCellSizeCombo.setItems(FXCollections.observableArrayList(ScheduleCellSizeTypeEnum.values()));
        Platform.runLater(() -> {
            scheduleCellSizeCombo.setValue(ScheduleCellSizeTypeEnum.getEnum(this.properties.getProperty(ClientPropertyConstants.PROP_KEY_SCEDULE_CELL_SIZE, ScheduleCellSizeTypeEnum.MONTHLY.name())));
        });

    }

    /**
     * 月次生産計画のタイトル表示設定の読み込み
     *
     */
    private void loadTitleDisplaySetting() {
        String tempProperty = properties.getProperty(ClientPropertyConstants.PROP_KEY_ITEM_TITLE, ClientPropertyConstants.DEFAULT_MASTER_ITEM_TITLES);
        String[] tempPropertys = tempProperty.split(",");
        ObservableList<String> stateList = FXCollections.observableArrayList(tempPropertys);
        selectTitleList.setItems(stateList);
        String selectTempPropety = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_TITLE, "");
        if (!selectTempPropety.isEmpty()) {
            String[] selectTempPropData = selectTempPropety.split(COMMA_SPLIT);
            IndexedCheckModel<String> cm = selectTitleList.getCheckModel();
            for (String selectTempProp : selectTempPropData) {
                for (String tempPropItem : tempPropertys) {
                    if (selectTempProp.equals(tempPropItem)) {
                        cm.check(tempPropItem);
                    }
                }
            }
        }
    }

    /**
     * 表示する機種の読み込み
     *
     */
    private void loadShowUnitTemplateSetting() {
        String text = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_UNITTEMPLATE, "0");
        String[] values = text.split(",");
        List<Long> unitTemplateIds = new ArrayList<>();
        for (String value : values) {
            if (value.isEmpty()) {
                continue;
            }
            Long unitTemplateId = Long.parseLong(value);
            if (unitTemplateId != 0L) {
                unitTemplateIds.add(unitTemplateId);
            }
        }

        if (!unitTemplateIds.isEmpty()) {
            List<UnitTemplateInfoEntity> entities = RestAPI.getUnitTemplate(unitTemplateIds);
            for (UnitTemplateInfoEntity entity : entities) {
                this.selectTemplates.add(new SelectedTableData<>(entity.getUnitTemplateName(), entity));
            }
        }

        Platform.runLater(() -> {
            unitTemplateList.getItems().clear();
            unitTemplateList.getItems().addAll(selectTemplates);
        });
    }

    /**
     * 表示する組織設定の読み込み
     *
     */
    private void loadShowOrganizationSetting() {
        String org = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_ORGANIZATION, "0");
        String[] orgs = org.split(",");

        List<Long> organizationIds = Arrays.stream(orgs).map(Long::valueOf).collect(Collectors.toList());
        List<OrganizationInfoEntity> organizations = CacheUtils.getCacheOrganization(organizationIds);

        selectOrganizations.addAll(organizations);

        Platform.runLater(() -> {
            organizationList.getItems().clear();
            organizationList.getItems().addAll(selectOrganizations);
        });
    }
}
