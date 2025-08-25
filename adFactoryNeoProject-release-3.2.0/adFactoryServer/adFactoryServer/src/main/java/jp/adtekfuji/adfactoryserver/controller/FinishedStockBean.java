/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.warehouse.Location;
import jp.adtekfuji.adFactory.enumerate.WarehouseEvent;
import jp.adtekfuji.adfactoryserver.common.Constants;
import static jp.adtekfuji.adfactoryserver.controller.WarehouseBean.FOCUS_BARCODE;
import static jp.adtekfuji.adfactoryserver.controller.WarehouseBean.FOCUS_USER_ID;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstProduct;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnMaterial;
import jp.adtekfuji.adfactoryserver.utility.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 * 完成入庫画面
 * 
 * @author s-heya
 */
@Named(value = "finishedStockBean")
@SessionScoped
public class FinishedStockBean extends WarehouseBean {
    private final Logger logger = LogManager.getLogger();

    private String productNo;
    private MstProduct product;
    private ArrayList<ItemData> productInfo;
    private String serial;
    private Integer stockNum;

    
    /**
     * 完成入庫画面を初期化する。
     */
    public void initialize() {
        this.initialize("FINISHEDSTOCK");
    }

    /**
     * フォーカスを遷移する。
     * 
     * @param name 要素名
     */
    @Override
    public void transitionFocus(String name) {
        try {

            switch (name) {
                case FOCUS_USER_ID:
                    PrimeFaces.current().executeScript("focusUserInfoForm();");
                    break;
                case FOCUS_BARCODE:
                    PrimeFaces.current().executeScript("focusItemInfoForm();");
                    break;
                case FOCUS_PRODUCT_NO:
                    PrimeFaces.current().executeScript("focusTextFiled('productNoForm:productNoInput');");
                    break;
                case FOCUS_LOCATION:
                    PrimeFaces.current().executeScript("focusTextFiled('registForm:locationNoInput');");
                    break;
                case FOCUS_LOCATION_CONFIRM:
                    PrimeFaces.current().executeScript("focusTextFiled('registForm:confirmLocationNoInput');");
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 社員番号を設定する。
     *
     * @param userId　社員番号
     */
    @Override
    public void setUserId(String userId) {
        super.setUserId(userId);

        if (!StringUtils.isEmpty(this.getUserName())) {
            this.transitionFocus(FOCUS_PRODUCT_NO);
        }
    }

    /**
     * シリアル番号を取得する。
     * 
     * @return シリアル番号
     */
    public String getSerial() {
        return serial;
    }

    /**
     * シリアル番号を設定する。
     * 
     * @param serial シリアル番号
     */
    public void setSerial(String serial) {
        if (StringUtils.equals(this.serial, serial)) {
            return;
        }
        
        this.serial = serial;
        this.updateButton();
        
        String _materialNo = TrnMaterial.ORDER_PREFIX + this.productNo + "-" + this.serial;
        this.material = this.warehouseModel.findMaterial(_materialNo);
        if (Objects.nonNull(this.material)) {
            //this.setLocationNo(_material.getLocation().getLocationNo());
            
            // 既に入庫されています
            PrimeFaces.current().executeScript("customAlert('" + LocaleUtils.getString("warehouse.warnMsgW008", this.getLanguage()) + "','" + FOCUS_BARCODE + "');");
            PrimeFaces.current().executeScript("soundPlay();");
            return;
        }
        
        this.transitionFocus(FOCUS_LOCATION);
    }

    /**
     * 製品情報フォームを空にする。
     */
    @Override
    protected void clearItemInfoForm() {
        super.clearItemInfoForm();
        this.productNo = "";
        this.product = null;
        this.productInfo = new ArrayList<>(); 
    }

    /**
     * 入力フォームを空にする。
     */
    @Override
    protected void clearRegistForm() {
        super.clearRegistForm();
        this.serial = "";
        this.stockNum = 1;
    }

    /**
     * 登録ボタンの有効性を更新する。
     */
    @Override
    protected void updateButton() {
        boolean enabled = !StringUtils.isEmpty(this.getUserName())
                && Objects.nonNull(this.product)
                && !StringUtils.isEmpty(this.serial)
                && this.stockNum > 0
                && ((!StringUtils.isEmpty(this.getReqLocationNo())) && StringUtils.equals(this.getLocationNo(), this.getReqLocationNo()) 
                        || (!StringUtils.isEmpty(this.getLocationNo()) && StringUtils.equals(this.getLocationNo(), this.getConfirmLocationNo())));
        this.setRegistDisabled(!enabled); 
    }

    /**
     * 入力内容を保存する。
     */
    public void update() {
        try {
            if (Objects.isNull(this.material)) {
                String no = this.productNo + "-" + this.serial;

                this.material = new TrnMaterial();
                this.material.setMaterialNo(TrnMaterial.ORDER_PREFIX + no);
                this.material.setSupplyNo(no);
                this.material.setProduct(this.product);
                this.material.setOrderNo(this.serial);
                this.material.setSerialNo(this.serial);
                this.material.setPartsNo(this.serial);
                this.material.setArrivalNum(1);
                this.material.setStockNum(this.stockNum);
                this.material.setCategory((short) 3);
            }

            Response res = warehouseModel.reciveWarehouseWithRegist(WarehouseEvent.RECEIPT_PRODUCTION, this.getUserId(), this.material, this.product, this.stockNum, this.getAreaName(), this.getLocationNo());
            if (res.getStatus() == HttpURLConnection.HTTP_OK) {
                // 成功
                if (this.isPrint()) {
                    this.createPrintUrl(this.material, this.stockNum, this.serial, this.getAreaName(), this.getLocationNo(), this.getUserId());
                    this.openPrintDialog("FinishedStockPrintDialog", 800, 620);
                } else {
                    this.transitionFocus(FOCUS_BARCODE);
                }

                this.clearRegistForm();
            } else {
                // 失敗
                this.showRegistMessage();
            }
            
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 
     */
    @Override
    public void closePrintDialog() {
        super.closePrintDialog();
        this.transitionFocus(FOCUS_BARCODE);
    }
    
    /**
     * 品目コードを取得する。
     * 
     * @return 品目コード
     */
    public String getProductNo() {
        return productNo;
    }

    /**
     * 品目コードを設定する。
     * 
     * @param productNo 品目コード
     */
    public void setProductNo(String productNo) {
        this.clearItemInfoForm();
        this.clearRegistForm();

        this.productNo = productNo;
    
        this.product = this.warehouseModel.findProduct(productNo);
        if (Objects.isNull(this.product)) {
            // 「該当する情報が存在しません。」
            this.setItemInfoMessage(LocaleUtils.getString("warehouse.errMsgE003", this.getLanguage()));
            PrimeFaces.current().executeScript("soundPlay();");
            PrimeFaces.current().executeScript("focusTextFiled('productNoForm:productNoInput');");
            return;
        }

        this.productInfo.add(new ItemData(LocaleUtils.getString("key.ProductName", this.getLanguage()), product.getProductName()));
 
        Map<String, String> properties = this.product.getProperty();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (adtekfuji.utility.StringUtils.equals(entry.getKey(), Constants.SPEC)) {
                this.productInfo.add(new ItemData(LocaleUtils.getString("key.KikakuKatasiki", this.getLanguage()), entry.getValue()));
            }
        }

        // 指定棚
        if (Objects.nonNull(product.getLocationList())) {
            for (Location location : product.getLocationList()) {
                if (adtekfuji.utility.StringUtils.equals(this.getAreaName(), location.getAreaName())) {
                    this.setReqLocationNo(location.getLocationNo());
                    break;
                }
            }
        }
        
        this.transitionFocus(FOCUS_BARCODE);
    }

    /**
     * 部品マスタを取得する。
     * 
     * @return 部品マスタ
     */
    public MstProduct getProduct() {
        return product;
    }

    /**
     * 部品マスタを設定する。
     * 
     * @param product 部品マスタ
     */
    public void setProduct(MstProduct product) {
        this.product = product;
    }

    /**
     * 製品情報を取得する。
     * 
     * @return 製品情報
     */
    public ArrayList<ItemData> getProductInfo() {
        return productInfo;
    }

    /**
     * 製品情報を設定する。
     * 
     * @param productInfo 製品情報
     */
    public void setProductInfo(ArrayList<ItemData> productInfo) {
        this.productInfo = productInfo;
    }

    /**
     * 入庫数を取得する。
     * 
     * @return 入庫数
     */
    public Integer getStockNum() {
        return stockNum;
    }

    /**
     * 入庫数を設定する。
     * 
     * @param stockNum 入庫数
     */
    public void setStockNum(Integer stockNum) {
        this.stockNum = stockNum;
    }

}
