package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ConnectionUtils;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.TreeMap;

public class BatteryInfo implements ReceiveData {
    public static final int id = 15;
    private static final Logger logger = LogManager.getLogger();
    enum BatteryInfoEnum {
        RemainedBatteryCapacity, // バッテリー残量（mAh)
        BatteryCapacity, // バッテリーフル充電時の容量（mAh)
        Reserved,
        CurrentValue, // 現在の電流値（mAh
        Temperature, // 温度
        Reserved2,
    };

    final static TreeMap<BatteryInfoEnum, Integer> receiveDataMap
            = new TreeMap<BatteryInfoEnum, Integer>() {{
        put(BatteryInfoEnum.RemainedBatteryCapacity, 4); // バッテリー残量（mAh)
        put(BatteryInfoEnum.BatteryCapacity, 4); // バッテリーフル充電時の容量（mAh)
        put(BatteryInfoEnum.Reserved, 4);
        put(BatteryInfoEnum.CurrentValue, 4); // 現在の電流値（mAh）
        put(BatteryInfoEnum.Temperature, 4); // 温度（℃）
        put(BatteryInfoEnum.Reserved2, 20);
    }};

    long remainedBatteryCapacity;
    long batteryCapacity;
    long currentValue;
    long temperature;



    @Override
    public long getId() {
        return id;
    }

    static Optional<ReceiveData> Create(byte[] data) {
        logger.info("receive BatteryInfo");
        return ConnectionUtils.decomposed(data, receiveDataMap)
                .map(elements -> {
                    BatteryInfo batteryInfo = new BatteryInfo();
                    batteryInfo.remainedBatteryCapacity = ConnectionUtils.fromLittleEndianToLong(elements.get(BatteryInfoEnum.RemainedBatteryCapacity));
                    batteryInfo.batteryCapacity = ConnectionUtils.fromLittleEndianToLong(elements.get(BatteryInfoEnum.BatteryCapacity));
                    batteryInfo.currentValue = ConnectionUtils.fromLittleEndianToLong(elements.get(BatteryInfoEnum.CurrentValue));
                    batteryInfo.temperature = ConnectionUtils.fromLittleEndianToLong(elements.get(BatteryInfoEnum.Temperature));
                    return batteryInfo;
                });
    }
}
