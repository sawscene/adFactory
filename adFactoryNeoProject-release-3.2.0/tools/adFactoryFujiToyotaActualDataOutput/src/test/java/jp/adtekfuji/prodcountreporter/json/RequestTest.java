package jp.adtekfuji.prodcountreporter.json;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

public class RequestTest {

    Request CreateRequest()
    {
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Request ret = new Request();
        ret.Orders = Arrays.asList(new Order("QC0012345","12345678", "123"), new Order("QC0012345","12345678", "123"));
        ret.ProcessCode = "MCY0";
        ret.MachineNo = "01";
        try {
            ret.StartDateTime = df.parse("2021-01-01 00:00:00");
            ret.EndDateTime   = df.parse("2021-01-02 00:00:00");
            Interrupt interrupt = new Interrupt();
            interrupt.StartDateTime=df.parse("2021-01-01 00:00:00");
            interrupt.EndDateTime=df.parse("2021-01-02 00:00:00");
            ret.Interrupts.add(interrupt);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        ret.MainWorker = "A4012";
        ret.Workers.add("A4012");

        return ret;
    }


    @Test
    public void checkData() {
        Request test1 = CreateRequest();
        assertEquals(0, test1.CheckData().size());

        // オーダ番号10文字以上はNG
        Request test2 = CreateRequest();
        test2.Orders.get(1).OrderNo = "QC00123451";
        assertEquals(1,test2.CheckData().size());

        //全角を含むとNG
        test2.Orders.get(1).OrderNo = "あいう";
        assertEquals(1,test2.CheckData().size());

        // シリアルが9文字以上はNG
        Request test3 = CreateRequest();
        test3.Orders.get(1).Serial = "123456789";
        assertEquals(1,test3.CheckData().size());

        // シリアルがアルファベットがあるとNG
        test3.Orders.get(1).Serial = "AAA";
        assertEquals(1,test3.CheckData().size());

        // シリアルが全角があるとNG
        test3.Orders.get(1).Serial = "あいう";
        assertEquals(1,test3.CheckData().size());

        test3 = CreateRequest();
        // 個数がアルファベットがる場合はNG
        test3.Orders.get(1).Quantity = "AAA";
        assertEquals(1,test3.CheckData().size());

        // 個数が全角があるとNG
        test3.Orders.get(1).Quantity = "あいう";
        assertEquals(1,test3.CheckData().size());

        // 工程コードが5文字以上はNG
        Request test4 = CreateRequest();
        test4.MachineNo = "12345";
        assertEquals(1,test4.CheckData().size());

        // 工程コードが全角を含むとNG
        test4.MachineNo = "あ";
        assertEquals(1,test4.CheckData().size());

        //作業時間が反転していたらNG
        Request test5= CreateRequest();
        Date tmp = test5.StartDateTime;
        test5.StartDateTime = test5.EndDateTime;
        test5.EndDateTime=tmp;
        assertEquals(1,test5.CheckData().size());

        //代表者は5文字以上はNG
        Request test6 = CreateRequest();
        test6.MainWorker = "123456";
        assertEquals(1,test6.CheckData().size());

        //代表者は全角はNG
        test6.MainWorker = "あ";
        assertEquals(1,test6.CheckData().size());

        //応援者は5文字以上はNG
        Request test7 = CreateRequest();
        test7.Workers.set(0, "123456");
        assertEquals(1,test7.CheckData().size());

        //応援者は全角はNG
        test7.Workers.set(0, "あ");
        assertEquals(1,test7.CheckData().size());

        Request test8= CreateRequest();
        Interrupt interrupt = new Interrupt();
        interrupt.StartDateTime = test8.Interrupts.get(0).EndDateTime;
        interrupt.EndDateTime = test8.Interrupts.get(0).StartDateTime;
        test8.Interrupts.add(interrupt);
        assertEquals(1,test8.CheckData().size());

    }
}
