package jp.adtekfuji.adFactory.entity.operation;

import adtekfuji.utility.StringUtils;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * アプリケーションタイプ
 * 
 * @author s-heya
 */
public enum OperateAppEnum {
    ADPRODUCT("adProduct"),
    ADPRODUCTWEB("adProductWeb"),
    ADPRODUCTLITE("adProductLite"),
    ADREPORTER("adReporter"),
    ADMANAGER("adManager"),
    UNKNOWN("unknown");

    private final String name;
    
    /**
     * コンストラクタ
     * 
     * @param name 名称
     */
    OperateAppEnum(String name) {
        this.name = name;
    }

    /**
     * 名称を取得する。
     * 
     * @return 名称 
     */
    public String getName() {
        return name;
    }

    /**
     * Enum型を返す。
     * 
     * @param name 名称
     * @return Enum型
     */
    static public OperateAppEnum toEnum(String name) {
        if(Objects.isNull(name)) {
            return null;
        }

        return Stream.of(OperateAppEnum.values())
                .filter(entity-> StringUtils.equals(entity.name, name))
                .findFirst()
                .orElse(null);
    }
};
