package jp.adtekfuji.prodcountreporter.json;

import org.junit.Test;

import static org.junit.Assert.*;

public class RequestTest {

    Request CreateRequest() {

        Request ret = new Request();
        Request.InData indata = new Request.InData();
        indata.aufnr = "BBBBBBBBBBBB";
        indata.vornr = "AAAA";
        indata.lmnga = "10";
        indata.budat = "20250312";
        ret.inData = indata;

        return ret;
    }

    @Test
    public void checkData() {
        Request test1 = CreateRequest();
        assertEquals(0, test1.CheckData().size());

        // 指図番号13文字以上はNG
        Request test2 = CreateRequest();
        test2.inData.aufnr = "ABCDIFGHIJKLM";
        assertEquals(1, test2.CheckData().size());

        // 全角を含むとNG
        test2.inData.aufnr = "ＡＢＣＤ";
        assertEquals(1, test2.CheckData().size());

        // 作業/作業番号が4文字以外はNG
        Request test3 = CreateRequest();
        test3.inData.vornr = "AAA";
        assertEquals(1, test3.CheckData().size());

        // 全角を含むとNG
        test3.inData.vornr = "ＡＢＣ";
        assertEquals(1, test3.CheckData().size());

        // 確認対象歩留が整数部分10桁以上または小数部分3桁以上であればNG
        Request test4 = CreateRequest();
        test4.inData.lmnga = "12345678911";
        assertEquals(1, test4.CheckData().size());

        // 転記日付が8文字以外はNG
        Request test5 = CreateRequest();
        test5.inData.budat = "202503151";
        assertEquals(1, test5.CheckData().size());

        // 全角を含むとNG
        test5.inData.budat = "２０２５０３１５";
        assertEquals(1, test3.CheckData().size());

        // 数値以外を含むとNG
        test5.inData.budat = "2025031A";
        assertEquals(1, test3.CheckData().size());
    }
}
