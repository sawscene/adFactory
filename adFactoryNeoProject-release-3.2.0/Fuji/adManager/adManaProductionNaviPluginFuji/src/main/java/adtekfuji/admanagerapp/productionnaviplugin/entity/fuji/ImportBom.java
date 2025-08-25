/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.entity.fuji;

/**
 * BOM情報インポート用データ
 *
 * @author nar-nakamura
 */
public class ImportBom {

    private String orderNo;// オーダー番号
    private String productionOrderNo;// 製造オーダー番号
    private String serialNumber;// シリアル番号
    private String orderPartNo;// 親品目コード
    private String requiredPartNo;// 子品目コード
    private String processNo;// 工程No
    private String processCode;// 工程コード
    private String processName;// 工程名
    private String requiredQuantity;// 必要量

    /**
     * コンストラクタ
     */
    public ImportBom() {
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
     * 親品目コードを取得する。
     *
     * @return 親品目コード
     */
    public String getOrderPartNo() {
        return this.orderPartNo;
    }

    /**
     * 親品目コードを設定する。
     *
     * @param orderPartNo 親品目コード
     */
    public void setOrderPartNo(String orderPartNo) {
        this.orderPartNo = orderPartNo;
    }

    /**
     * 子品目コードを取得する。
     *
     * @return 子品目コード
     */
    public String getRequiredPartNo() {
        return this.requiredPartNo;
    }

    /**
     * 子品目コードを設定する。
     *
     * @param requiredPartNo 子品目コード
     */
    public void setRequiredPartNo(String requiredPartNo) {
        this.requiredPartNo = requiredPartNo;
    }

    /**
     * 工程Noを取得する。
     *
     * @return 工程No
     */
    public String getProcessNo() {
        return this.processNo;
    }

    /**
     * 工程Noを設定する。
     *
     * @param processNo 工程No
     */
    public void setProcessNo(String processNo) {
        this.processNo = processNo;
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
     * 必要量を取得する。
     *
     * @return 必要量
     */
    public String getRequiredQuantity() {
        return this.requiredQuantity;
    }

    /**
     * 必要量を設定する。
     *
     * @param requiredQuantity 必要量
     */
    public void setRequiredQuantity(String requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    @Override
    public String toString() {
        return new StringBuilder("ImportBom{")
                .append("orderNo=").append(this.orderNo)
                .append(", productionOrderNo=").append(this.productionOrderNo)
                .append(", serialNumber=").append(this.serialNumber)
                .append(", orderPartNo=").append(this.orderPartNo)
                .append(", requiredPartNo=").append(this.requiredPartNo)
                .append(", processNo=").append(this.processNo)
                .append(", processCode=").append(this.processCode)
                .append(", processName=").append(this.processName)
                .append(", requiredQuantity=").append(this.requiredQuantity)
                .append("}")
                .toString();
    }
}
