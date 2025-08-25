package jp.adtekfuji.prodcountreporter.json;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class Request {
    public List<Order> Orders;
    public String ProcessCode;
    public String MachineNo;
    public Date StartDateTime;
    public Date EndDateTime;
    public String Note = "";
    public String MainWorker;
    public List<String> Workers = new ArrayList<>();
    public List<Interrupt> Interrupts = new ArrayList<>();

    public List<String> CheckData() {
        List<String> data = new ArrayList<>();

        if (Orders.stream().anyMatch(l->isNotHalfAlphanumeric(l.OrderNo) || l.OrderNo.length() > 9)) {
            data.add("オーダ番号データ異常");
        }

        if (Orders.stream().anyMatch(l-> (!l.Serial.isEmpty() && isNotHalfNumeric(l.Serial)) || l.Serial.length() > 8)) {
            data.add("シリアルデータ異常");
        }

        if (Orders.stream().anyMatch(l->isNotHalfNumeric(l.Quantity))) {
            data.add("完了数異常");
        }

        if (isNotHalfAlphanumeric(this.ProcessCode) || this.ProcessCode.length() > 8) {
            data.add("工程コードデータ異常");
        }
        if (isNotHalfNumeric(this.MachineNo) || this.MachineNo.length() > 2) {
            data.add("機械番号データ異常");
        }
        if (this.EndDateTime.before(this.StartDateTime)) {
            data.add("実施時間設定異常");
        }

        if(isNotHalfAlphanumeric(this.MainWorker) || this.MainWorker.length()>5) {
            data.add("代表者職番異常");
        }

        if (Workers.stream()
                .anyMatch(worker -> isNotHalfAlphanumeric(worker) || worker.length() > 5)) {
            data.add("職番異常");
        }

        if (Interrupts.stream().anyMatch(interrupt -> interrupt.EndDateTime.before(interrupt.StartDateTime))) {
            data.add("中断時間設定異常");
        }

        return data;
    }


    private static final String HAS_HALF_ALPHANUMERIC = "^[0-9a-zA-Z-]+$";
    public static boolean isNotHalfAlphanumeric(String str) {
        if (Objects.isNull(str)) {
            return true;
        }

        if (str.isEmpty()) {
            return true;
        }

        return !str.matches(HAS_HALF_ALPHANUMERIC);
    }

    private static final String HAS_HALF_NUMERIC = "^[0-9]+$";
    public static boolean isNotHalfNumeric(String str) {
        if (Objects.isNull(str)) {
            return true;
        }

        if (str.isEmpty()) {
            return true;
        }

        return !str.matches(HAS_HALF_NUMERIC);
    }

    public Map<String, String> createMessage() {
        final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Map<String, String> ret = new HashMap<>();
        ret.put("[[Order, Serial, Quantity]]", "[" + this.Orders.stream().map(l -> "[" + l.OrderNo + ", " + l.Serial + "," + l.Quantity + "]").collect(Collectors.joining(", ")) + "]");
        ret.put("ProcessCode",this.ProcessCode);
        ret.put("MachineNo", this.MachineNo);
        ret.put("Workers", this.Workers.toString());
        try {
            ret.put("StartDateTime", df.format(this.StartDateTime));
            ret.put("EndDateTime", df.format(this.EndDateTime));
        } catch (Exception ignored) {

        }
        return ret;
    }

}
