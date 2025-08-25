/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.warehouseservicetesttool.utils;

import jp.adtekfuji.warehouseservicetesttool.entity.BhtAffili;
import jp.adtekfuji.warehouseservicetesttool.entity.BhtStktake;
import jp.adtekfuji.warehouseservicetesttool.entity.BhtStktakeRes;
import jp.adtekfuji.warehouseservicetesttool.entity.BhtWorker;

/**
 *
 * @author nar-nakamura
 */
public class BhtFileRecordConverter {

//<editor-fold defaultstate="collapsed" desc="BHTファイルフォーマット">
    /* BHT設定データフォーマット */
    private static final int htconf[] = {
        1,      // 0: prtAccept: 受入印刷有効フラグ (0:無効, 1:有効)
        1,      // 1: loginType: ログイン種類 (0:通常ログイン, 1:作業時ログイン)
        1,      // 2: prtShipment: 払出印刷有効フラグ (0:無効, 1:有効)
        1       // 3: prtStktake: 棚卸印刷有効フラグ (0:無効, 1:有効)
    };

    /* BHTメニューデータフォーマット */
    private static final int htmenu[] = {
        16,     // 0: itemName: メニュー項目名
        2       // 1: itemCode: メニュー項目コード
    };

    /* BHT作業者データフォーマット */
    private static final int worker[] = {
        20,     // 0: workerCode: 作業者QRコード
        11,     // 1: workerID: 作業者ID
        50      // 2: workerName: 作業者名
    };

    /* BHT受入データフォーマット */
    private static final int accept[] = {
        128,    // 0: acceptCode: 受入バーコード (発注No)
        22,     // 1: control: 管理区分(自達・支給)＋図番
        20,     // 2: chartNo: 図番(表示用)
        50,     // 3: articleName: 品名
        50,     // 4: standard: 規格
        50,     // 5: maker: メーカ
        11,     // 6: orderNum: 数量(発注数)
        10,     // 7: rackNo: 棚番
        11,     // 8: arriveNum: 数量(受入数)
        1,      // 9: flag: 受入状態(正常・不具合あり・欠品・分納)
        8,      // 10: workDate: 作業日付(yy/MM/dd)
        5,      // 11: workTime: 作業時刻(hh:mm)
        11      // 12: workerID: 作業者ID
    };

    /* BHT入庫データフォーマット */
    private static final int stock[] = {
        128,    // 0: orderQRCode: 入荷ラベルQRコード (発注No)
        22,     // 1: control: 管理区分(自達・支給)＋図番
        20,     // 2: chartNo: 図番(表示用)
        50,     // 3: articleName: 品名
        50,     // 4: standard: 規格
        50,     // 5: maker: メーカ
        11,     // 6: arriveNum: 数量(入荷数)
        10,     // 7: rackNo: 棚番
        11,     // 8: arriveNum: 数量(入庫数)
        8,      // 9: workDate: 作業日付(yy/MM/dd)
        5,      // 10: workTime: 作業時刻(hh:mm)
        11      // 11: workerID: 作業者ID
    };

    /* BHT払出データフォーマット */
    private static final int shipment[] = {
        64,     // 0: unitQRCode: ユニット票QRコード (ユニットコード＋開始番号＋終了番号)
        20,     // 1: unitCode: ユニットコード
        6,      // 2: startNum: 開始番号
        6,      // 3: endNum: 終了番号
        80,     // 4: unitName: ユニット名
        11,     // 5: machineNum: 台数
        50,     // 6: sectionName: 払出先名
        22,     // 7: control: 管理区分(自達・支給)＋図番
        20,     // 8: chartNo: 図番(表示用)
        50,     // 9: articleName: 品名
        50,     // 10: standard: 規格
        50,     // 11: maker: メーカ
        11,     // 12: stockNum: 在庫数
        11,     // 13: tempNum: 仮在庫数
        11,     // 14: requestNum: 要求数
        10,     // 15: rackNo: 棚番
        11,     // 16: shipNum: 数量(払出数)
        8,      // 17: workDate: 作業日付(yy/MM/dd)
        5,      // 18: workTime: 作業時刻(hh:mm)
        11,     // 19: workerID: 作業者ID
        128     // 20: payoutQR: 払出QRコード
    };

    /* BHT払出ラベルデータフォーマット */
    private static final int shipPrt[] = {
        64,     // 0: unitQRCode: ユニット票QRコード (ユニットコード＋開始番号＋終了番号)
        20,     // 1: unitCode: ユニットコード
        6,      // 2: startNum: 開始番号
        6,      // 3: endNum: 終了番号
        80,     // 4: unitName: ユニット名
        11,     // 5: machineNum: 台数
        50,     // 6: sectionName: 払出先名
        22,     // 7: control: 管理区分(自達・支給)＋図番
        20,     // 8: chartNo: 図番(表示用)
        50,     // 9: articleName: 品名
        50,     // 10: standard: 規格
        50,     // 11: maker: メーカ
        11,     // 12: stockNum: 在庫数
        11,     // 13: tempNum: 仮在庫数
        11,     // 14: requestNum: 要求数
        10,     // 15: rackNo: 棚番
        11,     // 16: shipNum: 数量(払出数)
        8,      // 17: workDate: 作業日付(yy/MM/dd)
        5,      // 18: workTime: 作業時刻(hh:mm)
        11,     // 19: workerID: 作業者ID
        128,    // 20: payoutQR: 払出QRコード
        50      // 21: workerName: 作業者名
    };

