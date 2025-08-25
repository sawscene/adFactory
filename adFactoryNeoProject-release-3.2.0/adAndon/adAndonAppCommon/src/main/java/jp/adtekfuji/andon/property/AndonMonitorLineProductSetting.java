/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.property;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CompCountTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ProductionTypeEnum;
import jp.adtekfuji.andon.entity.CountdownMelodyInfoEntity;
import jp.adtekfuji.andon.enumerate.AndonMonitorTypeEnum;
import jp.adtekfuji.andon.enumerate.CountdownMelodyInfoTypeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * 進捗モニタ設定
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "andonMonitorLineProductSetting")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AndonMonitorLineProductSetting implements Serializable {

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
    private BooleanProperty autoCountdownProperty = null;
    private BooleanProperty followStartProperty = null;
    private StringProperty unitProperty = null;
    private ObjectProperty<Integer> targetMonitorProperty = null;  // ディスプレイ番号プロパティ
    private BooleanProperty remoteLayoutProperty = null;
    private StringProperty layoutProperty = null;
    private StringProperty customizeToolLayoutProperty = null;
    private BooleanProperty useDailyPlanNumProperty = null;
    private ObjectProperty<ProductionTypeEnum> productionTypeProperty = null;
    private ObjectProperty<CompCountTypeEnum> compCountTypeProperty = null;
    private BooleanProperty reportByWorkProperty = null;

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
    private List<CountdownMelodyInfoEntity> countdownMelodyInfoCollection = new ArrayList<>();
    private Boolean autoCountdown = false;
    private Boolean followStart = false;
    private String unit = "";
    private Integer targetMonitor = 1; // ディスプレイ番号
    private Boolean remoteLayout = false;
    private Boolean useDailyPlanNum = false;
    private ProductionTypeEnum productionType = ProductionTypeEnum.ONE_PIECE; // 生産タイプ
    private CompCountTypeEnum compCountType = CompCountTypeEnum.KANBAN; // 当日実績数のカウント方法
    private Boolean reportByWork = false; // 対象工程選択の情報を使用して工程進捗を表示する

    /**
     * アジェンダモニター設定
     */
    private AgendaMonitorSetting agenda;

    /**
     * モニターID
     */
    private Long monitorId;

    /**
     * 本日の作業開始時間
     */
    private Date todayStartTime = new Date(0L);
    
    // 進捗モニターレイアウト設定
    private String layout;
    
    // カスタマイズツールレイアウト設定
    private String customizeToolLayout;

    /**
     * コンストラクタ
     */
    public AndonMonitorLineProductSetting() {
    }

    /**
     * コンストラクタ
     *
     * @param monitorType
     */
    public AndonMonitorLineProductSetting(AndonMonitorTypeEnum monitorType) {
        this.monitorType = monitorType;
    }

    /**
     * 進捗モニタ設定を初期化して作成する。
     *
     * @return 進捗モニタ設定
     */
    public static AndonMonitorLineProductSetting create() {
        AndonMonitorLineProductSetting setting = new AndonMonitorLineProductSetting(AndonMonitorTypeEnum.LINE_PRODUCT);
        setting.setMonitorId(0L);
        setting.setLineId(0L);
        setting.setTargetMonitor(1);
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
        setting.setCountdownMelodyInfoCollection(new ArrayList<>());
        setting.getCountdownMelodyInfoCollection().add(new CountdownMelodyInfoEntity(CountdownMelodyInfoTypeEnum.TIME_RING_TIMING_END_OF_COUNTDOWN, "60"));
        setting.getCountdownMelodyInfoCollection().add(new CountdownMelodyInfoEntity(CountdownMelodyInfoTypeEnum.TIME_RING_TIMING_END_OF_BREAKTIME, "60"));
        setting.setAutoCountdown(false);
        setting.setFollowStart(false);
        setting.setUnit("");
        setting.setRemoteLayout(false);
        setting.setUseDailyPlanNum(false);
        setting.setLayout(null);
        setting.setCustomizeToolLayout(null);
        setting.setProductionType(ProductionTypeEnum.ONE_PIECE);
        setting.setCompCountType(CompCountTypeEnum.KANBAN);
        setting.setReportByWork(false);
        return setting;
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
     * サイクルカウントダウンメロディ情報一覧を取得する。
     *
     * @return サイクルカウントダウンメロディ情報一覧
     */
    @XmlElementWrapper(name = "countdownMelodiesInfo")
    @XmlElement(name = "countdownMelodyInfo")
    public List<CountdownMelodyInfoEntity> getCountdownMelodyInfoCollection() {
        return countdownMelodyInfoCollection;
    }

    /**
     * サイクルカウントダウンメロディ情報一覧を設定する。
     *
     * @param countdownMelodyInfoCollection サイクルカウントダウンメロディ情報一覧
     */
    public void setCountdownMelodyInfoCollection(List<CountdownMelodyInfoEntity> countdownMelodyInfoCollection) {
        this.countdownMelodyInfoCollection = countdownMelodyInfoCollection;
    }

    /**
     * カウントダウンモードプロパティを取得する。
     *
     * @return カウントダウンモードプロパティ
     */
    public BooleanProperty autoCountdownProperty() {
        if (Objects.isNull(this.autoCountdownProperty)) {
            this.autoCountdownProperty = new SimpleBooleanProperty(this.autoCountdown);
        }
        return this.autoCountdownProperty;
    }

    /**
     * カウントダウンモードを取得する。
     *
     * @return カウントダウンモード
     */
    public Boolean isAutoCountdown() {
        if (Objects.nonNull(this.autoCountdownProperty)) {
            return this.autoCountdownProperty.get();
        }
        return this.autoCountdown;
    }

    /**
     * カウントダウンモードを設定する。
     *
     * @param autoCountdown カウントダウンモード
     */
    public void setAutoCountdown(Boolean autoCountdown) {
        if (Objects.nonNull(this.autoCountdownProperty)) {
            this.autoCountdownProperty.set(autoCountdown);
        } else {
            this.autoCountdown = autoCountdown;
        }
    }

    /**
     * 作業開始時間追従プロパティを取得する。
     *
     * @return 作業開始時間追従プロパティ
     */
    public BooleanProperty followStartProperty() {
        if (Objects.isNull(this.followStartProperty)) {
            this.followStartProperty = new SimpleBooleanProperty(this.followStart);
        }
        return this.followStartProperty;
    }

    /**
     * 作業開始追従を取得する。
     *
     * @return false: 作業開始時間 true: 実際に作業が開始された時間
     * (進捗モニター設定の作業開始時間より以前に実行された場合は無視される)
     */
    public Boolean isFollowStart() {
        return this.followStart;
    }

    /**
     * 作業開始追従を設定する。
     *
     * @param followStart 作業開始追従
     */
    public void setFollowStart(Boolean followStart) {
        this.followStart = followStart;
    }

    /**
     * モニターIDを取得する。
     *
     * @return モニターID
     */
    public Long getMonitorId() {
        return monitorId;
    }

    /**
     * モニターIDを設定する。
     *
     * @param monitorId モニターID
     */
    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    /**
     * 本日の作業開始時間を取得する。
     *
     * @return 本日の作業開始時間
     */
    @XmlTransient
    public Date getTodayStartTime() {
        return todayStartTime;
    }

    /**
     * 本日の作業開始時間を設定する。
     *
     * @param todayStartTime 本日の作業開始時間
     */
    public void setTodayStartTime(Date todayStartTime) {
        this.todayStartTime = todayStartTime;
    }

    /**
     * 単位プロパティを取得する。
     *
     * @return 単位プロパティ
     */
    public StringProperty unitProperty() {
        if (Objects.isNull(unitProperty)) {
            unitProperty = new SimpleStringProperty(unit);
        }
        return unitProperty;
    }

    /**
     * 単位を取得する。
     *
     * @return 単位
     */
    public String getUnit() {
        if (Objects.nonNull(unitProperty)) {
            return unitProperty.get();
        }
        return unit;
    }

    /**
     * 単位を設定する。
     *
     * @param unit 単位
     */
    public void setUnit(String unit) {
        if (Objects.nonNull(unitProperty)) {
            unitProperty.set(unit);
        } else {
            this.unit = unit;
        }
    }
    
    /**
     * ディスプレイ番号プロパティを取得する。
     *
     * @return ディスプレイ番号プロパティ
     */
    public ObjectProperty<Integer> targetMonitorProperty() {
        if (Objects.isNull(targetMonitorProperty)) {
            this.targetMonitorProperty = new SimpleObjectProperty(this.targetMonitor);
        }
        return this.targetMonitorProperty;
    }

    /**
     * ディスプレイ番号を取得する。
     *
     * @return ディスプレイ番号
     */
    public int getTargetMonitor() {
        if (Objects.nonNull(this.targetMonitorProperty)) {
            return this.targetMonitorProperty.get();
        }
        return this.targetMonitor;
    }

    /**
     * ディスプレイ番号を設定する。
     *
     * @param targetMonitor ディスプレイ番号
     */
    public void setTargetMonitor(int targetMonitor) {
        if (Objects.nonNull(this.targetMonitorProperty)) {
            this.targetMonitorProperty.set(targetMonitor);
        } else {
            this.targetMonitor = targetMonitor;
        }
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

    /**
     * 生産タイププロパティを取得する。
     *
     * @return 生産タイププロパティ (0:一個流し生産, 1:ロット一個流し生産, 2:ロット生産)
     */
    public ObjectProperty<ProductionTypeEnum> productionTypeProperty() {
        if (Objects.isNull(this.productionTypeProperty)) {
            this.productionTypeProperty = new SimpleObjectProperty(this.productionType);
        }
        return this.productionTypeProperty;
    }

    /**
     * 生産タイプを取得する。
     *
     * @return 生産タイプ (0:一個流し生産, 1:ロット一個流し生産, 2:ロット生産)
     */
    public ProductionTypeEnum getProductionType() {
        if (Objects.nonNull(this.productionTypeProperty)) {
            return this.productionTypeProperty.get();
        }
        return this.productionType;
    }

    /**
     * 生産タイプを設定する。
     *
     * @param productionType 生産タイプ (0:一個流し生産, 1:ロット一個流し生産, 2:ロット生産)
     */
    public void setProductionType(ProductionTypeEnum productionType) {
        if (Objects.nonNull(this.productionTypeProperty)) {
            this.productionTypeProperty.set(productionType);
        } else {
            this.productionType = productionType;
        }
    }

    /**
     * 当日実績数のカウント方法プロパティを取得する。
     *
     * @return 当日実績数のカウント方法プロパティ
     */
    public ObjectProperty<CompCountTypeEnum> compCountTypeProperty() {
        if (Objects.isNull(this.compCountTypeProperty)) {
            this.compCountTypeProperty = new SimpleObjectProperty(this.compCountType);
        }
        return this.compCountTypeProperty;
    }

    /**
     * 当日実績数のカウント方法を取得する。
     *
     * @return 当日実績数のカウント方法
     */
    public CompCountTypeEnum getCompCountType() {
        if (Objects.nonNull(this.compCountTypeProperty)) {
            return this.compCountTypeProperty.get();
        }
        return this.compCountType;
    }

    /**
     * 当日実績数のカウント方法を設定する。
     *
     * @param compCountType 当日実績数のカウント方法
     */
    public void setCompCountType(CompCountTypeEnum compCountType) {
        if (Objects.nonNull(this.compCountTypeProperty)) {
            this.compCountTypeProperty.set(compCountType);
        } else {
            this.compCountType = compCountType;
        }
    }

    /**
     * 対象工程選択の情報を使用して工程進捗を表示するプロパティを取得する。
     * 
     * @return 
     */
    public BooleanProperty reportByWorkProperty() {
        if (Objects.isNull(this.reportByWorkProperty)) {
            this.reportByWorkProperty = new SimpleBooleanProperty(this.reportByWork);
        }
        return this.reportByWorkProperty;
    }

    /**
     * 対象工程選択の情報を使用して工程進捗を表示するかどうかを返す。
     * 
     * @return 
     */
    public Boolean isReportByWork() {
        if (Objects.nonNull(this.reportByWorkProperty)) {
            return this.reportByWorkProperty.get();
        }
        return this.reportByWork;
    }

    /**
     * 対象工程選択の情報を使用して工程進捗を表示するを設定する。
     * 
     * @param reportByWork 
     */
    public void setReportByWork(Boolean reportByWork) {
        if (Objects.nonNull(this.reportByWorkProperty)) {
            this.reportByWorkProperty.set(reportByWork);
        } else {
            this.reportByWork = reportByWork;
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
        final AndonMonitorLineProductSetting other = (AndonMonitorLineProductSetting) obj;
        return Objects.equals(this.getLineId(), other.getLineId());
    }

    @Override
    public String toString() {
        return new StringBuilder("AndonMonitorLineProductSetting{")
                .append("monitorType=").append(this.monitorType)
                .append(", monitorId=").append(this.monitorId)
                .append(", lineId=").append(this.lineId)
                .append(", title=").append(this.title)
                .append(", modelName=").append(this.modelName)
                .append(", startWorkTime=").append(this.startWorkTime)
                .append(", endWorkTime=").append(this.endWorkTime)
                .append(", weekdays=").append(this.weekdays)
                .append(", dailyPlanNum=").append(this.dailyPlanNum)
                .append(", montlyPlanNum=").append(this.montlyPlanNum)
                .append(", resetTime=").append(this.resetTime)
                .append(", cautionRetentionParcent=").append(this.cautionRetentionParcent)
                .append(", cautionFontColor=").append(this.cautionFontColor)
                .append(", cautionBackColor=").append(this.cautionBackColor)
                .append(", warningRetentionParcent=").append(this.warningRetentionParcent)
                .append(", warningFontColor=").append(this.warningFontColor)
                .append(", warningBackColor=").append(this.warningBackColor)
                .append(", delayReasonCollection=").append(this.delayReasonCollection)
                .append(", interruptReasonCollection=").append(this.interruptReasonCollection)
                .append(", lineTakt=").append(this.lineTakt)
                .append(", autoCountdown=").append(this.autoCountdown)
                .append(", followStart=").append(this.followStart)
                .append(", unit=").append(this.unit)
                .append(", targetMonitor=").append(this.targetMonitor)
                .append(", productionType=").append(this.productionType)
                .append(", compCountType=").append(this.compCountType)
                .append("}")
                .toString();
    }

    /**
     * テスト用
     *
     * @return
     * @throws java.lang.IllegalAccessException
     */
    public boolean hasAllAttributes() throws IllegalArgumentException, IllegalAccessException {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.getName().contains("Property") && !field.getName().contains("agendaMonitorSetting") && !field.getName().contains("layout") && !field.getName().contains("customizeToolLayout")) {
                if (field.get(this) == null) {
                    return false;
                }
                System.out.println(field.get(this));
            }
        }
        return true;
    }

    /**
     * 進捗モニタ設定のコピーを新規作成する。
     *
     * @return 進捗モニタ設定
     */
    @Override
    public AndonMonitorLineProductSetting clone() {
        AndonMonitorLineProductSetting setting = new AndonMonitorLineProductSetting(this.getMonitorType());

        setting.setTargetMonitor(this.getTargetMonitor());
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
        setting.setUnit(this.getUnit());
        setting.setMontlyPlanNum(this.getMontlyPlanNum());
        setting.setDailyPlanNum(this.getDailyPlanNum());
        setting.setWarningRetentionParcent(this.getWarningRetentionParcent());
        setting.setCautionRetentionParcent(this.getCautionRetentionParcent());
        setting.setRemoteLayout(this.getRemoteLayout());
        setting.setUseDailyPlanNum(this.getUseDailyPlanNum());
        setting.setLayout(this.getLayout());
        setting.setCustomizeToolLayout(this.getCustomizeToolLayout());

        // 中断理由
        setting.setInterruptReasonCollection(new ArrayList(this.getInterruptReasonCollection()));
        // 遅延理由
        setting.setDelayReasonCollection(new ArrayList(this.getDelayReasonCollection()));
        //
        setting.setWeekdays(new ArrayList(this.getWeekdays()));
        // 休憩時間
        setting.setBreaktimes(new ArrayList(this.getBreaktimes()));

        // 設備情報
        List<WorkEquipmentSetting> workEquips = new LinkedList();
        this.getWorkEquipmentCollection().stream().forEach(c -> workEquips.add(c.clone()));
        setting.setWorkEquipmentCollection(workEquips);

        // 工程設定
        List<WorkSetting> workSettings = new LinkedList();
        this.getWorkCollection().stream().forEach(c -> workSettings.add(c.clone()));
        setting.setWorkCollection(workSettings);

        // メロディ
        List<CountdownMelodyInfoEntity> melodies = new LinkedList();
        this.getCountdownMelodyInfoCollection().stream().forEach(c -> melodies.add(c.clone()));
        setting.setCountdownMelodyInfoCollection(melodies);

        // 自動カウントダウン
        setting.setAutoCountdown(this.isAutoCountdown());
        // 開始を追従する
        setting.setFollowStart(this.isFollowStart());
        // モデル名
        setting.setModelName(this.getModelName());

        setting.setMonitorType(this.getMonitorType());

        if (Objects.nonNull(this.getAgendaMonitorSetting())) {
            setting.setAgendaMonitorSetting(this.getAgendaMonitorSetting().clone());
        }

        setting.setProductionType(this.getProductionType());// 生産タイプ
        setting.setCompCountType(this.getCompCountType());// 当日実績数のカウント方法
        setting.setReportByWork(this.isReportByWork());

        return setting;
    }

    /**
     * 各項目を比較して同じ情報を持つか確認する。
     *
     * @param setting　進捗モニタ設定
     * @return
     */
    public boolean equalsDisplayInfo(AndonMonitorLineProductSetting setting) {
        boolean ret = false;
        if (StringUtils.equals(getTitle(), setting.getTitle())
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
                && Objects.equals(this.getUnit(), setting.getUnit())
                && Objects.equals(this.getTargetMonitor(), setting.getTargetMonitor())
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
                && countdownMelodiesEquals(this.getCountdownMelodyInfoCollection(), setting.getCountdownMelodyInfoCollection())
                && Objects.equals(this.isAutoCountdown(), setting.isAutoCountdown())
                && Objects.equals(this.isFollowStart(), setting.isFollowStart())
                && Objects.equals(this.getMonitorType(), setting.getMonitorType())
                // アジェンダモニター設定はNULLか、同じかで判断 (equalsが使えないため)
                && ((Objects.isNull(this.getAgendaMonitorSetting()) && Objects.isNull(setting.getAgendaMonitorSetting())) || this.getAgendaMonitorSetting().equalsDisplayInfo(setting.getAgendaMonitorSetting()))
                && Objects.equals(this.getProductionType(), setting.getProductionType())// 生産タイプ
                && Objects.equals(this.getCompCountType(), setting.getCompCountType())// 当日実績数のカウント方法
                && Objects.equals(this.isReportByWork(), setting.isReportByWork())
                ) {
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

    /**
     * カウントダウンメロディの内容が一致するか調べる。
     *
     * @param a
     * @param b
     * @return
     */
    private boolean countdownMelodiesEquals(List a, List b) {
        if (Objects.isNull(a) || Objects.isNull(b)) {
            return false;
        }

        if (a.size() != b.size()) {
            return false;
        }

        Iterator<CountdownMelodyInfoEntity> it1 = a.iterator();
        Iterator<CountdownMelodyInfoEntity> it2 = b.iterator();

        while (it1.hasNext()) {
            CountdownMelodyInfoEntity entity1 = it1.next();
            CountdownMelodyInfoEntity entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }
}
