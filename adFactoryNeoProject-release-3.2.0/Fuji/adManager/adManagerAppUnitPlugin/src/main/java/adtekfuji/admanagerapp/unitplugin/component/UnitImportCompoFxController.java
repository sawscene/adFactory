/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unitplugin.component;

import adtekfuji.admanagerapp.unitplugin.entity.ImportUnitEntity;
import adtekfuji.admanagerapp.unitplugin.entity.ImportUnitPropertyEntity;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.entity.unit.UnitHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitPropertyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.forfujiapp.utils.CsvFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 生産計画読み込み画面(ユニット)
 *
 * @author s-maeda
 */
@FxComponent(id = "UnitImportCompo", fxmlPath = "/fxml/compo/unitImportCompo.fxml")
public class UnitImportCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private static final String IMPORT_PATH = "unitImportPath";// インポートパス設定

    private static final String UNIT_CSV = "unit.csv";// ユニット情報ファイル
    private static final String UNIT_PROPERTY_CSV = "unit_property.csv";// ユニットプロパティファイル
    private static final String CHARSET = "UTF-8";
    private static final String DELIMITER = "\\|";
    private static final Long RANGE = 20l;

    /**
     * ユニット情報 - ユニット階層名
     */
    private static final Integer NUM_UNI_HIERARCHY_NAME = 0;
    /**
     * ユニット情報 - ユニット名
     */
    private static final Integer NUM_UNI_UNIT_NAME = 1;
    /**
     * ユニット情報 - ユニットテンプレート名
     */
    private static final Integer NUM_UNI_TEMPLATE_NAME = 2;
    /**
     * ユニット情報 - 着手日
     */
    private static final Integer NUM_UNI_START_DATE = 3;
    /**
     * ユニット情報 - 納品日
     */
    private static final Integer NUM_UNI_DELIVERY_DATE = 4;

    /**
     * ユニットプロパティ情報 - ユニット名
     */
    private static final Integer NUM_UNIP_UNIT_NAME = 0;
    /**
     * ユニットプロパティ情報 - プロパティ名
     */
    private static final Integer NUM_UNIP_PROP_NAME = 1;
    /**
     * ユニットプロパティ情報 - 型
     */
    private static final Integer NUM_UNIP_PROP_TYPE = 2;
    /**
     * ユニットプロパティ情報 - 値
     */
    private static final Integer NUM_UNIP_PROP_VALUE = 3;

    @FXML
    private TextField importFolderField;
    @FXML
    private Button importButton;
    @FXML
    private ListView resultList;
    @FXML
    private Pane progressPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 役割の権限によるボタン無効化.
        if(!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            importButton.setDisable(true);
        }

        final String path = System.getProperty("user.home") + File.separator + "Documents";
        String importPath = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).getProperty(IMPORT_PATH, path);
        this.importFolderField.setText(importPath);        
        blockUI(false);
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {

    }

    /**
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            sc.blockUI("ContentNaviPane", flg);
            progressPane.setVisible(flg);
        });
    }

    /**
     * 結果リストにメッセージを追加し、追加したメッセージが見えるようにスクロールする
     *
     * @param message
     */
    private void addResult(String message) {
        Platform.runLater(() -> {
            this.resultList.getItems().add(message);
            this.resultList.scrollTo(message);
        });
    }

    /**
     * フォルダ選択ボタン Action
     *
     * @param event
     */
    @FXML
    private void onSelectFolderAction(ActionEvent event) {
        blockUI(true);
        DirectoryChooser dc = new DirectoryChooser();
        File fol = new File(importFolderField.getText());
        if (fol.exists() && fol.isDirectory()) {
            dc.setInitialDirectory(fol);
        }
        File selectedFile = dc.showDialog(sc.getStage().getScene().getWindow());
        if (selectedFile != null) {
            importFolderField.setText(selectedFile.getPath());
        }
        blockUI(false);
    }

    /**
     * インポートボタン Action
     *
     * @param event
     */
    @FXML
    private void onImportAction(ActionEvent event) {
        try {
            blockUI(true);
            this.resultList.getItems().clear();

            // 出力先
            String folder = this.importFolderField.getText();
            if (Objects.isNull(folder) || folder.isEmpty()) {
                return;
            }

            File file = new File(folder);
            if (!file.exists() || !file.isDirectory()) {
                return;
            }

            AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(IMPORT_PATH, folder);

            // インポート
            this.importUnitTask(folder);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * インポート処理
     *
     * @param folder
     */
    private void importUnitTask(String folder) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                blockUI(true);
                try {
                    addResult(String.format("%s [%s]", LocaleUtils.getString("key.ImportUnitStart"), folder));// 生産計画取り込み開始
                    importUnit(folder);
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    addResult(LocaleUtils.getString("key.ImportUnitEnd"));// 生産計画取り込み終了
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * ユニットインポート
     *
     * @param folder
     * @throws Exception
     */
    private void importUnit(String folder) throws Exception {
        // ユニット情報 読込
        String unitPath = folder + File.separator + UNIT_CSV;
        List<ImportUnitEntity> importUnits = readUnitCsv(unitPath);
        if (importUnits.isEmpty()) {
            addResult(LocaleUtils.getString("key.ImportUnit_FileNothing"));// 指定フォルダにユニット情報ファイルがない
            return;
        }
        addResult(LocaleUtils.getString("key.ImportUnit_ReadUnitCsv"));// ユニット情報ファイル読み込み

        // ユニットプロパティ情報 読込
        String unitPropPath = folder + File.separator + UNIT_PROPERTY_CSV;
        List<ImportUnitPropertyEntity> importUnitProps = readUnitPropertyCsv(unitPropPath);
        if (!importUnitProps.isEmpty()) {
            addResult(LocaleUtils.getString("key.ImportUnit_ReadUnitPropertyCsv"));// ユニットプロパティ情報ファイル読み込み
        }

        Map<String, UnitInfoEntity> unitMap = new HashMap();
        Map<String, UnitHierarchyInfoEntity> unitHierarchyMap = new HashMap();
        Map<String, UnitTemplateInfoEntity> unitTemplateMap = new HashMap();

        int procNum = 0;
        int skipUnitNum = 0;
        int successNum = 0;
        int failedNum = 0;
        for (ImportUnitEntity importUnit : importUnits) {
            procNum++;

            // ユニット名
            String unitName = importUnit.getUnitName();
            if (Objects.isNull(unitName) || unitName.isEmpty()) {
                continue;
            }
            addResult(String.format("%s: %s", LocaleUtils.getString("key.ImportUnit_TargetUnitName"), unitName));// 読み込みユニット

            // ユニット階層
            UnitHierarchyInfoEntity unitHierarchy;
            if (unitHierarchyMap.containsKey(importUnit.getUnitHierarchyName())) {
                unitHierarchy = unitHierarchyMap.get(importUnit.getUnitHierarchyName());
            } else {
                unitHierarchy = RestAPI.getUnitHierarchyByName(URLEncoder.encode(importUnit.getUnitHierarchyName(), CHARSET));
                unitHierarchyMap.put(importUnit.getUnitHierarchyName(), unitHierarchy);
            }
            Long unitHierarchyId = unitHierarchy.getUnitHierarchyId();
            if (Objects.isNull(unitHierarchyId)) {
                // 存在しないユニット階層のためスキップ
                skipUnitNum++;
                addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportUnit_HierarchyNothing"), importUnit.getUnitHierarchyName()));
                continue;
            }

            // ユニットテンプレートID
            UnitTemplateInfoEntity unitTemplate;
            if (unitTemplateMap.containsKey(importUnit.getUnitTemplateName())) {
                unitTemplate = unitTemplateMap.get(importUnit.getUnitTemplateName());
            } else {
                unitTemplate = RestAPI.getUnitTemplateByName(URLEncoder.encode(importUnit.getUnitTemplateName(), CHARSET));
                unitTemplateMap.put(importUnit.getUnitTemplateName(), unitTemplate);
            }
            Long unitTemplateId = unitTemplate.getUnitTemplateId();
            if (Objects.isNull(unitTemplateId)) {
                // 存在しないユニットテンプレートのためスキップ
                skipUnitNum++;
                addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportUnit_UnitTemplateNothing"), importUnit.getUnitTemplateName()));
                continue;
            }
            String unitTemplateName = unitTemplate.getUnitTemplateName();

            // ユニット作成
            UnitInfoEntity unit;
            if (unitMap.containsKey(unitName)) {
                unit = unitMap.get(unitName);
            } else {
                unit = RestAPI.getUnitByName(URLEncoder.encode(unitName, CHARSET));
            }
            if (Objects.nonNull(unit.getUnitId())) {
                // 同名ユニットが既に存在する場合
                // ユニットテンプレート名の相違確認
                String existingName = unit.getUnitTemplateName();
                if (!unitTemplateName.equals(existingName)) {
                    // ユニットテンプレートが既存と異なるため、スキップ
                    skipUnitNum++;
                    addResult("  > " + String.format(LocaleUtils.getString("key.ImportUnit_DifferentUnitTemplates"), unitTemplateName, existingName));
                    continue;
                }
            }

            unit.setUnitName(unitName);
            unit.setParentId(unitHierarchyId);
            unit.setFkUnitTemplateId(unitTemplateId);
            unit.setWorkflowDiaglam(unitTemplate.getWorkflowDiaglam());
            
            // 着手日
            if (Objects.nonNull(importUnit.getStartDatetime()) && !importUnit.getStartDatetime().isEmpty()) {
                Date unitStartDateTime = datetimeFormatter.parse(importUnit.getStartDatetime());
                unit.setStartDatetime(unitStartDateTime);
            }

            // 納品日
            if (Objects.nonNull(importUnit.getDeliveryDatetime()) && !importUnit.getDeliveryDatetime().isEmpty()) {
                Date unitDeliveryDateTime = datetimeFormatter.parse(importUnit.getDeliveryDatetime());
                unit.setCompDatetime(unitDeliveryDateTime);
            }

            // 新規作成分は一旦登録
            if (Objects.isNull(unit.getUnitId())) {
                // ユニット追加
                addResult(String.format("  > %s", LocaleUtils.getString("key.ImportUnit_CreateKanban")));
                ResponseEntity createRes = RestAPI.registUnit(unit);
                if (Objects.nonNull(createRes) && createRes.isSuccess()) {
                    // 追加成功
                    addResult(String.format("  > %s", LocaleUtils.getString("key.ImportUnit_RegistSuccess")));
                } else {
                    // 追加失敗
                    failedNum++;
                    addResult(String.format("  > %s", LocaleUtils.getString("key.ImportUnit_RegistFailed")));
                    continue;
                }
                unit = RestAPI.getUnitUri(createRes.getUri());
            }

            // ユニットプロパティ
            List<ImportUnitPropertyEntity> props = importUnitProps.stream().filter(p -> unitName.equals(p.getUnitName())).collect(Collectors.toList());
            for (ImportUnitPropertyEntity prop : props) {
                String propName = prop.getUnitPropertyName();
                if (Objects.isNull(propName) || propName.isEmpty()) {
                    continue;
                }

                // プロパティ型
                CustomPropertyTypeEnum propType = null;
                if (Objects.nonNull(prop.getUnitPropertyType()) && !prop.getUnitPropertyType().isEmpty()) {
                    String propTypeString = prop.getUnitPropertyType();
                    if (CustomPropertyTypeEnum.TYPE_STRING.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_STRING;
                    } else if (CustomPropertyTypeEnum.TYPE_BOOLEAN.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_BOOLEAN;
                    } else if (CustomPropertyTypeEnum.TYPE_INTEGER.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_INTEGER;
                    } else if (CustomPropertyTypeEnum.TYPE_NUMERIC.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_NUMERIC;
                    } else if (CustomPropertyTypeEnum.TYPE_IP4_ADDRESS.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_IP4_ADDRESS;
                    } else if (CustomPropertyTypeEnum.TYPE_MAC_ADDRESS.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_MAC_ADDRESS;
                    } else if (CustomPropertyTypeEnum.TYPE_PLUGIN.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_PLUGIN;
                    } else if (CustomPropertyTypeEnum.TYPE_TRACE.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_TRACE;
                    }
                }

                // 更新対象のユニットプロパティ
                Optional<UnitPropertyInfoEntity> findUnitProp = unit.getUnitPropertyCollection().stream().filter(p -> propName.equals(p.getUnitPropertyName())).findFirst();
                if (findUnitProp.isPresent()) {
                    UnitPropertyInfoEntity unitProp = findUnitProp.get();

                    // プロパティ型
                    if (Objects.nonNull(propType)) {
                        unitProp.setUnitPropertyType(propType);
                    }

                    // プロパティ値
                    if (Objects.nonNull(prop.getUnitPropertyValue()) && !prop.getUnitPropertyValue().isEmpty()) {
                        unitProp.setUnitPropertyValue(prop.getUnitPropertyValue());
                    }
                } else // 存在しないプロパティの場合は、プロパティ名とプロパティ値が入っていたら追加する。
                {
                    if (Objects.nonNull(propType)) {
                        UnitPropertyInfoEntity unitProp = new UnitPropertyInfoEntity();
                        unitProp.setUnitPropertyName(propName);
                        unitProp.setUnitPropertyType(propType);

                        // プロパティ値
                        if (Objects.nonNull(prop.getUnitPropertyValue()) && !prop.getUnitPropertyValue().isEmpty()) {
                            unitProp.setUnitPropertyValue(prop.getUnitPropertyValue());
                        }

                        // プロパティ表示順の最大値から、次に追加するプロパティの表示順を設定
                        int propOrder = 1;
                        Optional<UnitPropertyInfoEntity> lastProp = unit.getUnitPropertyCollection().stream().max(Comparator.comparingInt(p -> p.getUnitPropertyOrder()));
                        if (lastProp.isPresent()) {
                            propOrder = lastProp.get().getUnitPropertyOrder() + 1;
                        }
                        unitProp.setUnitPropertyOrder(propOrder);

                        unit.getUnitPropertyCollection().add(unitProp);
                    }
                }
            }

            // 更新情報セット
            unit.setUpdateDatetime(DateUtils.toDate(LocalDateTime.now()));// 更新日時
            unit.setFkUpdatePersonId(loginUserInfoEntity.getId());// 更新者

            // ユニット更新
            addResult(String.format("  > %s", LocaleUtils.getString("key.ImportUnit_UpdateUnit")));
            logger.info("import unit:{}", unit);
            ResponseEntity updateRes = RestAPI.updateUnit(unit);
            if (Objects.nonNull(updateRes) && updateRes.isSuccess()) {
                // 更新成功
                successNum++;
                addResult(String.format("  > %s", LocaleUtils.getString("key.ImportUnit_UpdateSuccess")));
                unitMap.put(unitName, unit);
            } else {
                // 更新失敗
                failedNum++;
                addResult(String.format("  > %s", LocaleUtils.getString("key.ImportUnit_UpdateFailed")));
            }
        }

        addResult(String.format("%s: %s (%s: %s, %s: %s, %s: %s)",
                LocaleUtils.getString("key.ImportUnit_ProccessNum"), procNum,
                LocaleUtils.getString("key.ImportUnit_SuccessNum"), successNum,
                LocaleUtils.getString("key.ImportUnit_SkipNum"), skipUnitNum,
                LocaleUtils.getString("key.ImportUnit_FailedNum"), failedNum));
    }

    /**
     * ユニット情報 CSVファイル読込
     *
     * @param path ファイルパス
     * @return
     */
    private List<ImportUnitEntity> readUnitCsv(String path) {
        List<ImportUnitEntity> importUnits = null;
        try {
            List<List<String>> unitRows = CsvFileUtils.readCsv(path, 2);// 2行目から読み込み

            importUnits = new ArrayList<>();
            for (List<String> row : unitRows) {
                for (int i = 0; i < row.size(); i++) {
                    row.set(i, CsvFileUtils.repraceEscapeString(row.get(i)));
                }

                ImportUnitEntity importUnit = new ImportUnitEntity();
                importUnit.setUnitHierarchyName(row.get(NUM_UNI_HIERARCHY_NAME));
                importUnit.setUnitName(row.get(NUM_UNI_UNIT_NAME));
                importUnit.setUnitTemplateName(row.get(NUM_UNI_TEMPLATE_NAME));
                importUnit.setStartDatetime(row.get(NUM_UNI_START_DATE));
                importUnit.setDeliveryDatetime(row.get(NUM_UNI_DELIVERY_DATE));

                importUnits.add(importUnit);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return importUnits;
    }

    /**
     * ユニットプロパティ情報 CSVファイル読込
     *
     * @param path
     * @return
     */
    private List<ImportUnitPropertyEntity> readUnitPropertyCsv(String path) {
        List<ImportUnitPropertyEntity> importUnitProps = null;
        try {
            List<List<String>> unitPropRows = CsvFileUtils.readCsv(path, 2);// 2行目から読み込み

            importUnitProps = new ArrayList<>();
            for (List<String> row : unitPropRows) {
                for (int i = 0; i < row.size(); i++) {
                    row.set(i, CsvFileUtils.repraceEscapeString(row.get(i)));
                }

                ImportUnitPropertyEntity importUnitProp = new ImportUnitPropertyEntity();
                importUnitProp.setUnitName(row.get(NUM_UNIP_UNIT_NAME));
                importUnitProp.setUnitPropertyName(row.get(NUM_UNIP_PROP_NAME));
                importUnitProp.setUnitPropertyType(row.get(NUM_UNIP_PROP_TYPE));
                importUnitProp.setUnitPropertyValue(row.get(NUM_UNIP_PROP_VALUE));

                importUnitProps.add(importUnitProp);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return importUnitProps;
    }
}
