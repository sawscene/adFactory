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
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;

/**
 *
 * @author ta.ito
 */
public class WorkflowPane extends VBox {

    private WorkflowInfoEntity workflowEntity;
    private final List<CellBase> cellList = new LinkedList<>();
    private final ToggleGroup group = new ToggleGroup();
    private CellBase selectedCellBase = null;

    public WorkflowPane() {
    }

    public WorkflowInfoEntity getWorkflowEntity() {
        return workflowEntity;
    }

    public void setWorkflowEntity(WorkflowInfoEntity workflowEntity) {
        this.workflowEntity = workflowEntity;
    }

    public List<CellBase> getCellList() {
        return cellList;
    }

    public void setCellList(List<CellBase> cellList) {
        this.cellList.addAll(cellList);
    }

    public ToggleGroup getToggleGroup() {
        return group;
    }

    public CellBase getSelectedCellBase() {
        if (Objects.nonNull(selectedCellBase) && selectedCellBase.isSelected()) {
            return selectedCellBase;
        } else {
            for (CellBase cell : cellList) {
                if (cell.isSelected()) {
                    selectedCellBase = cell;
                    return cell;
                }
            }
        }

        return null;
    }

    public List<CellBase> getCheckedCells() {
        return cellList.stream().filter(p -> p.isChecked()).collect(Collectors.toList());
    }

    /**
     * 工程セルを追加する
     *
     * @param frontCell
     * @param cell
     * @return
     */
    public boolean addNextCell(CellBase frontCell, WorkCell cell) {
        boolean ret = false;
        if (frontCell.getParent() instanceof VBox) {
            //cell追加
            cell.setToggleGroup(getToggleGroup());
            VBox vbox = (VBox) frontCell.getParent();
            int idx = vbox.getChildren().indexOf(frontCell);
            vbox.getChildren().add(idx + 1, cell);

            boolean isAdded = false;
            for (int ii = 0; ii < this.cellList.size(); ii++) {
                CellBase cellBase = this.cellList.get(ii);
                if (frontCell.equals(cellBase)) {
                    this.cellList.add(ii + 1, cell);
                    isAdded = true;
                    break;
                }
            }

            if (!isAdded) {
                this.cellList.add(cell);
            }

            frontCell.getNextCell().setPrevCell(cell);
            cell.setNextCell(frontCell.getNextCell());
            
            cell.setPrevCell(frontCell);
            frontCell.setNextCell(cell);

            ret = true;
        }

        return ret;
    }

    /**
     * 並列工程セルを追加する
     *
     * @param frontCell
     * @param gateway1
     * @param gateway2
     * @return
     */
    public boolean addNextCell(CellBase frontCell, ParallelStartCell gateway1, ParallelEndCell gateway2) {
        boolean ret = false;
        if (frontCell.getParent() instanceof VBox) {
            gateway1.setToggleGroup(getToggleGroup());
            VBox vbox = (VBox) frontCell.getParent();
            int idx = vbox.getChildren().indexOf(frontCell);
            vbox.getChildren().add(idx + 1, gateway1);
            idx = vbox.getChildren().indexOf(gateway1);
            vbox.getChildren().add(idx + 1, gateway2);
            gateway1.setParallelEndCell(gateway2);

            boolean isAdded = false;
            for (int ii = 0; ii < this.cellList.size(); ii++) {
                CellBase cellBase = this.cellList.get(ii);
                if (frontCell.equals(cellBase)) {
                    this.cellList.add(ii + 1, gateway1);
                    this.cellList.add(ii + 2, gateway2);
                    isAdded = true;
                    break;
                }
            }

            if (!isAdded) {
                this.cellList.add(gateway1);
                this.cellList.add(gateway2);
            }

            frontCell.getNextCell().setPrevCell(gateway2);
            gateway2.setNextCell(frontCell.getNextCell());
            
            gateway1.setPrevCell(frontCell);
            frontCell.setNextCell(gateway1);

            ret = true;
        }

        return ret;
    }

