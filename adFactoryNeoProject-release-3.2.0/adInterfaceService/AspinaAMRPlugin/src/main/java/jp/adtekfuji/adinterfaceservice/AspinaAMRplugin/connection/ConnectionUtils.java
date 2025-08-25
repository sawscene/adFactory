package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection;

import adtekfuji.utility.Tuple;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive.InvalidReceiveData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class ConnectionUtils {
    private static final Logger logger = LogManager.getLogger();

    private final static byte[] Marker = {0x12, 0x34, 0x56, 0x78};

    private final static int dataIdByte = 4;
    private final static int dataSizeByte = 4;
    private final static int checkSumByte = 4;


    /**
     * 送信データを作成
     * @param id ID
     * @param data データ
     * @return 送信データ
     */
    public static byte[] createSendData(long id, byte[] data) {
        return concat(
                Marker,  // マーカ 1～4
                toLittleEndian(id, dataIdByte), // データID
                toLittleEndian(Objects.isNull(data) ? 0 : data.length, dataSizeByte), // データサイズ
                data, // データ
                toLittleEndian(createCheckSum(data), checkSumByte) // チェックサム
        );
    }

    /**
     * 受信データを解析し、データを返す
     * @param data 受信データ
     * @return データ
     */
    public static Tuple<byte[], List<ReceiveData>> parseReceiveData(byte[] data, BiFunction<Long, byte[], Optional<ReceiveData>> func) {
        List<ReceiveData> receiveData = new ArrayList<>();
        if (Objects.isNull(data)) {
            func.apply((long) InvalidReceiveData.id, null)
                    .ifPresent(receiveData::add);
            return new Tuple<>(null, receiveData);
        }

//        logger.info("receiveData =====");

        int top = 0;
        for (int pos=0; pos<data.length; ++pos) {
            // 最低限のデータサイズチェック
            if (pos + Marker.length + dataIdByte + dataSizeByte + checkSumByte > data.length) {
                break;
            }

            // マーカ検索
            int finalPos = pos;
            if (!IntStream
                    .rangeClosed(0, Marker.length-1)
                    .allMatch(n -> Objects.equals(data[finalPos+n], Marker[n]))) {
                continue;
            }
            int index = pos + Marker.length;
            // ID
            byte[] IdData = new byte[dataIdByte];
            System.arraycopy(data, index, IdData, 0, IdData.length);
            long id = fromLittleEndianToLong(IdData);
            index += IdData.length;

            // dataSize
            byte[] dataSizeData = new byte[dataSizeByte];
            System.arraycopy(data, index, dataSizeData, 0, dataSizeData.length);
            index += dataSizeData.length;

            // データ本体
            long dataSize =  fromLittleEndianToLong(dataSizeData);
            // データサイズチェック
            if (pos + Marker.length + dataIdByte + dataSizeByte + dataSize + checkSumByte > data.length) {
//                logger.info("sizeCheckError requestedDataSize({}) > dataSize({})", pos + Marker.length + dataIdByte + dataSizeByte + dataSize + checkSumByte, data.length);
                break;
            }

            byte[] revData = null;
            if (dataSize > 0) {
                revData = new byte[(int)dataSize];
                System.arraycopy(data, index, revData, 0, revData.length);
                index += revData.length;
            }

            // チェックサム
            byte[] checkSumData = new byte[checkSumByte];
            System.arraycopy(data, index, checkSumData, 0, checkSumData.length);
            index += checkSumData.length;

            long checkSum = fromLittleEndianToLong(checkSumData);
            long calcCheckSum = createCheckSum(revData);
            if (!Objects.equals(checkSum, calcCheckSum)) {
                logger.fatal("check sum error id={}, {}, {}", id, checkSum, calcCheckSum);
//                continue;
            }

            // ReceiveDataへ変換
            func.apply(id, revData).ifPresent(receiveData::add);
            // 次の位置の更新(++pos分マイナス)
            pos = index - 1;
            top = index;
        }

        byte[] rest = null;
        int restLength = data.length - top;
        if (restLength > 0) {
            rest = new byte[restLength];
            System.arraycopy(data, top, rest, 0, restLength);
        }
        return new Tuple<>(rest, receiveData);
    }


    /**
     * 要素に分解をする。
     * @param data 分解するデータ
     * @param map マップ
     * @return 分解されたデータ
     */
    public static <T extends Comparable<T>> Optional<TreeMap<T, byte[]>> decomposed(byte[] data, TreeMap<T, Integer> map) {
        if (!Objects.equals(map.values().stream().mapToInt(a->a).sum(), data.length)) {
            logger.fatal("received data size error!! : Expected: {}, Actual: {}", map.values().stream().mapToInt(a->a).sum(), data.length);
            return Optional.empty();
        }

        TreeMap<T, byte[]> ret = new TreeMap<>();

        int pos = 0;
        for (Map.Entry<T, Integer> entry : map.entrySet()) {
            int size = entry.getValue();
            byte[] item = new byte[size];
            System.arraycopy(data, pos, item, 0, size);
            ret.put(entry.getKey(), item);
            pos += size;
        }
        return Optional.of(ret);
    }


    /**
     * チェックサムを作成
     * @param data データ
     * @return チェックサム
     */
    static long createCheckSum(byte[] data) {
        if (Objects.isNull(data)) {
            return 0;
        }

        byte[] tmp = {0, 0};
        long sum = 0;
        for (byte d : data) {
            tmp[1] = d;
            sum +=ByteBuffer.wrap(tmp).getShort();;
        }
        return sum;
    }

    /**
     * byteの配列を反転する
     * @param val 反転するbyte配列.
     * @return 反転された配列.
     */
    private static byte[] reverse(byte[] val) {
        for (int f = 0, l = val.length - 1; f < l; f++, l--){
            byte temp = val[f];
            val[f]  = val[l];
            val[l] = temp;
        }
        return val;
    }

    /**
     * 符号なし8バイト用
     * @param value 数値
     * @param byteNum バイト数
     * @return リトルエンディアンの配列
     */
    public static byte[] toLittleEndian(BigInteger value, int byteNum) {
        return Arrays.copyOf(reverse(value.toByteArray()), byteNum);
    }

    /**
     * 符号なし4バイト用
     * @param value 数値
     * @param byteNum バイト数
     * @return リトルエンディアンの配列
     */
    public static byte[] toLittleEndian(long value, int byteNum) {
        return Arrays.copyOf(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array(), byteNum);
    }

    /**
     * リトルエンディアンのバイト配列をBigIntegerに変換する。
     * @param value リトルエンディアンのバイト配列
     * @return 変換されたBigIntegerオブジェクト
     */
    public static BigInteger fromLittleEndianToBigInteger(byte[] value)
    {
        byte[] tmp = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        System.arraycopy(value, 0, tmp, 0, value.length);
        return new BigInteger(reverse(tmp));
    }

    /**
     * リトルエンディアンからlong値へ変換
     * @param value 元データ
     * @return long値
     */
    public static long fromLittleEndianToLong(byte[] value) {
        byte[] tmp = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        System.arraycopy(value, 0, tmp, 0, value.length);
        return ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    /**
     * リトルエンディアンからintへ変換
     * @param value 元データ
     * @return int値
     */
    public static int fromLittleEndianToInt(byte[] value) {
        byte[] tmp = {0,0,0,0};
        System.arraycopy(value, 0, tmp, 0, value.length);
        return ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    /**
     * リトルエンディアンからshortへ変換
     * @param value 元データ
     * @return short値
     */
    public static short fromLittleEndianToShort(byte[] value) {
        byte[] tmp = {0,0};
        System.arraycopy(value, 0, tmp, 0, value.length);
        return ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    /**
     * byteの配列を繋げる
     * @param arrays 繋げる配列
     * @return 繋げた配列
     */
    public static byte[] concat(final byte[]... arrays) {
        int size = 0;
        for (byte[] array : arrays) {
            if (Objects.nonNull(array)) {
                size += array.length;
            }
        }

        byte[] ret = new byte[size];
        int n = 0;
        for (byte[] array : arrays) {
            if (Objects.nonNull(array)) {
                System.arraycopy(array, 0, ret, n, array.length);
                n += array.length;
            }
        }
        return ret;
    }
}
