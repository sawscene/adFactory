/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.workflowmodel;

import java.util.Objects;

import adtekfuji.locale.LocaleUtils;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.utility.DataFormatUtil;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.javafxcommon.enumeration.MarkerStatus;

/**
 *
 * @author ta.ito
 */
public class ParallelEndCell extends CellBase {

    private ParallelStartCell parallelStartCell;

    private CellBase nextCell = null;

    public ParallelEndCell(ParallelStartCell parallelStartCell) {
        this(parallelStartCell, true);
    }

    public ParallelEndCell(ParallelStartCell parallelStartCell, boolean editable) {
        super(editable);

        this.parallelStartCell = parallelStartCell;
        Label label = new Label(LocaleUtils.getString("key.EndParallelFlow"));
        VBox vBox = new VBox();
        cellPane.getChildren().add(0, label);
        vBox.getChildren().add(cellPane);
        vBox.getStyleClass().add("cellBorder");
        vBox.prefWidthProperty().bind(parallelStartCell.getPane().widthProperty());
        super.getChildren().add(vBox);
    }

    public ParallelStartCell getParallelStartCell() {
        return parallelStartCell;
    }

    public void setParallelStartCell(ParallelStartCell parallelStartCell) {
        this.parallelStartCell = parallelStartCell;
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
        return false;
    }

    @Override
    public void setSelected(boolean selectedCellBase) {
    }

    @Override
    protected void showMarker(DragEvent event) {
        Dragboard dragboard = event.getDragboard();

        boolean isNext = false;
        boolean isAncestor = false;
        if (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class))) {
            WorkflowFlowCellEntity entity = (WorkflowFlowCellEntity) dragboard.getContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class));

            //祖先関係有無の検索
            isAncestor = this.isAncestor(entity.getCellId());
            //下のセルと同一でないか確認
            isNext = this.nextCell.getBpmnNode().getId().equals(entity.getCellId());
        }

        if (!isAncestor && !isNext && (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkInfoEntity.class))
                || dragboard.hasContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class)))) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);

            // Styleの変更
            this.marker.setStyle("-fx-border-color: black black red black; -fx-border-width: 0 0 10 0;");
            this.markerStatus = MarkerStatus.BOTTOM;
        }
    }

    /**
     * 上位にこのIDのセルが存在するか
     *
     * @param cellId
     * @return
     */
    private boolean isAncestor(String cellId) {
        CellBase tmp = this;
        while (Objects.nonNull(tmp) && !(tmp instanceof EndCell)) {
            if (tmp instanceof ParallelEndCell) {
                BpmnNode startNode = ((ParallelEndCell) tmp).getParallelStartCell().getBpmnNode();
                if (startNode.getId().equals(cellId)) {
                    return true;
                }
            }

            if (tmp instanceof ParallelStartCell) {
                tmp = ((ParallelStartCell) tmp).getParallelEndCell().getNextCell();
            } else {
                tmp = tmp.getNextCell();
            }
        }
        return false;
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
        this.parallelStartCell.setScale(value);
    }
}
