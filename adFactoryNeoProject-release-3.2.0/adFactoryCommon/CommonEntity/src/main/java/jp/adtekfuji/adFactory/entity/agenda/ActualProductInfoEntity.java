package jp.adtekfuji.adFactory.entity.agenda;



import java.util.Date;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;

/**
 * カンバン予実情報
 *
 * @author yu.nara
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ActualProduct")
public class ActualProductInfoEntity {

    @XmlElement()
    private Long kanbanId; // カンバンID

    @XmlElement()
    private String modelName; // モデル名

    @XmlElement()
    private String productNumber; // 製番

    @XmlElement()
    private Date startDatetime;// 開始予定日時

    @XmlElement()
    private Date compDatetime;// 完了予定日時

    @XmlElement()
    private Date actualStartDatetime;// 開始予定日時

    @XmlElement()
    private Date actualCompDatetime;// 完了予定日時

    @XmlElement()
    private StatusPatternEnum kanbanStatus;// 工程ステータス

    @XmlElement()
    private Long workNum; // 工程数

    @XmlElement()
    private Long compWorkNum; // 完了工程数

    @XmlElement()
    private String kanbanAdditionalInfo; // 追加情報

    @XmlElement()
    private String interruptReason; // 中断理由

    @XmlElement()
    private String fontColor;// 中断時の文字色

    @XmlElement()
    private String backColor;// 中断時の背景色

    @XmlElement()
    private LightPatternEnum lightPattern; // 点灯パターン

    @XmlElement()
    private String defectReason; // 不良理由

    @XmlElement()
    private Long lotQuantity; // ロット数量

    @XmlElement()
    private Long compNum; // 完成数

    @XmlElement()
    private Long cycleTime; // 標準サイクルタイム
    
    public ActualProductInfoEntity() {
    }

    /**
     * カンバンID取得
     * @return カンバンID
     */
    public Long getKanbanId() {
        return kanbanId;
    }

    /**
     * カンバンID設定
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * モデル名取得
     * @return モデル名
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * モデル名設定
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 製品番号取得
     * @return 製品番号設定
     */
    public String getProductNumber() {
        return productNumber;
    }

    /**
     * 製品番号設定
     * @param productNumber 製品番号
     */
    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    /**
     * 開始予定時間取得
     * @return 開始予定時間
     */
    public Date getStartDatetime() {
        return startDatetime;
    }

    /**
     * 開始予定時間設定
     * @param startDatetime 開始予定時間
     */
    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    /**
     * 完了予定時間
     * @return 完了予定時間
     */
    public Date getCompDatetime() {
        return compDatetime;
    }

    /**
     * 完了予定時間設定
     * @param compDatetime 完了予定時間
     */
    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    /**
     * 実開始時間取得
     * @return 実開始時間
     */
    public Date getActualStartDatetime() {
        return actualStartDatetime;
    }

    /**
     * 実開始時間設定
     * @param actualStartDatetime 実開始時間
     */
    public void setActualStartDatetime(Date actualStartDatetime) {
        this.actualStartDatetime = actualStartDatetime;
    }

    /**
     * 実完了時間取得
     * @return 実完了時間
     */
    public Date getActualCompDatetime() {
        return actualCompDatetime;
    }

    /**
     * 実完了時間設定
     * @param actualCompDatetime 実完成時間
     */
    public void setActualCompDatetime(Date actualCompDatetime) {
        this.actualCompDatetime = actualCompDatetime;
    }

    /**
     * 状態パターン取得
     * @return 状態パターン
     */
    public StatusPatternEnum getKanbanStatus() {
        return kanbanStatus;
    }

    /**
     * 状態パターン設定
     * @param kanbanStatus 状態パターン
     */
    public void setKanbanStatus(StatusPatternEnum kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
    }

    /**
     * ワーク数取得
     * @return ワーク数取得
     */
    public Long getWorkNum() {
        return workNum;
    }

    /**
     * ワーク数設定
     * @param workNum ワーク数
     */
    public void setWorkNum(Long workNum) {
        this.workNum = workNum;
    }

    /**
     * 完了ワーク数取得
     * @return 完了ワーク数
     */
    public Long getCompWorkNum() {
        return compWorkNum;
    }

    /**
     * 完了ワーク数設定
     * @param compWorkNum 完了ワーク数
     */
    public void setCompWorkNum(Long compWorkNum) {
        this.compWorkNum = compWorkNum;
    }

    /**
     * 追加情報取得
     * @return 追加情報取得
     */
    public String getKanbanAdditionalInfo() {
        return kanbanAdditionalInfo;
    }

    /**
     * 追加情報設定
     * @param kanbanAdditionalInfo 追加情報
     */
    public void setKanbanAdditionalInfo(String kanbanAdditionalInfo) {
        this.kanbanAdditionalInfo = kanbanAdditionalInfo;
    }

    /**
     * 中断理由取得
     * @return 中断理由
     */
    public String getInterruptReason() {
        return interruptReason;
    }

    /**
     * 中断理由設定
     * @param interruptReason 中断理由
     */
    public void setInterruptReason(String interruptReason) {
        this.interruptReason = interruptReason;
    }

    /**
     * フォントカラー取得
     * @return フォントカラー
     */
    public String getFontColor() {
        return fontColor;
    }

    /**
     * フォントカラー設定
     * @param fontColor フォントカラー
     */
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * 背景色取得
     * @return 背景色
     */
    public String getBackColor() {
        return backColor;
    }

    /**
     * 背景色設定
     * @param backColor 背景色
     */
    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    /**
     * 点灯パターン取得
     * @return 点灯パターン
     */
    public LightPatternEnum getLightPattern() {
        return lightPattern;
    }

    /**
     * 点灯パターン設定
     * @param lightPattern 点灯パターン
     */
    public void setLightPattern(LightPatternEnum lightPattern) {
        this.lightPattern = lightPattern;
    }

    /**
     * 不良理由取得
     * @return 不良理由
     */
    public String getDefectReason() {
        return defectReason;
    }

    /**
     * 不良理由設定
     * @param defectReason 不良理由
     */
    public void setDefectReason(String defectReason) {
        this.defectReason = defectReason;
    }


    /**
     * ロット数量取得
     * @return ロット数量取得
     */
    public Long getLotQuantity () {
        return lotQuantity;
    }

    /**
     * ロット数量設定
     * @param lotQuantity ロット数量
     */
    public void setLotQuantity(Long lotQuantity) {
        this.lotQuantity = lotQuantity;
    }

    /**
     * 完成数取得
     * @return 完成数
     */
    public Long getCompNum() {
        return compNum;
    }

    /**
     * 完成数設定
     * @param compNum 完成数
     */
    public void setCompNum(Long compNum) {
        this.compNum = compNum;
    }

    /**
     * 標準サイクルタイム
     * 
     * @return 標準サイクルタイム(秒)
     */
    public Long getCycleTime() {
        return cycleTime;
    }

    
}
