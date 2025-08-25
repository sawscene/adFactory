/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.common;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.warehouse.TrnLotTraceInfo;

/**
 * ロットトレース情報のソート用 Comparator
 *
 * @author nar-nakamura
 */
public class LotTraceComparators {

    /**
     * 日時 Comparator
     */
    private static final Comparator<LocalDateTime> localDateTimeComparator = (p1, p2) -> {
        if (p1.isBefore(p2)) {
            return -1;
        } else if (p1.isAfter(p2)) {
            return 1;
        } else {
            return 0;
        }
    };

    /**
     * ロットトレース情報の出庫番号 Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTraceDeliveryNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getDeliveryNo()) ? "" : p1.getDeliveryNo();
        String value2 = Objects.isNull(p2.getDeliveryNo()) ? "" : p2.getDeliveryNo();
        return value1.compareTo(value2);
    };

    /**
     * ロットトレース情報の明細番号 Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTraceItemNoComparator = (p1, p2) -> {
        Integer value1 = Objects.isNull(p1.getItemNo()) ? Integer.MAX_VALUE : p1.getItemNo();
        Integer value2 = Objects.isNull(p2.getItemNo()) ? Integer.MAX_VALUE : p2.getItemNo();
        return value1.compareTo(value2);
    };

    /**
     * ロットトレース情報の資材番号 Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTraceMaterialNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getMaterialNo()) ? "" : p1.getMaterialNo();
        String value2 = Objects.isNull(p2.getMaterialNo()) ? "" : p2.getMaterialNo();
        return value1.compareTo(value2);
    };

    /**
     * ロットトレース情報の製造番号(ロット番号) Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTraceLotNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getPartsNo()) ? "" : p1.getPartsNo();
        String value2 = Objects.isNull(p2.getPartsNo()) ? "" : p2.getPartsNo();
        return value1.compareTo(value2);
    };

    /**
     * ロットトレース情報の社員番号 Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTracePersonNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getPersonNo()) ? "" : p1.getPersonNo();
        String value2 = Objects.isNull(p2.getPersonNo()) ? "" : p2.getPersonNo();
        return value1.compareTo(value2);
    };

    /**
     * ロットトレース情報の作業者 Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTracePersonNameComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getPersonName()) ? "" : p1.getPersonName();
        String value2 = Objects.isNull(p2.getPersonName()) ? "" : p2.getPersonName();
        return value1.compareTo(value2);
    };

    /**
     * ロットトレース情報のカンバンID Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTraceKanbanIdComparator = (p1, p2) -> {
        Long value1 = Objects.isNull(p1.getKanbanId()) ? Long.MAX_VALUE : p1.getKanbanId();
        Long value2 = Objects.isNull(p2.getKanbanId()) ? Long.MAX_VALUE : p2.getKanbanId();
        return value1.compareTo(value2);
    };

    /**
     * ロットトレース情報のカンバン名 Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTraceKanbanNameComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getKanbanName()) ? "" : p1.getKanbanName();
        String value2 = Objects.isNull(p2.getKanbanName()) ? "" : p2.getKanbanName();
        return value1.compareTo(value2);
    };

    /**
     * ロットトレース情報のモデル名 Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTraceModelNameComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getModelName()) ? "" : p1.getModelName();
        String value2 = Objects.isNull(p2.getModelName()) ? "" : p2.getModelName();
        return value1.compareTo(value2);
    };

    /**
     * ロットトレース情報の工程名 Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTraceWorkNameComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getWorkName()) ? "" : p1.getWorkName();
        String value2 = Objects.isNull(p2.getWorkName()) ? "" : p2.getWorkName();
        return value1.compareTo(value2);
    };

    /**
     * ロットトレース情報の組付け日時 Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTraceAssemblyDatetimeComparator = (p1, p2) -> {
        LocalDateTime value1 = Objects.isNull(p1.getAssemblyDatetime()) ? LocalDateTime.MAX : p1.getAssemblyDatetime();
        LocalDateTime value2 = Objects.isNull(p2.getAssemblyDatetime()) ? LocalDateTime.MAX : p2.getAssemblyDatetime();
        return localDateTimeComparator.compare(value1, value2);
    };

    /**
     * ロットトレース情報の製造オーダー番号 Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTraceOrderNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getOrderNo()) ? "" : p1.getOrderNo();
        String value2 = Objects.isNull(p2.getOrderNo()) ? "" : p2.getOrderNo();
        return value1.compareTo(value2);
    };

    /**
     * ロットトレース情報の図番 Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTraceFigureNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getFigureNo()) ? "" : p1.getFigureNo();
        String value2 = Objects.isNull(p2.getFigureNo()) ? "" : p2.getFigureNo();
        return value1.compareTo(value2);
    };

    /**
     * ロットトレース情報の品目 Comparator
     */
    public static final Comparator<TrnLotTraceInfo> lotTraceProductNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getProductNo()) ? "" : p1.getProductNo();
        String value2 = Objects.isNull(p2.getProductNo()) ? "" : p2.getProductNo();
        return value1.compareTo(value2);
    };
}
