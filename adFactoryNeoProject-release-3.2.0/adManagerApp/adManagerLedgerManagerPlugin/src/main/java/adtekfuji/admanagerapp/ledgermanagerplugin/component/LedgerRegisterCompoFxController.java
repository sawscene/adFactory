/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.component;

import adtekfuji.admanagerapp.ledgermanagerplugin.common.NameAndValueFieldRecordFactory;
import adtekfuji.admanagerapp.ledgermanagerplugin.common.ScheduleRecordFactory;
import adtekfuji.admanagerapp.ledgermanagerplugin.entity.NameAndTagEntity;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.LedgerInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerConditionEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerInfoEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerTargetEntity;
import jp.adtekfuji.adFactory.entity.ledger.NameValueEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleConditionInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LedgerTypeEnum;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

/**
 * 帳票設定
 * @author yu.nara
 */
@FxComponent(id = "LedgerRegisterDialog", fxmlPath = "/fxml/compo/ledger_register_dialog.fxml")
public class LedgerRegisterCompoFxController implements Initializable, ArgumentDelivery, DialogHandler {

    public static final String PROPERTY_TAG = "LedgerRegister";
    public static final String PROPERTY_FILE = "adFactoryLedgerManager.properties";
    public static final String SELECT_LEDGER_TEMPLATE_PATH = "select.ledger.template.path";// ダウンロードパス設定

    final String defaultPath = System.getProperty("user.home") + File.separator + "Documents";

    Properties properties;


    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final static LedgerInfoFacade ledgerInfoFacade = new LedgerInfoFacade();

    private Dialog dialog;


    @FXML
    private TextField ledgerName; // 帳票名

    @FXML
    private TextField ledgerSheetPath; // テンプレートファイル

    @FXML
    private TextField target; // 出力対象

    @FXML
    private TextField organization; // 組織
    @FXML
    private TextField equipment; // 設備
    @FXML
    private CheckBox noRemoveTagsCheck; // 置換され無かったタグを残す
    @FXML
    private ComboBox<LedgerTypeEnum> ledgerTypeCombo;
    @FXML
    private VBox schedulePain;
    @FXML
    private VBox keyTagPain;
    @FXML
    private Pane progressPane;
    @FXML
    private StackPane stackPane;

    @FXML
    private Label ledgerNameLabel;
    @FXML
    private Label ledgerSheetLabel;
    @FXML
    private Label ledgerTypeLabel;
    @FXML
    private Label tagetWorkLabel;

    private LedgerInfoEntity ledgerInfoEntity;
    private LedgerConditionEntity ledgerConditionEntity;

    private String filePath = "";
    private List<LedgerTargetEntity> ledgerTargetEntities;
    private List<EquipmentInfoEntity> equipmentInfoEntities;
    private List<OrganizationInfoEntity> organizationInfoEntities;

    private LinkedList<ScheduleConditionInfoEntity> scheduleConditionInfoEntities = new LinkedList<>();
    private final LinkedList<NameAndTagEntity> keyTagInfoList = new LinkedList<>();

    /**
     * コンストラクタ
     */
    public LedgerRegisterCompoFxController() {

    }

