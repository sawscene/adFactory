/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

/**
 * カンバン用定数
 *
 * @author e.mori
 * @version 1.6.1
 * @since 2017.1.19.Wen
 */
public class WorkPlanConstants {

// カンバン生成設定
    public final static String OPENING_DATE_TIME = "opening_date_time";
    public final static String CLOSING_DATE_TIME = "closing_date_time";
    public final static String LOT_QUANTITY = "one_Piece_Flow";
    public final static Integer DEFAULT_LOT_QUANTITY = 1;

    // カンバン編集画面
    public final static Long SEPARATE_WORKFLOW_ID = 0l;
    public final static String DEFAULT_OFFSETTIME = "00:00:00";
}
