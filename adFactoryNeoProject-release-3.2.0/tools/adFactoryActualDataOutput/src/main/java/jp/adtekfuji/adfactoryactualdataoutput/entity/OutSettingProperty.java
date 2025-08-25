/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryactualdataoutput.entity;

import adtekfuji.locale.LocaleUtils;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class OutSettingProperty {

    public enum TimeSettingEnum {

        PAST_TIME,
        RANGE_TIME;
    }

    public enum OutFolderSettingEnum {

        LOCAL,
        SHARE,
        FTP;
    }

    public enum FileEncodeSettingEnum {

        UTF_8("UTF-8"),
        SHIFT_JIS("SHIFT-JIS"),
        EUC_JP("EUC-JIS");

        private final String charset;

        private FileEncodeSettingEnum(String charset) {
            this.charset = charset;
        }

        public String getCharset() {
            return charset;
        }
    }

    private static final Logger logger = LogManager.getLogger();
    private static final String PROPERTY_FILE = System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + "adFactoryActualDataOutput.properties";
    private static final String OUTPUT_SECTION = "OutputSetting";
    private static final String COLUMN_SECTION = "ColumnSetting";
    private static final String FILTER_SECTION = "FilterSetting";

    private static final String key_timeSetting = "timeSetting";
    private static final String key_pastTime = "pastTime";
    private static final String key_fromRangeTime = "fromRangeTime";
    private static final String key_toRangeTime = "toRangeTime";
    private static final String key_fileEncode = "fileEncode";
    private static final String key_firstRowColumnCheck = "firstRowColumnCheck";
    private static final String key_doubleMarkCheck = "doubleMarkCheck";
    private static final String key_addLastStringCheck = "addLastStringCheck";
    private static final String key_lastString = "lastString";
    private static final String key_outFolderSetting = "outFolderSetting";
    private static final String key_localPath = "localPath";
    private static final String key_sharedPath = "sharedPath";
    private static final String key_sharedUser = "sharedUser";
    private static final String key_sharedPass = "sharedPass";
    private static final String key_ftpPath = "ftpPath";
    private static final String key_ftpPort = "ftpPort";
    private static final String key_ftpUser = "ftpUser";
    private static final String key_ftpPass = "ftpPass";
    private static final String key_filePath = "filePath";

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private final HierarchicalINIConfiguration iniConfig = new HierarchicalINIConfiguration();

    private final ObjectProperty<TimeSettingEnum> timeSettingProperty = new SimpleObjectProperty<>();
    private final IntegerProperty pastTimeProperty = new SimpleIntegerProperty();
    private final ObjectProperty<Date> fromRangeTimeProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> toRangeTimeProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<FileEncodeSettingEnum> fileEncodeProperty = new SimpleObjectProperty<>();
    private final BooleanProperty firstRowColumnCheckProperty = new SimpleBooleanProperty();
    private final BooleanProperty doubleMarkCheckProperty = new SimpleBooleanProperty();
    private final BooleanProperty addLastStringCheckProperty = new SimpleBooleanProperty();
    private final StringProperty lastStringProperty = new SimpleStringProperty();
    private final ObjectProperty<OutFolderSettingEnum> outFolderProperty = new SimpleObjectProperty<>();
    private final StringProperty localPathProperty = new SimpleStringProperty();
    private final StringProperty sharedPathProperty = new SimpleStringProperty();
    private final StringProperty sharedUserProperty = new SimpleStringProperty();
    private final StringProperty sharedPassProperty = new SimpleStringProperty();
    private final StringProperty ftpPathProperty = new SimpleStringProperty();
    private final IntegerProperty ftpPortProperty = new SimpleIntegerProperty();
    private final StringProperty ftpUserProperty = new SimpleStringProperty();
    private final StringProperty ftpPassProperty = new SimpleStringProperty();
    private final StringProperty filePathProperty = new SimpleStringProperty();
    private final List<ColumnNameProperty> columnNameList = new ArrayList<>();

    public OutSettingProperty() {
        iniConfig.setDelimiterParsingDisabled(true);
        iniConfig.setEncoding(FileEncodeSettingEnum.UTF_8.getCharset());
        iniConfig.setFileName(PROPERTY_FILE);
    }

    /**
     * load
     *
     * @throws ConfigurationException
     * @throws java.text.ParseException
     */
    public void load() throws ConfigurationException, ParseException {
        logger.info("load property file");
        iniConfig.load(new File(PROPERTY_FILE));
        SubnodeConfiguration outSec = iniConfig.getSection(OUTPUT_SECTION);
        //出力時間設定.
        timeSettingProperty.set(TimeSettingEnum.valueOf(outSec.getString(key_timeSetting, "PAST_TIME")));
        pastTimeProperty.set(outSec.getInteger(key_pastTime, 24));
        fromRangeTimeProperty.set(sdf.parse(outSec.getString(key_fromRangeTime, sdf.format(new Date()))));
        toRangeTimeProperty.set(sdf.parse(outSec.getString(key_toRangeTime, sdf.format(new Date()))));
        //出力形式設定.
        fileEncodeProperty.set(FileEncodeSettingEnum.valueOf(outSec.getString(key_fileEncode, "UTF_8")));
        firstRowColumnCheckProperty.set(outSec.getBoolean(key_firstRowColumnCheck, true));
        doubleMarkCheckProperty.set(outSec.getBoolean(key_doubleMarkCheck, false));
        addLastStringCheckProperty.set(outSec.getBoolean(key_addLastStringCheck, false));
        lastStringProperty.set(outSec.getString(key_lastString, ""));
        //出力フォルダパス設定.
        outFolderProperty.set(OutFolderSettingEnum.valueOf(outSec.getString(key_outFolderSetting, "LOCAL")));
        localPathProperty.set(outSec.getString(key_localPath, ""));
        sharedPathProperty.set(outSec.getString(key_sharedPath, ""));
        sharedUserProperty.set(outSec.getString(key_sharedUser, ""));
        sharedPassProperty.set(outSec.getString(key_sharedPass, ""));
        ftpPathProperty.set(outSec.getString(key_ftpPath, ""));
        ftpPortProperty.set(outSec.getInteger(key_ftpPort, 21));
        ftpUserProperty.set(outSec.getString(key_ftpUser, ""));
        ftpPassProperty.set(outSec.getString(key_ftpPass, ""));
        //出力ファイルパス設定.
        filePathProperty.set(outSec.getString(key_lastString, ""));
        //出力カラム名変換設定.
        SubnodeConfiguration colSec = iniConfig.getSection(COLUMN_SECTION);
        Iterator<String> ite = colSec.getKeys();
        while (ite.hasNext()) {
            String key = ite.next();
            String value = colSec.getString(key);
            columnNameList.add(new ColumnNameProperty(key, value));
        }
        createColumnData();
        //実績データフィルター設定.
    }

    /**
     * save
     *
     * @throws ConfigurationException
     */
    public void save() throws ConfigurationException {
        logger.info("save property file");
        //出力時間設定.
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_timeSetting, timeSettingProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_pastTime, pastTimeProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_fromRangeTime, sdf.format(fromRangeTimeProperty.get()));
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_toRangeTime, sdf.format(toRangeTimeProperty.get()));
        //出力形式設定.
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_fileEncode, fileEncodeProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_firstRowColumnCheck, firstRowColumnCheckProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_doubleMarkCheck, doubleMarkCheckProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_addLastStringCheck, addLastStringCheckProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_lastString, lastStringProperty.get());
        //出力フォルダパス設定.
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_outFolderSetting, outFolderProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_localPath, localPathProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_sharedPath, sharedPathProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_sharedUser, sharedUserProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_sharedPass, sharedPassProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_ftpPath, ftpPathProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_ftpPort, ftpPortProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_ftpUser, ftpUserProperty.get());
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_ftpPass, ftpPassProperty.get());
        //出力ファイルパス設定.
        iniConfig.setProperty(OUTPUT_SECTION + "." + key_filePath, filePathProperty.get());
        //出力カラム名変換設定.
        for (ColumnNameProperty column : columnNameList) {
            iniConfig.setProperty(COLUMN_SECTION + "." + column.getInColumnName(), column.getOutColumnName());
        }
        //実績データフィルター設定.
        iniConfig.save();
    }

    //
    private void createColumnData() {
        if (!columnNameList.isEmpty()) {
            return;
        }

        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.KanbanHierarch")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.KanbanName")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.KanbanSubname")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.OrderProcessesHierarch")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.OrderProcessesName")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.ProcessName")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.AdditionalProcess")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.OrganizationParentName")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.OrganizationParentIdentName")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.OrganizationName")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.OrganizationsManagementName")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.EquipmentParentName")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.EquipmentsParentIdentName")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.EquipmentName")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.EquipmentsManagementName")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.Status"), LocaleUtils.getString("key.Status") + "(WORKING,SUSPEND,INTERRUPT,COMPLETION)"));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.EditInterruptReasonTitle")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.EditDelayReasonTitle")));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.ImplementTime"), LocaleUtils.getString("key.ImplementTime") + "(yyyy/MM/dd)"));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.TactTime"), LocaleUtils.getString("key.TactTime") + "(min)"));
        columnNameList.add(new ColumnNameProperty(LocaleUtils.getString("key.WorkTime"), LocaleUtils.getString("key.WorkTime") + "(min)"));
        columnNameList.add(new ColumnNameProperty("ワークオーダ"));
        columnNameList.add(new ColumnNameProperty("シリアル"));
        columnNameList.add(new ColumnNameProperty("作業区分"));
        columnNameList.add(new ColumnNameProperty("ワークセンタ"));
    }

    // property
    public ObjectProperty<TimeSettingEnum> timeSettingProperty() {
        return timeSettingProperty;
    }

    public IntegerProperty pastTimeProperty() {
        return pastTimeProperty;
    }

    public ObjectProperty<Date> fromRangeTimeProperty() {
        return fromRangeTimeProperty;
    }

    public ObjectProperty<Date> toRangeTimeProperty() {
        return toRangeTimeProperty;
    }

    public ObjectProperty<FileEncodeSettingEnum> fileEncodeProperty() {
        return fileEncodeProperty;
    }

    public BooleanProperty firstRowColumnCheckProperty() {
        return firstRowColumnCheckProperty;
    }

    public BooleanProperty doubleMarkCheckProperty() {
        return doubleMarkCheckProperty;
    }

    public BooleanProperty addLastStringCheckProperty() {
        return addLastStringCheckProperty;
    }

    public StringProperty lastStringProperty() {
        return lastStringProperty;
    }

    public ObjectProperty<OutFolderSettingEnum> outFolderProperty() {
        return outFolderProperty;
    }

    public StringProperty localPathProperty() {
        return localPathProperty;
    }

    public StringProperty sharedPathProperty() {
        return sharedPathProperty;
    }

    public StringProperty sharedUserProperty() {
        return sharedUserProperty;
    }

    public StringProperty sharedPassProperty() {
        return sharedPassProperty;
    }

    public StringProperty ftpPathProperty() {
        return ftpPathProperty;
    }

    public IntegerProperty ftpPortProperty() {
        return ftpPortProperty;
    }

    public StringProperty ftpUserProperty() {
        return ftpUserProperty;
    }

    public StringProperty ftpPassProperty() {
        return ftpPassProperty;
    }

    public StringProperty filePathProperty() {
        return filePathProperty;
    }

    // set/get
    public TimeSettingEnum getTimeSetting() {
        return timeSettingProperty.get();
    }

    public void setTimeSetting(TimeSettingEnum timeSetting) {
        this.timeSettingProperty.set(timeSetting);
    }

    public Integer getPastTime() {
        return pastTimeProperty.get();
    }

    public void setPastTime(Integer pastTime) {
        this.pastTimeProperty.set(pastTime);
    }

    public Date getFromRangeTime() {
        return fromRangeTimeProperty.get();
    }

    public void setFromRangeTime(Date fromRangeTime) {
        this.fromRangeTimeProperty.set(fromRangeTime);
    }

    public Date getToRangeTime() {
        return toRangeTimeProperty.get();
    }

    public void setToRangeTime(Date toRangeTime) {
        this.toRangeTimeProperty.set(toRangeTime);
    }

    public FileEncodeSettingEnum getFileEncode() {
        return fileEncodeProperty.get();
    }

    public void setFileEncode(FileEncodeSettingEnum fileEncode) {
        this.fileEncodeProperty.set(fileEncode);
    }

    public Boolean getFirstRowColumnCheck() {
        return firstRowColumnCheckProperty.get();
    }

    public void setFirstRowColumnCheck(Boolean firstRowColumnCheck) {
        this.firstRowColumnCheckProperty.set(firstRowColumnCheck);
    }

    public Boolean getDoubleMarkCheck() {
        return doubleMarkCheckProperty.get();
    }

    public void setDoubleMarkCheck(Boolean doubleMarkCheck) {
        this.doubleMarkCheckProperty.set(doubleMarkCheck);
    }

    public Boolean getAddLastStringCheck() {
        return addLastStringCheckProperty.get();
    }

    public void setAddLastStringCheck(Boolean addLastStringCheck) {
        this.addLastStringCheckProperty.set(addLastStringCheck);
    }

    public String getLastStringProperty() {
        return lastStringProperty.get();
    }

    public void setLastStringProperty(String lastString) {
        lastStringProperty.set(lastString);
    }

    public OutFolderSettingEnum getOutFolder() {
        return outFolderProperty.get();
    }

    public void setOutFolder(OutFolderSettingEnum timeSetting) {
        this.outFolderProperty.set(timeSetting);
    }

    public String getLocalPath() {
        return localPathProperty.get();
    }

    public void setLocalPath(String localPath) {
        localPathProperty.set(localPath);
    }

    public String getSharedPath() {
        return sharedPathProperty.get();
    }

    public void setSharedPath(String sharedPath) {
        sharedPathProperty.set(sharedPath);
    }

    public String getSharedUser() {
        return sharedUserProperty.get();
    }

    public void setSharedUser(String sharedUser) {
        sharedUserProperty.set(sharedUser);
    }

    public String getSharedPass() {
        return sharedPassProperty.get();
    }

    public void setSharedPass(String sharedPass) {
        sharedPassProperty.set(sharedPass);
    }

    public String getFtpPath() {
        return ftpPathProperty.get();
    }

    public void setFtpPath(String ftpPath) {
        ftpPathProperty.set(ftpPath);
    }

    public Integer getFtpPort() {
        return ftpPortProperty.get();
    }

    public void setFtpPort(Integer ftpPort) {
        ftpPortProperty.set(ftpPort);
    }

    public String getFtpUser() {
        return ftpUserProperty.get();
    }

    public void setFtpUser(String ftpUser) {
        ftpUserProperty.set(ftpUser);
    }

    public String getFtpPass() {
        return ftpPassProperty.get();
    }

    public void setFtpPass(String ftpPass) {
        ftpPassProperty.set(ftpPass);
    }

    public String getFilePath() {
        return filePathProperty.get();
    }

    public void setFilePath(String filePath) {
        filePathProperty.set(filePath);
    }

    public List<ColumnNameProperty> getColumnNameList() {
        return columnNameList;
    }

    public void setColumnNameList(List<ColumnNameProperty> columnNameList) {
        this.columnNameList.clear();
        this.columnNameList.addAll(columnNameList);
    }

}