    /**
     * テンプレートファイルを選択
     */
    public void onSelectTemplateFile(ActionEvent event) {
        try {
            blockUI(true);

            File path = null;

            if (StringUtils.nonEmpty(ledgerSheetPath.getText())) {
                File fol = new File(ledgerSheetPath.getText());
                if (fol.exists()) {
                    if (fol.isDirectory()) {
                        path = fol;
                    } else if (fol.isFile()) {
                        if (fol.getParentFile() != null) {
                            path = fol.getParentFile();
                        }
                    }
                }
            }

            if (Objects.isNull(path)) {
                if (Objects.nonNull(properties)) {
                    File fol = new File(properties.getProperty(SELECT_LEDGER_TEMPLATE_PATH, this.defaultPath));
                    if (fol.exists() && fol.isDirectory()) {
                        path = fol;
                    }
                }
            }

            if (Objects.isNull(path)){
                path = Paths.get(System.getProperty("user.home"), "Desktop").toFile();
            }

            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(path);
            fc.setTitle(LocaleUtils.getString("key.LedgerSheet") + LocaleUtils.getString("key.FileChoice"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(LocaleUtils.getString("key.LedgerSheet"), "*.xlsx", "*.xlsm"));
            File selectedFile = fc.showOpenDialog((Stage)((Node) event.getSource()).getScene().getWindow());
            if (selectedFile != null) {
                filePath = selectedFile.getPath();
                ledgerSheetPath.setText(selectedFile.getName());

                if(Objects.nonNull(properties)) {
                    properties.setProperty(SELECT_LEDGER_TEMPLATE_PATH, selectedFile.getParent());
                    AdProperty.store(PROPERTY_TAG);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally{
            blockUI(false);
        }
    }


    /**
     * 対象工程選択
     */
    public void onSelectTarget(ActionEvent event) {
        try {
            blockUI(true);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.TargetWork") + LocaleUtils.getString("key.Choice"), "WorkSelectionCompo", this.ledgerTargetEntities, (Stage)((Node) event.getSource()).getScene().getWindow());
            if (ret.equals(ButtonType.OK)) {
                this.target.setText(this.toLedgerText(this.ledgerTargetEntities));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 対象組織の設定
     */
    public void onSelectOrganization(ActionEvent event) {
        try {
            blockUI(true);
            SelectDialogEntity<OrganizationInfoEntity> selectDialog = new SelectDialogEntity<>();
            selectDialog.organizations(new ArrayList<>(this.organizationInfoEntities));

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialog, (Stage)((Node) event.getSource()).getScene().getWindow());
            if (ButtonType.OK.equals(ret)) {
                this.organizationInfoEntities = selectDialog.getOrganizations();
                this.organization.setText(toOrganizationText(this.organizationInfoEntities));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 対象設備の設定
     */
    public void onSelectEquipment(ActionEvent event) {
        try {
            blockUI(true);

            SelectDialogEntity<EquipmentInfoEntity> selectDialog = new SelectDialogEntity<>();
            selectDialog.equipments(new ArrayList<>(this.equipmentInfoEntities));
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Equipment"), "EquipmentSelectionCompo", selectDialog, (Stage)((Node) event.getSource()).getScene().getWindow());
            if (ButtonType.OK.equals(ret)) {
                this.equipmentInfoEntities = selectDialog.getEquipments();
                this.equipment.setText(this.toEquipmentText(this.equipmentInfoEntities));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 帳票種表示用セルクラス
     */
    static class LedgerTypeEnumComboBoxCellFactory extends ListCell<LedgerTypeEnum> {
        @Override
        protected void updateItem(LedgerTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.resourceKey));
            }
        }
    }

    /**
     * ダイアログを初期化する。
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            blockUI(true);
            ledgerNameLabel.setText(ledgerNameLabel.getText() + LocaleUtils.getString("key.RequiredMark"));
            ledgerSheetLabel.setText(ledgerSheetLabel.getText() + LocaleUtils.getString("key.RequiredMark"));
            ledgerTypeLabel.setText(ledgerTypeLabel.getText() + LocaleUtils.getString("key.RequiredMark"));
            tagetWorkLabel.setText(tagetWorkLabel.getText() + LocaleUtils.getString("key.RequiredMark"));

            try {
                AdProperty.load(PROPERTY_TAG, PROPERTY_FILE);
                properties = AdProperty.getProperties(PROPERTY_TAG);
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }

            Callback<ListView<LedgerTypeEnum>, ListCell<LedgerTypeEnum>> comboCellFactory = (ListView<LedgerTypeEnum> param) -> new LedgerRegisterCompoFxController.LedgerTypeEnumComboBoxCellFactory();
            ledgerTypeCombo.setButtonCell(new LedgerRegisterCompoFxController.LedgerTypeEnumComboBoxCellFactory());
            ledgerTypeCombo.setCellFactory(comboCellFactory);
            Platform.runLater(() -> {
                ledgerTypeCombo.setItems(FXCollections.observableArrayList(LedgerTypeEnum.values()));
                ledgerTypeCombo.setVisible(true);
            });

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 帳票の表示
     * @param entities 表示エンティティ
     * @return 表示
     */
    String toLedgerText(List<LedgerTargetEntity> entities) {
        if (Objects.isNull(entities) || entities.isEmpty()) {
            return "";
        }

        if (entities.size() == 1) {
            return entities.get(0).getName();
        }

        return entities.get(0).getName() + "(+" + (entities.size()-1) + ")";
    }

    /**
     * 組織の表示
     * @param entities 表示エンティティ
     * @return 表示
     */
    String toEquipmentText(List<EquipmentInfoEntity> entities) {
        if (Objects.isNull(entities) || entities.isEmpty()) {
            return "";
        }

        if (entities.size() == 1) {
            return entities.get(0).getEquipmentName();
        }

        return entities.get(0).getEquipmentName() + "(+" + (entities.size()-1) + ")";
    }

    /**
     * 組織の表示
     * @param entities 表示エンティティ
     * @return 表示
     */
    String toOrganizationText(List<OrganizationInfoEntity> entities) {
        if (Objects.isNull(entities) || entities.isEmpty()) {
            return "";
        }

        if (entities.size() == 1) {
            return entities.get(0).getOrganizationName();
        }
        return entities.get(0).getOrganizationName() + "(+" + (entities.size()-1) + ")";
    }


    /**
     * パラメーターを設定する。
     * 
     * @param argument パラメーター
     */
    @Override
    public void setArgument(Object argument) {
        try {
            blockUI(true);

            if (!(argument instanceof LedgerInfoEntity)) {
                return;
            }

            this.ledgerInfoEntity = (LedgerInfoEntity) argument;
            this.filePath = ((LedgerInfoEntity) argument).getLedgerPhysicalFileName();
            this.ledgerTargetEntities = this.ledgerInfoEntity.getLedgerTarget();

            this.ledgerConditionEntity = ledgerInfoEntity.getLedgerCondition();

            keyTagInfoList.clear();
            this.ledgerConditionEntity
                    .getKeyTag()
                    .stream()
                    .filter(entity -> !entity.isEmpty())
                    .forEach(keyTag -> keyTagInfoList.add(new NameAndTagEntity(keyTag)));

            this.keyTagPain.getChildren().clear();
            Table<NameAndTagEntity> keyTagPanTable = new Table<>(this.keyTagPain.getChildren());
            keyTagPanTable.isColumnTitleRecord(true);
            keyTagPanTable.isAddRecord(true);
            keyTagPanTable.maxRecord(3);
            keyTagPanTable.setAbstractRecordFactory(new NameAndValueFieldRecordFactory(rb, keyTagPanTable, this.keyTagInfoList));

            this.scheduleConditionInfoEntities = new LinkedList<>(this.ledgerConditionEntity.getScheduleConditionInfoEntity());
            this.schedulePain.getChildren().clear();
            Table<ScheduleConditionInfoEntity> table = new Table<>(this.schedulePain.getChildren());
            table.isColumnTitleRecord(true);
            table.isAddRecord(true);
            table.maxRecord(3);
            ScheduleRecordFactory scheduleRecordFactory = new ScheduleRecordFactory(table, this.scheduleConditionInfoEntities, this::blockUI);
            table.setAbstractRecordFactory(scheduleRecordFactory);

            CashManager cache = CashManager.getInstance();
            CacheUtils.createCacheEquipment(true);
            this.equipmentInfoEntities
                    = ledgerConditionEntity
                    .getEquipmentIds()
                    .stream()
                    .map(id -> cache.getItem(EquipmentInfoEntity.class, id))
                    .filter(EquipmentInfoEntity.class::isInstance)
                    .map(EquipmentInfoEntity.class::cast)
                    .collect(toList());

            CacheUtils.createCacheOrganization(true);
            this.organizationInfoEntities
                    = ledgerConditionEntity
                    .getOrganizationIds()
                    .stream()
                    .map(id -> cache.getItem(OrganizationInfoEntity.class, id))
                    .filter(OrganizationInfoEntity.class::isInstance)
                    .map(OrganizationInfoEntity.class::cast)
                    .collect(toList());

            Platform.runLater(() -> {
                ledgerTypeCombo.setValue(this.ledgerConditionEntity.getLedgerType());
                ledgerName.setText(this.ledgerInfoEntity.getLedgerName());
                ledgerSheetPath.setText(this.ledgerInfoEntity.getLedgerFileName());
                target.setText(this.toLedgerText(this.ledgerTargetEntities));
                organization.setText(this.toOrganizationText(this.organizationInfoEntities));
                equipment.setText(this.toEquipmentText(this.equipmentInfoEntities));
                noRemoveTagsCheck.setSelected(ledgerConditionEntity.getNoRemoveTags());
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * ダイアログを設定する。
     * 
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> this.cancelDialog());
    }

    private boolean checkData() {
        if(Objects.isNull(ledgerTypeCombo.getValue())
        || StringUtils.isEmpty(ledgerName.getText())
        || ledgerTargetEntities.isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * 帳票テンプレートファイルアップロード
     */
    static final Pattern pattern = Pattern.compile("^\\d+/template/\\d{17}$");
    public boolean uploadFile(LedgerInfoEntity entity) {
        if (pattern.matcher(entity.getLedgerPhysicalFileName()).find()) {
            return true;
        }


        File fol = new File(filePath);
        if (!fol.exists() || !fol.isFile()) {
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistLedger"),
                    String.format(LocaleUtils.getString("key.FileNotExist"), LocaleUtils.getString("key.Ledger")));
            return false;
        }

        ResponseEntity res = ledgerInfoFacade.uploadTemplate(entity.getLedgerPhysicalFileName());
        if (Objects.isNull(res) || !ResponseAnalyzer.getAnalyzeResult(res)) {
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistLedger"),
                    String.format(LocaleUtils.getString("key.FailedFileUpload"), LocaleUtils.getString("key.Ledger")));
            return false;
        }

        entity.setLedgerPhysicalFileName(res.getUri());
        return true;
    }


    /**
     * 変更ボタンのアクション
     *
     */
    @FXML
    private void onRegister() {
        try {
            if (!checkData()) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return;
            }

            this.ledgerInfoEntity.setLedgerName(ledgerName.getText());
            this.ledgerInfoEntity.setLedgerFileName(ledgerSheetPath.getText());
            this.ledgerInfoEntity.setLedgerTarget(this.ledgerTargetEntities);
            this.ledgerInfoEntity.setUpdateDatetime(new Date());
            this.ledgerInfoEntity.setLedgerPhysicalFileName(this.filePath);
            this.ledgerInfoEntity.setLedgerFileName(this.ledgerSheetPath.getText());

            final List<Long> equipmentIds
                    = this.equipmentInfoEntities
                    .stream()
                    .map(EquipmentInfoEntity::getEquipmentId)
                    .collect(toList());

            final List<Long> organizationIds
                    = this.organizationInfoEntities
                    .stream()
                    .map(OrganizationInfoEntity::getOrganizationId)
                    .collect(toList());

            final List<NameValueEntity> keyTag
                    = this.keyTagInfoList
                    .stream()
                    .map(NameAndTagEntity::getKeyTagEntity)
                    .filter(entity -> !entity.isEmpty())
                    .collect(toList());

            final List<ScheduleConditionInfoEntity> schedule
                    = this.scheduleConditionInfoEntities
                    .stream()
                    .filter(entity -> !entity.isEmpty())
                    .collect(toList());

            this.ledgerInfoEntity.setLedgerCondition(
                    new LedgerConditionEntity(
                            equipmentIds,
                            organizationIds,
                            noRemoveTagsCheck.isSelected(),
                            ledgerTypeCombo.getValue(),
                            keyTag,
                            schedule));

            if (!this.uploadFile(this.ledgerInfoEntity)) {
                return;
            }

            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * キャンセルボタンのアクション
     *
     */
    @FXML
    private void onCancel() {
        this.cancelDialog();
    }
    
    /**
     * キャンセル処理
     */
    private void cancelDialog() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * UIロック
     *
     * @param flg フラグ
     */
    private void blockUI(Boolean flg) {
        sc.blockUI(flg);
        this.stackPane.setDisable(flg);
        this.progressPane.setVisible(flg);
    }

}
