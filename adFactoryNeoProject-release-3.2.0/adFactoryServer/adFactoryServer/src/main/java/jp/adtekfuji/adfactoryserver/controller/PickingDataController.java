/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import adtekfuji.utility.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jp.adtekfuji.adFactory.entity.search.DeliveryCondition;
import jp.adtekfuji.adFactory.enumerate.DeliveryRule;
import jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum;
import jp.adtekfuji.adFactory.enumerate.WarehouseEvent;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.common.ServiceConfig;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.warehouse.LogStock;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstProduct;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDelivery;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDeliveryItem;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnMaterial;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnReserveMaterial;
import jp.adtekfuji.adfactoryserver.model.warehouse.WarehouseModel;
import jp.adtekfuji.adfactoryserver.service.warehouse.LabelPrinter;
import jp.adtekfuji.adfactoryserver.utility.CookieUtils;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.adfactoryserver.utility.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.shaded.json.JSONException;

/**
 * 出庫画面
 *
 * @author 18-0326
 */
@Named(value = "pickingData")
@SessionScoped
public class PickingDataController extends WarehouseBean {


    
    private final ServiceConfig config = ServiceConfig.getInstance();
    private final Logger logger = LogManager.getLogger();

    @EJB
    private WarehouseModel warehouseModel;

    private final String[] patterns = new String[4];    // バーコードのパターン(正規表現)
    private final Integer gsOrderPos;                   // 現品票(納品書) GSオーダー開始桁数
    private final Integer gsOrderLength;               // GSオーダー桁数

    private DeliveryRule deliveryRule = DeliveryRule.NORMAL;
    private Short eventId = WarehouseEvent.LEAVE.getId();

    private String userId;                              // 社員番号
    private String userName;                            // 社員名
    private String userAreaMsg;                         // 社員情報エラーメッセージ

    private String areaName;                            // 現在区画名
    private Date pickingDate;

    private String pickingId;                           // 出庫指示番号
    private String remaining;                           // ピッキング数
    private Integer itemCount;                       // 払出作業総数
    private Integer currentItem;                       // 現在の作業数

    private String materialNo;                          // バーコード
    private String shelfId;                             // 棚番
    private ArrayList<ItemData> additionData;           // 資材情報詳細
    private Map<String, String> materialItems;          // 表示項目
    private String itemInfoAreaStyle;                   // 重要度エリア
    private String attentionMsg = "";                   // 重要度
    private Integer itemNo;                             // 明細番号
    private String checkResult;

    private Integer stockVal;                           // 要求数
    private boolean renderedStockVal;                   // 要求数
    private Integer stockOutVal;                        // 出庫数
    private int printStockOut;                          // 印刷用の出庫数
    private String orderNoPrefix;
    private List<String> selectedOrderNo;
    private String printOrderNo;                        // 印刷用の製造番号

    private String stockOutValStyle;

    private String readInfoMsg = "";                    // 読取情報エラーメッセージ
    private String operationGuide = "";

    private boolean renderedUserId;
    private boolean renderedBarcode;                    // バーコード入力
    private boolean renderedAlertWarnMsg;               // 警告
    private boolean renderedUserErrMsg;                 // 社員情報エラーメッセージ

    private String styleItemInfo;
    private String styleStockOutForm;

    private boolean barcodeDisable;                     // バーコード無効化
    private boolean stockOutValDisable;                 // 出庫数無効化
    private boolean stockOutDisable;                    // 登録ボタン無効化の設定
    private boolean buttonNextDisable;                  // 次ボタン無効化の設定

    private final ArrayList<SelectItem> orderNoList = new ArrayList<> (); // 製造番号

    private List<TrnDelivery> kanbans;
    private TrnDelivery selectedKanban;
    private TrnDeliveryItem trnDeliveryItem;                // 出庫指示アイテム
    private List<TrnDeliveryItem> trnDeliveryItemList;      // 出庫指示アイテムリスト
    private List<Boolean> trnDeliveryItemCompleteInfoList;  // 出庫・未出庫情報管理リスト
    private TrnMaterial trnMaterial;
    private String selectedUnitNo;
    private boolean showUnitTable = false;

    private static final boolean STATUS_DELIVERED = true;   // 出庫済
    private static final boolean STATUS_UNDELIVERED = false; // 未出庫

    /**
     * コンストラクタ
     */
    public PickingDataController() {
        this.patterns[0] = config.getPatternE1();
        //this.patterns[1] = config.getPatternL2();
        //this.patterns[2] = config.getPatternE3();
        this.patterns[1] = this.config.getPatternE3();
        this.patterns[2] = this.config.getPatternE5();
        this.patterns[3] = config.getPatternE6();
        this.gsOrderPos = config.getGsOrderPos();
        this.gsOrderLength = config.getGsOrderLength();

        initUserInf();
        initPickingInf();
        initItemInf();
        initStockOutInfo();
        LoadConfig();
    }

    @PostConstruct
    public void initUserId() {
        initInfo();
        //社員情報Cookie読み出し
        String cookieUserId = CookieUtils.getCookieValue("userId");
        if (!StringUtils.isEmpty(cookieUserId)) {
            setUserId(cookieUserId);
        }

        //倉庫情報Cookie読み出し
        String cookieAreaName = CookieUtils.getCookieValue("areaName");
        this.areaName = "";

        if (!StringUtils.isEmpty(cookieAreaName)) {
            this.areaName = cookieAreaName;
        }

        String cookiePrintFlg = CookieUtils.getCookieValue("pickingPrintFlg");
        if (!StringUtils.isEmpty(cookiePrintFlg)) {
            this.setPrint(Boolean.valueOf(cookiePrintFlg));
        }
    }

    /**
     * 社員情報初期化
     */
    private void initUserInf() {
        this.userId = "";
        this.userName = "";
        this.userAreaMsg = "";
        this.renderedUserId = true;
        this.renderedUserErrMsg = false;
    }

    /**
     * 出庫指示情報初期化
     */
    private void initPickingInf() {
        this.pickingId = "";
        this.remaining = "";
        this.itemCount = 0;
        this.currentItem = 0;
    }

    /**
     * 資材情報初期化
     */
    private void initItemInf() {
        this.shelfId = "";
        this.additionData = new ArrayList<>();
        this.materialNo = "";
        this.itemInfoAreaStyle = Constants.NORMAL_STYLE;
        this.attentionMsg = "";
        this.barcodeDisable = true;
        this.buttonNextDisable = true;
        this.styleItemInfo = "disp-none";
    }

    /**
     * 出庫情報初期化
     */
    private void initStockOutInfo() {
        this.stockVal = 0;
        this.stockOutVal = 0;
        this.stockOutValDisable = true;
        this.stockOutDisable = true;
        this.styleStockOutForm = "disp-none";
        this.renderedStockVal = true;
        this.stockOutValStyle = "color:black";
    }

    /**
     * メッセージ表示初期化
     */
    private void initInfo() {
        this.readInfoMsg = "";
        this.renderedAlertWarnMsg = false;
    }

    /**
     * メニュー画面遷移時処理
     */
    public void backMenuScreen() {
        initUserInf();
        initPickingInf();
        initItemInf();
        initStockOutInfo();
        clearMessage();
        initUserId();

    }

