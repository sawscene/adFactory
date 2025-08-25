/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.services;

import adtekfuji.clientservice.WarehouseInfoFaced;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.search.DeliveryCondition;
import jp.adtekfuji.adFactory.entity.warehouse.ReqWithdrawItem;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryInfo;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 出庫指示情報のインポート
 *
 * @author s-heya
 */
public class ImportDelivery {

    private final String SETTING_FILE_NAME = "pickinglist_setting.xml";
    public static final String MATERIAL = "Material";
    public static final String VENDOR = "Vendor";
    public static final String SPEC = "Spec";
    public static final String NOTE = "Note";
    private static final String UNIT_NO_REGEX = "^([0-9]|[1-9][0-9])$";     // ユニット番号
    private static final String SUPPLY_NO_REGEX = "..\\d{7}\\+\\d{3}(-\\d{3})?"; // 発注番号

    private final Logger logger = LogManager.getLogger();

    private final String workingRoot;
    private final String settingFilePath;

    /**
     * コンストラクタ
     */
    public ImportDelivery() {
        this.workingRoot = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator;
        this.settingFilePath = System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + SETTING_FILE_NAME;
    }

    /**
     * ピッキングリストをインポートする。
     * 
     * @param selected インポート対象ファイル
     * @return true: 
     */
    public List<TrnDeliveryInfo> importData(File selected) {
        try {
            logger.info("importData Start.");

            // 設定ファイルを読み込み
            List<String> paramList = Arrays.asList("unitNo", "prodNo", "suppyNo", "prodName", "spec", "material", "vendor", "reqNum", "unit");
            WarehouseCommonSettingInfo settingInfo = ImportUtility.readSetting(settingFilePath, paramList);

            if (Objects.isNull(settingInfo)) {
                logger.error("Read setting file failed.");
                return null;
            }

            List<List<String>> readData;
            if (WarehouseCommonSettingInfo.FORMAT_CSV.equalsIgnoreCase(settingInfo.getFormat())) {
                readData = ImportUtility.readCSV(selected.getPath(), settingInfo);
            } else {
                readData = ImportUtility.readExcel(selected.getPath(), settingInfo);
            }

            if (Objects.isNull(readData)) {
                throw new Exception("Read Excel file failed.");
            }
            
            List<ReqWithdrawItem> items = this.convertToObjects(selected.getName(), readData, settingInfo);
            if (Objects.isNull(items)) {
                throw new Exception("Convert to objects failed.");
            }

            if (items.isEmpty()) {
                return new ArrayList<>();
            }
            
            String filePath = workingRoot + "import" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".json";
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF8"))) {
                writer.write(JsonUtils.objectToJson(items));

            } catch (IOException ex) {
                logger.fatal(ex, ex);
            }

            WarehouseInfoFaced faced = new WarehouseInfoFaced();
            ResponseEntity response = faced.importDelivery(filePath);

            switch (response.getErrorType()) {
                case SUCCESS:
                    logger.info("Import Success.");
                    break;
                case NAME_OVERLAP:
                    // 既に払出指示が登録されています。
                    SceneContiner.getInstance().showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("importPickingList"), LocaleUtils.getString("overlapDelivery"));
                    return null;
                default:
                    throw new Exception("Import failed.");
                
            }
                        
            ReqWithdrawItem item = items.get(0);
           
            DeliveryCondition condition = new DeliveryCondition();
            condition.setModelName(item.getModelName());
            condition.setOrderNo(item.getOrderNo());
            List<TrnDeliveryInfo> deliveries = faced.searchDeliveryRange(condition, null, null);

