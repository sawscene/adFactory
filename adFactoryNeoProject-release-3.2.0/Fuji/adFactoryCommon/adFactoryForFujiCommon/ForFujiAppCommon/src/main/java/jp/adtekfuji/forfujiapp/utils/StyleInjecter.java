/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.utils;

import javafx.scene.Node;

/**
 * スタイルクラスの注入
 *
 * @author e-mori
 * @version Fver
 * @since 2016.07.06.thr
 */
public class StyleInjecter {

    /**
     * 指定されたノードに枠線をセット
     *
     * @param node
     * @param color
     */
    public static void setBorderColorStyle(Node node, String color) {
        node.setStyle(node.getStyle() + "; -fx-border-width: 0.5; -fx-border-color: " + color + ";");
    }

    /**
     * 指定されたノードに背景色をセット
     *
     * @param node
     * @param color
     */
    public static void setBackGrandColorStyle(Node node, String color) {
        node.setStyle(node.getStyle() + ";-fx-background-color: " + color + ";");
    }

    /**
     * 指定されたノードのテキストの色をセット
     *
     * @param node
     * @param color
     * @param size
     * @param isBold
     */
    public static void setTextStyle(Node node, String color, int size, boolean isBold) {
        node.setStyle(node.getStyle() + ";-fx-text-fill:" + color + ";-fx-font-size:" + size + "px;");
        if (isBold) {
            node.setStyle(node.getStyle() + ";-fx-font-weight: bold;");
        }
    }

    /**
     * カスタムスタイルをセット
     *
     * @param node
     * @param style
     */
    public static void setCustomStyle(Node node, String style) {
        node.setStyle(node.getStyle() + ";" + style);
    }
}
