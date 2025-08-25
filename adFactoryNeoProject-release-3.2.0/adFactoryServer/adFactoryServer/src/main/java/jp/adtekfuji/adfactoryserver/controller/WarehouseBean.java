/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import adtekfuji.utility.StringUtils;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.ejb.EJB;
import jp.adtekfuji.adFactory.entity.warehouse.Location;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.common.ServiceConfig;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstLocation;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstProduct;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnMaterial;
import jp.adtekfuji.adfactoryserver.model.warehouse.WarehouseModel;
import jp.adtekfuji.adfactoryserver.utility.CookieUtils;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.adfactoryserver.utility.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 * 倉庫案内ビーン
 * 
 * @author s-heya
 */
public class WarehouseBean implements Serializable {
    
    /**
     * 画面ID
     */
    public enum Screen {
        ACCEPT,     // 受入画面
        ENTRY,      // 入庫画面
        PICKING,    // 出庫画面
        INVENTORY,  // 棚卸画面
        SHEIFMOVE,  // 資材移動画面
        REPRINT,    // ラベル印刷画面
        INSPECTION, // 受入検査画面
        FINISHEDSTOCK; // 完成入庫
    }
    
    protected static final String FOCUS_USER_ID = "userIdInput";
    protected static final String FOCUS_LOCATION = "locationNoInput";
    protected static final String FOCUS_LOCATION_CONFIRM = "registForm:confirmLocationNoInput";
    protected static final String FOCUS_BARCODE = "barcodeInput";
    protected static final String FOCUS_DEFECTNUM = "defectNumInput"; // 不良品
    protected static final String FOCUS_NOTE = "noteInput"; // コメント
    protected static final String FOCUS_PRODUCT_NO = "productNoInput"; // 品目
    protected static final String FOCUS_PICKING_ID = "pickingIdInput";    // 出庫指示番号テキストボックス
    protected static final String FOCUS_STOCKOUT = "StockOutValInput";    // 出庫数数テキストボックス

    private final Logger logger = LogManager.getLogger();
    private final ServiceConfig config = ServiceConfig.getInstance();

    @EJB
    protected WarehouseModel warehouseModel;
   
    private Screen screenId;
    private String language;
    private String[] patterns = new String[4];
    private String areaName;                            // 区画
    private boolean registDisabled;                     // 登録ボタンの有効
        
    // 社員情報フォーム
    private String userId;                              // 社員番号
    private String userName;                            // 社員名
    private boolean showUserError;                      // エラーメッセージの表示

    // 資材情報フォーム
    private String barcode;                             // バーコード
    private Map<String, String> materialItems;
    protected TrnMaterial material;
    private ArrayList<ItemData> details;
    private String itemInfoStyle;
    private String itemInfoMessage;
    private String reqLocationNo;
        
    // 入力フォーム
    private String locationNo;
    private String confirmLocationNo;
    private String registMessage;
    
    private boolean print;
    private String printUrl;
    private String printLog;
    private boolean printReqResult;
    private boolean hiddenPrintDialog;
    private String copies;
     
    /**
     * 倉庫案内を初期化する。
     * 
     * @param screenId 画面ID
     */
    public void initialize(String screenId) {
        try {
            this.screenId = Screen.valueOf(screenId);

            this.language = CookieUtils.getCookieValue("locale");
            if (StringUtils.isEmpty(this.language)) {
                this.language = "ja";
            }
            this.areaName = CookieUtils.getCookieValue("areaName");
            this.userId = CookieUtils.getCookieValue("userId");
            this.setUserId(userId);

            String printFlg = CookieUtils.getCookieValue("printFlg");
            if (!StringUtils.isEmpty(printFlg)) {
                setPrint(Boolean.valueOf(printFlg));
            }
    
            this.patterns[0] = this.config.getPatternE1();
            this.patterns[1] = this.config.getPatternE3();
            this.patterns[2] = this.config.getPatternE5();
            this.patterns[3] = this.config.getPatternE6();
        
            String values = this.config.getMaterialItems();
            this.materialItems = JsonUtils.jsonToMap(values);
            
            this.clearItemInfoForm();
            this.clearRegistForm();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.materialItems = new HashMap<>();
        }
    }



    /**
     * 画面IDを取得する。
     * 
     * @return 画面ID
     */
    public Screen getScreenId() {
        return screenId;
    }
   
