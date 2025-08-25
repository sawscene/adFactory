/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.warehouseplugin;

import adtekfuji.clientservice.WarehouseInfoFaced;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adinterfaceservice.entity.ReqStoreInItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 納入情報のインポート
 * @author 14-0282
 */
public class ImportReqStoreIn extends ImportExecutor {

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     *
     * @param root 読み込みファイルのルートパス
     * @param settingRoot 設定ファイルのルートパス
     * @param settingFileName 設定ファイル名
     * @param defaultFileName インポートファイル名
     */
    public ImportReqStoreIn(String root, String settingRoot, String settingFileName, String defaultFileName) {
        super(root, settingRoot, settingFileName, "req_storein.csv", Arrays.asList("supplyNo", "prodNo", "arrNum"));
    }
    
    /**
     * 読み取ったデータをJsonに変換する。
     *
     * @param readData 読み取ったデータ
     * @param settingInfo フォーマット情報
     * @return Json
     */
    @Override
    protected String doCconvert(List<List<String>> readData, WarehouseCommonSettingInfo settingInfo) {
        List<ReqStoreInItem> dataList = new ArrayList<>();

        try {
           for (int l = 0; l < readData.size(); l++) {
                ReqStoreInItem item = new ReqStoreInItem();
                item.setNo(1);

                for (int i = 0; i < settingInfo.getParameters().size(); i++) {
                    String key = settingInfo.getParameters().get(i).key;
                    Integer column = settingInfo.getParameters().get(i).column;

                    if (column > readData.get(l).size()) {
                        logger.error("ImportReqStoreIn: The set column number is out of range.");
                        return null;
                    }

                    String val = readData.get(l).get(column - 1);
                    switch (key) {
                        case "supplyNo":
                            if (StringUtils.isEmpty(val)) {
                                logger.error("ImportReqStoreIn: Required item(supplyNo) is empty.");
                                break;
                            }
                            item.setSupplyNo(val);
                            break;
                        case "orderNo":
                            item.setOrderNo(val);
                            break;
                        case "no":
                            if (StringUtils.isEmpty(val)) {
                                logger.warn("ImportReqStoreIn: Required item(no) is empty.");
                                val = "1";
                            }
                            item.setNo(Integer.valueOf(val));
                            break;
                        case "prodNo":
                            if (StringUtils.isEmpty(val)) {
                                logger.error("ImportReqStoreIn: Required item(prodNo) is empty.");
                                break;
                            }
                            item.setProdNo(val);
                            break;
                        case "prodName":
                            item.setProdName(val);
                            break;
                        case "arrNum":
                            if (StringUtils.isEmpty(val)) {
                                logger.error("ImportReqStoreIn: Required item(arrNum) is empty.");
                                break;
                            }
                            item.setArrNum(Integer.valueOf(val));
                            break;
                        case "arrPlan":
                            item.setArrPlan(val);
                            break;
                        case "mod":
                            if (!StringUtils.isEmpty(val)) {
                                item.setMod(Integer.valueOf(val));
                            }
                            break;
                        case "del":
                            if (!StringUtils.isEmpty(val)) {
                                item.setDel(Integer.valueOf(val));
                            }
                            break;
                        case "spec":
                            item.setSpec(val);
                            break;
                        case "vendor":
                            item.setVendor(val);
                           break;
                        case "note":
                            item.setNote(val);
                            break;
                        default:
                            break;
                    }
                }
                
                if (StringUtils.isEmpty(item.getSupplyNo()) 
                        || StringUtils.isEmpty(item.getProdNo())
                        || Objects.isNull(item.getArrNum())) {
                    continue;
                }

                if (!StringUtils.isEmpty(settingInfo.getQRCode())) {
                    String value = String.format(settingInfo.getQRCode(), item.getSupplyNo(), item.getNo());
                    item.setSupplyNo(value);
                    item.setNo(1);
                }

                item.setCategory(2);
                dataList.add(item);
            }
            
            return JsonUtils.objectToJson(dataList);

        } catch (NumberFormatException ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * インポートAPIを呼び出す。
     * 
     * @param jsonFilePath Jsonファイルパス
     * @return 処理結果
     */
    @Override
    protected ResponseEntity doImport(String jsonFilePath) {
        WarehouseInfoFaced faced = new WarehouseInfoFaced();
        return faced.importSupply(jsonFilePath);
    }
}
