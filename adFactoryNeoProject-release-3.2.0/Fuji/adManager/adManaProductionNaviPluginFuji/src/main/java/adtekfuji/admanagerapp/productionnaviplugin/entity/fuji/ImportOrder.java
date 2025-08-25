/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.entity.fuji;

/**
 * 計画情報インポート用データ
 *
 * @author nar-nakamura
 */
public class ImportOrder {

    private String workCenter;// ワークセンター
    private String model;// 機種
    private String sn;// S/N
    private String deliveryDate;// 納期日
    private String quantity;// 数量
    private String orderNo;// オーダー番号
    private String productionOrderNo;// 製造オーダー番号
    private String serialNumber;// シリアル番号
    private String processCode;// 工程コード
    private String processName;// 工程名
    private String tactTime;// タクトタイム
    private String partNo;// 品目コード
    private String productName;// 品名
    private String startDatetime;// 開始日時
    private String compDatetime;// 終了日時
    private String workerId;// 作業員ID
    private String procedure;// 手順

    /**
     * コンストラクタ
     */
    public ImportOrder() {
    }

    /**
     * ワークセンターを取得する。
     *
     * @return ワークセンター
     */
    public String getWorkCenter() {
        return this.workCenter;
    }

    /**
     * ワークセンターを設定する。
     *
     * @param workCenter ワークセンター
     */
    public void setWorkCenter(String workCenter) {
        this.workCenter = workCenter;
    }

    /**
     * 機種を取得する。
     *
     * @return 機種
     */
    public String getModel() {
        return this.model;
    }

    /**
     * 機種を設定する。
     *
     * @param model 機種
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * S/Nを取得する。
     *
     * @return S/N
     */
    public String getSn() {
        return this.sn;
    }

    /**
     * S/Nを設定する。
     *
     * @param sn S/N
     */
    public void setSn(String sn) {
        this.sn = sn;
    }

    /**
     * 納期日を取得する。
     *
     * @return 納期日
     */
    public String getDeliveryDate() {
        return this.deliveryDate;
    }

    /**
     * 納期日を設定する。
     *
     * @param deliveryDate 納期日
     */
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * 数量を取得する。
     *
     * @return 数量
     */
    public String getQuantity() {
        return this.quantity;
    }

    /**
     * 数量を設定する。
     *
     * @param quantity 数量
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * オーダー番号を取得する。
     *
     * @return オーダー番号
     */
    public String getOrderNo() {
        return this.orderNo;
    }

    /**
     * オーダー番号を設定する。
     *
     * @param orderNo オーダー番号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 製造オーダー番号を取得する。
     *
     * @return 製造オーダー番号
     */
    public String getProductionOrderNo() {
        return this.productionOrderNo;
    }

    /**
     * 製造オーダー番号を設定する。
     *
     * @param productionOrderNo 製造オーダー番号
     */
    public void setProductionOrderNo(String productionOrderNo) {
        this.productionOrderNo = productionOrderNo;
    }

    /**
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public String getSerialNumber() {
        return this.serialNumber;
    }

    /**
     * シリアル番号を設定する。
     *
     * @param serialNumber シリアル番号
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * 工程コードを取得する。
     *
     * @return 工程コード
     */
    public String getProcessCode() {
        return this.processCode;
    }

    /**
     * 工程コードを設定する。
     *
     * @param processCode 工程コード
     */
    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getProcessName() {
        return this.processName;
    }

    /**
     * 工程名を設定する。
     *
     * @param processName 工程名
     */
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    /**
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public String getTactTime() {
        return this.tactTime;
    }

    /**
     * タクトタイムを設定する。
     *
     * @param tactTime タクトタイム
     */
    public void setTactTime(String tactTime) {
        this.tactTime = tactTime;
    }

    /**
     * 品目コードを取得する。
     *
     * @return 品目コード
     */
    public String getPartNo() {
        return this.partNo;
    }

    /**
     * 品目コードを設定する。
     *
     * @param partNo 品目コード
     */
    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    /**
     * 品名を取得する。
     *
     * @return 品名
     */
    public String getProductName() {
        return this.productName;
    }

    /**
     * 品名を設定する。
     *
     * @param productName 品名
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * 開始日時を取得する。
     *
     * @return 開始日時
     */
    public String getStartDatetime() {
        return this.startDatetime;
    }

    /**
     * 開始日時を設定する。
     *
     * @param startDatetime 開始日時
     */
    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }

    /**
     * 終了日時を取得する。
     *
     * @return 終了日時
     */
    public String getCompDatetime() {
        return this.compDatetime;
    }

    /**
     * 終了日時を設定する。
     *
     * @param compDatetime 終了日時
     */
    public void setCompDatetime(String compDatetime) {
        this.compDatetime = compDatetime;
    }

    /**
     * 作業員IDを取得する。
     *
     * @return 作業員ID
     */
    public String getWorkerId() {
        return this.workerId;
    }

    /**
     * 作業員IDを設定する。
     *
     * @param workerId 作業員ID
     */
    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    /**
     * 手順を取得する。
     *
     * @return 手順
     */
    public String getProcedure() {
        return this.procedure;
    }

    /**
     * 手順を設定する。
     *
     * @param procedure 手順
     */
    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    @Override
    public String toString() {
        return new StringBuilder("ImportOrder{")
                .append(" workCenter=").append(this.workCenter)
                .append(", model=").append(this.model)
                .append(", sn=").append(this.sn)
                .append(", deliveryDate=").append(this.deliveryDate)
                .append(", quantity=").append(this.quantity)
                .append(", orderNo=").append(this.orderNo)
                .append(", productionOrderNo=").append(this.productionOrderNo)
                .append(", serialNumber=").append(this.serialNumber)
                .append(", processCode=").append(this.processCode)
                .append(", processName=").append(this.processName)
                .append(", tactTime=").append(this.tactTime)
                .append(", partNo=").append(this.partNo)
                .append(", productName=").append(this.productName)
                .append(", startDatetime=").append(this.startDatetime)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", workerId=").append(this.workerId)
                .append(", procedure=").append(this.procedure)
                .append("}")
                .toString();
    }
}