    /**
     * ラベルを印刷するかどうかを返す。
     * 
     * @return true: 印刷する、false: 印刷しない 
     */
    public boolean isPrint() {
        return print;
    }

    /**
     * ラベルを印刷するかどうかを設定する。
     * 
     * @param print true: 印刷する、false: 印刷しない 
     */
    public void setPrint(boolean print) {
        this.print = print;
    }

    /**
     * 印刷発行URLを取得する。
     *
     * @return 印刷発行URL
     */
    public String getPrintUrl() {
        return printUrl;
    }

    /**
     * 印刷エラーメッセージを取得する。
     * 
     * @return 印刷エラーメッセージ
     */
    public String getPrintLog() {
        return printLog;
    }

    /**
     * 印刷エラーメッセージを設定する。
     * 
     * @param printLog 印刷エラーメッセージ
     */
    public void setPrintLog(String printLog) {
        this.printLog = printLog;
        
    }

    /**
     * 印刷結果を取得する。
     * 
     * @return 印刷結果
     */
    public boolean getPrintReqResult() {
        return printReqResult;
    }

    /**
     * 印刷結果を設定する。
     * 
     * @param printReqResult 印刷結果
     */
    public void setPrintReqResult(boolean printReqResult) {
        this.printReqResult = printReqResult;
    }

    /**
     * 印刷ダイアログが非表示かどうかを返す。
     * 
     * @return true: 非表示
     */
    public boolean isHiddenDialog() {
        return hiddenPrintDialog;
    }

    /**
     * 印刷ダイアログを非表示にする。
     * 
     * @param hide true: 非表示
     */
    public void setHiddenDialog(boolean hide) {
        this.hiddenPrintDialog = hide;
        if (this.hiddenPrintDialog) {
            this.closePrintDialog();
        }
    }

    public String getCopies() {
        return copies;
    }

    public void setCopies(String copies) {
        this.copies = copies;
    }

