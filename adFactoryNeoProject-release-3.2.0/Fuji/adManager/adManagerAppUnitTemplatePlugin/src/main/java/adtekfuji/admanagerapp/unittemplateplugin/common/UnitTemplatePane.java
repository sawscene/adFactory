/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.common;

import adtekfuji.admanagerapp.unittemplateplugin.common.UnitTemplateCell;
import adtekfuji.locale.LocaleUtils;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jp.adtekfuji.javafxcommon.workflowmodel.CellBase;
import jp.adtekfuji.javafxcommon.workflowmodel.EndCell;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelEndCell;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelStartCell;
import jp.adtekfuji.javafxcommon.workflowmodel.StartCell;

/**
 * ユニットテンプレートBPMN描画用クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.31.Mon
 */
public class UnitTemplatePane extends VBox {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private UnitTemplateInfoEntity unitTemplateInfoEntity;
    private final List<CellBase> cellList = new LinkedList<>();
    private final ToggleGroup group = new ToggleGroup();
    private CellBase selectedCellBase = null;

    public UnitTemplatePane() {
    }

    public UnitTemplateInfoEntity getUnitTemplateEntity() {
        return unitTemplateInfoEntity;
    }

    public void setUnitTemplateEntity(UnitTemplateInfoEntity unitTemplateInfoEntity) {
        this.unitTemplateInfoEntity = unitTemplateInfoEntity;
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

    public void setSelectedCellBase(CellBase cellBase) {
        if (Objects.nonNull(cellBase)) {
            cellBase.setSelected(true);
            this.selectedCellBase = cellBase;
        }
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
     * @param frontCell 追加する先のワーク
     * @param cell 追加するセル
     * @return
     */
    public boolean addNextCell(CellBase frontCell, UnitTemplateCell cell) {
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

            ret = true;
        }

        return ret;
    }

    /**
     * 並列工程セルを追加する
     *
     * @param frontCell 追加する先のセル
     * @param gateway1 追加する並列工程開始セル
     * @param gateway2 追加する並列工程終了セル
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

            ret = true;
        }

        return ret;
    }

    /**
     * 並列工程セルに工程を追加する
     *
     * @param gateway 追加する先の並列工程
     * @param index 並列の順番
     * @param cell 追加するセル
     * @return
     */
    public boolean addParallelCell(ParallelStartCell gateway, int index, UnitTemplateCell cell) {
        cell.setToggleGroup(getToggleGroup());
        VBox vbox = new VBox();
        vbox.getChildren().add(cell);
        if (0 > index || gateway.getParallelPane().getChildren().size() < index) {
            gateway.getParallelPane().getChildren().add(vbox);
        } else {
            gateway.getParallelPane().getChildren().add(index, vbox);
        }
        getCellList().add(cell);
        return true;
    }

    /**
     * 並列工程セルにゲートウェイを追加する
     *
     * @param gateway 追加する先の並列工程
     * @param index 並列工程の順番
     * @param gateway1 並列工程開始のセル
     * @param gateway2 並列工程終了のセル
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

        return true;
    }

    /**
     * セルを削除する
     *
     * @param cell 削除するセル
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

        return true;
    }

    /**
     * 並列工程セルを削除する
     *
     * @param gateway1 削除する開始セル
     * @param gateway2 削除する終了セル
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

        return true;
    }

    /**
     * 工程セルの前のセルを取得する
     *
     * @param cell 取得したいセルの前のセル情報
     * @return
     */
    public CellBase getPreviousCell(UnitTemplateCell cell) {
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
     * @param cell 並列工程
     * @return
     */
    public UnitTemplateCell getFirstBPMNCell(ParallelStartCell cell) {
        if (cell.getParallelPane().getChildrenUnmodifiable().size() > 0) {
            if (cell.getParallelPane().getChildrenUnmodifiable().get(0) instanceof VBox) {
                VBox vbox = (VBox) cell.getParallelPane().getChildrenUnmodifiable().get(0);
                return (UnitTemplateCell) vbox.getChildren().get(0);
            }
        }

        return null;
    }

    /**
     * 並列セル内の最後の工程セルを取得する
     *
     * @param cell 並列工程
     * @return
     */
    public UnitTemplateCell getLastBPMNCell(ParallelStartCell cell) {
        List<UnitTemplateCell> cells = new LinkedList<>();
        addAllBPMNCell(cell.getParallelPane(), cells);

        Optional<UnitTemplateCell> ret = cells.stream().max((cell1, cell2) -> cell1.getUnitTemplateAssociate().getStandardEndTime().compareTo(cell2.getUnitTemplateAssociate().getStandardEndTime()));
        if (ret.isPresent()) {
            return ret.get();
        }

        return null;
    }

    /**
     * 指定されたすべてのセルデータを追加する
     *
     * @param parent 追加するためのノード
     * @param cells 追加するセル
     */
    private static void addAllBPMNCell(Node parent, List<UnitTemplateCell> cells) {
        if (parent instanceof HBox) {
            for (Node node : ((HBox) parent).getChildrenUnmodifiable()) {
                if (node instanceof UnitTemplateCell) {
                    cells.add((UnitTemplateCell) node);
                }
                addAllBPMNCell(node, cells);
            }
        } else if (parent instanceof VBox) {
            for (Node node : ((VBox) parent).getChildrenUnmodifiable()) {
                if (node instanceof UnitTemplateCell) {
                    cells.add((UnitTemplateCell) node);
                }
                addAllBPMNCell(node, cells);
            }
        } else if (parent instanceof ParallelStartCell) {
            for (Node node : ((ParallelStartCell) parent).getChildrenUnmodifiable()) {
                if (node instanceof UnitTemplateCell) {
                    cells.add((UnitTemplateCell) node);
                }
                addAllBPMNCell(node, cells);
            }
        }
    }

    /**
     * 開始セルと終了セルを追加する
     *
     * @param startCell 追加する開始セル
     * @param endCell 追加する終了セル
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
    }
}