    /* BHT棚卸データフォーマット */
    private static final int stktake[] = {
        22,     // 0: control: 管理区分(自達・支給)＋図番
        20,     // 1: chartNo: 図番(表示用)
        50,     // 2: articleName: 品名
        50,     // 3: standard: 規格
        50,     // 4: maker: メーカ
        10,     // 5: rackNo: 棚番
        11,     // 6: stockNum: 数量(在庫数)
        11,     // 7: truthNum: 数量(実際の数)
        8,      // 8: workDate: 作業日付(yy/MM/dd)
        5,      // 9: workTime: 作業時刻(hh:mm)
        11,     // 10: workerID: 作業者ID
        50,     // 11: workerName: 作業者名
        16,     // 12: affiliName: 部品所属名
        2,      // 13: affiliCode: 部品所属コード
        11      // 14: labekNo: 連番
    };

    /* BHT棚卸結果フォーマット */
    private static final int stktakeRes[] = {
        22,     // 0: control: 管理区分(自達・支給)＋図番
        11      // 1: labekNo: 連番
    };

    /* BHT棚移動データフォーマット */
    private static final int rackmove[] = {
        22,     // 0: control: 管理区分(自達・支給)＋図番, 管理区分(台車)＋台車コード
        20,     // 1: chartNo: 図番(表示用), 台車コード(表示用)
        50,     // 2: articleName: 品名, 台車名
        50,     // 3: standard: 規格, タイプ
        50,     // 4: maker: メーカ, (無)
        10,     // 5: rackNo: 棚番(移動前), 所在コード(移動前)
        50,     // 6: rackNm: 棚名(移動前), 所在名(移動前)
        10,     // 7: newRackNo: 棚番(移動後), 所在コード(移動後)
        8,      // 8: workDate: 作業日付(yy/MM/dd)
        5,      // 9: workTime: 作業時刻(hh:mm)
        11      // 10: workerID: 作業者ID
    };

    /* BHT棚移動結果フォーマット */
    private static final int rackmoveRes[] = {
        22,     // 0: control: 管理区分(自達・支給)＋図番, 管理区分(台車)＋台車コード
        10,     // 1: newRackNo: 棚番(移動後), 所在コード(移動後)
        50      // 2: newRackNm: 棚名(移動後), 所在名(移動後)
    };

    /* BHT一括印刷データフォーマット */
    private static final int print[] = {
        128,    // 0: acceptCode: 受入バーコード (発注No)
        22,     // 1: control: 管理区分(自達・支給)＋図番
        20,     // 2: chartNo: 図番(表示用)
        50,     // 3: articleName: 品名
        50,     // 4: standard: 規格
        50,     // 5: maker: メーカ
        11,     // 6: orderNum: 数量(発注数)
        10,     // 7: rackNo: 棚番
        1,      // 8: flag: 受入状態(正常・不具合あり・欠品・分納)
        8,      // 9: workDate: 作業日付(yy/MM/dd)
        5,      // 10: workTime: 作業時刻(hh:mm)
        11      // 11: workerID: 作業者ID
    };