    /**
     * 印刷部数一覧を取得する。
     *
     * @return 印刷部数一覧
     */
    public List<Integer> getNumList() {
        return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    /**
     * 印刷発行コマンドを生成する。
     * 
     * @param trnMaterial 資材情報
     * @param quantity 数量
     * @param orderNo 製造番号
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @param userId 社員番号
     */
    protected void createPrintUrl(TrnMaterial trnMaterial, Integer quantity, String orderNo, String areaName, String locationNo, String userId) {

        this.printUrl = "";

        if (Objects.isNull(trnMaterial)) {
            return;
        }

        try {
            if (StringUtils.isEmpty(orderNo)) {
                orderNo = trnMaterial.getOrderNo();
            }
            
            String spec = "";
            String note = "";

            if (Objects.nonNull(trnMaterial.getProperty())) {
                // 型式・仕様
                if (trnMaterial.getProperty().containsKey(Constants.SPEC)) {
                    spec = StringUtils.isEmpty(trnMaterial.getProperty().get(Constants.SPEC)) ? "" : trnMaterial.getProperty().get(Constants.SPEC);
                }

                // 備考
                if (trnMaterial.getProperty().containsKey(Constants.NOTE)) {
                    note = StringUtils.isEmpty(trnMaterial.getProperty().get(Constants.NOTE)) ? "" : URLEncoder.encode(trnMaterial.getProperty().get(Constants.NOTE), Constants.UTF_8);
                }
            }
            
            // 区画名、棚番号
            String area = "";
            String location = "";
            switch (this.screenId) {
                case PICKING:
                case REPRINT:
                    if (Objects.nonNull(trnMaterial.getLocation())) {
                        area = URLEncoder.encode(trnMaterial.getLocation().getAreaName(), Constants.UTF_8);
                        location = URLEncoder.encode(trnMaterial.getLocation().getLocationNo(), Constants.UTF_8);
                    }
                    break;
                case INVENTORY:
                    if (Objects.nonNull(trnMaterial.getInventoryLocation())) {
                        // 棚卸の場合は訂正棚番
                        area = URLEncoder.encode(trnMaterial.getInventoryLocation().getAreaName(), Constants.UTF_8);
                        location = URLEncoder.encode(trnMaterial.getInventoryLocation().getLocationNo(), Constants.UTF_8);
                    } else if (Objects.nonNull(trnMaterial.getLocation())) {
                        area = URLEncoder.encode(trnMaterial.getLocation().getAreaName(), Constants.UTF_8);
                        location = URLEncoder.encode(trnMaterial.getLocation().getLocationNo(), Constants.UTF_8);
                    }
                    break;
                default:
                    area = URLEncoder.encode(areaName, Constants.UTF_8);
                    location = URLEncoder.encode(locationNo, Constants.UTF_8);
                    break;
            }
           
            Date date = new Date();

            // SmaPri 印刷コマンドを生成
            StringBuilder sb = new StringBuilder(this.getBaseUrl());
            sb.append("/Format/Print?__format_archive_url=");

            // フォーマットファイルのURL
            sb.append(URLEncoder.encode(warehouseModel.getServerAddress(), Constants.UTF_8));
            sb.append("/adFactoryServer/deploy/SmaPri.spfmtz");

            // フォーマットファイルの更新チェック
            sb.append("&__format_archive_update=update");

            if (!Screen.INVENTORY.equals(this.screenId)) {
                // 現品ラベル
                sb.append("&__format_id_number=");
                sb.append(this.config.getPrintFormat());
                sb.append("&order=");
                sb.append(StringUtils.isEmpty(orderNo) ? "" : URLEncoder.encode(orderNo, Constants.UTF_8));
                sb.append("&note=");
                sb.append(note);
            } else {
                // 棚卸ラベル
                sb.append("&__format_id_number=2");
            }

            sb.append("&material=");
            //sb.append(URLEncoder.encode(trnMaterial.getMaterialNo(), Constants.UTF_8));
            sb.append(URLEncoder.encode(trnMaterial.getSupplyNo(), Constants.UTF_8));
            sb.append("&product=");
            sb.append(URLEncoder.encode(trnMaterial.getProduct().getProductNo(), Constants.UTF_8));
            sb.append("&name=");
            sb.append(StringUtils.isEmpty(trnMaterial.getProduct().getProductName()) ? "" : URLEncoder.encode(trnMaterial.getProduct().getProductName(), Constants.UTF_8));
            sb.append("&spec=");
            sb.append(spec);
            sb.append("&lotNo=");
            sb.append(StringUtils.isEmpty(trnMaterial.getPartsNo()) ? "" : URLEncoder.encode(trnMaterial.getPartsNo(), Constants.UTF_8));
            sb.append("&area=");
            sb.append(area);
            sb.append("&location=");
            sb.append(location);
            sb.append("&stock=");
            sb.append(quantity);
            sb.append("&person=");
            sb.append(URLEncoder.encode(userId, Constants.UTF_8));
            sb.append("&date=");
            sb.append(URLEncoder.encode(new SimpleDateFormat("yy/MM/dd").format(date), Constants.UTF_8));
            sb.append("&time=");
            sb.append(URLEncoder.encode(new SimpleDateFormat("HH:mm").format(date), Constants.UTF_8));
           
            this.printUrl = sb.toString();

        } catch (UnsupportedEncodingException ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * ベースURLを取得する。
     */
    protected String getBaseUrl() {
        String baseUrl;

        String URLScheme = this.config.getPrintUrlScheme();
        String fqdn = this.config.getPrintUrlFqdn();
        String basePath = this.config.getPrintUrlBasePath();

        if (StringUtils.isEmpty(basePath)) {
            baseUrl = URLScheme + "://" + fqdn;
        } else {
            baseUrl = URLScheme + "://" + fqdn + "//" + basePath;
        }

        return baseUrl;
    }

    /**
     * 印刷ダイアログを開く。
     * 
     * @param name ダイアログ名
     * @param height ダイアログの高さ
     * @param width ダイアログの幅
     * 
     */
    public void openPrintDialog(String name, int width, int height) {
        logger.info("openPrintDialog: " + name);

        this.printLog = "";
        this.printReqResult = false;
        this.hiddenPrintDialog = false;

        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("position", "top");
        options.put("contentWidth", width);
        options.put("contentHeight", height);
        options.put("closable", false);

        //RequestContext.getCurrentInstance().openDialog(name, options, null);
        PrimeFaces.current().dialog().openDynamic(name, options, null);
    }

    /**
     * 印刷ダイアログを閉じる。
     */
    public void closePrintDialog() {
        logger.info("closePrintDialog.");
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.closeDialog("OK");
        //context.execute("window.parent.location.reload();");
        PrimeFaces.current().dialog().closeDynamic("OK");
        PrimeFaces.current().executeScript("window.parent.location.reload();");
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
     * 現品ラベルを印刷する。
     */
    public void printLabel() {
        //RequestContext context = RequestContext.getCurrentInstance();
        //context.execute("sendPrintRequest('" + this.printUrl + "','" + this.language + "');");
        PrimeFaces.current().executeScript("sendPrintRequest('" + this.printUrl + "','" + this.language + "');");
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
     * 区画名を取得する。
     * 
     * @return 区画名
     */
    public String getAreaName() {
        return this.areaName;
    }

    /**
     * 社員番号を取得する。
     *
     * @return 社員番号
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * 社員番号を設定する。
     *
     * @param userId　社員番号
     */
    public void setUserId(String userId) {
        this.userId = userId;
        this.showUserError = false;

        OrganizationEntity organization = warehouseModel.findOrganization(userId);
        if (Objects.nonNull(organization) && !StringUtils.isEmpty(organization.getOrganizationName())) {
            this.userName = organization.getOrganizationName();

        } else {
            this.userName = "";
            this.showUserError = true;

            //RequestContext context = RequestContext.getCurrentInstance();
            //context.execute("focusUserInfoForm();");
            //context.execute("soundPlay();");
            PrimeFaces.current().executeScript("focusUserInfoForm();");
            PrimeFaces.current().executeScript("soundPlay();");
        }
    }

    /**
     * 社員名 取得
     *
     * @return 社員名
     */
    public String getUserName() {
        return userName;
    }

    public boolean isShowUserError() {
        return showUserError;
    }
   
    /**
     * バーコードを取得する。
     * 
     * @return バーコード
     */
    public String getBarcode() {
        return this.barcode;
    }
    
    /**
     * バーコードを設定する。
     * 
     * @param barcode バーコード
     */
    public void setBarcode(String barcode) {
        logger.info("setBarcode: " + barcode);

        try {
            this.clearItemInfoForm();
            this.barcode = barcode;
            this.material = this.findMaterial(barcode);

            if (Objects.isNull(this.material)) {
                // 「該当する情報が存在しません。」
                this.itemInfoMessage = LocaleUtils.getString("warehouse.errMsgE003", this.language);
                //RequestContext context = RequestContext.getCurrentInstance();
                //context.execute("soundPlay();");
                //context.execute("focusItemInfoForm();");
                PrimeFaces.current().executeScript("soundPlay();");
                PrimeFaces.current().executeScript("focusItemInfoForm();");
                return;
            }

            MstProduct product = this.material.getProduct();

            Map<String, String> properties = this.material.getProperty();
            for (Map.Entry<String, String> entry : this.materialItems.entrySet()) {
                if (StringUtils.equals(entry.getKey(), Constants.PRODUCT_NO)) {
                    this.details.add(new ItemData(entry.getValue(), product.getProductNo()));

                } else if (StringUtils.equals(entry.getKey(), Constants.PRODUCT_NAME)) {
                    this.details.add(new ItemData(entry.getValue(), product.getProductName()));

                } else if (StringUtils.equals(entry.getKey(), Constants.FIGURE_NO)) {
                    this.details.add(new ItemData(entry.getValue(), product.getFigureNo()));

                } else if (StringUtils.equals(entry.getKey(), Constants.ORDER_NO)) {
                    this.details.add(new ItemData(entry.getValue(), this.material.getOrderNo()));

                } else if (StringUtils.equals(entry.getKey(), Constants.LOT_NO)) {
                     this.details.add(new ItemData(entry.getValue(), this.material.getPartsNo()));

                } else if (Objects.nonNull(properties) && properties.containsKey(entry.getKey())) {
                    String value = properties.get(entry.getKey());
                    this.details.add(new ItemData(entry.getValue(), value));
                }
            }

            // 指定棚
            if (Objects.nonNull(product.getLocationList())) {
                for (Location location : product.getLocationList()) {
                    if (StringUtils.equals(this.areaName, location.getAreaName())) {
                        break;
                    }
                }
            }

            // 重要度
            if (Objects.nonNull(product.getImportantRank())) {
                switch (product.getImportantRank()) {
                    case 1: // 取り扱い注意品
                        this.itemInfoStyle = Constants.IMPORTANT_STYLE;
                        this.itemInfoMessage = LocaleUtils.getString("warehouse.importantItem", this.language);
                        break;

                    case 2: // 要検査品
                        if (Objects.isNull(this.material.getInspectedAt())) {
                            // 要検査
                            this.itemInfoStyle = Constants.INSPECTION_STYLE;
                            this.itemInfoMessage = LocaleUtils.getString("warehouse.insepectionRequired", this.language);
                        } else {
                            // 検査済み
                            this.itemInfoStyle = Constants.IMPORTANT_STYLE;
                            this.itemInfoMessage = LocaleUtils.getString("warehouse.inspected", this.language);
                        }
                        break;

                    case 0: // 通常
                    default:
                        this.itemInfoStyle = Constants.NORMAL_STYLE;
                        this.itemInfoMessage = "";
                        break;
                 }
            }
        
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            
        } finally {
            this.updateButton();
        }
    }

    /**
     * 資材情報を取得する。
     * 
     * @return 資材情報
     */
    public TrnMaterial getMaterial() {
        return this.material;
    }

    /**
     * 資材情報を設定する。
     * 
     * @param material 資材情報
     */
    public void setMaterial(TrnMaterial material) {
        this.material = material;
    }
    
    /**
     * 資材詳細情報を取得する。
     *
     * @return 資材詳細情報
     */
    public ArrayList<ItemData> getDetails() {
        return this.details;
    }

    /**
     * 資材情報フォームのスタイルを取得する。
     * 
     * @return 資材情報フォームのスタイル
     */
    public String getItemInfoStyle() {
        return itemInfoStyle;
    }
    
    /**
     * 納入数を取得する。
     * 
     * @return 納入数
     */
    public Integer getArrivalNum() {
        return Objects.nonNull(this.material) ? this.material.getArrivalNum() : 0;
    }

    /**
     * 在庫数を取得する。
     * 
     * @return 在庫数
     */
    public Integer getInStockNum() {
        return Objects.nonNull(this.material) ? this.material.getInStockNum() : 0;
    }

    /**
     * 資材情報フォームのメッセージを取得する。
     * 
     * @return 
     */
    public String getItemInfoMessage() {
        return this.itemInfoMessage;
    }

    /**
     * 資材情報フォームのメッセージを設定する。
     * 
     * @param itemInfoMessage メッセージ
    */
    public void setItemInfoMessage(String itemInfoMessage) {
        this.itemInfoMessage = itemInfoMessage;
    }
    
    /**
     * 登録フォームのメッセージを取得する。
     * 
     * @return メッセージ 
     */
    public String getRegistMessage() {
        return registMessage;
    }

    /**
     * 登録フォームのメッセージを設定する。
     * 
     * @param registMessage メッセージ 
     */    
    public void setRegistMessage(String registMessage) {
        this.registMessage = registMessage;
    }

    /**
     * 登録ボタンの無効性を返す。
     * 
     * @return 
     */
    public boolean isRegistDisabled() {
        return registDisabled;
    }

    /**
     * 登録ボタンの無効性を設定する。
     * 
     * @param registDisabled 
     */
    public void setRegistDisabled(boolean registDisabled) {
        this.registDisabled = registDisabled;
    }

    /**
     * フォーカスを遷移する。
     * 
     * @param name 要素名
     */
    public void transitionFocus(String name) {
    }

    /**
     * 資材情報を検索する。
     * 
     * @param barcode バーコード
     * @return 資材情報
     */
    protected TrnMaterial findMaterial(String barcode) {
        try {
            if (!StringUtils.isEmpty(barcode)) {

                // QRラベル(又は資材番号直接入力)による資材照会
                if (barcode.startsWith(TrnMaterial.SUPPLY_PREFIX) || barcode.startsWith(TrnMaterial.ORDER_PREFIX)) {
                    return warehouseModel.findMaterial(barcode);
                }

                return warehouseModel.findMaterialBySupplyNo(barcode);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }
    
    /**
     * 資材情報フォームを空にする。
     */
    protected void clearItemInfoForm() {
        this.material = null;
        this.barcode = "";
        this.itemInfoStyle = Constants.NORMAL_STYLE;
        this.details = new ArrayList<>();
        this.itemInfoMessage = "";
        this.reqLocationNo = "";
    }
    
    /**
     * 登録フォームを空にする。
     */
    protected void clearRegistForm() {
        this.registMessage = "";
        this.locationNo = "";
        this.confirmLocationNo = "";
    }
    
    protected void clearMaterial() {
        this.material = null;
        this.itemInfoStyle = Constants.NORMAL_STYLE;
        this.details = new ArrayList<>();
        this.itemInfoMessage = "";
    }

    /**
     * 入力フォームにメッセージを表示する。
     */
    protected void showRegistMessage() {
        try {
            this.transitionFocus(FOCUS_BARCODE);
            this.registMessage = LocaleUtils.getString("warehouse.errMsgE004", this.language);
            //RequestContext.getCurrentInstance().execute("soundPlay();");
            PrimeFaces.current().executeScript("soundPlay();");
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 登録ボタンの有効性を更新する。
     */
    protected void updateButton() {
        switch (this.screenId) {
            case INSPECTION:
                this.registDisabled = StringUtils.isEmpty(this.userName)
                        || Objects.isNull(this.material)
                        || material.getInStockNum() <= 0;
                break;
            default:
                break;
        }
    }

    /**
     * 指定棚を取得する。
     * 
     * @return 指定棚
     */
    public String getReqLocationNo() {
        return this.reqLocationNo;
    }

    /**
     * 指定棚を設定する。
     * 
     * @param reqLocationNo 指定棚
     */
    public void setReqLocationNo(String reqLocationNo) {
        this.reqLocationNo = reqLocationNo;
    }

    /**
     * 棚番号を取得する。
     *
     * @return 棚番号
     */
    public String getLocationNo() {
        return this.locationNo;
    }

    /**
     * 棚番号を設定する。
     *
     * @param locationNo 棚番号
     */
    public void setLocationNo(String locationNo) {
        this.registMessage = "";
        this.locationNo = "";
        this.confirmLocationNo = "";

        //RequestContext context = RequestContext.getCurrentInstance();

        if (StringUtils.isEmpty(locationNo)) {
            this.updateButton();

            // 「棚番号が存在しません。」
            //this.registMessage = LocaleUtils.getString("warehouse.errMsgE006", this.language);
            //context.execute("soundPlay();");
            //context.execute("focusLocationNoInput();");
            PrimeFaces.current().executeScript("soundPlay();");
            PrimeFaces.current().executeScript("focusLocationNoInput();");
            return;
        }
       
        // 棚マスタ照会
        MstLocation location = this.warehouseModel.findLocation(this.areaName, locationNo);
        if (Objects.isNull(location)) {
 
            // 「棚番号が存在しません。」
            this.registMessage = LocaleUtils.getString("warehouse.errMsgE006", this.language);
            //context.execute("soundPlay();");
            //context.execute("focusLocationNoInput();");
            PrimeFaces.current().executeScript("soundPlay();");
            PrimeFaces.current().executeScript("focusLocationNoInput();");
        } else {
            this.locationNo = locationNo;

            if (!StringUtils.isEmpty(this.reqLocationNo) && !StringUtils.equals(locationNo, this.reqLocationNo)) {
                // 「指定された棚ではありません。」
                //context.execute("customAlert('" + LocaleUtils.getString("warehouse.warnMsgW001", this.language) + "','" + FOCUS_LOCATION_CONFIRM + "');");
                //context.execute("soundPlay();");
                PrimeFaces.current().executeScript("customAlert('" + LocaleUtils.getString("warehouse.warnMsgW001", this.language) + "','" + FOCUS_LOCATION_CONFIRM + "');");
                PrimeFaces.current().executeScript("soundPlay();");
            }
        }

        this.updateButton();
    }

    /**
     * 棚番号(確認用)を取得する。
     * 
     * @return 棚番号(確認用)
     */
    public String getConfirmLocationNo() {
        return confirmLocationNo;
    }

    /**
     * 棚番号(確認用)を設定する。
     * 
     * @param confirmLocationNo 棚番号(確認用)
     */
    public void setConfirmLocationNo(String confirmLocationNo) {
        this.confirmLocationNo = confirmLocationNo;
        this.updateButton();
    }

}
