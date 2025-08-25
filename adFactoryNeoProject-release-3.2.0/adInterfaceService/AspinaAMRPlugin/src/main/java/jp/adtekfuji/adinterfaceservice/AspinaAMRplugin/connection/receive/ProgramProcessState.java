package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ConnectionUtils;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.TreeMap;

public class ProgramProcessState implements ReceiveData {
    private static final Logger logger = LogManager.getLogger();
    static final public int id = 33;

    enum ProgramProcessStateEnum {
        ProgramPositionNumber, // プログラムポジション番号
        State, // 状態 -1: ニュートラル, 1:移動中, 2:中継点到着, 3:時間待機開始, 4:指示待ち, 5:指示受付, 時間待機中
        RemainingTime, // 残り時間
        Hour, //時間
        Minute, // 分
        Second, // 秒
        RESERVED, // RESERVED
        TargetProgram, // 対象のプログラム
    }


    final static TreeMap<ProgramProcessStateEnum, Integer> sendDataMap
            = new TreeMap<ProgramProcessStateEnum, Integer>() {{
                put(ProgramProcessStateEnum.ProgramPositionNumber, 4); // プログラムポジション番号
                put(ProgramProcessStateEnum.State, 4); // 状態 -1: ニュートラル, 1:移動中, 2:中継点到着, 3:時間待機開始, 4:指示待ち, 5:指示受付, 時間待機中
                put(ProgramProcessStateEnum.RemainingTime, 4); // 残り時間
                put(ProgramProcessStateEnum.Hour, 2); //時間
                put(ProgramProcessStateEnum.Minute, 2); // 分
                put(ProgramProcessStateEnum.Second, 2); // 秒
                put(ProgramProcessStateEnum.RESERVED, 2); // RESERVED
                put(ProgramProcessStateEnum.TargetProgram, 4); // 対象のプログラム
    }};

    long programPositionNumber; // プログラムポジション番号
    @Getter
    long state; // 状態 -1: ニュートラル, 1:移動中, 2:中継点到着, 3:時間待機開始, 4:指示待ち, 5:指示受付, 時間待機中
    long remainingTime; // 残り時間
    long hour; //時間
    long minute; // 分
    long second; // 秒
    long RESERVED; // RESERVED
    @Getter
    long targetProgram; // 対象のプログラム

    @Override
    public long getId() {
        return id;
    }

    private ProgramProcessState() {

    }

    static Optional<ReceiveData> Create(byte[] data) {
        logger.info("receive ProgramProcessState");
        return ConnectionUtils.decomposed(data, sendDataMap)
                .map(elements -> {
                    // 受信データから変換
                    ProgramProcessState ret = new ProgramProcessState();
                    ret.programPositionNumber = ConnectionUtils.fromLittleEndianToLong(elements.get(ProgramProcessStateEnum.ProgramPositionNumber));
                    ret.state = ConnectionUtils.fromLittleEndianToLong(elements.get(ProgramProcessStateEnum.State));
                    ret.remainingTime = ConnectionUtils.fromLittleEndianToLong(elements.get(ProgramProcessStateEnum.RemainingTime));
                    ret.hour = ConnectionUtils.fromLittleEndianToInt(elements.get(ProgramProcessStateEnum.Hour));
                    ret.minute = ConnectionUtils.fromLittleEndianToInt(elements.get(ProgramProcessStateEnum.Minute));
                    ret.second = ConnectionUtils.fromLittleEndianToInt(elements.get(ProgramProcessStateEnum.Second));
                    ret.RESERVED = ConnectionUtils.fromLittleEndianToInt(elements.get(ProgramProcessStateEnum.RESERVED));
                    ret.targetProgram = ConnectionUtils.fromLittleEndianToLong(elements.get(ProgramProcessStateEnum.TargetProgram));
                    return ret;
                });
    }
}
