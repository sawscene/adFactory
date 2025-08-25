/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin.schedule.cell;

import jp.adtekfuji.forfujiapp.utils.StyleInjecter;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * アジェンタに表示するシリアルのタイトル用セル
 *
 * @author e-mori
 * @version Fver
 * @since 2016.07.15.Fri
 */
public class SerialCell extends GridPane {

    private static final String PLAN_LABEL = "計画";
    private static final String ACTUAL_LABEL = "実績";

    private final Label serialLabel = new Label();
    private final Label planLabel = new Label(PLAN_LABEL);
    private final Label actualLabel = new Label(ACTUAL_LABEL);

    /**
     * 初期生成処理
     *
     * @param serialName シリアル名
     * @param prefHight セルの高さ
     */
    public SerialCell(String serialName ,double prefHight) {
        //セル生成
        this.serialLabel.setText(serialName);
        this.createColumn();
        this.createRow();
        this.add(createAnchor(serialLabel), 0, 0, 1, 2);
        this.add(createAnchor(planLabel), 1, 0);
        this.add(createAnchor(actualLabel), 1, 1);
        this.setPrefHeight(prefHight);
        this.setStyle();
    }

    /**
     * グリッドのカラム生成
     *
     */
    private void createColumn() {
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        this.getColumnConstraints().addAll(column1, column2);
    }

    /**
     * グリッドのロウ生成
     */
    private void createRow() {
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        row1.setVgrow(Priority.ALWAYS);
        row2.setVgrow(Priority.ALWAYS);
        this.getRowConstraints().addAll(row1, row2);
    }

    /**
     * ノードの親にAnchorPaneを挿入する
     *
     * @param node
     * @return
     */
    private AnchorPane createAnchor(Node node) {
        AnchorPane anchor = new AnchorPane();
        anchor.getChildren().add(node);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        return anchor;
    }

    /**
     * スタイルクラス生成
     *
     */
    private void setStyle() {
        StyleInjecter.setBorderColorStyle(this.serialLabel, "gray");
        StyleInjecter.setBorderColorStyle(this.planLabel, "gray");
        StyleInjecter.setBorderColorStyle(this.actualLabel, "gray");
    }
}
