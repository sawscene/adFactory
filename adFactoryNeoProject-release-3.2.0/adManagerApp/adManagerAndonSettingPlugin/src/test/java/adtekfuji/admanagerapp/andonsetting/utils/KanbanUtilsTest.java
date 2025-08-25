/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.utils;

import java.util.Arrays;
import java.util.List;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Test;

/**
 *
 * @author fu-kato
 */
public class KanbanUtilsTest {

    public KanbanUtilsTest() {
    }

    /**
     * Test of sortByIdList method, of class KanbanUtils.
     */
    @Test
    public void testSortByIdList() {

        List<KanbanInfoEntity> src = Arrays.asList(
                new KanbanInfoEntity(1L, 100L, "kanban1", "KANBAN1"),
                new KanbanInfoEntity(2L, 100L, "kanban2", "KANBAN2"),
                new KanbanInfoEntity(3L, 100L, "kanban3", "KANBAN3")
        );

        List<Long> srcIndices = Arrays.asList(4L, 3L, 2L, 1L, 0L);

        List<KanbanInfoEntity> result = KanbanUtils.sortByIdList(src, srcIndices);

        assertThat(result, contains(
                new KanbanInfoEntity(3L, 100L, "kanban3", "KANBAN3"),
                new KanbanInfoEntity(2L, 100L, "kanban2", "KANBAN2"),
                new KanbanInfoEntity(1L, 100L, "kanban1", "KANBAN1")
        ));

    }

}
