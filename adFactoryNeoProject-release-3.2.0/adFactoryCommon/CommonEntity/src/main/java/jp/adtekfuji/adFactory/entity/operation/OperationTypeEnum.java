package jp.adtekfuji.adFactory.entity.operation;

import adtekfuji.utility.StringUtils;
import java.util.stream.Stream;

/**
 * 操作タイプ
 * 
 * @author s-heya
 */
public enum OperationTypeEnum {
    CALL("Call"),                   // 呼出
    INDIRECT_WORK("IndirectWork"),  // 間接作業
    WORK("Work"),                   // 作業
    TRIP("Trip"),                   // 出張
    FINISH("Finish"),               // 退勤
    CHANGE_RESULT("ChangeResult");  // 実績修正
    
    private final String name;
    
    /**
     * コンストラクタ
     * 
     * @param name 名称 
     */
    OperationTypeEnum(String name) {
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
    static public OperationTypeEnum toEnum(String name) {
        return Stream.of(OperationTypeEnum.values())
                .filter(entity-> StringUtils.equals(entity.name, name))
                .findFirst()
                .orElse(null);
    }

}
