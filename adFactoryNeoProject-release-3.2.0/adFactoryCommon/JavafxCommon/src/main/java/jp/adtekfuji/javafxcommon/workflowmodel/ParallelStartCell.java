/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.workflowmodel;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import adtekfuji.locale.LocaleUtils;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.utility.DataFormatUtil;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.javafxcommon.enumeration.MarkerStatus;

/**
 *
 * @author ta.ito
 */
public class ParallelStartCell extends CellBase {

    private ParallelEndCell parallelEndCell;
    private final HBox hbox = new HBox();
    private final VBox vBox = new VBox();
    private final ToggleButton toggleButton;

    private CellBase prevCell = null;

    public ParallelStartCell() {
        this(true);
    }

    public ParallelStartCell(boolean editable) {
        super(editable);

        this.toggleButton = new ToggleButton();
        this.toggleButton.setText(LocaleUtils.getString("key.StartParallelFlow"));
        this.toggleButton.setMinWidth(100.0);
        this.toggleButton.setFocusTraversable(editable);
        cellPane.getChildren().add(0, toggleButton);
        vBox.getChildren().add(cellPane);
        vBox.getChildren().add(this.hbox);
        vBox.getStyleClass().add("cellBorder");
        super.getChildren().add(vBox);

        toggleButton.setOnDragDetected((MouseEvent event) -> {
            Optional.ofNullable(this.getOnDragDetected()).ifPresent(handler -> handler.handle(event));
        });
    }

    public ParallelEndCell getParallelEndCell() {
        return this.parallelEndCell;
    }

    public void setParallelEndCell(ParallelEndCell parallelEndCell) {
        this.parallelEndCell = parallelEndCell;
    }

    public HBox getParallelPane() {
        return this.hbox;
    }

    public Pane getPane() {
        return (Pane) this.vBox;
    }

    @Override
    public ToggleGroup getToggleGroup() {
        return this.toggleButton.getToggleGroup();
    }

    @Override
    public void setToggleGroup(ToggleGroup group) {
        this.toggleButton.setToggleGroup(group);
    }

    public ToggleButton getToggleButton() {
        return toggleButton;
    }

    public List<CellBase> getCells() {
        List<CellBase> list = new LinkedList<>();
        for (Node node : this.getParallelPane().getChildrenUnmodifiable()) {
            if (node instanceof VBox) {
                for (Node child : ((VBox) node).getChildren()) {
                    list.add((CellBase) child);
                }
            }
        }
        return list;
    }

    /**
     * 最初の行に存在するセルを取得する。
     *
     * @return
     */
    public List<CellBase> getFirstRow() {
        List<CellBase> list = new LinkedList<>();
        for (Node node : this.getParallelPane().getChildrenUnmodifiable()) {
            if (node instanceof VBox) {
                VBox cellBox = (VBox) node;
                if (!cellBox.getChildren().isEmpty()) {
                    list.add((CellBase) cellBox.getChildren().get(0));
                }
            }
        }
        return list;
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
        return this.toggleButton.isSelected();
    }

    @Override
    public void setSelected(boolean selectedCellBase) {
        this.toggleButton.setSelected(selectedCellBase);
    }

    @Override
    protected void showMarker(DragEvent event) {
        Dragboard dragboard = event.getDragboard();

        boolean isSame = false;
        boolean isNext = false;
        boolean isAncestor = false;
        if (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class))) {
            WorkflowFlowCellEntity entity = (WorkflowFlowCellEntity) dragboard
                    .getContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class));
            //同じセルでないか確認
            isSame = this.getBpmnNode().getId().equals(entity.getCellId());
            if (!isSame) {
                //祖先関係有無の検索
                isAncestor = this.isAncestor(entity.getCellId());
                //下のセルと同一でないか確認
                isNext = this.getFirstRow().stream().filter((CellBase cell)
                        -> cell.getBpmnNode().getId().equals(entity.getCellId())).count() > 0;
            }
        }

        if (!isSame && !isAncestor && (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkInfoEntity.class))
                || dragboard.hasContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class)))) {

            // Styleの変更
            if (!isNext && (event.getY() <= cellPane.getHeight()) && (event.getX() <= cellPane.getWidth())) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                this.marker.setStyle("-fx-border-color: black black red black; -fx-border-width: 0 0 10 0;");
                this.markerStatus = MarkerStatus.BOTTOM;
            } else {
                this.marker.setStyle("-fx-border-color: black black black black; -fx-border-width: 0 0 0 0;");
                this.markerStatus = MarkerStatus.NONE;
            }
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
    public CellBase getPrevCell() {
        return prevCell;
    }

    @Override
    public void setPrevCell(CellBase prevCell) {
        this.prevCell = prevCell;
    }

    @Override
    public void setScale(double value) {
        this.toggleButton.setPrefWidth(72.0 * value);

        // 拡大状態から縮小したとき並列工程内の並列工程間に隙間ができるため再表示を行う
        Platform.runLater(() -> {
            hbox.setManaged(false);
            hbox.layout();
            hbox.setManaged(true);
        });
    }
}
