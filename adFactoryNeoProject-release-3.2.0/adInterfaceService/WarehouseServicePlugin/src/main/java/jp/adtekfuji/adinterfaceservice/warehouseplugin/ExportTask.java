/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.warehouseplugin;

import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WarehouseInfoFaced;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimerTask;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.warehouse.LogStockInfo;
import jp.adtekfuji.adFactory.enumerate.WarehouseEvent;
import jp.adtekfuji.adinterfaceservice.entity.ResInventoryItem;
import jp.adtekfuji.adinterfaceservice.entity.ResShippingItem;
import jp.adtekfuji.adinterfaceservice.entity.ResStoreInItem;
import jp.adtekfuji.adinterfaceservice.entity.ResStoreInTechsItem;
import jp.adtekfuji.adinterfaceservice.entity.ResWithdrawItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 実績出力タスク
 * 
 * @author s-heya
 */
public class ExportTask extends TimerTask {
    private static final Logger logger = LogManager.getLogger();
    
    private static final String FILE_NAME_SUPPLY = "res_supply.csv";
    private static final String FILE_NAME_STOCKIN1 = "res_storein_1.csv";
    private static final String FILE_NAME_STOCKIN2 = "res_storein_2.csv";
    private static final String FILE_NAME_WITHDRAW = "res_withdraw.csv";
    private static final String FILE_NAME_SHIPPING = "res_shipping.csv";
    private static final String FILE_NAME_INVENTORY = "stocklist.csv";
    private static final String FILE_EXT_LOCK = ".lock";


    private final WarehouseInfoFaced faced = new WarehouseInfoFaced();
    private final OrganizationInfoFacade organizationFacade = new OrganizationInfoFacade();
    private final Map<String, String> persons = new HashMap<>();

    /**
     * コンストラクタ
     */
    public ExportTask() {
    }

