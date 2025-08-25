/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model;

import adtekfuji.utility.RegexUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.xml.bind.JAXB;
import jp.adtekfuji.andon.entity.MonitorLineTimerInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ライン管理
 *
 * @author s-heya
 */
public class LineManager {

    private final Logger logger = LogManager.getLogger();

    private static LineManager instance;
    private final Map<Long, MonitorLineTimerInfoEntity> lineTimerCollection = new HashMap<>();
    private final Map<Long, AndonMonitorLineProductSetting> lineSettingCollection = new HashMap<>();

    /**
     * コンストラクタ
     */
    private LineManager() {
    }

    /**
     * インスタンスを取得する。
     *
     * @return
     */
    public static LineManager getInstance() {
        if (Objects.isNull(instance)) {
            instance = new LineManager();
            instance.initialize();
        }
        return instance;
    }

    /**
     * カウントダウン情報を設定する。
     *
     * @param monitorId モニターID
     * @param entity
     */
    public void setLineTimer(Long monitorId, MonitorLineTimerInfoEntity entity) {
        this.lineTimerCollection.put(monitorId, entity);
    }

    /**
     * カウントダウン情報を取得する。
     *
     * @param monitorId モニターID
     * @return
     */
    public MonitorLineTimerInfoEntity getLineTimer(Long monitorId) {
        return this.lineTimerCollection.get(monitorId);
    }

    /**
     * カウントダウン情報を保持しているかどうかを返す。
     *
     * @param monitorId モニターID
     * @return
     */
    public boolean containsLineTimer(Long monitorId) {
        return this.lineTimerCollection.containsKey(monitorId);
    }

    /**
     * カウントダウン情報を削除する。
     *
     * @param monitorId モニターID
     */
    public void removeLineTimer(Long monitorId) {
        this.lineTimerCollection.remove(monitorId);
    }

    /**
     * 進捗モニター設定を設定する。
     *
     * @param monitorId モニターID
     * @param entity
     */
    public void setLineSetting(Long monitorId, AndonMonitorLineProductSetting entity) {
        entity.setMonitorId(monitorId);
        if (this.lineSettingCollection.containsKey(monitorId)) {
            AndonMonitorLineProductSetting old = this.lineSettingCollection.get(monitorId);
            entity.setTodayStartTime(old.getTodayStartTime());
        }
        this.lineSettingCollection.put(monitorId, entity);
    }

    /**
     * 進捗モニター設定を取得する。
     *
     * @param monitorId モニターID
     * @return
     */
    public AndonMonitorLineProductSetting getLineSetting(Long monitorId) {
        return this.lineSettingCollection.get(monitorId);
    }

    /**
     * 進捗モニター設定を保持しているかどうかを返す。
     *
     * @param monitorId モニターID
     * @return
     */
    public boolean containsLineSetting(Long monitorId) {
        return this.lineSettingCollection.containsKey(monitorId);
    }

    /**
     * 進捗モニター設定を削除する。
     *
     * @param monitorId モニターID
     */
    public void removeLineSetting(Long monitorId) {
        if (this.lineSettingCollection.containsKey(monitorId)) {
            this.lineSettingCollection.remove(monitorId);
        }
    }

    /**
     * 進捗モニター設定を取得する。
     *
     * @return
     */
    public Collection<AndonMonitorLineProductSetting> getLineSetting() {
        return this.lineSettingCollection.values();
    }

    /**
     * 作業開始時間を保存する。
     *
     * @param lineId ラインID
     * @param date
     */
    //public void setTodayStartTime(Long lineId, Date date) {
    //    //AndonMonitorLineProductSettingFileAccessor fileAccessor = new AndonMonitorLineProductSettingFileAccessor();
    //    for (Entry<Long, AndonMonitorLineProductSetting> entry :  this.lineSettingCollection.entrySet()) {
    //        if (Objects.equals(lineId, entry.getValue().getLineId())) {
    //            entry.getValue().setTodayStartTime(date);
    //            //fileAccessor.save(entry.getKey(), entry.getValue());
    //        }
    //    }
    //}

    /**
     * ライン管理を初期化する。
     */
    private void initialize() {
        try {
            logger.info("initialize start.");

            List<File> files = new ArrayList<>();
            File temp = new File(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            File[] array = temp.listFiles((File file) -> file.getName().matches("MonitorLineProductSetting_line[0-9]+\\.xml"));
            if (Objects.nonNull(array)) {
                for (File file : array) {
                    files.add(file);
                }
            }

            for (File file : files) {
                try {
                    AndonMonitorLineProductSetting setting = JAXB.unmarshal(file, AndonMonitorLineProductSetting.class);

                    String monitorId = RegexUtils.extract("[0-9]+", file.getName());
                    logger.info("MonitorLineProductSetting: " + monitorId);

                    this.setLineSetting(Long.valueOf(monitorId), setting);
                } catch (IllegalStateException | NumberFormatException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
        catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        finally {
            logger.info("initialize end.");
        }
    }

    /**
     * すべての進捗モニター設定を取得する。
     *
     * @return
     */
    public Collection<AndonMonitorLineProductSetting> getMonitorSettings() {
        return this.lineSettingCollection.values();
    }

    /**
     * テストのセットアップ
     */
    public void setUpTest() {
        this.lineTimerCollection.clear();
        this.lineSettingCollection.clear();
    }
}