            return deliveries;

        } catch (Exception ex) {
            logger.fatal(ex, ex);

            return null;
        }
    }

    /**
     * オブジェクトに変換する。
     *
     * @param fileName ファイル名
     * @param readData 読み込みデータ
     * @param settingInfo 設定ファイル
     * @return
     */
    private List<ReqWithdrawItem> convertToObjects(String fileName, List<List<String>> readData, WarehouseCommonSettingInfo settingInfo) {
        try {
            Date now = new Date();
            
            String[] array = fileName.split("-");
            if (array.length < 5) {
                return null;
            }

            String modelName = array[0];
            String beginSerial = array[3];
            String endSerial = array[4].substring(0, array[4].lastIndexOf("."));
            
            if (StringUtils.isEmpty(modelName) 
                    || StringUtils.isEmpty(beginSerial) 
                    || StringUtils.isEmpty(endSerial)) {
                return null;
            }
                        
            String orderNo = beginSerial.equals(endSerial) ? beginSerial : beginSerial + "-" + endSerial;

            List<ReqWithdrawItem> items = new ArrayList<>();
            String curUnitNo = "01";
            int itemNo = 1;

            for (int line = 0; line < readData.size(); line++) {
                ReqWithdrawItem item = new ReqWithdrawItem();
                
                String unitNo = null;
                String prodNo = null;
                String suppyNo = null;
                String prodName = "";
                String material = "";
                String spec = null;
                String vendor = null;
                String unit = null;
                Integer arrange = 0;
                String arrangeNo = "";
                String note = "";
                String locationNo = null;
                Integer reqNum = null;
                
                Pattern unitNoPattern = Pattern.compile(UNIT_NO_REGEX);
                Pattern supplyNoPattern = Pattern.compile(SUPPLY_NO_REGEX);

                for (int i = 0; i < settingInfo.getParameters().size(); i++) {
                    String key = settingInfo.getParameters().get(i).key;
                    Integer column = settingInfo.getParameters().get(i).column;

                    if (column > readData.get(line).size()) {
                        logger.error("The set column number is out of range.");
                        return null;
                    }

                    String val = readData.get(line).get(column - 1);
                    switch (key) {
                        case ("unitNo"):
                            if (!StringUtils.isEmpty(val)) {
                                String[] _val = val.split("-");
                                if (_val.length >= 1 && unitNoPattern.matcher(_val[0]).matches()) {
                                    unitNo = _val[0].length() == 1 ? "0" + _val[0] : _val[0];
                                    break;                                
                                }
                            }
                            break;
                        case ("prodNo"):
                            prodNo = val;
                            break;
                        case ("suppyNo"):
                            suppyNo = val;
                            break;
                        case ("prodName"):
                            if (!StringUtils.isEmpty(val)) {
                                prodName = val;
                            }
                            break;
                        case ("material"):
                            if (!StringUtils.isEmpty(val)) {
                                material = val;
                            }
                            break;
                        case ("spec"):
                            spec = val;
                            break;
                        case ("vendor"):
                            vendor = val;
                            break;
                        case ("unit"):
                            unit = val;
                            break;
                        case ("reqNum"):
                            if (!StringUtils.isEmpty(val)) {
                                try {
                                    reqNum = Integer.valueOf(val);
                                } catch (NumberFormatException ex) {
                                }
                            }
                            break;
                        case ("arrange"):
                            if (!StringUtils.isEmpty(val)) {
                                arrange = val.contains("在庫") ? 2 : 0;
                                note = val;
                                if (arrange == 2) {
                                    locationNo = val;
                                }
                            }
                            break;
                        case ("arrangeNo"):
                            if (!StringUtils.isEmpty(val) 
                                    && supplyNoPattern.matcher(val).matches()) {
                                arrange = 1;
                                arrangeNo = val;
                            }
                            break;
                        case ("note"):
                            break;
                        default:
                            break;
                    }
                }
                
                if ( (StringUtils.isEmpty(prodNo) && StringUtils.isEmpty(suppyNo))
                        || Objects.isNull(reqNum)
                        || (arrange == 0 && StringUtils.isEmpty(unitNo))) {
                    // 部品番号＝空白 かつ 購入品番号＝空白の場合
                    // 個数＝空白の場合
                    // 手配区分≠在庫品 かつ 組立工程＝空白の場合
                    continue;
                }

                if (!StringUtils.isEmpty(unitNo) && !StringUtils.equals(curUnitNo, unitNo)) {
                    curUnitNo = unitNo;
                    itemNo = 1;
                }
                
                String deliveryNo = modelName + "-" + orderNo + "-" + curUnitNo;
                //Map<String, String> map = new HashMap<>();
                //map.put(MATERIAL, material);
                //map.put(VENDOR, vendor);
                //map.put(SPEC, spec);
                //map.put(NOTE, note);
                //
                //MstProductInfo product = new MstProductInfo((StringUtils.isEmpty(prodNo) ? suppyNo : prodNo), prodName, now);
                //product.setProperty(map);

                item.setDeliveryNo(deliveryNo);
                item.setNo(itemNo);
                item.setUnitNo(modelName + "-" + curUnitNo);
                item.setOrderNo(orderNo);
                item.setProdNo(StringUtils.isEmpty(prodNo) ? suppyNo : prodNo);
                item.setProdName(prodName);
                item.setSpec(spec);
                item.setVendor(vendor);
                item.setUnit(unit);
                item.setReqNum(reqNum);
                item.setArrangeNo(arrangeNo);
                item.setModelName(modelName);
                item.setArrange(arrange);
                item.setDeliveryRule(2);
                item.setLocationNo(locationNo);

                items.add(item);
                
                itemNo++;
            }

            return items;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 必須項目確認
     *
     * @param settingInfo 設定情報
     * @return
     */
    private boolean verifyFormat(WarehouseCommonSettingInfo settingInfo) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < settingInfo.getParameters().size(); i++) {
            list.add(settingInfo.getParameters().get(i).key);
        }

        List<String> requiredParamList = Arrays.asList("unitNo", "prodNo", "suppyNo", "prodName", "spec", "material", "vendor", "reqNum", "unit");
        for (int i = 0; i < requiredParamList.size(); i++) {
            if (!list.contains(requiredParamList.get(i))) {
                return true;
            }
        }
        return false;
    }
}