    /**
     * 並列工程セルに工程を追加する
     *
     * @param gateway
     * @param index
     * @param cell
     * @return
     */
    public boolean addParallelCell(ParallelStartCell gateway, int index, WorkCell cell) {
        cell.setToggleGroup(getToggleGroup());
        VBox vbox = new VBox();
        vbox.getChildren().add(cell);
        if (0 > index || gateway.getParallelPane().getChildren().size() < index) {
            gateway.getParallelPane().getChildren().add(vbox);
        } else {
            gateway.getParallelPane().getChildren().add(index, vbox);
        }
        getCellList().add(cell);

        cell.setPrevCell(gateway);
        
        cell.setNextCell(gateway.getParallelEndCell());

        return true;
    }

    /**
     * 並列工程セルにゲートウェイを追加する
     *
     * @param gateway
     * @param index
     * @param gateway1
     * @param gateway2
     * @return
     */
    public boolean addParallelCell(ParallelStartCell gateway, int index, ParallelStartCell gateway1, ParallelEndCell gateway2) {
        VBox vbox = new VBox();

        gateway1.setToggleGroup(getToggleGroup());
        vbox.getChildren().add(gateway1);
        vbox.getChildren().add(gateway2);
        gateway1.setParallelEndCell(gateway2);

        if (0 > index || gateway.getParallelPane().getChildren().size() < index) {
            gateway.getParallelPane().getChildren().add(vbox);
        } else {
            gateway.getParallelPane().getChildren().add(index, vbox);
        }

        boolean isAdded = false;
        for (int ii = 0; ii < this.cellList.size(); ii++) {
            CellBase cellBase = this.cellList.get(ii);
            if (gateway.equals(cellBase)) {
                this.cellList.add(ii + 1, gateway1);
                this.cellList.add(ii + 2, gateway2);
                isAdded = true;
                break;
            }
        }

        if (!isAdded) {
            this.cellList.add(gateway1);
            this.cellList.add(gateway2);
        }

        gateway1.setPrevCell(gateway);
        
        gateway2.setNextCell(gateway.getParallelEndCell());
        
        return true;
    }

    /**
     * セルを削除する
     *
     * @param cell
     * @return
     */
    public boolean removeCell(CellBase cell) {

        VBox vbox = (VBox) cell.getParent();
        vbox.getChildren().remove(cell);
        if (vbox.getChildren().isEmpty() && vbox.getParent() instanceof HBox) {
            HBox hbox = (HBox) vbox.getParent();
            hbox.getChildren().remove(vbox);
        }

        this.getCellList().remove(cell);

        if(!(cell.getPrevCell() instanceof ParallelStartCell)){
            cell.getPrevCell().setNextCell(cell.getNextCell());
        }
        
        if(!(cell.getNextCell() instanceof ParallelEndCell)){
            cell.getNextCell().setPrevCell(cell.getPrevCell());
        }

        return true;
    }

    /**
     * 並列工程セルを削除する
     *
     * @param gateway1
     * @param gateway2
     * @return
     */
    public boolean removeCell(CellBase gateway1, CellBase gateway2) {

        VBox vbox = (VBox) gateway1.getParent();
        vbox.getChildren().remove(gateway1);
        vbox.getChildren().remove(gateway2);
        this.getCellList().remove(gateway1);
        this.getCellList().remove(gateway2);

        if (vbox.getChildren().isEmpty() && vbox.getParent() instanceof HBox) {
            HBox hbox = (HBox) vbox.getParent();
            hbox.getChildren().remove(vbox);
        }

        if(!(gateway1.getPrevCell() instanceof ParallelStartCell)){
            gateway1.getPrevCell().setNextCell(gateway2.getNextCell());
        }
        
        if(!(gateway2.getNextCell() instanceof ParallelEndCell)){
            gateway2.getNextCell().setPrevCell(gateway1.getPrevCell());
        }

        return true;
    }

    /**
     * 工程セルの前のセルを取得する
     *
     * @param cell
     * @return
     */
    public CellBase getPreviousCell(WorkCell cell) {
        if (cell.getParent() instanceof VBox) {
            VBox vbox = (VBox) cell.getParent();
            int idx = vbox.getChildren().indexOf(cell);
            if (idx > 0) {
                return (CellBase) vbox.getChildren().get(idx - 1);
            }
        }

        return null;
    }

