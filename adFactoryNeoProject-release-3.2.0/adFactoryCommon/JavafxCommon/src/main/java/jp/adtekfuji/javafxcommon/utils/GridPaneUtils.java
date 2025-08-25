/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.utils;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * GridPaneの拡張ユーティリティ
 *
 * @author s-maeda
 */
public class GridPaneUtils {

    /**
     * 座標を指定してノードを取得
     *
     * @param pane
     * @param columnIndex
     * @param rowIndex
     * @return
     */
    public static Node getNodeByMatrixIndex(GridPane pane, int columnIndex, int rowIndex) {
        Node ret = pane.getChildren().stream().filter(
                p -> (GridPane.getColumnIndex(p) == columnIndex)
                && (GridPane.getRowIndex(p) == rowIndex))
                .findFirst().get();

        return ret;
    }

    /**
     * 特定の列のノードを全て取得
     * 
     * @param pane
     * @param columnIndex
     * @return 
     */
    public static List<Node> getNodesInColumn(GridPane pane, int columnIndex) {
        List<Node> ret = new ArrayList<>();
        
        pane.getChildren().forEach(node -> {
            if (GridPane.getColumnIndex(node) == columnIndex) {
                ret.add(node);
            }
        });

        return ret;
    }

    /**
     * 特定の行のノードを全て取得
     * 
     * @param pane
     * @param rowIndex
     * @return 
     */
    public static List<Node> getNodesInRow(GridPane pane, int rowIndex) {
        List<Node> ret = new ArrayList<>();
        
        pane.getChildren().forEach(node -> {
            if (GridPane.getRowIndex(node) == rowIndex) {
                ret.add(node);
            }
        });

        return ret;
    }
}
