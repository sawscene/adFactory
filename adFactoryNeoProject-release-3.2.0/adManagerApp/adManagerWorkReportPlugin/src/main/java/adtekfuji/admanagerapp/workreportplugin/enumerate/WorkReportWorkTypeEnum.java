/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.enumerate;

/**
 * 作業日報の作業種別
 *
 * @author nar-nakamura
 */
public enum WorkReportWorkTypeEnum {
    /**
     * 直接作業
     */
    DIRECT_WORK(0, 1),
    /**
     * 間接作業
     */
    INDIRECT_WORK(1, 3),
    /**
     * 中断時間
     */
    NON_WORK_TIME(2, 2),

    /**
     * 後戻り作業・赤作業
     */
    REWORK(3, 1);

    private final int value;// 作業種別の値
    private final int sortOrder;// 作業種別の順

    /**
     * コンストラクタ
     *
     * @param value 作業種別の値
     * @param sortOrder 作業種別の順
     */
    private WorkReportWorkTypeEnum(int value, int sortOrder) {
        this.value = value;
        this.sortOrder = sortOrder;
    }

    /**
     * 作業種別の値を取得する。
     *
     * @return 作業種別の値
     */
    public int getValue() {
        return this.value;
    }

    /**
     * 作業種別の順を取得する。
     *
     * @return 作業種別の順
     */
    public int getSortOrder() {
        return this.sortOrder;
    }

    /**
     * 値からWorkReportWorkTypeEnumに変換する。
     * 
     * @param value 値
     * @return WorkReportWorkTypeEnum
     */
    public static WorkReportWorkTypeEnum valueOf(int value) {
        for (WorkReportWorkTypeEnum workType : WorkReportWorkTypeEnum.values()) {
            if (value == workType.value) {
                return workType;
            }
        }
        return null;
    }
}
