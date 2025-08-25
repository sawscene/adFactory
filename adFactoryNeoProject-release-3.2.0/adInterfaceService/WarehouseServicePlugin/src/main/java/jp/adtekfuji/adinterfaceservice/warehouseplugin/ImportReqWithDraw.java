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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.warehouse.ReqWithdrawItem;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 出庫指示情報のインポート
 *
 * @author 14-0282
 */
public class ImportReqWithDraw extends ImportExecutor {

    public final String SETTING_FILE_NAME = "withdraw_setting.xml";

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     *
     * @param root 読み込みファイルのルートパス
     * @param settingRoot 設定ファイルのルートパス
     * @param settingFileName 設定ファイル名
     * @param defaultFileName インポートファイル名
     */
    public ImportReqWithDraw(String root, String settingRoot, String settingFileName, String defaultFileName) {
        super(root, settingRoot, settingFileName, "req_withdraw.csv", Arrays.asList("deliveryNo", "prodNo", "reqNum"));
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
        List<ReqWithdrawItem> dataList = new ArrayList<>();

        try {

            for (int l = 0; l < readData.size(); l++) {
                ReqWithdrawItem item = new ReqWithdrawItem();

                for (int i = 0; i < settingInfo.getParameters().size(); i++) {
                    String key = settingInfo.getParameters().get(i).key;
                    Integer column = settingInfo.getParameters().get(i).column;

                    if (column > readData.get(l).size()) {
                        logger.error("ImportReqWithDraw: The set column number is out of range.");
                        return null;
                    }

                    String val = readData.get(l).get(column - 1);
                    switch (key) {
                        case ("deliveryNo"):
                            if (StringUtils.isEmpty(val)) {
                                logger.warn("ImportReqWithDraw:Required item(deliveryNo) is empty.");
                                break;
                            }
                            item.setDeliveryNo(val);
                            break;
                        case ("no"):
                            if (StringUtils.isEmpty(val)) {
                                logger.warn("ImportReqWithDraw: Required item(no) is empty.");
                                break;
                            }
                            item.setNo(Integer.valueOf(val));
                            break;
                        case ("orderNo"):
                            item.setOrderNo(val);
                            break;
                        case ("type"):
                            item.setType(val);
                            break;
                        case ("prodNo"):
                            item.setProdNo(val);
                            break;
                        case ("prodName"):
                            item.setProdName(val);
                            break;
                        case ("reqNum"):
                            int reqNum = 0;
                            if (!StringUtils.isEmpty(val)) {
                                try {
                                    reqNum = (int)Double.parseDouble(val);
                                } catch (NumberFormatException e) {
                                    logger.fatal(e);
                                }
                            }
                            item.setReqNum(reqNum);
                            break;
                        case ("due"):
                            if (!StringUtils.isEmpty(val)) {
                                item.setDue(val);
                            }
                            break;
                        case ("mod"):
                            if (!StringUtils.isEmpty(val)) {
                                item.setMod(Integer.valueOf(val));
                            }
                            break;
                        case ("del"):
                            if (!StringUtils.isEmpty(val)) {
                                item.setDel(Integer.valueOf(val));
                            }
                            break;
                        case ("unitNo"):
                            item.setUnitNo(val);
                            break;
                        case ("modelName"):
                            item.setModelName(val);
                            break;
                        case ("loc"):
                            item.setLocationNo(val);
                            break;
                        case ("usageNum"):
                            int usageNum = 0;
                            if (!StringUtils.isEmpty(val)) {
                                try {
                                    usageNum = (int)Double.parseDouble(val);
                                } catch (NumberFormatException e) {
                                    logger.fatal(e);
                                }
                            }
                            item.setUsageNum(usageNum);
                            break;
                        default:
                            break;
                    }
                }
                
                if (StringUtils.isEmpty(item.getDeliveryNo())
                    || Objects.isNull(item.getNo())
                    || StringUtils.isEmpty(item.getProdNo())) {
                    continue;
                }

                dataList.add(item);
            }
   
            boolean exist = settingInfo.getParameters().stream()
                    .filter(o -> StringUtils.equals(o.key, "no"))
                    .findFirst()
                    .isPresent();
            
            if (!exist) {
                Map<String, List<ReqWithdrawItem>> map = dataList.stream()
                        .collect(Collectors.groupingBy(o -> o.getDeliveryNo()));

                for (List<ReqWithdrawItem> items :  map.values()) {
                    int ii = 1;
                    for (ReqWithdrawItem item : items) {
                        item.setNo(ii++);
                    }
                }
            }

            return JsonUtils.objectToJson(dataList);

        } catch (Exception ex) {
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
        return faced.importDelivery(jsonFilePath);
    }
}
