package jp.adtekfuji.prodcountreporter.json;


import java.util.Objects;

public class Order {
    public String OrderNo;
    public String Serial;
    public String Quantity;

    public Order(String orderNo, String serial, String quantity)
    {
        this.OrderNo = Objects.nonNull(orderNo) ? orderNo : "";
        this.Serial = Objects.nonNull(serial) ? serial : "";
        this.Quantity = Objects.nonNull(quantity) ? quantity : "";
    }
}
