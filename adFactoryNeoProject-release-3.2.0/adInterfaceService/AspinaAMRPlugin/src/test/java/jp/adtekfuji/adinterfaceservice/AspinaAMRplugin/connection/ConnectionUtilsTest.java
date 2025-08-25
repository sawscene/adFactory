package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection;

import adtekfuji.utility.Tuple;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive.SelectReceiveData;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send.SendConveyProgram;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConnectionUtilsTest {

    @Test
    public void testToLittleEndian() {
        byte[] ret;

        BigInteger bigIntegerValue = new BigInteger("18446744073709551615");
        ret = ConnectionUtils.toLittleEndian(bigIntegerValue, 8);
        assertThat(ret[0], is((byte)0xFF));
        assertThat(ret[1], is((byte)0xFF));
        assertThat(ret[2], is((byte)0xFF));
        assertThat(ret[3], is((byte)0xFF));
        assertThat(ret[4], is((byte)0xFF));
        assertThat(ret[5], is((byte)0xFF));
        assertThat(ret[6], is((byte)0xFF));
        assertThat(ret[7], is((byte)0xFF));
        assertThat(bigIntegerValue, is(ConnectionUtils.fromLittleEndianToBigInteger(ret)));

        bigIntegerValue = new BigInteger("0");
        ret = ConnectionUtils.toLittleEndian(bigIntegerValue, 8);
        assertThat(ret[0], is((byte)0x00));
        assertThat(ret[1], is((byte)0x00));
        assertThat(ret[2], is((byte)0x00));
        assertThat(ret[3], is((byte)0x00));
        assertThat(ret[4], is((byte)0x00));
        assertThat(ret[5], is((byte)0x00));
        assertThat(ret[6], is((byte)0x00));
        assertThat(ret[7], is((byte)0x00));
        assertThat(bigIntegerValue, is(ConnectionUtils.fromLittleEndianToBigInteger(ret)));


        long LongValue;
        //符号なし4バイト 最大値
        LongValue = 4294967295L;
        ret = ConnectionUtils.toLittleEndian(LongValue, 4);
        assertThat(ret[0], is((byte)0xFF));
        assertThat(ret[1], is((byte)0xFF));
        assertThat(ret[2], is((byte)0xFF));
        assertThat(ret[3], is((byte)0xFF));
        assertThat(LongValue, is(ConnectionUtils.fromLittleEndianToLong(ret)));

        //符号なし4バイト 最小値
        LongValue = 0;
        ret = ConnectionUtils.toLittleEndian(LongValue, 4);
        assertThat(ret[0], is((byte)0x00));
        assertThat(ret[1], is((byte)0x00));
        assertThat(ret[2], is((byte)0x00));
        assertThat(ret[3], is((byte)0x00));
        assertThat(LongValue, is(ConnectionUtils.fromLittleEndianToLong(ret)));

        int intValue;
        //符号あり4バイト 最大値
        intValue = 2147483647;
        ret = ConnectionUtils.toLittleEndian(intValue, 4);
        assertThat(ret[0], is((byte)0xFF));
        assertThat(ret[1], is((byte)0xFF));
        assertThat(ret[2], is((byte)0xFF));
        assertThat(ret[3], is((byte)0x7F));
        assertThat(intValue, is(ConnectionUtils.fromLittleEndianToInt(ret)));

        //符号あり4バイト 最小値
        intValue = -2147483648;
        ret = ConnectionUtils.toLittleEndian(intValue, 4);
        assertThat(ret[0], is((byte)0x00));
        assertThat(ret[1], is((byte)0x00));
        assertThat(ret[2], is((byte)0x00));
        assertThat(ret[3], is((byte)0x80));
        assertThat(intValue, is(ConnectionUtils.fromLittleEndianToInt(ret)));

        short shortValue;
        //符号なし2バイト 最大値
        shortValue = (short)65535;
        ret = ConnectionUtils.toLittleEndian(65535, 2);
        assertThat(ret[0], is((byte)0xFF));
        assertThat(ret[1], is((byte)0xFF));
        assertThat(shortValue, is(ConnectionUtils.fromLittleEndianToShort(ret)));

    }

    @Test
    public void testConnect() {
        byte[] a = {0,1,2,3};
        byte[] b = {4,5,6};
        byte[] c = {7,8,9,10};

        byte[] ret = ConnectionUtils.concat(a,b,c);
        for (byte n=0; n<10; ++n) {
            assertThat(n, is(ret[n]));
        }
    }

    @Test
    public void testParseReceiveData() {

        SendConveyProgram sendConveyProgram = new SendConveyProgram(0, 0);
        Tuple<byte[], List<ReceiveData>> rest = ConnectionUtils.parseReceiveData(sendConveyProgram.crateSendData(), SelectReceiveData::decodeReceiveData);
        assertThat(rest.getLeft(), nullValue());

        byte[] data = ConnectionUtils.concat(
                sendConveyProgram.crateSendData(),
                sendConveyProgram.crateSendData());

        rest = ConnectionUtils.parseReceiveData(data, SelectReceiveData::decodeReceiveData);
        assertThat(rest.getLeft(), nullValue());

        int length = 10;
        byte[] data1 = new byte[length];
        byte[] data2 = new byte[data.length - length];
        System.arraycopy(data, 0, data1, 0, data1.length);
        System.arraycopy(data, data1.length, data2, 0, data2.length);
        rest = ConnectionUtils.parseReceiveData(data1, SelectReceiveData::decodeReceiveData);
        rest = ConnectionUtils.parseReceiveData(ConnectionUtils.concat(rest.getLeft(), data2), SelectReceiveData::decodeReceiveData);
        assertThat(rest.getLeft(), nullValue());
    }


}
