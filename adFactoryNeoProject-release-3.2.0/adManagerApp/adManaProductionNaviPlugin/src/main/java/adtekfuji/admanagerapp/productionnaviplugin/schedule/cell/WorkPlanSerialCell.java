/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.schedule.cell;

import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
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
public class WorkPlanSerialCell extends GridPane {

    private static String PLAN_LABEL = LocaleUtils.getString("key.VisiblePlan");// 計画
    private static String ACTUAL_LABEL = LocaleUtils.getString("key.ChartSeries");// 実績

    private static final String NEW_LINE = "\r\n ";

    private Label kanbanLabel = new Label();
    private Label planLabel = new Label(PLAN_LABEL);
    private Label actualLabel = new Label(ACTUAL_LABEL);

    /**
     * 初期生成処理
     *
     * @param kanbanName カンバン名
     * @param modelName モデル名
     * @param workName 工程順名
     * @param orderNo オーダー番号
     * @param serial シリアル
     * @param prefHight セルの高さ
     */
    public WorkPlanSerialCell(String kanbanName, String modelName, String workName, String orderNo, String serial, double prefHight) {
        //セル生成

        StringBuilder title = new StringBuilder(kanbanName);
        if (Objects.nonNull(modelName) && !modelName.isEmpty()) {
            title.append(NEW_LINE);
            title.append(modelName);
        }
        if (Objects.nonNull(workName) && !workName.isEmpty()) {
            title.append(NEW_LINE);
            title.append(workName);
        }
        if (Objects.nonNull(orderNo) && !orderNo.isEmpty()) {
            title.append(NEW_LINE);
            title.append(orderNo);
        }
        if (Objects.nonNull(serial) && !serial.isEmpty()) {
            title.append(NEW_LINE);
            title.append(serial);
        }

        this.planLabel.setText(LocaleUtils.getString("key.VisiblePlan"));
        this.actualLabel.setText(LocaleUtils.getString("key.ChartSeries"));
        this.kanbanLabel.setText(title.toString());
        this.createColumn();
        this.createRow();

        this.add(createAnchor(this.kanbanLabel), 0, 0, 1, 2);
        this.add(createAnchor(this.planLabel), 1, 0);
        this.add(createAnchor(this.actualLabel), 1, 1);
        this.setPrefHeight(prefHight);

        this.setSelectedStyle(false);
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
     * 選択状態のスタイルを設定する。
     *
     * @param isSelected (true: 選択状態, false：非選択状態)
     */
    public void setSelectedStyle(boolean isSelected) {
        
        String style;
        if (isSelected) {
            style = "-fx-background-color: #0096c9; -fx-border-width: 0.3; -fx-border-color: lightgray; -fx-text-fill: white;";
        } else {
            style = "-fx-background-color: transparent; -fx-border-width: 0.3; -fx-border-color: lightgray; -fx-text-fill: black;";
        }

        this.kanbanLabel.setStyle(style);
        this.planLabel.setStyle(style);
        this.actualLabel.setStyle(style);
    }
}