    /**
     * 並列セル内の最初の工程セルを取得する
     *
     * @param cell
     * @return
     */
    public WorkCell getFirstWorkCell(ParallelStartCell cell) {
        if (cell.getParallelPane().getChildrenUnmodifiable().size() > 0) {
            if (cell.getParallelPane().getChildrenUnmodifiable().get(0) instanceof VBox) {
                VBox vbox = (VBox) cell.getParallelPane().getChildrenUnmodifiable().get(0);
                return (WorkCell) vbox.getChildren().get(0);
            }
        }

        return null;
    }

    /**
     * 並列セル内の最後の工程セルを取得する
     *
     * @param cell
     * @return
     */
    public WorkCell getLastWorkCell(ParallelStartCell cell) {
        List<WorkCell> cells = new LinkedList<>();
        addAllWorkCell(cell.getParallelPane(), cells);

        Optional<WorkCell> ret = cells.stream().max((cell1, cell2) -> cell1.getWorkflowWork().getStandardEndTime().compareTo(cell2.getWorkflowWork().getStandardEndTime()));
        if (ret.isPresent()) {
            return ret.get();
        }

        return null;
    }

    private static void addAllWorkCell(Node parent, List<WorkCell> cells) {
        if (parent instanceof HBox) {
            for (Node node : ((HBox) parent).getChildrenUnmodifiable()) {
                if (node instanceof WorkCell) {
                    cells.add((WorkCell) node);
                }
                addAllWorkCell(node, cells);
            }
        } else if (parent instanceof VBox) {
            for (Node node : ((VBox) parent).getChildrenUnmodifiable()) {
                if (node instanceof WorkCell) {
                    cells.add((WorkCell) node);
                }
                addAllWorkCell(node, cells);
            }
        } else if (parent instanceof ParallelStartCell) {
            for (Node node : ((ParallelStartCell) parent).getChildrenUnmodifiable()) {
                if (node instanceof WorkCell) {
                    cells.add((WorkCell) node);
                }
                addAllWorkCell(node, cells);
            }
        }
    }

    /**
     * ペイン生成
     *
     * @param startCell
     * @param endCell
     */
    public void createPane(StartCell startCell, EndCell endCell) {
        selectedCellBase = null;
        getChildren().clear();
        cellList.clear();

        startCell.setToggleGroup(getToggleGroup());
        getChildren().add(startCell);
        getChildren().add(endCell);

        cellList.add(startCell);
        cellList.add(endCell);

        startCell.setNextCell(endCell);
        endCell.setPrevCell(startCell);
    }

    /**
     * ペイン生成
     *
     * @param startCell
     * @param endCell
     */
    public void createPane(ParallelStartCell startCell, ParallelEndCell endCell) {
        selectedCellBase = null;
        getChildren().clear();
        cellList.clear();

        startCell.setToggleGroup(getToggleGroup());
        getChildren().add(startCell);
        getChildren().add(endCell);

        cellList.add(startCell);
        cellList.add(endCell);

        startCell.setParallelEndCell(endCell);
        endCell.setParallelStartCell(startCell);
    }

    /**
     * 工程フローに存在するセルの拡大率を設定する。
     *
     * @param value
     */
    public void setScale(double value) {
        cellList.stream().forEach(cell -> {
            cell.setScale(value);
        });
    }

    /**
     * 工程セルの設備名の表示有無を設定する。
     *
     * @param value trueの場合、各工程セルの設備名を表示する。
     */
    public void setVisibleEquipment(Boolean value) {
        cellList.stream().forEach(cell -> {
            if (cell instanceof WorkCell) {
                ((WorkCell) cell).setVisibleEquipment(value);
            }
        });
    }

    /**
     * 工程セルの組織名の表示有無を設定する。
     *
     * @param value
     */
    public void setVisibleOrganization(Boolean value) {
        cellList.stream().forEach(cell -> {
            if (cell instanceof WorkCell) {
                ((WorkCell) cell).setVisibleOrganization(value);
            }
        });
    }
}
