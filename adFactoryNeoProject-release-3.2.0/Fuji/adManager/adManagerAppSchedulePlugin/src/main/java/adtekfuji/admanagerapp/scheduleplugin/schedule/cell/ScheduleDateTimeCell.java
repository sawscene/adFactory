/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin.schedule.cell;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import jp.adtekfuji.forfujiapp.utils.StyleInjecter;

/**
 * サンプルデータの注入
 *
 * @author e-mori
 * @version Fver
 * @since 2016.07.06.thr
 */
public class ScheduleDateTimeCell extends GridPane {

    private final Label serialLabel = new Label();
    private final HBox timePane = new HBox();

    /**
     * 初期生成処理
     *
     * @param serialName シリアル名
     * @param prefWidth セルの幅
     * @param prefHight セルの高さ
     */
    public ScheduleDateTimeCell(String serialName, double prefWidth, double prefHight) {
        //セル生成
        this.serialLabel.setText(serialName);
        this.serialLabel.setAlignment(Pos.CENTER);
        this.serialLabel.setPrefSize(prefWidth, Control.USE_COMPUTED_SIZE);
        this.serialLabel.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        this.serialLabel.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        this.timePane.setPrefWidth(prefWidth);
        this.createColumn();
        this.createRow();
        this.add(createAnchor(serialLabel), 0, 0);
        this.add(timePane, 0, 1);
        for (int timeNum = 0; timeNum < 24; timeNum++) {
            Label time;
            if (timeNum < 10) {
                time = new Label("0" + timeNum + ":30");
            } else {
                time = new Label(timeNum + ":30");
            }
            time.setAlignment(Pos.CENTER);
            time.setPrefSize(prefWidth / 24, Control.USE_COMPUTED_SIZE);
            time.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
            time.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
            time.setRotate(270);
            timePane.getChildren().add(time);
        }
        this.setPrefSize(prefWidth, prefHight);
        this.setStyle();
    }

    /**
     * グリッドのカラム生成
     *
     */
    private void createColumn() {
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        this.getColumnConstraints().addAll(column1);
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
        StyleInjecter.setBorderColorStyle(this, "gray");
    }
}
