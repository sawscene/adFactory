package jp.adtekfuji.adFactory.entity.model;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 統計情報集計条件
 *
 * @author kentarou.suzuki
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "summaryReportCondition")
public class SummaryReportCondition  implements Serializable
{
    /**
     * エンティティのバージョン
     */
    private static final long serialVersionUID = 1L;

    /**
     * サマリーIDプロパティ
     */
    private LongProperty summaryIdProperty;

    /**
     * レポート種別プロパティ
     */
    private ObjectProperty<ReportTypeEnum> reportTypeProperty;

    /**
     * 送信周期プロパティ
     */
    private ObjectProperty<SendCycleEnum> sendCycleProperty;

    /**
     * 先頭年月日プロパティ
     */
    private StringProperty fromDateProperty;

    /**
     * 末尾年月日プロパティ
     */
    private StringProperty toDateProperty;

    /**
     * サマリーID
     */
    @XmlElement()
    private Long summaryId;

    /**
     * レポート種別
     */
    @XmlElement()
    private ReportTypeEnum reportType;

    /**
     * 送信周期
     */
    @XmlElement()
    private SendCycleEnum sendCycle;

    /**
     * 先頭年月日
     */
    @XmlElement()
    private String fromDate;

    /**
     * 末尾年月日
     */
    @XmlElement()
    private String toDate;

    /**
     * モデル設定一覧
     */
    @XmlElementWrapper(name = "modelSettings")
    @XmlElement(name = "modelSetting")
    private List<ModelSetting> modelSettings = new ArrayList<>();

    /**
     * コンストラクタ
     */
    public SummaryReportCondition() {
    }

    /**
     * コンストラクタ
     *
     * @param reportType レポート種別 (生産/品質/経営)
     * @param sendCycle 送信周期 (日次/週次/月次)
     */
    public SummaryReportCondition(ReportTypeEnum reportType, SendCycleEnum sendCycle) {
        this.reportType = reportType;
        this.sendCycle = sendCycle;
    }

    /**
     * サマリーレポート設定を初期化して作成する。
     *
     * @return サマリーレポート設定
     */
    public static SummaryReportCondition create() {
        SummaryReportCondition setting = new SummaryReportCondition(ReportTypeEnum.PRODUCTION, SendCycleEnum.DAILY);
        setting.setSummaryId(1L);
        setting.setModelSettings(Arrays.asList(ModelSetting.create()));
        return setting;
    }

    /**
     * サマリーIDプロパティを取得する。
     *
     * @return サマリーIDプロパティ
     */
    public LongProperty summaryIdProperty() {
        if (Objects.isNull(summaryIdProperty)) {
            summaryIdProperty = new SimpleLongProperty(summaryId);
        }
        return summaryIdProperty;
    }

    /**
     * サマリーIDを取得する。
     *
     * @return サマリーID
     */
    public Long getSummaryId() {
        if (Objects.nonNull(summaryIdProperty)) {
            return summaryIdProperty.get();
        }
        return summaryId;
    }

    /**
     * サマリーIDを設定する。
     *
     * @param summaryId サマリーID
     */
    public void setSummaryId(Long summaryId) {
        if (Objects.nonNull(summaryIdProperty)) {
            summaryIdProperty.set(summaryId);
        } else {
            this.summaryId = summaryId;
        }
    }

    /**
     * レポート種別プロパティを取得する。
     *
     * @return レポート種別プロパティ (生産/品質/経営)
     */
    public ObjectProperty<ReportTypeEnum> reportTypeProperty() {
        if (Objects.isNull(this.reportTypeProperty)) {
            this.reportTypeProperty = new SimpleObjectProperty(this.reportType);
        }
        return this.reportTypeProperty;
    }

    /**
     * レポート種別を取得する。
     *
     * @return レポート種別 (生産/品質/経営)
     */
    public ReportTypeEnum getReportType() {
        if (Objects.nonNull(this.reportTypeProperty)) {
            return this.reportTypeProperty.get();
        }
        return this.reportType;
    }

