/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.equipmenteditplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jakarta.xml.bind.JAXB;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentSettingInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.PropertyEnum;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellSimpleComboBox;
import jp.adtekfuji.javafxcommon.property.CellSwitchButton;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author e.mori
 */
public class EquipmentSettingRecordFactory extends AbstractRecordFactory<EquipmentSettingInfoEntity> {

    private final static String FILENAME_ADACCESSORYPLUGINS = "adAccessoryPlugins.xml";
    private final static Logger logger = LogManager.getLogger();
    private static adAccessoryPluginsEntity adAccessoryPlugins = null;

    private ChangeListener<String> changeListener;

    /**
     * プロパティ情報型表示用セルクラス
     */
    class PropertyTypeComboBoxCell extends ListCell<CustomPropertyTypeEnum> {

        @Override
        protected void updateItem(CustomPropertyTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
            }
        }
    }

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");;

    public EquipmentSettingRecordFactory(Table table, LinkedList<EquipmentSettingInfoEntity> entitys) {
        this(table, entitys, (observable, oldvalue, newvalue) -> {
        });
    }

    public EquipmentSettingRecordFactory(Table table, LinkedList<EquipmentSettingInfoEntity> entitys, ChangeListener<String> changeListener) {
        super(table, entitys);
        if (Objects.isNull(adAccessoryPlugins)) {
            adAccessoryPlugins = this.load();
        }
        this.changeListener = changeListener;
    }

    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();

        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.PropertyName") + LocaleUtils.getString("key.RequiredMark"))).setPrefWidth(200.0).addStyleClass("ContentTitleLabel"));
        //cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.PropertyType") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.PropertyContent"))).setPrefWidth(200.0).addStyleClass("ContentTitleLabel"));
        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    @Override
    protected Record createRecord(EquipmentSettingInfoEntity entity) {
        Record record = new Record(super.getTable(), false);
        Callback<ListView<CustomPropertyTypeEnum>, ListCell<CustomPropertyTypeEnum>> comboCellFactory = (ListView<CustomPropertyTypeEnum> param) -> new PropertyTypeComboBoxCell();

        LinkedList<AbstractCell> cells = new LinkedList<>();

        // プロパティ名
        PropertyEnum property = PropertyEnum.toEnum(entity.getEquipmentSettingName());
        if (Objects.nonNull(property)) {
            cells.add(new CellLabel(record, property.getDisplayName(rb)).setPrefWidth(200.0).addStyleClass("ContentTextBox"));
        } else {
            cells.add(new CellLabel(record, entity.getEquipmentSettingType().getDisplayName(rb)).setPrefWidth(200.0).addStyleClass("ContentTextBox"));
        }

        // プロパティタイプ名
        //cells.add(new CellComboBox<>(record, Arrays.asList(CustomPropertyTypeEnum.values()), new PropertyTypeComboBoxCell(), comboCellFactory, entity.equipmentSettingTypeProperty()).addStyleClass("ContentComboBox"));

        // プロパティ値
        switch (entity.getEquipmentSettingType()) {
            case TYPE_PLUGIN:
                cells.add(new CellSimpleComboBox(record, adAccessoryPlugins.getPluginNames(), entity.equipmentSettingValueProperty(), true)
                        .setChangeListener(changeListener)
                        .setPrefWidth(200.0)
                        .addStyleClass("ContentTextBox"));
                break;
            case TYPE_BOOLEAN:
                cells.add(new CellSwitchButton(record, entity.equipmentSettingValueProperty()).setPrefWidth(200.0));
                break;
            default:
                 cells.add(new CellTextField(record, entity.equipmentSettingValueProperty()).setPrefWidth(200.0).addStyleClass("ContentTextBox"));
                 break;
        }

        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return EquipmentPropertyInfoEntity.class;
    }

    /**
     * プラグイン情報を読み込む
     *
     * @return
     */
    private adAccessoryPluginsEntity load()  {
        try {
            logger.info("load start.");

            adAccessoryPluginsEntity entity = null;

            String filePath = System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + FILENAME_ADACCESSORYPLUGINS;
            File file = new File(filePath);

            try (InputStream fis = new FileInputStream(file); InputStreamReader reader = new InputStreamReader(fis, "UTF-8"); BufferedReader br = new BufferedReader(reader)) {
                StringBuilder xml = new StringBuilder();
                String str;
                while ((str = br.readLine()) != null) {
                    xml.append(str);
                }

                entity = JAXB.unmarshal(new StringReader(xml.toString()), adAccessoryPluginsEntity.class);
            }

            return entity;
        } catch (Exception  ex) {
            logger.fatal(ex, ex);
            return new adAccessoryPluginsEntity();
        } finally {
            logger.info("load end.");
        }
    }
     
    /**
     * プラグイン名のリストを返す
     *
     * @return List<String> プラグイン名のリスト
     */
    public static List<String> getPluginNames() {
        try {
            logger.info("getPluginNames start.");

            adAccessoryPluginsEntity entity = null;

            String filePath = System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + FILENAME_ADACCESSORYPLUGINS;
            File file = new File(filePath);

            try (InputStream fis = new FileInputStream(file); InputStreamReader reader = new InputStreamReader(fis, "UTF-8"); BufferedReader br = new BufferedReader(reader)) {
                StringBuilder xml = new StringBuilder();
                String str;
                while ((str = br.readLine()) != null) {
                    xml.append(str);
                }

                entity = JAXB.unmarshal(new StringReader(xml.toString()), adAccessoryPluginsEntity.class);
            }
            return entity.getPluginNames();
        } catch (Exception  ex) {
            logger.fatal(ex, ex);
            return new ArrayList<String>();
        } finally {
            logger.info("getPluginNames end.");
        }
    }  
}
