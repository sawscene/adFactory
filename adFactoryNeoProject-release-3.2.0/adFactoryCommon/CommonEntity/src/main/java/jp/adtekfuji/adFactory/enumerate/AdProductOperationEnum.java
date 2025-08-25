package jp.adtekfuji.adFactory.enumerate;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum AdProductOperationEnum {

    CALL("Call"), // 呼出
    INDIRECT_WORK("IndirectWork"); // 間接作業

    final private String name;
    final public AdFactoryApplicationEnum adProduct = AdFactoryApplicationEnum.ADPRODUCT;

    AdProductOperationEnum(String name) {
        this.name = name;
    }

    /**
     * 名称->Enumへ変換
     *
     * @param name
     * @return
     */
    public static AdFactoryApplicationEnum toEnum(String name) {

        return Arrays
                .stream(AdFactoryApplicationEnum.values())
                .filter(l -> StringUtils.equals(name, l.toString()))
                .findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return name;
    }
}
