/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import adtekfuji.utility.StringUtils;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.inject.Named;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.common.ServiceConfig;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.model.warehouse.WarehouseModel;
import jp.adtekfuji.adfactoryserver.service.SystemResource;
import jp.adtekfuji.adfactoryserver.utility.CookieUtils;
import jp.adtekfuji.adfactoryserver.utility.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 倉庫案内メニュー画面 データコントローラー
 * @author 18-0326
 */
@Named(value = "menuData")
@SessionScoped
public class MenuDataController implements Serializable {
    
    private final Logger logger = LogManager.getLogger();

    /**
     * エリアマスタ情報
     */
    List<String> areaList;

    /**
     * エリアマスタ選択表示
     */    
    private String areaName;
    
    /**
     * ボタン無効化切替
     */  
    private Boolean disabled;

    /**
     * 棚卸中
     */
    private Boolean inventory;
    
    //エラーメッセージ
    private String errMsg;
   
    private String language;

    @EJB
    private WarehouseModel warehouseModel;
    
    @EJB
    private SystemResource systemResource;

    public MenuDataController() {
        this.areaName = "";
        this.errMsg ="";
        this.disabled = true;
        //エリアマスタ取得処理
        areaList = new ArrayList<>();
    }
   
    /**
     * 倉庫案内メニュー画面を初期化する。
     * 
     */
    @PostConstruct
    public void init() {
        this.errMsg = "";

        this.onChangeLocale(null);
        
        try {
            this.areaList = warehouseModel.getAreaNames();
        } catch(Exception ex){
            this.areaList = null;
        }
        
        if(Objects.nonNull(areaList) && areaList.size() > 0){
            // エリアマスタ取得成功
            this.disabled = false;

            String _areaName = CookieUtils.getCookieValue("areaName");
            if (!StringUtils.isEmpty(_areaName)) {
                this.areaName = _areaName;
            } else {
                this.areaName = this.areaList.get(0);
            }
        } else {
            // エリアマスタ取得失敗 又は　取得件数が0件
            errMsg = LocaleUtils.getString("warehouse.errMsgE001", this.language);
        }

        this.onChangeArea(null);
    }
    
    /**
     * エリア名 設定 
     * @return 
     */    
    public String getAreaName() {
        return areaName;
    }
    
    /**
     * エリア名 取得 
     * @param areaName
     */    
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    /**
     * エリアリスト 取得
     * @return 
     */    
    public List<String> getAreaList() {
        return areaList;
    }
    
    /**
     * 操作の無効かどうかを返す。
     * 
     * @return true: 操作無効、false:操作有効
     */    
    public boolean isDisabled() {
        return disabled;
    }

     /**
     * エラーメッセージ 取得
     * @return 
     */    
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * ライセンスを確認する。
     */
    public void verifyLicense() {
        LicenseManager licenseManager = LicenseManager.getInstance();
        if (!licenseManager.getLicenseOption(Constants.LICENSE_WAREHOUSE)) {
            this.disabled = true;
            // 「倉庫案内の使用が許可されていません。」
            this.errMsg = LocaleUtils.getString("warehouse.errMsgE012", this.language);

            try {
                // 画面遷移
                FacesContext facesContext = FacesContext.getCurrentInstance();
                String contextPath = facesContext.getExternalContext().getRequestContextPath();
                facesContext.getExternalContext().redirect(contextPath + "/warehouse/MenuBlank.xhtml");	
            } catch (IOException ex) {
                logger.fatal(ex, ex);
            }
        }

        String cookieAreaName = CookieUtils.getCookieValue("areaName");
        if (!StringUtils.isEmpty(cookieAreaName)) {
            this.inventory = warehouseModel.getAreaInventoryFlag(cookieAreaName);
        }
    }

    /**
     * 入出庫が無効かどうかを取得する。
     *
     * @return 入出庫が無効かどうか(true: 無効, false: 有効)
     */
    public Boolean getDisabledInOut() {
        // 倉庫機能が無効、または棚卸中の区画の場合は無効
        return this.disabled || this.inventory;
    }

    /**
     * 棚卸が無効かどうかを取得する。
     *
     * @return 棚卸が無効かどうか(true: 無効, false: 有効)
     */
    public Boolean getDisabledInventory() {
        // 倉庫機能が無効、または棚卸中でない区画の場合は無効
        return this.disabled || !this.inventory;
    }

    /**
     * 言語を切り替える。
     * 
     * @param event 
     */
    public void onChangeLocale(AjaxBehaviorEvent event){  
        try {
            List<LocaleBean> locales = this.getLocaleList();
            this.language = locales.get(0).getLocale().getLanguage();

            String _language = this.isLanguageOption() ? CookieUtils.getCookieValue("locale") : this.language;
            locales.stream().filter(o -> o.getLocale().getLanguage().equals(_language)).findFirst().ifPresent(o -> {
                FacesContext context = FacesContext.getCurrentInstance();
                UIViewRoot viewRoot = context.getViewRoot();
                viewRoot.setLocale(o.getLocale());

                this.language = _language;
            });
        
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 区画選択イベント
     *
     * @param event 
     */
    public void onChangeArea(AjaxBehaviorEvent event){  
        try {
            String cookieAreaName = CookieUtils.getCookieValue("areaName");
            if (!StringUtils.isEmpty(cookieAreaName)) {
                this.areaName = cookieAreaName;
            }

            this.inventory = warehouseModel.getAreaInventoryFlag(this.areaName);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.inventory = false;
        }
    }
    
    /**
     * 動作モードを取得する。
     * 
     * @return 
     */
    public String getWarehouseMode() {
        return ServiceConfig.getInstance().getWarehouseMode();
    }

    /**
     * 言語を取得する。
     * 
     * @return 言語
     */
    public String getLanguage() {
        return language;
    }

    /**
     * 言語を設定する。
     * 
     * @param language 言語 
     */
    public void setLanguage(String language) {
    }

    /**
     * ロケール一覧を取得する。
     * 
     * @return ロケール一覧
     */
    public List<LocaleBean> getLocaleList() {
        return Arrays.asList(new LocaleBean("日本語", Locale.JAPANESE), new LocaleBean("English", Locale.ENGLISH));
    }
    
    /**
     * 言語切替オプションが有効かどうかを返す。
     * 
     * @return true: 有効、false: 無効
     */
    public Boolean isLanguageOption() {
        return this.systemResource.findLicenseOption("@LanguageOption", null).getEnable();
    }
    
}
