/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.common;

import jp.adtekfuji.javafxcommon.workflowmodel.CellBase;
import java.text.SimpleDateFormat;
import java.util.Objects;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import jp.adtekfuji.forfujiapp.entity.unittemplate.ConUnitTemplateAssociateInfoEntity;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;

/**
 * ユニットテンプレート用ワークフローセル
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.31.Mon
 */
public class UnitTemplateCell extends CellBase {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private final ConUnitTemplateAssociateInfoEntity unittemplateAssociate;
    private final CheckBox checkBox = new CheckBox();
    private final ToggleButton toggleButton = new ToggleButton();
    private final Label unittemplateNameLabel = new Label();
    private final String unittemplateName;
    private final Label revisionLabel = new Label();
    private final Integer revision;

    public UnitTemplateCell(ConUnitTemplateAssociateInfoEntity unittemplateAssociate, String unittemplateName) {
        this(unittemplateAssociate, unittemplateName, null);
    }

    public UnitTemplateCell(ConUnitTemplateAssociateInfoEntity unittemplateAssociate, String unittemplateName, Integer rev) {
        this.unittemplateAssociate = unittemplateAssociate;
        VBox vbox = new VBox();
        vbox.getChildren().add(checkBox);
        vbox.getChildren().add(unittemplateNameLabel);
        vbox.getChildren().add(revisionLabel);
        toggleButton.setGraphic(vbox);
        toggleButton.getStyleClass().add("StartEndWorkCell");
        this.getChildren().add(toggleButton);
        this.unittemplateName = unittemplateName;
        this.unittemplateNameLabel.setText(unittemplateName);
        this.revision = rev;
        if (Objects.nonNull(rev)) {
            this.revisionLabel.setText(": " + rev.toString());
        }
        if (Objects.nonNull(unittemplateAssociate)) {
            if (Objects.nonNull(unittemplateAssociate.getFkUnitTemplateId())) {
                toggleButton.setStyle(toggleButton.getStyle() + "; -fx-background-color:mediumblue ;");
                unittemplateNameLabel.setStyle(unittemplateNameLabel.getStyle() + "; -fx-text-fill: #FAFAFA;");
            } else if (Objects.nonNull(unittemplateAssociate.getFkWorkflowId())) {
                toggleButton.setStyle(toggleButton.getStyle() + "; -fx-background-color:khaki");
            }
        }

        Tooltip toolTip = TooltipBuilder.build();
        toolTip.showingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                StringBuilder sb = new StringBuilder();
                sb.append(unittemplateNameLabel.getText());
                if (!revisionLabel.getText().isEmpty()) {
                    sb.append(" : ").append(revision.toString());
                }
                sb.append(System.lineSeparator());
                String time = sdf.format(unittemplateAssociate.getStandardStartTime()) + " - " + sdf.format(unittemplateAssociate.getStandardEndTime());
                sb.append(time);
                toolTip.setText(sb.toString());
            }
        });

        toggleButton.setTooltip(toolTip);
    }

    public ConUnitTemplateAssociateInfoEntity getUnitTemplateAssociate() {
        return unittemplateAssociate;
    }

    public String getUnitTemplateName() {
        return unittemplateName;
    }

    public String getUnitTemplateNameLabelText() {
        return unittemplateNameLabel.getText();
    }

    public void setUnitTemplateNameLabelText(String text) {
        unittemplateNameLabel.setText(text);
    }

    public Integer getRevision() {
        return revision;
    }

    public String getRevisionLabelText() {
        return revisionLabel.getText();
    }

    public void setRevisionLabelText(Integer rev) {
        revisionLabel.setText(rev.toString());
    }

    public ToggleGroup getToggleGroup() {
        return toggleButton.getToggleGroup();
    }

    public void setToggleGroup(ToggleGroup group) {
        toggleButton.setToggleGroup(group);
    }

    public ToggleButton getToggleButton() {
        return toggleButton;
    }

    public void setCss(String css) {
        toggleButton.setStyle(css);
    }

    /**
     * ユニットテンプレートに版数が存在する場合、版数を" : "で接続した名前を返す。版数が存在しない場合、名前をそのまま返す。
     *
     * @return
     */
    public String getUnitTemplateNameWithRev() {
        return unittemplateName + (Objects.nonNull(revision) ? " : " + String.valueOf(revision) : "");
    }

    @Override
    public boolean isChecked() {
        return checkBox.isSelected();
    }

    @Override
    public void setChecked(boolean value) {
        checkBox.setSelected(value);
    }

    @Override
    public boolean isSelected() {
        return toggleButton.isSelected();
    }

    @Override
    public void setSelected(boolean value) {
        toggleButton.setSelected(value);
    }
}
