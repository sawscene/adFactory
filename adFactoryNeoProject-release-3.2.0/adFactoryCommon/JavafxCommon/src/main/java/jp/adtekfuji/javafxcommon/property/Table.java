/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import jp.adtekfuji.javafxcommon.controls.DragResizer;
import javafx.scene.layout.HBox;

/**
 * ドキュメント型レイアウトペイン
 *
 * @param <E>
 * @author e-mori
 */
public class Table<E> implements RecordInterface {

    private static final String ROW_BG_PANE_TAG = "rowBg";
    private static final String ODD_ROW_STYLE = "-fx-background-color: white;";// 奇数行の背景色
    private static final String EVEN_ROW_STYLE = "-fx-background-color: transparent;";// 偶数行の背景色

    private final ObservableList<Node> parentNodes;
    private AbstractRecordFactory<E> abstractRecordFactory = null;

    private String titleName = null;
    private String titleStyle = "";
    private Boolean isAddRecord = false;
    private Boolean isSelectCheckRecord = false;
    private Boolean isColumnTitleRecord = false;
    private Boolean isChangeDataRowColor = false;// データ行の背景色を変更する？
    private final List<PropertyChangeListener> listeners = new ArrayList<>();
    private final List<Node> footerItems = new ArrayList<>();
    private EventHandler<ActionEvent> addRecordListener;
    private int maxRecord = -1;
    private int rowCount = 0;
    private int minRow = 0;

    private final VBox headerPane = new VBox();
    private final GridPane bodyPane = new GridPane();
    private final VBox footerPane = new VBox();
    private final Map<Integer, Region> resizeColumns = new HashMap<>();
    private final Map<Integer, ObservableList<Region>> resizeCells = new HashMap<>();

    /**
     * コンストラクタ
     *
     * @param parentNodes
     */
    public Table(ObservableList<Node> parentNodes) {
        this.parentNodes = parentNodes;
        if (!this.parentNodes.isEmpty()) {
            this.headerPane.setPadding(new Insets(10, 0, 4, 0));
        } else {
            this.headerPane.setPadding(new Insets(0, 0, 4, 0));
        }
        this.parentNodes.add(this.parentNodes.size(), headerPane);
        this.bodyPane.setHgap(8);
        this.bodyPane.setVgap(4);
        this.parentNodes.add(this.parentNodes.size(), bodyPane);
        this.footerPane.setPadding(new Insets(4, 0, 0, 0));
        this.parentNodes.add(this.parentNodes.size(), footerPane);
    }

    /**
     * ヘッダーのレイアウト管理の有効無効を切り替える
     *
     * @param value falseのときレイアウトを管理しない(サイズが0になる)
     * @return
     */
    public Table headerManaged(boolean value) {
        headerPane.setManaged(value);
        return this;
    }

    /**
     * フッターのレイアウト管理の有効無効を切り替える
     *
     * @param value falseのときレイアウトを管理しない(サイズが0になる)
     * @return
     */
    public Table footerManaged(boolean value) {
        footerPane.setManaged(value);
        return this;
    }

    /**
     * bodyのpadding設定
     *
     * @param value
     * @return
     */
    public Table bodyPadding(Insets value) {
        this.bodyPane.setPadding(value);
        return this;
    }
    
    /**
     * footerのpadding設定
     *
     * @param value 値
     * @return this
     */
    public Table footerPadding(Insets value) {
        this.footerPane.setPadding(value);
        return this;
    }
    
    /**
     * bodyのalignment設定
     *
     * @param value
     * @return
     */
    public Table bodyAlignment(Pos value) {
        this.bodyPane.setAlignment(value);
        return this;
    }
    
    /**
     * bodyのColumnConstraints設定
     *
     * @param constraints 設定値
     * @return this
     */
    public Table bodyColumnConstraints(List<ColumnConstraints> constraints) {
        this.bodyPane.getColumnConstraints().clear();
        this.bodyPane.getColumnConstraints().addAll(constraints);
        return this;
    }

    /**
     * bodyのgap設定
     *
     * @param vgap 垂直方向
     * @param hgap 水平方向
     * @return this
     */
    public Table bodyGap(Double vgap, Double hgap) {
        if (Objects.nonNull(vgap)) {
            this.bodyPane.setVgap(vgap);
        }
        if (Objects.nonNull(hgap)) {
            this.bodyPane.setHgap(hgap);
        }

        return this;
    }

