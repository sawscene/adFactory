/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.workflowmodel;

import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.javafxcommon.enumeration.MarkerStatus;

/**
 *
 * @author ta.ito
 */
public abstract class CellBase extends AnchorPane {

    private BpmnNode bpmnNode;
    private boolean selectedCellBase;
    private boolean checkedCellBase;

    protected boolean editable = true;

    protected final AnchorPane cellPane = new AnchorPane();

    protected final Region marker = new Region();
    protected MarkerStatus markerStatus = MarkerStatus.NONE;

    public CellBase() {
        this(true);
    }

    public CellBase(boolean editable) {
        this.editable = editable;

        super.getChildren().add(cellPane);

        if (this.editable) {
            marker.setMouseTransparent(true);
            marker.setOpacity(0.6);

            cellPane.getChildren().add(marker);

            AnchorPane.setTopAnchor(marker, 0.0);
            AnchorPane.setRightAnchor(marker, 0.0);
            AnchorPane.setBottomAnchor(marker, 0.0);
            AnchorPane.setLeftAnchor(marker, 0.0);

            cellPane.setOnDragOver((DragEvent event) -> {
                showMarker(event);
            });

            cellPane.setOnDragEntered((DragEvent event) -> {
                System.out.println("DragEntered");
            });

            cellPane.setOnDragExited((DragEvent event) -> {
                System.out.println("DragExited");
                deleteMarker(event);
            });
        }
    }

    public BpmnNode getBpmnNode() {
        return bpmnNode;
    }

    public void setBpmnNode(BpmnNode bpmnNode) {
        this.bpmnNode = bpmnNode;
    }

    public boolean isSelected() {
        return selectedCellBase;
    }

    public void setSelected(boolean value) {
        this.selectedCellBase = value;
    }

    public boolean isChecked() {
        return checkedCellBase;
    }

    public void setChecked(boolean value) {
        this.checkedCellBase = value;
    }

    public MarkerStatus getStatus() {
        return markerStatus;
    }

    protected void showMarker(DragEvent event) {
    }

    protected void deleteMarker(DragEvent event) {
        this.marker.setStyle("-fx-border-color: black black black black; -fx-border-width: 0 0 0 0;");
        this.markerStatus = MarkerStatus.NONE;
    }

    protected CellBase getNextCell() {
        return null;
    }

    protected void setNextCell(CellBase nextCell) {
    }

    protected CellBase getPrevCell() {
        return null;
    }

    protected void setPrevCell(CellBase prevCell) {
    }

    protected ToggleGroup getToggleGroup() {
        return null;
    }

    protected void setToggleGroup(ToggleGroup group) {
    }

    /**
     * セルの拡大率を設定する。
     *
     * @param value 1.0をデフォルトのサイズとする拡大率
     */
    public void setScale(double value) {
    }

    /**
     * セルの現在の拡大率を取得する。
     *
     * @return 現在セルに設定されている1.0を基準とした拡大率
     */
    public double getScale() {
        return 1.0;
    }
}