    /**
     * レポート種別を設定する。
     *
     * @param reportType レポート種別 (生産/品質/経営)
     */
    public void setReportType(ReportTypeEnum reportType) {
        if (Objects.nonNull(this.reportTypeProperty)) {
            this.reportTypeProperty.set(reportType);
        } else {
            this.reportType = reportType;
        }
    }

    /**
     * 送信周期プロパティを取得する。
     *
     * @return 送信周期プロパティ (日次/週次/月次)
     */
    public ObjectProperty<SendCycleEnum> sendCycleProperty() {
        if (Objects.isNull(this.sendCycleProperty)) {
            this.sendCycleProperty = new SimpleObjectProperty(this.sendCycle);
        }
        return this.sendCycleProperty;
    }

    /**
     * 送信周期を取得する。
     *
     * @return 送信周期 (日次/週次/月次)
     */
    public SendCycleEnum getSendCycle() {
        if (Objects.nonNull(this.sendCycleProperty)) {
            return this.sendCycleProperty.get();
        }
        return this.sendCycle;
    }

    /**
     * 送信周期を設定する。
     *
     * @param sendCycle 送信周期 (日次/週次/月次)
     */
    public void setSendCycle(SendCycleEnum sendCycle) {
        if (Objects.nonNull(this.sendCycleProperty)) {
            this.sendCycleProperty.set(sendCycle);
        } else {
            this.sendCycle = sendCycle;
        }
    }

    /**
     * 先頭年月日プロパティを取得する。
     *
     * @return 先頭年月日プロパティ
     */
    public StringProperty fromDateProperty() {
        if (Objects.isNull(fromDateProperty)) {
            fromDateProperty = new SimpleStringProperty(fromDate);
        }
        return fromDateProperty;
    }

    /**
     * 先頭年月日を取得する。
     *
     * @return 先頭年月日
     */
    public String getFromDate() {
        if (Objects.nonNull(fromDateProperty)) {
            return fromDateProperty.get();
        }
        return fromDate;
    }

    /**
     * 先頭年月日を設定する。
     *
     * @param fromDate 先頭年月日
     */
    public void setFromDate(String fromDate) {
        if (Objects.nonNull(fromDateProperty)) {
            fromDateProperty.set(fromDate);
        } else {
            this.fromDate = fromDate;
        }
    }

    /**
     * 末尾年月日プロパティを取得する。
     *
     * @return 末尾年月日プロパティ
     */
    public StringProperty toDateProperty() {
        if (Objects.isNull(toDateProperty)) {
            toDateProperty = new SimpleStringProperty(toDate);
        }
        return toDateProperty;
    }

    /**
     * 末尾年月日を取得する。
     *
     * @return 末尾年月日
     */
    public String getToDate() {
        if (Objects.nonNull(toDateProperty)) {
            return toDateProperty.get();
        }
        return toDate;
    }

    /**
     * 末尾年月日を設定する。
     *
     * @param toDate 末尾年月日
     */
    public void setToDate(String toDate) {
        if (Objects.nonNull(toDateProperty)) {
            toDateProperty.set(toDate);
        } else {
            this.toDate = toDate;
        }
    }

    /**
     * モデル設定一覧を取得する。
     *
     * @return モデル設定
     */
    public List<ModelSetting> getModelSettings() {
        return modelSettings;
    }

    /**
     * モデル設定一覧を設定する。
     *
     * @param modelSettings モデル設定一覧
     */
    public void setModelSettings(List<ModelSetting> modelSettings) {
        this.modelSettings = modelSettings;
    }

    /**
     * ハッシュコードを取得する。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.summaryId);
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
        final SummaryReportSetting other = (SummaryReportSetting) obj;
        return Objects.equals(this.getSummaryId(), other.getSummaryId());
    }

    /**
     * 文字列表現を取得する。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("SummaryReportCondition{")
                .append("summaryId=").append(this.summaryId)
                .append(", reportType=").append(this.reportType)
                .append(", sendCycle=").append(this.sendCycle)
                .append(", fromDate=").append(this.fromDate)
                .append(", toDate=").append(this.toDate)
                .append(", modelSettings=").append(this.modelSettings)
                .append("}")
                .toString();
    }
}
