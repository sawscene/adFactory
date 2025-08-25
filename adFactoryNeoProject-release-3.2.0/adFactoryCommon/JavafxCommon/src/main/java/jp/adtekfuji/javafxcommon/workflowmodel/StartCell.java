/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.workflowmodel;


import adtekfuji.locale.LocaleUtils;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.utility.DataFormatUtil;
import jp.adtekfuji.javafxcommon.enumeration.MarkerStatus;

/**
 *
 * @author ta.ito
 */
public class StartCell extends CellBase {

    ToggleButton toggleButton = new ToggleButton();

    private CellBase nextCell = null;

    public StartCell() {
        this(true);
    }

    public StartCell(boolean editable) {
        super(editable);

        toggleButton.setText(LocaleUtils.getString("key.StartFlow"));
        toggleButton.setPrefSize(72, 72);
        toggleButton.setFocusTraversable(editable);
        cellPane.getChildren().add(0, toggleButton);
    }

    public ToggleButton getToggleButton() {
        return toggleButton;
    }

    @Override
    public ToggleGroup getToggleGroup() {
        return toggleButton.getToggleGroup();
    }

    @Override
    public void setToggleGroup(ToggleGroup group) {
        toggleButton.setToggleGroup(group);
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void setChecked(boolean value) {
    }

    @Override
    public boolean isSelected() {
        return toggleButton.isSelected();
    }

    @Override
    public void setSelected(boolean selectedCellBase) {
        toggleButton.setSelected(selectedCellBase);
    }

    @Override
    protected void showMarker(DragEvent event) {
        Dragboard dragboard = event.getDragboard();

        boolean isNext = false;
        if (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class))) {
            WorkflowFlowCellEntity entity = (WorkflowFlowCellEntity) dragboard.getContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class));
            //下のセルと同一でないか確認
            isNext = this.nextCell.getBpmnNode().getId().equals(entity.getCellId());
        }

        if (!isNext && (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkInfoEntity.class))
                || dragboard.hasContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class)))) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);

            // Styleの変更
            this.marker.setStyle("-fx-border-color: black black red black; -fx-border-width: 0 0 10 0;");
            this.markerStatus = MarkerStatus.BOTTOM;
        }
    }

    @Override
    public CellBase getNextCell() {
        return nextCell;
    }

    @Override
    public void setNextCell(CellBase nextCell) {
        this.nextCell = nextCell;
    }

    @Override
    public void setScale(double value) {
        this.toggleButton.setPrefSize(72.0 * value, 72.0 * value);
    }

    @Override
    public double getScale() {
        return this.toggleButton.getPrefHeight() / 72.0;
    }
}
