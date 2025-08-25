package adtekfuji.admanagerapp.workfloweditplugin.property;

import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.javafxcommon.property.CellInterface;
import jp.adtekfuji.javafxcommon.property.CellTextField;

import java.util.Objects;

public class CellInputRule extends CellTextField {
    final WorkPropertyInfoEntity workProperty;
    public CellInputRule(CellInterface cell, WorkPropertyInfoEntity workProperty,  boolean isDisabled) {
        super(cell, workProperty.workPropValidationRuleProperty(), isDisabled);
        this.workProperty = workProperty;
    }

    @Override
    public void createNode() {
        super.createNode();

        workProperty.workPropCategoryProperty().addListener((observable, oldValue, newValue) -> {
            if (WorkPropertyCategoryEnum.TIMESTAMP.equals(newValue)) {
                this.setDisable(true);
                this.workProperty.workPropValidationRuleProperty().setValue("");
            } else {
                this.setDisable(false);
            }
        });

        this.setDisable(WorkPropertyCategoryEnum.TIMESTAMP.equals(workProperty.getWorkPropCategory()));
    }
}


