/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.MenuItem;

/**
 *
 * @author e-mori
 */
public class Record implements CellInterface {

    private final RecordInterface recordInterface;
    private HBox recordPane = null;
    private final LinkedList<AbstractCell> cells = new LinkedList<>();
    private ObservableList<Node> parentNodes;
    private boolean isRemoveRecord = true;
    private boolean isSelectCheckRecord = false;
    private final BooleanProperty isChecked = new SimpleBooleanProperty(false);
    private Object recordItem = null;
    private int rowIndex = 0;
    private boolean isEditableOrder = false;
    private Insets removeButtonInsets = null;
    private ContextMenu contextMenu = null;

    /*
     * Constructor
     *
     * @param table  parent Node data.
     */
    public Record(RecordInterface recordInterface) {
        this.recordInterface = recordInterface;
    }

    /*
     * Constructor
     *
     * @param table  parent Node data.
     * @param isRemoveRecord remove record property.
     */
    public Record(RecordInterface recordInterface, Boolean isRemoveRecord) {
        this.recordInterface = recordInterface;
        this.isRemoveRecord = isRemoveRecord;
    }

    /**
     * レコードに変換した元のオブジェクトを取得
     *
     * @return 変換元オブジェクト
     */
    public Object getRecordItem() {
        return recordItem;
    }

    /**
     * レコードに変換した元のオブジェクトをセット
     *
     * @param recordItem 変換元オブジェクト
     */
    public void setRecordItem(Object recordItem) {
        this.recordItem = recordItem;
    }

    /*
     * create Record.
     * Record draws the cell you have.
     */
    private void createNode() {
        this.recordPane = new HBox();
        this.recordPane.setSpacing(2.0);
        this.parentNodes = recordPane.getChildren();
    }

    /*
     * return cell data.
     *
     */
    @Override
    public List<AbstractCell> getCells() {
        return this.cells;
    }

    public void setTitleCells(List<AbstractCell> cells) {
        cells.stream().forEach((cell) -> {
            this.addCell(cell);
        });
        this.recordInterface.setTitle(true);
    }

    /*
     *
     */
    public void setCells(List<AbstractCell> cells) {
        if (isSelectCheckRecord) {
            CellRecordCheckBox checkBox = new CellRecordCheckBox(this, isChecked);
            this.recordInterface.registCheckListener(checkBox);
            this.addCell(checkBox);
        }
        cells.stream().forEach((cell) -> {
            this.addCell(cell);
        });
        if (isRemoveRecord) {
            CellRemoveButton crb = new CellRemoveButton(this);
            if (Objects.nonNull(removeButtonInsets)) {
                GridPane.setMargin(crb.getNode(), removeButtonInsets);
            }
            this.addCell(crb);
        }

        // 表示順が編集可の場合、[▲][▼]ボタンを表示
        if (this.isEditableOrder) {
            this.addCell(new CellIncreaseButton(this));
            this.addCell(new CellDecreaseButton(this));

            // メニュー表示ボタン表示処理
            if (Objects.nonNull(this.contextMenu)) {
                CellMenuShowButton menuShowButton = new CellMenuShowButton(this, this.contextMenu);
                this.addCell(menuShowButton);
            }
        }
    }

    /*
     *
     */
    public void setIsRemoveRecord(boolean isRemoveRecord) {
        this.isRemoveRecord = isRemoveRecord;
    }
    
    /**
     * 削除ボタンのマージンを設定する
     *
     * @param insets マージン
     */
    public void setRemoveButtonInsets(Insets insets) {
        this.removeButtonInsets = insets;
    }

    /**
     * TODO: レコードにチェック項目を追加するか否か
     *
     * @param isSelectCheckRecord
     * @return
     */
    public Record IsSelectCheckRecord(boolean isSelectCheckRecord) {
        this.isSelectCheckRecord = isSelectCheckRecord;
        return this;
    }

    public boolean isEditableOrder() {
        return this.isEditableOrder;
    }

    public void setEditableOrder(boolean isEditableOrder) {
        this.isEditableOrder = isEditableOrder;
    }

    public boolean isChecked() {
        return this.isChecked.getValue();
    }

    /*
     *
     */
    public Node getNode() {
        if (Objects.isNull(recordPane)) {
            createNode();
        }
        return recordPane;
    }

    @Override
    public void addCell(AbstractCell cell) {
        cells.add(cell);
        Platform.runLater(() -> {
            if (Objects.isNull(recordPane)) {
                createNode();
            }
            parentNodes.add(parentNodes.size(), cell.getNode());
        });
    }

    @Override
    public void removeCell() {
        recordInterface.removeRecord(this);
    }

    @Override
    public void clearCell() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void checkCell() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 表示順を上げる
     */
    @Override
    public void increaseOrder() {
        this.recordInterface.increaseOrder(this);
    }

    /**
     * 表示順を下げる
     */
    @Override
    public void decreaseOrder() {
        this.recordInterface.decreaseOrder(this);
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public String toString() {
        return "Record{" + "recordInterface=" + recordInterface + ", recordPane=" + recordPane + ", cells=" + cells + ", parentNodes=" + parentNodes + ", isRemoveRecord=" + isRemoveRecord + ", isSelectCheckRecord=" + isSelectCheckRecord + ", isChecked=" + isChecked + ", recordItem=" + recordItem + ", rowIndex=" + rowIndex + '}';
    }

    /**
     * コンテキストメニューのメニュー設定をセット
     *
     * @param menuItems コンテキストメニューのメニュー設定
     */
    public void setContextMenu(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }
}