    /* BHT部品所属データフォーマット */
    private static final int affili[] = {
        16,     // 0: affiliName: 部品所属名
        2       // 1: affiliCode: 部品所属コード
    };
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHT設定データ変換">
//    /**
//     * BHT設定データレコード文字列をBHT設定データクラスに変換
//     * @param recordString
//     * @return
//     */
//    public static BhtHtconf BhtHtConfEncoder(String recordString) {
//        int start = 0;
//        String columns[] = new String[htconf.length];
//        for (int i = 0; i < htconf.length; i++) {
//            columns[i] = FileUtility.getBytesString(recordString, start, htconf[i]).trim();
//            start += htconf[i];
//        }
//
//        String prtAccept = columns[0];  // 受入印刷有効フラグ (0:無効, 1:有効)
//        String loginType = columns[1];  // ログイン種類 (0：通常ログイン，1：作業時ログイン)
//        String prtShipment = columns[2];// 払出印刷有効フラグ (0:無効, 1:有効)
//        String prtStktake = columns[3]; // 棚卸印刷有効フラグ (0:無効, 1:有効)
//
//        return new BhtHtconf(prtAccept, loginType, prtShipment, prtStktake);
//    }
//
//    /**
//     * BHT設定データクラスをBHT設定データレコード文字列に変換
//     * @param dat
//     * @return
//     */
//    public static String BhtHtConfDecoder(BhtHtconf dat) {
//        String recordString =
//                FileUtility.padBytesString(dat.getPrtAccept(), htconf[0]) +     // 受入印刷有効フラグ (0:無効, 1:有効)
//                FileUtility.padBytesString(dat.getLoginType(), htconf[1]) +     // ログイン種類 (0：通常ログイン，1：作業時ログイン)
//                FileUtility.padBytesString(dat.getPrtShipment(), htconf[2]) +   // 払出印刷有効フラグ (0:無効, 1:有効)
//                FileUtility.padBytesString(dat.getPrtStktake(), htconf[3]);     // 棚卸印刷有効フラグ (0:無効, 1:有効)
//
//        return recordString;
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHTメニューデータ変換">
//    /**
//     * BHTメニューデータレコード文字列をBHTメニューデータクラスに変換
//     * @param recordString
//     * @return
//     */
//    public static BhtHtmenu BhtHtMenuEncoder(String recordString) {
//        int start = 0;
//        String columns[] = new String[htmenu.length];
//        for (int i = 0; i < htmenu.length; i++) {
//            columns[i] = FileUtility.getBytesString(recordString, start, htmenu[i]).trim();
//            start += htmenu[i];
//        }
//
//        String itemName = columns[0];   // メニュー項目名
//        String itemCode = columns[1];   // メニュー項目コード
//
//        return new BhtHtmenu(itemName, itemCode);
//    }
//
//    /**
//     * BHTメニューデータクラスをBHTメニューデータレコード文字列に変換
//     * @param dat
//     * @return
//     */
//    public static String BhtHtMenuDecoder(BhtHtmenu dat) {
//        String recordString =
//                FileUtility.padBytesString(dat.getItemName(), htmenu[0]) +  // メニュー項目名
//                FileUtility.padBytesString(dat.getItemCode(), htmenu[1]);   // メニュー項目コード
//
//        return recordString;
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHT作業者マスタ変換">
    /**
     * BHT作業者マスタレコード文字列をBHT作業者マスタクラスに変換
     * @param recordString
     * @return
     */
    public static BhtWorker BhtWorkerEncoder(String recordString) {
        int start = 0;
        String columns[] = new String[worker.length];
        for (int i = 0; i < worker.length; i++) {
            columns[i] = FileUtility.getBytesString(recordString, start, worker[i]).trim();
            start += worker[i];
        }

        String workerCode = columns[0]; // 作業者QRコード
        String workerId = columns[1];   // 作業者ID
        String workerName = columns[2]; // 作業者名

        return new BhtWorker(workerCode, workerId, workerName);
    }

