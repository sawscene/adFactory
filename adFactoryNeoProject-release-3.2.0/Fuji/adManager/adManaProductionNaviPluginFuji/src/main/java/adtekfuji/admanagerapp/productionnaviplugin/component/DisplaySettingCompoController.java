/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleCellSizeTypeEnum;
import adtekfuji.admanagerapp.productionnaviplugin.schedule.cell.WorkPlanScheduleCellSizeCellFactory;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.locale.LocaleUtils;
import java.io.IOException;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.IndexedCheckModel;

/**
 * 設定画面クラス
 *
 * @author (TST)H.Nishimrua
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "DisplaySettingCompo", fxmlPath = "/fxml/compo/displaySettingCompo.fxml")
public class DisplaySettingCompoController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle RB = LocaleUtils.getBundle("locale.locale");

    // adFactoryREST クライアント
    private final DisplayedStatusInfoFacade displayedStatusInfoFacade = new DisplayedStatusInfoFacade();
    private final OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();

    private List<OrganizationInfoEntity> selectOrganizations = new ArrayList<>();

    private final String COMMA_SPLIT = ",";

    /**
     * 予定表示サイズ
     */
    @FXML
    private ComboBox<WorkPlanScheduleCellSizeTypeEnum> scheduleCellSizeCombo;
    /**
     * タイトルの表示設定一覧
     */
    @FXML
    private CheckListView<String> selectTitleList;
    /**
     * 組織一覧
     */
//    @FXML
//    private ListView<OrganizationInfoEntity> organizationList;
    /**
     * 休日の文字色
     */
    @FXML
    private ColorPicker holidayColerChar;
    /**
     * 休日の背景色
     */
    @FXML
    private ColorPicker holidayColerBack;
    /**
     * 予定ありの文字色
     */
    @FXML
    private ColorPicker plansColerChar;
    /**
     * 予定ありの背景色
     */
    @FXML
    private ColorPicker plansColerBack;
    /**
     * 予定なしの文字色
     */
    @FXML
    private ColorPicker noneColerChar;
    /**
     * 予定なしの背景色
     */
    @FXML
    private ColorPicker noneColerBack;
            
    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(":initialize start");

        try{
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

//            Callback<ListView<OrganizationInfoEntity>, ListCell<OrganizationInfoEntity>> organizationCellFactory = (ListView<OrganizationInfoEntity> param) -> new OrganizationListItemCell();
//            organizationList.setCellFactory(organizationCellFactory);

            this.loadSetting(prop);
        } catch (IOException ex) {
            logger.error(ex,ex);
        }

        logger.info(":initialize end");
    }


    /**
     * 作業者選択ダイアログの表示
     *
     * @param event
     */