    /**
     * エクスポート処理を実行する。
     */
    @Override
    public void run() {
        logger.info("ExportTask start.");

        long startTime = System.currentTimeMillis();
        WarehouseConfig config = WarehouseConfig.getInstance();
        String exportPath = config.getExportPath(); // 入出庫実績出力先
        int max = config.getMaxExport(); // 実績の最大件数

        // 入出庫実績出力ファイルのフルパス
        File[] files = {
            new File(exportPath + File.separator + FILE_NAME_SUPPLY),
            new File(exportPath + File.separator + FILE_NAME_STOCKIN1),
            new File(exportPath + File.separator + FILE_NAME_STOCKIN2),
            new File(exportPath + File.separator + FILE_NAME_WITHDRAW),
            new File(exportPath + File.separator + FILE_NAME_SHIPPING),
            new File(exportPath + File.separator + FILE_NAME_INVENTORY)
        };
       
        // ロック後のファイルのフルパス
        File[] locks = {
            new File(exportPath + File.separator + FILE_NAME_SUPPLY + FILE_EXT_LOCK),
            new File(exportPath + File.separator + FILE_NAME_STOCKIN1 + FILE_EXT_LOCK),
            new File(exportPath + File.separator + FILE_NAME_STOCKIN2 + FILE_EXT_LOCK),
            new File(exportPath + File.separator + FILE_NAME_WITHDRAW + FILE_EXT_LOCK),
            new File(exportPath + File.separator + FILE_NAME_SHIPPING + FILE_EXT_LOCK),
            new File(exportPath + File.separator + FILE_NAME_INVENTORY + FILE_EXT_LOCK)
        };
        
        // 区画コードを取得
        Map<String, String> areaCodes = config.getAreaCodes();
        
        boolean locked = false;
        List<Long> eventIds = null;

        try {
            File folder = new File(exportPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            
            List<ResStoreInTechsItem> storeInFujiList = new LinkedList<>();
            List<ResStoreInItem> storeInList1 = new LinkedList<>();
            List<ResStoreInItem> storeInList2 = new LinkedList<>();
            List<ResWithdrawItem> withdrawList = new LinkedList<>();
            List<ResShippingItem> shippingList = new LinkedList<>();
            List<ResInventoryItem> inventoryList = new LinkedList<>();

            List<LogStockInfo> logStockList  = this.faced.findSyncedLogStock(false, max);
            logger.info("LogStock count: " + logStockList.size());
            if (logStockList.isEmpty()) {
                return;
            }
            
            for (LogStockInfo logStock : logStockList) {

                // 倉庫コードを取得
                String areaCode = areaCodes.get(logStock.getAreaName());
                if (StringUtils.isEmpty(areaCode)) {
                    areaCode = "0000";
                    logger.warn("Not found AreaCode: " + logStock.getAreaName());
                }
                
                switch (WarehouseEvent.valueOf(logStock.getEventKind())) {
                    case RECIVE: {       // 受入・入庫
                            switch (logStock.getCategory()) {
                                case 1:     // 支給品
                                    storeInFujiList.add(new ResStoreInTechsItem(logStock, areaCode));
                                    break;
                                case 2:     // 購入品
                                    storeInList1.add(new ResStoreInItem(logStock, areaCode));
                                    break;
                                case 3:     // 加工品
                                    storeInList2.add(new ResStoreInItem(logStock, areaCode));
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;

                    case LEAVE:             // 出庫
                        withdrawList.add(new ResWithdrawItem(logStock, areaCode));
                        break;
                        
                    case SHIPPING:          // 出荷払出
                        String name = this.persons.get(logStock.getPersonNo());
                        if (StringUtils.isEmpty(name)) {
                            // 担当者名を取得
                            OrganizationInfoEntity org = this.organizationFacade.findName(logStock.getPersonNo());
                            name = org.getOrganizationName();
                            this.persons.put(logStock.getPersonNo(), name);
                        }
                        
                        shippingList.add(new ResShippingItem(logStock, name, areaCode));
                        break;

                    case INVENTORY:         // 棚卸
                        inventoryList.add(new ResInventoryItem(logStock, areaCode));
                        break;
                }
            }

            // ファイルをロック
            logger.info("Lock file.");
            for (int ii = 0; ii < files.length; ii++) {
                if (!this.lockFile(files[ii], locks[ii])) {
                    logger.info("Failed to lock the file: " + files[ii]);
                    return;
                }
            }
            
            // 同期フラグを更新
            eventIds = logStockList.stream().map(o -> o.getEventId()).collect(Collectors.toList());
            this.faced.updateSyncedLogStock(eventIds, Boolean.TRUE);

            locked = true;
            
            // 支給品の入庫実績情報を出力
            if (!storeInFujiList.isEmpty()) {
                List<ResStoreInTechsItem> outputList;
                final int lockFile = 0;

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(locks[lockFile]), "SJIS"))) {
                    CsvToBeanBuilder<ResStoreInTechsItem> builder = new CsvToBeanBuilder<>(reader);
                    builder.withType(ResStoreInTechsItem.class);
                    builder.withSkipLines(1); // 1行目はスキップして読み込む
                    outputList = builder.build().parse();
                }

                // ファイルを初期化する
                locks[lockFile].createNewFile();
                
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(locks[lockFile]), "SJIS"))) {
                    writer.write("\"調整\",\"入庫担当者ｺｰﾄﾞ\",\"入庫担当者\",\"入庫日\",\"品番\",\"倉庫コード\",\"倉庫\",\"棚番\",\"ﾛｯﾄ№\",\"ﾁｬｰｼﾞ№\","
                            + "\"製造日\",\"元製番\",\"元製品№\",\"入庫数\",\"単位\",\"単価入力区分\",\"単価\",\"金額\",\"備考\"\r\n");
                }

