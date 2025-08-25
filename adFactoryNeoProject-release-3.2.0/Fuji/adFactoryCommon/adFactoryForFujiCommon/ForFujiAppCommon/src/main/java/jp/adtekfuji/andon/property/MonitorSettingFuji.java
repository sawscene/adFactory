/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.property;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.andon.enumerate.AndonMonitorTypeEnum;
import jp.adtekfuji.forfujiapp.entity.monitor.CycleMelodyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.monitor.CycleTaktInfoEntity;

/**
 * 進捗モニタ設定情報 ForFuji
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "andonMonitorLineProductSetting")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class MonitorSettingFuji implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty lineIdProperty = null;
    private StringProperty titleProperty = null;
    private StringProperty modelNameProperty = null;
    private ObjectProperty<LocalTime> startWorkTimeProperty = null;
    private ObjectProperty<LocalTime> endWorkTimeProperty = null;
    private IntegerProperty dailyPlanNumProperty = null;
    private IntegerProperty montlyPlanNumProperty = null;
    private ObjectProperty<LocalTime> resetTimeProperty = null;
    private IntegerProperty cautionRetentionParcentProperty = null;
    private ObjectProperty<Color> cautionFontColorProperty = null;
    private ObjectProperty<Color> cautionBackColorProperty = null;
    private IntegerProperty warningRetentionParcentProperty = null;
    private ObjectProperty<Color> warningFontColorProperty = null;
    private ObjectProperty<Color> warningBackColorProperty = null;
    private ObjectProperty<LocalTime> lineTaktProperty;
    private BooleanProperty remoteLayoutProperty = null;
    private StringProperty layoutProperty = null;
    private StringProperty customizeToolLayoutProperty = null;
    private BooleanProperty useDailyPlanNumProperty = null;

    // モニター種別
    private AndonMonitorTypeEnum monitorType;

    private Long lineId;
    private String title = "";
    private String modelName = "";
    private LocalTime startWorkTime = LocalTime.of(0, 0, 0);
    private LocalTime endWorkTime = LocalTime.of(0, 0, 0);
    private List<BreakTimeInfoEntity> breaktimes = new ArrayList<>();
    private List<DayOfWeek> weekdays = new ArrayList<>();
    private Integer dailyPlanNum;
    private Integer montlyPlanNum;
    private LocalTime resetTime;
    private Integer cautionRetentionParcent;
    private Color cautionFontColor;
    private Color cautionBackColor;
    private Integer warningRetentionParcent;
    private Color warningFontColor;
    private Color warningBackColor;
    private List<WorkEquipmentSetting> workEquipmentCollection = new ArrayList<>();
    private List<WorkSetting> workCollection = new ArrayList<>();
    private List<String> delayReasonCollection = new ArrayList<>();
    private List<String> interruptReasonCollection = new ArrayList<>();
    private LocalTime lineTakt = LocalTime.of(0, 0, 0);
    private Boolean remoteLayout = false;
    private Boolean useDailyPlanNum = false;
    
    private List<CycleTaktInfoEntity> cycleTaktCollection = new ArrayList<>();
    private List<CycleMelodyInfoEntity> cycleMelodyCollection = new ArrayList<>();

    /**
     * アジェンダモニター設定
     */
    private AgendaMonitorSetting agenda;

    /**
     * モニターID
     */
    private long monitorId;

    // 進捗モニターレイアウト設定
    private String layout;

    // カスタマイズツールレイアウト設定
    private String customizeToolLayout;

    /**
     * コンストラクタ
     */
    public MonitorSettingFuji() {
    }

    /**
     * コンストラクタ
     *
     * @param monitorType
     */
    public MonitorSettingFuji(AndonMonitorTypeEnum monitorType) {
        this.monitorType = monitorType;
    }

    /**
     * 進捗モニタ設定を初期化して作成する。
     *
     * @return 進捗モニタ設定
     */
    public static MonitorSettingFuji create() {
        MonitorSettingFuji setting = new MonitorSettingFuji(AndonMonitorTypeEnum.LINE_PRODUCT);
        setting.setLineId(0L);
        setting.setTitle("");
        setting.setModelName("");
        setting.setStartWorkTime(LocalTime.of(9, 0, 0));
        setting.setEndWorkTime(LocalTime.of(17, 0, 0));
        setting.setBreaktimes(new ArrayList<>());
        setting.setWeekdays(Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));
        setting.setDailyPlanNum(0);
        setting.setMontlyPlanNum(0);
        setting.setResetTime(LocalTime.of(0, 0, 0));
        setting.setCautionRetentionParcent(5);
        setting.setCautionFontColor(Color.BLACK);
        setting.setCautionBackColor(Color.ORANGE);
        setting.setWarningRetentionParcent(10);
        setting.setWarningFontColor(Color.BLACK);
        setting.setWarningBackColor(Color.RED);
        setting.setWorkEquipmentCollection(new ArrayList<>());
        setting.setWorkCollection(new ArrayList<>());
        setting.setDelayReasonCollection(new ArrayList<>());
        setting.setInterruptReasonCollection(new ArrayList<>());
        setting.setRemoteLayout(false);
        setting.setUseDailyPlanNum(false);
        setting.setLayout(null);
        setting.setCustomizeToolLayout(null);
        return setting;
    }

    /**
     * モニターIDを取得する。
     *
     * @return モニターID
     */
    public long getMonitorId() {
        return monitorId;
    }

    /**
     * モニターIDを設定する。
     *
     * @param monitorId モニターID
     */
    public void setMonitorId(long monitorId) {
        this.monitorId = monitorId;
    }

    /**
     * モニター種別を取得する。
     *
     * @return
     */
    public AndonMonitorTypeEnum getMonitorType() {
        if (Objects.isNull(this.monitorType)) {
            this.monitorType = AndonMonitorTypeEnum.LINE_PRODUCT;
        }
        return this.monitorType;
    }

    /**
     * モニター種別を設定する。
     *
     * @param monitorType
     */
    public void setMonitorType(AndonMonitorTypeEnum monitorType) {
        this.monitorType = monitorType;
    }

    /**
     * ラインIDプロパティを取得する。
     *
     * @return ラインIDプロパティ
     */
    public LongProperty lineIdProperty() {
        if (Objects.isNull(lineIdProperty)) {
            lineIdProperty = new SimpleLongProperty(lineId);
        }
        return lineIdProperty;
    }

    /**
     * ラインIDを取得する。
     *
     * @return ラインID
     */
    public Long getLineId() {
        if (Objects.nonNull(lineIdProperty)) {
            return lineIdProperty.get();
        }
        return lineId;
    }

    /**
     * ラインIDを設定する。
     *
     * @param lineId ラインID
     */
    public void setLineId(Long lineId) {
        if (Objects.nonNull(lineIdProperty)) {
            lineIdProperty.set(lineId);
        } else {
            this.lineId = lineId;
        }
    }

    /**
     * 表示タイトルプロパティを取得する。
     *
     * @return 表示タイトルプロパティ
     */
    public StringProperty titleProperty() {
        if (Objects.isNull(titleProperty)) {
            titleProperty = new SimpleStringProperty(title);
        }
        return titleProperty;
    }

    /**
     * 表示タイトルを取得する。
     *
     * @return 表示タイトル
     */
    public String getTitle() {
        if (Objects.nonNull(titleProperty)) {
            return titleProperty.get();
        }
        return title;
    }

    /**
     * 表示タイトルを設定する。
     *
     * @param title 表示タイトル
     */
    public void setTitle(String title) {
        if (Objects.nonNull(titleProperty)) {
            titleProperty.set(title);
        } else {
            this.title = title;
        }
    }

    /**
     * モデル名プロパティを取得する。
     *
     * @return モデル名プロパティ
     */
    public StringProperty modelNameProperty() {
        if (Objects.isNull(this.modelNameProperty)) {
            this.modelNameProperty = new SimpleStringProperty(this.modelName);
        }
        return this.modelNameProperty;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        if (Objects.nonNull(modelNameProperty)) {
            return this.modelNameProperty.get();
        }
        return this.modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        if (Objects.nonNull(modelNameProperty)) {
            modelNameProperty.set(modelName);
        } else {
            this.modelName = modelName;
        }
    }

    /**
     * 作業開始時刻プロパティを取得する。
     *
     * @return 作業開始時刻プロパティ
     */
    public ObjectProperty<LocalTime> startWorkTimeProperty() {
        if (Objects.isNull(startWorkTimeProperty)) {
            startWorkTimeProperty = new SimpleObjectProperty<>(startWorkTime);
        }
        return startWorkTimeProperty;
    }

    /**
     * 作業開始時刻を取得する。
     *
     * @return 作業開始時刻
     */
    public LocalTime getStartWorkTime() {
        if (Objects.nonNull(startWorkTimeProperty)) {
            return startWorkTimeProperty.get();
        }
        return startWorkTime;
    }

    /**
     * 作業開始時刻を設定する。
     *
     * @param startWorkTime 作業開始時刻
     */
    public void setStartWorkTime(LocalTime startWorkTime) {
        if (Objects.nonNull(startWorkTimeProperty)) {
            startWorkTimeProperty.set(startWorkTime);
        } else {
            this.startWorkTime = startWorkTime;
        }
    }

    /**
     * 作業終了時刻プロパティを取得する。
     *
     * @return 作業終了時刻プロパティ
     */
    public ObjectProperty<LocalTime> endWorkTimeProperty() {
        if (Objects.isNull(endWorkTimeProperty)) {
            endWorkTimeProperty = new SimpleObjectProperty<>(endWorkTime);
        }
        return endWorkTimeProperty;
    }

    /**
     * サーバーレイアウトの有効無効設定を取得する
     * 
     * @return サーバーからレイアウトを取得する場合true
     */
    public BooleanProperty remoteLayoutProperty() {
        if (Objects.isNull(remoteLayoutProperty)) {
            remoteLayoutProperty = new SimpleBooleanProperty(remoteLayout);
        }
        return remoteLayoutProperty;
    }

    /**
     * ライン全体の当日計画数仕様設定を取得する
     *
     * @return
     */
    public BooleanProperty useDailyPlanNumProperty() {
        if (Objects.isNull(useDailyPlanNumProperty)) {
            useDailyPlanNumProperty = new SimpleBooleanProperty(useDailyPlanNum);
        }
        return useDailyPlanNumProperty;
    }

    /**
     * 作業終了時刻を取得する。
     *
     * @return 作業終了時刻
     */
    public LocalTime getEndWorkTime() {
        if (Objects.nonNull(endWorkTimeProperty)) {
            return endWorkTimeProperty.get();
        }
        return endWorkTime;
    }

    /**
     * 作業終了時刻を設定する。
     *
     * @param endWorkTime 作業終了時刻
     */
    public void setEndWorkTime(LocalTime endWorkTime) {
        if (Objects.nonNull(endWorkTimeProperty)) {
            endWorkTimeProperty.set(endWorkTime);
        } else {
            this.endWorkTime = endWorkTime;
        }
    }

    /**
     * 休憩時間情報一覧を取得する。
     *
     * @return 休憩時間情報一覧
     */
    @XmlElementWrapper(name = "breaktimes")
    @XmlElement(name = "breaktime")
    public List<BreakTimeInfoEntity> getBreaktimes() {
        return breaktimes;
    }

    /**
     * 休憩時間情報一覧を設定する。
     *
     * @param breaktimes 休憩時間情報一覧
     */
    public void setBreaktimes(List<BreakTimeInfoEntity> breaktimes) {
        this.breaktimes = breaktimes;
    }

    /**
     * 休日(曜日)一覧を取得する。
     *
     * @return 休日(曜日)一覧
     */
    @XmlElementWrapper(name = "weekdays")
    @XmlElement(name = "weekday")
    public List<DayOfWeek> getWeekdays() {
        return weekdays;
    }

    /**
     * 休日(曜日)一覧を設定する。
     *
     * @param weekdays 休日(曜日)一覧
     */
    public void setWeekdays(List<DayOfWeek> weekdays) {
        this.weekdays = weekdays;
    }

    /**
     * 当日計画数プロパティを取得する。
     *
     * @return 当日計画数プロパティ
     */
    public IntegerProperty dailyPlanNumProperty() {
        if (Objects.isNull(dailyPlanNumProperty)) {
            dailyPlanNumProperty = new SimpleIntegerProperty(dailyPlanNum);
        }
        return dailyPlanNumProperty;
    }

    /**
     * 当日計画数を取得する。
     *
     * @return 当日計画数
     */
    public Integer getDailyPlanNum() {
        if (Objects.nonNull(dailyPlanNumProperty)) {
            return dailyPlanNumProperty.get();
        }
        return dailyPlanNum;
    }

    /**
     * 当日計画数を設定する。
     *
     * @param dailyPlanNum 当日計画数
     */
    public void setDailyPlanNum(Integer dailyPlanNum) {
        if (Objects.nonNull(dailyPlanNumProperty)) {
            dailyPlanNumProperty.set(dailyPlanNum);
        } else {
            this.dailyPlanNum = dailyPlanNum;
        }
    }

    /**
     * 当月計画数プロパティを取得する。
     *
     * @return 当月計画数プロパティ
     */
    public IntegerProperty montlyPlanNumProperty() {
        if (Objects.isNull(montlyPlanNumProperty)) {
            montlyPlanNumProperty = new SimpleIntegerProperty(montlyPlanNum);
        }
        return montlyPlanNumProperty;
    }

    /**
     * 当月計画数を取得する。
     *
     * @return 当月計画数
     */
    public Integer getMontlyPlanNum() {
        if (Objects.nonNull(montlyPlanNumProperty)) {
            return montlyPlanNumProperty.get();
        }
        return montlyPlanNum;
    }

    /**
     * 当月計画数を設定する。
     *
     * @param montlyPlanNum 当月計画数
     */
    public void setMontlyPlanNum(Integer montlyPlanNum) {
        if (Objects.nonNull(montlyPlanNumProperty)) {
            montlyPlanNumProperty.set(montlyPlanNum);
        } else {
            this.montlyPlanNum = montlyPlanNum;
        }
    }

    // 未使用
    public ObjectProperty<LocalTime> resetTimeProperty() {
        if (Objects.isNull(resetTimeProperty)) {
            resetTimeProperty = new SimpleObjectProperty<>(resetTime);
        }
        return resetTimeProperty;
    }

    // 未使用
    public LocalTime getResetTime() {
        if (Objects.nonNull(resetTimeProperty)) {
            return resetTimeProperty.get();
        }
        return resetTime;
    }

    // 未使用
    public void setResetTime(LocalTime resetTime) {
        if (Objects.nonNull(resetTimeProperty)) {
            resetTimeProperty.set(resetTime);
        } else {
            this.resetTime = resetTime;
        }
    }

    /**
     * 滞留量の注意閾値[％]プロパティを取得する。
     *
     * @return 滞留量の注意閾値[％]プロパティ
     */
    public IntegerProperty cautionRetentionParcentProperty() {
        if (Objects.isNull(cautionRetentionParcentProperty)) {
            cautionRetentionParcentProperty = new SimpleIntegerProperty(cautionRetentionParcent);
        }
        return cautionRetentionParcentProperty;
    }

    /**
     * 滞留量の注意閾値[％]を取得する。
     *
     * @return 滞留量の注意閾値[％]
     */
    public Integer getCautionRetentionParcent() {
        if (Objects.nonNull(cautionRetentionParcentProperty)) {
            return cautionRetentionParcentProperty.get();
        }
        return cautionRetentionParcent;
    }

    /**
     * 滞留量の注意閾値[％]を設定する。
     *
     * @param cautionRetentionParcent 滞留量の注意閾値[％]
     */
    public void setCautionRetentionParcent(Integer cautionRetentionParcent) {
        if (Objects.nonNull(cautionRetentionParcentProperty)) {
            cautionRetentionParcentProperty.set(cautionRetentionParcent);
        } else {
            this.cautionRetentionParcent = cautionRetentionParcent;
        }
    }

    /**
     * 注意時の文字色プロパティを取得する。
     *
     * @return 注意時の文字色プロパティ
     */
    public ObjectProperty<Color> cautionFontColorProperty() {
        if (Objects.isNull(cautionFontColorProperty)) {
            cautionFontColorProperty = new SimpleObjectProperty<>(cautionFontColor);
        }
        return cautionFontColorProperty;
    }

    /**
     * 注意時の文字色を取得する。
     *
     * @return 注意時の文字色
     */
    public Color getCautionFontColor() {
        if (Objects.nonNull(cautionFontColorProperty)) {
            return cautionFontColorProperty.get();
        }
        return cautionFontColor;
    }

    /**
     * 注意時の文字色を設定する。
     *
     * @param cautionFontColor 注意時の文字色
     */
    public void setCautionFontColor(Color cautionFontColor) {
        if (Objects.nonNull(cautionFontColorProperty)) {
            cautionFontColorProperty.set(cautionFontColor);
        } else {
            this.cautionFontColor = cautionFontColor;
        }
    }

    /**
     * 注意時の背景色プロパティを取得する。
     *
     * @return 注意時の背景色プロパティ
     */
    public ObjectProperty<Color> cautionBackColorProperty() {
        if (Objects.isNull(cautionBackColorProperty)) {
            cautionBackColorProperty = new SimpleObjectProperty<>(cautionBackColor);
        }
        return cautionBackColorProperty;
    }

    /**
     * 注意時の背景色を取得する。
     *
     * @return 注意時の背景色
     */
    public Color getCautionBackColor() {
        if (Objects.nonNull(cautionBackColorProperty)) {
            return cautionBackColorProperty.get();
        }
        return cautionBackColor;
    }

    /**
     * 注意時の背景色を設定する。
     *
     * @param cautionBackColor 注意時の背景色
     */
    public void setCautionBackColor(Color cautionBackColor) {
        if (Objects.nonNull(cautionBackColorProperty)) {
            cautionBackColorProperty.set(cautionBackColor);
        } else {
            this.cautionBackColor = cautionBackColor;
        }
    }

    /**
     * 滞留量の警告閾値[％]プロパティを取得する。
     *
     * @return 滞留量の警告閾値[％]プロパティ
     */
    public IntegerProperty warningRetentionParcentProperty() {
        if (Objects.isNull(warningRetentionParcentProperty)) {
            warningRetentionParcentProperty = new SimpleIntegerProperty(warningRetentionParcent);
        }
        return warningRetentionParcentProperty;
    }

    /**
     * 滞留量の警告閾値[％]を取得する。
     *
     * @return 滞留量の警告閾値[％]
     */
    public Integer getWarningRetentionParcent() {
        if (Objects.nonNull(warningRetentionParcentProperty)) {
            return warningRetentionParcentProperty.get();
        }
        return warningRetentionParcent;
    }

    /**
     * 滞留量の警告閾値[％]を設定する。
     *
     * @param warningRetentionParcent 滞留量の警告閾値[％]
     */
    public void setWarningRetentionParcent(Integer warningRetentionParcent) {
        if (Objects.nonNull(warningRetentionParcentProperty)) {
            warningRetentionParcentProperty.set(warningRetentionParcent);
        } else {
            this.warningRetentionParcent = warningRetentionParcent;
        }
    }

    /**
     * 警告時の文字色プロパティを取得する。
     *
     * @return 警告時の文字色プロパティ
     */
    public ObjectProperty<Color> warningFontColorProperty() {
        if (Objects.isNull(warningFontColorProperty)) {
            warningFontColorProperty = new SimpleObjectProperty<>(warningFontColor);
        }
        return warningFontColorProperty;
    }

    /**
     * 警告時の文字色を取得する。
     *
     * @return 警告時の文字色
     */
    public Color getWarningFontColor() {
        if (Objects.nonNull(warningFontColorProperty)) {
            return warningFontColorProperty.get();
        }
        return warningFontColor;
    }

    /**
     * 警告時の文字色を設定する。
     *
     * @param warningFontColor 警告時の文字色
     */
    public void setWarningFontColor(Color warningFontColor) {
        if (Objects.nonNull(warningFontColorProperty)) {
            warningFontColorProperty.set(warningFontColor);
        } else {
            this.warningFontColor = warningFontColor;
        }
    }

    /**
     * 警告時の背景色プロパティを取得する。
     *
     * @return 警告時の背景色プロパティ
     */
    public ObjectProperty<Color> warningBackColorProperty() {
        if (Objects.isNull(warningBackColorProperty)) {
            warningBackColorProperty = new SimpleObjectProperty<>(warningBackColor);
        }
        return warningBackColorProperty;
    }

    /**
     * 警告時の背景色を取得する。
     *
     * @return 警告時の背景色
     */
    public Color getWarningBackColor() {
        if (Objects.nonNull(warningBackColorProperty)) {
            return warningBackColorProperty.get();
        }
        return warningBackColor;
    }

    /**
     * 警告時の背景色を設定する。
     *
     * @param warningBackColor 警告時の背景色
     */
    public void setWarningBackColor(Color warningBackColor) {
        if (Objects.nonNull(warningBackColorProperty)) {
            warningBackColorProperty.set(warningBackColor);
        } else {
            this.warningBackColor = warningBackColor;
        }
    }

    /**
     * 対象設備情報一覧を取得する。
     *
     * @return 対象設備情報一覧
     */
    @XmlElementWrapper(name = "workEquipments")
    @XmlElement(name = "workEquipment")
    public List<WorkEquipmentSetting> getWorkEquipmentCollection() {
        return workEquipmentCollection;
    }

    /**
     * 対象設備情報一覧を設定する。
     *
     * @param workEquipmentCollection 対象設備情報一覧
     */
    public void setWorkEquipmentCollection(List<WorkEquipmentSetting> workEquipmentCollection) {
        this.workEquipmentCollection = workEquipmentCollection;
    }

    /**
     * 対象工程情報一覧を取得する。
     *
     * @return 対象工程情報一覧
     */
    @XmlElementWrapper(name = "works")
    @XmlElement(name = "work")
    public List<WorkSetting> getWorkCollection() {
        return workCollection;
    }

    /**
     * 対象工程情報一覧を設定する。
     *
     * @param workCollection 対象工程情報一覧
     */
    public void setWorkCollection(List<WorkSetting> workCollection) {
        this.workCollection = workCollection;
    }

    /**
     * 遅延理由一覧を取得する。
     *
     * @return 遅延理由一覧
     */
    @XmlElementWrapper(name = "delayReasons")
    @XmlElement(name = "delayReason")
    public List<String> getDelayReasonCollection() {
        return delayReasonCollection;
    }

    /**
     * 遅延理由一覧を設定する。
     *
     * @param delayReasonCollection 遅延理由一覧
     */
    public void setDelayReasonCollection(List<String> delayReasonCollection) {
        this.delayReasonCollection = delayReasonCollection;
    }

    /**
     * 中断理由一覧を取得する。
     *
     * @return 中断理由一覧
     */
    @XmlElementWrapper(name = "interruptReasons")
    @XmlElement(name = "interruptReason")
    public List<String> getInterruptReasonCollection() {
        return interruptReasonCollection;
    }

    /**
     * 中断理由一覧を設定する。
     *
     * @param interruptReasonCollection 中断理由一覧
     */
    public void setInterruptReasonCollection(List<String> interruptReasonCollection) {
        this.interruptReasonCollection = interruptReasonCollection;
    }

    /**
     * ラインタクトタイム一覧プロパティを取得する。
     *
     * @return ラインタクトタイム一覧プロパティ
     */
    public ObjectProperty<LocalTime> lineTaktProperty() {
        if (Objects.isNull(this.lineTaktProperty)) {
            this.lineTaktProperty = new SimpleObjectProperty<>(lineTakt);
        }
        return this.lineTaktProperty;
    }

    /**
     * ラインタクトタイム一覧を取得する。
     *
     * @return ラインタクトタイム一覧
     */
    public LocalTime getLineTakt() {
        if (Objects.nonNull(this.lineTaktProperty)) {
            return this.lineTaktProperty.get();
        }
        return this.lineTakt;
    }

    /**
     * ラインタクトタイム一覧を設定する。
     *
     * @param lineTakt ラインタクトタイム一覧
     */
    public void setLineTakt(LocalTime lineTakt) {
        if (Objects.nonNull(this.lineTaktProperty)) {
            this.lineTaktProperty.set(lineTakt);
        } else {
            this.lineTakt = lineTakt;
        }
    }

    /**
     * サイクルタクト情報一覧を取得する。
     *
     * @return サイクルタクト情報一覧
     */
    @XmlElementWrapper(name = "cycleTakts")
    @XmlElement(name = "cycleTakt")
    public List<CycleTaktInfoEntity> getCycleTaktCollection() {
        return this.cycleTaktCollection;
    }

    /**
     * サイクルタクト情報一覧を設定する。
     *
     * @param cycleTaktCollection サイクルタクト情報一覧
     */
    public void setCycleTaktCollection(List<CycleTaktInfoEntity> cycleTaktCollection) {
        this.cycleTaktCollection = cycleTaktCollection;
    }

    /**
     * サイクルメロディ情報一覧を取得する。
     *
     * @return サイクルメロディ情報一覧
     */
    @XmlElementWrapper(name = "cycleMelodies")
    @XmlElement(name = "cycleMelody")
    public List<CycleMelodyInfoEntity> getCycleMelodyCollection() {
        return this.cycleMelodyCollection;
    }

    /**
     * サイクルメロディ情報一覧を設定する。
     *
     * @param cycleMelodyCollection サイクルメロディ情報一覧
     */
    public void setCycleMelodyCollection(List<CycleMelodyInfoEntity> cycleMelodyCollection) {
        this.cycleMelodyCollection = cycleMelodyCollection;
    }

    public Boolean getRemoteLayout() {
        if (Objects.nonNull(remoteLayoutProperty)) {
            return remoteLayoutProperty.get();
        } else {
            return remoteLayout;
        }
    }

    public void setRemoteLayout(Boolean remoteLayout) {
        if (Objects.nonNull(remoteLayoutProperty)) {
            remoteLayoutProperty.set(remoteLayout);
        } else {
            this.remoteLayout = remoteLayout;
        }
    }

    public Boolean getUseDailyPlanNum() {
        if (Objects.nonNull(useDailyPlanNumProperty)) {
            return useDailyPlanNumProperty.get();
        } else {
            return useDailyPlanNum;
        }
    }

    public void setUseDailyPlanNum(Boolean useDailyPlanNum) {
        if (Objects.nonNull(useDailyPlanNumProperty)) {
            useDailyPlanNumProperty.set(useDailyPlanNum);
        } else {
            this.useDailyPlanNum = useDailyPlanNum;
        }
    }

    /**
     * アジェンダモニタ設定を取得する。
     *
     * @return アジェンダモニタ設定
     */
    public AgendaMonitorSetting getAgendaMonitorSetting() {
        return agenda;
    }

    /**
     * アジェンダモニタ設定を設定する。
     *
     * @param agendaMonitorSetting アジェンダモニタ設定
     */
    public void setAgendaMonitorSetting(AgendaMonitorSetting agendaMonitorSetting) {
        this.agenda = agendaMonitorSetting;
    }

    public StringProperty layoutProperty() {
        if (Objects.isNull(layoutProperty)) {
            layoutProperty = new SimpleStringProperty(layout);
        }
        return layoutProperty;
    }

    public String getLayout() {

        if (Objects.nonNull(layoutProperty)) {
            return layoutProperty.get();
        }
        return layout;
    }

    public void setLayout(String layout) {
        if (Objects.nonNull(layoutProperty)) {
            layoutProperty.set(layout);
        } else {
            this.layout = layout;
        }
    }

    public StringProperty customizeToolLayoutProperty() {
        if (Objects.isNull(customizeToolLayoutProperty)) {
            customizeToolLayoutProperty = new SimpleStringProperty(customizeToolLayout);
        }
        return customizeToolLayoutProperty;
    }

    public String getCustomizeToolLayout() {

        if (Objects.nonNull(customizeToolLayoutProperty)) {
            return customizeToolLayoutProperty.get();
        }
        return customizeToolLayout;
    }

    public void setCustomizeToolLayout(String layout) {
        if (Objects.nonNull(customizeToolLayoutProperty)) {
            customizeToolLayoutProperty.set(layout);
        } else {
            this.customizeToolLayout = layout;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.getLineId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MonitorSettingFuji other = (MonitorSettingFuji) obj;
        return Objects.equals(this.getLineId(), other.getLineId());
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorSettingFuji{")
                .append("lineId=").append(this.lineId)
                .append(", ")
                .append("title=").append(this.title)
                .append(", ")
                .append("modelName=").append(this.modelName)
                .append(", ")
                .append("startWorkTime=").append(this.startWorkTime)
                .append(", ")
                .append("endWorkTime=").append(this.endWorkTime)
                //.append(", ")
                //.append("breaktimes=").append(this.breaktimes)
                .append(", ")
                .append("weekdays=").append(this.weekdays)
                .append(", ")
                .append("dailyPlanNum=").append(this.dailyPlanNum)
                .append(", ")
                .append("montlyPlanNum=").append(this.montlyPlanNum)
                .append(", ")
                .append("resetTime=").append(this.resetTime)
                .append(", ")
                .append("cautionRetentionParcent=").append(this.cautionRetentionParcent)
                .append(", ")
                .append("cautionFontColor=").append(this.cautionFontColor)
                .append(", ")
                .append("cautionBackColor=").append(this.cautionBackColor)
                .append(", ")
                .append("warningRetentionParcent=").append(this.warningRetentionParcent)
                .append(", ")
                .append("warningFontColor=").append(this.warningFontColor)
                .append(", ")
                .append("warningBackColor=").append(this.warningBackColor)
                //.append(", ")
                //.append("workEquipmentCollection=").append(this.workEquipmentCollection)
                //.append(", ")
                //.append("workCollection=").append(this.workCollection)
                .append(", ")
                .append("delayReasonCollection=").append(this.delayReasonCollection)
                .append(", ")
                .append("interruptReasonCollection=").append(this.interruptReasonCollection)
                .append(", ")
                .append("lineTakt=").append(this.lineTakt)
                //.append(", ")
                //.append("cycleTaktCollection=").append(this.cycleTaktCollection)
                //.append(", ")
                //.append("cycleMelodyCollection=").append(this.cycleMelodyCollection)
                .append("}")
                .toString();
    }

    /**
     * 進捗モニタ設定のコピーを新規作成する。
     *
     * @return 進捗モニタ設定
     */
    @Override
    public MonitorSettingFuji clone() {
        MonitorSettingFuji setting = new MonitorSettingFuji();

        setting.setTitle(this.getTitle());
        setting.setModelName(this.getModelName());
        setting.setLineId(this.getLineId());
        setting.setLineTakt(this.getLineTakt());
        setting.setCautionBackColor(this.getCautionBackColor());
        setting.setCautionFontColor(this.getCautionFontColor());
        setting.setWarningBackColor(this.getWarningBackColor());
        setting.setWarningFontColor(this.getWarningFontColor());
        setting.setResetTime(this.getResetTime());
        setting.setStartWorkTime(this.getStartWorkTime());
        setting.setEndWorkTime(this.getEndWorkTime());
        setting.setMontlyPlanNum(this.getMontlyPlanNum());
        setting.setDailyPlanNum(this.getDailyPlanNum());
        setting.setWarningRetentionParcent(this.getWarningRetentionParcent());
        setting.setCautionRetentionParcent(this.getCautionRetentionParcent());
        setting.setRemoteLayout(this.getRemoteLayout());
        setting.setUseDailyPlanNum(this.getUseDailyPlanNum());
        setting.setLayout(this.getLayout());
        setting.setCustomizeToolLayout(this.getCustomizeToolLayout());

        //中断理由
        setting.setInterruptReasonCollection(new ArrayList(this.getInterruptReasonCollection()));
        //遅延理由
        setting.setDelayReasonCollection(new ArrayList(this.getDelayReasonCollection()));
        //
        setting.setWeekdays(new ArrayList(this.getWeekdays()));
        //休憩時間
        setting.setBreaktimes(new ArrayList(this.getBreaktimes()));

        //設備情報
        List<WorkEquipmentSetting> workEquips = new LinkedList();
        this.getWorkEquipmentCollection().stream().forEach(c -> workEquips.add(c.clone()));
        setting.setWorkEquipmentCollection(workEquips);

        //工程設定
        List<WorkSetting> workSettings = new LinkedList();
        this.getWorkCollection().stream().forEach(c -> workSettings.add(c.clone()));
        setting.setWorkCollection(workSettings);

        List<CycleTaktInfoEntity> cycleTakts = new LinkedList();
        this.getCycleTaktCollection().stream().forEach(c -> cycleTakts.add(c.clone()));
        setting.setCycleTaktCollection(cycleTakts);

        List<CycleMelodyInfoEntity> cycleMelodies = new LinkedList();
        this.getCycleMelodyCollection().stream().forEach(c -> cycleMelodies.add(c.clone()));
        setting.setCycleMelodyCollection(cycleMelodies);
        
        setting.setMonitorType(this.getMonitorType());

        // アジェンダモニター設定
        if (Objects.nonNull(this.getAgendaMonitorSetting())) {
            setting.setAgendaMonitorSetting(this.getAgendaMonitorSetting().clone());
        }

        return setting;
    }

    /**
     * 各項目を比較して同じ情報を持つか確認する。
     *
     * @param setting 進捗モニタ設定
     * @return
     */
    public boolean equalsDisplayInfo(MonitorSettingFuji setting) {
        boolean ret = false;
        if (Objects.equals(this.getTitle(), setting.getTitle())
                && Objects.equals(this.getModelName(), setting.getModelName())
                && Objects.equals(this.getLineId(), setting.getLineId())
                && Objects.equals(this.getLineTakt(), setting.getLineTakt())
                && Objects.equals(this.getCautionFontColor(), setting.getCautionFontColor())
                && Objects.equals(this.getCautionBackColor(), setting.getCautionBackColor())
                && Objects.equals(this.getWarningBackColor(), setting.getWarningBackColor())
                && Objects.equals(this.getWarningFontColor(), setting.getWarningFontColor())
                && Objects.equals(this.getResetTime(), setting.getResetTime())
                && Objects.equals(this.getStartWorkTime(), setting.getStartWorkTime())
                && Objects.equals(this.getEndWorkTime(), setting.getEndWorkTime())
                && Objects.equals(this.getRemoteLayout(), setting.getRemoteLayout())
                && Objects.equals(this.getUseDailyPlanNum(), setting.getUseDailyPlanNum())
                && Objects.equals(this.getLayout(), setting.getLayout())
                && Objects.equals(this.getCustomizeToolLayout(), setting.getCustomizeToolLayout())
                && Objects.equals(this.getDailyPlanNum(), setting.getDailyPlanNum())
                && Objects.equals(this.getMontlyPlanNum(), setting.getMontlyPlanNum())
                && Objects.equals(this.getWarningRetentionParcent(), setting.getWarningRetentionParcent())
                && Objects.equals(this.getCautionRetentionParcent(), setting.getCautionRetentionParcent())
                && Objects.equals(this.getInterruptReasonCollection(), setting.getInterruptReasonCollection())
                && Objects.equals(this.getDelayReasonCollection(), setting.getDelayReasonCollection())
                && Objects.equals(this.getWeekdays(), setting.getWeekdays())
                && Objects.equals(this.getBreaktimes(), setting.getBreaktimes())
                && workEquipmentsEquals(this.getWorkEquipmentCollection(), setting.getWorkEquipmentCollection())
                && worksEquals(this.getWorkCollection(), setting.getWorkCollection())
                && cycleTaktsEquals(this.getCycleTaktCollection(), setting.getCycleTaktCollection())
                && cycleMelodiesEquals(this.getCycleMelodyCollection(), setting.getCycleMelodyCollection())
                && Objects.equals(this.getMonitorType(), setting.getMonitorType())
                // アジェンダモニター設定はNULLか、同じかで判断 (equalsが使えないため)
                && ((Objects.isNull(this.getAgendaMonitorSetting()) && Objects.isNull(setting.getAgendaMonitorSetting())) || this.getAgendaMonitorSetting().equalsDisplayInfo(setting.getAgendaMonitorSetting()))) {
            ret = true;
        }
        return ret;
    }

    /**
     * サイクルメロディが一致するか調べる。
     *
     * @param a
     * @param b
     * @return
     */
    private boolean cycleMelodiesEquals(List a, List b) {
        if (Objects.isNull(a) || Objects.isNull(b)) {
            return false;
        }

        if (a.size() != b.size()) {
            return false;
        }

        Iterator<CycleMelodyInfoEntity> it1 = a.iterator();
        Iterator<CycleMelodyInfoEntity> it2 = b.iterator();

        while (it1.hasNext()) {
            CycleMelodyInfoEntity entity1 = it1.next();
            CycleMelodyInfoEntity entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * サイクルタクトが一致するか調べる。
     *
     * @param a
     * @param b
     * @return
     */
    private boolean cycleTaktsEquals(List a, List b) {
        if (Objects.isNull(a) || Objects.isNull(b)) {
            return false;
        }

        if (a.size() != b.size()) {
            return false;
        }

        Iterator<CycleTaktInfoEntity> it1 = a.iterator();
        Iterator<CycleTaktInfoEntity> it2 = b.iterator();

        while (it1.hasNext()) {
            CycleTaktInfoEntity entity1 = it1.next();
            CycleTaktInfoEntity entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 設備情報が一致するか調べる。
     *
     * @param a
     * @param b
     * @return
     */
    private boolean workEquipmentsEquals(List a, List b) {
        if (Objects.isNull(a) || Objects.isNull(b)) {
            return false;
        }

        if (a.size() != b.size()) {
            return false;
        }

        Iterator<WorkEquipmentSetting> it1 = a.iterator();
        Iterator<WorkEquipmentSetting> it2 = b.iterator();

        while (it1.hasNext()) {
            WorkEquipmentSetting entity1 = it1.next();
            WorkEquipmentSetting entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 工程設定が一致するか調べる。
     *
     * @param a
     * @param b
     * @return
     */
    private boolean worksEquals(List a, List b) {
        if (Objects.isNull(a) || Objects.isNull(b)) {
            return false;
        }

        if (a.size() != b.size()) {
            return false;
        }

        Iterator<WorkSetting> it1 = a.iterator();
        Iterator<WorkSetting> it2 = b.iterator();

        while (it1.hasNext()) {
            WorkSetting entity1 = it1.next();
            WorkSetting entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }
}
