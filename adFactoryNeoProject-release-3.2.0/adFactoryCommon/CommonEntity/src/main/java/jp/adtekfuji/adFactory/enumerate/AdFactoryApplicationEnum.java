package jp.adtekfuji.adFactory.enumerate;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum AdFactoryApplicationEnum {
    ADFACTORYSERVER("adFactoryServer"),
    ADINTERFACE("adInterface"),
    ADMANAGER("adManager"),
    ADPRODUCT("adProduct"),
    ADAGENDA("adAgendaMonitor"),
    ADMONITOR("adMonitor");

    private final String name;

    AdFactoryApplicationEnum(String name) {
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
