package jp.adtekfuji.adFactory.enumerate;

import junit.framework.TestCase;
import org.junit.Test;


public class KanbanStatusEnumTest extends TestCase {

    public void testGetEnum() {
        assertEquals(KanbanStatusEnum.PLANNING, KanbanStatusEnum.getEnum("PLANNING"));
        assertEquals(KanbanStatusEnum.PLANNED, KanbanStatusEnum.getEnum("PLANNED"));
        assertEquals(KanbanStatusEnum.WORKING, KanbanStatusEnum.getEnum("WORKING"));
        assertEquals(KanbanStatusEnum.SUSPEND, KanbanStatusEnum.getEnum("SUSPEND"));
        assertEquals(KanbanStatusEnum.INTERRUPT, KanbanStatusEnum.getEnum("INTERRUPT"));
        assertEquals(KanbanStatusEnum.COMPLETION, KanbanStatusEnum.getEnum("COMPLETION"));
        assertEquals(KanbanStatusEnum.OTHER, KanbanStatusEnum.getEnum("OTHER"));
        assertEquals(KanbanStatusEnum.DEFECT, KanbanStatusEnum.getEnum("DEFECT"));
        assertNull(KanbanStatusEnum.getEnum("hogehoge"));
        }



}