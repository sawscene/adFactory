package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ConnectionUtils;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.util.Optional;
import java.util.TreeMap;

public class AMRStatus implements ReceiveData {
    private static final Logger logger = LogManager.getLogger();

    enum AMRStatusEnum{
        ConnectStatusWithUnitMainUnit, // 台車制御基板との通信状態
        BatteryConnectionStatus, // バッテリーとの通信状態
        ExBoardStatus, // 拡張ボートとの通信状態
        ConnectStatusForWithQRUnit, // QRコード読込ユニットとの通信状態
        SLMAMode, // SLAMモード
        ConveyorProcState, // プログラム搬送実行状態
        TimeoutOfMapReading, // マップ読込タイムアウト
        UserCompareResult, // ユーザ名のコンペア結果
        NeutralState, // ニュートラル状態
        MotorErrorCode, // モータエラー発生コード
        DatabaseFileName, // データベースファイル名
        HasDepthData, // センサから深度データが来ているか?
        HasImage, // センサから画像データが来ているか?
        Has2DSkinData, // センサから2Dスキンデータが来ているか?
        Reserved,
        SceneNumber, // 現在のシーン番号
        IsConnectWithMotorDriver1, // モータドライバ1の通信が成功しているか?
        IsConnectWithMotorDriver2, // モーだドライバ2の通信が成功しているか?
        IsConnectWithBattery, // 台車制御基板経由でドライバーと通信出来ているか?
        StorageSize, // ストレージトータル容量
        StorageFreeSize, // ストレージ空き容量
        DatabaseSize, // データベースサイズ
        MotorErrorCodeL, // モータエラーコードL
        MotorErrorCodeR, // モーターエラーコードR
        MapDataLoading, // マップ読込中フラグ
        LinearNavigatorStatus, // 直線ナビゲータステータス
    }

    final static TreeMap<AMRStatusEnum, Integer> receiveDataMap
            = new TreeMap<AMRStatusEnum, Integer>() {{
        put(AMRStatusEnum.ConnectStatusWithUnitMainUnit, 4); // 台車制御基板との通信状態
        put(AMRStatusEnum.BatteryConnectionStatus, 4); // バッテリーとの通信状態
        put(AMRStatusEnum.ExBoardStatus, 4); // 拡張ボートとの通信状態
        put(AMRStatusEnum.ConnectStatusForWithQRUnit, 4); // QRコード読込ユニットとの通信状態
        put(AMRStatusEnum.SLMAMode, 4); // SLAMモード
        put(AMRStatusEnum.ConveyorProcState, 4); // プログラム搬送実行状態
        put(AMRStatusEnum.TimeoutOfMapReading, 4); // マップ読込タイムアウト
        put(AMRStatusEnum.UserCompareResult, 4); // ユーザ名のコンペア結果
        put(AMRStatusEnum.NeutralState, 4); // ニュートラル状態
        put(AMRStatusEnum.MotorErrorCode, 4); // モータエラー発生コード
        put(AMRStatusEnum.DatabaseFileName, 64); // データベースファイル名
        put(AMRStatusEnum.HasDepthData, 4); // センサから深度データが来ているか?
        put(AMRStatusEnum.HasImage, 4); // センサから画像データが来ているか?
        put(AMRStatusEnum.Has2DSkinData, 4); // センサから2Dスキンデータが来ているか?
        put(AMRStatusEnum.Reserved, 4);
        put(AMRStatusEnum.SceneNumber, 4); // 現在のシーン番号
        put(AMRStatusEnum.IsConnectWithMotorDriver1, 4); // モータドライバ1の通信が成功しているか?
        put(AMRStatusEnum.IsConnectWithMotorDriver2, 4);// モーだドライバ2の通信が成功しているか?
        put(AMRStatusEnum.IsConnectWithBattery, 4); // 台車制御基板経由でドライバーと通信出来ているか?
        put(AMRStatusEnum.StorageSize, 4);// ストレージトータル容量
        put(AMRStatusEnum.StorageFreeSize, 4);// ストレージ空き容量
        put(AMRStatusEnum.DatabaseSize, 8);// データベースサイズ
        put(AMRStatusEnum.MotorErrorCodeL, 4);// モータエラーコードL
        put(AMRStatusEnum.MotorErrorCodeR, 4);// モーターエラーコードR
        put(AMRStatusEnum.MapDataLoading, 4);// マップ読込中フラグ
        put(AMRStatusEnum.LinearNavigatorStatus, 4);// 直線ナビゲータステータス
    }};