    public Table title(String title) {
        this.titleName = title;
        return this;
    }

    public Table styleClass(String styleClass) {
        this.titleStyle = styleClass;
        return this;
    }

    public Table isAddRecord(Boolean isAddRecord) {
        this.isAddRecord = isAddRecord;
        return this;
    }

    //TODO: レコードにチェック項目をつけるか否か
    public Table isSelectCheckRecord(Boolean isSelectCheckRecord) {
        this.isSelectCheckRecord = isSelectCheckRecord;
        return this;
    }

    /**
     * データ行の背景色を変更する？
     *
     * @param value (true: する, false: しない)
     * @return
     */
    public Table isChangeDataRowColor(Boolean value) {
        this.isChangeDataRowColor = value;
        return this;
    }

    /**
     * 項目追加・削除時に発生させるイベント
     *
     * @param handler
     * @return
     */
    public Table addRecordListener(EventHandler<ActionEvent> handler) {
        this.addRecordListener = handler;
        return this;
    }

    /**
     * リスナーを追加する
     *
     * @param listener
     */
    public void addLisener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }

    /**
     * リスナーを削除する
     *
     * @param listener
     */
    public void removeLisener(PropertyChangeListener listener) {
        this.listeners.remove(listener);
    }

    public Table customFooterItem(List<Node> items) {
        this.footerItems.addAll(items);
        return this;
    }

    /**
     * タイトル行を表示する？
     *
     * @param isColumnTitleRecord
     * @return
     */
    public Table isColumnTitleRecord(Boolean isColumnTitleRecord) {
        this.isColumnTitleRecord = isColumnTitleRecord;
        return this;
    }

    public Table maxRecord(int maxRecord) {
        this.maxRecord = maxRecord;
        return this;
    }

    /*
     *
     */
    public void setAbstractRecordFactory(AbstractRecordFactory abstractRecordFactory) {
        this.abstractRecordFactory = abstractRecordFactory;
        this.setRecords();
    }

    /*
     * Set record data.
     *
     * @param records
     */
    public void setRecords() {
        List<Record> records = abstractRecordFactory.getRecords();
        abstractRecordFactory.createRecords();

        // ヘッダー
        if (Objects.nonNull(titleName)) {
            Label titleLabel = new Label(titleName);
            titleLabel.getStyleClass().add(titleStyle);
            headerPane.getChildren().add(titleLabel);
        }

        // タイトル
        if (isColumnTitleRecord) {
            Record culomnTitleRecord = new Record(this);

            // 全選択チェックボックス
            if (isSelectCheckRecord) {
                CellRecordCheckBox titleCellCheckBox = new CellRecordCheckBox(culomnTitleRecord, new SimpleBooleanProperty(false));
                CheckBox titleCheckBox = (CheckBox) titleCellCheckBox.getNode();

                titleCheckBox.setOnAction((ActionEvent e) -> {
                    listeners.stream().forEach((listener) -> {
                        listener.onSelected(titleCheckBox.isSelected());
                    });
                });

                culomnTitleRecord.addCell(titleCellCheckBox);
                abstractRecordFactory.createCulomunTitleRecord().getCells().stream().forEach((cell) -> {
                    culomnTitleRecord.addCell(cell);
                });
                addRecord(culomnTitleRecord, this.rowCount++);

            } else {
                
                for (int ii = 0; ii < abstractRecordFactory.createCulomunTitleRecord().getCells().size(); ii++) {
                    AbstractCell cell = abstractRecordFactory.createCulomunTitleRecord().getCells().get(ii);
                    if (cell.isResize() && cell.getNode() instanceof Region) {
                        resizeColumns.put(ii, (Region) cell.getNode());
                        resizeCells.put(ii, FXCollections.observableArrayList());

                    }
                    culomnTitleRecord.addCell(cell);
                }

                addRecord(culomnTitleRecord, this.rowCount++);
            }
            this.minRow = this.rowCount;
        }

        records.stream().forEach(record -> {
            this.addRecord(record, this.rowCount++);
        });

        for (Map.Entry<Integer, Region> entry : resizeColumns.entrySet()) {
            DragResizer.resizable(entry.getValue(), resizeCells.get(entry.getKey()), titleName + entry.getKey());
        }
        
        if (isAddRecord) {
            Button addRecordButton = new Button("+");
            addRecordButton.setOnAction((ActionEvent e) -> {
                if (maxRecord != -1 && maxRecord <= abstractRecordFactory.getRecodeNum()) {
                    return;
                }
                
                this.addRecord(abstractRecordFactory.addRecord(), this.rowCount++);
                if (Objects.nonNull(this.addRecordListener)) {
                    this.addRecordListener.handle(new ActionEvent());
                }
            });

            // 追加ボタンとメニュー表示ボタンを横に表示
            HBox addPane = new HBox();
            addPane.setSpacing(4);
            addPane.getChildren().add(addRecordButton);

            // 追加ボタン横のメニュー表示ボタン表示処理
            // メニュー内容の設定(この場合は該当行が無い為引数のNULLをセット)
            abstractRecordFactory
                    .createContextMenu(null)
                    .ifPresent(contextMenu -> {
                        // ボタンの設定
                        Button menuShowButton = new Button("…");
                        menuShowButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e->contextMenu.show(addPane, e.getScreenX(), e.getScreenY()));
                        addPane.getChildren().add(menuShowButton);
                    });

            footerPane.getChildren().add(addPane);
        }
        if (!footerItems.isEmpty()) {
            footerItems.stream().forEach((item) -> {
                footerPane.getChildren().add(item);
            });
        }
    }

    /**
     * 
     * @return
     */
    public List<Object> getCheckedRecordItems() {
        return abstractRecordFactory.getCheckedRecordItems();
    }

    /**
     * 行を追加する。
     *
     * @param record 行データ
     * @param rowIndex 行番号
     */
    @Override
    public void addRecord(Record record, int rowIndex) {
        System.out.println("addRecord row: " + rowIndex + ", " + record);
        record.setRowIndex(rowIndex);

        for (Integer i : resizeColumns.keySet()) {
            if (i < record.getCells().size()) {
                AbstractCell cell = record.getCells().get(i);
                if (cell.getNode() instanceof Region) {
                    List<Region> list = resizeCells.get(i);
                    list.add((Region) cell.getNode());
                }
            }
        }
                
        Platform.runLater(() -> {
            // データ行の背景色変更が有効な場合、背景設定を行なう。
            if (this.isChangeDataRowColor && (!isColumnTitleRecord || (isColumnTitleRecord && rowIndex > 0))) {
                Pane rowBackgroundPane = new Pane();
                rowBackgroundPane.setUserData(ROW_BG_PANE_TAG);
                rowBackgroundPane.autosize();
                if (rowIndex % 2 == 0) {
                    rowBackgroundPane.setStyle(EVEN_ROW_STYLE);
                } else {
                    rowBackgroundPane.setStyle(ODD_ROW_STYLE);
                }
                bodyPane.add(rowBackgroundPane, 0, rowIndex);
                GridPane.setColumnSpan(rowBackgroundPane, GridPane.REMAINING);
            }

            int column = 0;
            for (AbstractCell cell : record.getCells()) {
                bodyPane.add(cell.getNode(), column++, rowIndex);
            }
        });
    }

    /**
     * 行を削除する。
     *
     * @param record
     */
    @Override
    public void removeRecord(Record record) {
        Platform.runLater(() -> {
            System.out.println("removeRecord row:" + record.getRowIndex() + ", " + record);
            abstractRecordFactory.removeRecord(record);
            int row = record.getRowIndex();
            List<Node> children = new LinkedList<>(bodyPane.getChildren());
            for (Node node : children) {
                int nodeRow = GridPane.getRowIndex(node);
                if (nodeRow == row) {
                    bodyPane.getChildren().remove(node);
                } else if (nodeRow > row) {
                    GridPane.setRowIndex(node, nodeRow - 1);
                }
            }
            rowCount--;

            List<Record> records = abstractRecordFactory.getRecords();
            int number;
            if (isColumnTitleRecord) {
                number = this.minRow;
            } else {
                number = 1;
            }
            for (Record r : records) {
                if (r.getRowIndex() > row) {
                    r.setRowIndex(r.getRowIndex() - 1);
                }
                for (AbstractCell cell : r.getCells()) {
                    if (cell instanceof CellAutoNumberLabel) {
                        ((CellAutoNumberLabel) cell).updateNumber(number);
                    }
                }
                number++;
            }

            if (Objects.nonNull(this.addRecordListener)) {
                this.addRecordListener.handle(new ActionEvent());
            }
        });
    }

    /**
     * 行を全て削除する。
     */
    @Override
    public void clearRecord() {
        Platform.runLater(() -> {
            bodyPane.getChildren().clear();
        });
        abstractRecordFactory.clearRecord();
        parentNodes.clear();
        rowCount = 0;
    }

    @Override
    public void registCheckListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * 行の表示順を上げる。
     *
     * @param record
     */
    @Override
    public void increaseOrder(Record record) {
        Platform.runLater(() -> {
            final int curRow = record.getRowIndex();
            if (curRow <= this.minRow) {
                return;
            }

            abstractRecordFactory.increaseOrder(record);

            final int newRow = curRow - 1;
            List<Node> children = new LinkedList<>(bodyPane.getChildren());
            for (Node node : children) {
                int row = GridPane.getRowIndex(node);
                int setRow = row;
                if (row == curRow) {
                    GridPane.setRowIndex(node, newRow);
                    setRow = newRow;
                } else if (row == newRow) {
                    GridPane.setRowIndex(node, curRow);
                    setRow = curRow;
                }

                // データ行の背景色変更が有効な場合、背景設定を行なう。
                if (this.isChangeDataRowColor && (!isColumnTitleRecord || (isColumnTitleRecord && setRow > 0))) {
                    if (node instanceof Pane && GridPane.getColumnIndex(node) == 0) {
                        Object userData = node.getUserData();
                        if (userData instanceof String && ROW_BG_PANE_TAG.equals(userData)) {
                            if (setRow % 2 == 0) {
                                node.setStyle(EVEN_ROW_STYLE);
                            } else {
                                node.setStyle(ODD_ROW_STYLE);
                            }
                        }
                    }
                }
            }

            List<Record> records = abstractRecordFactory.getRecords();
            int ii = this.minRow;
            int number;
            if (isColumnTitleRecord) {
                number = this.minRow;
            } else {
                number = 1;
            }
            for (Record rec : records) {
                rec.setRowIndex(ii);
                for (AbstractCell cell : rec.getCells()) {
                    if (cell instanceof CellAutoNumberLabel) {
                        ((CellAutoNumberLabel) cell).updateNumber(number);
                    }
                }
                ii++;
                number++;
            }
        });
    }

    /**
     * 行の表示順を下げる。
     *
     * @param record
     */
    @Override
    public void decreaseOrder(Record record) {
        Platform.runLater(() -> {
            final int curRow = record.getRowIndex();
            if (curRow >= (this.rowCount - 1)) {
                return;
            }

            abstractRecordFactory.decreaseOrder(record);

            final int newRow = curRow + 1;
            List<Node> children = new LinkedList<>(bodyPane.getChildren());
            for (Node node : children) {
                int row = GridPane.getRowIndex(node);
                int setRow = row;
                if (row == curRow) {
                    GridPane.setRowIndex(node, newRow);
                    setRow = newRow;
                } else if (row == newRow) {
                    GridPane.setRowIndex(node, curRow);
                    setRow = curRow;
                }

                // データ行の背景色変更が有効な場合、背景設定を行なう。
                if (this.isChangeDataRowColor && (!isColumnTitleRecord || (isColumnTitleRecord && setRow > 0))) {
                    if (node instanceof Pane && GridPane.getColumnIndex(node) == 0) {
                        Object userData = node.getUserData();
                        if (userData instanceof String && ROW_BG_PANE_TAG.equals(userData)) {
                            if (setRow % 2 == 0) {
                                node.setStyle(EVEN_ROW_STYLE);
                            } else {
                                node.setStyle(ODD_ROW_STYLE);
                            }
                        }
                    }
                }
            }

            List<Record> records = abstractRecordFactory.getRecords();
            int ii = this.minRow;
            int number;
            if (isColumnTitleRecord) {
                number = this.minRow;
            } else {
                number = 1;
            }
            for (Record rec : records) {
                rec.setRowIndex(ii);
                for (AbstractCell cell : rec.getCells()) {
                    if (cell instanceof CellAutoNumberLabel) {
                        ((CellAutoNumberLabel) cell).updateNumber(number);
                    }
                }
                ii++;
                number++;
            }
        });
    }

    /**
     * タイトルを表示するかどうか
     *
     * @param isTitle
     */
    @Override
    public void setTitle(boolean isTitle) {
        this.minRow = isTitle ? 1 : 0;
    }

    /**
     * Tableのbodyからインデックスを指定してNodeを取得する
     *
     * @param row
     * @param column
     * @return
     */
    public Optional<Node> getNodeFromBody(int row, int column) {
        return this.bodyPane.getChildren().stream()
                .filter(node -> GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column)
                .findAny();
    }

    /**
     * TableのBodyが指定した文字列をLabelとして持つ場合その行のインデックスを返す
     *
     * @param text ラベルの文字列
     * @return 0から始まる行インデックス
     */
    public Optional<Integer> findLabelRow(String text) {
        return this.bodyPane.getChildren().stream()
                .filter(p -> p instanceof Label && Objects.equals(((Label) p).getText(), text))
                .map(node -> GridPane.getRowIndex((Label) node))
                .findAny();
    }

    /**
     * bodyにNodeを追加する
     *
     * @param node
     * @param columnIndex
     * @param rowIndex
     */
    public void addNodeToBody(Node node, int columnIndex, int rowIndex) {
        this.bodyPane.add(node, columnIndex, rowIndex);
    }

    /**
     * 表示順を挿入。 this.rowCountは関数内でカウントアップしています。
     *
     * @param record 貼り付け対象Recordクラス
     * @param index 貼り付け行の行番号(1～) ※-1の場合は最終行に追加
     */
    public void insertRecord(Record record, final int index) {
        if (Objects.isNull(record)) {
            return;
        }

        Platform.runLater(() -> {
            List<Node> children = bodyPane.getChildren();
            final int insertIndex = index + this.minRow;

            // 挿入行より下をずらす
            children.forEach(node -> {
                final int row = GridPane.getRowIndex(node);
                if (insertIndex <= row) {
                    GridPane.setRowIndex(node, row + 1);
                }
            });

            // 挿入
            int column = 0;
            for (AbstractCell cell : record.getCells()) {
                bodyPane.add(cell.getNode(), column++, insertIndex);
            }

            // データ行の背景色変更が有効な場合、背景設定を行なう。
            if (this.isChangeDataRowColor) {
                children.stream()
                        .filter(node -> !isColumnTitleRecord || GridPane.getRowIndex(node) > 0)
                        .filter(node -> GridPane.getColumnIndex(node) == 0)
                        .filter(node -> node instanceof Pane)
                        .filter(node -> ROW_BG_PANE_TAG.equals(node.getUserData()))
                        .forEach(node -> {
                            final String style = GridPane.getRowIndex(node) % 2 == 0
                                    ? EVEN_ROW_STYLE
                                    : ODD_ROW_STYLE;
                            node.setStyle(style);
                        });
            }

            List<Record> records = abstractRecordFactory.getRecords();
            int ii = this.minRow;
            int number = isColumnTitleRecord ? this.minRow : 1;
            for (Record rec : records) {
                rec.setRowIndex(ii);
                for (AbstractCell cell : rec.getCells()) {
                    if (cell instanceof CellAutoNumberLabel) {
                        ((CellAutoNumberLabel) cell).updateNumber(number);
                    }
                }
                ++ii;
                ++number;
            }

            if (Objects.nonNull(this.addRecordListener)) {
                this.addRecordListener.handle(new ActionEvent());
            }
        });

        ++this.rowCount;
    }

    //レコードにチェック項目をつけるか否か
    public boolean getIsSelectCheckRecord() {
        return this.isSelectCheckRecord;
    }
    
    /**
     * レイアウトを調整する。
     */
    public void adjustLayout() {
        this.resizeCells.values().forEach(o -> {
            Label dummy = new Label();
            o.add(dummy);
            o.remove(dummy);
        });
    }

    /**
     * タイトル名を取得する。
     * 
     * @return タイトル名
     */
    public String getTitleName() {
        return titleName;
    }

    /**
     * 編集禁止に設定する。
     * 
     * @param disabled 
     */
    public void setDisable(Boolean disabled) {
        this.footerPane.setDisable(disabled);
    }

    /**
     * 行数を取得する。
     *
     * @return 行数
     */
    public int getRowCount() {
        return this.rowCount;
    }
}