    /**
     * BHT作業者マスタクラスをBHT作業者マスタレコード文字列に変換
     * @param dat
     * @return
     */
    public static String BhtWorkerDecoder(BhtWorker dat) {
        String recordString =
                FileUtility.padBytesString(dat.getWorkerCode(), worker[0]) +    // 作業者QRコード
                FileUtility.padBytesString(dat.getWorkerId(), worker[1]) +      // 作業者ID
                FileUtility.padBytesString(dat.getWorkerName(), worker[2]);     // 作業者名

        return recordString;
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHT受入データ変換">
//    /**
//     * BHT受入データレコード文字列をBHT受入データクラスに変換
//     * @param recordString
//     * @return
//     */
//    public static BhtAccept BhtAcceptEncoder(String recordString) {
//        int start = 0;
//        String columns[] = new String[accept.length];
//        for (int i = 0; i < accept.length; i++) {
//            columns[i] = FileUtility.getBytesString(recordString, start, accept[i]).trim();
//            start += accept[i];
//        }
//
//        String acceptCode = columns[0]; // 受入バーコード (発注No)
//        String control = columns[1];    // 管理区分(自達・支給)＋図番
//        String chartNo = columns[2];    // 図番(表示用)
//        String articleName = columns[3];// 品名
//        String standard = columns[4];   // 規格
//        String maker = columns[5];      // メーカ
//        String orderNum = columns[6];   // 数量(発注数)
//        String rackNo = columns[7];     // 棚番
//        String arriveNum = columns[8];  // 数量(受入数)
//        String flag = columns[9];       // 受入状態(正常・不具合あり・欠品・分納)
//        String workDate = columns[10];  // 作業日付(yy/MM/dd)
//        String workTime = columns[11];  // 作業時刻(hh:mm)
//        String workerID = columns[12];  // 作業者ID
//
//        return new BhtAccept(acceptCode, control, chartNo,
//                articleName, standard, maker, orderNum, rackNo,
//                arriveNum, flag, workDate, workTime, workerID);
//    }
//
//    /**
//     * BHT受入データクラスをBHT受入データレコード文字列に変換
//     * @param dat
//     * @return
//     */
//    public static String BhtAcceptDecoder(BhtAccept dat) {
//        String recordString =
//                FileUtility.padBytesString(dat.getAcceptCode(), accept[0]) +    // 受入バーコード (発注No)
//                FileUtility.padBytesString(dat.getControl(), accept[1]) +       // 管理区分(自達・支給)＋図番
//                FileUtility.padBytesString(dat.getChartNo(), accept[2]) +       // 図番(表示用)
//                FileUtility.padBytesString(dat.getArticleName(), accept[3]) +   // 品名
//                FileUtility.padBytesString(dat.getStandard(), accept[4]) +      // 規格
//                FileUtility.padBytesString(dat.getMaker(), accept[5]) +         // メーカ
//                FileUtility.padBytesString(dat.getOrderNum(), accept[6]) +      // 数量(発注数)
//                FileUtility.padBytesString(dat.getRackNo(), accept[7]) +        // 棚番
//                FileUtility.padBytesString(dat.getArriveNum(), accept[8]) +     // 数量(受入数)
//                FileUtility.padBytesString(dat.getFlag(), accept[9]) +          // 受入状態(正常・不具合あり・欠品・分納)
//                FileUtility.padBytesString(dat.getWorkDate(), accept[10]) +     // 作業日付(yy/MM/dd)
//                FileUtility.padBytesString(dat.getWorkTime(), accept[11]) +     // 作業時刻(hh:mm)
//                FileUtility.padBytesString(dat.getWorkerID(), accept[12]);      // 作業者ID
//
//        return recordString;
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHT入庫データ変換">
//    /**
//     * BHT入庫データレコード文字列をBHT入庫データクラスに変換
//     * @param recordString
//     * @return
//     */
//    public static BhtStock BhtStockEncoder(String recordString) {
//        int start = 0;
//        String columns[] = new String[stock.length];
//        for (int i = 0; i < stock.length; i++) {
//            columns[i] = FileUtility.getBytesString(recordString, start, stock[i]).trim();
//            start += stock[i];
//        }
//
//        String orderQRCode = columns[0];// 入荷ラベルQRコード (発注No)
//        String control = columns[1];    // 管理区分(自達・支給)＋図番
//        String chartNo = columns[2];    // 図番(表示用)
//        String articleName = columns[3];// 品名
//        String standard = columns[4];   // 規格
//        String maker = columns[5];      // メーカ
//        String arriveNum = columns[6];  // 数量(入荷数)
//        String rackNo = columns[7];     // 棚番
//        String stockNum = columns[8];   // 数量(入庫数)
//        String workDate = columns[9];   // 作業日付(yy/MM/dd)
//        String workTime = columns[10];  // 作業時刻(hh:mm)
//        String workerID = columns[11];  // 作業者ID
//
//        return new BhtStock(orderQRCode, control, chartNo,
//                articleName, standard, maker, arriveNum, rackNo,
//                stockNum, workDate, workTime, workerID);
//    }
//
//    /**
//     * BHT入庫データクラスをBHT入庫データレコード文字列に変換
//     * @param dat
//     * @return
//     */
//    public static String BhtStockDecoder(BhtStock dat) {
//        String recordString =
//                FileUtility.padBytesString(dat.getOrderQRCode(), stock[0]) +    // 入荷ラベルQRコード (発注No)
//                FileUtility.padBytesString(dat.getControl(), stock[1]) +        // 管理区分(自達・支給)＋図番
//                FileUtility.padBytesString(dat.getChartNo(), stock[2]) +        // 図番(表示用)
//                FileUtility.padBytesString(dat.getArticleName(), stock[3]) +    // 品名
//                FileUtility.padBytesString(dat.getStandard(), stock[4]) +       // 規格
//                FileUtility.padBytesString(dat.getMaker(), stock[5]) +          // メーカ
//                FileUtility.padBytesString(dat.getArriveNum(), stock[6]) +      // 数量(入荷数)
//                FileUtility.padBytesString(dat.getRackNo(), stock[7]) +         // 棚番
//                FileUtility.padBytesString(dat.getStockNum(), stock[8]) +       // 数量(入庫数)
//                FileUtility.padBytesString(dat.getWorkDate(), stock[9]) +       // 作業日付(yy/MM/dd)
//                FileUtility.padBytesString(dat.getWorkTime(), stock[10]) +      // 作業時刻(hh:mm)
//                FileUtility.padBytesString(dat.getWorkerID(), stock[11]);       // 作業者ID
//
//        return recordString;
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHT払出データ変換">
//    /**
//     * BHT払出データレコード文字列をBHT払出データクラスに変換
//     * @param recordString
//     * @return
//     */
//    public static BhtShipment BhtShipmentEncoder(String recordString) {
//        int start = 0;
//        String columns[] = new String[shipment.length];
//        for (int i = 0; i < shipment.length; i++) {
//            columns[i] = FileUtility.getBytesString(recordString, start, shipment[i]).trim();
//            start += shipment[i];
//        }
//
//        String unitQRCode = columns[0]; // ユニット票QRコード (ユニットコード＋開始番号＋終了番号)
//        String unitCode = columns[1];   // ユニットコード
//        String startNum = columns[2];   // 開始番号
//        String endNum = columns[3];     // 終了番号
//        String unitName = columns[4];   // ユニット名
//        String machineNum = columns[5]; // 台数
//        String sectionName = columns[6];// 払出先名
//        String control = columns[7];    // 管理区分(自達・支給)＋図番
//        String chartNo = columns[8];    // 図番(表示用)
//        String articleName = columns[9];// 品名
//        String standard = columns[10];  // 規格
//        String maker = columns[11];     // メーカ
//        String stockNum = columns[12];  // 在庫数
//        String tempNum = columns[13];   // 仮在庫数
//        String requestNum = columns[14];// 要求数
//        String rackNo = columns[15];    // 棚番
//        String shipNum = columns[16];   // 数量(払出数)
//        String workDate = columns[17];  // 作業日付(yy/MM/dd)
//        String workTime = columns[18];  // 作業時刻(hh:mm)
//        String workerID = columns[19];  // 作業者ID
//        String payoutQR = columns[20];  // 払出QRコード
//
//        return new BhtShipment(unitQRCode, unitCode,
//                startNum, endNum, unitName, machineNum, sectionName,
//                control, chartNo, articleName, standard, maker, stockNum, tempNum,
//                requestNum, rackNo, shipNum, workDate, workTime, workerID, payoutQR);
//    }
//
//    /**
//     * BHT払出データクラスをBHT払出データレコード文字列に変換
//     * @param dat
//     * @return
//     */
//    public static String BhtShipmentDecoder(BhtShipment dat) {
//        String recordString =
//                FileUtility.padBytesString(dat.getUnitQRCode(), shipment[0]) +  // ユニット票QRコード (ユニットコード＋開始番号＋終了番号)
//                FileUtility.padBytesString(dat.getUnitCode(), shipment[1]) +    // ユニットコード
//                FileUtility.padBytesString(dat.getStartNum(), shipment[2]) +    // 開始番号
//                FileUtility.padBytesString(dat.getEndNum(), shipment[3]) +      // 終了番号
//                FileUtility.padBytesString(dat.getUnitName(), shipment[4]) +    // ユニット名
//                FileUtility.padBytesString(dat.getMachineNum(), shipment[5]) +  // 台数
//                FileUtility.padBytesString(dat.getSectionName(), shipment[6]) + // 払出先名
//                FileUtility.padBytesString(dat.getControl(), shipment[7]) +     // 管理区分(自達・支給)＋図番
//                FileUtility.padBytesString(dat.getChartNo(), shipment[8]) +     // 図番(表示用)
//                FileUtility.padBytesString(dat.getArticleName(), shipment[9]) + // 品名
//                FileUtility.padBytesString(dat.getStandard(), shipment[10]) +   // 規格
//                FileUtility.padBytesString(dat.getMaker(), shipment[11]) +      // メーカ
//                FileUtility.padBytesString(dat.getStockNum(), shipment[12]) +   // 在庫数
//                FileUtility.padBytesString(dat.getTempNum(), shipment[13]) +    // 仮在庫数
//                FileUtility.padBytesString(dat.getRequestNum(), shipment[14]) + // 要求数
//                FileUtility.padBytesString(dat.getRackNo(), shipment[15]) +     // 棚番
//                FileUtility.padBytesString(dat.getShipNum(), shipment[16]) +    // 数量(払出数)
//                FileUtility.padBytesString(dat.getWorkDate(), shipment[17]) +   // 作業日付(yy/MM/dd)
//                FileUtility.padBytesString(dat.getWorkTime(), shipment[18]) +   // 作業時刻(hh:mm)
//                FileUtility.padBytesString(dat.getWorkerID(), shipment[19]) +   // 作業者ID
//                FileUtility.padBytesString(dat.getPayoutQR(), shipment[20]);    // 払出QRコード
//
//        return recordString;
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHT棚卸データ変換">
    /**
     * BHT棚卸データレコード文字列をBHT棚卸データクラスに変換
     * @param recordString
     * @return
     */
    public static BhtStktake BhtStktakeEncoder(String recordString) {
        int start = 0;
        String columns[] = new String[stktake.length];
        for (int i = 0; i < stktake.length; i++) {
            columns[i] = FileUtility.getBytesString(recordString, start, stktake[i]).trim();
            start += stktake[i];
        }

        String control = columns[0];    // 管理区分(自達・支給)＋図番
        String chartNo = columns[1];    // 図番(表示用)
        String articleName = columns[2];// 品名
        String standard = columns[3];   // 規格
        String maker = columns[4];      // メーカ
        String rackNo = columns[5];     // 棚番
        String stockNum = columns[6];   // 数量(在庫数)
        String truthNum = columns[7];   // 数量(実際の数)
        String workDate = columns[8];   // 作業日付(yy/MM/dd)
        String workTime = columns[9];   // 作業時刻(hh:mm)
        String workerID = columns[10];  // 作業者ID
        String workerName = columns[11];// 作業者名
        String affiliName = columns[12];// 部品所属名
        String affiliCode = columns[13];// 部品所属コード
        String labelNo = columns[14];   // 連番

        return new BhtStktake(control, chartNo, articleName, standard, maker,
                rackNo, stockNum, truthNum, workDate, workTime, workerID, workerName, affiliName, affiliCode, labelNo);
    }

    /**
     * BHT棚卸データクラスをBHT棚卸データレコード文字列に変換
     * @param dat
     * @return
     */
    public static String BhtStktakeDecoder(BhtStktake dat) {
        String recordString =
                FileUtility.padBytesString(dat.getControl(), stktake[0]) +      // 管理区分(自達・支給)＋図番
                FileUtility.padBytesString(dat.getChartNo(), stktake[1]) +      // 図番(表示用)
                FileUtility.padBytesString(dat.getArticleName(), stktake[2]) +  // 品名
                FileUtility.padBytesString(dat.getStandard(), stktake[3]) +     // 規格
                FileUtility.padBytesString(dat.getMaker(), stktake[4]) +        // メーカ
                FileUtility.padBytesString(dat.getRackNo(), stktake[5]) +       // 棚番
                FileUtility.padBytesString(dat.getStockNum(), stktake[6]) +     // 数量(在庫数)
                FileUtility.padBytesString(dat.getTruthNum(), stktake[7]) +     // 数量(実際の数)
                FileUtility.padBytesString(dat.getWorkDate(), stktake[8]) +     // 作業日付(yy/MM/dd)
                FileUtility.padBytesString(dat.getWorkTime(), stktake[9]) +     // 作業時刻(hh:mm)
                FileUtility.padBytesString(dat.getWorkerID(), stktake[10]) +    // 作業者ID
                FileUtility.padBytesString(dat.getWorkerName(), stktake[11]) +  // 作業者名
                FileUtility.padBytesString(dat.getAffiliName(), stktake[12]) +  // 部品所属名
                FileUtility.padBytesString(dat.getAffiliCode(), stktake[13]) +  // 部品所属コード
                FileUtility.padBytesString(dat.getLabelNo(), stktake[14]);      // 連番

        return recordString;
    }
    // </editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHT棚卸結果変換">
    /**
     * BHT棚卸結果レコード文字列をBHT棚卸結果クラスに変換
     * @param recordString
     * @return
     */
    public static BhtStktakeRes BhtStktakeResEncoder(String recordString) {
        int start = 0;
        String columns[] = new String[stktakeRes.length];
        for (int i = 0; i < stktakeRes.length; i++) {
            columns[i] = FileUtility.getBytesString(recordString, start, stktakeRes[i]).trim();
            start += stktakeRes[i];
        }

        String control = columns[0];    // 管理区分(自達・支給)＋図番
        String labelNo = columns[1];    // 連番

        return new BhtStktakeRes(control, labelNo);
    }

    /**
     * BHT棚移動結果クラスをBHT棚移動結果レコード文字列に変換
     * @param dat
     * @return
     */
    public static String BhtStktakeResDecoder(BhtStktakeRes dat) {
        String recordString =
                FileUtility.padBytesString(dat.getControl(), stktakeRes[0]) +   // 管理区分(自達・支給)＋図番
                FileUtility.padBytesString(dat.getLabelNo(), stktakeRes[1]);    // 連番

        return recordString;
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHT棚移動データ変換">
//    /**
//     * BHT棚移動データレコード文字列をBHT棚移動データクラスに変換
//     * @param recordString
//     * @return
//     */
//    public static BhtRackmove BhtRackmoveEncoder(String recordString) {
//        int start = 0;
//        String columns[] = new String[rackmove.length];
//        for (int i = 0; i < rackmove.length; i++) {
//            columns[i] = FileUtility.getBytesString(recordString, start, rackmove[i]).trim();
//            start += rackmove[i];
//        }
//
//        String control = columns[0];    // 管理区分(自達・支給)＋図番, 管理区分(台車)＋台車コード
//        String chartNo = columns[1];    // 図番(表示用), 台車コード(表示用)
//        String articleName = columns[2];// 品名, 台車名
//        String standard = columns[3];   // 規格, タイプ
//        String maker = columns[4];      // メーカ, (無)
//        String rackNo = columns[5];     // 棚番(移動前), 所在コード(移動前)
//        String rackNm = columns[6];     // 棚名(移動前), 所在名(移動前)
//        String newRackNo = columns[7];  // 棚番(移動後), 所在コード(移動後)
//        String workDate = columns[8];   // 作業日付(yy/MM/dd)
//        String workTime = columns[9];   // 作業時刻(hh:mm)
//        String workerID = columns[10];  // 作業者ID
//
//        return new BhtRackmove(control, chartNo, articleName, standard, maker,
//                rackNo, rackNm, newRackNo, workDate, workTime, workerID);
//    }
//
//    /**
//     * BHT棚移動データクラスをBHT棚移動データレコード文字列に変換
//     * @param dat
//     * @return
//     */
//    public static String BhtRackmoveDecoder(BhtRackmove dat) {
//        String recordString =
//                FileUtility.padBytesString(dat.getControl(), rackmove[0]) +     // 管理区分(自達・支給)＋図番, 管理区分(台車)＋台車コード
//                FileUtility.padBytesString(dat.getChartNo(), rackmove[1]) +     // 図番(表示用), 台車コード(表示用)
//                FileUtility.padBytesString(dat.getArticleName(), rackmove[2]) + // 品名, 台車名
//                FileUtility.padBytesString(dat.getStandard(), rackmove[3]) +    // 規格, タイプ
//                FileUtility.padBytesString(dat.getMaker(), rackmove[4]) +       // メーカ, (無)
//                FileUtility.padBytesString(dat.getRackNo(), rackmove[5]) +      // 棚番(移動前), 所在コード(移動前)
//                FileUtility.padBytesString(dat.getRackNm(), rackmove[6]) +      // 棚名(移動前), 所在名(移動前)
//                FileUtility.padBytesString(dat.getNewRackNo(), rackmove[7]) +   // 棚番(移動後), 所在コード(移動後)
//                FileUtility.padBytesString(dat.getWorkDate(), rackmove[8]) +    // 作業日付(yy/MM/dd)
//                FileUtility.padBytesString(dat.getWorkTime(), rackmove[9]) +    // 作業時刻(hh:mm)
//                FileUtility.padBytesString(dat.getWorkerID(), rackmove[10]);    // 作業者ID
//
//        return recordString;
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHT棚移動結果変換">
//    /**
//     * BHT棚移動結果レコード文字列をBHT棚移動結果クラスに変換
//     * @param recordString
//     * @return
//     */
//    public static BhtRackmoveRes BhtRackmoveResEncoder(String recordString) {
//        int start = 0;
//        String columns[] = new String[rackmoveRes.length];
//        for (int i = 0; i < rackmoveRes.length; i++) {
//            columns[i] = FileUtility.getBytesString(recordString, start, rackmoveRes[i]).trim();
//            start += rackmoveRes[i];
//        }
//
//        String control = columns[0];    // 管理区分(自達・支給)＋図番, 管理区分(台車)＋台車コード
//        String newRackNo = columns[1];  // 棚番(移動後), 所在コード(移動後)
//        String newRackNm = columns[2];  // 棚名(移動後), 所在名(移動後)
//
//        return new BhtRackmoveRes(control, newRackNo, newRackNm);
//    }
//
//    /**
//     * BHT棚移動結果クラスをBHT棚移動結果レコード文字列に変換
//     * @param dat
//     * @return
//     */
//    public static String BhtRackmoveResDecoder(BhtRackmoveRes dat) {
//        String recordString =
//                FileUtility.padBytesString(dat.getControl(), rackmoveRes[0]) +      // 管理区分(自達・支給)＋図番, 管理区分(台車)＋台車コード
//                FileUtility.padBytesString(dat.getNewRackNo(), rackmoveRes[1]) +    // 棚番(移動後), 所在コード(移動後)
//                FileUtility.padBytesString(dat.getNewRackNm(), rackmoveRes[2]);     // 棚名(移動後), 所在名(移動後)
//
//        return recordString;
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHT一括印刷データ変換">
//    /**
//     * BHT一括印刷データレコード文字列をBHT一括印刷データクラスに変換
//     * @param recordString
//     * @return
//     */
//    public static BhtPrint BhtPrintEncoder(String recordString) {
//        int start = 0;
//        String columns[] = new String[print.length];
//        for (int i = 0; i < print.length; i++) {
//            columns[i] = FileUtility.getBytesString(recordString, start, print[i]).trim();
//            start += print[i];
//        }
//
//        String order = columns[0];      // 受入バーコード (発注No)
//        String control = columns[1];    // 管理区分(自達・支給)＋図番
//        String chartNo = columns[2];    // 図番(表示用)
//        String articleName = columns[3];// 品名
//        String standard = columns[4];   // 規格
//        String maker = columns[5];      // メーカ
//        String orderNum = columns[6];   // 数量(発注数)
//        String rackNo = columns[7];     // 棚番
//        String flag = columns[8];       // 受入状態(正常・不具合あり・欠品・分納)
//        String workDate = columns[9];   // 作業日付(yy/MM/dd)
//        String workTime = columns[10];  // 作業時刻(hh:mm)
//        String workerID = columns[11];  // 作業者ID
//
//        return new BhtPrint(order, control, chartNo, articleName, standard, maker,
//            orderNum, rackNo, flag, workDate, workTime, workerID);
//    }
//
//    /**
//     * BHT一括印刷データクラスをBHT一括印刷データレコード文字列に変換
//     * @param dat
//     * @return
//     */
//    public static String BhtPrintDecoder(BhtPrint dat) {
//        String recordString =
//                FileUtility.padBytesString(dat.getOrder(), print[0]) +          // 受入バーコード (発注No)
//                FileUtility.padBytesString(dat.getControl(), print[1]) +        // 管理区分(自達・支給)＋図番
//                FileUtility.padBytesString(dat.getChartNo(), print[2]) +        // 図番(表示用)
//                FileUtility.padBytesString(dat.getArticleName(), print[3]) +    // 品名
//                FileUtility.padBytesString(dat.getStandard(), print[4]) +       // 規格
//                FileUtility.padBytesString(dat.getMaker(), print[5]) +          // メーカ
//                FileUtility.padBytesString(dat.getOrderNum(), print[6]) +       // 数量(発注数)
//                FileUtility.padBytesString(dat.getRackNo(), print[7]) +         // 棚番
//                FileUtility.padBytesString(dat.getFlag(), print[8]) +           // 受入状態(正常・不具合あり・欠品・分納)
//                FileUtility.padBytesString(dat.getWorkDate(), print[9]) +       // 作業日付(yy/MM/dd)
//                FileUtility.padBytesString(dat.getWorkTime(), print[10]) +      // 作業時刻(hh:mm)
//                FileUtility.padBytesString(dat.getWorkerID(), print[11]);       // 作業者ID
//
//        return recordString;
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHT払出ラベルデータ変換">
//    /**
//     * BHT払出ラベルデータレコード文字列をBHT払出ラベルデータクラスに変換
//     * @param recordString
//     * @return
//     */
//    public static BhtShipPrt BhtShipPrtEncoder(String recordString) {
//        int start = 0;
//        String columns[] = new String[shipPrt.length];
//        for (int i = 0; i < shipPrt.length; i++) {
//            columns[i] = FileUtility.getBytesString(recordString, start, shipPrt[i]).trim();
//            start += shipPrt[i];
//        }
//
//        String unitQRCode = columns[0]; // ユニット票QRコード (ユニットコード＋開始番号＋終了番号)
//        String unitCode = columns[1];   // ユニットコード
//        String startNum = columns[2];   // 開始番号
//        String endNum = columns[3];     // 終了番号
//        String unitName = columns[4];   // ユニット名
//        String machineNum = columns[5]; // 台数
//        String sectionName = columns[6];// 払出先名
//        String control = columns[7];    // 管理区分(自達・支給)＋図番
//        String chartNo = columns[8];    // 図番(表示用)
//        String articleName = columns[9];// 品名
//        String standard = columns[10];  // 規格
//        String maker = columns[11];     // メーカ
//        String stockNum = columns[12];  // 在庫数
//        String tempNum = columns[13];   // 仮在庫数
//        String requestNum = columns[14];// 要求数
//        String rackNo = columns[15];    // 棚番
//        String shipNum = columns[16];   // 数量(払出数)
//        String workDate = columns[17];  // 作業日付(yy/MM/dd)
//        String workTime = columns[18];  // 作業時刻(hh:mm)
//        String workerID = columns[19];  // 作業者ID
//        String payoutQR = columns[20];  // 払出QRコード
//        String workerName = columns[21];// 作業者名
//
//        return new BhtShipPrt(unitQRCode, unitCode,
//                startNum, endNum, unitName, machineNum, sectionName,
//                control, chartNo, articleName, standard, maker, stockNum, tempNum,
//                requestNum, rackNo, shipNum, workDate, workTime, workerID, payoutQR, workerName);
//    }
//
//    /**
//     * BHT払出ラベルデータクラスをBHT払出ラベルデータレコード文字列に変換
//     * @param dat
//     * @return
//     */
//    public static String BhtShipPrtDecoder(BhtShipPrt dat) {
//        String recordString =
//                FileUtility.padBytesString(dat.getUnitQRCode(), shipPrt[0]) +   // ユニット票QRコード (ユニットコード＋開始番号＋終了番号)
//                FileUtility.padBytesString(dat.getUnitCode(), shipPrt[1]) +     // ユニットコード
//                FileUtility.padBytesString(dat.getStartNum(), shipPrt[2]) +     // 開始番号
//                FileUtility.padBytesString(dat.getEndNum(), shipPrt[3]) +       // 終了番号
//                FileUtility.padBytesString(dat.getUnitName(), shipPrt[4]) +     // ユニット名
//                FileUtility.padBytesString(dat.getMachineNum(), shipPrt[5]) +   // 台数
//                FileUtility.padBytesString(dat.getSectionName(), shipPrt[6]) +  // 払出先名
//                FileUtility.padBytesString(dat.getControl(), shipPrt[7]) +      // 管理区分(自達・支給)＋図番
//                FileUtility.padBytesString(dat.getChartNo(), shipPrt[8]) +      // 図番(表示用)
//                FileUtility.padBytesString(dat.getArticleName(), shipPrt[9]) +  // 品名
//                FileUtility.padBytesString(dat.getStandard(), shipPrt[10]) +    // 規格
//                FileUtility.padBytesString(dat.getMaker(), shipPrt[11]) +       // メーカ
//                FileUtility.padBytesString(dat.getStockNum(), shipPrt[12]) +    // 在庫数
//                FileUtility.padBytesString(dat.getTempNum(), shipPrt[13]) +     // 仮在庫数
//                FileUtility.padBytesString(dat.getRequestNum(), shipPrt[14]) +  // 要求数
//                FileUtility.padBytesString(dat.getRackNo(), shipPrt[15]) +      // 棚番
//                FileUtility.padBytesString(dat.getShipNum(), shipPrt[16]) +     // 数量(払出数)
//                FileUtility.padBytesString(dat.getWorkDate(), shipPrt[17]) +    // 作業日付(yy/MM/dd)
//                FileUtility.padBytesString(dat.getWorkTime(), shipPrt[18]) +    // 作業時刻(hh:mm)
//                FileUtility.padBytesString(dat.getWorkerID(), shipPrt[19]) +    // 作業者ID
//                FileUtility.padBytesString(dat.getPayoutQR(), shipPrt[20]) +    // 払出QRコード
//                FileUtility.padBytesString(dat.getWorkerName(), shipPrt[21]);   // 作業者名
//
//        return recordString;
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="BHT部品所属データ変換">
    /**
     * BHT部品所属データレコード文字列をBHT部品所属データクラスに変換
     * @param recordString
     * @return
     */
    public static BhtAffili BhtAffiliEncoder(String recordString) {
        int start = 0;
        String columns[] = new String[affili.length];
        for (int i = 0; i < affili.length; i++) {
            columns[i] = FileUtility.getBytesString(recordString, start, affili[i]).trim();
            start += affili[i];
        }

        String affiliName = columns[0]; // 部品所属名
        String affiliCode = columns[1]; // 部品所属コード

        return new BhtAffili(affiliName, affiliCode);
    }

    /**
     * BHT部品所属データクラスをBHT部品所属データレコード文字列に変換
     * @param dat
     * @return
     */
    public static String BhtAffiliDecoder(BhtAffili dat) {
        String recordString =
                FileUtility.padBytesString(dat.getAffiliName(), affili[0]) +    // 部品所属名
                FileUtility.padBytesString(dat.getAffiliCode(), affili[1]);     // 部品所属コード

        return recordString;
    }
//</editor-fold>
}