//    @FXML
//    public void onSelectOrganization(ActionEvent event) {
//        logger.info(":onSelectOrganization start");
//        sc.blockUI(Boolean.TRUE);
//        try {
////            List<OrganizationInfoEntity> org = selectOrganizations;
////            jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity a = new jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity();
////            SelectDialogEntity selectDialogEntity = a.organizations(selectOrganizations);
//            SelectDialogEntity selectDialogEntity
//                    = new jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity().organizations(selectOrganizations);
//            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialogEntity);
//            if (ret.equals(ButtonType.OK)) {
//                selectOrganizations = selectDialogEntity.getOrganizations();
//
//                Platform.runLater(() -> {
//                    organizationList.getItems().clear();
//                    organizationList.getItems().addAll(selectOrganizations);
//                });
//            }
//        } catch (Exception ex) {
//            logger.fatal(ex, ex);
//        } finally {
//            sc.blockUI(Boolean.FALSE);
//        }
//        logger.info(":onSelectOrganization end");
//    }

    /**
     * 登録処理を行う
     *
     * @param event
     */
    @FXML
    public void onRegistButton(ActionEvent event) {
        logger.info(":onRegistButton start");
        try {
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            
            this.storeSetting(prop);
        } catch (IOException ex) {
            logger.error(ex,ex);
        }
        logger.info(":onRegistButton end");
    }

    /**
     * 各種設定読み込み処理後で外だし
     *s
     */
    private void loadSetting(Properties prop) {
        logger.info(":loadSearchSetting start");
//        sc.blockUI(Boolean.TRUE);

        String color = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_HOLIDAY_COLOR_CHAR, ProductionNaviPropertyConstants.INIT_SETTING_HOLIDAY_COLOR_CHAR);
        holidayColerChar.setValue(Color.web(color));
        color = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_HOLIDAY_COLOR_BACK, ProductionNaviPropertyConstants.INIT_SETTING_HOLIDAY_COLOR_BACK);
        holidayColerBack.setValue(Color.web(color));
        color = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_PLANS_COLOR_CHAR, ProductionNaviPropertyConstants.INIT_SETTING_PLANS_COLOR_CHAR);
        plansColerChar.setValue(Color.web(color));
        color = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_PLANS_COLOR_BACK, ProductionNaviPropertyConstants.INIT_SETTING_PLANS_COLOR_BACK);
        plansColerBack.setValue(Color.web(color));
        color = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_NONE_COLOR_CHAR, ProductionNaviPropertyConstants.INIT_SETTING_NONE_COLOR_CHAR);
        noneColerChar.setValue(Color.web(color));
        color = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_NONE_COLOR_BACK, ProductionNaviPropertyConstants.INIT_SETTING_NONE_COLOR_BACK);
        noneColerBack.setValue(Color.web(color));
  
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    loadSchduleCellSizeSetting(prop);
                    loadTitleDisplaySetting(prop);
