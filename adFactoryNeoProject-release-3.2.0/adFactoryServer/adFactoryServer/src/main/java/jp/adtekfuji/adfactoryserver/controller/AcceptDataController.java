/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import adtekfuji.utility.StringUtils;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.adtekfuji.adFactory.entity.warehouse.Location;
import jp.adtekfuji.adFactory.enumerate.WarehouseEvent;
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
import org.primefaces.event.SelectEvent;

/**
 * 受入・入庫/資材移動/ラベル印刷 データコントローラ
 *
 * @author 14-0282
 */
@Named(value = "acceptData")
@SessionScoped
public class AcceptDataController extends BackingBeen {

    /**
     * 画面ID
     */
    enum Screen {
        ACCEPT,     // 受入画面
        ENTRY,      // 入庫画面
        INVENTORY,  // 棚卸画面
        SHEIFMOVE,  // 資材移動画面
        REPRINT;    // ラベル印刷画面
    }
    
    @EJB
    private WarehouseModel warehouseModel;

    /**
     * フォーカス位置
     */
    private static final String FOCUS_USER_ID = "userIdInput";          // 社員番号フィールド
    private static final String FOCUS_BARCODE = "BarcodeInput";         // バーコード情報フィールド
    private static final String FOCUS_LOCATION = "LocationNoInput";     // 棚番号フィールド
    private static final String FOCUS_RECEIVEDNUM = "ReceivedNumInput"; // 入庫数フィールド
    private static final String FOCUS_SUPPLYNO = "supplyNoInput";       // 納品書フィールド
    private static final String FOCUS_MOVENUM = "moveNumInput";         // 移動数フィールド
    private static final String FOCUS_SUBMIT = "submit";
    private static final String FOCUS_INVENTORYNUM = "iventoryNum";
    private static final String FOCUS_INVENTORYLOC = "iventoryLoc";
    private static final String FOCUS_SORTNUM = "sortNumInput";         // 仕分数フィールド
    
    /**
     * バーコード読み込み設定
     */
    private String[] patterns = new String[4];          // バーコードのパターン(正規表現)
    private Integer gsOrderPos;                         // GSオーダー開始桁数
    private Integer gsOrderLength;                      // GSオーダー桁数

    /**
     * 社員情報
     */
    private String userId;                              // 社員番号
    private String userName;                            // 社員名
    private boolean renderedUserErrMsg;                 // 社員番号エラーメッセージ表示フラグ

    private String areaName;                            // 現在区画名

    /**
     * 納品書情報
     */
    private String supplyNo;                            // 納品番号
    private boolean renderedSupplyNo = false;           // 納品書フォームの表示性

    /**
     * 資材情報
     */
    private String barcode;                             // バーコード情報
    private boolean renderedReadInf;                    // 読み込み情報表示フラグ
    private Map<String, String> materialItems;          // 表示アイテム情報
    private ArrayList<ItemData> additionData;           // 資材情報詳細
    private String planedNum;                           // 納入予定数
    private String stockNum;                            // 在庫数
    private String attentionMessage;                    // 重要度メッセージ
    private boolean renderedAttentionMessage;           // 重要度メッセージ表示フラグ
    private String itemInfoAreaStyle;                   // 資材情報フレーム スタイル情報
    private TrnMaterial trnMaterial = null;             // 資材情報
    private MstProduct mstProduct = null;               // 部品マスタ

    /**
     * ラベル印刷
     */
    private boolean renderedLabelInf;                   // ラベル入力フォームの表示性 2022-03-19 s-heya
    private String productNo;                           // 品番                 2022-03-19 s-heya
    private String productName;                         // 品名                 2022-03-19 s-heya
    private Integer orderNum;                           // 発注数               2022-03-19 s-heya
    private Integer sortNum;                            // 仕分数               2022-03-19 s-heya
    private Integer sortNumWater;                       // 仕分数(透かし)        2022-03-19 s-heya
    private String sortNumStyle;                        // 仕分数の表示スタイル   2022-03-19 s-heya
    private boolean disableBarcode;

    /**
     * 入庫情報
     */
    private String registrationAreaStyle;               // 入庫情報フレーム　スタイル情報
    private Integer receivedNum;                        // 入庫数
    private Integer receivedNumWater;                   // 入庫数(透かし)
    private boolean renderedReceivedNum;                // 入庫数フィールドの表示性
    private String receivedNumStyle;
    private String reqLoc;                              // 指定棚
    private String locationNo;                          // 棚番号
    private String locationNoWater;                     // 棚番号(透かし)
    private String confirmLocationNo;
    
    /**
     * 資材移動情報
     */
    private Integer moveNum;                            // 移動数
    private Integer moveNumWater;                       // 移動数(透かし)
    private String moveNumStyle;                        // 移動数の表示スタイル
    private String infMsg;                              // バーコード読み込み情報
    private boolean renderedInfMsg;                     // バーコード読み込み情報
    private boolean reqLocReadStatusFlg;
    private boolean buttonDisable;                      // ボタン活性・非活性フラグ

    private Integer inventoryNum;                       // 棚卸在庫数
    private String inventoryNumStyle;                   // 棚卸在庫数の表示スタイル
    private String inventoryLoc;                        // 棚番訂正
    private String inventoryLocStyle;                   // 棚番訂正の表示スタイル

    private String destArea;
    List<String> areaList;

    /**
     * 印刷ダイアログ
     */
    private boolean printFlg;
    private String printUrl;
    private String responseMsg;
    private boolean printReqResult;

    private final ServiceConfig config = ServiceConfig.getInstance();
    private final Logger logger = LogManager.getLogger();
    private boolean chkDialog;

    private Screen screenId;                        // 画面ID
    private String language;                        // 言語

    /**
     * コンストラクタ
     *
     */
    public AcceptDataController() {
        this.LoadConfig();
    }

    /**
     * ページを初期化する。
     */
    public void initPage() {
        // Cookieから区画名を取得
        String cookieAreaName = CookieUtils.getCookieValue("areaName");
        this.areaName = "";

        if (!StringUtils.isEmpty(cookieAreaName)) {
            this.areaName = cookieAreaName;
        }

        this.destArea = this.areaName;

        // Cookieから社員番号を取得
        String cookieUserId = CookieUtils.getCookieValue("userId");
        if (!StringUtils.isEmpty(cookieUserId)) {
            setUserId(cookieUserId);
        }

        String cookieKey = "printFlg";
        
        switch (this.screenId) {
            case ACCEPT:     // 受入画面
                break;
            case ENTRY:      // 入庫画面
                cookieKey = "entryPrintFlg";
                break;
            case INVENTORY:  // 棚卸画面
                cookieKey = "inventoryPrintFlg";
                break;
            case SHEIFMOVE:  // 資材移動画面
                cookieKey = "shelfMovePrintFlg";
                break;
        }

        // Cookieからラベル発行フラグを取得
        String cookiePrintFlg = CookieUtils.getCookieValue(cookieKey);
        if (!StringUtils.isEmpty(cookiePrintFlg)) {
            setPrintFlg(Boolean.valueOf(cookiePrintFlg));
        }
    }

    /**
     * 初期化処理(メニュー画面から遷移時)
     */
    public void init() {
        this.screenId = Screen.ACCEPT;
        if (this.config.isEnableStatement()) {
            // 納品書フォームを表示
            this.renderedSupplyNo = true;
        }
        this.initCommon();
    }

    /**
     * 初期化処理(メニュー画面から入庫に遷移時)
     */
    public void initEntry() {
        this.screenId = Screen.ENTRY;
        this.renderedReceivedNum = this.config.isEnableSemiProducts();
        this.renderedSupplyNo = false;
        this.initCommon();
    }

    /**
     * 初期化処理(メニュー画面からラベル発行に遷移時)
     */
    public void initReprint() {
        this.screenId = Screen.REPRINT;
        this.renderedSupplyNo = false;
        this.initCommon();

        if (this.disableBarcode) {
            // バーコード無しの場合、品目から入力する
            this.setBarcode(Constants.DUMMY_CODE);
        }
    }

    /**
     * 初期化処理(メニュー画面から棚移動に遷移時)
     */
    public void initShelf() {
        this.screenId = Screen.SHEIFMOVE;
        this.renderedSupplyNo = false;
        
        try {
            this.areaList = warehouseModel.getAreaNames();
        } catch(Exception ex){
            // エリアマスタ取得失敗
            this.areaList = null;
        }

        this.initCommon();
    }

    /**
     * 棚卸画面の初期化をおこなう。
     */
    public void initInventory() {
        this.screenId = Screen.INVENTORY;
        this.renderedSupplyNo = false;
        this.initCommon();
    }

