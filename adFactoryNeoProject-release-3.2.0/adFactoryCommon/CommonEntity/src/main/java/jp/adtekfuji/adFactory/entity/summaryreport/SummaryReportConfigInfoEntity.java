/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.summaryreport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javafx.beans.property.*;
import jp.adtekfuji.adFactory.enumerate.AggregateUnitEnum;
import jp.adtekfuji.adFactory.enumerate.SendFrequencyEnum;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * サマリーレポート設定情報
 *
 * @author okada
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "summaryReportConfigEntity")
public class SummaryReportConfigInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // 名称(レポート名)
    @JsonProperty("title")
    @XmlElement()
    private String title;

    // 有効・無効(無効項目)
    @JsonProperty("disable")
    @XmlElement()
    private Boolean disable;
    
    // 集計単位
    @JsonProperty("aggregateUnit")
    @JsonSerialize(using = AggregateUnitEnum.Serializer.class)
    @JsonDeserialize(using = AggregateUnitEnum.Deserializer.class)
    @XmlElement()
    private AggregateUnitEnum aggregateUnit;

    // 項目名
    @JsonProperty("itemName")
    @XmlElement()
    private String itemName;

    // メール頻度
    @JsonProperty("sendFrequency")
    @JsonSerialize(using = SendFrequencyEnum.Serializer.class)
    @JsonDeserialize(using = SendFrequencyEnum.Deserializer.class)
    @XmlElement()
    private SendFrequencyEnum sendFrequency;

    // メール送信日
    @JsonProperty("sendDate")
    @XmlElement()
    private String sendDate;

    // 送信時間(集計開始時間)
    @JsonProperty("sendTime")
    @XmlElement()
    private String sendTime;

    // メールリスト(送付先)
    @JsonProperty("mails")
    @XmlElementWrapper(name = "mails")
    @XmlElement(name = "mail")
    private List<Long> mails;

    // 要素リスト(メール内容設定)
    @JsonProperty("SummaryReportElements")
    @XmlElementWrapper(name = "summaryReportElementEntities")
    @XmlElement(name = "summaryReportElementEntity")
    LinkedList<SummaryReportConfigElementEntity> summaryReportElementEntities;

    // タイトル項目プロパティ
    @JsonIgnore
    @XmlTransient
    private StringProperty titleProperty = null;

    // 無効項目のプロパティ
    @JsonIgnore
    @XmlTransient
    private BooleanProperty disableProperty = null;

    // 集計単位項目プロパティ
    @JsonIgnore
    @XmlTransient
    private ObjectProperty<AggregateUnitEnum> aggregateUnitProperty = null;

    // 項目名プロパティ
    @JsonIgnore
    @XmlTransient
    private StringProperty itemNameProperty = null;

    // メール頻度のプロパティ
    @JsonIgnore
    @XmlTransient
    private ObjectProperty<SendFrequencyEnum> sendFrequencyProperty = null;

    // 送信日プロパティ
    @JsonIgnore
    @XmlTransient
    private StringProperty sendDateProperty = null;

    // 送信時間プロパティ
    @JsonIgnore
    @XmlTransient
    private StringProperty sendTimeProperty = null;

    // 新規作成フラグ
    @JsonIgnore
    @XmlTransient
    private boolean createFlag = false;

    // ルートフラグ
    @JsonIgnore
    @XmlTransient
    private boolean rootFlag = false;

    // 情報のID(識別番号)
    @JsonIgnore
    @XmlTransient
    private Integer id = null;

    /**
     * コンストラクタ
     */
    public SummaryReportConfigInfoEntity() {
        this.title = "";
        this.disable = false;
        this.aggregateUnit = AggregateUnitEnum.ORDER_PROCESSES;
        this.itemName = "";
        this.sendFrequency = SendFrequencyEnum.EVERYDAY;
        this.sendDate = "";
        this.sendTime = "00:00";
        this.mails = new ArrayList<>();
        this.summaryReportElementEntities = new LinkedList<>();
        this.createFlag = false;
        this.rootFlag = false;
        this.id = null;
    }

    /**
     * コンストラクタ
     *
     * @param title 名称(レポート名)
     */
    public SummaryReportConfigInfoEntity(String title) {
        this();
        this.title = title;
    }

    /**
     * コンストラクタ
     *
     * @param title    名称(レポート名)
     * @param rootFlag ルートフラグ
     */
    public SummaryReportConfigInfoEntity(String title, boolean rootFlag) {
        this();
        this.title = title;
        this.rootFlag = rootFlag;
    }

    /**
     * コンストラクタ
     *
     * @param createFlag 新規作成フラグ
     */
    public SummaryReportConfigInfoEntity(boolean createFlag) {
        this();
        this.createFlag = createFlag;
    }

    /**
     * 名称(レポート名)を設定
     *
     * @param title 名称(レポート名)
     */
    public void setTitle(String title) {
        if (Objects.nonNull(titleProperty)) {
            titleProperty.set(title);
        }
        this.title = title;
    }

    /**
     * 名称(レポート名)のプロパティを取得
     *
     * @return 名称(レポート名)
     */
    public String getTitle() {
        if (Objects.nonNull(titleProperty)) {
            return titleProperty.get();
        }
        return title;
    }

    /**
     * 名称(レポート名)のプロパティを取得
     *
     * @return 名称(レポート名)プロパティ
     */
    public StringProperty titleProperty() {
        if (Objects.isNull(titleProperty)) {
            this.titleProperty = new SimpleStringProperty(this.title);
        }
        return titleProperty;
    }

    /**
     * 有効・無効(無効項目)を設定
     *
     * @param disable 有効・無効(無効項目)
     */
    public void setDisableBoolean(Boolean disable) {
        if (Objects.nonNull(this.disableProperty)) {
            this.disableProperty.set(disable);
        } else {
            this.disable = disable;
        }
    }

    /**
     * 有効・無効(無効項目)を取得
     *
     * @return 有効・無効(無効項目)
     */
    public Boolean getDisable() {
        if (Objects.nonNull(this.disableProperty)) {
            return this.disableProperty.get();
        }
        return this.disable;
    }

    /**
     * 有効・無効(無効項目)のプロパティを取得
     *
     * @return 無効項目プロパティ
     */
    public BooleanProperty disableProperty() {
        if (Objects.isNull(this.disableProperty)) {
            this.disableProperty = new SimpleBooleanProperty(this.disable);
        }
        return this.disableProperty;
    }

    /**
     * 集計単位を設定
     *
     * @param aggregateUnit 集計単位
     */
    public void setAggregateUnit(AggregateUnitEnum aggregateUnit) {
        if (Objects.nonNull(this.aggregateUnitProperty)) {
            this.aggregateUnitProperty.set(aggregateUnit);
        } else {
            this.aggregateUnit = aggregateUnit;
        }
    }

    /**
     * 集計単位を取得
     *
     * @return 集計単位
     */
    public AggregateUnitEnum getAggregateUnit() {
        if (Objects.nonNull(this.aggregateUnitProperty)) {
            return this.aggregateUnitProperty.get();
        }
        return this.aggregateUnit;
    }

    /**
     * 集計単位のプロパティを取得
     *
     * @return 集計単位プロパティ
     */
    public ObjectProperty<AggregateUnitEnum> aggregateUnitProperty() {
        if (Objects.isNull(this.aggregateUnitProperty)) {
            this.aggregateUnitProperty = new SimpleObjectProperty<>(this.aggregateUnit);
        }
        return this.aggregateUnitProperty;
    }

    /**
     * 項目名を設定
     *
     * @param itemName 項目名
     */
    public void setItemName(String itemName) {
        if (Objects.nonNull(this.itemNameProperty)) {
            this.itemNameProperty.set(itemName);
        }
        this.itemName = itemName;
    }

    /**
     * 項目名を取得
     *
     * @return 項目名
     */
    public String getItemName() {
        if (Objects.nonNull(this.itemNameProperty)) {
            return this.itemNameProperty.get();
        }
        return itemName;
    }

    /**
     * 項目名のプロパティを取得
     *
     * @return 項目名プロパティ
     */
    public StringProperty itemNameProperty() {
        if (Objects.isNull(this.itemNameProperty)) {
            this.itemNameProperty = new SimpleStringProperty(this.itemName);
        }
        return this.itemNameProperty;
    }

    /**
     * メール頻度を設定
     *
     * @param sendFrequency メール頻度
     */
    public void setSendFrequency(SendFrequencyEnum sendFrequency) {
        if (Objects.nonNull(this.sendFrequencyProperty)) {
            this.sendFrequencyProperty.set(sendFrequency);
        }
        this.sendFrequency = sendFrequency;
    }

    /**
     * メール頻度を取得
     *
     * @return メール頻度
     */
    public SendFrequencyEnum getSendFrequency() {
        if (Objects.nonNull(this.sendFrequencyProperty)) {
            return this.sendFrequencyProperty.get();
        }
        return this.sendFrequency;
    }

    /**
     * メール頻度のプロパティを取得
     *
     * @return メール頻度プロパティ
     */
    public ObjectProperty<SendFrequencyEnum> sendFrequencyProperty() {
        if (Objects.isNull(this.sendFrequencyProperty)) {
            this.sendFrequencyProperty = new SimpleObjectProperty<>(this.sendFrequency);
        }
        return this.sendFrequencyProperty;
    }

    /**
     * 送信日を設定
     *
     * @param sendDate 送信日
     */
    public void setSendDate(String sendDate) {
        if (Objects.nonNull(this.sendDateProperty)) {
            this.sendDateProperty.set(sendDate);
        }
        this.sendDate = sendDate;
    }

    /**
     * 送信日を取得
     *
     * @return 送信日
     */
    public String getSendDate() {
        if (Objects.nonNull(this.sendDateProperty)) {
            return this.sendDateProperty.get();
        }
        return sendDate;
    }

    /**
     * 送信日のプロパティを取得
     *
     * @return 送信日プロパティ
     */
    public StringProperty sendDateProperty() {
        if (Objects.isNull(this.sendDateProperty)) {
            this.sendDateProperty = new SimpleStringProperty(sendDate);
        }
        return this.sendDateProperty;
    }

    /**
     * 送信時間(集計開始時間)を設定
     *
     * @param sendTime 送信時間
     */
    public void setSendTime(String sendTime) {
        if (Objects.nonNull(this.sendTimeProperty)) {
            this.sendTimeProperty.set(sendTime);
        }

        this.sendTime = sendTime;
    }

    /**
     * 送信時間(集計開始時間)を取得
     *
     * @return 送信時間
     */
    public String getSendTime() {
        if (Objects.nonNull(this.sendTimeProperty)) {
            return this.sendTimeProperty.get();
        }
        return sendTime;
    }

    /**
     * 送信時間のプロパティを取得
     *
     * @return 送信時間
     */
    public StringProperty sendTimeProperty() {
        if (Objects.isNull(this.sendTimeProperty)) {
            this.sendTimeProperty = new SimpleStringProperty(sendTime);
        }
        return this.sendTimeProperty;
    }

    /**
     * メールリストを設定
     *
     * @param mails メールリスト
     */
    public void setMails(List<Long> mails) {
        this.mails = mails;
    }

    /**
     * メールリストを取得
     *
     * @return メールリスト
     */
    public List<Long> getMails() {
        return this.mails;
    }

    /**
     * 要素リスト(メール内容設定)を設定
     *
     * @param summaryReportElementEntities 要素リスト(メール内容設定)
     */
    public void setSummaryReportElementEntities(LinkedList<SummaryReportConfigElementEntity> summaryReportElementEntities) {
        this.summaryReportElementEntities = summaryReportElementEntities;
    }

    /**
     * 要素リスト(メール内容設定)を取得
     *
     * @return サマリーレポート要素
     */
    public LinkedList<SummaryReportConfigElementEntity> getSummaryReportElementEntities() {
        return this.summaryReportElementEntities;
    }

    /**
     * 新規作成フラグを設定
     *
     * @param createFlag 新規作成フラグ
     */
    @JsonIgnore
    public void setCreateFlag(boolean createFlag) {
        this.createFlag = createFlag;
    }

    /**
     * 新規作成フラグを取得
     *
     * @return 新規作成フラグ
     */
    @JsonIgnore
    public boolean getCreateFlag() {
        return this.createFlag;
    }

    /**
     * ルートフラグを設定
     *
     * @param rootFlag ルートフラグ
     */
    @JsonIgnore
    public void setRootFlag(boolean rootFlag) {
        this.rootFlag = rootFlag;
    }

    /**
     * ルートフラグを取得
     *
     * @return ルートフラグ
     */
    @JsonIgnore
    public boolean getRootFlag() {
        return this.rootFlag;
    }

    /**
     * IDを設定
     *
     * @param id ID
     */
    @JsonIgnore
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * IDを取得
     *
     * @return ID
     */
    @JsonIgnore
    public Integer getId() {
        return this.id;
    }

    /**
     * 画面の値と内容が一致しているかどうかを調べる
     *
     * @param info 比較対象のエンティティ
     * @return true:一致している
     */
    public boolean displayInfoEquals(SummaryReportConfigInfoEntity info) {

        return Objects.equals(getTitle(), info.getTitle())
                && Objects.equals(this.getDisable(), info.getDisable())
                && Objects.equals(this.getAggregateUnit(), info.getAggregateUnit())
                && Objects.equals(this.getItemName(), info.getItemName())
                && Objects.equals(this.getSendFrequency(), info.getSendFrequency())
                && SendDateEquals(info.getSendFrequency(), info.getSendDate())
                && Objects.equals(this.getSendTime(), info.getSendTime())
                && mailsListEquals(this.getMails(), info.getMails())
                && summaryReportElementsListEquals(this.getSummaryReportElementEntities(), info.getSummaryReportElementEntities());

    }

    /**
     * メール送信日が一致するか調べる
     *
     * @param sendFrequency メール頻度
     * @param sendDate      メール送信日
     * @return true:一致している
     */
    private boolean SendDateEquals(SendFrequencyEnum sendFrequency, String sendDate) {

        // 毎月以外はチェックを行わない
        if (SendFrequencyEnum.MONTHLY == sendFrequency) {
            return true;
        }

        if (Objects.equals(this.getSendDate(), sendDate)) {
            return true;
        }

        return false;
    }

    /**
     * メールリスト(送付先)の各項目が一致するか調べる
     *
     * @param a 調査対象A
     * @param b 調査対象B
     * @return true:一致している
     */
    private boolean mailsListEquals(List<Long> a, List<Long> b) {
        if (a.size() != b.size()) {
            return false;
        }

        Iterator<Long> it1 = a.iterator();
        Iterator<Long> it2 = b.iterator();

        while (it1.hasNext()) {
            Long entity1 = it1.next();
            Long entity2 = it2.next();
            if (!Objects.equals(entity1, entity2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 要素リスト(メール内容設定)の各項目が一致するか調べる
     *
     * @param a 調査対象A
     * @param b 調査対象B
     * @return true:一致している
     */
    private boolean summaryReportElementsListEquals(List<SummaryReportConfigElementEntity> a, List<SummaryReportConfigElementEntity> b) {
        if (a.size() != b.size()) {
            return false;
        }

        Iterator<SummaryReportConfigElementEntity> it1 = a.iterator();
        Iterator<SummaryReportConfigElementEntity> it2 = b.iterator();

        while (it1.hasNext()) {
            SummaryReportConfigElementEntity entity1 = it1.next();
            SummaryReportConfigElementEntity entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * クローン作成
     * ※StringProperty等プロパティを利用している為、Cloneableインターフェイスを実装する方法では対応出来なかった。
     *
     * @return クローンエンティティ
     */
    @Override
    public SummaryReportConfigInfoEntity clone() {
        SummaryReportConfigInfoEntity entity = new SummaryReportConfigInfoEntity();

        // 名称(レポート名)
        entity.setTitle(this.getTitle());
        // 有効・無効(無効項目)
        entity.setDisableBoolean(this.getDisable());
        // ※集計単位
        entity.setAggregateUnit(this.getAggregateUnit());
        // 項目名設定
        entity.setItemName(this.getItemName());
        // ※メール頻度
        entity.setSendFrequency(this.getSendFrequency());
        // 送信日
        entity.setSendDate(this.getSendDate());
        // 送信時間(集計開始時間)
        entity.setSendTime(this.getSendTime());
        // メールリスト
        entity.setMails(this.getMails());
        // 要素リスト(メール内容設定)
        LinkedList<SummaryReportConfigElementEntity> infos =
                this.summaryReportElementEntities
                        .stream()
                        .map(SummaryReportConfigElementEntity::clone)
                        .collect(Collectors.toCollection(LinkedList::new));

        entity.setSummaryReportElementEntities(infos);
        // 新規作成フラグ
        entity.setCreateFlag(this.getCreateFlag());
        // ルートフラグ
        entity.setRootFlag(this.getRootFlag());
        // IDフラグ
        entity.setId(this.getId());

        return entity;
    }

}
