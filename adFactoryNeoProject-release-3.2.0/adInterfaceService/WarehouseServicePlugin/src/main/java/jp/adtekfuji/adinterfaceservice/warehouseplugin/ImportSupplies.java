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
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adinterfaceservice.entity.ReqStoreInItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 支給品リストのインポート
 * 
 * @author s-heya
 */
public class ImportSupplies extends ImportExecutor {

    private final Logger logger = LogManager.getLogger();
    
    /**
     * コンストラクタ
     *
     * @param root 読み込みファイル ルートパス
     * @param settingRoot 設定ファイル ルートパス
     */
    public ImportSupplies(String root, String settingRoot) {
        super(root, settingRoot, "supplies_setting.xml", "supplies.csv", Arrays.asList("supplyNo", "orderNo", "prodNo", "prodName", "material", "vendor", "spec", "arrNum", "arrPlan"));
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
                for (int i = 0; i < settingInfo.getParameters().size(); i++) {
                    String key = settingInfo.getParameters().get(i).key;
                    Integer column = settingInfo.getParameters().get(i).column;

                    if (column > readData.get(l).size()) {
                        logger.error("ImportSupplies: The set column number is out of range.");
                        return null;
                    }

                    String val = readData.get(l).get(column - 1);
                    switch (key) {
                        // 倉庫オーダー番号
                        case "supplyNo":
                            if (StringUtils.isEmpty(val)) {
                                logger.error("ImportSupplies: Required item(supplyNo) is empty.");
                                return null;
                            }
                            item.setSupplyNo(val);
                            break;
                        // 製造オーダー番号
                        case "orderNo":
                            item.setOrderNo(val);
                            break;
                        // 品目
                        case "prodNo":
                            if (StringUtils.isEmpty(val)) {
                                logger.error("ImportSupplies: Required item(prodNo) is empty.");
                                return null;
                            }
                            item.setProdNo(val);
                            break;
                        // 品名
                        case "prodName":
                            item.setProdName(val);
                            break;
                        // 材質
                        case "material":
                            item.setProdName(val);
                            break;
                        // メーカー
                        case "vendor":
                            item.setProdName(val);
                            break;
                        // 規格・型式
                        case "spec":
                            item.setSpec(val);
                            break;
                        // 今回支給数
                        case "arrNum":
                            if (StringUtils.isEmpty(val)) {
                                logger.error("ImportSupplies: Required item(arrNum) is empty.");
                                return null;
                            }
                            item.setArrNum(Integer.valueOf(val));
                            break;
                        // 発行日
                        case "arrPlan":
                            if (StringUtils.isEmpty(val)) {
                                logger.error("ImportSupplies: Required item(arrPlan) is empty.");
                                return null;
                            }
                            StringBuilder sb = new StringBuilder();
                            sb.append(val.substring(0, 3));
                            sb.append("/");
                            sb.append(val.substring(4, 5));
                            sb.append("/");
                            sb.append(val.substring(6, 7));
                            item.setArrPlan(sb.toString());
                            break;
                        default:
                            break;
                    }
                }

                item.setNo(l);
                item.setCategory(1); // 手配区分:支給品
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
