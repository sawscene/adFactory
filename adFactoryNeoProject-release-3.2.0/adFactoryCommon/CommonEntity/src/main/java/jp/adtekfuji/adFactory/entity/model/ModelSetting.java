/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * モデル設定
 *
 * @author kentarou.suzuki
 */
@XmlRootElement(name = "modelSetting")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelSetting implements Serializable {

    /**
     * エンティティのバージョン
     */
    private static final long serialVersionUID = 1L;

    /**
     * 初期値 (表示単位)
     */
    private static final String DEFAULT_DISPLAY_UNIT = "台";

    ////------------------------ 生産数 ------------------------------
    /**
     * 初期値 (生産数：非表示にするかどうか)
     */
    private static final Boolean DEFAULT_PRODUCTION_VOLUME_INVISIBLE = false;

    /**
     * 初期値 (生産数：達成率差異の閾値(%))
     */
    private static final Double DEFAULT_ACHIEVEMENT_RATE_THRESHOLD = -10.0;

    ////----------------------- 稼働率(モデル) ------------------------------
    /**
     * 初期値 (稼働率：非表示にするかどうか)
     */
    private static final Boolean DEFAULT_OPERATING_RATE_INVISIBLE = false;

    /**
     * 初期値 (稼働率：目標稼働率(%))
     */
    private static final Double DEFAULT_TARGET_OPERATING_RATE = 98.0;

    /**
     * 初期値 (稼働率：達成率差異の閾値(%))
     */
    private static final Double DEFAULT_OPERATING_RATE_THRESHOLD = -10.0;


    ////-------------------- 稼働率(作業者)---------------------------
    /**
     * 初期値 (稼働率：作業員一人当たりの目標稼働率(%))
     */
    private static final Double DEFAULT_TARGET_OPERATING_RATE_PER_PERSON = 95.0;

    /**
     * 初期値 (稼働率：作業員一人当たりの稼働率差異の閾値(%))
     */
    private static final Double DEFAULT_OPERATING_RATE_THRESHOLD_PER_PERSON = -5.0;

    /**
     * 初期値 (タクトタイム：非表示にするかどうか)
     */
    private static final Boolean DEFAULT_TAKTTIME_INVISIBLE = false;

    /**
     * 初期値 (タクトタイム：達成度差異の閾値(%))
     */
    private static final Double DEFAULT_TAKTTIME_ACHIEVEMENTR_RATE_THRESHOLD = 10.0;

    /**
     * 初期値 (中断理由ランキング：非表示にするかどうか)
     */
    private static final Boolean DEFAULT_INTERRUPTREASON_INVISIBLE = false;

    /**
     * 初期値 (中断理由ランキング：ランキング上限数)
     */
    private static final Integer DEFAULT_MAX_INTERRUPTREASON_NUN = 3;

    /**
     * 初期値 (中断理由ランキング：中断ロス時間の閾値(分))
     */
    private static final Double DEFAULT_LOSSTIME_THRESHOLD = 20.0;

    /**
     * 初期値 (遅延理由ランキング：非表示にするかどうか)
     */
    private static final Boolean DEFAULT_DELAYREASON_INVISIBLE = false;

    /**
     * 初期値 (遅延理由ランキング：ランキング上限数)
     */
    private static final Integer DEFAULT_MAX_DELAYREASON_NUN = 3;

    /**
     * 初期値 (遅延理由ランキング：遅延時間の閾値(分))
     */
    private static final Double DEFAULT_DELAYTIME_THRESHOLD = 20.0;

    /**
     * 初期値 (呼出理由ランキング：非表示にするかどうか)
     */
    private static final Boolean DEFAULT_CALLREASON_INVISIBLE = false;

    /**
     * 初期値 (呼出理由ランキング：ランキング上限数)
     */
    private static final Integer DEFAULT_MAX_CALLREASON_NUN = 3;


    //===========================================================================
    /**
     * モデル名プロパティ
     */
    private StringProperty modelNameProperty = null;

    /**
     * モデル表示単位プロパティ
     */
    private StringProperty displayUnitProperty = null;

    /**
     * 時間単位
     */
    private StringProperty displayTimeUnitProperty = null;

    //------------------------生産数---------------------------------
    /**
     * 生産数：非表示にするかどうかプロパティ
     */
    private BooleanProperty productionVolumeInvisibleProperty = null;

    /**
     * 生産数：日次計画数プロパティ
     */
    private IntegerProperty planningQuantityProperty = null;

    /**
     * 生産数：達成率差異の閾値(%)プロパティ
     */
    private DoubleProperty achievementRateThresholdProperty = null;

    //------------------------ 稼働率 ----------------------
    /**
     * 稼働率：非表示にするかどうかプロパティ
     */
    private BooleanProperty operatingRateInvisibleProperty = null;

    /**
     * 稼働率：稼働可能時間プロパティ
     */
    private DoubleProperty workingPossibleTimeProperty = null;

    /**
     * 稼働率：目標稼働率(%)プロパティ
     */
    private DoubleProperty targetOperatingRateProperty = null;

    /**
     * 稼働率：稼働率差異の閾値(%)プロパティ
     */
    private DoubleProperty operatingRateThresholdProperty = null;

    //-------------------- 稼働率(作業者)------------------
    /**
     * 稼働率：作業者一人当たりの稼働可能時間プロパティ
     */
    private DoubleProperty workingPossibleTimePerPersonProperty = null;
    /**
     * 稼働率：作業員一人当たりの目標稼働率(%)プロパティ
     */
    private DoubleProperty targetOperatingRatePerPersonProperty = null;

    /**
     * 稼働率：作業員一人当たりの稼働率差異の閾値(%)プロパティ
     */
    private DoubleProperty operatingRateThresholdPerPersonProperty = null;

    /**
     * タクトタイム：非表示にするかどうかプロパティ
     */
    private BooleanProperty taktTimeInvisibleProperty = null;

    /**
     * タクトタイム：標準作業時間(分)プロパティ
     */
    private DoubleProperty averageWorkTimeProperty = null;

    /**
     * タクトタイム：達成度差異の閾値(%)プロパティ
     */
    private DoubleProperty tactTimeAchievementRateThresholdProperty = null;

    /**
     * タクトタイム：非表示にするかどうかプロパティ
     */
    private BooleanProperty taktTimePerWorkInvisibleProperty = null;

    /**
     * タクトタイム：標準作業時間(分)プロパティ
     */
    private DoubleProperty averageWorkTimePerWorkProperty = null;

    /**
     * タクトタイム：達成度差異の閾値(%)プロパティ
     */
    private DoubleProperty taktTimeAchievementRateThresholdPerWorkProperty = null;

    /**
     * 中断理由ランキング：非表示にするかどうかプロパティ
     */
    private BooleanProperty interruptReasonInvisibleProperty = null;

    /**
     * 中断理由ランキング：ランキング上限数プロパティ
     */
    private IntegerProperty maxInterruptReasonNumProperty = null;

    /**
     * 中断理由ランキング：中断ロス時間の閾値(分)プロパティ
     */
    private DoubleProperty lossTimeThresholdProperty = null;

    /**
     * 遅延理由ランキング：非表示にするかどうかプロパティ
     */
    private BooleanProperty delayReasonInvisibleProperty = null;

    /**
     * 遅延理由ランキング：ランキング上限数プロパティ
     */
    private IntegerProperty maxDelayReasonNumProperty = null;

    /**
     * 遅延理由ランキング：遅延時間の閾値(分)プロパティ
     */
    private DoubleProperty delayTimeThresholdProperty = null;

    /**
     * 呼出理由ランキング：非表示にするかどうかプロパティ
     */
    private BooleanProperty callReasonInvisibleProperty = null;

    /**
     * 呼出理由ランキング：ランキング上限数プロパティ
     */
    private IntegerProperty maxCallReasonNumProperty = null;

    /**
     * PowerBIのURLプロパティ
     */
    private StringProperty powerBIURLProperty;
    /**
     * モデル名
     */
    @XmlElement()
    @JsonProperty("Model")
    private String modelName;

    /**
     * モデル表示単位
     */
    @XmlElement()
    @JsonProperty("DisplayUnit")
    private String displayUnit;

    /**
     * モデル表示単位
     */
    @XmlElement()
    @JsonProperty("DisplayTimeUnit")
    private String displayTimeUnit;

    /**
     * 作業者一覧
     */
    @XmlElementWrapper(name = "workers")
    @XmlElement(name = "worker")
    @JsonProperty("Workers")
    private List<String> workers;

    /**
     * 生産数：非表示にするかどうか
     */
    @XmlElement()
    @JsonProperty("ProductionVolumeInvisible")
    private Boolean productionVolumeInvisible;

    /**
     * 生産数：日次計画数
     */
    @XmlElement()
    @JsonProperty("PlanningQuantity")
    private Integer planningQuantity;

    /**
     * 生産数：達成率差異の閾値(%)
     */
    @XmlElement()
    @JsonProperty("AchievementRateThreshold")
    private Double achievementRateThreshold;

    /**
     * 稼働率：非表示にするかどうか
     */
    @XmlElement()
    @JsonProperty("OperatingRateInvisible")
    private Boolean operatingRateInvisible;


    /**
     * 加工可能時間
     */
    @XmlElement()
    @JsonProperty("WorkingPossibleTime")
    private Double workingPossibleTime;


    /**
     * 稼働率：目標稼働率(%)
     */
    @XmlElement()
    @JsonProperty("TargetOperatingRate")
    private Double targetOperatingRate;

    /**
     * 稼働率：稼働率差異の閾値(%)
     */
    @XmlElement()
    @JsonProperty("OperatingRateThreshold")
    private Double operatingRateThreshold;

    /**
     * 稼働率 : 作業者一人当たりの加工可能時間
     */
    @XmlElement()
    @JsonProperty("WorkingPossibleTimePerPerson")
    private Double workingPossibleTimePerPerson;

    /**
     * 稼働率：作業員一人当たりの目標稼働率(%)
     */
    @XmlElement()
    @JsonProperty("TargetOperatingRatePerPerson")
    private Double targetOperatingRatePerPerson;

    /**
     * 稼働率：作業員一人当たりの稼働率差異の閾値(%)
     */
    @XmlElement()
    @JsonProperty("OperatingRateThresholdPerPerson")
    private Double operatingRateThresholdPerPerson;

    /**
     * 作業時間：非表示にするかどうか
     */
    @XmlElement()
    @JsonProperty("TactTimeInvisible")
    private Boolean tactTimeInvisible;

    /**
     * 作業時間：標準作業時間(分)
     */
    @XmlElement()
    @JsonProperty("AverageWorkTime")
    private Double averageWorkTime;

    /**
     * 作業時間：達成度差異の閾値(%)
     */
    @XmlElement()
    @JsonProperty("TactTimeAchievementRateThreshold")
    private Double tactTimeAchievementRateThreshold;


    /**
     * 作業時間：非表示にするかどうか
     */
    @XmlElement()
    @JsonProperty("TactTimePerWorkInvisible")
    private Boolean tactTimePerWorkInvisible;

    /**
     * 作業時間：標準作業時間(分)
     */
    @XmlElement()
    @JsonProperty("AverageWorkTimePerWork")
    private Double averageWorkTimePerWork;

    /**
     * 作業時間：達成度差異の閾値(%)
     */
    @XmlElement()
    @JsonProperty("TactTimeAchievementRateThresholdPerWork")
    private Double tactTimeAchievementRateThresholdPerWork;

    /**
     * 中断理由ランキング：非表示にするかどうか
     */
    @XmlElement()
    @JsonProperty("InterruptReasonInvisible")
    private Boolean interruptReasonInvisible;

    /**
     * 中断理由ランキング：ランキング上限数
     */
    @XmlElement()
    @JsonProperty("MaxInterruptReasonNum")
    private Integer maxInterruptReasonNum;

    /**
     * 中断理由ランキング：中断ロス時間の閾値(分)
     */
    @XmlElement()
    @JsonProperty("LossTimeThreshold")
    private Double lossTimeThreshold;

    /**
     * 遅延理由ランキング：非表示にするかどうか
     */
    @XmlElement()
    @JsonProperty("DelayReasonInvisible")
    private Boolean delayReasonInvisible;

    /**
     * 遅延理由ランキング：ランキング上限数
     */
    @XmlElement()
    @JsonProperty("MaxDelayReasonNum")
    private Integer maxDelayReasonNum;

    /**
     * 遅延理由ランキング：遅延時間の閾値(分)
     */
    @XmlElement()
    @JsonProperty("DelayTimeThreshold")
    private Double delayTimeThreshold;

    /**
     * 呼出理由ランキング：非表示にするかどうか
     */
    @XmlElement()
    @JsonProperty("CallReasonInvisible")
    private Boolean callReasonInvisible;

    /**
     * 呼出理由ランキング：ランキング上限数
     */
    @XmlElement()
    @JsonProperty("MaxCallReasonNum")
    private Integer maxCallReasonNum;

    /**
     * PowerBIのURL
     */
    @XmlElement()
    @JsonProperty("PowerBIURL")
    private String powerBIURL;

    /**
     * PowerBIのURLプロパティを取得する。
     *
     * @return PowerBIのURLプロパティ
     */
    public StringProperty powerBIURLProperty() {
        if (Objects.isNull(powerBIURLProperty)) {
            powerBIURLProperty = new SimpleStringProperty(powerBIURL);
        }
        return powerBIURLProperty;
    }

    /**
     * PowerBIのURLを取得する。
     *
     * @return PowerBIのURL
     */
    public String getPowerBIURL() {
        if (Objects.nonNull(powerBIURLProperty)) {
            return powerBIURLProperty.get();
        }
        return powerBIURL;
    }

    /**
     * PowerBIのURLを設定する。
     *
     * @param powerBIURL PowerBIのURL
     */
    public void setPowerBIURL(String powerBIURL) {
        if (Objects.nonNull(powerBIURLProperty)) {
            powerBIURLProperty.set(powerBIURL);
        } else {
            this.powerBIURL = powerBIURL;
        }
    }



    /**
     * コンストラクタ
     */
    public ModelSetting() {
    }

    /**
     * 進捗モニタ設定を初期化して作成する。
     *
     * @return 進捗モニタ設定
     */
    public static ModelSetting create() {
        ModelSetting setting = new ModelSetting();
        setting.setModelName("");
        setting.setDisplayUnit(DEFAULT_DISPLAY_UNIT);
        setting.setWorkers(new ArrayList<>());
        setting.setProductionVolumeInvisible(DEFAULT_PRODUCTION_VOLUME_INVISIBLE);
        setting.setPlanningQuantity(0);
        setting.setAchievementRateThreshold(DEFAULT_ACHIEVEMENT_RATE_THRESHOLD);
        setting.setOperatingRateInvisible(DEFAULT_OPERATING_RATE_INVISIBLE);
        setting.setWorkingPossibleTime(0.0);
        setting.setTargetOperatingRate(DEFAULT_TARGET_OPERATING_RATE);
        setting.setOperatingRateThreshold(DEFAULT_OPERATING_RATE_THRESHOLD);
        setting.setWorkingPossibleTimePerPerson(0.0);
        setting.setTargetOperatingRatePerPerson(DEFAULT_TARGET_OPERATING_RATE_PER_PERSON);
        setting.setOperatingRateThresholdPerPerson(DEFAULT_OPERATING_RATE_THRESHOLD_PER_PERSON);
        setting.setTactTimeInvisible(DEFAULT_TAKTTIME_INVISIBLE);
        setting.setAverageWorkTime(0.0);
        setting.setTactTimeAchievementRateThreshold(DEFAULT_TAKTTIME_ACHIEVEMENTR_RATE_THRESHOLD);
        setting.setTactTimePerWorkInvisible(DEFAULT_TAKTTIME_INVISIBLE);
        setting.setAverageWorkTimePerWork(0.0);
        setting.setTactTimeAchievementRateThresholdPerWork(DEFAULT_TAKTTIME_ACHIEVEMENTR_RATE_THRESHOLD);
        setting.setInterruptReasonInvisible(DEFAULT_INTERRUPTREASON_INVISIBLE);
        setting.setMaxInterruptReasonNum(DEFAULT_MAX_INTERRUPTREASON_NUN);
        setting.setLossTimeThreshold(DEFAULT_LOSSTIME_THRESHOLD);
        setting.setDelayReasonInvisible(DEFAULT_DELAYREASON_INVISIBLE);
        setting.setMaxDelayReasonNum(DEFAULT_MAX_DELAYREASON_NUN);
        setting.setDelayTimeThreshold(DEFAULT_DELAYTIME_THRESHOLD);
        setting.setCallReasonInvisible(DEFAULT_CALLREASON_INVISIBLE);
        setting.setMaxCallReasonNum(DEFAULT_MAX_CALLREASON_NUN);
        setting.setPowerBIURL("");
        return setting;
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
     * モデル表示単位プロパティを取得する。
     *
     * @return モデル表示単位プロパティ
     */
    public StringProperty displayUnitProperty() {
        if (Objects.isNull(this.displayUnitProperty)) {
            this.displayUnitProperty = new SimpleStringProperty(this.displayUnit);
        }
        return this.displayUnitProperty;
    }

    /**
     * モデル表示単位を取得する。
     *
     * @return モデル表示単位
     */
    public String getDisplayUnit() {
        if (Objects.nonNull(displayUnitProperty)) {
            return this.displayUnitProperty.get();
        }
        return this.displayUnit;
    }

    /**
     * モデル表示単位を設定する。
     *
     * @param displayUnit モデル表示単位
     */
    public void setDisplayUnit(String displayUnit) {
        if (Objects.nonNull(displayUnitProperty)) {
            displayUnitProperty.set(displayUnit);
        } else {
            this.displayUnit = displayUnit;
        }
    }

    /**
     * モデル表示時間単位プロパティを取得する。
     *
     * @return モデル表示単位プロパティ
     */
    public StringProperty displayTimeUnitProperty() {
        if (Objects.isNull(this.displayTimeUnitProperty)) {
            this.displayTimeUnitProperty = new SimpleStringProperty(this.displayTimeUnit);
        }
        return this.displayTimeUnitProperty;
    }

    /**
     * モデル表示時間単位を取得する。
     *
     * @return モデル表示単位
     */
    public String getDisplayTimeUnit() {
        if (Objects.nonNull(displayTimeUnitProperty)) {
            return this.displayTimeUnitProperty.get();
        }
        return this.displayTimeUnit;
    }

    /**
     * モデル表示時間単位を設定する。
     *
     * @param displayTimeUnit モデル表示単位
     */
    public void setDisplayTimeUnit(String displayTimeUnit) {
        if (Objects.nonNull(displayTimeUnitProperty)) {
            displayTimeUnitProperty.set(displayTimeUnit);
        } else {
            this.displayTimeUnit = displayTimeUnit;
        }
    }




    /**
     * 作業者一覧を取得する。
     *
     * @return 作業者一覧
     */
    public List<String> getWorkers() {
        return workers;
    }

    /**
     * 作業者一覧を設定する。
     *
     * @param workers 作業者一覧
     */
    public void setWorkers(List<String> workers) {
        this.workers = workers;
    }

    /**
     * 生産数：非表示にするかどうかプロパティを取得する。
     *
     * @return 生産数：非表示にするかどうかプロパティ
     */
    public BooleanProperty productionVolumeInvisibleProperty() {
        if (Objects.isNull(this.productionVolumeInvisibleProperty)) {
            this.productionVolumeInvisibleProperty = new SimpleBooleanProperty(this.productionVolumeInvisible);
        }
        return this.productionVolumeInvisibleProperty;
    }

    /**
     * 生産数：非表示にするかどうかを取得する。
     *
     * @return 生産数：非表示にするかどうか
     */
    public Boolean getProductionVolumeInvisible() {
        if (Objects.nonNull(productionVolumeInvisibleProperty)) {
            return this.productionVolumeInvisibleProperty.get();
        }
        return this.productionVolumeInvisible;
    }

    /**
     * 生産数：非表示にするかどうかを設定する。
     *
     * @param productionVolumeInvisible 生産数：非表示にするかどうか
     */
    public void setProductionVolumeInvisible(Boolean productionVolumeInvisible) {
        if (Objects.nonNull(productionVolumeInvisibleProperty)) {
            productionVolumeInvisibleProperty.set(productionVolumeInvisible);
        } else {
            this.productionVolumeInvisible = productionVolumeInvisible;
        }
    }

    /**
     * 生産数：日次計画数プロパティを取得する。
     *
     * @return 生産数：日次計画数プロパティ
     */
    public IntegerProperty planningQuantityProperty() {
        if (Objects.isNull(this.planningQuantityProperty)) {
            this.planningQuantityProperty = new SimpleIntegerProperty(this.planningQuantity);
        }
        return this.planningQuantityProperty;
    }

    /**
     * 生産数：日次計画数を取得する。
     *
     * @return 生産数：日次計画数
     */
    public Integer getPlanningQuantity() {
        if (Objects.nonNull(planningQuantityProperty)) {
            return this.planningQuantityProperty.get();
        }
        return this.planningQuantity;
    }

    /**
     * 生産数：日次計画数を設定する。
     *
     * @param planningQuantity 生産数：日次計画数
     */
    public void setPlanningQuantity(Integer planningQuantity) {
        if (Objects.nonNull(planningQuantityProperty)) {
            planningQuantityProperty.set(planningQuantity);
        } else {
            this.planningQuantity = planningQuantity;
        }
    }

    /**
     * 生産数：達成率差異の閾値(%)プロパティを取得する。
     *
     * @return 生産数：達成率差異の閾値(%)プロパティ
     */
    public DoubleProperty achievementRateThresholdProperty() {
        if (Objects.isNull(this.achievementRateThresholdProperty)) {
            this.achievementRateThresholdProperty = new SimpleDoubleProperty(this.achievementRateThreshold);
        }
        return this.achievementRateThresholdProperty;
    }

    /**
     * 生産数：達成率差異の閾値(%)を取得する。
     *
     * @return 生産数：達成率差異の閾値(%)
     */
    public Double getAchievementRateThreshold() {
        if (Objects.nonNull(achievementRateThresholdProperty)) {
            return this.achievementRateThresholdProperty.get();
        }
        return this.achievementRateThreshold;
    }

    /**
     * 生産数：達成率差異の閾値(%)を設定する。
     *
     * @param achievementRateThreshold 生産数：達成率差異の閾値(%)
     */
    public void setAchievementRateThreshold(Double achievementRateThreshold) {
        if (Objects.nonNull(achievementRateThresholdProperty)) {
            achievementRateThresholdProperty.set(achievementRateThreshold);
        } else {
            this.achievementRateThreshold = achievementRateThreshold;
        }
    }

    /**
     * 稼働率：非表示にするかどうかプロパティを取得する。
     *
     * @return 稼働率：非表示にするかどうかプロパティ
     */
    public BooleanProperty operatingRateInvisibleProperty() {
        if (Objects.isNull(this.operatingRateInvisibleProperty)) {
            this.operatingRateInvisibleProperty = new SimpleBooleanProperty(this.operatingRateInvisible);
        }
        return this.operatingRateInvisibleProperty;
    }

    /**
     * 稼働率：非表示にするかどうかを取得する。
     *
     * @return 稼働率：非表示にするかどうか
     */
    public Boolean getOperatingRateInvisible() {
        if (Objects.nonNull(operatingRateInvisibleProperty)) {
            return this.operatingRateInvisibleProperty.get();
        }
        return this.operatingRateInvisible;
    }

    /**
     * 稼働率：非表示にするかどうかを設定する。
     *
     * @param operatingRateInvisible 稼働率：非表示にするかどうか
     */
    public void setOperatingRateInvisible(Boolean operatingRateInvisible) {
        if (Objects.nonNull(operatingRateInvisibleProperty)) {
            operatingRateInvisibleProperty.set(operatingRateInvisible);
        } else {
            this.operatingRateInvisible = operatingRateInvisible;
        }
    }




    /**
     * 稼働率：稼働可能時間プロパティを取得する。
     *
     * @return 稼働率：始業時刻プロパティ
     */
    public DoubleProperty workingPossibleTimeProperty() {
        if (Objects.isNull(this.workingPossibleTimeProperty)) {
            this.workingPossibleTimeProperty = new SimpleDoubleProperty(this.workingPossibleTime);
        }
        return this.workingPossibleTimeProperty;
    }

    /**
     * 稼働率：稼働可能時間を取得する
     *
     * @return 稼働率：稼働可能時間
     */
    public Double getWorkingPossibleTime() {
        if (Objects.nonNull(workingPossibleTimeProperty)) {
            return this.workingPossibleTimeProperty.get();
        }
        return this.workingPossibleTime;
    }
    /**
     * 稼働率：稼働可能時間を設定する。
     *
     * @param WorkingPossibleTime 稼働率：稼働可能時間
     */
    public void setWorkingPossibleTime(Double WorkingPossibleTime) {
        if (Objects.nonNull(workingPossibleTimeProperty)) {
            workingPossibleTimeProperty.set(WorkingPossibleTime);
        } else {
            this.workingPossibleTime = WorkingPossibleTime;
        }
    }



    /**
     * 稼働率：目標稼働率(%)プロパティを取得する。
     *
     * @return 稼働率：目標稼働率(%)プロパティ
     */
    public DoubleProperty targetOperatingRateProperty() {
        if (Objects.isNull(this.targetOperatingRateProperty)) {
            this.targetOperatingRateProperty = new SimpleDoubleProperty(this.targetOperatingRate);
        }
        return this.targetOperatingRateProperty;
    }

    /**
     * 稼働率：目標稼働率(%)を取得する。
     *
     * @return 稼働率：目標稼働率(%)
     */
    public Double getTargetOperatingRate() {
        if (Objects.nonNull(targetOperatingRateProperty)) {
            return this.targetOperatingRateProperty.get();
        }
        return this.targetOperatingRate;
    }

    /**
     * 稼働率：目標稼働率(%)を設定する。
     *
     * @param targetOperatingRate 稼働率：目標稼働率(%)
     */
    public void setTargetOperatingRate(Double targetOperatingRate) {
        if (Objects.nonNull(targetOperatingRateProperty)) {
            targetOperatingRateProperty.set(targetOperatingRate);
        } else {
            this.targetOperatingRate = targetOperatingRate;
        }
    }

    /**
     * 稼働率：稼働率差異の閾値(%)プロパティを取得する。
     *
     * @return 稼働率：稼働率差異の閾値(%)プロパティ
     */
    public DoubleProperty operatingRateThresholdProperty() {
        if (Objects.isNull(this.operatingRateThresholdProperty)) {
            this.operatingRateThresholdProperty = new SimpleDoubleProperty(this.operatingRateThreshold);
        }
        return this.operatingRateThresholdProperty;
    }

    /**
     * 稼働率：稼働率差異の閾値(%)を取得する。
     *
     * @return 稼働率：稼働率差異の閾値(%)
     */
    public Double getOperatingRateThreshold() {
        if (Objects.nonNull(operatingRateThresholdProperty)) {
            return this.operatingRateThresholdProperty.get();
        }
        return this.operatingRateThreshold;
    }

    /**
     * 稼働率：稼働率差異の閾値(%)を設定する。
     *
     * @param operatingRateThreshold 稼働率：稼働率差異の閾値(%)
     */
    public void setOperatingRateThreshold(Double operatingRateThreshold) {
        if (Objects.nonNull(operatingRateThresholdProperty)) {
            operatingRateThresholdProperty.set(operatingRateThreshold);
        } else {
            this.operatingRateThreshold = operatingRateThreshold;
        }
    }



    /**
     * 稼働率：作業者一人当たりの稼働可能時間プロパティを取得する。
     *
     * @return 稼働率：作業者一人当たりの稼働可能時間プロパティ
     */
    public DoubleProperty workingPossibleTimePerPersonProperty() {
        if (Objects.isNull(this.workingPossibleTimePerPersonProperty)) {
            this.workingPossibleTimePerPersonProperty = new SimpleDoubleProperty(this.workingPossibleTimePerPerson);
        }
        return this.workingPossibleTimePerPersonProperty;
    }

    /**
     * 稼働率：作業者一人当たりの稼働可能時間を取得する
     *
     * @return 稼働率：作業者一人当たりの稼働可能時間
     */
    public Double getWorkingPossibleTimePerPerson() {
        if (Objects.nonNull(workingPossibleTimePerPersonProperty)) {
            return this.workingPossibleTimePerPersonProperty.get();
        }
        return this.workingPossibleTimePerPerson;
    }
    /**
     * 稼働率：作業者一人当たりの稼働可能時間を設定する。
     *
     * @param WorkingPossibleTimePerPerson 稼働率：作業者一人当たりの稼働可能時間
     */
    public void setWorkingPossibleTimePerPerson(Double WorkingPossibleTimePerPerson) {
        if (Objects.nonNull(workingPossibleTimePerPersonProperty)) {
            workingPossibleTimePerPersonProperty.set(WorkingPossibleTimePerPerson);
        } else {
            this.workingPossibleTimePerPerson = WorkingPossibleTimePerPerson;
        }
    }




    /**
     * 稼働率：作業員一人当たりの目標稼働率(%)プロパティを取得する。
     *
     * @return 稼働率：作業員一人当たりの目標稼働率(%)プロパティ
     */
    public DoubleProperty targetOperatingRatePerPersonProperty() {
        if (Objects.isNull(this.targetOperatingRatePerPersonProperty)) {
            this.targetOperatingRatePerPersonProperty = new SimpleDoubleProperty(this.targetOperatingRatePerPerson);
        }
        return this.targetOperatingRatePerPersonProperty;
    }

    /**
     * 稼働率：作業員一人当たりの目標稼働率(%)を取得する。
     *
     * @return 稼働率：作業員一人当たりの目標稼働率(%)
     */
    public Double getTargetOperatingRatePerPerson() {
        if (Objects.nonNull(targetOperatingRatePerPersonProperty)) {
            return this.targetOperatingRatePerPersonProperty.get();
        }
        return this.targetOperatingRatePerPerson;
    }

    /**
     * 稼働率：作業員一人当たりの目標稼働率(%)を設定する。
     *
     * @param targetOperatingRatePerPerson 稼働率：作業員一人当たりの目標稼働率(%)
     */
    public void setTargetOperatingRatePerPerson(Double targetOperatingRatePerPerson) {
        if (Objects.nonNull(targetOperatingRatePerPersonProperty)) {
            targetOperatingRatePerPersonProperty.set(targetOperatingRatePerPerson);
        } else {
            this.targetOperatingRatePerPerson = targetOperatingRatePerPerson;
        }
    }

    /**
     * 稼働率：作業員一人当たりの稼働率差異の閾値(%)プロパティを取得する。
     *
     * @return 稼働率：作業員一人当たりの稼働率差異の閾値(%)プロパティ
     */
    public DoubleProperty operatingRateThresholdPerPersonProperty() {
        if (Objects.isNull(this.operatingRateThresholdPerPersonProperty)) {
            this.operatingRateThresholdPerPersonProperty = new SimpleDoubleProperty(this.operatingRateThresholdPerPerson);
        }
        return this.operatingRateThresholdPerPersonProperty;
    }

    /**
     * 稼働率：作業員一人当たりの稼働率差異の閾値(%)を取得する。
     *
     * @return 稼働率：作業員一人当たりの稼働率差異の閾値(%)
     */
    public Double getOperatingRateThresholdPerPerson() {
        if (Objects.nonNull(operatingRateThresholdPerPersonProperty)) {
            return this.operatingRateThresholdPerPersonProperty.get();
        }
        return this.operatingRateThresholdPerPerson;
    }

    /**
     * 稼働率：作業員一人当たりの稼働率差異の閾値(%)を設定する。
     *
     * @param operatingRateThresholdPerPerson 稼働率：作業員一人当たりの稼働率差異の閾値(%)
     */
    public void setOperatingRateThresholdPerPerson(Double operatingRateThresholdPerPerson) {
        if (Objects.nonNull(operatingRateThresholdPerPersonProperty)) {
            operatingRateThresholdPerPersonProperty.set(operatingRateThresholdPerPerson);
        } else {
            this.operatingRateThresholdPerPerson = operatingRateThresholdPerPerson;
        }
    }

    /**
     * タクトタイム：非表示にするかどうかプロパティを取得する。
     *
     * @return タクトタイム：非表示にするかどうかプロパティ
     */
    public BooleanProperty tactTimeInvisibleProperty() {
        if (Objects.isNull(this.taktTimeInvisibleProperty)) {
            this.taktTimeInvisibleProperty = new SimpleBooleanProperty(this.tactTimeInvisible);
        }
        return this.taktTimeInvisibleProperty;
    }

    /**
     * タクトタイム：非表示にするかどうかを取得する。
     *
     * @return タクトタイム：非表示にするかどうか
     */
    public Boolean getTactTimeInvisible() {
        if (Objects.nonNull(taktTimeInvisibleProperty)) {
            return this.taktTimeInvisibleProperty.get();
        }
        return this.tactTimeInvisible;
    }

    /**
     * タクトタイム：非表示にするかどうかを設定する。
     *
     * @param tactTimeInvisible タクトタイム：非表示にするかどうか
     */
    public void setTactTimeInvisible(Boolean tactTimeInvisible) {
        if (Objects.nonNull(taktTimeInvisibleProperty)) {
            taktTimeInvisibleProperty.set(tactTimeInvisible);
        } else {
            this.tactTimeInvisible = tactTimeInvisible;
        }
    }

    /**
     * タクトタイム：標準作業時間(分)プロパティを取得する。
     *
     * @return タクトタイム：標準作業時間(分)プロパティ
     */
    public DoubleProperty averageWorkTimeProperty() {
        if (Objects.isNull(this.averageWorkTimeProperty)) {
            this.averageWorkTimeProperty = new SimpleDoubleProperty(this.averageWorkTime);
        }
        return this.averageWorkTimeProperty;
    }

    /**
     * タクトタイム：標準作業時間(分)を取得する。
     *
     * @return タクトタイム：標準作業時間(分)
     */
    public Double getAverageWorkTime() {
        if (Objects.nonNull(averageWorkTimeProperty)) {
            return this.averageWorkTimeProperty.get();
        }
        return this.averageWorkTime;
    }

    /**
     * タクトタイム：標準作業時間(分)を設定する。
     *
     * @param averageWorkTime タクトタイム：標準作業時間(分)
     */
    public void setAverageWorkTime(Double averageWorkTime) {
        if (Objects.nonNull(averageWorkTimeProperty)) {
            averageWorkTimeProperty.set(averageWorkTime);
        } else {
            this.averageWorkTime = averageWorkTime;
        }
    }

    /**
     * タクトタイム：達成度差異の閾値(%)プロパティを取得する。
     *
     * @return タクトタイム：達成度差異の閾値(%)プロパティ
     */
    public DoubleProperty tactTimeAchievementRateThresholdProperty() {
        if (Objects.isNull(this.tactTimeAchievementRateThresholdProperty)) {
            this.tactTimeAchievementRateThresholdProperty = new SimpleDoubleProperty(this.tactTimeAchievementRateThreshold);
        }
        return this.tactTimeAchievementRateThresholdProperty;
    }

    /**
     * タクトタイム：達成度差異の閾値(%)を取得する。
     *
     * @return タクトタイム：達成度差異の閾値(%)
     */
    public Double getTactTimeAchievementRateThreshold() {
        if (Objects.nonNull(tactTimeAchievementRateThresholdProperty)) {
            return this.tactTimeAchievementRateThresholdProperty.get();
        }
        return this.tactTimeAchievementRateThreshold;
    }

    /**
     * タクトタイム：達成度差異の閾値(%)を設定する。
     *
     * @param tactTimeAchievementRateThreshold タクトタイム：達成度差異の閾値(%)
     */
    public void setTactTimeAchievementRateThreshold(Double tactTimeAchievementRateThreshold) {
        if (Objects.nonNull(tactTimeAchievementRateThresholdProperty)) {
            tactTimeAchievementRateThresholdProperty.set(tactTimeAchievementRateThreshold);
        } else {
            this.tactTimeAchievementRateThreshold = tactTimeAchievementRateThreshold;
        }
    }


    /**
     * タクトタイム：非表示にするかどうかプロパティを取得する。
     *
     * @return タクトタイム：非表示にするかどうかプロパティ
     */
    public BooleanProperty tactTimePerWorkInvisibleProperty() {
        if (Objects.isNull(this.taktTimePerWorkInvisibleProperty)) {
            this.taktTimePerWorkInvisibleProperty = new SimpleBooleanProperty(this.tactTimePerWorkInvisible);
        }
        return this.taktTimePerWorkInvisibleProperty;
    }

    /**
     * タクトタイム：非表示にするかどうかを取得する。
     *
     * @return タクトタイム：非表示にするかどうか
     */
    public Boolean getTactTimePerWorkInvisible() {
        if (Objects.nonNull(taktTimePerWorkInvisibleProperty)) {
            return this.taktTimePerWorkInvisibleProperty.get();
        }
        return this.tactTimePerWorkInvisible;
    }

    /**
     * タクトタイム：非表示にするかどうかを設定する。
     *
     * @param tactTimePerWorkInvisible タクトタイム：非表示にするかどうか
     */
    public void setTactTimePerWorkInvisible(Boolean tactTimePerWorkInvisible) {
        if (Objects.nonNull(taktTimePerWorkInvisibleProperty)) {
            taktTimePerWorkInvisibleProperty.set(tactTimePerWorkInvisible);
        } else {
            this.tactTimePerWorkInvisible = tactTimePerWorkInvisible;
        }
    }

    /**
     * タクトタイム：標準作業時間(分)プロパティを取得する。
     *
     * @return タクトタイム：標準作業時間(分)プロパティ
     */
    public DoubleProperty averageWorkPerWorkTimeProperty() {
        if (Objects.isNull(this.averageWorkTimePerWorkProperty)) {
            this.averageWorkTimePerWorkProperty = new SimpleDoubleProperty(this.averageWorkTimePerWork);
        }
        return this.averageWorkTimePerWorkProperty;
    }

    /**
     * タクトタイム：標準作業時間(分)を取得する。
     *
     * @return タクトタイム：標準作業時間(分)
     */
    public Double getAverageWorkTimePerWork() {
        if (Objects.nonNull(averageWorkTimePerWorkProperty)) {
            return this.averageWorkTimePerWorkProperty.get();
        }
        return this.averageWorkTimePerWork;
    }

    /**
     * タクトタイム：標準作業時間(分)を設定する。
     *
     * @param averageWorkTimePerWork タクトタイム：標準作業時間(分)
     */
    public void setAverageWorkTimePerWork(Double averageWorkTimePerWork) {
        if (Objects.nonNull(averageWorkTimePerWorkProperty)) {
            averageWorkTimePerWorkProperty.set(averageWorkTimePerWork);
        } else {
            this.averageWorkTimePerWork = averageWorkTimePerWork;
        }
    }

    /**
     * タクトタイム：達成度差異の閾値(%)プロパティを取得する。
     *
     * @return タクトタイム：達成度差異の閾値(%)プロパティ
     */
    public DoubleProperty tactTimeAchievementRateThresholdPerWorkProperty() {
        if (Objects.isNull(this.taktTimeAchievementRateThresholdPerWorkProperty)) {
            this.taktTimeAchievementRateThresholdPerWorkProperty = new SimpleDoubleProperty(this.tactTimeAchievementRateThresholdPerWork);
        }
        return this.taktTimeAchievementRateThresholdPerWorkProperty;
    }

    /**
     * タクトタイム：達成度差異の閾値(%)を取得する。
     *
     * @return タクトタイム：達成度差異の閾値(%)
     */
    public Double getTactTimeAchievementRateThresholdPerWork() {
        if (Objects.nonNull(taktTimeAchievementRateThresholdPerWorkProperty)) {
            return this.taktTimeAchievementRateThresholdPerWorkProperty.get();
        }
        return this.tactTimeAchievementRateThresholdPerWork;
    }

    /**
     * タクトタイム：達成度差異の閾値(%)を設定する。
     *
     * @param tactTimeAchievementRateThresholdPerWork タクトタイム：達成度差異の閾値(%)
     */
    public void setTactTimeAchievementRateThresholdPerWork(Double tactTimeAchievementRateThresholdPerWork) {
        if (Objects.nonNull(taktTimeAchievementRateThresholdPerWorkProperty)) {
            taktTimeAchievementRateThresholdPerWorkProperty.set(tactTimeAchievementRateThresholdPerWork);
        } else {
            this.tactTimeAchievementRateThresholdPerWork = tactTimeAchievementRateThresholdPerWork;
        }
    }


    /**
     * 中断理由ランキング：非表示にするかどうかプロパティを取得する。
     *
     * @return 中断理由ランキング：非表示にするかどうかプロパティ
     */
    public BooleanProperty interruptReasonInvisibleProperty() {
        if (Objects.isNull(this.interruptReasonInvisibleProperty)) {
            this.interruptReasonInvisibleProperty = new SimpleBooleanProperty(this.interruptReasonInvisible);
        }
        return this.interruptReasonInvisibleProperty;
    }

    /**
     * 中断理由ランキング：非表示にするかどうかを取得する。
     *
     * @return 中断理由ランキング：非表示にするかどうか
     */
    public Boolean getInterruptReasonInvisible() {
        if (Objects.nonNull(interruptReasonInvisibleProperty)) {
            return this.interruptReasonInvisibleProperty.get();
        }
        return this.interruptReasonInvisible;
    }

    /**
     * 中断理由ランキング：非表示にするかどうかを設定する。
     *
     * @param interruptReasonInvisible 中断理由ランキング：非表示にするかどうか
     */
    public void setInterruptReasonInvisible(Boolean interruptReasonInvisible) {
        if (Objects.nonNull(interruptReasonInvisibleProperty)) {
            interruptReasonInvisibleProperty.set(interruptReasonInvisible);
        } else {
            this.interruptReasonInvisible = interruptReasonInvisible;
        }
    }

    /**
     * 中断理由ランキング：ランキング上限数プロパティを取得する。
     *
     * @return 中断理由ランキング：ランキング上限数プロパティ
     */
    public IntegerProperty maxInterruptReasonNumProperty() {
        if (Objects.isNull(this.maxInterruptReasonNumProperty)) {
            this.maxInterruptReasonNumProperty = new SimpleIntegerProperty(this.maxInterruptReasonNum);
        }
        return this.maxInterruptReasonNumProperty;
    }

    /**
     * 中断理由ランキング：ランキング上限数を取得する。
     *
     * @return 中断理由ランキング：ランキング上限数
     */
    public Integer getMaxInterruptReasonNum() {
        if (Objects.nonNull(maxInterruptReasonNumProperty)) {
            return this.maxInterruptReasonNumProperty.get();
        }
        return this.maxInterruptReasonNum;
    }

    /**
     * 中断理由ランキング：ランキング上限数を設定する。
     *
     * @param maxInterruptReasonNum 中断理由ランキング：ランキング上限数
     */
    public void setMaxInterruptReasonNum(Integer maxInterruptReasonNum) {
        if (Objects.nonNull(maxInterruptReasonNumProperty)) {
            maxInterruptReasonNumProperty.set(maxInterruptReasonNum);
        } else {
            this.maxInterruptReasonNum = maxInterruptReasonNum;
        }
    }

    /**
     * 中断理由ランキング：中断ロス時間の閾値(分)プロパティを取得する。
     *
     * @return 中断理由ランキング：中断ロス時間の閾値(分)プロパティ
     */
    public DoubleProperty lossTimeThresholdProperty() {
        if (Objects.isNull(this.lossTimeThresholdProperty)) {
            this.lossTimeThresholdProperty = new SimpleDoubleProperty(this.lossTimeThreshold);
        }
        return this.lossTimeThresholdProperty;
    }

    /**
     * 中断理由ランキング：中断ロス時間の閾値(分)を取得する。
     *
     * @return 中断理由ランキング：中断ロス時間の閾値(分)
     */
    public Double getLossTimeThreshold() {
        if (Objects.nonNull(lossTimeThresholdProperty)) {
            return this.lossTimeThresholdProperty.get();
        }
        return this.lossTimeThreshold;
    }

    /**
     * 中断理由ランキング：中断ロス時間の閾値(分)を設定する。
     *
     * @param LossTimeThreshold 中断理由ランキング：中断ロス時間の閾値(分)
     */
    public void setLossTimeThreshold(Double LossTimeThreshold) {
        if (Objects.nonNull(lossTimeThresholdProperty)) {
            lossTimeThresholdProperty.set(LossTimeThreshold);
        } else {
            this.lossTimeThreshold = LossTimeThreshold;
        }
    }

    /**
     * 遅延理由ランキング：非表示にするかどうかプロパティを取得する。
     *
     * @return 遅延理由ランキング：非表示にするかどうかプロパティ
     */
    public BooleanProperty delayReasonInvisibleProperty() {
        if (Objects.isNull(this.delayReasonInvisibleProperty)) {
            this.delayReasonInvisibleProperty = new SimpleBooleanProperty(this.delayReasonInvisible);
        }
        return this.delayReasonInvisibleProperty;
    }

    /**
     * 遅延理由ランキング：非表示にするかどうかを取得する。
     *
     * @return 遅延理由ランキング：非表示にするかどうか
     */
    public Boolean getDelayReasonInvisible() {
        if (Objects.nonNull(delayReasonInvisibleProperty)) {
            return this.delayReasonInvisibleProperty.get();
        }
        return this.delayReasonInvisible;
    }

    /**
     * 遅延理由ランキング：非表示にするかどうかを設定する。
     *
     * @param delayReasonInvisible 遅延理由ランキング：非表示にするかどうか
     */
    public void setDelayReasonInvisible(Boolean delayReasonInvisible) {
        if (Objects.nonNull(delayReasonInvisibleProperty)) {
            delayReasonInvisibleProperty.set(delayReasonInvisible);
        } else {
            this.delayReasonInvisible = delayReasonInvisible;
        }
    }

    /**
     * 遅延理由ランキング：ランキング上限数プロパティを取得する。
     *
     * @return 遅延理由ランキング：ランキング上限数プロパティ
     */
    public IntegerProperty maxDelayReasonNumProperty() {
        if (Objects.isNull(this.maxDelayReasonNumProperty)) {
            this.maxDelayReasonNumProperty = new SimpleIntegerProperty(this.maxDelayReasonNum);
        }
        return this.maxDelayReasonNumProperty;
    }

    /**
     * 遅延理由ランキング：ランキング上限数を取得する。
     *
     * @return 遅延理由ランキング：ランキング上限数
     */
    public Integer getMaxDelayReasonNum() {
        if (Objects.nonNull(maxDelayReasonNumProperty)) {
            return this.maxDelayReasonNumProperty.get();
        }
        return this.maxDelayReasonNum;
    }

    /**
     * 遅延理由ランキング：ランキング上限数を設定する。
     *
     * @param maxDelayReasonNum 遅延理由ランキング：ランキング上限数
     */
    public void setMaxDelayReasonNum(Integer maxDelayReasonNum) {
        if (Objects.nonNull(maxDelayReasonNumProperty)) {
            maxDelayReasonNumProperty.set(maxDelayReasonNum);
        } else {
            this.maxDelayReasonNum = maxDelayReasonNum;
        }
    }

    /**
     * 遅延理由ランキング：遅延時間の閾値(分)プロパティを取得する。
     *
     * @return 遅延理由ランキング：遅延時間の閾値(分)プロパティ
     */
    public DoubleProperty delayTimeThresholdProperty() {
        if (Objects.isNull(this.delayTimeThresholdProperty)) {
            this.delayTimeThresholdProperty = new SimpleDoubleProperty(this.delayTimeThreshold);
        }
        return this.delayTimeThresholdProperty;
    }

    /**
     * 遅延理由ランキング：遅延時間の閾値(分)を取得する。
     *
     * @return 遅延理由ランキング：遅延時間の閾値(分)
     */
    public Double getDelayTimeThreshold() {
        if (Objects.nonNull(delayTimeThresholdProperty)) {
            return this.delayTimeThresholdProperty.get();
        }
        return this.delayTimeThreshold;
    }

    /**
     * 遅延理由ランキング：遅延時間の閾値(分)を設定する。
     *
     * @param delayTimeThreshold 遅延理由ランキング：遅延時間の閾値(分)
     */
    public void setDelayTimeThreshold(Double delayTimeThreshold) {
        if (Objects.nonNull(delayTimeThresholdProperty)) {
            delayTimeThresholdProperty.set(delayTimeThreshold);
        } else {
            this.delayTimeThreshold = delayTimeThreshold;
        }
    }

    /**
     * 呼出理由ランキング：非表示にするかどうかプロパティを取得する。
     *
     * @return 呼出理由ランキング：非表示にするかどうかプロパティ
     */
    public BooleanProperty callReasonInvisibleProperty() {
        if (Objects.isNull(this.callReasonInvisibleProperty)) {
            this.callReasonInvisibleProperty = new SimpleBooleanProperty(this.callReasonInvisible);
        }
        return this.callReasonInvisibleProperty;
    }

    /**
     * 呼出理由ランキング：非表示にするかどうかを取得する。
     *
     * @return 呼出理由ランキング：非表示にするかどうか
     */
    public Boolean getCallReasonInvisible() {
        if (Objects.nonNull(callReasonInvisibleProperty)) {
            return this.callReasonInvisibleProperty.get();
        }
        return this.callReasonInvisible;
    }

    /**
     * 呼出理由ランキング：非表示にするかどうかを設定する。
     *
     * @param callReasonInvisible 呼出理由ランキング：非表示にするかどうか
     */
    public void setCallReasonInvisible(Boolean callReasonInvisible) {
        if (Objects.nonNull(callReasonInvisibleProperty)) {
            callReasonInvisibleProperty.set(callReasonInvisible);
        } else {
            this.callReasonInvisible = callReasonInvisible;
        }
    }

    /**
     * 呼出理由ランキング：ランキング上限数プロパティを取得する。
     *
     * @return 呼出理由ランキング：ランキング上限数プロパティ
     */
    public IntegerProperty maxCallReasonNumProperty() {
        if (Objects.isNull(this.maxCallReasonNumProperty)) {
            this.maxCallReasonNumProperty = new SimpleIntegerProperty(this.maxCallReasonNum);
        }
        return this.maxCallReasonNumProperty;
    }

    /**
     * 呼出理由ランキング：ランキング上限数を取得する。
     *
     * @return 呼出理由ランキング：ランキング上限数
     */
    public Integer getMaxCallReasonNum() {
        if (Objects.nonNull(maxCallReasonNumProperty)) {
            return this.maxCallReasonNumProperty.get();
        }
        return this.maxCallReasonNum;
    }

    /**
     * 呼出理由ランキング：ランキング上限数を設定する。
     *
     * @param maxCallReasonNum 呼出理由ランキング：ランキング上限数
     */
    public void setMaxCallReasonNum(Integer maxCallReasonNum) {
        if (Objects.nonNull(maxCallReasonNumProperty)) {
            maxCallReasonNumProperty.set(maxCallReasonNum);
        } else {
            this.maxCallReasonNum = maxCallReasonNum;
        }
    }

    /**
     * ハッシュコードを取得する。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.modelName);
        return hash;
    }

    /**
     * オブジェクトが等しいかどうかを取得する。
     *
     * @param obj 比較対象のオブジェクト
     * @return オブジェクトが等しい場合はtrue、それ以外の場合はfalse
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModelSetting other = (ModelSetting) obj;
        return Objects.equals(this.getModelName(), other.getModelName());
    }

    /**
     * 文字列表現を取得する。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ModelSetting{")
                .append("modelName=").append(this.modelName)
                .append(", displayUnit=").append(this.displayUnit)
                .append(", displayTimeUnit=").append(this.displayTimeUnit)
                .append(", workers=").append(this.workers)
                .append(", productionVolumeInvisible=").append(this.productionVolumeInvisible)
                .append(", planningQuantity=").append(this.planningQuantity)
                .append(", achievementRateThreshold=").append(this.achievementRateThreshold)
                .append(", operatingRateInvisible=").append(this.operatingRateInvisible)
                .append(", workingPossibleTime=").append(this.workingPossibleTime)
                .append(", targetOperatingRate=").append(this.targetOperatingRate)
                .append(", operatingRateThreshold=").append(this.operatingRateThreshold)
                .append(", targetOperatingRatePerPerson=").append(this.targetOperatingRatePerPerson)
                .append(", operatingRateThresholdPerPerson=").append(this.operatingRateThresholdPerPerson)
                .append(", tactTimeInvisible=").append(this.tactTimeInvisible)
                .append(", averageWorkTime=").append(this.averageWorkTime)
                .append(", tactTimeAchievementRateThreshold=").append(this.tactTimeAchievementRateThreshold)
                .append(", tactTimePerWorkInvisible=").append(this.tactTimePerWorkInvisible)
                .append(", averageWorkTimePerWork=").append(this.averageWorkTimePerWork)
                .append(", tactTimeAchievementRateThresholdPerWork=").append(this.tactTimeAchievementRateThresholdPerWork)
                .append(", interruptReasonInvisible=").append(this.interruptReasonInvisible)
                .append(", maxInterruptReasonNumber=").append(this.maxInterruptReasonNum)
                .append(", lossTimeThreshold=").append(this.lossTimeThreshold)
                .append(", delayReasonInvisible=").append(this.delayReasonInvisible)
                .append(", maxDelayReasonNumber=").append(this.maxDelayReasonNum)
                .append(", delayTimeThreshold=").append(this.delayTimeThreshold)
                .append(", callReasonInvisible=").append(this.callReasonInvisible)
                .append(", maxCallReasonNumber=").append(this.maxCallReasonNum)
                .append(", powerBIURL=").append(this.powerBIURL)
                .append("}")
                .toString();
    }

    /**
     * モデル設定のコピーを新規作成する。
     *
     * @return モデル設定
     */
    @Override
    public ModelSetting clone() {
        ModelSetting setting = new ModelSetting();

        setting.setModelName(this.getModelName());
        setting.setDisplayUnit(this.getDisplayUnit());
        setting.setWorkers(new ArrayList(this.getWorkers()));
        setting.setProductionVolumeInvisible(this.getProductionVolumeInvisible());
        setting.setPlanningQuantity(this.getPlanningQuantity());
        setting.setAchievementRateThreshold(this.getAchievementRateThreshold());
        setting.setOperatingRateInvisible(this.getOperatingRateInvisible());
        setting.setWorkingPossibleTime(this.getWorkingPossibleTime());
        setting.setTargetOperatingRate(this.getTargetOperatingRate());
        setting.setOperatingRateThreshold(this.getOperatingRateThreshold());
        setting.setWorkingPossibleTimePerPerson(this.getWorkingPossibleTimePerPerson());
        setting.setTargetOperatingRatePerPerson(this.getTargetOperatingRatePerPerson());
        setting.setOperatingRateThresholdPerPerson(this.getOperatingRateThresholdPerPerson());
        setting.setTactTimeInvisible(this.getTactTimeInvisible());
        setting.setAverageWorkTime(this.getAverageWorkTime());
        setting.setTactTimeAchievementRateThreshold(this.getTactTimeAchievementRateThreshold());
        setting.setTactTimePerWorkInvisible(this.getTactTimePerWorkInvisible());
        setting.setAverageWorkTimePerWork(this.getAverageWorkTimePerWork());
        setting.setTactTimeAchievementRateThresholdPerWork(this.getTactTimeAchievementRateThresholdPerWork());
        setting.setInterruptReasonInvisible(this.getInterruptReasonInvisible());
        setting.setMaxInterruptReasonNum(this.getMaxInterruptReasonNum());
        setting.setLossTimeThreshold(this.getLossTimeThreshold());
        setting.setDelayReasonInvisible(this.getDelayReasonInvisible());
        setting.setMaxDelayReasonNum(this.getMaxDelayReasonNum());
        setting.setDelayTimeThreshold(this.getDelayTimeThreshold());
        setting.setCallReasonInvisible(this.getCallReasonInvisible());
        setting.setMaxCallReasonNum(this.getMaxCallReasonNum());
        setting.setPowerBIURL(this.getPowerBIURL());
        return setting;
    }

    /**
     * 各項目を比較して同じ情報を持つか確認する。
     *
     * @param setting　モデル設定
     * @return 各項目が全て等しい場合はtrue、それ以外はfalse
     */
    public boolean equalsConfigInfo(ModelSetting setting) {
        boolean ret = false;
        if (Objects.equals(this.getModelName(), setting.getModelName())
                && Objects.equals(this.getDisplayUnit(), setting.getDisplayUnit())
                && Objects.equals(this.getWorkers(), setting.getWorkers())
                && Objects.equals(this.getProductionVolumeInvisible(), setting.getProductionVolumeInvisible())
                && Objects.equals(this.getPlanningQuantity(), setting.getPlanningQuantity())
                && Objects.equals(this.getAchievementRateThreshold(), setting.getAchievementRateThreshold())
                && Objects.equals(this.getOperatingRateInvisible(), setting.getOperatingRateInvisible())
                && Objects.equals(this.getWorkingPossibleTime(), setting.getWorkingPossibleTime())
                && Objects.equals(this.getTargetOperatingRate(), setting.getTargetOperatingRate())
                && Objects.equals(this.getOperatingRateThreshold(), setting.getOperatingRateThreshold())
                && Objects.equals(this.getWorkingPossibleTimePerPerson(), setting.getWorkingPossibleTimePerPerson())
                && Objects.equals(this.getTargetOperatingRatePerPerson(), setting.getTargetOperatingRatePerPerson())
                && Objects.equals(this.getOperatingRateThresholdPerPerson(), setting.getOperatingRateThresholdPerPerson())
                && Objects.equals(this.getTactTimeInvisible(), setting.getTactTimeInvisible())
                && Objects.equals(this.getAverageWorkTime(), setting.getAverageWorkTime())
                && Objects.equals(this.getTactTimeAchievementRateThreshold(), setting.getTactTimeAchievementRateThreshold())
                && Objects.equals(this.getTactTimePerWorkInvisible(), setting.getTactTimePerWorkInvisible())
                && Objects.equals(this.getAverageWorkTimePerWork(), setting.getAverageWorkTimePerWork())
                && Objects.equals(this.getTactTimeAchievementRateThresholdPerWork(), setting.getTactTimeAchievementRateThresholdPerWork())
                && Objects.equals(this.getInterruptReasonInvisible(), setting.getInterruptReasonInvisible())
                && Objects.equals(this.getMaxInterruptReasonNum(), setting.getMaxInterruptReasonNum())
                && Objects.equals(this.getLossTimeThreshold(), setting.getLossTimeThreshold())
                && Objects.equals(this.getDelayReasonInvisible(), setting.getDelayReasonInvisible())
                && Objects.equals(this.getMaxDelayReasonNum(), setting.getMaxDelayReasonNum())
                && Objects.equals(this.getDelayTimeThreshold(), setting.getDelayTimeThreshold())
                && Objects.equals(this.getCallReasonInvisible(), setting.getCallReasonInvisible())
                && Objects.equals(this.getMaxCallReasonNum(), setting.getMaxCallReasonNum())
                && Objects.equals(this.getPowerBIURL(), setting.getPowerBIURL())
        ){
            ret = true;
        }
        return ret;
    }
}
