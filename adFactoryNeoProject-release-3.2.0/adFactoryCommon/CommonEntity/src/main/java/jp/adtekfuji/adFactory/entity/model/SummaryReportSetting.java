/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * サマリーレポート設定
 *
 * @author kentarou.suzuki
 */
@XmlRootElement(name = "summaryReportSetting")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryReportSetting implements Serializable {

    /**
     * エンティティのバージョン
     */
    private static final long serialVersionUID = 1L;

    /**
     * 初期値 (送信時刻)
     */
    private static final String DEFAULT_SEND_TIME = "05:00";

    /**
     * 初期値 (レポートを有効にするか)
     */
    private static final Boolean DEFAULT_ENABLED = true;

    /**
     * 初期値 (メール件名)
     */
    private static final String DEFAULT_MAIL_TITLE = "日次生産レポート";

    /**
     * 初期値 (メール本文)
     */
    private static final String DEFAULT_MAIL_BODY = "生産レポートを送ります。";

    /**
     * 初期値 (メール警告背景色)
     */
    private static final String DEFAULT_MAIL_WARNING_BACKCOLOR = "#FF0000";

    /**
     * 初期値 (メール警告文字色)
     */
    private static final String DEFAULT_MAIL_WARNING_FONTCOLOR = "#FFFFFF";

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
     * 送信時刻プロパティ
     */
    private StringProperty sendTimeProperty;

    /**
     * レポートを有効にするかプロパティ
     */
    private BooleanProperty enabledProperty;

    /**
     * メール件名プロパティ
     */
    private StringProperty mailTitleProperty;

    /**
     * メール本文プロパティ
     */
    private StringProperty mailBodyProperty;

    /**
     * メール警告背景色プロパティ
     */
    private StringProperty mailWarningBackColorProperty;

    /**
     * メール警告文字色プロパティ
     */
    private StringProperty mailWarningFontColorProperty;

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
    @JsonProperty("SummaryId")
    private Long summaryId;

    /**
     * レポート種別
     */
    @XmlElement()
    @JsonProperty("Type")
    private ReportTypeEnum reportType;

    /**
     * 送信周期
     */
    @XmlElement()
    @JsonProperty("Cycle")
    private SendCycleEnum sendCycle;

    /**
     * 送信時刻
     */
    @XmlElement()
    @JsonProperty("Time")
    private String sendTime;

    /**
     * レポートを有効にするか
     */
    @XmlElement()
    @JsonProperty("Enabled")
    private Boolean enabled;

    /**
     * メール宛先
     */
    @XmlElement()
    @JsonProperty("Mail")
    private List<String> mailToCollection;

    /**
     * メール件名
     */
    @XmlElement()
    @JsonProperty("MailTitle")
    private String mailTitle;

    /**
     * メール本文
     */
    @XmlElement()
    @JsonProperty("MailBody")
    private String mailBody;

    /**
     * メール警告背景色
     */
    @XmlElement()
    @JsonProperty("MailWarningBackColor")
    private String mailWarningBackColor;

    /**
     * メール警告文字色
     */
    @XmlElement()
    @JsonProperty("MailWarningFontColor")
    private String mailWarningFontColor;

    /**
     * 先頭年月日
     */
    @XmlElement()
    @JsonProperty("FromDate")
    private String fromDate;

    /**
     * 末尾年月日
     */
    @XmlElement()
    @JsonProperty("ToDate")
    private String toDate;

    /**
     * モデル設定
     */
    @XmlElement()
    @JsonProperty("Config")
    private Config config = new Config();

    /**
     * コンストラクタ
     */
    public SummaryReportSetting() {
    }

    /**
     * コンストラクタ
     *
     * @param reportType レポート種別 (生産/品質/経営)
     * @param sendCycle 送信周期 (日次/週次/月次)
     */
    public SummaryReportSetting(ReportTypeEnum reportType, SendCycleEnum sendCycle) {
        this.reportType = reportType;
        this.sendCycle = sendCycle;
    }

    /**
     * サマリーレポート設定を初期化して作成する。
     *
     * @return サマリーレポート設定
     */
    public static SummaryReportSetting create() {
        SummaryReportSetting setting = new SummaryReportSetting(ReportTypeEnum.PRODUCTION, SendCycleEnum.DAILY);
        setting.setSummaryId(1L);
        setting.setSendTime(DEFAULT_SEND_TIME);
        setting.setEnabled(DEFAULT_ENABLED);
        setting.setMailToCollection(new ArrayList<>());
        setting.setMailTitle(DEFAULT_MAIL_TITLE);
        setting.setMailBody(DEFAULT_MAIL_BODY);
        setting.setMailWarningBackColor(DEFAULT_MAIL_WARNING_BACKCOLOR);
        setting.setMailWarningFontColor(DEFAULT_MAIL_WARNING_FONTCOLOR);
        setting.setConfig(Config.create());
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
     * 送信時刻プロパティを取得する。
     *
     * @return 送信時刻プロパティ (HH:MM)
     */
    public StringProperty sendTimeProperty() {
        if (Objects.isNull(sendTimeProperty)) {
            sendTimeProperty = new SimpleStringProperty(sendTime);
        }
        return sendTimeProperty;
    }

    /**
     * 送信時刻を取得する。
     *
     * @return 送信時刻 (HH:MM)
     */
    public String getSendTime() {
        if (Objects.nonNull(sendTimeProperty)) {
            return sendTimeProperty.get();
        }
        return sendTime;
    }

    /**
     * 送信時刻を設定する。
     *
     * @param sendTime 送信時刻 (HH:MM)
     */
    public void setSendTime(String sendTime) {
        if (Objects.nonNull(sendTimeProperty)) {
            sendTimeProperty.set(sendTime);
        } else {
            this.sendTime = sendTime;
        }
    }

    /**
     * レポートを有効にするかプロパティを取得する。
     *
     * @return レポートを有効にするかプロパティ
     */
    public BooleanProperty enabledProperty() {
        if (Objects.isNull(enabledProperty)) {
            enabledProperty = new SimpleBooleanProperty(enabled);
        }
        return enabledProperty;
    }

    /**
     * レポートを有効にするかを取得する。
     *
     * @return レポートを有効にするか
     */
    public Boolean getEnabled() {
        if (Objects.nonNull(enabledProperty)) {
            return enabledProperty.get();
        }
        return enabled;
    }

    /**
     * レポートを有効にするかを設定する。
     *
     * @param enabled レポートを有効にするか
     */
    public void setEnabled(Boolean enabled) {
        if (Objects.nonNull(enabledProperty)) {
            enabledProperty.set(enabled);
        } else {
            this.enabled = enabled;
        }
    }



    /**
     * メール宛先一覧を取得する。
     *
     * @return メール宛先一覧
     */
    public List<String> getMailToCollection() {
        return mailToCollection;
    }

    /**
     * メール宛先一覧を設定する。
     *
     * @param mailToCollection メール宛先一覧
     */
    public void setMailToCollection(List<String> mailToCollection) {
        this.mailToCollection = mailToCollection;
    }

    /**
     * メール件名プロパティを取得する。
     *
     * @return メール件名プロパティ
     */
    public StringProperty mailTitleProperty() {
        if (Objects.isNull(mailTitleProperty)) {
            mailTitleProperty = new SimpleStringProperty(mailTitle);
        }
        return mailTitleProperty;
    }

    /**
     * メール件名を取得する。
     *
     * @return メール件名
     */
    public String getMailTitle() {
        if (Objects.nonNull(mailTitleProperty)) {
            return mailTitleProperty.get();
        }
        return mailTitle;
    }

    /**
     * メール件名を設定する。
     *
     * @param mailTitle メール件名
     */
    public void setMailTitle(String mailTitle) {
        if (Objects.nonNull(mailTitleProperty)) {
            mailTitleProperty.set(mailTitle);
        } else {
            this.mailTitle = mailTitle;
        }
    }

    /**
     * メール本文プロパティを取得する。
     *
     * @return メール本文プロパティ
     */
    public StringProperty mailBodyProperty() {
        if (Objects.isNull(mailBodyProperty)) {
            mailBodyProperty = new SimpleStringProperty(mailBody);
        }
        return mailBodyProperty;
    }

    /**
     * メール本文を取得する。
     *
     * @return メール本文
     */
    public String getMailBody() {
        if (Objects.nonNull(mailBodyProperty)) {
            return mailBodyProperty.get();
        }
        return mailBody;
    }

    /**
     * メール本文を設定する。
     *
     * @param mailBody メール本文
     */
    public void setMailBody(String mailBody) {
        if (Objects.nonNull(mailBodyProperty)) {
            mailBodyProperty.set(mailBody);
        } else {
            this.mailBody = mailBody;
        }
    }

    /**
     * メール警告背景色プロパティを取得する。
     *
     * @return メール警告背景色プロパティ
     */
    public StringProperty mailWarningBackColorProperty() {
        if (Objects.isNull(mailWarningBackColorProperty)) {
            mailWarningBackColorProperty = new SimpleStringProperty(mailWarningBackColor);
        }
        return mailWarningBackColorProperty;
    }

    /**
     * メール警告背景色を取得する。
     *
     * @return メール警告背景色
     */
    public String getMailWarningBackColor() {
        if (Objects.nonNull(mailWarningBackColorProperty)) {
            return mailWarningBackColorProperty.get();
        }
        return mailWarningBackColor;
    }

    /**
     * メール警告背景色を設定する。
     *
     * @param mailWarningBackColor メール警告背景色
     */
    public void setMailWarningBackColor(String mailWarningBackColor) {
        if (Objects.nonNull(mailWarningBackColorProperty)) {
            mailWarningBackColorProperty.set(mailWarningBackColor);
        } else {
            this.mailWarningBackColor = mailWarningBackColor;
        }
    }

    /**
     * メール警告文字色プロパティを取得する。
     *
     * @return メール警告文字色プロパティ
     */
    public StringProperty mailWarningFontColorProperty() {
        if (Objects.isNull(mailWarningFontColorProperty)) {
            mailWarningFontColorProperty = new SimpleStringProperty(mailWarningFontColor);
        }
        return mailWarningFontColorProperty;
    }

    /**
     * メール警告文字色を取得する。
     *
     * @return メール警告文字色
     */
    public String getMailWarningFontColor() {
        if (Objects.nonNull(mailWarningFontColorProperty)) {
            return mailWarningFontColorProperty.get();
        }
        return mailWarningFontColor;
    }

    /**
     * メール警告文字色を設定する。
     *
     * @param mailWarningFontColor メール警告文字色
     */
    public void setMailWarningFontColor(String mailWarningFontColor) {
        if (Objects.nonNull(mailWarningFontColorProperty)) {
            mailWarningFontColorProperty.set(mailWarningFontColor);
        } else {
            this.mailWarningFontColor = mailWarningFontColor;
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
     * モデル設定を取得する。
     *
     * @return モデル設定
     */
    public Config getConfig() {
        return config;
    }

    /**
     * モデル設定を設定する。
     *
     * @param config モデル設定
     */
    public void setConfig(Config config) {
        this.config = config;
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
        return new StringBuilder("SummaryReportSetting{")
                .append("summaryId=").append(this.summaryId)
                .append(", reportType=").append(this.reportType)
                .append(", sendCycle=").append(this.sendCycle)
                .append(", sendTime=").append(this.sendTime)
                .append(", enabled=").append(this.enabled)
                .append(", mailToCollection=").append(this.mailToCollection)
                .append(", mailTitle=").append(this.mailTitle)
                .append(", mailBody=").append(this.mailBody)
                .append(", mailWarningBackColor=").append(this.mailWarningBackColor)
                .append(", mailWarningFontColor=").append(this.mailWarningFontColor)
                .append(", fromDate=").append(this.fromDate)
                .append(", toDate=").append(this.toDate)
                .append(", config=").append(this.config)
                .append("}")
                .toString();
    }

    /**
     * サマリーレポート設定のコピーを新規作成する。
     *
     * @return サマリーレポート設定
     */
    @Override
    public SummaryReportSetting clone() {
        SummaryReportSetting setting = new SummaryReportSetting(this.getReportType(), this.getSendCycle());

        setting.setSummaryId(this.getSummaryId());
        setting.setSendTime(this.getSendTime());
        setting.setEnabled(this.getEnabled());
        setting.setMailToCollection(new ArrayList(this.getMailToCollection()));
        setting.setMailTitle(this.getMailTitle());
        setting.setMailBody(this.getMailBody());
        setting.setFromDate(this.getFromDate());
        setting.setToDate(this.getToDate());
        setting.setMailWarningBackColor(this.getMailWarningBackColor());
        setting.setMailWarningFontColor(this.getMailWarningFontColor());
        setting.setConfig(this.getConfig());
        
        return setting;
    }
}