    /**
     * 画面共通の初期化をおこなう。
     */
    private void initCommon() {
        try {
            
            boolean invalid = false;
            boolean inventory = warehouseModel.getAreaInventoryFlag(CookieUtils.getCookieValue("areaName"));
            switch (this.screenId)  {
                case ACCEPT:
                case REPRINT:
                    break;
                case INVENTORY:
                    invalid = !inventory;
                    break;
                default:
                    invalid = inventory;
                    break;
            }

            if (invalid) {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                String contextPath = facesContext.getExternalContext().getRequestContextPath();
                facesContext.getExternalContext().redirect(contextPath + "/warehouse/index.xhtml");	
            }

        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        // 言語
        this.language = CookieUtils.getCookieValue("locale");
        if (StringUtils.isEmpty(this.language)) {
            this.language = "ja";
        }

        this.initUserInf();
        this.initSupplyNo();
        this.initItemInf();
        this.initWarehouseInf();
        this.initPrintDialog();
        this.initPage();
    }

    /**
     * メニュー画面へ遷移時処理
     */
    public void backMenuScreen() {
        this.initSupplyNo();
        this.initItemInf();
        this.initWarehouseInf();
    }

    /**
     * 社員情報初期化
     */
    private void initUserInf() {
        this.userId = "";
        this.userName = "";
        this.renderedUserErrMsg = false;
    }

    /**
     * 納品書情報を初期化する。
     */
    private void initSupplyNo() {
        this.supplyNo = "";
    }
                
    /**
     * 資材情報初期化
     */
    private void initItemInf() {
        this.trnMaterial = null;
        this.mstProduct = null;
        this.barcode = "";
        this.itemInfoAreaStyle = Constants.NORMAL_STYLE;
        this.additionData = new ArrayList<>();
        this.receivedNum = 0;
        this.receivedNumWater = 0;
        this.receivedNumStyle = "color:black";
        this.moveNum = 0;
        this.moveNumWater = 0;
        this.moveNumStyle = "color:black";
        this.inventoryNum = null;
        this.inventoryNumStyle = "color:black";
        this.inventoryLoc = "";
        this.inventoryLocStyle = "color:black";
        this.planedNum = "";
        this.stockNum = "";
        this.attentionMessage = "";
        this.renderedAttentionMessage = false;
        this.renderedReadInf = false;
        this.renderedLabelInf = false;
        this.productNo = null;
        this.productName = null;
        this.orderNum = null;
        this.sortNum = null;
        this.sortNumWater = 0;
        this.sortNumStyle = "color:black";
    }

    /**
     * 入庫情報初期化
     */
    private void initWarehouseInf() {
        this.reqLoc = "";
        this.locationNo = "";
        this.confirmLocationNo = "";
        this.infMsg = "";
        this.locationNoWater = "";
        this.renderedInfMsg = false;
        this.buttonDisable = true;
        this.reqLocReadStatusFlg = false;
        this.registrationAreaStyle = "disp-none";
    }

    /**
     * 印刷ダイアログ：初期化
     */
    private void initPrintDialog() {
        this.infMsg = "";
        this.renderedInfMsg = false;
        this.responseMsg = "";
    }

    /**
     * 社員番号 取得
     *
     * @return 社員番号
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 社員番号 設定
     *
     * @param userId　社員番号
     */
    public void setUserId(String userId) {
        this.userId = "";
        this.renderedUserErrMsg = false;

        OrganizationEntity organizationEntity = warehouseModel.findOrganization(userId);
        if (Objects.nonNull(organizationEntity) && !StringUtils.isEmpty(organizationEntity.getOrganizationName())) {
            this.userId = userId;
            this.userName = organizationEntity.getOrganizationName();

            if (!this.renderedReadInf) {
                if (this.renderedSupplyNo) {
                    this.transitionFocus(FOCUS_SUPPLYNO);
                } else {
                    this.transitionFocus(FOCUS_BARCODE);
                }
            } else {
                switch (screenId) {
                    case ACCEPT:
                        this.transitionFocus(FOCUS_RECEIVEDNUM);
                        break;
                    case ENTRY:
                        this.transitionFocus(FOCUS_LOCATION);
                        break;
                    case SHEIFMOVE:
                        this.transitionFocus(FOCUS_BARCODE);
                        break;
                }
            }
        } else {
            this.userId = userId;
            this.userName = "";
            this.renderedUserErrMsg = true;
            PrimeFaces.current().executeScript("focusUserInfoForm();");
            PrimeFaces.current().executeScript("soundPlay();");
        }
        setButtonDisable();
    }

    /**
     * 社員名 取得
     *
     * @return 社員名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * バーコード情報 取得
     *
     * @return バーコード情報
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * バーコード情報 設定
     *
     * @param barcode　バーコード情報
     */
    public void setBarcode(String barcode) {
        logger.info("setBarcode: " + barcode);

        if (this.renderedSupplyNo) {
            if (!StringUtils.equals(this.supplyNo, barcode)) {
                this.barcode = "";
                // 「納品書と一致しません。」
                this.attentionMessage = LocaleUtils.getString("warehouse.errMsgE013", this.language);
                this.renderedAttentionMessage = true;
                setButtonDisable();
                PrimeFaces.current().executeScript("soundPlay();");
                PrimeFaces.current().executeScript("focusItemInfoForm();");
                return;
            }
            
            this.barcode = barcode;

        } else {
            initItemInf();
            initWarehouseInf();

            this.trnMaterial = findMaterial(barcode);
        }
        
        if (Objects.nonNull(this.trnMaterial)) {

            renderMaterial();
            this.registrationAreaStyle = "disp-RegistrationInf";
            this.renderedReadInf = true;
            setButtonDisable();

            switch (screenId) {
                case ACCEPT:
                    transitionFocus(FOCUS_RECEIVEDNUM);

                    // 欠品していた部品です。管理者に連絡をお願いします。
                    if ("HAMAI".equals(this.config.getWarehouseMode())) {
                        if (this.warehouseModel.updateReserveMaterial(this.trnMaterial)) {
                            PrimeFaces.current().executeScript("customAlert('" + LocaleUtils.getString("warehouse.infoMsgI004", this.language) + "');");
                            PrimeFaces.current().executeScript("soundPlay();");
                        }
                    }
                    break;

                case ENTRY:
                    if (Objects.isNull(this.trnMaterial.getInStockNum()) || 0 >= this.trnMaterial.getInStockNum()) {
                        this.attentionMessage = LocaleUtils.getString("warehouse.warnMsgW006", this.language); // 在庫がありません
                        this.renderedAttentionMessage = true;
                        setButtonDisable();
                        PrimeFaces.current().executeScript("soundPlay();");
                        PrimeFaces.current().executeScript("focusItemInfoForm();");
                        return;
                    }
                    
                    if (Objects.nonNull(this.trnMaterial.getProduct()) 
                            && Objects.equals(this.trnMaterial.getProduct().getImportantRank(), 2)
                            && Objects.isNull(this.trnMaterial.getInspectedAt())) {
                        // 受入検査が終わっていない
                        setButtonDisable();
                        PrimeFaces.current().executeScript("customAlert('" + LocaleUtils.getString("warehouse.insepectionWarning", this.language) + "','" + FOCUS_BARCODE +"')");
                        PrimeFaces.current().executeScript("soundPlay();");
                        return;
                    }

                    transitionFocus(FOCUS_LOCATION);
                    break;

                case INVENTORY:
                    if (Objects.nonNull(this.trnMaterial.getInventoryNum())) {
                        // 既に在庫を確認しています
                        PrimeFaces.current().executeScript("customAlert('" + LocaleUtils.getString("warehouse.warnMsgW007", this.language) + "','" + FOCUS_INVENTORYNUM + "');");
                        PrimeFaces.current().executeScript("soundPlay();");
                        return;
                    }
                    
                    this.transitionFocus(FOCUS_INVENTORYNUM);
                    break;

                case SHEIFMOVE:
                    if (Objects.nonNull(this.trnMaterial.getProduct())
                            && Objects.equals(this.trnMaterial.getProduct().getImportantRank(), 2) 
                            && Objects.isNull(this.trnMaterial.getInspectedAt())) {
                        // 受入検査が終わっていない
                        setButtonDisable();
                        PrimeFaces.current().executeScript("customAlert('" + LocaleUtils.getString("warehouse.insepectionWarning", this.language) + "','" + FOCUS_BARCODE +"')");
                        PrimeFaces.current().executeScript("soundPlay();");
                        return;
                    }

                    this.transitionFocus(FOCUS_MOVENUM);
                    break;

                case REPRINT:
                    this.transitionFocus(FOCUS_SORTNUM);
                    break;
            }

        } else {
            if (!Screen.REPRINT.equals(this.screenId)) {
                this.barcode = "";
                // 「該当する情報が存在しません。」
                this.attentionMessage = LocaleUtils.getString("warehouse.errMsgE003", this.language);
                this.renderedAttentionMessage = true;
                setButtonDisable();
                PrimeFaces.current().executeScript("soundPlay();");
                PrimeFaces.current().executeScript("focusItemInfoForm();");
                return;
            }

            this.trnMaterial = new TrnMaterial();
            this.mstProduct = new MstProduct();
            this.registrationAreaStyle = "disp-RegistrationInf";
            this.renderedLabelInf = true;
            setButtonDisable();
            PrimeFaces.current().executeScript("focusInput('registForm:productNoInput')");
        }
    }

    /**
     * 資材照会実施
     *
     * @param inputStr バーコード文字列
     */
    private TrnMaterial findMaterial(String inputStr) {
        TrnMaterial material = null;

        try {
            for (;;) {
                // QRラベル(又は資材番号直接入力)による資材照会
                if (inputStr.startsWith(TrnMaterial.SUPPLY_PREFIX) || inputStr.startsWith(TrnMaterial.ORDER_PREFIX)) {
                    this.barcode = inputStr;
                    material = warehouseModel.findMaterial(inputStr);
                    break;
                }

                // オペレーションNo:E1 支給品の現品票(QRコード)による資材照会
                // QRコード:<品目><品名><数量><倉庫オーダー>
                if (inputStr.matches(this.patterns[0])) {
                    Integer gsOrderStartingForSubstring = gsOrderPos - 1;
                    this.barcode = inputStr.substring(gsOrderStartingForSubstring, gsOrderStartingForSubstring + gsOrderLength);
                    material = this.getMaterialSupply(this.barcode, inputStr);
                    break;
                }
   
                // 後方一致で支給品かどうかを確認する。バーコードリーダーのエンコードがShift-JIS以外の場合、半角カナで桁数が変るため。
                if (inputStr.matches(".*\\d{6}GS\\d{7}\\d{7}.{63}$") && inputStr.length() > 140) {
                    this.barcode = inputStr.substring(inputStr.length() - 79).substring(0, gsOrderLength);
                    material = this.getMaterialSupply(this.barcode, inputStr);
                    break;
                }

                // オペレーションNo:E6 現品票(QRコード)
                // QRコード:<製造オーダー番号>␣<図番>␣<品目>␣<品名>␣<数量>␣<ロット番号>
                if (inputStr.matches(this.patterns[3])) {
                    String[] values = inputStr.split(Constants.SPACE, 0);
                    if (values.length == 6) {
                        this.barcode = values[5];
                        material = warehouseModel.findMaterialBySupplyNo(this.barcode);
                        break;
                    }
                }

                // オペレーションNo:E5 製造指示書(QRコード)による資材照会
                // QRコード:<製造番号>␣<製品No>␣<品目>␣<品名>␣<数量>
                if (inputStr.matches(this.patterns[2])) {
                    String[] values = inputStr.split(Constants.SPACE, 0);
                    if (values.length >= 5 && values[0].length() == 9) { // 製造指示書のQRコードの製造番号は9桁固定
                        // 製造番号と製品Noで一意の値となる
                        this.barcode = values[0] + Constants.KEY_SEPARATOR + values[1];
                        material = warehouseModel.findMaterialBySupplyNo(this.barcode);

                        if ((Screen.ACCEPT.equals(this.screenId) || Screen.ENTRY.equals(this.screenId)) && Objects.isNull(material)) {
                            StringBuilder _productName = new StringBuilder(); // 品名
                            for (int ii = 3; ii < (values.length - 1); ii++) {
                                _productName.append(values[ii]);
                                _productName.append(" ");
                            }
                            
                            int num = Integer.valueOf(values[values.length - 1]); // 手配数
                            
                            // 資材情報が登録されていない場合、QRコードの情報を使用
                            MstProduct product = warehouseModel.findProduct(values[2]);
                            if (Objects.nonNull(product)) {
                                if (_productName.length() > 0) {
                                    product.setProductName(_productName.toString().trim());
                                }
                            } else {
                                product = new MstProduct();
                                product.setProductNo(values[2]);
                                product.setProductName(_productName.toString().trim());
                            }

                            material = new TrnMaterial();
                            material.setMaterialNo(TrnMaterial.ORDER_PREFIX + this.barcode);
                            material.setSupplyNo(this.barcode);
                            material.setProduct(product);
                            material.setOrderNo(values[0]);
                            material.setSerialNo(values[1]);
                            material.setArrivalNum(num);
                            material.setStockNum(0);
                            material.setCategory((short) 3);
                        }
                        break;
                    }
                }

                // オペレーションNo:E3 購入品の現品票(QRコード)による資材照会
                // QRコード:<発注伝票No>␣<品目>␣<品名>␣<数量>
                if (inputStr.matches(this.patterns[1]) || inputStr.matches("^.+\\s.+\\s.\\d+$")) {
                    String[] values = inputStr.split(Constants.SPACE, 0);
                    if (values.length >= 3) {
                        this.barcode = values[0].toUpperCase();
                        
                        if (Constants.DUMMY_CODE.equals(values[0])) {
                            // adManagerの倉庫案内のインポートにて登録された在庫情報を照会する
                            String code = "$$" + this.getAreaName() + "-" + values[1];
                            logger.info("Find stock item: " + code);

                            material = warehouseModel.findMaterial(code);
                            if (Objects.nonNull(material)) {
                                break;
                            }
                        }
                        
                        material = warehouseModel.findMaterialBySupplyNo(this.barcode);

                        if ((Screen.ACCEPT.equals(this.screenId) || Screen.ENTRY.equals(this.screenId))&& Objects.isNull(material)) {
                            StringBuilder _productName = new StringBuilder(); // 品名
                            for (int ii = 2; ii < (values.length - 1); ii++) {
                                _productName.append(values[ii]);
                                _productName.append(" ");
                            }

                            int num = Integer.valueOf(values[values.length - 1]); // 手配数

                            // 資材情報が登録されていない場合、QRコードの情報を使用
                            MstProduct product = warehouseModel.findProduct(values[1]);
                            if (Objects.nonNull(product)) {
                                if (_productName.length() > 0) {
                                    product.setProductName(_productName.toString().trim());
                                }
                            } else {
                                product = new MstProduct();
                                product.setProductNo(values[1]);
                                product.setProductName(_productName.toString().trim());
                            }

                            material = new TrnMaterial();
                            material.setMaterialNo(TrnMaterial.SUPPLY_PREFIX + values[0]);
                            material.setSupplyNo(values[0]);
                            material.setProduct(product);
                            material.setArrivalNum(num);
                            material.setStockNum(0);
                            material.setCategory((short) 2);
                        }
                        break;
                    }
                }
                
                break;
            }

            if (StringUtils.isEmpty(this.barcode)) {
                // オペレーションNo:E2  購入品の現品票(納品書)による資材照会
                this.barcode = inputStr;
                material = warehouseModel.findMaterialBySupplyNo(barcode);
            }
        } catch (NumberFormatException ex) {
            //エラーログ出力
            logger.fatal(ex, ex);

            this.barcode = inputStr;
            return null;
        }
        return material;
    }

    /**
     * 資材情報(支給品)を取得する。
     * 
     * @param gsOrderNo 倉庫オーダー
     * @param qrcode QRコード(文字列)
     * @return 
     */
    private TrnMaterial getMaterialSupply(String gsOrderNo, String qrcode) {
        TrnMaterial _material = warehouseModel.findMaterialBySupplyNo(gsOrderNo);

        if (Objects.isNull(_material)) {
            String _productNo = qrcode.substring(0, 46).trim();
            String _productName = qrcode.substring(46, 77).trim();

            // 数量
            String str = qrcode.substring(77, 83).trim();
            Pattern p = Pattern.compile("^0+([0-9]+.*)");
            Matcher m = p.matcher(str);
            Integer _quantity = m.matches() ? Integer.parseInt(m.group(1)) : Integer.parseInt(str);

            logger.info("getMaterialSupply: _productNo={}, _productName={}, _quantity={}", _productNo, _productName, _quantity);
            
            MstProduct product = warehouseModel.findProduct(_productNo);
            if (Objects.nonNull(product)) {
                if (_productName.length() > 0) {
                    product.setProductName(_productName);
                }
            } else {
                product = new MstProduct();
                product.setProductNo(_productNo);
                product.setProductName(_productName);
            }

            _material = new TrnMaterial();
            _material.setMaterialNo(TrnMaterial.SUPPLY_PREFIX + gsOrderNo);
            _material.setSupplyNo(gsOrderNo);
            _material.setProduct(product);
            _material.setArrivalNum(_quantity);
            _material.setStockNum(0);
            _material.setCategory((short) 1);
        }
        
        return _material;
    }
    
    
    /**
     * 部品マスタ登録情報 取得
     */
    private void getMstProductInf() {
        // 指定棚
        if (Objects.nonNull(this.mstProduct.getLocationList())) {
            for (Location location : this.mstProduct.getLocationList()) {
                if (StringUtils.equals(this.areaName, location.getAreaName())) {
                    this.reqLoc = location.getLocationNo();
                    break;
                }
            }
        }

        // 重要度 (WarehouseBean にもコード有り)
        if (Objects.nonNull(this.mstProduct.getImportantRank())) {
            switch (this.mstProduct.getImportantRank()) {
                case 1: // 取り扱い注意品
                    this.itemInfoAreaStyle = Constants.IMPORTANT_STYLE;
                    this.attentionMessage = LocaleUtils.getString("warehouse.importantItem", this.language);
                    this.renderedAttentionMessage = true;
                    break;

                case 2: // 要検査品
                    if (Objects.isNull(this.trnMaterial.getInspectedAt())) {
                        // 要検査
                        this.itemInfoAreaStyle = Constants.INSPECTION_STYLE;
                        this.attentionMessage = LocaleUtils.getString("warehouse.insepectionRequired", this.language);
                    } else {
                        // 検査済み
                        this.itemInfoAreaStyle = Constants.IMPORTANT_STYLE;
                        this.attentionMessage = LocaleUtils.getString("warehouse.inspected", this.language);
                    }
                    this.renderedAttentionMessage = true;
                    break;

                case 0: // 通常
                default:
                    this.itemInfoAreaStyle = Constants.NORMAL_STYLE;
                    this.attentionMessage = "";
                    this.renderedAttentionMessage = false;
                    break;
             }
        }
    }

    /**
     * 資材情報を表示する。
     */
    private void renderMaterial() {
        this.mstProduct = this.trnMaterial.getProduct();
        if (Objects.nonNull(this.mstProduct)) {
            getMstProductInf();
        }

        Integer arrivalNumBuf = this.trnMaterial.getArrivalNum();
        Integer stockNumBuf = this.trnMaterial.getInStockNum();

        if (Objects.isNull(stockNumBuf)) {
            stockNumBuf = 0;
        }
        Integer receivedNumBuf = arrivalNumBuf - stockNumBuf;

        // 入庫数
        if (this.receivedNum.equals(0)) {

            if (receivedNumBuf > 0) {
                this.receivedNum = receivedNumBuf;
                this.receivedNumWater = receivedNumBuf;
            } else {
                this.receivedNum = 0;
                this.receivedNumWater = 0;
                this.receivedNumStyle = "color:red";
            }
        }
        
        // 納入予定数
        if (StringUtils.isEmpty(this.planedNum)) {
            this.planedNum = String.valueOf(arrivalNumBuf);
        }

        // 在庫数
        this.stockNum = String.valueOf(stockNumBuf);

        // 棚番号(保管棚)
        if (Objects.nonNull(this.trnMaterial.getLocation())) {
            if (StringUtils.equals(this.areaName, this.trnMaterial.getLocation().getAreaName())) {
                this.locationNoWater = this.trnMaterial.getLocation().getLocationNo();
            }
        }

        if (StringUtils.isEmpty(this.locationNoWater) && !Screen.REPRINT.equals(this.screenId)) {
            // 保管棚が未登録の場合、指定棚を表示
            this.locationNoWater = this.reqLoc;
        }

         // 資材情報詳細
        Map<String, String> properties = this.trnMaterial.getProperty();
        for (Map.Entry<String, String> entry : this.materialItems.entrySet()) {
            if (StringUtils.equals(entry.getKey(), Constants.PRODUCT_NO)) {
                this.addAdditonData(entry.getValue(), this.trnMaterial.getProduct().getProductNo());

            } else if (StringUtils.equals(entry.getKey(), Constants.PRODUCT_NAME)) {
                this.addAdditonData(entry.getValue(), this.trnMaterial.getProduct().getProductName());
                
            } else if (StringUtils.equals(entry.getKey(), Constants.FIGURE_NO)) {
                this.addAdditonData(entry.getValue(), this.trnMaterial.getProduct().getFigureNo());

            } else if (StringUtils.equals(entry.getKey(), Constants.ORDER_NO)) {
                this.addAdditonData(entry.getValue(), this.trnMaterial.getOrderNo());

            } else if (StringUtils.equals(entry.getKey(), Constants.LOT_NO)) {
                this.addAdditonData(entry.getValue(), this.trnMaterial.getPartsNo());

            } else if (Objects.nonNull(properties) && properties.containsKey(entry.getKey())) {
                String value = properties.get(entry.getKey());
                addAdditonData(entry.getValue(), value);
            }
        }

        switch (this.screenId) {
            case INVENTORY:
                // 棚卸在庫数
                if (Objects.nonNull(this.trnMaterial.getInventoryNum())) {
                    this.inventoryNum = this.trnMaterial.getInventoryNum();
                }

                // 棚番訂正
                if (Objects.nonNull(this.trnMaterial.getInventoryLocation())) {
                    this.inventoryLoc = this.trnMaterial.getInventoryLocation().getLocationNo();
                }
                break;
            case REPRINT:
                this.sortNum = null;
                this.sortNumWater = stockNumBuf;
                this.sortNumStyle = "color:black";
                break;
            case SHEIFMOVE:
                // 資材移動画面
                this.moveNum = stockNumBuf;
                this.moveNumWater = stockNumBuf;
                this.moveNumStyle = "color:black";
                break;
            default:
                break;
        }
    }

    /**
     * 資材情報詳細 データ追加
     *
     */
    private void addAdditonData(String name, String value) {
        ItemData data = new ItemData(name, value);
        this.additionData.add(data);
    }

    /**
     * 読取情報表示フラグ 取得
     *
     * @return 読取情報表示フラグ
     */
    public boolean isRenderedReadInf() {
        return renderedReadInf;
    }

    /**
     * 資材情報詳細 取得
     *
     * @return 資材情報詳細
     */
    public ArrayList<ItemData> getAdditionData() {
        return additionData;
    }

    /**
     * 重要度フレームスタイル 取得
     *
     * @return
     */
    public String getItemInfoAreaStyle() {
        return itemInfoAreaStyle;
    }

    /**
     * 重要度ラベル 取得
     *
     * @return 重要度ラベル
     */
    public String getAttentionMessage() {
        return attentionMessage;
    }

    /**
     * 納入予定数 取得
     *
     * @return 納入予定数
     */
    public String getPlanedNum() {
        return planedNum + " " + this.getUnit();
    }

    /**
     * 入庫情報表示スタイル 取得
     *
     * @return 入庫情報表示スタイル
     */
    public String getRegistrationAreaStyle() {
        return registrationAreaStyle;
    }

    /**
     * 入庫数 取得
     *
     * @return 入庫数
     */
    public Integer getReceivedNum() {
        return receivedNum;
    }
    
    /**
     * 入庫数フィールドの透かしを取得する。
     * 
     * @return 入庫数フィールドの透かし
     */
    public String getReceivedNumWater() {
        return this.receivedNumWater + " " + this.getUnit();
    }
    
    /**
     * 入庫数 設定
     *
     * @param receivedNum　入庫数
     */
    public void setReceivedNum(Integer receivedNum) {
        if (Objects.isNull(receivedNum)) {
            receivedNum = 0;
        }

        this.receivedNum = receivedNum;
        
        setButtonDisable();
        if (this.buttonDisable) {
            return;
        }

        if (!(StringUtils.isEmpty(this.planedNum))
                && !(StringUtils.isEmpty(this.stockNum))) {

            Integer receivedNumBuf = Integer.valueOf(this.planedNum) - Integer.valueOf(this.stockNum);

            // 納入予定数と入庫数を比較
            if (!Objects.equals(receivedNum, receivedNumBuf)) {
                //String language = CookieUtils.getCookieValue("locale");
                
                PrimeFaces.current().executeScript("confirmReciveNum('" + this.planedNum + "','" + this.receivedNum + "','" + language + "');");
                PrimeFaces.current().executeScript("soundPlay();");
            } else {
                this.receivedNumStyle = "color:black";
                switch (this.screenId)  {
                    case ACCEPT:
                        this.transitionFocus(FOCUS_SUBMIT);
                        break;
                    case SHEIFMOVE:
                        this.transitionFocus(FOCUS_LOCATION);
                        break;
                    default:
                        break;
                }            
            }
        }
    }

    /**
     * 入庫数フィールドの表示性を取得する。
     * 
     * @return 入庫数フィールドの表示性
     */
    public boolean getRenderedReceivedNum() {
        return this.renderedReceivedNum;
    }

    /**
     * 入庫数文字スタイル 取得
     *
     * @return
     */
    public String getReceivedNumStyle() {
        return receivedNumStyle;
    }

    /**
     * 入庫数文字スタイル 設定
     *
     * @param receivedNumStyle
     */
    public void setReceivedNumStyle(String receivedNumStyle) {
        this.receivedNumStyle = receivedNumStyle;
    }

    /**
     * 指定棚 取得
     *
     * @return 指定棚
     */
    public String getReqLoc() {
        return reqLoc;
    }

    /**
     * 棚番号を設定する。
     *
     * @param locationNo 棚番号
     */
    public void setLocationNo(String locationNo) {
        this.reqLocReadStatusFlg = false;
        this.infMsg = "";
        this.renderedInfMsg = false;
        this.confirmLocationNo = "";

        if (StringUtils.isEmpty(locationNo)) {
            this.locationNo = "";
            // 「棚番号が存在しません。」
            this.infMsg = LocaleUtils.getString("warehouse.errMsgE006", this.language);
            this.renderedInfMsg = true;
            setButtonDisable();
            PrimeFaces.current().executeScript("soundPlay();");
            PrimeFaces.current().executeScript("focusLocationNoInput();");
            return;
        }

        String _areaName = Screen.SHEIFMOVE.equals(this.screenId) ? this.destArea : this.areaName;
        this.locationNo = locationNo;
        
        // 棚マスタ照会
        MstLocation location = warehouseModel.findLocation(_areaName, locationNo);
        if (Objects.isNull(location)) {
            this.locationNo = "";
            // 「棚番号が存在しません。」
            this.infMsg = LocaleUtils.getString("warehouse.errMsgE006", this.language);
            this.renderedInfMsg = true;
            PrimeFaces.current().executeScript("soundPlay();");
            PrimeFaces.current().executeScript("focusLocationNoInput();");
        } else {
            this.reqLocReadStatusFlg = true;
            if (!StringUtils.isEmpty(this.reqLoc) && !StringUtils.equals(locationNo, this.reqLoc)) {
                // 「指定された棚ではありません。」
                PrimeFaces.current().executeScript("customAlert('" + LocaleUtils.getString("warehouse.warnMsgW001", this.language) + "','" + FOCUS_LOCATION + "');");
                PrimeFaces.current().executeScript("soundPlay();");
            }
        }

        this.setButtonDisable();

        if (!this.buttonDisable) {
            PrimeFaces.current().executeScript("focusCconfirmLocationNoInput();");
        }
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
     * 棚番号(透かし)を取得する。
     *
     * @return 棚番号(透かし)
     */
    public String getLocationNoWater() {
        return this.locationNoWater;
    }

    /**
     * 棚番号(確認用)を設定する。
     * 
     * @param confirmLocationNo 棚番号(確認用)
     */
    public void setConfirmLocationNo(String confirmLocationNo) {
        this.confirmLocationNo = confirmLocationNo;

        if (StringUtils.equals(this.locationNo, confirmLocationNo)) {
            PrimeFaces.current().executeScript("clickSubmitButton();");
            
        } else {
            // 「棚番号が一致しません。」
            this.infMsg = LocaleUtils.getString("warehouse.wrongLocationNo", this.language);
            this.renderedInfMsg = true;
            setButtonDisable();

            PrimeFaces.current().executeScript("soundPlay();focusLocationNoInput();");
            
            this.locationNo = "";
            this.confirmLocationNo = "";
        }
    }

    /**
     * 棚番号(確認用)を取得する。
     * 
     * @return 棚番号(確認用)
     */
    public String getConfirmLocationNo() {
        return this.confirmLocationNo;
    }

    /**
     * 棚番号(透かし)を設定する。
     *
     * @param locationNoWater 棚番号(透かし)
     */
    public void setLocationNoWater(String locationNoWater) {
        this.locationNoWater = locationNoWater;
    }

    /**
     * メッセージ情報 取得
     *
     * @return メッセージ情報
     */
    public String getInfMsg() {
        return infMsg;
    }

    /**
     * 在庫数 取得
     *
     * @return 在庫数
     */
    public String getStockNum() {
        return stockNum + " " + this.getUnit();
    }

    public boolean isPrintFlg() {
        return printFlg;
    }

    public void setPrintFlg(boolean printFlg) {
        this.printFlg = printFlg;
    }

    /**
     * 登録・ラベル印刷ボタン活性・非活性フラグ 取得
     *
     * @return ボタン活性・非活性フラグ
     */
    public boolean isButtonDisable() {
        return buttonDisable;
    }

    /**
     * 登録・ラベル印刷ボタン活性・非活性フラグ 設定
     *
     */
    private void setButtonDisable() {
        switch (this.screenId) {
            case ACCEPT:
                // ユーザー検索・バーコード入力・棚マスタ確認あり
                this.buttonDisable = StringUtils.isEmpty(this.userName)
                        || Objects.equals(this.renderedReadInf, false)
                        || Constants.DUMMY_CODE.equals(this.barcode);
                break;

            case ENTRY:
                if (Objects.nonNull(this.trnMaterial)) {
                    // 受入検査が終わっていない
                    if (Objects.equals(this.mstProduct.getImportantRank(), 2)
                            && Objects.isNull(this.trnMaterial.getInspectedAt())) {
                        this.buttonDisable = true;
                        break;
                    }
                }
                                    
                // ユーザー検索・バーコード入力・棚マスタ確認あり
                this.buttonDisable = StringUtils.isEmpty(this.userName)
                        || Objects.equals(this.renderedReadInf, false)
                        || Objects.equals(this.reqLocReadStatusFlg, false)
                        || Constants.DUMMY_CODE.equals(this.barcode);
                break;
    
            case SHEIFMOVE:
                if (Objects.nonNull(this.trnMaterial)) {
                    // 受入検査が終わっていない
                    if (Objects.equals(this.mstProduct.getImportantRank(), 2)
                            && Objects.isNull(this.trnMaterial.getInspectedAt())) {
                        this.buttonDisable = true;
                        break;
                    }
                }

                this.buttonDisable = StringUtils.isEmpty(this.userName)
                        || Objects.isNull(this.trnMaterial)
                        || Objects.equals(this.renderedReadInf, false)
                        || this.moveNum < 1 || this.moveNum > this.trnMaterial.getInStockNum()
                        || this.areaName.equals(this.destArea) ? !this.reqLocReadStatusFlg : false;
                break;

            case INVENTORY:
                this.buttonDisable = StringUtils.isEmpty(this.userName)
                        || Objects.equals(this.renderedReadInf, false)
                        || Objects.isNull(this.inventoryNum);
                break;

            case REPRINT:
                if (this.renderedLabelInf) {
                    this.buttonDisable = StringUtils.isEmpty(this.userName)
                            || Objects.isNull(this.trnMaterial)
                            || Objects.isNull(this.mstProduct)
                            || Objects.isNull(this.orderNum);
                } else {
                    this.buttonDisable = StringUtils.isEmpty(this.userName)
                            || Objects.isNull(this.trnMaterial)
                            || (Objects.nonNull(this.sortNum) && (this.sortNum < 1 || this.sortNum > this.trnMaterial.getInStockNum()));
                }
                break;

            default:
                this.buttonDisable = StringUtils.isEmpty(this.userName)
                        || Objects.equals(this.renderedReadInf, false);
                break;
        }
    }

    /**
     * 社員情報エラーメッセージ表示フラグ 取得
     *
     * @return 社員情報エラーメッセージ表示フラグ
     */
    public boolean isRenderedUserErrMsg() {
        return renderedUserErrMsg;
    }

    /**
     * 重要度メッセージ表示フラグ 取得
     *
     * @return 重要度メッセージ表示フラグ
     */
    public boolean isRenderedAttentionMessage() {
        return renderedAttentionMessage;
    }

    /**
     * バーコード読取メッセージ表示フラグ 取得
     *
     * @return バーコード読取メッセージ表示フラグ
     */
    public boolean isRenderedInfMsg() {
        return renderedInfMsg;
    }

    /**
     * フォーカス遷移
     */
    private void transitionFocus(String transitionDestination) {

        if (StringUtils.isEmpty(this.userName)) {
            PrimeFaces.current().executeScript("focusUserInfoForm();");
            return;
        }

        switch (transitionDestination) {

            case FOCUS_USER_ID:
                PrimeFaces.current().executeScript("focusUserInfoForm();");
                break;

            case FOCUS_BARCODE:
                if (this.renderedSupplyNo && StringUtils.isEmpty(this.supplyNo)) {
                    PrimeFaces.current().executeScript("focusSupplyNoForm();");
                } else {
                    PrimeFaces.current().executeScript("focusItemInfoForm();");
                }
                break;

            case FOCUS_RECEIVEDNUM:
                if (StringUtils.isEmpty(this.barcode)) {
                    // 資材番号照会未実施の場合：バーコードにフォーカス遷移
                    PrimeFaces.current().executeScript("focusItemInfoForm();");
                } else {
                    PrimeFaces.current().executeScript("focusReceivedNumInput();");
                }
                break;
                
            case FOCUS_MOVENUM:
                if (StringUtils.isEmpty(this.barcode)) {
                    PrimeFaces.current().executeScript("focusItemInfoForm();");
                } else {
                    PrimeFaces.current().executeScript("focusMoveNumInput();");
                }
                break;

            case FOCUS_LOCATION:
                if (StringUtils.isEmpty(this.barcode)) {
                    // 資材番号照会未実施の場合：バーコードにフォーカス遷移
                    PrimeFaces.current().executeScript("focusItemInfoForm();");
                } else {
                    PrimeFaces.current().executeScript("focusLocationNoInput();");
                }
                break;

            case FOCUS_SUPPLYNO:
                PrimeFaces.current().executeScript("focusSupplyNoForm();");
                break;
            case FOCUS_SUBMIT:
                PrimeFaces.current().executeScript("focusSubmitButton();");
                break;
            case FOCUS_INVENTORYNUM:
                PrimeFaces.current().executeScript("focusInventoryNumInput();");
                break;
            case FOCUS_INVENTORYLOC:
                PrimeFaces.current().executeScript("focusInventoryLocInput();");
                break;
            case FOCUS_SORTNUM:
                PrimeFaces.current().executeScript("focusInput('registForm:sortNumInput');");
                break;

            default:
                break;
        }

    }

    /**
     * 受入・入庫 印刷・登録
     */
    public void registWarehouseInfo() {
        if (Objects.equals(printFlg, true)) {
            // ラベル印刷ダイアログを表示
            this.openPrintDialog();
        } else {
            this.registWarehouseData();
        }
    }

    /**
     * 受入・入庫 登録処理
     */
    public void registWarehouseData() {
        logger.info("registWarehouseData start.");
        Response ret;
        try {
            // 資材情報 又は、部品マスタが未登録の場合、それらを登録してから受入する
            WarehouseEvent event;
            if (Screen.ENTRY.equals(this.screenId)) {
                // 入庫画面
                event = WarehouseEvent.ENTRY;
                ret = warehouseModel.reciveWarehouseWithRegist(event, userId, trnMaterial, mstProduct, 0, areaName, locationNo);
            } else {
                // 受入画面
                event = WarehouseEvent.RECIVE;
                ret = warehouseModel.reciveWarehouseWithRegist(event, userId, trnMaterial, mstProduct, receivedNum, areaName, locationNo);
            }

            if (ret.getStatus() == HttpURLConnection.HTTP_OK) {
                // 登録成功
                this.clearForm();
            } else {
                // 登録失敗
                this.setErrorMessage();
            }
        } catch (Exception ex) {
            // 登録失敗
            this.setErrorMessage();
        }
    }

    /**
     * 資材移動 印刷・登録
     */
    public void registMoveWarehouse() {
        try {
            if (Objects.equals(printFlg, true)) {
                boolean allot = !Objects.equals(this.moveNum, trnMaterial.getInStockNum());
                if (allot) {
                    this.stockNum = String.valueOf(this.moveNum); // 在庫数
                }

                // ラベル印刷ダイアログを表示
                this.openPrintDialog();

            } else {
                this.registMoveData();
                this.clearForm();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 資材移動 登録処理
     * @return 処理結果 (true: 成功、false: 失敗)
     */
    public boolean registMoveData() {
        logger.info("registMoveData start.");
        Response ret;
        try {
            boolean isCreate = !Objects.equals(this.moveNum, trnMaterial.getInStockNum()) || !StringUtils.equals(this.areaName, destArea);
            String _locationNo = StringUtils.isEmpty(locationNo) ? "UNKNOWN" : this.locationNo;
            
            if (isCreate) {
                ret = warehouseModel.moveWarehouse(userId, trnMaterial.getMaterialNo(), moveNum, destArea, _locationNo);
            } else {
                ret = warehouseModel.moveWarehouse(userId, trnMaterial.getMaterialNo(), destArea, _locationNo);
            }

            if (ret.getStatus() == HttpURLConnection.HTTP_OK) {
                // 登録成功
                if (isCreate) {
                    this.trnMaterial = (TrnMaterial) ret.getEntity(); // 移動された資材情報
                    this.stockNum = String.valueOf(this.trnMaterial.getInStockNum()); // 在庫数
                }
                return true;

            } else {
                // 登録失敗
                this.setErrorMessage();
            }
        } catch (Exception ex) {
            // 登録失敗
            this.setErrorMessage();
        }
        
        return false;
    }

    /**
     * 棚卸 印刷・登録
     */
    public void registInventory() {
        if (Objects.equals(printFlg, true)) {
            MstLocation location = this.warehouseModel.findLocation(this.areaName, this.inventoryLoc);

            this.trnMaterial.setInventoryPersonNo(this.userId);     // 棚卸実施者
            this.trnMaterial.setInventoryNum(this.inventoryNum);    // 棚卸在庫数
            this.trnMaterial.setInventoryLocation(location);        // 棚番訂正

            // ラベル印刷ダイアログを表示
            this.openPrintDialog();
        } else {
            this.registInventoryData();
        }
    }

    /**
     * 棚卸 登録処理
     */
    public void registInventoryData() {
        logger.info("registInventoryData start.");
        Response ret = null;
        try {
            // 資材情報の棚卸情報を更新する。
            ret = warehouseModel.registInventory(userId, trnMaterial, mstProduct, inventoryNum, areaName, inventoryLoc);

            if (ret.getStatus() == HttpURLConnection.HTTP_OK) {
                // 登録成功
                this.clearForm();
            } else {
                // 登録失敗
                this.setErrorMessage();
            }
        } catch (Exception ex) {
            // 登録失敗
            this.setErrorMessage();
        }
    }

    /**
     * 表示をクリアする。
     */
    private void clearForm() {
        try {
            this.initSupplyNo();
            this.initItemInf();
            this.initWarehouseInf();
            this.initPrintDialog();
            this.renderedReadInf = false;
            this.registrationAreaStyle = "disp-none";
            this.transitionFocus(FOCUS_BARCODE);
            this.setButtonDisable();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * エラーメッセージを表示する。
     */
    private void setErrorMessage() {
        try {
            this.transitionFocus(FOCUS_BARCODE);
            this.infMsg = LocaleUtils.getString("warehouse.errMsgE004", this.language);
            this.renderedInfMsg = true;
            PrimeFaces.current().executeScript("soundPlay();");
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 設定情報を読み込む。
     */
    private void LoadConfig() {
        try {
            this.patterns[0] = this.config.getPatternE1();
            this.patterns[1] = this.config.getPatternE3();
            this.patterns[2] = this.config.getPatternE5();
            this.patterns[3] = this.config.getPatternE6();
            this.gsOrderPos = this.config.getGsOrderPos();
            this.gsOrderLength = this.config.getGsOrderLength();
        
            String values = this.config.getMaterialItems();
            this.materialItems = JsonUtils.jsonToMap(values);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.materialItems = new HashMap<>();
        }
    }

    /**
     * 印刷ダイアログ：印刷実行ログ文字列 取得
     *
     * @return
     */
    public String getResponseMsg() {
        return responseMsg;    //response
    }

    /**
     * 印刷ダイアログ：印刷実行ログ文字列 設定
     *
     * @param responseMsg
     */
    public void setResponseMsg(String responseMsg) {
        logger.info("Response:" + responseMsg);
        this.responseMsg = responseMsg;
    }

    /**
     * 印刷ダイアログ：印刷要求送信結果 取得
     *
     * @return
     */
    public boolean isPrintReqResult() {
        return printReqResult;
    }

    /**
     * 印刷ダイアログ：印刷要求送信結果 設定
     *
     * @param printReqResult
     */
    public void setPrintReqResult(boolean printReqResult) {
        this.printReqResult = printReqResult;
    }

    /**
     * 印刷ダイアログ：印刷実行要求URL文字列 取得
     *
     * @return
     */
    public String getPrintUrl() {
        return printUrl;
    }
    
    /**
     * 印刷ダイアログ：印刷実行要求URL文字列 設定
     */
    private void setPrintUrl() {

        this.printUrl = "";

        if (Objects.isNull(this.trnMaterial)) {
            this.trnMaterial = warehouseModel.findMaterialBySupplyNo(this.barcode);
        }

        try {
            String spec = "";
            String note = "";
            if (Objects.nonNull(this.trnMaterial.getProperty())) {
                // 型式・仕様
                if (this.trnMaterial.getProperty().containsKey(Constants.SPEC)) {
                    spec = StringUtils.isEmpty(this.trnMaterial.getProperty().get(Constants.SPEC)) ? "" : URLEncoder.encode(this.trnMaterial.getProperty().get(Constants.SPEC));
                }

                // 備考
                if (this.trnMaterial.getProperty().containsKey(Constants.NOTE)) {
                    note = StringUtils.isEmpty(this.trnMaterial.getProperty().get(Constants.NOTE)) ? "" : URLEncoder.encode(this.trnMaterial.getProperty().get(Constants.NOTE), Constants.UTF_8);
                }
            }
            
            // 在庫数
            int stock = !StringUtils.isEmpty(stockNum) ? Integer.parseInt(stockNum) : 0;

             // 区画名、棚番号
            String area = "";
            String location = "";
            switch (this.screenId) {
                case REPRINT:
                    if (Objects.nonNull(trnMaterial.getLocation())) {
                        area = URLEncoder.encode(trnMaterial.getLocation().getAreaName(), Constants.UTF_8);
                        location = URLEncoder.encode(trnMaterial.getLocation().getLocationNo(), Constants.UTF_8);
                    }
                    if (this.renderedLabelInf) {
                        stock = Objects.nonNull(orderNum) ? orderNum : stock;
                    } else {
                        stock = Objects.nonNull(sortNum) ? sortNum : stock;
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
                    if (Objects.nonNull(trnMaterial.getInventoryNum())) {
                        stock = trnMaterial.getInventoryNum();
                    } else {
                        stock = trnMaterial.getStockNum();
                    }
                    break;
                case ACCEPT:
                    // 受入の場合、表示在庫数に入庫数を加算
                    stock = receivedNum + stock;
                    area = URLEncoder.encode(this.areaName, Constants.UTF_8);
                    String receivingLocation = LocaleUtils.getString("warehouse.ReceivingLocation", this.language);
                    
                    if (Objects.nonNull(trnMaterial.getLocation())
                            && !StringUtils.equals(receivingLocation, trnMaterial.getLocation().getLocationNo())) {
                        // 入庫済みの場合、入庫先の棚番号
                        location = URLEncoder.encode(trnMaterial.getLocation().getLocationNo(), Constants.UTF_8);
                    } else {
                        // 指定された棚番号
                        Optional<Location> locationOpt = mstProduct.getLocationList().stream()
                                .filter(o -> StringUtils.equals(this.areaName, o.getAreaName()))
                                .findFirst();
                        if (locationOpt.isPresent()) {
                            location = URLEncoder.encode(locationOpt.get().getLocationNo(), Constants.UTF_8);
                        }
                    }
                    break;
                default:
                    area = URLEncoder.encode(this.areaName, Constants.UTF_8);
                    location = URLEncoder.encode(this.locationNo, Constants.UTF_8);
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
                sb.append(StringUtils.isEmpty(trnMaterial.getOrderNo()) ? "" : URLEncoder.encode(trnMaterial.getOrderNo(), Constants.UTF_8));
                sb.append("&note=");
                sb.append(note);
            } else {
                // 棚卸ラベル
                sb.append("&__format_id_number=");
                sb.append(this.config.getInventoryLabelFormat());
            }

            
            sb.append("&material=");
            
            //if (!Screen.REPRINT.equals(this.screenId)) {
                sb.append(URLEncoder.encode(trnMaterial.getSupplyNo(), Constants.UTF_8));
            //} else {
            //    String barcode = Objects.nonNull(sortNum) ? String.format("%s QTY=%d", trnMaterial.getSupplyNo(), sortNum) : trnMaterial.getSupplyNo();
            //    sb.append(URLEncoder.encode(barcode, Constants.UTF_8));
            //}
            
            sb.append("&product=");
            sb.append(URLEncoder.encode(mstProduct.getProductNo(), Constants.UTF_8));
            sb.append("&name=");
            sb.append(StringUtils.isEmpty(mstProduct.getProductName()) ? "" : URLEncoder.encode(mstProduct.getProductName(), Constants.UTF_8));
            sb.append("&spec=");
            sb.append(spec);
            sb.append("&lotNo=");
            sb.append(StringUtils.isEmpty(trnMaterial.getPartsNo()) ? "" : URLEncoder.encode(trnMaterial.getPartsNo(), Constants.UTF_8));
            sb.append("&area=");
            sb.append(area);
            sb.append("&location=");
            sb.append(location);
            sb.append("&stock=");
            sb.append(stock);
            sb.append("&person=");
            sb.append(URLEncoder.encode(this.userId, Constants.UTF_8));
            sb.append("&date=");
            sb.append(URLEncoder.encode(new SimpleDateFormat("yy/MM/dd").format(date), Constants.UTF_8));
            sb.append("&time=");
            sb.append(URLEncoder.encode(new SimpleDateFormat("HH:mm").format(date), Constants.UTF_8));
           
            printUrl = sb.toString();

        } catch (UnsupportedEncodingException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 印刷ダイアログ：BaseUrl取得
     */
    private String getBaseUrl() {
        String baseUrl;

        String URLScheme = config.getPrintUrlScheme();
        String fqdn = config.getPrintUrlFqdn();
        String basePath = config.getPrintUrlBasePath();

        if (StringUtils.isEmpty(basePath)) {
            baseUrl = URLScheme + "://" + fqdn;
        } else {
            baseUrl = URLScheme + "://" + fqdn + "//" + basePath;
        }

        return baseUrl;
    }

    /**
     * 印刷ダイアログを開く。
     */
    public void openPrintDialog() {

        //プリント送信要求結果 初期化
        printReqResult = false;

        //印刷要求Url情報設定
        setPrintUrl();

        //ダイアログ設定値
        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("resizable", false);
        options.put("draggable", true);
        options.put("position", "top");
        options.put("contentHeight", 620);
        options.put("contentWidth", 800);
        options.put("closable", false);

        //ダイアログオープン
        //RequestContext.getCurrentInstance().openDialog("PrintDialog", options, null);
        PrimeFaces.current().dialog().openDynamic("PrintDialog", options, null);
    }

    /**
     * 印刷ダイアログ：印刷結果判定
     */
    public void jdgPrintResult() {

    }

    public void onDialogReturn(SelectEvent event) {
        closePrintDialog();
    }

    /**
     * 印刷ダイアログ：ダイアログクローズ
     */
    public void closePrintDialog() {
        PrimeFaces.current().executeScript("window.parent.location.reload();");

        if (this.printFlg && this.printReqResult) {
            switch (this.screenId) {
                case ACCEPT:    // 受入画面
                case ENTRY:     // 入庫画面
                    this.registWarehouseData();
                    break;
                case INVENTORY: // 棚卸画面
                    this.registInventoryData();
                    break;
                case SHEIFMOVE:
                    this.clearForm();
                    break;
                default:
                    break;
            }
        }
        
        if (Screen.REPRINT.equals(this.screenId)) {
            this.clearForm();
            this.initReprint();
        }
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
     * 印刷チェックボックス値 設定
     *
     * @param chkDialog
     */
    public void setChkDialog(boolean chkDialog) {
        this.chkDialog = chkDialog;
        if (chkDialog) {
            closePrintDialog();
        }
    }

    /**
     * 印刷チェックボックス値 取得
     *
     * @return
     */
    public boolean isChkDialog() {
        return chkDialog;
    }

    /**
     * 移動数を取得する。
     * 
     * @return 移動数
     */
    public Integer getMoveNum() {
        return moveNum;
    }

    
    /**
     * 移動数フィールドの透かしを取得する。
     * 
     * @return 移動数フィールドの透かし
     */
    public Integer getMoveNumWater() {
        return this.moveNumWater;
    }

    /**
     * 移動数を設定する。
     * 
     * @param moveNum 移動数 
     */
    public void setMoveNum(Integer moveNum) {
        try {
            this.infMsg = "";
            this.renderedInfMsg = false;

            this.moveNum = moveNum;

            if (Objects.equals(this.moveNum, trnMaterial.getInStockNum())) {
                this.moveNumStyle = "color:black";
            } else {
                this.moveNumStyle = "color:red";
            }
    
            if (moveNum  < 1 || moveNum > this.trnMaterial.getInStockNum()) {
                // 不正な値の場合は、「移動数には在庫数の範囲で入力してください。」を表示
                this.infMsg = LocaleUtils.getString("warehouse.errMsgE011", this.language);
                this.renderedInfMsg = true;
                this.buttonDisable = true;
                PrimeFaces.current().executeScript("soundPlay();");
                PrimeFaces.current().executeScript("focusMoveNumInput();");
                return;
            }

            this.setButtonDisable();

            // 棚番号にフォーカスを移動
            this.transitionFocus(FOCUS_LOCATION);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.buttonDisable = true;
        }
    }

    /**
     * 移動数の表示スタイルを取得する。
     * 
     * @return 移動数の表示スタイル
     */
    public String getMoveNumStyle() {
        return this.moveNumStyle;
    }

    /**
     * ラベル印刷ボタンが押下された。
     */
    public void onPrint() {
        if (("NEXAS".equalsIgnoreCase(this.config.getWarehouseMode())
                || "HAMAI".equalsIgnoreCase(this.config.getWarehouseMode()))
                && this.renderedLabelInf) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.barcode);
            sb.append(" ");
            sb.append(this.productNo);
            sb.append(" ");
            sb.append(StringUtils.isEmpty(this.productName) ? "*" : this.productName);
            sb.append(" ");
            sb.append(this.orderNum);
            this.trnMaterial.setSupplyNo(sb.toString());
            // this.trnMaterial.setMaterialNo(sb.toString());
        }
        
        this.openPrintDialog();
    }

    /**
     * 区画名を取得する。
     * 
     * @return 区画名
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * 棚卸在庫数を取得する。
     * 
     * @return 棚卸在庫数
     */
    public Integer getInventoryNum() {
        return inventoryNum;
    }

    /**
     * 棚卸在庫数を設定する。
     * 
     * @param inventoryNum 棚卸在庫数
     */
    public void setInventoryNum(Integer inventoryNum) {
        this.inventoryNum = inventoryNum;
        this.reqLocReadStatusFlg = false;
        this.infMsg = "";
        this.setButtonDisable();
        this.transitionFocus(FOCUS_INVENTORYLOC);
    }

    /**
     * 棚卸在庫数の表示スタイルを取得する。
     * 
     * @return 棚卸在庫数の表示スタイル
     */
    public String getInventoryNumStyle() {
        return inventoryNumStyle;
    }

    /**
     * 棚番訂正を取得する。
     * 
     * @return 棚番訂正
     */
    public String getInventoryLoc() {
        return inventoryLoc;
    }

    /**
     * 棚番訂正を設定する。
     * 
     * @param inventoryLoc 棚番訂正
     */
    public void setInventoryLoc(String inventoryLoc) {
        this.inventoryLoc = inventoryLoc;
        this.reqLocReadStatusFlg = false;
        this.infMsg = "";

        if (StringUtils.isEmpty(this.inventoryLoc)) {
            return;
        }

        // 棚マスタ照会
        MstLocation mstLoc = warehouseModel.findLocation(areaName, this.inventoryLoc);
        if (Objects.isNull(mstLoc)) {
            this.inventoryLoc = null;
            // 「棚番号が存在しません。」
            this.infMsg = LocaleUtils.getString("warehouse.errMsgE006", this.language);
            this.renderedInfMsg = true;
            PrimeFaces.current().executeScript("soundPlay();");
            PrimeFaces.current().executeScript("focusInventoryLocInput();");
            return;
        } else {
            this.renderedInfMsg = true;
            if (!StringUtils.isEmpty(this.reqLoc) && !StringUtils.equals(this.inventoryLoc, this.reqLoc)) {
                // 「指定された棚ではありません。」
                PrimeFaces.current().executeScript("customAlert('" + LocaleUtils.getString("warehouse.WarnMsgW001", this.language) + "','');");
                PrimeFaces.current().executeScript("soundPlay();");
            }
        }

        this.transitionFocus(FOCUS_SUBMIT);
    }

    /**
     * 棚番訂正の表示スタイルを取得する。
     * 
     * @return 棚番訂正の表示スタイル
     */
    public String getInventoryLocStyle() {
        return inventoryLocStyle;
    }

    /**
     * 納品書フォームの表示性を取得する。
     * 
     * @return 納品書フォームの表示性
     */
    public boolean getRenderedSupplyNo() {
        return this.renderedSupplyNo;
    }

    /**
     * 発注番号を取得する。
     * 
     * @return 発注番号 
     */
    public String getSupplyNo() {
        return this.supplyNo;
    }
    
    /**
     * 発注番号を設定する。
     * 
     * @param supplyNo 発注番号
     */
    public void setSupplyNo(String supplyNo) {
        logger.info("setSupplyNo: " + supplyNo);

        initItemInf();
        initWarehouseInf();

        this.trnMaterial = warehouseModel.findMaterialBySupplyNo(supplyNo);
        if (Objects.isNull(this.trnMaterial)) {
            this.supplyNo = "";
            // 「該当する情報が存在しません。」
            this.attentionMessage = LocaleUtils.getString("warehouse.errMsgE003", this.language);
            this.renderedAttentionMessage = true;
            setButtonDisable();
            PrimeFaces.current().executeScript("soundPlay();");
            this.transitionFocus(FOCUS_SUPPLYNO);
            return;
        }
        
        this.supplyNo = supplyNo;
        this.transitionFocus(FOCUS_BARCODE);
    }

    /**
     * ラベル入力フォームの表示性を取得する。
     * @return 
     */
    public boolean isRenderedLabelInf() {
        return renderedLabelInf;
    }

    /**
     * 品番を取得する。
     * @return 品番
     */
    public String getProductNo() {
        return this.productNo;
    }

    /**
     * 品番を設定する。
     * @param productNo 品番 
     */
    public void setProductNo(String productNo) {
        this.productNo = StringUtils.isEmpty(productNo) ? "" : productNo.replace(" ", "");
        this.productName = null;
        this.orderNum = null;
        if (!StringUtils.isEmpty(productNo)) {
            MstProduct product = this.warehouseModel.findProduct(this.productNo);
            if (Objects.nonNull(product)) {
                this.mstProduct = product;
                this.productNo = this.mstProduct.getProductNo();
                this.productName = this.mstProduct.getProductName();
                
                if (Constants.DUMMY_CODE.equals(this.barcode)) {
                    // adManagerの倉庫案内のインポートにて登録された在庫情報を照会する
                    String code = "$$" + this.getAreaName() + "-" + this.mstProduct.getProductNo();
                    logger.info("Find stock item: " + code);
                    TrnMaterial material = warehouseModel.findMaterial(code);
                    if (Objects.nonNull(material)) {
                       this.orderNum = material.getInStockNum();
                    }
                }

                PrimeFaces.current().executeScript("focusInput('registForm:orderNumInput');");
                this.setButtonDisable();
                return;
            }
        }
        this.mstProduct.setProductNo(this.productNo);
        this.setButtonDisable();
    }

    /**
     * 品名を取得する。
     * @return 品名
     */
    public String getProductName() {
        return this.productName;
    }

    /**
     * 品名を設定する。
     * @param productName 品名 
     */
    public void setProductName(String productName) {
        this.productName = StringUtils.isEmpty(productName) ? "" : productName.replace(" ", "");
        this.mstProduct.setProductName(this.productName);
    }

    /**
     * 仕分数を取得する。
     * @return 仕分数
     */
    public Integer getSortNum() {
        return sortNum;
    }

    /**
     * 発注数を取得する。
     * @return 発注数
     */
    public Integer getOrderNum() {
        return orderNum;
    }

    /**
     * 発注数を設定する。
     * @param orderNum 発注数
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
        this.setButtonDisable();
        PrimeFaces.current().executeScript("focusSubmitButton();");
    }

    /**
     * 仕分数を設定する。
     * @param sortNum 仕分数 
     */
    public void setSortNum(Integer sortNum) {
        try {
            this.infMsg = "";
            this.renderedInfMsg = false;
            this.sortNum = sortNum;
    
            if (Objects.nonNull(sortNum) && (sortNum < 1 || sortNum > trnMaterial.getInStockNum())) {
                // 不正な値の場合は、「在庫数の範囲で入力してください。」を表示
                this.sortNumStyle = "color:red";
                this.infMsg = LocaleUtils.getString("warehouse.errMsgE011", this.language);
                this.renderedInfMsg = true;
                this.buttonDisable = true;
                PrimeFaces.current().executeScript("soundPlay();");
                PrimeFaces.current().executeScript("focusSortNumInput();");
                return;
            }

            this.sortNumStyle = "color:black";

            this.setButtonDisable();
            PrimeFaces.current().executeScript("focusSubmitButton();");

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.buttonDisable = true;
        }
    }

    /**
     * 仕分数(透かし)を取得する。
     * @return 仕分数(透かし)
     */
    public Integer getSortNumWater() {
        return this.sortNumWater;
    }

    /**
     * 仕分数の表示スタイルを取得する。
     * @return スタイル
     */
    public String getSortNumStyle() {
        return this.sortNumStyle;
    }

    /**
     * バーコード入力が無効かどうかを返す。
     * 
     * @return 
     */
    public boolean isDisableBarcode() {
        return disableBarcode;
    }

    /**
     * バーコード入力を無効にする。
     * 
     * @param disableBarcode 
     */
    public void setDisableBarcode(boolean disableBarcode) {
        this.disableBarcode = disableBarcode;
        if (this.disableBarcode) {
            this.setBarcode(Constants.DUMMY_CODE);
        } else {
            this.clearForm();
            this.initReprint();
        }
    }

    /**
     * 現品ラベルを印刷する
     * 印刷ダイアログの印刷ボタンが押下された時に呼出される
     */
    public void printLabel() {
        switch (this.screenId) {
            case SHEIFMOVE:
                // 資材移動の場合、資材情報を登録
                if (!this.registMoveData()) {
                    return;
                }
                this.setPrintUrl();
                break;

            default:
                break;
        }

        PrimeFaces.current().executeScript("sendPrintRequest('" + this.printUrl + "','" + this.language + "');");
    }

    /**
     * 移動先の区画を取得する。
     * 
     * @return 移動先の区画
     */
    public String getDestArea() {
        return destArea;
    }

    /**
     * 移動先の区画を設定する。
     * 
     * @param destArea 移動先の区画
     */
    public void setDestArea(String destArea) {
        this.destArea = destArea;
        this.setButtonDisable();
    }

    /**
     * エリアリスト 取得
     * @return 
     */    
    public List<String> getAreaList() {
        return areaList;
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
     * 単位を取得する。
     * 
     * @return 単位
     */
    public String getUnit() {
        return Objects.nonNull(this.mstProduct) ? (Objects.nonNull(this.mstProduct.getUnit()) ? this.mstProduct.getUnit() : "") : "";
    }

}