//                    loadShowOrganizationSetting(prop);
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    sc.blockUI(Boolean.FALSE);
                }
                return null;
            }
        };
        new Thread(task).start();
        logger.info(":loadSearchSetting end");
    }

    /**
     * 検索設定書き込み処理
     *
     */
    private void storeSetting(Properties prop) {
        logger.info(":storeSearchSetting start");

        try {
            // 予定表示サイズ
            prop.setProperty(ProductionNaviPropertyConstants.KEY_SETTING_SCEDULE_CELL_SIZE, scheduleCellSizeCombo.getSelectionModel().getSelectedItem().name());
            // タイトルの表示設定
            ObservableList<String> selectTempTitle = selectTitleList.getCheckModel().getCheckedItems();
            StringBuilder selectTempTitles = new StringBuilder();
            for (int i = 0; i < selectTempTitle.size(); i++) {
                selectTempTitles.append(selectTempTitle.get(i));
                if ((i + 1) < selectTempTitle.size()) {
                    selectTempTitles.append(",");
                }
            }
            prop.setProperty(ProductionNaviPropertyConstants.KEY_SETTING_TITLE, selectTempTitles.toString());

            // 休日・予定あり・予定なしの文字色と背景色
            prop.setProperty(ProductionNaviPropertyConstants.KEY_SETTING_HOLIDAY_COLOR_CHAR, this.holidayColerChar.getValue().toString());
            prop.setProperty(ProductionNaviPropertyConstants.KEY_SETTING_HOLIDAY_COLOR_BACK, this.holidayColerBack.getValue().toString());
            prop.setProperty(ProductionNaviPropertyConstants.KEY_SETTING_PLANS_COLOR_CHAR, this.plansColerChar.getValue().toString());
            prop.setProperty(ProductionNaviPropertyConstants.KEY_SETTING_PLANS_COLOR_BACK, this.plansColerBack.getValue().toString());
            prop.setProperty(ProductionNaviPropertyConstants.KEY_SETTING_NONE_COLOR_CHAR, this.noneColerChar.getValue().toString());
            prop.setProperty(ProductionNaviPropertyConstants.KEY_SETTING_NONE_COLOR_BACK, this.noneColerBack.getValue().toString());

            // 組織
            StringBuilder orgIds = new StringBuilder();
            for (int i = 0; i < selectOrganizations.size(); i++) {
                orgIds.append(selectOrganizations.get(i).getOrganizationId());
                if ((i + 1) < selectOrganizations.size()) {
                    orgIds.append(",");
                }
            }
            prop.setProperty(ProductionNaviPropertyConstants.KEY_SETTING_ORGANIZATION, orgIds.toString());
            AdProperty.store(ProductionNaviPropertyConstants.PROPERTY_TAG);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(":storeSearchSetting end");
    }

    /**
     * 表示するスケジュールの日付サイズの読み込み
     *
     */
    private void loadSchduleCellSizeSetting(Properties prop) {
        //設定を読み込んで検索条件保持用クラスにデータを入れる
        Callback<ListView<WorkPlanScheduleCellSizeTypeEnum>, ListCell<WorkPlanScheduleCellSizeTypeEnum>> comboCellFactory = (ListView<WorkPlanScheduleCellSizeTypeEnum> param) -> new WorkPlanScheduleCellSizeCellFactory();

        scheduleCellSizeCombo.setButtonCell(new WorkPlanScheduleCellSizeCellFactory());
        scheduleCellSizeCombo.setCellFactory(comboCellFactory);
        scheduleCellSizeCombo.setItems(FXCollections.observableArrayList(WorkPlanScheduleCellSizeTypeEnum.values()));
        Platform.runLater(() -> {
            scheduleCellSizeCombo.setValue(WorkPlanScheduleCellSizeTypeEnum.getEnum(prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_SCEDULE_CELL_SIZE, WorkPlanScheduleCellSizeTypeEnum.DAILY.name())));
        });

    }

    /**
     * 月次生産計画のタイトル表示設定の読み込み
     *
     */
    private void loadTitleDisplaySetting(Properties prop) {
        String select = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_TITLE, ProductionNaviPropertyConstants.MASTER_DATA_SETTING_TITLE);
        String master = prop.getProperty(ProductionNaviPropertyConstants.MASTER_SETTING_TITLE, ProductionNaviPropertyConstants.MASTER_DATA_SETTING_TITLE);

        String[] masterDatas = master.split(COMMA_SPLIT);
        String[] selectDatas = select.split(COMMA_SPLIT);
        this.logger.trace(" title display setting get --> [" + select + "]  : [" + master + "] : [" + masterDatas.length + "]");

        selectTitleList.setItems(FXCollections.observableArrayList(masterDatas));

        IndexedCheckModel<String> cm = selectTitleList.getCheckModel();
        for(String masterData : masterDatas){
            for(String selectData : selectDatas){
                if(masterData.equals(selectData)){
                    Platform.runLater(() -> {
                        cm.check(selectData);
                    });
                }
            }
        }
    }


    /**
     * 表示する組織設定の読み込み
     *
     */
//    private void loadShowOrganizationSetting(Properties prop) {
//        this.logger.info(":loadShowOrganizationSetting start");
//        String org = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_ORGANIZATION, "0");
//        this.logger.debug(" setting organization -> [" + org + "]");
//        String[] orgs = org.split(",");
//        for (String org1 : orgs) {
//            if (org1.isEmpty()) {
//                continue;
//            }
//            Long organizationId = Long.parseLong(org1);
//            this.logger.debug(" organizationId:" + organizationId);
//            if (organizationId != 0l) {
//                this.logger.debug(" > selectOrganizations.add");
//                selectOrganizations.add(ScheduleSearcher.getOrganization(organizationId));
//                this.logger.debug(" > Platform.runLater ");
//                Platform.runLater(() -> {
//                    this.logger.debug(" > organizationList.getItems().clear()");
//                    organizationList.getItems().clear();
//                    this.logger.debug("organizationList.getItems().addAll()");
//                    organizationList.getItems().addAll(selectOrganizations);
//                    this.logger.debug("aaaa");
//                });
//            }
//        }
//        this.logger.info(":loadShowOrganizationSetting end");
//    }


}
