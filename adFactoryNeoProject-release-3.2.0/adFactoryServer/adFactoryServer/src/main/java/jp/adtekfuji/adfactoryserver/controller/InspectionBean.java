/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import java.net.HttpURLConnection;
import java.util.Objects;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adfactoryserver.utility.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 * 受入検査画面
 * 
 * @author s-heya
 */
@Named(value = "inspectionBean")
@SessionScoped
public class InspectionBean  extends WarehouseBean {
    private final Logger logger = LogManager.getLogger();

    private Integer defectNum;
    private String note;

    /**
     * 受入検査画面を初期化する。
     */
    public void initialize() {
        this.initialize("INSPECTION");
        this.defectNum = 0;
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
                case FOCUS_DEFECTNUM:
                    PrimeFaces.current().executeScript("focusTextFiled('registForm:defectNumInput');");
                    break;
                case FOCUS_NOTE:
                    PrimeFaces.current().executeScript("focusTextFiled('registForm:noteInput');");
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
            this.transitionFocus(FOCUS_BARCODE);
        }
    }

    /**
     * バーコードを設定する。
     * 
     * @param barcode バーコード
     */
    @Override
    public void setBarcode(String barcode) {
        if (StringUtils.equals(this.getBarcode(), barcode)) {
            return;
        }
        
        super.setBarcode(barcode);
        
        if (Objects.nonNull(this.material)) {
            this.clearRegistForm();
            
            if (this.material.getInStockNum() <= 0) {
                // 在庫がありません
                this.setItemInfoMessage(LocaleUtils.getString("warehouse.warnMsgW006", this.getLanguage()));
                PrimeFaces.current().executeScript("soundPlay();");
                return;
            }

            if (Objects.nonNull(this.material.getInspectedAt())) {
                // 検査実施済み
                PrimeFaces.current().executeScript("confirmInspection('" + this.getLanguage() + "');");
                PrimeFaces.current().executeScript("soundPlay();");
                return;
            }
            
            this.transitionFocus(FOCUS_DEFECTNUM);
        }
    }

    /**
     * 入力フォームを空にする。
     */
    @Override
    protected void clearRegistForm() {
        super.clearRegistForm();
        this.defectNum = 0;
        this.note = null;
    }

    /**
     * 入力内容を保存する。
     */
    public void update() {
        try {
            Response res = this.warehouseModel.updateMaterialInspection(this.material.getMaterialNo(), this.defectNum, this.note, this.getUserId());

            if (res.getStatus() == HttpURLConnection.HTTP_OK) {
                // 成功
                if (this.isPrint()) {
                    String _areaName = "";
                    String _locationNo = "";

                    if (Objects.nonNull(this.material.getLocation())) {
                        _areaName = this.material.getLocation().getAreaName();
                        _locationNo = this.material.getLocation().getLocationNo();
                    }
                            
                    this.createPrintUrl(this.material, this.material.getStockNum(), null, _areaName, _locationNo, this.getUserId());
                    this.openPrintDialog("InspectionPrintDialog", 800, 620);
                } else {
                    this.transitionFocus(FOCUS_BARCODE);
                }

                this.clearItemInfoForm();
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
     * 不良数を取得する。
     * 
     * @return 不良数 
     */
    public Integer getDefectNum() {
        return defectNum;
    }

    /**
     * 不良数を設定する。
     * 
     * @param defectNum 不良数
     */
    public void setDefectNum(Integer defectNum) {
        this.defectNum = defectNum;
        this.transitionFocus(FOCUS_NOTE);
    }

    /**
     * コメントを取得する。
     * 
     * @return コメント
     */
    public String getNote() {
        return note;
    }

    /**
     * コメントを設定する。
     * 
     * @param note コメント
     */
    public void setNote(String note) {
        this.note = note;
    }
    
    /**
     * メッセージ確認後、資材情報を消去する。
     * (JavaScriptから呼び出される)
     */
    public void confirmMessage() {
        this.clearItemInfoForm();
    }
}