    long connectStatusWithUnitMainUnit; // 台車制御基板との通信状態
    long batteryConnectionStatus; // バッテリーとの通信状態
    long exBoardStatus; // 拡張ボートとの通信状態
    long connectStatusForWithQRUnit; // QRコード読込ユニットとの通信状態
    long SLMAMode; // SLAMモード
    long conveyorProcState; // プログラム搬送実行状態
    long timeoutOfMapReading; // マップ読込タイムアウト
    long userCompareResult; // ユーザ名のコンペア結果
    @lombok.Getter
    long neutralState; // ニュートラル状態
    long motorErrorCode; // モータエラー発生コード
    byte[] databaseFileName; // データベースファイル名
    long hasDepthData; // センサから深度データが来ているか?
    long hasImage; // センサから画像データが来ているか?
    long has2DSkinData; // センサから2Dスキンデータが来ているか?
    long sceneNumber; // 現在のシーン番号
    long isConnectWithMotorDriver1; // モータドライバ1の通信が成功しているか?
    long isConnectWithMotorDriver2; // モーだドライバ2の通信が成功しているか?
    long isConnectWithBattery; // 台車制御基板経由でドライバーと通信出来ているか?
    long storageSize; // ストレージトータル容量
    long storageFreeSize; // ストレージ空き容量
    BigInteger databaseSize; // データベースサイズ
    long motorErrorCodeL; // モータエラーコードL
    long motorErrorCodeR; // モーターエラーコードR
    long mapDataLoading; // マップ読込中フラグ
    long linearNavigatorStatus; // 直線ナビゲータステータス

    static final public int id = 32;
    @Override
    public long getId() {
        return id;
    }

    private AMRStatus() {
    }

    static Optional<ReceiveData> Create(byte[] data) {
        logger.info("receive AMRStatus");
        return ConnectionUtils.decomposed(data, receiveDataMap)
                .map(elements -> {
                    // 受信データから変換
                    AMRStatus ret = new AMRStatus();
                    ret.connectStatusWithUnitMainUnit = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.ConnectStatusWithUnitMainUnit)); // 台車制御基板との通信状態
                    ret.batteryConnectionStatus = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.BatteryConnectionStatus)); // バッテリーとの通信状態
                    ret.exBoardStatus = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.ExBoardStatus)); // 拡張ボートとの通信状態
                    ret.connectStatusForWithQRUnit = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.ConnectStatusForWithQRUnit)); // QRコード読込ユニットとの通信状態
                    ret.SLMAMode = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.SLMAMode)); // SLAMモード
                    ret.conveyorProcState = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.ConveyorProcState)); // プログラム搬送実行状態
                    ret.timeoutOfMapReading = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.TimeoutOfMapReading)); // マップ読込タイムアウト
                    ret.userCompareResult = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.UserCompareResult)); // ユーザ名のコンペア結果
                    ret.neutralState = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.NeutralState)); // ニュートラル状態
                    ret.motorErrorCode = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.MotorErrorCode)); // モータエラー発生コード
                    ret.databaseFileName = elements.get(AMRStatusEnum.DatabaseFileName); // データベースファイル名
                    ret.hasDepthData = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.HasImage)); // センサから深度データが来ているか?
                    ret.hasImage = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.HasDepthData)); // センサから画像データが来ているか?
                    ret.has2DSkinData = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.Has2DSkinData)); // センサから2Dスキンデータが来ているか?
                    ret.sceneNumber = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.SceneNumber)); // 現在のシーン番号
                    ret.isConnectWithMotorDriver1 = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.IsConnectWithMotorDriver1)); // モータドライバ1の通信が成功しているか?
                    ret.isConnectWithMotorDriver2 = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.IsConnectWithMotorDriver2)); // モーだドライバ2の通信が成功しているか?
                    ret.isConnectWithBattery = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.IsConnectWithBattery)); // 台車制御基板経由でドライバーと通信出来ているか?
                    ret.storageSize = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.StorageSize)); // ストレージトータル容量
                    ret.storageFreeSize = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.StorageFreeSize)); // ストレージ空き容量
                    ret.databaseSize = ConnectionUtils.fromLittleEndianToBigInteger(elements.get(AMRStatusEnum.DatabaseSize)); // データベースサイズ
                    ret.motorErrorCodeL = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.MotorErrorCodeL)); // モータエラーコードL
                    ret.motorErrorCodeR = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.MotorErrorCodeR)); // モーターエラーコードR
                    ret.mapDataLoading = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.MapDataLoading)); // マップ読込中フラグ
                    ret.linearNavigatorStatus = ConnectionUtils.fromLittleEndianToLong(elements.get(AMRStatusEnum.LinearNavigatorStatus)); // 直線ナビゲータステータス
                    return ret;
                });
    }
}