    /**
     * 初期化処理(メニュー画面から遷移時)
     * 
     * @param mode
     * @param eventId
     */
    public void init(String mode, Short eventId) {
        logger.info("Initailize picking page: " + eventId);
        try {
            this.deliveryRule = DeliveryRule.valueOf(mode);
            this.eventId = eventId;

            this.initialize("PICKING");

            if (DeliveryRule.ISSUE.equals(this.deliveryRule)) {
                this.renderedBarcode = true;
            } else {
                this.renderedBarcode = "HAMAI".equalsIgnoreCase(this.getWarehouseMode()) ? false : this.config.isInputBarcode();
            }
            
            if (warehouseModel.getAreaInventoryFlag(CookieUtils.getCookieValue("areaName"))) {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                String contextPath = facesContext.getExternalContext().getRequestContextPath();
                facesContext.getExternalContext().redirect(contextPath + "/warehouse/index.xhtml");	
            }
            
            if (this.eventId == 31) {
                this.renderedBarcode = !"JCM".equalsIgnoreCase(this.getWarehouseMode()); // JCMカスタム
                this.pickingDate = new Date();
            }
         
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        initUserInf();  //社員情報 初期化
        initPickingInf();
        initItemInf();
        initStockOutInfo();
        clearMessage();
        initUserId();
    }

    /**
     * 社員番号 取得
     *
     * @return
     */
    @Override
    public String getUserId() {
        return userId;
    }

    /**
     * 社員番号 設定
     *
     * @param userId
     */
    @Override
    public void setUserId(String userId) {
        initUserInf();

        if (StringUtils.isEmpty(userId)) {
            this.userId = userId;
            this.userAreaMsg = LocaleUtils.getString("warehouse.errMsgE003", this.getLanguage());
            this.renderedUserErrMsg = true;
            PrimeFaces.current().executeScript("focusUserInfoForm();");
            PrimeFaces.current().executeScript("soundPlay();");
            checkStatus();
            return;
        }

        OrganizationEntity userInfo = warehouseModel.findOrganization(userId);

        if (Objects.nonNull(userInfo) && Objects.nonNull(userInfo.getOrganizationName())) {
            this.userId = userId;
            this.userName = userInfo.getOrganizationName();
            this.userAreaMsg = "";
            PrimeFaces.current().executeScript("writeUserIdCookie('" + this.userId + "');");
            transitionFocus(FOCUS_PICKING_ID);
        } else {
            this.userId = userId;
            this.userAreaMsg = LocaleUtils.getString("warehouse.errMsgE003", this.getLanguage());
            this.renderedUserErrMsg = true;
            PrimeFaces.current().executeScript("focusUserInfoForm();");
            PrimeFaces.current().executeScript("soundPlay();");
        }
        checkStatus();
    }

    /**
     * 社員名 取得
     *
     * @return
     */
    @Override
    public String getUserName() {
        return userName;
    }

    /**
     * 出庫指示番号 取得
     *
     * @return
     */
    public String getPickingId() {
        return pickingId;
    }

    /**
     * 出庫指示番号　設定
     *
     * @param pickingId
     * @throws org.primefaces.json.JSONException
     */
    public void setPickingId(String pickingId) throws JSONException {
        logger.info("setPickingId: " + pickingId);
        
        initPickingInf();
        initItemInf();
        initStockOutInfo();
        this.readInfoMsg = "";

        this.pickingId = pickingId;

        if (StringUtils.isEmpty(pickingId)) {
            this.readInfoMsg = LocaleUtils.getString("warehouse.errMsgE003", this.getLanguage());
            this.pickingId = "";
            PrimeFaces.current().executeScript("focusPickingIdInfoForm();");
            PrimeFaces.current().executeScript("soundPlay();");
            PrimeFaces.current().executeScript("scrollToBottom();");
            checkStatus();
            return;
        }

        this.trnDeliveryItemList = null;
        this.trnDeliveryItem = null;
        this.trnDeliveryItemCompleteInfoList = new LinkedList<>();

        // 出庫指示アイテムを照会
        if (WarehouseEvent.LEAVE.getId().equals(this.eventId)) {
            this.trnDeliveryItem = this.findDeliveryItem(pickingId);
        } else {
            // 出荷払出
            if ("JCM".equalsIgnoreCase(this.config.getWarehouseMode())) {
                this.trnDeliveryItem = this.findDeliveryItem(pickingId);
            } else if ("NEXAS".equalsIgnoreCase(this.config.getWarehouseMode())) {
                this.trnDeliveryItem = this.findDeliveryItemForNEXAS(pickingId);
            }
        }

        if (Objects.nonNull(this.trnDeliveryItem)) {
            this.pickingId = trnDeliveryItem.getPK().getDeliveryNo();
        } else {
            // オペレーションNo:L5 払出指示書(出庫番号)
            this.trnDeliveryItemList = warehouseModel.findDeliveryItem(pickingId, this.areaName, this.deliveryRule);
        }

        if (Objects.isNull(trnDeliveryItem) && (Objects.isNull(trnDeliveryItemList) || trnDeliveryItemList.isEmpty())) {
            // 取得失敗 TrnDeliveryが0件のとき
            if (!StringUtils.equals(this.readInfoMsg, LocaleUtils.getString("warehouse.errMsgE003", this.getLanguage()))) {
                // 「出庫が必要な資材はありません。」
                this.readInfoMsg = LocaleUtils.getString("warehouse.errMsgE010", this.getLanguage());
            }
            this.pickingId = "";
            PrimeFaces.current().executeScript("focusPickingIdInfoForm();");
            PrimeFaces.current().executeScript("soundPlay();");
            PrimeFaces.current().executeScript("scrollToBottom();");
            checkStatus();
            return;
        }

        //出庫指示アイテムまたは出庫指示照会 情報取得
        if (Objects.isNull(trnDeliveryItem)) {

            this.readInfoMsg = "";

            this.trnDeliveryItem = trnDeliveryItemList.get(0);

            for (int i = 0; i < trnDeliveryItemList.size(); i++) {
                //出庫完了フラグ
                this.trnDeliveryItemCompleteInfoList.add(STATUS_UNDELIVERED);
            }
            if (Objects.isNull(trnDeliveryItem)) {
                //  取得失敗
                this.readInfoMsg = LocaleUtils.getString("warehouse.errMsgE003", this.getLanguage());
                PrimeFaces.current().executeScript("focusPickingIdInfoForm();");
                PrimeFaces.current().executeScript("soundPlay();");
                PrimeFaces.current().executeScript("scrollToBottom();");
                checkStatus();
                return;
            }

            // ピッキング点数
            this.itemCount = this.trnDeliveryItemList.size();

            // 作業残件 現在作業数/払出作業総数
            this.remaining = this.itemCount > 0 ? (this.currentItem + 1) + "/" + this.itemCount : "0/0";
        }

        // 資材情報詳細取得
        this.setDeliveryItem(this.trnDeliveryItem);

        this.renderedUserId = false;
        this.styleItemInfo = "formCommon form-nomal";
        this.styleStockOutForm = "formCommon form-stockOutForm";

        if (Objects.nonNull(trnDeliveryItem)) {
            if (trnDeliveryItem.getDeliveryNum() >= trnDeliveryItem.getRequiredNum()) {
                // 「出庫が必要な資材はありません。」
                this.readInfoMsg = LocaleUtils.getString("warehouse.errMsgE010", this.getLanguage());
                this.barcodeDisable = true;
                this.stockOutValDisable = true;
                PrimeFaces.current().executeScript("focusPickingIdInfoForm();");
                PrimeFaces.current().executeScript("soundPlay();");
                PrimeFaces.current().executeScript("scrollToBottom();");
                checkStatus();
                return;
            }
        }

        if (this.pickingId.startsWith(TrnMaterial.SUPPLY_PREFIX)
                || this.pickingId.startsWith(TrnMaterial.ORDER_PREFIX)
                || !this.renderedBarcode) {
            // 資材番号・現品票による出庫の場合
            this.barcodeDisable = true;
            this.renderedStockVal = false;
            
            if (WarehouseEvent.LEAVE.getId().equals(eventId)) {
                // 出庫数にフォーカスを設定
                this.stockOutValDisable = false;
                transitionFocus(FOCUS_STOCKOUT);
            } else {
                // 出荷払出画面
                stockOutVal = trnDeliveryItem.getRequiredNum();
            }
        } else {
            this.renderedStockVal = true;
            // バーコードにフォーカスを設定
            this.barcodeDisable = false;
            transitionFocus(FOCUS_BARCODE);
        }

        checkStatus();
    }

    /**
     * 出庫指示アイテムを照会する。()
     *
     * @return 出庫指示アイテム
     */
    private TrnDeliveryItem findDeliveryItem(String inputStr) {
        TrnDeliveryItem deliveryItem = null;
        boolean isFindDeliveryItemImple = false;

        try {
            String _materialNo = null;
            for (;;) {
                // オペレーションNo:L1 QRラベル(QRコード)
                if (inputStr.startsWith(TrnMaterial.SUPPLY_PREFIX) || pickingId.startsWith(TrnMaterial.ORDER_PREFIX)) {
                    _materialNo = inputStr;
                    break;
                }

                // オペレーションNo:L2 払出指示書(QRコード)
                // QRコード:<製造番号>␣<製品No>␣<指示明細No>␣<品目>␣<品名>␣<数量>
                if (inputStr.matches(this.patterns[1])) {
                    String[] values = inputStr.split(Constants.SPACE, 0);
                    if (values.length < 5) {
                        break;
                    }
                    isFindDeliveryItemImple = true;
                    
                    String deliveryNo = values[2];  // 出庫番号
                    String productNo = values[3];   // 品目
                    StringBuilder productName = new StringBuilder();    // 品名
                    String orderNo = values[0];     // 製造番号
                    String serialNo = values[1];    //シリアル番号
                    int requiredNum = Integer.parseInt(values[values.length - 1]); // 要求数
                    
                    for (int ii = 4; ii < (values.length - 1); ii++) {
                        productName.append(values[ii]);
                        productName.append(" ");
                    }
                    
                    deliveryItem = warehouseModel.findOrCreateDeliveryItem(deliveryNo, productNo, productName.toString().trim(), orderNo, serialNo, requiredNum, this.areaName);
                    return deliveryItem;
                }

                // オペレーションNo:L3 支給品の現品票(QRコード)
                // QRコード:<品目><品名><数量><倉庫オーダー>            
                if (inputStr.matches(this.patterns[0])) {
                    int pos = gsOrderPos - 1;
                    String gsOrderNo = inputStr.substring(pos, pos + gsOrderLength);
                    if (!StringUtils.isEmpty(gsOrderNo)) {
                        isFindDeliveryItemImple = true;
                        _materialNo = TrnMaterial.SUPPLY_PREFIX + gsOrderNo;
                    }
                    break;
                }

                // オペレーションNo:L4 購入品の現品票(発注No)
                TrnMaterial _trnMaterial = warehouseModel.findMaterialBySupplyNo(inputStr);
                if (Objects.nonNull(_trnMaterial)) {
                    _materialNo = _trnMaterial.getMaterialNo();
                }
                break;
            }

            if (!StringUtils.isEmpty(_materialNo)) {
                isFindDeliveryItemImple = true;
                this.pickingId = _materialNo;
                deliveryItem = warehouseModel.findDeliveryByMaterialNo(_materialNo);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);

        } finally {
            if (deliveryItem == null && isFindDeliveryItemImple) {
                // 取得失敗 
                this.readInfoMsg = LocaleUtils.getString("warehouse.errMsgE003", this.getLanguage());
                //RequestContext context = RequestContext.getCurrentInstance();
                //PrimeFaces.current().executeScript("focusPickingIdInfoForm();");
                //PrimeFaces.current().executeScript("soundPlay();");
            }
        }

        return deliveryItem;
    }

    /**
     * 出荷払出のQRコードから出庫指示アイテムを照会する
     *   QRコード: <払出指示明細No>␣<出荷No>␣<品番>␣<品名>␣<出荷数>␣<明細数>
     * 
     * @param inputStr 出荷払出のQRコード
     * @return 出庫指示アイテム
     */
    private TrnDeliveryItem findDeliveryItemForNEXAS(String inputStr) {
        TrnDeliveryItem deliveryItem = null;

        try {
                
            String[] values = inputStr.trim().split(Constants.SPACE);
            if (values.length < 6) {
                return deliveryItem;
            }

            String deliveryNo = values[0];                                  // 出庫番号 (払出指示明細No)
            String orderNo = values[1];                                     // 製造番号 (出荷No)
            String productNo = values[2];                                   // 品番
            int requiredNum = Integer.parseInt(values[values.length - 2]);  // 出荷数
            String serialNo = values[values.length - 1];                    // 明細数

            StringBuilder productName = new StringBuilder(); // 品名
            for (int ii = 3; ii < (values.length - 2); ii++) {
                productName.append(values[ii]);
                productName.append(" ");
            }

            deliveryItem = warehouseModel.findOrCreateDeliveryItem(deliveryNo, productNo, productName.toString(), orderNo, serialNo, requiredNum, this.areaName);

            logger.info("deliveryItem: deliveryNo={}, requiredNum={}, deliveryNum={}" + deliveryNo, deliveryItem.getRequiredNum(), deliveryItem.getDeliveryNum());
        
            if (Objects.equals(deliveryItem.getRequiredNum(), deliveryItem.getDeliveryNum())) {
                // 払出完了済みの場合、次の払出指示を
                deliveryItem.setRequiredNum(requiredNum);
                deliveryItem.setDeliveryNum(0);
                deliveryItem.setOrderNo(orderNo);
                deliveryItem.setUpdateDate(new Date());

                warehouseModel.updateDeliveryItem(deliveryItem);
            
                List<TrnDeliveryItem> list = warehouseModel.findDeliveryItem(deliveryItem.getPK().getDeliveryNo(), this.areaName, this.deliveryRule);
                deliveryItem = list.get(0);
            }
            
            return deliveryItem;

        } catch (NumberFormatException ex) {
            logger.fatal(ex, ex);

        } finally {
            if (Objects.isNull(deliveryItem)) {
                // 取得失敗 
                this.readInfoMsg = LocaleUtils.getString("warehouse.errMsgE003", this.getLanguage());
                //RequestContext context = RequestContext.getCurrentInstance();
                //PrimeFaces.current().executeScript("focusPickingIdInfoForm();");
                //PrimeFaces.current().executeScript("soundPlay();");
            }
        }

        return deliveryItem;
    }

    /**
     * 作業残件 取得
     *
     * @return
     */
    public String getRemaining() {
        return remaining;
    }

    /**
     * 棚番号 取得
     *
     * @return
     */
    public String getShelfId() {
        return shelfId;
    }

    /**
     * バーコード 取得
     *
     * @return
     */
    @Override
    public String getBarcode() {
        return materialNo;
    }
  
    /**
     * 資材情報を照合する
     *
     * @param barcode バーコード文字列
     */
    @Override
    public void setBarcode(String barcode) {
        logger.info("setBarcode: " + barcode);

        this.readInfoMsg = "";

        // 資材情報を取得
        this.trnMaterial = this.findMaterial(barcode);
        
        if (Objects.isNull(this.trnMaterial) 
                || this.trnMaterial.getInStockNum() <= 0
                || Objects.isNull(this.trnMaterial.getLocation())
                || !StringUtils.equals(this.trnMaterial.getLocation().getAreaName(), this.areaName)) {
            // 入庫なしか在庫なしの場合、「バーコードが間違っています。」
            logger.info("Invalid TrnMaterial: " + this.trnMaterial);
            errorBarCodeSetting(LocaleUtils.getString("warehouse.errMsgE008", this.getLanguage()));
            checkStatus();
            return;
        }
        
        this.materialNo = trnMaterial.getMaterialNo();

        if (WarehouseEvent.LEAVE.getId().equals(this.eventId) 
                && Objects.nonNull(trnMaterial.getCategory())
                && trnMaterial.getCategory() == 9) {
            // 出庫数を入力可能にする
            logger.info("Unkown material.");
            this.stockOutValDisable = false;
            transitionFocus(FOCUS_STOCKOUT);
            checkStatus();
            checkInsepaction();
            return;
        }

        if (Objects.nonNull(this.selectedKanban) 
                && this.selectedKanban.getDeliveryRule() == 2) {
            
            //Optional<TrnReserveMaterial> opt = this.trnDeliveryItem.getReserveMaterials().stream()
            //        .filter(o -> o.getMaterial().getSupplyNo().endsWith(barcode))
            //        .findFirst();
            //if (opt.isPresent()) {
            //    TrnReserveMaterial _reserveMaterial = opt.get();

            TrnReserveMaterial _reserveMaterial = this.warehouseModel.findReserveMaterial(this.trnDeliveryItem.getPK().getDeliveryNo(), this.trnDeliveryItem.getPK().getItemNo(), this.materialNo, true);
            if (Objects.nonNull(_reserveMaterial)) {
                logger.info("Found TrnReserveMaterial: {}", _reserveMaterial);
                if (_reserveMaterial.getReservedNum() <= _reserveMaterial.getDeliveryNum()) {
                    // ピッキング済です
                    errorBarCodeSetting(LocaleUtils.getString("warehouse.errMsgE016", this.getLanguage()));
                    checkStatus();
                    return;
                }
                
                // 引当情報の照合がOKの場合
                this.stockOutVal = _reserveMaterial.getReservedNum();

                this.checkStatus();

                if (this.isPrint()) {
                    PrimeFaces.current().executeScript("clickSubmitButton();");
                } else {
                    this.checkResult = "OK";
                    this.regist();
                }

            } else {
                errorBarCodeSetting(LocaleUtils.getString("warehouse.errMsgE008", this.getLanguage()));
                checkStatus();
                return;
            }
            return;
        }

        // 出庫アイテムの候補を取得
        List<TrnMaterial> materialList = this.trnDeliveryItem.getMaterialList();
        if (Objects.nonNull(materialList) && !materialList.isEmpty()) {
            for (TrnMaterial material : materialList) {
                // 資材番号を照合
                if (StringUtils.equals(material.getMaterialNo(), this.materialNo)) {
                    logger.info("MaterialNo matched.");
                    
                    if (WarehouseEvent.LEAVE.getId().equals(this.eventId)) {
                        // 出庫数を入力可能にする
                        this.stockOutValDisable = false;
                        transitionFocus(FOCUS_STOCKOUT);
                    } else {
                        this.stockOutValDisable = true;
                        int requiredNum = this.trnDeliveryItem.getRequiredNum() - this.trnDeliveryItem.getDeliveryNum();
                        int num = this.trnMaterial.getInStockNum() - requiredNum;
                        this.stockOutVal = num < 0 ? requiredNum + num : requiredNum;
                    }
                    checkStatus();
                    checkInsepaction();
                    return;
                }
            }
        }

        if (StringUtils.equals(trnMaterial.getProduct().getProductNo(), this.trnDeliveryItem.getProduct().getProductNo())) {
            // 品目一致の場合、「古い在庫品が存在します。」を表示
            logger.info("ProductNo matched.");
            warnBarCodeSetting();
            if (WarehouseEvent.SHIPPING.getId().equals(this.eventId)) {
                this.stockOutValDisable = true;
                int requiredNum = this.trnDeliveryItem.getRequiredNum() - this.trnDeliveryItem.getDeliveryNum();
                int num = this.trnMaterial.getInStockNum() - requiredNum;
                this.stockOutVal = num < 0 ? requiredNum + num : requiredNum;
            }
            checkStatus();
            checkInsepaction();
            return;
        }

        // 出庫指示情報リスト確認
        if (Objects.isNull(this.trnDeliveryItemList) || this.trnDeliveryItemList.isEmpty()) {
            // 「バーコードが間違っています。」
            logger.info("DeliveryItemList is empty.");
            errorBarCodeSetting(LocaleUtils.getString("warehouse.errMsgE008", this.getLanguage()));
            checkStatus();
            return;
        }

        // 別の出庫指示の資材番号比較
        for (int j = 0; j < this.trnDeliveryItemList.size(); j++) {
            //出庫指示から資材情報リスト取得
            List<TrnMaterial> materialListAft = this.trnDeliveryItemList.get(j).getMaterialList();
            if (Objects.isNull(materialListAft) || materialListAft.isEmpty()) {
                //資材情報リストがない場合
                continue;
            }

            // 資材情報リストから入力された資材番号検索
            for (int i = 0; i < materialListAft.size(); i++) {
                // 資材情報リストの資材番号と入力された資材番号を照合
                if (StringUtils.equals(materialListAft.get(i).getMaterialNo(), this.materialNo)) {
                    if(Objects.equals(this.trnDeliveryItemCompleteInfoList.get(j), STATUS_DELIVERED)){
                        continue;
                    }
                    logger.info("MaterialNo matched.");

                    // 出庫情報詳細追加
                    currentItem = j;
                    remaining = (currentItem + 1) + "/" + itemCount;
                    trnDeliveryItem = trnDeliveryItemList.get(j);
                    setDeliveryItem(trnDeliveryItem);
                    this.materialNo = materialListAft.get(i).getMaterialNo();
                    // 出庫数を入力可能にする
                    this.stockOutValDisable = false;
                    // フォーカスを出庫数に移動
                    transitionFocus(FOCUS_STOCKOUT);
                    checkStatus();
                    checkInsepaction();
                    return;
                }
            }
        }

        // 別の出庫指示の品目照会
        for (int j = 0; j < this.trnDeliveryItemList.size(); j++) {
            // 入力資材番号から取得した資材情報の品目と出庫アイテムの品目と照合
            if (StringUtils.equals(trnMaterial.getProduct().getProductNo(), this.trnDeliveryItemList.get(j).getProduct().getProductNo())) {
                if(Objects.equals(this.trnDeliveryItemCompleteInfoList.get(j), STATUS_DELIVERED)){
                    continue;
                }
                logger.info("ProductNo matched.");
                // 出庫情報詳細追加
                currentItem = j;
                remaining = (currentItem + 1) + "/" + itemCount;
                trnDeliveryItem = trnDeliveryItemList.get(j);
                setDeliveryItem(trnDeliveryItem);
                this.materialNo = trnMaterial.getMaterialNo();

                // フォーカスを出庫数に移動
                PrimeFaces.current().executeScript("focusStockOutValInput();");

                // 品目一致の場合、「古い在庫品が存在します。」を表示
                warnBarCodeSetting();
                checkStatus();
                checkInsepaction();
                return;
            }
        }

        // 一致する品目がなかった場合、「バーコードが間違っています。」
        errorBarCodeSetting(LocaleUtils.getString("warehouse.errMsgE008", this.getLanguage()));
        checkStatus();
    }

    /**
     * バーコード設定 エラー出力処理
     *
     * @param message 表示メッセージ
     */
    private void errorBarCodeSetting(String message) {
        this.readInfoMsg = LocaleUtils.getString("warehouse.errMsgE008", this.getLanguage());
        this.readInfoMsg = message;
        this.stockOutValDisable = true;
        this.materialNo = "";
        PrimeFaces.current().executeScript("focusBarcodeForm();");
        PrimeFaces.current().executeScript("soundPlay();");
        PrimeFaces.current().executeScript("scrollToBottom();");
    }

    /**
     * バーコード設定 警告出力処理
     *
     */
    private void warnBarCodeSetting() {
        PrimeFaces.current().executeScript("customAlert('" + LocaleUtils.getString("warehouse.warnMsgW004", this.getLanguage()) + "','" + FOCUS_STOCKOUT + "');");
        PrimeFaces.current().executeScript("soundPlay();");
        this.renderedAlertWarnMsg = true;
        this.stockOutValDisable = false;
    }

    /**
     * 資材情報を照会する。
     *
     * @param barcode バーコード文字列
     */
    @Override
    protected TrnMaterial findMaterial(String barcode) {
        TrnMaterial _material = null;
        try {
            for (;;) {
                // QRラベル
                if (barcode.startsWith(TrnMaterial.SUPPLY_PREFIX) || barcode.startsWith(TrnMaterial.ORDER_PREFIX)) {
                    _material = warehouseModel.findMaterial(barcode);
                    break;
                }

                // 支給品の現品票(QRコード)
                // QRコード:<品目><品名><数量><倉庫オーダー>
                if (barcode.matches(this.patterns[0])) {
                    Integer pos = gsOrderPos - 1;
                    String gsOrderNo = barcode.substring(pos, pos + gsOrderLength);
                    _material = warehouseModel.findMaterialBySupplyNo(gsOrderNo);
                    break;
                }

                // 後方一致で支給品かどうかを確認する。バーコードリーダーのエンコードがShift-JIS以外の場合、半角カナで桁数が変るため。
                if (barcode.matches(".*\\d{6}GS\\d{7}\\d{7}.{63}$") && barcode.length() > 140) {
                    String gsOrderNo = barcode.substring(barcode.length() - 79).substring(0, gsOrderLength);
                    _material = warehouseModel.findMaterialBySupplyNo(gsOrderNo);
                    if (Objects.nonNull(_material)) {
                        break;
                    }
                }
                
                // 現品票(QRコード)
                // QRコード:<製造オーダー番号>␣<図番>␣<品目>␣<品名>␣<数量>␣<ロット番号>
                if (barcode.matches(this.patterns[3])) {
                    String[] values = barcode.split(Constants.SPACE, 0);
                    if (values.length >= 6) {
                        this.materialNo = values[5];
                        _material = warehouseModel.findMaterialBySupplyNo(this.materialNo);
                        break;
                    }
                }

                // オペレーションNo:E5 製造指示書(QRコード)による資材照会
                // QRコード:<製造番号>␣<製品No>␣<品目>␣<品名>␣<数量>
                if (barcode.matches(this.patterns[2])) {
                    String[] values = barcode.split(Constants.SPACE, 0);
                    if (values.length >= 5 && values[0].length() == 9) {
                        // 製造番号と製品Noで一意の値となる
                        this.materialNo = values[0] + Constants.KEY_SEPARATOR + values[1];
                        _material = warehouseModel.findMaterialBySupplyNo(this.materialNo);
                        break;
                    }
                }
                        
                // オペレーションNo:E3 購入品の現品票(QRコード)による資材照会
                // QRコード:<発注伝票No>␣<品目>␣<品名>␣<数量>
                if (barcode.matches(this.patterns[1]) || barcode.matches("^.+\\s.+\\s.\\d+$")) {
                    String[] values = barcode.split(Constants.SPACE, 0);
                    if (values.length >= 3) {
                        if (Constants.DUMMY_CODE.equals(values[0]) && Objects.nonNull(this.trnDeliveryItem)) {
                            // adManagerの倉庫案内のインポートにて登録された在庫情報を照会する
                            String code = "$$" + this.getAreaName() + "-" + this.trnDeliveryItem.getProduct().getProductNo();
                            logger.info("Find stock item: " + code);
                            _material = warehouseModel.findMaterial(code);
                            if (Objects.nonNull(material)) {
                                break;
                            }
                        }

                        _material = warehouseModel.findMaterialBySupplyNo(values[0]);
                        break;
                    }
                }

                // 購入品の現品票(発注No)
                _material = warehouseModel.findMaterialBySupplyNo(barcode);
                if (Objects.isNull(_material)) {
                    if (Constants.DUMMY_CODE.equals(barcode) && Objects.nonNull(this.trnDeliveryItem)) {
                        // システム移行中の資材情報
                        String areaCode = warehouseModel.getAreaCode(this.areaName);
                        String falseNo = Constants.DUMMY_CODE + areaCode + Constants.KEY_SEPARATOR + this.trnDeliveryItem.getProduct().getProductNo();
                        _material = warehouseModel.findMaterialBySupplyNo(falseNo);
                        
                        if (Objects.isNull(_material)) {
                            _material = warehouseModel.findMaterialBySupplyNo(this.areaName + Constants.KEY_SEPARATOR + this.trnDeliveryItem.getProduct().getProductNo());
                        }
                    }
                }
                
                break;
            }
        } catch (NumberFormatException ex) {
            logger.fatal(ex, ex);
            return null;
        }
        return _material;
    }

    /**
     * 要求数 取得
     *
     * @return
     */
    public String getStockVal() {
        return stockVal + " " + this.getUnit();
    }

    /**
     * 出庫数 取得
     *
     * @return
     */
    public Integer getStockOutVal() {
        return stockOutVal;
    }

    /**
     * 出庫数 設定
     *
     * @param stockOutVal
     */
    public void setStockOutVal(Integer stockOutVal) {
        this.readInfoMsg = "";
        this.stockOutVal = stockOutVal;
        this.stockOutValStyle = "color:black";
        clearMessage();

        if (Objects.nonNull(this.stockOutVal)) {
            // 登録ボタンの活性判定
            checkStatus();

            if (this.renderedStockVal) {
                // 出庫数が要求数と異なる場合
                if (!Objects.equals(this.stockOutVal, this.stockVal)) {
                    this.stockOutValStyle = "color:red";
                    PrimeFaces.current().executeScript("confirmDeliveryNum('" + this.stockOutVal + "','" + this.stockVal + "','" + this.getLanguage() + "');");
                    PrimeFaces.current().executeScript("soundPlay();");
                    return;
                }
            }
        
            if (!this.stockOutDisable) {
                PrimeFaces.current().executeScript("focusSubmitButton();");                
            } else {
                transitionFocus(FOCUS_STOCKOUT);
            }
        } else {
            checkStatus();
            transitionFocus(FOCUS_STOCKOUT);
        }

    }

    /**
     * 出庫数文字スタイル 取得
     *
     * @return
     */
    public String getStockOutValStyle() {
        return stockOutValStyle;
    }

    /**
     * 出庫数文字スタイル 設定
     *
     * @param stockOutValStyle
     */
    public void setStockOutValStyle(String stockOutValStyle) {
        this.stockOutValStyle = stockOutValStyle;
    }

    /**
     * 登録ボタンが押下された。
     */
    public void onRegist() {
        this.orderNoList.clear();
        this.orderNoPrefix = "";
        this.selectedOrderNo = new ArrayList<>();
        this.printOrderNo = null;
        
        if (StringUtils.isEmpty(this.materialNo)) {
            // 現品票のバーコードが入力されていません。バーコード入力が無効になっている可能性があります
            // adFactoryService.properties の wh_inputBarcode を確認すること
            this.readInfoMsg = LocaleUtils.getString("warehouse.errMsgE014");
            return;
        }

        if (this.isPrint()) {
            this.trnMaterial = this.warehouseModel.findMaterial(this.materialNo);

            // 製造番号
            String orderNo = this.trnMaterial.getOrderNo();
            if (this.config.isInputBarcode()) {
                orderNo = this.trnDeliveryItem.getOrderNo();
            } else {
                // シリアル番号一覧の生成 (HAMAI)
                this.listSerialNo();
            }

            // 現品ラベル発行
            this.printStockOut = this.stockOutVal;
            this.createPrintUrl(this.trnMaterial, this.printStockOut, orderNo, this.areaName, null, this.userId);
            this.openPrintDialog("PickingPrintDialog", 800, 700);
        } else {
            this.regist();
        }
    }

    /**
     * 出庫 登録処理
     */
    public void regist() {
        logger.info("regist start.");

        // 出庫情報の登録
        Response response;
        if (Objects.nonNull(this.itemNo)) {
            response = warehouseModel.leaveWarehouse(WarehouseEvent.valueOf(this.eventId), this.userId, this.pickingId, 
                    this.itemNo, this.materialNo, this.stockOutVal, this.printOrderNo, (this.eventId == 31) ? this.pickingDate : new Date());
        } else {
            response = warehouseModel.leaveWarehouse(this.userId, this.materialNo, this.stockOutVal, this.printOrderNo);
        }

        if (response.getStatus() == HttpURLConnection.HTTP_OK) {
            if (DeliveryRule.ISSUE.equals(this.deliveryRule)) {
                this.trnDeliveryItem.setDeliveryNum(this.trnDeliveryItem.getDeliveryNum() + this.stockOutVal);
                this.trnDeliveryItem.getReserveMaterials().stream()
                    .filter(o -> StringUtils.equals(this.materialNo, o.getPK().getMaterialNo()))
                    .findFirst()
                    .ifPresent(o -> o.setDeliveryNum(this.stockOutVal));
                PrimeFaces.current().executeScript("popupBox('OK');");
            }
               
            if (!this.pickingId.startsWith(TrnMaterial.SUPPLY_PREFIX)
                    && !this.pickingId.startsWith(TrnMaterial.ORDER_PREFIX)
                    && (this.stockVal - this.stockOutVal > 0)) {

                if (DeliveryRule.ISSUE.equals(this.deliveryRule)) {
                    setDeliveryItem(this.trnDeliveryItem);

                } else {
                    // 出庫指示書による分割出庫：出庫数が要求数に満たない場合
                    this.stockVal = this.stockVal - this.stockOutVal;
                    if (Objects.nonNull(this.trnDeliveryItemList) && !this.trnDeliveryItemList.isEmpty()) {
                        this.trnDeliveryItemList.get(this.currentItem).setDeliveryNum(this.trnDeliveryItemList.get(this.currentItem).getDeliveryNum() + this.stockOutVal);
                    }

                    if (Objects.nonNull(this.trnDeliveryItem)) {
                        this.trnDeliveryItem.setDeliveryNum(this.trnDeliveryItem.getDeliveryNum() + this.stockOutVal);
                    }

                    this.stockOutVal = 0;
                }

                this.stockOutValStyle = "color:black";
                this.materialNo = "";
                transitionFocus(FOCUS_BARCODE);

            } else if (0 < this.itemCount) {

                //「読取情報エラーメッセージ」フレーム内・「数情報」フレーム内の情報をクリア
                clearItemInfo();
                // 出庫完了フラグを更新する
                if (!trnDeliveryItemCompleteInfoList.isEmpty()){
                    this.trnDeliveryItemCompleteInfoList.set(currentItem, STATUS_DELIVERED);                
                }
                // 現在の作業数を更新する
                currentItem++;

                // 出庫状況確認
                for (int cnt = currentItem; cnt < itemCount; cnt++) {
                    if (Objects.equals(trnDeliveryItemCompleteInfoList.get(cnt), STATUS_DELIVERED)) {
                        currentItem++;
                    } else {
                        break;
                    }
                }

                if (this.currentItem >= this.itemCount) {
                    // 最後まで来たので、出庫されていない資材が存在しないか確認
                    for (int j = 0; j < this.itemCount; j++) {
                        if (Objects.equals(this.trnDeliveryItemCompleteInfoList.get(j), STATUS_UNDELIVERED)) {
                            // 出庫されていない
                            this.currentItem = j;
                            break;
                        }
                    }

                    if (this.currentItem >= this.itemCount) {
                        // 念のため、出庫指示情報リストを再取得して確認
                        this.trnDeliveryItemList = warehouseModel.findDeliveryItem(pickingId, this.areaName, this.deliveryRule);
                        if (!this.trnDeliveryItemList.isEmpty()) {
                            logger.info("Undelivered items exists.");
                            // 出庫されていない
                            this.currentItem = 0;
                            this.itemCount = trnDeliveryItemList.size();
                        }
                    }
                }

                // 現在の作業数が払出作業総数に満たない場合
                if (this.currentItem < this.itemCount) {
                    // 出庫指示情報リストを使用し、次の情報を表示する
                    this.remaining = (this.currentItem + 1) + "/" + this.itemCount;
                    this.trnDeliveryItem = null;
                    this.trnDeliveryItem = this.trnDeliveryItemList.get(currentItem);

                    // 出庫情報詳細追加
                    setDeliveryItem(this.trnDeliveryItem);
                    // フォーカスをバーコードに移動
                    transitionFocus(FOCUS_BARCODE);

                } else if (this.currentItem >= this.itemCount) {
                    // 次の出庫指示番号の入力を要求する
                    this.nextPickingId();

                    //if (PickingMode.ISSUE.equals(this.mode)) {
                    //    this.goBackPickKanban();
                    //    return;
                    //}
                }

            } else {
                // 資材番号・現品票による出庫の場合、次の出庫指示番号の入力を要求する
                this.nextPickingId();
            }

            // 出庫数フィールドを無効化
            this.stockOutValDisable = true;

        } else {
            // 登録失敗
            this.readInfoMsg = LocaleUtils.getString("warehouse.errMsgE004", this.getLanguage());
            // フォーカスを出庫指示番号に移動
            PrimeFaces.current().executeScript("focusPickingIdInfoForm();");
            PrimeFaces.current().executeScript("soundPlay();");
            PrimeFaces.current().executeScript("scrollToBottom();");
        }

        this.checkStatus();
    }

    /**
     * シリアル番号一覧を生成する。
     */
    private void listSerialNo() {
        this.orderNoPrefix = this.trnMaterial.getOrderNo();

        if (!StringUtils.isEmpty(this.trnMaterial.getOrderNo()) && this.trnMaterial.getOrderNo().length() > 5) {

            List<LogStock> logStocks = this.warehouseModel.findLogStock(this.trnMaterial.getMaterialNo(), WarehouseEvent.LEAVE.getId());

            List<String> serialNoList = new ArrayList<>();
            logStocks.stream().forEach(o -> {
                if (!StringUtils.isEmpty(o.getOrderNo()) && o.getOrderNo().length() > 5) {
                    String[] values = o.getOrderNo().substring(6).split(",");
                    serialNoList.addAll(Arrays.asList(values));
                }
            });
        
            try {
                String value[] = this.trnMaterial.getOrderNo().split("-");
                if (value.length >= 3) {
                    int start = Integer.parseInt(value[value.length - 2]);
                    int end = Integer.parseInt(value[value.length - 1]);
                    String format = "%0" + String.valueOf(value[value.length - 1].length()) + "d";
                    for (; start <= end; start++) {
                        String serialNo = String.format(format, start);
                        if (!serialNoList.contains(serialNo)) {
                            this.orderNoList.add(new SelectItem(serialNo, serialNo));
                        }
                    }
                    if (orderNoList.size() > 1) {
                        this.orderNoPrefix = this.trnMaterial.getOrderNo().substring(0, 5);
                    } else {
                        this.orderNoList.clear();
                    }
                }
            } catch (NumberFormatException e) {
            }
        }        
    }

    /**
     * 次の出庫指示番号の入力を要求する。
     */
    private void nextPickingId() {
        initPickingInf();
        initItemInf();
        initStockOutInfo();

        this.renderedUserId = true;
        
        // 資材情報フレーム非表示
        styleItemInfo = "disp-none";
        // 出庫情報フレーム非表示
        styleStockOutForm = "disp-none";

        // バーコードの無効化
        this.barcodeDisable = true;
        
        if (WarehouseEvent.LEAVE.getId().equals(eventId) && DeliveryRule.ISSUE.equals(this.deliveryRule)) {
            TrnDelivery _delivery = this.warehouseModel.findDelivery(this.selectedKanban.getDeliveryNo());
            if (!(DeliveryStatusEnum.PICKED.equals(_delivery.getStatus()) 
                    || DeliveryStatusEnum.COMPLETED.equals(_delivery.getStatus()))) {
                this.warehouseModel.updateDeliveryStatus(this.selectedKanban.getDeliveryNo(), DeliveryStatusEnum.SUSPEND);
            }
            
            // ピッキングが完了しました。
            this.operationGuide = LocaleUtils.getString("warehouse.infoMsgI002", this.getLanguage());
            this.onBackPickKanban();
            return;
        }

        // 「次の出庫指示番号を入力して下さい。」を表示
        this.readInfoMsg = LocaleUtils.getString("warehouse.infoMsgI001", this.getLanguage());
        // フォーカスを出庫指示番号に移動
        transitionFocus(FOCUS_PICKING_ID);
    }
    
    /**
     * 次へボタン クリック時処理
     */
    public void postNextRequest() {
        clearMessage();
        //「読取情報エラーメッセージ」フレーム内・「数情報」フレーム内の情報をクリア
        clearItemInfo();

        // 現在の作業件数更新
        currentItem++;
        // 無効化切り替え
        this.stockOutValDisable = true;

        // 出庫状況確認
        for (int cnt = currentItem; cnt < itemCount; cnt++) {
            if (Objects.equals(trnDeliveryItemCompleteInfoList.get(cnt), STATUS_DELIVERED)) {
                currentItem++;
            } else {
                break;
            }
        }
        
        if (this.currentItem >= this.itemCount) {
            // 最後まで来たので、出庫されていない資材が存在しないか確認
            for (int j = 0; j < this.itemCount; j++) {
                if (Objects.equals(this.trnDeliveryItemCompleteInfoList.get(j), STATUS_UNDELIVERED)) {
                    // 出庫されていない
                    this.currentItem = j;
                    break;
                }
            }

            if (this.currentItem >= this.itemCount) {
                // 念のため、出庫指示情報リストを再取得して確認
                this.trnDeliveryItemList = warehouseModel.findDeliveryItem(pickingId, this.areaName, this.deliveryRule);
                if (!this.trnDeliveryItemList.isEmpty()) {
                    logger.info("Undelivered items exists.");
                    // 出庫されていない
                    this.currentItem = 0;
                    this.itemCount = trnDeliveryItemList.size();
                }
            }
        }
                
        // 現在の作業数が払出作業総数に満たない場合
        if (currentItem < itemCount) {

            // 出庫指示情報リストを使用し、次の情報を表示する
            this.remaining = (this.currentItem + 1) + "/" + this.itemCount;
            this.trnDeliveryItem = null;
            this.trnDeliveryItem = trnDeliveryItemList.get(currentItem);
            setDeliveryItem(this.trnDeliveryItem);

            transitionFocus(FOCUS_BARCODE);

        } else if (currentItem >= itemCount) {
            //現在の作業数が払出作業総数に満たす場合
            initPickingInf();
            initItemInf();
            initStockOutInfo();
            // 資材情報フレーム非表示
            styleItemInfo = "disp-none";
            // 出庫情報フレーム非表示
            styleStockOutForm = "disp-none";
            // 出庫指示番号へフォーカスを移動
            PrimeFaces.current().executeScript("focusPickingIdInfoForm();");
        }
        checkStatus();
    }

    /**
     * 資材情報詳細 取得
     *
     * @return
     */
    public ArrayList<ItemData> getAdditionData() {
        return additionData;
    }

    /**
     * 資材情報詳細 データ追加
     */
    private void addAdditonData(String name, String value) {
        ItemData data = new ItemData(name, value);
        this.additionData.add(data);
    }

    /**
     * バーコード無効化の設定 取得
     *
     * @return
     */
    public Boolean getBarcodeCompleted() {
        return barcodeDisable;
    }

    /**
     * 取り扱い注意エリア 取得
     *
     * @return
     */
    public String getItemInfoAreaStyle() {
        return itemInfoAreaStyle;
    }

    /**
     * 取り扱い注意部品警告 取得
     *
     * @return
     */
    public String getAttentionMsg() {
        return attentionMsg;
    }

    /**
     * 出庫数無効化の設定 取得
     *
     * @return
     */
    public Boolean getStockOutValCompleted() {
        return stockOutValDisable;
    }

    /**
     * メッセージ初期化
     *
     * @return
     */
    private void clearMessage() {
        this.userAreaMsg = "";
        this.renderedUserErrMsg = false;
        this.readInfoMsg = "";
        this.renderedAlertWarnMsg = false;
        this.operationGuide = "";
    }

    /**
     * バーコード読込情報初期化
     *
     * @return
     */
    private void clearItemInfo() {
        this.shelfId = "";
        this.additionData.clear();
        this.materialNo = "";
        this.stockVal = 0;
        this.stockOutVal = 0;
        this.renderedAlertWarnMsg = false;
        this.itemInfoAreaStyle = Constants.NORMAL_STYLE;
        this.attentionMsg = "";
        this.checkResult = "";
    }

    /**
     * 出庫情報を設定する。
     */
    private void setDeliveryItem(TrnDeliveryItem trnDeliveryItem) {
        this.clearItemInfo();

        if (WarehouseEvent.LEAVE.getId().equals(eventId) && DeliveryRule.ISSUE.equals(this.deliveryRule)) {
            Optional<TrnReserveMaterial> opt = trnDeliveryItem.getReserveMaterials().stream()
                    .filter(o -> o.getReservedNum() > 0 && o.getReservedNum() > o.getDeliveryNum())
                    .findFirst();

            if (opt.isPresent()) {
                TrnReserveMaterial reserveMaterial = opt.get();
                this.addAdditonData(LocaleUtils.getString("warehouse.supplyNo", this.getLanguage()), reserveMaterial.getMaterial().getSupplyNo());
                this.stockVal = trnDeliveryItem.getRequiredNum() - (Objects.nonNull(trnDeliveryItem.getDeliveryNum()) ? trnDeliveryItem.getDeliveryNum() : 0);
                this.stockOutVal = reserveMaterial.getReservedNum();
                this.shelfId = reserveMaterial.getMaterial().getLocation().getLocationNo();
            }

        } else {
            
            this.materialNo = trnDeliveryItem.getMaterialNo();
            this.shelfId = trnDeliveryItem.getGuideLocationNo();

            // 要求数
            Integer deliveryNum = trnDeliveryItem.getDeliveryNum();
            if (!Objects.equals(deliveryNum, 0)) {
                this.stockVal = trnDeliveryItem.getRequiredNum() - deliveryNum;
            } else {
                this.stockVal = trnDeliveryItem.getRequiredNum();
            }

            // 出庫数
            this.stockOutVal = 0;
        }

        // 明細番号
        this.itemNo = trnDeliveryItem.getPK().getItemNo();

        MstProduct product = this.trnDeliveryItem.getProduct();
            
        // 資材情報詳細
        Map<String, String> properties = trnDeliveryItem.getProperty();
        for (Map.Entry<String, String> entry : this.materialItems.entrySet()) {
            if (StringUtils.equals(entry.getKey(), Constants.PRODUCT_NO)) {
                this.addAdditonData(entry.getValue(), product.getProductNo());

            } else if (StringUtils.equals(entry.getKey(), Constants.PRODUCT_NAME)) {
                this.addAdditonData(entry.getValue(), product.getProductName());
                
            } else if (StringUtils.equals(entry.getKey(), Constants.FIGURE_NO)) {
                this.addAdditonData(entry.getValue(), product.getFigureNo());

            } else if (StringUtils.equals(entry.getKey(), Constants.ORDER_NO)) {
                // 出荷払出の場合、製番を出荷Noに置き換え
                this.addAdditonData(WarehouseEvent.LEAVE.getId().equals(this.eventId) ? entry.getValue() : LocaleUtils.getString("warehouse.shippingNo", this.getLanguage()), trnDeliveryItem.getOrderNo());
                
            } else if (Objects.nonNull(properties) && properties.containsKey(entry.getKey())) {
                String value = properties.get(entry.getKey());
                this.addAdditonData(entry.getValue(), value);
            }
        }
        
        // 重要度 (WarehouseBean にもコード有り)
        if (Objects.nonNull(product.getImportantRank())) {
            switch (product.getImportantRank()) {
                case 1: // 取り扱い注意品
                    this.itemInfoAreaStyle = Constants.IMPORTANT_STYLE;
                    this.attentionMsg = LocaleUtils.getString("warehouse.importantItem", this.getLanguage());
                    break;

                case 2: // 要検査品
                    if (Objects.nonNull(this.trnMaterial)) {
                        if (Objects.isNull(this.trnMaterial.getInspectedAt())) {
                            // 要検査
                            this.itemInfoAreaStyle = Constants.INSPECTION_STYLE;
                            this.attentionMsg = LocaleUtils.getString("warehouse.insepectionRequired", this.getLanguage());
                        } else {
                            // 検査済み
                            this.itemInfoAreaStyle = Constants.IMPORTANT_STYLE;
                            this.attentionMsg = LocaleUtils.getString("warehouse.inspected", this.getLanguage());
                        }
                        break;
                    }

                case 0: // 通常
                default:
                    this.itemInfoAreaStyle = Constants.NORMAL_STYLE;
                    this.attentionMsg = "";
                    break;
             }
        }
    }

    /**
     * 表示アイテム情報を読み込む
     */
    private void LoadConfig() {
        try {
            String values = config.getMaterialItems();
            this.materialItems = JsonUtils.jsonToMap(values);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.materialItems = new HashMap<>();
        }
    }

    /**
     * 社員情報エラーメッセージ 取得
     *
     * @return
     */
    public String getUserAreaMsg() {
        return userAreaMsg;
    }

    /**
     * 読取情報エラーメッセージ 取得
     *
     * @return
     */
    public String getReadInfoMsg() {
        return Objects.nonNull(readInfoMsg) ? readInfoMsg : "";
    }

    /**
     * オペレーションガイドを取得する。
     * 
     * @return オペレーションガイド
     */
    public String getOperationGuide() {
        return Objects.nonNull(this.operationGuide) ? operationGuide : "";
    }
     
    /**
     * バーコード入力可否フラグ 取得
     *
     * @return
     */
    public boolean isBarcodeDisable() {
        return barcodeDisable;
    }

    /**
     * 出庫表示可否フラグ 取得
     *
     * @return
     */
    public boolean isStockOutValDisable() {
        return stockOutValDisable;
    }

    /**
     * 出庫情報入力可否フラグ 取得
     *
     * @return
     */
    public boolean isStockOutDisable() {
        return stockOutDisable;
    }

    /**
     * 社員情報エラーメッセージ 表示可否フラグ 取得
     *
     * @return
     */
    public boolean isRenderedUserErrMsg() {
        return renderedUserErrMsg;
    }

    /**
     * 資材情報フォームスタイル 取得
     *
     * @return
     */
    public String getStyleItemInfo() {
        return styleItemInfo;
    }

    /**
     * 出庫情報フォームスタイル 取得
     *
     * @return
     */
    public String getStyleStockOutForm() {
        return styleStockOutForm;
    }

    /**
     * バーコード入力を表示するかを返す。
     * 
     * @return true:表示、false:非表示
     */
    public boolean isRenderedBarcode() {
        return renderedBarcode;
    }

    /**
     * 要求数 表示可否フラグ　取得
     *
     * @return
     */
    public boolean isRenderedStockVal() {
        return renderedStockVal;
    }

    /**
     * 警告 表示可否フラグ　取得
     *
     * @return
     */
    public boolean isRenderedAlertWarnMsg() {
        return renderedAlertWarnMsg;
    }

    /**
     * 次ボタン有効無効フラグ 取得
     *
     * @return
     */
    public boolean isButtonNextDisable() {
        return buttonNextDisable;
    }

    /**
     * フォーカスを遷移する。
     * 
     * @param name 要素名
     */
    @Override
    public void transitionFocus(String name) {
        switch (name) {

            case (FOCUS_USER_ID):
                PrimeFaces.current().executeScript("focusUserInfoForm();");
                break;

            case (FOCUS_PICKING_ID):
                if (StringUtils.isEmpty(this.userName)) {
                    //社員照会未実施の場合：社員番号にフォーカス遷移
                    PrimeFaces.current().executeScript("focusUserInfoForm();");
                } else {
                    if (DeliveryRule.NORMAL.equals(this.deliveryRule)) {
                        PrimeFaces.current().executeScript("focusPickingIdInfoForm();");
                        break;
                    }
                    PrimeFaces.current().executeScript("focusBarcodeForm();");
                }
                break;

            case (FOCUS_BARCODE):
                if (StringUtils.isEmpty(this.userName)) {
                    //社員照会未実施の場合：社員番号にフォーカス遷移
                    PrimeFaces.current().executeScript("focusUserInfoForm();");
                } else if (DeliveryRule.NORMAL.equals(this.deliveryRule) && StringUtils.isEmpty(this.pickingId)) {
                    //出庫指示番号照会未実施の場合：出庫指示番号にフォーカス遷移
                    PrimeFaces.current().executeScript("focusPickingIdInfoForm();");
                } else {
                    PrimeFaces.current().executeScript("focusBarcodeForm();");
                }
                break;

            case (FOCUS_STOCKOUT):
                if (StringUtils.isEmpty(this.userName)) {
                    //社員照会未実施の場合：社員番号にフォーカス遷移
                    PrimeFaces.current().executeScript("focusUserInfoForm();");
                } else if (DeliveryRule.NORMAL.equals(this.deliveryRule) && StringUtils.isEmpty(this.pickingId)) {
                    //出庫指示番号照会未実施の場合：出庫指示番号にフォーカス遷移
                    PrimeFaces.current().executeScript("focusPickingIdInfoForm();");
                } else if (StringUtils.isEmpty(this.materialNo)
                        && Objects.equals(this.barcodeDisable, false)) {
                    //バーコード未入力かつバーコード入力可の場合：バーコードにフォーカス遷移
                    PrimeFaces.current().executeScript("focusBarcodeForm();");
                } else {
                    PrimeFaces.current().executeScript("focusStockOutValInput();");
                }
                break;
            
            default:
                break;
        }
    }

    /**
     * 出庫操作が可能か確認する。
     */
    private void checkStatus() {
        setNextButtonDisable();
        setRegistButtonDisable();
    }
    
    /**
     * 受入検査を確認する。
     */
    private void checkInsepaction() {
        if (!this.stockOutDisable) {
            if (Objects.nonNull(this.trnMaterial)
                    && Objects.equals(this.trnMaterial.getProduct().getImportantRank(), 2) 
                    && Objects.isNull(this.trnMaterial.getInspectedAt())) {
                // 受入検査が終わっていない
                this.stockOutDisable = true;
                PrimeFaces.current().executeScript("customAlert('" + LocaleUtils.getString("warehouse.insepectionWarning", this.getLanguage()) + "','" + FOCUS_BARCODE +"')");
                PrimeFaces.current().executeScript("soundPlay();");
            }
        }
    }

    /**
     * 次へボタン 活性・非活性 設定
     */
    private void setNextButtonDisable() {
        // 出庫総数2件以上のとき
        this.buttonNextDisable = (itemCount < 2);
    }

    /**
     * 登録ボタン 活性・非活性 設定
     */
    private void setRegistButtonDisable() {
        // 社員番号入力済み・出庫指示入力済み
        // 出庫数が0以上・バーコード入力不可 又は バーコード入力あり
        this.stockOutDisable = (userName.isEmpty() || pickingId.isEmpty()
                || (!barcodeDisable && StringUtils.isEmpty(materialNo)) || stockOutVal.equals(0));

        if (!this.stockOutDisable) {
            // 受入検査が終わっていない
            if (Objects.nonNull(this.trnMaterial) 
                    && Objects.equals(this.trnMaterial.getProduct().getImportantRank(), 2) 
                    && Objects.isNull(this.trnMaterial.getInspectedAt())) {
                this.stockOutDisable = true;
            }
        }
    }

    /**
     * 区画名を取得する。
     * 
     * @return 区画名
     */
    @Override
    public String getAreaName() {
        return areaName;
    }

    public Date getPickingDate() {
        return pickingDate;
    }

    public void setPickingDate(Date  pickingDate) {
        this.pickingDate = pickingDate;
    }

    /**
     * 製造番号(前半部分)を取得する。
     * 
     * @return 製造番号(前半部分)
     */
    public String getOrderNoPrefix() {
        return this.orderNoPrefix;
    }

    /**
     * 製造番号一覧を取得する。
     * 
     * @return 製造番号一覧
     */
    public List<SelectItem> getOrderNoList() {
        return this.orderNoList;
    }

    /**
     * 製造番号を取得する。
     * 
     * @return 製造番号一覧
     */
    public List<String> getSelectedOrderNo() {
        return selectedOrderNo;
    }

    /**
     * 製造番号を設定する。
     * 
     * @param selectedOrderNo 製造番号一覧
     */
    public void setSelectedOrderNo(List<String> selectedOrderNo) {
        this.selectedOrderNo = selectedOrderNo;
    }
    
    /**
     * 製造番号が変更された。
     */
    public void orderNoChanged(){
        if (!this.selectedOrderNo.isEmpty()) {
            Collections.sort(this.selectedOrderNo);

            StringBuilder sb = new StringBuilder();
            sb.append(this.orderNoPrefix).append("-");
            sb.append(String.join(",", this.selectedOrderNo));

            this.printOrderNo = sb.toString();

            this.createPrintUrl(this.trnMaterial, this.printStockOut, this.printOrderNo, this.areaName, null, this.userId);
        }
    }
  
    /**
     * 印刷ダイアログを非表示にする。
     * 
     * @param hide true: 非表示
     */
    @Override
    public void setHiddenDialog(boolean hide) {
        super.setHiddenDialog(hide);
        //this.transitionFocus(FOCUS_PICKING_ID);
    }

    /**
     * 社員番号を表示性を返す。
     * 
     * @return 
     */
    public boolean isRenderedUserId() {
        return renderedUserId;
    }

    /**
     * 印刷ダイアログを閉じる。
     */
    @Override
    public void closePrintDialog() {
        super.closePrintDialog();

        if (this.isPrint() && this.getPrintReqResult()) {
            this.regist();
        }
    }

    /**
     * 画面タイトルを取得する。
     * 
     * @return 画面タイトル
     */
    public String getTitle() {
        switch (WarehouseEvent.valueOf(eventId)) {
            case LEAVE:
                return LocaleUtils.getString("warehouse.menu.picking", this.getLanguage());
            case SHIPPING:
                return LocaleUtils.getString("warehouse.menu.shipping", this.getLanguage());
            case DELIVERY:
                return LocaleUtils.getString("warehouse.menu.delivery", this.getLanguage());
        }
        return ""; 
    }
    
    /**
     * イベントIDを取得する。
     * 
     * @return 30: 出庫、31: 出荷払出
     */
    public Short getEventId() {
        return eventId;
    }
    
    /**
     * ラベルを印刷するかどうかを返す。
     * 
     * @return true: 印刷する、false: 印刷しない 
     */
    @Override
    public boolean isPrint() {
        return WarehouseEvent.LEAVE.getId().equals(eventId) ? super.isPrint() : false;
    }

    /**
     * ピッキングを開始する。
     */
    public void onStart() {
        try {
            Response response = this.warehouseModel.updateDeliveryStatus(this.selectedKanban.getDeliveryNo(), DeliveryStatusEnum.WORKING);
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                // データの更新に失敗しました
                this.readInfoMsg = LocaleUtils.getString("warehouse.errMsgE004", this.getLanguage());

                PrimeFaces.current().executeScript("soundPlay();");
                PrimeFaces.current().executeScript("scrollToBottom();");
                return;
            }

            this.setPickingId(this.selectedKanban.getDeliveryNo());
            
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String contextPath = facesContext.getExternalContext().getRequestContextPath();
            facesContext.getExternalContext().redirect(contextPath + "/warehouse/PickingScreen.xhtml");	
        } catch (IOException|JSONException ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * ピッキング画面を終了する。
     */
    public void exitPickingScreen() {
        logger.info("exitPage");
        try {
            if (DeliveryRule.ISSUE.equals(this.deliveryRule) && Objects.nonNull(this.selectedKanban)) {
                TrnDelivery delivery = this.warehouseModel.findDelivery(this.selectedKanban.getDeliveryNo());

                if (Objects.nonNull(delivery.getDeliveryRule())
                        && DeliveryStatusEnum.WORKING.equals(delivery.getStatus())) {
                    Response response = this.warehouseModel.updateDeliveryStatus(this.selectedKanban.getDeliveryNo(), DeliveryStatusEnum.SUSPEND);
                    if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                        this.readInfoMsg = LocaleUtils.getString("warehouse.errMsgE004", this.getLanguage());

                        PrimeFaces.current().executeScript("soundPlay();");
                        PrimeFaces.current().executeScript("scrollToBottom();");
                        return;
                    }

                }
            }

            FacesContext facesContext = FacesContext.getCurrentInstance();
            String contextPath = facesContext.getExternalContext().getRequestContextPath();
            if (DeliveryRule.ISSUE.equals(this.deliveryRule)) {
                facesContext.getExternalContext().redirect(contextPath + "/warehouse/PickKanbanScreen.xhtml");
            } else {
                facesContext.getExternalContext().redirect(contextPath + "/warehouse/index.xhtml");
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 払出指示リストをユニットで絞り込む。
     * 
     * @param event 
     */
    public void onSelectUnit(SelectEvent event) {
        this.updateKanbans(this.selectedUnitNo);
        this.showUnitTable = false;
    }

    /**
     * 払出指示リストを更新する。
     */
    public void updateAll() {
        this.showUnitTable = false;
        this.selectedUnitNo = LocaleUtils.getString("warehouse.selectUnit", this.getLanguage());
        this.updateKanbans(null);
    }
    
    /**
     * 払出指示リストを更新する。
     * 
     * @param unitNo ユニットNo
     */
    public void updateKanbans(String unitNo) {
        try {
            logger.info("update start.");
           
            DeliveryCondition condition = new DeliveryCondition();
            
            if (this.eventId == 32) {
                // 配達の場合
                condition.setStatuses(Arrays.asList(DeliveryStatusEnum.PICKED, DeliveryStatusEnum.SUSPEND));
                condition.setDeliveryRule(2);
            } else {
                condition.setStatuses(Arrays.asList(DeliveryStatusEnum.WAITING, DeliveryStatusEnum.WORKING, DeliveryStatusEnum.SUSPEND));
            }
            
            if (!StringUtils.isEmpty(unitNo)) {
               condition.setUnitNo(unitNo);
               condition.setExactMatch(true);
            }
            
            this.selectedKanban = null;
            this.kanbans = this.warehouseModel.searchDeliveryRange(condition, null, null, null);
         
            // ステータス順に並べ替
            Collections.sort(this.kanbans, (TrnDelivery o1, TrnDelivery o2) -> o1.getStatus().getSortKey().compareTo(o2.getStatus().getSortKey()));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("update end.");
        }
    }

    /**
     * すべてのユニットNoを取得する。
     * 
     * @return ユニットNo一覧
     */
    public List<String> getUnitAll() {
        return this.warehouseModel.findUnitAll(this.eventId == 32);
    }

    /**
     * 払出指示リストを取得する。
     * 
     * @return 払出指示リスト
     */
    public List<TrnDelivery> getKanbans() {
        return kanbans;
    }

    /**
     * 選択中の払出指示を取得する。
     * 
     * @return 
     */
    public TrnDelivery getSelectedKanban() {
        return selectedKanban;
    }
    
    /**
     * 払出指示リストから払出指示が選択された。
     * 
     * @param event イベント
     */
    public void onSelectKanban(SelectEvent event) {
        logger.info("onRowSelect: " + event);
        try {
            this.userAreaMsg = "";
            this.renderedUserErrMsg = false;
            this.readInfoMsg = "";
            
            if (StringUtils.isEmpty(this.userName)) {
                // 社員番号を入力して下さい
                this.userAreaMsg = LocaleUtils.getString("warehouse.M0001", this.getLanguage());
                this.renderedUserErrMsg = true;

                PrimeFaces.current().executeScript("focusUserInfoForm();");
                PrimeFaces.current().executeScript("soundPlay();");
                return;
            }

            this.selectedKanban = (TrnDelivery) event.getObject();

            FacesContext facesContext = FacesContext.getCurrentInstance();
            String contextPath = facesContext.getExternalContext().getRequestContextPath();

            switch (WarehouseEvent.valueOf(eventId)) {
                default:
                case LEAVE:
                    // ピッキング
                    facesContext.getExternalContext().redirect(contextPath + "/warehouse/PickingStartScreen.xhtml");
                    break;
                case DELIVERY:
                    // 在庫払出
                    facesContext.getExternalContext().redirect(contextPath + "/warehouse/DeliveryScreen.xhtml");
                    break;
            }
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 払出指示リスト画面に戻る。
     */
    public void onBackPickKanban() {
        logger.info("onBackPickKanban");
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String contextPath = facesContext.getExternalContext().getRequestContextPath();
            facesContext.getExternalContext().redirect(contextPath + "/warehouse/PickKanbanScreen.xhtml");	
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 払出指示番号を取得する。
     * 
     * @return 払出指示番号
     */
    public String getDeliveryNo() {
        return Objects.nonNull(this.selectedKanban) ? selectedKanban.getDeliveryNo() : "";
    }
    
    /**
     * 払出指示番号を設定する。
     * 
     * @param deliveryNo 払出指示番号
     */
    public void setDeliveryNo(String deliveryNo) {
        if (!StringUtils.isEmpty(deliveryNo)) {

            Optional<TrnDelivery> opt = this.kanbans.stream()
                    .filter(o -> deliveryNo.equals(o.getDeliveryNo()))
                    .findFirst();

            if (opt.isPresent()) {
                try {
                    this.userAreaMsg = "";
                    this.renderedUserErrMsg = false;
                    this.readInfoMsg = "";
                    this.operationGuide = "";

                    if (StringUtils.isEmpty(this.userName)) {
                        // 社員番号を入力して下さい
                        this.userAreaMsg = LocaleUtils.getString("warehouse.M0001", this.getLanguage());
                        this.renderedUserErrMsg = true;

                        PrimeFaces.current().executeScript("focusUserInfoForm();");
                        PrimeFaces.current().executeScript("soundPlay();");
                        return;
                    }

                    this.selectedKanban = opt.get();

                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    String contextPath = facesContext.getExternalContext().getRequestContextPath();
                    
                    switch (WarehouseEvent.valueOf(eventId)) {
                        default:
                        case LEAVE:
                            // ピッキング
                            facesContext.getExternalContext().redirect(contextPath + "/warehouse/PickingStartScreen.xhtml");
                            break;
                        case DELIVERY:
                            // 在庫払出
                            facesContext.getExternalContext().redirect(contextPath + "/warehouse/DeliveryScreen.xhtml");
                            break;
                    }
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
    }

    /**
     * 機種名を取得する。
     * 
     * @return 機種名
     */
    public String getModelName() {
        return Objects.nonNull(this.selectedKanban) ? (Objects.nonNull(selectedKanban.getModelName()) ? selectedKanban.getModelName() : "") : "";
    }

    /**
     * ユニット番号を取得する。
     * 
     * @return ユニット番号
     */
    public String getUnitNo() {
        return Objects.nonNull(this.selectedKanban) ? (Objects.nonNull(selectedKanban.getUnitNo()) ? selectedKanban.getUnitNo() : "") : "";
    }

    /**
     * 作業日を取得する。
     * 
     * @return 作業日
     */
    public String getWorkDate() {
        return Objects.nonNull(this.selectedKanban) ? selectedKanban.getDueDateLong() : "";
    }

    /**
     * ステータスを取得する。
     * 
     * @return 
     */
    public String getStatus() {
        return Objects.nonNull(this.selectedKanban) ? selectedKanban.getDisplayStatus(this.getLanguage()) : "";
    }

    /**
     * ユニットNoを取得する。
     * 
     * @return 
     */
    public String getSelectedUnitNo() {
        return selectedUnitNo;
    }

    /**
     * ユニットNoを設定する。
     * 
     * @param selectedUnitNo 
     */
    public void setSelectedUnitNo(String selectedUnitNo) {
        this.selectedUnitNo = selectedUnitNo;
    }

    /**
     * ユニットリストを表示する。
     * 
     */
    public void selectUnitNo() {
        this.showUnitTable = true;
    }

    /**
     * ユニットリストの表示性を取得する。
     * 
     * @return 
     */
    public boolean getShowUnitTable() {
        return showUnitTable;
    }

    /**
     * 動作モードを取得する。
     * 
     * @return 動作モード
     */
    public String getMode() {
        return deliveryRule.name();
    }
        
    /**
     * 単位を取得する。
     * 
     * @return 単位
     */
    public String getUnit() {
        return Objects.nonNull(this.trnDeliveryItem) ? (Objects.nonNull(this.trnDeliveryItem.getProduct().getUnit()) ? this.trnDeliveryItem.getProduct().getUnit() : "") : "";
    }

    public String getCheckResult() {
        return this.checkResult;
    }
    
    public void onDelivery() {
        logger.info("onDelivery start.");

        try {
            Response response = this.warehouseModel.doDelivery(this.selectedKanban.getDeliveryNo(), this.userId);
            if (response.getStatus() == HttpURLConnection.HTTP_OK) {
                // 払出が完了しました。
                this.operationGuide = LocaleUtils.getString("warehouse.infoMsgI003", this.getLanguage());
                this.onBackPickKanban();
                return;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        
        // 登録失敗
        this.readInfoMsg = LocaleUtils.getString("warehouse.errMsgE004", this.getLanguage());
        PrimeFaces.current().executeScript("soundPlay();");
        PrimeFaces.current().executeScript("scrollToBottom();");
    }

    public void openKitPrintDialog() {
        this.setCopies("1");
        this.setPrintLog("");
        this.openPrintDialog("KitPrintDialog", 800, 700);
    }
    
    public void printKitLabel() {
        try {
            LabelPrinter printer  = new LabelPrinter();
            printer.printKitLabel(
                    this.getBaseUrl(), 
                    this.warehouseModel.getServerAddress(), 
                    this.selectedKanban.getDeliveryNo(), 
                    this.selectedKanban.getUnitNo(), 
                    this.selectedKanban.getModelName(), 
                    this.userId,
                    Integer.parseInt(this.getCopies()));
           
        } catch (Exception ex) {
            this.setPrintLog(LocaleUtils.getString("warehouse.errMsgE015", this.getLanguage()));
            logger.fatal(ex, ex);
        }
    }
}
