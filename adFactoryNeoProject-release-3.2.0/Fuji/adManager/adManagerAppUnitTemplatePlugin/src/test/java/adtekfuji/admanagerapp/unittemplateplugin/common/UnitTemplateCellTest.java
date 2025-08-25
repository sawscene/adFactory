/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.common;

import javafx.stage.Stage;
import jp.adtekfuji.forfujiapp.entity.unittemplate.ConUnitTemplateAssociateInfoEntity;
import org.junit.Test;
import static org.junit.Assert.*;
import org.testfx.framework.junit.ApplicationTest;

/**
 *
 * @author fu-kato
 */
public class UnitTemplateCellTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {

    }

    @Test
    public void unit_template_name_without_revision() {
        UnitTemplateCell cell = new UnitTemplateCell(new ConUnitTemplateAssociateInfoEntity(), "SampleUnitTemplateName");
        assertEquals(cell.getUnitTemplateNameWithRev(), "SampleUnitTemplateName");
    }

    @Test
    public void unit_template_name_with_revision() {
        UnitTemplateCell cell = new UnitTemplateCell(new ConUnitTemplateAssociateInfoEntity(), "SampleUnitTemplateName", 3);
        assertEquals(cell.getUnitTemplateNameWithRev(), "SampleUnitTemplateName : 3");
    }
}
