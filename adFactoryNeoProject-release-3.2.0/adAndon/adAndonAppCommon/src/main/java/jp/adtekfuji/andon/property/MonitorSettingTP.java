/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.property;

import adtekfuji.utility.StringUtils;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;

/**
 * 進捗モニタ設定情報 (トプコン様用)
 *
 * @author s-heya
 */
@XmlRootElement(name = "andonMonitorLineProductSetting")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class MonitorSettingTP implements Serializable {

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
    private BooleanProperty useDailyPlanNumProperty = null;

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
    private Boolean useDailyPlanNum = false;

    // グループ進捗
    private IntegerProperty groupAttenThresholdProperty;
    private IntegerProperty groupWarnThresholdProperty;
    private IntegerProperty yieldDiffProperty;
    private Integer groupAttenThreshold = 0;
    private Integer groupWarnThreshold = 0;
    private Integer yieldDiff;
    private List<WorkSetting> groupWorkCollection = new ArrayList<>();

    // 工程実績進捗
    private IntegerProperty workAttenThresholdProperty;
    private IntegerProperty workWarnThresholdProperty;
    private Integer workAttenThreshold = 0;
    private Integer workWarnThreshold = 0;
    private List<WorkEquipmentSetting> workActualCollection = new ArrayList<>();

    // 中断発生率
    private StringProperty suspendedTitleProperty;
    private IntegerProperty suspendedAttenThresholdProperty;
    private IntegerProperty suspendedWarnThresholdProperty;
    private String suspendedTitle;
    private Integer suspendedAttenThreshold = 0;
    private Integer suspendedWarnThreshold = 0;
    private List<WorkSetting> suspendedWorkCollection = new ArrayList<>();

    /**
     * コンストラクタ
     */
    public MonitorSettingTP() {
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
     * グループ進捗 注意閾値 プロパティを取得する。
     *
     * @return グループ進捗 注意閾値 プロパティ
     */
    public IntegerProperty groupAttenThresholdProperty() {
        if (Objects.isNull(this.groupAttenThresholdProperty)) {
            this.groupAttenThresholdProperty = new SimpleIntegerProperty(this.groupAttenThreshold);
        }
        return this.groupAttenThresholdProperty;
    }

    /**
     * グループ進捗 注意閾値を取得する。
     *
     * @return グループ進捗 注意閾値
     */
    public Integer getGroupAttenThreshold() {
        if (Objects.nonNull(this.groupAttenThresholdProperty)) {
            return this.groupAttenThresholdProperty.get();
        }
        return this.groupAttenThreshold;
    }

    /**
     * グループ進捗 注意閾値を設定する。
     *
     * @param groupAttenThreshold グループ進捗 注意閾値
     */
    public void setGroupAttenThreshold(Integer groupAttenThreshold) {
        if (Objects.nonNull(this.groupAttenThresholdProperty)) {
            this.groupAttenThresholdProperty.set(groupAttenThreshold);
        } else {
            this.groupAttenThreshold = groupAttenThreshold;
        }
    }

    /**
     * グループ進捗 警告閾値 プロパティを取得する。
     *
     * @return グループ進捗 警告閾値 プロパティ
     */
    public IntegerProperty groupWarnThresholdProperty() {
        if (Objects.isNull(this.groupWarnThresholdProperty)) {
            this.groupWarnThresholdProperty = new SimpleIntegerProperty(this.groupWarnThreshold);
        }
        return this.groupWarnThresholdProperty;
    }

    /**
     * 出来高差異の表示対象工程のプロパティを取得する。
     * 
     * @return 出来高差異の表示対象工程のプロパティ
     */
    public IntegerProperty yieldDiffProperty() {
        if (Objects.isNull(this.yieldDiffProperty)) {
            this.yieldDiffProperty = Objects.nonNull(this.yieldDiff) ? new SimpleIntegerProperty(this.yieldDiff) : new SimpleIntegerProperty();
        }
        return this.yieldDiffProperty;
    }

    /**
     * グループ進捗 警告閾値を取得する。
     *
     * @return グループ進捗 警告閾値
     */
    public Integer getGroupWarnThreshold() {
        if (Objects.nonNull(this.groupWarnThresholdProperty)) {
            return this.groupWarnThresholdProperty.get();
        }
        return this.groupWarnThreshold;
    }

    /**
     * グループ進捗 警告閾値を設定する。
     *
     * @param groupWarnThreshold グループ進捗 警告閾値
     */
    public void setGroupWarnThreshold(Integer groupWarnThreshold) {
        if (Objects.nonNull(this.groupWarnThresholdProperty)) {
            this.groupWarnThresholdProperty.set(groupWarnThreshold);
        } else {
            this.groupWarnThreshold = groupWarnThreshold;
        }
    }

    /**
     * 出来高差異の表示対象工程を取得する。
     * 
     * @return 出来高差異の表示対象のグループ工程
     */
    public Integer getYieldDiff() {
        if (Objects.nonNull(this.yieldDiffProperty)) {
            return this.yieldDiffProperty.get();
        }
        return yieldDiff;
    }

    /**
     * 出来高差異の表示対象工程を設定する。
     * 
     * @param yieldDiff 出来高差異の表示対象のグループ工程
     */
    public void setYieldDiff(Integer yieldDiff) {
        if (Objects.nonNull(this.yieldDiffProperty)) {
            this.yieldDiffProperty.set(yieldDiff);
        } else {
            this.yieldDiff = yieldDiff;
        }
    }

    /**
     * グループ情報を取得する。
     *
     * @return グループ情報
     */
    @XmlElementWrapper(name = "groupWorks")
    @XmlElement(name = "setting")
    public List<WorkSetting> getGroupWorkCollection() {
        return this.groupWorkCollection;
    }

    /**
     * グループ情報を設定する。
     *
     * @param groupWorkCollection グループ情報
     */
    public void setGroupWorkCollection(List<WorkSetting> groupWorkCollection) {
        this.groupWorkCollection = groupWorkCollection;
    }

    /**
     * 工程実績進捗 注意閾値 プロパティを取得する。
     *
     * @return 工程実績進捗 注意閾値 プロパティ
     */
    public IntegerProperty workAttenThresholdProperty() {
        if (Objects.isNull(this.workAttenThresholdProperty)) {
            this.workAttenThresholdProperty = new SimpleIntegerProperty(this.workAttenThreshold);
        }
        return this.workAttenThresholdProperty;
    }

    /**
     * 工程実績進捗 注意閾値を取得する。
     *
     * @return 工程実績進捗 注意閾値
     */
    public Integer getWorkAttenThreshold() {
        if (Objects.nonNull(this.workAttenThresholdProperty)) {
            return this.workAttenThresholdProperty.get();
        }
        return this.workAttenThreshold;
    }

    /**
     * 工程実績進捗 注意閾値を設定する。
     *
     * @param workAttenThreshold 工程実績進捗 注意閾値
     */
    public void setWorkAttenThreshold(Integer workAttenThreshold) {
        if (Objects.nonNull(this.workAttenThresholdProperty)) {
            this.workAttenThresholdProperty.set(workAttenThreshold);
        } else {
            this.workAttenThreshold = workAttenThreshold;
        }
    }

    /**
     * 工程実績進捗 警告閾値 プロパティを取得する。
     *
     * @return 工程実績進捗 警告閾値 プロパティ
     */
    public IntegerProperty workWarnThresholdProperty() {
        if (Objects.isNull(this.workWarnThresholdProperty)) {
            this.workWarnThresholdProperty = new SimpleIntegerProperty(this.workWarnThreshold);
        }
        return this.workWarnThresholdProperty;
    }

    /**
     * 工程実績進捗 警告閾値を取得する。
     *
     * @return 工程実績進捗 警告閾値
     */
    public Integer getWorkWarnThreshold() {
        if (Objects.nonNull(this.workWarnThresholdProperty)) {
            return this.workWarnThresholdProperty.get();
        }
        return this.workWarnThreshold;
    }

    /**
     * 工程実績進捗 警告閾値を設定する。
     *
     * @param workWarnThreshold 工程実績進捗 警告閾値
     */
    public void setWorkWarnThreshold(Integer workWarnThreshold) {
        if (Objects.nonNull(this.workWarnThresholdProperty)) {
            this.workWarnThresholdProperty.set(workWarnThreshold);
        } else {
            this.workWarnThreshold = workWarnThreshold;
        }
    }

    /**
     * 工程実績情報を取得する。
     *
     * @return 工程実績情報
     */
    @XmlElementWrapper(name = "workActuals")
    @XmlElement(name = "setting")
    public List<WorkEquipmentSetting> getWorkActualCollection() {
        return this.workActualCollection;
    }

    /**
     * 工程実績情報を設定する。
     *
     * @param workActualCollection 工程実績情報
     */
    public void setWorkActualCollection(List<WorkEquipmentSetting> workActualCollection) {
        this.workActualCollection = workActualCollection;
    }

    /**
     * 中断発生率 タイトル プロパティを取得する。
     *
     * @return 中断発生率 タイトル プロパティ
     */
    public StringProperty suspendedTitleProperty() {
        if (Objects.isNull(this.suspendedTitleProperty)) {
            if (StringUtils.isEmpty(this.suspendedTitle)) {
                this.suspendedTitle = "";
            }
            this.suspendedTitleProperty = new SimpleStringProperty(this.suspendedTitle);
        }
        return this.suspendedTitleProperty;
    }

    /**
     * 中断発生率 タイトルを取得する。
     *
     * @return 中断発生率 タイトル
     */
    public String getSuspendedTitle() {
        if (Objects.nonNull(this.suspendedTitleProperty)) {
            return this.suspendedTitleProperty.get();
        }
        return this.suspendedTitle;
    }

    /**
     * 中断発生率 タイトルを設定する。
     *
     * @param suspenedTitle 中断発生率 タイトル
     */
    public void setSuspendedTitle(String suspenedTitle) {
        if (Objects.nonNull(this.suspendedTitleProperty)) {
            this.suspendedTitleProperty.set(suspenedTitle);
        } else {
            this.suspendedTitle = suspenedTitle;
        }
    }

    /**
     * 中断発生率 注意閾値 プロパティを取得する。
     *
     * @return 中断発生率 注意閾値 プロパティ
     */
    public IntegerProperty suspendedAttenThresholdProperty() {
        if (Objects.isNull(this.suspendedAttenThresholdProperty)) {
            this.suspendedAttenThresholdProperty = new SimpleIntegerProperty(this.suspendedAttenThreshold);
        }
        return this.suspendedAttenThresholdProperty;
    }

    /**
     * 中断発生率 注意閾値を取得する。
     *
     * @return 中断発生率 注意閾値
     */
    public Integer getSuspendedAttenThreshold() {
        if (Objects.nonNull(this.suspendedAttenThresholdProperty)) {
            return this.suspendedAttenThresholdProperty.get();
        }
        return this.suspendedAttenThreshold;
    }

    /**
     * 中断発生率 注意閾値を設定する。
     *
     * @param suspendedAttenThreshold 中断発生率 注意閾値
     */
    public void setSuspendedAttenThreshold(Integer suspendedAttenThreshold) {
        if (Objects.nonNull(this.suspendedAttenThresholdProperty)) {
            this.suspendedAttenThresholdProperty.set(suspendedAttenThreshold);
        } else {
            this.suspendedAttenThreshold = suspendedAttenThreshold;
        }
    }

    /**
     * 中断発生率 警告閾値 プロパティを取得する。
     *
     * @return 中断発生率 警告閾値 プロパティ
     */
    public IntegerProperty suspendedWarnThresholdProperty() {
        if (Objects.isNull(this.suspendedWarnThresholdProperty)) {
            this.suspendedWarnThresholdProperty = new SimpleIntegerProperty(this.suspendedWarnThreshold);
        }
        return this.suspendedWarnThresholdProperty;
    }

    /**
     * 中断発生率 警告閾値を取得する。
     *
     * @return 中断発生率 警告閾値
     */
    public Integer getSuspendedWarnThreshold() {
        if (Objects.nonNull(this.suspendedWarnThresholdProperty)) {
            return this.suspendedWarnThresholdProperty.get();
        }
        return this.suspendedWarnThreshold;
    }

    /**
     * 中断発生率 警告閾値を設定する。
     *
     * @param suspendedWarnThreshold 中断発生率 警告閾値
     */
    public void setSuspendedWarnThreshold(Integer suspendedWarnThreshold) {
        if (Objects.nonNull(this.suspendedWarnThresholdProperty)) {
            this.suspendedWarnThresholdProperty.set(suspendedWarnThreshold);
        } else {
            this.suspendedWarnThreshold = suspendedWarnThreshold;
        }
    }

    /**
     * 中断発生率情報を取得する。
     *
     * @return 中断発生率情報
     */
    @XmlElementWrapper(name = "suspendedWorks")
    @XmlElement(name = "setting")
    public List<WorkSetting> getSuspendedWorkCollection() {
        return this.suspendedWorkCollection;
    }

    /**
     * 中断発生率情報を設定する。
     *
     * @param suspendedWorkCollection 中断発生率情報
     */
    public void setSuspendedWorkCollection(List<WorkSetting> suspendedWorkCollection) {
        this.suspendedWorkCollection = suspendedWorkCollection;
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
        final MonitorSettingTP other = (MonitorSettingTP) obj;
        return Objects.equals(this.getLineId(), other.getLineId());
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorSettingTP{")
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
                .append(", ")
                .append("groupAttenThreshold=").append(this.groupAttenThreshold)
                .append(", ")
                .append("groupWarnThreshold=").append(this.groupWarnThreshold)
                //.append(", ")
                //.append("groupWorkCollection=").append(this.groupWorkCollection)
                .append(", ")
                .append("workAttenThreshold=").append(this.workAttenThreshold)
                .append(", ")
                .append("workWarnThreshold=").append(this.workWarnThreshold)
                //.append(", ")
                //.append("workActualCollection=").append(this.workActualCollection)
                .append(", ")
                .append("suspendedTitle=").append(this.suspendedTitle)
                .append(", ")
                .append("suspendedAttenThreshold=").append(this.suspendedAttenThreshold)
                .append(", ")
                .append("suspendedWarnThreshold=").append(this.suspendedWarnThreshold)
                //.append(", ")
                //.append("suspendedWorkCollection=").append(this.suspendedWorkCollection)
                .append("}")
                .toString();
    }

    /**
     * 進捗モニタ設定のコピーを新規作成する。
     *
     * @return 進捗モニタ設定
     */
    @Override
    public MonitorSettingTP clone() {
        MonitorSettingTP setting = new MonitorSettingTP();

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
        setting.setUseDailyPlanNum(this.getUseDailyPlanNum());

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

        //グループ情報
        List<WorkSetting> groupWorks = new LinkedList();
        this.getGroupWorkCollection().stream().forEach(c -> groupWorks.add(c.clone()));
        setting.setGroupWarnThreshold(this.getGroupWarnThreshold());
        setting.setGroupAttenThreshold(this.getGroupAttenThreshold());
        setting.setYieldDiff(this.getYieldDiff());
        setting.setGroupWorkCollection(groupWorks);

        //工程実績情報
        List<WorkEquipmentSetting> workActuals = new LinkedList();
        this.getWorkActualCollection().stream().forEach(c -> workActuals.add(c.clone()));
        setting.setWorkWarnThreshold(this.getWorkWarnThreshold());
        setting.setWorkAttenThreshold(this.getWorkAttenThreshold());
        setting.setWorkActualCollection(workActuals);

        //中断発生率状況
        List<WorkSetting> suspends = new LinkedList();
        this.getSuspendedWorkCollection().stream().forEach(c -> suspends.add(c.clone()));
        setting.setSuspendedWarnThreshold(this.getSuspendedWarnThreshold());
        setting.setSuspendedAttenThreshold(this.getSuspendedAttenThreshold());
        setting.setSuspendedTitle(this.getSuspendedTitle());
        setting.setSuspendedWorkCollection(suspends);

        return setting;
    }

    /**
     * 各項目を比較して同じ情報を持つか確認する
     *
     * @param s
     * @return
     */
    public boolean equalsDisplayInfo(MonitorSettingTP s) {
        boolean ret = false;
        if (Objects.equals(this.getTitle(), s.getTitle())
                && Objects.equals(this.getModelName(), s.getModelName())
                && Objects.equals(this.getLineId(), s.getLineId())
                && Objects.equals(this.getLineTakt(), s.getLineTakt())
                && Objects.equals(this.getCautionFontColor(), s.getCautionFontColor())
                && Objects.equals(this.getCautionBackColor(), s.getCautionBackColor())
                && Objects.equals(this.getWarningBackColor(), s.getWarningBackColor())
                && Objects.equals(this.getWarningFontColor(), s.getWarningFontColor())
                && Objects.equals(this.getResetTime(), s.getResetTime())
                && Objects.equals(this.getStartWorkTime(), s.getStartWorkTime())
                && Objects.equals(this.getEndWorkTime(), s.getEndWorkTime())
                && Objects.equals(this.getDailyPlanNum(), s.getDailyPlanNum())
                && Objects.equals(this.getMontlyPlanNum(), s.getMontlyPlanNum())
                && Objects.equals(this.getWarningRetentionParcent(), s.getWarningRetentionParcent())
                && Objects.equals(this.getCautionRetentionParcent(), s.getCautionRetentionParcent())
                && Objects.equals(this.getInterruptReasonCollection(), s.getInterruptReasonCollection())
                && Objects.equals(this.getDelayReasonCollection(), s.getDelayReasonCollection())
                && Objects.equals(this.getWeekdays(), s.getWeekdays())
                && Objects.equals(this.getBreaktimes(), s.getBreaktimes())
                && workEquipmentsEquals(this.getWorkEquipmentCollection(), s.getWorkEquipmentCollection())
                && worksEquals(this.getWorkCollection(), s.getWorkCollection())
                && Objects.equals(this.getGroupAttenThreshold(), s.getGroupAttenThreshold())
                && Objects.equals(this.getGroupWarnThreshold(), s.getGroupWarnThreshold())
                && worksEquals(this.getGroupWorkCollection(), s.getGroupWorkCollection())
                && Objects.equals(this.getWorkAttenThreshold(), s.getWorkAttenThreshold())
                && Objects.equals(this.getWorkWarnThreshold(), s.getWorkWarnThreshold())
                && workEquipmentsEquals(this.getWorkActualCollection(), s.getWorkActualCollection())
                && Objects.equals(this.getSuspendedTitle(), s.getSuspendedTitle())
                && Objects.equals(this.getSuspendedAttenThreshold(), s.getSuspendedAttenThreshold())
                && Objects.equals(this.getSuspendedWarnThreshold(), s.getSuspendedWarnThreshold())
                && worksEquals(this.getSuspendedWorkCollection(), s.getSuspendedWorkCollection())
                && Objects.equals(this.getYieldDiff(), s.getYieldDiff())) {
            ret = true;
        }
        return ret;
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