                for (ResStoreInTechsItem item : storeInFujiList) {
                    Optional<ResStoreInTechsItem> opt = outputList.stream().filter(o -> o.equals(item)).findFirst();
                    if (opt.isPresent()) {
                        ResStoreInTechsItem data = opt.get();
                        data.setStockNum(data.getStockNum() + item.getStockNum());
                        data.setDate(item.getDate());
                    } else {
                        outputList.add(item);
                    }
                }
                
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(locks[lockFile], true), "SJIS"))) {
                    StatefulBeanToCsv<ResStoreInTechsItem> beanToCsv = new StatefulBeanToCsvBuilder<ResStoreInTechsItem>(writer).withLineEnd("\r\n").build();
                    beanToCsv.write(outputList);
                }
            }

            // 入庫実績情報を出力
            if (!storeInList1.isEmpty()) {
                List<ResStoreInItem> outputList;
                final int lockFile = 1;

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(locks[lockFile]), "SJIS"))) {
                    CsvToBeanBuilder<ResStoreInItem> builder = new CsvToBeanBuilder<>(reader);
                    builder.withType(ResStoreInItem.class);
                    builder.withSkipLines(1); // 1行目はスキップして読み込む
                    outputList = builder.build().parse();
                }

                // ファイルを初期化する
                locks[lockFile].createNewFile();
                
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(locks[lockFile]), "SJIS"))) {
                    writer.write("\"納入番号\",\"製番(工事番号)\",\"製品No(枝番)\",\"倉庫コード\",\"入庫数\",\"担当者コード\",\"入庫日\"\r\n");
                }

                for (ResStoreInItem item : storeInList1) {
                    Optional<ResStoreInItem> opt = outputList.stream().filter(o -> o.equals(item)).findFirst();
                    if (opt.isPresent()) {
                        ResStoreInItem data = opt.get();
                        data.setStockNum(data.getStockNum() + item.getStockNum());
                        data.setPersonNo(item.getPersonNo());
                        data.setDate(item.getDate());
                    } else {
                        outputList.add(item);
                    }
                }
                
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(locks[lockFile], true), "SJIS"))) {
                    StatefulBeanToCsv<ResStoreInItem> beanToCsv = new StatefulBeanToCsvBuilder<ResStoreInItem>(writer).withLineEnd("\r\n").build();
                    beanToCsv.write(outputList);
                }
            }

            // 入庫実績情報を出力
            if (!storeInList2.isEmpty()) {
                List<ResStoreInItem> outputList;
                final int lockFile = 2;

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(locks[lockFile]), "SJIS"))) {
                    CsvToBeanBuilder<ResStoreInItem> builder = new CsvToBeanBuilder<>(reader);
                    builder.withType(ResStoreInItem.class);
                    builder.withSkipLines(1); // 1行目はスキップして読み込む
                    outputList = builder.build().parse();
                }

                // ファイルを初期化する
                locks[lockFile].createNewFile();
                
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(locks[lockFile]), "SJIS"))) {
                    writer.write("\"納入番号\",\"製番(工事番号)\",\"製品No(枝番)\",\"倉庫コード\",\"入庫数\",\"担当者コード\",\"入庫日\"\r\n");
                }

                for (ResStoreInItem item : storeInList2) {
                    Optional<ResStoreInItem> opt = outputList.stream().filter(o -> o.equals(item)).findFirst();
                    if (opt.isPresent()) {
                        ResStoreInItem data = opt.get();
                        data.setStockNum(data.getStockNum() + item.getStockNum());
                        data.setPersonNo(item.getPersonNo());
                        data.setDate(item.getDate());
                    } else {
                        outputList.add(item);
                    }
                }

                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(locks[lockFile], true), "SJIS"))) {
                    StatefulBeanToCsv<ResStoreInItem> beanToCsv = new StatefulBeanToCsvBuilder<ResStoreInItem>(writer).withLineEnd("\r\n").build();
                    beanToCsv.write(outputList);
                }
            }

            // 出荷払出情報を出力
            if (!shippingList.isEmpty()) {
                List<ResShippingItem> outputList;
                final int lockFile = 4;

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(locks[lockFile]), "SJIS"))) {
                    CsvToBeanBuilder<ResShippingItem> builder = new CsvToBeanBuilder<>(reader);
                    builder.withType(ResShippingItem.class);
                    builder.withSkipLines(1); // 1行目はスキップして読み込む
                    outputList = builder.build().parse();
                }
                
                // ファイルを初期化する
                locks[lockFile].createNewFile();

                // ヘッダー書き込み
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(locks[lockFile]), "SJIS"))) {
                    writer.write("\"払出担当者コード\",\"払出担当者\",\"払出日\",\"出荷N\",\"払出指示明細№\",\"品番\",\"払出数\",\"単位\",\"原価計上課コード\",\"原価計上課\",\"原価計上部門コード\",\"原価計上部門\",\"倉庫コード\",\"倉庫\",\"棚番\",\"備考\"\r\n");
                }

                shippingList.stream().sorted((x, y) -> {
                    // 出荷No
                    int compare = x.getOrderNo().compareTo(y.getOrderNo());
                    if (compare != 0) {
                        return compare;
                    }
                    
                    // 指示明細No
                    compare = x.getDeliveryNo().compareTo(y.getDeliveryNo());
                    if (compare != 0) {
                        return compare;
                    }
                    
                    // イベントID
                    return x.getEventId().compareTo(y.getEventId());
                });
                
                String orderNo = null;
                String deliveryNo = null;
                int count = 0;
                List<String> removed = new ArrayList<>();
                
                for (ResShippingItem item : shippingList) {
                    if (!Objects.equals(orderNo, item.getOrderNo())) {
                        if (count > 0) {
                            // 明細が不足しているため、出力データから除外する
                            removed.add(orderNo);
                        }
                        orderNo = item.getOrderNo();
                        deliveryNo = item.getDeliveryNo();
                        count = item.getItemNum() - 1;

                    } else if (!Objects.equals(deliveryNo, item.getDeliveryNo())) {
                        deliveryNo = item.getDeliveryNo();
                    	count--;
                    }
                }
                
                if (!StringUtils.isEmpty(orderNo) && count > 0) {
                    // 明細が不足しているため、出力データから除外する
                    removed.add(orderNo);
                }           
                
                List<ResShippingItem> tempList1 = shippingList.stream().filter(o -> !removed.contains(o.getOrderNo())).collect(Collectors.toList());
                List<ResShippingItem> tempList2 = new ArrayList<>();

                // 出荷Noと出荷明細Noでデータを集約
                for (ResShippingItem item : tempList1) {
                    Optional<ResShippingItem> opt = tempList2.stream().filter(o -> o.equals(item) && !o.isFlag()).findFirst();
                    if (opt.isPresent()) {
                        ResShippingItem data = opt.get();
                        data.setDeliveryNum(data.getDeliveryNum() + item.getDeliveryNum());
                        data.setPersonNo(item.getPersonNo());
                        data.setDate(item.getDate());
                        // 完結フラグを設定
                        data.setFlag(Objects.equals(data.getRequestNum(), data.getDeliveryNum()));
                        
                    } else {
                        tempList2.add(item);
                        // 完結フラグを設定
                        item.setFlag(Objects.equals(item.getRequestNum(), item.getDeliveryNum()));
                    }
                }

                // 払出数が不足している実績は 出力データから除外する
                removed.addAll(tempList2.stream().filter(o -> !Objects.equals(o.getDeliveryNum(), o.getRequestNum())).map(o -> o.getOrderNo()).collect(Collectors.toList()));
                outputList.addAll(tempList2.stream().filter(o -> !removed.contains(o.getOrderNo())).collect(Collectors.toList()));

                // レコード書き込み
                if (!outputList.isEmpty()) {
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(locks[lockFile], true), "SJIS"))) {
                        StatefulBeanToCsv<ResShippingItem> beanToCsv = new StatefulBeanToCsvBuilder<ResShippingItem>(writer).withLineEnd("\r\n").build();
                        beanToCsv.write(outputList);
                    }
                }

                if (!removed.isEmpty()) {
                    List<Long> ids = logStockList.stream().filter(o -> removed.contains(o.getOrderNo())).map(o -> o.getEventId()).collect(Collectors.toList());
                    this.faced.updateSyncedLogStock(ids, Boolean.FALSE);
                }
            }

            if (!withdrawList.isEmpty()) {
                List<ResWithdrawItem> outputList;
                final int lockFile = 3;

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(locks[lockFile]), "SJIS"))) {
                    CsvToBeanBuilder<ResWithdrawItem> builder = new CsvToBeanBuilder<>(reader);
                    builder.withType(ResWithdrawItem.class);
                    builder.withSkipLines(1); // 1行目はスキップして読み込む
                    outputList = builder.build().parse();
                }
                
                // ファイルを初期化する
                locks[lockFile].createNewFile();

                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(locks[lockFile]), "SJIS"))) {
                    writer.write("\"出庫依頼番号\",\"倉庫コード\",\"出庫数\",\"担当者コード\",\"出庫日\"\r\n");
                }

                for (ResWithdrawItem item : withdrawList) {
                    Optional<ResWithdrawItem> opt = outputList.stream().filter(o -> o.equals(item)).findFirst();
                    if (opt.isPresent()) {
                        ResWithdrawItem data = opt.get();
                        data.setDeliveryNum(data.getDeliveryNum() + item.getDeliveryNum());
                        data.setPersonNo(item.getPersonNo());
                        data.setDate(item.getDate());
                    } else {
                        outputList.add(item);
                    }
                }

                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(locks[lockFile], true), "SJIS"))) {
                    StatefulBeanToCsv<ResWithdrawItem> beanToCsv = new StatefulBeanToCsvBuilder<ResWithdrawItem>(writer).withLineEnd("\r\n").build();
                    beanToCsv.write(outputList);
                }
            }

            // 棚卸情報を出力
            if (!inventoryList.isEmpty()) {
                List<ResInventoryItem> outputList;
                final int lockFile = 5;

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(locks[lockFile]), "SJIS"))) {
                    CsvToBeanBuilder<ResInventoryItem> builder = new CsvToBeanBuilder<>(reader);
                    builder.withType(ResInventoryItem.class);
                    builder.withSkipLines(1); // 1行目はスキップして読み込む
                    outputList = builder.build().parse();
                }
                
                // ファイルを初期化する
                locks[lockFile].createNewFile();

                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(locks[lockFile]), "SJIS"))) {
                    writer.write("\"品番\",\"倉庫コード\",\"在庫数\",\"担当者コード\",\"棚卸日\"\r\n");
                }

                for (ResInventoryItem item : inventoryList) {
                    Optional<ResInventoryItem> opt = outputList.stream().filter(o -> o.equals(item)).findFirst();
                    if (opt.isPresent()) {
                        ResInventoryItem data = opt.get();
                        data.setInventoryNum(data.getInventoryNum() + item.getInventoryNum());
                        data.setPersonNo(item.getPersonNo());
                        data.setDate(item.getDate());
                    } else {
                        outputList.add(item);
                    }
                }
                
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(locks[lockFile], true), "SJIS"))) {
                    StatefulBeanToCsv<ResInventoryItem> beanToCsv = new StatefulBeanToCsvBuilder<ResInventoryItem>(writer).withLineEnd("\r\n").build();
                    beanToCsv.write(outputList);
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            // 失敗した場合、同期フラグをリセットする
            this.faced.updateSyncedLogStock(eventIds, Boolean.FALSE);

        } finally {
            // ロックを解除
            if (locked) {
                boolean failed = false;
                try {
                    logger.info("Unlock file.");
                    for (int ii = 0; ii < locks.length; ii++) {
                        if (!this.unlockFile(locks[ii], files[ii])) {
                            logger.info("Failed to unlock the file: " + locks[ii]);
                            failed = true;
                            break;
                        }
                    }

                } catch (Exception ex) {
                    logger.fatal(ex);
                    failed = true;

                } finally {
                    // 失敗した場合、同期フラグをリセットする
                    if (failed && Objects.nonNull(eventIds) && !eventIds.isEmpty()) {
                        logger.info("Reset the sync flag.");
                        this.faced.updateSyncedLogStock(eventIds, Boolean.FALSE);
                    }
                }
            }
            
            long stopTime = System.currentTimeMillis();
            logger.info("ExportTask end: {}[ms] ", (stopTime - startTime));
        }
    }

    /**
     * ファイルをロックする。
     * 
     * @param file ロック前のファイル名
     * @param lock ロック後のファイル名
     * @return true: 成功、 false: 失敗
     * @throws IOException 
     */
    private boolean lockFile(File file, File lock) throws IOException {
        if (file.exists()) {
            if (!file.canRead() || !file.canWrite()) {
                logger.info("Can't read/write file: " + file);
                // 読み込み不可 もしくは 書き込み不可
                return false;
            }
        } else {
            // ファイルを作成
            logger.info("Create file: " + file);
            file.createNewFile();
        }

        //if (lock.exists()) {
        //    logger.info("Exists file: " + lock);
        //    lock.delete();
        //}

        // return file..renameTo(lock);
        try {
            Files.copy(file.toPath(), lock.toPath(), REPLACE_EXISTING);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * ファイルのロックを解除する。
     * 
     * @param lock ロック後のファイル名
     * @param file ロック前のファイル名
     * @return true: 成功、 false: 失敗
     * @throws IOException 
     */    
    private boolean unlockFile(File lock, File file) throws IOException {
        if (file.exists()) {
            logger.info("Exists file: " + file);
            if (!file.canRead() || !file.canWrite()) {
                logger.info("Can't read/write file: " + file);
                // 読み込み不可 もしくは 書き込み不可
                return false;
            }
            //file.delete();
        }
        
        //return lock.renameTo(file);
        try {
            Files.copy(lock.toPath(), file.toPath(), REPLACE_EXISTING);
            lock.delete();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
