/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.utils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;

/**
 *
 * @author fu-kato
 */
public class KanbanUtils {

    /**
     * カンバンエンティティのリストを与えられたIDの順番通りとなるよう並べ替える。IDリストに存在しないものは無視される。
     *
     * @param src 並べ替え対象となるカンバンのリスト
     * @param srcIndices このIDと同じとなるよう並べ替える
     * @return
     */
    public static List<KanbanInfoEntity> sortByIdList(List<KanbanInfoEntity> src, List<Long> srcIndices) {

        Function<Long, Optional<KanbanInfoEntity>> findById = id -> src.stream()
                .filter(entity -> id.equals(entity.getKanbanId()))
                .findAny();

        return srcIndices.stream()
                .map(findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

}
