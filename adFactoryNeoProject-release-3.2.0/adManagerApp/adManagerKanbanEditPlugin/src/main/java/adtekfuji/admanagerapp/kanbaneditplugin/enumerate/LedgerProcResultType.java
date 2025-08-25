/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.enumerate;

/**
 * 帳票出力結果種別
 *
 * @author nar-nakamura
 */
public enum LedgerProcResultType {

    SUCCESS(0),
    SUCCESS_INCOMPLETE(1),
    ERROR_OCCURED(2),
    TEMPLATE_NONE(3),
    GET_INFO_FAILURED(4),
    TEMPLATE_UNREGISTERED(5),
    KANBAN_INCOMPLETED(6),
    FAILD_OTHER(99);

    private final Integer type;

    /**
     *
     * @param type
     */
    private LedgerProcResultType(Integer type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public Integer getType() {
        return this.type;
    }
}
